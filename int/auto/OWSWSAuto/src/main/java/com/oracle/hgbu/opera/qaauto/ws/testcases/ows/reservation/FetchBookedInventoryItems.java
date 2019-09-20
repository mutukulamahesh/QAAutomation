package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchBookedInventoryItems extends WSSetUp {

	static HashMap<String, String> Confirmation = new HashMap<String, String>();

	// Total Test Cases : 5

	// Creating Profile
	public String createProfile(String ds) {
		String profileID = "";
		try {
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			// WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a
			// Profile"+"</b>");

			profileID = CreateProfile.createProfile(ds);
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		return profileID;
	}

	// Creating Reservation
	public String createReservation(String ds) {
		String reservationId = "";
		try {
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			/*************
			 * Fetch Details for rate code, payment method
			 ******************/
			WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
			WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
			WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
			WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
			WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
			WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			// WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a
			// Reservation"+"</b>");
			Confirmation = CreateReservation.createReservation(ds);
			reservationId = Confirmation.get("reservationId");

			WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + reservationId + "</b>");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		return reservationId;
	}

	public void setOWSHeader() {
		try {
			String resort = OPERALib.getResort();
			String uname = OPERALib.getUserName();
			String channel = OWSLib.getChannel();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "OWS Header not set.");
		}
	}

	public void setOperaHeader() {
		try {
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Opera Header not set.");
		}
	}

	// Sanity Test Case :1
	@Test(groups = { "sanity", "Reservation", "FetchBookedInventoryItems", "OWS" })
	public void fetchBookedInventoryItems_39123() {
		String previousValue = "Y";
		try {
			String testname = "fetchBookedInventoryItems_39123";
			WSClient.startTest(testname,
					"Verify that inventory items booked to a reservation are fetched correctly by passing valid values in mandatory fields",
					"sanity");

			setOperaHeader();

			// Setting ITEM_INVENTORY Parameter
			WSClient.setData("{var_parameter}", "ITEM_INVENTORY");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue = Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId, resvID;

				// Creating a profile
				if (!(UniqueId = createProfile("DS_01")).equals("error")) {
					// Creating reservation for above profile
					if (!(resvID = createReservation("DS_04")).equals("error")) {
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);

						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						// Setting Variables
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);
						WSClient.setData("{var_resort}", resort);
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));

						// Attaching Inventory Items to Reservation
						String ReserveInventoryItemsReq = WSClient.createSOAPMessage("ReserveInventoryItems", "DS_01");
						String ReserveInventoryItemsRes = WSClient.processSOAPMessage(ReserveInventoryItemsReq);

						if (WSAssert.assertIfElementExists(ReserveInventoryItemsRes, "ReserveInventoryItemsRS_Success",
								true)) {

							setOWSHeader();

							resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

							// Creating request and processing response for OWS
							// FetchBookedInventoryItems Operation
							String FetchBookedItemsReq = WSClient.createSOAPMessage("OWSFetchBookedInventoryItems",
									"DS_01");
							String FetchBookedItemsRes = WSClient.processSOAPMessage(FetchBookedItemsReq);

							// Validating response of FetchBookedInventoryItems
							if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
									"FetchBookedInventoryItemsResponse_Result_resultStatusFlag", true)) {
								if (WSAssert.assertIfElementValueEquals(FetchBookedItemsRes,
										"FetchBookedInventoryItemsResponse_Result_resultStatusFlag", "SUCCESS",
										false)) {

									// Database Validation
									LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
									LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
									xPath.put("BookedItemList_InventoryItem_ItemCode",
											"FetchBookedInventoryItemsResponse_BookedItemList_InventoryItem");
									xPath.put("BookedItemList_InventoryItem_ItemName",
											"FetchBookedInventoryItemsResponse_BookedItemList_InventoryItem");
									xPath.put("Description_Text_TextElement",
											"FetchBookedInventoryItemsResponse_BookedItemList_InventoryItem");
									xPath.put("BookedItemList_InventoryItem_SellControl",
											"FetchBookedInventoryItemsResponse_BookedItemList_InventoryItem");
									xPath.put("BookedItemList_InventoryItem_SellSeparate",
											"FetchBookedInventoryItemsResponse_BookedItemList_InventoryItem");
									xPath.put("BookedItemList_InventoryItem_Quantity",
											"FetchBookedInventoryItemsResponse_BookedItemList_InventoryItem");
									actualValues = WSClient.getSingleNodeList(FetchBookedItemsRes, xPath, false,
											XMLType.RESPONSE);
									String query = WSClient.getQuery("QS_01");
									expectedValues = WSClient.getDBRow(query);
									WSAssert.assertEquals(expectedValues, actualValues, false);

								} else {
									if (WSAssert.assertIfElementExists(FetchBookedItemsRes, "Result_Text_TextElement",
											true)) {
										String message = WSAssert.getElementValue(FetchBookedItemsRes,
												"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"The error displayed in the FetchBookedItems response is :" + message);
										if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
												"FetchBookedInventoryItemsResponse_Result_OperaErrorCode", true)) {
											String operaErrorCode = WSAssert.getElementValue(FetchBookedItemsRes,
													"FetchBookedInventoryItemsResponse_Result_OperaErrorCode",
													XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"Opera Error Code :" + operaErrorCode);

										}
									}
									if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
											"FetchBookedInventoryItemsResponse_Result_GDSError", true)) {
										String message = WSAssert.getElementValue(FetchBookedItemsRes,
												"FetchBookedInventoryItemsResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"The gds error displayed in the FetchBookedItems response is :"
														+ message);
									}
								}
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}

							// Canceling Reservation
							// WSClient.writeToReport(LogStatus.INFO,
							// "<b>"+"Cancelling Reservation"+"</b>");
							CancelReservation.cancelReservation("DS_02");
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisites failed!------HoldItemInventory-----Blocked");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		} finally {
			// Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "ITEM_INVENTORY");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter");
			}
		}
	}

	// Minimum Regression Test Case :1
	@Test(groups = { "minimumRegression", "Reservation", "FetchBookedInventoryItems", "OWS" })
	public void fetchBookedInventoryItems_39126() {
		String previousValue = "Y";
		try {
			String testname = "fetchBookedInventoryItems_39126";
			WSClient.startTest(testname, "Verify that error message is obatined by passing wrong hotel code",
					"minimumRegression");

			setOperaHeader();

			// Setting ITEM_INVENTORY Parameter
			WSClient.setData("{var_parameter}", "ITEM_INVENTORY");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue = Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId, resvID;

				// Creating a profile
				if (!(UniqueId = createProfile("DS_01")).equals("error")) {
					// Creating reservation for above profile
					if (!(resvID = createReservation("DS_04")).equals("error")) {
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						// Setting Variables
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);
						WSClient.setData("{var_resort}", resort);
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));

						// Attaching Inventory Items to Reservation
						String ReserveInventoryItemsReq = WSClient.createSOAPMessage("ReserveInventoryItems", "DS_01");
						String ReserveInventoryItemsRes = WSClient.processSOAPMessage(ReserveInventoryItemsReq);

						if (WSAssert.assertIfElementExists(ReserveInventoryItemsRes, "ReserveInventoryItemsRS_Success",
								true)) {

							setOWSHeader();
							WSClient.setData("{var_owsresort}", WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));

							// Creating request and processing response for OWS
							// FetchBookedInventoryItems Operation
							String FetchBookedItemsReq = WSClient.createSOAPMessage("OWSFetchBookedInventoryItems",
									"DS_05");
							String FetchBookedItemsRes = WSClient.processSOAPMessage(FetchBookedItemsReq);

							// Validating response of FetchBookedInventoryItems
							if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
									"FetchBookedInventoryItemsResponse_Result_resultStatusFlag", true)) {
								if (WSAssert.assertIfElementValueEquals(FetchBookedItemsRes,
										"FetchBookedInventoryItemsResponse_Result_resultStatusFlag", "FAIL", false)) {

									if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
											"FetchBookedInventoryItemsResponse_Result_OperaErrorCode", false)) {
										if (WSAssert.assertIfElementValueEquals(FetchBookedItemsRes,
												"FetchBookedInventoryItemsResponse_Result_OperaErrorCode",
												"INVALID_PROPERTY", true)) {
											WSAssert.assertIfElementValueEquals(FetchBookedItemsRes,
													"FetchBookedInventoryItemsResponse_Result_OperaErrorCode",
													"INVALID_PROPERTY", false);
										} else if (WSAssert.assertIfElementContains(FetchBookedItemsRes,
												"FetchBookedInventoryItemsResponse_Result_OperaErrorCode",
												"Unable to identify hotel code conversion for the requested channel",
												false)) {

										}
									}

								} else {
									if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
											"FetchBookedInventoryItemsResponse_Result_OperaErrorCode", true)) {
										String operaErrorCode = WSAssert.getElementValue(FetchBookedItemsRes,
												"FetchBookedInventoryItemsResponse_Result_OperaErrorCode",
												XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :" + operaErrorCode);
									}
									if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
											"FetchBookedInventoryItemsResponse_Result_GDSError", true)) {
										String message = WSAssert.getElementValue(FetchBookedItemsRes,
												"FetchBookedInventoryItemsResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"The gds error displayed in the FetchBookedItems response is :"
														+ message);
									}
								}
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}

							// Canceling Reservation
							// WSClient.writeToReport(LogStatus.INFO,
							// "<b>"+"Cancelling Reservation"+"</b>");
							CancelReservation.cancelReservation("DS_02");

						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisites failed!------HoldItemInventory-----Blocked");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		} finally {
			// Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "ITEM_INVENTORY");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : ITEM_INVENTORY");
			}
		}
	}

	// Minimum Regression Test Case :2
	@Test(groups = { "minimumRegression", "Reservation", "FetchBookedInventoryItems", "OWS" })
	public void fetchBookedInventoryItems_39127() {
		String previousValue = "Y";
		try {
			String testname = "fetchBookedInventoryItems_39127";
			WSClient.startTest(testname, "Verify that error message is obatined by not passing reservation id",
					"minimumRegression");

			setOperaHeader();

			// Setting ITEM_INVENTORY Parameter
			WSClient.setData("{var_parameter}", "ITEM_INVENTORY");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue = Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId, resvID;

				// Creating a profile
				if (!(UniqueId = createProfile("DS_01")).equals("error")) {

					// Setting Variables
					System.out.println(UniqueId);
					WSClient.setData("{var_profileId}", UniqueId);

					String resort = OPERALib.getResort();
					WSClient.setData("{var_owsresort}", resort);
					WSClient.setData("{var_resort}", resort);

					setOWSHeader();

					resort = OPERALib.getResort();
					String channel = OWSLib.getChannel();
					WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

					WSClient.setData("{var_resvId}", "");

					// Creating request and processing response for OWS
					// FetchBookedInventoryItems Operation
					String FetchBookedItemsReq = WSClient.createSOAPMessage("OWSFetchBookedInventoryItems", "DS_01");
					String FetchBookedItemsRes = WSClient.processSOAPMessage(FetchBookedItemsReq);

					// Validating response of FetchBookedInventoryItems
					if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
							"FetchBookedInventoryItemsResponse_Result_resultStatusFlag", true)) {
						if (WSAssert.assertIfElementValueEquals(FetchBookedItemsRes,
								"FetchBookedInventoryItemsResponse_Result_resultStatusFlag", "FAIL", false)) {

							if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
									"FetchBookedInventoryItemsResponse_Result_OperaErrorCode", false)) {
								if (WSAssert.assertIfElementValueEquals(FetchBookedItemsRes,
										"FetchBookedInventoryItemsResponse_Result_OperaErrorCode", "BOOKING_NOT_FOUND",
										true)) {
									WSAssert.assertIfElementValueEquals(FetchBookedItemsRes,
											"FetchBookedInventoryItemsResponse_Result_OperaErrorCode",
											"BOOKING_NOT_FOUND", false);
								} else if (WSAssert.assertIfElementContains(FetchBookedItemsRes,
										"FetchBookedInventoryItemsResponse_Result_OperaErrorCode", "BOOKING NOT FOUND",
										false)) {

								}
							}

						} else {
							if (WSAssert.assertIfElementExists(FetchBookedItemsRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(FetchBookedItemsRes,
										"Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The error displayed in the FetchBookedItems response is :" + message);
								if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
										"FetchBookedInventoryItemsResponse_Result_OperaErrorCode", true)) {
									String operaErrorCode = WSAssert.getElementValue(FetchBookedItemsRes,
											"FetchBookedInventoryItemsResponse_Result_OperaErrorCode",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :" + operaErrorCode);

								}
							}
							if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
									"FetchBookedInventoryItemsResponse_Result_GDSError", true)) {
								String message = WSAssert.getElementValue(FetchBookedItemsRes,
										"FetchBookedInventoryItemsResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The gds error displayed in the FetchBookedItems response is :" + message);
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
					}

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		} finally {
			// Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "ITEM_INVENTORY");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : ITEM_INVENTORY");
			}
		}
	}

	// Minimum Regression Test Case :3
	@Test(groups = { "minimumRegression", "Reservation", "FetchBookedInventoryItems", "OWS" })
	public void fetchBookedInventoryItems_39129() {
		String previousValue = "Y";
		try {
			String testname = "fetchBookedInventoryItems_39129";
			WSClient.startTest(testname,
					"Verify that error message is obtained by passing reservation id having no associated inventory items",
					"minimumRegression");

			setOperaHeader();

			// Setting ITEM_INVENTORY Parameter
			WSClient.setData("{var_parameter}", "ITEM_INVENTORY");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue = Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId, resvID;

				// Creating a profile
				if (!(UniqueId = createProfile("DS_01")).equals("error")) {
					// Creating reservation for above profile
					if (!(resvID = createReservation("DS_04")).equals("error")) {
						// Setting Variables
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);
						WSClient.setData("{var_resort}", resort);
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));

						setOWSHeader();

						resort = OPERALib.getResort();
						String channel = OWSLib.getChannel();
						WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

						// Creating request and processing response for OWS
						// FetchBookedInventoryItems Operation
						String FetchBookedItemsReq = WSClient.createSOAPMessage("OWSFetchBookedInventoryItems",
								"DS_02");
						String FetchBookedItemsRes = WSClient.processSOAPMessage(FetchBookedItemsReq);

						// Validating response of FetchBookedInventoryItems
						if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
								"FetchBookedInventoryItemsResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(FetchBookedItemsRes,
									"FetchBookedInventoryItemsResponse_Result_resultStatusFlag", "FAIL", false)) {

								if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
										"FetchBookedInventoryItemsResponse_Result_OperaErrorCode", false)) {
									if (WSAssert.assertIfElementValueEquals(FetchBookedItemsRes,
											"FetchBookedInventoryItemsResponse_Result_OperaErrorCode",
											"NO_ITEMS_FOUND_WITH_RESERVATION", false)) {

									}
								}

							} else {
								if (WSAssert.assertIfElementExists(FetchBookedItemsRes, "Result_Text_TextElement",
										true)) {
									String message = WSAssert.getElementValue(FetchBookedItemsRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"The error displayed in the FetchBookedItems response is :" + message);
									if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
											"FetchBookedInventoryItemsResponse_Result_OperaErrorCode", true)) {
										String operaErrorCode = WSAssert.getElementValue(FetchBookedItemsRes,
												"FetchBookedInventoryItemsResponse_Result_OperaErrorCode",
												XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :" + operaErrorCode);

									}
								}
								if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
										"FetchBookedInventoryItemsResponse_Result_GDSError", true)) {
									String message = WSAssert.getElementValue(FetchBookedItemsRes,
											"FetchBookedInventoryItemsResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"The gds error displayed in the FetchBookedItems response is :" + message);
								}
							}
						} else {
							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
						}

						// Canceling Reservation
						// WSClient.writeToReport(LogStatus.INFO,
						// "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		} finally {
			// Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "ITEM_INVENTORY");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : ITEM_INVENTORY");
			}
		}
	}

	// Minimum Regression Test Case : 4
	@Test(groups = { "minimumRegression", "Reservation", "FetchBookedInventoryItems", "OWS" })
	public void fetchBookedInventoryItems_41644() {
		String previousValue = "Y";
		try {
			String testname = "fetchBookedInventoryItems_41644";
			WSClient.startTest(testname,
					"Verify that inventory items booked to a reservation are fetched correctly by passing confirmation id instead of reservation id",
					"minimumRegression");

			setOperaHeader();

			// Setting ITEM_INVENTORY Parameter
			WSClient.setData("{var_parameter}", "ITEM_INVENTORY");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue = Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId, resvID;

				// Creating a profile
				if (!(UniqueId = createProfile("DS_01")).equals("error")) {
					// Creating reservation for above profile
					if (!(resvID = createReservation("DS_04")).equals("error")) {
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);

						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						// Setting Variables
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);
						WSClient.setData("{var_resort}", resort);
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));

						// Attaching Inventory Items to Reservation
						String ReserveInventoryItemsReq = WSClient.createSOAPMessage("ReserveInventoryItems", "DS_01");
						String ReserveInventoryItemsRes = WSClient.processSOAPMessage(ReserveInventoryItemsReq);

						if (WSAssert.assertIfElementExists(ReserveInventoryItemsRes, "ReserveInventoryItemsRS_Success",
								true)) {

							setOWSHeader();

							resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

							WSClient.setData("{var_confirmationNo}", Confirmation.get("confirmationId"));

							// Creating request and processing response for OWS
							// FetchBookedInventoryItems Operation
							String FetchBookedItemsReq = WSClient.createSOAPMessage("OWSFetchBookedInventoryItems",
									"DS_06");
							String FetchBookedItemsRes = WSClient.processSOAPMessage(FetchBookedItemsReq);

							// Validating response of FetchBookedInventoryItems
							if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
									"FetchBookedInventoryItemsResponse_Result_resultStatusFlag", true)) {
								if (WSAssert.assertIfElementValueEquals(FetchBookedItemsRes,
										"FetchBookedInventoryItemsResponse_Result_resultStatusFlag", "SUCCESS",
										false)) {

									// Database Validation
									LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
									LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
									xPath.put("BookedItemList_InventoryItem_ItemCode",
											"FetchBookedInventoryItemsResponse_BookedItemList_InventoryItem");
									xPath.put("BookedItemList_InventoryItem_ItemName",
											"FetchBookedInventoryItemsResponse_BookedItemList_InventoryItem");
									xPath.put("Description_Text_TextElement",
											"FetchBookedInventoryItemsResponse_BookedItemList_InventoryItem");
									xPath.put("BookedItemList_InventoryItem_SellControl",
											"FetchBookedInventoryItemsResponse_BookedItemList_InventoryItem");
									xPath.put("BookedItemList_InventoryItem_SellSeparate",
											"FetchBookedInventoryItemsResponse_BookedItemList_InventoryItem");
									xPath.put("BookedItemList_InventoryItem_Quantity",
											"FetchBookedInventoryItemsResponse_BookedItemList_InventoryItem");
									actualValues = WSClient.getSingleNodeList(FetchBookedItemsRes, xPath, false,
											XMLType.RESPONSE);
									String query = WSClient.getQuery("QS_01");
									expectedValues = WSClient.getDBRow(query);
									WSAssert.assertEquals(expectedValues, actualValues, false);

								} else {
									if (WSAssert.assertIfElementExists(FetchBookedItemsRes, "Result_Text_TextElement",
											true)) {
										String message = WSAssert.getElementValue(FetchBookedItemsRes,
												"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"The error displayed in the FetchBookedItems response is :" + message);
										if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
												"FetchBookedInventoryItemsResponse_Result_OperaErrorCode", true)) {
											String operaErrorCode = WSAssert.getElementValue(FetchBookedItemsRes,
													"FetchBookedInventoryItemsResponse_Result_OperaErrorCode",
													XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"Opera Error Code :" + operaErrorCode);

										}
									}
									if (WSAssert.assertIfElementExists(FetchBookedItemsRes,
											"FetchBookedInventoryItemsResponse_Result_GDSError", true)) {
										String message = WSAssert.getElementValue(FetchBookedItemsRes,
												"FetchBookedInventoryItemsResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"The gds error displayed in the FetchBookedItems response is :"
														+ message);
									}
								}
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}

							// Canceling Reservation
							// WSClient.writeToReport(LogStatus.INFO,
							// "<b>"+"Cancelling Reservation"+"</b>");
							CancelReservation.cancelReservation("DS_02");
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisites failed!------HoldItemInventory-----Blocked");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		} finally {
			// Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "ITEM_INVENTORY");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : ITEM_INVENTORY");
			}
		}
	}

}
