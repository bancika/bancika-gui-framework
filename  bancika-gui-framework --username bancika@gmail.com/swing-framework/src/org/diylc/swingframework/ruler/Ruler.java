package org.diylc.swingframework.ruler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * {@link JComponent} that renders ruler. It features configurable units (cm or
 * in), orientation and ability to indicate cursor position.
 * 
 * @author Branislav Stojkovic
 */
public class Ruler extends JComponent {

	private static final long serialVersionUID = 1L;

	public static final Color COLOR = Color.decode("#C0FF3E");
	public static final Color SELECTION_COLOR = Color.blue;

	public static final int PIXELS_PER_INCH = Toolkit.getDefaultToolkit()
			.getScreenResolution();
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	public static final int SIZE = 18;

	public int orientation;
	private boolean isMetric;
	// private float increment;
	private float unitSize;
	private int indicatorValue = -1;
	private float ticksPerUnit;

	private Graphics bufferGraphics;
	private Image bufferImage;

	private double zoomLevel = 1d;

	private double cmSpacing;
	private double inSpacing;

	private Rectangle2D selectionRect = null;

	public Ruler(int orientation, boolean isMetric) {
		this(orientation, isMetric, 0, 0);
	}

	public Ruler(int orientation, boolean isMetric, double cmSpacing,
			double inSpacing) {
		this.orientation = orientation;
		this.isMetric = isMetric;
		this.cmSpacing = cmSpacing;
		this.inSpacing = inSpacing;
		setIncrementAndUnits();

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				bufferImage = new BufferedImage(e.getComponent().getWidth(), e
						.getComponent().getHeight(), BufferedImage.TYPE_INT_RGB);
				bufferGraphics = bufferImage.getGraphics();
			}
		});
	}

	public void setSelectionRect(Rectangle2D selectionRect) {
		this.selectionRect = selectionRect;
		repaint();
	}

	public void setZoomLevel(double zoomLevel) {
		this.zoomLevel = zoomLevel;
		setIncrementAndUnits();
		repaint();
	}

	public void setIsMetric(boolean isMetric) {
		this.isMetric = isMetric;
		setIncrementAndUnits();
		repaint();
	}

	/**
	 * Changes cursor position. If less than zero, indication will not be
	 * rendered. For horizontal ruler this should be X coordinate of mouse
	 * position, and Y for vertical.
	 * 
	 * @param indicatortValue
	 */
	public void setIndicatorValue(int indicatortValue) {
		this.indicatorValue = indicatortValue;
	}

	private void setIncrementAndUnits() {
		if (isMetric) {
			unitSize = (float) ((cmSpacing == 0 ? PIXELS_PER_INCH / 2.54f
					: cmSpacing) * zoomLevel);
			ticksPerUnit = 4;
		} else {
			ticksPerUnit = 10;
			unitSize = (float) ((inSpacing == 0 ? (PIXELS_PER_INCH) : inSpacing) * zoomLevel);
		}
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
		if (bufferGraphics == null) {
			return;
		}
		Rectangle clipRect = g.getClipBounds();

		bufferGraphics.setColor(COLOR);
		bufferGraphics.fillRect(clipRect.x, clipRect.y, clipRect.width,
				clipRect.height);

		// Do the ruler labels in a small font that's black.
		bufferGraphics.setFont(new Font("SansSerif", Font.PLAIN, 10));
		bufferGraphics.setColor(Color.black);

		// Some vars we need.
		float start = 0;
		int tickLength = 0;
		String text = null;
		// int count;
		float increment = unitSize / ticksPerUnit;

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

		// ticks and labels
		int x = 0;
		int i = 0;
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
				if (isMetric) {
					tickLength -= 2 * (i % Math.round(ticksPerUnit) % 2);
				} else if (i % Math.round(ticksPerUnit) != 5) {
					tickLength -= 2;
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
			bufferGraphics.setColor(Color.red);
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
}
