package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2008b.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchLOV extends WSSetUp {

	/**
	 * @author psarawag, nilsaini Prerequisite: FetchLOV details should be
	 *         updated in database Description: This test case is to check
	 *         whether VIPLEVEL LOV values are present in the database or not
	 *         and the response is fetching the details correctly or not.
	 */

	@Test(groups = { "sanity", "FetchLOV", "HTNG2008BExt", "HTNG" })
	public void fetchLOV_Ext_4532() {
		try {
			boolean blockedFlag = true;
			String testName = "fetchLOV_Ext_4532";
			WSClient.startTest(testName,
					"This test case is to check whether VIPLEVEL LOV values are present in the database or not and the response is fetching the details correctly or not.",
					"sanity");
			String prerequisite[] = { "VipLevel" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String fetchLOVReq = WSClient.createSOAPMessage("HTNGExtFetchLOV", "DS_01");
				String fetchLOVRes = WSClient.processSOAPMessage(fetchLOVReq);
				List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
				HashMap<String, String> xPath = new HashMap<String, String>();
				String query=WSClient.getQuery("QS_01");
				db = WSClient.getDBRows(query);
				if (db.isEmpty()) {
					blockedFlag = false;
				}
				if (blockedFlag && WSAssert.assertIfElementValueEquals(fetchLOVRes,
						"FetchLovResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					List<LinkedHashMap<String, String>> res = new ArrayList<LinkedHashMap<String, String>>();
					xPath.put("FetchLovResponse_LovQueryResult_LovValue", "FetchLovResponse_LovQueryResult_LovValue");
					xPath.put("FetchLovResponse_LovQueryResult_LovValue_description",
							"FetchLovResponse_LovQueryResult_LovValue");
					res = WSClient.getMultipleNodeList(fetchLOVRes, xPath, false, XMLType.RESPONSE);
					WSAssert.assertEquals(res, db, true);

				}
				if (WSAssert.assertIfElementExists(fetchLOVRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(fetchLOVRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for VIPLevel failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	/**
	 * @author psarawag, nilsaini Prerequisite: FetchLOV details should be
	 *         updated in database Description: This test case is to check
	 *         whether FETCHROOMSTATUS LOV values are present in the database or
	 *         not and the response is fetching the details correctly or not.
	 */

	@Test(groups = { "minimumRegression", "FetchLOV", "HTNG2008BExt", "HTNG" })
	public void fetchLOV_Ext_1956() {
		try {
			boolean blockedFlag = true;
			String testName = "fetchLOV_Ext_1956";
			WSClient.startTest(testName,
					"This test case is to check whether FETCHROOMSTATUS LOV values are present in the database or not and the response is fetching the details correctly or not.",
					"minimumRegression");
			String prerequisite[] = { "OOOSReason" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String fetchLOVReq = WSClient.createSOAPMessage("HTNGExtFetchLOV", "DS_02");
				String fetchLOVRes = WSClient.processSOAPMessage(fetchLOVReq);

				List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
				HashMap<String, String> xPath = new HashMap<String, String>();
				String query=WSClient.getQuery("QS_02");
				db = WSClient.getDBRows(query);
				if (db.isEmpty()) {
					blockedFlag = false;
					WSClient.writeToReport(LogStatus.WARNING, "Blocked");
				}
				if (blockedFlag && WSAssert.assertIfElementValueEquals(fetchLOVRes,
						"FetchLovResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					List<LinkedHashMap<String, String>> res = new ArrayList<LinkedHashMap<String, String>>();
					xPath.put("FetchLovResponse_LovQueryResult_LovValue", "FetchLovResponse_LovQueryResult_LovValue");
					xPath.put("FetchLovResponse_LovQueryResult_LovValue_description",
							"FetchLovResponse_LovQueryResult_LovValue");
					res = WSClient.getMultipleNodeList(fetchLOVRes, xPath, false, XMLType.RESPONSE);
					WSAssert.assertEquals(res, db, true);

				}
				if (WSAssert.assertIfElementExists(fetchLOVRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(fetchLOVRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RoomStatusReason failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	/**
	 * @author psarawag, nilsaini Prerequisite: FetchLOV details should be
	 *         updated in database Description: This test case is to check
	 *         whether TASKCODE LOV values are present in the database or not
	 *         and the response is fetching the details correctly or not.
	 */

	@Test(groups = { "minimumRegression", "FetchLOV", "HTNG2008BExt", "HTNG" })
	public void fetchLOV_Ext_1957() {
		try {
			boolean blockedFlag = true;
			String testName = "fetchLOV_Ext_1957";
			WSClient.startTest(testName,
					"This test case is to check whether TASKCODE LOV values are present in the database or not and the response is fetching the details correctly or not.",
					"minimumRegression");
			String prerequisite[] = { "TaskCode" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String fetchLOVReq = WSClient.createSOAPMessage("HTNGExtFetchLOV", "DS_03");
				String fetchLOVRes = WSClient.processSOAPMessage(fetchLOVReq);
				List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
				HashMap<String, String> xPath = new HashMap<String, String>();
				String query=WSClient.getQuery("QS_03");
				db = WSClient.getDBRows(query);
				if (db.isEmpty()) {
					blockedFlag = false;
					WSClient.writeToReport(LogStatus.WARNING, "Blocked");
				}
				if (blockedFlag && WSAssert.assertIfElementValueEquals(fetchLOVRes,
						"FetchLovResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					List<LinkedHashMap<String, String>> res = new ArrayList<LinkedHashMap<String, String>>();
					xPath.put("FetchLovResponse_LovQueryResult_LovValue", "FetchLovResponse_LovQueryResult_LovValue");
					xPath.put("FetchLovResponse_LovQueryResult_LovValue_description",
							"FetchLovResponse_LovQueryResult_LovValue");
					res = WSClient.getMultipleNodeList(fetchLOVRes, xPath, false, XMLType.RESPONSE);
					WSAssert.assertEquals(res, db, true);

				}
				if (WSAssert.assertIfElementExists(fetchLOVRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(fetchLOVRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for TaskCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	/**
	 * @author psarawag, nilsaini Prerequisite: FetchLOV details should be
	 *         updated in database Description: This test case is to check
	 *         whether VIPLEVEL LOV values are present in the database or not
	 *         and the response is fetching the details of active VIPLEVEL LOV
	 *         only.
	 */

	@Test(groups = { "minimumRegression", "FetchLOV", "HTNG2008BExt", "HTNG" })
	public void fetchLOV_Ext_1962() {
		try {
			String testName = "fetchLOV_Ext_1962";
			WSClient.startTest(testName,
					"This test case is to check whether VIPLEVEL LOV values are present in the database or not and the response is fetching the details of active VIPLEVEL LOV only.",
					"minimumRegression");
			String prerequisite[] = { "VipLevel" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String fetchLOVReq = WSClient.createSOAPMessage("HTNGExtFetchLOV", "DS_01");
				String fetchLOVRes = WSClient.processSOAPMessage(fetchLOVReq);
				List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
				HashMap<String, String> xPath = new HashMap<String, String>();
				String query=WSClient.getQuery("QS_01");
				db = WSClient.getDBRows(query);
				if ( WSAssert.assertIfElementValueEquals(fetchLOVRes,
						"FetchLovResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					List<LinkedHashMap<String, String>> res = new ArrayList<LinkedHashMap<String, String>>();
					xPath.put("FetchLovResponse_LovQueryResult_LovValue", "FetchLovResponse_LovQueryResult_LovValue");
					xPath.put("FetchLovResponse_LovQueryResult_LovValue_description",
							"FetchLovResponse_LovQueryResult_LovValue");
					res = WSClient.getMultipleNodeList(fetchLOVRes, xPath, false, XMLType.RESPONSE);
					if (WSAssert.assertEquals(res, db, true)) {
						List<LinkedHashMap<String, String>> db1 = new ArrayList<LinkedHashMap<String, String>>();
						String query1=WSClient.getQuery("QS_04");
						db1 = WSClient.getDBRows(query1);
						int resSize=res.size();
						res.remove(db1);
						if (res.size()==resSize) 
							WSClient.writeToReport(LogStatus.PASS,
									"<b>Verification of Inactive VIPLEVEL Records Passed!!!</b>");
						else
							WSClient.writeToReport(LogStatus.FAIL,
									"<b>Verification of Inactive VIPLEVEL Records Failed!!!</b>");
					}
				}
				if (WSAssert.assertIfElementExists(fetchLOVRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(fetchLOVRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for VIPLevel failed! -----Blocked");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	/**
	 * @author psarawag, nilsaini Prerequisite: FetchLOV details should be
	 *         updated in database Description: This test case is to check
	 *         whether ROOOMSTATUSREASON LOV values are present in the database
	 *         or not and the response is fetching the details of active
	 *         ROOMSTATUSREASON LOV only.
	 */

	@Test(groups = { "minimumRegression", "FetchLOV", "HTNG2008BExt", "HTNG" })
	public void fetchLOV_Ext_1961() {
		try {

			String testName = "fetchLOV_Ext_1961";
			WSClient.startTest(testName,
					"This test case is to check whether ROOOMSTATUSREASON LOV values are present in the database or not and the response is fetching the details of active ROOMSTATUSREASON LOV only.",
					"minimumRegression");
			String prerequisite[] = { "OOOSReason" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String fetchLOVReq = WSClient.createSOAPMessage("HTNGExtFetchLOV", "DS_02");
				String fetchLOVRes = WSClient.processSOAPMessage(fetchLOVReq);
				List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
				HashMap<String, String> xPath = new HashMap<String, String>();
				String query=WSClient.getQuery("QS_02");
				db = WSClient.getDBRows(query);
				if ( WSAssert.assertIfElementValueEquals(fetchLOVRes,
						"FetchLovResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					List<LinkedHashMap<String, String>> res = new ArrayList<LinkedHashMap<String, String>>();
					xPath.put("FetchLovResponse_LovQueryResult_LovValue", "FetchLovResponse_LovQueryResult_LovValue");
					xPath.put("FetchLovResponse_LovQueryResult_LovValue_description",
							"FetchLovResponse_LovQueryResult_LovValue");
					res = WSClient.getMultipleNodeList(fetchLOVRes, xPath, false, XMLType.RESPONSE);
					if (WSAssert.assertEquals(res, db, true)) {
						List<LinkedHashMap<String, String>> db1 = new ArrayList<LinkedHashMap<String, String>>();
						String query1=WSClient.getQuery("QS_05");
						db1 = WSClient.getDBRows(query1);
						int resSize=res.size();
						res.remove(db1);
						if (res.size()==resSize) {
							WSClient.writeToReport(LogStatus.PASS,
									"<b>Verification of Inactive RoomStatusReason Records Passed!!!</b>");
						}
						else {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b>Verification of Inactive RoomStatusReason Records Failed!!!</b>");
					}
						}
						
							
				}
				if (WSAssert.assertIfElementExists(fetchLOVRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(fetchLOVRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RoomStatusReason failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}
	
	@Test(groups = { "fullRegression", "FetchLOV", "HTNG2008BExt", "HTNG" })
	public void fetchLOV_Ext_1958() {
		try {
			boolean blockedFlag = true;
			String testName = "fetchLOV_Ext_1958";
			WSClient.startTest(testName,
					"Verify that error message is coming when no LOV identifier is given in request",
					"fullRegression");
			String prerequisite[] = { "VipLevel" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String fetchLOVReq = WSClient.createSOAPMessage("HTNGExtFetchLOV", "DS_04");
				String fetchLOVRes = WSClient.processSOAPMessage(fetchLOVReq);
				List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
				HashMap<String, String> xPath = new HashMap<String, String>();
				String query=WSClient.getQuery("QS_01");
				db = WSClient.getDBRows(query);
				if (db.isEmpty()) {
					blockedFlag = false;
				}
				if (blockedFlag && WSAssert.assertIfElementValueEquals(fetchLOVRes,
						"FetchLovResponse_Result_resultStatusFlag", "FAIL", false)) {
					

				}
				if (WSAssert.assertIfElementExists(fetchLOVRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(fetchLOVRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for VIPLevel failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	@Test(groups = { "fullRegression", "FetchLOV", "HTNG2008BExt", "HTNG" })
	public void fetchLOV_Ext_1959() {
		try {
			boolean blockedFlag = true;
			String testName = "fetchLOV_Ext_1959";
			WSClient.startTest(testName,
					"Verify that error message is coming when invalid data is given the request",
					"fullRegression");
			String prerequisite[] = { "VipLevel" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);

				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String fetchLOVReq = WSClient.createSOAPMessage("HTNGExtFetchLOV", "DS_05");
				String fetchLOVRes = WSClient.processSOAPMessage(fetchLOVReq);
				List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
				HashMap<String, String> xPath = new HashMap<String, String>();
				String query=WSClient.getQuery("QS_01");
				db = WSClient.getDBRows(query);
				if (db.isEmpty()) {
					blockedFlag = false;
				}
				if (blockedFlag && WSAssert.assertIfElementValueEquals(fetchLOVRes,
						"FetchLovResponse_Result_resultStatusFlag", "FAIL", false)) {
					

				}
				if (WSAssert.assertIfElementExists(fetchLOVRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(fetchLOVRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for VIPLevel failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

}
