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

public class CreateMembershipClass extends WSSetUp{
	
	public boolean createMembershipClass(String dataset)
	{
		try
		{
//			String req_fetchMC = WSClient.createSOAPMessage("FetchMembershipClasses", dataset);
//			String res_fetchMC = WSClient.processSOAPMessage(req_fetchMC);
//	
//			if (WSAssert.assertIfElementExists(res_fetchMC, "FetchMembershipClassesRS_Success", false))
//			{
//				if (WSAssert.assertIfElementExists(res_fetchMC,"FetchMembershipClassesRS_MembershipClasses_MembershipClass", true))
//				{
//					WSClient.writeToReport(LogStatus.INFO, "Membership Class "+WSClient.getElementValue(res_fetchMC, "MembershipClasses_MembershipClass_Code", XMLType.RESPONSE)+" already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Membership Class</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateMembershipClasses", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Membership Class " + WSClient.getData("{var_MembershipClass}") +" already exists");
				return true;
			}
				else
				{
					String req_createMC = WSClient.createSOAPMessage("CreateMembershipClasses", dataset);
					String res_createMC = WSClient.processSOAPMessage(req_createMC);

					if (WSAssert.assertIfElementExists(res_createMC, "CreateMembershipClassesRS_Success",false)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreateMembershipClassesRS/Success exists on the response message");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, false)) {
							WSClient.writeToReport(LogStatus.INFO, "Membership Class has been created");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Membership Class not created");
							return false;
						}
							
					}
					else{
						if(WSAssert.assertIfElementExists(res_createMC, "CreateMembershipClassesRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createMC, "CreateMembershipClassesRS_Errors_Error_ShortText", XMLType.RESPONSE));
						}
						return false;
					}
					
			}
			
		}catch (Exception e) {
				e.printStackTrace();
				return false;
		}
	}
	
	
	@Test(groups={"createMembershipClasses"})
	public void createMembershipClasses()
	{
		String testName = "createMembershipClasses";
		WSClient.startTest(testName, "Creating a membership class","OperaConfig");
		boolean flag=true;
		String memClass;
		WSClient.setData("{var_chain}", OPERALib.getChain());
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		int i;
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("MembershipClass") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			memClass=OperaPropConfig.getDataSetForCode("MembershipClass", dataset);
			WSClient.setData("{var_MembershipClass}",memClass);
			flag=flag && createMembershipClass(dataset);
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("MembershipClass", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("MembershipClass", "N");
	}

}
