package com.oracle.hgbu.opera.qaauto.ws.wrapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;

import com.oracle.hgbu.opera.qaauto.ws.common.Logs;
import com.oracle.hgbu.opera.qaauto.ws.custom.WSLib;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient.XMLType;
import com.relevantcodes.customextentreports.LogStatus;

public class WSAssert extends WSLib {

	/*
	 * Method to verify if the given element exists on the response message
	 */
	public static boolean assertIfElementExists(String xml, String elementXpath, boolean overrideLog,
			String operationKeyword, boolean... args) throws Exception {
		boolean check = false;
		Logs.logger.info("Inside Assert if elementy exists");
		if (elementXpath != "") {
			elementXpath = WSLib.getResponseXPath(elementXpath, operationKeyword);
			Logs.logger.info("Element XPath: " + elementXpath);
			elementXpath = elementXpath.replace("@", "/@");
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Logs.logger.info(xml);
			InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
			org.w3c.dom.Document doc = docBuilder.parse(inputStream);
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile(elementXpath);
			Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
			if (node != null) {
				check = true;
				if (overrideLog == false) {

					if (args.length != 1) {
						writeToReport(LogStatus.PASS, elementXpath + " exists on the response message");
					}
				}
			} else {
				check = false;
				if (overrideLog == false) {
					if (args.length != 1) {
						writeToReport(LogStatus.FAIL, elementXpath + " doesn't exist on the response message");
					}
				}
			}
		}
		return check;
	}

	/*
	 * Method to verify if the given element is not present on the response
	 * message (This is used for negative validation)
	 */
	public static boolean assertIfElementDoesNotExist(String xml, String elementXpath, boolean overrideLog,
			String operationKeyword) throws Exception {
		boolean check = false;
		check = assertIfElementExists(xml, elementXpath, overrideLog, operationKeyword);
		if (overrideLog == false) {
			if (check)
				writeToReport(LogStatus.FAIL, elementXpath + " exists on the response message");
			else
				writeToReport(LogStatus.PASS, elementXpath + " doesn't exist on the response message");
		}
		return check;
	}

	/*
	 * Method to verify if the value located on the response message under the
	 * given element equals to the expected value
	 */
	public static boolean assertIfElementValueEquals(String xml, String elementXpath, String expectedValue,
			boolean overrideLog, String operationKeyword, boolean... args) throws Exception {
		Boolean matchFlag = false;
		String actualValue = "";
		// manage Index
		String actualElementPath = getResponseXPath(elementXpath, operationKeyword);
		String elementTag = actualElementPath.substring(actualElementPath.lastIndexOf('/') + 1,
				actualElementPath.length());
		if (assertIfElementExists(xml, elementXpath, false, operationKeyword)) {
			actualValue = getElementValue(xml, elementXpath, XMLType.RESPONSE, operationKeyword);
			if (actualValue.equalsIgnoreCase((expectedValue))) {
				matchFlag = true;
			}

			if (args.length == 1) {

			} else {
				if (overrideLog == false) {
					if (matchFlag == true) {
						writeToReport(LogStatus.PASS,
								elementTag + " -> " + "Expected :" + expectedValue + " Actual :" + actualValue);
					} else {
						writeToReport(LogStatus.FAIL,
								elementTag + " -> " + "Expected :" + expectedValue + " Actual :" + actualValue);
					}
				}

				else {
					if (overrideLog == false) {
						if (expectedValue != null) {
							writeToReport(LogStatus.FAIL, "Element " + elementTag + " is not found on the response");
							writeToReport(LogStatus.INFO, elementTag + " -> " + "Expected :" + expectedValue
									+ " Actual : not found on response");
						} else {
							writeToReport(LogStatus.INFO,
									elementTag + " -> " + "Expected :" + expectedValue + " Actual : null");
						}
					}
				}
			}
		}
		return matchFlag;
	}

