package org.diylc.appframework.simplemq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.EnumSet;

import org.diylc.appframework.simplemq.IMessageListener;
import org.diylc.appframework.simplemq.MessageDispatcher;
import org.junit.Before;
import org.junit.Test;

public class MessageDispatcherTest {

	MessageDispatcher<EventType> dispatcher;
	MockMessageListener listener;
	long startTime;

	@Before
	public void setUp() throws Exception {
		dispatcher = new MessageDispatcher<EventType>(false);
		listener = new MockMessageListener();
		dispatcher.registerListener(listener);
	}

	@Test
	public void testDispatchMessage() {
		startTime = System.nanoTime();
		dispatcher.dispatchMessage(EventType.TEST_EVENT, 12345, "abc");
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
		assertNotNull(listener.eventType);
		assertEquals(EventType.TEST_EVENT, listener.eventType);
		assertNotNull(listener.params);
		assertEquals(2, listener.params.length);
		assertEquals(12345, listener.params[0]);
		assertEquals("abc", listener.params[1]);
	}

	@Test
	public void testDispatchMessageAnotherEvent() {
		dispatcher.dispatchMessage(EventType.ANOTHER_EVENT, 12345, "abc");
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
		assertNull(listener.eventType);
		assertNull(listener.params);
	}

	enum EventType {
		TEST_EVENT, ANOTHER_EVENT
	}

	class MockMessageListener implements IMessageListener<EventType> {

		private EventType eventType;
		private Object[] params;

		@Override
		public void processMessage(EventType eventType, Object... params) {
			System.out.println(Thread.currentThread().getName() + " - received message in "
					+ (System.nanoTime() - startTime) / 1e6 + " ms");
			this.eventType = eventType;
			this.params = params;
		}

		@Override
		public EnumSet<EventType> getSubscribedEventTypes() {
			return EnumSet.of(EventType.TEST_EVENT);
		}
	}
}
