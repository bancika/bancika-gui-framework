package org.diylc.appframework.simplemq;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/**
 * Utility for synchronous or asynchronous message distribution.
 * 
 * @author Branislav Stojkovic
 * 
 * @param <E>
 *            enum that contains all available event types
 * 
 * @see IMessageListener
 */
public class MessageDispatcher<E extends Enum<E>> {

	private static final Logger LOG = Logger.getLogger(MessageDispatcher.class);

	private Map<IMessageListener<E>, EnumSet<E>> listenerMap;
	private Object mutex = new Object();
	private ExecutorService threadFactory;
	private final boolean synchronous;

	public MessageDispatcher(boolean synchronous) {
		this.synchronous = synchronous;
		listenerMap = new HashMap<IMessageListener<E>, EnumSet<E>>();
		if (!synchronous) {
			threadFactory = Executors.newCachedThreadPool();
		}
	}

	public void registerListener(IMessageListener<E> listener) {
		if (listener.getSubscribedEventTypes() != null) {
			synchronized (mutex) {
				listenerMap.put(listener, listener.getSubscribedEventTypes());
			}
		}
	}

	public void unregisterListener(IMessageListener<E> listener) {
		synchronized (mutex) {
			listenerMap.remove(listener);
		}
	}

	/**
	 * Notifies all interested listeners.
	 * 
	 * @param eventType
	 * @param params
	 */
	public void dispatchMessage(E eventType, Object... params) {
		if (synchronous) {
			List<IMessageListener<E>> listeners = new ArrayList<IMessageListener<E>>();
			for (Map.Entry<IMessageListener<E>, EnumSet<E>> entry : listenerMap.entrySet()) {
				if (entry.getValue().contains(eventType)) {
					listeners.add(entry.getKey());
				}
			}
			for (IMessageListener<E> listener : listeners) {
				try {
					listener.processMessage(eventType, params);
				} catch (Exception e) {
					LOG.error("Listener threw an exception", e);
				}
			}
		} else {
			threadFactory.execute(new EventRunnable(eventType, params));
		}
	}

	/**
	 * {@link Runnable} implementation that loops over <code>listenerMap</code>
	 * and dispatches the event to listeners that are listening for that
	 * particular event type.
	 * 
	 */
	class EventRunnable implements Runnable {

		private E eventType;
		private Object[] params;

		public EventRunnable(E eventType, Object[] params) {
			super();
			this.eventType = eventType;
			this.params = params;
		}

		@Override
		public void run() {
			List<IMessageListener<E>> listeners = new ArrayList<IMessageListener<E>>();
			synchronized (mutex) {
				for (Map.Entry<IMessageListener<E>, EnumSet<E>> entry : listenerMap.entrySet()) {
					if (entry.getValue().contains(eventType)) {
						listeners.add(entry.getKey());
					}
				}
			}
			for (IMessageListener<E> listener : listeners) {
				try {
					listener.processMessage(eventType, params);
				} catch (Exception e) {
					LOG.error("Listener threw an exception", e);
				}
			}
		}
	}
}
