package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.name;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.owsMigration.SOAPClient;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.XMLutil;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.testSetUp;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.relevantcodes.customextentreports.LogStatus;

public class InsertGuestCard {
	
  @Test
  public void InsertGuestCard_TC001() {
	  
	  String testName = "InsertGuestCard_TC001";
		WSClient.startTest(testName, "Verify InsertGuestCard RQ and RS","sanity");
		
		// Refer to existing scripts
		
//		HashMap<String, String> resDetails;
//		List resDifferences;
//		ArrayList<String> actualDifferencess;
//
//		HashMap<String, String> reqDetails = testSetUp.getServiceSetUpData("Name_InsertGuestCard");
//		
//		String v5Req = testSetUp.soapV5ReqAsString();
//		String cloudReq = testSetUp.soapCloudReqAsString();
//
//		resDetails = SOAPClient.processSOAPMessage(v5Req, cloudReq, reqDetails);
//		
//		testSetUp.createResponseFile(resDetails.get("resV5"), testSetUp.v5ResponseFileName);
//		testSetUp.createResponseFile(resDetails.get("resCloud"), testSetUp.cloudResponseFileName);
//		
//		resDifferences = XMLutil.compareXML();
//		actualDifferencess = XMLutil.differencesByExcludingExceptions(resDifferences);
//		if (actualDifferencess.size() == 0) {
//			WSClient.writeToReport(LogStatus.PASS, "<b>Validating Room Type and Available Room Information</b>");
//			Assert.assertTrue(true, actualDifferencess.toString());
//
//		} else {
//			WSClient.writeToReport(LogStatus.FAIL, "<b>Validating Room Type and Available Room Information</b>");
//			Assert.fail(actualDifferencess.toString());
//		}
	}
  }

