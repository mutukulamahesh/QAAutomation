package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2008b.ext;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.AssignRoom;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CheckinReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CheckoutReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateRoom;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchHotelRooms;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.SetHousekeepingRoomStatus;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class UpdateRoomStatus extends WSSetUp {

	String profileID = "", queueResvParameterEnabled = "";
	String resvID1 = "", resvID = "";

	public boolean changeToOccupied() {
		try {
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);

				String uname = OPERALib.getUserName();

				OPERALib.setOperaHeader(uname);
				String roomNumber = "";

				// Prerequisite 1 - create profile

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					// WSClient.setData("{VAR_RATEPLANCODE}",
					// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					// WSClient.setData("{VAR_RATEPLANCODE}", "CKRRC");
					// WSClient.setData("{VAR_ROOMTYPE}",
					// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					// WSClient.setData("{var_RoomType}",
					// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

					// WSClient.setData("{VAR_RATEPLANCODE}",
					// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));

					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					// Prerequisite 2 Create Reservation

					HashMap<String, String> resv = CreateReservation.createReservation("DS_12");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: " + resvID + "</b>");
						WSClient.setData("{var_resvId}", resvID);

						// WSClient.setData("{var_resvId}", "2212659802");

						// Prerequisite 3: Fetching available Hotel rooms with
						// room type

						roomNumber = FetchHotelRooms.fetchHotelRooms("DS_03");
						// Prerequisite 4: Creating a room to assign
						if (roomNumber.equals("error")) {
							roomNumber = CreateRoom.createRoom("RoomMaint");
						}

						WSClient.setData("{var_roomNumber}", roomNumber);

						// Prerequisite 5: Changing the room status to inspected
						// to assign the room for checking in

						if (SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01")) {

							// Prerequisite 6: Assign Room

							if (AssignRoom.assignRoom("DS_01")) {

								// Prerequisite 7: CheckIn Reservation

								if (CheckinReservation.checkinReservation("DS_01")) {
									WSClient.setData("{var_RoomNo}", roomNumber);
									return true;
								}
							}

						}

					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		return false;
	}

	public boolean shareReservation() {
		try {

			String profileID1 = CreateProfile.createProfile("DS_01");
			if (!profileID1.equals("error")) {
				WSClient.setData("{var_profileId}", profileID1);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID1 + "</b>");
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				// Prerequisite 2 - Create Reservation

				HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
				String resvID = resv.get("reservationId");

				if (!resvID.equals("error")) {

					WSClient.setData("{var_resvId}", resvID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");

					String profileID2 = CreateProfile.createProfile("DS_01");
					if (!profileID2.equals("error")) {
						WSClient.setData("{var_profileId}", profileID2);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID2 + "</b>");

						HashMap<String, String> resv1 = CreateReservation.createReservation("DS_04");
						String resvID1 = resv1.get("reservationId");

						if (!resvID1.equals("error")) {

							WSClient.setData("{var_resvId1}", resvID1);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID1 + "</b>");

							// Prerequisite 4 : combining Reservation

							String combineReq = WSClient.createSOAPMessage("CombineShareReservations", "DS_01");
							String combineRes = WSClient.processSOAPMessage(combineReq);

							if (WSAssert.assertIfElementExists(combineRes, "CombineShareReservationsRS_Success",
									true)) {

								String combineResID = WSClient.getElementValue(combineRes,
										"Reservation_ReservationIDList_UniqueID_ID", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully combined the reservation.</b>");
								WSClient.setData("{var_resvId}", combineResID);
								return true;

							}
						}
					}
				}
			}
		} catch (Exception e) {
		}
		return false;

	}

	public boolean fetchVacant() {
		try {
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();

				OPERALib.setOperaHeader(uname);
				String roomNumber = "";

				// Prerequisite 1 - create profile

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					// Prerequisite 2 Create Reservation

					HashMap<String, String> resv = CreateReservation.createReservation("DS_12");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: " + resvID + "</b>");
						WSClient.setData("{var_resvId}", resvID);

						// Prerequisite 3: Fetching available Hotel rooms with
						// room type

						roomNumber = FetchHotelRooms.fetchHotelRooms("DS_03");
						// Prerequisite 4: Creating a room to assign
						if (roomNumber.equals("error")) {
							roomNumber = CreateRoom.createRoom("RoomMaint");
						}

						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.setData("{var_RoomNo}", roomNumber);

						// Prerequisite 5: Changing the room status to inspected
						// to assign the room for checking in

						SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
						return true;

					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		return false;
	}

	@Test(groups = { "sanity", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are Dirty, not reserved and if there is no room
	 * found, then creating a room
	 *****/
	/*****
	 * Updating the Room Status to Clean and validating if the room status
	 * obtained from the database is Clean
	 *****/
	public void updateRoomStatus_4527() {
		try {
			String testName = "updateRoomStatus_4527";
			WSClient.startTest(testName, "Verify that room status updated to Clean", "sanity");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");
				if (room_num.equals("error")) {
					room_num = CreateRoom.createRoom("RoomMaint");
				}
				if (!room_num.equals("error")) {
					WSClient.setData("{var_RoomNo}", room_num);
					if (SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_04")) {
						WSClient.writeToReport(LogStatus.INFO, "<b> Room Status is updated to DIRTY </b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Clean");
						String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							String query = WSClient.getQuery("QS_01");
							HashMap<String, String> roomRecord = WSClient.getDBRow(query);
							String status = WSClient.getElementValue(UpdateRoomStatusReq,
									"UpdateRoomStatusRequest_RoomStatus", XMLType.REQUEST);

							// Validating the room status from the request with
							// the
							// Database
							// value
							boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);
							String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_RoomStatus");

							if (result == true) {
								WSClient.writeToReport(LogStatus.PASS,
										reqXpath + " is the Xpath where the value is equal to the database value that is "
												+ roomRecord.get("ROOM_STATUS"));
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										reqXpath + "is the Xpath where the value is not equal to the database value that is "
												+ roomRecord.get("ROOM_STATUS"));

							}

						}
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
								true)) {
							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The text displayed in the response is    :     " + message + "</b>");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are Clean, not reserved and if there is no room
	 * found, then creating a room
	 *****/
	/*****
	 * Updating the FO Status of a Dirty room to OCCUPIED and validating if an
	 * error is populated
	 *****/
	public void updateRoomStatus_1352() {
		try {
			String testName = "updateRoomStatus_1352";
			WSClient.startTest(testName,
					"Verify that an error message is populated in the response when updating Front Office Status of a DIRTY ROOM to Occupied",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
					WSClient.setData("{var_resort}", OPERALib.getResort());

					String resortOperaValue = OPERALib.getResort();
					String interfaceName = HTNGLib.getHTNGInterface();
					String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
					WSClient.setData("{var_extResort}", resortExtValue);

					// Prerequisite :Fetching rooms that are not reserved
					OPERALib.setOperaHeader(OPERALib.getUserName());
					WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");
					if (room_num.equals("error")) {
						room_num = CreateRoom.createRoom("RoomMaint");
					}
					if (!room_num.equals("error")) {
						WSClient.setData("{var_RoomNo}", room_num);
						if (SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_04")) {
							HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
									HTNGLib.getInterfaceFromAddress());
							// Updating the FO status of the Created/fetched
							// room which is Dirty to Occupied
							String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "FO");
							String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
							if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_resultStatusFlag", true)) {
								if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
										"UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL", false)) {

									if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
											"Result_Text_TextElement", true)) {
										String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
												"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>"
												+ "The text displayed in the response is    :     " + message + "</b>");
									}

								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"No error is populated on the response when FO status of a DIRTY ROOM is being updated to OCCUPIED");

								}
								String fo_status = WSClient.getDBRow(WSClient.getQuery("QS_05")).get("FO_STATUS");
								if (WSAssert.assertEquals("Vacant", fo_status, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"FO Status is not Updated in DB as expected");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "FO Status got Updated in DB");
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG","updateRoomStatus_1359" })
	/*****
	 * Fetching the rooms that have reservation status as Arrived and if there
	 * is no room found, then creating a room
	 *****/
	/*****
	 * Updating the TurnDown Status to Required and validating if an error is
	 * populated
	 *****/
	public void updateRoomStatus_1359() {
		try {

			String testName = "updateRoomStatus_1359";

			WSClient.startTest(testName,
					"Verify that an error message is populated in the response when updating TurnDown Status to Required without room being checked in",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);

				// Prerequisite :Fetching rooms that have reservation status as
				// arrived
				OPERALib.setOperaHeader(OPERALib.getUserName());
				String profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID + "</b>");

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					HashMap<String, String> id = CreateReservation.createReservation("DS_21");
					if (!id.get("reservationId").equals("error")) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:- " + id.get("reservationId") + "</b>");
						WSClient.setData("{var_resvId}", id.get("reservationId"));
						String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");
						if (room_num.equals("error")) {
							room_num = CreateRoom.createRoom("RoomMaint");
						}
						if (!room_num.equals("error")) {
							WSClient.setData("{var_RoomNo}", room_num);
							WSClient.setData("{var_roomNumber}", room_num);
							if (SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01")) {
								if (AssignRoom.assignRoom("DS_01")) {
									HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
											HTNGLib.getInterfaceFromAddress());

									// Updating the TurnDown status of the
									// Created/fetched room to
									// Required

									String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus",
											"TDR");
									String UpdateRoomStatusResponseXML = WSClient
											.processSOAPMessage(UpdateRoomStatusReq);
									if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
											"UpdateRoomStatusResponse_Result_resultStatusFlag", true)) {
										WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
												"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false);

										String turnDownStatus = WSClient.getDBRow(WSClient.getQuery("QS_04"))
												.get("TURNDOWN_STATUS");
										if (turnDownStatus == null) {
											WSClient.writeToReport(LogStatus.PASS,
													"TurnDownStatus is not Updated in DB as expected");
										} else {
											WSClient.writeToReport(LogStatus.FAIL, "TurnDownStatus got Updated in DB");
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CancelReservation.cancelReservation("DS_02");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are Clean, not reserved and if there is no room
	 * found, then creating a room
	 *****/
	/*****
	 * Updating the Room Status to Dirty and validating if the room status
	 * obtained from the database is Dirty
	 *****/
	public void updateRoomStatus_1319() {
		try {
			String testName = "updateRoomStatus_1319";
			WSClient.startTest(testName, "Verify that room status is updated to Dirty", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);

				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");
				if (room_num.equals("error")) {
					room_num = CreateRoom.createRoom("RoomMaint");
				}
				if (!room_num.equals("error")) {
					WSClient.setData("{var_RoomNo}", room_num);
					WSClient.setData("{var_roomNumber}", room_num);
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());

					// Updating the Room status of the Created/fetched room to
					// Dirty
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Dirty");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						String query = WSClient.getQuery("QS_01");
						HashMap<String, String> roomRecord = WSClient.getDBRow(query);
						String status = WSClient.getElementValue(UpdateRoomStatusReq,
								"UpdateRoomStatusRequest_RoomStatus", XMLType.REQUEST);

						// Validating the room status from the request with the
						// Database
						// value
						boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);
						String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_RoomStatus");

						if (result == true) {
							WSClient.writeToReport(LogStatus.PASS,
									reqXpath + " is the Xpath where the value is equal to the database value that is "
											+ roomRecord.get("ROOM_STATUS"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									reqXpath + "is the Xpath where the value is not equal to the database value that is "
											+ roomRecord.get("ROOM_STATUS"));

						}

					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_roomNumber}").equals("")) {
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are Not Reserved, not reserved and if there is no
	 * room found, then creating a room
	 *****/
	/*****
	 * Updating the Room Status to Inspected and validating if the room status
	 * obtained from the database is Inspected
	 *****/
	public void updateRoomStatus_1318() {
		try {
			String testName = "updateRoomStatus_1318";
			WSClient.startTest(testName, "Verify that room status is updated to Inspected", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);

				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");
				if (room_num.equals("error")) {
					room_num = CreateRoom.createRoom("RoomMaint");
				}
				if (!room_num.equals("error")) {
					WSClient.setData("{var_RoomNo}", room_num);
					WSClient.setData("{var_roomNumber}", room_num);
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());

					// Updating the Room status of the Created/fetched room to
					// Inspected
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Inspected");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						String query = WSClient.getQuery("QS_01");
						HashMap<String, String> roomRecord = WSClient.getDBRow(query);
						String status = WSClient.getElementValue(UpdateRoomStatusReq,
								"UpdateRoomStatusRequest_RoomStatus", XMLType.REQUEST);

						// Validating the room status from the request with the
						// Database
						// value
						boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);
						String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_RoomStatus");

						if (result == true) {
							WSClient.writeToReport(LogStatus.PASS,
									reqXpath + " is the Xpath where the value is equal to the database value that is "
											+ roomRecord.get("ROOM_STATUS"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									reqXpath + "is the Xpath where the value is not equal to the database value that is "
											+ roomRecord.get("ROOM_STATUS"));

						}

					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_roomNumber}").equals("")) {
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*****
	 * Fetching the rooms that are not reserved and if there is no room found,
	 * then creating a room
	 *****/
	/*****
	 * Updating the Room Status to Pickup and validating if the room status
	 * obtained from the database is Pickup
	 *****/
	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	public void updateRoomStatus_1320() {
		try {

			String testName = "updateRoomStatus_1320";
			WSClient.startTest(testName, "Verify that room status is updated to Pickup", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);

				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");
				if (room_num.equals("error")) {
					room_num = CreateRoom.createRoom("RoomMaint");
				}
				if (!room_num.equals("error")) {
					WSClient.setData("{var_RoomNo}", room_num);
					WSClient.setData("{var_roomNumber}", room_num);
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());

					// Updating the Room status of the Created/fetched room to
					// Pickup

					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Pickup");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						String query = WSClient.getQuery("QS_01");
						HashMap<String, String> roomRecord = WSClient.getDBRow(query);
						String status = WSClient.getElementValue(UpdateRoomStatusReq,
								"UpdateRoomStatusRequest_RoomStatus", XMLType.REQUEST);

						// Validating the room status from the request with the
						// Database
						// value
						boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);
						String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_RoomStatus");

						if (result == true) {
							WSClient.writeToReport(LogStatus.PASS,
									reqXpath + " is the Xpath where the value is equal to the database value that is "
											+ roomRecord.get("ROOM_STATUS"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									reqXpath + "is the Xpath where the value is not equal to the database value that is "
											+ roomRecord.get("ROOM_STATUS"));

						}

					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_roomNumber}").equals("")) {
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are occupied and if there is no room found, then
	 * creating a room
	 *****/
	/*****
	 * Updating the Housekeeping Status to Vacant and validating if the
	 * Housekeeping status obtained from the database is vacant
	 *****/
	public void updateRoomStatus_1326() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateRoomStatus_1326";
			WSClient.startTest(testName, "Verify that housekeeping status is updated to Vacant", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				prerequisite_block_flag = changeToOccupied();

				// Updating the Front Office status of the Created/fetched room
				// to Vacant
				if (prerequisite_block_flag == true) {
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Vacant");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						String query = WSClient.getQuery("QS_02");
						HashMap<String, String> roomRecord = WSClient.getDBRow(query);
						String status = WSClient.getElementValue(UpdateRoomStatusReq,
								"UpdateRoomStatusRequest_HKStatus", XMLType.REQUEST);

						// Validating the room status from the request with the
						// Database
						// value
						boolean result = WSAssert.assertEquals(status, roomRecord.get("HK_STATUS"), true);
						String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_HKStatus");

						if (result == true) {
							WSClient.writeToReport(LogStatus.PASS,
									reqXpath + " is the Xpath where the value is equal to the database value that is "
											+ roomRecord.get("HK_STATUS"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									reqXpath + "is the Xpath where the value is not equal to the database value that is "
											+ roomRecord.get("HK_STATUS"));

						}

					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CheckoutReservation.checkOutReservation("DS_01");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "fullRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are occupied and if there is no room found, then
	 * creating a room
	 *****/
	/*****
	 * Updating the Housekeeping Status to Vacant and validating if the
	 * Housekeeping status obtained from the database is vacant
	 *****/
	public void updateRoomStatus_1330() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateRoomStatus_1330";
			WSClient.startTest(testName,
					"Verify that an error message is populated in response when updating housekeeping status with invalid status",
					"fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				OPERALib.setOperaHeader(OPERALib.getUserName());
				prerequisite_block_flag = changeToOccupied();

				// Updating the Front Office status of the Created/fetched room
				// to Vacant
				if (prerequisite_block_flag == true) {
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Vacant1");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL", false)) {
						String query = WSClient.getQuery("QS_02");
						HashMap<String, String> roomRecord = WSClient.getDBRow(query);
						String status = WSClient.getElementValue(UpdateRoomStatusReq,
								"UpdateRoomStatusRequest_HKStatus", XMLType.REQUEST);

						// Validating the room status from the request with the
						// Database
						// value
						boolean result = WSAssert.assertEquals(status, roomRecord.get("HK_STATUS"), true);
						String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_HKStatus");

						if (result == true) {
							WSClient.writeToReport(LogStatus.FAIL, "HK status is updated");
						} else {
							WSClient.writeToReport(LogStatus.PASS, "HK status is not updated");

						}

					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CheckoutReservation.checkOutReservation("DS_01");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are vacant and if there is no room found, then
	 * creating a room
	 *****/
	/*****
	 * Updating the Housekeeping Status to Occupied and validating if the
	 * Housekeeping status obtained from the database is vacant
	 *****/
	public void updateRoomStatus_1327() {
		try {
			String testName = "updateRoomStatus_1327";
			WSClient.startTest(testName, "Verify that housekeeping status is updated to Occupied", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				// Prerequisite :Fetching rooms that are vacant
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");
				if (room_num.equals("error")) {
					room_num = CreateRoom.createRoom("RoomMaint");
				}
				if (!room_num.equals("error")) {
					WSClient.setData("{var_RoomNo}", room_num);
					WSClient.setData("{var_roomNumber}", room_num);

					// Updating the Front Office status of the Created/fetched
					// room
					// to Occupied
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Occupied");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						String query = WSClient.getQuery("QS_02");
						HashMap<String, String> roomRecord = WSClient.getDBRow(query);
						String status = WSClient.getElementValue(UpdateRoomStatusReq,
								"UpdateRoomStatusRequest_HKStatus", XMLType.REQUEST);

						// Validating the room status from the request with the
						// Database
						// value
						boolean result = WSAssert.assertEquals(status, roomRecord.get("HK_STATUS"), true);
						String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_HKStatus");

						if (result == true) {
							WSClient.writeToReport(LogStatus.PASS,
									reqXpath + " is the Xpath where the value is equal to the database value that is "
											+ roomRecord.get("HK_STATUS"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									reqXpath + "is the Xpath where the value is not equal to the database value that is "
											+ roomRecord.get("HK_STATUS"));

						}

					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_roomNumber}").equals("")) {
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are vacant and if there is no room found, then
	 * creating a room
	 *****/
	/*****
	 * Updating the Room Status to Out Of Order and validating if the Front
	 * office status obtained from the database is Out Of Order
	 *****/
	public void updateRoomStatus_1335() {
		try {
			String testName = "updateRoomStatus_1335";
			WSClient.startTest(testName, "Verify that room status is updated to Out of Order", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");
				if (room_num.equals("error")) {
					room_num = CreateRoom.createRoom("RoomMaint");
				}
				if (!room_num.equals("error")) {
					WSClient.setData("{var_RoomNo}", room_num);
					WSClient.setData("{var_roomNumber}", room_num);
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());

					// Updating the Room status of the Created/fetched room to
					// Out
					// Of Order
					String roomStatusReason = OperaPropConfig.getDataSetForCode("OOOSReason", "DS_01");
					WSClient.setData("{var_roomStatusReason}", roomStatusReason);
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "OO");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						String query = WSClient.getQuery("QS_01");
						HashMap<String, String> roomRecord = WSClient.getDBRow(query);
						String status = WSClient.getElementValue(UpdateRoomStatusReq,
								"UpdateRoomStatusRequest_RoomRepair_RepairStatus", XMLType.REQUEST);

						// Validating the room status from the request with the
						// Database
						// value
						boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);
						String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_RoomRepair_RepairStatus");

						if (result == true) {
							WSClient.writeToReport(LogStatus.PASS,
									reqXpath + " is the Xpath where the value is equal to the database value that is "
											+ roomRecord.get("ROOM_STATUS"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									reqXpath + "is the Xpath where the value is not equal to the database value that is "
											+ roomRecord.get("ROOM_STATUS"));

						}

					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			// } finally {
			// try {
			// if(!WSClient.getData("{var_roomNumber}").equals(""))
			// {
			// SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
			// }
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are vacant and if there is no room found, then
	 * creating a room
	 *****/
	/*****
	 * Updating the Room Status to Out Of Service and validating if the Room
	 * status obtained from the database is Out Of Service
	 *****/
	public void updateRoomStatus_1336() {
		try {
			String testName = "updateRoomStatus_1336";
			WSClient.startTest(testName, "Verify that room status is updated to Out of Service", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);

				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");
				if (room_num.equals("error")) {
					room_num = CreateRoom.createRoom("RoomMaint");
				}
				if (!room_num.equals("error")) {
					WSClient.setData("{var_RoomNo}", room_num);
					WSClient.setData("{var_roomNumber}", room_num);
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());

					// Updating the Room status of the Created/fetched room to
					// Out
					// Of Service
					String roomStatusReason = OperaPropConfig.getDataSetForCode("OOOSReason", "DS_01");
					WSClient.setData("{var_roomStatusReason}", roomStatusReason);
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "OS");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						String query = WSClient.getQuery("QS_01");
						HashMap<String, String> roomRecord = WSClient.getDBRow(query);
						String status = WSClient.getElementValue(UpdateRoomStatusReq,
								"UpdateRoomStatusRequest_RoomRepair_RepairStatus", XMLType.REQUEST);

						// Validating the room status from the request with the
						// Database
						// value
						boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);
						String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_RoomRepair_RepairStatus");

						if (result == true) {
							WSClient.writeToReport(LogStatus.PASS,
									reqXpath + " is the Xpath where the value is equal to the database value that is "
											+ roomRecord.get("ROOM_STATUS"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									reqXpath + "is the Xpath where the value is not equal to the database value that is "
											+ roomRecord.get("ROOM_STATUS"));

						}

					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			// } finally {
			// try {
			// if(!WSClient.getData("{var_roomNumber}").equals(""))
			// {
			// SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
			// }
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are occupied and if there is no room found, then
	 * creating a room
	 *****/
	/*****
	 * Updating the Service Status to Make Up and validating if the Service
	 * status obtained from the database is Make up
	 *****/
	public void updateRoomStatus_1346() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateRoomStatus_1346";
			WSClient.startTest(testName, "Verify that Service Status is updated to MakeUp Room", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);

				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				// Prerequisite:If there are no rooms that are occupied then
				// rooms are created

				prerequisite_block_flag = changeToOccupied();

				// Updating the Service status of the Created/fetched room to
				// Make Up
				if (prerequisite_block_flag == true) {
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());

					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "MU");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						String query = WSClient.getQuery("QS_03");
						HashMap<String, String> roomRecord = WSClient.getDBRow(query);
						String status = WSClient.getElementValue(UpdateRoomStatusReq,
								"UpdateRoomStatusRequest_GuestServiceStatus", XMLType.REQUEST);

						// Validating the room status from the request with the
						// Database
						// value
						boolean result = WSAssert.assertEquals(status, roomRecord.get("SERVICE_STATUS"), true);
						String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_GuestServiceStatus");

						if (result == true) {
							WSClient.writeToReport(LogStatus.PASS,
									reqXpath + " is the Xpath where the value is equal to the database value that is "
											+ roomRecord.get("SERVICE_STATUS"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									reqXpath + "is the Xpath where the value is not equal to the database value that is "
											+ roomRecord.get("SERVICE_STATUS"));

						}

					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CheckoutReservation.checkOutReservation("DS_01");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are occupied and if there is no room found, then
	 * creating a room
	 *****/
	/*****
	 * Updating the Service Status to Do Not Disturb and validating if the
	 * Service status obtained from the database is Do Not Disturb
	 *****/
	public void updateRoomStatus_1347() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateRoomStatus_1347";
			WSClient.startTest(testName, "Verify that Service Status is updated to Do Not Disturb Room",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				// Prerequisite :Fetching rooms that are occupied
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				// Prerequisite:If there are no rooms that are occupied then
				// rooms are created

				prerequisite_block_flag = changeToOccupied();

				// Updating the Service status of the Created/fetched room to Do
				// Not Disturb
				if (prerequisite_block_flag == true) {
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "DND");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						String query = WSClient.getQuery("QS_03");
						HashMap<String, String> roomRecord = WSClient.getDBRow(query);
						String status = WSClient.getElementValue(UpdateRoomStatusReq,
								"UpdateRoomStatusRequest_GuestServiceStatus", XMLType.REQUEST);

						// Validating the room status from the request with the
						// Database
						// value
						boolean result = WSAssert.assertEquals(status, roomRecord.get("SERVICE_STATUS"), true);
						String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_GuestServiceStatus");

						if (result == true) {
							WSClient.writeToReport(LogStatus.PASS,
									reqXpath + " is the Xpath where the value is equal to the database value that is "
											+ roomRecord.get("SERVICE_STATUS"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									reqXpath + "is the Xpath where the value is not equal to the database value that is "
											+ roomRecord.get("SERVICE_STATUS"));

						}

					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CheckoutReservation.checkOutReservation("DS_01");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that have reservation status as Arrived and if there
	 * is no room found, then creating a room
	 *****/
	/*****
	 * Updating the TurnDown Status to Required and validating if the TurnDown
	 * status obtained from the database is required
	 *****/
	public void updateRoomStatus_1353() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateRoomStatus_1353";
			WSClient.startTest(testName, "Verify that TurnDown Status is updated to Required", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				// Prerequisite :Fetching rooms that have reservation status as
				// arrived
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				// Prerequisite:If there are no rooms that are in arrived status
				// then
				// rooms are created

				prerequisite_block_flag = changeToOccupied();

				// Updating the TurnDown status of the Created/fetched room to
				// Required
				if (prerequisite_block_flag == true) {
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "TDR");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						String query = WSClient.getQuery("QS_04");
						HashMap<String, String> roomRecord = WSClient.getDBRow(query);
						String status = WSClient.getElementValue(UpdateRoomStatusReq,
								"UpdateRoomStatusRequest_TurnDownStatus", XMLType.REQUEST);

						// Validating the room status from the request with the
						// Database
						// value
						boolean result = WSAssert.assertEquals(status, roomRecord.get("TURNDOWN_STATUS"), true);
						String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_TurnDownStatus");

						if (result == true) {
							WSClient.writeToReport(LogStatus.PASS,
									reqXpath + " is the Xpath where the value is equal to the database value that is "
											+ roomRecord.get("TURNDOWN_STATUS"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									reqXpath + "is the Xpath where the value is not equal to the database value that is "
											+ roomRecord.get("TURNDOWN_STATUS"));

						}

					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CheckoutReservation.checkOutReservation("DS_01");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "fullRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that have reservation status as Arrived and if there
	 * is no room found, then creating a room
	 *****/
	/*****
	 * Updating the TurnDown Status to Required and validating if the TurnDown
	 * status obtained from the database is required
	 *****/
	public void updateRoomStatus_1358() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateRoomStatus_1358";
			WSClient.startTest(testName,
					"Verify that an error message is populated in the response when updating TurnDown Status with an INVALID STATUS",
					"fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);

				// Prerequisite :Fetching rooms that have reservation status as
				// arrived
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				// Prerequisite:If there are no rooms that are in arrived status
				// then
				// rooms are created

				prerequisite_block_flag = changeToOccupied();

				// Updating the TurnDown status of the Created/fetched room to
				// Required
				if (prerequisite_block_flag == true) {
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "TDR1");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL", false)) {
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
								true)) {
							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The text displayed in the response is    :     " + message + "</b>");
						}
					}
					String query = WSClient.getQuery("QS_04");
					HashMap<String, String> roomRecord = WSClient.getDBRow(query);
					String status = WSClient.getElementValue(UpdateRoomStatusReq,
							"UpdateRoomStatusRequest_TurnDownStatus", XMLType.REQUEST);

					// Validating the room status from the request with the
					// Database
					// value
					boolean result = WSAssert.assertEquals(status, roomRecord.get("TURNDOWN_STATUS"), true);
					String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_TurnDownStatus");

					if (result == true) {
						WSClient.writeToReport(LogStatus.FAIL, "Turndown is updated");
					} else {
						WSClient.writeToReport(LogStatus.PASS, "Turndown is NOT updated");

					}

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CheckoutReservation.checkOutReservation("DS_01");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that have reservation status as Arrived and if there
	 * is no room found, then creating a room
	 *****/
	/*****
	 * Updating the TurnDown Status to Completed and validating if the TurnDown
	 * status obtained from the database is completed
	 *****/
	public void updateRoomStatus_1344() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateRoomStatus_1344";
			WSClient.startTest(testName, "Verify that TurnDown status is updated to Completed", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);

				// Prerequisite :Fetching rooms that have reservation status as
				// Arrived
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				// Prerequisite:If there are no rooms that are in arrived status
				// then
				// rooms are created

				prerequisite_block_flag = changeToOccupied();

				// Updating the TurnDown status of the Created/fetched room to
				// Completed
				if (prerequisite_block_flag == true) {
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "TDC");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						String query = WSClient.getQuery("QS_04");
						HashMap<String, String> roomRecord = WSClient.getDBRow(query);
						String status = WSClient.getElementValue(UpdateRoomStatusReq,
								"UpdateRoomStatusRequest_TurnDownStatus", XMLType.REQUEST);

						// Validating the room status from the request with the
						// Database
						// value
						boolean result = WSAssert.assertEquals(status, roomRecord.get("TURNDOWN_STATUS"), true);
						String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_TurnDownStatus");

						if (result == true) {
							WSClient.writeToReport(LogStatus.PASS,
									reqXpath + " is the Xpath where the value is equal to the database value that is "
											+ roomRecord.get("TURNDOWN_STATUS"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									reqXpath + "is the Xpath where the value is not equal to the database value that is "
											+ roomRecord.get("TURNDOWN_STATUS"));

						}

					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CheckoutReservation.checkOutReservation("DS_01");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that have reservation status as Arrived and if there
	 * is no room found, then creating a room
	 *****/
	/*****
	 * Updating the TurnDown Status to Not Required and validating if the
	 * TurnDown status obtained from the database is Not required
	 *****/

	public void updateRoomStatus_1355() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateRoomStatus_1355";
			WSClient.startTest(testName, "Verify that TurnDown Status is updated to Not Required", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				// Prerequisite :Fetching rooms that have reservation status as
				// Arrived

				// Prerequisite:If there are no rooms that are not reserved then
				// rooms are created

				prerequisite_block_flag = changeToOccupied();

				// Updating the TurnDown status of the Created/fetched room to
				// Not Required
				if (prerequisite_block_flag == true) {
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "TDNR");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						String query = WSClient.getQuery("QS_04");
						HashMap<String, String> roomRecord = WSClient.getDBRow(query);
						String status = WSClient.getElementValue(UpdateRoomStatusReq,
								"UpdateRoomStatusRequest_TurnDownStatus", XMLType.REQUEST);

						// Validating the room status from the request with the
						// Database
						// value
						boolean result = WSAssert.assertEquals(status, roomRecord.get("TURNDOWN_STATUS"), true);
						String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_TurnDownStatus");

						if (result == true) {
							WSClient.writeToReport(LogStatus.PASS,
									reqXpath + " is the Xpath where the value is equal to the database value that is "
											+ roomRecord.get("TURNDOWN_STATUS"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									reqXpath + "is the Xpath where the value is not equal to the database value that is "
											+ roomRecord.get("TURNDOWN_STATUS"));

						}

					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CheckoutReservation.checkOutReservation("DS_01");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// @Test(groups = { "minimumRegression", "UpdateRoomStatus",
	// "HTNG2008BExt","HTNG" })
	/*****
	 * Fetching the rooms that have reservation status as Arrived and if there
	 * is no room found, then creating a room
	 *****/
	/*****
	 * Updating the TurnDown Status to Not Required and validating if the
	 * TurnDown status obtained from the database is Not required
	 *****/
	public void updateRoomStatus_40402() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateRoomStatus_40402";
			WSClient.startTest(testName, "Update TurnDown Status to Not Required when the parameter is disabled",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				// Prerequisite :Fetching rooms that have reservation status as

				prerequisite_block_flag = changeToOccupied();

				// Updating the TurnDown status of the Created/fetched room to
				// Not Required
				if (prerequisite_block_flag == true) {
					WSClient.writeToReport(LogStatus.INFO, "<b>****Disabling the parameter : TURNDOWN****</b>");
					WSClient.setData("{var_parameter}", "TURNDOWN");
					WSClient.setData("{var_settingValue}", "N");
					queueResvParameterEnabled = ChangeApplicationParameters.changeApplicationParameter("DS_01",
							"DS_01");
					if (!queueResvParameterEnabled.equals("error")) {

						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "TDNR");
						String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							String query = WSClient.getQuery("QS_04");
							HashMap<String, String> roomRecord = WSClient.getDBRow(query);
							String status = WSClient.getElementValue(UpdateRoomStatusReq,
									"UpdateRoomStatusRequest_TurnDownStatus", XMLType.REQUEST);

							// Validating the room status from the request with
							// the
							// Database
							// value
							boolean result = WSAssert.assertEquals(status, roomRecord.get("TURNDOWN_STATUS"), true);

							if (result == true) {
								WSClient.writeToReport(LogStatus.FAIL,
										"Turndown status is being updated despite the TURNDOWN parameter being disabled");
							} else {
								WSClient.writeToReport(LogStatus.PASS,
										"Turndown status is not being updated as the TURNDOWN parameter is disabled");

							}

						}
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
								true)) {
							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The text displayed in the response is    :     " + message + "</b>");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"Prerequisite blocked---->Change Application Parameter Failed!");
					}

					WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter : TURNDOWN****</b>");
					WSClient.setData("{var_parameter}", "TURNDOWN");
					WSClient.setData("{var_settingValue}", "Y");
					queueResvParameterEnabled = ChangeApplicationParameters.changeApplicationParameter("DS_01",
							"DS_01");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CheckoutReservation.checkOutReservation("DS_01");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are occupied and if there is no room found, then
	 * creating a room
	 *****/
	/*****
	 * Updating the Service Status to Do Not Disturb and validating if the
	 * Service status obtained from the database is Do Not Disturb
	 *****/
	public void updateRoomStatus_42002() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateRoomStatus_42002";
			WSClient.startTest(testName,
					"Verify that an error message is populated in the response when updating Service Status to DND Room with the parameter GUEST_SERVICE_STATUS disabled",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				// Prerequisite :Fetching rooms that are occupied
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				// Prerequisite:If there are no rooms that are occupied then
				// rooms are created

				prerequisite_block_flag = changeToOccupied();

				// String
				// roomStatus=WSClient.getDBRow(WSClient.getQuery("HTNGExtUpdateRoomStatus","QS_03")).get("SERVICE_STATUS");
				// if(roomStatus.equalsIgnoreCase("DoNotDisturb")){
				// String
				// setGuestHousekeepingServiceReq=WSClient.createSOAPMessage("SetGuestHousekeepingServiceRequest",
				// "DS_01");
				// String
				// setGuestHousekeepingServiceRes=WSClient.processSOAPMessage(setGuestHousekeepingServiceReq);
				// if(WSAssert.assertIfElementExists(setGuestHousekeepingServiceRes,
				// "SetGuestHousekeepingServiceRequestRS_Success", true)){
				// String
				// roomStatus1=WSClient.getDBRow(WSClient.getQuery("HTNGExtUpdateRoomStatus","QS_03")).get("SERVICE_STATUS");
				// if(roomStatus1.equalsIgnoreCase("DoNotDisturb"))
				// prerequisite_block_flag = false;
				// }else{
				// prerequisite_block_flag = false;
				// }
				//
				// }
				// Updating the Service status of the Created/fetched room to Do
				// Not Disturb
				if (prerequisite_block_flag == true) {
					WSClient.writeToReport(LogStatus.INFO, "<b>****Disabling the parameter : TURNDOWN****</b>");
					WSClient.setData("{var_parameter}", "GUEST_SERVICE_STATUS");
					WSClient.setData("{var_settingValue}", "N");
					queueResvParameterEnabled = ChangeApplicationParameters.changeApplicationParameter("DS_01",
							"DS_01");
					if (!queueResvParameterEnabled.equals("error")) {

						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "DND");
						String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL", false)) {
							String query = WSClient.getQuery("QS_03");
							HashMap<String, String> roomRecord = WSClient.getDBRow(query);
							String status = WSClient.getElementValue(UpdateRoomStatusReq,
									"UpdateRoomStatusRequest_GuestServiceStatus", XMLType.REQUEST);

							// Validating the room status from the request with
							// the
							// Database
							// value
							boolean result = WSAssert.assertEquals(status, roomRecord.get("SERVICE_STATUS"), true);

							if (result == true) {
								WSClient.writeToReport(LogStatus.FAIL,
										"Guest Service Status is being updated despite the parameter GUEST_SERVICE_STATUS is disabled");
							} else {
								WSClient.writeToReport(LogStatus.PASS,
										"Guest Service Status is not updated as the parameter GUEST_SERVICE_STATUS is disabled");

							}

						}
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
								true)) {
							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The text displayed in the response is    :     " + message + "</b>");
						}

						WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter : TURNDOWN****</b>");
						WSClient.setData("{var_parameter}", "GUEST_SERVICE_STATUS");
						WSClient.setData("{var_settingValue}", "Y");
						queueResvParameterEnabled = ChangeApplicationParameters.changeApplicationParameter("DS_01",
								"DS_01");
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"Prerequisite blocked---->Change Application Parameter Failed!");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CheckoutReservation.checkOutReservation("DS_01");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	//
	// /*****
	// * Fetching the rooms that are not reserved and if there is no room found,
	// * then creating a room
	// *****/
	// /*****
	// * Updating the Room Status to Pickup and validating if the room status
	// * obtained from the database is Pickup
	// *****/
	// @Test(groups = { "minimumRegression", "UpdateRoomStatus",
	// "HTNG2008BExt","HTNG" })
	// public void SetHousekeepingRoomStatusToPickupwithParameter() {
	// try {
	// boolean prerequisite_block_flag = false;
	// String testName = "updateRoomStatus_1320";
	// WSClient.startTest(testName, "Update Room Status to Pickup",
	// "minimumRegression");
	// WSClient.setData("{var_resort}", OPERALib.getResort());
	// // Prerequisite :Fetching rooms that are not reserved
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	//
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// HTNGLib.getInterfaceFromAddress());
	// String fetchHKBoardReq =
	// WSClient.createSOAPMessage("FetchHousekeepingBoard", "NotReserved");
	// String fetchHKBoardResponseXML =
	// WSClient.processSOAPMessage(fetchHKBoardReq);
	// System.out.println(prerequisite_block_flag);
	// if (WSAssert.assertIfElementExists(fetchHKBoardResponseXML,
	// "FetchHousekeepingBoardRS_Errors", true)) {
	// WSClient.writeToReport(LogStatus.WARNING,
	// WSClient.getElementValue(fetchHKBoardResponseXML,
	// "FetchHousekeepingBoardRS_Errors_Error_ShortText", XMLType.RESPONSE));
	// } else {
	// String room_num = WSClient.getElementValue(fetchHKBoardResponseXML,
	// "HousekeepingRoomInfo_HousekeepingRooms_Room_RoomNumber",
	// XMLType.RESPONSE);
	// if (!room_num.equals("*null*") || !room_num.contains("doesn't exist")) {
	//
	// WSClient.writeToReport(LogStatus.PASS,
	// WSClient.getElementValue(fetchHKBoardResponseXML,
	// "HousekeepingRoomInfo_HousekeepingRooms_Room_RoomNumber",
	// XMLType.RESPONSE));
	// WSClient.setData("{var_RoomNo}",
	// WSClient.getElementValue(fetchHKBoardResponseXML,
	// "HousekeepingRoomInfo_HousekeepingRooms_Room_RoomNumber",
	// XMLType.RESPONSE));
	// }
	// // Prerequisite:If there are no rooms that are not reserved then
	// // rooms are created
	// else {
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	//
	// String createRoomReq = WSClient.createSOAPMessage("CreateRoom",
	// "RoomMaint");
	// String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
	//
	// if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success",
	// true)) {
	// String roomNumber = WSClient.getElementValue(createRoomReq,
	// "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
	// WSClient.setData("{var_RoomNo}", roomNumber);
	//
	// } else {
	// prerequisite_block_flag = true;
	// WSClient.writeToReport(LogStatus.WARNING,
	// "************Blocked : Unable to create a room****************");
	// }
	//
	// }
	// // Updating the Room status of the Created/fetched room to
	// // Pickup
	// if (prerequisite_block_flag == false) {
	//
	// WSClient.writeToReport(LogStatus.INFO, "<b>****Disabling the parameter :
	// PICKUP****</b>");
	// WSClient.setData("{var_parameter}", "PICKUP_STATUS");
	// WSClient.setData("{var_settingValue}", "N");
	// queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
	// String UpdateRoomStatusReq =
	// WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Pickup");
	// System.out.println(UpdateRoomStatusReq);
	// String UpdateRoomStatusResponseXML =
	// WSClient.processSOAPMessage(UpdateRoomStatusReq);
	// if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
	// "UpdateRoomStatusResponse_Result_resultStatusFlag", false)) {
	// String query = WSClient.getQuery("QS_01");
	// HashMap<String, String> roomRecord = WSClient.getDBRow(query);
	// System.out.println(roomRecord);
	// String status = WSClient.getElementValue(UpdateRoomStatusReq,
	// "UpdateRoomStatusRequest_RoomStatus", XMLType.REQUEST);
	//
	// // Validating the room status from the request with the
	// // Database
	// // value
	// boolean result = WSAssert.assertEquals(status,
	// roomRecord.get("ROOM_STATUS"), true);
	// String reqXpath =
	// WSClient.getRequestXPath("UpdateRoomStatusRequest_RoomStatus");
	//
	// if (result == true) {
	// WSClient.writeToReport(LogStatus.PASS,
	// reqXpath + " is the Xpath where the value is equal to the database value
	// that is "
	// + roomRecord.get("ROOM_STATUS"));
	// } else {
	// WSClient.writeToReport(LogStatus.FAIL,
	// reqXpath + "is the Xpath where the value is not equal to the database
	// value that is "
	// + roomRecord.get("ROOM_STATUS"));
	//
	// }
	//
	// }
	// }
	// }
	// WSClient.writeToReport(LogStatus.INFO, "<b>****Disabling the parameter :
	// PICKUP_STATUS****</b>");
	// WSClient.setData("{var_parameter}", "PICKUP_STATUS");
	// WSClient.setData("{var_settingValue}", "N");
	// queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	//
	// }
	// }

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Updating the Room Status to Dirty and validating if the room status
	 * obtained from the database is Dirty
	 *****/
	public void updateRoomStatus_1321() {
		String room_num = "";
		String roomNumber2 = "";
		try {
			String testName = "updateRoomStatus_1321";
			WSClient.startTest(testName,
					"Verify that component room status is updated to Dirty, when master room status updated to Dirty",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "Rooms" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				// WSClient.setData("{var_RoomType}",
				// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				// room_num="5021";
				// roomNumber2="1000";
				room_num = OperaPropConfig.getDataSetForCode("Rooms", "DS_42");
				roomNumber2 = OperaPropConfig.getDataSetForCode("Rooms", "DS_43");
				// Prerequisite:If the rooms are already dirty make it inspected
				WSClient.setData("{var_roomNumber}", room_num);
				if (SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01")) {
					WSClient.setData("{var_roomNumber}", roomNumber2);
					if (SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01")) {
						WSClient.setData("{var_RoomNo}", room_num);

						WSClient.writeToReport(LogStatus.INFO, "<b>Master room number :" + room_num + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Component room number :" + roomNumber2 + "</b>");

						// Updating the Room status of the Created/fetched room
						// to Dirty
						String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Dirty");
						String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Checking the room status of the Master Room </b>");
							String query = WSClient.getQuery("QS_01");
							HashMap<String, String> roomRecord = WSClient.getDBRow(query);
							String status = WSClient.getElementValue(UpdateRoomStatusReq,
									"UpdateRoomStatusRequest_RoomStatus", XMLType.REQUEST);

							// Validating the room status from the request with
							// the
							// Database
							// value
							boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);
							String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_RoomStatus");

							if (result == true) {
								WSClient.writeToReport(LogStatus.PASS, "Master Room's status is updated to DIRTY");
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Master Room's status is not updated to DIRTY");

							}

							WSClient.setData("{var_RoomNo}", roomNumber2);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Checking the room status of the Component Room </b>");

							String query2 = WSClient.getQuery("QS_01");
							HashMap<String, String> roomRecord1 = WSClient.getDBRow(query2);
							String status1 = WSClient.getElementValue(UpdateRoomStatusReq,
									"UpdateRoomStatusRequest_RoomStatus", XMLType.REQUEST);
							boolean result1 = WSAssert.assertEquals(status1, roomRecord1.get("ROOM_STATUS"), true);
							if (result1 == true) {
								WSClient.writeToReport(LogStatus.PASS,
										"Connected room's status is updated as Dirty due to the updation of the master room's status");
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Connected room's status is not updated as Dirty despite the updation of the master room's status");

							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

			try {
				WSClient.setData("{var_roomNumber}", room_num);
				SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
				WSClient.setData("{var_roomNumber}", roomNumber2);
				SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "fullRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are Dirty, not reserved and if there is no room
	 * found, then creating a room
	 *****/
	/*****
	 * Updating the Room Status to Clean and validating if the room status
	 * obtained from the database is Clean
	 *****/
	public void updateRoomStatus_1324a() {
		try {
			String testName = "updateRoomStatus_13246";
			WSClient.startTest(testName,
					"Verify that room status is not updated when an invalid resort is passed in the request",
					"fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Resort passed is : INVALIDRESORT</b>");
				WSClient.setData("{var_resort}", "INVALIDRESORT");
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", "INVALIDRESORT");
				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");
				if (room_num.equals("error")) {
					room_num = CreateRoom.createRoom("RoomMaint");
				}
				if (!room_num.equals("error")) {
					WSClient.setData("{var_RoomNo}", room_num);
					if (SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_04")) {
						WSClient.writeToReport(LogStatus.INFO, "<b> Room Status is updated to DIRTY </b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						WSClient.setData("{var_resort}", "INVALIDRESORT");

						String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Clean");
						String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL", false)) {
							WSClient.setData("{var_resort}", OPERALib.getResort());

							String query = WSClient.getQuery("QS_01");
							HashMap<String, String> roomRecord = WSClient.getDBRow(query);
							String status = WSClient.getElementValue(UpdateRoomStatusReq,
									"UpdateRoomStatusRequest_RoomStatus", XMLType.REQUEST);

							// Validating the room status from the request with
							// the
							// Database
							// value
							boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);
							String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_RoomStatus");

							if (result == true) {
								WSClient.writeToReport(LogStatus.FAIL,
										reqXpath + " is the Xpath where the value is equal to the database value that is "
												+ roomRecord.get("ROOM_STATUS"));
							} else {
								WSClient.writeToReport(LogStatus.PASS,
										reqXpath + " is the Xpath where the value is not equal to the database value that is "
												+ roomRecord.get("ROOM_STATUS"));

							}

						}
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
								true)) {
							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The text displayed in the response is    :     " + message + "</b>");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	@Test(groups = { "fullRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are Dirty, not reserved and if there is no room
	 * found, then creating a room
	 *****/
	/*****
	 * Updating the Room Status to Clean and validating if the room status
	 * obtained from the database is Clean
	 *****/
	public void updateRoomStatus_1324b() {
		try {
			String testName = "updateRoomStatus_13247";
			WSClient.startTest(testName,
					"Verify that room status is not updated when an invalid room number is passed in the request",
					"fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Resort passed is : INVALIDRESORT</b>");
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				// String room_num=FetchHotelRooms.fetchHotelRooms("DS_15");
				String room_num = "INVALIDROOMNUM";

				if (room_num.equals("error")) {
					room_num = CreateRoom.createRoom("RoomMaint");
				}
				if (!room_num.equals("error")) {
					WSClient.setData("{var_RoomNo}", room_num);
					if (SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_04")) {
						WSClient.writeToReport(LogStatus.INFO, "<b> Room Status is updated to DIRTY </b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Clean");
						String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL", false)) {

						}
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
								true)) {
							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The text displayed in the response is    :     " + message + "</b>");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	@Test(groups = { "fullRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are Dirty, not reserved and if there is no room
	 * found, then creating a room
	 *****/
	/*****
	 * Updating the Room Status to Clean and validating if the room status
	 * obtained from the database is Clean
	 *****/
	public void updateRoomStatus_1325a() {
		try {
			String testName = "updateRoomStatus_13258";
			WSClient.startTest(testName,
					"Verify that an error message is populated in response when no room number is passed in the request",
					"fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				// WSClient.writeToReport(LogStatus.INFO, "<b>Resort passed is :
				// INVALIDRESORT</b>");
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				// String room_num=FetchHotelRooms.fetchHotelRooms("DS_15");
				// String room_num="INVALIDROOMNUM";

				// if(room_num.equals("error")){
				// room_num=CreateRoom.createRoom("RoomMaint");
				// }
				// if(!room_num.equals("error")){
				// WSClient.setData("{var_RoomNo}", room_num);
				// if(SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_04")){
				WSClient.writeToReport(LogStatus.INFO, "<b> Room Status is updated to DIRTY </b>");
				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());
				String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "CleanTR2");
				String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
				if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
						"UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL", false)) {

				}
				if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
					String message = WSClient.getElementValue(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is    :     " + message + "</b>");
				}
			}
			// }
			// }
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	@Test(groups = { "fullRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are Dirty, not reserved and if there is no room
	 * found, then creating a room
	 *****/
	/*****
	 * Updating the Room Status to Clean and validating if the room status
	 * obtained from the database is Clean
	 *****/
	public void updateRoomStatus_1325b() {
		try {
			String testName = "updateRoomStatus_13259";
			WSClient.startTest(testName,
					"Verify that an error message is populated in response when no resort is passed in the request",
					"fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				// WSClient.writeToReport(LogStatus.INFO, "<b>Resort passed is :
				// INVALIDRESORT</b>");
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");

				if (room_num.equals("error")) {
					room_num = CreateRoom.createRoom("RoomMaint");
				}
				if (!room_num.equals("error")) {
					WSClient.setData("{var_RoomNo}", room_num);
					if (SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_04")) {
						WSClient.writeToReport(LogStatus.INFO, "<b> Room Status is updated to DIRTY </b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "CleanTR3");
						String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL", false)) {

						}
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
								true)) {
							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The text displayed in the response is    :     " + message + "</b>");
						}
					}
					// }
					// }
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	@Test(groups = { "fullRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are occupied and if there is no room found, then
	 * creating a room
	 *****/
	/*****
	 * Updating the Service Status to Make Up and validating if the Service
	 * status obtained from the database is Make up
	 *****/
	public void updateRoomStatus_1350() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateRoomStatus_1350";
			WSClient.startTest(testName,
					"Verify that an error message is populated in the response when updating Service Status to MakeUp Room, without passing mandatory fields on the request",
					"fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				// Prerequisite:If there are no rooms that are occupied then
				// rooms are created

				prerequisite_block_flag = changeToOccupied();

				// Updating the Service status of the Created/fetched room to
				// Make Up
				if (prerequisite_block_flag == true) {
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());

					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "MUFT");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL", false)) {

					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CheckoutReservation.checkOutReservation("DS_01");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "fullRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	/*****
	 * Fetching the rooms that are occupied and if there is no room found, then
	 * creating a room
	 *****/
	/*****
	 * Updating the Service Status to Clean and validating if an error has
	 * occured
	 *****/
	public void updateRoomStatus_1351() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateRoomStatus_1351";
			WSClient.startTest(testName,
					"Verify that an error message is populated in the response when updating Service Status to MakeUp Room, with passing an invalid status on the request",
					"fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				// Prerequisite:If there are no rooms that are occupied then
				// rooms are created

				prerequisite_block_flag = changeToOccupied();

				// Updating the Service status of the Created/fetched room to
				// Make Up
				if (prerequisite_block_flag == true) {
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());

					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "MUFT1");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL", false)) {

					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CheckoutReservation.checkOutReservation("DS_01");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "fullRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })

	public void updateRoomStatus_1324c() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateRoomStatus_13243";
			WSClient.startTest(testName,
					"Verify that room status is not updated when an invalid room status is passed in the request",
					"fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");
				if (room_num.equals("error")) {
					room_num = CreateRoom.createRoom("RoomMaint");
				}
				if (!room_num.equals("error")) {
					WSClient.setData("{var_RoomNo}", room_num);
					if (SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_04")) {
						WSClient.writeToReport(LogStatus.INFO, "<b> Room Status is updated to TEST </b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());

						String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "RSTR");
						String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL", false)) {

						}
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
								true)) {
							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The text displayed in the response is    :     " + message + "</b>");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals("")) {
					CheckoutReservation.checkOutReservation("DS_01");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "targetedRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG", "updateRoomStatus_1356" })
	/*****
	 * Fetching the rooms that have reservation status as Arrived and if there
	 * is no room found, then creating a room
	 *****/
	/*****
	 * Updating the TurnDown Status to Required and validating if the TurnDown
	 * status obtained from the database is required
	 *****/
	public void updateRoomStatus_1356() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateRoomStatus_1356";
			WSClient.startTest(testName, "Verify that TurnDown Status is updated to Required for a Shared Reservation",
					"targetedRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				// Prerequisite :Fetching rooms that have reservation status as
				// arrived
				OPERALib.setOperaHeader(OPERALib.getUserName());
				// WSClient.setData("{var_RoomType}", "OWSRT");
				// WSClient.setData("{var_roomType}", "OWSRT");

				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				// Prerequisite:If there are no rooms that are in arrived status
				// then
				// rooms are created

				prerequisite_block_flag = shareReservation();
				String roomNumber = FetchHotelRooms.fetchHotelRooms("DS_03");
				// Prerequisite 4: Creating a room to assign
				if (roomNumber.equals("error")) {
					roomNumber = CreateRoom.createRoom("RoomMaint");
				}

				WSClient.setData("{var_roomNumber}", roomNumber);

				// Prerequisite 5: Changing the room status to inspected to
				// assign the room for checking in

				if (SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01")) {

					// Prerequisite 6: Assign Room

					if (AssignRoom.assignRoom("DS_01")) {

						// Prerequisite 7: CheckIn Reservation

						if (CheckinReservation.checkinReservation("DS_01")) {
							WSClient.setData("{var_RoomNo}", roomNumber);
						} else {
							prerequisite_block_flag = false;
						}
					} else {
						prerequisite_block_flag = false;
					}
				} else {
					prerequisite_block_flag = false;
				}

				// Updating the TurnDown status of the Created/fetched room to
				// Required
				if (prerequisite_block_flag == true) {
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "TDR");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						String query = WSClient.getQuery("QS_04");
						HashMap<String, String> roomRecord = WSClient.getDBRow(query);
						String status = WSClient.getElementValue(UpdateRoomStatusReq,
								"UpdateRoomStatusRequest_TurnDownStatus", XMLType.REQUEST);

						// Validating the room status from the request with the
						// Database
						// value
						boolean result = WSAssert.assertEquals(status, roomRecord.get("TURNDOWN_STATUS"), true);
						String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_TurnDownStatus");

						if (result == true) {
							WSClient.writeToReport(LogStatus.PASS,
									reqXpath + " is the Xpath where the value is equal to the database value that is "
											+ roomRecord.get("TURNDOWN_STATUS"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									reqXpath + "is the Xpath where the value is not equal to the database value that is "
											+ roomRecord.get("TURNDOWN_STATUS"));

						}

					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			// } finally {
			// try {
			// if(!WSClient.getData("{var_resvId}").equals(""))
			// {
			// CheckoutReservation.checkOutReservation("DS_01");
			// }
			//
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
		}
	}

	// @Test(groups = { "fullRegression", "UpdateRoomStatus",
	// "HTNG2008BExt","HTNG" })
	// /*****
	// * Fetching the rooms that have reservation status as Arrived and if there
	// * is no room found, then creating a room
	// *****/
	// /*****
	// * Updating the TurnDown Status to Required and validating if the TurnDown
	// * status obtained from the database is required
	// *****/
	// public void updateRoomStatus_1360() {
	// try {
	// boolean prerequisite_block_flag = false;
	// String testName = "updateRoomStatus_1360";
	// WSClient.startTest(testName, "Update HK status, Room Status, Guest
	// Service Status, TurnDown Status", "fullRegression");
	// if (OperaPropConfig.getPropertyConfigResults(
	// new String[] {"RoomType"})) {
	// WSClient.setData("{var_resort}", OPERALib.getResort());
	//
	// // Prerequisite :Fetching rooms that have reservation status as
	// // arrived
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_roomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	//
	//
	// // Prerequisite:If there are no rooms that are in arrived status
	// // then
	// // rooms are created
	// prerequisite_block_flag=changeToOccupied();
	//
	// // Updating the TurnDown status of the Created/fetched room to
	// // Required
	// if (prerequisite_block_flag == true) {
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// HTNGLib.getInterfaceFromAddress());
	// String UpdateRoomStatusReq =
	// WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "All");
	// String UpdateRoomStatusResponseXML =
	// WSClient.processSOAPMessage(UpdateRoomStatusReq);
	// if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
	// "UpdateRoomStatusResponse_Result_resultStatusFlag","SUCCESS", false)) {
	// String query = WSClient.getQuery("QS_04");
	// HashMap<String, String> roomRecord = WSClient.getDBRow(query);
	// String status = WSClient.getElementValue(UpdateRoomStatusReq,
	// "UpdateRoomStatusRequest_TurnDownStatus", XMLType.REQUEST);
	//
	// // Validating the room status from the request with the
	// // Database
	// // value
	// boolean result = WSAssert.assertEquals(status,
	// roomRecord.get("TURNDOWN_STATUS"), true);
	// String reqXpath =
	// WSClient.getRequestXPath("UpdateRoomStatusRequest_TurnDownStatus");
	//
	// if (result == true) {
	// WSClient.writeToReport(LogStatus.PASS,
	// reqXpath + " is the Xpath where the value is equal to the database value
	// that is "
	// + roomRecord.get("TURNDOWN_STATUS"));
	// } else {
	// WSClient.writeToReport(LogStatus.FAIL,
	// reqXpath + "is the Xpath where the value is not equal to the database
	// value that is "
	// + roomRecord.get("TURNDOWN_STATUS"));
	//
	// }
	//
	// }
	// if(WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,"Result_Text_TextElement",
	// true)){
	// String
	// message=WSClient.getElementValue(UpdateRoomStatusResponseXML,"Result_Text_TextElement",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the
	// response is : " + message+"</b>");
	// }
	// }
	// }
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// try {
	// if(!WSClient.getData("{var_resvId}").equals(""))
	// {
	// CheckoutReservation.checkOutReservation("DS_01");
	// }
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// }
	//
	//

	// @Test(groups = { "fullRegression", "UpdateRoomStatus",
	// "HTNG2008BExt","HTNG" })
	// /*****
	// * Fetching the rooms that are Dirty, not reserved and if there is no room
	// * found, then creating a room
	// *****/
	// /*****
	// * Updating the Room Status to Clean and validating if the room status
	// * obtained from the database is Clean
	// *****/
	// public void updateRoomStatus_1323() {
	// try {
	// String testName = "updateRoomStatus_1323";
	// WSClient.startTest(testName, "Update Room Status of an OOS room to
	// Clean", "fullRegression");
	// if (OperaPropConfig.getPropertyConfigResults(
	// new String[] {"RoomType"})) {
	// WSClient.setData("{var_resort}", OPERALib.getResort());
	// // Prerequisite :Fetching rooms that are not reserved
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_roomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// //String room_num="'A65331";
	// String room_num=FetchHotelRooms.fetchHotelRooms("DS_15");
	//
	// String
	// reasonCode=OperaPropConfig.getDataSetForCode("OOOSReason","DS_01");
	// WSClient.setData("{var_ReasonCode}",reasonCode);
	// if(room_num.equals("error")){
	// room_num=CreateRoom.createRoom("RoomMaint");
	// }
	// if(!room_num.equals("error")){
	// WSClient.setData("{var_OSRoom}",room_num);
	// WSClient.setData("{var_Resort}",OPERALib.getResort());
	// WSClient.setData("{var_RoomNo}",room_num);
	// WSClient.writeToReport(LogStatus.INFO, "<b> Room Status is updated to Out
	// OF Service </b>" );
	//
	// String req_createOORoom =
	// WSClient.createSOAPMessage("SetRoomOutOfService", "DS_01");
	// String res_createOORoom = WSClient.processSOAPMessage(req_createOORoom);
	// String query = WSClient.getQuery("HTNGExtUpdateRoomStatus","QS_01");
	// HashMap<String, String> roomRecord = WSClient.getDBRow(query);
	// if(WSAssert.assertEquals("OutOfService", roomRecord.get("ROOM_STATUS"),
	// false)){
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// HTNGLib.getInterfaceFromAddress());
	// WSClient.writeToReport(LogStatus.INFO, "<b> Out Of Order room is updated
	// to CLEAN </b>" );
	//
	// String UpdateRoomStatusReq =
	// WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Clean");
	// String UpdateRoomStatusResponseXML =
	// WSClient.processSOAPMessage(UpdateRoomStatusReq);
	// if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
	// "UpdateRoomStatusResponse_Result_resultStatusFlag","FAIL", false)) {
	// if(WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,"Result_Text_TextElement",
	// true)){
	// String
	// message=WSClient.getElementValue(UpdateRoomStatusResponseXML,"Result_Text_TextElement",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the
	// response is : " + message+"</b>");
	// }
	// }
	// query = WSClient.getQuery("QS_01");
	// roomRecord = WSClient.getDBRow(query);
	// String status = WSClient.getElementValue(UpdateRoomStatusReq,
	// "UpdateRoomStatusRequest_RoomStatus",
	// XMLType.REQUEST);
	//
	// // Validating the room status from the request with the
	// // Database
	// // value
	// boolean result = WSAssert.assertEquals(status,
	// roomRecord.get("ROOM_STATUS"), true);
	// String reqXpath =
	// WSClient.getRequestXPath("UpdateRoomStatusRequest_RoomStatus");
	//
	// if (result == true) {
	// WSClient.writeToReport(LogStatus.FAIL,
	// "Room Status of an Out of Service room is updated to CLEAN in the
	// Database");
	// } else {
	// WSClient.writeToReport(LogStatus.PASS,
	// "Room Status of an Out of Service room is not updated to CLEAN in the
	// Database");
	//
	// }
	//
	// }
	//
	// }
	// }
	//
	// }catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	//
	// }
	// }
	//
	//
	// @Test(groups = { "fullRegression", "UpdateRoomStatus",
	// "HTNG2008BExt","HTNG" })
	// /*****
	// * Fetching the rooms that are Dirty, not reserved and if there is no room
	// * found, then creating a room
	// *****/
	// /*****
	// * Updating the Room Status to Clean and validating if the room status
	// * obtained from the database is Clean
	// *****/
	// public void updateRoomStatus_1329() {
	// try {
	// String testName = "updateRoomStatus_1329";
	// WSClient.startTest(testName, "Update HK Status of an OOS room to
	// Occupied", "fullRegression");
	// if (OperaPropConfig.getPropertyConfigResults(
	// new String[] {"RoomType"})) {
	// WSClient.setData("{var_resort}", OPERALib.getResort());
	// // Prerequisite :Fetching rooms that are not reserved
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_roomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// //String room_num="'A65331";
	// String room_num=FetchHotelRooms.fetchHotelRooms("DS_15");
	//
	// String
	// reasonCode=OperaPropConfig.getDataSetForCode("OOOSReason","DS_01");
	// WSClient.setData("{var_ReasonCode}",reasonCode);
	// if(room_num.equals("error")){
	// room_num=CreateRoom.createRoom("RoomMaint");
	// }
	// if(!room_num.equals("error")){
	// WSClient.setData("{var_OSRoom}",room_num);
	// WSClient.setData("{var_Resort}",OPERALib.getResort());
	// WSClient.setData("{var_RoomNo}",room_num);
	// WSClient.writeToReport(LogStatus.INFO, "<b> Room Status is updated to Out
	// OF Service </b>" );
	//
	// String req_createOORoom =
	// WSClient.createSOAPMessage("SetRoomOutOfService", "DS_01");
	// String res_createOORoom = WSClient.processSOAPMessage(req_createOORoom);
	// String query = WSClient.getQuery("HTNGExtUpdateRoomStatus","QS_01");
	// HashMap<String, String> roomRecord = WSClient.getDBRow(query);
	// if(WSAssert.assertEquals("OutOfService", roomRecord.get("ROOM_STATUS"),
	// false)){
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// HTNGLib.getInterfaceFromAddress());
	// WSClient.writeToReport(LogStatus.INFO, "<b> Out Of Service room is
	// updated to OCCUPIED </b>" );
	//
	// String UpdateRoomStatusReq =
	// WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Occupied");
	// String UpdateRoomStatusResponseXML =
	// WSClient.processSOAPMessage(UpdateRoomStatusReq);
	// if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
	// "UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL",false)) {
	// if(WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,"Result_Text_TextElement",
	// true)){
	// String
	// message=WSClient.getElementValue(UpdateRoomStatusResponseXML,"Result_Text_TextElement",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the
	// response is : " + message+"</b>");
	// }
	// }
	// query = WSClient.getQuery("QS_02");
	// roomRecord = WSClient.getDBRow(query);
	// String status = WSClient.getElementValue(UpdateRoomStatusReq,
	// "UpdateRoomStatusRequest_HKStatus", XMLType.REQUEST);
	//
	// // Validating the room status from the request with the
	// // Database
	// // value
	// boolean result = WSAssert.assertEquals(status,
	// roomRecord.get("HK_STATUS"), true);
	// String reqXpath =
	// WSClient.getRequestXPath("UpdateRoomStatusRequest_HKStatus");
	//
	// if (result == true) {
	// WSClient.writeToReport(LogStatus.FAIL,
	// "Housekeeping status is updated to OCCUPIED despite the room being OUT OF
	// SERVICE");
	// } else {
	// WSClient.writeToReport(LogStatus.PASS,
	// "Housekeeping status is not updated to OCCUPIED as the room is OUT OF
	// SERVICE");
	//
	// }
	//
	// }
	//
	// }
	// }
	//
	// }catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	//
	// }
	// }

	// @Test(groups = { "fullRegression", "UpdateRoomStatus",
	// "HTNG2008BExt","HTNG" })
	// /*****
	// * Fetching the rooms that are Dirty, not reserved and if there is no room
	// * found, then creating a room
	// *****/
	// /*****
	// * Updating the Room Status to Clean and validating if the room status
	// * obtained from the database is Clean
	// *****/
	// public void updateRoomStatus_1328() {
	// try {
	// String testName = "updateRoomStatus_1328";
	// WSClient.startTest(testName, "Update HK of an OOO room to OCCUPIED",
	// "fullRegression");
	// if (OperaPropConfig.getPropertyConfigResults(
	// new String[] {"RoomType"})) {
	// WSClient.setData("{var_resort}", OPERALib.getResort());
	// // Prerequisite :Fetching rooms that are not reserved
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_roomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// //String room_num="'A65331";
	// String room_num=FetchHotelRooms.fetchHotelRooms("DS_15");
	//
	// String
	// reasonCode=OperaPropConfig.getDataSetForCode("OOOSReason","DS_01");
	// WSClient.setData("{var_ReasonCode}",reasonCode);
	// if(room_num.equals("error")){
	// room_num=CreateRoom.createRoom("RoomMaint");
	// }
	// if(!room_num.equals("error")){
	// WSClient.setData("{var_OORoom}",room_num);
	// WSClient.setData("{var_Resort}",OPERALib.getResort());
	// WSClient.setData("{var_RoomNo}",room_num);
	// WSClient.writeToReport(LogStatus.INFO, "<b> Room Status is updated to Out
	// OF Order </b>" );
	//
	// String req_createOORoom = WSClient.createSOAPMessage("SetRoomOutOfOrder",
	// "DS_01");
	// String res_createOORoom = WSClient.processSOAPMessage(req_createOORoom);
	// String query = WSClient.getQuery("HTNGExtUpdateRoomStatus","QS_01");
	// HashMap<String, String> roomRecord = WSClient.getDBRow(query);
	// if(WSAssert.assertEquals("OutOfOrder", roomRecord.get("ROOM_STATUS"),
	// false)){
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// HTNGLib.getInterfaceFromAddress());
	// WSClient.writeToReport(LogStatus.INFO, "<b> Out Of Service room is
	// updated to OCCUPIED </b>" );
	//
	// String UpdateRoomStatusReq =
	// WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Occupied");
	// String UpdateRoomStatusResponseXML =
	// WSClient.processSOAPMessage(UpdateRoomStatusReq);
	// if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
	// "UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL",false)) {
	// if(WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,"Result_Text_TextElement",
	// true)){
	// String
	// message=WSClient.getElementValue(UpdateRoomStatusResponseXML,"Result_Text_TextElement",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the
	// response is : " + message+"</b>");
	// }}
	// query = WSClient.getQuery("QS_02");
	// roomRecord = WSClient.getDBRow(query);
	// String status = WSClient.getElementValue(UpdateRoomStatusReq,
	// "UpdateRoomStatusRequest_HKStatus", XMLType.REQUEST);
	//
	// // Validating the room status from the request with the
	// // Database
	// // value
	// boolean result = WSAssert.assertEquals(status,
	// roomRecord.get("HK_STATUS"), true);
	// String reqXpath =
	// WSClient.getRequestXPath("UpdateRoomStatusRequest_HKStatus");
	//
	// if (result == true) {
	// WSClient.writeToReport(LogStatus.FAIL,
	// "Housekeeping status is updated to OCCUPIED despite the room being OUT OF
	// ORDER");
	// } else {
	// WSClient.writeToReport(LogStatus.PASS,
	// "Housekeeping status is not updated to OCCUPIED as the room is OUT OF
	// ORDER");
	//
	// }
	//
	// }
	//
	// }
	// }
	//
	//
	// }catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	//
	// }
	// }

	// @Test(groups = { "fullRegression", "UpdateRoomStatus",
	// "HTNG2008BExt","HTNG" })
	// /*****
	// * Fetching the rooms that are Dirty, not reserved and if there is no room
	// * found, then creating a room
	// *****/
	// /*****
	// * Updating the Room Status to Clean and validating if the room status
	// * obtained from the database is Clean
	// *****/
	// public void updateRoomStatus_1322() {
	// try {
	// String testName = "updateRoomStatus_1322";
	// WSClient.startTest(testName, "Update Room Status of an OOO room to
	// Clean", "fullRegression");
	// if (OperaPropConfig.getPropertyConfigResults(
	// new String[] {"RoomType"})) {
	// WSClient.setData("{var_resort}", OPERALib.getResort());
	// // Prerequisite :Fetching rooms that are not reserved
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_roomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// //String room_num="'A65331";
	// String room_num=FetchHotelRooms.fetchHotelRooms("DS_15");
	//
	// String
	// reasonCode=OperaPropConfig.getDataSetForCode("OOOSReason","DS_01");
	// WSClient.setData("{var_ReasonCode}",reasonCode);
	// if(room_num.equals("error")){
	// room_num=CreateRoom.createRoom("RoomMaint");
	// }
	// if(!room_num.equals("error")){
	// WSClient.setData("{var_OORoom}",room_num);
	// WSClient.setData("{var_Resort}",OPERALib.getResort());
	// WSClient.setData("{var_RoomNo}",room_num);
	// WSClient.writeToReport(LogStatus.INFO, "<b> Room Status is updated to Out
	// OF Order </b>" );
	//
	// String req_createOORoom = WSClient.createSOAPMessage("SetRoomOutOfOrder",
	// "DS_01");
	// String res_createOORoom = WSClient.processSOAPMessage(req_createOORoom);
	// String query = WSClient.getQuery("HTNGExtUpdateRoomStatus","QS_01");
	// HashMap<String, String> roomRecord = WSClient.getDBRow(query);
	// if(WSAssert.assertEquals("OutOfOrder", roomRecord.get("ROOM_STATUS"),
	// false)){
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// HTNGLib.getInterfaceFromAddress());
	// WSClient.writeToReport(LogStatus.INFO, "<b> Out Of Order room is updated
	// to CLEAN </b>" );
	//
	// String UpdateRoomStatusReq =
	// WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Clean");
	// String UpdateRoomStatusResponseXML =
	// WSClient.processSOAPMessage(UpdateRoomStatusReq);
	// if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
	// "UpdateRoomStatusResponse_Result_resultStatusFlag","FAIL", false)) {
	// if(WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,"Result_Text_TextElement",
	// true)){
	// String
	// message=WSClient.getElementValue(UpdateRoomStatusResponseXML,"Result_Text_TextElement",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the
	// response is : " + message+"</b>");
	// }
	// }
	//
	// query = WSClient.getQuery("QS_01");
	// roomRecord = WSClient.getDBRow(query);
	// String status = WSClient.getElementValue(UpdateRoomStatusReq,
	// "UpdateRoomStatusRequest_RoomStatus",
	// XMLType.REQUEST);
	//
	// // Validating the room status from the request with the
	// // Database
	// // value
	// boolean result = WSAssert.assertEquals(status,
	// roomRecord.get("ROOM_STATUS"), true);
	// String reqXpath =
	// WSClient.getRequestXPath("UpdateRoomStatusRequest_RoomStatus");
	//
	// if (result == true) {
	// WSClient.writeToReport(LogStatus.FAIL,
	// "Room Status of an Out of Order room is updated to CLEAN in the
	// Database");
	// } else {
	// WSClient.writeToReport(LogStatus.PASS,
	// "Room Status of an Out of Order room is not updated to CLEAN in the
	// Database");
	//
	// }
	//
	// }
	//
	// }
	// }
	//
	// }catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	//
	// }
	// }
	//
	// @Test(groups = { "targetedRegression", "UpdateRoomStatus",
	// "HTNG2008BExt","HTNG" })
	// public void updateRoomStatus_1348() {
	// try {
	// boolean prerequisite_block_flag = false;
	// String testName = "updateRoomStatus_1348";
	// WSClient.startTest(testName, "Verify that an error message is displayed
	// on the response when Guest Service update is initiated via
	// UpdateRoomStatus call for a room which is vacant", "targetedRegression");
	// if (OperaPropConfig.getPropertyConfigResults(
	// new String[] {"RoomType"})) {
	// WSClient.setData("{var_resort}", OPERALib.getResort());
	//
	// // Prerequisite :Fetching rooms that are not reserved
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_roomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	//
	// // Prerequisite:If there are no rooms that are occupied then
	// // rooms are created
	//
	// prerequisite_block_flag=fetchVacant();
	//
	// // Updating the Service status of the Created/fetched room to
	// // Make Up
	// if (prerequisite_block_flag == true) {
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// HTNGLib.getInterfaceFromAddress());
	//
	// String UpdateRoomStatusReq =
	// WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "MU");
	// String UpdateRoomStatusResponseXML =
	// WSClient.processSOAPMessage(UpdateRoomStatusReq);
	// if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
	// "UpdateRoomStatusResponse_Result_resultStatusFlag","FAIL" ,false)) ;
	// if(WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,"Result_Text_TextElement",
	// true)){
	// String
	// message=WSClient.getElementValue(UpdateRoomStatusResponseXML,"Result_Text_TextElement",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the
	// response is : " + message+"</b>");
	// }
	// }
	// }
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// try {
	// if(!WSClient.getData("{var_resvId}").equals(""))
	// {
	// CheckoutReservation.checkOutReservation("DS_01");
	// }
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// }

	@Test(groups = { "fullRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	public void updateRoomStatus_1337() {
		try {
			String testName = "updateRoomStatus_1337";
			WSClient.startTest(testName,
					"Verify that an error message is displayed on the response message when UpdateRoomStatus call is issued with missing mandatory data for updating the repair status",
					"fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");
				if (room_num.equals("error")) {
					room_num = CreateRoom.createRoom("RoomMaint");
				}
				if (!room_num.equals("error")) {
					WSClient.setData("{var_RoomNo}", "");
					WSClient.setData("{var_roomNumber}", room_num);
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());

					// Updating the Room status of the Created/fetched room to
					// Out
					// Of Service
					String roomStatusReason = OperaPropConfig.getDataSetForCode("OOOSReason", "DS_01");
					WSClient.setData("{var_roomStatusReason}", roomStatusReason);
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "OS");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL", false))
						;

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_roomNumber}").equals("")) {
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "targetedRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })
	public void updateRoomStatus_1333() {
		try {
			String testName = "updateRoomStatus_1333";
			WSClient.startTest(testName,
					"Verify that both the Room Status and Housekeeping Status are at a time being updated correctly for the requested room via UpdateRoomStatus call",
					"targetedRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				// Prerequisite :Fetching rooms that are vacant
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");
				if (room_num.equals("error")) {
					room_num = CreateRoom.createRoom("RoomMaint");
				}
				if (!room_num.equals("error")) {

					WSClient.setData("{var_RoomNo}", room_num);
					WSClient.setData("{var_roomNumber}", room_num);
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_06");

					// Updating the Front Office status of the Created/fetched
					// room
					// to Occupied
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
							HTNGLib.getInterfaceFromAddress());
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Occupied");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
					if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						String query = WSClient.getQuery("QS_02");
						HashMap<String, String> roomRecord = WSClient.getDBRow(query);
						String status = WSClient.getElementValue(UpdateRoomStatusReq,
								"UpdateRoomStatusRequest_HKStatus", XMLType.REQUEST);

						// Validating the room status from the request with the
						// Database
						// value
						boolean result = WSAssert.assertEquals(status, roomRecord.get("HK_STATUS"), true);
						String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_HKStatus");

						if (result == true) {
							WSClient.writeToReport(LogStatus.PASS,
									reqXpath + " is the Xpath where the value is equal to the database value that is "
											+ roomRecord.get("HK_STATUS"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									reqXpath + "is the Xpath where the value is not equal to the database value that is "
											+ roomRecord.get("HK_STATUS"));
						}

						query = WSClient.getQuery("QS_01");
						roomRecord = WSClient.getDBRow(query);
						status = WSClient.getElementValue(UpdateRoomStatusReq, "UpdateRoomStatusRequest_RoomStatus",
								XMLType.REQUEST);

						// Validating the room status from the request with the
						// Database
						// value
						result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);
						reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_RoomStatus");

						if (result == true) {
							WSClient.writeToReport(LogStatus.PASS,
									reqXpath + " is the Xpath where the value is equal to the database value that is "
											+ roomRecord.get("ROOM_STATUS"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									reqXpath + "is the Xpath where the value is not equal to the database value that is "
											+ roomRecord.get("ROOM_STATUS"));

						}
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>" + "The text displayed in the response is    :     " + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_roomNumber}").equals("")) {
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "targetedRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })

	public void updateRoomStatus_1341() {
		try {
			String testName = "updateRoomStatus_1341";
			WSClient.startTest(testName, "Verify that return status of an OOS room is updated to Completed",
					"targetedRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				// String room_num="'A65331";
				String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");

				String reasonCode = OperaPropConfig.getDataSetForCode("OOOSReason", "DS_01");
				WSClient.setData("{var_ReasonCode}", reasonCode);
				WSClient.setData("{var_roomStatusReason}", reasonCode);

				if (room_num.equals("error")) {
					room_num = CreateRoom.createRoom("RoomMaint");
				}
				if (!room_num.equals("error")) {
					WSClient.setData("{var_OSRoom}", room_num);
					WSClient.setData("{var_Resort}", OPERALib.getResort());
					WSClient.setData("{var_RoomNo}", room_num);
					WSClient.writeToReport(LogStatus.INFO, "<b> Room Status is updated to Out OF Service </b>");

					String req_createOORoom = WSClient.createSOAPMessage("SetRoomOutOfService", "DS_01");
					String res_createOORoom = WSClient.processSOAPMessage(req_createOORoom);
					String query = WSClient.getQuery("HTNGExtUpdateRoomStatus", "QS_01");
					HashMap<String, String> roomRecord = WSClient.getDBRow(query);
					if (WSAssert.assertEquals("OutOfService", roomRecord.get("ROOM_STATUS"), false)) {
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						WSClient.writeToReport(LogStatus.INFO, "<b> Out Of Order room is updated to Completed </b>");

						String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus",
								"OOComplete");
						String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							query = WSClient.getQuery("QS_01");
							roomRecord = WSClient.getDBRow(query);
							String status = "CLEAN";

							// Validating the room status from the request with
							// the
							// Database
							// value
							boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);
							String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_RoomStatus");

							if (result == true) {
								WSClient.writeToReport(LogStatus.PASS,
										"Room Status of an Out of Order room is updated to CLEAN in the Database as the repair status is COMPLETED");
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Room Status of an Out of Order room is not updated to CLEAN in the Database eventhough the repair status is COMPLETED");

							}

						}
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
								true)) {
							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The text displayed in the response is    :     " + message + "</b>");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	@Test(groups = { "targetedRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })

	public void updateRoomStatus_1340() {
		try {
			String testName = "updateRoomStatus_1340";
			WSClient.startTest(testName, "Verify that return status of an OOO room is updated to Completed",
					"targetedRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				// String room_num="'A65331";
				String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");

				String reasonCode = OperaPropConfig.getDataSetForCode("OOOSReason", "DS_01");
				WSClient.setData("{var_ReasonCode}", reasonCode);
				WSClient.setData("{var_roomStatusReason}", reasonCode);

				if (room_num.equals("error")) {
					room_num = CreateRoom.createRoom("RoomMaint");
				}
				if (!room_num.equals("error")) {
					WSClient.setData("{var_OORoom}", room_num);
					WSClient.setData("{var_Resort}", OPERALib.getResort());
					WSClient.setData("{var_RoomNo}", room_num);
					WSClient.writeToReport(LogStatus.INFO, "<b> Room Status is updated to Out OF Order </b>");

					String req_createOORoom = WSClient.createSOAPMessage("SetRoomOutOfOrder", "DS_01");
					String res_createOORoom = WSClient.processSOAPMessage(req_createOORoom);
					String query = WSClient.getQuery("HTNGExtUpdateRoomStatus", "QS_01");
					HashMap<String, String> roomRecord = WSClient.getDBRow(query);
					if (WSAssert.assertEquals("OutOfOrder", roomRecord.get("ROOM_STATUS"), false)) {
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						WSClient.writeToReport(LogStatus.INFO, "<b> Out Of Order room is updated to Completed </b>");

						String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus",
								"OSComplete");
						String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							query = WSClient.getQuery("QS_01");
							roomRecord = WSClient.getDBRow(query);
							String status = "CLEAN";

							// Validating the room status from the request with
							// the
							// Database
							// value
							boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);
							String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_RoomStatus");

							if (result == true) {
								WSClient.writeToReport(LogStatus.PASS,
										"Room Status of an Out of Order room is updated to CLEAN in the Database as the repair status is COMPLETED");
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Room Status of an Out of Order room is not updated to CLEAN in the Database eventhough the repair status is COMPLETED");

							}

						}
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
								true)) {
							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The text displayed in the response is    :     " + message + "</b>");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	// @Test(groups = { "targetedRegression", "UpdateRoomStatus",
	// "HTNG2008BExt","HTNG" })
	// /*****
	// * Updating the Room Status to Dirty and validating if the room status
	// * obtained from the database is Dirty
	// *****/
	// public void updateRoomStatus_1332() {
	// String room_num="";
	// String roomNumber2="";
	// try {
	// String testName = "updateRoomStatus_1332";
	// WSClient.startTest(testName, "Update master Housekeeping Status to
	// Occupied in the component rooms", "targetedRegression");
	// if (OperaPropConfig.getPropertyConfigResults(
	// new String[] {"Rooms"})) {
	// WSClient.setData("{var_resort}", OPERALib.getResort());
	// // Prerequisite :Fetching rooms that are not reserved
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// //WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	//
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// HTNGLib.getInterfaceFromAddress());
	//
	//
	//// room_num="5021";
	//// roomNumber2="1000";
	// room_num=OperaPropConfig.getDataSetForCode("Rooms", "DS_42");
	// roomNumber2=OperaPropConfig.getDataSetForCode("Rooms", "DS_43");
	// // Prerequisite:If the rooms are already dirty make it inspected
	// WSClient.setData("{var_roomNumber}", room_num);
	// if(SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01")){
	// WSClient.setData("{var_roomNumber}", roomNumber2);
	// if(SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01")){
	// WSClient.setData("{var_RoomNo}", room_num);
	//
	// WSClient.writeToReport(LogStatus.INFO, "<b>Master room number
	// :"+room_num+"</b>");
	// WSClient.writeToReport(LogStatus.INFO, "<b>Component room number
	// :"+roomNumber2+"</b>");
	//
	// // Updating the Room status of the Created/fetched room to Dirty
	// String UpdateRoomStatusReq =
	// WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "Occupied");
	// String UpdateRoomStatusResponseXML =
	// WSClient.processSOAPMessage(UpdateRoomStatusReq);
	// if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
	// "UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS",false)) {
	//
	// String query = WSClient.getQuery("QS_02");
	// HashMap<String, String> roomRecord = WSClient.getDBRow(query);
	// String status = WSClient.getElementValue(UpdateRoomStatusReq,
	// "UpdateRoomStatusRequest_HKStatus", XMLType.REQUEST);
	//
	// // Validating the room status from the request with the
	// // Database
	// // value
	// WSClient.writeToReport(LogStatus.INFO, "<b>Checking the HK status of the
	// Master room</b>");
	//
	// boolean result = WSAssert.assertEquals(status,
	// roomRecord.get("HK_STATUS"), true);
	// String reqXpath =
	// WSClient.getRequestXPath("UpdateRoomStatusRequest_HKStatus");
	//
	// if (result == true) {
	// WSClient.writeToReport(LogStatus.PASS,
	// "Master room's Housekeeping status is updated to Occupied");
	// } else {
	// WSClient.writeToReport(LogStatus.FAIL,
	// "Master room's Housekeeping status is NOT updated to Occupied");
	//
	// }
	// WSClient.writeToReport(LogStatus.INFO, "<b>Checking the HK status of the
	// component room</b>");
	// WSClient.setData("{var_RoomNo}", roomNumber2);
	//
	// query = WSClient.getQuery("QS_02");
	// roomRecord = WSClient.getDBRow(query);
	// status = WSClient.getElementValue(UpdateRoomStatusReq,
	// "UpdateRoomStatusRequest_HKStatus", XMLType.REQUEST);
	//
	// // Validating the room status from the request with the
	// // Database
	// // value
	// result = WSAssert.assertEquals(status, roomRecord.get("HK_STATUS"),
	// true);
	//
	// if (result == true) {
	// WSClient.writeToReport(LogStatus.PASS,
	// "Component's Housekeeping status is also updated to Occupied");
	// } else {
	// WSClient.writeToReport(LogStatus.FAIL,
	// "Component's Housekeeping status is NOT updated to Occupied");
	//
	// }
	//
	// }
	// }
	// }
	// }
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	//
	// try {
	// WSClient.setData("{var_roomNumber}", room_num);
	// SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
	// WSClient.setData("{var_roomNumber}", roomNumber2);
	// SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// @Test(groups = { "targetedRegression", "UpdateRoomStatus",
	// "HTNG2008BExt","HTNG" })
	// /*****
	// * Updating the Room Status to Dirty and validating if the room status
	// * obtained from the database is Dirty
	// *****/
	// public void updateRoomStatus_1349() {
	// String room_num="";
	// String roomNumber2="";
	// boolean prerequisite_block_flag=true;
	// try {
	// String testName = "updateRoomStatus_1349";
	// WSClient.startTest(testName, "Update master Guest Service to Do Not
	// Disturb in the component rooms", "targetedRegression");
	// if (OperaPropConfig.getPropertyConfigResults(
	// new String[] {"Rooms"})) {
	// WSClient.setData("{var_resort}", OPERALib.getResort());
	// // Prerequisite :Fetching rooms that are not reserved
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// //WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	//
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// HTNGLib.getInterfaceFromAddress());
	//
	//
	//// room_num="6031";
	//// roomNumber2="200";
	// room_num=OperaPropConfig.getDataSetForCode("Rooms", "DS_42");
	// roomNumber2=OperaPropConfig.getDataSetForCode("Rooms", "DS_43");
	// WSClient.writeToReport(LogStatus.INFO, "<b>Master Room
	// :"+room_num+"</b>");
	// WSClient.writeToReport(LogStatus.INFO, "<b>Component Room
	// :"+roomNumber2+"</b>");
	//
	// if(profileID.equals(""))
	// profileID=CreateProfile.createProfile("DS_01");
	// if(!profileID.equals("error"))
	// {
	// WSClient.setData("{var_profileId}", profileID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:
	// "+profileID+"</b>");
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	//// WSClient.setData("{VAR_RATEPLANCODE}", "CKRRC");
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	//// WSClient.setData("{var_roomType}", "OWSRT");
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_payment}",
	// OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	//
	// // Prerequisite 2 Create Reservation
	//
	// HashMap<String,String> resv=CreateReservation.createReservation("DS_12");
	// String resvID = resv.get("reservationId");
	//
	// if(!resvID.equals("error"))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:
	// "+resvID+"</b>");
	// WSClient.setData("{var_resvId}", resvID);
	//
	//
	// // Prerequisite 3: Fetching available Hotel rooms with room type
	//
	// String roomNumber=room_num;
	// //Prerequisite 4: Creating a room to assign
	//
	// WSClient.setData("{var_roomNumber}", roomNumber);
	//
	// //Prerequisite 5: Changing the room status to inspected to assign the
	// room for checking in
	//
	// if (SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01")) {
	//
	// // Prerequisite 6: Assign Room
	//
	// if (AssignRoom.assignRoom("DS_01")) {
	//
	// //Prerequisite 7: CheckIn Reservation
	//
	//
	// if(CheckinReservation.checkinReservation("DS_01")){
	// WSClient.setData("{var_RoomNo}", roomNumber);
	// }
	// }
	//
	// }
	//
	//
	//
	// // Prerequisite:If the rooms are already dirty make it inspected
	//
	// WSClient.setData("{var_roomNumber}", room_num);
	// if(SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01")){
	// WSClient.setData("{var_roomNumber}", roomNumber2);
	// if(SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01")){
	// WSClient.setData("{var_RoomNo}", room_num);
	//
	// WSClient.writeToReport(LogStatus.INFO, "<b>Master room number
	// :"+room_num+"</b>");
	// WSClient.writeToReport(LogStatus.INFO, "<b>Component room number
	// :"+roomNumber2+"</b>");
	//
	// // Updating the Room status of the Created/fetched room to Dirty
	// if (prerequisite_block_flag == true) {
	// String UpdateRoomStatusReq =
	// WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus", "DND");
	// String UpdateRoomStatusResponseXML =
	// WSClient.processSOAPMessage(UpdateRoomStatusReq);
	// if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
	// "UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS",false)) {
	// String query = WSClient.getQuery("QS_03");
	// HashMap<String, String> roomRecord = WSClient.getDBRow(query);
	// String status = WSClient.getElementValue(UpdateRoomStatusReq,
	// "UpdateRoomStatusRequest_GuestServiceStatus", XMLType.REQUEST);
	//
	// // Validating the room status from the request with the
	// // Database
	// // value
	// WSClient.writeToReport(LogStatus.INFO, "<b>Checking the Guest Service
	// status of the Master room</b>");
	//
	// boolean result = WSAssert.assertEquals(status,
	// roomRecord.get("SERVICE_STATUS"), true);
	// String reqXpath =
	// WSClient.getRequestXPath("UpdateRoomStatusRequest_GuestServiceStatus");
	//
	// if (result == true) {
	// WSClient.writeToReport(LogStatus.PASS,
	// reqXpath + " is the Xpath where the value is equal to the database value
	// that is "
	// + roomRecord.get("SERVICE_STATUS"));
	// } else {
	// WSClient.writeToReport(LogStatus.FAIL,
	// reqXpath + "is the Xpath where the value is not equal to the database
	// value that is "
	// + roomRecord.get("SERVICE_STATUS"));
	//
	// }
	//
	//
	//
	//
	//
	//
	// WSClient.writeToReport(LogStatus.INFO, "<b>Checking the Guest Service
	// status of the component room</b>");
	// WSClient.setData("{var_RoomNo}", roomNumber2);
	//
	// query = WSClient.getQuery("QS_03");
	// roomRecord = WSClient.getDBRow(query);
	// status = WSClient.getElementValue(UpdateRoomStatusReq,
	// "UpdateRoomStatusRequest_GuestServiceStatus", XMLType.REQUEST);
	//
	// // Validating the room status from the request with the
	// // Database
	// // value
	//
	// result = WSAssert.assertEquals(status, roomRecord.get("SERVICE_STATUS"),
	// true);
	//
	// if (result == true) {
	// WSClient.writeToReport(LogStatus.PASS,"Component Room's guest service
	// status is updated to DO NOT DISTURB"
	// );
	// } else {
	// WSClient.writeToReport(LogStatus.FAIL,
	// "Component Room's guest service status is NOT updated to DO NOT
	// DISTURB");
	//
	// }
	//
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	// catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	//
	// try {
	// WSClient.setData("{var_roomNumber}", room_num);
	// SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
	// WSClient.setData("{var_roomNumber}", roomNumber2);
	// SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	//
	//

	@Test(groups = { "fullRegression", "UpdateRoomStatus", "HTNG2008BExt", "HTNG" })

	public void updateRoomStatus_1342() {
		try {
			String testName = "updateRoomStatus_1342";
			WSClient.startTest(testName,
					"Verify that an error message is populated in response when updating return status of a NOT an Out Of Order room, to Completed",
					"fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				// Prerequisite :Fetching rooms that are not reserved
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				// String room_num="'A65331";
				String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");
				if (room_num.equals("error")) {
					room_num = CreateRoom.createRoom("RoomMaint");
				}
				if (!room_num.equals("error")) {
					WSClient.setData("{var_RoomNo}", room_num);
					WSClient.setData("{var_roomNumber}", room_num);

					if (SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01")) {
						WSClient.writeToReport(LogStatus.INFO, "<b> Room Status is updated to INSPECTED </b>");
						String query = WSClient.getQuery("HTNGExtUpdateRoomStatus", "QS_01");
						HashMap<String, String> roomRecord = WSClient.getDBRow(query);
						if (WSAssert.assertEquals("Inspected", roomRecord.get("ROOM_STATUS"), false)) {
							HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
									HTNGLib.getInterfaceFromAddress());
							// WSClient.writeToReport(LogStatus.INFO, "<b> Dirty
							// room is updated to Completed </b>" );
							String roomStatusReason = OperaPropConfig.getDataSetForCode("OOOSReason", "DS_01");
							WSClient.setData("{var_roomStatusReason}", roomStatusReason);
							String UpdateRoomStatusReq = WSClient.createSOAPMessage("HTNGExtUpdateRoomStatus",
									"OOComplete");
							String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
							if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL", false)) {

								query = WSClient.getQuery("QS_01");
								roomRecord = WSClient.getDBRow(query);
								String status = "CLEAN";

								// Validating the room status from the request
								// with the
								// Database
								// value
								boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);
								String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_RoomStatus");

								if (result == true) {
									WSClient.writeToReport(LogStatus.FAIL,
											"Room Status of NOT an Out of Order room is updated to CLEAN in the Database as the repair status is COMPLETED");
								} else {
									WSClient.writeToReport(LogStatus.PASS,
											"Room Status of an Out of Order room is NOT updated to CLEAN in the Database when the repair status is COMPLETED");

								}

							}
							if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
									true)) {
								String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
										"Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>" + "The text displayed in the response is    :     " + message + "</b>");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}
}
