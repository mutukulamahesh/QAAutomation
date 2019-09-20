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

public class IdentificationType extends WSSetUp {

	/**
	 * @author psarawag 
	 * Description: Method to create two document types.
	 */
	boolean createIdentificationType(String dataset) {
		try {
	
//			String req_fetchIdentification = WSClient.createSOAPMessage("FetchIdentificationTypes", dataset);
//			String res_fetchIdentification = WSClient.processSOAPMessage(req_fetchIdentification);
//			
//			if(WSAssert.assertIfElementExists(res_fetchIdentification, "FetchIdentificationTypesRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(res_fetchIdentification, "IdentificationTypes_IdentificationType_Code", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "Identification type already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Identification Type</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateIdentificationTypes", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Identification Type " + WSClient.getData("{var_Cashier}") +" already exists");
				return true;
			}
			
				else{
					WSClient.writeToReport(LogStatus.INFO, "Identification Type Code doesnot exist!!");
					String req_createIdentification = WSClient.createSOAPMessage("CreateIdentificationTypes", dataset);
					String res_createIdentification = WSClient.processSOAPMessage(req_createIdentification);
					if(WSAssert.assertIfElementExists(res_createIdentification, "CreateIdentificationTypesRS_Success", true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreateIdentificationTypesRS/Success exists on the response message");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Identification type");
						return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Identification Type not created");
							return false;
				}
			}
			else {
				if(WSAssert.assertIfElementExists(res_createIdentification, "CreateIdentificationTypesRS_Errors_Error_ShortText", false)) {
					WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createIdentification, "CreateIdentificationTypesRS_Errors_Error_ShortText", XMLType.RESPONSE));
				}
				return false;
			}
		}
			
		}catch(Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, e.toString());
		return false;
		}
	}
	@Test(groups= {"OperaConfig"})
	public void createMultiple_IdentificationType() {
		int i;
		boolean flag = true;
		String testName = "CreateIdentificationType";
		WSClient.startTest(testName, "Create Identification Type", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		WSClient.setData("{var_chain}", OPERALib.getChain());
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("IdentificationType") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("IdentificationType" , dataset);
			WSClient.setData("{var_IdentificationType}", value);
			flag = flag && createIdentificationType(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("IdentificationType", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("IdentificationType", "N");
	}

}
