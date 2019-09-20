package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;

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

public class UpdatePayRouting extends WSSetUp {
	String profileID = null, resvID = null, payeeProfileID = null, payeeResvID = null, newReservationId = null, newProfileId = null;



	@Test(groups = { "sanity", "UpdatePayRouting", "ResvAdvanced", "OWS" })
	/**
	 * Verify that OWS Update Pay Routing is working i.e to update the routing
	 * instruction to one profile to another profile successfully.
	 *
	 * PreRequisites Required: --> Three Profiles are created -->One
	 * Reservations are created -> There should be market codes,source
	 * codes,routing instructions,Payment method,rate codes,transaction
	 * codes,transaction sub group,transaction groups to be available.
	 *
	 */

	public void updatePayrouting_39580() throws Exception {
		try {
			String testName = "updatePayrouting_39580";
			WSClient.startTest(testName, "Verify that payrouting details are getting updated.", "sanity");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation created for Guest");

					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");

					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID.equals("error") || !resvID.equals(null))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvID + "</b>");

						/************
						 * Prerequisite 3: Create profile for Payee
						 *****************/

						WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created for payee </b>");

						payeeProfileID = CreateProfile.createProfile("DS_01");

						if (!payeeProfileID.equals("error")) {

							WSClient.writeToReport(LogStatus.INFO, "<b>Payee Profile ID : " + payeeProfileID + "</b>");

							WSClient.setData("{var_profileId}", payeeProfileID);

							WSClient.setData("{var_payeeProfileId}", payeeProfileID);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_profileId}", profileID);
							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
							WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
							WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));

							/*** Prerequisite 4: Create Routing Instructions **/

							String payroutReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
							String payroutRes = WSClient.processSOAPMessage(payroutReq);

							if (WSAssert.assertIfElementExists(payroutRes, "CreateRoutingInstructionsRS_Success", true)) {

								String updateQuery = WSClient.getQuery("OWSUpdatePayRouting", "QS_01");
								String routeId = WSClient.getDBRow(updateQuery).get("ROUTING_INSTRUCTIONS_ID");
								String folio = WSClient.getDBRow(updateQuery).get("FOLIO_VIEW");
								WSClient.writeToReport(LogStatus.INFO, "<b>Routing Instruction ID :" + routeId + "</b>");

								WSClient.setData("{var_folio}", folio);
								WSClient.setData("{var_routeId}", routeId);
								WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));

								/************
								 * Prerequisite 5: Create profile to attach to a
								 * new Payee
								 *********************************/

								WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created For New Payee </b>");

								newProfileId = CreateProfile.createProfile("DS_01");

								if (!newProfileId.equals("error")) {

									WSClient.writeToReport(LogStatus.INFO, "<b> Payee New Profile ID : " + newProfileId + "</b>");
									WSClient.setData("{var_payeeProfileId}", newProfileId);

									OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

									/*******************
									 * OWS Update PayRouting Operation
									 ************************/

									String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_02");
									String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

									if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
										if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

											String trxCode = WSClient.getElementValue(updateResvResq, "RoutingCodes_RoutingInstructionCode_InstructionCode", XMLType.REQUEST);
											System.out.println(trxCode);
											String query = WSClient.getQuery("QS_02");
											System.out.println(query);
											LinkedHashMap<String, String> db = WSClient.getDBRow(query);
											System.out.println(db);

											/*****
											 * Validation against the Database
											 *******/

											if (WSAssert.assertEquals(newProfileId, db.get("BILL_TO_NAME_ID"), true)) {
												WSClient.writeToReport(LogStatus.PASS, "New Profile ID -> 	Expected :		" + newProfileId + "		 Actual :	 " + db.get("BILL_TO_NAME_ID"));

											} else {
												WSClient.writeToReport(LogStatus.FAIL, "New Profile ID ->	 Expected : 		" + newProfileId + " 		Actual : 	 " + db.get("BILL_TO_NAME_ID"));

											}

											if (WSAssert.assertEquals(trxCode, db.get("TRX_CODE"), true)) {
												WSClient.writeToReport(LogStatus.PASS, "Transaction Code -> 	Expected :		" + trxCode + "		 Actual :	 " + db.get("TRX_CODE"));

											} else {
												WSClient.writeToReport(LogStatus.FAIL, "Transaction Code  ->	 Expected : 		" + trxCode + " 		Actual : 	 " + db.get("TRX_CODE"));

											}

										} else
											WSClient.writeToReport(LogStatus.FAIL, "The update payrouting operation has failed!");
									} else
										WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

									if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

										/****
										 * Verifying that the error message is
										 * populated on the response
										 ********/

										String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
										if (message != "*null*")
											WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
									}

									if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

										/****
										 * Verifying whether the error Message
										 * is populated on the response
										 ****/

										String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
										if (message != "*null*")
											WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
									} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

										/****
										 * Verifying whether the error Message
										 * is populated on the response
										 ****/

										String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										if (message != "*null*")
											WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
									}

									if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

										/****
										 * Verifying whether the error Message
										 * is populated on the response
										 ****/

										String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
										if (message != "*null*")
											WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
									}

								} else
									WSClient.writeToReport(LogStatus.PASS, "Blocked : Create Profile for new payee failed!");

							} else
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Pay routings!");

						} else
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");

					} else
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation");

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile ");

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Property Config Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error") || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePayRouting", "ResvAdvanced", "OWS" })
	/**
	 * Verify that Routing Instruction type assigned to a reservation is changed
	 * from type Window to Room successfully. PreRequisites Required: --> Three
	 * Profiles are created -->Three Reservations are created -> There should be
	 * market codes,source codes,routing instructions,Payment method,rate
	 * codes,transaction codes,transaction sub group,transaction groups to be
	 * available. ->New payee Reservation should be checked in.
	 *
	 */

	public void updatePayrouting_41244() throws Exception {
		try {
			String testName = "updatePayrouting_41244";
			WSClient.startTest(testName, "Verify that Routing Instruction type assigned to a reservation is changed from type Window to Room successfully.", "minimumRegression");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code,Transaction Code,Transaction Group,Transaction Sub Group
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation are created for Guest.</b>");

				profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID.equals("error") || !resvID.equals(null))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvID + "</b>");

						/************
						 * Prerequisite 3: Create profile for payee
						 *********************************/

						WSClient.writeToReport(LogStatus.INFO, "<b>Profile is create for Payee. </b>");

						payeeProfileID = CreateProfile.createProfile("DS_01");

						if (!payeeProfileID.equals("error")) {

							WSClient.writeToReport(LogStatus.INFO, "<b>Payee Profile ID : " + payeeProfileID + "</b>");

							WSClient.setData("{var_profileId}", payeeProfileID);

							WSClient.setData("{var_payeeProfileId}", payeeProfileID);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_profileId}", profileID);
							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
							WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
							WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));

							/*****
							 * Prerequisite 4: Opera Create Routing Instructions
							 ******/

							String payroutReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
							String payroutRes = WSClient.processSOAPMessage(payroutReq);

							if (WSAssert.assertIfElementExists(payroutRes, "CreateRoutingInstructionsRS_Success", true)) {

								String updateQuery = WSClient.getQuery("OWSUpdatePayRouting", "QS_01");
								String routeId = WSClient.getDBRow(updateQuery).get("ROUTING_INSTRUCTIONS_ID");

								WSClient.writeToReport(LogStatus.INFO, "<b>Routing Instruction ID :" + routeId + "</b>");

								WSClient.setData("{var_routeId}", routeId);
								WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));
								WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_04"));
								WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_04"));

								/************
								 * Prerequisite 5: Create profile for new Payee
								 *********************************/
								WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation are created for new Payee.</b>");

								newProfileId = CreateProfile.createProfile("DS_01");

								WSClient.writeToReport(LogStatus.INFO, "<b> New Profile ID : " + newProfileId + "</b>");
								if (!newProfileId.equals("error")) {

									/*******************
									 * Prerequisite 12:Create a Reservation for
									 * new Payee
									 ************************/

									WSClient.setData("{var_profileId}", newProfileId);
									newReservationId = CreateReservation.createReservation("DS_13").get("reservationId");

									if (!newReservationId.equals("error") || !newReservationId.equals(null)) {

										WSClient.writeToReport(LogStatus.INFO, "<b>New Reservation ID :" + newReservationId + "</b>");

										WSClient.writeToReport(LogStatus.INFO, "<b>Checking In the new payee reservation </b>");

										String roomNumber1 = null;
										WSClient.setData("{var_profileId}", newProfileId);
										WSClient.setData("{var_resvId}", newReservationId);
										/******
										 * Prerequisite 13: Fetching available
										 * Hotel rooms with room type
										 ******/

										String fetchHotelRoomsReq1 = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
										String fetchHotelRoomsRes1 = WSClient.processSOAPMessage(fetchHotelRoomsReq1);

										if (WSAssert.assertIfElementExists(fetchHotelRoomsRes1, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes1, "FetchHotelRoomsRS_HotelRooms_Room", true)) {

											roomNumber1 = WSClient.getElementValue(fetchHotelRoomsRes1, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

										} else {

											/*****
											 * Prerequisite 6: Creating a room
											 * to assign
											 *******/

											String createRoomReq1 = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
											String createRoomRes1 = WSClient.processSOAPMessage(createRoomReq1);

											if (WSAssert.assertIfElementExists(createRoomRes1, "CreateRoomRS_Success", true)) {

												roomNumber1 = WSClient.getElementValue(createRoomReq1, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

											} else {
												WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
											}

										}
										/****
										 * Prerequisite 7: Changing the room
										 * status to inspected to assign the
										 * room for checking in
										 *****/

										WSClient.setData("{var_roomNumber}", roomNumber1);
										String setHousekeepingRoomStatusReq1 = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
										String setHousekeepingRoomStatusRes1 = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq1);

										if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1, "SetHousekeepingRoomStatusRS_Success", true)) {

											/*****
											 * Prerequisite 16: Assign Room
											 *****/

											String assignRoomReq1 = WSClient.createSOAPMessage("AssignRoom", "DS_01");
											String assignRoomRes1 = WSClient.processSOAPMessage(assignRoomReq1);

											if (WSAssert.assertIfElementExists(assignRoomRes1, "AssignRoomRS_Success", true)) {

												/*****
												 * Prerequisite 17: CheckIn
												 * Reservation
												 *****/

												String checkInReq1 = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
												String checkInRes1 = WSClient.processSOAPMessage(checkInReq1);

												if (WSAssert.assertIfElementExists(checkInRes1, "CheckinReservationRS_Success", true)) {

													WSClient.setData("{var_profileId}", profileID);
													WSClient.setData("{var_resvId}", resvID);
													WSClient.setData("{var_payeeProfileId}", newProfileId);
													WSClient.setData("{var_payeeResvId}", newReservationId);

													OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
													/*******************
													 * OWS Update PayRouting
													 * Operation
													 ************************/

													String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_03");
													String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

													if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
														if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
															/*
															 * String
															 * trxSubGroup =
															 * WSClient.
															 * getElementValue(
															 * updateResvResq,
															 * "RoutingCodes_RoutingInstructionCode_TCSubGroup",
															 * XMLType.
															 * REQUEST);
															 * System.out.
															 * println(
															 * trxSubGroup);
															 */
															// String
															// trxGroup =
															// WSClient.getElementValue(
															// updateResvResq,
															// "RoutingCodes_RoutingInstructionCode_TCGroup",
															// XMLType.REQUEST);
															// System.out.println(trxGroup);

															/******
															 * Validation from
															 * the Database
															 ******/

															String trxCode = WSClient.getElementValue(updateResvResq, "RoutingCodes_RoutingInstructionCode_InstructionCode", XMLType.REQUEST);
															System.out.println(trxCode);
															String routing = WSClient.getElementValue(updateResvResq, "UpdatePayRoutingRequest_PayRoutings_RoutingType", XMLType.REQUEST);
															System.out.println(routing);
															String query = WSClient.getQuery("QS_03");
															System.out.println(query);
															LinkedHashMap<String, String> db = WSClient.getDBRow(query);
															System.out.println(db);

															if (WSAssert.assertEquals(newReservationId, db.get("TO_RESV_NAME_ID"), true)) {
																WSClient.writeToReport(LogStatus.PASS, "New Reservation ID ->	Expected :		" + newReservationId + "		 Actual :	 " + db.get("TO_RESV_NAME_ID"));

															} else {
																WSClient.writeToReport(LogStatus.FAIL, "New Profile ID ->	 Expected : 		" + newReservationId + " 		Actual : 	 " + db.get("TO_RESV_NAME_ID"));

															}

															if (WSAssert.assertEquals(trxCode, db.get("TRX_CODE"), true)) {
																WSClient.writeToReport(LogStatus.PASS, "Transaction Code -> 	Expected :		" + trxCode + "		 Actual :	 " + db.get("TRX_CODE"));

															} else {
																WSClient.writeToReport(LogStatus.FAIL, "Transaction Code  ->	 Expected : 		" + trxCode + " 		Actual : 	 " + db.get("TRX_CODE"));

															}
															if (WSAssert.assertEquals(routing, db.get("ROUTINGTYPE"), true)) {
																WSClient.writeToReport(LogStatus.PASS, "Routing Type -> 	Expected :		" + routing + "		 Actual :	 " + db.get("ROUTINGTYPE"));

															} else {
																WSClient.writeToReport(LogStatus.FAIL, "Routing Type ->	 Expected : 		" + routing + " 		Actual : 	 " + db.get("ROUTINGTYPE"));

															}

														} else
															WSClient.writeToReport(LogStatus.FAIL, "The update payrouting operation has failed!");
													} else
														WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

													if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

														/****
														 * Verifying that the
														 * error message is
														 * populated on the
														 * response
														 ********/

														String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
														if (message != "*null*")
															WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
													}

													if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

														/****
														 * Verifying whether the
														 * error Message is
														 * populated on the
														 * response
														 ****/

														String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
														if (message != "*null*")
															WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
													} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

														/****
														 * Verifying whether the
														 * error Message is
														 * populated on the
														 * response
														 ****/

														String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
														if (message != "*null*")
															WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
													}

													if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

														/****
														 * Verifying whether the
														 * error Message is
														 * populated on the
														 * response
														 ****/

														String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
														if (message != "*null*")
															WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
													}

												} else {
													WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to checkin reservation");
												}
											} else {
												WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to assign room");
											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to change the status of room to vacant and inspected ");

										}

									} else
										WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation for New Payee");

								} else
									WSClient.writeToReport(LogStatus.PASS, "Blocked : Create Profile for new payee failed!");

							} else
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Pay routings!");

						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation");
					}

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile ");
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Property Config Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error") || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}

			if (!newReservationId.equals("error") || !newReservationId.equals(null)) {
				WSClient.setData("{var_resvId}", newReservationId);
				CheckoutReservation.checkOutReservation("DS_01");
			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePayRouting", "ResvAdvanced", "OWS" })
	/**
	 * Verify that Routing Instruction type assigned to a reservation is routed
	 * to another reservation. PreRequisites Required: --> Three Profiles are
	 * created -->Three Reservations are created -> There should be market
	 * codes,source codes,routing instructions,Payment method,rate
	 * codes,transaction codes,transaction sub group,transaction groups to be
	 * available. ->New payee,Payee reservation ID should be checked in.
	 *
	 */

	public void updatePayrouting_41245() throws Exception {
		try {
			String testName = "updatePayrouting_41245";
			WSClient.startTest(testName, "Verify that Routing Instruction type assigned to a reservation is routed to another reservation.", "minimumRegression");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code,Transaction Code,Transaction Group,Transaction Sub Group
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation created for Guest. </b>");
				profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID.equals("error") || !resvID.equals(null))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvID + "</b>");

						/************
						 * Prerequisite 3: Create profile for payee
						 *********************************/

						WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation created for Payee. </b>");

						payeeProfileID = CreateProfile.createProfile("DS_01");

						if (!payeeProfileID.equals("error")) {

							WSClient.writeToReport(LogStatus.INFO, "<b> Payee Profile ID : " + payeeProfileID + "</b>");

							WSClient.setData("{var_profileId}", payeeProfileID);

							/*******************
							 * Prerequisite 4:Create a Reservation for payee
							 ************************/

							payeeResvID = CreateReservation.createReservation("DS_13").get("reservationId");

							if (!payeeResvID.equals("error") || !payeeResvID.equals(null) || !resvID.equals(null)) {

								WSClient.writeToReport(LogStatus.INFO, "<b> Payee Reservation ID : " + payeeResvID + "</b>");
								WSClient.writeToReport(LogStatus.INFO, "<b>Checking In Payee Reservation ID </b>");

								String roomNumber = null;
								WSClient.setData("{var_profileId}", payeeProfileID);
								WSClient.setData("{var_resvId}", payeeResvID);
								/******
								 * Prerequisite 5: Fetching available Hotel
								 * rooms with room type
								 ******/

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {

									roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

								} else {

									/*****
									 * Prerequisite 6: Creating a room to assign
									 *******/

									String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
									String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

									if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

										roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
									}
								}

								/****
								 * Prerequisite 7: Changing the room status to
								 * inspected to assign the room for checking in
								 *****/

								WSClient.setData("{var_roomNumber}", roomNumber);
								String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
								String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

								if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {

									/*****
									 * Prerequisite 8: Assign Room
									 *****/

									String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
									String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

									if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

										/*****
										 * Prerequisite 9: CheckIn Reservation
										 *****/

										String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
										String checkInRes = WSClient.processSOAPMessage(checkInReq);

										if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {

											WSClient.setData("{var_payeeProfileId}", payeeProfileID);
											WSClient.setData("{var_payeeResvId}", payeeResvID);
											WSClient.setData("{var_resvId}", resvID);
											WSClient.setData("{var_profileId}", profileID);
											WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
											WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
											WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));

											/*****
											 * Prerequisite 10: Opera Create
											 * Routing Instructions
											 ******/

											String payroutReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_02");
											String payroutRes = WSClient.processSOAPMessage(payroutReq);

											if (WSAssert.assertIfElementExists(payroutRes, "CreateRoutingInstructionsRS_Success", true)) {

												String updateQuery = WSClient.getQuery("OWSUpdatePayRouting", "QS_01");
												String routeId = WSClient.getDBRow(updateQuery).get("ROUTING_INSTRUCTIONS_ID");

												WSClient.writeToReport(LogStatus.INFO, "<b>Routing Instruction ID :" + routeId + "</b>");

												WSClient.setData("{var_routeId}", routeId);
												WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));
												WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_04"));
												WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_04"));

												/************
												 * Prerequisite 11: Create
												 * profile for new Payee
												 *********************************/
												WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation is created for New Payee </b>");

												newProfileId = CreateProfile.createProfile("DS_01");

												WSClient.writeToReport(LogStatus.INFO, "<b> New Profile ID : " + newProfileId + "</b>");
												if (!newProfileId.equals("error")) {

													/*******************
													 * Prerequisite 12:Create a
													 * Reservation for new Payee
													 ************************/

													WSClient.setData("{var_profileId}", newProfileId);
													newReservationId = CreateReservation.createReservation("DS_13").get("reservationId");

													if (!newReservationId.equals("error") || !newReservationId.equals(null)) {

														WSClient.writeToReport(LogStatus.INFO, "<b> New Reservation ID :" + newReservationId + "</b>");
														WSClient.writeToReport(LogStatus.INFO, "<b>Checking In the new Payee Reservation ID</b>");

														String roomNumber1 = null;
														WSClient.setData("{var_profileId}", newProfileId);
														WSClient.setData("{var_resvId}", newReservationId);
														/******
														 * Prerequisite 13:
														 * Fetching available
														 * Hotel rooms with room
														 * type
														 ******/

														String fetchHotelRoomsReq1 = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
														String fetchHotelRoomsRes1 = WSClient.processSOAPMessage(fetchHotelRoomsReq1);

														if (WSAssert.assertIfElementExists(fetchHotelRoomsRes1, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes1, "FetchHotelRoomsRS_HotelRooms_Room", true)) {

															roomNumber1 = WSClient.getElementValue(fetchHotelRoomsRes1, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

														} else {

															/*****
															 * Prerequisite 14:
															 * Creating a room
															 * to assign
															 *******/

															String createRoomReq1 = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
															String createRoomRes1 = WSClient.processSOAPMessage(createRoomReq1);

															if (WSAssert.assertIfElementExists(createRoomRes1, "CreateRoomRS_Success", true)) {

																roomNumber1 = WSClient.getElementValue(createRoomReq1, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

															} else {
																WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
															}
														}

														/****
														 * Prerequisite 15:
														 * Changing the room
														 * status to inspected
														 * to assign the room
														 * for checking in
														 *****/

														WSClient.setData("{var_roomNumber}", roomNumber1);
														String setHousekeepingRoomStatusReq1 = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
														String setHousekeepingRoomStatusRes1 = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq1);

														if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1, "SetHousekeepingRoomStatusRS_Success", true)) {

															/*****
															 * Prerequisite 16:
															 * Assign Room
															 *****/

															String assignRoomReq1 = WSClient.createSOAPMessage("AssignRoom", "DS_01");
															String assignRoomRes1 = WSClient.processSOAPMessage(assignRoomReq1);

															if (WSAssert.assertIfElementExists(assignRoomRes1, "AssignRoomRS_Success", true)) {

																/*****
																 * Prerequisite
																 * 17: CheckIn
																 * Reservation
																 *****/

																String checkInReq1 = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
																String checkInRes1 = WSClient.processSOAPMessage(checkInReq1);

																if (WSAssert.assertIfElementExists(checkInRes1, "CheckinReservationRS_Success", true)) {

																	WSClient.setData("{var_profileId}", profileID);
																	WSClient.setData("{var_resvId}", resvID);
																	WSClient.setData("{var_payeeProfileId}", newProfileId);
																	WSClient.setData("{var_payeeResvId}", newReservationId);

																	OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

																	/*******************
																	 * OWS
																	 * Update
																	 * PayRouting
																	 * Operation
																	 ************************/

																	String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_03");
																	String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

																	if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
																		if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

																			String trxCode = WSClient.getElementValue(updateResvResq, "RoutingCodes_RoutingInstructionCode_InstructionCode", XMLType.REQUEST);
																			System.out.println(trxCode);
																			String query = WSClient.getQuery("QS_03");
																			System.out.println(query);
																			LinkedHashMap<String, String> db = WSClient.getDBRow(query);
																			System.out.println(db);

																			/************
																			 * Validation
																			 * against
																			 * the
																			 * database
																			 ********/

																			if (WSAssert.assertEquals(newReservationId, db.get("TO_RESV_NAME_ID"), true)) {
																				WSClient.writeToReport(LogStatus.PASS, "New Reservation ID -> 	Expected :		" + newReservationId + "		 Actual :	 " + db.get("TO_RESV_NAME_ID"));

																			} else {
																				WSClient.writeToReport(LogStatus.FAIL, "New Reservation ID	-> Expected : 		" + newReservationId + " 		Actual : 	 " + db.get("TO_RESV_NAME_ID"));

																			}

																			if (WSAssert.assertEquals(trxCode, db.get("TRX_CODE"), true)) {
																				WSClient.writeToReport(LogStatus.PASS, "Transaction Code -> 	Expected :		" + trxCode + "		 Actual :	 " + db.get("TRX_CODE"));

																			} else {
																				WSClient.writeToReport(LogStatus.FAIL, "Transaction Code  ->	 Expected : 		" + trxCode + " 		Actual : 	 " + db.get("TRX_CODE"));

																			}

																		} else
																			WSClient.writeToReport(LogStatus.FAIL, "The update payrouting operation has failed!");
																	} else
																		WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

																	if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

																		/****
																		 * Verifying
																		 * that
																		 * the
																		 * error
																		 * message
																		 * is
																		 * populated
																		 * on
																		 * the
																		 * response
																		 ********/

																		String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
																		if (message != "*null*")
																			WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
																	}

																	if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

																		/****
																		 * Verifying
																		 * whether
																		 * the
																		 * error
																		 * Message
																		 * is
																		 * populated
																		 * on
																		 * the
																		 * response
																		 ****/

																		String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
																		if (message != "*null*")
																			WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
																	} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

																		/****
																		 * Verifying
																		 * whether
																		 * the
																		 * error
																		 * Message
																		 * is
																		 * populated
																		 * on
																		 * the
																		 * response
																		 ****/

																		String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
																		if (message != "*null*")
																			WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
																	}

																	if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

																		/****
																		 * Verifying
																		 * whether
																		 * the
																		 * error
																		 * Message
																		 * is
																		 * populated
																		 * on
																		 * the
																		 * response
																		 ****/

																		String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
																		if (message != "*null*")
																			WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
																	}

																} else {
																	WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to checkin reservation");
																}
															} else {
																WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to assign room");
															}

														} else {
															WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to change the status of room to vacant and inspected ");

														}

													} else
														WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation for New Payee");

												} else
													WSClient.writeToReport(LogStatus.PASS, "Blocked : Create Profile for new payee failed!");

											} else
												WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Pay routings!");

										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to checkin reservation");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to assign room");
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to change the status of room to vacant and inspected");

								}

							} else
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation for Payee");

						} else
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");

					} else
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation");

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile ");

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Property Config Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));

			if (!payeeResvID.equals("error") || !payeeResvID.equals(null) || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", payeeResvID);
				CheckoutReservation.checkOutReservation("DS_01");
			}
			if (!newReservationId.equals("error") || !newReservationId.equals(null)) {
				WSClient.setData("{var_resvId}", newReservationId);
				CheckoutReservation.checkOutReservation("DS_01");
			}

			if (!resvID.equals("error") || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePayRouting", "ResvAdvanced", "OWS" })
	/**
	 * Verify that OWS Update Pay Routing is working i.e to update the day
	 * routing details successfully.
	 *
	 * PreRequisites Required: --> Two Profiles are created -->One Reservation
	 * are created ->Guest Reservation should be checked in-> There should be
	 * market codes,source codes,routing instructions,Payment method,rate
	 * codes,transaction codes,transaction sub group,transaction groups to be
	 * available.
	 *
	 */

	public void updatePayrouting_41262() throws Exception {
		try {
			String testName = "updatePayrouting_41262";
			WSClient.startTest(testName, "Verify that day payrouting details are getting updated successfully.", "minimumRegression");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation are created for Guest. </b>");
				profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");

					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID.equals("error") || !resvID.equals(null))

					{
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Checking In Guest Reservation.  </b>");

						/******
						 * Prerequisite 3: Fetching available Hotel rooms with
						 * room type
						 ******/
						String roomNumber = null;
						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
						String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

						if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {

							roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

						} else {

							/*****
							 * Prerequisite 4: Creating a room to assign
							 *******/

							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
							String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

							if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

								roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
							}
						}

						/****
						 * Prerequisite 5: Changing the room status to inspected
						 * to assign the room for checking in
						 *****/

						WSClient.setData("{var_roomNumber}", roomNumber);
						String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
						String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

						if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {

							/***** Prerequisite 6: Assign Room *****/

							String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
							String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

							if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

								/*****
								 * Prerequisite 7: CheckIn Reservation
								 *****/

								String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {

									/************
									 * Prerequisite 8: Create profile for Payee
									 *********************************/

									WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created for Payee. </b>");

									payeeProfileID = CreateProfile.createProfile("DS_01");

									if (!payeeProfileID.equals("error")) {

										WSClient.writeToReport(LogStatus.INFO, "<b>Payee Profile ID : " + payeeProfileID + "</b>");

										WSClient.setData("{var_profileId}", payeeProfileID);

										WSClient.setData("{var_payeeProfileId}", payeeProfileID);
										WSClient.setData("{var_resvId}", resvID);
										WSClient.setData("{var_profileId}", profileID);
										WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
										WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
										WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));

										/***
										 * Prerequisite 9: Create Routing
										 * Instructions
										 **/

										String payroutReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_04");
										String payroutRes = WSClient.processSOAPMessage(payroutReq);

										if (WSAssert.assertIfElementExists(payroutRes, "CreateRoutingInstructionsRS_Success", true)) {
											String updateQuery = WSClient.getQuery("OWSUpdatePayRouting", "QS_01");
											String routeId = WSClient.getDBRow(updateQuery).get("ROUTING_INSTRUCTIONS_ID");
											String folio = WSClient.getDBRow(updateQuery).get("FOLIO_VIEW");
											WSClient.writeToReport(LogStatus.INFO, "<b>Routing Instruction ID :" + routeId + "</b>");

											WSClient.setData("{var_folio}", folio);
											WSClient.setData("{var_routeId}", routeId);

											OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

											/*******************
											 * OWS Update PayRouting Operation
											 ************************/

											String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_04");
											String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

											if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
												if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

													String daily = WSClient.getElementValue(updateResvResq, "UpdatePayRoutingRequest_PayRoutings_Daily", XMLType.REQUEST);
													System.out.println(daily);
													String day1 = WSClient.getElementValue(updateResvResq, "UpdatePayRoutingRequest_PayRoutings_Day1", XMLType.REQUEST);
													System.out.println(day1);
													String day2 = WSClient.getElementValue(updateResvResq, "UpdatePayRoutingRequest_PayRoutings_Day2", XMLType.REQUEST);
													System.out.println(day2);
													String day3 = WSClient.getElementValue(updateResvResq, "UpdatePayRoutingRequest_PayRoutings_Day3", XMLType.REQUEST);
													System.out.println(day3);
													String day4 = WSClient.getElementValue(updateResvResq, "UpdatePayRoutingRequest_PayRoutings_Day4", XMLType.REQUEST);
													System.out.println(day4);
													String day5 = WSClient.getElementValue(updateResvResq, "UpdatePayRoutingRequest_PayRoutings_Day5", XMLType.REQUEST);
													System.out.println(day5);
													String day6 = WSClient.getElementValue(updateResvResq, "UpdatePayRoutingRequest_PayRoutings_Day6", XMLType.REQUEST);
													System.out.println(day6);
													String day7 = WSClient.getElementValue(updateResvResq, "UpdatePayRoutingRequest_PayRoutings_Day7", XMLType.REQUEST);
													System.out.println(day7);
													String credit = WSClient.getElementValue(updateResvResq, "UpdatePayRoutingRequest_PayRoutings_CreditLimit", XMLType.REQUEST);
													System.out.println(credit);
													String declined = WSClient.getElementValue(updateResvResq, "UpdatePayRoutingRequest_PayRoutings_Declined", XMLType.REQUEST);
													System.out.println(declined);
													String routing = WSClient.getElementValue(updateResvResq, "UpdatePayRoutingRequest_PayRoutings_RoutingLimitType", XMLType.REQUEST);
													System.out.println(routing);

													String query = WSClient.getQuery("QS_04");
													System.out.println(query);
													LinkedHashMap<String, String> db = WSClient.getDBRow(query);
													System.out.println(db);

													if (WSAssert.assertEquals(daily, db.get("DAILY_YN"), true)) {
														WSClient.writeToReport(LogStatus.PASS, "Daily -> 	Expected :		" + daily + "		 Actual :	 " + db.get("DAILY_YN"));

													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Daily ->	 Expected : 		" + daily + " 		Actual : 	 " + db.get("DAILY_YN"));

													}

													if (WSAssert.assertEquals(day1, db.get("DAY1"), true)) {
														WSClient.writeToReport(LogStatus.PASS, "Day 1 -> 	Expected :		" + day1 + "		 Actual :	 " + db.get("DAY1"));

													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Day 1 ->	 Expected : 		" + day1 + " 		Actual : 	 " + db.get("DAY1"));

													}

													if (WSAssert.assertEquals(day2, db.get("DAY2"), true)) {
														WSClient.writeToReport(LogStatus.PASS, "Day 2 -> 	Expected :		" + day2 + "		 Actual :	 " + db.get("DAY2"));

													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Day2 ->	 Expected : 		" + day2 + " 		Actual : 	 " + db.get("DAY2"));

													}
													if (WSAssert.assertEquals(daily, db.get("DAY3"), true)) {
														WSClient.writeToReport(LogStatus.PASS, "Day 3 -> 	Expected :		" + day3 + "		 Actual :	 " + db.get("DAY3"));

													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Day 3 ->	 Expected : 		" + day3 + " 		Actual : 	 " + db.get("DAY3"));

													}
													if (WSAssert.assertEquals(day4, db.get("DAY4"), true)) {
														WSClient.writeToReport(LogStatus.PASS, "Day 4 -> 	Expected :		" + day4 + "		 Actual :	 " + db.get("DAY4"));

													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Day 4 ->	 Expected : 		" + day4 + " 		Actual : 	 " + db.get("DAY4"));

													}
													if (WSAssert.assertEquals(day5, db.get("DAY5"), true)) {
														WSClient.writeToReport(LogStatus.PASS, "Day 5 -> 	Expected :		" + day5 + "		 Actual :	 " + db.get("DAY5"));

													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Day 5 ->	 Expected : 		" + day5 + " 		Actual : 	 " + db.get("DAY5"));

													}
													if (WSAssert.assertEquals(day6, db.get("DAY6"), true)) {
														WSClient.writeToReport(LogStatus.PASS, "Day 6 -> 	Expected :		" + day6 + "		 Actual :	 " + db.get("DAY6"));

													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Day 6 ->	 Expected : 		" + day6 + " 		Actual : 	 " + db.get("DAY6"));

													}
													if (WSAssert.assertEquals(day7, db.get("DAY7"), true)) {
														WSClient.writeToReport(LogStatus.PASS, "Day 7 -> 	Expected :		" + day7 + "		 Actual :	 " + db.get("DAY7"));

													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Day 7 ->	 Expected : 		" + day7 + " 		Actual : 	 " + db.get("DAY7"));

													}
													if (WSAssert.assertEquals(credit, db.get("CREDIT_LIMIT"), true)) {
														WSClient.writeToReport(LogStatus.PASS, "Credit Limit -> 	Expected :		" + credit + "		 Actual :	 " + db.get("CREDIT_LIMIT"));

													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Credit Limit ->	 Expected : 		" + credit + " 		Actual : 	 " + db.get("CREDIT_LIMIT"));

													}
													if (WSAssert.assertEquals(declined, db.get("DECLINED_YN"), true)) {
														WSClient.writeToReport(LogStatus.PASS, "Declined YN -> 	Expected :		" + declined + "		 Actual :	 " + db.get("DECLINED_YN"));

													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Declined YN ->	 Expected : 		" + declined + " 		Actual : 	 " + db.get("DECLINED_YN"));

													}
													if (WSAssert.assertEquals(routing, db.get("ROUTING_LIMIT_TYPE"), true)) {
														WSClient.writeToReport(LogStatus.PASS, "Routing Limit Type -> 	Expected :		" + routing + "		 Actual :	 " + db.get("ROUTING_LIMIT_TYPE"));

													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Routing Limit Type ->	 Expected : 		" + routing + " 		Actual : 	 " + db.get("ROUTING_LIMIT_TYPE"));

													}

												} else
													WSClient.writeToReport(LogStatus.FAIL, "The update payrouting operation has failed!");
											} else
												WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

											if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

												/****
												 * Verifying that the error
												 * message is populated on the
												 * response
												 ********/

												String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
												if (message != "*null*")
													WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
											}

											if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

												/****
												 * Verifying whether the error
												 * Message is populated on the
												 * response
												 ****/

												String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
												if (message != "*null*")
													WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
											} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

												/****
												 * Verifying whether the error
												 * Message is populated on the
												 * response
												 ****/

												String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
												if (message != "*null*")
													WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
											}

											if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

												/****
												 * Verifying whether the error
												 * Message is populated on the
												 * response
												 ****/

												String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
												if (message != "*null*")
													WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
											}

										} else
											WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Pay routings!");

									} else
										WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to checkin reservation");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to assign room");
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to change the status of room to vacant and inspected");

						}

					} else
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation");

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile ");

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Property Config Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error") || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", resvID);
				CheckoutReservation.checkOutReservation("DS_01");
			}

		}
	}

	/*@Test(groups = { "minimumRegression", "UpdatePayRouting", "ResvAdvanced", "OWS" })
	 *//**
	 * Verify that error message is populated while incorrectly trying to update
	 * day routing details of a routing instruction.
	 *
	 * PreRequisites Required: --> Two Profiles are created -->One Reservation
	 * is created ->Reservation should be checked in-> There should be market
	 * codes,source codes,routing instructions,Payment method,rate
	 * codes,transaction codes,transaction sub group,transaction groups to be
	 * available.
	 *
	 *//*

	public void updatePayrouting_41263() throws Exception {
		try {
			String testName = "updatePayrouting_41263";
			WSClient.startTest(testName, "Verify that error message is populated while incorrectly trying to update day routing details of a routing instruction.", "minimumRegression");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

	  *//*************
	  * Prerequisite : Room type, Rate Plan Code, Source Code, Market
	  * Code
	  *********************************//*

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));

	   *//************
	   * Prerequisite 1: Create profile
	   *********************************//*

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation are created for Guest .</b>");

				profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation created for Guest and Payee </b>");
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");

					WSClient.setData("{var_profileId}", profileID);

	    *//*******************
	    * Prerequisite 2:Create a Reservation
	    ************************//*

					resvID = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID.equals("error") || !resvID.equals(null))

					{
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Checking in Guest Reservation . </b>");

	     *//******
	     * Prerequisite 3: Fetching available Hotel rooms with
	     * room type
	     ******//*
						String roomNumber = null;
						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
						String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

						if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {

							roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

						} else {

	      *//*****
	      * Prerequisite 4: Creating a room to assign
	      *******//*

							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
							String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

							if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

								roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
							}
						}
	       *//****
	       * Prerequisite 5: Changing the room status to inspected
	       * to assign the room for checking in
	       *****//*

						WSClient.setData("{var_roomNumber}", roomNumber);
						String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
						String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

						if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {

	        *//***** Prerequisite 6: Assign Room *****//*

							String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
							String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

							if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

	         *//*****
	         * Prerequisite 7: CheckIn Reservation
	         *****//*

								String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {

	          *//************
	          * Prerequisite 8: Create profile for Payee
	          *********************************//*

									WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created Payee .</b>");

									payeeProfileID = CreateProfile.createProfile("DS_01");

									if (!payeeProfileID.equals("error")) {

										WSClient.writeToReport(LogStatus.INFO, "<b>Payee Profile ID : " + payeeProfileID + "</b>");

										WSClient.setData("{var_profileId}", payeeProfileID);

										WSClient.setData("{var_payeeProfileId}", payeeProfileID);
										WSClient.setData("{var_resvId}", resvID);
										WSClient.setData("{var_profileId}", profileID);
										WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
										WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
										WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));

	           *//***
	           * Prerequisite 9: Create Routing
	           * Instructions
	           **//*

										String payroutReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
										String payroutRes = WSClient.processSOAPMessage(payroutReq);

										if (WSAssert.assertIfElementExists(payroutRes, "CreateRoutingInstructionsRS_Success", true)) {
											String updateQuery = WSClient.getQuery("OWSUpdatePayRouting", "QS_01");
											String routeId = WSClient.getDBRow(updateQuery).get("ROUTING_INSTRUCTIONS_ID");
											String folio = WSClient.getDBRow(updateQuery).get("FOLIO_VIEW");
											WSClient.writeToReport(LogStatus.INFO, "<b>Routing Instruction ID :" + routeId + "</b>");

											WSClient.setData("{var_folio}", folio);
											WSClient.setData("{var_routeId}", routeId);

											OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

	            *//*******************
	            * OWS Update PayRouting Operation
	            ************************//*

											String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_04");
											String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

											if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
												if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

												} else
													WSClient.writeToReport(LogStatus.FAIL, "The update payrouting operation has Passed!");
											} else
												WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

											if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

	             *//****
	             * Verifying that the error
	             * message is populated on the
	             * response
	             ********//*

												String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
												if (message != "*null*")
													WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
											}

											if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

	              *//****
	              * Verifying whether the error
	              * Message is populated on the
	              * response
	              ****//*

												String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
												if (message != "*null*")
													WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
											} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

	               *//****
	               * Verifying whether the error
	               * Message is populated on the
	               * response
	               ****//*

												String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
												if (message != "*null*")
													WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
											}

											if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

	                *//****
	                * Verifying whether the error
	                * Message is populated on the
	                * response
	                ****//*

												String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
												if (message != "*null*")
													WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
											}

										} else
											WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Pay routings!");

									} else
										WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to checkin reservation");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to assign room");
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to change the status of room to vacant and inspected");

						}
					} else
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation");

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile ");

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Property Config Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error") || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", resvID);
				CheckoutReservation.checkOutReservation("DS_01");
			}
		}
	}*/

	@Test(groups = { "minimumRegression", "UpdatePayRouting", "ResvAdvanced", "OWS" })
	/**
	 * Verify that routing instruction gets added to the reservation when
	 * routing instruction id is not sent on the request.(Window Routing)
	 * PreRequisites Required: --> Two Profiles are created -->One Reservation
	 * is created -> There should be market codes,source codes,routing
	 * instructions,Payment method,rate codes,transaction codes,transaction sub
	 * group,transaction groups to be available. ->Payee Reservation should be
	 * checked in.
	 *
	 */

	public void updatePayrouting_41264() throws Exception {
		try {
			String testName = "updatePayrouting_41264";
			WSClient.startTest(testName, "Verify that routing instruction gets added to the reservation when routing instruction id is not sent on the request(Window Routing).", "minimumRegression");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code,Transaction Code,Transaction Group,Transaction Sub Group
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation are created for Guest .</b>");

				profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID.equals("error") || !resvID.equals(null))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvID + "</b>");

						/************
						 * Prerequisite 3: Create profile for payee
						 *********************************/
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created for Payee . </b>");

						payeeProfileID = CreateProfile.createProfile("DS_01");

						if (!payeeProfileID.equals("error")) {

							WSClient.writeToReport(LogStatus.INFO, "<b> Payee Profile ID : " + payeeProfileID + "</b>");

							WSClient.setData("{var_payeeProfileId}", payeeProfileID);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_profileId}", profileID);
							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
							WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
							WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							/*******************
							 * OWS Update PayRouting Operation
							 ************************/

							String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_01");
							String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

							if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
								if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									/******
									 * Validation against the database
									 *******/

									String trxCode = WSClient.getElementValue(updateResvResq, "RoutingCodes_RoutingInstructionCode_InstructionCode", XMLType.REQUEST);
									System.out.println(trxCode);
									String query = WSClient.getQuery("QS_02");
									System.out.println(query);
									LinkedHashMap<String, String> db = WSClient.getDBRow(query);
									System.out.println(db);
									if (WSAssert.assertEquals(payeeProfileID, db.get("BILL_TO_NAME_ID"), true)) {
										WSClient.writeToReport(LogStatus.PASS, "Payee Profile ID -> 	Expected :		" + payeeProfileID + "		 Actual :	 " + db.get("BILL_TO_NAME_ID"));

									} else {
										WSClient.writeToReport(LogStatus.FAIL, "New Profile ID ->	 Expected : 		" + payeeProfileID + " 		Actual : 	 " + db.get("BILL_TO_NAME_ID"));

									}

									if (WSAssert.assertEquals(trxCode, db.get("TRX_CODE"), true)) {
										WSClient.writeToReport(LogStatus.PASS, "Transaction Code -> 	Expected :		" + trxCode + "		 Actual :	 " + db.get("TRX_CODE"));

									} else {
										WSClient.writeToReport(LogStatus.FAIL, "Transaction Code  ->	 Expected : 		" + trxCode + " 		Actual : 	 " + db.get("TRX_CODE"));

									}

								} else
									WSClient.writeToReport(LogStatus.FAIL, "The update payrouting operation has failed!");
							} else
								WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

							if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
								if (message != "*null*")
									WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
								if (message != "*null*")
									WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
							} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								if (message != "*null*")
									WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
								if (message != "*null*")
									WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
							}

						} else
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");

					} else
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation");

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile ");

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Property Config Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error") || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}
		}
	}

	@Test(groups = { "minimumRegression", "UpdatePayRouting", "ResvAdvanced", "OWS" })
	/**
	 * Verify that routing instruction gets added to the reservation when
	 * routing instruction id is not sent on the request.(Room Routing)
	 * PreRequisites Required: --> Two Profiles are created -->One Reservations
	 * are created -> There should be market codes,source codes,routing
	 * instructions,Payment method,rate codes,transaction codes,transaction sub
	 * group,transaction groups to be available.
	 *
	 */

	public void updatePayrouting_41265() throws Exception {
		try {
			String testName = "updatePayrouting_41265";
			WSClient.startTest(testName, "Verify that routing instruction gets added to the reservation when routing instruction id is not sent on the request(Room Routing).", "minimumRegression");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code,Transaction Code,Transaction Group,Transaction Sub Group
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation are created for Guest . </b>");

				profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID.equals("error") || !resvID.equals(null))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvID + "</b>");

						/************
						 * Prerequisite 3: Create profile for payee
						 *********************************/

						WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation are created for Payee . </b>");

						payeeProfileID = CreateProfile.createProfile("DS_01");

						if (!payeeProfileID.equals("error")) {

							WSClient.writeToReport(LogStatus.INFO, "<b> Payee Profile ID : " + payeeProfileID + "</b>");

							/*******************
							 * Prerequisite 4 :Create a Reservation for payee
							 ************************/

							payeeResvID = CreateReservation.createReservation("DS_13").get("reservationId");

							if (!payeeResvID.equals("error") || !payeeResvID.equals(null) || !resvID.equals(null)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> Payee Reservation ID : " + payeeResvID + "</b>");
								WSClient.writeToReport(LogStatus.INFO, "<b>Checking in Payee Reservation . </b>");
								/******
								 * Prerequisite 5: Fetching available Hotel
								 * rooms with room type
								 ******/
								WSClient.setData("{var_resvId}", payeeResvID);
								String roomNumber = null;
								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {

									roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

								} else {

									/*****
									 * Prerequisite 6: Creating a room to assign
									 *******/

									String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
									String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

									if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

										roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
									}
								}
								/****
								 * Prerequisite 7: Changing the room status to
								 * inspected to assign the room for checking in
								 *****/

								WSClient.setData("{var_roomNumber}", roomNumber);
								String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
								String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

								if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {

									/*****
									 * Prerequisite 7: Assign Room
									 *****/

									String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
									String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

									if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

										/*****
										 * Prerequisite 8 : CheckIn Reservation
										 *****/

										String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
										String checkInRes = WSClient.processSOAPMessage(checkInReq);

										if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {
											WSClient.setData("{var_payeeprofileId}", payeeProfileID);
											WSClient.setData("{var_payeeResvId}", payeeResvID);
											WSClient.setData("{var_resvId}", resvID);
											WSClient.setData("{var_profileId}", profileID);
											WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
											WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
											WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));

											OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

											/*******************
											 * OWS Update PayRouting Operation
											 ************************/

											String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_05");
											String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

											if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
												if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

													String trxCode = WSClient.getElementValue(updateResvResq, "RoutingCodes_RoutingInstructionCode_InstructionCode", XMLType.REQUEST);
													System.out.println(trxCode);
													String query = WSClient.getQuery("QS_03");
													System.out.println(query);
													LinkedHashMap<String, String> db = WSClient.getDBRow(query);
													System.out.println(db);
													if (WSAssert.assertEquals(payeeResvID, db.get("TO_RESV_NAME_ID"), true)) {
														WSClient.writeToReport(LogStatus.PASS, "Payee Reservation ID -> 	Expected :		" + payeeResvID + "		 Actual :	 " + db.get("TO_RESV_NAME_ID"));

													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Payee Reservation ID ->	 Expected : 		" + payeeResvID + " 		Actual : 	 " + db.get("TO_RESV_NAME_ID"));

													}

													if (WSAssert.assertEquals(trxCode, db.get("TRX_CODE"), true)) {
														WSClient.writeToReport(LogStatus.PASS, "Transaction Code -> 	Expected :		" + trxCode + "		 Actual :	 " + db.get("TRX_CODE"));

													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Transaction Code  ->	 Expected : 		" + trxCode + " 		Actual : 	 " + db.get("TRX_CODE"));

													}

												} else
													WSClient.writeToReport(LogStatus.FAIL, "The update payrouting operation has failed!");
											} else
												WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

											if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

												/****
												 * Verifying that the error
												 * message is populated on the
												 * response
												 ********/

												String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
												if (message != "*null*")
													WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
											}

											if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

												/****
												 * Verifying whether the error
												 * Message is populated on the
												 * response
												 ****/

												String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
												if (message != "*null*")
													WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
											} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

												/****
												 * Verifying whether the error
												 * Message is populated on the
												 * response
												 ****/

												String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
												if (message != "*null*")
													WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
											}

											if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

												/****
												 * Verifying whether the error
												 * Message is populated on the
												 * response
												 ****/

												String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
												if (message != "*null*")
													WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to checkin reservation");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to assign room");
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to change the status of room to vacant and inspected");

								}

							} else
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation for Payee");
						} else
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");

					} else
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation");

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile ");

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Property Config Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error") || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}
			if (!payeeResvID.equals("error") || !payeeResvID.equals(null) || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", payeeResvID);
				CheckoutReservation.checkOutReservation("DS_01");
			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePayRouting", "ResvAdvanced", "OWS" })
	/**
	 * Verify that error message is populated on the response when folio number
	 * is given greater than 8 for a routing instruction attached to a
	 * reservation. PreRequisites Required: --> Two Profiles are created -->One
	 * Reservation is created -> There should be market codes,source
	 * codes,routing instructions,Payment method,rate codes,transaction
	 * codes,transaction sub group,transaction groups to be available.
	 *
	 */

	public void updatePayrouting_41267() throws Exception {
		try {
			String testName = "updatePayrouting_41267";
			WSClient.startTest(testName, "Verify that error message is populated on the response when folio number is given greater than 8 for a routing instruction attached to a reservation.", "minimumRegression");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code,Transaction Code,Transaction Group,Transaction Sub Group
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation are created for Guest. </b>");

				profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID.equals("error") || !resvID.equals(null))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvID + "</b>");

						/************
						 * Prerequisite 3: Create profile for payee
						 *********************************/

						WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created for Payee. </b>");
						payeeProfileID = CreateProfile.createProfile("DS_01");

						if (!payeeProfileID.equals("error")) {

							WSClient.writeToReport(LogStatus.INFO, "<b> Payee Profile ID : " + payeeProfileID + "</b>");

							WSClient.setData("{var_payeeProfileId}", payeeProfileID);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_profileId}", profileID);
							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
							WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
							WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							/*******************
							 * OWS Update PayRouting Operation
							 ************************/

							String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_06");
							String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

							if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
								if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

								} else
									WSClient.writeToReport(LogStatus.FAIL, "The update payrouting operation has passed!");
							} else
								WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

							if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
								if (message != "*null*")
									WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
								if (message != "*null*")
									WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
							} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								if (message != "*null*")
									WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
								if (message != "*null*")
									WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
							}

						} else
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");

					} else
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation");

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile ");

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Property Config Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error") || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePayRouting", "ResvAdvanced", "OWS" })
	/**
	 * Verify that OWS Update Pay Routing is working i.e to update the routing
	 * instruction to a keytrack successfully.
	 *
	 * PreRequisites Required: --> Three Profiles are created -->One
	 * Reservations are created -> There should be market codes,source
	 * codes,routing instructions,Payment method,rate codes,transaction
	 * codes,transaction sub group,transaction groups to be available.
	 *
	 */

	public void updatePayrouting_41268() throws Exception {
		try {
			String testName = "updatePayrouting_41268";
			WSClient.startTest(testName, "Verify that payrouting details are getting updated to a keytrack successfully.", "minimumRegression");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));
				WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resortOperaValue, channel));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation are created for Guest. </b>");

				profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation created for Guest,Payee and New Payee </b>");
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");

					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID.equals("error") || !resvID.equals(null))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvID + "</b>");

						WSClient.setData("{var_keyTrack2}", "21140000000000000005");
						WSClient.setData("{var_resvId}", resvID);

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String req_setKeyData = WSClient.createSOAPMessage("OWSSetKeyData", "DS_01");
						String res_setKeyData = WSClient.processSOAPMessage(req_setKeyData);
						if (WSAssert.assertIfElementExists(res_setKeyData, "SetKeyDataResponse_Result", true)) {

							/************
							 * Prerequisite 3: Create profile for Payee
							 *****************/

							WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created for Payee . </b>");

							payeeProfileID = CreateProfile.createProfile("DS_01");

							if (!payeeProfileID.equals("error")) {

								WSClient.writeToReport(LogStatus.INFO, "<b>Payee Profile ID : " + payeeProfileID + "</b>");

								WSClient.setData("{var_profileId}", payeeProfileID);

								WSClient.setData("{var_payeeProfileId}", payeeProfileID);
								WSClient.setData("{var_resvId}", resvID);
								WSClient.setData("{var_profileId}", profileID);
								WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
								WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
								WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));

								/***
								 * Prerequisite 4: Create Routing Instructions
								 **/

								String payroutReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
								String payroutRes = WSClient.processSOAPMessage(payroutReq);

								if (WSAssert.assertIfElementExists(payroutRes, "CreateRoutingInstructionsRS_Success", true)) {

									String updateQuery = WSClient.getQuery("OWSUpdatePayRouting", "QS_01");
									String routeId = WSClient.getDBRow(updateQuery).get("ROUTING_INSTRUCTIONS_ID");
									String folio = WSClient.getDBRow(updateQuery).get("FOLIO_VIEW");
									WSClient.writeToReport(LogStatus.INFO, "<b>Routing Instruction ID :" + routeId + "</b>");

									WSClient.setData("{var_folio}", folio);
									WSClient.setData("{var_routeId}", routeId);
									WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));

									OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

									/*******************
									 * OWS Update PayRouting Operation
									 ************************/

									String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_07");
									String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

									if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
										if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

											String trxCode = WSClient.getElementValue(updateResvResq, "RoutingCodes_RoutingInstructionCode_InstructionCode", XMLType.REQUEST);
											System.out.println(trxCode);
											String keyTrack = WSClient.getElementValue(updateResvResq, "UpdatePayRoutingRequest_ReservationRequest_KeyTrack_Key2Track", XMLType.REQUEST);
											System.out.println(keyTrack);
											String routing = WSClient.getElementValue(updateResvResq, "UpdatePayRoutingRequest_PayRoutings_RoutingType", XMLType.REQUEST);
											System.out.println(routing);

											String query = WSClient.getQuery("QS_05");
											System.out.println(query);
											LinkedHashMap<String, String> db = WSClient.getDBRow(query);
											System.out.println(db);

											if (WSAssert.assertEquals(payeeProfileID, db.get("BILL_TO_NAME_ID"), true)) {
												WSClient.writeToReport(LogStatus.PASS, "Profile ID -> 	Expected :		" + payeeProfileID + "		 Actual :	 " + db.get("BILL_TO_NAME_ID"));

											} else {
												WSClient.writeToReport(LogStatus.FAIL, "Profile ID ->	 Expected : 		" + payeeProfileID + " 		Actual : 	 " + db.get("BILL_TO_NAME_ID"));

											}

											if (WSAssert.assertEquals(trxCode, db.get("TRX_CODE"), true)) {
												WSClient.writeToReport(LogStatus.PASS, "Transaction Code -> 	Expected :		" + trxCode + "		 Actual :	 " + db.get("TRX_CODE"));

											} else {
												WSClient.writeToReport(LogStatus.FAIL, "Transaction Code  ->	 Expected : 		" + trxCode + " 		Actual : 	 " + db.get("TRX_CODE"));

											}
											if (WSAssert.assertEquals(routing, db.get("ROUTINGTYPE"), true)) {
												WSClient.writeToReport(LogStatus.PASS, "Routing Type -> 	Expected :		" + routing + "		 Actual :	 " + db.get("ROUTINGTYPE"));

											} else {
												WSClient.writeToReport(LogStatus.FAIL, "Routing Type ->	 Expected : 		" + routing + " 		Actual : 	 " + db.get("ROUTINGTYPE"));

											}

											if (WSAssert.assertEquals(folio, db.get("FOLIO_VIEW"), true)) {
												WSClient.writeToReport(LogStatus.PASS, "Window Number -> 	Expected :		" + folio + "		 Actual :	 " + db.get("FOLIO_VIEW"));

											} else {
												WSClient.writeToReport(LogStatus.FAIL, "Window Number ->	 Expected : 		" + folio + " 		Actual : 	 " + db.get("FOLIO_VIEW"));

											}
											if (WSAssert.assertEquals(keyTrack, db.get("KEYTRACK"), true)) {
												WSClient.writeToReport(LogStatus.PASS, "Key Track -> 	Expected :		" + keyTrack + "		 Actual :	 " + db.get("KEYTRACK"));

											} else {
												WSClient.writeToReport(LogStatus.FAIL, "Key Track ->	 Expected : 		" + keyTrack + " 		Actual : 	 " + db.get("KEYTRACK"));

											}

										} else
											WSClient.writeToReport(LogStatus.FAIL, "The update payrouting operation has failed!");
									} else
										WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

									if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

										/****
										 * Verifying that the error message is
										 * populated on the response
										 ********/

										String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
										if (message != "*null*")
											WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
									}

									if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

										/****
										 * Verifying whether the error Message
										 * is populated on the response
										 ****/

										String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
										if (message != "*null*")
											WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
									} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

										/****
										 * Verifying whether the error Message
										 * is populated on the response
										 ****/

										String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										if (message != "*null*")
											WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
									}

									if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

										/****
										 * Verifying whether the error Message
										 * is populated on the response
										 ****/

										String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
										if (message != "*null*")
											WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
									}

								} else
									WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Pay routings!");

							} else
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");
						} else
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Set Key Data");
					} else
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation");

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile ");

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Property Config Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error") || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePayRouting", "ResvAdvanced", "OWS" })
	/**
	 * Verify that Routing Instruction type assigned to a reservation is changed
	 * from type Room to Window successfully. PreRequisites Required: --> Three
	 * Profiles are created -->Two Reservations are created -> There should be
	 * market codes,source codes,routing instructions,Payment method,rate
	 * codes,transaction codes,transaction sub group,transaction groups to be
	 * available. ->Payee Reservation ID should be checked in.
	 */

	public void updatePayrouting_41246() throws Exception {
		try {
			String testName = "updatePayrouting_41246";
			WSClient.startTest(testName, "Verify that Routing Instruction type assigned to a reservation is changed from type Room to Window successfully.", "minimumRegression");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code,Transaction Code,Transaction Group,Transaction Sub Group
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation created for Guest. </b>");

				profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID.equals("error") || !resvID.equals(null))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvID + "</b>");

						/************
						 * Prerequisite 3: Create profile for payee
						 *********************************/

						payeeProfileID = CreateProfile.createProfile("DS_01");

						if (!payeeProfileID.equals("error")) {

							WSClient.writeToReport(LogStatus.INFO, "<b>Payee Profile ID : " + payeeProfileID + "</b>");

							WSClient.setData("{var_profileId}", payeeProfileID);
							payeeResvID = CreateReservation.createReservation("DS_13").get("reservationId");

							/************
							 * Prerequisite 4: Create Reservation for payee
							 *********************************/

							if (!payeeResvID.equals("error") || !payeeResvID.equals(null) || !resvID.equals(null)) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Payee Reservation ID : " + payeeResvID + "</b>");
								WSClient.writeToReport(LogStatus.INFO, "<b>Checking In Payee Reservation . </b>");

								WSClient.setData("{var_resvId}", payeeResvID);
								WSClient.setData("{var_profileId}", payeeProfileID);

								String roomNumber1 = null;

								/******
								 * Prerequisite 5: Fetching available Hotel
								 * rooms with room type
								 ******/

								String fetchHotelRoomsReq1 = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes1 = WSClient.processSOAPMessage(fetchHotelRoomsReq1);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes1, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes1, "FetchHotelRoomsRS_HotelRooms_Room", true)) {

									roomNumber1 = WSClient.getElementValue(fetchHotelRoomsRes1, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

								} else {

									/*****
									 * Prerequisite 7: Creating a room to assign
									 *******/

									String createRoomReq1 = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
									String createRoomRes1 = WSClient.processSOAPMessage(createRoomReq1);

									if (WSAssert.assertIfElementExists(createRoomRes1, "CreateRoomRS_Success", true)) {

										roomNumber1 = WSClient.getElementValue(createRoomReq1, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
									}
								}
								/****
								 * Prerequisite 8: Changing the room status to
								 * inspected to assign the room for checking in
								 *****/

								WSClient.setData("{var_roomNumber}", roomNumber1);
								String setHousekeepingRoomStatusReq1 = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
								String setHousekeepingRoomStatusRes1 = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq1);

								if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1, "SetHousekeepingRoomStatusRS_Success", true)) {

									/*****
									 * Prerequisite 9 : Assign Room
									 *****/

									String assignRoomReq1 = WSClient.createSOAPMessage("AssignRoom", "DS_01");
									String assignRoomRes1 = WSClient.processSOAPMessage(assignRoomReq1);

									if (WSAssert.assertIfElementExists(assignRoomRes1, "AssignRoomRS_Success", true)) {

										/*****
										 * Prerequisite 10: CheckIn Reservation
										 *****/

										String checkInReq1 = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
										String checkInRes1 = WSClient.processSOAPMessage(checkInReq1);

										if (WSAssert.assertIfElementExists(checkInRes1, "CheckinReservationRS_Success", true)) {

											WSClient.setData("{var_payeeResvId}", payeeResvID);
											WSClient.setData("{var_resvId}", resvID);
											WSClient.setData("{var_profileId}", profileID);
											WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));

											/*****
											 * Prerequisite 11 : Opera Create
											 * Routing Instructions
											 ******/

											String payroutReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_02");
											String payroutRes = WSClient.processSOAPMessage(payroutReq);

											if (WSAssert.assertIfElementExists(payroutRes, "CreateRoutingInstructionsRS_Success", true)) {

												String updateQuery = WSClient.getQuery("OWSUpdatePayRouting", "QS_01");
												String routeId = WSClient.getDBRow(updateQuery).get("ROUTING_INSTRUCTIONS_ID");

												WSClient.writeToReport(LogStatus.INFO, "<b>Routing Instruction ID :" + routeId + "</b>");

												WSClient.setData("{var_routeId}", routeId);
												WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));
												WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_04"));
												WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_04"));

												/************
												 * Prerequisite 12 : Create
												 * profile for new Payee
												 *********************************/

												WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created for New Payee . </b>");
												newProfileId = CreateProfile.createProfile("DS_01");

												WSClient.writeToReport(LogStatus.INFO, "<b> New Profile ID : " + newProfileId + "</b>");
												if (!newProfileId.equals("error")) {

													WSClient.setData("{var_profileId}", profileID);
													WSClient.setData("{var_resvId}", resvID);
													WSClient.setData("{var_payeeProfileId}", newProfileId);

													OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

													/*******************
													 * OWS Update PayRouting
													 * Operation
													 ************************/

													String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_01");
													String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

													if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
														if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

															String routing = WSClient.getElementValue(updateResvResq, "UpdatePayRoutingRequest_PayRoutings_RoutingType", XMLType.REQUEST);
															System.out.println(routing);

															String trxCode = WSClient.getElementValue(updateResvResq, "RoutingCodes_RoutingInstructionCode_InstructionCode", XMLType.REQUEST);
															String folio = WSClient.getElementValue(updateResvResq, "UpdatePayRoutingRequest_PayRoutings_Window", XMLType.REQUEST);
															System.out.println(trxCode);
															String query = WSClient.getQuery("QS_02");
															System.out.println(query);
															LinkedHashMap<String, String> db = WSClient.getDBRow(query);
															System.out.println(db);

															/*****
															 * Validation
															 * against the
															 * database
															 ********/

															if (WSAssert.assertEquals(newProfileId, db.get("BILL_TO_NAME_ID"), true)) {
																WSClient.writeToReport(LogStatus.PASS, "New Profile ID ->	Expected :		" + newProfileId + "		 Actual :	 " + db.get("BILL_TO_NAME_ID"));

															} else {
																WSClient.writeToReport(LogStatus.FAIL, "New Profile ID ->	 Expected : 		" + newProfileId + " 		Actual : 	 " + db.get("BILL_TO_NAME_ID"));

															}

															if (WSAssert.assertEquals(trxCode, db.get("TRX_CODE"), true)) {
																WSClient.writeToReport(LogStatus.PASS, "Transaction Code -> 	Expected :		" + trxCode + "		 Actual :	 " + db.get("TRX_CODE"));

															} else {
																WSClient.writeToReport(LogStatus.FAIL, "Transaction Code  ->	 Expected : 		" + trxCode + " 		Actual : 	 " + db.get("TRX_CODE"));

															}
															if (WSAssert.assertEquals(routing, db.get("ROUTINGTYPE"), true)) {
																WSClient.writeToReport(LogStatus.PASS, "Routing Type -> 	Expected :		" + routing + "		 Actual :	 " + db.get("ROUTINGTYPE"));

															} else {
																WSClient.writeToReport(LogStatus.FAIL, "Routing Type ->	 Expected : 		" + routing + " 		Actual : 	 " + db.get("ROUTINGTYPE"));

															}

														} else
															WSClient.writeToReport(LogStatus.FAIL, "The update payrouting operation has failed!");
													} else
														WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

													if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

														/****
														 * Verifying that the
														 * error message is
														 * populated on the
														 * response
														 ********/

														String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
														if (message != "*null*")
															WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
													}

													if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

														/****
														 * Verifying whether the
														 * error Message is
														 * populated on the
														 * response
														 ****/

														String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
														if (message != "*null*")
															WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
													} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

														/****
														 * Verifying whether the
														 * error Message is
														 * populated on the
														 * response
														 ****/

														String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
														if (message != "*null*")
															WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
													}

													if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

														/****
														 * Verifying whether the
														 * error Message is
														 * populated on the
														 * response
														 ****/

														String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
														if (message != "*null*")
															WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
													}

												} else
													WSClient.writeToReport(LogStatus.PASS, "Blocked : Create Profile for new payee failed!");

											} else
												WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Pay routings!");
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to checkin reservation");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to assign room");
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to change the status of room to vacant and inspected");
								}

							} else
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation for payee");
						} else
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for payee");
					} else
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation for Guest");
				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Guest");
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked :Property Profig Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error") || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}

			if (!payeeResvID.equals("error") || !payeeResvID.equals(null) || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", payeeResvID);
				CheckoutReservation.checkOutReservation("DS_01");

			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePayRouting", "ResvAdvanced", "OWS" })

	/**
	 * Verify that routing instruction is not updated for a cancelled
	 * reservation. PreRequisites Required: --> Two Profiles are created -->One
	 * Reservation are created(Cancelled) -> There should be market codes,source
	 * codes,routing instructions,Payment method,rate codes,transaction
	 * codes,transaction sub group,transaction groups to be available.
	 *
	 */

	public void updatePayrouting_41271() throws Exception {
		try {
			String testName = "updatePayrouting_41271";
			WSClient.startTest(testName, "Verify that routing instruction is not updated for a cancelled  reservation.", "minimumRegression");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code,Transaction Code,Transaction Group,Transaction Sub Group
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation is created for Guest. </b>");

				profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID.equals("error") || !resvID.equals(null))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvID + "</b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Cancelling Guest Reservation . </b>");

						/**********
						 * Prerequisite 3 : Cancel Reservation
						 ***********/
						WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
						WSClient.setData("{var_resvId}", resvID);
						if (CancelReservation.cancelReservation("DS_02")) {

							/************
							 * Prerequisite 4 : Create profile for payee
							 *********************************/

							WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created for Payee . </b>");

							payeeProfileID = CreateProfile.createProfile("DS_01");

							if (!payeeProfileID.equals("error")) {

								WSClient.writeToReport(LogStatus.INFO, "<b> Payee Profile ID : " + payeeProfileID + "</b>");

								WSClient.setData("{var_payeeProfileId}", payeeProfileID);
								WSClient.setData("{var_resvId}", resvID);
								WSClient.setData("{var_profileId}", profileID);
								WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
								WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
								WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));

								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

								/*******************
								 * OWS Update PayRouting Operation
								 ************************/

								String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_01");
								String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

								if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
									if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

									} else
										WSClient.writeToReport(LogStatus.FAIL, "The reservation is not cancelled!");
								} else
									WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

								if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

									/****
									 * Verifying that the error message is
									 * populated on the response
									 ********/

									String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
									if (message != "*null*")
										WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
								} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

									/****
									 * Verifying whether the error Message is
									 * populated on the response
									 ****/

									String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
									if (message != "*null*")
										WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
								} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

									/****
									 * Verifying whether the error Message is
									 * populated on the response
									 ****/

									String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									if (message != "*null*")
										WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
								} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

									/****
									 * Verifying whether the error Message is
									 * populated on the response
									 ****/

									String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
									if (message != "*null*")
										WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
								}

							} else
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");

						} else
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation");

					} else
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Cancel Reservation");

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile ");

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Property Config Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error") || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePayRouting", "ResvAdvanced", "OWS" })
	/**
	 * Verify that routing instruction is not updated for a checked out payee
	 * reservation. PreRequisites Required: --> Three Profiles are created
	 * -->Three Reservations are created -> Payee Reservation should be 'CHECKED
	 * IN' ->New Payee Reservation should be 'CHECKED OUT' -> There should be
	 * market codes,source codes,routing instructions,Payment method,rate
	 * codes,transaction codes,transaction sub group,transaction groups to be
	 * available.
	 *
	 */

	public void updatePayrouting_41273() throws Exception {
		try {
			String testName = "updatePayrouting_41273";
			WSClient.startTest(testName, "Verify that routing instruction is not updated for a checked out payee reservation.", "minimumRegression");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code,Transaction Code,Transaction Group,Transaction Sub Group
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation created for Guest . </b>");

				profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID.equals("error") || !resvID.equals(null))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvID + "</b>");

						/************
						 * Prerequisite 3: Create profile for payee
						 *********************************/

						WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation are created for Payee . </b>");

						payeeProfileID = CreateProfile.createProfile("DS_01");

						if (!payeeProfileID.equals("error")) {

							WSClient.writeToReport(LogStatus.INFO, "<b> Payee Profile ID : " + payeeProfileID + "</b>");

							WSClient.setData("{var_profileId}", payeeProfileID);

							/*******************
							 * Prerequisite 4:Create a Reservation for payee
							 ************************/

							payeeResvID = CreateReservation.createReservation("DS_13").get("reservationId");

							if (!payeeResvID.equals("error") || !payeeResvID.equals(null) || !resvID.equals(null)) {

								WSClient.writeToReport(LogStatus.INFO, "<b> Payee Reservation ID : " + payeeResvID + "</b>");
								WSClient.writeToReport(LogStatus.INFO, "<b>Checking In Payee Reservation . </b>");

								String roomNumber = null;
								WSClient.setData("{var_profileId}", payeeProfileID);
								WSClient.setData("{var_resvId}", payeeResvID);
								/******
								 * Prerequisite 5: Fetching available Hotel
								 * rooms with room type
								 ******/

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {

									roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

								} else {

									/*****
									 * Prerequisite 6: Creating a room to assign
									 *******/

									String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
									String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

									if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

										roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
									}
								}

								/****
								 * Prerequisite 7: Changing the room status to
								 * inspected to assign the room for checking in
								 *****/

								WSClient.setData("{var_roomNumber}", roomNumber);
								String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
								String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

								if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {

									/*****
									 * Prerequisite 8: Assign Room
									 *****/

									String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
									String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

									if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

										/*****
										 * Prerequisite 9: CheckIn Reservation
										 *****/

										String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
										String checkInRes = WSClient.processSOAPMessage(checkInReq);

										if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {

											WSClient.setData("{var_payeeProfileId}", payeeProfileID);
											WSClient.setData("{var_payeeResvId}", payeeResvID);
											WSClient.setData("{var_resvId}", resvID);
											WSClient.setData("{var_profileId}", profileID);
											WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
											WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
											WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));

											/*****
											 * Prerequisite 10: Opera Create
											 * Routing Instructions
											 ******/

											String payroutReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_02");
											String payroutRes = WSClient.processSOAPMessage(payroutReq);

											if (WSAssert.assertIfElementExists(payroutRes, "CreateRoutingInstructionsRS_Success", true)) {

												String updateQuery = WSClient.getQuery("OWSUpdatePayRouting", "QS_01");
												String routeId = WSClient.getDBRow(updateQuery).get("ROUTING_INSTRUCTIONS_ID");

												WSClient.writeToReport(LogStatus.INFO, "<b>Routing Instruction ID :" + routeId + "</b>");

												WSClient.setData("{var_routeId}", routeId);
												WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));
												WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_04"));
												WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_04"));

												/************
												 * Prerequisite 11: Create
												 * profile for new Payee
												 *********************************/
												WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation created for New Payee. </b>");

												newProfileId = CreateProfile.createProfile("DS_01");

												WSClient.writeToReport(LogStatus.INFO, "<b> New Profile ID : " + newProfileId + "</b>");
												if (!newProfileId.equals("error")) {

													/*******************
													 * Prerequisite 12:Create a
													 * Reservation for new Payee
													 ************************/

													WSClient.setData("{var_profileId}", newProfileId);
													newReservationId = CreateReservation.createReservation("DS_13").get("reservationId");

													if (!newReservationId.equals("error") || !newReservationId.equals(null)) {

														WSClient.writeToReport(LogStatus.INFO, "<b> New Reservation ID :" + newReservationId + "</b>");
														WSClient.writeToReport(LogStatus.INFO, "<b>Checking In and Checking Out New Payee Reservation. </b>");

														String roomNumber1 = null;
														WSClient.setData("{var_profileId}", newProfileId);
														WSClient.setData("{var_resvId}", newReservationId);

														/******
														 * Prerequisite 13:
														 * Fetching available
														 * Hotel rooms with room
														 * type
														 ******/

														String fetchHotelRoomsReq1 = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
														String fetchHotelRoomsRes1 = WSClient.processSOAPMessage(fetchHotelRoomsReq1);

														if (WSAssert.assertIfElementExists(fetchHotelRoomsRes1, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes1, "FetchHotelRoomsRS_HotelRooms_Room", true)) {

															roomNumber1 = WSClient.getElementValue(fetchHotelRoomsRes1, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

														} else {

															/*****
															 * Prerequisite 14:
															 * Creating a room
															 * to assign
															 *******/

															String createRoomReq1 = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
															String createRoomRes1 = WSClient.processSOAPMessage(createRoomReq1);

															if (WSAssert.assertIfElementExists(createRoomRes1, "CreateRoomRS_Success", true)) {

																roomNumber1 = WSClient.getElementValue(createRoomReq1, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

															} else {
																WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
															}

															/****
															 * Prerequisite 15:
															 * Changing the room
															 * status to
															 * inspected to
															 * assign the room
															 * for checking in
															 *****/

															WSClient.setData("{var_roomNumber}", roomNumber1);
															String setHousekeepingRoomStatusReq1 = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
															String setHousekeepingRoomStatusRes1 = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq1);

															if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1, "SetHousekeepingRoomStatusRS_Success", true)) {

																/*****
																 * Prerequisite
																 * 16: Assign
																 * Room
																 *****/

																String assignRoomReq1 = WSClient.createSOAPMessage("AssignRoom", "DS_01");
																String assignRoomRes1 = WSClient.processSOAPMessage(assignRoomReq1);

																if (WSAssert.assertIfElementExists(assignRoomRes1, "AssignRoomRS_Success", true)) {

																	/*****
																	 * Prerequisite
																	 * 17:
																	 * CheckIn
																	 * Reservation
																	 *****/

																	String checkInReq1 = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
																	String checkInRes1 = WSClient.processSOAPMessage(checkInReq1);

																	if (WSAssert.assertIfElementExists(checkInRes1, "CheckinReservationRS_Success", true)) {

																		if (CheckoutReservation.checkOutReservation("DS_01")) {

																			WSClient.setData("{var_profileId}", profileID);
																			WSClient.setData("{var_resvId}", resvID);
																			WSClient.setData("{var_payeeProfileId}", newProfileId);
																			WSClient.setData("{var_payeeResvId}", newReservationId);

																			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

																			/*******************
																			 * OWS
																			 * Update
																			 * PayRouting
																			 * Operation
																			 ************************/

																			String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_03");
																			String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

																			if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
																				if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

																				} else
																					WSClient.writeToReport(LogStatus.FAIL, "The update payrouting operation was successful!");
																			} else
																				WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

																			if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

																				/****
																				 * Verifying
																				 * that
																				 * the
																				 * error
																				 * message
																				 * is
																				 * populated
																				 * on
																				 * the
																				 * response
																				 ********/

																				String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
																				if (message != "*null*")
																					WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
																			}

																			if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

																				/****
																				 * Verifying
																				 * whether
																				 * the
																				 * error
																				 * Message
																				 * is
																				 * populated
																				 * on
																				 * the
																				 * response
																				 ****/

																				String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
																				if (message != "*null*")
																					WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
																			} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

																				/****
																				 * Verifying
																				 * whether
																				 * the
																				 * error
																				 * Message
																				 * is
																				 * populated
																				 * on
																				 * the
																				 * response
																				 ****/

																				String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
																				if (message != "*null*")
																					WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
																			}

																			if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

																				/****
																				 * Verifying
																				 * whether
																				 * the
																				 * error
																				 * Message
																				 * is
																				 * populated
																				 * on
																				 * the
																				 * response
																				 ****/

																				String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
																				if (message != "*null*")
																					WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
																			}
																		} else
																			WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to checkout reservation");
																	} else {
																		WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to checkin reservation");
																	}
																} else {
																	WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to assign room");
																}

															} else {
																WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to change the status of room to vacant and inspected ");

															}
														}
													} else
														WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation for New Payee");

												} else
													WSClient.writeToReport(LogStatus.PASS, "Blocked : Create Profile for new payee failed!");

											} else
												WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Pay routings!");

										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to checkin reservation");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to assign room");
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to change the status of room to vacant and inspected");

								}

							} else
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation for Payee");

						} else
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");

					} else
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation");

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile ");

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Property Config Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));

			if (!payeeResvID.equals("error") || !payeeResvID.equals(null) || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", payeeResvID);
				CheckoutReservation.checkOutReservation("DS_01");
			}
			//			if (!newReservationId.equals("error") || !newReservationId.equals(null) || !newReservationId.equals(null)) {
			//				WSClient.setData("{var_resvId}", newReservationId);
			//				CheckoutReservation.checkOutReservation("DS_01");
			//			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePayRouting", "ResvAdvanced", "OWS" })
	/**
	 * Verify that routing instruction is not for a invalid reservation.
	 * PreRequisites Required: --> One Profile is created -> There should be
	 * market codes,source codes,routing instructions,Payment method,rate
	 * codes,transaction codes,transaction sub group,transaction groups to be
	 * available.
	 *
	 */

	public void updatePayrouting_41275() throws Exception {
		try {
			String testName = "updatePayrouting_41275";
			WSClient.startTest(testName, "Verify that routing instruction is not added for a invalid reservation.", "minimumRegression");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

				/*************
				 * Prerequisite 1 : Room type, Rate Plan Code, Source Code,
				 * Market Code,Transaction Code,Transaction Group,Transaction
				 * Sub Group
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				WSClient.setData("{var_resvId}", WSClient.getKeywordData("{KEYWORD_RANDNUM_3}"));

				/************
				 * Prerequisite 2 : Create profile for payee
				 *********************************/

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created for Payee . </b>");
				payeeProfileID = CreateProfile.createProfile("DS_01");

				if (!payeeProfileID.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO, "<b> Payee Profile ID : " + payeeProfileID + "</b>");

					WSClient.setData("{var_payeeProfileId}", payeeProfileID);
					WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

					/*******************
					 * OWS Update PayRouting Operation
					 ************************/

					String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_01");
					String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

					if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

						} else
							WSClient.writeToReport(LogStatus.FAIL, "The reservation is not cancelled!");
					} else
						WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

					if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
						if (message != "*null*")
							WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
					} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
						if (message != "*null*")
							WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
					} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						if (message != "*null*")
							WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
					} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
						if (message != "*null*")
							WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
					}

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Property Config Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePayRouting", "ResvAdvanced", "OWS" })
	/**
	 * Verify that error message is populated on the response when invalid
	 * transaction code is sent on the request.
	 *
	 * PreRequisites Required: -->Three Profiles are created -->One Reservation
	 * is created --> There should be market codes,source codes,routing
	 * instructions,Payment method,rate codes,transaction codes,transaction sub
	 * group,transaction groups to be available.
	 *
	 */

	public void updatePayrouting_42461() throws Exception {
		try {
			String testName = "updatePayrouting_42461";
			WSClient.startTest(testName, "Verify that error message is populated on  the response when invalid transaction code is sent on the request.", "minimumRegression");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation created for Guest.  </b>");

					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");

					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID.equals("error") || !resvID.equals(null))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvID + "</b>");

						/************
						 * Prerequisite 3: Create profile for Payee
						 *****************/
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created for Payee . </b>");

						payeeProfileID = CreateProfile.createProfile("DS_01");

						if (!payeeProfileID.equals("error")) {

							WSClient.writeToReport(LogStatus.INFO, "<b>Payee Profile ID : " + payeeProfileID + "</b>");

							WSClient.setData("{var_profileId}", payeeProfileID);

							WSClient.setData("{var_payeeProfileId}", payeeProfileID);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_profileId}", profileID);
							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
							WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
							WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));

							/*** Prerequisite 4: Create Routing Instructions **/

							String payroutReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
							String payroutRes = WSClient.processSOAPMessage(payroutReq);

							if (WSAssert.assertIfElementExists(payroutRes, "CreateRoutingInstructionsRS_Success", true)) {
								String updateQuery = WSClient.getQuery("OWSUpdatePayRouting", "QS_01");
								String routeId = WSClient.getDBRow(updateQuery).get("ROUTING_INSTRUCTIONS_ID");
								String folio = WSClient.getDBRow(updateQuery).get("FOLIO_VIEW");
								WSClient.writeToReport(LogStatus.INFO, "<b>Routing Instruction ID :" + routeId + "</b>");

								WSClient.setData("{var_folio}", folio);
								WSClient.setData("{var_routeId}", routeId);
								WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04") + "X");

								/************
								 * Prerequisite 5: Create profile to attach to a
								 * new Payee
								 *********************************/
								WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created for New Payee. </b>");

								newProfileId = CreateProfile.createProfile("DS_01");
								if (!newProfileId.equals("error")) {
									WSClient.writeToReport(LogStatus.INFO, "<b> Payee New Profile ID : " + newProfileId + "</b>");
									WSClient.setData("{var_payeeProfileId}", newProfileId);

									OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

									/*******************
									 * OWS Update PayRouting Operation
									 ************************/

									String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_02");
									String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

									if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
										if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

										} else
											WSClient.writeToReport(LogStatus.FAIL, "The update payrouting operation is successful");
									} else
										WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

									if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

										/****
										 * Verifying that the error message is
										 * populated on the response
										 ********/

										String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
										if (message != "*null*")
											WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
									}

									if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

										/****
										 * Verifying whether the error Message
										 * is populated on the response
										 ****/

										String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
										if (message != "*null*")
											WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
									} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

										/****
										 * Verifying whether the error Message
										 * is populated on the response
										 ****/

										String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										if (message != "*null*")
											WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
									}

									if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

										/****
										 * Verifying whether the error Message
										 * is populated on the response
										 ****/

										String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
										if (message != "*null*")
											WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
									}

								} else
									WSClient.writeToReport(LogStatus.PASS, "Blocked : Create Profile for new payee failed!");

							} else
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Pay routings!");

						} else
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");

					} else
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation");

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile ");

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Property Config Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error") || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePayRouting", "ResvAdvanced", "OWS" })
	/**
	 * Verify that pay routing details are getting inserted when invalid routing
	 * Id is sent on the request. PreRequisites Required: --> Three Profiles are
	 * created -->One Reservations are created -> There should be market
	 * codes,source codes,routing instructions,Payment method,rate
	 * codes,transaction codes,transaction sub group,transaction groups to be
	 * available.
	 *
	 */

	public void updatePayrouting_42467() throws Exception {
		try {
			String testName = "updatePayrouting_42467";
			WSClient.startTest(testName, "Verify that payrouting details are getting inserted when invalid routing Id is sent on the request.", "minimumRegression");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation created for Guest. </b>");
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");

					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID.equals("error") || !resvID.equals(null))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvID + "</b>");

						/************
						 * Prerequisite 3: Create profile for Payee
						 *****************/

						WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created for Payee . </b>");

						payeeProfileID = CreateProfile.createProfile("DS_01");

						if (!payeeProfileID.equals("error")) {

							WSClient.writeToReport(LogStatus.INFO, "<b>Payee Profile ID : " + payeeProfileID + "</b>");

							WSClient.setData("{var_profileId}", payeeProfileID);

							WSClient.setData("{var_payeeProfileId}", payeeProfileID);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_profileId}", profileID);
							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));

							String updateQuery = WSClient.getQuery("OWSUpdatePayRouting", "QS_06");
							String routeId = WSClient.getDBRow(updateQuery).get("ROUTE_ID");

							WSClient.setData("{var_routeId}", routeId);

							/************
							 * Prerequisite 5: Create profile to attach to a new
							 * Payee
							 *********************************/

							WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created for New Payee . </b>");

							newProfileId = CreateProfile.createProfile("DS_01");
							if (!newProfileId.equals("error")) {
								WSClient.writeToReport(LogStatus.INFO, "<b> Payee New Profile ID : " + newProfileId + "</b>");
								WSClient.setData("{var_payeeProfileId}", newProfileId);

								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

								/*******************
								 * OWS Update PayRouting Operation
								 ************************/

								String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_09");
								String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

								if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
									if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										String trxCode = WSClient.getElementValue(updateResvResq, "RoutingCodes_RoutingInstructionCode_InstructionCode", XMLType.REQUEST);
										System.out.println(trxCode);
										String query = WSClient.getQuery("QS_02");
										System.out.println(query);
										LinkedHashMap<String, String> db = WSClient.getDBRow(query);
										System.out.println(db);

										/******
										 * Validation Against the database
										 ******/

										if (WSAssert.assertEquals(newProfileId, db.get("BILL_TO_NAME_ID"), true)) {
											WSClient.writeToReport(LogStatus.PASS, "New Profile ID -> 	Expected :		" + newProfileId + "		 Actual :	 " + db.get("BILL_TO_NAME_ID"));

										} else {
											WSClient.writeToReport(LogStatus.FAIL, "New Profile ID ->	 Expected : 		" + newProfileId + " 		Actual : 	 " + db.get("BILL_TO_NAME_ID"));

										}

										if (WSAssert.assertEquals(trxCode, db.get("TRX_CODE"), true)) {
											WSClient.writeToReport(LogStatus.PASS, "Transaction Code -> 	Expected :		" + trxCode + "		 Actual :	 " + db.get("TRX_CODE"));

										} else {
											WSClient.writeToReport(LogStatus.FAIL, "Transaction Code  ->	 Expected : 		" + trxCode + " 		Actual : 	 " + db.get("TRX_CODE"));

										}

									} else
										WSClient.writeToReport(LogStatus.FAIL, "The update payrouting operation has failed!");
								} else
									WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

								if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

									/****
									 * Verifying that the error message is
									 * populated on the response
									 ********/

									String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
									if (message != "*null*")
										WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
								}

								if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

									/****
									 * Verifying whether the error Message is
									 * populated on the response
									 ****/

									String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
									if (message != "*null*")
										WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
								} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

									/****
									 * Verifying whether the error Message is
									 * populated on the response
									 ****/

									String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									if (message != "*null*")
										WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
								}

								if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

									/****
									 * Verifying whether the error Message is
									 * populated on the response
									 ****/

									String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
									if (message != "*null*")
										WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
								}

							} else
								WSClient.writeToReport(LogStatus.PASS, "Blocked : Create Profile for new payee failed!");

						} else
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");

					} else
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation");

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile ");

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Property Config Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error") || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePayRouting", "ResvAdvanced", "OWS" })
	/**
	 * Verify that error message is populated on the response when reservation
	 * Id of payee is not "CHECKED IN". PreRequisites Required: --> Three
	 * Profiles are created -->Three Reservations are created -> There should be
	 * market codes,source codes,routing instructions,Payment method,rate
	 * codes,transaction codes,transaction sub group,transaction groups to be
	 * available. ->New payee Reservation should NOT be CHECKED IN
	 *
	 */

	public void updatePayrouting_42466() throws Exception {
		try {
			String testName = "updatePayrouting_42466";
			WSClient.startTest(testName, "Verify that error message is populated on the response when reservation Id of Payee is not 'CHECKED IN'", "minimumRegression");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code,Transaction Code,Transaction Group,Transaction Sub Group
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation are created for Guest.</b>");

				profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID.equals("error") || !resvID.equals(null))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvID + "</b>");

						/************
						 * Prerequisite 3: Create profile for payee
						 *********************************/

						WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created for Payee . </b>");
						payeeProfileID = CreateProfile.createProfile("DS_01");

						if (!payeeProfileID.equals("error")) {

							WSClient.writeToReport(LogStatus.INFO, "<b>Payee Profile ID : " + payeeProfileID + "</b>");

							WSClient.setData("{var_profileId}", payeeProfileID);

							WSClient.setData("{var_payeeProfileId}", payeeProfileID);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_profileId}", profileID);
							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
							WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
							WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));

							/*****
							 * Prerequisite 4: Opera Create Routing Instructions
							 ******/

							String payroutReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
							String payroutRes = WSClient.processSOAPMessage(payroutReq);

							if (WSAssert.assertIfElementExists(payroutRes, "CreateRoutingInstructionsRS_Success", true)) {

								String updateQuery = WSClient.getQuery("OWSUpdatePayRouting", "QS_01");
								String routeId = WSClient.getDBRow(updateQuery).get("ROUTING_INSTRUCTIONS_ID");

								WSClient.writeToReport(LogStatus.INFO, "<b>Routing Instruction ID :" + routeId + "</b>");

								WSClient.setData("{var_routeId}", routeId);
								WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));
								WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_04"));
								WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_04"));

								/************
								 * Prerequisite 5: Create profile for new Payee
								 *********************************/
								WSClient.writeToReport(LogStatus.INFO, "<b>Profile and Reservation are created New Payee . </b>");

								newProfileId = CreateProfile.createProfile("DS_01");

								WSClient.writeToReport(LogStatus.INFO, "<b> New Profile ID : " + newProfileId + "</b>");
								if (!newProfileId.equals("error")) {

									/*******************
									 * Prerequisite 12:Create a Reservation for
									 * new Payee
									 ************************/

									WSClient.setData("{var_profileId}", newProfileId);
									newReservationId = CreateReservation.createReservation("DS_13").get("reservationId");

									if (!newReservationId.equals("error") || !newReservationId.equals(null)) {

										WSClient.writeToReport(LogStatus.INFO, "<b>New Reservation ID :" + newReservationId + "</b>");

										WSClient.setData("{var_profileId}", profileID);
										WSClient.setData("{var_resvId}", resvID);
										WSClient.setData("{var_payeeProfileId}", newProfileId);
										WSClient.setData("{var_payeeResvId}", newReservationId);

										OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
										/*******************
										 * OWS Update PayRouting Operation
										 ************************/

										String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_03");
										String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

										if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
											if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

											} else
												WSClient.writeToReport(LogStatus.FAIL, "The update payrouting operation was successful!");
										} else
											WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

										if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

											/****
											 * Verifying that the error message
											 * is populated on the response
											 ********/

											String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
											if (message != "*null*")
												WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
										}

										if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

											/****
											 * Verifying whether the error
											 * Message is populated on the
											 * response
											 ****/

											String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
											if (message != "*null*")
												WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
										} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

											/****
											 * Verifying whether the error
											 * Message is populated on the
											 * response
											 ****/

											String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											if (message != "*null*")
												WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
										}

										if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

											/****
											 * Verifying whether the error
											 * Message is populated on the
											 * response
											 ****/

											String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
											if (message != "*null*")
												WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
										}

									} else
										WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation for New Payee");

								} else
									WSClient.writeToReport(LogStatus.PASS, "Blocked : Create Profile for new payee failed!");

							} else
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Pay routings!");

						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation");
					}

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile ");
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Property Config Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error") || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}

			if (!newReservationId.equals("error") || !newReservationId.equals(null)) {
				WSClient.setData("{var_resvId}", newReservationId);
				CancelReservation.cancelReservation("DS_02");
			}

		}
	}

	@Test(groups = { "minimumRegression", "UpdatePayRouting", "ResvAdvanced", "OWS" })
	/**
	 * Verify that error message is populated on the response when no
	 * transaction code is sent on the request.
	 *
	 * PreRequisites Required: --> Three Profiles are created -->One
	 * Reservations are created -> There should be market codes,source
	 * codes,routing instructions,Payment method,rate codes,transaction
	 * codes,transaction sub group,transaction groups to be available.
	 *
	 */

	public void updatePayrouting_42465() throws Exception {
		try {
			String testName = "updatePayrouting_42465";
			WSClient.startTest(testName, "Verify that error message is populated on  the response when no transaction code is sent on the request.", "minimumRegression");
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
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PaymentMethod", "SourceCode", "MarketCode", "TransactionGroup", "TransactionCode", "TransactionSubGroup" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_extresort}", OWSLib.getChannelResort(resortOperaValue, channel));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile and reservation created for Guest . </b>");
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");

					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					resvID = CreateReservation.createReservation("DS_13").get("reservationId");
					if (!resvID.equals("error") || !resvID.equals(null))

					{
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvID + "</b>");

						/************
						 * Prerequisite 3: Create profile for Payee
						 *****************/
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created for Payee . </b>");

						payeeProfileID = CreateProfile.createProfile("DS_01");

						if (!payeeProfileID.equals("error")) {

							WSClient.writeToReport(LogStatus.INFO, "<b>Payee Profile ID : " + payeeProfileID + "</b>");

							WSClient.setData("{var_profileId}", payeeProfileID);

							WSClient.setData("{var_payeeProfileId}", payeeProfileID);
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_profileId}", profileID);
							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
							WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
							WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));

							/*** Prerequisite 4: Create Routing Instructions **/

							String payroutReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
							String payroutRes = WSClient.processSOAPMessage(payroutReq);

							if (WSAssert.assertIfElementExists(payroutRes, "CreateRoutingInstructionsRS_Success", true)) {
								String updateQuery = WSClient.getQuery("OWSUpdatePayRouting", "QS_01");
								String routeId = WSClient.getDBRow(updateQuery).get("ROUTING_INSTRUCTIONS_ID");
								String folio = WSClient.getDBRow(updateQuery).get("FOLIO_VIEW");
								WSClient.writeToReport(LogStatus.INFO, "<b>Routing Instruction ID :" + routeId + "</b>");

								WSClient.setData("{var_folio}", folio);
								WSClient.setData("{var_routeId}", routeId);

								/************
								 * Prerequisite 5: Create profile to attach to a
								 * new Payee
								 *********************************/

								WSClient.writeToReport(LogStatus.INFO, "<b>Profile is created for New Payee . </b>");

								newProfileId = CreateProfile.createProfile("DS_01");
								if (!newProfileId.equals("error")) {
									WSClient.writeToReport(LogStatus.INFO, "<b> Payee New Profile ID : " + newProfileId + "</b>");
									WSClient.setData("{var_payeeProfileId}", newProfileId);

									OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

									/*******************
									 * OWS Update PayRouting Operation
									 ************************/

									String updateResvResq = WSClient.createSOAPMessage("OWSUpdatePayRouting", "DS_10");
									String updateResvRess = WSClient.processSOAPMessage(updateResvResq);

									if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", false)) {
										if (WSAssert.assertIfElementValueEquals(updateResvRess, "UpdatePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

										} else
											WSClient.writeToReport(LogStatus.FAIL, "The update payrouting operation is successful");
									} else
										WSClient.writeToReport(LogStatus.FAIL, " The  update payrouting operation has failed! ");

									if (WSAssert.assertIfElementExists(updateResvRess, "Result_Text_TextElement", true)) {

										/****
										 * Verifying that the error message is
										 * populated on the response
										 ********/

										String message = WSAssert.getElementValue(updateResvRess, "Result_Text_TextElement", XMLType.RESPONSE);
										if (message != "*null*")
											WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
									}

									if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", true)) {

										/****
										 * Verifying whether the error Message
										 * is populated on the response
										 ****/

										String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_GDSError", XMLType.RESPONSE);
										if (message != "*null*")
											WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
									} else if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", true)) {

										/****
										 * Verifying whether the error Message
										 * is populated on the response
										 ****/

										String message = WSAssert.getElementValue(updateResvRess, "UpdatePayRoutingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										if (message != "*null*")
											WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
									}

									if (WSAssert.assertIfElementExists(updateResvRess, "UpdatePayRoutingResponse_faultcode", true)) {

										/****
										 * Verifying whether the error Message
										 * is populated on the response
										 ****/

										String message = WSClient.getElementValue(updateResvRess, "UpdatePayRoutingResponse_faultstring", XMLType.RESPONSE);
										if (message != "*null*")
											WSClient.writeToReport(LogStatus.INFO, "The error populated on the response is : <b>" + message + "</b>");
									}

								} else
									WSClient.writeToReport(LogStatus.PASS, "Blocked : Create Profile for new payee failed!");

							} else
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Pay routings!");

						} else
							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile for Payee");

					} else
						WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Reservation");

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Create Profile ");

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked : Property Config Data");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error") || !resvID.equals(null)) {
				WSClient.setData("{var_resvId}", resvID);
				CancelReservation.cancelReservation("DS_02");
			}

		}
	}

}
