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


public class ReservationType extends WSSetUp {

	
	public boolean createReservationType(String dataset) {
		try {
			
			
//			String req_fetchreservationtype = WSClient.createSOAPMessage("FetchGuaranteeCodes", dataset);
//			String res_fetchresevationtype = WSClient.processSOAPMessage(req_fetchreservationtype);
//			
//			if(WSAssert.assertIfElementExists(res_fetchresevationtype, "FetchGuaranteeCodesRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(res_fetchresevationtype, "FetchGuaranteeCodesRS_GuaranteeCodes_GuaranteeCode", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "ReservationType already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Reservation Type</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateGuaranteeCode", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Reservation Type " + WSClient.getData("{var_RevType}") +" already exists");
				return true;
			}
			
				else {
					WSClient.writeToReport(LogStatus.INFO, "ReservationType does not  exist!!");
					String req_createreservationtype = WSClient.createSOAPMessage("CreateGuaranteeCode", dataset);
					String res_createreservationtype = WSClient.processSOAPMessage(req_createreservationtype);
					if(WSAssert.assertIfElementExists(res_createreservationtype, "CreateGuaranteeCodeRS_Success", true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreateGuaranteeCodeRS/Success exists on the response message");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully created Reservation Type");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Reservation Type not created");
							return false;
						}
					}
					else{
						if(WSAssert.assertIfElementExists(res_createreservationtype, "CreateGuaranteeCodeRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createreservationtype, "CreateGuaranteeCodeRS_Errors_Error_ShortText", XMLType.RESPONSE));
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
	
	
	@Test(groups= {"OperaConfig", "createReservationType"}, dependsOnGroups = {"createTemplateReservationType"})
	public void createMultiple_ReservationType() {
		int i;
		boolean flag = true;
		String testName = "createReservationType";
		WSClient.startTest(testName, "Create Reservation Type", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("ReservationType") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("ReservationType" , dataset);
			WSClient.setData("{var_RevType}", value);
			flag = flag && createReservationType(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("ReservationType", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("ReservationType", "N");
	}
}

















