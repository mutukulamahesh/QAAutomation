package com.oracle.hgbu.opera.qaauto.ws.testcases.htng;

import java.util.ArrayList;
import java.util.List;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;

public class HTNGRunner {
	public static void main(String[] args) {
		new HTNGRunner().runHTNGSuite(args[0]);
	}
	public void runHTNGSuite(String testNGSuiteXMLFilePathAlongWithName) {
		TestListenerAdapter tla = new TestListenerAdapter();
		TestNG runner = new TestNG();
		List<String> suitefiles = new ArrayList<String>();
		suitefiles.add(testNGSuiteXMLFilePathAlongWithName+".xml");
		runner.setTestSuites(suitefiles);
		runner.addListener(tla);
		runner.run();
	}
}
