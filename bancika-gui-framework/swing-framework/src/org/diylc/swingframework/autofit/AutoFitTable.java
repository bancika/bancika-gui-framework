package org.diylc.swingframework.autofit;

import java.awt.Component;
import java.awt.FontMetrics;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

public class AutoFitTable extends JTable {

	private final static Logger LOG = Logger.getLogger(AutoFitTable.class);

	private static final long serialVersionUID = 1L;

	public AutoFitTable() {
		super();
	}

	public AutoFitTable(int numRows, int numColumns) {
		super(numRows, numColumns);
	}

	public AutoFitTable(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
	}

	public AutoFitTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
	}

	public AutoFitTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
	}

	public AutoFitTable(TableModel dm) {
		super(dm);
	}

	public AutoFitTable(Vector rowData, Vector columnNames) {
		super(rowData, columnNames);
	}

	public void autoFit(List<Integer> ignoreColumns) {
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		FontMetrics metrics = getTableHeader().getGraphics().getFontMetrics();
		for (int i = 0; i < getColumnCount(); i++) {
			int columnIdx = convertRowIndexToModel(i);
			if ((ignoreColumns != null) && ignoreColumns.contains(columnIdx)) {
				continue;
			}
			int width = metrics.stringWidth(getColumnName(i)) + 20;
			for (int j = 0; j < getRowCount(); j++) {
				int rowIdx = convertRowIndexToModel(j);
				Object value = getValueAt(j, i);
				TableCellRenderer renderer = getCellRenderer(j, i);
				if (renderer != null) {
					Component rendererComponent = renderer.getTableCellRendererComponent(this,
							value, false, false, rowIdx, columnIdx);
					try {
						Method getText = rendererComponent.getClass().getMethod("getText");
						String text = (String) getText.invoke(rendererComponent);
						int cellWidth = getGraphics().getFontMetrics(rendererComponent.getFont())
								.stringWidth(text) + 12;
						if (cellWidth > width) {
							width = cellWidth;
						}
					} catch (Exception e) {
					}
				}
			}
			LOG.debug("Column " + i + " width: " + width);
			getColumnModel().getColumn(i).setPreferredWidth(width);
		}
	}
}