	/*
	 * Method to verify if the value located on the response message under the
	 * given element at given index equals to the expected value
	 */
	public static boolean assertIfElementValueEqualsByIndex(String xml, String parentXPath, String xPath, int index,
			String expectedValue, XMLType xmlType, boolean overrideLog, String operationKeyword, boolean... args)
					throws Exception {
		Boolean matchFlag = false;
		String actualValue = "", actualXPath = "";
		actualValue = getElementValueByIndex(xml, parentXPath, xPath, index, xmlType, operationKeyword);
		if (actualValue.equalsIgnoreCase(expectedValue)) {
			matchFlag = true;
		}

		if (xmlType == XMLType.REQUEST) {
			actualXPath = getRequestXPath(xPath, operationKeyword);
		} else if (xmlType == XMLType.RESPONSE) {
			actualXPath = getResponseXPath(xPath, operationKeyword);
		}

		if (args.length == 1) {

		} else {
			if (overrideLog == false) {
				if (matchFlag == true) {
					writeToReport(LogStatus.PASS,
							actualXPath + " -> " + "Expected :" + expectedValue + " Actual :" + actualValue);
				} else {
					writeToReport(LogStatus.FAIL,
							actualXPath + " -> " + "Expected :" + expectedValue + " Actual :" + actualValue);
				}
			}

			else {
				if (overrideLog == false) {
					if (expectedValue != null) {
						writeToReport(LogStatus.FAIL, "Element " + actualXPath + " is not found on the response");
						writeToReport(LogStatus.INFO, actualXPath + " -> " + "Expected :" + expectedValue
								+ " Actual : not found on response");
					} else {
						writeToReport(LogStatus.INFO,
								actualXPath + " -> " + "Expected :" + expectedValue + " Actual : null");
					}
				}
			}
		}
		return matchFlag;
	}

	/*
	 * Method to verify if the value located on the response message under the
	 * given element at given two level index equals to the expected value
	 */
	public static boolean assertIfElementValueEqualsByTwoLevelIndex(String xml, String parentXPath1, int index1, String parentXPath2, int index2, String xPath,
			XMLType xmlType, String operationKeyword, String expectedValue, boolean overrideLog, boolean... args)
					throws Exception {
		Boolean matchFlag = false;
		String actualValue = "", actualXPath = "";
		actualValue = getElementValueByTwoLevelIndex(xml, parentXPath1, index1, parentXPath2, index2, xPath, xmlType, operationKeyword);
		if (actualValue.equalsIgnoreCase(expectedValue)) {
			matchFlag = true;
		}

		if (xmlType == XMLType.REQUEST) {
			actualXPath = getRequestXPath(xPath, operationKeyword);
		} else if (xmlType == XMLType.RESPONSE) {
			actualXPath = getResponseXPath(xPath, operationKeyword);
		}

		if (args.length == 1) {

		} else {
			if (overrideLog == false) {
				if (matchFlag == true) {
					writeToReport(LogStatus.PASS,
							actualXPath + " -> " + "Expected :" + expectedValue + " Actual :" + actualValue);
				} else {
					writeToReport(LogStatus.FAIL,
							actualXPath + " -> " + "Expected :" + expectedValue + " Actual :" + actualValue);
				}
			}

			else {
				if (overrideLog == false) {
					if (expectedValue != null) {
						writeToReport(LogStatus.FAIL, "Element " + actualXPath + " is not found on the response");
						writeToReport(LogStatus.INFO, actualXPath + " -> " + "Expected :" + expectedValue
								+ " Actual : not found on response");
					} else {
						writeToReport(LogStatus.INFO,
								actualXPath + " -> " + "Expected :" + expectedValue + " Actual : null");
					}
				}
			}
		}
		return matchFlag;
	}


	/*
	 * This method is to sort List of linked Hash Maps
	 */
	private static ArrayList<LinkedHashMap<String, String>> sortListOfLinkedHashMap(
			List<LinkedHashMap<String, String>> nodeValues) {
		ArrayList<LinkedHashMap<String, String>> sortedList = new ArrayList<>();
		for (int index = 0; index < nodeValues.size(); index++) {
			TreeMap<String, String> tempTreeMap = new TreeMap<>(nodeValues.get(index));
			LinkedHashMap<String, String> tempLinkedMap = new LinkedHashMap<>(tempTreeMap);
			sortedList.add(tempLinkedMap);
		}
		return sortedList;
	}

