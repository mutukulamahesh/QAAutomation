package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class Preferences extends WSSetUp{
	
	public boolean preferences(String dataset){
		try {

//			String fetchpreferencesReq = WSClient.createSOAPMessage("FetchPreferences", dataset);
//			String fetchpreferencesRes = WSClient.processSOAPMessage(fetchpreferencesReq);
//			if(WSAssert.assertIfElementExists(fetchpreferencesRes,"FetchPreferencesRS_Success",false)) {
//				if(WSAssert.assertIfElementExists(fetchpreferencesRes,"FetchPreferencesRS_HotelPreferences",true)) {
//					if(WSAssert.assertIfElementExists(fetchpreferencesRes,"FetchPreferencesRS_HotelPreferences_HotelPreference",false)) {
//						WSClient.writeToReport(LogStatus.INFO, "Details of Preference Exists.");
//						return true;
//					}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Preferences</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query;
			if(WSClient.getData("{var_isGlobal}").equalsIgnoreCase("Y")) {
			query = WSClient.getQuery("CreatePreferences", "QS_02");
			}
			else
			{
			 query = WSClient.getQuery("CreatePreferences", "QS_01");	
			}
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Preference " + WSClient.getData("{var_PreferenceType}") +" already exists");
				return true;
			}
				else {
						WSClient.writeToReport(LogStatus.INFO, "Preference doesnot exist!!");
						String createpreferenceReq = WSClient.createSOAPMessage("CreatePreferences", dataset);
						System.out.println("New Request " +  dataset );
						String createpreferenceRes = WSClient.processSOAPMessage(createpreferenceReq);
						
						if(WSAssert.assertIfElementExists(createpreferenceRes, "CreatePreferencesRS_Success", true)) {
							WSClient.writeToReport(LogStatus.PASS, "//CreatePreferencesRS/Success exists on the response message");
							dbResult = WSClient.getDBRow(query);
							val = dbResult.get("COUNT");
							if(WSAssert.assertEquals("1", val, true)) {
								WSClient.writeToReport(LogStatus.INFO, "Successfully created Preference");
								return true;
							}
							else {
								WSClient.writeToReport(LogStatus.WARNING, "Preference not created");
								return false;
							}
						}
						else {
							if(WSAssert.assertIfElementExists(createpreferenceRes, "CreatePreferencesRS_Errors_Error_ShortText", false)) {
								WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createpreferenceRes, "CreatePreferencesRS_Errors_Error_ShortText", XMLType.RESPONSE));
							}
							return false;
						}
					}
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	
	@Test(groups= {"OperaConfig"}, dependsOnGroups = {"createTemplatePreference"})
	public void createMultiple_preferences() {
		int i;
		boolean flag = true;
		String testName = "preferences";
		WSClient.startTest(testName,"Preferences","OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		WSClient.setData("{var_chain}", OPERALib.getChain());
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("PreferenceCode") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","PreferenceCode");
		boolean res = false;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String comm = OperaPropConfig.getCellComment("OperaConfig",row, i);
			//System.out.println(comm);
			if(comm==null) {
				WSClient.writeToReport(LogStatus.WARNING, "Please provide dependencies");
			}
			dependencies = OperaPropConfig.getDependency(comm);
			String value = OperaPropConfig.getDataSetForCode("PreferenceCode" , dataset);
			String val_group = OperaPropConfig.getDataSetForCode("PreferenceGroup" , dependencies.get("PreferenceGroup"));
			WSClient.setData("{var_isGlobal}",  dependencies.get("Global"));
			WSClient.setData("{var_PreferenceType}", value);
			WSClient.setData("{var_PreferenceGroup}", val_group);
			res = preferences(dataset);
			flag = flag && res;
			System.out.println(dataset + " " + flag);
			
			dependencies.clear();
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("PreferenceCode", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("PreferenceCode", "N");
	}

}



