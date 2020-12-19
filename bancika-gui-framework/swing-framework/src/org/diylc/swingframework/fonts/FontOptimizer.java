package org.diylc.swingframework.fonts;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.CellRendererPane;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.diylc.appframework.miscutils.ConfigurationManager;
import org.diylc.appframework.miscutils.IConfigurationManager;

/***
 * Analyzes installed fonts and looks for ones that are slow to render. Those are kept in
 * {@link #longRunningFonts} collection.
 * 
 * @author bancika
 *
 */
public class FontOptimizer {

  private static final Logger LOG = Logger.getLogger(FontOptimizer.class);

  private static Set<String> longRunningFonts = new HashSet<String>();
  private static Object mutex = new Object();

  private static final int TIMEOUT = 25;
  private static final String CONFIG_KEY = "FontOptimizer.longRunningFonts";

  public static Set<String> getLongRunningFonts() {
    synchronized (mutex) {
      return new HashSet<String>(longRunningFonts);
    }
  }

  /**
   * Needs to be called at the startup.
   */
  @SuppressWarnings("unchecked")
  public static void run(IConfigurationManager<?> configManager) {
    LOG.info("Starting font optimizer");

    try {
      synchronized (mutex) {
        longRunningFonts =
            new HashSet<String>((Set<String>) configManager.readObject(CONFIG_KEY, new HashSet<String>()));
      }
    } catch (Exception e) {
      LOG.error("Error loading " + CONFIG_KEY, e);
    }

    long now = System.currentTimeMillis();

    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    now = System.currentTimeMillis();
    String[] fonts = ge.getAvailableFontFamilyNames();
    now = System.currentTimeMillis();
    Arrays.sort(fonts);

    LOG.debug("Fonts loaded in " + (System.currentTimeMillis() - now) + "ms");
    now = System.currentTimeMillis();

    JLabel testLabel = new JLabel();
    CellRendererPane p = new CellRendererPane();

    String previewString = "AaBbCc";
    long start = System.currentTimeMillis();
    for (String fontName : fonts) {
      Font font = new Font(fontName, Font.PLAIN, 10);
      // show only supported characters
      StringBuilder thisPreview = new StringBuilder();
      for (int i = 0; i < previewString.length(); i++) {
        char c = previewString.charAt(i);
        if (font.canDisplay(c))
          thisPreview.append(c);
      }
      testLabel.setText(thisPreview.toString());
      testLabel.setFont(font);

      BufferedImage img = new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
      Graphics g = img.getGraphics();
      SwingUtilities.paintComponent(g, testLabel, p, 0, 0, img.getWidth(), img.getHeight());
      g.dispose();

      // try {
      // ImageIO.write(img, "png", new File("e:\\tmp\\" + fontName + ".png"));
      // } catch (IOException e) {
      // e.printStackTrace();
      // }

      Graphics2D graphics = (Graphics2D) testLabel.getGraphics();
      testLabel.paint(graphics);

      Long time = System.currentTimeMillis() - now;
      if (time > TIMEOUT)
        synchronized (mutex) {
          longRunningFonts.add(fontName);
        }

      now = System.currentTimeMillis();
    }
    LOG.info("Font optimization completed in " + (System.currentTimeMillis() - start) + "ms. Found "
        + longRunningFonts.size() + " slow fonts");
    if (longRunningFonts.size() > 0)
      LOG.debug("Slow fonts: " + String.join(",", longRunningFonts));
    
    ConfigurationManager.getInstance().writeValue(CONFIG_KEY, longRunningFonts);
  }
}
