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


public class AddressType extends WSSetUp{


	public boolean createAddressType(String dataset){
		try{
			//
			//			String fetchAddressTypeReq=WSClient.createSOAPMessage("FetchAddressTypes", dataset);
			//			String fetchAddressTypeRes=WSClient.processSOAPMessage(fetchAddressTypeReq);
			//			if(WSAssert.assertIfElementExists(fetchAddressTypeRes, "FetchAddressTypesRS_Success", false)) {
			//				if(WSAssert.assertIfElementExists(fetchAddressTypeRes, "AddressTypes_AddressType_Code", true)) {
			//					WSClient.writeToReport(LogStatus.INFO, "Address Type already exists");
			//					return true;
			//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Address Type</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateAddressTypes", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Address Type " + WSClient.getData("{var_AddressType}") +" already exists");
				return true;
			}
			else {
				WSClient.writeToReport(LogStatus.INFO, "Address code doesnot exist!!");
				String createAddressTypeReq=WSClient.createSOAPMessage("CreateAddressTypes", dataset);
				String createAddressTypeRes=WSClient.processSOAPMessage(createAddressTypeReq);
				if(WSAssert.assertIfElementExists(createAddressTypeRes, "CreateAddressTypesRS_Success", true)){
					WSClient.writeToReport(LogStatus.PASS, "//CreateAddressTypesRS/Success exists on the response message");

					//DB Validation
					dbResult = new LinkedHashMap<String, String>();
					query = WSClient.getQuery("QS_01");
					dbResult = WSClient.getDBRow(query);
					val = dbResult.get("COUNT");
					if(WSAssert.assertEquals("1", val, true)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully Created Address Type");
						return true;
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "Address Type not created");
						return false;
					}
				}
				else{
					if(WSAssert.assertIfElementExists(createAddressTypeRes, "CreateAddressTypesRS_Errors_Error_ShortText", false)) {
						WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createAddressTypeRes, "CreateAddressTypesRS_Errors_Error_ShortText", XMLType.RESPONSE));
					}
					return false;
				}
			}

		}

		catch (Exception e) {
			e.printStackTrace();
			WSClient.writeToReport(LogStatus.ERROR, "Exception ocuured due to :" +e);
			return false;
		}
	}

	@Test(groups= {"OperaConfig"})
	public void createMultiple_AddressType() {
		int i;
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String testName="AddressType";
		String dataset = "";
		WSClient.startTest(testName, "Create Address Type", "OperaConfig");
		WSClient.setData("{var_Chain}", OPERALib.getChain());
		int length = OperaPropConfig.getLengthForCode("AddressType") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i;
			else
				dataset = "DS_" + i;
			String value = OperaPropConfig.getDataSetForCode("AddressType" , dataset);
			WSClient.setData("{var_AddressType}", value);
			flag = flag && createAddressType(dataset);
		}

		if(flag == true)
			OperaPropConfig.setPropertyConfigResults("AddressType", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("AddressType", "N");
	}
}