	/*
	 * Method to verify if the values of a given XPath at a particular index is
	 * matching with the given value
	 */
	public synchronized static boolean assertIfElementValueEqualsByIndex(String xml, String indexPath, int index,
			String elementXpath, String expectedValue, String operationKeyword, boolean overrideLog) throws Exception {
		boolean matchFlag = false;
		indexPath = getResponseXPath(indexPath, operationKeyword);
		elementXpath = getResponseXPath(elementXpath, operationKeyword);
		elementXpath = elementXpath.replace(indexPath, indexPath + "[" + index + "]");

		String actualValue = "";
		actualValue = getElementValue(xml, elementXpath, XMLType.RESPONSE, operationKeyword);
		if (actualValue.equals(expectedValue)) {
			matchFlag = true;
		}
		if (overrideLog == false) {
			if (matchFlag == true)
				writeToReport(LogStatus.PASS, "Element - " + elementXpath.substring(elementXpath.indexOf("]") + 1)
				+ " value is populated as expected");
			else
				writeToReport(LogStatus.FAIL, "Element - " + elementXpath.substring(elementXpath.indexOf("]") + 1)
				+ " value is populated incorrectly");
		}
		writeToReport(LogStatus.INFO, "<b>Expected: </b>" + expectedValue + "<b> Actual: </b>" + actualValue);
		return matchFlag;
	}

	/*
	 * Method to verify if the value of the given XPath contains the given text
	 * (It's like a LIKE operator)
	 */
	public synchronized static boolean assertIfElementContains(String xml, String elementXpath, String expectedValue,
			String operationKeyword, boolean overrideLog) throws Exception {
		Boolean matchFlag = false;
		String actualValue = "";
		String actualElementPath = getResponseXPath(elementXpath, operationKeyword);
		actualValue = getElementValue(xml, elementXpath, XMLType.RESPONSE, operationKeyword);
		if (actualValue.contains((expectedValue))) {
			matchFlag = true;
		}
		if (overrideLog == false) {
			if (matchFlag == true)
				writeToReport(LogStatus.PASS, "Element value at " + actualElementPath + " is populated as expected");
			else
				writeToReport(LogStatus.FAIL, "Element value at " + actualElementPath + " is populated incorrectly");
		}
		writeToReport(LogStatus.INFO, "<b>Expected: </b>" + expectedValue + "<b> Actual: </b>" + actualValue);
		return matchFlag;
	}

	/*
	 * Method to verify if the given two values are matching
	 */
	public synchronized static boolean assertEquals(String expectedValue, String actualValue, boolean overrideLog)
			throws Exception {

		String actual = actualValue == null ? "null" : actualValue;
		String expected = expectedValue == null ? "null" : expectedValue;
		boolean matchFlag = false;
		if (expected.equalsIgnoreCase(actual)) {
			matchFlag = true;
		}
		if (overrideLog == false) {
			if (matchFlag == true) {
				writeToReport(LogStatus.PASS, "Values are equal");
				writeToReport(LogStatus.INFO, "<b>Expected: </b>" + expected + "<b> Actual: </b>" + actual);

			} else {
				writeToReport(LogStatus.FAIL, "Values are not equal");
				writeToReport(LogStatus.INFO, "<b>Expected: </b>" + expected + "<b> Actual: </b>" + actual);
			}
		}
		return matchFlag;
	}

