package org.diylc.swingframework.ruler;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;

import org.diylc.swingframework.IDrawingProvider;


/**
 * Undecorated {@link JDialog} that hooks onto a {@link JScrollPane} and renders
 * a thumbnail of the Viewport. When mouse is moved inside the dialog it shows
 * visible rect around the cursor. When clicked Viewport position is moved to
 * the mouse position.
 * 
 * @author Branislav Stojkovic
 */
public class NavigateDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final int MAX_SIZE = 192;

	private GraphicsConfiguration screenGraphicsConfiguration;

	private IDrawingProvider provider;

	public NavigateDialog(final JScrollPane scrollPane, IDrawingProvider provider) {
		super();
		this.provider = provider;
		setAlwaysOnTop(true);
		setUndecorated(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

		requestFocus();

		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = graphicsEnvironment.getScreenDevices();
		screenGraphicsConfiguration = devices[0].getDefaultConfiguration();

		addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowGainedFocus(WindowEvent e) {
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				setVisible(false);
			}
		});

		DrawComponent drawComponent = new DrawComponent(scrollPane);

		add(drawComponent);
		pack();
	}

	/**
	 * {@link JComponent} that renders thumbnail of the viewport component and
	 * visible rectangle.
	 * 
	 * @author Branislav Stojkovic
	 */
	class DrawComponent extends JComponent {

		private static final long serialVersionUID = 1L;
		private final JScrollPane scrollPane;
		private BufferedImage thumbnailImage;
		private VolatileImage bufferImage;
		double scaleRatio;

		public DrawComponent(JScrollPane scrollPane) {
			super();
			this.scrollPane = scrollPane;
			final Dimension dim = provider.getSize();

			if (dim.width > dim.height) {
				scaleRatio = (double) MAX_SIZE / dim.width;
			} else {
				scaleRatio = (double) MAX_SIZE / dim.height;
			}

			int width = (int) (dim.width * scaleRatio);
			int height = (int) (dim.height * scaleRatio);

			setPreferredSize(new Dimension(width, height));
			thumbnailImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = thumbnailImage.createGraphics();
			g2d
					.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.scale(scaleRatio, scaleRatio);
			provider.draw(0, g2d);
			g2d.setTransform(new AffineTransform());
			g2d.setColor(Color.lightGray);
			g2d.drawRect(0, 0, width - 1, height - 1);
			g2d.dispose();
			// addComponentListener(new ComponentAdapter() {
			//
			// @Override
			// public void componentResized(ComponentEvent e) {
			// re();
			// }
			// });
			addMouseMotionListener(new MouseMotionAdapter() {

				@Override
				public void mouseMoved(MouseEvent e) {
					repaint();
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					repaint();
				}
			});
			addMouseListener(new MouseAdapter() {

				@Override
				public void mouseExited(MouseEvent e) {
					NavigateDialog.this.setVisible(false);
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					int x = (int) (getMousePosition().x / scaleRatio - DrawComponent.this.scrollPane
							.getVisibleRect().width / 2);
					if (x > dim.width
							- DrawComponent.this.scrollPane.getViewport().getVisibleRect().width
							- 1) {
						x = dim.width
								- DrawComponent.this.scrollPane.getViewport().getVisibleRect().width
								- 1;
					}
					if (x < 0) {
						x = 0;
					}
					int y = (int) (getMousePosition().y / scaleRatio - DrawComponent.this.scrollPane
							.getVisibleRect().height / 2);
					if (y > dim.height
							- DrawComponent.this.scrollPane.getViewport().getVisibleRect().height
							- 1) {
						y = dim.height
								- DrawComponent.this.scrollPane.getViewport().getVisibleRect().height
								- 1;
					}
					if (y < 0) {
						y = 0;
					}
					DrawComponent.this.scrollPane.getViewport().setViewPosition(new Point(x, y));
					NavigateDialog.this.setVisible(false);
				}
			});
		}

		@Override
		public void paint(Graphics g) {
			if (bufferImage == null) {
				createBufferImage();
			}
			do {
				Graphics2D g2d = (Graphics2D) bufferImage.createGraphics();
				int validation = bufferImage.validate(screenGraphicsConfiguration);
				if (validation == VolatileImage.IMAGE_INCOMPATIBLE) {
					createBufferImage();
				}
				g2d.drawImage(thumbnailImage, new AffineTransform(), NavigateDialog.this);
				if (getMousePosition() == null) {
					return;
				}

				//Composite oldComposite = g2d.getComposite();
				try {
					AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
							0.1f);
					g2d.setComposite(composite);
					Rectangle visibleRect = ((JComponent) scrollPane.getViewport().getView())
							.getVisibleRect();
					// Dimension dim = provider.getSize();
					// double scaleRatio = 1d * visibleRect.height / dim.height;
					int visibleWidth = (int) (visibleRect.getWidth() * scaleRatio);
					int visibleHeight = (int) (visibleRect.getHeight() * scaleRatio);
					int x = getMousePosition().x - visibleWidth / 2;
					if (x < 0) {
						x = 0;
					}
					if (x > getWidth() - visibleWidth - 1) {
						x = getWidth() - visibleWidth - 1;
					}
					int y = getMousePosition().y - visibleHeight / 2;
					if (y < 0) {
						y = 0;
					}
					if (y > getHeight() - visibleHeight - 1) {
						y = getHeight() - visibleHeight - 1;
					}

					g2d.setColor(Color.blue);
					g2d.fillRect(x, y, visibleWidth, visibleHeight);

					g2d.setColor(Color.blue.darker());
					g2d.drawRect(x, y, visibleWidth, visibleHeight);
				} finally {
					g2d.dispose();					
				}				
			} while (bufferImage.contentsLost());

			g.drawImage(bufferImage, 0, 0, this);			
		}

		@Override
		public void update(Graphics g) {
			paint(g);
		}

		public void createBufferImage() {
			bufferImage = screenGraphicsConfiguration.createCompatibleVolatileImage(getWidth(),
					getHeight());
		}
	}
}
