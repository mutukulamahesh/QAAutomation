package com.oracle.hgbu.opera.qaauto.ui.rsv.testcases.reservation;

import java.util.ArrayList;
import java.util.List;

import com.beust.testng.TestNG;
import com.oracle.hgbu.opera.qaauto.ui.utilities.CustomReport;
import com.oracle.hgbu.opera.qaauto.ui.utilities.Utils;

@SuppressWarnings("deprecation")
public class RSVTestRunner {

	public static void main(String  testng[]) {
		
		//new RSVTestRunner().runReservationSuite(testng[0]);
		new RSVTestRunner().runReservation();
		
		try {
			Utils.emailtrigger();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void runReservation()
	{
		// Create object of TestNG Class
		TestNG runner = new TestNG();
		runner.addListener(new CustomReport());
		// Create a list of String
		List<String> suitefiles = new ArrayList<String>();

		// Add xml file which you have to execute
		suitefiles.add("BAT_RSV.xml");

		// now set xml file for execution
		runner.setTestSuites(suitefiles);

		// finally execute the runner using run method
		runner.run();
	}
	
	public void runReservationSuite(String suite)
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


