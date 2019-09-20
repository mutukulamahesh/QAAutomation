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

public class CreateTransactionCodes extends WSSetUp {

	public boolean createTransactionCode(String dataSet) {

		try {
			// String fetchTCReq =
			// WSClient.createSOAPMessage("FetchTransactionCodes", dataSet);
			// String fetchTCRes = WSClient.processSOAPMessage(fetchTCReq);
			//
			// if(WSAssert.assertIfElementExists(fetchTCRes,
			// "FetchTransactionCodesRS_Success", false))
			// {
			// if(WSAssert.assertIfElementExists(fetchTCRes,
			// "FetchTransactionCodesRS_TransactionCodes", false))
			// {
			// if(WSAssert.assertIfElementExists(fetchTCRes,
			// "FetchTransactionCodesRS_TransactionCodes_TransactionCode_Code",
			// true))
			// {
			// WSClient.writeToReport(LogStatus.INFO, "Transaction Code
			// "+WSClient.getElementValue(fetchTCRes,
			// "FetchTransactionCodesRS_TransactionCodes_TransactionCode_Code",
			// XMLType.RESPONSE)+" already exists");
			// WSClient.writeToReport(LogStatus.INFO, "Transaction Code already
			// exists.");
			// return true;
			// }
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Transaction Code</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateTransactionCodes", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if (WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Transaction Code " + WSClient.getData("{var_TransactionCode}") + " already exists");
				return true;
			} else {
				WSClient.writeToReport(LogStatus.INFO, "Transaction Code doesn't exist.");
				String createTransactionCodeReq = WSClient.createSOAPMessage("CreateTransactionCodes", dataSet);
				String createTransactionCodeRes = WSClient.processSOAPMessage(createTransactionCodeReq);
				if (WSAssert.assertIfElementExists(createTransactionCodeRes, "CreateTransactionCodesRS_Success", true)) {
					WSClient.writeToReport(LogStatus.PASS, "//CreateTransactionCodesRS/Success exists on the response message");
					dbResult = WSClient.getDBRow(query);
					val = dbResult.get("COUNT");
					if (WSAssert.assertEquals("1", val, false)) {
						WSClient.writeToReport(LogStatus.INFO, "Transaction Code has been created");
						return true;
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Transaction Code not created");
						return false;
					}

				} else {
					if (WSAssert.assertIfElementExists(createTransactionCodeRes, "CreateTransactionCodesRS_Errors_Error_ShortText", false)) {
						WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createTransactionCodeRes, "CreateTransactionCodesRS_Errors_Error_ShortText", XMLType.RESPONSE));
					}
					return false;

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Error is " + e);
			e.printStackTrace();
			return false;
		}

	}

	public boolean createTransactionCodeGenerates() {

		try {
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Transaction Code Generates</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateTransactionGenerates", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if (WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Transaction Code " + WSClient.getData("{var_TransactionCode}") + " already has generates");
				return true;
			} else {

				WSClient.writeToReport(LogStatus.INFO, "Transaction Code doesn't exist.");
				String createTransactionCodeReq = WSClient.createSOAPMessage("CreateTransactionGenerates", "DS_01");
				String createTransactionCodeRes = WSClient.processSOAPMessage(createTransactionCodeReq);
				if (WSAssert.assertIfElementExists(createTransactionCodeRes, "CreateTransactionGeneratesRS_Success", true)) {
					WSClient.writeToReport(LogStatus.PASS, "//CreateTransactionGeneratesRS/Success exists on the response message");
					dbResult = WSClient.getDBRow(query);
					val = dbResult.get("COUNT");
					if (WSAssert.assertEquals("1", val, false)) {
						WSClient.writeToReport(LogStatus.INFO, "Transaction Code Generates has been created");
						return true;
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Transaction Code Generates has not been created");
						return false;
					}

				} else {
					if (WSAssert.assertIfElementExists(createTransactionCodeRes, "CreateTransactionCodesRS_Errors_Error_ShortText", false)) {
						WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createTransactionCodeRes, "CreateTransactionCodesRS_Errors_Error_ShortText", XMLType.RESPONSE));
					}
					return false;

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Error is " + e);
			e.printStackTrace();
			return false;
		}

	}

	@Test(groups = { "createTransactionCodes", "bat" }, dependsOnGroups = { "createTemplateTransactionCodes" })
	public void createTransactionCodes() {
		try {
			String testName = "createTransactionCodes";
			WSClient.startTest(testName, "Creating a transaction code", "OperaConfig");
			boolean flag = true;
			String ds, TSG, TG, tCode;
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_Resort}", OPERALib.getResort());
			int i;
			String dataset = "";
			int length = OperaPropConfig.getLengthForCode("TransactionCode") - 1;
			HashMap<String, String> dependencies = new HashMap<String, String>();
			int row = OperaPropConfig.getRowIndex("OperaConfig", "TransactionCode");
			for (i = 1; i <= length; i++) {
				if (i <= 9)
					dataset = "DS_0" + i;
				else
					dataset = "DS_" + i;
				String comm = OperaPropConfig.getCellComment("OperaConfig", row, i);
				// System.out.println(comm);
				if (comm == null) {
					WSClient.writeToReport(LogStatus.WARNING, "Please provide dependencies");
				}
				dependencies = OperaPropConfig.getDependency(comm);
				TG = OperaPropConfig.getDataSetForCode("TransactionGroup", dependencies.get("TransactionGroup"));
				WSClient.setData("{var_TransactionGroup}", TG);
				TSG = OperaPropConfig.getDataSetForCode("TransactionSubGroup", dependencies.get("TransactionSubGroup"));
				WSClient.setData("{var_TransactionSubGroup}", TSG);
				tCode = OperaPropConfig.getDataSetForCode("TransactionCode", dataset);
				WSClient.setData("{var_TransactionCode}", tCode);
				flag = flag && createTransactionCode(dataset);
				String tax = OperaPropConfig.getDataSetForCode("TransactionCode", dependencies.get("TransactionCode"));
				WSClient.setData("{var_taxCode}", tax);
				dependencies.clear();
			}

			flag = flag && createTransactionCodeGenerates();
			if (flag == true)
				OperaPropConfig.setPropertyConfigResults("TransactionCode", "Y");
			else
				OperaPropConfig.setPropertyConfigResults("TransactionCode", "N");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Error is " + e);
			e.printStackTrace();
		}
	}
}
