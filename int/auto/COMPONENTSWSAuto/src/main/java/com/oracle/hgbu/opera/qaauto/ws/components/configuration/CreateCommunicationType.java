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

public class CreateCommunicationType extends WSSetUp {


	public boolean createCommunicationType(String dataset) {

		try {
			
			
//			String fetchCommTypeReq = WSClient.createSOAPMessage("FetchCommunicationTypes", dataset);
//			String fetchCommTypeRes = WSClient.processSOAPMessage(fetchCommTypeReq);
//			if(WSAssert.assertIfElementExists(fetchCommTypeRes, "FetchCommunicationTypesRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(fetchCommTypeRes, "FetchCommunicationTypesRS_CommunicationTypes_CommunicationType_Code", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "Communication Type already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Communication Type</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateCommunicationType", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Communication Type " + WSClient.getData("{var_CommunicationType}") +" already exists");
				return true;
			}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Commnunication Type doesnot exist!!");
					String createCommTypeReq = WSClient.createSOAPMessage("CreateCommunicationType", dataset);
					String createCommTypeRes = WSClient.processSOAPMessage(createCommTypeReq);
					if (WSAssert.assertIfElementExists(createCommTypeRes, "CreateCommunicationTypeRS_Success",true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreateCommunicationTypeRS/Success exists on the response message");
						dbResult = new LinkedHashMap<String, String>();
						query = WSClient.getQuery("CreateCommunicationType", "QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully created Communication Type");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Communication Type not created");
							return false;
						}
						
					}
					else{
						if(WSAssert.assertIfElementExists(createCommTypeRes, "CreateCommunicationTypeRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createCommTypeRes, "CreateCommunicationTypeRS_Errors_Error_ShortText", XMLType.RESPONSE));
						}
						return false;
					}
				}
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	
	@Test(groups= {"OperaConfig"}, dependsOnGroups= {"createCommunicationMethod"})
	public void createMultiple_CommunicationType() {
		int i;
		boolean flag = true;
		String testName = "CreateCommunicationType";
		WSClient.startTest(testName, "Create Communication Type", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		WSClient.setData("{var_chain}", OPERALib.getChain());
		String dataset = "",  val_commMethod="";
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int length = OperaPropConfig.getLengthForCode("CommunicationType") - 1;
		int row = OperaPropConfig.getRowIndex("OperaConfig", "CommunicationType");
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
			val_commMethod=OperaPropConfig.getDataSetForCode("CommunicationMethod", dependencies.get("CommunicationMethod"));

			String value = OperaPropConfig.getDataSetForCode("CommunicationType" , dataset);
			WSClient.setData("{var_CommunicationType}", value);
			WSClient.setData("{var_CommunicationMethod}", val_commMethod);
			flag = flag && createCommunicationType(dataset);
			dependencies.clear();
		}
		

		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("CommunicationType", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("CommunicationType", "N");
	}

}
