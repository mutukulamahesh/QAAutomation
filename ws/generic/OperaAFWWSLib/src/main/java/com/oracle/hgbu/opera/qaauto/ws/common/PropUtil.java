package com.oracle.hgbu.opera.qaauto.ws.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient;
import com.relevantcodes.customextentreports.LogStatus;

public class PropUtil {

	/*
	 * This method fetches the properties from a given file
	 */
	public HashMap<String, String> getProperties(String filepath) {
		HashMap<String, String> propertiesMap = new HashMap<String, String>();
		filepath = filepath.replace("\\", "/");
		try {
			File file = new File(filepath);
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();

			Enumeration<Object> enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = properties.getProperty(key);
				propertiesMap.put(key, value);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return propertiesMap;
	}

	/*
	 * This method fetches the property value that is assigned to a given
	 * property name
	 */
	public String getPropertyValue(String filepath, String propertyName) {
		String propValue = "";
		HashMap<String, String> propertiesMap = new HashMap<String, String>();
		propertiesMap = getProperties(filepath);
		if (propertiesMap.containsKey(propertyName))
			propValue = propertiesMap.get(propertyName);
		else
			WSClient.writeToReport(LogStatus.ERROR, propertyName + " - XPath Variable not found");
		return propValue;
	}
}