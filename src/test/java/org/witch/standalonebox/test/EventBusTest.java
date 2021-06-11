package org.witch.standalonebox.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Before;
import org.junit.Test;
import org.witch.standalonebox.WitchServer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.techcable.event4j.EventHandler;
import net.techcable.event4j.EventPriority;

public class EventBusTest
{
	
	private final WitchServer server;
	private TestListener testListener;
	
	public EventBusTest() throws IOException
	{
		server = new WitchServer();
		server.start();
	}
	
	@Before
	public void register()
	{
		testListener = new TestListener();
		server.getPluginManager().getEventBus().register(testListener);
	}
	
	@Test
	public void testEventFire()
	{
		for(int i = 0; i < 256; i++)
		{
			int testValue = ThreadLocalRandom.current().nextInt(256);
			TestEvent event = new TestEvent(testValue, 0);
			event = server.getPluginManager().callEvent(event);
			assertEquals(testValue + 200, event.getTestValue());
			assertEquals(event.getChanges(), 2);
		}
	}
	
	private static class TestListener
	{
		@EventHandler(priority = EventPriority.LOWEST)
		public void onTest(TestEvent event)
		{
			int value = event.getTestValue();
			assertEquals(event.getChanges(), 0);
			event.setTestValue(value + 50);
		}
		
		@EventHandler(priority = EventPriority.NORMAL)
		public void onTest2(TestEvent event)
		{
			int value = event.getTestValue();
			assertEquals(event.getChanges(), 1);
			event.setTestValue(value + 150);
		}
	}
	
	@AllArgsConstructor
	private static class TestEvent
	{
		@Getter private int testValue;
		@Getter private int changes;
		
		public void setTestValue(int value)
		{
			testValue = value;
			changes++;
		}
	}
	
}
