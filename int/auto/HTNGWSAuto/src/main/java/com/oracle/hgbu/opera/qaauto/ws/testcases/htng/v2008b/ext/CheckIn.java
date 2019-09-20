package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2008b.ext;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CheckoutReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.SetHousekeepingRoomStatus;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

/**
 * <b>Author</b> amaddela <br>
 * <b>Description</b> HTNG - Ext--CheckIn <br>
 * <b>Created Date</b> 26/10/2017
 */

public class CheckIn extends WSSetUp {
	boolean flag = false;

	public String fetchHotelRooms(String ds) {
		String roomNumber = "";

		try {
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_05"));

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", ds);
			String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);
			if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true)) {
				if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
					roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
							"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "Successfully Fetched Room to assign, room Number is: " + roomNumber + "</b>");
					WSClient.setData("{var_roomNumber}", roomNumber);

				} else {
					roomNumber = "na";
				}
			} else {
				roomNumber = "error";
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		return roomNumber;
	}

	// Creating Room to assign
	public String createRoom(String ds) {
		String roomNumber = "";
		try {
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_05"));

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			String createRoomReq = WSClient.createSOAPMessage("CreateRoom", ds);
			String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
			if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
				roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber",
						XMLType.REQUEST);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>" + "Successfully created Room, room number is:" + roomNumber + "</b>");
				WSClient.setData("{var_roomNumber}", roomNumber);
			} else {
				roomNumber = "error";
				WSClient.writeToReport(LogStatus.WARNING, "Not able to create Room");
			}

		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		}

		return roomNumber;
	}

	// Setting Housekeeping Room Status and calling Assign Room
	public boolean setHousekeepingRoomStatus(String ds) {
		try {
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", ds);
			String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);
			if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success",
					true)) {

				return true;
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "SetHousekeepingRoomStatus blocked");
			}

		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		}
		return false;
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

			String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", ds);
			String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);
			if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Successfully Assigned Room" + "</b>");
				return true;
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "<b>" + "Assign Room is unsuccessful" + "</b>");
				return false;
			}

		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		}
		return false;
	}

	@Test(groups = { "sanity", "CheckIn", "HTNG2008BExt", "HTNG" })

	/*
	 * Method to check the check in operation by passing reservation id,resort
	 * id and room number
	 */

	public void checkIn_Ext_40550() {
		try {
			String testName = "checkIn_Ext_40550";
			WSClient.startTest(testName,
					"Verify that the checking in of reservation is successful when resvId,resortId,roomNumber are passed in request ",
					"sanity");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				flag = false;
				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_parameter}", "SHOW_INSPECTED_ROOMS");
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking the parameters and setting them if required</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("Y")) {
					Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}
				if (!Parameter.equals("error")) {

					/*************
					 * Prerequisite 1: Creating new profile by fetching values
					 * required
					 ******************/

					String operaProfileID = CreateProfile.createProfile("DS_01");
					if (!operaProfileID.equals("error")) {
						System.out.println(operaProfileID);
						WSClient.setData("{var_profileId}", operaProfileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + operaProfileID + "</b>");
						/*************
						 * Prerequisite 2: Fetch Details for room type,rate
						 * code,source code,market code
						 ******************/

						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_05"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						String reservationId = CreateReservation.createReservation("DS_13").get("reservationId");
						if (!reservationId.equals("error")) {
							WSClient.setData("{var_resvId}", reservationId);
							System.out.println(reservationId);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: " + reservationId + "</b>");
							WSClient.setData("{var_extResort}", resortExtValue);

							/************
							 * Prerequisite 3: Fetching available Hotel rooms
							 * which are vacant and inspected to assign a room
							 * for checking in
							 ****************/
						
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Fetching rooms to ensure that room is available and inspected</b> ");
							String roomNumber = fetchHotelRooms("DS_17");
							if (!roomNumber.equals("error")) {
								if (roomNumber.equals("na")) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Creating Room as Room not available to checkin guest</b> ");

									roomNumber = createRoom("RoomMaint");
									if (!roomNumber.equals("error")) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Setting Room status for created room " + roomNumber
														+ "as 'Inspected'</b> ");
										setHousekeepingRoomStatus("DS_01");

									}
								}
								/************
								 * Operation Check In : Performing the Check In
								 * operation
								 ****************/
								HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
								String checkInReq = WSClient.createSOAPMessage("HTNGExtCheckIn", "DS_01");
								String CheckInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(CheckInRes, "CheckInResponse_Result", false)) {
									if (WSAssert.assertIfElementValueEquals(CheckInRes,
											"CheckInResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										flag = true;
										String checkin = WSClient.getDBRow(WSClient.getQuery("QS_01"))
												.get("RESV_STATUS");

										if (WSAssert.assertEquals("CHECKED IN", checkin, true)) {
											WSClient.writeToReport(LogStatus.PASS,
													"<b>" + "ReservationStatus->Expected : CHECKED IN      ACTUAL :  "
															+ checkin + "</b>");

										} else {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b>" + "ReservationStatus->Expected : CHECKED IN      ACTUAL :  "
															+ checkin + "</b>");
										}
										WSClient.writeToReport(LogStatus.PASS, "Check In is successful");
									}
									// Checking for result text
									if (WSAssert.assertIfElementExists(CheckInRes, "Result_Text_TextElement", true)) {
										String message = WSClient.getElementValue(CheckInRes, "Result_Text_TextElement",
												XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>"
												+ "The text displayed in the response is    :     " + message + "</b>");
									}

								}
							}
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked :  prerequisites for RateCode or RoomType or SourceCode or MarketCode or PaymentMethod**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// checking out reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals("") && flag == true) {
					CheckoutReservation.checkOutReservation("DS_01");
				} else if (!WSClient.getData("{var_resvId}").equals("")) {
					CancelReservation.cancelReservation("DS_02");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			}
		}
	}

	@Test(groups = { "minimumRegression", "CheckIn", "HTNG2008BExt", "HTNG" })

	/*
	 * Method to check the check in operation by passing reservation id,resort
	 * id and room number when no inspected room is present
	 */

	public void checkIn_Ext_40040() {
		try {
			String testName = "checkIn_Ext_40040";
			WSClient.startTest(testName,
					"Verify that the checking in of reservation is successful to a clean room when no inspected room is present.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				// boolean prerequisite = true;
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				flag = false;
				/*************
				 * Prerequisite 1: Creating new profile by fetching values
				 * required
				 ******************/

				String operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileId}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + operaProfileID + "</b>");
					/*************
					 * Prerequisite 2: Fetch Details for room type,rate
					 * code,source code,market code
					 ******************/

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_05"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					String reservationId = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!reservationId.equals("error")) {
						WSClient.setData("{var_resvId}", reservationId);
						System.out.println(reservationId);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: " + reservationId + "</b>");
						WSClient.setData("{var_extResort}", resortExtValue);

						/************
						 * Prerequisite 3: Fetching available Hotel rooms which
						 * are vacant and clean to assign a room for checking
						 * in.There should be no inspected room available.
						 ****************/

						WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_05"));
						WSClient.setData("{var_roomNumber}", OperaPropConfig.getDataSetForCode("Rooms", "DS_41"));
						WSClient.writeToReport(LogStatus.INFO, "<b>Setting Room status for room Number "
								+ OperaPropConfig.getDataSetForCode("Rooms", "DS_41")
								+ "(dedicated to this operation) as clean so as to checkIn guest to a clean room</b>");
						setHousekeepingRoomStatus("DS_02");

						List<LinkedHashMap<String, String>> rooms = WSClient
								.getDBRows(WSClient.getQuery("HTNGExtCheckIn", "QS_05"));

						for (int i = 0; i < rooms.size(); i++) {
							if (rooms.get(i).get("ROOM_STATUS").equals("IP")) {
								WSClient.setData("{var_roomNumber}", rooms.get(i).get("ROOM"));
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Setting Room Status For all Inspected Rooms as 'Clean' to make sure checkIn happens to clean room only</b>");

								setHousekeepingRoomStatus("DS_02");

							}
						}
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Fetching rooms to ensure that room is available and Clean</b> ");
						String roomNumber = fetchHotelRooms("DS_10");
						if (!roomNumber.equals("error")) {
							if (roomNumber.equals("na")) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Creating Room when no clean room is available</b>");

								roomNumber = createRoom("RoomMaint");
								if (!roomNumber.equals("error")) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Setting Room Status of 'Created Room' to 'Clean'</b>");
									setHousekeepingRoomStatus("DS_02");

								}
							}

							/************
							 * Operation Check In : Performing the Check In
							 * operation
							 ****************/
							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String checkInReq = WSClient.createSOAPMessage("HTNGExtCheckIn", "DS_02");
							String CheckInRes = WSClient.processSOAPMessage(checkInReq);

							if (WSAssert.assertIfElementExists(CheckInRes, "CheckInResponse_Result", false)) {
								if (WSAssert.assertIfElementValueEquals(CheckInRes,
										"CheckInResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									flag = true;
									LinkedHashMap<String, String> checkin = WSClient
											.getDBRow(WSClient.getQuery("QS_06"));
									if (WSAssert.assertEquals("CL", checkin.get("ROOM_STATUS"), true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"<b>" + "RoomStatus->Expected : CL     ACTUAL :  "
														+ checkin.get("ROOM_STATUS") + "</b>");

									} else {
										WSClient.writeToReport(LogStatus.FAIL,
												"<b>" + "RoomStatus->Expected : CL     ACTUAL :  "
														+ checkin.get("ROOM_STATUS") + "</b>");
									}
									if (WSAssert.assertEquals("CHECKED IN", checkin.get("RESV_STATUS"), true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"<b>" + "ReservationStatus->Expected : CHECKED IN      ACTUAL :  "
														+ checkin.get("RESV_STATUS") + "</b>");

									} else {
										WSClient.writeToReport(LogStatus.FAIL,
												"<b>" + "ReservationStatus->Expected : CHECKED IN      ACTUAL :  "
														+ checkin.get("RESV_STATUS") + "</b>");
									}
									WSClient.writeToReport(LogStatus.PASS, "Check In is successful");
								}

								// Checking for result text
								if (WSAssert.assertIfElementExists(CheckInRes, "Result_Text_TextElement", true)) {
									String message = WSClient.getElementValue(CheckInRes, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>"
											+ "The text displayed in the response is    :     " + message + "</b>");
								}

							}

						}

					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked :  prerequisites for RateCode or RoomType or SourceCode or MarketCode or PaymentMethod**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// checking out reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals("") && flag == true) {
					CheckoutReservation.checkOutReservation("DS_01");
				} else if (!WSClient.getData("{var_resvId}").equals("")) {
					CancelReservation.cancelReservation("DS_02");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			}
		}
	}


	@Test(groups = { "minimumRegression", "CheckIn", "HTNG2008BExt", "HTNG" })

	/*
	 * Method to check the check in operation by passing reservation id,resort
	 * id and room number when arrival date is not today's date
	 */

	public void checkIn_Ext_40580() {
		try {
			String testName = "checkIn_Ext_40580";
			WSClient.startTest(testName,
					"Verify that the checking in of reservation whose arrival date is not today's date is unsuccessful",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				/*************
				 * Prerequisite 1: Creating new profile by fetching values
				 * required
				 ******************/

				String operaProfileID = CreateProfile.createProfile("DS_01");

				if (!operaProfileID.equals("error")) {

					System.out.println(operaProfileID);
					WSClient.setData("{var_profileId}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + operaProfileID + "</b>");

					/*************
					 * Prerequisite 2: Fetch Details for room type,rate
					 * code,source code,market code
					 ******************/

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_05"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");

					if (!reservationId.equals("error")) {

						WSClient.setData("{var_resvId}", reservationId);
						System.out.println(reservationId);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: " + reservationId + "</b>");
						WSClient.setData("{var_extResort}", resortExtValue);

						/************
						 * Prerequisite 3: Fetching available Hotel rooms which
						 * are vacant and inspected to assign a room for
						 * checking in
						 ****************/
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Fetching rooms to ensure that room is available and inspected</b> ");
						String roomNumber = fetchHotelRooms("DS_17");

						if (!roomNumber.equals("error")) {
							if (roomNumber.equals("na")) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Creating Room when Room is not available</b>");

								roomNumber = createRoom("RoomMaint");
								if (!roomNumber.equals("error")) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Setting Room Status For Created Room  as 'Inspected'</b>");
									setHousekeepingRoomStatus("DS_01");

								}
							}

							/************
							 * Operation Check In : Performing the Check In
							 * operation
							 ****************/

							HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
							String checkInReq = WSClient.createSOAPMessage("HTNGExtCheckIn", "DS_01");
							String CheckInRes = WSClient.processSOAPMessage(checkInReq);

							if (WSAssert.assertIfElementExists(CheckInRes, "CheckInResponse_Result", false)) {
								if (WSAssert.assertIfElementValueEquals(CheckInRes,
										"CheckInResponse_Result_resultStatusFlag", "FAIL", false)) {

									// Checking for result text
									if (WSAssert.assertIfElementExists(CheckInRes, "Result_Text_TextElement", true)) {
										String message = WSClient.getElementValue(CheckInRes, "Result_Text_TextElement",
												XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>" + "The text displayed in the response is :" + message + "</b>");
									}
								}
							}
						}
					}
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked :  prerequisites for RateCode or RoomType or SourceCode or MarketCode or PaymentMethod**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// cancel reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CancelReservation.cancelReservation("DS_02");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			}
		}
	}

	@Test(groups = { "minimumRegression", "CheckIn", "HTNG2008BExt", "HTNG" })

	/*
	 * Method to check the check in operation by passing reservation id,resort
	 * id and room number of accompany guest
	 */

	public void checkIn_Ext_40555() {
		try {
			String testName = "checkIn_Ext_40555";
			WSClient.startTest(testName, "Verify that the checking in of accompany reservation is successful",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				flag = false;

				/*************
				 * Prerequisite 1: Creating new profile by fetching values
				 * required
				 ******************/

				String profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID1->Guest->: " + profileID1 + "</b>");
					String profileID2 = CreateProfile.createProfile("DS_01");

					if (!profileID2.equals("error")) {

						WSClient.setData("{var_profileId2}", profileID2);
						WSClient.setData("{var_profileId1}", profileID1);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Profile ID2->Accompanying Guest->: " + profileID2 + "</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_05"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_13");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_reservation_id}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: " + resvID + "</b>");
							String query = WSClient.getQuery("HTNGExtCheckIn", "QS_02");
							LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
							temp = WSClient.getDBRow(query);

							WSClient.setData("{var_firstName}", temp.get("FIRST"));
							WSClient.setData("{var_lastName}", temp.get("LAST"));

							// Pre-requisite : Change Reservation to add
							// accompany guest
							WSClient.writeToReport(LogStatus.INFO, "<b>Adding accompany guest to main guest</b>");
							String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_01");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							if (WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success",
									true)) {

								/************
								 * Prerequisite 3: Fetching available Hotel
								 * rooms which are vacant and inspected to
								 * assign a room for checking in
								 ****************/
								WSClient.setData("{var_roomNumber}",
										OperaPropConfig.getDataSetForCode("Rooms", "DS_41"));
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Setting Room Status of the Room "
												+ OperaPropConfig.getDataSetForCode("Rooms", "DS_41")
												+ "(dedicated to this operation) as 'Inspected'</b>");
								setHousekeepingRoomStatus("DS_01");
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Fetching rooms to ensure that room is available and inspected</b> ");
								String roomNumber = fetchHotelRooms("DS_17");
								if (!roomNumber.equals("error")) {
									if (roomNumber.equals("na")) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Creating Room When Inspected Room Not Available</b>");
										roomNumber = createRoom("RoomMaint");
										if (!roomNumber.equals("error")) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Setting Room Status of the Created Room " + roomNumber
															+ "(dedicated to this operation) as 'Inspected'</b>");
											setHousekeepingRoomStatus("DS_01");
										}
									}
									/************
									 * Operation Check In : Performing the Check
									 * In operation
									 ****************/
									HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
									String checkInReq = WSClient.createSOAPMessage("HTNGExtCheckIn", "DS_01");
									String CheckInRes = WSClient.processSOAPMessage(checkInReq);

									if (WSAssert.assertIfElementExists(CheckInRes, "CheckInResponse_Result", false)) {
										if (WSAssert.assertIfElementValueEquals(CheckInRes,
												"CheckInResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											flag = true;
											// DB Validation
											String checkin = WSClient.getDBRow(WSClient.getQuery("QS_01"))
													.get("RESV_STATUS");
											if (WSAssert.assertEquals("CHECKED IN", checkin, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b>" + "ReservationStatus->Expected : CHECKED IN      ACTUAL :  "
																+ checkin + "</b>");

											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b>" + "ReservationStatus->Expected : CHECKED IN      ACTUAL :  "
																+ checkin + "</b>");
											}
											WSClient.writeToReport(LogStatus.PASS,
													"Check In is successful for Direct Guest");

											String checkin1 = WSClient.getDBRow(WSClient.getQuery("QS_03"))
													.get("RESV_STATUS");
											if (WSAssert.assertEquals("CHECKED IN", checkin1, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b>" + "ReservationStatus->Expected : CHECKED IN      ACTUAL :  "
																+ checkin1 + "</b>");

											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b>" + "ReservationStatus->Expected : CHECKED IN      ACTUAL :  "
																+ checkin1 + "</b>");
											}
											WSClient.writeToReport(LogStatus.PASS,
													"Check In is successful for Accompany Guest");

										}

										// Checking for result text
										if (WSAssert.assertIfElementExists(CheckInRes, "Result_Text_TextElement",
												true)) {
											String message = WSClient.getElementValue(CheckInRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>" + "The text displayed in the response is    :     " + message
															+ "</b>");
										}

									}
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"************Blocked :  Unable to add accompany guest**********");
							}
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked :  prerequisites for RateCode or RoomType or SourceCode or MarketCode or PaymentMethod**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// cancel reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals("") && flag == true) {
					CheckoutReservation.checkOutReservation("DS_01");
				} else if (!WSClient.getData("{var_resvId}").equals("")) {
					CancelReservation.cancelReservation("DS_02");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			}
		}
	}

	@Test(groups = { "minimumRegression", "CheckIn", "HTNG2008BExt", "HTNG" })

	/*
	 * Method to check the check in operation by passing reservation id,resort
	 * id and room number of shared reservation
	 */

	public void checkIn_Ext_40563() {
		try {
			String testName = "checkIn_Ext_40563";
			WSClient.startTest(testName,
					"Verify that when one of shared reservation is being checkedIn,the other should not get checkedIn automatically",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				flag = false;

				/*************
				 * Prerequisite 1: Creating new profile by fetching values
				 * required
				 ******************/

				WSClient.setData("{var_parameter}", "AUTO_CHECKIN_SHARE_WITH_RESERVATION");
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO,
						"<b>Checking the parameter(AUTO_CHECKIN_SHARE_WITH_RESERVATION) and setting it if required</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("Y")) {
					Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}
				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					String profileID1 = CreateProfile.createProfile("DS_01");
					if (!profileID1.equals("error")) {
						WSClient.setData("{var_profileId}", profileID1);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID1: First Guest->" + profileID1 + "</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_05"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 - Create Reservation

						HashMap<String, String> resv = CreateReservation.createReservation("DS_13");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID1:First Guest-> " + resvID + "</b>");
							String profileID2 = CreateProfile.createProfile("DS_01");
							if (!profileID2.equals("error")) {
								WSClient.setData("{var_profileId}", profileID2);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Profile ID2:Second Guest " + profileID2 + "</b>");
								HashMap<String, String> resv1 = CreateReservation.createReservation("DS_13");
								String resvID1 = resv1.get("reservationId");

								if (!resvID1.equals("error")) {

									WSClient.setData("{var_resvId1}", resvID1);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Reservation ID2:Second guest-> " + resvID1 + "</b>");

									// Prerequisite 4 : combining Reservation
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Creating shared reservation for the two guests</b>");
									String combineReq = WSClient.createSOAPMessage("CombineShareReservations", "DS_01");
									String combineRes = WSClient.processSOAPMessage(combineReq);

									if (WSAssert.assertIfElementExists(combineRes, "CombineShareReservationsRS_Success",
											true)) {

										String combineResID = WSClient.getElementValue(combineRes,
												"Reservation_ReservationIDList_UniqueID_ID", XMLType.RESPONSE);
										WSClient.setData("{var_resvId}", combineResID);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Combine Reservation ID: " + combineResID + "</b>");
										WSClient.setData("{var_roomNumber}",
												OperaPropConfig.getDataSetForCode("Rooms", "DS_41"));
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Setting Room Status of the Room "
														+ OperaPropConfig.getDataSetForCode("Rooms", "DS_41")
														+ "(dedicated to this operation) as 'Inspected'</b>");
										setHousekeepingRoomStatus("DS_01");
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Fetching rooms to ensure that room is available and inspected</b> ");
										String roomNumber = fetchHotelRooms("DS_17");
										if (!roomNumber.equals("error")) {
											if (roomNumber.equals("na")) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Creating Room when 'Inspected' Room is not available </b>");
												roomNumber = createRoom("RoomMaint");
												if (!roomNumber.equals("error")) {
													WSClient.writeToReport(LogStatus.INFO,
															"<b>Setting Room Status of the Created Room " + roomNumber
																	+ " as 'Inspected'</b>");
													setHousekeepingRoomStatus("DS_01");
												}
											}

											/************
											 * Operation Check In : Performing
											 * the Check In operation
											 ****************/

											HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
											String checkInReq = WSClient.createSOAPMessage("HTNGExtCheckIn", "DS_01");
											String CheckInRes = WSClient.processSOAPMessage(checkInReq);

											if (WSAssert.assertIfElementExists(CheckInRes, "CheckInResponse_Result",
													false)) {
												if (WSAssert.assertIfElementValueEquals(CheckInRes,
														"CheckInResponse_Result_resultStatusFlag", "SUCCESS", false)) {
													flag = true;
													// DB Validation
													String checkin = WSClient.getDBRow(WSClient.getQuery("QS_01"))
															.get("RESV_STATUS");

													if (WSAssert.assertEquals("CHECKED IN", checkin, true)) {
														WSClient.writeToReport(LogStatus.PASS,
																"<b>" + "ReservationStatus->Expected : CHECKED IN      ACTUAL :  "
																		+ checkin + "</b>");

													} else {
														WSClient.writeToReport(LogStatus.FAIL,
																"<b>" + "ReservationStatus->Expected : CHECKED IN      ACTUAL :  "
																		+ checkin + "</b>");
													}
													WSClient.writeToReport(LogStatus.PASS,
															"Check In is successful for first Guest");

													String checkin1 = WSClient.getDBRow(WSClient.getQuery("QS_04"))
															.get("RESV_STATUS");

													if (WSAssert.assertEquals("PROSPECT", checkin1, true)) {
														WSClient.writeToReport(LogStatus.PASS,
																"<b>" + "ReservationStatus->Expected : PROSPECT      ACTUAL :  "
																		+ checkin1 + "</b>");

													} else {
														WSClient.writeToReport(LogStatus.FAIL,
																"<b>" + "ReservationStatus->Expected :PROSPECT      ACTUAL :   "
																		+ checkin1 + "</b>");
													}
													WSClient.writeToReport(LogStatus.PASS,
															"Second Guest is not getting checked in");

												}

												// Checking for result text
												if (WSAssert.assertIfElementExists(CheckInRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(CheckInRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");
												}

											}
										}
									}
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"************Blocked :  Unable to combine reservation**********");
							}
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked :  prerequisites for RateCode or RoomType or SourceCode or MarketCode or PaymentMethod**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// cancel reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals("") && flag == true) {
					CheckoutReservation.checkOutReservation("DS_01");
				}
				if (!WSClient.getData("{var_resvId1}").equals("")) {
					WSClient.setData("{var_resvId}", WSClient.getData("{var_resvId1}"));
					CancelReservation.cancelReservation("DS_02");
				} else if (!WSClient.getData("{var_resvId}").equals("")) {
					CancelReservation.cancelReservation("DS_02");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			}
		}
	}

	@Test(groups = { "minimumRegression", "CheckIn", "HTNG2008BExt", "HTNG" })

	/*
	 * Method to check the check in operation by passing reservation id,resort
	 * id
	 */

	public void checkIn_Ext_40938() {
		try {
			String testName = "checkIn_Ext_40938";
			WSClient.startTest(testName,
					"Verify that the checking in of reservation with resvId and resortId in request is successful  ",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				flag = false;
				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_parameter}", "SHOW_INSPECTED_ROOMS");
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking the parameters and setting them if required</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("Y")) {
					Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}
				if (!Parameter.equals("error")) {

					/*************
					 * Prerequisite 1: Creating new profile by fetching values
					 * required
					 ******************/

					String operaProfileID = CreateProfile.createProfile("DS_01");
					if (!operaProfileID.equals("error")) {
						System.out.println(operaProfileID);
						WSClient.setData("{var_profileId}", operaProfileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + operaProfileID + "</b>");
						/*************
						 * Prerequisite 2: Fetch Details for room type,rate
						 * code,source code,market code
						 ******************/

						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_05"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						String reservationId = CreateReservation.createReservation("DS_13").get("reservationId");
						if (!reservationId.equals("error")) {
							WSClient.setData("{var_resvId}", reservationId);
							System.out.println(reservationId);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: " + reservationId + "</b>");
							WSClient.setData("{var_extResort}", resortExtValue);

							WSClient.setData("{var_roomNumber}", OperaPropConfig.getDataSetForCode("Rooms", "DS_41"));
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Setting Room Status of the Room "
											+ OperaPropConfig.getDataSetForCode("Rooms", "DS_41")
											+ "(dedicated to this operation) as 'Inspected'</b>");
							setHousekeepingRoomStatus("DS_01");
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Fetching rooms to ensure that room is available and inspected</b> ");
							String roomNumber = fetchHotelRooms("DS_17");
							if (!roomNumber.equals("error")) {
								if (roomNumber.equals("na")) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Creating Room When Inspected Rooms are not avaialable</b>");
									roomNumber = createRoom("RoomMaint");
									if (!roomNumber.equals("error")) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Setting Houskeeping Status of the Created Room " + roomNumber
														+ " as 'Inspected'</b>");
										setHousekeepingRoomStatus("DS_01");
									}
								}

								/************
								 * Operation Check In : Performing the Check In
								 * operation
								 ****************/
								HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
								String checkInReq = WSClient.createSOAPMessage("HTNGExtCheckIn", "DS_02");
								String CheckInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(CheckInRes, "CheckInResponse_Result", false)) {
									if (WSAssert.assertIfElementValueEquals(CheckInRes,
											"CheckInResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										flag = true;
										String checkin = WSClient.getDBRow(WSClient.getQuery("QS_01"))
												.get("RESV_STATUS");
										if (WSAssert.assertEquals("CHECKED IN", checkin, true)) {
											WSClient.writeToReport(LogStatus.PASS,
													"<b>" + "ReservationStatus->Expected : CHECKED IN      ACTUAL :  "
															+ checkin + "</b>");

										} else {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b>" + "ReservationStatus->Expected : CHECKED IN      ACTUAL :  "
															+ checkin + "</b>");
										}
										WSClient.writeToReport(LogStatus.PASS, "Check In is successful");
									}

									// Checking for result text
									if (WSAssert.assertIfElementExists(CheckInRes, "Result_Text_TextElement", true)) {
										String message = WSClient.getElementValue(CheckInRes, "Result_Text_TextElement",
												XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>"
												+ "The text displayed in the response is    :     " + message + "</b>");
									}

								}

							}
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked :  prerequisites for RateCode or RoomType or SourceCode or MarketCode or PaymentMethod**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// checking out reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals("") && flag == true) {
					CheckoutReservation.checkOutReservation("DS_01");
				} else if (!WSClient.getData("{var_resvId}").equals("")) {
					CancelReservation.cancelReservation("DS_02");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			}
		}
	}

	@Test(groups = { "minimumRegression", "CheckIn", "HTNG2008BExt", "HTNG" })

	/*
	 * Method to check the check in operation by passing reservation id,resort
	 * id and room number
	 */

	public void checkIn_Ext_40936() {
		try {
			String testName = "checkIn_Ext_40936";
			WSClient.startTest(testName, "Verify that the checking in of an invalid reservation is unsuccessful",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			OPERALib.setOperaHeader(uname);
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_extResort}", resortExtValue);

			/************
			 * Operation Check In : Performing the Check In operation
			 ****************/
			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
			String checkInReq = WSClient.createSOAPMessage("HTNGExtCheckIn", "DS_03");
			String CheckInRes = WSClient.processSOAPMessage(checkInReq);

			if (WSAssert.assertIfElementExists(CheckInRes, "CheckInResponse_Result", false)) {
				if (WSAssert.assertIfElementValueEquals(CheckInRes, "CheckInResponse_Result_resultStatusFlag", "FAIL",
						false)) {

					// Checking for result text
					if (WSAssert.assertIfElementExists(CheckInRes, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(CheckInRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "CheckIn", "HTNG2008BExt", "HTNG" })
	/*
	 * Method to check the check in operation by passing reservation id,resort
	 * id and room number when dirty room is assigned
	 */

	public void checkIn_Ext_41277() {
		try {
			String testName = "checkIn_Ext_41277";
			WSClient.startTest(testName, "Verify that the checking in of reservation is unsuccessful to a dirty room ",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				// boolean prerequisite = true;
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				flag = false;

				/*************
				 * Prerequisite 1: Creating new profile by fetching values
				 * required
				 ******************/

				String operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileId}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + operaProfileID + "</b>");
					/*************
					 * Prerequisite 2: Fetch Details for room type,rate
					 * code,source code,market code
					 ******************/

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					// WSClient.setData("{VAR_ROOMTYPE}",
					// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_05"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					String reservationId = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!reservationId.equals("error")) {
						WSClient.setData("{var_resvId}", reservationId);
						System.out.println(reservationId);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: " + reservationId + "</b>");
						WSClient.setData("{var_extResort}", resortExtValue);

						/************
						 * Prerequisite 3: Fetching available Hotel rooms which
						 * are vacant and inspected to assign a room for
						 * checking in
						 ****************/
						WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_05"));
						WSClient.setData("{var_roomNumber}", OperaPropConfig.getDataSetForCode("Rooms", "DS_41"));
						String roomNumber = WSClient.getData("{var_roomNumber}");
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Setting Room Status of the Room "
										+ OperaPropConfig.getDataSetForCode("Rooms", "DS_41")
										+ "(dedicated to this operation) as 'Dirty'</b>");
						setHousekeepingRoomStatus("DS_06");
						List<LinkedHashMap<String, String>> rooms = WSClient
								.getDBRows(WSClient.getQuery("HTNGExtCheckIn", "QS_05"));

						for (int i = 0; i < rooms.size(); i++) {
							if (rooms.get(i).get("ROOM_STATUS").equals("IP")
									&& rooms.get(i).get("ROOM_STATUS").equals("CL")) {
								// WSClient.setData("{var_roomNumber}",
								// rooms.get(i).get("ROOM"));
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Setting room Status of all Clean and Inspected Rooms in that room Type as 'Dirty'</b>");
								setHousekeepingRoomStatus("DS_06");
							}
						}
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Fetching rooms to ensure that room is available</b> ");
						roomNumber = fetchHotelRooms("DS_18");
						if (roomNumber.equals("")) {
							WSClient.writeToReport(LogStatus.INFO, "<b>Creating Room When no available room</b>");
							roomNumber = createRoom("RoomMaint");
							if (!roomNumber.equals("error")) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Setting ROOM Status of Created Room" + roomNumber + " as 'Dirty'</b>");
								setHousekeepingRoomStatus("DS_06");
							}
						} else
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Setting room Status of the fetched room as 'Dirty'</b>");
						setHousekeepingRoomStatus("DS_06");

						if (!roomNumber.equals("")) {

							String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_03");
							String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

							if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

								WSClient.writeToReport(LogStatus.INFO, "Assign Room Successful");
								/************
								 * Operation Check In : Performing the Check In
								 * operation
								 ****************/
								HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
								String checkInReq = WSClient.createSOAPMessage("HTNGExtCheckIn", "DS_01");
								String CheckInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(CheckInRes, "CheckInResponse_Result", false)) {

									if (WSAssert.assertIfElementValueEquals(CheckInRes,
											"CheckInResponse_Result_resultStatusFlag", "FAIL", false)) {

										// Checking for result text
										if (WSAssert.assertIfElementExists(CheckInRes, "Result_Text_TextElement",
												true)) {
											String message = WSClient.getElementValue(CheckInRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>" + "The text displayed in the response is    :     " + message
															+ "</b>");
										}
									} else {
										flag = true;

										WSClient.writeToReport(LogStatus.FAIL, "Check In is successful!!!ERROR");
									}
								}
							} else
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite-Assign Room blocked");
						}

					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked :  prerequisites for RateCode or RoomType or SourceCode or MarketCode or PaymentMethod**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// checking out reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals("") && flag == true) {
					CheckoutReservation.checkOutReservation("DS_01");
				} else if (!WSClient.getData("{var_resvId}").equals("")) {
					CancelReservation.cancelReservation("DS_02");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			}
		}
	}

	@Test(groups = { "fullRegression", "CheckIn", "HTNG2008BExt", "HTNG" })
	public void checkIn_Ext_352() {
		try {

			String testName = "checkIn_Ext_352";
			WSClient.startTest(testName,
					"Verifying the error message response when the reservationID which had already in checked-in status is passed on the CheckInRequest.",
					"fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				String profileID = "";
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				OPERALib.setOperaHeader(uname);
				String roomNumber = "";

				// Prerequisite 1 - create profile

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
					// Prerequisite 2:Create a Reservation
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					String resvID = CreateReservation.createReservation("DS_12").get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						// Prerequisite 3: Fetching available Hotel rooms with
						// room type

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
						String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

						if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true)
								&& WSAssert.assertIfElementExists(fetchHotelRoomsRes,
										"FetchHotelRoomsRS_HotelRooms_Room", true)) {

							roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
									"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

						} else {

							// Prerequisite 4: Creating a room to assign

							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
							String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

							if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

								roomNumber = WSClient.getElementValue(createRoomReq,
										"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
							}
						}
						if (!roomNumber.equals("")) {
							WSClient.setData("{var_roomNumber}", roomNumber);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Successfully fetched the room -> Room number : " + roomNumber + "</b>");

							// Prerequisite 5: Changing the room status to
							// inspected
							// to assign the room for checking in

							String setHousekeepingRoomStatusReq = WSClient
									.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
							String setHousekeepingRoomStatusRes = WSClient
									.processSOAPMessage(setHousekeepingRoomStatusReq);

							if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
									"SetHousekeepingRoomStatusRS_Success", true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Successfully changed the status of room</b>");
								// Prerequisite 6: Assign Room

								String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
								String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

								if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

									// Prerequisite 7: CheckIn Reservation

									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");

									String OperacheckInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
									String checkInRes = WSClient.processSOAPMessage(OperacheckInReq);

									if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success",
											true)) {

										WSClient.writeToReport(LogStatus.INFO,
												"<b>Successfully checked in the reservation</b>");

										// *****OWS Cancel CheckIn*********//

										HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
										String checkInReq = WSClient.createSOAPMessage("HTNGExtCheckIn", "DS_01");
										String CheckInRes = WSClient.processSOAPMessage(checkInReq);

										if (WSAssert.assertIfElementExists(CheckInRes, "CheckInResponse_Result",
												false)) {
											if (WSAssert.assertIfElementValueEquals(CheckInRes,
													"CheckInResponse_Result_resultStatusFlag", "FAIL", false)) {

												// Checking for result text
												if (WSAssert.assertIfElementExists(CheckInRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(CheckInRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");
												}
											}
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING,
												"Blocked : Unable to checkin reservation");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to assign room");
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Blocked : Unable to change the status of room to vacant and inspected");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to fetch room");
						}

					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "targetedRegression", "CheckIn", "HTNG2008BExt", "HTNG", "checkIn_Ext_353" })

	/*
	 * Method to check the check in operation by passing reservation id,resort
	 * id and room number
	 */

	public void checkIn_Ext_353() {
		try {
			String testName = "checkIn_Ext_353";
			WSClient.startTest(testName,
					"Verify that the checking in of reservation is successful when resvId,resortId,roomNumber are passed in request ",
					"targetedRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				flag = false;
				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_parameter}", "SHOW_INSPECTED_ROOMS");
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking the parameters and setting them if required</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("Y")) {
					Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}
				if (!Parameter.equals("error")) {

					/*************
					 * Prerequisite 1: Creating new profile by fetching values
					 * required
					 ******************/

					String operaProfileID = CreateProfile.createProfile("DS_01");
					if (!operaProfileID.equals("error")) {
						System.out.println(operaProfileID);
						WSClient.setData("{var_profileId}", operaProfileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + operaProfileID + "</b>");
						/*************
						 * Prerequisite 2: Fetch Details for room type,rate
						 * code,source code,market code
						 ******************/

						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_05"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						String reservationId = CreateReservation.createReservation("DS_13").get("reservationId");
						if (!reservationId.equals("error")) {
							WSClient.setData("{var_resvId}", reservationId);
							System.out.println(reservationId);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: " + reservationId + "</b>");
							WSClient.setData("{var_extResort}", resortExtValue);

							/************
							 * Prerequisite 3: Fetching available Hotel rooms
							 * which are vacant and inspected to assign a room
							 * for checking in
							 ****************/
							WSClient.setData("{var_roomNumber}", OperaPropConfig.getDataSetForCode("Rooms", "DS_41"));
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Setting Room status for room Number(dedicated to this operation) "
											+ OperaPropConfig.getDataSetForCode("Rooms", "DS_41")
											+ "as 'Inspected' </b>");
							setHousekeepingRoomStatus("DS_01");
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Fetching rooms to ensure that room is available and inspected</b> ");
							String roomNumber = fetchHotelRooms("DS_19");
							if (!roomNumber.equals("error")) {
								if (roomNumber.equals("na")) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Creating Room as Room not available to checkin guest</b> ");

									roomNumber = createRoom("RoomMaint");
									if (!roomNumber.equals("error")) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Setting Room status for created room " + roomNumber
														+ "as 'Inspected'</b> ");
										setHousekeepingRoomStatus("DS_01");

									}
								}
								/************
								 * Operation Check In : Performing the Check In
								 * operation
								 ****************/
								HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

								String checkInReq = WSClient.createSOAPMessage("HTNGExtCheckIn", "DS_01");
								String CheckInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(CheckInRes, "CheckInResponse_Result", false)) {
									if (WSAssert.assertIfElementValueEquals(CheckInRes,
											"CheckInResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										flag = true;
										String checkin = WSClient.getDBRow(WSClient.getQuery("QS_01"))
												.get("RESV_STATUS");

										if (WSAssert.assertEquals("CHECKED IN", checkin, true)) {
											WSClient.writeToReport(LogStatus.PASS,
													"<b>" + "ReservationStatus->Expected : CHECKED IN      ACTUAL :  "
															+ checkin + "</b>");

										} else {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b>" + "ReservationStatus->Expected : CHECKED IN      ACTUAL :  "
															+ checkin + "</b>");
										}
										WSClient.writeToReport(LogStatus.PASS, "Check In is successful");
									}
									// Checking for result text
									if (WSAssert.assertIfElementExists(CheckInRes, "Result_Text_TextElement", true)) {
										String message = WSClient.getElementValue(CheckInRes, "Result_Text_TextElement",
												XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>"
												+ "The text displayed in the response is    :     " + message + "</b>");
									}

								}
							}
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked :  prerequisites for RateCode or RoomType or SourceCode or MarketCode or PaymentMethod**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// checking out reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals("") && flag == true) {
					CheckoutReservation.checkOutReservation("DS_01");
				} else if (!WSClient.getData("{var_resvId}").equals("")) {
					CancelReservation.cancelReservation("DS_02");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			}
		}
	}

	@Test(groups = { "minimumRegression", "CheckIn", "HTNG2008BExt", "HTNG" })

	/*
	 * Method to check the check in operation by passing reservation id,resort
	 * id and room number
	 */

	public void checkIn_Ext_345() {
		try {
			String testName = "checkIn_Ext_345";
			WSClient.startTest(testName,
					"Verify that the checking in is unsuccessful when mandatory field reservationID is missed in request",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			OPERALib.setOperaHeader(uname);
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_resvId}", " ");

			/************
			 * Operation Check In : Performing the Check In operation
			 ****************/
			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
			String checkInReq = WSClient.createSOAPMessage("HTNGExtCheckIn", "DS_02");
			String CheckInRes = WSClient.processSOAPMessage(checkInReq);

			if (WSAssert.assertIfElementExists(CheckInRes, "CheckInResponse_Result", false)) {
				if (WSAssert.assertIfElementValueEquals(CheckInRes, "CheckInResponse_Result_resultStatusFlag", "FAIL",
						false)) {

					// Checking for result text
					if (WSAssert.assertIfElementExists(CheckInRes, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(CheckInRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "fullRegression", "CheckIn", "HTNG2008BExt", "HTNG" })

	/*
	 * Method to check the check in operation by passing reservation id,resort
	 * id and room number
	 */

	public void checkIn_Ext_351() {
		try {
			String testName = "checkIn_Ext_351";
			WSClient.startTest(testName,
					"Verifying the error message response when invalid ReservationID and valid ResortId are passed on the CheckInRequest.",
					"fullRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			OPERALib.setOperaHeader(uname);
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_extResort}", resortExtValue);

			/************
			 * Operation Check In : Performing the Check In operation
			 ****************/
			HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
			String checkInReq = WSClient.createSOAPMessage("HTNGExtCheckIn", "DS_03");
			String CheckInRes = WSClient.processSOAPMessage(checkInReq);

			if (WSAssert.assertIfElementExists(CheckInRes, "CheckInResponse_Result", false)) {
				if (WSAssert.assertIfElementValueEquals(CheckInRes, "CheckInResponse_Result_resultStatusFlag", "FAIL",
						false)) {

					// Checking for result text
					if (WSAssert.assertIfElementExists(CheckInRes, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(CheckInRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "fullRegression", "CheckIn", "HTNG2008BExt", "HTNG" })

	/*
	 * Method to check the check in operation by passing reservation id,resort
	 * id and room number when no inspected room is present
	 */

	public void checkIn_Ext_355() {
		try {
			String testName = "checkIn_Ext_355";
			WSClient.startTest(testName,
					"Verify that error exists when no rooms available under the selected room category is given in the request",
					"fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				// boolean prerequisite = true;
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				flag = false;
				/*************
				 * Prerequisite 1: Creating new profile by fetching values
				 * required
				 ******************/

				String operaProfileID = CreateProfile.createProfile("DS_01");
				if (!operaProfileID.equals("error")) {
					System.out.println(operaProfileID);
					WSClient.setData("{var_profileId}", operaProfileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + operaProfileID + "</b>");
					/*************
					 * Prerequisite 2: Fetch Details for room type,rate
					 * code,source code,market code
					 ******************/

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_09"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					String reservationId = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!reservationId.equals("error")) {
						WSClient.setData("{var_resvId}", reservationId);
						System.out.println(reservationId);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: " + reservationId + "</b>");
						WSClient.setData("{var_extResort}", resortExtValue);

						/************
						 * Operation Check In : Performing the Check In
						 * operation
						 ****************/
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String checkInReq = WSClient.createSOAPMessage("HTNGExtCheckIn", "DS_02");
						String CheckInRes = WSClient.processSOAPMessage(checkInReq);

						if (WSAssert.assertIfElementExists(CheckInRes, "CheckInResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(CheckInRes,
									"CheckInResponse_Result_resultStatusFlag", "FAIL", false))
								;

							// Checking for result text
							if (WSAssert.assertIfElementExists(CheckInRes, "Result_Text_TextElement", true)) {
								String message = WSClient.getElementValue(CheckInRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "The text displayed in the response is    :     " + message + "</b>");
							}

						}

					}

				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"************Blocked :  prerequisites for RateCode or RoomType or SourceCode or MarketCode or PaymentMethod**********");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// checking out reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals("") && flag == true) {
					CheckoutReservation.checkOutReservation("DS_01");
				} else if (!WSClient.getData("{var_resvId}").equals("")) {
					CancelReservation.cancelReservation("DS_02");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			}
		}
	}

}