package com.diyfever.gui.miscutils;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.log4j.Logger;

/**
 * Utility class that provides JAR manipulation methods.
 * 
 * @author Branislav Stojkovic
 */
public class JarScanner {

	private static final Logger LOG = Logger.getLogger(JarScanner.class);

	private static JarScanner instance;

	private JarFileLoader jarFileLoader = new JarFileLoader(new URL[] {});

	public static JarScanner getInstance() {
		if (instance == null) {
			instance = new JarScanner();
		}
		return instance;
	}

	private JarScanner() {
	}

	/**
	 * Scans jar file for classes and returns a list of class names.
	 * 
	 * @param jar
	 * @return
	 */
	public List<String> extractClassNames(File jar) {
		ArrayList<String> classes = new ArrayList<String>();

		LOG.debug("Scanning " + jar.getName());
		try {
			JarInputStream jarFile = new JarInputStream(
					new FileInputStream(jar));
			JarEntry jarEntry;

			while (true) {
				jarEntry = jarFile.getNextJarEntry();
				if (jarEntry == null) {
					break;
				}
				if (jarEntry.getName().endsWith(".class")) {
					String className = jarEntry.getName()
							.replaceAll("/", "\\.");
					className = className.substring(0, className
							.lastIndexOf('.'));
					if (!className.contains("$")) {
						LOG.trace("Found " + className);
						classes.add(className);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classes;
	}

	/**
	 * Scans folder for files with .JAR extension.
	 * 
	 * @param folder
	 *            a folder to scan
	 * @param recursive
	 *            if true, scanner will go in depth and scan all sub-folders.
	 * @return
	 */
	public List<File> getJarFiles(File folder, boolean recursive) {
		File[] listOfFiles = folder.listFiles();
		List<File> jarFiles = new ArrayList<File>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				if (listOfFiles[i].getName().toLowerCase().endsWith(".jar")) {
					LOG.debug("Found JAR file: " + listOfFiles[i].getName());
					jarFiles.add(listOfFiles[i]);
				}
			} else if (listOfFiles[i].isDirectory() && recursive) {
				jarFiles.addAll(getJarFiles(listOfFiles[i], recursive));
			}
		}
		return jarFiles;
	}

	/**
	 * Loads all JAR files from the specifier folder and tries to find and load
	 * classes from those JARs that implement specified interface.
	 * 
	 * @param folderName
	 * @param packageName
	 * @param baseInterface
	 * @return
	 */
	public List<Class<?>> scanFolder(String folderName, Class<?> baseInterface) {
		List<File> jars = getJarFiles(new File(folderName), true);
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (File jar : jars) {
			List<String> classNames = extractClassNames(jar);
			try {
				jarFileLoader.addFile(jar.getAbsolutePath());
				if (!classNames.isEmpty()) {

					for (String className : classNames) {
						Class<?> clazz;
						try {
							clazz = Class.forName(className);
							if (baseInterface.isAssignableFrom(clazz)
									&& !clazz.isInterface()) {
								LOG.debug("Loaded class: " + className);
								classes.add(clazz);
							}
						} catch (ClassNotFoundException e) {
							LOG.warn("Class not found: " + className);
						} catch (Throwable t) {
							LOG.warn("Could not load: " + className);
						}
					}
				}
			} catch (MalformedURLException e) {
				LOG.error("Could not load JAR. " + e.getMessage());
			}
		}
		return classes;
	}

	class JarFileLoader extends URLClassLoader {

		public JarFileLoader(URL[] urls) {
			super(urls);
		}

		public void addFile(String path) throws MalformedURLException {
			LOG.info("Trying to load: " + path);
			String urlPath = "jar:file://" + path + "!/";
			addURL(new URL(urlPath));
		}
	}
}