	/*
	 * Method to verify if the given two records (HashMaps) are matching
	 */
	public synchronized static boolean assertEquals(LinkedHashMap<String, String> recordOfExpectedValues,
			LinkedHashMap<String, String> recordOfActualValues, boolean overrideLog) {
		boolean matchFlag = true;
		if (!recordOfExpectedValues.equals(recordOfActualValues)) {
			matchFlag = false;
		}
		if (!overrideLog) {
			if (recordOfExpectedValues.size() != 0 || recordOfActualValues.size() != 0) {
				if (recordOfExpectedValues.size() >= recordOfActualValues.size()) {
					Iterator<?> itre = recordOfExpectedValues.entrySet().iterator();
					while (itre.hasNext()) {
						@SuppressWarnings("rawtypes")
						Map.Entry pairs = (Map.Entry) itre.next();
						String key = pairs.getKey().toString();

						String exp = pairs.getValue() == null ? "Null" : pairs.getValue().toString();
						String act = recordOfActualValues.get(key) == null ? "Null"
								: recordOfActualValues.get(key).toString();

						if (act.equalsIgnoreCase(exp)) {
							writeToReport(LogStatus.PASS, key + " -> " + "Expected :" + exp + " Actual :" + act);
						} else {
							writeToReport(LogStatus.FAIL, key + " -> " + "Expected :" + exp + " Actual :" + act);
							matchFlag = false;
						}
					}
				} else {
					Iterator<?> itre = recordOfActualValues.entrySet().iterator();
					while (itre.hasNext()) {
						@SuppressWarnings("rawtypes")
						Map.Entry pairs = (Map.Entry) itre.next();
						String key = pairs.getKey().toString();

						String act = pairs.getValue() == null ? "Null" : pairs.getValue().toString();
						String exp = recordOfExpectedValues.get(key) == null ? "Null"
								: recordOfExpectedValues.get(key).toString();

						if (act.equalsIgnoreCase(exp)) {
							writeToReport(LogStatus.PASS, key + " -> " + "Expected :" + exp + " Actual :" + act);
						} else {
							writeToReport(LogStatus.FAIL, key + " -> " + "Expected :" + exp + " Actual :" + act);
							matchFlag = false;
						}
					}
				}
			} else {
				writeToReport(LogStatus.INFO, "<b>Both Expected and Actual records are empty</b>");
			}
		} else {
			writeToReport(LogStatus.INFO,
					"<b>Expected: </b>" + recordOfExpectedValues + "<b> Actual: </b>" + recordOfActualValues);
		}
		return matchFlag;
	}

