package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CheckoutReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.GenerateFolio;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class MakePayment extends WSSetUp {

	public void setOwsHeader() throws SQLException {
		String resort = OPERALib.getResort();
		String channel = OWSLib.getChannel();
		String uname = OPERALib.getUserName();
		String pwd = OPERALib.getPassword();
		String channelType = OWSLib.getChannelType(channel);
		String channelCarrier = OWSLib.getChannelCarier(resort, channel);
		OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	}

	@Test(groups = { "sanity", "MakePayment", "ResvAdvanced", "OWS", "P1"})
	public void makeCashPayment() {
		String profileID = "", reservationID = "", resort = "";
		try {
			String testName = "makeCashPayment";
			WSClient.startTest(testName, "Verify that the Cash Payment is successful for a checked-in reservation", "sanity");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String uname = OPERALib.getUserName();
			String channel = OWSLib.getChannel();
			resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);

			HashMap<String, String> resvMap = new HashMap<>();
			String roomNumber = "";

			// Check if ENV Setup is Complete
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/*************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					/**************
					 * Prerequisite 2:Create a Reservation
					 *********************************/

					resvMap = CreateReservation.createReservation("DS_12");
					if (!resvMap.get("reservationId").equals("error")) {
						reservationID = resvMap.get("reservationId");
						WSClient.setData("{var_resvId}", reservationID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + reservationID + "</b>");

						/*************
						 * Prerequisite 3: Fetching available Hotel rooms with
						 * room type
						 **********************************/

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
						String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

						if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
							roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						} else {

							/*****************
							 * Prerequisite 4: Creating a room to assign
							 *********************************/

							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
							String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

							if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
								roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
							}
						}

						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : " + roomNumber + "</b>");

						/*******************
						 * Prerequisite 5: Changing the room status to inspected
						 * to assign the room for checking in
						 *************************************/

						String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
						String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

						if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {
							/********************
							 * Prerequisite 6: Assign Room
							 ************************************/
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room</b>");
							String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
							String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

							if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

								/********************
								 * Prerequisite 7: CheckIn Reservation
								 **************************************/

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");
								String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");
									WSClient.setData("{var_reservationID}", reservationID);
									String QS02 = WSClient.getQuery("OWSMakePayment", "QS_01");
									System.out.println(QS02);
									LinkedHashMap<String, String> financialTransactions1 = WSClient.getDBRow(QS02);
									System.out.println(financialTransactions1);

									WSClient.setData("{var_resort}", resort);
									WSClient.setData("{var_resvId}", reservationID);
									WSClient.setData("{var_cashierID}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
									WSClient.setData("{var_cashierId}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
									WSClient.setData("{var_trx}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));

									/********************
									 * Prerequisite 8: Apply Final Postings
									 **************************************/

									String applyFinalPostingsReq = WSClient.createSOAPMessage("ApplyFinalPostings", "DS_01");
									String applyFinalPostingsRes = WSClient.processSOAPMessage(applyFinalPostingsReq);

									String QS01 = WSClient.getQuery("OWSMakePayment", "QS_01");
									System.out.println(QS01);
									LinkedHashMap<String, String> financialTransactionsAfterApplyingPostings = WSClient.getDBRow(QS01);
									System.out.println(financialTransactionsAfterApplyingPostings);
									String balanceBefore = "";

									if (financialTransactionsAfterApplyingPostings.get("BALANCE") == null)
										balanceBefore = "0";
									else
										balanceBefore = financialTransactionsAfterApplyingPostings.get("BALANCE");

									int finalBalance = (Integer.parseInt(balanceBefore) < 0) ? Integer.parseInt(balanceBefore) : 0;

									if (WSAssert.assertIfElementExists(applyFinalPostingsRes, "ApplyFinalPostingsRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO, "<b>" + "Applied final postings" + "</b>");

										/*********************
										 * Prerequisite 9: Post Charges (To Pay
										 * the exact balance Amount)
										 ****************************************/

										String postBillingChargesReq = WSClient.createSOAPMessage("PostBillingCharges", "DS_02");
										String postBillingChargesRes = WSClient.processSOAPMessage(postBillingChargesReq);

										if (WSAssert.assertIfElementExists(postBillingChargesRes, "PostBillingChargesRS_Success", true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Posted Billing Charges to Reservation</b>");
											String folioNumber = WSClient.getElementValue(postBillingChargesReq, "Criteria_Charges_Charge_FolioWindowNo", XMLType.REQUEST);
											setOwsHeader();
											WSClient.setData("{var_reservationID}", reservationID);
											WSClient.setData("{var_resort}", resort);
											WSClient.setData("{var_postDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
											WSClient.setData("{var_userName}", uname);
											WSClient.setData("{var_folioNo}", folioNumber);

											LinkedHashMap<String, String> financialTransactionsAfterPayment = WSClient.getDBRow(QS01);
											System.out.println(financialTransactionsAfterPayment);
											String balance = "";
											if (financialTransactionsAfterPayment.get("BALANCE") == null)
												balance = "0";
											else
												balance = financialTransactionsAfterPayment.get("BALANCE");

											WSClient.setData("{var_charge}", balance);
											int totalAmmountPosted = Integer.parseInt(balance);
											String makePaymentReq = WSClient.createSOAPMessage("OWSMakePayment", "DS_01");
											String makePaymentRes = WSClient.processSOAPMessage(makePaymentReq);

											if (WSAssert.assertIfElementExists(makePaymentRes, "MakePaymentResponse_Result_resultStatusFlag", true)) {
												if (WSAssert.assertIfElementValueEquals(makePaymentRes, "MakePaymentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
													if (WSAssert.assertIfElementValueEquals(makePaymentRes, "MakePaymentResponse_ReservationID_UniqueID", reservationID, false)) {
														WSClient.writeToReport(LogStatus.INFO, "<b>Retrieveing Folio to validate if payment is made to the reservation</b>");
														WSClient.setData("{var_reservationID}", reservationID);
														WSClient.setData("{var_resort}", resort);
														String amountPaid = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_Charge", XMLType.REQUEST);
														int totalAmountPaid = Integer.parseInt(amountPaid);
														String longInfo = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_LongInfo", XMLType.REQUEST);
														String postDate = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_PostDate", XMLType.REQUEST);
														String reference = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Reference", XMLType.REQUEST);

														WSClient.writeToReport(LogStatus.INFO, "<b>Call FetchFolio Operation to see if the Payment Posting is correctly reflected on the folio</b>");
														WSClient.setData("{var_folioWindowNumber}", folioNumber);
														String fetchFolioReq = WSClient.createSOAPMessage("FetchFolio", "DS_01");
														String fetchFolioRes = WSClient.processSOAPMessage(fetchFolioReq);

														if (WSAssert.assertIfElementExists(fetchFolioRes, "FetchFolioRS_Success", true)) {
															if (WSClient.getAttributeValueByAttribute(fetchFolioRes, "ReservationInfo_ReservationIDList_UniqueID", "ID", "Reservation", XMLType.RESPONSE).equals(reservationID)) {

																WSClient.writeToReport(LogStatus.INFO, "<b>Validate if the overall balance is correctly calculated after making the payment</b>");
																WSAssert.assertIfElementValueEquals(fetchFolioRes, "ReservationInfo_RoomStay_Balance_Amount", new Integer(totalAmmountPosted - totalAmountPaid).toString(), false);

																WSClient.writeToReport(LogStatus.INFO, "<b>Validate if Payment and Balance Information is correctly populated on the Folio</b>");
																int totalPayment = totalAmountPaid - finalBalance;

																int folioNodeIndex = WSClient.getNodeIndex(fetchFolioRes, "1", "ReservationFolioInformation_FolioWindows_FolioWindow", "ReservationFolioInformation_FolioWindows_FolioWindow_FolioWindowNo", XMLType.RESPONSE);
																WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_Payment_Amount", "-" + totalPayment, false);
																WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_Balance_Amount", new Integer(totalAmmountPosted - totalAmountPaid).toString(), false);
																WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_PaymentMethod_FolioView", folioNumber, false);
																WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_PaymentMethod_PaymentMethod", "CASH", false);

																WSClient.writeToReport(LogStatus.INFO, "<b>Validating if Posting information (Transaction Type = Payment) is correct on the Folio</b>");
																int postingPaymentNodeIndex = WSClient.getSecondLevelNodeIndex(fetchFolioRes, "Payment", "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", "Postings_Posting_TransactionType", XMLType.RESPONSE);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Folio_Postings_Posting_PostingDate", XMLType.RESPONSE, postDate, false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Folio_Postings_Posting_TransactionDate", XMLType.RESPONSE, postDate, false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Remark", XMLType.RESPONSE, longInfo, false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Reference", XMLType.RESPONSE, reference, false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_PostedAmount_Amount", XMLType.RESPONSE, amountPaid, false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Price_Amount", XMLType.RESPONSE, amountPaid, false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Quantity", XMLType.RESPONSE, "1", false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_TransactionType", XMLType.RESPONSE, "Payment", false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_CreditAmount_Amount", XMLType.RESPONSE, amountPaid, false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Posting_Exchange_CreditAmount_Amount", XMLType.RESPONSE, amountPaid, false);
															} else {
																WSClient.writeToReport(LogStatus.FAIL, "<b>Reservation " + reservationID + " is not found on the response </b>");
															}
														} else {
															WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the folio</b>");
														}
														WSClient.writeToReport(LogStatus.INFO, "<b>Validate that the Payment is not going into the Deposits");
														String fetchDepositFolioReq = WSClient.createSOAPMessage("FetchDepositFolio", "DS_01");
														String fetchDepositFolioRes = WSClient.processSOAPMessage(fetchDepositFolioReq);

														if (WSAssert.assertIfElementExists(fetchDepositFolioRes, "FetchDepositFolioRS_Success", false)) {
															if (WSClient.getAttributeValueByAttribute(fetchDepositFolioRes, "ReservationInfo_ReservationIDList_UniqueID", "ID", "Reservation", XMLType.RESPONSE).equals(reservationID)) {
																WSAssert.assertIfElementDoesNotExist(fetchDepositFolioRes, "Deposits_Deposit_PostedAmount", false);
															} else {
																WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the Deposit Folio for the reservation " + reservationID + "</b>");
															}
														} else {
															WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the Deposit Folio</b>");
														}
													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag is found, but the ReservationID is missing on the response");
													}
												} else {
													WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag != SUCCESS");
												}
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag is not found on the response");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Failed to post chrges");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to apply final postings");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to checkin reservation");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to assign room");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to change the status of room to vacant and inspected");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a reservation failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a profile failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisites of reservation creation failed ");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// checkout the Reservation.
			try {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				WSClient.setData("{var_cashierId}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
				WSClient.setData("{var_resvId}", reservationID);
				WSClient.setData("{var_profileId}", profileID);
				boolean generateFolio = GenerateFolio.generateFolio("DS_01");
				if (generateFolio) {
					CheckoutReservation.checkOut("DS_05");
				}
				System.out.println("In Finally");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "MakePayment", "ResvAdvanced", "OWS", "P1" })
	public void makeCashDepositAndPayment() {
		String profileID = "", reservationID = "", resort = "", referenceStr = "";
		try {
			String testName = "makeCashDepositAndPayment";
			WSClient.startTest(testName, "Verify the following (1) Cash Payment is going into the Deposit when it's paid before checking-in the guest (2) Cash Payment is reflected on the Folio when it's paid after check-in", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String uname = OPERALib.getUserName();
			String channel = OWSLib.getChannel();
			resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);

			HashMap<String, String> resvMap = new HashMap<>();
			String roomNumber = "";

			// Check if ENV Setup is Complete
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/*************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					/**************
					 * Prerequisite 2:Create a Reservation
					 **************/

					resvMap = CreateReservation.createReservation("DS_12");
					if (!resvMap.get("reservationId").equals("error")) {
						reservationID = resvMap.get("reservationId");
						WSClient.setData("{var_resvId}", reservationID);

						/***
						 * Prerequisite 3: Fetching available Hotel rooms with
						 * room type
						 ***/

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
						String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

						if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
							roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						} else {
							/*** Prerequisite 4: Creating a room to assign ***/
							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
							String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

							if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
								roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
							}
						}

						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : " + roomNumber + "</b>");
						/*****
						 * Prerequisite 5: Changing the room status to inspected
						 * to assign the room for checking in
						 ****/

						String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
						String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

						if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {
							/**** Prerequisite 6: Assign Room ****/
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room</b>");
							String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
							String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

							if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

								String folioNumber = "1";
								setOwsHeader();
								WSClient.setData("{var_reservationID}", reservationID);
								WSClient.setData("{var_resort}", resort);
								WSClient.setData("{var_postDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
								WSClient.setData("{var_userName}", uname);
								WSClient.setData("{var_folioNo}", folioNumber);

								String balance = "10";
								WSClient.setData("{var_charge}", balance);
								int firstPayment = Integer.parseInt(balance);
								String makePaymentReq = WSClient.createSOAPMessage("OWSMakePayment", "DS_01");
								String makePaymentRes = WSClient.processSOAPMessage(makePaymentReq);

								if (WSAssert.assertIfElementExists(makePaymentRes, "MakePaymentResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(makePaymentRes, "MakePaymentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										if (WSAssert.assertIfElementValueEquals(makePaymentRes, "MakePaymentResponse_ReservationID_UniqueID", reservationID, false)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Payment Made Via Cash : " + firstPayment + "</b> ");
											referenceStr = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Reference", XMLType.REQUEST);
											String amountPaid1 = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_Charge", XMLType.REQUEST);
											String longInfo = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_LongInfo", XMLType.REQUEST);
											String postDate = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_PostDate", XMLType.REQUEST);

											WSClient.setData("{var_folioWindowNumber}", folioNumber);
											String fetchFolioReq = WSClient.createSOAPMessage("FetchFolio", "DS_01");
											String fetchFolioRes = WSClient.processSOAPMessage(fetchFolioReq);
											if (WSAssert.assertIfElementExists(fetchFolioRes, "FetchFolioRS_Success", true)) {
												if (WSClient.getAttributeValueByAttribute(fetchFolioRes, "ReservationInfo_ReservationIDList_UniqueID", "ID", "Reservation", XMLType.RESPONSE).equals(reservationID)) {
													WSClient.writeToReport(LogStatus.INFO, "<b>Validate balance on the fetchFolio</b>");
													WSAssert.assertIfElementValueEquals(fetchFolioRes, "ReservationInfo_RoomStay_Balance_Amount", "-" + balance, false);

													WSClient.writeToReport(LogStatus.INFO, "<b>Validating if Payment and Balance is zero under Folio window  as the reservation is not checked-in</b>");
													int folioNodeIndex = WSClient.getNodeIndex(fetchFolioRes, "1", "ReservationFolioInformation_FolioWindows_FolioWindow", "ReservationFolioInformation_FolioWindows_FolioWindow_FolioWindowNo", XMLType.RESPONSE);
													WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_Payment_Amount", "0", false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_Balance_Amount", "0", false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_PaymentMethod_FolioView", folioNumber, false);

													WSClient.writeToReport(LogStatus.INFO, "<b>Validating if Posting information (Transaction Type = Payment) is not populated in Folio as the reservation is not checked-in</b>");
													int postingPaymentNodeIndex = WSClient.getSecondLevelNodeIndex(fetchFolioRes, "Payment", "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", "Postings_Posting_TransactionType", XMLType.RESPONSE);
													if (postingPaymentNodeIndex <= 0) {
														WSClient.writeToReport(LogStatus.PASS, "<b>Payment Postings are not avaialble on the response </b>");
													} else {
														WSClient.writeToReport(LogStatus.FAIL, "<b>Payment Postings are not avaialble on the response </b>");
													}
												} else {
													WSClient.writeToReport(LogStatus.FAIL, "<b>Reservation " + reservationID + " is not found on the response </b>");
												}
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the folio</b>");
											}

											WSClient.writeToReport(LogStatus.INFO, "<b>Validating that the Payment is going into the Deposits");
											String fetchDepositFolioReq = WSClient.createSOAPMessage("FetchDepositFolio", "DS_01");
											String fetchDepositFolioRes = WSClient.processSOAPMessage(fetchDepositFolioReq);

											if (WSAssert.assertIfElementExists(fetchDepositFolioRes, "FetchDepositFolioRS_Success", false)) {
												if (WSClient.getAttributeValueByAttribute(fetchDepositFolioRes, "ReservationInfo_ReservationIDList_UniqueID", "ID", "Reservation", XMLType.RESPONSE).equals(reservationID)) {
													int depositNodeIndex = WSClient.getNodeIndex(fetchDepositFolioRes, referenceStr, "ReservationDepositFolio_Deposits_Deposit", "Deposits_Deposit_Reference", XMLType.RESPONSE);

													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_Remark", longInfo, false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_Reference", referenceStr, false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_PostedAmount_Amount", amountPaid1, false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_Price_Amount", amountPaid1, false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_TransactionType", "Payment", false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_CreditAmount_Amount", amountPaid1, false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "ReservationDepositFolio_Deposits_Deposit_TransactionDate", postDate, false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "ReservationDepositFolio_Deposits_Deposit_TransactionAmount", amountPaid1, false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "ReservationDepositFolio_Deposits_Deposit_PostingDate", postDate, false);

												} else {
													WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the Deposit Folio for the reservation " + reservationID + "</b>");
												}
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the Deposit Folio</b>");
											}

											WSClient.writeToReport(LogStatus.INFO, "<b>Checking-in the Guest");
											String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
											String checkInRes = WSClient.processSOAPMessage(checkInReq);

											if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");

												WSClient.setData("{var_reservationID}", reservationID);
												String QS02 = WSClient.getQuery("OWSMakePayment", "QS_01");
												System.out.println(QS02);
												LinkedHashMap<String, String> financialTransactions1 = WSClient.getDBRow(QS02);
												System.out.println("Balance after CheckIn: " + financialTransactions1);

												WSClient.setData("{var_resort}", resort);
												WSClient.setData("{var_resvId}", reservationID);
												WSClient.setData("{var_cashierID}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
												WSClient.setData("{var_cashierId}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
												WSClient.setData("{var_trx}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));

												String applyFinalPostingsReq = WSClient.createSOAPMessage("ApplyFinalPostings", "DS_01");
												String applyFinalPostingsRes = WSClient.processSOAPMessage(applyFinalPostingsReq);
												String QS01 = WSClient.getQuery("OWSMakePayment", "QS_01");
												System.out.println(QS01);
												LinkedHashMap<String, String> financialTransactionsAfterApplyingPostings = WSClient.getDBRow(QS01);
												System.out.println(financialTransactionsAfterApplyingPostings);
												String balanceBefore = "";

												if (financialTransactionsAfterApplyingPostings.get("BALANCE") == null)
													balanceBefore = "0";
												else
													balanceBefore = financialTransactionsAfterApplyingPostings.get("BALANCE");

												int balanceAfterFirstPayment = (Integer.parseInt(balanceBefore) < 0) ? Integer.parseInt(balanceBefore) : 0;

												WSClient.writeToReport(LogStatus.INFO, "<b> Balance Amount to be paid: " + balanceBefore + "</b> ");

												boolean balanceIsNegative = false;
												if (Integer.parseInt(balanceBefore) < 0) {
													balanceIsNegative = true;
												}

												if (WSAssert.assertIfElementExists(applyFinalPostingsRes, "ApplyFinalPostingsRS_Success", true)) {
													WSClient.writeToReport(LogStatus.INFO, "<b>" + "Applied final postings" + "</b>");

													String postBillingChargesReq = WSClient.createSOAPMessage("PostBillingCharges", "DS_02");
													String postBillingChargesRes = WSClient.processSOAPMessage(postBillingChargesReq);

													if (WSAssert.assertIfElementExists(postBillingChargesRes, "PostBillingChargesRS_Success", true)) {
														WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Posted Billing Charges to Reservation</b>");
														folioNumber = WSClient.getElementValue(postBillingChargesReq, "Criteria_Charges_Charge_FolioWindowNo", XMLType.REQUEST);
														int chargesPosted = Integer.parseInt(WSClient.getElementValue(postBillingChargesReq, "Charges_Charge_Price_Amount", XMLType.REQUEST)) * Integer.parseInt(WSClient.getElementValue(postBillingChargesReq, "Charges_Charge_PostingQuantity", XMLType.REQUEST));

														WSClient.writeToReport(LogStatus.INFO, "<b> Charges Posted: " + chargesPosted + "</b> ");

														setOwsHeader();
														WSClient.setData("{var_reservationID}", reservationID);
														WSClient.setData("{var_resort}", resort);
														WSClient.setData("{var_postDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
														WSClient.setData("{var_userName}", uname);
														WSClient.setData("{var_folioNo}", folioNumber);

														LinkedHashMap<String, String> financialTransactionsAfterPayment = WSClient.getDBRow(QS01);
														System.out.println(financialTransactionsAfterPayment);
														balance = "";
														if (financialTransactionsAfterPayment.get("BALANCE") == null)
															balance = "0";
														else
															balance = financialTransactionsAfterPayment.get("BALANCE");

														WSClient.setData("{var_charge}", balance);
														int balanceToBePaid = Integer.parseInt(balance);
														String makePaymentReq2 = WSClient.createSOAPMessage("OWSMakePayment", "DS_01");
														String makePaymentRes2 = WSClient.processSOAPMessage(makePaymentReq2);

														if (WSAssert.assertIfElementExists(makePaymentRes2, "MakePaymentResponse_Result_resultStatusFlag", true)) {
															if (WSAssert.assertIfElementValueEquals(makePaymentRes2, "MakePaymentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
																if (WSAssert.assertIfElementValueEquals(makePaymentRes2, "MakePaymentResponse_ReservationID_UniqueID", reservationID, false)) {
																	WSClient.writeToReport(LogStatus.INFO, "<b> Payment Made via Cash: " + balanceToBePaid + "</b> ");
																	WSClient.writeToReport(LogStatus.INFO, "<b>Retrieveing Folio to validate if payment is made to the reservation</b>");
																	WSClient.setData("{var_reservationID}", reservationID);
																	WSClient.setData("{var_resort}", resort);
																	longInfo = WSClient.getElementValue(makePaymentReq2, "MakePaymentRequest_Posting_LongInfo", XMLType.REQUEST);
																	postDate = WSClient.getElementValue(makePaymentReq2, "MakePaymentRequest_Posting_PostDate", XMLType.REQUEST);
																	String reference = WSClient.getElementValue(makePaymentReq2, "MakePaymentRequest_Reference", XMLType.REQUEST);
																	String amountPaid2 = WSClient.getElementValue(makePaymentReq2, "MakePaymentRequest_Posting_Charge", XMLType.REQUEST);
																	int secondPayment = Integer.parseInt(amountPaid2);
																	WSClient.setData("{var_folioWindowNumber}", folioNumber);
																	String fetchFolioReq2 = WSClient.createSOAPMessage("FetchFolio", "DS_01");
																	String fetchFolioRes2 = WSClient.processSOAPMessage(fetchFolioReq2);

																	if (WSAssert.assertIfElementExists(fetchFolioRes2, "FetchFolioRS_Success", true)) {
																		if (WSClient.getAttributeValueByAttribute(fetchFolioRes2, "ReservationInfo_ReservationIDList_UniqueID", "ID", "Reservation", XMLType.RESPONSE).equals(reservationID)) {

																			WSClient.writeToReport(LogStatus.INFO, "<b>Validating if the overall balance is correctly calculated after making the payment</b>");
																			WSAssert.assertIfElementValueEquals(fetchFolioRes2, "ReservationInfo_RoomStay_Balance_Amount", new Integer(balanceToBePaid - secondPayment).toString(), false);

																			WSClient.writeToReport(LogStatus.INFO, "<b>Validating if Payment and Balance Information is correct on the Folio</b>");
																			int totalPayment = 0;
																			if (balanceIsNegative == true) {
																				totalPayment = ((secondPayment - balanceAfterFirstPayment) - firstPayment);
																			} else {
																				// totalPayment=-(totalAmmountPosted);
																				totalPayment = (secondPayment);
																			}

																			int folioNodeIndex = WSClient.getNodeIndex(fetchFolioRes2, "1", "ReservationFolioInformation_FolioWindows_FolioWindow", "ReservationFolioInformation_FolioWindows_FolioWindow_FolioWindowNo", XMLType.RESPONSE);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_Payment_Amount", "-" + totalPayment, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_Balance_Amount", new Integer(balanceToBePaid - secondPayment).toString(), false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_PaymentMethod_FolioView", folioNumber, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_PaymentMethod_PaymentMethod", "CASH", false);

																			WSClient.writeToReport(LogStatus.INFO, "<b>Validating if Posting information (Transaction Type = Payment) is correct on the Folio</b>");
																			int postingPaymentNodeIndex = WSClient.getSecondLevelNodeIndex(fetchFolioRes2, reference, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", "Postings_Posting_Reference", XMLType.RESPONSE);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Folio_Postings_Posting_PostingDate", XMLType.RESPONSE, postDate, false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Folio_Postings_Posting_TransactionDate", XMLType.RESPONSE, postDate, false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Remark", XMLType.RESPONSE, longInfo, false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Reference", XMLType.RESPONSE, reference, false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_PostedAmount_Amount", XMLType.RESPONSE, amountPaid2, false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Price_Amount", XMLType.RESPONSE, amountPaid2, false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Quantity", XMLType.RESPONSE, "1", false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_TransactionType", XMLType.RESPONSE, "Payment", false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_CreditAmount_Amount", XMLType.RESPONSE, amountPaid2, false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Posting_Exchange_CreditAmount_Amount", XMLType.RESPONSE, amountPaid2, false);

																		} else {
																			WSClient.writeToReport(LogStatus.FAIL, "<b>Reservation " + reservationID + " is not found on the response </b>");
																		}
																	} else {
																		WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the folio</b>");
																	}

																	WSClient.writeToReport(LogStatus.INFO, "<b>Validating that the Payment is not added to the existing deposit");
																	String fetchDepositFolioReq2 = WSClient.createSOAPMessage("FetchDepositFolio", "DS_01");
																	String fetchDepositFolioRes2 = WSClient.processSOAPMessage(fetchDepositFolioReq2);

																	if (WSAssert.assertIfElementExists(fetchDepositFolioRes2, "FetchDepositFolioRS_Success", false)) {
																		if (WSClient.getAttributeValueByAttribute(fetchDepositFolioRes2, "ReservationInfo_ReservationIDList_UniqueID", "ID", "Reservation", XMLType.RESPONSE).equals(reservationID)) {
																			int depositNodeIndex = WSClient.getNodeIndex(fetchDepositFolioRes2, referenceStr, "ReservationDepositFolio_Deposits_Deposit", "Deposits_Deposit_Reference", XMLType.RESPONSE);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_Remark", longInfo, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_Reference", referenceStr, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_PostedAmount_Amount", amountPaid1, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_Price_Amount", amountPaid1, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_TransactionType", "Payment", false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_CreditAmount_Amount", amountPaid1, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "ReservationDepositFolio_Deposits_Deposit_TransactionDate", postDate, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "ReservationDepositFolio_Deposits_Deposit_TransactionAmount", amountPaid1, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "ReservationDepositFolio_Deposits_Deposit_PostingDate", postDate, false);

																		} else {
																			WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the Deposit Folio for the reservation " + reservationID + "</b>");
																		}
																	} else {
																		WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the Deposit Folio</b>");
																	}

																} else {
																	WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag is found, but the ReservationID is missing on the response");
																}
															} else {
																WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag != SUCCESS");
															}
														} else {
															WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag is not found on the response");
														}
													} else {
														WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Failed to post chrges");
													}
												} else {
													WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to apply final postings");
												}
												// Inner Loop
											} else {
												WSClient.writeToReport(LogStatus.INFO, "<b>Check-in failed</b>");
											}

										} else {
											WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag is found, but the ReservationID is missing on the response");
										}
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag != SUCCESS");
									}
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag is not found on the response");
								}
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Room Assignment is failed");
							}
						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Changing the HK Status to clean is failed");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failure >> Reservation Creation is failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failure >> Profile Creation is failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite collection for reservation creation is failed");
			}
		} catch (Exception e) {

		} finally {
			try {
				// cancel the Reservation.
				WSClient.setData("{var_resort}", OPERALib.getResort());
				WSClient.setData("{var_reservation_id}", reservationID);
				WSClient.setData("{var_profileId}", profileID);
				CancelReservation.cancelReservation("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "sanity", "MakePayment", "ResvAdvanced", "OWS", "P1", "makeVaultCreditCardPayment" })
	public void makeVaultCreditCardPayment() {
		String profileID = "", reservationID = "", resort = "";
		try {
			String testName = "makeVaultCreditCardPayment";
			WSClient.startTest(testName, "Verify that the Credit Card Payment is successful for a checked-in reservation", "sanity");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String uname = OPERALib.getUserName();
			String channel = OWSLib.getChannel();
			resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);

			HashMap<String, String> resvMap = new HashMap<>();
			String roomNumber = "";

			// Check if ENV Setup is Complete
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/*************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{var_cardHolderName}", fname + " " + lname);
					WSClient.setData("{var_paymentMethod}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_03"));
					// WSClient.setData("{var_cardNumber}", "340000000001239");
					// WSClient.setData("{var_expDate}", "2020-05-20");
					WSClient.setData("{var_cardNumber}", "34000000000" + WSClient.getKeywordData("{KEYWORD_RANDNUM_4}"));
					WSClient.setData("{var_expDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_600}"));
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					/**************
					 * Prerequisite 2:Create a Reservation
					 *********************************/
					resvMap = CreateReservation.createReservation("DS_12");
					if (!resvMap.get("reservationId").equals("error")) {
						reservationID = resvMap.get("reservationId");
						WSClient.setData("{var_resvId}", reservationID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + reservationID + "</b>");

						/*************
						 * Prerequisite 3: Fetching available Hotel rooms with
						 * room type
						 **********************************/

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
						String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

						if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
							roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						} else {

							/*****************
							 * Prerequisite 4: Creating a room to assign
							 *********************************/

							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
							String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

							if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
								roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
							}
						}

						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : " + roomNumber + "</b>");

						/*******************
						 * Prerequisite 5: Changing the room status to inspected
						 * to assign the room for checking in
						 *************************************/

						String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
						String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

						if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {
							/********************
							 * Prerequisite 6: Assign Room
							 ************************************/
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room</b>");
							String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
							String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

							if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

								/********************
								 * Prerequisite 7: CheckIn Reservation
								 **************************************/

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");
								String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");
									WSClient.setData("{var_reservationID}", reservationID);
									String QS02 = WSClient.getQuery("OWSMakePayment", "QS_01");
									System.out.println(QS02);
									LinkedHashMap<String, String> financialTransactions1 = WSClient.getDBRow(QS02);
									System.out.println(financialTransactions1);

									WSClient.setData("{var_resort}", resort);
									WSClient.setData("{var_resvId}", reservationID);
									WSClient.setData("{var_cashierID}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
									WSClient.setData("{var_cashierId}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
									WSClient.setData("{var_trx}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));

									/********************
									 * Prerequisite 8: Apply Final Postings
									 **************************************/

									String applyFinalPostingsReq = WSClient.createSOAPMessage("ApplyFinalPostings", "DS_01");
									String applyFinalPostingsRes = WSClient.processSOAPMessage(applyFinalPostingsReq);

									String QS01 = WSClient.getQuery("OWSMakePayment", "QS_01");
									System.out.println(QS01);
									LinkedHashMap<String, String> financialTransactionsAfterApplyingPostings = WSClient.getDBRow(QS01);
									System.out.println(financialTransactionsAfterApplyingPostings);
									String balanceBefore = "";

									if (financialTransactionsAfterApplyingPostings.get("BALANCE") == null)
										balanceBefore = "0";
									else
										balanceBefore = financialTransactionsAfterApplyingPostings.get("BALANCE");

									int finalBalance = (Integer.parseInt(balanceBefore) < 0) ? Integer.parseInt(balanceBefore) : 0;

									if (WSAssert.assertIfElementExists(applyFinalPostingsRes, "ApplyFinalPostingsRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO, "<b>" + "Applied final postings" + "</b>");

										/*********************
										 * Prerequisite 9: Post Charges (To Pay
										 * the exact balance Amount)
										 ****************************************/

										String postBillingChargesReq = WSClient.createSOAPMessage("PostBillingCharges", "DS_02");
										String postBillingChargesRes = WSClient.processSOAPMessage(postBillingChargesReq);

										if (WSAssert.assertIfElementExists(postBillingChargesRes, "PostBillingChargesRS_Success", true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Posted Billing Charges to Reservation</b>");
											String folioNumber = WSClient.getElementValue(postBillingChargesReq, "Criteria_Charges_Charge_FolioWindowNo", XMLType.REQUEST);
											setOwsHeader();
											WSClient.setData("{var_reservationID}", reservationID);
											WSClient.setData("{var_resort}", resort);
											WSClient.setData("{var_postDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
											WSClient.setData("{var_userName}", uname);
											WSClient.setData("{var_folioNo}", folioNumber);

											LinkedHashMap<String, String> financialTransactionsAfterPayment = WSClient.getDBRow(QS01);
											System.out.println(financialTransactionsAfterPayment);
											String balance = "";
											if (financialTransactionsAfterPayment.get("BALANCE") == null)
												balance = "0";
											else
												balance = financialTransactionsAfterPayment.get("BALANCE");

											WSClient.setData("{var_charge}", balance);
											int totalAmmountPosted = Integer.parseInt(balance);
											String makePaymentReq = WSClient.createSOAPMessage("OWSMakePayment", "DS_02");
											String makePaymentRes = WSClient.processSOAPMessage(makePaymentReq);

											if (WSAssert.assertIfElementExists(makePaymentRes, "MakePaymentResponse_Result_resultStatusFlag", true)) {
												if (WSAssert.assertIfElementValueEquals(makePaymentRes, "MakePaymentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
													if (WSAssert.assertIfElementValueEquals(makePaymentRes, "MakePaymentResponse_ReservationID_UniqueID", reservationID, false)) {
														WSClient.writeToReport(LogStatus.INFO, "<b>Retrieveing Folio to validate if payment is made to the reservation</b>");
														WSClient.setData("{var_reservationID}", reservationID);
														WSClient.setData("{var_resort}", resort);
														String amountPaid = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_Charge", XMLType.REQUEST);
														int totalAmountPaid = Integer.parseInt(amountPaid);
														String longInfo = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_LongInfo", XMLType.REQUEST);
														String postDate = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_PostDate", XMLType.REQUEST);
														String reference = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Reference", XMLType.REQUEST);

														WSClient.writeToReport(LogStatus.INFO, "<b>Call FetchFolio Operation to see if the Payment Posting is correctly reflected on the folio</b>");
														WSClient.setData("{var_folioWindowNumber}", folioNumber);
														String fetchFolioReq = WSClient.createSOAPMessage("FetchFolio", "DS_01");
														String fetchFolioRes = WSClient.processSOAPMessage(fetchFolioReq);

														if (WSAssert.assertIfElementExists(fetchFolioRes, "FetchFolioRS_Success", true)) {
															if (WSClient.getAttributeValueByAttribute(fetchFolioRes, "ReservationInfo_ReservationIDList_UniqueID", "ID", "Reservation", XMLType.RESPONSE).equals(reservationID)) {

																WSClient.writeToReport(LogStatus.INFO, "<b>Validate if the overall balance is correctly calculated after making the payment</b>");
																WSAssert.assertIfElementValueEquals(fetchFolioRes, "ReservationInfo_RoomStay_Balance_Amount", new Integer(totalAmmountPosted - totalAmountPaid).toString(), false);

																WSClient.writeToReport(LogStatus.INFO, "<b>Validate if Payment and Balance Information is correctly populated on the Folio</b>");
																int totalPayment = totalAmountPaid - finalBalance;

																int folioNodeIndex = WSClient.getNodeIndex(fetchFolioRes, "1", "ReservationFolioInformation_FolioWindows_FolioWindow", "ReservationFolioInformation_FolioWindows_FolioWindow_FolioWindowNo", XMLType.RESPONSE);
																WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_Payment_Amount", "-" + totalPayment, false);
																WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_Balance_Amount", new Integer(totalAmmountPosted - totalAmountPaid).toString(), false);
																WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_PaymentMethod_FolioView", folioNumber, false);
																WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_PaymentMethod_PaymentMethod", "CASH", false);

																WSClient.writeToReport(LogStatus.INFO, "<b>Validating if Posting information (Transaction Type = Payment) is correct on the Folio</b>");
																int postingPaymentNodeIndex = WSClient.getSecondLevelNodeIndex(fetchFolioRes, "Payment", "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", "Postings_Posting_TransactionType", XMLType.RESPONSE);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Folio_Postings_Posting_PostingDate", XMLType.RESPONSE, postDate, false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Folio_Postings_Posting_TransactionDate", XMLType.RESPONSE, postDate, false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Remark", XMLType.RESPONSE, longInfo, false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Reference", XMLType.RESPONSE, reference, false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_PostedAmount_Amount", XMLType.RESPONSE, amountPaid, false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Price_Amount", XMLType.RESPONSE, amountPaid, false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Quantity", XMLType.RESPONSE, "1", false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_TransactionType", XMLType.RESPONSE, "Payment", false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_CreditAmount_Amount", XMLType.RESPONSE, amountPaid, false);
																WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Posting_Exchange_CreditAmount_Amount", XMLType.RESPONSE, amountPaid, false);
															} else {
																WSClient.writeToReport(LogStatus.FAIL, "<b>Reservation " + reservationID + " is not found on the response </b>");
															}
														} else {
															WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the folio</b>");
														}
														WSClient.writeToReport(LogStatus.INFO, "<b>Validate that the Payment is not going into the Deposits");
														String fetchDepositFolioReq = WSClient.createSOAPMessage("FetchDepositFolio", "DS_01");
														String fetchDepositFolioRes = WSClient.processSOAPMessage(fetchDepositFolioReq);

														if (WSAssert.assertIfElementExists(fetchDepositFolioRes, "FetchDepositFolioRS_Success", false)) {
															if (WSClient.getAttributeValueByAttribute(fetchDepositFolioRes, "ReservationInfo_ReservationIDList_UniqueID", "ID", "Reservation", XMLType.RESPONSE).equals(reservationID)) {
																WSAssert.assertIfElementDoesNotExist(fetchDepositFolioRes, "Deposits_Deposit_PostedAmount", false);
															} else {
																WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the Deposit Folio for the reservation " + reservationID + "</b>");
															}
														} else {
															WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the Deposit Folio</b>");
														}
													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag is found, but the ReservationID is missing on the response");
													}
												} else {
													WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag != SUCCESS");
												}
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag is not found on the response");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Failed to post chrges");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to apply final postings");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to checkin reservation");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to assign room");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to change the status of room to vacant and inspected");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a reservation failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a profile failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisites of reservation creation failed ");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// checkout the Reservation.
			try {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				WSClient.setData("{var_cashierId}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
				WSClient.setData("{var_resvId}", reservationID);
				WSClient.setData("{var_profileId}", profileID);
				boolean generateFolio = GenerateFolio.generateFolio("DS_01");
				if (generateFolio) {
					CheckoutReservation.checkOut("DS_05");
				}
				System.out.println("In Finally");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "MakePayment", "ResvAdvanced", "OWS", "P1", "makeCreditCardDepositAndPayment" })
	public void makeCreditCardDepositAndPayment() {
		String profileID = "", reservationID = "", resort = "", referenceStr = "";
		try {
			String testName = "makeCreditCardDepositAndPayment";
			WSClient.startTest(testName, "Verify the following (1) Credit Card Payment is going into the Deposit when it's paid before checking-in the guest (2) Credit Card Payment is reflected on the Folio when it's paid after check-in ", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String uname = OPERALib.getUserName();
			String channel = OWSLib.getChannel();
			resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);

			HashMap<String, String> resvMap = new HashMap<>();
			String roomNumber = "";

			// Check if ENV Setup is Complete
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/*************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
					WSClient.setData("{var_cardHolderName}", fname + " " + lname);
					WSClient.setData("{var_paymentMethod}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_03"));
					// WSClient.setData("{var_cardNumber}", "340000000001239");
					// WSClient.setData("{var_expDate}", "2020-05-20");
					WSClient.setData("{var_cardNumber}", "34000000000" + WSClient.getKeywordData("{KEYWORD_RANDNUM_4}"));
					WSClient.setData("{var_expDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_600}"));

					/**************
					 * Prerequisite 2:Create a Reservation
					 **************/

					resvMap = CreateReservation.createReservation("DS_12");
					if (!resvMap.get("reservationId").equals("error")) {
						reservationID = resvMap.get("reservationId");
						WSClient.setData("{var_resvId}", reservationID);

						/***
						 * Prerequisite 3: Fetching available Hotel rooms with
						 * room type
						 ***/

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
						String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

						if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
							roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						} else {
							/*** Prerequisite 4: Creating a room to assign ***/
							String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
							String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

							if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
								roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
							}
						}

						WSClient.setData("{var_roomNumber}", roomNumber);
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : " + roomNumber + "</b>");
						
						/*****
						 * Prerequisite 5: Changing the room status to inspected
						 * to assign the room for checking in
						 ****/

						String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
						String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

						if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {
							/**** Prerequisite 6: Assign Room ****/
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room</b>");
							String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
							String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

							if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

								String folioNumber = "1";
								setOwsHeader();
								WSClient.setData("{var_reservationID}", reservationID);
								WSClient.setData("{var_resort}", resort);
								WSClient.setData("{var_postDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
								WSClient.setData("{var_userName}", uname);
								WSClient.setData("{var_folioNo}", folioNumber);

								String balance = "10";
								WSClient.setData("{var_charge}", balance);
								int firstPayment = Integer.parseInt(balance);
								String makePaymentReq = WSClient.createSOAPMessage("OWSMakePayment", "DS_02");
								String makePaymentRes = WSClient.processSOAPMessage(makePaymentReq);

								if (WSAssert.assertIfElementExists(makePaymentRes, "MakePaymentResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(makePaymentRes, "MakePaymentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										if (WSAssert.assertIfElementValueEquals(makePaymentRes, "MakePaymentResponse_ReservationID_UniqueID", reservationID, false)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Payment Made Via Cash : " + firstPayment + "</b> ");
											referenceStr = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Reference", XMLType.REQUEST);
											String amountPaid1 = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_Charge", XMLType.REQUEST);
											String longInfo = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_LongInfo", XMLType.REQUEST);
											String postDate = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_PostDate", XMLType.REQUEST);

											WSClient.setData("{var_folioWindowNumber}", folioNumber);
											String fetchFolioReq = WSClient.createSOAPMessage("FetchFolio", "DS_01");
											String fetchFolioRes = WSClient.processSOAPMessage(fetchFolioReq);
											if (WSAssert.assertIfElementExists(fetchFolioRes, "FetchFolioRS_Success", true)) {
												if (WSClient.getAttributeValueByAttribute(fetchFolioRes, "ReservationInfo_ReservationIDList_UniqueID", "ID", "Reservation", XMLType.RESPONSE).equals(reservationID)) {
													WSClient.writeToReport(LogStatus.INFO, "<b>Validate balance on the fetchFolio</b>");
													WSAssert.assertIfElementValueEquals(fetchFolioRes, "ReservationInfo_RoomStay_Balance_Amount", "-" + balance, false);

													WSClient.writeToReport(LogStatus.INFO, "<b>Validating if Payment and Balance is zero under Folio window  as the reservation is not checked-in</b>");
													int folioNodeIndex = WSClient.getNodeIndex(fetchFolioRes, "1", "ReservationFolioInformation_FolioWindows_FolioWindow", "ReservationFolioInformation_FolioWindows_FolioWindow_FolioWindowNo", XMLType.RESPONSE);
													WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_Payment_Amount", "0", false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_Balance_Amount", "0", false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_PaymentMethod_FolioView", folioNumber, false);

													WSClient.writeToReport(LogStatus.INFO, "<b>Validating if Posting information (Transaction Type = Payment) is not populated in Folio as the reservation is not checked-in</b>");
													int postingPaymentNodeIndex = WSClient.getSecondLevelNodeIndex(fetchFolioRes, "Payment", "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", "Postings_Posting_TransactionType", XMLType.RESPONSE);
													if (postingPaymentNodeIndex <= 0) {
														WSClient.writeToReport(LogStatus.PASS, "<b>Payment Postings are not avaialble on the response </b>");
													} else {
														WSClient.writeToReport(LogStatus.FAIL, "<b>Payment Postings are not avaialble on the response </b>");
													}
												} else {
													WSClient.writeToReport(LogStatus.FAIL, "<b>Reservation " + reservationID + " is not found on the response </b>");
												}
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the folio</b>");
											}

											WSClient.writeToReport(LogStatus.INFO, "<b>Validating that the Payment is going into the Deposits");
											String fetchDepositFolioReq = WSClient.createSOAPMessage("FetchDepositFolio", "DS_01");
											String fetchDepositFolioRes = WSClient.processSOAPMessage(fetchDepositFolioReq);

											if (WSAssert.assertIfElementExists(fetchDepositFolioRes, "FetchDepositFolioRS_Success", false)) {
												if (WSClient.getAttributeValueByAttribute(fetchDepositFolioRes, "ReservationInfo_ReservationIDList_UniqueID", "ID", "Reservation", XMLType.RESPONSE).equals(reservationID)) {
													int depositNodeIndex = WSClient.getNodeIndex(fetchDepositFolioRes, referenceStr, "ReservationDepositFolio_Deposits_Deposit", "Deposits_Deposit_Reference", XMLType.RESPONSE);

													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_Remark", longInfo, false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_Reference", referenceStr, false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_PostedAmount_Amount", amountPaid1, false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_Price_Amount", amountPaid1, false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_TransactionType", "Payment", false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_CreditAmount_Amount", amountPaid1, false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "ReservationDepositFolio_Deposits_Deposit_TransactionDate", postDate, false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "ReservationDepositFolio_Deposits_Deposit_TransactionAmount", amountPaid1, false);
													WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "ReservationDepositFolio_Deposits_Deposit_PostingDate", postDate, false);

												} else {
													WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the Deposit Folio for the reservation " + reservationID + "</b>");
												}
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the Deposit Folio</b>");
											}

											WSClient.writeToReport(LogStatus.INFO, "<b>Checking-in the Guest");
											String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
											String checkInRes = WSClient.processSOAPMessage(checkInReq);

											if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");

												WSClient.setData("{var_reservationID}", reservationID);
												String QS02 = WSClient.getQuery("OWSMakePayment", "QS_01");
												System.out.println(QS02);
												LinkedHashMap<String, String> financialTransactions1 = WSClient.getDBRow(QS02);
												System.out.println("Balance after CheckIn: " + financialTransactions1);

												WSClient.setData("{var_resort}", resort);
												WSClient.setData("{var_resvId}", reservationID);
												WSClient.setData("{var_cashierID}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
												WSClient.setData("{var_cashierId}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
												WSClient.setData("{var_trx}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));

												String applyFinalPostingsReq = WSClient.createSOAPMessage("ApplyFinalPostings", "DS_01");
												String applyFinalPostingsRes = WSClient.processSOAPMessage(applyFinalPostingsReq);
												String QS01 = WSClient.getQuery("OWSMakePayment", "QS_01");
												System.out.println(QS01);
												LinkedHashMap<String, String> financialTransactionsAfterApplyingPostings = WSClient.getDBRow(QS01);
												System.out.println(financialTransactionsAfterApplyingPostings);
												String balanceBefore = "";

												if (financialTransactionsAfterApplyingPostings.get("BALANCE") == null)
													balanceBefore = "0";
												else
													balanceBefore = financialTransactionsAfterApplyingPostings.get("BALANCE");

												int balanceAfterFirstPayment = (Integer.parseInt(balanceBefore) < 0) ? Integer.parseInt(balanceBefore) : 0;

												WSClient.writeToReport(LogStatus.INFO, "<b> Balance Amount to be paid: " + balanceBefore + "</b> ");

												boolean balanceIsNegative = false;
												if (Integer.parseInt(balanceBefore) < 0) {
													balanceIsNegative = true;
												}

												if (WSAssert.assertIfElementExists(applyFinalPostingsRes, "ApplyFinalPostingsRS_Success", true)) {
													WSClient.writeToReport(LogStatus.INFO, "<b>" + "Applied final postings" + "</b>");

													String postBillingChargesReq = WSClient.createSOAPMessage("PostBillingCharges", "DS_02");
													String postBillingChargesRes = WSClient.processSOAPMessage(postBillingChargesReq);

													if (WSAssert.assertIfElementExists(postBillingChargesRes, "PostBillingChargesRS_Success", true)) {
														WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Posted Billing Charges to Reservation</b>");
														folioNumber = WSClient.getElementValue(postBillingChargesReq, "Criteria_Charges_Charge_FolioWindowNo", XMLType.REQUEST);
														int chargesPosted = Integer.parseInt(WSClient.getElementValue(postBillingChargesReq, "Charges_Charge_Price_Amount", XMLType.REQUEST)) * Integer.parseInt(WSClient.getElementValue(postBillingChargesReq, "Charges_Charge_PostingQuantity", XMLType.REQUEST));

														WSClient.writeToReport(LogStatus.INFO, "<b> Charges Posted: " + chargesPosted + "</b> ");

														setOwsHeader();
														WSClient.setData("{var_reservationID}", reservationID);
														WSClient.setData("{var_resort}", resort);
														WSClient.setData("{var_postDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
														WSClient.setData("{var_userName}", uname);
														WSClient.setData("{var_folioNo}", folioNumber);

														LinkedHashMap<String, String> financialTransactionsAfterPayment = WSClient.getDBRow(QS01);
														System.out.println(financialTransactionsAfterPayment);
														balance = "";
														if (financialTransactionsAfterPayment.get("BALANCE") == null)
															balance = "0";
														else
															balance = financialTransactionsAfterPayment.get("BALANCE");

														WSClient.setData("{var_charge}", balance);
														int balanceToBePaid = Integer.parseInt(balance);
														String makePaymentReq2 = WSClient.createSOAPMessage("OWSMakePayment", "DS_02");
														String makePaymentRes2 = WSClient.processSOAPMessage(makePaymentReq2);

														if (WSAssert.assertIfElementExists(makePaymentRes2, "MakePaymentResponse_Result_resultStatusFlag", true)) {
															if (WSAssert.assertIfElementValueEquals(makePaymentRes2, "MakePaymentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
																if (WSAssert.assertIfElementValueEquals(makePaymentRes2, "MakePaymentResponse_ReservationID_UniqueID", reservationID, false)) {
																	WSClient.writeToReport(LogStatus.INFO, "<b> Payment Made via Cash: " + balanceToBePaid + "</b> ");
																	WSClient.writeToReport(LogStatus.INFO, "<b>Retrieveing Folio to validate if payment is made to the reservation</b>");
																	WSClient.setData("{var_reservationID}", reservationID);
																	WSClient.setData("{var_resort}", resort);
																	longInfo = WSClient.getElementValue(makePaymentReq2, "MakePaymentRequest_Posting_LongInfo", XMLType.REQUEST);
																	postDate = WSClient.getElementValue(makePaymentReq2, "MakePaymentRequest_Posting_PostDate", XMLType.REQUEST);
																	String reference = WSClient.getElementValue(makePaymentReq2, "MakePaymentRequest_Reference", XMLType.REQUEST);
																	String amountPaid2 = WSClient.getElementValue(makePaymentReq2, "MakePaymentRequest_Posting_Charge", XMLType.REQUEST);
																	int secondPayment = Integer.parseInt(amountPaid2);
																	WSClient.setData("{var_folioWindowNumber}", folioNumber);
																	String fetchFolioReq2 = WSClient.createSOAPMessage("FetchFolio", "DS_01");
																	String fetchFolioRes2 = WSClient.processSOAPMessage(fetchFolioReq2);

																	if (WSAssert.assertIfElementExists(fetchFolioRes2, "FetchFolioRS_Success", true)) {
																		if (WSClient.getAttributeValueByAttribute(fetchFolioRes2, "ReservationInfo_ReservationIDList_UniqueID", "ID", "Reservation", XMLType.RESPONSE).equals(reservationID)) {

																			WSClient.writeToReport(LogStatus.INFO, "<b>Validating if the overall balance is correctly calculated after making the payment</b>");
																			WSAssert.assertIfElementValueEquals(fetchFolioRes2, "ReservationInfo_RoomStay_Balance_Amount", new Integer(balanceToBePaid - secondPayment).toString(), false);

																			WSClient.writeToReport(LogStatus.INFO, "<b>Validating if Payment and Balance Information is correct on the Folio</b>");
																			int totalPayment = 0;
																			if (balanceIsNegative == true) {																				
																				totalPayment = ((secondPayment - balanceAfterFirstPayment) - firstPayment);
																			} else {
																				// totalPayment=-(totalAmmountPosted);
																				totalPayment = (secondPayment);
																			}

																			int folioNodeIndex = WSClient.getNodeIndex(fetchFolioRes2, "1", "ReservationFolioInformation_FolioWindows_FolioWindow", "ReservationFolioInformation_FolioWindows_FolioWindow_FolioWindowNo", XMLType.RESPONSE);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_Payment_Amount", "-" + totalPayment, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_Balance_Amount", new Integer(balanceToBePaid - secondPayment).toString(), false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_PaymentMethod_FolioView", folioNumber, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_PaymentMethod_PaymentMethod", "CASH", false);

																			WSClient.writeToReport(LogStatus.INFO, "<b>Validating if Posting information (Transaction Type = Payment) is correct on the Folio</b>");
																			int postingPaymentNodeIndex = WSClient.getSecondLevelNodeIndex(fetchFolioRes2, reference, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", "Postings_Posting_Reference", XMLType.RESPONSE);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Folio_Postings_Posting_PostingDate", XMLType.RESPONSE, postDate, false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Folio_Postings_Posting_TransactionDate", XMLType.RESPONSE, postDate, false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Remark", XMLType.RESPONSE, longInfo, false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Reference", XMLType.RESPONSE, reference, false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_PostedAmount_Amount", XMLType.RESPONSE, amountPaid2, false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Price_Amount", XMLType.RESPONSE, amountPaid2, false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Quantity", XMLType.RESPONSE, "1", false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_TransactionType", XMLType.RESPONSE, "Payment", false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_CreditAmount_Amount", XMLType.RESPONSE, amountPaid2, false);
																			WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Posting_Exchange_CreditAmount_Amount", XMLType.RESPONSE, amountPaid2, false);

																		} else {
																			WSClient.writeToReport(LogStatus.FAIL, "<b>Reservation " + reservationID + " is not found on the response </b>");
																		}
																	} else {
																		WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the folio</b>");
																	}

																	WSClient.writeToReport(LogStatus.INFO, "<b>Validating that the Payment is not added to the existing deposit");
																	String fetchDepositFolioReq2 = WSClient.createSOAPMessage("FetchDepositFolio", "DS_01");
																	String fetchDepositFolioRes2 = WSClient.processSOAPMessage(fetchDepositFolioReq2);

																	if (WSAssert.assertIfElementExists(fetchDepositFolioRes2, "FetchDepositFolioRS_Success", false)) {
																		if (WSClient.getAttributeValueByAttribute(fetchDepositFolioRes2, "ReservationInfo_ReservationIDList_UniqueID", "ID", "Reservation", XMLType.RESPONSE).equals(reservationID)) {
																			int depositNodeIndex = WSClient.getNodeIndex(fetchDepositFolioRes2, referenceStr, "ReservationDepositFolio_Deposits_Deposit", "Deposits_Deposit_Reference", XMLType.RESPONSE);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_Remark", longInfo, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_Reference", referenceStr, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_PostedAmount_Amount", amountPaid1, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_Price_Amount", amountPaid1, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_TransactionType", "Payment", false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "Deposits_Deposit_CreditAmount_Amount", amountPaid1, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "ReservationDepositFolio_Deposits_Deposit_TransactionDate", postDate, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "ReservationDepositFolio_Deposits_Deposit_TransactionAmount", amountPaid1, false);
																			WSAssert.assertIfElementValueEqualsByIndex(fetchDepositFolioRes2, "ReservationDepositFolio_Deposits_Deposit", depositNodeIndex, "ReservationDepositFolio_Deposits_Deposit_PostingDate", postDate, false);

																		} else {
																			WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the Deposit Folio for the reservation " + reservationID + "</b>");
																		}
																	} else {
																		WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the Deposit Folio</b>");
																	}

																} else {
																	WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag is found, but the ReservationID is missing on the response");
																}
															} else {
																WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag != SUCCESS");
															}
														} else {
															WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag is not found on the response");
														}
													} else {
														WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Failed to post chrges");
													}
												} else {
													WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to apply final postings");
												}
												// Inner Loop
											} else {
												WSClient.writeToReport(LogStatus.INFO, "<b>Check-in failed</b>");
											}

										} else {
											WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag is found, but the ReservationID is missing on the response");
										}
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag != SUCCESS");
									}
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag is not found on the response");
								}
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "Room Assignment is failed");
							}
						} else {
							WSClient.writeToReport(LogStatus.FAIL, "Changing the HK Status to clean is failed");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failure >> Reservation Creation is failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failure >> Profile Creation is failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite collection for reservation creation is failed");
			}
		} catch (Exception e) {

		} finally {
			try {
				// cancel the Reservation.
				WSClient.setData("{var_resort}", OPERALib.getResort());
				WSClient.setData("{var_reservation_id}", reservationID);
				WSClient.setData("{var_profileId}", profileID);
				CancelReservation.cancelReservation("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "MakePayment", "ResvAdvanced", "OWS", "P1", "UNDER_AUTOMATION_13" })
	public void makeCashPaymentPostStay() {
		String profileID = "", reservationID = "", resort = "";
		try {
			String testName = "makeCashPaymentPostStay";
			WSClient.startTest(testName, "Verify that the Cash Payment is successful for a checked-out reservation for which Post-stay charge flag is enabled", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String uname = OPERALib.getUserName();
			String channel = OWSLib.getChannel();
			resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);

			HashMap<String, String> resvMap = new HashMap<>();
			String roomNumber = "";

			// Check if ENV Setup is Complete
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/*************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					/**************
					 * Prerequisite 2:Create a Reservation
					 *********************************/

					resvMap = CreateReservation.createReservation("DS_38");
					if (!resvMap.get("reservationId").equals("error")) {
						reservationID = resvMap.get("reservationId");
						WSClient.setData("{var_resvId}", reservationID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + reservationID + "</b>");

						WSClient.setData("{var_GuaranteeCode}", OperaPropConfig.getDataSetForCode("ReservationType", "DS_04"));
						WSClient.setData("{var_paymentMethod}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						WSClient.writeToReport(LogStatus.INFO, "<b>Changing Reservation to enable Post Stay Charges </b>");
						String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_13");
						String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

						if (WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success", true)) {

							/*************
							 * Prerequisite 3: Fetching available Hotel rooms
							 * with room type
							 **********************************/

							String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
							String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

							if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
								roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
							} else {

								/*****************
								 * Prerequisite 4: Creating a room to assign
								 *********************************/

								String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
								String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

								if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
									roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
								}
							}

							WSClient.setData("{var_roomNumber}", roomNumber);
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : " + roomNumber + "</b>");

							/*******************
							 * Prerequisite 5: Changing the room status to
							 * inspected to assign the room for checking in
							 *************************************/

							String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
							String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

							if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {
								/********************
								 * Prerequisite 6: Assign Room
								 ************************************/
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room</b>");
								String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
								String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

								if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

									/********************
									 * Prerequisite 7: CheckIn Reservation
									 **************************************/

									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");
									String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
									String checkInRes = WSClient.processSOAPMessage(checkInReq);

									if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");
										WSClient.setData("{var_reservationID}", reservationID);
										String QS02 = WSClient.getQuery("OWSMakePayment", "QS_01");
										System.out.println(QS02);
										LinkedHashMap<String, String> financialTransactions1 = WSClient.getDBRow(QS02);
										System.out.println(financialTransactions1);

										WSClient.setData("{var_resort}", resort);
										WSClient.setData("{var_resvId}", reservationID);
										WSClient.setData("{var_cashierID}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
										WSClient.setData("{var_cashierId}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
										WSClient.setData("{var_trx}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));

										/********************
										 * Prerequisite 8: Apply Final Postings
										 **************************************/

										String applyFinalPostingsReq = WSClient.createSOAPMessage("ApplyFinalPostings", "DS_01");
										String applyFinalPostingsRes = WSClient.processSOAPMessage(applyFinalPostingsReq);

										String QS01 = WSClient.getQuery("OWSMakePayment", "QS_01");
										System.out.println(QS01);
										LinkedHashMap<String, String> financialTransactionsAfterApplyingPostings = WSClient.getDBRow(QS01);
										System.out.println(financialTransactionsAfterApplyingPostings);
										String balanceBefore = "";

										if (financialTransactionsAfterApplyingPostings.get("BALANCE") == null)
											balanceBefore = "0";
										else
											balanceBefore = financialTransactionsAfterApplyingPostings.get("BALANCE");

										int finalBalance = (Integer.parseInt(balanceBefore) < 0) ? Integer.parseInt(balanceBefore) : 0;

										if (WSAssert.assertIfElementExists(applyFinalPostingsRes, "ApplyFinalPostingsRS_Success", true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>" + "Applied final postings" + "</b>");

											/*********************
											 * Prerequisite 9: Post Charges (To
											 * Pay the exact balance Amount)
											 ****************************************/

											String postBillingChargesReq = WSClient.createSOAPMessage("PostBillingCharges", "DS_02");
											String postBillingChargesRes = WSClient.processSOAPMessage(postBillingChargesReq);

											if (WSAssert.assertIfElementExists(postBillingChargesRes, "PostBillingChargesRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Posted Billing Charges to Reservation</b>");
												String folioNumber = WSClient.getElementValue(postBillingChargesReq, "Criteria_Charges_Charge_FolioWindowNo", XMLType.REQUEST);
												setOwsHeader();
												WSClient.setData("{var_reservationID}", reservationID);
												WSClient.setData("{var_resort}", resort);
												WSClient.setData("{var_postDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
												WSClient.setData("{var_userName}", uname);
												WSClient.setData("{var_folioNo}", folioNumber);

												LinkedHashMap<String, String> financialTransactionsAfterPayment = WSClient.getDBRow(QS01);
												System.out.println(financialTransactionsAfterPayment);
												String balance = "";
												if (financialTransactionsAfterPayment.get("BALANCE") == null)
													balance = "0";
												else
													balance = financialTransactionsAfterPayment.get("BALANCE");

												WSClient.setData("{var_charge}", balance);
												int totalAmmountPosted = Integer.parseInt(balance);
												String makePaymentReq = WSClient.createSOAPMessage("OWSMakePayment", "DS_01");
												String makePaymentRes = WSClient.processSOAPMessage(makePaymentReq);

												if (WSAssert.assertIfElementExists(makePaymentRes, "MakePaymentResponse_Result_resultStatusFlag", true)) {
													if (WSAssert.assertIfElementValueEquals(makePaymentRes, "MakePaymentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
														if (WSAssert.assertIfElementValueEquals(makePaymentRes, "MakePaymentResponse_ReservationID_UniqueID", reservationID, false)) {
															WSClient.writeToReport(LogStatus.INFO, "<b>Retrieveing Folio to validate if payment is made to the reservation</b>");
															WSClient.setData("{var_reservationID}", reservationID);
															WSClient.setData("{var_resort}", resort);
															String amountPaid = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_Charge", XMLType.REQUEST);
															int totalAmountPaid = Integer.parseInt(amountPaid);
															String longInfo = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_LongInfo", XMLType.REQUEST);
															String postDate = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_PostDate", XMLType.REQUEST);
															String reference = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Reference", XMLType.REQUEST);

															WSClient.writeToReport(LogStatus.INFO, "<b>Call FetchFolio Operation to see if the Payment Posting is correctly reflected on the folio</b>");
															WSClient.setData("{var_folioWindowNumber}", folioNumber);
															String fetchFolioReq = WSClient.createSOAPMessage("FetchFolio", "DS_01");
															String fetchFolioRes = WSClient.processSOAPMessage(fetchFolioReq);

															if (WSAssert.assertIfElementExists(fetchFolioRes, "FetchFolioRS_Success", true)) {
																if (WSClient.getAttributeValueByAttribute(fetchFolioRes, "ReservationInfo_ReservationIDList_UniqueID", "ID", "Reservation", XMLType.RESPONSE).equals(reservationID)) {

																	WSClient.writeToReport(LogStatus.INFO, "<b>Validate if the overall balance is correctly calculated after making the payment</b>");
																	WSAssert.assertIfElementValueEquals(fetchFolioRes, "ReservationInfo_RoomStay_Balance_Amount", new Integer(totalAmmountPosted - totalAmountPaid).toString(), false);

																	WSClient.writeToReport(LogStatus.INFO, "<b>Validate if Payment and Balance Information is correctly populated on the Folio</b>");
																	int totalPayment = totalAmountPaid - finalBalance;

																	int folioNodeIndex = WSClient.getNodeIndex(fetchFolioRes, "1", "ReservationFolioInformation_FolioWindows_FolioWindow", "ReservationFolioInformation_FolioWindows_FolioWindow_FolioWindowNo", XMLType.RESPONSE);
																	WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_Payment_Amount", "-" + totalPayment, false);
																	WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_Balance_Amount", new Integer(totalAmmountPosted - totalAmountPaid).toString(), false);
																	WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_PaymentMethod_FolioView", folioNumber, false);
																	WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_PaymentMethod_PaymentMethod", "CASH", false);

																	WSClient.writeToReport(LogStatus.INFO, "<b>Validating if Posting information (Transaction Type = Payment) is correct on the Folio</b>");
																	int postingPaymentNodeIndex = WSClient.getSecondLevelNodeIndex(fetchFolioRes, "Payment", "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", "Postings_Posting_TransactionType", XMLType.RESPONSE);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Folio_Postings_Posting_PostingDate", XMLType.RESPONSE, postDate, false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Folio_Postings_Posting_TransactionDate", XMLType.RESPONSE, postDate, false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Remark", XMLType.RESPONSE, longInfo, false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Reference", XMLType.RESPONSE, reference, false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_PostedAmount_Amount", XMLType.RESPONSE, amountPaid, false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Price_Amount", XMLType.RESPONSE, amountPaid, false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Quantity", XMLType.RESPONSE, "1", false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_TransactionType", XMLType.RESPONSE, "Payment", false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_CreditAmount_Amount", XMLType.RESPONSE, amountPaid, false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Posting_Exchange_CreditAmount_Amount", XMLType.RESPONSE, amountPaid, false);
																} else {
																	WSClient.writeToReport(LogStatus.FAIL, "<b>Reservation " + reservationID + " is not found on the response </b>");
																}

																WSClient.setData("{var_resort}", OPERALib.getResort());
																WSClient.setData("{var_cashierId}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
																WSClient.setData("{var_resvId}", reservationID);
																WSClient.setData("{var_profileId}", profileID);
																boolean generateFolio = GenerateFolio.generateFolio("DS_01");
																if (generateFolio) {
																	CheckoutReservation.checkOut("DS_05");
																}
																int postStayCharge = Integer.parseInt(WSClient.getKeywordData("{KEYWORD_RANDNUM_2}"));
																WSClient.setData("{var_amount}", String.valueOf(postStayCharge));
																WSClient.setData("{var_quantity}", "1");
																String postBillingChargesReq2 = WSClient.createSOAPMessage("PostBillingCharges", "DS_03");
																String postBillingChargesRes2 = WSClient.processSOAPMessage(postBillingChargesReq2);

																if (WSAssert.assertIfElementExists(postBillingChargesRes2, "PostBillingChargesRS_Success", true)) {
																	WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Posted Billing Charges to Reservation</b>");
																	setOwsHeader();
																	WSClient.setData("{var_reservationID}", reservationID);
																	WSClient.setData("{var_resort}", resort);
																	WSClient.setData("{var_postDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
																	WSClient.setData("{var_userName}", uname);
																	WSClient.setData("{var_folioNo}", folioNumber);
																	WSClient.setData("{var_charge}", String.valueOf(postStayCharge));

																	String makePaymentReq2 = WSClient.createSOAPMessage("OWSMakePayment", "DS_01");
																	String makePaymentRes2 = WSClient.processSOAPMessage(makePaymentReq2);

																	if (WSAssert.assertIfElementExists(makePaymentRes2, "MakePaymentResponse_Result_resultStatusFlag", true)) {
																		if (WSAssert.assertIfElementValueEquals(makePaymentRes2, "MakePaymentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
																			if (WSAssert.assertIfElementValueEquals(makePaymentRes2, "MakePaymentResponse_ReservationID_UniqueID", reservationID, false)) {
																				WSClient.writeToReport(LogStatus.INFO, "<b>Retrieveing Folio to validate if payment is made to the reservation</b>");
																				WSClient.setData("{var_reservationID}", reservationID);
																				WSClient.setData("{var_resort}", resort);
																				String longInfo2 = WSClient.getElementValue(makePaymentReq2, "MakePaymentRequest_Posting_LongInfo", XMLType.REQUEST);
																				String postDate2 = WSClient.getElementValue(makePaymentReq2, "MakePaymentRequest_Posting_PostDate", XMLType.REQUEST);
																				String reference2 = WSClient.getElementValue(makePaymentReq2, "MakePaymentRequest_Reference", XMLType.REQUEST);

																				WSClient.writeToReport(LogStatus.INFO, "<b>Call FetchFolio Operation to see if the Payment Posting is correctly reflected on the folio</b>");
																				WSClient.setData("{var_folioWindowNumber}", folioNumber);
																				String fetchFolioReq2 = WSClient.createSOAPMessage("FetchFolio", "DS_01");
																				String fetchFolioRes2 = WSClient.processSOAPMessage(fetchFolioReq2);

																				if (WSAssert.assertIfElementExists(fetchFolioRes2, "FetchFolioRS_Success", true)) {
																					if (WSClient.getAttributeValueByAttribute(fetchFolioRes2, "ReservationInfo_ReservationIDList_UniqueID", "ID", "Reservation", XMLType.RESPONSE).equals(reservationID)) {

																						WSClient.writeToReport(LogStatus.INFO, "<b>Validate if the overall balance is correctly calculated after making the payment</b>");
																						WSAssert.assertIfElementValueEquals(fetchFolioRes2, "ReservationInfo_RoomStay_Balance_Amount", "0", false);

																						WSClient.writeToReport(LogStatus.INFO, "<b>Validate if Payment and Balance Information is correctly populated on the Folio</b>");

																						int folioNodeIndex1 = WSClient.getNodeIndex(fetchFolioRes2, "1", "ReservationFolioInformation_FolioWindows_FolioWindow", "ReservationFolioInformation_FolioWindows_FolioWindow_FolioWindowNo", XMLType.RESPONSE);
																						WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "FolioWindows_FolioWindow_Payment_Amount", "-" + postStayCharge, false);
																						WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "FolioWindows_FolioWindow_Balance_Amount", "0", false);
																						WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "FolioWindows_FolioWindow_PaymentMethod_FolioView", folioNumber, false);
																						WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "FolioWindows_FolioWindow_PaymentMethod_PaymentMethod", "CASH", false);

																						WSClient.writeToReport(LogStatus.INFO, "<b>Validating if Posting information (Transaction Type = Payment) is correct on the Folio</b>");
																						int postingPaymentNodeIndex = WSClient.getSecondLevelNodeIndex(fetchFolioRes, "Payment", "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", "Postings_Posting_TransactionType", XMLType.RESPONSE);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Folio_Postings_Posting_PostingDate", XMLType.RESPONSE, postDate2, false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Folio_Postings_Posting_TransactionDate", XMLType.RESPONSE, postDate2, false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Remark", XMLType.RESPONSE, longInfo2, false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Reference", XMLType.RESPONSE, reference2, false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_PostedAmount_Amount", XMLType.RESPONSE, String.valueOf(postStayCharge), false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Price_Amount", XMLType.RESPONSE, String.valueOf(postStayCharge), false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Quantity", XMLType.RESPONSE, "1", false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_TransactionType", XMLType.RESPONSE, "Payment", false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_CreditAmount_Amount", XMLType.RESPONSE, String.valueOf(postStayCharge), false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Posting_Exchange_CreditAmount_Amount", XMLType.RESPONSE, String.valueOf(postStayCharge), false);
																					} else {
																						WSClient.writeToReport(LogStatus.FAIL, "<b>Reservation " + reservationID + " is not found on the response </b>");
																					}
																				}
																			}
																		}
																	}

																} else {
																	WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the folio</b>");
																}

															}

														} else {
															WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag is found, but the ReservationID is missing on the response");
														}
													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag != SUCCESS");
													}
												} else {
													WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag is not found on the response");
												}
											} else {
												WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Failed to post chrges");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to apply final postings");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to checkin reservation");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to assign room");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to change the status of room to vacant and inspected");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> changing reservation failed");
						}
					}

					else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a reservation failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a profile failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisites of reservation creation failed ");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "MakePayment", "ResvAdvanced", "OWS", "P1" })
	public void makeCCPaymentPostStay() {
		String profileID = "", reservationID = "", resort = "";
		try {
			String testName = "makeCCPaymentPostStay";
			WSClient.startTest(testName, "Verify that the Credit Card Payment is successful for a checked-out reservation for which the Post-Stay charges flag is enabled", "minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			String uname = OPERALib.getUserName();
			String channel = OWSLib.getChannel();
			resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_chainCode}", chain);

			HashMap<String, String> resvMap = new HashMap<>();
			String roomNumber = "";

			// Check if ENV Setup is Complete
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

				/*************
				 * Prerequisite 1: Create profile
				 *********************************/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{var_cardHolderName}", fname + " " + lname);
					WSClient.setData("{var_paymentMethod}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_03"));
					// WSClient.setData("{var_cardNumber}", "340000000001239");
					// WSClient.setData("{var_expDate}", "2020-05-20");
					WSClient.setData("{var_cardNumber}", "34000000000" + WSClient.getKeywordData("{KEYWORD_RANDNUM_4}"));
					WSClient.setData("{var_expDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_600}"));
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");

					/**************
					 * Prerequisite 2:Create a Reservation
					 *********************************/

					resvMap = CreateReservation.createReservation("DS_12");
					if (!resvMap.get("reservationId").equals("error")) {
						reservationID = resvMap.get("reservationId");
						WSClient.setData("{var_resvId}", reservationID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + reservationID + "</b>");

						WSClient.setData("{var_GuaranteeCode}", OperaPropConfig.getDataSetForCode("ReservationType", "DS_04"));
						WSClient.setData("{var_paymentMethod}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_13");
						String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

						if (WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success", true)) {

							/*************
							 * Prerequisite 3: Fetching available Hotel rooms
							 * with room type
							 **********************************/

							String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
							String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

							if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
								roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
							} else {

								/*****************
								 * Prerequisite 4: Creating a room to assign
								 *********************************/

								String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
								String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

								if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
									roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
								}
							}

							WSClient.setData("{var_roomNumber}", roomNumber);
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched the room -> Room number : " + roomNumber + "</b>");

							/*******************
							 * Prerequisite 5: Changing the room status to
							 * inspected to assign the room for checking in
							 *************************************/

							String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
							String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

							if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true)) {
								/********************
								 * Prerequisite 6: Assign Room
								 ************************************/
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room</b>");
								String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
								String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

								if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {

									/********************
									 * Prerequisite 7: CheckIn Reservation
									 **************************************/

									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned the room</b>");
									String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
									String checkInRes = WSClient.processSOAPMessage(checkInReq);

									if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO, "<b>Successfully checked in the reservation</b>");
										WSClient.setData("{var_reservationID}", reservationID);
										String QS02 = WSClient.getQuery("OWSMakePayment", "QS_01");
										System.out.println(QS02);
										LinkedHashMap<String, String> financialTransactions1 = WSClient.getDBRow(QS02);
										System.out.println(financialTransactions1);

										WSClient.setData("{var_resort}", resort);
										WSClient.setData("{var_resvId}", reservationID);
										WSClient.setData("{var_cashierID}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
										WSClient.setData("{var_cashierId}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
										WSClient.setData("{var_trx}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_04"));

										/********************
										 * Prerequisite 8: Apply Final Postings
										 **************************************/

										String applyFinalPostingsReq = WSClient.createSOAPMessage("ApplyFinalPostings", "DS_01");
										String applyFinalPostingsRes = WSClient.processSOAPMessage(applyFinalPostingsReq);

										String QS01 = WSClient.getQuery("OWSMakePayment", "QS_01");
										System.out.println(QS01);
										LinkedHashMap<String, String> financialTransactionsAfterApplyingPostings = WSClient.getDBRow(QS01);
										System.out.println(financialTransactionsAfterApplyingPostings);
										String balanceBefore = "";

										if (financialTransactionsAfterApplyingPostings.get("BALANCE") == null)
											balanceBefore = "0";
										else
											balanceBefore = financialTransactionsAfterApplyingPostings.get("BALANCE");

										int finalBalance = (Integer.parseInt(balanceBefore) < 0) ? Integer.parseInt(balanceBefore) : 0;

										if (WSAssert.assertIfElementExists(applyFinalPostingsRes, "ApplyFinalPostingsRS_Success", true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>" + "Applied final postings" + "</b>");

											/*********************
											 * Prerequisite 9: Post Charges (To
											 * Pay the exact balance Amount)
											 ****************************************/

											String postBillingChargesReq = WSClient.createSOAPMessage("PostBillingCharges", "DS_02");
											String postBillingChargesRes = WSClient.processSOAPMessage(postBillingChargesReq);

											if (WSAssert.assertIfElementExists(postBillingChargesRes, "PostBillingChargesRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Posted Billing Charges to Reservation</b>");
												String folioNumber = WSClient.getElementValue(postBillingChargesReq, "Criteria_Charges_Charge_FolioWindowNo", XMLType.REQUEST);
												setOwsHeader();
												WSClient.setData("{var_reservationID}", reservationID);
												WSClient.setData("{var_resort}", resort);
												WSClient.setData("{var_postDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
												WSClient.setData("{var_userName}", uname);
												WSClient.setData("{var_folioNo}", folioNumber);

												LinkedHashMap<String, String> financialTransactionsAfterPayment = WSClient.getDBRow(QS01);
												System.out.println(financialTransactionsAfterPayment);
												String balance = "";
												if (financialTransactionsAfterPayment.get("BALANCE") == null)
													balance = "0";
												else
													balance = financialTransactionsAfterPayment.get("BALANCE");

												WSClient.setData("{var_charge}", balance);
												int totalAmmountPosted = Integer.parseInt(balance);
												String makePaymentReq = WSClient.createSOAPMessage("OWSMakePayment", "DS_02");
												String makePaymentRes = WSClient.processSOAPMessage(makePaymentReq);

												if (WSAssert.assertIfElementExists(makePaymentRes, "MakePaymentResponse_Result_resultStatusFlag", true)) {
													if (WSAssert.assertIfElementValueEquals(makePaymentRes, "MakePaymentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
														if (WSAssert.assertIfElementValueEquals(makePaymentRes, "MakePaymentResponse_ReservationID_UniqueID", reservationID, false)) {
															WSClient.writeToReport(LogStatus.INFO, "<b>Retrieveing Folio to validate if payment is made to the reservation</b>");
															WSClient.setData("{var_reservationID}", reservationID);
															WSClient.setData("{var_resort}", resort);
															String amountPaid = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_Charge", XMLType.REQUEST);
															int totalAmountPaid = Integer.parseInt(amountPaid);
															String longInfo = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_LongInfo", XMLType.REQUEST);
															String postDate = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Posting_PostDate", XMLType.REQUEST);
															String reference = WSClient.getElementValue(makePaymentReq, "MakePaymentRequest_Reference", XMLType.REQUEST);

															WSClient.writeToReport(LogStatus.INFO, "<b>Call FetchFolio Operation to see if the Payment Posting is correctly reflected on the folio</b>");
															WSClient.setData("{var_folioWindowNumber}", folioNumber);
															String fetchFolioReq = WSClient.createSOAPMessage("FetchFolio", "DS_01");
															String fetchFolioRes = WSClient.processSOAPMessage(fetchFolioReq);

															if (WSAssert.assertIfElementExists(fetchFolioRes, "FetchFolioRS_Success", true)) {
																if (WSClient.getAttributeValueByAttribute(fetchFolioRes, "ReservationInfo_ReservationIDList_UniqueID", "ID", "Reservation", XMLType.RESPONSE).equals(reservationID)) {

																	WSClient.writeToReport(LogStatus.INFO, "<b>Validate if the overall balance is correctly calculated after making the payment</b>");
																	WSAssert.assertIfElementValueEquals(fetchFolioRes, "ReservationInfo_RoomStay_Balance_Amount", new Integer(totalAmmountPosted - totalAmountPaid).toString(), false);

																	WSClient.writeToReport(LogStatus.INFO, "<b>Validate if Payment and Balance Information is correctly populated on the Folio</b>");
																	int totalPayment = totalAmountPaid - finalBalance;

																	int folioNodeIndex = WSClient.getNodeIndex(fetchFolioRes, "1", "ReservationFolioInformation_FolioWindows_FolioWindow", "ReservationFolioInformation_FolioWindows_FolioWindow_FolioWindowNo", XMLType.RESPONSE);
																	WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_Payment_Amount", "-" + totalPayment, false);
																	WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_Balance_Amount", new Integer(totalAmmountPosted - totalAmountPaid).toString(), false);
																	WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_PaymentMethod_FolioView", folioNumber, false);
																	WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "FolioWindows_FolioWindow_PaymentMethod_PaymentMethod", "CASH", false);

																	WSClient.writeToReport(LogStatus.INFO, "<b>Validating if Posting information (Transaction Type = Payment) is correct on the Folio</b>");
																	int postingPaymentNodeIndex = WSClient.getSecondLevelNodeIndex(fetchFolioRes, "Payment", "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", "Postings_Posting_TransactionType", XMLType.RESPONSE);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Folio_Postings_Posting_PostingDate", XMLType.RESPONSE, postDate, false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Folio_Postings_Posting_TransactionDate", XMLType.RESPONSE, postDate, false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Remark", XMLType.RESPONSE, longInfo, false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Reference", XMLType.RESPONSE, reference, false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_PostedAmount_Amount", XMLType.RESPONSE, amountPaid, false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Price_Amount", XMLType.RESPONSE, amountPaid, false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Quantity", XMLType.RESPONSE, "1", false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_TransactionType", XMLType.RESPONSE, "Payment", false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_CreditAmount_Amount", XMLType.RESPONSE, amountPaid, false);
																	WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex, "Folio_Postings_Posting", postingPaymentNodeIndex, "Posting_Exchange_CreditAmount_Amount", XMLType.RESPONSE, amountPaid, false);
																} else {
																	WSClient.writeToReport(LogStatus.FAIL, "<b>Reservation " + reservationID + " is not found on the response </b>");
																}

																WSClient.setData("{var_resort}", OPERALib.getResort());
																WSClient.setData("{var_cashierId}", OperaPropConfig.getDataSetForCode("Cashiers", "DS_01"));
																WSClient.setData("{var_resvId}", reservationID);
																WSClient.setData("{var_profileId}", profileID);
																boolean generateFolio = GenerateFolio.generateFolio("DS_01");
																if (generateFolio) {
																	CheckoutReservation.checkOut("DS_05");
																}

																WSClient.setData("{var_amount}", "20");
																WSClient.setData("{var_quantity}", "1");
																String postBillingChargesReq2 = WSClient.createSOAPMessage("PostBillingCharges", "DS_03");
																String postBillingChargesRes2 = WSClient.processSOAPMessage(postBillingChargesReq2);

																if (WSAssert.assertIfElementExists(postBillingChargesRes2, "PostBillingChargesRS_Success", true)) {
																	WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Posted Billing Charges to Reservation</b>");
																	setOwsHeader();
																	WSClient.setData("{var_reservationID}", reservationID);
																	WSClient.setData("{var_resort}", resort);
																	WSClient.setData("{var_postDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
																	WSClient.setData("{var_userName}", uname);
																	WSClient.setData("{var_folioNo}", folioNumber);
																	WSClient.setData("{var_charge}", "20");

																	String makePaymentReq2 = WSClient.createSOAPMessage("OWSMakePayment", "DS_02");
																	String makePaymentRes2 = WSClient.processSOAPMessage(makePaymentReq2);

																	if (WSAssert.assertIfElementExists(makePaymentRes2, "MakePaymentResponse_Result_resultStatusFlag", true)) {
																		if (WSAssert.assertIfElementValueEquals(makePaymentRes2, "MakePaymentResponse_Result_resultStatusFlag", "SUCCESS", false)) {
																			if (WSAssert.assertIfElementValueEquals(makePaymentRes2, "MakePaymentResponse_ReservationID_UniqueID", reservationID, false)) {
																				WSClient.writeToReport(LogStatus.INFO, "<b>Retrieveing Folio to validate if payment is made to the reservation</b>");
																				WSClient.setData("{var_reservationID}", reservationID);
																				WSClient.setData("{var_resort}", resort);
																				String amountPaid2 = WSClient.getElementValue(makePaymentReq2, "MakePaymentRequest_Posting_Charge", XMLType.REQUEST);
																				int totalAmountPaid2 = Integer.parseInt(amountPaid2);
																				String longInfo2 = WSClient.getElementValue(makePaymentReq2, "MakePaymentRequest_Posting_LongInfo", XMLType.REQUEST);
																				String postDate2 = WSClient.getElementValue(makePaymentReq2, "MakePaymentRequest_Posting_PostDate", XMLType.REQUEST);
																				String reference2 = WSClient.getElementValue(makePaymentReq2, "MakePaymentRequest_Reference", XMLType.REQUEST);

																				WSClient.writeToReport(LogStatus.INFO, "<b>Call FetchFolio Operation to see if the Payment Posting is correctly reflected on the folio</b>");
																				WSClient.setData("{var_folioWindowNumber}", folioNumber);
																				String fetchFolioReq2 = WSClient.createSOAPMessage("FetchFolio", "DS_01");
																				String fetchFolioRes2 = WSClient.processSOAPMessage(fetchFolioReq2);

																				if (WSAssert.assertIfElementExists(fetchFolioRes2, "FetchFolioRS_Success", true)) {
																					if (WSClient.getAttributeValueByAttribute(fetchFolioRes2, "ReservationInfo_ReservationIDList_UniqueID", "ID", "Reservation", XMLType.RESPONSE).equals(reservationID)) {

																						WSClient.writeToReport(LogStatus.INFO, "<b>Validate if the overall balance is correctly calculated after making the payment</b>");
																						WSAssert.assertIfElementValueEquals(fetchFolioRes2, "ReservationInfo_RoomStay_Balance_Amount", "0", false);

																						WSClient.writeToReport(LogStatus.INFO, "<b>Validate if Payment and Balance Information is correctly populated on the Folio</b>");
																						int totalPayment2 = totalAmountPaid2;

																						int folioNodeIndex1 = WSClient.getNodeIndex(fetchFolioRes2, "1", "ReservationFolioInformation_FolioWindows_FolioWindow", "ReservationFolioInformation_FolioWindows_FolioWindow_FolioWindowNo", XMLType.RESPONSE);
																						WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "FolioWindows_FolioWindow_Payment_Amount", "-" + totalPayment2, false);
																						WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "FolioWindows_FolioWindow_Balance_Amount", "0", false);
																						WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "FolioWindows_FolioWindow_PaymentMethod_FolioView", folioNumber, false);
																						WSAssert.assertIfElementValueEqualsByIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "FolioWindows_FolioWindow_PaymentMethod_PaymentMethod", "CASH", false);

																						WSClient.writeToReport(LogStatus.INFO, "<b>Validating if Posting information (Transaction Type = Payment) is correct on the Folio</b>");
																						int postingPaymentNodeIndex = WSClient.getSecondLevelNodeIndex(fetchFolioRes, "Payment", "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", "Postings_Posting_TransactionType", XMLType.RESPONSE);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Folio_Postings_Posting_PostingDate", XMLType.RESPONSE, postDate2, false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Folio_Postings_Posting_TransactionDate", XMLType.RESPONSE, postDate2, false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Remark", XMLType.RESPONSE, longInfo2, false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Reference", XMLType.RESPONSE, reference2, false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_PostedAmount_Amount", XMLType.RESPONSE, amountPaid2, false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Price_Amount", XMLType.RESPONSE, amountPaid2, false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_Quantity", XMLType.RESPONSE, "1", false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_TransactionType", XMLType.RESPONSE, "Payment", false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Postings_Posting_CreditAmount_Amount", XMLType.RESPONSE, amountPaid2, false);
																						WSAssert.assertIfElementValueEqualsByTwoLevelIndex(fetchFolioRes2, "ReservationFolioInformation_FolioWindows_FolioWindow", folioNodeIndex1, "Folio_Postings_Posting", postingPaymentNodeIndex, "Posting_Exchange_CreditAmount_Amount", XMLType.RESPONSE, amountPaid2, false);
																					} else {
																						WSClient.writeToReport(LogStatus.FAIL, "<b>Reservation " + reservationID + " is not found on the response </b>");
																					}
																				}
																			}
																		}
																	}

																} else {
																	WSClient.writeToReport(LogStatus.FAIL, "<b>Failed to fetch the folio</b>");
																}
															}

														} else {
															WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag is found, but the ReservationID is missing on the response");
														}
													} else {
														WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag != SUCCESS");
													}
												} else {
													WSClient.writeToReport(LogStatus.FAIL, "Make Payment Operation is failed. ResultStatusFlag is not found on the response");
												}
											} else {
												WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Failed to post chrges");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to apply final postings");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to checkin reservation");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to assign room");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>  Unable to change the status of room to vacant and inspected");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> changing reservation failed");
						}
					}

					else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a reservation failed");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Creating a profile failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisites of reservation creation failed ");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}
	
}
