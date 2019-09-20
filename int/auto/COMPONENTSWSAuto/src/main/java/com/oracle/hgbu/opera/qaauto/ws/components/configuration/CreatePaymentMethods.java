package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreatePaymentMethods extends WSSetUp {
	
	public boolean create_Payment_Method(String dataset) {
		try {
		
			
//			String req_fetchPaymentMethods = WSClient.createSOAPMessage("CashieringConfigFetchPaymentMethods", dataset);
//			String res_fetchPaymentMethods = WSClient.processSOAPMessage(req_fetchPaymentMethods);
//			if(WSAssert.assertIfElementExists(res_fetchPaymentMethods, "FetchPaymentMethodsRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(res_fetchPaymentMethods, "FetchPaymentMethodsRS_PaymentMethods_PaymentMethod_PaymentMethod", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "Payment Method already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Payment Method</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CashieringConfigCreatePaymentMethods", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Payment Method " + WSClient.getData("{var_PaymentMethod}") +" already exists");
				return true;
			}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Payment Method doesnot exist!!");
					String req_createPaymentMethods = WSClient.createSOAPMessage("CashieringConfigCreatePaymentMethods", dataset);
					String res_createPaymentMethods = WSClient.processSOAPMessage(req_createPaymentMethods);
					if(WSAssert.assertIfElementExists(res_createPaymentMethods, "CreatePaymentMethodsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreatePaymentMethodsRS/Success exists on the response message");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, false)) {
							WSClient.writeToReport(LogStatus.INFO, "New Payment Method has been created");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "New Payment Method not created");
							return false;
						}
							
					}
					else{
						if(WSAssert.assertIfElementExists(res_createPaymentMethods, "CreatePaymentMethodsRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createPaymentMethods, "CreatePaymentMethodsRS_Errors_Error_ShortText", XMLType.RESPONSE));
						}
						return false;
					}
			}	
		
	}
		catch(Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	@Test(groups= {"OperaConfig"} , dependsOnGroups = {"createTransactionCodes", "createTransactionSubgroups", "createTransactionGroups"})
	public void createMultiple_PaymentMethods() {
		int i;
		boolean flag = true;
		String testName = "CreatePaymentMethods";
		WSClient.startTest(testName, "Create Payment methods", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("PaymentMethod") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","PaymentMethod");
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
			String value = OperaPropConfig.getDataSetForCode("PaymentMethod" , dataset);
			WSClient.setData("{var_PaymentMethod}", value);
			String val_tcode = OperaPropConfig.getDataSetForCode("TransactionCode" , dependencies.get("TransactionCode"));
			String val_tgroup = OperaPropConfig.getDataSetForCode("TransactionGroup" , dependencies.get("TransactionGroup"));
			String val_tsubgroup = OperaPropConfig.getDataSetForCode("TransactionSubGroup" , dependencies.get("TransactionSubGroup"));
			WSClient.setData("{var_TransactionCode}", val_tcode);
			WSClient.setData("{var_TransactionGroup}", val_tgroup);
			WSClient.setData("{var_TransactionSubGroup}", val_tsubgroup);
			flag = flag && create_Payment_Method(dataset);
			dependencies.clear();
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("PaymentMethod", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("PaymentMethod", "N");
	}

}
