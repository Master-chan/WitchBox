package org.witch.standalonebox.api.plugin;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import com.google.common.base.Preconditions;

@Data
@RequiredArgsConstructor(access = AccessLevel.NONE)
public abstract class Command
{

	private final String name;
	private final String[] aliases;

	public Command(String name)
	{
		this(name, new String[0]);
	}

	public Command(String name, String... aliases)
	{
		Preconditions.checkArgument(name != null, "name");
		this.name = name;
		this.aliases = aliases;
	}

	public abstract void execute(UUID invoker, String[] args);
}
