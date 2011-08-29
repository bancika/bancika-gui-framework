package org.diylc.swingframework.ruler;

/**
 * Interface for listening for ruler changes.
 * 
 * @author Branislav Stojkovic
 */
public interface IRulerListener {

	/**
	 * Notification that ruler units have been changed.
	 * 
	 * @param isMetric
	 *            if <code>true</code>, user has chosen metric units, otherwise
	 *            imperial.
	 */
	void unitsChanged(boolean isMetric);
}
