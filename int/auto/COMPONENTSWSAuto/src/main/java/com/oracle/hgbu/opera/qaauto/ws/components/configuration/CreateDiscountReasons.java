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

public class CreateDiscountReasons extends WSSetUp {

	

	public boolean createDiscountReasons(String dataset){
		try{
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Discount Reasons</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateDiscountReasons", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Discount Reasons " + WSClient.getData("{var_code}") +" already exists");
				return true;
			}
			else {
				WSClient.writeToReport(LogStatus.INFO, "Discount Reasons doesnot exist!!");
				String createDiscountReasonsReq=WSClient.createSOAPMessage("CreateDiscountReasons", dataset);
				String createDiscountReasonsRes=WSClient.processSOAPMessage(createDiscountReasonsReq);
				if(WSAssert.assertIfElementExists(createDiscountReasonsRes, "CreateDiscountReasonsRS_Success", true)){
					WSClient.writeToReport(LogStatus.PASS, WSClient.getResponseXPath("CreateDiscountReasonsRS_Success")+" exists on the response message");
						
						//DB Validation
						dbResult = new LinkedHashMap<String, String>();
						query = WSClient.getQuery("QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully Created Discount Reasons");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Discount Reasons not created");
							return false;
						}
					}
					else{
						if(WSAssert.assertIfElementExists(createDiscountReasonsRes, "CreateDiscountReasonsRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createDiscountReasonsRes, "CreateDiscountReasonsRS_Errors_Error_ShortText", XMLType.RESPONSE));
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

	@Test(groups= {"OperaConfig"})
	public void createMultiple_DiscountReasons() {
		int i;
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		WSClient.setData("{var_chain}", OPERALib.getChain());
		String testName="DiscountReasons";
		WSClient.startTest(testName, "Create Discount Reasons", "OperaConfig");
		int length = OperaPropConfig.getLengthForCode("DiscountReasons") - 1;
		String dataset = "";
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;  
			String value = OperaPropConfig.getDataSetForCode("DiscountReasons" , dataset);
			WSClient.setData("{var_code}", value);
			flag = flag && createDiscountReasons(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("DiscountReasons", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("DiscountReasons", "N");
}
}
