package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;



public class CreateSourceCode extends WSSetUp {
	
	public boolean createSourceCode(String dataset) {
		try {
			
			String req_fetchSourceCode = WSClient.createSOAPMessage("FetchSourceCodes", dataset);
			String res_fetchSourceCode = WSClient.processSOAPMessage(req_fetchSourceCode);
			if(WSAssert.assertIfElementExists(res_fetchSourceCode, "FetchSourceCodesRS_Success", false)) {
				if(WSAssert.assertIfElementExists(res_fetchSourceCode, "FetchSourceCodesRS_SourceCodes_SourceCode_Code", true)) {
					WSClient.writeToReport(LogStatus.INFO, "Source Code already exists");
					return true;
				}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Source Code doesnot exist!!");
					String req_createSourceCode = WSClient.createSOAPMessage("CreateSourceCode", dataset);
					String res_createSourceCode = WSClient.processSOAPMessage(req_createSourceCode);
					if(WSAssert.assertIfElementExists(res_createSourceCode, "CreateSourceCodeRS_Success", false)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Source Code");
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
	
	
	@Test(groups = {"OperaConfig"}, dependsOnGroups = {"createSourceGroup"})
	public void createMultiple_SourceCode() {
		int i;
		boolean flag = true;
		String testName = "CreateSourceCode";
		WSClient.startTest(testName, "Create Source Codes", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("SourceCode") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","SourceCode");
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i; 
			String comm = OperaPropConfig.getCellComment("OperaConfig",row, i);
			//System.out.println(comm);
			if(comm==null) {
				WSClient.writeToReport(LogStatus.WARNING, "Please provide dependencies");
			}
			dependencies = OperaPropConfig.getDependency(comm);
			String value = OperaPropConfig.getDataSetForCode("SourceCode" , dataset);
			String val_srcgrp = OperaPropConfig.getDataSetForCode("SourceGroup" , dependencies.get("SourceGroup"));
			WSClient.setData("{var_SourceCode}", value);
			WSClient.setData("{var_SourceGroup}", val_srcgrp);
			flag = flag && createSourceCode(dataset);
			dependencies.clear();
		}
		

		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("SourceCode", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("SourceCode", "N");
	}
	
	
}