package org.witch.standalonebox;

import java.io.IOException;
import java.util.logging.Logger;

import lombok.Getter;


public class Main
{
	
	@Getter private static WitchServer instance;
	
	public static Logger getLogger()
	{
		return instance.getLogger();
	}

	public static void main(String[] args) throws IOException
	{

		instance = new WitchServer();
		instance.start();
				
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if(instance != null && instance.getIsRunning().get())
				{
					instance.shutdownHook();
				}
			}
		}, "WitchBox Shutdown Hook"));
		
		String line;
		while((instance.getIsRunning().get()) && ((line = instance.getConsoleReader().readLine()) != null))
		{
			instance.getLogger().info(line);
			if(!instance.getPluginManager().dispatchCommand(line))
			{
				instance.getLogger().info("Command not found");
			}
		}
	}

}
