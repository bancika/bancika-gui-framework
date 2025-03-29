package org.diylc.swingframework.images;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * Loads image resources as Icons.
 * 
 * @author Branislav Stojkovic
 */
public enum IconLoader {

  Error("error.png"), Warning("warning.png"), LightBulbOn("lightbulb_on.png"), LightBulbOff("lightbulb_off.png"), MoveSmall(
      "move_small.png"), Undo("undo.png"), Redo("redo.png"), Arrow("arrow.png");

  protected String name;

  private IconLoader(String name) {
    this.name = name;
  }

  public Icon getIcon() {
    BufferedImage img = null;
    try {
      img = ImageIO.read(getClass().getResourceAsStream("/swing-framework-images/" + name));
    } catch (IOException e) {
      Logger.getLogger(IconLoader.class).error("Couldn't load file: " + name);
    }
    return new ImageIcon(img);
  }
}
