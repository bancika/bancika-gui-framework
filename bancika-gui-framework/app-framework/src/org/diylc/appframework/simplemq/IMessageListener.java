package org.diylc.appframework.simplemq;

import java.util.EnumSet;

import javax.swing.SwingUtilities;

/**
 * Interface for message listener.
 * 
 * @author Branislav Stojkovic
 * 
 * @param <E>
 *            enum that contains all available event types
 * 
 * @see MessageDispatcher
 */
public interface IMessageListener<E extends Enum<E>> {

	/**
	 * Returns a set of event types to subscribe for. Listener will be notified
	 * only if event type is contained in this set.
	 * 
	 * @return
	 */
	EnumSet<E> getSubscribedEventTypes();

	/**
	 * Called from the background thread when event is received. Use
	 * {@link SwingUtilities#invokeLater} if event processing needs to take
	 * place in the EDT.
	 * 
	 * @param eventType
	 * @param params
	 */
	void processMessage(E eventType, Object... params);
}
