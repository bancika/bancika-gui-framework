package org.diylc.swingframework;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Interface for providing a drawing.
 * 
 * @author Branislav Stojkovic
 */
public interface IDrawingProvider {

	/**
	 * Returns size of the drawing.
	 * 
	 * @return
	 */
	Dimension getSize();

	/**
	 * Returns number of pages.
	 * 
	 * @return
	 */
	int getPageCount();

	/**
	 * Draws on the {@link Graphics}.
	 * 
	 * @param page
	 * @param g
	 */
	void draw(int page, Graphics g);
}