	/*
	 * This Method is used to compare multiple records where each record
	 * consists of multiple elements. Example, Profile Details (First Name,
	 * Gender, Birthdate etc.,) are to be validated across all the profiles on
	 * the response message against the given Map
	 */
	public synchronized static boolean assertEquals(
			List<LinkedHashMap<String, String>> actualListOfValuesForMultipleRecords,
			List<LinkedHashMap<String, String>> expectedListOfValuesForMultipleRecords, boolean caseSensitive)
					throws Exception {
		boolean matchFlag = false;

		if (!caseSensitive) {
			List<LinkedHashMap<String, String>> upperCase_nodeValues = new ArrayList<LinkedHashMap<String, String>>();
			for (int i = 0; i < actualListOfValuesForMultipleRecords.size(); i++) {
				LinkedHashMap<String, String> upperCase_map = new LinkedHashMap<String, String>();
				Set<String> keys = actualListOfValuesForMultipleRecords.get(i).keySet();
				for (String key : keys) {
					String value = actualListOfValuesForMultipleRecords.get(i).get(key);
					upperCase_map.put(key, value.toUpperCase());
				}
				upperCase_nodeValues.add(upperCase_map);
			}
			actualListOfValuesForMultipleRecords = upperCase_nodeValues;
			List<LinkedHashMap<String, String>> upperCase_expectedListofValues = new ArrayList<LinkedHashMap<String, String>>();
			for (int i = 0; i < expectedListOfValuesForMultipleRecords.size(); i++) {
				LinkedHashMap<String, String> upperCase_map = new LinkedHashMap<String, String>();
				Set<String> keys = expectedListOfValuesForMultipleRecords.get(i).keySet();
				for (String key : keys) {
					String value = expectedListOfValuesForMultipleRecords.get(i).get(key);
					upperCase_map.put(key, value.toUpperCase());
				}
				upperCase_expectedListofValues.add(upperCase_map);
			}
			expectedListOfValuesForMultipleRecords = upperCase_expectedListofValues;
		}

		// Sort the Linked HashMaps
		actualListOfValuesForMultipleRecords = sortListOfLinkedHashMap(actualListOfValuesForMultipleRecords);
		expectedListOfValuesForMultipleRecords = sortListOfLinkedHashMap(expectedListOfValuesForMultipleRecords);
		List<LinkedHashMap<String, String>> tempExpectedListOfValues = new ArrayList<LinkedHashMap<String, String>>();
		for (int i = 0; i < expectedListOfValuesForMultipleRecords.size(); i++) {
			tempExpectedListOfValues.add(expectedListOfValuesForMultipleRecords.get(i));
		}

		Set<HashMap<String, String>> nodeSet = new HashSet<>(actualListOfValuesForMultipleRecords);
		Set<HashMap<String, String>> expectedSetOfValues = new HashSet<>(expectedListOfValuesForMultipleRecords);
		Integer expectedSize = expectedListOfValuesForMultipleRecords.size();
		Integer actualSize = actualListOfValuesForMultipleRecords.size();
		if (expectedSize == 0) {
			writeToReport(LogStatus.WARNING, "<b>Blocked: Prerequisite failure. Number of expected values zero.</b>");
			return false;
		} else {

			if (nodeSet.equals(expectedSetOfValues) && (expectedSize.equals(actualSize))) {
				matchFlag = true;
				writeToReport(LogStatus.PASS, "<b> Records are correctly retrieved</b> ");
			} else {
				if (expectedSetOfValues.size() != nodeSet.size()) {
					writeToReport(LogStatus.FAIL,
							"<b>Descripency identified between the no. of records displayed</b> ");
				} else {
					writeToReport(LogStatus.FAIL, "<b> Descripency identified between the records displayed</b>");
				}
			}
			List<String> expectedRecords = new ArrayList<String>();
			writeToReport(LogStatus.INFO, "<b> Number of expected records: </b>" + expectedSize.toString()
			+ " <b> Number of actual records: </b>" + actualSize.toString());

			for (int i = 0; i < expectedListOfValuesForMultipleRecords.size(); i++) {
				String resstr = "";

				Iterator<?> itre = expectedListOfValuesForMultipleRecords.get(i).entrySet().iterator();
				while (itre.hasNext()) {
					@SuppressWarnings("rawtypes")
					Map.Entry pairs = (Map.Entry) itre.next();
					resstr = resstr + ", " + "" + pairs.getKey().toString() + " =" + pairs.getValue().toString();
				}
				expectedRecords.add("Record " + (i + 1) + ": " + resstr.substring(1));
			}

			writeToReport(LogStatus.INFO, "Show/hide expected records", expectedRecords);

			List<String> actualRecords = new ArrayList<String>();
			for (int i = 0; i < actualListOfValuesForMultipleRecords.size(); i++) {
				String resstr = "";
				Iterator<?> itre = actualListOfValuesForMultipleRecords.get(i).entrySet().iterator();
				while (itre.hasNext()) {
					@SuppressWarnings("rawtypes")
					Map.Entry pairs = (Map.Entry) itre.next();
					resstr = resstr + ", " + "" + pairs.getKey().toString() + " =" + pairs.getValue().toString();
				}
				actualRecords.add("Record " + (i + 1) + ": " + resstr.substring(1));
			}

			writeToReport(LogStatus.INFO, "Show/hide actual records", actualRecords);

			List<String> unmatchedRecords = new ArrayList<String>();
			if (expectedListOfValuesForMultipleRecords.size() > actualListOfValuesForMultipleRecords.size()) {
				expectedListOfValuesForMultipleRecords.removeAll(actualListOfValuesForMultipleRecords);
				for (int i = 0; i < expectedListOfValuesForMultipleRecords.size(); i++) {
					String resstr = "";
					Iterator<?> itre = expectedListOfValuesForMultipleRecords.get(i).entrySet().iterator();
					while (itre.hasNext()) {
						@SuppressWarnings("rawtypes")
						Map.Entry pairs = (Map.Entry) itre.next();
						resstr = resstr + ", " + "" + pairs.getKey().toString() + " =" + pairs.getValue().toString();
					}
					unmatchedRecords.add("Record " + (i + 1) + ": " + resstr.substring(1));
				}
			}
			actualListOfValuesForMultipleRecords.removeAll(tempExpectedListOfValues);
			if (!actualListOfValuesForMultipleRecords.isEmpty()) {
				for (int i = 0; i < actualListOfValuesForMultipleRecords.size(); i++) {
					String resstr = "";
					Iterator<?> itre = actualListOfValuesForMultipleRecords.get(i).entrySet().iterator();
					while (itre.hasNext()) {
						@SuppressWarnings("rawtypes")
						Map.Entry pairs = (Map.Entry) itre.next();
						resstr = resstr + ", " + "" + pairs.getKey().toString() + " =" + pairs.getValue().toString();
					}
					unmatchedRecords.add("Record " + (i + 1) + ": " + resstr.substring(1));
				}
			}
			if (!matchFlag)
				writeToReport(LogStatus.INFO, "Show/hide unmatched records", unmatchedRecords);
		}
		return matchFlag;
	}
}
