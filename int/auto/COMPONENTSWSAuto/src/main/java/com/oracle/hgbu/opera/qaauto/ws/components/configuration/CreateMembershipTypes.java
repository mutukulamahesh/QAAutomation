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

public class CreateMembershipTypes extends WSSetUp {

	public boolean createMembershipTypes(String dataset) {
		try {
					
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Membership Types</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateMembershipTypes", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
	
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Membership Type " + WSClient.getData("{var_MembershipType}") +" already exists");
				return true;
			}
			else {
			     	String req_createMT = WSClient.createSOAPMessage("CreateMembershipTypes", dataset);
					String res_createMT = WSClient.processSOAPMessage(req_createMT);

					if (WSAssert.assertIfElementExists(res_createMT, "CreateMembershipTypesRS_Success",true)) {
					
						WSClient.writeToReport(LogStatus.PASS, "//CreateMembershipTypesRS/Success exists on the response message");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, false)) {
							WSClient.writeToReport(LogStatus.INFO, "Membership Type has been created");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Membership Type not created");
							return false;
						}
							
					}
					else{
						if(WSAssert.assertIfElementExists(res_createMT, "CreateMembershipTypesRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createMT, "CreateMembershipTypesRS_Errors_Error_ShortText", XMLType.RESPONSE));
						}
						return false;
					}
		}
						
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
		
	}

	@Test(groups = {"createMembershipTypes"},dependsOnGroups={"createMembershipClasses"})
	public void createMultipleMembershipTypes() {
		int i;
		boolean flag=true;
		String testName = "createMembershipTypes";
		WSClient.startTest(testName, "Create Membership Types", "OperaConfig");
		String ds,memClass,memType;
		WSClient.setData("{var_chain}", OPERALib.getChain());
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("MembershipType") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","MembershipType");
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
			memClass=OperaPropConfig.getDataSetForCode("MembershipClass", dependencies.get("MembershipClass"));
			WSClient.setData("{var_MembershipClass}",memClass);
			memType=OperaPropConfig.getDataSetForCode("MembershipType", dataset);
			WSClient.setData("{var_MembershipType}",memType);
			
			flag=flag && createMembershipTypes(dataset);			
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("MembershipType", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("MembershipType", "N");
	}

}
