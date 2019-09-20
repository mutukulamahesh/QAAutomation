package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchResPromotionCode extends WSSetUp {
	
//	@Test(groups = { "minimumRegression", "FetchReservationPromotionCode", "ResvAdvanced","FetchResPromotionCode", "OWS", "in-QA" })
//	/*****
//	 * Verify if Promotion codes are fetched for passed Reservation ID with
//	 * mandatory fields in the Fetch Reservation Promotion Code Request
//	 *****/
//	/*****
//	 * * * PreRequisites Required: -->There should be a Promotion Codes available
//	 *****/
//	public void fetchResPromotionCode_60007() {
//		try {
//
//			String testName = "fetchResPromotionCode_60007";
//			WSClient.startTest(testName,
//					"Verify that Promotion codes are fetched for passed Reservation ID with mandatory fields in the Fetch Reservation Promotion Code Request when channel and channel carrier name are different",
//					"minimumRegression");
//
//			String resortOperaValue = OPERALib.getResort();
//			String channel = OWSLib.getChannel(3);
//
//			// ********* Setting the OWS Header**************//
//			OPERALib.setOperaHeader(OPERALib.getUserName());
//			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
//					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));
//
//			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
//			WSClient.setData("{var_resort}", resortOperaValue);
//			WSClient.setData("{var_owsresort}", resort);
//			// *********Prerequisite: Create Profile**************//
//
//			String operaProfileID = CreateProfile.createProfile("DS_01");
//			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
//			if (!operaProfileID.equals("error")) {
//				WSClient.setData("{var_profileId}", operaProfileID);
//				if (OperaPropConfig.getPropertyConfigResults(
//						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PromotionCode" })) {
//					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
//					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
//					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
//					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
//					WSClient.setData("{var_promoCode}", OperaPropConfig.getDataSetForCode("PromotionCode", "DS_01"));
//			
//					// ********Prerequisite: Create Reservation***************//
//
//					HashMap<String, String> resv = CreateReservation.createReservation("DS_07");
//					String reservationId = resv.get("reservationId");
//					String confirmationId = resv.get("confirmationId");
//					if (!reservationId.equals("error")) {
//						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
//						WSClient.setData("{var_resvId}", reservationId);
//
//						// WSClient.setData("{var_extResort}", resortExtValue)
//						// Invoking targeted service
//						String fetchResPromoCodeReq = WSClient.createSOAPMessage("OWSFetchResPromotionCode", "DS_01");
//						String fetchResPromoCodeRes = WSClient.processSOAPMessage(fetchResPromoCodeReq);
//
//						if (WSAssert.assertIfElementValueEquals(fetchResPromoCodeRes,
//								"FetchResPromotionCodeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
//
//							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
//							db = WSClient.getDBRow(WSClient.getQuery("QS_01"));
//							LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
//							LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
//							xPath.put("FetchResPromotionCodeResponse_PromotionCode_PromotionCode",
//									"FetchResPromotionCodeResponse_PromotionCode");
//							xPath.put("FetchResPromotionCodeResponse_PromotionCode_PromotionName",
//									"FetchResPromotionCodeResponse_PromotionCode");
//							res = WSClient.getSingleNodeList(fetchResPromoCodeRes, xPath, false, XMLType.RESPONSE);
//							WSAssert.assertEquals(res, db, false);
//						}
//						if (WSAssert.assertIfElementExists(fetchResPromoCodeRes, "Result_Text_TextElement", true)) {
//
//							/****
//							 * Verifying that the error message is populated on the response
//							 ********/
//
//							String message = WSAssert.getElementValue(fetchResPromoCodeRes, "Result_Text_TextElement",
//									XMLType.RESPONSE);
//							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
//						}
//
//						if (WSAssert.assertIfElementExists(fetchResPromoCodeRes,
//								"FetchResPromotionCodeResponse_Result_OperaErrorCode", true)) {
//
//							/****
//							 * Verifying whether the error Message is populated on the response
//							 ****/
//
//							String message = WSAssert.getElementValue(fetchResPromoCodeRes,
//									"FetchResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
//							WSClient.writeToReport(LogStatus.INFO,
//									"The OperaErrorCode displayed in the  response is :" + message);
//						}
//						if (WSAssert.assertIfElementExists(fetchResPromoCodeRes,
//								"FetchResPromotionCodeResponse_Result_GDSError", true)) {
//
//							/****
//							 * Verifying whether the error Message is populated on the response
//							 ****/
//
//							String message = WSAssert.getElementValue(fetchResPromoCodeRes,
//									"FetchResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
//							WSClient.writeToReport(LogStatus.INFO,
//									"The GDSerror displayed in the response is :" + message);
//						}
//					} else {
//						WSClient.writeToReport(LogStatus.WARNING,
//								"************Blocked : Reservation ID created was null**********");
//					}
//				} else {
//					WSClient.writeToReport(LogStatus.WARNING,
//							"************Blocked : Config prerequisite not met**********");
//				}
//			} else {
//				WSClient.writeToReport(LogStatus.WARNING,
//						"************Blocked : Create Profile Id not created**********");
//			}
//
//		} catch (Exception e) {
//			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
//		} finally {
//			// Cancel Reservation
//			try {
//				if(!WSClient.getData("{var_resvId}").equals(""))
//				CancelReservation.cancelReservation("DS_02");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}

	@Test(groups = { "sanity", "FetchReservationPromotionCode", "ResvAdvanced","FetchResPromotionCode", "OWS", "in-QA" })
	/*****
	 * Verify if Promotion codes are fetched for passed Reservation ID with
	 * mandatory fields in the Fetch Reservation Promotion Code Request
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a Promotion Codes available
	 *****/
	public void fetchResPromotionCode_38442() {
		try {

			String testName = "fetchResPromotionCode_38442";
			WSClient.startTest(testName,
					"Verify that Promotion codes are fetched for the given Reservation ID",
					"sanity");

			String resortOperaValue = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));

			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsresort}", resort);
			// *********Prerequisite: Create Profile**************//

			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PromotionCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_promoCode}", OperaPropConfig.getDataSetForCode("PromotionCode", "DS_01"));
			
					// ********Prerequisite: Create Reservation***************//

					HashMap<String, String> resv = CreateReservation.createReservation("DS_07");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					if (!reservationId.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
						WSClient.setData("{var_resvId}", reservationId);

						// WSClient.setData("{var_extResort}", resortExtValue)
						// Invoking targeted service
						String fetchResPromoCodeReq = WSClient.createSOAPMessage("OWSFetchResPromotionCode", "DS_01");
						String fetchResPromoCodeRes = WSClient.processSOAPMessage(fetchResPromoCodeReq);

						if (WSAssert.assertIfElementValueEquals(fetchResPromoCodeRes,
								"FetchResPromotionCodeResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
							db = WSClient.getDBRow(WSClient.getQuery("QS_01"));
							LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
							xPath.put("FetchResPromotionCodeResponse_PromotionCode_PromotionCode",
									"FetchResPromotionCodeResponse_PromotionCode");
							xPath.put("FetchResPromotionCodeResponse_PromotionCode_PromotionName",
									"FetchResPromotionCodeResponse_PromotionCode");
							res = WSClient.getSingleNodeList(fetchResPromoCodeRes, xPath, false, XMLType.RESPONSE);
							WSAssert.assertEquals(res, db, false);
						}
						if (WSAssert.assertIfElementExists(fetchResPromoCodeRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the response
							 ********/

							String message = WSAssert.getElementValue(fetchResPromoCodeRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}

						if (WSAssert.assertIfElementExists(fetchResPromoCodeRes,
								"FetchResPromotionCodeResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated on the response
							 ****/

							String message = WSAssert.getElementValue(fetchResPromoCodeRes,
									"FetchResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is :" + message);
						}
						if (WSAssert.assertIfElementExists(fetchResPromoCodeRes,
								"FetchResPromotionCodeResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is populated on the response
							 ****/

							String message = WSAssert.getElementValue(fetchResPromoCodeRes,
									"FetchResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is :" + message);
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Reservation ID created was null**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"************Blocked : Config prerequisite not met**********");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked : Create Profile Id not created**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchReservationPromotionCode","FetchResPromotionCode", "ResvAdvanced", "OWS" })
	/*****
	 * Verify if proper error message is returned when already removed reservation
	 * id is provided in the request
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a Promotion Codes available
	 *****/
	public void fetchResPromotionCode_39041() {
		try {

			String testName = "fetchResPromotionCode_39041";
			WSClient.startTest(testName,
					"Verify that proper error message is returned when already removed reservation id is provided in the request",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));

			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsresort}", resort);
			// *********Prerequisite: Create Profile**************//

			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PromotionCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_promoCode}", OperaPropConfig.getDataSetForCode("PromotionCode", "DS_01"));
					
					// ********Prerequisite: Create Reservation***************//

					HashMap<String, String> resv = CreateReservation.createReservation("DS_07");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					if (!reservationId.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
						WSClient.setData("{var_resvId}", reservationId);
						// WSClient.setData("{var_extResort}", resortExtValue)

						String removeReservationReq = WSClient.createSOAPMessage("RemoveReservation", "DS_01");
						String removeReservationRes = WSClient.processSOAPMessage(removeReservationReq);

						if (WSAssert.assertIfElementExists(removeReservationRes, "RemoveReservationRS_Success",
								false)) {

							// Invoking targeted service
							String fetchResPromoCodeReq = WSClient.createSOAPMessage("OWSFetchResPromotionCode",
									"DS_01");
							String fetchResPromoCodeRes = WSClient.processSOAPMessage(fetchResPromoCodeReq);

							if (WSAssert.assertIfElementValueEquals(fetchResPromoCodeRes,
									"FetchResPromotionCodeResponse_Result_resultStatusFlag", "FAIL", false))
							{
								
							}
							
							if (WSAssert.assertIfElementExists(fetchResPromoCodeRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the response
								 ********/

								String message = WSAssert.getElementValue(fetchResPromoCodeRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
								// WSAssert.assertIfElementValueEquals(fetchResPromoCodeRes,
								// "Result_Text_TextElement", "Opera DB Call Did Not Return Any Promotion code",
								// false);

								if (WSAssert.assertIfElementExists(fetchResPromoCodeRes,
										"FetchResPromotionCodeResponse_Result_OperaErrorCode", true)) {

									/****
									 * Verifying whether the error Message is populated on the response
									 ****/

									String message = WSAssert.getElementValue(fetchResPromoCodeRes,
											"FetchResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The OperaErrorCode displayed in the  response is :" + message + "</b>");
								}
							if (WSAssert.assertIfElementExists(fetchResPromoCodeRes,
									"FetchResPromotionCodeResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is populated on the response
								 ****/

								String message = WSAssert.getElementValue(fetchResPromoCodeRes,
										"FetchResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The GDSerror displayed in the response is :" + message + "</b>");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"************Blocked : Reservation ID not deleted**********");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Reservation ID created was null**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"************Blocked : Config prerequisite not met**********");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked : Create Profile Id not created**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "FetchReservationPromotionCode","FetchResPromotionCode", "ResvAdvanced", "OWS" })
	/*****
	 * verify if proper error message is returned when reservation id's field left
	 * empty
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a Promotion Codes available
	 *****/
	public void fetchResPromotionCode_39043() {
		try {

			String testName = "fetchResPromotionCode_39043";
			WSClient.startTest(testName,
					"verify that proper error message is returned when reservation id's field left empty",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));

			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsresort}", resort);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "PromotionCode" })) {

				WSClient.setData("{var_promoCode}", OperaPropConfig.getDataSetForCode("PromotionCode", "DS_01"));
				

				WSClient.setData("{var_resvId}", "");

				// Invoking targeted service
				String fetchResPromoCodeReq = WSClient.createSOAPMessage("OWSFetchResPromotionCode", "DS_01");
				String fetchResPromoCodeRes = WSClient.processSOAPMessage(fetchResPromoCodeReq);

				if (WSAssert.assertIfElementValueEquals(fetchResPromoCodeRes,
						"FetchResPromotionCodeResponse_Result_resultStatusFlag", "FAIL", false)) {
					// WSAssert.assertIfElementValueEquals(fetchResPromoCodeRes,
					// "Result_Text_TextElement","Opera Reservation Id or Key Track 2 is Missing in
					// Request Message", false);

				}

				if (WSAssert.assertIfElementExists(fetchResPromoCodeRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the response
					 ********/

					String message = WSAssert.getElementValue(fetchResPromoCodeRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The text displayed in the response is :" + message + "</b>");
				}
				if (WSAssert.assertIfElementExists(fetchResPromoCodeRes,
						"FetchResPromotionCodeResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the response
					 ****/

					String message = WSAssert.getElementValue(fetchResPromoCodeRes,
							"FetchResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The OperaErrorCode displayed in the  response is :" + message + "</b>");
				}
				if (WSAssert.assertIfElementExists(fetchResPromoCodeRes,
						"FetchResPromotionCodeResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the response
					 ****/

					String message = WSAssert.getElementValue(fetchResPromoCodeRes,
							"FetchResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The GDSerror displayed in the response is :" + message + "</b>");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "FetchReservationPromotionCode","FetchResPromotionCode", "ResvAdvanced", "OWS" })
	/*****
	 * verify if proper error message is returned when invalid reservation id is
	 * provided like a random string
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a Promotion Codes available
	 *****/
	public void fetchResPromotionCode_39044() {
		try {

			String testName = "fetchResPromotionCode_39044";
			WSClient.startTest(testName,
					"verify that proper error message is returned when invalid reservation id is provided like a random string",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));

			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsresort}", resort);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "PromotionCode" })) {
				WSClient.setData("{var_promoCode}", OperaPropConfig.getDataSetForCode("PromotionCode", "DS_01"));
				

				String fetchResPromoCodeReq = WSClient.createSOAPMessage("OWSFetchResPromotionCode", "DS_02");
				String fetchResPromoCodeRes = WSClient.processSOAPMessage(fetchResPromoCodeReq);

				if (WSAssert.assertIfElementValueEquals(fetchResPromoCodeRes,
						"FetchResPromotionCodeResponse_Result_resultStatusFlag", "FAIL", false)) {

				}
				if (WSAssert.assertIfElementExists(fetchResPromoCodeRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the response
					 ********/

					String message = WSAssert.getElementValue(fetchResPromoCodeRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The text displayed in the response is :" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(fetchResPromoCodeRes,
						"FetchResPromotionCodeResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the response
					 ****/

					String message = WSAssert.getElementValue(fetchResPromoCodeRes,
							"FetchResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The OperaErrorCode displayed in the  response is :" + message + "</b>");
				}
				if (WSAssert.assertIfElementExists(fetchResPromoCodeRes,
						"FetchResPromotionCodeResponse_Result_GDSError", true)) {

					/****
					 * Verifying whether the error Message is populated on the response
					 ****/

					String message = WSAssert.getElementValue(fetchResPromoCodeRes,
							"FetchResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The GDSerror displayed in the response is :" + message + "</b>");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "FetchReservationPromotionCode","FetchResPromotionCode", "ResvAdvanced", "OWS" })
	/*****
	 * Verify if Promotion codes are fetched for passed confirmation ID with
	 * mandatory fields in the Fetch Reservation Promotion Code Request
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a Promotion Codes available
	 *****/
	public void fetchResPromotionCode_39045() {
		try {

			String testName = "fetchResPromotionCode_39045";
			WSClient.startTest(testName,
					"Verify that Promotion codes are fetched for passed confirmation ID with mandatory fields in the Fetch Reservation Promotion Code Request",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String channel = OWSLib.getChannel();

			// ********* Setting the OWS Header**************//
			OPERALib.setOperaHeader(OPERALib.getUserName());
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
					OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));

			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsresort}", resort);
			// *********Prerequisite: Create Profile**************//

			String operaProfileID = CreateProfile.createProfile("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PromotionCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_promoCode}", OperaPropConfig.getDataSetForCode("PromotionCode", "DS_01"));
					
					// ********Prerequisite: Create Reservation***************//

					HashMap<String, String> resv = CreateReservation.createReservation("DS_07");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					if (!reservationId.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
						WSClient.setData("{var_resvId}", reservationId);
						WSClient.setData("{var_confirmationId}", confirmationId);
						// WSClient.setData("{var_extResort}", resortExtValue)
						String fetchResPromoCodeReq = WSClient.createSOAPMessage("OWSFetchResPromotionCode", "DS_03");
						String fetchResPromoCodeRes = WSClient.processSOAPMessage(fetchResPromoCodeReq);

						if (WSAssert.assertIfElementValueEquals(fetchResPromoCodeRes,
								"FetchResPromotionCodeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
							db = WSClient.getDBRow(WSClient.getQuery("QS_02"));
							LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
							xPath.put("FetchResPromotionCodeResponse_PromotionCode_PromotionCode",
									"FetchResPromotionCodeResponse_PromotionCode");
							xPath.put("FetchResPromotionCodeResponse_PromotionCode_PromotionName",
									"FetchResPromotionCodeResponse_PromotionCode");
							res = WSClient.getSingleNodeList(fetchResPromoCodeRes, xPath, false, XMLType.RESPONSE);
							WSAssert.assertEquals(res, db, false);
						}
						
						if (WSAssert.assertIfElementExists(fetchResPromoCodeRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the response
							 ********/

							String message = WSAssert.getElementValue(fetchResPromoCodeRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(fetchResPromoCodeRes,
								"FetchResPromotionCodeResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated on the response
							 ****/

							String message = WSAssert.getElementValue(fetchResPromoCodeRes,
									"FetchResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is :" + message);
						}
						if (WSAssert.assertIfElementExists(fetchResPromoCodeRes,
								"FetchResPromotionCodeResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is populated on the response
							 ****/

							String message = WSAssert.getElementValue(fetchResPromoCodeRes,
									"FetchResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is :" + message);
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Reservation ID created was null**********");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"************Blocked : Config prerequisite not met**********");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked : Create Profile Id not created**********");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// fetch res promotion minimum regression
	@Test(groups = { "minimumRegression", "ResvAdvanced", "FetchReservationPromotionCode","FetchResPromotionCode", "OWS", "in-QA" })

	/*
	 * Method to check the fetch res operation by passing Hotel Code, and
	 * External confirmation number and leg number
	 */

	public void fetchResPromotionCode_41529() {
		try {
			String testName = "fetchResPromotionCode_41529";
			WSClient.startTest(testName,
					"Verify that promotion code are getting retrived when External confirmation number and leg number in the request",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "ResvCancelReason", "PromotionCode" })) {
				// boolean prerequisite = true;

				/*************
				 * Prerequisite : Creating new profile by fetching values required
				 ******************/
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chain);
				String uname = OPERALib.getUserName();
				OPERALib.setOperaHeader(uname);
				String operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
					WSClient.setData("{var_profileId}", operaProfileID);

					/********
					 * Prerequisite : Creating reservation
					 **************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
					WSClient.setData("{var_promoCode}", OperaPropConfig.getDataSetForCode("PromotionCode", "DS_01"));
					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
					WSClient.setData("{var_resvId}", reservationId);
					WSClient.setData("{var_confirmationId}", confirmationId);

					resv = CreateReservation.createReservation("DS_26");
					reservationId = resv.get("reservationId");
					confirmationId = resv.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
					WSClient.setData("{var_resvId}", reservationId);
					WSClient.setData("{var_confirmationId}", confirmationId);
					String legNo = WSClient.getDBRow(WSClient.getQuery("OWSCancelBooking", "QS_04"))
							.get("CONFIRMATION_LEG_NO");
					WSClient.setData("{var_legNo}", legNo);

					/************
					 * Performing the fetchResPromotionCode operation
					 ****************/
					resortOperaValue = OPERALib.getResort();
					String channel = OWSLib.getChannel();

					// ********* Setting the OWS Header**************//
					OPERALib.setOperaHeader(OPERALib.getUserName());
					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
							OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));

					String resort = OWSLib.getChannelResort(resortOperaValue, channel);
					WSClient.setData("{var_resort}", resortOperaValue);
					WSClient.setData("{var_owsresort}", resort);
					
						

						String fetchResPromotionCodeReq = WSClient.createSOAPMessage("OWSFetchResPromotionCode", "DS_04");
						String fetchResPromotionCodeRes = WSClient.processSOAPMessage(fetchResPromotionCodeReq);

						if (WSAssert.assertIfElementValueEquals(fetchResPromotionCodeRes,
								"FetchResPromotionCodeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
							db = WSClient.getDBRow(WSClient.getQuery("QS_03"));
							LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
							xPath.put("FetchResPromotionCodeResponse_PromotionCode_PromotionCode",
									"FetchResPromotionCodeResponse_PromotionCode");
							xPath.put("FetchResPromotionCodeResponse_PromotionCode_PromotionName",
									"FetchResPromotionCodeResponse_PromotionCode");
							res = WSClient.getSingleNodeList(fetchResPromotionCodeRes, xPath, false, XMLType.RESPONSE);
							WSAssert.assertEquals(res, db, false);
						}

						if (WSAssert.assertIfElementExists(fetchResPromotionCodeRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the response
							 ********/

							String message = WSAssert.getElementValue(fetchResPromotionCodeRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message + "</b>");
						}
						
						if (WSAssert.assertIfElementExists(fetchResPromotionCodeRes,
								"FetchResPromotionCodeResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated on the response
							 ****/

							String message = WSAssert.getElementValue(fetchResPromotionCodeRes,
									"FetchResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The OperaErrorCode displayed in the  response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(fetchResPromotionCodeRes,
								"FetchResPromotionCodeResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is populated on the response
							 ****/

							String message = WSAssert.getElementValue(fetchResPromotionCodeRes,
									"FetchResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The GDSerror displayed in the response is :" + message + "</b>");
						}
				
					
				}
			}
			 else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked :  prerequisites for RateCode or RoomType or SourceCode or MarketCode or PaymentMethod**********");
			 }
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

}
