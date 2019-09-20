package com.oracle.hgbu.opera.qaauto.ws.soap.client;

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

import com.oracle.hgbu.opera.qaauto.ws.common.utils.Logs;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.utils.WSLib;
import com.relevantcodes.customextentreports.LogStatus;

public class WSAssert extends WSLib {
	/**
	 * @author sbasana Description: Method to verify if the given element exists
	 *         on the response message
	 * @param elementXpath
	 * @param overrideLog
	 * @return boolean
	 * @throws Exception
	 */

	public static boolean assertIfElementExists(String xml, String elementXpath, boolean overrideLog,boolean ...args) throws Exception {
		boolean check = false;
		Logs.logger.info("Inside Assert if elementy exists");
		if (elementXpath != "") {
			elementXpath = WSLib.getResponseXPath(elementXpath);
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

					if(args.length!=1) {
						writeToReport(LogStatus.PASS, elementXpath + " exists on the response message");}
				}
			} else {
				check = false;
				if (overrideLog == false) {
					if(args.length!=1) {
						writeToReport(LogStatus.FAIL, elementXpath + " doesn't exist on the response message");
					}
				}
			}
		}
		return check;

	}




	public static boolean assertIfElementDoesNotExist(String xml, String elementXpath, boolean overrideLog) throws Exception {
		boolean check = false;
		Logs.logger.info("Inside Assert if elementy exists");
		if (elementXpath != "") {
			elementXpath = WSLib.getResponseXPath(elementXpath);
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

					writeToReport(LogStatus.FAIL, elementXpath + " exists on the response message");
				}
			} else {
				check = false;
				if (overrideLog == false)
					writeToReport(LogStatus.PASS, elementXpath + " doesn't exist on the response message");
			}
		}
		return check;

	}

	/**
	 * @author sbasana Description: Method to verify if the value located on the
	 *         response message under the given element equals to the expected
	 *         value
	 * @param elementXpath
	 * @param expectedValue
	 * @param overrideLog
	 * @return boolean
	 * @throws Exception
	 *
	 *
	 */
	public static boolean assertIfElementValueEquals(String xml, String elementXpath, String expectedValue,
			boolean overrideLog,boolean ...args) throws Exception {
		Boolean matchFlag = false;
		String actualValue = "";
		//manage Index
		String actualElementPath = getResponseXPath(elementXpath);
		String elementTag = actualElementPath.substring(actualElementPath.lastIndexOf('/') + 1, actualElementPath.length());
		if (assertIfElementExists(xml, elementXpath, true)) {
			actualValue = getElementValue(xml, elementXpath, XMLType.RESPONSE);
			if (actualValue.equalsIgnoreCase((expectedValue))) {
				matchFlag = true;
			}

			if(args.length==1) {
			}else {
				if (overrideLog == false) {
					if (matchFlag == true) {
						//writeToReport(LogStatus.INFO, "Element - " + elementXpath+ " value is populated as expected");
						writeToReport(LogStatus.PASS, elementTag+" -> Expected: " + expectedValue + " Actual: " + actualValue);
					}
					else {
						//writeToReport(LogStatus.INFO, "Element - " + elementXpath+ " value is incorrectly populated");
						writeToReport(LogStatus.FAIL, elementTag+" -> Expected: " + expectedValue + " Actual: " + actualValue);
					}
				}
			}
		}
		return matchFlag;
	}

	public static boolean assertIfElementValueFoundUsingAttributeEquals(String xml, String elementXpath, String expectedValue,
			boolean overrideLog,boolean ...args) throws Exception {
		Boolean matchFlag = false;
		String actualValue = "";
		//manage Index
		String actualElementPath = getResponseXPath(elementXpath);
		String elementTag = actualElementPath.substring(actualElementPath.lastIndexOf('/') + 1, actualElementPath.length());
		if (assertIfElementExists(xml, elementXpath, true)) {
			actualValue = getElementValue(xml, elementXpath, XMLType.RESPONSE);
			if (actualValue.equalsIgnoreCase((expectedValue))) {
				matchFlag = true;
			}

			if(args.length==1) {
			}else {
				if (overrideLog == false) {
					if (matchFlag == true) {
						//writeToReport(LogStatus.INFO, "Element - " + elementXpath+ " value is populated as expected");
						writeToReport(LogStatus.PASS, elementTag+" -> Expected: " + expectedValue + " Actual: " + actualValue);
					}
					else {
						//writeToReport(LogStatus.INFO, "Element - " + elementXpath+ " value is incorrectly populated");
						writeToReport(LogStatus.FAIL, elementTag+" -> Expected: " + expectedValue + " Actual: " + actualValue);
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
			XMLType xmlType, String expectedValue, boolean overrideLog, boolean... args)
					throws Exception {
		Boolean matchFlag = false;
		String actualValue = "", actualXPath = "";
		actualValue = getElementValueByTwoLevelIndex(xml, parentXPath1, index1, parentXPath2, index2, xPath, xmlType);

		if (actualValue.equalsIgnoreCase(expectedValue)) {
			matchFlag = true;
		}

		if (xmlType == XMLType.REQUEST) {
			actualXPath = getRequestXPath(xPath);
		} else if (xmlType == XMLType.RESPONSE) {
			actualXPath = getResponseXPath(xPath);
		}
		String elementTag = actualXPath.substring(actualXPath.lastIndexOf('/') + 1, actualXPath.length());
		if (args.length == 1) {
		} else {
			if (overrideLog == false) {
				if (matchFlag == true) {
					//	writeToReport(LogStatus.PASS, "Element - " + elementXpath.substring(elementXpath.indexOf("]") + 1)+ " value is populated as expected");
					writeToReport(LogStatus.INFO, "Element - " + actualXPath+ " value is populated as expected");
					writeToReport(LogStatus.PASS, elementTag+" -> Expected: " + expectedValue + " Actual: " + actualValue);
				}
				else {
					writeToReport(LogStatus.INFO, "Element - " + actualXPath+ " value is incorrectly populated");
					writeToReport(LogStatus.FAIL, elementTag+" -> Expected: " + expectedValue + " Actual: " + actualValue);
				}
			}
		}

		return matchFlag;
	}

	/*
	 * Author psarawag,nilsaini This Method is used to compare depended values
	 * e.g. If we want to compare response element's attribute value and
	 * element's value with database values i.e. two or more dependent values in
	 * response with two or more dependent values in database. Moreover,
	 * caseSensitive is true if the values of HashMap are case sensitive and
	 * false if not case sensitive
	 */
	public synchronized static boolean assertEquals(List<LinkedHashMap<String, String>> nodeValues,
			List<LinkedHashMap<String, String>> expectedListOfValues, boolean caseSensitive) throws Exception {
		boolean matchFlag = false;

		if (!caseSensitive) {
			System.out.println("caseSensitive = "+caseSensitive);
			List<LinkedHashMap<String, String>> upperCase_nodeValues = new ArrayList<LinkedHashMap<String, String>>();
			for (int i = 0; i < nodeValues.size(); i++) {
				LinkedHashMap<String, String> upperCase_map = new LinkedHashMap<String, String>();
				Set<String> keys = nodeValues.get(i).keySet();
				for (String key : keys) {
					String value = nodeValues.get(i).get(key);
					upperCase_map.put(key.toUpperCase(), value.toUpperCase());
				}
				upperCase_nodeValues.add(upperCase_map);
			}

			nodeValues = upperCase_nodeValues;
			System.out.println("Node Values: "+nodeValues);
			List<LinkedHashMap<String, String>> upperCase_expectedListofValues = new ArrayList<LinkedHashMap<String, String>>();

			for (int i = 0; i < expectedListOfValues.size(); i++) {
				LinkedHashMap<String, String> upperCase_map = new LinkedHashMap<String, String>();
				Set<String> keys = expectedListOfValues.get(i).keySet();
				for (String key : keys) {
					String value = expectedListOfValues.get(i).get(key);
					upperCase_map.put(key.toUpperCase(), value.toUpperCase());
				}
				upperCase_expectedListofValues.add(upperCase_map);
			}
			expectedListOfValues = upperCase_expectedListofValues;
			System.out.println("DB Values: "+expectedListOfValues);
		}


		//Sort the Linked HashMaps

		nodeValues = sortListOfLinkedHashMap(nodeValues);
		expectedListOfValues = sortListOfLinkedHashMap(expectedListOfValues);

		List<LinkedHashMap<String, String>> tempExpectedListOfValues = new ArrayList<LinkedHashMap<String,String>>();
		for(int i=0;i<expectedListOfValues.size();i++) {
			tempExpectedListOfValues.add(expectedListOfValues.get(i));
		}

		Set<HashMap<String, String>> nodeSet = new HashSet<>(nodeValues);
		Set<HashMap<String, String>> expectedSetOfValues = new HashSet<>(expectedListOfValues);

		Integer expectedSize = expectedListOfValues.size();
		Integer actualSize = nodeValues.size();

		if(expectedSize == 0) {
			writeToReport(LogStatus.WARNING, "<b>Blocked: Prerequisite failure. Number of expected values zero.</b>");
			return false;
		}
		else{

			if (nodeSet.equals(expectedSetOfValues) && (expectedSize.equals(actualSize))) {
				matchFlag = true;
				writeToReport(LogStatus.PASS, "<b> Records are correctly retrieved</b> ");
			} else {
				if (expectedSetOfValues.size() != nodeSet.size()) {
					writeToReport(LogStatus.FAIL,
							"<b>Descripency identified between the no. of records displayed</b> ");
				} else {
					writeToReport(LogStatus.FAIL,
							"<b> Descripency identified between the records displayed</b>");
				}
			}
			List<String> expectedRecords = new ArrayList<String>();
			writeToReport(LogStatus.INFO, "<b> Number of expected records: </b>" + expectedSize.toString()
			+ " <b> Number of actual records: </b>" + actualSize.toString());


			for (int i = 0; i < expectedListOfValues.size(); i++) {
				String resstr = "";

				Iterator<?> itre = expectedListOfValues.get(i).entrySet().iterator();
				while (itre.hasNext()) {
					@SuppressWarnings("rawtypes")
					Map.Entry pairs = (Map.Entry) itre.next();
					resstr = resstr + ", " + "" + pairs.getKey().toString() + " =" + pairs.getValue().toString();
				}
				expectedRecords.add("Record " + (i + 1) + ": " + resstr.substring(1));
			}

			writeToReport(LogStatus.INFO, "Show/hide expected records", expectedRecords);


			List<String> actualRecords = new ArrayList<String>();
			for (int i = 0; i < nodeValues.size(); i++) {
				String resstr = "";
				Iterator<?> itre = nodeValues.get(i).entrySet().iterator();
				while (itre.hasNext()) {
					@SuppressWarnings("rawtypes")
					Map.Entry pairs = (Map.Entry) itre.next();
					resstr = resstr + ", " + "" + pairs.getKey().toString() + " =" + pairs.getValue().toString();
				}
				actualRecords.add("Record " + (i + 1) + ": " + resstr.substring(1));
			}

			writeToReport(LogStatus.INFO, "Show/hide actual records", actualRecords);

			List<String> unmatchedRecords = new ArrayList<String>();

			if (expectedListOfValues.size() > nodeValues.size()){
				expectedListOfValues.removeAll(nodeValues);
				for (int i = 0; i < expectedListOfValues.size(); i++) {
					String resstr = "";
					Iterator<?> itre = expectedListOfValues.get(i).entrySet().iterator();
					while (itre.hasNext()) {
						@SuppressWarnings("rawtypes")
						Map.Entry pairs = (Map.Entry) itre.next();
						resstr = resstr + ", " + "" + pairs.getKey().toString() + " =" + pairs.getValue().toString();
					}
					unmatchedRecords.add("Record " + (i + 1) + ": " + resstr.substring(1));
				}
			}

			nodeValues.removeAll(tempExpectedListOfValues);
			if(!nodeValues.isEmpty()){
				for (int i = 0; i < nodeValues.size(); i++) {
					String resstr = "";
					Iterator<?> itre = nodeValues.get(i).entrySet().iterator();
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

	public static ArrayList<LinkedHashMap<String, String>> sortListOfLinkedHashMap(List<LinkedHashMap<String, String>> nodeValues){

		ArrayList<LinkedHashMap<String, String>> sortedList = new ArrayList<>();
		for(int index = 0; index < nodeValues.size(); index++){
			TreeMap<String, String> tempTreeMap = new TreeMap<>(nodeValues.get(index));
			LinkedHashMap<String, String> tempLinkedMap = new LinkedHashMap<>(tempTreeMap);
			sortedList.add(tempLinkedMap);
		}
		return sortedList;
	}

	/**
	 * @author sbasana Description: Method to verify if the values located on
	 *         the response message under the given element of a particular
	 *         summary equals to the expected value
	 * @param indexPath
	 * @param index
	 * @param elementXpath
	 * @param expectedValue
	 * @param overrideLog
	 * @return booleab
	 * @throws Exception
	 */
	public synchronized static boolean assertIfElementValueEqualsByIndex(String xml, String indexPath, int index,
			String elementXpath, String expectedValue, boolean overrideLog) throws Exception {
		boolean matchFlag = false;
		indexPath = getResponseXPath(indexPath);
		elementXpath = getResponseXPath(elementXpath);
		elementXpath = elementXpath.replace(indexPath, indexPath + "[" + index + "]");

		String actualValue = "";
		actualValue = getElementValue(xml, elementXpath, XMLType.RESPONSE);
		if (actualValue.equals(expectedValue)) {
			matchFlag = true;
		}
		if (overrideLog == false) {
			if (matchFlag == true) {
				//	writeToReport(LogStatus.PASS, "Element - " + elementXpath.substring(elementXpath.indexOf("]") + 1)+ " value is populated as expected");
				writeToReport(LogStatus.INFO, "Element - " + elementXpath+ " value is populated as expected");
				writeToReport(LogStatus.PASS, elementXpath.substring(elementXpath.indexOf("]") + 1)+" -> Expected: " + expectedValue + " Actual: " + actualValue);
			}
			else {
				writeToReport(LogStatus.INFO, "Element - " + elementXpath+ " value is incorrectly populated");
				writeToReport(LogStatus.FAIL, elementXpath.substring(elementXpath.indexOf("]") + 1)+" -> Expected: " + expectedValue + " Actual: " + actualValue);
			}
		}
		return matchFlag;
	}

	/**
	 * @author sbasana Description: Method to verify if the values located on
	 *         the response message under the given element contains the
	 *         expected value
	 * @param elementXpath
	 * @param expectedValue
	 * @param overrideLog
	 * @return
	 * @throws Exception
	 */
	public synchronized static boolean assertIfElementContains(String xml, String elementXpath, String expectedValue,
			boolean overrideLog) throws Exception {
		Boolean matchFlag = false;
		String actualValue = "";
		String actualElementPath = getResponseXPath(elementXpath);
		actualValue = getElementValue(xml, elementXpath, XMLType.RESPONSE);
		if (actualValue.contains((expectedValue))) {
			matchFlag = true;
		}
		if (overrideLog == false) {
			if (matchFlag == true)
				writeToReport(LogStatus.PASS, "Element value at " + actualElementPath + " is populated as expected");
			else
				writeToReport(LogStatus.FAIL, "Element value at " + actualElementPath + " is populated incorrectly");
		}
		writeToReport(LogStatus.INFO, "Expected: " + expectedValue + " Actual: " + actualValue);
		return matchFlag;
	}

	/**
	 * @author sbasana Description: Method to verify if the given two values are
	 *         matching
	 * @param expectedValue
	 * @param actualValue
	 * @param overrideLog
	 * @return
	 * @throws Exception
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
				writeToReport(LogStatus.INFO, "Expected: " + expected + "Actual:" + actual);

			} else {
				writeToReport(LogStatus.FAIL, "Values are not equal");
				writeToReport(LogStatus.INFO, "Expected:" + expected + "<b> Actual:" + actual);
			}
		}

		return matchFlag;
	}

	/**
	 * @author rishuja Description: Method to verify if the given two HashMap
	 *         are matching
	 * @param expectedValue
	 * @param actualValue
	 * @param overrideLog
	 * @return
	 * @throws Exception
	 */
	public synchronized static boolean assertEquals(LinkedHashMap<String, String> expectedValue,
			LinkedHashMap<String, String> actualValue, boolean overrideLog) {
		boolean matchFlag = true;
		if (!expectedValue.equals(actualValue)) {
			matchFlag = false;
		}
		if (!overrideLog) {
			if(expectedValue.size()!=0 || actualValue.size()!=0)
			{
				if (expectedValue.size() >= actualValue.size()) {
					Iterator<?> itre = expectedValue.entrySet().iterator();
					while (itre.hasNext()) {
						@SuppressWarnings("rawtypes")
						Map.Entry pairs = (Map.Entry) itre.next();
						String key = pairs.getKey().toString();

						String exp = pairs.getValue() == null ? "Null" : pairs.getValue().toString();
						String act = actualValue.get(key) == null ? "Null" : actualValue.get(key).toString();

						if (act.equalsIgnoreCase(exp)) {
							writeToReport(LogStatus.PASS, key + " -> " + "Expected :" + exp + " Actual :" + act);
						} else {
							writeToReport(LogStatus.FAIL, key + " -> " + "Expected :" + exp + " Actual :" + act);
							matchFlag = false;
						}
					}

				} else {
					Iterator<?> itre = actualValue.entrySet().iterator();
					while (itre.hasNext()) {
						@SuppressWarnings("rawtypes")
						Map.Entry pairs = (Map.Entry) itre.next();
						String key = pairs.getKey().toString();

						String act = pairs.getValue() == null ? "Null" : pairs.getValue().toString();
						String exp = expectedValue.get(key) == null ? "Null" : expectedValue.get(key).toString();

						if (act.equalsIgnoreCase(exp)) {
							writeToReport(LogStatus.PASS, key + " -> " + "Expected :" + exp + " Actual :" + act);
						} else {
							writeToReport(LogStatus.FAIL, key + " -> " + "Expected :" + exp + " Actual :" + act);
							matchFlag = false;
						}
					}

				}}
			else{
				writeToReport(LogStatus.INFO,"Both Expected and Actual records are empty");
			}
		} else {
			writeToReport(LogStatus.INFO, "Expected:" + expectedValue + "Actual: " + actualValue);
		}

		return matchFlag;
	}

}
