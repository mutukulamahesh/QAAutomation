package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.guestServices;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CheckoutReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.SetHousekeepingRoomStatus;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class UpdateRoomStatus extends WSSetUp {

	boolean prerequisite_block_flag = false;

	void setOccupiedStatus() {

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

				String profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
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

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");

						// Prerequisite 3: Fetching available Hotel rooms with
						// room type

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_15");
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

							}
						}
						if (!roomNumber.equals("")) {
							WSClient.setData("{var_roomNumber}", roomNumber);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Successfully fetched the room , Room Number : " + roomNumber + "</b>");
							// Prerequisite 5: Changing the room status to
							// inspected to assign the room for checking in

							String setHousekeepingRoomStatusReq = WSClient
									.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
							String setHousekeepingRoomStatusRes = WSClient
									.processSOAPMessage(setHousekeepingRoomStatusReq);

							if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
									"SetHousekeepingRoomStatusRS_Success", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status.</b>");
								// Prerequisite 6: Assign Room

								String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
								String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

								if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room.</b>");
									// Prerequisite 7: CheckIn Reservation

									String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
									String checkInRes = WSClient.processSOAPMessage(checkInReq);

									if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success",
											true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Successfully checked in the reservation.</b>");
										WSClient.setData("{var_RoomNo}", roomNumber);

									} else {
										prerequisite_block_flag = true;
										WSClient.writeToReport(LogStatus.WARNING,
												"Blocked : Unable to checkin reservation");
									}
								} else {
									prerequisite_block_flag = true;
									WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to assign room");
								}

							} else {
								prerequisite_block_flag = true;
								WSClient.writeToReport(LogStatus.WARNING,
										"Blocked : Unable to change the status of room to vacant and inspected");
							}

						} else {
							prerequisite_block_flag = true;
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to fetch/create room");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// @Test(groups = { "minimumRegression", "UpdateRoomStatus", "OWS"
	// ,"GuestServices" })
	//
	// public void updateRoomStatus_60019()
	// {
	// try
	// {
	// String testName = "updateRoomStatus_60019";
	// WSClient.startTest(testName, "verify that room status is changed to Dirty
	// with channel when channel and carrier name are
	// different","minimumRegression");
	//
	// if(OperaPropConfig.getPropertyConfigResults(new String[]{"RoomType"})){
	// String resortOperaValue = OPERALib.getResort();
	// String chain = OPERALib.getChain();
	// WSClient.setData("{var_chain}", chain);
	// WSClient.setData("{var_resort}", resortOperaValue);
	//
	// prerequisite_block_flag = false;
	//
	// String uname = OPERALib.getUserName();
	// String pwd = OPERALib.getPassword();
	// String channel = OWSLib.getChannel(3);
	// String channelType = OWSLib.getChannelType(channel);
	// String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	// channel);
	// WSClient.setData("{var_owsresort}", resort);
	// OPERALib.setOperaHeader(uname);
	// WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_roomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// //fetching hotel rooms
	//
	//
	// String fetchHotelRoomsReq =
	// WSClient.createSOAPMessage("FetchHotelRooms","DS_15");
	// String fetchHotelRoomsRes =
	// WSClient.processSOAPMessage(fetchHotelRoomsReq);
	//
	// if (WSAssert.assertIfElementExists(fetchHotelRoomsRes,
	// "FetchHotelRoomsRS_Success", true) &&
	// WSAssert.assertIfElementExists(fetchHotelRoomsRes,"FetchHotelRoomsRS_HotelRooms_Room",
	// true)) {
	//
	// String roomNumber =
	// WSClient.getElementValue(fetchHotelRoomsRes,"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber",
	// XMLType.RESPONSE);
	// WSClient.setData("{var_RoomNo}",roomNumber);
	// WSClient.setData("{var_roomNumber}",roomNumber);
	//
	// WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched
	// room.</b>");
	// }
	// else
	// {
	// //unable to fetch housekeeping board --->create a room
	//
	// String createRoomReq = WSClient.createSOAPMessage("CreateRoom",
	// "RoomMaint");
	// String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
	//
	// if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success",
	// true)) {
	// String roomNumber =
	// WSClient.getElementValue(createRoomReq,"CreateRoomRQ_Room_RoomDetails_RoomNumber",
	// XMLType.REQUEST);
	// WSClient.setData("{var_RoomNo}",roomNumber);
	// WSClient.setData("{var_roomNumber}",roomNumber);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created
	// room.</b>");
	//
	// }
	// else
	// {
	// prerequisite_block_flag=true;
	// WSClient.writeToReport(LogStatus.WARNING,"************Blocked : Unable to
	// create a room****************");
	// }
	//
	// }
	//
	// // OWS UpdateRoomStatus
	// if (prerequisite_block_flag==false) {
	//
	// OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
	// channelCarrier);
	// String UpdateRoomStatusReq =
	// WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_01");
	// String UpdateRoomStatusResponseXML =
	// WSClient.processSOAPMessage(UpdateRoomStatusReq);
	//
	//
	//
	//
	// if(WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
	// "UpdateRoomStatusResponse_Result_resultStatusFlag", false))
	// {
	// if(WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
	// "UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false))
	// {
	// //Validation
	// String query=WSClient.getQuery("QS_01");
	// HashMap<String, String> roomRecord = WSClient.getDBRow(query);
	// String status = WSClient.getElementValue(UpdateRoomStatusReq,
	// "UpdateRoomStatusRequest_RoomStatus",
	// XMLType.REQUEST);
	//
	//
	// boolean result = WSAssert.assertEquals(status,
	// roomRecord.get("ROOM_STATUS"), true);
	//
	// if (result == true) {
	// WSClient.writeToReport(LogStatus.PASS,
	// "Room Status is updated successfully to "
	// + roomRecord.get("ROOM_STATUS"));
	// } else {
	// WSClient.writeToReport(LogStatus.FAIL,
	// "Room Status is not updated");
	//
	// }
	// }
	// }
	// if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML ,
	// "UpdateRoomStatusResponse_faultcode", true)) {
	// if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML ,
	// "UpdateRoomStatusResponse_faultstring", true)) {
	//
	// String message = WSClient.getElementValue(UpdateRoomStatusResponseXML ,
	// "UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.FAIL,"<b>"+ message+"</b>");
	// }
	// }
	//
	// if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML ,
	// "Result_Text_TextElement",
	// true)) {
	//
	// String message = WSClient.getElementValue(UpdateRoomStatusResponseXML ,
	// "Result_Text_TextElement", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>The text displayed in the response is :" + message+"</b>");
	// }
	//
	// if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML ,
	// "UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
	// String code = WSClient.getElementValue(UpdateRoomStatusResponseXML ,
	// "UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>The error code displayed in the response is :" + code+"</b>");
	// }
	// if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML ,
	// "UpdateRoomStatusResponse_Result_GDSError", true)) {
	// String message = WSClient.getElementValue(UpdateRoomStatusResponseXML ,
	// "UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>The error displayed in the response is :" + message+"</b>");
	// }
	// }
	//
	// }
	// }
	// catch(Exception e)
	// {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// }
	// finally
	// {
	// if(!WSClient.getData("{var_roomNumber}").equals(""))
	// try {
	// SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	//

	@Test(groups = { "sanity", "UpdateRoomStatus", "OWS", "GuestServices" })

	public void updateRoomStatus_39110() {
		try {
			String testName = "updateRoomStatus_39110";
			WSClient.startTest(testName, "Verify that the room status is changed to Dirty", "sanity");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				prerequisite_block_flag = false;

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				// fetching hotel rooms

				String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_15");
				String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

				if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert
						.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {

					String roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
							"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
					WSClient.setData("{var_RoomNo}", roomNumber);
					WSClient.setData("{var_roomNumber}", roomNumber);

					WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched room.</b>");
				} else {
					// unable to fetch housekeeping board --->create a room

					String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
					String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

					if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
						String roomNumber = WSClient.getElementValue(createRoomReq,
								"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
						WSClient.setData("{var_RoomNo}", roomNumber);
						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created room.</b>");

					} else {
						prerequisite_block_flag = true;
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Unable to create a room****************");
					}

				}

				// OWS UpdateRoomStatus
				if (prerequisite_block_flag == false) {

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_01");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							// Validation
							String query = WSClient.getQuery("QS_01");
							HashMap<String, String> roomRecord = WSClient.getDBRow(query);
							String status = WSClient.getElementValue(UpdateRoomStatusReq,
									"UpdateRoomStatusRequest_RoomStatus", XMLType.REQUEST);

							boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);

							if (result == true) {
								WSClient.writeToReport(LogStatus.PASS,
										"Room Status is updated successfully to " + roomRecord.get("ROOM_STATUS"));
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Room Status is not updated");

							}
						}
					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_faultcode", true)) {
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_faultstring", true)) {

							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
						}
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {

						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the response is :" + message + "</b>");
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
						String code = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error code displayed in the response is :" + code + "</b>");
					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_GDSError", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the response is :" + message + "</b>");
					}
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			if (!WSClient.getData("{var_roomNumber}").equals(""))
				try {
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "OWS", "GuestServices" })

	public void updateRoomStatus_39111() {
		try {
			String testName = "updateRoomStatus_39111";
			WSClient.startTest(testName, "verify that room status is changed to Inspected", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				prerequisite_block_flag = false;

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				WSClient.setData("{var_parameter}", "USE_INSPECTED_STATUS");
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking USE_INSPECTED_STATUS=Y</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing USE_INSPECTED_STATUS to Y</b>");
					Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}

				// fetching hotel rooms

				if (!Parameter.equals("error")) {
					String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_15");
					String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

					if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true)
							&& WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room",
									true)) {

						String roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
								"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						WSClient.setData("{var_RoomNo}", roomNumber);
						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched room.</b>");

					} else {
						// unable to fetch housekeeping board --->create a room

						String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
						String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

						if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
							String roomNumber = WSClient.getElementValue(createRoomReq,
									"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							WSClient.setData("{var_RoomNo}", roomNumber);
							WSClient.setData("{var_roomNumber}", roomNumber);
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created room.</b>");

						} else {
							prerequisite_block_flag = true;
							WSClient.writeToReport(LogStatus.WARNING,
									"************Blocked : Unable to create a room****************");
						}

					}

					// OWS UpdateRoomStatus
					if (prerequisite_block_flag == false) {

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String UpdateRoomStatusReq = WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_02");
						String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								// Validation
								String query = WSClient.getQuery("QS_01");
								HashMap<String, String> roomRecord = WSClient.getDBRow(query);
								String status = WSClient.getElementValue(UpdateRoomStatusReq,
										"UpdateRoomStatusRequest_RoomStatus", XMLType.REQUEST);

								boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);

								if (result == true) {
									WSClient.writeToReport(LogStatus.PASS,
											"Room Status is updated successfully to " + roomRecord.get("ROOM_STATUS"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Room Status is not updated");
								}
							}
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_faultcode", true)) {
							if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_faultstring", true)) {

								String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
										"UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
							}
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
								true)) {

							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
							String code = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error code displayed in the response is :" + code + "</b>");
						}
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_GDSError", true)) {
							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the response is :" + message + "</b>");
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			if (!WSClient.getData("{var_roomNumber}").equals(""))
				try {
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "OWS", "GuestServices" })

	public void updateRoomStatus_39112() {
		try {
			String testName = "updateRoomStatus_39112";
			WSClient.startTest(testName, "verify that room status is changed to PickUp", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				prerequisite_block_flag = false;

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				WSClient.setData("{var_parameter}", "PICKUP_STATUS");
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking PICKUP_STATUS=Y</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing PICKUP_STATUS to Y</b>");
					Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}

				// fetching hotel rooms

				if (!Parameter.equals("error")) {

					String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_15");
					String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

					if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true)
							&& WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room",
									true)) {

						String roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
								"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						WSClient.setData("{var_RoomNo}", roomNumber);
						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched room.</b>");

					} else {
						// unable to fetch housekeeping board --->create a room

						String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
						String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

						if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
							String roomNumber = WSClient.getElementValue(createRoomReq,
									"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							WSClient.setData("{var_RoomNo}", roomNumber);
							WSClient.setData("{var_roomNumber}", roomNumber);
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created room.</b>");

						} else {
							prerequisite_block_flag = true;
							WSClient.writeToReport(LogStatus.WARNING,
									"************Blocked : Unable to create a room****************");
						}

					}

					// OWS UpdateRoomStatus
					if (prerequisite_block_flag == false) {

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String UpdateRoomStatusReq = WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_03");
						String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								// Validation
								String query = WSClient.getQuery("QS_01");
								HashMap<String, String> roomRecord = WSClient.getDBRow(query);
								String status = WSClient.getElementValue(UpdateRoomStatusReq,
										"UpdateRoomStatusRequest_RoomStatus", XMLType.REQUEST);

								boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);

								if (result == true) {
									WSClient.writeToReport(LogStatus.PASS,
											"Room Status is updated successfully to " + roomRecord.get("ROOM_STATUS"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Room Status is not updated");
								}
							}
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_faultcode", true)) {
							if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_faultstring", true)) {

								String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
										"UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
							}
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
								true)) {

							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
							String code = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error code displayed in the response is :" + code + "</b>");
						}
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_GDSError", true)) {
							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the response is :" + message + "</b>");
						}

					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			if (!WSClient.getData("{var_roomNumber}").equals(""))
				try {
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "OWS", "GuestServices" })

	public void updateRoomStatus_39113() {
		try {
			String testName = "updateRoomStatus_39113";
			WSClient.startTest(testName, "verify that room status is changed to clean", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				prerequisite_block_flag = false;

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				// fetching hotel rooms

				String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_15");
				String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

				if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert
						.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {

					String roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
							"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
					WSClient.setData("{var_RoomNo}", roomNumber);
					WSClient.setData("{var_roomNumber}", roomNumber);
					WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched room.</b>");

				} else {
					// unable to fetch housekeeping board --->create a room

					String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
					String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

					if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
						String roomNumber = WSClient.getElementValue(createRoomReq,
								"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
						WSClient.setData("{var_RoomNo}", roomNumber);
						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created room.</b>");

					} else {
						prerequisite_block_flag = true;
						WSClient.writeToReport(LogStatus.WARNING,
								"************Blocked : Unable to create a room****************");
					}

				}

				// change status to dirty

				String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_04");
				String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);
				if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success",
						true)) {
					WSClient.writeToReport(LogStatus.INFO, "Room Status changed to Dirty Successfully");
				} else {
					prerequisite_block_flag = true;
					WSClient.writeToReport(LogStatus.WARNING,
							"************Blocked : Unable to change status to dirty****************");
				}

				// OWS UpdateRoomStatus
				if (prerequisite_block_flag == false) {

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_04");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							// Validation
							String query = WSClient.getQuery("QS_01");
							HashMap<String, String> roomRecord = WSClient.getDBRow(query);
							String status = WSClient.getElementValue(UpdateRoomStatusReq,
									"UpdateRoomStatusRequest_RoomStatus", XMLType.REQUEST);

							boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);

							if (result == true) {
								WSClient.writeToReport(LogStatus.PASS,
										"Room Status is updated successfully to " + roomRecord.get("ROOM_STATUS"));
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Room Status is not updated");
							}
						}
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_faultcode", true)) {
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_faultstring", true)) {

							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
						}
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {

						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the response is :" + message + "</b>");
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
						String code = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error code displayed in the response is :" + code + "</b>");
					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_GDSError", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the response is :" + message + "</b>");
					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			if (!WSClient.getData("{var_roomNumber}").equals(""))
				try {
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "OWS", "GuestServices", "Failed" })

	public void updateRoomStatus_39114() {
		try {
			String testName = "updateRoomStatus_39114";
			WSClient.startTest(testName, "verify that room status is changed to OutofOrder", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				prerequisite_block_flag = false;

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_parameter}", "OUT_OF_ORDER");
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking OUT_OF_ORDER=Y</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing OUT_OF_ORDER to Y</b>");
					Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}

				// fetching hotel rooms

				if (!Parameter.equals("error")) {

					String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_15");
					String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

					if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true)
							&& WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room",
									true)) {

						String roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
								"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						WSClient.setData("{var_RoomNo}", roomNumber);
						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched room.</b>");

					} else {
						// unable to fetch housekeeping board --->create a room

						String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
						String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

						if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
							String roomNumber = WSClient.getElementValue(createRoomReq,
									"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							WSClient.setData("{var_RoomNo}", roomNumber);
							WSClient.setData("{var_roomNumber}", roomNumber);
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created room.</b>");

						} else {
							prerequisite_block_flag = true;
							WSClient.writeToReport(LogStatus.WARNING,
									"************Blocked : Unable to create a room****************");
						}

					}

					// OWS UpdateRoomStatus
					if (prerequisite_block_flag == false) {

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String UpdateRoomStatusReq = WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_05");
						String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								// Validation
								String query = WSClient.getQuery("QS_01");
								HashMap<String, String> roomRecord = WSClient.getDBRow(query);
								String status = WSClient.getElementValue(UpdateRoomStatusReq,
										"UpdateRoomStatusRequest_RoomStatus", XMLType.REQUEST);

								boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);

								if (result == true) {
									WSClient.writeToReport(LogStatus.PASS,
											"Room Status is updated successfully to " + roomRecord.get("ROOM_STATUS"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Room Status is not updated");
								}
							}
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_faultcode", true)) {
							if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_faultstring", true)) {

								String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
										"UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
							}
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
								true)) {

							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
							String code = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error code displayed in the response is :" + code + "</b>");
						}
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_GDSError", true)) {
							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the response is :" + message + "</b>");
						}

					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			if (!WSClient.getData("{var_roomNumber}").equals(""))
				try {
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "OWS", "GuestServices", "Failed" })

	public void updateRoomStatus_39115() {
		try {
			String testName = "updateRoomStatus_39115";
			WSClient.startTest(testName, "verify that room status is changed to OutofService", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				prerequisite_block_flag = false;

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_parameter}", "OUT_OF_SERVICE");
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking OUT_OF_SERVICE=Y</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing OUT_OF_SERVICE to Y</b>");
					Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}

				// fetching hotel rooms

				if (!Parameter.equals("error")) {

					String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_15");
					String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

					if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true)
							&& WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room",
									true)) {

						String roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
								"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						WSClient.setData("{var_RoomNo}", roomNumber);
						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched room.</b>");

					} else {
						// unable to fetch housekeeping board --->create a room

						String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
						String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

						if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
							String roomNumber = WSClient.getElementValue(createRoomReq,
									"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							WSClient.setData("{var_RoomNo}", roomNumber);
							WSClient.setData("{var_roomNumber}", roomNumber);
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created room.</b>");

						} else {
							prerequisite_block_flag = true;
							WSClient.writeToReport(LogStatus.WARNING,
									"************Blocked : Unable to create a room****************");
						}

					}

					// OWS UpdateRoomStatus
					if (prerequisite_block_flag == false) {

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String UpdateRoomStatusReq = WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_06");
						String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								// Validation
								String query = WSClient.getQuery("QS_01");
								HashMap<String, String> roomRecord = WSClient.getDBRow(query);
								String status = WSClient.getElementValue(UpdateRoomStatusReq,
										"UpdateRoomStatusRequest_RoomStatus", XMLType.REQUEST);

								boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);

								if (result == true) {
									WSClient.writeToReport(LogStatus.PASS,
											"Room Status is updated successfully to " + roomRecord.get("ROOM_STATUS"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Room Status is not updated");
								}
							}
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_faultcode", true)) {
							if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_faultstring", true)) {

								String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
										"UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
							}
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
								true)) {

							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
							String code = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error code displayed in the response is :" + code + "</b>");
						}
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_GDSError", true)) {
							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the response is :" + message + "</b>");
						}

					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			if (!WSClient.getData("{var_roomNumber}").equals(""))
				try {
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "OWS", "GuestServices" })

	public void updateRoomStatus_39116() {
		try {
			String testName = "updateRoomStatus_39116";
			WSClient.startTest(testName, "verify that room repair status is changed to OutOfOrder",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType", "OOOSReason" })) {
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				prerequisite_block_flag = false;

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				String roomStatusReason = OperaPropConfig.getDataSetForCode("OOOSReason", "DS_01");
				WSClient.setData("{var_roomStatusReason}", roomStatusReason);

				WSClient.setData("{var_parameter}", "OUT_OF_ORDER");
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking OUT_OF_ORDER=Y</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing OUT_OF_ORDER to Y</b>");
					Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}

				// fetching hotel rooms

				if (!Parameter.equals("error")) {

					String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_15");
					String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

					if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true)
							&& WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room",
									true)) {

						String roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
								"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						WSClient.setData("{var_RoomNo}", roomNumber);
						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched room.</b>");

					} else {
						// unable to fetch housekeeping board --->create a room

						String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
						String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

						if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
							String roomNumber = WSClient.getElementValue(createRoomReq,
									"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							WSClient.setData("{var_RoomNo}", roomNumber);
							WSClient.setData("{var_roomNumber}", roomNumber);
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created room.</b>");

						} else {
							prerequisite_block_flag = true;
							WSClient.writeToReport(LogStatus.WARNING,
									"************Blocked : Unable to create a room****************");
						}

					}
					// OWS UpdateRoomStatus
					if (prerequisite_block_flag == false) {

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String UpdateRoomStatusReq = WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_07");
						String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								// Validation

								String query1 = WSClient.getQuery("QS_04");
								LinkedHashMap<String, String> actuals = WSClient.getDBRow(query1);

								LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();
								expected.put("ROOM_STATUS", WSClient.getElementValue(UpdateRoomStatusReq,
										"UpdateRoomStatusRequest_RoomRepair_RepairStatus", XMLType.REQUEST));
								expected.put("REASON_CODE", WSClient.getElementValue(UpdateRoomStatusReq,
										"UpdateRoomStatusRequest_RoomRepair_RepairReason", XMLType.REQUEST));
								expected.put("REPAIR_TYPE", WSClient.getElementValue(UpdateRoomStatusReq,
										"UpdateRoomStatusRequest_RoomRepair_RepairType", XMLType.REQUEST));
								expected.put("REPAIR_REMARKS", WSClient.getElementValue(UpdateRoomStatusReq,
										"UpdateRoomStatusRequest_RoomRepair_RepairRemarks", XMLType.REQUEST));
								expected.put("RETURN_STATUS", WSClient.getElementValue(UpdateRoomStatusReq,
										"UpdateRoomStatusRequest_RoomRepair_ReturnStatus", XMLType.REQUEST));

								expected.put("BEGIN_DATE", WSClient.getElementValue(UpdateRoomStatusReq,
										"RoomRepair_RepairDate_StartDate", XMLType.REQUEST));
								expected.put("END_DATE", WSClient.getElementValue(UpdateRoomStatusReq,
										"RoomRepair_RepairDate_EndDate", XMLType.REQUEST));

								WSAssert.assertEquals(expected, actuals, false);
							}
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_faultcode", true)) {
							if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_faultstring", true)) {

								String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
										"UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
							}
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
								true)) {

							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
							String code = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error code displayed in the response is :" + code + "</b>");
						}
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_GDSError", true)) {
							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the response is :" + message + "</b>");
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			if (!WSClient.getData("{var_roomNumber}").equals(""))
				try {
					String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus",
							"DS_07");
					String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "OWS", "GuestServices" })

	public void updateRoomStatus_39117() {
		try {
			String testName = "updateRoomStatus_39117";
			WSClient.startTest(testName, "verify that room repair status is changed to OutOfService",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType", "OOOSReason" })) {
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				prerequisite_block_flag = false;

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				String roomStatusReason = OperaPropConfig.getDataSetForCode("OOOSReason", "DS_01");
				WSClient.setData("{var_roomStatusReason}", roomStatusReason);

				WSClient.setData("{var_parameter}", "OUT_OF_SERVICE");
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking OUT_OF_SERVICE=Y</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing OUT_OF_SERVICE to Y</b>");
					Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}

				// fetching hotel rooms

				if (!Parameter.equals("error")) {

					String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_15");
					String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

					if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true)
							&& WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room",
									true)) {

						String roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
								"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						WSClient.setData("{var_RoomNo}", roomNumber);
						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched room.</b>");

					} else {
						// unable to fetch housekeeping board --->create a room

						String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
						String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

						if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
							String roomNumber = WSClient.getElementValue(createRoomReq,
									"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							WSClient.setData("{var_RoomNo}", roomNumber);
							WSClient.setData("{var_roomNumber}", roomNumber);
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created room.</b>");

						} else {
							prerequisite_block_flag = true;
							WSClient.writeToReport(LogStatus.WARNING,
									"************Blocked : Unable to create a room****************");
						}

					}

					// OWS UpdateRoomStatus
					if (prerequisite_block_flag == false) {

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String UpdateRoomStatusReq = WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_08");
						String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", false)) {
							if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								// Validation

								String query1 = WSClient.getQuery("QS_04");
								LinkedHashMap<String, String> actuals = WSClient.getDBRow(query1);

								LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();
								expected.put("ROOM_STATUS", WSClient.getElementValue(UpdateRoomStatusReq,
										"UpdateRoomStatusRequest_RoomRepair_RepairStatus", XMLType.REQUEST));
								expected.put("REASON_CODE", WSClient.getElementValue(UpdateRoomStatusReq,
										"UpdateRoomStatusRequest_RoomRepair_RepairReason", XMLType.REQUEST));
								expected.put("REPAIR_TYPE", WSClient.getElementValue(UpdateRoomStatusReq,
										"UpdateRoomStatusRequest_RoomRepair_RepairType", XMLType.REQUEST));
								expected.put("REPAIR_REMARKS", WSClient.getElementValue(UpdateRoomStatusReq,
										"UpdateRoomStatusRequest_RoomRepair_RepairRemarks", XMLType.REQUEST));
								expected.put("RETURN_STATUS", WSClient.getElementValue(UpdateRoomStatusReq,
										"UpdateRoomStatusRequest_RoomRepair_ReturnStatus", XMLType.REQUEST));

								expected.put("BEGIN_DATE", WSClient.getElementValue(UpdateRoomStatusReq,
										"RoomRepair_RepairDate_StartDate", XMLType.REQUEST));
								expected.put("END_DATE", WSClient.getElementValue(UpdateRoomStatusReq,
										"RoomRepair_RepairDate_EndDate", XMLType.REQUEST));

								WSAssert.assertEquals(expected, actuals, false);
							}
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_faultcode", true)) {
							if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_faultstring", true)) {

								String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
										"UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
							}
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
								true)) {

							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
							String code = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error code displayed in the response is :" + code + "</b>");
						}
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_GDSError", true)) {
							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the response is :" + message + "</b>");
						}

					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			if (!WSClient.getData("{var_roomNumber}").equals(""))
				try {
					String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus",
							"DS_08");
					String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "OWS", "GuestServices" })

	public void updateRoomStatus_39118() {
		try {
			String testName = "updateRoomStatus_39118";
			WSClient.startTest(testName, "verify that guest service status is changed to MakeUpRoom",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			prerequisite_block_flag = false;

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_parameter}", "GUEST_SERVICE_STATUS");
			WSClient.setData("{var_settingValue}", "Y");
			WSClient.writeToReport(LogStatus.INFO, "<b>Checking if GUEST_SERVICE_STATUS=Y</b>");
			String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Changing GUEST_SERVICE_STATUS to Y</b>");
				Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}
			if (!Parameter.equals("error")) {
				setOccupiedStatus();

				// OWS UpdateRoomStatus
				if (prerequisite_block_flag == false) {

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_09");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							// Validation
							String query = WSClient.getQuery("QS_02");
							HashMap<String, String> roomRecord = WSClient.getDBRow(query);

							String status = WSClient.getElementValue(UpdateRoomStatusReq,
									"UpdateRoomStatusRequest_GuestServiceStatus", XMLType.REQUEST);

							boolean result = WSAssert.assertEquals(status, roomRecord.get("SERVICE_STATUS"), true);

							if (result == true) {
								WSClient.writeToReport(LogStatus.PASS,
										"Service Status is successfully updated to" + roomRecord.get("SERVICE_STATUS"));
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Service Status didn't get updated");

							}
						}
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_faultcode", true)) {
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_faultstring", true)) {

							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
						}
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {

						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the response is :" + message + "</b>");
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
						String code = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error code displayed in the response is :" + code + "</b>");
					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_GDSError", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the response is :" + message + "</b>");
					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// checking out reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CheckoutReservation.checkOutReservation("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "OWS", "GuestServices" })

	public void updateRoomStatus_39119() {
		try {
			String testName = "updateRoomStatus_39119";
			WSClient.startTest(testName, "verify that guest service status is changed to DoNotDisturb",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			prerequisite_block_flag = false;

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_parameter}", "GUEST_SERVICE_STATUS");
			WSClient.setData("{var_settingValue}", "Y");
			WSClient.writeToReport(LogStatus.INFO, "<b>Checking if GUEST_SERVICE_STATUS=Y</b>");
			String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Changing GUEST_SERVICE_STATUS to Y</b>");
				Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}
			if (!Parameter.equals("error")) {
				setOccupiedStatus();

				// OWS UpdateRoomStatus
				if (prerequisite_block_flag == false) {

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_10");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							// Validation
							String query = WSClient.getQuery("QS_02");
							HashMap<String, String> roomRecord = WSClient.getDBRow(query);

							String status = WSClient.getElementValue(UpdateRoomStatusReq,
									"UpdateRoomStatusRequest_GuestServiceStatus", XMLType.REQUEST);

							boolean result = WSAssert.assertEquals(status, roomRecord.get("SERVICE_STATUS"), true);

							if (result == true) {
								WSClient.writeToReport(LogStatus.PASS,
										"Service Status is successfully updated to" + roomRecord.get("SERVICE_STATUS"));
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Service Status didn't get updated");

							}
						}
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_faultcode", true)) {
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_faultstring", true)) {

							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
						}
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {

						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the response is :" + message + "</b>");
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
						String code = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error code displayed in the response is :" + code + "</b>");
					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_GDSError", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the response is :" + message + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// checking out reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CheckoutReservation.checkOutReservation("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "OWS", "GuestServices" })

	public void updateRoomStatus_39120() {
		try {
			String testName = "updateRoomStatus_39120";
			WSClient.startTest(testName, "verify that turn down status is changed to REQUIRED", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			prerequisite_block_flag = false;

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);

			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

			WSClient.setData("{var_parameter}", "TURNDOWN");
			WSClient.setData("{var_settingValue}", "Y");
			WSClient.writeToReport(LogStatus.INFO, "<b>Checking if TURNDOWN=Y</b>");
			String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Changing TURNDOWN to Y</b>");
				Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}
			if (!Parameter.equals("error")) {

				setOccupiedStatus();

				// OWS UpdateRoomStatus
				if (prerequisite_block_flag == false) {

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_11");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							// Validation
							String query = WSClient.getQuery("QS_03");
							HashMap<String, String> roomRecord = WSClient.getDBRow(query);

							String status = WSClient.getElementValue(UpdateRoomStatusReq,
									"UpdateRoomStatusRequest_TurnDownStatus", XMLType.REQUEST);

							boolean result = WSAssert.assertEquals(status, roomRecord.get("TURNDOWN_STATUS"), true);

							if (result == true) {
								WSClient.writeToReport(LogStatus.PASS, "Turndown Status is updated successfully to "
										+ roomRecord.get("TURNDOWN_STATUS"));
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Turndown Status didn't get updated");

							}
						}
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_faultcode", true)) {
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_faultstring", true)) {

							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
						}
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {

						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the response is :" + message + "</b>");
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
						String code = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error code displayed in the response is :" + code + "</b>");
					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_GDSError", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the response is :" + message + "</b>");
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// checking out reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CheckoutReservation.checkOutReservation("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "OWS", "GuestServices" })

	public void updateRoomStatus_39121() {
		try {
			String testName = "updateRoomStatus_39121";
			WSClient.startTest(testName, "verify that turn down status is changed to COMPLETED", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			prerequisite_block_flag = false;

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_parameter}", "TURNDOWN");
			WSClient.setData("{var_settingValue}", "Y");
			WSClient.writeToReport(LogStatus.INFO, "<b>Checking if TURNDOWN=Y</b>");
			String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Changing TURNDOWN to Y</b>");
				Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}
			if (!Parameter.equals("error")) {

				setOccupiedStatus();

				// OWS UpdateRoomStatus
				if (prerequisite_block_flag == false) {

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_12");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							// Validation
							String query = WSClient.getQuery("QS_03");
							HashMap<String, String> roomRecord = WSClient.getDBRow(query);

							String status = WSClient.getElementValue(UpdateRoomStatusReq,
									"UpdateRoomStatusRequest_TurnDownStatus", XMLType.REQUEST);

							boolean result = WSAssert.assertEquals(status, roomRecord.get("TURNDOWN_STATUS"), true);

							if (result == true) {
								WSClient.writeToReport(LogStatus.PASS, "Turndown Status is updated successfully to "
										+ roomRecord.get("TURNDOWN_STATUS"));
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Turndown Status didn't get updated");

							}
						}
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_faultcode", true)) {
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_faultstring", true)) {

							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
						}
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {

						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the response is :" + message + "</b>");
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
						String code = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error code displayed in the response is :" + code + "</b>");
					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_GDSError", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the response is :" + message + "</b>");
					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// checking out reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CheckoutReservation.checkOutReservation("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "OWS", "GuestServices" })

	public void updateRoomStatus_39122() {
		try {
			String testName = "updateRoomStatus_39122";
			WSClient.startTest(testName, "verify that turn down status is changed to NOT REQUIRED",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			prerequisite_block_flag = false;

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_parameter}", "TURNDOWN");
			WSClient.setData("{var_settingValue}", "Y");
			WSClient.writeToReport(LogStatus.INFO, "<b>Checking if TURNDOWN=Y</b>");
			String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Changing TURNDOWN to Y</b>");
				Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}
			if (!Parameter.equals("error")) {

				setOccupiedStatus();

				// OWS UpdateRoomStatus
				if (prerequisite_block_flag == false) {

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_13");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							// Validation
							String query = WSClient.getQuery("QS_03");
							HashMap<String, String> roomRecord = WSClient.getDBRow(query);

							String status = WSClient.getElementValue(UpdateRoomStatusReq,
									"UpdateRoomStatusRequest_TurnDownStatus", XMLType.REQUEST);

							boolean result = WSAssert.assertEquals(status, roomRecord.get("TURNDOWN_STATUS"), true);

							if (result == true) {
								WSClient.writeToReport(LogStatus.PASS, "Turndown Status is updated successfully to "
										+ roomRecord.get("TURNDOWN_STATUS"));
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Turndown Status didn't get updated");

							}
						}
					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_faultcode", true)) {
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_faultstring", true)) {

							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
						}
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {

						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the response is :" + message + "</b>");
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
						String code = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error code displayed in the response is :" + code + "</b>");
					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_GDSError", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the response is :" + message + "</b>");
					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// checking out reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CheckoutReservation.checkOutReservation("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "OWS", "GuestServices" })

	public void updateRoomStatus_41522() {
		try {
			String testName = "updateRoomStatus_41522";
			WSClient.startTest(testName,
					"verify that error message is coming when trying to change guest service status with GUEST_SERVICE_STATUS=N",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			prerequisite_block_flag = false;

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_parameter}", "GUEST_SERVICE_STATUS");
			WSClient.setData("{var_settingValue}", "N");
			WSClient.writeToReport(LogStatus.INFO, "<b>Checking if GUEST_SERVICE_STATUS=N</b>");
			String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter.equals("N")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Changing GUEST_SERVICE_STATUS to N</b>");
				Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}
			if (!Parameter.equals("error")) {
				setOccupiedStatus();

				// OWS UpdateRoomStatus
				if (prerequisite_block_flag == false) {

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_09");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", false)) {
						WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL", false);

					}

					// Validation
					String query = WSClient.getQuery("QS_02");
					HashMap<String, String> roomRecord = WSClient.getDBRow(query);

					String status = WSClient.getElementValue(UpdateRoomStatusReq,
							"UpdateRoomStatusRequest_GuestServiceStatus", XMLType.REQUEST);

					boolean result = WSAssert.assertEquals(status, roomRecord.get("SERVICE_STATUS"), true);

					if (result == true) {
						WSClient.writeToReport(LogStatus.FAIL,
								"Service Status is updated to" + roomRecord.get("SERVICE_STATUS"));
					} else {
						WSClient.writeToReport(LogStatus.PASS, "Service Status didn't get updated");

					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_faultcode", true)) {
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_faultstring", true)) {

							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
						}
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {

						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the response is :" + message + "</b>");
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
						String code = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error code displayed in the response is :" + code + "</b>");
					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_GDSError", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the response is :" + message + "</b>");
					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// checking out reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CheckoutReservation.checkOutReservation("DS_01");

				WSClient.setData("{var_parameter}", "GUEST_SERVICE_STATUS");
				WSClient.setData("{var_settingValue}", "Y");

				WSClient.writeToReport(LogStatus.INFO, "<b>Changing GUEST_SERVICE_STATUS to Y</b>");
				ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "OWS", "GuestServices", "Failed" })

	public void updateRoomStatus_41523() {
		try {
			String testName = "updateRoomStatus_41523";
			WSClient.startTest(testName,
					"verify that error message is coming when trying to change turn down status when TURNDOWN=N",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			prerequisite_block_flag = false;

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);

			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

			WSClient.setData("{var_parameter}", "TURNDOWN");
			WSClient.setData("{var_settingValue}", "N");
			WSClient.writeToReport(LogStatus.INFO, "<b>Checking if TURNDOWN=N</b>");
			String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
			if (!Parameter.equals("N")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Changing TURNDOWN to N</b>");
				Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			}
			if (!Parameter.equals("error")) {

				setOccupiedStatus();

				// OWS UpdateRoomStatus
				if (prerequisite_block_flag == false) {

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String UpdateRoomStatusReq = WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_11");
					String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_resultStatusFlag", false)) {
						WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_resultStatusFlag", "FAIL", false);

					}

					// Validation
					String query = WSClient.getQuery("QS_03");
					HashMap<String, String> roomRecord = WSClient.getDBRow(query);

					String status = WSClient.getElementValue(UpdateRoomStatusReq,
							"UpdateRoomStatusRequest_TurnDownStatus", XMLType.REQUEST);

					boolean result = WSAssert.assertEquals(status, roomRecord.get("TURNDOWN_STATUS"), true);

					if (result == true) {
						WSClient.writeToReport(LogStatus.FAIL,
								"Turndown Status is updated to " + roomRecord.get("TURNDOWN_STATUS"));
					} else {
						WSClient.writeToReport(LogStatus.PASS, "Turndown Status didn't get updated");

					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_faultcode", true)) {
						if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_faultstring", true)) {

							String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
									"UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
						}
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {

						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the response is :" + message + "</b>");
					}

					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
						String code = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error code displayed in the response is :" + code + "</b>");
					}
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_GDSError", true)) {
						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The error displayed in the response is :" + message + "</b>");
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// checking out reservation
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CheckoutReservation.checkOutReservation("DS_01");

				WSClient.setData("{var_parameter}", "TURNDOWN");
				WSClient.setData("{var_settingValue}", "Y");

				WSClient.writeToReport(LogStatus.INFO, "<b>Changing TURNDOWN to Y</b>");
				ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "UpdateRoomStatus", "GuestServices", "OWS" })

	public void updateRoomStatus_42225() {

		String room_num = "";
		String roomNumber2 = "";
		try {
			prerequisite_block_flag = false;
			String testName = "updateRoomStatus_42225";
			WSClient.startTest(testName, "Update master Room Status to Dirty in connecting rooms", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);

			// Prerequisite :Fetching rooms that are not reserved
			OPERALib.setOperaHeader(OPERALib.getUserName());
			WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

			room_num = OperaPropConfig.getDataSetForCode("Rooms", "DS_42");
			roomNumber2 = OperaPropConfig.getDataSetForCode("Rooms", "DS_43");

			WSClient.writeToReport(LogStatus.INFO, "<b>Master room number :" + room_num + "</b>");
			if (!room_num.equals("")) {
				WSClient.setData("{var_roomNumber}", room_num);
				SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
			}
			WSClient.writeToReport(LogStatus.INFO, "<b>Component room number :" + roomNumber2 + "</b>");
			if (!roomNumber2.equals("")) {
				WSClient.setData("{var_roomNumber}", roomNumber2);
				SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
			}
			WSClient.setData("{var_RoomNo}", room_num);

			// Updating the Room status of the Created/fetched room to Dirty
			if (prerequisite_block_flag == false) {
				String UpdateRoomStatusReq = WSClient.createSOAPMessage("OWSUpdateRoomStatus", "DS_01");
				System.out.println(UpdateRoomStatusReq);
				String UpdateRoomStatusResponseXML = WSClient.processSOAPMessage(UpdateRoomStatusReq);
				if (WSAssert.assertIfElementValueEquals(UpdateRoomStatusResponseXML,
						"UpdateRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

					String query = WSClient.getQuery("QS_01");
					HashMap<String, String> roomRecord = WSClient.getDBRow(query);
					System.out.println(roomRecord);
					String status = WSClient.getElementValue(UpdateRoomStatusReq, "UpdateRoomStatusRequest_RoomStatus",
							XMLType.REQUEST);

					// Validating the room status from the request with the
					// Database
					// value
					boolean result = WSAssert.assertEquals(status, roomRecord.get("ROOM_STATUS"), true);
					String reqXpath = WSClient.getRequestXPath("UpdateRoomStatusRequest_RoomStatus");

					if (result == true) {
						WSClient.writeToReport(LogStatus.PASS,
								"Master Room Status is updated successfully to " + roomRecord.get("ROOM_STATUS"));
					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"Master Room Status didn't updated to" + roomRecord.get("ROOM_STATUS"));

					}

					WSClient.setData("{var_RoomNo}", roomNumber2);

					String query2 = WSClient.getQuery("QS_01");
					HashMap<String, String> roomRecord1 = WSClient.getDBRow(query2);
					System.out.println(roomRecord1);
					String status1 = WSClient.getElementValue(UpdateRoomStatusReq, "UpdateRoomStatusRequest_RoomStatus",
							XMLType.REQUEST);
					boolean result1 = WSAssert.assertEquals(status1, roomRecord1.get("ROOM_STATUS"), true);
					if (result1 == true) {
						WSClient.writeToReport(LogStatus.PASS,
								"Connected room's status is updated as Dirty due to the updation of the master room's status");
					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"Connected room's status is not updated as Dirty despite the updation of the master room's status");

					}

				}

				if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "UpdateRoomStatusResponse_faultcode",
						true)) {
					if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_faultstring", true)) {

						String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
								"UpdateRoomStatusResponse_faultstring", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
					}
				}

				if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML, "Result_Text_TextElement", true)) {

					String message = WSClient.getElementValue(UpdateRoomStatusResponseXML, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The text displayed in the response is :" + message + "</b>");
				}

				if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
						"UpdateRoomStatusResponse_Result_OperaErrorCode", true)) {
					String code = WSClient.getElementValue(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The error code displayed in the response is :" + code + "</b>");
				}
				if (WSAssert.assertIfElementExists(UpdateRoomStatusResponseXML,
						"UpdateRoomStatusResponse_Result_GDSError", true)) {
					String message = WSClient.getElementValue(UpdateRoomStatusResponseXML,
							"UpdateRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>The error displayed in the response is :" + message + "</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!room_num.equals("")) {
					WSClient.setData("{var_roomNumber}", room_num);
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
				}
				if (!roomNumber2.equals("")) {
					WSClient.setData("{var_roomNumber}", roomNumber2);
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_02");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
