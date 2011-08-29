package org.diylc.appframework.images;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Loads image resources as Icons.
 * 
 * @author Branislav Stojkovic
 */
public enum IconLoader {

	Error("error.png"), Warning("warning.png"), LightBulbOn("lightbulb_on.png"), LightBulbOff(
			"lightbulb_off.png"), MoveSmall("move_small.png"), Undo("undo.png"), Redo("redo.png"), ;

	protected String name;

	private IconLoader(String name) {
		this.name = name;
	}

	public Icon getIcon() {
		java.net.URL imgURL = getClass().getResource(name);
		if (imgURL != null) {
			return new ImageIcon(imgURL, name);
		} else {
			System.err.println("Couldn't find file: " + name);
			return null;
		}
	}
}
