package org.witch.standalonebox.api.plugin;

import java.io.File;
import java.util.logging.Logger;

import org.witch.standalonebox.WitchServer;
import org.witch.standalonebox.api.Server;

import lombok.Getter;
import lombok.Setter;



public abstract class Plugin
{

	public void onEnable(){}
	public void onDisable(){}
	public void onLoad(){}
	
	@Getter protected Server server;
	@Getter protected PluginDescription description;
	@Getter protected File file;
	@Getter protected Logger logger;
	@Getter @Setter protected boolean enabled = false;
	
	public File getDataFolder()
	{
		return new File(Server.getInstance().getPluginFolder(), getDescription().getName());
	}
	
    final void init(WitchServer server, PluginDescription description)
    {
        this.server = server;
        this.description = description;
        this.file = description.getFile();
        this.logger = server.getLogger();
    }

	
}
