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

public class PreferenceGroups extends WSSetUp{

	
	public boolean preferenceGroups(String dataset){
		try {

//			String fetchpreferenceGroupReq = WSClient.createSOAPMessage("FetchPreferenceGroups", dataset);
//			String fetchpreferenceGroupResponse = WSClient.processSOAPMessage(fetchpreferenceGroupReq);
//			if(WSAssert.assertIfElementExists(fetchpreferenceGroupResponse,"FetchPreferenceGroupsRS_Success",false)) {
//				if(WSAssert.assertIfElementExists(fetchpreferenceGroupResponse,"FetchPreferenceGroupsRS_PreferenceGroups",true)) {
//					if(WSAssert.assertIfElementExists(fetchpreferenceGroupResponse,"FetchPreferenceGroupsRS_PreferenceGroups_PreferenceGroup",true)) {
//						WSClient.writeToReport(LogStatus.INFO, "Details of Preference Group Exists.");
//						return true;
//					}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Preference Group</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreatePreferenceGroups", "QS_01");
			dbResult = WSClient.getDBRow(query);
         	String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Preference Group " + WSClient.getData("{var_PreferenceGroup}") +" already exists");
				return true;
			}
			
				else {
						WSClient.writeToReport(LogStatus.INFO, "Preference Group doesnot exist!!");
						String createpreferenceGroupReq = WSClient.createSOAPMessage("CreatePreferenceGroups", dataset);
						String createpreferenceGroupesponse = WSClient.processSOAPMessage(createpreferenceGroupReq);
						
						if(WSAssert.assertIfElementExists(createpreferenceGroupesponse, "CreatePreferenceGroupsRS_Success", true)) {
							WSClient.writeToReport(LogStatus.PASS, "//CreatePreferenceGroupsRS/Success exists on the response message");
							dbResult = WSClient.getDBRow(query);
							val = dbResult.get("COUNT");
							if(WSAssert.assertEquals("1", val, true)) {
								WSClient.writeToReport(LogStatus.INFO, "Successfully created Preference Group");
								return true;
							}
							else {
								WSClient.writeToReport(LogStatus.WARNING, "Preference Group not created");
								return false;
							}
						}
						else {
								if(WSAssert.assertIfElementExists(createpreferenceGroupesponse, "CreatePreferenceGroupsRS_Errors_Error_ShortText", false)) {
									WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createpreferenceGroupesponse, "CreatePreferenceGroupsRS_Errors_Error_ShortText", XMLType.RESPONSE));
								}
								return false;
						}
					}			
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	
	@Test(groups= {"createPreferenceGroups"})
	public void createMultiple_preferenceGroups() {
		int i;
		boolean flag = true;
		String testName = "preferenceGroups";
		WSClient.startTest(testName,"Preference Groups","OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		WSClient.setData("{var_Chain}", OPERALib.getChain());
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("PreferenceGroup") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("PreferenceGroup" , dataset);
			WSClient.setData("{var_PreferenceGroup}", value);
			flag = flag && preferenceGroups(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("PreferenceGroup", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("PreferenceGroup", "N");
	}
}
