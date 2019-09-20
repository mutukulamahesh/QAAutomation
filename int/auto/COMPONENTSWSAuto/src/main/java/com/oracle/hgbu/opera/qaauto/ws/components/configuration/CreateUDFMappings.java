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

public class CreateUDFMappings extends WSSetUp{
	public boolean createUDFMapping(String dataset) {
		try {
			
//			String req_fetchCashiers = WSClient.createSOAPMessage("FetchCashiers", dataset);
//			String res_fetchCashiers = WSClient.processSOAPMessage(req_fetchCashiers);
//			
//			if(WSAssert.assertIfElementExists(res_fetchCashiers, "Cashiers_Cashier_CashierID", true)){
//				
//				WSClient.writeToReport(LogStatus.INFO,"CashierId already exist");
//				return true;
//			}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching UDFMapping</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("ChangeUDFMapping", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "UDF Mapping for " + WSClient.getData("{var_UDFName}") +" already exists");
				return true;
			}
			else
			{
				String req_createUDFMapping = WSClient.createSOAPMessage("ChangeUDFMapping", dataset);
				String res_createUDFMapping = WSClient.processSOAPMessage(req_createUDFMapping);
				if(WSAssert.assertIfElementExists(res_createUDFMapping, "ChangeUDFMappingRS_Success", true)){
					WSClient.writeToReport(LogStatus.PASS, "//ChangeUDFMappingRS/Success exists on the response message");
					query = WSClient.getQuery("ChangeUDFMapping", "QS_01");
					dbResult = WSClient.getDBRow(query);
					val = dbResult.get("COUNT");
					if(WSAssert.assertEquals("1", val, false)) {
						WSClient.writeToReport(LogStatus.INFO, "UDF Mapping has been created");
						return true;
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "UDF Mapping not created");
						return false;
					}
						
				}
				else{
					if(WSAssert.assertIfElementExists(res_createUDFMapping, "ChangeUDFMappingRS_Errors_Error_ShortText", false)) {
						WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createUDFMapping, "ChangeUDFMappingRS_Errors_Error_ShortText", XMLType.RESPONSE));
					}
					return false;
				}
			}
	}
		catch(Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	
	
	@Test(groups= {"OperaConfig"})
	public void createMultipleUDFMappings() {
		int i;
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String testName = "CreateUDFMappings";
		WSClient.startTest(testName, "Create UDF Mappings", "OperaConfig");
		WSClient.setData("{var_chain}", OPERALib.getChain());
		
		// module : Profile
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("UDFLabel_P") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;     
			String value = OperaPropConfig.getDataSetForCode("UDFName" , dataset);
			String label = OperaPropConfig.getDataSetForCode("UDFLabel_P", dataset);
			WSClient.setData("{var_UDFName}", value);
			WSClient.setData("{var_UDFLabel}", label);
			WSClient.setData("{var_ModuleName}", "P");
			flag = flag && createUDFMapping("DS_01");
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("UDFLabel_P", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("UDFLabel_P", "N");
		
		
		// module : Reservation
		length = OperaPropConfig.getLengthForCode("UDFLabel_R") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;  
			String value = OperaPropConfig.getDataSetForCode("UDFName" , dataset);
			String label = OperaPropConfig.getDataSetForCode("UDFLabel_R", dataset);
			WSClient.setData("{var_UDFName}", value);
			WSClient.setData("{var_UDFLabel}", label);
			WSClient.setData("{var_ModuleName}", "R");
			flag = flag && createUDFMapping("DS_02");
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("UDFLabel_R", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("UDFLabel_R", "N");
		
		
	}

}
