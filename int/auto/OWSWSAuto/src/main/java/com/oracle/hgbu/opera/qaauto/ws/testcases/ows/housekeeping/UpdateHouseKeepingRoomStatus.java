package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.housekeeping;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class UpdateHouseKeepingRoomStatus extends WSSetUp {

	public void setOwsHeader() throws Exception {
		String resort = OPERALib.getResort();
		String channel = OWSLib.getChannel();
		String uname = OPERALib.getUserName();
		String pwd = OPERALib.getPassword();
		String channelType = OWSLib.getChannelType(channel);
		String channelCarrier = OWSLib.getChannelCarier(resort, channel);
		OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	}

	@Parameters({ "runOnEntry" })
	@Test(groups = { "sanity", "UpdateHouseKeepingRoomStatus", "OWS", "Housekeeping" })

	public void updateHouseKeepingRoomStatus_39562() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateHouseKeepingRoomStatus_39562";
			WSClient.startTest(testName, "Verify that  House Keeping Room Status is updated to Occupied", "sanity");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				WSClient.setData("{var_chain}", OPERALib.getChain());
				// Prerequisite :Fetching rooms that are not reserved
				String resortOperaValue = OPERALib.getResort();
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				String interfaceName = OWSLib.getChannel();

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());
				String fetchHKBoardReq = WSClient.createSOAPMessage("FetchHousekeepingBoard", "Clean1");
				String fetchHKBoardResponseXML = WSClient.processSOAPMessage(fetchHKBoardReq);
				int flag = 0;
				System.out.println(prerequisite_block_flag);
				if (WSAssert.assertIfElementExists(fetchHKBoardResponseXML, "FetchHousekeepingBoardRS_Errors", true)) {
					WSClient.writeToReport(LogStatus.WARNING, WSClient.getElementValue(fetchHKBoardResponseXML,
							"FetchHousekeepingBoardRS_Errors_Error_ShortText", XMLType.RESPONSE));
				} else {
					String room_num = WSClient.getElementValue(fetchHKBoardResponseXML,
							"HousekeepingRoomInfo_HousekeepingRooms_Room_RoomNumber", XMLType.RESPONSE);
					if (!room_num.equals("*null*")) {
						flag = 1;
						WSClient.setData("{var_roomNumber}", WSClient.getElementValue(fetchHKBoardResponseXML,
								"HousekeepingRoomInfo_HousekeepingRooms_Room_RoomNumber", XMLType.RESPONSE));
					}

					// Prerequisite:If there are no rooms that are not reserved
					// then
					// rooms are created
					else {
						String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "Sanity");
						String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

						if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
							String roomNumber = WSClient.getElementValue(createRoomReq,
									"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							WSClient.setData("{var_roomNumber}", roomNumber);

						} else {
							prerequisite_block_flag = true;
							WSClient.writeToReport(LogStatus.WARNING,
									"************Blocked : Unable to create a room****************");
						}

					}

					// Updating the Room status of the Created/fetched room to
					// Dirty
					if (prerequisite_block_flag == false) {
						WSClient.setData("{var_hkStatus}", "OCCUPIED");

						String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
						WSClient.setData("{var_owsresort}", resortExtValue);
						setOwsHeader();

						String updateHouseKeepingStatusReq = WSClient
								.createSOAPMessage("OWSUpdatehouseKeepingRoomStatus", "DS_01");
						String updateHouseKeepingStatusRes = WSClient.processSOAPMessage(updateHouseKeepingStatusReq);
						if (WSAssert.assertIfElementValueEquals(updateHouseKeepingStatusRes,
								"UpdateHouseKeepingRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							String dbHkstatus = WSClient.getDBRow(WSClient.getQuery("QS_01")).get("HK_STATUS");

							if (WSAssert.assertEquals(dbHkstatus, "OCC", true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"House Keeping Status - Expected : OCC, Actual : " + dbHkstatus);
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"House Keeping Status - Expected : OCC, Actual : " + dbHkstatus);
							}

						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Parameters({ "runOnEntry" })
	@Test(groups = { "minimumRegression", "UpdateHouseKeepingRoomStatus", "OWS", "Housekeeping" })

	public void updateHouseKeepingRoomStatus_39563() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateHouseKeepingRoomStatus_39563";
			WSClient.startTest(testName, "Verify that  House Keeping Room Status is updated to Vacant",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				WSClient.setData("{var_chain}", OPERALib.getChain());
				// Prerequisite :Fetching rooms that are not reserved

				String interfaceName = OWSLib.getChannel();

				String resortOperaValue = OPERALib.getResort();
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());
				String fetchHKBoardReq = WSClient.createSOAPMessage("FetchHousekeepingBoard", "Occupied");
				String fetchHKBoardResponseXML = WSClient.processSOAPMessage(fetchHKBoardReq);
				int flag = 0;
				System.out.println(prerequisite_block_flag);
				if (WSAssert.assertIfElementExists(fetchHKBoardResponseXML, "FetchHousekeepingBoardRS_Errors", true)) {
					WSClient.writeToReport(LogStatus.WARNING, WSClient.getElementValue(fetchHKBoardResponseXML,
							"FetchHousekeepingBoardRS_Errors_Error_ShortText", XMLType.RESPONSE));
				} else {
					String room_num = WSClient.getElementValue(fetchHKBoardResponseXML,
							"HousekeepingRoomInfo_HousekeepingRooms_Room_RoomNumber", XMLType.RESPONSE);
					if (!room_num.equals("*null*")) {
						flag = 1;
						WSClient.setData("{var_roomNumber}", WSClient.getElementValue(fetchHKBoardResponseXML,
								"HousekeepingRoomInfo_HousekeepingRooms_Room_RoomNumber", XMLType.RESPONSE));
					}

					// Prerequisite:If there are no rooms that are not reserved
					// then
					// rooms are created
					else {
						String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "Sanity");
						String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

						if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
							String roomNumber = WSClient.getElementValue(createRoomReq,
									"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							WSClient.setData("{var_roomNumber}", roomNumber);

						} else {
							prerequisite_block_flag = true;
							WSClient.writeToReport(LogStatus.WARNING,
									"************Blocked : Unable to create a room****************");
						}

					}

					// Updating the Room status of the Created/fetched room to
					// Dirty
					if (prerequisite_block_flag == false) {
						WSClient.setData("{var_hkStatus}", "VACANT");

						String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
						WSClient.setData("{var_owsresort}", resortExtValue);
						setOwsHeader();

						String updateHouseKeepingStatusReq = WSClient
								.createSOAPMessage("OWSUpdatehouseKeepingRoomStatus", "DS_01");
						String updateHouseKeepingStatusRes = WSClient.processSOAPMessage(updateHouseKeepingStatusReq);
						if (WSAssert.assertIfElementValueEquals(updateHouseKeepingStatusRes,
								"UpdateHouseKeepingRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							String dbHkstatus = WSClient.getDBRow(WSClient.getQuery("QS_01")).get("HK_STATUS");

							if (WSAssert.assertEquals(dbHkstatus, "VAC", true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"House Keeping Status - Expected : VAC, Actual : " + dbHkstatus);
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"House Keeping Status - Expected : VAC, Actual : " + dbHkstatus);
							}

						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Parameters({ "runOnEntry" })
	@Test(groups = { "minimumRegression", "UpdateHouseKeepingRoomStatus", "OWS", "updateHouseKeepingRoomStatus_39564"})

	public void updateHouseKeepingRoomStatus_39564() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateHouseKeepingRoomStatus_39564";
			WSClient.startTest(testName, "Verify that Front Office Room Status is updated to Ocuupied",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				WSClient.setData("{var_chain}", OPERALib.getChain());
				// Prerequisite :Fetching rooms that are not reserved
				String resortOperaValue = OPERALib.getResort();
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());
				String fetchHKBoardReq = WSClient.createSOAPMessage("FetchHousekeepingBoard", "Vacant");
				String fetchHKBoardResponseXML = WSClient.processSOAPMessage(fetchHKBoardReq);
				int flag = 0;
				System.out.println(prerequisite_block_flag);
				if (WSAssert.assertIfElementExists(fetchHKBoardResponseXML, "FetchHousekeepingBoardRS_Errors", true)) {
					WSClient.writeToReport(LogStatus.WARNING, WSClient.getElementValue(fetchHKBoardResponseXML,
							"FetchHousekeepingBoardRS_Errors_Error_ShortText", XMLType.RESPONSE));
				} else {
					String room_num = WSClient.getElementValue(fetchHKBoardResponseXML,
							"HousekeepingRoomInfo_HousekeepingRooms_Room_RoomNumber", XMLType.RESPONSE);
					WSClient.setData("{var_roomNumber}", room_num);
					if (!room_num.equals("*null*")) {
						flag = 1;
						WSClient.setData("{var_roomNumber}", room_num);
					}

					// Prerequisite:If there are no rooms that are not reserved
					// then
					// rooms are created
					else {
						String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "Sanity");
						String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

						if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
							String roomNumber = WSClient.getElementValue(createRoomReq,
									"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							WSClient.setData("{var_roomNumber}", roomNumber);

						} else {
							prerequisite_block_flag = true;
							WSClient.writeToReport(LogStatus.WARNING,
									"************Blocked : Unable to create a room****************");
						}

					}

					// Updating the Room status of the Created/fetched room to
					// Dirty
					if (prerequisite_block_flag == false) {
						WSClient.setData("{var_foStatus}", "OCCUPIED");

						String interfaceName = OWSLib.getChannel();

						String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
						WSClient.setData("{var_owsresort}", resortExtValue);
						setOwsHeader();
						String updateHouseKeepingStatusReq = WSClient
								.createSOAPMessage("OWSUpdatehouseKeepingRoomStatus", "DS_02");
						String updateHouseKeepingStatusRes = WSClient.processSOAPMessage(updateHouseKeepingStatusReq);
						if (WSAssert.assertIfElementValueEquals(updateHouseKeepingStatusRes,
								"UpdateHouseKeepingRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							String dbFOstatus = WSClient.getDBRow(WSClient.getQuery("QS_02")).get("FO_STATUS");

							if (WSAssert.assertEquals(dbFOstatus, "OCC", true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Front Office Status - Expected : OCC, Actual : " + dbFOstatus);
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Front Office Status - Expected : OCC, Actual : " + dbFOstatus);
							}

						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Parameters({ "runOnEntry" })
	@Test(groups = { "minimumRegression", "UpdateHouseKeepingRoomStatus", "OWS", "Housekeeping" })

	public void updateHouseKeepingRoomStatus_39565() {
		try {
			boolean prerequisite_block_flag = false;
			String testName = "updateHouseKeepingRoomStatus_39565";
			WSClient.startTest(testName, "Verify that Room Status is updated to Dirty", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				WSClient.setData("{var_chain}", OPERALib.getChain());
				// Prerequisite :Fetching rooms that are not reserved
				String resortOperaValue = OPERALib.getResort();
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				String interfaceName = OWSLib.getChannel();

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());
				String fetchHKBoardReq = WSClient.createSOAPMessage("FetchHousekeepingBoard", "Clean1");
				String fetchHKBoardResponseXML = WSClient.processSOAPMessage(fetchHKBoardReq);
				int flag = 0;
				System.out.println(prerequisite_block_flag);
				if (WSAssert.assertIfElementExists(fetchHKBoardResponseXML, "FetchHousekeepingBoardRS_Errors", true)) {
					WSClient.writeToReport(LogStatus.WARNING, WSClient.getElementValue(fetchHKBoardResponseXML,
							"FetchHousekeepingBoardRS_Errors_Error_ShortText", XMLType.RESPONSE));
				} else {
					String room_num = WSClient.getElementValue(fetchHKBoardResponseXML,
							"HousekeepingRoomInfo_HousekeepingRooms_Room_RoomNumber", XMLType.RESPONSE);
					if (!room_num.equals("*null*")) {
						flag = 1;
						WSClient.setData("{var_roomNumber}", WSClient.getElementValue(fetchHKBoardResponseXML,
								"HousekeepingRoomInfo_HousekeepingRooms_Room_RoomNumber", XMLType.RESPONSE));
					}

					// Prerequisite:If there are no rooms that are not reserved
					// then
					// rooms are created
					else {
						String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "Sanity");
						String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

						if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {
							String roomNumber = WSClient.getElementValue(createRoomReq,
									"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
							WSClient.setData("{var_roomNumber}", roomNumber);

						} else {
							prerequisite_block_flag = true;
							WSClient.writeToReport(LogStatus.WARNING,
									"************Blocked : Unable to create a room****************");
						}

					}

					// Updating the Room status of the Created/fetched room to
					// Dirty
					if (prerequisite_block_flag == false) {
						WSClient.setData("{var_roomStatus}", "DIRTY");

						String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
						WSClient.setData("{var_owsresort}", resortExtValue);
						setOwsHeader();

						String updateHouseKeepingStatusReq = WSClient
								.createSOAPMessage("OWSUpdatehouseKeepingRoomStatus", "DS_03");
						String updateHouseKeepingStatusRes = WSClient.processSOAPMessage(updateHouseKeepingStatusReq);
						if (WSAssert.assertIfElementValueEquals(updateHouseKeepingStatusRes,
								"UpdateHouseKeepingRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							String dbHkstatus = WSClient.getDBRow(WSClient.getQuery("QS_03")).get("ROOM_STATUS");

							if (WSAssert.assertEquals(dbHkstatus, "DI", true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Room Status - Expected : DI, Actual : " + dbHkstatus);
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Room Status - Expected : DI, Actual : " + dbHkstatus);
							}

						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Parameters({ "runOnEntry" })
	@Test(groups = { "minimumRegression", "UpdateHouseKeepingRoomStatus", "OWS", "Housekeeping" })

	public void updateHouseKeepingRoomStatus_39566() {
		try {
			String testName = "updateHouseKeepingRoomStatus_39566";
			WSClient.startTest(testName,
					"Verify that the Error Message has been populated when an invalid Room Number is given",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RoomType" })) {
				WSClient.setData("{var_resort}", OPERALib.getResort());
				WSClient.setData("{var_chain}", OPERALib.getChain());
				// Prerequisite :Fetching rooms that are not reserved
				String resortOperaValue = OPERALib.getResort();
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				String interfaceName = OWSLib.getChannel();

				String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
				WSClient.setData("{var_owsresort}", resortExtValue);
				setOwsHeader();

				String room = WSClient.getDBRow(WSClient.getQuery("OWSUpdatehouseKeepingRoomStatus", "QS_04"))
						.get("ROOM");
				WSClient.setData("{var_roomNumber}", room);
				String updateHouseKeepingStatusReq = WSClient.createSOAPMessage("OWSUpdatehouseKeepingRoomStatus",
						"DS_04");
				String updateHouseKeepingStatusRes = WSClient.processSOAPMessage(updateHouseKeepingStatusReq);
				if (WSAssert.assertIfElementValueEquals(updateHouseKeepingStatusRes,
						"UpdateHouseKeepingRoomStatusResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>" + WSClient.getElementValue(updateHouseKeepingStatusRes,
							"Result_Text_TextElement", XMLType.RESPONSE) + "</b>");

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

}
