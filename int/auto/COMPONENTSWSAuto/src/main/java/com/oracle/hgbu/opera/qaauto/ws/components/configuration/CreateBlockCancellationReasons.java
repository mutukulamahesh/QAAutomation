package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateBlockCancellationReasons extends WSSetUp{
	public boolean createBlockCancellationReason(String dataset) {
		try {
			
			String req_fetchCancelReasons = WSClient.createSOAPMessage("FetchBlockCancellationReasons", dataset);
			String res_fetchCancelReasons = WSClient.processSOAPMessage(req_fetchCancelReasons);
			if(WSAssert.assertIfElementExists(res_fetchCancelReasons, "FetchBlockCancellationReasonsRS_Success", false)) {
				if(WSAssert.assertIfElementExists(res_fetchCancelReasons, "BlockCancellationReasons_BlockCancellationReason_Code", true)) {
					WSClient.writeToReport(LogStatus.INFO, "Block Cancellation Reason already exists");
					return true;
				}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Block Cancellation Reason doesnot exist!!");
					String req_createCancelReasons = WSClient.createSOAPMessage("CreateBlockCancellationReasons", dataset);
					String res_createCancelReasons = WSClient.processSOAPMessage(req_createCancelReasons);
					if(WSAssert.assertIfElementExists(res_createCancelReasons, "CreateBlockCancellationReasonsRS_Success", false)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Block Cancellation Reason");
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
	
	
	@Test(groups= {"OperaConfig", "createBlockCancelReasons"})
	public void createMultiple_BlockCancellationReasons() {
		int i;
		boolean flag = true;
		String testName = "Create Block Cancellation Reasons";
		WSClient.startTest(testName, "Create Block Cancellation Reasons", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("BlockCancellationReason") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;
			String value = OperaPropConfig.getDataSetForCode("BlockCancellationReason" , dataset);
			WSClient.setData("{var_BlockCancelReason}", value);
			flag = flag && createBlockCancellationReason(dataset);
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("BlockCancellationReason", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("BlockCancellationReason", "N");
	}

}
