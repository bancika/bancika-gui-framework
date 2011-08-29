package org.diylc.swingframework.objecttable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.diylc.swingframework.objecttable.ObjectListTableModel;
import org.junit.Before;
import org.junit.Test;

public class ObjectListTableModelTest {

	ObjectListTableModel<Point> model;

	@Before
	public void setUp() throws Exception {
		model = new ObjectListTableModel<Point>(Point.class, new String[] { "getX/setX", "getY",
				"action:Delete" });
		model.setData(Arrays.asList(new Point(10, 20), new Point(5, 2)));
	}

	@Test
	public void testIsCellEditable() {
		assertTrue(model.isCellEditable(0, 0));
		assertFalse(model.isCellEditable(0, 1));
		assertFalse(model.isCellEditable(0, 2));
	}

	@Test
	public void testGetActionColumns() {
		assertEquals(1, model.getActionColumns().size());
		assertEquals(2, model.getActionColumns().get(0).intValue());
	}

	@Test
	public void testGetColumnClass() {
		assertEquals(int.class, model.getColumnClass(0));
		assertEquals(int.class, model.getColumnClass(1));
		assertNotNull(model.getColumnClass(2));
	}

	@Test
	public void testGetColumnCount() {
		assertEquals(3, model.getColumnCount());
	}

	@Test
	public void testGetColumnName() {
		assertEquals("X", model.getColumnName(0));
		assertEquals("Y", model.getColumnName(1));
		assertEquals("Delete", model.getColumnName(2));
	}

	@Test
	public void testGetRowCount() {
		assertEquals(2, model.getRowCount());
	}

	@Test
	public void testGetValueAt() {
		assertEquals(10, model.getValueAt(0, 0));
		assertEquals(20, model.getValueAt(0, 1));
		assertEquals(5, model.getValueAt(1, 0));
		assertEquals(2, model.getValueAt(1, 1));
	}

	@Test
	public void testSetValueAt() {
		model.setValueAt(100, 0, 0);
		assertEquals(100, model.getValueAt(0, 0));
	}
}
