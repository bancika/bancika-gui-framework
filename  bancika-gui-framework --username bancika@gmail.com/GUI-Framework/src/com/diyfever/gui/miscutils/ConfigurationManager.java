package com.diyfever.gui.miscutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ConfigurationManager {

	private static final Logger LOG = Logger.getLogger(ConfigurationManager.class);

	private static ConfigurationManager instance;

	private XStream xStream;
	private Map<String, Object> configuration;

	public static ConfigurationManager getInstance() {
		if (instance == null) {
			instance = new ConfigurationManager();
		}
		return instance;
	}

	private ConfigurationManager() {
		xStream = new XStream(new DomDriver());
		initializeConfiguration();
	}

	@SuppressWarnings("unchecked")
	private void initializeConfiguration() {
		LOG.info("Initializing configuration");
		File configFile = new File("config.xml");
		try {
			FileInputStream in = new FileInputStream(configFile);
			configuration = (Map<String, Object>) xStream.fromXML(in);
			in.close();
		} catch (Exception e) {
			configuration = new HashMap<String, Object>();
		}
	}

	public void saveConfigration() {
		LOG.info("Saving configuration");
		File configFile = new File("config.xml");
		try {
			FileOutputStream out = new FileOutputStream(configFile);
			xStream.toXML(configuration, out);
			out.close();
		} catch (Exception e) {
			LOG.error("Could not save configuration: " + e.getMessage());
		}
	}

	public Object getConfigurationItem(String key) {
		return configuration.get(key);
	}

	public void setConfigurationItem(String key, Object value) {
		configuration.put(key, value);
		saveConfigration();
	}
}
