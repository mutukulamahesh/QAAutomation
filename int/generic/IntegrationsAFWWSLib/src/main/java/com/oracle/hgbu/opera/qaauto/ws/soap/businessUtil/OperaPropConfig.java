package com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil;

import java.util.HashMap;
import java.util.List;

import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.relevantcodes.customextentreports.LogStatus;

public class OperaPropConfig {

	static PropertyConfig propConfig = new PropertyConfig();


	public static String getDataSetForCode(String key, String dataset){
		return propConfig.getDataSetForCode(key,dataset);
	}

	public static int getLengthForCode(String key){
		return propConfig.getLengthForCode(key);
	}

	public static int getLengthForCodeOWS(String key){
		return propConfig.getLengthForCodeOWS(key);
	}

	public static String getChannelCodeForDataSet(String key, String dataset){
		return propConfig.getChannelCodeForDataSet(key,dataset);
	}


	public static void setPropertyConfigResults(String key, String value) {
		propConfig.propertyResultsSetData(key,value);
	}


	public static void writeResults() {
		propConfig.writePropertyResults();
	}


	public static boolean getPropertyConfigResults(String[] key) throws Exception {
		boolean val = propConfig.getConfigResult(key);
		if(val)
			return true;
		else {
			WSClient.writeToReport(LogStatus.WARNING, key + " :- Pre-requisite failed");
			return false;
		}
	}


	public static List<HashMap<String, String>> getHtngHashMap() {
		return propConfig.getHtngHashMap();
	}

	public static List<HashMap<String, String>> getAppParamHashMap() {
		return propConfig.getAppParamHashMap();
	}

	public static int getRowIndex(String sheetName, String key) {
		return propConfig.getRowIndex(sheetName, key);
	}

	public static String getCellComment(String sheetName, int rowIndex, int colIndex) {
		return propConfig.getCellComment(sheetName, rowIndex, colIndex);
	}

	public static HashMap<String,String> getDependency(String comment){
		return propConfig.getDependencies(comment);
	}
}