package com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.WSLib;
import com.relevantcodes.customextentreports.LogStatus;

public class PropertyConfig extends WSLib {

	public static List<HashMap<String, String>> propertyConfigData = new ArrayList<HashMap<String, String>>();
	public static List<HashMap<String, String>> htngConfigData = new ArrayList<HashMap<String,String>>();
	public static List<HashMap<String, String>> owsConfigData = new ArrayList<HashMap<String,String>>();
	public static List<HashMap<String, String>> appParameters = new ArrayList<HashMap<String, String>>();
	public static HashMap<String,String> propertyConfigResultsData = new HashMap<String, String>();
	public static HashMap<String,String> propertyConfigResultsValue = new HashMap<String, String>();

	PropertyConfig(){
		/**
		 * @author heegupta, rnagasun
		 * Description: Method to load configuration data for a property from excel sheet.
		 *
		 * @throws Exception
		 */

		String path = configReader.getPropertySetUpDataPath();
		try {
			propertyConfigData = dsReader.getPropertyConfigData(path, "OperaConfig");
			htngConfigData = dsReader.getPropertyConfigData(path, "HTNG");
			owsConfigData=dsReader.getPropertyConfigData(path, "OWS");
			appParameters = dsReader.getPropertyConfigData(path, "AppParam");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author heegupta,rnagasun
	 * Description : Method to get the record for a variable name from the list of hashmap
	 *
	 * @param key
	 * @param dataset
	 */

	public String getDataSetForCode(String key, String dataset){
		Iterator<HashMap<String, String>> it = propertyConfigData.iterator();
		while(it.hasNext()) {
			HashMap<String,String> temp = it.next();
			if(temp.get("VARIABLE_NAME").equals(key)) {
				if(key.equalsIgnoreCase("Cashiers")) {
					int tmpVar = Math.round(Float.valueOf(temp.get(dataset)));
					return new Integer(tmpVar).toString();
				}
				else {
					return temp.get(dataset);
				}
			}
		}
		return null;
	}

	/**
	 * @author heegupta
	 * Description : Method to get the length (number of codes) of a variable from OperaConfig
	 *
	 * @param key
	 * @param dataset
	 */

	public int getLengthForCode(String key){
		Iterator<HashMap<String, String>> it = propertyConfigData.iterator();
		while(it.hasNext()) {
			HashMap<String,String> temp = it.next();
			if(temp.get("VARIABLE_NAME").equals(key))
				return temp.size();
		}
		return 0;
	}

	/**
	 * @author heegupta
	 * Description : Method to get the length (number of codes) of a variable from OWS
	 *
	 * @param key
	 * @param dataset
	 */

	public int getLengthForCodeOWS(String key){
		Iterator<HashMap<String, String>> it = owsConfigData.iterator();
		while(it.hasNext()) {
			HashMap<String,String> temp = it.next();
			if(temp.get("VARIABLE_NAME").equals(key))
				return temp.size();
		}
		return 0;
	}


	/**
	 * @author rnagasun
	 * Description : Method to get the record for a variable name from the list of ows config hashmap
	 *
	 * @param key
	 * @param dataset
	 */

	public String getChannelCodeForDataSet(String key, String dataset){
		Iterator<HashMap<String, String>> it = owsConfigData.iterator();
		System.out.println(owsConfigData);
		while(it.hasNext()) {
			HashMap<String,String> temp = it.next();
			if(temp.get("VARIABLE_NAME").equals(key))
				return temp.get(dataset);
		}
		return null;
	}

	/**
	 * @author heegupta
	 * Description: Method to read data from config excel sheet to a hashmap
	 * @throws Exception
	 */

	public synchronized boolean getConfigResult(String[] key) throws Exception {
		if(propertyConfigResultsValue.size() == 0) {
			String folder_path = configReader.getPreRequisitePath();
			String env = configReader.getEnvironmentKey();
			String property = configReader.getResort();
			String file = folder_path + "\\ConfigResults_" + env + "_" + property + ".xlsx";
			propertyConfigResultsValue = dsReader.getConfigResults(file, "ConfigResults");
		}
		boolean flag = true;
		String val;
		for (int i = 0; i < key.length ; i++ ){
			val = propertyConfigResultsValue.get(key[i]);
			if(val.equals("N")){
				WSClient.writeToReport(LogStatus.WARNING, "Pre Requisite Falied to Setup" + key[i]);
				flag = false;
			}
		}

		return flag;

	}

	/** @author heegupta Description: Method to store the received value in a
	 *         global hashmap (propertyConfigData)
	 * @param key
	 * @param value
	 */
	public void propertyResultsSetData(String key, String value) {
		propertyConfigResultsData.put(key, value);
		System.out.println(key+ " : "+  value);
	}

	/**
	 * @author heegupta
	 * Description: Method to write property config results to an excel sheet.
	 */

	public synchronized void writePropertyResults() {
		String folder_path = configReader.getPreRequisitePath();
		String env = configReader.getEnvironmentKey();
		String property = configReader.getResort();
		String workbook_name = "ConfigResults_" + env + "_" + property;
		dsReader.writePropertyResults(folder_path, workbook_name, "ConfigResults", propertyConfigResultsData);
	}



	/**
	 * @author heegupta
	 * Description: Method to return hashmap having HTNG config values
	 * @return size of hashmap
	 */

	public List<HashMap<String, String>> getHtngHashMap() {
		return htngConfigData;
	}

	/**
	 * @author heegupta
	 * Description: Method to return hashmap having Application Parameters values
	 * @return size of hashmap
	 */

	public List<HashMap<String, String>> getAppParamHashMap() {
		return appParameters;
	}

	/**
	 * @author heegupta
	 * Description:  Method to get the row index of a key
	 * @return row index
	 */

	public int getRowIndex(String sheet, String key) {
		String path = configReader.getPropertySetUpDataPath();
		int index = dsReader.getRowIndex(path,sheet , key);
		return index;
	}

	/**
	 * @author heegupta
	 * Description:  Method to get the comment of a cell
	 * @return row index
	 */

	public String getCellComment(String sheet, int rowIndex, int colIndex) {
		String path = configReader.getPropertySetUpDataPath();
		String comment = dsReader.getCellComment(path, sheet, rowIndex, colIndex);
		return comment;
	}

	/**
	 * @author heegupta
	 * Description : Method to split the dependencies of a key
	 */

	public HashMap<String, String> getDependencies(String comment){
		String[] temp = comment.split(",");
		HashMap<String,String> dependency = new HashMap<String,String>();
		for(int i = 0; i <temp.length;i++) {
			String[] t = temp[i].split(":");
			dependency.put(t[0].trim(), t[1].trim());
		}
		return dependency;
	}



}
