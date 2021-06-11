package org.witch.standalonebox.api.plugin;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.witch.standalonebox.Main;
import org.witch.standalonebox.WitchServer;
import org.witch.standalonebox.api.Server;
import org.witch.standalonebox.api.config.JsonConfigurationLoader;
import org.witch.standalonebox.api.config.YamlConfigurationLoader;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import lombok.Getter;
import net.techcable.event4j.EventBus;
import net.techcable.event4j.EventExecutor;

public class PluginManager
{

	private List<String> namesToCheck = Stream.of("boxplugin.yml", "boxplugin.yaml", "boxplugin.json", "plugin.yml", "plugin.yaml", "plugin.json").collect(Collectors.toList());
	private static final Pattern argsSplit = Pattern.compile( " " );
	private static boolean update = System.getenv("SkipPluginUpdates") == null;
	
	private final WitchServer server;
	@Getter private final EventBus<Object, Object> eventBus;

	private Map<String, PluginDescription> toLoad = new HashMap<>();
	private Map<String, Plugin> plugins = new LinkedHashMap<>();
	private Multimap<Plugin, Object> listenersByPlugin = ArrayListMultimap.create();
	
	private final Map<String, Command> commandMap = new HashMap<>();
	private final Multimap<Plugin, Command> commandsByPlugin = ArrayListMultimap.create();

	@Getter private YamlConfigurationLoader ymlMapper = new YamlConfigurationLoader();
	@Getter private JsonConfigurationLoader jsonMapper = new JsonConfigurationLoader();

	public PluginManager(WitchServer server)
	{
		this.server = server;
		eventBus = EventBus.builder()
				.executorFactory(EventExecutor.Factory.REFLECTION_LISTENER_FACTORY)
				.eventClass(Object.class)
				.listenerClass(Object.class)
				.build();
	}

	
    public void registerCommand(Plugin plugin, Command command)
    {
        commandMap.put( command.getName().toLowerCase(), command );
        for ( String alias : command.getAliases() )
        {
            commandMap.put( alias.toLowerCase(), command );
        }
        commandsByPlugin.put( plugin, command );
    }

    /**
     * Unregister a command so it will no longer be executed.
     *
     * @param command the command to unregister
     */
    public void unregisterCommand(Command command)
    {
        while ( commandMap.values().remove( command ) );
        commandsByPlugin.values().remove( command );
    }

    /**
     * Unregister all commands owned by a {@link Plugin}
     *
     * @param plugin the plugin to register the commands of
     */
    public void unregisterCommands(Plugin plugin)
    {
        for ( Iterator<Command> it = commandsByPlugin.get( plugin ).iterator(); it.hasNext(); )
        {
            Command command = it.next();
            while ( commandMap.values().remove( command ) );
            it.remove();
        }
    }


    /**
     * Execute a command if it is registered, else return false.
     */
	public boolean dispatchCommand(UUID invoker, String commandLine)
	{
		String[] split = argsSplit.split(commandLine, -1);
		// Check for chat that only contains " "
		if(split.length == 0)
		{
			return false;
		}

		String commandName = split[0].toLowerCase();
		Command command = commandMap.get(commandName);
		if(command == null)
		{
			return false;
		}
		
        String[] args = Arrays.copyOfRange( split, 1, split.length );
		try
		{
			command.execute(invoker, args);
		}
		catch(Exception ex)
		{
			Server.getInstance().getLogger().log(Level.WARNING, "Error in dispatching command", ex);
		}
        return true;
	}
	
	public boolean dispatchCommand(String commandLine)
	{
		return dispatchCommand(null, commandLine);
	}
	
	
    public Collection<Plugin> getPlugins()
    {
        return plugins.values();
    }

    public Plugin getPlugin(String name)
    {
        return plugins.get( name );
    }

    public void loadPlugins()
    {
        Map<PluginDescription, Boolean> pluginStatuses = new HashMap<>();
        for ( Map.Entry<String, PluginDescription> entry : toLoad.entrySet() )
        {
            PluginDescription plugin = entry.getValue();
            if ( !enablePlugin( pluginStatuses, new Stack<PluginDescription>(), plugin ) )
            {
                Server.getInstance().getLogger().log( Level.WARNING, "Failed to enable {0}", entry.getKey() );
            }
        }
        toLoad.clear();
        toLoad = null;
    }

	public void enablePlugins()
	{
		for(Plugin plugin : plugins.values())
		{
			try
			{
				plugin.onEnable();
				plugin.setEnabled(true);
				Server
						.getInstance()
						.getLogger()
						.log(Level.INFO,
								"Enabled plugin {0} version {1} by {2}",
								new Object[] { plugin.getDescription().getName(), plugin.getDescription().getVersion(),
										plugin.getDescription().getAuthor() });
			}
			catch(Throwable t)
			{
				Server.getInstance().getLogger()
						.log(Level.WARNING, "Exception encountered when loading plugin: " + plugin.getDescription().getName(), t);
			}
		}
	}

