package com.diyfever.gui;

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
	 * Draws on the {@link Graphics}.
	 * 
	 * @param g
	 */
	void draw(Graphics g);
}
