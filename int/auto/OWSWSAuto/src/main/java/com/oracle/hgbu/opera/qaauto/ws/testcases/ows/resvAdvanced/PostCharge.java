package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CheckoutReservation;
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

public class PostCharge extends WSSetUp {

	public void setOwsHeader() throws SQLException {
		String resort = OPERALib.getResort();
		String channel = OWSLib.getChannel();
		String uname = OPERALib.getUserName();
		String pwd = OPERALib.getPassword();
		String channelType = OWSLib.getChannelType(channel);
		String channelCarrier = OWSLib.getChannelCarier(resort, channel);
		OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	}

	@Test(groups = { "sanity", "PostCharge", "ResvAdvanced", "OWS" })
	public void postCharge_26547() {
		try {
			String testName = "postCharge_26547";
			WSClient.startTest(testName, "Verify if Charges are Posted by payment type transaction code for the given Reservation.", "sanity");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);

			String profileID;
			HashMap<String, String> resvID = new HashMap<>();
			String roomNumber = "";

			// Check if ENV Setup is Complete
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/*************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
					/**************
					 * Prerequisite 2:Create a Reservation
					 **************/

					resvID = CreateReservation.createReservation("DS_12");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID.get("reservationId") + "</b>");
						/***
						 * Prerequisite 3: Fetching available Hotel rooms with
						 * room type
						 ***/

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
						String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

