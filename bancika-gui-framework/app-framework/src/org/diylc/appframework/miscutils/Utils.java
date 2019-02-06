package org.diylc.appframework.miscutils;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.log4j.Logger;

public class Utils {

  private static final Logger LOG = Logger.getLogger(Utils.class);

  static final String[] browsers = {"google-chrome", "firefox", "opera", "epiphany", "konqueror", "conkeror", "midori",
      "kazehakase", "mozilla"};
  static final String errMsg = "Error attempting to launch web browser";

  public static void openURL(String url) throws Exception {
    try { // attempt to use Desktop library from JDK 1.6+
      Class<?> d = Class.forName("java.awt.Desktop");
      d.getDeclaredMethod("browse", new Class[] {java.net.URI.class}).invoke(
          d.getDeclaredMethod("getDesktop").invoke(null), new Object[] {java.net.URI.create(url)});
      // above code mimicks: java.awt.Desktop.getDesktop().browse()
    } catch (Exception ignore) { // library not available or failed
      String osName = System.getProperty("os.name");
      if (osName.startsWith("Mac OS")) {
        Class.forName("com.apple.eio.FileManager").getDeclaredMethod("openURL", new Class[] {String.class})
            .invoke(null, new Object[] {url});
      } else if (osName.startsWith("Windows"))
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
      else { // assume Unix or Linux
        String browser = null;
        for (String b : browsers)
          if (browser == null && Runtime.getRuntime().exec(new String[] {"which", b}).getInputStream().read() != -1)
            Runtime.getRuntime().exec(new String[] {browser = b, url});
        if (browser == null)
          throw new Exception(Arrays.toString(browsers));
      }
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

  public static boolean isWindows() {
    String os = System.getProperty("os.name").toLowerCase();
    // windows
    return (os.indexOf("win") >= 0);
  }

  public static boolean isMac() {
    String os = System.getProperty("os.name").toLowerCase();
    // Mac
    return (os.indexOf("mac") >= 0);
  }

  public static boolean isUnix() {
    String os = System.getProperty("os.name").toLowerCase();
    // linux or unix
    return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
  }

  public static boolean isSolaris() {
    String os = System.getProperty("os.name").toLowerCase();
    // Solaris
    return (os.indexOf("sunos") >= 0);
  }

  public static Set<Class<?>> getClasses(String packageName) throws Exception {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    return getClasses(loader, packageName);
  }

  public static Set<Class<?>> getClasses(ClassLoader loader, String packageName) throws IOException,
      ClassNotFoundException {
    Set<Class<?>> classes = new HashSet<Class<?>>();
    String path = packageName.replace('.', '/');
    Enumeration<URL> resources = loader.getResources(path);
    if (resources != null) {
      while (resources.hasMoreElements()) {
        String filePath = URLDecoder.decode(resources.nextElement().getFile(), "UTF-8");

        if (filePath != null) {
          if ((filePath.indexOf("!") > 0) && (filePath.indexOf(".jar") > 0)) {
            String jarPath = filePath.substring(0, filePath.lastIndexOf("!")).substring(filePath.indexOf(":") + 1);

            classes.addAll(getFromJARFile(jarPath, path));
          } else {
            classes.addAll(getFromDirectory(new File(filePath), packageName));
          }
        }
      }
    }
    return classes;
  }

  public static Set<Class<?>> getFromDirectory(File directory, String packageName) throws ClassNotFoundException {
    Set<Class<?>> classes = new HashSet<Class<?>>();
    if (directory.exists()) {
      LOG.debug("Scanning directory: " + directory.getAbsolutePath());
      for (String file : directory.list()) {
        if (file.endsWith(".class")) {
          String name = packageName + '.' + stripFilenameExtension(file);
          Class<?> clazz = Class.forName(name);
          classes.add(clazz);
        }
      }
    }
    return classes;
  }

  private static String stripFilenameExtension(String file) {
    int i = file.length() - 1;
    while (file.charAt(i) != '.' && i > 0)
      i--;
    return file.substring(0, i);
  }

  public static Set<Class<?>> getFromJARFile(String jar, String packageName) throws FileNotFoundException, IOException,
      ClassNotFoundException {
    Set<Class<?>> classes = new HashSet<Class<?>>();
    LOG.debug("Scanning jar: " + jar);
    JarInputStream jarFile = new JarInputStream(new FileInputStream(jar));
    try {
      JarEntry jarEntry;
      do {
        jarEntry = jarFile.getNextJarEntry();
        if (jarEntry != null) {
          String className = jarEntry.getName();
          if (className.endsWith(".class")) {
            className = stripFilenameExtension(className);
            if (className.startsWith(packageName))
              classes.add(Class.forName(className.replace('/', '.')));
          }
        }
      } while (jarEntry != null);
    } finally {
      jarFile.close();
    }
    return classes;
  }

  /**
   * Creates a rectangle which opposite corners are lying in the specified points.
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

  public static String getUserDataDirectory(String appName) {
    return System.getProperty("user.home") + File.separator + "." + appName + File.separator;
  }
}
