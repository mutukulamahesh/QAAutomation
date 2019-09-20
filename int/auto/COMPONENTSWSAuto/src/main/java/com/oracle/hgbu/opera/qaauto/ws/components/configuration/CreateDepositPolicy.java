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

public class CreateDepositPolicy extends WSSetUp {
	public boolean createDepositPolicy(String dataset){
		try{
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Deposit Policy</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateDepositPolicy", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Deposit Policy " + WSClient.getData("{var_DepositCode}") +" already exists");
				return true;
			}
			else {
				WSClient.writeToReport(LogStatus.INFO, "Deposit Policy doesnot exist!!");
				String createDepositPolicyReq=WSClient.createSOAPMessage("CreateDepositPolicy", dataset);
				String createDepositPolicyRes=WSClient.processSOAPMessage(createDepositPolicyReq);
				if(WSAssert.assertIfElementExists(createDepositPolicyRes, "CreateDepositPolicyRS_Success", true)){
					WSClient.writeToReport(LogStatus.PASS, "//CreateDepositPolicyRS/Success exists on the response message");
						
						//DB Validation
						dbResult = new LinkedHashMap<String, String>();
						query = WSClient.getQuery("QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully Created Deposit Policy");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Deposit Policy not created");
							return false;
						}
					}
					else{
						if(WSAssert.assertIfElementExists(createDepositPolicyRes, "CreateDepositPolicyRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createDepositPolicyRes, "CreateDepositPolicyRS_Errors_Error_ShortText", XMLType.RESPONSE));
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

	@Test(groups= {"OperaConfig","createDepositPolicy"}, dependsOnGroups = {"createRateCode", "createReservationType"})
	public void createMultiple_DepositPolicy() {
		int i;
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String testName="DepositPolicy";
		WSClient.startTest(testName, "Create Deposit Policy", "OperaConfig");
		
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("DepositPolicy") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("DepositPolicy" , dataset);
			WSClient.setData("{var_DepositCode}", value);
			flag = flag && createDepositPolicy(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("DepositPolicy", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("DepositPolicy", "N");
}


}
