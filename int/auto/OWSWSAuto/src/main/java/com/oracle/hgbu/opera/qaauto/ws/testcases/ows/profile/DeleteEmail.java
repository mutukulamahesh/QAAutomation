package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class DeleteEmail extends WSSetUp {

	@Test(groups = { "sanity", "OWS", "DeleteEmail", "Name", "in-QA" })
	public void deleteEmail_38285() {

		try {
			String testName = "deleteEmail_38285";
			WSClient.startTest(testName, "Verify that if email is deleted.", "sanity");
			String resortOperaValue = OPERALib.getResort();

			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_email}", fname + "." + lname + "@oracle.com");
			WSClient.setData("{var_primary}", "true");
			WSClient.setData("{var_resort}", resortOperaValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());

			String profileID = CreateProfile.createProfile("DS_13");
			WSClient.setData("{var_profileID}", profileID);

			if (profileID != "") {
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

				String operaID = "";
				String query1 = WSClient.getQuery("QS_04");
				operaID = WSClient.getDBRow(query1).get("PHONE_ID");
				WSClient.setData("{var_phoneID}", operaID);
				System.out.println("OPID : " + operaID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Created the Profile With Email attached whose operaID is :" + operaID + "</b>");

				if (operaID != "") {
					String deleteEmailReq = WSClient.createSOAPMessage("OWSDeleteEmail", "DS_01");
					String deleteEmailRes = WSClient.processSOAPMessage(deleteEmailReq);

					if (WSAssert.assertIfElementValueEquals(deleteEmailRes, "DeleteEmailResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						//db validation
						String query2 = WSClient.getQuery("QS_02");
						String count = WSClient.getDBRow(query2).get("COUNT").trim();
						if (WSAssert.assertEquals(count, "0", true))
							WSClient.writeToReport(LogStatus.PASS, "The Email is Deleted");
						else WSClient.writeToReport(LogStatus.FAIL, "Email Deletion Failed >> Record still exist in DB");
					}

					//Opera Error Codes
					if (WSAssert.assertIfElementExists(deleteEmailRes, "DeleteEmailResponse_Result_GDSError", true))
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(deleteEmailRes, "DeleteEmailResponse_Result_GDSError", XMLType.RESPONSE) + "</b>");

					if (WSAssert.assertIfElementExists(deleteEmailRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(deleteEmailRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}

					if (WSAssert.assertIfElementExists(deleteEmailRes, "DeleteEmailResponse_Result_OperaErrorCode", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(deleteEmailRes, "DeleteEmailResponse_Result_OperaErrorCode", XMLType.RESPONSE) + "</b>");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Email is not Available for the Profile");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "DeleteEmail", "Name", "in-QA" })
	public void deleteEmail_38287() {

		try {
			String testName = "deleteEmail_38287";
			WSClient.startTest(testName, "Verify Fail result status flag is populated when invalid operaID is passed in Request.", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			WSClient.setData("{var_phoneID}", WSClient.getKeywordData("{KEYWORD_RANDNUM_10}"));

			String deleteEmailReq = WSClient.createSOAPMessage("OWSDeleteEmail", "DS_01");
			String deleteEmailRes = WSClient.processSOAPMessage(deleteEmailReq);

			WSAssert.assertIfElementValueEquals(deleteEmailRes, "DeleteEmailResponse_Result_resultStatusFlag", "FAIL", false);

			//Opera Error Codes 
			if (WSAssert.assertIfElementExists(deleteEmailRes, "DeleteEmailResponse_Result_GDSError", true))
				WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(deleteEmailRes, "DeleteEmailResponse_Result_GDSError", XMLType.RESPONSE) + "</b>");

			if (WSAssert.assertIfElementExists(deleteEmailRes, "Result_Text_TextElement", true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(deleteEmailRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
			}

			if (WSAssert.assertIfElementExists(deleteEmailRes, "DeleteEmailResponse_Result_OperaErrorCode", true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(deleteEmailRes, "DeleteEmailResponse_Result_OperaErrorCode", XMLType.RESPONSE) + "</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "OWS", "DeleteEmail", "Name", "in-QA" })
	public void deleteEmail_40297() {

		try {
			String testName = "deleteEmail_40297";
			WSClient.startTest(testName, "Verify that Phone is <b>NOT</b> deleted.", "minimumRegression");
			WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
			WSClient.setData("{var_primary}", "false");
			String resortOperaValue = OPERALib.getResort();

			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
			WSClient.setData("{var_email}", fname + "." + lname + "@oracle.com");
			WSClient.setData("{var_primary}", "true");
			WSClient.setData("{var_resort}", resortOperaValue);

			OPERALib.setOperaHeader(OPERALib.getUserName());

			String profileID = CreateProfile.createProfile("DS_11");

			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Created ID: " + profileID);
			WSClient.setData("{var_profileID}", profileID);

			if (profileID != "error") {
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

				String operaID = "";
				String query1 = WSClient.getQuery("QS_06");
				operaID = WSClient.getDBRow(query1).get("PHONE_ID");
				System.out.println("operaId : " + operaID);
				WSClient.setData("{var_phoneID}", operaID);

				if (operaID != "") {
					String deleteEmailReq = WSClient.createSOAPMessage("OWSDeleteEmail", "DS_01");
					String deleteEmailRes = WSClient.processSOAPMessage(deleteEmailReq);

					String query2 = WSClient.getQuery("QS_02");
					String count = WSClient.getDBRow(query2).get("COUNT").trim();

					WSAssert.assertIfElementValueEquals(deleteEmailRes, "DeleteEmailResponse_Result_resultStatusFlag", "FAIL", false);
					if (WSAssert.assertEquals(count, "0", true))
						WSClient.writeToReport(LogStatus.FAIL, "ERROR: The phone is Deleted.");
					else WSClient.writeToReport(LogStatus.PASS, "Phone Not Deleted.");

					//Opera Error COdes
					if (WSAssert.assertIfElementExists(deleteEmailRes, "DeleteEmailResponse_Result_GDSError", true))
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(deleteEmailRes, "DeleteEmailResponse_Result_GDSError", XMLType.RESPONSE) + "</b>");

					if (WSAssert.assertIfElementExists(deleteEmailRes, "Result_Text_TextElement", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(deleteEmailRes, "Result_Text_TextElement", XMLType.RESPONSE) + "</b>");
					}

					if (WSAssert.assertIfElementExists(deleteEmailRes, "DeleteEmailResponse_Result_OperaErrorCode", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Text Message: " + WSClient.getElementValue(deleteEmailRes, "DeleteEmailResponse_Result_OperaErrorCode", XMLType.RESPONSE) + "</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Email is not Available for the Profile");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

}
