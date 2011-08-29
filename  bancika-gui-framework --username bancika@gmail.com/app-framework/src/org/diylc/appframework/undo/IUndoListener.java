package org.diylc.appframework.undo;

/**
 * Interface for undo/redo notifications.
 * 
 * @see UndoHandler
 * 
 * @author Branislav Stojkovic
 * 
 * @param <T>
 */
public interface IUndoListener<T> {

	/**
	 * Called when undo or redo is performed.
	 * 
	 * @param currentState
	 */
	void actionPerformed(T currentState);
}
