package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

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

public class UpdateInventoryItem extends WSSetUp {

	String profileID = "";
	HashMap<String, String> resvID = new HashMap<>();

	@Test(groups = { "sanity", "updateInventoryItem", "OWS", "Reservation" })

	public void updateInventoryItems_39541() throws Exception {
		try {
			String testName = "updateInventoryItem_39541";
			WSClient.startTest(testName, "Verify that  item information is updated for a reservation", "sanity");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			OPERALib.setOperaHeader(uname);

			if (profileID.equals(""))

				/**** Prerequisite 1 :Create Profile ****/

				profileID = CreateProfile.createProfile("DS_01");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod",
						"SourceCode", "MarketCode", "ItemCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					/**** Prerequisite 2 : Create Reservation ****/

					resvID = CreateReservation.createReservation("DS_04");

					if (!resvID.containsValue("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						WSClient.setData("{var_count}", "10");

						/***** Prerequisite 3 :Reserve Inventory Items ****/

						String reserveInventoryReq = WSClient.createSOAPMessage("ReserveInventoryItems", "DS_01");
						String reserveInventoryRes = WSClient.processSOAPMessage(reserveInventoryReq);

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						if (WSAssert.assertIfElementExists(reserveInventoryRes, "ReserveInventoryItemsRS_Success",
								true)) {

							String updateInventoryReq = WSClient.createSOAPMessage("OWSUpdateInventoryItem", "DS_01");
							String updateInventoryRes = WSClient.processSOAPMessage(updateInventoryReq);

							if (WSAssert.assertIfElementValueEquals(updateInventoryRes,
									"UpdateInventoryItemResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
								HashMap<String, String> xpath = new HashMap<String, String>();
								xpath.put("UpdateInventoryItemRequest_ResvNameId", "_UpdateInventoryItemRequest");
								xpath.put("UpdateInventoryItemRequest_ItemCode", "_UpdateInventoryItemRequest");
								xpath.put("UpdateInventoryItemRequest_Quantity", "_UpdateInventoryItemRequest");
								xpath.put("UpdateInventoryItemRequest_StayDateRange_StartDate",
										"UpdateInventoryItemRequest_StayDateRange");
								xpath.put("UpdateInventoryItemRequest_StayDateRange_EndDate",
										"UpdateInventoryItemRequest_StayDateRange");
								expectedValues = WSClient.getSingleNodeList(updateInventoryReq, xpath, false,
										XMLType.REQUEST);
								actualValues = WSClient.getDBRow(WSClient.getQuery("QS_01"));
								WSAssert.assertEquals(expectedValues, actualValues, false);

							}

						}

						else {
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to attach item to reservation");
						}
					} else
						WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Reservation Failed !");
				} else
					WSClient.writeToReport(LogStatus.WARNING,
							" Pre requisiteBlocked :Property config Data is not Available !");
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Profile Failed !");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error") || !resvID.isEmpty())
				CancelReservation.cancelReservation("DS_02");

		}
	}

	@Test(groups = { "minimumRegression", "updateInventoryItem", "OWS", "Reservation" })

	public void updateInventoryItems_41100() throws Exception {
		try {
			String testName = "updateInventoryItem_41100";
			WSClient.startTest(testName, "Verify that  item information is added for a reservation",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			OPERALib.setOperaHeader(uname);

			if (profileID.equals(""))

				/**** Prerequisite 1 :Create Profile ****/

				profileID = CreateProfile.createProfile("DS_01");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				WSClient.writeToReport(LogStatus.PASS, "Profile ID : " + profileID);

				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod",
						"SourceCode", "MarketCode", "ItemCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					/**** Prerequisite 2 : Create Reservation ****/

					resvID = CreateReservation.createReservation("DS_04");

					WSClient.writeToReport(LogStatus.PASS, " Reservation ID : " + resvID.get("reservationId"));

					if (!resvID.containsValue("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						WSClient.setData("{var_count}", "10");

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						String updateInventoryReq = WSClient.createSOAPMessage("OWSUpdateInventoryItem", "DS_01");
						String updateInventoryRes = WSClient.processSOAPMessage(updateInventoryReq);

						if (WSAssert.assertIfElementValueEquals(updateInventoryRes,
								"UpdateInventoryItemResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
							HashMap<String, String> xpath = new HashMap<String, String>();
							xpath.put("UpdateInventoryItemRequest_ResvNameId", "_UpdateInventoryItemRequest");
							xpath.put("UpdateInventoryItemRequest_ItemCode", "_UpdateInventoryItemRequest");
							xpath.put("UpdateInventoryItemRequest_Quantity", "_UpdateInventoryItemRequest");
							xpath.put("UpdateInventoryItemRequest_StayDateRange_StartDate",
									"UpdateInventoryItemRequest_StayDateRange");
							xpath.put("UpdateInventoryItemRequest_StayDateRange_EndDate",
									"UpdateInventoryItemRequest_StayDateRange");
							expectedValues = WSClient.getSingleNodeList(updateInventoryReq, xpath, false,
									XMLType.REQUEST);
							actualValues = WSClient.getDBRow(WSClient.getQuery("QS_01"));
							WSAssert.assertEquals(expectedValues, actualValues, false);

						}

					} else
						WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Reservation Failed !");
				} else
					WSClient.writeToReport(LogStatus.WARNING,
							" Pre requisiteBlocked :Property config Data is not Available !");
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Profile Failed !");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error") || !resvID.isEmpty())
				CancelReservation.cancelReservation("DS_02");

		}
	}

	@Test(groups = { "minimumRegression", "updateInventoryItem", "OWS", "Reservation" })
	public void updateInventoryItems_41122() throws Exception {
		try {
			String testName = "updateInventoryItems_41122";
			WSClient.startTest(testName, "Verify that error when all the days of the week are false for a reservation",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			OPERALib.setOperaHeader(uname);

			if (profileID.equals(""))

				/**** Prerequisite 1 :Create Profile ****/

				profileID = CreateProfile.createProfile("DS_01");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod",
						"SourceCode", "MarketCode", "ItemCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					/**** Prerequisite 2 : Create Reservation ****/

					resvID = CreateReservation.createReservation("DS_04");

					if (!resvID.containsValue("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						WSClient.setData("{var_count}", "10");

						/***** Prerequisite 3 :Reserve Inventory Items ****/

						String reserveInventoryReq = WSClient.createSOAPMessage("ReserveInventoryItems", "DS_01");
						String reserveInventoryRes = WSClient.processSOAPMessage(reserveInventoryReq);

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						if (WSAssert.assertIfElementExists(reserveInventoryRes, "ReserveInventoryItemsRS_Success",
								true)) {

							String updateInventoryReq = WSClient.createSOAPMessage("OWSUpdateInventoryItem", "DS_02");
							String updateInventoryRes = WSClient.processSOAPMessage(updateInventoryReq);

							WSAssert.assertIfElementValueEquals(updateInventoryRes,
									"UpdateInventoryItemResponse_Result_resultStatusFlag", "FAIL", false);

						}

					} else
						WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Reservation Failed !");
				} else
					WSClient.writeToReport(LogStatus.WARNING,
							" Pre requisiteBlocked :Property config Data is not Available !");
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Profile Failed !");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error") || !resvID.isEmpty())
				CancelReservation.cancelReservation("DS_02");

		}
	}

	@Test(groups = { "minimumRegression", "updateInventoryItem", "OWS", "Reservation" })

	public void updateInventoryItems_41170() throws Exception {
		try {
			String testName = "updateInventoryItems_41170";
			WSClient.startTest(testName,
					"Verify that  item information is updated for a reservation by giving confirmation-id in te request",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			OPERALib.setOperaHeader(uname);

			if (profileID.equals(""))

				/**** Prerequisite 1 :Create Profile ****/

				profileID = CreateProfile.createProfile("DS_01");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod",
						"SourceCode", "MarketCode", "ItemCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					/**** Prerequisite 2 : Create Reservation ****/

					resvID = CreateReservation.createReservation("DS_04");

					if (!resvID.containsValue("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.setData("{var_confirmationId}", resvID.get("confirmationId"));
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						WSClient.setData("{var_count}", "10");

						/***** Prerequisite 3 :Reserve Inventory Items ****/

						String reserveInventoryReq = WSClient.createSOAPMessage("ReserveInventoryItems", "DS_01");
						String reserveInventoryRes = WSClient.processSOAPMessage(reserveInventoryReq);

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						if (WSAssert.assertIfElementExists(reserveInventoryRes, "ReserveInventoryItemsRS_Success",
								true)) {

							String updateInventoryReq = WSClient.createSOAPMessage("OWSUpdateInventoryItem", "DS_03");
							String updateInventoryRes = WSClient.processSOAPMessage(updateInventoryReq);

							if (WSAssert.assertIfElementValueEquals(updateInventoryRes,
									"UpdateInventoryItemResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
								HashMap<String, String> xpath = new HashMap<String, String>();
								xpath.put("UpdateInventoryItemRequest_ConfirmationNumber",
										"_UpdateInventoryItemRequest");
								xpath.put("UpdateInventoryItemRequest_LegNumber", "_UpdateInventoryItemRequest");
								xpath.put("UpdateInventoryItemRequest_ItemCode", "_UpdateInventoryItemRequest");
								xpath.put("UpdateInventoryItemRequest_Quantity", "_UpdateInventoryItemRequest");
								xpath.put("UpdateInventoryItemRequest_StayDateRange_StartDate",
										"UpdateInventoryItemRequest_StayDateRange");
								xpath.put("UpdateInventoryItemRequest_StayDateRange_EndDate",
										"UpdateInventoryItemRequest_StayDateRange");
								expectedValues = WSClient.getSingleNodeList(updateInventoryReq, xpath, false,
										XMLType.REQUEST);
								actualValues = WSClient.getDBRow(WSClient.getQuery("QS_02"));
								WSAssert.assertEquals(expectedValues, actualValues, false);

							}

						}

						else {
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to attach item to reservation");
						}
					} else
						WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Reservation Failed !");
				} else
					WSClient.writeToReport(LogStatus.WARNING,
							" Pre requisiteBlocked :Property config Data is not Available !");
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Profile Failed !");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error") || !resvID.isEmpty())
				CancelReservation.cancelReservation("DS_02");

		}
	}

	//// Negative Scenario -- Invalid
	// @Test(groups = { "minimumRegression", "updateInventoryItem", "OWS",
	//// "Reservation" })
	//
	// public void updateInventoryItems_41103() throws Exception {
	// try {
	// String testName = "updateInventoryItems_41103";
	// WSClient.startTest(testName,
	// "Verify that error exists when item information is updated for a
	//// cancelled reservation", "minimumRegression");
	//
	// String resortOperaValue = OPERALib.getResort();
	// String chain = OPERALib.getChain();
	// WSClient.setData("{var_chain}", chain);
	// WSClient.setData("{var_resort}", resortOperaValue);
	//
	// String uname = OPERALib.getUserName();
	// String pwd = OPERALib.getPassword();
	// String channel = OWSLib.getChannel();
	// String channelType = OWSLib.getChannelType(channel);
	// String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsresort}", resort);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	//// channel);
	//
	// OPERALib.setOperaHeader(uname);
	//
	// if (profileID.equals(""))
	//
	// /**** Prerequisite 1 :Create Profile ****/
	//
	// profileID = CreateProfile.createProfile("DS_01");
	// if (!profileID.equals("error")) {
	// WSClient.setData("{var_profileId}", profileID);
	//
	//
	//
	// if (OperaPropConfig.getPropertyConfigResults(
	// new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode",
	//// "MarketCode","ItemCode"})) {
	// WSClient.setData("{VAR_RATEPLANCODE}",
	//// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	//// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	//// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	//// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_payment}",
	//// OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	//
	// /**** Prerequisite 2 : Create Reservation ****/
	//
	// resvID = CreateReservation.createReservation("DS_04");
	//
	//
	//
	//
	// if (!resvID.containsValue("error")) {
	//
	// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
	// WSClient.setData("{var_startDate}",
	//// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
	// WSClient.setData("{var_endDate}",
	//// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
	// WSClient.setData("{var_itemCode}",
	//// OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
	// WSClient.setData("{var_count}","10");
	//
	// /***** Prerequisite 3 :Reserve Inventory Items ****/
	//
	// String reserveInventoryReq =
	//// WSClient.createSOAPMessage("ReserveInventoryItems", "DS_01");
	// String reserveInventoryRes =
	//// WSClient.processSOAPMessage(reserveInventoryReq);
	//
	// OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
	//// channelCarrier);
	//
	// if (WSAssert.assertIfElementExists(reserveInventoryRes,
	//// "ReserveInventoryItemsRS_Success",
	// true)) {
	//
	// CancelReservation.cancelReservation("DS_02");
	//
	// String
	//// updateInventoryReq=WSClient.createSOAPMessage("OWSUpdateInventoryItem",
	//// "DS_01");
	// String
	//// updateInventoryRes=WSClient.processSOAPMessage(updateInventoryReq);
	//
	// WSAssert.assertIfElementValueEquals(updateInventoryRes,
	//// "UpdateInventoryItemResponse_Result_resultStatusFlag","FAIL", false);
	//
	//
	// }
	//
	// else {
	// WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to attach
	//// item to reservation");
	// }
	// } else
	// WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create
	//// Reservation Failed !");
	// } else
	// WSClient.writeToReport(LogStatus.WARNING,
	// " Pre requisiteBlocked :Property config Data is not Available !");
	// } else
	// WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create
	//// Profile Failed !");
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	//// to:" + e);
	// } finally {
	// WSClient.setData("{var_cancelReasonCode}",
	//// OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
	// if (!resvID.containsValue("error") || !resvID.isEmpty())
	// CancelReservation.cancelReservation("DS_02");
	//
	// }
	// }

	@Test(groups = { "minimumRegression", "updateInventoryItem", "OWS", "Reservation" })

	public void updateInventoryItems_41141() throws Exception {
		try {
			String testName = "updateInventoryItems_41141";
			WSClient.startTest(testName, "Verify that another item  is added to a reservation", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			OPERALib.setOperaHeader(uname);

			if (profileID.equals(""))

				/**** Prerequisite 1 :Create Profile ****/

				profileID = CreateProfile.createProfile("DS_01");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod",
						"SourceCode", "MarketCode", "ItemCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					/**** Prerequisite 2 : Create Reservation ****/

					resvID = CreateReservation.createReservation("DS_04");

					if (!resvID.containsValue("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						WSClient.setData("{var_count}", "10");

						/***** Prerequisite 3 :Reserve Inventory Items ****/

						String reserveInventoryReq = WSClient.createSOAPMessage("ReserveInventoryItems", "DS_01");
						String reserveInventoryRes = WSClient.processSOAPMessage(reserveInventoryReq);

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						if (WSAssert.assertIfElementExists(reserveInventoryRes, "ReserveInventoryItemsRS_Success",
								true)) {
							WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_02"));
							WSClient.setData("{var_count}", "10");
							String updateInventoryReq = WSClient.createSOAPMessage("OWSUpdateInventoryItem", "DS_01");
							String updateInventoryRes = WSClient.processSOAPMessage(updateInventoryReq);

							if (WSAssert.assertIfElementValueEquals(updateInventoryRes,
									"UpdateInventoryItemResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
								HashMap<String, String> xpath = new HashMap<String, String>();
								xpath.put("UpdateInventoryItemRequest_ResvNameId", "_UpdateInventoryItemRequest");
								xpath.put("UpdateInventoryItemRequest_ItemCode", "_UpdateInventoryItemRequest");
								xpath.put("UpdateInventoryItemRequest_Quantity", "_UpdateInventoryItemRequest");
								xpath.put("UpdateInventoryItemRequest_StayDateRange_StartDate",
										"UpdateInventoryItemRequest_StayDateRange");
								xpath.put("UpdateInventoryItemRequest_StayDateRange_EndDate",
										"UpdateInventoryItemRequest_StayDateRange");
								expectedValues = WSClient.getSingleNodeList(updateInventoryReq, xpath, false,
										XMLType.REQUEST);
								actualValues = WSClient.getDBRow(WSClient.getQuery("QS_01"));
								WSAssert.assertEquals(expectedValues, actualValues, false);

							}

						}

						else {
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to attach item to reservation");
						}
					} else
						WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Reservation Failed !");
				} else
					WSClient.writeToReport(LogStatus.WARNING,
							" Pre requisiteBlocked :Property config Data is not Available !");
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Profile Failed !");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error") || !resvID.isEmpty())
				CancelReservation.cancelReservation("DS_02");

		}
	}

	@Test(groups = { "minimumRegression", "updateInventoryItem", "OWS", "Reservation" })

	public void updateInventoryItems_41247() throws Exception {
		try {
			String testName = "updateInventoryItems_41247";
			WSClient.startTest(testName, "Verify that  item  group  is added to a reservation", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			OPERALib.setOperaHeader(uname);

			if (profileID.equals(""))

				/**** Prerequisite 1 :Create Profile ****/

				profileID = CreateProfile.createProfile("DS_01");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod",
						"SourceCode", "MarketCode", "ItemCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					/**** Prerequisite 2 : Create Reservation ****/

					resvID = CreateReservation.createReservation("DS_04");

					if (!resvID.containsValue("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemGroup}", OperaPropConfig.getDataSetForCode("ItemClass", "DS_01"));
						WSClient.setData("{var_count}", "10");

						/***** Prerequisite 3 :Reserve Inventory Items ****/

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						String updateInventoryReq = WSClient.createSOAPMessage("OWSUpdateInventoryItem", "DS_04");
						String updateInventoryRes = WSClient.processSOAPMessage(updateInventoryReq);

						if (WSAssert.assertIfElementValueEquals(updateInventoryRes,
								"UpdateInventoryItemResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
							LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
							HashMap<String, String> xpath = new HashMap<String, String>();

							expectedValues = WSClient.getDBRow(WSClient.getQuery("QS_04"));
							actualValues = WSClient.getDBRow(WSClient.getQuery("QS_03"));
							WSAssert.assertEquals(expectedValues, actualValues, false);

							WSClient.writeToReport(LogStatus.INFO, "<b>Expected number of items in the group are</b> :"
									+ WSClient.getDBRow(WSClient.getQuery("QS_06")).get("count"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Actual number of items in the group are</b> :"
									+ WSClient.getDBRow(WSClient.getQuery("QS_07")).get("count"));

						}

					} else
						WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Reservation Failed !");
				} else
					WSClient.writeToReport(LogStatus.WARNING,
							" Pre requisiteBlocked :Property config Data is not Available !");
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Profile Failed !");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.containsValue("error") || !resvID.isEmpty())
				CancelReservation.cancelReservation("DS_02");

		}
	}

	@Test(groups = { "minimumRegression", "updateInventoryItem", "OWS", "Reservation" })

	public void updateInventoryItems_41266() throws Exception {
		try {
			String testName = "updateInventoryItem_41266";
			WSClient.startTest(testName, "Verify that  item dates are updated for a reservation", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			OPERALib.setOperaHeader(uname);

			if (profileID.equals(""))

				/**** Prerequisite 1 :Create Profile ****/

				profileID = CreateProfile.createProfile("DS_01");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod",
						"SourceCode", "MarketCode", "ItemCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					/**** Prerequisite 2 : Create Reservation ****/

					resvID = CreateReservation.createReservation("DS_14");

					if (!resvID.containsValue("error")) {

						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						WSClient.setData("{var_count}", "10");

						/***** Prerequisite 3 :Reserve Inventory Items ****/

						String reserveInventoryReq = WSClient.createSOAPMessage("ReserveInventoryItems", "DS_01");
						String reserveInventoryRes = WSClient.processSOAPMessage(reserveInventoryReq);

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						if (WSAssert.assertIfElementExists(reserveInventoryRes, "ReserveInventoryItemsRS_Success",
								true)) {
							WSClient.setData("{var_startDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_3}"));
							WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_3}"));

							String updateInventoryReq = WSClient.createSOAPMessage("OWSUpdateInventoryItem", "DS_01");
							String updateInventoryRes = WSClient.processSOAPMessage(updateInventoryReq);

							if (WSAssert.assertIfElementValueEquals(updateInventoryRes,
									"UpdateInventoryItemResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
								HashMap<String, String> xpath = new HashMap<String, String>();
								xpath.put("UpdateInventoryItemRequest_ResvNameId", "_UpdateInventoryItemRequest");
								xpath.put("UpdateInventoryItemRequest_ItemCode", "_UpdateInventoryItemRequest");
								xpath.put("UpdateInventoryItemRequest_Quantity", "_UpdateInventoryItemRequest");
								xpath.put("UpdateInventoryItemRequest_StayDateRange_StartDate",
										"UpdateInventoryItemRequest_StayDateRange");
								xpath.put("UpdateInventoryItemRequest_StayDateRange_EndDate",
										"UpdateInventoryItemRequest_StayDateRange");
								expectedValues = WSClient.getSingleNodeList(updateInventoryReq, xpath, false,
										XMLType.REQUEST);
								actualValues = WSClient.getDBRow(WSClient.getQuery("QS_05"));
								WSAssert.assertEquals(expectedValues, actualValues, false);

							}

						}

						else {
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to attach item to reservation");
						}
					} else
						WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Reservation Failed !");
				} else
					WSClient.writeToReport(LogStatus.WARNING,
							" Pre requisiteBlocked :Property config Data is not Available !");
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Profile Failed !");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			// if (!resvID.containsValue("error") || !resvID.isEmpty())
			// CancelReservation.cancelReservation("DS_02");

		}
	}
}
