package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;



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

public class PayRouting extends WSSetUp {

	String profileID1 = "", resvID1 = "", profileID2 = "", resvID2 = "";

	// Sanity case1:window routing

	@Test(groups = { "sanity", "PayRouting", "ResvAdvanced", "OWS" })

	public void payRouting_38860() {

		try {

			String testName = "payRouting_38860";
			WSClient.startTest(testName, "Verify that the window payrouting information is fetched for a reservation",
					"sanity");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {

				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resortOperaValue, channel));
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

				WSClient.writeToReport(LogStatus.INFO,
						"<b>Creating profile and reservation each for both the guest and the payee</b>");
				// if (profileID1.equals(""))
				profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,"<b>Profile created for guest------"+profileID1+"</b");
					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					// if (resvID1.equals(""))
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
					WSClient.setData("{var_folio}", "1");
					resvID1 = CreateReservation.createReservation("DS_16").get("reservationId");
					if (!resvID1.equals("error")) {

						WSClient.writeToReport(LogStatus.INFO,"<b>Reservation created for guest-----"+resvID1+"</b");

						WSClient.setData("{var_resvId}", resvID1);

						/************
						 * Prerequisite 1: Create profile
						 *********************************/
						if (profileID2.equals(""))
							profileID2 = CreateProfile.createProfile("DS_01");
						if (!profileID2.equals("error")) {

							WSClient.writeToReport(LogStatus.INFO,"<b>Profile created for payee--------"+profileID2+"</b");
							WSClient.setData("{var_profileId}", profileID2);
							WSClient.setData("{var_payeeProfileId}", profileID2);
							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							if (resvID2.equals(""))
								WSClient.setData("{var_payment}",
										OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_02"));
							WSClient.setData("{var_folio}", "2");
							resvID2 = CreateReservation.createReservation("DS_16").get("reservationId");
							if (!resvID2.equals("error")) {

								WSClient.writeToReport(LogStatus.INFO,"<b>Reservation created for payee------"+resvID2+"</b");

								// WSClient.setData("{var_resvId2}", resvID2);

								WSClient.setData("{var_trxCode}",
										OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
								// WSClient.setData("{var_trxGroup}",
								// OperaPropConfig.getDataSetForCode("TransactionGroup",
								// "DS_03"));
								// WSClient.setData("{var_trxSubGroup}",
								// OperaPropConfig.getDataSetForCode("TransactionSubGroup",
								// "DS_03"));

								/*******************
								 * OPERA CreateRoutingInstructions
								 ************************/
								WSClient.setData("{var_profileId}", profileID1);
								WSClient.setData("{var_resvId}", resvID1);
								String CreateRoutingInstructionsReq = WSClient
										.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
								String CreateRoutingInstructionsRes = WSClient
										.processSOAPMessage(CreateRoutingInstructionsReq);
								if (WSAssert.assertIfElementExists(CreateRoutingInstructionsRes,
										"CreateRoutingInstructionsRS_Success", true)) {

									WSClient.writeToReport(LogStatus.INFO,"<b>Successfully created routing instruction</b");

									/*******************
									 * OWS Pay Routing Operation
									 ************************/

									OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

									String PayRoutingReq = WSClient.createSOAPMessage("OWSPayRouting", "DS_01");
									String PayRoutingRes = WSClient.processSOAPMessage(PayRoutingReq);
									if (WSAssert.assertIfElementExists(PayRoutingRes, "Result_Text_TextElement",
											true)) {

										/*
										 * Verifying that the error message is populated on
										 * the response
										 */

										String message = WSAssert.getElementValue(PayRoutingRes,
												"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The text displayed in the response is----</b> :" + message);
									}
									if (WSAssert.assertIfElementExists(PayRoutingRes,
											"PayRoutingResponse_Result_OperaErrorCode", true)) {

										/*
										 * Verifying whether the error Message is populated
										 * on the response
										 */
										String message1 = WSAssert.getElementValue(PayRoutingRes,
												"PayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);

										WSClient.writeToReport(LogStatus.INFO,
												"<b>The OPERA error displayed in the Response is----</b> : " + message1);



									}
									if (WSAssert.assertIfElementExists(PayRoutingRes,
											"PayRoutingResponse_Result_GDSError", true)) {

										/*
										 * Verifying whether the error Message is populated
										 * on the response
										 */


										String message = WSAssert.getElementValue(PayRoutingRes,
												"PayRoutingResponse_Result_GDSError", XMLType.RESPONSE);

										WSClient.writeToReport(LogStatus.INFO,
												"<b>The GDSError displayed in the  response is :---</b> " + message);

									}
									if (WSAssert.assertIfElementValueEquals(PayRoutingRes,
											"PayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										WSClient.writeToReport(LogStatus.INFO,"<b>Successfully fetched payRouting</b");

										String PayeeOwner = WSClient.getElementValue(PayRoutingRes,
												"PayRoutingResponse_PayMethods_Owner", XMLType.RESPONSE);
										String window = WSClient.getElementValue(PayRoutingRes,
												"PayRoutingResponse_PayRoutings_Window", XMLType.RESPONSE);
										String paymentMethod = WSClient.getElementValue(PayRoutingRes,
												"PayRoutingResponse_PayMethods_PaymentMethod_value", XMLType.RESPONSE);
										WSClient.setData("{var_resvId}", WSClient.getElementValue(PayRoutingReq,
												"ReservationRequest_ReservationID_UniqueID", XMLType.REQUEST));
										String query = WSClient.getQuery("QS_01");

										if (WSAssert.assertEquals(PayeeOwner, WSClient.getDBRow(query).get("owner"),
												true)) {
											WSClient.writeToReport(LogStatus.PASS,
													"<b>Owner---------Expected : </b>"
															+ WSClient.getDBRow(query).get("owner")
															+ "   <b>  Actual : </b>" + PayeeOwner);

										} else {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b>Owner---------Expected : </b>"
															+ WSClient.getDBRow(query).get("owner")
															+ "   <b>  Actual : </b>" + PayeeOwner);
										}
										String query1 = WSClient.getQuery("QS_02");
										if (WSAssert.assertEquals(paymentMethod,
												WSClient.getDBRow(query1).get("PAYMENT_METHOD"), true)) {
											WSClient.writeToReport(LogStatus.PASS,
													"<b>PaymentMethod----Expected : "
															+ WSClient.getDBRow(query1).get("PAYMENT_METHOD")
															+ "   <b>  Actual : </b>" + paymentMethod);

										} else {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b>PaymentMethod----Expected : </b>"
															+ WSClient.getDBRow(query1).get("PAYMENT_METHOD")
															+ "   <b>  Actual : </b>" + paymentMethod);
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
			// logger.endExtentTest();
			try {

				String uname = OPERALib.getUserName();
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_reservation_id}", resvID1);
				if (CancelReservation.cancelReservation("DS_01"))
					WSClient.writeToLog("Reservation cancellation sucessful");
				else
					WSClient.writeToLog("Reservation cancellation failed");
				WSClient.setData("{var_reservation_id}", resvID2);
				if (CancelReservation.cancelReservation("DS_01"))
					WSClient.writeToLog("Reservation cancellation sucessful");
				else
					WSClient.writeToLog("Reservation cancellation failed");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			}
		}

	}

	// MinimumRegression case1:Attaching 2 routing codes
	@Test(groups = { "minimumRegression", "PayRouting", "ResvAdvanced", "OWS" })

	public void payRouting_40181() {

		try {

			String testName = "payRouting_40181";
			WSClient.startTest(testName,
					"Verifying that window payrouting information is fetched when two routing codes are attached",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {

				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resortOperaValue, channel));
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
					WSClient.writeToReport(LogStatus.INFO,"<b>Profile created for guest-----"+profileID1+"</b");
					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
					WSClient.setData("{var_folio}", "1");
					resvID1 = CreateReservation.createReservation("DS_16").get("reservationId");
					if (!resvID1.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO,"<b>Reservation created for guest-----"+resvID1+"</b");

						WSClient.setData("{var_resvId}", resvID1);

						/************
						 * Prerequisite 1: Create profile
						 *********************************/
						if (profileID2.equals(""))
							profileID2 = CreateProfile.createProfile("DS_01");
						if (!profileID2.equals("error")) {

							WSClient.writeToReport(LogStatus.INFO,"<b>Profile created for payee-----"+profileID2+"</b");
							WSClient.setData("{var_profileId}", profileID2);
							WSClient.setData("{var_payeeProfileId}", profileID2);
							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							if (resvID2.equals(""))
								WSClient.setData("{var_payment}",
										OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_02"));
							WSClient.setData("{var_folio}", "2");
							resvID2 = CreateReservation.createReservation("DS_16").get("reservationId");
							if (!resvID2.equals("error")) {

								WSClient.writeToReport(LogStatus.INFO,"<b>Reservation created for payee-----"+resvID2+"</b");

								// WSClient.setData("{var_resvId2}", resvID2);

								WSClient.setData("{var_trxCode}",
										OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));

								WSClient.setData("{var_trxcode2}",
										OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));

								/*******************
								 * OPERA CreateRoutingInstructions
								 ************************/
								WSClient.setData("{var_profileId}", profileID1);
								WSClient.setData("{var_resvId}", resvID1);
								String CreateRoutingInstructionsReq = WSClient
										.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_03");
								String CreateRoutingInstructionsRes = WSClient
										.processSOAPMessage(CreateRoutingInstructionsReq);
								if (WSAssert.assertIfElementExists(CreateRoutingInstructionsRes,
										"CreateRoutingInstructionsRS_Success", true)) {

									WSClient.writeToReport(LogStatus.INFO,"<b>Successfully created routing Instructions</b");

									/*******************
									 * OWS Pay Routing Operation
									 ************************/

									OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

									String PayRoutingReq = WSClient.createSOAPMessage("OWSPayRouting", "DS_01");
									String PayRoutingRes = WSClient.processSOAPMessage(PayRoutingReq);
									if (WSAssert.assertIfElementExists(PayRoutingRes, "Result_Text_TextElement",
											true)) {

										/*
										 * Verifying that the error message is populated on
										 * the response
										 */

										String message = WSAssert.getElementValue(PayRoutingRes,
												"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The text displayed in the response is----</b> :" + message);
									}
									if (WSAssert.assertIfElementExists(PayRoutingRes,
											"PayRoutingResponse_Result_OperaErrorCode", true)) {

										/*
										 * Verifying whether the error Message is populated
										 * on the response
										 */
										String message1 = WSAssert.getElementValue(PayRoutingRes,
												"PayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);

										WSClient.writeToReport(LogStatus.INFO,
												"<b>The OPERA error displayed in the Response is----</b> : " + message1);



									}
									if (WSAssert.assertIfElementExists(PayRoutingRes,
											"PayRoutingResponse_Result_GDSError", true)) {

										/*
										 * Verifying whether the error Message is populated
										 * on the response
										 */


										String message = WSAssert.getElementValue(PayRoutingRes,
												"PayRoutingResponse_Result_GDSError", XMLType.RESPONSE);

										WSClient.writeToReport(LogStatus.INFO,
												"<b>The GDSError displayed in the  response is :---</b> " + message);

									}
									if (WSAssert.assertIfElementValueEquals(PayRoutingRes,
											"PayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										WSClient.writeToReport(LogStatus.INFO,"<b>Successfully fetched payRouting information</b");
										String PayeeOwner = WSClient.getElementValue(PayRoutingRes,
												"PayRoutingResponse_PayMethods_Owner", XMLType.RESPONSE);
										String window = WSClient.getElementValue(PayRoutingRes,
												"PayRoutingResponse_PayRoutings_Window", XMLType.RESPONSE);
										String paymentMethod = WSClient.getElementValue(PayRoutingRes,
												"PayRoutingResponse_PayMethods_PaymentMethod_value", XMLType.RESPONSE);
										WSClient.setData("{var_resvId}", WSClient.getElementValue(PayRoutingReq,
												"ReservationRequest_ReservationID_UniqueID", XMLType.REQUEST));
										String query = WSClient.getQuery("QS_01");

										if (WSAssert.assertEquals(PayeeOwner, WSClient.getDBRow(query).get("owner"),
												true)) {
											WSClient.writeToReport(LogStatus.PASS,
													"<b>Owner---------Expected : </b>"
															+ WSClient.getDBRow(query).get("owner")
															+ "   <b>  Actual : </b>" + PayeeOwner);

										} else {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b>Owner---------Expected : </b>"
															+ WSClient.getDBRow(query).get("owner")
															+ "   <b>  Actual : </b>" + PayeeOwner);
										}

										String query1 = WSClient.getQuery("QS_02");

										if (WSAssert.assertEquals(paymentMethod,
												WSClient.getDBRow(query1).get("PAYMENT_METHOD"), true)) {
											WSClient.writeToReport(LogStatus.PASS,
													"<b>PaymentMethod----Expected : "
															+ WSClient.getDBRow(query1).get("PAYMENT_METHOD")
															+ "   <b>  Actual : </b>" + paymentMethod);

										} else {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b>PaymentMethod----Expected : </b>"
															+ WSClient.getDBRow(query1).get("PAYMENT_METHOD")
															+ "   <b>  Actual : </b>" + paymentMethod);
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
			// logger.endExtentTest();
			try {

				String uname = OPERALib.getUserName();
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_reservation_id}", resvID1);
				if (CancelReservation.cancelReservation("DS_01"))
					WSClient.writeToLog("Reservation cancellation sucessful");
				else
					WSClient.writeToLog("Reservation cancellation failed");
				WSClient.setData("{var_reservation_id}", resvID2);
				if (CancelReservation.cancelReservation("DS_01"))
					WSClient.writeToLog("Reservation cancellation sucessful");
				else
					WSClient.writeToLog("Reservation cancellation failed");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			}
		}

	}

	@Test(groups = { "minimumRegression", "PayRouting", "ResvAdvanced", "OWS", "payrouting_41169" })
	/**
	 * Verify that Room PayRouting information is fetched correctly
	 */

	public void payrouting_41169() throws Exception {
		try {
			String testName = "payrouting_41169";
			WSClient.startTest(testName,
					"Verify that Room Routing information is fetched correctly",
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
			//String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resortOperaValue, channel));
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod",
					"SourceCode", "MarketCode",  "TransactionCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code,Transaction Code,Transaction Group,Transaction Sub Group
				 *********************************/
				//WSClient.setData("{VAR_RATEPLANCODE}", "CKRRC");
				//WSClient.setData("{VAR_ROOMTYPE}", "OWSRT");
				//WSClient.setData("{var_roomType}", "OWSRT");
				//WSClient.setData("{var_RoomType}", "OWSRT");
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID1.equals(""))
					profileID1 = CreateProfile.createProfile("DS_01");

				if (!profileID1.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO,"<b>Profile created for guest----"+profileID1+"</b");

					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID1 = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID1.equals("error"))

					{
						WSClient.writeToReport(LogStatus.INFO,"<b>Reservation created for guest-----"+resvID1+"</b");

						/************
						 * Prerequisite 3: Create profile for payee
						 *********************************/
						if (profileID2.equals(""))
							profileID2 = CreateProfile.createProfile("DS_01");

						if (!profileID2.equals("error")) {

							WSClient.writeToReport(LogStatus.INFO,"<b>Profile created for payee-----"+profileID2+"</b");

							WSClient.setData("{var_profileId}", profileID2);

							/*******************
							 * Prerequisite 4:Create a Reservation for payee
							 ************************/

							resvID2 = CreateReservation.createReservation("DS_13").get("reservationId");

							if (!resvID2.equals("error")) {

								WSClient.writeToReport(LogStatus.INFO,"<b>Reservation created for payee-------"+resvID2+"</b");

								String roomNumber = null;
								WSClient.setData("{var_profileId}", profileID2);
								WSClient.setData("{var_resvId}", resvID2);
								WSClient.setData("{var_payeeResvId}",resvID2);
								/******
								 * Prerequisite 5: Fetching available Hotel
								 * rooms with room type
								 ******/

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
										&& WSAssert.assertIfElementExists(fetchHotelRoomsRes,
												"FetchHotelRoomsRS_HotelRooms_Room", true)) {
									WSClient.writeToReport(LogStatus.INFO,"<b>Successfully Fetched available rooms to assign to payee</b");

									roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
											"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

								} else {

									/*****
									 * Prerequisite 6: Creating a room to assign
									 *******/

									String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
									String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

									if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

										WSClient.writeToReport(LogStatus.INFO,"<b>Created room to assign to payee</b");

										roomNumber = WSClient.getElementValue(createRoomReq,
												"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
									}
								}

								/****
								 * Prerequisite 7: Changing the room status to
								 * inspected to assign the room for checking in
								 *****/

								WSClient.setData("{var_roomNumber}", roomNumber);
								String setHousekeepingRoomStatusReq = WSClient
										.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
								String setHousekeepingRoomStatusRes = WSClient
										.processSOAPMessage(setHousekeepingRoomStatusReq);

								if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
										"SetHousekeepingRoomStatusRS_Success", true)) {

									WSClient.writeToReport(LogStatus.INFO,"<b>Changed the created room status to Inspected </b");

									/***** Prerequisite 8: Assign Room *****/

									String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
									String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

									if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

										WSClient.writeToReport(LogStatus.INFO,"<b>Assigned room to payee</b");

										/*****
										 * Prerequisite 9: CheckIn Reservation
										 *****/

										String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
										String checkInRes = WSClient.processSOAPMessage(checkInReq);

										if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success",
												true)) {

											WSClient.writeToReport(LogStatus.INFO,"<b>Checked in the payee</b");


											WSClient.setData("{var_payeeProfileId}", profileID2);
											WSClient.setData("{var_payeeResvId}", resvID2);
											WSClient.setData("{var_resvId}", resvID1);
											WSClient.setData("{var_profileId}", profileID1);
											WSClient.setData("{var_trxCode}",
													OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
											WSClient.setData("{var_trxGroup}",
													OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
											WSClient.setData("{var_trxSubGroup}",
													OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));

											/*****
											 * Prerequisite 10: Opera Create
											 * Routing Instructions
											 ******/

											String payroutReq = WSClient
													.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_02");
											String payroutRes = WSClient.processSOAPMessage(payroutReq);

											if (WSAssert.assertIfElementExists(payroutRes,
													"CreateRoutingInstructionsRS_Success", true)) {

												WSClient.writeToReport(LogStatus.INFO,"<b>Successfully created routing instruction</b");

												OWSLib.setOWSHeader(uname, pwd, resort, channelType,
														channelCarrier);


												String PayRoutingReq = WSClient.createSOAPMessage("OWSPayRouting", "DS_01");
												String PayRoutingRes = WSClient.processSOAPMessage(PayRoutingReq);
												if (WSAssert.assertIfElementExists(PayRoutingRes, "Result_Text_TextElement",
														true)) {

													/*
													 * Verifying that the error message is populated on
													 * the response
													 */

													String message = WSAssert.getElementValue(PayRoutingRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>The error displayed in the response is----</b> :" + message);
												}
												if (WSAssert.assertIfElementExists(PayRoutingRes,
														"PayRoutingResponse_Result_OperaErrorCode", true)) {

													/*
													 * Verifying whether the error Message is populated
													 * on the response
													 */
													String message1 = WSAssert.getElementValue(PayRoutingRes,
															"PayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);

													WSClient.writeToReport(LogStatus.INFO,
															"<b>The OPERA error displayed in the Response is----</b> : " + message1);



												}
												if (WSAssert.assertIfElementExists(PayRoutingRes,
														"PayRoutingResponse_Result_GDSError", true)) {

													/*
													 * Verifying whether the error Message is populated
													 * on the response
													 */


													String message = WSAssert.getElementValue(PayRoutingRes,
															"PayRoutingResponse_Result_GDSError", XMLType.RESPONSE);

													WSClient.writeToReport(LogStatus.INFO,
															"<b>The GDSError displayed in the  response is :---</b> " + message);

												}
												if (WSAssert.assertIfElementValueEquals(PayRoutingRes,
														"PayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

													WSClient.writeToReport(LogStatus.INFO,"<b>Successfully fetched routing information</b");

													String PayeeOwner = WSClient.getElementValue(PayRoutingRes,
															"PayRoutingResponse_PayMethods_Owner", XMLType.RESPONSE);
													String window = WSClient.getElementValue(PayRoutingRes,
															"PayRoutingResponse_PayRoutings_Window", XMLType.RESPONSE);
													String paymentMethod = WSClient.getElementValue(PayRoutingRes,
															"PayRoutingResponse_PayMethods_PaymentMethod_value", XMLType.RESPONSE);
													WSClient.setData("{var_resvId}", WSClient.getElementValue(PayRoutingReq,
															"ReservationRequest_ReservationID_UniqueID", XMLType.REQUEST));
													WSClient.setData("{var_resort}", resortOperaValue);
													String query = WSClient.getQuery("QS_04");

													if (WSAssert.assertEquals(PayeeOwner, WSClient.getDBRow(query).get("owner"),
															true)) {
														WSClient.writeToReport(LogStatus.PASS,
																"<b>Owner---------Expected : </b>"
																		+ WSClient.getDBRow(query).get("owner")
																		+ "   <b>  Actual : </b>" + PayeeOwner);

													} else {
														WSClient.writeToReport(LogStatus.FAIL,
																"<b>Owner---------Expected : </b>"
																		+ WSClient.getDBRow(query).get("owner")
																		+ "   <b>  Actual : </b>" + PayeeOwner);
													}
													String query1 = WSClient.getQuery("QS_02");
													if (WSAssert.assertEquals(paymentMethod,
															WSClient.getDBRow(query1).get("PAYMENT_METHOD"), true)) {
														WSClient.writeToReport(LogStatus.PASS,
																"<b>PaymentMethod----Expected : "
																		+ WSClient.getDBRow(query1).get("PAYMENT_METHOD")
																		+ "   <b>  Actual : </b>" + paymentMethod);

													} else {
														WSClient.writeToReport(LogStatus.FAIL,
																"<b>PaymentMethod----Expected : </b>"
																		+ WSClient.getDBRow(query1).get("PAYMENT_METHOD")
																		+ "   <b>  Actual : </b>" + paymentMethod);
													}
												}



											}
										}

										else {
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
							} else
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation for Payee");

						} else
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");

					} else
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation");

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile ");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID1.equals(""))
				CancelReservation.cancelReservation("DS_02");

			if (!resvID2.equals("")) {
				//				WSClient.setData("{var_resvId}", resvID2);
				//				String cancelCheckReq = WSClient.createSOAPMessage("CheckoutReservation", "DS_01");
				//				String cancelCheckRes = WSClient.processSOAPMessage(cancelCheckReq);
				//
				//				if (WSAssert.assertIfElementExists(cancelCheckRes, "CheckoutReservationRS_Success", true)) {
				//
				//				} else
				//					WSClient.writeToReport(LogStatus.INFO, "Payee Reservation is not checked out!");
				WSClient.setData("{var_resvId}", resvID2);
				CheckoutReservation.checkOutReservation("DS_01");
			}


		}
	}



	//MinimumRegression case:3--keeping keytrack2


	@Test(groups = { "minimumRegression", "PayRouting", "ResvAdvanced", "OWS" })

	public void payRouting_41270() {

		try {

			String testName = "payRouting_41270";
			WSClient.startTest(testName, "Verify that the payrouting information is fetched for a reservation keeping key2track",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {

				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);
				String owsresort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_extResort}", owsresort);
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resortOperaValue, channel));
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

				WSClient.writeToReport(LogStatus.INFO,
						"<b>Creating profile and reservation each for both the guest and the payee</b>");
				if (profileID1.equals(""))
					profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,"<b>Profile created for guest"+profileID1+"</b");
					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					// if (resvID1.equals(""))
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
					WSClient.setData("{var_folio}", "1");
					resvID1 = CreateReservation.createReservation("DS_16").get("reservationId");
					if (!resvID1.equals("error")) {

						WSClient.writeToReport(LogStatus.INFO,"<b>Reservation created for guest"+resvID1+"</b");

						WSClient.setData("{var_resvId}", resvID1);
						//WSClient.setData("{var_keyTrack2}", "2114000000000000000566666666666666666666666666666666666666666");
						WSClient.setData("{var_keyTrack2}", "21140000000000000005");

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						String req_setKeyData = WSClient.createSOAPMessage("OWSSetKeyData", "DS_01");
						String res_setKeyData = WSClient.processSOAPMessage(req_setKeyData);
						if (WSAssert.assertIfElementValueEquals(res_setKeyData, "SetKeyDataResponse_Result_resultStatusFlag","SUCCESS", true)) {

							/************
							 * Prerequisite 1: Create profile
							 *********************************/
							if (profileID2.equals(""))
								profileID2 = CreateProfile.createProfile("DS_01");
							if (!profileID2.equals("error")) {

								WSClient.writeToReport(LogStatus.INFO,"<b>Profile created for payee"+profileID2+"</b");
								WSClient.setData("{var_profileId}", profileID2);
								WSClient.setData("{var_payeeProfileId}", profileID2);
								/*******************
								 * Prerequisite 2:Create a Reservation
								 ************************/

								if (resvID2.equals(""))
									WSClient.setData("{var_payment}",
											OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_02"));
								WSClient.setData("{var_folio}", "2");
								resvID2 = CreateReservation.createReservation("DS_16").get("reservationId");
								if (!resvID2.equals("error")) {

									WSClient.writeToReport(LogStatus.INFO,"<b>Reservation created for payee"+resvID2+"</b");

									// WSClient.setData("{var_resvId2}", resvID2);


									WSClient.setData("{var_trxCode}",
											OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
									// WSClient.setData("{var_trxGroup}",
									// OperaPropConfig.getDataSetForCode("TransactionGroup",
									// "DS_03"));
									// WSClient.setData("{var_trxSubGroup}",
									// OperaPropConfig.getDataSetForCode("TransactionSubGroup",
									// "DS_03"));

									/*******************
									 * OPERA CreateRoutingInstructions
									 ************************/
									WSClient.setData("{var_profileId}", profileID1);
									WSClient.setData("{var_resvId}", resvID1);
									String CreateRoutingInstructionsReq = WSClient
											.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
									String CreateRoutingInstructionsRes = WSClient
											.processSOAPMessage(CreateRoutingInstructionsReq);
									if (WSAssert.assertIfElementExists(CreateRoutingInstructionsRes,
											"CreateRoutingInstructionsRS_Success", true)) {

										WSClient.writeToReport(LogStatus.INFO,"<b>Successfully created routing instruction</b");

										/*******************
										 * OWS Pay Routing Operation
										 ************************/

										OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

										String PayRoutingReq = WSClient.createSOAPMessage("OWSPayRouting", "DS_06");
										String PayRoutingRes = WSClient.processSOAPMessage(PayRoutingReq);
										if (WSAssert.assertIfElementExists(PayRoutingRes, "Result_Text_TextElement",
												true)) {

											/*
											 * Verifying that the error message is populated on
											 * the response
											 */

											String message = WSAssert.getElementValue(PayRoutingRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is----</b> :" + message);
										}
										if (WSAssert.assertIfElementExists(PayRoutingRes,
												"PayRoutingResponse_Result_OperaErrorCode", true)) {

											/*
											 * Verifying whether the error Message is populated
											 * on the response
											 */
											String message1 = WSAssert.getElementValue(PayRoutingRes,
													"PayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);

											WSClient.writeToReport(LogStatus.INFO,
													"<b>The OPERA error displayed in the Response is----</b> : " + message1);



										}
										if (WSAssert.assertIfElementExists(PayRoutingRes,
												"PayRoutingResponse_Result_GDSError", true)) {

											/*
											 * Verifying whether the error Message is populated
											 * on the response
											 */


											String message = WSAssert.getElementValue(PayRoutingRes,
													"PayRoutingResponse_Result_GDSError", XMLType.RESPONSE);

											WSClient.writeToReport(LogStatus.INFO,
													"<b>The GDSError displayed in the  response is :---</b> " + message);

										}
										if (WSAssert.assertIfElementValueEquals(PayRoutingRes,
												"PayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

											WSClient.writeToReport(LogStatus.INFO,"<b>Successfully fetched payRouting information</b");
											String PayeeOwner = WSClient.getElementValue(PayRoutingRes,
													"PayRoutingResponse_PayMethods_Owner", XMLType.RESPONSE);
											String window = WSClient.getElementValue(PayRoutingRes,
													"PayRoutingResponse_PayRoutings_Window", XMLType.RESPONSE);
											String paymentMethod = WSClient.getElementValue(PayRoutingRes,
													"PayRoutingResponse_PayMethods_PaymentMethod_value", XMLType.RESPONSE);
											WSClient.setData("{var_resvId}", WSClient.getElementValue(PayRoutingReq,
													"ReservationRequest_ReservationID_UniqueID", XMLType.REQUEST));
											String query = WSClient.getQuery("QS_01");

											if (WSAssert.assertEquals(PayeeOwner, WSClient.getDBRow(query).get("owner"),
													true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b>Owner---------Expected : </b>"
																+ WSClient.getDBRow(query).get("owner")
																+ "   <b>  Actual : </b>" + PayeeOwner);

											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b>Owner---------Expected : </b>"
																+ WSClient.getDBRow(query).get("owner")
																+ "   <b>  Actual : </b>" + PayeeOwner);
											}
											String query1 = WSClient.getQuery("QS_02");
											if (WSAssert.assertEquals(paymentMethod,
													WSClient.getDBRow(query1).get("PAYMENT_METHOD"), true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b>PaymentMethod----Expected : "
																+ WSClient.getDBRow(query1).get("PAYMENT_METHOD")
																+ "   <b>  Actual : </b>" + paymentMethod);

											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b>PaymentMethod----Expected : </b>"
																+ WSClient.getDBRow(query1).get("PAYMENT_METHOD")
																+ "   <b>  Actual : </b>" + paymentMethod);
											}

										}

									}

								}

							}

						}
						else
							WSClient.writeToReport(LogStatus.WARNING,"Not able to assign keytrack with OWSSetKeyData");

					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
			try {

				String uname = OPERALib.getUserName();
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_reservation_id}", resvID1);
				if (CancelReservation.cancelReservation("DS_01"))
					WSClient.writeToLog("Reservation cancellation sucessful");
				else
					WSClient.writeToLog("Reservation cancellation failed");
				WSClient.setData("{var_reservation_id}", resvID2);
				if (CancelReservation.cancelReservation("DS_01"))
					WSClient.writeToLog("Reservation cancellation sucessful");
				else
					WSClient.writeToLog("Reservation cancellation failed");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			}
		}

	}

	//minimumRegression: case4:invalid resort id


	@Test(groups = { "minimumRegression", "PayRouting", "ResvAdvanced", "OWS" })

	public void payRouting_38861() {

		try {

			String testName = "payRouting_38861";
			WSClient.startTest(testName, "Verify that an error message populates when invalid resortId is passed",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TransactionCode" })) {

				String resortOperaValue = OPERALib.getResort();

				String channel = OWSLib.getChannel();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String uname = OPERALib.getUserName();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resortOperaValue, channel));
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

				WSClient.writeToReport(LogStatus.INFO,
						"<b>Creating profile and reservation each for both the guest and the payee</b>");
				if (profileID1.equals(""))
					profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO,"<b>Profile created for guest------"+profileID1+"</b");
					WSClient.setData("{var_profileId}", profileID1);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					// if (resvID1.equals(""))
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
					WSClient.setData("{var_folio}", "1");
					resvID1 = CreateReservation.createReservation("DS_16").get("reservationId");
					if (!resvID1.equals("error")) {

						WSClient.writeToReport(LogStatus.INFO,"<b>Reservation created for guest------"+resvID1+"</b");

						WSClient.setData("{var_resvId}", resvID1);

						/************
						 * Prerequisite 1: Create profile
						 *********************************/
						if (profileID2.equals(""))
							profileID2 = CreateProfile.createProfile("DS_01");
						if (!profileID2.equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,"<b>Profile created for payee-----"+profileID2+"</b");
							WSClient.setData("{var_profileId}", profileID2);
							WSClient.setData("{var_payeeProfileId}", profileID2);
							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							if (resvID2.equals(""))
								WSClient.setData("{var_payment}",
										OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_02"));
							WSClient.setData("{var_folio}", "2");
							resvID2 = CreateReservation.createReservation("DS_16").get("reservationId");
							if (!resvID2.equals("error")) {

								WSClient.writeToReport(LogStatus.INFO,"<b>Reservation created for payee-----"+resvID2+"</b");

								// WSClient.setData("{var_resvId2}", resvID2);

								WSClient.setData("{var_trxCode}",
										OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
								// WSClient.setData("{var_trxGroup}",
								// OperaPropConfig.getDataSetForCode("TransactionGroup",
								// "DS_03"));
								// WSClient.setData("{var_trxSubGroup}",
								// OperaPropConfig.getDataSetForCode("TransactionSubGroup",
								// "DS_03"));

								/*******************
								 * OPERA CreateRoutingInstructions
								 ************************/
								WSClient.setData("{var_profileId}", profileID1);
								WSClient.setData("{var_resvId}", resvID1);
								String CreateRoutingInstructionsReq = WSClient
										.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
								String CreateRoutingInstructionsRes = WSClient
										.processSOAPMessage(CreateRoutingInstructionsReq);
								if (WSAssert.assertIfElementExists(CreateRoutingInstructionsRes,
										"CreateRoutingInstructionsRS_Success", true)) {

									WSClient.writeToReport(LogStatus.INFO,"<b>Successfully created routing instruction</b");

									/*******************
									 * OWS Pay Routing Operation
									 ************************/

									OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

									String PayRoutingReq = WSClient.createSOAPMessage("OWSPayRouting", "DS_07");
									String PayRoutingRes = WSClient.processSOAPMessage(PayRoutingReq);

									if (WSAssert.assertIfElementExists(PayRoutingRes,
											"PayRoutingResponse_Result_OperaErrorCode", true)) {

										/*
										 * Verifying whether the error Message is populated
										 * on the response
										 */
										String message1 = WSAssert.getElementValue(PayRoutingRes,
												"PayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);

										WSClient.writeToReport(LogStatus.INFO,
												"<b>The OPERA error displayed in the Response is----</b> : " + message1);



									}
									if (WSAssert.assertIfElementExists(PayRoutingRes,
											"PayRoutingResponse_Result_GDSError", true)) {

										/*
										 * Verifying whether the error Message is populated
										 * on the response
										 */


										String message = WSAssert.getElementValue(PayRoutingRes,
												"PayRoutingResponse_Result_GDSError", XMLType.RESPONSE);

										WSClient.writeToReport(LogStatus.INFO,
												"<b>The GDSError displayed in the  response is :---</b> " + message);

									}

									if (WSAssert.assertIfElementValueEquals(PayRoutingRes, "PayRoutingResponse_Result_resultStatusFlag","FAIL", false))
									{
										WSClient.writeToReport(LogStatus.PASS , "PayRouting cant be done without valid ResortID");

									}
									else if (WSAssert.assertIfElementValueEquals(PayRoutingRes,
											"PayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										WSClient.writeToReport(LogStatus.FAIL , "PayRouting is successful!!!ERROR!!!!!");

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
			// logger.endExtentTest();
			try {

				String uname = OPERALib.getUserName();
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_reservation_id}", resvID1);
				if (CancelReservation.cancelReservation("DS_01"))
					WSClient.writeToLog("Reservation cancellation sucessful");
				else
					WSClient.writeToLog("Reservation cancellation failed");
				WSClient.setData("{var_reservation_id}", resvID2);
				if (CancelReservation.cancelReservation("DS_01"))
					WSClient.writeToLog("Reservation cancellation sucessful");
				else
					WSClient.writeToLog("Reservation cancellation failed");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	//MinimumRegression:case5---Passing invalid ReservationID in request

	@Test(groups = { "minimumRegression", "PayRouting", "ResvAdvanced", "OWS"})

	public void payRouting_38862() {

		try {
			String testName = "payRouting_38862";
			WSClient.startTest(testName, "Verify that error message is populated when invalid ReservationID is passed ", "minimumRegression");


			String resort = OPERALib.getResort();
			String resortOperaValue=OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resortOperaValue, channel));


			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
			String payRoutingReq = WSClient.createSOAPMessage("OWSPayRouting", "DS_03");
			String payRoutingRes = WSClient.processSOAPMessage(payRoutingReq);
			if (WSAssert.assertIfElementExists(payRoutingRes, "Result_Text_TextElement",
					true)) {

				/*
				 * Verifying that the error message is populated on
				 * the response
				 */

				String message = WSAssert.getElementValue(payRoutingRes,
						"Result_Text_TextElement", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The error displayed in the response is----</b> :" + message);
			}
			if (WSAssert.assertIfElementExists(payRoutingRes,
					"PayRoutingResponse_Result_OperaErrorCode", true)) {

				/*
				 * Verifying whether the error Message is populated
				 * on the response
				 */
				String message1 = WSAssert.getElementValue(payRoutingRes,
						"PayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);

				WSClient.writeToReport(LogStatus.INFO,
						"<b>The OPERA error displayed in the Response is----</b> : " + message1);



			}
			if (WSAssert.assertIfElementExists(payRoutingRes,
					"PayRoutingResponse_Result_GDSError", true)) {

				/*
				 * Verifying whether the error Message is populated
				 * on the response
				 */


				String message = WSAssert.getElementValue(payRoutingRes,
						"PayRoutingResponse_Result_GDSError", XMLType.RESPONSE);

				WSClient.writeToReport(LogStatus.INFO,
						"<b>The GDSError displayed in the  response is :---</b> " + message);

			}

			if (WSAssert.assertIfElementValueEquals(payRoutingRes, "PayRoutingResponse_Result_resultStatusFlag","FAIL", false))
			{
				WSClient.writeToReport(LogStatus.PASS , "PayRouting cant be done without valid ReservationID");

			}
			else if(WSAssert.assertIfElementValueEquals(payRoutingRes, "PayRoutingResponse_Result_resultStatusFlag","PASS", true))

				WSClient.writeToReport(LogStatus.FAIL , "PayRouting is successful!ERROR");







		}
		catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}


	}
}
