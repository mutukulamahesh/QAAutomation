package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ReleaseItemInventoryHold;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchHoldItems extends WSSetUp {

	String profileID = "";

	@Test(groups = { "sanity", "FetchHoldItems", "OWS", "Reservation" })
	public void fetchHoldItems_39780() {
		try {
			String testName = "fetchHoldItems_39780";
			WSClient.startTest(testName,
					"Verify that hold item information are displayed correctly in the response when a single item id is passed in request",
					"sanity");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod", "ItemCode" })) {

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
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					// Prerequisite 2 Create Reservation
					HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");

						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						WSClient.setData("{var_count}", "1");

						// Prerequisite 3 : Create Item Hold

						String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory", "DS_01");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);

						if (WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_Success", true)) {

							// OWS Fetch Hold Items
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully held the item.</b>");
							WSClient.setData("{var_itemHoldId}", WSClient
									.getDBRow(WSClient.getQuery("OWSFetchHoldItems", "QS_01")).get("ITEM_HOLD_ID"));
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchHoldItemsReq = WSClient.createSOAPMessage("OWSFetchHoldItems", "DS_01");
							String fetchHoldItemsRes = WSClient.processSOAPMessage(fetchHoldItemsReq);

							if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
									"FetchHoldItemsResponse_Result_resultStatusFlag", false)) {
								if (WSAssert.assertIfElementValueEquals(fetchHoldItemsRes,
										"FetchHoldItemsResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									// DB Validation
									LinkedHashMap<String, String> expected = WSClient
											.getDBRow(WSClient.getQuery("QS_02"));
									LinkedHashMap<String, String> actuals = new LinkedHashMap<String, String>();
									actuals.put("ITEM_CODE",
											WSClient.getElementValue(fetchHoldItemsRes,
													"HoldInventoryItemElements_HoldInventoryItemElement_ItemCode",
													XMLType.RESPONSE));
									actuals.put("NAME",
											WSClient.getElementValue(fetchHoldItemsRes,
													"HoldInventoryItemElements_HoldInventoryItemElement_ItemName",
													XMLType.RESPONSE));
									actuals.put("QUANTITY",
											WSClient.getElementValue(fetchHoldItemsRes,
													"HoldInventoryItemElements_HoldInventoryItemElement_Quantity",
													XMLType.RESPONSE));
									actuals.put("BEGIN_DATE",
											WSClient.getElementValue(fetchHoldItemsRes,
													"HoldInventoryItemElements_HoldInventoryItemElement_ItemStartDate",
													XMLType.RESPONSE));
									actuals.put("END_DATE",
											WSClient.getElementValue(fetchHoldItemsRes,
													"HoldInventoryItemElements_HoldInventoryItemElement_ItemEndDate",
													XMLType.RESPONSE));

									WSAssert.assertEquals(expected, actuals, false);
								}

							}

							if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "FetchHoldItemsResponse_faultcode",
									true)) {
								if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
										"FetchHoldItemsResponse_faultstring", true)) {

									String message = WSClient.getElementValue(fetchHoldItemsRes,
											"FetchHoldItemsResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchHoldItemsRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
									"FetchHoldItemsResponse_Result_OperaErrorCode", true)) {

								String code = WSClient.getElementValue(fetchHoldItemsRes,
										"FetchHoldItemsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error code displayed in the response is :" + code + "</b>");
							}

							if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
									"FetchHoldItemsResponse_Result_GDSError", true)) {

								String message = WSClient.getElementValue(fetchHoldItemsRes,
										"FetchHoldItemsResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error displayed in the response is :" + message + "</b>");
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to hold item");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CancelReservation.cancelReservation("DS_02");
					if (!WSClient.getData("{var_itemHoldId}").equals("")) {
						ReleaseItemInventoryHold.releaseItemInventoryHold("DS_01");
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchHoldItems", "OWS", "Reservation" })
	public void fetchHoldItems_40243() {
		try {
			String testName = "fetchHoldItems_40243";
			WSClient.startTest(testName,
					"Verify that hold item information are displayed correctly in the response when multiple item id is passed in request",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod", "ItemCode" })) {

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
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					// Prerequisite 2 Create Reservation
					HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);

						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						WSClient.setData("{var_count}", "1");

						// Prerequisite 3 : Create Item Hold

						String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory", "DS_01");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);

						if (WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_Success", true)) {

							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully held the item.</b>");
							WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_02"));

							String holdItemInventoryReq1 = WSClient.createSOAPMessage("HoldItemInventory", "DS_01");
							String holdItemInventoryRes1 = WSClient.processSOAPMessage(holdItemInventoryReq1);

							if (WSAssert.assertIfElementExists(holdItemInventoryRes1, "HoldItemInventoryRS_Success",
									true)) {

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully held the item.</b>");

								// OWS Fetch Hold Items
								List<LinkedHashMap<String, String>> item_ids = WSClient
										.getDBRows(WSClient.getQuery("OWSFetchHoldItems", "QS_01"));

								for (int i = 0; i < item_ids.size(); ++i) {
									int j = i + 1;
									WSClient.setData("{var_itemHoldId" + j + "}", item_ids.get(i).get("ITEM_HOLD_ID"));
								}

								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

								String fetchHoldItemsReq = WSClient.createSOAPMessage("OWSFetchHoldItems", "DS_02");
								String fetchHoldItemsRes = WSClient.processSOAPMessage(fetchHoldItemsReq);

								if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
										"FetchHoldItemsResponse_Result_resultStatusFlag", false)) {
									if (WSAssert.assertIfElementValueEquals(fetchHoldItemsRes,
											"FetchHoldItemsResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										// DB Validation
										List<LinkedHashMap<String, String>> expected = WSClient
												.getDBRows(WSClient.getQuery("QS_03"));
										HashMap<String, String> xpath = new HashMap<String, String>();

										xpath.put("HoldInventoryItemElements_HoldInventoryItemElement_ItemCode",
												"FetchHoldItemsResponse_HoldInventoryItemElements_HoldInventoryItemElement");
										xpath.put("HoldInventoryItemElements_HoldInventoryItemElement_ItemName",
												"FetchHoldItemsResponse_HoldInventoryItemElements_HoldInventoryItemElement");
										xpath.put("HoldInventoryItemElements_HoldInventoryItemElement_Quantity",
												"FetchHoldItemsResponse_HoldInventoryItemElements_HoldInventoryItemElement");
										xpath.put("HoldInventoryItemElements_HoldInventoryItemElement_ItemStartDate",
												"FetchHoldItemsResponse_HoldInventoryItemElements_HoldInventoryItemElement");
										xpath.put("HoldInventoryItemElements_HoldInventoryItemElement_ItemEndDate",
												"FetchHoldItemsResponse_HoldInventoryItemElements_HoldInventoryItemElement");

										List<LinkedHashMap<String, String>> actuals = WSClient
												.getMultipleNodeList(fetchHoldItemsRes, xpath, false, XMLType.RESPONSE);
										WSAssert.assertEquals(actuals, expected, false);
									}

								}

								if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
										"FetchHoldItemsResponse_faultcode", true)) {
									if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
											"FetchHoldItemsResponse_faultstring", true)) {

										String message = WSClient.getElementValue(fetchHoldItemsRes,
												"FetchHoldItemsResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
									}
								}

								if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "Result_Text_TextElement",
										true)) {

									String message = WSClient.getElementValue(fetchHoldItemsRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}

								if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
										"FetchHoldItemsResponse_Result_OperaErrorCode", true)) {

									String code = WSClient.getElementValue(fetchHoldItemsRes,
											"FetchHoldItemsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The error code displayed in the response is :" + code + "</b>");
								}

								if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
										"FetchHoldItemsResponse_Result_GDSError", true)) {

									String message = WSClient.getElementValue(fetchHoldItemsRes,
											"FetchHoldItemsResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The error displayed in the response is :" + message + "</b>");
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to hold item");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to hold item");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CancelReservation.cancelReservation("DS_02");
					if (!WSClient.getData("{var_itemHoldId}").equals("")) {
						ReleaseItemInventoryHold.releaseItemInventoryHold("DS_01");
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchHoldItems", "OWS", "Reservation" })
	public void fetchHoldItems_40244() {
		try {
			String testName = "fetchHoldItems_40244";
			WSClient.startTest(testName,
					"Verify that error is coming in response when empty hotel code is passed in request.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod", "ItemCode" })) {

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
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					// Prerequisite 2 Create Reservation
					HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						WSClient.setData("{var_count}", "1");

						// Prerequisite 3 : Create Item Hold

						String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory", "DS_01");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);

						if (WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_Success", true)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully held the item.</b>");
							// OWS Fetch Hold Items
							WSClient.setData("{var_itemHoldId}", WSClient
									.getDBRow(WSClient.getQuery("OWSFetchHoldItems", "QS_01")).get("ITEM_HOLD_ID"));
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchHoldItemsReq = WSClient.createSOAPMessage("OWSFetchHoldItems", "DS_03");
							String fetchHoldItemsRes = WSClient.processSOAPMessage(fetchHoldItemsReq);

							if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
									"FetchHoldItemsResponse_Result_resultStatusFlag", false)) {
								WSAssert.assertIfElementValueEquals(fetchHoldItemsRes,
										"FetchHoldItemsResponse_Result_resultStatusFlag", "FAIL", false);
							}
							if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "FetchHoldItemsResponse_faultcode",
									true)) {
								if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
										"FetchHoldItemsResponse_faultstring", true)) {

									String message = WSClient.getElementValue(fetchHoldItemsRes,
											"FetchHoldItemsResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchHoldItemsRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
									"FetchHoldItemsResponse_Result_OperaErrorCode", true)) {

								String code = WSClient.getElementValue(fetchHoldItemsRes,
										"FetchHoldItemsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error code displayed in the response is :" + code + "</b>");
							}

							if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
									"FetchHoldItemsResponse_Result_GDSError", true)) {

								String message = WSClient.getElementValue(fetchHoldItemsRes,
										"FetchHoldItemsResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error displayed in the response is :" + message + "</b>");
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to hold item");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CancelReservation.cancelReservation("DS_02");
					if (!WSClient.getData("{var_itemHoldId}").equals("")) {
						ReleaseItemInventoryHold.releaseItemInventoryHold("DS_01");
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchHoldItems", "OWS", "Reservation" })
	public void fetchHoldItems_40247() {
		try {
			String testName = "fetchHoldItems_40247";
			WSClient.startTest(testName,
					"Verify that error is coming in response when invalid hotel code is passed in request.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod", "ItemCode" })) {

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
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					// Prerequisite 2 Create Reservation
					HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						WSClient.setData("{var_count}", "1");

						// Prerequisite 3 : Create Item Hold

						String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory", "DS_01");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);

						if (WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_Success", true)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully held the item.</b>");
							// OWS Fetch Hold Items
							WSClient.setData("{var_itemHoldId}", WSClient
									.getDBRow(WSClient.getQuery("OWSFetchHoldItems", "QS_01")).get("ITEM_HOLD_ID"));
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchHoldItemsReq = WSClient.createSOAPMessage("OWSFetchHoldItems", "DS_04");
							String fetchHoldItemsRes = WSClient.processSOAPMessage(fetchHoldItemsReq);

							if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
									"FetchHoldItemsResponse_Result_resultStatusFlag", false)) {
								WSAssert.assertIfElementValueEquals(fetchHoldItemsRes,
										"FetchHoldItemsResponse_Result_resultStatusFlag", "FAIL", false);
							}

							if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "FetchHoldItemsResponse_faultcode",
									true)) {
								if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
										"FetchHoldItemsResponse_faultstring", true)) {

									String message = WSClient.getElementValue(fetchHoldItemsRes,
											"FetchHoldItemsResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchHoldItemsRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
									"FetchHoldItemsResponse_Result_OperaErrorCode", true)) {

								String code = WSClient.getElementValue(fetchHoldItemsRes,
										"FetchHoldItemsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error code displayed in the response is :" + code + "</b>");
							}

							if (WSAssert.assertIfElementExists(fetchHoldItemsRes,
									"FetchHoldItemsResponse_Result_GDSError", true)) {

								String message = WSClient.getElementValue(fetchHoldItemsRes,
										"FetchHoldItemsResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error displayed in the response is :" + message + "</b>");
							}

						}

					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to hold item");
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CancelReservation.cancelReservation("DS_02");
					if (!WSClient.getData("{var_itemHoldId}").equals("")) {
						ReleaseItemInventoryHold.releaseItemInventoryHold("DS_01");
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchHoldItems", "OWS", "Reservation" })
	public void fetchHoldItems_40245() {
		try {
			String testName = "fetchHoldItems_40245";
			WSClient.startTest(testName,
					"Verify that error is coming in response when empty item code is passed in request.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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

				// OWS Fetch Hold Items

				OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
				String fetchHoldItemsReq = WSClient.createSOAPMessage("OWSFetchHoldItems", "DS_05");
				String fetchHoldItemsRes = WSClient.processSOAPMessage(fetchHoldItemsReq);

				if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "FetchHoldItemsResponse_Result_resultStatusFlag",
						false)) {
					WSAssert.assertIfElementValueEquals(fetchHoldItemsRes,
							"FetchHoldItemsResponse_Result_resultStatusFlag", "FAIL", false);
				}
				if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "FetchHoldItemsResponse_faultcode", true)) {
					if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "FetchHoldItemsResponse_faultstring", true)) {

						String message = WSClient.getElementValue(fetchHoldItemsRes,
								"FetchHoldItemsResponse_faultstring", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
					}
				}

				if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "Result_Text_TextElement", true)) {

					String message = WSClient.getElementValue(fetchHoldItemsRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The text displayed in the response is :" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "FetchHoldItemsResponse_Result_OperaErrorCode",
						true)) {

					String code = WSClient.getElementValue(fetchHoldItemsRes,
							"FetchHoldItemsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The error code displayed in the response is :" + code + "</b>");
				}

				if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "FetchHoldItemsResponse_Result_GDSError", true)) {

					String message = WSClient.getElementValue(fetchHoldItemsRes,
							"FetchHoldItemsResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The error displayed in the response is :" + message + "</b>");
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "FetchHoldItems", "OWS", "Reservation" })
	public void fetchHoldItems_40246() {
		try {
			String testName = "fetchHoldItems_40246";
			WSClient.startTest(testName,
					"Verify that error is coming in response when invalid item code is passed in request.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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

				// OWS Fetch Hold Items
				WSClient.setData("{var_itemHoldId}", WSClient.getKeywordData("{KEYWORD_STR}"));
				OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
				String fetchHoldItemsReq = WSClient.createSOAPMessage("OWSFetchHoldItems", "DS_01");
				String fetchHoldItemsRes = WSClient.processSOAPMessage(fetchHoldItemsReq);

				if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "FetchHoldItemsResponse_Result_resultStatusFlag",
						false)) {
					WSAssert.assertIfElementValueEquals(fetchHoldItemsRes,
							"FetchHoldItemsResponse_Result_resultStatusFlag", "FAIL", false);
				}
				if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "FetchHoldItemsResponse_faultcode", true)) {
					if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "FetchHoldItemsResponse_faultstring", true)) {

						String message = WSClient.getElementValue(fetchHoldItemsRes,
								"FetchHoldItemsResponse_faultstring", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
					}
				}

				if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "Result_Text_TextElement", true)) {

					String message = WSClient.getElementValue(fetchHoldItemsRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The text displayed in the response is :" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "FetchHoldItemsResponse_Result_OperaErrorCode",
						true)) {

					String code = WSClient.getElementValue(fetchHoldItemsRes,
							"FetchHoldItemsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The error code displayed in the response is :" + code + "</b>");
				}

				if (WSAssert.assertIfElementExists(fetchHoldItemsRes, "FetchHoldItemsResponse_Result_GDSError", true)) {

					String message = WSClient.getElementValue(fetchHoldItemsRes,
							"FetchHoldItemsResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The error displayed in the response is :" + message + "</b>");
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}
}
