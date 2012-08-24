package org.diylc.swingframework.ruler;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import org.diylc.swingframework.IDrawingProvider;


/**
 * {@link IDrawingProvider} implementation that draws the specified
 * {@link Component} onto the canvas.
 * 
 * @author Branislav Stojkovic
 */
public class ComponentThumbnailProvider implements IDrawingProvider {

	private Component view;

	public ComponentThumbnailProvider(Component view) {
		super();
		this.view = view;
	}

	@Override
	public Dimension getSize() {
		return view.getSize();
	}

	@Override
	public void draw(int page, Graphics g) {
		view.paint(g);
	}

	@Override
	public int getPageCount() {
		return 1;
	}
}
