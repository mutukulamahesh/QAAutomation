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

public class CreateTemplateReservationType extends WSSetUp{
	public boolean createTemplateReservationType(String dataset) {
		try {
			
			
//			String req_fetchTemplateReservationType = WSClient.createSOAPMessage("FetchTemplateGuaranteeCodes", "DS_01");
//			String res_fetchTemplateReservationType = WSClient.processSOAPMessage(req_fetchTemplateReservationType);
//			
//			if(WSAssert.assertIfElementExists(res_fetchTemplateReservationType, "FetchTemplateGuaranteeCodesRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(res_fetchTemplateReservationType, "FetchTemplateGuaranteeCodesRS_GuaranteeCodes_GuaranteeCode_GuaranteeCode", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "Template Reservation Type already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Template for Reservation Type</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateTemplateGuaranteeCode", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Template for Reservation Type " + WSClient.getData("{var_RevType}") +" already exists");
				return true;
			}
			
				else {
					WSClient.writeToReport(LogStatus.INFO, "Template ReservationType does not  exist!!");
					String req_createTemplateReservationType = WSClient.createSOAPMessage("CreateTemplateGuaranteeCode", dataset);
					String res_createTemplateReservationType = WSClient.processSOAPMessage(req_createTemplateReservationType);
					if(WSAssert.assertIfElementExists(res_createTemplateReservationType, "CreateTemplateGuaranteeCodeRS_Success", true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreateTemplateGuaranteeCodeRS/Success exists on the response message");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully created Template Reservation Type");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "New CashiersID not created");
							return false;
						}
					}
					else
					{
						if(WSAssert.assertIfElementExists(res_createTemplateReservationType, "CreateTemplateGuaranteeCodeRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createTemplateReservationType, "CreateTemplateGuaranteeCodeRS_Errors_Error_ShortText", XMLType.RESPONSE));
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
	
	
	@Test(groups= {"OperaConfig", "createTemplateReservationType"})
	public void createMultiple_TemplateReservationType() {
		int i;
		boolean flag = true;
		String testName = "createTemplateReservationType";
		WSClient.startTest(testName, "Create Template for Reservation Type", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		WSClient.setData("{var_chain}", OPERALib.getChain());
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("ReservationType") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("ReservationType" , dataset);
			WSClient.setData("{var_RevType}", value);
			flag = flag && createTemplateReservationType(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("TemplateReservationType", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("TemplateReservationType", "N");
	}

}
