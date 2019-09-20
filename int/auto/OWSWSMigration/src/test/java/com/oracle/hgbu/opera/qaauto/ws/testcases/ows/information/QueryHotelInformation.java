package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.information;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.owsMigration.SOAPClient;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.Setup;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.XMLutil;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.testSetUp;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.relevantcodes.customextentreports.LogStatus;

public class QueryHotelInformation  extends Setup {
  @Test(groups = { "sanity", "OWS", "QueryHotelInformation_TC001" })
  public void QueryHotelInformation_TC001() throws Exception {
	  String testName = "QueryHotelInformation_TC001";
		WSClient.startTest(testName, "Verify QueryHotelInformation response parameters ", "sanity");
		HashMap<String, String> resDetails;
		List resDifferences;
		ArrayList<String> actualDifferencess;

		HashMap<String, String> reqDetails = testSetUp.getServiceSetUpData("Information _QueryHotelInformation");
		String v5Req = testSetUp.soapV5ReqAsString();
		String cloudReq = testSetUp.soapCloudReqAsString();

		resDetails = SOAPClient.processSOAPMessage(v5Req, cloudReq, reqDetails);
		
		testSetUp.createResponseFile(resDetails.get("resV5"), testSetUp.v5ResponseFileName);
		testSetUp.createResponseFile(resDetails.get("resCloud"), testSetUp.cloudResponseFileName);
		
		resDifferences = XMLutil.compareXML();
		actualDifferencess = XMLutil.differencesByExcludingExceptions(resDifferences);
		if (actualDifferencess.size() == 0) {
			WSClient.writeToReport(LogStatus.PASS, "<b>Validation QueryHotelInformation RS </b>");
			Assert.assertTrue(true, actualDifferencess.toString());

		} else {
			WSClient.writeToReport(LogStatus.FAIL, "<b>Validation QueryHotelInformation RS</b>");
			WSClient.writeToReport(LogStatus.INFO, "<b> Differences </b>" + actualDifferencess.toString());
			Assert.assertFalse(false, actualDifferencess.toString());
		}
	  
  }
}
