package org.diylc.swingframework.export;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;

import javax.imageio.ImageIO;

import org.diylc.swingframework.IDrawingProvider;

import com.orsonpdf.PDFDocument;
import com.orsonpdf.PDFGraphics2D;
import com.orsonpdf.PDFHints;
import com.orsonpdf.Page;

/**
 * Utility class that handles image exports to:
 * 
 * <ul>
 * <li>PDF</li>
 * <li>PNG</li>
 * <li>Printer</li>
 * </ul>
 * 
 * @author Branislav Stojkovic
 */
public class DrawingExporter {

  private static final double margin = 0; // 1cm
  private static final int PDF_RESOLUTION = 72;
  private static final int PNG_RESOLUTION = 300;
  private static final int SCREEN_RESOLUTION = Toolkit.getDefaultToolkit().getScreenResolution();

  private static DrawingExporter instance;

  public static DrawingExporter getInstance() {
    if (instance == null) {
      instance = new DrawingExporter();
    }
    return instance;
  }

  private DrawingExporter() {}

  /**
   * Prints the image and scales it down if needed.
   * 
   * @param provider
   * @throws PrinterException
   */
  public void print(final IDrawingProvider provider) throws PrinterException {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    final int pageCount = provider.getPageCount();
    printJob.setPrintable(new Printable() {

      @Override
      public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
          throws PrinterException {
        if (pageIndex >= pageCount) {
          return (NO_SUCH_PAGE);
        } else {
          Graphics2D g2d = (Graphics2D) graphics;

          Dimension d = provider.getSize();

          double pageRatio = pageFormat.getWidth() / pageFormat.getHeight();
          double imageRatio = d.getWidth() / d.getHeight();
          double scale;
          if (imageRatio > pageRatio) {
            scale = (pageFormat.getWidth() - 2 * margin) / d.getWidth();
          } else {
            scale = (pageFormat.getHeight() - 2 * margin) / d.getHeight();
          }
          if (scale > 1) {
            scale = 1d;
          }

          g2d.translate(pageFormat.getImageableX() + margin, pageFormat.getImageableY() + margin);

          g2d.setFont(new Font("Tahoma", Font.PLAIN, 6));
          FontMetrics metrics = g2d.getFontMetrics();
          g2d.setColor(Color.gray);

          if (scale < 1) {
            String warningStr = "Note: image has been scaled down to fit the page.";
            g2d.drawString(warningStr, 0, (int) (d.getHeight() * scale + metrics.getHeight()));
          }

          // g2d.scale(scale, scale);

          provider.draw(pageIndex, g2d, scale);

          return (PAGE_EXISTS);
        }
      }
    });
    if (printJob.printDialog()) {
      printJob.print();
    }
  }

  /**
   * Creates a PDF in the same size as the project.
   * 
   * @param provider
   * @param file
   * @throws DocumentException
   * @throws FileNotFoundException
   */
  public void exportPDF(IDrawingProvider provider, File file) {
    Dimension d = provider.getSize();
    // We have to scale everything down because PDF resolution is slightly
    // lower.
    double factor = 1f * PDF_RESOLUTION / SCREEN_RESOLUTION;
    float totalWidth = (float) (factor * (2 * margin + d.getWidth()));
    float totalHeight = (float) (factor * (2 * margin + d.getHeight()));    
    PDFDocument doc = new PDFDocument();     
    Rectangle bounds = new Rectangle((int) Math.round(totalWidth), (int) Math.round(totalHeight));
    for (int i = 0; i < provider.getPageCount(); i++) {
      Page page = doc.createPage(bounds);      
      PDFGraphics2D g2d = page.getGraphics2D();
      g2d.setRenderingHint(PDFHints.KEY_DRAW_STRING_TYPE, PDFHints.VALUE_DRAW_STRING_TYPE_VECTOR);
      provider.draw(i, g2d, factor);
    }
    doc.writeToFile(file);
  }

  /**
   * Renders the project into a PNG file.
   * 
   * @param provider
   * @param file
   */
  public void exportPNG(IDrawingProvider provider, File file) {
    try {
      int pageCount = provider.getPageCount();
      Dimension d = provider.getSize();
      double factor = 1f * PNG_RESOLUTION / SCREEN_RESOLUTION;

      if (pageCount == 1) {
        BufferedImage image = new BufferedImage((int) (d.getWidth() * factor),
            (int) (d.getHeight() * factor), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();

        // g2d.scale(factor, factor);
        provider.draw(0, g2d, factor);
        g2d.dispose();

        ImageIO.write(image, "PNG", file);
      } else {
        for (int i = 0; i < pageCount; i++) {
          BufferedImage image = new BufferedImage((int) (d.getWidth() * factor),
              (int) (d.getHeight() * factor), BufferedImage.TYPE_INT_RGB);
          Graphics2D g2d = (Graphics2D) image.getGraphics();

          // g2d.scale(factor, factor);
          // Draw a page
          provider.draw(i, g2d, factor);
          // Move down
          g2d.translate(0, (int) (d.getHeight() * factor));
          g2d.dispose();
          ImageIO.write(image, "PNG",
              new File(file.getAbsolutePath().replaceAll("\\.png", "_" + (i + 1) + ".png")));
        }
      }
    } catch (Exception e) {
      System.out.println("Error exporting: " + e);
    }
  }
}
