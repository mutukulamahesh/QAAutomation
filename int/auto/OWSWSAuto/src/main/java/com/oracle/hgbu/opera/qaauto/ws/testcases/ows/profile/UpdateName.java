package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import java.util.LinkedHashMap;

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

public class UpdateName extends WSSetUp {
	@Test(groups = { "sanity", "UpdateName", "OWS", "Name" })
	/*****
	 * Verify if Last Name is updated for a valid opera id provided in the
	 * request with a lastName value
	 *****/
	/*****
	 * * *
	 *****/
	public void updateName_38581() {
		try {

			String testName = "updateName_38581";
			WSClient.startTest(testName, "Verify that Last Name is updated for a valid opera id provided in the request with a lastName value", "sanity");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort,
			// channel));
			// *********Prerequisite: Create Profile**************//

			String operaProfileID = CreateProfile.createProfile("DS_35");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);
				// Invoking targeted service
				String updateNameReq = WSClient.createSOAPMessage("OWSUpdateName", "DS_01");
				String updateNameRes = WSClient.processSOAPMessage(updateNameReq);

				if (WSAssert.assertIfElementValueEquals(updateNameRes, "UpdateNameResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation logic
					LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
					db = WSClient.getDBRow(WSClient.getQuery("QS_01"));
					LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("UpdateNameRequest_PersonName_lastName", "UpdateNameRequest_PersonName");
					res = WSClient.getSingleNodeList(updateNameReq, xPath, false, XMLType.REQUEST);
					WSAssert.assertEquals(res, db, false);
				}
				// Error handling
				if (WSAssert.assertIfElementExists(updateNameRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(updateNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}

				if (WSAssert.assertIfElementExists(updateNameRes, "UpdateNameResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateNameRes, "UpdateNameResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
				}
				if (WSAssert.assertIfElementExists(updateNameRes, "UpdateNameResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateNameRes, "UpdateNameResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Create Profile returned null profile ID**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "UpdateName", "OWS", "Name" })
	/*****
	 * update a particular name id provided in a request with a full person name
	 * check if it reflects in the DB
	 *****/
	/*****
	 * * *
	 *****/
	public void updateName_38609() {
		try {

			String testName = "updateName_38609";
			WSClient.startTest(testName, "verify that updating a particular name id provided in a request with a full person name, reflects in the DB", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort,
			// channel));
			// *********Prerequisite: Create Profile**************//
			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);

				// Invoking targeted service
				String updateNameReq = WSClient.createSOAPMessage("OWSUpdateName", "DS_02");
				String updateNameRes = WSClient.processSOAPMessage(updateNameReq);

				if (WSAssert.assertIfElementValueEquals(updateNameRes, "UpdateNameResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation logic
					LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
					db = WSClient.getDBRow(WSClient.getQuery("QS_02"));
					LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("UpdateNameRequest_PersonName_firstName", "UpdateNameRequest_PersonName");
					xPath.put("UpdateNameRequest_PersonName_middleName", "UpdateNameRequest_PersonName");
					xPath.put("UpdateNameRequest_PersonName_lastName", "UpdateNameRequest_PersonName");
					res = WSClient.getSingleNodeList(updateNameReq, xPath, false, XMLType.REQUEST);
					WSAssert.assertEquals(res, db, false);
				}
				// Error handling
				if (WSAssert.assertIfElementExists(updateNameRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(updateNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}

				if (WSAssert.assertIfElementExists(updateNameRes, "UpdateNameResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateNameRes, "UpdateNameResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
				}
				if (WSAssert.assertIfElementExists(updateNameRes, "UpdateNameResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateNameRes, "UpdateNameResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Create Profile returned null profile ID**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "UpdateName", "OWS", "Name" })
	/*****
	 * verify if updated full person name and the full native name gets
	 * reflected in the DB
	 *****/
	/*****
	 * * *
	 *****/
	public void updateName_38611() {
		try {

			String testName = "updateName_38611";
			WSClient.startTest(testName, "verify that updated full person name and the full native name gets reflected in the DB", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort,
			// channel));
			// *********Prerequisite: Create Profile**************//
			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);
				// Invoking targeted service
				String updateNameReq = WSClient.createSOAPMessage("OWSUpdateName", "DS_03");
				String updateNameRes = WSClient.processSOAPMessage(updateNameReq);

				if (WSAssert.assertIfElementValueEquals(updateNameRes, "UpdateNameResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation logic
					LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
					db = WSClient.getDBRow(WSClient.getQuery("QS_02"));
					LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("UpdateNameRequest_PersonName_firstName", "UpdateNameRequest_PersonName");
					xPath.put("UpdateNameRequest_PersonName_middleName", "UpdateNameRequest_PersonName");
					xPath.put("UpdateNameRequest_PersonName_lastName", "UpdateNameRequest_PersonName");
					res = WSClient.getSingleNodeList(updateNameReq, xPath, false, XMLType.REQUEST);
					WSAssert.assertEquals(res, db, false);

					db = new LinkedHashMap<String, String>();
					db = WSClient.getDBRow(WSClient.getQuery("QS_03"));
					res = new LinkedHashMap<String, String>();
					xPath = new LinkedHashMap<String, String>();
					xPath.put("UpdateNameRequest_NativeName_firstName", "UpdateNameRequest_NativeName");
					xPath.put("UpdateNameRequest_NativeName_lastName", "UpdateNameRequest_NativeName");
					res = WSClient.getSingleNodeList(updateNameReq, xPath, false, XMLType.REQUEST);
					WSAssert.assertEquals(res, db, false);
				}
				// Error handling
				if (WSAssert.assertIfElementExists(updateNameRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(updateNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}

				if (WSAssert.assertIfElementExists(updateNameRes, "UpdateNameResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateNameRes, "UpdateNameResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
				}
				if (WSAssert.assertIfElementExists(updateNameRes, "UpdateNameResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateNameRes, "UpdateNameResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Create Profile returned null profile ID**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "UpdateName", "OWS", "Name" })
	/*****
	 * verify if vip code is updated in the db when provided in the request for
	 * a particular name id
	 *****/
	/*****
	 * * * prerequisite: vip Codes should be available
	 *****/
	public void updateName_38612() {
		try {

			String testName = "updateName_38612";
			WSClient.startTest(testName, "verify that vip code is updated in the db when provided in the request for a particular name id", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort,
			// channel));
			// *********Prerequisite: Create Profile**************//

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "VipLevel" })) {
				WSClient.setData("{var_vipcode}", OperaPropConfig.getDataSetForCode("VipLevel", "DS_01"));

				String operaProfileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
				if (!operaProfileID.equals("error")) {
					WSClient.setData("{var_profileId}", operaProfileID);
					// Invoking targeted service
					String updateNameReq = WSClient.createSOAPMessage("OWSUpdateName", "DS_04");
					String updateNameRes = WSClient.processSOAPMessage(updateNameReq);

					if (WSAssert.assertIfElementValueEquals(updateNameRes, "UpdateNameResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						// Validation logic
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_04"));
						LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("UpdateNameRequest_vipCode", "UpdateNameRequest_vipCode");
						xPath.put("UpdateNameRequest_PersonName_lastName", "UpdateNameRequest_PersonName");
						res = WSClient.getSingleNodeList(updateNameReq, xPath, false, XMLType.REQUEST);
						WSAssert.assertEquals(res, db, false);
					}
					// Error handling
					if (WSAssert.assertIfElementExists(updateNameRes, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(updateNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
					}

					if (WSAssert.assertIfElementExists(updateNameRes, "UpdateNameResponse_Result_OperaErrorCode", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSAssert.getElementValue(updateNameRes, "UpdateNameResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
					}
					if (WSAssert.assertIfElementExists(updateNameRes, "UpdateNameResponse_Result_GDSError", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSAssert.getElementValue(updateNameRes, "UpdateNameResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Create Profile returned null profile ID**********");
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "UpdateName", "OWS", "Name" })
	/*****
	 * verify if gender is getting updated in the DB when provided in the
	 * request
	 *****/
	/*****
	 * * *
	 *****/
	public void updateName_38613() {
		try {

			String testName = "updateName_38613";
			WSClient.startTest(testName, "verify that gender is getting updated in the DB when provided in the request", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort,
			// channel));
			// *********Prerequisite: Create Profile**************//
			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);
				// Invoking targeted service
				String updateNameReq = WSClient.createSOAPMessage("OWSUpdateName", "DS_05");
				String updateNameRes = WSClient.processSOAPMessage(updateNameReq);

				if (WSAssert.assertIfElementValueEquals(updateNameRes, "UpdateNameResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation logic
					LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
					db = WSClient.getDBRow(WSClient.getQuery("QS_05"));
					LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("UpdateNameRequest_Gender", "UpdateNameRequest_Gender");
					xPath.put("UpdateNameRequest_PersonName_lastName", "UpdateNameRequest_PersonName");
					res = WSClient.getSingleNodeList(updateNameReq, xPath, false, XMLType.REQUEST);
					WSAssert.assertEquals(res, db, false);
				}
				// Error handling
				if (WSAssert.assertIfElementExists(updateNameRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(updateNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}

				if (WSAssert.assertIfElementExists(updateNameRes, "UpdateNameResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateNameRes, "UpdateNameResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
				}
				if (WSAssert.assertIfElementExists(updateNameRes, "UpdateNameResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateNameRes, "UpdateNameResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Create Profile returned null profile ID**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "UpdateName", "OWS", "Name" })
	/*****
	 * verify if birthday is getting updated in the DB when provided in the
	 * request
	 *****/
	/*****
	 * * *
	 *****/
	public void updateName_38614() {
		try {

			String testName = "updateName_38614";
			WSClient.startTest(testName, "verify that birthday is getting updated in the DB when provided in the request", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort,
			// channel));
			// *********Prerequisite: Create Profile**************//
			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);
				// Invoking targeted service
				String updateNameReq = WSClient.createSOAPMessage("OWSUpdateName", "DS_06");
				String updateNameRes = WSClient.processSOAPMessage(updateNameReq);

				if (WSAssert.assertIfElementValueEquals(updateNameRes, "UpdateNameResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation logic
					LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
					db = WSClient.getDBRow(WSClient.getQuery("QS_06"));
					LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("UpdateNameRequest_Birthdate", "UpdateNameRequest_Birthdate");
					xPath.put("UpdateNameRequest_PersonName_lastName", "UpdateNameRequest_PersonName");
					res = WSClient.getSingleNodeList(updateNameReq, xPath, false, XMLType.REQUEST);

					String birthDate = res.get("Birthdate1");
					if (res.containsKey("Birthdate1")) {
						res.put("Birthdate1", "XXXXXX" + birthDate.substring(birthDate.length() - 2));
					}

					WSAssert.assertEquals(res, db, false);
				}

				if (WSAssert.assertIfElementExists(updateNameRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(updateNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
				// Error handling
				if (WSAssert.assertIfElementExists(updateNameRes, "UpdateNameResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateNameRes, "UpdateNameResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
				}
				if (WSAssert.assertIfElementExists(updateNameRes, "UpdateNameResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateNameRes, "UpdateNameResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Create Profile returned null profile ID**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "UpdateName", "OWS", "Name" })
	/*****
	 * verify if active_yn is getting updated in the DB when provided in the
	 * request
	 *****/
	/*****
	 * * *
	 *****/
	public void updateName_42300() {
		try {

			String testName = "updateName_42300";
			WSClient.startTest(testName, "verify that active_yn is getting updated in the DB when provided in the request", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort,
			// channel));
			// *********Prerequisite: Create Profile**************//
			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);
				WSClient.setData("{var_activeYN}", "false");
				// Invoking targeted service
				String updateNameReq = WSClient.createSOAPMessage("OWSUpdateName", "DS_07");
				String updateNameRes = WSClient.processSOAPMessage(updateNameReq);

				if (WSAssert.assertIfElementValueEquals(updateNameRes, "UpdateNameResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation logic
					LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
					db = WSClient.getDBRow(WSClient.getQuery("QS_07"));
					LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("UpdateNameRequest_Activate", "_UpdateNameRequest");
					xPath.put("UpdateNameRequest_PersonName_lastName", "UpdateNameRequest_PersonName");
					res = WSClient.getSingleNodeList(updateNameReq, xPath, false, XMLType.REQUEST);
					if (db.get("Activate1").equals("N"))
						db.put("Activate1", "false");
					else
						db.put("Activate1", "true");
					WSAssert.assertEquals(db, res, false);
				}

				if (WSAssert.assertIfElementExists(updateNameRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(updateNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
				// Error handling
				if (WSAssert.assertIfElementExists(updateNameRes, "UpdateNameResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateNameRes, "UpdateNameResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
				}
				if (WSAssert.assertIfElementExists(updateNameRes, "UpdateNameResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateNameRes, "UpdateNameResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Create Profile returned null profile ID**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}


	@Test(groups = { "minimumRegression", "UpdateName", "OWS", "Name" })
	/*****
	 * verify if passport is getting updated in the DB when provided in the
	 * request
	 *****/
	/*****
	 * * *
	 *****/
	public void updateName_42303() {
		try {
			String testName = "updateName_42303";
			WSClient.startTest(testName, "verify that passport is getting updated in the DB when provided in the request", "minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(), OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort,
			// channel));
			// *********Prerequisite: Create Profile**************//
			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);
				String documentNo = WSClient.getKeywordData("{KEYWORD_RANDSTR_4}") + WSClient.getKeywordData("{KEYWORD_RANDNUM_4}");
				WSClient.setData("{var_docnum}", documentNo);
				String docType = OperaPropConfig.getDataSetForCode("IdentificationType", "DS_01");
				WSClient.setData("{var_docType}", docType);

				// Invoking targeted service
				String updateNameReq = WSClient.createSOAPMessage("OWSUpdateName", "DS_09");
				String updateNameRes = WSClient.processSOAPMessage(updateNameReq);

				if (WSAssert.assertIfElementValueEquals(updateNameRes, "UpdateNameResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					// Validation logic
					LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
					db = WSClient.getDBRow(WSClient.getQuery("QS_09"));
					if (db.get("count").equals("1")) {
						WSClient.writeToReport(LogStatus.PASS, "Passport Inserted");
					} else {
						WSClient.writeToReport(LogStatus.FAIL, "Passport not Inserted");
					}
				}

				if (WSAssert.assertIfElementExists(updateNameRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the
					 * response
					 ********/

					String message = WSAssert.getElementValue(updateNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}
				// Error handling
				if (WSAssert.assertIfElementExists(updateNameRes, "UpdateNameResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateNameRes, "UpdateNameResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
				}
				if (WSAssert.assertIfElementExists(updateNameRes, "UpdateNameResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the
					 * response
					 ****/

					String message = WSAssert.getElementValue(updateNameRes, "UpdateNameResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Create Profile returned null profile ID**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}



}