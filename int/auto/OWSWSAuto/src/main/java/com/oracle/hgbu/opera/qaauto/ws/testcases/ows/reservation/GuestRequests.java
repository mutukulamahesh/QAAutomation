package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

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

public class GuestRequests extends WSSetUp {

	String profileID = "";
	HashMap<String, String> resvID = new HashMap<>();

	@Test(groups = { "sanity", "GuestRequests", "Reservation", "OWS" })

	public void guestRequests_39523() throws Exception {

		try {

			String testName = "guestRequests_39523";
			WSClient.startTest(testName, "Verify that the comments are inserted sucessfully", "sanity");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID.toString() + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						WSClient.setData("{var_actType}", "ADD");
						WSClient.setData("{var_resType}", "COMMENTS");

						/*******************
						 * OWS Guest Requests Operation
						 ************************/
						String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_01");
						String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
						if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
								"GuestRequestsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
							HashMap<String, String> xpath = new HashMap<String, String>();
							xpath.put("Comments_Comment_Text", "GuestRequests_Comments_Comment");
							xpath.put("Comments_Comment_CommentId", "GuestRequests_Comments_Comment");
							xpath.put("Comments_Comment_InternalYn", "GuestRequests_Comments_Comment");
							// xpath.put("GuestRequests_Comments_Comment_guestViewable","GuestRequests_Comments_Comment");
							expectedValues = WSClient.getSingleNodeList(GuestResponseRes, xpath, false,
									XMLType.RESPONSE);
							actualValues = WSClient.getDBRow(WSClient.getQuery("QS_01"));
							WSAssert.assertEquals(expectedValues, actualValues, false);
							// WSClient.writeToReport(LogStatus.INFO,
							// "<b>Successfully added comments"+"</b>");

						}
					}

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}

	@Test(groups = { "minimumRegression", "GuestRequests", "Reservation", "OWS" })

	public void guestRequests_39522() throws Exception {

		try {

			String testName = "guestRequests_39522";
			WSClient.startTest(testName, "Verify that the comments are updated successfully", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation with comments
					 ************************/

					resvID = CreateReservation.createReservation("DS_19");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
						LinkedHashMap<String, String> confirmationNumber = new LinkedHashMap<String, String>();
						confirmationNumber = WSClient.getDBRow(WSClient.getQuery("QS_02"));
						WSClient.setData("{var_confNumber}", confirmationNumber.get("CONFIRMATION_NO").toString());
						LinkedHashMap<String, String> comment = new LinkedHashMap<String, String>();

						comment = WSClient.getDBRow(WSClient.getQuery("CreateReservation", "QS_04"));
						WSClient.setData("{var_commentId}", comment.get("COMMENT_ID").toString());
						WSClient.writeToReport(LogStatus.INFO, "<b>Comment ID:" + comment.get("COMMENT_ID").toString());
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						WSClient.setData("{var_actType}", "UPDATE");
						WSClient.setData("{var_resType}", "COMMENTS");

						/*******************
						 * OWS Guest Requests Operation
						 ************************/

						String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_08");
						String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
						if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
								"GuestRequestsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
							HashMap<String, String> xpath = new HashMap<String, String>();

							xpath.put("Comments_Comment_Text", "GuestRequests_Comments_Comment");
							xpath.put("Comments_Comment_CommentId", "GuestRequests_Comments_Comment");

							// xpath.put("GuestRequests_Comments_Comment_guestViewable","GuestRequests_Comments_Comment");
							expectedValues = WSClient.getSingleNodeList(GuestResponseRes, xpath, false,
									XMLType.RESPONSE);

							actualValues = WSClient.getDBRow(WSClient.getQuery("QS_08"));

							WSAssert.assertEquals(expectedValues, actualValues, false);
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully updated comment" + "</b>");

						}
					}

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}

	@Test(groups = { "minimumRegression", "GuestRequests", "Reservation", "OWS" })

	public void guestRequests_41431() throws Exception {

		try {

			String testName = "guestRequests_41431";
			WSClient.startTest(testName, "Verify that the comments are deleted successfully", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				WSClient.writeToReport(LogStatus.INFO, "rec2");

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation with comments
					 ************************/

					resvID = CreateReservation.createReservation("DS_19");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
						LinkedHashMap<String, String> confirmationNumber = new LinkedHashMap<String, String>();
						confirmationNumber = WSClient.getDBRow(WSClient.getQuery("CreateReservation", "QS_02"));
						WSClient.setData("{var_confNumber}", confirmationNumber.get("CONFIRMATION_NO").toString());
						LinkedHashMap<String, String> comment = new LinkedHashMap<String, String>();
						comment = WSClient.getDBRow(WSClient.getQuery("CreateReservation", "QS_04"));
						WSClient.setData("{var_commentId}", comment.get("COMMENT_ID").toString());
						WSClient.writeToReport(LogStatus.INFO, comment.get("COMMENT_ID").toString() + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						WSClient.setData("{var_actType}", "DELETE");
						WSClient.setData("{var_resType}", "COMMENTS");

						/*******************
						 * OWS Guest Requests Operation
						 ************************/
						String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_11");
						String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
						if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
								"GuestRequestsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_06"));
							if (WSAssert.assertEquals("0", db.get("COUNT"), true))
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully deleted comments" + "</b>");
							else
								WSClient.writeToReport(LogStatus.FAIL, "<b>Unable to delete comment" + "</b>");

						}
					}

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}

	// Negative scenario -- In Valid as per
	// https://jira.oraclecorp.com/jira/browse/HOWSS-5754

	// @Test(groups = { "minimumRegression", "GuestRequests", "Reservation",
	// "OWS"})
	//
	// public void guestRequests_41433() throws Exception {
	//
	// try {
	//
	// String testName = "guestRequests_41433";
	// WSClient.startTest(testName,
	// "Verify that an error message is obtained when updating traces of a
	// Reservation that has no traces attached to it",
	// "minimumRegression");
	// if (OperaPropConfig.getPropertyConfigResults(
	// new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode",
	// "PaymentMethod" })) {
	// String resortOperaValue = OPERALib.getResort();
	//
	// String channel = OWSLib.getChannel();
	// String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsResort}", owsResort);
	// String pwd = OPERALib.getPassword();
	// String channelType = OWSLib.getChannelType(channel);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	// channel);
	// String uname = OPERALib.getUserName();
	//
	// WSClient.setData("{var_resort}", resortOperaValue);
	//
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	//
	// /************
	// * Prerequisite : Room type, Rate Plan Code, Source Code, Market
	// * Code
	// *********************************/
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_payment}",
	// OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	//
	// /************
	// * Prerequisite 1: Create profile
	// *********************************/
	//
	// profileID = CreateProfile.createProfile("DS_01");
	// if (!profileID.equals("error")) {
	// WSClient.setData("{var_profileId}", profileID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID +
	// "</b>");
	//
	// /*******************
	// * Prerequisite 2:Create a Reservation with comments
	// ************************/
	//
	// resvID = CreateReservation.createReservation("DS_01");
	// if (!resvID.equals("error")) {
	//
	// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
	// LinkedHashMap<String, String> confirmationNumber = new
	// LinkedHashMap<String, String>();
	// confirmationNumber = WSClient.getDBRow(WSClient.getQuery("QS_02"));
	// WSClient.setData("{var_confNumber}",
	// confirmationNumber.get("CONFIRMATION_NO").toString());
	// OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
	// channelCarrier);
	//
	// WSClient.setData("{var_actType}", "UPDATE");
	// WSClient.setData("{var_resType}", "TRACES");
	//
	// /*******************
	// * OWS Guest Requests Operation
	// ************************/
	// String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests",
	// "DS_17");
	// String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "Result_Text_TextElement", true)) {
	//
	// /****
	// * Verifying that the error message is populated on
	// * the response
	// ********/
	//
	// String message = WSAssert.getElementValue(GuestResponseRes,
	// "Result_Text_TextElement",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>The error displayed in the Guest Requests response is :" + message +
	// "</b>");
	// }
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "GuestRequestsResponse_Result_OperaErrorCode", true)) {
	//
	// /****
	// * Verifying whether the error Message is populated
	// * on the response
	// ****/
	//
	// String message = WSAssert.getElementValue(GuestResponseRes,
	// "GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>The error displayed in the Guest Requests response is :" + message +
	// "</b>");
	// }
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "GuestRequestsResponse_Result_resultStatusFlag", false)) {
	//
	// if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
	// "GuestRequestsResponse_Result_resultStatusFlag", "FAIL", false))
	// ;
	//
	// }
	// }
	//
	// }
	//
	// }
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// WSClient.setData("{var_cancelReasonCode}",
	// OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
	// if (!resvID.containsValue("error")) {
	//
	// // CancelReservation.cancelReservation("DS_02");
	// }
	// }
	//
	// }
	//
	// @Test(groups = { "minimumRegression", "GuestRequests", "Reservation",
	// "OWS" })
	//
	// public void guestRequests_41434() throws Exception {
	//
	// try {
	//
	// String testName = "guestRequests_41434";
	// WSClient.startTest(testName,
	// "Verify that an error message is obtained when updating comments of a
	// Reservation that has no comments attached to it",
	// "minimumRegression");
	// if (OperaPropConfig.getPropertyConfigResults(
	// new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode",
	// "PaymentMethod" })) {
	// String resortOperaValue = OPERALib.getResort();
	//
	// String channel = OWSLib.getChannel();
	// String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsResort}", owsResort);
	// String pwd = OPERALib.getPassword();
	// String channelType = OWSLib.getChannelType(channel);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	// channel);
	// String uname = OPERALib.getUserName();
	//
	// WSClient.setData("{var_resort}", resortOperaValue);
	//
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	//
	// /************
	// * Prerequisite : Room type, Rate Plan Code, Source Code, Market
	// * Code
	// *********************************/
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_payment}",
	// OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	//
	// /************
	// * Prerequisite 1: Create profile
	// *********************************/
	//
	// profileID = CreateProfile.createProfile("DS_01");
	// if (!profileID.equals("error")) {
	// WSClient.setData("{var_profileId}", profileID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID +
	// "</b>");
	//
	// /*******************
	// * Prerequisite 2:Create a Reservation with comments
	// ************************/
	//
	// resvID = CreateReservation.createReservation("DS_01");
	// if (!resvID.equals("error")) {
	//
	// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
	// LinkedHashMap<String, String> confirmationNumber = new
	// LinkedHashMap<String, String>();
	// confirmationNumber = WSClient.getDBRow(WSClient.getQuery("QS_02"));
	// WSClient.setData("{var_confNumber}",
	// confirmationNumber.get("CONFIRMATION_NO").toString());
	// OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
	// channelCarrier);
	//
	// WSClient.setData("{var_actType}", "UPDATE");
	// WSClient.setData("{var_resType}", "COMMENTS");
	//
	// /*******************
	// * OWS Guest Requests Operation
	// ************************/
	//
	// String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests",
	// "DS_18");
	// String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "Result_Text_TextElement", true)) {
	//
	// /****
	// * Verifying that the error message is populated on
	// * the response
	// ********/
	//
	// String message = WSAssert.getElementValue(GuestResponseRes,
	// "Result_Text_TextElement",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>The error displayed in the Guest Requests response is :" + message +
	// "</b>");
	// }
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "GuestRequestsResponse_Result_OperaErrorCode", true)) {
	//
	// /****
	// * Verifying whether the error Message is populated
	// * on the response
	// ****/
	//
	// String message = WSAssert.getElementValue(GuestResponseRes,
	// "GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>The error displayed in the Guest Requests response is :" + message +
	// "</b>");
	// }
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "GuestRequestsResponse_Result_resultStatusFlag", false)) {
	//
	// if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
	// "GuestRequestsResponse_Result_resultStatusFlag", "FAIL", false))
	// ;
	//
	// }
	// }
	//
	// }
	//
	// }
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// WSClient.setData("{var_cancelReasonCode}",
	// OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
	// if (!resvID.containsValue("error")) {
	//
	// // CancelReservation.cancelReservation("DS_02");
	// }
	// }
	//
	// }
	//
	// @Test(groups = { "minimumRegression", "GuestRequests", "Reservation",
	// "OWS" })
	//
	// public void guestRequests_41435() throws Exception {
	//
	// try {
	//
	// String testName = "guestRequests_41435";
	// WSClient.startTest(testName,
	// "Verify that an error message is obtained when updating alerts of a
	// Reservation that has no alerts attached to it",
	// "minimumRegression");
	// if (OperaPropConfig.getPropertyConfigResults(
	// new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode",
	// "PaymentMethod" })) {
	// String resortOperaValue = OPERALib.getResort();
	//
	// String channel = OWSLib.getChannel();
	// String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsResort}", owsResort);
	// String pwd = OPERALib.getPassword();
	// String channelType = OWSLib.getChannelType(channel);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	// channel);
	// String uname = OPERALib.getUserName();
	//
	// WSClient.setData("{var_resort}", resortOperaValue);
	//
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	//
	// /************
	// * Prerequisite : Room type, Rate Plan Code, Source Code, Market
	// * Code
	// *********************************/
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_payment}",
	// OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	//
	// /************
	// * Prerequisite 1: Create profile
	// *********************************/
	//
	// profileID = CreateProfile.createProfile("DS_01");
	// if (!profileID.equals("error")) {
	// WSClient.setData("{var_profileId}", profileID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID +
	// "</b>");
	//
	// /*******************
	// * Prerequisite 2:Create a Reservation with comments
	// ************************/
	//
	// resvID = CreateReservation.createReservation("DS_01");
	// if (!resvID.equals("error")) {
	//
	// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
	// LinkedHashMap<String, String> confirmationNumber = new
	// LinkedHashMap<String, String>();
	// confirmationNumber = WSClient.getDBRow(WSClient.getQuery("QS_02"));
	// WSClient.setData("{var_confNumber}",
	// confirmationNumber.get("CONFIRMATION_NO").toString());
	// OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
	// channelCarrier);
	//
	// WSClient.setData("{var_actType}", "UPDATE");
	// WSClient.setData("{var_resType}", "ALERTS");
	//
	// /*******************
	// * OWS Guest Requests Operation
	// ************************/
	// String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests",
	// "DS_18");
	// String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "Result_Text_TextElement", true)) {
	//
	// /****
	// * Verifying that the error message is populated on
	// * the response
	// ********/
	//
	// String message = WSAssert.getElementValue(GuestResponseRes,
	// "Result_Text_TextElement",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>The error displayed in the Guest Requests response is :" + message +
	// "</b>");
	// }
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "GuestRequestsResponse_Result_OperaErrorCode", true)) {
	//
	// /****
	// * Verifying whether the error Message is populated
	// * on the response
	// ****/
	//
	// String message = WSAssert.getElementValue(GuestResponseRes,
	// "GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>The error displayed in the Guest Requests response is :" + message +
	// "</b>");
	// }
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "GuestRequestsResponse_Result_resultStatusFlag", false)) {
	//
	// if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
	// "GuestRequestsResponse_Result_resultStatusFlag", "FAIL", false))
	// ;
	//
	// }
	// }
	//
	// }
	//
	// }
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// WSClient.setData("{var_cancelReasonCode}",
	// OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
	// if (!resvID.containsValue("error")) {
	//
	// // CancelReservation.cancelReservation("DS_02");
	// }
	// }
	//
	// }
	//

	@Test(groups = { "minimumRegression", "GuestRequests", "Reservation", "OWS" })

	public void guestRequests_41436() throws Exception {

		try {

			String testName = "guestRequests_41436";
			WSClient.startTest(testName, "Verify that the alerts are inserted successfully", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "AlertCode" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_alertCode}", OperaPropConfig.getDataSetForCode("AlertCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
						LinkedHashMap<String, String> confirmationNumber = new LinkedHashMap<String, String>();
						confirmationNumber = WSClient.getDBRow(WSClient.getQuery("QS_02"));
						WSClient.setData("{var_confNumber}", confirmationNumber.get("CONFIRMATION_NO").toString());

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						WSClient.setData("{var_actType}", "ADD");
						WSClient.setData("{var_resType}", "ALERTS");

						/*******************
						 * OWS Guest Requests Operation
						 ************************/
						String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_02");
						String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
						if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
								"GuestRequestsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
							HashMap<String, String> xpath = new HashMap<String, String>();
							xpath.put("Alerts_ReservationAlerts_AlertID", "GuestRequests_Alerts_ReservationAlerts");
							xpath.put("Alerts_ReservationAlerts_Code", "GuestRequests_Alerts_ReservationAlerts");
							xpath.put("Alerts_ReservationAlerts_Description", "GuestRequests_Alerts_ReservationAlerts");
							// xpath.put("GuestRequests_Comments_Comment_guestViewable","GuestRequests_Comments_Comment");
							expectedValues = WSClient.getSingleNodeList(GuestResponseRes, xpath, false,
									XMLType.RESPONSE);
							actualValues = WSClient.getDBRow(WSClient.getQuery("QS_03"));
							WSAssert.assertEquals(expectedValues, actualValues, false);
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully inserted alerts" + "</b>");

						}
					}

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}

	@Test(groups = { "minimumRegression", "GuestRequests", "Reservation", "OWS" })

	public void guestRequests_39521() throws Exception {

		try {

			String testName = "guestRequests_39521";
			WSClient.startTest(testName, "Verify that the alerts are updated successfully", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "AlertCode" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_alertCode}", OperaPropConfig.getDataSetForCode("AlertCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
						LinkedHashMap<String, String> confirmationNumber = new LinkedHashMap<String, String>();
						confirmationNumber = WSClient.getDBRow(WSClient.getQuery("QS_02"));
						WSClient.setData("{var_confNumber}", confirmationNumber.get("CONFIRMATION_NO").toString());

						String changeResvReq = WSClient.createSOAPMessage("ChangeReservation", "DS_03");
						String changeResvRes = WSClient.processSOAPMessage(changeResvReq);
						if (WSAssert.assertIfElementExists(changeResvRes, "ChangeReservationRS_Success", true)) {

							LinkedHashMap<String, String> comment = new LinkedHashMap<String, String>();
							comment = WSClient.getDBRow(WSClient.getQuery("QS_02"));
							WSClient.setData("{var_alertId}", comment.get("ALERT_ID").toString());
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							WSClient.setData("{var_actType}", "UPDATE");
							WSClient.setData("{var_resType}", "ALERTS");

							/*******************
							 * OWS Guest Requests Operation
							 ************************/
							String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_12");
							String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
							if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error displayed in the Guest Requests response is :" + message
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(GuestResponseRes,
										"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error displayed in the Guest Requests response is :" + message
												+ "</b>");
							}
							if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
									"GuestRequestsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
								HashMap<String, String> xpath = new HashMap<String, String>();
								xpath.put("Alerts_ReservationAlerts_AlertID", "GuestRequests_Alerts_ReservationAlerts");
								xpath.put("Alerts_ReservationAlerts_Code", "GuestRequests_Alerts_ReservationAlerts");
								xpath.put("Alerts_ReservationAlerts_Description",
										"GuestRequests_Alerts_ReservationAlerts");
								expectedValues = WSClient.getSingleNodeList(GuestResponseRes, xpath, false,
										XMLType.RESPONSE);
								actualValues = WSClient.getDBRow(WSClient.getQuery("QS_09"));
								WSAssert.assertEquals(expectedValues, actualValues, false);
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully updated alerts" + "</b>");

							}
						}
					}

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}

	@Test(groups = { "minimumRegression", "GuestRequests", "Reservation", "OWS" })

	public void guestRequests_41438() throws Exception {

		try {

			String testName = "guestRequests_41438";
			WSClient.startTest(testName, "Verify that an error message is obtained when no confirmation ID is passed",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "DepartmentCode" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_department}", OperaPropConfig.getDataSetForCode("DepartmentCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						WSClient.setData("{var_actType}", "ADD");
						WSClient.setData("{var_resType}", "TRACES");

						/*******************
						 * OWS Guest Requests Operation
						 ************************/
						String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_16");
						String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
						if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_resultStatusFlag", false)) {

							if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
									"GuestRequestsResponse_Result_resultStatusFlag", "FAIL", false))
								;

						}

					}

				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}

	@Test(groups = { "minimumRegression", "GuestRequests", "Reservation", "OWS" })

	public void guestRequests_41440() throws Exception {

		try {

			String testName = "guestRequests_41440";
			WSClient.startTest(testName,
					"Verify that an error message is obtained when an invalid confirmation ID is passed",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(resortOperaValue, channel));
				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_department}", OperaPropConfig.getDataSetForCode("DepartmentCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						WSClient.setData("{var_confNumber}", WSClient.getKeywordData("{KEYWORD_ID}"));
						WSClient.setData("{var_actType}", "ADD");
						WSClient.setData("{var_resType}", "TRACES");

						/*******************
						 * OWS Guest Requests Operation
						 ************************/
						String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_07");
						String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
						if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_resultStatusFlag", false)) {

							if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
									"GuestRequestsResponse_Result_resultStatusFlag", "FAIL", false))
								;

						}

					}

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}

	@Test(groups = { "minimumRegression", "GuestRequests", "Reservation", "OWS" })

	public void guestRequests_41441() throws Exception {

		try {

			String testName = "guestRequests_41441";
			WSClient.startTest(testName, "Verify that the traces are deleted successfully", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "DepartmentCode" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_departmentId}", OperaPropConfig.getDataSetForCode("DepartmentCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
						LinkedHashMap<String, String> confirmationNumber = new LinkedHashMap<String, String>();
						confirmationNumber = WSClient.getDBRow(WSClient.getQuery("CreateReservation", "QS_02"));
						WSClient.setData("{var_confNumber}", confirmationNumber.get("CONFIRMATION_NO").toString());

						String changeResvReq = WSClient.createSOAPMessage("ChangeReservation", "DS_04");
						String changeResvRes = WSClient.processSOAPMessage(changeResvReq);
						if (WSAssert.assertIfElementExists(changeResvRes, "ChangeReservationRS_Success", true)) {

							LinkedHashMap<String, String> trace = new LinkedHashMap<String, String>();
							trace = WSClient.getDBRow(WSClient.getQuery("OWSGuestRequests", "QS_14"));
							WSClient.setData("{var_traceId}", trace.get("TRACE_ID").toString());
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Trace ID:" + trace.get("TRACE_ID").toString() + "</b>");
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							WSClient.setData("{var_actType}", "DELETE");
							WSClient.setData("{var_resType}", "TRACES");

							/*******************
							 * OWS Guest Requests Operation
							 ************************/
							String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_14");
							String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
							if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error displayed in the Guest Requests response is :" + message
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(GuestResponseRes,
										"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error displayed in the Guest Requests response is :" + message
												+ "</b>");
							}
							if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
									"GuestRequestsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_15"));
								if (WSAssert.assertEquals("0", db.get("COUNT"), true))
									WSClient.writeToReport(LogStatus.PASS,
											"<b>Successfully deleted the trace" + "</b>");
								else
									WSClient.writeToReport(LogStatus.FAIL, "<b>Unable to delete trace" + "</b>");

							}

						}
					}

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}

	@Test(groups = { "minimumRegression", "GuestRequests", "Reservation", "OWS" })

	public void guestRequests_41443() throws Exception {

		try {

			String testName = "guestRequests_41443";
			WSClient.startTest(testName, "Verify that the specials are deleted successfully", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
						LinkedHashMap<String, String> confirmationNumber = new LinkedHashMap<String, String>();
						confirmationNumber = WSClient.getDBRow(WSClient.getQuery("QS_02"));
						WSClient.setData("{var_confNumber}", confirmationNumber.get("CONFIRMATION_NO").toString());
						WSClient.setData("{var_prefType}", "SPECIALS");
						WSClient.setData("{var_prefValue}", "COT");
						String changeResvReq = WSClient.createSOAPMessage("ChangeReservation", "DS_05");
						String changeResvRes = WSClient.processSOAPMessage(changeResvReq);
						if (WSAssert.assertIfElementExists(changeResvRes, "ChangeReservationRS_Success", true)) {

							LinkedHashMap<String, String> special = new LinkedHashMap<String, String>();
							special = WSClient.getDBRow(WSClient.getQuery("QS_04"));
							WSClient.setData("{var_specialId}", special.get("SPECIAL_ID").toString());
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Special ID:" + special.get("SPECIAL_ID").toString() + "</b>");
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							WSClient.setData("{var_actType}", "DELETE");
							WSClient.setData("{var_resType}", "SPECIALS");

							/*******************
							 * OWS Guest Requests Operation
							 ************************/
							String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_19");
							String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
							if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error displayed in the Guest Requests response is :" + message
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(GuestResponseRes,
										"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error displayed in the Guest Requests response is :" + message
												+ "</b>");
							}
							if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
									"GuestRequestsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_12"));
								if (WSAssert.assertEquals("0", db.get("COUNT"), true))
									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully deleted special" + "</b>");
								else
									WSClient.writeToReport(LogStatus.FAIL, "<b>Unable to delete special" + "</b>");

							}

						}
					}

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}

	@Test(groups = { "minimumRegression", "GuestRequests", "Reservation", "OWS" })

	public void guestRequests_41444() throws Exception {

		try {

			String testName = "guestRequests_41444";
			WSClient.startTest(testName, "Verify that the features are deleted successfully", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "RoomFeature" })) {

				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
						LinkedHashMap<String, String> confirmationNumber = new LinkedHashMap<String, String>();
						confirmationNumber = WSClient.getDBRow(WSClient.getQuery("QS_02"));
						WSClient.setData("{var_confNumber}", confirmationNumber.get("CONFIRMATION_NO").toString());
						WSClient.setData("{var_prefType}", "ROOM FEATURES");
						WSClient.setData("{var_prefValue}", OperaPropConfig.getDataSetForCode("RoomFeature", "DS_01"));
						String changeResvReq = WSClient.createSOAPMessage("ChangeReservation", "DS_05");
						String changeResvRes = WSClient.processSOAPMessage(changeResvReq);
						if (WSAssert.assertIfElementExists(changeResvRes, "ChangeReservationRS_Success", true)) {

							LinkedHashMap<String, String> special = new LinkedHashMap<String, String>();
							special = WSClient.getDBRow(WSClient.getQuery("QS_05"));
							WSClient.setData("{var_featureId}", special.get("FEATURE_ID").toString());
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Feature ID:" + special.get("FEATURE_ID").toString() + "</b>");
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							WSClient.setData("{var_actType}", "DELETE");
							WSClient.setData("{var_resType}", "FEATURES");

							/*******************
							 * OWS Guest Requests Operation
							 ************************/
							String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_20");
							String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
							if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error displayed in the Guest Requests response is :" + message
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(GuestResponseRes,
										"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error displayed in the Guest Requests response is :" + message
												+ "</b>");
							}
							if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
									"GuestRequestsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_13"));
								if (WSAssert.assertEquals("0", db.get("COUNT"), true))
									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully deleted feature" + "</b>");
								else
									WSClient.writeToReport(LogStatus.FAIL, "<b>Unable to delete feature" + "</b>");

							}

						}
					}

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}

	@Test(groups = { "minimumRegression", "GuestRequests", "Reservation", "OWS" })

	public void guestRequests_41445() throws Exception {

		try {

			String testName = "guestRequests_41445";
			WSClient.startTest(testName, "Verify that the alerts are deleted successfully", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "RoomFeature", "AlertCode" })) {

				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_alertCode}", OperaPropConfig.getDataSetForCode("AlertCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
						LinkedHashMap<String, String> confirmationNumber = new LinkedHashMap<String, String>();
						confirmationNumber = WSClient.getDBRow(WSClient.getQuery("CreateReservation", "QS_02"));
						WSClient.setData("{var_confNumber}", confirmationNumber.get("CONFIRMATION_NO").toString());

						String changeResvReq = WSClient.createSOAPMessage("ChangeReservation", "DS_03");
						String changeResvRes = WSClient.processSOAPMessage(changeResvReq);
						if (WSAssert.assertIfElementExists(changeResvRes, "ChangeReservationRS_Success", true)) {

							LinkedHashMap<String, String> alert = new LinkedHashMap<String, String>();
							alert = WSClient.getDBRow(WSClient.getQuery("ChangeReservation", "QS_02"));
							WSClient.setData("{var_alertId}", alert.get("ALERT_ID").toString());
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Alert ID:" + alert.get("ALERT_ID").toString() + "</b>");
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							WSClient.setData("{var_actType}", "DELETE");
							WSClient.setData("{var_resType}", "ALERTS");

							/*******************
							 * OWS Guest Requests Operation
							 ************************/
							String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_12");
							String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
							if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error displayed in the Guest Requests response is :" + message
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(GuestResponseRes,
										"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error displayed in the Guest Requests response is :" + message
												+ "</b>");
							}
							if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
									"GuestRequestsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_10"));
								if (WSAssert.assertEquals("0", db.get("COUNT"), true))
									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully deleted alerts" + "</b>");
								else
									WSClient.writeToReport(LogStatus.FAIL, "<b>Unable to delete alert" + "</b>");

							}
						}

					}

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}

	// Negative scenario – Invalid as per JIRA
	// https://jira.oraclecorp.com/jira/browse/HOWSS-5754
	// @Test(groups = { "minimumRegression", "GuestRequests", "Reservation",
	// "OWS" })
	//
	// public void guestRequests_41446() throws Exception {
	//
	// try {
	//
	// String testName = "guestRequests_41446";
	// WSClient.startTest(testName, "Verify that an error message is obtained
	// when no ActionType is passed",
	// "minimumRegression");
	// if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode",
	// "RoomType", "SourceCode",
	// "MarketCode", "PaymentMethod", "RoomFeature" })) {
	//
	// String resortOperaValue = OPERALib.getResort();
	//
	// String channel = OWSLib.getChannel();
	// String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsResort}", owsResort);
	// String pwd = OPERALib.getPassword();
	// String channelType = OWSLib.getChannelType(channel);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	// channel);
	// String uname = OPERALib.getUserName();
	//
	// WSClient.setData("{var_resort}", resortOperaValue);
	//
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	//
	// /************
	// * Prerequisite : Room type, Rate Plan Code, Source Code, Market
	// * Code
	// *********************************/
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	//
	// /************
	// * Prerequisite 1: Create profile
	// *********************************/
	//
	// profileID = CreateProfile.createProfile("DS_01");
	// if (!profileID.equals("error")) {
	// WSClient.setData("{var_profileId}", profileID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID +
	// "</b>");
	//
	// /*******************
	// * Prerequisite 2:Create a Reservation
	// ************************/
	//
	// resvID = CreateReservation.createReservation("DS_01");
	// if (!resvID.equals("error")) {
	//
	// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
	//
	// OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
	// channelCarrier);
	//
	// WSClient.setData("{var_resType}", "ALERTS");
	//
	// /*******************
	// * OWS Guest Requests Operation
	// ************************/
	// String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests",
	// "DS_03");
	// String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "Result_Text_TextElement", true)) {
	//
	// /****
	// * Verifying that the error message is populated on
	// * the response
	// ********/
	//
	// String message = WSAssert.getElementValue(GuestResponseRes,
	// "Result_Text_TextElement",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>The error displayed in the Guest Requests response is :" + message +
	// "</b>");
	// }
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "GuestRequestsResponse_Result_OperaErrorCode", true)) {
	//
	// /****
	// * Verifying whether the error Message is populated
	// * on the response
	// ****/
	//
	// String message = WSAssert.getElementValue(GuestResponseRes,
	// "GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>The error displayed in the Guest Requests response is :" + message +
	// "</b>");
	// }
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "GuestRequestsResponse_Result_resultStatusFlag", false)) {
	//
	// if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
	// "GuestRequestsResponse_Result_resultStatusFlag", "FAIL", false))
	// ;
	//
	// }
	//
	// }
	//
	// }
	//
	// }
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// WSClient.setData("{var_cancelReasonCode}",
	// OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
	// if (!resvID.containsValue("error")) {
	//
	// // CancelReservation.cancelReservation("DS_02");
	// }
	// }
	//
	// }
	//

	// @Test(groups = { "minimumRegression", "GuestRequests","Reservation",
	// "OWS"})

	public void guestRequests_39526() throws Exception {

		try {

			String testName = "guestRequests_39526";
			WSClient.startTest(testName, "Verify that an error message is obtained when no Resort is passed",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "RoomFeature" })) {

				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						WSClient.setData("{var_actType}", "ADD");
						WSClient.setData("{var_resType}", "ALERTS");

						/*******************
						 * OWS Guest Requests Operation
						 ************************/
						String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_04");
						String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
						if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_resultStatusFlag", false)) {

							if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
									"GuestRequestsResponse_Result_resultStatusFlag", "FAIL", false))
								;

						}

					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}

	@Test(groups = { "minimumRegression", "GuestRequests", "Reservation", "OWS" })

	public void guestRequests_39527() throws Exception {

		try {

			String testName = "guestRequests_39527";
			WSClient.startTest(testName, "Verify that an error message is obtained when no Reservation ID is passed",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "RoomFeature" })) {

				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						WSClient.setData("{var_actType}", "ADD");
						WSClient.setData("{var_resType}", "ALERTS");

						/*******************
						 * OWS Guest Requests Operation
						 ************************/
						String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_05");
						String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
						if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_resultStatusFlag", false)) {

							if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
									"GuestRequestsResponse_Result_resultStatusFlag", "FAIL", false))
								;

						}

					}

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}

	// // Invalid Scenario
	// @Test(groups = { "minimumRegression", "GuestRequests", "Reservation",
	// "OWS" })
	//
	// public void guestRequests_39528() throws Exception {
	//
	// try {
	//
	// String testName = "guestRequests_39528";
	// WSClient.startTest(testName, "Verify that an error message is obtained
	// when no Request Type is passed",
	// "minimumRegression");
	// if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode",
	// "RoomType", "SourceCode",
	// "MarketCode", "PaymentMethod", "RoomFeature" })) {
	//
	// String resortOperaValue = OPERALib.getResort();
	//
	// String channel = OWSLib.getChannel();
	// String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsResort}", owsResort);
	// String pwd = OPERALib.getPassword();
	// String channelType = OWSLib.getChannelType(channel);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	// channel);
	// String uname = OPERALib.getUserName();
	//
	// WSClient.setData("{var_resort}", resortOperaValue);
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	//
	// /************
	// * Prerequisite : Room type, Rate Plan Code, Source Code, Market
	// * Code
	// *********************************/
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	//
	// /************
	// * Prerequisite 1: Create profile
	// *********************************/
	//
	// profileID = CreateProfile.createProfile("DS_01");
	// if (!profileID.equals("error")) {
	// WSClient.setData("{var_profileId}", profileID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID +
	// "</b>");
	//
	// /*******************
	// * Prerequisite 2:Create a Reservation
	// ************************/
	//
	// resvID = CreateReservation.createReservation("DS_01");
	// if (!resvID.equals("error")) {
	//
	// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
	//
	// OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
	// channelCarrier);
	//
	// WSClient.setData("{var_actType}", "ADD");
	//
	// /*******************
	// * OWS Guest Requests Operation
	// ************************/
	// String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests",
	// "DS_06");
	// String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "Result_Text_TextElement", true)) {
	//
	// /****
	// * Verifying that the error message is populated on
	// * the response
	// ********/
	//
	// String message = WSAssert.getElementValue(GuestResponseRes,
	// "Result_Text_TextElement",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>The error displayed in the Guest Requests response is :" + message +
	// "</b>");
	// }
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "GuestRequestsResponse_Result_OperaErrorCode", true)) {
	//
	// /****
	// * Verifying whether the error Message is populated
	// * on the response
	// ****/
	//
	// String message = WSAssert.getElementValue(GuestResponseRes,
	// "GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>The error displayed in the Guest Requests response is :" + message +
	// "</b>");
	// }
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "GuestRequestsResponse_Result_resultStatusFlag", false)) {
	//
	// if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
	// "GuestRequestsResponse_Result_resultStatusFlag", "FAIL", false))
	// ;
	//
	// }
	//
	// }
	//
	// }
	// }
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// WSClient.setData("{var_cancelReasonCode}",
	// OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
	// if (!resvID.containsValue("error")) {
	//
	// // CancelReservation.cancelReservation("DS_02");
	// }
	// }
	//
	// }

	// // Invalid Scenario
	// @Test(groups = { "minimumRegression", "GuestRequests", "Reservation",
	// "OWS" })
	//
	// public void guestRequests_39540() throws Exception {
	//
	// try {
	//
	// String testName = "guestRequests_39540";
	// WSClient.startTest(testName,
	// "Verify that an error message is obtained when an invalid Request Type is
	// passed",
	// "minimumRegression");
	// if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode",
	// "RoomType", "SourceCode",
	// "MarketCode", "PaymentMethod", "RoomFeature" })) {
	//
	// String resortOperaValue = OPERALib.getResort();
	//
	// String channel = OWSLib.getChannel();
	// String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsResort}", owsResort);
	// String pwd = OPERALib.getPassword();
	// String channelType = OWSLib.getChannelType(channel);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	// channel);
	// String uname = OPERALib.getUserName();
	//
	// WSClient.setData("{var_resort}", resortOperaValue);
	//
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	//
	// /************
	// * Prerequisite : Room type, Rate Plan Code, Source Code, Market
	// * Code
	// *********************************/
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	//
	// /************
	// * Prerequisite 1: Create profile
	// *********************************/
	//
	// profileID = CreateProfile.createProfile("DS_01");
	// if (!profileID.equals("error")) {
	// WSClient.setData("{var_profileId}", profileID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID +
	// "</b>");
	//
	// /*******************
	// * Prerequisite 2:Create a Reservation
	// ************************/
	//
	// resvID = CreateReservation.createReservation("DS_01");
	// if (!resvID.equals("error")) {
	//
	// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
	//
	// OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
	// channelCarrier);
	//
	// WSClient.setData("{var_actType}", "ADD");
	// WSClient.setData("{var_resType}",
	// WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));
	//
	// /*******************
	// * OWS Guest Requests Operation
	// ************************/
	// String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests",
	// "DS_01");
	// String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "Result_Text_TextElement", true)) {
	//
	// /****
	// * Verifying that the error message is populated on
	// * the response
	// ********/
	//
	// String message = WSAssert.getElementValue(GuestResponseRes,
	// "Result_Text_TextElement",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>The error displayed in the Guest Requests response is :" + message +
	// "</b>");
	// }
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "GuestRequestsResponse_Result_OperaErrorCode", true)) {
	//
	// /****
	// * Verifying whether the error Message is populated
	// * on the response
	// ****/
	//
	// String message = WSAssert.getElementValue(GuestResponseRes,
	// "GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>The error displayed in the Guest Requests response is :" + message +
	// "</b>");
	// }
	// if (WSAssert.assertIfElementExists(GuestResponseRes,
	// "GuestRequestsResponse_Result_resultStatusFlag", false)) {
	//
	// if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
	// "GuestRequestsResponse_Result_resultStatusFlag", "FAIL", false))
	// ;
	//
	// }
	//
	// }
	//
	// }
	// }
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// WSClient.setData("{var_cancelReasonCode}",
	// OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
	// if (!resvID.containsValue("error")) {
	//
	// // CancelReservation.cancelReservation("DS_02");
	// }
	// }
	//
	// }

	@Test(groups = { "minimumRegression", "GuestRequests", "Reservation", "OWS" })

	public void guestRequests_39544() throws Exception {

		try {

			String testName = "guestRequests_39544";
			WSClient.startTest(testName,
					"Verify that an error message is obtained when an invalid Reservation ID is passed",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "RoomFeature" })) {

				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						WSClient.setData("{var_actType}", "ADD");
						WSClient.setData("{var_resType}", "COMMENTS");

						/*******************
						 * OWS Guest Requests Operation
						 ************************/
						String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_09");
						String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
						if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_resultStatusFlag", false)) {

							if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
									"GuestRequestsResponse_Result_resultStatusFlag", "FAIL", false))
								;

						}

					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}

	@Test(groups = { "minimumRegression", "GuestRequests", "Reservation", "OWS" })

	public void guestRequests_41452() throws Exception {

		try {

			String testName = "guestRequests_41452";
			WSClient.startTest(testName, "Verify that the traces are properly inserted by the guest",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "RoomFeature", "DepartmentCode" })) {

				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_department}", OperaPropConfig.getDataSetForCode("DepartmentCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
						LinkedHashMap<String, String> confirmationNumber = new LinkedHashMap<String, String>();
						confirmationNumber = WSClient.getDBRow(WSClient.getQuery("QS_02"));
						WSClient.setData("{var_confNumber}", confirmationNumber.get("CONFIRMATION_NO").toString());

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						WSClient.setData("{var_actType}", "ADD");
						WSClient.setData("{var_resType}", "TRACES");

						/*******************
						 * OWS Guest Requests Operation
						 ************************/
						String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_07");
						String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
						if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
								"GuestRequestsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
							HashMap<String, String> xpath = new HashMap<String, String>();
							xpath.put("GuestRequests_Traces_Trace_department", "GuestRequests_Traces_Trace");
							xpath.put("Traces_Trace_TraceId", "GuestRequests_Traces_Trace");
							xpath.put("Traces_Trace_TraceDate", "GuestRequests_Traces_Trace");
							expectedValues = WSClient.getSingleNodeList(GuestResponseRes, xpath, false,
									XMLType.RESPONSE);
							actualValues = WSClient.getDBRow(WSClient.getQuery("QS_04"));
							if (WSAssert.assertEquals(expectedValues, actualValues, false)) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully inserted traces" + "</b>");
							}

						}

					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}

	@Test(groups = { "minimumRegression", "GuestRequests", "Reservation", "OWS" })

	public void guestRequests_41453() throws Exception {

		try {

			String testName = "guestRequests_41453";
			WSClient.startTest(testName, "Verify that the specials are properly inserted by the guest",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "RoomFeature" })) {

				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String owsResort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsResort}", owsResort);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
						LinkedHashMap<String, String> confirmationNumber = new LinkedHashMap<String, String>();
						confirmationNumber = WSClient.getDBRow(WSClient.getQuery("QS_02"));
						WSClient.setData("{var_confNumber}", confirmationNumber.get("CONFIRMATION_NO").toString());

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						WSClient.setData("{var_actType}", "ADD");
						WSClient.setData("{var_resType}", "SPECIALS");

						/*******************
						 * OWS Guest Requests Operation
						 ************************/
						String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_10");
						String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
						if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
								"GuestRequestsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
							HashMap<String, String> xpath = new HashMap<String, String>();
							xpath.put("Specials_SpecialRequest_SpecialRequestId",
									"GuestRequests_Specials_SpecialRequest");
							expectedValues = WSClient.getSingleNodeList(GuestResponseRes, xpath, false,
									XMLType.RESPONSE);
							actualValues = WSClient.getDBRow(WSClient.getQuery("QS_05"));
							WSAssert.assertEquals(expectedValues, actualValues, false);
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully inserted specials" + "</b>");

						}

					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}

	// @Test(groups = { "minimumRegression", "GuestRequests","Reservation",
	// "OWS"})

	public void guestRequests_39560() throws Exception {

		try {

			String testName = "guestRequests_395560";
			WSClient.startTest(testName, "Verify that the features are properly inserted by the guest",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomFeature" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();
				WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(resortOperaValue, channel));
				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					if (resvID.isEmpty())
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId").toString() + "</b>");
						LinkedHashMap<String, String> confirmationNumber = new LinkedHashMap<String, String>();
						confirmationNumber = WSClient.getDBRow(WSClient.getQuery("QS_02"));
						WSClient.setData("{var_confNumber}", confirmationNumber.get("CONFIRMATION_NO").toString());
						WSClient.setData("{var_prefValue}", OperaPropConfig.getDataSetForCode("RoomFeature", "DS_01"));

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						WSClient.setData("{var_actType}", "ADD");
						WSClient.setData("{var_resType}", "SPECIALS");

						/*******************
						 * OWS Guest Requests Operation
						 ************************/

						String GuestRequestReq = WSClient.createSOAPMessage("OWSGuestRequests", "DS_21");
						String GuestResponseRes = WSClient.processSOAPMessage(GuestRequestReq);
						if (WSAssert.assertIfElementExists(GuestResponseRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(GuestResponseRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(GuestResponseRes,
								"GuestRequestsResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(GuestResponseRes,
									"GuestRequestsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the Guest Requests response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementValueEquals(GuestResponseRes,
								"GuestRequestsResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							// if(WSAssert.assertIfElementExists(GuestResponseRes,
							// "GuestRequestsResponse_Result_OperaErrorCode",true))

							// LinkedHashMap<String,String> expectedValues = new
							// LinkedHashMap<String,String>();
							// LinkedHashMap<String, String> actualValues = new
							// LinkedHashMap<String,String>();
							// HashMap<String,String> xpath=new
							// HashMap<String,String>();
							// xpath.put("GuestRequests_Features_Features_Feature","GuestRequests_Features_Features");
							// xpath.put("GuestRequests_Features_Features_Description","GuestRequests_Features_Features");
							// expectedValues=WSClient.getSingleNodeList(GuestResponseRes,
							// xpath, false, XMLType.RESPONSE);
							// actualValues=WSClient.getDBRow(WSClient.getQuery("QS_05"));
							// WSAssert.assertEquals(expectedValues,actualValues,false);
							//
						}

					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error")) {

				// CancelReservation.cancelReservation("DS_02");
			}
		}

	}
}
