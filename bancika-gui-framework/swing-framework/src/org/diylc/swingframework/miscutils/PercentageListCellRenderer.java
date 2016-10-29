package org.diylc.swingframework.miscutils;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * {@link ListCellRenderer} that shows {@link Double} values as percents, e.g.
 * 0.5 will be rendered as 50%.
 * 
 * @author Branislav Stojkovic
 * 
 */
public class PercentageListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
		label.setText(Integer.toString((int) (100 * (Double) value)) + "%");
		return label;
	}
}
