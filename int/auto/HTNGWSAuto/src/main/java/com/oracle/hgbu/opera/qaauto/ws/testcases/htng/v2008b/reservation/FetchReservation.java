package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2008b.reservation;

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

public class FetchReservation extends WSSetUp {

	String resvId, mktCode, srcCode;
	String profileID = "";

	/**
	 * Method to check if the HTNG Fetch Reservation is working i.e., fetching
	 * reservation details such as Reservation id,Confirmation no,Resort
	 * Id,Reservation status,Profile id for a given reservation.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * 
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "sanity", "FetchReservation", "HTNG2008B", "HTNG" })

	public void fetchReservation_2008_4530() throws Exception {
		try {
			String testName = "fetchReservation_2008_4530";
			WSClient.startTest(testName, "Verify that the fetching reservation is successful", "sanity");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_extResort}", resortExtValue);

			String uname = OPERALib.getUserName();

			OPERALib.setOperaHeader(uname);

			/************
			 * Prerequisite 1: Create profile
			 *********************************/
			if (profileID == "")
				profileID = CreateProfile.createProfile("DS_01");

			if (!profileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + profileID + "</b>");

				/*************
				 * Prerequisite 2: Fetch Room Type,Rate details,Market Code and
				 * Source Codes
				 ******************/
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					/******************
					 * Prerequisite 3:Create a Reservation
					 ************************/
					WSClient.setData("{var_profileId}", profileID);
					resvId = CreateReservation.createReservation("DS_01").get("reservationId");

					if (!resvId.equals("error")) {

						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :" + resvId + "</b>");
						/******************
						 * Fetching Reservation
						 ************************/
						WSClient.setData("{var_resvId}", resvId);

						/******************
						 * Fetching Reservation
						 ************************/
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String fetchResvReq = WSClient.createSOAPMessage("HTNG2008BFetchReservation", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes,
									"FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query1 = WSClient.getQuery("QS_01");
								HashMap<String, String> dataMap = WSClient.getDBRow(query1);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_ProfileID", dataMap.get("NAME_ID"),
										false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_ResortId",
										HTNGLib.getExtResort(resortOperaValue, interfaceName), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_ReservationID",
										dataMap.get("RESV_NAME_ID"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_ConfirmationNO",
										dataMap.get("CONFIRMATION_NO"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_reservationStatus",
										dataMap.get("RESV_STATUS"), false);
							}
						} else
							WSClient.writeToReport(LogStatus.FAIL, "Fetch Reservation operation has failed");

						if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}

					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Prerequisite failed >> Prerequisite required for Reservation are not created");
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

	/**
	 * Method to check if the HTNG Fetch Reservation is working i.e., fetching
	 * reservation details such as Arrival date and departure date for a given
	 * reservation.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * 
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "FetchReservation", "HTNG2008B", "HTNG" })
	public void fetchReservation_2008_1091() throws Exception {
		try {

			String testName = "fetchReservation_2008_1091";
			WSClient.startTest(testName,
					"Verify that correct arrival and departure date are fetched on the fetch reservation response",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_extResort}", resortExtValue);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			/************
			 * Prerequisite 1: Create profile
			 *********************************/

			if (profileID == "")
				profileID = CreateProfile.createProfile("DS_01");

			if (!profileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + profileID + "</b>");
				/*************
				 * Prerequisite 2: Fetch Room Type,Rate details,Market Code and
				 * Source Codes
				 ******************/
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					/******************
					 * Prerequisite 3:Create a Reservation
					 ************************/
					String resvId = CreateReservation.createReservation("DS_01").get("reservationId");