	private boolean enablePlugin(Map<PluginDescription, Boolean> pluginStatuses, Stack<PluginDescription> dependStack,
			PluginDescription plugin)
	{
		if(pluginStatuses.containsKey(plugin))
		{
			return pluginStatuses.get(plugin);
		}

		// combine all dependencies for 'for loop'
		Set<String> dependencies = new HashSet<>();
		dependencies.addAll(plugin.getDepends());
		dependencies.addAll(plugin.getSoftDepends());

		// success status
		boolean status = true;

		// try to load dependencies first
		for(String dependName : dependencies)
		{
			PluginDescription depend = toLoad.get(dependName);
			Boolean dependStatus = (depend != null) ? pluginStatuses.get(depend) : Boolean.FALSE;

			if(dependStatus == null)
			{
				if(dependStack.contains(depend))
				{
					StringBuilder dependencyGraph = new StringBuilder();
					for(PluginDescription element : dependStack)
					{
						dependencyGraph.append(element.getName()).append(" -> ");
					}
					dependencyGraph.append(plugin.getName()).append(" -> ").append(dependName);
					Server.getInstance().getLogger().log(Level.WARNING, "Circular dependency detected: {0}", dependencyGraph);
					status = false;
				}
				else
				{
					dependStack.push(plugin);
					dependStatus = this.enablePlugin(pluginStatuses, dependStack, depend);
					dependStack.pop();
				}
			}

			if(dependStatus.equals(Boolean.FALSE) && plugin.getDepends().contains(dependName))
			{
				Server.getInstance()
						.getLogger()
						.log(Level.WARNING, "{0} (required by {1}) is unavailable",
								new Object[] { String.valueOf(dependName), plugin.getName() });
				status = false;
			}

			if(!status)
			{
				break;
			}
		}
		
		if(update)
		{
			
			
		}

		// do actual loading
		if(status)
		{
			try
			{
				@SuppressWarnings("resource")
				URLClassLoader loader = new PluginClassloader(new URL[] { plugin.getFile().toURI().toURL() });
				Class<?> main = loader.loadClass(plugin.getMain());
				Plugin clazz = (Plugin) main.getDeclaredConstructor().newInstance();

				clazz.init(Main.getInstance(), plugin);
				plugins.put(plugin.getName(), clazz);
				clazz.onLoad();
				Server.getInstance()
						.getLogger()
						.log(Level.INFO, "Loaded plugin {0} version {1} by {2}",
								new Object[] { plugin.getName(), plugin.getVersion(), plugin.getAuthor() });
			}
			catch(Throwable t)
			{
				Server.getInstance().getLogger().log(Level.WARNING, "Exception enabling plugin " + plugin.getName(), t);
			}
		}

		pluginStatuses.put(plugin, status);
		return status;
	}
	
	public void disablePlugins()
	{
		for(Plugin enabledPlugin : getPlugins())
		{
			disablePlugin(enabledPlugin);
		}
	}
	
	public void disablePlugin(Plugin enabledPlugin)
	{
		if(enabledPlugin.isEnabled())
		{
			try
			{
				unregisterListeners(enabledPlugin);
				enabledPlugin.onDisable();
				enabledPlugin.setEnabled(false);
				Server.getInstance().getLogger().log(Level.INFO, "Disabled plugin {0} version {1} by {2}",
												new Object[] { enabledPlugin.getDescription().getName(), enabledPlugin.getDescription().getVersion(), enabledPlugin.getDescription().getAuthor() });
			}
			catch(Throwable t)
			{
				Server.getInstance().getLogger().log(Level.WARNING, "Exception disabling plugin " + enabledPlugin.getDescription().getName(), t);
	
			}
		}
	}

	public void detectPlugins(File folder)
	{
		Preconditions.checkNotNull(folder, "Data folder is null");
		Preconditions.checkArgument(folder.isDirectory(), "Must load from a directory");
		for(File file : folder.listFiles())
		{
			if((file.isFile()) && (file.getName().endsWith(".jar")))
			{
				PluginDescription description;
				try(JarFile jar = new JarFile(file))
				{
					JarEntry pdf = null;
					boolean json = false;
					for(String name : namesToCheck)
					{
						pdf = jar.getJarEntry(name);
						json = name.endsWith(".json");
						if(pdf != null)
						{
							break;
						}
					}
					
					Preconditions.checkNotNull(pdf, "Plugin must have a plugin of boxplugin config file in .yml or .json format.");
					// Plugin description file exists
					if(pdf != null)
					{
						try(InputStream in = jar.getInputStream(pdf))
						{
							description = json ? 
									jsonMapper.loadConfiguration(in, PluginDescription.class) 
									: ymlMapper.loadConfiguration(in, PluginDescription.class);
							description.setFile(file);
							this.toLoad.put(description.getName(), description);
						}
					}
				}
				catch(Throwable ex)
				{
					Server.getInstance().getLogger().log(Level.WARNING, "Could not load plugin from file " + file, ex);
				}
			}
		}
	}

	public <T extends Object> T callEvent(T event)
	{
		//long start = System.nanoTime();
		try
		{
			eventBus.fire(event);
		}
		catch(Throwable t)
		{
			server.getLogger().log(Level.SEVERE, String.format("Exception executing event %1$s", event), t);
		}
		/*long elapsed = start - System.nanoTime();
		if(elapsed > 250000L)
		{
			server.getLogger().log(Level.WARNING, "Event {0} took more {1}ns to process!", new Object[] { event, Long.valueOf(elapsed) });
		}*/
		return event;
	}

	public void registerListener(Plugin plugin, Object listener)
	{
		this.eventBus.register(listener);
		this.listenersByPlugin.put(plugin, listener);
	}

	public void unregisterListener(Object listener)
	{
		this.eventBus.unregister(listener);
		this.listenersByPlugin.values().remove(listener);
	}

	public void unregisterListeners(Plugin plugin)
	{
		for(Iterator<Object> it = this.listenersByPlugin.get(plugin).iterator(); it.hasNext();)
		{
			this.eventBus.unregister(it.next());
			it.remove();
		}
	}

}
