package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateRoom;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchHotelRooms;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.SetHousekeepingRoomStatus;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class AssignRoom extends WSSetUp {

	String profileID = "", queueResvParameterEnabled = "";

	HashMap<String, String> resvID = new HashMap<>();

	HashMap<String, String> resvID1 = new HashMap<>();

	/**
	 *
	 * Method to check if the OWS Assign Room is working i.e., fetching
	 * reservation details such as Reservation id,Confirmation no,Resort
	 * Id,Reservation status,Profile id for a given reservation.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "sanity", "AssignRoom", "OWS", "Reservation" })

	public void assignRoom_26318() {
		try {
			String testName = "assignRoom_26318";
			WSClient.startTest(testName, "Verify that a room is assigned to a reservation", "sanity");

			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String roomnum = WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")
						+ WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}");
				WSClient.setData("{var_roomNum}", roomnum);
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();

				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resort = OPERALib.getResort();

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resort, interfaceName));
				// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
				// OWSLib.getChannelType(channel),
				// OWSLib.getChannelCarier(resort, channel));
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_state}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "COUNTRY_CODE"));
				WSClient.setData("{var_addressType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
				WSClient.setData("{var_phoneType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE"));
				WSClient.setData("{var_profileType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));

				OPERALib.setOperaHeader(OPERALib.getUserName());

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						if (resvID != null) {
							/******************
							 * Prerequisite 3: Creating a room
							 ************************/
							WSClient.setData("{var_CreateRoom}", roomnum);

							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "DS_04");

							String createRoomResponseXML = WSClient.processSOAPMessage(createRoomReq);
							if (WSAssert.assertIfElementExists(createRoomResponseXML, "CreateRoomRS_Success",
									false) == false) {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->CreateRoom fails");

							} else {
								WSClient.setData("{var_roomNumber}", WSClient.getElementValue(createRoomReq,
										"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST));
							}

							/******************
							 * Prerequisite 4: Assigning a Room
							 ************************/
							String fetchResvReq = WSClient.createSOAPMessage("OWSAssignRoom", "DS_01");
							String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
							if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode", true)) {

								String message = WSClient.getElementValue(fetchResvRes,
										"AssignRoomResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL, message);
							} else {
								if (WSAssert.assertIfElementExists(fetchResvRes,
										"AssignRoomResponse_Result_resultStatusFlag", false)) {
									if (WSAssert.assertIfElementValueEquals(fetchResvRes,
											"AssignRoomResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										String query = WSClient.getQuery("OWSResvAdvAssignRoom", "QS_01");

										LinkedHashMap<String, String> assignRoom = WSClient.getDBRow(query);

										String resvID = WSClient.getElementValue(fetchResvReq,
												"AssignRoomRequest_ResvNameId", XMLType.REQUEST);
										if (WSAssert.assertEquals(resvID, assignRoom.get("RESERVATIONID"), true))
											WSClient.writeToReport(LogStatus.PASS, "Reservation ID-> Expected: "
													+ resvID + " Actual " + assignRoom.get("RESERVATIONID"));
										else
											WSClient.writeToReport(LogStatus.FAIL, "Reservation ID-> Expected: "
													+ resvID + " Actual " + assignRoom.get("RESERVATIONID"));
										String roomNumber = WSClient.getElementValue(fetchResvReq,
												"AssignRoomRequest_RoomNoRequested", XMLType.REQUEST);
										if (WSAssert.assertEquals(roomNumber, assignRoom.get("ROOMNUMBER"), true))
											WSClient.writeToReport(LogStatus.PASS, "Room Number-> Expected: "
													+ roomNumber + " Actual " + assignRoom.get("ROOMNUMBER"));
										else
											WSClient.writeToReport(LogStatus.FAIL, "Room Number-> Expected: "
													+ roomNumber + " Actual " + assignRoom.get("ROOMNUMBER"));
									} else
										WSClient.writeToReport(LogStatus.FAIL, "Assign Room fails");
								}
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite failed >> Reservation ID is not created");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile ID is not created");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile is not created");
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
	 * MINIMUM REGRESSION 1: Auto Assigning of a Room
	 *
	 * Checking if a room is getting assigned automatically to a reservation
	 * without giving a room
	 */

	@Test(groups = { "minimumRegression", "AssignRoom", "OWS" })
	public void assignRoom_26319() {
		try {
			String testName = "assignRoom_26319";
			WSClient.startTest(testName, "Verify that a room is auto assigned to a reservation", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String roomnum = WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")
						+ WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}");
				WSClient.setData("{var_roomNum}", roomnum);
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resort = OPERALib.getResort();

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resort, interfaceName));
				// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
				// OWSLib.getChannelType(channel),
				// OWSLib.getChannelCarier(resort, channel));
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_state}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "COUNTRY_CODE"));
				WSClient.setData("{var_addressType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
				WSClient.setData("{var_phoneType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE"));
				WSClient.setData("{var_profileType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));

				OPERALib.setOperaHeader(OPERALib.getUserName());

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_02"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						if (resvID != null) {
							WSClient.writeToReport(LogStatus.INFO, resvID.toString());

							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							/******************
							 * Prerequisite 3: Creating a room
							 ************************/
							WSClient.setData("{var_CreateRoom}", roomnum);

							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "DS_04");

							String createRoomResponseXML = WSClient.processSOAPMessage(createRoomReq);
							if (WSAssert.assertIfElementExists(createRoomResponseXML, "CreateRoomRS_Success",
									false) == false) {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->CreateRoom fails");

							} else {
								String roomNumber = WSClient.getElementValue(createRoomReq,
										"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

								WSClient.setData("{var_roomNumber}", roomNumber);
								/**********
								 * Prerequisite 5: Changing the room status to
								 * inspected to assign the room for checking in
								 ***********/
								String setHousekeepingRoomStatusReq = WSClient
										.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
								String setHousekeepingRoomStatusRes = WSClient
										.processSOAPMessage(setHousekeepingRoomStatusReq);

								if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
										"SetHousekeepingRoomStatusRS_Success", true)) {
									/***********
									 *
									 * /****************** Assigning a Room
									 ************************/
									String fetchResvReq = WSClient.createSOAPMessage("OWSAssignRoom", "DS_04");
									String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
									if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode",
											true)) {

										String message = WSClient.getElementValue(fetchResvRes,
												"AssignRoomResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL, message);
									} else {
										if (WSAssert.assertIfElementValueEquals(fetchResvRes,
												"AssignRoomResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											String query = WSClient.getQuery("OWSResvAdvAssignRoom", "QS_02");
											LinkedHashMap<String, String> assignRoom = WSClient.getDBRow(query);
											WSClient.writeToReport(LogStatus.INFO,
													"-----------------AUTO ASSIGNING OF A ROOM-----------------");
											String resvID = WSClient.getElementValue(fetchResvReq,
													"AssignRoomRequest_ResvNameId", XMLType.REQUEST);
											if (WSAssert.assertEquals(resvID, assignRoom.get("RESERVATIONID"), true))
												WSClient.writeToReport(LogStatus.PASS,
														"Values for Reservation ID Expected: " + resvID + " Actual "
																+ assignRoom.get("RESERVATIONID"));
											else
												WSClient.writeToReport(LogStatus.FAIL,
														"Values for Reservation ID Expected: " + resvID + " Actual "
																+ assignRoom.get("RESERVATIONID"));
											roomNumber = WSClient.getElementValue(fetchResvRes,
													"AssignRoomResponse_RoomNoAssigned", XMLType.RESPONSE);
											if (WSAssert.assertEquals(roomNumber, assignRoom.get("ROOMNUMBER"), true))
												WSClient.writeToReport(LogStatus.PASS,
														"Values for Room Number Expected: " + roomNumber + " Actual "
																+ assignRoom.get("ROOMNUMBER"));
											else
												WSClient.writeToReport(LogStatus.FAIL,
														"Values for Room Number Expected: " + roomNumber + " Actual "
																+ assignRoom.get("ROOMNUMBER"));

										} else
											WSClient.writeToReport(LogStatus.FAIL, "Assign Room fails");

									}
								}
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite failed >> Reservation ID is not created");
						}

					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	/***
	 * Minimum Regression: 2 Checking if an error is populated if an Out Of
	 * Order room is assigned to a reservation
	 */
	@Test(groups = { "minimumRegression", "AssignRoom", "OWS"})

	public void assignRoom_26320() {
		try {
			String testName = "assignRoom_26320";
			WSClient.startTest(testName, "Verify that assigning room of an out of service room is unsuccessful", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();
				WSClient.setData("{var_chain}", OPERALib.getChain());
				String roomnum=WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")+WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}");
				WSClient.setData("{var_roomNum}", roomnum);
				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resort = OPERALib.getResort();
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resort, interfaceName));
				//OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort, OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_state}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "COUNTRY_CODE"));
				WSClient.setData("{var_addressType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
				WSClient.setData("{var_phoneType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE"));
				WSClient.setData("{var_profileType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));

				OPERALib.setOperaHeader(OPERALib.getUserName());

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
				String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);

				if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true)) {

					if (WSAssert.assertIfElementExists(createProfileResponseXML,
							"CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {
						String profileId = WSClient.getElementValue(createProfileResponseXML,
								"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
						WSClient.setData("{var_profileId}", profileId);


						/******************
						 * Prerequisite 2:Create a Reservation
						 ************************/
						String createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_01");
						String createResvRes = WSClient.processSOAPMessage(createResvReq);

						if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success", true)) {

							String reservationId = WSClient.getElementValue(createResvRes,
									"Reservation_ReservationIDList_UniqueID_ID", XMLType.RESPONSE);

							WSClient.setData("{var_resvId}", reservationId);
							System.out.println(reservationId);

							if (reservationId != null && reservationId != "") {
								/******************
								 * Prerequisite 3: Setting an Out of Order room
								 ************************/
								String room_num=FetchHotelRooms.fetchHotelRooms("DS_15");

								String reasonCode=OperaPropConfig.getDataSetForCode("OOOSReason","DS_01");
								WSClient.setData("{var_ReasonCode}",reasonCode);
								if(room_num.equals("error")){
									room_num=CreateRoom.createRoom("RoomMaint");
								}
								if(!room_num.equals("error")){
									WSClient.setData("{var_OSRoom}",room_num);
									WSClient.setData("{var_Resort}",OPERALib.getResort());
									WSClient.setData("{var_RoomNo}",room_num);
									WSClient.writeToReport(LogStatus.INFO, "<b> Room Status is updated to Out OF Service </b>" );

									String req_createOORoom = WSClient.createSOAPMessage("SetRoomOutOfService", "DS_01");
									String res_createOORoom = WSClient.processSOAPMessage(req_createOORoom);
									String query = WSClient.getQuery("HTNGExtUpdateRoomStatus","QS_01");
									HashMap<String, String> roomRecord = WSClient.getDBRow(query);
									if(WSAssert.assertEquals("OutOfService", roomRecord.get("ROOM_STATUS"), false)){

										WSClient.setData("{var_roomNumber}", room_num);


										/******************
										 * Assigning a Room
										 ************************/
										String fetchResvReq = WSClient.createSOAPMessage("OWSAssignRoom", "DS_01");
										String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
										if(WSAssert.assertIfElementExists(fetchResvRes,"AssignRoomResponse_faultcode", true)){

											String message=WSClient.getElementValue(fetchResvRes,"AssignRoomResponse_faultstring", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.FAIL, message);
										}
										else{
											if (WSAssert.assertIfElementValueEquals(fetchResvRes,
													"AssignRoomResponse_Result_resultStatusFlag","SUCCESS", true)) {
												WSClient.writeToReport(LogStatus.FAIL, "An Error is not displayed as Out Of Service room is passed");

											}
											if(WSAssert.assertIfElementValueEquals(fetchResvRes,
													"AssignRoomResponse_Result_resultStatusFlag","FAIL", false)){
												String err=WSClient.getElementValue(fetchResvRes, "AssignRoomResponse_Result_GDSError",XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.PASS, "An Error is displayed as the room given is Out Of Order");
												WSClient.writeToReport(LogStatus.PASS, "Error message displayed is--> "+err);

											}
										} }else{
											WSClient.writeToReport(LogStatus.WARNING,
													"Prerequisite failed >> Room is not updated to Out Of Service");
										}
								}
								else{
									WSClient.writeToReport(LogStatus.WARNING,
											"Prerequisite failed >> Room is not created");
								}

							}else {

								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisite failed >> Reservation is not created");
							}

						} else {

							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite failed >> Reservation ID is not created");

						}
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile is not created");
					}
				}else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile is not created");
				}
			}

		}

		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}


	/*** Minimum Regression: 2
	 * Checking if an error is populated if an Out Of Service room
	 * is assigned to a reservation
	 */

	@Test(groups = { "minimumRegression", "AssignRoom", "OWS"})

	public void assignRoom_26321() {
		try {
			String testName = "assignRoom_26321";
			WSClient.startTest(testName, "Verify that assigning room of an out of service room is unsuccessful", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();
				WSClient.setData("{var_chain}", OPERALib.getChain());
				String roomnum=WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")+WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}");
				WSClient.setData("{var_roomNum}", roomnum);
				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resort = OPERALib.getResort();
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resort, interfaceName));
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_state}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "COUNTRY_CODE"));
				WSClient.setData("{var_addressType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
				WSClient.setData("{var_phoneType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE"));
				WSClient.setData("{var_profileType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));

				OPERALib.setOperaHeader(OPERALib.getUserName());

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				//				String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
				//				String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);
				//
				//				if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true)) {
				//
				//					if (WSAssert.assertIfElementExists(createProfileResponseXML,
				//							"CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {
				//						String profileId = WSClient.getElementValue(createProfileResponseXML,
				//								"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
				//						WSClient.setData("{var_profileId}", profileId);
				//
				//
				//						/******************
				//						 * Prerequisite 2:Create a Reservation
				//						 ************************/
				//						String createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_01");
				//						String createResvRes = WSClient.processSOAPMessage(createResvReq);
				//
				//						if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success", true)) {
				//
				//							String reservationId = WSClient.getElementValue(createResvRes,
				//									"Reservation_ReservationIDList_UniqueID_ID", XMLType.RESPONSE);
				//
				//							WSClient.setData("{var_resvId}", reservationId);
				//							System.out.println(reservationId);
				//
				//							if (reservationId != null && reservationId != "") {

				profileID=CreateProfile.createProfile("DS_01");
				if(!profileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", profileID);

					/******************* Prerequisite 2:Create a Reservation ************************/

					resvID=CreateReservation.createReservation("DS_01");
					if(!resvID.equals("error"))
					{
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						if (resvID != null) {
							/******************
							 * Prerequisite 3: Setting an OutOfService room
							 ************************/

							String room_num=FetchHotelRooms.fetchHotelRooms("DS_15");

							String reasonCode=OperaPropConfig.getDataSetForCode("OOOSReason","DS_01");
							WSClient.setData("{var_ReasonCode}",reasonCode);
							if(room_num.equals("error")){
								room_num=CreateRoom.createRoom("RoomMaint");
							}
							if(!room_num.equals("error")){
								WSClient.setData("{var_OORoom}",room_num);
								WSClient.setData("{var_Resort}",OPERALib.getResort());
								WSClient.setData("{var_RoomNo}",room_num);
								WSClient.setData("{var_roomNumber}",room_num);

								WSClient.writeToReport(LogStatus.INFO, "<b> Room Status is updated to Out OF Order </b>" );

								String req_createOORoom = WSClient.createSOAPMessage("SetRoomOutOfOrder", "DS_01");
								String res_createOORoom = WSClient.processSOAPMessage(req_createOORoom);
								String query = WSClient.getQuery("HTNGExtUpdateRoomStatus","QS_01");
								HashMap<String, String> roomRecord = WSClient.getDBRow(query);
								if(WSAssert.assertEquals("OutOfOrder", roomRecord.get("ROOM_STATUS"), false)){


									/******************
									 *  Assigning a Room
									 ************************/
									String fetchResvReq = WSClient.createSOAPMessage("OWSAssignRoom", "DS_01");
									String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
									if(WSAssert.assertIfElementExists(fetchResvRes,"AssignRoomResponse_faultcode", true)){

										String message=WSClient.getElementValue(fetchResvRes,"AssignRoomResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL, message);
									}
									else{
										if (WSAssert.assertIfElementValueEquals(fetchResvRes,
												"AssignRoomResponse_Result_resultStatusFlag","SUCCESS", true)) {
											WSClient.writeToReport(LogStatus.FAIL, "An Error is not displayed as there is no reservation associated");


										}
										if(WSAssert.assertIfElementValueEquals(fetchResvRes,
												"AssignRoomResponse_Result_resultStatusFlag","FAIL", false)){
											String err=WSClient.getElementValue(fetchResvRes, "AssignRoomResponse_Result_GDSError",XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.PASS, "An Error is displayed as the room given is Out Of Service");
											WSClient.writeToReport(LogStatus.PASS, "Error message displayed is--> "+err);

										}}
								}
							} }else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisite failed >> Reservation ID is not created");
							}
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile is not created");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	/***
	 * MR 4 Assigning a room when there is no reservation ID associated
	 * Verifying if an error is populated
	 */
	@Test(groups = { "minimumRegression", "AssignRoom", "OWS" })

	public void assignRoom_26322() {
		try {
			String testName = "assignRoom_26322";
			WSClient.startTest(testName, "Verify that assigning room is unsuccessful without a reservation",
					"minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resort = OPERALib.getResort();
				String roomnum = WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")
						+ WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}");
				WSClient.setData("{var_roomNum}", roomnum);
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resort, interfaceName));
				// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
				// OWSLib.getChannelType(channel),
				// OWSLib.getChannelCarier(resort, channel));
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_state}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "COUNTRY_CODE"));
				WSClient.setData("{var_addressType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
				WSClient.setData("{var_phoneType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE"));
				WSClient.setData("{var_profileType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));

				OPERALib.setOperaHeader(OPERALib.getUserName());

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				// String createProfileReq =
				// WSClient.createSOAPMessage("CreateProfile", "DS_01");
				// String createProfileResponseXML =
				// WSClient.processSOAPMessage(createProfileReq);
				//
				// if (WSAssert.assertIfElementExists(createProfileResponseXML,
				// "CreateProfileRS_Success", true)) {
				//
				// if (WSAssert.assertIfElementExists(createProfileResponseXML,
				// "CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {
				// String profileId =
				// WSClient.getElementValue(createProfileResponseXML,
				// "CreateProfileRS_ProfileIDList_UniqueID_ID",
				// XMLType.RESPONSE);
				// WSClient.setData("{var_profileId}", profileId);

				if (profileID.equals(""))
					CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					/******************
					 * Prerequisite 2: Creating a room
					 ************************/
					WSClient.setData("{var_CreateRoom}", roomnum);

					WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "DS_04");

					String createRoomResponseXML = WSClient.processSOAPMessage(createRoomReq);
					if (WSAssert.assertIfElementExists(createRoomResponseXML, "CreateRoomRS_Success", false) == false) {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->CreateRoom fails");

					} else {
						WSClient.setData("{var_roomNumber}", WSClient.getElementValue(createRoomReq,
								"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST));
					}

					/******************
					 * Assigning a Room
					 ************************/
					String fetchResvReq = WSClient.createSOAPMessage("OWSAssignRoom", "DS_03");
					String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
					if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode", true)) {

						String message = WSClient.getElementValue(fetchResvRes, "AssignRoomResponse_faultstring",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, message);
					} else {
						if (WSAssert.assertIfElementValueEquals(fetchResvRes,
								"AssignRoomResponse_Result_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"An Error is not displayed as there is no reservation associated");

						}
						if (WSAssert.assertIfElementValueEquals(fetchResvRes,
								"AssignRoomResponse_Result_resultStatusFlag", "FAIL", false)) {
							String err = WSClient.getElementValue(fetchResvRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.PASS,
									"An Error is displayed as there is no reservation associated");

						}
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile ID is not created");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile is not created");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	/*********************
	 * Minimum Regression 5: Verifying if an error is populated when a
	 * reservation that is checked in is being assigned a room
	 *
	 */

	@Test(groups = { "minimumRegression", "AssignRoom", "OWS" })

	public void assignRoom_26323() {
		try {
			String testName = "assignRoom_26323";
			WSClient.startTest(testName, "Verify that a checked in reservation cannot be assigned a room",
					"minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();
				String roomNumber = "";
				String reservation;
				WSClient.setData("{var_chain}", OPERALib.getChain());
				String roomnum = WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")
						+ WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}");
				WSClient.setData("{var_roomNum}", roomnum);
				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resort = OPERALib.getResort();

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resort, interfaceName));
				// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
				// OWSLib.getChannelType(channel),
				// OWSLib.getChannelCarier(resort, channel));
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_state}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "COUNTRY_CODE"));
				WSClient.setData("{var_addressType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
				WSClient.setData("{var_phoneType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE"));
				WSClient.setData("{var_profileType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));

				OPERALib.setOperaHeader(OPERALib.getUserName());

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				// String createProfileReq =
				// WSClient.createSOAPMessage("CreateProfile", "DS_01");
				// String createProfileResponseXML =
				// WSClient.processSOAPMessage(createProfileReq);
				//
				// if (WSAssert.assertIfElementExists(createProfileResponseXML,
				// "CreateProfileRS_Success", true)) {
				// if
				// (WSAssert.assertIfElementExists(createProfileResponseXML,"CreateProfileRS_ProfileIDList_UniqueID_ID",
				// true)) {
				// String operaProfileID =
				// WSClient.getElementValue(createProfileResponseXML,"CreateProfileRS_ProfileIDList_UniqueID_ID",
				// XMLType.RESPONSE);
				//
				// WSClient.setData("{var_profileId}", operaProfileID);
				//
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				// /*****
				// * Prerequisite 2 Create Reservation
				// *********/
				// String createResvReq =
				// WSClient.createSOAPMessage("CreateReservation", "DS_04");
				// String createResvRes =
				// WSClient.processSOAPMessage(createResvReq);
				//
				// if (WSAssert.assertIfElementExists(createResvRes,
				// "CreateReservationRS_Success", true)) {
				// String reservationId =
				// WSClient.getElementValue(createResvRes,"Reservation_ReservationIDList_UniqueID_ID",
				// XMLType.RESPONSE);
				//
				// WSClient.setData("{var_resvId}", reservationId);

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_12");
					if (!resvID.equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						if (resvID != null) {

							/**********
							 * Prerequisite 3: Fetching available Hotel rooms
							 * with room type
							 ***********/

							String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
							String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

							if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room",
									true)) {

								roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
										"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

							}

							else {
								/***********
								 * Prerequisite 4: Creating a room to assign
								 ********/
								WSClient.setData("{var_CreateRoom}", roomnum);

								WSClient.setData("{var_RoomType}",
										OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
								String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "DS_04");
								String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

								if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

									roomNumber = WSClient.getElementValue(createRoomReq,
											"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
								}
							}

							WSClient.setData("{var_roomNumber}", roomNumber);
							/**********
							 * Prerequisite 5: Changing the room status to
							 * inspected to assign the room for checking in
							 ***********/
							String setHousekeepingRoomStatusReq = WSClient
									.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
							String setHousekeepingRoomStatusRes = WSClient
									.processSOAPMessage(setHousekeepingRoomStatusReq);

							if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
									"SetHousekeepingRoomStatusRS_Success", true)) {
								/***********
								 * Prerequisite 6: Assign Room
								 **********/
								String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
								String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

								if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {
									/************
									 * Prerequisite 7: CheckIn Reservation
									 **************/
									String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
									String checkInRes = WSClient.processSOAPMessage(checkInReq);

									if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success",
											true)) {

										/******************
										 * Assigning a Room
										 ************************/

										String fetchResvReq = WSClient.createSOAPMessage("OWSAssignRoom", "DS_01");
										String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
										if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode",
												true)) {

											String message = WSClient.getElementValue(fetchResvRes,
													"AssignRoomResponse_faultstring", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.FAIL, message);
										} else {
											if (WSAssert.assertIfElementValueEquals(fetchResvRes,
													"AssignRoomResponse_Result_resultStatusFlag", "SUCCESS", true)) {
												WSClient.writeToReport(LogStatus.FAIL,
														"An Error is not displayed as there is no reservation associated");

											}
											if (WSAssert.assertIfElementValueEquals(fetchResvRes,
													"AssignRoomResponse_Result_resultStatusFlag", "FAIL", false)) {
												String err = WSClient.getElementValue(fetchResvRes,
														"AssignRoomResponse_Result_GDSError", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.PASS,
														"An Error is displayed as there is no reservation associated.");
												WSClient.writeToReport(LogStatus.PASS,
														"Error message displayed is--> " + err);

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
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to do reservation");
						}

					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create profile");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create profile");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	/*
	 * Cancel a reservation and assign a room
	 */
