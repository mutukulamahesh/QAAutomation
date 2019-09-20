package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateNationality extends WSSetUp{
	
	public boolean createNationality(String dataset) {
		try {
			
			String req_fetchNationality = WSClient.createSOAPMessage("FetchNationalities", "DS_01");
			String res_fetchNationality = WSClient.processSOAPMessage(req_fetchNationality);
			if(WSAssert.assertIfElementExists(res_fetchNationality, "FetchNationalitiesRS_Success", false)) {
				if(WSAssert.assertIfElementExists(res_fetchNationality, "Nationalities_Nationality_Code", true)) {
					WSClient.writeToReport(LogStatus.INFO, "Nationality already exists");
					return true;
				}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Nationality doesnot exist!!");
					String req_createNationality = WSClient.createSOAPMessage("CreateNationalities", dataset);
					String res_createNationality = WSClient.processSOAPMessage(req_createNationality);
					if(WSAssert.assertIfElementExists(res_createNationality, "CreateNationalitiesRS_Success", false)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Nationality");
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
	public void createMultiple_Nationality() {
		int i;
		boolean flag = true;
		String testName = "CreateNationality";
		WSClient.startTest(testName, "Create Nationality", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("Nationality") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("Nationality" , dataset);
			WSClient.setData("{var_Nationality}", value);
			flag = flag && createNationality(dataset);
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("Nationality", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("Nationality", "N");
	}

}
