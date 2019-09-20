package com.oracle.hgbu.opera.qaauto.ws.soap.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.util.SystemOutLogger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.oracle.hgbu.opera.qaauto.ws.common.utils.DatabaseUtil;
import com.oracle.hgbu.opera.qaauto.ws.common.utils.Logs;
import com.oracle.hgbu.opera.qaauto.ws.common.utils.PropUtil;
import com.oracle.hgbu.opera.qaauto.ws.common.utils.ReportFormatter;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.BusinessLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.BusinessLib.Product;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.relevantcodes.customextentreports.CustomHTMLMethods;
import com.relevantcodes.customextentreports.ExtentReports;
import com.relevantcodes.customextentreports.ExtentTest;
import com.relevantcodes.customextentreports.LogStatus;
import com.relevantcodes.customextentreports.NetworkMode;
import com.relevantcodes.customextentreports.OperationTime;

public class WSLib {

	private static boolean wsLogFlag = false;
	private static boolean sbLogFlag = false;

	private static List<String> wsLogslist;
	private static List<String> sbLogslist;

	private static boolean serverLogsFlag = false;
	public static ExtentReports report = null;
	public static ExtentTest logger = null;
	private static DataGen inpObj;
	protected static Connection dbConnection;
	protected static PropUtil propUtil;
	public static DSReader dsReader;

	private static String iteration = null;
	private static String testCaseName = "";
	private static String timeStamp = "";
	public static String dataXMLPath = "";
	public static String OUTPUT_FOLDER = "";
	public static String schema = "";
	public static String tcFinalResults = "";

	public static HashMap<String, String> lastRunData = new HashMap<String, String>();
	public static List<HashMap<String, String>> propertyConfigData = new ArrayList<HashMap<String, String>>();
	public static List<HashMap<String, String>> htngConfigData = new ArrayList<HashMap<String, String>>();
	public static HashMap<String, String> propertyConfigResultsData = new HashMap<String, String>();
	public static HashMap<String, String> propertyConfigResultsValue = new HashMap<String, String>();
	public static HashMap<String, String> varData = new HashMap<String, String>();
	public static HashMap<String, String> securityData = new HashMap<String, String>();
	private static HashMap<String, String> dynamicData = new HashMap<String, String>();
	private static final String FILE_NAME = "/ExtentReport2.0.html";
	private static String EXT_CONFIG_FILE = "";
	protected static ConfigReader configReader;
	private static int result = 1;

	private static long strStartTime;
	private static long strEndTime;
	public static String testExecutionTime;
	public static int totalTCs =0;
	public static int passedTCs;
	public static int failedTCs;
	public static int blockedTCs;
	public static int errorTCs;
	public static String strTestName;
	public static String strTestGroup;

	public static String[] testNGGroups;
	private static String lastOperationKey = "";

	public static List<HashMap<String, String>> emailResults;
	public static HashMap<String, String> emailResultsAtTestLevel = new HashMap<>();

	public static HashMap<String, List<HashMap<String, String>>> resultsGroup = new HashMap<String, List<HashMap<String, String>>>();
	public static HashMap<String, ExtentReports> resultsGroupExtentReports = new HashMap<String, ExtentReports>();
	public static String strTestSuiteName;

	
	protected static BusinessLib businessLibHTNG = null;
	protected static BusinessLib businessLibOWS = null;
	protected static BusinessLib businessLibOPERA = null;
	
	/*
	 * This method is to set the resortEntry (Chain, Property, User and
	 * Password) against which the test cases are to be executed
	 */
	public static void setResortEntry(String resortEntry) {
		configReader.setResortEntry(resortEntry);
		if (configReader.checkIfValidResortEntry() == false) {
			System.out.println("Invalid Resort Entry. " + resortEntry);
			Logs.logger.info("INVALID RESORT ENTRY. " + resortEntry);
			System.exit(0);
		} else {
			Logs.logger.info("RESORT ENTRY IS FOUND IN THE WSCONFIG FILE " + resortEntry);
		}
	}

	/*
	 * This method is to set the resortEntry (Chain, Property, User and
	 * Password) against which the test cases are to be executed
	 */
	public static void setWSLayer(String wsLayer) {
		configReader.setLayer(wsLayer.trim());
		if (wsLayer.equalsIgnoreCase("OEDS") || wsLayer.equalsIgnoreCase("SB") || wsLayer.equalsIgnoreCase("SSD")) {
			Logs.logger.info("VALID WS LAYER ENTERED IN TestNG XML Suite " + wsLayer);
		} else {
			System.out.println("Invalid WSLayer. " + wsLayer);
			Logs.logger.info("INVALID WS LAYER. " + wsLayer);
			System.exit(0);
		}
	}

	/**
	 * Description: Method to load configuration data, global properties and to
	 * establish a DB connection
	 *
	 * @throws Exception
	 * @author sbasana
	 */

	public static void beforeTest(String environemnt, String testGroup, String fileName, String runOnEntry, String wsLayer, String version) throws Exception {
		System.out.println("Before Suite Start");
		configReader = new ConfigReader(environemnt);
		dsReader = new DSReader();
		securityData = configReader.getSecurityVariables();
		strTestGroup = testGroup;
		if (resultsGroup.containsKey(testGroup)) {
			emailResults = resultsGroup.get(testGroup);
			//Extent Reports
			report = resultsGroupExtentReports.get(testGroup);
		} else {
			emailResults = new ArrayList<HashMap<String, String>>();
			resultsGroup.put(testGroup, emailResults);
			// Extent Reports
			report = new ExtentReports(OUTPUT_FOLDER + "/" + testGroup + ".html", NetworkMode.OFFLINE);
			resultsGroupExtentReports.put(testGroup, report);
		}
		if (configReader.verifyIfFileExists(configReader.getParentPath() + EXT_CONFIG_FILE) == true) {
			report.loadConfig(new File(configReader.getParentPath() + EXT_CONFIG_FILE));
		} else {
			report.loadConfig(new File(configReader.getWorkingDirectory() + EXT_CONFIG_FILE));
		}
		loadDatabaseConnection();
		setResortEntry(runOnEntry);
		setWSLayer(wsLayer);
		System.out.println("End of Before Method");
		businessLibHTNG = new BusinessLib(Product.HTNG, version);
		businessLibOWS = new BusinessLib(Product.OWS, version);
		businessLibOPERA = new BusinessLib(Product.OPERA, version);
		Logs.logger.info("Extent Report is initialized..");
		System.out.println("Before Suite End");

	}

	/**
	 * Description: Method to load configuration data, global properties and to
	 * establish a DB connection
	 *
	 * @throws Exception
	 * @author sbasana
	 */

	public static void beforeSuite() throws Exception {
		strStartTime = System.currentTimeMillis();
		configReader = new ConfigReader();
		propUtil = new PropUtil();

		String timeStampStr = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_"
				+ new SimpleDateFormat("HHMMSS").format(new Date());
		setTimeStamp(timeStampStr);
		constructdataXMLPath();

		Logs.start(OUTPUT_FOLDER + "log.out");
		Logs.logger.info(
				"*******************************************STARTING BEFORE SUITE ACTIONS*************************************************");
		Logs.logger.info("OUTPUT FOLDER: " + OUTPUT_FOLDER);
		EXT_CONFIG_FILE = propUtil.getPropertyValue(configReader.getPathConfig(), "ExtentReportConfigFile");
		// report = new ExtentReports(OUTPUT_FOLDER + FILE_NAME,
		// NetworkMode.OFFLINE);

		System.out.println("Before Suite End");
	}

