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


public class CreateRateCategory extends WSSetUp {


	public boolean createRateCategory(String dataset) {
		try {
			
				
//			String req_fetchRateCategory = WSClient.createSOAPMessage("FetchRateCategory", dataset);
//			String res_fetchRateCategory = WSClient.processSOAPMessage(req_fetchRateCategory);
//			if(WSAssert.assertIfElementExists(res_fetchRateCategory, "HotelRateCategories_RateCategories_RateCategory_RateCategory", true)){
//				
//				WSClient.writeToReport(LogStatus.INFO,"RateCategory already exist");
//				return true;
//			}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Rate Category</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateRateCategory", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Rate Category " + WSClient.getData("{var_RateCategory}") +" already exists");
				return true;
			}
			else
			{
				String req_createRateCategory = WSClient.createSOAPMessage("CreateRateCategory", dataset);
				String res_createRateCategory = WSClient.processSOAPMessage(req_createRateCategory);
				if(WSAssert.assertIfElementExists(res_createRateCategory, "CreateRateCategoryRS_Success", true)){
					WSClient.writeToReport(LogStatus.PASS, "//CreateRateCategoryRS/Success exists on the response message");
					query = WSClient.getQuery("CreateRateCategory", "QS_01");
					dbResult = WSClient.getDBRow(query);
					val = dbResult.get("COUNT");
					if(WSAssert.assertEquals("1", val, true)) {
						WSClient.writeToReport(LogStatus.INFO, "New Rate Category has been created");
						return true;
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "Rate Category not created");
						return false;
					}
				}
				else{
					if(WSAssert.assertIfElementExists(res_createRateCategory, "CreateCashiersRS_Errors_Error_ShortText", false)) {
						WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createRateCategory, "CreateCashiersRS_Errors_Error_ShortText", XMLType.RESPONSE));
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
	
	
	@Test(groups= {"createRateCategory"}, dependsOnGroups = {"createRateClass"})
	public void createMultipleRateCategory() {
		int i;
		boolean flag = true;
		String testName = "CreateRateCategory";
		WSClient.startTest(testName, "Create Rate Category", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("RateCategory") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","RateCategory");
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
			String value = OperaPropConfig.getDataSetForCode("RateCategory" , dataset);
			String val_rateClass = OperaPropConfig.getDataSetForCode("RateClass" , dependencies.get("RateClass"));
			WSClient.setData("{var_RateCategory}", value);
			WSClient.setData("{var_RateClass}", val_rateClass);
			flag = flag && createRateCategory(dataset);
			dependencies.clear();
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("RateCategory", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("RateCategory", "N");
	}	

}
