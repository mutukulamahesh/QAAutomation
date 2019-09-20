package com.oracle.hgbu.opera.qaauto.ws.owsMigration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Element;

/**
 *
 * Java program to compare two XML files using XMLUnit example
 * 
 * @author
 */
public class XMLutil {

	public static List compareXML() {
		return compareXML(testSetUp.getV5ResponseFilePath(), testSetUp.getCloudResponseFilepath());
	}

	public static List compareXML(String sourceFilePath, String targetFilePaht) {
		BufferedReader source;
		BufferedReader target;
		List differences = null;
		try {
			source = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFilePath)));
			target = new BufferedReader(new InputStreamReader(new FileInputStream(targetFilePaht)));

			// configuring XMLUnit to ignore white spaces
			XMLUnit.setIgnoreWhitespace(true);

			// comparing two XML using XMLUnit in Java
			differences = compareXML(source, target);

			// showing differences found in two xml files
			// printDifferences(differences);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return differences;
	}

	public static List compareXML(Reader source, Reader target) throws SAXException, IOException {

		// creating Diff instance to compare two XML files
		Diff xmlDiff = new Diff(source, target);

		// for getting detailed differences between two xml files
		DetailedDiff detailXmlDiff = new DetailedDiff(xmlDiff);
	

		return detailXmlDiff.getAllDifferences();
	}

	public static void printDifferences(List differences) {
		int totalDifferences = differences.size();
		System.out.println("===============================");
		System.out.println("Total differences : " + totalDifferences);
		System.out.println("================================");

		for (Object difference : differences.toArray()) {
			System.out.println(difference);
		}
	}

	public static ArrayList<String> differencesByExcludingExceptions(List differences) {
		int totalDifferences = differences.size();
		ArrayList<String> actDifferencess = new ArrayList<String>();

		for (Object difference : differences.toArray()) {

			if (difference.toString().contains("/Envelope[1]/Header[1]/MessageID[1]/text()[1]")
					|| difference.toString().contains("/Envelope[1]/Header[1]/RelatesTo[1]/text()[1]")
					||  difference.toString().contains("/Envelope[1]/Header[1]/OGHeader[1]/@timeStamp")
					) {
	
			}else{
				actDifferencess.add(difference.toString());
			}
		}
		return actDifferencess;
	}
	
	public static ArrayList<String> getElementValues(String xmlString, String parentPath, String attributeName ) throws Exception{
		ArrayList<String> attributeList = new ArrayList<>();
		 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document document = db.parse(xmlString);

         XPathFactory xpf = XPathFactory.newInstance();
         XPath xpath = xpf.newXPath();
         Element userElement = (Element) xpath.evaluate(parentPath, document, XPathConstants.NODE);
         attributeList.add((userElement.getAttribute(attributeName)));
         
		return attributeList;

	}

}