	/*
	 * This method is to handle the database connections separately for each
	 * test case.
	 */
	public static synchronized void loadDatabaseConnection() {
		String port;
		try {
			String dbPort = configReader.getDBData("DBPort");
			if (dbPort.contains("."))
				port = dbPort.substring(0, dbPort.indexOf("."));
			else
				port = dbPort;
			System.out.println("Connection Details are read from configuration XML");
			DatabaseUtil.setDBConnection(configReader.getDBData("DBHost"), port, configReader.getDBData("DBService"),
					configReader.getDBData("DBUser"), configReader.getDBData("DBPassword"));
			dbConnection = DatabaseUtil.connection;
			inpObj = new DataGen(dbConnection, configReader);
			Logs.logger.info(
					"DB Connection established. Connection String >> " + "Host: " + configReader.getDBData("DBHost")
							+ " Port: " + port + " Service: " + configReader.getDBData("DBService") + " User: "
							+ configReader.getDBData("DBUser") + " Password: " + configReader.getDBData("DBPassword"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getQueryHeaderFromOperationKeyword() {
		String queryHeader = "/*SWAF(" + System.getProperty("user.name") + ") ";
		if (lastOperationKey.length() != 0) {
			queryHeader = queryHeader + configReader.getModule(lastOperationKey) + ":";
			queryHeader = queryHeader + configReader.getService(lastOperationKey) + ":";
			queryHeader = queryHeader + configReader.getOperation(lastOperationKey) + "*/";
		} else {
			queryHeader = queryHeader + "Internal*/";
		}
		return queryHeader;
	}

	/**
	 * Description: Method to close/flush connections that are established as
	 * part of the execution,after every suite gets executed
	 *
	 * @author sbasana
	 * @throws Exception
	 */
	// @AfterSuite(alwaysRun = true)
	public static synchronized void afterSuite() throws Exception {
		DatabaseUtil.closeDatabase();
		dbConnection.close();
		inpObj.flushConnections();
		inpObj = null;
		
		for (Entry<String, ExtentReports> entry : resultsGroupExtentReports.entrySet())
		{
		    System.out.println(entry.getKey() + " closing the connection: " );
		    entry.getValue().close();
		    
		}
		
		
//		report.close();
		Logs.logger.info("All connections are closed");

		strEndTime = System.currentTimeMillis();
		testExecutionTime = ((strEndTime - strStartTime) / (1000 * 60)) + " Mins " + ((strEndTime - strStartTime) % 60)
				+ " Sec ";

//		totalTCs = emailResults.size();

		StringBuffer tcResults = new StringBuffer();
		int sNo = 0;

		for (Entry<String, List<HashMap<String, String>>> entryResult : resultsGroup.entrySet()) {
			emailResults = entryResult.getValue();
			totalTCs = totalTCs + emailResults.size();
			tcResults.append("<tr> ");
			tcResults.append("<td colspan='4' class='testGroup'> "+entryResult.getKey() + "</td>" );
			for (HashMap<String, String> hashMap : emailResults) {
				tcResults.append("<tr>");
				tcResults.append("<td> " + ++sNo + "</td>");
				tcResults.append("<td> " + hashMap.get("TestName") + "</td>");
//				tcResults.append("<td> " + hashMap.get("Result") + "</td>");
				String tcResult = hashMap.get("Result");

//				for (Map.Entry<String, String> entry : hashMap.entrySet()) {
//					System.out.println(entry.getKey() + "/" + entry.getValue());
					switch(tcResult) {
						case "Pass": tcResults.append("<td class='pass'> " + tcResult + "</td>");
					break;
						case "Fail": tcResults.append("<td class='fail'> " + tcResult + "</td>");
					break;
						case "Blocked": tcResults.append("<td class='blocked '> " + tcResult + "</td>");
					break;
						case "Error": tcResults.append("<td class='fail'> " +tcResult + "</td>");
					break;
						default : 
							tcResults.append("<td> " + tcResult + "</td>");
					}
//				}
					tcResults.append("<td> " + hashMap.get("Execution Time") + "</td>");

				if (hashMap.get("Result").equalsIgnoreCase("Pass")) {
					passedTCs = passedTCs + 1;
				} else if (hashMap.get("Result").equalsIgnoreCase("Fail")) {
					failedTCs = failedTCs + 1;
				} else if (hashMap.get("Result").equalsIgnoreCase("Blocked")) {
					blockedTCs = blockedTCs +1;
				} else if (hashMap.get("Result").equalsIgnoreCase("Error")) {
					errorTCs = errorTCs +1;
				}
				tcResults.append("</tr>");
			}
			
		}

		// System.out.println(tcResults);
		tcFinalResults = tcFinalResults + tcResults.toString();

	}

	/*public static void afterTest() {

		StringBuffer tcResults = new StringBuffer();
		int sNo = 0;

		tcResults.append("<br/> 	<center> <b> " + strTestName + " </b> </center> <br/>");
		tcResults.append("<table>");
		for (HashMap<String, String> hashMap : emailResults) {
			tcResults.append("<tr>");
			tcResults.append("<td> " + ++sNo + "</td>");
			for (Map.Entry<String, String> entry : hashMap.entrySet()) {
				System.out.println(entry.getKey() + "/" + entry.getValue());
				tcResults.append("<td> " + entry.getValue() + "</td>");
			}
			if (hashMap.get("Result").equalsIgnoreCase("Pass")) {
				passedTCs = passedTCs + 1;
			} else if (hashMap.get("Result").equalsIgnoreCase("Fail")) {
				failedTCs = failedTCs + 1;
			} else if (hashMap.get("Result").equalsIgnoreCase("Warning")) {
				failedTCs = failedTCs + 1;
			}
			tcResults.append("</tr>");
		}

		tcResults.append("</table>");

		// System.out.println(tcResults);
		tcFinalResults = tcResults.toString();
	}*/

	public static String emailBody() {
		String msgbody = null;
		try {
			msgbody = FileUtils.readFileToString(new File(ConfigReader.getReportsPath() + "Email_config.html"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		msgbody = msgbody.replace("var_TimeTaken", WSLib.testExecutionTime);
		msgbody = msgbody.replace("var_ExecutedBy", System.getProperty("user.name"));
		msgbody = msgbody.replace("var_TestSuiteName", strTestSuiteName);

		msgbody = msgbody.replace("var_TotalTCs", Integer.toString(WSLib.totalTCs));
		msgbody = msgbody.replace("var_PassedTCs", Integer.toString(WSLib.passedTCs));
		msgbody = msgbody.replace("var_FailedTCs", Integer.toString(WSLib.failedTCs));
		msgbody = msgbody.replace("var_BlockedTCs", Integer.toString(WSLib.blockedTCs));
		msgbody = msgbody.replace("var_ErrorTCs", Integer.toString(WSLib.errorTCs));

		msgbody = msgbody.replace("var_TestCaseBody", WSLib.tcFinalResults);
		
		File newFile = new File(OUTPUT_FOLDER+"\\"+"tmpMessageBody.html");		
		FileWriter reqWriter;
		try {
			reqWriter = new FileWriter(newFile);
			BufferedWriter bufWrite = new BufferedWriter(reqWriter);
			bufWrite.write(msgbody);
			bufWrite.flush();
			bufWrite.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msgbody;
	}

	// @BeforeMethod(alwaysRun = true)
	public static synchronized void beforeMethod(String runOnEntry, String wsLayer) {
		serverLogsFlag = false;		
		schema = configReader.getSchema();
		System.out.println("DB Schema: "+schema);
		setData("{var_schema}", schema);
	}

	/**
	 * @author sbasana Description: Method to logs the result (pass/fail) of
	 *         each test upon it's execution completion
	 * @param result
	 */
	// @AfterMethod(alwaysRun = true)
	public static synchronized void afterMethod(String timeInSec) {
		try {
			if (wsLogFlag) {
				logger.setWSLog(CustomHTMLMethods.logsReport("WebLogic Server", wsLogslist));
				wsLogFlag = false;
			}

			if (sbLogFlag) {
				logger.setSBLog(CustomHTMLMethods.logsReport("ServiceBus Server", sbLogslist));
				sbLogFlag = false;
			}

			report.endTest(logger);
			report.flush();
			varData.clear();
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			emailResultsAtTestLevel.put("Execution Time", timeInSec);
			emailResults.add((HashMap<String, String>) emailResultsAtTestLevel.clone());
			System.out.println("emailResultsAtTestLevel " + emailResultsAtTestLevel);
			System.out.println("emailResults " + emailResults);
			emailResultsAtTestLevel.clear();
			System.out.println("emailResults " + emailResults);
		}

	}

	/**
	 * @author sbasana Description: Method to exclude the namespaces from the
	 *         xPath
	 * @param node
	 * @return String
	 */
	public static String getXPath(Node node) {
		Node parent = node.getParentNode();
		String nodeName = node.getNodeName();
		int index = 1;
		if (parent == null || nodeName.equals("soapenv:Body") || nodeName.equals("soap:Body")
				|| nodeName.equals("soap:Header") || nodeName.equals("soapenv:Header"))
			return "/";
		Node temp = node.getPreviousSibling();
		while (temp != null) {
			if (temp.getNodeName().equals(nodeName)) {
				index++;
			}
			temp = temp.getPreviousSibling();
		}
		if (index == 1)
			return getXPath(parent) + "/" + node.getNodeName().substring(node.getNodeName().lastIndexOf(":") + 1);
		else
			return getXPath(parent) + "/" + node.getNodeName().substring(node.getNodeName().lastIndexOf(":") + 1) + "["
					+ index + "]";
	}

	/**
	 * @author sbasana Description: Method to get Query by Query Set ID (QS_ID)
	 * @param querySetID
	 * @return String
	 */
	public static String getQuery(String querySetID) throws Exception {
		String query = "";
		HashMap<String, String> queries = new HashMap<String, String>();
		String dataSourcePath = lastRunData.get("dataFilePath") + lastRunData.get("productToInvoke") + "\\"
				+ lastRunData.get("service") + "\\" + lastRunData.get("operation") + ".xlsx";
		queries = dsReader.getQuery(dataSourcePath, querySetID);
		query = queries.get("Query");
		if (queries.get("Variables") != null && !queries.get("Variables").isEmpty()) {
			String[] variables = queries.get("Variables").split(",");
			for (int i = 0; i < variables.length; i++) {
				if (query.contains(variables[i].trim())) {
					if (varData.containsKey(variables[i].trim())) {
							query = query.replace(variables[i].trim(), varData.get(variables[i].trim()));
					} else {
						Logs.logger.error("Found that the variable Data is not available during query construction "
								+ variables[i].trim());
						writeToReport(LogStatus.FAIL, "Global Data is not available for " + variables[i].trim());
					}
				}
			}
		}
		writeToReport(LogStatus.INFO, "<b>Query: </b>" + query);
		return query;
	}

	/**
	 * @author sbasana Description: Method to construct the directory to store
	 *         request and response files for each suite that is under execution
	 */
	private static void constructdataXMLPath() throws Exception {
		dataXMLPath = "";
		OUTPUT_FOLDER = "";
		try {
			dataXMLPath = configReader.getResultsPath() + "Run_" + timeStamp + "\\";
			Logs.logger.info("Results path: " + dataXMLPath);
			OUTPUT_FOLDER = dataXMLPath;
		} catch (Exception e) {
			Logs.logger.error("Creating DataXMLPath Directory is failed! " + e.getStackTrace());
			writeToReport(LogStatus.FAIL, "Creating DataXMLPath Directory is failed! " + e.getStackTrace());
		}
	}

	public static void getHeaderGMTTime() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis() - 19800000);
		Date date = new Date(timestamp.getTime());
		setData("{var_headerGMTTime}", new SimpleDateFormat("yyyy-MM-dd").format(date) + "T"
				+ new SimpleDateFormat("HH:mm:ss").format(date) + "."
				+ timestamp.toString().substring(timestamp.toString().length() - 3, timestamp.toString().length())
				+ "Z");

	}

	/**
	 * @author sbasana Description: Method to fetch the Request Message XPath of
	 *         a given XPath variable
	 * @param xPathVariable
	 * @return String
	 */
	public static String getRequestXPath(String xPathVariable) throws Exception {
		String xPath = propUtil.getPropertyValue(
				configReader.getPropertiesFilePath() + "\\" + lastRunData.get("productToInvoke") + "\\"
						+ lastRunData.get("service") + "\\" + "REQ_" + lastRunData.get("operation") + ".properties",
				xPathVariable);
		return xPath;
	}

	/**
	 * @author sbasana Description: Method to fetch the Response Message XPath
	 *         of a given XPath variable
	 * @param xPathVariable
	 * @return String
	 */
	public static String getResponseXPath(String xPathVariable) throws Exception {
		String xPath = propUtil.getPropertyValue(
				configReader.getPropertiesFilePath() + "\\" + lastRunData.get("productToInvoke") + "\\"
						+ lastRunData.get("service") + "\\" + "RES_" + lastRunData.get("operation") + ".properties",
				xPathVariable);
		return xPath;
	}

	/**
	 * @author sbasana Description: Method to fetch the Node List of a given
	 *         XPath
	 * @param xPath
	 * @param responsePath
	 * @return NodeList
	 */
	protected static NodeList getNodeListOfAnXPath(String xPath, String responsePath) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		org.w3c.dom.Document doc = docBuilder.parse(new File(responsePath));
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile(xPath);
		NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		return nodeList;
	}

	/**
	 * @author sbasana Description: Method to fetch the Node List of a given
	 *         XPath
	 * @param xPath
	 * @param responsePath
	 * @return NodeList
	 */
	protected static NodeList getNodeListOfAnXPathinXML(String xPath, String xml) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
		org.w3c.dom.Document doc = docBuilder.parse(inputStream);
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile(xPath);
		NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		return nodeList;
	}

	/**
	 * @author sbasana Description: Method to fetch the Node List of a given
	 *         XPath
	 * @param parenetPath
	 * @param elementXpath
	 * @return NodeList
	 * @throws Exception
	 */
	protected static List<String> getNodeList(String xml, String parenetPath, String elementXpath, XMLType xmlType)
			throws Exception {
		List<String> nodeValues = new ArrayList<String>();
		String[] elementXpaths = elementXpath.split(";");
		NodeList nodeList = getNodeListOfAnXPathinXML(parenetPath, xml);
		NodeList tmpNodeList;
		if (nodeList.getLength() > 0) {
			for (int i = 1; i <= nodeList.getLength(); i++) {
				if (elementXpath == "") {
					tmpNodeList = getNodeListOfAnXPathinXML((parenetPath + "[" + i + "]"), xml);
					if (tmpNodeList.getLength() > 0) {
						for (int counter = 0; counter < tmpNodeList.getLength(); counter++) {
							Node node = tmpNodeList.item(counter);
							nodeValues.add(node.getTextContent());
						}
					} else {
						nodeValues.add("*null*");
					}
				} else {
					String tmpStr = "";
					for (int j = 0; j < elementXpaths.length; j++) {
						String elementValue = "";
						String str = elementXpaths[j].replace(parenetPath, "");
						String tmpElementXpath = parenetPath + "[" + i + "]" + str;
						// tmpElementXpath = tmpElementXpath.replace("@", "/@");
						elementValue = getElementValue(xml, tmpElementXpath, xmlType);
						if (elementValue == "")
							elementValue = "*null*";
						tmpStr = tmpStr + ";" + elementValue;
					}
					tmpStr = tmpStr.substring(1);
					nodeValues.add(tmpStr);
				}
			}
		}
		return nodeValues;
	}

	public static LinkedHashMap<String, String> getSingleNodeList(String xml, HashMap<String, String> xPath,
			boolean readMap, XMLType xmlType) throws Exception {
		List<LinkedHashMap<String, String>> singleNodelist = getMultipleNodeList(xml, xPath, readMap, xmlType);
		if (singleNodelist.size() == 0) {
			return new LinkedHashMap<String, String>();
		}
		return singleNodelist.get(0);
	}

	@SuppressWarnings("rawtypes")
	public static List<LinkedHashMap<String, String>> getMultipleNodeList(String xml, HashMap<String, String> xPath,
			boolean readMap, XMLType xmlType) throws Exception {
		List<LinkedHashMap<String, String>> nodeValues = new ArrayList<LinkedHashMap<String, String>>();
		HashMap<String, String> elementXPaths = new HashMap<String, String>();

		Iterator<?> it = xPath.entrySet().iterator();
		while (it.hasNext()) {
			String tmpStr = "";
			String elementXpath = "";
			Map.Entry pair = (Map.Entry) it.next();
			String parentPath = "";
			if (xmlType == XMLType.REQUEST) {
				parentPath = getRequestXPath(pair.getValue().toString());
				tmpStr = getRequestXPath(pair.getKey().toString());

			} else if (xmlType == XMLType.RESPONSE) {
				parentPath = getResponseXPath(pair.getValue().toString());
				tmpStr = getResponseXPath(pair.getKey().toString());

			}

			tmpStr = tmpStr.trim().replace(parentPath.toString().trim(), "");
			elementXpath = elementXpath + ";" + tmpStr;
			elementXpath = elementXpath.substring(1);
			elementXPaths.put(elementXpath, parentPath);
		}

		Iterator<?> itr = elementXPaths.entrySet().iterator();
		Map.Entry pair = (Map.Entry) itr.next();
		NodeList nodeList = getNodeListOfAnXPathinXML(pair.getValue().toString(), xml);
		if (nodeList.getLength() > 0) {
			for (int i = 1; i <= nodeList.getLength(); i++) {
				LinkedHashMap<String, String> nodeLists = new LinkedHashMap<String, String>();
				itr = elementXPaths.entrySet().iterator();
				while (itr.hasNext()) {
					pair = (Map.Entry) itr.next();
					NodeList tmpNodeList;
					if (pair.getKey().toString() == "") {
						tmpNodeList = getNodeListOfAnXPathinXML((pair.getValue().toString() + "[" + i + "]"), xml);
						if (tmpNodeList.getLength() > 0) {
							Node node = tmpNodeList.item(0);
							nodeLists.put(node.getNodeName(), node.getTextContent());
						} else {
							nodeLists.put("*null*", "*null*");
						}
					} else {
						for (int j = 1; j <= 5; j++) {
							String elementValue = "";
							String str = pair.getKey().toString().replace(pair.getValue().toString(), "");

							if (str.indexOf("/@") == 0)
								str = str.replace("@", "[" + j + "]@");
							else
								str = str + "[" + j + "]";

							String tmpElementXpath = pair.getValue().toString() + "[" + i + "]" + str;
							// tmpElementXpath = tmpElementXpath.replace("@",
							// "/@");
							elementValue = getElementValue(xml, tmpElementXpath, xmlType);
							if (elementValue == "" || elementValue.contains("doesn't exist")) {
								elementValue = "*null*";
							}

							if (!elementValue.equalsIgnoreCase("*null*")) {

								str = str.replace("@", "");
								// elementValue = elementValue.substring(1);

								if (str.equals("[" + Integer.toString(j) + "]")) {
									String splitted[] = pair.getValue().toString().split("/");
									str = splitted[splitted.length - 1];
									str = str + Integer.toString(j);

								}

								if (str.contains(Integer.toString(j))) {
									str = str.replace("[", "");
									str = str.replace("]", "");
									str = str.replace(Integer.toString(j), "");
									str = str + Integer.toString(j);
								}

								str = str.replace("/", "");
								if (str.length() > 30) {
									str = str.substring(str.length() - 30, str.length());
								}
								nodeLists.put(str, elementValue);
							}
						}
					}
				}
				if (nodeLists.size() > 0) {
					nodeValues.add(nodeLists);
				}
			}
		}

		if (readMap) {
			String dataSourcePath = lastRunData.get("dataFilePath") + lastRunData.get("productToInvoke") + "\\"
					+ lastRunData.get("service") + "\\" + lastRunData.get("operation") + ".xlsx";
			HashMap<String, String> outhash = new HashMap<String, String>();
			outhash = dsReader.getServiceMap(dataSourcePath);
			List<LinkedHashMap<String, String>> mappedNodeValues = new ArrayList<LinkedHashMap<String, String>>();
			LinkedHashMap<String, String> tempHash = new LinkedHashMap<String, String>();

			int i = 0;
			while (i < nodeValues.size()) {
				Iterator it2 = nodeValues.get(i).entrySet().iterator();
				while (it2.hasNext()) {
					pair = (Map.Entry) it2.next();
					try {
						tempHash.put(outhash.get(pair.getKey()), pair.getValue().toString());
					} catch (Exception e) {
						Logs.logger.error("mapping not found!");
					}
				}
				mappedNodeValues.add(tempHash);
				tempHash = new LinkedHashMap<String, String>();
				i++;
			}

			return mappedNodeValues;

		} else {

			return nodeValues;
		}

	}

	public static List<String> getElementMultipleValuesFromResponse(String xml, String elementXpath, XMLType xmlType)
			throws Exception {
		List<String> out = new ArrayList<String>();
		org.w3c.dom.Document doc;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		int flag = 0;
		if (elementXpath != "") {
			if (elementXpath.contains("/"))
				flag = 1;
			if (flag == 0) {
				if (xmlType == XMLType.REQUEST) {
					elementXpath = getRequestXPath(elementXpath);
				} else if (xmlType == XMLType.RESPONSE) {
					elementXpath = getResponseXPath(elementXpath);
				}
			}
			if (elementXpath.contains("@"))
				elementXpath = elementXpath.replace("@", "/@");

			InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
			doc = docBuilder.parse(inputStream);

			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile(elementXpath);
			NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			String value = "";
			for (int i = 0; i < nodeList.getLength(); i++) {
				value = nodeList.item(i).getTextContent();
				out.add(value);
			}
		}

		return out;
	}

	/**
	 * @author sbasana Description: Method to create the request message using
	 *         the request xml schema definition
	 * @param XML
	 *            TemplateFilePath
	 * @param operationName
	 * @param authFilePath
	 * @param dataMap
	 * @param requestXMLPath
	 * @param dataSetID
	 * @param timeStamp
	 * @return String
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException
	 */
	private static String createRequestUsingSOAPTemplate(String XMLTemplateFilePath, String operationName,
			String authFilePath, HashMap<String, String> dataMap, String dataSetID,
			String timeStamp) throws IOException, ParserConfigurationException, SAXException, TransformerException {

		String reqPath = "", securityStr = "", line = "", output="";
		String schemaXML = XMLTemplateFilePath + "req_" + operationName + ".xml";
		try {
			File varTmpDir = new File(schemaXML);
			boolean exists = varTmpDir.exists();
			if (exists) {
				// Read the schema file for the required operation
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				org.w3c.dom.Document doc = docBuilder.parse(schemaXML);
				Logs.logger.info("Schema file for " + operationName + " is read..");

				// Read security header from the authentication file
				File authFile = new File(authFilePath);

				// System.out.println(authFilePath);
				BufferedReader bufReader = new BufferedReader(new FileReader(authFile));

				// Parse the security file and replace the actual
				while ((line = bufReader.readLine()) != null) {
					securityStr += line;
				}
				// System.out.println(securityData);
				ArrayList<String> securityvariables = new ArrayList<String>(securityData.keySet());
				for (int i = 0; i < securityvariables.size(); i++) {
					String key = securityvariables.get(i);
					if (securityStr.contains(key)) {
						if (securityData.get(key).contains("var") || securityData.get(key).contains("VAR")) {
							securityStr = securityStr.replace(key, getData(securityData.get(key)));
							// System.out.println(securityStr);
						} else if (securityData.get(key).contains("keyword")
								|| securityData.get(key).contains("KEYWORD")) {
							String keywordVal = inpObj.getDynamicData(securityData.get(key));
							securityStr = securityStr.replace(key, keywordVal);
						}

						else {
							securityStr = securityStr.replace(key, securityData.get(key));
						}
					}

				}
				Logs.logger.info("Header is read and formatted with the actual values from the Config sheet");
				bufReader.close();
				org.w3c.dom.Document docHeader = null;
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder;
				try {
					builder = factory.newDocumentBuilder();
					docHeader = builder.parse(new InputSource(new StringReader(securityStr)));
				} catch (Exception e) {
					e.printStackTrace();
				}
				NodeList list = docHeader.getChildNodes();
				Node root = null;
				for (int i = 0; i < list.getLength(); i++) {
					if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
						root = list.item(i);
						break;
					}
				}

				Node headerNode = doc.getElementsByTagName("soapenv:Header").item(0);
				if (headerNode == null)
					headerNode = doc.getElementsByTagName("soap:Header").item(0);
				Node importedNode = doc.adoptNode(root.cloneNode(true));
				headerNode.getParentNode().replaceChild(importedNode, headerNode);
				Node node;
				node = doc.getElementsByTagName("soapenv:Body").item(0);
				if (node == null)
					node = doc.getElementsByTagName("soap:Body").item(0);
				// get attribute nodes which are to be populated or to be
				// deleted based on test data
				NodeList nList = node.getChildNodes();
				doc = populateDataOnRequest(nList, doc, operationName, dataMap);
				// path to folder where request XML would be stored
				/*reqPath = requestXMLPath + operationName + "_" + dataSetID.replace("_", "").trim() + "_" + timeStamp
						+ "_RQ" + "_" + "1" + ".xml";
				Logs.logger.info("Request >> " + reqPath);
				// handle when request file is alreday available
				File newFile = new File(reqPath);
				while (newFile.exists()) {
					int index = reqPath.lastIndexOf("_");
					String reqFileNoStr = reqPath.substring(index + 1, reqPath.indexOf(".xml"));
					int reqFileNo = Integer.parseInt(reqFileNoStr) + 1;
					reqPath = reqPath.substring(0, index + 1) + reqFileNo + ".xml";
					newFile = new File(reqPath);

				}

				FileWriter reqWriter = new FileWriter(reqPath);
				BufferedWriter bufWrite = new BufferedWriter(reqWriter);*/
				// transform XML document to string format
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				StringWriter writer = new StringWriter();
				transformer.transform(new DOMSource(doc), new StreamResult(writer));
				output = writer.getBuffer().toString().replaceAll("\n|\r", "");
				// Write to request XML file
				/*bufWrite.write(output);
				bufWrite.flush();
				bufWrite.close();*/
			} else {
				String errMsg = "Schema file " + schemaXML + " doesn't exist ";
				writeToReport(LogStatus.FAIL, errMsg);
				throw new IllegalArgumentException(errMsg);
			}
		} catch (Exception e) {
			Logs.logger.error("Creating Request message from XML Template is failed");
			writeToReport(LogStatus.FAIL, "Creating Request message from XML Template is failed" + e);
		}
		return output;
	}

	private static String createRequestUsingXMLTemplate(String XMLTemplateFilePath, String operationName,
			HashMap<String, String> dataMap, String dataSetID, String timeStamp)
			throws IOException, ParserConfigurationException, SAXException, TransformerException {

		String reqPath = "", output="";
		String schemaXML = XMLTemplateFilePath + "req_" + operationName + ".xml";
		try {
			File varTmpDir = new File(schemaXML);
			boolean exists = varTmpDir.exists();
			if (exists) {
				// Read the schema file for the required operation
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				org.w3c.dom.Document doc = docBuilder.parse(schemaXML);
				Logs.logger.info("Schema file for " + operationName + " is read..");

				// Read Root Element of the XML
				Node node = doc.getDocumentElement();
				System.out.println(node.getNodeName());

				String nodeXPath = getXPath(node);
				// Handle Root Node Attributes
				org.w3c.dom.Element el = (org.w3c.dom.Element) node;
				NamedNodeMap attrNodeMap = el.getAttributes();
				ArrayList<String> attrMap = new ArrayList<String>();
				for (int s = 0; s < attrNodeMap.getLength(); s++) {
					attrMap.add(attrNodeMap.item(s).getNodeName());
				}
				int numAttr = attrMap.size();
				String attributeName = "";
				int j = 0;
				while (j < numAttr) {
					attributeName = attrMap.get(j);
					String attrValue = null;
					if (dataMap.containsKey(nodeXPath + "@" + attributeName))
						attrValue = dataMap.get(nodeXPath + "@" + attributeName);
					// delete attribute if no value or empty value provided
					if (attrValue == null || attrValue.equals("")) {
						if (el.hasAttribute(attributeName))
							el.removeAttribute(attributeName);
					} else {
						el.setAttribute(attributeName, attrValue);
					}
					j = j + 1;
				}

				// get attribute nodes which are to be populated or to be
				// deleted based on test data
				NodeList nList = node.getChildNodes();
				doc = populateDataOnRequest(nList, doc, operationName, dataMap);
				/*// path to folder where request XML would be stored
				reqPath = resultRequestXMLPath + operationName + "_" + dataSetID.replace("_", "").trim() + "_"
						+ timeStamp + "_RQ" + "_" + "1" + ".xml";
				Logs.logger.info("Request >> " + reqPath);
				// handle when request file is alreday available
				File newFile = new File(reqPath);
				while (newFile.exists()) {
					int index = reqPath.lastIndexOf("_");
					String reqFileNoStr = reqPath.substring(index + 1, reqPath.indexOf(".xml"));
					int reqFileNo = Integer.parseInt(reqFileNoStr) + 1;
					reqPath = reqPath.substring(0, index + 1) + reqFileNo + ".xml";
					newFile = new File(reqPath);

				}

				FileWriter reqWriter = new FileWriter(reqPath);
				BufferedWriter bufWrite = new BufferedWriter(reqWriter);*/
				// transform XML document to string format
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				StringWriter writer = new StringWriter();
				transformer.transform(new DOMSource(doc), new StreamResult(writer));
				output = writer.getBuffer().toString().replaceAll("\n|\r", "");
				// Write to request XML file
				/*bufWrite.write(output);
				bufWrite.flush();
				bufWrite.close();*/
			} else {
				String errMsg = "Schema file " + schemaXML + " doesn't exist ";
				writeToReport(LogStatus.FAIL, errMsg);
				throw new IllegalArgumentException(errMsg);
			}
		} catch (Exception e) {
			Logs.logger.error("Creating Request message from XML Template is failed");
			writeToReport(LogStatus.FAIL, "Creating Request message from XML Template is failed" + e);
		}
		return output;
	}

	/**
	 * @author sbasana Description: Method to populate the data on the request
	 *         message
	 * @param nList
	 * @param doc
	 * @param operationName
	 * @param dataMap
	 * @return String
	 */

	@SuppressWarnings("unused")
	private static org.w3c.dom.Document populateDataOnRequest(NodeList nList, org.w3c.dom.Document doc,
			String operationName, HashMap<String, String> dataMap) {
		HashMap<String, String> xPathsAndVariables = new HashMap<String, String>();
		List<String> xPaths = new ArrayList<String>();
		int count = nList.getLength();
		int i = 0;
		try {
			while (i < count) {
				Node n = nList.item(i);
				String nodeXPath = getXPath(n);
				// Check if node is an element
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					NodeList nListTemp = n.getChildNodes();
					// Call this method recursively for child node list
					doc = populateDataOnRequest(nListTemp, doc, operationName, dataMap);
					org.w3c.dom.Element el = (org.w3c.dom.Element) n;
					NamedNodeMap attrNodeMap = el.getAttributes();
					ArrayList<String> attrMap = new ArrayList<String>();
					for (int s = 0; s < attrNodeMap.getLength(); s++) {
						attrMap.add(attrNodeMap.item(s).getNodeName());
					}
					int numAttr = attrMap.size();
					String attributeName = "";
					int j = 0;
					while (j < numAttr) {
						attributeName = attrMap.get(j);
						String attrValue = null;
						if (dataMap.containsKey(nodeXPath + "@" + attributeName))
							attrValue = dataMap.get(nodeXPath + "@" + attributeName);
						// delete attribute if no value or empty value provided
						if (attrValue == null || attrValue.equals("")) {
							if (el.hasAttribute(attributeName))
								el.removeAttribute(attributeName);
						} else {
							el.setAttribute(attributeName, attrValue);
						}
						j = j + 1;
					}

					String elementVal = null;
					if (dataMap.containsKey(nodeXPath))
						elementVal = dataMap.get(nodeXPath);
					// delete element if no value or empty value provided (Check
					// if the attributes have any values before deleting the
					// element)
					if (elementVal == null || elementVal.equals("")) {
						boolean flag = false;
						NamedNodeMap checkList = n.getAttributes();
						for (int iter = 0; iter < checkList.getLength(); iter++) {
							if (!checkList.item(iter).getNodeValue().equals("")) {
								flag = true;
								break;
							}
						}
						if (n.getChildNodes().getLength() > 0 || flag) {
							i++;
						} else {
							n.getParentNode().removeChild(n);
							count--;
						}
					} else {
						if (elementVal.equals("*"))
							n.setTextContent("");
						else
							n.setTextContent(elementVal);
						i++;
					}

				} else {
					n.getParentNode().removeChild(n);
					count--;
				}
			}
		} catch (Exception e) {
			Logs.logger.error("Populating data on request message is failed");
			writeToReport(LogStatus.FAIL, "Populating data on request message is failed" + e);

		}
		return doc;
	}

	/*
	 * Method to create a SOAP request message by replacing the variables and
	 * keywords with it's actual data
	 */
	public static String createSOAPMessage(String operationKey, String dataSetID, boolean... override)
			throws Exception {
		String XMLString="";
		List<String> conditionalParamsToBeIgnored = new ArrayList<String>();
		lastOperationKey = operationKey;
		setData("{var_headerGMTTime}", inpObj.getGMTTime());
		if (configReader.getOperation(operationKey) == null) {
			System.out.println("Operation Name in the SetviceSetUp file is null");
			Logs.logger.error("Operation Name in the SetviceSetUp file is null");
			writeToReport(LogStatus.ERROR, "Operation is null");
			throw new NullPointerException();
		} else {
			if (override.length == 1) {
				Logs.logger.info("Operation: " + operationKey);
				String service = configReader.getService(operationKey);
				String operation = configReader.getOperation(operationKey);
				String module = configReader.getModule(operationKey);

				lastRunData.clear();
				HashMap<String, String> xPathCollectionReq = new HashMap<String, String>();
				HashMap<String, String> requestDataMap = new HashMap<String, String>();
				HashMap<String, String> dataMap = new HashMap<String, String>();
				String returnStr = "", endPointUrl = "";

				// Get the file path where in the data files are located
				String dataSourcePath = configReader.getdataFilePath() + "\\" + module + "\\" + service + "\\";
				// Get the file path where in the wsService file is located
				requestDataMap.clear();
				xPathCollectionReq.clear();
				int maxLengthforSheetNm = (operation.length() < 25) ? operation.length() : 25;
				{
					String authFile = configReader.getModule(operationKey).toLowerCase() + ".txt";
					String wsHost = "";
					String resourcePath = "";

					if (configReader.getModule(operationKey).toLowerCase().equals("opera")) {
						wsHost = configReader.getWeblogicHost();
					} else if (configReader.getLayer().equals("SSD")) {
						wsHost = configReader.getSSDHost();
					} else if (configReader.getLayer().equals("OEDS")) {
						wsHost = configReader.getOEDSHost();
					} else if (configReader.getLayer().equals("SB")) {
						wsHost = configReader.getSBHost();
					}

					if (configReader.getResourcePath(operationKey) != "") {
						resourcePath = configReader.getResourcePath(operationKey);
					} else {
						String errMsg = "Resource Path for " + service + " is not configured in ServiceSetUp file";
						Logs.logger.error(errMsg);
						writeToReport(LogStatus.FAIL, errMsg);
						throw new IllegalArgumentException(errMsg);
					}

					// endPointUrl = configReader.getWebServer(productToInvoke)
					// + configReader.getResourcePath(service);
					endPointUrl = wsHost + resourcePath;
					Logs.logger.info("Endpoint: " + endPointUrl);
					Logs.logger.info("XPath properties file >> " + configReader.getPropertiesFilePath() + "\\" + module
							+ "\\" + service + "\\" + "REQ_" + operation + ".properties");
					// Read the XPaths and the corresponding variables
					xPathCollectionReq = propUtil.getProperties(configReader.getPropertiesFilePath() + "\\" + module
							+ "\\" + service + "\\" + "REQ_" + operation + ".properties");
					// Read the test data that is to be supplied on the request
					// message
					dataMap.clear();
					dataMap = dsReader.getTestData(dataSourcePath + operation + ".xlsx",
							"DS_" + operation.substring(0, maxLengthforSheetNm), dataSetID);

					// System.out.println("Datamap" + dataMap);
					// writeToReport(LogStatus.INFO, "data map :>> "+ dataMap);
					Logs.logger.info("Datasheet Path: " + dataSourcePath + operation + ".xlsx");
					Logs.logger.info("Datasheet Name: " + "DS_" + operation.substring(0, maxLengthforSheetNm));
					Logs.logger.info("TEST");
					Set<?> set = dataMap.entrySet();
					Iterator<?> it = set.iterator();

					// Eliminate the XPaths having no data
					while (it.hasNext()) {
						@SuppressWarnings("unchecked")
						Map.Entry<String, String> me = (Map.Entry<String, String>) it.next();
						if (xPathCollectionReq.containsKey(me.getKey()))
							if (!requestDataMap.containsKey(me.getKey()))
								requestDataMap.put(xPathCollectionReq.get(me.getKey()), dataMap.get(me.getKey()));
					}
					Logs.logger.info("Data Map after removing the unused elements " + requestDataMap);
					set.clear();
					it = null;
					set = requestDataMap.entrySet();
					it = set.iterator();
					boolean exitLoop = true;
					// Handle dynamic data generation, replace keywords with data
					while (it.hasNext()) {
						@SuppressWarnings("unchecked")
						Map.Entry<String, String> me = (Map.Entry<String, String>) it.next();						
						if (requestDataMap.get(me.getKey()).toUpperCase().startsWith("{KEYWORD_")) {
							String tempStr = inpObj.getDynamicData(requestDataMap.get(me.getKey()));
							// Capture the auto generated keyword data in a hash
							// map
							dynamicData.put(me.getKey(), tempStr);
							requestDataMap.put(me.getKey(), tempStr);
						}
						else if (requestDataMap.get(me.getKey()).toUpperCase().startsWith("{VAR_CONDITIONAL_PARAM_")) {
							if (varData.get(requestDataMap.get(me.getKey())) == null) {
								requestDataMap.put(me.getKey(), "");
								conditionalParamsToBeIgnored.add(me.getKey());								
								// throw new NullPointerException();
							} else {
								requestDataMap.put(me.getKey(), varData.get(requestDataMap.get(me.getKey())));
							}
						}
						// Replace variables with it's data
						else if (requestDataMap.get(me.getKey()).toUpperCase().startsWith("{VAR_")) {
							if (varData.get(requestDataMap.get(me.getKey())) == null) {
								Logs.logger.info(requestDataMap.get(me.getKey()) + "is not populated!");
								writeToReport(LogStatus.ERROR, requestDataMap.get(me.getKey()) + "is not populated");
								exitLoop = false;
								// throw new NullPointerException();
							} else {
								requestDataMap.put(me.getKey(), varData.get(requestDataMap.get(me.getKey())));
							}
						}
					}
					
					if(conditionalParamsToBeIgnored.size() > 0) {
						for(int x=0; x<conditionalParamsToBeIgnored.size();x++) {
							requestDataMap.remove(conditionalParamsToBeIgnored.get(x));
						}
					}
					
					Logs.logger.info("Data Map after the data is generated dynamically " + requestDataMap);
					if (!exitLoop) {
						throw new NullPointerException();
					}
					
					
					// Construct request message following the corresponding
					// schema with xPaths and data gather during the above steps
					String authFilePath = configReader.getsecurityFilePath(operationKey) + "\\";
					Logs.logger.info("SOAP Header file Path " + authFilePath);
					XMLString = createRequestUsingSOAPTemplate(
							configReader.getschemaFilePath() + "\\" + module + "\\" + service + "\\", operation,
							authFilePath + authFile, requestDataMap, dataSetID,
							timeStamp.substring(8).replace(".", "").trim());
					Logs.logger.info(operation + " SOAP Request Message has been created");
					varData.put("{var_currentEndPointUrl}", endPointUrl);
					returnStr = XMLString;

					// Store the required details in a global variable
					lastRunData.put("operation", operation);
					lastRunData.put("service", service);
					lastRunData.put("module", module);
					lastRunData.put("productToInvoke", module);
					lastRunData.put("operationKey", operationKey);
				/*	lastRunData.put("requestPath", XMLPath);
					lastRunData.put("responsePath", XMLPath.replace("_RQ", "_RS"));*/
					lastRunData.put("dataFilePath", configReader.getdataFilePath());
					lastRunData.put("endPointUrl", endPointUrl);
					return returnStr;
				}
			} else {

				Logs.logger.info("Operation: " + operationKey);

				String service = configReader.getService(operationKey);
				String operation = configReader.getOperation(operationKey);
				String module = configReader.getModule(operationKey);
				writeToReport(LogStatus.INFO, "********* Preparing to execute " + operation
						+ " operation <font color=blue> " + operation + "</font>" + " ***************");
				lastRunData.clear();
				HashMap<String, String> xPathCollectionReq = new HashMap<String, String>();
				HashMap<String, String> requestDataMap = new HashMap<String, String>();
				HashMap<String, String> dataMap = new HashMap<String, String>();
				String XMLPath, returnStr = "", endPointUrl = "";

				// Get the file path where in the data files are located
				String dataSourcePath = configReader.getdataFilePath() + "\\" + module + "\\" + service + "\\";
				// Get the file path where in the wsService file is located
				requestDataMap.clear();
				xPathCollectionReq.clear();
				int maxLengthforSheetNm = (operation.length() < 25) ? operation.length() : 25;
				{

					String authFile = configReader.getModule(operationKey).toLowerCase() + ".txt";
					String wsHost = "";
					String resourcePath = "";

					if (configReader.getModule(operationKey).toLowerCase().equals("opera")) {
						wsHost = configReader.getWeblogicHost();
					} else if (configReader.getLayer().equals("SSD")) {
						wsHost = configReader.getSSDHost();
					} else if (configReader.getLayer().equals("SB")) {
						wsHost = configReader.getSBHost();
					} else if (configReader.getLayer().equals("OEDS")) {
						wsHost = configReader.getOEDSHost();
					}

					if (configReader.getResourcePath(operationKey) != "") {
						resourcePath = configReader.getResourcePath(operationKey);
					} else {
						String errMsg = "Resource Path for " + service + " is not configured in ServiceSetUp file";
						Logs.logger.error(errMsg);
						writeToReport(LogStatus.FAIL, errMsg);
						throw new IllegalArgumentException(errMsg);
					}

					// endPointUrl = configReader.getWebServer(productToInvoke)
					// + configReader.getResourcePath(service);
					endPointUrl = wsHost + resourcePath;
					Logs.logger.info("Endpoint: " + endPointUrl);

					Logs.logger.info("XPath properties file >> " + configReader.getPropertiesFilePath() + "\\" + module
							+ "\\" + service + "\\" + "REQ_" + operation + ".properties");
					// Read the XPaths and the corresponding variables
					xPathCollectionReq = propUtil.getProperties(configReader.getPropertiesFilePath() + "\\" + module
							+ "\\" + service + "\\" + "REQ_" + operation + ".properties");
					// Read the test data that is to be supplied on the request
					// message
					dataMap.clear();
					dataMap = dsReader.getTestData(dataSourcePath + operation + ".xlsx",
							"DS_" + operation.substring(0, maxLengthforSheetNm), dataSetID);
					// System.out.println("Datamap" + dataMap);
					// writeToReport(LogStatus.INFO, "data map :>> "+ dataMap);
					Logs.logger.info("Datasheet Path: " + dataSourcePath + operation + ".xlsx");
					Logs.logger.info("Datasheet Name: " + "DS_" + operation.substring(0, maxLengthforSheetNm));

					Set<?> set = dataMap.entrySet();
					Iterator<?> it = set.iterator();

					// Eliminate the XPaths having no data
					while (it.hasNext()) {
						@SuppressWarnings("unchecked")
						Map.Entry<String, String> me = (Map.Entry<String, String>) it.next();
						if (xPathCollectionReq.containsKey(me.getKey()))
							if (!requestDataMap.containsKey(me.getKey()))
								requestDataMap.put(xPathCollectionReq.get(me.getKey()), dataMap.get(me.getKey()));
					}
					set.clear();
					it = null;
					set = requestDataMap.entrySet();
					it = set.iterator();
					boolean exitLoop = true;
					// Handle dynamic data generation, replace keywords with
					// actual
					// data
					while (it.hasNext()) {
						@SuppressWarnings("unchecked")
						Map.Entry<String, String> me = (Map.Entry<String, String>) it.next();
						// Replace dynamic generated data in place of keywords
						// starting
						// with "KEYWORD["
						if (requestDataMap.get(me.getKey()).toUpperCase().startsWith("{KEYWORD_")) {
							String tempStr = inpObj.getDynamicData(requestDataMap.get(me.getKey()));
							// Capture the auto generated keyword data in a hash
							// map
							dynamicData.put(me.getKey(), tempStr);
							requestDataMap.put(me.getKey(), tempStr);
						}
						else if (requestDataMap.get(me.getKey()).toUpperCase().startsWith("{VAR_CONDITIONAL_PARAM_")) {
							if (varData.get(requestDataMap.get(me.getKey())) == null) {
								requestDataMap.put(me.getKey(), "");
								// throw new NullPointerException();
							} else {
								requestDataMap.put(me.getKey(), varData.get(requestDataMap.get(me.getKey())));
							}
						}
						// Replace variables with it's data
						else if (requestDataMap.get(me.getKey()).toUpperCase().startsWith("{VAR_")) {
							if (varData.get(requestDataMap.get(me.getKey())) == null) {
								Logs.logger.info(requestDataMap.get(me.getKey()) + "is not getting populated!");
								writeToReport(LogStatus.ERROR, requestDataMap.get(me.getKey()) + "is not populated");
								exitLoop = false;
								// throw new NullPointerException();
							} else {
								requestDataMap.put(me.getKey(), varData.get(requestDataMap.get(me.getKey())));
							}
						}
					}
					
					if (!exitLoop) {
						throw new NullPointerException();
					}
					
					if(conditionalParamsToBeIgnored.size() > 0) {
						for(int x=0; x<conditionalParamsToBeIgnored.size();x++) {
							requestDataMap.remove(conditionalParamsToBeIgnored.get(x));
						}
					}
					// Construct request message following the corresponding
					// schema with xPaths and data gather during the above steps
					String authFilePath = configReader.getsecurityFilePath(operationKey);
					
					XMLString = createRequestUsingSOAPTemplate(
					configReader.getschemaFilePath() + "\\" + module + "\\" + service + "\\", operation,
					authFilePath + authFile, requestDataMap, dataSetID,
					timeStamp.substring(8).replace(".", "").trim());
					
					Logs.logger.info(operation + " SOAP Request Message has been created");
					varData.put("{var_currentEndPointUrl}", endPointUrl);
					returnStr = XMLString;

					// Store the required details in a global variable, so that
					// it
					// will
					// be accessable for the sub sequent test
					lastRunData.put("operation", operation);
					lastRunData.put("service", service);
					lastRunData.put("module", module);
					lastRunData.put("productToInvoke", module);
					lastRunData.put("operationKey", operationKey);
					lastRunData.put("dataFilePath", configReader.getdataFilePath());
					lastRunData.put("endPointUrl", endPointUrl);
					return returnStr;
				}
			}
		}
	}

	/*
	 * Method to create a SOAP request message by replacing the variables and
	 * keywords with it's actual data
	 */
	public static String createXMLMessage(String operationKey, String dataSetID, boolean... override) throws Exception {
		String XMLString="";
		lastOperationKey = operationKey;
		setData("{var_headerGMTTime}", inpObj.getGMTTime());
		if (configReader.getOperation(operationKey) == null) {
			System.out.println("Operation Name in the SetviceSetUp file is null");
			Logs.logger.error("Operation Name in the SetviceSetUp file is null");
			writeToReport(LogStatus.ERROR, "Operation is null");
			throw new NullPointerException();
		} else {
			if (override.length == 1) {
				Logs.logger.info("Operation: " + operationKey);
				String service = configReader.getService(operationKey);
				String operation = configReader.getOperation(operationKey);
				String module = configReader.getModule(operationKey);

				lastRunData.clear();
				HashMap<String, String> xPathCollectionReq = new HashMap<String, String>();
				HashMap<String, String> requestDataMap = new HashMap<String, String>();
				HashMap<String, String> dataMap = new HashMap<String, String>();
				String returnStr = "", endPointUrl = "";

				// Get the file path where in the data files are located
				String dataSourcePath = configReader.getdataFilePath() + "\\" + module + "\\" + service + "\\";
				// Get the file path where in the wsService file is located
				requestDataMap.clear();
				xPathCollectionReq.clear();
				int maxLengthforSheetNm = (operation.length() < 25) ? operation.length() : 25;
				{
					String wsHost = "";
					String resourcePath = "";

					if (configReader.getModule(operationKey).toLowerCase().equals("opera")) {
						wsHost = configReader.getWeblogicHost();
					} else if (configReader.getLayer().equals("SSD")) {
						wsHost = configReader.getSSDHost();
					} else if (configReader.getLayer().equals("OEDS")) {
						wsHost = configReader.getOEDSHost();
					} else if (configReader.getLayer().equals("SB")) {
						wsHost = configReader.getSBHost();
					}

					if (configReader.getResourcePath(operationKey) != "") {
						resourcePath = configReader.getResourcePath(operationKey);
					} else {
						String errMsg = "Resource Path for " + service + " is not configured in ServiceSetUp file";
						Logs.logger.error(errMsg);
						writeToReport(LogStatus.FAIL, errMsg);
						throw new IllegalArgumentException(errMsg);
					}

					// endPointUrl = configReader.getWebServer(productToInvoke)
					// + configReader.getResourcePath(service);
					endPointUrl = wsHost + resourcePath;
					Logs.logger.info("Endpoint: " + endPointUrl);
					Logs.logger.info("XPath properties file >> " + configReader.getPropertiesFilePath() + "\\" + module
							+ "\\" + service + "\\" + "REQ_" + operation + ".properties");
					// Read the XPaths and the corresponding variables
					xPathCollectionReq = propUtil.getProperties(configReader.getPropertiesFilePath() + "\\" + module
							+ "\\" + service + "\\" + "REQ_" + operation + ".properties");
					// Read the test data that is to be supplied on the request
					// message
					dataMap.clear();
					dataMap = dsReader.getTestData(dataSourcePath + operation + ".xlsx",
							"DS_" + operation.substring(0, maxLengthforSheetNm), dataSetID);

					// System.out.println("Datamap" + dataMap);
					// writeToReport(LogStatus.INFO, "data map :>> "+ dataMap);
					Logs.logger.info("Datasheet Path: " + dataSourcePath + operation + ".xlsx");
					Logs.logger.info("Datasheet Name: " + "DS_" + operation.substring(0, maxLengthforSheetNm));
					Set<?> set = dataMap.entrySet();
					Iterator<?> it = set.iterator();

					// Eliminate the XPaths having no data
					while (it.hasNext()) {
						@SuppressWarnings("unchecked")
						Map.Entry<String, String> me = (Map.Entry<String, String>) it.next();
						if (xPathCollectionReq.containsKey(me.getKey()))
							if (!requestDataMap.containsKey(me.getKey()))
								requestDataMap.put(xPathCollectionReq.get(me.getKey()), dataMap.get(me.getKey()));
					}
					Logs.logger.info("Data Map after removing the unused elements " + requestDataMap);
					set.clear();
					it = null;
					set = requestDataMap.entrySet();
					it = set.iterator();
					boolean exitLoop = true;
					// Handle dynamic data generation, replace keywords with
					// actual
					// data
					while (it.hasNext()) {
						@SuppressWarnings("unchecked")
						Map.Entry<String, String> me = (Map.Entry<String, String>) it.next();
						// Replace dynamic generated data in place of keywords
						// starting
						// with "KEYWORD["
						if (requestDataMap.get(me.getKey()).toUpperCase().startsWith("{KEYWORD_")) {
							String tempStr = inpObj.getDynamicData(requestDataMap.get(me.getKey()));
							// Capture the auto generated keyword data in a hash
							// map
							dynamicData.put(me.getKey(), tempStr);
							requestDataMap.put(me.getKey(), tempStr);
						}
						// Replace variables with it's data
						else if (requestDataMap.get(me.getKey()).toUpperCase().startsWith("{VAR_")) {
							if (varData.get(requestDataMap.get(me.getKey())) == null) {
								Logs.logger.info(requestDataMap.get(me.getKey()) + "is not populated!");
								writeToReport(LogStatus.ERROR, requestDataMap.get(me.getKey()) + "is not populated");
								exitLoop = false;
								// throw new NullPointerException();
							} else {
								requestDataMap.put(me.getKey(), varData.get(requestDataMap.get(me.getKey())));
							}

						}
					}
					Logs.logger.info("Data Map after the data is generated dynamically " + requestDataMap);
					if (!exitLoop) {
						throw new NullPointerException();
					}
					/*String updDataXMLPath = "";
					if (iteration == null)
						updDataXMLPath = dataXMLPath + testCaseName + "\\";
					else
						updDataXMLPath = dataXMLPath + testCaseName + "_" + iteration + "\\";
					// Create a folder with test case name
					File testCaseFolder = new File(updDataXMLPath);
					if (!testCaseFolder.exists()) {
						if (testCaseFolder.mkdir())
							Logs.logger.info(updDataXMLPath + " directory is created!");
					} else {
						Logs.logger.info(updDataXMLPath + " directory already exists");
					}*/
					// Construct request message following the corresponding
					// schema with xPaths and data gather during the above steps
					XMLString = createRequestUsingXMLTemplate(
							configReader.getschemaFilePath() + "\\" + module + "\\" + service + "\\", operation,
							requestDataMap, dataSetID, timeStamp.substring(8).replace(".", "").trim());
					Logs.logger.info(operation + "Request Message has been created");
					varData.put("{var_currentEndPointUrl}", endPointUrl);
					returnStr = XMLString;

					// Store the required details in a global variable, so that
					// it will be accessable for the sub sequent test
					lastRunData.put("operation", operation);
					lastRunData.put("service", service);
					lastRunData.put("module", module);
					lastRunData.put("productToInvoke", module);
					lastRunData.put("operationKey", operationKey);
				/*	lastRunData.put("requestPath", XMLPath);
					lastRunData.put("responsePath", XMLPath.replace("_RQ", "_RS"));*/
					lastRunData.put("dataFilePath", configReader.getdataFilePath());
					lastRunData.put("endPointUrl", endPointUrl);
					return returnStr;
				}
			} else {

				Logs.logger.info("Operation: " + operationKey);

				String service = configReader.getService(operationKey);
				String operation = configReader.getOperation(operationKey);
				String module = configReader.getModule(operationKey);
				writeToReport(LogStatus.INFO, "********* Preparing to execute " + operation
						+ " operation <font color=blue> " + operation + "</font>" + " ***************");
				lastRunData.clear();
				HashMap<String, String> xPathCollectionReq = new HashMap<String, String>();
				HashMap<String, String> requestDataMap = new HashMap<String, String>();
				HashMap<String, String> dataMap = new HashMap<String, String>();
				String returnStr = "", endPointUrl = "";

				// Get the file path where in the data files are located
				String dataSourcePath = configReader.getdataFilePath() + "\\" + module + "\\" + service + "\\";
				// Get the file path where in the wsService file is located
				requestDataMap.clear();
				xPathCollectionReq.clear();
				int maxLengthforSheetNm = (operation.length() < 25) ? operation.length() : 25;
				{

					String wsHost = "";
					String resourcePath = "";

					if (configReader.getModule(operationKey).toLowerCase().equals("opera")) {
						wsHost = configReader.getWeblogicHost();
					} else if (configReader.getLayer().equals("SSD")) {
						wsHost = configReader.getSSDHost();
					} else if (configReader.getLayer().equals("SB")) {
						wsHost = configReader.getSBHost();
					} else if (configReader.getLayer().equals("OEDS")) {
						wsHost = configReader.getOEDSHost();
					}

					if (configReader.getResourcePath(operationKey) != "") {
						resourcePath = configReader.getResourcePath(operationKey);
					} else {
						String errMsg = "Resource Path for " + service + " is not configured in ServiceSetUp file";
						Logs.logger.error(errMsg);
						writeToReport(LogStatus.FAIL, errMsg);
						throw new IllegalArgumentException(errMsg);
					}

					// endPointUrl = configReader.getWebServer(productToInvoke)
					// + configReader.getResourcePath(service);
					endPointUrl = wsHost + resourcePath;
					Logs.logger.info("Endpoint: " + endPointUrl);

					Logs.logger.info("XPath properties file >> " + configReader.getPropertiesFilePath() + "\\" + module
							+ "\\" + service + "\\" + "REQ_" + operation + ".properties");
					// Read the XPaths and the corresponding variables
					xPathCollectionReq = propUtil.getProperties(configReader.getPropertiesFilePath() + "\\" + module
							+ "\\" + service + "\\" + "REQ_" + operation + ".properties");
					// Read the test data that is to be supplied on the request
					// message
					dataMap.clear();
					dataMap = dsReader.getTestData(dataSourcePath + operation + ".xlsx",
							"DS_" + operation.substring(0, maxLengthforSheetNm), dataSetID);
					// System.out.println("Datamap" + dataMap);
					// writeToReport(LogStatus.INFO, "data map :>> "+ dataMap);
					Logs.logger.info("Datasheet Path: " + dataSourcePath + operation + ".xlsx");
					Logs.logger.info("Datasheet Name: " + "DS_" + operation.substring(0, maxLengthforSheetNm));

					Set<?> set = dataMap.entrySet();
					Iterator<?> it = set.iterator();

					// Eliminate the XPaths having no data
					while (it.hasNext()) {
						@SuppressWarnings("unchecked")
						Map.Entry<String, String> me = (Map.Entry<String, String>) it.next();
						if (xPathCollectionReq.containsKey(me.getKey()))
							if (!requestDataMap.containsKey(me.getKey()))
								requestDataMap.put(xPathCollectionReq.get(me.getKey()), dataMap.get(me.getKey()));
					}
					set.clear();
					it = null;
					set = requestDataMap.entrySet();
					it = set.iterator();
					boolean exitLoop = true;
					// Handle dynamic data generation, replace keywords with
					// actual
					// data
					while (it.hasNext()) {
						@SuppressWarnings("unchecked")
						Map.Entry<String, String> me = (Map.Entry<String, String>) it.next();
						// Replace dynamic generated data in place of keywords
						// starting
						// with "KEYWORD["
						if (requestDataMap.get(me.getKey()).toUpperCase().startsWith("{KEYWORD_")) {
							String tempStr = inpObj.getDynamicData(requestDataMap.get(me.getKey()));
							// Capture the auto generated keyword data in a hash
							// map
							dynamicData.put(me.getKey(), tempStr);
							requestDataMap.put(me.getKey(), tempStr);
						}
						// Replace variables with it's data
						else if (requestDataMap.get(me.getKey()).toUpperCase().startsWith("{VAR_")) {
							if (varData.get(requestDataMap.get(me.getKey())) == null) {
								Logs.logger.info(requestDataMap.get(me.getKey()) + "is not getting populated!");
								writeToReport(LogStatus.ERROR, requestDataMap.get(me.getKey()) + "is not populated");
								exitLoop = false;
								// throw new NullPointerException();
							} else {
								requestDataMap.put(me.getKey(), varData.get(requestDataMap.get(me.getKey())));
							}

						}
					}
					if (!exitLoop) {
						throw new NullPointerException();
					}
					/*String updDataXMLPath = "";
					if (iteration == null)
						updDataXMLPath = dataXMLPath + testCaseName + "\\";
					else
						updDataXMLPath = dataXMLPath + testCaseName + "_" + iteration + "\\";
					// Create a folder with test case name
					File testCaseFolder = new File(updDataXMLPath);
					if (!testCaseFolder.exists()) {
						if (testCaseFolder.mkdir())
							Logs.logger.info(updDataXMLPath + " directory is created!");
					}*/

					XMLString = createRequestUsingXMLTemplate(
							configReader.getschemaFilePath() + "\\" + module + "\\" + service + "\\", operation,
							requestDataMap, dataSetID, timeStamp.substring(8).replace(".", "").trim());

					Logs.logger.info(operation + " Request Message has been created");
					varData.put("{var_currentEndPointUrl}", endPointUrl);
					returnStr = XMLString;

					// Store the required details in a global variable, so that
					// it
					// will
					// be accessable for the sub sequent test
					lastRunData.put("operation", operation);
					lastRunData.put("service", service);
					lastRunData.put("module", module);
					lastRunData.put("productToInvoke", module);
					lastRunData.put("operationKey", operationKey);
					/*lastRunData.put("requestPath", XMLPath);
					lastRunData.put("responsePath", XMLPath.replace("_RQ", "_RS"));*/
					lastRunData.put("dataFilePath", configReader.getdataFilePath());
					lastRunData.put("endPointUrl", endPointUrl);
					lastRunData.put("user", configReader.getUser());
					lastRunData.put("pwd", configReader.getPassword());
					lastRunData.put("layer", configReader.getLayer());
					return returnStr;
				}
			}
		}
	}

	/**
	 * @author sbasana Description: Method to store the received value in a
	 *         global hashmap
	 * @param variableName
	 * @param value
	 */
	public static void setData(String variableName, String value) {
		varData.put(variableName, value);
	}

	/**
	 * @author sbasana Description: Method to set the global timestamp
	 * @param value
	 */
	public synchronized static void setTimeStamp(String value) {
		timeStamp = value;
	}

	/**
	 * @author sbasana Description: Method to set the testcase name that is
	 *         under execution
	 * @param tcName
	 */
	public synchronized static void setTestCaseName(String tcName) {
		testCaseName = tcName;
	}

	public synchronized static void startTest(String testcaseName, String description, String category) {
		setTestCaseName(testcaseName);
		Logs.logger.info("TestCase: " + testcaseName);
		System.out.println("Running : " + testcaseName);
		setData("Des", description);
		setData("FuncName", testcaseName);
		String[] categories = new String[testNGGroups.length + 1];
		int i;
		for (i = 0; i < testNGGroups.length; i++) {
			categories[i] = testNGGroups[i];
		}
		categories[i] = category;
		logger = report.startTest(testcaseName, description).assignCategory(categories);
		/*
		 * writeToFile("D:\\owsName.txt",
		 * description+"|"+description+"|"+"INTERFACES"+"|"+"OEDS"+
		 * "|"+"OPERA Web Services"+"|"+"Regression"+"|"+categories[1]+"|"+
		 * "Regression"+"|"+"Generic"+"|"+"Generic"+"|"+"P2"+"|"+category+"|"+
		 * "Ready"+"|"+categories[2]+"|"+"Automated"+"|"+"Yes"+"|"+testcaseName+
		 * "|"+"Make sure that Configuration part is run through Automation Suite and OEDS services are up and running"
		 * +"|"+ "1"+"|"+description+"|"+description+"\n"); throw new
		 * NullPointerException("demo");
		 */
		emailResultsAtTestLevel.put("TestName", testcaseName.split("_")[0].toUpperCase() + " :- " + description);
		emailResultsAtTestLevel.put("Result", "Pass");
		
	}

	private static void writeToFile(String fileName, String data) {
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			File file = new File(fileName);
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			bw.write(data);

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}
	}

	/**
	 * @author sbasana Description: Method to get the value that is stored in
	 *         the global variable
	 * @param variableName
	 * @return String
	 */
	public static synchronized String getData(String variableName) {
		String value = "";
		if (varData.containsKey(variableName))
			value = varData.get(variableName);
		return value;
	}

	/**
	 * @author sbasana Description: Method to get the testcase name that is
	 *         under execution
	 * @return String
	 */
	public synchronized String getTestCaseName() {
		return Thread.currentThread().getName();
	}

	/**
	 * @author sbasana Description: Method to post the request message to the
	 *         configured endpoint
	 * @param strToPost
	 * @return boolean
	 */

	private static HashMap<String, String> postSOAPMessage(String xml, String endPointUrl, String soapAction) {
		HashMap<String, String> output = new HashMap<String, String>();
		try {
			URL oURL = new URL(endPointUrl);
			HttpURLConnection con = (HttpURLConnection) oURL.openConnection();
			long startTime = Calendar.getInstance().getTime().getTime();
			TrustModifier.relaxHostChecking(con);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-type", "text/xml; charset=utf-8");
			con.setRequestProperty("SOAPAction", soapAction);
			con.setDoOutput(true);
			String response = "";
			OutputStream reqStream = con.getOutputStream();
			reqStream.write(xml.getBytes());
			int result = con.getResponseCode();
			long endTime = Calendar.getInstance().getTime().getTime();
			if (result == 200) {
				InputStream resStream = con.getInputStream();
				StringWriter responseWriter = new StringWriter();
				IOUtils.copy(resStream, responseWriter, "UTF-8");
				String reSXmlStr = responseWriter.getBuffer().toString().replaceAll("\n|\r", "");
				// System.out.println("Response Message: " + reSXmlStr);
				response = reSXmlStr;
			} else if (result == 302) {

				writeToReport(LogStatus.INFO, "Response status code that is received: " + result);
				writeToReport(LogStatus.INFO, "Url redirected to >> " + con.getHeaderField("Location"));
				output.put("redirectedUrl", con.getHeaderField("Location"));
			}

			else {
				InputStream resStream = con.getErrorStream();
				StringWriter responseWriter = new StringWriter();
				IOUtils.copy(resStream, responseWriter, "UTF-8");
				String reSXmlStr = responseWriter.getBuffer().toString().replaceAll("\n|\r", "");
				// System.out.println("Response Message: " + reSXmlStr);
				response = reSXmlStr;
			}
			output.put("responseCode", String.valueOf(result));
			output.put("responseMessage", response);
			OperationTime.insertTime(lastRunData.get("operationKey"), startTime, endTime - startTime, result);

			long timeInMilliseconds = endTime - startTime;
			long timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds);
			long timeInminutes = TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds);

			output.put("timeTaken_milliseconds", String.valueOf(timeInMilliseconds));
			output.put("timeTaken_seconds", String.valueOf(timeInSeconds));
			output.put("timeTaken_minutes", String.valueOf(timeInminutes));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return output;

	}

