package org.diylc.appframework.miscutils;

import java.io.File;
import java.io.FileInputStream;
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
			try {
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
			} finally {
			  jarFile.close();
			}
		} catch (Exception e) {
			LOG.error("Error extracting class names from the jar.", e);
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
			classes.addAll(scanJar(jar, baseInterface));
		}
		return classes;
	}

	public List<Class<?>> scanJar(File jar, Class<?> baseInterface) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		List<String> classNames = extractClassNames(jar);
		if (!classNames.isEmpty()) {
			String filePath = jar.getAbsolutePath();
			try {
				ClassLoaderUtil.addFile(filePath);
				filePath = "jar:file://" + filePath + "!/";
				URL url = new File(filePath).toURI().toURL();
				URLClassLoader clazzLoader = new URLClassLoader(
						new URL[] { url });
				for (String className : classNames) {
					Class<?> clazz;
					try {
						clazz = clazzLoader.loadClass(className);
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
			} catch (Exception e) {
				LOG.warn("Could not add JAR to the classpath.", e);
			}
		}
		return classes;
	}
}
