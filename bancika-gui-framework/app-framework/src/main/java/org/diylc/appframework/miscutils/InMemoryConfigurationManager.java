package org.diylc.appframework.miscutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility that keeps configuration in memory without persistance. Each configuration item should
 * have a unique name. To write to the configuration use {@link #writeValue(String, Object)}. Use
 * <code>readXYZ</code> methods where XYZ stands for specific data types.
 * 
 * @author Branislav Stojkovic
 */
public class InMemoryConfigurationManager implements IConfigurationManager<Void> {

  private static InMemoryConfigurationManager instance;

  private Map<String, Object> configuration;
  private Map<String, List<IConfigListener>> listeners;

  public static InMemoryConfigurationManager getInstance() {
    if (instance == null) {
      instance = new InMemoryConfigurationManager();
    }
    return instance;
  }

  public InMemoryConfigurationManager() {
    this.listeners = new HashMap<String, List<IConfigListener>>();
    this.configuration = new HashMap<String, Object>();
  }

  public InMemoryConfigurationManager(Map<String, Object> configuration) {
    this.listeners = new HashMap<String, List<IConfigListener>>();
    this.configuration = configuration;
  }

  public void addConfigListener(String key, IConfigListener listener) {
    List<IConfigListener> listenerList;
    if (listeners.containsKey(key)) {
      listenerList = listeners.get(key);
    } else {
      listenerList = new ArrayList<IConfigListener>();
      listeners.put(key, listenerList);
    }
    listenerList.add(listener);
  }

  public boolean readBoolean(String key, boolean defaultValue) {
    if (configuration.containsKey(key)) {
      return (Boolean) configuration.get(key);
    } else {
      return defaultValue;
    }
  }

  public String readString(String key, String defaultValue) {
    if (configuration.containsKey(key)) {
      return (String) configuration.get(key);
    } else {
      return defaultValue;
    }
  }

  public int readInt(String key, int defaultValue) {
    if (configuration.containsKey(key)) {
      return (Integer) configuration.get(key);
    } else {
      return defaultValue;
    }
  }

  public float readFloat(String key, float defaultValue) {
    if (configuration.containsKey(key)) {
      return (Float) configuration.get(key);
    } else {
      return defaultValue;
    }
  }

  public double readDouble(String key, double defaultValue) {
    if (configuration.containsKey(key)) {
      return (Double) configuration.get(key);
    } else {
      return defaultValue;
    }
  }

  public Object readObject(String key, Object defaultValue) {
    if (configuration.containsKey(key)) {
      return configuration.get(key);
    } else {
      return defaultValue;
    }
  }

  public void writeValue(String key, Object value) {
    configuration.put(key, value);
    if (listeners.containsKey(key)) {
      for (IConfigListener listener : listeners.get(key)) {
        listener.valueChanged(key, value);
      }
    }
  }

  @Override
  public Void getSerializer() {
    return null;
  }

  @Override
  public void initialize(String appName) {
    // do nothing
  }
}
