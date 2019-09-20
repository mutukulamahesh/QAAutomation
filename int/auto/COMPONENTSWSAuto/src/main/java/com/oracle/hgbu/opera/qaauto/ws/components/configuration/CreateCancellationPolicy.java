package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateCancellationPolicy extends WSSetUp {

	public boolean createCancelPolicy(String dataset){
		try{
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Cancellation Policy</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateCancellationPolicy", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Cancellation Policy " + WSClient.getData("{var_CancelCode}") +" already exists");
				return true;
			}
			else {
				WSClient.writeToReport(LogStatus.INFO, "Cancellation Policy doesnot exist!!");
				String createCancelPolicyReq=WSClient.createSOAPMessage("CreateCancellationPolicy", dataset);
				String createCancelPolicyRes=WSClient.processSOAPMessage(createCancelPolicyReq);
				if(WSAssert.assertIfElementExists(createCancelPolicyRes, "CreateCancellationPolicyRS_Success", true)){
					WSClient.writeToReport(LogStatus.PASS, "//CreateCancellationPolicyRS/Success exists on the response message");
						
						//DB Validation
						dbResult = new LinkedHashMap<String, String>();
						query = WSClient.getQuery("QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully Created Cancellation Policy");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Cancellation Policy not created");
							return false;
						}
					}
					else{
						if(WSAssert.assertIfElementExists(createCancelPolicyRes, "CreateCancellationPolicyRS_Errors_Error_ShortText", true)) {
							WSClient.writeToReport(LogStatus.FAIL, "Cancellation Policy is not created and error on response is "+ WSAssert.getElementValue(createCancelPolicyRes, "CreateCancellationPolicyRS_Errors_Error_ShortText", XMLType.RESPONSE));
						}
						return false;
					}
				}
			
			}
			
		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception ocuured due to :" +e);
			return false;
		} 
	}

	@Test(groups= {"OperaConfig", "createCancellationPolicy"}, dependsOnGroups = {"createRateCode", "createReservationType"})
	public void createMultiple_CancelPolicy() {
		int i;
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String testName="CancellationPolicy";
		WSClient.startTest(testName, "Create Cancellation Policy", "OperaConfig");
		
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("CancellationPolicy") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;
			String value = OperaPropConfig.getDataSetForCode("CancellationPolicy" , dataset);
			WSClient.setData("{var_CancelCode}", value);
			flag = flag && createCancelPolicy(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("CancellationPolicy", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("CancellationPolicy", "N");
}

}
