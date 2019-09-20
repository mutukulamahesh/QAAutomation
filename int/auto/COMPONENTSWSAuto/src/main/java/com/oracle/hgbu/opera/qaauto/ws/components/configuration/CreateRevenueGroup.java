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

public class CreateRevenueGroup extends WSSetUp{

	public boolean createRevenueGroup(String dataset){
		try{
			
//			String fetchRevenueGroupsReq=WSClient.createSOAPMessage("FetchRevenueGroups", dataset);
//			String fetchRevenueGroupsRes=WSClient.processSOAPMessage(fetchRevenueGroupsReq);
//			if(WSAssert.assertIfElementExists(fetchRevenueGroupsRes, "FetchRevenueGroupsRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(fetchRevenueGroupsRes, "RevenueGroups_RevenueGroup_Code", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "Revenue Group already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Revenue Group</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateRevenueGroups", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Revenue Group " + WSClient.getData("{var_RevenueGroup}") +" already exists");
				return true;
			}
			
				else {
					WSClient.writeToReport(LogStatus.INFO, "Revenue Group doesnot exist!!");
					String createRevenueGroupReq=WSClient.createSOAPMessage("CreateRevenueGroups", dataset);
					String createRevenueGroupRes=WSClient.processSOAPMessage(createRevenueGroupReq);
					if(WSAssert.assertIfElementExists(createRevenueGroupRes, "CreateRevenueGroupsRS_Success", true)){
						WSClient.writeToReport(LogStatus.PASS, "//CreateRevenueGroupsRS/Success exists on the response message");
						query = WSClient.getQuery("CreateRevenueGroups", "QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully Created Revenue Group");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Revenue Group not created");
							return false;
						}
					}
					else{
						if(WSAssert.assertIfElementExists(createRevenueGroupRes, "CreateRevenueGroupsRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createRevenueGroupRes, "CreateRevenueGroupsRS_Errors_Error_ShortText", XMLType.RESPONSE));
						}
						return false;
					}
						
				}
			
		}
		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception ocuured due to :" +e);
			return false;
		} 
	}

	@Test(groups= {"OperaConfig", "createRevenueGroup"})
	public void createMultiple_RevenueGroup() {
		int i;
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort());
		WSClient.setData("{var_chain}", OPERALib.getChain());
		String testName="RevenueGroup";
		WSClient.startTest(testName, "Create Revenue Group", "OperaConfig");
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("RevenueGroup") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("RevenueGroup" ,dataset);
			WSClient.setData("{var_RevenueGroup}", value);
			flag = flag && createRevenueGroup(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("RevenueGroup", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("RevenueGroup", "N");
}

}
