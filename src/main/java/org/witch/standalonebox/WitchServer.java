package org.witch.standalonebox;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.fusesource.jansi.AnsiConsole;
import org.witch.standalonebox.api.Server;
import org.witch.standalonebox.api.plugin.PluginManager;
import org.witch.standalonebox.api.scheduler.ThreadPoolExecutorService;
import org.witch.standalonebox.api.scheduler.WitchboxExecutorService;
import org.witch.standalonebox.utils.ResourceStreamUtil;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import jline.console.ConsoleReader;
import jline.internal.InputStreamReader;
import lombok.Getter;
import lombok.SneakyThrows;


public class WitchServer extends Server
{
	
	@Getter private AtomicBoolean isRunning = new AtomicBoolean(false);
	@Getter private File pluginFolder;
	@Getter private Logger logger;
	@Getter private PluginManager pluginManager;
	@Getter private ConsoleReader consoleReader;
	@Getter private WitchboxExecutorService scheduler;
	
	public void start() throws IOException
	{
		Server.setServer(this);
		AnsiConsole.systemInstall();
	    consoleReader = new ConsoleReader();
	    consoleReader.setExpandEvents(false);
	    
	    File loggerDir = new File("logs");
	    if(!loggerDir.isDirectory())
	    {
	    	loggerDir.delete();
	    	loggerDir.mkdirs();
	    }
	    
	    LogManager.getLogManager().readConfiguration(ResourceStreamUtil.getResourceAsStream("/logger.properties"));
		logger = Logger.getLogger(WitchServer.class.getName());
		
	    // Load ASCII logo
	    try(BufferedReader asciiLogo = new BufferedReader(new InputStreamReader(ResourceStreamUtil.getResourceAsStream("/logo"), Charsets.UTF_8)))
	    {
			String line;
			while((line = asciiLogo.readLine()) != null)
	    	{
	    		logger.info(line);
	    	}
	    }
	    catch(IOException ex)
	    {
	    	logger.log(Level.INFO, "Couldn't load ASCII logo from jar file.", ex);
	    }
	    
	    String version = "unknown";
	    try(InputStream versionProperties = ResourceStreamUtil.getResourceAsStream("/version.properties"))
	    {
	    	Properties versionProp = new Properties();
	    	versionProp.load(versionProperties);
	    	version = versionProp.getProperty("version");
	    }
	    catch(Throwable t)
	    {
	    	logger.log(Level.INFO, "Couldn't load version.properties.", t);
	    }

	    logger.log(Level.INFO, "String WitchBox bootstrap server v{0}", version);

		pluginFolder = new File("module");
		scheduler = new ThreadPoolExecutorService(this, 
				new ThreadFactoryBuilder().setNameFormat("WitchServer Scheduler Thread-%d").setDaemon(true).build(), 16);

		pluginManager = new PluginManager(this);
		
		pluginManager.registerCommand(null, new StopCommand());
		
		logger.info("Loading plugins.");
		pluginFolder.mkdir();
	    pluginManager.detectPlugins(pluginFolder);
	    pluginManager.loadPlugins();
	    pluginManager.enablePlugins();

	    if(pluginManager.getPlugins().size() == 0)
	    {
	    	logger.warning("Couldn't load any plugins!");
	    	isRunning.set(true);
	    	//shutdown();
	    }
	    else
	    {
		    isRunning.set(true);
		    logger.info("Loaded " + pluginManager.getPlugins().size() + " plugins.");
	    }
	}
	
	public void shutdownHook()
	{
		if(isRunning.getAndSet(false))
		{
			if(pluginManager != null)
			{
				logger.info("Disabling plugins...");
				pluginManager.disablePlugins();
			}
			
			synchronized(isRunning)
			{
				isRunning.notifyAll();
			}
		}
	}
	
	public void shutdown()
	{
		shutdownHook();
		System.exit(0);
	}

	@Override
	@SneakyThrows
	public <T extends Object> T callEvent(T event)
	{
		if(pluginManager != null && pluginManager.getEventBus() != null)
		{
			return pluginManager.callEvent(event);
		}
		else
		{
			throw new UnsupportedOperationException("Can't call event: event bus is not initialized yet.");
		}
	}

}
