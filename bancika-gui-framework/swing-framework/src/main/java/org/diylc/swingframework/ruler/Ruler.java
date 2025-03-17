package org.diylc.swingframework.ruler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.VolatileImage;
import java.util.Objects;
import javax.swing.JComponent;

/**
 * {@link JComponent} that renders ruler. It features configurable units (cm or
 * in), orientation and ability to indicate cursor position.
 * 
 * @author Branislav Stojkovic
 */
public class Ruler extends JComponent {

	private static final long serialVersionUID = 1L;
	private static final long REPAINT_THROTTLE_MS = 100; // 10 repaints per second

	public static final Color COLOR = Color.decode("#C0FF3E");
	public static final Color SELECTION_COLOR = Color.red;
	public static final Color CURSOR_COLOR = Color.blue;

	public static final int PIXELS_PER_INCH = Toolkit.getDefaultToolkit()
			.getScreenResolution();
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	public static final int SIZE = 18;
	public static final int MIN_TICK_SPACING = 8;

	public int orientation;
	private boolean isMetric;
	// private float increment;
	private float unitSize;
	private int indicatorValue = -1;
	private float ticksPerUnit;
	private long lastRepaintTime = 0;

	private Graphics bufferGraphics;
	private Image bufferImage;
	
	private GraphicsConfiguration screenGraphicsConfiguration;
	public boolean useHardwareAcceleration = false;

	private double zoomLevel = 1d;

	private double cmSpacing;
	private double inSpacing;
	
	private double zeroLocation = 0;
	
	private InchSubdivision inSubdivision;

	private Rectangle2D selectionRect = null;

	public Ruler(int orientation, boolean isMetric) {
		this(orientation, isMetric, 0, 0);
	}
	
	public Ruler(int orientation, boolean isMetric, double cmSpacing,
        double inSpacing) {
	  this(orientation, isMetric, cmSpacing, inSpacing, InchSubdivision.BASE_10);
	}

