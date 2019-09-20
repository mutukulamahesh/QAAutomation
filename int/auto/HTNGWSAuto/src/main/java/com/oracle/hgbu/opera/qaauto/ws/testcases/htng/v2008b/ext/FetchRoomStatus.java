
package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2008b.ext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchRoomStatus extends WSSetUp {
	@Test(groups = { "sanity", "FetchRoomStatus", "HTNG2008BExt", "HTNG","fetchRoomStatus_Ext_4526" })
	/***** Fetching the rooms that are available in the resort *****/
	/*****
	 * * * PreRequisites Required: -->Fetch the fields to be validated using
	 * fetchHotelRooms core service. --> There should be rooms in the resort,If
	 * not rooms are to be created.
	 * 
	 *****/
	public void fetchRoomStatus_Ext_4526() {
		try {
			String testName = "fetchRoomStatus_Ext_4526";
			WSClient.startTest(testName, "Verify that all the rooms are fetched that are available in a resort", "sanity");
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			boolean prerequisite_block_flag = true;
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			// **** Setting Opera Header ****//

			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_resort}", resortOperaValue);

			// ******** Prerequisite : There should be rooms there in the
			// resort.**********//

			LinkedHashMap<String, String> hotelRooms = new LinkedHashMap<String, String>();
			List<LinkedHashMap<String, String>> hotelRoomsList = new ArrayList<LinkedHashMap<String, String>>();
			LinkedHashMap<String, String> fetchRooms = new LinkedHashMap<String, String>();
			List<LinkedHashMap<String, String>> fetchRoomsList = new ArrayList<LinkedHashMap<String, String>>();

			// *********Prerequisite : Fetch all the rooms in the resort.
			// **************//

			String fetchHotelRoomsXML = WSClient.createSOAPMessage("FetchHotelRooms", "DS_01");
			String fetchHotelResponse = WSClient.processSOAPMessage(fetchHotelRoomsXML);

			if (WSAssert.assertIfElementExists(fetchHotelResponse, "FetchHotelRoomsRS_Success", true)) {

				String room_no = WSClient.getElementValue(fetchHotelResponse, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

				// ****** Prerequisite: If the rooms are not there in the
				// resort,then rooms are
				// created.*********//

				if (!room_no.equals("")) {

					WSClient.writeToReport(LogStatus.INFO, "<b>Rooms are available in the resort, Fetching Details </b>");
				}

				else {
					String CreateRoomClassTempReq = WSClient.createSOAPMessage("CreateRoomClassTemplate", "RoomMaint");
					System.out.println(CreateRoomClassTempReq);
					String CreateRoomClassteTempResponseXML = WSClient.processSOAPMessage(CreateRoomClassTempReq);
					String RoomClass = WSClient.getElementValue(CreateRoomClassTempReq, "CreateRoomClassTemplateRQ_RoomClassTemplate_Code", XMLType.REQUEST);
					if (WSAssert.assertIfElementExists(CreateRoomClassteTempResponseXML, "CreateRoomClassTemplateRS_Success", false) == false) {
						prerequisite_block_flag = true;
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked ---> CreateRoomClassTemplate fails");

					} else {
						WSClient.setData("{var_RoomClass}", RoomClass);
						String CreateRoomTypeTempReq = WSClient.createSOAPMessage("CreateRoomTypeTemplates", "RoomMaint");
						String CreateRoomTypeTempResponseXML = WSClient.processSOAPMessage(CreateRoomTypeTempReq);
						if (WSAssert.assertIfElementExists(CreateRoomTypeTempResponseXML, "CreateRoomTypeTemplatesRS_Success", false) == false) {
							prerequisite_block_flag = true;
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->CreateRoomTypeTemplate fails");

						} else {
							String fetchRoomTypeReq = WSClient.createSOAPMessage("FetchRoomTypes", "FetchAllRoomTypes");
							String fetchRoomTypeResponseXML = WSClient.processSOAPMessage(fetchRoomTypeReq);
							if (WSAssert.assertIfElementExists(fetchRoomTypeResponseXML, "FetchRoomTypesRS_RoomTypesSummary_RoomTypeSummary_RoomType", false) == false) {
								prerequisite_block_flag = true;
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->FetchRoomType fails");
							} else {
								WSClient.setData("{var_RoomType}", WSClient.getElementValue(fetchRoomTypeResponseXML, "FetchRoomTypesRS_RoomTypesSummary_RoomTypeSummary_RoomType", XMLType.RESPONSE));
								String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
								String createRoomResponseXML = WSClient.processSOAPMessage(createRoomReq);
								if (WSAssert.assertIfElementExists(createRoomResponseXML, "CreateRoomRS_Success", false) == false) {
									prerequisite_block_flag = true;
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->CreateRoom fails");

								} else {
									WSClient.setData("{var_RoomNo}", WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST));
								}
							}
						}
					}
				}

				if (prerequisite_block_flag == true) {

					hotelRooms.put("FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", "FetchHotelRoomsRS_HotelRooms_Room");
					hotelRooms.put("Room_Housekeeping_RoomStatus_RoomStatus", "FetchHotelRoomsRS_HotelRooms_Room");

					hotelRoomsList = WSClient.getMultipleNodeList(fetchHotelResponse, hotelRooms, true, XMLType.RESPONSE);

					WSClient.setData("{var_extresort}", resortExtValue);

					// **** HTNG Fetch Room Status *****//

					// **** Setting HTNG Header ****//

					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String fetchRoomStatusXML = WSClient.createSOAPMessage("HTNGExtFetchRoomStatus", "DS_01");
					String fetchHotelRoomsResponse = WSClient.processSOAPMessage(fetchRoomStatusXML);

					if (WSAssert.assertIfElementExists(fetchHotelRoomsResponse, "FetchRoomStatusResponse_Result", false)) {

						if (WSAssert.assertIfElementValueEquals(fetchHotelRoomsResponse, "FetchRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							fetchRooms.put("FetchRoomStatusResponse_FetchRoomStatus_RoomNumber", "FetchRoomStatusResponse_FetchRoomStatus");
							fetchRooms.put("FetchRoomStatusResponse_FetchRoomStatus_RoomStatus", "FetchRoomStatusResponse_FetchRoomStatus");

							fetchRoomsList = WSClient.getMultipleNodeList(fetchHotelRoomsResponse, fetchRooms, false, XMLType.RESPONSE);

							// ******************** Validation *************//

							WSAssert.assertEquals(hotelRoomsList, fetchRoomsList, false);

						}
						if (WSAssert.assertIfElementExists(fetchHotelRoomsResponse, "Result_Text_TextElement", true)) {
							String message = WSClient.getElementValue(fetchHotelRoomsResponse, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>" + "The text displayed in the response is    :     " + message + "</b>");
						}
					}
				}
			} else {
				// ********** When fetchHotelRooms is blocked.*********//
				WSClient.writeToReport(LogStatus.WARNING, "The pre requisites failed for Fetch Hotel Rooms ! Available rooms are not fetched -----------Blocked! ");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// ExtentReport.endExtentTest();
		}
	}

	@Test(groups = { "minimumRegression", "FetchRoomStatus", "HTNG2008BExt", "HTNG", "fetchRoomStatus_Ext_562" })

	/***** Fetching room details of the available rooms in a resort *****/
	/*****
	 * * * PreRequisites Required: -->Fetch the fields to be validated using
	 * fetchHotelRooms core service. --> There should be rooms in the resort,If
	 * not rooms are to be created. --> Fetch a random room using
	 * fetchHotelRooms core service.
	 * 
	 *****/

	public void fetchRoomStatus_Ext_562() {
		try {
			String testName = "fetchRoomStatus_Ext_562";
			WSClient.startTest(testName, "Verify that all the details of the room are being fetched by giving room number in fetch room status request", "minimumRegression");
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			boolean prerequisite_block_flag = true;
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			// **** Setting Opera Header ****//

			OPERALib.setOperaHeader(uname);

			String room;
			// **** Prerequisite : Rooms should be there in the resort. ******//

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extresort}", resortExtValue);

			// ***** Prerequisite : Fetching all the available rooms in a
			// resort. *****//

			String fetchHotelRoomsPreXML = WSClient.createSOAPMessage("FetchHotelRooms", "DS_01");
			String hotelRoomPreResponse = WSClient.processSOAPMessage(fetchHotelRoomsPreXML);

			if (WSAssert.assertIfElementExists(hotelRoomPreResponse, "FetchHotelRoomsRS_Success", true)) {
				room = WSAssert.getElementValue(hotelRoomPreResponse, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

				// ****** Prerequisite :If there are no available rooms.Create a
				// new room.
				// *****//

				if (!room.equals("")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Rooms are available in the resort, Fetching Details </b>");
				} else {
					String CreateRoomClassTempReq = WSClient.createSOAPMessage("CreateRoomClassTemplate", "RoomMaint");
					System.out.println(CreateRoomClassTempReq);
					String CreateRoomClassteTempResponseXML = WSClient.processSOAPMessage(CreateRoomClassTempReq);
					String RoomClass = WSClient.getElementValue(CreateRoomClassTempReq, "CreateRoomClassTemplateRQ_RoomClassTemplate_Code", XMLType.REQUEST);
					if (WSAssert.assertIfElementExists(CreateRoomClassteTempResponseXML, "CreateRoomClassTemplateRS_Success", false) == false) {
						prerequisite_block_flag = true;
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked ---> CreateRoomClassTemplate fails");

					} else {
						WSClient.setData("{var_RoomClass}", RoomClass);
						String CreateRoomTypeTempReq = WSClient.createSOAPMessage("CreateRoomTypeTemplates", "RoomMaint");
						String CreateRoomTypeTempResponseXML = WSClient.processSOAPMessage(CreateRoomTypeTempReq);
						if (WSAssert.assertIfElementExists(CreateRoomTypeTempResponseXML, "CreateRoomTypeTemplatesRS_Success", false) == false) {
							prerequisite_block_flag = true;
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->CreateRoomTypeTemplate fails");

						} else {
							String fetchRoomTypeReq = WSClient.createSOAPMessage("FetchRoomTypes", "FetchAllRoomTypes");
							String fetchRoomTypeResponseXML = WSClient.processSOAPMessage(fetchRoomTypeReq);
							if (WSAssert.assertIfElementExists(fetchRoomTypeResponseXML, "FetchRoomTypesRS_RoomTypesSummary_RoomTypeSummary_RoomType", false) == false) {
								prerequisite_block_flag = false;
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->FetchRoomType fails");
							} else {
								WSClient.setData("{var_RoomType}", WSClient.getElementValue(fetchRoomTypeResponseXML, "FetchRoomTypesRS_RoomTypesSummary_RoomTypeSummary_RoomType", XMLType.RESPONSE));
								String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
								String createRoomResponseXML = WSClient.processSOAPMessage(createRoomReq);
								if (WSAssert.assertIfElementExists(createRoomResponseXML, "CreateRoomRS_Success", false) == false) {
									prerequisite_block_flag = false;
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->CreateRoom fails");

								} else {
									WSClient.setData("{var_RoomNo}", WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST));
								}
							}
						}
					}
				}

				if (prerequisite_block_flag == true) {
					WSClient.setData("{var_RoomNo}", room);

					// ***** Prerequisite : Fetch room details for the available
					// room using
					// fetchHotelRooms. ***//

					String fetchHotelRoomsXML = WSClient.createSOAPMessage("FetchHotelRooms", "DS_02");
					String hotelRoomsResponse = WSClient.processSOAPMessage(fetchHotelRoomsXML);

					if (WSAssert.assertIfElementExists(hotelRoomsResponse, "FetchHotelRoomsRS_Success", true)) {

						String roomNo = WSClient.getElementValue(hotelRoomsResponse, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
						String roomType = WSClient.getElementValue(hotelRoomsResponse, "HotelRooms_Room_RoomType_RoomType", XMLType.RESPONSE);
						String roomStatus = WSClient.getElementValue(hotelRoomsResponse, "Room_Housekeeping_RoomStatus_RoomStatus", XMLType.RESPONSE);
						String foStatus = WSClient.getElementValue(hotelRoomsResponse, "Room_Housekeeping_RoomStatus_FrontOfficeStatus", XMLType.RESPONSE);

						// ******** HTNG Fetch Room Status *************//

						// **** Setting HTNG Header ****//

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						WSClient.setData("{var_extresort}", resortExtValue);

						String fetchRoomStatusXML = WSClient.createSOAPMessage("HTNGExtFetchRoomStatus", "DS_02");
						String fetchRoomResponse = WSClient.processSOAPMessage(fetchRoomStatusXML);

						if (WSAssert.assertIfElementExists(fetchRoomResponse, "FetchRoomStatusResponse_Result", false)) {

							if (WSAssert.assertIfElementValueEquals(fetchRoomResponse, "FetchRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								// ******** Validation *********//

								String fetchRoomNo = WSClient.getElementValue(fetchRoomResponse, "FetchRoomStatusResponse_FetchRoomStatus_RoomNumber", XMLType.RESPONSE);
								String fetchRoomType = WSClient.getElementValue(fetchRoomResponse, "FetchRoomStatusResponse_FetchRoomStatus_RoomType", XMLType.RESPONSE);
								String fetchRoomStatus = WSClient.getElementValue(fetchRoomResponse, "FetchRoomStatusResponse_FetchRoomStatus_RoomStatus", XMLType.RESPONSE);

								String fetchFoStatus = WSClient.getElementValue(fetchRoomResponse, "FetchRoomStatusResponse_FetchRoomStatus_FrontOfficeStatus", XMLType.RESPONSE);
								if (fetchFoStatus.equals("VAC"))
									fetchFoStatus = "Vacant";
								else if (fetchFoStatus.equals("OCC"))
									fetchFoStatus = "Occupied";

								if (WSAssert.assertEquals(roomNo, fetchRoomNo, true)) {

									WSClient.writeToReport(LogStatus.PASS, "Room Number :		Expected :	" + roomNo + "		 Actual :	 	 " + fetchRoomNo);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Room Number :  		Expected :	" + roomNo + "		 Actual : 		 " + fetchRoomNo);

								}
								if (WSAssert.assertEquals(roomType, fetchRoomType, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Room Type :  		Expected :	" + roomType + "		 Actual :	 	 " + fetchRoomType);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Room Type : 		Expected :	" + roomType + "		 Actual : 		 " + fetchRoomType);

								}
								if (WSAssert.assertEquals(roomStatus, fetchRoomStatus, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Room Status	:	Expected :		" + roomStatus + "		 Actual :	 " + fetchRoomStatus);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Room Status :		 Expected : 		" + roomStatus + " 		Actual : 	 " + fetchRoomStatus);

								}

								if (WSAssert.assertEquals(foStatus, fetchFoStatus, true)) {
									WSClient.writeToReport(LogStatus.PASS, "FO Status: 	Expected :	" + foStatus + "		Actual :		  " + fetchFoStatus);

								} else {
									WSClient.writeToReport(LogStatus.FAIL, "FO Status: 	Expected :" + foStatus + " 	Actual : 	 " + fetchFoStatus);

								}
							}
							if (WSAssert.assertIfElementExists(fetchRoomResponse, "Result_Text_TextElement", true)) {
								String message = WSClient.getElementValue(fetchRoomResponse, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>" + "The text displayed in the response is    :     " + message + "</b>");
							}
						}

					} else
						// ********** When fetchHotelRooms is
						// blocked.*********//
						WSClient.writeToReport(LogStatus.WARNING, "The pre requisites failed for Fetch Hotel Rooms for the given room  !! Available rooms are not fetched -----------Blocked! ");

				}
			} else
				// ********** When fetchHotelRooms is blocked.*********//
				WSClient.writeToReport(LogStatus.WARNING, "The pre requisites failed for Fetch Hotel Rooms ! Available rooms are not fetched -----------Blocked! ");

		}

		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// ExtentReport.endExtentTest();
		}
	}

	@Test(groups = { "minimumRegression", "FetchRoomStatus", "HTNG2008BExt", "HTNG", "fetchRoomStatus_Ext_563" })
	/***** Fetching room details of the available room types in a resort *****/
	/*****
	 * * * PreRequisites Required: -->Fetch the fields to be validated using
	 * fetchHotelRooms core service. --> There should be rooms in the resort,If
	 * not rooms are to be created. --> Fetch a random room type using
	 * fetchHotelRooms core service.
	 *****/
	public void fetchRoomStatus_Ext_563() {
		try {
			String testName = "fetchRoomStatus_Ext_563";
			WSClient.startTest(testName, "Verify that all the details of the room are being fetched by giving room type in fetch room status request", "minimumRegression");
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
			boolean prerequisite_block_flag = true;
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String fromAddress = HTNGLib.getInterfaceFromAddress();

			// *** Setting Opera Header ***** //

			OPERALib.setOperaHeader(uname);

			String roomType;
			String room;

			// **** Prerequisite : Rooms should be there in the resort. ******//

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extresort}", resortExtValue);

			// *********Prerequisite : Fetch all the rooms in the resort.
			// **************//

			String fetchHotelRoomsPreResponseXML = WSClient.createSOAPMessage("FetchHotelRooms", "DS_01");
			String hotelRoomPreResponse = WSClient.processSOAPMessage(fetchHotelRoomsPreResponseXML);

			if (WSAssert.assertIfElementExists(hotelRoomPreResponse, "FetchHotelRoomsRS_Success", true)) {

				room = WSAssert.getElementValue(hotelRoomPreResponse, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

				// ****** Prerequisite :If there are no available rooms.Create a
				// new room.
				// *****//

				if (!room.equals("")) {

					WSClient.writeToReport(LogStatus.INFO, "<b>Rooms are available in the resort, Fetching Details </b>");
				} else {
					String CreateRoomClassTempReq = WSClient.createSOAPMessage("CreateRoomClassTemplate", "RoomMaint");
					System.out.println(CreateRoomClassTempReq);
					String CreateRoomClassteTempResponseXML = WSClient.processSOAPMessage(CreateRoomClassTempReq);
					String RoomClass = WSClient.getElementValue(CreateRoomClassTempReq, "CreateRoomClassTemplateRQ_RoomClassTemplate_Code", XMLType.REQUEST);
					if (WSAssert.assertIfElementExists(CreateRoomClassteTempResponseXML, "CreateRoomClassTemplateRS_Success", false) == false) {
						prerequisite_block_flag = true;
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked ---> CreateRoomClassTemplate fails");

					} else {
						WSClient.setData("{var_RoomClass}", RoomClass);
						String CreateRoomTypeTempReq = WSClient.createSOAPMessage("CreateRoomTypeTemplates", "RoomMaint");
						String CreateRoomTypeTempResponseXML = WSClient.processSOAPMessage(CreateRoomTypeTempReq);
						if (WSAssert.assertIfElementExists(CreateRoomTypeTempResponseXML, "CreateRoomTypeTemplatesRS_Success", false) == false) {
							prerequisite_block_flag = true;
							WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->CreateRoomTypeTemplate fails");

						} else {
							String fetchRoomTypeReq = WSClient.createSOAPMessage("FetchRoomTypes", "FetchAllRoomTypes");
							String fetchRoomTypeResponseXML = WSClient.processSOAPMessage(fetchRoomTypeReq);
							if (WSAssert.assertIfElementExists(fetchRoomTypeResponseXML, "FetchRoomTypesRS_RoomTypesSummary_RoomTypeSummary_RoomType", false) == false) {
								prerequisite_block_flag = true;
								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->FetchRoomType fails");
							} else {
								WSClient.setData("{var_RoomType}", WSClient.getElementValue(fetchRoomTypeResponseXML, "FetchRoomTypesRS_RoomTypesSummary_RoomTypeSummary_RoomType", XMLType.RESPONSE));
								String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
								String createRoomResponseXML = WSClient.processSOAPMessage(createRoomReq);
								if (WSAssert.assertIfElementExists(createRoomResponseXML, "CreateRoomRS_Success", false) == false) {
									prerequisite_block_flag = true;
									WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->CreateRoom fails");

								} else {
									WSClient.setData("{var_RoomNo}", WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST));
								}
							}
						}
					}
				}

				if (prerequisite_block_flag == true) {

					roomType = WSAssert.getElementValue(hotelRoomPreResponse, "HotelRooms_Room_RoomType_RoomType", XMLType.RESPONSE);
					WSClient.setData("{var_roomType}", roomType);

					LinkedHashMap<String, String> hotelRooms = new LinkedHashMap<String, String>();
					List<LinkedHashMap<String, String>> hotelRoomsList = new ArrayList<LinkedHashMap<String, String>>();
					LinkedHashMap<String, String> fetchRooms = new LinkedHashMap<String, String>();
					List<LinkedHashMap<String, String>> fetchRoomsList = new ArrayList<LinkedHashMap<String, String>>();

					// ***** Prerequisite : Fetch rooms details for the
					// available room type using
					// fetchHotelRooms. ***//

					String fetchHotelRoomsXML = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
					String hotelRoomsResponse = WSClient.processSOAPMessage(fetchHotelRoomsXML);

					if (WSAssert.assertIfElementExists(hotelRoomsResponse, "FetchHotelRoomsRS_Success", true)) {

						hotelRooms.put("FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", "FetchHotelRoomsRS_HotelRooms_Room");
						hotelRooms.put("HotelRooms_Room_RoomType_RoomType", "FetchHotelRoomsRS_HotelRooms_Room");

						hotelRoomsList = WSClient.getMultipleNodeList(hotelRoomsResponse, hotelRooms, true, XMLType.RESPONSE);

						// **** HTNG Fetch Room Status ******//

						// **** Setting HTNG Header ****//

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);

						WSClient.setData("{var_extresort}", resortExtValue);

						String fetchRoomStatusXML = WSClient.createSOAPMessage("HTNGExtFetchRoomStatus", "DS_03");
						String fetchRoomsResponse = WSClient.processSOAPMessage(fetchRoomStatusXML);

						if (WSAssert.assertIfElementExists(fetchRoomsResponse, "FetchRoomStatusResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(fetchRoomsResponse, "FetchRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								fetchRooms.put("FetchRoomStatusResponse_FetchRoomStatus_RoomNumber", "FetchRoomStatusResponse_FetchRoomStatus");
								fetchRooms.put("FetchRoomStatusResponse_FetchRoomStatus_RoomType", "FetchRoomStatusResponse_FetchRoomStatus");

								fetchRoomsList = WSClient.getMultipleNodeList(fetchRoomsResponse, fetchRooms, false, XMLType.RESPONSE);

								// ****** Validation *******//

								WSAssert.assertEquals(hotelRoomsList, fetchRoomsList, false);

							}
							if (WSAssert.assertIfElementExists(fetchRoomsResponse, "Result_Text_TextElement", true)) {
								String message = WSClient.getElementValue(fetchRoomsResponse, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>" + "The text displayed in the response is    :     " + message + "</b>");
							}
						} else
							// ********** When fetchHotelRooms is
							// blocked.*********//
							WSClient.writeToReport(LogStatus.WARNING, "The pre requisites failed for Fetch Hotel Rooms for the given room type !! Available rooms are not fetched -----------Blocked! ");
					}
				}

			} else
				// ********** When fetchHotelRooms is blocked.*********//
				WSClient.writeToReport(LogStatus.WARNING, "The pre requisites failed for Fetch Hotel Rooms ! Available rooms are not fetched -----------Blocked! ");

		}

		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// ExtentReport.endExtentTest();
		}
	}

}
