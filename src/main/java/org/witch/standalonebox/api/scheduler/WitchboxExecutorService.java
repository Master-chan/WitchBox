package org.witch.standalonebox.api.scheduler;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.witch.standalonebox.api.Server;
import org.witch.standalonebox.api.plugin.Plugin;

public interface WitchboxExecutorService
{

	public Server getServer();
	
	public ScheduledExecutorService getUnsafeExecutorService();
	
	public <T> Future<T> submit(Plugin taskOwner, Callable<T> task);

	public <T> Future<T> submit(Plugin taskOwner, Runnable task, T result);
	
	public Future<?> submit(Plugin taskOwner, Runnable task);
	
	public <T> List<Future<T>> invokeAll(Plugin taskOwner, Collection<? extends Callable<T>> tasks) throws InterruptedException;

	public <T> List<Future<T>> invokeAll(Plugin taskOwner, Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException;

	public ScheduledFuture<?> schedule(Plugin taskOwner, Runnable command, long delay, TimeUnit unit);

	public <V> ScheduledFuture<V> schedule(Plugin taskOwner, Callable<V> callable, long delay, TimeUnit unit);
	
	public ScheduledFuture<?> scheduleAtFixedRate(Plugin taskOwner, Runnable command, long initialDelay, long period, TimeUnit unit);
	
	public ScheduledFuture<?> scheduleWithFixedDelay(Plugin taskOwner, Runnable command, long initialDelay, long delay, TimeUnit unit);
	
	public boolean cancel(ScheduledFuture<?> task, boolean interrupt);
}
