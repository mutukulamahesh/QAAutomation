package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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

public class CancelBooking extends WSSetUp {

	String Parameter;

	// Creating Room to assign
	public String createRoom(String ds) {
		String roomNumber = "";
		try {
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Creating a Room to assign to the reservation" + "</b>");
			String createRoomReq = WSClient.createSOAPMessage("CreateRoom", ds);
			String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
			if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

				roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber",
						XMLType.REQUEST);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>" + "Successfully created Room, room number is:" + roomNumber + "</b>");
				WSClient.setData("{var_roomNumber}", roomNumber);
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Not able to create Room");
			}

		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		}

		return roomNumber;
	}

	// Setting Housekeeping Room Status and calling Assign Room
	public void setHousekeepingRoomStatus(String ds) {
		try {
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Changing HouseKeeping status" + "</b>");
			String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", ds);
			String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);
			if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success",
					true)) {
				WSClient.writeToReport(LogStatus.INFO,
						"<b>" + "Successfully changed room status to vacant and inspected" + "</b>");
				assignRoom("DS_01");
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "SetHousekeepingRoomStatus blocked");
			}

		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		}
	}

	// Assigning Room
	public boolean assignRoom(String ds) {
		try {
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Assigning the Room" + "</b>");
			String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", ds);
			String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);
			if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Successfully Assigned Room" + "</b>");

				return true;
			} else {
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Assign Room is unsuccessful" + "</b>");
				WSClient.writeToReport(LogStatus.INFO,
						"<b>" + "-------------------------------------------------------" + "</b>");
				return false;
			}

		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		}
		return false;
	}

	// Fetch Hotel Rooms
	public void fetchHotelRooms(String ds) {
		String roomNumber;
		try {
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
			WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>" + "Fetching available Hotel Rooms to assign a room" + "</b>");
			String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", ds);
			String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);
			if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true)) {
				if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
					roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
							"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "Successfully Fetched Room to assign, room Number is: " + roomNumber + "</b>");
					WSClient.setData("{var_roomNumber}", roomNumber);
					boolean assignFlag = assignRoom("DS_01");
					if (assignFlag == false) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "Unable to assign the fetched Room so Preparing to create a new Room" + "</b>");
						/************
						 * Prerequisite : Creating a room to assign
						 ****************/
						roomNumber = createRoom("FirstFloor");
						WSClient.setData("{var_roomNumber}", roomNumber);

						/************
						 * Prerequisite : Changing the room status to inspected
						 * and then ASSIGN the room for release room
						 ****************/
						setHousekeepingRoomStatus("DS_01");
					}
				} else {
					/************
					 * Prerequisite : Creating a room to assign
					 ****************/
					roomNumber = createRoom("FirstFloor");
					WSClient.setData("{var_roomNumber}", roomNumber);

					/************
					 * Prerequisite : Changing the room status to inspected and
					 * then ASSIGN the room for release room
					 ****************/
					setHousekeepingRoomStatus("DS_01");
				}
			} else {
				WSClient.writeToReport(LogStatus.INFO, "Fetching Room Unsuccessful, creating a room now");

				/************
				 * Prerequisite : Creating a room to assign
				 ****************/
				roomNumber = createRoom("FirstFloor");
				WSClient.setData("{var_roomNumber}", roomNumber);

				/************
				 * Prerequisite : Changing the room status to inspected and then
				 * ASSIGN the room for release room
				 ****************/
				setHousekeepingRoomStatus("DS_01");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	public void getApplicationParameter(String parameter) throws Exception {
		WSClient.setData("{var_parameter}", parameter);
		String getAppParamReq = WSClient.createSOAPMessage("GetParameters", "DS_01");
		String getAppParamRes = WSClient.processSOAPMessage(getAppParamReq);

		if (WSAssert.assertIfElementExists(getAppParamRes, "GetParametersRS_Success", true)) {
			if (WSAssert.assertIfElementExists(getAppParamRes, "GetParametersRS_Parameters_Parameter", true)) {

				Parameter = WSClient.getElementValue(getAppParamRes, "GetParametersRS_Parameters_Parameter_Value",
						XMLType.RESPONSE);
			} else {
				Parameter = "error";
				WSClient.writeToReport(LogStatus.WARNING,
						"Prerequisite failed >> Checking the Application parameter fetch Reservation failed");
			}
		} else {
			Parameter = "error";
			WSClient.writeToReport(LogStatus.WARNING,
					"Prerequisite failed >> Checking the Application parameter fetch Reservation failed");
		}
	}

	public void changeApplicationParameter(String parameter, String settingValue) throws Exception {
		WSClient.setData("{var_parameter}", parameter);
		WSClient.setData("{var_settingValue}", settingValue);
		String getAppParamReq = WSClient.createSOAPMessage("ChangeApplicationSettings", "DS_01");
		String getAppParamRes = WSClient.processSOAPMessage(getAppParamReq);

		if (WSAssert.assertIfElementExists(getAppParamRes, "ChangeApplicationSettingsRS_Success", true)) {
			getApplicationParameter(parameter);
		} else {
			Parameter = "error";
			WSClient.writeToReport(LogStatus.WARNING,
					"Prerequisite failed >> Changing the Application parameter fetch Reservation failed");
		}
	}

	// Cancel Booking Sanity
	@Test(groups = { "sanity", "Reservation", "CancelBooking", "OWS", "in-QA" })

	/*
	 * Method to check the CANCEL BOOKING operation by passing Hotel Code, and
	 * Resv_Name_ID
	 */

	public void cancelBooking_6285() {
		try {
			String testName = "cancelBooking_6285";
			WSClient.startTest(testName,
					"Verify that booking is getting properly cancelled after passing hotel code and reservation id in the request",
					"sanity");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				// boolean prerequisite = true;

				/*************
				 * Prerequisite : Creating new profile by fetching values
				 * required
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
					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
					WSClient.setData("{var_resvId}", reservationId);

					/************
					 * Performing the Cancel Booking operation
					 ****************/
					String resort = OPERALib.getResort();
					String channel = OWSLib.getChannel();
					String uName = OPERALib.getUserName();
					String pass = OPERALib.getPassword();
					String channelType = OWSLib.getChannelType(channel);
					String channelCarrier = OWSLib.getChannelCarier(resort, channel);
					OWSLib.setOWSHeader(uName, pass, resort, channelType, channelCarrier);

					String cancelBookingReq = WSClient.createSOAPMessage("OWSCancelBooking", "DS_01");
					String cancelBookingRes = WSClient.processSOAPMessage(cancelBookingReq);
					if (WSAssert.assertIfElementExists(cancelBookingRes,
							"CancelBookingResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(cancelBookingRes,
								"CancelBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							String cancellationId = WSClient.getElementValue(cancelBookingRes,
									"Result_IDs_IDPair_operaId", XMLType.RESPONSE);
							WSClient.setData("{var_cancellationId}", cancellationId);

							String QS_01 = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> dbDataMap = WSClient.getDBRow(QS_01);

							String resv_status = dbDataMap.get("RESV_STATUS");
							if (resv_status.equals("CANCELLED")) {
								WSClient.writeToReport(LogStatus.PASS, "Booking successfully cancelled");
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Booking not cancelled");
							}
						}
					}

					// Checking for Text Element in Result
					if (WSAssert.assertIfElementExists(cancelBookingRes, "Result_Text_TextElement", true)) {

						String message = WSAssert.getElementValue(cancelBookingRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + message + "</b>");
					}

					// Checking For OperaErrorCode
					if (WSAssert.assertIfElementExists(cancelBookingRes, "CancelBookingResponse_Result_OperaErrorCode",
							true)) {
						String code = WSAssert.getElementValue(cancelBookingRes,
								"CancelBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The error code displayed in the response is :" + code + "</b>");
					}

					// Checking For Fault Schema
					if (WSAssert.assertIfElementExists(cancelBookingRes, "CancelBookingResponse_faultcode", true)) {
						String message = WSClient.getElementValue(cancelBookingRes, "CancelBookingResponse_faultstring",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"The Response has Fault Schema with message: " + message);
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

	// Cancel Booking Sanity
	@Test(groups = { "minimumRegression", "Reservation", "CancelBooking", "OWS", "in-QA" })

	/*
	 * Method to check the CANCEL BOOKING operation by passing Hotel Code, and
	 * Resv_Name_ID
	 */

	public void cancelBooking_6286() {
		try {
			String testName = "cancelBooking_6286";
			WSClient.startTest(testName,
					"Verify that booking is getting properly cancelled after passing hotel code, reservation id and reason code in the request",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "ResvCancelReason" })) {
				// boolean prerequisite = true;

				/*************
				 * Prerequisite : Creating new profile by fetching values
				 * required
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
					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
					WSClient.setData("{var_resvId}", reservationId);

					/************
					 * Performing the Cancel Booking operation
					 ****************/
					String resort = OPERALib.getResort();
					String channel = OWSLib.getChannel();
					String uName = OPERALib.getUserName();
					String pass = OPERALib.getPassword();
					String channelType = OWSLib.getChannelType(channel);
					String channelCarrier = OWSLib.getChannelCarier(resort, channel);
					OWSLib.setOWSHeader(uName, pass, resort, channelType, channelCarrier);

					WSClient.setData("{var_cancelReasonCode}",
							OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));

					String cancelBookingReq = WSClient.createSOAPMessage("OWSCancelBooking", "DS_02");
					String cancelBookingRes = WSClient.processSOAPMessage(cancelBookingReq);
					if (WSAssert.assertIfElementExists(cancelBookingRes,
							"CancelBookingResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(cancelBookingRes,
								"CancelBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							String cancellationId = WSClient.getElementValue(cancelBookingRes,
									"Result_IDs_IDPair_operaId", XMLType.RESPONSE);
							WSClient.setData("{var_cancellationId}", cancellationId);

							String QS_01 = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> dbDataMap = WSClient.getDBRow(QS_01);
							String resv_status = dbDataMap.get("RESV_STATUS");
							if (resv_status.equals("CANCELLED")) {
								String QS_02 = WSClient.getQuery("QS_02");
								dbDataMap = WSClient.getDBRow(QS_02);
								HashMap<String, String> xpath = new HashMap<String, String>();
								xpath.put("CancelTerm_CancelReason_Text", "CancelTerm_CancelReason_Text");
								xpath.put("CancelBookingRequest_CancelTerm_cancelReasonCode",
										"CancelBookingRequest_CancelTerm");
								LinkedHashMap<String, String> resResult = WSClient.getSingleNodeList(cancelBookingReq,
										xpath, false, XMLType.REQUEST);
								WSAssert.assertEquals(resResult, dbDataMap, false);
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Booking not cancelled");
							}
						}
					}

					// Checking for Text Element in Result
					if (WSAssert.assertIfElementExists(cancelBookingRes, "Result_Text_TextElement", true)) {

						String message = WSAssert.getElementValue(cancelBookingRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + message + "</b>");
					}

					// Checking For OperaErrorCode
					if (WSAssert.assertIfElementExists(cancelBookingRes, "CancelBookingResponse_Result_OperaErrorCode",
							true)) {
						String code = WSAssert.getElementValue(cancelBookingRes,
								"CancelBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The error code displayed in the response is :" + code + "</b>");
					}

					// Checking For Fault Schema

					if (WSAssert.assertIfElementExists(cancelBookingRes, "CancelBookingResponse_faultcode", true)) {
						String message = WSClient.getElementValue(cancelBookingRes, "CancelBookingResponse_faultstring",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"The Response has Fault Schema with message: " + message);
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

	// Cancel Booking minimumRegression
	@Test(groups = { "minimumRegression", "Reservation", "CancelBooking", "OWS", "in-QA" })

	/*
	 * Method to Verify that correct error code is received when cancelling
	 * already cancelled booking.
	 */

	public void cancelBooking_6297() {
		try {
			String testName = "cancelBooking_6297";
			WSClient.startTest(testName,
					"Verify that correct error code is received when cancelling already cancelled booking.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				// boolean prerequisite = true;

				/*************
				 * Prerequisite : Creating new profile by fetching values
				 * required
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
					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
					WSClient.setData("{var_resvId}", reservationId);

					/************
					 * Performing the Cancel Booking operation
					 ****************/
					String resort = OPERALib.getResort();
					String channel = OWSLib.getChannel();
					String uName = OPERALib.getUserName();
					String pass = OPERALib.getPassword();
					String channelType = OWSLib.getChannelType(channel);
					String channelCarrier = OWSLib.getChannelCarier(resort, channel);
					OWSLib.setOWSHeader(uName, pass, resort, channelType, channelCarrier);

					String cancelBookingReq = WSClient.createSOAPMessage("OWSCancelBooking", "DS_01");
					String cancelBookingRes = WSClient.processSOAPMessage(cancelBookingReq);
					if (WSAssert.assertIfElementExists(cancelBookingRes,
							"CancelBookingResponse_Result_resultStatusFlag", true)) {
						if (WSAssert.assertIfElementValueEquals(cancelBookingRes,
								"CancelBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
							String cancellationId = WSClient.getElementValue(cancelBookingRes,
									"Result_IDs_IDPair_operaId", XMLType.RESPONSE);
							WSClient.setData("{var_cancellationId}", cancellationId);

							String QS_01 = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> dbDataMap = WSClient.getDBRow(QS_01);

							String resv_status = dbDataMap.get("RESV_STATUS");
							if (resv_status.equals("CANCELLED") || cancellationId.equals(null)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "Canceling the cancelled Booking" + "</b>");
								cancelBookingReq = WSClient.createSOAPMessage("OWSCancelBooking", "DS_01");
								cancelBookingRes = WSClient.processSOAPMessage(cancelBookingReq);
								if (WSAssert.assertIfElementValueEquals(cancelBookingRes,
										"CancelBookingResponse_Result_resultStatusFlag", "FAIL", false))
									WSAssert.assertIfElementValueEquals(cancelBookingRes,
											"CancelBookingResponse_Result_OperaErrorCode",
											"BOOKING_PREVIOUSLY_CANCELLED", false);

								// Checking for Text Element in Result
								if (WSAssert.assertIfElementExists(cancelBookingRes, "Result_Text_TextElement", true)) {

									String message = WSAssert.getElementValue(cancelBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" + "The text displayed in the response is :" + message + "</b>");
								}

								// Checking For OperaErrorCode
								if (WSAssert.assertIfElementExists(cancelBookingRes,
										"CancelBookingResponse_Result_OperaErrorCode", true)) {
									String code = WSAssert.getElementValue(cancelBookingRes,
											"CancelBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" + "The error code displayed in the response is :" + code + "</b>");
								}

								// Checking For Fault Schema

								if (WSAssert.assertIfElementExists(cancelBookingRes, "CancelBookingResponse_faultcode",
										true)) {
									String message = WSClient.getElementValue(cancelBookingRes,
											"CancelBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"The Response has Fault Schema with message: " + message);
								}
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Booking not cancelled");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"************ Blocked :  prerequisite for Booking cancellation is not completed ************");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"************ Blocked :  prerequisite for Booking cancellation is not completed ************");
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

	// Cancel Booking minimumRegression
	@Test(groups = { "minimumRegression", "Reservation", "CancelBooking", "OWS", "in-QA" })

	/*
	 * Method to Verify that correct error code is received when cancelling
	 * already checked in reservation.
	 */

	public void cancelBooking_6299() {
		try {
			String testName = "cancelBooking_6299";
			WSClient.startTest(testName,
					"Verify that correct error code is received when cancelling already checked in reservation.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				// boolean prerequisite = true;

				/*************
				 * Prerequisite : Creating new profile by fetching values
				 * required
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
					HashMap<String, String> resv = CreateReservation.createReservation("DS_12");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
					WSClient.setData("{var_resvId}", reservationId);

					/********
					 * Prerequisite : Fetch Hotel Rooms
					 **************/
					fetchHotelRooms("DS_03");
					String resort = OPERALib.getResort();
					WSClient.setData("{var_owsresort}", resort);

					String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
					String checkInRes = WSClient.processSOAPMessage(checkInReq);

					if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {
						/************
						 * Performing the Cancel Booking operation
						 ****************/
						String channel = OWSLib.getChannel();
						String uName = OPERALib.getUserName();
						String pass = OPERALib.getPassword();
						String channelType = OWSLib.getChannelType(channel);
						String channelCarrier = OWSLib.getChannelCarier(resort, channel);
						OWSLib.setOWSHeader(uName, pass, resort, channelType, channelCarrier);

						String cancelBookingReq = WSClient.createSOAPMessage("OWSCancelBooking", "DS_01");
						String cancelBookingRes = WSClient.processSOAPMessage(cancelBookingReq);
						if (WSAssert.assertIfElementExists(cancelBookingRes,
								"CancelBookingResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(cancelBookingRes,
									"CancelBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
								WSAssert.assertIfElementValueEquals(cancelBookingRes,
										"CancelBookingResponse_Result_OperaErrorCode",
										"RESTRICTION_ON_CANCEL_OR_MODIFY_CONTACT_HOTEL", false);
							}
						}

						// Checking for Text Element in Result
						if (WSAssert.assertIfElementExists(cancelBookingRes, "Result_Text_TextElement", true)) {

							String message = WSAssert.getElementValue(cancelBookingRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The text displayed in the response is :" + message + "</b>");
						}

						// Checking For OperaErrorCode
						if (WSAssert.assertIfElementExists(cancelBookingRes,
								"CancelBookingResponse_Result_OperaErrorCode", true)) {
							String code = WSAssert.getElementValue(cancelBookingRes,
									"CancelBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The error code displayed in the response is :" + code + "</b>");
						}

						// Checking For Fault Schema

						if (WSAssert.assertIfElementExists(cancelBookingRes, "CancelBookingResponse_faultcode", true)) {
							String message = WSClient.getElementValue(cancelBookingRes,
									"CancelBookingResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The Response has Fault Schema with message: " + message);
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Blocked: checkin prerequisite failed!");
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

	// //Cancel Booking minimumRegression
	// @Test(groups = { "minimumRegression", "Reservation", "CancelBooking",
	// "OWS", "in-QA" })
	//
	// /* Method to Verify that correct error code is received when canceling
	// already checked out reservation. */
	//
	// public void cancelBooking_6300()
	// {
	// try
	// {
	// String testName = "cancelBooking_6300";
	// WSClient.startTest(testName, "Verify that correct error code is received
	// when cancelling already checked out reservation.", "minimumRegression");
	// if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode",
	// "RoomType", "SourceCode", "MarketCode", "PaymentMethod" }))
	// {
	// // boolean prerequisite = true;
	//
	// /*************
	// * Prerequisite : Creating new profile by fetching values
	// * required
	// ******************/
	// String resortOperaValue = OPERALib.getResort();
	// String chain=OPERALib.getChain();
	//
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_chain}",chain);
	// String uname = OPERALib.getUserName();
	// OPERALib.setOperaHeader(uname);
	// String operaProfileID = CreateProfile.createProfile("DS_01");
	// if(!operaProfileID.equals("error"))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID
	// + "</b>");
	// WSClient.setData("{var_profileId}", operaProfileID);
	//
	// /********
	// * Prerequisite : Creating reservation
	// **************/
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_payment}",
	// OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	// HashMap<String,String> resv=
	// CreateReservation.createReservation("DS_12");
	// String reservationId = resv.get("reservationId");
	// String confirmationId = resv.get("confirmationId");
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " +
	// reservationId + "</b>");
	// WSClient.setData("{var_resvId}", reservationId);
	//
	// /********
	// * Prerequisite : Fetch Hotel Rooms
	// **************/
	// fetchHotelRooms("DS_03");
	// String resort = OPERALib.getResort();
	// WSClient.setData("{var_owsresort}", resort);
	// WSClient.writeToReport(LogStatus.INFO, "Checking in reservation");
	// String checkInReq =
	// WSClient.createSOAPMessage("CheckinReservation","DS_01");
	// String checkInRes = WSClient.processSOAPMessage(checkInReq);
	//
	// if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "Successfully checked in");
	// WSClient.writeToReport(LogStatus.INFO, "Checking out reservation");
	// String checkOutReq =
	// WSClient.createSOAPMessage("CheckoutReservation","DS_01");
	// String checkOutRes = WSClient.processSOAPMessage(checkOutReq);
	// if(WSAssert.assertIfElementExists(checkOutRes,"CheckoutReservationRS_Success",true))
	// {
	// /************
	// * Performing the Cancel Booking
	// * operation
	// ****************/
	// String channel = OWSLib.getChannel();
	// String uName = OPERALib.getUserName();
	// String pass = OPERALib.getPassword();
	// String channelType = OWSLib.getChannelType(channel);
	// String channelCarrier = OWSLib.getChannelCarier(resort, channel);
	// OWSLib.setOWSHeader(uName, pass, resort, channelType, channelCarrier);
	//
	// String cancelBookingReq = WSClient.createSOAPMessage("OWSCancelBooking",
	// "DS_01");
	// String cancelBookingRes = WSClient.processSOAPMessage(cancelBookingReq);
	// if (WSAssert.assertIfElementExists(cancelBookingRes,
	// "CancelBookingResponse_Result_resultStatusFlag", true)) {
	// if (WSAssert.assertIfElementValueEquals(cancelBookingRes,
	// "CancelBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
	// WSAssert.assertIfElementValueEquals(cancelBookingRes,
	// "CancelBookingResponse_Result_OperaErrorCode",
	// "RESTRICTION_ON_CANCEL_OR_MODIFY_CONTACT_HOTEL", false);
	// }
	// }
	//
	// //Checking for Text Element in Result
	// if (WSAssert.assertIfElementExists(cancelBookingRes,
	// "Result_Text_TextElement", true))
	// {
	//
	// String message = WSAssert.getElementValue(cancelBookingRes,
	// "Result_Text_TextElement", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the
	// response is :" + message+"</b>");
	// }
	//
	// //Checking For OperaErrorCode
	// if
	// (WSAssert.assertIfElementExists(cancelBookingRes,"CancelBookingResponse_Result_OperaErrorCode",
	// true))
	// {
	// String code = WSAssert.getElementValue(cancelBookingRes,
	// "CancelBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error code displayed in
	// the response is :" + code+"</b>");
	// }
	//
	// //Checking For Fault Schema
	//
	// if(WSAssert.assertIfElementExists(cancelBookingRes,
	// "CancelBookingResponse_faultcode", true))
	// {
	// String
	// message=WSClient.getElementValue(cancelBookingRes,"CancelBookingResponse_faultstring",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "The Response has Fault Schema
	// with message: "+message);
	// }
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Blocked: checkout prerequisite
	// failed!");
	// }
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Blocked: checkin prerequisite
	// failed!");
	// }
	//
	//
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Unique
	// ID's failed!------ Create Profile -----Blocked");
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "************Blocked :
	// prerequisites for RateCode or RoomType or SourceCode or MarketCode or
	// PaymentMethod**********");
	// }
	// }
	// catch (Exception e)
	// {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// }
	// finally
	// {
	// // logger.endExtentTest();
	// }
	// }
	//

	//
	// //Cancel Booking minimum regression
	// //EOD required
	// @Test(groups = { "minimumRegression", "Reservation", "CancelBooking",
	// "OWS", "in-QA" })
	//
	// /* Verify if proper error code is retrieved when cancelling a booking for
	// which arrival date is already passed! */
	//
	// public void cancelBooking_6301()
	// {
	// try
	// {
	// String testName = "cancelBooking_6301";
	// WSClient.startTest(testName, "Verify that proper error code is retrieved
	// when cancelling a booking for which arrival date is already passed!",
	// "minimumRegression");
	// if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode",
	// "RoomType", "SourceCode", "MarketCode", "PaymentMethod" }))
	// {
	// // boolean prerequisite = true;
	//
	// /*************
	// * Prerequisite : Creating new profile by fetching values
	// * required
	// ******************/
	// String resortOperaValue = OPERALib.getResort();
	// String chain=OPERALib.getChain();
	//
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_chain}",chain);
	// String uname = OPERALib.getUserName();
	// OPERALib.setOperaHeader(uname);
	// String operaProfileID = CreateProfile.createProfile("DS_01");
	// if(!operaProfileID.equals("error"))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID
	// + "</b>");
	// WSClient.setData("{var_profileId}", operaProfileID);
	//
	// /********
	// * Prerequisite : Creating reservation
	// **************/
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_payment}",
	// OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	// HashMap<String,String> resv=
	// CreateReservation.createReservation("DS_12");
	// String reservationId = resv.get("reservationId");
	// String confirmationId = resv.get("confirmationId");
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " +
	// reservationId + "</b>");
	// WSClient.setData("{var_resvId}", reservationId);
	//
	//
	// /************
	// * Performing the Cancel Booking
	// * operation
	// ****************/
	// String resort = OPERALib.getResort();
	// String channel = OWSLib.getChannel();
	// String uName = OPERALib.getUserName();
	// String pass = OPERALib.getPassword();
	// String channelType = OWSLib.getChannelType(channel);
	// String channelCarrier = OWSLib.getChannelCarier(resort, channel);
	// OWSLib.setOWSHeader(uName, pass, resort, channelType, channelCarrier);
	//
	//
	//
	// String cancelBookingReq = WSClient.createSOAPMessage("OWSCancelBooking",
	// "DS_02");
	// String cancelBookingRes = WSClient.processSOAPMessage(cancelBookingReq);
	// if (WSAssert.assertIfElementExists(cancelBookingRes,
	// "CancelBookingResponse_Result_resultStatusFlag", false)) {
	// if (WSAssert.assertIfElementValueEquals(cancelBookingRes,
	// "CancelBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
	// String cancellationId = WSClient.getElementValue(cancelBookingRes,
	// "Result_IDs_IDPair_operaId", XMLType.RESPONSE);
	// WSClient.setData("{var_cancellationId}", cancellationId);
	//
	// String QS_01 = WSClient.getQuery("QS_01");
	// WSClient.writeToReport(LogStatus.INFO, QS_01);
	// LinkedHashMap<String,String> dbDataMap=WSClient.getDBRow(QS_01);
	//
	// String resv_status = dbDataMap.get("RESV_STATUS");
	// if (resv_status.equals("CANCELLED")) {
	// String QS_02 = WSClient.getQuery("QS_02");
	// dbDataMap = WSClient.getDBRow(QS_02);
	// HashMap<String, String> xpath = new HashMap<String, String>();
	// xpath.put("CancelTerm_CancelReason_Text",
	// "CancelTerm_CancelReason_Text");
	// xpath.put("CancelBookingRequest_CancelTerm_cancelReasonCode",
	// "CancelBookingRequest_CancelTerm");
	// LinkedHashMap<String, String> resResult =
	// WSClient.getSingleNodeList(cancelBookingReq, xpath, false,
	// XMLType.REQUEST);
	// WSAssert.assertEquals(resResult, dbDataMap, false);
	// }
	// else {
	// WSClient.writeToReport(LogStatus.FAIL, "Booking not cancelled");
	// }
	// }
	// }
	//
	//
	// //Checking for Text Element in Result
	// if (WSAssert.assertIfElementExists(cancelBookingRes,
	// "Result_Text_TextElement", true))
	// {
	//
	// String message = WSAssert.getElementValue(cancelBookingRes,
	// "Result_Text_TextElement", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the
	// response is :" + message+"</b>");
	// }
	//
	// //Checking For OperaErrorCode
	// if
	// (WSAssert.assertIfElementExists(cancelBookingRes,"CancelBookingResponse_Result_OperaErrorCode",
	// true))
	// {
	// String code = WSAssert.getElementValue(cancelBookingRes,
	// "CancelBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error code displayed in
	// the response is :" + code+"</b>");
	// }
	//
	// //Checking For Fault Schema
	//
	// if(WSAssert.assertIfElementExists(cancelBookingRes,
	// "CancelBookingResponse_faultcode", true))
	// {
	// String
	// message=WSClient.getElementValue(cancelBookingRes,"CancelBookingResponse_faultstring",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "The Response has Fault Schema
	// with message: "+message);
	// }
	//
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Unique
	// ID's failed!------ Create Profile -----Blocked");
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "************Blocked :
	// prerequisites for RateCode or RoomType or SourceCode or MarketCode or
	// PaymentMethod**********");
	// }
	// }
	// catch (Exception e)
	// {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// }
	// finally
	// {
	// // logger.endExtentTest();
	// }
	// }

	// //Cancel Booking minimum regression
	// //waiting for parameterized ows v5 header
	// @Test(groups = { "minimumRegression", "Reservation", "CancelBooking",
	// "OWS", "in-QA" })
	//
	// /* Method to Verify if proper error message is return when username or
	// password are not correctly provided in request */
	//
	// public void cancelBooking_6305()
	// {
	// try
	// {
	// String testName = "cancelBooking_6305";
	// WSClient.startTest(testName, "Verify that proper error message is return
	// when username or password are not correctly provided in request",
	// "minimumRegression");
	// if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode",
	// "RoomType", "SourceCode", "MarketCode", "PaymentMethod" }))
	// {
	// // boolean prerequisite = true;
	//
	// /*************
	// * Prerequisite : Creating new profile by fetching values
	// * required
	// ******************/
	// String resortOperaValue = OPERALib.getResort();
	// String chain=OPERALib.getChain();
	//
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_chain}",chain);
	// String uname = OPERALib.getUserName();
	// OPERALib.setOperaHeader(uname);
	// String operaProfileID = CreateProfile.createProfile("DS_01");
	// if(!operaProfileID.equals("error"))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID
	// + "</b>");
	// WSClient.setData("{var_profileId}", operaProfileID);
	//
	// /********
	// * Prerequisite : Creating reservation
	// **************/
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_payment}",
	// OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	// HashMap<String,String> resv=
	// CreateReservation.createReservation("DS_01");
	// String reservationId = resv.get("reservationId");
	// String confirmationId = resv.get("confirmationId");
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " +
	// reservationId + "</b>");
	// WSClient.setData("{var_resvId}", reservationId);
	//
	//
	// /************
	// * Performing the Cancel Booking
	// * operation
	// ****************/
	// String resort = OPERALib.getResort();
	// String channel = OWSLib.getChannel();
	// String uName = OPERALib.getUserName();
	// String pass = OPERALib.getPassword();
	// String channelType = OWSLib.getChannelType(channel);
	// String channelCarrier = OWSLib.getChannelCarier(resort, channel);
	// OWSLib.setOWSHeader(uName, pass, resort, channelType, channelCarrier);
	//
	//
	//
	// String cancelBookingReq = WSClient.createSOAPMessage("OWSCancelBooking",
	// "DS_02");
	// String cancelBookingRes = WSClient.processSOAPMessage(cancelBookingReq);
	// if (WSAssert.assertIfElementExists(cancelBookingRes,
	// "CancelBookingResponse_Result_resultStatusFlag", false)) {
	// if (WSAssert.assertIfElementValueEquals(cancelBookingRes,
	// "CancelBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
	// String cancellationId = WSClient.getElementValue(cancelBookingRes,
	// "Result_IDs_IDPair_operaId", XMLType.RESPONSE);
	// WSClient.setData("{var_cancellationId}", cancellationId);
	//
	// String QS_01 = WSClient.getQuery("QS_01");
	// WSClient.writeToReport(LogStatus.INFO, QS_01);
	// LinkedHashMap<String,String> dbDataMap=WSClient.getDBRow(QS_01);
	//
	// String resv_status = dbDataMap.get("RESV_STATUS");
	// if (resv_status.equals("CANCELLED")) {
	// String QS_02 = WSClient.getQuery("QS_02");
	// dbDataMap = WSClient.getDBRow(QS_02);
	// HashMap<String, String> xpath = new HashMap<String, String>();
	// xpath.put("CancelTerm_CancelReason_Text",
	// "CancelTerm_CancelReason_Text");
	// xpath.put("CancelBookingRequest_CancelTerm_cancelReasonCode",
	// "CancelBookingRequest_CancelTerm");
	// LinkedHashMap<String, String> resResult =
	// WSClient.getSingleNodeList(cancelBookingReq, xpath, false,
	// XMLType.REQUEST);
	// WSAssert.assertEquals(resResult, dbDataMap, false);
	// }
	// else {
	// WSClient.writeToReport(LogStatus.FAIL, "Booking not cancelled");
	// }
	// }
	// }
	//
	//
	// //Checking for Text Element in Result
	// if (WSAssert.assertIfElementExists(cancelBookingRes,
	// "Result_Text_TextElement", true))
	// {
	//
	// String message = WSAssert.getElementValue(cancelBookingRes,
	// "Result_Text_TextElement", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the
	// response is :" + message+"</b>");
	// }
	//
	// //Checking For OperaErrorCode
	// if
	// (WSAssert.assertIfElementExists(cancelBookingRes,"CancelBookingResponse_Result_OperaErrorCode",
	// true))
	// {
	// String code = WSAssert.getElementValue(cancelBookingRes,
	// "CancelBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error code displayed in
	// the response is :" + code+"</b>");
	// }
	//
	// //Checking For Fault Schema
	//
	// if(WSAssert.assertIfElementExists(cancelBookingRes,
	// "CancelBookingResponse_faultcode", true))
	// {
	// String
	// message=WSClient.getElementValue(cancelBookingRes,"CancelBookingResponse_faultstring",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "The Response has Fault Schema
	// with message: "+message);
	// }
	//
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Unique
	// ID's failed!------ Create Profile -----Blocked");
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "************Blocked :
	// prerequisites for RateCode or RoomType or SourceCode or MarketCode or
	// PaymentMethod**********");
	// }
	// }
	// catch (Exception e)
	// {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// }
	// finally
	// {
	// // logger.endExtentTest();
	// }
	// }

	// Cancel Booking minimum regression
	@Test(groups = { "minimumRegression", "Reservation", "CancelBooking", "OWS", "in-QA" })

	/*
	 * Method to check the CANCEL BOOKING operation by passing Hotel Code, and
	 * External confirmation number
	 */

	public void cancelBooking_6314() {
		try {
			String testName = "cancelBooking_6314";
			WSClient.startTest(testName,
					"Verify that booking is getting properly cancelled after passing hotel code and External confirmation number in the request",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "ResvCancelReason" })) {
				// boolean prerequisite = true;

				/*************
				 * Prerequisite : Creating new profile by fetching values
				 * required
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
					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
					WSClient.setData("{var_resvId}", reservationId);

					WSClient.setData("{var_confirmationId}", confirmationId);

					/************
					 * Performing the Cancel Booking operation
					 ****************/
					String resort = OPERALib.getResort();
					String channel = OWSLib.getChannel();
					String uName = OPERALib.getUserName();
					String pass = OPERALib.getPassword();
					String channelType = OWSLib.getChannelType(channel);
					String channelCarrier = OWSLib.getChannelCarier(resort, channel);
					OWSLib.setOWSHeader(uName, pass, resort, channelType, channelCarrier);

					WSClient.setData("{var_cancelReasonCode}",
							OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));

					String cancelBookingReq = WSClient.createSOAPMessage("OWSCancelBooking", "DS_02");
					String cancelBookingRes = WSClient.processSOAPMessage(cancelBookingReq);
					if (WSAssert.assertIfElementExists(cancelBookingRes,
							"CancelBookingResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(cancelBookingRes,
								"CancelBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							String cancellationId = WSClient.getElementValue(cancelBookingRes,
									"Result_IDs_IDPair_operaId", XMLType.RESPONSE);
							WSClient.setData("{var_cancellationId}", cancellationId);

							String QS_01 = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> dbDataMap = WSClient.getDBRow(QS_01);

							String resv_status = dbDataMap.get("RESV_STATUS");
							if (resv_status.equals("CANCELLED")) {
								String QS_02 = WSClient.getQuery("QS_02");
								dbDataMap = WSClient.getDBRow(QS_02);
								HashMap<String, String> xpath = new HashMap<String, String>();
								xpath.put("CancelTerm_CancelReason_Text", "CancelTerm_CancelReason_Text");
								xpath.put("CancelBookingRequest_CancelTerm_cancelReasonCode",
										"CancelBookingRequest_CancelTerm");
								LinkedHashMap<String, String> resResult = WSClient.getSingleNodeList(cancelBookingReq,
										xpath, false, XMLType.REQUEST);
								WSAssert.assertEquals(resResult, dbDataMap, false);
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Booking not cancelled");
							}
						}
					}

					// Checking for Text Element in Result
					if (WSAssert.assertIfElementExists(cancelBookingRes, "Result_Text_TextElement", true)) {

						String message = WSAssert.getElementValue(cancelBookingRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + message + "</b>");
					}

					// Checking For OperaErrorCode
					if (WSAssert.assertIfElementExists(cancelBookingRes, "CancelBookingResponse_Result_OperaErrorCode",
							true)) {
						String code = WSAssert.getElementValue(cancelBookingRes,
								"CancelBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The error code displayed in the response is :" + code + "</b>");
					}

					// Checking For Fault Schema

					if (WSAssert.assertIfElementExists(cancelBookingRes, "CancelBookingResponse_faultcode", true)) {
						String message = WSClient.getElementValue(cancelBookingRes, "CancelBookingResponse_faultstring",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"The Response has Fault Schema with message: " + message);
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

	// Cancel Booking minimum regression
	@Test(groups = { "minimumRegression", "Reservation", "CancelBooking", "OWS", "in-QA" })

	/*
	 * Method to check the CANCEL BOOKING operation by passing Hotel Code, and
	 * External confirmation number and leg number
	 */

	public void cancelBooking_6287() {
		try {
			String testName = "cancelBooking_6287";
			WSClient.startTest(testName,
					"Verify that booking is getting properly cancelled after passing hotel code and External confirmation number and leg number in the request",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "ResvCancelReason" })) {
				// boolean prerequisite = true;

				/*************
				 * Prerequisite : Creating new profile by fetching values
				 * required
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
					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
					WSClient.setData("{var_resvId}", reservationId);
					WSClient.setData("{var_confirmationId}", confirmationId);
					String legNo = WSClient.getDBRow(WSClient.getQuery("OWSCancelBooking", "QS_04"))
							.get("CONFIRMATION_LEG_NO");
					WSClient.setData("{var_legNo}", legNo);
					resv = CreateReservation.createReservation("DS_10");
					reservationId = resv.get("reservationId");
					confirmationId = resv.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
					WSClient.setData("{var_resvId}", reservationId);
					WSClient.setData("{var_confirmationId}", confirmationId);
					legNo = WSClient.getDBRow(WSClient.getQuery("OWSCancelBooking", "QS_04"))
							.get("CONFIRMATION_LEG_NO");
					WSClient.setData("{var_legNo}", legNo);

					/************
					 * Performing the Cancel Booking operation
					 ****************/
					String resort = OPERALib.getResort();
					String channel = OWSLib.getChannel();
					String uName = OPERALib.getUserName();
					String pass = OPERALib.getPassword();
					String channelType = OWSLib.getChannelType(channel);
					String channelCarrier = OWSLib.getChannelCarier(resort, channel);
					OWSLib.setOWSHeader(uName, pass, resort, channelType, channelCarrier);

					WSClient.setData("{var_cancelReasonCode}",
							OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));

					String cancelBookingReq = WSClient.createSOAPMessage("OWSCancelBooking", "DS_04");
					String cancelBookingRes = WSClient.processSOAPMessage(cancelBookingReq);
					if (WSAssert.assertIfElementExists(cancelBookingRes,
							"CancelBookingResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(cancelBookingRes,
								"CancelBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							String cancellationId = WSClient.getElementValue(cancelBookingRes,
									"Result_IDs_IDPair_operaId", XMLType.RESPONSE);
							WSClient.setData("{var_cancellationId}", cancellationId);

							String QS_01 = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> dbDataMap = WSClient.getDBRow(QS_01);

							String resv_status = dbDataMap.get("RESV_STATUS");
							if (resv_status.equals("CANCELLED")) {
								String QS_02 = WSClient.getQuery("QS_02");
								dbDataMap = WSClient.getDBRow(QS_02);
								HashMap<String, String> xpath = new HashMap<String, String>();
								xpath.put("CancelTerm_CancelReason_Text", "CancelTerm_CancelReason_Text");
								xpath.put("CancelBookingRequest_CancelTerm_cancelReasonCode",
										"CancelBookingRequest_CancelTerm");
								LinkedHashMap<String, String> resResult = WSClient.getSingleNodeList(cancelBookingReq,
										xpath, false, XMLType.REQUEST);
								WSAssert.assertEquals(resResult, dbDataMap, false);
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Booking not cancelled");
							}
						}
					}

					// Checking for Text Element in Result
					if (WSAssert.assertIfElementExists(cancelBookingRes, "Result_Text_TextElement", true)) {

						String message = WSAssert.getElementValue(cancelBookingRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + message + "</b>");
					}

					// Checking For OperaErrorCode
					if (WSAssert.assertIfElementExists(cancelBookingRes, "CancelBookingResponse_Result_OperaErrorCode",
							true)) {
						String code = WSAssert.getElementValue(cancelBookingRes,
								"CancelBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The error code displayed in the response is :" + code + "</b>");
					}

					// Checking For Fault Schema

					if (WSAssert.assertIfElementExists(cancelBookingRes, "CancelBookingResponse_faultcode", true)) {
						String message = WSClient.getElementValue(cancelBookingRes, "CancelBookingResponse_faultstring",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"The Response has Fault Schema with message: " + message);
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

	// //Cancel Booking minimum regression
	// // sending Email required
	// @Test(groups = { "minimumRegression", "Reservation", "CancelBooking",
	// "OWS", "in-QA" })
	//
	// /* Method to Verify that specified Confirmation Letter to be used for
	// 'CANCELLED' reservations */
	//
	// public void cancelBooking_22976()
	// {
	// }

	// Cancel Booking minimum regression
	@Test(groups = { "minimumRegression", "Reservation", "CancelBooking", "OWS", "in-QA" })

	/*
	 * Method to check the CANCEL BOOKING operation by passing shared
	 * reservation
	 */

	public void cancelBooking_40081() {
		try {
			String testName = "cancelBooking_40081";
			WSClient.startTest(testName,
					"Verify that booking is getting properly cancelled if shared reservations are used",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "ResvCancelReason" })) {
				// boolean prerequisite = true;

				/*************
				 * Prerequisite : Creating new profile by fetching values
				 * required
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
					HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
					HashMap<String, String> resv1 = CreateReservation.createReservation("DS_01");
					String reservationId1 = resv.get("reservationId");
					String confirmationId1 = resv.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId1 + "</b>");
					WSClient.setData("{var_resvId}", reservationId);
					WSClient.setData("{var_resvId1}", reservationId1);

					String combineReq = WSClient.createSOAPMessage("CombineShareReservations", "DS_01");
					String combineRes = WSClient.processSOAPMessage(combineReq);

					if (WSAssert.assertIfElementExists(combineRes, "CombineShareReservationsRS_Success", true)) {
						String combineResID = WSClient.getElementValue(combineRes,
								"Reservation_ReservationIDList_UniqueID_ID", XMLType.RESPONSE);
						WSClient.setData("{var_resvId}", combineResID);
					}

					/************
					 * Performing the Cancel Booking operation
					 ****************/
					String resort = OPERALib.getResort();
					String channel = OWSLib.getChannel();
					String uName = OPERALib.getUserName();
					String pass = OPERALib.getPassword();
					String channelType = OWSLib.getChannelType(channel);
					String channelCarrier = OWSLib.getChannelCarier(resort, channel);
					OWSLib.setOWSHeader(uName, pass, resort, channelType, channelCarrier);

					WSClient.setData("{var_cancelReasonCode}",
							OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
					String cancelBookingReq = WSClient.createSOAPMessage("OWSCancelBooking", "DS_02");
					String cancelBookingRes = WSClient.processSOAPMessage(cancelBookingReq);
					if (WSAssert.assertIfElementExists(cancelBookingRes,
							"CancelBookingResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(cancelBookingRes,
								"CancelBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							String cancellationId = WSClient.getElementValue(cancelBookingRes,
									"Result_IDs_IDPair_operaId", XMLType.RESPONSE);
							WSClient.setData("{var_cancellationId}", cancellationId);

							String QS_01 = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> dbDataMap = WSClient.getDBRow(QS_01);
							String resv_status = dbDataMap.get("RESV_STATUS");
							if (resv_status.equals("CANCELLED")) {
								String QS_02 = WSClient.getQuery("QS_02");
								dbDataMap = WSClient.getDBRow(QS_02);
								HashMap<String, String> xpath = new HashMap<String, String>();
								xpath.put("CancelTerm_CancelReason_Text", "CancelTerm_CancelReason_Text");
								xpath.put("CancelBookingRequest_CancelTerm_cancelReasonCode",
										"CancelBookingRequest_CancelTerm");
								LinkedHashMap<String, String> resResult = WSClient.getSingleNodeList(cancelBookingReq,
										xpath, false, XMLType.REQUEST);
								WSAssert.assertEquals(resResult, dbDataMap, false);
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Booking not cancelled");
							}
						}
					}

					// Checking for Text Element in Result
					if (WSAssert.assertIfElementExists(cancelBookingRes, "Result_Text_TextElement", true)) {

						String message = WSAssert.getElementValue(cancelBookingRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + message + "</b>");
					}

					// Checking For OperaErrorCode
					if (WSAssert.assertIfElementExists(cancelBookingRes, "CancelBookingResponse_Result_OperaErrorCode",
							true)) {
						String code = WSAssert.getElementValue(cancelBookingRes,
								"CancelBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The error code displayed in the response is :" + code + "</b>");
					}

					// Checking For Fault Schema

					if (WSAssert.assertIfElementExists(cancelBookingRes, "CancelBookingResponse_faultcode", true)) {
						String message = WSClient.getElementValue(cancelBookingRes, "CancelBookingResponse_faultstring",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"The Response has Fault Schema with message: " + message);
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

	// not getting proper message in response, not able to assign cancellation
	// policy to rate codes
	// Cancel Booking minimum regression
	@Test(groups = { "minimumRegression", "Reservation", "CancelBooking", "OWS", "in-QA" })

	/*
	 * Method to check the CANCEL BOOKING operation by passing cancellation
	 * policy
	 */

	public void cancelBooking_6308() {
		HashMap<String, String> parameterStatus = new HashMap<String, String>();
		try {
			String testName = "cancelBooking_6308";
			WSClient.startTest(testName,
					"Verify that booking with cancellation policy attached to it is getting cancelled successfully.",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			getApplicationParameter("AUTO_DEPOSIT_CANCELLATION_REFUND");
			parameterStatus.put("AUTO_DEPOSIT_CANCELLATION_REFUND", Parameter);
			if (!Parameter.equals("Y")) {
				changeApplicationParameter("AUTO_DEPOSIT_CANCELLATION_REFUND", "Y");
			}

			getApplicationParameter("CONFIRMATION_LETTER_CANCEL_RESERVATION");
			parameterStatus.put("CONFIRMATION_LETTER_CANCEL_RESERVATION", Parameter);
			if (!Parameter.equals("Y")) {
				changeApplicationParameter("CONFIRMATION_LETTER_CANCEL_RESERVATION", "Y");
			}

			getApplicationParameter("CANCEL_WITH_DEPOSIT");
			parameterStatus.put("CANCEL_WITH_DEPOSIT", Parameter);
			if (!Parameter.equals("Y")) {
				changeApplicationParameter("CANCEL_WITH_DEPOSIT", "Y");
			}

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "ResvCancelReason" })) {
				// boolean prerequisite = true;

				/*************
				 * Prerequisite : Creating new profile by fetching values
				 * required
				 ******************/
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

					WSClient.setData("{var_Resort}", resortOperaValue);
					String value = OperaPropConfig.getDataSetForCode("CancellationPolicy", "DS_01");
					WSClient.setData("{var_CancelCode}", value);

					String createCancellationPolicyReq = WSClient.createSOAPMessage("CreateCancellationPolicy",
							"DS_01");
					String createCancellationPolicyRes = WSClient.processSOAPMessage(createCancellationPolicyReq);

					if (WSAssert.assertIfElementExists(createCancellationPolicyRes,
							"CreateCancellationPolicyRS_Success", true)) {

					}

					/************
					 * Performing the Cancel Booking operation
					 ****************/
					String resort = OPERALib.getResort();
					String channel = OWSLib.getChannel();
					String uName = OPERALib.getUserName();
					String pass = OPERALib.getPassword();
					String channelType = OWSLib.getChannelType(channel);
					String channelCarrier = OWSLib.getChannelCarier(resort, channel);
					OWSLib.setOWSHeader(uName, pass, resort, channelType, channelCarrier);

					WSClient.setData("{var_cancelReasonCode}",
							OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
					String cancelBookingReq = WSClient.createSOAPMessage("OWSCancelBooking", "DS_02");
					String cancelBookingRes = WSClient.processSOAPMessage(cancelBookingReq);
					if (WSAssert.assertIfElementExists(cancelBookingRes,
							"CancelBookingResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(cancelBookingRes,
								"CancelBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							WSAssert.assertIfElementValueEquals(cancelBookingRes,
									"CancelBookingResponse_Result_OperaErrorCode",
									"CANCELLATION_CONFIRMED_PENALTIES_APPLIED", false);
						}
					}

					// Checking for Text Element in Result
					if (WSAssert.assertIfElementExists(cancelBookingRes, "Result_Text_TextElement", true)) {

						String message = WSAssert.getElementValue(cancelBookingRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + message + "</b>");
					}

					// Checking For OperaErrorCode
					if (WSAssert.assertIfElementExists(cancelBookingRes, "CancelBookingResponse_Result_OperaErrorCode",
							true)) {
						String code = WSAssert.getElementValue(cancelBookingRes,
								"CancelBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The error code displayed in the response is :" + code + "</b>");
					}

					// Checking For Fault Schema

					if (WSAssert.assertIfElementExists(cancelBookingRes, "CancelBookingResponse_faultcode", true)) {
						String message = WSClient.getElementValue(cancelBookingRes, "CancelBookingResponse_faultstring",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"The Response has Fault Schema with message: " + message);
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
			try {
				Iterator<?> it = parameterStatus.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					String parameterName = pair.getValue().toString();
					String parameterValue = pair.getKey().toString();
					changeApplicationParameter(parameterName, parameterValue);
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			}
		}
	}

	@Test(groups = { "minimumRegression", "CancelBooking", "OWS", "Reservation" })

	public void cancelBooking_40082() {
		try {

			String testName = "cancelBooking_40082";
			WSClient.startTest(testName, "Verify that queued reservation is getting properly cancelled",
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

				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/**** check parameter value of QUEUE_ROOMS ****/
				getApplicationParameter("QUEUE_ROOMS");
				if (!Parameter.equals("Y")) {
					changeApplicationParameter("QUEUE_ROOMS", "Y");
				}

				String profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + profileID + "</b>");
					WSClient.setData("{var_profileId}", profileID);

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					// Prerequisite 2 - Create Reservation

					HashMap<String, String> resv = CreateReservation.createReservation("DS_05");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					if (!reservationId.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
						WSClient.setData("{var_resvId}", reservationId);
						WSClient.setData("{var_confirmationId}", confirmationId);
						String queueResvReq = WSClient.createSOAPMessage("AddReservationToQueue", "DS_01");
						String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

						// ****Add to Queue ***//
						if (WSAssert.assertIfElementExists(queueResvRes, "AddReservationToQueueRS_Success", true)) {
							if (WSAssert.assertIfElementExists(queueResvRes,
									"AddReservationToQueueRS_QueueInfo_Priority", true)) {
								String priority = WSClient.getElementValue(queueResvRes,
										"AddReservationToQueueRS_QueueInfo_Priority", XMLType.RESPONSE);

								WSClient.setData("{var_qPriority}", priority);

								String query = WSClient.getQuery("QS_01");
								HashMap<String, String> dbResults = WSClient.getDBRow(query);

								if (dbResults.get("RESV_NAME_ID") == null) {
									WSClient.writeToReport(LogStatus.WARNING,
											"<b> Blocked : The Reservation is not pushed into queue.Not inserted into DB</b>");
								} else {

									// ******OWS Fetch Booking*******//

									OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
									String cancelBookingReq = WSClient.createSOAPMessage("OWSCancelBooking", "DS_01");
									String cancelBookingRes = WSClient.processSOAPMessage(cancelBookingReq);

									if (WSAssert.assertIfElementExists(cancelBookingRes,
											"CancelBookingResponse_Result_resultStatusFlag", false)) {
										if (WSAssert.assertIfElementValueEquals(cancelBookingRes,
												"CancelBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											String query1 = WSClient.getQuery("QS_05");
											String queueNo = WSClient.getDBRow(query1).get("QUEUE_PRIORITY");
											WSAssert.assertEquals("0", queueNo, false);
										}

									}
								}
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable add to queue");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to add to queue");
					}

				}

				else {
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to do reservation");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create profile");
			}
		}

		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

			// Cancel Reservation for reservation
			try {
				changeApplicationParameter("QUEUE_ROOMS", "N");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// @Test(groups = { "minimumRegression", "CancelBooking", "OWS" ,
	// "Reservation"})
	// public void cancelBooking_34567() {
	// HashMap<String, String> parameterStatus = new HashMap<String, String>();
	// try {
	// String testName = "cancelBooking_34567";
	// WSClient.startTest(testName,
	// "verify that rate plan information are coming correctly in fetch booking
	// response",
	// "minimumRegression");
	// String profileID = "";
	// if (OperaPropConfig.getPropertyConfigResults(
	// new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode",
	// "PaymentMethod" })) {
	// String resortOperaValue = OPERALib.getResort();
	// String chain = OPERALib.getChain();
	// WSClient.setData("{var_chain}", chain);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// String uname = OPERALib.getUserName();
	// String pwd = OPERALib.getPassword();
	// String channel = OWSLib.getChannel();
	// String channelType = OWSLib.getChannelType(channel);
	// String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsresort}", resort);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	// channel);
	//
	// WSClient.setData("{var_resort}", resortOperaValue);
	// OPERALib.setOperaHeader(uname);
	//
	// getApplicationParameter("AUTO_DEPOSIT_CANCELLATION_REFUND");
	// parameterStatus.put("AUTO_DEPOSIT_CANCELLATION_REFUND", Parameter);
	// if(!Parameter.equals("Y"))
	// {
	// changeApplicationParameter("AUTO_DEPOSIT_CANCELLATION_REFUND","Y");
	// }
	//
	// getApplicationParameter("CONFIRMATION_LETTER_CANCEL_RESERVATION");
	// parameterStatus.put("CONFIRMATION_LETTER_CANCEL_RESERVATION", Parameter);
	// if(!Parameter.equals("Y"))
	// {
	// changeApplicationParameter("CONFIRMATION_LETTER_CANCEL_RESERVATION","Y");
	// }
	//
	// getApplicationParameter("CANCEL_WITH_DEPOSIT");
	// parameterStatus.put("CANCEL_WITH_DEPOSIT", Parameter);
	// if(!Parameter.equals("Y"))
	// {
	// changeApplicationParameter("CANCEL_WITH_DEPOSIT","Y");
	// }
	// // Prerequisite 1 - create profile
	// if (!Parameter.equals("error")) {
	// if (profileID.equals(""))
	// profileID = CreateProfile.createProfile("DS_01");
	// if (!profileID.equals("error")) {
	// WSClient.setData("{var_profileId}", profileID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :
	// "+profileID+"</b>");
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_11"));
	// // WSClient.setData("{VAR_RATEPLANCODE}", "QA_SUITE20");
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_payment}",
	// OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	// // Prerequisite 2 Create Reservation
	// HashMap<String, String> resv =
	// CreateReservation.createReservation("DS_01");
	// String resvID = resv.get("reservationId");
	// if (!resvID.equals("error")) {
	// WSClient.setData("{var_resvId}", resvID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :
	// "+resvID+"</b>");
	//
	// // ******OWS Fetch Booking*******//
	// OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
	// channelCarrier);
	// String cancelBookingReq = WSClient.createSOAPMessage("OWSCancelBooking",
	// "DS_01");
	// String cancelBookingRes = WSClient.processSOAPMessage(cancelBookingReq);
	//
	//
	// if (WSAssert.assertIfElementExists(cancelBookingRes,
	// "CancelBookingResponse_Result_resultStatusFlag", false)) {
	// if (WSAssert.assertIfElementValueEquals(cancelBookingRes,
	// "CancelBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
	// // DB Validation
	// LinkedHashMap<String,String>
	// rateDetExp=WSClient.getDBRow(WSClient.getQuery("QS_40"));
	// LinkedHashMap<String,String> rateDetAct=new
	// LinkedHashMap<String,String>();
	// rateDetAct.put("RATE_CODE",
	// WSClient.getElementValue(cancelBookingRes,"RoomStay_RoomRates_RoomRate_ratePlanCode",XMLType.RESPONSE));
	// rateDetAct.put("DESCRIPTION",
	// WSClient.getElementValue(cancelBookingRes,"RatePlan_RatePlanDescription_Text",XMLType.RESPONSE));
	// WSAssert.assertEquals(rateDetExp, rateDetAct, false);
	// List<LinkedHashMap<String,String>> resValues=new
	// ArrayList<LinkedHashMap<String,String>>();
	// HashMap<String,String> xPath=new HashMap<String,String>();
	// xPath.put("RatePlan_AdditionalDetails_AdditionalDetail_detailType",
	// "RatePlan_AdditionalDetails_AdditionalDetail");
	// xPath.put("AdditionalDetail_AdditionalDetailDescription_Text",
	// "RatePlan_AdditionalDetails_AdditionalDetail");
	// resValues=WSClient.getMultipleNodeList(cancelBookingRes, xPath, true,
	// XMLType.RESPONSE);
	// String cancelDate=WSClient.getElementValue(cancelBookingRes,
	// "RatePlans_RatePlan_CancellationDateTime", XMLType.RESPONSE);
	// String depositAmount=WSClient.getElementValue(cancelBookingRes,
	// "RatePlan_DepositRequired_DepositAmount", XMLType.RESPONSE);
	// String dueDate=WSClient.getElementValue(cancelBookingRes,
	// "RatePlan_DepositRequired_DueDate", XMLType.RESPONSE);
	// String depositDueAmount=WSClient.getElementValue(cancelBookingRes,
	// "RatePlan_DepositRequired_DepositDueAmount", XMLType.RESPONSE);
	//
	// for(int i=0;i<resValues.size();i++){
	// if(resValues.get(i).get("DetailType").equals("CancelPolicy")){
	// resValues.get(i).put("CancellationDate",
	// cancelDate.substring(0,cancelDate.indexOf('T')));
	// resValues.get(i).put("CancellationTime",
	// cancelDate.substring(cancelDate.indexOf('T')+1,cancelDate.length()));
	// }
	// if(resValues.get(i).get("DetailType").equals("DepositPolicy")){
	// resValues.get(i).put("DepositAmount", depositAmount);
	// resValues.get(i).put("DepositDueAmount", depositDueAmount);
	// resValues.get(i).put("DueDate", dueDate);
	// }
	// }
	//
	// LinkedHashMap<String,String>
	// depositValues=WSClient.getDBRow(WSClient.getQuery("QS_39"));
	// LinkedHashMap<String,String>
	// cancelValues=WSClient.getDBRow(WSClient.getQuery("QS_38"));
	// depositValues.put("DepositDueAmount",WSClient.getDBRow(WSClient.getQuery("QS_41")).get("ROOM_DEPOSIT"));
	//
	// if(cancelValues.size()>0){
	// cancelValues.put("DetailType", "CancelPolicy");
	// }
	// if(cancelValues.containsKey("Text")){
	// cancelValues.put("Text", "Cancel By "+cancelValues.get("Text"));
	// }
	// if(depositValues.size()>0){
	// depositValues.put("DetailType", "DepositPolicy");
	// }
	// if(depositValues.containsKey("DepositAmount")&&depositValues.containsKey("Text")){
	// depositValues.put("Text", "A deposit of
	// "+depositValues.get("DepositAmount")+".00 is due by
	// "+depositValues.get("Text")+" in order to guarantee your reservation.");
	// }
	//
	// for(int i=0;i<resValues.size();i++){
	// if(resValues.get(i).get("DetailType").equals("CancelPolicy")){
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Cancellation
	// Policy</b>");
	// WSAssert.assertEquals(cancelValues, resValues.get(i), false);
	// }
	// if(resValues.get(i).get("DetailType").equals("DepositPolicy")){
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Deposit
	// Policy</b>");
	// WSAssert.assertEquals(depositValues, resValues.get(i), false);
	// }
	// }
	// }
	// }
	//// if(WSAssert.assertIfElementExists(cancelBookingRes,"FetchBookingResponse_faultcode",
	// true)){
	//// if(WSAssert.assertIfElementExists(cancelBookingRes,"FetchBookingResponse_faultstring",
	// true)){
	////
	//// String
	// message=WSClient.getElementValue(cancelBookingRes,"FetchBookingResponse_faultstring",
	// XMLType.RESPONSE);
	//// WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
	//// }
	//// }
	//
	// if (WSAssert.assertIfElementExists(cancelBookingRes,
	// "Result_Text_TextElement", true)) {
	// String message = WSClient.getElementValue(cancelBookingRes,
	// "Result_Text_TextElement",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>The text displayed in the response is :" + message+"</b>");
	// }
	//
	//// if(WSAssert.assertIfElementExists(cancelBookingRes,"FetchBookingResponse_Result_OperaErrorCode",
	// true)){
	////
	//// String code=WSClient.getElementValue(cancelBookingRes,
	// "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	//// WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in
	// the response is :"+ code+"</b>");
	//// }
	////
	//// if(WSAssert.assertIfElementExists(cancelBookingRes,"FetchBookingResponse_Result_GDSError",true)){
	////
	//// String message=WSClient.getElementValue(cancelBookingRes,
	// "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
	//// WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the
	// response is :"+ message+"</b>");
	//// }
	// }
	// }
	// }
	// }
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// // Cancel Reservation
	// try {
	// if(!WSClient.getData("{var_resvId}").equals(""))
	// CancelReservation.cancelReservation("DS_02");
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

}
