package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.util.HashMap;

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

public class DeleteInventoryItem extends WSSetUp {
	/****
	 * Verify that DeleteInventoryItem operation deletes the item attached to
	 * the reservation successfully.
	 * 
	 * Prerequisites : ->Profile is created ->Reservation is created ->Items are
	 * attached to the reservation ->Market code,source code,room Type are to be
	 * available in the resort.
	 * 
	 */
	String profileID = "", resvID = "", confirmationID;

	@Test(groups = { "sanity", "DeleteInventoryItem", "OWS", "Reservation" })
	public void deleteInventoryItem_39620() throws Exception {
		try {
			String testName = "deleteInventoryItem_39620";
			WSClient.startTest(testName,
					"Verify that DeleteInventoryItem operation deletes the item attached to the reservation successfully.",
					"sanity");

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

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					/**** Prerequisite 2 : Create Reservation ****/

					HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
					resvID = resv.get("reservationId");

					WSClient.writeToReport(LogStatus.INFO, " <b>Reservation ID : " + resvID + "</b>");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_extresort}", resort);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						// WSClient.setData("{var_count}", "0");

						/***** Prerequisite 3 :Reserve Inventory Items ****/

						String holdItemInventoryReq = WSClient.createSOAPMessage("ReserveInventoryItems", "DS_01");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);

						if (WSAssert.assertIfElementExists(holdItemInventoryRes, "ReserveInventoryItemsRS_Success",
								true)) {

							String query = WSClient.getQuery("OWSDeleteInventoryItem", "QS_01");
							String db = WSClient.getDBRow(query).get("ITEM_ID");

							WSClient.writeToReport(LogStatus.INFO,
									"<b> The item added to the reservation is : " + db + "</b>");

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							/*** OWS Delete Inventory Item ***/

							WSClient.setData("{var_confirmationId}", confirmationID);

							String deleteInventoryReq = WSClient.createSOAPMessage("OWSDeleteInventoryItem", "DS_01");
							String deleteInventoryRes = WSClient.processSOAPMessage(deleteInventoryReq);
							if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_Result_resultStatusFlag", false)) {
								if (WSAssert.assertIfElementValueEquals(deleteInventoryRes,
										"DeleteInventoryItemResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									String db1 = WSClient.getDBRow(query).get("ITEM_ID");
									System.out.println(db1);
									if (db1 == null)
										WSClient.writeToReport(LogStatus.PASS,
												"The item has been removed from the reservation");
									else
										WSClient.writeToReport(LogStatus.FAIL, "The delete operation was unsuccessful");

								} else
									WSClient.writeToReport(LogStatus.FAIL, "Delete Inventory Items has failed!");

							} else
								WSClient.writeToReport(LogStatus.FAIL, "Delete Inventory Items has failed!");

							if (WSAssert.assertIfElementExists(deleteInventoryRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(deleteInventoryRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(deleteInventoryRes,
										"DeleteInventoryItemResponse_Result_GDSError", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							} else if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(deleteInventoryRes,
										"DeleteInventoryItemResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_faultcode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSClient.getElementValue(deleteInventoryRes,
										"DeleteInventoryItemResponse_faultstring", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Blocked : Unable to add item to the reservation");
						}
					} else
						WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Reservation Failed !");
				} else
					WSClient.writeToReport(LogStatus.WARNING,
							" Pre requisite Blocked :Property config Data is not Available !");
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Profile Failed !");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (resvID != null || !resvID.equals("error")) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}

		}
	}

	@Test(groups = { "minimumRegression", "DeleteInventoryItem", "OWS", "Reservation" })
	/****
	 * Verify that error message is populated on the response when item code is
	 * not sent on the request.
	 * 
	 * Prerequisites : ->Profile is created ->Reservation is created ->Items are
	 * attached to the reservation ->Market code,source code,room Type are to be
	 * available in the resort.
	 * 
	 * 
	 */
	public void deleteInventoryItem_40266() throws Exception {
		try {
			String testName = "deleteInventoryItem_40266";
			WSClient.startTest(testName,
					"Verify that error message is populated on the response when invalid item Code on the request.",
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

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod",
						"SourceCode", "MarketCode", "ItemClass", "ItemCode" })) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					/**** Prerequisite 2 : Create Reservation ****/

					HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
					resvID = resv.get("reservationId");

