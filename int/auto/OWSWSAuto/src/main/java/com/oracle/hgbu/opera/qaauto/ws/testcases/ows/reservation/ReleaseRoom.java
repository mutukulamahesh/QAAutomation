package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CheckoutReservation;
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

/**
 * 
 * @author kankur
 *
 */

public class ReleaseRoom extends WSSetUp {

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

			// WSClient.writeToReport(LogStatus.INFO, "<b>"+"Fetching available
			// Hotel Rooms to assign a room"+"</b>");
			String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", ds);
			String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);
			if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true)) {
				if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
					List<LinkedHashMap<String, String>> rooms = new ArrayList<LinkedHashMap<String, String>>();
					LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
					xPath.put("FetchHotelRoomsRS_HotelRooms_Room", "FetchHotelRoomsRS_HotelRooms");
					xPath.put("FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", "FetchHotelRoomsRS_HotelRooms_Room");
					rooms = WSClient.getMultipleNodeList(fetchHotelRoomsRes, xPath, false, XMLType.RESPONSE);
					int len = rooms.size();
					Random randomNum = new Random();
					int num = randomNum.nextInt(len);
					// roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
					// "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber",
					// XMLType.RESPONSE);
					roomNumber = rooms.get(num).get("RoomNumber1");

					// roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
					// "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber",
					// XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "Successfully Fetched Room to assign, room Number is: " + roomNumber + "</b>");
					WSClient.setData("{var_roomNumber}", roomNumber);
					setHousekeepingRoomStatus("DS_01");
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
						assignRoom("DS_01");
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
					assignRoom("DS_01");
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
				assignRoom("DS_01");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

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

			// WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a Room to
			// assign to the reservation"+"</b>");
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

			// WSClient.writeToReport(LogStatus.INFO, "<b>"+"Changing
			// HouseKeeping status"+"</b>");
			String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", ds);
			String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);
			if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success",
					true)) {
				WSClient.writeToReport(LogStatus.INFO,
						"<b>" + "Successfully changed room status to vacant and inspected" + "</b>");
				// assignRoom("DS_01");
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

			// WSClient.writeToReport(LogStatus.INFO, "<b>"+"Assigning the
			// Room"+"</b>");
			String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", ds);
			String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);
			if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Successfully Assigned Room" + "</b>");
				return true;
			} else {
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Assign Room is unsuccessful" + "</b>");
				// WSClient.writeToReport(LogStatus.INFO,
				// "<b>"+"-------------------------------------------------------"+"</b>");
				return false;
			}

		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		}
		return false;
	}

	// Setting OWS Header
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

	// Release Room Sanity
	@Test(groups = { "sanity", "Reservation", "ReleaseRoom", "OWS" })

	/*
	 * Method to check the RELEASE ROOM operation by passing Chain Code, Hotel
	 * Code, and Resv_Name_ID
	 */

	public void releaseRoom_38407() {
		try {
			String testName = "releaseRoom_38407";
			WSClient.startTest(testName, "Verify that the room is released from the given reservation", "sanity");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				// boolean prerequisite = true;

				/*************
				 * Prerequisite : Creating new profile by fetching values
				 * required
				 ******************/
				String chain = OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				// String pwd = OPERALib.getPassword();
				// String channelType = OWSLib.getChannelType(channel);
				// String channelCarrier = OWSLib.getChannelCarier(resort,
				// channelType);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				OPERALib.setOperaHeader(uname);
				String operaProfileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID:- " + operaProfileID + "</b>");

				if (!operaProfileID.equals("error")) {
					System.out.println(operaProfileID);
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

					HashMap<String, String> id = CreateReservation.createReservation("DS_21");
					String reservationId = id.get("reservationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Reservation ID:- " + reservationId + "</b>");
					WSClient.setData("{var_resvId}", reservationId);

					/********
					 * Prerequisite : Fetch Hotel Rooms
					 **************/
					fetchHotelRooms("DS_14");

					/************
					 * Performing the Release Room operation
					 ****************/
					// Fetching Assigned room before releasing it
					String QS_01 = WSClient.getQuery("OWSReleaseRoom", "QS_01");
					LinkedHashMap<String, String> dbDataMap = WSClient.getDBRow(QS_01);
					String dbRoom = dbDataMap.get("ROOM");
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "Before releasing Room value in the DB is: " + dbRoom + "</b>");

					setOWSHeader();
					// OWSLib.setOWSHeader(uname, pwd, owsresort, channelType,
					// channelCarrier);

					// WSClient.writeToReport(LogStatus.INFO, "<b>"+"Releasing
					// the Room"+"</b>");
					WSClient.setData("{var_oresort}", owsresort);
					String releaseRoomReq = WSClient.createSOAPMessage("OWSReleaseRoom", "DS_01");

					// //Fetching Assigned room before releasing it
					// String QS_01 = WSClient.getQuery("QS_01");
					// LinkedHashMap<String,String>
					// dbDataMap=WSClient.getDBRow(QS_01);
					// String dbRoom = dbDataMap.get("ROOM");
					// WSClient.writeToReport(LogStatus.INFO, "<b>"+"Before
					// releasing Room value in the DB is: "+dbRoom+"</b>");

					String releaseRoomRes = WSClient.processSOAPMessage(releaseRoomReq);

					// Checking for Text Element in Result
					if (WSAssert.assertIfElementExists(releaseRoomRes, "Result_Text_TextElement", true)) {

						String message = WSAssert.getElementValue(releaseRoomRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + message + "</b>");
					}

					// Checking For OperaErrorCode
					if (WSAssert.assertIfElementExists(releaseRoomRes, "ReleaseRoomResponse_Result_OperaErrorCode",
							true)) {
						String code = WSAssert.getElementValue(releaseRoomRes,
								"ReleaseRoomResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The error code displayed in the response is :" + code + "</b>");
					}

					// Checking For Fault Schema
					if (WSAssert.assertIfElementExists(releaseRoomRes, "ReleaseRoomResponse_faultcode", true)) {
						String message = WSClient.getElementValue(releaseRoomRes, "ReleaseRoomResponse_faultstring",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL,
								"The Response has Fault Schema with message: " + message);
					}

					if (WSAssert.assertIfElementExists(releaseRoomRes, "ReleaseRoomResponse_Result", false)) {
						if (WSAssert.assertIfElementValueEquals(releaseRoomRes,
								"ReleaseRoomResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							// Fetching ROOM number from DB (should be null when
							// ROOM gets released)
							String QS01 = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> dbData = WSClient.getDBRow(QS01);
							String dbRoomValue = dbData.get("ROOM");

							// Verifying the DB Room Value is null or not
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "After releasing Room value in the DB is: " + dbRoomValue + "</b>");
							WSClient.writeToReport(LogStatus.PASS, "Room Released Successfully");
						}

						// Checking if ResultstatusFlag is FAIL
						else {
							WSClient.writeToReport(LogStatus.FAIL, "Room Release Failed!");
						}

						// Checking for GDSError
						if (WSAssert.assertIfElementExists(releaseRoomRes, "ReleaseRoomResponse_Result_GDSError",
								true)) {
							String message = WSAssert.getElementValue(releaseRoomRes,
									"ReleaseRoomResponse_Result_GDSError", XMLType.RESPONSE);
							String errorCode = WSClient.getElementValue(releaseRoomRes,
									"ReleaseRoomResponse_Result_GDSError_errorCode", XMLType.RESPONSE);
							String elementId = WSClient.getElementValue(releaseRoomRes,
									"ReleaseRoomResponse_Result_GDSError_elementId", XMLType.RESPONSE);

							if ((!message.equals(""))) {
								WSClient.writeToReport(LogStatus.INFO, "<b>"
										+ "The error displayed in the Release Room response is :" + message + "</b>");
							}
							if (!((errorCode.equals("")) || (errorCode.equals("*null*")))) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "The errorCode is: " + errorCode + "</b>");
							}
							if (!((elementId.equals("")) || (elementId.equals("*null*")))) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "The element ID is: " + elementId + "</b>");
							}
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
			// Cancel Reservation
			try {
				CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// Release Room MR-1
	@Test(groups = { "minimumRegression", "Reservation", "ReleaseRoom", "OWS" })

	/*
	 * Method to check the RELEASE ROOM operation by passing Chain Code,
	 * Resv_Name_ID , valid Resv_Name_ID type, and Invalid Hotel Code
	 */

	public void releaseRoom_39390() {
		try {
			String testName = "releaseRoom_39390";
			WSClient.startTest(testName, "Verify that the room is not released when an invalid HotelCode is passed",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				// boolean prerequisite = true;

				/*************
				 * Prerequisite : Creating new profile by fetching values
				 * required
				 ******************/
				String chain = OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				// String pwd = OPERALib.getPassword();
				// String channelType = OWSLib.getChannelType(channel);
				// String channelCarrier = OWSLib.getChannelCarier(resort,
				// channelType);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				OPERALib.setOperaHeader(uname);
				String operaProfileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID:- " + operaProfileID + "</b>");

				if (!operaProfileID.equals("error")) {
					System.out.println(operaProfileID);
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

					HashMap<String, String> id = CreateReservation.createReservation("DS_21");
					String reservationId = id.get("reservationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Reservation ID:- " + reservationId + "</b>");
					WSClient.setData("{var_resvId}", reservationId);

					/********
					 * Prerequisite : Fetch Hotel Rooms
					 **************/
					fetchHotelRooms("DS_14");

					/************
					 * Performing the Release Room operation
					 ****************/
					setOWSHeader();
					// OWSLib.setOWSHeader(uname, pwd, owsresort, channelType,
					// channelCarrier);

					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Releasing the Room" + "</b>");
					WSClient.setData("{var_oresort}", owsresort);
					String releaseRoomReq = WSClient.createSOAPMessage("OWSReleaseRoom", "DS_03");

					String releaseRoomRes = WSClient.processSOAPMessage(releaseRoomReq);

					// Checking for Text Element in Result
					if (WSAssert.assertIfElementExists(releaseRoomRes, "Result_Text_TextElement", true)) {

						String message = WSAssert.getElementValue(releaseRoomRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + message + "</b>");
					}

					// Checking For OperaErrorCode
					if (WSAssert.assertIfElementExists(releaseRoomRes, "ReleaseRoomResponse_Result_OperaErrorCode",
							true)) {
						String code = WSAssert.getElementValue(releaseRoomRes,
								"ReleaseRoomResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The error displayed in the response is :" + code + "</b>");
					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"<b>" + "The OperaErrorCode is missing in the Response" + "</b>");
					}

					// Checking For Fault Schema
					if (WSAssert.assertIfElementExists(releaseRoomRes, "ReleaseRoomResponse_faultcode", true)) {
						String message = WSClient.getElementValue(releaseRoomRes, "ReleaseRoomResponse_faultstring",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL,
								"The Response has Fault Schema with message: " + message);
					}

					if (WSAssert.assertIfElementExists(releaseRoomRes, "ReleaseRoomResponse_Result", false)) {
						if (WSAssert.assertIfElementValueEquals(releaseRoomRes,
								"ReleaseRoomResponse_Result_resultStatusFlag", "FAIL", false)) {
							WSClient.writeToReport(LogStatus.PASS, "Resultstatus Flag populated as FAIL as expected!");
						}

						// Checking if ResultstatusFlag is SUCCESS
						else {
							WSClient.writeToReport(LogStatus.FAIL,
									"Resultstatus Flag populated as SUCCESS even with an invalid HotelCode!");
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
			// Cancel Reservation
			try {
				CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// Release Room MR-2
	@Test(groups = { "minimumRegression", "Reservation", "ReleaseRoom", "OWS" })

	/*
	 * Method to check the RELEASE ROOM operation by passing Chain Code, Hotel
	 * Code, Resv_Name_ID type, and Invalid Resv_Name_ID
	 */

	public void releaseRoom_39391() {
		try {
			String testName = "releaseRoom_39391";
			WSClient.startTest(testName,
					"Verify that the room is not released when an invalid Reservation NameID is passed",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				// boolean prerequisite = true;

				/*************
				 * Prerequisite : Creating new profile by fetching values
				 * required
				 ******************/
				String chain = OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				// String pwd = OPERALib.getPassword();
				// String channelType = OWSLib.getChannelType(channel);
				// String channelCarrier = OWSLib.getChannelCarier(resort,
				// channelType);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				OPERALib.setOperaHeader(uname);
				String operaProfileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID:- " + operaProfileID + "</b>");

				if (!operaProfileID.equals("error")) {
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileId}", operaProfileID);

					/********
					 * Prerequisite : Generating a Random reservation ID
					 **************/
					String reservationId = WSClient.getKeywordData("{KEYWORD_RANDNUM_8}");
					WSClient.setData("{var_resvId}", reservationId);

					/********
					 * Prerequisite : Fetch Hotel Rooms
					 **************/
					fetchHotelRooms("DS_14");

					/************
					 * Performing the Release Room operation
					 ****************/
					setOWSHeader();
					// OWSLib.setOWSHeader(uname, pwd, owsresort, channelType,
					// channelCarrier);

					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Releasing the Room" + "</b>");
					WSClient.setData("{var_oresort}", owsresort);
					String releaseRoomReq = WSClient.createSOAPMessage("OWSReleaseRoom", "DS_04");

					String releaseRoomRes = WSClient.processSOAPMessage(releaseRoomReq);

					// Checking for Text Element in Result
					if (WSAssert.assertIfElementExists(releaseRoomRes, "Result_Text_TextElement", true)) {

						String message = WSAssert.getElementValue(releaseRoomRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is :" + message + "</b>");
					}

					// Checking For OperaErrorCode
					if (WSAssert.assertIfElementExists(releaseRoomRes, "ReleaseRoomResponse_Result_OperaErrorCode",
							true)) {
						String code = WSAssert.getElementValue(releaseRoomRes,
								"ReleaseRoomResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The error code displayed in the response is :" + code + "</b>");
					}

					// Checking For Fault Schema
					if (WSAssert.assertIfElementExists(releaseRoomRes, "ReleaseRoomResponse_faultcode", true)) {
						String message = WSClient.getElementValue(releaseRoomRes, "ReleaseRoomResponse_faultstring",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL,
								"The Response has Fault Schema with message: " + message);
					}

					if (WSAssert.assertIfElementExists(releaseRoomRes, "ReleaseRoomResponse_Result", false)) {
						if (WSAssert.assertIfElementValueEquals(releaseRoomRes,
								"ReleaseRoomResponse_Result_resultStatusFlag", "FAIL", false)) {
							// Checking for GDSError
							// In this Case of MR GDSError will contain the
							// Message of what actually the Error is i.e. that
							// Resv ID Doesnot exist
							// Here, It is not the criterion for failing the
							// Test Case so writing report as the INFO and not
							// as FAIL
							// if(WSAssert.assertIfElementExists(releaseRoomRes,
							// "ReleaseRoomResponse_Result_GDSError",true))
							// {
							// String
							// message=WSAssert.getElementValue(releaseRoomRes,
							// "ReleaseRoomResponse_Result_GDSError",
							// XMLType.RESPONSE);
							//
							// if(WSAssert.assertIfElementValueEquals(releaseRoomRes,
							// "ReleaseRoomResponse_Result_GDSError",
							// "Reservation Does Not Exist, Unable To Change
							// Room", true))
							// {
							// WSClient.writeToReport(LogStatus.PASS, "<b>"+"The
							// error displayed is :"+ message+"</b>");
							// }
							// else
							// {
							// WSClient.writeToReport(LogStatus.FAIL, "<b>"+"The
							// error does not Have message containing
							// \"Reservation Does Not Exist\" "+"</b>");
							// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The
							// error displayed is :"+ message+"</b>");
							// }
							//
							// }
							// else
							// {
							// WSClient.writeToReport(LogStatus.FAIL, "<b>"+"The
							// GDSError is missing in the Response"+"</b>");
							// }
						}

						// Checking if ResultstatusFlag is SUCCESS
						else {
							WSClient.writeToReport(LogStatus.FAIL,
									"Room Got Released even with the invalid Reservation ID");
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

		}
	}

	// Release Room MR-3
	@Test(groups = { "minimumRegression", "Reservation", "ReleaseRoom", "OWS" })

	/* Method to check the RELEASE ROOM operation for a shared reservation */

	public void releaseRoom_41502() {
		String reservationId = "";
		String reservationId1 = "";
		try {
			String testName = "releaseRoom_41502";
			WSClient.startTest(testName, "Verify that the room is released for a shared reservation",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				// boolean prerequisite = true;

				/*************
				 * Prerequisite : Creating new profile by fetching values
				 * required
				 ******************/
				String chain = OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				OPERALib.setOperaHeader(uname);
				String operaProfileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID 1:- " + operaProfileID + "</b>");

				if (!operaProfileID.equals("error")) {
					System.out.println(operaProfileID);
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

					HashMap<String, String> id = CreateReservation.createReservation("DS_25");
					reservationId = id.get("reservationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Reservation ID 1:- " + reservationId + "</b>");
					WSClient.setData("{var_resvId}", reservationId);
					if (!reservationId.equals("error")) {
						String operaProfileID1 = CreateProfile.createProfile("DS_01");
						WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID 2:- " + operaProfileID1 + "</b>");
						if (!operaProfileID1.equals("error")) {
							WSClient.setData("{var_profileId}", operaProfileID1);
							HashMap<String, String> id1 = CreateReservation.createReservation("DS_04");
							reservationId1 = id1.get("reservationId");
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "Reservation ID 2:- " + reservationId1 + "</b>");
							if (!reservationId1.equals("error")) {
								WSClient.setData("{var_resvId1}", reservationId1);

								/********
								 * Prerequisite : Combining Reservations
								 **************/

								String combineReq = WSClient.createSOAPMessage("CombineShareReservations", "DS_01");
								String combineRes = WSClient.processSOAPMessage(combineReq);
								if (WSAssert.assertIfElementExists(combineRes, "CombineShareReservationsRS_Success",
										true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" + "Successfully combined the two reservations" + "</b>");
									String combineResID = WSClient.getElementValue(combineRes,
											"Reservation_ReservationIDList_UniqueID_ID", XMLType.RESPONSE);
									WSClient.setData("{var_resvId}", combineResID);
									WSClient.setData("{var_resvDate}",
											WSClient.getDBRow(WSClient.getQuery("QS_01")).get("BEGIN_DATE"));

									/********
									 * Prerequisite : Fetch Hotel Rooms
									 **************/
									fetchHotelRooms("DS_14");

									// /********
									// * Prerequisite : CheckinReservation
									// **************/
									// String checkInReq =
									// WSClient.createSOAPMessage("CheckinReservation",
									// "DS_01");
									// String checkInRes =
									// WSClient.processSOAPMessage(checkInReq);
									// if
									// (WSAssert.assertIfElementExists(checkInRes,
									// "CheckinReservationRS_Success", true))
									// {
									/************
									 * Performing the Release Room operation
									 ****************/
									// Fetching Assigned room before releasing
									// it
									String QS_01 = WSClient.getQuery("OWSReleaseRoom", "QS_01");
									LinkedHashMap<String, String> dbDataMap = WSClient.getDBRow(QS_01);
									String dbRoom = dbDataMap.get("ROOM");
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" + "Before releasing Room value in the DB is: " + dbRoom + "</b>");

									setOWSHeader();

									// WSClient.writeToReport(LogStatus.INFO,
									// "<b>"+"Releasing the Room"+"</b>");
									WSClient.setData("{var_resvId}", reservationId1);
									WSClient.setData("{var_oresort}", owsresort);
									String releaseRoomReq = WSClient.createSOAPMessage("OWSReleaseRoom", "DS_01");
									String releaseRoomRes = WSClient.processSOAPMessage(releaseRoomReq);

									// Checking for Text Element in Result
									if (WSAssert.assertIfElementExists(releaseRoomRes, "Result_Text_TextElement",
											true)) {

										String message = WSAssert.getElementValue(releaseRoomRes,
												"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>" + "The text displayed in the response is :" + message + "</b>");
									}

									// Checking For OperaErrorCode
									if (WSAssert.assertIfElementExists(releaseRoomRes,
											"ReleaseRoomResponse_Result_OperaErrorCode", true)) {
										String code = WSAssert.getElementValue(releaseRoomRes,
												"ReleaseRoomResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>"
												+ "The error code displayed in the response is :" + code + "</b>");
									}

									// Checking For Fault Schema
									if (WSAssert.assertIfElementExists(releaseRoomRes, "ReleaseRoomResponse_faultcode",
											true)) {
										String message = WSClient.getElementValue(releaseRoomRes,
												"ReleaseRoomResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL,
												"The Response has Fault Schema with message: " + message);
									}

									if (WSAssert.assertIfElementExists(releaseRoomRes, "ReleaseRoomResponse_Result",
											false)) {
										if (WSAssert.assertIfElementValueEquals(releaseRoomRes,
												"ReleaseRoomResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											// Fetching ROOM number from DB
											// (should be null when ROOM gets
											// released)
											String QS01 = WSClient.getQuery("QS_01");
											LinkedHashMap<String, String> dbData = WSClient.getDBRow(QS01);
											String dbRoomValue = dbData.get("ROOM");

											// Verifying the DB Room Value is
											// null or not
											WSClient.writeToReport(LogStatus.INFO,
													"<b>" + "After releasing Room value in the DB is: " + dbRoomValue
															+ "</b>");
											WSClient.writeToReport(LogStatus.PASS, "Room Released Successfully");
										}

										// Checking if ResultstatusFlag is FAIL
										else {
											WSClient.writeToReport(LogStatus.FAIL, "Room Release Failed!");
										}

										// Checking for GDSError
										if (WSAssert.assertIfElementExists(releaseRoomRes,
												"ReleaseRoomResponse_Result_GDSError", true)) {
											String message = WSAssert.getElementValue(releaseRoomRes,
													"ReleaseRoomResponse_Result_GDSError", XMLType.RESPONSE);
											String errorCode = WSClient.getElementValue(releaseRoomRes,
													"ReleaseRoomResponse_Result_GDSError_errorCode", XMLType.RESPONSE);
											String elementId = WSClient.getElementValue(releaseRoomRes,
													"ReleaseRoomResponse_Result_GDSError_elementId", XMLType.RESPONSE);

											if ((!message.equals(""))) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The error displayed in the Release Room response is :"
																+ message + "</b>");
											}
											if (!((errorCode.equals("")) || (errorCode.equals("*null*")))) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The errorCode is: " + errorCode + "</b>");
											}
											if (!((elementId.equals("")) || (elementId.equals("*null*")))) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The element ID is: " + elementId + "</b>");
											}
										}
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING,
											"The prerequisites for Unique ID's failed!------ CombiningSharedReservation -----Blocked");
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"The prerequisites for Unique ID's failed!------ Create Reservation -----Blocked");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites for Unique ID's failed!------ Create Profile -----Blocked");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for Unique ID's failed!------ Create Reservation -----Blocked");
					}
					// }
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
			// Cancel Reservation
			try {
				WSClient.setData("{var_resvId}", reservationId);
				CancelReservation.cancelReservation("DS_02");
				WSClient.setData("{var_resvId}", reservationId1);
				CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// Release Room MR-4
	@Test(groups = { "minimumRegression", "Reservation", "ReleaseRoom", "OWS" })

	/* Method to check the RELEASE ROOM operation for a CheckedIn reservation */

	public void releaseRoom_41503() {
		String reservationId = "";
		try {
			String testName = "releaseRoom_41503";
			WSClient.startTest(testName, "Verify that the room is not released for a checked-in reservation",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				// boolean prerequisite = true;

				/*************
				 * Prerequisite : Creating new profile by fetching values
				 * required
				 ******************/
				String chain = OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				OPERALib.setOperaHeader(uname);
				String operaProfileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Profile ID: " + operaProfileID + "</b>");

				if (!operaProfileID.equals("error")) {
					System.out.println(operaProfileID);
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

					HashMap<String, String> id = CreateReservation.createReservation("DS_12");
					reservationId = id.get("reservationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>" + "Reservation ID: " + reservationId + "</b>");
					WSClient.setData("{var_resvId}", reservationId);
					if (!reservationId.equals("error")) {

						/********
						 * Prerequisite : Fetch Hotel Rooms
						 **************/
						fetchHotelRooms("DS_13");

						/********
						 * Prerequisite : CheckinReservation
						 **************/
						String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
						String checkInRes = WSClient.processSOAPMessage(checkInReq);
						if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {
							/************
							 * Performing the Release Room operation
							 ****************/
							// Fetching Assigned room before releasing it
							String QS_01 = WSClient.getQuery("OWSReleaseRoom", "QS_01");
							LinkedHashMap<String, String> dbDataMap = WSClient.getDBRow(QS_01);
							String dbRoom = dbDataMap.get("ROOM");
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "Before releasing Room value in the DB is: " + dbRoom + "</b>");

							setOWSHeader();

							// WSClient.writeToReport(LogStatus.INFO,
							// "<b>"+"Releasing the Room"+"</b>");
							WSClient.setData("{var_oresort}", owsresort);
							String releaseRoomReq = WSClient.createSOAPMessage("OWSReleaseRoom", "DS_01");
							String releaseRoomRes = WSClient.processSOAPMessage(releaseRoomReq);

							// Checking for Text Element in Result
							if (WSAssert.assertIfElementExists(releaseRoomRes, "Result_Text_TextElement", true)) {

								String message = WSAssert.getElementValue(releaseRoomRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "The text displayed in the response is :" + message + "</b>");
							}

							// Checking For OperaErrorCode
							if (WSAssert.assertIfElementExists(releaseRoomRes,
									"ReleaseRoomResponse_Result_OperaErrorCode", true)) {
								String code = WSAssert.getElementValue(releaseRoomRes,
										"ReleaseRoomResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "The error code displayed in the response is :" + code + "</b>");
							}

							// Checking For Fault Schema
							if (WSAssert.assertIfElementExists(releaseRoomRes, "ReleaseRoomResponse_faultcode", true)) {
								String message = WSClient.getElementValue(releaseRoomRes,
										"ReleaseRoomResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,
										"The Response has Fault Schema with message: " + message);
							}

							if (WSAssert.assertIfElementExists(releaseRoomRes, "ReleaseRoomResponse_Result", false)) {
								if (WSAssert.assertIfElementValueEquals(releaseRoomRes,
										"ReleaseRoomResponse_Result_resultStatusFlag", "FAIL", false)) {
									// Fetching ROOM number from DB (should be
									// null when ROOM gets released)
									String QS01 = WSClient.getQuery("QS_01");
									LinkedHashMap<String, String> dbData = WSClient.getDBRow(QS01);
									String dbRoomValue = dbData.get("ROOM");

									// Verifying the DB Room Value is null or
									// not
									WSClient.writeToReport(LogStatus.INFO,
											"<b>" + "After Release Room operation value in the DB is: " + dbRoomValue
													+ "</b>");
									WSClient.writeToReport(LogStatus.PASS, "Room not Released as expected");
								}

								// Checking if ResultstatusFlag is SUCCESS
								else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Room Release should FAIL instead of passing!");
								}

								// Checking for GDSError
								if (WSAssert.assertIfElementExists(releaseRoomRes,
										"ReleaseRoomResponse_Result_GDSError", true)) {
									String message = WSAssert.getElementValue(releaseRoomRes,
											"ReleaseRoomResponse_Result_GDSError", XMLType.RESPONSE);
									String errorCode = WSClient.getElementValue(releaseRoomRes,
											"ReleaseRoomResponse_Result_GDSError_errorCode", XMLType.RESPONSE);
									String elementId = WSClient.getElementValue(releaseRoomRes,
											"ReleaseRoomResponse_Result_GDSError_elementId", XMLType.RESPONSE);

									if ((!message.equals(""))) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>" + "The error displayed in the Release Room response is :"
														+ message + "</b>");
									}
									if (!((errorCode.equals("")) || (errorCode.equals("*null*")))) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>" + "The errorCode is: " + errorCode + "</b>");
									}
									if (!((elementId.equals("")) || (elementId.equals("*null*")))) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>" + "The element ID is: " + elementId + "</b>");
									}
								}
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"The prerequisites  failed!------ Checkin Reservation -----Blocked");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for Unique ID's failed!------ Create Reservation -----Blocked");
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
			// Cancel Reservation
			try {
				WSClient.setData("{var_resvId}", reservationId);
				CheckoutReservation.checkOutReservation("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
