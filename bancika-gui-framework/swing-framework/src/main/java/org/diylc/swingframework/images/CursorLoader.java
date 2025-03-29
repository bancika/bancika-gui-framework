package org.diylc.swingframework.images;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Image;

import org.apache.log4j.Logger;

public enum CursorLoader {
  
  ScrollCenter("scroll_center.png"), ScrollN("scroll_n.png"), ScrollNE("scroll_ne.png"), ScrollE("scroll_e.png"), ScrollSE("scroll_se.png"), 
  ScrollS("scroll_s.png"), ScrollSW("scroll_sw.png"), ScrollW("scroll_w.png"), ScrollNW("scroll_nw.png");

  private String name;
  private Cursor cursor = null;

  private CursorLoader(String name) {
    this.name = name;
  }

  public Cursor getCursor() {
    if (cursor == null) {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      URL imgURL = getClass().getResource(name);
      BufferedImage image = null;
      try {
        image = ImageIO.read(getClass().getResourceAsStream("/swing-framework-images/" + name));
      } catch (IOException e) {
        Logger.getLogger(CursorLoader.class).error("Couldn't load file: " + name);
        return Cursor.getDefaultCursor();
      }
      int width = image.getWidth(null);
      int height = image.getHeight(null);
      cursor = toolkit.createCustomCursor(image , new Point(width / 2, height / 2), "custom:" + name);
    }
    return cursor;
  }
}
