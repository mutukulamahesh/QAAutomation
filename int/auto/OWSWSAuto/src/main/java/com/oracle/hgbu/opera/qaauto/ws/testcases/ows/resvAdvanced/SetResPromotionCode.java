package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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

public class SetResPromotionCode extends WSSetUp {
	
	
//	@Test(groups = { "minimumRegression", "SetReservationPromotionCode", "SetResPromotionCode", "ResvAdvanced", "OWS" })
//	/*****
//	 * Verify if Promotion is created for passed Reservation ID with mandatory
//	 * fields in the Set Reservation Promotion Code Request
//	 *****/
//	/*****
//	 * * * PreRequisites Required: -->There should be a Promotion Codes available
//	 *****/
//	public void setResPromotionCode_60016() {
//		try {
//
//			String testName = "setResPromotionCode_60016";
//			WSClient.startTest(testName,
//					"Verify that Promotion is created for passed Reservation ID with mandatory fields in the Set Reservation Promotion Code Request"
//					+ " with channel where channel and channel carrier name are different.",
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
//					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
//					String reservationId = resv.get("reservationId");
//					String confirmationId = resv.get("confirmationId");
//					if (!reservationId.equals("error")) {
//						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
//						WSClient.setData("{var_resvId}", reservationId);
//						WSClient.setData("{var_confirmationId}", confirmationId);
//						// WSClient.setData("{var_extResort}", resortExtValue)
//						String setResPromoCodeReq = WSClient.createSOAPMessage("OWSSetResPromotionCode", "DS_01");
//						String setResPromoCodeRes = WSClient.processSOAPMessage(setResPromoCodeReq);
//
//						if (WSAssert.assertIfElementValueEquals(setResPromoCodeRes,
//								"SetResPromotionCodeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
//							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
//							db = WSClient.getDBRow(WSClient.getQuery("QS_01"));
//							LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
//							LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
//							xPath.put("SetResPromotionCodeRequest_PromotionCode",
//									"SetResPromotionCodeRequest_PromotionCode");
//							res = WSClient.getSingleNodeList(setResPromoCodeReq, xPath, false, XMLType.REQUEST);
//							WSAssert.assertEquals(db, res, false);
//						}
//						if (WSAssert.assertIfElementExists(setResPromoCodeRes, "Result_Text_TextElement", true)) {
//
//							/****
//							 * Verifying that the error message is populated on the response
//							 ********/
//
//							String message = WSAssert.getElementValue(setResPromoCodeRes, "Result_Text_TextElement",
//									XMLType.RESPONSE);
//							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
//						}
//
//						if (WSAssert.assertIfElementExists(setResPromoCodeRes,
//								"SetResPromotionCodeResponse_Result_OperaErrorCode", true)) {
//
//							/****
//							 * Verifying whether the error Message is populated on the response
//							 ****/
//
//							String message = WSAssert.getElementValue(setResPromoCodeRes,
//									"SetResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
//							WSClient.writeToReport(LogStatus.INFO,
//									"The OperaErrorCode displayed in the  response is :" + message);
//						}
//						if (WSAssert.assertIfElementExists(setResPromoCodeRes,
//								"SetResPromotionCodeResponse_Result_GDSError", true)) {
//
//							/****
//							 * Verifying whether the error Message is populated on the response
//							 ****/
//
//							String message = WSAssert.getElementValue(setResPromoCodeRes,
//									"SetResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
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

