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
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.relevantcodes.customextentreports.LogStatus;

public class UpdatePhone extends Setup {
	String profileID1 = "";
	@Test(groups = { "sanity", "OWS", "UpdatePhone_TC001" })

  public void UpdatePhone_TC001() {
		
		HashMap<String, String> resDetails;
		HashMap<String, String> reqDetails;
		List resDifferences;
		ArrayList<String> actualDifferencess;
		String phoneOperaID = null;
		

		String testName = "UpdatePhone_TC001";
		WSClient.startTest(testName, "Verify UpdatePhone RQ & RS parameters", "minimumRegression");
		
		try {
		  	String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
//			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
//			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
//			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
		
		  if (profileID1.equals(""))
				profileID1 = CreateProfile.createProfile("DS_01");
			if (!profileID1.equals("error")) {
				
				/*
				 *  Insert Phone
				 */
				WSClient.writeToReport(LogStatus.INFO, "Preparing to execute InsertPhone operation");
				reqDetails = testSetUp.getServiceSetUpData("Name_InsertPhone");
				String InsertPhoneReq = testSetUp.soapV5ReqAsString("InsertPhone_Prerequisite_01");
				InsertPhoneReq = InsertPhoneReq.replaceAll("Var_NameID", profileID1);
				
				phoneOperaID = InsertPhone.insertPhone(InsertPhoneReq, reqDetails);
				System.out.println("phoneOperaID " + phoneOperaID);
				
				
				/*
				 *  Update Phone
				 */
				WSClient.writeToReport(LogStatus.INFO, "Preparing to execute UpdateEmail operation");
				reqDetails = testSetUp.getServiceSetUpData("Name_UpdatePhone");
				String v5Req = testSetUp.soapV5ReqAsString();
				String cloudReq = testSetUp.soapCloudReqAsString();
				
				//Replacing all the variables with respective values.
				v5Req = v5Req.replaceAll("Var_namePhoneOperaId", phoneOperaID);
				cloudReq = cloudReq.replaceAll("Var_namePhoneOperaId", phoneOperaID);
				
				
				resDetails = SOAPClient.processSOAPMessage(v5Req, cloudReq, reqDetails);

				testSetUp.createResponseFile(resDetails.get("resV5"), testSetUp.v5ResponseFileName);
				testSetUp.createResponseFile(resDetails.get("resCloud"), testSetUp.cloudResponseFileName);
				
				WSClient.writeToReport(LogStatus.INFO, "Comparing the V5 and Cloud response");
				resDifferences = XMLutil.compareXML();
				actualDifferencess = XMLutil.differencesByExcludingExceptions(resDifferences);
				if (actualDifferencess.size() == 0) {
					WSClient.writeToReport(LogStatus.PASS,
							"<b>Validating Name_UpdateEmail RS</b>");

				} else {
					WSClient.writeToReport(LogStatus.FAIL,
							"<b>Validating Name_UpdateEmail RS</b>");
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Differences </b>" + actualDifferencess.toString());
				}
				
				
			}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			}
		  
		
		
  }
}
