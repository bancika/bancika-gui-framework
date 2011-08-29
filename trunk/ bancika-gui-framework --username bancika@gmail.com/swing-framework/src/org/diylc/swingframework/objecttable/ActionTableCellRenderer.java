package org.diylc.swingframework.objecttable;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ActionTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private Icon icon;
	private String actionText;

	public ActionTableCellRenderer(Icon icon, String actionText) {
		super();
		this.icon = icon;
		this.actionText = actionText;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		// JLabel label = (JLabel) super.getTableCellRendererComponent(table,
		// value, isSelected,
		// hasFocus, row, column);
		JButton button = new JButton(actionText);
		button.setText(actionText);
		button.setIcon(icon);
		// button.setBackground(Color.pink);
		// button.setOpaque(true);
		// button.setHorizontalAlignment(SwingConstants.CENTER);
		return button;
	}
}
