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

public class CreatePromotionCode extends WSSetUp {
	public boolean createPromotionCode(String dataset){
		try{
			
//			String fetchPromoCodeReq=WSClient.createSOAPMessage("FetchPromotionCodes", dataset);
//			String fetchPromoCodeRes=WSClient.processSOAPMessage(fetchPromoCodeReq);
//			if(WSAssert.assertIfElementExists(fetchPromoCodeRes, "FetchPromotionCodesRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(fetchPromoCodeRes, "PropertyPromotionCodes_PropertyPromotionCodes_PropertyPromotionCode_PromotionCode", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "Promotion Code already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Promotion Code</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreatePromotionCode", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Promotion Code " + WSClient.getData("{var_PromoCode}") +" already exists");
				return true;
			}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Promotion Code doesnot exist!!");
					String createPromoCodeReq=WSClient.createSOAPMessage("CreatePromotionCode", dataset);
					String createPromoCodeRes=WSClient.processSOAPMessage(createPromoCodeReq);
					if(WSAssert.assertIfElementExists(createPromoCodeRes, "CreatePromotionCodeRS_Success", true)){
						WSClient.writeToReport(LogStatus.PASS, "//CreatePromotionCodeRS/Success exists on the response message");
						query = WSClient.getQuery("CreatePromotionCode", "QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully Created Promotion Code");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Promotion Code not created");
							return false;
						}
					}
					else{
						if(WSAssert.assertIfElementExists(createPromoCodeRes, "CreatePromotionCodeRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createPromoCodeRes, "CreatePromotionCodeRS_Errors_Error_ShortText", XMLType.RESPONSE));
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

	@Test(groups= {"OperaConfig"} ,dependsOnGroups= {"createPromotionGroup", "createRateCode"})
	public void createMultiple_PromotionCodes() {
		int i;
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String testName="PromotionCodes";
		WSClient.startTest(testName, "Create Promotion Code", "OperaConfig");
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("PromotionCode") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","PromotionCode");
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
			String value = OperaPropConfig.getDataSetForCode("PromotionCode" , dataset);
			String val_group = OperaPropConfig.getDataSetForCode("PromotionGroup", dependencies.get("PromotionGroup"));
			WSClient.setData("{var_RateCode}", OperaPropConfig.getDataSetForCode("RateCode" , dependencies.get("RateCode")));
			WSClient.setData("{var_PromoCode}", value);
			WSClient.setData("{var_PromoGroup}", val_group);
			flag = flag && createPromotionCode(dataset);
			dependencies.clear();
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("PromotionCode", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("PromotionCode", "N");
}

}
