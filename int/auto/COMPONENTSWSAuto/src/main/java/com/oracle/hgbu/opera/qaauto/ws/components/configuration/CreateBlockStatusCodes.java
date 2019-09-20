package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateBlockStatusCodes extends WSSetUp {
	public boolean createBlockStatusCode(String dataset) {
		try {
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Block Status Code</b>----------");
			String req_fetchStatusCodes = WSClient.createSOAPMessage("FetchBlockStatusCodes", dataset);
			String res_fetchStatusCodes = WSClient.processSOAPMessage(req_fetchStatusCodes);
			if(WSAssert.assertIfElementExists(res_fetchStatusCodes, "FetchBlockStatusCodesRS_Success", false)) {
				if(WSAssert.assertIfElementExists(res_fetchStatusCodes, "BlockStatusCodeList_BlockStatusCode_Status_Code", true)) {
					WSClient.writeToReport(LogStatus.INFO, "Status Code already exists");
					return true;
				}
			
//			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
//			String query = WSClient.getQuery("CreateBlockStatusCode", "QS_01");
//			dbResult = WSClient.getDBRow(query);
//			String val = dbResult.get("COUNT");
//			if(WSAssert.assertEquals("1", val, true)) {
//				WSClient.writeToReport(LogStatus.INFO, "Status code " + WSClient.getData("{var_StatusCode}") +" already exists");
//				return true;
//			}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Status Code doesnot exist!!");
					String req_createStatusCodes = WSClient.createSOAPMessage("CreateBlockStatusCode", dataset);
					String res_createStatusCodes = WSClient.processSOAPMessage(req_createStatusCodes);
					if(WSAssert.assertIfElementExists(res_createStatusCodes, "CreateBlockStatusCodeRS_Success", true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreateBlockStatusCodeRS/Success exists on the response message");
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Status Code");
						return true;
//						dbResult = new LinkedHashMap<String, String>();
//						query = WSClient.getQuery("CreateBlockStatusCode", "QS_01");
//						dbResult = WSClient.getDBRow(query);
//						val = dbResult.get("COUNT");
//						if(WSAssert.assertEquals("1", val, true)) {
//							WSClient.writeToReport(LogStatus.INFO, "Successfully created Status Code");
//							return true;
//						}
//						else {
//							WSClient.writeToReport(LogStatus.WARNING, "Status Code not created");
//							return false;
//						}
					}
					else {
						if(WSAssert.assertIfElementExists(res_createStatusCodes, "CreateBlockStatusCodeRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createStatusCodes, "CreateBlockStatusCodeRS_Errors_Error_ShortText", XMLType.RESPONSE));
							return false;
						}
					}
			}
				return false;
	}
			return false;
	}
			
		
		catch(Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	
	
	@Test(groups= {"OperaConfig", "BlockStatusCode"}, dependsOnGroups= {"createBlockCancelReasons"})
	public void createMultiple_StatusCodes() {
		int i;
		boolean flag = true;
		String testName = "CreateBlockStatusCode";
		WSClient.startTest(testName, "Create Block Status Code", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("BlockStatusCodes") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","BlockStatusCodes");
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;
			String comm = OperaPropConfig.getCellComment("OperaConfig",row, i);
			if(comm!=null) {
				dependencies = OperaPropConfig.getDependency(comm);
				WSClient.setData("{var_CancelReason}", OperaPropConfig.getDataSetForCode("BlockCancellationReason", dependencies.get("BlockCancellationReason")));
			}
			String value = OperaPropConfig.getDataSetForCode("BlockStatusCodes" , dataset);
			WSClient.setData("{var_StatusCode}", value);
			flag = flag && createBlockStatusCode(dataset);
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("BlockStatusCodes", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("BlockStatusCodes", "N");
	}

}
