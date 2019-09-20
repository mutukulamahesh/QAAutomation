package com.oracle.hgbu.opera.qaauto.ws.wrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.httpclient.HttpException;

import com.oracle.hgbu.opera.qaauto.ws.common.DatabaseUtil;
import com.oracle.hgbu.opera.qaauto.ws.custom.WSLib;
import com.relevantcodes.customextentreports.LogStatus;

public class WSClient {

	/*
	 * This is a Wrapper class that contains some of the methods that are to be
	 * exposed to the test classes for interacting with the XML payloads
	 */

	public enum XMLType {
		REQUEST, RESPONSE, OPERATION_KEY;

		@Override
		public String toString() {
			switch (this) {
			case REQUEST:
				return "REQUEST";
			case RESPONSE:
				return "RESPONSE";
			case OPERATION_KEY:
				return "OPERATION_KEY";
			default:
				return "RESPONSE";
			}
		}
	}

	public static void setResortEntry(String resortEntry) {
		WSLib.setResortEntry(resortEntry);
	}

	public static LinkedHashMap<String, String> getElementValuesOfSingleRecordByIndex(String xml,
			HashMap<String, String> xPath, int index, XMLType xmlType, String operationKeyword) throws Exception {
		return getElementValuesOfSingleRecordByIndex(xml,xPath, index, xmlType, operationKeyword);
	}

	public static LinkedHashMap<String, String> getElementValuesOfSingleRecord(String xml,
			HashMap<String, String> xPath, XMLType xmlType, String operationKeyword) throws Exception {
		return WSLib.getElementValuesOfSingleRecord(xml, xPath, xmlType, operationKeyword);
	}

	public static List<LinkedHashMap<String, String>> getElementValuesOfMultipleRecords(String xml,
			HashMap<String, String> xPath,XMLType xmlType, String operationKeyword) throws Exception {
		return WSLib.getElementValuesOfMultipleRecords(xml, xPath, xmlType, operationKeyword);
	}

	public static List<LinkedHashMap<String, String>> getElementValuesOfARecordMatchingWithGivenIndex(String xml,
			HashMap<String, String> xPath, XMLType xmlType, String operationKeyword, String rootElementXPath, int indexOfRootElement) throws Exception {
		return WSLib.getElementValuesOfARecordMatchingWithGivenIndex(xml, xPath, xmlType, operationKeyword, rootElementXPath, indexOfRootElement);
	}

	public static String getQuery(String operationKeyWord, String querySetID) throws Exception {
		return WSLib.getQuery(operationKeyWord, querySetID);
	}

	public static String getQuery(String querySetID) throws Exception {
		return WSLib.getQuery(querySetID);
	}

	public static String getRequestXPath(String xPathVariable, String operationKeyword) throws Exception {
		return WSLib.getRequestXPath(xPathVariable, operationKeyword);
	}

	public static String getResponseXPath(String xPathVariable, String operationKeyword) throws Exception {
		return WSLib.getResponseXPath(xPathVariable, operationKeyword);
	}

	public static void setGlobalData(String variableName, String value) {
		WSLib.setData(variableName, value);
	}

	public static String createSOAPMessage(String operationKey, String dataSetID, boolean... override)
			throws Exception {
		return WSLib.createSOAPMessage(operationKey, dataSetID, override);
	}

	public static String processSOAPMessage(String strToPost, boolean... override) throws HttpException, IOException {
		return WSLib.processSOAPMessage(strToPost, override);
	}

	public synchronized static LinkedHashMap<String, String> getDBRow(String querySetID) throws Exception {
		return DatabaseUtil.getDBRow(querySetID);
	}

	public synchronized static ArrayList<LinkedHashMap<String, String>> getDBRows(String query) throws Exception {
		return DatabaseUtil.getDBRows(query);
	}

	public static synchronized String getGlobalData(String variableName) {
		return WSLib.getData(variableName);
	}

	public static String getElementValue(String xml, String elementXpath, XMLType xmlType, String operationKeyword)
			throws Exception {
		return WSLib.getElementValue(xml, elementXpath, xmlType, operationKeyword);
	}

	public static String getElementValueByAttribute(String xml, String elementXpath, String attributeXpath,
			String attributeValue, XMLType xmlType, String operationKeyword) throws Exception {
		return WSLib.getElementValueByAttribute(xml, elementXpath, attributeXpath, attributeValue, xmlType,
				operationKeyword);
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

	public static String getElementValueByIndex(String xml, String parentXPath, String xPath, int index, XMLType xmlType,String operationKeyword) throws Exception {
		return WSLib.getElementValueByIndex(xml, parentXPath, xPath, index, xmlType, operationKeyword);
	}

	public static int getNodeIndex(String xml, String identifier, String parentPath, String elementXpath,
			XMLType xmlType, String operationKeyword) throws Exception {
		return WSLib.getNodeIndex(xml, identifier, parentPath, elementXpath, xmlType, operationKeyword);
	}

	/*
	 * public static String getElementValueByAttribute(String xml, String
	 * elementXpath, String attributeValue, XMLType xmlType,String
	 * operationKeyword) throws Exception { return
	 * WSLib.getElementValueByAttribute(xml, elementXpath, attributeValue,
	 * xmlType,operationKeyword); }
	 */

	public static String getAttributeValueByAttribute(String xml, String elementXpath,
			String attributeNameForWhichValueIsToBeReturned, String attributeValueThatIsToBeCompared, XMLType xmlType,
			String operationKeyword) throws Exception {
		return WSLib.getAttributeValueByAttribute(xml, elementXpath, attributeNameForWhichValueIsToBeReturned,
				attributeValueThatIsToBeCompared, xmlType, operationKeyword);
	}

	public static void setHeaderParameters(String uname) {
		try {
			WSLib.setData("{var_userName}", uname);
		} catch (Exception e) {
			System.out.println("Exception occurred while setting up the security header");
			e.printStackTrace();
		}
	}
}
