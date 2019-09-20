package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

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

public class DeleteAccompanyGuest extends WSSetUp {

	// Total Test Cases : 11

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

			reservationId = CreateReservation.createReservation(ds).get("reservationId");
			WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + reservationId + "</b>");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		return reservationId;
	}

	// Fetch Hotel Rooms
	public void fetchHotelRooms(String ds) {
		String roomNumber;
		try {
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_04"));
			WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_04"));

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
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "No Room available so Preparing to create a new Room" + "</b>");
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
			WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_04"));

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

				return false;
			}

		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		}
		return false;
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
		String resortOperaValue = OPERALib.getResort();
		String chain = OPERALib.getChain();
		WSClient.setData("{var_resort}", resortOperaValue);
		WSClient.setData("{var_chain}", chain);
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
	}

	// Sanity Test Case :1
	@Test(groups = { "sanity", "Reservation", "Invoice", "OWS" })
	public void deleteAccompanyGuest_39860() {
		try {
			String testname = "deleteAccompanyGuest_39860";
			WSClient.startTest(testname,
					"Verify that an Accompany Guest associated to a reservation is deleted by passing profile id of accompany guest",
					"sanity");

			setOperaHeader();

			// Setting ACCOMPANYING_GUEST Parameter
			WSClient.setData("{var_parameter}", "ACCOMPANYING_GUEST");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter1.equals("Y")) {
				Parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId1, UniqueId2, resvID;

				// Creating a profile of Accompanying Guest
				if (!(UniqueId1 = createProfile("DS_01")).equals("error")) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Successfully created profile of Accompanying guest.</b>");
					// Creating a profile of primary Guest
					if (!(UniqueId2 = createProfile("DS_01")).equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created profile of primary guest.</b>");
						// Creating a reservation for the real Guest
						if (!(resvID = createReservation("DS_04")).equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Successfully created reservation for primary guest.</b>");
							WSClient.setData("{var_profileId2}", UniqueId1);
							WSClient.setData("{var_profileId1}", UniqueId2);
							WSClient.setData("{var_profileId}", UniqueId1);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_reservation_id}", resvID);

							String query = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");

							LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
							temp = WSClient.getDBRow(query);

							// Setting firstname and lastname of Accompanying
							// Guest
							WSClient.setData("{var_firstName}", temp.get("FIRST"));
							WSClient.setData("{var_lastName}", temp.get("LAST"));

							// Adding Accompanying Guest to the above
							// reservation
							String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_01");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							if (WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success",
									true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Successfully associated accompany guest with the primary guest reservation.</b>");
								setOWSHeader();

								String query3 = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_02");
								LinkedHashMap<String, String> temp3 = new LinkedHashMap<String, String>();
								temp3 = WSClient.getDBRow(query3);
								WSClient.setData("{var_confirmNo}", temp3.get("CONFIRMATION_NO"));

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								// Creating request and processing response for
								// OWS DeleteAccompanyguest Operation
								String deleteAccompanyGuestReq = WSClient.createSOAPMessage("OWSDeleteAccompanyGuest",
										"DS_01");
								String deleteAccompanyGuestRes = WSClient.processSOAPMessage(deleteAccompanyGuestReq);

								// Validating response of OWS
								// DeleteAccompanyguest Operation
								if (WSAssert.assertIfElementExists(deleteAccompanyGuestRes,
										"DeleteAccompanyGuestResponse_Result_resultStatusFlag", false)) {
									if (WSAssert.assertIfElementValueEquals(deleteAccompanyGuestRes,
											"DeleteAccompanyGuestResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										// Database Validation
										LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
										String query_n = WSClient.getQuery("QS_03");
										db = WSClient.getDBRow(query_n);

										if (Integer.valueOf(db.get("count")).intValue() < 1) {
											WSClient.writeToReport(LogStatus.PASS,
													"Record Deleted---------Test Passed");
										} else {
											WSClient.writeToReport(LogStatus.FAIL,
													"Record not deleted------Test Failed");
										}

									} else {
										if (WSAssert.assertIfElementExists(deleteAccompanyGuestRes,
												"DeleteAccompanyGuestResponse_Result_GDSError", true)) {
											String message = WSAssert.getElementValue(deleteAccompanyGuestRes,
													"DeleteAccompanyGuestResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"The gds error displayed in the deleteAccomapnyGuest response is :"
															+ message);
										}
									}
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisites failed!------ChangeReservation-----Blocked");
							}
							CancelReservation.cancelReservation("DS_02");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :1
	@Test(groups = { "minimumRegression", "Reservation", "Invoice", "OWS" })
	public void deleteAccompanyGuest_41080() {
		try {
			String testname = "deleteAccompanyGuest_41080";
			WSClient.startTest(testname,
					"Verify that error message is obtained when deleting non-existing accompany guest by giving random profileid",
					"minimumRegression");

			setOperaHeader();

			// Setting ACCOMPANYING_GUEST Parameter
			WSClient.setData("{var_parameter}", "ACCOMPANYING_GUEST");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter1.equals("Y")) {
				Parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId1, UniqueId2, resvID;

				// Creating a profile of Accompanying Guest
				if (!(UniqueId1 = createProfile("DS_01")).equals("error")) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Successfully created profile of Accompanying guest.</b>");
					// Creating a profile of real Guest
					if (!(UniqueId2 = createProfile("DS_01")).equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created profile of primary guest.</b>");
						// Creating a reservation for the real Guest
						if (!(resvID = createReservation("DS_04")).equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Successfully created reservation for primary guest.</b>");
							WSClient.setData("{var_profileId2}", UniqueId1);
							WSClient.setData("{var_profileId1}", UniqueId2);
							WSClient.setData("{var_profileId}", UniqueId2);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_reservation_id}", resvID);

							String query = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");
							LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
							temp = WSClient.getDBRow(query);

							// Setting firstname and lastname of Accompanying
							// Guest
							WSClient.setData("{var_firstName}", temp.get("FIRST"));
							WSClient.setData("{var_lastName}", temp.get("LAST"));

							// Adding Accompanying Guest to the above
							// reservation
							String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_01");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							if (WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success",
									true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Successfully associated accompany guest with the primary guest reservation.</b>");
								setOWSHeader();
								WSClient.setData("{var_profileId}", UniqueId1);
								String query2 = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");
								LinkedHashMap<String, String> temp2 = new LinkedHashMap<String, String>();
								temp2 = WSClient.getDBRow(query2);
								WSClient.setData("{var_firstName}", temp2.get("FIRST"));
								WSClient.setData("{var_lastName}", temp2.get("LAST"));

								String query3 = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_02");
								LinkedHashMap<String, String> temp3 = new LinkedHashMap<String, String>();
								temp3 = WSClient.getDBRow(query3);
								WSClient.setData("{var_confirmNo}", temp3.get("CONFIRMATION_NO"));

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								// Creating request and processing response for
								// OWS DeleteAccompanyguest Operation
								String deleteAccompanyGuestReq = WSClient.createSOAPMessage("OWSDeleteAccompanyGuest",
										"DS_03");
								String deleteAccompanyGuestRes = WSClient.processSOAPMessage(deleteAccompanyGuestReq);

								// Validating response of OWS
								// DeleteAccompanyguest Operation
								if (WSAssert.assertIfElementExists(deleteAccompanyGuestRes,
										"DeleteAccompanyGuestResponse_Result_resultStatusFlag", true)) {
									WSAssert.assertIfElementValueEquals(deleteAccompanyGuestRes,
											"DeleteAccompanyGuestResponse_Result_resultStatusFlag", "FAIL", false);

									/*
									 * if(WSAssert.assertIfElementExists(
									 * deleteAccompanyGuestRes,
									 * "DeleteAccompanyGuestResponse_Result_GDSError",
									 * false)) {
									 * if(WSAssert.assertIfElementContains(
									 * deleteAccompanyGuestRes,
									 * "DeleteAccompanyGuestResponse_Result_GDSError",
									 * "Invalid Name Id or Accompany guest not found for reservation"
									 * , false)) {
									 *
									 * } }
									 */
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisites failed!------ChangeReservation-----Blocked");
							}
							CancelReservation.cancelReservation("DS_02");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :2
	@Test(groups = { "minimumRegression", "Reservation", "Invoice", "OWS" })
	public void deleteAccompanyGuest_41081() {
		try {
			String testname = "deleteAccompanyGuest_41081";
			WSClient.startTest(testname,
					"Verify that error message is obtained when deleting accompany guest by passing primary guest profileid",
					"minimumRegression");

			setOperaHeader();

			// Setting ACCOMPANYING_GUEST Parameter
			WSClient.setData("{var_parameter}", "ACCOMPANYING_GUEST");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter1.equals("Y")) {
				Parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId1, UniqueId2, resvID;

				// Creating a profile of Accompanying Guest
				if (!(UniqueId1 = createProfile("DS_01")).equals("error")) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Successfully created profile of Accompanying guest.</b>");
					// Creating a profile of real Guest
					if (!(UniqueId2 = createProfile("DS_01")).equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created profile of primary guest.</b>");
						// Creating a reservation for the real Guest
						if (!(resvID = createReservation("DS_04")).equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Successfully created reservation for primary guest.</b>");
							WSClient.setData("{var_profileId2}", UniqueId1);
							WSClient.setData("{var_profileId1}", UniqueId2);
							WSClient.setData("{var_profileId}", UniqueId2);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_reservation_id}", resvID);

							String query = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");
							LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
							temp = WSClient.getDBRow(query);

							// Setting firstname and lastname of Accompanying
							// Guest
							WSClient.setData("{var_firstName}", temp.get("FIRST"));
							WSClient.setData("{var_lastName}", temp.get("LAST"));

							// Adding Accompanying Guest to the above
							// reservation
							String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_01");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							if (WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success",
									true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Successfully associated accompany guest with the primary guest reservation.</b>");
								setOWSHeader();

								String query2 = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");
								LinkedHashMap<String, String> temp2 = new LinkedHashMap<String, String>();
								temp2 = WSClient.getDBRow(query2);
								WSClient.setData("{var_firstName}", temp2.get("FIRST"));
								WSClient.setData("{var_lastName}", temp2.get("LAST"));

								String query3 = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_02");
								LinkedHashMap<String, String> temp3 = new LinkedHashMap<String, String>();
								temp3 = WSClient.getDBRow(query3);
								WSClient.setData("{var_confirmNo}", temp3.get("CONFIRMATION_NO"));

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								// Creating request and processing response for
								// OWS DeleteAccompanyguest Operation
								String deleteAccompanyGuestReq = WSClient.createSOAPMessage("OWSDeleteAccompanyGuest",
										"DS_02");
								String deleteAccompanyGuestRes = WSClient.processSOAPMessage(deleteAccompanyGuestReq);

								// Validating response of OWS
								// DeleteAccompanyguest Operation
								if (WSAssert.assertIfElementExists(deleteAccompanyGuestRes,
										"DeleteAccompanyGuestResponse_Result_resultStatusFlag", true)) {
									WSAssert.assertIfElementValueEquals(deleteAccompanyGuestRes,
											"DeleteAccompanyGuestResponse_Result_resultStatusFlag", "FAIL", false);

									/*
									 * if(WSAssert.assertIfElementExists(
									 * deleteAccompanyGuestRes,
									 * "DeleteAccompanyGuestResponse_Result_GDSError",
									 * false)) {
									 * if(WSAssert.assertIfElementContains(
									 * deleteAccompanyGuestRes,
									 * "DeleteAccompanyGuestResponse_Result_GDSError",
									 * "Invalid Name Id or Accompany guest not found for reservation"
									 * , false)) {
									 *
									 * } }
									 */
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisites failed!------ChangeReservation-----Blocked");
							}
							CancelReservation.cancelReservation("DS_02");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :3
	@Test(groups = { "minimumRegression", "Reservation", "Invoice", "OWS" })
	public void deleteAccompanyGuest_41082() {
		try {
			String testname = "deleteAccompanyGuest_41082";
			WSClient.startTest(testname,
					"Verify that error message is obtained when deleting accompany guest by passing primary guest firstname and lastname",
					"minimumRegression");

			setOperaHeader();

			// Setting ACCOMPANYING_GUEST Parameter
			WSClient.setData("{var_parameter}", "ACCOMPANYING_GUEST");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter1.equals("Y")) {
				Parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId1, UniqueId2, resvID;

				// Creating a profile of Accompanying Guest
				if (!(UniqueId1 = createProfile("DS_01")).equals("error")) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Successfully created profile of Accompanying guest.</b>");
					// Creating a profile of real Guest
					if (!(UniqueId2 = createProfile("DS_01")).equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created profile of primary guest.</b>");
						// Creating a reservation for the real Guest
						if (!(resvID = createReservation("DS_04")).equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Successfully created reservation for primary guest.</b>");
							WSClient.setData("{var_profileId2}", UniqueId1);
							WSClient.setData("{var_profileId1}", UniqueId2);
							WSClient.setData("{var_profileId}", UniqueId2);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_reservation_id}", resvID);

							String query = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");
							LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
							temp = WSClient.getDBRow(query);

							// Setting firstname and lastname of Accompanying
							// Guest
							WSClient.setData("{var_firstName}", temp.get("FIRST"));
							WSClient.setData("{var_lastName}", temp.get("LAST"));

							// Adding Accompanying Guest to the above
							// reservation
							String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_01");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							if (WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success",
									true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Successfully associated accompany guest with the primary guest reservation.</b>");
								setOWSHeader();
								String query2 = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");
								LinkedHashMap<String, String> temp2 = new LinkedHashMap<String, String>();
								temp2 = WSClient.getDBRow(query2);
								WSClient.setData("{var_firstName}", temp2.get("FIRST"));
								WSClient.setData("{var_lastName}", temp2.get("LAST"));

								String query3 = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_02");
								LinkedHashMap<String, String> temp3 = new LinkedHashMap<String, String>();
								temp3 = WSClient.getDBRow(query3);
								WSClient.setData("{var_confirmNo}", temp3.get("CONFIRMATION_NO"));

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								// Creating request and processing response for
								// OWS DeleteAccompanyguest Operation
								String deleteAccompanyGuestReq = WSClient.createSOAPMessage("OWSDeleteAccompanyGuest",
										"DS_02");
								String deleteAccompanyGuestRes = WSClient.processSOAPMessage(deleteAccompanyGuestReq);

								// Validating response of OWS
								// DeleteAccompanyguest Operation
								if (WSAssert.assertIfElementExists(deleteAccompanyGuestRes,
										"DeleteAccompanyGuestResponse_Result_resultStatusFlag", true)) {
									WSAssert.assertIfElementValueEquals(deleteAccompanyGuestRes,
											"DeleteAccompanyGuestResponse_Result_resultStatusFlag", "FAIL", false);

									/*
									 * if(WSAssert.assertIfElementExists(
									 * deleteAccompanyGuestRes,
									 * "DeleteAccompanyGuestResponse_Result_GDSError",
									 * false)) {
									 * if(WSAssert.assertIfElementContains(
									 * deleteAccompanyGuestRes,
									 * "DeleteAccompanyGuestResponse_Result_GDSError",
									 * "Invalid Name Id or Accompany guest not found for reservation"
									 * , false)) {
									 *
									 * } }
									 */
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisites failed!------ChangeReservation-----Blocked");
							}
							CancelReservation.cancelReservation("DS_02");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :4
	@Test(groups = { "minimumRegression", "Reservation", "Invoice", "OWS" })
	public void deleteAccompanyGuest_41083() {
		try {
			String testname = "deleteAccompanyGuest_41083";
			WSClient.startTest(testname,
					"Verify that accompany guest is deleted by passing accompany guest firstname and lastname",
					"minimumRegression");

			setOperaHeader();

			// Setting ACCOMPANYING_GUEST Parameter
			WSClient.setData("{var_parameter}", "ACCOMPANYING_GUEST");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter1.equals("Y")) {
				Parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId1, UniqueId2, resvID;

				// Creating a profile of Accompanying Guest
				if (!(UniqueId1 = createProfile("DS_01")).equals("error")) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Successfully created profile of Accompanying guest.</b>");
					// Creating a profile of real Guest
					if (!(UniqueId2 = createProfile("DS_01")).equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created profile of primary guest.</b>");
						// Creating a reservation for the real Guest
						if (!(resvID = createReservation("DS_04")).equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Successfully created reservation for primary guest.</b>");
							WSClient.setData("{var_profileId2}", UniqueId1);
							WSClient.setData("{var_profileId1}", UniqueId2);
							WSClient.setData("{var_profileId}", UniqueId1);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_reservation_id}", resvID);

							String query = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");
							LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
							temp = WSClient.getDBRow(query);

							// Setting firstname and lastname of Accompanying
							// Guest
							WSClient.setData("{var_firstName}", temp.get("FIRST"));
							WSClient.setData("{var_lastName}", temp.get("LAST"));

							// Adding Accompanying Guest to the above
							// reservation
							String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_01");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							if (WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success",
									true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Successfully associated accompany guest with the primary guest reservation.</b>");
								setOWSHeader();
								String query2 = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");
								LinkedHashMap<String, String> temp2 = new LinkedHashMap<String, String>();
								temp2 = WSClient.getDBRow(query2);
								WSClient.setData("{var_firstName}", temp2.get("FIRST"));
								WSClient.setData("{var_lastName}", temp2.get("LAST"));

								String query3 = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_02");
								LinkedHashMap<String, String> temp3 = new LinkedHashMap<String, String>();
								temp3 = WSClient.getDBRow(query3);
								WSClient.setData("{var_confirmNo}", temp3.get("CONFIRMATION_NO"));

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								// Creating request and processing response for
								// OWS DeleteAccompanyguest Operation
								String deleteAccompanyGuestReq = WSClient.createSOAPMessage("OWSDeleteAccompanyGuest",
										"DS_02");
								String deleteAccompanyGuestRes = WSClient.processSOAPMessage(deleteAccompanyGuestReq);

								// Validating response of OWS
								// DeleteAccompanyguest Operation
								if (WSAssert.assertIfElementExists(deleteAccompanyGuestRes,
										"DeleteAccompanyGuestResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(deleteAccompanyGuestRes,
											"DeleteAccompanyGuestResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										// Database Validation
										LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
										String query_n = WSClient.getQuery("QS_03");
										db = WSClient.getDBRow(query_n);

										if (Integer.valueOf(db.get("count")).intValue() < 1) {
											WSClient.writeToReport(LogStatus.PASS,
													"Record Deleted---------Test Passed");
										} else {
											WSClient.writeToReport(LogStatus.FAIL,
													"Record not deleted------Test Failed");
										}

									} else {
										if (WSAssert.assertIfElementExists(deleteAccompanyGuestRes,
												"DeleteAccompanyGuestResponse_Result_GDSError", true)) {
											String message = WSAssert.getElementValue(deleteAccompanyGuestRes,
													"DeleteAccompanyGuestResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"The gds error displayed in the deleteAccomapnyGuest response is :"
															+ message);
										}

									}
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisites failed!------ChangeReservation-----Blocked");
							}
							CancelReservation.cancelReservation("DS_02");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :5
	@Test(groups = { "minimumRegression", "Reservation", "Invoice", "OWS" })
	public void deleteAccompanyGuest_41085() {
		try {
			String testname = "deleteAccompanyGuest_41085";
			WSClient.startTest(testname,
					"Verify that error message is obtained when deleting accompany guest by not passing id or name",
					"minimumRegression");

			setOperaHeader();

			// Setting ACCOMPANYING_GUEST Parameter
			WSClient.setData("{var_parameter}", "ACCOMPANYING_GUEST");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter1.equals("Y")) {
				Parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId1, UniqueId2, resvID;

				// Creating a profile of Accompanying Guest
				if (!(UniqueId1 = createProfile("DS_01")).equals("error")) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Successfully created profile of Accompanying guest.</b>");
					// Creating a profile of real Guest
					if (!(UniqueId2 = createProfile("DS_01")).equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created profile of primary guest.</b>");
						// Creating a reservation for the real Guest
						if (!(resvID = createReservation("DS_04")).equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Successfully created reservation for primary guest.</b>");
							WSClient.setData("{var_profileId2}", UniqueId1);
							WSClient.setData("{var_profileId1}", UniqueId2);
							WSClient.setData("{var_profileId}", UniqueId2);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_reservation_id}", resvID);

							String query = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");
							LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
							temp = WSClient.getDBRow(query);

							// Setting firstname and lastname of Accompanying
							// Guest
							WSClient.setData("{var_firstName}", temp.get("FIRST"));
							WSClient.setData("{var_lastName}", temp.get("LAST"));

							// Adding Accompanying Guest to the above
							// reservation
							String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_01");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							if (WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success",
									true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Successfully associated accompany guest with the primary guest reservation.</b>");
								setOWSHeader();
								WSClient.setData("{var_profileId}", UniqueId1);

								String query3 = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_02");
								LinkedHashMap<String, String> temp3 = new LinkedHashMap<String, String>();
								temp3 = WSClient.getDBRow(query3);
								WSClient.setData("{var_confirmNo}", temp3.get("CONFIRMATION_NO"));

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								// Creating request and processing response for
								// OWS DeleteAccompanyguest Operation
								String deleteAccompanyGuestReq = WSClient.createSOAPMessage("OWSDeleteAccompanyGuest",
										"DS_04");
								String deleteAccompanyGuestRes = WSClient.processSOAPMessage(deleteAccompanyGuestReq);

								// Validating response of OWS
								// DeleteAccompanyguest Operation
								if (WSAssert.assertIfElementExists(deleteAccompanyGuestRes,
										"DeleteAccompanyGuestResponse_Result_resultStatusFlag", true)) {
									WSAssert.assertIfElementValueEquals(deleteAccompanyGuestRes,
											"DeleteAccompanyGuestResponse_Result_resultStatusFlag", "FAIL", false);

									/*
									 * if(WSAssert.assertIfElementExists(
									 * deleteAccompanyGuestRes,
									 * "DeleteAccompanyGuestResponse_Result_GDSError",
									 * false)) {
									 * if(WSAssert.assertIfElementContains(
									 * deleteAccompanyGuestRes,
									 * "DeleteAccompanyGuestResponse_Result_GDSError",
									 * "Accompany guest not found for reservation"
									 * , false)) {
									 *
									 * } }
									 */
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisites failed!------ChangeReservation-----Blocked");
							}
							CancelReservation.cancelReservation("DS_02");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :6
	@Test(groups = { "minimumRegression", "Reservation", "Invoice", "OWS" })
	public void deleteAccompanyGuest_41087() {
		try {
			String testname = "deleteAccompanyGuest_41087";
			WSClient.startTest(testname,
					"Verify that error message is obtained by passing only accompany guest firstname",
					"minimumRegression");

			setOperaHeader();

			// Setting ACCOMPANYING_GUEST Parameter
			WSClient.setData("{var_parameter}", "ACCOMPANYING_GUEST");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter1.equals("Y")) {
				Parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId1, UniqueId2, resvID;

				// Creating a profile of Accompanying Guest
				if (!(UniqueId1 = createProfile("DS_01")).equals("error")) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Successfully created profile of Accompanying guest.</b>");
					// Creating a profile of real Guest
					if (!(UniqueId2 = createProfile("DS_01")).equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created profile of primary guest.</b>");
						// Creating a reservation for the real Guest
						if (!(resvID = createReservation("DS_04")).equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Successfully created reservation for primary guest.</b>");
							WSClient.setData("{var_profileId2}", UniqueId1);
							WSClient.setData("{var_profileId1}", UniqueId2);
							WSClient.setData("{var_profileId}", UniqueId1);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_reservation_id}", resvID);

							String query = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");
							LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
							temp = WSClient.getDBRow(query);

							// Setting firstname and lastname of Accompanying
							// Guest
							WSClient.setData("{var_firstName}", temp.get("FIRST"));
							WSClient.setData("{var_lastName}", temp.get("LAST"));

							// Adding Accompanying Guest to the above
							// reservation
							String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_01");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							if (WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success",
									true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Successfully associated accompany guest with the primary guest reservation.</b>");
								setOWSHeader();
								String query2 = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");
								LinkedHashMap<String, String> temp2 = new LinkedHashMap<String, String>();
								temp2 = WSClient.getDBRow(query2);
								WSClient.setData("{var_firstName}", temp2.get("FIRST"));

								String query3 = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_02");
								LinkedHashMap<String, String> temp3 = new LinkedHashMap<String, String>();
								temp3 = WSClient.getDBRow(query3);
								WSClient.setData("{var_confirmNo}", temp3.get("CONFIRMATION_NO"));

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								// Creating request and processing response for
								// OWS DeleteAccompanyguest Operation
								String deleteAccompanyGuestReq = WSClient.createSOAPMessage("OWSDeleteAccompanyGuest",
										"DS_05");
								String deleteAccompanyGuestRes = WSClient.processSOAPMessage(deleteAccompanyGuestReq);

								// Validating response of OWS
								// DeleteAccompanyguest Operation
								if (WSAssert.assertIfElementExists(deleteAccompanyGuestRes,
										"DeleteAccompanyGuestResponse_Result_resultStatusFlag", true)) {
									WSAssert.assertIfElementValueEquals(deleteAccompanyGuestRes,
											"DeleteAccompanyGuestResponse_Result_resultStatusFlag", "FAIL", false);

									/*
									 * if(WSAssert.assertIfElementExists(
									 * deleteAccompanyGuestRes,
									 * "DeleteAccompanyGuestResponse_Result_GDSError",
									 * false)) {
									 * if(WSAssert.assertIfElementContains(
									 * deleteAccompanyGuestRes,
									 * "DeleteAccompanyGuestResponse_Result_GDSError",
									 * "Accompany guest not found for reservation"
									 * , false)) {
									 *
									 * } }
									 */

									/*
									 * else { if(WSAssert.assertIfElementExists(
									 * deleteAccompanyGuestRes,
									 * "DeleteAccompanyGuestResponse_Result_GDSError",
									 * true)) { String
									 * message=WSAssert.getElementValue(
									 * deleteAccompanyGuestRes,
									 * "DeleteAccompanyGuestResponse_Result_GDSError",
									 * XMLType.RESPONSE);
									 * WSClient.writeToReport(LogStatus.INFO,
									 * "The gds error displayed in the deleteAccomapnyGuest response is :"
									 * + message); }
									 *
									 *
									 * }
									 */
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisites failed!------ChangeReservation-----Blocked");
							}
							CancelReservation.cancelReservation("DS_02");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :7
	@Test(groups = { "minimumRegression", "Reservation", "Invoice", "OWS" })
	public void deleteAccompanyGuest_42202() {
		try {
			String testname = "deleteAccompanyGuest_12345";
			WSClient.startTest(testname,
					"Verify that an Accompany Guest corresponding to particular given profile id given is deleted",
					"minimumRegression");

			setOperaHeader();

			// Setting ACCOMPANYING_GUEST Parameter
			WSClient.setData("{var_parameter}", "ACCOMPANYING_GUEST");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter1.equals("Y")) {
				Parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId1, UniqueId2, UniqueId3, UniqueId4, resvID;

				// Creating a profile of Primary Guest
				if (!(UniqueId1 = createProfile("DS_01")).equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created profile of primary guest.</b>");

					// Creating a reservation for the real Guest
					if (!(resvID = createReservation("DS_04")).equals("error")) {
						// Creating a profile of Accompanying Guest 1
						if (!(UniqueId2 = createProfile("DS_01")).equals("error")) {
							// Creating a profile of Accompanying Guest 2
							if (!(UniqueId3 = createProfile("DS_01")).equals("error")) {
								// Creating a profile of Accompanying Guest 3
								if (!(UniqueId4 = createProfile("DS_01")).equals("error")) {

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Successfully created profiles of three Accompanying guests.</b>");

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Successfully created reservation for primary guest.</b>");

									WSClient.setData("{var_profileId2}", UniqueId2);
									WSClient.setData("{var_profileId1}", UniqueId1);
									WSClient.setData("{var_profileId}", UniqueId2);
									WSClient.setData("{var_resvId}", resvID);
									WSClient.setData("{var_reservation_id}", resvID);

									String query = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");

									LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
									temp = WSClient.getDBRow(query);

									// Setting firstname and lastname of
									// Accompanying Guest
									WSClient.setData("{var_firstName}", temp.get("FIRST"));
									WSClient.setData("{var_lastName}", temp.get("LAST"));

									// Adding Accompanying Guest to the above
									// reservation
									String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation",
											"DS_01");
									String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

									if (WSAssert.assertIfElementExists(changeReservationRes,
											"ChangeReservationRS_Success", true)) {

										WSClient.writeToReport(LogStatus.INFO,
												"<b>Successfully associated accompany guest 1 with the primary guest reservation.</b>");

										WSClient.setData("{var_profileId2}", UniqueId3);
										WSClient.setData("{var_profileId1}", UniqueId1);
										WSClient.setData("{var_profileId}", UniqueId3);
										WSClient.setData("{var_resvId}", resvID);
										WSClient.setData("{var_reservation_id}", resvID);

										query = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");

										temp = WSClient.getDBRow(query);

										// Setting firstname and lastname of
										// Accompanying Guest
										WSClient.setData("{var_firstName}", temp.get("FIRST"));
										WSClient.setData("{var_lastName}", temp.get("LAST"));

										// Adding Accompanying Guest to the
										// above reservation
										changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_01");
										changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

										if (WSAssert.assertIfElementExists(changeReservationRes,
												"ChangeReservationRS_Success", true)) {

											WSClient.writeToReport(LogStatus.INFO,
													"<b>Successfully associated accompany guest 2 with the primary guest reservation.</b>");

											WSClient.setData("{var_profileId2}", UniqueId4);
											WSClient.setData("{var_profileId1}", UniqueId1);
											WSClient.setData("{var_profileId}", UniqueId4);
											WSClient.setData("{var_resvId}", resvID);
											WSClient.setData("{var_reservation_id}", resvID);

											query = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");

											temp = WSClient.getDBRow(query);

											// Setting firstname and lastname of
											// Accompanying Guest
											WSClient.setData("{var_firstName}", temp.get("FIRST"));
											WSClient.setData("{var_lastName}", temp.get("LAST"));

											// Adding Accompanying Guest to the
											// above reservation
											changeReservationReq = WSClient.createSOAPMessage("ChangeReservation",
													"DS_01");
											changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

											if (WSAssert.assertIfElementExists(changeReservationRes,
													"ChangeReservationRS_Success", true)) {

												WSClient.writeToReport(LogStatus.INFO,
														"<b>Successfully associated accompany guest 3 with the primary guest reservation.</b>");

												setOWSHeader();

												String resort = OPERALib.getResort();
												String channel = OWSLib.getChannel();
												WSClient.setData("{var_owsresort}",
														OWSLib.getChannelResort(resort, channel));

												String query3 = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_02");
												LinkedHashMap<String, String> temp3 = new LinkedHashMap<String, String>();
												temp3 = WSClient.getDBRow(query3);
												WSClient.setData("{var_confirmNo}", temp3.get("CONFIRMATION_NO"));

												// Creating request and
												// processing response for OWS
												// DeleteAccompanyguest
												// Operation
												String deleteAccompanyGuestReq = WSClient
														.createSOAPMessage("OWSDeleteAccompanyGuest", "DS_01");
												String deleteAccompanyGuestRes = WSClient
														.processSOAPMessage(deleteAccompanyGuestReq);

												// Validating response of OWS
												// DeleteAccompanyguest
												// Operation
												if (WSAssert.assertIfElementExists(deleteAccompanyGuestRes,
														"DeleteAccompanyGuestResponse_Result_resultStatusFlag", true)) {
													if (WSAssert.assertIfElementValueEquals(deleteAccompanyGuestRes,
															"DeleteAccompanyGuestResponse_Result_resultStatusFlag",
															"SUCCESS", false)) {

														// Database Validation
														LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
														String query_n = WSClient.getQuery("QS_03");
														db = WSClient.getDBRow(query_n);

														if (Integer.valueOf(db.get("count")).intValue() < 1) {
															WSClient.writeToReport(LogStatus.PASS,
																	"Record Deleted---------Test Passed");
														} else {
															WSClient.writeToReport(LogStatus.FAIL,
																	"Record not deleted------Test Failed");
														}

													} else {
														if (WSAssert.assertIfElementExists(deleteAccompanyGuestRes,
																"DeleteAccompanyGuestResponse_Result_GDSError", true)) {
															String message = WSAssert.getElementValue(
																	deleteAccompanyGuestRes,
																	"DeleteAccompanyGuestResponse_Result_GDSError",
																	XMLType.RESPONSE);
															WSClient.writeToReport(LogStatus.INFO,
																	"The gds error displayed in the deleteAccomapnyGuest response is :"
																			+ message);
														}

													}
												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Prerequisites failed!------ChangeReservation-----Blocked");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Prerequisites failed!------ChangeReservation-----Blocked");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING,
												"Prerequisites failed!------ChangeReservation-----Blocked");
									}
									CancelReservation.cancelReservation("DS_02");
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :8
	@Test(groups = { "minimumRegression", "Reservation", "Invoice", "OWS" })
	public void deleteAccompanyGuest_42325() {
		try {
			String testname = "deleteAccompanyGuest_42325";
			WSClient.startTest(testname,
					"Verify that eror message is obtained by passing only Accompany Guest profile id", "minimumRegression");

			setOperaHeader();

			// Setting ACCOMPANYING_GUEST Parameter
			WSClient.setData("{var_parameter}", "ACCOMPANYING_GUEST");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter1.equals("Y")) {
				Parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId1, UniqueId2, resvID;

				// Creating a profile of Accompanying Guest
				if (!(UniqueId1 = createProfile("DS_01")).equals("error")) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Successfully created profile of Accompanying guest.</b>");
					// Creating a profile of primary Guest
					if (!(UniqueId2 = createProfile("DS_01")).equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created profile of primary guest.</b>");
						// Creating a reservation for the real Guest
						if (!(resvID = createReservation("DS_04")).equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Successfully created reservation for primary guest.</b>");
							WSClient.setData("{var_profileId2}", UniqueId1);
							WSClient.setData("{var_profileId1}", UniqueId2);
							WSClient.setData("{var_profileId}", UniqueId1);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_reservation_id}", resvID);

							String query = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");

							LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
							temp = WSClient.getDBRow(query);

							// Setting firstname and lastname of Accompanying
							// Guest
							WSClient.setData("{var_firstName}", temp.get("FIRST"));
							WSClient.setData("{var_lastName}", temp.get("LAST"));

							// Adding Accompanying Guest to the above
							// reservation
							String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_01");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							if (WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success",
									true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Successfully associated accompany guest with the primary guest reservation.</b>");
								setOWSHeader();

								String query3 = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_02");
								LinkedHashMap<String, String> temp3 = new LinkedHashMap<String, String>();
								temp3 = WSClient.getDBRow(query3);
								// WSClient.setData("{var_confirmNo}",
								// temp3.get("CONFIRMATION_NO"));

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								// Creating request and processing response for
								// OWS DeleteAccompanyguest Operation
								String deleteAccompanyGuestReq = WSClient.createSOAPMessage("OWSDeleteAccompanyGuest",
										"DS_06");
								String deleteAccompanyGuestRes = WSClient.processSOAPMessage(deleteAccompanyGuestReq);

								// Validating response of OWS
								// DeleteAccompanyguest Operation
								if (WSAssert.assertIfElementExists(deleteAccompanyGuestRes,
										"DeleteAccompanyGuestResponse_Result_resultStatusFlag", true)) {
									WSAssert.assertIfElementValueEquals(deleteAccompanyGuestRes,
											"DeleteAccompanyGuestResponse_Result_resultStatusFlag", "FAIL", false);

									/*
									 * if(WSAssert.assertIfElementExists(
									 * deleteAccompanyGuestRes,
									 * "DeleteAccompanyGuestResponse_Result_GDSError",
									 * false)) {
									 * if(WSAssert.assertIfElementContains(
									 * deleteAccompanyGuestRes,
									 * "DeleteAccompanyGuestResponse_Result_GDSError",
									 * "Accompany guest not found for reservation"
									 * , false)) {
									 *
									 * } }
									 */
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisites failed!------ChangeReservation-----Blocked");
							}
							CancelReservation.cancelReservation("DS_02");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :9
	@Test(groups = { "minimumRegression", "Reservation", "Invoice", "OWS" })
	public void deleteAccompanyGuest_42326() {
		try {
			String testname = "deleteAccompanyGuest_42326";
			WSClient.startTest(testname,
					"Verify that an Accompany Guest associated to a reservation is deleted after reservation of primary guest is cancelled",
					"minimumRegression");

			setOperaHeader();

			// Setting ACCOMPANYING_GUEST Parameter
			WSClient.setData("{var_parameter}", "ACCOMPANYING_GUEST");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter1.equals("Y")) {
				Parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId1, UniqueId2, resvID;

				// Creating a profile of Accompanying Guest
				if (!(UniqueId1 = createProfile("DS_01")).equals("error")) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Successfully created profile of Accompanying guest.</b>");
					// Creating a profile of primary Guest
					if (!(UniqueId2 = createProfile("DS_01")).equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created profile of primary guest.</b>");
						// Creating a reservation for the real Guest
						if (!(resvID = createReservation("DS_04")).equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Successfully created reservation for primary guest.</b>");
							WSClient.setData("{var_profileId2}", UniqueId1);
							WSClient.setData("{var_profileId1}", UniqueId2);
							WSClient.setData("{var_profileId}", UniqueId1);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_reservation_id}", resvID);

							String query = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");

							LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
							temp = WSClient.getDBRow(query);

							// Setting firstname and lastname of Accompanying
							// Guest
							WSClient.setData("{var_firstName}", temp.get("FIRST"));
							WSClient.setData("{var_lastName}", temp.get("LAST"));

							// Adding Accompanying Guest to the above
							// reservation
							String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_01");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							if (WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success",
									true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Successfully associated accompany guest with the primary guest reservation.</b>");
								setOWSHeader();

								// Cancelling Reservation
								CancelReservation.cancelReservation("DS_02");

								String query3 = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_02");
								LinkedHashMap<String, String> temp3 = new LinkedHashMap<String, String>();
								temp3 = WSClient.getDBRow(query3);
								WSClient.setData("{var_confirmNo}", temp3.get("CONFIRMATION_NO"));

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								// Creating request and processing response for
								// OWS DeleteAccompanyguest Operation
								String deleteAccompanyGuestReq = WSClient.createSOAPMessage("OWSDeleteAccompanyGuest",
										"DS_01");
								String deleteAccompanyGuestRes = WSClient.processSOAPMessage(deleteAccompanyGuestReq);

								// Validating response of OWS
								// DeleteAccompanyguest Operation
								// Validating response of OWS
								// DeleteAccompanyguest Operation
								if (WSAssert.assertIfElementExists(deleteAccompanyGuestRes,
										"DeleteAccompanyGuestResponse_Result_resultStatusFlag", false)) {
									if (WSAssert.assertIfElementValueEquals(deleteAccompanyGuestRes,
											"DeleteAccompanyGuestResponse_Result_resultStatusFlag", "FAIL", false)) {

										// // Database Validation
										// LinkedHashMap<String, String> db =
										// new LinkedHashMap<String, String>();
										// String query_n =
										// WSClient.getQuery("QS_03");
										// db = WSClient.getDBRow(query_n);
										//
										// if
										// (Integer.valueOf(db.get("count")).intValue()
										// < 1) {
										// WSClient.writeToReport(LogStatus.PASS,
										// "Record Deleted---------Test
										// Passed");
										// } else {
										// WSClient.writeToReport(LogStatus.FAIL,
										// "Record not deleted------Test
										// Failed");
										// }
										if (WSAssert.assertIfElementExists(deleteAccompanyGuestRes,
												"DeleteAccompanyGuestResponse_Result_GDSError", true)) {
											String message = WSAssert.getElementValue(deleteAccompanyGuestRes,
													"DeleteAccompanyGuestResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"The gds error displayed in the deleteAccomapnyGuest response is :"
															+ message);
										}

									} else {

									}
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisites failed!------ChangeReservation-----Blocked");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :10
	@Test(groups = { "minimumRegression", "Reservation", "Invoice", "OWS" })
	public void deleteAccompanyGuest_42327() {
		try {
			String testname = "deleteAccompanyGuest_42327";
			WSClient.startTest(testname,
					"Verify that an Accompany Guest associated to a reservation is deleted after reservation of primary guest is checkedin",
					"minimumRegression");

			setOperaHeader();

			// Setting ACCOMPANYING_GUEST Parameter
			WSClient.setData("{var_parameter}", "ACCOMPANYING_GUEST");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter1.equals("Y")) {
				Parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId1, UniqueId2, resvID;

				// Creating a profile of Accompanying Guest
				if (!(UniqueId1 = createProfile("DS_01")).equals("error")) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Successfully created profile of Accompanying guest.</b>");
					// Creating a profile of primary Guest
					if (!(UniqueId2 = createProfile("DS_01")).equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created profile of primary guest.</b>");
						// Creating a reservation for the real Guest
						if (!(resvID = createReservation("DS_04")).equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Successfully created reservation for primary guest.</b>");
							WSClient.setData("{var_profileId2}", UniqueId1);
							WSClient.setData("{var_profileId1}", UniqueId2);
							WSClient.setData("{var_profileId}", UniqueId1);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_reservation_id}", resvID);

							String query = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_01");

							LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
							temp = WSClient.getDBRow(query);

							// Setting firstname and lastname of Accompanying
							// Guest
							WSClient.setData("{var_firstName}", temp.get("FIRST"));
							WSClient.setData("{var_lastName}", temp.get("LAST"));

							// Adding Accompanying Guest to the above
							// reservation
							String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_01");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							if (WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success",
									true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Successfully associated accompany guest with the primary guest reservation.</b>");

								WSClient.setData("{var_profileId}", UniqueId2);
								// Fetching and assigning room to reservation
								fetchHotelRooms("DS_13");
								String resort = OPERALib.getResort();
								WSClient.setData("{var_owsresort}", resort);

								// Checking In Reservation
								String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {

									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");

									WSClient.setData("{var_profileId}", UniqueId1);

									setOWSHeader();

									String query3 = WSClient.getQuery("OWSDeleteAccompanyGuest", "QS_02");
									LinkedHashMap<String, String> temp3 = new LinkedHashMap<String, String>();
									temp3 = WSClient.getDBRow(query3);
									WSClient.setData("{var_confirmNo}", temp3.get("CONFIRMATION_NO"));

									resort = OPERALib.getResort();
									String channel = OWSLib.getChannel();
									WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

									// Creating request and processing response
									// for OWS DeleteAccompanyguest Operation
									String deleteAccompanyGuestReq = WSClient
											.createSOAPMessage("OWSDeleteAccompanyGuest", "DS_01");
									String deleteAccompanyGuestRes = WSClient
											.processSOAPMessage(deleteAccompanyGuestReq);

									// Validating response of OWS
									// DeleteAccompanyguest Operation
									if (WSAssert.assertIfElementExists(deleteAccompanyGuestRes,
											"DeleteAccompanyGuestResponse_Result_resultStatusFlag", false)) {
										if (WSAssert.assertIfElementValueEquals(deleteAccompanyGuestRes,
												"DeleteAccompanyGuestResponse_Result_resultStatusFlag", "SUCCESS",
												false)) {

											// Database Validation
											LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
											String query_n = WSClient.getQuery("QS_03");
											db = WSClient.getDBRow(query_n);

											if (Integer.valueOf(db.get("count")).intValue() < 1) {
												WSClient.writeToReport(LogStatus.PASS,
														"Record Deleted---------Test Passed");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"Record not deleted------Test Failed");
											}

										} else {
											if (WSAssert.assertIfElementExists(deleteAccompanyGuestRes,
													"DeleteAccompanyGuestResponse_Result_GDSError", true)) {
												String message = WSAssert.getElementValue(deleteAccompanyGuestRes,
														"DeleteAccompanyGuestResponse_Result_GDSError",
														XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"The gds error displayed in the deleteAccomapnyGuest response is :"
																+ message);
											}
										}
									}
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisites failed!------ChangeReservation-----Blocked");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

}
