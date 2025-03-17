package org.diylc.appframework.miscutils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;

import ca.cgjennings.jvm.JarLoader;

public class ClassLoaderUtil {
	// Log object
	private static Logger LOG = Logger.getLogger(ClassLoaderUtil.class);

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
		JarLoader.addToClassPath(f);
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
	  LOG.info("Adding file to the classpath: " + u);
	  JarLoader.addToClassPath(new File(u.getFile()));
	}

}
