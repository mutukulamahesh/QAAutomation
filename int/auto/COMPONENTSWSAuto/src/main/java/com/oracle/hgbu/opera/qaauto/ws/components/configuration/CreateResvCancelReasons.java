package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateResvCancelReasons extends WSSetUp {
	
	public boolean createCancelReason(String dataset) {
		try {
			
			String req_fetchCancelReason = WSClient.createSOAPMessage("FetchCancellationCodes", dataset);
			String res_fetchCancelReason = WSClient.processSOAPMessage(req_fetchCancelReason);
			if(WSAssert.assertIfElementExists(res_fetchCancelReason, "FetchCancellationCodesRS_Success", false)) {
				if(WSAssert.assertIfElementExists(res_fetchCancelReason, "CancellationCodes_CancellationCode_Code", true)) {
					WSClient.writeToReport(LogStatus.INFO, "Reservation Cancel Reason already exists");
					return true;
				}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Reservation Cancel Reason doesnot exist!!");
					String req_createCancelReason = WSClient.createSOAPMessage("CreateCancellationCodes", dataset);
					String res_createCancelReason = WSClient.processSOAPMessage(req_createCancelReason);
					if(WSAssert.assertIfElementExists(res_createCancelReason, "CreateCancellationCodesRS_Success", false)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Reservation Cancel Reason");
						return true;
					}
					else
						return false;
			}	
		}
			else
				return false;
	}
		catch(Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	
	
	@Test(groups= {"OperaConfig"})
	public void createMultiple_CacnelReasons() {
		int i;
		boolean flag = true;
		String testName = "CreateResvCacnelReasons";
		WSClient.startTest(testName, "Create Reservation Cancel Reasons", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("ResvCancelReason") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("ResvCancelReason" , dataset);
			WSClient.setData("{var_CancelCode}", value);
			flag = flag && createCancelReason(dataset);
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("ResvCancelReason", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("ResvCancelReason", "N");
	}

}
