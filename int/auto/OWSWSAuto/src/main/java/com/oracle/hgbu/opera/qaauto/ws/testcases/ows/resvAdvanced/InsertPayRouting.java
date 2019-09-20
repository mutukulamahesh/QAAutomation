
package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
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

public class InsertPayRouting extends WSSetUp {

	String profileID1 = "", profileID2 = "";
	HashMap<String, String> resvID1 = new HashMap<>();
	HashMap<String, String> resvID2 = new HashMap<>();

	// @Test(groups = { "minimumRegression", "InsertPayRouting",
	// "ResvAdvanced","OWS"})
	// public void insertPayRouting_60011() throws Exception {
	//
	//
	// try {
	//
	// String testName = "insertPayRouting_60011";
	// WSClient.startTest(testName, "Verify that the reservation is routed to
	// another profile with channel where channel and carrier name are
	// different", "minimumRegression");
	// if (OperaPropConfig
	// .getPropertyConfigResults(new String[] {"TransactionCode"})) {
	// String resortOperaValue = OPERALib.getResort();
	// String channel = OWSLib.getChannel(3);
	//
	// String resortExtValue =
	// OWSLib.getChannelResort(resortOperaValue,channel);
	// String pwd = OPERALib.getPassword();
	// String channelType = OWSLib.getChannelType(channel);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	// channel);
	// String uname = OPERALib.getUserName();
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}",OWSLib.getChannelResort(resortOperaValue,channel));
	//
	//
	// OPERALib.setOperaHeader(OPERALib.getUserName());
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
	// WSClient.writeToReport(LogStatus.INFO,"<b>Creating 2 reservations-One For
	// Payee and the other for Guest</b>");
	// /************
	// * Prerequisite 1: Create profile
	// *********************************/
	// if(profileID1.equals(""))
	// profileID1=CreateProfile.createProfile("DS_01");
	// if(!profileID1.equals("error"))
	// {
	// WSClient.setData("{var_profileId}", profileID1);
	//
	// /******************* Prerequisite 2:Create a Reservation
	// ************************/
	//
	//
	// resvID1=CreateReservation.createReservation("DS_01");
	// if(!resvID1.equals("error"))
	// {
	//
	// WSClient.setData("{var_resvId}", resvID1.get("reservationId"));
	//
	// /************
	// * Prerequisite 1: Create profile
	// *********************************/
	// if(profileID2.equals(""))
	// profileID2=CreateProfile.createProfile("DS_01");
	// if(!profileID2.equals("error"))
	// {
	//
	// WSClient.setData("{var_profileId2}", profileID2);
	// /******************* Prerequisite 2:Create a Reservation
	// ************************/
	//
	//
	// resvID2=CreateReservation.createReservation("DS_01");
	// if(!resvID2.containsValue("error"))
	// {
	//
	// WSClient.setData("{var_resvId2}", resvID2.get("reservationId"));
	//
	// WSClient.setData("{var_instCode}",
	// OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
	//
	//
	// OWSLib.setOWSHeader(uname, pwd,resortOperaValue, channelType,
	// channelCarrier);
	//
	//
	// /******************* OWS Insert Pay Routing
	// Operation************************/
	// String InsertRoutingReq =
	// WSClient.createSOAPMessage("OWSInsertPayRouting", "DS_01");
	// String InsertRoutingRes = WSClient.processSOAPMessage(InsertRoutingReq);
	// if(WSAssert.assertIfElementValueEquals(InsertRoutingRes,
	// "InsertPayRoutingResponse_Result_resultStatusFlag","SUCCESS", false)){
	//
	// LinkedHashMap<String,String> expectedValues = new
	// LinkedHashMap<String,String>();
	// LinkedHashMap<String, String> actualValues = new
	// LinkedHashMap<String,String>();
	// HashMap<String,String> xpath=new HashMap<String,String>();
	// xpath.put("ReservationRequest_ReservationID_UniqueID","InsertPayRoutingRequest_ReservationRequest_ReservationID");
	// xpath.put("InsertPayRoutingRequest_PayRoutings_Window","InsertPayRoutingRequest_PayRoutings");
	// xpath.put("InsertPayRoutingRequest_PayRoutings_RoutingType","InsertPayRoutingRequest_PayRoutings");
	// xpath.put("InsertPayRoutingRequest_PayRoutings_BillToNameID","InsertPayRoutingRequest_PayRoutings");
	// xpath.put("RoutingCodes_RoutingInstructionCode_InstructionCode","PayRoutings_RoutingCodes_RoutingInstructionCode");
	// expectedValues=WSClient.getSingleNodeList(InsertRoutingReq, xpath, false,
	// XMLType.REQUEST);
	// actualValues=WSClient.getDBRow(WSClient.getQuery("QS_01"));
	// WSAssert.assertEquals(expectedValues, actualValues,false);
	//
	// }
	//
	//
	//
	//
	//
	// }
	//
	//
	// }
	//
	//
	//
	//
	// }
	//
	//
	// }
	// }
	//
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// WSClient.setData("{var_cancelReasonCode}",
	// OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
	// if (!resvID1.containsValue("error") || !resvID1.isEmpty())
	// CancelReservation.cancelReservation("DS_02");
	//
	// }
	//
	// }

	@Test(groups = { "sanity", "InsertPayRouting", "ResvAdvanced", "OWS" })
	public void insertPayRouting_39540() throws Exception {

		try {

			String testName = "insertPayRouting_27181";
			WSClient.startTest(testName, "Verify that the reservation is routed to another profile", "sanity");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();

				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, channel);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resortOperaValue, channel));

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				WSClient.writeToReport(LogStatus.INFO,
						"<b>Creating 2 reservations-One For Payee and the other for Guest</b>");
				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID1.equals(""))
					profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {
					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID1 = CreateReservation.createReservation("DS_01");
					if (!resvID1.equals("error")) {

						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

						/************
						 * Prerequisite 1: Create profile
						 *********************************/
						if (profileID2.equals(""))
							profileID2 = CreateProfile.createProfile("DS_01");
						if (!profileID2.equals("error")) {

							WSClient.setData("{var_profileId2}", profileID2);
							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							resvID2 = CreateReservation.createReservation("DS_01");
							if (!resvID2.containsValue("error")) {

								WSClient.setData("{var_resvId2}", resvID2.get("reservationId"));

								WSClient.setData("{var_instCode}",
										OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));

								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

								/*******************
								 * OWS Insert Pay Routing Operation
								 ************************/
								String InsertRoutingReq = WSClient.createSOAPMessage("OWSInsertPayRouting", "DS_01");
								String InsertRoutingRes = WSClient.processSOAPMessage(InsertRoutingReq);
								if (WSAssert.assertIfElementValueEquals(InsertRoutingRes,
										"InsertPayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
									HashMap<String, String> xpath = new HashMap<String, String>();
									xpath.put("ReservationRequest_ReservationID_UniqueID",
											"InsertPayRoutingRequest_ReservationRequest_ReservationID");
									xpath.put("InsertPayRoutingRequest_PayRoutings_Window",
											"InsertPayRoutingRequest_PayRoutings");
									xpath.put("InsertPayRoutingRequest_PayRoutings_RoutingType",
											"InsertPayRoutingRequest_PayRoutings");
									xpath.put("InsertPayRoutingRequest_PayRoutings_BillToNameID",
											"InsertPayRoutingRequest_PayRoutings");
									xpath.put("RoutingCodes_RoutingInstructionCode_InstructionCode",
											"PayRoutings_RoutingCodes_RoutingInstructionCode");
									expectedValues = WSClient.getSingleNodeList(InsertRoutingReq, xpath, false,
											XMLType.REQUEST);
									actualValues = WSClient.getDBRow(WSClient.getQuery("QS_01"));
									WSAssert.assertEquals(expectedValues, actualValues, false);

								}

							}

						}

					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID1.containsValue("error") || !resvID1.isEmpty())
				CancelReservation.cancelReservation("DS_02");

		}

	}

	@Test(groups = { "minimumRegression", "InsertPayRouting", "OWS", "ResvAdvanced" })
	public void insertPayRouting_40103() {

		try {

			String testName = "insertPayRouting_40103";
			WSClient.startTest(testName, "Verify that the  reservation is routed when routing type is room",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();
				String roomNumber = " ";

				WSClient.setData("{var_resort}", resortOperaValue);
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resortOperaValue, channel));
				OPERALib.setOperaHeader(OPERALib.getUserName());

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
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.writeToReport(LogStatus.INFO,
						"<b>Creating 2 reservations-One For Payee and the other for Guest</b>");
				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID1.equals(""))
					profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {
					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID1 = CreateReservation.createReservation("DS_01");
					if (!resvID1.containsValue("error")) {

						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

						/************
						 * Prerequisite 1: Create profile
						 *********************************/
						if (profileID2.equals(""))
							profileID2 = CreateProfile.createProfile("DS_01");
						if (!profileID2.equals("error")) {

							WSClient.setData("{var_profileId2}", profileID2);
							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							resvID2 = CreateReservation.createReservation("DS_12");
							if (!resvID2.equals("error")) {

								WSClient.setData("{var_resvId2}", resvID2.get("reservationId"));
								WSClient.setData("{var_resvId}", resvID2.get("reservationId"));
								WSClient.setData("{var_profileId}", profileID2);

								/**********
								 * Prerequisite 3: Fetching available Hotel
								 * rooms with room type
								 ***********/

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes,
										"FetchHotelRoomsRS_HotelRooms_Room", true)) {

									roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
											"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

								}

								else {
									/***********
									 * Prerequisite 4: Creating a room to assign
									 ********/
									String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
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
												true))

										{

											WSClient.setData("{var_instCode}",
													OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));

											OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
													channelCarrier);

											/*******************
											 * OWS Insert Pay Routing Operation
											 ************************/
											WSClient.setData("{var_resvId}", resvID1.get("reservationId"));
											String InsertRoutingReq = WSClient.createSOAPMessage("OWSInsertPayRouting",
													"DS_02");
											String InsertRoutingRes = WSClient.processSOAPMessage(InsertRoutingReq);
											if (WSAssert.assertIfElementValueEquals(InsertRoutingRes,
													"InsertPayRoutingResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {

												LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
												LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
												HashMap<String, String> xpath = new HashMap<String, String>();

												xpath.put("ReservationRequest_ReservationID_UniqueID",
														"InsertPayRoutingRequest_ReservationRequest_ReservationID");
												xpath.put("InsertPayRoutingRequest_PayRoutings_RoutingType",
														"InsertPayRoutingRequest_PayRoutings");
												xpath.put("InsertPayRoutingRequest_PayRoutings_ToResvNameID",
														"InsertPayRoutingRequest_PayRoutings");
												xpath.put("RoutingCodes_RoutingInstructionCode_InstructionCode",
														"PayRoutings_RoutingCodes_RoutingInstructionCode");
												expectedValues = WSClient.getSingleNodeList(InsertRoutingReq, xpath,
														false, XMLType.REQUEST);
												actualValues = WSClient.getDBRow(WSClient.getQuery("QS_02"));
												WSAssert.assertEquals(expectedValues, actualValues, false);

											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Pre_requisite1 check-in is unsuccessful,unable to perform ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING,
												"Pre_requisite-Assign Room is unsuccessful,unable to perform ");
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING,
											"Pre_requisite- SetHousekeepingRoom is unsuccessful,unable to perform ");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Pre_requisite- FetchHotelRoom is unsuccessful,unable to perform ");
							}
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

	@Test(groups = { "minimumRegression", "InsertPayRouting", "OWS", "ResvAdvanced" })

	public void insertPayRouting_40144() {

		try {

			String testName = "insertPayRouting_40144";
			WSClient.startTest(testName, "Verify that the  reservation is routed with multiple transaction-codes",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.writeToReport(LogStatus.INFO,
						"<b>Creating 2 reservations-One For Payee and the other for Guest</b>");

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID1.equals(""))
					profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {
					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					// if(resvID1.equals(""))
					resvID1 = CreateReservation.createReservation("DS_01");

					if (!resvID1.containsValue("error")) {

						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

						/************
						 * Prerequisite 1: Create profile
						 *********************************/
						if (profileID2.equals(""))
							profileID2 = CreateProfile.createProfile("DS_01");
						if (!profileID2.equals("error")) {

							WSClient.setData("{var_profileId2}", profileID2);
							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							resvID2 = CreateReservation.createReservation("DS_01");
							if (!resvID2.containsValue("error")) {

								WSClient.setData("{var_resvId2}", resvID2.get("reservationId"));

								WSClient.setData("{var_instCode}",
										OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
								WSClient.setData("{var_instCode1}",
										OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));
								String resortExtValue = OWSLib.getChannelResort(resortOperaValue, channel);
								WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resortOperaValue, channel));
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

								/*******************
								 * OWS Insert Pay Routing Operation
								 ************************/
								String InsertRoutingReq = WSClient.createSOAPMessage("OWSInsertPayRouting", "DS_03");
								String InsertRoutingRes = WSClient.processSOAPMessage(InsertRoutingReq);
								if (WSAssert.assertIfElementValueEquals(InsertRoutingRes,
										"InsertPayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									HashMap<String, String> xpath = new HashMap<String, String>();
									xpath.put("RoutingCodes_RoutingInstructionCode_InstructionCode",
											"PayRoutings_RoutingCodes_RoutingInstructionCode");
									List<LinkedHashMap<String, String>> expectedValues = WSClient
											.getMultipleNodeList(InsertRoutingReq, xpath, false, XMLType.REQUEST);
									List<LinkedHashMap<String, String>> actualValues = WSClient
											.getDBRows(WSClient.getQuery("QS_03"));
									WSAssert.assertEquals(actualValues, expectedValues, false);

								}

							}

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

	@Test(groups = { "minimumRegression", "InsertPayRouting", "ResvAdvanced", "OWS" })

	public void insertPayRouting_40180() {

		try {

			String testName = "insertPayRouting_40180";
			WSClient.startTest(testName, "Verify that the  reservation is routed when keytrack is passed",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {
				String resortOperaValue = OPERALib.getResort();

				String interfaceName = OWSLib.getChannel();
				String channel = OWSLib.getChannel();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();
				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

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

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID1.equals(""))
					profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {
					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID1 = CreateReservation.createReservation("DS_01");
					WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

					if (!resvID1.containsValue("error")) {

						WSClient.setData("{var_keyTrack2}", "21140000000000000005");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String req_setKeyData = WSClient.createSOAPMessage("OWSSetKeyData", "DS_01");
						String res_setKeyData = WSClient.processSOAPMessage(req_setKeyData);
						if (WSAssert.assertIfElementExists(res_setKeyData, "SetKeyDataResponse_Result", true)) {

							/************
							 * Prerequisite 1: Create profile
							 *********************************/
							if (profileID2.equals(""))
								profileID2 = CreateProfile.createProfile("DS_01");
							if (!profileID2.equals("error")) {

								WSClient.setData("{var_profileId2}", profileID2);
								/*******************
								 * Prerequisite 2:Create a Reservation
								 ************************/

								WSClient.setData("{var_instCode}",
										OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));

								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

								/*******************
								 * OWS Insert Pay Routing Operation
								 ************************/
								String InsertRoutingReq = WSClient.createSOAPMessage("OWSInsertPayRouting", "DS_05");
								String InsertRoutingRes = WSClient.processSOAPMessage(InsertRoutingReq);
								if (WSAssert.assertIfElementValueEquals(InsertRoutingRes,
										"InsertPayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
									HashMap<String, String> xpath = new HashMap<String, String>();

									xpath.put("InsertPayRoutingRequest_ReservationRequest_KeyTrack_Key2Track",
											"InsertPayRoutingRequest_ReservationRequest_KeyTrack");
									xpath.put("ReservationRequest_ReservationID_UniqueID",
											"InsertPayRoutingRequest_ReservationRequest_ReservationID");
									xpath.put("InsertPayRoutingRequest_PayRoutings_Window",
											"InsertPayRoutingRequest_PayRoutings");
									xpath.put("InsertPayRoutingRequest_PayRoutings_RoutingType",
											"InsertPayRoutingRequest_PayRoutings");
									xpath.put("InsertPayRoutingRequest_PayRoutings_BillToNameID",
											"InsertPayRoutingRequest_PayRoutings");
									xpath.put("RoutingCodes_RoutingInstructionCode_InstructionCode",
											"PayRoutings_RoutingCodes_RoutingInstructionCode");
									expectedValues = WSClient.getSingleNodeList(InsertRoutingReq, xpath, false,
											XMLType.REQUEST);
									actualValues = WSClient.getDBRow(WSClient.getQuery("QS_06"));
									WSAssert.assertEquals(expectedValues, actualValues, false);

								}

							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite SetKeyData Failed");

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

	@Test(groups = { "minimumRegression", "InsertPayRouting", "ResvAdvanced", "OWS" })

	public void insertPayRouting_40182() {

		try {

			String testName = "insertPayRouting_40182";
			WSClient.startTest(testName, "Verify that the error  is displayed when reservation id are not passed",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

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
				if (profileID1.equals(""))
					profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {
					WSClient.setData("{var_profileId}", profileID1);

					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					if (profileID2.equals(""))
						profileID2 = CreateProfile.createProfile("DS_01");
					if (!profileID2.equals("error")) {

						WSClient.setData("{var_profileId2}", profileID2);
						/*******************
						 * Prerequisite 2:Create a Reservation
						 ************************/

						WSClient.setData("{var_instCode}",
								OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));

						String resortExtValue = OWSLib.getChannelResort(resortOperaValue, channel);
						WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resortOperaValue, channel));
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						/*******************
						 * OWS Insert Pay Routing Operation
						 ************************/
						String InsertRoutingReq = WSClient.createSOAPMessage("OWSInsertPayRouting", "DS_06");
						String InsertRoutingRes = WSClient.processSOAPMessage(InsertRoutingReq);
						WSAssert.assertIfElementValueEquals(InsertRoutingRes,
								"InsertPayRoutingResponse_Result_resultStatusFlag", "FAIL", false);
						if (WSAssert.assertIfElementExists(InsertRoutingRes, "Result_Text_TextElement", true)) {

							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error message is:</b>" + "<b>" + WSClient.getElementValue(InsertRoutingRes,
											"Result_Text_TextElement", XMLType.RESPONSE) + "</b>");

						}
						if (WSAssert.assertIfElementExists(InsertRoutingRes, "InsertPayRoutingResponse_Result_GDSError",
								true)) {

							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error message is: </b>" + WSClient.getElementValue(InsertRoutingRes,
											"InsertPayRoutingResponse_Result_GDSError", XMLType.RESPONSE));

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

	@Test(groups = { "minimumRegression", "InsertPayRouting", "ResvAdvanced", "OWS" })
	public void insertPayRouting_40200() {

		try {

			String testName = "insertPayRouting_40200";
			WSClient.startTest(testName,
					"Verify that the Error flag is displayed when empty routing instructions are passed",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

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
				if (profileID1.equals(""))
					profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {
					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID1 = CreateReservation.createReservation("DS_01");
					if (!resvID1.equals("error")) {

						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

						/************
						 * Prerequisite 1: Create profile
						 *********************************/
						// if(profileID2.equals(""))
						profileID2 = CreateProfile.createProfile("DS_01");
						if (!profileID2.equals("error")) {

							WSClient.setData("{var_profileId2}", profileID2);
							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							resvID2 = CreateReservation.createReservation("DS_01");
							if (!resvID2.containsValue("error")) {

								WSClient.setData("{var_resvId2}", resvID2.get("reservationId"));

								WSClient.setData("{var_instCode}",
										OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));

								String resortExtValue = OWSLib.getChannelResort(resortOperaValue, channel);
								WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resortOperaValue, channel));
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

								/*******************
								 * OWS Insert Pay Routing Operation
								 ************************/
								String InsertRoutingReq = WSClient.createSOAPMessage("OWSInsertPayRouting", "DS_08");
								String InsertRoutingRes = WSClient.processSOAPMessage(InsertRoutingReq);
								WSAssert.assertIfElementValueEquals(InsertRoutingRes,
										"InsertPayRoutingResponse_Result_resultStatusFlag", "FAIL", false);
								if (WSAssert.assertIfElementExists(InsertRoutingRes, "Result_Text_TextElement", true)) {

									WSClient.writeToReport(LogStatus.INFO,
											"<b>The error message is:</b>" + "<b>"
													+ WSClient.getElementValue(InsertRoutingRes,
															"Result_Text_TextElement", XMLType.RESPONSE)
													+ "</b>");

								}
								if (WSAssert.assertIfElementExists(InsertRoutingRes,
										"InsertPayRoutingResponse_Result_GDSError", true)) {

									WSClient.writeToReport(LogStatus.INFO,
											"<b>The error message is: </b>" + WSClient.getElementValue(InsertRoutingRes,
													"InsertPayRoutingResponse_Result_GDSError", XMLType.RESPONSE));

								}

							}

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

	@Test(groups = { "minimumRegression", "InsertPayRouting", "ResvAdvanced", "OWS" })
	public void insertPayRouting_40306() throws Exception {

		try {

			String testName = "insertPayRouting_40306";
			WSClient.startTest(testName,
					"Verify that error exists when the cancelled/checkedout reservation is routed to a guest",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();
				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				WSClient.writeToReport(LogStatus.INFO,
						"<b>Creating 2 reservations-One For Payee and the other for Guest</b>");
				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {
					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID1 = CreateReservation.createReservation("DS_01");
					if (!resvID1.equals("error")) {

						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));
						CancelReservation.cancelReservation("DS_02");

						/************
						 * Prerequisite 1: Create profile
						 *********************************/
						if (profileID2.equals(""))
							profileID2 = CreateProfile.createProfile("DS_01");
						if (!profileID2.equals("error")) {

							WSClient.setData("{var_profileId2}", profileID2);
							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							WSClient.setData("{var_instCode}",
									OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));

							String resortExtValue = OWSLib.getChannelResort(resortOperaValue, channel);
							WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resortOperaValue, channel));
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							/*******************
							 * OWS Insert Pay Routing Operation
							 ************************/
							String InsertRoutingReq = WSClient.createSOAPMessage("OWSInsertPayRouting", "DS_01");
							String InsertRoutingRes = WSClient.processSOAPMessage(InsertRoutingReq);
							WSAssert.assertIfElementValueEquals(InsertRoutingRes,
									"InsertPayRoutingResponse_Result_resultStatusFlag", "FAIL", false);
							if (WSAssert.assertIfElementExists(InsertRoutingRes, "Result_Text_TextElement", true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error message is:</b>" + "<b>"
												+ WSClient.getElementValue(InsertRoutingRes, "Result_Text_TextElement",
														XMLType.RESPONSE)
												+ "</b>");

							}
							if (WSAssert.assertIfElementExists(InsertRoutingRes,
									"InsertPayRoutingResponse_Result_GDSError", true)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>The error message is: </b>" + WSClient.getElementValue(InsertRoutingRes,
												"InsertPayRoutingResponse_Result_GDSError", XMLType.RESPONSE));

							}

						}

					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			// if (!resvID1.containsValue("error") || !resvID1.isEmpty())
			// CancelReservation.cancelReservation("DS_02");

		}

	}

	@Test(groups = { "minimumRegression", "InsertPayRouting", "ResvAdvanced", "OWS" })
	public void insertPayRouting_40309() throws Exception {

		try {

			String testName = "insertPayRouting_403009";
			WSClient.startTest(testName, "Verify that Error exixts when invalid window number is given",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();
				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				WSClient.writeToReport(LogStatus.INFO,
						"<b>Creating 2 reservations-One For Payee and the other for Guest</b>");
				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID1.equals(""))
					profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {
					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID1 = CreateReservation.createReservation("DS_01");
					if (!resvID1.equals("error")) {

						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

						/************
						 * Prerequisite 1: Create profile
						 *********************************/
						if (profileID2.equals(""))
							profileID2 = CreateProfile.createProfile("DS_01");
						if (!profileID2.equals("error")) {

							WSClient.setData("{var_profileId2}", profileID2);
							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							resvID2 = CreateReservation.createReservation("DS_01");
							if (!resvID2.containsValue("error")) {

								WSClient.setData("{var_resvId2}", resvID2.get("reservationId"));

								WSClient.setData("{var_instCode}",
										OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));

								String resortExtValue = OWSLib.getChannelResort(resortOperaValue, channel);
								WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resortOperaValue, channel));
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

								/*******************
								 * OWS Insert Pay Routing Operation
								 ************************/
								String InsertRoutingReq = WSClient.createSOAPMessage("OWSInsertPayRouting", "DS_07");
								String InsertRoutingRes = WSClient.processSOAPMessage(InsertRoutingReq);
								WSAssert.assertIfElementValueEquals(InsertRoutingRes,
										"InsertPayRoutingResponse_Result_resultStatusFlag", "FAIL", false);
								if (WSAssert.assertIfElementExists(InsertRoutingRes, "Result_Text_TextElement", true)) {

									WSClient.writeToReport(LogStatus.INFO,
											"<b>The error message is:</b>" + "<b>"
													+ WSClient.getElementValue(InsertRoutingRes,
															"Result_Text_TextElement", XMLType.RESPONSE)
													+ "</b>");

								}
								if (WSAssert.assertIfElementExists(InsertRoutingRes,
										"InsertPayRoutingResponse_Result_GDSError", true)) {

									WSClient.writeToReport(LogStatus.INFO,
											"<b>The error message is: </b>" + WSClient.getElementValue(InsertRoutingRes,
													"InsertPayRoutingResponse_Result_GDSError", XMLType.RESPONSE));

								}

							}

						}

					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID1.containsValue("error") || !resvID1.isEmpty())
				CancelReservation.cancelReservation("DS_02");

		}

	}

	@Test(groups = { "minimumRegression", "InsertPayRouting", "OWS", "ResvAdvanced" })
	public void insertPayRouting_41269() {

		try {

			String testName = "insertPayRouting_41269";
			WSClient.startTest(testName,
					"Verify that the Error occurs if same transaction code is routed to 2 diff reservation",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();
				String roomNumber = " ";

				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

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
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID1.equals(""))
					profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {
					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID1 = CreateReservation.createReservation("DS_01");
					if (!resvID1.containsValue("error")) {

						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

						/************
						 * Prerequisite 1: Create profile
						 *********************************/
						if (profileID2.equals(""))
							profileID2 = CreateProfile.createProfile("DS_01");
						if (!profileID2.equals("error")) {

							WSClient.setData("{var_profileId2}", profileID2);
							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							resvID2 = CreateReservation.createReservation("DS_12");
							if (!resvID2.equals("error")) {

								WSClient.setData("{var_resvId2}", resvID2.get("reservationId"));
								WSClient.setData("{var_resvId}", resvID2.get("reservationId"));
								WSClient.setData("{var_profileId}", profileID2);

								/**********
								 * Prerequisite 3: Fetching available Hotel
								 * rooms with room type
								 ***********/

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes,
										"FetchHotelRoomsRS_HotelRooms_Room", true)) {

									roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
											"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

								}

								else {
									/***********
									 * Prerequisite 4: Creating a room to assign
									 ********/
									String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
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
												true))

										{

											WSClient.setData("{var_instCode}",
													OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));

											String resortExtValue = OWSLib.getChannelResort(resortOperaValue, channel);
											WSClient.setData("{var_extResort}",
													OWSLib.getChannelResort(resortOperaValue, channel));
											OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
													channelCarrier);

											/*******************
											 * OWS Insert Pay Routing Operation
											 ************************/
											WSClient.setData("{var_resvId}", resvID1.get("reservationId"));
											String InsertRoutingReq = WSClient.createSOAPMessage("OWSInsertPayRouting",
													"DS_02");
											String InsertRoutingRes = WSClient.processSOAPMessage(InsertRoutingReq);
											if (WSAssert.assertIfElementValueEquals(InsertRoutingRes,
													"InsertPayRoutingResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {

												profileID2 = CreateProfile.createProfile("DS_01");
												WSClient.setData("{var_profileId2}", profileID2);
												resvID2 = CreateReservation.createReservation("DS_01");
												WSClient.setData("{var_resvId2}", resvID2.get("reservationId"));

												InsertRoutingReq = WSClient.createSOAPMessage("OWSInsertPayRouting",
														"DS_02");
												InsertRoutingRes = WSClient.processSOAPMessage(InsertRoutingReq);
												WSAssert.assertIfElementValueEquals(InsertRoutingRes,
														"InsertPayRoutingResponse_Result_resultStatusFlag", "FAIL",
														false);
												if (WSAssert.assertIfElementExists(InsertRoutingRes,
														"Result_Text_TextElement", true)) {

													WSClient.writeToReport(LogStatus.INFO,
															"<b>The error message is:</b>" + "<b>"
																	+ WSClient.getElementValue(InsertRoutingRes,
																			"Result_Text_TextElement", XMLType.RESPONSE)
																	+ "</b>");

												}
												if (WSAssert.assertIfElementExists(InsertRoutingRes,
														"InsertPayRoutingResponse_Result_GDSError", true)) {

													WSClient.writeToReport(LogStatus.INFO,
															"<b>The error message is: </b>"
																	+ WSClient.getElementValue(InsertRoutingRes,
																			"InsertPayRoutingResponse_Result_GDSError",
																			XMLType.RESPONSE));

												}

											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Pre_requisite1 check-in is unsuccessful,unable to perform ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING,
												"Pre_requisite-Assign Room is unsuccessful,unable to perform ");
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING,
											"Pre_requisite- SetHousekeepingRoom is unsuccessful,unable to perform ");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Pre_requisite- FetchHotelRoom is unsuccessful,unable to perform ");
							}
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

	@Test(groups = { "minimumRegression", "InsertPayRouting", "ResvAdvanced", "OWS" })
	public void insertPayRouting_40314() throws Exception {

		try {

			String testName = "insertPayRouting_40314";
			WSClient.startTest(testName, "Verify that the reservation is routed to  two different folio numbers",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();
				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				WSClient.writeToReport(LogStatus.INFO,
						"<b>Creating 2 reservations-One For Payee and the other for Guest</b>");
				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID1.equals(""))
					profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {
					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID1 = CreateReservation.createReservation("DS_01");
					if (!resvID1.equals("error")) {

						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

						/************
						 * Prerequisite 1: Create profile
						 *********************************/
						if (profileID2.equals(""))
							profileID2 = CreateProfile.createProfile("DS_01");
						if (!profileID2.equals("error")) {

							WSClient.setData("{var_profileId2}", profileID2);
							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							resvID2 = CreateReservation.createReservation("DS_01");

							if (!resvID2.containsValue("error")) {

								WSClient.setData("{var_resvId2}", resvID2.get("reservationId"));

								WSClient.setData("{var_instCode}",
										OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));

								String resortExtValue = OWSLib.getChannelResort(resortOperaValue, channel);
								WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resortOperaValue, channel));
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

								/*******************
								 * OWS Insert Pay Routing Operation
								 ************************/
								String InsertRoutingReq = WSClient.createSOAPMessage("OWSInsertPayRouting", "DS_01");
								String InsertRoutingRes = WSClient.processSOAPMessage(InsertRoutingReq);
								WSClient.setData("{var_window}", "3");
								InsertRoutingReq = WSClient.createSOAPMessage("OWSInsertPayRouting", "DS_09");
								InsertRoutingRes = WSClient.processSOAPMessage(InsertRoutingReq);

								if (WSAssert.assertIfElementValueEquals(InsertRoutingRes,
										"InsertPayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
									HashMap<String, String> xpath = new HashMap<String, String>();
									xpath.put("ReservationRequest_ReservationID_UniqueID",
											"InsertPayRoutingRequest_ReservationRequest_ReservationID");
									xpath.put("InsertPayRoutingRequest_PayRoutings_Window",
											"InsertPayRoutingRequest_PayRoutings");

									xpath.put("InsertPayRoutingRequest_PayRoutings_BillToNameID",
											"InsertPayRoutingRequest_PayRoutings");

									expectedValues = WSClient.getSingleNodeList(InsertRoutingReq, xpath, false,
											XMLType.REQUEST);
									actualValues = WSClient.getDBRow(WSClient.getQuery("QS_07"));
									WSAssert.assertEquals(expectedValues, actualValues, false);

								}

							}

						}

					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			// if (!resvID1.containsValue("error") || !resvID1.isEmpty())
			// CancelReservation.cancelReservation("DS_02");

		}

	}

	@Test(groups = { "minimumRegression", "InsertPayRouting", "ResvAdvanced", "OWS" })
	public void insertPayRouting_41274() throws Exception {

		try {

			String testName = "insertPayRouting_41274";
			WSClient.startTest(testName,
					"Verify that the reservation is routed to another profile when days are given in the request",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {
				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();
				WSClient.setData("{var_resort}", resortOperaValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				WSClient.writeToReport(LogStatus.INFO,
						"<b>Creating 2 reservations-One For Payee and the other for Guest</b>");
				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID1.equals(""))
					profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {
					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID1 = CreateReservation.createReservation("DS_01");
					if (!resvID1.equals("error")) {

						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

						/************
						 * Prerequisite 1: Create profile
						 *********************************/
						if (profileID2.equals(""))
							profileID2 = CreateProfile.createProfile("DS_01");
						if (!profileID2.equals("error")) {

							WSClient.setData("{var_profileId2}", profileID2);
							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							resvID2 = CreateReservation.createReservation("DS_01");
							if (!resvID2.containsValue("error")) {

								WSClient.setData("{var_resvId2}", resvID2.get("reservationId"));

								WSClient.setData("{var_instCode}",
										OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
								String resortExtValue = OWSLib.getChannelResort(resortOperaValue, channel);
								WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resortOperaValue, channel));

								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

								/*******************
								 * OWS Insert Pay Routing Operation
								 ************************/
								String InsertRoutingReq = WSClient.createSOAPMessage("OWSInsertPayRouting", "DS_10");
								String InsertRoutingRes = WSClient.processSOAPMessage(InsertRoutingReq);
								if (WSAssert.assertIfElementValueEquals(InsertRoutingRes,
										"InsertPayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
									HashMap<String, String> xpath = new HashMap<String, String>();
									xpath.put("ReservationRequest_ReservationID_UniqueID",
											"InsertPayRoutingRequest_ReservationRequest_ReservationID");
									xpath.put("InsertPayRoutingRequest_PayRoutings_Window",
											"InsertPayRoutingRequest_PayRoutings");
									xpath.put("InsertPayRoutingRequest_PayRoutings_RoutingType",
											"InsertPayRoutingRequest_PayRoutings");
									xpath.put("InsertPayRoutingRequest_PayRoutings_BillToNameID",
											"InsertPayRoutingRequest_PayRoutings");
									xpath.put("RoutingCodes_RoutingInstructionCode_InstructionCode",
											"PayRoutings_RoutingCodes_RoutingInstructionCode");
									xpath.put("InsertPayRoutingRequest_PayRoutings_Day1",
											"InsertPayRoutingRequest_PayRoutings");
									xpath.put("InsertPayRoutingRequest_PayRoutings_Day2",
											"InsertPayRoutingRequest_PayRoutings");
									xpath.put("InsertPayRoutingRequest_PayRoutings_Day3",
											"InsertPayRoutingRequest_PayRoutings");
									xpath.put("InsertPayRoutingRequest_PayRoutings_Day4",
											"InsertPayRoutingRequest_PayRoutings");
									xpath.put("InsertPayRoutingRequest_PayRoutings_Day5",
											"InsertPayRoutingRequest_PayRoutings");
									xpath.put("InsertPayRoutingRequest_PayRoutings_Day6",
											"InsertPayRoutingRequest_PayRoutings");
									xpath.put("InsertPayRoutingRequest_PayRoutings_Day7",
											"InsertPayRoutingRequest_PayRoutings");
									expectedValues = WSClient.getSingleNodeList(InsertRoutingReq, xpath, false,
											XMLType.REQUEST);
									actualValues = WSClient.getDBRow(WSClient.getQuery("QS_08"));
									WSAssert.assertEquals(expectedValues, actualValues, false);

								}

							}

						}

					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID1.containsValue("error") || !resvID1.isEmpty())
				CancelReservation.cancelReservation("DS_02");

		}

	}

	@Test(groups = { "minimumRegression", "InsertPayRouting", "ResvAdvanced", "OWS" })
	public void insertPayRouting_39549() throws Exception {

		try {

			String testName = "insertPayRouting_39549";
			WSClient.startTest(testName, "Verify that error exists when invalid transaction code is passed",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();

				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, channel);
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resortOperaValue, channel));

				OPERALib.setOperaHeader(OPERALib.getUserName());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				WSClient.writeToReport(LogStatus.INFO,
						"<b>Creating 2 reservations-One For Payee and the other for Guest</b>");
				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID1.equals(""))
					profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {
					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID1 = CreateReservation.createReservation("DS_01");
					if (!resvID1.equals("error")) {

						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

						/************
						 * Prerequisite 1: Create profile
						 *********************************/
						if (profileID2.equals(""))
							profileID2 = CreateProfile.createProfile("DS_01");
						if (!profileID2.equals("error")) {

							WSClient.setData("{var_profileId2}", profileID2);
							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							resvID2 = CreateReservation.createReservation("DS_01");
							if (!resvID2.containsValue("error")) {

								WSClient.setData("{var_resvId2}", resvID2.get("reservationId"));

								WSClient.setData("{var_instCode}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

								/*******************
								 * OWS Insert Pay Routing Operation
								 ************************/
								String InsertRoutingReq = WSClient.createSOAPMessage("OWSInsertPayRouting", "DS_01");
								String InsertRoutingRes = WSClient.processSOAPMessage(InsertRoutingReq);
								WSAssert.assertIfElementValueEquals(InsertRoutingRes,
										"InsertPayRoutingResponse_Result_resultStatusFlag", "FAIL", false);
								if (WSAssert.assertIfElementExists(InsertRoutingRes, "Result_Text_TextElement", true)) {

									WSClient.writeToReport(LogStatus.INFO,
											"<b>The error message is:</b>" + "<b>"
													+ WSClient.getElementValue(InsertRoutingRes,
															"Result_Text_TextElement", XMLType.RESPONSE)
													+ "</b>");

								}
								if (WSAssert.assertIfElementExists(InsertRoutingRes,
										"InsertPayRoutingResponse_Result_GDSError", true)) {

									WSClient.writeToReport(LogStatus.INFO,
											"<b>The error message is: </b>" + WSClient.getElementValue(InsertRoutingRes,
													"InsertPayRoutingResponse_Result_GDSError", XMLType.RESPONSE));

								}

							}

						}

					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID1.containsValue("error") || !resvID1.isEmpty())
				CancelReservation.cancelReservation("DS_02");

		}

	}

}
