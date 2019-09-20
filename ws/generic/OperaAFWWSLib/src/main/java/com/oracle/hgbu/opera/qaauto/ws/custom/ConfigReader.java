package com.oracle.hgbu.opera.qaauto.ws.custom;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.oracle.hgbu.opera.qaauto.ws.common.ExcelUtil;
import com.oracle.hgbu.opera.qaauto.ws.common.PropUtil;
import com.relevantcodes.customextentreports.OperationTime;
import com.relevantcodes.customextentreports.StaticValues;

enum RESORT_INFO { RESORT, CHAIN, USERNAME, PASSWORD, INTERFACE }
enum EMAIL_PROP {SMTP_HOST, SMTP_PORT, EMAIL_FROM, EMAIL_TO, EMAIL_CC}
public class ConfigReader {
	PropUtil propUtil = new PropUtil();
	private static String runEnvironment = "", runOnLayer = "";
	private static String configFilePath = "", wsServiceFilePath = "", randomNames = "";
	private static HashMap<String, String> globalEnvMap = new HashMap<String, String>();
	public static HashMap<String, String> soapWSResourcePaths = new HashMap<String, String>();
	public static HashMap<String, String> soapSSDResourcePaths = new HashMap<String, String>();
	public static HashMap<String, String> soapModules = new HashMap<String, String>();
	public static HashMap<String, String> soapServices = new HashMap<String, String>();
	public static HashMap<String, String> soapOperations = new HashMap<String, String>();
	public static HashMap<String, String> soapActions = new HashMap<String, String>();
	public static HashMap<String,String> emailProperties = new HashMap<String,String>();
	public static List<LinkedHashMap<String, String>> resorts = new ArrayList<LinkedHashMap<String, String>>();

	private static Path currentRelativePath = Paths.get("");
	private static String actualWorkingDir = currentRelativePath.toAbsolutePath().toString();
	private static String workingDir = new File(actualWorkingDir).getParent() + "//"+ "resources//";
	private static String parentPathOfWorkingDir = new File(new File(workingDir).getParent()).getParent() + "//"
			+ "resources//";
	private String pathProp = parentPathOfWorkingDir + "/config/etc/internal/path.properties";
	private String headerProp = parentPathOfWorkingDir + "/config/etc/internal/header.properties";

