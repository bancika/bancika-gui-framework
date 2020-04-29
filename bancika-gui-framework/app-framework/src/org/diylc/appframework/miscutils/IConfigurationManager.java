package org.diylc.appframework.miscutils;

public interface IConfigurationManager {
  
  void addConfigListener(String key, IConfigListener listener);

  boolean readBoolean(String key, boolean defaultValue);

  String readString(String key, String defaultValue);

  int readInt(String key, int defaultValue);

  float readFloat(String key, float defaultValue);

  double readDouble(String key, double defaultValue);

  Object readObject(String key, Object defaultValue);

  void writeValue(String key, Object value);
}
