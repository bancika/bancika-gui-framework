package org.diylc.appframework.miscutils;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.util.Properties;

import org.diylc.appframework.miscutils.PropertyInjector;
import org.junit.Test;

public class PropertyInjectorTest {

	public static int intValue;
	public static double doubleValue;
	public static String stringValue;
	public static Color colorValue;
	public static Boolean booleanValue;

	@Test
	public void testInjectPropretiesInt() {
		Properties props = new Properties();
		int expected = 15;
		props.put(PropertyInjectorTest.class.getName() + ".intValue", expected);
		PropertyInjector.injectProperties(props);
		assertEquals(expected, intValue);
	}

	@Test
	public void testInjectPropretiesDouble() {
		Properties props = new Properties();
		double expected = 123.456;
		props.put(PropertyInjectorTest.class.getName() + ".doubleValue", Double.toString(expected));
		PropertyInjector.injectProperties(props);
		assertEquals(expected, doubleValue, 1e-10);
	}

	@Test
	public void testInjectPropretiesBoolean() {
		Properties props = new Properties();
		boolean expected = true;
		props.put(PropertyInjectorTest.class.getName() + ".booleanValue", Boolean
				.toString(expected));
		PropertyInjector.injectProperties(props);
		assertEquals(expected, booleanValue);
	}

	@Test
	public void testInjectPropretiesString() {
		Properties props = new Properties();
		boolean expected = true;
		props.put(PropertyInjectorTest.class.getName() + ".stringValue", expected);
		PropertyInjector.injectProperties(props);
		assertEquals(expected, stringValue);
	}

	@Test
	public void testInjectPropretiesColor() {
		Properties props = new Properties();
		String expectedHex = "#FFCC00";
		Color expected = Color.decode(expectedHex);
		props.put(PropertyInjectorTest.class.getName() + ".colorValue", expectedHex);
		PropertyInjector.injectProperties(props);
		assertEquals(expected, colorValue);
	}
}