	@Test(groups = { "sanity", "SetReservationPromotionCode", "SetResPromotionCode", "ResvAdvanced", "OWS" })
	/*****
	 * Verify if Promotion is created for passed Reservation ID with mandatory
	 * fields in the Set Reservation Promotion Code Request
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a Promotion Codes available
	 *****/
	public void setResPromotionCode_38361() {
		try {

			String testName = "setResPromotionCode_38361";
			WSClient.startTest(testName,
					"Verify that Promotion is created for passed Reservation ID with mandatory fields in the Set Reservation Promotion Code Request",
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

					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					if (!reservationId.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
						WSClient.setData("{var_resvId}", reservationId);
						WSClient.setData("{var_confirmationId}", confirmationId);
						// WSClient.setData("{var_extResort}", resortExtValue)
						String setResPromoCodeReq = WSClient.createSOAPMessage("OWSSetResPromotionCode", "DS_01");
						String setResPromoCodeRes = WSClient.processSOAPMessage(setResPromoCodeReq);

						if (WSAssert.assertIfElementValueEquals(setResPromoCodeRes,
								"SetResPromotionCodeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
							db = WSClient.getDBRow(WSClient.getQuery("QS_01"));
							LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
							xPath.put("SetResPromotionCodeRequest_PromotionCode",
									"SetResPromotionCodeRequest_PromotionCode");
							res = WSClient.getSingleNodeList(setResPromoCodeReq, xPath, false, XMLType.REQUEST);
							WSAssert.assertEquals(db, res, false);
						}
						if (WSAssert.assertIfElementExists(setResPromoCodeRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the response
							 ********/

							String message = WSAssert.getElementValue(setResPromoCodeRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}

						if (WSAssert.assertIfElementExists(setResPromoCodeRes,
								"SetResPromotionCodeResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated on the response
							 ****/

							String message = WSAssert.getElementValue(setResPromoCodeRes,
									"SetResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is :" + message);
						}
						if (WSAssert.assertIfElementExists(setResPromoCodeRes,
								"SetResPromotionCodeResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is populated on the response
							 ****/

							String message = WSAssert.getElementValue(setResPromoCodeRes,
									"SetResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
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

	@Test(groups = { "minimumRegression", "SetReservationPromotionCode","SetResPromotionCode", "ResvAdvanced", "OWS" })
	/*****
	 * Verify if Promotion is created for passed Confirmation ID with mandatory
	 * fields in the Set Reservation Promotion Code Request
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a Promotion Codes available
	 *****/
	public void setResPromotionCode_38362() {
		try {

			String testName = "setResPromotionCode_38362";
			WSClient.startTest(testName,
					"Verify that Promotion is created for passed Confirmation ID with mandatory fields in the Set Reservation Promotion Code Request",
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

					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					if (!reservationId.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
						WSClient.setData("{var_resvId}", reservationId);
						WSClient.setData("{var_confirmationId}", confirmationId);
						// WSClient.setData("{var_extResort}", resortExtValue)
						String setResPromoCodeReq = WSClient.createSOAPMessage("OWSSetResPromotionCode", "DS_02");
						String setResPromoCodeRes = WSClient.processSOAPMessage(setResPromoCodeReq);

						if (WSAssert.assertIfElementValueEquals(setResPromoCodeRes,
								"SetResPromotionCodeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
							db = WSClient.getDBRow(WSClient.getQuery("QS_02"));
							LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
							xPath.put("SetResPromotionCodeRequest_PromotionCode",
									"SetResPromotionCodeRequest_PromotionCode");
							res = WSClient.getSingleNodeList(setResPromoCodeReq, xPath, false, XMLType.REQUEST);
							WSAssert.assertEquals(db, res, false);
						}
						if (WSAssert.assertIfElementExists(setResPromoCodeRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the response
							 ********/

							String message = WSAssert.getElementValue(setResPromoCodeRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}

						if (WSAssert.assertIfElementExists(setResPromoCodeRes,
								"SetResPromotionCodeResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated on the response
							 ****/

							String message = WSAssert.getElementValue(setResPromoCodeRes,
									"SetResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is :" + message);
						}
						if (WSAssert.assertIfElementExists(setResPromoCodeRes,
								"SetResPromotionCodeResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is populated on the response
							 ****/

							String message = WSAssert.getElementValue(setResPromoCodeRes,
									"SetResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
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

	@Test(groups = { "minimumRegression", "SetReservationPromotionCode","SetResPromotionCode", "ResvAdvanced", "OWS" })
	/*****
	 * Verify that when setting duplicate promotion codes no new records are added
	 * in the DB
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a Promotion Codes available
	 *****/
	public void setResPromotionCode_39029() {
		try {

			String testName = "setResPromotionCode_39029";
			WSClient.startTest(testName,
					"Verify that when setting duplicate promotion codes no new records are added in the DB",
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
						String setResPromoCodeReq = WSClient.createSOAPMessage("OWSSetResPromotionCode", "DS_02");
						String setResPromoCodeRes = WSClient.processSOAPMessage(setResPromoCodeReq);

						if (WSAssert.assertIfElementValueEquals(setResPromoCodeRes,
								"SetResPromotionCodeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							db = WSClient.getDBRows(WSClient.getQuery("QS_02"));
							Integer num = db.size();
							LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
							List<LinkedHashMap<String, String>> res = new ArrayList<LinkedHashMap<String, String>>();
							xPath.put("SetResPromotionCodeRequest_PromotionCode",
									"SetResPromotionCodeRequest_PromotionCode");
							res = WSClient.getMultipleNodeList(setResPromoCodeReq, xPath, false, XMLType.REQUEST);
							if (WSAssert.assertEquals(db, res, true)) {
								
								if (num == 1) {
									WSClient.writeToReport(LogStatus.PASS, "No duplicate entries were created!");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Number of records retrieved are " + num.toString());
								}
							}
						}
						if (WSAssert.assertIfElementExists(setResPromoCodeRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on the response
							 ********/

							String message = WSAssert.getElementValue(setResPromoCodeRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}

						if (WSAssert.assertIfElementExists(setResPromoCodeRes,
								"SetResPromotionCodeResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated on the response
							 ****/

							String message = WSAssert.getElementValue(setResPromoCodeRes,
									"SetResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is :" + message);
						}
						if (WSAssert.assertIfElementExists(setResPromoCodeRes,
								"SetResPromotionCodeResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is populated on the response
							 ****/

							String message = WSAssert.getElementValue(setResPromoCodeRes,
									"SetResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
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

	@Test(groups = { "minimumRegression", "SetReservationPromotionCode","SetResPromotionCode", "ResvAdvanced", "OWS" })
	/*****
	 * Verify if proper error message is returned when no reservation id is provided
	 * in the request
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a Promotion Codes available
	 *****/
	public void setResPromotionCode_39031() {
		String reservationId;
		try {

			String testName = "setResPromotionCode_39031";
			WSClient.startTest(testName,
					"Verify that proper error message is returned when no reservation id is provided in the request",
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

				String setResPromoCodeReq = WSClient.createSOAPMessage("OWSSetResPromotionCode", "DS_01");
				String setResPromoCodeRes = WSClient.processSOAPMessage(setResPromoCodeReq);

				if (WSAssert.assertIfElementValueEquals(setResPromoCodeRes,
						"SetResPromotionCodeResponse_Result_resultStatusFlag", "FAIL", false)) {
					// if (WSAssert.assertIfElementValueEquals(setResPromoCodeRes,
					// "Result_Text_TextElement",
					// "Opera Reservation Id or Key Track 2 is Missing in Request Message", true)) {
					// WSClient.writeToReport(LogStatus.PASS, "Proper error message was displayed");
					// } else {
					// WSClient.writeToReport(LogStatus.FAIL, "Expected error message was not
					// displayed");
					// }
				}
				if (WSAssert.assertIfElementExists(setResPromoCodeRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the response
					 ********/

					String message = WSAssert.getElementValue(setResPromoCodeRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The text displayed in the response is :" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(setResPromoCodeRes,
						"SetResPromotionCodeResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the response
					 ****/

					String message = WSAssert.getElementValue(setResPromoCodeRes,
							"SetResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The OperaErrorCode displayed in the  response is :" + message + "</b>");
				}
				if (WSAssert.assertIfElementExists(setResPromoCodeRes, "SetResPromotionCodeResponse_Result_GDSError",
						true)) {

					/****
					 * Verifying whether the error Message is populated on the response
					 ****/

					String message = WSAssert.getElementValue(setResPromoCodeRes,
							"SetResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The GDSerror displayed in the response is :" + message + "</b>");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	@Test(groups = { "minimumRegression", "SetReservationPromotionCode","SetResPromotionCode", "ResvAdvanced", "OWS" })
	/*****
	 * Verify if proper error message is returned when invalid reservation id is
	 * provided in the request
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a Promotion Codes available
	 *****/
	public void setResPromotionCode_39033() {
		try {

			String testName = "setResPromotionCode_39033";
			WSClient.startTest(testName,
					"Verify that proper error message is returned when invalid reservation id is provided in the request",
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
				

				String setResPromoCodeReq = WSClient.createSOAPMessage("OWSSetResPromotionCode", "DS_03");
				String setResPromoCodeRes = WSClient.processSOAPMessage(setResPromoCodeReq);

				if (WSAssert.assertIfElementValueEquals(setResPromoCodeRes,
						"SetResPromotionCodeResponse_Result_resultStatusFlag", "FAIL", false)) {
					/*
					 * if(WSAssert.assertIfElementValueEquals(setResPromoCodeRes,
					 * "Result_Text_TextElement",
					 * "Opera Reservation Id or Key Track 2 is Missing in Request Message", true)) {
					 * WSClient.writeToReport(LogStatus.PASS, "Proper error message was displayed");
					 * } else { WSClient.writeToReport(LogStatus.FAIL,
					 * "Expected error message was not displayed"); }
					 */
				}
				if (WSAssert.assertIfElementExists(setResPromoCodeRes, "Result_Text_TextElement", true)) {

					/****
					 * Verifying that the error message is populated on the response
					 ********/

					String message = WSAssert.getElementValue(setResPromoCodeRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The text displayed in the response is :" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(setResPromoCodeRes,
						"SetResPromotionCodeResponse_Result_OperaErrorCode", true)) {

					/****
					 * Verifying whether the error Message is populated on the response
					 ****/

					String message = WSAssert.getElementValue(setResPromoCodeRes,
							"SetResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The OperaErrorCode displayed in the  response is :" + message + "</b>");
				}
				if (WSAssert.assertIfElementExists(setResPromoCodeRes, "SetResPromotionCodeResponse_Result_GDSError",
						true)) {

					/****
					 * Verifying whether the error Message is populated on the response
					 ****/

					String message = WSAssert.getElementValue(setResPromoCodeRes,
							"SetResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The GDSerror displayed in the response is :" + message + "</b>");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	@Test(groups = { "minimumRegression", "SetReservationPromotionCode","SetResPromotionCode", "ResvAdvanced", "OWS" })
	/*****
	 * Verify if proper error message is returned when already removed reservation
	 * id is provided in the request
	 *****/
	/*****
	 * * * PreRequisites Required: -->There should be a Promotion Codes available
	 *****/
	public void setResPromotionCode_39035() {
		try {

			String testName = "setResPromotionCode_39035";
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
						WSClient.setData("{var_confirmationId}", confirmationId);
						// WSClient.setData("{var_extResort}", resortExtValue)

						String removeReservationReq = WSClient.createSOAPMessage("RemoveReservation", "DS_01");
						String removeReservationRes = WSClient.processSOAPMessage(removeReservationReq);

						if (WSAssert.assertIfElementExists(removeReservationRes, "RemoveReservationRS_Success",
								false)) {

							String setResPromoCodeReq = WSClient.createSOAPMessage("OWSSetResPromotionCode", "DS_01");
							String setResPromoCodeRes = WSClient.processSOAPMessage(setResPromoCodeReq);

							if (WSAssert.assertIfElementValueEquals(setResPromoCodeRes,
									"SetResPromotionCodeResponse_Result_resultStatusFlag", "FAIL", false)) {

							}
							if (WSAssert.assertIfElementExists(setResPromoCodeRes,
									"SetResPromotionCodeResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is populated on the response
								 ****/

								String message = WSAssert.getElementValue(setResPromoCodeRes,
										"SetResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The OperaErrorCode displayed in the  response is :" + message + "</b>");
							}
							if (WSAssert.assertIfElementExists(setResPromoCodeRes,
									"SetResPromotionCodeResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is populated on the response
								 ****/

								String message = WSAssert.getElementValue(setResPromoCodeRes,
										"SetResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The GDSerror displayed in the response is :" + message + "</b>");
							}
							
							if (WSAssert.assertIfElementExists(setResPromoCodeRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated on the response
								 ********/

								String message = WSAssert.getElementValue(setResPromoCodeRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
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
		} finally {
		}
	}

	// set res promotion minimum regression
	@Test(groups = { "minimumRegression", "ResvAdvanced", "SetReservationPromotionCode","SetResPromotionCode", "OWS", "in-QA" })

	/*
	 * Method to check the CANCEL BOOKING operation by passing Hotel Code, and
	 * External confirmation number and leg number
	 */

	public void setResPromotionCode_41528() {
		try {
			String testName = "setResPromotionCode_41528";
			WSClient.startTest(testName,
					"Verify that promotion code are getting set when External confirmation number and leg number in the request",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "ResvCancelReason" })) {
				// boolean prerequisite = true;

				/*************
				 * Prerequisite : Creating new profile by fetching values required
				 ******************/
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();

				// ********* Setting the OWS Header**************//
				OPERALib.setOperaHeader(OPERALib.getUserName());
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resortOperaValue, channel));

				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_owsresort}", resort);
				
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
					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
					WSClient.setData("{var_resvId}", reservationId);
					WSClient.setData("{var_confirmationId}", confirmationId);
					
					
					resv = CreateReservation.createReservation("DS_10");
					reservationId = resv.get("reservationId");
					confirmationId = resv.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
					WSClient.setData("{var_resvId}", reservationId);
					WSClient.setData("{var_confirmationId}", confirmationId);
					String legNo = WSClient.getDBRow(WSClient.getQuery("OWSCancelBooking", "QS_04"))
							.get("CONFIRMATION_LEG_NO");
					WSClient.setData("{var_legNo}", legNo);

					/************
					 * Performing the setResPromotionCode operation
					 ****************/
					

					if (OperaPropConfig.getPropertyConfigResults(new String[] { "PromotionCode" })) {

						WSClient.setData("{var_promoCode}", OperaPropConfig.getDataSetForCode("PromotionCode", "DS_01"));
						WSClient.setData("{var_resort}", resort);

					String setResPromotionCodeReq = WSClient.createSOAPMessage("OWSSetResPromotionCode", "DS_04");
					String setResPromotionCodeRes = WSClient.processSOAPMessage(setResPromotionCodeReq);
					if (WSAssert.assertIfElementExists(setResPromotionCodeRes,
							"SetResPromotionCodeResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(setResPromotionCodeRes,
								"SetResPromotionCodeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
							WSClient.setData("{var_resort}", resortOperaValue);
							db = WSClient.getDBRow(WSClient.getQuery("QS_03"));
							LinkedHashMap<String, String> res = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
							xPath.put("SetResPromotionCodeRequest_PromotionCode",
									"SetResPromotionCodeRequest_PromotionCode");
							res = WSClient.getSingleNodeList(setResPromotionCodeReq, xPath, false, XMLType.REQUEST);
							WSAssert.assertEquals(db, res, false);
						}
					}

					if (WSAssert.assertIfElementExists(setResPromotionCodeRes,
							"SetResPromotionCodeResponse_Result_OperaErrorCode", true)) {

						/****
						 * Verifying whether the error Message is populated on the response
						 ****/

						String message = WSAssert.getElementValue(setResPromotionCodeRes,
								"SetResPromotionCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The OperaErrorCode displayed in the  response is :" + message + "</b>");
					}
					if (WSAssert.assertIfElementExists(setResPromotionCodeRes,
							"SetResPromotionCodeResponse_Result_GDSError", true)) {

						/****
						 * Verifying whether the error Message is populated on the response
						 ****/

						String message = WSAssert.getElementValue(setResPromotionCodeRes,
								"SetResPromotionCodeResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The GDSerror displayed in the response is :" + message + "</b>");
					}

					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
				}
			} else {
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
