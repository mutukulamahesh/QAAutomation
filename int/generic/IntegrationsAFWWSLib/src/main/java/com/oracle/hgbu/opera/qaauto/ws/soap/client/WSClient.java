package com.oracle.hgbu.opera.qaauto.ws.soap.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.httpclient.HttpException;

import com.oracle.hgbu.opera.qaauto.ws.common.utils.DatabaseUtil;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.WSLib;
import com.relevantcodes.customextentreports.LogStatus;

public class WSClient {
	public enum XMLType {
		REQUEST, RESPONSE;

		@Override
		public String toString() {
			switch (this) {
			case REQUEST:
				return "REQUEST";
			case RESPONSE:
				return "RESPONSE";
			default:
				return "RESPONSE";
			}
		}
	}

	public static LinkedHashMap<String, String> getSingleNodeList(String xml, HashMap<String, String> xPath,
			boolean readMap, XMLType xmlType) throws Exception {
		return WSLib.getSingleNodeList(xml, xPath, readMap, xmlType);
	}

	public static List<LinkedHashMap<String, String>> getMultipleNodeList(String xml, HashMap<String, String> xPath,
			boolean readMap, XMLType xmlType) throws Exception {
		return WSLib.getMultipleNodeList(xml, xPath, readMap, xmlType);
	}

	public static String getQuery(String operationKeyWord, String querySetID, boolean...printOnReport) throws Exception {
		return WSLib.getQuery(operationKeyWord, querySetID, printOnReport);
	}

	public static String getQuery(String querySetID) throws Exception {
		return WSLib.getQuery(querySetID);
	}

	public static String getRequestXPath(String xPathVariable) throws Exception {
		return WSLib.getRequestXPath(xPathVariable);
	}

	public static String getResponseXPath(String xPathVariable) throws Exception {
		return WSLib.getResponseXPath(xPathVariable);
	}

	public static void setData(String variableName, String value) {
		WSLib.setData(variableName, value);
	}

	public static String createXMLMessage(String operationKey, String dataSetID, boolean... override) throws Exception {
		return WSLib.createXMLMessage(operationKey, dataSetID, override);
	}
	
	public static String createSOAPMessage(String operationKey, String dataSetID,boolean ...override) throws Exception {
		return WSLib.createSOAPMessage(operationKey, dataSetID,override);
	}

	public static String processSOAPMessage(String strToPost,boolean ...override) throws HttpException, IOException {
		return WSLib.processSOAPMessage(strToPost,override);
	}

	public static String processXMLMessage(String strToPost, String method) throws HttpException, IOException {
		return WSLib.processXMLMessage(strToPost, method);
	}
	public synchronized static LinkedHashMap<String, String> getDBRow(String querySetID) throws Exception {
		return DatabaseUtil.getDBRow(querySetID);
	}

	public synchronized static ArrayList<LinkedHashMap<String, String>> getDBRows(String query) throws Exception {
		return DatabaseUtil.getDBRows(query);
	}

	public static synchronized String getData(String variableName) {
		return WSLib.getData(variableName);
	}

	public static String getElementValue(String xml, String elementXpath, XMLType xmlType) throws Exception {
		return WSLib.getElementValue(xml, elementXpath, xmlType);
	}

	public static String getElementValueByAttribute(String xml, String elementXpath, String attributeXpath,
			String attributeValue, XMLType xmlType) throws Exception {
		return WSLib.getElementValueByAttribute(xml, elementXpath, attributeXpath, attributeValue, xmlType);
	}

	public synchronized static String getKeywordData(String keyword) throws Exception {
		return WSLib.getKeywordData(keyword);
	}

	public synchronized static void startTest(String testcaseName, String description, String category) {
		WSLib.startTest(testcaseName, description, category);
	}

	public static void writeToReport(LogStatus status, String message) {

		WSLib.writeToReport(status, message);
	}

	public static void writeToReport(LogStatus status, String heading, List<String> elements) {
		WSLib.writeToReport(status, heading, elements);
	}

	public static void writeToLog(String message) {
		WSLib.writeToLog(message);
	}

	public static String getElementValueByIndex(String xml, LinkedHashMap<String, Integer> xpathValues, XMLType xmlType)
			throws Exception {
		return WSLib.getElementValueByIndex(xml, xpathValues, xmlType);
	}

	public static int getNodeIndex(String xml, String identifier, String parentPath, String elementXpath,
			XMLType xmlType) throws Exception {
		return WSLib.getNodeIndex(xml, identifier, parentPath, elementXpath, xmlType);
	}

	public static String getElementValueByAttribute(String xml, String elementXpath, String attributeValue,
			XMLType xmlType) throws Exception {
		return WSLib.getElementValueByAttribute(xml, elementXpath, attributeValue, xmlType);
	}

	public static String getAttributeValueByAttribute(String xml, String elementXpath, String attributeName,
			String attributeValue, XMLType xmlType) throws Exception {
		return WSLib.getAttributeValueByAttribute(xml, elementXpath, attributeName, attributeValue, xmlType);
	}

	public static LinkedHashMap<String, String> getDBRowWithNulls(String query) throws Exception {
		return DatabaseUtil.getDBRowWithNulls(query);
	}

	public static void setResortEntry(String resortEntry) {
		WSLib.setResortEntry(resortEntry);
	}

	public static List<LinkedHashMap<String, String>> getElementValuesOfARecordMatchingWithGivenIndex(String xml,
			HashMap<String, String> xPath, XMLType xmlType, String rootElementXPath, int indexOfRootElement) throws Exception {
		return WSLib.getElementValuesOfARecordMatchingWithGivenIndex(xml, xPath, xmlType, rootElementXPath, indexOfRootElement);
	}

	public synchronized static int getSecondLevelNodeIndex(String xml, String identifier, String parentXPath1, int index1, String parentXPath2, String xPath,
			XMLType xmlType) throws Exception {
		return WSLib.getSecondLevelNodeIndex(xml, identifier, parentXPath1, index1, parentXPath2, xPath,xmlType);
	}
}
