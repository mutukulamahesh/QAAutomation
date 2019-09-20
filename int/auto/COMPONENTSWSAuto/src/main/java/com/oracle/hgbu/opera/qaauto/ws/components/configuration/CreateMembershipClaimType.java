package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateMembershipClaimType extends WSSetUp{
	
	public boolean createClaimType(String dataset) {
		try {
			
			String req_fetchClaimType = WSClient.createSOAPMessage("FetchMembershipClaimTypes", "DS_01");
			String res_fetchClaimType = WSClient.processSOAPMessage(req_fetchClaimType);
			if(WSAssert.assertIfElementExists(res_fetchClaimType, "FetchMembershipClaimTypesRS_Success", false)) {
				if(WSAssert.assertIfElementExists(res_fetchClaimType, "MembershipClaimTypes_MembershipClaimType_Code", true)) {
					WSClient.writeToReport(LogStatus.INFO, "Membership Claim Type already exists");
					return true;
				}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Membership Claim Type doesnot exist!!");
					String req_createEvtCodes = WSClient.createSOAPMessage("CreateMembershipClaimTypes", dataset);
					String res_createEvtCodes = WSClient.processSOAPMessage(req_createEvtCodes);
					if(WSAssert.assertIfElementExists(res_createEvtCodes, "CreateMembershipClaimTypesRS_Success", false)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Membership Claim Type");
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
	public void createMultiple_ClaimTypes() {
		int i;
		boolean flag = true;
		String testName = "CreateMembershipClaimTypes";
		WSClient.startTest(testName, "Create Membership Claim Type", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("MClaimType") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("MClaimType" , dataset);
			WSClient.setData("{var_MClaimType}", value);
			flag = flag && createClaimType(dataset);
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("MClaimType", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("MClaimType", "N");
	}
}
