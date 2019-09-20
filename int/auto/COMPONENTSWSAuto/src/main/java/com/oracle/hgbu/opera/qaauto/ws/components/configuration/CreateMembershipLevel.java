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

public class CreateMembershipLevel extends WSSetUp{

	public boolean createMembershipLevels(String ds) {
		try
		{
//			String req_fetchMT = WSClient.createSOAPMessage("FetchMembershipLevels", ds);
//			String res_fetchMT = WSClient.processSOAPMessage(req_fetchMT);
//			if (WSAssert.assertIfElementExists(res_fetchMT, "FetchMembershipLevelsRS_Success", false))
//			{
//				if (WSAssert.assertIfElementExists(res_fetchMT, "FetchMembershipLevelsRS_MembershipLevels_MembershipLevel",true)) 
//				{
//					WSClient.writeToReport(LogStatus.INFO, "Membership Level"+ WSClient.getElementValue(res_fetchMT, "MembershipLevels_MembershipLevel_Code", XMLType.RESPONSE)+" already exists");
//				    return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Membership Level</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateMembershipLevels", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Membership Level " + WSClient.getData("{var_MembershipLevel}") +" already exists");
				return true;
			}
				else
				{
					String req_createML = WSClient.createSOAPMessage("CreateMembershipLevels", ds);
					String res_createML = WSClient.processSOAPMessage(req_createML);

					if (WSAssert.assertIfElementExists(res_createML, "CreateMembershipLevelsRS_Success",true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreateMembershipLevelsRS/Success exists on the response message");
					dbResult = WSClient.getDBRow(query);
					val = dbResult.get("COUNT");
					if(WSAssert.assertEquals("1", val, false)) {
						WSClient.writeToReport(LogStatus.INFO, "Membership Level has been created");
						return true;
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "Membership Level not created");
						return false;
					}
						
				}
				else{
					if(WSAssert.assertIfElementExists(res_createML, "CreateMembershipLevelsRS_Errors_Error_ShortText", false)) {
						WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createML, "CreateMembershipLevelsRS_Errors_Error_ShortText", XMLType.RESPONSE));
					}
					return false;
				}
				}
			
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR,"Error is "+e);
			return false;
		}
	}
	
	@Test(groups="OperaConfig",dependsOnGroups="createMembershipTypes")
	public void createMultipleMembershipLevel()
	{
		int i;
		boolean flag=true;
		String testName = "createMembershipLevels";
		WSClient.startTest(testName, "Create Membership Levels", "OperaConfig");
		String ds,memLevel,memType;
		WSClient.setData("{var_chain}", OPERALib.getChain());
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("MembershipLevel") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","MembershipLevel");
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
			memLevel=OperaPropConfig.getDataSetForCode("MembershipLevel", dataset);
			WSClient.setData("{var_MembershipLevel}",memLevel);
			memLevel=OperaPropConfig.getDataSetForCode("MembershipLevel", dependencies.get("MembershipLevel1"));
			WSClient.setData("{var_MembershipLevel1}",memLevel);
			memType=OperaPropConfig.getDataSetForCode("MembershipType", dependencies.get("MembershipType"));
			WSClient.setData("{var_MembershipType}",memType);
			flag=flag && createMembershipLevels(dataset);			
			dependencies.clear();
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("MembershipLevel", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("MembershipLevel", "N");
	}

}
