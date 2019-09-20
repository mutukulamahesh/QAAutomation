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


public class CreateCashiers extends WSSetUp {

	public boolean createCashiers(String dataset) {
		try {
			
//			String req_fetchCashiers = WSClient.createSOAPMessage("FetchCashiers", dataset);
//			String res_fetchCashiers = WSClient.processSOAPMessage(req_fetchCashiers);
//			
//			if(WSAssert.assertIfElementExists(res_fetchCashiers, "Cashiers_Cashier_CashierID", true)){
//				
//				WSClient.writeToReport(LogStatus.INFO,"CashierId already exist");
//				return true;
//			}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Cashier</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateCashiers", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Cashier ID " + WSClient.getData("{var_Cashier}") +" already exists");
				return true;
			}
			else
			{
				String req_createCashiers = WSClient.createSOAPMessage("CreateCashiers", dataset);
				String res_createCashiers = WSClient.processSOAPMessage(req_createCashiers);
				if(WSAssert.assertIfElementExists(res_createCashiers, "CreateCashiersRS_Success", true)){
					WSClient.writeToReport(LogStatus.PASS, "//CreateCashiersRS/Success exists on the response message");
					dbResult = new LinkedHashMap<String, String>();
					query = WSClient.getQuery("CreateCashiers", "QS_01");
					dbResult = WSClient.getDBRow(query);
					val = dbResult.get("COUNT");
					if(WSAssert.assertEquals("1", val, false)) {
						WSClient.writeToReport(LogStatus.INFO, "New CashiersID has been created");
						return true;
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "New CashiersID not created");
						return false;
					}
						
				}
				else{
					if(WSAssert.assertIfElementExists(res_createCashiers, "CreateCashiersRS_Errors_Error_ShortText", false)) {
						WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createCashiers, "CreateCashiersRS_Errors_Error_ShortText", XMLType.RESPONSE));
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
	public void createMultipleCashiers() {
		int i;
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String testName = "CreateCashiers";
		WSClient.startTest(testName, "Create Cashiers", "OperaConfig");
		WSClient.setData("{var_AttachedUser}", uname);
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("Cashiers") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;
			String value = OperaPropConfig.getDataSetForCode("Cashiers" , dataset);
			WSClient.setData("{var_Cashier}", value);
			flag = flag && createCashiers(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("Cashiers", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("Cashiers", "N");
	}
}
