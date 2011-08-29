package org.diylc.swingframework;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.Format;

import javax.swing.JComponent;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

/**
 * {@link JComponent} that draws current memory usage as a vertical bar. Details
 * are provided in the tooltip. Click on the component will run the garbage
 * collector. Memory usage information is read periodically.
 * 
 * @author Branislav Stojkovic
 */
public class MemoryBar extends JComponent {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(MemoryBar.class);

	private static final int DELAY = 10000;
	private static final boolean USE_LOG = false;
	private static final Format format = new DecimalFormat("0.00");
	private static final String logPattern = "%s MB of %s MB free, max %s MB is available";
	private static final String tooltipPattern = "<html>%s MB of %s MB free<br>"
			+ "Max %s MB is available<br>" + "Click to run the garbage collector</html>";

	private static final double THRESHOLD = 0.1d;

	private Thread bgThread;

	private long totalMemory = 0;
	private long freeMemory = 0;
	private long maxMemory = 0;
	private double percentFree = 0;

	private boolean autoGC;

	public MemoryBar(boolean autoGC) {
		super();
		this.autoGC = autoGC;

		setPreferredSize(new Dimension(16, 20));
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		bgThread = new Thread(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				while (true) {
					totalMemory = Runtime.getRuntime().totalMemory();
					freeMemory = Runtime.getRuntime().freeMemory();
					maxMemory = Runtime.getRuntime().maxMemory();
					percentFree = (double) freeMemory / totalMemory;
					// Run the garbage collector if needed.
					if (MemoryBar.this.autoGC && percentFree < THRESHOLD) {
						System.gc();
					}
					setToolTipText(String.format(tooltipPattern, format
							.format(convertToMb(freeMemory)), format
							.format(convertToMb(totalMemory)), format
							.format(convertToMb(maxMemory))));
					if (USE_LOG) {
						LOG.debug(String.format(logPattern, format.format(convertToMb(freeMemory)),
								format.format(convertToMb(totalMemory)), format
										.format(convertToMb(maxMemory))));
					}
					repaint();
					try {
						Thread.sleep(DELAY);
					} catch (InterruptedException e) {
					}
				}
			}
		});
		bgThread.start();

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				LOG.info("Running GC");
				System.gc();
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

		int barHeight = (int) ((1 - percentFree) * getHeight());
		if (percentFree < THRESHOLD) {
			g2d.setColor(Color.red);
		} else {
			g2d.setColor(UIManager.getColor("List.selectionBackground"));
		}
		g2d.fillRect(0, getHeight() - barHeight - 1, getWidth() - 1, barHeight);

		g2d.setColor(UIManager.getColor("InternalFrame.borderShadow"));
		g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}

	private double convertToMb(long size) {
		return (double) size / 1024 / 1024;
	}

	public void dispose() {
		if (bgThread != null) {
			bgThread.interrupt();
		}
	}
}
