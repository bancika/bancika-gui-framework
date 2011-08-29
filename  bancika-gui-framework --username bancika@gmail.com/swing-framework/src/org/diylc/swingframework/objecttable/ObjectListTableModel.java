package org.diylc.swingframework.objecttable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

/**
 * {@link TableModel} that parses object of type <code>T</code> based on the
 * specified rules and shows each object in a separate rows. Individual
 * properties are shown as cell in the row.
 * 
 * @see ObjectListTable
 * 
 * @author Branislav Stojkovic
 * 
 * @param <T>
 */
public class ObjectListTableModel<T> extends AbstractTableModel {

	private static final Logger LOG = Logger.getLogger(ObjectListTableModel.class);

	private static final long serialVersionUID = 1L;

	private List<T> data;
	// private Class<?> dataClass;
	private List<Method> fieldGetters;
	private List<Method> fieldSetters;
	private List<Class<?>> fieldTypes;
	private List<String> fieldNames;
	private List<Integer> actionColumns;

	/**
	 * Creates a model based on the specified <code>dataClass</code> and
	 * <code>field</code>. Each field may have on of the following syntax forms: <br>
	 * <ul>
	 * <li><b>getterName</b> - creates a read only field which reads values
	 * using the specified getter</li>
	 * <li><b>getterName/setterName</b> - creates an editable field which uses
	 * getter to read and setter to write value</li>
	 * <li><b>action:actionName</b> - creates an action column with the
	 * specified name</li>
	 * <ul>
	 * <br>
	 * 
	 * @param dataClass
	 * @param fields
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public ObjectListTableModel(Class<? extends T> dataClass, String[] fields)
			throws SecurityException, NoSuchMethodException {
		// this.dataClass = dataClass;
		super();
		this.fieldGetters = new ArrayList<Method>();
		this.fieldSetters = new ArrayList<Method>();
		this.fieldTypes = new ArrayList<Class<?>>();
		this.fieldNames = new ArrayList<String>();
		this.actionColumns = new ArrayList<Integer>();
		for (int i = 0; i < fields.length; i++) {
			String field = fields[i];
			if (field.toLowerCase().startsWith("action:")) {
				fieldGetters.add(null);
				fieldSetters.add(null);
				fieldTypes.add(String.class);
				fieldNames.add(field.substring(7));
				actionColumns.add(i);
			} else {
				Method getter;
				Method setter;
				if (field.contains("/")) {
					String[] parts = field.split("/");
					getter = dataClass.getMethod(parts[0]);
					setter = dataClass.getMethod(parts[1], getter.getReturnType());
				} else {
					getter = dataClass.getMethod(field);
					setter = null;
				}
				fieldGetters.add(getter);
				fieldSetters.add(setter);
				fieldTypes.add(getter.getReturnType());
				fieldNames.add(convertGetterToName(getter.getName()));
			}
		}
	}

	public List<Integer> getActionColumns() {
		return actionColumns;
	}

	public void setData(List<T> data) {
		this.data = data;
		fireTableDataChanged();
	}

	public List<?> getData() {
		return data;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return fieldTypes.get(columnIndex);
	}

	@Override
	public int getColumnCount() {
		return fieldGetters.size();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return fieldNames.get(columnIndex);
	}

	@Override
	public int getRowCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			Method getter = fieldGetters.get(columnIndex);
			if (getter == null) {
				return null;
			}
			return getter.invoke(data.get(rowIndex));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return fieldSetters.get(columnIndex) != null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		try {
			fieldSetters.get(columnIndex).invoke(data.get(rowIndex), aValue);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	private String convertGetterToName(String getter) {
		String name;
		if (getter.startsWith("get")) {
			name = getter.substring(3);
		} else if (getter.startsWith("is")) {
			name = getter.substring(2);
		} else {
			name = getter;
		}
		for (int i = 1; i < name.length(); i++) {
			if (Character.isUpperCase(name.charAt(i))) {
				name = name.substring(0, i) + " " + name.substring(i);
				i++;
			}
		}
		name = name.replace('_', ' ');
		return name;
	}
}
