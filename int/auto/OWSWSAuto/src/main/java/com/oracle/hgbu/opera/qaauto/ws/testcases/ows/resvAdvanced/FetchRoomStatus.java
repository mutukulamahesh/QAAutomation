package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

/**
 *
 * @author kankur
 *
 */

public class FetchRoomStatus extends WSSetUp {
	// Setting OWS Header
	public void setOWSHeader() {
		try {
			String resort = OPERALib.getResort();
			String uname = OPERALib.getUserName();
			String channel = OWSLib.getChannel();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "OWS Header not set.");
		}
	}

	// @Test(groups = { "minimumRegression", "ResvAdvanced", "FetchRoomStatus",
	// "OWS" })
	// public void fetchRoomStatus_60009()
	// {
	// try
	// {
	// String testName = "fetchRoomStatus_60009";
	// WSClient.startTest(testName, "Verify that the Room Status and related
	// details is fetched for the given room number where channel and carrier
	// name are different", "minimumRegression");
	//
	// String prerequisite[] = {"Rooms"};
	// if(OperaPropConfig.getPropertyConfigResults(prerequisite))
	// {
	// //Means Required prerequisite are fulfilled
	// //Getting Required values for fetching the Room Status.
	// String resortOperaValue = OPERALib.getResort();
	// String channel = OWSLib.getChannel(3);
	// String owsresort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_oresort}", owsresort);
	// String chain=OPERALib.getChain();
	// String roomNumber = OperaPropConfig.getDataSetForCode("Rooms", "DS_31");
	//
	// //Setting Variables
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_chain}",chain);
	// WSClient.setData("{var_roomNumber}", roomNumber);
	//
	//
	// String resort = OPERALib.getResort();
	// String uName = OPERALib.getUserName();
	// String pass = OPERALib.getPassword();
	// String channelType = OWSLib.getChannelType(channel);
	// String channelCarrier = OWSLib.getChannelCarier(resort, channel);
	// OWSLib.setOWSHeader(uName, pass, resort, channelType, channelCarrier);
	//
	//// WSClient.writeToReport(LogStatus.INFO, "Fetching Room Status");
	// String fetchRoomStatusReq =
	// WSClient.createSOAPMessage("OWSFetchRoomStatus", "DS_01");
	// String fetchRoomStatusRes =
	// WSClient.processSOAPMessage(fetchRoomStatusReq);
	//
	// //Checking For Result Flag
	// if (WSAssert.assertIfElementExists(fetchRoomStatusRes,
	// "FetchRoomStatusResponse_Result_resultStatusFlag", true))
	// {
	// if(WSAssert.assertIfElementValueEquals(fetchRoomStatusRes,
	// "FetchRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false))
	// {
	// //Fetching Record from DB
	// String QS01 = WSClient.getQuery("QS_01");
	// LinkedHashMap<String, String> dB = WSClient.getDBRow(QS01);
	//
	// //Fetching Record from Response
	// String roomStatus=WSClient.getElementValue(fetchRoomStatusRes,
	// "FetchRoomStatusResponse_RoomStatus_RoomStatus", XMLType.RESPONSE);
	// String frontOfficeStatus=WSClient.getElementValue(fetchRoomStatusRes,
	// "FetchRoomStatusResponse_RoomStatus_FrontOfficeStatus",
	// XMLType.RESPONSE);
	// String houseKeepingStatus = WSClient.getElementValue(fetchRoomStatusRes,
	// "FetchRoomStatusResponse_RoomStatus_HouseKeepingStatus",
	// XMLType.RESPONSE);
	// String houseKeepingInspectionFlag =
	// WSClient.getElementValue(fetchRoomStatusRes,
	// "FetchRoomStatusResponse_RoomStatus_HouseKeepingInspectionFlag",
	// XMLType.RESPONSE);
	// String turnDownYn = WSClient.getElementValue(fetchRoomStatusRes,
	// "FetchRoomStatusResponse_RoomStatus_TurnDownYn", XMLType.RESPONSE);
	// String roomNo = WSClient.getElementValue(fetchRoomStatusRes,
	// "FetchRoomStatusResponse_RoomStatus_RoomNumber", XMLType.RESPONSE);
	// String roomType = WSClient.getElementValue(fetchRoomStatusRes,
	// "FetchRoomStatusResponse_RoomStatus_RoomType", XMLType.RESPONSE);
	// String roomDescription = WSClient.getElementValue(fetchRoomStatusRes,
	// "FetchRoomStatusResponse_RoomStatus_RoomDescription", XMLType.RESPONSE);
	//
	// LinkedHashMap<String,String> actualRecord = new LinkedHashMap<>();
	// //Now adding roomStatus, frontOfficeStatus, houseKeepingStatus,
	// houseKeepingInspectionFlag, turnDownYn
	// //roomNumber, roomType, roomDescription into above HashMap
	// actualRecord.put("RoomStatus",roomStatus);
	// actualRecord.put("FrontOfficeStatus",frontOfficeStatus);
	// actualRecord.put("HouseKeepingStatus", houseKeepingStatus);
	// actualRecord.put("HouseKeepingInspectionFlag",
	// houseKeepingInspectionFlag);
	// actualRecord.put("TurnDownYn", turnDownYn);
	// actualRecord.put("RoomNumber", roomNo);
	// actualRecord.put("RoomType", roomType);
	// actualRecord.put("RoomDescription", roomDescription);
	//
	// WSAssert.assertEquals(dB, actualRecord, false);
	// }
	// //Checking if ResultstatusFlag is FAIL
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "Fetch Room Status Failed!");
	// }
	//
	// //Checking for GDSError
	// if(WSAssert.assertIfElementExists(fetchRoomStatusRes,
	// "FetchRoomStatusResponse_Result_GDSError",true))
	// {
	// String message=WSAssert.getElementValue(fetchRoomStatusRes,
	// "FetchRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.FAIL, "The error displayed response is
	// :"+ message);
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does
	// not Exist in the response!");
	// }
	//
	//
	// //Checking for Text Element in Result
	// if (WSAssert.assertIfElementExists(fetchRoomStatusRes,
	// "Result_Text_TextElement", true))
	// {
	//
	// String message = WSAssert.getElementValue(fetchRoomStatusRes,
	// "Result_Text_TextElement", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the
	// response is :" + message+"</b>");
	// }
	//
	// //Checking For OperaErrorCode
	// if
	// (WSAssert.assertIfElementExists(fetchRoomStatusRes,"FetchRoomStatusResponse_Result_OperaErrorCode",
	// true))
	// {
	// String code = WSAssert.getElementValue(fetchRoomStatusRes,
	// "FetchRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error code displayed in
	// the response is :" + code+"</b>");
	// }
	//
	//
	// //Checking For Fault Schema
	// if(WSAssert.assertIfElementExists(fetchRoomStatusRes,
	// "FetchRoomStatusResponse_faultcode", true))
	// {
	// String
	// message=WSClient.getElementValue(fetchRoomStatusRes,"FetchRoomStatusResponse_faultstring",
	// XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Fault Schema in Response
	// with message: "+"</b>"+message);
	// }
	//
	//
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "The prerequisites
	// failed!------ Rooms not available -----Blocked");
	// }
	// }
	// catch (Exception ex)
	// {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + ex);
	// }
	// finally
	// {
	//
	// }
	//
	// }
	//
	// Fetch Room Status Sanity
	@Test(groups = { "sanity", "ResvAdvanced", "FetchRoomStatus", "OWS" })
	public void fetchRoomStatus_39400() {
		try {
			String testName = "fetchRoomStatus_39400";
			WSClient.startTest(testName,
					"Verify that the Room Status and related details is fetched for the given room number", "sanity");

			String prerequisite[] = { "Rooms" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				// Means Required prerequisite are fulfilled
				// Getting Required values for fetching the Room Status.
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_oresort}", owsresort);
				String chain = OPERALib.getChain();
				String roomNumber = OperaPropConfig.getDataSetForCode("Rooms", "DS_31");

				// Setting Variables
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_roomNumber}", roomNumber);

				// String resort = OPERALib.getResort();
				// String channel = OWSLib.getChannel();
				// String uName = OPERALib.getUserName();
				// String pass = OPERALib.getPassword();
				// String channelType = OWSLib.getChannelType(channel);
				// String channelCarrier = OWSLib.getChannelCarier(resort,
				// channelType);
				// OWSLib.setOWSHeader(uName, pass, resort, channelType,
				// channelCarrier);
				setOWSHeader();

				// WSClient.writeToReport(LogStatus.INFO, "Fetching Room
				// Status");
				String fetchRoomStatusReq = WSClient.createSOAPMessage("OWSFetchRoomStatus", "DS_01");
				String fetchRoomStatusRes = WSClient.processSOAPMessage(fetchRoomStatusReq);

				// Checking For Result Flag
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes,
						"FetchRoomStatusResponse_Result_resultStatusFlag", true)) {
					if (WSAssert.assertIfElementValueEquals(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						// Fetching Record from DB
						String QS01 = WSClient.getQuery("QS_01");
						LinkedHashMap<String, String> dB = WSClient.getDBRow(QS01);

						// Fetching Record from Response
						String roomStatus = WSClient.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_RoomStatus_RoomStatus", XMLType.RESPONSE);
						String frontOfficeStatus = WSClient.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_RoomStatus_FrontOfficeStatus", XMLType.RESPONSE);
						String houseKeepingStatus = WSClient.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_RoomStatus_HouseKeepingStatus", XMLType.RESPONSE);
						String houseKeepingInspectionFlag = WSClient.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_RoomStatus_HouseKeepingInspectionFlag", XMLType.RESPONSE);
						String turnDownYn = WSClient.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_RoomStatus_TurnDownYn", XMLType.RESPONSE);
						String roomNo = WSClient.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_RoomStatus_RoomNumber", XMLType.RESPONSE);
						String roomType = WSClient.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_RoomStatus_RoomType", XMLType.RESPONSE);
						String roomDescription = WSClient.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_RoomStatus_RoomDescription", XMLType.RESPONSE);

						LinkedHashMap<String, String> actualRecord = new LinkedHashMap<>();
						// Now adding roomStatus, frontOfficeStatus,
						// houseKeepingStatus, houseKeepingInspectionFlag,
						// turnDownYn
						// roomNumber, roomType, roomDescription into above
						// HashMap
						actualRecord.put("RoomStatus", roomStatus);
						actualRecord.put("FrontOfficeStatus", frontOfficeStatus);
						actualRecord.put("HouseKeepingStatus", houseKeepingStatus);
						actualRecord.put("HouseKeepingInspectionFlag", houseKeepingInspectionFlag);
						actualRecord.put("TurnDownYn", turnDownYn);
						actualRecord.put("RoomNumber", roomNo);
						actualRecord.put("RoomType", roomType);
						actualRecord.put("RoomDescription", roomDescription);

						WSAssert.assertEquals(dB, actualRecord, false);
					}
					// Checking if ResultstatusFlag is FAIL
					else {
						WSClient.writeToReport(LogStatus.FAIL, "Fetch Room Status Failed!");
					}

					// Checking for GDSError
					if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_GDSError",
							true)) {
						String message = WSAssert.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "The error displayed response is :" + message);
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
				}

				// Checking for Text Element in Result
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "Result_Text_TextElement", true)) {

					String message = WSAssert.getElementValue(fetchRoomStatusRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + message + "</b>");
				}

				// Checking For OperaErrorCode
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_OperaErrorCode",
						true)) {
					String code = WSAssert.getElementValue(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The error code displayed in the response is :" + code + "</b>");
				}

				// Checking For Fault Schema
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_faultcode", true)) {
					String message = WSClient.getElementValue(fetchRoomStatusRes, "FetchRoomStatusResponse_faultstring",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL,
							"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Rooms not available -----Blocked");
			}
		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}

	}

	// Fetch Room Status MR-1
	@Test(groups = { "minimumRegression", "ResvAdvanced", "FetchRoomStatus", "OWS" })
	public void fetchRoomStatus_39401() {
		try {
			String testName = "fetchRoomStatus_39401";
			WSClient.startTest(testName,
					"Verify that the Room Status and related details is fetched for the given room type",
					"minimumRegression");

			String prerequisite[] = { "RoomType" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				// Means Required prerequisite are fulfilled
				// Getting Required values for fetching the Room Status.
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_oresort}", owsresort);
				String chain = OPERALib.getChain();
				String roomTyp = OperaPropConfig.getDataSetForCode("RoomType", "DS_03");

				// Setting Variables
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_roomType}", roomTyp);

				// String resort = OPERALib.getResort();
				// String channel = OWSLib.getChannel();
				// String uName = OPERALib.getUserName();
				// String pass = OPERALib.getPassword();
				// String channelType = OWSLib.getChannelType(channel);
				// String channelCarrier = OWSLib.getChannelCarier(resort,
				// channelType);
				// OWSLib.setOWSHeader(uName, pass, resort, channelType,
				// channelCarrier);
				setOWSHeader();

				// WSClient.writeToReport(LogStatus.INFO, "Fetching Room
				// Status");
				String fetchRoomStatusReq = WSClient.createSOAPMessage("OWSFetchRoomStatus", "DS_02");
				String fetchRoomStatusRes = WSClient.processSOAPMessage(fetchRoomStatusReq);

				// Checking For Result Flag
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes,
						"FetchRoomStatusResponse_Result_resultStatusFlag", true)) {
					if (WSAssert.assertIfElementValueEquals(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						List<LinkedHashMap<String, String>> dB = new ArrayList<LinkedHashMap<String, String>>();
						List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

						HashMap<String, String> xPath = new HashMap<String, String>();

						// Fetching Record from DB
						String QS02 = WSClient.getQuery("QS_02");
						dB = WSClient.getDBRows(QS02);

						// xPaths of the records being verified and their
						// parents are being put in a hashmap
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomNumber",
								"FetchRoomStatusResponse_RoomStatus");
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomStatus",
								"FetchRoomStatusResponse_RoomStatus");
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomType", "FetchRoomStatusResponse_RoomStatus");
						// xPath.put("FetchRoomStatusResponse_RoomStatus_RoomDescription","FetchRoomStatusResponse_RoomStatus");

						// response records are being stored in a list of
						// hashmaps
						actualValues = WSClient.getMultipleNodeList(fetchRoomStatusRes, xPath, false, XMLType.RESPONSE);

						// database records and response records are being
						// compared
						WSAssert.assertEquals(actualValues, dB, false);

					}
					// Checking if ResultstatusFlag is FAIL
					else {
						WSClient.writeToReport(LogStatus.FAIL, "Fetch Room Status Failed!");
					}

					// Checking for GDSError
					if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_GDSError",
							true)) {
						String message = WSAssert.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "The error displayed response is :" + message);
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
				}

				// Checking for Text Element in Result
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "Result_Text_TextElement", true)) {

					String message = WSAssert.getElementValue(fetchRoomStatusRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + message + "</b>");
				}

				// Checking For OperaErrorCode
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_OperaErrorCode",
						true)) {
					String code = WSAssert.getElementValue(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The error code displayed in the response is :" + code + "</b>");
				}

				// Checking For Fault Schema
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_faultcode", true)) {
					String message = WSClient.getElementValue(fetchRoomStatusRes, "FetchRoomStatusResponse_faultstring",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL,
							"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ RoomType not available -----Blocked");
			}
		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}
	}

	// Fetch Room Status MR-2
	@Test(groups = { "minimumRegression", "ResvAdvanced", "FetchRoomStatus", "OWS" })
	public void fetchRoomStatus_39402() {
		try {
			String testName = "fetchRoomStatus_39402";
			WSClient.startTest(testName,
					"Verify that the Room Status and related details is fetched for the given room type, room number and room number belonging to that room type",
					"minimumRegression");

			String prerequisite[] = { "Rooms", "RoomType" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				// Means Required prerequisite are fulfilled
				// Getting Required values for fetching the Room Status.
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_oresort}", owsresort);
				String chain = OPERALib.getChain();
				String roomNo = OperaPropConfig.getDataSetForCode("Rooms", "DS_31");
				String roomTyp = OperaPropConfig.getDataSetForCode("RoomType", "DS_03");

				// Setting Variables
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_roomNumber}", roomNo);
				WSClient.setData("{var_roomType}", roomTyp);

				// String resort = OPERALib.getResort();
				// String channel = OWSLib.getChannel();
				// String uName = OPERALib.getUserName();
				// String pass = OPERALib.getPassword();
				// String channelType = OWSLib.getChannelType(channel);
				// String channelCarrier = OWSLib.getChannelCarier(resort,
				// channelType);
				// OWSLib.setOWSHeader(uName, pass, resort, channelType,
				// channelCarrier);
				setOWSHeader();

				// WSClient.writeToReport(LogStatus.INFO, "Fetching Room
				// Status");
				String fetchRoomStatusReq = WSClient.createSOAPMessage("OWSFetchRoomStatus", "DS_03");
				String fetchRoomStatusRes = WSClient.processSOAPMessage(fetchRoomStatusReq);

				// Checking For Result Flag
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes,
						"FetchRoomStatusResponse_Result_resultStatusFlag", true)) {
					if (WSAssert.assertIfElementValueEquals(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						// Fetching Record from DB
						String QS03 = WSClient.getQuery("QS_03");
						LinkedHashMap<String, String> dB = WSClient.getDBRow(QS03);

						// Fetching Record from Response
						String roomStatus = WSClient.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_RoomStatus_RoomStatus", XMLType.RESPONSE);
						String roomNumber = WSClient.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_RoomStatus_RoomNumber", XMLType.RESPONSE);
						String roomType = WSClient.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_RoomStatus_RoomType", XMLType.RESPONSE);
						// String
						// frontOfficeStatus=WSClient.getElementValue(fetchRoomStatusRes,
						// "FetchRoomStatusResponse_RoomStatus_FrontOfficeStatus",
						// XMLType.RESPONSE);
						// String houseKeepingStatus =
						// WSClient.getElementValue(fetchRoomStatusRes,
						// "FetchRoomStatusResponse_RoomStatus_HouseKeepingStatus",
						// XMLType.RESPONSE);
						// String houseKeepingInspectionFlag =
						// WSClient.getElementValue(fetchRoomStatusRes,
						// "FetchRoomStatusResponse_RoomStatus_HouseKeepingInspectionFlag",
						// XMLType.RESPONSE);
						// String turnDownYn =
						// WSClient.getElementValue(fetchRoomStatusRes,
						// "FetchRoomStatusResponse_RoomStatus_TurnDownYn",
						// XMLType.RESPONSE);
						// String roomDescription =
						// WSClient.getElementValue(fetchRoomStatusRes,
						// "FetchRoomStatusResponse_RoomStatus_RoomDescription",
						// XMLType.RESPONSE);

						LinkedHashMap<String, String> actualRecord = new LinkedHashMap<>();
						// Now adding roomStatus, frontOfficeStatus,
						// houseKeepingStatus, houseKeepingInspectionFlag,
						// turnDownYn
						// roomNumber, roomType, roomDescription into above
						// HashMap
						actualRecord.put("RoomStatus", roomStatus);
						actualRecord.put("RoomNumber", roomNumber);
						actualRecord.put("RoomType", roomType);
						// actualRecord.put("FrontOfficeStatus",frontOfficeStatus);
						// actualRecord.put("HouseKeepingStatus",
						// houseKeepingStatus);
						// actualRecord.put("HouseKeepingInspectionFlag",
						// houseKeepingInspectionFlag);
						// actualRecord.put("TurnDownYn", turnDownYn);
						// actualRecord.put("RoomDescription", roomDescription);

						WSAssert.assertEquals(dB, actualRecord, false);

					}
					// Checking if ResultstatusFlag is FAIL
					else {
						WSClient.writeToReport(LogStatus.FAIL, "Fetch Room Status Failed!");
					}

					// Checking for GDSError
					if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_GDSError",
							true)) {
						String message = WSAssert.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "The error displayed response is :" + message);
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
				}

				// Checking for Text Element in Result
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "Result_Text_TextElement", true)) {

					String message = WSAssert.getElementValue(fetchRoomStatusRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + message + "</b>");
				}

				// Checking For OperaErrorCode
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_OperaErrorCode",
						true)) {
					String code = WSAssert.getElementValue(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The error code displayed in the response is :" + code + "</b>");
				}

				// Checking For Fault Schema
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_faultcode", true)) {
					String message = WSClient.getElementValue(fetchRoomStatusRes, "FetchRoomStatusResponse_faultstring",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL,
							"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Room , RoomType not available -----Blocked");
			}
		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}
	}

	// Fetch Room Status MR-3
	@Test(groups = { "minimumRegression", "ResvAdvanced", "FetchRoomStatus", "OWS", "fetchRoomStatus_39403" })
	public void fetchRoomStatus_39403() {
		try {
			String testName = "fetchRoomStatus_39403";
			WSClient.startTest(testName,
					"Verify that the Room Status and related details is fetched for the given feature",
					"minimumRegression");

			String prerequisite[] = { "RoomFeature", "RoomType" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				System.out.println("Room Feature and Room Type are Y");
				// Means Required prerequisite are fulfilled
				// Getting Required values for fetching the Room Status.
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_oresort}", owsresort);
				String chain = OPERALib.getChain();
				String feature = OperaPropConfig.getDataSetForCode("RoomFeature", "DS_02");
				String roomTyp = OperaPropConfig.getDataSetForCode("RoomType", "DS_03");
				System.out.println("Room Feature and Room Type are read");
				// Setting Variables
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_roomType}", roomTyp);
				WSClient.setData("{var_feature}", feature);


				setOWSHeader();

				// WSClient.writeToReport(LogStatus.INFO, "Fetching Room
				// Status");
				String fetchRoomStatusReq = WSClient.createSOAPMessage("OWSFetchRoomStatus", "DS_04");
				String fetchRoomStatusRes = WSClient.processSOAPMessage(fetchRoomStatusReq);

				// Checking For Result Flag
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes,
						"FetchRoomStatusResponse_Result_resultStatusFlag", true)) {
					if (WSAssert.assertIfElementValueEquals(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						List<LinkedHashMap<String, String>> dB = new ArrayList<LinkedHashMap<String, String>>();
						List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

						HashMap<String, String> xPath = new HashMap<String, String>();

						// Fetching Record from DB
						String QS05 = WSClient.getQuery("QS_05");
						dB = WSClient.getDBRows(QS05);

						// xPaths of the records being verified and their
						// parents are being put in a hashmap
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomNumber",
								"FetchRoomStatusResponse_RoomStatus");
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomStatus",
								"FetchRoomStatusResponse_RoomStatus");
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomType", "FetchRoomStatusResponse_RoomStatus");
						xPath.put("RoomStatus_Features_Features_Feature", "FetchRoomStatusResponse_RoomStatus");
						xPath.put("FetchRoomStatusResponse_RoomStatus_FrontOfficeStatus",
								"FetchRoomStatusResponse_RoomStatus");
						// xPath.put("FetchRoomStatusResponse_RoomStatus_RoomDescription","FetchRoomStatusResponse_RoomStatus");

						// response records are being stored in a list of
						// hashmaps
						actualValues = WSClient.getMultipleNodeList(fetchRoomStatusRes, xPath, false, XMLType.RESPONSE);

						// database records and response records are being
						// compared
						WSAssert.assertEquals(actualValues, dB, false);

					}
					// Checking if ResultstatusFlag is FAIL
					else {
						WSClient.writeToReport(LogStatus.FAIL, "Fetch Room Status is FAIL!");
					}

					// Checking for GDSError
					if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_GDSError",
							true)) {
						String message = WSAssert.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "The error displayed response is :" + message);
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
				}

				// Checking for Text Element in Result
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "Result_Text_TextElement", true)) {

					String message = WSAssert.getElementValue(fetchRoomStatusRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + message + "</b>");
				}

				// Checking For OperaErrorCode
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_OperaErrorCode",
						true)) {
					String code = WSAssert.getElementValue(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The error code displayed in the response is :" + code + "</b>");
				}

				// Checking For Fault Schema
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_faultcode", true)) {
					String message = WSClient.getElementValue(fetchRoomStatusRes, "FetchRoomStatusResponse_faultstring",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL,
							"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ RoomType, RoomFeature not available -----Blocked");
			}
		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}
	}

	// Fetch Room Status MR-4
	@Test(groups = { "minimumRegression", "ResvAdvanced", "FetchRoomStatus", "OWS" })
	public void fetchRoomStatus_39404() {
		try {
			String testName = "fetchRoomStatus_39404";
			WSClient.startTest(testName,
					"Verify that the Room Status and related details is fetched for the given Room Class",
					"minimumRegression");

			String prerequisite[] = { "RoomClass" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				// Means Required prerequisite are fulfilled
				// Getting Required values for fetching the Room Status.
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_oresort}", owsresort);
				String chain = OPERALib.getChain();
				String roomCls = OperaPropConfig.getDataSetForCode("RoomClass", "DS_03");

				// Setting Variables
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_roomClass}", roomCls);

				setOWSHeader();

				// WSClient.writeToReport(LogStatus.INFO, "Fetching Room
				// Status");
				String fetchRoomStatusReq = WSClient.createSOAPMessage("OWSFetchRoomStatus", "DS_05");
				String fetchRoomStatusRes = WSClient.processSOAPMessage(fetchRoomStatusReq);

				// Checking For Result Flag
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes,
						"FetchRoomStatusResponse_Result_resultStatusFlag", true)) {
					if (WSAssert.assertIfElementValueEquals(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						List<LinkedHashMap<String, String>> dB = new ArrayList<LinkedHashMap<String, String>>();
						List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

						HashMap<String, String> xPath = new HashMap<String, String>();

						// Fetching Record from DB
						String QS06 = WSClient.getQuery("QS_06");
						dB = WSClient.getDBRows(QS06);

						// xPaths of the records being verified and their
						// parents are being put in a hashmap
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomNumber",
								"FetchRoomStatusResponse_RoomStatus");
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomStatus",
								"FetchRoomStatusResponse_RoomStatus");
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomClass", "FetchRoomStatusResponse_RoomStatus");
						// xPath.put("FetchRoomStatusResponse_RoomStatus_RoomType","FetchRoomStatusResponse_RoomStatus");
						// xPath.put("FetchRoomStatusResponse_RoomStatus_RoomDescription","FetchRoomStatusResponse_RoomStatus");
						// xPath.put("FetchRoomStatusResponse_RoomStatus_FrontOfficeStatus","FetchRoomStatusResponse_RoomStatus");

						// response records are being stored in a list of
						// hashmaps
						actualValues = WSClient.getMultipleNodeList(fetchRoomStatusRes, xPath, false, XMLType.RESPONSE);

						// database records and response records are being
						// compared
						WSAssert.assertEquals(actualValues, dB, false);

					}
					// Checking if ResultstatusFlag is FAIL
					else {
						WSClient.writeToReport(LogStatus.FAIL, "Fetch Room Status is FAIL!");
					}

					// Checking for GDSError
					if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_GDSError",
							true)) {
						String message = WSAssert.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "The error displayed response is :" + message);
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
				}

				// Checking for Text Element in Result
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "Result_Text_TextElement", true)) {

					String message = WSAssert.getElementValue(fetchRoomStatusRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + message + "</b>");
				}

				// Checking For OperaErrorCode
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_OperaErrorCode",
						true)) {
					String code = WSAssert.getElementValue(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The error code displayed in the response is :" + code + "</b>");
				}

				// Checking For Fault Schema
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_faultcode", true)) {
					String message = WSClient.getElementValue(fetchRoomStatusRes, "FetchRoomStatusResponse_faultstring",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL,
							"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ RoomClass not available -----Blocked");
			}
		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}
	}

	// Fetch Room Status MR-5
	@Test(groups = { "minimumRegression", "ResvAdvanced", "FetchRoomStatus", "OWS" })
	public void fetchRoomStatus_39405() {
		try {
			String testName = "fetchRoomStatus_39405";
			WSClient.startTest(testName,
					"Verify that the Room Status and related details is fetched for the given Smoking Preference",
					"minimumRegression");

			String prerequisite[] = { "RoomType" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				// Means Required prerequisite are fulfilled
				// Getting Required values for fetching the Room Status.
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_oresort}", owsresort);
				String chain = OPERALib.getChain();
				String roomTyp = OperaPropConfig.getDataSetForCode("RoomType", "DS_03");

				// Setting Variables
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_roomType}", roomTyp);

				// String resort = OPERALib.getResort();
				// String channel = OWSLib.getChannel();
				// String uName = OPERALib.getUserName();
				// String pass = OPERALib.getPassword();
				// String channelType = OWSLib.getChannelType(channel);
				// String channelCarrier = OWSLib.getChannelCarier(resort,
				// channelType);
				// OWSLib.setOWSHeader(uName, pass, resort, channelType,
				// channelCarrier);
				setOWSHeader();

				// WSClient.writeToReport(LogStatus.INFO, "Fetching Room
				// Status");
				String fetchRoomStatusReq = WSClient.createSOAPMessage("OWSFetchRoomStatus", "DS_06");
				String fetchRoomStatusRes = WSClient.processSOAPMessage(fetchRoomStatusReq);

				// Checking For Result Flag
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes,
						"FetchRoomStatusResponse_Result_resultStatusFlag", true)) {
					if (WSAssert.assertIfElementValueEquals(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						List<LinkedHashMap<String, String>> dB = new ArrayList<LinkedHashMap<String, String>>();
						List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

						HashMap<String, String> xPath = new HashMap<String, String>();

						// Fetching Record from DB
						String QS07 = WSClient.getQuery("QS_07");
						dB = WSClient.getDBRows(QS07);

						// xPaths of the records being verified and their
						// parents are being put in a hashmap
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomNumber",
								"FetchRoomStatusResponse_RoomStatus");
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomType", "FetchRoomStatusResponse_RoomStatus");
						xPath.put("FetchRoomStatusResponse_RoomStatus_ServiceStatus",
								"FetchRoomStatusResponse_RoomStatus");
						// xPath.put("FetchRoomStatusResponse_RoomStatus_RoomDescription","FetchRoomStatusResponse_RoomStatus");
						// xPath.put("FetchRoomStatusResponse_RoomStatus_FrontOfficeStatus","FetchRoomStatusResponse_RoomStatus");
						// xPath.put("FetchRoomStatusResponse_RoomStatus_RoomStatus","FetchRoomStatusResponse_RoomStatus");

						// response records are being stored in a list of
						// hashmaps
						actualValues = WSClient.getMultipleNodeList(fetchRoomStatusRes, xPath, false, XMLType.RESPONSE);

						// database records and response records are being
						// compared
						WSAssert.assertEquals(actualValues, dB, false);

					}
					// Checking if ResultstatusFlag is FAIL
					else {
						WSClient.writeToReport(LogStatus.FAIL, "Fetch Room Status is FAIL!");
					}

					// Checking for GDSError
					if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_GDSError",
							true)) {
						String message = WSAssert.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "The error displayed response is :" + message);
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
				}

				// Checking for Text Element in Result
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "Result_Text_TextElement", true)) {

					String message = WSAssert.getElementValue(fetchRoomStatusRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + message + "</b>");
				}

				// Checking For OperaErrorCode
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_OperaErrorCode",
						true)) {
					String code = WSAssert.getElementValue(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The error code displayed in the response is :" + code + "</b>");
				}

				// Checking For Fault Schema
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_faultcode", true)) {
					String message = WSClient.getElementValue(fetchRoomStatusRes, "FetchRoomStatusResponse_faultstring",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL,
							"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ RoomType not available -----Blocked");
			}
		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}
	}

	// Fetch Room Status MR-6
	@Test(groups = { "minimumRegression", "ResvAdvanced", "FetchRoomStatus", "OWS" })
	public void fetchRoomStatus_39406() {
		try {
			String testName = "fetchRoomStatus_39406";
			WSClient.startTest(testName,
					"Verify that the Room Status and related details is fetched for the given floor",
					"minimumRegression");

			String prerequisite[] = { "RoomFloor", "RoomType" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				// Means Required prerequisite are fulfilled
				// Getting Required values for fetching the Room Status.
				String resortOperaValue = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_oresort}", owsresort);
				String chain = OPERALib.getChain();
				String floor = OperaPropConfig.getDataSetForCode("RoomFloor", "DS_02");
				String roomTyp = OperaPropConfig.getDataSetForCode("RoomType", "DS_03");

				// Setting Variables
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_roomType}", roomTyp);
				WSClient.setData("{var_floor}", floor);

				// String resort = OPERALib.getResort();
				// String channel = OWSLib.getChannel();
				// String uName = OPERALib.getUserName();
				// String pass = OPERALib.getPassword();
				// String channelType = OWSLib.getChannelType(channel);
				// String channelCarrier = OWSLib.getChannelCarier(resort,
				// channelType);
				// OWSLib.setOWSHeader(uName, pass, resort, channelType,
				// channelCarrier);
				setOWSHeader();

				// WSClient.writeToReport(LogStatus.INFO, "Fetching Room
				// Status");
				String fetchRoomStatusReq = WSClient.createSOAPMessage("OWSFetchRoomStatus", "DS_07");
				String fetchRoomStatusRes = WSClient.processSOAPMessage(fetchRoomStatusReq);

				// Checking For Result Flag
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes,
						"FetchRoomStatusResponse_Result_resultStatusFlag", true)) {
					if (WSAssert.assertIfElementValueEquals(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						List<LinkedHashMap<String, String>> dB = new ArrayList<LinkedHashMap<String, String>>();
						List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

						HashMap<String, String> xPath = new HashMap<String, String>();

						// Fetching Record from DB
						String QS08 = WSClient.getQuery("QS_08");
						dB = WSClient.getDBRows(QS08);

						// xPaths of the records being verified and their
						// parents are being put in a hashmap
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomNumber",
								"FetchRoomStatusResponse_RoomStatus");
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomStatus",
								"FetchRoomStatusResponse_RoomStatus");
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomType", "FetchRoomStatusResponse_RoomStatus");
						// xPath.put("FetchRoomStatusResponse_RoomStatus_RoomDescription","FetchRoomStatusResponse_RoomStatus");
						xPath.put("FetchRoomStatusResponse_RoomStatus_Floor", "FetchRoomStatusResponse_RoomStatus");

						// response records are being stored in a list of
						// hashmaps
						actualValues = WSClient.getMultipleNodeList(fetchRoomStatusRes, xPath, false, XMLType.RESPONSE);

						// database records and response records are being
						// compared
						WSAssert.assertEquals(actualValues, dB, false);

					}
					// Checking if ResultstatusFlag is FAIL
					else {
						WSClient.writeToReport(LogStatus.FAIL, "Fetch Room Status is FAIL!");
					}

					// Checking for GDSError
					if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_GDSError",
							true)) {
						String message = WSAssert.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "The error displayed response is :" + message);
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
				}

				// Checking for Text Element in Result
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "Result_Text_TextElement", true)) {

					String message = WSAssert.getElementValue(fetchRoomStatusRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + message + "</b>");
				}

				// Checking For OperaErrorCode
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_OperaErrorCode",
						true)) {
					String code = WSAssert.getElementValue(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The error code displayed in the response is :" + code + "</b>");
				}

				// Checking For Fault Schema
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_faultcode", true)) {
					String message = WSClient.getElementValue(fetchRoomStatusRes, "FetchRoomStatusResponse_faultstring",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL,
							"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ RoomType, RoomFeature not available -----Blocked");
			}
		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}
	}

	// Fetch Room Status MR-7
	@Test(groups = { "minimumRegression", "ResvAdvanced", "FetchRoomStatus", "OWS" })
	public void fetchRoomStatus_39407() {
		try {
			String testName = "fetchRoomStatus_39407";
			WSClient.startTest(testName,
					"Verify that Room Status and related details are NOT fetched for the given room number when an invalid HotelCode is passed",
					"minimumRegression");

			String prerequisite[] = { "Rooms" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				// Means Required prerequisite are fulfilled
				// Getting Required values for fetching the Room Status.
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_oresort}", owsresort);
				String roomNumber = OperaPropConfig.getDataSetForCode("Rooms", "DS_31");

				// Setting Variables
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_roomNumber}", roomNumber);

				// String resort = OPERALib.getResort();
				// String channel = OWSLib.getChannel();
				// String uName = OPERALib.getUserName();
				// String pass = OPERALib.getPassword();
				// String channelType = OWSLib.getChannelType(channel);
				// String channelCarrier = OWSLib.getChannelCarier(resort,
				// channelType);
				// OWSLib.setOWSHeader(uName, pass, resort, channelType,
				// channelCarrier);
				setOWSHeader();

				// WSClient.writeToReport(LogStatus.INFO, "Fetching Room
				// Status");
				String fetchRoomStatusReq = WSClient.createSOAPMessage("OWSFetchRoomStatus", "DS_09");
				String fetchRoomStatusRes = WSClient.processSOAPMessage(fetchRoomStatusReq);

				// Checking For Result Flag
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes,
						"FetchRoomStatusResponse_Result_resultStatusFlag", true)) {
					if (WSAssert.assertIfElementValueEquals(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_resultStatusFlag", "FAIL", false)) {
						WSClient.writeToReport(LogStatus.PASS, "Fetch Room Status Failed");
					}
					// Checking if ResultstatusFlag is SUCCESS
					else {
						WSClient.writeToReport(LogStatus.FAIL, "Fetch Room Status Should have failed!");
					}

					// Checking for GDSError
					if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_GDSError",
							true)) {
						String message = WSAssert.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "The error displayed response is :" + message);
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
				}

				// //Checking for Text Element in Result
				// if (WSAssert.assertIfElementExists(fetchRoomStatusRes,
				// "Result_Text_TextElement", true))
				// {
				//
				// String message = WSAssert.getElementValue(fetchRoomStatusRes,
				// "Result_Text_TextElement", XMLType.RESPONSE);
				// WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text
				// displayed in the response is :" + message+"</b>");
				// }

				// Checking For OperaErrorCode
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_OperaErrorCode",
						true)) {
					String code = WSAssert.getElementValue(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The error code displayed in the response is :" + code + "</b>");
				}

				// Checking For Fault Schema
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_faultcode", true)) {
					String message = WSClient.getElementValue(fetchRoomStatusRes, "FetchRoomStatusResponse_faultstring",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL,
							"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Rooms not available -----Blocked");
			}
		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}

	}

	// Fetch Room Status MR-8
	@Test(groups = { "minimumRegression", "ResvAdvanced", "FetchRoomStatus", "OWS" })
	public void fetchRoomStatus_40340() {
		try {
			String testName = "fetchRoomStatus_40340";
			WSClient.startTest(testName,
					"Verify that the Room Status and related details is fetched for the given property",
					"minimumRegression");

			String prerequisite[] = { "Rooms" };
			if (OperaPropConfig.getPropertyConfigResults(prerequisite)) {
				// Means Required prerequisite are fulfilled
				// Getting Required values for fetching the Room Status.
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_oresort}", owsresort);

				// Setting Variables
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_chain}", chain);

				// String resort = OPERALib.getResort();
				// String channel = OWSLib.getChannel();
				// String uName = OPERALib.getUserName();
				// String pass = OPERALib.getPassword();
				// String channelType = OWSLib.getChannelType(channel);
				// String channelCarrier = OWSLib.getChannelCarier(resort,
				// channelType);
				// OWSLib.setOWSHeader(uName, pass, resort, channelType,
				// channelCarrier);
				setOWSHeader();

				// WSClient.writeToReport(LogStatus.INFO, "Fetching Room
				// Status");
				String fetchRoomStatusReq = WSClient.createSOAPMessage("OWSFetchRoomStatus", "DS_08");
				String fetchRoomStatusRes = WSClient.processSOAPMessage(fetchRoomStatusReq);

				// Checking For Result Flag
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes,
						"FetchRoomStatusResponse_Result_resultStatusFlag", true)) {
					if (WSAssert.assertIfElementValueEquals(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						List<LinkedHashMap<String, String>> dB = new ArrayList<LinkedHashMap<String, String>>();
						List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

						HashMap<String, String> xPath = new HashMap<String, String>();

						// Fetching Record from DB
						String QS09 = WSClient.getQuery("QS_09");
						dB = WSClient.getDBRows(QS09);

						// xPaths of the records being verified and their
						// parents are being put in a hashmap
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomNumber",
								"FetchRoomStatusResponse_RoomStatus");
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomStatus",
								"FetchRoomStatusResponse_RoomStatus");
						xPath.put("FetchRoomStatusResponse_RoomStatus_RoomType", "FetchRoomStatusResponse_RoomStatus");
						// xPath.put("FetchRoomStatusResponse_RoomStatus_RoomDescription","FetchRoomStatusResponse_RoomStatus");

						// response records are being stored in a list of
						// hashmaps
						actualValues = WSClient.getMultipleNodeList(fetchRoomStatusRes, xPath, false, XMLType.RESPONSE);

						// database records and response records are being
						// compared
						WSAssert.assertEquals(actualValues, dB, false);
					}
					// Checking if ResultstatusFlag is FAIL
					else {
						WSClient.writeToReport(LogStatus.FAIL, "Fetch Room Status Failed!");
					}

					// Checking for GDSError
					if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_GDSError",
							true)) {
						String message = WSAssert.getElementValue(fetchRoomStatusRes,
								"FetchRoomStatusResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.FAIL, "The error displayed response is :" + message);
					}
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag Itself does not Exist in the response!");
				}

				// Checking for Text Element in Result
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "Result_Text_TextElement", true)) {

					String message = WSAssert.getElementValue(fetchRoomStatusRes, "Result_Text_TextElement",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The text displayed in the response is :" + message + "</b>");
				}

				// Checking For OperaErrorCode
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_Result_OperaErrorCode",
						true)) {
					String code = WSAssert.getElementValue(fetchRoomStatusRes,
							"FetchRoomStatusResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "The error code displayed in the response is :" + code + "</b>");
				}

				// Checking For Fault Schema
				if (WSAssert.assertIfElementExists(fetchRoomStatusRes, "FetchRoomStatusResponse_faultcode", true)) {
					String message = WSClient.getElementValue(fetchRoomStatusRes, "FetchRoomStatusResponse_faultstring",
							XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.FAIL,
							"<b>" + "Fault Schema in Response with message: " + "</b>" + message);
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites failed!------ Rooms not available -----Blocked");
			}
		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		} finally {

		}

	}

}
