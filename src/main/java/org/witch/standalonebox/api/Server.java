package org.witch.standalonebox.api;

import java.io.File;
import java.util.logging.Logger;

import org.witch.standalonebox.api.plugin.PluginManager;
import org.witch.standalonebox.api.scheduler.WitchboxExecutorService;

public abstract class Server
{
	
	private static Server instance;
	
	public static void setServer(Server newServer)
	{
		if(instance != null)
		{
			throw new UnsupportedOperationException("Can't change singleton!");
		}
		instance = newServer;
	}
	
	public abstract Logger getLogger();
	public abstract PluginManager getPluginManager();
	public abstract File getPluginFolder();
	public abstract WitchboxExecutorService getScheduler();
	public abstract <T> T callEvent(T event);
	public abstract void shutdown();
	
	public static Server getInstance()
	{
		return instance;
	}
	

}
