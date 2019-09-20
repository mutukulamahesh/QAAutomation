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

public class CreateItemClass extends WSSetUp{

	public boolean createItemClass(String dataset){
		try{
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Item Class</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateItemClasses", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Item Class " + WSClient.getData("{var_ItemClass}") +" already exists");
				return true;
			}
					String createItemClassReq=WSClient.createSOAPMessage("CreateItemClasses", dataset);
					String createItemClassRes=WSClient.processSOAPMessage(createItemClassReq);
					if(WSAssert.assertIfElementExists(createItemClassRes, "CreateItemClassesRS_Success", true)){
						WSClient.writeToReport(LogStatus.PASS, "//CreateItemClassesRS/Success exists on the response message");
						dbResult = new LinkedHashMap<String, String>();
						query = WSClient.getQuery("CreateItemClasses", "QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully Created Item Class");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Item Class not created");
							return false;
						}
					}
					else {
						if(WSAssert.assertIfElementExists(createItemClassRes, "CreateItemClassesRS_Errors_Error_ShortText", false)) {
//							String actual_msg = WSAssert.getElementValue(createItemClassRes, "CreateItemClassesRS_Errors_Error_ShortText", XMLType.RESPONSE);
//							if(actual_msg.contains("exists")) {
//								WSClient.writeToReport(LogStatus.INFO, "Item Class Already Exists");
//								return true;
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createItemClassRes, "CreateItemClassesRS_Errors_Error_ShortText", XMLType.RESPONSE));
							}
						return false;
						
					}
		}
		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception ocuured due to :" +e);
			return false;
		} 
	}

	@Test(groups= {"OperaConfig", "createItemClass"}, dependsOnGroups= {"createRevenueType"})
	public void createMultiple_ItemClass() {
		int i;
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort());
		String testName="ItemClass";
		WSClient.startTest(testName, "Create ItemClass", "OperaConfig");
		
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("ItemClass") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("ItemClass" ,dataset);
			WSClient.setData("{var_ItemClass}", value);
			flag = flag && createItemClass(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("ItemClass", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("ItemClass", "N");
	}
}
