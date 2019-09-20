package com.oracle.hgbu.opera.qaauto.ui.crm.testcases.profile;

import java.util.ArrayList;
import java.util.List;

import com.beust.testng.TestNG;
import com.oracle.hgbu.opera.qaauto.ui.utilities.CustomReport;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;

@SuppressWarnings("deprecation")
public class CRMProfileTestRunner {

	public static void main(String  testng[]) {
		
//		new CRMProfileTestRunner().runProfileSuite(testng[0]);
		new CRMProfileTestRunner().runProfileSuite("BAT_Profile");
//		new CRMProfileTestRunner().runProfile();
		try {
			Utils.emailtrigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void runProfile()
	{
		// Create object of TestNG Class
		TestNG runner = new TestNG();
		runner.addListener(new CustomReport());
		// Create a list of String
		List<String> suitefiles = new ArrayList<String>();

		// Add xml file which you have to execute
		suitefiles.add("BAT_Profile.xml");

		// now set xml file for execution
		runner.setTestSuites(suitefiles);

		// finally execute the runner using run method
		runner.run();
	}
	
	public void runProfileSuite(String suite)
	{
		// Create object of TestNG Class
		TestNG runner = new TestNG();
		runner.addListener(new CustomReport());
		// Create a list of String
		List<String> suitefiles = new ArrayList<String>();

		// Add xml file which you have to execute
		suitefiles.add(suite+".xml");

		// now set xml file for execution
		runner.setTestSuites(suitefiles);

		// finally execute the runner using run method
		runner.run();
	}

}


