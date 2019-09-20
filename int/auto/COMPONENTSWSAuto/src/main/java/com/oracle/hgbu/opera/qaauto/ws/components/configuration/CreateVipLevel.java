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


public class CreateVipLevel extends WSSetUp {

	
	public boolean createVIPLevel(String dataset) {
		try {
			
//			String req_fetchVIP = WSClient.createSOAPMessage("FetchVIPLevels", dataset);
//			String res_fetchVIP = WSClient.processSOAPMessage(req_fetchVIP);
//			if(WSAssert.assertIfElementExists(res_fetchVIP, "FetchVIPLevelsRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(res_fetchVIP, "VIPLevels_VIPLevel_Code", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "VIP Level already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Vip Level</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateVIPLevels", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "VIP level " + WSClient.getData("{var_VipLevel}") +" already exists");
				return true;
			}
				else {
					WSClient.writeToReport(LogStatus.INFO, "VIP Level Code doesnot exist!!");
					String req_createVIP = WSClient.createSOAPMessage("CreateVIPLevels", dataset);
					String res_createVIP = WSClient.processSOAPMessage(req_createVIP);
					if(WSAssert.assertIfElementExists(res_createVIP, "CreateVIPLevelsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreateVIPLevelsRS/Success exists on the response message");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully created VIP Level");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "VIP Level not created");
							return false;
						}
					}
					else{
						if(WSAssert.assertIfElementExists(res_createVIP, "CreateVIPLevelsRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createVIP, "CreateVIPLevelsRS_Errors_Error_ShortText", XMLType.RESPONSE));
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
	public void createMultiple_VIPLevel() {
		int i;
		boolean flag = true;
		String testName = "CreateVIPLevel";
		WSClient.startTest(testName, "Create VIP Level", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		WSClient.setData("{var_chain}", OPERALib.getChain());
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("VipLevel") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("VipLevel" , dataset);
			WSClient.setData("{var_VipLevel}", value);
			flag = flag && createVIPLevel(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("VipLevel", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("VipLevel", "N");
	}
}
