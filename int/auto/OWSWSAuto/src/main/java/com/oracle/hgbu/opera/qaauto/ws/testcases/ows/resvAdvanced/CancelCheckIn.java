package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;

import java.util.HashMap;
import java.util.LinkedHashMap;

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

public class CancelCheckIn extends WSSetUp {

	String profileID = "";


	@Test(groups = { "sanity", "CancelCheckIn", "OWS", "ResvAdvanced","cancelCheckIn_38388" })
	public void cancelCheckIn_38388() {
		try {

			String testName = "cancelCheckIn_38388";
			WSClient.startTest(testName, "verify that the cancelling of checked in reservation is successful", "sanity");
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
				String roomNumber = "";

				// Prerequisite 1 - create profile

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
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
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
						// Prerequisite 3: Fetching available Hotel rooms with
						// room type

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_18");
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
						if(!roomNumber.equals("")){
							WSClient.setData("{var_roomNumber}", roomNumber);
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : "+roomNumber+"</b>");

							// Prerequisite 5: Changing the room status to inspected
							// to assign the room for checking in

							String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus",
									"DS_01");
							String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

							if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
									"SetHousekeepingRoomStatusRS_Success", true)) {

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room</b>");
								// Prerequisite 6: Assign Room

								String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
								String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

								if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

									// Prerequisite 7: CheckIn Reservation

									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");

									String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
									String checkInRes = WSClient.processSOAPMessage(checkInReq);

									if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {

										WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");

										// *****OWS Cancel CheckIn*********//

										OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
										String cancelCheckInReq = WSClient.createSOAPMessage("OWSCancelCheckIn", "DS_01");
										String cancelCheckInRes = WSClient.processSOAPMessage(cancelCheckInReq);

										if (WSAssert.assertIfElementValueEquals(cancelCheckInRes,
												"CancelCheckInResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											// validation from db
											String query = WSClient.getQuery("QS_01");
											LinkedHashMap<String, String> db = WSClient.getDBRow(query);
											String resvStatus = db.get("RESV_STATUS");
											if (resvStatus.equalsIgnoreCase("PROSPECT")) {
												WSClient.writeToReport(LogStatus.PASS,
														"The checkin is cancelled successfully");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"The cancellation of checkin is unsuccessful");
											}

										}

										if (WSAssert.assertIfElementExists(cancelCheckInRes,
												"CancelCheckInResponse_faultcode", true)) {

											String message = WSClient.getElementValue(cancelCheckInRes,
													"CancelCheckInResponse_faultstring", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
										}
										if (WSAssert.assertIfElementExists(cancelCheckInRes, "Result_Text_TextElement",
												true)) {

											String message = WSClient.getElementValue(cancelCheckInRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is :" + message + "</b>");
										}

										if (WSAssert.assertIfElementExists(cancelCheckInRes,
												"CancelCheckInResponse_Result_OperaErrorCode", true)) {

											String code = WSClient.getElementValue(cancelCheckInRes,
													"CancelCheckInResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The error code displayed in the response is :" + code + "</b>");
										}

										if (WSAssert.assertIfElementExists(cancelCheckInRes,
												"CancelCheckInResponse_Result_GDSError", true)) {

											String message = WSClient.getElementValue(cancelCheckInRes,
													"CancelCheckInResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The error displayed in the response is :" + message + "</b>");
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
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,
									"Blocked : Unable to fetch room");
						}

					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "CancelCheckIn", "OWS", "ResvAdvanced" })

	public void cancelCheckIn_39053() {
		try {

			String testName = "cancelCheckIn_39053";
			WSClient.startTest(testName,
					"verify that the cancelling of checked in reservation is successful with confirmation number",
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
				String roomNumber = "";

				// Prerequisite 1 - create profile
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
					// Prerequisite 2 Create Reservation

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					HashMap<String, String> resv = CreateReservation.createReservation("DS_12");
					String resvID = resv.get("reservationId");
					String confirmationNo = resv.get("confirmationId");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
						WSClient.setData("{var_confNo}", confirmationNo);

						// Prerequisite 3: Fetching available Hotel rooms with
						// room type

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_18");
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

						if(!roomNumber.equals("")){
							WSClient.setData("{var_roomNumber}", roomNumber);
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : "+roomNumber+"</b>");


							// Prerequisite 5: Changing the room status to inspected
							// to assign the room for checking in

							String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus",
									"DS_01");
							String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

							if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
									"SetHousekeepingRoomStatusRS_Success", true)) {

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room</b>");

								// Prerequisite 6: Assign Room

								String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
								String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

								if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {


									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");

									// Prerequisite 7: CheckIn Reservation

									String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
									String checkInRes = WSClient.processSOAPMessage(checkInReq);

									if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {


										WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");


										// *****OWS Cancel CheckIn*********//


										OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
										String cancelCheckInReq = WSClient.createSOAPMessage("OWSCancelCheckIn", "DS_02");
										String cancelCheckInRes = WSClient.processSOAPMessage(cancelCheckInReq);

										if (WSAssert.assertIfElementValueEquals(cancelCheckInRes,
												"CancelCheckInResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											// validation from db
											String query = WSClient.getQuery("QS_01");
											LinkedHashMap<String, String> db = WSClient.getDBRow(query);
											String resvStatus = db.get("RESV_STATUS");
											if (resvStatus.equalsIgnoreCase("PROSPECT")) {
												WSClient.writeToReport(LogStatus.PASS,
														"The checkin is cancelled successfully");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"The cancellation of checkin is unsuccessful");
											}

										}

										if (WSAssert.assertIfElementExists(cancelCheckInRes,
												"CancelCheckInResponse_faultcode", true)) {

											String message = WSClient.getElementValue(cancelCheckInRes,
													"CancelCheckInResponse_faultstring", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
										}
										if (WSAssert.assertIfElementExists(cancelCheckInRes, "Result_Text_TextElement",
												true)) {

											String message = WSClient.getElementValue(cancelCheckInRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is :" + message + "</b>");
										}

										if (WSAssert.assertIfElementExists(cancelCheckInRes,
												"CancelCheckInResponse_Result_OperaErrorCode", true)) {

											String code = WSClient.getElementValue(cancelCheckInRes,
													"CancelCheckInResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The error code displayed in the response is :" + code + "</b>");
										}

										if (WSAssert.assertIfElementExists(cancelCheckInRes,
												"CancelCheckInResponse_Result_GDSError", true)) {

											String message = WSClient.getElementValue(cancelCheckInRes,
													"CancelCheckInResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The error displayed in the response is :" + message + "</b>");
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
						}
						else
						{

						}

					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "CancelCheckIn", "OWS", "ResvAdvanced" })

	public void cancelCheckIn_39054() {
		try {

			String testName = "cancelCheckIn_39054";
			WSClient.startTest(testName,
					"verify that the cancelling of checked in reservation is successful with confirmation number and leg number",
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
				String roomNumber = "";

				// Prerequisite 1 - create profile

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");

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
					String confirmationNo = resv.get("confirmationId");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
						WSClient.setData("{var_confNo}", confirmationNo);

						String query = WSClient.getQuery("QS_01");

						String legNo = WSClient.getDBRow(query).get("CONFIRMATION_LEG_NO");
						WSClient.setData("{var_legNo}", legNo);

						// Prerequisite 3: Fetching available Hotel rooms with
						// room type

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_18");
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
						if(!roomNumber.equals("")){
							WSClient.setData("{var_roomNumber}", roomNumber);
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : "+roomNumber+"</b>");

							// Prerequisite 5: Changing the room status to inspected
							// to assign the room for checking in

							String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus",
									"DS_01");
							String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

							if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
									"SetHousekeepingRoomStatusRS_Success", true)) {

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status</b>");

								// Prerequisite 6: Assign Room

								String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
								String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

								if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");

									// Prerequisite 7: CheckIn Reservation

									String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
									String checkInRes = WSClient.processSOAPMessage(checkInReq);

									if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {

										WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation");

										// *****OWS Cancel CheckIn*********//

										OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
										String cancelCheckInReq = WSClient.createSOAPMessage("OWSCancelCheckIn", "DS_03");
										String cancelCheckInRes = WSClient.processSOAPMessage(cancelCheckInReq);

										if (WSAssert.assertIfElementValueEquals(cancelCheckInRes,
												"CancelCheckInResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											// validation from db
											String query1 = WSClient.getQuery("QS_01");
											LinkedHashMap<String, String> db = WSClient.getDBRow(query1);
											String resvStatus = db.get("RESV_STATUS");
											if (resvStatus.equalsIgnoreCase("PROSPECT")) {
												WSClient.writeToReport(LogStatus.PASS,
														"The checkin is cancelled successfully");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"The cancellation of checkin is unsuccessful");
											}

										}
										if (WSAssert.assertIfElementExists(cancelCheckInRes,
												"CancelCheckInResponse_faultcode", true)) {

											String message = WSClient.getElementValue(cancelCheckInRes,
													"CancelCheckInResponse_faultstring", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
										}
										if (WSAssert.assertIfElementExists(cancelCheckInRes, "Result_Text_TextElement",
												true)) {

											String message = WSClient.getElementValue(cancelCheckInRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is :" + message + "</b>");
										}

										if (WSAssert.assertIfElementExists(cancelCheckInRes,
												"CancelCheckInResponse_Result_OperaErrorCode", true)) {

											String code = WSClient.getElementValue(cancelCheckInRes,
													"CancelCheckInResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The error code displayed in the response is :" + code + "</b>");
										}

										if (WSAssert.assertIfElementExists(cancelCheckInRes,
												"CancelCheckInResponse_Result_GDSError", true)) {

											String message = WSClient.getElementValue(cancelCheckInRes,
													"CancelCheckInResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The error displayed in the response is :" + message + "</b>");
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
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,
									"Blocked : Unable to fetch the room");
						}

					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "CancelCheckIn", "OWS", "ResvAdvanced" })

	public void cancelCheckIn_39057() {
		try {

			String testName = "cancelCheckIn_39057";
			WSClient.startTest(testName,
					"verify that error is displayed if trying to cancel checkin of not a inhouse guest",
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

				// Prerequisite 1 - create profile
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					HashMap<String, String> resv = CreateReservation.createReservation("DS_12");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");

						// *****OWS Cancel CheckIn*********//

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String cancelCheckInReq = WSClient.createSOAPMessage("OWSCancelCheckIn", "DS_01");
						String cancelCheckInRes = WSClient.processSOAPMessage(cancelCheckInReq);

						WSAssert.assertIfElementValueEquals(cancelCheckInRes,
								"CancelCheckInResponse_Result_resultStatusFlag", "FAIL", false);
						if (WSAssert.assertIfElementExists(cancelCheckInRes, "CancelCheckInResponse_faultcode", true)) {

							String message = WSClient.getElementValue(cancelCheckInRes,
									"CancelCheckInResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(cancelCheckInRes, "Result_Text_TextElement", true)) {

							String message = WSClient.getElementValue(cancelCheckInRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(cancelCheckInRes,
								"CancelCheckInResponse_Result_OperaErrorCode", true)) {

							String code = WSClient.getElementValue(cancelCheckInRes,
									"CancelCheckInResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error code displayed in the response is :" + code + "</b>");
						}

						if (WSAssert.assertIfElementExists(cancelCheckInRes, "CancelCheckInResponse_Result_GDSError",
								true)) {

							String message = WSClient.getElementValue(cancelCheckInRes,
									"CancelCheckInResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The error displayed in the response is :" + message + "</b>");
						}


					}

				}
			}

		}

		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "CancelCheckIn", "OWS", "ResvAdvanced" })
	public void cancelCheckIn_39058() {
		try {

			String testName = "cancelCheckIn_39058";
			WSClient.startTest(testName,
					"verify that error message is displayed when reservation id is missing in the request",
					"minimumRegression");

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

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
			String cancelCheckInReq = WSClient.createSOAPMessage("OWSCancelCheckIn", "DS_04");
			String cancelCheckInRes = WSClient.processSOAPMessage(cancelCheckInReq);

			WSAssert.assertIfElementValueEquals(cancelCheckInRes, "CancelCheckInResponse_Result_resultStatusFlag",
					"FAIL", false);

			if (WSAssert.assertIfElementExists(cancelCheckInRes, "CancelCheckInResponse_faultcode", true)) {

				String message = WSClient.getElementValue(cancelCheckInRes, "CancelCheckInResponse_faultstring",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
			}
			if (WSAssert.assertIfElementExists(cancelCheckInRes, "Result_Text_TextElement", true)) {

				String message = WSClient.getElementValue(cancelCheckInRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(cancelCheckInRes, "CancelCheckInResponse_Result_OperaErrorCode", true)) {

				String code = WSClient.getElementValue(cancelCheckInRes, "CancelCheckInResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The error code displayed in the response is :" + code + "</b>");
			}

			if (WSAssert.assertIfElementExists(cancelCheckInRes, "CancelCheckInResponse_Result_GDSError", true)) {

				String message = WSClient.getElementValue(cancelCheckInRes, "CancelCheckInResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The error displayed in the response is :" + message + "</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "CancelCheckIn", "OWS", "ResvAdvanced" })
	public void cancelCheckIn_39059() {
		try {

			String testName = "cancelCheckIn_39059";
			WSClient.startTest(testName,
					"verify that error message is displayed in the response of cancel checkin when invalid reservation id is given in request",
					"minimumRegression");

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

			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
			String cancelCheckInReq = WSClient.createSOAPMessage("OWSCancelCheckIn", "DS_05");
			String cancelCheckInRes = WSClient.processSOAPMessage(cancelCheckInReq);

			WSAssert.assertIfElementValueEquals(cancelCheckInRes, "CancelCheckInResponse_Result_resultStatusFlag",
					"FAIL", false);

			if (WSAssert.assertIfElementExists(cancelCheckInRes, "CancelCheckInResponse_faultcode", true)) {

				String message = WSClient.getElementValue(cancelCheckInRes, "CancelCheckInResponse_faultstring",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
			}
			if (WSAssert.assertIfElementExists(cancelCheckInRes, "Result_Text_TextElement", true)) {

				String message = WSClient.getElementValue(cancelCheckInRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO, "<b>The text displayed in the response is :" + message + "</b>");
			}

			if (WSAssert.assertIfElementExists(cancelCheckInRes, "CancelCheckInResponse_Result_OperaErrorCode", true)) {

				String code = WSClient.getElementValue(cancelCheckInRes, "CancelCheckInResponse_Result_OperaErrorCode",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The error code displayed in the response is :" + code + "</b>");
			}

			if (WSAssert.assertIfElementExists(cancelCheckInRes, "CancelCheckInResponse_Result_GDSError", true)) {

				String message = WSClient.getElementValue(cancelCheckInRes, "CancelCheckInResponse_Result_GDSError",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The error displayed in the response is :" + message + "</b>");
			}


		}

		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "CancelCheckIn", "OWS", "ResvAdvanced" })

	public void cancelCheckIn_39060() {
		try {

			String testName = "cancelCheckIn_39060";
			WSClient.startTest(testName,
					"verify that cancellation of checkin of one reservation doesnot affect other reservation in shared reservation. ",
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
				String roomNumber = "";

				// Prerequisite 1 - create profile

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					// Prerequisite 2 Create Reservation
					HashMap<String, String> resv = CreateReservation.createReservation("DS_12");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");

						String operaProfileID1 = CreateProfile.createProfile("DS_01");

						if (!operaProfileID1.equals("error")) {

							WSClient.setData("{var_profileId}", operaProfileID1);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+operaProfileID1+"</b>");

							// Prerequisite -> Create Reservation

							HashMap<String, String> resv1 = CreateReservation.createReservation("DS_12");
							String resvID1 = resv1.get("reservationId");

							if (!resvID1.equals("error")) {

								WSClient.setData("{var_resvId1}", resvID1);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID1+"</b>");

								// Prerequisite 4 : combining Reservation

								String combineReq = WSClient.createSOAPMessage("CombineShareReservations", "DS_01");
								String combineRes = WSClient.processSOAPMessage(combineReq);

								if (WSAssert.assertIfElementExists(combineRes, "CombineShareReservationsRS_Success",
										true)) {

									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully combined reservation.</b>");

									String combineResID = WSClient.getElementValue(combineRes,
											"Reservation_ReservationIDList_UniqueID_ID", XMLType.RESPONSE);
									WSClient.setData("{var_resvId}", combineResID);

									// Prerequisite 3: Fetching available Hotel
									// rooms with room type

									String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_18");
									String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

									if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
											true)
											&& WSAssert.assertIfElementExists(fetchHotelRoomsRes,
													"FetchHotelRoomsRS_HotelRooms_Room", true)) {

										roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
												"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

									} else {

										// Prerequisite 4: Creating a room to
										// assign

										String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
										String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

										if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success",
												true)) {

											roomNumber = WSClient.getElementValue(createRoomReq,
													"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Blocked : Unable to create room");
										}
									}

									if(!roomNumber.equals("")){

										WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched room , RoomNumber : "+roomNumber+"</b>");
										WSClient.setData("{var_roomNumber}", roomNumber);

										// Prerequisite 5: Changing the room status
										// to inspected to assign the room for
										// checking in

										String setHousekeepingRoomStatusReq = WSClient
												.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
										String setHousekeepingRoomStatusRes = WSClient
												.processSOAPMessage(setHousekeepingRoomStatusReq);

										if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
												"SetHousekeepingRoomStatusRS_Success", true)) {

											WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status</b>");

											// Prerequisite 6: Assign Room

											String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
											String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

											if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
													true)) {
												WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room.</b>");


												// Prerequisite 7: CheckIn
												// Reservation


												String checkInReq = WSClient.createSOAPMessage("CheckinReservation",
														"DS_01");
												String checkInRes = WSClient.processSOAPMessage(checkInReq);

												if (WSAssert.assertIfElementExists(checkInRes,
														"CheckinReservationRS_Success", true)) {
													WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in reservation.</b>");

													String checkInReq1 = WSClient.createSOAPMessage("CheckinReservation",
															"DS_02");
													String checkInRes1 = WSClient.processSOAPMessage(checkInReq1);

													if (WSAssert.assertIfElementExists(checkInRes1,
															"CheckinReservationRS_Success", true)) {
														WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in reservation.</b>");
														String query2 = WSClient.getQuery("OWSCancelCheckIn", "QS_01");
														LinkedHashMap<String, String> db2 = WSClient.getDBRow(query2);
														String resvStatus2 = db2.get("RESV_STATUS");
														WSClient.writeToReport(LogStatus.INFO,
																"<b>" + "For first Reservation: " + resvStatus2 + "</b>");

														String query3 = WSClient.getQuery("OWSCancelCheckIn", "QS_02");
														LinkedHashMap<String, String> db3 = WSClient.getDBRow(query3);
														String resvStatus3 = db3.get("RESV_STATUS");
														WSClient.writeToReport(LogStatus.INFO,
																"<b>" + "For second Reservation: " + resvStatus3 + "</b>");

														// *****OWS Cancel
														// CheckIn*********//

														OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
																channelCarrier);
														String cancelCheckInReq = WSClient
																.createSOAPMessage("OWSCancelCheckIn", "DS_01");
														String cancelCheckInRes = WSClient
																.processSOAPMessage(cancelCheckInReq);

														if (WSAssert.assertIfElementValueEquals(cancelCheckInRes,
																"CancelCheckInResponse_Result_resultStatusFlag", "SUCCESS",
																false)) {
															// validation from db
															WSClient.writeToReport(LogStatus.INFO,
																	"<b>" + "For first Reservation" + "</b>");
															String query = WSClient.getQuery("QS_01");
															LinkedHashMap<String, String> db = WSClient.getDBRow(query);
															String resvStatus = db.get("RESV_STATUS");
															if (resvStatus.equalsIgnoreCase("PROSPECT")) {
																WSClient.writeToReport(LogStatus.PASS,
																		"The checkin is cancelled successfully");
															} else {
																WSClient.writeToReport(LogStatus.FAIL,
																		"The cancellation of checkin is unsuccessful");
															}

															WSClient.writeToReport(LogStatus.INFO,
																	"<b>" + "For second Reservation" + "</b>");
															String query1 = WSClient.getQuery("QS_02");
															LinkedHashMap<String, String> db1 = WSClient.getDBRow(query1);
															String resvStatus1 = db1.get("RESV_STATUS");
															if (resvStatus1.equalsIgnoreCase("CHECKED IN")) {
																WSClient.writeToReport(LogStatus.PASS,
																		"Second reservation not cancelled");
															} else {
																WSClient.writeToReport(LogStatus.FAIL,
																		"Second reservation is cancelled");
															}

														}

														if (WSAssert.assertIfElementExists(cancelCheckInRes,
																"CancelCheckInResponse_faultcode", true)) {

															String message = WSClient.getElementValue(cancelCheckInRes,
																	"CancelCheckInResponse_faultstring", XMLType.RESPONSE);
															WSClient.writeToReport(LogStatus.FAIL,
																	"<b>" + message + "</b>");
														}
														if (WSAssert.assertIfElementExists(cancelCheckInRes,
																"Result_Text_TextElement", true)) {

															String message = WSClient.getElementValue(cancelCheckInRes,
																	"Result_Text_TextElement", XMLType.RESPONSE);
															WSClient.writeToReport(LogStatus.INFO,
																	"<b>The text displayed in the response is :" + message
																	+ "</b>");
														}

														if (WSAssert.assertIfElementExists(cancelCheckInRes,
																"CancelCheckInResponse_Result_OperaErrorCode", true)) {

															String code = WSClient.getElementValue(cancelCheckInRes,
																	"CancelCheckInResponse_Result_OperaErrorCode",
																	XMLType.RESPONSE);
															WSClient.writeToReport(LogStatus.INFO,
																	"<b>The error code displayed in the response is :"
																			+ code + "</b>");
														}

														if (WSAssert.assertIfElementExists(cancelCheckInRes,
																"CancelCheckInResponse_Result_GDSError", true)) {

															String message = WSClient.getElementValue(cancelCheckInRes,
																	"CancelCheckInResponse_Result_GDSError",
																	XMLType.RESPONSE);
															WSClient.writeToReport(LogStatus.INFO,
																	"<b>The error displayed in the response is :" + message
																	+ "</b>");
														}

													} else {
														WSClient.writeToReport(LogStatus.WARNING,
																"Blocked : Unable to checkin reservation");
													}
												} else {
													WSClient.writeToReport(LogStatus.WARNING,
															"Blocked : Unable to checkin reservation");
												}
											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Blocked : Unable to assign room");
											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Blocked : Unable to change the status of room to vacant and inspected");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING,
												"Blocked : Unable to fetch room");
									}
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,
											"Blocked : Unable to combine reservation");
								}
							}
						}
					}
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// cancel first reservation
			// checkout second reservation
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
				if(!WSClient.getData("{var_resvId1}").equals(""))
					CheckoutReservation.checkOutReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "CancelCheckIn", "OWS", "ResvAdvanced"})

	public void cancelCheckIn_39061() {
		try {

			String testName = "cancelCheckIn_39061";
			WSClient.startTest(testName,
					"verify that error is displayed in cancel chekin response if some balance is left.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "PaymentMethod", "Cashiers", "TransactionCode" })) {

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
				String roomNumber = "";

				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");

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
					String confirmationNo = resv.get("confirmationId");
					if (!resvID.equals("error")) {

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
						WSClient.setData("{var_confNo}", confirmationNo);

						// Prerequisite 3: Fetching available Hotel rooms with
						// room type

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_18");
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

						if(!roomNumber.equals("")){
							WSClient.setData("{var_roomNumber}", roomNumber);
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room , room number : ,"+roomNumber+"</b>");
							// Prerequisite 5: Changing the room status to inspected
							// to assign the room for checking in

							String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus",
									"DS_01");
							String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

							if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
									"SetHousekeepingRoomStatusRS_Success", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status</b>");
								// Prerequisite 6: Assign Room

								String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
								String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

								if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");
									// Prerequisite 7: CheckIn Reservation

									String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
									String checkInRes = WSClient.processSOAPMessage(checkInReq);

									if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");
										// Prerequisite 8 :Post billing charges

										WSClient.setData("{var_cashierID}",
												OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
										WSClient.setData("{var_trx}",
												OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));
										String postBillingReq = WSClient.createSOAPMessage("PostBillingCharges", "DS_01");
										String postBillingRes = WSClient.processSOAPMessage(postBillingReq);

										String query = WSClient.getQuery("QS_01");
										LinkedHashMap<String, String> count = WSClient.getDBRow(query);
										String c = count.get("COUNT");
										int no = Integer.parseInt(c);
										if (WSAssert.assertIfElementExists(postBillingRes, "PostBillingChargesRS_Success",
												true) && no > 0) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Successfully posted the charges.</b>");


											// *****OWS Cancel CheckIn*********//

											OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
											String cancelCheckInReq = WSClient.createSOAPMessage("OWSCancelCheckIn",
													"DS_01");
											String cancelCheckInRes = WSClient.processSOAPMessage(cancelCheckInReq);

											if (WSAssert.assertIfElementExists(cancelCheckInRes,
													"CancelCheckInResponse_faultcode", true)) {

												String message = WSClient.getElementValue(cancelCheckInRes,
														"CancelCheckInResponse_faultstring", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.FAIL, "<b>" + message + "</b>");
											}
											if (WSAssert.assertIfElementExists(cancelCheckInRes, "Result_Text_TextElement",
													true)) {

												String message = WSClient.getElementValue(cancelCheckInRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>The text displayed in the response is :" + message + "</b>");
											}

											if (WSAssert.assertIfElementExists(cancelCheckInRes,
													"CancelCheckInResponse_Result_OperaErrorCode", true)) {

												String code = WSClient.getElementValue(cancelCheckInRes,
														"CancelCheckInResponse_Result_OperaErrorCode", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>The error code displayed in the response is :" + code + "</b>");
											}

											if (WSAssert.assertIfElementExists(cancelCheckInRes,
													"CancelCheckInResponse_Result_GDSError", true)) {

												String message = WSClient.getElementValue(cancelCheckInRes,
														"CancelCheckInResponse_Result_GDSError", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>The error displayed in the response is :" + message + "</b>");
											}
											WSAssert.assertIfElementValueEquals(cancelCheckInRes,
													"CancelCheckInResponse_Result_resultStatusFlag", "FAIL", false);

										}

										else {
											WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to post charges");
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
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,
									"Blocked : Unable to fetch the room");
						}

					}

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

			// Cancel Reservation
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
