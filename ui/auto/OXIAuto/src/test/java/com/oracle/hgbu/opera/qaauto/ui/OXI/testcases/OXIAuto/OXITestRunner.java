package com.oracle.hgbu.opera.qaauto.ui.OXI.testcases.OXIAuto;

import java.util.ArrayList;
import java.util.List;

import com.beust.testng.TestNG;
import com.oracle.hgbu.opera.qaauto.ui.utilities.CustomReport;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;

@SuppressWarnings("deprecation")
public class OXITestRunner {

	public static void main(String  testng[]) {
		
		
		new OXITestRunner().runProfileSuite(testng[0]);
		try {
			Utils.emailtrigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void runProfileSuite(String testng)
	{
		// Create object of TestNG Class
		TestNG runner = new TestNG();
		runner.addListener(new CustomReport());
		// Create a list of String
		List<String> suitefiles = new ArrayList<String>();

		// Add xml file which you have to execute
		suitefiles.add(testng+".xml");

		// now set xml file for execution
		runner.setTestSuites(suitefiles);

		// finally execute the runner using run method
		runner.run();
	}

}