					if (!resvId.equals("error")) {
						WSClient.setData("{var_resvId}", resvId);
						/******************
						 * Fetching Reservation
						 ************************/

						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :" + resvId + "</b>");
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						/*************
						 * Fetching Reservation
						 ********************/
						String fetchResvReq = WSClient.createSOAPMessage("HTNG2008BFetchReservation", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes,
									"FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								/***********
								 * Validating the dates against the database
								 ************/
								String query = WSClient.getQuery("QS_01");
								HashMap<String, String> DBList = WSClient.getDBRow(query);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_ResortId",
										HTNGLib.getExtResort(resortOperaValue, interfaceName), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_ReservationID",
										DBList.get("RESV_NAME_ID"), false);

								String DBArrival = DBList.get("BEGIN_DATE");
								DBArrival = DBArrival.replace(' ', 'T');
								DBArrival = DBArrival.substring(0, DBArrival.indexOf('.'));

								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_ArrivalDate", DBArrival, false);
								String DBDeparture = DBList.get("END_DATE");
								DBDeparture = DBDeparture.replace(' ', 'T');
								DBDeparture = DBDeparture.substring(0, DBDeparture.indexOf('.'));
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_DepartureDate", DBDeparture, false);
							}
						} else
							WSClient.writeToReport(LogStatus.FAIL, "Fetch Reservation operation has failed");

						if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Prerequisite failed >> Prerequisite required for Reservation are not created");
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

	@Test(groups = { "minimumRegression", "FetchReservation", "HTNG2008B", "HTNG" })
	/**
	 * Method to check if the HTNG Fetch Reservation is working i.e., fetching
	 * correct room number,room rate and room type when a room is assigned to
	 * reservation.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * 
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code->Room to be assigned.
	 */
	public void fetchReservation_2008_1092() throws Exception {
		try {
			String testName = "fetchReservation_2008_1092";
			WSClient.startTest(testName,
					"Verify that correct room number, room rate, and room type is fetched on the fetch reservation response.",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_extResort}", resortExtValue);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
			/************
			 * Prerequisite 1: Create profile
			 *********************************/

			if (profileID == "")
				profileID = CreateProfile.createProfile("DS_01");

			if (!profileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + profileID + "</b>");
				/*************
				 * Prerequisite 2: Fetch Room Type,Rate details,Market Code and
				 * Source Codes
				 ******************/
				WSClient.setData("{var_profileId}", profileID);
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					/******************
					 * Prerequisite 3:Create a Reservation
					 ************************/

					resvId = CreateReservation.createReservation("DS_01").get("reservationId");

					if (!resvId.equals("error")) {
						WSClient.setData("{var_resvId}", resvId);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :" + resvId + "</b>");
						/******************
						 * Fetching Reservation
						 ************************/

						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());

						/*******************
						 * Prerequisite 6:Assign a Room
						 **************************/
						String createRoomReq = WSClient.createSOAPMessage("AutoAssignRoom", "DS_01");
						String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

						if (WSAssert.assertIfElementExists(createRoomRes,
								"AutoAssignRoomRS_AutoRoomAssignRsList_RoomNumber", true)) {

							String roomNo = WSClient.getElementValue(createRoomRes,
									"AutoAssignRoomRS_AutoRoomAssignRsList_RoomNumber", XMLType.RESPONSE);
							WSClient.setData("{var_roomNo}", roomNo);
							WSClient.setData("{var_roomNumber}", roomNo);
							System.out.println("\n\n\n\n" + roomNo + "\n\n\n\n");
							if (roomNo != null && roomNo != "") {
								/******************
								 * Fetching Reservation
								 ************************/
								String fetchResvReq = WSClient.createSOAPMessage("HTNG2008BFetchReservation", "DS_01");
								String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
								if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result",
										false)) {
									if (WSAssert.assertIfElementValueEquals(fetchResvRes,
											"FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										HashMap<String, String> dataMap4 = WSClient
												.getDBRow(WSClient.getQuery("QS_01"));
										System.out.println(
												"******************************************************\n\n\n\n");
										System.out.println(dataMap4);
										System.out.println(
												"\n\n\n\n******************************************************");
										WSAssert.assertIfElementValueEquals(fetchResvRes,
												"FetchReservationResponse_ReservationData_ProfileID",
												dataMap4.get("NAME_ID"), false);
										WSAssert.assertIfElementValueEquals(fetchResvRes,
												"FetchReservationResponse_ReservationData_ResortId", resortExtValue,
												false);
										WSAssert.assertIfElementValueEquals(fetchResvRes,
												"FetchReservationResponse_ReservationData_ReservationID",
												dataMap4.get("RESV_NAME_ID"), false);
										WSAssert.assertIfElementValueEquals(fetchResvRes,
												"FetchReservationResponse_ReservationData_ConfirmationNO",
												dataMap4.get("CONFIRMATION_NO"), false);
										WSAssert.assertIfElementValueEquals(fetchResvRes,
												"FetchReservationResponse_ReservationData_reservationStatus",
												dataMap4.get("RESV_STATUS"), false);
										HashMap<String, String> dataMap5 = WSClient
												.getDBRow(WSClient.getQuery("QS_05"));
										WSAssert.assertIfElementValueEquals(fetchResvRes,
												"FetchReservationResponse_ReservationData_ShortRateCode",
												dataMap5.get("RATE_CODE"), false);
										WSAssert.assertIfElementValueEquals(fetchResvRes,
												"FetchReservationResponse_ReservationData_RoomNumber",
												dataMap5.get("ROOM"), false);
										HashMap<String, String> dataMap6 = WSClient
												.getDBRow(WSClient.getQuery("QS_06"));
										WSAssert.assertIfElementValueEquals(fetchResvRes,
												"FetchReservationResponse_ReservationData_ShortRoomType",
												dataMap6.get("LABEL"), false);
									}
								} else
									WSClient.writeToReport(LogStatus.FAIL, "Fetch Reservation operation has failed");

								if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {

									/****
									 * Verifying that the error message is
									 * populated on the response
									 ********/

									String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"The text displayed in the response is :" + message);
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisite failed >> No room Number Assigned");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> AssignRoom Failed");
						}
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Prerequisite failed >> Prerequisite required for Reservation are not created");
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

	@Test(groups = { "minimumRegression", "FetchReservation", "HTNG2008B", "HTNG" })
	/**
	 * Method to check if the HTNG Fetch Reservation is working i.e.,to check if
	 * the correct package code,package descripted are displayed on the fetch
	 * reservation response for a given reservation .
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * 
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	public void fetchReservation_2008_1093() throws Exception {
		try {
			String testName = "fetchReservation_2008_1093";
			WSClient.startTest(testName,
					"Verify that correct package code,description are fetched on the fetch reservation response.",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_extResort}", resortExtValue);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
			/************
			 * Prerequisite 1: Create profile
			 *********************************/

			if (profileID == "")
				profileID = CreateProfile.createProfile("DS_01");

			WSClient.setData("{var_profileId}", profileID);

			if (!profileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + profileID + "</b>");
				/*************
				 * Prerequisite 2: Fetch Room Type,Rate details,Market Code and
				 * Source Codes
				 ******************/
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					/******************
					 * Prerequisite 3:Create a Reservation
					 ************************/

					resvId = CreateReservation.createReservation("DS_01").get("reservationId");

					if (!resvId.equals("error")) {

						WSClient.setData("{var_resvId}", resvId);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :" + resvId + "</b>");
						/******************
						 * Fetching Reservation
						 ************************/

						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());

						String fetchResvReq = WSClient.createSOAPMessage("HTNG2008BFetchReservation", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes,
									"FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								HashMap<String, String> dataMap = WSClient.getDBRow(WSClient.getQuery("QS_01"));
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_ProfileID", dataMap.get("NAME_ID"),
										false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_ResortId", resortExtValue, false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_ReservationID",
										dataMap.get("RESV_NAME_ID"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_ConfirmationNO",
										dataMap.get("CONFIRMATION_NO"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_reservationStatus",
										dataMap.get("RESV_STATUS"), false);
								String packageId = WSClient.getElementValue(fetchResvRes,
										"PackageElements_PackageElement_ElementCode", XMLType.RESPONSE);
								WSClient.setData("{var_pkgId}", packageId);
								HashMap<String, String> dataMap2 = WSClient.getDBRow(WSClient.getQuery("QS_03"));
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"PackageElements_PackageElement_ElementCode", dataMap2.get("PRODUCT"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"PackageElements_PackageElement_ElementDescription",
										dataMap2.get("DESCRIPTION"), false);
							}
						} else
							WSClient.writeToReport(LogStatus.FAIL, "Fetch Reservation operation has failed");

						if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}

					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Prerequisite failed >> Prerequisite required for Reservation are not created");
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

	@Test(groups = { "minimumRegression", "FetchReservation", "HTNG2008B", "HTNG" })
	/**
	 * Method to check if the HTNG Fetch Reservation is working i.e.,to check if
	 * correct Reservation id,Confirmation no,Resort Id,Reservation
	 * status,Profile id,market code,source code are fetched on the response for
	 * a given reservation.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * 
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	public void fetchReservation_2008_1095() throws Exception {
		try {
			String testName = "fetchReservation_2008_1095";
			WSClient.startTest(testName,
					"Verify that correct Reservation id,Confirmation no,ResortId,Reservation status,Profile id,market code,source code are fetched on the fetch reservation response. ",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_extResort}", resortExtValue);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			/************
			 * Prerequisite 1: Create profile
			 *********************************/

			if (profileID == "")
				profileID = CreateProfile.createProfile("DS_01");

			WSClient.setData("{var_profileId}", profileID);

			if (!profileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + profileID + "</b>");

				/*************
				 * Prerequisite 2: Fetch Room Type,Rate details,Market Code and
				 * Source Codes
				 ******************/
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					/******************
					 * Prerequisite 3:Create a Reservation
					 ************************/

					resvId = CreateReservation.createReservation("DS_01").get("reservationId");

					if (!resvId.equals("error")) {
						WSClient.setData("{var_resvId}", resvId);

						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :" + resvId + "</b>");

						/******************
						 * Fetching Reservation
						 ************************/
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());

						/******************
						 * Fetching Reservation
						 ************************/
						String fetchResvReq = WSClient.createSOAPMessage("HTNG2008BFetchReservation", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);

						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes,
									"FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								HashMap<String, String> dataMap = WSClient.getDBRow(WSClient.getQuery("QS_01"));

								HashMap<String, String> dataMap1 = WSClient.getDBRow(WSClient.getQuery("QS_08"));

								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_ProfileID", dataMap.get("NAME_ID"),
										false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_ResortId", resortExtValue, false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_ReservationID",
										dataMap.get("RESV_NAME_ID"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_ConfirmationNO",
										dataMap.get("CONFIRMATION_NO"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_reservationStatus",
										dataMap.get("RESV_STATUS"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_MarketSegment",
										dataMap1.get("MARKET_CODE"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_SourceCode",
										dataMap1.get("ORIGIN_OF_BOOKING"), false);

								HashMap<String, String> dataMap3 = WSClient.getDBRow(WSClient.getQuery("QS_04"));
								String no_post_db = dataMap3.get("POSTING_ALLOWED_YN");
								System.out.println("************\n\n\n\n" + no_post_db + "\n\n\n\n*********");
								String no_post_equiv = "";
								if (no_post_db.equals("N"))
									no_post_equiv = "true";
								else if (no_post_db.equals("Y"))
									no_post_equiv = "false";
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_ReservationData_NoPostFlag", no_post_equiv, false);
							}
						} else
							WSClient.writeToReport(LogStatus.FAIL, "Fetch Reservation operation has failed");

						if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Prerequisite failed >> Prerequisite required for Reservation are not created");
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

	@Test(groups = { "minimumRegression", "FetchReservation", "HTNG2008B", "HTNG" })
	/**
	 * Method to check if the HTNG Fetch Reservation is working i.e.,to check if
	 * correct transportation details are fetched on the response for a given
	 * reservation.
	 * 
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * 
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	public void fetchReservation_2008_1100() throws Exception {
		try {
			String testName = "fetchReservation_2008_1100";
			WSClient.startTest(testName, "Verify that the transport details of a reservation are fetched successfully",
					"minimumRegression");
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_extResort}", resortExtValue);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			/************
			 * Prerequisite 1: Create profile
			 *********************************/

			if (profileID == "")
				profileID = CreateProfile.createProfile("DS_01");

			if (!profileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + profileID + "</b>");
				WSClient.setData("{var_profileId}", profileID);
				/*************
				 * Prerequisite 2: Fetch Room Type,Rate details,Market Code and
				 * Source Codes
				 ******************/
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					WSClient.setData("{var_transportType}",
							OperaPropConfig.getDataSetForCode("TransportationType", "DS_01"));

					WSClient.setData("{var_transportCode}", "123");

					/******************
					 * Prerequisite 3:Create a Reservation
					 ************************/
					resvId = CreateReservation.createReservation("DS_15").get("reservationId");

					if (!resvId.equals("error")) {

						WSClient.setData("{var_resvId}", resvId);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :" + resvId + "</b>");
						/******************
						 * Fetching Reservation
						 ************************/

						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String fetchResvReq = WSClient.createSOAPMessage("HTNG2008BFetchReservation", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);

						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes,
									"FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								HashMap<String, String> dataMap = WSClient.getDBRow(WSClient.getQuery("QS_07"));

								/*** Validation ****/

								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"Transportation_Transportation_TranspoftType",
										dataMap.get("ARRIVAL_TRANSPORT_TYPE"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"Transportation_Transportation_TransportCode",
										dataMap.get("ARRIVAL_TRANSPORT_CODE"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"Transportation_Transportation_Carrier", dataMap.get("ARRIVAL_CARRIER_CODE"),
										false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"Transportation_Transportation_Location", dataMap.get("ARRIVAL_STATION_CODE"),
										false);

								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"Transportation_Transportation_TranspoftType_2",
										dataMap.get("DEPARTURE_TRANSPORT_TYPE"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"Transportation_Transportation_TransportCode_2",
										dataMap.get("DEPARTURE_TRANSPORT_CODE"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"Transportation_Transportation_Carrier_2",
										dataMap.get("DEPARTURE_CARRIER_CODE"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes,
										"Transportation_Transportation_Location_2",
										dataMap.get("DEPARTURE_STATION_CODE"), false);
							}
						} else
							WSClient.writeToReport(LogStatus.FAIL, "Fetch Reservation operation has failed");

						if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Prerequisite failed >> Prerequisite required for Reservation are not created");
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

	// Invalid Resvervation Id
	@Test(groups = { "fullRegression", "FetchReservation", "HTNG2008B", "HTNG" })
	public void fetchReservation_2008_1102() {

		try {
			String testName = "fetchReservation_2008_1102";
			WSClient.startTest(testName, "Verify Error message when Invalid  Reservation ID is passed.",
					"fullRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_extResort}", resortExtValue);

			String uname = OPERALib.getUserName();

			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID == "")
					profileID = CreateProfile.createProfile("DS_01");

				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.PASS, "Profile ID :" + profileID);

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
					WSClient.setData("{var_profileId}", profileID);
					resvId = WSClient.getKeywordData("{KEYWORD_RANDNUM_12}");
					if (!resvId.equals("error")) {
						WSClient.writeToReport(LogStatus.PASS, "Reservation ID :" + resvId);
						/******************
						 * Fetching Reservation
						 ************************/
						WSClient.setData("{var_resvId}", resvId);

						/******************
						 * Fetching Reservation
						 ************************/

						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());

						String fetchResvReq = WSClient.createSOAPMessage("HTNG2008BFetchReservation", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes,
									"FetchReservationResponse_Result_resultStatusFlag", "FAIL", false)) {

							}
							// Text Message
							if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is : <b>" + message + "</b>");
							}
						}

						else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite failed >> Reservation ID is not created");
						}
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Prerequisite failed >> Prerequisite required for Reservation are not created");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	// Prefernces
	@Test(groups = { "minimumRegression", "FetchReservationExt", "HTNG2008B", "HTNG" })
	public void fetchReservation_2008_1096() {

		try {

			String testName = "fetchReservation_2008_1096";
			String[] preReqs = { "PreferenceCode", "PreferenceGroup", "RateCode", "RoomType", "MarketCode",
					"SourceCode" };
			WSClient.startTest(testName,
					"Verify correct Preference type & preference value should be displayed in FetchReservation response when passing ReservationID & ResortID in the FetchReservationRequest",
					"minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_interface}", interfaceName);
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(preReqs)) {
				/*******************
				 * Create Profile
				 ***************************/
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

						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String fetchResvReq = WSClient.createSOAPMessage("HTNG2008BFetchReservation", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes,
									"FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();

								HashMap<String, String> xpath = new HashMap<String, String>();
								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();

								WSClient.writeToReport(LogStatus.INFO,
										"Query to fetch reservation Lookup details from DataBase");
								String query = WSClient.getQuery("QS_12");
								db = WSClient.getDBRow(query);

								xpath.put("ReservationPreferences_Preference_Type",
										"ReservationData_ReservationPreferences_Preference");
								xpath.put("ReservationPreferences_Preference_Value",
										"ReservationData_ReservationPreferences_Preference");
								actualValues = WSClient.getSingleNodeList(fetchResvRes, xpath, false, XMLType.RESPONSE);
								WSAssert.assertEquals(db, actualValues, false);
							}

							// text Element
							if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
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

	// Missing Resv Id
	@Test(groups = { "fullRegression", "FetchReservation", "HTNG2008B", "HTNG" })
	public void fetchReservation_2008_1103() {
		String[] preReqs = { "RateCode", "RoomType", "MarketCode", "SourceCode" };
		try {
			String testName = "fetchReservation_2008_1103";
			WSClient.startTest(testName,
					"Verify that an error Message is obtained when no reservation ID is passed in the request",
					"fullRegression");
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

				/******************
				 * Fetching Reservation
				 ************************/
				WSClient.setData("{var_resvId}", "");
				HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
				String fetchResvReq = WSClient.createSOAPMessage("HTNG2008BFetchReservation", "DS_01");
				String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
				if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
					if (WSAssert.assertIfElementValueEquals(fetchResvRes,
							"FetchReservationResponse_Result_resultStatusFlag", "FAIL", false)) {
					}
					// text Element
					if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
						String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>Text :  :" + message + "</b>");
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "fullRegression", "FetchReservation", "HTNG2008B", "HTNG" })

	public void fetchReservation_2008_1098() throws Exception {
		try {
			String testName = "fetchReservation_2008_1098";
			WSClient.startTest(testName, "Verify that udfs are getting fetched in fetch reservation response",
					"fullRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_extResort}", resortExtValue);

			String uname = OPERALib.getUserName();

			OPERALib.setOperaHeader(uname);

			/************
			 * Prerequisite 1: Create profile
			 *********************************/
			if (profileID == "")
				profileID = CreateProfile.createProfile("DS_01");

			if (!profileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + profileID + "</b>");

				/*************
				 * Prerequisite 2: Fetch Room Type,Rate details,Market Code and
				 * Source Codes
				 ******************/
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					/******************
					 * Prerequisite 3:Create a Reservation
					 ************************/
					WSClient.setData("{var_profileId}", profileID);
					resvId = CreateReservation.createReservation("DS_04").get("reservationId");

					if (!resvId.equals("error")) {

						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :" + resvId + "</b>");
						/******************
						 * Fetching Reservation
						 ************************/
						WSClient.setData("{var_resvId}", resvId);
						/******************
						 * Adding UDF
						 ************************/
						String charUDF = HTNGLib.getUDFLabel("C", "R");
						String numUDF = HTNGLib.getUDFLabel("N", "R");
						String dateUDF = HTNGLib.getUDFLabel("D", "R");
						String charName = HTNGLib.getUDFName("C", charUDF, "R");
						String numName = HTNGLib.getUDFName("N", numUDF, "R");
						String dateName = HTNGLib.getUDFName("D", dateUDF, "R");
						WSClient.setData("{var_charName}", charName);
						WSClient.setData("{var_numericName}", numName);
						WSClient.setData("{var_dateName}", dateName);
						String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_06");
						String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

						// ******OWS Fetch Booking*******//

						if (WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success", true)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added UDFs to reservation.</b>");
							/******************
							 * Fetching Reservation
							 ************************/
							HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
									HTNGLib.getInterfaceFromAddress());
							String fetchResvReq = WSClient.createSOAPMessage("HTNG2008BFetchReservation", "DS_01");
							String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
							if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result",
									false)) {
								if (WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									LinkedHashMap<String, String> actuals = new LinkedHashMap<String, String>();

									actuals.put(WSClient.getData("{var_dateName}"),
											WSClient.getElementValue(fetchResvRes,
													"UserDefinedValues_UserDefinedValue_DateValue", XMLType.RESPONSE)
													.replace("T", " "));
									actuals.put(WSClient.getData("{var_numericName}"),
											WSClient.getElementValue(fetchResvRes,
													"UserDefinedValues_UserDefinedValue_NumericValue",
													XMLType.RESPONSE));
									actuals.put(WSClient.getData("{var_charName}"),
											WSClient.getElementValue(fetchResvRes,
													"UserDefinedValues_UserDefinedValue_CharacterValue",
													XMLType.RESPONSE));
									LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_10"));

									WSAssert.assertEquals(db, actuals, false);
								}
							} else
								WSClient.writeToReport(LogStatus.FAIL, "Fetch Reservation operation has failed");

							if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite failed >> Prerequisite required for adding UDF");
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Prerequisite failed >> Prerequisite required for Reservation are not created");
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

	// @Test(groups = { "minimumRegression", "FetchReservation", "HTNG2008B",
	// "HTNG" })
	//
	// public void fetchReservation_2008_profiledet() throws Exception {
	// try {
	// String testName = "fetchReservation_2008_4532";
	// WSClient.startTest(testName, "Verify that profile details are correctly
	// fetched in the fetchreservation ", "minimumRegression");
	//
	// String interfaceName = HTNGLib.getHTNGInterface();
	// String resortOperaValue = OPERALib.getResort();
	// String resortExtValue =
	// HTNGLib.getExtResort(resortOperaValue,interfaceName);
	// String chain = OPERALib.getChain();
	// String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
	// String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
	//
	// WSClient.setData("{var_profileSource}", interfaceName);
	//
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_chain}", chain);
	// WSClient.setData("{var_fname}", fname);
	// WSClient.setData("{var_lname}", lname);
	// WSClient.setData("{var_extResort}", resortExtValue);
	//
	// String uname = OPERALib.getUserName();
	//
	// OPERALib.setOperaHeader(uname);
	//
	// /************
	// * Prerequisite 1: Create profile
	// *********************************/
	// WSClient.setData("{var_gender}","F");
	// WSClient.setData("{var_nationality}",OperaPropConfig.getDataSetForCode("Nationality",
	// "DS_02"));
	// WSClient.setData("{var_nameTitle}",OperaPropConfig.getDataSetForCode("Title",
	// "DS_01"));
	// WSClient.setData("{var_vipCode}",OperaPropConfig.getDataSetForCode("VipLevel",
	// "DS_01"));
	// WSClient.setData("{var_birthDate}","1995-11-09");
	// String profileID=CreateProfile.createProfile("DS_29");
	//
	//
	// if (!profileID.equals("error")) {WSClient.writeToReport(LogStatus.INFO,
	// "<b>Profile ID :"+profileID+"</b>");
	//
	//
	// /*************
	// * Prerequisite 2: Fetch Room Type,Rate details,Market Code and
	// * Source Codes
	// ******************/
	// if(OperaPropConfig.getPropertyConfigResults(new String[]
	// {"RateCode","RoomType","SourceCode","MarketCode"}))
	// {
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	//
	// /******************
	// * Prerequisite 3:Create a Reservation
	// ************************/
	// WSClient.setData("{var_profileId}", profileID);
	// resvId =
	// CreateReservation.createReservation("DS_01").get("reservationId");
	//
	// if (!resvId.equals("error")) {
	//
	//
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID
	// :"+resvId+"</b>");
	// /******************
	// * Fetching Reservation
	// ************************/
	// WSClient.setData("{var_resvId}", resvId);
	//
	//
	// /******************
	// * Fetching Reservation
	// ************************/
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(),
	// OPERALib.getPassword(),HTNGLib.getInterfaceFromAddress());
	// String fetchResvReq =
	// WSClient.createSOAPMessage("HTNG2008BFetchReservation", "DS_01");
	// String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
	// if (WSAssert.assertIfElementExists(fetchResvRes,
	// "FetchReservationResponse_Result",false))
	// {
	// if
	// (WSAssert.assertIfElementValueEquals(fetchResvRes,"FetchReservationResponse_Result_resultStatusFlag",
	// "SUCCESS", false))
	// {
	// String query1=WSClient.getQuery("QS_09");
	// HashMap<String, String> dataMap = WSClient.getDBRow(query1);
	// WSAssert.assertIfElementValueEquals(fetchResvRes,
	// "FetchReservationResponse_ReservationData_ProfileID",dataMap.get("NAME_ID"),
	// false);
	// WSAssert.assertIfElementValueEquals(fetchResvRes,
	// "ReservationData_ProfileInfo_NameTitle", dataMap.get("TITLE"),false);
	// WSAssert.assertIfElementValueEquals(fetchResvRes,
	// "ReservationData_ProfileInfo_FirstName",dataMap.get("FIRST"), false);
	// WSAssert.assertIfElementValueEquals(fetchResvRes,
	// "ReservationData_ProfileInfo_MiddleName",dataMap.get("MIDDLE"), false);
	// WSAssert.assertIfElementValueEquals(fetchResvRes,
	// "ReservationData_ProfileInfo_LastName",dataMap.get("LAST"), false);
	// }
	// }else
	// WSClient.writeToReport(LogStatus.FAIL, "Fetch Reservation operation has
	// failed");
	//
	// if (WSAssert.assertIfElementExists(fetchResvRes,
	// "Result_Text_TextElement", true)) {
	//
	// /****
	// * Verifying that the error message is populated on the
	// * response
	// ********/
	//
	// String message = WSAssert.getElementValue(fetchResvRes,
	// "Result_Text_TextElement",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "The text displayed in the response is :" + message);
	// }
	//
	//
	// }
	//
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>
	// Prerequisite required for Reservation are not created");
	// }
	// }
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// try {
	// if(!WSClient.getData("{var_resvId}").equals(""))
	// CancelReservation.cancelReservation("DS_02");
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

	@Test(groups = { "fullRegression", "FetchReservation", "HTNG2008B", "HTNG" })

	public void fetchReservation_2008_1106() throws Exception {
		try {
			String testName = "fetchReservation_2008_1106";
			WSClient.startTest(testName, "Verify that specials are getting fetched in fetch reservation response",
					"fullRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			String chain = OPERALib.getChain();
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			WSClient.setData("{var_extResort}", resortExtValue);

			String uname = OPERALib.getUserName();

			OPERALib.setOperaHeader(uname);

			/************
			 * Prerequisite 1: Create profile
			 *********************************/
			if (profileID == "")
				profileID = CreateProfile.createProfile("DS_01");

			if (!profileID.equals("error")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + profileID + "</b>");

				/*************
				 * Prerequisite 2: Fetch Room Type,Rate details,Market Code and
				 * Source Codes
				 ******************/
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					/******************
					 * Prerequisite 3:Create a Reservation
					 ************************/
					WSClient.setData("{var_profileId}", profileID);
					resvId = CreateReservation.createReservation("DS_04").get("reservationId");

					if (!resvId.equals("error")) {

						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :" + resvId + "</b>");
						/******************
						 * Fetching Reservation
						 ************************/
						WSClient.setData("{var_resvId}", resvId);
						/******************
						 * Adding UDF
						 ************************/
						WSClient.setData("{var_prefType}", "SPECIALS");
						WSClient.setData("{var_prefValue}",
								OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_04"));
						String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_05");
						String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

						// ******OWS Fetch Booking*******//

						if (WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success", true)) {
							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added UDFs to reservation.</b>");
							/******************
							 * Fetching Reservation
							 ************************/
							HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
									HTNGLib.getInterfaceFromAddress());
							String fetchResvReq = WSClient.createSOAPMessage("HTNG2008BFetchReservation", "DS_01");
							String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
							if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result",
									false)) {
								if (WSAssert.assertIfElementValueEquals(fetchResvRes,
										"FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									LinkedHashMap<String, String> actuals = new LinkedHashMap<String, String>();
									actuals.put("TYPE", WSClient.getElementValue(fetchResvRes,
											"ReservationPreferences_Preference_Type", XMLType.RESPONSE));
									actuals.put("VALUE", WSClient.getElementValue(fetchResvRes,
											"ReservationPreferences_Preference_Value", XMLType.RESPONSE));
									LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_11"));

									WSAssert.assertEquals(db, actuals, false);
								}
							} else
								WSClient.writeToReport(LogStatus.FAIL, "Fetch Reservation operation has failed");

							if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message);
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite failed >> Prerequisite required for adding special");
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Prerequisite failed >> Prerequisite required for Reservation are not created");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				// if(!WSClient.getData("{var_resvId}").equals(""))
				// CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// @Test(groups = { "minimumRegression", "FetchReservation", "HTNG2008B",
	// "HTNG" })
	//
	// public void fetchReservation_2008_actvity() throws Exception {
	// try {
	// String testName = "fetchReservation_2008_4530";
	// WSClient.startTest(testName, "Verify that activity is fetched in fetch
	// reservation response", "minimumRegression");
	//
	// String uname=OPERALib.getUserName();
	// String pwd=OPERALib.getPassword();
	// String fromAddress=HTNGLib.getInterfaceFromAddress();
	// String interfaceName = HTNGLib.getHTNGInterface();
	// String resortOperaValue = OPERALib.getResort();
	// String resortExtValue =
	// HTNGLib.getExtResort(resortOperaValue,interfaceName);
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	//
	// //************Prerequisite 1 - Opera Create
	// Profile******************************************//
	// if (OperaPropConfig.getPropertyConfigResults(
	// new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
	//
	// OPERALib.setOperaHeader(uname);
	//
	// if(profileID.equals(""))
	// profileID=CreateProfile.createProfile("DS_01");
	// if(!profileID.equals("error"))
	// {
	// WSClient.setData("{var_profileId}", profileID);
	// WSClient.setData("{var_profileID}", profileID);
	//
	// WSClient.setData("{var_E_profileID}",WSClient.getKeywordData("{KEYWORD_ID}"));
	//
	// //************Prerequisite 2 - HTNG
	// Subscription*****************************************//
	// HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
	// String subscriptionReq =
	// WSClient.createSOAPMessage("HTNG2008Subscription", "DS_05");
	// String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);
	//
	// if (WSAssert.assertIfElementValueEquals(subscriptionRes,
	// "SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", true)) {
	//
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	//
	// //******************** Prerequisite 3 - Create
	// Reservation****************************//
	//
	// OPERALib.setOperaHeader(uname);
	//
	// HashMap<String,String> resv=CreateReservation.createReservation("DS_05");
	// String resvID = resv.get("reservationId");
	//
	// if(!resvID.equals("error"))
	// {
	//
	// WSClient.setData("{var_resvId}", resvID);
	//
	//
	// WSClient.setData("{var_extID}", WSClient.getKeywordData("{KEYWORD_ID}"));
	//
	// WSClient.setData("{var_extResort}", resortExtValue);
	// WSClient.setData("{var_actstatus}",
	// HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_STATUS"));
	// WSClient.setData("{var_acttype}",
	// HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_TYPE"));
	// WSClient.setData("{var_actlocation}",
	// HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"ACTIVITY_LOCATION"));
	//
	// // **********************HTNG Creating
	// activity*****************************//
	//
	// HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
	// String createActivityReq =
	// WSClient.createSOAPMessage("HTNG2008CreateActivity", "DS_02");
	// String createActivityResponseXML =
	// WSClient.processSOAPMessage(createActivityReq);
	//
	// if (WSAssert.assertIfElementValueEquals(createActivityResponseXML,
	// "CreateActivityResponse_Results_resultStatusFlag", "SUCCESS", true)) {
	//
	// String fetchResvReq =
	// WSClient.createSOAPMessage("HTNG2008BFetchReservation", "DS_01");
	// String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
	// if (WSAssert.assertIfElementExists(fetchResvRes,
	// "FetchReservationResponse_Result", false)) {
	// if (WSAssert.assertIfElementValueEquals(fetchResvRes,
	// "FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
	//
	// //DB Validation
	// }
	// } else
	// WSClient.writeToReport(LogStatus.FAIL, "Fetch Reservation operation has
	// failed");
	//
	// if (WSAssert.assertIfElementExists(fetchResvRes,
	// "Result_Text_TextElement", true)) {
	//
	// /****
	// * Verifying that the error message is populated on
	// * the response
	// ********/
	//
	// String message = WSAssert.getElementValue(fetchResvRes,
	// "Result_Text_TextElement", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "The text displayed in the
	// response is :" + message);
	// }
	//
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>
	// Prerequisite required for activity are not created");
	// }
	//
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >>
	// Prerequisite required for subscription");
	// }
	// }
	// }
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// try {
	// if (!WSClient.getData("{var_resvId}").equals(""))
	// CancelReservation.cancelReservation("DS_02");
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

	/*
	 * @Test(groups = { "fullRegression", "FetchReservation", "HTNG2008B",
	 * "HTNG" })
	 * 
	 * public void fetchReservation_2008_42490() throws Exception { String[]
	 * preReqs = { "RateCode", "RoomType", "MarketCode", "SourceCode" }; try {
	 * 
	 * String testName = "fetchReservation_2008_42490";
	 * WSClient.startTest(testName,
	 * "Verify that error message is populated when opera hotel code is sent on the response."
	 * , "fullRegression");
	 * 
	 * if (OperaPropConfig.getPropertyConfigResults(preReqs)) { String
	 * interfaceName = HTNGLib.getHTNGInterface(); String resortOperaValue =
	 * OPERALib.getResort();
	 * 
	 * String uname = OPERALib.getUserName(); String pwd =
	 * OPERALib.getPassword(); String fromAddress =
	 * HTNGLib.getInterfaceFromAddress();
	 * 
	 * OPERALib.setOperaHeader(uname);
	 * 
	 * WSClient.setData("{var_profileSource}", interfaceName);
	 * WSClient.setData("{var_resort}", resortOperaValue);
	 * WSClient.setData("{var_extResort}", resortOperaValue);
	 * 
	 *//*******************************
		 * Pre Requisite : 1 create Profile
		 *************/
	/*
	 * String profileId = CreateProfile.createProfile("DS_01");
	 * 
	 * if (!profileId.equals("error")) {
	 * 
	 * WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId +
	 * "</b>");
	 *//*************
		 * Prerequisite 2: Fetch Room Type,Rate details,Market Code and Source
		 * Codes
		 ******************/
	/*
	 * WSClient.setData("{VAR_RATEPLANCODE}",
	 * OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	 * WSClient.setData("{VAR_ROOMTYPE}",
	 * OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	 * WSClient.setData("{VAR_MARKETCODE}",
	 * OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	 * WSClient.setData("{var_sourceCode}",
	 * OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	 * 
	 *//******************
		 * Prerequisite 3:Create a Reservation
		 ************************/

	/*
	 * String resvId =
	 * CreateReservation.createReservation("DS_01").get("reservationId");
	 * WSClient.setData("{var_resvId}", resvId);
	 * 
	 * if (!resvId.equals("error")) {
	 * 
	 *//******************
		 * Fetching Reservation
		 ************************//*
								 * HTNGLib.setHTNGHeader(uname, pwd,
								 * fromAddress);
								 * 
								 * String fetchResvReq =
								 * WSClient.createSOAPMessage(
								 * "HTNG2008BFetchReservation", "DS_01"); String
								 * fetchResvRes =
								 * WSClient.processSOAPMessage(fetchResvReq); if
								 * (WSAssert.assertIfElementExists(fetchResvRes,
								 * "FetchReservationResponse_Result", false)) {
								 * if (WSAssert.assertIfElementValueEquals(
								 * fetchResvRes,
								 * "FetchReservationResponse_Result_resultStatusFlag",
								 * "FAIL", false)) {
								 * 
								 * // text Element if
								 * (WSAssert.assertIfElementExists(fetchResvRes,
								 * "Result_Text_TextElement", true)) { String
								 * message =
								 * WSAssert.getElementValue(fetchResvRes,
								 * "Result_Text_TextElement", XMLType.RESPONSE);
								 * WSClient.writeToReport(LogStatus.INFO,
								 * "<b>Error on the response is :" + message +
								 * "</b>"); } } } } }
								 * 
								 * } } catch (Exception e) {
								 * WSClient.writeToReport(LogStatus.ERROR,
								 * "Exception occured in test due to:" + e); }
								 * finally { try { if
								 * (!WSClient.getData("{var_resvId}").equals("")
								 * )
								 * CancelReservation.cancelReservation("DS_02");
								 * } catch (Exception e) { // TODO
								 * Auto-generated catch block
								 * e.printStackTrace(); } }
								 * 
								 * }
								 */

	@Test(groups = { "fullRegression", "FetchReservation", "HTNG2008B", "HTNG" })

	public void fetchReservation_2008_1104() {
		try {

			String testName = "fetchReservation_2008_1104";
			WSClient.startTest(testName, "Verify that primary profile detail is being displayed for shared reservation",
					"fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Successfully combined the reservation.</b>");
									WSClient.setData("{var_resvId}", combineResID);
									WSClient.setData("{var_resvDate}",
											WSClient.getDBRow(WSClient.getQuery("QS_01")).get("BEGIN_DATE"));

									HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

									String fetchResvReq = WSClient.createSOAPMessage("HTNG2008BFetchReservation",
											"DS_01");
									String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
									if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result",
											false)) {
										if (WSAssert.assertIfElementValueEquals(fetchResvRes,
												"FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {

											String query1 = WSClient.getQuery("QS_14");
											HashMap<String, String> dataMap = WSClient.getDBRow(query1);
											WSAssert.assertIfElementValueEquals(fetchResvRes,
													"FetchReservationResponse_ReservationData_ProfileID",
													dataMap.get("NAME_ID"), false);
											WSAssert.assertIfElementValueEquals(fetchResvRes,
													"FetchReservationResponse_ReservationData_ReservationID",
													dataMap.get("RESV_NAME_ID"), false);
											WSAssert.assertIfElementValueEquals(fetchResvRes,
													"FetchReservationResponse_ReservationData_ConfirmationNO",
													dataMap.get("CONFIRMATION_NO"), false);

										}

										if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement",
												true)) {
											String message = WSAssert.getElementValue(fetchResvRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Error on the response is :" + message + "</b>");
										}
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING,
											"Blocked : Unable to combine reservation");
								}
							}
						}
					}
				}
			}

		}

		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation for reservation 1
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
				if (!WSClient.getData("{var_resvId1}").equals(""))
					CancelReservation.cancelReservation("DS_03");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "fullRegression", "FetchReservation", "HTNG2008B", "HTNG" })
	public void fetchReservation_2008_1105() {
		try {

			String testName = "fetchReservation_2008_1105";
			WSClient.startTest(testName,
					"Verify that primary profile detail is being displayed for accompany reservation",
					"fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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

					String profileID2 = CreateProfile.createProfile("DS_01");
					String profileID3 = CreateProfile.createProfile("DS_01");

					if (!profileID2.equals("error")) {

						WSClient.setData("{var_profileId2}", profileID2);

						WSClient.setData("{var_profileId3}", profileID3);
						WSClient.setData("{var_profileId1}", profileID1);

						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID 2: " + profileID2 + "</b>");

						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						WSClient.setData("{var_profileId}", profileID1);

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_reservation_id}", resvID);

							WSClient.writeToReport(LogStatus.INFO, "c1");

							String query = WSClient.getQuery("HTNG2008BFetchReservation", "QS_13");
							LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
							temp = WSClient.getDBRow(query);

							WSClient.writeToReport(LogStatus.INFO, "c2");

							WSClient.setData("{var_firstName}", temp.get("FIRST"));
							WSClient.setData("{var_lastName}", temp.get("LAST"));

							WSClient.writeToReport(LogStatus.INFO, "c3");

							// Pre-requisite : Change Reservation to add
							// accompany guest

							String changeReservationReq = WSClient.createSOAPMessage("ChangeReservation", "DS_01");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							WSClient.writeToReport(LogStatus.INFO, "c4");

							if (WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success",
									true)) {

								HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

								String fetchResvReq = WSClient.createSOAPMessage("HTNG2008BFetchReservation", "DS_01");
								String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
								if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result",
										false)) {
									if (WSAssert.assertIfElementValueEquals(fetchResvRes,
											"FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										// text Element
										String query1 = WSClient.getQuery("QS_14");
										HashMap<String, String> dataMap = WSClient.getDBRow(query1);
										WSAssert.assertIfElementValueEquals(fetchResvRes,
												"FetchReservationResponse_ReservationData_ProfileID",
												dataMap.get("NAME_ID"), false);
										WSAssert.assertIfElementValueEquals(fetchResvRes,
												"FetchReservationResponse_ReservationData_ReservationID",
												dataMap.get("RESV_NAME_ID"), false);
										WSAssert.assertIfElementValueEquals(fetchResvRes,
												"FetchReservationResponse_ReservationData_ConfirmationNO",
												dataMap.get("CONFIRMATION_NO"), false);

									}

									if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
										String message = WSAssert.getElementValue(fetchResvRes,
												"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Error on the response is :" + message + "</b>");
									}
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to add accompany guest");
							}
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation for reservation 1
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
				if (!WSClient.getData("{var_resvId1}").equals(""))
					CancelReservation.cancelReservation("DS_03");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "targetedRegression", "FetchReservation", "HTNG2008B", "HTNG" })
	public void fetchReservation_2008_1094() {
		try {
			String testName = "fetchReservation_2008_1094";
			WSClient.startTest(testName,
					"Verify that multiple package code,description are fetched on the fetch reservation response.",
					"targetedRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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

				if (profileID == "")
					profileID = CreateProfile.createProfile("DS_01");

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

					resvId = CreateReservation.createReservation("DS_01").get("reservationId");

					if (!resvId.equals("error")) {

						WSClient.setData("{var_resvId}", resvId);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :" + resvId + "</b>");
						/******************
						 * Fetching Reservation
						 ************************/

						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());

						String fetchResvReq = WSClient.createSOAPMessage("HTNG2008BFetchReservation", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes,
									"FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								List<LinkedHashMap<String, String>> dataMap2 = WSClient
										.getDBRows(WSClient.getQuery("QS_15"));
								HashMap<String, String> xPath = new HashMap<String, String>();
								xPath.put("PackageElements_PackageElement_ElementCode",
										"ReservationData_PackageElements_PackageElement");
								xPath.put("PackageElements_PackageElement_ElementDescription",
										"ReservationData_PackageElements_PackageElement");
								List<LinkedHashMap<String, String>> actuals = WSClient.getMultipleNodeList(fetchResvRes,
										xPath, false, XMLType.RESPONSE);
								WSAssert.assertEquals(actuals, dataMap2, false);
							}
						} else
							WSClient.writeToReport(LogStatus.FAIL, "Fetch Reservation operation has failed");

						if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}

					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

		finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// Prefernces
	@Test(groups = { "targetedRegression", "FetchReservationExt", "HTNG2008B", "HTNG" })
	public void fetchReservation_2008_1097() {

		try {

			String testName = "fetchReservation_2008_1097";
			String[] preReqs = { "PreferenceCode", "PreferenceGroup", "RateCode", "RoomType", "MarketCode",
					"SourceCode" };
			WSClient.startTest(testName,
					"Verify that multiple  Preference type & preference value "
							+ "are displayed in FetchReservation response "
							+ "when passing ReservationID & ResortID in the FetchReservationRequest",
					"targetedRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_interface}", interfaceName);
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(preReqs)) {
				/*******************
				 * Create Profile
				 ***************************/
				String profileId = CreateProfile.createProfile("DS_01");
				if (!profileId.equals("error")) {
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

						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String fetchResvReq = WSClient.createSOAPMessage("HTNG2008BFetchReservation", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes,
									"FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

								HashMap<String, String> xpath = new HashMap<String, String>();
								List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

								WSClient.writeToReport(LogStatus.INFO,
										"Query to fetch reservation Lookup details from DataBase");
								String query = WSClient.getQuery("QS_12");
								db = WSClient.getDBRows(query);

								xpath.put("ReservationPreferences_Preference_Type",
										"ReservationData_ReservationPreferences_Preference");
								xpath.put("ReservationPreferences_Preference_Value",
										"ReservationData_ReservationPreferences_Preference");
								actualValues = WSClient.getMultipleNodeList(fetchResvRes, xpath, false,
										XMLType.RESPONSE);
								WSAssert.assertEquals(db, actualValues, false);
							}

							// text Element
							if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
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
