package com.oracle.hgbu.opera.qaauto.ui.crm;

import java.util.ArrayList;
import java.util.List;

import com.beust.testng.TestNG;
import com.oracle.hgbu.opera.qaauto.ui.utilities.CustomReport;

@SuppressWarnings("deprecation")
public class CRMTestRunner {

	public static void main(String  testng[]) {
		
		//CRMProfileTestRunner crm =new CRMProfileTestRunner();
		//crm.runProfile();
		//crm.runProfileSuite("profile");
		
		new CRMTestRunner().runSuite(testng[0]);
	
		try {
			//Utils.emailtrigger();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void runSuite(String suite)
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


