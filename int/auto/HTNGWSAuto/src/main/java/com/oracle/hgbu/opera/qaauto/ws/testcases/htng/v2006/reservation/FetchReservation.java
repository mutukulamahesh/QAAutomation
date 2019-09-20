package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2006.reservation;

import java.util.HashMap;
import java.util.LinkedHashMap;

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
	 * Id,Reservation status,source,Profile id for a given reservation.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 *
	 * @throws Exception
	 */
	@Test(groups = { "sanity", "FetchReservation", "HTNG2006", "HTNG" })
	public void fetchReservation_2006_20346() throws Exception {
		try {
			String testName = "fetchReservation_2006_20464";
			WSClient.startTest(testName, "Verify if the correct reservation Id, confirmation No,Reservation status, resort Id, Profile Id,Source are displayed on fetch reservation response.", "sanity");

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
				/*************
				 * Prerequisite 2: Fetch Room Type,Rate details,Market Code and
				 * Source Codes
				 ******************/
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
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

						WSClient.writeToReport(LogStatus.PASS, "Profile ID :" + profileID);
						WSClient.writeToReport(LogStatus.PASS, "Reservation ID :" + resvId);
						/******************
						 * Fetching Reservation
						 ************************/
						WSClient.setData("{var_resvId}", resvId);

						/******************
						 * Fetching Reservation
						 ************************/
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
						String fetchResvReq = WSClient.createSOAPMessage("HTNG2006FetchReservation", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query1 = WSClient.getQuery("QS_01");
								HashMap<String, String> dataMap = WSClient.getDBRow(query1);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ProfileID", dataMap.get("NAME_ID"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ResortId", HTNGLib.getExtResort(resortOperaValue, interfaceName), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ReservationID", dataMap.get("RESV_NAME_ID"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ConfirmationNO", dataMap.get("CONFIRMATION_NO"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_reservationStatus", dataMap.get("RESV_STATUS"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ReservationID_source", "OPERA", false);
							}
						}

						else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation ID is not created");
						}
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchReservation", "HTNG2006", "HTNG" })
	/**
	 * Method to check if the HTNG Fetch Reservation is working i.e., to check
	 * whether correct arrival and departure date is displayed on the fetch
	 * reservation response for a given reservation.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */

	public void fetchReservation_2006_20352() throws Exception {
		try {

			String testName = "fetchReservation_2006_20352";
			WSClient.startTest(testName, "Verify if  correct arrival and departure date are displayed on the fetch reservation response.", "minimumRegression");

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
				/*************
				 * Prerequisite 2: Fetch Room Type,Rate details,Market Code and
				 * Source Codes
				 ******************/
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
					WSClient.setData("{var_profileId}", profileID);
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
						WSClient.writeToReport(LogStatus.PASS, "Profile ID :" + profileID);
						WSClient.writeToReport(LogStatus.PASS, "Reservation ID :" + resvId);
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
						/*************
						 * Fetching Reservation
						 ********************/
						String fetchResvReq = WSClient.createSOAPMessage("HTNG2006FetchReservation", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								/***********
								 * Validating the dates against the database
								 ************/
								String query = WSClient.getQuery("QS_01");
								HashMap<String, String> DBList = WSClient.getDBRow(query);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ResortId", HTNGLib.getExtResort(resortOperaValue, interfaceName), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ReservationID", DBList.get("RESV_NAME_ID"), false);

								String DBArrival = DBList.get("BEGIN_DATE");
								DBArrival = DBArrival.replace(' ', 'T');
								DBArrival = DBArrival.substring(0, DBArrival.indexOf('.'));

								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ArrivalDate", DBArrival, false);
								String DBDeparture = DBList.get("END_DATE");
								DBDeparture = DBDeparture.replace(' ', 'T');
								DBDeparture = DBDeparture.substring(0, DBDeparture.indexOf('.'));
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_DepartureDate", DBDeparture, false);
							}
						} else
							WSClient.writeToReport(LogStatus.FAIL, "Fetch Reservation operation has failed");

						if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchReservation", "HTNG2006", "HTNG" })
	/**
	 * Method to check if the HTNG Fetch Reservation is working i.e., fetching
	 * correct room number,room rate and room type when a room is assigned to
	 * reservation.
	 *
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	public void fetchReservation_2006_20357() throws Exception {
		try {
			String testName = "fetchReservation_2006_20357";
			WSClient.startTest(testName, "Verify if the correct room number, room rate, and room type are displayed on fetch reservation response.", "minimumRegression");
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
				/*************
				 * Prerequisite 2: Fetch Room Type,Rate details,Market Code and
				 * Source Codes
				 ******************/
				WSClient.setData("{var_profileId}", profileID);
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					/******************
					 * Prerequisite 3:Create a Reservation
					 ************************/

					resvId = CreateReservation.createReservation("DS_01").get("reservationId");
					WSClient.setData("{var_resvId}", resvId);

					if (!resvId.equals("error")) {
						WSClient.writeToReport(LogStatus.PASS, "Profile ID :" + profileID);
						WSClient.writeToReport(LogStatus.PASS, "Reservation ID :" + resvId);
						/******************
						 * Fetching Reservation
						 ************************/

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
							if (roomNo != null && roomNo != "") {
								/******************
								 * Fetching Reservation
								 ************************/
								String fetchResvReq = WSClient.createSOAPMessage("HTNG2006FetchReservation", "DS_01");
								String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
								if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
									if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										HashMap<String, String> dataMap4 = WSClient.getDBRow(WSClient.getQuery("QS_01"));
										System.out.println("******************************************************\n\n\n\n");
										System.out.println(dataMap4);
										System.out.println("\n\n\n\n******************************************************");
										WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ProfileID", dataMap4.get("NAME_ID"), false);
										WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ResortId", resortExtValue, false);
										WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ReservationID", dataMap4.get("RESV_NAME_ID"), false);
										WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ConfirmationNO", dataMap4.get("CONFIRMATION_NO"), false);
										WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_reservationStatus", dataMap4.get("RESV_STATUS"), false);
										HashMap<String, String> dataMap5 = WSClient.getDBRow(WSClient.getQuery("QS_05"));
										WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ShortRateCode", dataMap5.get("RATE_CODE"), false);
										WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_RoomNumber", dataMap5.get("ROOM"), false);
										HashMap<String, String> dataMap6 = WSClient.getDBRow(WSClient.getQuery("QS_06"));
										WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ShortRoomType", dataMap6.get("LABEL"), false);
									}
								} else
									WSClient.writeToReport(LogStatus.FAIL, "Fetch Reservation operation has failed");

								if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {

									/****
									 * Verifying that the error message is
									 * populated on the response
									 ********/

									String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> No room Number Assigned");
							}

						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> AssignRoom Failed");
						}
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchReservation", "HTNG2006", "HTNG" })
	/**
	 * Method to check if the HTNG Fetch Reservation is working i.e.,to check
	 * whether correct package details and package description are displayed on
	 * the fetch reservation response for a given reservation.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 *
	 * @throws Exception
	 */
	public void fetchReservation_2006_20358() throws Exception {
		try {
			String testName = "fetchReservation_2006_20358";
			WSClient.startTest(testName, "Verify if correct package code,package description are displayed on fetch reservation response.", "minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_resort}", resortOperaValue);
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
				/*************
				 * Prerequisite 2: Fetch Room Type,Rate details,Market Code and
				 * Source Codes
				 ******************/
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					/******************
					 * Prerequisite 3:Create a Reservation
					 ************************/

					resvId = CreateReservation.createReservation("DS_01").get("reservationId");
					WSClient.setData("{var_resvId}", resvId);

					if (!resvId.equals("error")) {
						WSClient.writeToReport(LogStatus.PASS, "Profile ID :" + profileID);
						WSClient.writeToReport(LogStatus.PASS, "Reservation ID :" + resvId);
						/******************
						 * Fetching Reservation
						 ************************/

						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

						String fetchResvReq = WSClient.createSOAPMessage("HTNG2006FetchReservation", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								HashMap<String, String> dataMap = WSClient.getDBRow(WSClient.getQuery("QS_01"));
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ProfileID", dataMap.get("NAME_ID"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ResortId", resortExtValue, false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ReservationID", dataMap.get("RESV_NAME_ID"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ConfirmationNO", dataMap.get("CONFIRMATION_NO"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_reservationStatus", dataMap.get("RESV_STATUS"), false);
								String packageId = WSClient.getElementValue(fetchResvRes, "PackageElements_PackageElement_ElementCode", XMLType.RESPONSE);
								WSClient.setData("{var_pkgId}", packageId);
								HashMap<String, String> dataMap2 = WSClient.getDBRow(WSClient.getQuery("QS_03"));
								WSAssert.assertIfElementValueEquals(fetchResvRes, "PackageElements_PackageElement_ElementCode", dataMap2.get("PRODUCT"), false);
								WSAssert.assertIfElementValueEquals(fetchResvRes, "PackageElements_PackageElement_ElementDescription", dataMap2.get("DESCRIPTION"), false);
							}
						} else
							WSClient.writeToReport(LogStatus.FAIL, "Fetch Reservation operation has failed");

						// Text Message
						if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
							String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
						}

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// Invalid Resvervation Id
	@Test(groups = { "fullRegression", "FetchReservation", "HTNG2006", "HTNG" })
	public void fetchReservation_2006_20364() {

		try {
			String testName = "fetchReservation_2006_20364";
			WSClient.startTest(testName, "Verify Error message when Invalid  Reservation ID is passed.", "fullRegression");

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
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
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

						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());

						String fetchResvReq = WSClient.createSOAPMessage("HTNG2006FetchReservation", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_Result_resultStatusFlag", "FAIL", false)) {

							}
							// Text Message
							if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is : </b>" + message + "</b>");
							}
						}

						else {
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation ID is not created");
						}
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Prerequisite required for Reservation are not created");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	//	@Test(groups = { "minimumRegression", "FetchReservation", "HTNG2006", "HTNG" })
	//
	//	public void fetchReservation_2006_profiledet() throws Exception {
	//		try {
	//			String testName = "fetchReservation_2006_4530";
	//			WSClient.startTest(testName, "Verify that profile details are correctly fetched in the fetchreservation ", "minimumRegression");
	//
	//			String interfaceName = HTNGLib.getHTNGInterface();
	//			String resortOperaValue = OPERALib.getResort();
	//			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
	//			String chain = OPERALib.getChain();
	//			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
	//			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
	//
	//			WSClient.setData("{var_profileSource}", interfaceName);
	//
	//			WSClient.setData("{var_resort}", resortOperaValue);
	//			WSClient.setData("{var_chain}", chain);
	//			WSClient.setData("{var_fname}", fname);
	//			WSClient.setData("{var_lname}", lname);
	//			WSClient.setData("{var_extResort}", resortExtValue);
	//
	//			String uname = OPERALib.getUserName();
	//
	//			OPERALib.setOperaHeader(uname);
	//
	//			/************
	//			 * Prerequisite 1: Create profile
	//			 *********************************/
	//			WSClient.setData("{var_gender}", "F");
	//			WSClient.setData("{var_nationality}", OperaPropConfig.getDataSetForCode("Nationality", "DS_02"));
	//			WSClient.setData("{var_nameTitle}", OperaPropConfig.getDataSetForCode("Title", "DS_01"));
	//			WSClient.setData("{var_vipCode}", OperaPropConfig.getDataSetForCode("VipLevel", "DS_01"));
	//			WSClient.setData("{var_birthDate}", "1995-11-09");
	//			String profileID = CreateProfile.createProfile("DS_29");
	//
	//			if (!profileID.equals("error")) {
	//				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + profileID + "</b>");
	//
	//				/*************
	//				 * Prerequisite 2: Fetch Room Type,Rate details,Market Code and
	//				 * Source Codes
	//				 ******************/
	//				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
	//					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	//					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	//					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	//					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	//
	//					/******************
	//					 * Prerequisite 3:Create a Reservation
	//					 ************************/
	//					WSClient.setData("{var_profileId}", profileID);
	//					resvId = CreateReservation.createReservation("DS_01").get("reservationId");
	//
	//					if (!resvId.equals("error")) {
	//
	//						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :" + resvId + "</b>");
	//						/******************
	//						 * Fetching Reservation
	//						 ************************/
	//						WSClient.setData("{var_resvId}", resvId);
	//
	//						/******************
	//						 * Fetching Reservation
	//						 ************************/
	//						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
	//						String fetchResvReq = WSClient.createSOAPMessage("HTNG2006FetchReservation", "DS_01");
	//						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
	//						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
	//							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {
	//
	//								LinkedHashMap<String, String> actuals = new LinkedHashMap<String, String>();
	//								actuals.put("NAME_ID", WSClient.getElementValue(fetchResvRes, "FetchReservationResponse_ReservationData_ProfileID", XMLType.RESPONSE));
	//								actuals.put("TITLE", WSClient.getElementValue(fetchResvRes, "ReservationData_ProfileInfo_NameTitle", XMLType.RESPONSE));
	//								actuals.put("FIRST", WSClient.getElementValue(fetchResvRes, "ReservationData_ProfileInfo_FirstName", XMLType.RESPONSE));
	//								actuals.put("MIDDLE", WSClient.getElementValue(fetchResvRes, "ReservationData_ProfileInfo_MiddleName", XMLType.RESPONSE));
	//								actuals.put("LAST", WSClient.getElementValue(fetchResvRes, "ReservationData_ProfileInfo_LastName", XMLType.RESPONSE));
	//								LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_07"));
	//
	//								WSAssert.assertEquals(db, actuals, false);
	//							}
	//						} else
	//							WSClient.writeToReport(LogStatus.FAIL, "Fetch Reservation operation has failed");
	//
	//						if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
	//
	//							/****
	//							 * Verifying that the error message is populated on
	//							 * the response
	//							 ********/
	//
	//							String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
	//							WSClient.writeToReport(LogStatus.INFO, "The text displayed in the response is :" + message);
	//						}
	//
	//					}
	//
	//				} else {
	//					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Prerequisite required for Reservation are not created");
	//				}
	//			}
	//
	//		} catch (Exception e) {
	//			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	//		} finally {
	//			try {
	//				if (!WSClient.getData("{var_resvId}").equals(""))
	//					CancelReservation.cancelReservation("DS_02");
	//			} catch (Exception e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//		}
	//	}

	@Test(groups = { "fullRegression", "FetchReservation", "HTNG2006", "HTNG" })
	public void fetchReservation_2006_42497() {
		String[] preReqs = { "RateCode", "RoomType", "MarketCode", "SourceCode" };
		try {
			String testName = "fetchReservation_2006_42497";
			WSClient.startTest(testName, "Verify that an error Message is obtained when no reservation ID is passed in the request", "fullRegression");
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
					 * Fetching Reservation
					 ************************/
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String fetchResvReq = WSClient.createSOAPMessage("HTNG2006FetchReservation", "DS_02");
					String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
					if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
						if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_Result_resultStatusFlag", "FAIL", false)) {
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
		}
	}

	/**
	 * Method to check if the HTNG Fetch Reservation is working i.e.,Verify that
	 * error message is populated when opera hotel code is sent on the response.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 *
	 * @throws Exception
	 */
	/*@Test(groups = { "fullRegression", "FetchReservation", "HTNG2006", "HTNG","fetchReservation_2006_42496" })

	public void fetchReservation_2006_42496() throws Exception {
		String[] preReqs = { "RateCode", "RoomType", "MarketCode", "SourceCode" };
		try {

			String testName = "fetchReservation_2006_42496";
			WSClient.startTest(testName, "Verify that error message is populated when opera hotel code is sent on the response.", "fullRegression");

			if (OperaPropConfig.getPropertyConfigResults(preReqs)) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();

				OPERALib.setOperaHeader(uname);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);

	 *//*******************************
	 * Pre Requisite : 1 create Profile
	 *************//*
				String profileId = CreateProfile.createProfile("DS_01");

				if (!profileId.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileId + "</b>");
	  *//*************
	  * Prerequisite 2: Fetch Room Type,Rate details,Market Code
	  * and Source Codes
	  ******************//*
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

	   *//******************
	   * Prerequisite 3:Create a Reservation
	   ************************//*
					String resvId = CreateReservation.createReservation("DS_01").get("reservationId");
					WSClient.setData("{var_resvId}", resvId);
					WSClient.setData("{var_extResort}", "");
					if (!resvId.equals("error")) {

	    *//******************
	    * Fetching Reservation
	    ************************//*
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

						String fetchResvReq = WSClient.createSOAPMessage("HTNG2006FetchReservation", "DS_01");
						String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
						if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_Result_resultStatusFlag", "FAIL", false)) {

								// text Element
								if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>Error on the response is :" + message + "</b>");
								}
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

	}*/


	@Test(groups = { "fullRegression", "FetchReservation", "HTNG2006" , "HTNG"})

	public void fetchReservation_2006_20385() {
		try {

			String testName = "fetchReservation_2006_20385";
			WSClient.startTest(testName, "Verify that primary profile detail is being displayed for shared reservation","fullRegression");
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
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID1+"</b>");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					// Prerequisite 2 - Create Reservation


					HashMap<String,String> resv=CreateReservation.createReservation("DS_04");
					String resvID = resv.get("reservationId");

					if(!resvID.equals("error"))
					{

						WSClient.setData("{var_resvId}", resvID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");

						String profileID2 = CreateProfile.createProfile("DS_01");
						if(!profileID2.equals("error")){
							WSClient.setData("{var_profileId}", profileID2);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID2+"</b>");

							HashMap<String,String> resv1=CreateReservation.createReservation("DS_04");
							String resvID1 = resv1.get("reservationId");

							if(!resvID1.equals("error"))
							{

								WSClient.setData("{var_resvId1}", resvID1);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID1+"</b>");


								//Prerequisite 4 : combining Reservation

								String combineReq = WSClient.createSOAPMessage("CombineShareReservations", "DS_01");
								String combineRes = WSClient.processSOAPMessage(combineReq);

								if(WSAssert.assertIfElementExists(combineRes, "CombineShareReservationsRS_Success", true)){

									String combineResID= WSClient.getElementValue(combineRes,"Reservation_ReservationIDList_UniqueID_ID", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully combined the reservation.</b>");
									WSClient.setData("{var_resvId}", combineResID);
									WSClient.setData("{var_resvDate}",WSClient.getDBRow(WSClient.getQuery("QS_01")).get("BEGIN_DATE"));



									HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

									String fetchResvReq = WSClient.createSOAPMessage("HTNG2006FetchReservation", "DS_01");
									String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
									if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
										if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {

											String query1 = WSClient.getQuery("QS_09");
											HashMap<String, String> dataMap = WSClient.getDBRow(query1);
											WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ProfileID", dataMap.get("NAME_ID"), false);
											WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ReservationID", dataMap.get("RESV_NAME_ID"), false);
											WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ConfirmationNO", dataMap.get("CONFIRMATION_NO"), false);


										}

										if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
											String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>Error on the response is :" + message + "</b>");
										}
									}

								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to combine reservation");
								}
							}
						}
					}
				}
			}

		}


		catch(Exception e)
		{
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		finally
		{
			// Cancel Reservation for reservation 1
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
				if(!WSClient.getData("{var_resvId1}").equals(""))
					CancelReservation.cancelReservation("DS_03");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
	}

	@Test(groups = { "fullRegression", "FetchReservation", "HTNG2006" , "HTNG"})

	public void fetchReservation_2006_20387() {
		try {

			String testName = "fetchReservation_2006_20387";
			WSClient.startTest(testName, "Verify that primary profile detail is being displayed for accompany reservation","fullRegression");
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

					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID 1 : "+profileID1+"</b>");

					String profileID2=CreateProfile.createProfile("DS_01");

					if (!profileID2.equals("error")) {

						WSClient.setData("{var_profileId2}", profileID2);

						WSClient.setData("{var_profileId1}",profileID1);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID 2: "+profileID2+"</b>");

						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));


						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_reservation_id}", resvID);

							String query = WSClient.getQuery("HTNG2006FetchReservation","QS_08");
							LinkedHashMap<String,String> temp = new LinkedHashMap<String,String>();
							temp=WSClient.getDBRow(query);

							WSClient.setData("{var_firstName}", temp.get("FIRST"));
							WSClient.setData("{var_lastName}", temp.get("LAST"));


							// Pre-requisite : Change Reservation to add accompany guest

							String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_01");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);


							if(WSAssert.assertIfElementExists(changeReservationRes, "ChangeReservationRS_Success", true)){

								HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

								String fetchResvReq = WSClient.createSOAPMessage("HTNG2006FetchReservation", "DS_01");
								String fetchResvRes = WSClient.processSOAPMessage(fetchResvReq);
								if (WSAssert.assertIfElementExists(fetchResvRes, "FetchReservationResponse_Result", false)) {
									if (WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										// text Element
										String query1 = WSClient.getQuery("QS_09");
										HashMap<String, String> dataMap = WSClient.getDBRow(query1);
										WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ProfileID", dataMap.get("NAME_ID"), false);
										WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ReservationID", dataMap.get("RESV_NAME_ID"), false);
										WSAssert.assertIfElementValueEquals(fetchResvRes, "FetchReservationResponse_ReservationData_ConfirmationNO", dataMap.get("CONFIRMATION_NO"), false);

									}

									if (WSAssert.assertIfElementExists(fetchResvRes, "Result_Text_TextElement", true)) {
										String message = WSAssert.getElementValue(fetchResvRes, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>Error on the response is :" + message + "</b>");
									}
								}

							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to add accompany guest");
							}
						}
					}
				}
			}

		}


		catch(Exception e)
		{
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		finally
		{
			// Cancel Reservation for reservation 1
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
				if(!WSClient.getData("{var_resvId1}").equals(""))
					CancelReservation.cancelReservation("DS_03");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
	}

}