	/**
	 * @author sbasana Description: Method to post the request message to the
	 *         configured endpoint
	 * @param strToPost
	 * @return boolean
	 */

	private static HashMap<String, String> postXMLMessage(String xml, String method, String endPointUrl) {
		HashMap<String, String> output = new HashMap<String, String>();
		try {
			URL oURL = new URL(endPointUrl);
			HttpURLConnection con = (HttpURLConnection) oURL.openConnection();
			long startTime = Calendar.getInstance().getTime().getTime();
			TrustModifier.relaxHostChecking(con);
			con.setRequestMethod(method);
			con.setRequestProperty("Content-type", "text/xml; charset=utf-8");

			if (lastRunData.get("layer").equalsIgnoreCase("SSD")) {
				// CredentialSet format should be "username:password"
				String credentialSet = lastRunData.get("user") + ":" + lastRunData.get("pwd");
				String encoding = Base64.getEncoder().encodeToString((credentialSet).getBytes());
				con.setRequestProperty("Authorization", "Basic " + encoding);
			}

			con.setDoOutput(true);
			String response = "";
			OutputStream reqStream = con.getOutputStream();
			reqStream.write(xml.getBytes());
			int result = con.getResponseCode();
			long endTime = Calendar.getInstance().getTime().getTime();
			if (result == 200) {
				InputStream resStream = con.getInputStream();
				StringWriter responseWriter = new StringWriter();
				IOUtils.copy(resStream, responseWriter, "UTF-8");
				String reSXmlStr = responseWriter.getBuffer().toString().replaceAll("\n|\r", "");
				// System.out.println("Response Message: " + reSXmlStr);
				response = reSXmlStr;
			} else if (result == 302) {

				writeToReport(LogStatus.INFO, "Response status code that is received: " + result);
				writeToReport(LogStatus.INFO, "Url redirected to >> " + con.getHeaderField("Location"));
				output.put("redirectedUrl", con.getHeaderField("Location"));
			}

			else {
				InputStream resStream = con.getErrorStream();
				StringWriter responseWriter = new StringWriter();
				IOUtils.copy(resStream, responseWriter, "UTF-8");
				String reSXmlStr = responseWriter.getBuffer().toString().replaceAll("\n|\r", "");
				// System.out.println("Response Message: " + reSXmlStr);
				response = reSXmlStr;
			}
			output.put("responseCode", String.valueOf(result));
			output.put("responseMessage", response);
			OperationTime.insertTime(lastRunData.get("operationKey"), startTime, endTime - startTime, result);

			long timeInMilliseconds = endTime - startTime;
			long timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds);
			long timeInminutes = TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds);

