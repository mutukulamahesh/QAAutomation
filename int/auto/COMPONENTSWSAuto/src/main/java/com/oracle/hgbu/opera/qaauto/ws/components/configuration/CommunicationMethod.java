package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CommunicationMethod extends WSSetUp {
	
	public boolean createCommunicationMethod(String dataset) {

		try {
			
			
			String fetchCommMethodReq = WSClient.createSOAPMessage("FetchCommunicationMethodsEntDetails", dataset);
			String fetchCommMethodRes = WSClient.processSOAPMessage(fetchCommMethodReq);
			if(WSAssert.assertIfElementExists(fetchCommMethodRes, "FetchCommunicationMethodsEntDetailsRS_Success", false)) {
				if(WSAssert.assertIfElementExists(fetchCommMethodRes, "CommunicationMethodsEntDetails_CommunicationMethodsEntDetail_Code", true)) {
					WSClient.writeToReport(LogStatus.INFO, "Communication Method already exists");
					return true;
				}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Commnunication Method doesnot exist!!");
					String createCommMethodReq = WSClient.createSOAPMessage("CreateCommunicationMethodsEntDetails", dataset);
					String createCommMethodRes = WSClient.processSOAPMessage(createCommMethodReq);
					if (WSAssert.assertIfElementExists(createCommMethodRes, "CreateCommunicationMethodsEntDetailsRS_Success",false)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Communication Method");
						
						//DB Validation
						
						return true;
						
					}
					else
						return false;
				}
			}
			else
				return false;
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	
	@Test(groups= {"createCommunicationMethod"})
	public void createMultiple_CommunicationMethod() {
		int i;
		boolean flag = true;
		String testName = "CreateCommunicationMethod";
		WSClient.startTest(testName, "Create Communication Method", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		for(i=1;i<=2;i++) {
			String dataset = "DS_0" + i; 
			String value = OperaPropConfig.getDataSetForCode("CommunicationMethod" , dataset);
			
			WSClient.setData("{var_CommunicationMethod}", value);
			
			flag = flag && createCommunicationMethod(dataset);
		}
		

		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("CommunicationMethod", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("CommunicationMethod", "N");
	}


}
