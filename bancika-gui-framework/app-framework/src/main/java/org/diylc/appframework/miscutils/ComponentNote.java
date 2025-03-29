package org.diylc.appframework.miscutils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ComponentNote extends JPanel {

	private static final long serialVersionUID = 1L;

	private static int MARGIN = 4;

	private JComponent mainComponent;

	public ComponentNote(Icon icon, String text, JComponent mainComponent) {
		super(new BorderLayout());
		this.mainComponent = mainComponent;
		JLabel label = new JLabel(text);
		if (icon != null) {
			label.setIcon(icon);
		}
		add(label, BorderLayout.CENTER);

		JLabel closeLabel = new JLabel("X");
		closeLabel.setBorder(BorderFactory.createEmptyBorder(0, MARGIN, 0, 0));
		add(closeLabel, BorderLayout.EAST);
		closeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		closeLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
			}
		});

		setOpaque(true);
		setBackground(Color.orange);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black),
				BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN)));
		mainComponent.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentMoved(ComponentEvent e) {
				updateLocation();
			}

			@Override
			public void componentResized(ComponentEvent e) {
				updateLocation();
			}
		});
		mainComponent.getParent().addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				updateLocation();
			}
		});
		updateLocation();
	}

	private void updateLocation() {
		Rectangle mainBounds = SwingUtilities.convertRectangle(mainComponent.getParent(),
				mainComponent.getBounds(), null);
		Rectangle rootBounds = SwingUtilities.getRoot(mainComponent).getBounds();
		Dimension bounds = getPreferredSize();
		int x = mainBounds.x + (mainBounds.width - bounds.width) / 2;
		if (x + bounds.width > rootBounds.width) {
			x -= x + bounds.width - rootBounds.width + 2 * MARGIN;
		}
		int y = mainBounds.y - bounds.height - mainBounds.height - 2 * MARGIN;
		setBounds(new Rectangle(x, y, bounds.width, bounds.height));
	}
}