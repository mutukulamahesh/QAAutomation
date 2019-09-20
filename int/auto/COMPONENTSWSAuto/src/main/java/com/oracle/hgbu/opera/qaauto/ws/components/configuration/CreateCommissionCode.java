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

public class CreateCommissionCode extends WSSetUp{

	public boolean createCommissionCode(String dataset){
		try{
			
//			String fetchCommissionCodeReq=WSClient.createSOAPMessage("FetchCommissionCode", dataset);
//			String fetchCommissionCodeRes=WSClient.processSOAPMessage(fetchCommissionCodeReq);
//			if(WSAssert.assertIfElementExists(fetchCommissionCodeRes, "FetchCommissionCodeRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(fetchCommissionCodeRes, "CommissionCodeDetail_CommissionCodeInfo_CommissionCode", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "Commission Code already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Commission Code</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateCommissionCodes", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Commission Code " + WSClient.getData("{var_CommissionCode}") +" already exists");
				return true;
			}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Commission Code doesnot exist!!");
					String createCommissionCodeReq=WSClient.createSOAPMessage("CreateCommissionCodes", dataset);
					String createCommissionCodeRes=WSClient.processSOAPMessage(createCommissionCodeReq);
					if(WSAssert.assertIfElementExists(createCommissionCodeRes, "CreateCommissionCodesRS_Success", true)){
						WSClient.writeToReport(LogStatus.PASS, "//CreateCommissionCodesRS/Success exists on the response message");
						dbResult = new LinkedHashMap<String, String>();
						query = WSClient.getQuery("CreateCommissionCodes", "QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully Created Commission Code");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Commission Code not created");
							return false;
						}
					}
					else{
						if(WSAssert.assertIfElementExists(createCommissionCodeRes, "CreateCommissionCodesRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createCommissionCodeRes, "CreateCommissionCodesRS_Errors_Error_ShortText", XMLType.RESPONSE));
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

	@Test(groups= {"OperaConfig", "createCommissionCode"})
	public void createMultiple_CommissionCode() {
		int i;
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String testName="CommissionCode";
		WSClient.startTest(testName, "Create Commission Code", "OperaConfig");
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("CommissionCode") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("CommissionCode" , dataset);
			WSClient.setData("{var_CommissionCode}", value);
			flag = flag && createCommissionCode(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("CommissionCode", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("CommissionCode", "N");
}
}