	public Ruler(int orientation, boolean isMetric, double cmSpacing,
			double inSpacing, InchSubdivision inSubdivision) {
		this.orientation = orientation;
		this.isMetric = isMetric;
		this.cmSpacing = cmSpacing;
		this.inSpacing = inSpacing;
		this.inSubdivision = inSubdivision;
		setIncrementAndUnits();

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
			  bufferImage = null;
			}
		});
	}
	
	protected void createBufferImage() {
	  if (useHardwareAcceleration) {
        bufferImage = getScreenGraphicsConfiguration().createCompatibleVolatileImage(getWidth(), getHeight());
          ((VolatileImage) bufferImage).validate(screenGraphicsConfiguration);
        } else {
          bufferImage = createImage(getWidth(), getHeight());
        }

        bufferGraphics = bufferImage.getGraphics();    
	}
	
    public void setUseHardwareAcceleration(boolean useHardwareAcceleration) {
      this.useHardwareAcceleration = useHardwareAcceleration;
      bufferImage = null;
    }

    public void setSelectionRect(Rectangle2D selectionRect) {
      if (Objects.equals(selectionRect, this.selectionRect)) {
        return;
      }
      this.selectionRect = selectionRect;
      throttledRepaint();
    }

	public void setZoomLevel(double zoomLevel) {
		if (this.zoomLevel == zoomLevel) {
			return;
		}
		this.zoomLevel = zoomLevel;
		setIncrementAndUnits();
		throttledRepaint();
	}

	public void setIsMetric(boolean isMetric) {
		if (this.isMetric == isMetric) {
			return;
		}
		this.isMetric = isMetric;
		setIncrementAndUnits();
		throttledRepaint();
	}
	
	public void setZeroLocation(double zeroLocation) {
		if (this.zeroLocation == zeroLocation) {
			return;
		}
		this.zeroLocation = zeroLocation;
		throttledRepaint();
    }	
	
	public InchSubdivision getInSubdivision() {
		return inSubdivision;
	}
	
	public void setInSubdivision(InchSubdivision inSubdivision) {
		if (this.inSubdivision == inSubdivision) {
			return;
		}
		this.inSubdivision = inSubdivision;
		setIncrementAndUnits();
		throttledRepaint();
	}

	/**
	 * Changes cursor position. If less than zero, indication will not be
	 * rendered. For horizontal ruler this should be X coordinate of mouse
	 * position, and Y for vertical.
	 * 
	 * @param indicatortValue
	 */
	public void setIndicatorValue(int indicatortValue) {
		if (this.indicatorValue == indicatortValue) {
			return;
		}
		this.indicatorValue = indicatortValue;
		throttledRepaint();
	}
	
	private GraphicsConfiguration getScreenGraphicsConfiguration() {
	  if (screenGraphicsConfiguration == null) {
	    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] devices = graphicsEnvironment.getScreenDevices();
	    screenGraphicsConfiguration = devices[0].getDefaultConfiguration();
	  }
      return screenGraphicsConfiguration;
    }

	private void setIncrementAndUnits() {
		if (isMetric) {
			unitSize = (float) ((cmSpacing == 0 ? PIXELS_PER_INCH / 2.54f
					: cmSpacing) * zoomLevel);
			if (unitSize > 10 * MIN_TICK_SPACING)
			  ticksPerUnit = 10;
			else
			  ticksPerUnit = 4;
		} else {
			ticksPerUnit = inSubdivision == InchSubdivision.BASE_10 ? 10 : 16;
			unitSize = (float) ((inSpacing == 0 ? (PIXELS_PER_INCH) : inSpacing) * zoomLevel);
		}
		while (unitSize / ticksPerUnit < MIN_TICK_SPACING && ticksPerUnit % 2 == 0)
		  ticksPerUnit /= 2;
		// ticksPerUnit = 1;
		// while (unitSize / ticksPerUnit > 48) {
		// ticksPerUnit *= 2;
		// }
		// while (unitSize / ticksPerUnit < 24) {
		// ticksPerUnit /= 2;
		// }
	}

	public boolean isMetric() {
		return this.isMetric;
	}

	public void setPreferredHeight(int ph) {
		setPreferredSize(new Dimension(SIZE, ph));
	}

	public void setPreferredWidth(int pw) {
		setPreferredSize(new Dimension(pw, SIZE));
	}

	protected void paintComponent(Graphics g) {
	    if (bufferImage == null)
	      createBufferImage();
		if (bufferGraphics == null) {
			return;
		}
		Rectangle clipRect = g.getClipBounds();

		bufferGraphics.setColor(COLOR);
		bufferGraphics.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);

		// Do the ruler labels in a small font that's black.
		bufferGraphics.setFont(new Font("SansSerif", Font.PLAIN, 10));
		bufferGraphics.setColor(Color.black);
		
		double offset = (int) (zeroLocation * unitSize);

		// Some vars we need.
		double start = 0;
		int tickLength = 0;
		String text = null;
		// int count;
		double increment = unitSize / ticksPerUnit;

		// Use clipping bounds to calculate first and last tick locations.
		int firstUnit;
		if (orientation == HORIZONTAL) {
			firstUnit = (int) (clipRect.x / unitSize);
			start = (int) (clipRect.x / unitSize) * unitSize;
			// count = Math.round(clipRect.width / increment) + 1;
		} else {
			firstUnit = (int) (clipRect.y / unitSize);
			start = (int) (clipRect.y / unitSize) * unitSize;
			// count = Math.round(clipRect.height / increment) + 1;
		}
		
		start += offset;

		// ticks and labels
		int x = 0;
		int i = (int) -offset;
		// System.out.print("start\n");
		while (x < (orientation == HORIZONTAL ? (clipRect.x + clipRect.width)
				: (clipRect.y + clipRect.height))) {
			if ((ticksPerUnit <= 1) || (i % Math.round(ticksPerUnit) == 0)) {
				tickLength = 10;
				text = Integer.toString(firstUnit
						+ Math.round(i / ticksPerUnit));
				// System.out.printf("firstUnit: %s; ticksPerUnit: %s; i: %s; text: %s\n",
				// firstUnit, ticksPerUnit, i, text);
			} else {
				tickLength = 7;
				int j = i % Math.round(ticksPerUnit);
                while (j < 0)
                  j += ticksPerUnit;
				if (isMetric) {
  				  if (ticksPerUnit == 10) {
  					  if (j != 5)
  						  tickLength -= 2;
  				  } else { // 4
  					if (j != 2) {					
						tickLength -= 2;
  					}
  				  }
				} else {
					if (Math.round(ticksPerUnit) == 10) { // 10
						if (j != 5) {					
							tickLength -= 2;
						}
					} else {
						int pow = (int) Math.round(Math.log(ticksPerUnit) / Math.log(2));

						if (pow == 2) {
							if (j != 2) {					
								tickLength -= 2;
							}
						} else if (pow == 3) {
							if (j != 4) {					
								tickLength -= 2;
							}
							if (j != 2 && j != 4 && j != 6) {                   
                              tickLength -= 2;
                          }
						} else if (pow == 4) {
							if (j != 8) {					
								tickLength -= 2;
							}
							if (j != 4 && j != 8 && j != 12) {					
								tickLength -= 2;
							}
						}
					}					
				}
				text = null;
			}

			x = (int) (start + i * increment);

			if (tickLength != 0) {
				if (orientation == HORIZONTAL) {
					bufferGraphics.drawLine(x, SIZE - 1, x, SIZE - tickLength
							- 1);
					if (text != null) {
						bufferGraphics.drawString(text, x + 2, 15);
					}
				} else {
					bufferGraphics.drawLine(SIZE - 1, x, SIZE - tickLength - 1,
							x);
					if (text != null) {
						FontMetrics fm = bufferGraphics.getFontMetrics();
						bufferGraphics.drawString(text, SIZE
								- (int) fm
										.getStringBounds(text, bufferGraphics)
										.getWidth() - 2, x + 10);
					}
				}
			}
			i++;
		}
		// System.out.print("end\n");

		// highlight value
		if (indicatorValue >= 0) {
			bufferGraphics.setColor(CURSOR_COLOR);
			if (orientation == HORIZONTAL) {
				if (indicatorValue < getWidth()) {
					bufferGraphics.drawLine(indicatorValue, 0, indicatorValue,
							SIZE - 1);
				}
			} else {
				if (indicatorValue < getHeight()) {
					bufferGraphics.drawLine(0, indicatorValue, SIZE - 1,
							indicatorValue);
				}
			}
		}

		// selection
		if (selectionRect != null) {
			bufferGraphics.setColor(SELECTION_COLOR);
			if (orientation == HORIZONTAL) {
				bufferGraphics.drawLine((int) selectionRect.getX(), 0,
						(int) selectionRect.getX(), SIZE - 1);
				bufferGraphics
						.drawLine((int) (selectionRect.getX() + selectionRect
								.getWidth()), 0,
								(int) (selectionRect.getX() + selectionRect
										.getWidth()), SIZE - 1);
			} else {
				bufferGraphics.drawLine(0, (int) selectionRect.getY(),
						SIZE - 1, (int) selectionRect.getY());
				bufferGraphics
						.drawLine(0,
								(int) (selectionRect.getY() + selectionRect
										.getHeight()), SIZE - 1,
								(int) (selectionRect.getY() + selectionRect
										.getHeight()));
			}
		}

		// lines
		bufferGraphics.setColor(Color.black);
		if (orientation == HORIZONTAL) {
			bufferGraphics.drawLine(0, SIZE - 1, getWidth(), SIZE - 1);
		} else {
			bufferGraphics.drawLine(SIZE - 1, 0, SIZE - 1, getHeight());
		}
		// Draw the buffer
		g.drawImage(bufferImage, 0, 0, this);
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	public enum InchSubdivision {
	  BASE_10, BASE_2,
	}

	private void throttledRepaint() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastRepaintTime >= REPAINT_THROTTLE_MS) {
			repaint();
			lastRepaintTime = currentTime;
		} else {
			// Schedule another repaint after the throttling period to ensure we show the latest value
			javax.swing.Timer timer = new javax.swing.Timer((int)(REPAINT_THROTTLE_MS - (currentTime - lastRepaintTime)), null);
			timer.addActionListener(e -> {
				repaint();
				lastRepaintTime = System.currentTimeMillis();
				timer.stop();
			});
			timer.setRepeats(false);
			timer.start();
		}
	}
}