	public ConfigReader(String environemnt) throws Exception {

		if(verifyIfFileExists(pathProp) == false) {
			pathProp = workingDir+"/config/etc/internal/path.properties";
		}
		if(verifyIfFileExists(headerProp) == false) {
			headerProp = workingDir+"/config/etc/internal/header.properties";
		}
		// Read the Paths for configuration file, serviceSetUp file and randomNames excel files
		configFilePath = parentPathOfWorkingDir + propUtil.getPropertyValue(pathProp, "configFile");
		if(verifyIfFileExists(configFilePath) == false) {
			configFilePath = workingDir + propUtil.getPropertyValue(pathProp, "configFile");
		}
		wsServiceFilePath = parentPathOfWorkingDir + propUtil.getPropertyValue(pathProp, "wsServiceFile");
		if(verifyIfFileExists(wsServiceFilePath) == false) {
			wsServiceFilePath = workingDir + propUtil.getPropertyValue(pathProp, "wsServiceFile");
		}
		randomNames = parentPathOfWorkingDir + propUtil.getPropertyValue(pathProp, "randomNamesFile");
		if(verifyIfFileExists(randomNames) == false) {
			randomNames = workingDir +  propUtil.getPropertyValue(pathProp, "randomNamesFile");
		}

		// Read the environment and the layer against which the tests are to be
		// executed
		/*runEnvironment = ExcelUtil.getDataFromGivenColumns(configFilePath, "Run", 0, 1).get("environment").trim();
		runOnLayer = ExcelUtil.getDataFromGivenColumns(configFilePath, "Run", 0, 2).get("environment").trim();*/

		runEnvironment = environemnt;

		// Check if the given Environment Key is valid
		if (!ExcelUtil.checkIfSheetExists(configFilePath, runEnvironment)) {
			System.out.println(
					"Sheet " + runEnvironment + "is not available in " + configFilePath + ". Cannot proceed further!");
			System.exit(0);
		} else {
			// Check if resorts are provided in the sheet mapped to the selected environment
			resorts = ExcelUtil.getAllRecords(configFilePath, runEnvironment);
			if (resorts.isEmpty()) {
				System.out.println("Resorts are not configured for " + runEnvironment + " in " + configFilePath
						+ ". Cannot proceed further!");
				System.exit(0);
			}
		}

		// Read all the necessary configurations into Maps
		globalEnvMap = ExcelUtil.getRowDataByKey(configFilePath, "Environments", runEnvironment);
		emailProperties = ExcelUtil.getRecordInSheetByRowIndex(configFilePath, "Common", 1);
		soapModules = ExcelUtil.getDataFromGivenColumns(wsServiceFilePath, "setUp", 1, 2);
		soapServices = ExcelUtil.getDataFromGivenColumns(wsServiceFilePath, "setUp", 1, 3);
		soapOperations = ExcelUtil.getDataFromGivenColumns(wsServiceFilePath, "setUp", 1, 4);
		soapWSResourcePaths = ExcelUtil.getDataFromGivenColumns(wsServiceFilePath, "setUp", 1, 5);
		soapSSDResourcePaths = ExcelUtil.getDataFromGivenColumns(wsServiceFilePath, "setUp", 1, 6);
		soapActions = ExcelUtil.getDataFromGivenColumns(wsServiceFilePath, "setUp", 1, 7);

		// set environment values for the report dash Board
		StaticValues.globalEnvMap = globalEnvMap;
		OperationTime.soapProjects = soapModules;
		OperationTime.soapServices = soapServices;
		OperationTime.soapOperations = soapOperations;

	}

	public ConfigReader() {
		
		if(verifyIfFileExists(pathProp) == false) {
			System.out.println(pathProp+ " is not found");
			pathProp = workingDir+"/config/etc/internal/path.properties";
		}
		
		
	}

	public String emailProperty(EMAIL_PROP fieldName) {
		String returnVal = emailProperties.get(fieldName.toString());
		if(fieldName == EMAIL_PROP.SMTP_PORT) {
			int tmpInt = (int) Float.parseFloat(returnVal);
			Integer.toString(tmpInt);
		}
		return returnVal;
	}

	public void setLayer(String layer) {
		System.out.println(layer+ "is set");
		runOnLayer = layer;
	}

	/*
	 * This method validates the resort entry that is parameterized at the
	 * suite/test level from the testNG XML i.e. it checks if the value passed
	 * in the XML is available in the entries mapped to the selected environment
	 */
	public boolean checkIfValidResortEntry(String resortEntry) {
		boolean validEntry = false;
		for (LinkedHashMap<String, String> entry : resorts) {
			String keyValue = entry.get("EntryKey");
			if (keyValue.equalsIgnoreCase(resortEntry)) {
				validEntry = true;
				break;
			}
		}
		return validEntry;
	}

