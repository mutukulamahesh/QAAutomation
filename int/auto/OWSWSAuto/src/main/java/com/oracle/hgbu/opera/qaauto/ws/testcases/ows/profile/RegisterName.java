package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.profile;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class RegisterName extends WSSetUp {
	@Test(groups = { "sanity", "RegisterName", "OWS", "Name", "BAT" })
	/*****
	 * Verify if Name is registered and a valid opera id is returned when lastName
	 * is provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_38580() {
		try {

			String testName = "registerName_38580";
			WSClient.startTest(testName,
					"Verify that Name is registered and a valid opera id is returned when lastName is provided in the request",
					"sanity");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// Invoking targeted request
			String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_01");
			String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

			if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {

					String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_nameId}", operaNameId);
					if (!(operaNameId.equals(""))) {
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_01"));
						LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("RegisterNameRequest_PersonName_lastName", "RegisterNameRequest_PersonName");
						res = WSClient.getSingleNodeList(registerNameReq, xPath, false, XMLType.REQUEST);
						res.put("resort1", WSClient.getData("{var_resort}"));
						WSAssert.assertEquals(res, db, false);
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Register name returned null name ID**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
				}
			}

			// Error handling
			if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "RegisterName", "OWS", "Name" })
	/*****
	 * Verify if Name is registered and a valid opera id is returned when lastName
	 * is provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_38582() {
		try {

			String testName = "registerName_38582";
			WSClient.startTest(testName,
					"verity that name is correctly registered when full person name is provided in request",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// Invoking targeted service
			String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_02");
			String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

			if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {

					String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_nameId}", operaNameId);
					if (!(operaNameId.equals(""))) {

						// Validation logic
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_02"));
						LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("RegisterNameRequest_PersonName_firstName", "RegisterNameRequest_PersonName");
						xPath.put("RegisterNameRequest_PersonName_middleName", "RegisterNameRequest_PersonName");
						xPath.put("RegisterNameRequest_PersonName_lastName", "RegisterNameRequest_PersonName");
						res = WSClient.getSingleNodeList(registerNameReq, xPath, false, XMLType.REQUEST);
						WSAssert.assertEquals(res, db, false);
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Register name returned null name ID**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
				}
			}
			// Error handling
			if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "RegisterName", "OWS", "Name" })
	/*****
	 * Verify if Name is registered and a valid opera id is returned when lastName
	 * is provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_38583() {
		try {

			String testName = "registerName_38583";
			WSClient.startTest(testName,
					"Verify that name id is returned and correctly populated with person name and native name when both are provided in the request message.",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// Invoking targeted service
			String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_03");
			String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

			if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {

					String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_nameId}", operaNameId);
					if (!(operaNameId.equals(""))) {

						// Validation logic
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_02"));
						LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("RegisterNameRequest_PersonName_firstName", "RegisterNameRequest_PersonName");
						xPath.put("RegisterNameRequest_PersonName_middleName", "RegisterNameRequest_PersonName");
						xPath.put("RegisterNameRequest_PersonName_lastName", "RegisterNameRequest_PersonName");
						res = WSClient.getSingleNodeList(registerNameReq, xPath, false, XMLType.REQUEST);
						WSAssert.assertEquals(res, db, false);
						db = WSClient.getDBRow(WSClient.getQuery("QS_03"));
						xPath = new LinkedHashMap<String, String>();
						xPath.put("RegisterNameRequest_NativeName_firstName", "RegisterNameRequest_NativeName");
						xPath.put("RegisterNameRequest_NativeName_lastName", "RegisterNameRequest_NativeName");
						res = WSClient.getSingleNodeList(registerNameReq, xPath, false, XMLType.REQUEST);
						WSAssert.assertEquals(res, db, false);
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Register name returned null name ID**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
				}
			}
			// Error handling
			if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "RegisterName", "OWS", "Name" })
	/*****
	 * Verify if Name is registered and a valid opera id is returned when lastName
	 * is provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_38587() {
		try {

			String testName = "registerName_38587";
			WSClient.startTest(testName, "Verify that vip code is correctly populated in db when provided in request",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// Invoking targeted service
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "VipLevel" })) {
				WSClient.setData("{var_vipcode}", OperaPropConfig.getDataSetForCode("VipLevel", "DS_01"));

				String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_04");
				String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

				if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
						"SUCCESS", false)) {
					if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {

						String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
						WSClient.setData("{var_nameId}", operaNameId);
						if (!(operaNameId.equals(""))) {

							// Validation logic
							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
							db = WSClient.getDBRow(WSClient.getQuery("QS_04"));
							LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
							xPath.put("RegisterNameRequest_vipCode", "RegisterNameRequest_vipCode");
							xPath.put("RegisterNameRequest_PersonName_lastName", "RegisterNameRequest_PersonName");
							res = WSClient.getSingleNodeList(registerNameReq, xPath, false, XMLType.REQUEST);
							WSAssert.assertEquals(res, db, false);
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"************Blocked : Register name returned null name ID**********");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
					}
				}

				// Error handling
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the response
					 ********/

					String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
				}

				if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						true)) {

					/****
					 * Verifying whether the error Message is populated on the response
					 ****/

					String message = WSAssert.getElementValue(registerNameRes,
							"RegisterNameResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"The OperaErrorCode displayed in the  response is :" + message);
				}
				if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the response
					 ****/

					String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "RegisterName", "OWS", "Name" })
	/*****
	 * Verify if Name is registered and a valid opera id is returned when lastName
	 * is provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_38590() {
		try {

			String testName = "registerName_38590";
			WSClient.startTest(testName, "Verify that gender is correctly populated when last name and gender provided",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// Invoking targeted service
			String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_05");
			String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

			if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {

					String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_nameId}", operaNameId);
					if (!(operaNameId.equals(""))) {

						// Validation logic
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_05"));
						LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("RegisterNameRequest_Gender", "RegisterNameRequest_Gender");
						xPath.put("RegisterNameRequest_PersonName_lastName", "RegisterNameRequest_PersonName");
						res = WSClient.getSingleNodeList(registerNameReq, xPath, false, XMLType.REQUEST);
						WSAssert.assertEquals(res, db, false);
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Register name returned null name ID**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
				}
			}

			// Error handling
			if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "RegisterName", "OWS", "Name" })
	/*****
	 * Verify if Name is registered and a valid opera id is returned when lastName
	 * is provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_38596() {
		try {

			String testName = "registerName_38596";
			WSClient.startTest(testName,
					"Verify that birthday is populated correctly when last name and birthday is provided in request message",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// Invoking targeted service
			String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_06");
			String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

			if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {

					String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_nameId}", operaNameId);
					if (!(operaNameId.equals(""))) {

						// Validation logic
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_06"));
						LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("RegisterNameRequest_Birthdate", "RegisterNameRequest_Birthdate");
						xPath.put("RegisterNameRequest_PersonName_lastName", "RegisterNameRequest_PersonName");
						res = WSClient.getSingleNodeList(registerNameReq, xPath, false, XMLType.REQUEST);

						String birthDate = res.get("Birthdate1");
						if (res.containsKey("Birthdate1")) {
							res.put("Birthdate1", "XXXXXX" + birthDate.substring(birthDate.length() - 2));
						}

						WSAssert.assertEquals(res, db, false);
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Register name returned null name ID**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
				}
			}

			// Error handling
			if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "RegisterName", "OWS", "Name" })
	/*****
	 * Verify if Name is registered and a valid opera id is returned when lastName
	 * and address is provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_39105() {
		try {

			String testName = "registerName_39105";
			WSClient.startTest(testName,
					"Verify that gender is correctly populated when last name and address provided",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// getting address details for request
			WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
			HashMap<String, String> address = new HashMap<String, String>();
			address = OPERALib.fetchAddressLOV();
			WSClient.setData("{var_city}", address.get("City"));
			WSClient.setData("{var_state}", address.get("State"));
			WSClient.setData("{var_country}", address.get("Country"));
			WSClient.setData("{var_zip}", address.get("Zip"));

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// Invoking targeted request
			String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_07");
			String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

			if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {

					String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_nameId}", operaNameId);
					if (!(operaNameId.equals(""))) {

						// Validation logic
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_07"));
						LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("RegisterNameRequest_Address_AddressLine", "RegisterNameRequest_Address");
						xPath.put("RegisterNameRequest_Address_cityName", "RegisterNameRequest_Address");
						xPath.put("RegisterNameRequest_Address_stateProv", "RegisterNameRequest_Address");
						xPath.put("RegisterNameRequest_Address_countryCode", "RegisterNameRequest_Address");
						xPath.put("RegisterNameRequest_Address_postalCode", "RegisterNameRequest_Address");
						xPath.put("RegisterNameRequest_Address_addressType", "RegisterNameRequest_Address");
						res = WSClient.getSingleNodeList(registerNameReq, xPath, false, XMLType.REQUEST);
						WSAssert.assertEquals(res, db, false);
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Register name returned null name ID**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
				}
			}

			// error handling
			if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	
	
	@Test(groups = { "minimumRegression", "RegisterName", "OWS", "Name" })
	/*****
	 * Verify if Name is registered and a valid opera id is returned when lastName
	 * is provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_41868() {
		try {

			String testName = "registerName_41868";
			WSClient.startTest(testName,
					"Verify that COMPANY type Name is registered and a valid opera id is returned when lastName is provided in the request",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// Invoking targeted request
			String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_08");
			String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

			if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {

					String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_nameId}", operaNameId);
					if (!(operaNameId.equals(""))) {
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_08"));
						LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("RegisterNameRequest_PersonName_lastName", "RegisterNameRequest_PersonName");
						xPath.put("RegisterNameRequest_NameType", "RegisterNameRequest_NameType");
						res = WSClient.getSingleNodeList(registerNameReq, xPath, false, XMLType.REQUEST);
						WSAssert.assertEquals(res, db, false);
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Register name returned null name ID**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
				}
			}

			// Error handling
			if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	
	@Test(groups = { "minimumRegression", "RegisterName", "OWS", "Name" })
	/*****
	 * Verify if Name is registered and a valid opera id is returned when lastName
	 * is provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_41869() {
		try {

			String testName = "registerName_41869";
			WSClient.startTest(testName,
					"Verify that TRAVEL_AGENT type Name is registered and a valid opera id is returned when lastName is provided in the request",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// Invoking targeted request
			String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_09");
			String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

			if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {

					String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_nameId}", operaNameId);
					if (!(operaNameId.equals(""))) {
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_08"));
						LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("RegisterNameRequest_PersonName_lastName", "RegisterNameRequest_PersonName");
						xPath.put("RegisterNameRequest_NameType", "RegisterNameRequest_NameType");
						
						res = WSClient.getSingleNodeList(registerNameReq, xPath, false, XMLType.REQUEST);
						
						WSAssert.assertEquals(res, db, false);
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Register name returned null name ID**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
				}
			}

			// Error handling
			if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	
	
	@Test(groups = { "minimumRegression", "RegisterName", "OWS", "Name" })
	/*****
	 * Verify if Name is registered and a valid opera id is returned when lastName
	 * is provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_41870() {
		try {

			String testName = "registerName_41870";
			WSClient.startTest(testName,
					"Verify that CONTACT type Name is registered and a valid opera id is returned when lastName is provided in the request",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// Invoking targeted request
			String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_10");
			String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

			if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {

					String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_nameId}", operaNameId);
					if (!(operaNameId.equals(""))) {
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_09"));
						LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("RegisterNameRequest_PersonName_lastName", "RegisterNameRequest_PersonName");
						xPath.put("RegisterNameRequest_NameType", "RegisterNameRequest_NameType");
						
						res = WSClient.getSingleNodeList(registerNameReq, xPath, false, XMLType.REQUEST);
					
						WSAssert.assertEquals(res, db, false);
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Register name returned null name ID**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
				}
			}

			// Error handling
			if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	
	@Test(groups = { "minimumRegression", "RegisterName", "OWS", "Name" })
	/*****
	 * Verify if Name is registered and a valid opera id is returned when lastName
	 * is provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_41872() {
		try {

			String testName = "registerName_41872";
			WSClient.startTest(testName,
					"Verify that SOURCE type Name is registered and a valid opera id is returned when lastName is provided in the request",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// Invoking targeted request
			String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_11");
			String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

			if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {

					String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_nameId}", operaNameId);
					if (!(operaNameId.equals(""))) {
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_10"));
						LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("RegisterNameRequest_PersonName_lastName", "RegisterNameRequest_PersonName");
						xPath.put("RegisterNameRequest_NameType", "RegisterNameRequest_NameType");
						
						res = WSClient.getSingleNodeList(registerNameReq, xPath, false, XMLType.REQUEST);
						
						WSAssert.assertEquals(res, db, false);
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Register name returned null name ID**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
				}
			}

			// Error handling
			if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	
	@Test(groups = { "minimumRegression", "RegisterName", "OWS", "Name" })
	/*****
	 * Verify if Name is registered and a valid opera id is returned when lastName
	 * is provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_41873() {
		try {

			String testName = "registerName_41873";
			WSClient.startTest(testName,
					"Verify that GROUP type Name is registered and a valid opera id is returned when lastName is provided in the request",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));

			// Invoking targeted request
			String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_12");
			String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

			if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {

					String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_nameId}", operaNameId);
					if (!(operaNameId.equals(""))) {
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_11"));
						LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("RegisterNameRequest_PersonName_lastName", "RegisterNameRequest_PersonName");
						xPath.put("RegisterNameRequest_NameType", "RegisterNameRequest_NameType");
						
						res = WSClient.getSingleNodeList(registerNameReq, xPath, false, XMLType.REQUEST);
						
						WSAssert.assertEquals(res, db, false);
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Register name returned null name ID**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
				}
			}

			// Error handling
			if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	@Test(groups = { "minimumRegression", "RegisterName", "OWS", "Name" })
	/*****
	 * Verify if Name is registered along with title and a valid opera id is returned when lastName,title
	 * are provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_42445() {
		try {

			String testName = "registerName_42445";
			WSClient.startTest(testName,
					"Verify that Name is registered along with title and a valid opera id is returned",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
            WSClient.setData("{var_title}", OperaPropConfig.getDataSetForCode("Title", "DS_01"));
			// Invoking targeted request
			String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_13");
			String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

			if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {

					String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_nameId}", operaNameId);
					if (!(operaNameId.equals(""))) {
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_12"));
						
						LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("RegisterNameRequest_PersonName_lastName", "RegisterNameRequest_PersonName");
						xPath.put("RegisterNameRequest_PersonName_nameTitle", "RegisterNameRequest_PersonName");
						res = WSClient.getSingleNodeList(registerNameReq, xPath, false, XMLType.REQUEST);
						
						res.put("resort1", WSClient.getData("{var_resort}"));
						
						WSClient.writeToReport(LogStatus.INFO,"Validating the details");
						
						
						WSAssert.assertEquals(res, db, false);
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Register name returned null name ID**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
				}
			}

			// Error handling
			if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	@Test(groups = { "minimumRegression", "RegisterName", "OWS", "Name" })
	/*****
	 * Verify if Name is registered along with privacy option and a valid opera id is returned when lastName,title
	 * are provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_42380() {
		try {

			String testName = "registerName_42380";
			WSClient.startTest(testName,
					"Verify that Name is registered along with privacy option and a valid opera id is returned",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// Invoking targeted request
			String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_14");
			String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

			if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {
					String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,"<b>"+"Registered Name with privacy option and profileID->" + operaNameId + "</b>");
					
					
					WSClient.setData("{var_nameId}", operaNameId);
					if (!(operaNameId.equals(""))) {
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_13"));
						
						LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
						
						
						res.put("lastName", WSClient.getElementValue(registerNameReq, "RegisterNameRequest_PersonName_lastName", XMLType.REQUEST));
						
						String market_yn=WSClient.getElementValueByAttribute(registerNameReq,"RegisterNameRequest_Privacy_PrivacyOption_OptionValue", "RegisterNameRequest_Privacy_PrivacyOption_OptionType", "MarketResearch", XMLType.REQUEST);
						res.put("MARKET_RESERCH_YN",market_yn);
						
						res.put("resort",WSClient.getData("{var_resort}"));
						
						WSClient.writeToReport(LogStatus.INFO,"Validating the details");
						
						
						WSAssert.assertEquals(res, db, false);
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Register name returned null name ID**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
				}
			}

			// Error handling
			if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	@Test(groups = { "minimumRegression", "RegisterName", "OWS", "Name" })
	/*****
	 * Verify if Name is registered and a valid opera id is returned when lastName
	 * is provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_42452() {
		try {

			String testName = "registerName_42452";
			WSClient.startTest(testName,
					"Verify that Name is registered with email and a valid opera id is returned",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
            String lastName=WSClient.getKeywordData("{KEYWORD_LNAME}");
            WSClient.setData("{var_lname}",lastName);
            WSClient.setData("{var_email}",lastName+"@web.com");
			// Invoking targeted request
			String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_15");
			String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

			if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {

					String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_nameId}", operaNameId);
					if (!(operaNameId.equals(""))) {
						LinkedHashMap<String, String> db1 = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> db2 = new LinkedHashMap<String, String>();
						String resort1=WSClient.getData("{var_resort}");
						String lastname=WSClient.getElementValue(registerNameReq, "RegisterNameRequest_PersonName_lastName", XMLType.REQUEST);
						String email=WSClient.getElementValue(registerNameReq, "RegisterNameRequest_Email",  XMLType.REQUEST);
						db1 = WSClient.getDBRow(WSClient.getQuery("QS_14"));
						db2 = WSClient.getDBRow(WSClient.getQuery("QS_15"));
						if(WSAssert.assertEquals(lastname, db1.get("lastName"),true)){
						WSClient.writeToReport(LogStatus.PASS, "LastName->Expected:"+lastname+"Actual:"+db1.get("lastName"));	
						}
						else{
							WSClient.writeToReport(LogStatus.FAIL, "LastName->Expected:"+lastname+"Actual:"+db1.get("lastName"));	
						}
						if(WSAssert.assertEquals(resort1, db1.get("resort"),true)){
							WSClient.writeToReport(LogStatus.PASS, "Resort->Expected:"+resort1+"Actual:"+db1.get("resort"));	
							}
							else{
								WSClient.writeToReport(LogStatus.FAIL, "Resort->Expected:"+resort1+"Actual:"+db1.get("resort"));	
							}
						if(WSAssert.assertEquals(email, db2.get("PHONE_NUMBER"),true)){
							WSClient.writeToReport(LogStatus.PASS, "Email->Expected:"+email+"Actual:"+db2.get("PHONE_NUMBER"));	
							}
							else{
								WSClient.writeToReport(LogStatus.FAIL, "Email->Expected:"+email+"Actual:"+db2.get("PHONE_NUMBER"));	
							}
						
						
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Register name returned null name ID**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
				}
			}

			// Error handling
			if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	@Test(groups = { "minimumRegression", "RegisterName", "OWS", "Name" })
	/*****
	 * Verify if Name is registered and a valid opera id is returned when lastName
	 * is provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_42481() {
		try {

			String testName = "registerName_42481";
			WSClient.startTest(testName,
					"Verify that Name is registered with passport and a valid opera id is returned ",
					"minimumRegression");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
            WSClient.setData("{var_number}",WSClient.getKeywordData("{KEYWORD_RANDNUM_10}"));
            WSClient.setData("{var_type}", "PASSPORT");
			// Invoking targeted request
			String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_16");
			String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

			if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {

					String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_nameId}", operaNameId);
					if (!(operaNameId.equals(""))) {
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_01"));
						LinkedHashMap<String, String> db2 = new LinkedHashMap<String, String>();
						db2 = WSClient.getDBRow(WSClient.getQuery("QS_16"));
						
						LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("RegisterNameRequest_PersonName_lastName", "RegisterNameRequest_PersonName");
						res = WSClient.getSingleNodeList(registerNameReq, xPath, false, XMLType.REQUEST);
						res.put("resort1", WSClient.getData("{var_resort}"));
						WSClient.setData("{var_number}",WSClient.getElementValue(registerNameReq,"RegisterNameRequest_Passport_documentNumber",XMLType.REQUEST));
						db.put("ENCRYPTED_PASSPORT_ID",db2.get("ENCRYPTED_PASSPORT_ID"));
						db2 = WSClient.getDBRow(WSClient.getQuery("QS_17"));
						res.put("ENCRYPTED_PASSPORT_ID",db2.get("ENCRYPTED_PASSPORT_ID"));
						WSAssert.assertEquals(res, db, false);
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Register name returned null name ID**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
				}
			}

			// Error handling
			if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	@Test(groups = { "minimumRegression", "RegisterName", "OWS", "Name" })
	/*****
	 * Verify if Name is registered and a valid opera id is returned when lastName
	 * is provided in the request
	 *****/
	/*****
	 * * *
	 *****/
	public void registerName_42482() {
		try {

			String testName = "registerName_42482";
			WSClient.startTest(testName,
					"Verify that Name is registered with phone details and a valid opera id is returned",
					"sanity");

			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
			// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
			// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
            WSClient.setData("{var_phone}", WSClient.getKeywordData("{KEYWORD_RANDNUM_10}"));
            WSClient.setData("{var_phoneType}",OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));
            WSClient.setData("{var_phoneRole}",OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
			// Invoking targeted request
			String registerNameReq = WSClient.createSOAPMessage("OWSRegisterName", "DS_17");
			String registerNameRes = WSClient.processSOAPMessage(registerNameReq);

			if (WSAssert.assertIfElementValueEquals(registerNameRes, "RegisterNameResponse_Result_resultStatusFlag",
					"SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(registerNameRes, "Result_IDs_IDPair_operaId", true)) {

					String operaNameId = WSClient.getElementValue(registerNameRes, "Result_IDs_IDPair_operaId",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_nameId}", operaNameId);
					if (!(operaNameId.equals(""))) {
						LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
						db = WSClient.getDBRow(WSClient.getQuery("QS_01"));
						LinkedHashMap<String, String> db2 = new LinkedHashMap<String, String>();
						db2 = WSClient.getDBRow(WSClient.getQuery("QS_18"));
						LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
						LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
						xPath.put("RegisterNameRequest_PersonName_lastName", "RegisterNameRequest_PersonName");
						res = WSClient.getSingleNodeList(registerNameReq, xPath, false, XMLType.REQUEST);
						res.put("resort1", WSClient.getData("{var_resort}"));
						res.put("phoneRole1", WSClient.getElementValue(registerNameReq, "RegisterNameRequest_Phone_phoneRole", XMLType.REQUEST));
						res.put("phoneType1", WSClient.getElementValue(registerNameReq, "RegisterNameRequest_Phone_phoneType", XMLType.REQUEST));
						res.put("phoneNumber1", WSClient.getElementValue(registerNameReq, "RegisterNameRequest_Phone_PhoneNumber", XMLType.REQUEST));
						res.put("primaryPhone1", WSClient.getElementValue(registerNameReq, "RegisterNameRequest_Phone_primaryPhone", XMLType.REQUEST));
						db.put("phoneRole1", db2.get("PHONE_ROLE"));
						db.put("phoneType1", db2.get("PHONE_TYPE"));
						db.put("phoneNumber1", db2.get("PHONE_NUMBER"));
						db.put("primaryPhone1", db2.get("PrimaryPhone"));
						WSAssert.assertEquals(res, db, false);
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Register name returned null name ID**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "************Blocked :Name ID not created**********");
				}
			}

			// Error handling
			if (WSAssert.assertIfElementExists(registerNameRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(registerNameRes, "Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
			}

			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The OperaErrorCode displayed in the  response is :" + message);
			}
			if (WSAssert.assertIfElementExists(registerNameRes, "RegisterNameResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is populated on the response
				 ****/

				String message = WSAssert.getElementValue(registerNameRes, "RegisterNameResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "The GDSerror displayed in the response is :" + message);
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
}