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

public class CreateExternalSystems extends WSSetUp {
	
	public boolean createExternalSystems(String dataset) {
		try {
			
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching External System</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateExternalSystems", "QS_01");
			WSClient.writeToReport(LogStatus.INFO, query);
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "External System " + WSClient.getData("{var_code}") +" already exists");
				return true;
			}
				else {
					WSClient.writeToReport(LogStatus.INFO, "External System does not exist!!");
					String req_createExtCodes = WSClient.createSOAPMessage("CreateExternalSystems", dataset);
					String res_createExtCodes = WSClient.processSOAPMessage(req_createExtCodes);
					if(WSAssert.assertIfElementExists(res_createExtCodes, "CreateExternalSystemsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreateExternalSystemsRS/Success");
						dbResult = new LinkedHashMap<String, String>();
						query = WSClient.getQuery("CreateExternalSystems", "QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully created External System");
						return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "External System not created");
							return false;
						}
					}
					else{
						if(WSAssert.assertIfElementExists(res_createExtCodes, "CreateExternalSystemsRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createExtCodes, "CreateExternalSystemsRS_Errors_Error_ShortText", XMLType.RESPONSE));
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
	public void createMultiple_ExternalSystems() {
		int i;
		boolean flag = true;
		String testName = "CreateExternalSystems";
		WSClient.startTest(testName, "Create External System", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("ExternalSystem") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			WSClient.writeToReport(LogStatus.INFO, OperaPropConfig.getDataSetForCode("ExternalSystem" , dataset));
			String value = OperaPropConfig.getDataSetForCode("ExternalSystem" , dataset);
			WSClient.setData("{var_code}", value);
			WSClient.setData("{var_description}", "Creating External System"+i);
			flag = flag && createExternalSystems(dataset);
			
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("ExternalSystem", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("ExternalSystem", "N");
	}



}
