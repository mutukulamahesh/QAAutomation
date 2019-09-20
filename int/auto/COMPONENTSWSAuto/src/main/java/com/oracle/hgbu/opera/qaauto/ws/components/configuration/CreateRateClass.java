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



public class CreateRateClass extends WSSetUp{
	
	public boolean createRateClass(String ds){
		try{
	
//			String fetchRateClassReq = WSClient.createSOAPMessage("FetchRateClass", ds);
//			String fetchRateClassRes = WSClient.processSOAPMessage(fetchRateClassReq);
//			
//			
//			if(WSAssert.assertIfElementValueEquals(fetchRateClassRes, "FetchRateClassRS_RateClasses_TotalRows", "1", true)){
//				WSClient.writeToReport(LogStatus.INFO, "Rate Class already exists");
//				return true;
//			}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Rate Class</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateRateClass", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Rate Class " + WSClient.getData("{var_RateClass}") +" already exists");
				return true;
			}
			else{
				String createRateClassReq = WSClient.createSOAPMessage("CreateRateClass", ds);
				String createRateClassRes = WSClient.processSOAPMessage(createRateClassReq);
				
				if(WSAssert.assertIfElementExists(createRateClassRes, "CreateRateClassRS_Success", true)){
					WSClient.writeToReport(LogStatus.PASS, "//CreateRateClassRS/Success exists on the response message");
					query = WSClient.getQuery("CreateRateClass", "QS_01");
					dbResult = WSClient.getDBRow(query);
					val = dbResult.get("COUNT");
					if(WSAssert.assertEquals("1", val, true)) {
						WSClient.writeToReport(LogStatus.INFO, "Rate Class Was Successfully Created");
						return true;
					}
					else 
						{
							WSClient.writeToReport(LogStatus.WARNING, "Rate Class not created");
							return false;
						}
				}
				else{
					if(WSAssert.assertIfElementExists(createRateClassRes, "CreateRateClassRS_Errors_Error_ShortText", false)) {
						WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createRateClassRes, "CreateRateClassRS_Errors_Error_ShortText", XMLType.RESPONSE));
					}
					return false;
				}
			}
		}
				
		catch (Exception e){
			
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			return false;
		}
	}
	
	@Test(groups = {"OperaConfig","createRateClass"})
	public void createMultiple_RateClasses(){
		String testName = "createRateClass";
		WSClient.startTest(testName,"Create Rate Classes if not present",	"OperaConfig");
		boolean created = true;
		int i;
		String rc;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("RateClass") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			rc=OperaPropConfig.getDataSetForCode("RateClass", dataset);
			WSClient.setData("{var_RateClass}",rc);
			created = created && createRateClass(dataset);
		}
		
		if(created)
			OperaPropConfig.setPropertyConfigResults("RateClass", "Y");
		else 
			OperaPropConfig.setPropertyConfigResults("RateClass", "N");
		
	}

}

