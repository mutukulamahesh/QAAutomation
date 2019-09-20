package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2008b.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchReservationExt extends WSSetUp {

	@Test(groups = { "minimumRegression", "FetchReservationExt", "HTNG2008BExt", "HTNG" })
	public void fetchReservationExt_Ext_4538() {

		String[] preReqs = { "RateCode", "RoomType", "MarketCode", "SourceCode" };
		try {

			String testName = "fetchReservationExt_Ext_4538";
			WSClient.startTest(testName, "Verify correct reservationStatus,ReservationID,ConfiramtionNO,ProfileID & ResortID " + "should be displayed in the FetchReservationExt " + "response when passing ReservationID in the FetchReservationExtRequest.", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(preReqs)) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				OPERALib.setOperaHeader(uname);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				/*******************************
				 * Pre Requisite : 1 create Profile
				 *************/
				String profileId = CreateProfile.createProfile("DS_01");

				if (!profileId.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId + "</b>");
					/*************
					 * Prerequisite 2: Fetch Room Type,Rate details,Market Code
					 * and Source Codes
					 ******************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					/******************
					 * Prerequisite 3:Create a Reservation
					 ************************/
					String resvId = CreateReservation.createReservation("DS_01").get("reservationId");
					WSClient.setData("{var_resvId}", resvId);

					if (!resvId.equals("error")) {

						/******************
						 * Fetching Reservation
						 ************************/
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

						String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationExtResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_01");
								HashMap<String, String> dataMap = WSClient.getDBRow(query);

								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ProfileID", dataMap.get("NAME_ID"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ResortId", resortExtValue, false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ReservationID", dataMap.get("RESV_NAME_ID"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ConfirmationNO", dataMap.get("CONFIRMATION_NO"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_reservationStatus", dataMap.get("RESV_STATUS"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ReservationID_source", "OPERA", false);
							}

							// text Element
							if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>Text :  :" + message + "</b>");
							}
						}
					}
				}
			}

		} catch (

		Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the HTNG Fetch Reservation Ext is working i.e.,
	 * fetching reservation details such as Arrival date and departure date for
	 * a given reservation.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */

	@Test(groups = { "minimumRegression", "FetchReservationEXT", "HTNG2008BEXT", "HTNG" })
	public void fetchReservationExt_Ext_1965() {

		String[] preReqs = { "RateCode", "RoomType", "MarketCode", "SourceCode" };
		try {
			String testName = "fetchReservationExt_Ext_1965";
			WSClient.startTest(testName, "Verify that the fetching reservation with correct arrival and departure date is successful", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(preReqs)) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				String uname = OPERALib.getUserName();
				OPERALib.setOperaHeader(uname);
				String profileId = CreateProfile.createProfile("DS_01");
				if (!profileId.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId + "</b>");
					WSClient.setData("{var_profileId}", profileId);
					/*******
					 * Checking if pre-requisites required are created or not
					 **********/
					if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						/******************
						 * Prerequisite 2:Create a Reservation
						 ************************/
						String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");
						WSClient.setData("{var_resvId}", reservationId);
						System.out.println(reservationId);
						if (!reservationId.equals("error")) {
							HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
							/*************
							 * Fetching Reservation
							 ********************/
							String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
							String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
							if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationExtResponse_Result", false)) {
								if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									/***********
									 * Validating the dates against the database
									 ************/
									String query = WSClient.getQuery("QS_02");
									HashMap<String, String> DBList = WSClient.getDBRow(query);
									System.out.println(DBList);
									WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ResortId", HTNGLib.getExtResort(resortOperaValue, interfaceName), false);
									WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ReservationID", DBList.get("RESV_NAME_ID"), false);
									String DBArrival = DBList.get("BEGIN_DATE");
									DBArrival = DBArrival.replace(' ', 'T');
									DBArrival = DBArrival.substring(0, DBArrival.indexOf('.'));
									WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ArrivalDate", DBArrival, false);
									String DBDeparture = DBList.get("END_DATE");
									DBDeparture = DBDeparture.replace(' ', 'T');
									DBDeparture = DBDeparture.substring(0, DBDeparture.indexOf('.'));
									WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_DepartureDate", DBDeparture, false);
								}
								// text Element
								if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>Text :  :" + message + "</b>");
								}
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Prerequisite required for Reservation are not created");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchReservationExt", "HTNG2008BExt", "HTNG" })
	public void fetchReservationExt_Ext_1966() {

		String[] preReqs = { "RateCode", "RoomType", "MarketCode", "SourceCode" };
		try {
			String testName = "fetchReservationExt_Ext_1966";
			WSClient.startTest(testName, "Verify correct rate code, room type, room number " + "should be displayed in the FetchReservationExt response " + "when passing ReservationID & ResortID in the FetchReservationExtRequest.", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(preReqs)) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					String uname = OPERALib.getUserName();
					OPERALib.setOperaHeader(uname);
					String profileId = CreateProfile.createProfile("DS_01");
					if (!profileId.equals("error")) {
						System.out.println(profileId);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId + "</b>");
						WSClient.setData("{var_profileId}", profileId);

						/*******
						 * Checking if pre-requisites required are created or
						 * not
						 **********/
						if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
							WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

							/******************
							 * Prerequisite 2:Create a Reservation
							 ************************/
							String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");
							WSClient.setData("{var_resvId}", reservationId);
							System.out.println(reservationId);
							if (!reservationId.equals("error")) {
								HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

								/*******************
								 * Prerequisite 6:Assign a Room
								 **************************/
								String createRoomReq = WSClient.createSOAPMessage("AutoAssignRoom", "DS_01");
								String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

								if (WSAssert.assertIfElementExists(createRoomRes, "AutoAssignRoomRS_AutoRoomAssignRsList_RoomNumber", true)) {

									String roomNo = WSClient.getElementValue(createRoomRes, "AutoAssignRoomRS_AutoRoomAssignRsList_RoomNumber", XMLType.RESPONSE);
									WSClient.setData("{var_roomNo}", roomNo);
									WSClient.setData("{var_roomNumber}", roomNo);
									System.out.println("\n\n\n\n" + roomNo + "\n\n\n\n");
									if (reservationId != null && reservationId != "" && roomNo != null && roomNo != "") {
										/******************
										 * Fetching Reservation
										 ************************/
										String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
										String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
										if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationExtResponse_Result", false)) {
											if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {

												HashMap<String, String> dataMap4 = WSClient.getDBRow(WSClient.getQuery("QS_01"));
												System.out.println("******************************************************\n\n\n\n");
												System.out.println(dataMap4);
												System.out.println("\n\n\n\n******************************************************");
												WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ProfileID", dataMap4.get("NAME_ID"), false);
												WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ResortId", resortExtValue, false);
												WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ReservationID", dataMap4.get("RESV_NAME_ID"), false);
												WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ConfirmationNO", dataMap4.get("CONFIRMATION_NO"), false);
												WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_reservationStatus", dataMap4.get("RESV_STATUS"), false);
												HashMap<String, String> dataMap5 = WSClient.getDBRow(WSClient.getQuery("QS_03"));
												WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ShortRateCode", dataMap5.get("RATE_CODE"), false);
												WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_RoomNumber", dataMap5.get("ROOM"), false);
												HashMap<String, String> dataMap6 = WSClient.getDBRow(WSClient.getQuery("QS_04"));
												WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ShortRoomType", dataMap6.get("LABEL"), false);
											}

											// text Element
											if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
												String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "<b>Text :  :" + message + "</b>");
											}
										}
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Room Not assigned");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Prerequisite required for Reservation are not created");
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

				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchReservationExt", "HTNG2008BExt", "HTNG" })
	public void fetchReservationExt_Ext_1967() {

		String[] preReqs = { "RateCode", "RoomType", "MarketCode", "SourceCode", "PackageCode", "PackageGroup" };
		try {
			String testName = "fetchReservationExt_Ext_1967";
			WSClient.startTest(testName, "Verify correct package element code & package element description " + "should be displayed in the FetchReservationExt response" + " when passing ReservationID & ResortID in the FetchReservationExtRequest.", "minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			if (OperaPropConfig.getPropertyConfigResults(preReqs)) {

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				String uname = OPERALib.getUserName();
				OPERALib.setOperaHeader(uname);
				String profileId = CreateProfile.createProfile("DS_01");
				if (!profileId.equals("error")) {
					System.out.println(profileId);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId + "</b>");
					WSClient.setData("{var_profileId}", profileId);

					/*******
					 * Checking if pre-requisites required are created or not
					 **********/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					/******************
					 * Prerequisite 2:Create a Reservation
					 ************************/
					String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");
					WSClient.setData("{var_resvId}", reservationId);
					System.out.println(reservationId);
					if (!reservationId.equals("error")) {
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
						/******************
						 * Fetching Reservation
						 ************************/
						String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);

						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationExtResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								HashMap<String, String> dataMap = WSClient.getDBRow(WSClient.getQuery("QS_01"));
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ProfileID", dataMap.get("NAME_ID"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ResortId", resortExtValue, false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ReservationID", dataMap.get("RESV_NAME_ID"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ConfirmationNO", dataMap.get("CONFIRMATION_NO"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_reservationStatus", dataMap.get("RESV_STATUS"), false);
								String packageId = WSClient.getElementValue(fetchResvRes, "PackageElements_PackageElement_ElementCode", XMLType.RESPONSE);
								WSClient.setData("{var_pkgId}", packageId);
								HashMap<String, String> dataMap2 = WSClient.getDBRow(WSClient.getQuery("QS_05"));
								WSAssert.assertIfElementValueEquals(fetchResvRes, "PackageElements_PackageElement_ElementCode", dataMap2.get("PRODUCT"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "PackageElements_PackageElement_ElementDescription", dataMap2.get("DESCRIPTION"), false);
							}

						}
						// text Element
						if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
							String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>Text :  :" + message + "</b>");
						}

					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Prerequisite required for Reservation are not created");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchReservationExt", "HTNG2008BExt", "HTNG" })
	public void fetchReservationExt_Ext_1969() {

		String[] preReqs = { "RateCode", "RoomType", "MarketCode", "SourceCode" };
		try {

			String testName = "fetchReservationExt_Ext_1969";
			WSClient.startTest(testName, "Verify correct Market segment, Source Code & No Post Flag " + "should be displayed in the FetchReservationExt response " + "when passing ReservationID & ResortID in the FetchReservationExtReques", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(preReqs)) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				String uname = OPERALib.getUserName();
				OPERALib.setOperaHeader(uname);
				String profileId = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId + "</b>");
				WSClient.setData("{var_profileId}", profileId);

				/*******
				 * Checking if pre-requisites required are created or not
				 **********/
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					/******************
					 * Prerequisite 2:Create a Reservation
					 ************************/
					String createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_01");
					String createResvRes = WSClient.processSOAPMessage(createResvReq);
					String mktCode = WSClient.getElementValue(createResvReq, "RoomStay_RoomRates_RoomRate_MarketCode", XMLType.REQUEST);
					String srcCode = WSClient.getElementValue(createResvReq, "RoomStay_RoomRates_RoomRate_SourceOfBusiness", XMLType.REQUEST);
					if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success", true)) {
						String reservationId = WSClient.getElementValue(createResvRes, "Reservation_ReservationIDList_UniqueID_ID", XMLType.RESPONSE);
						WSClient.setData("{var_resvId}", reservationId);
						System.out.println(reservationId);
						if (reservationId != null && reservationId != "") {
							HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

							/******************
							 * Fetching Reservation
							 ************************/
							String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
							String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
							if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationExtResponse_Result", false)) {
								if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									HashMap<String, String> dataMap = WSClient.getDBRow(WSClient.getQuery("QS_01"));
									WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ProfileID", dataMap.get("NAME_ID"), false);
									WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ResortId", resortExtValue, false);
									WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ReservationID", dataMap.get("RESV_NAME_ID"), false);
									WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ConfirmationNO", dataMap.get("CONFIRMATION_NO"), false);
									WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_reservationStatus", dataMap.get("RESV_STATUS"), false);
									WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_MarketSegment", mktCode, false);
									WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_SourceCode", srcCode, false);

									HashMap<String, String> dataMap3 = WSClient.getDBRow(WSClient.getQuery("QS_06"));
									String no_post_db = dataMap3.get("POSTING_ALLOWED_YN");
									System.out.println("************\n\n\n\n" + no_post_db + "\n\n\n\n*********");
									String no_post_equiv = "";
									if (no_post_db.equals("N"))
										no_post_equiv = "true";
									else if (no_post_db.equals("Y"))
										no_post_equiv = "false";
									WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_NoPostFlag", no_post_equiv, false);
								}
								// text Element
								if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>Text :  :" + message + "</b>");
								}
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation ID is not created");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Prerequisite required for Reservation are not created");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchReservationExt", "HTNG2008BExt", "HTNG" })
	public void fetchReservationExt_Ext_1975() {

		String[] preReqs = { "RateCode", "RoomType", "MarketCode", "SourceCode" };
		try {

			String testName = "fetchReservationExt_Ext_1975";
			WSClient.startTest(testName, "Verify correct Transportation detials should be displayed in FetchReservationExt response when passing ReservationID & ResortID in the FetchReservationExtRequest", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(preReqs)) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				String uname = OPERALib.getUserName();
				OPERALib.setOperaHeader(uname);
				/******************* Create Profile ***************************/
				String profileId = CreateProfile.createProfile("DS_01");
				if (!profileId.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId + "</b>");
					/**************************
					 * Add reservation to the profile
					 *****************/
					System.out.println(OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{var_transportCode}", "TransCode");
					WSClient.setData("{var_transportType}", "TransType");

					String resvId = CreateReservation.createReservation("DS_15").get("reservationId");

					if (!resvId.equals("error")) {

						WSClient.setData("{var_resvId}", resvId);
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
						String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);

						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationExtResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								HashMap<String, String> dataMap = WSClient.getDBRow(WSClient.getQuery("QS_07"));

								// validating the Response.

								WSAssert.assertIfElementValueEquals(fetchResvRes, "Transportation_Transportation_TranspoftType", dataMap.get("ARRIVAL_TRANSPORT_TYPE"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "Transportation_Transportation_TransportCode", dataMap.get("ARRIVAL_TRANSPORT_CODE"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "Transportation_Transportation_Carrier", dataMap.get("ARRIVAL_CARRIER_CODE"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "Transportation_Transportation_Location", dataMap.get("ARRIVAL_STATION_CODE"), false);
								/****************************
								 * departure validation
								 **********/
								WSAssert.assertIfElementValueEquals(fetchResvRes, "Transportation_Transportation2_TranspoftType", dataMap.get("DEPARTURE_TRANSPORT_TYPE"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "Transportation_Transportation2_TransportCode", dataMap.get("DEPARTURE_TRANSPORT_CODE"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "Transportation_Transportation2_Carrier", dataMap.get("DEPARTURE_CARRIER_CODE"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "Transportation_Transportation2_Location", dataMap.get("DEPARTURE_STATION_CODE"), false);
							}
							// text Element
							if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>Text :  :" + message + "</b>");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

	}

	// //accompany guest surya
	@Test(groups = { "targetedRegression", "FetchReservationExt", "HTNG2008BExt", "HTNG" })
	public void fetchReservationExt_Ext_1976() {

		String[] preReqs = { "RateCode", "RoomType", "MarketCode", "SourceCode" };
		String resvId = "";
		try {
			String testName = "fetchReservationExt_Ext_1976";
			WSClient.startTest(testName, "Verify correct AccompanyGuest first name, last name & name id should be displayed in the FetchReservationExt response when passing ReservationID & ResortID in the FetchReservationExtRequest.", "targetedRegression");
			if (OperaPropConfig.getPropertyConfigResults(preReqs)) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				OPERALib.setOperaHeader(uname);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				/****************************
				 * Create Accompany Guest Profile
				 ******************************/
				String lname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String fname = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_firstName}", fname);
				WSClient.setData("{var_lastName}", lname);
				String profileId1 = CreateProfile.createProfile("DS_06");
				String lname1 = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String fname1 = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_lname}", lname1);
				WSClient.setData("{var_fname}", fname1);
				String profileId2 = CreateProfile.createProfile("DS_06");
				WSClient.setData("{var_profileId}", profileId2);
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{var_accompanyProfileId}", profileId1);

				/****************************
				 * Attach reservation to profile 1
				 ******************************/
				HashMap<String, String> resvIds = new HashMap<>();
				if (profileId2 != "" && profileId1 != "")
					resvIds = CreateReservation.createReservation("DS_01");
				resvId = resvIds.get("reservationId");
				System.out.println("resvId :" + resvId);
				WSClient.setData("{var_resvId}", resvId);
				WSClient.setData("{var_reservation_id}", resvId);

				WSClient.setData("{var_profileId1}", profileId1);
				WSClient.setData("{var_profileId2}", profileId2);
				// ********************************* Add Accompany Guest to the
				// Above Reservation ***********************//
				String changeResvReq = WSClient.createSOAPMessage("ChangeReservation", "DS_01");
				String changeResvRes = WSClient.processSOAPMessage(changeResvReq);

				WSClient.setData("{var_resvId}", resvId);
				if (resvId != "") {
					// fetchResv
					HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
					String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
					String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);

					// Validate the response
					if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationExtResponse_Result", false)) {
						if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							LinkedHashMap<String, String> db = new LinkedHashMap<>();
							HashMap<String, String> xpath = new HashMap<>();
							LinkedHashMap<String, String> actualValues = new LinkedHashMap<>();
							WSClient.writeToReport(LogStatus.INFO, "Query to fetch reservation Lookup details from DataBase");
							String query = WSClient.getQuery("QS_08");
							db = WSClient.getDBRow(query);
							xpath.put("AccompanyGuests_AccompanyGuest_NameID", "ReservationData_AccompanyGuests_AccompanyGuest");
							xpath.put("AccompanyGuests_AccompanyGuest_FirstName", "ReservationData_AccompanyGuests_AccompanyGuest");
							xpath.put("AccompanyGuests_AccompanyGuest_LastName", "ReservationData_AccompanyGuests_AccompanyGuest");
							actualValues = WSClient.getSingleNodeList(fetchResvRes, xpath, false, XMLType.RESPONSE);
							WSAssert.assertEquals(db, actualValues, false);

						}
						// text Element
						if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
							String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>Text :  :" + message + "</b>");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			if (resvId != "")
				try {
					CancelReservation.cancelReservation("DS_01");
				} catch (Exception e) {

					e.printStackTrace();
				}
		}
	}

	@Test(groups = { "minimumRegression", "FetchReservationExt", "HTNG2008BExt", "HTNG" })
	public void fetchReservationExt_Ext_1970() {

		try {

			String testName = "fetchReservationExt_Ext_1970";
			String[] preReqs = { "PreferenceCode", "PreferenceGroup", "RateCode", "RoomType", "MarketCode", "SourceCode" };
			WSClient.startTest(testName, "Verify correct Preference type & preference value " + "should be displayed in FetchReservationExt response" + " when passing ReservationID & ResortID in the FetchReservationExtRequest", "minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			WSClient.setData("{var_interface}", interfaceName);
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(preReqs)) {
				/******************* Create Profile ***************************/
				String profileId = CreateProfile.createProfile("DS_01");
				if (!profileId.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId + "</b>");
					/**************************
					 * Add reservation to the profile
					 *****************/
					WSClient.setData("{var_prefCode}", OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_05"));
					WSClient.setData("{var_prefGroup}", OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_05"));
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					String resvId = CreateReservation.createReservation("DS_03").get("reservationId");

					if (!resvId.equals("error")) {

						WSClient.setData("{var_resvId}", resvId);

						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

						String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);

						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationExtResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();

								HashMap<String, String> xpath = new HashMap<String, String>();
								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();

								WSClient.writeToReport(LogStatus.INFO, "Query to fetch reservation Lookup details from DataBase");
								String query = WSClient.getQuery("QS_09");
								expectedValues = WSClient.getDBRow(query);

								xpath.put("ReservationPreferences_Preference_Type", "ReservationData_ReservationPreferences_Preference");
								xpath.put("ReservationPreferences_Preference_Value", "ReservationData_ReservationPreferences_Preference");
								actualValues = WSClient.getSingleNodeList(fetchResvRes, xpath, false, XMLType.RESPONSE);
								WSAssert.assertEquals(expectedValues, actualValues, false);

								System.out.println("expe" + expectedValues);
								System.out.println("ac" + actualValues);
							}

							// text Element
							if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>Text :  :" + message + "</b>");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "fullRegression", "FetchReservationExt", "HTNG2008BExt", "HTNG" })
	public void fetchReservationExt_Ext_1986() {

		String[] preReqs = { "RateCode", "RoomType", "MarketCode", "SourceCode" };
		try {

			String testName = "fetchReservationExt_Ext_1986";
			WSClient.startTest(testName, "Verify error message " + "should be displayed in the FetchReservationExt response " + "when passing invalid ReservationID & Valid ResortID in the FetchReservationExtRequest.", "fullRegression");

			if (OperaPropConfig.getPropertyConfigResults(preReqs)) {

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				OPERALib.setOperaHeader(uname);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				/*******************************
				 * Pre Requisite : 1 create Profile
				 *************/
				String profileId = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");

				if (!profileId.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId + "</b>");
					/*************
					 * Prerequisite 2: Fetch Room Type,Rate details,Market Code
					 * and Source Codes
					 ******************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					/******************
					 * Prerequisite 3: Create Invalid Reservation ID.
					 ************************/
					String resvId = WSClient.getKeywordData("{KEYWORD_RANDNUM_14}");
					WSClient.writeToReport(LogStatus.INFO, "<b>Invalid Reservation ID : " + resvId + "</b>");
					WSClient.setData("{var_resvId}", resvId);

					if (!resvId.equals("error")) {

						/******************
						 * Fetching Reservation
						 ************************/
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

						String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationExtResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_Result_resultStatusFlag", "FAIL", false)) {

								List<LinkedHashMap<String, String>> errors = new ArrayList<LinkedHashMap<String, String>>();
								LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
								xPath.put("Result_Text_TextElement", "FetchReservationExtResponse_Result_Text");
								errors = WSClient.getMultipleNodeList(fetchResvRes, xPath, false, XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, errors.toString());
							
								//WSClient.writeToReport(LogStatus.INFO, errors.toString());
								String text2 = "";
								String text = errors.get(0).get("TextElement1");
								if(errors.get(0).containsKey("TextElement2"))
								text2 = errors.get(0).get("TextElement2");
								if(text2.contains("BOOKING NOT FOUND") || text.contains("Reservation Id was not found")) WSClient.writeToReport(LogStatus.PASS, "Test Passed");  
								else WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
								WSClient.writeToReport(LogStatus.INFO, "<b>Text :  :" + text + "</b>");
							}
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "fullRegression", "FetchReservationExt", "HTNG2008B", "HTNG" })
	public void fetchReservationExt_Ext_1987() {

		String[] preReqs = { "RateCode", "RoomType", "MarketCode", "SourceCode" };
		try {
			String testName = "fetchReservationExt_Ext_1987";
			WSClient.startTest(testName, "Verify error message " + "should be displayed in the FetchReservationExt response " + "when missing reservationID and passing ResortID alone in the FetchReservationExtRequest", "fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(preReqs)) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				/*******************************/
				WSClient.setData("{var_resvId}", "");
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_02");
				String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);

				if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", false)) {
					if (WSAssert.assertIfElementContains(fetchResvRes, "Result_Text_TextElement", "No reservation ID specified", true)) {
						WSAssert.assertIfElementContains(fetchResvRes, "Result_Text_TextElement", "No reservation ID specified", false);
					} else if (WSAssert.assertIfElementContains(fetchResvRes, "Result_Text_TextElement", "Reservation information is missing", false)) {

					}
				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}


	@Test(groups = { "fullRegression", "FetchReservationExt", "HTNG2008BExt", "HTNG" })
	public void fetchReservationExt_Ext_1972() {

		try {
			String testName = "fetchReservationExt_Ext_1972";
			WSClient.startTest(testName, "Verify correct User defined type & User defined value " + "should be displayed in FetchReservationExt response " + "when passing ReservationID & ResortID in the FetchReservationExtRequest", "fullRegression");
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			/*******************************
			 * Pre Requisite : 1 create Profile
			 *************/
			String profileId = CreateProfile.createProfile("DS_01");

			if (!profileId.equals("error")) {

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId + "</b>");
				/*************
				 * Prerequisite 2: Fetch Room Type,Rate details,Market Code and
				 * Source Codes
				 ******************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

				/******************
				 * Prerequisite 3:Create a Reservation
				 ************************/
				String resvId = CreateReservation.createReservation("DS_01").get("reservationId");
				WSClient.setData("{var_resvId}", resvId);

				if (!resvId.equals("error")) {

					WSClient.setData("{var_charName}", OperaPropConfig.getDataSetForCode("UDFName", "DS_03"));
					WSClient.setData("{var_charValue}", "India");

					String changeResvReq = WSClient.createSOAPMessage("ChangeReservation", "DS_09");
					String changeResvRes = WSClient.processSOAPMessage(changeResvReq);

					if (WSAssert.assertIfElementExists(changeResvRes, "ChangeReservationRS_Success", false)) {
						/******************
						 * Fetching Reservation
						 ************************/
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

						String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);

						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationExtResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								String query = WSClient.getQuery("QS_10");
								HashMap<String, String> dataMap = WSClient.getDBRow(query);

								WSAssert.assertIfElementValueEquals(fetchResvRes, "UserDefinedValues_UserDefinedValue_CharacterValue", dataMap.get(OperaPropConfig.getDataSetForCode("UDFName", "DS_03")), false);

								WSAssert.assertIfElementValueEquals(fetchResvRes, "ReservationData_UserDefinedValues_UserDefinedValue_valueName", OperaPropConfig.getDataSetForCode("UDFLabel_R", "DS_03"), false);
							}
						}
					}
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

	@Test(groups = { "fullRegression", "FetchReservationExt", "HTNG2008BExt", "HTNG" })
	public void fetchReservationExt_Ext_1973() {

		try {
			String testName = "fetchReservationExt_Ext_1973";
			WSClient.startTest(testName, "Verify correct multiple user defined types & User defined values " + "should be displayed in FetchReservationExt response " + "when passing ReservationID & ResortID in the FetchReservationExtRequest", "fullRegression");
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			/*******************************
			 * Pre Requisite : 1 create Profile
			 *************/
			String profileId = CreateProfile.createProfile("DS_01");

			if (!profileId.equals("error")) {

				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId + "</b>");
				/*************
				 * Prerequisite 2: Fetch Room Type,Rate details,Market Code and
				 * Source Codes
				 ******************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

				/******************
				 * Prerequisite 3:Create a Reservation
				 ************************/
				String resvId = CreateReservation.createReservation("DS_01").get("reservationId");
				WSClient.setData("{var_resvId}", resvId);

				if (!resvId.equals("error")) {

					WSClient.setData("{var_charName}", OperaPropConfig.getDataSetForCode("UDFName", "DS_03"));
					WSClient.setData("{var_charValue}", "India");
					WSClient.setData("{var_numericName}", OperaPropConfig.getDataSetForCode("UDFName", "DS_01"));
					WSClient.setData("{var_numericValue}", "26");
					WSClient.setData("{var_dateName}", OperaPropConfig.getDataSetForCode("UDFName", "DS_05"));
					WSClient.setData("{var_dateValue}", WSClient.getData("{KEYWORD_BUSINESSDATE_MINUS_562}"));

					/******************
					 * Adding Reservation Udfs
					 ************************/

					String changeResvReq = WSClient.createSOAPMessage("ChangeReservation", "DS_10");
					String changeResvRes = WSClient.processSOAPMessage(changeResvReq);

					if (WSAssert.assertIfElementExists(changeResvRes, "ChangeReservationRS_Success", false)) {
						/******************
						 * Fetching Reservation
						 ************************/
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

						String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);

						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationExtResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								String query = WSClient.getQuery("QS_11");
								HashMap<String, String> dataMap = WSClient.getDBRow(query);

								WSAssert.assertIfElementValueEquals(fetchResvRes, "UserDefinedValues_UserDefinedValue_CharacterValue", dataMap.get( OperaPropConfig.getDataSetForCode("UDFName", "DS_03")), false);

								WSAssert.assertIfElementValueEquals(fetchResvRes, "UserDefinedValues_UserDefinedValue_NumericValue", dataMap.get(OperaPropConfig.getDataSetForCode("UDFName", "DS_01")), false);

								String dateValue = WSClient.getElementValue(fetchResvRes, "UserDefinedValues_UserDefinedValue_DateValue", XMLType.RESPONSE);
								dateValue = dateValue.substring(0, 10);
								WSAssert.assertEquals(dataMap.get(OperaPropConfig.getDataSetForCode("UDFName", "DS_05")).substring(0, 10), dateValue, false);

								HashMap<String, String> xpath = new HashMap<String, String>();
								xpath.put("ReservationData_UserDefinedValues_UserDefinedValue_valueName", "ReservationData_UserDefinedValues_UserDefinedValue");

								List<LinkedHashMap<String, String>> data = new ArrayList<LinkedHashMap<String, String>>();
								List<LinkedHashMap<String, String>> data2 = new ArrayList<LinkedHashMap<String, String>>();
								LinkedHashMap<String, String> data3 = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> data4 = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> data5 = new LinkedHashMap<String, String>();
								data = WSClient.getMultipleNodeList(fetchResvRes, xpath, false, XMLType.RESPONSE);
								// WSClient.writeToReport(LogStatus.INFO,
								// data.toString());

								data3.put("valueName1", OperaPropConfig.getDataSetForCode("UDFLabel_R", "DS_03"));
								data4.put("valueName1", OperaPropConfig.getDataSetForCode("UDFLabel_R", "DS_05"));
								data5.put("valueName1", OperaPropConfig.getDataSetForCode("UDFLabel_R", "DS_01"));
								data2.add(data3);
								data2.add(data4);
								data2.add(data5);
								// WSClient.writeToReport(LogStatus.INFO,
								// data2.toString());

								WSAssert.assertEquals(data, data2, false);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchReservationEXT", "HTNG2008BEXT", "HTNG" })
	public void fetchReservationExt_Ext_1978() {

		String[] preReqs = { "RateCode", "RoomType", "MarketCode", "SourceCode" };
		try {
			String testName = "fetchReservationExt_Ext_1978";
			WSClient.startTest(testName, "Verify correct Estimated Arrival time & Estimated Departure time " + "should be displayed in the FetchReservationExt response " + "when passing ReservationID & ResortID in the FetchReservationExtRequest", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(preReqs)) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				String uname = OPERALib.getUserName();
				OPERALib.setOperaHeader(uname);
				String profileId = CreateProfile.createProfile("DS_01");
				if (!profileId.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId + "</b>");
					WSClient.setData("{var_profileId}", profileId);

					/*******
					 * Checking if pre-requisites required are created or not
					 **********/
					if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

						/******************
						 * Prerequisite 2:Create a Reservation
						 ************************/
						String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");
						WSClient.setData("{var_resvId}", reservationId);
						System.out.println(reservationId);
						if (!reservationId.equals("error")) {
							HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
							/*************
							 * Fetching Reservation
							 ********************/

							String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
							String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
							if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationExtResponse_Result", false)) {
								if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									/***********
									 * Validating the dates against the database
									 ************/
									String query = WSClient.getQuery("QS_02");
									HashMap<String, String> DBList = WSClient.getDBRow(query);

									System.out.println(DBList);

									WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ResortId", HTNGLib.getExtResort(resortOperaValue, interfaceName), false);
									WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_ReservationID", DBList.get("RESV_NAME_ID"), false);

									String DBArrival = DBList.get("BEGIN_DATE");
									DBArrival = DBArrival.replace(' ', 'T');
									DBArrival = DBArrival.substring(0, DBArrival.indexOf('.'));

									WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_EstimatedArrivalTime", DBArrival, false);
									String DBDeparture = DBList.get("END_DATE");
									DBDeparture = DBDeparture.replace(' ', 'T');
									DBDeparture = DBDeparture.substring(0, DBDeparture.indexOf('.'));
									WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_ReservationData_EstimatedDepartureTime", DBDeparture, false);
								}

								// text Element
								if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>Text :  :" + message + "</b>");
								}
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Prerequisite required for Reservation are not created");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "targetedRegression", "FetchReservationExt", "HTNG2008BExt", "HTNG" })
	public void fetchReservationExt_Ext_1981() {

		try {
			String testName = "fetchReservationExt_Ext_1981";
			WSClient.startTest(testName, "Verify correct GuestCounts " + "should be displayed in the FetchReservationExt response " + "when passing ReservationID & ResortID in the FetchReservationExtRequest", "targetedRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				OPERALib.setOperaHeader(uname);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				String profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID 1 : " + profileID1 + "</b>");

					WSClient.setData("{var_profileId1}", profileID1);
					WSClient.setData("{var_profileId}", profileID1);

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					WSClient.setData("{var_adult}", "0");
					WSClient.setData("{var_child}", "0");

					// Prerequisite 2 Create Reservation
					HashMap<String, String> resv = CreateReservation.createReservation("DS_30");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

						String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationExtResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
								String adultCount = WSClient.getAttributeValueByAttribute(fetchResvRes, "ReservationData_GuestCounts_GuestCount", "count", "ADULT", XMLType.RESPONSE);
								expectedValues.put("Count1", adultCount);
								actualValues.put("Count1", WSClient.getData("{var_adult}"));
								WSAssert.assertEquals(expectedValues, actualValues, false);
							}
							if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>Error on the response is :" + message + "</b>");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "targetedRegression", "FetchReservationExt", "HTNG2008BExt", "HTNG" })
	public void fetchReservationExt_Ext_1982() {

		try {
			String testName = "fetchReservationExt_Ext_1982";
			WSClient.startTest(testName, "Verify correct multiple GuestCounts " + "should be displayed in the FetchReservationExt response " + "when passing ReservationID & ResortID in the FetchReservationExtRequest.", "targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				OPERALib.setOperaHeader(uname);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				String profileID1 = CreateProfile.createProfile("DS_01");
				if (!profileID1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID 1 : " + profileID1 + "</b>");

					WSClient.setData("{var_profileId1}", profileID1);
					WSClient.setData("{var_profileId}", profileID1);

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					WSClient.setData("{var_adult}", "1");
					WSClient.setData("{var_child}", "1");

					// Prerequisite 2 Create Reservation
					HashMap<String, String> resv = CreateReservation.createReservation("DS_30");
					String resvID = resv.get("reservationId");

					if (!resvID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

						String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationExtResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();
								String adultCount = WSClient.getAttributeValueByAttribute(fetchResvRes, "ReservationData_GuestCounts_GuestCount", "count", "ADULT", XMLType.RESPONSE);
								String childCount = WSClient.getAttributeValueByAttribute(fetchResvRes, "ReservationData_GuestCounts_GuestCount", "count", "CHILD", XMLType.RESPONSE);
								expectedValues.put("Count1", adultCount);
								expectedValues.put("Count2", childCount);
								actualValues.put("Count1", WSClient.getData("{var_adult}"));
								actualValues.put("Count2", WSClient.getData("{var_child}"));
								WSAssert.assertEquals(expectedValues, actualValues, false);
							}
							if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>Error on the response is :" + message + "</b>");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "targetedRegression", "FetchReservationExt", "HTNG2008BExt", "HTNG" })
	public void fetchReservationExt_Ext_1968() {

		try {
			String testName = "fetchReservationExt_Ext_1968";
			WSClient.startTest(testName, "Verify correct multiple package element codes & package element descriptions should be displayed in the FetchReservationExt response " + "when passing ReservationID & ResortID in the FetchReservationExtRequest", "targetedRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);

				WSClient.setData("{var_fname}", fname);
				WSClient.setData("{var_lname}", lname);
				WSClient.setData("{var_extResort}", resortExtValue);

				String uname = OPERALib.getUserName();
				OPERALib.setOperaHeader(uname);
				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				String profileID = CreateProfile.createProfile("DS_01");

				WSClient.setData("{var_profileId}", profileID);

				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + profileID + "</b>");
					/*************
					 * Prerequisite 2: Fetch Room Type,Rate details,Market Code
					 * and Source Codes
					 ******************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_02"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					/******************
					 * Prerequisite 3:Create a Reservation
					 ************************/

					String resvId = CreateReservation.createReservation("DS_01").get("reservationId");

					if (!resvId.equals("error")) {

						WSClient.setData("{var_resvId}", resvId);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :" + resvId + "</b>");
						/******************
						 * Fetching Reservation
						 ************************/

						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

						String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);

						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationExtResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> expectedValues = new LinkedHashMap<String, String>();

								LinkedHashMap<String, String> xPath = new LinkedHashMap<String, String>();
								xPath.put("PackageElements_PackageElement_ElementCode", "ReservationData_PackageElements_PackageElement");

								actualValues = WSClient.getSingleNodeList(fetchResvRes, xPath, false, XMLType.RESPONSE);
								expectedValues = WSClient.getDBRow(WSClient.getQuery("QS_13"));

								WSAssert.assertEquals(expectedValues,actualValues,false);
							}
						}
						if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
							String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}

					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "targetedRegression", "FetchReservationExt", "HTNG2008BExt", "HTNG" })
	public void fetchReservationExt_Ext_1971() {

		try {

			String testName = "fetchReservationExt_Ext_1971";
			String[] preReqs = { "PreferenceCode", "PreferenceGroup", "RateCode", "RoomType", "MarketCode", "SourceCode" };
			WSClient.startTest(testName, "Verify correct multiple Preference types & preference values " + "should be displayed in FetchReservationExt response " + "when passing ReservationID & ResortID in the FetchReservationExtRequest", "targetedRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			WSClient.setData("{var_interface}", interfaceName);
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(preReqs)) {
				/******************* Create Profile ***************************/
				String profileId = CreateProfile.createProfile("DS_01");
				if (!profileId.equals("error")) {
					WSClient.setData("{var_profileId}", profileId);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId + "</b>");
					/**************************
					 * Add reservation to the profile
					 *****************/
					WSClient.setData("{var_prefCode}", OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_05"));
					WSClient.setData("{var_prefGroup}", OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_05"));
					WSClient.setData("{var_prefCode2}", OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_06"));
					WSClient.setData("{var_prefGroup2}", OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_06"));
					
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					String resvId = CreateReservation.createReservation("DS_33").get("reservationId");

					if (!resvId.equals("error")) {

						WSClient.setData("{var_resvId}", resvId);

						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

						String fetchResvReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);

						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationExtResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationExtResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								List<LinkedHashMap<String, String>> expectedValues = new ArrayList<LinkedHashMap<String, String>>();

								HashMap<String, String> xpath = new HashMap<String, String>();
								List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

								WSClient.writeToReport(LogStatus.INFO, "Query to fetch reservation Lookup details from DataBase");
								String query = WSClient.getQuery("QS_09");
								expectedValues = WSClient.getDBRows(query);

								xpath.put("ReservationPreferences_Preference_Type", "ReservationData_ReservationPreferences_Preference");
								xpath.put("ReservationPreferences_Preference_Value", "ReservationData_ReservationPreferences_Preference");
								actualValues = WSClient.getMultipleNodeList(fetchResvRes, xpath, false, XMLType.RESPONSE);
								WSAssert.assertEquals(expectedValues, actualValues, false);

								System.out.println("expe" + expectedValues);
								System.out.println("ac" + actualValues);
							}

							// text Element
							if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>Text :  :" + message + "</b>");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

}