						if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
							roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						} else {
							/*** Prerequisite 4: Creating a room to assign ***/
							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
							String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

							if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
								roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
							}
						}

						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : " + roomNumber + "</b>");
						/*****
						 * Prerequisite 5: Changing the room status to inspected
						 * to assign the room for checking in
						 ****/

						String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
						String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

						if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {
							/**** Prerequisite 6: Assign Room ****/
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room</b>");
							String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
							String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

							if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {
								/*** Prerequisite 7: CheckIn Reservation ***/

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");
								String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {

									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");
									// Reservation is Checked In.
									setOwsHeader();
									String postingDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
									String pricePerUnit = WSClient.getKeywordData("{KEYWORD_RANDNUM_2}");
									System.out.println(postingDate);
									String transCode = OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04");
									WSClient.setData("{var_pricePerUnit}", pricePerUnit);
									WSClient.setData("{var_postDate}", postingDate);
									WSClient.setData("{var_transCode}", transCode);
									WSClient.setData("{var_longInfo}", "LongInfo");
									String postChargeReq = WSClient.createSOAPMessage("OWSPostCharge", "DS_01");
									String postChargeRes = WSClient.processSOAPMessage(postChargeReq);

									// validate the response with DB
									if (WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_ReservationID_UniqueID", resvID.get("reservationId"), false);

										String query = WSClient.getQuery("QS_01");
										LinkedHashMap<String, String> actualValue = WSClient.getDBRow(query);
										LinkedHashMap<String, String> expectedValue = new LinkedHashMap<>();
										expectedValue.put("POSTING_DATE", postingDate);
										expectedValue.put("NET_AMOUNT", pricePerUnit);
										expectedValue.put("REMARK", "LongInfo");
										expectedValue.put("TRX_CODE", transCode);
										WSAssert.assertEquals(expectedValue, actualValue, false);
									}

									if (WSAssert.assertIfElementExists(postChargeRes, "Result_Text_TextElement", true)) {

										String message = WSClient.getElementValue(postChargeRes, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message + "</b>");
									}

									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", true)) {
										String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
									}
									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", true)) {
										String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to checkin reservation");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to assign room");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to change the status of room to vacant and inspected");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a reservation failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a profile failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisites of reservation creation failed ");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// cancel the Reservation.

			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CheckoutReservation.checkOutReservation("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "PostCharge", "ResvAdvanced", "OWS" })
	public void postCharge_26548() {
		try {
			String testName = "postCharge_26548";
			WSClient.startTest(testName, "Verify if negative Charges for adjustment are Posted for the given Reservation.", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);

			String profileID;
			HashMap<String, String> resvID = new HashMap<>();
			String roomNumber = "";

			// Check if ENV Setup is Complete
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/*************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
					/**************
					 * Prerequisite 2:Create a Reservation
					 **************/

					resvID = CreateReservation.createReservation("DS_12");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID.get("reservationId") + "</b>");
						/***
						 * Prerequisite 3: Fetching available Hotel rooms with
						 * room type
						 ***/

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
						String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

						if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
							roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						} else {
							/*** Prerequisite 4: Creating a room to assign ***/
							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
							String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

							if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
								roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
							}
						}

						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : " + roomNumber + "</b>");

						/*****
						 * Prerequisite 5: Changing the room status to inspected
						 * to assign the room for checking in
						 ****/

						String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
						String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

						if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {
							/**** Prerequisite 6: Assign Room ****/
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room</b>");
							String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
							String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

							if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {
								/*** Prerequisite 7: CheckIn Reservation ***/
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");
								String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");
									// Reservation is Checked In.
									setOwsHeader();
									String postingDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
									String pricePerUnit = "-" + WSClient.getKeywordData("{KEYWORD_RANDNUM_2}");
									System.out.println(postingDate);
									String transCode = OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04");
									WSClient.setData("{var_pricePerUnit}", pricePerUnit);
									WSClient.setData("{var_postDate}", postingDate);
									WSClient.setData("{var_transCode}", transCode);
									WSClient.setData("{var_longInfo}", "LongInfo");
									String postChargeReq = WSClient.createSOAPMessage("OWSPostCharge", "DS_01");
									String postChargeRes = WSClient.processSOAPMessage(postChargeReq);

									// validate the response with DB
									if (WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_ReservationID_UniqueID", resvID.get("reservationId"), false);

										String query = WSClient.getQuery("QS_01");
										LinkedHashMap<String, String> actualValue = WSClient.getDBRow(query);
										LinkedHashMap<String, String> expectedValue = new LinkedHashMap<>();
										expectedValue.put("POSTING_DATE", postingDate);
										expectedValue.put("NET_AMOUNT", pricePerUnit);
										expectedValue.put("REMARK", "LongInfo");
										expectedValue.put("TRX_CODE", transCode);
										WSAssert.assertEquals(expectedValue, actualValue, false);
									}

									if (WSAssert.assertIfElementExists(postChargeRes, "Result_Text_TextElement", true)) {

										String message = WSClient.getElementValue(postChargeRes, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message + "</b>");
									}

									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", true)) {
										String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
									}
									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", true)) {
										String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to checkin reservation");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to assign room");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to change the status of room to vacant and inspected");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a reservation failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a profile failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisites of reservation creation failed ");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// cancel the Reservation.
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CheckoutReservation.checkOutReservation("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// before Charges are added when guest is not checked in

	@Test(groups = { "minimumRegression", "PostCharge", "ResvAdvanced", "OWS" })
	public void postCharge_34562() {
		try {
			String testName = "postCharge_34562";
			WSClient.startTest(testName, "Verify if Charges are Posted for the given Reservation when PRE_STAY_CHARGES='Y'.", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				WSClient.setData("{var_parameter}", "PRE_STAY_CHARGES");
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if PRE_STAY_CHARGES=Y</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing PRE_STAY_CHARGES to Y</b>");
					Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				String profileId = CreateProfile.createProfile("DS_06");
				if (profileId != "") {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId + "</b>");
					HashMap<String, String> resvIds = CreateReservation.createReservation("DS_01");
					String resvId = resvIds.get("reservationId");
					System.out.println("ReservationId" + resvId);
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvId + "</b>");
					WSClient.setData("{var_resvId}", resvId);
					if (resvId != "") {
						// Set Data for PostCharge Operation
						String postingDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
						String pricePerUnit = WSClient.getKeywordData("{KEYWORD_RANDNUM_2}");
						System.out.println(postingDate);
						String transCode = OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04");
						WSClient.setData("{var_pricePerUnit}", pricePerUnit);
						WSClient.setData("{var_postDate}", postingDate);
						WSClient.setData("{var_transCode}", transCode);
						WSClient.setData("{var_longInfo}", "LongInfo");
						setOwsHeader();

						String postChargeReq = WSClient.createSOAPMessage("OWSPostCharge", "DS_01");
						String postChargeRes = WSClient.processSOAPMessage(postChargeReq);

						if (WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_ReservationID_UniqueID", resvId, false);

							String query = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> actualValue = WSClient.getDBRow(query);
							LinkedHashMap<String, String> expectedValue = new LinkedHashMap<>();
							expectedValue.put("POSTING_DATE", postingDate);
							expectedValue.put("NET_AMOUNT", pricePerUnit);
							expectedValue.put("REMARK", "LongInfo");
							expectedValue.put("TRX_CODE", transCode);
							WSAssert.assertEquals(expectedValue, actualValue, false);
						}

						if (WSAssert.assertIfElementExists(postChargeRes, "Result_Text_TextElement", true)) {

							String message = WSClient.getElementValue(postChargeRes, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", true)) {
							String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
						}
						if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", true)) {
							String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * Verify that the Charges are posted to the reservation checked-OUT Needs
	 * parameter CheckOut to be enabled to postCharges
	 *
	 */
	@Test(groups = { "minimumRegression", "PostCharge", "ResvAdvanced", "OWS", "testfailows1" })
	public void postCharge_36272() {
		try {
			String testName = "postCharge_36272";
			WSClient.startTest(testName, "Verify if Charges are Posted for the given Reservation when POST_STAY_CHARGES=Y.", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);

			String profileID;
			HashMap<String, String> resvID = new HashMap<>();
			String roomNumber = "";

			// Check if ENV Setup is Complete
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				WSClient.setData("{var_parameter}", "POST_STAY_CHARGES");
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if POST_STAY_CHARGES=Y</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing POST_STAY_CHARGES to Y</b>");
					Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/*************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					/**************
					 * Prerequisite 2:Create a Reservation
					 **************/
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
					resvID = CreateReservation.createReservation("DS_12");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID.get("reservationId") + "</b>");
						/***
						 * Prerequisite 3: Fetching available Hotel rooms with
						 * room type
						 ***/

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
						String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

						if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
							roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						} else {
							/*** Prerequisite 4: Creating a room to assign ***/
							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
							String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

							if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
								roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
							}
						}

						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : " + roomNumber + "</b>");

						/*****
						 * Prerequisite 5: Changing the room status to inspected
						 * to assign the room for checking in
						 ****/

						String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
						String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

						if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {
							/**** Prerequisite 6: Assign Room ****/
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room</b>");

							String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
							String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

							if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {
								/*** Prerequisite 7: CheckIn Reservation ***/
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");

								String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");

									if (CheckoutReservation.checkOutReservation("DS_01")) {
										WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked out the reservation</b>");

										setOwsHeader();
										String postingDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
										String pricePerUnit = WSClient.getKeywordData("{KEYWORD_RANDNUM_2}");
										System.out.println(postingDate);
										String transCode = OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04");
										WSClient.setData("{var_pricePerUnit}", pricePerUnit);
										WSClient.setData("{var_postDate}", postingDate);
										WSClient.setData("{var_transCode}", transCode);
										WSClient.setData("{var_longInfo}", "LongInfo");
										String postChargeReq = WSClient.createSOAPMessage("OWSPostCharge", "DS_01");
										String postChargeRes = WSClient.processSOAPMessage(postChargeReq);

										// validate the response with DB
										if (WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_ReservationID_UniqueID", resvID.get("reservationId"), false);

											String query = WSClient.getQuery("QS_02");
											LinkedHashMap<String, String> actualValue = WSClient.getDBRow(query);
											LinkedHashMap<String, String> expectedValue = new LinkedHashMap<>();
											expectedValue.put("POSTING_DATE", postingDate);
											expectedValue.put("NET_AMOUNT", pricePerUnit);
											expectedValue.put("REMARK", "LongInfo");
											expectedValue.put("TRX_CODE", transCode);
											WSAssert.assertEquals(expectedValue, actualValue, false);
										}
										if (WSAssert.assertIfElementExists(postChargeRes, "Result_Text_TextElement", true)) {

											String message = WSClient.getElementValue(postChargeRes, "Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message + "</b>");
										}

										if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", true)) {
											String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
										}
										if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", true)) {
											String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to checkout reservation");
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to checkin reservation");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to assign room");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to change the status of room to vacant and inspected");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a reservation failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a profile failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisites of reservation creation failed ");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// cancel the Reservation.
		}

	}

	/*
	 * verify that the charges are posted to the Cancelled Reservation.
	 */
	@Test(groups = { "minimumRegression", "PostCharge", "ResvAdvanced", "OWS" })
	public void postCharge_36273() {
		try {
			String testName = "postCharge_36273";
			WSClient.startTest(testName, "Verify if Charges are Posted for the cancelled Reservation when NOSHOW_AND_CANCELLATION_POSTINGS=Y.", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);

			String profileID;
			HashMap<String, String> resvID = new HashMap<>();
			String roomNumber = "";

			// Check if ENV Setup is Complete
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				WSClient.setData("{var_parameter}", "NOSHOW_AND_CANCELLATION_POSTINGS");
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if NOSHOW_AND_CANCELLATION_POSTINGS=Y</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing NOSHOW_AND_CANCELLATION_POSTINGS to Y</b>");
					Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/*************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<bProfile ID : " + profileID + "</b>");

					/**************
					 * Prerequisite 2:Create a Reservation
					 **************/

					resvID = CreateReservation.createReservation("DS_12");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.setData("{var_reservation_id}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID.get("reservationId") + "</b>");

						// Cancel the reservation
						if (CancelReservation.cancelReservation("DS_01")) {
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully cancelled the reservation</b>");

							String postingDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
							String pricePerUnit = WSClient.getKeywordData("{KEYWORD_RANDNUM_2}");
							System.out.println(postingDate);
							String transCode = OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04");
							WSClient.setData("{var_pricePerUnit}", pricePerUnit);
							WSClient.setData("{var_postDate}", postingDate);
							WSClient.setData("{var_transCode}", transCode);
							WSClient.setData("{var_longInfo}", "LongInfo");
							setOwsHeader();

							String postChargeReq = WSClient.createSOAPMessage("OWSPostCharge", "DS_01");
							String postChargeRes = WSClient.processSOAPMessage(postChargeReq);

							if (WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_ReservationID_UniqueID", resvID.get("reservationId"), false);

								String query = WSClient.getQuery("QS_01");
								LinkedHashMap<String, String> actualValue = WSClient.getDBRow(query);
								LinkedHashMap<String, String> expectedValue = new LinkedHashMap<>();
								expectedValue.put("POSTING_DATE", postingDate);
								expectedValue.put("NET_AMOUNT", pricePerUnit);
								expectedValue.put("REMARK", "LongInfo");
								expectedValue.put("TRX_CODE", transCode);
								WSAssert.assertEquals(expectedValue, actualValue, false);
							}

							if (WSAssert.assertIfElementExists(postChargeRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(postChargeRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", true)) {
								String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
							}
							if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", true)) {
								String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
							}

						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a reservation failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a profile failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisites of reservation creation failed ");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// cancel the Reservation.
		}

	}

	/*
	 * Verify Post Charge with Revenue Type Transaction Code
	 *
	 */
	@Test(groups = { "minimumRegression", "PostCharge", "ResvAdvanced", "OWS" })
	public void postCharge_36274() {
		try {
			String testName = "postCharge_36274";
			WSClient.startTest(testName, "Verify if Charges are Posted by revenue type transaction code for the given Reservation.", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String channel = OWSLib.getChannel();
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				String profileId = CreateProfile.createProfile("DS_06");
				if (profileId != "") {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId + "</b>");
					HashMap<String, String> resvIds = CreateReservation.createReservation("DS_01");
					String resvId = resvIds.get("reservationId");
					System.out.println("ReservationId" + resvId);
					WSClient.setData("{var_resvId}", resvId);

					if (resvId != "") {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvId + "</b>");
						// Set Data for PostCharge Operation
						String postingDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
						String pricePerUnit = WSClient.getKeywordData("{KEYWORD_RANDNUM_2}");
						System.out.println(postingDate);
						String transCode = OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03");
						WSClient.setData("{var_pricePerUnit}", pricePerUnit);
						WSClient.setData("{var_postDate}", postingDate);
						WSClient.setData("{var_transCode}", transCode);
						WSClient.setData("{var_longInfo}", "LongInfo");
						setOwsHeader();

						String postChargeReq = WSClient.createSOAPMessage("OWSPostCharge", "DS_01");
						String postChargeRes = WSClient.processSOAPMessage(postChargeReq);

						if (WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_ReservationID_UniqueID", resvId, false);

							String query = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> actualValue = WSClient.getDBRow(query);
							LinkedHashMap<String, String> expectedValue = new LinkedHashMap<>();
							expectedValue.put("POSTING_DATE", postingDate);
							expectedValue.put("NET_AMOUNT", pricePerUnit);
							expectedValue.put("REMARK", "LongInfo");
							expectedValue.put("TRX_CODE", transCode);
							WSAssert.assertEquals(expectedValue, actualValue, false);
						}

						if (WSAssert.assertIfElementExists(postChargeRes, "Result_Text_TextElement", true)) {

							String message = WSClient.getElementValue(postChargeRes, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", true)) {
							String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
						}
						if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", true)) {
							String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

		finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/*
	 * Verify that the Charges are posted using Wrapper type transaction Code
	 */
	@Test(groups = { "minimumRegression", "PostCharge", "ResvAdvanced", "OWS" })
	public void postCharge_36275() {
		try {
			String testName = "postCharge_36275";
			WSClient.startTest(testName, "Verify if Charges are not Posted by wrapper type transaction code for the given Reservation.", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String channel = OWSLib.getChannel();
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				String profileId = CreateProfile.createProfile("DS_06");
				if (profileId != "") {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId + "</b>");
					HashMap<String, String> resvIds = CreateReservation.createReservation("DS_01");
					String resvId = resvIds.get("reservationId");
					System.out.println("ReservationId" + resvId);
					WSClient.setData("{var_resvId}", resvId);
					if (resvId != "") {
						// Set Data for PostCharge Operation
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvId + "</b>");
						String postingDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
						String pricePerUnit = WSClient.getKeywordData("{KEYWORD_RANDNUM_2}");
						System.out.println(postingDate);
						String transCode = OperaPropConfig.getDataSetForCode("TransactionCode", "DS_01");
						WSClient.setData("{var_pricePerUnit}", pricePerUnit);
						WSClient.setData("{var_postDate}", postingDate);
						WSClient.setData("{var_transCode}", transCode);
						WSClient.setData("{var_longInfo}", "LongInfo");
						setOwsHeader();

						String postChargeReq = WSClient.createSOAPMessage("OWSPostCharge", "DS_01");
						String postChargeRes = WSClient.processSOAPMessage(postChargeReq);

						if (WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_Result_resultStatusFlag", "FAIL", false)) {

						}
						if (WSAssert.assertIfElementExists(postChargeRes, "Result_Text_TextElement", true)) {

							String message = WSClient.getElementValue(postChargeRes, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", true)) {
							String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
						}
						if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", true)) {
							String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
						}

					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * verify that the Transaction are posted using Non-Manual Transaction Codes
	 */

	/*
	 * Verify that charges are posted Using Adjustment Transaction Codes
	 */

	/*
	 * Verify Other curreinces are allowed to post charges.
	 */

	@Test(groups = { "minimumRegression", "PostCharge", "ResvAdvanced", "OWS" })
	/*
	 * Verify that error message is populated on the response when invalid
	 * 'Transaction Code' is not sent on the request.
	 */
	public void postCharge_36294() {
		try {
			String testName = "postCharge_36294";
			WSClient.startTest(testName, "Verify that error message is populated on the response when invalid 'Transaction Code' is not sent on the request.", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);

			String profileID;
			HashMap<String, String> resvID = new HashMap<>();
			String roomNumber = "";

			// Check if ENV Setup is Complete
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/*************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					/**************
					 * Prerequisite 2:Create a Reservation
					 **************/

					resvID = CreateReservation.createReservation("DS_12");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID.get("reservationId") + "</b>");
						/***
						 * Prerequisite 3: Fetching available Hotel rooms with
						 * room type
						 ***/

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
						String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

						if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
							roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						} else {
							/*** Prerequisite 4: Creating a room to assign ***/
							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
							String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

							if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
								roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
							}
						}

						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : " + roomNumber + "</b>");
						/*****
						 * Prerequisite 5: Changing the room status to inspected
						 * to assign the room for checking in
						 ****/

						String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
						String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

						if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {
							/**** Prerequisite 6: Assign Room ****/
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room</b>");
							String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
							String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

							if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {
								/*** Prerequisite 7: CheckIn Reservation ***/

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");
								String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {

									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");
									// Reservation is Checked In.
									setOwsHeader();
									String postingDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
									String pricePerUnit = WSClient.getKeywordData("{KEYWORD_RANDNUM_2}");
									System.out.println(postingDate);

									WSClient.setData("{var_pricePerUnit}", pricePerUnit);
									WSClient.setData("{var_postDate}", postingDate);
									String transCode = OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04") + "X";
									WSClient.setData("{var_transCode}", transCode);
									WSClient.setData("{var_longInfo}", "LongInfo");
									String postChargeReq = WSClient.createSOAPMessage("OWSPostCharge", "DS_01");
									String postChargeRes = WSClient.processSOAPMessage(postChargeReq);

									// validate the response with DB
									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_resultStatusFlag", false)) {
										if (WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_Result_resultStatusFlag", "FAIL", false)) {

										}
									}
									if (WSAssert.assertIfElementExists(postChargeRes, "Result_Text_TextElement", true)) {

										String message = WSClient.getElementValue(postChargeRes, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message + "</b>");
									}

									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", true)) {
										String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
									}
									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", true)) {
										String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to checkin reservation");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to assign room");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to change the status of room to vacant and inspected");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a reservation failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a profile failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisites of reservation creation failed ");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// cancel the Reservation.

			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CheckoutReservation.checkOutReservation("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "PostCharge", "ResvAdvanced", "OWS" })
	/*
	 * Verify that error message is populated on the response when inactive
	 * 'Article Code' is sent on the request.
	 */
	public void postCharge_36295() {
		try {
			String testName = "postCharge_36295";
			WSClient.startTest(testName, "Verify that error message is populated on the response when Inactive Article Code is sent on the request.", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);

			String profileID;
			HashMap<String, String> resvID = new HashMap<>();
			String roomNumber = "";

			// Check if ENV Setup is Complete
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ArticleCode" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/*************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
					/**************
					 * Prerequisite 2:Create a Reservation
					 **************/

					resvID = CreateReservation.createReservation("DS_12");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID.get("reservationId") + "</b>");
						/***
						 * Prerequisite 3: Fetching available Hotel rooms with
						 * room type
						 ***/

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
						String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

						if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
							roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						} else {
							/*** Prerequisite 4: Creating a room to assign ***/
							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
							String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

							if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
								roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
							}
						}

						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : " + roomNumber + "</b>");
						/*****
						 * Prerequisite 5: Changing the room status to inspected
						 * to assign the room for checking in
						 ****/

						String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
						String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

						if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {
							/**** Prerequisite 6: Assign Room ****/
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room</b>");
							String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
							String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

							if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

								/*** Prerequisite 7: CheckIn Reservation ***/

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");
								String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {

									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");
									// Reservation is Checked In.
									setOwsHeader();
									String postingDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
									String pricePerUnit = WSClient.getKeywordData("{KEYWORD_RANDNUM_2}");
									System.out.println(postingDate);

									WSClient.setData("{var_pricePerUnit}", pricePerUnit);
									WSClient.setData("{var_postDate}", postingDate);
									Integer value = (int) Float.parseFloat(OperaPropConfig.getDataSetForCode("ArticleCode", "DS_02"));
									WSClient.setData("{var_articleCode}", value.toString());
									String transCode = OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04");
									WSClient.setData("{var_transCode}", transCode);

									WSClient.setData("{var_longInfo}", "LongInfo");
									String postChargeReq = WSClient.createSOAPMessage("OWSPostCharge", "DS_03");
									String postChargeRes = WSClient.processSOAPMessage(postChargeReq);

									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_resultStatusFlag", false))
										if (WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_Result_resultStatusFlag", "FAIL", false)) {

										}

									if (WSAssert.assertIfElementExists(postChargeRes, "Result_Text_TextElement", true)) {

										String message = WSClient.getElementValue(postChargeRes, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message + "</b>");
									}

									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", true)) {
										String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
									}
									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", true)) {
										String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to checkin reservation");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to assign room");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to change the status of room to vacant and inspected");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a reservation failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a profile failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisites of reservation creation failed ");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// cancel the Reservation.

			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CheckoutReservation.checkOutReservation("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "PostCharge", "ResvAdvanced", "OWS" })
	/*
	 * Verify that charges are posted when article number is sent on the
	 * request.
	 */
	public void postCharge_36296() {
		try {
			String testName = "postCharge_36296";
			WSClient.startTest(testName, "Verify that charges are posted when article number is sent on the request.", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);

			String profileID;
			HashMap<String, String> resvID = new HashMap<>();
			String roomNumber = "";

			// Check if ENV Setup is Complete
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ArticleCode", "TransactionCode" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/*************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					/**************
					 * Prerequisite 2:Create a Reservation
					 **************/

					resvID = CreateReservation.createReservation("DS_12");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID.get("reservationId") + "</b>");

						/***
						 * Prerequisite 3: Fetching available Hotel rooms with
						 * room type
						 ***/

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
						String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

						if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
							roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						} else {
							/*** Prerequisite 4: Creating a room to assign ***/

							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
							String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

							if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
								roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
							}
						}

						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : " + roomNumber + "</b>");
						/*****
						 * Prerequisite 5: Changing the room status to inspected
						 * to assign the room for checking in
						 ****/

						String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
						String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

						if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {
							/**** Prerequisite 6: Assign Room ****/
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room</b>");
							String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
							String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

							if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {
								/*** Prerequisite 7: CheckIn Reservation ***/

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");
								String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {

									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");
									// Reservation is Checked In.
									setOwsHeader();
									String postingDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
									String pricePerUnit = WSClient.getKeywordData("{KEYWORD_RANDNUM_2}");
									System.out.println(postingDate);
									WSClient.setData("{var_pricePerUnit}", pricePerUnit);
									WSClient.setData("{var_postDate}", postingDate);
									Integer value = (int) Float.parseFloat(OperaPropConfig.getDataSetForCode("ArticleCode", "DS_01"));
									WSClient.setData("{var_articleCode}", value.toString());
									String transCode = OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04");
									WSClient.setData("{var_transCode}", transCode);
									WSClient.setData("{var_longInfo}", "LongInfo");
									String postChargeReq = WSClient.createSOAPMessage("OWSPostCharge", "DS_03");
									String postChargeRes = WSClient.processSOAPMessage(postChargeReq);

									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_resultStatusFlag", false))
										if (WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_Result_resultStatusFlag", "SUCCESS", false)) {

											String query1 = WSClient.getQuery("QS_04");
											String articleId = WSClient.getDBRow(query1).get("ARTICLE_ID");
											String articleTrans = WSClient.getDBRow(query1).get("TRX_CODE");
											WSClient.setData("{var_transCode}", articleTrans);

											String query = WSClient.getQuery("QS_03");
											LinkedHashMap<String, String> actualValue = WSClient.getDBRow(query);
											LinkedHashMap<String, String> expectedValue = new LinkedHashMap<>();
											expectedValue.put("POSTING_DATE", postingDate);
											expectedValue.put("NET_AMOUNT", pricePerUnit);
											expectedValue.put("REMARK", "LongInfo");
											expectedValue.put("TRX_CODE", transCode);
											expectedValue.put("ARTICLE_ID", articleId);
											WSAssert.assertEquals(expectedValue, actualValue, false);

										}

									if (WSAssert.assertIfElementExists(postChargeRes, "Result_Text_TextElement", true)) {

										String message = WSClient.getElementValue(postChargeRes, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message + "</b>");
									}

									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", true)) {
										String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
									}
									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", true)) {
										String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to checkin reservation");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to assign room");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to change the status of room to vacant and inspected");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a reservation failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a profile failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisites of reservation creation failed ");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// cancel the Reservation.

			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CheckoutReservation.checkOutReservation("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "PostCharge", "ResvAdvanced", "OWS" })
	/*
	 * Verify that charges are posted when transaction code with tax is sent on
	 * the request.
	 */
	public void postCharge_36297() {
		try {
			String testName = "postCharge_36297";
			WSClient.startTest(testName, "Verify that charges are posted when transaction code with tax is sent on the request.", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);

			String profileID;
			HashMap<String, String> resvID = new HashMap<>();
			String roomNumber = "";

			// Check if ENV Setup is Complete
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "TransactionCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/*************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					/**************
					 * Prerequisite 2:Create a Reservation
					 **************/

					resvID = CreateReservation.createReservation("DS_12");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID.get("reservationId") + "</b>");

						/***
						 * Prerequisite 3: Fetching available Hotel rooms with
						 * room type
						 ***/

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
						String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

						if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
							roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						} else {
							/*** Prerequisite 4: Creating a room to assign ***/

							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
							String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

							if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
								roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
							}
						}

						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : " + roomNumber + "</b>");
						/*****
						 * Prerequisite 5: Changing the room status to inspected
						 * to assign the room for checking in
						 ****/

						String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
						String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

						if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {

							/**** Prerequisite 6: Assign Room ****/

							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room</b>");
							String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
							String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

							if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

								/*** Prerequisite 7: CheckIn Reservation ***/

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");
								String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {

									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");
									// Reservation is Checked In.
									setOwsHeader();
									String postingDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
									String pricePerUnit = WSClient.getKeywordData("{KEYWORD_RANDNUM_2}");
									System.out.println(postingDate);
									WSClient.setData("{var_pricePerUnit}", pricePerUnit);
									WSClient.setData("{var_postDate}", postingDate);
									//									Integer value = 456;
									//									// Integer value = (int)
									//									// Float.parseFloat(OperaPropConfig.getDataSetForCode("ArticleCode",
									//									// "DS_01"));
									//									WSClient.setData("{var_articleCode}", value.toString());
									String transCode = OperaPropConfig.getDataSetForCode("TransactionCode", "DS_06");
									WSClient.setData("{var_transCode}", transCode);
									WSClient.setData("{var_longInfo}", "LongInfo");
									String postChargeReq = WSClient.createSOAPMessage("OWSPostCharge", "DS_01");
									String postChargeRes = WSClient.processSOAPMessage(postChargeReq);

									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_resultStatusFlag", false))
										if (WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_Result_resultStatusFlag", "SUCCESS", false)) {


											String query = WSClient.getQuery("QS_03");
											LinkedHashMap<String, String> actualValue = WSClient.getDBRow(query);
											LinkedHashMap<String, String> expectedValue = new LinkedHashMap<>();

											/**** Validation ********/

											expectedValue.put("POSTING_DATE", postingDate);
											expectedValue.put("NET_AMOUNT", pricePerUnit);
											expectedValue.put("REMARK", "LongInfo");
											expectedValue.put("TRX_CODE", transCode);

											WSAssert.assertEquals(expectedValue, actualValue, false);

											String queryTrans = WSClient.getQuery("QS_06");
											String transCode1 = WSClient.getDBRow(queryTrans).get("TRX_CODE");
											String percent = WSClient.getDBRow(queryTrans).get("PERCENTAGE");
											WSClient.setData("{var_transCode}", transCode1);
											String query2 = WSClient.getQuery("QS_05");
											LinkedHashMap<String, String> actualValue1 = WSClient.getDBRow(query2);
											LinkedHashMap<String, String> expectedValue1 = new LinkedHashMap<>();

											/******
											 * Calculating charges on tax
											 ****/
											String tax;
											Double a1 = 0.0;
											if (percent != null) {
												Double des1 = (Double.parseDouble(percent) * Double.parseDouble(pricePerUnit));
												a1 = des1 / 100;
												tax = a1.toString();
												System.out.println(a1);
											} else {
												tax = null;
											}
											if (a1 == Math.floor(a1)) {
												Integer p = (int) Math.round(a1);
												tax = p.toString();
												System.out.println(tax);
											}

											/**** Validation ********/

											WSClient.writeToReport(LogStatus.INFO, "Validation of Tax Charge Postings");
											expectedValue1.put("POSTING_DATE", postingDate);
											expectedValue1.put("NET_AMOUNT", tax);
											expectedValue1.put("TRX_CODE", transCode1);


											WSAssert.assertEquals(expectedValue1, actualValue1, false);

										}

									if (WSAssert.assertIfElementExists(postChargeRes, "Result_Text_TextElement", true)) {

										String message = WSClient.getElementValue(postChargeRes, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message + "</b>");
									}

									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", true)) {
										String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
									}
									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", true)) {
										String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to checkin reservation");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to assign room");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to change the status of room to vacant and inspected");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a reservation failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a profile failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisites of reservation creation failed ");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// cancel the Reservation.

			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CheckoutReservation.checkOutReservation("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// @Test(groups = { "minimumRegression", "PostCharge", "ResvAdvanced", "OWS"
	// })
	// /*
	// * Verify that error message is populated on the response when
	// 'Transaction
	// * Code' is not sent on the request.
	// */
	// public void postCharge_14() {
	// try {
	// String testName = "postCharge_14";
	// WSClient.startTest(testName, "Verify that error message is populated on
	// the response when 'Transaction Code' is not sent on the request.",
	// "minimumRegression");
	// String resortOperaValue = OPERALib.getResort();
	// String chain = OPERALib.getChain();
	// String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
	// String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
	// String uname = OPERALib.getUserName();
	// String pwd = OPERALib.getPassword();
	// String channel = OWSLib.getChannel();
	// String channelType = OWSLib.getChannelType(channel);
	// String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsresort}", resort);
	// String channelCarrier = OWSLib.getChannelCarier(resort, channel);
	// OPERALib.setOperaHeader(uname);
	//
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_chain}", chain);
	// WSClient.setData("{var_fname}", fname);
	// WSClient.setData("{var_lname}", lname);
	// WSClient.setData("{var_chainCode}", chain);
	//
	// String profileID;
	// HashMap<String, String> resvID = new HashMap<>();
	// String roomNumber = "";
	//
	// // Check if ENV Setup is Complete
	// if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode",
	// "RoomType", "SourceCode", "MarketCode" })) {
	//
	// /*************
	// * Prerequisite : Room type, Rate Plan Code, Source Code, Market
	// * Code
	// *********************************/
	//
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
	// WSClient.setData("{var_roomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
	// WSClient.setData("{var_payment}",
	// OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	//
	// /*************
	// * Prerequisite 1: Create profile
	// *********************************/
	//
	// profileID = CreateProfile.createProfile("DS_01");
	// if (!profileID.equals("error")) {
	// WSClient.setData("{var_profileId}", profileID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID +
	// "</b>");
	// /**************
	// * Prerequisite 2:Create a Reservation
	// **************/
	//
	// resvID = CreateReservation.createReservation("DS_12");
	// if (!resvID.get("reservationId").equals("error")) {
	// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " +
	// resvID.get("reservationId") + "</b>");
	// /***
	// * Prerequisite 3: Fetching available Hotel rooms with
	// * room type
	// ***/
	//
	// String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms",
	// "DS_03");
	// String fetchHotelRoomsRes =
	// WSClient.processSOAPMessage(fetchHotelRoomsReq);
	//
	// if (WSAssert.assertIfElementExists(fetchHotelRoomsRes,
	// "FetchHotelRoomsRS_Success", true) &&
	// WSAssert.assertIfElementExists(fetchHotelRoomsRes,
	// "FetchHotelRoomsRS_HotelRooms_Room", true)) {
	// roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
	// "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
	// } else {
	// /*** Prerequisite 4: Creating a room to assign ***/
	// String createRoomReq = WSClient.createSOAPMessage("CreateRoom",
	// "RoomMaint");
	// String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
	//
	// if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success",
	// true)) {
	// roomNumber = WSClient.getElementValue(createRoomReq,
	// "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create
	// room");
	// }
	// }
	//
	// WSClient.setData("{var_roomNumber}", roomNumber);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room
	// -> Room number : " + roomNumber + "</b>");
	// /*****
	// * Prerequisite 5: Changing the room status to inspected
	// * to assign the room for checking in
	// ****/
	//
	// String setHousekeepingRoomStatusReq =
	// WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
	// String setHousekeepingRoomStatusRes =
	// WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);
	//
	// if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
	// "SetHousekeepingRoomStatusRS_Success", true)) {
	// /**** Prerequisite 6: Assign Room ****/
	// WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the
	// status of room</b>");
	// String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
	// String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);
	//
	// if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
	// true)) {
	// /*** Prerequisite 7: CheckIn Reservation ***/
	//
	// WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the
	// room</b>");
	// String checkInReq = WSClient.createSOAPMessage("CheckinReservation",
	// "DS_01");
	// String checkInRes = WSClient.processSOAPMessage(checkInReq);
	//
	// if (WSAssert.assertIfElementExists(checkInRes,
	// "CheckinReservationRS_Success", true)) {
	//
	// WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the
	// reservation</b>");
	// // Reservation is Checked In.
	// setOwsHeader();
	// String postingDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
	// String pricePerUnit = WSClient.getKeywordData("{KEYWORD_RANDNUM_2}");
	// System.out.println(postingDate);
	//
	// WSClient.setData("{var_pricePerUnit}", pricePerUnit);
	// WSClient.setData("{var_postDate}", postingDate);
	// String transCode = OperaPropConfig.getDataSetForCode("TransactionCode",
	// "DS_04");
	// WSClient.setData("{var_transCode}", transCode);
	// WSClient.setData("{var_longInfo}", "LongInfo");
	// String postChargeReq = WSClient.createSOAPMessage("OWSPostCharge",
	// "DS_02");
	// String postChargeRes = WSClient.processSOAPMessage(postChargeReq);
	//
	// // validate the response with DB
	// if (WSAssert.assertIfElementValueEquals(postChargeRes,
	// "PostChargeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
	// WSAssert.assertIfElementValueEquals(postChargeRes,
	// "PostChargeResponse_ReservationID_UniqueID", resvID.get("reservationId"),
	// false);
	// String query = WSClient.getQuery("QS_01");
	// LinkedHashMap<String, String> actualValue = WSClient.getDBRow(query);
	// LinkedHashMap<String, String> expectedValue = new LinkedHashMap<>();
	// expectedValue.put("POSTING_DATE", postingDate);
	// expectedValue.put("NET_AMOUNT", pricePerUnit);
	// expectedValue.put("REMARK", "LongInfo");
	// WSAssert.assertEquals(expectedValue, actualValue, false);
	// }
	//
	// if (WSAssert.assertIfElementExists(postChargeRes,
	// "Result_Text_TextElement", true)) {
	//
	// String message = WSClient.getElementValue(postChargeRes,
	// "Result_Text_TextElement", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the
	// response is :" + message + "</b>");
	// }
	//
	// if (WSAssert.assertIfElementExists(postChargeRes,
	// "PostChargeResponse_Result_OperaErrorCode", true)) {
	// String message = WSAssert.getElementValue(postChargeRes,
	// "PostChargeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed
	// :---</b> " + message);
	// }
	// if (WSAssert.assertIfElementExists(postChargeRes,
	// "PostChargeResponse_Result_GDSErrorCode", true)) {
	// String message = WSAssert.getElementValue(postChargeRes,
	// "PostChargeResponse_Result_GDSErrorCode", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed
	// :---</b> " + message);
	// }
	//
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Unable
	// to checkin reservation");
	// }
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Unable
	// to assign room");
	// }
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Unable
	// to change the status of room to vacant and inspected");
	// }
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>
	// Creating a reservation failed");
	// }
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>
	// Creating a profile failed");
	// }
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Prerequisites of reservation
	// creation failed ");
	// }
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// // cancel the Reservation.
	//
	// try {
	// if (!WSClient.getData("{var_resvId}").equals(""))
	// CheckoutReservation.checkOutReservation("DS_01");
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// }

	@Test(groups = { "minimumRegression", "PostCharge", "ResvAdvanced", "OWS" })
	/*
	 * Verify that error message is populated on the response when 'Transaction
	 * Code' and 'Article Number' is sent is not sent on the request.
	 */
	public void postCharge_36298() {
		try {
			String testName = "postCharge_36298";
			WSClient.startTest(testName, "Verify that error message is populated on the response when 'Transaction Code' is not sent and 'Article Number' is sent on the request.", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);

			String profileID;
			HashMap<String, String> resvID = new HashMap<>();
			String roomNumber = "";

			// Check if ENV Setup is Complete
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ArticleCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/*************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
					/**************
					 * Prerequisite 2:Create a Reservation
					 **************/

					resvID = CreateReservation.createReservation("DS_12");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID.get("reservationId") + "</b>");
						/***
						 * Prerequisite 3: Fetching available Hotel rooms with
						 * room type
						 ***/

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
						String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

						if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
							roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						} else {
							/*** Prerequisite 4: Creating a room to assign ***/
							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
							String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

							if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
								roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
							}
						}

						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : " + roomNumber + "</b>");
						/*****
						 * Prerequisite 5: Changing the room status to inspected
						 * to assign the room for checking in
						 ****/

						String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
						String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

						if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {
							/**** Prerequisite 6: Assign Room ****/
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room</b>");
							String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
							String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

							if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {
								/*** Prerequisite 7: CheckIn Reservation ***/

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");
								String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {

									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");
									// Reservation is Checked In.
									setOwsHeader();
									String postingDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
									String pricePerUnit = WSClient.getKeywordData("{KEYWORD_RANDNUM_2}");
									System.out.println(postingDate);

									WSClient.setData("{var_pricePerUnit}", pricePerUnit);
									WSClient.setData("{var_postDate}", postingDate);
									WSClient.setData("{var_longInfo}", "LongInfo");
									Integer value = (int) Float.parseFloat(OperaPropConfig.getDataSetForCode("ArticleCode", "DS_02"));
									WSClient.setData("{var_articleCode}", value.toString());
									String postChargeReq = WSClient.createSOAPMessage("OWSPostCharge", "DS_04");
									String postChargeRes = WSClient.processSOAPMessage(postChargeReq);

									// validate the response with DB
									if (WSAssert.assertIfElementValueEquals(postChargeRes, "PostChargeResponse_Result_resultStatusFlag", "FAIL", false)) {

									}

									if (WSAssert.assertIfElementExists(postChargeRes, "Result_Text_TextElement", true)) {

										String message = WSClient.getElementValue(postChargeRes, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message + "</b>");
									}

									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", true)) {
										String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The OPERA error displayed :---</b> " + message);
									}
									if (WSAssert.assertIfElementExists(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", true)) {
										String message = WSAssert.getElementValue(postChargeRes, "PostChargeResponse_Result_GDSErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The GDS error displayed :---</b> " + message);
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to checkin reservation");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to assign room");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to change the status of room to vacant and inspected");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a reservation failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a profile failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisites of reservation creation failed ");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// cancel the Reservation.

			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CheckoutReservation.checkOutReservation("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
