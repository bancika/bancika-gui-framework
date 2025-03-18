package org.diylc.appframework.images;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.log4j.Logger;

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
		java.net.URL imgURL = getClass().getResource("/app-framework-images/" + name);
		if (imgURL != null) {
		return new ImageIcon(imgURL, name);
		} else {
			Logger.getLogger(IconLoader.class).error("Couldn't load file: " + name);
			return null;
		}
	}
}
