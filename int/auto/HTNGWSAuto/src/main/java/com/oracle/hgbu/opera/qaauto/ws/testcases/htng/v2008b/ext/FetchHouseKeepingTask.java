package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2008b.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CheckoutReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateRoom;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.SetHousekeepingRoomStatus;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchHouseKeepingTask extends WSSetUp {

	@Test(groups = { "sanity", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_4521() {

		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_4521";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated for scheduled taskcode through the FetchHousekeepingTask call for all the rooms with the requested task code.",
					"sanity");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "", parameter3 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {

						WSClient.writeToReport(LogStatus.INFO,
								"<b>****Setting the parameter :  ADDITIONAL_TASK_ASSIGNMENTS as null****</b>");
						WSClient.setData("{var_parameter}", "ADDITIONAL_TASK_ASSIGNMENTS");
						WSClient.setData("{var_settingValue}", "");

						if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals(""))
							parameter3 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
						if (parameter3.equals("error")) {

							if (profileID.equals(""))
								profileID = CreateProfile.createProfile("DS_01");
							if (!profileID.equals("error")) {
								WSClient.setData("{var_profileId}", profileID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
								WSClient.setData("{VAR_RATEPLANCODE}",
										OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
								WSClient.setData("{VAR_ROOMTYPE}",
										OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
								WSClient.setData("{var_roomType}",
										OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
								WSClient.setData("{var_RoomType}",
										OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
								WSClient.setData("{var_sourceCode}",
										OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
								WSClient.setData("{VAR_MARKETCODE}",
										OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
								WSClient.setData("{var_payment}",
										OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

								// Prerequisite 2 Create Reservation
								HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
								String resvID = resv.get("reservationId");

								if (!resvID.equals("error")) {

									WSClient.setData("{var_resvId}", resvID);
									WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
									// Prerequisite 3: Fetching available Hotel
									// rooms with
									// room type

									String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
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
									if (!roomNumber.equals("")) {
										WSClient.setData("{var_roomNumber}", roomNumber);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully fetched/create room.</b>");
										// Prerequisite 5: Changing the room
										// status
										// to inspected
										// to assign the room for checking in

										String setHousekeepingRoomStatusReq = WSClient
												.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
										String setHousekeepingRoomStatusRes = WSClient
												.processSOAPMessage(setHousekeepingRoomStatusReq);

										if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
												"SetHousekeepingRoomStatusRS_Success", true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Succesfully changed the status of room.</b>");

											// Prerequisite 6: Assign Room

											String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
											String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

											if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
													true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully assigned room.</b>");
												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_01");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1", WSClient
																.getDBRow(WSClient.getQuery("QS_08")).get("COUNT"));

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_05");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Unable to change status of room ");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3170() {
		String profileID = "", roomNumber = "";

		try {

			String testName = "fetchHousekeepingTask_Ext_3170";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated for task whose departure task = Y and is not scheduled through the FetchHousekeepingTask call for all the rooms with the requested task code.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_12");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											WSClient.setData("{var_taskCode}",
													OperaPropConfig.getDataSetForCode("TaskCode", "DS_01"));
											HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
													HTNGLib.getInterfaceFromAddress());
											String fetchHKTaskReq = WSClient
													.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_01");
											String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

											if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
													"FetchHouseKeepingTaskResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {

												/**
												 * Verifying that the Tasksheet
												 * record is inserted
												 */
												// LinkedHashMap<String,String>
												// count=new
												// LinkedHashMap<String,String>();

												/**
												 * Validating the
												 * TaskDate,TaskCode,
												 * TaskSheetNumber,
												 * TotalRoomsInSheet,
												 * TaskInstruction details
												 * obtained from the response
												 * against the database details
												 */
												List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath2 = new HashMap<String, String>();
												xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												String query = WSClient.getQuery("QS_04");
												db2 = WSClient.getDBRows(query);
												actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
														false, XMLType.RESPONSE);
												for (int i = 0; i < actualValues2.size(); ++i) {

													LinkedHashMap<String, String> exp = db2.get(i);

													exp.put("TotalRoomsInSheet1",
															WSClient.getDBRows(WSClient.getQuery("QS_06")).size() + "");

												}
												WSAssert.assertEquals(actualValues2, db2, false);

												/**
												 * Checking if the Room Numbers
												 * displayed in the response
												 * message under each of the
												 * Task Sheet Details section
												 * are correctly matching with
												 * the room numbers resulting
												 * from the response of the
												 * database
												 */

												List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath3 = new HashMap<String, String>();
												xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
												String query1 = WSClient.getQuery("QS_06");
												db3 = WSClient.getDBRows(query1);
												actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
														false, XMLType.RESPONSE);
												WSAssert.assertEquals(actualValues3, db3, false);

											}

											if (WSAssert.assertIfElementExists(fetchHKTaskRes,
													"Result_Text_TextElement", true)) {
												String message = WSClient.getElementValue(fetchHKTaskRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The text displayed in the response is    :     "
																+ message + "</b>");

											}

										}

										else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
			if (!WSClient.getData("{var_resvId}").equals(""))
				try {
					CancelReservation.cancelReservation("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3172() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3172";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated for task whose departure task = Y and is scheduled through the FetchHousekeepingTask call for all the rooms with the requested task code.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				/*** Change departure task as y ***/
				WSClient.setData("{var_taskCode}", OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));

				String changeHKTaskReq = WSClient.createSOAPMessage("ChangeHousekeepingTasks", "DS_02");
				String changeHKTaskRes = WSClient.processSOAPMessage(changeHKTaskReq);
				if (WSAssert.assertIfElementExists(changeHKTaskRes, "ChangeHousekeepingTasksRS_Success", true)) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Successfully changed the task as default departure = Y</b>");
					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
					WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter1.equals("error")) {

						WSClient.writeToReport(LogStatus.INFO,
								"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
						WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
						WSClient.setData("{var_settingValue}", "Y");

						if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
							parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

						if (!parameter2.equals("error")) {

							if (profileID.equals(""))
								profileID = CreateProfile.createProfile("DS_01");
							if (!profileID.equals("error")) {
								WSClient.setData("{var_profileId}", profileID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
								WSClient.setData("{VAR_RATEPLANCODE}",
										OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
								WSClient.setData("{VAR_ROOMTYPE}",
										OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
								WSClient.setData("{var_roomType}",
										OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
								WSClient.setData("{var_RoomType}",
										OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
								WSClient.setData("{var_sourceCode}",
										OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
								WSClient.setData("{VAR_MARKETCODE}",
										OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
								WSClient.setData("{var_payment}",
										OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

								// Prerequisite 2 Create Reservation
								HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
								String resvID = resv.get("reservationId");

								if (!resvID.equals("error")) {

									WSClient.setData("{var_resvId}", resvID);
									WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
									// Prerequisite 3: Fetching available Hotel
									// rooms with
									// room type

									String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
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
									if (!roomNumber.equals("")) {
										WSClient.setData("{var_roomNumber}", roomNumber);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully fetched/create room.</b>");
										// Prerequisite 5: Changing the room
										// status to inspected
										// to assign the room for checking in

										String setHousekeepingRoomStatusReq = WSClient
												.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
										String setHousekeepingRoomStatusRes = WSClient
												.processSOAPMessage(setHousekeepingRoomStatusReq);

										if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
												"SetHousekeepingRoomStatusRS_Success", true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Succesfully changed the status of room.</b>");

											// Prerequisite 6: Assign Room

											String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
											String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

											if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
													true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully assigned room.</b>");
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_01");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);
													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);
													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1",
																WSClient.getDBRows(WSClient.getQuery("QS_07")).size()
																+ "");

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_07");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Unable to change status of room ");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
									}
								}

							}
						}
					}
				}

				else {
					WSClient.writeToReport(LogStatus.WARNING, "Unable to change task as default departure");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();

			/*** Change departure task as y ***/
			WSClient.setData("{var_taskCode}", OperaPropConfig.getDataSetForCode("TaskCode", "DS_01"));
			try {
				String changeHKTaskReq1 = WSClient.createSOAPMessage("ChangeHousekeepingTasks", "DS_01");
				String changeHKTaskRes1 = WSClient.processSOAPMessage(changeHKTaskReq1);
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "targetedRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3145() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3145";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated for multiple attendant through the FetchHousekeepingTask call for all the rooms with the requested task code.",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_12");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											WSClient.setData("{var_taskCode}",
													OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
											HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
													HTNGLib.getInterfaceFromAddress());
											String fetchHKTaskReq = WSClient
													.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_02");
											String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

											if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
													"FetchHouseKeepingTaskResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {
												String a = WSClient.getElementValue(fetchHKTaskReq,
														"FetchHouseKeepingTaskRequest_NoOfAttendants", XMLType.REQUEST);
												String tr = WSClient.getDBRow(WSClient.getQuery("QS_08")).get("COUNT");
												int attendant = Integer.parseInt(a);
												int tot_room = Integer.parseInt(tr);
												int r = tot_room / attendant;
												int extra = tot_room % attendant;
												int room = r;

												List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath2 = new HashMap<String, String>();
												xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
														"FetchHouseKeepingTaskResponse_TaskSheets");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
														"FetchHouseKeepingTaskResponse_TaskSheets");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskInstruction",
														"FetchHouseKeepingTaskResponse_TaskSheets");
												xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
														"FetchHouseKeepingTaskResponse_TaskSheets");

												String query = WSClient.getQuery("QS_26");
												db2 = WSClient.getDBRows(query);
												actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
														true, XMLType.RESPONSE);
												System.out.println(actualValues2 + "");

												for (int i = 0; i < actualValues2.size(); ++i) {

													WSClient.writeToReport(LogStatus.INFO,
															"<b>Validating TaskSheet Header<b>");
													LinkedHashMap<String, String> exp = db2.get(i);
													if (extra > 0) {
														r = r + 1;
														exp.put("TotalRoomsInSheet1", r + "");
														r = room;
														--extra;
													} else {
														exp.put("TotalRoomsInSheet1", r + "");
													}

													WSAssert.assertEquals(exp, actualValues2.get(i), false);
												}

											}

											if (WSAssert.assertIfElementExists(fetchHKTaskRes,
													"Result_Text_TextElement", true)) {
												String message = WSClient.getElementValue(fetchHKTaskRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The text displayed in the response is    :     "
																+ message + "</b>");

											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3125() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3125";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated for dirty rooms through the FetchHousekeepingTask call for all the rooms with the requested task code.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "Dirty");
				WSClient.setData("{var_roomStatus}", "DI");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String setHousekeepingRoomStatusReq1 = WSClient
													.createSOAPMessage("SetHousekeepingRoomStatus", "DS_06");
											String setHousekeepingRoomStatusRes1 = WSClient
													.processSOAPMessage(setHousekeepingRoomStatusReq1);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
													"SetHousekeepingRoomStatusRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully changed the status of room.</b>");
												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_03");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1", WSClient
																.getDBRow(WSClient.getQuery("QS_12")).get("COUNT"));

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_11");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to change status of room ");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3132() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3132";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated for vacant rooms through the FetchHousekeepingTask call for all the rooms with the requested task code.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_fStatus}", "Vacant");
				WSClient.setData("{var_foStatus}", "VAC");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											WSClient.setData("{var_taskCode}",
													OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
											HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
													HTNGLib.getInterfaceFromAddress());
											String fetchHKTaskReq = WSClient
													.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_04");
											String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

											if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
													"FetchHouseKeepingTaskResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {

												/**
												 * Verifying that the Tasksheet
												 * record is inserted
												 */
												// LinkedHashMap<String,String>
												// count=new
												// LinkedHashMap<String,String>();

												/**
												 * Validating the
												 * TaskDate,TaskCode,
												 * TaskSheetNumber,
												 * TotalRoomsInSheet,
												 * TaskInstruction details
												 * obtained from the response
												 * against the database details
												 */
												List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath2 = new HashMap<String, String>();
												xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												String query = WSClient.getQuery("QS_04");
												db2 = WSClient.getDBRows(query);

												actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
														false, XMLType.RESPONSE);

												for (int i = 0; i < actualValues2.size(); ++i) {

													LinkedHashMap<String, String> exp = db2.get(i);

													exp.put("TotalRoomsInSheet1",
															WSClient.getDBRow(WSClient.getQuery("QS_14")).get("COUNT"));

												}
												WSAssert.assertEquals(actualValues2, db2, false);

												/**
												 * Checking if the Room Numbers
												 * displayed in the response
												 * message under each of the
												 * Task Sheet Details section
												 * are correctly matching with
												 * the room numbers resulting
												 * from the response of the
												 * database
												 */

												List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath3 = new HashMap<String, String>();
												xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
												String query1 = WSClient.getQuery("QS_13");
												db3 = WSClient.getDBRows(query1);
												actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
														false, XMLType.RESPONSE);
												WSAssert.assertEquals(actualValues3, db3, false);

											}

											if (WSAssert.assertIfElementExists(fetchHKTaskRes,
													"Result_Text_TextElement", true)) {
												String message = WSClient.getElementValue(fetchHKTaskRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The text displayed in the response is    :     "
																+ message + "</b>");

											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3131() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3131";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated for occupied rooms through the FetchHousekeepingTask call for all the rooms with the requested task code.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_fStatus}", "Occupied");
				WSClient.setData("{var_foStatus}", "OCC");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");

											String checkInReq = WSClient.createSOAPMessage("CheckinReservation",
													"DS_01");
											String checkInRes = WSClient.processSOAPMessage(checkInReq);

											if (WSAssert.assertIfElementExists(checkInRes,
													"CheckinReservationRS_Success", true)) {

												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully checked in the reservation.</b>");

												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_04");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1", WSClient
																.getDBRow(WSClient.getQuery("QS_14")).get("COUNT"));

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_13");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to checkin the reservation ");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
								}

							}
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3134() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3134";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call for the rooms which are allocated to the Arrived Reservations with the requested task code.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_resvStatus}", "Arrived");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");

											String checkInReq = WSClient.createSOAPMessage("CheckinReservation",
													"DS_01");
											String checkInRes = WSClient.processSOAPMessage(checkInReq);

											if (WSAssert.assertIfElementExists(checkInRes,
													"CheckinReservationRS_Success", true)) {

												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully checked in the reservation.</b>");

												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_05");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1",
																WSClient.getDBRows(WSClient.getQuery("QS_15")).size()
																+ "");

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_15");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to change status of room ");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
								}

							}
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3135() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3135";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call for the rooms which are allocated to the Arrival reservations with the requested task code.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_resvStatus}", "Arrival");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");

											WSClient.setData("{var_taskCode}",
													OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
											HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
													HTNGLib.getInterfaceFromAddress());
											String fetchHKTaskReq = WSClient
													.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_05");
											String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

											if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
													"FetchHouseKeepingTaskResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {

												/**
												 * Verifying that the Tasksheet
												 * record is inserted
												 */
												// LinkedHashMap<String,String>
												// count=new
												// LinkedHashMap<String,String>();

												/**
												 * Validating the
												 * TaskDate,TaskCode,
												 * TaskSheetNumber,
												 * TotalRoomsInSheet,
												 * TaskInstruction details
												 * obtained from the response
												 * against the database details
												 */
												List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath2 = new HashMap<String, String>();
												xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												String query = WSClient.getQuery("QS_04");
												db2 = WSClient.getDBRows(query);

												actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
														false, XMLType.RESPONSE);

												for (int i = 0; i < actualValues2.size(); ++i) {

													LinkedHashMap<String, String> exp = db2.get(i);

													exp.put("TotalRoomsInSheet1",
															WSClient.getDBRows(WSClient.getQuery("QS_16")).size() + "");

												}
												WSAssert.assertEquals(actualValues2, db2, false);

												/**
												 * Checking if the Room Numbers
												 * displayed in the response
												 * message under each of the
												 * Task Sheet Details section
												 * are correctly matching with
												 * the room numbers resulting
												 * from the response of the
												 * database
												 */

												List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath3 = new HashMap<String, String>();
												xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
												String query1 = WSClient.getQuery("QS_16");
												db3 = WSClient.getDBRows(query1);
												actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
														false, XMLType.RESPONSE);
												WSAssert.assertEquals(actualValues3, db3, false);

											}

											if (WSAssert.assertIfElementExists(fetchHKTaskRes,
													"Result_Text_TextElement", true)) {
												String message = WSClient.getElementValue(fetchHKTaskRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The text displayed in the response is    :     "
																+ message + "</b>");

											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3126() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3126";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated for clean rooms through the FetchHousekeepingTask call for all the rooms with the requested task code.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "Clean");
				WSClient.setData("{var_roomStatus}", "CL");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String setHousekeepingRoomStatusReq1 = WSClient
													.createSOAPMessage("SetHousekeepingRoomStatus", "DS_02");
											String setHousekeepingRoomStatusRes1 = WSClient
													.processSOAPMessage(setHousekeepingRoomStatusReq1);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
													"SetHousekeepingRoomStatusRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully changed the status of room.</b>");

												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_03");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1", WSClient
																.getDBRow(WSClient.getQuery("QS_12")).get("COUNT"));

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_11");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to change status of room ");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3127() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3127";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated for Inspected rooms through the FetchHousekeepingTask call for all the rooms with the requested task code.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "Inspected");
				WSClient.setData("{var_roomStatus}", "IP");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");

											WSClient.setData("{var_taskCode}",
													OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
											HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
													HTNGLib.getInterfaceFromAddress());
											String fetchHKTaskReq = WSClient
													.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_03");
											String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

											if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
													"FetchHouseKeepingTaskResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {

												/**
												 * Verifying that the Tasksheet
												 * record is inserted
												 */
												// LinkedHashMap<String,String>
												// count=new
												// LinkedHashMap<String,String>();

												/**
												 * Validating the
												 * TaskDate,TaskCode,
												 * TaskSheetNumber,
												 * TotalRoomsInSheet,
												 * TaskInstruction details
												 * obtained from the response
												 * against the database details
												 */
												List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath2 = new HashMap<String, String>();
												xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												String query = WSClient.getQuery("QS_04");
												db2 = WSClient.getDBRows(query);

												actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
														false, XMLType.RESPONSE);

												for (int i = 0; i < actualValues2.size(); ++i) {

													LinkedHashMap<String, String> exp = db2.get(i);

													exp.put("TotalRoomsInSheet1",
															WSClient.getDBRow(WSClient.getQuery("QS_12")).get("COUNT"));

												}
												WSAssert.assertEquals(actualValues2, db2, false);

												/**
												 * Checking if the Room Numbers
												 * displayed in the response
												 * message under each of the
												 * Task Sheet Details section
												 * are correctly matching with
												 * the room numbers resulting
												 * from the response of the
												 * database
												 */

												List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath3 = new HashMap<String, String>();
												xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
												String query1 = WSClient.getQuery("QS_11");
												db3 = WSClient.getDBRows(query1);
												actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
														false, XMLType.RESPONSE);
												WSAssert.assertEquals(actualValues3, db3, false);

											}

											if (WSAssert.assertIfElementExists(fetchHKTaskRes,
													"Result_Text_TextElement", true)) {
												String message = WSClient.getElementValue(fetchHKTaskRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The text displayed in the response is    :     "
																+ message + "</b>");

											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3128() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3128";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated for Pickup rooms through the FetchHousekeepingTask call for all the rooms with the requested task code.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "Pickup");
				WSClient.setData("{var_roomStatus}", "PU");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String setHousekeepingRoomStatusReq1 = WSClient
													.createSOAPMessage("SetHousekeepingRoomStatus", "DS_03");
											String setHousekeepingRoomStatusRes1 = WSClient
													.processSOAPMessage(setHousekeepingRoomStatusReq1);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
													"SetHousekeepingRoomStatusRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully changed the status of room.</b>");
												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_03");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1", WSClient
																.getDBRow(WSClient.getQuery("QS_12")).get("COUNT"));

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_11");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to change status of room ");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3130() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3130";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated for OutofService rooms through the FetchHousekeepingTask call for all the rooms with the requested task code.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "OutOfService");
				WSClient.setData("{var_roomStatus}", "OS");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String setHousekeepingRoomStatusReq1 = WSClient
													.createSOAPMessage("SetHousekeepingRoomStatus", "DS_08");
											String setHousekeepingRoomStatusRes1 = WSClient
													.processSOAPMessage(setHousekeepingRoomStatusReq1);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
													"SetHousekeepingRoomStatusRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully changed the status of room.</b>");

												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_03");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1", WSClient
																.getDBRow(WSClient.getQuery("QS_12")).get("COUNT"));

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_11");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											}

											else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to change status of room ");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3129() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3129";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated for Out of Order rooms through the FetchHousekeepingTask call for all the rooms with the requested task code.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "OutOfOrder");
				WSClient.setData("{var_roomStatus}", "OO");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String setHousekeepingRoomStatusReq1 = WSClient
													.createSOAPMessage("SetHousekeepingRoomStatus", "DS_07");
											String setHousekeepingRoomStatusRes1 = WSClient
													.processSOAPMessage(setHousekeepingRoomStatusReq1);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
													"SetHousekeepingRoomStatusRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully changed the status of room.</b>");

												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_03");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1", WSClient
																.getDBRow(WSClient.getQuery("QS_12")).get("COUNT"));

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_11");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to change status of room ");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3136() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3136";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call for the rooms which are allocated to the Departed reservations with the requested task code",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_resvStatus}", "Departed");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_12");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");

											// Prerequisite 7: CheckIn
											// Reservation

											String checkInReq = WSClient.createSOAPMessage("CheckinReservation",
													"DS_01");
											String checkInRes = WSClient.processSOAPMessage(checkInReq);

											if (WSAssert.assertIfElementExists(checkInRes,
													"CheckinReservationRS_Success", true)) {

												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully checked in the reservation.</b>");

												if (CheckoutReservation.checkOutReservation("DS_01")) {

													WSClient.writeToReport(LogStatus.INFO,
															"<b>Succesfully checked out the reservation.</b>");
													WSClient.setData("{var_taskCode}",
															OperaPropConfig.getDataSetForCode("TaskCode", "DS_01"));
													HTNGLib.setHTNGHeader(OPERALib.getUserName(),
															OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
													String fetchHKTaskReq = WSClient
															.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_05");
													String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

													if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
															"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
															"SUCCESS", false)) {

														/**
														 * Verifying that the
														 * Tasksheet record is
														 * inserted
														 */
														// LinkedHashMap<String,String>
														// count=new
														// LinkedHashMap<String,String>();

														/**
														 * Validating the
														 * TaskDate,TaskCode,
														 * TaskSheetNumber,
														 * TotalRoomsInSheet,
														 * TaskInstruction
														 * details obtained from
														 * the response against
														 * the database details
														 */
														List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
														List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
														HashMap<String, String> xpath2 = new HashMap<String, String>();
														xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														String query = WSClient.getQuery("QS_04");
														db2 = WSClient.getDBRows(query);

														actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																xpath2, false, XMLType.RESPONSE);

														for (int i = 0; i < actualValues2.size(); ++i) {

															LinkedHashMap<String, String> exp = db2.get(i);

															exp.put("TotalRoomsInSheet1", WSClient
																	.getDBRows(WSClient.getQuery("QS_17")).size() + "");

														}
														WSAssert.assertEquals(actualValues2, db2, false);

														/**
														 * Checking if the Room
														 * Numbers displayed in
														 * the response message
														 * under each of the
														 * Task Sheet Details
														 * section are correctly
														 * matching with the
														 * room numbers
														 * resulting from the
														 * response of the
														 * database
														 */

														List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
														List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
														HashMap<String, String> xpath3 = new HashMap<String, String>();
														xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
																"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
														String query1 = WSClient.getQuery("QS_17");
														db3 = WSClient.getDBRows(query1);
														actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																xpath3, false, XMLType.RESPONSE);
														WSAssert.assertEquals(actualValues3, db3, false);

													}

													if (WSAssert.assertIfElementExists(fetchHKTaskRes,
															"Result_Text_TextElement", true)) {
														String message = WSClient.getElementValue(fetchHKTaskRes,
																"Result_Text_TextElement", XMLType.RESPONSE);
														WSClient.writeToReport(LogStatus.INFO,
																"<b>" + "The text displayed in the response is    :     "
																		+ message + "</b>");

													}
												} else {
													WSClient.writeToReport(LogStatus.WARNING,
															"Unable to checkout reservation ");
												}
											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to checkin reservation ");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
			// logger.endExtentTest();
		}

	}

	@Test(groups = { "fullRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3171() {
		try {

			String testName = "fetchHousekeepingTask_Ext_3171";
			WSClient.startTest(testName,
					"Verify that an error message is displayed on the response when Incorrect Resort is submitted to request for generating a new task using the FetchHousekeepingTask call",
					"fullRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", "INVRESORT");
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.setData("{var_taskCode}", OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());
				String fetchHKTaskReq = WSClient.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_01");
				String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

				if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
						"FetchHouseKeepingTaskResponse_Result_resultStatusFlag", "FAIL", false)) {

				}

				if (WSAssert.assertIfElementExists(fetchHKTaskRes, "Result_Text_TextElement", true)) {
					String message = WSClient.getElementValue(fetchHKTaskRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is    :     " + message + "</b>");

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}

	}

	@Test(groups = { "fullRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3169() {
		try {

			String testName = "fetchHousekeepingTask_Ext_3169";
			WSClient.startTest(testName,
					"Verify that an error message is displayed on the response when Resort is missed while submitting the request for generating a new task using the FetchHousekeepingTask call",
					"fullRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", "");
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.setData("{var_taskCode}", OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());
				String fetchHKTaskReq = WSClient.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_01");
				String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

				if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
						"FetchHouseKeepingTaskResponse_Result_resultStatusFlag", "FAIL", false)) {

				}

				if (WSAssert.assertIfElementExists(fetchHKTaskRes, "Result_Text_TextElement", true)) {
					String message = WSClient.getElementValue(fetchHKTaskRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is    :     " + message + "</b>");

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}

	}

	@Test(groups = { "fullRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3173() {
		try {

			String testName = "fetchHousekeepingTask_Ext_3173";
			WSClient.startTest(testName,
					"Verify that an error message is displayed on the response when Incorrect Task Code is submitted to request for generating a new task using the FetchHousekeepingTask call",
					"fullRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {

				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.setData("{var_taskCode}", "INVTASK");
				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());
				String fetchHKTaskReq = WSClient.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_01");
				String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

				if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
						"FetchHouseKeepingTaskResponse_Result_resultStatusFlag", "FAIL", false)) {

				}

				if (WSAssert.assertIfElementExists(fetchHKTaskRes, "Result_Text_TextElement", true)) {
					String message = WSClient.getElementValue(fetchHKTaskRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is    :     " + message + "</b>");

				}

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3133() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3133";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call for the rooms which are allocated to the Due Out reservations with the requested task code",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_resvStatus}", "DueOut");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_12");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");

											// Prerequisite 7: CheckIn
											// Reservation

											String checkInReq = WSClient.createSOAPMessage("CheckinReservation",
													"DS_01");
											String checkInRes = WSClient.processSOAPMessage(checkInReq);

											if (WSAssert.assertIfElementExists(checkInRes,
													"CheckinReservationRS_Success", true)) {

												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully checked in the reservation.</b>");

												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_01"));
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_05");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1",
																WSClient.getDBRows(WSClient.getQuery("QS_18")).size()
																+ "");

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_18");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}
											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to checkin reservation ");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
			// try {
			// if(!WSClient.getData("{var_resvId}").equals(""))
			// CheckoutReservation.checkOutReservation("DS_01");
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
		}

	}

	@Test(groups = { "targetedRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3148() {

		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3148";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call for the VIP rooms with the given task code",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode", "Title", "VipLevel" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							WSClient.setData("{var_nameTitle}", OperaPropConfig.getDataSetForCode("Title", "DS_01"));
						WSClient.setData("{var_vipCode}", OperaPropConfig.getDataSetForCode("VipLevel", "DS_02"));
						WSClient.setData("{var_birthDate}", "1995-11-09");
						profileID = CreateProfile.createProfile("DS_22");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											WSClient.setData("{var_taskCode}",
													OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
											WSClient.setData("{var_includeVip}", "true");
											HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
													HTNGLib.getInterfaceFromAddress());
											String fetchHKTaskReq = WSClient
													.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_11");
											String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

											if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
													"FetchHouseKeepingTaskResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {

												/**
												 * Verifying that the Tasksheet
												 * record is inserted
												 */
												// LinkedHashMap<String,String>
												// count=new
												// LinkedHashMap<String,String>();

												/**
												 * Validating the
												 * TaskDate,TaskCode,
												 * TaskSheetNumber,
												 * TotalRoomsInSheet,
												 * TaskInstruction details
												 * obtained from the response
												 * against the database details
												 */
												List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath2 = new HashMap<String, String>();
												xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												String query = WSClient.getQuery("QS_04");
												db2 = WSClient.getDBRows(query);

												actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
														false, XMLType.RESPONSE);

												for (int i = 0; i < actualValues2.size(); ++i) {

													LinkedHashMap<String, String> exp = db2.get(i);

													exp.put("TotalRoomsInSheet1",
															WSClient.getDBRows(WSClient.getQuery("QS_27")).size() + "");

												}
												WSAssert.assertEquals(actualValues2, db2, false);

												/**
												 * Checking if the Room Numbers
												 * displayed in the response
												 * message under each of the
												 * Task Sheet Details section
												 * are correctly matching with
												 * the room numbers resulting
												 * from the response of the
												 * database
												 */

												List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath3 = new HashMap<String, String>();
												xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
												String query1 = WSClient.getQuery("QS_27");
												db3 = WSClient.getDBRows(query1);
												actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
														false, XMLType.RESPONSE);
												WSAssert.assertEquals(actualValues3, db3, false);

											}

											if (WSAssert.assertIfElementExists(fetchHKTaskRes,
													"Result_Text_TextElement", true)) {
												String message = WSClient.getElementValue(fetchHKTaskRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The text displayed in the response is    :     "
																+ message + "</b>");

											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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

	}

	// /**not correct**/
	// @Test(groups = { "minimumRegression", "FetchHouseKeepingTask",
	// "HTNG2008BExt", "HTNG" })
	//
	// public void fetchHouseKeepingTask_Ext_12() {
	// String profileID = "", roomNumber = "";
	// try {
	//
	// String testName = "fetchHousekeepingTask_Ext_12";
	// WSClient.startTest(testName,
	// "Verify that a new Task sheet is generated for Day Use through the
	// FetchHousekeepingTask call for all the rooms with the requested task
	// code.",
	// "minimumRegression");
	//
	// if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode"
	// })) {
	// String parameter1 = "", parameter2 = "";
	// String interfaceName = HTNGLib.getHTNGInterface();
	// String resortOperaValue = OPERALib.getResort();
	// String resortExtValue = HTNGLib.getExtResort(resortOperaValue,
	// interfaceName);
	// WSClient.setData("{var_extResort}", resortExtValue);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_chain}", OPERALib.getChain());
	// WSClient.setData("{var_resvStatus}", "DayUse");
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	//
	// WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :
	// FACILITY_MANAGEMENT****</b>");
	// WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
	// WSClient.setData("{var_settingValue}", "Y");
	//
	// if
	// (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
	// parameter1 =
	// ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
	//
	// if (!parameter1.equals("error")) {
	//
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
	// WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
	// WSClient.setData("{var_settingValue}", "Y");
	//
	// if
	// (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
	// parameter2 =
	// ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
	//
	// if (!parameter2.equals("error")) {
	// if (profileID.equals(""))
	// profileID = CreateProfile.createProfile("DS_01");
	// if (!profileID.equals("error")) {
	// WSClient.setData("{var_profileId}", profileID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID +
	// "</b>");
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_roomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_payment}",
	// OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	//
	// // Prerequisite 2 Create Reservation
	// HashMap<String, String> resv =
	// CreateReservation.createReservation("DS_12");
	// String resvID = resv.get("reservationId");
	//
	// if (!resvID.equals("error")) {
	//
	// WSClient.setData("{var_resvId}", resvID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID +
	// "</b>");
	// // Prerequisite 3: Fetching available Hotel
	// // rooms with
	// // room type
	//
	// String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms",
	// "DS_03");
	// String fetchHotelRoomsRes =
	// WSClient.processSOAPMessage(fetchHotelRoomsReq);
	//
	// if (WSAssert.assertIfElementExists(fetchHotelRoomsRes,
	// "FetchHotelRoomsRS_Success",
	// true)
	// && WSAssert.assertIfElementExists(fetchHotelRoomsRes,
	// "FetchHotelRoomsRS_HotelRooms_Room", true)) {
	//
	// roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
	// "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
	//
	// } else {
	//
	// // Prerequisite 4: Creating a room to assign
	//
	// String createRoomReq = WSClient.createSOAPMessage("CreateRoom",
	// "RoomMaint");
	// String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
	//
	// if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success",
	// true)) {
	//
	// roomNumber = WSClient.getElementValue(createRoomReq,
	// "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
	//
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create
	// room");
	// }
	// }
	// if (!roomNumber.equals("")) {
	// WSClient.setData("{var_roomNumber}", roomNumber);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create
	// room.</b>");
	// // Prerequisite 5: Changing the room status
	// // to inspected
	// // to assign the room for checking in
	//
	// String setHousekeepingRoomStatusReq = WSClient
	// .createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
	// String setHousekeepingRoomStatusRes = WSClient
	// .processSOAPMessage(setHousekeepingRoomStatusReq);
	//
	// if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
	// "SetHousekeepingRoomStatusRS_Success", true)) {
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>Succesfully changed the status of room.</b>");
	//
	// // Prerequisite 6: Assign Room
	//
	// String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
	// String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);
	//
	// if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
	// true)) {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned
	// room.</b>");
	//
	// // Prerequisite 7: CheckIn
	// String checkInReq = WSClient.createSOAPMessage("CheckinReservation",
	// "DS_01");
	// String checkInRes = WSClient.processSOAPMessage(checkInReq);
	//
	// if (WSAssert.assertIfElementExists(checkInRes,
	// "CheckinReservationRS_Success", true)) {
	//
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>Succesfully checked in the reservation.</b>");
	//
	//
	// WSClient.setData("{var_taskCode}",
	// OperaPropConfig.getDataSetForCode("TaskCode", "DS_01"));
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// HTNGLib.getInterfaceFromAddress());
	// String fetchHKTaskReq = WSClient
	// .createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_05");
	// String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);
	//
	// if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
	// "FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
	// "SUCCESS", false)) {
	//
	// /**
	// * Verifying that the
	// * Tasksheet record is
	// * inserted
	// */
	// // LinkedHashMap<String,String>
	// // count=new
	// // LinkedHashMap<String,String>();
	//
	// /**
	// * Validating the
	// * TaskDate,TaskCode,TaskSheetNumber,TotalRoomsInSheet,TaskInstruction
	// * details obtained from the
	// * response against the
	// * database details
	// */
	// List<LinkedHashMap<String, String>> db2 = new
	// ArrayList<LinkedHashMap<String, String>>();
	// List<LinkedHashMap<String, String>> actualValues2 = new
	// ArrayList<LinkedHashMap<String, String>>();
	// HashMap<String, String> xpath2 = new HashMap<String, String>();
	// xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
	// "FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
	// xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
	// "FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
	// xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
	// "FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
	// xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
	// "FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
	// String query = WSClient.getQuery("QS_04");
	// db2 = WSClient.getDBRows(query);
	//
	// actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
	// false, XMLType.RESPONSE);
	//
	// for (int i = 0; i < actualValues2.size(); ++i) {
	//
	// LinkedHashMap<String, String> exp = db2.get(i);
	//
	// exp.put("TotalRoomsInSheet1",
	// WSClient.getDBRows(WSClient.getQuery("QS_19")).size()
	// + "");
	//
	// }
	// WSAssert.assertEquals(actualValues2, db2, false);
	//
	// /**
	// * Checking if the Room
	// * Numbers displayed in the
	// * response message under
	// * each of the Task Sheet
	// * Details section are
	// * correctly matching with
	// * the room numbers
	// * resulting from the
	// * response of the database
	// */
	//
	// List<LinkedHashMap<String, String>> db3 = new
	// ArrayList<LinkedHashMap<String, String>>();
	// List<LinkedHashMap<String, String>> actualValues3 = new
	// ArrayList<LinkedHashMap<String, String>>();
	// HashMap<String, String> xpath3 = new HashMap<String, String>();
	// xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
	// "FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
	// String query1 = WSClient.getQuery("QS_19");
	// db3 = WSClient.getDBRows(query1);
	// actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
	// false, XMLType.RESPONSE);
	// WSAssert.assertEquals(actualValues3, db3, false);
	//
	// }
	//
	// if (WSAssert.assertIfElementExists(fetchHKTaskRes,
	// "Result_Text_TextElement", true)) {
	// String message = WSClient.getElementValue(fetchHKTaskRes,
	// "Result_Text_TextElement", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>" + "The text displayed in the response is : "
	// + message + "</b>");
	//
	// }
	// }
	//
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "Unable to checkin
	// reservation");
	// }
	// }else {
	// WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
	// }
	//
	// }
	//
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of
	// room ");
	// }
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
	// }
	//
	// }
	// }
	// }
	//
	// }
	//
	// }
	//
	// catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// try {
	//// if(!WSClient.getData("{var_resvId}").equals(""))
	//// CancelReservation.cancelReservation("DS_02");
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// }

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3139() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3139";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call for the rooms which are not allocated to any of the reservations (Vacant Rooms) with the requested task code",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_resvStatus}", "NotReserved");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

						String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
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
						if (!roomNumber.equals("")) {
							WSClient.setData("{var_roomNumber}", roomNumber);
							WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
							// Prerequisite 5: Changing the room status
							// to inspected
							// to assign the room for checking in

							String setHousekeepingRoomStatusReq = WSClient
									.createSOAPMessage("SetHousekeepingRoomStatus", "DS_06");
							String setHousekeepingRoomStatusRes = WSClient
									.processSOAPMessage(setHousekeepingRoomStatusReq);

							if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
									"SetHousekeepingRoomStatusRS_Success", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Succesfully changed the status of room.</b>");

								WSClient.setData("{var_taskCode}",
										OperaPropConfig.getDataSetForCode("TaskCode", "DS_01"));
								HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
										HTNGLib.getInterfaceFromAddress());
								String fetchHKTaskReq = WSClient.createSOAPMessage("HTNGExtFetchHousekeepingTask",
										"DS_05");
								String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

								if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
										"FetchHouseKeepingTaskResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									/**
									 * Verifying that the Tasksheet record is
									 * inserted
									 */
									// LinkedHashMap<String,String>
									// count=new
									// LinkedHashMap<String,String>();

									/**
									 * Validating the
									 * TaskDate,TaskCode,TaskSheetNumber,
									 * TotalRoomsInSheet,TaskInstruction details
									 * obtained from the response against the
									 * database details
									 */
									List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
									List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
									HashMap<String, String> xpath2 = new HashMap<String, String>();
									xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
											"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
									xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
											"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
									xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
											"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
									xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
											"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
									String query = WSClient.getQuery("QS_04");
									db2 = WSClient.getDBRows(query);

									actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2, false,
											XMLType.RESPONSE);

									for (int i = 0; i < actualValues2.size(); ++i) {

										LinkedHashMap<String, String> exp = db2.get(i);

										exp.put("TotalRoomsInSheet1",
												WSClient.getDBRows(WSClient.getQuery("QS_20")).size() + "");

									}
									WSAssert.assertEquals(actualValues2, db2, false);

									/**
									 * Checking if the Room Numbers displayed in
									 * the response message under each of the
									 * Task Sheet Details section are correctly
									 * matching with the room numbers resulting
									 * from the response of the database
									 */

									List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
									List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
									HashMap<String, String> xpath3 = new HashMap<String, String>();
									xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
											"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
									String query1 = WSClient.getQuery("QS_20");
									db3 = WSClient.getDBRows(query1);
									actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3, false,
											XMLType.RESPONSE);
									WSAssert.assertEquals(actualValues3, db3, false);

								}

								if (WSAssert.assertIfElementExists(fetchHKTaskRes, "Result_Text_TextElement", true)) {
									String message = WSClient.getElementValue(fetchHKTaskRes, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>"
											+ "The text displayed in the response is    :     " + message + "</b>");

								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
						}

					}
				}
			}

		}

		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "targetedRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3140() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3140";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call for the rooms which are Dirty (Room Staus as Dirty) and Vacant (FO Sttaus as Vacant) with the requested Task code",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "Dirty");
				WSClient.setData("{var_fStatus}", "Vacant");
				WSClient.setData("{var_foStatus}", "VAC");
				WSClient.setData("{var_roomStatus}", "DI");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String setHousekeepingRoomStatusReq1 = WSClient
													.createSOAPMessage("SetHousekeepingRoomStatus", "DS_06");
											String setHousekeepingRoomStatusRes1 = WSClient
													.processSOAPMessage(setHousekeepingRoomStatusReq1);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
													"SetHousekeepingRoomStatusRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully changed the status of room.</b>");
												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_06");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1",
																WSClient.getDBRows(WSClient.getQuery("QS_21")).size()
																+ "");

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_21");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to change status of room ");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "targetedRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3141() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3141";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call for the rooms which are Dirty (Room Staus as Dirty) and Occupied (FO Sttaus as Occupied) with the requested Task code",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "Dirty");
				WSClient.setData("{var_fStatus}", "Occupied");
				WSClient.setData("{var_foStatus}", "OCC");
				WSClient.setData("{var_roomStatus}", "DI");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String checkInReq = WSClient.createSOAPMessage("CheckinReservation",
													"DS_01");
											String checkInRes = WSClient.processSOAPMessage(checkInReq);

											if (WSAssert.assertIfElementExists(checkInRes,
													"CheckinReservationRS_Success", true)) {

												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully checked in the reservation.</b>");
												String setHousekeepingRoomStatusReq1 = WSClient
														.createSOAPMessage("SetHousekeepingRoomStatus", "DS_06");
												String setHousekeepingRoomStatusRes1 = WSClient
														.processSOAPMessage(setHousekeepingRoomStatusReq1);

												if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
														"SetHousekeepingRoomStatusRS_Success", true)) {
													WSClient.writeToReport(LogStatus.INFO,
															"<b>Succesfully changed the status of room.</b>");
													WSClient.setData("{var_taskCode}",
															OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
													HTNGLib.setHTNGHeader(OPERALib.getUserName(),
															OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
													String fetchHKTaskReq = WSClient
															.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_06");
													String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

													if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
															"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
															"SUCCESS", false)) {

														/**
														 * Verifying that the
														 * Tasksheet record is
														 * inserted
														 */
														// LinkedHashMap<String,String>
														// count=new
														// LinkedHashMap<String,String>();

														/**
														 * Validating the
														 * TaskDate,TaskCode,
														 * TaskSheetNumber,
														 * TotalRoomsInSheet,
														 * TaskInstruction
														 * details obtained from
														 * the response against
														 * the database details
														 */
														List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
														List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
														HashMap<String, String> xpath2 = new HashMap<String, String>();
														xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														String query = WSClient.getQuery("QS_04");
														db2 = WSClient.getDBRows(query);

														actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																xpath2, false, XMLType.RESPONSE);

														for (int i = 0; i < actualValues2.size(); ++i) {

															LinkedHashMap<String, String> exp = db2.get(i);

															exp.put("TotalRoomsInSheet1", WSClient
																	.getDBRows(WSClient.getQuery("QS_21")).size() + "");

														}
														WSAssert.assertEquals(actualValues2, db2, false);

														/**
														 * Checking if the Room
														 * Numbers displayed in
														 * the response message
														 * under each of the
														 * Task Sheet Details
														 * section are correctly
														 * matching with the
														 * room numbers
														 * resulting from the
														 * response of the
														 * database
														 */

														List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
														List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
														HashMap<String, String> xpath3 = new HashMap<String, String>();
														xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
																"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
														String query1 = WSClient.getQuery("QS_21");
														db3 = WSClient.getDBRows(query1);
														actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																xpath3, false, XMLType.RESPONSE);
														WSAssert.assertEquals(actualValues3, db3, false);

													}

													if (WSAssert.assertIfElementExists(fetchHKTaskRes,
															"Result_Text_TextElement", true)) {
														String message = WSClient.getElementValue(fetchHKTaskRes,
																"Result_Text_TextElement", XMLType.RESPONSE);
														WSClient.writeToReport(LogStatus.INFO,
																"<b>" + "The text displayed in the response is    :     "
																		+ message + "</b>");

													}

												} else {
													WSClient.writeToReport(LogStatus.WARNING,
															"Unable to change status of room ");
												}
											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to checkin reservation ");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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

				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "targetedRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3142() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3142";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call for the rooms which are to be Pickedup (Room Staus as Pickup) and Arrival (Reservation Staus as Arrival) with the requested Task code",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "Pickup");
				WSClient.setData("{var_resvStatus}", "Arrival");
				WSClient.setData("{var_roomStatus}", "PU");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String setHousekeepingRoomStatusReq1 = WSClient
													.createSOAPMessage("SetHousekeepingRoomStatus", "DS_03");
											String setHousekeepingRoomStatusRes1 = WSClient
													.processSOAPMessage(setHousekeepingRoomStatusReq1);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
													"SetHousekeepingRoomStatusRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully changed the status of room.</b>");
												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_07");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1",
																WSClient.getDBRows(WSClient.getQuery("QS_22")).size()
																+ "");

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_22");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to change status of room ");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "targetedRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3143() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3143";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call for the rooms having the status as Dirty, Pickup  statuses with the requested task code.",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "Dirty");
				WSClient.setData("{var_roomStatus}", "DI");
				WSClient.setData("{var_rStatus1}", "Pickup");
				WSClient.setData("{var_roomStatus1}", "PU");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String setHousekeepingRoomStatusReq1 = WSClient
													.createSOAPMessage("SetHousekeepingRoomStatus", "DS_06");
											String setHousekeepingRoomStatusRes1 = WSClient
													.processSOAPMessage(setHousekeepingRoomStatusReq1);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
													"SetHousekeepingRoomStatusRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully changed the status of room.</b>");
												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_08");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1",
																WSClient.getDBRows(WSClient.getQuery("QS_23")).size()
																+ "");

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_23");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to change status of room ");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "targetedRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3144() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3144";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call for the rooms allotted for Arrived and Arrival reservations with the requested task code",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_resvStatus}", "Arrived");
				WSClient.setData("{var_resvStatus1}", "Arrival");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");

											String checkInReq = WSClient.createSOAPMessage("CheckinReservation",
													"DS_01");
											String checkInRes = WSClient.processSOAPMessage(checkInReq);

											if (WSAssert.assertIfElementExists(checkInRes,
													"CheckinReservationRS_Success", true)) {

												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully checked in the reservation.</b>");

												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_09");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1",
																WSClient.getDBRows(WSClient.getQuery("QS_24")).size()
																+ "");

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_24");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to change status of room ");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Test(groups = { "targetedRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3147() {

		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3147";
			WSClient.startTest(testName,
					"Verify that the Room instruction which is submitted through the FetchHousekeepingTask service call is added to the generated task sheet",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											WSClient.setData("{var_taskCode}",
													OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
											HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
													HTNGLib.getInterfaceFromAddress());
											String fetchHKTaskReq = WSClient
													.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_10");
											String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

											if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
													"FetchHouseKeepingTaskResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {

												/**
												 * Verifying that the Tasksheet
												 * record is inserted
												 */
												// LinkedHashMap<String,String>
												// count=new
												// LinkedHashMap<String,String>();

												/**
												 * Validating the
												 * TaskDate,TaskCode,
												 * TaskSheetNumber,
												 * TotalRoomsInSheet,
												 * TaskInstruction details
												 * obtained from the response
												 * against the database details
												 */
												List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath2 = new HashMap<String, String>();
												xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												String query = WSClient.getQuery("QS_04");
												db2 = WSClient.getDBRows(query);

												actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
														false, XMLType.RESPONSE);

												for (int i = 0; i < actualValues2.size(); ++i) {

													LinkedHashMap<String, String> exp = db2.get(i);

													exp.put("TotalRoomsInSheet1",
															WSClient.getDBRows(WSClient.getQuery("QS_25")).size() + "");

												}
												WSAssert.assertEquals(db2, actualValues2, false);

												/**
												 * Checking if the Room Numbers
												 * displayed in the response
												 * message under each of the
												 * Task Sheet Details section
												 * are correctly matching with
												 * the room numbers resulting
												 * from the response of the
												 * database
												 */

												List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath3 = new HashMap<String, String>();
												xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
												xpath3.put("TaskSheets_TasksheetDetails_RoomInstruction",
														"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
												String query1 = WSClient.getQuery("QS_25");
												db3 = WSClient.getDBRows(query1);
												actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
														false, XMLType.RESPONSE);
												WSAssert.assertEquals(actualValues3, db3, false);

											}

											if (WSAssert.assertIfElementExists(fetchHKTaskRes,
													"Result_Text_TextElement", true)) {
												String message = WSClient.getElementValue(fetchHKTaskRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The text displayed in the response is    :     "
																+ message + "</b>");

											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Test(groups = { "targetedRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3149() {

		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3149";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call for all the rooms available for the given Resort when the Include VIP Only parameter is submitted as false",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode", "Title", "VipLevel" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							WSClient.setData("{var_nameTitle}", OperaPropConfig.getDataSetForCode("Title", "DS_01"));
						WSClient.setData("{var_vipCode}", OperaPropConfig.getDataSetForCode("VipLevel", "DS_02"));
						WSClient.setData("{var_birthDate}", "1995-11-09");
						profileID = CreateProfile.createProfile("DS_22");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											WSClient.setData("{var_taskCode}",
													OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
											WSClient.setData("{var_includeVip}", "false");
											HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
													HTNGLib.getInterfaceFromAddress());
											String fetchHKTaskReq = WSClient
													.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_11");
											String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

											if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
													"FetchHouseKeepingTaskResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {

												/**
												 * Verifying that the Tasksheet
												 * record is inserted
												 */
												// LinkedHashMap<String,String>
												// count=new
												// LinkedHashMap<String,String>();

												/**
												 * Validating the
												 * TaskDate,TaskCode,
												 * TaskSheetNumber,
												 * TotalRoomsInSheet,
												 * TaskInstruction details
												 * obtained from the response
												 * against the database details
												 */
												List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath2 = new HashMap<String, String>();
												xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												String query = WSClient.getQuery("QS_04");
												db2 = WSClient.getDBRows(query);

												actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
														false, XMLType.RESPONSE);

												for (int i = 0; i < actualValues2.size(); ++i) {

													LinkedHashMap<String, String> exp = db2.get(i);

													exp.put("TotalRoomsInSheet1",
															WSClient.getDBRows(WSClient.getQuery("QS_05")).size() + "");

												}
												WSAssert.assertEquals(actualValues2, db2, false);

												/**
												 * Checking if the Room Numbers
												 * displayed in the response
												 * message under each of the
												 * Task Sheet Details section
												 * are correctly matching with
												 * the room numbers resulting
												 * from the response of the
												 * database
												 */

												List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath3 = new HashMap<String, String>();
												xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
												String query1 = WSClient.getQuery("QS_05");
												db3 = WSClient.getDBRows(query1);
												actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
														false, XMLType.RESPONSE);
												WSAssert.assertEquals(actualValues3, db3, false);

											}

											if (WSAssert.assertIfElementExists(fetchHKTaskRes,
													"Result_Text_TextElement", true)) {
												String message = WSClient.getElementValue(fetchHKTaskRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The text displayed in the response is    :     "
																+ message + "</b>");

											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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

	}

	@Test(groups = { "targetedRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3152() {

		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3152";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call for all the VIP rooms which are occupied with the given task code",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode", "Title", "VipLevel" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_fStatus}", "Occupied");
				WSClient.setData("{var_foStatus}", "OCC");
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							WSClient.setData("{var_nameTitle}", OperaPropConfig.getDataSetForCode("Title", "DS_01"));
						WSClient.setData("{var_vipCode}", OperaPropConfig.getDataSetForCode("VipLevel", "DS_02"));
						WSClient.setData("{var_birthDate}", "1995-11-09");
						profileID = CreateProfile.createProfile("DS_22");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String checkInReq = WSClient.createSOAPMessage("CheckinReservation",
													"DS_01");
											String checkInRes = WSClient.processSOAPMessage(checkInReq);

											if (WSAssert.assertIfElementExists(checkInRes,
													"CheckinReservationRS_Success", true)) {

												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully checked in the reservation.</b>");
												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
												WSClient.setData("{var_includeVip}", "true");
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_12");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1",
																WSClient.getDBRows(WSClient.getQuery("QS_28")).size()
																+ "");

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_28");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to checkin reservation ");
											}
										} else {

											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}

	}

	@Test(groups = { "targetedRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3153() {

		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3153";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call for all the Dirty VIP rooms which are Vacant",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode", "Title", "VipLevel" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_fStatus}", "Vacant");
				WSClient.setData("{var_foStatus}", "VAC");
				WSClient.setData("{var_rStatus}", "Dirty");
				WSClient.setData("{var_roomStatus}", "DI");
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							WSClient.setData("{var_nameTitle}", OperaPropConfig.getDataSetForCode("Title", "DS_01"));
						WSClient.setData("{var_vipCode}", OperaPropConfig.getDataSetForCode("VipLevel", "DS_02"));
						WSClient.setData("{var_birthDate}", "1995-11-09");
						profileID = CreateProfile.createProfile("DS_22");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String setHousekeepingRoomStatusReq1 = WSClient
													.createSOAPMessage("SetHousekeepingRoomStatus", "DS_06");
											String setHousekeepingRoomStatusRes1 = WSClient
													.processSOAPMessage(setHousekeepingRoomStatusReq1);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
													"SetHousekeepingRoomStatusRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully changed the status of room.</b>");
												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
												WSClient.setData("{var_includeVip}", "true");
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_13");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1",
																WSClient.getDBRows(WSClient.getQuery("QS_29")).size()
																+ "");

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_29");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to change status of room ");
											}
										} else {

											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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

	}

	@Test(groups = { "targetedRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3150() {

		String profileID = "", roomNumber = "", resvID = "", resvID1 = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3150";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call for all the VIP rooms excluding the rooms satisfying the submitted VIP exclusion criteria",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode", "Title", "VipLevel" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							WSClient.setData("{var_nameTitle}", OperaPropConfig.getDataSetForCode("Title", "DS_01"));
						WSClient.setData("{var_vipCode}", OperaPropConfig.getDataSetForCode("VipLevel", "DS_02"));
						WSClient.setData("{var_birthDate}", "1995-11-09");
						profileID = CreateProfile.createProfile("DS_22");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");

											WSClient.setData("{var_nameTitle}",
													OperaPropConfig.getDataSetForCode("Title", "DS_01"));
											WSClient.setData("{var_vipCode}",
													OperaPropConfig.getDataSetForCode("VipLevel", "DS_01"));
											WSClient.setData("{var_birthDate}", "1995-11-09");
											profileID = CreateProfile.createProfile("DS_22");
											if (!profileID.equals("error")) {
												WSClient.setData("{var_profileId}", profileID);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Profile ID : " + profileID + "</b>");
												WSClient.setData("{VAR_RATEPLANCODE}",
														OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
												WSClient.setData("{VAR_ROOMTYPE}",
														OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
												WSClient.setData("{var_roomType}",
														OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
												WSClient.setData("{var_RoomType}",
														OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
												WSClient.setData("{var_sourceCode}",
														OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
												WSClient.setData("{VAR_MARKETCODE}",
														OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
												WSClient.setData("{var_payment}",
														OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

												// Prerequisite 2 Create
												// Reservation
												HashMap<String, String> resv1 = CreateReservation
														.createReservation("DS_04");
												resvID1 = resv1.get("reservationId");

												if (!resvID1.equals("error")) {

													WSClient.setData("{var_resvId}", resvID1);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>Reservation ID : " + resvID1 + "</b>");
													// Prerequisite 3: Fetching
													// available Hotel
													// rooms with
													// room type

													String fetchHotelRoomsReq1 = WSClient
															.createSOAPMessage("FetchHotelRooms", "DS_03");
													String fetchHotelRoomsRes1 = WSClient
															.processSOAPMessage(fetchHotelRoomsReq1);

													if (WSAssert.assertIfElementExists(fetchHotelRoomsRes1,
															"FetchHotelRoomsRS_Success", true)
															&& WSAssert.assertIfElementExists(fetchHotelRoomsRes1,
																	"FetchHotelRoomsRS_HotelRooms_Room", true)) {

														roomNumber = WSClient.getElementValue(fetchHotelRoomsRes1,
																"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber",
																XMLType.RESPONSE);

													} else {

														// Prerequisite 4:
														// Creating a room to
														// assign

														String createRoomReq = WSClient.createSOAPMessage("CreateRoom",
																"RoomMaint");
														String createRoomRes = WSClient
																.processSOAPMessage(createRoomReq);

														if (WSAssert.assertIfElementExists(createRoomRes,
																"CreateRoomRS_Success", true)) {

															roomNumber = WSClient.getElementValue(createRoomReq,
																	"CreateRoomRQ_Room_RoomDetails_RoomNumber",
																	XMLType.REQUEST);

														} else {
															WSClient.writeToReport(LogStatus.WARNING,
																	"Blocked : Unable to create room");
														}
													}
													if (!roomNumber.equals("")) {
														WSClient.setData("{var_roomNumber}", roomNumber);
														WSClient.writeToReport(LogStatus.INFO,
																"<b>Succesfully fetched/create room.</b>");
														// Prerequisite 5:
														// Changing the room
														// status
														// to inspected
														// to assign the room
														// for checking in

														String setHousekeepingRoomStatusReq2 = WSClient
																.createSOAPMessage("SetHousekeepingRoomStatus",
																		"DS_01");
														String setHousekeepingRoomStatusRes2 = WSClient
																.processSOAPMessage(setHousekeepingRoomStatusReq2);

														if (WSAssert.assertIfElementExists(
																setHousekeepingRoomStatusRes2,
																"SetHousekeepingRoomStatusRS_Success", true)) {
															WSClient.writeToReport(LogStatus.INFO,
																	"<b>Succesfully changed the status of room.</b>");

															// Prerequisite 6:
															// Assign Room

															String assignRoomReq1 = WSClient
																	.createSOAPMessage("AssignRoom", "DS_01");
															String assignRoomRes1 = WSClient
																	.processSOAPMessage(assignRoomReq1);

															if (WSAssert.assertIfElementExists(assignRoomRes1,
																	"AssignRoomRS_Success", true)) {
																WSClient.writeToReport(LogStatus.INFO,
																		"<b>Succesfully assigned room.</b>");

																WSClient.setData("{var_taskCode}", OperaPropConfig
																		.getDataSetForCode("TaskCode", "DS_02"));
																WSClient.setData("{var_includeVip}", "true");
																WSClient.setData("{var_excludeVip}", OperaPropConfig
																		.getDataSetForCode("VipLevel", "DS_01"));

																HTNGLib.setHTNGHeader(OPERALib.getUserName(),
																		OPERALib.getPassword(),
																		HTNGLib.getInterfaceFromAddress());
																String fetchHKTaskReq = WSClient.createSOAPMessage(
																		"HTNGExtFetchHousekeepingTask", "DS_14");
																String fetchHKTaskRes = WSClient
																		.processSOAPMessage(fetchHKTaskReq);

																if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
																		"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
																		"SUCCESS", false)) {

																	/**
																	 * Verifying
																	 * that the
																	 * Tasksheet
																	 * record is
																	 * inserted
																	 */
																	// LinkedHashMap<String,String>
																	// count=new
																	// LinkedHashMap<String,String>();

																	/**
																	 * Validating
																	 * the
																	 * TaskDate,
																	 * TaskCode,
																	 * TaskSheetNumber
																	 * ,
																	 * TotalRoomsInSheet
																	 * ,
																	 * TaskInstruction
																	 * details
																	 * obtained
																	 * from the
																	 * response
																	 * against
																	 * the
																	 * database
																	 * details
																	 */
																	List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
																	List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
																	HashMap<String, String> xpath2 = new HashMap<String, String>();
																	xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
																			"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
																	xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
																			"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
																	xpath2.put(
																			"TaskSheets_TaskSheetHeader_TaskSheetNumber",
																			"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
																	xpath2.put(
																			"TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
																			"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
																	String query = WSClient.getQuery("QS_04");
																	db2 = WSClient.getDBRows(query);

																	actualValues2 = WSClient.getMultipleNodeList(
																			fetchHKTaskRes, xpath2, false,
																			XMLType.RESPONSE);

																	for (int i = 0; i < actualValues2.size(); ++i) {

																		LinkedHashMap<String, String> exp = db2.get(i);

																		exp.put("TotalRoomsInSheet1",
																				WSClient.getDBRows(
																						WSClient.getQuery("QS_30"))
																				.size() + "");

																	}
																	WSAssert.assertEquals(actualValues2, db2, false);

																	/**
																	 * Checking
																	 * if the
																	 * Room
																	 * Numbers
																	 * displayed
																	 * in the
																	 * response
																	 * message
																	 * under
																	 * each of
																	 * the Task
																	 * Sheet
																	 * Details
																	 * section
																	 * are
																	 * correctly
																	 * matching
																	 * with the
																	 * room
																	 * numbers
																	 * resulting
																	 * from the
																	 * response
																	 * of the
																	 * database
																	 */

																	List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
																	List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
																	HashMap<String, String> xpath3 = new HashMap<String, String>();
																	xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
																			"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
																	String query1 = WSClient.getQuery("QS_30");
																	db3 = WSClient.getDBRows(query1);
																	actualValues3 = WSClient.getMultipleNodeList(
																			fetchHKTaskRes, xpath3, false,
																			XMLType.RESPONSE);
																	WSAssert.assertEquals(actualValues3, db3, false);

																}

																if (WSAssert.assertIfElementExists(fetchHKTaskRes,
																		"Result_Text_TextElement", true)) {
																	String message = WSClient.getElementValue(
																			fetchHKTaskRes, "Result_Text_TextElement",
																			XMLType.RESPONSE);
																	WSClient.writeToReport(LogStatus.INFO,
																			"<b>" + "The text displayed in the response is    :     "
																					+ message + "</b>");

																}

															} else {
																WSClient.writeToReport(LogStatus.WARNING,
																		"Unable to assign the room ");
															}

														} else {
															WSClient.writeToReport(LogStatus.WARNING,
																	"Unable to change status of room ");
														}
													} else {
														WSClient.writeToReport(LogStatus.WARNING,
																"Unable to fetch room ");
													}
												}

											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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

				if (!resvID.equals("")) {
					WSClient.setData("{var_resvId}", resvID);
					CancelReservation.cancelReservation("DS_02");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Test(groups = { "targetedRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })
	public void fetchHouseKeepingTask_Ext_3146() {
		String profileID = "", roomNumber = "";
		try {
			String testName = "fetchHousekeepingTask_Ext_3146";
			WSClient.startTest(testName,
					"Verify that the default task instruction is added to the generated task sheet when task instruction is not submitted through the FetchHousekeepingTask service call",
					"targetedRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");
				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				if (!parameter1.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");
					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_12");
							String resvID = resv.get("reservationId");
							if (!resvID.equals("error")) {
								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type
								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);
								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in
									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);
									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");
										// Prerequisite 6: Assign Room
										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);
										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											WSClient.setData("{var_taskCode}",
													OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
											HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
													HTNGLib.getInterfaceFromAddress());
											String fetchHKTaskReq = WSClient
													.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_15");
											String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);
											if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
													"FetchHouseKeepingTaskResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {

												WSClient.writeToReport(LogStatus.INFO,
														"<b>Validating default task instruction is populated in Tasksheet Header in response</b>");
												LinkedHashMap<String, String> db2 = new LinkedHashMap<String, String>();
												List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath2 = new HashMap<String, String>();
												xpath2.put("TaskSheets_TaskSheetHeader_TaskInstruction",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												String query1 = WSClient.getQuery("QS_45");
												db2 = WSClient.getDBRow(query1);

												actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
														false, XMLType.RESPONSE);
												for (int i = 0; i < actualValues2.size(); i++) {
													WSAssert.assertEquals(db2, actualValues2.get(i), false);
												}
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Validating default task instruction is populated in Tasksheet Details in response</b>");
												List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath3 = new HashMap<String, String>();
												xpath3.put("TaskSheets_TasksheetDetails_TaskInstruction",
														"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
												;
												xpath3.put("TaskSheets_TasksheetDetails_TaskCode",
														"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");

												actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
														false, XMLType.RESPONSE);
												for (int i = 0; i < actualValues3.size(); i++) {
													WSAssert.assertEquals(db2, actualValues3.get(i), false);
												}
												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");
												}

											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "fullRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3175() {

		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3175";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call and the Rooms assignments are ordered by room number",
					"fullRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											WSClient.setData("{var_taskCode}",
													OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
											WSClient.setData("{var_sortBy}", "ROOM");
											HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
													HTNGLib.getInterfaceFromAddress());
											String fetchHKTaskReq = WSClient
													.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_16");
											String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

											if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
													"FetchHouseKeepingTaskResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {

												/**
												 * Verifying that the Tasksheet
												 * record is inserted
												 */
												// LinkedHashMap<String,String>
												// count=new
												// LinkedHashMap<String,String>();

												/**
												 * Validating the
												 * TaskDate,TaskCode,
												 * TaskSheetNumber,
												 * TotalRoomsInSheet,
												 * TaskInstruction details
												 * obtained from the response
												 * against the database details
												 */
												List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath2 = new HashMap<String, String>();
												xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												String query = WSClient.getQuery("QS_04");
												db2 = WSClient.getDBRows(query);

												actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
														false, XMLType.RESPONSE);

												for (int i = 0; i < actualValues2.size(); ++i) {

													LinkedHashMap<String, String> exp = db2.get(i);

													exp.put("TotalRoomsInSheet1",
															WSClient.getDBRows(WSClient.getQuery("QS_33")).size() + "");

												}
												WSAssert.assertEquals(actualValues2, db2, false);

												/**
												 * Checking if the Room Numbers
												 * displayed in the response
												 * message under each of the
												 * Task Sheet Details section
												 * are correctly matching with
												 * the room numbers resulting
												 * from the response of the
												 * database
												 */

												List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath3 = new HashMap<String, String>();
												xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
												String query1 = WSClient.getQuery("QS_33");
												db3 = WSClient.getDBRows(query1);
												actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
														false, XMLType.RESPONSE);
												WSAssert.assertEquals(actualValues3, db3, false);

											}

											if (WSAssert.assertIfElementExists(fetchHKTaskRes,
													"Result_Text_TextElement", true)) {
												String message = WSClient.getElementValue(fetchHKTaskRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The text displayed in the response is    :     "
																+ message + "</b>");

											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Test(groups = { "fullRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3176() {

		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3176";
			WSClient.startTest(testName,
					"Verify that a new task sheets are generated for each floor involved when the Fetch Housekeeping Tasks call is submitted with the criteria where the respective rooms are associated to multiple floors",
					"fullRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											WSClient.setData("{var_taskCode}",
													OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
											WSClient.setData("{var_sortBy}", "FLOOR");
											HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
													HTNGLib.getInterfaceFromAddress());
											String fetchHKTaskReq = WSClient
													.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_16");
											String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

											if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
													"FetchHouseKeepingTaskResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {

												/**
												 * Verifying that the Tasksheet
												 * record is inserted
												 */
												// LinkedHashMap<String,String>
												// count=new
												// LinkedHashMap<String,String>();

												/**
												 * Validating the
												 * TaskDate,TaskCode,
												 * TaskSheetNumber,
												 * TotalRoomsInSheet,
												 * TaskInstruction details
												 * obtained from the response
												 * against the database details
												 */
												List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath2 = new HashMap<String, String>();
												xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
														"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
												String query = WSClient.getQuery("QS_04");
												db2 = WSClient.getDBRows(query);

												actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
														false, XMLType.RESPONSE);

												for (int i = 0; i < actualValues2.size(); ++i) {

													LinkedHashMap<String, String> exp = db2.get(i);

													exp.put("TotalRoomsInSheet1",
															WSClient.getDBRows(WSClient.getQuery("QS_34")).size() + "");

												}
												WSAssert.assertEquals(actualValues2, db2, false);

												/**
												 * Checking if the Room Numbers
												 * displayed in the response
												 * message under each of the
												 * Task Sheet Details section
												 * are correctly matching with
												 * the room numbers resulting
												 * from the response of the
												 * database
												 */

												List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
												List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
												HashMap<String, String> xpath3 = new HashMap<String, String>();
												xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
														"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
												xpath3.put("TaskSheets_TasksheetDetails_Floor",
														"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
												String query1 = WSClient.getQuery("QS_34");
												db3 = WSClient.getDBRows(query1);
												actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
														false, XMLType.RESPONSE);
												WSAssert.assertEquals(actualValues3, db3, false);

											}

											if (WSAssert.assertIfElementExists(fetchHKTaskRes,
													"Result_Text_TextElement", true)) {
												String message = WSClient.getElementValue(fetchHKTaskRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>" + "The text displayed in the response is    :     "
																+ message + "</b>");

											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3154() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3154";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated through the FetchHousekeepingTask call has the credits calclated correctly",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode", "Rooms" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "Dirty");
				WSClient.setData("{var_roomStatus}", "DI");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type
								roomNumber = CreateRoom.createRoom("DS_36");
								// roomNumber=OperaPropConfig.getDataSetForCode("Rooms","DS_13");
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);

									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String setHousekeepingRoomStatusReq1 = WSClient
													.createSOAPMessage("SetHousekeepingRoomStatus", "DS_06");
											String setHousekeepingRoomStatusRes1 = WSClient
													.processSOAPMessage(setHousekeepingRoomStatusReq1);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
													"SetHousekeepingRoomStatusRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully changed the status of room.</b>");
												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_03");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalCreditsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1", WSClient
																.getDBRow(WSClient.getQuery("QS_12")).get("COUNT"));
														exp.put("TotalCreditsInSheet1",
																WSClient.getDBRow(WSClient.getQuery("QS_36"))
																.get("TotalCredits1"));

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													xpath3.put("TaskSheets_TasksheetDetails_Credit",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_35");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to change status of room ");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3161() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3161";
			WSClient.startTest(testName,
					"Verify that only the Pickup rooms tagged to an existing Task sheet are being retrieved onto the response when fetch request is submitted with the Room Status as \"Pickup\" via FetchHouseKeepingTasks call",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "Pickup");
				WSClient.setData("{var_roomStatus}", "PU");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String setHousekeepingRoomStatusReq1 = WSClient
													.createSOAPMessage("SetHousekeepingRoomStatus", "DS_03");
											String setHousekeepingRoomStatusRes1 = WSClient
													.processSOAPMessage(setHousekeepingRoomStatusReq1);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
													"SetHousekeepingRoomStatusRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully changed the status of room.</b>");
												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));

												String autoGenerateTaskSheetsReq = WSClient
														.createSOAPMessage("AutoGenerateTaskSheets", "DS_01");
												String autoGenerateTaskSheetsRes = WSClient
														.processSOAPMessage(autoGenerateTaskSheetsReq);
												if (WSAssert.assertIfElementExists(autoGenerateTaskSheetsRes,
														"AutoGenerateTaskSheetsRS_Success", true)) {
													WSClient.writeToReport(LogStatus.INFO,
															"<b>Succesfully generated tasksheet</b>");
													HTNGLib.setHTNGHeader(OPERALib.getUserName(),
															OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
													String fetchHKTaskReq = WSClient
															.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_17");
													String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

													if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
															"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
															"SUCCESS", false)) {

														/**
														 * Verifying that the
														 * Tasksheet record is
														 * inserted
														 */
														// LinkedHashMap<String,String>
														// count=new
														// LinkedHashMap<String,String>();

														/**
														 * Validating the
														 * TaskDate,TaskCode,
														 * TaskSheetNumber,
														 * TotalRoomsInSheet,
														 * TaskInstruction
														 * details obtained from
														 * the response against
														 * the database details
														 */
														List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
														List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
														HashMap<String, String> xpath2 = new HashMap<String, String>();
														xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");

														String query = WSClient.getQuery("QS_04");
														db2 = WSClient.getDBRows(query);

														actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																xpath2, false, XMLType.RESPONSE);

														WSAssert.assertEquals(actualValues2, db2, false);

														/**
														 * Checking if the Room
														 * Numbers displayed in
														 * the response message
														 * under each of the
														 * Task Sheet Details
														 * section are correctly
														 * matching with the
														 * room numbers
														 * resulting from the
														 * response of the
														 * database
														 */

														List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
														List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
														HashMap<String, String> xpath3 = new HashMap<String, String>();
														xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
																"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
														String query1 = WSClient.getQuery("QS_37");
														db3 = WSClient.getDBRows(query1);
														actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																xpath3, false, XMLType.RESPONSE);
														WSAssert.assertEquals(actualValues3, db3, false);

													}

													if (WSAssert.assertIfElementExists(fetchHKTaskRes,
															"Result_Text_TextElement", true)) {
														String message = WSClient.getElementValue(fetchHKTaskRes,
																"Result_Text_TextElement", XMLType.RESPONSE);
														WSClient.writeToReport(LogStatus.INFO,
																"<b>" + "The text displayed in the response is    :     "
																		+ message + "</b>");

													}

												}
											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to auto-generate task code");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Unable to change status of room ");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "targetedRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3168() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3168";
			WSClient.startTest(testName,
					"Verify that only the Dirty Occupied rooms tagged to an existing Task sheet are being retrieved onto the response when fetch request is submitted with the Room Status as Dirty and FO Status as Occupied through the FetchHouseKeepingTasks call",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "Dirty");
				WSClient.setData("{var_roomStatus}", "DI");
				WSClient.setData("{var_fStatus}", "Occupied");
				WSClient.setData("{var_foStatus}", "OCC");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");

											String checkInReq = WSClient.createSOAPMessage("CheckinReservation",
													"DS_01");
											String checkInRes = WSClient.processSOAPMessage(checkInReq);

											if (WSAssert.assertIfElementExists(checkInRes,
													"CheckinReservationRS_Success", true)) {

												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully checked in the reservation.</b>");

												String setHousekeepingRoomStatusReq1 = WSClient
														.createSOAPMessage("SetHousekeepingRoomStatus", "DS_06");
												String setHousekeepingRoomStatusRes1 = WSClient
														.processSOAPMessage(setHousekeepingRoomStatusReq1);

												if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
														"SetHousekeepingRoomStatusRS_Success", true)) {
													WSClient.writeToReport(LogStatus.INFO,
															"<b>Succesfully changed the status of room.</b>");
													WSClient.setData("{var_taskCode}",
															OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));

													String autoGenerateTaskSheetsReq = WSClient
															.createSOAPMessage("AutoGenerateTaskSheets", "DS_01");
													String autoGenerateTaskSheetsRes = WSClient
															.processSOAPMessage(autoGenerateTaskSheetsReq);
													if (WSAssert.assertIfElementExists(autoGenerateTaskSheetsRes,
															"AutoGenerateTaskSheetsRS_Success", true)) {
														WSClient.writeToReport(LogStatus.INFO,
																"<b>Succesfully generated tasksheet</b>");
														HTNGLib.setHTNGHeader(OPERALib.getUserName(),
																OPERALib.getPassword(),
																HTNGLib.getInterfaceFromAddress());
														String fetchHKTaskReq = WSClient.createSOAPMessage(
																"HTNGExtFetchHousekeepingTask", "DS_20");
														String fetchHKTaskRes = WSClient
																.processSOAPMessage(fetchHKTaskReq);

														if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
																"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
																"SUCCESS", false)) {

															/**
															 * Verifying that
															 * the Tasksheet
															 * record is
															 * inserted
															 */
															// LinkedHashMap<String,String>
															// count=new
															// LinkedHashMap<String,String>();

															/**
															 * Validating the
															 * TaskDate,TaskCode
															 * ,TaskSheetNumber,
															 * TotalRoomsInSheet
															 * ,TaskInstruction
															 * details obtained
															 * from the response
															 * against the
															 * database details
															 */
															List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
															List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
															HashMap<String, String> xpath2 = new HashMap<String, String>();
															xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
																	"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
															xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
																	"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
															xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
																	"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");

															String query = WSClient.getQuery("QS_04");
															db2 = WSClient.getDBRows(query);

															actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																	xpath2, false, XMLType.RESPONSE);

															WSAssert.assertEquals(actualValues2, db2, false);

															/**
															 * Checking if the
															 * Room Numbers
															 * displayed in the
															 * response message
															 * under each of the
															 * Task Sheet
															 * Details section
															 * are correctly
															 * matching with the
															 * room numbers
															 * resulting from
															 * the response of
															 * the database
															 */

															List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
															List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
															HashMap<String, String> xpath3 = new HashMap<String, String>();
															xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
																	"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
															String query1 = WSClient.getQuery("QS_40");
															db3 = WSClient.getDBRows(query1);
															actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																	xpath3, false, XMLType.RESPONSE);
															WSAssert.assertEquals(actualValues3, db3, false);

														}

														if (WSAssert.assertIfElementExists(fetchHKTaskRes,
																"Result_Text_TextElement", true)) {
															String message = WSClient.getElementValue(fetchHKTaskRes,
																	"Result_Text_TextElement", XMLType.RESPONSE);
															WSClient.writeToReport(LogStatus.INFO,
																	"<b>" + "The text displayed in the response is    :     "
																			+ message + "</b>");

														}

													}
												} else {
													WSClient.writeToReport(LogStatus.WARNING,
															"Unable to auto-generate task code");
												}
											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to change status of room ");
											}
										}

										else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to checkin reservation ");

										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");

									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3162() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3162";
			WSClient.startTest(testName,
					"Verify that only the Clean rooms tagged to an existing Task sheet are being retrieved onto the response when fetch request is submitted with the Room Status as Clean via FetchHouseKeepingTasks call",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "Clean");
				WSClient.setData("{var_roomStatus}", "CL");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String setHousekeepingRoomStatusReq1 = WSClient
													.createSOAPMessage("SetHousekeepingRoomStatus", "DS_02");
											String setHousekeepingRoomStatusRes1 = WSClient
													.processSOAPMessage(setHousekeepingRoomStatusReq1);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
													"SetHousekeepingRoomStatusRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully changed the status of room.</b>");
												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));

												String autoGenerateTaskSheetsReq = WSClient
														.createSOAPMessage("AutoGenerateTaskSheets", "DS_01");
												String autoGenerateTaskSheetsRes = WSClient
														.processSOAPMessage(autoGenerateTaskSheetsReq);
												if (WSAssert.assertIfElementExists(autoGenerateTaskSheetsRes,
														"AutoGenerateTaskSheetsRS_Success", true)) {
													WSClient.writeToReport(LogStatus.INFO,
															"<b>Succesfully generated tasksheet</b>");
													HTNGLib.setHTNGHeader(OPERALib.getUserName(),
															OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
													String fetchHKTaskReq = WSClient
															.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_17");
													String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

													if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
															"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
															"SUCCESS", false)) {

														/**
														 * Verifying that the
														 * Tasksheet record is
														 * inserted
														 */
														// LinkedHashMap<String,String>
														// count=new
														// LinkedHashMap<String,String>();

														/**
														 * Validating the
														 * TaskDate,TaskCode,
														 * TaskSheetNumber,
														 * TotalRoomsInSheet,
														 * TaskInstruction
														 * details obtained from
														 * the response against
														 * the database details
														 */
														List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
														List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
														HashMap<String, String> xpath2 = new HashMap<String, String>();
														xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");

														String query = WSClient.getQuery("QS_04");
														db2 = WSClient.getDBRows(query);

														actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																xpath2, false, XMLType.RESPONSE);

														WSAssert.assertEquals(actualValues2, db2, false);

														/**
														 * Checking if the Room
														 * Numbers displayed in
														 * the response message
														 * under each of the
														 * Task Sheet Details
														 * section are correctly
														 * matching with the
														 * room numbers
														 * resulting from the
														 * response of the
														 * database
														 */

														List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
														List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
														HashMap<String, String> xpath3 = new HashMap<String, String>();
														xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
																"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
														String query1 = WSClient.getQuery("QS_37");
														db3 = WSClient.getDBRows(query1);
														actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																xpath3, false, XMLType.RESPONSE);
														WSAssert.assertEquals(actualValues3, db3, false);

													}

													if (WSAssert.assertIfElementExists(fetchHKTaskRes,
															"Result_Text_TextElement", true)) {
														String message = WSClient.getElementValue(fetchHKTaskRes,
																"Result_Text_TextElement", XMLType.RESPONSE);
														WSClient.writeToReport(LogStatus.INFO,
																"<b>" + "The text displayed in the response is    :     "
																		+ message + "</b>");

													}

												}
											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to auto-generate task code");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Unable to change status of room ");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3163() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3163";
			WSClient.startTest(testName,
					"Verify that only the Out Of Order rooms tagged to an existing Task sheet are being retrieved onto the response when fetch request is submitted with the Room Status as Out Of Order via FetchHouseKeepingTasks call",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "OutOfOrder");
				WSClient.setData("{var_roomStatus}", "OO");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String setHousekeepingRoomStatusReq1 = WSClient
													.createSOAPMessage("SetHousekeepingRoomStatus", "DS_07");
											String setHousekeepingRoomStatusRes1 = WSClient
													.processSOAPMessage(setHousekeepingRoomStatusReq1);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
													"SetHousekeepingRoomStatusRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully changed the status of room.</b>");
												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));

												String autoGenerateTaskSheetsReq = WSClient
														.createSOAPMessage("AutoGenerateTaskSheets", "DS_01");
												String autoGenerateTaskSheetsRes = WSClient
														.processSOAPMessage(autoGenerateTaskSheetsReq);
												if (WSAssert.assertIfElementExists(autoGenerateTaskSheetsRes,
														"AutoGenerateTaskSheetsRS_Success", true)) {
													WSClient.writeToReport(LogStatus.INFO,
															"<b>Succesfully generated tasksheet</b>");
													HTNGLib.setHTNGHeader(OPERALib.getUserName(),
															OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
													String fetchHKTaskReq = WSClient
															.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_17");
													String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

													if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
															"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
															"SUCCESS", false)) {

														/**
														 * Verifying that the
														 * Tasksheet record is
														 * inserted
														 */
														// LinkedHashMap<String,String>
														// count=new
														// LinkedHashMap<String,String>();

														/**
														 * Validating the
														 * TaskDate,TaskCode,
														 * TaskSheetNumber,
														 * TotalRoomsInSheet,
														 * TaskInstruction
														 * details obtained from
														 * the response against
														 * the database details
														 */
														List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
														List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
														HashMap<String, String> xpath2 = new HashMap<String, String>();
														xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");

														String query = WSClient.getQuery("QS_04");
														db2 = WSClient.getDBRows(query);

														actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																xpath2, false, XMLType.RESPONSE);

														WSAssert.assertEquals(actualValues2, db2, false);

														/**
														 * Checking if the Room
														 * Numbers displayed in
														 * the response message
														 * under each of the
														 * Task Sheet Details
														 * section are correctly
														 * matching with the
														 * room numbers
														 * resulting from the
														 * response of the
														 * database
														 */

														List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
														List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
														HashMap<String, String> xpath3 = new HashMap<String, String>();
														xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
																"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
														String query1 = WSClient.getQuery("QS_37");
														db3 = WSClient.getDBRows(query1);
														actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																xpath3, false, XMLType.RESPONSE);
														WSAssert.assertEquals(actualValues3, db3, false);

													}

													if (WSAssert.assertIfElementExists(fetchHKTaskRes,
															"Result_Text_TextElement", true)) {
														String message = WSClient.getElementValue(fetchHKTaskRes,
																"Result_Text_TextElement", XMLType.RESPONSE);
														WSClient.writeToReport(LogStatus.INFO,
																"<b>" + "The text displayed in the response is    :     "
																		+ message + "</b>");

													}

												}
											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to auto-generate task code");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Unable to change status of room ");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3164() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3164";
			WSClient.startTest(testName,
					"Verify that only the Out Of Service rooms tagged to an existing Task sheet are being retrieved onto the response when fetch request is submitted with the Room Status as Out Of Service via FetchHouseKeepingTasks call",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "OutOfService");
				WSClient.setData("{var_roomStatus}", "OS");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String setHousekeepingRoomStatusReq1 = WSClient
													.createSOAPMessage("SetHousekeepingRoomStatus", "DS_08");
											String setHousekeepingRoomStatusRes1 = WSClient
													.processSOAPMessage(setHousekeepingRoomStatusReq1);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
													"SetHousekeepingRoomStatusRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully changed the status of room.</b>");
												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));

												String autoGenerateTaskSheetsReq = WSClient
														.createSOAPMessage("AutoGenerateTaskSheets", "DS_01");
												String autoGenerateTaskSheetsRes = WSClient
														.processSOAPMessage(autoGenerateTaskSheetsReq);
												if (WSAssert.assertIfElementExists(autoGenerateTaskSheetsRes,
														"AutoGenerateTaskSheetsRS_Success", true)) {
													WSClient.writeToReport(LogStatus.INFO,
															"<b>Succesfully generated tasksheet</b>");
													HTNGLib.setHTNGHeader(OPERALib.getUserName(),
															OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
													String fetchHKTaskReq = WSClient
															.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_17");
													String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

													if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
															"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
															"SUCCESS", false)) {

														/**
														 * Verifying that the
														 * Tasksheet record is
														 * inserted
														 */
														// LinkedHashMap<String,String>
														// count=new
														// LinkedHashMap<String,String>();

														/**
														 * Validating the
														 * TaskDate,TaskCode,
														 * TaskSheetNumber,
														 * TotalRoomsInSheet,
														 * TaskInstruction
														 * details obtained from
														 * the response against
														 * the database details
														 */
														List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
														List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
														HashMap<String, String> xpath2 = new HashMap<String, String>();
														xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");

														String query = WSClient.getQuery("QS_04");
														db2 = WSClient.getDBRows(query);

														actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																xpath2, false, XMLType.RESPONSE);

														WSAssert.assertEquals(actualValues2, db2, false);

														/**
														 * Checking if the Room
														 * Numbers displayed in
														 * the response message
														 * under each of the
														 * Task Sheet Details
														 * section are correctly
														 * matching with the
														 * room numbers
														 * resulting from the
														 * response of the
														 * database
														 */

														List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
														List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
														HashMap<String, String> xpath3 = new HashMap<String, String>();
														xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
																"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
														String query1 = WSClient.getQuery("QS_37");
														db3 = WSClient.getDBRows(query1);
														actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																xpath3, false, XMLType.RESPONSE);
														WSAssert.assertEquals(actualValues3, db3, false);

													}

													if (WSAssert.assertIfElementExists(fetchHKTaskRes,
															"Result_Text_TextElement", true)) {
														String message = WSClient.getElementValue(fetchHKTaskRes,
																"Result_Text_TextElement", XMLType.RESPONSE);
														WSClient.writeToReport(LogStatus.INFO,
																"<b>" + "The text displayed in the response is    :     "
																		+ message + "</b>");

													}

												}
											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to auto-generate task code");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Unable to change status of room ");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3160() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3160";
			WSClient.startTest(testName,
					"Verify that only the Inspected rooms tagged to an existing Task sheet are being retrieved onto the response when fetch request is submitted with the Room Status as Inspected via FetchHouseKeepingTasks call",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "Inspected");
				WSClient.setData("{var_roomStatus}", "IP");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											WSClient.setData("{var_taskCode}",
													OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));

											String autoGenerateTaskSheetsReq = WSClient
													.createSOAPMessage("AutoGenerateTaskSheets", "DS_01");
											String autoGenerateTaskSheetsRes = WSClient
													.processSOAPMessage(autoGenerateTaskSheetsReq);
											if (WSAssert.assertIfElementExists(autoGenerateTaskSheetsRes,
													"AutoGenerateTaskSheetsRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully generated tasksheet</b>");
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_17");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");

													String query = WSClient.getQuery("QS_04");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_37");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Unable to auto-generate task code");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3159() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3159";
			WSClient.startTest(testName,
					"Verify that only the Dirty rooms tagged to an existing Task sheet are being retrieved onto the response when fetch request is submitted with the Room Status as Dirty via FetchHouseKeepingTasks call",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "Dirty");
				WSClient.setData("{var_roomStatus}", "DI");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String setHousekeepingRoomStatusReq1 = WSClient
													.createSOAPMessage("SetHousekeepingRoomStatus", "DS_06");
											String setHousekeepingRoomStatusRes1 = WSClient
													.processSOAPMessage(setHousekeepingRoomStatusReq1);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
													"SetHousekeepingRoomStatusRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully changed the status of room.</b>");
												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));

												String autoGenerateTaskSheetsReq = WSClient
														.createSOAPMessage("AutoGenerateTaskSheets", "DS_01");
												String autoGenerateTaskSheetsRes = WSClient
														.processSOAPMessage(autoGenerateTaskSheetsReq);
												if (WSAssert.assertIfElementExists(autoGenerateTaskSheetsRes,
														"AutoGenerateTaskSheetsRS_Success", true)) {
													WSClient.writeToReport(LogStatus.INFO,
															"<b>Succesfully generated tasksheet</b>");
													HTNGLib.setHTNGHeader(OPERALib.getUserName(),
															OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
													String fetchHKTaskReq = WSClient
															.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_17");
													String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

													if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
															"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
															"SUCCESS", false)) {

														/**
														 * Verifying that the
														 * Tasksheet record is
														 * inserted
														 */
														// LinkedHashMap<String,String>
														// count=new
														// LinkedHashMap<String,String>();

														/**
														 * Validating the
														 * TaskDate,TaskCode,
														 * TaskSheetNumber,
														 * TotalRoomsInSheet,
														 * TaskInstruction
														 * details obtained from
														 * the response against
														 * the database details
														 */
														List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
														List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
														HashMap<String, String> xpath2 = new HashMap<String, String>();
														xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");

														String query = WSClient.getQuery("QS_04");
														db2 = WSClient.getDBRows(query);

														actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																xpath2, false, XMLType.RESPONSE);

														WSAssert.assertEquals(actualValues2, db2, false);

														/**
														 * Checking if the Room
														 * Numbers displayed in
														 * the response message
														 * under each of the
														 * Task Sheet Details
														 * section are correctly
														 * matching with the
														 * room numbers
														 * resulting from the
														 * response of the
														 * database
														 */

														List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
														List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
														HashMap<String, String> xpath3 = new HashMap<String, String>();
														xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
																"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
														String query1 = WSClient.getQuery("QS_37");
														db3 = WSClient.getDBRows(query1);
														actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																xpath3, false, XMLType.RESPONSE);
														WSAssert.assertEquals(actualValues3, db3, false);

													}

													if (WSAssert.assertIfElementExists(fetchHKTaskRes,
															"Result_Text_TextElement", true)) {
														String message = WSClient.getElementValue(fetchHKTaskRes,
																"Result_Text_TextElement", XMLType.RESPONSE);
														WSClient.writeToReport(LogStatus.INFO,
																"<b>" + "The text displayed in the response is    :     "
																		+ message + "</b>");

													}

												}
											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to auto-generate task code");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Unable to change status of room ");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	//
	// @Test(groups = { "minimumRegression", "FetchHouseKeepingTask",
	// "HTNG2008BExt", "HTNG" })
	//
	// public void fetchHouseKeepingTask_Ext_3165() {
	// String profileID = "", roomNumber = "";
	// try {
	//
	// String testName = "fetchHousekeepingTask_Ext_3165";
	// WSClient.startTest(testName,
	// "Verify that only the vacant rooms tagged to an existing Task sheet are
	// being retrieved onto the response when fetch request is submitted with
	// the FO Status as Vacant via FetchHouseKeepingTasks call",
	// "minimumRegression");
	//
	// if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode"
	// })) {
	// String parameter1 = "", parameter2 = "";
	// String interfaceName = HTNGLib.getHTNGInterface();
	// String resortOperaValue = OPERALib.getResort();
	// String resortExtValue = HTNGLib.getExtResort(resortOperaValue,
	// interfaceName);
	// WSClient.setData("{var_extResort}", resortExtValue);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_chain}", OPERALib.getChain());
	// WSClient.setData("{var_fStatus}", "Vacant");
	// WSClient.setData("{var_foStatus}", "VAC");
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	//
	// WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :
	// FACILITY_MANAGEMENT****</b>");
	// WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
	// WSClient.setData("{var_settingValue}", "Y");
	//
	// if
	// (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
	// parameter1 =
	// ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
	//
	// if (!parameter1.equals("error")) {
	//
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
	// WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
	// WSClient.setData("{var_settingValue}", "Y");
	//
	// if
	// (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
	// parameter2 =
	// ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
	//
	// if (!parameter2.equals("error")) {
	// if (profileID.equals(""))
	// profileID = CreateProfile.createProfile("DS_01");
	// if (!profileID.equals("error")) {
	// WSClient.setData("{var_profileId}", profileID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID +
	// "</b>");
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_roomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_payment}",
	// OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	//
	// // Prerequisite 2 Create Reservation
	// HashMap<String, String> resv =
	// CreateReservation.createReservation("DS_04");
	// String resvID = resv.get("reservationId");
	//
	// if (!resvID.equals("error")) {
	//
	// WSClient.setData("{var_resvId}", resvID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID +
	// "</b>");
	// // Prerequisite 3: Fetching available Hotel
	// // rooms with
	// // room type
	//
	// String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms",
	// "DS_03");
	// String fetchHotelRoomsRes =
	// WSClient.processSOAPMessage(fetchHotelRoomsReq);
	//
	// if (WSAssert.assertIfElementExists(fetchHotelRoomsRes,
	// "FetchHotelRoomsRS_Success",
	// true)
	// && WSAssert.assertIfElementExists(fetchHotelRoomsRes,
	// "FetchHotelRoomsRS_HotelRooms_Room", true)) {
	//
	// roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
	// "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
	//
	// } else {
	//
	// // Prerequisite 4: Creating a room to assign
	//
	// String createRoomReq = WSClient.createSOAPMessage("CreateRoom",
	// "RoomMaint");
	// String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
	//
	// if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success",
	// true)) {
	//
	// roomNumber = WSClient.getElementValue(createRoomReq,
	// "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
	//
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create
	// room");
	// }
	// }
	// if (!roomNumber.equals("")) {
	// WSClient.setData("{var_roomNumber}", roomNumber);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create
	// room.</b>");
	// // Prerequisite 5: Changing the room status
	// // to inspected
	// // to assign the room for checking in
	//
	// String setHousekeepingRoomStatusReq = WSClient
	// .createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
	// String setHousekeepingRoomStatusRes = WSClient
	// .processSOAPMessage(setHousekeepingRoomStatusReq);
	//
	// if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
	// "SetHousekeepingRoomStatusRS_Success", true)) {
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>Succesfully changed the status of room.</b>");
	//
	// // Prerequisite 6: Assign Room
	//
	// String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
	// String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);
	//
	// if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
	// true)) {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned
	// room.</b>");
	// WSClient.setData("{var_taskCode}",
	// OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
	//
	// String
	// autoGenerateTaskSheetsReq=WSClient.createSOAPMessage("AutoGenerateTaskSheets",
	// "DS_01");
	// String autoGenerateTaskSheetsRes =
	// WSClient.processSOAPMessage(autoGenerateTaskSheetsReq);
	// if(WSAssert.assertIfElementExists(autoGenerateTaskSheetsRes,
	// "AutoGenerateTaskSheetsRS_Success", true)) {
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>Succesfully generated tasksheet</b>");
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// HTNGLib.getInterfaceFromAddress());
	// String fetchHKTaskReq = WSClient
	// .createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_18");
	// String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);
	//
	// if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
	// "FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
	// "SUCCESS", false)) {
	//
	// /**
	// * Verifying that the
	// * Tasksheet record is
	// * inserted
	// */
	// // LinkedHashMap<String,String>
	// // count=new
	// // LinkedHashMap<String,String>();
	//
	// /**
	// * Validating the
	// * TaskDate,TaskCode,TaskSheetNumber,TotalRoomsInSheet,TaskInstruction
	// * details obtained from the
	// * response against the
	// * database details
	// */
	// List<LinkedHashMap<String, String>> db2 = new
	// ArrayList<LinkedHashMap<String, String>>();
	// List<LinkedHashMap<String, String>> actualValues2 = new
	// ArrayList<LinkedHashMap<String, String>>();
	// HashMap<String, String> xpath2 = new HashMap<String, String>();
	// xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
	// "FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
	// xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
	// "FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
	// xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
	// "FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
	//
	// String query = WSClient.getQuery("QS_04");
	// db2 = WSClient.getDBRows(query);
	//
	// actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
	// false, XMLType.RESPONSE);
	//
	// WSAssert.assertEquals(actualValues2, db2, false);
	//
	// /**
	// * Checking if the Room
	// * Numbers displayed in the
	// * response message under
	// * each of the Task Sheet
	// * Details section are
	// * correctly matching with
	// * the room numbers
	// * resulting from the
	// * response of the database
	// */
	//
	// List<LinkedHashMap<String, String>> db3 = new
	// ArrayList<LinkedHashMap<String, String>>();
	// List<LinkedHashMap<String, String>> actualValues3 = new
	// ArrayList<LinkedHashMap<String, String>>();
	// HashMap<String, String> xpath3 = new HashMap<String, String>();
	// xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
	// "FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
	// String query1 = WSClient.getQuery("QS_38");
	// db3 = WSClient.getDBRows(query1);
	// actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
	// false, XMLType.RESPONSE);
	// WSAssert.assertEquals(actualValues3, db3, false);
	//
	// }
	//
	// if (WSAssert.assertIfElementExists(fetchHKTaskRes,
	// "Result_Text_TextElement", true)) {
	// String message = WSClient.getElementValue(fetchHKTaskRes,
	// "Result_Text_TextElement", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>" + "The text displayed in the response is : "
	// + message + "</b>");
	//
	// }
	//
	// }
	// }else{
	// WSClient.writeToReport(LogStatus.WARNING,"Unable to auto-generate task
	// code");
	// }
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
	// }
	//
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of
	// room ");
	// }
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
	// }
	//
	// }
	// }
	// }
	//
	// }
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	//
	// try {
	// if (!WSClient.getData("{var_resvId}").equals(""))
	// CancelReservation.cancelReservation("DS_02");
	// if (!WSClient.getData("{var_roomNumber}").equals(""))
	// SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// }
	//
	// @Test(groups = { "minimumRegression", "FetchHouseKeepingTask",
	// "HTNG2008BExt", "HTNG" })
	//
	// public void fetchHouseKeepingTask_Ext_3166() {
	// String profileID = "", roomNumber = "";
	// try {
	//
	// String testName = "fetchHousekeepingTask_Ext_3166";
	// WSClient.startTest(testName,
	// "Verify that only the Occupied rooms tagged to an existing Task sheet are
	// being retrieved onto the response when fetch request is submitted with
	// the FO Status as Occupied via FetchHouseKeepingTasks call",
	// "minimumRegression");
	//
	// if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode"
	// })) {
	// String parameter1 = "", parameter2 = "";
	// String interfaceName = HTNGLib.getHTNGInterface();
	// String resortOperaValue = OPERALib.getResort();
	// String resortExtValue = HTNGLib.getExtResort(resortOperaValue,
	// interfaceName);
	// WSClient.setData("{var_extResort}", resortExtValue);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_chain}", OPERALib.getChain());
	// WSClient.setData("{var_fStatus}", "Occupied");
	// WSClient.setData("{var_foStatus}", "OCC");
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	//
	// WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :
	// FACILITY_MANAGEMENT****</b>");
	// WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
	// WSClient.setData("{var_settingValue}", "Y");
	//
	// if
	// (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
	// parameter1 =
	// ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
	//
	// if (!parameter1.equals("error")) {
	//
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
	// WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
	// WSClient.setData("{var_settingValue}", "Y");
	//
	// if
	// (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
	// parameter2 =
	// ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
	//
	// if (!parameter2.equals("error")) {
	// if (profileID.equals(""))
	// profileID = CreateProfile.createProfile("DS_01");
	// if (!profileID.equals("error")) {
	// WSClient.setData("{var_profileId}", profileID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID +
	// "</b>");
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_roomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_RoomType}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_payment}",
	// OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	//
	// // Prerequisite 2 Create Reservation
	// HashMap<String, String> resv =
	// CreateReservation.createReservation("DS_04");
	// String resvID = resv.get("reservationId");
	//
	// if (!resvID.equals("error")) {
	//
	// WSClient.setData("{var_resvId}", resvID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID +
	// "</b>");
	// // Prerequisite 3: Fetching available Hotel
	// // rooms with
	// // room type
	//
	// String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms",
	// "DS_03");
	// String fetchHotelRoomsRes =
	// WSClient.processSOAPMessage(fetchHotelRoomsReq);
	//
	// if (WSAssert.assertIfElementExists(fetchHotelRoomsRes,
	// "FetchHotelRoomsRS_Success",
	// true)
	// && WSAssert.assertIfElementExists(fetchHotelRoomsRes,
	// "FetchHotelRoomsRS_HotelRooms_Room", true)) {
	//
	// roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
	// "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
	//
	// } else {
	//
	// // Prerequisite 4: Creating a room to assign
	//
	// String createRoomReq = WSClient.createSOAPMessage("CreateRoom",
	// "RoomMaint");
	// String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
	//
	// if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success",
	// true)) {
	//
	// roomNumber = WSClient.getElementValue(createRoomReq,
	// "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
	//
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create
	// room");
	// }
	// }
	// if (!roomNumber.equals("")) {
	// WSClient.setData("{var_roomNumber}", roomNumber);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create
	// room.</b>");
	// // Prerequisite 5: Changing the room status
	// // to inspected
	// // to assign the room for checking in
	//
	// String setHousekeepingRoomStatusReq = WSClient
	// .createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
	// String setHousekeepingRoomStatusRes = WSClient
	// .processSOAPMessage(setHousekeepingRoomStatusReq);
	//
	// if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
	// "SetHousekeepingRoomStatusRS_Success", true)) {
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>Succesfully changed the status of room.</b>");
	//
	// // Prerequisite 6: Assign Room
	//
	// String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
	// String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);
	//
	// if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
	// true)) {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned
	// room.</b>");
	//
	//
	// String checkInReq = WSClient.createSOAPMessage("CheckinReservation",
	// "DS_01");
	// String checkInRes = WSClient.processSOAPMessage(checkInReq);
	//
	// if (WSAssert.assertIfElementExists(checkInRes,
	// "CheckinReservationRS_Success", true)) {
	//
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>Succesfully checked in the reservation.</b>");
	//
	// WSClient.setData("{var_taskCode}",
	// OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
	//
	// String
	// autoGenerateTaskSheetsReq=WSClient.createSOAPMessage("AutoGenerateTaskSheets",
	// "DS_01");
	// String autoGenerateTaskSheetsRes =
	// WSClient.processSOAPMessage(autoGenerateTaskSheetsReq);
	// if(WSAssert.assertIfElementExists(autoGenerateTaskSheetsRes,
	// "AutoGenerateTaskSheetsRS_Success", true)) {
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>Succesfully generated tasksheet</b>");
	// HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// HTNGLib.getInterfaceFromAddress());
	// String fetchHKTaskReq = WSClient
	// .createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_18");
	// String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);
	//
	// if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
	// "FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
	// "SUCCESS", false)) {
	//
	// /**
	// * Verifying that the
	// * Tasksheet record is
	// * inserted
	// */
	// // LinkedHashMap<String,String>
	// // count=new
	// // LinkedHashMap<String,String>();
	//
	// /**
	// * Validating the
	// * TaskDate,TaskCode,TaskSheetNumber,TotalRoomsInSheet,TaskInstruction
	// * details obtained from the
	// * response against the
	// * database details
	// */
	// List<LinkedHashMap<String, String>> db2 = new
	// ArrayList<LinkedHashMap<String, String>>();
	// List<LinkedHashMap<String, String>> actualValues2 = new
	// ArrayList<LinkedHashMap<String, String>>();
	// HashMap<String, String> xpath2 = new HashMap<String, String>();
	// xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
	// "FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
	// xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
	// "FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
	// xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
	// "FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
	//
	// String query = WSClient.getQuery("QS_04");
	// db2 = WSClient.getDBRows(query);
	//
	// actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
	// false, XMLType.RESPONSE);
	//
	// WSAssert.assertEquals(actualValues2, db2, false);
	//
	// /**
	// * Checking if the Room
	// * Numbers displayed in the
	// * response message under
	// * each of the Task Sheet
	// * Details section are
	// * correctly matching with
	// * the room numbers
	// * resulting from the
	// * response of the database
	// */
	//
	// List<LinkedHashMap<String, String>> db3 = new
	// ArrayList<LinkedHashMap<String, String>>();
	// List<LinkedHashMap<String, String>> actualValues3 = new
	// ArrayList<LinkedHashMap<String, String>>();
	// HashMap<String, String> xpath3 = new HashMap<String, String>();
	// xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
	// "FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
	// String query1 = WSClient.getQuery("QS_38");
	// db3 = WSClient.getDBRows(query1);
	// actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
	// false, XMLType.RESPONSE);
	// WSAssert.assertEquals(actualValues3, db3, false);
	//
	// }
	//
	// if (WSAssert.assertIfElementExists(fetchHKTaskRes,
	// "Result_Text_TextElement", true)) {
	// String message = WSClient.getElementValue(fetchHKTaskRes,
	// "Result_Text_TextElement", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>" + "The text displayed in the response is : "
	// + message + "</b>");
	//
	// }
	//
	// }
	// else{
	// WSClient.writeToReport(LogStatus.WARNING,"Unable to auto-generate task
	// code");
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "Unable to checkin the room ");
	// }
	// }else {
	// WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
	// }
	//
	// }
	//
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of
	// room ");
	// }
	// } else {
	// WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
	// }
	//
	// }
	// }
	// }
	//
	// }
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	//
	// try {
	// if (!WSClient.getData("{var_resvId}").equals(""))
	// CancelReservation.cancelReservation("DS_02");
	// if (!WSClient.getData("{var_roomNumber}").equals(""))
	// SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// }
	//

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })
	public void fetchHouseKeepingTask_Ext_3155() {

		try {
			String profileID = "";
			String testName = "fetchHousekeepingTask_Ext_3155";
			WSClient.startTest(testName,
					"Verify that the Task Header details are correctly being retrieved onto the response through FetchHouseKeepingTask call",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");
							if (!resvID.equals("error")) {
								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type
								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);
								String roomNumber = " ";
								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/created room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in
									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);
									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");
										// Prerequisite 6: Assign Room
										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);
										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											WSClient.setData("{var_taskCode}",
													OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));

											String autoGenerateTaskSheetsReq = WSClient
													.createSOAPMessage("AutoGenerateTaskSheets", "DS_01");
											String autoGenerateTaskSheetsRes = WSClient
													.processSOAPMessage(autoGenerateTaskSheetsReq);
											if (WSAssert.assertIfElementExists(autoGenerateTaskSheetsRes,
													"AutoGenerateTaskSheetsRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully generated tasksheet</b>");

												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_19");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);
												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskInstruction",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TravelingCredit",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_39");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1",
																WSClient.getDBRows(WSClient.getQuery("QS_05")).size()
																+ "");

													}
													WSAssert.assertEquals(actualValues2, db2, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to auto-generate task code");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
							}

						}
					}

				}
			}
		}

		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "targetedRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3167() {
		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3167";
			WSClient.startTest(testName,
					"Verify that only the Dirty and Inspected rooms tagged to an existing Task sheet are being retrieved onto the response when fetch request is submitted with the Room Status as Dirty and Pickup via FetchHouseKeepingTasks call",
					"targetedRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				WSClient.setData("{var_rStatus}", "Dirty");
				WSClient.setData("{var_rStatus1}", "Inspected");

				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");

							if (!resvID.equals("error")) {

								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type

								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/create room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in

									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);

									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");

										// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											String setHousekeepingRoomStatusReq1 = WSClient
													.createSOAPMessage("SetHousekeepingRoomStatus", "DS_06");
											String setHousekeepingRoomStatusRes1 = WSClient
													.processSOAPMessage(setHousekeepingRoomStatusReq1);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes1,
													"SetHousekeepingRoomStatusRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully changed the status of room.</b>");
												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));

												String autoGenerateTaskSheetsReq = WSClient
														.createSOAPMessage("AutoGenerateTaskSheets", "DS_01");
												String autoGenerateTaskSheetsRes = WSClient
														.processSOAPMessage(autoGenerateTaskSheetsReq);
												if (WSAssert.assertIfElementExists(autoGenerateTaskSheetsRes,
														"AutoGenerateTaskSheetsRS_Success", true)) {
													WSClient.writeToReport(LogStatus.INFO,
															"<b>Succesfully generated tasksheet</b>");
													HTNGLib.setHTNGHeader(OPERALib.getUserName(),
															OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
													String fetchHKTaskReq = WSClient
															.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_21");
													String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

													if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
															"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
															"SUCCESS", false)) {

														/**
														 * Verifying that the
														 * Tasksheet record is
														 * inserted
														 */
														// LinkedHashMap<String,String>
														// count=new
														// LinkedHashMap<String,String>();

														/**
														 * Validating the
														 * TaskDate,TaskCode,
														 * TaskSheetNumber,
														 * TotalRoomsInSheet,
														 * TaskInstruction
														 * details obtained from
														 * the response against
														 * the database details
														 */
														List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
														List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
														HashMap<String, String> xpath2 = new HashMap<String, String>();
														xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
														xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
																"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");

														String query = WSClient.getQuery("QS_04");
														db2 = WSClient.getDBRows(query);

														actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																xpath2, false, XMLType.RESPONSE);

														WSAssert.assertEquals(actualValues2, db2, false);

														/**
														 * Checking if the Room
														 * Numbers displayed in
														 * the response message
														 * under each of the
														 * Task Sheet Details
														 * section are correctly
														 * matching with the
														 * room numbers
														 * resulting from the
														 * response of the
														 * database
														 */

														List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
														List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
														HashMap<String, String> xpath3 = new HashMap<String, String>();
														xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
																"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
														String query1 = WSClient.getQuery("QS_41");
														db3 = WSClient.getDBRows(query1);
														actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes,
																xpath3, false, XMLType.RESPONSE);
														WSAssert.assertEquals(actualValues3, db3, false);

													}

													if (WSAssert.assertIfElementExists(fetchHKTaskRes,
															"Result_Text_TextElement", true)) {
														String message = WSClient.getElementValue(fetchHKTaskRes,
																"Result_Text_TextElement", XMLType.RESPONSE);
														WSClient.writeToReport(LogStatus.INFO,
																"<b>" + "The text displayed in the response is    :     "
																		+ message + "</b>");

													}

												}
											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to auto-generate task code");
											}
										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Unable to change status of room ");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
									}

								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_roomNumber}").equals(""))
					SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })
	public void fetchHouseKeepingTask_Ext_3174() {

		try {
			String profileID = "";
			String testName = "fetchHousekeepingTask_Ext_3174";
			WSClient.startTest(testName,
					"Verify that all the task sheets available for the current business date are fetched when no task code is passed in the request of FetchHousekeepingTask",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");
							if (!resvID.equals("error")) {
								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type
								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);
								String roomNumber = " ";
								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/created room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in
									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);
									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");
										// Prerequisite 6: Assign Room
										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);
										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											WSClient.setData("{var_taskCode}",
													OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));

											String autoGenerateTaskSheetsReq = WSClient
													.createSOAPMessage("AutoGenerateTaskSheets", "DS_01");
											String autoGenerateTaskSheetsRes = WSClient
													.processSOAPMessage(autoGenerateTaskSheetsReq);
											if (WSAssert.assertIfElementExists(autoGenerateTaskSheetsRes,
													"AutoGenerateTaskSheetsRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully generated tasksheet</b>");

												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_22");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);
												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets");
													// xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
													// "FetchHouseKeepingTaskResponse_TaskSheets");
													String query = WSClient.getQuery("QS_42");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															true, XMLType.RESPONSE);

													WSAssert.assertEquals(actualValues2, db2, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Unable to auto-generate task code");
											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
										}

									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to change status of room ");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
			// logger.endExtentTest();
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })
	public void fetchHouseKeepingTask_Ext_3156() {

		try {
			String profileID = "";
			String testName = "fetchHousekeepingTask_Ext_3156";
			WSClient.startTest(testName,
					"Verify that the Task Sheet details are correctly being retrieved onto the response through FetchHouseKeepingTask call",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
							WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
							WSClient.setData("{var_sourceCode}",
									OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
							WSClient.setData("{VAR_MARKETCODE}",
									OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
							WSClient.setData("{var_payment}",
									OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
							// Prerequisite 2 Create Reservation
							HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
							String resvID = resv.get("reservationId");
							if (!resvID.equals("error")) {
								WSClient.setData("{var_resvId}", resvID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
								// Prerequisite 3: Fetching available Hotel
								// rooms with
								// room type
								String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
								String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);
								String roomNumber = " ";
								if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success",
										true)
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
								if (!roomNumber.equals("")) {
									WSClient.setData("{var_roomNumber}", roomNumber);
									WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully fetched/created room.</b>");
									// Prerequisite 5: Changing the room status
									// to inspected
									// to assign the room for checking in
									String setHousekeepingRoomStatusReq = WSClient
											.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
									String setHousekeepingRoomStatusRes = WSClient
											.processSOAPMessage(setHousekeepingRoomStatusReq);
									if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
											"SetHousekeepingRoomStatusRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully changed the status of room.</b>");
										// Prerequisite 6: Assign Room
										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
										String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);
										if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
												true)) {
											WSClient.writeToReport(LogStatus.INFO, "<b>Succesfully assigned room.</b>");
											WSClient.setData("{var_taskCode}",
													OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));

											String autoGenerateTaskSheetsReq = WSClient
													.createSOAPMessage("AutoGenerateTaskSheets", "DS_01");
											String autoGenerateTaskSheetsRes = WSClient
													.processSOAPMessage(autoGenerateTaskSheetsReq);
											if (WSAssert.assertIfElementExists(autoGenerateTaskSheetsRes,
													"AutoGenerateTaskSheetsRS_Success", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully generated tasksheet</b>");

												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_19");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);
												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet details
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													// xpath3.put("TaskSheets_TasksheetDetails_Floor",
													// "FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													xpath3.put("TaskSheets_TasksheetDetails_Credit",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													xpath3.put("TaskSheets_TasksheetDetails_TaskInstruction",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													xpath3.put("TaskSheets_TasksheetDetails_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_43");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											}

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
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_3166() {

		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_3166";
			WSClient.startTest(testName,
					"Verify that a new Task sheet is generated for scheduled taskcode through the FetchHousekeepingTask call for all the rooms with the requested task code.",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "", parameter3 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "N");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("N"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Setting the parameter :  ADDITIONAL_TASK_ASSIGNMENTS as null****</b>");
					WSClient.setData("{var_parameter}", "ADDITIONAL_TASK_ASSIGNMENTS");
					WSClient.setData("{var_settingValue}", "");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals(""))
						parameter3 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
					if (parameter3.equals("error")) {
						WSClient.setData("{var_taskCode}", OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
						String fetchHKTaskReq = WSClient.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_01");
						String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

						if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
								"FetchHouseKeepingTaskResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							/**
							 * Verifying that the Tasksheet record is inserted
							 */
							// LinkedHashMap<String,String>
							// count=new
							// LinkedHashMap<String,String>();

							/**
							 * Validating the TaskDate,TaskCode,TaskSheetNumber,
							 * TotalRoomsInSheet,TaskInstruction details
							 * obtained from the response against the database
							 * details
							 */

							/**
							 * Checking if the Room Numbers displayed in the
							 * response message under each of the Task Sheet
							 * Details section are correctly matching with the
							 * room numbers resulting from the response of the
							 * database
							 */

							List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
							List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
							HashMap<String, String> xpath3 = new HashMap<String, String>();
							xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
									"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
							String query1 = WSClient.getQuery("QS_44");
							db3 = WSClient.getDBRows(query1);
							actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3, false,
									XMLType.RESPONSE);
							WSAssert.assertEquals(actualValues3, db3, false);

						}

						if (WSAssert.assertIfElementExists(fetchHKTaskRes, "Result_Text_TextElement", true)) {
							String message = WSClient.getElementValue(fetchHKTaskRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>" + "The text displayed in the response is    :     " + message + "</b>");

						}

					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Test(groups = { "minimumRegression", "FetchHouseKeepingTask", "HTNG2008BExt", "HTNG" })

	public void fetchHouseKeepingTask_Ext_45215() {

		String profileID = "", roomNumber = "";
		try {

			String testName = "fetchHousekeepingTask_Ext_45215";
			WSClient.startTest(testName,
					"Verify that the task instruction submitted through the FetchHousekeepingTask service call is added to the generated task sheet details and task sheet header",
					"minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "TaskCode" })) {
				String parameter1 = "", parameter2 = "", parameter3 = "";
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_chain}", OPERALib.getChain());
				OPERALib.setOperaHeader(OPERALib.getUserName());

				WSClient.writeToReport(LogStatus.INFO, "<b>****Enabling the parameter :  FACILITY_MANAGEMENT****</b>");
				WSClient.setData("{var_parameter}", "FACILITY_MANAGEMENT");
				WSClient.setData("{var_settingValue}", "Y");

				if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
					parameter1 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

				if (!parameter1.equals("error")) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>****Enabling the parameter : ADVANCED_FACILITY_TASK****</b>");
					WSClient.setData("{var_parameter}", "ADVANCED_FACILITY_TASKS");
					WSClient.setData("{var_settingValue}", "Y");

					if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals("Y"))
						parameter2 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");

					if (!parameter2.equals("error")) {

						WSClient.writeToReport(LogStatus.INFO,
								"<b>****Setting the parameter :  ADDITIONAL_TASK_ASSIGNMENTS as null****</b>");
						WSClient.setData("{var_parameter}", "ADDITIONAL_TASK_ASSIGNMENTS");
						WSClient.setData("{var_settingValue}", "");

						if (!FetchApplicationParameters.getApplicationParameter("DS_01").equals(""))
							parameter3 = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
						if (parameter3.equals("error")) {

							if (profileID.equals(""))
								profileID = CreateProfile.createProfile("DS_01");
							if (!profileID.equals("error")) {
								WSClient.setData("{var_profileId}", profileID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
								WSClient.setData("{VAR_RATEPLANCODE}",
										OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
								WSClient.setData("{VAR_ROOMTYPE}",
										OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
								WSClient.setData("{var_roomType}",
										OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
								WSClient.setData("{var_RoomType}",
										OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
								WSClient.setData("{var_sourceCode}",
										OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
								WSClient.setData("{VAR_MARKETCODE}",
										OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
								WSClient.setData("{var_payment}",
										OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

								// Prerequisite 2 Create Reservation
								HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
								String resvID = resv.get("reservationId");

								if (!resvID.equals("error")) {

									WSClient.setData("{var_resvId}", resvID);
									WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + resvID + "</b>");
									// Prerequisite 3: Fetching available Hotel
									// rooms with
									// room type

									String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
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
									if (!roomNumber.equals("")) {
										WSClient.setData("{var_roomNumber}", roomNumber);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully fetched/create room.</b>");
										// Prerequisite 5: Changing the room
										// status
										// to inspected
										// to assign the room for checking in

										String setHousekeepingRoomStatusReq = WSClient
												.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
										String setHousekeepingRoomStatusRes = WSClient
												.processSOAPMessage(setHousekeepingRoomStatusReq);

										if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
												"SetHousekeepingRoomStatusRS_Success", true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Succesfully changed the status of room.</b>");

											// Prerequisite 6: Assign Room

											String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
											String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

											if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",
													true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully assigned room.</b>");
												WSClient.setData("{var_taskCode}",
														OperaPropConfig.getDataSetForCode("TaskCode", "DS_02"));
												HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
														HTNGLib.getInterfaceFromAddress());
												String fetchHKTaskReq = WSClient
														.createSOAPMessage("HTNGExtFetchHousekeepingTask", "DS_01");
												String fetchHKTaskRes = WSClient.processSOAPMessage(fetchHKTaskReq);

												if (WSAssert.assertIfElementValueEquals(fetchHKTaskRes,
														"FetchHouseKeepingTaskResponse_Result_resultStatusFlag",
														"SUCCESS", false)) {

													/**
													 * Verifying that the
													 * Tasksheet record is
													 * inserted
													 */
													// LinkedHashMap<String,String>
													// count=new
													// LinkedHashMap<String,String>();

													/**
													 * Validating the
													 * TaskDate,TaskCode,
													 * TaskSheetNumber,
													 * TotalRoomsInSheet,
													 * TaskInstruction details
													 * obtained from the
													 * response against the
													 * database details
													 */
													List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath2 = new HashMap<String, String>();
													xpath2.put("TaskSheets_TaskSheetHeader_TaskDate",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskSheetNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TotalRoomsInSheet",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TaskInstruction",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													xpath2.put("TaskSheets_TaskSheetHeader_TravelingCredit",
															"FetchHouseKeepingTaskResponse_TaskSheets_TaskSheetHeader");
													String query = WSClient.getQuery("QS_39");
													db2 = WSClient.getDBRows(query);

													actualValues2 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath2,
															false, XMLType.RESPONSE);

													for (int i = 0; i < actualValues2.size(); ++i) {

														LinkedHashMap<String, String> exp = db2.get(i);

														exp.put("TotalRoomsInSheet1", WSClient
																.getDBRow(WSClient.getQuery("QS_08")).get("COUNT"));

													}
													WSAssert.assertEquals(actualValues2, db2, false);

													/**
													 * Checking if the Room
													 * Numbers displayed in the
													 * response message under
													 * each of the Task Sheet
													 * Details section are
													 * correctly matching with
													 * the room numbers
													 * resulting from the
													 * response of the database
													 */

													List<LinkedHashMap<String, String>> db3 = new ArrayList<LinkedHashMap<String, String>>();
													List<LinkedHashMap<String, String>> actualValues3 = new ArrayList<LinkedHashMap<String, String>>();
													HashMap<String, String> xpath3 = new HashMap<String, String>();
													xpath3.put("TaskSheets_TasksheetDetails_RoomNumber",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													xpath3.put("TaskSheets_TasksheetDetails_Credit",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													xpath3.put("TaskSheets_TasksheetDetails_TaskInstruction",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													xpath3.put("TaskSheets_TasksheetDetails_TaskCode",
															"FetchHouseKeepingTaskResponse_TaskSheets_TasksheetDetails");
													String query1 = WSClient.getQuery("QS_43");
													db3 = WSClient.getDBRows(query1);
													actualValues3 = WSClient.getMultipleNodeList(fetchHKTaskRes, xpath3,
															false, XMLType.RESPONSE);
													WSAssert.assertEquals(actualValues3, db3, false);

												}

												if (WSAssert.assertIfElementExists(fetchHKTaskRes,
														"Result_Text_TextElement", true)) {
													String message = WSClient.getElementValue(fetchHKTaskRes,
															"Result_Text_TextElement", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>" + "The text displayed in the response is    :     "
																	+ message + "</b>");

												}

											} else {
												WSClient.writeToReport(LogStatus.WARNING, "Unable to assign the room ");
											}

										} else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Unable to change status of room ");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Unable to fetch room ");
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
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}