//	@Test(groups = { "minimumRegression", "AssignRoom", "OWS" })

	public void assignRoom_26331() {
		try {
			String testName = "assignRoom_26324";
			WSClient.startTest(testName, "Verify that a cancelled reservation cannot be assigned a room",
					"minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resort = OPERALib.getResort();
				String roomnum = WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")
						+ WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}");
				WSClient.setData("{var_roomNum}", roomnum);
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resort, interfaceName));
				// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
				// OWSLib.getChannelType(channel),
				// OWSLib.getChannelCarier(resort, channel));
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_state}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "COUNTRY_CODE"));
				WSClient.setData("{var_addressType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
				WSClient.setData("{var_phoneType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE"));
				WSClient.setData("{var_profileType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));

				OPERALib.setOperaHeader(OPERALib.getUserName());

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/
					resvID = CreateReservation.createReservation("DS_05");
					WSClient.writeToReport(LogStatus.INFO, resvID.toString());
					if (!resvID.equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						if (CancelReservation.cancelReservation("DS_02")) {
							WSClient.writeToLog("Reservation cancellation successful");

							/******************
							 * Prerequisite 4: Assigning a Room
							 ************************/
							String fetchResvReq = WSClient.createSOAPMessage("OWSAssignRoom", "DS_02");
							String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
							if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode", true)) {

								String message = WSClient.getElementValue(fetchResvRes, "AssignRoomResponse_faultstring",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL, message);
							} else {
								if (WSAssert.assertIfElementValueEquals(fetchResvRes,
										"AssignRoomResponse_Result_resultStatusFlag", "FAIL", false)) {
									WSClient.writeToReport(LogStatus.PASS, "******Errored Response when a cancelled reservation is being assigned a room*****");
								} else
									WSClient.writeToReport(LogStatus.FAIL, "******No Errored Response when a cancelled reservation is being assigned a room*****");
							}
						} else
							WSClient.writeToLog("Reservation cancellation failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation ID is not created");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
			}

		} catch (Exception e) {

			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	/*
	 *
	 * Assigning a Pseudo Room
	 */

//	@Test(groups = { "minimumRegression", "AssignRoom", "OWS","qwwwssss" })
//
//	public void assignRoom_26338() {
//		try {
//			String testName = "assignRoom_26338";
//			WSClient.startTest(testName, "Verify that assigning a pseudo room is successful", "minimumRegression");
//			if (OperaPropConfig
//					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
//				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));
//
//				String resortOperaValue = OPERALib.getResort();
//				String interfaceName = OWSLib.getChannel();
//				WSClient.setData("{var_chain}", OPERALib.getChain());
//				String roomnum = WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")
//						+ WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}");
//				WSClient.setData("{var_roomNum}", roomnum);
//				OPERALib.setOperaHeader(OPERALib.getUserName());
//				String resort = OPERALib.getResort();
//
//				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
//						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resort, interfaceName));
//				// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
//				// OWSLib.getChannelType(channel),
//				// OWSLib.getChannelCarier(resort, channel));
//				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
//
//				WSClient.setData("{var_profileSource}", interfaceName);
//				WSClient.setData("{var_resort}", resortOperaValue);
//				WSClient.setData("{var_extResort}", resortExtValue);
//				WSClient.setData("{var_profileSource}", interfaceName);
//				WSClient.setData("{var_resort}", resortOperaValue);
//				WSClient.setData("{var_extResort}", resortExtValue);
//
//				WSClient.setData("{var_state}",
//						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "COUNTRY_CODE"));
//				WSClient.setData("{var_addressType}",
//						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
//				WSClient.setData("{var_phoneType}",
//						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE"));
//				WSClient.setData("{var_profileType}",
//						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));
//
//				OPERALib.setOperaHeader(OPERALib.getUserName());
//
//				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
//						HTNGLib.getInterfaceFromAddress());
//
//				/************
//				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
//				 * Code
//				 *********************************/
//				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
//				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_04"));
//				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
//				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
//				// WSClient.setData("{var_roomNumber}",
//				// WSClient.getDataSetForCode("Rooms", "DS_02"));
//
//				/************
//				 * Prerequisite 1: Create profile
//				 *********************************/
//
//				profileID = CreateProfile.createProfile("DS_01");
//				if (!profileID.equals("error")) {
//					WSClient.setData("{var_profileId}", profileID);
//
//					/*******************
//					 * Prerequisite 2:Create a Reservation
//					 ************************/
//
//					resvID = CreateReservation.createReservation("DS_01");
//					if (!resvID.equals("error")) {
//						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
//						//if (resvID != null) {
//						/******************
//						 * Prerequisite 3: Creating a room
//						 ************************/
//						WSClient.setData("{var_CreateRoom}", roomnum);
//						// modified script
//						WSClient.setData("{var_roomNumber}", OperaPropConfig.getDataSetForCode("PseudoRoom", "DS_04"));
//						//WSClient.setData("{var_roomNumber}", OperaPropConfig.getDataSetForCode("PseudoRoom", "DS_02"));
//						
//						String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "DS_04");
//
//						String createRoomResponseXML = WSClient.processSOAPMessage(createRoomReq);
//						if (WSAssert.assertIfElementExists(createRoomResponseXML, "CreateRoomRS_Success",
//								false) == false) {
//							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->CreateRoom fails");
//
//						} else {
//							String roomNumber1 = WSClient.getElementValue(createRoomReq,
//									"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
//
//							WSClient.setData("{var_roomNumber}", roomNumber1);
//
//						/******************
//						 * Prerequisite 4: Assigning a Room
//						 ************************/
//						String fetchResvReq = WSClient.createSOAPMessage("OWSAssignRoom", "DS_01");
//						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
//						if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode", true)) {
//
//							String message = WSClient.getElementValue(fetchResvRes,
//									"AssignRoomResponse_faultstring", XMLType.RESPONSE);
//							WSClient.writeToReport(LogStatus.FAIL, message);
//						} else {
//							if (WSAssert.assertIfElementExists(fetchResvRes,
//									"AssignRoomResponse_Result_resultStatusFlag", false)) {
//								if (WSAssert.assertIfElementValueEquals(fetchResvRes,
//										"AssignRoomResponse_Result_resultStatusFlag", "SUCCESS", false)) {
//									String query = WSClient.getQuery("OWSResvAdvAssignRoom", "QS_01");
//
//									LinkedHashMap<String, String> assignRoom = WSClient.getDBRow(query);
//
//									String resvID = WSClient.getElementValue(fetchResvReq,
//											"AssignRoomRequest_ResvNameId", XMLType.REQUEST);
//									if (WSAssert.assertEquals(resvID, assignRoom.get("RESERVATIONID"), true))
//										WSClient.writeToReport(LogStatus.PASS, "Reservation ID-> Expected: "
//												+ resvID + " Actual " + assignRoom.get("RESERVATIONID"));
//									else
//										WSClient.writeToReport(LogStatus.FAIL, "Reservation ID-> Expected: "
//												+ resvID + " Actual " + assignRoom.get("RESERVATIONID"));
//									String roomNumber = WSClient.getElementValue(fetchResvReq,
//											"AssignRoomRequest_RoomNoRequested", XMLType.REQUEST);
//									if (WSAssert.assertEquals(roomNumber, assignRoom.get("ROOMNUMBER"), true))
//										WSClient.writeToReport(LogStatus.PASS, "Room Number-> Expected: "
//												+ roomNumber + " Actual " + assignRoom.get("ROOMNUMBER"));
//									else
//										WSClient.writeToReport(LogStatus.FAIL, "Room Number-> Expected: "
//												+ roomNumber + " Actual " + assignRoom.get("ROOMNUMBER"));
//								} else
//									WSClient.writeToReport(LogStatus.FAIL, "Assign Room fails");
//
//							}
//						}
//						}
//					} else {
//						WSClient.writeToReport(LogStatus.WARNING,
//								"Prerequisite failed >> Reservation ID is not created");
//					}
//				} else {
//					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
//				}
//
//			} else {
//				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile ID is not created");
//			}
//			/*} else {
//				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile is not created");
//			}*/
//
//		} catch (Exception e) {
//			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
//		} finally {
//			// logger.endExtentTest();
//		}
//	}
	/*
	 *
	 * Assigning a Shared Reservation
	 */

	//@Test(groups = { "minimumRegression", "AssignRoom", "OWS" })

	public void assignRoom_263381() {
		try {
			String testName = "assignRoom_26318";
			WSClient.startTest(testName, "Verify that assigning a room to a shared reservation is successful",
					"minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();
				WSClient.setData("{var_chain}", OPERALib.getChain());
				String roomnum = WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")
						+ WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}");
				WSClient.setData("{var_roomNum}", roomnum);
				WSClient.setData("{var_CreateRoom}", roomnum);

				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resort = OPERALib.getResort();

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resort, interfaceName));
				// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
				// OWSLib.getChannelType(channel),
				// OWSLib.getChannelCarier(resort, channel));
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_state}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "COUNTRY_CODE"));
				WSClient.setData("{var_addressType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
				WSClient.setData("{var_phoneType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE"));
				WSClient.setData("{var_profileType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));

				OPERALib.setOperaHeader(OPERALib.getUserName());

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_02"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_12");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						/*******************
						 * Prerequisite 3:Create a Profile (Sharer)
						 ************************/
						profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);

							/*******************
							 * Prerequisite 4:Create a Reservation (Sharer)
							 ************************/

							resvID1 = CreateReservation.createReservation("DS_12");
							if (!resvID1.get("reservationId").equals("error")) {
								WSClient.setData("{var_resvId1}", resvID1.get("reservationId"));

								/*******************
								 * Prerequisite 5:Share the reservations
								 ************************/
								String combineShareReq = WSClient.createSOAPMessage("CombineShareReservations",
										"DS_01");
								String combineShareRes = WSClient.processSOAPMessage(combineShareReq);

								if (WSAssert.assertIfElementExists(combineShareRes,
										"CombineShareReservationsRS_Success", true)) {
									if (resvID != null) {
										/******************
										 * Prerequisite 3: Creating a room
										 ************************/
										WSClient.setData("{var_CreateRoom}", roomnum);

										WSClient.setData("{var_RoomType}",
												OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
										String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "DS_04");

										String createRoomResponseXML = WSClient.processSOAPMessage(createRoomReq);
										if (WSAssert.assertIfElementExists(createRoomResponseXML,
												"CreateRoomRS_Success", false) == false) {
											WSClient.writeToReport(LogStatus.WARNING,
													"Prerequisite blocked --->CreateRoom fails");

										} else {
											WSClient.setData("{var_roomNumber}", WSClient.getElementValue(createRoomReq,
													"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST));
										}

										/******************
										 * Prerequisite 4: Assigning a Room
										 ************************/
									} else {
										WSClient.writeToReport(LogStatus.WARNING,
												"Prerequisite failed >> Reservation ID is not created");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING,
											"Prerequisite failed >> Reservation is not created");
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisite failed >> Profile ID is not created");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile is not created");
						}

					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}

	}

	/*
	 *
	 * Assigning a Component Room
	 */



	@Test(groups = { "minimumRegression", "AssignRoom", "OWS", "Reservation" })

	public void assignRoom_26318_dirty() {
		try {
			String testName = "assignRoom_26329";
			WSClient.startTest(testName, "Verify that a dirty room is assigned to a reservation", "minimumRegression");

			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {


				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();

				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resort = OPERALib.getResort();
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resort, interfaceName));
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());
				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						if (resvID != null) {
							/******************
							 * Prerequisite 3: Creating a room
							 ************************/
							String roomNumber1 = "";
							roomNumber1 = FetchHotelRooms.fetchHotelRooms("DS_03");
							if (roomNumber1.equals("") || roomNumber1.equals("error"))
								roomNumber1 = CreateRoom.createRoom("RoomMaint");
							if (!roomNumber1.equals("") || !roomNumber1.equals("error")) {
								WSClient.setData("{var_roomNumber}", roomNumber1);
								SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_06");

								/******************
								 * Prerequisite 4: Assigning a Room
								 ************************/
								String fetchResvReq = WSClient.createSOAPMessage("OWSAssignRoom", "DS_01");
								String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
								if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode",
										true)) {

									String message = WSClient.getElementValue(fetchResvRes,
											"AssignRoomResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, message);
								} else {
									if (WSAssert.assertIfElementExists(fetchResvRes,
											"AssignRoomResponse_Result_resultStatusFlag", false)) {
										if (WSAssert.assertIfElementValueEquals(fetchResvRes,
												"AssignRoomResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											String query = WSClient.getQuery("OWSResvAdvAssignRoom", "QS_01");

											LinkedHashMap<String, String> assignRoom = WSClient.getDBRow(query);

											String resvID = WSClient.getElementValue(fetchResvReq,
													"AssignRoomRequest_ResvNameId", XMLType.REQUEST);
											if (WSAssert.assertEquals(resvID, assignRoom.get("RESERVATIONID"), true))
												WSClient.writeToReport(LogStatus.PASS, "Reservation ID-> Expected: "
														+ resvID + " Actual " + assignRoom.get("RESERVATIONID"));
											else
												WSClient.writeToReport(LogStatus.FAIL, "Reservation ID-> Expected: "
														+ resvID + " Actual " + assignRoom.get("RESERVATIONID"));
											String roomNumber = WSClient.getElementValue(fetchResvReq,
													"AssignRoomRequest_RoomNoRequested", XMLType.REQUEST);
											if (WSAssert.assertEquals(roomNumber, assignRoom.get("ROOMNUMBER"), true))
												WSClient.writeToReport(LogStatus.PASS, "Room Number-> Expected: "
														+ roomNumber + " Actual " + assignRoom.get("ROOMNUMBER"));
											else
												WSClient.writeToReport(LogStatus.FAIL, "Room Number-> Expected: "
														+ roomNumber + " Actual " + assignRoom.get("ROOMNUMBER"));
										} else
											WSClient.writeToReport(LogStatus.FAIL, "Assign Room fails");
									}
								}
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite failed >> Reservation ID is not created");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile ID is not created");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile is not created");
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

	//	@Test(groups = { "minimumRegression", "AssignRoom", "OWS", "Reservation" })

	public void assignRoom_263189() {
		try {
			String testName = "assignRoom_263189";
			WSClient.startTest(testName,
					"Verify that a different room is assigned to a reservation when an already assigned room is sent in the request ",
					"minimumRegression");

			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String roomnum = WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")
						+ WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}");


				WSClient.setData("{var_roomNum}", roomnum);
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resort = OPERALib.getResort();

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resort, interfaceName));
				// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
				// OWSLib.getChannelType(channel),
				// OWSLib.getChannelCarier(resort, channel));
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_state}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "COUNTRY_CODE"));
				WSClient.setData("{var_addressType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
				WSClient.setData("{var_phoneType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE"));
				WSClient.setData("{var_profileType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));

				OPERALib.setOperaHeader(OPERALib.getUserName());

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						if (resvID != null) {
							/******************
							 * Prerequisite 3: Creating a room
							 ************************/
							WSClient.setData("{var_CreateRoom}", roomnum);

							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "DS_04");

							String createRoomResponseXML = WSClient.processSOAPMessage(createRoomReq);
							if (WSAssert.assertIfElementExists(createRoomResponseXML, "CreateRoomRS_Success",
									false) == false) {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->CreateRoom fails");

							} else {
								WSClient.setData("{var_roomNumber}", WSClient.getElementValue(createRoomReq,
										"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST));
							}

							/******************
							 * Prerequisite 4: Assigning a Room
							 ************************/
							String fetchResvReq = WSClient.createSOAPMessage("OWSAssignRoom", "DS_01");
							String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
							if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode", true)) {

								String message = WSClient.getElementValue(fetchResvRes,
										"AssignRoomResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL, message);
							} else {
								if (WSAssert.assertIfElementExists(fetchResvRes,
										"AssignRoomResponse_Result_resultStatusFlag", false)) {

									if (WSAssert.assertIfElementValueEquals(fetchResvRes,
											"AssignRoomResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										resvID = CreateReservation.createReservation("DS_01");
										if (!resvID.equals("error")) {
											WSClient.setData("{var_resvId}", resvID.get("reservationId"));
											if (resvID != null) {
												fetchResvReq = WSClient.createSOAPMessage("OWSAssignRoom", "DS_01");
												fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
												WSAssert.assertIfElementValueEquals(fetchResvRes,
														"AssignRoomResponse_Result_resultStatusFlag", "SUCCESS", false);

												String query = WSClient.getQuery("OWSResvAdvAssignRoom", "QS_03");

												LinkedHashMap<String, String> assignRoom = WSClient.getDBRow(query);

												String roomNumber = WSClient.getElementValue(fetchResvRes,
														"AssignRoomResponse_RoomNoAssigned", XMLType.RESPONSE);
												if (WSAssert.assertEquals(assignRoom.get("ROOMNUMBER"), roomNumber,
														true))
													WSClient.writeToReport(LogStatus.PASS, "Room Number-> Expected: "
															+ assignRoom.get("ROOMNUMBER") + " Actual " + roomNumber);
												else
													WSClient.writeToReport(LogStatus.FAIL, "Room Number-> Expected: "
															+ assignRoom.get("ROOMNUMBER") + " Actual ");
											} else
												WSClient.writeToReport(LogStatus.FAIL, "Assign Room fails");
										}
									}
								}
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite failed >> Reservation ID is not created");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile ID is not created");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile is not created");
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

	@Test(groups = { "minimumRegression", "AssignRoom", "OWS" })
	public void assignRoom_263197() {
		try {
			String testName = "assignRoom_263197";
			WSClient.startTest(testName,
					"Verify that error exists when a roomtype without rooms is passed in the request",
					"minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String roomnum = WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")
						+ WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}");
				WSClient.setData("{var_roomNum}", roomnum);
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();

				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resort = OPERALib.getResort();
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resort, interfaceName));
				// OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
				// OWSLib.getChannelType(channel),
				// OWSLib.getChannelCarier(resort, channel));
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_state}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "COUNTRY_CODE"));
				WSClient.setData("{var_addressType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "ADDRESS_TYPES"));
				WSClient.setData("{var_phoneType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PHONE_TYPE"));
				WSClient.setData("{var_profileType}",
						HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "PROFILE_TYPE"));

				OPERALib.setOperaHeader(OPERALib.getUserName());

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_09"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.equals("error")) {
						String roomNumber = "";
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						if (resvID != null) {
							WSClient.writeToReport(LogStatus.INFO, resvID.toString());

							WSClient.setData("{var_resvId}", resvID.get("reservationId"));

							/******************
							 * Assigning a Room
							 ************************/
							String fetchResvReq = WSClient.createSOAPMessage("OWSAssignRoom", "DS_02");
							String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
							if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_Result_GDSError",
									true)) {

								String message = WSClient.getElementValue(fetchResvRes,
										"AssignRoomResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.PASS, message);
							}
							if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode", true)) {

								String message = WSClient.getElementValue(fetchResvRes,
										"AssignRoomResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL, message);
							} else {
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"AssignRoomResponse_Result_resultStatusFlag", "FAIL", false);

							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite failed >> Reservation ID is not created");
						}

					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

}
