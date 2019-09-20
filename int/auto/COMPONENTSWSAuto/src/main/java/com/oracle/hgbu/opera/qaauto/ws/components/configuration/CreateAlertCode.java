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

public class CreateAlertCode extends WSSetUp{
	public boolean createAlerts(String dataset) {
		try {
			
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Alert Code</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateAlertCodes", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Alert Code " + WSClient.getData("{var_AlertCode}") +" already exists");
				return true;
			}
			else
			{
				String req_createAlertCodes = WSClient.createSOAPMessage("CreateAlertCodes", dataset);
				String res_createAlertCodes = WSClient.processSOAPMessage(req_createAlertCodes);
				if(WSAssert.assertIfElementExists(res_createAlertCodes, "CreateAlertCodesRS_Success", true)){
					WSClient.writeToReport(LogStatus.PASS, "//CreateAlertCodesRS/Success exists on the response message");
					dbResult = WSClient.getDBRow(query);
					val = dbResult.get("COUNT");
					if(WSAssert.assertEquals("1", val, false)) {
						WSClient.writeToReport(LogStatus.INFO, "New Alert Code has been created");
						return true;
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "Alert Code not created");
						return false;
					}
						
				}
				else{
					if(WSAssert.assertIfElementExists(res_createAlertCodes, "CreateAlertCodesRS_Errors_Error_ShortText", false)) {
						WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createAlertCodes, "CreateAlertCodesRS_Errors_Error_ShortText", XMLType.RESPONSE));
					}
					return false;
				}
			}
	}
		catch(Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	
	
	@Test(groups= {"OperaConfig"})
	public void createMultipleAlertCodes() {
		int i;
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String testName = "CreateAlertCodes";
		String dataset = "";
		WSClient.startTest(testName, "Create Alert Codes", "OperaConfig");
		int length = OperaPropConfig.getLengthForCode("AlertCode") - 1;
		for(i=1;i<length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;
			String value = OperaPropConfig.getDataSetForCode("AlertCode" , dataset);
			WSClient.setData("{var_AlertCode}", value);
			flag = flag && createAlerts(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("AlertCode", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("AlertCode", "N");
	}

}
