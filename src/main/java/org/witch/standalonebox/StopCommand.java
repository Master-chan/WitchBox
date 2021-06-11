package org.witch.standalonebox;

import java.util.UUID;

import org.witch.standalonebox.api.plugin.Command;


public class StopCommand extends Command
{

	public StopCommand()
	{
		super("stop", new String[0]);
	}

	@Override
	public void execute(UUID invoker, String[] args)
	{
		Main.getInstance().shutdown();
	}

}
