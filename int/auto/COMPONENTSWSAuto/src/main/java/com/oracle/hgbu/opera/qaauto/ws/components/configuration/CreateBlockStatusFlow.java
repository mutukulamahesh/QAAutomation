package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateBlockStatusFlow extends WSSetUp{
	
	public boolean createBlockStatusFlow() {
		try {
			
					String req_createBlockFlow = WSClient.createSOAPMessage("ChangeNextBlockStatusCodes", "DS_01");
					String res_createBlockFlow = WSClient.processSOAPMessage(req_createBlockFlow);
					if(WSAssert.assertIfElementExists(res_createBlockFlow, "ChangeNextBlockStatusCodesRS_Success", false)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Status Flow");
						return true;
					}
					else {
						WSClient.writeToReport(LogStatus.INFO, "Unable to Status Flow");
						return false;
					}
	}
		catch(Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	
	
	@Test(groups= {"OperaConfig"}, dependsOnGroups= {"BlockStatusCode"})
	public void createMultiple_StatusCodes() {
		int i;
		boolean flag = true;
		String testName = "CreateBlockStatusFlow";
		WSClient.startTest(testName, "Create Block Status Flow", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("BlockStatusCodes") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;
			
			WSClient.setData("{var_Code"+ i+ "}", OperaPropConfig.getDataSetForCode("BlockStatusCodes" , dataset));
		}
		flag = flag && createBlockStatusFlow();
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("BlockStatusFlow", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("BlockStatusFlow", "N");
	}

}
