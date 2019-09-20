package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateRoom;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchHotelRooms;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class AssignRoomResvAdv extends WSSetUp {

	/**
	 * Method to check if the OWS Reservation Advanced Assign Room is working
	 * i.e., fetching
	 *
	 * reservation details such as Reservation id,Confirmation no,Resort
	 * Id,Reservation status,Profile id for a given reservation.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */

	String profileID = "", queueResvParameterEnabled = "";
	HashMap<String, String> resvID = new HashMap<>();
	HashMap<String, String> resvID1 = new HashMap<>();

	// @Test(groups = { "minimumRegression", "AssignRoomResvAdvanced",
	// "OWS","ResvAdvanced"})
	//
	// public void assignRoomResvAdvanced_60001() {
	// try {
	// String testName = "assignRoomResvAdvanced_60001";
	// WSClient.startTest(testName, "Verify that a room is assigned to a
	// reservation with channel when channel name are carrier name are
	// different", "minimumRegression");
	// if (OperaPropConfig
	// .getPropertyConfigResults(new String[] { "RateCode", "RoomType",
	// "SourceCode", "MarketCode" })) {
	// String interfaceName = HTNGLib.getHTNGInterface();
	// WSClient.setData("{var_chain}", OPERALib.getChain());
	// WSClient.setData("{var_owsresort}",
	// OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel(3)));
	//
	// String resortOperaValue = OPERALib.getResort();
	// String resortExtValue = HTNGLib.getExtResort(resortOperaValue,
	// interfaceName);
	// String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue,
	// interfaceName, "GENDER_MF");
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_gender}", genderExtValue);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	// String
	// roomnum=WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")+WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}");
	// WSClient.setData("{var_roomNum}", roomnum);
	// WSClient.setData("{var_state}",
	// HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName,
	// "COUNTRY_CODE"));
	// WSClient.setData("{var_addressType}",
	// HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName,
	// "ADDRESS_TYPES"));
	// WSClient.setData("{var_phoneType}",
	// HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName,
	// "PHONE_TYPE"));
	// WSClient.setData("{var_profileType}",
	// HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName,
	// "PROFILE_TYPE"));
	//
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	//
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// HTNGLib.getInterfaceFromAddress());
	//
	// /************
	// * Prerequisite : Room type, Rate Plan Code, Source Code, Market
	// * Code
	// *********************************/
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	//
	//
	// /************
	// * Prerequisite 1: Create profile
	// *********************************/
	//
	// profileID=CreateProfile.createProfile("DS_01");
	// if(!profileID.equals("error"))
	// {
	// WSClient.setData("{var_profileId}", profileID);
	//
	// /******************* Prerequisite 2:Create a Reservation
	// ************************/
	//
	// resvID=CreateReservation.createReservation("DS_01");
	// if(!resvID.equals("error"))
	// {
	// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
	// if (resvID != null) {
	// /******************
	// * Prerequisite 3: Creating a room
	// ************************/
	// WSClient.setData("{var_CreateRoom}", roomnum);
	//
	// WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "DS_04");
	// String createRoomResponseXML =
	// WSClient.processSOAPMessage(createRoomReq);
	// if (WSAssert.assertIfElementExists(createRoomResponseXML,
	// "CreateRoomRS_Success",
	// true) == false) {
	// WSClient.writeToReport(LogStatus.WARNING,
	// "Prerequisite blocked --->CreateRoom fails");
	//
	// } else {
	// WSClient.setData("{var_roomNumber}",
	// WSClient.getElementValue(createRoomReq,
	// "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST));
	// }
	// /******************
	// * OWS Assign Room Operation
	// ************************/
	// interfaceName = OWSLib.getChannel(3);
	// OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// OPERALib.getResort(),
	// OWSLib.getChannelType(interfaceName),
	// OWSLib.getChannelCarier(resortOperaValue, interfaceName));
	// String fetchResvReq = WSClient.createSOAPMessage("OWSResvAdvAssignRoom",
	// "DS_01");
	// String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
	// if(WSAssert.assertIfElementExists(fetchResvRes,"AssignRoomResponse_faultcode",
	// true)){
	//
	// String
	// message=WSClient.getElementValue(fetchResvRes,"AssignRoomResponse_faultstring",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.FAIL, message);
	// }
	// else{
	// if (WSAssert.assertIfElementExists(fetchResvRes,
	// "AssignRoomAdvResponse_RoomNoAssigned",
	// false)) {
	// String query=WSClient.getQuery("QS_01");
	// LinkedHashMap<String, String> assignRoom = WSClient.getDBRow(query);
	// String roomNumber = WSClient.getElementValue(fetchResvReq,
	// "AssignRoomAdvRequest_RoomNoRequested", XMLType.REQUEST);
	// if (WSAssert.assertEquals(roomNumber, assignRoom.get("ROOMNUMBER"),
	// true))
	// WSClient.writeToReport(LogStatus.PASS, "Room Number-> Expected: "
	// + roomNumber + " Actual: " + assignRoom.get("ROOMNUMBER"));
	// else
	// WSClient.writeToReport(LogStatus.FAIL, "Room Number-> Expected: "
	// + roomNumber + " Actual: " + assignRoom.get("ROOMNUMBER"));
	// String resvID = WSClient.getElementValue(fetchResvReq,
	// "AssignRoomAdvRequest_ResvNameId", XMLType.REQUEST);
	// if (WSAssert.assertEquals(resvID, assignRoom.get("RESERVATIONID"), true))
	// WSClient.writeToReport(LogStatus.PASS, "Reservation ID->Expected: "
	// + resvID + " Actual: " + assignRoom.get("RESERVATIONID"));
	// else
	// WSClient.writeToReport(LogStatus.FAIL, "Reservation ID->Expected: "
	// + resvID + " Actual: " + assignRoom.get("RESERVATIONID"));
	//
	// }
	// }
	// }else {
	// WSClient.writeToReport(LogStatus.WARNING,
	// "Prerequisite failed >> Reservation ID is not created");
	// }
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING,
	// "Prerequisite failed >> Reservation is not created");
	// }
	//
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile
	// ID is not created");
	// }
	// }
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally{
	// try {
	// if(!WSClient.getData("{var_resvId}").equals(""))
	// CancelReservation.cancelReservation("DS_02");
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	//

	/********
	 * To create a profile and set profileID global variable
	 ***************/

	@Test(groups = { "sanity", "AssignRoomResvAdvanced", "OWS", "ResvAdvanced" })

	public void assignRoomResvAdvanced_38445() {
		try {
			String testName = "assignRoomResvAdvanced_38445";
			WSClient.startTest(testName, "Verify that a room is assigned to a reservation", "sanity");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String genderExtValue = HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName, "GENDER_MF");
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_gender}", genderExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				String roomnum = WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")
						+ WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}");
				WSClient.setData("{var_roomNum}", roomnum);
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
							 * OWS Assign Room Operation
							 ************************/
							interfaceName = OWSLib.getChannel();
							OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
									OWSLib.getChannelType(interfaceName),
									OWSLib.getChannelCarier(resortOperaValue, interfaceName));
							String fetchResvReq = WSClient.createSOAPMessage("OWSResvAdvAssignRoom", "DS_01");
							String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
							if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode", true)) {

								String message = WSClient.getElementValue(fetchResvRes,
										"AssignRoomResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL, message);
							} else {
								if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomAdvResponse_RoomNoAssigned",
										false)) {
									String query = WSClient.getQuery("QS_01");
									LinkedHashMap<String, String> assignRoom = WSClient.getDBRow(query);
									String roomNumber = WSClient.getElementValue(fetchResvReq,
											"AssignRoomAdvRequest_RoomNoRequested", XMLType.REQUEST);
									if (WSAssert.assertEquals(roomNumber, assignRoom.get("ROOMNUMBER"), true))
										WSClient.writeToReport(LogStatus.PASS, "Room Number-> Expected: " + roomNumber
												+ " Actual: " + assignRoom.get("ROOMNUMBER"));
									else
										WSClient.writeToReport(LogStatus.FAIL, "Room Number-> Expected: " + roomNumber
												+ " Actual: " + assignRoom.get("ROOMNUMBER"));
									String resvID = WSClient.getElementValue(fetchResvReq,
											"AssignRoomAdvRequest_ResvNameId", XMLType.REQUEST);
									if (WSAssert.assertEquals(resvID, assignRoom.get("RESERVATIONID"), true))
										WSClient.writeToReport(LogStatus.PASS, "Reservation ID->Expected: " + resvID
												+ " Actual: " + assignRoom.get("RESERVATIONID"));
									else
										WSClient.writeToReport(LogStatus.FAIL, "Reservation ID->Expected: " + resvID
												+ " Actual: " + assignRoom.get("RESERVATIONID"));

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
	 *
	 *
	 * @Test(groups = { "minimumRegression", "AssignRoomResvAdvanced", "OWS"})
	 *
	 * public void assignRoomResvAdvanced_26318() { try { String testName =
	 * "assignRoomResvAdvanced_26318"; WSClient.startTest(testName,
	 * "Verify that  auto assigning of a room is successful",
	 * "minimumRegression"); if (OperaPropConfig .getPropertyConfigResults(new
	 * String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
	 * String resortOperaValue = OPERALib.getResort(); String resort =
	 * OPERALib.getResort(); String interfaceName = OWSLib.getChannel();
	 * WSClient.setData("{var_chain}", OPERALib.getChain());
	 * WSClient.setData("{var_owsresort}",
	 * OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));
	 *
	 *
	 * OPERALib.setOperaHeader(OPERALib.getUserName());
	 * OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	 * OPERALib.getResort(),OWSLib.getChannelType(interfaceName),
	 * OWSLib.getChannelCarier(resort, interfaceName)); String resortExtValue =
	 * OWSLib.getChannelResort(resortOperaValue, interfaceName);
	 * WSClient.setData("{var_profileSource}", interfaceName);
	 * WSClient.setData("{var_resort}", resortOperaValue);
	 * WSClient.setData("{var_extResort}", resortExtValue); String
	 * roomnum=WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")+WSClient.
	 * getKeywordData("{KEYWORD_ROOM_NUMBER}");
	 * WSClient.setData("{var_roomNum}", roomnum);
	 * WSClient.setData("{var_state}",
	 * HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName,
	 * "COUNTRY_CODE")); WSClient.setData("{var_addressType}",
	 * HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName,
	 * "ADDRESS_TYPES")); WSClient.setData("{var_phoneType}",
	 * HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName,
	 * "PHONE_TYPE")); WSClient.setData("{var_profileType}",
	 * HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName,
	 * "PROFILE_TYPE"));
	 *
	 *
	 *
	 *
	 *//************
	 * Prerequisite : Room type, Rate Plan Code, Source Code, Market Code
	 *********************************/
	/*
	 * WSClient.setData("{VAR_RATEPLANCODE}",
	 * OperaPropConfig.getDataSetForCode("RateCode", "DS_02"));
	 * WSClient.setData("{VAR_ROOMTYPE}",OperaPropConfig.getDataSetForCode(
	 * "RoomType", "DS_02" )); WSClient.setData("{var_RoomType}",
	 * OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
	 * WSClient.setData("{var_sourceCode}",
	 * OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	 * WSClient.setData("{VAR_MARKETCODE}",
	 * OperaPropConfig.getDataSetForCode("MarketCode", "DS_01")); //
	 * WSClient.setData("{var_roomNumber}", //
	 * WSClient.getDataSetForCode("Rooms", "DS_02"));
	 *
	 *//************
	 * Prerequisite 1: Create profile
	 *********************************/
	/*
	 *
	 * profileID=CreateProfile.createProfile("DS_01");
	 *
	 * WSClient.setData("{var_profileId}", profileID);
	 *
	 *//*******************
	 * Prerequisite 2:Create a Reservation
	 ************************/
	/*
	 *
	 * resvID=CreateReservation.createReservation("DS_01");
	 *
	 * WSClient.setData("{var_resvId}", resvID.get("reservationId")); if (resvID
	 * != null) {
	 *
	 * WSClient.setData("{var_CreateRoom}", roomnum);
	 *
	 * WSClient.setData("{var_RoomType}",
	 * OperaPropConfig.getDataSetForCode("RoomType", "DS_02")); String
	 * createRoomReq = WSClient.createSOAPMessage("CreateRoom", "DS_04"); String
	 * createRoomResponseXML = WSClient.processSOAPMessage(createRoomReq); if
	 * (WSAssert.assertIfElementExists(createRoomResponseXML,
	 * "CreateRoomRS_Success", false) == false) {
	 * WSClient.writeToReport(LogStatus.WARNING,
	 * "Prerequisite blocked --->CreateRoom fails");
	 *
	 * } String roomNumber = WSClient.getElementValue(createRoomReq,
	 * "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
	 *
	 * WSClient.setData("{var_roomNumber}", roomNumber);
	 *//**********
	 * Prerequisite 5: Changing the room status to inspected to assign the
	 * room for checking in
	 ***********/
	/*
	 * String setHousekeepingRoomStatusReq =
	 * WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01"); String
	 * setHousekeepingRoomStatusRes =
	 * WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);
	 *
	 * if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
	 * "SetHousekeepingRoomStatusRS_Success", true)) {
	 *//***********
	 * /****************** Assigning a Room
	 ************************//*
	 * String fetchResvReq =
	 * WSClient.createSOAPMessage(
	 * "OWSResvAdvAssignRoom", "DS_02"); String
	 * fetchResvRes =
	 * WSClient.processSOAPMessage(fetchResvReq);
	 * if(WSAssert.assertIfElementExists(
	 * fetchResvRes,"AssignRoomResponse_faultcode",
	 * true)){
	 *
	 * String
	 * message=WSClient.getElementValue(fetchResvRes
	 * ,"AssignRoomResponse_faultstring",
	 * XMLType.RESPONSE);
	 * WSClient.writeToReport(LogStatus.FAIL,
	 * message); } else{ if
	 * (WSAssert.assertIfElementValueEquals(
	 * fetchResvRes,
	 * "AssignRoomAdvResponse_Result_resultStatusFlag"
	 * ,"SUCCESS", false)) { if
	 * (WSAssert.assertIfElementExists(fetchResvRes,
	 * "AssignRoomAdvResponse_RoomNoAssigned",
	 * false)) { String
	 * query=WSClient.getQuery("QS_02");
	 * LinkedHashMap<String, String> assignRoom =
	 * WSClient.getDBRow(query); roomNumber =
	 * WSClient.getElementValue(fetchResvRes,
	 * "AssignRoomAdvResponse_RoomNoAssigned",
	 * XMLType.RESPONSE); if
	 * (WSAssert.assertEquals(roomNumber,
	 * assignRoom.get("ROOMNUMBER"), true))
	 * WSClient.writeToReport(LogStatus.PASS,
	 * "Values for Room Number Expected: " +
	 * roomNumber + " Actual: " +
	 * assignRoom.get("ROOMNUMBER")); else
	 * WSClient.writeToReport(LogStatus.FAIL,
	 * "Values for Room Number Expected: " +
	 * roomNumber + " Actual: " +
	 * assignRoom.get("ROOMNUMBER")); String resvID
	 * = WSClient.getElementValue(fetchResvReq,
	 * "AssignRoomAdvRequest_ResvNameId",
	 * XMLType.REQUEST); if
	 * (WSAssert.assertEquals(resvID,
	 * assignRoom.get("RESERVATIONID"), true))
	 * WSClient.writeToReport(LogStatus.PASS,
	 * "Values for Reservation ID Expected: " +
	 * resvID + " Actual: " +
	 * assignRoom.get("RESERVATIONID")); else
	 * WSClient.writeToReport(LogStatus.FAIL,
	 * "Values for Reservation ID Expected: " +
	 * resvID + " Actual: " +
	 * assignRoom.get("RESERVATIONID"));
	 *
	 *
	 * }}} } }else {
	 * WSClient.writeToReport(LogStatus.WARNING,
	 * "Prerequisite failed >> Reservation ID is not created"
	 * ); } } else {
	 * WSClient.writeToReport(LogStatus.WARNING,
	 * "Prerequisite failed >> Reservation is not created"
	 * ); }
	 *
	 *
	 * } catch (Exception e) {
	 * WSClient.writeToReport(LogStatus.ERROR,
	 * "Exception occured in test due to:" + e); }
	 * finally { // logger.endExtentTest(); } }
	 */

	/***
	 * Minimum Regression: 2 Checking if an error is populated if an Out Of
	 * Order room is assigned to a reservation
	 */
	@Test(groups = { "minimumRegression", "AssignRoomResvAdvanced", "OWS" })

	public void assignRoomResvAdvanced_26319() {
		try {
			String testName = "assignRoomResvAdvanced_26319";
			WSClient.startTest(testName, "Verify that assigning of an out of order room is unsuccessful",
					"minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				WSClient.setData("{var_chain}", OPERALib.getChain());

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
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");

				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				resvID = CreateReservation.createReservation("DS_01");
				if (!resvID.equals("error")) {
					WSClient.setData("{var_resvId}", resvID.get("reservationId"));
					if (resvID != null) {
						/******************
						 * Prerequisite 3: Setting an Out of Order room
						 ************************/

						String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");

						String reasonCode = OperaPropConfig.getDataSetForCode("OOOSReason", "DS_01");
						WSClient.setData("{var_ReasonCode}", reasonCode);
						if (room_num.equals("error")) {
							room_num = CreateRoom.createRoom("RoomMaint");
						}
						if (!room_num.equals("error")) {
							WSClient.setData("{var_OORoom}", room_num);
							WSClient.setData("{var_Resort}", OPERALib.getResort());
							WSClient.setData("{var_RoomNo}", room_num);
							WSClient.setData("{var_roomNumber}", room_num);

							WSClient.writeToReport(LogStatus.INFO, "<b> Room Status is updated to Out OF Order </b>");

							String req_createOORoom = WSClient.createSOAPMessage("SetRoomOutOfOrder", "DS_01");
							String res_createOORoom = WSClient.processSOAPMessage(req_createOORoom);
							String query = WSClient.getQuery("HTNGExtUpdateRoomStatus", "QS_01");
							HashMap<String, String> roomRecord = WSClient.getDBRow(query);
							if (WSAssert.assertEquals("OutOfOrder", roomRecord.get("ROOM_STATUS"), false)) {

								/******************
								 * Assigning a Room
								 ************************/
								String fetchResvReq = WSClient.createSOAPMessage("OWSResvAdvAssignRoom", "DS_01");
								String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
								if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode",
										true)) {

									String message = WSClient.getElementValue(fetchResvRes,
											"AssignRoomResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, message);
								} else {
									if (WSAssert.assertIfElementValueEquals(fetchResvRes,
											"AssignRoomAdvResponse_Result_resultStatusFlag", "SUCCESS", true)) {
										WSClient.writeToReport(LogStatus.FAIL,
												"An Error is not displayed as there is no reservation associated");

									}
									if (WSAssert.assertIfElementValueEquals(fetchResvRes,
											"AssignRoomAdvResponse_Result_resultStatusFlag", "FAIL", false)) {
										String err = WSClient.getElementValue(fetchResvRes,
												"AssignRoomResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.PASS,
												"An Error is displayed as the room given is Out Of Order");
										WSClient.writeToReport(LogStatus.PASS, "Error message displayed is--> " + err);

									}
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisite failed >> Room Status is not updated");
							}
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Profile ID is not created");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	/***
	 * Minimum Regression: 2 Checking if an error is populated if an Out Of
	 * Service room is assigned to a reservation
	 */

	@Test(groups = { "minimumRegression", "AssignRoomResvAdvanced", "OWS" })

	public void assignRoomResvAdvanced_26320() {
		try {
			String testName = "assignRoomResvAdvanced_26320";
			WSClient.startTest(testName, "Verify that assigning of an out of service room is unsuccessful",
					"minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();
				String roomnum = WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")
						+ WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}");
				WSClient.setData("{var_roomNum}", roomnum);
				OPERALib.setOperaHeader(OPERALib.getUserName());
				String resort = OPERALib.getResort();

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resort, interfaceName));
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
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
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
				//
				//
				// /******************
				// * Prerequisite 2:Create a Reservation
				// ************************/
				// String createResvReq =
				// WSClient.createSOAPMessage("CreateReservation", "DS_01");
				// String createResvRes =
				// WSClient.processSOAPMessage(createResvReq);
				//
				// if (WSAssert.assertIfElementExists(createResvRes,
				// "CreateReservationRS_Success", true)) {
				//
				// String reservationId =
				// WSClient.getElementValue(createResvRes,
				// "Reservation_ReservationIDList_UniqueID_ID",
				// XMLType.RESPONSE);
				//
				// WSClient.setData("{var_resvId}", reservationId);
				// System.out.println(reservationId);
				//
				// if (reservationId != null && reservationId != "") {

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_01");
					WSClient.setData("{var_resvId}", resvID.get("reservationId"));
					if (resvID != null) {
						/******************
						 * Prerequisite 3: Setting an OutOfService room
						 ************************/

						String room_num = FetchHotelRooms.fetchHotelRooms("DS_15");

						String reasonCode = OperaPropConfig.getDataSetForCode("OOOSReason", "DS_01");
						WSClient.setData("{var_ReasonCode}", reasonCode);
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

								WSClient.setData("{var_roomNumber}", room_num);

								/******************
								 * Assigning a Room
								 ************************/
								String fetchResvReq = WSClient.createSOAPMessage("OWSResvAdvAssignRoom", "DS_01");
								String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
								if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode",
										true)) {

									String message = WSClient.getElementValue(fetchResvRes,
											"AssignRoomResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, message);
								} else {
									if (WSAssert.assertIfElementValueEquals(fetchResvRes,
											"AssignRoomAdvResponse_Result_resultStatusFlag", "SUCCESS", true)) {
										WSClient.writeToReport(LogStatus.FAIL,
												"An Error is not displayed even if the room is Out of Service");

									}
									if (WSAssert.assertIfElementValueEquals(fetchResvRes,
											"AssignRoomAdvResponse_Result_resultStatusFlag", "FAIL", false)) {
										String err = WSClient.getElementValue(fetchResvRes,
												"AssignRoomResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.PASS,
												"An Error is displayed as the room given is Out Of Service");
										WSClient.writeToReport(LogStatus.PASS, "Error message displayed is--> " + err);

									}
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisite failed >> Out Of Service is not updated");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Room is not created");
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
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

	/***
	 * MR 4 Assigning a room when there is no reservation ID associated
	 * Verifying if an error is populated
	 */
	@Test(groups = { "minimumRegression", "AssignRoomResvAdvanced", "OWS" })

	public void assignRoom_26321() {
		try {
			String testName = "assignRoomResvAdvanced_26321";
			WSClient.startTest(testName, "Verify that assigning room to a missing reservation is unsuccessful",
					"minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				WSClient.setData("{var_chain}", OPERALib.getChain());
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
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				String roomnum = WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")
						+ WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}");
				WSClient.setData("{var_roomNum}", roomnum);
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
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
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

				profileID = CreateProfile.createProfile("DS_01");

				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_resvId}", resvID.get("reservationId"));
				if (resvID != null) {

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

					WSClient.setData("{var_roomNumber}", roomnum);

					/******************
					 * Assigning a Room
					 ************************/
					String fetchResvReq = WSClient.createSOAPMessage("OWSResvAdvAssignRoom", "DS_03");
					String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
					if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode", true)) {

						String message = WSClient.getElementValue(fetchResvRes, "AssignRoomResponse_faultstring",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, message);
					} else {
						if (WSAssert.assertIfElementValueEquals(fetchResvRes,
								"AssignRoomAdvResponse_Result_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"An Error is not displayed as there is no reservation associated");

						}
						if (WSAssert.assertIfElementValueEquals(fetchResvRes,
								"AssignRoomAdvResponse_Result_resultStatusFlag", "FAIL", false)) {
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

	@Test(groups = { "minimumRegression", "AssignRoomResvAdvanced", "OWS" })

	public void assignRoom_26322() {
		try {
			String testName = "assignRoomResvAdvanced_26322";
			WSClient.startTest(testName,
					"Verify that assigning room is unsuccessful when a checked in room is being assigned",
					"minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				WSClient.setData("{var_chain}", OPERALib.getChain());
				String roomnum = WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}")
						+ WSClient.getKeywordData("{KEYWORD_ROOM_NUMBER}");
				WSClient.setData("{var_roomNum}", roomnum);
				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();
				String roomNumber = "";
				String reservation;
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
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
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
				/*****
				 * Prerequisite 2 Create Reservation
				 *********/

				profileID = CreateProfile.createProfile("DS_01");
				WSClient.setData("{var_profileId}", profileID);
				String createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_12");
				String createResvRes = WSClient.processSOAPMessage(createResvReq);

				if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success", true)) {
					String reservationId = WSClient.getElementValue(createResvRes,
							"Reservation_ReservationIDList_UniqueID_ID", XMLType.RESPONSE);

					WSClient.setData("{var_resvId}", reservationId);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					/**********
					 * Prerequisite 3: Fetching available Hotel rooms with room
					 * type
					 ***********/

					/***********
					 * Prerequisite 4: Creating a room to assign
					 ********/
					String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
					String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

					if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

						roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber",
								XMLType.REQUEST);

					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
					}
				}

				WSClient.setData("{var_roomNumber}", roomNumber);
				/**********
				 * Prerequisite 5: Changing the room status to inspected to
				 * assign the room for checking in
				 ***********/
				String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
				String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

				if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success",
						true)) {
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

						if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {

							/******************
							 * Assigning a Room
							 ************************/

							String fetchResvReq = WSClient.createSOAPMessage("OWSResvAdvAssignRoom", "DS_01");
							String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
							if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode", true)) {

								String message = WSClient.getElementValue(fetchResvRes,
										"AssignRoomResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL, message);
							} else {
								if (WSAssert.assertIfElementValueEquals(fetchResvRes,
										"AssignRoomAdvResponse_Result_resultStatusFlag", "SUCCESS", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"An Error is not displayed as there is no reservation associated");

								}
								if (WSAssert.assertIfElementValueEquals(fetchResvRes,
										"AssignRoomAdvResponse_Result_resultStatusFlag", "FAIL", false)) {
									String err = WSClient.getElementValue(fetchResvRes,
											"AssignRoomResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.PASS, "An Error is displayed.");
									WSClient.writeToReport(LogStatus.PASS, "Error message displayed is--> " + err);

								}
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to checkin reservation");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to assign room");
					}
				}

				else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Blocked : Unable to change the status of room to vacant and inspected");
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to do reservation");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	/*
	 * Cancel a reservation and assign a room

	@Test(groups = { "minimumRegression", "AssignRoomResvAdvanced", "OWS" })

	public void assignRoom_26331() {
		try {

			String testName = "assignRoomResvAdvanced_26331";
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

	 *//************
	 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
	 * Code
	 *********************************//*
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_roomNumber}",
				// WSClient.getDataSetForCode("Rooms", "DS_02"));

	  *//************
	  * Prerequisite 1: Create profile
	  *********************************//*
				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

	   *//*******************
	   * Prerequisite 2:Create a Reservation
	   ************************//*

					resvID = CreateReservation.createReservation("DS_05");
					if (!resvID.equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						if (CancelReservation.cancelReservation("DS_02")) {
							WSClient.writeToLog("Reservation cancellation successful");
							WSClient.writeToReport(LogStatus.INFO, "<b>Cancellation Successful</b>");

						} else
							WSClient.writeToLog("Reservation cancellation failed");
	    *//******************
	    * Prerequisite 4: Assigning a Room
	    ************************//*
						String fetchResvReq = WSClient.createSOAPMessage("OWSResvAdvAssignRoom", "DS_02");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode", true)) {

							String message = WSClient.getElementValue(fetchResvRes, "AssignRoomResponse_faultstring",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL, message);
						} else {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes,
									"AssignRoomAdvResponse_Result_resultStatusFlag", "FAIL", false)) {
								WSClient.writeToReport(LogStatus.PASS,
										"******Errored Response when a cancelled reservation is being assigned a room*****");

							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"******No Errored Response when a cancelled reservation is being assigned a room*****");

						}

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
	}*/

	/*
	 *
	 * Assigning a Pseudo Room
	 */

	@Test(groups = { "minimumRegression", "AssignRoomResvAdvanced", "OWS" })

	public void assignRoomResvAdvanced_26338() {
		try {
			String testName = "assignRoomResvAdvanced_263321";
			WSClient.startTest(testName, "Verify that assigning a pseudo room is successful", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

				String resortOperaValue = OPERALib.getResort();
				String interfaceName = OWSLib.getChannel();
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
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
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
							String fetchResvReq = WSClient.createSOAPMessage("OWSResvAdvAssignRoom", "DS_01");
							String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
							if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode", true)) {

								String message = WSClient.getElementValue(fetchResvRes,
										"AssignRoomResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL, message);
							} else {
								if (WSAssert.assertIfElementExists(fetchResvRes,
										"AssignRoomAdvResponse_Result_resultStatusFlag", false)) {
									String query = WSClient.getQuery("QS_01");
									LinkedHashMap<String, String> assignRoom = WSClient.getDBRow(query);
									String resvID = WSClient.getElementValue(fetchResvReq,
											"AssignRoomAdvRequest_ResvNameId", XMLType.REQUEST);
									if (WSAssert.assertEquals(resvID, assignRoom.get("RESERVATIONID"), true))
										WSClient.writeToReport(LogStatus.PASS, "Values for Reservation ID Expected: "
												+ resvID + " Actual " + assignRoom.get("RESERVATIONID"));
									else
										WSClient.writeToReport(LogStatus.FAIL, "Values for Reservation ID Expected: "
												+ resvID + " Actual " + assignRoom.get("RESERVATIONID"));
									String roomNumber = WSClient.getElementValue(fetchResvReq,
											"AssignRoomAdvRequest_RoomNoRequested", XMLType.REQUEST);
									if (WSAssert.assertEquals(roomNumber, assignRoom.get("ROOMNUMBER"), true))
										WSClient.writeToReport(LogStatus.PASS, "Values for Room Number Expected: "
												+ roomNumber + " Actual " + assignRoom.get("ROOMNUMBER"));
									else
										WSClient.writeToReport(LogStatus.FAIL, "Values for Room Number Expected: "
												+ roomNumber + " Actual " + assignRoom.get("ROOMNUMBER"));

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
			// logger.endExtentTest();
		}
	}
	/*
	 *
	 * Assigning a Shared Reservation
	 */

	@Test(groups = { "minimumRegression", "AssignRoomResvAdvanced", "OWS" })

	public void assignRoomResvAdvanced_263381() {
		try {
			String testName = "assignRoomResvAdvanced_26329";
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
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
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
										String fetchResvReq = WSClient.createSOAPMessage("OWSResvAdvAssignRoom",
												"DS_01");
										String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
										if (WSAssert.assertIfElementExists(fetchResvRes, "AssignRoomResponse_faultcode",
												true)) {

											String message = WSClient.getElementValue(fetchResvRes,
													"AssignRoomResponse_faultstring", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.FAIL, message);
										} else {
											if (WSAssert.assertIfElementExists(fetchResvRes,
													"AssignRoomAdvResponse_Result_resultStatusFlag", false)) {
												String query = WSClient.getQuery("OWSResvAdvAssignRoom", "QS_01");
												LinkedHashMap<String, String> assignRoom = WSClient.getDBRow(query);
												String resvID = WSClient.getElementValue(fetchResvReq,
														"AssignRoomAdvRequest_ResvNameId", XMLType.REQUEST);
												if (WSAssert.assertEquals(resvID, assignRoom.get("RESERVATIONID"),
														true))
													WSClient.writeToReport(LogStatus.PASS,
															"Values for Reservation ID Expected: " + resvID + " Actual "
																	+ assignRoom.get("RESERVATIONID"));
												else
													WSClient.writeToReport(LogStatus.FAIL,
															"Values for Reservation ID Expected: " + resvID + " Actual "
																	+ assignRoom.get("RESERVATIONID"));
												String roomNumber = WSClient.getElementValue(fetchResvReq,
														"AssignRoomAdvRequest_RoomNoRequested", XMLType.REQUEST);
												if (WSAssert.assertEquals(roomNumber, assignRoom.get("ROOMNUMBER"),
														true))
													WSClient.writeToReport(LogStatus.PASS,
															"Values for Room Number Expected: " + roomNumber
															+ " Actual " + assignRoom.get("ROOMNUMBER"));
												else
													WSClient.writeToReport(LogStatus.FAIL,
															"Values for Room Number Expected: " + roomNumber
															+ " Actual " + assignRoom.get("ROOMNUMBER"));
												WSClient.setData("{var_resvId}", resvID1.get("reservationId"));
												query = WSClient.getQuery("OWSResvAdvAssignRoom", "QS_01");
												assignRoom = WSClient.getDBRow(query);
												if (WSAssert.assertEquals(resvID1.get("reservationId"),
														assignRoom.get("RESERVATIONID"), true))
													WSClient.writeToReport(LogStatus.PASS,
															"Values for Reservation ID Expected: "
																	+ resvID1.get("reservationId") + " Actual "
																	+ assignRoom.get("RESERVATIONID"));
												else
													WSClient.writeToReport(LogStatus.FAIL,
															"Values for Reservation ID Expected: "
																	+ resvID1.get("reservationId") + " Actual "
																	+ assignRoom.get("RESERVATIONID"));
												roomNumber = WSClient.getElementValue(fetchResvReq,
														"AssignRoomAdvRequest_RoomNoRequested", XMLType.REQUEST);
												if (WSAssert.assertEquals(roomNumber, assignRoom.get("ROOMNUMBER"),
														true))
													WSClient.writeToReport(LogStatus.PASS,
															"Values for Room Number Expected: " + roomNumber
															+ " Actual " + assignRoom.get("ROOMNUMBER"));
												else
													WSClient.writeToReport(LogStatus.FAIL,
															"Values for Room Number Expected: " + roomNumber
															+ " Actual " + assignRoom.get("ROOMNUMBER"));

											}
										}
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

	// @Test(groups = { "minimumRegression", "AssignRoom", "OWS"})
	//
	// public void assignRoom_26330() {
	// try {
	// String testName = "assignRoom_26318";
	// WSClient.startTest(testName, "Verify that assigning a component room is
	// successful", "sanity");
	// if (OperaPropConfig
	// .getPropertyConfigResults(new String[] { "RateCode", "RoomType",
	// "SourceCode", "MarketCode" })) {
	//
	// String resortOperaValue = OPERALib.getResort();
	// String interfaceName = OWSLib.getChannel();
	// WSClient.setData("{var_chain}", OPERALib.getChain());
	//
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// String resort = OPERALib.getResort();
	//
	// OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// OPERALib.getResort(),OWSLib.getChannelType(interfaceName),
	// OWSLib.getChannelCarier(resort, interfaceName));
	// //OWSLib.setOWSHeader("IDCUSER3", "WELCOME5", resort,
	// OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort,
	// channel));
	// String resortExtValue = OWSLib.getChannelResort(resortOperaValue,
	// interfaceName);
	//
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	//
	// WSClient.setData("{var_state}",
	// HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName,
	// "COUNTRY_CODE"));
	// WSClient.setData("{var_addressType}",
	// HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName,
	// "ADDRESS_TYPES"));
	// WSClient.setData("{var_phoneType}",
	// HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName,
	// "PHONE_TYPE"));
	// WSClient.setData("{var_profileType}",
	// HTNGLib.getRandomPMSValue(resortOperaValue, interfaceName,
	// "PROFILE_TYPE"));
	//
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	//
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// HTNGLib.getInterfaceFromAddress());
	//
	// /************
	// * Prerequisite : Room type, Rate Plan Code, Source Code, Market
	// * Code
	// *********************************/
	// WSClient.setData("{VAR_RATEPLANCODE}",OperaPropConfig.getDataSetForCode("RateCode",
	// "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",OperaPropConfig.getDataSetForCode("MarketCode",
	// "DS_01"));
	// // WSClient.setData("{var_roomNumber}",
	// // WSClient.getDataSetForCode("Rooms", "DS_02"));
	//
	// /************
	// * Prerequisite 1: Create profile
	// *********************************/
	// WSClient.setData("{var_payment}",
	// OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	//
	// profileID=CreateProfile.createProfile("DS_01");
	// if(!profileID.equals("error"))
	// {
	// WSClient.setData("{var_profileId}", profileID);
	//
	// /******************* Prerequisite 2:Create a Reservation
	// ************************/
	//
	// resvID=CreateReservation.createReservation("DS_01");
	// if(!resvID.equals("error"))
	// {
	// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
	//
	// WSClient.setData("{var_roomNumber}", "B1001");
	// }
	//
	// /******************
	// * Prerequisite 4: Assigning a Room
	// ************************/
	// String fetchResvReq = WSClient.createSOAPMessage("OWSResvAdvAssignRoom",
	// "DS_01");
	// String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
	// if(WSAssert.assertIfElementExists(fetchResvRes,"AssignRoomResponse_faultcode",
	// true)){
	//
	// String
	// message=WSClient.getElementValue(fetchResvRes,"AssignRoomResponse_faultstring",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.FAIL, message);
	// }
	// else{
	// if (WSAssert.assertIfElementExists(fetchResvRes,
	// "AssignRoomAdvResponse_Result_resultStatusFlag", false)) {
	// String query=WSClient.getQuery("QS_01");
	// LinkedHashMap<String, String> assignRoom = WSClient.getDBRow(query);
	// String resvID = WSClient.getElementValue(fetchResvReq,
	// "AssignRoomRequest_ResvNameId", XMLType.REQUEST);
	// if (WSAssert.assertEquals(resvID, assignRoom.get("RESERVATIONID"), true))
	// WSClient.writeToReport(LogStatus.PASS, "Values for Reservation ID
	// Expected: "
	// + resvID + " Actual " + assignRoom.get("RESERVATIONID"));
	// else
	// WSClient.writeToReport(LogStatus.FAIL, "Values for Reservation ID
	// Expected: "
	// + resvID + " Actual " + assignRoom.get("RESERVATIONID"));
	// String roomNumber = WSClient.getElementValue(fetchResvReq,
	// "AssignRoomRequest_RoomNoRequested", XMLType.REQUEST);
	// if (WSAssert.assertEquals(roomNumber, assignRoom.get("ROOMNUMBER"),
	// true))
	// WSClient.writeToReport(LogStatus.PASS, "Values for Room Number Expected:
	// "
	// + roomNumber + " Actual " + assignRoom.get("ROOMNUMBER"));
	// else
	// WSClient.writeToReport(LogStatus.FAIL, "Values for Room Number Expected:
	// "
	// + roomNumber + " Actual " + assignRoom.get("ROOMNUMBER"));
	//
	// }
	// } }else {
	// WSClient.writeToReport(LogStatus.WARNING,
	// "Prerequisite failed >> Reservation ID is not created");
	// }
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING,
	// "Prerequisite failed >> Reservation is not created");
	//
	// }
	// }catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// // logger.endExtentTest();
	// }
	// }

}
