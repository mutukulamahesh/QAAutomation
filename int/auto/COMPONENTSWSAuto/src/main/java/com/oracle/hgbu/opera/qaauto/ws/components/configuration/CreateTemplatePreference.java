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

public class CreateTemplatePreference extends WSSetUp{
	public boolean templatePreferences(String dataset){
		try {

//			String fetchtemplatePreferencesReq = WSClient.createSOAPMessage("FetchTemplatePreferences", "DS_01");
//			String fetchTemplatePreferencesRes = WSClient.processSOAPMessage(fetchtemplatePreferencesReq);
//			if(WSAssert.assertIfElementExists(fetchTemplatePreferencesRes,"FetchTemplatePreferencesRS_Success",true)) {
//					if(WSAssert.assertIfElementExists(fetchTemplatePreferencesRes,"FetchTemplatePreferencesRS_TemplatePreferences_TemplatePreference_Code",true)){
//						WSClient.writeToReport(LogStatus.INFO, "Template Preference Exists.");
//						return true;
//					}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Preference Template</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateTemplatePreferences", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Template for Preference " + WSClient.getData("{var_PreferenceType}") +" already exists");
				return true;
			}
					
					else {
						WSClient.writeToReport(LogStatus.INFO, "Template Preference doesnot exist!!");
						String createTemplatePreferenceReq = WSClient.createSOAPMessage("CreateTemplatePreferences", dataset);
						String createTemplatePreferenceRes = WSClient.processSOAPMessage(createTemplatePreferenceReq);
						
						if(WSAssert.assertIfElementExists(createTemplatePreferenceRes, "CreateTemplatePreferencesRS_Success", true)) {
							WSClient.writeToReport(LogStatus.PASS, "//CreateTemplatePreferencesRS/Success exists on the response message");
							dbResult = WSClient.getDBRow(query);
							val = dbResult.get("COUNT");
							if(WSAssert.assertEquals("1", val, true)) {
								WSClient.writeToReport(LogStatus.INFO, "Successfully created Template Preference");
								return true;
							}
							else {
								WSClient.writeToReport(LogStatus.WARNING, "Template Preference not created");
								return false;
							}
						}
						else {
							
							if(WSAssert.assertIfElementExists(createTemplatePreferenceRes, "CreateTemplatePreferencesRS_Errors_Error_ShortText", false)) {
								WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createTemplatePreferenceRes, "CreateTemplatePreferencesRS_Errors_Error_ShortText", XMLType.RESPONSE));
							}
							return false;
						}
					}			
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	
	@Test(groups= {"OperaConfig", "createTemplatePreference"}, dependsOnGroups = {"createPreferenceGroups"})
	public void createMultiple_Templatepreferences() {
		int i;
		boolean flag = true;
		String testName = "createTemplatePreference";
		WSClient.startTest(testName,"Create Template for Preferences","OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		WSClient.setData("{var_Chain}", OPERALib.getChain());
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("PreferenceCode") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","PreferenceCode");
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
			WSClient.setData("{var_PreferenceType}", value);
			WSClient.setData("{var_PreferenceGroup}", val_group);
			flag = flag && templatePreferences(dataset);
			dependencies.clear();
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("TemplatePreference", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("TemplatePreference", "N");
	}

}
