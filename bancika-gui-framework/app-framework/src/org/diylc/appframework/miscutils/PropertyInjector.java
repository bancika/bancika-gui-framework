package org.diylc.appframework.miscutils;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * Utility class that injects the provided properties into class static fields.
 * To illustrate it, consider the following example: <br>
 * <code>
 * com.bancika.ClassA.INT_COST=15
 * com.bancika.ClassB.STR_CONST=Test
 * </code> <br>
 * After {@link #injectProperties(Properties)} is executed, static field
 * INT_COST in the com.bancika.ClassA class will be populated with int value of
 * 15. Similar for the other property line.<br>
 * Currently it supports Boolean, Integer, Double and Color together with their
 * primitives.
 * 
 * @author Branislav Stojkovic
 */
public class PropertyInjector {

	private static final Logger LOG = Logger.getLogger(PropertyInjector.class);

	private PropertyInjector() {
	}

	/**
	 * Injects properties from the provided {@link Properties} object.
	 * 
	 * @param properties
	 */
	public static void injectProperties(Properties properties) {
		for (Entry<Object, Object> entry : properties.entrySet()) {
			String key = entry.getKey().toString();
			String value = entry.getValue().toString();
			try {
				String className = key.substring(0, key.lastIndexOf('.'));
				String fieldName = key.substring(key.lastIndexOf('.') + 1);
				Class<?> clazz = Class.forName(className);
				try {
					Field field = clazz.getField(fieldName);
					field.setAccessible(true);
					Class<?> fieldType = field.getType();
					if (String.class.isAssignableFrom(fieldType)) {
						field.set(null, value);
					}
					if (Integer.class.isAssignableFrom(fieldType)
							|| int.class.isAssignableFrom(fieldType)) {
						int intValue = Integer.parseInt(value);
						field.set(null, intValue);
					}
					if (Double.class.isAssignableFrom(fieldType)
							|| double.class.isAssignableFrom(fieldType)) {
						double doubleValue = Double.parseDouble(value);
						field.set(null, doubleValue);
					}
					if (Boolean.class.isAssignableFrom(fieldType)
							|| boolean.class.isAssignableFrom(fieldType)) {
						boolean booleanValue = Boolean.parseBoolean(value);
						field.set(null, booleanValue);
					}
					if (Color.class.isAssignableFrom(fieldType)) {
						Color color = Color.decode(value);
						field.set(null, color);
					}
				} catch (SecurityException e) {
					LOG.warn("Could not inject " + key + ". Field access denied: " + fieldName);
				} catch (NoSuchFieldException e) {
					LOG.warn("Could not inject " + key + ". Field not found: " + fieldName);
				} catch (IllegalArgumentException e) {
					LOG.warn("Could not inject " + key + ". Illegal access: " + fieldName);
				} catch (IllegalAccessException e) {
					LOG.warn("Could not inject " + key + ". Field illegal access: " + fieldName);
				}
			} catch (StringIndexOutOfBoundsException e) {
				LOG.warn("Property name does not match format ClassName.FIELD_NAME: " + key);
			} catch (ClassNotFoundException e) {
				LOG.warn("Could not inject " + key + ". Class not found: " + key);
			}
		}
	}
}
