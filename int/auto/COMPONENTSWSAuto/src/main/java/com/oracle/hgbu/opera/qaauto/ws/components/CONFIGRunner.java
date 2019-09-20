package com.oracle.hgbu.opera.qaauto.ws.components;

import java.util.ArrayList;
import java.util.List;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;


public class CONFIGRunner {
	public static void main(String[] args) {
		new CONFIGRunner().runCONFIGSuite(args[0]);
	}
	public void runCONFIGSuite(String testNGSuiteXMLFilePathAlongWithName) {
		TestListenerAdapter tla = new TestListenerAdapter();
		TestNG runner = new TestNG();
		List<String> suitefiles = new ArrayList<String>();
		suitefiles.add(testNGSuiteXMLFilePathAlongWithName+".xml");
		runner.setTestSuites(suitefiles);
		runner.addListener(tla);
		runner.run();
	}
}