	/*
	 * This method retrieves the Resort, Chain, User and Password information
	 * mapped to the given resortEntry of the selected environment
	 */
	private String getEnvironmentSpecificResortInfo(String key, RESORT_INFO field) {
		LinkedHashMap<String, String> tmpResorts = new LinkedHashMap<String, String>();
		String returnVal = "";
		try {
			int i = 0;
			for (LinkedHashMap<String, String> entry : resorts) {
				String keyValue = entry.get("EntryKey");
				if (keyValue.equalsIgnoreCase(key)) {
					break;
				}
				i = i + 1;
			}
			tmpResorts = resorts.get(i);
			if(field.equals(RESORT_INFO.RESORT)) {
				returnVal = tmpResorts.get("Resort");
			} else if (field.equals(RESORT_INFO.CHAIN)) {
				returnVal = tmpResorts.get("Chain");
			} if(field.equals(RESORT_INFO.USERNAME)) {
				returnVal = tmpResorts.get("User");
			} else if(field.equals(RESORT_INFO.PASSWORD)) {
				returnVal = tmpResorts.get("Password");
			} else if(field.equals(RESORT_INFO.INTERFACE)) {
				returnVal = tmpResorts.get("Interface");
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Resort entry is not found for " + key);
			return "";
		} catch (Exception e) {
			System.out.println("Something went wrong " + key);
			System.out.println(e.getMessage());
			return "";
		}

		return returnVal;
	}

	/*
	 * This method extracts the host from the given URL
	 */
	private String getHostFromUrl(String url) throws MalformedURLException {
		URL aURL = new URL(url);
		return aURL.getHost();
	}

	/*
	 * This method is to expose "pathProp" global variable i.e. location of the
	 * file where in the paths are maintained
	 */
	public String getPathConfig() {
		return pathProp;
	}

	/*
	 * This method is to expose "parentPathOfWorkingDir" global variable the
	 * parent path of the working directory
	 */
	public String getParentPath() {
		return parentPathOfWorkingDir;
	}

	public static String getReportsPath(){
		String reportPath = parentPathOfWorkingDir + "config/reports/"; 
		if(verifyIfFileExists(reportPath) == false) {
			reportPath = workingDir + "config/reports/"; 
		}
		return reportPath;
	}
	
	/*
	 * This method is to expose "workingDir"
	 */
	public String getWorkingDirectory() {
		return workingDir;
	}

	/*
	 * This method is to fetch the environment information such as DB details,
	 * End points and server details
	 */
	private String getEnvData(String key) {
		if (globalEnvMap.containsKey(key))
			return globalEnvMap.get(key);
		else
			return "";
	}

	public static boolean verifyIfFileExists(String filePath) {
		File tmpDir = new File(filePath);
		boolean exists = tmpDir.exists();
		return exists;
	}

	/*
	 * This method is to expose the selected environment key against which the
	 * tests are to be executed
	 */
	public String getEnvironmentKey() {
		return runEnvironment;
	}

	/*
	 * This method is to retrieve the security file path based on the layer that
	 * was selected WS/SSD
	 */
	public String getsecurityFilePath(String webLogicLayer) {
		String secDir = "";
		if(verifyIfFileExists(parentPathOfWorkingDir + propUtil.getPropertyValue(pathProp, "securityDir"))) {
			secDir = parentPathOfWorkingDir + propUtil.getPropertyValue(pathProp, "securityDir");
		}
		else {
			secDir = workingDir + propUtil.getPropertyValue(pathProp, "securityDir");
		}
		if (webLogicLayer.equalsIgnoreCase("WS"))
			secDir = secDir + "//ws//";
		else
			secDir = secDir + "//" + runOnLayer.toLowerCase() + "//";
		return secDir;
	}

	/*
	 * This method is to expose the root path of XML templates
	 */
	public String getschemaFilePath() {
		String schemaFilePath ="";
		if(verifyIfFileExists(parentPathOfWorkingDir + propUtil.getPropertyValue(pathProp, "schemaDir"))) {
			schemaFilePath= parentPathOfWorkingDir + propUtil.getPropertyValue(pathProp, "schemaDir");
		}
		else {
			schemaFilePath= workingDir + propUtil.getPropertyValue(pathProp, "schemaDir");
		}
		return schemaFilePath;
	}

	/*
	 * This method is to expose the root path of databanks
	 */
	public String getdataFilePath() {
		String dataFilePath = "";
		if(verifyIfFileExists(parentPathOfWorkingDir + propUtil.getPropertyValue(pathProp, "dataDir"))) {
			dataFilePath = parentPathOfWorkingDir + propUtil.getPropertyValue(pathProp, "dataDir");
		}
		else {
			dataFilePath = workingDir + propUtil.getPropertyValue(pathProp, "dataDir");
		}
		return dataFilePath;
	}

	/*
	 * This method is to expose the root path of results
	 */
	public String getResultsPath() {
		String resultsDir ="";
		if(verifyIfFileExists(parentPathOfWorkingDir + propUtil.getPropertyValue(pathProp, "resultsDir"))) {
			resultsDir= parentPathOfWorkingDir + propUtil.getPropertyValue(pathProp, "resultsDir");
		}
		else {
			resultsDir= workingDir + propUtil.getPropertyValue(pathProp, "resultsDir");
		}
		return resultsDir;
	}

	/*
	 * This method is to expose the root path of the object repository
	 */
	public String getPropertiesFilePath() {
		String propertiesDir ="";
		if(verifyIfFileExists(parentPathOfWorkingDir + propUtil.getPropertyValue(pathProp, "propertiesDir"))) {
			propertiesDir = parentPathOfWorkingDir + propUtil.getPropertyValue(pathProp, "propertiesDir");
		}
		else {
			propertiesDir = workingDir + propUtil.getPropertyValue(pathProp, "propertiesDir");
		}
		return propertiesDir;
	}

	/*
	 * This method is to expose Database Details such as Host, Port, Service and
	 * Credentials configured against the environment
	 */
	public String getDBData(String dbAttribute) {
		String returnValue = "";
		switch (dbAttribute) {
		case "DBHost":
			returnValue = getEnvData("DBHost");
			break;
		case "DBPort":
			returnValue = getEnvData("DBPort");
			break;
		case "DBService":
			returnValue = getEnvData("DBService");
			break;
		case "DBUser":
			returnValue = getEnvData("DBUser");
			break;
		case "DBPassword":
			returnValue = getEnvData("DBPassword");
			break;
		}
		return returnValue;
	}

	/*
	 * This method is to expose the layer (SSD/WS) against which the tests are
	 * to be executed
	 */
	public String getLayer() {
		return runOnLayer;
	}

	/*
	 * This method is to expose the weblogic endpoint
	 */
	public String getWeblogicHost() {
		return getEnvData("WeblogicBaseUrl");
	}

	/*
	 * This method is to expose the SSD endpoint
	 */
	public String getSSDHost() {
		return getEnvData("SSDBaseUrl");
	}

	/*
	 * This method is to fetch the resource path of the operation based on the
	 * layer (SSD/WS) selected
	 */
	public String getResourcePath(String operationKeyword) {
		String resourcePath = "";
		if (runOnLayer.equalsIgnoreCase("WS"))
			resourcePath = soapWSResourcePaths.get(operationKeyword);
		else if (runOnLayer.equalsIgnoreCase("SSD"))
			resourcePath = soapSSDResourcePaths.get(operationKeyword);
		return resourcePath;
	}

	/*
	 * This method is to return the Module mapped to the Operation Keyword
	 */
	public String getModule(String keyword) {
		return soapModules.get(keyword);
	}

	/*
	 * This method is to return the Operation mapped to the Operation Keyword
	 */
	public String getOperation(String keyword) {
		return soapOperations.get(keyword);
	}

	/*
	 * This method is to return the Service mapped to the Operation Keyword
	 */
	public String getService(String keyword) {
		return soapServices.get(keyword);
	}

	/*
	 * This method is to return the SOAP Action mapped to the Operation Keyword
	 */
	public String getSOAPAction(String keyword) {
		return soapActions.get(keyword);
	}

	/*
	 * This method is to return the value of the given security variable used in
	 * the SOAP header (when SOAP Header values are parameterized)
	 */
	public String getSecurityVariable(String keyword) {
		return propUtil.getProperties(headerProp).get(keyword);
	}

	/*
	 * This method is to return the key-value pairs of the security variables
	 * and values used in the SOAP header (when SOAP Header values are
	 * parameterized)
	 */
	public HashMap<String, String> getSecurityVariables() {
		return propUtil.getProperties(headerProp);
	}

	/*
	 * This method is to return random first/last name from the excel sheet
	 */
	public HashMap<String, String> getRandomName(int randomNumber) {
		return ExcelUtil.getRecordInSheetByRowIndex(randomNames, "Names", randomNumber);
	}

	/*
	 * This method is to retrieve the Weblogic Host where in the logs are
	 * located for the selected environment
	 */
	public String getWSLogsHost() {
		String host = "";
		try {
			host = getHostFromUrl(getWeblogicHost());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return host;
	}

	/*
	 * This method is to retrieve the User configured for the weblogic server
	 * where in the logs are located for the selected environment
	 */
	public String getWSLogsHostUsername() {
		return getEnvData("WeblogicUser");
	}

	/*
	 * This method is to retrieve the Password configured for the weblogic
	 * server where in the logs are located for the selected environment
	 */
	public String getWSLogsHostPassword() {
		return getEnvData("WeblogicPassword");
	}

	/*
	 * This method is to retrieve the no. of lines to be read from the log file
	 * located in the weblogic server of the selected environment
	 */
	public int getWSLogsHostLineCount() {
		String countStr = getEnvData("WeblogicLogLimit");
		int countInt = (int) Float.parseFloat(countStr);
		System.out.println(countInt);
		return countInt;
	}

	/*
	 * This method is to retrieve the path of the log file located in the
	 * weblogic server of the selected environment
	 */
	public String getWSLogsHostFilePath() {
		return getEnvData("WeblogicLogPath");
	}

	/*
	 * This method is to retrieve the the Flag to indicate whether to capture
	 * the log from Weblogic server or not during execution
	 */
	public String getWSLogFlag() {
		return getEnvData("WeblogicLog");
	}

	/*
	 * This method is to retrieve the SSD Host where in the logs are located for
	 * the selected environment
	 */
	public String getSSDLogsHost() {
		String host = "";
		try {
			host = getHostFromUrl(getEnvData("SSDBaseUrl"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return host;
	}

	/*
	 * This method is to retrieve the User configured for the SSD server where
	 * in the logs are located for the selected environment
	 */
	public String getSSDLogsHostUsername() {
		return getEnvData("SSDUser");
	}

	/*
	 * This method is to retrieve the Password configured for the SSD server
	 * where in the logs are located for the selected environment
	 */
	public String getSSDLogsHostPassword() {
		return getEnvData("SSDPassword");
	}

	/*
	 * This method is to retrieve the no. of lines to be read from the log file
	 * located in the SSD server of the selected environment
	 */
	public int getSSDLogsHostLineCount() {
		String countStr = getEnvData("SSDLogLimit");
		int countInt = (int) Float.parseFloat(countStr);
		System.out.println(countInt);
		return countInt;
	}

	/*
	 * This method is to retrieve the path of the log file located in the SSD
	 * server of the selected environment
	 */
	public String getSSDLogsHostFilePath() {
		return getEnvData("SSDLogPath");
	}

	/*
	 * This method is to retrieve the the Flag to indicate whether to capture
	 * the log from SSD Server or not during execution
	 */
	public String getSSDLogFlag() {
		return getEnvData("SSDLog");
	}

	/*
	 * This method return the resort mapped to the entry passed in the testNG
	 * XML
	 */
	public String getResort(String entryNo) {
		return getEnvironmentSpecificResortInfo(entryNo, RESORT_INFO.RESORT);
	}

	/*
	 * This method return the Chain mapped to the entry passed in the testNG XML
	 */
	public String getChain(String EntryKey) {
		return getEnvironmentSpecificResortInfo(EntryKey, RESORT_INFO.CHAIN);
	}

	/*
	 * This method return the User mapped to the entry passed in the testNG XML
	 */
	public String getUser(String EntryKey) {
		return getEnvironmentSpecificResortInfo(EntryKey, RESORT_INFO.USERNAME);
	}

	/*
	 * This method return the Password mapped to the entry passed in the testNG
	 * XML
	 */
	public String getPassword(String EntryKey) {
		return getEnvironmentSpecificResortInfo(EntryKey, RESORT_INFO.PASSWORD);
	}

	/*
	 * This method return the Interface mapped to the entry passed in the testNG
	 * XML
	 */
	public String getInterface(String EntryKey) {
		return getEnvironmentSpecificResortInfo(EntryKey, RESORT_INFO.INTERFACE);
	}
}
