package org.witch.standalonebox.api.scheduler;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.witch.standalonebox.WitchServer;
import org.witch.standalonebox.api.plugin.Plugin;

import com.google.common.base.Preconditions;

import lombok.Getter;

public class ThreadPoolExecutorService implements WitchboxExecutorService
{
	
	@Getter private final WitchServer server;
	@Getter private final ScheduledExecutorService unsafeExecutorService;
	
	public ThreadPoolExecutorService(WitchServer server, ThreadFactory threadFactory, int maxPoolSize)
	{
		this.server = server;
		this.unsafeExecutorService = Executors.newScheduledThreadPool(maxPoolSize, threadFactory);
	}


	@Override
	public <T> Future<T> submit(Plugin taskOwner, Callable<T> task)
	{
		Preconditions.checkNotNull(taskOwner, "Plugin can't be null");
		if(!taskOwner.isEnabled())
		{
			server.getLogger().warning(String.format("Plugin \"%s\" submitted task while disabled.", taskOwner.getDescription().getName()));
		}
		return unsafeExecutorService.submit(RunnableWrapper.wrapCallable(taskOwner, task));
	}

	@Override
	public <T> Future<T> submit(Plugin taskOwner, Runnable task, T result)
	{
		Preconditions.checkNotNull(taskOwner, "Plugin can't be null");
		if(!taskOwner.isEnabled())
		{
			server.getLogger().warning(String.format("Plugin \"%s\" submitted task while disabled.", taskOwner.getDescription().getName()));
		}
		return unsafeExecutorService.submit(RunnableWrapper.wrapRunnable(taskOwner, task), result);
	}

	@Override
	public Future<?> submit(Plugin taskOwner, Runnable task)
	{
		Preconditions.checkNotNull(taskOwner, "Plugin can't be null");
		if(!taskOwner.isEnabled())
		{
			server.getLogger().warning(String.format("Plugin \"%s\" submitted task while disabled.", taskOwner.getDescription().getName()));
		}
		return unsafeExecutorService.submit(RunnableWrapper.wrapRunnable(taskOwner, task));
	}

	@Override
	public <T> List<Future<T>> invokeAll(Plugin taskOwner, Collection<? extends Callable<T>> tasks)
			throws InterruptedException
	{
		Preconditions.checkNotNull(taskOwner, "Plugin can't be null");
		if(!taskOwner.isEnabled())
		{
			server.getLogger().warning(String.format("Plugin \"%s\" submitted task while disabled.", taskOwner.getDescription().getName()));
		}
		return unsafeExecutorService.invokeAll(tasks.stream().map(task -> RunnableWrapper.wrapCallable(taskOwner, task)).collect(Collectors.toList()));
	}

	@Override
	public <T> List<Future<T>> invokeAll(Plugin taskOwner, Collection<? extends Callable<T>> tasks, long timeout,
			TimeUnit unit) throws InterruptedException
	{
		Preconditions.checkNotNull(taskOwner, "Plugin can't be null");
		if(!taskOwner.isEnabled())
		{
			server.getLogger().warning(String.format("Plugin \"%s\" submitted task while disabled.", taskOwner.getDescription().getName()));
		}
		return unsafeExecutorService.invokeAll(tasks.stream().map(task -> RunnableWrapper.wrapCallable(taskOwner, task)).collect(Collectors.toList()), timeout, unit);
	}

	@Override
	public ScheduledFuture<?> schedule(Plugin taskOwner, Runnable command, long delay, TimeUnit unit)
	{
		Preconditions.checkNotNull(taskOwner, "Plugin can't be null");
		if(!taskOwner.isEnabled())
		{
			server.getLogger().warning(String.format("Plugin \"%s\" submitted task while disabled.", taskOwner.getDescription().getName()));
		}
		return unsafeExecutorService.schedule(RunnableWrapper.wrapRunnable(taskOwner, command), delay, unit);
	}

	@Override
	public <V> ScheduledFuture<V> schedule(Plugin taskOwner, Callable<V> callable, long delay, TimeUnit unit)
	{
		Preconditions.checkNotNull(taskOwner, "Plugin can't be null");
		if(!taskOwner.isEnabled())
		{
			server.getLogger().warning(String.format("Plugin \"%s\" submitted task while disabled.", taskOwner.getDescription().getName()));
		}
		return unsafeExecutorService.schedule(RunnableWrapper.wrapCallable(taskOwner, callable), delay, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Plugin taskOwner, Runnable command, long initialDelay, long period,
			TimeUnit unit)
	{
		Preconditions.checkNotNull(taskOwner, "Plugin can't be null");
		if(!taskOwner.isEnabled())
		{
			server.getLogger().warning(String.format("Plugin \"%s\" submitted task while disabled.", taskOwner.getDescription().getName()));
		}
		return unsafeExecutorService.scheduleAtFixedRate(RunnableWrapper.wrapRunnable(taskOwner, command), initialDelay, period, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Plugin taskOwner, Runnable command, long initialDelay, long delay,
			TimeUnit unit)
	{
		Preconditions.checkNotNull(taskOwner, "Plugin can't be null");
		if(!taskOwner.isEnabled())
		{
			server.getLogger().warning(String.format("Plugin \"%s\" submitted task while disabled.", taskOwner.getDescription().getName()));
		}
		return unsafeExecutorService.scheduleWithFixedDelay(RunnableWrapper.wrapRunnable(taskOwner, command), initialDelay, delay, unit);
	}

	@Override
	public boolean cancel(ScheduledFuture<?> task, boolean interrupt)
	{
		if(task != null)
		{
			return task.cancel(interrupt);
		}
		return false;
	}
	
	
	
}
