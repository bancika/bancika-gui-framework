package org.diylc.appframework.miscutils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.log4j.Logger;

public class ClassLoaderUtil {
	// Log object
	private static Logger LOG = Logger.getLogger(ClassLoaderUtil.class);

	// Parameters
	private static final Class<?>[] parameters = new Class<?>[] { URL.class };

	/**
	 * Add file to CLASSPATH
	 * 
	 * @param s
	 *            File name
	 * @throws IOException
	 *             IOException
	 */
	public static void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}

	/**
	 * Add file to CLASSPATH
	 * 
	 * @param f
	 *            File object
	 * @throws IOException
	 *             IOException
	 */
	public static void addFile(File f) throws IOException {
		LOG.info("Adding file to the classpath: " + f.getAbsolutePath());
		addURL(f.toURI().toURL());
	}

	/**
	 * Add URL to CLASSPATH
	 * 
	 * @param u
	 *            URL
	 * @throws IOException
	 *             IOException
	 */
	public static void addURL(URL u) throws IOException {

		URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		URL urls[] = sysLoader.getURLs();
		for (int i = 0; i < urls.length; i++) {
			if (urls[i].toString().equalsIgnoreCase(u.toString())) {
				LOG.debug("URL " + u + " is already in the classpath.");
				return;
			}
		}
		Class<URLClassLoader> sysClass = URLClassLoader.class;
		try {
			Method method = sysClass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysLoader, new Object[] { u });
		} catch (Throwable t) {
			LOG.error("Error, could not add URL to system classloader", t);
			throw new IOException("Error, could not add URL to system classloader");
		}
	}

}
