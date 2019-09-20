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

public class CreateRevenueTypes extends WSSetUp {
	public boolean createRevenueType(String dataset){
		try{
			
//			String fetchRevenueTypeReq=WSClient.createSOAPMessage("FetchRevenueTypes", dataset);
//			String fetchRevenueTypeRes=WSClient.processSOAPMessage(fetchRevenueTypeReq);
//			if(WSAssert.assertIfElementExists(fetchRevenueTypeRes, "FetchRevenueTypesRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(fetchRevenueTypeRes, "RevenueTypes_RevenueType_Code", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "Revenue Type already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Revenue Type</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateRevenueTypes", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Revenue Type " + WSClient.getData("{var_RevenueType}") +" already exists");
				return true;
			}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Revenue Type doesnot exist!!");
					String createRevenueTypeReq=WSClient.createSOAPMessage("CreateRevenueTypes", dataset);
					String createRevenueTypeRes=WSClient.processSOAPMessage(createRevenueTypeReq);
					if(WSAssert.assertIfElementExists(createRevenueTypeRes, "CreateRevenueTypesRS_Success", true)){
						WSClient.writeToReport(LogStatus.PASS, "//CreateRevenueTypesRS/Success exists on the response message");
						query = WSClient.getQuery("CreateRevenueTypes", "QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully Created Revenue Type");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "New CashiersID not created");
							return false;
						}
					}
					else{
						if(WSAssert.assertIfElementExists(createRevenueTypeRes, "CreateRevenueTypesRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createRevenueTypeRes, "CreateRevenueTypesRS_Errors_Error_ShortText", XMLType.RESPONSE));
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

	@Test(groups= {"OperaConfig", "createRevenueType"}, dependsOnGroups= {"createRevenueGroup"})
	public void createMultiple_RevenueType() {
		int i;
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort());
		WSClient.setData("{var_chain}", OPERALib.getChain());
		String testName="RevenueType";
		WSClient.startTest(testName, "Create Revenue Type", "OperaConfig");
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("RevenueType") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","RevenueType");
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
			String value = OperaPropConfig.getDataSetForCode("RevenueType" ,dataset);
			WSClient.setData("{var_RevenueType}", value);
			String rev_group = OperaPropConfig.getDataSetForCode("RevenueGroup", dependencies.get("RevenueGroup"));
			WSClient.setData("{var_RevenueGroup}", rev_group);
			flag = flag && createRevenueType(dataset);
			dependencies.clear();
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("RevenueType", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("RevenueType", "N");
}


}
