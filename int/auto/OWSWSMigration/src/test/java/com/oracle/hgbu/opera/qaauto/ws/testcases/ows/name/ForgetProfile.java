package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.name;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.SOAPClient;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.Setup;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.XMLutil;
import com.oracle.hgbu.opera.qaauto.ws.owsMigration.testSetUp;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.relevantcodes.customextentreports.LogStatus;

public class ForgetProfile extends Setup {
	String profileID1 = "";
  @Test(groups = { "sanity", "OWS", "ForgetProfile_TC001" })
  public void ForgetProfile_TC001() {
	
	HashMap<String, String> resDetails;
	List resDifferences;
	ArrayList<String> actualDifferencess;
	
	String testName = "ForgetProfile_TC001";
	WSClient.startTest(testName, "Verify ForgetProfile RQ & RS parameters", "minimumRegression");
	  
	  try {
		  if (profileID1.equals(""))
				profileID1 = CreateProfile.createProfile("DS_01");
			if (!profileID1.equals("error")) {
				WSClient.setData("{var_profileId}", profileID1);
				
						
				HashMap<String, String> reqDetails = testSetUp.getServiceSetUpData("Name_FetchProfile");
				String v5Req = testSetUp.soapV5ReqAsString();
				String cloudReq = testSetUp.soapCloudReqAsString();
				
				//Replacing all the variables with respective values.
				v5Req = v5Req.replaceAll("Var_NameID", profileID1);
				
				
				resDetails = SOAPClient.processSOAPMessage(v5Req, cloudReq, reqDetails);

				testSetUp.createResponseFile(resDetails.get("resV5"), testSetUp.v5ResponseFileName);
				testSetUp.createResponseFile(resDetails.get("resCloud"), testSetUp.cloudResponseFileName);

				resDifferences = XMLutil.compareXML();
				actualDifferencess = XMLutil.differencesByExcludingExceptions(resDifferences);
				if (actualDifferencess.size() == 0) {
					WSClient.writeToReport(LogStatus.PASS,
							"<b>Validating Name_FetchProfile RS</b>");
					Assert.assertTrue(true, actualDifferencess.toString());

				} else {
					WSClient.writeToReport(LogStatus.FAIL,
							"<b>Validating Name_FetchProfile RS</b>");
					Assert.fail(actualDifferencess.toString());
				}
				
			}
		  
	  }catch (Exception e){
		  WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	  } 
  }
  
}
