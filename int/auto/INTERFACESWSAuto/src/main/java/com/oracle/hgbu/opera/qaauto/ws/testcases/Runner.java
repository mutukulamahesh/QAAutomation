package com.oracle.hgbu.opera.qaauto.ws.testcases;

import java.util.ArrayList;
import java.util.List;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;

public class Runner {
	public static void main(String[] args) {
		new Runner().runINTERFACESSuite(args[0]);
	}
	public void runINTERFACESSuite(String testNGSuiteXMLFilePathAlongWithName) {
		TestListenerAdapter tla = new TestListenerAdapter();
		TestNG runner = new TestNG();
		List<String> suitefiles = new ArrayList<String>();
		suitefiles.add(testNGSuiteXMLFilePathAlongWithName+".xml");
		runner.setTestSuites(suitefiles);
		runner.addListener(tla);
		runner.run();
	}
}
