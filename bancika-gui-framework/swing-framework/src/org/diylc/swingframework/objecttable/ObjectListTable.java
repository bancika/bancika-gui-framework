package org.diylc.swingframework.objecttable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import org.diylc.swingframework.autofit.AutoFitTable;


/**
 * Customized {@link JTable} that shows objects displayed as rows in the table.
 * Cells in each rows are read from the object.
 * 
 * @author Branislav Stojkovic
 * 
 * @param T
 *            type of data that will be stored in the table
 */
public class ObjectListTable<T> extends AutoFitTable {

	private static final long serialVersionUID = 1L;
	private IActionProcessor<T> clickListener;

	/**
	 * Creates a new {@link ObjectListTable} that will display data of the
	 * specified type. For each element in the <code>fields</code> list a new
	 * column is created. There are two types of columns: <br>
	 * <ul>
	 * <li>Data columns; field is specified with the getter name</li>
	 * <li>Actions; field is specified in <code>action:<i>action name</i></code>
	 * format</li>
	 * </ul>
	 * 
	 * The example below shows a simple class that has two fields. We are making
	 * a table with three columns, two of which will show the data and one will
	 * trigger an action. <br>
	 * <br>
	 * <code>class Point {<br>
	 * &nbsp;&nbsp;int x, y;<br>
	 * &nbsp;&nbsp;public int getX() { return x; }<br>
	 * &nbsp;&nbsp;public int getY() { return y; }<br>
	 * }<br><br>
	 * new ObjectListTable&lt;Point&gt;(Point.class, new String[] {"getX/setY", "getY", "action:Delete"}, provider);
	 * </code> <br>
	 * <br>
	 * The resulting table will contain columns named "X", "Y" and "Delete" in
	 * that order. Column "Delete" contains buttons that trigger the action.
	 * Column "X" is editable because it has a setter defined. Event will be
	 * fired back the user though {@link IActionProcessor}.<br>
	 * <br>
	 * 
	 * @see IActionProcessor
	 * 
	 * @param dataClass
	 * @param fields
	 * @param clickListener
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings("unchecked")
	public ObjectListTable(Class<? extends T> dataClass, String[] fields,
			IActionProcessor<T> clickListener) throws SecurityException, NoSuchMethodException {
		super(new ObjectListTableModel<T>(dataClass, fields));
		this.clickListener = clickListener;

		for (Integer idx : ((ObjectListTableModel<T>) getModel()).getActionColumns()) {
			getColumnModel().getColumn(idx).setCellRenderer(
					new ActionTableCellRenderer(this.clickListener.getActionIcon(getModel()
							.getColumnName(idx)), this.clickListener.getActionLabel(getModel()
							.getColumnName(idx))));

		}

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int columnIndex = columnAtPoint(e.getPoint());
				if (((ObjectListTableModel) getModel()).getActionColumns().contains(columnIndex)) {
					int rowIndex = rowAtPoint(e.getPoint());
					Object value = ((ObjectListTableModel) getModel()).getData().get(rowIndex);
					ObjectListTable.this.clickListener.actionExecuted((T) value, getModel()
							.getColumnName(columnIndex));
				}
			}

		});
		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				int columnIndex = columnAtPoint(e.getPoint());
				if (((ObjectListTableModel) getModel()).getActionColumns().contains(columnIndex)) {
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					setCursor(Cursor.getDefaultCursor());
				}
			}
		});
	}

	/**
	 * Sets the data to the table. A new row is created for each object in the
	 * list.
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public void setData(List<T> data) {
		((ObjectListTableModel<T>) getModel()).setData(data);
	}

	/**
	 * Paints empty rows too, after letting the UI delegate do its painting.
	 */
	public void paint(Graphics g) {
		super.paint(g);
	}

	// /**
	// * Changes the behavior of a table in a JScrollPane to be more like the
	// behavior of JList,
	// which
	// * expands to fill the available space. JTable normally restricts its size
	// to just what's
	// needed
	// * by its model.
	// */
	// public boolean getScrollableTracksViewportHeight() {
	// if (getParent() instanceof JViewport) {
	// JViewport parent = (JViewport) getParent();
	// return (parent.getHeight() > getPreferredSize().height);
	// }
	// return false;
	// }

	/**
	 * Returns the appropriate background color for the given row.
	 */
	protected Color colorForRow(int row) {
		return (row % 2 == 1) ? new Color(240, 240, 240) : getBackground();
	}

	/**
	 * Shades alternate rows in different colors.
	 */
	@SuppressWarnings("unchecked")
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);
		if (!((ObjectListTableModel<T>) getModel()).getActionColumns().contains(column)) {
			if (isCellSelected(row, column) == false) {
				c.setBackground(colorForRow(row));
				c.setForeground(UIManager.getColor("Table.foreground"));
			} else {
				c.setBackground(UIManager.getColor("Table.selectionBackground"));
				c.setForeground(UIManager.getColor("Table.selectionForeground"));
			}
		}
		return c;
	}
}
