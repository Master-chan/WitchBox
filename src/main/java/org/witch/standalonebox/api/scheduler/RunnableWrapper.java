package org.witch.standalonebox.api.scheduler;

import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.witch.standalonebox.api.plugin.Plugin;

import com.google.common.base.Preconditions;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RunnableWrapper
{
	
	public static Runnable wrapRunnable(Plugin plugin, Runnable task)
	{
		Preconditions.checkNotNull(plugin);
		Preconditions.checkNotNull(task);
		return () ->
		{
			try
			{
				task.run();
			}
			catch(Throwable t)
			{
				plugin.getLogger().log(Level.SEVERE, "Task from plugin " + plugin.getDescription().getName() + " generated an exception: ", t);
			}
		};
	}
	
	public static <T> Callable<T> wrapCallable(Plugin plugin, Callable<T> task)
	{
		Preconditions.checkNotNull(plugin);
		Preconditions.checkNotNull(task);
		return new Callable<T>()
				{
					@Override
					public T call() throws Exception
					{
						try
						{
							return task.call();
						}
						catch(Throwable t)
						{
							plugin.getLogger().log(Level.SEVERE, "Task from plugin " + plugin.getDescription().getName() + " generated an exception: ", t);
						}
						return null;
					}
				};
	}
	
}
