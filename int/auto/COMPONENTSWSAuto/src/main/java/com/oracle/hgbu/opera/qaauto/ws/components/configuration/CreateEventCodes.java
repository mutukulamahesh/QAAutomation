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

public class CreateEventCodes extends WSSetUp {
	
	public boolean createEventCode(String dataset) {
		try {
			
//			String req_fetchEvtCodes = WSClient.createSOAPMessage("FetchEventCodes", dataset);
//			String res_fetchEvtCodes = WSClient.processSOAPMessage(req_fetchEvtCodes);
//			if(WSAssert.assertIfElementExists(res_fetchEvtCodes, "FetchEventCodesRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(res_fetchEvtCodes, "EventCodes_EventCode_Code", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "Event Code already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Event Codes</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateEventCodes", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Event Code " + WSClient.getData("{var_EventCode}") +" already exists");
				return true;
			}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Event Code doesnot exist!!");
					String req_createEvtCodes = WSClient.createSOAPMessage("CreateEventCodes", dataset);
					String res_createEvtCodes = WSClient.processSOAPMessage(req_createEvtCodes);
					if(WSAssert.assertIfElementExists(res_createEvtCodes, "CreateEventCodesRS_Success", true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreateEventCodesRS/Success exists on the response message");
						dbResult = new LinkedHashMap<String, String>();
						query = WSClient.getQuery("CreateEventCodes", "QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Event Code");
						return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Event Code not created");
							return false;
						}
					}
					else{
						if(WSAssert.assertIfElementExists(res_createEvtCodes, "CreateEventCodesRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createEvtCodes, "CreateEventCodesRS_Errors_Error_ShortText", XMLType.RESPONSE));
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
	public void createMultiple_EventCodes() {
		int i;
		boolean flag = true;
		String testName = "CreateEventCodes";
		WSClient.startTest(testName, "Create Event Code", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("EventCode") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("EventCode" , dataset);
			WSClient.setData("{var_EventCode}", value);
			flag = flag && createEventCode(dataset);
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("EventCode", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("EventCode", "N");
	}

}