					WSClient.writeToReport(LogStatus.INFO, " <b>Reservation ID : " + resvID + "</b>");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_extresort}", resort);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						// WSClient.setData("{var_count}", "0");

						/***** Prerequisite 3 :Reserve Inventory Items ****/

						String holdItemInventoryReq = WSClient.createSOAPMessage("ReserveInventoryItems", "DS_01");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);

						if (WSAssert.assertIfElementExists(holdItemInventoryRes, "ReserveInventoryItemsRS_Success",
								true)) {

							String query = WSClient.getQuery("OWSDeleteInventoryItem", "QS_01");
							String db = WSClient.getDBRow(query).get("ITEM_ID");

							WSClient.writeToReport(LogStatus.INFO,
									"<b> The item added to the reservation is : " + db + "</b>");

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							/*** OWS Delete Inventory Item ***/

							WSClient.setData("{var_itemCode}", WSClient.getKeywordData("{KEYWORD_RANDNUM_3}"));

							String deleteInventoryReq = WSClient.createSOAPMessage("OWSDeleteInventoryItem", "DS_01");
							String deleteInventoryRes = WSClient.processSOAPMessage(deleteInventoryReq);
							if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_Result_resultStatusFlag", false)) {
								if (WSAssert.assertIfElementValueEquals(deleteInventoryRes,
										"DeleteInventoryItemResponse_Result_resultStatusFlag", "FAIL", false)) {

								} else
									WSClient.writeToReport(LogStatus.FAIL, "Delete Inventory Items is successful ");

							} else
								WSClient.writeToReport(LogStatus.FAIL, "Delete Inventory Items has failed!");

							if (WSAssert.assertIfElementExists(deleteInventoryRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(deleteInventoryRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(deleteInventoryRes,
										"DeleteInventoryItemResponse_Result_GDSError", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							} else if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(deleteInventoryRes,
										"DeleteInventoryItemResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_faultcode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSClient.getElementValue(deleteInventoryRes,
										"DeleteInventoryItemResponse_faultstring", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Blocked : Unable to add item to the reservation");
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
			if (!resvID.equals("error")) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}
		}
	}

	@Test(groups = { "minimumRegression", "DeleteInventoryItem", "OWS", "Reservation" })
	/****
	 * Verify that error message is populated on the response when invalid
	 * reservation ID is sent on the request.
	 * 
	 * Prerequisites : ->Profile is created ->Reservation is created ->Items are
	 * attached to the reservation ->Market code,source code,room Type are to be
	 * available in the resort.
	 * 
	 * 
	 */
	public void deleteInventoryItem_40259() throws Exception {
		try {
			String testName = "deleteInventoryItem_40259";
			WSClient.startTest(testName,
					"Verify that error message is populated on the response when invalid reservation ID  sent on the request.",
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

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			/*** OWS Delete Inventory Item ***/

			WSClient.setData("{var_extresort}", resort);
			WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
			WSClient.setData("{var_resvId}", WSClient.getKeywordData("{KEYWORD_RANDNUM_4}"));
			String deleteInventoryReq = WSClient.createSOAPMessage("OWSDeleteInventoryItem", "DS_01");
			String deleteInventoryRes = WSClient.processSOAPMessage(deleteInventoryReq);
			if (WSAssert.assertIfElementExists(deleteInventoryRes,
					"DeleteInventoryItemResponse_Result_resultStatusFlag", false)) {
				if (WSAssert.assertIfElementValueEquals(deleteInventoryRes,
						"DeleteInventoryItemResponse_Result_resultStatusFlag", "FAIL", false)) {

				} else
					WSClient.writeToReport(LogStatus.FAIL, "Delete Inventory Items is successful ");

			} else
				WSClient.writeToReport(LogStatus.FAIL, "Delete Inventory Items has failed!");

			if (WSAssert.assertIfElementExists(deleteInventoryRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(deleteInventoryRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				if (message != "")
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(deleteInventoryRes, "DeleteInventoryItemResponse_Result_GDSError",
					true)) {

				/****
				 * Verifying whether the error Message is populated on the
				 * response
				 ****/

				String message = WSAssert.getElementValue(deleteInventoryRes,
						"DeleteInventoryItemResponse_Result_GDSError", XMLType.RESPONSE);
				if (message != "")
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			} else if (WSAssert.assertIfElementExists(deleteInventoryRes,
					"DeleteInventoryItemResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the
				 * response
				 ****/

				String message = WSAssert.getElementValue(deleteInventoryRes,
						"DeleteInventoryItemResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				if (message != "")
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(deleteInventoryRes, "DeleteInventoryItemResponse_faultcode", true)) {

				/****
				 * Verifying whether the error Message is populated on the
				 * response
				 ****/

				String message = WSClient.getElementValue(deleteInventoryRes, "DeleteInventoryItemResponse_faultstring",
						XMLType.RESPONSE);
				if (message != "")
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error")) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}

		}
	}

	@Test(groups = { "minimumRegression", "DeleteInventoryItem", "OWS", "Reservation" })
	/****
	 * Verify that items in an item class are deleted when attached to a
	 * reservation.
	 * 
	 * Prerequisites : ->Profile is created ->Reservation is created ->Items are
	 * attached to the reservation ->Market code,source code,room Type are to be
	 * available in the resort.
	 * 
	 * 
	 */
	public void deleteInventoryItem_40258() throws Exception {
		try {
			String testName = "deleteInventoryItem_40258";
			WSClient.startTest(testName,
					"Verify that items in an item class are deleted when attached to a reservation.",
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

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod",
						"SourceCode", "MarketCode", "ItemClass", "ItemCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					/**** Prerequisite 2 : Create Reservation ****/

					HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
					resvID = resv.get("reservationId");

					if (resvID != "error") {

						WSClient.writeToReport(LogStatus.INFO, " <b>Reservation ID : " + resvID + "</b>");
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_itemClass}", OperaPropConfig.getDataSetForCode("ItemClass", "DS_01")); // WSClient.setData("{var_count}",
						WSClient.setData("{var_extresort}", resort); // "0");
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						WSClient.setData("{var_itemCode2}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_02"));
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));

						/***** Prerequisite 3 :Reserve Inventory Items ****/

						String holdItemInventoryReq = WSClient.createSOAPMessage("ReserveInventoryItems", "DS_02");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);

						if (WSAssert.assertIfElementExists(holdItemInventoryRes, "ReserveInventoryItemsRS_Success",
								true)) {

							WSClient.setData("{var_resort}", resortOperaValue);
							String query = WSClient.getQuery("OWSDeleteInventoryItem", "QS_03");
							String db = WSClient.getDBRow(query).get("COUNT");

							WSClient.writeToReport(LogStatus.INFO,
									"<b> The Number of items added to the reservation  : " + db + "</b>");

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							/*** OWS Delete Inventory Item ***/

							WSClient.setData("{var_confirmationId}", confirmationID);

							String deleteInventoryReq = WSClient.createSOAPMessage("OWSDeleteInventoryItem", "DS_04");
							String deleteInventoryRes = WSClient.processSOAPMessage(deleteInventoryReq);
							if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_Result_resultStatusFlag", false)) {
								if (WSAssert.assertIfElementValueEquals(deleteInventoryRes,
										"DeleteInventoryItemResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									db = WSClient.getDBRow(query).get("COUNT");
									System.out.println(db);
									WSClient.writeToReport(LogStatus.INFO,
											"<b> The Number of items after delete  : " + db + "</b>");
									if (db.equals("0")) {
										WSClient.writeToReport(LogStatus.PASS,
												"The Items to an item class are deleted successfully");
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												"DeleteInventoryItems operation didnot delete all the items");

								} else
									WSClient.writeToReport(LogStatus.FAIL, "Delete Inventory Items is unsuccessful ");

							} else
								WSClient.writeToReport(LogStatus.FAIL, "Delete Inventory Items has failed!");

							if (WSAssert.assertIfElementExists(deleteInventoryRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(deleteInventoryRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(deleteInventoryRes,
										"DeleteInventoryItemResponse_Result_GDSError", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							} else if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(deleteInventoryRes,
										"DeleteInventoryItemResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_faultcode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSClient.getElementValue(deleteInventoryRes,
										"DeleteInventoryItemResponse_faultstring", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Blocked : Unable to add item to the reservation");
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
			if (!resvID.equals("error")) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");

			}
		}
	}

	@Test(groups = { "minimumRegression", "DeleteInventoryItem", "OWS", "Reservation" })
	/****
	 * Verify that a given item is deleted when multiple items are attached to
	 * the reservation. Prerequisites : ->Profile is created ->Reservation is
	 * created ->Items are attached to the reservation ->Market code,source
	 * code,room Type are to be available in the resort.
	 * 
	 ****/
	public void deleteInventoryItems_40256() throws Exception {
		try {
			String testName = "deleteInventoryItems_40256";
			WSClient.startTest(testName,
					"Verify that a given item is deleted when multiple items are attached to the reservation.",
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

				WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");

				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod",
						"SourceCode", "MarketCode", "ItemClass", "ItemCode" })) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
					WSClient.setData("{var_extresort}", resort);

					/**** Prerequisite 2 : Create Reservation ****/

					HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
					resvID = resv.get("reservationId");

					WSClient.writeToReport(LogStatus.INFO, " <b>Reservation ID : " + resvID + "</b>");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						WSClient.setData("{var_itemCode2}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_02"));

						// WSClient.setData("{var_count}", "0");

						/***** Prerequisite 3 :Reserve Inventory Items ****/

						String holdItemInventoryReq = WSClient.createSOAPMessage("ReserveInventoryItems", "DS_02");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);

						if (WSAssert.assertIfElementExists(holdItemInventoryRes, "ReserveInventoryItemsRS_Success",
								true)) {

							String query = WSClient.getQuery("OWSDeleteInventoryItem", "QS_01");
							int db = WSClient.getDBRows(query).size();

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							/*** OWS Delete Inventory Item ***/

							WSClient.setData("{var_confirmationId}", confirmationID);

							String deleteInventoryReq = WSClient.createSOAPMessage("OWSDeleteInventoryItem", "DS_01");
							String deleteInventoryRes = WSClient.processSOAPMessage(deleteInventoryReq);
							if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_Result_resultStatusFlag", false)) {
								if (WSAssert.assertIfElementValueEquals(deleteInventoryRes,
										"DeleteInventoryItemResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									System.out.println();
									int db1 = WSClient.getDBRows(query).size();

									if (db1 == db - 1) {

										String query1 = WSClient.getQuery("OWSDeleteInventoryItem", "QS_02");

										String item = WSClient.getDBRow(query1).get("ITEM_ID");

										if (item == null) {
											WSClient.writeToReport(LogStatus.PASS,
													" The item has been deleted successfully.");
										} else

											WSClient.writeToReport(LogStatus.FAIL, " Another item has been deleted.");
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												"The count in the database doesnot match");

								} else
									WSClient.writeToReport(LogStatus.FAIL, "Delete Inventory Items has failed!");

							} else
								WSClient.writeToReport(LogStatus.FAIL, "Delete Inventory Items has failed!");
							if (WSAssert.assertIfElementExists(deleteInventoryRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(deleteInventoryRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(deleteInventoryRes,
										"DeleteInventoryItemResponse_Result_GDSError", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							} else if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(deleteInventoryRes,
										"DeleteInventoryItemResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_faultcode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSClient.getElementValue(deleteInventoryRes,
										"DeleteInventoryItemResponse_faultstring", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to hold item");
						}
					} else
						WSClient.writeToReport(LogStatus.WARNING, "Blocked :Create Reservation Failed !");
				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked :Property config Data is not Available !");
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked :Create Profile Failed !");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error")) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}

		}
	}

	@Test(groups = { "minimumRegression", "DeleteInventoryItem", "OWS", "Reservation" })
	/****
	 * Verify item is deleted when Confirmation ID and Leg Number sent on the
	 * request.
	 * 
	 * Prerequisites : ->Profile is created ->Reservation is created ->Items are
	 * attached to the reservation ->Market code,source code,room Type are to be
	 * available in the resort.
	 * 
	 * 
	 */
	public void deleteInventoryItem_41243() throws Exception {
		try {
			String testName = "deleteInventoryItem_41243";
			WSClient.startTest(testName,
					"Verify item is deleted when Confirmation ID and leg Number is sent on the request.",
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

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod",
						"SourceCode", "MarketCode", "ItemClass", "ItemCode" })) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					/**** Prerequisite 2 : Create Reservation ****/

					HashMap<String, String> resv = CreateReservation.createReservation("DS_13");
					resvID = resv.get("reservationId");
					confirmationID = resv.get("confirmationId");

					WSClient.writeToReport(LogStatus.INFO, " <b>First Reservation ID : " + resvID + "</b>");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_extresort}", resort);

						String querylegNo = WSClient.getQuery("CreateReservation", "QS_01");
						String legNo = WSClient.getDBRow(querylegNo).get("CONFIRMATION_LEG_NO");

						WSClient.setData("{var_confId}", confirmationID);
						WSClient.setData("{var_legNo}", legNo);

						// WSClient.setData("{var_count}", "0");

						/***** Prerequisite 3 :Reserve Inventory Items ****/

						String holdItemInventoryReq = WSClient.createSOAPMessage("ReserveInventoryItems", "DS_01");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);

						if (WSAssert.assertIfElementExists(holdItemInventoryRes, "ReserveInventoryItemsRS_Success",
								true)) {

							String query = WSClient.getQuery("OWSDeleteInventoryItem", "QS_01");
							String db = WSClient.getDBRow(query).get("ITEM_ID");

							WSClient.writeToReport(LogStatus.INFO,
									"<b> The item added to the reservation is : " + db + "</b>");

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							WSClient.setData("{var_confId}", confirmationID);

							WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));

							/*** OWS Delete Inventory Item ***/

							String deleteInventoryReq = WSClient.createSOAPMessage("OWSDeleteInventoryItem", "DS_06");
							String deleteInventoryRes = WSClient.processSOAPMessage(deleteInventoryReq);
							if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_Result_resultStatusFlag", false)) {
								if (WSAssert.assertIfElementValueEquals(deleteInventoryRes,
										"DeleteInventoryItemResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									String db1 = WSClient.getDBRow(query).get("ITEM_ID");
									if (db1 == null)
										WSClient.writeToReport(LogStatus.PASS,
												"The item has been removed from the reservation");
									else
										WSClient.writeToReport(LogStatus.FAIL, "The delete operation was unsuccessful");

								} else
									WSClient.writeToReport(LogStatus.FAIL, "Delete Inventory Items is unsuccessful ");

							} else
								WSClient.writeToReport(LogStatus.FAIL, "Delete Inventory Items has failed!");

							if (WSAssert.assertIfElementExists(deleteInventoryRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(deleteInventoryRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(deleteInventoryRes,
										"DeleteInventoryItemResponse_Result_GDSError", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							} else if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(deleteInventoryRes,
										"DeleteInventoryItemResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(deleteInventoryRes,
									"DeleteInventoryItemResponse_faultcode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSClient.getElementValue(deleteInventoryRes,
										"DeleteInventoryItemResponse_faultstring", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Blocked : Unable to add item to the reservation");
						}

					} else
						WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Reservation Failed !");
				} else
					WSClient.writeToReport(LogStatus.WARNING,
							" Pre requisite Blocked :Property config Data is not Available !");
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Pre requisite Blocked :Create Profile Failed !");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error")) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}

		}
	}

	@Test(groups = { "minimumRegression", "DeleteInventoryItem", "OWS", "Reservation" })
	/****
	 * Verify that error message is populated on the response when invalid
	 * Confirmation ID is sent on the request.
	 * 
	 * Prerequisites : ->Profile is created ->Reservation is created ->Items are
	 * attached to the reservation ->Market code,source code,room Type are to be
	 * available in the resort.
	 * 
	 * 
	 */
	public void deleteInventoryItem_40260() throws Exception {
		try {
			String testName = "deleteInventoryItem_40260";
			WSClient.startTest(testName,
					"Verify that error message is populated on the response when invalid confirmation ID  sent on the request.",
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

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

			/*** OWS Delete Inventory Item ***/

			WSClient.setData("{var_extresort}", resort);
			WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
			WSClient.setData("{var_confId}", WSClient.getKeywordData("{KEYWORD_RANDNUM_4}"));
			String deleteInventoryReq = WSClient.createSOAPMessage("OWSDeleteInventoryItem", "DS_05");
			String deleteInventoryRes = WSClient.processSOAPMessage(deleteInventoryReq);
			if (WSAssert.assertIfElementExists(deleteInventoryRes,
					"DeleteInventoryItemResponse_Result_resultStatusFlag", false)) {
				if (WSAssert.assertIfElementValueEquals(deleteInventoryRes,
						"DeleteInventoryItemResponse_Result_resultStatusFlag", "FAIL", false)) {

				} else
					WSClient.writeToReport(LogStatus.FAIL, "Delete Inventory Items is successful ");

			} else
				WSClient.writeToReport(LogStatus.FAIL, "Delete Inventory Items has failed!");

			if (WSAssert.assertIfElementExists(deleteInventoryRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated on the response
				 ********/

				String message = WSAssert.getElementValue(deleteInventoryRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				if (message != "")
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(deleteInventoryRes, "DeleteInventoryItemResponse_Result_GDSError",
					true)) {

				/****
				 * Verifying whether the error Message is populated on the
				 * response
				 ****/

				String message = WSAssert.getElementValue(deleteInventoryRes,
						"DeleteInventoryItemResponse_Result_GDSError", XMLType.RESPONSE);
				if (message != "")
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			} else if (WSAssert.assertIfElementExists(deleteInventoryRes,
					"DeleteInventoryItemResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is populated on the
				 * response
				 ****/

				String message = WSAssert.getElementValue(deleteInventoryRes,
						"DeleteInventoryItemResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				if (message != "")
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(deleteInventoryRes, "DeleteInventoryItemResponse_faultcode", true)) {

				/****
				 * Verifying whether the error Message is populated on the
				 * response
				 ****/

				String message = WSClient.getElementValue(deleteInventoryRes, "DeleteInventoryItemResponse_faultstring",
						XMLType.RESPONSE);
				if (message != "")
					WSClient.writeToReport(LogStatus.INFO,
							"The error populated on the response is : <b>" + message + "</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error")) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}

		}
	}

}
