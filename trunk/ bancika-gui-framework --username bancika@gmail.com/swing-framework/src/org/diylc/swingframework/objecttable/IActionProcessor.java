package org.diylc.swingframework.objecttable;

import javax.swing.Icon;

/**
 * Interface for communication with {@link ObjectListTable}.
 * 
 * @author Branislav Stojkovic
 */
public interface IActionProcessor<T> {

	/**
	 * Returns the {@link Icon} for the specified action.
	 * 
	 * @param actionName
	 * @return
	 */
	Icon getActionIcon(String actionName);

	/**
	 * Returns the label for the specified action.
	 * 
	 * @param actionName
	 * @return
	 */
	String getActionLabel(String actionName);

	/**
	 * Notifies the listener that action has been executed. <code>value</code>
	 * parameter contains the object that represents the row that was clicked
	 * on.
	 * 
	 * @param value
	 * @param actionName
	 */
	void actionExecuted(T value, String actionName);
}