			output.put("timeTaken_milliseconds", String.valueOf(timeInMilliseconds));
			output.put("timeTaken_seconds", String.valueOf(timeInSeconds));
			output.put("timeTaken_minutes", String.valueOf(timeInminutes));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return output;

	}

	public static synchronized void beforeClass(String[] groups) {

		testNGGroups = groups;
	}

	public static String processSOAPMessage(String strToPost, boolean... override) throws HttpException, IOException {
		String responseMsg = "";

		String endPointUrl = varData.get("{var_currentEndPointUrl}");
		String soapAction = configReader.getSOAPAction(lastRunData.get("operationKey"));

		try {

			if (override.length == 1) {
				HashMap<String, String> out = postSOAPMessage(strToPost, endPointUrl, soapAction);

				if (!out.get("responseCode").equals("200")) {
					if (out.get("responseCode").equals("500")) {
						// System.out.println("ResponseCode:" +
						// out.get("responseCode") + ". Reposting the message
						// again.");
						Thread.sleep(10000);
						out = postSOAPMessage(strToPost, endPointUrl, soapAction);
					} else if (out.get("responseCode").equals("302")) {
						out = postSOAPMessage(strToPost, out.get("redirectedUrl"), soapAction);
					}
				}
				
				responseMsg = out.get("responseMessage");
				/*String resPath = lastRunData.get("responsePath");
				FileWriter resWriter = new FileWriter(resPath);
				BufferedWriter bufWrite = new BufferedWriter(resWriter);
				bufWrite.write(responseMsg);
				bufWrite.flush();
				bufWrite.close();*/

			} else {
				writeToReport(LogStatus.INFO, "Endpoint >> " + endPointUrl);
				HashMap<String, String> out = postSOAPMessage(strToPost, endPointUrl, soapAction);

				if (!out.get("responseCode").equals("200")) {
					if (out.get("responseCode").equals("500")) {
						Thread.sleep(10000);
						out = postSOAPMessage(strToPost, endPointUrl, soapAction);
					} else if (out.get("responseCode").equals("302")) {
						out = postSOAPMessage(strToPost, out.get("redirectedUrl"), soapAction);
					}
				}

				responseMsg = out.get("responseMessage");
				/*String resPath = lastRunData.get("responsePath");
				FileWriter resWriter = new FileWriter(resPath);
				BufferedWriter bufWrite = new BufferedWriter(resWriter);
				bufWrite.write(responseMsg);
				bufWrite.flush();
				bufWrite.close();*/

				writeToReport(LogStatus.INFO, ReportFormatter.printReqResp(strToPost, responseMsg,
						lastRunData.get("operation"), out.get("responseCode"), lastRunData.get("operationKey")));
				long seconds = Long.parseLong(out.get("timeTaken_seconds"));
				long remainingMilliSeconds = (Long.parseLong(out.get("timeTaken_milliseconds"))) % 1000;
				if (seconds > 0)
					writeToReport(LogStatus.INFO, "Time Taken >> " + out.get("timeTaken_milliseconds")
							+ " Milliseconds (" + seconds + " Second(s) " + remainingMilliSeconds + " Milliseconds)");
				else
					writeToReport(LogStatus.INFO,
							"Time Taken >> " + out.get("timeTaken_milliseconds") + " Milliseconds ( < 1 Second )");
				writeToReport(LogStatus.INFO, "Response status code that is received: " + out.get("responseCode"));
			}
		} catch (Exception e) {
			writeToReport(LogStatus.FAIL, "Connection to " + endPointUrl + " is failed and cannot execute the "
					+ lastRunData.get("operation"));
		}

		return responseMsg;
	}

	public static String processXMLMessage(String strToPost, String method) throws HttpException, IOException {
		String responseMsg = "";
		String endPointUrl = varData.get("{var_currentEndPointUrl}");

		try {
			writeToReport(LogStatus.INFO, "Endpoint >> " + endPointUrl);
			HashMap<String, String> out = null;
			out = postXMLMessage(strToPost, method, endPointUrl);
			if (!out.get("responseCode").equals("200")) {
				if (out.get("responseCode").equals("500")) {
					Thread.sleep(10000);
					out = postXMLMessage(strToPost, method, endPointUrl);
				} else if (out.get("responseCode").equals("302")) {
					out = postXMLMessage(strToPost, method, out.get("redirectedUrl"));
				}
			}

			responseMsg = out.get("responseMessage");
			/*String resPath = lastRunData.get("responsePath");
			FileWriter resWriter = new FileWriter(resPath);
			BufferedWriter bufWrite = new BufferedWriter(resWriter);
			bufWrite.write(responseMsg);
			bufWrite.flush();
			bufWrite.close();*/

			writeToReport(LogStatus.INFO, ReportFormatter.printReqResp(strToPost, responseMsg,
					lastRunData.get("operation"), out.get("responseCode"), lastRunData.get("operationKey")));
			long seconds = Long.parseLong(out.get("timeTaken_seconds"));
			long remainingMilliSeconds = (Long.parseLong(out.get("timeTaken_milliseconds"))) % 1000;
			if (seconds > 0)
				writeToReport(LogStatus.INFO, "Time Taken >> " + out.get("timeTaken_milliseconds") + " Milliseconds ("
						+ seconds + " Second(s) " + remainingMilliSeconds + " Milliseconds)");
			else
				writeToReport(LogStatus.INFO,
						"Time Taken >> " + out.get("timeTaken_milliseconds") + " Milliseconds ( < 1 Second )");
			writeToReport(LogStatus.INFO, "Response status code that is received: " + out.get("responseCode"));

		} catch (Exception e) {
			writeToReport(LogStatus.FAIL, "Connection to " + endPointUrl + " is failed and cannot execute the "
					+ lastRunData.get("operation"));
		}
		return responseMsg;
	}

	/**
	 * @author supellak Description: Method to get Query by
	 *         OperationKeyWord,Query Set ID (QS_ID)
	 * @param querySetID
	 * @return String
	 */
	public static String getQuery(String operationKeyWord, String querySetID, boolean... printQuery) throws Exception {
		lastOperationKey = operationKeyWord;
		String query = "";
		HashMap<String, String> queries = new HashMap<String, String>();

		String dataSourcePath = configReader.getdataFilePath() + configReader.getModule(operationKeyWord).toLowerCase()
				+ "\\" + configReader.getService(operationKeyWord) + "\\" + configReader.getOperation(operationKeyWord)
				+ ".xlsx";
		System.out.println(dataSourcePath);
		queries = dsReader.getQuery(dataSourcePath, querySetID);
		query = queries.get("Query");
		System.out.println(query);
		if (queries.get("Variables") != null && !queries.get("Variables").isEmpty()) {
			String[] variables = queries.get("Variables").split(",");
			for (int i = 0; i < variables.length; i++) {
				if (query.contains(variables[i].trim())) {
					if (varData.containsKey(variables[i].trim())) {
						query = query.replace(variables[i].trim(), varData.get(variables[i].trim()));
					} else {
						Logs.logger.error("Global Data is not available for " + variables[i].trim());
						writeToReport(LogStatus.FAIL, "Global Data is not available for " + variables[i].trim());
					}
				}
			}
		}
		if (printQuery.length == 0) {
			writeToReport(LogStatus.INFO, "<b>Query: </b>" + query);
		}
		return query;
	}

	/*
	 * This method is to retrieve the given element values of all records.
	 * (Example, Multiple Address Records of a profile that is located at the
	 * given index on ProfileLookUp response) at a given index
	 *
	 * Important: xPath HashMap -> Key = ElementXPath Variable and Value =
	 * ParentXPath of the element i.e. the element that keeps repeating (profile
	 * node is an example) rootElementXPath and index -> The individual main
	 * record indicator (profile can be called as a record on ProfileLookUp
	 * response, room can be called as a record on fetchHotelRooms response)
	 *
	 */
	@SuppressWarnings("rawtypes")
	public static List<LinkedHashMap<String, String>> getElementValuesOfARecordMatchingWithGivenIndex(String xml,
			HashMap<String, String> xPath, XMLType xmlType, String rootElementXPath, int indexOfRootElement)
			throws Exception {
		List<LinkedHashMap<String, String>> nodeValues = new ArrayList<LinkedHashMap<String, String>>();
		HashMap<String, String> elementXPaths = new HashMap<String, String>();

		Iterator<?> it = xPath.entrySet().iterator();
		while (it.hasNext()) {
			String childElementXpath = "";
			String elementXpath = "";
			Map.Entry pair = (Map.Entry) it.next();
			String parentPath = "";
			String rootElementActualXPath = "";
			if (xmlType == XMLType.REQUEST) {
				parentPath = getRequestXPath(pair.getValue().toString());
				childElementXpath = getRequestXPath(pair.getKey().toString());
				rootElementActualXPath = getRequestXPath(rootElementXPath);

			} else if (xmlType == XMLType.RESPONSE) {
				parentPath = getResponseXPath(pair.getValue().toString());
				childElementXpath = getResponseXPath(pair.getKey().toString());
				rootElementActualXPath = getResponseXPath(rootElementXPath);
			}

			String tmpStrXpath = "";
			tmpStrXpath = parentPath.trim().replace(rootElementActualXPath.toString().trim(), "");
			tmpStrXpath = rootElementActualXPath + "[" + indexOfRootElement + "]" + tmpStrXpath;

			childElementXpath = childElementXpath.trim().replace(parentPath.toString().trim(), "");
			elementXpath = elementXpath + ";" + childElementXpath;
			elementXpath = elementXpath.substring(1);
			elementXPaths.put(elementXpath, tmpStrXpath);
		}

		Iterator<?> itr = elementXPaths.entrySet().iterator();
		Map.Entry pair = (Map.Entry) itr.next();
		NodeList nodeList = getNodeListOfAnXPathinXML(pair.getValue().toString(), xml);
		if (nodeList.getLength() > 0) {
			for (int i = 1; i <= nodeList.getLength(); i++) {
				LinkedHashMap<String, String> nodeLists = new LinkedHashMap<String, String>();
				itr = elementXPaths.entrySet().iterator();
				while (itr.hasNext()) {
					pair = (Map.Entry) itr.next();
					NodeList tmpNodeList;
					if (pair.getKey().toString() == "") {
						tmpNodeList = getNodeListOfAnXPathinXML((pair.getValue().toString() + "[" + i + "]"), xml);
						if (tmpNodeList.getLength() > 0) {
							Node node = tmpNodeList.item(0);
							nodeLists.put(node.getNodeName(), node.getTextContent());
						} else {
							nodeLists.put("*null*", "*null*");
						}
					} else {
						for (int j = 1; j <= 5; j++) {
							String elementValue = "";
							String str = pair.getKey().toString().replace(pair.getValue().toString(), "");

							if (str.indexOf("/@") == 0)
								str = str.replace("@", "[" + j + "]@");
							else
								str = str + "[" + j + "]";

							String tmpElementXpath = pair.getValue().toString() + "[" + i + "]" + str;
							// tmpElementXpath = tmpElementXpath.replace("@",
							// "/@");
							elementValue = getElementValue(xml, tmpElementXpath, xmlType);
							if (elementValue == "" || elementValue.contains("doesn't exist")) {
								elementValue = "*null*";
							}

							if (!elementValue.equalsIgnoreCase("*null*")) {

								str = str.replace("@", "");
								// elementValue = elementValue.substring(1);

								if (str.equals("[" + Integer.toString(j) + "]")) {
									String splitted[] = pair.getValue().toString().split("/");
									str = splitted[splitted.length - 1];
									str = str + Integer.toString(j);

								}

								if (str.contains(Integer.toString(j))) {
									str = str.replace("[", "");
									str = str.replace("]", "");
									str = str.replace(Integer.toString(j), "");
									str = str + Integer.toString(j);
								}

								str = str.replace("/", "");
								if (str.length() > 30) {
									str = str.substring(str.length() - 30, str.length());
								}
								nodeLists.put(str, elementValue);
							}
						}
					}
				}
				if (nodeLists.size() > 0) {
					nodeValues.add(nodeLists);
				}
			}
		}
		return nodeValues;
	}

	/*
	 * Method to get element value located at the given XPath with two level
	 * indexes parentXpath Parameter -> This has to be the parent XPath of the
	 * actual element that is to be read.
	 *
	 * Example: <example> <profiles> <profile> <profileId>5665675</profileId>
	 * <profileAddress1>Address 1</profileAddress1> <profileAddress2>Address
	 * 2</profileAddress1> </profile> <profile> <profileId>5678568</profileId>
	 * <profileAddress1>Address 1</profileAddress1> <profileAddress2>Address
	 * 2</profileAddress1> </profile> </profiles> <example>
	 *
	 * In the above example, to get the index of the profileaddress1 of the
	 * profile two, parentXPath1, index1 belongs to the profile tag and
	 * parentXPath2, index2 belongs to the address has to be passed as
	 * example/profiles/ and XPath should be example/profiles/profile/profileId
	 *
	 * Note that the XPath variables have to be passed instead of the actual
	 * XPaths
	 */
	public static String getElementValueByTwoLevelIndex(String xml, String parentXPath1, int index1,
			String parentXPath2, int index2, String xPath, XMLType xmlType) throws Exception {
		String value = "", elementXpath = "", parentElementXpath1 = "", parentElementXpath2 = "",
				parentElementXpath = "", childElementXpath = "", parentElementXpathTemp = "";
		org.w3c.dom.Document doc;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		if (xmlType == XMLType.REQUEST) {
			childElementXpath = getRequestXPath(xPath);
			parentElementXpath1 = getRequestXPath(parentXPath1);
			parentElementXpath2 = getRequestXPath(parentXPath2);
		} else if (xmlType == XMLType.RESPONSE) {
			childElementXpath = getResponseXPath(xPath);
			parentElementXpath1 = getResponseXPath(parentXPath1);
			parentElementXpath2 = getResponseXPath(parentXPath2);
		}

		parentElementXpathTemp = parentElementXpath2.trim().replace(parentElementXpath1.toString().trim(), "");
		parentElementXpath = parentElementXpath1 + "[" + index1 + "]" + parentElementXpathTemp;
		childElementXpath = childElementXpath.trim().replace(parentElementXpath2.toString().trim(), "");
		elementXpath = parentElementXpath + "[" + index2 + "]" + childElementXpath;

		if (elementXpath.contains("@"))
			elementXpath = elementXpath.replace("@", "/@");
		InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
		doc = docBuilder.parse(inputStream);

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile(elementXpath);
		Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
		if (node == null)
			value = elementXpath + " doesn't exist";
		else if (node.getTextContent().equals(""))
			value = "";
		else
			value = node.getTextContent();

		return value;
	}

	/**
	 * @author rnagasun Description:Method to get element value located at a
	 *         particular index of an xpath. HashMap should contain the xpaths
	 *         from parent node to child nodes with their respective node no's
	 *         to be appended. Ex:Hashmap should be like
	 *         ["parentXpath"="nodeNo","childXpath"="nodeNo","child2Xpath"=
	 *         "nodeNo"] The final xpath is like
	 *         parentXpath+[nodeNo]+(childXpath-parentXpath)+[nodeNo]+(
	 *         child2Xpath-childXpath)+[nodeNo]
	 * @param xpathValues
	 * @return String
	 * @throws Exception
	 */
	public static String getElementValueByIndex(String xml, LinkedHashMap<String, Integer> xpathValues, XMLType xmlType)
			throws Exception {
		String value = "", elementXpath = "", parentElementXpath = "", childXpath = "", tempXpath = "";
		org.w3c.dom.Document doc;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		String key;
		Integer nodeCount;

		Set<String> keys = xpathValues.keySet();
		Iterator<String> keysIterator = keys.iterator();

		while (keysIterator.hasNext()) {
			key = keysIterator.next();
			nodeCount = xpathValues.get(key);
			if (xmlType == XMLType.REQUEST) {
				childXpath = getRequestXPath(key);
			} else if (xmlType == XMLType.RESPONSE) {
				childXpath = getResponseXPath(key);
			}
			tempXpath = childXpath.trim().replace(parentElementXpath.toString().trim(), "");
			elementXpath = elementXpath + tempXpath + "[" + nodeCount + "]";
			parentElementXpath = childXpath;

		}

		if (elementXpath.contains("@"))
			elementXpath = elementXpath.replace("@", "/@");
		System.out.println(elementXpath);
		InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
		doc = docBuilder.parse(inputStream);

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile(elementXpath);
		Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
		if (node == null)
			value = elementXpath + " doesn't exist";
		else if (node.getTextContent().equals(""))
			value = "*null*";
		else
			value = node.getTextContent();

		return value;
	}

	public static String getElementValueByIndex(String xml, String parentXPath, String xPath, int index,
			XMLType xmlType) throws Exception {
		String value = "", elementXpath = "", parentElementXpath = "", childElementXpath = "";
		org.w3c.dom.Document doc;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		if (xmlType == XMLType.REQUEST) {
			childElementXpath = getRequestXPath(xPath);
		} else if (xmlType == XMLType.RESPONSE) {
			childElementXpath = getResponseXPath(xPath);
		}

		if (xmlType == XMLType.REQUEST) {
			parentElementXpath = getRequestXPath(parentXPath);
		} else if (xmlType == XMLType.RESPONSE) {
			parentElementXpath = getResponseXPath(parentXPath);
		}

		childElementXpath = childElementXpath.trim().replace(parentElementXpath.toString().trim(), "");
		elementXpath = parentElementXpath + "[" + index + "]" + childElementXpath;

		if (elementXpath.contains("@"))
			elementXpath = elementXpath.replace("@", "/@");
		InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
		doc = docBuilder.parse(inputStream);

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile(elementXpath);
		Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
		if (node == null)
			value = elementXpath + " doesn't exist";
		else if (node.getTextContent().equals(""))
			value = "";
		else
			value = node.getTextContent();

		return value;
	}

	/**
	 * @author sbasana Description: Method to get the value located at a given
	 *         XPath on the response message xmlString parameter is optional, if
	 *         it's empty, the value will be taken from the response file stored
	 *         in global variable else it will be taken from the xmlString
	 * @param elementXpath,xmlString
	 * @return String
	 * @throws Exception
	 */
	public static String getElementValue(String xml, String elementXpath, XMLType xmlType) throws Exception {

		String value = "";
		org.w3c.dom.Document doc;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		int flag = 0;
		if (elementXpath != "") {
			if (elementXpath.contains("/"))
				flag = 1;
			if (flag == 0) {
				if (xmlType == XMLType.REQUEST) {
					elementXpath = getRequestXPath(elementXpath);
				} else if (xmlType == XMLType.RESPONSE) {
					elementXpath = getResponseXPath(elementXpath);
				}
			}
			if (elementXpath.contains("@"))
				elementXpath = elementXpath.replace("@", "/@");
			InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
			doc = docBuilder.parse(inputStream);

			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile(elementXpath);
			Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
			if (node == null)
				value = elementXpath + " doesn't exist";
			else if (node.getTextContent().equals(""))
				value = "*null*";
			else
				value = node.getTextContent();
		}

		return value;

	}

	/**
	 * @author sbasana Description: Method to get the index of a node where the
	 *         given values matches
	 * @param identifier,parentPath,elementXpath
	 * @param int
	 * @throws Exception
	 */
	public synchronized static int getNodeIndex(String xml, String identifier, String parentPath, String elementXpath,
			XMLType xmlType) throws Exception {
		int index = 0;
		List<String> rootNodeValues = new ArrayList<String>();
		if (xmlType == XMLType.REQUEST) {
			parentPath = getRequestXPath(parentPath);
			elementXpath = getRequestXPath(elementXpath);

		} else if (xmlType == XMLType.RESPONSE) {
			parentPath = getResponseXPath(parentPath);
			elementXpath = getResponseXPath(elementXpath);

		}
		rootNodeValues = getNodeList(xml, parentPath, elementXpath, xmlType);
		if (rootNodeValues.size() > 0) {
			for (int i = 0; i < rootNodeValues.size(); i++) {
				if (identifier.equalsIgnoreCase(rootNodeValues.get(i))) {
					index = i + 1;
					break;
				}
			}
		}
		return index;
	}

	/**
	 * @author sbasana Description: Method to get the index of a node at the
	 *         second level where the given values matches
	 * @param identifier,parentPath,elementXpath
	 * @param int
	 * @throws Exception
	 */
	public synchronized static int getSecondLevelNodeIndex(String xml, String identifier, String parentXPath1,
			int index1, String parentXPath2ForWhichNodeIndexToBeFound, String xPath, XMLType xmlType) throws Exception {
		int index = 0;
		List<String> rootNodeValues = new ArrayList<String>();
		String value = "", elementXpath = "", parentElementXpath1 = "", parentElementXpath2 = "",
				parentElementXpath = "", childElementXpath = "", parentElementXpathTemp = "";

		if (xmlType == XMLType.REQUEST) {
			childElementXpath = getRequestXPath(xPath);
			parentElementXpath1 = getRequestXPath(parentXPath1);
			parentElementXpath2 = getRequestXPath(parentXPath2ForWhichNodeIndexToBeFound);
		} else if (xmlType == XMLType.RESPONSE) {
			childElementXpath = getResponseXPath(xPath);
			parentElementXpath1 = getResponseXPath(parentXPath1);
			parentElementXpath2 = getResponseXPath(parentXPath2ForWhichNodeIndexToBeFound);
		}

		parentElementXpathTemp = parentElementXpath2.trim().replace(parentElementXpath1.toString().trim(), "");
		parentElementXpath = parentElementXpath1 + "[" + index1 + "]" + parentElementXpathTemp;
		childElementXpath = childElementXpath.trim().replace(parentElementXpath2.toString().trim(), "");
		elementXpath = parentElementXpath + childElementXpath;

		rootNodeValues = getNodeList(xml, parentElementXpath, elementXpath, xmlType);
		if (rootNodeValues.size() > 0) {
			for (int i = 0; i < rootNodeValues.size(); i++) {
				if (identifier.equalsIgnoreCase(rootNodeValues.get(i))) {
					index = i + 1;
					break;
				}
			}
		}
		return index;
	}

	/**
	 * @author sbasana Description: Method to generate data
	 * @param keyword
	 * @param String
	 * @throws Exception
	 */
	public synchronized static String getKeywordData(String keyword) throws Exception {
		return inpObj.getDynamicData(keyword);
	}

	/**
	 * @author supellak,amustoor Description: Method to generate the node list
	 *         for the given element Xpath .
	 * @param XML
	 * @param elementXpath
	 * @param xmlType
	 * @throws Exception
	 */

	public static NodeList getMultipleNodes(String xml, String elementXpath, XMLType xmlType) throws Exception {
		NodeList out = null;
		org.w3c.dom.Document doc;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		int flag = 0;
		if (elementXpath != "") {
			if (elementXpath.contains("/"))
				flag = 1;
			if (flag == 0) {

				if (xmlType == XMLType.RESPONSE)
					elementXpath = getResponseXPath(elementXpath);
				else if (xmlType == XMLType.REQUEST)
					elementXpath = getRequestXPath(elementXpath);
			}
			if (elementXpath.contains("@"))
				elementXpath = elementXpath.replace("@", "/@");

			InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
			doc = docBuilder.parse(inputStream);

			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile(elementXpath);

			out = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		}

		return out;
	}

	/**
	 * @author supellak,amustoor Description: Method to generate the element
	 *         value at the given element Xpath depending on the attribute
	 *         value.
	 * @param XML
	 * @param elementXpath
	 * @param attributeXpath
	 * @param attributeValue
	 * @param xmlType
	 * @throws Exception
	 */

	public synchronized static String getElementValueByAttribute(String xml, String elementXpath, String attributeXpath,
			String attributeValue, XMLType xmlType) throws Exception {
		String elementX = "";
		if (xmlType == XMLType.REQUEST) {

			elementX = getRequestXPath(elementXpath);

		} else if (xmlType == XMLType.RESPONSE) {

			elementX = getResponseXPath(elementXpath);

		}

		NodeList elementNodeList = getMultipleNodes(xml, elementXpath, xmlType);
		if (attributeValue.equals("")) {
			String attributeName = attributeXpath.substring(attributeXpath.lastIndexOf("_") + 1);
			for (int i = 0; i < elementNodeList.getLength(); i++) {
				Element e = (Element) elementNodeList.item(i);
				if (e.getAttribute(attributeName).equals("")) {
					if (e.getTextContent().equals(""))
						return "*null*";
					else
						return e.getTextContent();
				}

			}
		}

		NodeList attributeNodeList = getMultipleNodes(xml, attributeXpath, xmlType);

		for (int i = 0; i < attributeNodeList.getLength(); i++) {
			if (attributeNodeList.item(i).getTextContent().equals(attributeValue)) {
				if (elementNodeList.item(i).getTextContent().equals(""))
					return "*null*";
				else
					return elementNodeList.item(i).getTextContent();
			}
			// elementNodeList.item(i).getTextContent();
		}

		return elementX + " doesn't exist";

	}

	public String generateData(String keyword) throws Exception {
		return inpObj.getDynamicData(keyword);
	}

	/* Method to return database connection */
	public static Connection getConnection() {
		return dbConnection;
	}

	public static void writeToReport(LogStatus status, String message) {
		if (status == LogStatus.FAIL || status == LogStatus.WARNING || status == LogStatus.ERROR) {
			result = 0;
			writeToEmail(status);
		}
		message = ReportFormatter.formatMessage(status, message);
		logger.log(status, message);

		if ((status == LogStatus.FAIL || status == LogStatus.WARNING) && !serverLogsFlag) {
			ServerLogs();
		}

	}

	public static void writeToReport(LogStatus status, String heading, List<String> elements) {
		logger.log(status, heading, elements);
		if ((status == LogStatus.FAIL || status == LogStatus.WARNING) && !serverLogsFlag) {
			ServerLogs();
			writeToEmail(status);
		}
	}
	
	

	private static void writeToEmail(LogStatus status) {
		String strStatus = null;
		
		if (status == LogStatus.FAIL) {
			strStatus = "Fail";
		} else if (status == LogStatus.WARNING) {
			strStatus = "Blocked";
		} else if (status == LogStatus.ERROR){
			strStatus = "Error";
		}
		
		if (emailResultsAtTestLevel.get("Result") == "Pass") {
			emailResultsAtTestLevel.put("Result", strStatus);
		}
	}

	/**** Method to add text to the log.out file ***/
	public static void writeToLog(String text) {
		Logs.logger.info(text);
	}

	public synchronized static String getElementValueByAttribute(String xml, String elementXpath, String attributeValue,
			XMLType xmlType) throws Exception {

		String elementX = "";
		if (xmlType == XMLType.REQUEST) {

			elementX = getRequestXPath(elementXpath);

		} else if (xmlType == XMLType.RESPONSE) {

			elementX = getResponseXPath(elementXpath);

		}

		NodeList elementNodeList = getMultipleNodes(xml, elementXpath, xmlType);

		for (int i = 0; i < elementNodeList.getLength(); i++) {
			if (elementNodeList.item(i).hasAttributes()) {
				NamedNodeMap nnm = elementNodeList.item(i).getAttributes();
				for (int j = 0; j < nnm.getLength(); j++) {
					Node attrNode = nnm.item(j);
					if (attrNode.getTextContent().equals(attributeValue)) {
						if (elementNodeList.item(i).getTextContent().equals(""))
							return "*null*";
						else
							return elementNodeList.item(i).getTextContent();
					}
				}

			}

		}

		return elementX + " doesn't exist";

	}

	public synchronized static String getAttributeValueByAttribute(String xml, String elementXpath,
			String attributeName, String attributeValue, XMLType xmlType) throws Exception {

		String elementX = "";
		if (xmlType == XMLType.REQUEST) {

			elementX = getRequestXPath(elementXpath);

		} else if (xmlType == XMLType.RESPONSE) {

			elementX = getResponseXPath(elementXpath);

		}
		NodeList elementNodeList = getMultipleNodes(xml, elementXpath, xmlType);

		for (int i = 0; i < elementNodeList.getLength(); i++) {
			if (elementNodeList.item(i).hasAttributes()) {
				NamedNodeMap nnm = elementNodeList.item(i).getAttributes();
				for (int j = 0; j < nnm.getLength(); j++) {
					Node attrNode = nnm.item(j);
					if (attrNode.getTextContent().equals(attributeValue)) {
						Element e = (Element) elementNodeList.item(i);
						if (!e.getAttribute(attributeName).equals(""))
							return e.getAttribute(attributeName);
						else
							return "*null*";
					}
				}

			}

		}

		return elementX + "@" + attributeName + " doesn't exist";

	}

	public static void main(String args[]) {

	}

	public static void ServerLogs() {
		serverLogsFlag = true;
		Thread wsThread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (configReader.getWSLogFlag().equalsIgnoreCase("Y")) {
					wsLogFlag = true;
					wsLogslist = getLogs(configReader.getWSLogsHost(), configReader.getWSLogsHostUsername(),
							configReader.getWSLogsHostPassword(), configReader.getWSLogsHostLineCount(),
							configReader.getWSLogsHostFilePath());
				}

			}
		});

		Thread sbThread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (configReader.getSBLogFlag().equalsIgnoreCase("Y")) {
					sbLogFlag = true;
					sbLogslist = getLogs(configReader.getSBLogsHost(), configReader.getSBLogsHostUsername(),
							configReader.getSBLogsHostPassword(), configReader.getSBLogsHostLineCount(),
							configReader.getSBLogsHostFilePath());
				}
			}
		});
		wsThread.start();
		sbThread.start();

		while (true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!(wsThread.isAlive() || sbThread.isAlive())) {
				break;
			}
		}

	}

	private static List<String> getLogs(String host, String user, String password, int lines, String path) {

		String log = "";

		String command1 = "tail -" + lines + " " + path;
		try {
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig(config);
			session.connect();

			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command1);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];

			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					log = log + new String(tmp, 0, i);
				}
				if (channel.isClosed()) {
					writeToLog("exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
			writeToLog("DONE");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ReportFormatter.formatErrorLogs(log);

	}

}
