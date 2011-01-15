package com.diyfever.gui.miscutils;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class Utils {

	// Used to identify the windows platform.
	private static final String WIN_ID = "Windows";
	// The default system browser under windows.
	private static final String WIN_PATH = "rundll32";
	// The flag to display a url.
	private static final String WIN_FLAG = "url.dll,FileProtocolHandler";
	// The default browser under unix.
	private static final String UNIX_PATH = "netscape";
	// The flag to display a url.
	private static final String UNIX_FLAG = "-remote openURL";

	/**
	 * Display a file in the system browser. If you want to display a file, you
	 * must include the absolute path name.
	 * 
	 * @param url
	 *            the file's url (the url must start with either "http://" or
	 *            "file://").
	 */
	public static void openURL(String url) {
		boolean windows = isWindowsPlatform();
		String cmd = null;
		try {
			if (windows) {
				// cmd = 'rundll32 url.dll,FileProtocolHandler http://...'
				cmd = WIN_PATH + " " + WIN_FLAG + " " + url;
				Runtime.getRuntime().exec(cmd);
			} else {
				// Under Unix, Netscape has to be running for the "-remote"
				// command to work. So, we try sending the command and
				// check for an exit value. If the exit command is 0,
				// it worked, otherwise we need to start the browser.
				// cmd = 'netscape -remote openURL(http://www.java-tips.org)'
				cmd = UNIX_PATH + " " + UNIX_FLAG + "(" + url + ")";
				Process p = Runtime.getRuntime().exec(cmd);
				try {
					// wait for exit code -- if it's 0, command worked,
					// otherwise we need to start the browser up.
					int exitCode = p.waitFor();
					if (exitCode != 0) {
						// Command failed, start up the browser
						// cmd = 'netscape http://www.java-tips.org'
						cmd = UNIX_PATH + " " + url;
						p = Runtime.getRuntime().exec(cmd);
					}
				} catch (InterruptedException x) {
					System.err.println("Error bringing up browser, cmd='" + cmd + "'");
					System.err.println("Caught: " + x);
				}
			}
		} catch (IOException x) {
			// couldn't exec browser
			System.err.println("Could not invoke browser, command=" + cmd);
			System.err.println("Caught: " + x);
		}
	}

	public static Object clone(Object o) {
		Object clone = null;

		try {
			clone = o.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		// Walk up the superclass hierarchy
		for (Class<?> obj = o.getClass(); !obj.equals(Object.class); obj = obj.getSuperclass()) {
			Field[] fields = obj.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				try {
					// for each class/superclass, copy all fields
					// from this object to the clone
					fields[i].set(clone, clone(fields[i].get(o)));
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}
		return clone;
	}

	/**
	 * Try to determine whether this application is running under Windows or
	 * some other platform by examing the "os.name" property.
	 * 
	 * @return true if this application is running under a Windows OS
	 */
	public static boolean isWindowsPlatform() {
		String os = System.getProperty("os.name");
		if (os != null && os.startsWith(WIN_ID))
			return true;
		else
			return false;

	}

	/**
	 * Creates a rectangle which opposite corners are lying in the specified
	 * points.
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static Rectangle createRectangle(Point p1, Point p2) {
		int minX = p1.x < p2.x ? p1.x : p2.x;
		int minY = p1.y < p2.y ? p1.y : p2.y;
		int width = Math.abs(p1.x - p2.x);
		int height = Math.abs(p1.y - p2.y);
		return new Rectangle(minX, minY, width, height);
	}
	
	public static String toCommaString(List<?> list) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) {
				if (i == list.size() - 1) {
					builder.append(" and ");
				} else {
					builder.append(", ");
				}
			}
			builder.append(list.get(i));
		}
		return builder.toString();
	}
}
