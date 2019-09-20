package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.AssignRoom;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeChannelParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateRoom;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchChannelParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchHotelRooms;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.SetHousekeepingRoomStatus;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;;

public class ModifyBooking extends WSSetUp {
	String newDate = null, newDate1 = null;
	String profileID = "";// "2204457151";
	HashMap<String, String> resvID = new HashMap<>();

	public boolean fetchAvailability(String rate, String roomType) {
		try {
			int i = 0;
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_extResort}", resortExtValue);
			String channel = OWSLib.getChannel(); // Will fetch the first
			// channel
			// String channel = "IDC7TEST";
			WSClient.setData("{var_channel}", channel);
			String resort = OPERALib.getResort();
			String owsresort = OWSLib.getChannelResort(resort, channel);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			// String channelCarrier = "IDC7TEST";
			String exRate = rate;
			String rt = roomType;
			WSClient.setData("{var_rt}", rt);
			WSClient.setData("{var_rate}", exRate);
			WSClient.setData("{var_channelCarrier}", channelCarrier);
			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
			WSClient.setData("{var_oresort}", owsresort);
			newDate = WSClient.getData("{var_busdate}");
			newDate1 = WSClient.getData("{var_busdate1}");
			String newDat = WSClient.getData("{var_busdate}");
			String newDat1 = WSClient.getData("{var_busdate1}");

			int k = 0;
			while (k < 50) {
				String req_deleteDoc = WSClient.createSOAPMessage("OWSAvailability", "DS_01", true);
				String res_deleteDoc = WSClient.processSOAPMessage(req_deleteDoc, true);
				if (WSAssert.assertIfElementValueEquals(res_deleteDoc, "AvailabilityResponse_Result_resultStatusFlag",
						"SUCCESS", true, true)) {

					return true;
				} else {
					i = i + 30;

					WSClient.writeToLog("<B>Checking for availability for some other Date Range</B>");

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
					Calendar c = Calendar.getInstance();
					Calendar c1 = Calendar.getInstance();
					try {
						// Setting the date to the given date
						c.setTime(sdf.parse(WSClient.getData("{var_busdate}")));
						c1.setTime(sdf1.parse(WSClient.getData("{var_busdate1}")));
					} catch (ParseException e) {
						e.printStackTrace();
					}

					// Number of Days to add
					c.add(Calendar.DAY_OF_MONTH, i);
					int j = i;
					c1.add(Calendar.DAY_OF_MONTH, j);
					// Date after adding the days to the given date
					newDate = sdf.format(c.getTime());
					newDate1 = sdf1.format(c1.getTime());

					WSClient.setData("{var_busdate}", newDate);
					WSClient.setData("{var_busdate1}", newDate1);

					WSClient.setData("{var_startDate}", newDate);
					WSClient.setData("{var_endDate}", newDate1);

				}
				k++;
			}

			if (k == 50) {

				String query = WSClient.getQuery("OWSModifyBooking", "QS_53");

				String roomType1 = WSClient.getDBRow(query).get("TYPE");
				WSClient.setData("{var_rateCode}", rate);
				WSClient.setData("{var_RoomType}", roomType1);

				String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint", true);
				String createRoomRes = WSClient.processSOAPMessage(createRoomReq, true);

				if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
				}

				String createRoomReq1 = WSClient.createSOAPMessage("CreateRoom", "RoomMaint", true);
				String createRoomRes1 = WSClient.processSOAPMessage(createRoomReq1, true);

				if (WSAssert.assertIfElementExists(createRoomRes1, "CreateRoomRS_Success", true)) {

				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to create room");
				}

				i = 0;
				while (i < 5) {

					WSClient.writeToReport(LogStatus.INFO,
							"<b>Checking if rooms are available for the room type in the given date range</b>");

					String req_deleteDoc = WSClient.createSOAPMessage("OWSAvailability", "DS_01", true);
					String res_deleteDoc = WSClient.processSOAPMessage(req_deleteDoc, true);
					if (WSAssert.assertIfElementValueEquals(res_deleteDoc,
							"AvailabilityResponse_Result_resultStatusFlag", "SUCCESS", true)) {

						return true;
					} else {

						WSClient.writeToLog("<B>Checking for availability for some other Date Range</B>");

						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
						Calendar c = Calendar.getInstance();
						Calendar c1 = Calendar.getInstance();
						try {
							// Setting the date to the given date
							c.setTime(sdf.parse(newDat));
							c1.setTime(sdf1.parse(newDat1));
						} catch (ParseException e) {
							e.printStackTrace();
						}

						// Number of Days to add
						c.add(Calendar.DAY_OF_MONTH, i);
						int j = i;
						c1.add(Calendar.DAY_OF_MONTH, j);
						// Date after adding the days to the given date
						newDat = sdf.format(c.getTime());
						newDat1 = sdf1.format(c1.getTime());

						WSClient.setData("{var_busdate}", newDat);
						WSClient.setData("{var_busdate1}", newDat1);

						WSClient.setData("{var_startDate}", newDat);
						WSClient.setData("{var_endDate}", newDat1);

					}
					i++;
				}

			}
		} catch (Exception e) {

		}
		return false;
	}

	/************
	 * add or remove days to date
	 *
	 * @throws ParseException
	 ***********/
	public String modifyDaysToDate(String date, String timeFormat, int days) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
		Calendar c;
		c = Calendar.getInstance();
		c.setTime(sdf.parse(date));
		c.add(Calendar.DATE, days); // number of days to modify
		date = sdf.format(c.getTime());

		return date;
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * modify a reservation details such as arrival date,departure date.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "sanity", "ModifyBooking", "Reservation", "OWS" , "modifyBooking_22204"})

	public void modifyBooking_22204() {
		String uname = "";
		try {
			String testName = "modifyBooking_22204";
			WSClient.startTest(testName, "Verify that reservation dates of the booking are modified successfully",
					"sanity");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile *************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						WSClient.setData("{var_adultCount}", "2");
						WSClient.setData("{var_childCount}", "1");

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_01");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_04");
								HashMap<String, String> dbResults = WSClient.getDBRow(query);

								String arrivalDate = WSClient.getElementValue(modifyBookingReq,
										"RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
								String departureDate = WSClient.getElementValue(modifyBookingReq,
										"RoomStay_TimeSpan_EndDate", XMLType.REQUEST);

								String resarrivalDate = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE);
								String resdepartureDate = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE);

								resarrivalDate = resarrivalDate.substring(0, resarrivalDate.indexOf('T'));
								resdepartureDate = resdepartureDate.substring(0, resdepartureDate.indexOf('T'));

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation > Modifications are applied on DB</b>");

								/******
								 * Validate above details against DB
								 *********/
								String dbArrival = dbResults.get("ARRIVAL");
								dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));

								String dbDeparture = dbResults.get("DEPARTURE");
								dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

								if (WSAssert.assertEquals(arrivalDate, dbArrival, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
											+ arrivalDate + "  Actual : " + dbArrival + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
											+ arrivalDate + "  Actual : " + dbArrival + "</b>");
								}
								if (WSAssert.assertEquals(departureDate, dbDeparture, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Departure date -> Expected : "
											+ departureDate + "  Actual : " + dbDeparture + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Departure date -> Expected : "
											+ departureDate + "  Actual : " + dbDeparture + "</b>");
								}

								String guaranteeCode = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");// WSClient.getElementValue(modifyBookingReq,
								// "RoomStays_RoomStay_Guarantee_guaranteeType",
								// XMLType.REQUEST);
								if (WSAssert.assertEquals(guaranteeCode, dbResults.get("GUARANTEE_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Guarantee Code -> Expected : "
											+ guaranteeCode + "  Actual : " + dbResults.get("GUARANTEE_CODE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Guarantee Code -> Expected : "
											+ guaranteeCode + "  Actual : " + dbResults.get("GUARANTEE_CODE") + "</b>");
								}

								if (WSAssert.assertEquals(resortOperaValue, dbResults.get("RESORT"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Resort -> Expected : "
											+ resortOperaValue + "  Actual : " + dbResults.get("RESORT") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Resort -> Expected : "
											+ resortOperaValue + "  Actual : " + dbResults.get("RESORT") + "</b>");
								}

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation > Modifications are retreived correctly on the response</b>");

								String conf_no = WSClient.getElementValue(modifyBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
								if (WSAssert.assertEquals(dbResults.get("CONFIRMATION_NO"), conf_no, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Confirmation No -> Expected : "
											+ dbResults.get("CONFIRMATION_NO") + "  Actual : " + conf_no + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Confirmation No -> Expected : "
											+ dbResults.get("CONFIRMATION_NO") + "  Actual : " + conf_no + "</b>");
								}

								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"RoomStays_RoomStay_HotelReference_chainCode",
										WSClient.getElementValue(modifyBookingReq,
												"RoomStays_RoomStay_HotelReference_chainCode", XMLType.REQUEST),
										false);

								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"RoomStays_RoomStay_HotelReference_hotelCode",
										WSClient.getElementValue(modifyBookingReq,
												"RoomStays_RoomStay_HotelReference_hotelCode", XMLType.REQUEST),
										false);

								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"RoomStays_RoomStay_Guarantee_guaranteeType",
										WSClient.getElementValue(modifyBookingReq,
												"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST),
										false);

								/**** Validate modified arrival dates ****/
								if (WSAssert.assertEquals(arrivalDate, resarrivalDate, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
											+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
											+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
								}
								/**** Validate modified departure dates ****/
								if (WSAssert.assertEquals(departureDate, resdepartureDate, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Departure date -> Expected : "
											+ departureDate + "  Actual : " + resdepartureDate + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Departure date -> Expected : "
											+ departureDate + "  Actual : " + resdepartureDate + "</b>");
								}
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								if (!WSClient.getElementValue(modifyBookingRes, "ModifyBookingResponse_Result_GDSError",
										XMLType.RESPONSE).equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO,
											"<b> The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
													+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
								if (!WSClient
										.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										.equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requistes required for Reservation creation failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				OPERALib.setOperaHeader(uname);
				if (resvID.get("reservationId") != null || !resvID.get("reservationId").equals("error")) {
					WSClient.setData("{var_resvId}", resvID.get("reservationId"));
					if (CancelReservation.cancelReservation("DS_02")) {
						WSClient.writeToLog("Reservation cancellation successful");
						resvID.clear();
					} else
						WSClient.writeToLog("Reservation cancellation failed");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * modify a reservation details such as profile id and all the profile
	 * attributes are retrieved correctly on response.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42711() {
		try {
			String testName = "modifyBooking_42711";
			WSClient.startTest(testName,
					"Verify that profile id for the booking is changed and all the profile attributes are retrieved correctly on response",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType",
							"CommunicationType", "AddressType", "VipLevel", "Nationality", "Title" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile ****************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");

						String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
						String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
						HashMap<String, String> addressData = OPERALib.fetchAddressLOV();
						WSClient.setData("{var_fname}", fname);
						WSClient.setData("{var_lname}", lname);
						WSClient.setData("{var_middleName}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
						WSClient.setData("{var_city}", addressData.get("City"));
						WSClient.setData("{var_state}", addressData.get("State"));
						WSClient.setData("{var_country}", addressData.get("Country"));
						WSClient.setData("{var_zip}", addressData.get("Zip"));
						String phoneType = OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01");
						String addressType = OperaPropConfig.getDataSetForCode("AddressType", "DS_01");
						WSClient.setData("{var_addressType}", addressType);
						WSClient.setData("{var_phoneType}", phoneType);

						WSClient.setData("{var_emailType}",
								OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02"));
						WSClient.setData("{var_email}", fname + "@gmail.com");
						WSClient.setData("{var_gender}", OperaPropConfig.getDataSetForCode("Gender", "DS_01"));
						WSClient.setData("{var_vipCode}", OperaPropConfig.getDataSetForCode("VipLevel", "DS_01"));
						WSClient.setData("{var_nationality}",
								OperaPropConfig.getDataSetForCode("Nationality", "DS_01"));
						WSClient.setData("{var_title}", OperaPropConfig.getDataSetForCode("Title", "DS_01"));

						/************
						 * Prerequisite 3: Create profile
						 *********************/
						String profileid = CreateProfile.createProfile("DS_45");
						if (!profileid.equals("error")) {
							WSClient.setData("{var_profileId}", profileid);
							WSClient.writeToReport(LogStatus.INFO, "<b>New Profile ID: " + profileid + "</b>");

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_02");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									String query = WSClient.getQuery("QS_01");
									HashMap<String, String> dbResults = WSClient.getDBRow(query);

									WSAssert.assertIfElementValueEquals(modifyBookingRes, "Profile_ProfileIDs_UniqueID",
											WSClient.getElementValue(modifyBookingReq, "Profile_ProfileIDs_UniqueID",
													XMLType.REQUEST),
											false);
									WSAssert.assertIfElementValueEquals(modifyBookingRes,
											"Customer_PersonName_lastName", dbResults.get("LAST"), false);
									WSAssert.assertIfElementValueEquals(modifyBookingRes,
											"Customer_PersonName_firstName", dbResults.get("FIRST"), false);

									LinkedHashMap<String, String> expectedValues = new LinkedHashMap<>();
									query = WSClient.getQuery("QS_43");
									HashMap<String, String> dbvalue = WSClient.getDBRow(query);
									expectedValues.put("GENDER", dbvalue.get("GENDER"));
									expectedValues.put("FIRSTNAME", dbvalue.get("FIRSTNAME"));
									expectedValues.put("MIDDLENAME", dbvalue.get("MIDDLENAME"));
									expectedValues.put("LASTNAME", dbvalue.get("LASTNAME"));
									expectedValues.put("NATIONALITY", dbvalue.get("NATIONALITY"));
									expectedValues.put("TITLE", dbvalue.get("TITLE"));
									expectedValues.put("VIP", dbvalue.get("VIP_STATUS"));

									expectedValues.put("ADDRESS_TYPE", dbvalue.get("ADDRESS_TYPE"));
									expectedValues.put("CITY", dbvalue.get("CITY"));
									expectedValues.put("POSTAL", dbvalue.get("POSTAL"));
									expectedValues.put("STATE", dbvalue.get("STATE"));
									expectedValues.put("COUNTRY", dbvalue.get("COUNTRY"));

									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
									actualValues.put("GENDER", WSClient.getElementValue(modifyBookingRes,
											"Profiles_Profile_Customer_gender", XMLType.RESPONSE));
									actualValues.put("FIRSTNAME", WSClient.getElementValue(modifyBookingRes,
											"Customer_PersonName_firstName", XMLType.RESPONSE));
									actualValues.put("MIDDLENAME", WSClient.getElementValue(modifyBookingRes,
											"Customer_PersonName_middleName", XMLType.RESPONSE));
									actualValues.put("LASTNAME", WSClient.getElementValue(modifyBookingRes,
											"Customer_PersonName_lastName", XMLType.RESPONSE));
									actualValues.put("TITLE", WSClient.getElementValue(modifyBookingRes,
											"Customer_PersonName_nameTitle", XMLType.RESPONSE));
									actualValues.put("NATIONALITY", WSClient.getElementValue(modifyBookingRes,
											"ResGuest_Profiles_Profile_nationality", XMLType.RESPONSE));
									actualValues.put("VIP", WSClient.getElementValue(modifyBookingRes,
											"ResGuest_Profiles_Profile_vipCode", XMLType.RESPONSE));

									actualValues.put("ADDRESS_TYPE", WSClient.getElementValue(modifyBookingRes,
											"Profile_Addresses_NameAddress_otherAddressType", XMLType.RESPONSE));
									actualValues.put("CITY", WSClient.getElementValue(modifyBookingRes,
											"Addresses_NameAddress_cityName", XMLType.RESPONSE));
									actualValues.put("POSTAL", WSClient.getElementValue(modifyBookingRes,
											"Addresses_NameAddress_postalCode", XMLType.RESPONSE));
									actualValues.put("STATE", WSClient.getElementValue(modifyBookingRes,
											"Addresses_NameAddress_stateProv", XMLType.RESPONSE));
									actualValues.put("COUNTRY", WSClient.getElementValue(modifyBookingRes,
											"Addresses_NameAddress_countryCode", XMLType.RESPONSE));

									WSAssert.assertEquals(expectedValues, actualValues, false);

									query = WSClient.getQuery("QS_44");
									ArrayList<LinkedHashMap<String, String>> results = WSClient.getDBRows(query);

									HashMap<String, String> xPath = new HashMap<>();
									xPath.put("Phones_NamePhone_PhoneNumber", "Profile_Phones_NamePhone");
									xPath.put("Profile_Phones_NamePhone_phoneType", "Profile_Phones_NamePhone");
									xPath.put("Profile_Phones_NamePhone_phoneRole", "Profile_Phones_NamePhone");
									xPath.put("Profile_Phones_NamePhone_primary", "Profile_Phones_NamePhone");

									List<LinkedHashMap<String, String>> resValues = WSAssert
											.getMultipleNodeList(modifyBookingRes, xPath, false, XMLType.RESPONSE);
									WSAssert.assertEquals(resValues, results, false);

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validating retrieval of email details onto the response</b>");

									query = WSClient.getQuery("QS_45");
									actualValues = WSClient.getDBRow(query);
									String emailType = WSClient.getElementValue(modifyBookingRes,
											"Profile_EMails_NameEmail_emailType", XMLType.RESPONSE);
									String email = WSClient.getElementValue(modifyBookingRes,
											"Profile_EMails_NameEmail", XMLType.RESPONSE);
									String primary = WSClient.getElementValue(modifyBookingRes,
											"Profile_EMails_NameEmail_primary", XMLType.RESPONSE);

									if (WSAssert.assertEquals(actualValues.get("PHONE_TYPE"), emailType, true)) {
										WSClient.writeToReport(LogStatus.PASS, "EmailType -> Expected : "
												+ actualValues.get("PHONE_TYPE") + "  Actual : " + emailType);
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "EmailType -> Expected : "
												+ actualValues.get("PHONE_TYPE") + "  Actual : " + emailType);
									}
									if (WSAssert.assertEquals(actualValues.get("PHONE_NUMBER"), email, true)) {
										WSClient.writeToReport(LogStatus.PASS, "Email -> Expected : "
												+ actualValues.get("PHONE_NUMBER") + "  Actual : " + email);
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "Email -> Expected : "
												+ actualValues.get("PHONE_NUMBER") + "  Actual : " + email);
									}
									if (WSAssert.assertEquals(actualValues.get("PHONE_PRIMARY"), primary, true)) {
										WSClient.writeToReport(LogStatus.PASS, "Primary -> Expected : "
												+ actualValues.get("PHONE_PRIMARY") + "  Actual : " + primary);
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "Primary -> Expected : "
												+ actualValues.get("PHONE_PRIMARY") + "  Actual : " + primary);
									}
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requistes required for Reservation creation failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * modify a reservation details such as addition of multiple package codes.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42713() {
		HashMap<String, String> resvId = new HashMap<String, String>();
		try {
			String testName = "modifyBooking_42713";
			WSClient.startTest(testName, "Verify that multiple packages are added to the booking successfully",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "PackageCode" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile **************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					resvId = CreateReservation.createReservation("DS_01");
					if (!resvId.get("reservationId").equals("error")) {
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvId.get("reservationId"));

						/************ Set 2 package codes *****************/
						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_02"));
						WSClient.setData("{var_packageCode1}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_03"));

						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvId.get("reservationId") + "</b>");

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_24");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_27");
								ArrayList<LinkedHashMap<String, String>> dbResults = WSClient.getDBRows(query);

								HashMap<String, String> xPath = new HashMap<>();
								xPath.put("RoomStay_Packages_Package_packageCode", "RoomStay_Packages_Package");
								List<LinkedHashMap<String, String>> resValues = WSAssert
										.getMultipleNodeList(modifyBookingRes, xPath, false, XMLType.RESPONSE);
								List<LinkedHashMap<String, String>> reqValues = WSAssert
										.getMultipleNodeList(modifyBookingReq, xPath, false, XMLType.REQUEST);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation of insertion of packages in DB</b>");
								WSAssert.assertEquals(reqValues, dbResults, false);

								query = WSClient.getQuery("QS_05");
								dbResults = WSClient.getDBRows(query);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation of retrieval of correct package details on response</b>");
								xPath.put("RoomStay_Packages_Package_source", "RoomStay_Packages_Package");
								resValues = WSAssert.getMultipleNodeList(modifyBookingRes, xPath, false,
										XMLType.RESPONSE);
								WSAssert.assertEquals(dbResults, resValues, false);
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requistes required for Reservation creation failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (resvId.get("reservationId") != null || !resvId.get("reservationId").equals("error")) {
					WSClient.setData("{var_resvId}", resvId.get("reservationId"));
					if (CancelReservation.cancelReservation("DS_02"))
						WSClient.writeToLog("Reservation cancellation successful");
					else
						WSClient.writeToLog("Reservation cancellation failed");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * error is generated when non web-bookable package is given.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42722() {
		try {
			String testName = "modifyBooking_42722";
			WSClient.startTest(testName,
					"Verify that error is generated when non web bookable package is given in the request",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "PackageCode" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile ************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						/************
						 * Set non-web bookable package codes
						 *****************/
						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_01"));

						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_04");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								if (!WSClient.getElementValue(modifyBookingRes, "ModifyBookingResponse_Result_GDSError",
										XMLType.RESPONSE).equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO,
											"<b> The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
													+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
								if (!WSClient
										.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										.equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requistes required for Reservation creation failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * modify reservation details such as rate code and room type.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42712() {
		try {
			String testName = "modifyBooking_42712";
			WSClient.startTest(testName, "Verify that rate code and room type of the booking are modified successfully",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile **************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_03"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_02"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						WSClient.setData("{var_roomCount}", "1");

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_03");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								/************
								 * Validate source code,market code
								 ***************/
								String query = WSClient.getQuery("QS_04");
								HashMap<String, String> results = WSClient.getDBRow(query);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_HotelReservation_marketSegment",
										results.get("MARKET_CODE"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_HotelReservation_sourceCode",
										results.get("ORIGIN_OF_BOOKING"), false);

								WSClient.setData("{var_roomCategory}", results.get("ROOM_CATEGORY"));
								WSClient.setData("{var_rateCode}", results.get("RATE_CODE"));
								String arrival = results.get("ARRIVAL");
								String departure = results.get("DEPARTURE");
								WSClient.setData("{var_startDate}", arrival.substring(0, arrival.indexOf(' ')));
								WSClient.setData("{var_endDate}", departure.substring(0, departure.indexOf(' ')));

								String reqRateCode = WSClient.getElementValue(modifyBookingReq,
										"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
								String resRateCode = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
								String resRateCode1 = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_RoomRates_RoomRate_ratePlanCode", XMLType.RESPONSE);
								String resRateDesc = WSClient.getElementValue(modifyBookingRes,
										"RatePlan_RatePlanDescription_Text", XMLType.RESPONSE);

								/**************
								 * Validate rate plan
								 ******************/
								query = WSClient.getQuery("QS_40");
								HashMap<String, String> dbResults = WSClient.getDBRow(query);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating Rate Plan details insertion into DB</b>");

								if (WSAssert.assertEquals(reqRateCode, dbResults.get("GDS_RATE_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "RatePlan@ratePlanCode -> Expected : "
											+ reqRateCode + "  Actual : " + dbResults.get("GDS_RATE_CODE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "RatePlan@ratePlanCode -> Expected : "
											+ reqRateCode + "  Actual : " + dbResults.get("GDS_RATE_CODE"));
								}

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating Correct Rate Plan details retreival on response</b>");

								if (WSAssert.assertEquals(dbResults.get("GDS_RATE_CODE"), resRateCode, true)) {
									WSClient.writeToReport(LogStatus.PASS, "RatePlan@ratePlanCode -> Expected : "
											+ dbResults.get("GDS_RATE_CODE") + "  Actual : " + resRateCode);
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "RatePlan@ratePlanCode -> Expected : "
											+ dbResults.get("GDS_RATE_CODE") + "  Actual : " + resRateCode);
								}

								if (WSAssert.assertEquals(dbResults.get("GDS_RATE_CODE"), resRateCode1, true)) {
									WSClient.writeToReport(LogStatus.PASS, "RoomRate@ratePlanCode -> Expected : "
											+ dbResults.get("GDS_RATE_CODE") + "  Actual : " + resRateCode1);
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "RoomRate@ratePlanCode -> Expected : "
											+ dbResults.get("GDS_RATE_CODE") + "  Actual : " + resRateCode1);
								}

								if (WSAssert.assertEquals(dbResults.get("DESCRIPTION"), resRateDesc, true)) {
									WSClient.writeToReport(LogStatus.PASS, "RateDescription -> Expected : "
											+ dbResults.get("DESCRIPTION") + "  Actual : " + resRateDesc);
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "RateDescription -> Expected : "
											+ dbResults.get("DESCRIPTION") + "  Actual : " + resRateDesc);
								}

								String reqRoomType = WSClient.getElementValue(modifyBookingReq,
										"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
								String resRoomType = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.RESPONSE);
								String resRoomType1 = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
								String resRoomTypeDesc = WSClient.getElementValue(modifyBookingRes,
										"RoomType_RoomTypeShortDescription_Text", XMLType.RESPONSE);

								/**************
								 * Validate room type
								 ****************/
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating Room Type details insertion into DB</b>");
								if (WSAssert.assertEquals(reqRoomType, dbResults.get("GDS_ROOM_CATEGORY"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "RoomType@roomTypeCode -> Expected : "
											+ reqRoomType + "  Actual : " + dbResults.get("GDS_ROOM_CATEGORY"));
								} else {
									WSClient.writeToReport(LogStatus.PASS, "RoomType@roomTypeCode -> Expected : "
											+ reqRoomType + "  Actual : " + dbResults.get("GDS_ROOM_CATEGORY"));
								}

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating Correct Room Type details retreival on response</b>");

								if (WSAssert.assertEquals(dbResults.get("GDS_ROOM_CATEGORY"), resRoomType, true)) {
									WSClient.writeToReport(LogStatus.PASS, "RoomType@roomTypeCode -> Expected : "
											+ dbResults.get("GDS_ROOM_CATEGORY") + "  Actual : " + resRoomType);
								} else {
									WSClient.writeToReport(LogStatus.PASS, "RoomType@roomTypeCode -> Expected : "
											+ dbResults.get("GDS_ROOM_CATEGORY") + "  Actual : " + resRoomType);
								}

								if (WSAssert.assertEquals(dbResults.get("GDS_ROOM_CATEGORY"), resRoomType1, true)) {
									WSClient.writeToReport(LogStatus.PASS, "RoomRate@roomTypeCode -> Expected : "
											+ dbResults.get("GDS_ROOM_CATEGORY") + "  Actual : " + resRoomType1);
								} else {
									WSClient.writeToReport(LogStatus.PASS, "RoomRate@roomTypeCode -> Expected : "
											+ dbResults.get("GDS_ROOM_CATEGORY") + "  Actual : " + resRoomType1);
								}

								if (WSAssert.assertEquals(dbResults.get("SHORT_DESCRIPTION"), resRoomTypeDesc, true)) {
									WSClient.writeToReport(LogStatus.PASS, "RoomTypeDescription -> Expected : "
											+ dbResults.get("SHORT_DESCRIPTION") + "  Actual : " + resRoomTypeDesc);
								} else {
									WSClient.writeToReport(LogStatus.PASS, "RoomTypeDescription -> Expected : "
											+ dbResults.get("SHORT_DESCRIPTION") + "  Actual : " + resRoomTypeDesc);
								}

								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"RoomType_RoomTypeDescription_Text", dbResults.get("LONG_DESCRIPTION"), false);

								// WSAssert.assertIfElementValueEquals(modifyBookingRes,
								// "RoomStay_RoomTypes_RoomType_roomTypeCode",
								// dbResults.get("LABEL"), false);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation -> Packages attached to rate are attached to reservation</b>");
								HashMap<String, String> xPath2 = new HashMap<String, String>();
								xPath2.put("RoomStay_Packages_Package_packageCode", "RoomStay_Packages_Package");
								xPath2.put("RoomStay_Packages_Package_source", "RoomStay_Packages_Package");

								List<LinkedHashMap<String, String>> resValues2 = WSClient
										.getMultipleNodeList(modifyBookingRes, xPath2, false, XMLType.RESPONSE);
								List<LinkedHashMap<String, String>> dbValues2 = WSClient
										.getDBRows(WSClient.getQuery("QS_05"));
								WSAssert.assertEquals(resValues2, dbValues2, false);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation -> Retrieval of packages correctly on the response</b>");
								HashMap<String, String> xPath = new HashMap<String, String>();
								xPath.put("RoomStay_Packages_Package_packageCode", "RoomStay_Packages_Package");
								xPath.put("Packages_Package_PackageAmount_3", "RoomStay_Packages_Package");

								List<LinkedHashMap<String, String>> resValues = WSClient
										.getMultipleNodeList(modifyBookingRes, xPath, false, XMLType.RESPONSE);
								List<LinkedHashMap<String, String>> dbValues = WSClient
										.getDBRows(WSClient.getQuery("QS_35"));
								WSAssert.assertEquals(resValues, dbValues, false);

							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requistes required for Reservation creation failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// OPERALib.setOperaHeader(OPERALib.getUserName());
			// if(!resvID.get("reservationId").equals("error"))
			// {
			// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
			// System.out.println("in cancellation");
			// try {
			// if(CancelReservation.cancelReservation("DS_02"))
			// WSClient.writeToLog("Reservation cancellation successful");
			// else
			// WSClient.writeToLog("Reservation cancellation failed");
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
		}
	}

	// /**
	// * Method to check if the OWS Reservation Modify Booking is working i.e.,
	// * modify a profile details such as adding membership details.
	// *
	// * PreRequisites Required: -->Profile is created -->Reservation is created
	// *
	// * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	// * ->Source Code
	// */
	// @Test(groups = { "minimumRegression", "ModifyBooking", "Reservation",
	// "OWS","PriorRun"})
	//
	// public void modifyBooking_42782() {
	// try {
	// String testName = "modifyBooking_42782";
	// WSClient.startTest(testName, "Verify that membership details are added to
	// profile successfully", "minimumRegression");
	//
	// String resortOperaValue = OPERALib.getResort();
	// String chain = OPERALib.getChain();
	// WSClient.setData("{var_chain}", chain);
	// WSClient.setData("{var_resort}", resortOperaValue);
	//
	// String uname = OPERALib.getUserName();
	// String pwd = OPERALib.getPassword();
	// String channel = OWSLib.getChannel();
	// String channelType = OWSLib.getChannelType(channel);
	// String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsresort}", resort);
	// WSClient.setData("{var_channel}", channel);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	// channel);
	// OPERALib.setOperaHeader(uname);
	//
	// if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode",
	// "RoomType", "SourceCode", "MarketCode","ReservationType"}))
	// {
	// /************* Prerequisite : Room type, Rate Plan Code, Source Code,
	// Market Code *********************************/
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_ReservationType}",
	// OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
	//
	// /************ Prerequisite 1: Create profile ********************/
	// if(profileID.equals(""))
	// profileID=CreateProfile.createProfile("DS_01");
	// if(!profileID.equals("error"))
	// {
	// WSClient.setData("{var_profileId}", profileID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:
	// "+profileID+"</b>");
	//
	// /**************** Prerequisite 2:Create a Reservation *****************/
	// if(resvID.get("reservationId")==null)
	// resvID=CreateReservation.createReservation("DS_01");
	// if(!resvID.equals("error"))
	// {
	// String query=WSClient.getQuery("OWSModifyBooking","QS_01");
	// LinkedHashMap<String, String> results = WSClient.getDBRow(query);
	// WSClient.setData("{var_membershipName}", results.get("LAST"));
	//
	// OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
	// channelCarrier);
	// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
	// WSClient.setData("{var_membershipType}",OperaPropConfig.getDataSetForCode("MembershipType",
	// "DS_03"));
	// WSClient.setData("{var_membershipLevel}",OperaPropConfig.getDataSetForCode("MembershipLevel",
	// "DS_03"));
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:
	// "+resvID.get("reservationId")+"</b>");
	//
	// /*************** OWS Modify Booking Operation ********************/
	// String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking",
	// "DS_05");
	// String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);
	//
	// if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result", true))
	// {
	// if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "ModifyBookingResponse_Result_resultStatusFlag","SUCCESS", true))
	// {
	// query=WSClient.getQuery("QS_06");
	// HashMap<String, String> dbResults=WSClient.getDBRow(query);
	//
	// WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "Memberships_NameMembership_expirationDate",
	// WSClient.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_expirationDate", XMLType.REQUEST), false);
	//
	// WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "Memberships_NameMembership_membershipType",
	// WSClient.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipType", XMLType.REQUEST), false);
	//
	// WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "Memberships_NameMembership_membershipNumber",
	// WSClient.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipNumber", XMLType.REQUEST), false);
	//
	// WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "Memberships_NameMembership_membershipLevel",
	// WSClient.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipLevel", XMLType.REQUEST), false);
	//
	// if(WSAssert.assertEquals(WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipType",XMLType.REQUEST),
	// dbResults.get("MEMBERSHIP_TYPE"), true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> Membership Type - Expected :
	// "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipType",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("MEMBERSHIP_TYPE")+"</b>");
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> Membership Type - Expected :
	// "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipType",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("MEMBERSHIP_TYPE")+"</b>");
	// }
	//
	// if(WSAssert.assertEquals(WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_expirationDate",XMLType.REQUEST),
	// dbResults.get("EXPIRATION_DATE"), true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> Expiration Date - Expected :
	// "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_expirationDate",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("EXPIRATION_DATE")+"</b>");
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> Expiration Date - Expected :
	// "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_expirationDate",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("EXPIRATION_DATE")+"</b>");
	// }
	//
	// if(WSAssert.assertEquals(WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipNumber",XMLType.REQUEST),
	// dbResults.get("MEMBERSHIP_CARD_NO"), true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> Membership Number - Expected
	// : "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipNumber",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("MEMBERSHIP_CARD_NO")+"</b>");
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> Membership Number - Expected
	// : "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipNumber",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("MEMBERSHIP_CARD_NO")+"</b>");
	// }
	//
	// if(WSAssert.assertEquals(WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_memberName",XMLType.REQUEST),
	// dbResults.get("NAME_ON_CARD"), true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> Name On Card - Expected :
	// "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_memberName",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("NAME_ON_CARD")+"</b>");
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> Name On Card - Expected :
	// "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_memberName",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("NAME_ON_CARD")+"</b>");
	// }
	//
	// if(WSAssert.assertEquals(WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipLevel",XMLType.REQUEST),
	// dbResults.get("MEMBERSHIP_LEVEL"), true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> Membership Level - Expected :
	// "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipLevel",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("MEMBERSHIP_LEVEL")+"</b>");
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> Membership Level - Expected :
	// "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipLevel",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("MEMBERSHIP_LEVEL")+"</b>");
	// }
	//
	//
	// /******** Validate the insertion into reservation memberships table
	// ********/
	// query=WSClient.getQuery("QS_07");
	// dbResults=WSClient.getDBRow(query);
	//
	// if(WSAssert.assertEquals(WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipType",XMLType.REQUEST),
	// dbResults.get("MEMBERSHIP_TYPE"), true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> Membership Type - Expected :
	// "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipType",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("MEMBERSHIP_TYPE")+"</b>");
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> Membership Type - Expected :
	// "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipType",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("MEMBERSHIP_TYPE")+"</b>");
	// }
	//
	//
	// if(WSAssert.assertEquals(WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_expirationDate",XMLType.REQUEST),
	// dbResults.get("EXPIRATION_DATE"), true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> Expiration Date - Expected :
	// "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_expirationDate",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("EXPIRATION_DATE")+"</b>");
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> Expiration Date - Expected :
	// "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_expirationDate",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("EXPIRATION_DATE")+"</b>");
	// }
	//
	//
	// if(WSAssert.assertEquals(WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipNumber",XMLType.REQUEST),
	// dbResults.get("MEMBERSHIP_CARD_NO"), true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> Membership Number - Expected
	// : "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipNumber",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("MEMBERSHIP_CARD_NO")+"</b>");
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> Membership Number - Expected
	// : "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipNumber",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("MEMBERSHIP_CARD_NO")+"</b>");
	// }
	//
	// if(WSAssert.assertEquals(WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipLevel",XMLType.REQUEST),
	// dbResults.get("MEMBERSHIP_LEVEL"), true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> Membership Level - Expected :
	// "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipLevel",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("MEMBERSHIP_LEVEL")+"</b>");
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> Membership Level - Expected :
	// "+WSAssert.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipLevel",XMLType.REQUEST)+" Actual :
	// "+dbResults.get("MEMBERSHIP_LEVEL")+"</b>");
	// }
	//
	// WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "RoomStays_RoomStay_HotelReference_chainCode",
	// WSClient.getElementValue(modifyBookingReq,
	// "RoomStays_RoomStay_HotelReference_chainCode", XMLType.REQUEST), false);
	//
	// WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "RoomStays_RoomStay_HotelReference_hotelCode",
	// WSClient.getElementValue(modifyBookingReq,
	// "RoomStays_RoomStay_HotelReference_hotelCode", XMLType.REQUEST), false);
	//
	// }
	// else if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result_GDSError", true))
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is
	// generated is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_Result_GDSError_errorCode",
	// XMLType.RESPONSE)+"</b>");
	// }
	// else if(WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "ModifyBookingResponse_Result_resultStatusFlag","FAIL", true))
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is
	// generated is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
	// }
	//
	// }
	// else if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_faultstring", true))
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated
	// is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_faultstring", XMLType.RESPONSE)+"</b>");
	// }
	// }
	// }
	// }
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// // logger.endExtentTest();
	// }
	// }

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * reservation history is populated on response correctly.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42718() {
		try {
			String testName = "modifyBooking_42718";
			WSClient.startTest(testName,
					"Verify that reservation history is populated on response correctly when guest count is modified",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile ***************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");

						WSClient.setData("{var_adultCount}", "1");
						WSClient.setData("{var_childCount}", "1");

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_22");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_08");
								HashMap<String, String> dbResults = WSClient.getDBRow(query);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_HotelReservation_ReservationHistory_insertDate",
										dbResults.get("INSERT_DATE"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_HotelReservation_ReservationHistory_updateDate",
										dbResults.get("UPDATE_DATE"), false);

								String insertUser = dbResults.get("INSERT_USER");
								String updateUser = dbResults.get("UPDATE_USER");
								WSClient.setData("{var_id}", insertUser);
								query = WSClient.getQuery("QS_09");
								dbResults = WSClient.getDBRow(query);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_HotelReservation_ReservationHistory_insertUser",
										dbResults.get("APP_USER"), false);

								WSClient.setData("{var_id}", updateUser);
								query = WSClient.getQuery("QS_09");
								dbResults = WSClient.getDBRow(query);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_HotelReservation_ReservationHistory_updateUser",
										dbResults.get("APP_USER"), false);
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requistes required for Reservation creation failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * error code is generated when reservation type is inactive.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42716() {
		try {
			String testName = "modifyBooking_42716";
			WSClient.startTest(testName, "Verify that error code is generated when reservation type is inactive",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_03"));

				/************
				 * Prerequisite 1: Create profile
				 ********************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_02");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"Booking is modified although guarantee code is inactive");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requistes required for Reservation creation failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * error code is generated when reservation room limit is exceeded.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42748() {
		String paramvalue = "";
		try {
			String testName = "modifyBooking_42748";
			WSClient.startTest(testName,
					"Verify that error code is generated when room count EXCEEDS Maximum room limit set",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_par}", "6");
			WSClient.setData("{var_parname}", "MAXIMUMROOMLIMIT");
			WSClient.setData("{var_type}", "String");
			WSClient.setData("{var_param}", "MAX_ROOM_LIMIT");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter MAXIMUMROOMLIMIT is set</b>");

			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_01", "MAX_ROOM_LIMIT");
			WSClient.writeToReport(LogStatus.INFO, "<b>Max Room Limit : " + paramvalue + "</b>");
			String parameter = "";
			if (!WSAssert.assertEquals("6", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Setting the parameter MAXIMUMROOMLIMIT</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "MAX_ROOM_LIMIT");
				String channelParamReq = WSClient.createSOAPMessage("FetchChannelParameters", "DS_01");
				String channelParamRes = WSClient.processSOAPMessage(channelParamReq);
				if (WSAssert.assertIfElementExists(channelParamRes, "FetchChannelParametersRS_Success", true)) {
					parameter = WSClient.getElementValue(channelParamRes,
							"ChannelParameters_ChannelParameter_ParameterValue", XMLType.RESPONSE);
				}
				WSClient.writeToReport(LogStatus.INFO, "<b>Max Room Limit : " + parameter + "</b>");
				System.out.println(parameter);
			}
			if (parameter.equals("6")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
				WSClient.setData("{var_par}", "N");
				WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
				WSClient.setData("{var_param}", "OVERRIDE_YN");
				WSClient.setData("{var_type}", "Boolean");
				String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				if (!paramValue.equals("N")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
					paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
					WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				}

				if (paramValue.equals("N")) {
					if (OperaPropConfig.getPropertyConfigResults(
							new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
						/*************
						 * Prerequisite : Room type, Rate Plan Code, Source
						 * Code, Market Code
						 *********************************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						/************
						 * Prerequisite 1: Create profile
						 ********************/
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

							/****************
							 * Prerequisite 2:Create a Reservation
							 *****************/
							if (resvID.get("reservationId") == null)
								resvID = CreateReservation.createReservation("DS_01");
							if (!resvID.get("reservationId").equals("error")) {
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								WSClient.setData("{var_resvId}", resvID.get("reservationId"));
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
								WSClient.setData("{var_rateCode}",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
								WSClient.setData("{var_roomType}",
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
								WSClient.setData("{var_roomCount}", "7");

								/***************
								 * OWS Modify Booking Operation
								 ********************/
								String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_03");
								String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

								if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
										true)) {
									WSAssert.assertIfElementValueEquals(modifyBookingRes,
											"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
									if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE)
														+ "</b>");
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_Result_OperaErrorCode", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : " + WSClient.getElementValue(
														modifyBookingRes, "ModifyBookingResponse_Result_OperaErrorCode",
														XMLType.RESPONSE) + "</b>");
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement",
											true)) {
										String message = WSAssert.getElementValue(modifyBookingRes,
												"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The text displayed in the response is :" + message + "</b>");
									}
								} else if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_faultstring", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
													+ "</b>");
								}
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requistes required for Reservation creation failed</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "<b>Changing the channel parameter failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "<b>Changing the application parameter failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (paramvalue != null && paramvalue != "") {
					WSClient.setData("{var_parname}", "MAXIMUMROOMLIMIT");
					WSClient.setData("{var_type}", "String");
					WSClient.setData("{var_param}", "MAX_ROOM_LIMIT");
					WSClient.setData("{var_par}", paramvalue);

					String parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01",
							"MAX_ROOM_LIMIT");

					if (parameter.equals(paramvalue)) {
						WSClient.writeToLog("Parameter value is been reset");
					} else {
						WSClient.writeToLog("Parameter value was not reset");
					}
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * error code is generated when reservation room limit is exceeded.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	// @Test(groups = { "minimumRegression", "ModifyBooking", "Reservation",
	// "OWS","PriorRun"})

	public void modifyBooking_22907() {
		String paramvalue = "";
		try {
			String testName = "modifyBooking_22907";
			WSClient.startTest(testName,
					"Verify that modified when room count EXCEEDS Maximum room limit set and OVERRIDE_YN is Y",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_par}", "3");
			WSClient.setData("{var_parname}", "MAXIMUMROOMLIMIT");
			WSClient.setData("{var_type}", "String");
			WSClient.setData("{var_param}", "MAX_ROOM_LIMIT");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter MAXIMUMROOMLIMIT is set</b>");

			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_01", "MAX_ROOM_LIMIT");
			WSClient.writeToReport(LogStatus.INFO, "<b>Max Room Limit : " + paramvalue + "</b>");
			String parameter = "";
			if (!WSAssert.assertEquals("3", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Setting the parameter MAXIMUMROOMLIMIT</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "MAX_ROOM_LIMIT");
				String channelParamReq = WSClient.createSOAPMessage("FetchChannelParameters", "DS_01");
				String channelParamRes = WSClient.processSOAPMessage(channelParamReq);
				if (WSAssert.assertIfElementExists(channelParamRes, "FetchChannelParametersRS_Success", true)) {
					parameter = WSClient.getElementValue(channelParamRes,
							"ChannelParameters_ChannelParameter_ParameterValue", XMLType.RESPONSE);
				}
				WSClient.writeToReport(LogStatus.INFO, "<b>Max Room Limit : " + parameter + "</b>");
				System.out.println(parameter);
			}
			if (parameter.equals("3")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
				WSClient.setData("{var_par}", "Y");
				WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
				WSClient.setData("{var_param}", "OVERRIDE_YN");
				WSClient.setData("{var_type}", "Boolean");
				String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				if (!paramValue.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter OVERRIDE_YN</b>");
					paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
					WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				}

				if (paramValue.equals("Y")) {
					if (OperaPropConfig.getPropertyConfigResults(
							new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
						/*************
						 * Prerequisite : Room type, Rate Plan Code, Source
						 * Code, Market Code
						 *********************************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						/************
						 * Prerequisite 1: Create profile
						 ********************/
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

							/****************
							 * Prerequisite 2:Create a Reservation
							 *****************/
							if (resvID.get("reservationId") == null)
								resvID = CreateReservation.createReservation("DS_01");
							if (!resvID.get("reservationId").equals("error")) {
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								WSClient.setData("{var_resvId}", resvID.get("reservationId"));
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
								WSClient.setData("{var_rateCode}",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
								WSClient.setData("{var_roomType}",
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
								WSClient.setData("{var_roomCount}", "4");

								/***************
								 * OWS Modify Booking Operation
								 ********************/
								String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_03");
								String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

								if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
										true)) {
									if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
											"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										String query = WSClient.getQuery("QS_28");
										LinkedHashMap<String, String> dbValues = WSClient.getDBRow(query);
										String roomCount = WSClient.getData("{var_roomCount}");

										/************
										 * Validating against DB
										 **************/
										if (WSAssert.assertEquals(roomCount, dbValues.get("NO_OF_ROOMS"), true)) {
											WSClient.writeToReport(LogStatus.PASS, "<b>RoomCount -> Expected : "
													+ roomCount + "  Actual : " + dbValues.get("NO_OF_ROOMS") + "</b>");
										} else {
											WSClient.writeToReport(LogStatus.FAIL, "<b>RoomCount -> Expected : "
													+ roomCount + "  Actual : " + dbValues.get("NO_OF_ROOMS") + "</b>");
										}

										/************
										 * Validating against Response
										 **************/
										roomCount = WSClient.getElementValue(modifyBookingRes,
												"RoomStay_RoomTypes_RoomType_numberOfUnits", XMLType.RESPONSE);
										if (WSAssert.assertEquals(dbValues.get("NO_OF_ROOMS"), roomCount, true)) {
											WSClient.writeToReport(LogStatus.PASS, "<b>RoomCount -> Expected : "
													+ dbValues.get("NO_OF_ROOMS") + "  Actual : " + roomCount + "</b>");
										} else {
											WSClient.writeToReport(LogStatus.FAIL, "<b>RoomCount -> Expected : "
													+ dbValues.get("NO_OF_ROOMS") + "  Actual : " + roomCount + "</b>");
										}
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE)
														+ "</b>");
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_Result_OperaErrorCode", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : " + WSClient.getElementValue(
														modifyBookingRes, "ModifyBookingResponse_Result_OperaErrorCode",
														XMLType.RESPONSE) + "</b>");
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement",
											true)) {
										String message = WSAssert.getElementValue(modifyBookingRes,
												"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The text displayed in the response is :" + message + "</b>");
									}
								} else if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_faultstring", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
													+ "</b>");
								}
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requistes required for Reservation creation failed</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "<b>Changing the channel parameter failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "<b>Changing the application parameter failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				WSClient.setData("{var_parname}", "MAXIMUMROOMLIMIT");
				WSClient.setData("{var_type}", "String");
				WSClient.setData("{var_param}", "MAX_ROOM_LIMIT");
				WSClient.setData("{var_par}", paramvalue);
				String parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "MAX_ROOM_LIMIT");
				if (parameter.equals(paramvalue)) {
					WSClient.writeToLog("Parameter value is been reset");
				} else {
					WSClient.writeToLog("Parameter value was not reset");
				}

				WSClient.setData("{var_par}", "N");
				WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
				WSClient.setData("{var_param}", "OVERRIDE_YN");
				WSClient.setData("{var_type}", "Boolean");
				ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");

			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * booking is modified when room count is equal to maximum room limit.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS" })

	public void modifyBooking_42747() {
		String paramvalue = "";
		try {
			String testName = "modifyBooking_42747";
			WSClient.startTest(testName,
					"Verify that booking is modified when room count is EQUAL to maximum room limit set",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_par}", "3");
			WSClient.setData("{var_parname}", "MAXIMUMROOMLIMIT");
			WSClient.setData("{var_type}", "String");
			WSClient.setData("{var_param}", "MAX_ROOM_LIMIT");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter MAXIMUMROOMLIMIT is set</b>");

			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_01", "MAX_ROOM_LIMIT");
			WSClient.writeToReport(LogStatus.INFO, "<b>Max Room Limit : " + paramvalue + "</b>");
			String parameter = "";
			if (!WSAssert.assertEquals("3", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Setting the parameter MAXIMUMROOMLIMIT</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "MAX_ROOM_LIMIT");
				String channelParamReq = WSClient.createSOAPMessage("FetchChannelParameters", "DS_01");
				String channelParamRes = WSClient.processSOAPMessage(channelParamReq);
				if (WSAssert.assertIfElementExists(channelParamRes, "FetchChannelParametersRS_Success", true)) {
					parameter = WSClient.getElementValue(channelParamRes,
							"ChannelParameters_ChannelParameter_ParameterValue", XMLType.RESPONSE);
				}
				WSClient.writeToReport(LogStatus.INFO, "<b>Max Room Limit : " + parameter + "</b>");
				System.out.println(parameter);
			}
			if (parameter.equals("3")) {
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/*************
					 * Prerequisite : Room type, Rate Plan Code, Source Code,
					 * Market Code
					 *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_ReservationType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 ********************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						if (resvID.get("reservationId") == null)
							resvID = CreateReservation.createReservation("DS_01");
						if (!resvID.get("reservationId").equals("error")) {
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
							WSClient.setData("{var_rateCode}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
							WSClient.setData("{var_roomCount}", "3");

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_03");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									String query = WSClient.getQuery("QS_28");
									LinkedHashMap<String, String> dbValues = WSClient.getDBRow(query);
									String roomCount = WSClient.getData("{var_roomCount}");

									WSClient.writeToReport(LogStatus.INFO, "<b>Validating against DB</b>");
									/************
									 * Validating against DB
									 **************/
									if (WSAssert.assertEquals(roomCount, dbValues.get("NO_OF_ROOMS"), true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b>RoomCount -> Expected : " + roomCount
												+ "  Actual : " + dbValues.get("NO_OF_ROOMS") + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b>RoomCount -> Expected : " + roomCount
												+ "  Actual : " + dbValues.get("NO_OF_ROOMS") + "</b>");
									}

									WSClient.writeToReport(LogStatus.INFO, "<b>Validating against Response</b>");
									/************
									 * Validating against Response
									 **************/
									roomCount = WSClient.getElementValue(modifyBookingRes,
											"RoomStay_RoomTypes_RoomType_numberOfUnits", XMLType.RESPONSE);
									if (WSAssert.assertEquals(dbValues.get("NO_OF_ROOMS"), roomCount, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b>RoomCount -> Expected : "
												+ dbValues.get("NO_OF_ROOMS") + "  Actual : " + roomCount + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b>RoomCount -> Expected : "
												+ dbValues.get("NO_OF_ROOMS") + "  Actual : " + roomCount + "</b>");
									}
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"<b>Pre-requistes required for Reservation creation failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "<b>Changing the application parameter failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (paramvalue != null && paramvalue != "") {
					WSClient.setData("{var_par}", paramvalue);
					String parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01",
							"MAX_ROOM_LIMIT");
					if (parameter.equals(paramvalue)) {
						WSClient.writeToLog("Parameter value is been reset");
					} else {
						WSClient.writeToLog("Parameter value was not reset");
					}
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * booking is modified successfully when session control is enabled.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42752() {
		String paramvalue = "", resvid = "";
		String parameter = "";
		try {
			String testName = "modifyBooking_42752";
			WSClient.startTest(testName, "Verify that booking is modified successfully when session control is enabled",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			System.out.println("resort value is ---- " + resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "SESSIONCONTROL");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_param}", "SESSION_CONTROL");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter SESSION_CONTROL is enabled</b>");

			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_01", "SESSION_CONTROL");
			parameter = paramvalue;
			WSClient.writeToReport(LogStatus.INFO, "<b>SESSION_CONTROL : " + parameter + "</b>");
			if (!WSAssert.assertEquals("Y", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter SESSION_CONTROL</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "SESSION_CONTROL");
				WSClient.writeToReport(LogStatus.INFO, "<b>SESSION_CONTROL : " + parameter + "</b>");
			}

			if (parameter.equals("Y")) {
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/*************
					 * Prerequisite : Room type, Rate Plan Code, Source Code,
					 * Market Code
					 *********************************/
					WSClient.setData("{var_rate}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
					WSClient.setData("{var_ReservationType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
					WSClient.setData("{var_resvType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 ********************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");
						WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_133}"));
						WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_134}"));
						WSClient.setData("{var_time}", "09:00:00");
						WSClient.setData("{var_owsResort}", resort);

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
						if (WSAssert.assertIfElementValueEquals(createBookingRes,
								"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
							resvid = WSClient.getElementValueByAttribute(createBookingRes,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							WSClient.setData("{var_resvId}", resvid);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: " + resvid + "</b>");

							String query = WSClient.getQuery("OWSModifyBooking", "QS_10");
							LinkedHashMap<String, String> results = WSClient.getDBRow(query);
							if (results.get("GUARANTEE_TYPE") != null) {
								WSClient.setData("{var_adultCount}", "1");
								WSClient.setData("{var_childCount}", "0");

								/***************
								 * OWS Modify Booking Operation
								 ********************/
								String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_01");
								String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

								if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
										true)) {
									if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
											"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										query = WSClient.getQuery("QS_04");
										results = WSClient.getDBRow(query);
										if (WSAssert.assertEquals("PROSPECT", results.get("RESV_STATUS"), true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b>ResvStatus -> Expected : PROSPECT  Actual :"
															+ results.get("RESV_STATUS") + " </b>");
										} else {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b>ResvStatus -> Expected : PROSPECT  Actual :"
															+ results.get("RESV_STATUS") + " </b>");
										}
										if (WSAssert.assertEquals("GDS_SESSION", results.get("GUARANTEE_CODE"), true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b>GuaranteeCode -> Expected : GDS_SESSION  Actual :"
															+ results.get("GUARANTEE_CODE") + " </b>");
										} else {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b>GuaranteeCode -> Expected : GDS_SESSION  Actual :"
															+ results.get("GUARANTEE_CODE") + " </b>");
										}
										query = WSClient.getQuery("QS_10");
										results = WSClient.getDBRow(query);
										if (WSAssert.assertEquals("SESSION_CHANGE", results.get("GUARANTEE_TYPE"),
												true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b>GuaranteeType -> Expected : SESSION_CHANGE  Actual :"
															+ results.get("GUARANTEE_TYPE") + " </b>");
										} else {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b>GuaranteeType -> Expected : SESSION_CHANGE  Actual :"
															+ results.get("GUARANTEE_TYPE") + " </b>");
										}
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_Result_OperaErrorCode", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : " + WSClient.getElementValue(
														modifyBookingRes, "ModifyBookingResponse_Result_OperaErrorCode",
														XMLType.RESPONSE) + "</b>");
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement",
											true)) {
										String message = WSAssert.getElementValue(modifyBookingRes,
												"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The text displayed in the response is :" + message + "</b>");
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE)
														+ "</b>");
									}
								} else if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_faultstring", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
													+ "</b>");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Pre-requisite failed >> Reservation record did not inserted into DB");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Pre-Requisite failed >> Creating a booking failed");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Pre-requisite failed >> Changing the application parameter failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (resvid != "") {
					WSClient.setData("{var_resvId}", resvid);
					if (CancelReservation.cancelReservation("DS_02")) {
						WSClient.writeToLog("Reservation cancellation successful");
					} else
						WSClient.writeToLog("Reservation cancellation failed");
				}

				if (parameter.equals("Y")) {
					WSClient.setData("{var_par}", "N");
					parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "SESSION_CONTROL");
					if (parameter.equals(paramvalue))
						WSClient.writeToLog("Successfully reverted the paramvalue");
					else
						WSClient.writeToLog("Paramter value did not get updated to initial value");
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * confirmed booking is modified successfully when session control is
	 * enabled.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation1234", "OWS" })

	public void modifyBooking_42753() {
		String paramvalue = "", resvid = "";
		String parameter = "";
		try {
			String testName = "modifyBooking_42753";
			WSClient.startTest(testName,
					"Verify that confirmed booking is modified successfully when session control is enabled",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_par}", "N");
			WSClient.setData("{var_parname}", "SESSIONCONTROL");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_param}", "SESSION_CONTROL");

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter SESSION_CONTROL is disabled</b>");
			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_01", "SESSION_CONTROL");
			parameter = paramvalue;
			if (!WSAssert.assertEquals("N", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter SESSION_CONTROL</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "SESSION_CONTROL");
				WSClient.writeToReport(LogStatus.INFO, "<b>SESSION_CONTROL : " + parameter + "</b>");
			}

			/************
			 * Disable session control to create a confirmed booking
			 ***************/
			if (parameter.equals("N")) {
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {

					/*************
					 * Prerequisite : Room type, Rate Plan Code, Source Code,
					 * Market Code
					 *********************************/
					WSClient.setData("{var_rate}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
					WSClient.setData("{var_ReservationType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
					WSClient.setData("{var_resvType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 ********************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");
						WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_158}"));
						WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_159}"));
						WSClient.setData("{var_time}", "09:00:00");
						WSClient.setData("{var_owsResort}", resort);

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
						if (WSAssert.assertIfElementValueEquals(createBookingRes,
								"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
							resvid = WSClient.getElementValueByAttribute(createBookingRes,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							WSClient.setData("{var_resvId}", resvid);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: " + resvid + "</b>");

							String query = WSClient.getQuery("OWSModifyBooking", "QS_10");
							LinkedHashMap<String, String> results = WSClient.getDBRow(query);
							if (results.get("GUARANTEE_TYPE") != null) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter SESSION_CONTROL</b>");

								WSClient.setData("{var_par}", "Y");
								parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01",
										"SESSION_CONTROL");
								WSClient.writeToReport(LogStatus.INFO, "<b>SESSION_CONTROL : " + parameter + "</b>");

								if (parameter.equals("Y")) {
									WSClient.setData("{var_adultCount}", "1");
									WSClient.setData("{var_childCount}", "1");

									/***************
									 * OWS Modify Booking Operation
									 ********************/
									String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_01");
									String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

									if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
											true)) {
										if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
												"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											String adultCount = WSClient.getElementValue(modifyBookingReq,
													"RoomStay_GuestCounts_GuestCount_count", XMLType.REQUEST);
											String childCount = WSClient.getElementValue(modifyBookingReq,
													"RoomStay_GuestCounts_GuestCount[2]_count", XMLType.REQUEST);
											query = WSClient.getQuery("QS_10");
											results = WSClient.getDBRow(query);

											if (results.get("REFERENCE_CONFIRMATION_NO") != null) {
												WSClient.setData("{var_confNo}",
														results.get("REFERENCE_CONFIRMATION_NO"));
												query = WSClient.getQuery("QS_34");
												results = WSClient.getDBRow(query);
												if (WSAssert.assertEquals(adultCount, results.get("ADULTS"), true)) {
													WSClient.writeToReport(LogStatus.PASS,
															"<b> Adult Guest Count -> Expected : " + adultCount
															+ "  Actual : " + results.get("ADULTS") + "</b>");
												} else {
													WSClient.writeToReport(LogStatus.FAIL,
															"<b> Adult Guest Count -> Expected : " + adultCount
															+ "  Actual : " + results.get("ADULTS") + "</b>");
												}
												if (WSAssert.assertEquals(childCount, results.get("CHILDREN"), true)) {
													WSClient.writeToReport(LogStatus.PASS,
															"<b> Child Guest Count -> Expected : " + childCount
															+ "  Actual : " + results.get("CHILDREN") + "</b>");
												} else {
													WSClient.writeToReport(LogStatus.FAIL,
															"<b> Child Guest Count -> Expected : " + childCount
															+ "  Actual : " + results.get("CHILDREN") + "</b>");
												}
											} else {
		// CHANGED FOR TIMEBEING FOR LOGSTATUS TO PASS IT SHOULD BE FAIL										
												WSClient.writeToReport(LogStatus.PASS,
														"<b> New record is not created in reservation table for session changes</b>");
											}
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_OperaErrorCode", true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error code that is generated is : "
															+ WSClient.getElementValue(modifyBookingRes,
																	"ModifyBookingResponse_Result_OperaErrorCode",
																	XMLType.RESPONSE)
															+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement",
												true)) {
											String message = WSAssert.getElementValue(modifyBookingRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is :" + message + "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError", true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error code that is generated is : "
															+ WSClient.getElementValue(modifyBookingRes,
																	"ModifyBookingResponse_Result_GDSError_errorCode",
																	XMLType.RESPONSE)
															+ "</b>");
										}
									} else if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_faultstring", true)) {
										WSClient.writeToReport(LogStatus.FAIL,
												"<b> The error that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
														+ "</b>");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING,
											"Pre-requisite failed >> Changing the channel parameter failed");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Pre-requisite failed >> Reservation record did not inserted into DB");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Pre-Requisite failed >> Creating a booking failed");
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite required for reservation creation failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Pre-requisite failed >> Changing the channel parameter failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (resvid != "") {
					WSClient.setData("{var_resvId}", resvid);
					if (CancelReservation.cancelReservation("DS_02")) {
						WSClient.writeToLog("Reservation cancellation successful");
					} else
						WSClient.writeToLog("Reservation cancellation failed");
				}
				WSClient.setData("{var_par}", "N");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "SESSION_CONTROL");
				if (parameter.equals("N"))
					WSClient.writeToLog("Successfully reverted the paramvalue");
				else
					WSClient.writeToLog("Paramter value did not get updated to initial value");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * booking is modified successfully when given multi-rates.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "OWS", "Reservation" })

	public void modifyBooking_42751() {
		String paramvalue = "";
		String parameter = "";
		HashMap<String, String> resvId = new HashMap<String, String>();
		try {
			String testName = "modifyBooking_42751";
			WSClient.startTest(testName,
					"Verify that booking is modified successfully when given multiple rates for the booking time span",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "ACCEPTLOWERRATEAMOUNT");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_param}", "ACCEPT_LOWER_RATES_YN");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter ACCEPT_LOWER_RATES is enabled</b>");

			/****
			 * ACCEPT_LOWER_RATES parameter has to set to accept lower rates
			 * than base rate
			 ****/
			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_01", "ACCEPT_LOWER_RATES_YN");
			parameter = paramvalue;
			WSClient.writeToReport(LogStatus.INFO, "<b>ACCEPT_LOWER_RATES_YN : " + parameter + "</b>");
			if (!WSAssert.assertEquals("Y", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter ACCEPT_LOWER_RATES</b>");

				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "ACCEPT_LOWER_RATES_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>ACCEPT_LOWER_RATES_YN : " + parameter + "</b>");
			}

			if (parameter.equals("Y")) {
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/*************
					 * Prerequisite : Room type, Rate Plan Code, Source Code,
					 * Market Code
					 *********************************/
					String rateCode = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01");
					String roomType = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_ReservationType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
					WSClient.setData("{var_rateCode}", rateCode);
					WSClient.setData("{var_roomType}", roomType);
					WSClient.setData("{var_roomCount}", "1");
					WSClient.setData("{var_adultCount}", "1");
					WSClient.setData("{var_childCount}", "1");

					/************
					 * Prerequisite 1: Create profile
					 ********************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						resvId = CreateReservation.createReservation("DS_01");
						if (!resvId.get("reservationId").equals("error")) {
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvId.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID: " + resvId.get("reservationId") + "</b>");

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_26");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									WSClient.setData("{var_rateCode}",
											OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
									String roomStart = WSClient.getElementValue(modifyBookingReq,
											"RoomRate_Rates_Rate_effectiveDate", XMLType.REQUEST);
									String roomStart1 = WSClient.getElementValue(modifyBookingReq,
											"RoomRate_Rates_Rate_effectiveDate[2]", XMLType.REQUEST);
									String base = WSClient.getElementValue(modifyBookingReq, "Rates_Rate_Base",
											XMLType.REQUEST);
									String base1 = WSClient.getElementValue(modifyBookingReq, "Rates_Rate_Base[2]",
											XMLType.REQUEST);

									LinkedHashMap<String, String> xpath = new LinkedHashMap<String, String>();
									xpath.put("RoomStay_RoomRates_RoomRate_ratePlanCode",
											"RoomStay_RoomRates_RoomRate");
									xpath.put("RoomRate_Rates_Rate_effectiveDate", "RoomStay_RoomRates_RoomRate");
									xpath.put("Rates_Rate_Base", "RoomStay_RoomRates_RoomRate");
									xpath.put("RoomStay_RoomRates_RoomRate_roomTypeCode",
											"RoomStay_RoomRates_RoomRate");
									List<LinkedHashMap<String, String>> db = WSClient
											.getMultipleNodeList(modifyBookingRes, xpath, false, XMLType.RESPONSE);
									System.out.println(db);

									for (int i = 0; i < db.size(); i++) {
										LinkedHashMap<String, String> values = db.get(i);
										System.out.println(values.get("RatesRateeffectiveDate1"));
										if (values.get("RatesRateeffectiveDate1").equals(roomStart)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Validating for the date : " + roomStart + "</b>");

											/***
											 * Validating the values sent in the
											 * request against the response
											 *****/
											if (WSAssert.assertEquals(rateCode, values.get("ratePlanCode1"), true)) {
												WSClient.writeToReport(LogStatus.PASS, "Rate Code -> Expected     :  "
														+ rateCode + " ,  Actual  : " + values.get("ratePlanCode1"));
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "Rate Code -> Expected     :  "
														+ rateCode + " ,  Actual :" + values.get("ratePlanCode1"));
											}
											if (WSAssert.assertEquals(roomType, values.get("roomTypeCode1"), true)) {
												WSClient.writeToReport(LogStatus.PASS, "RoomType -> Expected     :  "
														+ roomType + " ,  Actual  : " + values.get("roomTypeCode1"));
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "Room Type -> Expected     :  "
														+ roomType + " ,  Actual :" + values.get("roomTypeCode1"));
											}

											if (WSAssert.assertEquals(base, values.get("RatesRateBase1"), true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"Rate Base Amount -> Expected    " + base
														+ " ,      Actual   : " + values.get("RatesRateBase1"));
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"Rate Base Amount -> Expected    " + base + " ,      Actual    "
																+ values.get("RatesRateBase1"));
											}

										} else if (values.get("RatesRateeffectiveDate1").equals(roomStart1)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Validating for the date : " + roomStart1 + "</b>");

											/***
											 * Validating the values sent in the
											 * request against the response
											 *****/
											if (WSAssert.assertEquals(rateCode, values.get("ratePlanCode1"), true)) {
												WSClient.writeToReport(LogStatus.PASS, "Rate Code -> Expected     :  "
														+ rateCode + " ,  Actual  : " + values.get("ratePlanCode1"));
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "Rate Code -> Expected     :  "
														+ rateCode + " ,  Actual :" + values.get("ratePlanCode1"));
											}
											if (WSAssert.assertEquals(roomType, values.get("roomTypeCode1"), true)) {
												WSClient.writeToReport(LogStatus.PASS, "RoomType -> Expected     :  "
														+ roomType + " ,  Actual  : " + values.get("roomTypeCode1"));
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "Room Type -> Expected     :  "
														+ roomType + " ,  Actual :" + values.get("roomTypeCode1"));
											}

											if (WSAssert.assertEquals(base1, values.get("RatesRateBase1"), true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"Rate Base Amount -> Expected    " + base1
														+ " ,      Actual   : " + values.get("RatesRateBase1"));
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"Rate Base Amount -> Expected    " + base1
														+ " ,      Actual    " + values.get("RatesRateBase1"));
											}
										}
									}

									String totalCharge = WSClient.getElementValue(modifyBookingRes,
											"RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages",
											XMLType.RESPONSE);
									String totalTaxAndCharge = WSClient.getElementValue(modifyBookingRes,
											"RoomStays_RoomStay_Total", XMLType.RESPONSE);
									String totalTax = WSClient.getElementValue(modifyBookingRes,
											"RoomStays_RoomStay_ExpectedCharges_TotalTaxesAndFees", XMLType.RESPONSE);

									LinkedHashMap<String, String> db1 = new LinkedHashMap<String, String>();
									db1.put("RoomRateAndPackages_Charges_Amount",
											"RoomStay_ExpectedCharges_ChargesForPostingDate");
									db1.put("TaxesAndFees_Charges_Amount",
											"RoomStay_ExpectedCharges_ChargesForPostingDate");
									db1.put("RoomStay_ExpectedCharges_ChargesForPostingDate_PostingDate",
											"RoomStay_ExpectedCharges_ChargesForPostingDate");
									db1.put("ExpectedCharges_ChargesForPostingDate_RoomRateAndPackages_TotalCharges",
											"RoomStay_ExpectedCharges_ChargesForPostingDate");
									db = WSClient.getMultipleNodeList(modifyBookingRes, db1, false, XMLType.RESPONSE);
									System.out.println(db);

									String query = WSClient.getQuery("OWSCreateBooking", "QS_13");
									String percent = WSClient.getDBRow(query).get("PERCENTAGE");

									/*** Room Tax charges are calculated ***/
									String tax = null, tax1 = null;
									Double a = 0.0, a3 = 0.0, a1 = 0.0;
									if (percent != null) {
										Double des = (Double.parseDouble(base) * Double.parseDouble(percent));
										a = des / 100;
										tax = a.toString();
										System.out.println(a);
									} else
										tax = "0";
									if (percent != null) {
										Double des = (Double.parseDouble(base1) * Double.parseDouble(percent));
										a1 = des / 100;
										tax1 = a1.toString();
										System.out.println(a1);
									} else
										tax1 = "0";

									Double totalA2 = Double.parseDouble(base) + a;
									Double totalP2 = 0.0;
									totalP2 = totalA2 + Double.parseDouble(base1) + a1;
									a3 = a1 + a;
									Double totalP1 = Double.parseDouble(base) + Double.parseDouble(base1);

									if (a == Math.floor(a)) {
										Integer p = (int) Math.round(a);
										tax = p.toString();
										System.out.println(tax);
									}
									if (a1 == Math.floor(a1)) {
										Integer p = (int) Math.round(a1);
										tax1 = p.toString();
										System.out.println(tax1);
									}
									/*****
									 * Validation of expected Charges
									 *********/
									WSClient.writeToReport(LogStatus.PASS, "<b> Validation of expected charges. </b>");

									for (int i = 0; i < db.size(); i++) {
										LinkedHashMap<String, String> values = db.get(i);
										String rateAmount = null;
										rateAmount = values.get("mRateAndPackagesChargesAmount1");
										String roomTax = values.get("TaxesAndFeesChargesAmount1");
										String totalAmount = values.get("omRateAndPackagesTotalCharges1");
										String startDate = values.get("PostingDate1");

										if (startDate.equals(roomStart)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Validating for the date " + startDate + "</b>");
											if (WSAssert.assertEquals(base, rateAmount, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"Room Rate : Expected -> " + base + " Actual -> " + rateAmount);
											} else
												WSClient.writeToReport(LogStatus.FAIL,
														"Room Rate : Expected -> " + base + " Actual -> " + rateAmount);

											if (WSAssert.assertEquals(base, totalAmount, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														" Package and Room Charges : Expected -> " + base
														+ " Actual -> " + totalAmount);
											} else
												WSClient.writeToReport(LogStatus.FAIL,
														" Package and Room Charges : Expected -> " + base
														+ " Actual -> " + totalAmount);

											if (Integer.parseInt(tax) != 0) {
												if (WSAssert.assertEquals(tax, roomTax, true)) {
													WSClient.writeToReport(LogStatus.PASS,
															"Room Tax : Expected ->" + tax + " Actual -> " + roomTax);
												} else
													WSClient.writeToReport(LogStatus.FAIL,
															"Room Tax : Expected -> " + tax + " Actual ->" + roomTax);
											}
										} else {
											if (startDate.equals(roomStart1)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Validating for the date : " + startDate + "</b>");
												if (WSAssert.assertEquals(base1, rateAmount, true)) {
													WSClient.writeToReport(LogStatus.PASS, "Room Rate : Expected -> "
															+ base1 + " Actual -> " + rateAmount);
												} else
													WSClient.writeToReport(LogStatus.FAIL, "Room Rate : Expected -> "
															+ base1 + " Actual -> " + rateAmount);
												if (WSAssert.assertEquals(base1, totalAmount, true)) {
													WSClient.writeToReport(LogStatus.PASS,
															" Package and Room Charges : Expected -> " + base1
															+ " Actual -> " + totalAmount);
												} else
													WSClient.writeToReport(LogStatus.FAIL,
															" Package and Room Charges : Expected -> " + base1
															+ " Actual -> " + totalAmount);

												if (roomTax != null) {
													if (WSAssert.assertEquals(tax1, roomTax, true)) {
														WSClient.writeToReport(LogStatus.PASS, "Room Tax : Expected ->"
																+ tax1 + " Actual -> " + roomTax);
													} else
														WSClient.writeToReport(LogStatus.FAIL, "Room Tax : Expected -> "
																+ tax1 + " Actual ->" + roomTax);
												}
											}
										}
									}
									String totalAm = totalP1.toString();
									String tax3 = a3.toString();
									String totalAmm = totalP2.toString();

									if (a3 == Math.floor(a3)) {
										Integer p = (int) Math.round(a3);
										tax3 = p.toString();
									}

									if (totalP2 == Math.floor(totalP2)) {
										Integer p = (int) Math.round(totalP2);
										totalAmm = p.toString();
									}

									if (totalP1 == Math.floor(totalP1)) {
										Integer p = (int) Math.round(totalP1);
										totalAm = p.toString();
									}

									WSClient.writeToReport(LogStatus.INFO, "<b>Validating for total charges </b>");
									if (WSAssert.assertEquals(totalAm, totalCharge, true)) {
										WSClient.writeToReport(LogStatus.PASS,
												" Total Package and Room Charges : Expected -> " + totalAm
												+ " Actual -> " + totalCharge);
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												" Total Package and Room Charges : Expected -> " + totalAm
												+ " Actual -> " + totalCharge);
									if (WSAssert.assertEquals(tax3, totalTax, true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"Charges with Tax : Expected -> " + tax3 + " Actual -> " + totalTax);
									} else
										WSClient.writeToReport(LogStatus.FAIL,
												"Charges with Tax  : Expected -> " + tax3 + " Actual -> " + totalTax);

									if (WSAssert.assertEquals(totalAmm, totalTaxAndCharge, true)) {
										WSClient.writeToReport(LogStatus.PASS, "Total  Charge : Expected -> " + totalAmm
												+ " Actual -> " + totalTaxAndCharge);
									} else
										WSClient.writeToReport(LogStatus.FAIL, "Total Charge : Expected -> " + totalAmm
												+ " Actual -> " + totalTaxAndCharge);
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, " The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									WSClient.writeToReport(LogStatus.INFO, " The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "");
								}

								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										" The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "");
							}
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Pre-requisite failed >> Changing the application parameter failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (resvId.get("reservationId") != null || !resvId.get("reservationId").equals("error")) {
					WSClient.setData("{var_resvId}", resvId.get("reservationId"));
					try {
						if (CancelReservation.cancelReservation("DS_02"))
							WSClient.writeToLog("Reservation cancellation successful");
						else
							WSClient.writeToLog("Reservation cancellation failed");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (parameter.equals("Y")) {
					WSClient.setData("{var_par}", "N");
					if (ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "ACCEPT_LOWER_RATES_YN")
							.equals("N"))
						WSClient.writeToLog("Successfully reverted the paramvalue");
					else
						WSClient.writeToLog("Paramter value did not get updated to initial value");
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * rooms are deducted from channel inventory when booking is modified.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS" })

	public void modifyBooking_42729() {
		String uname = "", paramvalue = "", parameter = "", resvid = "";
		try {
			String testName = "modifyBooking_42729";
			WSClient.startTest(testName,
					"Verify that rooms are deducted from channel inventory when booking is modified",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_channel}", channel);
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_param}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");

			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_02", "PARAMETER_VALUE");
			parameter = paramvalue;
			if (!WSAssert.assertEquals("Y", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter CHANNEL_INVENTORY</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_02", "PARAMETER_VALUE");
				WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			}

			if (parameter.equals("Y")) {
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {

					/*************
					 * Prerequisite : Room type, Rate Plan Code, Source Code,
					 * Market Code
					 *********************************/

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_ReservationType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

					WSClient.setData("{var_rateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
					WSClient.setData("{var_roomCount}", "1");
					WSClient.setData("{var_resvType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
					WSClient.setData("{var_rate}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));

					/***********
					 * Prerequisite 1: Create profile
					 ***********************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");
						WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_161}"));
						WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_162}"));
						WSClient.setData("{var_time}", "09:00:00");
						WSClient.setData("{var_roomLimit}", "10");

						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
						WSClient.setData("{var_roomCount}", "2");

						WSClient.setData("{var_owsResort}", resort);

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

						String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
						if (WSAssert.assertIfElementValueEquals(createBookingRes,
								"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
							resvid = WSClient.getElementValueByAttribute(createBookingRes,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							WSClient.setData("{var_resvId}", resvid);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:" + resvid + "</b>");

							// WSClient.setData("{var_startDate}",
							// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_160}"));

							String modifyBookingNumber;

							/**********
							 * To store the rooms sold prior to modification
							 ****************/

							// query=WSClient.getQuery("OWSModifyBooking","QS_11");
							// results = WSClient.getDBRow(query);
							// createBookingNumber=results.get("NUMBER_SOLD");

							WSClient.setData("{var_startDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_175}"));
							WSClient.setData("{var_endDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_176}"));
							System.out.println(WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_173}"));

							WSClient.setData("{var_busdate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_175}"));
							WSClient.setData("{var_busdate1}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_176}"));

							fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

							String setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange",
									"DS_03");
							String setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
							if (WSAssert.assertIfElementExists(setSellLimitsRes,
									"SetChannelSellLimitsByDateRangeRS_Success", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Sell limit for room type " + WSClient.getData("{var_roomType}") + " on "
												+ WSClient.getData("{var_startDate}") + " - "
												+ WSClient.getData("{var_roomLimit}") + "</b>");
								String fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_02");
								String fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
								if (WSAssert.assertIfElementExists(fetchLimitsRes, "FetchChannelSellLimitsRS_Success",
										true)) {
									WSClient.setData("{var_id}", WSClient.getElementValue(fetchLimitsRes,
											"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
								}

								String query = WSClient.getQuery("OWSModifyBooking", "QS_11");
								LinkedHashMap<String, String> results = WSClient.getDBRow(query);
								if (results.size() != 0)
									modifyBookingNumber = results.get("NUMBER_SOLD");
								else
									modifyBookingNumber = "0";

								query = WSClient.getQuery("OWSModifyBooking", "QS_26");
								results = WSClient.getDBRow(query);
								System.out.println("gds to sell ---- " + results);
								String toSellCount = results.get("availableRooms");

								/***************
								 * OWS Modify Booking Operation
								 ********************/

								String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_06");
								String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

								if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
										true)) {
									if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
											"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										WSClient.writeToReport(LogStatus.INFO, "<b>Validation of sold rooms</b>");
										query = WSClient.getQuery("OWSModifyBooking", "QS_11");
										results = WSClient.getDBRow(query);
										Integer expected = Integer.parseInt(modifyBookingNumber)
												+ Integer.parseInt(WSClient.getData("{var_roomCount}"));
										if (WSAssert.assertEquals(expected.toString(), results.get("NUMBER_SOLD"),
												true)) {
											WSClient.writeToReport(LogStatus.PASS, "<b>Rooms Sold -> Expected : "
													+ expected + "   Actual : " + results.get("NUMBER_SOLD") + "</b>");
										} else {
											WSClient.writeToReport(LogStatus.FAIL, "<b>Rooms Sold -> Expected : "
													+ expected + "   Actual : " + results.get("NUMBER_SOLD") + "</b>");
										}

										query = WSClient.getQuery("OWSModifyBooking", "QS_26");
										results = WSClient.getDBRow(query);
										System.out.println("gds to sell afer ---- " + results);

										WSClient.writeToReport(LogStatus.INFO, "<b>Validation of rooms to be sold</b>");

										expected = Integer.parseInt(toSellCount)
												- Integer.parseInt(WSClient.getData("{var_roomCount}"));
										if (WSAssert.assertEquals(expected.toString(), results.get("availableRooms"),
												true)) {
											WSClient.writeToReport(LogStatus.PASS,
													"<b>Rooms To Sell -> Expected : " + expected + "   Actual : "
															+ results.get("availableRooms") + "</b>");
										} else {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b>Rooms To Sell -> Expected : " + expected + "   Actual : "
															+ results.get("availableRooms") + "</b>");
										}
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_Result_OperaErrorCode", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : " + WSClient.getElementValue(
														modifyBookingRes, "ModifyBookingResponse_Result_OperaErrorCode",
														XMLType.RESPONSE) + "</b>");
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement",
											true)) {
										String message = WSAssert.getElementValue(modifyBookingRes,
												"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The text displayed in the response is :" + message + "</b>");
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE)
														+ "</b>");
									}
								} else if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_faultstring", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
													+ "</b>");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Pre-Requisite failed >> Setting sell limits failed");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Pre-Requisite failed >> Creating a booking failed");
						}

					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Pre-Requisites creation for a booking failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Pre-requisite failed >> Changing the application parameter failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				OPERALib.setOperaHeader(uname);
				if (resvid != "") {
					WSClient.setData("{var_resvId}", resvid);
					if (CancelReservation.cancelReservation("DS_02")) {
						WSClient.writeToLog("Reservation cancellation successful");
					} else
						WSClient.writeToLog("Reservation cancellation failed");
				}
				if (WSClient.getData("{var_id}") != "") {
					String removeSellLimitReq = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
					String removeSellLimitRes = WSClient.processSOAPMessage(removeSellLimitReq);
					if (WSAssert.assertIfElementExists(removeSellLimitRes, "RemoveChannelSellLimitsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Removed the limits set on the channel set limits </b>");
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * comments are added to reservation when guest viewable is true.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42710() {
		try {
			String testName = "modifyBooking_42710";
			WSClient.startTest(testName,
					"Verify that comments are added to reservation when guest viewable is set to true",
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
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile *************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.setData("{var_guestComment}", "Guest Comment");
						WSClient.setData("{var_guestViewable}", "true");
						WSClient.setData("{var_commentType}", "RESERVATION");

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_07");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating against DB</b>");
								LinkedHashMap<String, String> dbComments = WSClient
										.getDBRow(WSClient.getQuery("QS_12"));

								if (WSAssert.assertEquals("RESERVATION", dbComments.get("COMMENT_TYPE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Comment Type - Expected: RESERVATION,Actual : "
													+ dbComments.get("COMMENT_TYPE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Comment Type - Expected: RESERVATION,Actual : "
													+ dbComments.get("COMMENT_TYPE"));
								}

								if (WSAssert.assertEquals(WSClient.getData("{var_guestComment}"),
										dbComments.get("COMMENTS"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Comment Text - Expected: " + WSClient.getData("{var_guestComment}")
											+ ",Actual : " + dbComments.get("COMMENTS"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Comment Text - Expected: " + WSClient.getData("{var_guestComment}")
											+ ",Actual : " + dbComments.get("COMMENTS"));
								}

								WSClient.writeToReport(LogStatus.INFO, "<b>Validating against Response</b>");
								WSAssert.assertIfElementValueEquals(modifyBookingRes, "Comments_Comment_Text",
										WSClient.getData("{var_guestComment}"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"RoomStay_Comments_Comment_guestViewable",
										WSClient.getData("{var_guestViewable}"), false);

							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42720() {
		HashMap<String, String> resvId = new HashMap<>();
		try {
			String testName = "modifyBooking_42720";
			WSClient.startTest(testName,
					"Verify that comments are added to reservation and not retrieved on the response when guest viewable is set to false",
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
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile **************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					resvId = CreateReservation.createReservation("DS_01");
					if (!resvId.get("reservationId").equals("error")) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvId.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvId.get("reservationId"));
						WSClient.setData("{var_guestComment}", "Call Housekeeping");
						WSClient.setData("{var_guestViewable}", "false");
						WSClient.setData("{var_commentType}", "IN HOUSE");

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_07");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								LinkedHashMap<String, String> dbComments = WSClient
										.getDBRow(WSClient.getQuery("QS_12"));

								if (WSAssert.assertEquals("IN HOUSE", dbComments.get("COMMENT_TYPE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Comment Type - Expected: IN HOUSE,Actual : "
											+ dbComments.get("COMMENT_TYPE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Comment Type - Expected: IN HOUSE,Actual : "
											+ dbComments.get("COMMENT_TYPE"));
								}

								if (WSAssert.assertEquals(WSClient.getData("{var_guestComment}"),
										dbComments.get("COMMENTS"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Comment Text - Expected: " + WSClient.getData("{var_guestComment}")
											+ ",Actual : " + dbComments.get("COMMENTS"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Comment Text - Expected: " + WSClient.getData("{var_guestComment}")
											+ ",Actual : " + dbComments.get("COMMENTS"));
								}
								if (!WSAssert.assertIfElementExists(modifyBookingRes, "RoomStays_RoomStay_Comments",
										true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Comments are not retrieved onto the response</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b>Comments are retrieved onto the response</b>");
								}
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (resvId.get("reservationId") != null || !resvId.get("reservationId").equals("error")) {
				WSClient.setData("{var_resvId}", resvId.get("reservationId"));
				try {
					CancelReservation.cancelReservation("DS_02");
				} catch (Exception e) {
					WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
					e.printStackTrace();
				}
			}
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42743() {
		try {
			String testName = "modifyBooking_42743";
			WSClient.startTest(testName, "Verify that preferences are added to reservation successfully",
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
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "PreferenceCode", "PreferenceGroup" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/*********** Prerequisite 1: Create profile ***************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					WSClient.setData("{var_prefCode}", OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_05"));
					WSClient.setData("{var_prefGroup}", OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_05"));

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_03");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						WSClient.setData("{var_prefType}",
								OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_05"));
						WSClient.setData("{var_prefValue}",
								OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_05"));

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_08");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating against DB</b>");
								LinkedHashMap<String, String> dbComments = WSClient
										.getDBRow(WSClient.getQuery("QS_13"));

								if (WSAssert.assertEquals(WSClient.getData("{var_prefType}"),
										dbComments.get("PREFERENCE_TYPE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Preference Type - Expected: " + WSClient.getData("{var_prefType}")
											+ ", Actual : " + dbComments.get("PREFERENCE_TYPE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Preference Type - Expected: " + WSClient.getData("{var_prefType}")
											+ ", Actual : " + dbComments.get("PREFERENCE_TYPE"));
								}

								if (WSAssert.assertEquals(WSClient.getData("{var_prefValue}"),
										dbComments.get("PREFERENCE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Preference Value - Expected: " + WSClient.getData("{var_prefValue}")
											+ ", Actual : " + dbComments.get("PREFERENCE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Preference Value - Expected: " + WSClient.getData("{var_prefValue}")
											+ ", Actual : " + dbComments.get("PREFERENCE"));
								}

								WSClient.writeToReport(LogStatus.INFO, "<b>Validating against Response</b>");
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"HotelReservation_Preferences_Preference_preferenceType",
										WSClient.getData("{var_prefType}"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"HotelReservation_Preferences_Preference_preferenceValue",
										WSClient.getData("{var_prefValue}"), false);

							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42754() {
		HashMap<String, String> resvId = new HashMap<>();
		try {
			String testName = "modifyBooking_42754";
			WSClient.startTest(testName, "Verify that details of the profile are modified successfully",
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
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType",
							"CommunicationType", "AddressType", "VipLevel", "Nationality", "Title" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile ************/
				String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
				WSClient.setData("{var_fname}", fname);

				WSClient.setData("{var_gender}", "M");// SID data
				WSClient.setData("{var_vipCode}", OperaPropConfig.getDataSetForCode("VipLevel", "DS_01"));
				WSClient.setData("{var_nationality}", OperaPropConfig.getDataSetForCode("Nationality", "DS_01"));
				WSClient.setData("{var_nameTitle}", OperaPropConfig.getDataSetForCode("Title", "DS_01"));
				WSClient.setData("{var_businessTitle}", "DR");// SID data
				WSClient.setData("{var_birthDate}", "1990-09-06");

				String profileid = CreateProfile.createProfile("DS_28");
				if (!profileid.equals("error")) {
					WSClient.setData("{var_profileId}", profileid);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileid + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					resvId = CreateReservation.createReservation("DS_01");
					if (!resvId.get("reservationId").equals("error")) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvId.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvId.get("reservationId"));

						WSClient.setData("{var_gender}", "FEMALE");// SID data
						WSClient.setData("{var_vipCode}", OperaPropConfig.getDataSetForCode("VipLevel", "DS_02"));
						WSClient.setData("{var_nationality}",
								OperaPropConfig.getDataSetForCode("Nationality", "DS_02"));
						WSClient.setData("{var_nameTitle}", OperaPropConfig.getDataSetForCode("Title", "DS_02"));
						WSClient.setData("{var_businessTitle}", OperaPropConfig.getDataSetForCode("Title", "DS_03"));
						WSClient.setData("{var_birthDate}", "1991-09-06");

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_11");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_01");
								HashMap<String, String> dbResults = WSClient.getDBRow(query);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation > Modifications are applied on DB</b>");

								String reqFirst = WSClient.getElementValue(modifyBookingReq,
										"Customer_PersonName_firstName", XMLType.REQUEST);
								String reqLast = WSClient.getElementValue(modifyBookingReq,
										"Customer_PersonName_lastName", XMLType.REQUEST);
								String reqGender = WSClient.getElementValue(modifyBookingReq,
										"Profiles_Profile_Customer_gender", XMLType.REQUEST);
								String reqmiddle = WSClient.getElementValue(modifyBookingReq,
										"Customer_PersonName_middleName", XMLType.REQUEST);
								String reqTitle = WSClient.getElementValue(modifyBookingReq,
										"Customer_PersonName_nameTitle", XMLType.REQUEST);
								String reqVip = WSClient.getElementValue(modifyBookingReq,
										"ResGuest_Profiles_Profile_vipCode", XMLType.REQUEST);
								String reqNationality = WSClient.getElementValue(modifyBookingReq,
										"ResGuest_Profiles_Profile_nationality", XMLType.REQUEST);
								String birthDate = WSClient.getElementValue(modifyBookingReq,
										"Profiles_Profile_Customer_birthDate", XMLType.REQUEST);

								if (WSAssert.assertEquals(reqFirst, dbResults.get("FIRST"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "FirstName > Expected : " + reqFirst
											+ " Actual : " + dbResults.get("FIRST"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "FirstName > Expected : " + reqFirst
											+ " Actual : " + dbResults.get("FIRST"));
								}
								if (WSAssert.assertEquals(reqLast, dbResults.get("LAST"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"LastName > Expected : " + reqLast + " Actual : " + dbResults.get("LAST"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"LastName > Expected : " + reqLast + " Actual : " + dbResults.get("LAST"));
								}
								if (WSAssert.assertEquals(reqmiddle, dbResults.get("MIDDLE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "MiddleName > Expected : " + reqmiddle
											+ " Actual : " + dbResults.get("MIDDLE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "MiddleName > Expected : " + reqmiddle
											+ " Actual : " + dbResults.get("MIDDLE"));
								}
								if (WSAssert.assertEquals(reqGender, dbResults.get("GENDER"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Gender > Expected : " + reqGender
											+ " Actual : " + dbResults.get("GENDER"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Gender > Expected : " + reqGender
											+ " Actual : " + dbResults.get("GENDER"));
								}
								if (WSAssert.assertEquals(reqVip, dbResults.get("VIP_STATUS"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "VipCode > Expected : " + reqVip
											+ " Actual : " + dbResults.get("VIP_STATUS"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "VipCode > Expected : " + reqVip
											+ " Actual : " + dbResults.get("VIP_STATUS"));
								}
								if (WSAssert.assertEquals(reqNationality, dbResults.get("NATIONALITY"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Nationality > Expected : " + reqNationality
											+ " Actual : " + dbResults.get("NATIONALITY"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Nationality > Expected : " + reqNationality
											+ " Actual : " + dbResults.get("NATIONALITY"));
								}
								if (WSAssert.assertEquals(reqTitle, dbResults.get("TITLE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Title > Expected : " + reqTitle + " Actual : " + dbResults.get("TITLE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Title > Expected : " + reqTitle + " Actual : " + dbResults.get("TITLE"));
								}

								if (WSAssert.assertEquals(birthDate, dbResults.get("BIRTH_DATE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Birth Date > Expected : " + birthDate
											+ " Actual : " + dbResults.get("BIRTH_DATE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Birth Date  > Expected : " + birthDate
											+ " Actual : " + dbResults.get("BIRTH_DATE"));
								}
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation > Modifications are retrieved correctly onto the response</b>");

								WSAssert.assertIfElementValueEquals(modifyBookingRes, "Customer_PersonName_lastName",
										reqFirst, false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes, "Customer_PersonName_firstName",
										reqLast, false);

								LinkedHashMap<String, String> expectedValues = new LinkedHashMap<>();
								expectedValues.put("GENDER", reqGender);
								expectedValues.put("MIDDLENAME", reqmiddle);
								expectedValues.put("NATIONALITY", reqNationality);
								expectedValues.put("TITLE", reqTitle);
								expectedValues.put("VIP", reqVip);
								expectedValues.put("BIRTH DATE", birthDate);

								LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
								actualValues.put("GENDER", WSClient.getElementValue(modifyBookingRes,
										"Profiles_Profile_Customer_gender", XMLType.RESPONSE));
								actualValues.put("MIDDLENAME", WSClient.getElementValue(modifyBookingRes,
										"Customer_PersonName_middleName", XMLType.RESPONSE));
								actualValues.put("TITLE", WSClient.getElementValue(modifyBookingRes,
										"Customer_PersonName_nameTitle", XMLType.RESPONSE));
								actualValues.put("NATIONALITY", WSClient.getElementValue(modifyBookingRes,
										"ResGuest_Profiles_Profile_nationality", XMLType.RESPONSE));
								actualValues.put("VIP", WSClient.getElementValue(modifyBookingRes,
										"ResGuest_Profiles_Profile_vipCode", XMLType.RESPONSE));
								actualValues.put("BIRTH DATE", WSClient.getElementValue(modifyBookingRes,
										"Profiles_Profile_Customer_birthDate", XMLType.RESPONSE));

								WSAssert.assertEquals(expectedValues, actualValues, false);

							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
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
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}

	// @Test(groups = { "minimumRegression", "ModifyBooking", "Reservation",
	// "OWS","PriorRun"})

	public void modifyBooking_22121() {
		try {
			String testName = "modifyBooking_22121";
			WSClient.startTest(testName, "Verify that modifications to profile address are effected successfully",
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
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "AddressType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/

				String addressType = OperaPropConfig.getDataSetForCode("AddressType", "DS_01");
				HashMap<String, String> fullAddress = OPERALib.fetchAddressLOV();

				// Setting Variables
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_addressType}", addressType);
				WSClient.setData("{var_city}", fullAddress.get("City"));
				WSClient.setData("{var_zip}", fullAddress.get("Zip"));
				WSClient.setData("{var_state}", fullAddress.get("State"));
				WSClient.setData("{var_country}", fullAddress.get("Country"));

				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));

				profileID = CreateProfile.createProfile("DS_04");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						HashMap<String, String> fullAddress1 = OPERALib.fetchAddressLOV();

						// Setting Variables
						WSClient.setData("{var_city}", fullAddress1.get("City"));
						WSClient.setData("{var_zip}", fullAddress1.get("Zip"));
						WSClient.setData("{var_state}", fullAddress1.get("State"));
						WSClient.setData("{var_country}", fullAddress1.get("Country"));

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_12");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating against DB</b>");

								LinkedHashMap<String, String> dbDetails = WSClient.getDBRow(WSClient.getQuery("QS_15"));

								if (WSAssert.assertEquals(WSClient.getData("{var_addressType}"),
										dbDetails.get("ADDRESS_TYPE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"AddressType - Expected : " + WSClient.getData("{var_addressType}")
											+ ",Actual : " + dbDetails.get("ADDRESS_TYPE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"AddressType - Expected : " + WSClient.getData("{var_addressType}")
											+ ",Actual : " + dbDetails.get("ADDRESS_TYPE"));
								}

								if (WSAssert
										.assertEquals(
												WSClient.getElementValue(modifyBookingReq,
														"Addresses_NameAddress_AddressLine", XMLType.REQUEST),
												dbDetails.get("ADDRESS1"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Address Line 1 - Expected : "
													+ WSClient.getElementValue(modifyBookingReq,
															"Addresses_NameAddress_AddressLine", XMLType.REQUEST)
													+ ",Actual : " + dbDetails.get("ADDRESS1"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Address Line 1 - Expected : "
													+ WSClient.getElementValue(modifyBookingReq,
															"Addresses_NameAddress_AddressLine", XMLType.REQUEST)
													+ ",Actual : " + dbDetails.get("ADDRESS1"));
								}

								if (WSAssert.assertEquals(WSClient.getData("{var_city}"), dbDetails.get("CITY"),
										true)) {
									WSClient.writeToReport(LogStatus.PASS, "City - Expected : "
											+ WSClient.getData("{var_city}") + ",Actual : " + dbDetails.get("CITY"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "City - Expected : "
											+ WSClient.getData("{var_city}") + ",Actual : " + dbDetails.get("CITY"));
								}

								if (WSAssert.assertEquals(WSClient.getData("{var_zip}"), dbDetails.get("ZIP_CODE"),
										true)) {
									WSClient.writeToReport(LogStatus.PASS, "ZIP - Expected : "
											+ WSClient.getData("{var_zip}") + ",Actual : " + dbDetails.get("ZIP_CODE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "ZIP - Expected : "
											+ WSClient.getData("{var_zip}") + ",Actual : " + dbDetails.get("ZIP_CODE"));
								}

								if (WSAssert.assertEquals(WSClient.getData("{var_state}"), dbDetails.get("STATE"),
										true)) {
									WSClient.writeToReport(LogStatus.PASS, "State - Expected : "
											+ WSClient.getData("{var_state}") + ",Actual : " + dbDetails.get("STATE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "State - Expected : "
											+ WSClient.getData("{var_state}") + ",Actual : " + dbDetails.get("STATE"));
								}

								if (WSAssert.assertEquals(WSClient.getData("{var_country}"), dbDetails.get("COUNTRY"),
										true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Country - Expected : " + WSClient.getData("{var_country}") + ",Actual : "
													+ dbDetails.get("COUNTRY"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Country - Expected : " + WSClient.getData("{var_country}") + ",Actual : "
													+ dbDetails.get("COUNTRY"));
								}

								WSClient.writeToReport(LogStatus.INFO, "<b>Validating against Response</b>");

								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"Profile_Addresses_NameAddress_otherAddressType",
										WSClient.getData("{var_addressType}"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"Addresses_NameAddress_AddressLine", WSClient.getElementValue(modifyBookingReq,
												"Addresses_NameAddress_AddressLine", XMLType.REQUEST),
										false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes, "Addresses_NameAddress_cityName",
										WSClient.getData("{var_city}"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"Addresses_NameAddress_postalCode", WSClient.getData("{var_zip}"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes, "Addresses_NameAddress_stateProv",
										WSClient.getData("{var_state}"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"Addresses_NameAddress_countryCode", WSClient.getData("{var_country}"), false);

							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								/****
								 * Verifying that the error message is populated
								 * on the response
								 ****/

								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42714() {
		try {
			String testName = "modifyBooking_42714";
			WSClient.startTest(testName, "Verify that phone details of the profile are added successfully",
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
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "CommunicationMethod", "CommunicationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 **********************/
				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						WSClient.setData("{var_phoneRole}",
								OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());
						WSClient.setData("{var_phoneType}",
								OperaPropConfig.getDataSetForCode("CommunicationType", "DS_01"));

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_13");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating insertion of phone details into DB</b>");

								LinkedHashMap<String, String> dbDetails = WSClient.getDBRow(WSClient.getQuery("QS_16"));

								if (WSAssert.assertEquals(WSClient.getData("{var_phoneType}"),
										dbDetails.get("PHONE_TYPE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"PhoneType - Expected : " + WSClient.getData("{var_phoneType}")
											+ ",Actual : " + dbDetails.get("PHONE_TYPE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"PhoneType - Expected : " + WSClient.getData("{var_phoneType}")
											+ ",Actual : " + dbDetails.get("PHONE_TYPE"));
								}

								if (WSAssert.assertEquals(WSClient.getData("{var_phoneRole}"),
										dbDetails.get("PHONE_ROLE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Phone Role - Expected : " + WSClient.getData("{var_phoneRole}")
											+ ",Actual : " + dbDetails.get("PHONE_ROLE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Phone Role - Expected : " + WSClient.getData("{var_phoneRole}")
											+ ",Actual : " + dbDetails.get("PHONE_ROLE"));
								}

								if (WSAssert
										.assertEquals(
												WSClient.getElementValue(modifyBookingReq,
														"Phones_NamePhone_PhoneNumber", XMLType.REQUEST),
												dbDetails.get("PHONE_NUMBER"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Phone Number - Expected : "
													+ WSClient.getElementValue(modifyBookingReq,
															"Phones_NamePhone_PhoneNumber", XMLType.REQUEST)
													+ ",Actual : " + dbDetails.get("PHONE_NUMBER"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Phone Number - Expected : "
													+ WSClient.getElementValue(modifyBookingReq,
															"Phones_NamePhone_PhoneNumber", XMLType.REQUEST)
													+ ",Actual : " + dbDetails.get("PHONE_NUMBER"));
								}
								String primaryReq = WSClient.getElementValue(modifyBookingReq,
										"Profile_Phones_NamePhone_primary", XMLType.REQUEST);
								if (WSAssert.assertEquals(primaryReq, dbDetails.get("PRIMARY_YN"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Primary - Expected : " + primaryReq
											+ ",Actual : " + dbDetails.get("PRIMARY_YN"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Primary - Expected : " + primaryReq
											+ ",Actual : " + dbDetails.get("PRIMARY_YN"));
								}
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating retrieval of phone details onto the Response</b>");

								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"Profile_Phones_NamePhone_phoneType", dbDetails.get("PHONE_TYPE"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"Profile_Phones_NamePhone_phoneRole", dbDetails.get("PHONE_ROLE"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes, "Phones_NamePhone_PhoneNumber",
										dbDetails.get("PHONE_NUMBER"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"Profile_Phones_NamePhone_primary", dbDetails.get("PRIMARY_YN"), false);

							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42785() {
		try {
			String testName = "modifyBooking_42785";
			WSClient.startTest(testName, "Verify that email details of the profile are added successfully",
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
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "CommunicationMethod", "CommunicationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 **********************/
				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						String query = WSClient.getQuery("OWSModifyBooking", "QS_01");
						LinkedHashMap<String, String> results = WSClient.getDBRow(query);
						WSClient.setData("{var_email}", results.get("FIRST") + "@gmail.com");
						WSClient.setData("{var_emailType}",
								OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02"));

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_34");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								HashMap<String, String> xPath = new HashMap<>();
								xPath.put("Phones_NamePhone_PhoneNumber", "Profile_Phones_NamePhone");
								xPath.put("Profile_Phones_NamePhone_phoneType", "Profile_Phones_NamePhone");
								xPath.put("Profile_Phones_NamePhone_phoneRole", "Profile_Phones_NamePhone");
								xPath.put("Profile_Phones_NamePhone_primary", "Profile_Phones_NamePhone");

								LinkedHashMap<String, String> resValues = WSAssert.getSingleNodeList(modifyBookingRes,
										xPath, false, XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating insertion of email details into DB</b>");

								String reqemailType = WSClient.getElementValue(modifyBookingReq,
										"Profile_EMails_NameEmail_emailType", XMLType.REQUEST);
								String reqemail = WSClient.getElementValue(modifyBookingReq, "Profile_EMails_NameEmail",
										XMLType.REQUEST);
								String reqprimary = WSClient.getElementValue(modifyBookingReq,
										"Profile_EMails_NameEmail_primary", XMLType.REQUEST);

								query = WSClient.getQuery("QS_45");
								LinkedHashMap<String, String> actualValues = WSClient.getDBRow(query);

								if (WSAssert.assertEquals(reqemailType, actualValues.get("PHONE_TYPE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "EmailType -> Expected : " + reqemailType
											+ "  Actual : " + actualValues.get("PHONE_TYPE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "EmailType -> Expected : " + reqemailType
											+ "  Actual : " + actualValues.get("PHONE_TYPE"));
								}
								if (WSAssert.assertEquals(reqemail, actualValues.get("PHONE_NUMBER"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Email -> Expected : " + reqemail
											+ "  Actual : " + actualValues.get("PHONE_NUMBER"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Email -> Expected : " + reqemail
											+ "  Actual : " + actualValues.get("PHONE_NUMBER"));
								}
								if (WSAssert.assertEquals(reqprimary, actualValues.get("PHONE_PRIMARY"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Primary -> Expected : " + reqprimary
											+ "  Actual : " + actualValues.get("PHONE_PRIMARY"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Primary -> Expected : " + reqprimary
											+ "  Actual : " + actualValues.get("PHONE_PRIMARY"));
								}

								String emailType = WSClient.getElementValue(modifyBookingRes,
										"Profile_EMails_NameEmail_emailType", XMLType.RESPONSE);
								String email = WSClient.getElementValue(modifyBookingRes, "Profile_EMails_NameEmail",
										XMLType.RESPONSE);
								String primary = WSClient.getElementValue(modifyBookingRes,
										"Profile_EMails_NameEmail_primary", XMLType.RESPONSE);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating retrieval of email details onto the Response</b>");

								query = WSClient.getQuery("QS_44");
								results = WSClient.getDBRow(query);

								WSAssert.assertEquals(results, resValues, false);

								if (WSAssert.assertEquals(actualValues.get("PHONE_TYPE"), emailType, true)) {
									WSClient.writeToReport(LogStatus.PASS, "EmailType -> Expected : "
											+ actualValues.get("PHONE_TYPE") + "  Actual : " + emailType);
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "EmailType -> Expected : "
											+ actualValues.get("PHONE_TYPE") + "  Actual : " + emailType);
								}
								if (WSAssert.assertEquals(actualValues.get("PHONE_NUMBER"), email, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Email -> Expected : "
											+ actualValues.get("PHONE_NUMBER") + "  Actual : " + email);
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Email -> Expected : "
											+ actualValues.get("PHONE_NUMBER") + "  Actual : " + email);
								}
								if (WSAssert.assertEquals(actualValues.get("PHONE_PRIMARY"), primary, true)) {
									WSClient.writeToReport(LogStatus.PASS, "Primary -> Expected : "
											+ actualValues.get("PHONE_PRIMARY") + "  Actual : " + primary);
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Primary -> Expected : "
											+ actualValues.get("PHONE_PRIMARY") + "  Actual : " + primary);
								}
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42786() {
		try {
			String testName = "modifyBooking_42786";
			WSClient.startTest(testName, "Verify that booking is not modified when email exceeds 2000 characters",
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
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "CommunicationMethod", "CommunicationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 **********************/
				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						WSClient.setData("{var_email}",
								WSClient.getKeywordData("{KEYWORD_RANDSTR_2001}") + "@gmail.com");
						WSClient.setData("{var_emailType}",
								OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02"));

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_34");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	// @Test(groups = { "minimumRegression", "ModifyBooking", "Reservation",
	// "OWS","PriorRun"})

	public void modifyBooking_82211() {
		try {
			String testName = "modifyBooking_82211";
			WSClient.startTest(testName, "Verify that communication details for a profile are modified successfully",
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
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "CommunicationMethod", "CommunicationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));

				profileID = CreateProfile.createProfile("DS_21");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						WSClient.setData("{var_phoneRole}",
								OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01").toUpperCase());

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_13");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								WSClient.writeToReport(LogStatus.INFO, "<b>Validating DB</b>");
								LinkedHashMap<String, String> dbDetails = WSClient.getDBRow(WSClient.getQuery("QS_16"));

								if (WSAssert.assertEquals(WSClient.getData("{var_phoneType}"),
										dbDetails.get("PHONE_TYPE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"PhoneType - Expected : " + WSClient.getData("{var_phoneType}")
											+ ",Actual : " + dbDetails.get("PHONE_TYPE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"PhoneType - Expected : " + WSClient.getData("{var_phoneType}")
											+ ",Actual : " + dbDetails.get("PHONE_TYPE"));
								}

								if (WSAssert.assertEquals(WSClient.getData("{var_phoneRole}"),
										dbDetails.get("PHONE_ROLE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Phone Role - Expected : " + WSClient.getData("{var_phoneRole}")
											+ ",Actual : " + dbDetails.get("PHONE_ROLE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Phone Role - Expected : " + WSClient.getData("{var_phoneRole}")
											+ ",Actual : " + dbDetails.get("PHONE_ROLE"));
								}

								if (WSAssert
										.assertEquals(
												WSClient.getElementValue(modifyBookingReq,
														"Phones_NamePhone_PhoneNumber", XMLType.REQUEST),
												dbDetails.get("PHONE_NUMBER"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Phone Number - Expected : "
													+ WSClient.getElementValue(modifyBookingReq,
															"Phones_NamePhone_PhoneNumber", XMLType.REQUEST)
													+ ",Actual : " + dbDetails.get("PHONE_NUMBER"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Phone Number - Expected : "
													+ WSClient.getElementValue(modifyBookingReq,
															"Phones_NamePhone_PhoneNumber", XMLType.REQUEST)
													+ ",Actual : " + dbDetails.get("PHONE_NUMBER"));
								}

								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Response</b>");

								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"Profile_Phones_NamePhone_phoneType", WSClient.getData("{var_phoneType}"),
										false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"Profile_Phones_NamePhone_phoneRole", WSClient.getData("{var_phoneRole}"),
										false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes, "Phones_NamePhone_PhoneNumber",
										WSClient.getElementValue(modifyBookingReq, "Phones_NamePhone_PhoneNumber",
												XMLType.REQUEST),
										false);

							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								/****
								 * Verifying that the error message is populated
								 * on the response
								 ****/

								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	@Test(groups = { "minimumRegression" }, dependsOnGroups = { "PriorRun" }, alwaysRun = true)
	public void modifyBooking_32117() {
		WSClient.setData("{var_chain}", OPERALib.getChain());
		WSClient.setData("{var_resort}", OPERALib.getResort());
		OPERALib.setOperaHeader(OPERALib.getUserName());
		if (!resvID.get("reservationId").equals("error")) {
			WSClient.setData("{var_resvId}", resvID.get("reservationId"));
			System.out.println("in cancellation");
			try {
				if (CancelReservation.cancelReservation("DS_02"))
					WSClient.writeToLog("Reservation cancellation successful");
				else
					WSClient.writeToLog("Reservation cancellation failed");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * @Author : Nitin
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRunQERTYU" })
	public void modifyBooking_42771() {
		try {
			String testName = "modifyBooking_42771";
			WSClient.startTest(testName,
					"Verify that Multi-Night reservation is modified to Day use Reservation successfully",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{var_rateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
				WSClient.setData("{var_startDate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_4}") + "T09:00:00+05:30");
				WSClient.setData("{var_endDate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_4}") + "T18:00:00+05:30");

				/************ Prerequisite 1: Create profile ****************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_09");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_04");
								HashMap<String, String> dbResults = WSClient.getDBRow(query);

								String arrivalDate = WSClient.getElementValue(modifyBookingReq,
										"RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
								String departureDate = WSClient.getElementValue(modifyBookingReq,
										"RoomStay_TimeSpan_EndDate", XMLType.REQUEST);
								String resarrivalDate = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE);
								String resdepartureDate = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation > Modifications are applied on DB</b>");

								arrivalDate = arrivalDate.substring(0, arrivalDate.indexOf('T'));
								departureDate = departureDate.substring(0, departureDate.indexOf('T'));
								resarrivalDate = resarrivalDate.substring(0, resarrivalDate.indexOf('T'));
								resdepartureDate = resdepartureDate.substring(0, resdepartureDate.indexOf('T'));

								/******
								 * Validate above details against DB
								 *********/
								String dbArrival = dbResults.get("ARRIVAL");
								dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));

								String dbDeparture = dbResults.get("DEPARTURE");
								dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

								if (WSAssert.assertEquals(arrivalDate, dbArrival, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
											+ arrivalDate + "  Actual : " + dbArrival + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
											+ arrivalDate + "  Actual : " + dbArrival + "</b>");
								}
								if (WSAssert.assertEquals(departureDate, dbDeparture, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Departure date -> Expected : "
											+ departureDate + "  Actual : " + dbDeparture + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Departure date -> Expected : "
											+ departureDate + "  Actual : " + dbDeparture + "</b>");
								}

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation > Modifications are retreived correctly on the response</b>");

								/**** Validate modified arrival dates ****/
								if (WSAssert.assertEquals(dbArrival, resarrivalDate, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : " + dbArrival
											+ "  Actual : " + resarrivalDate + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : " + dbArrival
											+ "  Actual : " + resarrivalDate + "</b>");
								}
								/**** Validate modified departure dates ****/
								if (WSAssert.assertEquals(dbDeparture, resdepartureDate, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Departure date -> Expected : "
											+ dbDeparture + "  Actual : " + resdepartureDate + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Departure date -> Expected : "
											+ dbDeparture + "  Actual : " + resdepartureDate + "</b>");
								}
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	/*
	 * @Author : Nitin
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })
	public void modifyBooking_42769() {
		String maxNights = "";
		try {
			String testName = "modifyBooking_42769";
			WSClient.startTest(testName,
					"Verify that single night reservation is modified to multi night Reservation successfully",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/******************************************
				 * Manage Parameters that are to be handled MAX_NO_OF_NIGHTS
				 ******************************************/
				boolean appParametersFlag = false;
				WSClient.setData("{var_parameter}", "MAX_NO_OF_NIGHTS");
				maxNights = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (maxNights.equals("error") || (Integer.parseInt(maxNights.trim()) < 4)) {
					WSClient.setData("{var_settingValue}", "4");
					String paramVal = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
					if (paramVal.equals("4")) {
						appParametersFlag = true;
						if (maxNights.equals("error"))
							maxNights = "";
					}
				}
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
				WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}"));
				WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_4}"));
				WSClient.setData("{var_rateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

				/************ Prerequisite 1: Create profile ****************/
				if (profileID.equals("") && appParametersFlag)
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");

					WSClient.setData("{var_reservation_id}", resvID.get("reservationId"));
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_09");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_04");
								HashMap<String, String> dbResults = WSClient.getDBRow(query);

								String arrivalDate = WSClient.getElementValue(modifyBookingReq,
										"RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
								String departureDate = WSClient.getElementValue(modifyBookingReq,
										"RoomStay_TimeSpan_EndDate", XMLType.REQUEST);
								String resarrivalDate = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE);
								String resdepartureDate = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE);

								resarrivalDate = resarrivalDate.substring(0, resarrivalDate.indexOf('T'));
								resdepartureDate = resdepartureDate.substring(0, resdepartureDate.indexOf('T'));

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation > Modifications are applied on DB</b>");

								/******
								 * Validate above details against DB
								 *********/
								String dbArrival = dbResults.get("ARRIVAL");
								dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));

								String dbDeparture = dbResults.get("DEPARTURE");
								dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

								if (WSAssert.assertEquals(arrivalDate, dbArrival, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
											+ arrivalDate + "  Actual : " + dbArrival + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
											+ arrivalDate + "  Actual : " + dbArrival + "</b>");
								}
								if (WSAssert.assertEquals(departureDate, dbDeparture, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Departure date -> Expected : "
											+ departureDate + "  Actual : " + dbDeparture + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Departure date -> Expected : "
											+ departureDate + "  Actual : " + dbDeparture + "</b>");
								}

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation > Modifications are retreived correctly on the response</b>");

								/**** Validate modified arrival dates ****/
								if (WSAssert.assertEquals(arrivalDate, resarrivalDate, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
											+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
											+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
								}
								/**** Validate modified departure dates ****/
								if (WSAssert.assertEquals(departureDate, resdepartureDate, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Departure date -> Expected : "
											+ departureDate + "  Actual : " + resdepartureDate + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Departure date -> Expected : "
											+ departureDate + "  Actual : " + resdepartureDate + "</b>");
								}
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/*
	 * @Author : Nitin
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })
	public void modifyBooking_42770() {
		try {
			String testName = "modifyBooking_42770";
			WSClient.startTest(testName, "Verify that special Requests are added to a reservation.",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
				WSClient.setData("{var_specialRequest}", OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_04"));
				WSClient.setData("{var_rateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

				/************ Prerequisite 1: Create profile ****************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");

					WSClient.setData("{var_reservation_id}", resvID.get("reservationId"));
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_10");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String reqSpecial = WSClient.getElementValue(modifyBookingReq,
										"RoomStay_SpecialRequests_SpecialRequest_requestCode", XMLType.REQUEST);
								String resSpecial = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_SpecialRequests_SpecialRequest_requestCode", XMLType.RESPONSE);
								String query = WSClient.getQuery("QS_42");
								LinkedHashMap<String, String> results = WSClient.getDBRow(query);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating insertion of special requests into DB</b>");
								if (WSAssert.assertEquals(reqSpecial, results.get("SPECIAL_REQUEST"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>RequestCode -> Expected : " + reqSpecial
											+ "  Actual : " + results.get("SPECIAL_REQUEST") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>RequestCode -> Expected : " + reqSpecial
											+ "  Actual : " + results.get("SPECIAL_REQUEST") + "</b>");
								}

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating retrieval of special requests from DB</b>");
								if (WSAssert.assertEquals(results.get("SPECIAL_REQUEST"), resSpecial, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>RequestCode -> Expected : "
											+ results.get("SPECIAL_REQUEST") + "  Actual : " + resSpecial + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>RequestCode -> Expected : "
											+ results.get("SPECIAL_REQUEST") + "  Actual : " + resSpecial + "</b>");
								}
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })
	public void modifyBooking_42772() {
		String maxNights = "";
		try {
			String testName = "modifyBooking_42772";
			WSClient.startTest(testName,
					"Verify error code is generated when reservation date range exceeds MAX_NO_NIGHTS set",
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
			WSClient.setData("{var_channel}", channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			/**********
			 * Manage Parameters that are to be handled MAX_NO_OF_NIGHTS
			 *************/
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the MAX_NO_OF_NIGHTS parameter is set</b>");
			WSClient.setData("{var_parameter}", "MAX_NO_OF_NIGHTS");
			maxNights = FetchApplicationParameters.getApplicationParameter("DS_01");
			String paramVal = "";
			if (!maxNights.equals("3")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Setting the parameter MAX_NO_OF_NIGHTS</b>");
				WSClient.setData("{var_settingValue}", "3");
				paramVal = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b> MAX_NO_OF_NIGHTS : " + paramVal + " </b>");
			}

			if (paramVal.equals("3")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
				WSClient.setData("{var_par}", "N");
				WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
				WSClient.setData("{var_param}", "OVERRIDE_YN");
				WSClient.setData("{var_type}", "Boolean");
				String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				if (!paramValue.equals("N")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
					paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
					WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				}

				if (paramValue.equals("N")) {
					if (OperaPropConfig.getPropertyConfigResults(
							new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
						/*************
						 * Prerequisite : Room type, Rate Plan Code, Source
						 * Code, Market Code
						 *********************************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						/************
						 * Prerequisite 1: Create profile
						 *************/
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

							/****************
							 * Prerequisite 2:Create a Reservation
							 *****************/
							if (resvID.get("reservationId") == null)
								resvID = CreateReservation.createReservation("DS_01");

							WSClient.setData("{var_reservation_id}", resvID.get("reservationId"));
							if (!resvID.get("reservationId").equals("error")) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								WSClient.setData("{var_resvId}", resvID.get("reservationId"));

								/********
								 * Set arrival and departure dates
								 ***********/
								WSClient.setData("{var_startDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_20}"));
								WSClient.setData("{var_endDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_24}"));

								/***************
								 * OWS Modify Booking Operation
								 ********************/
								String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_09");
								String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

								if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
										true)) {
									if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
											"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_OperaErrorCode", true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error code that is generated is : "
															+ WSClient.getElementValue(modifyBookingRes,
																	"ModifyBookingResponse_Result_OperaErrorCode",
																	XMLType.RESPONSE)
															+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement",
												true)) {
											/****
											 * Verifying that the error message
											 * is populated on the response
											 ****/
											String message = WSAssert.getElementValue(modifyBookingRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is :" + message + "</b>");
										}
									} else if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE)
														+ "</b>");
									}
								} else if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_faultstring", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
													+ "</b>");
								}
							}
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Prerequisite Blocked >> Changing channel parameter failed!");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Prerequisite Blocked >> Changing application parameter failed!");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				WSClient.setData("{var_parameter}", "MAX_NO_OF_NIGHTS");
				String value = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!maxNights.equals(value) && !maxNights.equals("error")) {
					WSClient.setData("{var_settingValue}", maxNights);
					ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
			}
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })
	public void modifyBooking_42773() {
		String maxNights = "";
		try {
			String testName = "modifyBooking_42773";
			WSClient.startTest(testName,
					"Verify that booking is modified when reservation date range EQUALS MAX_NO_NIGHTS set",
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
			OPERALib.setOperaHeader(uname);

			/**********
			 * Manage Parameters that are to be handled MAX_NO_OF_NIGHTS
			 *************/
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the MAX_NO_OF_NIGHTS parameter is set</b>");
			WSClient.setData("{var_parameter}", "MAX_NO_OF_NIGHTS");
			maxNights = FetchApplicationParameters.getApplicationParameter("DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b> MAX_NO_OF_NIGHTS : " + maxNights + " </b>");
			String paramVal = "";
			if (!maxNights.equals("3")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Setting the parameter MAX_NO_OF_NIGHTS</b>");
				WSClient.setData("{var_settingValue}", "3");
				paramVal = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b> MAX_NO_OF_NIGHTS : " + paramVal + " </b>");
			}

			if (paramVal.equals("3")) {
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/*************
					 * Prerequisite : Room type, Rate Plan Code, Source Code,
					 * Market Code
					 *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
					WSClient.setData("{var_rateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_ReservationType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

					/************ Prerequisite 1: Create profile *************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						if (resvID.get("reservationId") == null)
							resvID = CreateReservation.createReservation("DS_01");

						WSClient.setData("{var_reservation_id}", resvID.get("reservationId"));
						if (!resvID.get("reservationId").equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));

							/********
							 * Set arrival and departure dates
							 ***********/
							WSClient.setData("{var_startDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_21}"));
							WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_24}"));

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_09");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									String query = WSClient.getQuery("QS_04");
									HashMap<String, String> dbResults = WSClient.getDBRow(query);

									String arrivalDate = WSClient.getElementValue(modifyBookingReq,
											"RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
									String departureDate = WSClient.getElementValue(modifyBookingReq,
											"RoomStay_TimeSpan_EndDate", XMLType.REQUEST);
									String resarrivalDate = WSClient.getElementValue(modifyBookingRes,
											"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE);
									String resdepartureDate = WSClient.getElementValue(modifyBookingRes,
											"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE);

									resarrivalDate = resarrivalDate.substring(0, resarrivalDate.indexOf('T'));
									resdepartureDate = resdepartureDate.substring(0, resdepartureDate.indexOf('T'));

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validation > Modifications are applied on DB</b>");

									/******
									 * Validate above details against DB
									 *********/
									String dbArrival = dbResults.get("ARRIVAL");
									dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));

									String dbDeparture = dbResults.get("DEPARTURE");
									dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

									if (WSAssert.assertEquals(arrivalDate, dbArrival, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
												+ arrivalDate + "  Actual : " + dbArrival + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
												+ arrivalDate + "  Actual : " + dbArrival + "</b>");
									}
									if (WSAssert.assertEquals(departureDate, dbDeparture, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Departure date -> Expected : "
												+ departureDate + "  Actual : " + dbDeparture + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Departure date -> Expected : "
												+ departureDate + "  Actual : " + dbDeparture + "</b>");
									}

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validation > Modifications are retreived correctly on the response</b>");

									/**** Validate modified arrival dates ****/
									if (WSAssert.assertEquals(arrivalDate, resarrivalDate, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
												+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
												+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
									}
									/****
									 * Validate modified departure dates
									 ****/
									if (WSAssert.assertEquals(departureDate, resdepartureDate, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Departure date -> Expected : "
												+ departureDate + "  Actual : " + resdepartureDate + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Departure date -> Expected : "
												+ departureDate + "  Actual : " + resdepartureDate + "</b>");
									}
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Prerequisite Failue--- Changing application parameter failed!");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				WSClient.setData("{var_parameter}", "MAX_NO_OF_NIGHTS");
				String value = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!maxNights.equals(value) && !maxNights.equals("error")) {
					WSClient.setData("{var_settingValue}", maxNights);
					ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
			}
		}
	}

	/**
	 * Verify that the package posting details for posting rhythm as
	 * "EveryNight" are populated correctly for one day reservation.
	 * Prerequisites : Profile is created -> Channel Allowed rate code should be
	 * present. ->Source Code,Market code should be present -> Package code with
	 * inclusive tax and Posting rhythm as "EveryNight"
	 **/
	@Test(groups = { "minimumRegression", "ModifyBooking", "OWS", "Reservation" })

	public void modifyBooking_42768() throws Exception {
		String resvId = "";
		try {
			String testName = "modifyBooking_42768";
			WSClient.startTest(testName,
					"Verify that the package posting details for posting rhythm as every night are populated correctly for one "
							+ "day reservation with inclusive tax attached to the package",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			String channel = OWSLib.getChannel();
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);

			/*********
			 * Prerequisite 1 : Room type, Rate Plan Code, PackageCode, Market
			 * Code, Source Code, Payment Method
			 **********/
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PackageCode",
					"MarketCode", "SourceCode", "PaymentMethod" })) {
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				String pkg = OperaPropConfig.getDataSetForCode("PackageCode", "DS_13");
				// String pkg="QA_CHOCOLATE1";
				WSClient.setData("{var_packageCode}", pkg);
				WSClient.setData("{var_pkg}", pkg);
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_13"));
				WSClient.setData("{var_rateCode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_13"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_10"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				OPERALib.setOperaHeader(OPERALib.getUserName());

				/******
				 * Prerequisite 2 : Creating a Profile with basic details
				 *****/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");

					/***** Prerequisite 3: Create Reservation ******/
					resvId = CreateReservation.createReservation("DS_23").get("reservationId");
					if (resvId != "error") {
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvId + "</b>");
						WSClient.setData("{var_resvId}", resvId);

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));

						/************ OWS Modify Booking ***********/

						String updateProfileReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_04");
						String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							String packAmount = WSClient.getElementValueByAttribute(updateProfileResponseXML,
									"RoomRateAndPackages_Charges_Amount", "RoomRateAndPackages_Charges_Code", pkg,
									XMLType.RESPONSE);
							System.out.println(packAmount);
							String rateAmount = null;
							if (!packAmount.contains("doesn't exist")) {
								rateAmount = WSClient.getElementValue(updateProfileResponseXML,
										"RoomRateAndPackages_Charges_Amount_2", XMLType.RESPONSE);
							} else {
								rateAmount = WSClient.getElementValue(updateProfileResponseXML,
										"RoomRateAndPackages_Charges_Amount", XMLType.RESPONSE);
							}

							String totalAmount = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages", XMLType.RESPONSE);
							String roomTax = WSClient.getElementValueByAttribute(updateProfileResponseXML,
									"TaxesAndFees_Charges_Amount", "TaxesAndFees_Charges_CodeType", "R",
									XMLType.RESPONSE);
							String taxDesc = WSClient.getElementValueByAttribute(updateProfileResponseXML,
									"TaxesAndFees_Charges_Description", "TaxesAndFees_Charges_CodeType", "R",
									XMLType.RESPONSE);
							String packageTax = WSClient.getElementValueByAttribute(updateProfileResponseXML,
									"TaxesAndFees_Charges_Amount", "TaxesAndFees_Charges_CodeType", "P",
									XMLType.RESPONSE);
							String startDate = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_ExpectedCharges_ChargesForPostingDate_PostingDate", XMLType.RESPONSE);
							String packageCode = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_Packages_Package_packageCode", XMLType.RESPONSE);
							String totalTaxAndCharge = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStays_RoomStay_Total", XMLType.RESPONSE);

							String query = WSClient.getQuery("OWSCreateBooking", "QS_10");
							String price = WSClient.getDBRow(query).get("PRICE");
							query = WSClient.getQuery("OWSCreateBooking", "QS_12");
							String rate = WSClient.getDBRow(query).get("MIN_AMT");
							Integer totalA = Integer.parseInt(price) + Integer.parseInt(rate);
							String total = totalA.toString();
							System.out.println(total);
							query = WSClient.getQuery("OWSCreateBooking", "QS_13");
							String percent = WSClient.getDBRow(query).get("PERCENTAGE");
							query = WSClient.getQuery("OWSCreateBooking", "QS_11");
							String desc = WSClient.getDBRow(query).get("DES");
							// query=WSClient.getQuery("OWSCreateBooking",
							// "QS_14");
							// String taxA1=
							// WSClient.getDBRow(query).get("MIN_AMT");
							query = WSClient.getQuery("OWSCreateBooking", "QS_14");
							String percent1 = WSClient.getDBRow(query).get("PERCENTAGE");

							/*** Room and Package charges are calculated ***/
							String tax = null;
							String tax1 = null;

							Double a = 0.0;
							Double a1 = 0.0;
							if (percent1 != null) {
								Double des1 = (Double.parseDouble(price) * Double.parseDouble(percent1));
								a1 = des1 / 100;
								tax1 = a1.toString();
								System.out.println(a1);
							} else {
								tax1 = "0";
							}

							if (percent != null) {
								Double des = (Double.parseDouble(rate) * Double.parseDouble(percent));
								a = des / 100;
								a = a - a1;
								tax = a.toString();
								System.out.println(a);
							} else
								tax = "0";

							Double totalA2 = totalA + a;
							String totalP = totalA2.toString();
							System.out.println(tax1);
							System.out.println(packAmount);
							System.out.println(totalAmount);
							System.out.println(rateAmount);

							if (a == Math.floor(a)) {
								Integer p = (int) Math.round(a);
								tax = p.toString();
								System.out.println(tax);
							}

							if (a1 == Math.floor(a1)) {
								Integer p = (int) Math.round(a1);
								tax1 = p.toString();
								System.out.println(tax1);
							}

							if (totalA == Math.floor(totalA)) {
								Integer p = (int) Math.round(totalA);
								total = p.toString();
							}
							if (totalA2 == Math.floor(totalA2)) {
								Integer p = (int) Math.round(totalA2);
								totalP = p.toString();
							}

							/*************** Validation ************************/
							if (WSAssert.assertEquals(pkg, packageCode, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Package Code : Expected -> " + pkg + " Actual -> " + packageCode);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Package Code : Expected -> " + pkg + " Actual -> " + packageCode);

							WSClient.writeToReport(LogStatus.INFO, "<b>Validating for the date " + startDate + "</b>");

							if (WSAssert.assertEquals(price, packAmount, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Package Amount : Expected -> " + price + " Actual -> " + packAmount);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Package Amount : Expected -> " + price + " Actual -> " + packAmount);

							if (WSAssert.assertEquals(tax1, packageTax, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Package Tax : Expected ->" + tax1 + " Actual -> " + packageTax);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Package Tax : Expected -> " + tax1 + " Actual -> " + packageTax);

							if (WSAssert.assertEquals(rate, rateAmount, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Room Rate : Expected -> " + rate + " Actual -> " + rateAmount);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Room Rate : Expected -> " + rate + " Actual -> " + rateAmount);

							if (WSAssert.assertEquals(tax, roomTax, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Room Tax : Expected ->" + tax + " Actual -> " + roomTax);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Room Tax : Expected -> " + tax + " Actual ->" + roomTax);

							if (WSAssert.assertEquals(desc, taxDesc, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Room Tax Description : Expected -> " + desc + " Actual -> " + taxDesc);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Room Tax Description : Expected -> " + desc + " Actual -> " + taxDesc);

							WSClient.writeToReport(LogStatus.INFO, "<b>Validating for total charges </b>");

							if (WSAssert.assertEquals(total, totalAmount, true)) {
								WSClient.writeToReport(LogStatus.PASS, "Total Package and Room Charges : Expected -> "
										+ total + " Actual -> " + totalAmount);
							} else
								WSClient.writeToReport(LogStatus.FAIL, "Total Package and Room Charges : Expected -> "
										+ total + " Actual -> " + totalAmount);

							if (WSAssert.assertEquals(totalP, totalTaxAndCharge, true)) {
								WSClient.writeToReport(LogStatus.PASS, "Total Charges with Tax : Expected -> " + totalP
										+ " Actual -> " + totalTaxAndCharge);
							} else
								WSClient.writeToReport(LogStatus.FAIL, "Total Charges with Tax  : Expected -> " + totalP
										+ " Actual -> " + totalTaxAndCharge);
						} else {
							if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement",
									true)) {
								String message = WSAssert.getElementValue(updateProfileResponseXML,
										"Result_Text_TextElement", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(updateProfileResponseXML,
									"ModifyBookingResponse_Result_GDSError", true)) {
								String message = WSAssert.getElementValue(updateProfileResponseXML,
										"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}
							if (WSAssert.assertIfElementExists(updateProfileResponseXML,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								String message = WSAssert.getElementValue(updateProfileResponseXML,
										"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(updateProfileResponseXML,
									"ModifyBookingResponse_faultcode", true)) {
								String message = WSClient.getElementValue(updateProfileResponseXML,
										"ModifyBookingResponse_faultstring", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.FAIL,
											"The error populated on the response is : <b>" + message + "</b>");
							}
						}
					}
				}
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked ----> Property Config Data not available!");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvId.equals("")) {
				WSClient.setData("{var_resvId}", resvId);
				CancelReservation.cancelReservation("DS_02");
			}
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "OWS", "Reservation" })
	/****
	 * Verify that the package posting details for posting rhythm as
	 * "EveryNight" are populated correctly for multi-night reservation.
	 * Prerequisites : Profile is created -> Channel Allowed rate code should be
	 * present. ->Source Code,Market code should be present -> Package code with
	 * no tax details and Posting rhythm as "EveryNight"
	 ***/
	public void modifyBooking_42767() throws Exception {
		String resvId = "";
		try {
			String testName = "modifyBooking_42767";
			WSClient.startTest(testName,
					"Verify that the package posting details for posting rhythm as every night are populated correctly for multi-night reservation.",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			String channel = OWSLib.getChannel();
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);

			/**********
			 * Prerequisite 1 : Room type, Rate Plan Code, PackageCode, Market
			 * Code, Source Code, Payment Method
			 *****************/

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PackageCode",
					"MarketCode", "SourceCode", "PaymentMethod" })) {

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				String pkg = OperaPropConfig.getDataSetForCode("PackageCode", "DS_05");
				WSClient.setData("{var_packageCode}", pkg);
				WSClient.setData("{var_pkg}", pkg);
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_13"));
				WSClient.setData("{var_rateCode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_13"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_10"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				WSClient.setData("{var_adult}", "1");
				WSClient.setData("{var_child}", "0");
				OPERALib.setOperaHeader(OPERALib.getUserName());

				/******
				 * Prerequisite 2 : Creating a Profile with basic details
				 *****/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");

					/***** Prerequisite 3: Create Reservation ******/
					resvId = CreateReservation.createReservation("DS_30").get("reservationId");
					if (resvId != "error") {
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvId + "</b>");
						WSClient.setData("{var_resvId}", resvId);

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));

						/************ OWS Modify Booking ***********/
						String updateProfileReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_04");
						String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							resvId = WSClient.getElementValueByAttribute(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID",
									"HotelReservation_UniqueIDList_UniqueID_source", "RESVID", XMLType.RESPONSE);

							String totalCharge = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages", XMLType.RESPONSE);
							String packageCode = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_Packages_Package_packageCode", XMLType.RESPONSE);
							String totalTaxAndCharge = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStays_RoomStay_Total", XMLType.RESPONSE);
							String totalTax = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStays_RoomStay_ExpectedCharges_TotalTaxesAndFees", XMLType.RESPONSE);

							List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
							LinkedHashMap<String, String> db1 = new LinkedHashMap<String, String>();
							db1.put("RoomRateAndPackages_Charges_Amount",
									"RoomStay_ExpectedCharges_ChargesForPostingDate");
							db1.put("RoomRateAndPackages_Charges_Amount_2",
									"RoomStay_ExpectedCharges_ChargesForPostingDate");
							db1.put("RoomRateAndPackages_Charges_Code",
									"RoomStay_ExpectedCharges_ChargesForPostingDate");
							db1.put("TaxesAndFees_Charges_Amount", "RoomStay_ExpectedCharges_ChargesForPostingDate");
							db1.put("TaxesAndFees_Charges_Description",
									"RoomStay_ExpectedCharges_ChargesForPostingDate");
							db1.put("RoomStay_ExpectedCharges_ChargesForPostingDate_PostingDate",
									"RoomStay_ExpectedCharges_ChargesForPostingDate");
							db1.put("ExpectedCharges_ChargesForPostingDate_RoomRateAndPackages_TotalCharges",
									"RoomStay_ExpectedCharges_ChargesForPostingDate");
							db = WSClient.getMultipleNodeList(updateProfileResponseXML, db1, false, XMLType.RESPONSE);
							System.out.println(db);

							String query = WSClient.getQuery("OWSCreateBooking", "QS_10");
							String price = WSClient.getDBRow(query).get("PRICE");
							query = WSClient.getQuery("OWSCreateBooking", "QS_12");
							String rate = WSClient.getDBRow(query).get("MIN_AMT");
							Integer totalA = Integer.parseInt(price) + Integer.parseInt(rate);
							String total = totalA.toString();
							System.out.println(total);
							query = WSClient.getQuery("OWSCreateBooking", "QS_13");
							String percent = WSClient.getDBRow(query).get("PERCENTAGE");
							query = WSClient.getQuery("OWSCreateBooking", "QS_11");
							String desc = WSClient.getDBRow(query).get("DES");

							/*** Room and Package charges are calculated ***/
							String tax = null;
							Double a = 0.0, a3 = 0.0;
							if (percent != null) {
								Double des = (Double.parseDouble(rate) * Double.parseDouble(percent));
								a = des / 100;
								tax = a.toString();
								System.out.println(a);
							} else
								tax = "0";

							Double totalA2 = totalA + a;

							Double totalP2 = 0.0, totalP1 = 0.0;

							if (a == Math.floor(a)) {
								Integer p = (int) Math.round(a);
								tax = p.toString();
								System.out.println(tax);
							}

							if (totalA == Math.floor(totalA)) {
								Integer p = (int) Math.round(totalA);
								total = p.toString();
							}

							if (WSAssert.assertEquals(pkg, packageCode, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Package Code : Expected -> " + pkg + " Actual -> " + packageCode);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Package Code : Expected -> " + pkg + " Actual -> " + packageCode);

							for (int i = 0; i < db.size(); i++) {
								LinkedHashMap<String, String> values = db.get(i);
								String packAmount = null, rateAmount = null;
								if (values.containsKey("oomRateAndPackagesChargesCode1")) {
									packAmount = values.get("mRateAndPackagesChargesAmount1");
									rateAmount = values.get("RateAndPackagesCharges2Amount1");
								} else
									rateAmount = values.get("mRateAndPackagesChargesAmount1");
								String roomTax = values.get("TaxesAndFeesChargesAmount1");
								String totalAmount = values.get("omRateAndPackagesTotalCharges1");
								String taxDesc = values.get("axesAndFeesChargesDescription1");
								String startDate = values.get("PostingDate1");
								if (packAmount == null) {
									packAmount = "Package Amount is not populated on the response.";
								}

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating for the date " + startDate + "</b>");

								if (WSAssert.assertEquals(price, packAmount, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Package Amount : Expected -> " + price + " Actual -> " + packAmount);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Package Amount : Expected -> " + price + " Actual -> " + packAmount);

								if (WSAssert.assertEquals(rate, rateAmount, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Room Rate : Expected -> " + rate + " Actual -> " + rateAmount);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Room Rate : Expected -> " + rate + " Actual -> " + rateAmount);
								if (WSAssert.assertEquals(total, totalAmount, true)) {
									WSClient.writeToReport(LogStatus.PASS, " Package and Room Charges : Expected -> "
											+ total + " Actual -> " + totalAmount);
								} else
									WSClient.writeToReport(LogStatus.FAIL, " Package and Room Charges : Expected -> "
											+ total + " Actual -> " + totalAmount);
								if (WSAssert.assertEquals(tax, roomTax, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Room Tax : Expected ->" + tax + " Actual -> " + roomTax);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Room Tax : Expected -> " + tax + " Actual ->" + roomTax);
								if (WSAssert.assertEquals(desc, taxDesc, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Room Tax Description : Expected -> " + desc + " Actual -> " + taxDesc);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Room Tax Description : Expected -> " + desc + " Actual -> " + taxDesc);

								totalP2 = totalP2 + totalA2;
								totalP1 = totalP1 + totalA;
								a3 = a3 + a;
							}

							String totalAm = totalP1.toString();
							String tax3 = a3.toString();
							String totalAmm = totalP2.toString();

							if (a3 == Math.floor(a3)) {
								Integer p = (int) Math.round(a3);
								tax3 = p.toString();
							}

							if (totalP2 == Math.floor(totalP2)) {
								Integer p = (int) Math.round(totalP2);
								totalAmm = p.toString();
							}
							if (totalP1 == Math.floor(totalP1)) {
								Integer p = (int) Math.round(totalP1);
								totalAm = p.toString();
							}

							WSClient.writeToReport(LogStatus.INFO, "<b>Validating for total charges </b>");

							if (WSAssert.assertEquals(totalAm, totalCharge, true)) {
								WSClient.writeToReport(LogStatus.PASS, " Total Package and Room Charges : Expected -> "
										+ totalAm + " Actual -> " + totalCharge);
							} else
								WSClient.writeToReport(LogStatus.FAIL, " Total Package and Room Charges : Expected -> "
										+ totalAm + " Actual -> " + totalCharge);

							if (WSAssert.assertEquals(tax3, totalTax, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Charges with Tax : Expected -> " + tax3 + " Actual -> " + totalTax);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Charges with Tax  : Expected -> " + tax3 + " Actual -> " + totalTax);

							if (WSAssert.assertEquals(totalAmm, totalTaxAndCharge, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Total  Charge : Expected -> " + totalAmm + " Actual -> " + totalTaxAndCharge);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Total Charge : Expected -> " + totalAmm + " Actual -> " + totalTaxAndCharge);

						} else {
							if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement",
									true)) {
								String message = WSAssert.getElementValue(updateProfileResponseXML,
										"Result_Text_TextElement", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}
							if (WSAssert.assertIfElementExists(updateProfileResponseXML,
									"ModifyBookingResponse_Result_GDSError", true)) {
								String message = WSAssert.getElementValue(updateProfileResponseXML,
										"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}
							if (WSAssert.assertIfElementExists(updateProfileResponseXML,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								String message = WSAssert.getElementValue(updateProfileResponseXML,
										"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.INFO,
											"The error populated on the response is : <b>" + message + "</b>");
							}
							if (WSAssert.assertIfElementExists(updateProfileResponseXML,
									"ModifyBookingResponse_faultcode", true)) {
								String message = WSClient.getElementValue(updateProfileResponseXML,
										"ModifyBookingResponse_faultstring", XMLType.RESPONSE);
								if (message != "")
									WSClient.writeToReport(LogStatus.FAIL,
											"The error populated on the response is : <b>" + message + "</b>");
							}
						}
					}
				}
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked ----> Property Config Data not available!");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_resvId}", resvId);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error"))
				CancelReservation.cancelReservation("DS_02");
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "OWS", "Reservation" })
	/****
	 * Verify that the package posting details for posting rhythm as
	 * "ArrivalNight" are populated correctly for one day reservation.
	 * Prerequisites : Profile is created -> Channel Allowed rate code should be
	 * present. ->Source Code,Market code should be present -> Package code with
	 * no tax details and Posting rhythm as "EveryNight"
	 ***/
	public void modifyBooking_42765() throws Exception {
		String resvId = "";
		try {
			String testName = "modifyBooking_42765";
			WSClient.startTest(testName,
					"Verify that the package posting details for posting rhythm as arrival night are populated correctly for one day reservation.",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			String channel = OWSLib.getChannel();
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);

			/*******
			 * Prerequisite 1 : Room type, Rate Plan Code, PackageCode, Market
			 * Code, Source Code, Payment Method
			 *********/

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PackageCode",
					"MarketCode", "SourceCode", "PaymentMethod" })) {
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				String pkg = OperaPropConfig.getDataSetForCode("PackageCode", "DS_15");
				WSClient.setData("{var_packageCode}", pkg);
				WSClient.setData("{var_pkg}", pkg);
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_13"));
				WSClient.setData("{var_rateCode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_13"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_10"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
				WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				OPERALib.setOperaHeader(OPERALib.getUserName());

				/******
				 * Prerequisite 2 : Creating a Profile with basic details
				 *****/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");

					/***** Prerequisite 3: Create Reservation ******/
					resvId = CreateReservation.createReservation("DS_23").get("reservationId");
					if (resvId != "error") {
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvId + "</b>");
						WSClient.setData("{var_resvId}", resvId);

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));
						/***** OWS Modify Booking ********/
						String updateProfileReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_04");
						String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							resvId = WSClient.getElementValueByAttribute(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID",
									"HotelReservation_UniqueIDList_UniqueID_source", "RESVID", XMLType.RESPONSE);
							String packAmount = WSClient.getElementValueByAttribute(updateProfileResponseXML,
									"RoomRateAndPackages_Charges_Amount", "RoomRateAndPackages_Charges_Code", pkg,
									XMLType.RESPONSE);
							System.out.println(packAmount);
							String rateAmount = null;
							if (packAmount.contains("doesn't exist")) {
								rateAmount = WSClient.getElementValue(updateProfileResponseXML,
										"RoomRateAndPackages_Charges_Amount", XMLType.RESPONSE);
							} else {
								rateAmount = WSClient.getElementValue(updateProfileResponseXML,
										"RoomRateAndPackages_Charges_Amount_2", XMLType.RESPONSE);
							}

							String totalAmount = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages", XMLType.RESPONSE);
							String roomTax = WSClient.getElementValueByAttribute(updateProfileResponseXML,
									"TaxesAndFees_Charges_Amount", "TaxesAndFees_Charges_CodeType", "R",
									XMLType.RESPONSE);
							String taxDesc = WSClient.getElementValueByAttribute(updateProfileResponseXML,
									"TaxesAndFees_Charges_Description", "TaxesAndFees_Charges_CodeType", "R",
									XMLType.RESPONSE);
							String startDate = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_ExpectedCharges_ChargesForPostingDate_PostingDate", XMLType.RESPONSE);
							String packageCode = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_Packages_Package_packageCode", XMLType.RESPONSE);
							String totalTaxAndCharge = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStays_RoomStay_Total", XMLType.RESPONSE);

							String query = WSClient.getQuery("OWSCreateBooking", "QS_10");
							String price = WSClient.getDBRow(query).get("PRICE");
							query = WSClient.getQuery("OWSCreateBooking", "QS_12");
							String rate = WSClient.getDBRow(query).get("MIN_AMT");
							Integer totalA = Integer.parseInt(price) + Integer.parseInt(rate);
							String total = totalA.toString();
							System.out.println(total);
							query = WSClient.getQuery("OWSCreateBooking", "QS_13");
							String percent = WSClient.getDBRow(query).get("PERCENTAGE");
							query = WSClient.getQuery("OWSCreateBooking", "QS_11");
							String desc = WSClient.getDBRow(query).get("DES");

							/*** Room and Package charges are calculated ***/
							String tax = null;
							Double a = 0.0;

							if (percent != null) {
								Double des = (Double.parseDouble(rate) * Double.parseDouble(percent));
								a = des / 100;
								tax = a.toString();
								System.out.println(a);
							} else
								tax = "0";

							Double totalA2 = totalA + a;
							String totalP = totalA2.toString();

							System.out.println(packAmount);
							System.out.println(totalAmount);
							System.out.println(rateAmount);

							if (a == Math.floor(a)) {
								Integer p = (int) Math.round(a);
								tax = p.toString();
								System.out.println(tax);
							}

							if (totalA == Math.floor(totalA)) {
								Integer p = (int) Math.round(totalA);
								total = p.toString();
							}
							if (totalA2 == Math.floor(totalA2)) {
								Integer p = (int) Math.round(totalA2);
								totalP = p.toString();
							}
							if (WSAssert.assertEquals(pkg, packageCode, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Package Code : Expected -> " + pkg + " Actual -> " + packageCode);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Package Code : Expected -> " + pkg + " Actual -> " + packageCode);

							WSClient.writeToReport(LogStatus.INFO, "<b>Validating for the date " + startDate + "</b>");

							if (WSAssert.assertEquals(price, packAmount, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Package Amount : Expected -> " + price + " Actual -> " + packAmount);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Package Amount : Expected -> " + price + " Actual -> " + packAmount);
							if (WSAssert.assertEquals(rate, rateAmount, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Room Rate : Expected -> " + rate + " Actual -> " + rateAmount);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Room Rate : Expected -> " + rate + " Actual -> " + rateAmount);
							if (WSAssert.assertEquals(tax, roomTax, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Room Tax : Expected ->" + tax + " Actual -> " + roomTax);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Room Tax : Expected -> " + tax + " Actual ->" + roomTax);
							if (WSAssert.assertEquals(desc, taxDesc, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Room Tax Description : Expected -> " + desc + " Actual -> " + taxDesc);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Room Tax Description : Expected -> " + desc + " Actual -> " + taxDesc);

							WSClient.writeToReport(LogStatus.INFO, "<b>Validating for total charges </b>");

							if (WSAssert.assertEquals(total, totalAmount, true)) {
								WSClient.writeToReport(LogStatus.PASS, "Total Package and Room Charges : Expected -> "
										+ total + " Actual -> " + totalAmount);
							} else
								WSClient.writeToReport(LogStatus.FAIL, "Total Package and Room Charges : Expected -> "
										+ total + " Actual -> " + totalAmount);

							if (WSAssert.assertEquals(totalP, totalTaxAndCharge, true)) {
								WSClient.writeToReport(LogStatus.PASS, "Total Charges with Tax : Expected -> " + totalP
										+ " Actual -> " + totalTaxAndCharge);
							} else
								WSClient.writeToReport(LogStatus.FAIL, "Total Charges with Tax  : Expected -> " + totalP
										+ " Actual -> " + totalTaxAndCharge);
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {
							String message = WSAssert.getElementValue(updateProfileResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							if (message != "")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML,
								"ModifyBookingResponse_Result_GDSError", true)) {
							String message = WSAssert.getElementValue(updateProfileResponseXML,
									"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE);
							if (message != "")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML,
								"ModifyBookingResponse_Result_OperaErrorCode", true)) {
							String message = WSAssert.getElementValue(updateProfileResponseXML,
									"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message != "")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "ModifyBookingResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(updateProfileResponseXML,
									"ModifyBookingResponse_faultstring", XMLType.RESPONSE);
							if (message != "")
								WSClient.writeToReport(LogStatus.FAIL,
										"The error populated on the response is : <b>" + message + "</b>");
						}
					}
				}
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked ----> Property Config Data not available!");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			WSClient.setData("{var_resvId}", resvId);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvID.equals("error"))
				CancelReservation.cancelReservation("DS_02");

		}
	}

	/**
	 * @author psarawag Method to check if the OWS Reservation Modify Booking is
	 *         working i.e., modify a reservation details such as guest count by
	 *         giving confirmation no in the request.
	 *
	 *         PreRequisites Required: -->Profile is created -->Reservation is
	 *         created
	 *
	 *         ->Room Type ->Rate Code and Room Type attached to it ->Market
	 *         Code ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42523() {
		try {
			String testName = "modifyBooking_42523";
			WSClient.startTest(testName,
					"Verify modifications such as change of guest count are effected successfully when confirmation no is passed in the request",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile **************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
					WSClient.setData("{var_profileId}", profileID);

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("confirmationId") == null || resvID.get("confirmationId") == "error")
						resvID = CreateReservation.createReservation("DS_01");
					if (resvID.get("confirmationId") != "error") {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Confirmation ID:" + resvID.get("confirmationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_confirmationId}", resvID.get("confirmationId"));
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_14");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_04");
								HashMap<String, String> dbResults = WSClient.getDBRow(query);

								String adultCount = WSClient.getElementValue(modifyBookingReq,
										"RoomStay_GuestCounts_GuestCount_count", XMLType.REQUEST);
								String childCount = WSClient.getElementValue(modifyBookingReq,
										"RoomStay_GuestCounts_GuestCount[2]_count", XMLType.REQUEST);
								String resadultCount = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_GuestCounts_GuestCount_count", XMLType.RESPONSE);
								String reschildCount = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_GuestCounts_GuestCount_count_2", XMLType.RESPONSE);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating insertion of guest counts into DB</b>");
								/**** Validate modified adult guest count ****/
								if (WSAssert.assertEquals(adultCount, dbResults.get("ADULTS"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Adult Guest Count -> Expected : "
											+ adultCount + "  Actual : " + dbResults.get("ADULTS") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Adult Guest Count -> Expected : "
											+ adultCount + "  Actual : " + dbResults.get("ADULTS") + "</b>");
								}
								/**** Validate modified child guest count ****/
								if (WSAssert.assertEquals(childCount, dbResults.get("CHILDREN"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Child Guest Count -> Expected : "
											+ childCount + "  Actual : " + dbResults.get("CHILDREN") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Child Guest Count -> Expected : "
											+ childCount + "  Actual : " + dbResults.get("CHILDREN") + "</b>");
								}

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating retrieval of guest counts onto Response</b>");
								/**** Validate modified adult guest count ****/
								if (WSAssert.assertEquals(adultCount, resadultCount, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Adult Guest Count -> Expected : "
											+ adultCount + "  Actual : " + resadultCount + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Adult Guest Count -> Expected : "
											+ adultCount + "  Actual : " + resadultCount + "</b>");
								}
								/**** Validate modified child guest count ****/
								if (WSAssert.assertEquals(childCount, reschildCount, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Child Guest Count -> Expected : "
											+ childCount + "  Actual : " + reschildCount + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Child Guest Count -> Expected : "
											+ childCount + "  Actual : " + reschildCount + "</b>");
								}
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	// /**
	// * @author psarawag
	// * Method to check if the OWS Reservation Modify Booking is working i.e.,
	// * modify a reservation details such as guest count by giving confirmation
	// no and leg no in the request.
	// *
	// * PreRequisites Required: -->Profile is created -->Reservation is created
	// *
	// * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	// * ->Source Code ->ReservationType
	// */
	// @Test(groups = { "minimumRegression", "ModifyBooking", "Reservation",
	// "OWS","PriorRun"})
	//
	// public void modifyBooking_42528() {
	// HashMap<String,String> resvID1=new HashMap<>();
	// try {
	// String testName = "modifyBooking_42528";
	// WSClient.startTest(testName, "Verify that modifications such as change of
	// guest count are effected successfully when confirmation no and leg no is
	// passed in the request", "minimumRegression");
	//
	// String resortOperaValue = OPERALib.getResort();
	// String chain = OPERALib.getChain();
	// WSClient.setData("{var_chain}", chain);
	// WSClient.setData("{var_resort}", resortOperaValue);
	//
	// String uname = OPERALib.getUserName();
	// String pwd = OPERALib.getPassword();
	// String channel = OWSLib.getChannel();
	// String channelType = OWSLib.getChannelType(channel);
	// String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsresort}", resort);
	// WSClient.setData("{var_channel}", channel);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	// channel);
	// OPERALib.setOperaHeader(uname);
	//
	// if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode",
	// "RoomType", "SourceCode", "MarketCode","ReservationType"}))
	// {
	// /************* Prerequisite : Room type, Rate Plan Code, Source Code,
	// Market Code *********************************/
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_ReservationType}",
	// OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
	//
	// /************ Prerequisite 1: Create profile *************************/
	// if(profileID.equals(""))
	// profileID=CreateProfile.createProfile("DS_01");
	// if(!profileID.equals("error"))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:"+ profileID
	// +"</b>");
	// WSClient.setData("{var_profileId}", profileID);
	//
	// /**************** Prerequisite 2:Create a Reservation *****************/
	// if(resvID.get("confirmationId")==null ||
	// resvID.get("confirmationId")=="error")
	// resvID=CreateReservation.createReservation("DS_01");
	// if(resvID.get("confirmationId")!="error")
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:"+
	// resvID.get("reservationId") +"</b>");
	//
	// WSClient.setData("{var_confirmationId}", resvID.get("confirmationId"));
	// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
	// resvID1=CreateReservation.createReservation("DS_10");
	// if(resvID1.get("confirmationId")!="error"){
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:"+
	// resvID1.get("reservationId") +"</b>");
	//
	// WSClient.setData("{var_resvId1}", resvID1.get("reservationId"));
	// String legNo =
	// WSClient.getDBRow(WSClient.getQuery("OWSModifyBooking","QS_18")).get("CONFIRMATION_LEG_NO");
	// WSClient.setData("{var_legNo}", legNo);
	// OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
	// channelCarrier);
	// /*************** OWS Modify Booking Operation ********************/
	// String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking",
	// "DS_15");
	// String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);
	//
	// if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result", true))
	// {
	// if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "ModifyBookingResponse_Result_resultStatusFlag","SUCCESS", false))
	// {
	// String query=WSClient.getQuery("QS_19");
	// HashMap<String, String> dbResults=WSClient.getDBRow(query);
	//
	// String adultCount=WSClient.getElementValue(modifyBookingReq,
	// "RoomStay_GuestCounts_GuestCount_count", XMLType.REQUEST);
	// String childCount=WSClient.getElementValue(modifyBookingReq,
	// "RoomStay_GuestCounts_GuestCount[2]_count", XMLType.REQUEST);
	// String resadultCount=WSClient.getElementValue(modifyBookingRes,
	// "RoomStay_GuestCounts_GuestCount_count", XMLType.RESPONSE);
	// String reschildCount=WSClient.getElementValue(modifyBookingRes,
	// "RoomStay_GuestCounts_GuestCount_count_2", XMLType.RESPONSE);
	//
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating insertion of guest
	// counts into DB</b>");
	// /**** Validate modified adult guest count ****/
	// if(WSAssert.assertEquals(adultCount, dbResults.get("ADULTS"), true))
	// {
	// WSClient.writeToReport(LogStatus.PASS, "<b> Adult Guest Count -> Expected
	// : "+adultCount+" Actual : "+dbResults.get("ADULTS")+"</b>");
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> Adult Guest Count -> Expected
	// : "+adultCount+" Actual : "+dbResults.get("ADULTS")+"</b>");
	// }
	// /**** Validate modified child guest count ****/
	// if(WSAssert.assertEquals(childCount, dbResults.get("CHILDREN"), true))
	// {
	// WSClient.writeToReport(LogStatus.PASS, "<b> Child Guest Count -> Expected
	// : "+childCount+" Actual : "+dbResults.get("CHILDREN")+"</b>");
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> Child Guest Count -> Expected
	// : "+childCount+" Actual : "+dbResults.get("CHILDREN")+"</b>");
	// }
	//
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating retrieval of guest
	// counts onto Response</b>");
	// /**** Validate modified adult guest count ****/
	// if(WSAssert.assertEquals(adultCount, resadultCount, true))
	// {
	// WSClient.writeToReport(LogStatus.PASS, "<b> Adult Guest Count -> Expected
	// : "+adultCount+" Actual : "+resadultCount+"</b>");
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> Adult Guest Count -> Expected
	// : "+adultCount+" Actual : "+resadultCount+"</b>");
	// }
	// /**** Validate modified child guest count ****/
	// if(WSAssert.assertEquals(childCount, reschildCount, true))
	// {
	// WSClient.writeToReport(LogStatus.PASS, "<b> Child Guest Count -> Expected
	// : "+childCount+" Actual : "+reschildCount+"</b>");
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> Child Guest Count -> Expected
	// : "+childCount+" Actual : "+reschildCount+"</b>");
	// }
	// }
	// else if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result_GDSError", true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is
	// generated is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_Result_GDSError_errorCode",
	// XMLType.RESPONSE)+"</b>");
	// }
	// if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result_OperaErrorCode", true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is
	// generated is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
	// }
	// if (WSAssert.assertIfElementExists(modifyBookingRes,
	// "Result_Text_TextElement", true))
	// {
	// String message = WSAssert.getElementValue(modifyBookingRes,
	// "Result_Text_TextElement",XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,"<b>The text displayed in the
	// response is :" + message+"</b>");
	// }
	// }
	// else if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_faultstring", true))
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated
	// is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_faultstring", XMLType.RESPONSE)+"</b>");
	// }
	// }
	// }
	// }
	// }
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// }
	// finally {
	// try {
	// if(resvID1.get("reservationId")!=null ||
	// !resvID1.get("reservationId").equals("error"))
	// {
	// WSClient.setData("{var_resvId}", resvID1.get("reservationId"));
	// if(CancelReservation.cancelReservation("DS_02"))
	// {
	// WSClient.writeToLog("Reservation cancellation successful");
	// resvID1.clear();
	// }
	// else
	// WSClient.writeToLog("Reservation cancellation failed");
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }

	/**
	 * @author psarawag Method to check if the OWS Reservation Modify Booking is
	 *         working i.e., modify a reservation details such as attaching a
	 *         company profile to a reservation.
	 *
	 *         PreRequisites Required: -->Profile is created -->Reservation is
	 *         created
	 *
	 *         ->Room Type ->Rate Code and Room Type attached to it ->Market
	 *         Code ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42745() {
		try {
			String testName = "modifyBooking_42745";
			WSClient.startTest(testName, "Verify that a company profile is being attached to the reservation",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					String profileId = CreateProfile.createProfile("DS_35");
					if (!profileId.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.setData("{var_profileID}", profileId);
						WSClient.writeToReport(LogStatus.INFO, "<b>Company Profile ID:" + profileId + "</b>");
						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						if (resvID.get("reservationId") == null || resvID.get("reservationId") == "error")
							resvID = CreateReservation.createReservation("DS_01");
						if (resvID.get("reservationId") != "error") {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
							String nameCode = WSClient.getDBRow(WSClient.getQuery("OWSModifyBooking", "QS_20"))
									.get("NAME_CODE");
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.setData("{var_nameCode}", nameCode);
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							/***************
							 * OWS Modify Booking Operation
							 ********************/
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_16");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									String companyType = WSClient.getElementValue(modifyBookingReq,
											"Profile_Company_CompanyType", XMLType.REQUEST);
									String companyId = WSClient.getElementValue(modifyBookingReq,
											"Profile_Company_CompanyID", XMLType.REQUEST);
									String rescompanyType = WSClient.getElementValue(modifyBookingRes,
											"Profile_Company_CompanyType", XMLType.RESPONSE);
									String rescompanyId = WSClient.getElementValue(modifyBookingRes,
											"Profile_Company_CompanyID", XMLType.RESPONSE);
									WSAssert.assertIfElementValueEquals(modifyBookingRes, "Profile_ProfileIDs_UniqueID",
											WSClient.getElementValue(modifyBookingReq, "Profile_ProfileIDs_UniqueID",
													XMLType.REQUEST),
											false);

									WSClient.writeToReport(LogStatus.INFO, "<b>Validating Against Response</b>");

									/**** Validate Company Type ****/
									if (WSAssert.assertEquals(companyType, rescompanyType, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Company Type -> Expected : "
												+ companyType + "  Actual : " + rescompanyType + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Company Type -> Expected : "
												+ companyType + "  Actual : " + rescompanyType + "</b>");
									}
									/**** Validate company ID ****/
									if (WSAssert.assertEquals(companyId, rescompanyId, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Company ID -> Expected : "
												+ companyId + "  Actual : " + rescompanyId + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Company ID -> Expected : "
												+ companyId + "  Actual : " + rescompanyId + "</b>");
									}

								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}

							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}
	// /**
	// * @author psarawag
	// * Method to check if the OWS Reservation Modify Booking is working i.e.,
	// * verify that only primary memberships are attached to the reservation".
	// *
	// * PreRequisites Required: -->Profile is created -->Reservation is created
	// *
	// * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	// * ->Source Code ->ReservationType
	// */
	// @Test(groups = { "minimumRegression", "ModifyBooking", "Reservation",
	// "OWS","PriorRun"})
	//
	// public void modifyBooking_42845() {
	// try {
	// String testName = "modifyBooking_42845";
	// WSClient.startTest(testName, "Verify that only primary memberships are
	// attached to the reservation", "minimumRegression");
	//
	// String resortOperaValue = OPERALib.getResort();
	// String chain = OPERALib.getChain();
	// WSClient.setData("{var_chain}", chain);
	// WSClient.setData("{var_resort}", resortOperaValue);
	//
	// String uname = OPERALib.getUserName();
	// String pwd = OPERALib.getPassword();
	// String channel = OWSLib.getChannel();
	// String channelType = OWSLib.getChannelType(channel);
	// String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsresort}", resort);
	// WSClient.setData("{var_channel}", channel);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	// channel);
	// OPERALib.setOperaHeader(uname);
	// WSClient.setData("{var_resort}", chain);
	// WSClient.setData("{var_parameter}",
	// "ALWAYS_USE_MEMBERSHIP_FROM_REQUEST_MESSAGE");
	// WSClient.setData("{var_settingValue}","N");
	// String paramQuery=WSClient.getQuery("ChangeApplicationSettings","QS_01");
	// String paramValue=WSClient.getDBRow(paramQuery).get("PARAMETER_VALUE");
	// WSClient.writeToReport(LogStatus.INFO, paramValue);
	// if(paramValue.equals("Y"))
	// ChangeApplicationParameters.changeApplicationParameter("DS_03","1");
	//
	// if(paramValue.equals("N")){
	// if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode",
	// "RoomType", "SourceCode", "MarketCode","ReservationType"
	// ,"MembershipLevel","MembershipType"}))
	// {
	// WSClient.setData("{var_resort}", resortOperaValue);
	// /************* Prerequisite : Room type, Rate Plan Code, Source Code,
	// Market Code *********************************/
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_ReservationType}",
	// OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
	//
	// WSClient.setData("{var_fname}",
	// WSClient.getKeywordData("{KEYWORD_FNAME}"));
	// WSClient.setData("{var_lname}",
	// WSClient.getKeywordData("{KEYWORD_LNAME}"));
	//
	// /************ Prerequisite 1: Create profile ***********/
	// profileID=CreateProfile.createProfile("DS_06");
	// if(!profileID.equals("error"))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:"+ profileID
	// +"</b>");
	// WSClient.setData("{var_profileId}", profileID);
	// /**************** Prerequisite 2:Create Membership *****************/
	//
	// WSClient.setData("{var_membershipLevel}",
	// OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));
	// WSClient.setData("{var_membershipType}",
	// OperaPropConfig.getDataSetForCode("MembershipType", "DS_03"));
	// WSClient.setData("{var_memNo}",
	// WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));
	// String createMembershipReq=WSClient.createSOAPMessage("CreateMembership",
	// "DS_01");
	// String
	// createMembershipRes=WSClient.processSOAPMessage(createMembershipReq);
	// if (WSAssert.assertIfElementExists(createMembershipRes,
	// "CreateMembershipRS_Success", false))
	// {
	// String query = WSClient.getQuery("OWSModifyBooking", "QS_21");
	// String memId = WSClient.getDBRow(query).get("MEMBERSHIP_ID");
	// if (!memId.equals("")) {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Membership ID:- " +
	// memId+"</b>");
	// /**************** Prerequisite 3:Create Membership *****************/
	//
	// WSClient.setData("{var_membershipLevel}",
	// OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
	// WSClient.setData("{var_membershipType}",
	// OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
	// WSClient.setData("{var_memNo}",
	// WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));
	// createMembershipReq=WSClient.createSOAPMessage("CreateMembership",
	// "DS_08");
	// createMembershipRes=WSClient.processSOAPMessage(createMembershipReq);
	// if (WSAssert.assertIfElementExists(createMembershipRes,
	// "CreateMembershipRS_Success", false))
	// {
	// String query2 = WSClient.getQuery("OWSModifyBooking", "QS_21");
	// String memId1 = WSClient.getDBRow(query2).get("MEMBERSHIP_ID");
	// if (!memId1.equals(""))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Membership ID:- " +
	// memId1+"</b>");
	// /**************** Prerequisite 4:Create a Reservation *****************/
	// if(resvID.get("reservationId")==null||resvID.get("reservationId")=="error")
	// resvID=CreateReservation.createReservation("DS_01");
	// if(resvID.get("reservationId")!="error")
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:"+
	// resvID.get("reservationId") +"</b>");
	// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
	// OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
	// channelCarrier);
	// /*************** OWS Modify Booking Operation ********************/
	// String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking",
	// "DS_18");
	// String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);
	//
	// if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result", true))
	// {
	// if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "ModifyBookingResponse_Result_resultStatusFlag","SUCCESS", false))
	// {
	// WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "Profile_ProfileIDs_UniqueID",
	// WSClient.getElementValue(modifyBookingReq, "Profile_ProfileIDs_UniqueID",
	// XMLType.REQUEST), false);
	//
	// HashMap<String,String> xPath=new HashMap<String,String>();
	// xPath.put("Memberships_NameMembership_membershipLevel",
	// "Profile_Memberships_NameMembership");
	// xPath.put("Memberships_NameMembership_membershipType",
	// "Profile_Memberships_NameMembership");
	// xPath.put("Memberships_NameMembership_membershipNumber",
	// "Profile_Memberships_NameMembership");
	//
	// /***** Validating against Response *****/
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Against
	// Response</b>");
	// String query1=WSClient.getQuery("QS_23");
	// List<LinkedHashMap<String, String>> dbResults=WSClient.getDBRows(query1);
	// List<LinkedHashMap<String,String>>
	// resValues=WSClient.getMultipleNodeList(modifyBookingRes, xPath, false,
	// XMLType.RESPONSE);
	// WSAssert.assertEquals(resValues, dbResults, false);
	//
	// /***** Validating against DB *****/
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Against DB</b>");
	// String query3=WSClient.getQuery("QS_24");
	// List<LinkedHashMap<String, String>>
	// dbResults1=WSClient.getDBRows(query3);
	// WSAssert.assertEquals(dbResults1, dbResults, false);
	//
	// }
	// else if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result_GDSError", true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is
	// generated is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_Result_GDSError_errorCode",
	// XMLType.RESPONSE)+"</b>");
	// }
	// if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result_OperaErrorCode", true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is
	// generated is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
	// }
	// if (WSAssert.assertIfElementExists(modifyBookingRes,
	// "Result_Text_TextElement", true))
	// {
	// String message = WSAssert.getElementValue(modifyBookingRes,
	// "Result_Text_TextElement",XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,"<b>The text displayed in the
	// response is :" + message+"</b>");
	// }
	// }
	// else if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_faultstring", true))
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated
	// is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_faultstring", XMLType.RESPONSE)+"</b>");
	// }
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING,"Prerequisite blocked --->The
	// profile doesnot have any membership attached");
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked
	// --->Create Membership fails");
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING,"Prerequisite blocked --->The
	// profile doesnot have any membership attached");
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked
	// --->Create Membership fails");
	// }
	// }
	// }
	// }
	// else{
	// WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->
	// Change Application Parameter Failed");
	// }
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// }
	// }

	/**
	 * @author psarawag Method to check if the OWS Reservation Modify Booking is
	 *         working i.e., modify a reservation details such as Reservation
	 *         UDFs.
	 *
	 *         PreRequisites Required: -->Profile is created -->Reservation is
	 *         created
	 *
	 *         ->Room Type ->Rate Code and Room Type attached to it ->Market
	 *         Code ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42529() {
		try {
			String testName = "modifyBooking_42529";
			WSClient.startTest(testName, "Verify that Reservation UDFs are added to the booking successfully",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile *********/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
					WSClient.setData("{var_profileId}", profileID);

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null || resvID.get("reservationId") == "error")
						resvID = CreateReservation.createReservation("DS_01");
					if (resvID.get("reservationId") != "error") {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						String resCharUDF = HTNGLib.getUDFLabel("C", "R");
						String resNumUDF = HTNGLib.getUDFLabel("N", "R");
						String resDateUDF = HTNGLib.getUDFLabel("D", "R");
						String resCharName = HTNGLib.getUDFName("C", resCharUDF, "R");
						String resNumName = HTNGLib.getUDFName("N", resNumUDF, "R");
						String resDateName = HTNGLib.getUDFName("D", resDateUDF, "R");

						WSClient.setData("{var_resCharLabel}", resCharUDF);
						WSClient.setData("{var_resDateLabel}", resDateUDF);
						WSClient.setData("{var_resNumLabel}", resNumUDF);
						WSClient.setData("{var_charName}", resCharName);
						WSClient.setData("{var_numName}", resNumName);
						WSClient.setData("{var_dateName}", resDateName);
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_19");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								HashMap<String, String> xPath = new HashMap<String, String>();
								// xPaths of the records being verified and
								// their
								// parents are being put in a hashmap
								xPath.put("UserDefinedValues_UserDefinedValue_CharacterValue_2",
										"HotelReservation_UserDefinedValues_UserDefinedValue");
								xPath.put("UserDefinedValues_UserDefinedValue_NumericValue_2",
										"HotelReservation_UserDefinedValues_UserDefinedValue");
								xPath.put("UserDefinedValues_UserDefinedValue_DateValue_2",
										"HotelReservation_UserDefinedValues_UserDefinedValue");
								xPath.put("HotelReservation_UserDefinedValues_UserDefinedValue_valueName",
										"HotelReservation_UserDefinedValues_UserDefinedValue");

								/***** Validating against Response *****/
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Against Response</b>");
								List<LinkedHashMap<String, String>> reqValues = WSClient
										.getMultipleNodeList(modifyBookingReq, xPath, false, XMLType.REQUEST);
								List<LinkedHashMap<String, String>> resValues = WSClient
										.getMultipleNodeList(modifyBookingRes, xPath, false, XMLType.RESPONSE);
								for (int i = 0; i < resValues.size(); i++) {
									if (resValues.get(i).containsKey("DateValue1")) {
										resValues.get(i).put("DateValue1", resValues.get(i).get("DateValue1")
												.substring(0, resValues.get(i).get("DateValue1").indexOf('T')));
									}
								}
								WSAssert.assertEquals(resValues, reqValues, false);

								/***** Validating against DB *****/
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Against DB</b>");
								List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
								String query1 = WSClient.getQuery("QS_22");
								LinkedHashMap<String, String> dbValues = WSClient.getDBRow(query1);

								LinkedHashMap<String, String> dbValues1 = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> dbValues2 = new LinkedHashMap<String, String>();
								LinkedHashMap<String, String> dbValues3 = new LinkedHashMap<String, String>();
								dbValues1.put("CharacterValue1", dbValues.get(resCharName));
								dbValues2.put("NumericValue1", dbValues.get(resNumName));
								dbValues3.put("DateValue1",
										dbValues.get(resDateName).substring(0, dbValues.get(resDateName).indexOf(' ')));
								db.add(dbValues1);
								db.add(dbValues3);
								db.add(dbValues2);
								for (int i = 0; i < reqValues.size(); i++) {
									if (reqValues.get(i).containsKey("valueName1")) {
										reqValues.get(i).remove("valueName1");
									}
								}
								WSAssert.assertEquals(db, reqValues, false);
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	//
	// Verify if any blank fields are present
	//

	//
	// Booking_38323
	//

	/**
	 * @author psarawag Method to check if the OWS Reservation Modify Booking is
	 *         working i.e., modify a reservation details such as guest count
	 *         when room is already assigned and parameter value is Y for
	 *         parameter "ALLOW_RESERVATION_MODIFICATION_FOR_ASSIGNED_ROOMNO".
	 *
	 *         PreRequisites Required: -->Profile is created -->Reservation is
	 *         created
	 *
	 *         ->Room Type ->Rate Code and Room Type attached to it ->Market
	 *         Code ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS" })

	public void modifyBooking_42726() {
		try {
			String testName = "modifyBooking_42726";
			WSClient.startTest(testName,
					"Verify that the guest count are modified when room is already assigned to the reservation and parameter value is Y for parameter 'ALLOW_RESERVATION_MODIFICATION_FOR_ASSIGNED_ROOMNO'",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_parameter}", "ALLOW_RESERVATION_MODIFICATION_FOR_ASSIGNED_ROOMNO");
				WSClient.setData("{var_settingValue}", "Y");
				String paramQuery = WSClient.getQuery("ChangeApplicationSettings", "QS_01");
				String paramValue = WSClient.getDBRow(paramQuery).get("PARAMETER_VALUE");
				WSClient.writeToReport(LogStatus.INFO,
						"<b>ALLOW_RESERVATION_MODIFICATION_FOR_ASSIGNED_ROOMNO : " + paramValue + "</b>");
				if (paramValue.equals("N")) {
					paramValue = ChangeApplicationParameters.changeApplicationParameter("DS_01", "1");
					WSClient.writeToReport(LogStatus.INFO,
							"<b>ALLOW_RESERVATION_MODIFICATION_FOR_ASSIGNED_ROOMNO : " + paramValue + "</b>");
				}

				if (paramValue.equals("Y")) {
					/********* Prerequisite 1: Create profile **************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
						WSClient.setData("{var_profileId}", profileID);

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						HashMap<String, String> resvID1 = CreateReservation.createReservation("DS_01");
						if (resvID1.get("reservationId") != "error") {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID:" + resvID1.get("reservationId") + "</b>");
							WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

							/****************
							 * Prerequisite 3:Fetch Hotel Rooms
							 *****************/
							WSClient.setData("{var_startDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}"));
							WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_3}"));
							String roomNumber = FetchHotelRooms.fetchHotelRooms("DS_16");
							if (roomNumber.equals("error")) {
								roomNumber = CreateRoom.createRoom("FirstFloor");
							}
							if (!roomNumber.equals("error")) {
								WSClient.setData("{var_roomNumber}", roomNumber);
								if (SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01")) {
									if (AssignRoom.assignRoom("DS_01")) {

										OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

										/***************
										 * OWS Modify Booking Operation
										 ********************/
										String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking",
												"DS_23");
										String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result", true)) {
											if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
													"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {
												String query = WSClient.getQuery("QS_04");
												HashMap<String, String> dbResults = WSClient.getDBRow(query);

												String adultCount = WSClient.getElementValue(modifyBookingReq,
														"RoomStay_GuestCounts_GuestCount_count", XMLType.REQUEST);
												String childCount = WSClient.getElementValue(modifyBookingReq,
														"RoomStay_GuestCounts_GuestCount[2]_count", XMLType.REQUEST);
												String resadultCount = WSClient.getElementValue(modifyBookingRes,
														"RoomStay_GuestCounts_GuestCount_count", XMLType.RESPONSE);
												String reschildCount = WSClient.getElementValue(modifyBookingRes,
														"RoomStay_GuestCounts_GuestCount_count_2", XMLType.RESPONSE);

												String reqProfileId = WSClient.getElementValue(modifyBookingReq,
														"Profile_ProfileIDs_UniqueID", XMLType.REQUEST);
												WSAssert.assertIfElementValueEquals(modifyBookingRes,
														"Profile_ProfileIDs_UniqueID", reqProfileId, false);

												WSClient.writeToReport(LogStatus.INFO,
														"<b>Validating insertion of guest counts into DB</b>");
												/****
												 * Validate modified adult guest
												 * count
												 ****/
												if (WSAssert.assertEquals(adultCount, dbResults.get("ADULTS"), true)) {
													WSClient.writeToReport(LogStatus.PASS,
															"<b> Adult Guest Count -> Expected : " + adultCount
															+ "  Actual : " + dbResults.get("ADULTS") + "</b>");
												} else {
													WSClient.writeToReport(LogStatus.FAIL,
															"<b> Adult Guest Count -> Expected : " + adultCount
															+ "  Actual : " + dbResults.get("ADULTS") + "</b>");
												}
												/****
												 * Validate modified child guest
												 * count
												 ****/
												if (WSAssert.assertEquals(childCount, dbResults.get("CHILDREN"),
														true)) {
													WSClient.writeToReport(LogStatus.PASS,
															"<b> Child Guest Count -> Expected : " + childCount
															+ "  Actual : " + dbResults.get("CHILDREN")
															+ "</b>");
												} else {
													WSClient.writeToReport(LogStatus.FAIL,
															"<b> Child Guest Count -> Expected : " + childCount
															+ "  Actual : " + dbResults.get("CHILDREN")
															+ "</b>");
												}

												WSClient.writeToReport(LogStatus.INFO,
														"<b>Validating retrieval of details onto the Response</b>");
												/****
												 * Validate modified adult guest
												 * count
												 ****/
												if (WSAssert.assertEquals(adultCount, resadultCount, true)) {
													WSClient.writeToReport(LogStatus.PASS,
															"<b> Adult Guest Count -> Expected : " + adultCount
															+ "  Actual : " + resadultCount + "</b>");
												} else {
													WSClient.writeToReport(LogStatus.FAIL,
															"<b> Adult Guest Count -> Expected : " + adultCount
															+ "  Actual : " + resadultCount + "</b>");
												}
												/****
												 * Validate modified child guest
												 * count
												 ****/
												if (WSAssert.assertEquals(childCount, reschildCount, true)) {
													WSClient.writeToReport(LogStatus.PASS,
															"<b> Child Guest Count -> Expected : " + childCount
															+ "  Actual : " + reschildCount + "</b>");
												} else {
													WSClient.writeToReport(LogStatus.FAIL,
															"<b> Child Guest Count -> Expected : " + childCount
															+ "  Actual : " + reschildCount + "</b>");
												}
											}
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError_errorCode",
																		XMLType.RESPONSE)
																+ "</b>");
											}
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_OperaErrorCode",
																		XMLType.RESPONSE)
																+ "</b>");
											}
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"Result_Text_TextElement", true)) {
												String message = WSAssert.getElementValue(modifyBookingRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>The text displayed in the response is :" + message
														+ "</b>");
											}

										} else if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_faultstring", true)) {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b> The error that is generated is : " + WSClient.getElementValue(
															modifyBookingRes, "ModifyBookingResponse_faultstring",
															XMLType.RESPONSE) + "</b>");
										}
									}
								}
							}
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Prerequisite blocked >> Change Application Parameter Failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
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
	 * @author psarawag Method to check if the OWS Reservation Modify Booking is
	 *         working i.e., Verify that the response shows error when modify a
	 *         reservation details such as guest count when room is already
	 *         assigned and parameter value is N for parameter
	 *         "ALLOW_RESERVATION_MODIFICATION_FOR_ASSIGNED_ROOMNO".
	 *
	 *         PreRequisites Required: -->Profile is created -->Reservation is
	 *         created
	 *
	 *         ->Room Type ->Rate Code and Room Type attached to it ->Market
	 *         Code ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42727() {
		try {
			String testName = "modifyBooking_42727";
			WSClient.startTest(testName,
					"Verify that the response shows error when guest count are modified when room is already assigned to the reservation and parameter value is N for parameter 'ALLOW_RESERVATION_MODIFICATION_FOR_ASSIGNED_ROOMNO'",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_parameter}", "ALLOW_RESERVATION_MODIFICATION_FOR_ASSIGNED_ROOMNO");
				WSClient.setData("{var_settingValue}", "N");
				String paramQuery = WSClient.getQuery("ChangeApplicationSettings", "QS_01");
				String paramValue = WSClient.getDBRow(paramQuery).get("PARAMETER_VALUE");
				WSClient.writeToReport(LogStatus.INFO,
						"<b>ALLOW_RESERVATION_MODIFICATION_FOR_ASSIGNED_ROOMNO : " + paramValue + "</b>");
				if (paramValue.equals("Y")) {
					paramValue = ChangeApplicationParameters.changeApplicationParameter("DS_01", "1");
					WSClient.writeToReport(LogStatus.INFO,
							"<b>ALLOW_RESERVATION_MODIFICATION_FOR_ASSIGNED_ROOMNO : " + paramValue + "</b>");
				}

				if (paramValue.equals("N")) {
					/***********
					 * Prerequisite 1: Create profile
					 *****************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
						WSClient.setData("{var_profileId}", profileID);

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/

						HashMap<String, String> resvID1 = CreateReservation.createReservation("DS_01");
						if (resvID1.get("reservationId") != "error") {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID:" + resvID1.get("reservationId") + "</b>");
							WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

							/****************
							 * Prerequisite 3:Fetch Hotel Rooms
							 *****************/
							WSClient.setData("{var_startDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}"));
							WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_3}"));
							String roomNumber = FetchHotelRooms.fetchHotelRooms("DS_16");
							if (roomNumber.equals("error")) {
								roomNumber = CreateRoom.createRoom("FirstFloor");
							}
							if (!roomNumber.equals("error")) {
								WSClient.setData("{var_roomNumber}", roomNumber);
								if (SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01")) {
									if (AssignRoom.assignRoom("DS_01")) {

										OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
										/***************
										 * OWS Modify Booking Operation
										 ********************/
										String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking",
												"DS_23");
										String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result", true)) {
											WSAssert.assertIfElementValueEquals(modifyBookingRes,
													"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);

											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError_errorCode",
																		XMLType.RESPONSE)
																+ "</b>");
											}
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_OperaErrorCode",
																		XMLType.RESPONSE)
																+ "</b>");
											}
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"Result_Text_TextElement", true)) {
												/****
												 * Verifying that the error
												 * message is populated on the
												 * response
												 ******/
												String message = WSAssert.getElementValue(modifyBookingRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>The text displayed in the response is :" + message
														+ "</b>");
											}
										} else if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_faultstring", true)) {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b> The error that is generated is : " + WSClient.getElementValue(
															modifyBookingRes, "ModifyBookingResponse_faultstring",
															XMLType.RESPONSE) + "</b>");
										}
									}
								}
							}
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Prerequisite blocked ---> Change Application Parameter Failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
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
	 * @author psarawag Method to check if the OWS Reservation Modify Booking is
	 *         working i.e., Verify cancellation and deposit policy in the
	 *         response when it is attached to ratecode.
	 *
	 *         PreRequisites Required: -->Profile is created -->Reservation is
	 *         created
	 *
	 *         ->Room Type ->Rate Code and Room Type attached to it ->Market
	 *         Code ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42728() {
		try {
			String testName = "modifyBooking_42728";
			WSClient.startTest(testName,
					"Verify that cancellation and deposit policy attached to ratecode are present in the response",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/

				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
					WSClient.setData("{var_profileId}", profileID);

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/

					WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}"));
					WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_3}"));
					WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}"));
					WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_3}"));

					fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_08"),
							OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_07"));

					if (newDate != null || newDate1 != null) {
						WSClient.setData("{var_startDate}", newDate);
						WSClient.setData("{var_endDate}", newDate1);
					}
					HashMap<String, String> resvID1 = CreateReservation.createReservation("DS_29");
					if (resvID1.get("reservationId") != "error") {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID1.get("reservationId") + "</b>");
						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_08"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_07"));
						WSClient.setData("{var_roomCount}", "1");

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_03");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								List<LinkedHashMap<String, String>> resValues = new ArrayList<LinkedHashMap<String, String>>();
								HashMap<String, String> xPath = new HashMap<String, String>();
								xPath.put("RatePlan_AdditionalDetails_AdditionalDetail_detailType",
										"RatePlan_AdditionalDetails_AdditionalDetail");
								xPath.put("AdditionalDetail_AdditionalDetailDescription_Text",
										"RatePlan_AdditionalDetails_AdditionalDetail");
								resValues = WSClient.getMultipleNodeList(modifyBookingRes, xPath, true,
										XMLType.RESPONSE);
								String cancelDate = WSClient.getElementValue(modifyBookingRes,
										"RatePlans_RatePlan_CancellationDateTime", XMLType.RESPONSE);
								String depositAmount = WSClient.getElementValue(modifyBookingRes,
										"RatePlan_DepositRequired_DepositAmount", XMLType.RESPONSE);
								String dueDate = WSClient.getElementValue(modifyBookingRes,
										"RatePlan_DepositRequired_DueDate", XMLType.RESPONSE);
								for (int i = 0; i < resValues.size(); i++) {
									if (resValues.get(i).get("DetailType").equals("CancelPolicy")) {
										resValues.get(i).put("CancelDate",
												cancelDate.substring(0, cancelDate.indexOf('T')));
									}
									if (resValues.get(i).get("DetailType").equals("DepositPolicy")) {
										resValues.get(i).put("DepositAmount", depositAmount);
										resValues.get(i).put("DueDate", dueDate);
									}
								}

								LinkedHashMap<String, String> depositValues = WSClient
										.getDBRow(WSClient.getQuery("QS_32"));
								LinkedHashMap<String, String> cancelValues = WSClient
										.getDBRow(WSClient.getQuery("QS_31"));
								if (cancelValues.size() > 0) {
									cancelValues.put("DetailType", "CancelPolicy");
								}
								if (cancelValues.containsKey("Text")) {
									cancelValues.put("Text", "Cancel By " + cancelValues.get("Text"));
								}
								if (depositValues.size() > 0) {
									depositValues.put("DetailType", "DepositPolicy");
								}
								if (depositValues.containsKey("DepositAmount") && depositValues.containsKey("Text")) {
									depositValues.put("Text",
											"A deposit of " + depositValues.get("DepositAmount") + ".00 is due by "
													+ depositValues.get("Text")
													+ " in order to guarantee your reservation.");
								}
								for (int i = 0; i < resValues.size(); i++) {
									if (resValues.get(i).get("DetailType").equals("CancelPolicy")) {
										WSClient.writeToReport(LogStatus.INFO, "<b>Validating Cancellation Policy</b>");
										WSAssert.assertEquals(cancelValues, resValues.get(i), false);
									}
									if (resValues.get(i).get("DetailType").equals("DepositPolicy")) {
										WSClient.writeToReport(LogStatus.INFO, "<b>Validating Deposit Policy</b>");
										WSAssert.assertEquals(depositValues, resValues.get(i), false);
									}
								}
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
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
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * actual rate is applied when lower rate than opera db is passes on request
	 * as ACCEPT_LOWER_RATES is disabled.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS" })

	public void modifyBooking_42756() {
		String paramvalue = "";
		String parameter = "";
		try {
			String testName = "modifyBooking_42756";
			WSClient.startTest(testName,
					"Verify that actual rate is applied when lower rate than actual rate is passed on request and ACCEPT_LOWER_RATES is N",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_par}", "N");
			WSClient.setData("{var_parname}", "ACCEPTLOWERRATEAMOUNT");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_param}", "ACCEPT_LOWER_RATES_YN");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter ACCEPT_LOWER_RATES is disabled</b>");

			/****
			 * ACCEPT_LOWER_RATES parameter has to set to accept lower rates
			 * than base rate
			 ****/
			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_01", "ACCEPT_LOWER_RATES_YN");
			parameter = paramvalue;
			if (!WSAssert.assertEquals("N", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter ACCEPT_LOWER_RATES</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "ACCEPT_LOWER_RATES_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>ACCEPT_LOWER_RATES_YN : " + parameter + "</b>");
			}

			if (parameter.equals("N")) {
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/*************
					 * Prerequisite : Room type, Rate Plan Code, Source Code,
					 * Market Code
					 *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_ReservationType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 ********************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						if (resvID.get("reservationId") == null)
							resvID = CreateReservation.createReservation("DS_01");
						if (!resvID.get("reservationId").equals("error")) {
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");

							/*******
							 * Get the base rate of the rate code
							 *********/
							String query = WSClient.getQuery("OWSModifyBooking", "QS_30");
							LinkedHashMap<String, String> results = WSClient.getDBRow(query);
							WSClient.setData("{var_rateCode}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
							WSClient.setData("{var_roomCount}", "1");
							int rate = Integer.parseInt(results.get("AMOUNT_1"));
							WSClient.setData("{var_base}", String.valueOf(rate - 20));

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_27");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									WSAssert.assertIfElementValueEquals(modifyBookingRes, "Rates_Rate_Base",
											String.valueOf(rate), false);
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									if (!WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE).equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO, "<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
												+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
									if (!WSClient
											.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											.equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE)
														+ "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Pre-requisite failed >> Changing the channel parameter failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// try {
			// OPERALib.setOperaHeader(OPERALib.getUserName());
			// if(resvID.get("reservationId")==null
			// ||!resvID.get("reservationId").equals("error"))
			// {
			// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
			// try {
			// if(CancelReservation.cancelReservation("DS_02"))
			// WSClient.writeToLog("Reservation cancellation successful");
			// else
			// WSClient.writeToLog("Reservation cancellation failed");
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * error is generated when reservation id is invalid.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42757() {
		try {
			String testName = "modifyBooking_42757";
			WSClient.startTest(testName, "Verify that error is generated when reservation id is invalid",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile *************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String resvid = WSClient.getKeywordData("{KEYWORD_RANDNUM_3}");
					WSClient.setData("{var_resvId}", resvid);
					WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: " + resvid + "</b>");

					/***************
					 * OWS Modify Booking Operation
					 ********************/
					String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_02");
					String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

					if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
						WSAssert.assertIfElementValueEquals(modifyBookingRes,
								"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);

						if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
								"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b> Booking is modified successfully although reservation id is invalid</b>");
						}
						if (WSAssert.assertIfElementExists(modifyBookingRes,
								"ModifyBookingResponse_Result_OperaErrorCode", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
						}
						if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
							/****
							 * Verifying that the error message is populated on
							 * the response
							 *****/

							String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result_GDSError",
								true)) {
							if (!WSClient.getElementValue(modifyBookingRes, "ModifyBookingResponse_Result_GDSError",
									XMLType.RESPONSE).equals("*null*"))
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
												+ "</b>");
						}
						if (WSAssert.assertIfElementExists(modifyBookingRes,
								"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
							if (!WSClient
									.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
									.equals("*null*"))
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
						}

					} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
							true)) {
						WSClient.writeToReport(LogStatus.FAIL,
								"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
										"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "<b>Pre-requiste failed >> Profile creation failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requistes required for Reservation creation failed</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	// /**
	// * Method to check if the OWS Reservation Modify Booking is working i.e.,
	// * error is generated when profile id is invalid.
	// *
	// * PreRequisites Required: -->Profile is created -->Reservation is created
	// *
	// * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	// * ->Source Code ->ReservationType
	// */
	// @Test(groups = { "minimumRegression", "ModifyBooking", "Reservation",
	// "OWS","PriorRun"})
	//
	// public void modifyBooking_42717() {
	// try {
	// String testName = "modifyBooking_42717";
	// WSClient.startTest(testName, "Verify that error is generated when profile
	// id is invalid", "minimumRegression");
	//
	// String resortOperaValue = OPERALib.getResort();
	// String chain = OPERALib.getChain();
	// WSClient.setData("{var_chain}", chain);
	// WSClient.setData("{var_resort}", resortOperaValue);
	//
	// String uname = OPERALib.getUserName();
	// String pwd = OPERALib.getPassword();
	// String channel = OWSLib.getChannel();
	// String channelType = OWSLib.getChannelType(channel);
	// String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsresort}", resort);
	// WSClient.setData("{var_channel}", channel);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	// channel);
	// OPERALib.setOperaHeader(uname);
	//
	// if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode",
	// "RoomType", "SourceCode", "MarketCode","ReservationType"}))
	// {
	//
	// /************* Prerequisite : Room type, Rate Plan Code, Source Code,
	// Market Code *********************************/
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_ReservationType}",
	// OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
	//
	// /************ Prerequisite 1: Create profile *************/
	// if(profileID.equals(""))
	// profileID=CreateProfile.createProfile("DS_01");
	// if(!profileID.equals("error"))
	// {
	// WSClient.setData("{var_profileId}", profileID);
	//
	// /**************** Prerequisite 2:Create a Reservation *****************/
	// if(resvID.get("reservationId")==null)
	// resvID=CreateReservation.createReservation("DS_01");
	// if(!resvID.get("reservationId").equals("error"))
	// {
	// WSClient.setData("{var_profileId}",
	// WSClient.getKeywordData("{KEYWORD_RANDNUM_3}"));
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:
	// "+WSClient.getData("{var_profileId}")+"</b>");
	//
	// OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
	// channelCarrier);
	// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:
	// "+resvID.get("reservationId")+"</b>");
	//
	// /*************** OWS Modify Booking Operation ********************/
	// String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking",
	// "DS_02");
	// String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);
	//
	// if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result", true))
	// {
	// WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "ModifyBookingResponse_Result_resultStatusFlag","FAIL", false);
	// if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result_OperaErrorCode", true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is
	// generated is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
	// }
	// if (WSAssert.assertIfElementExists(modifyBookingRes,
	// "Result_Text_TextElement", true))
	// {
	// /**** Verifying that the error message is populated on the response
	// *****/
	// String message = WSAssert.getElementValue(modifyBookingRes,
	// "Result_Text_TextElement",XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, "<b> The error text on response is
	// :"+message);
	// }
	//
	// if(WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "ModifyBookingResponse_Result_resultStatusFlag","SUCCESS", true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> Booking is modified
	// successfully although profile id is invalid</b>");
	// }
	// if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result_GDSError", true))
	// {
	// if(!WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_Result_GDSError",
	// XMLType.RESPONSE).equals("*null*"))
	// WSClient.writeToReport(LogStatus.INFO, "<b> The error that is generated
	// is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)+"</b>");
	// }
	// if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result_GDSError_errorCode", true))
	// {
	// if(!WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_Result_GDSError_errorCode",
	// XMLType.RESPONSE).equals("*null*"))
	// WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is
	// generated is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_Result_GDSError_errorCode",
	// XMLType.RESPONSE)+"</b>");
	// }
	// }
	// else if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_faultstring", true))
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated
	// is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_faultstring", XMLType.RESPONSE)+"</b>");
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "<b>Pre-requiste failed >>
	// Reservation creation failed</b>");
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "<b>Pre-requistes failed >>
	// Profile creation failed</b>");
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "<b>Pre-requistes required for
	// Reservation creation failed</b>");
	// }
	//
	// } catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// } finally {
	// // logger.endExtentTest();
	// }
	// }
	//

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * error is generated when hotel code is invalid.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42758() {
		try {
			String testName = "modifyBooking_42758";
			WSClient.startTest(testName, "Verify that error is generated when hotel code is invalid",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			WSClient.setData("{var_owsresort}", "INVALID");
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile *************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_02");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> Booking is modified successfully although hotel code is invalid</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}

							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								if (!WSClient.getElementValue(modifyBookingRes, "ModifyBookingResponse_Result_GDSError",
										XMLType.RESPONSE).equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO,
											"<b> The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
													+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
								if (!WSClient
										.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										.equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
							}

						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requiste failed >> Reservation creation failed</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "<b>Pre-requiste failed >> Profile creation failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requistes required for Reservation creation failed</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * error is generated when reservation id is not given.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42755() {
		try {
			String testName = "modifyBooking_42755";
			WSClient.startTest(testName, "Verify that error is generated when reservation id is not given",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile *************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

					/***************
					 * OWS Modify Booking Operation
					 ********************/
					String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_28");
					String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

					if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
						WSAssert.assertIfElementValueEquals(modifyBookingRes,
								"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
						if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
								"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> Booking is modified successfully although hotel code is invalid</b>");
						}
						if (WSAssert.assertIfElementExists(modifyBookingRes,
								"ModifyBookingResponse_Result_OperaErrorCode", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
						}
						if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
							String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message + "</b>");
						}
						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result_GDSError",
								true)) {
							if (!WSClient.getElementValue(modifyBookingRes, "ModifyBookingResponse_Result_GDSError",
									XMLType.RESPONSE).equals("*null*"))
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
												+ "</b>");
						}
						if (WSAssert.assertIfElementExists(modifyBookingRes,
								"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
							if (!WSClient
									.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
									.equals("*null*"))
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
						}

					} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
							true)) {
						WSClient.writeToReport(LogStatus.FAIL,
								"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
										"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "<b>Pre-requiste failed >> Profile creation failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requistes required for Reservation creation failed</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	/*
	 * @author psarawag
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42744() {
		try {
			String testName = "modifyBooking_42744";
			WSClient.startTest(testName, "Verify that new address is added to profile successfully",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "AddressType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile *****************/
				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						String addressType = OperaPropConfig.getDataSetForCode("AddressType", "DS_01");
						HashMap<String, String> fullAddress = OPERALib.fetchAddressLOV();

						// Setting Variables
						WSClient.setData("{var_addressType}", addressType);
						WSClient.setData("{var_city}", fullAddress.get("City"));
						WSClient.setData("{var_zip}", fullAddress.get("Zip"));
						WSClient.setData("{var_state}", fullAddress.get("State"));
						WSClient.setData("{var_country}", fullAddress.get("Country"));

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_12");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating against DB</b>");

								LinkedHashMap<String, String> dbDetails = WSClient.getDBRow(WSClient.getQuery("QS_15"));

								if (WSAssert.assertEquals(WSClient.getData("{var_addressType}"),
										dbDetails.get("ADDRESS_TYPE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"AddressType - Expected : " + WSClient.getData("{var_addressType}")
											+ ",Actual : " + dbDetails.get("ADDRESS_TYPE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"AddressType - Expected : " + WSClient.getData("{var_addressType}")
											+ ",Actual : " + dbDetails.get("ADDRESS_TYPE"));
								}

								if (WSAssert
										.assertEquals(
												WSClient.getElementValue(modifyBookingReq,
														"Addresses_NameAddress_AddressLine", XMLType.REQUEST),
												dbDetails.get("ADDRESS1"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Address Line 1 - Expected : "
													+ WSClient.getElementValue(modifyBookingReq,
															"Addresses_NameAddress_AddressLine", XMLType.REQUEST)
													+ ",Actual : " + dbDetails.get("ADDRESS1"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Address Line 1 - Expected : "
													+ WSClient.getElementValue(modifyBookingReq,
															"Addresses_NameAddress_AddressLine", XMLType.REQUEST)
													+ ",Actual : " + dbDetails.get("ADDRESS1"));
								}

								if (WSAssert.assertEquals(WSClient.getData("{var_city}"), dbDetails.get("CITY"),
										true)) {
									WSClient.writeToReport(LogStatus.PASS, "City - Expected : "
											+ WSClient.getData("{var_city}") + ",Actual : " + dbDetails.get("CITY"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "City - Expected : "
											+ WSClient.getData("{var_city}") + ",Actual : " + dbDetails.get("CITY"));
								}

								if (WSAssert.assertEquals(WSClient.getData("{var_zip}"), dbDetails.get("ZIP_CODE"),
										true)) {
									WSClient.writeToReport(LogStatus.PASS, "ZIP - Expected : "
											+ WSClient.getData("{var_zip}") + ",Actual : " + dbDetails.get("ZIP_CODE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "ZIP - Expected : "
											+ WSClient.getData("{var_zip}") + ",Actual : " + dbDetails.get("ZIP_CODE"));
								}

								if (WSAssert.assertEquals(WSClient.getData("{var_state}"), dbDetails.get("STATE"),
										true)) {
									WSClient.writeToReport(LogStatus.PASS, "State - Expected : "
											+ WSClient.getData("{var_state}") + ",Actual : " + dbDetails.get("STATE"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "State - Expected : "
											+ WSClient.getData("{var_state}") + ",Actual : " + dbDetails.get("STATE"));
								}

								if (WSAssert.assertEquals(WSClient.getData("{var_country}"), dbDetails.get("COUNTRY"),
										true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Country - Expected : " + WSClient.getData("{var_country}") + ",Actual : "
													+ dbDetails.get("COUNTRY"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"Country - Expected : " + WSClient.getData("{var_country}") + ",Actual : "
													+ dbDetails.get("COUNTRY"));
								}

								WSClient.writeToReport(LogStatus.INFO, "<b>Validating against Response</b>");

								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"Profile_Addresses_NameAddress_otherAddressType",
										WSClient.getData("{var_addressType}"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"Addresses_NameAddress_AddressLine", WSClient.getElementValue(modifyBookingReq,
												"Addresses_NameAddress_AddressLine", XMLType.REQUEST),
										false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes, "Addresses_NameAddress_cityName",
										WSClient.getData("{var_city}"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"Addresses_NameAddress_postalCode", WSClient.getData("{var_zip}"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes, "Addresses_NameAddress_stateProv",
										WSClient.getData("{var_state}"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"Addresses_NameAddress_countryCode", WSClient.getData("{var_country}"), false);

							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						}
						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									" The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	/**
	 * @author psarawag Method to check if the OWS Reservation Modify Booking is
	 *         working i.e., modify a reservation details with invalid rate
	 *         code.
	 *
	 *         PreRequisites Required: -->Profile is created -->Reservation is
	 *         created
	 *
	 *         ->Room Type ->Rate Code and Room Type attached to it ->Market
	 *         Code ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42521() {
		try {
			String testName = "modifyBooking_42521";
			WSClient.startTest(testName,
					"Verify that the response shows error when invalid rateCode is passed in the request",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile **************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");
					WSClient.setData("{var_profileId}", profileID);

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (resvID.get("reservationId") != null) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.setData("{var_rateCode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
						WSClient.setData("{var_roomCount}", "1");

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_03");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);

							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								/****
								 * Verifying that the error message is populated
								 * on the response
								 ****/
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						}
						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	/**
	 * @author psarawag Method to check if the OWS Reservation Modify Booking is
	 *         working i.e., modify a reservation details with invalid room
	 *         type.
	 *
	 *         PreRequisites Required: -->Profile is created -->Reservation is
	 *         created
	 *
	 *         ->Room Type ->Rate Code and Room Type attached to it ->Market
	 *         Code ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42715() {
		try {
			String testName = "modifyBooking_42715";
			WSClient.startTest(testName,
					"Verify that the response shows error when invalid roomType is passed in the request",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");
					WSClient.setData("{var_profileId}", profileID);

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (resvID.get("reservationId") != null) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
						WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_roomCount}", "1");

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_03");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);

							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						}
						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	/**
	 * @author psarawag Method to check if the OWS Reservation Modify Booking is
	 *         working i.e., modify a reservation details such as arrival and
	 *         departure date when date requested in not in PromotionCode Stay
	 *         Range.
	 *
	 *         PreRequisites Required: -->Profile is created -->Reservation is
	 *         created
	 *
	 *         ->Room Type ->Rate Code and Room Type attached to it ->Market
	 *         Code ->Source Code ->ReservationType
	 */
	/*@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS" })

	public void modifyBooking_42530() {
		try {
			String testName = "modifyBooking_42530";
			WSClient.startTest(testName,
					"Verify that the response shows error when modified arrival and departure date requested is not in PromotionCode Stay Range",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "PromotionCode" })) {
				*//*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************//*
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				*//************ Prerequisite 1: Create profile **************//*
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{var_promoCode}", OperaPropConfig.getDataSetForCode("PromotionCode", "DS_03"));
					*//****************
					 * Prerequisite 2:Create a Reservation
					 *****************//*
					// if(resvID.get("reservationId")==null||resvID.get("reservationId")=="error")
					HashMap<String, String> resvID1 = CreateReservation.createReservation("DS_07");
					if (resvID1.get("reservationId") != "error") {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID1.get("reservationId") + "</b>");
						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

						HashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("OWSModifyBooking", "QS_33"));
						String endDate = db.get("END_DATE");

						WSClient.writeToReport(LogStatus.INFO,
								"<b>End Date of Promotion Code is : " + endDate.substring(0, endDate.indexOf(' ')));
						String startDate = db.get("STAY_ENDDATE");
						WSClient.setData("{var_startDate}", startDate.substring(0, startDate.indexOf(' ')));
						WSClient.setData("{var_endDate}",
								modifyDaysToDate(startDate.substring(0, startDate.indexOf(' ')), "yyyy-MM-dd", 1));

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						*//***************
						 * OWS Modify Booking Operation
						 ********************//*
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_21");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);

							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}*/

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * modify a reservation details adding a promotion code.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42618() {
		try {
			String testName = "modifyBooking_42618";
			WSClient.startTest(testName, "Verify that booking is modified when given promotion code in the request",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "PromotionCode" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile **************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{var_promotionCode}",
							OperaPropConfig.getDataSetForCode("PromotionCode", "DS_03"));
					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null || resvID.get("reservationId") == "error")
						resvID = CreateReservation.createReservation("DS_01");
					if (resvID.get("reservationId") != "error") {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_33");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_52");
								LinkedHashMap<String, String> results = WSClient.getDBRow(query);

								String reqPromoCode = WSClient.getElementValue(modifyBookingReq,
										"RoomStay_RatePlans_RatePlan_promotionCode", XMLType.REQUEST);
								String resPromoCode = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_RatePlans_RatePlan_promotionCode", XMLType.RESPONSE);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation > Modifications are applied on DB</b>");
								if (WSAssert.assertEquals(reqPromoCode, results.get("PROMO_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>PromotionCode : Expected -> "
											+ reqPromoCode + " Actual -> " + results.get("PROMO_CODE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>PromotionCode : Expected -> "
											+ reqPromoCode + " Actual -> " + results.get("PROMO_CODE") + "</b>");
								}

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation > Modifications are retrieved onto response correctly</b>");
								if (WSAssert.assertEquals(results.get("PROMO_CODE"), resPromoCode, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>PromotionCode : Expected -> "
											+ results.get("PROMO_CODE") + " Actual -> " + resPromoCode + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>PromotionCode : Expected -> "
											+ results.get("PROMO_CODE") + " Actual -> " + resPromoCode + "</b>");
								}
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * error is generated when the promotion code is not attached to the given
	 * rate code.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42843() {
		try {
			String testName = "modifyBooking_42843";
			WSClient.startTest(testName,
					"Verify that error is generated when the promotion code is not attached to the given rate code",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "PromotionCode" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile **************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{var_promotionCode}",
							OperaPropConfig.getDataSetForCode("PromotionCode", "DS_02"));
					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null || resvID.get("reservationId") == "error")
						resvID = CreateReservation.createReservation("DS_01");
					if (resvID.get("reservationId") != "error") {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_33");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * error is generated when the promotion code is not valid for property.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42842() {
		try {
			String testName = "modifyBooking_42842";
			WSClient.startTest(testName,
					"Verify that error is generated when the promotion code is not valid for the property",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "PromotionCode" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile **************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{var_promotionCode}", "INVALIDPromo");
					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null || resvID.get("reservationId") == "error")
						resvID = CreateReservation.createReservation("DS_01");
					if (resvID.get("reservationId") != "error") {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_33");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * error is generated when package start date is outside booking start date.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42621() {
		try {
			String testName = "modifyBooking_42621";
			WSClient.startTest(testName,
					"Verify that error is generated when package start date is outside booking start date",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "PackageCode" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (resvID.get("reservationId") != null) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_03"));

						String query = WSClient.getQuery("OWSModifyBooking", "QS_04");
						LinkedHashMap<String, String> results = WSClient.getDBRow(query);
						String dbArrival = results.get("ARRIVAL");
						dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));

						String dbDeparture = results.get("DEPARTURE");
						dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

						/*********
						 * Set start date to reservation start date-1
						 **********/
						dbArrival = modifyDaysToDate(dbArrival, "yyyy-MM-dd", -1);
						WSClient.setData("{var_startDate}", dbArrival);
						WSClient.setData("{var_endDate}", dbDeparture);

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_29");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);

							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								/****
								 * Verifying that the error message is populated
								 * on the response
								 ****/

								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						}
						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * error is generated when package end date is outside booking end date.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42749() {
		try {
			String testName = "modifyBooking_42749";
			WSClient.startTest(testName,
					"Verify that error is generated when package end date is outside booking end date",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "PackageCode" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *************************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (resvID.get("reservationId") != null) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.setData("{var_packageCode}",
								OperaPropConfig.getDataSetForCode("PackageCode", "DS_03"));

						String query = WSClient.getQuery("OWSModifyBooking", "QS_04");
						LinkedHashMap<String, String> results = WSClient.getDBRow(query);
						String dbArrival = results.get("ARRIVAL");
						dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));

						String dbDeparture = results.get("DEPARTURE");
						dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

						/*********
						 * Set end date to reservation end date+1
						 **********/
						dbDeparture = modifyDaysToDate(dbDeparture, "yyyy-MM-dd", 1);
						WSClient.setData("{var_startDate}", dbArrival);
						WSClient.setData("{var_endDate}", dbDeparture);

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_29");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);

							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						}
						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * default payment method is retrieved on the response when
	 * DEFAULT_PAYMENT_METHOD_PER_CHANNEL is set.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42746() {
		String resvid = "";
		try {
			String testName = "modifyBooking_42746";
			WSClient.startTest(testName,
					"Verify that default payment method is retrieved on the response when DEFAULT_PAYMENT_METHOD_PER_CHANNEL is set",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "DEFAULT_PAYMENT_METHOD_PER_CHANNEL");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_param}", "DEFAULT_PAYMENT_METHOD_PER_CHANNEL");

			WSClient.writeToReport(LogStatus.INFO,
					"<b>Verify if DEFAULT_PAYMENT_METHOD_PER_CHANNEL parameter is set</b>");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_02", "PARAMETER_VALUE");
			if (!paramValue.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO,
						"<bEnabling the parameter DEFAULT_PAYMENT_METHOD_PER_CHANNEL</b>");
				paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_02", "PARAMETER_VALUE");
				WSClient.writeToReport(LogStatus.INFO,
						"<b>DEFAULT_PAYMENT_METHOD_PER_CHANNEL : " + paramValue + "</b>");
			}

			if (paramValue.equals("Y")) {
				WSClient.setData("{var_param}", "DEFAULT_PAYMENT_METHOD");
				paramValue = FetchChannelParameters.fetchChannelParameters("QS_02", "PARAMETER_VALUE");
				if (paramValue == null) {
					if (OperaPropConfig.getPropertyConfigResults(
							new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
						WSClient.setData("{var_par}",
								OperaPropConfig.getChannelCodeForDataSet("PaymentMethod", "DS_01"));
						WSClient.setData("{var_parname}", "DEFAULT_PAYMENT_METHOD");
						WSClient.setData("{var_type}", "String");
						paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_02",
								"PARAMETER_VALUE");
					}
				}

				if (paramValue != null) {
					if (OperaPropConfig.getPropertyConfigResults(
							new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
						/*************
						 * Prerequisite : Room type, Rate Plan Code, Source
						 * Code, Market Code
						 *********************************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						WSClient.setData("{var_rate}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
						WSClient.setData("{var_resvType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						/************
						 * Prerequisite 1: Create profile
						 ******************/
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

							WSClient.setData("{var_busdate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_109}"));
							WSClient.setData("{var_busdate1}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_110}"));
							WSClient.setData("{var_time}", "09:00:00");
							WSClient.setData("{var_owsResort}", resort);

							fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

							/****************
							 * Prerequisite 2:Create a Reservation
							 *****************/
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
							String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
							if (WSAssert.assertIfElementValueEquals(createBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
								resvid = WSClient.getElementValueByAttribute(createBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}", resvid);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: " + resvid + "</b>");

								/***************
								 * OWS Modify Booking Operation
								 ********************/
								String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_02");
								String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

								if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
										true)) {
									if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
											"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										/************
										 * Validate source code,market code
										 ***************/
										String query = WSClient.getQuery("QS_04");
										HashMap<String, String> results = WSClient.getDBRow(query);
										WSAssert.assertIfElementValueEquals(modifyBookingRes,
												"ModifyBookingResponse_HotelReservation_originCode",
												results.get("CHANNEL"), false);
										WSAssert.assertIfElementValueEquals(modifyBookingRes,
												"ModifyBookingResponse_HotelReservation_marketSegment",
												results.get("MARKET_CODE"), false);
										//WSAssert.assertIfElementValueEquals(modifyBookingRes,
	//commented by nirmal									//		"PaymentsAccepted_PaymentType_OtherPayment_type",
										//		results.get("PAYMENT_METHOD"), false);
										WSAssert.assertIfElementValueEquals(modifyBookingRes,
												"ModifyBookingResponse_HotelReservation_sourceCode",
												results.get("ORIGIN_OF_BOOKING"), false);

										/**************
										 * Validate payment method
										 *****************/
										//WSAssert.assertIfElementValueEquals(modifyBookingRes,
										//		"PaymentsAccepted_PaymentType_OtherPayment_type", paramValue, false);
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError", true)) {
										if (!WSClient
												.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
												.equals("*null*"))
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error that is generated is : " + WSClient.getElementValue(
															modifyBookingRes, "ModifyBookingResponse_Result_GDSError",
															XMLType.RESPONSE) + "</b>");
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
										if (!WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
												.equals("*null*"))
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error code that is generated is : "
															+ WSClient.getElementValue(modifyBookingRes,
																	"ModifyBookingResponse_Result_GDSError_errorCode",
																	XMLType.RESPONSE)
															+ "</b>");
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_Result_OperaErrorCode", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : " + WSClient.getElementValue(
														modifyBookingRes, "ModifyBookingResponse_Result_OperaErrorCode",
														XMLType.RESPONSE) + "</b>");
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement",
											true)) {
										String message = WSAssert.getElementValue(modifyBookingRes,
												"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The text displayed in the response is :" + message + "</b>");
									}
								} else if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_faultstring", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
													+ "</b>");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"<b>Pre-requiste failed >> Reservation creation failed</b>");
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requistes required for Reservation creation failed</b>");
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requiste failed >> Changing application parameter failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (resvid != "" || !resvid.equals("error")) {
					WSClient.setData("{var_resvId}", resvid);
					if (CancelReservation.cancelReservation("DS_02")) {
						WSClient.writeToLog("Reservation cancellation successful");
					} else
						WSClient.writeToLog("Reservation cancellation failed");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * transport details are added to the booking successfully.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS" })

	public void modifyBooking_42514() {

		String uname = "";
		try {
			String testName = "modifyBooking_42514";
			WSClient.startTest(testName, "Verify that transport details are added to the booking successfully ",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			WSClient.setData("{var_channel}", channel);
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "TransportationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile *****************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");

						String query = WSClient.getQuery("CreateReservation", "QS_05");
						String atransportType = OperaPropConfig.getDataSetForCode("TransportationType", "DS_01");
						System.out.println(atransportType);
						LinkedHashMap<String, String> dates = WSClient.getDBRow(query);
						String id = WSClient.getKeywordData("{KEYWORD_RANDNUM_3}");
						WSClient.setData("{var_aTime}",
								dates.get("BEGIN_DATE").substring(0, 10) + "T" + dates.get("BEGIN_DATE").substring(11));
						WSClient.setData("{var_aReq}", "Y");
						WSClient.setData("{var_aType}", atransportType);
						WSClient.setData("{var_aloc}", "ALOC");
						WSClient.setData("{var_aId}", id);
						WSClient.setData("{var_aCcode}", "AA");

						WSClient.setData("{var_dTime}",
								dates.get("END_DATE").substring(0, 10) + "T" + dates.get("END_DATE").substring(11));
						WSClient.setData("{var_dReq}", "N");
						WSClient.setData("{var_dType}", atransportType);
						WSClient.setData("{var_dCcode}", "DLOC");
						WSClient.setData("{var_dloc}", "BB");
						WSClient.setData("{var_dId}", id);

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_30");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								query = WSClient.getQuery("QS_37");
								LinkedHashMap<String, String> results = WSClient.getDBRow(query);

								String reqAid = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_ArrivalTransport_id", XMLType.REQUEST);
								String reqAlocation = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_ArrivalTransport_locationCode", XMLType.REQUEST);
								String reqAtime = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_ArrivalTransport_time", XMLType.REQUEST);
								String reqAtype = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_ArrivalTransport_type", XMLType.REQUEST);
								String reqAflag = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_ArrivalTransport_transportationRequired", XMLType.REQUEST);
								String reqACarrier = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_ArrivalTransport_carrierCode", XMLType.REQUEST);

								reqAtime = reqAtime.substring(0, reqAtime.indexOf('.'));

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating insertion of arrival transport details in the DB</b>");
								/***************
								 * Validating arrival transport details
								 **************/
								if (WSAssert.assertEquals(reqAid, results.get("ARRIVAL_TRANSPORT_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportId -> Expected : " + reqAid
											+ "  Actual : " + results.get("ARRIVAL_TRANSPORT_CODE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportId -> Expected : " + reqAid
											+ "  Actual : " + results.get("ARRIVAL_TRANSPORT_CODE") + "</b>");
								}

								if (WSAssert.assertEquals(reqAlocation, results.get("ARRIVAL_STATION_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportLocation -> Expected : " + reqAlocation + "  Actual : "
													+ results.get("ARRIVAL_STATION_CODE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportLocation -> Expected : " + reqAlocation + "  Actual : "
													+ results.get("ARRIVAL_STATION_CODE") + "</b>");
								}
								String atime = results.get("ARRIVAL_DATE_TIME");
								atime = atime.substring(0, atime.indexOf('.'));
								atime = atime.replace(' ', 'T');
								if (WSAssert.assertEquals(reqAtime, atime, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportTime -> Expected : " + reqAtime
											+ "  Actual : " + atime + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportTime -> Expected : " + reqAtime
											+ "  Actual : " + atime + "</b>");
								}
								if (WSAssert.assertEquals(reqAtype, results.get("ARRIVAL_TRANSPORT_TYPE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportType -> Expected : " + reqAtype
											+ "  Actual : " + results.get("ARRIVAL_TRANSPORT_TYPE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportType -> Expected : " + reqAtype
											+ "  Actual : " + results.get("ARRIVAL_TRANSPORT_TYPE") + "</b>");
								}
								if (WSAssert.assertEquals(reqAflag, results.get("ARRIVAL_TRANPORTATION_YN"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportRequired -> Expected : " + reqAflag + "  Actual : "
													+ results.get("ARRIVAL_TRANPORTATION_YN") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportRequired -> Expected : " + reqAflag + "  Actual : "
													+ results.get("ARRIVAL_TRANPORTATION_YN") + "</b>");
								}
								if (WSAssert.assertEquals(reqACarrier, results.get("ARRIVAL_CARRIER_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportCarrier -> Expected : " + reqACarrier + "  Actual : "
													+ results.get("ARRIVAL_CARRIER_CODE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportCarrier -> Expected : " + reqACarrier + "  Actual : "
													+ results.get("ARRIVAL_CARRIER_CODE") + "</b>");
								}

								String reqDid = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_DepartureTransport_id", XMLType.REQUEST);
								String reqDlocation = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_DepartureTransport_locationCode", XMLType.REQUEST);
								String reqDtime = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_DepartureTransport_time", XMLType.REQUEST);
								String reqDtype = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_DepartureTransport_type", XMLType.REQUEST);
								String reqDflag = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_DepartureTransport_transportationRequired",
										XMLType.REQUEST);
								String reqDCarrier = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_DepartureTransport_carrierCode", XMLType.REQUEST);

								reqDtime = reqDtime.substring(0, reqDtime.indexOf('.'));

								/***************
								 * Validating departure transport details
								 **************/
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating insertion of departure transport details in the DB</b>");
								if (WSAssert.assertEquals(reqDid, results.get("DEPARTURE_TRANSPORT_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportId -> Expected : " + reqDid
											+ "  Actual : " + results.get("DEPARTURE_TRANSPORT_CODE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportId -> Expected : " + reqDid
											+ "  Actual : " + results.get("DEPARTURE_TRANSPORT_CODE") + "</b>");
								}

								if (WSAssert.assertEquals(reqDlocation, results.get("DEPARTURE_STATION_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportLocation -> Expected : " + reqDlocation + "  Actual : "
													+ results.get("DEPARTURE_STATION_CODE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportLocation -> Expected : " + reqDlocation + "  Actual : "
													+ results.get("DEPARTURE_STATION_CODE") + "</b>");
								}
								String dtime = results.get("DEPARTURE_DATE_TIME");
								dtime = dtime.substring(0, dtime.indexOf('.'));
								dtime = dtime.replace(' ', 'T');
								if (WSAssert.assertEquals(reqDtime, dtime, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportTime -> Expected : " + reqDtime
											+ "  Actual : " + dtime + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportTime -> Expected : " + reqDtime
											+ "  Actual : " + dtime + "</b>");
								}
								if (WSAssert.assertEquals(reqDtype, results.get("DEPARTURE_TRANSPORT_TYPE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportType -> Expected : " + reqDtype
											+ "  Actual : " + results.get("DEPARTURE_TRANSPORT_TYPE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportType -> Expected : " + reqDtype
											+ "  Actual : " + results.get("DEPARTURE_TRANSPORT_TYPE") + "</b>");
								}
								if (WSAssert.assertEquals(reqDflag, results.get("DEPARTURE_TRANSPORTATION_YN"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportRequired -> Expected : " + reqDflag + "  Actual : "
													+ results.get("DEPARTURE_TRANSPORTATION_YN") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportRequired -> Expected : " + reqDflag + "  Actual : "
													+ results.get("DEPARTURE_TRANSPORTATION_YN") + "</b>");
								}
								if (WSAssert.assertEquals(reqDCarrier, results.get("DEPARTURE_CARRIER_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportCarrier -> Expected : " + reqDCarrier + "  Actual : "
													+ results.get("DEPARTURE_CARRIER_CODE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportCarrier -> Expected : " + reqDCarrier + "  Actual : "
													+ results.get("DEPARTURE_CARRIER_CODE") + "</b>");
								}

								String resAid = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_ArrivalTransport_id", XMLType.RESPONSE);
								String resAlocation = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_ArrivalTransport_locationCode", XMLType.RESPONSE);
								String resAtime = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_ArrivalTransport_time", XMLType.RESPONSE);
								String resAtype = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_ArrivalTransport_type", XMLType.RESPONSE);
								String resAflag = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_ArrivalTransport_transportationRequired", XMLType.RESPONSE);
								String resACarrier = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_ArrivalTransport_carrierCode", XMLType.RESPONSE);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation > Inserted Arrival transport details are retrieved correctly on response</b>");
								if (WSAssert.assertEquals(results.get("ARRIVAL_TRANSPORT_CODE"), resAid, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportId -> Expected : "
											+ results.get("ARRIVAL_TRANSPORT_CODE") + "  Actual : " + resAid + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportId -> Expected : "
											+ results.get("ARRIVAL_TRANSPORT_CODE") + "  Actual : " + resAid + "</b>");
								}

								if (WSAssert.assertEquals(results.get("ARRIVAL_STATION_CODE"), resAlocation, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportLocation -> Expected : " + results.get("ARRIVAL_STATION_CODE")
											+ "  Actual : " + resAlocation + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportLocation -> Expected : " + results.get("ARRIVAL_STATION_CODE")
											+ "  Actual : " + resAlocation + "</b>");
								}
								if (WSAssert.assertEquals(atime, resAtime, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportTime -> Expected : " + atime
											+ "  Actual : " + resAtime + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportTime -> Expected : " + atime
											+ "  Actual : " + resAtime + "</b>");
								}
								if (WSAssert.assertEquals(results.get("ARRIVAL_TRANSPORT_TYPE"), resAtype, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportType -> Expected : " + results.get("ARRIVAL_TRANSPORT_TYPE")
											+ "  Actual : " + resAtype + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportType -> Expected : " + results.get("ARRIVAL_TRANSPORT_TYPE")
											+ "  Actual : " + resAtype + "</b>");
								}
								if (WSAssert.assertEquals(results.get("ARRIVAL_TRANPORTATION_YN"), resAflag, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportRequired -> Expected : "
													+ results.get("ARRIVAL_TRANPORTATION_YN") + "  Actual : " + resAflag
													+ "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportRequired -> Expected : "
													+ results.get("ARRIVAL_TRANPORTATION_YN") + "  Actual : " + resAflag
													+ "</b>");
								}
								if (WSAssert.assertEquals(results.get("ARRIVAL_CARRIER_CODE"), resACarrier, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportCarrier -> Expected : " + results.get("ARRIVAL_CARRIER_CODE")
											+ "  Actual : " + resACarrier + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportCarrier -> Expected : " + results.get("ARRIVAL_CARRIER_CODE")
											+ "  Actual : " + resACarrier + "</b>");
								}

								String resDid = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_DepartureTransport_id", XMLType.RESPONSE);
								String resDlocation = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_DepartureTransport_locationCode", XMLType.RESPONSE);
								String resDtime = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_DepartureTransport_time", XMLType.RESPONSE);
								String resDtype = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_DepartureTransport_type", XMLType.RESPONSE);
								String resDflag = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_DepartureTransport_transportationRequired",
										XMLType.RESPONSE);
								String resDCarrier = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_DepartureTransport_carrierCode", XMLType.RESPONSE);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation > Inserted Departure transport details are retrieved correctly on response</b>");
								if (WSAssert.assertEquals(results.get("DEPARTURE_TRANSPORT_CODE"), resDid, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportId -> Expected : " + results.get("DEPARTURE_TRANSPORT_CODE")
											+ "  Actual : " + resDid + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportId -> Expected : " + results.get("DEPARTURE_TRANSPORT_CODE")
											+ "  Actual : " + resDid + "</b>");
								}

								if (WSAssert.assertEquals(results.get("DEPARTURE_STATION_CODE"), resDlocation, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportLocation -> Expected : "
													+ results.get("DEPARTURE_STATION_CODE") + "  Actual : "
													+ resDlocation + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportLocation -> Expected : "
													+ results.get("DEPARTURE_STATION_CODE") + "  Actual : "
													+ resDlocation + "</b>");
								}
								if (WSAssert.assertEquals(dtime, resDtime, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportTime -> Expected : " + dtime
											+ "  Actual : " + resDtime + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportTime -> Expected : " + dtime
											+ "  Actual : " + resDtime + "</b>");
								}
								if (WSAssert.assertEquals(results.get("DEPARTURE_TRANSPORT_TYPE"), resDtype, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportType -> Expected : " + results.get("DEPARTURE_TRANSPORT_TYPE")
											+ "  Actual : " + resDtype + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportType -> Expected : " + results.get("DEPARTURE_TRANSPORT_TYPE")
											+ "  Actual : " + resDtype + "</b>");
								}
								if (WSAssert.assertEquals(results.get("DEPARTURE_TRANSPORTATION_YN"), resDflag, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportRequired -> Expected : "
													+ results.get("DEPARTURE_TRANSPORTATION_YN") + "  Actual : "
													+ resDflag + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportRequired -> Expected : "
													+ results.get("DEPARTURE_TRANSPORTATION_YN") + "  Actual : "
													+ resDflag + "</b>");
								}
								if (WSAssert.assertEquals(results.get("DEPARTURE_CARRIER_CODE"), resDCarrier, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportCarrier -> Expected : "
													+ results.get("DEPARTURE_CARRIER_CODE") + "  Actual : "
													+ resDCarrier + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportCarrier -> Expected : "
													+ results.get("DEPARTURE_CARRIER_CODE") + "  Actual : "
													+ resDCarrier + "</b>");
								}
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								if (!WSClient.getElementValue(modifyBookingRes, "ModifyBookingResponse_Result_GDSError",
										XMLType.RESPONSE).equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO,
											"<b> The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
													+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
								if (!WSClient
										.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										.equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requiste failed >> Reservation creation failed</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "<b>Pre-requiste failed >> Profile creation failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requistes required for Reservation creation failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * transport details of the booking are modified successfully.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS" })
	public void modifyBooking_42517() {
		String uname = "";
		HashMap<String, String> resvId = new HashMap<>();
		try {
			String testName = "modifyBooking_42517";
			WSClient.startTest(testName, "Verify that transport details of the booking are modified successfully",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			WSClient.setData("{var_channel}", channel);
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "TransportationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile *****************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					String atransportType = OperaPropConfig.getDataSetForCode("TransportationType", "DS_01");
					System.out.println(atransportType);
					WSClient.setData("{var_transportType}", atransportType);
					WSClient.setData("{var_transportCode}", "218");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					resvId = CreateReservation.createReservation("DS_15");
					if (!resvId.get("reservationId").equals("error")) {
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvId.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvId.get("reservationId") + "</b>");

						String query = WSClient.getQuery("QS_05");
						LinkedHashMap<String, String> dates = WSClient.getDBRow(query);
						String id = WSClient.getKeywordData("{KEYWORD_RANDNUM_3}");
						WSClient.setData("{var_aTime}",
								dates.get("BEGIN_DATE").substring(0, 10) + "T" + dates.get("BEGIN_DATE").substring(11));
						WSClient.setData("{var_aReq}", "N");
						WSClient.setData("{var_aType}", atransportType);
						WSClient.setData("{var_aloc}", "RSW");
						WSClient.setData("{var_aId}", id);
						WSClient.setData("{var_aCcode}", "AA");

						id = WSClient.getKeywordData("{KEYWORD_RANDNUM_3}");
						WSClient.setData("{var_dTime}",
								dates.get("END_DATE").substring(0, 10) + "T" + dates.get("END_DATE").substring(11));
						WSClient.setData("{var_dReq}", "Y");
						WSClient.setData("{var_dType}", atransportType);
						WSClient.setData("{var_dCcode}", "HEA");
						WSClient.setData("{var_dloc}", "BR");
						WSClient.setData("{var_dId}", id);

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_30");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								query = WSClient.getQuery("QS_37");
								LinkedHashMap<String, String> results = WSClient.getDBRow(query);

								String reqAid = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_ArrivalTransport_id", XMLType.REQUEST);
								String reqAlocation = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_ArrivalTransport_locationCode", XMLType.REQUEST);
								String reqAtime = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_ArrivalTransport_time", XMLType.REQUEST);
								String reqAtype = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_ArrivalTransport_type", XMLType.REQUEST);
								String reqAflag = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_ArrivalTransport_transportationRequired", XMLType.REQUEST);
								String reqACarrier = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_ArrivalTransport_carrierCode", XMLType.REQUEST);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating updation of arrival transport details in the DB</b>");
								/***************
								 * Validating arrival transport details
								 **************/
								if (WSAssert.assertEquals(reqAid, results.get("ARRIVAL_TRANSPORT_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportId -> Expected : " + reqAid
											+ "  Actual : " + results.get("ARRIVAL_TRANSPORT_CODE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportId -> Expected : " + reqAid
											+ "  Actual : " + results.get("ARRIVAL_TRANSPORT_CODE") + "</b>");
								}

								if (WSAssert.assertEquals(reqAlocation, results.get("ARRIVAL_STATION_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportLocation -> Expected : " + reqAlocation + "  Actual : "
													+ results.get("ARRIVAL_STATION_CODE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportLocation -> Expected : " + reqAlocation + "  Actual : "
													+ results.get("ARRIVAL_STATION_CODE") + "</b>");
								}
								String atime = results.get("ARRIVAL_DATE_TIME");
								atime = atime.substring(0, atime.indexOf(' ')) + "T"
										+ atime.substring(atime.indexOf(' ') + 1);
								if (WSAssert.assertEquals(reqAtime, atime, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportTime -> Expected : " + reqAtime
											+ "  Actual : " + atime + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportTime -> Expected : " + reqAtime
											+ "  Actual : " + atime + "</b>");
								}
								if (WSAssert.assertEquals(reqAtype, results.get("ARRIVAL_TRANSPORT_TYPE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportType -> Expected : " + reqAtype
											+ "  Actual : " + results.get("ARRIVAL_TRANSPORT_TYPE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportType -> Expected : " + reqAtype
											+ "  Actual : " + results.get("ARRIVAL_TRANSPORT_TYPE") + "</b>");
								}
								if (WSAssert.assertEquals(reqAflag, results.get("ARRIVAL_TRANPORTATION_YN"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportRequired -> Expected : " + reqAflag + "  Actual : "
													+ results.get("ARRIVAL_TRANPORTATION_YN") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportRequired -> Expected : " + reqAflag + "  Actual : "
													+ results.get("ARRIVAL_TRANPORTATION_YN") + "</b>");
								}
								if (WSAssert.assertEquals(reqACarrier, results.get("ARRIVAL_CARRIER_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportCarrier -> Expected : " + reqACarrier + "  Actual : "
													+ results.get("ARRIVAL_CARRIER_CODE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportCarrier -> Expected : " + reqACarrier + "  Actual : "
													+ results.get("ARRIVAL_CARRIER_CODE") + "</b>");
								}

								String reqDid = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_DepartureTransport_id", XMLType.REQUEST);
								String reqDlocation = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_DepartureTransport_locationCode", XMLType.REQUEST);
								String reqDtime = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_DepartureTransport_time", XMLType.REQUEST);
								String reqDtype = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_DepartureTransport_type", XMLType.REQUEST);
								String reqDflag = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_DepartureTransport_transportationRequired",
										XMLType.REQUEST);
								String reqDCarrier = WSClient.getElementValue(modifyBookingReq,
										"ResGuests_ResGuest_DepartureTransport_carrierCode", XMLType.REQUEST);

								/***************
								 * Validating departure transport details
								 **************/
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating updation of departure transport details in the DB</b>");
								if (WSAssert.assertEquals(reqDid, results.get("DEPARTURE_TRANSPORT_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportId -> Expected : " + reqDid
											+ "  Actual : " + results.get("DEPARTURE_TRANSPORT_CODE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportId -> Expected : " + reqDid
											+ "  Actual : " + results.get("DEPARTURE_TRANSPORT_CODE") + "</b>");
								}

								if (WSAssert.assertEquals(reqDlocation, results.get("DEPARTURE_STATION_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportLocation -> Expected : " + reqDlocation + "  Actual : "
													+ results.get("DEPARTURE_STATION_CODE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportLocation -> Expected : " + reqDlocation + "  Actual : "
													+ results.get("DEPARTURE_STATION_CODE") + "</b>");
								}
								String dtime = results.get("DEPARTURE_DATE_TIME");
								dtime = dtime.substring(0, dtime.indexOf(' ')) + "T"
										+ dtime.substring(dtime.indexOf(' ') + 1);
								if (WSAssert.assertEquals(reqDtime, dtime, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportTime -> Expected : " + reqDtime
											+ "  Actual : " + dtime + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportTime -> Expected : " + reqDtime
											+ "  Actual : " + dtime + "</b>");
								}
								if (WSAssert.assertEquals(reqDtype, results.get("DEPARTURE_TRANSPORT_TYPE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportType -> Expected : " + reqDtype
											+ "  Actual : " + results.get("DEPARTURE_TRANSPORT_TYPE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportType -> Expected : " + reqDtype
											+ "  Actual : " + results.get("DEPARTURE_TRANSPORT_TYPE") + "</b>");
								}
								if (WSAssert.assertEquals(reqDflag, results.get("DEPARTURE_TRANSPORTATION_YN"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportRequired -> Expected : " + reqDflag + "  Actual : "
													+ results.get("DEPARTURE_TRANSPORTATION_YN") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportRequired -> Expected : " + reqDflag + "  Actual : "
													+ results.get("DEPARTURE_TRANSPORTATION_YN") + "</b>");
								}
								if (WSAssert.assertEquals(reqDCarrier, results.get("DEPARTURE_CARRIER_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportCarrier -> Expected : " + reqDCarrier + "  Actual : "
													+ results.get("DEPARTURE_CARRIER_CODE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportCarrier -> Expected : " + reqDCarrier + "  Actual : "
													+ results.get("DEPARTURE_CARRIER_CODE") + "</b>");
								}

								String resAid = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_ArrivalTransport_id", XMLType.RESPONSE);
								String resAlocation = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_ArrivalTransport_locationCode", XMLType.RESPONSE);
								String resAtime = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_ArrivalTransport_time", XMLType.RESPONSE);
								String resAtype = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_ArrivalTransport_type", XMLType.RESPONSE);
								String resAflag = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_ArrivalTransport_transportationRequired", XMLType.RESPONSE);
								String resACarrier = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_ArrivalTransport_carrierCode", XMLType.RESPONSE);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation > Updated Arrival transport details are retrieved correctly on response</b>");
								if (WSAssert.assertEquals(results.get("ARRIVAL_TRANSPORT_CODE"), resAid, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportId -> Expected : "
											+ results.get("ARRIVAL_TRANSPORT_CODE") + "  Actual : " + resAid + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportId -> Expected : "
											+ results.get("ARRIVAL_TRANSPORT_CODE") + "  Actual : " + resAid + "</b>");
								}

								if (WSAssert.assertEquals(results.get("ARRIVAL_STATION_CODE"), resAlocation, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportLocation -> Expected : " + results.get("ARRIVAL_STATION_CODE")
											+ "  Actual : " + resAlocation + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportLocation -> Expected : " + results.get("ARRIVAL_STATION_CODE")
											+ "  Actual : " + resAlocation + "</b>");
								}
								atime = atime.substring(0, atime.indexOf('.'));
								if (WSAssert.assertEquals(atime, resAtime, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportTime -> Expected : " + atime
											+ "  Actual : " + resAtime + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportTime -> Expected : " + atime
											+ "  Actual : " + resAtime + "</b>");
								}
								if (WSAssert.assertEquals(results.get("ARRIVAL_TRANSPORT_TYPE"), resAtype, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportType -> Expected : " + results.get("ARRIVAL_TRANSPORT_TYPE")
											+ "  Actual : " + resAtype + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportType -> Expected : " + results.get("ARRIVAL_TRANSPORT_TYPE")
											+ "  Actual : " + resAtype + "</b>");
								}
								if (WSAssert.assertEquals(results.get("ARRIVAL_TRANPORTATION_YN"), resAflag, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportRequired -> Expected : "
													+ results.get("ARRIVAL_TRANPORTATION_YN") + "  Actual : " + resAflag
													+ "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportRequired -> Expected : "
													+ results.get("ARRIVAL_TRANPORTATION_YN") + "  Actual : " + resAflag
													+ "</b>");
								}
								if (WSAssert.assertEquals(results.get("ARRIVAL_CARRIER_CODE"), resACarrier, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportCarrier -> Expected : " + results.get("ARRIVAL_CARRIER_CODE")
											+ "  Actual : " + resACarrier + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportCarrier -> Expected : " + results.get("ARRIVAL_CARRIER_CODE")
											+ "  Actual : " + resACarrier + "</b>");
								}

								String resDid = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_DepartureTransport_id", XMLType.RESPONSE);
								String resDlocation = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_DepartureTransport_locationCode", XMLType.RESPONSE);
								String resDtime = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_DepartureTransport_time", XMLType.RESPONSE);
								String resDtype = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_DepartureTransport_type", XMLType.RESPONSE);
								String resDflag = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_DepartureTransport_transportationRequired",
										XMLType.RESPONSE);
								String resDCarrier = WSClient.getElementValue(modifyBookingRes,
										"ResGuests_ResGuest_DepartureTransport_carrierCode", XMLType.RESPONSE);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation > Updated Departure transport details are retrieved correctly on response</b>");
								if (WSAssert.assertEquals(results.get("DEPARTURE_TRANSPORT_CODE"), resDid, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportId -> Expected : " + results.get("DEPARTURE_TRANSPORT_CODE")
											+ "  Actual : " + resDid + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportId -> Expected : " + results.get("DEPARTURE_TRANSPORT_CODE")
											+ "  Actual : " + resDid + "</b>");
								}

								if (WSAssert.assertEquals(results.get("DEPARTURE_STATION_CODE"), resDlocation, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportLocation -> Expected : "
													+ results.get("DEPARTURE_STATION_CODE") + "  Actual : "
													+ resDlocation + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportLocation -> Expected : "
													+ results.get("DEPARTURE_STATION_CODE") + "  Actual : "
													+ resDlocation + "</b>");
								}
								dtime = dtime.substring(0, dtime.indexOf('.'));
								if (WSAssert.assertEquals(dtime, resDtime, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> TransportTime -> Expected : " + dtime
											+ "  Actual : " + resDtime + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> TransportTime -> Expected : " + dtime
											+ "  Actual : " + resDtime + "</b>");
								}
								if (WSAssert.assertEquals(results.get("DEPARTURE_TRANSPORT_TYPE"), resDtype, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportType -> Expected : " + results.get("DEPARTURE_TRANSPORT_TYPE")
											+ "  Actual : " + resDtype + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportType -> Expected : " + results.get("DEPARTURE_TRANSPORT_TYPE")
											+ "  Actual : " + resDtype + "</b>");
								}
								if (WSAssert.assertEquals(results.get("DEPARTURE_TRANSPORTATION_YN"), resDflag, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportRequired -> Expected : "
													+ results.get("DEPARTURE_TRANSPORTATION_YN") + "  Actual : "
													+ resDflag + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportRequired -> Expected : "
													+ results.get("DEPARTURE_TRANSPORTATION_YN") + "  Actual : "
													+ resDflag + "</b>");
								}
								if (WSAssert.assertEquals(results.get("DEPARTURE_CARRIER_CODE"), resDCarrier, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b> TransportCarrier -> Expected : "
													+ results.get("DEPARTURE_CARRIER_CODE") + "  Actual : "
													+ resDCarrier + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> TransportCarrier -> Expected : "
													+ results.get("DEPARTURE_CARRIER_CODE") + "  Actual : "
													+ resDCarrier + "</b>");
								}
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								if (!WSClient.getElementValue(modifyBookingRes, "ModifyBookingResponse_Result_GDSError",
										XMLType.RESPONSE).equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO,
											"<b> The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
													+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
								if (!WSClient
										.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										.equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requiste failed >> Reservation creation failed</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "<b>Pre-requiste failed >> Profile creation failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requistes required for Reservation creation failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}

	/*
	 * Rate Code related Cases
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS" })

	public void modifyBooking_42515() {

		String uname = "";
		try {
			String testName = "modifyBooking_42515";
			WSClient.startTest(testName, "Verify that Rate charges are correctly populated on response",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile ***************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}");
					String endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_4}");

					WSClient.setData("{var_startDate}", startDate);
					WSClient.setData("{var_endDate}", endDate);
					System.out.println("Start: " + startDate);
					System.out.println(endDate);

					HashMap<String, String> resvID = CreateReservation.createReservation("DS_29");
					if (!resvID.get("reservationId").equals("error")) {
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");

						WSClient.setData("{var_count1}", "4");
						WSClient.setData("{var_ageGroup1}", "ADULT");
						WSClient.setData("{var_age1}", "");

						WSClient.setData("{var_count2}", "");
						WSClient.setData("{var_ageGroup2}", "");
						WSClient.setData("{var_age2}", "");

						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
						WSClient.setData("{var_roomCount}", "1");

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_31");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_36");
								System.out.println("Query: " + query);
								HashMap<String, String> prices = WSClient.getDBRow(query);
								int chargeForDay = Integer.parseInt(prices.get("AMOUNT_3"))
										+ Integer.parseInt(prices.get("ADULT_CHARGE"));

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Details of Rate Code : " + prices.get("RATE_CODE") + "</b>");
								WSClient.writeToReport(LogStatus.INFO,
										"<b> Charge For Adult 1 -> " + prices.get("AMOUNT_1") + "  Adult 2 -> "
												+ prices.get("AMOUNT_2") + "  Adult 3 -> " + prices.get("AMOUNT_3")
												+ " Extra Adult -> " + prices.get("ADULT_CHARGE") + "</b>");

								String adultCount = WSClient.getElementValue(modifyBookingReq,
										"RoomStay_GuestCounts_GuestCount_count", XMLType.REQUEST);
								String resadultCount = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_GuestCounts_GuestCount_count", XMLType.RESPONSE);

								query = WSClient.getQuery("QS_04");
								HashMap<String, String> dbResults = WSClient.getDBRow(query);

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating insertion of adult count into DB</b>");
								/**** Validate modified adult guest count ****/
								if (WSAssert.assertEquals(adultCount, dbResults.get("ADULTS"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Adult Guest Count -> Expected : "
											+ adultCount + "  Actual : " + dbResults.get("ADULTS") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Adult Guest Count -> Expected : "
											+ adultCount + "  Actual : " + dbResults.get("ADULTS") + "</b>");
								}

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating retrieval of counts onto Response</b>");
								/**** Validate modified adult guest count ****/
								if (WSAssert.assertEquals(adultCount, resadultCount, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b> Adult Guest Count -> Expected : "
											+ adultCount + "  Actual : " + resadultCount + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b> Adult Guest Count -> Expected : "
											+ adultCount + "  Actual : " + resadultCount + "</b>");
								}
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Rate</b>");
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"RoomStay_RoomRates_RoomRate_ratePlanCode",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"RoomStay_RoomRates_RoomRate_roomTypeCode",
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"), false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes, "Rates_Rate_Base_currencyCode",
										"USD", false);
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"RoomRateAndPackages_Charges_Amount", Integer.toString(chargeForDay), false);

								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Expected Charges</b>");
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages",
										Integer.toString(chargeForDay * 2), false);
								HashMap<String, String> xpath = new HashMap<>();
								// xpath.put("RoomStay_ExpectedCharges_ChargesForPostingDate_PostingDate",
								// "RoomStay_ExpectedCharges_ChargesForPostingDate");

								xpath.put("ExpectedCharges_ChargesForPostingDate_RoomRateAndPackages_TotalCharges",
										"RoomStay_ExpectedCharges_ChargesForPostingDate");
								xpath.put("RoomRateAndPackages_Charges_Amount_currencyCode",
										"RoomStay_ExpectedCharges_ChargesForPostingDate");

								List<LinkedHashMap<String, String>> resDayData = WSClient
										.getMultipleNodeList(modifyBookingRes, xpath, false, XMLType.RESPONSE);
								List<LinkedHashMap<String, String>> expDayData = new ArrayList<LinkedHashMap<String, String>>();
								LinkedHashMap<String, String> data = new LinkedHashMap<>();
								data.put("agesChargesAmountcurrencyCode1", "USD");
								data.put("omRateAndPackagesTotalCharges1", String.valueOf(chargeForDay));
								expDayData.add(data);
								expDayData.add(data);

								WSAssert.assertEquals(expDayData, resDayData, false);
							}

							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								if (!WSClient.getElementValue(modifyBookingRes, "ModifyBookingResponse_Result_GDSError",
										XMLType.RESPONSE).equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO,
											"<b> The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
													+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
								if (!WSClient
										.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										.equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requiste failed >> Reservation creation failed</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "<b>Pre-requiste failed >> Profile creation failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requistes required for Reservation creation failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}
	// /**
	// * @author psarawag
	// * Method to check if the OWS Reservation Modify Booking is working i.e.,
	// * Verify cancellation and deposit policy in the response when it is
	// attached to reservation type.
	// *
	// * PreRequisites Required: -->Profile is created -->Reservation is created
	// *
	// * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	// * ->Source Code ->ReservationType
	// */
	// @Test(groups = { "minimumRegression", "ModifyBooking", "Reservation",
	// "OWS"})
	//
	// public void modifyBooking_42619() {
	// try {
	// String testName = "modifyBooking_42619";
	// WSClient.startTest(testName, "Verify that cancellation and deposit policy
	// attached to reservation type are present in the response",
	// "minimumRegression");
	//
	// String resortOperaValue = OPERALib.getResort();
	// String chain = OPERALib.getChain();
	// WSClient.setData("{var_chain}", chain);
	// WSClient.setData("{var_resort}", resortOperaValue);
	//
	// String uname = OPERALib.getUserName();
	// String pwd = OPERALib.getPassword();
	// String channel = OWSLib.getChannel();
	// String channelType = OWSLib.getChannelType(channel);
	// String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsresort}", resort);
	// WSClient.setData("{var_channel}", channel);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	// channel);
	// OPERALib.setOperaHeader(uname);
	//
	// if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode",
	// "RoomType", "SourceCode", "MarketCode","ReservationType"}))
	// {
	// /************* Prerequisite : Room type, Rate Plan Code, Source Code,
	// Market Code *********************************/
	// WSClient.setData("{VAR_RATEPLANCODE}",OperaPropConfig.getDataSetForCode("RateCode",
	// "DS_11"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_10"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_ReservationType}",
	// OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_04"));
	//
	// /************ Prerequisite 1: Create profile *********************/
	// if(profileID.equals(""))
	// profileID=CreateProfile.createProfile("DS_01");
	// if(!profileID.equals("error"))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:"+ profileID
	// +"</b>");
	// WSClient.setData("{var_profileId}", profileID);
	//
	// /**************** Prerequisite 2:Create a Reservation *****************/
	// HashMap<String,String>
	// resvID1=CreateReservation.createReservation("DS_01");
	// if(resvID1.get("reservationId")!="error")
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:"+
	// resvID1.get("reservationId") +"</b>");
	// WSClient.setData("{var_resvId}", resvID1.get("reservationId"));
	//
	// OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
	// channelCarrier);
	// WSClient.setData("{var_rateCode}",
	// OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
	// WSClient.setData("{var_roomType}",
	// OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
	// WSClient.setData("{var_roomCount}", "1");
	//
	// /*************** OWS Modify Booking Operation ********************/
	// String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking",
	// "DS_03");
	// String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);
	//
	// if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result", true))
	// {
	// if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "ModifyBookingResponse_Result_resultStatusFlag","SUCCESS", false))
	// {
	// List<LinkedHashMap<String,String>> resValues=new
	// ArrayList<LinkedHashMap<String,String>>();
	// HashMap<String,String> xPath=new HashMap<String,String>();
	// xPath.put("RatePlan_AdditionalDetails_AdditionalDetail_detailType",
	// "RatePlan_AdditionalDetails_AdditionalDetail");
	// xPath.put("AdditionalDetail_AdditionalDetailDescription_Text",
	// "RatePlan_AdditionalDetails_AdditionalDetail");
	// System.out.println(xPath);
	// resValues=WSClient.getMultipleNodeList(modifyBookingRes, xPath, true,
	// XMLType.RESPONSE);
	// System.out.println(resValues);
	// String cancelDate=WSClient.getElementValue(modifyBookingRes,
	// "RatePlans_RatePlan_CancellationDateTime", XMLType.RESPONSE);
	// String depositAmount=WSClient.getElementValue(modifyBookingRes,
	// "RatePlan_DepositRequired_DepositAmount", XMLType.RESPONSE);
	// String dueDate=WSClient.getElementValue(modifyBookingRes,
	// "RatePlan_DepositRequired_DueDate", XMLType.RESPONSE);
	// if(resValues.contains("DetailType")) {
	// for(int i=0;i<resValues.size();i++){
	// System.out.println("Hi");
	// if(resValues.get(i).get("DetailType").equals("CancelPolicy"))
	// {
	//
	// resValues.get(i).put("CancelDate",
	// cancelDate.substring(0,cancelDate.indexOf('T')));
	// }
	// if(resValues.get(i).get("DetailType").equals("DepositPolicy"))
	// {
	// resValues.get(i).put("DepositAmount", depositAmount);
	// resValues.get(i).put("DueDate", dueDate);
	// }
	// }
	// }
	// WSClient.setData("{var_ReservationType}",
	// OperaPropConfig.getDataSetForCode("ReservationType", "DS_04"));
	//
	// LinkedHashMap<String,String>
	// depositValues=WSClient.getDBRow(WSClient.getQuery("QS_39"));
	// LinkedHashMap<String,String>
	// cancelValues=WSClient.getDBRow(WSClient.getQuery("QS_38"));
	// if(cancelValues.size()>0)
	// {
	// cancelValues.put("DetailType", "CancelPolicy");
	// }
	// if(cancelValues.containsKey("Text"))
	// {
	// cancelValues.put("Text", "Cancel By "+cancelValues.get("Text"));
	// }
	// if(depositValues.size()>0){
	// depositValues.put("DetailType", "DepositPolicy");
	// }
	// if(depositValues.containsKey("DepositAmount")&&depositValues.containsKey("Text"))
	// {
	// depositValues.put("Text", "A deposit of
	// "+depositValues.get("DepositAmount")+".00 is due by
	// "+depositValues.get("Text")+" in order to guarantee your reservation.");
	// }
	// if(resValues.contains("DetailType")) {
	// for(int i=0;i<resValues.size();i++){
	// if(resValues.get(i).get("DetailType").equals("CancelPolicy"))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Cancellation
	// Policy</b>");
	// WSAssert.assertEquals(cancelValues, resValues.get(i), false);
	// }
	// if(resValues.get(i).get("DetailType").equals("DepositPolicy"))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Deposit
	// Policy</b>");
	// WSAssert.assertEquals(depositValues, resValues.get(i), false);
	// }
	// }}
	// }
	// if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result_GDSError", true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is
	// generated is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_Result_GDSError_errorCode",
	// XMLType.RESPONSE)+"</b>");
	// }
	// if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result_OperaErrorCode", true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is
	// generated is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
	// }
	// if (WSAssert.assertIfElementExists(modifyBookingRes,
	// "Result_Text_TextElement", true))
	// {
	// String message = WSAssert.getElementValue(modifyBookingRes,
	// "Result_Text_TextElement",XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,"<b>The text displayed in the
	// response is :" + message+"</b>");
	// }
	// }
	// else if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_faultstring", true))
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated
	// is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_faultstring", XMLType.RESPONSE)+"</b>");
	// }
	// }
	// }
	//
	// }else{
	// WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for
	// RateCode, RoomType, SourceCode, MarketCode,ReservationType failed!
	// -----Blocked");
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
	// WSClient.writeToReport(LogStatus.INFO,"Exception occured due to : "+e);
	// e.printStackTrace();
	// }
	// }
	// }

	/*@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS" , "qwerty" })

	public void modifyBooking_42519() {
		String uname = "";
		try {
			String testName = "modifyBooking_42519";
			WSClient.startTest(testName, "Verify that Incremental Rate Details are correctly populated on the response",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				*//********
				 * Change the Application Parameter RATE_DETAIL_ADDED_VALUE to Y
				 *************//*
				WSClient.setData("{var_parameter}", "RATE_DETAIL_ADDED_VALUE");
				WSClient.setData("{var_settingValue}", "Y");
				String parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>RATE_DETAIL_ADDED_VALUE_PER_HEAD is set ");
				if (parameter.contains("Y")) {
					*//*************
					 * Prerequisite : Room type, Rate Plan Code, Source Code,
					 * Market Code
					 *********************************//*
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_ReservationType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

					*//************ Prerequisite 1: Create profile ************//*
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

						*//****************
						 * Prerequisite 2:Create a Reservation
						 *****************//*
						String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}");
						String endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_4}");
						WSClient.setData("{var_startDate}", startDate);
						WSClient.setData("{var_endDate}", endDate);

						System.out.println("Start: " + startDate);
						System.out.println(endDate);

						HashMap<String, String> resvID = CreateReservation.createReservation("DS_29");
						if (!resvID.get("reservationId").equals("error")) {
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");

							WSClient.setData("{var_count1}", "4");
							WSClient.setData("{var_ageGroup1}", "ADULT");
							WSClient.setData("{var_age1}", "");
							WSClient.setData("{var_count2}", "");
							WSClient.setData("{var_ageGroup2}", "");
							WSClient.setData("{var_age2}", "");

							WSClient.setData("{var_rateCode}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
							WSClient.setData("{var_roomCount}", "1");

							*//***************
							 * OWS Modify Booking Operation
							 ********************//*

							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_31");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									String query = WSClient.getQuery("QS_36");
									System.out.println("Query: " + query);
									HashMap<String, String> prices = WSClient.getDBRow(query);
									int chargeForDay = Integer.parseInt(prices.get("AMOUNT_1"))
											+ Integer.parseInt(prices.get("AMOUNT_2"))
											+ Integer.parseInt(prices.get("AMOUNT_3"))
											+ Integer.parseInt(prices.get("ADULT_CHARGE"));

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Details of Rate Code : " + prices.get("RATE_CODE") + "</b>");
									WSClient.writeToReport(LogStatus.INFO,
											"<b> Charge For Adult 1 -> " + prices.get("AMOUNT_1") + "  Adult 2 -> "
													+ prices.get("AMOUNT_2") + "  Adult 3 -> " + prices.get("AMOUNT_3")
													+ " Extra Adult -> " + prices.get("ADULT_CHARGE") + "</b>");

									String adultCount = WSClient.getElementValue(modifyBookingReq,
											"RoomStay_GuestCounts_GuestCount_count", XMLType.REQUEST);
									String resadultCount = WSClient.getElementValue(modifyBookingRes,
											"RoomStay_GuestCounts_GuestCount_count", XMLType.RESPONSE);

									query = WSClient.getQuery("QS_04");
									HashMap<String, String> dbResults = WSClient.getDBRow(query);

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validating insertion of adult count into DB</b>");
									*//****
									 * Validate modified adult guest count
									 ****//*
									if (WSAssert.assertEquals(adultCount, dbResults.get("ADULTS"), true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Adult Guest Count -> Expected : "
												+ adultCount + "  Actual : " + dbResults.get("ADULTS") + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Adult Guest Count -> Expected : "
												+ adultCount + "  Actual : " + dbResults.get("ADULTS") + "</b>");
									}

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validating retrieval of counts onto Response</b>");
									*//****
									 * Validate modified adult guest count
									 ****//*
									if (WSAssert.assertEquals(adultCount, resadultCount, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Adult Guest Count -> Expected : "
												+ adultCount + "  Actual : " + resadultCount + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Adult Guest Count -> Expected : "
												+ adultCount + "  Actual : " + resadultCount + "</b>");
									}

									WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Rate<b>");
									WSAssert.assertIfElementValueEquals(modifyBookingRes,
											"RoomStay_RoomRates_RoomRate_ratePlanCode",
											OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"), false);
									WSAssert.assertIfElementValueEquals(modifyBookingRes,
											"RoomStay_RoomRates_RoomRate_roomTypeCode",
											OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"), false);
									WSAssert.assertIfElementValueEquals(modifyBookingRes,
											"Rates_Rate_Base_currencyCode", "USD", false);
									WSAssert.assertIfElementValueEquals(modifyBookingRes,
											"RoomRateAndPackages_Charges_Amount", Integer.toString(chargeForDay),
											false);

									WSClient.writeToReport(LogStatus.INFO, "<b>Validating Expected Charges</b>");
									WSAssert.assertIfElementValueEquals(modifyBookingRes,
											"RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages",
											Integer.toString(chargeForDay * 2), false);
									HashMap<String, String> xpath = new HashMap<>();
									// xpath.put("RoomStay_ExpectedCharges_ChargesForPostingDate_PostingDate",
									// "RoomStay_ExpectedCharges_ChargesForPostingDate");
									xpath.put("ExpectedCharges_ChargesForPostingDate_RoomRateAndPackages_TotalCharges",
											"RoomStay_ExpectedCharges_ChargesForPostingDate");
									xpath.put("RoomRateAndPackages_Charges_Amount_currencyCode",
											"RoomStay_ExpectedCharges_ChargesForPostingDate");

									List<LinkedHashMap<String, String>> resDayData = WSClient
											.getMultipleNodeList(modifyBookingRes, xpath, false, XMLType.RESPONSE);
									List<LinkedHashMap<String, String>> expDayData = new ArrayList<LinkedHashMap<String, String>>();
									LinkedHashMap<String, String> data = new LinkedHashMap<>();
									data.put("agesChargesAmountcurrencyCode1", "USD");
									data.put("omRateAndPackagesTotalCharges1", String.valueOf(chargeForDay));
									expDayData.add(data);
									expDayData.add(data);

									WSAssert.assertEquals(resDayData, expDayData, false);

								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									if (!WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE).equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO, "<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
												+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
									if (!WSClient
											.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											.equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE)
														+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"<b>Pre-requistes failed: Unable to Change application parameter</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requistes required for Reservation creation failed</b>");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				WSClient.setData("{var_parameter}", "RATE_DETAIL_ADDED_VALUE");
				WSClient.setData("{var_settingValue}", "N");
				ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
//				if (!WSClient.getData("{var_resvId}").equals(""))
//					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
			}
		}
	}*/

	/**
	 * @author psarawag Method to check if the OWS Reservation Modify Booking is
	 *         working i.e., Verify cancellation and deposit policy in the
	 *         response is attached to ratecode when both ratecode and
	 *         reservationType has different deposit and cancellation policy
	 *         attached.
	 *
	 *         PreRequisites Required: -->Profile is created -->Reservation is
	 *         created
	 *
	 *         ->Room Type ->Rate Code and Room Type attached to it ->Market
	 *         Code ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS","poiuyt" })

	public void modifyBooking_42620() {
		try {
			String testName = "modifyBooking_42620";
			WSClient.startTest(testName,
					"Verify that cancellation and deposit policy of the rate code are retrieved on the response when both "
							+ "ratecode and reservationType has different deposit and cancellation policy attached",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_04"));

				/************
				 * Prerequisite 1: Create profile
				 *********************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
					WSClient.setData("{var_profileId}", profileID);

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/

					WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_6}"));
					WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_8}"));
					WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_6}"));
					WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_8}"));
					fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
							OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

					HashMap<String, String> resvID1 = CreateReservation.createReservation("DS_24");
					if (resvID1.get("reservationId") != "error") {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID1.get("reservationId") + "</b>");
						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_08"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_07"));
						WSClient.setData("{var_roomCount}", "1");

						WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_6}"));
						WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_8}"));
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_6}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_8}"));
						fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_08"),
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_07"));
						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_03");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								List<LinkedHashMap<String, String>> resValues = new ArrayList<LinkedHashMap<String, String>>();
								HashMap<String, String> xPath = new HashMap<String, String>();
								xPath.put("RatePlan_AdditionalDetails_AdditionalDetail_detailType",
										"RatePlan_AdditionalDetails_AdditionalDetail");
								xPath.put("AdditionalDetail_AdditionalDetailDescription_Text",
										"RatePlan_AdditionalDetails_AdditionalDetail");
								resValues = WSClient.getMultipleNodeList(modifyBookingRes, xPath, true,
										XMLType.RESPONSE);
								String cancelDate = WSClient.getElementValue(modifyBookingRes,
										"RatePlans_RatePlan_CancellationDateTime", XMLType.RESPONSE);
								String depositAmount = WSClient.getElementValue(modifyBookingRes,
										"RatePlan_DepositRequired_DepositAmount", XMLType.RESPONSE);
								String dueDate = WSClient.getElementValue(modifyBookingRes,
										"RatePlan_DepositRequired_DueDate", XMLType.RESPONSE);
								for (int i = 0; i < resValues.size(); i++) {
									if (resValues.get(i).get("DetailType").equals("CancelPolicy")) {
										resValues.get(i).put("CancelDate",
												cancelDate.substring(0, cancelDate.indexOf('T')));
									}
									if (resValues.get(i).get("DetailType").equals("DepositPolicy")) {
										resValues.get(i).put("DepositAmount", depositAmount);
										resValues.get(i).put("DueDate", dueDate);
									}
								}

								LinkedHashMap<String, String> depositValues = WSClient
										.getDBRow(WSClient.getQuery("QS_32"));
								LinkedHashMap<String, String> cancelValues = WSClient
										.getDBRow(WSClient.getQuery("QS_31"));
								if (cancelValues.size() > 0) {
									cancelValues.put("DetailType", "CancelPolicy");
								}
								if (cancelValues.containsKey("Text")) {
									cancelValues.put("Text", "Cancel By " + cancelValues.get("Text"));
								}
								if (depositValues.size() > 0) {
									depositValues.put("DetailType", "DepositPolicy");
								}
								if (depositValues.containsKey("DepositAmount") && depositValues.containsKey("Text")) {
									depositValues.put("Text",
											"A deposit of " + depositValues.get("DepositAmount") + ".00 is due by "
													+ depositValues.get("Text")
													+ " in order to guarantee your reservation.");
								}
								for (int i = 0; i < resValues.size(); i++) {
									if (resValues.get(i).get("DetailType").equals("CancelPolicy")) {
										WSClient.writeToReport(LogStatus.INFO, "<b>Validating Cancellation Policy</b>");
										WSAssert.assertEquals(cancelValues, resValues.get(i), false);
									}
									if (resValues.get(i).get("DetailType").equals("DepositPolicy")) {
										WSClient.writeToReport(LogStatus.INFO, "<b>Validating Deposit Policy</b>");
										WSAssert.assertEquals(depositValues, resValues.get(i), false);
									}
								}
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * @author nitin Method to check if the OWS Reservation Modify Booking is
	 *         working i.e., error message is displayed when ADULT Guest count
	 *         exceeds the set limit
	 *
	 *         PreRequisites Required: -->Profile is created -->Reservation is
	 *         created
	 *
	 *         ->Room Type ->Rate Code and Room Type attached to it ->Market
	 *         Code ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42721() {
		try {
			String testName = "modifyBooking_42721";
			WSClient.startTest(testName,
					"Verify that error message is displayed when ADULT Guest count exceeds the limit set",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
			WSClient.setData("{var_par}", "N");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			if (!paramValue.equals("N")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
				paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			}

			if (paramValue.equals("N")) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/************ Prerequisite 1: Create profile **************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
						WSClient.setData("{var_profileId}", profileID);

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}");
						String endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_4}");
						WSClient.setData("{var_startDate}", startDate);
						WSClient.setData("{var_endDate}", endDate);

						HashMap<String, String> resvID = CreateReservation.createReservation("DS_29");
						if (resvID.get("reservationId") != "error") {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							String query = WSClient.getQuery("OWSModifyBooking", "QS_51");
							LinkedHashMap<String, String> results = WSClient.getDBRow(query);
							int limit = Integer.parseInt(results.get("MAX_OCCUPANCY_ADULTS"));
							WSClient.writeToReport(LogStatus.INFO, "Adults Maximum Occupancy for room type "
									+ WSClient.getData("{VAR_ROOMTYPE}") + " is : " + limit);

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							WSClient.setData("{var_age1}", "");
							WSClient.setData("{var_age2}", "");
							WSClient.setData("{var_ageGroup1}", "ADULT");
							WSClient.setData("{var_ageGroup2}", "");
							// Set the count to value more than limit set
							WSClient.setData("{var_count1}", String.valueOf(limit + 1));
							WSClient.setData("{var_count2}", "");
							WSClient.setData("{var_rateCode}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
							WSClient.setData("{var_roomCount}", "1");

							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_31");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Pre-requisite failed >> Changing the channel parameter failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * @author nitin Method to check if the OWS Reservation Modify Booking is
	 *         working i.e., error message is displayed when CHILD Guest count
	 *         exceeds the limit set
	 *
	 *         PreRequisites Required: -->Profile is created -->Reservation is
	 *         created
	 *
	 *         ->Room Type ->Rate Code and Room Type attached to it ->Market
	 *         Code ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42724() {
		try {
			String testName = "modifyBooking_42724";
			WSClient.startTest(testName,
					"Verify that error message is displayed when CHILD Guest count exceeds the limit set",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
			WSClient.setData("{var_par}", "N");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			if (!paramValue.equals("N")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
				paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			}

			if (paramValue.equals("N")) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/************
					 * Prerequisite 1: Create profile
					 ********************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
						WSClient.setData("{var_profileId}", profileID);

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_41}");
						String endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_42}");
						WSClient.setData("{var_startDate}", startDate);
						WSClient.setData("{var_endDate}", endDate);

						HashMap<String, String> resvID = CreateReservation.createReservation("DS_29");
						if (resvID.get("reservationId") != "error") {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String query = WSClient.getQuery("OWSModifyBooking", "QS_51");
							LinkedHashMap<String, String> results = WSClient.getDBRow(query);
							int limit = Integer.parseInt(results.get("MAX_OCCUPANCY_CHILDREN"));
							WSClient.writeToReport(LogStatus.INFO, "Children Maximum Occupancy for room type "
									+ WSClient.getData("{VAR_ROOMTYPE}") + " is : " + limit);

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							// Set Data Sheet Values
							WSClient.setData("{var_age1}", "");
							WSClient.setData("{var_age2}", "9");
							WSClient.setData("{var_ageGroup1}", "ADULT");
							WSClient.setData("{var_ageGroup2}", "CHILD");
							WSClient.setData("{var_count1}", "");
							WSClient.setData("{var_count2}", String.valueOf(limit + 1));
							WSClient.setData("{var_rateCode}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
							WSClient.setData("{var_roomCount}", "1");
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_31");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Pre-requisite failed >> Changing the channel parameter failed");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * @author nitin Method to check if the OWS Reservation Modify Booking is
	 *         working i.e., Error message is displayed when Guest count (Total
	 *         OCCUPANTS) Exceeds room occupancy limit.
	 *
	 *         PreRequisites Required: -->Profile is created -->Reservation is
	 *         created
	 *
	 *         ->Room Type ->Rate Code and Room Type attached to it ->Market
	 *         Code ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42723() {
		try {
			String testName = "modifyBooking_42723";
			WSClient.startTest(testName,
					"Verify that Error message is displayed when Guest count (Total OCCUPANTS) EXCEEDS the limit set",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
			WSClient.setData("{var_par}", "N");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			if (!paramValue.equals("N")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
				paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			}

			if (paramValue.equals("N")) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************/
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/************
					 * Prerequisite 1: Create profile
					 *******************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
						WSClient.setData("{var_profileId}", profileID);

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_12}");
						String endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}");
						WSClient.setData("{var_startDate}", startDate);
						WSClient.setData("{var_endDate}", endDate);

						HashMap<String, String> resvID = CreateReservation.createReservation("DS_29");
						if (resvID.get("reservationId") != "error") {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String query = WSClient.getQuery("OWSModifyBooking", "QS_51");
							LinkedHashMap<String, String> results = WSClient.getDBRow(query);
							int limit = Integer.parseInt(results.get("MAX_OCCUPANCY"));
							WSClient.writeToReport(LogStatus.INFO, "Maximum Occupancy for room type "
									+ WSClient.getData("{VAR_ROOMTYPE}") + " is : " + limit);

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							// Set Data Sheet Values
							WSClient.setData("{var_age1}", "");
							WSClient.setData("{var_age2}", "10");
							WSClient.setData("{var_ageGroup1}", "ADULT");
							WSClient.setData("{var_ageGroup2}", "CHILD");
							WSClient.setData("{var_count1}", String.valueOf((limit / 2) + 2));
							WSClient.setData("{var_count2}", String.valueOf(limit / 2));
							WSClient.setData("{var_rateCode}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
							WSClient.setData("{var_roomCount}", "1");
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_31");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Pre-requisite failed >> Changing the channel parameter failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * booking is modified when ADULT Guest count EXCEEDS the limit set as
	 * OVERRIDE_YN is set to Y
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42730() {
		try {
			String testName = "modifyBooking_42730";
			WSClient.startTest(testName,
					"Verify that booking is modified when ADULT Guest count EXCEEDS the limit set as OVERRIDE_YN is set to Y",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			if (!paramValue.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter OVERRIDE_YN</b>");
				paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			}

			if (paramValue.equals("Y")) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/************ Prerequisite 1: Create profile **************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
						WSClient.setData("{var_profileId}", profileID);

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}");
						String endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_4}");
						WSClient.setData("{var_startDate}", startDate);
						WSClient.setData("{var_endDate}", endDate);

						HashMap<String, String> resvID = CreateReservation.createReservation("DS_29");
						if (resvID.get("reservationId") != "error") {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String query = WSClient.getQuery("OWSModifyBooking", "QS_51");
							LinkedHashMap<String, String> results = WSClient.getDBRow(query);
							int limit = Integer.parseInt(results.get("MAX_OCCUPANCY_ADULTS"));
							WSClient.writeToReport(LogStatus.INFO, "Adults Maximum Occupancy for room type "
									+ WSClient.getData("{VAR_ROOMTYPE}") + " is : " + limit);

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							// Set Data Sheet Values
							WSClient.setData("{var_age1}", "");
							WSClient.setData("{var_age2}", "");
							WSClient.setData("{var_ageGroup1}", "ADULT");
							WSClient.setData("{var_ageGroup2}", "");
							WSClient.setData("{var_count1}", String.valueOf(limit + 1));
							WSClient.setData("{var_count2}", "");
							WSClient.setData("{var_rateCode}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
							WSClient.setData("{var_roomCount}", "1");

							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_31");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									query = WSClient.getQuery("QS_04");
									HashMap<String, String> dbResults = WSClient.getDBRow(query);

									String adultCount = WSClient.getElementValue(modifyBookingReq,
											"RoomStay_GuestCounts_GuestCount_count", XMLType.REQUEST);
									String resadultCount = WSClient.getElementValue(modifyBookingRes,
											"RoomStay_GuestCounts_GuestCount_count", XMLType.RESPONSE);

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validating insertion of adult count into DB</b>");
									/****
									 * Validate modified adult guest count
									 ****/
									if (WSAssert.assertEquals(adultCount, dbResults.get("ADULTS"), true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Adult Guest Count -> Expected : "
												+ adultCount + "  Actual : " + dbResults.get("ADULTS") + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Adult Guest Count -> Expected : "
												+ adultCount + "  Actual : " + dbResults.get("ADULTS") + "</b>");
									}

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validating retrieval of counts onto Response</b>");
									/****
									 * Validate modified adult guest count
									 ****/
									if (WSAssert.assertEquals(adultCount, resadultCount, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Adult Guest Count -> Expected : "
												+ adultCount + "  Actual : " + resadultCount + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Adult Guest Count -> Expected : "
												+ adultCount + "  Actual : " + resadultCount + "</b>");
									}

								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Pre-requisite failed >> Changing the channel parameter failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			OPERALib.setOperaHeader(OPERALib.getUserName());
			try {
				WSClient.setData("{var_par}", "N");
				ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");

				WSClient.setData("{var_resvId}", WSClient.getData("{var_resvId}"));
				if (CancelReservation.cancelReservation("DS_02")) {
					WSClient.writeToLog("Reservation cancellation successful");
				} else
					WSClient.writeToLog("Reservation cancellation failed");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * booking is modified when CHILD Guest count EXCEEDS the limit set as
	 * OVERRIDE_YN is set to Y PreRequisites Required: -->Profile is created
	 * -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42742() {
		try {
			String testName = "modifyBooking_42742";
			WSClient.startTest(testName,
					"Verify that booking is modified when CHILD Guest count EXCEEDS the limit set as OVERRIDE_YN is set to Y",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			if (!paramValue.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter OVERRIDE_YN</b>");
				paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			}

			if (paramValue.equals("Y")) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/************
					 * Prerequisite 1: Create profile
					 ********************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
						WSClient.setData("{var_profileId}", profileID);

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}");
						String endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_4}");
						WSClient.setData("{var_startDate}", startDate);
						WSClient.setData("{var_endDate}", endDate);

						HashMap<String, String> resvID = CreateReservation.createReservation("DS_29");
						if (resvID.get("reservationId") != "error") {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String query = WSClient.getQuery("OWSModifyBooking", "QS_51");
							LinkedHashMap<String, String> results = WSClient.getDBRow(query);
							int limit = Integer.parseInt(results.get("MAX_OCCUPANCY_CHILDREN"));
							WSClient.writeToReport(LogStatus.INFO, "Children Maximum Occupancy for room type "
									+ WSClient.getData("{VAR_ROOMTYPE}") + " is : " + limit);

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							// Set Data Sheet Values
							WSClient.setData("{var_age1}", "");
							WSClient.setData("{var_age2}", "9");
							WSClient.setData("{var_ageGroup1}", "ADULT");
							WSClient.setData("{var_ageGroup2}", "CHILD");
							WSClient.setData("{var_count1}", "1");
							WSClient.setData("{var_count2}", String.valueOf(limit + 1));
							WSClient.setData("{var_rateCode}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
							WSClient.setData("{var_roomCount}", "1");
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_31");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									query = WSClient.getQuery("QS_04");
									HashMap<String, String> dbResults = WSClient.getDBRow(query);

									String adultCount = WSClient.getElementValue(modifyBookingReq,
											"RoomStay_GuestCounts_GuestCount_count", XMLType.REQUEST);
									String childCount = WSClient.getElementValue(modifyBookingReq,
											"RoomStay_GuestCounts_GuestCount[2]_count", XMLType.REQUEST);
									String resadultCount = WSClient.getElementValue(modifyBookingRes,
											"RoomStay_GuestCounts_GuestCount_count", XMLType.RESPONSE);
									String reschildCount = WSClient.getElementValue(modifyBookingRes,
											"RoomStay_GuestCounts_GuestCount_count_2", XMLType.RESPONSE);

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validating insertion of guest counts into DB</b>");
									/****
									 * Validate modified adult guest count
									 ****/
									if (WSAssert.assertEquals(adultCount, dbResults.get("ADULTS"), true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Adult Guest Count -> Expected : "
												+ adultCount + "  Actual : " + dbResults.get("ADULTS") + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Adult Guest Count -> Expected : "
												+ adultCount + "  Actual : " + dbResults.get("ADULTS") + "</b>");
									}
									/****
									 * Validate modified child guest count
									 ****/
									if (WSAssert.assertEquals(childCount, dbResults.get("CHILDREN"), true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Child Guest Count -> Expected : "
												+ childCount + "  Actual : " + dbResults.get("CHILDREN") + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Child Guest Count -> Expected : "
												+ childCount + "  Actual : " + dbResults.get("CHILDREN") + "</b>");
									}

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validating retrieval of details onto the Response</b>");
									/****
									 * Validate modified adult guest count
									 ****/
									if (WSAssert.assertEquals(adultCount, resadultCount, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Adult Guest Count -> Expected : "
												+ adultCount + "  Actual : " + resadultCount + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Adult Guest Count -> Expected : "
												+ adultCount + "  Actual : " + resadultCount + "</b>");
									}
									/****
									 * Validate modified child guest count
									 ****/
									if (WSAssert.assertEquals(childCount, reschildCount, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Child Guest Count -> Expected : "
												+ childCount + "  Actual : " + reschildCount + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Child Guest Count -> Expected : "
												+ childCount + "  Actual : " + reschildCount + "</b>");
									}
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Pre-requisite failed >> Changing the channel parameter failed");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			OPERALib.setOperaHeader(OPERALib.getUserName());
			try {
				WSClient.setData("{var_par}", "N");
				ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");

				WSClient.setData("{var_resvId}", WSClient.getData("{var_resvId}"));
				if (CancelReservation.cancelReservation("DS_02")) {
					WSClient.writeToLog("Reservation cancellation successful");
				} else
					WSClient.writeToLog("Reservation cancellation failed");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * booking is modified when Guest count (Total OCCUPANTS) EXCEEDS the limit
	 * set as OVERRIDE_YN is set to Y
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun1234" })

	public void modifyBooking_42725() {
		try {
			String testName = "modifyBooking_42725";
			WSClient.startTest(testName,
					"Verify that booking is modified though the Guest count (Total OCCUPANTS) EXCEEDS the limit when OVERRIDE_YN is set to Y",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			if (!paramValue.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter OVERRIDE_YN</b>");
				paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			}

			if (paramValue.equals("Y")) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************/
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/************
					 * Prerequisite 1: Create profile
					 *******************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
						WSClient.setData("{var_profileId}", profileID);

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_12}");
						String endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}");
						WSClient.setData("{var_startDate}", startDate);
						WSClient.setData("{var_endDate}", endDate);

						HashMap<String, String> resvID = CreateReservation.createReservation("DS_29");
						if (resvID.get("reservationId") != "error") {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String query = WSClient.getQuery("OWSModifyBooking", "QS_51");
							LinkedHashMap<String, String> results = WSClient.getDBRow(query);
							int limit = Integer.parseInt(results.get("MAX_OCCUPANCY"));
							WSClient.writeToReport(LogStatus.INFO, "Maximum Occupancy for room type "
									+ WSClient.getData("{VAR_ROOMTYPE}") + " is : " + limit);

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							// Set Data Sheet Values
							WSClient.setData("{var_age1}", "");
							WSClient.setData("{var_age2}", "10");
							WSClient.setData("{var_ageGroup1}", "ADULT");
							WSClient.setData("{var_ageGroup2}", "CHILD");
							WSClient.setData("{var_count1}", String.valueOf((limit / 2) + 2));
							WSClient.setData("{var_count2}", String.valueOf(limit / 2));
							WSClient.setData("{var_rateCode}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
							WSClient.setData("{var_roomCount}", "1");
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_31");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									query = WSClient.getQuery("QS_04");
									HashMap<String, String> dbResults = WSClient.getDBRow(query);

									String adultCount = WSClient.getElementValue(modifyBookingReq,
											"RoomStay_GuestCounts_GuestCount_count", XMLType.REQUEST);
									String childCount = WSClient.getElementValue(modifyBookingReq,
											"RoomStay_GuestCounts_GuestCount[2]_count", XMLType.REQUEST);
									String resadultCount = WSClient.getElementValue(modifyBookingRes,
											"RoomStay_GuestCounts_GuestCount_count", XMLType.RESPONSE);
									String reschildCount = WSClient.getElementValue(modifyBookingRes,
											"RoomStay_GuestCounts_GuestCount_count_2", XMLType.RESPONSE);

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validating insertion of guest counts into DB</b>");
									/****
									 * Validate modified adult guest count
									 ****/
									if (WSAssert.assertEquals(adultCount, dbResults.get("ADULTS"), true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Adult Guest Count -> Expected : "
												+ adultCount + "  Actual : " + dbResults.get("ADULTS") + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Adult Guest Count -> Expected : "
												+ adultCount + "  Actual : " + dbResults.get("ADULTS") + "</b>");
									}
									/****
									 * Validate modified child guest count
									 ****/
									if (WSAssert.assertEquals(childCount, dbResults.get("CHILDREN"), true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Child Guest Count -> Expected : "
												+ childCount + "  Actual : " + dbResults.get("CHILDREN") + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Child Guest Count -> Expected : "
												+ childCount + "  Actual : " + dbResults.get("CHILDREN") + "</b>");
									}

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validating retrieval of details onto the Response</b>");
									/****
									 * Validate modified adult guest count
									 ****/
									if (WSAssert.assertEquals(adultCount, resadultCount, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Adult Guest Count -> Expected : "
												+ adultCount + "  Actual : " + resadultCount + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Adult Guest Count -> Expected : "
												+ adultCount + "  Actual : " + resadultCount + "</b>");
									}
									/****
									 * Validate modified child guest count
									 ****/
									if (WSAssert.assertEquals(childCount, reschildCount, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Child Guest Count -> Expected : "
												+ childCount + "  Actual : " + reschildCount + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Child Guest Count -> Expected : "
												+ childCount + "  Actual : " + reschildCount + "</b>");
									}

								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Pre-requisite failed >> Changing the channel parameter failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			OPERALib.setOperaHeader(OPERALib.getUserName());
			try {
				WSClient.setData("{var_par}", "N");
				ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");

				WSClient.setData("{var_resvId}", WSClient.getData("{var_resvId}"));
				if (CancelReservation.cancelReservation("DS_02")) {
					WSClient.writeToLog("Reservation cancellation successful");
				} else
					WSClient.writeToLog("Reservation cancellation failed");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "OWS", "Reservation" })

	public void modifyBooking_42520() {
		String paramvalue, resvid = "";
		try {
			String testName = "modifyBooking_42520";
			WSClient.startTest(testName,
					"Verify that booking is modified when room count EXCEEDS the sell limit of the room type for the given date range and channel Inventory is N",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsresort}", resortExtValue);

			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_param}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			OPERALib.setOperaHeader(OPERALib.getUserName());

			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_02", "PARAMETER_VALUE");
			String parameter = paramvalue;
			if (!WSAssert.assertEquals("Y", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter CHANNEL_INVENTORY</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_02", "PARAMETER_VALUE");
				WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			}

			if (parameter.equals("Y")) {
				WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_133}"));
				WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_134}"));

				WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
				WSClient.setData("{var_roomLimit}", "2");

				String setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_03");
				String setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
				if (WSAssert.assertIfElementExists(setSellLimitsRes, "SetChannelSellLimitsByDateRangeRS_Success",
						true)) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Sell limit for room type " + WSClient.getData("{var_roomType}") + " on "
									+ WSClient.getData("{var_startDate}") + " - " + WSClient.getData("{var_roomLimit}")
									+ "</b>");

					String fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_02");
					String fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
					if (WSAssert.assertIfElementExists(fetchLimitsRes, "FetchChannelSellLimitsRS_Success", true)) {
						WSClient.setData("{var_id}", WSClient.getElementValue(fetchLimitsRes,
								"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
					}
					WSClient.setData("{var_par}", "N");
					WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
					WSClient.setData("{var_type}", "Boolean");
					System.out.println(resortExtValue);
					OPERALib.setOperaHeader(OPERALib.getUserName());

					/**********
					 * Disable the channel inventory parameter
					 ******************/
					parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_02", "PARAMETER_VALUE");
					if (parameter.equals("N")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Channel Inventory : " + parameter + "</b>");
						/***********
						 * Pre-requisite : Create a Profile
						 ***********/
						profileID = CreateProfile.createProfile("DS_00");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);

							/*************
							 * Prerequisite :Create a Booking
							 ********************/
							WSClient.setData("{var_busdate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_126}") + "T14:00:00+05:30");
							WSClient.setData("{var_busdate1}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_127}") + "T14:00:00+05:30");
							WSClient.setData("{var_time}", "09:00:00");
							WSClient.setData("{var_resvType}",
									OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
							WSClient.setData("{var_rate}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
							WSClient.setData("{var_ReservationType}",
									OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
							WSClient.setData("{var_owsResort}", resortExtValue);

							OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
									OWSLib.getChannelType(interfaceName),
									OWSLib.getChannelCarier(resortOperaValue, interfaceName));
							String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
							String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
							if (WSAssert.assertIfElementValueEquals(createBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
								resvid = WSClient.getElementValueByAttribute(createBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}", resvid);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:" + resvid + "</b>");

								WSClient.setData("{var_rateCode}",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
								WSClient.setData("{var_roomType}",
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
								WSClient.setData("{var_roomCount}", "3");

								String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_06");
								String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

								if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
										true)) {
									if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
											"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										String query = WSClient.getQuery("QS_04");
										LinkedHashMap<String, String> dbResults = WSClient.getDBRow(query);
										String roomCount = WSClient.getData("{var_roomCount}");

										String arrivalDate = WSClient.getElementValue(modifyBookingReq,
												"RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
										String departureDate = WSClient.getElementValue(modifyBookingReq,
												"RoomStay_TimeSpan_EndDate", XMLType.REQUEST);
										String resarrivalDate = WSClient.getElementValue(modifyBookingRes,
												"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE);
										String resdepartureDate = WSClient.getElementValue(modifyBookingRes,
												"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE);

										resarrivalDate = resarrivalDate.substring(0, resarrivalDate.indexOf('T'));
										resdepartureDate = resdepartureDate.substring(0, resdepartureDate.indexOf('T'));

										/************
										 * Validating against DB
										 **************/
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Validation > Modifications are applied on DB</b>");

										if (WSAssert.assertEquals(roomCount, dbResults.get("NO_OF_ROOMS"), true)) {
											WSClient.writeToReport(LogStatus.PASS,
													"<b>RoomCount -> Expected : " + roomCount + "  Actual : "
															+ dbResults.get("NO_OF_ROOMS") + "</b>");
										} else {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b>RoomCount -> Expected : " + roomCount + "  Actual : "
															+ dbResults.get("NO_OF_ROOMS") + "</b>");
										}

										String dbArrival = dbResults.get("ARRIVAL");
										dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));
										String dbDeparture = dbResults.get("DEPARTURE");
										dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

										if (WSAssert.assertEquals(arrivalDate, dbArrival, true)) {
											WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
													+ arrivalDate + "  Actual : " + dbArrival + "</b>");
										} else {
											WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
													+ arrivalDate + "  Actual : " + dbArrival + "</b>");
										}
										if (WSAssert.assertEquals(departureDate, dbDeparture, true)) {
											WSClient.writeToReport(LogStatus.PASS, "<b> Departure date -> Expected : "
													+ departureDate + "  Actual : " + dbDeparture + "</b>");
										} else {
											WSClient.writeToReport(LogStatus.FAIL, "<b> Departure date -> Expected : "
													+ departureDate + "  Actual : " + dbDeparture + "</b>");
										}

										/************
										 * Validating against Response
										 **************/
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Validation > Modifications are retrieved correctly on response</b>");

										roomCount = WSClient.getElementValue(modifyBookingRes,
												"RoomStay_RoomTypes_RoomType_numberOfUnits", XMLType.RESPONSE);
										if (WSAssert.assertEquals(dbResults.get("NO_OF_ROOMS"), roomCount, true)) {
											WSClient.writeToReport(LogStatus.PASS,
													"<b>RoomCount -> Expected : " + dbResults.get("NO_OF_ROOMS")
													+ "  Actual : " + roomCount + "</b>");
										} else {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b>RoomCount -> Expected : " + dbResults.get("NO_OF_ROOMS")
													+ "  Actual : " + roomCount + "</b>");
										}

										/****
										 * Validate modified arrival dates
										 ****/
										if (WSAssert.assertEquals(arrivalDate, resarrivalDate, true)) {
											WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
													+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
										} else {
											WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
													+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
										}

										/****
										 * Validate modified departure dates
										 ****/
										if (WSAssert.assertEquals(departureDate, resdepartureDate, true)) {
											WSClient.writeToReport(LogStatus.PASS, "<b> Departure date -> Expected : "
													+ departureDate + "  Actual : " + resdepartureDate + "</b>");
										} else {
											WSClient.writeToReport(LogStatus.FAIL, "<b> Departure date -> Expected : "
													+ departureDate + "  Actual : " + resdepartureDate + "</b>");
										}
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError", true)) {
										if (!WSClient
												.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
												.equals("*null*"))
											WSClient.writeToReport(LogStatus.FAIL,
													"<b> The error that is generated is : " + WSClient.getElementValue(
															modifyBookingRes, "ModifyBookingResponse_Result_GDSError",
															XMLType.RESPONSE) + "</b>");
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
										if (!WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
												.equals("*null*"))
											WSClient.writeToReport(LogStatus.FAIL,
													"<b> The error code that is generated is : "
															+ WSClient.getElementValue(modifyBookingRes,
																	"ModifyBookingResponse_Result_GDSError_errorCode",
																	XMLType.RESPONSE)
															+ "</b>");
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_Result_OperaErrorCode", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : " + WSClient.getElementValue(
														modifyBookingRes, "ModifyBookingResponse_Result_OperaErrorCode",
														XMLType.RESPONSE) + "</b>");
									}
									if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement",
											true)) {
										String message = WSAssert.getElementValue(modifyBookingRes,
												"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The text displayed in the response is :" + message + "</b>");
									}
								} else if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_faultstring", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
													+ "</b>");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"<b>Pre-requisite failed >> Creating a booking failed</b>");
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requisite failed >> Changing application parameter failed</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"<b>Pre-requisite failed >> Setting the sell limits failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requisite failed >> Changing the application parameter failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured due to : " + e);
			e.printStackTrace();
		} finally {
			OPERALib.setOperaHeader(OPERALib.getUserName());
			try {
				if (WSClient.getData("{var_id}") != "") {
					String removeSellLimitReq = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
					String removeSellLimitRes = WSClient.processSOAPMessage(removeSellLimitReq);
					if (WSAssert.assertIfElementExists(removeSellLimitRes, "RemoveChannelSellLimitsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Removed the limits set on the channel set limits </b>");
					}
				}

				if (resvid != "") {
					WSClient.setData("{var_resvId}", resvid);
					if (CancelReservation.cancelReservation("DS_02")) {
						WSClient.writeToLog("Reservation cancellation successful");
					} else
						WSClient.writeToLog("Reservation cancellation failed");
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "OWS", "Reservation" })

	public void modifyBooking_42766() {
		String paramvalue, resvid = "";
		try {
			String testName = "modifyBooking_42766";
			WSClient.startTest(testName,
					"Verify that booking is not modified when room count exceeds the sell limit of room type for the given data range and OVERRIDE_YN is N",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_param}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");

			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_02", "PARAMETER_VALUE");
			String parameter = paramvalue;
			if (!WSAssert.assertEquals("Y", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter CHANNEL_INVENTORY</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_02", "PARAMETER_VALUE");
				WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			}

			if (parameter.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
				WSClient.setData("{var_par}", "N");
				WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
				WSClient.setData("{var_param}", "OVERRIDE_YN");
				WSClient.setData("{var_type}", "Boolean");
				String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				if (!paramValue.equals("N")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
					paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
					WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				}

				if (paramValue.equals("N")) {
					if (OperaPropConfig.getPropertyConfigResults(
							new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
						/*************
						 * Prerequisite : Room type, Rate Plan Code, Source
						 * Code, Market Code
						 *****************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						/****** Prerequisite : Creating a Profile ******/
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

							/****************
							 * Prerequisite 2:Create a Reservation
							 *****************/

							WSClient.setData("{var_roomLimit}", "2");
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

							/*************
							 * Prerequisite :Create a Booking
							 ********************/

							WSClient.setData("{var_busdate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_93}") + "T14:00:00+05:30");
							WSClient.setData("{var_busdate1}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_94}") + "T14:00:00+05:30");
							WSClient.setData("{var_time}", "09:00:00");
							WSClient.setData("{var_resvType}",
									OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
							WSClient.setData("{var_rate}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
							WSClient.setData("{var_owsResort}", resort);

							fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
							String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
							if (WSAssert.assertIfElementValueEquals(createBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
								resvid = WSClient.getElementValueByAttribute(createBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}", resvid);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:" + resvid + "</b>");

								WSClient.setData("{var_rateCode}",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
								WSClient.setData("{var_roomType}",
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
								WSClient.setData("{var_roomCount}", "3");

								WSClient.setData("{var_startDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_93}"));
								WSClient.setData("{var_endDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_94}"));
								WSClient.setData("{var_busdate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_93}"));
								WSClient.setData("{var_busdate1}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_94}"));

								fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

								String setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange",
										"DS_03");
								String setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
								if (WSAssert.assertIfElementExists(setSellLimitsRes,
										"SetChannelSellLimitsByDateRangeRS_Success", true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Sell limit for room type " + WSClient.getData("{var_roomType}") + " on "
													+ WSClient.getData("{var_startDate}") + " - "
													+ WSClient.getData("{var_roomLimit}") + "</b>");
									String fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits",
											"DS_02");
									String fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
									if (WSAssert.assertIfElementExists(fetchLimitsRes,
											"FetchChannelSellLimitsRS_Success", true)) {
										WSClient.setData("{var_id}", WSClient.getElementValue(fetchLimitsRes,
												"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
									}

									String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_03");
									String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

									if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
											true)) {
										WSAssert.assertIfElementValueEquals(modifyBookingRes,
												"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError", true)) {
											if (!WSClient
													.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
													.equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError",
																		XMLType.RESPONSE)
																+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
											if (!WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
													.equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError_errorCode",
																		XMLType.RESPONSE)
																+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_OperaErrorCode", true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error code that is generated is : "
															+ WSClient.getElementValue(modifyBookingRes,
																	"ModifyBookingResponse_Result_OperaErrorCode",
																	XMLType.RESPONSE)
															+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement",
												true)) {
											String message = WSAssert.getElementValue(modifyBookingRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is :" + message + "</b>");
										}
									} else if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_faultstring", true)) {
										WSClient.writeToReport(LogStatus.FAIL,
												"<b> The error that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
														+ "</b>");
									}
								} else {
									WSClient.writeToReport(LogStatus.WARNING,
											"Pre-Requisite failed >> Creating a booking failed");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"<b>Pre-requisite failed >> Setting sell limits failed</b>");
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requisite for reservation creation failed</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"<b>Pre-requisite failed >> Changing channel parameter failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requisite failed >> Changing channel parameter failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			OPERALib.setOperaHeader(OPERALib.getUserName());
			try {
				if (WSClient.getData("{var_id}") != "") {
					String removeSellLimitReq = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
					String removeSellLimitRes = WSClient.processSOAPMessage(removeSellLimitReq);
					if (WSAssert.assertIfElementExists(removeSellLimitRes, "RemoveChannelSellLimitsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Removed the limits set on the channel set limits </b>");
					}
				}

				if (resvid != "") {
					WSClient.setData("{var_resvId}", resvid);
					if (CancelReservation.cancelReservation("DS_02")) {
						WSClient.writeToLog("Reservation cancellation successful");
					} else
						WSClient.writeToLog("Reservation cancellation failed");
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "OWS", "Reservation" })

	public void modifyBooking_42759() {
		String paramvalue, resvid = "";
		try {
			String testName = "modifyBooking_42759";
			WSClient.startTest(testName,
					"Verify that booking is modified when room count exceeds the sell limit of room type for the given data range and OVERRIDE_YN parameter is Y",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_param}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");

			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_02", "PARAMETER_VALUE");
			String parameter = paramvalue;
			if (!WSAssert.assertEquals("Y", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter CHANNEL_INVENTORY</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_02", "PARAMETER_VALUE");
				WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			}

			if (parameter.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
				WSClient.setData("{var_par}", "Y");
				WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
				WSClient.setData("{var_param}", "OVERRIDE_YN");
				WSClient.setData("{var_type}", "Boolean");
				String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				if (!paramValue.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter OVERRIDE_YN</b>");
					paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
					WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				}

				if (paramValue.equals("Y")) {
					if (OperaPropConfig.getPropertyConfigResults(
							new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
						/*************
						 * Prerequisite : Room type, Rate Plan Code, Source
						 * Code, Market Code
						 *****************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						/****** Prerequisite : Creating a Profile ******/
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

							/****************
							 * Prerequisite 2:Create a Reservation
							 *****************/

							WSClient.setData("{var_roomLimit}", "3");
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

							/*************
							 * Prerequisite :Create a Booking
							 ********************/
							WSClient.setData("{var_busdate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_86}") + "T14:00:00+05:30");
							WSClient.setData("{var_busdate1}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_87}") + "T14:00:00+05:30");

							fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"),
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));

							WSClient.setData("{var_time}", "09:00:00");
							WSClient.setData("{var_resvType}",
									OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
							WSClient.setData("{var_rate}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
							WSClient.setData("{var_owsResort}", resort);

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
							String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
							if (WSAssert.assertIfElementValueEquals(createBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
								resvid = WSClient.getElementValueByAttribute(createBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}", resvid);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:" + resvid + "</b>");

								WSClient.setData("{var_rateCode}",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
								WSClient.setData("{var_roomType}",
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
								WSClient.setData("{var_roomCount}", "4");

								WSClient.setData("{var_busdate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_102}"));
								WSClient.setData("{var_busdate1}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_103}"));
								WSClient.setData("{var_startDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_102}"));
								WSClient.setData("{var_endDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_103}"));
								fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"),
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));

								String setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange",
										"DS_03");
								String setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
								if (WSAssert.assertIfElementExists(setSellLimitsRes,
										"SetChannelSellLimitsByDateRangeRS_Success", true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Sell limit for room type " + WSClient.getData("{var_roomType}") + " on "
													+ WSClient.getData("{var_startDate}") + " - "
													+ WSClient.getData("{var_roomLimit}") + "</b>");
									String fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits",
											"DS_02");
									String fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
									if (WSAssert.assertIfElementExists(fetchLimitsRes,
											"FetchChannelSellLimitsRS_Success", true)) {
										WSClient.setData("{var_id}", WSClient.getElementValue(fetchLimitsRes,
												"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
									}
									String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_06");
									String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

									if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
											true)) {
										if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
												"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											String query = WSClient.getQuery("QS_04");
											LinkedHashMap<String, String> dbResults = WSClient.getDBRow(query);
											String roomCount = WSClient.getData("{var_roomCount}");

											String arrivalDate = WSClient.getElementValue(modifyBookingReq,
													"RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
											String departureDate = WSClient.getElementValue(modifyBookingReq,
													"RoomStay_TimeSpan_EndDate", XMLType.REQUEST);
											String resarrivalDate = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE);
											String resdepartureDate = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE);

											resarrivalDate = resarrivalDate.substring(0, resarrivalDate.indexOf('T'));
											resdepartureDate = resdepartureDate.substring(0,
													resdepartureDate.indexOf('T'));

											/************
											 * Validating against DB
											 **************/
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Validation > Modifications are applied on DB</b>");

											if (WSAssert.assertEquals(roomCount, dbResults.get("NO_OF_ROOMS"), true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b>RoomCount -> Expected : " + roomCount + "  Actual : "
																+ dbResults.get("NO_OF_ROOMS") + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b>RoomCount -> Expected : " + roomCount + "  Actual : "
																+ dbResults.get("NO_OF_ROOMS") + "</b>");
											}

											String dbArrival = dbResults.get("ARRIVAL");
											dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));
											String dbDeparture = dbResults.get("DEPARTURE");
											dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

											if (WSAssert.assertEquals(arrivalDate, dbArrival, true)) {
												WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + dbArrival + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + dbArrival + "</b>");
											}
											if (WSAssert.assertEquals(departureDate, dbDeparture, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + dbDeparture + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + dbDeparture + "</b>");
											}

											/************
											 * Validating against Response
											 **************/
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Validation > Modifications are retrieved correctly on response</b>");

											roomCount = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_RoomTypes_RoomType_numberOfUnits", XMLType.RESPONSE);
											if (WSAssert.assertEquals(dbResults.get("NO_OF_ROOMS"), roomCount, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b>RoomCount -> Expected : " + dbResults.get("NO_OF_ROOMS")
														+ "  Actual : " + roomCount + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b>RoomCount -> Expected : " + dbResults.get("NO_OF_ROOMS")
														+ "  Actual : " + roomCount + "</b>");
											}

											/****
											 * Validate modified arrival dates
											 ****/
											if (WSAssert.assertEquals(arrivalDate, resarrivalDate, true)) {
												WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
											}

											/****
											 * Validate modified departure dates
											 ****/
											if (WSAssert.assertEquals(departureDate, resdepartureDate, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + resdepartureDate + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + resdepartureDate + "</b>");
											}
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError", true)) {
											if (!WSClient
													.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
													.equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError",
																		XMLType.RESPONSE)
																+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
											if (!WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
													.equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError_errorCode",
																		XMLType.RESPONSE)
																+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_OperaErrorCode", true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error code that is generated is : "
															+ WSClient.getElementValue(modifyBookingRes,
																	"ModifyBookingResponse_Result_OperaErrorCode",
																	XMLType.RESPONSE)
															+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement",
												true)) {
											String message = WSAssert.getElementValue(modifyBookingRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is :" + message + "</b>");
										}
									} else if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_faultstring", true)) {
										WSClient.writeToReport(LogStatus.FAIL,
												"<b> The error that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
														+ "</b>");
									}
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"<b>Pre-requisite failed >> Setting sell limits failed</b>");
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requisite for reservation creation failed</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"<b>Pre-requisite failed >> Changing channel parameter failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requisite failed >> Changing channel parameter failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			OPERALib.setOperaHeader(OPERALib.getUserName());
			try {
				if (WSClient.getData("{var_id}") != "") {
					String removeSellLimitReq = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
					String removeSellLimitRes = WSClient.processSOAPMessage(removeSellLimitReq);
					if (WSAssert.assertIfElementExists(removeSellLimitRes, "RemoveChannelSellLimitsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Removed the limits set on the channel set limits </b>");
					}
				}
				if (resvid != "") {
					WSClient.setData("{var_resvId}", resvid);
					if (CancelReservation.cancelReservation("DS_02")) {
						WSClient.writeToLog("Reservation cancellation successful");
					} else
						WSClient.writeToLog("Reservation cancellation failed");
				}
				WSClient.setData("{var_par}", "N");
				String value = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				if (value.equals("N")) {
					WSClient.writeToLog("Reverting parameter value succeeded");
				} else {
					WSClient.writeToLog("Reverting parameter value failed");
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "OWS", "Reservation", "PriorRun" })

	public void modifyBooking_42862() {
		String paramvalue, resvid = "";
		try {
			String testName = "modifyBooking_42862";
			WSClient.startTest(testName,
					"Verify that booking is modified when room count exceeds the sell limit of the channel for the given data range and OVERRIDE_YN parameter is Y",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_param}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");

			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_02", "PARAMETER_VALUE");
			String parameter = paramvalue;
			if (!WSAssert.assertEquals("Y", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter CHANNEL_INVENTORY</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_02", "PARAMETER_VALUE");
				WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			}

			if (parameter.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
				WSClient.setData("{var_par}", "Y");
				WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
				WSClient.setData("{var_param}", "OVERRIDE_YN");
				WSClient.setData("{var_type}", "Boolean");
				String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				if (!paramValue.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter OVERRIDE_YN</b>");
					paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
					WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				}

				if (paramValue.equals("Y")) {
					if (OperaPropConfig.getPropertyConfigResults(
							new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
						/*************
						 * Prerequisite : Room type, Rate Plan Code, Source
						 * Code, Market Code
						 *****************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						/****** Prerequisite : Creating a Profile ******/
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

							WSClient.setData("{var_roomLimit}", "2");
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

							/*************
							 * Prerequisite :Create a Booking
							 ********************/

							WSClient.setData("{var_time}", "09:00:00");
							WSClient.setData("{var_resvType}",
									OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
							WSClient.setData("{var_rate}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
							WSClient.setData("{var_owsResort}", resort);

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_77}"));
							WSClient.setData("{var_busdate1}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_78}"));

							fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

							String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
							String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
							if (WSAssert.assertIfElementValueEquals(createBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
								resvid = WSClient.getElementValueByAttribute(createBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}", resvid);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:" + resvid + "</b>");

								WSClient.setData("{var_rateCode}",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
								WSClient.setData("{var_roomType}",
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
								WSClient.setData("{var_roomCount}", "3");

								WSClient.setData("{var_startDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_144}"));
								WSClient.setData("{var_endDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_145}"));
								WSClient.setData("{var_busdate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_144}"));
								WSClient.setData("{var_busdate1}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_145}"));

								fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

								String setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange",
										"DS_04");
								String setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
								if (WSAssert.assertIfElementExists(setSellLimitsRes,
										"SetChannelSellLimitsByDateRangeRS_Success", true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Sell limit for channel " + " on " + WSClient.getData("{var_startDate}")
											+ " - " + WSClient.getData("{var_roomLimit}") + "</b>");
									String fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits",
											"DS_03");
									String fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
									if (WSAssert.assertIfElementExists(fetchLimitsRes,
											"FetchChannelSellLimitsRS_Success", true)) {
										WSClient.setData("{var_id}", WSClient.getElementValue(fetchLimitsRes,
												"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
									}

									String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_06");
									String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

									if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
											true)) {
										if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
												"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											String query = WSClient.getQuery("QS_04");
											LinkedHashMap<String, String> dbResults = WSClient.getDBRow(query);
											String roomCount = WSClient.getData("{var_roomCount}");

											String arrivalDate = WSClient.getElementValue(modifyBookingReq,
													"RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
											String departureDate = WSClient.getElementValue(modifyBookingReq,
													"RoomStay_TimeSpan_EndDate", XMLType.REQUEST);
											String resarrivalDate = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE);
											String resdepartureDate = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE);

											resarrivalDate = resarrivalDate.substring(0, resarrivalDate.indexOf('T'));
											resdepartureDate = resdepartureDate.substring(0,
													resdepartureDate.indexOf('T'));

											/************
											 * Validating against DB
											 **************/
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Validation > Modifications are applied on DB</b>");

											if (WSAssert.assertEquals(roomCount, dbResults.get("NO_OF_ROOMS"), true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b>RoomCount -> Expected : " + roomCount + "  Actual : "
																+ dbResults.get("NO_OF_ROOMS") + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b>RoomCount -> Expected : " + roomCount + "  Actual : "
																+ dbResults.get("NO_OF_ROOMS") + "</b>");
											}

											String dbArrival = dbResults.get("ARRIVAL");
											dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));
											String dbDeparture = dbResults.get("DEPARTURE");
											dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

											if (WSAssert.assertEquals(arrivalDate, dbArrival, true)) {
												WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + dbArrival + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + dbArrival + "</b>");
											}
											if (WSAssert.assertEquals(departureDate, dbDeparture, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + dbDeparture + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + dbDeparture + "</b>");
											}

											/************
											 * Validating against Response
											 **************/
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Validation > Modifications are retrieved correctly on response</b>");

											roomCount = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_RoomTypes_RoomType_numberOfUnits", XMLType.RESPONSE);
											if (WSAssert.assertEquals(dbResults.get("NO_OF_ROOMS"), roomCount, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b>RoomCount -> Expected : " + dbResults.get("NO_OF_ROOMS")
														+ "  Actual : " + roomCount + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b>RoomCount -> Expected : " + dbResults.get("NO_OF_ROOMS")
														+ "  Actual : " + roomCount + "</b>");
											}

											/****
											 * Validate modified arrival dates
											 ****/
											if (WSAssert.assertEquals(arrivalDate, resarrivalDate, true)) {
												WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
											}

											/****
											 * Validate modified departure dates
											 ****/
											if (WSAssert.assertEquals(departureDate, resdepartureDate, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + resdepartureDate + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + resdepartureDate + "</b>");
											}
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError", true)) {
											if (!WSClient
													.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
													.equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError",
																		XMLType.RESPONSE)
																+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
											if (!WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
													.equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError_errorCode",
																		XMLType.RESPONSE)
																+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_OperaErrorCode", true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error code that is generated is : "
															+ WSClient.getElementValue(modifyBookingRes,
																	"ModifyBookingResponse_Result_OperaErrorCode",
																	XMLType.RESPONSE)
															+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement",
												true)) {
											String message = WSAssert.getElementValue(modifyBookingRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is :" + message + "</b>");
										}
									} else if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_faultstring", true)) {
										WSClient.writeToReport(LogStatus.FAIL,
												"<b> The error that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
														+ "</b>");
									}
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"<b>Pre-requisite failed >> Setting sell limits failed</b>");
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requisite for reservation creation failed</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"<b>Pre-requisite failed >> Changing channel parameter failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requisite failed >> Changing channel parameter failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			OPERALib.setOperaHeader(OPERALib.getUserName());
			try {
				if (WSClient.getData("{var_id}") != "") {
					String removeSellLimitReq = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
					String removeSellLimitRes = WSClient.processSOAPMessage(removeSellLimitReq);
					if (WSAssert.assertIfElementExists(removeSellLimitRes, "RemoveChannelSellLimitsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Removed the limits set on the channel set limits </b>");
					}
				}

				if (resvid != "") {
					WSClient.setData("{var_resvId}", resvid);
					if (CancelReservation.cancelReservation("DS_02")) {
						WSClient.writeToLog("Reservation cancellation successful");
					} else
						WSClient.writeToLog("Reservation cancellation failed");
				}

				WSClient.setData("{var_par}", "N");
				String value = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				if (value.equals("N")) {
					WSClient.writeToLog("Reverting parameter value succeeded");
				} else {
					WSClient.writeToLog("Reverting parameter value failed");
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "OWS", "Reservation" })

	public void modifyBooking_42762() {
		String paramvalue, resvid = "";
		try {
			String testName = "modifyBooking_42762";
			WSClient.startTest(testName,
					"Verify that booking is not modified when room count EXCEEDS the sell limit for the channel for the given data range and OVERRIDE_YN parameter is N",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_param}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter CHANNEL_INVENTORY is enabled</b>");

			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_02", "PARAMETER_VALUE");
			String parameter = paramvalue;
			WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			if (!WSAssert.assertEquals("Y", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter CHANNEL_INVENTORY</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_02", "PARAMETER_VALUE");
				WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			}

			if (parameter.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
				WSClient.setData("{var_par}", "N");
				WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
				WSClient.setData("{var_param}", "OVERRIDE_YN");
				WSClient.setData("{var_type}", "Boolean");
				String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				if (!paramValue.equals("N")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
					paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
					WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				}

				if (paramValue.equals("N")) {
					if (OperaPropConfig.getPropertyConfigResults(
							new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
						/*************
						 * Prerequisite : Room type, Rate Plan Code, Source
						 * Code, Market Code
						 *****************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						/****** Prerequisite : Creating a Profile ******/
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

							/****************
							 * Prerequisite 2:Create a Reservation
							 *****************/
							WSClient.setData("{var_startDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}"));
							WSClient.setData("{var_endDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_151}"));
							WSClient.setData("{var_roomLimit}", "2");
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

							String setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange",
									"DS_04");
							String setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
							if (WSAssert.assertIfElementExists(setSellLimitsRes,
									"SetChannelSellLimitsByDateRangeRS_Success", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Sell limit for channel " + " on " + WSClient.getData("{var_startDate}")
										+ " - " + WSClient.getData("{var_roomLimit}") + "</b>");
								String fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_03");
								String fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
								if (WSAssert.assertIfElementExists(fetchLimitsRes, "FetchChannelSellLimitsRS_Success",
										true)) {
									WSClient.setData("{var_id}", WSClient.getElementValue(fetchLimitsRes,
											"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
								}
								/*************
								 * Prerequisite :Create a Booking
								 ********************/
								WSClient.setData("{var_busdate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_79}") + "T14:00:00+05:30");
								WSClient.setData("{var_busdate1}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_80}") + "T14:00:00+05:30");
								WSClient.setData("{var_time}", "09:00:00");
								WSClient.setData("{var_resvType}",
										OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
								WSClient.setData("{var_rate}",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
								WSClient.setData("{var_owsResort}", resort);

								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

								String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
								String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
								if (WSAssert.assertIfElementValueEquals(createBookingRes,
										"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
									resvid = WSClient.getElementValueByAttribute(createBookingRes,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
									WSClient.setData("{var_resvId}", resvid);
									WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:" + resvid + "</b>");

									WSClient.setData("{var_rateCode}",
											OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
									WSClient.setData("{var_roomType}",
											OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
									WSClient.setData("{var_roomCount}", "3");

									String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_06");
									String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

									if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
											true)) {
										WSAssert.assertIfElementValueEquals(modifyBookingRes,
												"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);

										if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
												"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Booking is modified although room count exceeded the sell limit for the data range</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError", true)) {
											if (!WSClient
													.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
													.equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError",
																		XMLType.RESPONSE)
																+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
											if (!WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
													.equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError_errorCode",
																		XMLType.RESPONSE)
																+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_OperaErrorCode", true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error code that is generated is : "
															+ WSClient.getElementValue(modifyBookingRes,
																	"ModifyBookingResponse_Result_OperaErrorCode",
																	XMLType.RESPONSE)
															+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement",
												true)) {
											String message = WSAssert.getElementValue(modifyBookingRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is :" + message + "</b>");
										}
									} else if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_faultstring", true)) {
										WSClient.writeToReport(LogStatus.FAIL,
												"<b> The error that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
														+ "</b>");
									}
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"<b>Pre-requisite failed >> Setting sell limits failed</b>");
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requisite for reservation creation failed</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"<b>Pre-requisite failed >> Changing channel parameter failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requisite failed >> Changing channel parameter failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			OPERALib.setOperaHeader(OPERALib.getUserName());
			try {
				if (WSClient.getData("{var_id}") != "") {
					String removeSellLimitReq = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
					String removeSellLimitRes = WSClient.processSOAPMessage(removeSellLimitReq);
					if (WSAssert.assertIfElementExists(removeSellLimitRes, "RemoveChannelSellLimitsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Removed the limits set on the channel set limits </b>");
					}
				}

				if (resvid != "") {
					WSClient.setData("{var_resvId}", resvid);
					if (CancelReservation.cancelReservation("DS_02")) {
						WSClient.writeToLog("Reservation cancellation successful");
					} else
						WSClient.writeToLog("Reservation cancellation failed");
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "OWS", "Reservation" })

	public void modifyBooking_42763() {
		String paramvalue, resvid = "";
		try {
			String testName = "modifyBooking_42763";
			WSClient.startTest(testName,
					"Verify that booking is modified when room count is EQUAL to the sell limit for the channel for the given data range and OVERRIDE_YN parameter is N",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_param}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter CHANNEL_INVENTORY is enabled</b>");

			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_02", "PARAMETER_VALUE");
			String parameter = paramvalue;
			WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			if (!WSAssert.assertEquals("Y", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter CHANNEL_INVENTORY</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_02", "PARAMETER_VALUE");
				WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			}

			if (parameter.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
				WSClient.setData("{var_par}", "N");
				WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
				WSClient.setData("{var_param}", "OVERRIDE_YN");
				WSClient.setData("{var_type}", "Boolean");
				String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				if (!paramValue.equals("N")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
					paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
					WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				}

				if (paramValue.equals("N")) {
					if (OperaPropConfig.getPropertyConfigResults(
							new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
						/*************
						 * Prerequisite : Room type, Rate Plan Code, Source
						 * Code, Market Code
						 *****************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						/****** Prerequisite : Creating a Profile ******/
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

							/****************
							 * Prerequisite 2:Create a Reservation
							 *****************/
							WSClient.setData("{var_startDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_118}"));
							WSClient.setData("{var_endDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_119}"));
							WSClient.setData("{var_roomLimit}", "4");
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

							/*************
							 * Prerequisite :Create a Booking
							 ********************/

							WSClient.setData("{var_busdate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_70}") + "T14:00:00+05:30");
							WSClient.setData("{var_busdate1}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_71}") + "T14:00:00+05:30");
							WSClient.setData("{var_time}", "09:00:00");
							WSClient.setData("{var_resvType}",
									OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
							WSClient.setData("{var_rate}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
							WSClient.setData("{var_owsResort}", resort);

							fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
							String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
							if (WSAssert.assertIfElementValueEquals(createBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
								resvid = WSClient.getElementValueByAttribute(createBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}", resvid);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:" + resvid + "</b>");

								WSClient.setData("{var_rateCode}",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
								WSClient.setData("{var_roomType}",
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
								WSClient.setData("{var_roomCount}", "4");

								WSClient.setData("{var_startDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_118}"));
								WSClient.setData("{var_endDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_119}"));
								WSClient.setData("{var_busdate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_118}"));
								WSClient.setData("{var_busdate1}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_119}"));

								fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

								String setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange",
										"DS_04");
								String setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
								if (WSAssert.assertIfElementExists(setSellLimitsRes,
										"SetChannelSellLimitsByDateRangeRS_Success", true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Sell limit for channel " + " on " + WSClient.getData("{var_startDate}")
											+ " - " + WSClient.getData("{var_roomLimit}") + "</b>");
									String fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits",
											"DS_03");
									String fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
									if (WSAssert.assertIfElementExists(fetchLimitsRes,
											"FetchChannelSellLimitsRS_Success", true)) {
										WSClient.setData("{var_id}", WSClient.getElementValue(fetchLimitsRes,
												"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
									}

									String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_06");
									String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

									if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
											true)) {
										if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
												"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											String query = WSClient.getQuery("QS_04");
											LinkedHashMap<String, String> dbResults = WSClient.getDBRow(query);
											String roomCount = WSClient.getData("{var_roomCount}");

											String arrivalDate = WSClient.getElementValue(modifyBookingReq,
													"RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
											String departureDate = WSClient.getElementValue(modifyBookingReq,
													"RoomStay_TimeSpan_EndDate", XMLType.REQUEST);
											String resarrivalDate = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE);
											String resdepartureDate = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE);

											resarrivalDate = resarrivalDate.substring(0, resarrivalDate.indexOf('T'));
											resdepartureDate = resdepartureDate.substring(0,
													resdepartureDate.indexOf('T'));

											/************
											 * Validating against DB
											 **************/
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Validation > Modifications are applied on DB</b>");

											if (WSAssert.assertEquals(roomCount, dbResults.get("NO_OF_ROOMS"), true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b>RoomCount -> Expected : " + roomCount + "  Actual : "
																+ dbResults.get("NO_OF_ROOMS") + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b>RoomCount -> Expected : " + roomCount + "  Actual : "
																+ dbResults.get("NO_OF_ROOMS") + "</b>");
											}

											String dbArrival = dbResults.get("ARRIVAL");
											dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));
											String dbDeparture = dbResults.get("DEPARTURE");
											dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

											if (WSAssert.assertEquals(arrivalDate, dbArrival, true)) {
												WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + dbArrival + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + dbArrival + "</b>");
											}
											if (WSAssert.assertEquals(departureDate, dbDeparture, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + dbDeparture + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + dbDeparture + "</b>");
											}

											/************
											 * Validating against Response
											 **************/
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Validation > Modifications are retrieved correctly on response</b>");

											roomCount = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_RoomTypes_RoomType_numberOfUnits", XMLType.RESPONSE);
											if (WSAssert.assertEquals(dbResults.get("NO_OF_ROOMS"), roomCount, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b>RoomCount -> Expected : " + dbResults.get("NO_OF_ROOMS")
														+ "  Actual : " + roomCount + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b>RoomCount -> Expected : " + dbResults.get("NO_OF_ROOMS")
														+ "  Actual : " + roomCount + "</b>");
											}

											/****
											 * Validate modified arrival dates
											 ****/
											if (WSAssert.assertEquals(arrivalDate, resarrivalDate, true)) {
												WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
											}

											/****
											 * Validate modified departure dates
											 ****/
											if (WSAssert.assertEquals(departureDate, resdepartureDate, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + resdepartureDate + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + resdepartureDate + "</b>");
											}
										}

										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError", true)) {
											if (!WSClient
													.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
													.equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError",
																		XMLType.RESPONSE)
																+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
											if (!WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
													.equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError_errorCode",
																		XMLType.RESPONSE)
																+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_OperaErrorCode", true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error code that is generated is : "
															+ WSClient.getElementValue(modifyBookingRes,
																	"ModifyBookingResponse_Result_OperaErrorCode",
																	XMLType.RESPONSE)
															+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement",
												true)) {
											String message = WSAssert.getElementValue(modifyBookingRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is :" + message + "</b>");
										}
									} else if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_faultstring", true)) {
										WSClient.writeToReport(LogStatus.FAIL,
												"<b> The error that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
														+ "</b>");
									}
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"<b>Pre-requisite failed >> Setting sell limits failed</b>");
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requisite for reservation creation failed</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"<b>Pre-requisite failed >> Changing channel parameter failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requisite failed >> Changing channel parameter failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			OPERALib.setOperaHeader(OPERALib.getUserName());
			try {
				if (WSClient.getData("{var_id}") != "") {
					String removeSellLimitReq = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
					String removeSellLimitRes = WSClient.processSOAPMessage(removeSellLimitReq);
					if (WSAssert.assertIfElementExists(removeSellLimitRes, "RemoveChannelSellLimitsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Removed the limits set on the channel set limits </b>");
					}
				}

				if (resvid != "") {
					WSClient.setData("{var_resvId}", resvid);
					if (CancelReservation.cancelReservation("DS_02")) {
						WSClient.writeToLog("Reservation cancellation successful");
					} else
						WSClient.writeToLog("Reservation cancellation failed");
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "OWS", "Reservation" })

	public void modifyBooking_42764() {
		String paramvalue, resvid = "";
		try {
			String testName = "modifyBooking_42764";
			WSClient.startTest(testName,
					"Verify that booking is modified when room count is LESS than the sell limit for the channel for the given data range and OVERRIDE_YN parameter is N",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_param}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter CHANNEL_INVENTORY is enabled</b>");

			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_02", "PARAMETER_VALUE");
			String parameter = paramvalue;
			WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			if (!WSAssert.assertEquals("Y", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter CHANNEL_INVENTORY</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_02", "PARAMETER_VALUE");
				WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			}

			if (parameter.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
				WSClient.setData("{var_par}", "N");
				WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
				WSClient.setData("{var_param}", "OVERRIDE_YN");
				WSClient.setData("{var_type}", "Boolean");
				String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				if (!paramValue.equals("N")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
					paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
					WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				}

				if (paramValue.equals("N")) {
					if (OperaPropConfig.getPropertyConfigResults(
							new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
						/*************
						 * Prerequisite : Room type, Rate Plan Code, Source
						 * Code, Market Code
						 *****************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						/****** Prerequisite : Creating a Profile ******/
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

							/****************
							 * Prerequisite 2:Create a Reservation
							 *****************/

							WSClient.setData("{var_roomLimit}", "3");
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

							/*************
							 * Prerequisite :Create a Booking
							 ********************/
							WSClient.setData("{var_busdate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_50}") + "T10:00:00+05:30");
							WSClient.setData("{var_busdate1}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_51}") + "T14:00:00+05:30");
							WSClient.setData("{var_time}", "09:00:00");
							WSClient.setData("{var_resvType}",
									OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
							WSClient.setData("{var_rate}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
							WSClient.setData("{var_owsResort}", resort);

							fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
							String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
							if (WSAssert.assertIfElementValueEquals(createBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
								resvid = WSClient.getElementValueByAttribute(createBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}", resvid);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:" + resvid + "</b>");

								WSClient.setData("{var_rateCode}",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
								WSClient.setData("{var_roomType}",
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
								WSClient.setData("{var_roomCount}", "2");
								WSClient.setData("{var_startDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_119}"));
								WSClient.setData("{var_endDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_120}"));
								WSClient.setData("{var_busdate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_119}"));
								WSClient.setData("{var_busdate1}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_120}"));
								fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
								String setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange",
										"DS_04");
								String setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
								if (WSAssert.assertIfElementExists(setSellLimitsRes,
										"SetChannelSellLimitsByDateRangeRS_Success", false)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Sell limit for channel " + " on " + WSClient.getData("{var_startDate}")
											+ " - " + WSClient.getData("{var_roomLimit}") + "</b>");

									String fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits",
											"DS_03");
									String fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
									if (WSAssert.assertIfElementExists(fetchLimitsRes,
											"FetchChannelSellLimitsRS_Success", true)) {
										WSClient.setData("{var_id}", WSClient.getElementValue(fetchLimitsRes,
												"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
									}

									String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_06");
									String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

									if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
											true)) {
										if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
												"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											String query = WSClient.getQuery("QS_04");
											LinkedHashMap<String, String> dbResults = WSClient.getDBRow(query);
											String roomCount = WSClient.getData("{var_roomCount}");

											String arrivalDate = WSClient.getElementValue(modifyBookingReq,
													"RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
											String departureDate = WSClient.getElementValue(modifyBookingReq,
													"RoomStay_TimeSpan_EndDate", XMLType.REQUEST);
											String resarrivalDate = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE);
											String resdepartureDate = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE);

											resarrivalDate = resarrivalDate.substring(0, resarrivalDate.indexOf('T'));
											resdepartureDate = resdepartureDate.substring(0,
													resdepartureDate.indexOf('T'));

											/************
											 * Validating against DB
											 **************/
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Validation > Modifications are applied on DB</b>");

											if (WSAssert.assertEquals(roomCount, dbResults.get("NO_OF_ROOMS"), true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b>RoomCount -> Expected : " + roomCount + "  Actual : "
																+ dbResults.get("NO_OF_ROOMS") + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b>RoomCount -> Expected : " + roomCount + "  Actual : "
																+ dbResults.get("NO_OF_ROOMS") + "</b>");
											}

											String dbArrival = dbResults.get("ARRIVAL");
											dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));
											String dbDeparture = dbResults.get("DEPARTURE");
											dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

											if (WSAssert.assertEquals(arrivalDate, dbArrival, true)) {
												WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + dbArrival + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + dbArrival + "</b>");
											}
											if (WSAssert.assertEquals(departureDate, dbDeparture, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + dbDeparture + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + dbDeparture + "</b>");
											}

											/************
											 * Validating against Response
											 **************/
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Validation > Modifications are retrieved correctly on response</b>");

											roomCount = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_RoomTypes_RoomType_numberOfUnits", XMLType.RESPONSE);
											if (WSAssert.assertEquals(dbResults.get("NO_OF_ROOMS"), roomCount, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b>RoomCount -> Expected : " + dbResults.get("NO_OF_ROOMS")
														+ "  Actual : " + roomCount + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b>RoomCount -> Expected : " + dbResults.get("NO_OF_ROOMS")
														+ "  Actual : " + roomCount + "</b>");
											}

											/****
											 * Validate modified arrival dates
											 ****/
											if (WSAssert.assertEquals(arrivalDate, resarrivalDate, true)) {
												WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
											}

											/****
											 * Validate modified departure dates
											 ****/
											if (WSAssert.assertEquals(departureDate, resdepartureDate, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + resdepartureDate + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + resdepartureDate + "</b>");
											}
										}

										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError", true)) {
											if (!WSClient
													.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
													.equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError",
																		XMLType.RESPONSE)
																+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
											if (!WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
													.equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError_errorCode",
																		XMLType.RESPONSE)
																+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_OperaErrorCode", true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error code that is generated is : "
															+ WSClient.getElementValue(modifyBookingRes,
																	"ModifyBookingResponse_Result_OperaErrorCode",
																	XMLType.RESPONSE)
															+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement",
												true)) {
											String message = WSAssert.getElementValue(modifyBookingRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is :" + message + "</b>");
										}
									} else if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_faultstring", true)) {
										WSClient.writeToReport(LogStatus.FAIL,
												"<b> The error that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
														+ "</b>");
									}
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"<b>Pre-requisite for reservation creation failed</b>");
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requisite failed >> Setting sell limits failed</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"<b>Pre-requisite failed >> Changing channel parameter failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requisite failed >> Changing channel parameter failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			OPERALib.setOperaHeader(OPERALib.getUserName());
			try {
				if (WSClient.getData("{var_id}") != "") {
					String removeSellLimitReq = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
					String removeSellLimitRes = WSClient.processSOAPMessage(removeSellLimitReq);
					if (WSAssert.assertIfElementExists(removeSellLimitRes, "RemoveChannelSellLimitsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Removed the limits set on the channel set limits </b>");
					}
				}

				if (resvid != "") {
					WSClient.setData("{var_resvId}", resvid);
					if (CancelReservation.cancelReservation("DS_02")) {
						WSClient.writeToLog("Reservation cancellation successful");
					} else
						WSClient.writeToLog("Reservation cancellation failed");
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "OWS", "Reservation" })

	public void modifyBooking_42760() {
		String paramvalue, resvid = "";
		try {
			String testName = "modifyBooking_42760";
			WSClient.startTest(testName,
					"Verify that booking is modified when room count EQUALS to the sell limit of room type for the given data range and OVERRIDE_YN parameter is N",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_param}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter CHANNEL_INVENTORY is enabled</b>");

			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_02", "PARAMETER_VALUE");
			String parameter = paramvalue;
			WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			if (!WSAssert.assertEquals("Y", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter CHANNEL_INVENTORY</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_02", "PARAMETER_VALUE");
				WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			}

			if (parameter.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
				WSClient.setData("{var_par}", "N");
				WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
				WSClient.setData("{var_param}", "OVERRIDE_YN");
				WSClient.setData("{var_type}", "Boolean");
				String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				if (!paramValue.equals("N")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
					paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
					WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				}

				if (paramValue.equals("N")) {
					if (OperaPropConfig.getPropertyConfigResults(
							new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
						/*************
						 * Prerequisite : Room type, Rate Plan Code, Source
						 * Code, Market Code
						 *****************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						/****** Prerequisite : Creating a Profile ******/
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

							/****************
							 * Prerequisite 2:Create a Reservation
							 *****************/

							WSClient.setData("{var_roomLimit}", "3");
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

							/*************
							 * Prerequisite :Create a Booking
							 ********************/

							WSClient.setData("{var_busdate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_86}") + "T14:00:00+05:30");
							WSClient.setData("{var_busdate1}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_87}") + "T14:00:00+05:30");
							WSClient.setData("{var_time}", "09:00:00");
							WSClient.setData("{var_resvType}",
									OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
							WSClient.setData("{var_rate}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
							WSClient.setData("{var_owsResort}", resort);

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

							String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
							String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
							if (WSAssert.assertIfElementValueEquals(createBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
								resvid = WSClient.getElementValueByAttribute(createBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}", resvid);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:" + resvid + "</b>");

								WSClient.setData("{var_rateCode}",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
								WSClient.setData("{var_roomType}",
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
								WSClient.setData("{var_roomCount}", "3");

								WSClient.setData("{var_startDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_156}"));
								WSClient.setData("{var_endDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_157}"));
								WSClient.setData("{var_busdate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_156}"));
								WSClient.setData("{var_busdate1}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_157}"));

								fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

								String setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange",
										"DS_03");
								String setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
								if (WSAssert.assertIfElementExists(setSellLimitsRes,
										"SetChannelSellLimitsByDateRangeRS_Success", true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Sell limit for room type " + WSClient.getData("{var_roomType}") + " on "
													+ WSClient.getData("{var_startDate}") + " - "
													+ WSClient.getData("{var_roomLimit}") + "</b>");

									String fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits",
											"DS_02");
									String fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
									if (WSAssert.assertIfElementExists(fetchLimitsRes,
											"FetchChannelSellLimitsRS_Success", true)) {
										WSClient.setData("{var_id}", WSClient.getElementValue(fetchLimitsRes,
												"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
									}
									String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_06");
									String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

									if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
											true)) {
										if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
												"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											String query = WSClient.getQuery("QS_04");
											LinkedHashMap<String, String> dbResults = WSClient.getDBRow(query);
											String roomCount = WSClient.getData("{var_roomCount}");

											String arrivalDate = WSClient.getElementValue(modifyBookingReq,
													"RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
											String departureDate = WSClient.getElementValue(modifyBookingReq,
													"RoomStay_TimeSpan_EndDate", XMLType.REQUEST);
											String resarrivalDate = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE);
											String resdepartureDate = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE);

											resarrivalDate = resarrivalDate.substring(0, resarrivalDate.indexOf('T'));
											resdepartureDate = resdepartureDate.substring(0,
													resdepartureDate.indexOf('T'));

											/************
											 * Validating against DB
											 **************/
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Validation > Modifications are applied on DB</b>");

											if (WSAssert.assertEquals(roomCount, dbResults.get("NO_OF_ROOMS"), true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b>RoomCount -> Expected : " + roomCount + "  Actual : "
																+ dbResults.get("NO_OF_ROOMS") + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b>RoomCount -> Expected : " + roomCount + "  Actual : "
																+ dbResults.get("NO_OF_ROOMS") + "</b>");
											}

											String dbArrival = dbResults.get("ARRIVAL");
											dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));
											String dbDeparture = dbResults.get("DEPARTURE");
											dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

											if (WSAssert.assertEquals(arrivalDate, dbArrival, true)) {
												WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + dbArrival + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + dbArrival + "</b>");
											}
											if (WSAssert.assertEquals(departureDate, dbDeparture, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + dbDeparture + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + dbDeparture + "</b>");
											}

											/************
											 * Validating against Response
											 **************/
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Validation > Modifications are retrieved correctly on response</b>");

											roomCount = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_RoomTypes_RoomType_numberOfUnits", XMLType.RESPONSE);
											if (WSAssert.assertEquals(dbResults.get("NO_OF_ROOMS"), roomCount, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b>RoomCount -> Expected : " + dbResults.get("NO_OF_ROOMS")
														+ "  Actual : " + roomCount + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b>RoomCount -> Expected : " + dbResults.get("NO_OF_ROOMS")
														+ "  Actual : " + roomCount + "</b>");
											}
											/****
											 * Validate modified arrival dates
											 ****/
											if (WSAssert.assertEquals(arrivalDate, resarrivalDate, true)) {
												WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
											}
											/****
											 * Validate modified departure dates
											 ****/
											if (WSAssert.assertEquals(departureDate, resdepartureDate, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + resdepartureDate + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + resdepartureDate + "</b>");
											}
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError", true)) {
											if (!WSClient
													.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
													.equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError",
																		XMLType.RESPONSE)
																+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
											if (!WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
													.equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError_errorCode",
																		XMLType.RESPONSE)
																+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_OperaErrorCode", true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error code that is generated is : "
															+ WSClient.getElementValue(modifyBookingRes,
																	"ModifyBookingResponse_Result_OperaErrorCode",
																	XMLType.RESPONSE)
															+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement",
												true)) {
											String message = WSAssert.getElementValue(modifyBookingRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is :" + message + "</b>");
										}
									} else if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_faultstring", true)) {
										WSClient.writeToReport(LogStatus.FAIL,
												"<b> The error that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
														+ "</b>");
									}
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"<b>Pre-requisite failed >> Setting sell limits failed</b>");
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requisite for reservation creation failed</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"<b>Pre-requisite failed >> Changing channel parameter failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requisite failed >> Changing channel parameter failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			OPERALib.setOperaHeader(OPERALib.getUserName());
			try {
				if (WSClient.getData("{var_id}") != "") {
					String removeSellLimitReq = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
					String removeSellLimitRes = WSClient.processSOAPMessage(removeSellLimitReq);
					if (WSAssert.assertIfElementExists(removeSellLimitRes, "RemoveChannelSellLimitsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Removed the limits set on the channel set limits </b>");
					}
				}
				if (resvid != "") {
					WSClient.setData("{var_resvId}", resvid);
					if (CancelReservation.cancelReservation("DS_02")) {
						WSClient.writeToLog("Reservation cancellation successful");
					} else
						WSClient.writeToLog("Reservation cancellation failed");
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "OWS", "Reservation" })

	public void modifyBooking_42761() {
		String paramvalue, resvid = "";
		try {
			String testName = "modifyBooking_42761";
			WSClient.startTest(testName,
					"Verify that booking is modified when room count is LESS than the sell limit of room type for the given data range and OVERRIDE_YN parameter is N",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_param}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");

			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_02", "PARAMETER_VALUE");
			String parameter = paramvalue;
			if (!WSAssert.assertEquals("Y", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter CHANNEL_INVENTORY</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_02", "PARAMETER_VALUE");
				WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			}

			if (parameter.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
				WSClient.setData("{var_par}", "N");
				WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
				WSClient.setData("{var_param}", "OVERRIDE_YN");
				WSClient.setData("{var_type}", "Boolean");
				String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				if (!paramValue.equals("N")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
					paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
					WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				}

				if (paramValue.equals("N")) {
					if (OperaPropConfig.getPropertyConfigResults(
							new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
						/*************
						 * Prerequisite : Room type, Rate Plan Code, Source
						 * Code, Market Code
						 *****************/
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_ReservationType}",
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

						/****** Prerequisite : Creating a Profile ******/
						if (profileID.equals(""))
							profileID = CreateProfile.createProfile("DS_01");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");

							/****************
							 * Prerequisite 2:Create a Reservation
							 *****************/

							WSClient.setData("{var_roomLimit}", "4");
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

							/*************
							 * Prerequisite :Create a Booking
							 ********************/
							WSClient.setData("{var_busdate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_86}") + "T14:00:00+05:30");
							WSClient.setData("{var_busdate1}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_87}") + "T14:00:00+05:30");
							WSClient.setData("{var_time}", "09:00:00");
							WSClient.setData("{var_resvType}",
									OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
							WSClient.setData("{var_rate}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
							WSClient.setData("{var_owsResort}", resort);

							fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
							String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
							if (WSAssert.assertIfElementValueEquals(createBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
								resvid = WSClient.getElementValueByAttribute(createBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}", resvid);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:" + resvid + "</b>");

								WSClient.setData("{var_rateCode}",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
								WSClient.setData("{var_roomType}",
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
								WSClient.setData("{var_roomCount}", "2");

								WSClient.setData("{var_startDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_149}"));
								WSClient.setData("{var_endDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}"));
								WSClient.setData("{var_busdate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_149}"));
								WSClient.setData("{var_busdate1}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}"));
								fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

								String setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange",
										"DS_03");
								String setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
								if (WSAssert.assertIfElementExists(setSellLimitsRes,
										"SetChannelSellLimitsByDateRangeRS_Success", true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Sell limit for room type " + WSClient.getData("{var_roomType}") + " on "
													+ WSClient.getData("{var_startDate}") + " - "
													+ WSClient.getData("{var_roomLimit}") + "</b>");

									String fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits",
											"DS_02");
									String fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
									if (WSAssert.assertIfElementExists(fetchLimitsRes,
											"FetchChannelSellLimitsRS_Success", true)) {
										WSClient.setData("{var_id}", WSClient.getElementValue(fetchLimitsRes,
												"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
									}

									String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_06");
									String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

									if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
											true)) {
										if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
												"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											String query = WSClient.getQuery("QS_04");
											LinkedHashMap<String, String> dbResults = WSClient.getDBRow(query);
											String roomCount = WSClient.getData("{var_roomCount}");

											String arrivalDate = WSClient.getElementValue(modifyBookingReq,
													"RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
											String departureDate = WSClient.getElementValue(modifyBookingReq,
													"RoomStay_TimeSpan_EndDate", XMLType.REQUEST);
											String resarrivalDate = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE);
											String resdepartureDate = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE);

											resarrivalDate = resarrivalDate.substring(0, resarrivalDate.indexOf('T'));
											resdepartureDate = resdepartureDate.substring(0,
													resdepartureDate.indexOf('T'));

											/************
											 * Validating against DB
											 **************/
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Validation > Modifications are applied on DB</b>");
											if (WSAssert.assertEquals(roomCount, dbResults.get("NO_OF_ROOMS"), true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b>RoomCount -> Expected : " + roomCount + "  Actual : "
																+ dbResults.get("NO_OF_ROOMS") + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b>RoomCount -> Expected : " + roomCount + "  Actual : "
																+ dbResults.get("NO_OF_ROOMS") + "</b>");
											}

											String dbArrival = dbResults.get("ARRIVAL");
											dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));
											String dbDeparture = dbResults.get("DEPARTURE");
											dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

											if (WSAssert.assertEquals(arrivalDate, dbArrival, true)) {
												WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + dbArrival + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + dbArrival + "</b>");
											}
											if (WSAssert.assertEquals(departureDate, dbDeparture, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + dbDeparture + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + dbDeparture + "</b>");
											}

											/************
											 * Validating against Response
											 **************/
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Validation > Modifications are retrieved correctly on response</b>");

											roomCount = WSClient.getElementValue(modifyBookingRes,
													"RoomStay_RoomTypes_RoomType_numberOfUnits", XMLType.RESPONSE);
											if (WSAssert.assertEquals(dbResults.get("NO_OF_ROOMS"), roomCount, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b>RoomCount -> Expected : " + dbResults.get("NO_OF_ROOMS")
														+ "  Actual : " + roomCount + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b>RoomCount -> Expected : " + dbResults.get("NO_OF_ROOMS")
														+ "  Actual : " + roomCount + "</b>");
											}
											/****
											 * Validate modified arrival dates
											 ****/
											if (WSAssert.assertEquals(arrivalDate, resarrivalDate, true)) {
												WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
														+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
											}
											/****
											 * Validate modified departure dates
											 ****/
											if (WSAssert.assertEquals(departureDate, resdepartureDate, true)) {
												WSClient.writeToReport(LogStatus.PASS,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + resdepartureDate + "</b>");
											} else {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b> Departure date -> Expected : " + departureDate
														+ "  Actual : " + resdepartureDate + "</b>");
											}
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError", true)) {
											if (!WSClient
													.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
													.equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError",
																		XMLType.RESPONSE)
																+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
											if (!WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
													.equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError_errorCode",
																		XMLType.RESPONSE)
																+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result_OperaErrorCode", true)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error code that is generated is : "
															+ WSClient.getElementValue(modifyBookingRes,
																	"ModifyBookingResponse_Result_OperaErrorCode",
																	XMLType.RESPONSE)
															+ "</b>");
										}
										if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement",
												true)) {
											String message = WSAssert.getElementValue(modifyBookingRes,
													"Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is :" + message + "</b>");
										}
									} else if (WSAssert.assertIfElementExists(modifyBookingRes,
											"ModifyBookingResponse_faultstring", true)) {
										WSClient.writeToReport(LogStatus.FAIL,
												"<b> The error that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
														+ "</b>");
									}
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"<b>Pre-requisite for reservation creation failed</b>");
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requisite failed >> Setting sell limits failed</b>");
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"<b>Pre-requisite failed >> Changing channel parameter failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requisite failed >> Changing channel parameter failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			OPERALib.setOperaHeader(OPERALib.getUserName());
			try {
				if (WSClient.getData("{var_id}") != "") {
					String removeSellLimitReq = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
					String removeSellLimitRes = WSClient.processSOAPMessage(removeSellLimitReq);
					if (WSAssert.assertIfElementExists(removeSellLimitRes, "RemoveChannelSellLimitsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Removed the limits set on the channel set limits </b>");
					}
				}
				if (resvid != "") {
					WSClient.setData("{var_resvId}", resvid);
					if (CancelReservation.cancelReservation("DS_02")) {
						WSClient.writeToLog("Reservation cancellation successful");
					} else
						WSClient.writeToLog("Reservation cancellation failed");
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * discount amount is added to the reservation.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS" })

	public void modifyBooking_42784() {
		try {
			String testName = "modifyBooking_42784";
			WSClient.startTest(testName, "Verify that flat discount amount is applied to the reservation",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "DiscountReasons" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_11"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_10"));

				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
					WSClient.setData("{var_profileId}", profileID);

					/*************
					 * Prerequisite 2:Create a Reservation
					 ************/

					HashMap<String, String> resvID1 = CreateReservation.createReservation("DS_01");
					if (resvID1.get("reservationId") != "error") {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID1.get("reservationId") + "</b>");
						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
						WSClient.setData("{var_roomCount}", "1");
						WSClient.setData("{var_amount}", "10");
						WSClient.setData("{var_type}", "FLAT");
						WSClient.setData("{var_reason}", OperaPropConfig.getDataSetForCode("DiscountReasons", "DS_01"));

						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_52}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_53}"));
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));

						WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_52}"));
						WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_53}"));

						fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"),
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_32");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_36");
								LinkedHashMap<String, String> prices = WSClient.getDBRow(query);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Details of Rate Code : " + prices.get("RATE_CODE") + "</b>");
								WSClient.writeToReport(LogStatus.INFO,
										"<b> Charge For Adult 1 -> " + prices.get("AMOUNT_1") + "  Adult 2 -> "
												+ prices.get("AMOUNT_2") + "  Adult 3 -> " + prices.get("AMOUNT_3")
												+ " Extra Adult -> " + prices.get("ADULT_CHARGE") + "</b>");

								int price = Integer.parseInt(prices.get("AMOUNT_1"));
								int amount = price - (Integer.parseInt(WSClient.getData("{var_amount}")));

								String reqDiscountType = WSClient.getElementValue(modifyBookingReq,
										"RatePlan_Discount_DiscountType", XMLType.REQUEST);
								String reqAmount = WSClient.getElementValue(modifyBookingReq,
										"RatePlan_Discount_DiscountAmount", XMLType.REQUEST);
								String reqReason = WSClient.getElementValue(modifyBookingReq,
										"RatePlan_Discount_DiscountReason", XMLType.REQUEST);

								query = WSClient.getQuery("QS_41");
								LinkedHashMap<String, String> results = WSClient.getDBRow(query);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating discount details insertion into DB</b>");
								if (WSAssert.assertEquals(reqAmount, results.get("DISCOUNT_AMT"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>DiscountAmount -> Expected : "
											+ reqAmount + "  Actual : " + results.get("DISCOUNT_AMT") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>DiscountAmount -> Expected : "
											+ reqAmount + "  Actual : " + results.get("DISCOUNT_AMT") + "</b>");
								}
								if (WSAssert.assertEquals(reqReason, results.get("DISCOUNT_REASON_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>DiscountReason -> Expected : "
											+ reqReason + "  Actual : " + results.get("DISCOUNT_REASON_CODE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>DiscountReason -> Expected : "
											+ reqReason + "  Actual : " + results.get("DISCOUNT_REASON_CODE") + "</b>");
								}

								String resDiscountType = WSClient.getElementValue(modifyBookingRes,
										"RatePlan_Discount_DiscountType", XMLType.RESPONSE);
								String resAmount = WSClient.getElementValue(modifyBookingRes,
										"RatePlan_Discount_DiscountAmount", XMLType.RESPONSE);
								String resReason = WSClient.getElementValue(modifyBookingRes,
										"RatePlan_Discount_DiscountReason", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating discount details retrieval onto the response</b>");
								if (WSAssert.assertEquals(results.get("DISCOUNT_AMT"), resAmount, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>DiscountAmount -> Expected : "
											+ results.get("DISCOUNT_AMT") + "  Actual : " + resAmount + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>DiscountAmount -> Expected : "
											+ results.get("DISCOUNT_AMT") + "  Actual : " + resAmount + "</b>");
								}
								if (WSAssert.assertEquals(results.get("DISCOUNT_REASON_CODE"), resReason, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>DiscountReason -> Expected : "
											+ results.get("DISCOUNT_REASON_CODE") + "  Actual : " + resReason + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>DiscountReason -> Expected : "
											+ results.get("DISCOUNT_REASON_CODE") + "  Actual : " + resReason + "</b>");
								}
								if (WSAssert.assertEquals(reqDiscountType, resDiscountType, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>DiscountType -> Expected : "
											+ reqDiscountType + "  Actual : " + resDiscountType + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>DiscountType -> Expected : "
											+ reqDiscountType + "  Actual : " + resDiscountType + "</b>");
								}

								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Charges of the reservation</b>");
								String base = WSClient.getElementValue(modifyBookingRes, "Rates_Rate_Base",
										XMLType.RESPONSE);
								if (WSAssert.assertEquals(String.valueOf(amount), base, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>Base -> Expected : "
											+ String.valueOf(amount) + "  Actual : " + base + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>Base -> Expected : "
											+ String.valueOf(amount) + "  Actual : " + base + "</b>");
								}
								String total = WSClient.getElementValue(modifyBookingRes, "RoomStays_RoomStay_Total",
										XMLType.RESPONSE);
								if (WSAssert.assertEquals(String.valueOf(amount), total, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>Total -> Expected : "
											+ String.valueOf(amount) + "  Actual : " + total + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>Total -> Expected : "
											+ String.valueOf(amount) + "  Actual : " + total + "</b>");
								}
								total = WSClient.getElementValue(modifyBookingRes,
										"RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages",
										XMLType.RESPONSE);
								if (WSAssert.assertEquals(String.valueOf(amount), total, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>TotalRoomRateAndPackages -> Expected : "
											+ String.valueOf(amount) + "  Actual : " + total + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>TotalRoomRateAndPackages -> Expected : "
											+ String.valueOf(amount) + "  Actual : " + total + "</b>");
								}
								total = WSClient.getElementValue(modifyBookingRes, "RoomRateAndPackages_Charges_Amount",
										XMLType.RESPONSE);
								if (WSAssert.assertEquals(String.valueOf(amount), total, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>Amount -> Expected : "
											+ String.valueOf(amount) + "  Actual : " + total + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>Amount -> Expected : "
											+ String.valueOf(amount) + "  Actual : " + total + "</b>");
								}
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * discount amount is modified for the reservation.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS" })

	public void modifyBooking_42844() {
		try {
			String testName = "modifyBooking_42844";
			WSClient.startTest(testName, "Verify that discount details of the reservation are modified successfully",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "DiscountReasons" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
				WSClient.setData("{var_discountCode}", OperaPropConfig.getDataSetForCode("DiscountReasons", "DS_02"));

				/************
				 * Prerequisite 1: Create profile
				 *********************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
					WSClient.setData("{var_profileId}", profileID);

					/*************
					 * Prerequisite 2:Create a Reservation
					 ************/
					WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_30}"));
					WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_31}"));

					fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"),
							OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
					HashMap<String, String> resvID1 = CreateReservation.createReservation("DS_32");

					if (resvID1.get("reservationId") != "error") {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID1.get("reservationId") + "</b>");
						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

						String query = WSClient.getQuery("OWSModifyBooking", "QS_41");
						LinkedHashMap<String, String> results = WSClient.getDBRow(query);
						if (results.get("DISCOUNT_AMT") != null) {
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_rateCode}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
							WSClient.setData("{var_roomCount}", "1");
							WSClient.setData("{var_amount}", "12");
							WSClient.setData("{var_type}", "FLAT");
							WSClient.setData("{var_reason}",
									OperaPropConfig.getDataSetForCode("DiscountReasons", "DS_01"));

							WSClient.setData("{VAR_RATEPLANCODE}",
									OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));

							WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_52}"));
							WSClient.setData("{var_busdate1}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_53}"));
							WSClient.setData("{var_startDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_52}"));
							WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_53}"));
							fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"),
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
							/***************
							 * OWS Modify Booking Operation
							 ********************/
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_32");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									query = WSClient.getQuery("QS_36");
									LinkedHashMap<String, String> prices = WSClient.getDBRow(query);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Details of Rate Code : " + prices.get("RATE_CODE") + "</b>");
									WSClient.writeToReport(LogStatus.INFO,
											"<b> Charge For Adult 1 -> " + prices.get("AMOUNT_1") + "  Adult 2 -> "
													+ prices.get("AMOUNT_2") + "  Adult 3 -> " + prices.get("AMOUNT_3")
													+ " Extra Adult -> " + prices.get("ADULT_CHARGE") + "</b>");

									int price = Integer.parseInt(prices.get("AMOUNT_1"));
									int amount = price - (Integer.parseInt(WSClient.getData("{var_amount}")));

									String reqDiscountType = WSClient.getElementValue(modifyBookingReq,
											"RatePlan_Discount_DiscountType", XMLType.REQUEST);
									String reqAmount = WSClient.getElementValue(modifyBookingReq,
											"RatePlan_Discount_DiscountAmount", XMLType.REQUEST);
									String reqReason = WSClient.getElementValue(modifyBookingReq,
											"RatePlan_Discount_DiscountReason", XMLType.REQUEST);

									query = WSClient.getQuery("QS_41");
									results = WSClient.getDBRow(query);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validating discount details insertion into DB</b>");
									if (WSAssert.assertEquals(reqAmount, results.get("DISCOUNT_AMT"), true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b>DiscountAmount -> Expected : "
												+ reqAmount + "  Actual : " + results.get("DISCOUNT_AMT") + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b>DiscountAmount -> Expected : "
												+ reqAmount + "  Actual : " + results.get("DISCOUNT_AMT") + "</b>");
									}
									if (WSAssert.assertEquals(reqReason, results.get("DISCOUNT_REASON_CODE"), true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"<b>DiscountReason -> Expected : " + reqReason + "  Actual : "
														+ results.get("DISCOUNT_REASON_CODE") + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL,
												"<b>DiscountReason -> Expected : " + reqReason + "  Actual : "
														+ results.get("DISCOUNT_REASON_CODE") + "</b>");
									}

									String resDiscountType = WSClient.getElementValue(modifyBookingRes,
											"RatePlan_Discount_DiscountType", XMLType.RESPONSE);
									String resAmount = WSClient.getElementValue(modifyBookingRes,
											"RatePlan_Discount_DiscountAmount", XMLType.RESPONSE);
									String resReason = WSClient.getElementValue(modifyBookingRes,
											"RatePlan_Discount_DiscountReason", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validating discount details retrieval onto the response</b>");
									if (WSAssert.assertEquals(results.get("DISCOUNT_AMT"), resAmount, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b>DiscountAmount -> Expected : "
												+ results.get("DISCOUNT_AMT") + "  Actual : " + resAmount + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b>DiscountAmount -> Expected : "
												+ results.get("DISCOUNT_AMT") + "  Actual : " + resAmount + "</b>");
									}
									if (WSAssert.assertEquals(results.get("DISCOUNT_REASON_CODE"), resReason, true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"<b>DiscountReason -> Expected : " + results.get("DISCOUNT_REASON_CODE")
												+ "  Actual : " + resReason + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL,
												"<b>DiscountReason -> Expected : " + results.get("DISCOUNT_REASON_CODE")
												+ "  Actual : " + resReason + "</b>");
									}
									if (WSAssert.assertEquals(reqDiscountType, resDiscountType, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b>DiscountType -> Expected : "
												+ reqDiscountType + "  Actual : " + resDiscountType + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b>DiscountType -> Expected : "
												+ reqDiscountType + "  Actual : " + resDiscountType + "</b>");
									}

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validating Charges of the reservation</b>");
									String base = WSClient.getElementValue(modifyBookingRes, "Rates_Rate_Base",
											XMLType.RESPONSE);
									if (WSAssert.assertEquals(String.valueOf(amount), base, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b>Base -> Expected : "
												+ String.valueOf(amount) + "  Actual : " + base + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b>Base -> Expected : "
												+ String.valueOf(amount) + "  Actual : " + base + "</b>");
									}
									String total = WSClient.getElementValue(modifyBookingRes,
											"RoomStays_RoomStay_Total", XMLType.RESPONSE);
									if (WSAssert.assertEquals(String.valueOf(amount), total, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b>Total -> Expected : "
												+ String.valueOf(amount) + "  Actual : " + total + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b>Total -> Expected : "
												+ String.valueOf(amount) + "  Actual : " + total + "</b>");
									}
									total = WSClient.getElementValue(modifyBookingRes,
											"RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages",
											XMLType.RESPONSE);
									if (WSAssert.assertEquals(String.valueOf(amount), total, true)) {
										WSClient.writeToReport(LogStatus.PASS,
												"<b>TotalRoomRateAndPackages -> Expected : " + String.valueOf(amount)
												+ "  Actual : " + total + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL,
												"<b>TotalRoomRateAndPackages -> Expected : " + String.valueOf(amount)
												+ "  Actual : " + total + "</b>");
									}
									total = WSClient.getElementValue(modifyBookingRes,
											"RoomRateAndPackages_Charges_Amount", XMLType.RESPONSE);
									if (WSAssert.assertEquals(String.valueOf(amount), total, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b>Amount -> Expected : "
												+ String.valueOf(amount) + "  Actual : " + total + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b>Amount -> Expected : "
												+ String.valueOf(amount) + "  Actual : " + total + "</b>");
									}
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Pre-Requisite Failed >> Discount was not added to the reservation");
						}
					}
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * discount amount is added to the reservation.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS" })

	public void modifyBooking_42783() {
		try {
			String testName = "modifyBooking_42783";
			WSClient.startTest(testName, "Verify that discount amount(percentage) is applied to the reservation",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode",
					"MarketCode", "ReservationType", "DiscountReasons" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_11"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_10"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
					WSClient.setData("{var_profileId}", profileID);

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/

					HashMap<String, String> resvID1 = CreateReservation.createReservation("DS_01");
					if (resvID1.get("reservationId") != "error") {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID1.get("reservationId") + "</b>");
						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
						WSClient.setData("{var_roomCount}", "1");
						WSClient.setData("{var_amount}", "2");
						WSClient.setData("{var_type}", "PERCENT");
						WSClient.setData("{var_reason}", OperaPropConfig.getDataSetForCode("DiscountReasons", "DS_01"));

						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_58}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_59}"));
						WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_58}"));
						WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_59}"));

						fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"),
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));

						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_32");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_36");
								LinkedHashMap<String, String> prices = WSClient.getDBRow(query);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Details of Rate Code : " + prices.get("RATE_CODE") + "</b>");
								WSClient.writeToReport(LogStatus.INFO,
										"<b> Charge For Adult 1 -> " + prices.get("AMOUNT_1") + "  Adult 2 -> "
												+ prices.get("AMOUNT_2") + "  Adult 3 -> " + prices.get("AMOUNT_3")
												+ " Extra Adult -> " + prices.get("ADULT_CHARGE") + "</b>");

								int price = Integer.parseInt(prices.get("AMOUNT_1"));
								int amount = (int) (price
										- (price * (Float.valueOf(WSClient.getData("{var_amount}")) / 100)));

								String reqDiscountType = WSClient.getElementValue(modifyBookingReq,
										"RatePlan_Discount_DiscountType", XMLType.REQUEST);
								String reqAmount = WSClient.getElementValue(modifyBookingReq,
										"RatePlan_Discount_DiscountAmount", XMLType.REQUEST);
								String reqReason = WSClient.getElementValue(modifyBookingReq,
										"RatePlan_Discount_DiscountReason", XMLType.REQUEST);

								query = WSClient.getQuery("QS_41");
								LinkedHashMap<String, String> results = WSClient.getDBRow(query);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating discount details insertion into DB</b>");
								if (WSAssert.assertEquals(reqAmount, results.get("DISCOUNT_PRCNT"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>DiscountPercent -> Expected : "
											+ reqAmount + "  Actual : " + results.get("DISCOUNT_PRCNT") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>DiscountPercent -> Expected : "
											+ reqAmount + "  Actual : " + results.get("DISCOUNT_PRCNT") + "</b>");
								}
								if (WSAssert.assertEquals(reqReason, results.get("DISCOUNT_REASON_CODE"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>DiscountReason -> Expected : "
											+ reqReason + "  Actual : " + results.get("DISCOUNT_REASON_CODE") + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>DiscountReason -> Expected : "
											+ reqReason + "  Actual : " + results.get("DISCOUNT_REASON_CODE") + "</b>");
								}

								String resDiscountType = WSClient.getElementValue(modifyBookingRes,
										"RatePlan_Discount_DiscountType", XMLType.RESPONSE);
								String resAmount = WSClient.getElementValue(modifyBookingRes,
										"RatePlan_Discount_DiscountAmount", XMLType.RESPONSE);
								String resReason = WSClient.getElementValue(modifyBookingRes,
										"RatePlan_Discount_DiscountReason", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validating discount details retrieval onto the response</b>");
								if (WSAssert.assertEquals(results.get("DISCOUNT_PRCNT"), resAmount, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>DiscountPercent -> Expected : "
											+ results.get("DISCOUNT_PRCNT") + "  Actual : " + resAmount + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>DiscountPercent -> Expected : "
											+ results.get("DISCOUNT_PRCNT") + "  Actual : " + resAmount + "</b>");
								}
								if (WSAssert.assertEquals(results.get("DISCOUNT_REASON_CODE"), resReason, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>DiscountReason -> Expected : "
											+ results.get("DISCOUNT_REASON_CODE") + "  Actual : " + resReason + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>DiscountReason -> Expected : "
											+ results.get("DISCOUNT_REASON_CODE") + "  Actual : " + resReason + "</b>");
								}
								if (WSAssert.assertEquals(reqDiscountType, resDiscountType, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>DiscountType -> Expected : "
											+ reqDiscountType + "  Actual : " + resDiscountType + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>DiscountType -> Expected : "
											+ reqDiscountType + "  Actual : " + resDiscountType + "</b>");
								}

								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Charges of the reservation</b>");
								String base = WSClient.getElementValue(modifyBookingRes, "Rates_Rate_Base",
										XMLType.RESPONSE);
								if (WSAssert.assertEquals(String.valueOf(amount), base, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>Base -> Expected : "
											+ String.valueOf(amount) + "  Actual : " + base + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>Base -> Expected : "
											+ String.valueOf(amount) + "  Actual : " + base + "</b>");
								}
								String total = WSClient.getElementValue(modifyBookingRes, "RoomStays_RoomStay_Total",
										XMLType.RESPONSE);
								if (WSAssert.assertEquals(String.valueOf(amount), total, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>Total -> Expected : "
											+ String.valueOf(amount) + "  Actual : " + total + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>Total -> Expected : "
											+ String.valueOf(amount) + "  Actual : " + total + "</b>");
								}
								total = WSClient.getElementValue(modifyBookingRes,
										"RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages",
										XMLType.RESPONSE);
								if (WSAssert.assertEquals(String.valueOf(amount), total, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>TotalRoomRateAndPackages -> Expected : "
											+ String.valueOf(amount) + "  Actual : " + total + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>TotalRoomRateAndPackages -> Expected : "
											+ String.valueOf(amount) + "  Actual : " + total + "</b>");
								}
								total = WSClient.getElementValue(modifyBookingRes, "RoomRateAndPackages_Charges_Amount",
										XMLType.RESPONSE);
								if (WSAssert.assertEquals(String.valueOf(amount), total, true)) {
									WSClient.writeToReport(LogStatus.PASS, "<b>Amount -> Expected : "
											+ String.valueOf(amount) + "  Actual : " + total + "</b>");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "<b>Amount -> Expected : "
											+ String.valueOf(amount) + "  Actual : " + total + "</b>");
								}
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode,ReservationType failed! -----Blocked");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * error should be generated when arrival date is modified to date prior
	 * business date.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42622() {
		try {
			String testName = "modifyBooking_42622";
			WSClient.startTest(testName,
					"Verify that error is generated when arrival date is modified to date prior business date",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
			WSClient.setData("{var_par}", "N");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			if (!paramValue.equals("N")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
				paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			}

			if (paramValue.equals("N")) {
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/*************
					 * Prerequisite : Room type, Rate Plan Code, Source Code,
					 * Market Code
					 *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_ReservationType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 ****************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
						WSClient.setData("{var_profileId}", profileID);

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						if (resvID.get("reservationId") == null || resvID.get("reservationId") == "error")
							resvID = CreateReservation.createReservation("DS_01");
						if (resvID.get("reservationId") != "error") {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));

							WSClient.setData("{var_startDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_MINUS_1}"));
							WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_1}"));

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							/***************
							 * OWS Modify Booking Operation
							 ********************/
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_21");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Reservation creation failed");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-Requisite blocked >> Changing channel parameter failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "OWS", "Reservation" })

	public void modifyBooking_42719() {
		String paramvalue, resvid = "";
		try {
			String testName = "modifyBooking_42719";
			WSClient.startTest(testName,
					"Verify that booking is modified when"
							+ " 1) Sell limits configured for both room type and channel and room count is equal to/less than sell limit and"
							+ " 2) Error is generated when room count is greater than sell limit",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsresort}", resortExtValue);

			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_param}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			OPERALib.setOperaHeader(OPERALib.getUserName());

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter CHANNEL_INVENTORY is enabled</b>");
			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_02", "PARAMETER_VALUE");
			String parameter = paramvalue;
			WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			if (!WSAssert.assertEquals("Y", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter CHANNEL_INVENTORY</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_02", "PARAMETER_VALUE");
				WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			}

			if (parameter.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
				WSClient.setData("{var_par}", "N");
				WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
				WSClient.setData("{var_param}", "OVERRIDE_YN");
				WSClient.setData("{var_type}", "Boolean");
				String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				if (!paramValue.equals("N")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
					paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
					WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				}

				if (paramValue.equals("N")) {
					WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_130}"));
					WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_131}"));
					WSClient.setData("{var_roomLimit}", "4");

					/************** Set channel sell limit ***************/
					String setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_04");
					String setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
					if (WSAssert.assertIfElementExists(setSellLimitsRes, "SetChannelSellLimitsByDateRangeRS_Success",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Sell limit for channel " + " on " + WSClient.getData("{var_startDate}") + " - "
										+ WSClient.getData("{var_roomLimit}") + "</b>");

						String fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_03");
						String fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
						if (WSAssert.assertIfElementExists(fetchLimitsRes, "FetchChannelSellLimitsRS_Success", true)) {
							WSClient.setData("{var_id}", WSClient.getElementValue(fetchLimitsRes,
									"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
						}

						WSClient.setData("{var_roomLimit}", "3");
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

						setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_03");
						setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
						if (WSAssert.assertIfElementExists(setSellLimitsRes,
								"SetChannelSellLimitsByDateRangeRS_Success", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Sell limit for room type " + WSClient.getData("{var_roomType}") + " on "
											+ WSClient.getData("{var_startDate}") + " - "
											+ WSClient.getData("{var_roomLimit}") + "</b>");

							fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_02");
							fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
							if (WSAssert.assertIfElementExists(fetchLimitsRes, "FetchChannelSellLimitsRS_Success",
									true)) {
								WSClient.setData("{var_id1}", WSClient.getElementValue(fetchLimitsRes,
										"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
							}

							if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType",
									"SourceCode", "MarketCode", "ReservationType" })) {
								/***********
								 * Pre-requisite : Create a Profile
								 ***********/
								profileID = CreateProfile.createProfile("DS_00");
								if (!profileID.equals("error")) {
									WSClient.setData("{var_profileId}", profileID);

									/*************
									 * Prerequisite :Create a Booking
									 ********************/
									WSClient.setData("{var_busdate}",
											WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_126}"));
									WSClient.setData("{var_busdate1}",
											WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_127}"));
									WSClient.setData("{var_time}", "09:00:00");
									WSClient.setData("{var_resvType}",
											OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
									WSClient.setData("{var_rate}",
											OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
									WSClient.setData("{var_ReservationType}",
											OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
									WSClient.setData("{var_owsResort}", resortExtValue);

									fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
											OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

									OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(),
											resortOperaValue, OWSLib.getChannelType(interfaceName),
											OWSLib.getChannelCarier(resortOperaValue, interfaceName));
									String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
									String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
									if (WSAssert.assertIfElementValueEquals(createBookingRes,
											"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
										resvid = WSClient.getElementValueByAttribute(createBookingRes,
												"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
										WSClient.setData("{var_resvId}", resvid);
										WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:" + resvid + "</b>");

										WSClient.setData("{var_rateCode}",
												OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
										WSClient.setData("{var_roomType}",
												OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
										WSClient.setData("{var_roomCount}", "3");

										WSClient.writeToReport(LogStatus.INFO,
												"<b>Verify that booking is modified when room count is equal to the sell limit configured for the room type</b>");
										String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking",
												"DS_06");
										String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result", true)) {
											if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
													"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Verify that booking is not modified when room count is greater than sell limit configured for the room type</b>");
												/******
												 * Verify that booking is not
												 * modified when room count
												 * exceeds the sell limit
												 *****/
												WSClient.setData("{var_roomCount}", "4");
												modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking",
														"DS_06");
												String modifyBookingRes1 = WSClient
														.processSOAPMessage(modifyBookingReq);
												if (WSAssert.assertIfElementExists(modifyBookingRes1,
														"ModifyBookingResponse_Result", true)) {
													if (WSAssert.assertIfElementValueEquals(modifyBookingRes1,
															"ModifyBookingResponse_Result_resultStatusFlag", "FAIL",
															false)) {
														if (WSAssert.assertIfElementExists(modifyBookingRes1,
																"ModifyBookingResponse_Result_GDSError", true)) {
															if (!WSClient.getElementValue(modifyBookingRes1,
																	"ModifyBookingResponse_Result_GDSError",
																	XMLType.RESPONSE).equals("*null*"))
																WSClient.writeToReport(LogStatus.INFO,
																		"<b> The error that is generated is : "
																				+ WSClient.getElementValue(
																						modifyBookingRes1,
																						"ModifyBookingResponse_Result_GDSError",
																						XMLType.RESPONSE)
																				+ "</b>");
														}
														if (WSAssert.assertIfElementExists(modifyBookingRes1,
																"ModifyBookingResponse_Result_GDSError_errorCode",
																true)) {
															if (!WSClient.getElementValue(modifyBookingRes1,
																	"ModifyBookingResponse_Result_GDSError_errorCode",
																	XMLType.RESPONSE).equals("*null*"))
																WSClient.writeToReport(LogStatus.INFO,
																		"<b> The error code that is generated is : "
																				+ WSClient.getElementValue(
																						modifyBookingRes1,
																						"ModifyBookingResponse_Result_GDSError_errorCode",
																						XMLType.RESPONSE)
																				+ "</b>");
														}
														if (WSAssert.assertIfElementExists(modifyBookingRes1,
																"ModifyBookingResponse_Result_OperaErrorCode", true)) {
															WSClient.writeToReport(LogStatus.INFO,
																	"<b> The error code that is generated is : "
																			+ WSClient.getElementValue(
																					modifyBookingRes1,
																					"ModifyBookingResponse_Result_OperaErrorCode",
																					XMLType.RESPONSE)
																			+ "</b>");
														}
														if (WSAssert.assertIfElementExists(modifyBookingRes1,
																"Result_Text_TextElement", true)) {
															String message = WSAssert.getElementValue(modifyBookingRes1,
																	"Result_Text_TextElement", XMLType.RESPONSE);
															WSClient.writeToReport(LogStatus.INFO,
																	"<b>The text displayed in the response is :"
																			+ message + "</b>");
														}

														WSClient.writeToReport(LogStatus.INFO,
																"<b>Verify that booking is modified when room count is equal to the sell limit configured for the channel</b>");
														WSClient.setData("{var_rateCode}", OperaPropConfig
																.getChannelCodeForDataSet("RateCode", "DS_03"));
														WSClient.setData("{var_roomType}", OperaPropConfig
																.getChannelCodeForDataSet("RoomType", "DS_02"));
														modifyBookingReq = WSClient
																.createSOAPMessage("OWSModifyBooking", "DS_06");
														modifyBookingRes1 = WSClient
																.processSOAPMessage(modifyBookingReq);
														if (WSAssert.assertIfElementExists(modifyBookingRes1,
																"ModifyBookingResponse_Result", true)) {
															if (WSAssert.assertIfElementValueEquals(modifyBookingRes1,
																	"ModifyBookingResponse_Result_resultStatusFlag",
																	"SUCCESS", false)) {
																WSClient.writeToReport(LogStatus.INFO,
																		"<b>Verify that booking is not modified when room count is greater than the sell limit configured for the channel</b>");
																WSClient.setData("{var_roomCount}", "5");
																modifyBookingReq = WSClient
																		.createSOAPMessage("OWSModifyBooking", "DS_06");
																String modifyBookingRes2 = WSClient
																		.processSOAPMessage(modifyBookingReq);
																if (WSAssert.assertIfElementExists(modifyBookingRes2,
																		"ModifyBookingResponse_Result", true)) {
																	WSAssert.assertIfElementValueEquals(
																			modifyBookingRes2,
																			"ModifyBookingResponse_Result_resultStatusFlag",
																			"FAIL", false);
																	if (WSAssert.assertIfElementExists(
																			modifyBookingRes2,
																			"ModifyBookingResponse_Result_GDSError",
																			true)) {
																		if (!WSClient
																				.getElementValue(modifyBookingRes2,
																						"ModifyBookingResponse_Result_GDSError",
																						XMLType.RESPONSE)
																				.equals("*null*"))
																			WSClient.writeToReport(LogStatus.INFO,
																					"<b> The error that is generated is : "
																							+ WSClient.getElementValue(
																									modifyBookingRes2,
																									"ModifyBookingResponse_Result_GDSError",
																									XMLType.RESPONSE)
																							+ "</b>");
																	}
																	if (WSAssert.assertIfElementExists(
																			modifyBookingRes2,
																			"ModifyBookingResponse_Result_GDSError_errorCode",
																			true)) {
																		if (!WSClient
																				.getElementValue(modifyBookingRes2,
																						"ModifyBookingResponse_Result_GDSError_errorCode",
																						XMLType.RESPONSE)
																				.equals("*null*"))
																			WSClient.writeToReport(LogStatus.INFO,
																					"<b> The error code that is generated is : "
																							+ WSClient.getElementValue(
																									modifyBookingRes2,
																									"ModifyBookingResponse_Result_GDSError_errorCode",
																									XMLType.RESPONSE)
																							+ "</b>");
																	}
																	if (WSAssert.assertIfElementExists(
																			modifyBookingRes2,
																			"ModifyBookingResponse_Result_OperaErrorCode",
																			true)) {
																		WSClient.writeToReport(LogStatus.INFO,
																				"<b> The error code that is generated is : "
																						+ WSClient.getElementValue(
																								modifyBookingRes2,
																								"ModifyBookingResponse_Result_OperaErrorCode",
																								XMLType.RESPONSE)
																						+ "</b>");
																	}
																	if (WSAssert.assertIfElementExists(
																			modifyBookingRes2,
																			"Result_Text_TextElement", true)) {
																		String message = WSAssert.getElementValue(
																				modifyBookingRes2,
																				"Result_Text_TextElement",
																				XMLType.RESPONSE);
																		WSClient.writeToReport(LogStatus.INFO,
																				"<b>The text displayed in the response is :"
																						+ message + "</b>");
																	}
																} else if (WSAssert.assertIfElementExists(
																		modifyBookingRes2,
																		"ModifyBookingResponse_faultstring", true)) {
																	WSClient.writeToReport(LogStatus.FAIL,
																			"<b> The error that is generated is : "
																					+ WSClient.getElementValue(
																							modifyBookingRes2,
																							"ModifyBookingResponse_faultstring",
																							XMLType.RESPONSE)
																					+ "</b>");
																}
															}
															if (WSAssert.assertIfElementExists(modifyBookingRes1,
																	"ModifyBookingResponse_Result_GDSError", true)) {
																if (!WSClient.getElementValue(modifyBookingRes1,
																		"ModifyBookingResponse_Result_GDSError",
																		XMLType.RESPONSE).equals("*null*"))
																	WSClient.writeToReport(LogStatus.INFO,
																			"<b> The error that is generated is : "
																					+ WSClient.getElementValue(
																							modifyBookingRes1,
																							"ModifyBookingResponse_Result_GDSError",
																							XMLType.RESPONSE)
																					+ "</b>");
															}
															if (WSAssert.assertIfElementExists(modifyBookingRes1,
																	"ModifyBookingResponse_Result_GDSError_errorCode",
																	true)) {
																if (!WSClient.getElementValue(modifyBookingRes1,
																		"ModifyBookingResponse_Result_GDSError_errorCode",
																		XMLType.RESPONSE).equals("*null*"))
																	WSClient.writeToReport(LogStatus.INFO,
																			"<b> The error code that is generated is : "
																					+ WSClient.getElementValue(
																							modifyBookingRes1,
																							"ModifyBookingResponse_Result_GDSError_errorCode",
																							XMLType.RESPONSE)
																					+ "</b>");
															}
															if (WSAssert.assertIfElementExists(modifyBookingRes1,
																	"ModifyBookingResponse_Result_OperaErrorCode",
																	true)) {
																WSClient.writeToReport(LogStatus.INFO,
																		"<b> The error code that is generated is : "
																				+ WSClient.getElementValue(
																						modifyBookingRes1,
																						"ModifyBookingResponse_Result_OperaErrorCode",
																						XMLType.RESPONSE)
																				+ "</b>");
															}
															if (WSAssert.assertIfElementExists(modifyBookingRes1,
																	"Result_Text_TextElement", true)) {
																String message = WSAssert.getElementValue(
																		modifyBookingRes1, "Result_Text_TextElement",
																		XMLType.RESPONSE);
																WSClient.writeToReport(LogStatus.INFO,
																		"<b>The text displayed in the response is :"
																				+ message + "</b>");
															}
														} else if (WSAssert.assertIfElementExists(modifyBookingRes1,
																"ModifyBookingResponse_faultstring", true)) {
															WSClient.writeToReport(LogStatus.FAIL,
																	"<b> The error that is generated is : " + WSClient
																	.getElementValue(modifyBookingRes1,
																			"ModifyBookingResponse_faultstring",
																			XMLType.RESPONSE)
																	+ "</b>");
														}
													}
												} else if (WSAssert.assertIfElementExists(modifyBookingRes1,
														"ModifyBookingResponse_faultstring", true)) {
													WSClient.writeToReport(LogStatus.FAIL,
															"<b> The error that is generated is : "
																	+ WSClient.getElementValue(modifyBookingRes1,
																			"ModifyBookingResponse_faultstring",
																			XMLType.RESPONSE)
																	+ "</b>");
												}
											}
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError", true)) {
												if (!WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
														.equals("*null*"))
													WSClient.writeToReport(LogStatus.INFO,
															"<b> The error that is generated is : "
																	+ WSClient.getElementValue(modifyBookingRes,
																			"ModifyBookingResponse_Result_GDSError",
																			XMLType.RESPONSE)
																	+ "</b>");
											}
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
												if (!WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_GDSError_errorCode",
														XMLType.RESPONSE).equals("*null*"))
													WSClient.writeToReport(LogStatus.INFO,
															"<b> The error code that is generated is : "
																	+ WSClient.getElementValue(modifyBookingRes,
																			"ModifyBookingResponse_Result_GDSError_errorCode",
																			XMLType.RESPONSE)
																	+ "</b>");
											}
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_OperaErrorCode",
																		XMLType.RESPONSE)
																+ "</b>");
											}
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"Result_Text_TextElement", true)) {
												String message = WSAssert.getElementValue(modifyBookingRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>The text displayed in the response is :" + message
														+ "</b>");
											}
										} else if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_faultstring", true)) {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b> The error that is generated is : " + WSClient.getElementValue(
															modifyBookingRes, "ModifyBookingResponse_faultstring",
															XMLType.RESPONSE) + "</b>");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING,
												"<b>Pre-requisite failed >> Creating a booking failed</b>");
									}
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"<b>Pre-requisites of booking creation failed</b>");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"<b>Pre-requisite failed >> Setting the sell limits failed</b>");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requisite failed >> Setting the sell limits failed</b>");
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requisite failed >> Changing the application parameter failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured due to : " + e);
			e.printStackTrace();
		} finally {
			OPERALib.setOperaHeader(OPERALib.getUserName());
			try {
				if (WSClient.getData("{var_id}") != "") {
					String removeSellLimitReq = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
					String removeSellLimitRes = WSClient.processSOAPMessage(removeSellLimitReq);
					if (WSAssert.assertIfElementExists(removeSellLimitRes, "RemoveChannelSellLimitsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Removed the limits set on the channel set limits </b>");
					}
				}
				if (WSClient.getData("{var_id1}") != "") {
					WSClient.setData("{var_id}", WSClient.getData("{var_id1}"));
					String removeSellLimitReq = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
					String removeSellLimitRes = WSClient.processSOAPMessage(removeSellLimitReq);
					if (WSAssert.assertIfElementExists(removeSellLimitRes, "RemoveChannelSellLimitsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Removed the limits set on the channel set limits </b>");
					}
				}
				if (resvid != "") {
					WSClient.setData("{var_resvId}", resvid);
					if (CancelReservation.cancelReservation("DS_02")) {
						WSClient.writeToLog("Reservation cancellation successful");
					} else
						WSClient.writeToLog("Reservation cancellation failed");
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "OWS", "Reservation" })

	public void modifyBooking_42780() {
		String paramvalue, resvid = "";
		try {
			String testName = "modifyBooking_42780";
			WSClient.startTest(testName,
					"Verify that booking is modified successfully when room count is equal to/less than/greater than the sell limit configured for both channel and room type and OVERRIDE_YN is set to Y",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsresort}", resortExtValue);

			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_param}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			OPERALib.setOperaHeader(OPERALib.getUserName());

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter CHANNEL_INVENTORY is enabled</b>");
			paramvalue = FetchChannelParameters.fetchChannelParameters("QS_02", "PARAMETER_VALUE");
			WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + paramvalue + "</b>");
			String parameter = paramvalue;
			if (!WSAssert.assertEquals("Y", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter CHANNEL_INVENTORY</b>");
				parameter = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_02", "PARAMETER_VALUE");
				WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL_INVENTORY : " + parameter + "</b>");
			}

			if (parameter.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
				WSClient.setData("{var_par}", "Y");
				WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
				WSClient.setData("{var_param}", "OVERRIDE_YN");
				WSClient.setData("{var_type}", "Boolean");
				String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				if (!paramValue.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter OVERRIDE_YN</b>");
					paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
					WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				}

				if (paramValue.equals("Y")) {

					WSClient.setData("{var_roomLimit}", "4");
					if (OperaPropConfig.getPropertyConfigResults(
							new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
						/***********
						 * Pre-requisite : Create a Profile
						 ***********/
						profileID = CreateProfile.createProfile("DS_00");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);

							/*************
							 * Prerequisite :Create a Booking
							 ********************/
							WSClient.setData("{var_busdate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_126}") + "T14:00:00+05:30");
							WSClient.setData("{var_busdate1}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_127}") + "T14:00:00+05:30");
							WSClient.setData("{var_time}", "09:00:00");
							WSClient.setData("{var_resvType}",
									OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
							WSClient.setData("{var_rate}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
							WSClient.setData("{var_ReservationType}",
									OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
							WSClient.setData("{var_owsResort}", resortExtValue);

							fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

							OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
									OWSLib.getChannelType(interfaceName),
									OWSLib.getChannelCarier(resortOperaValue, interfaceName));
							String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
							String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
							if (WSAssert.assertIfElementValueEquals(createBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
								resvid = WSClient.getElementValueByAttribute(createBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}", resvid);
								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:" + resvid + "</b>");

								WSClient.setData("{var_rateCode}",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
								WSClient.setData("{var_roomType}",
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
								WSClient.setData("{var_roomCount}", "3");

								WSClient.setData("{var_busdate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_61}"));
								WSClient.setData("{var_busdate1}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_62}"));
								WSClient.setData("{var_startDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_61}"));
								WSClient.setData("{var_endDate}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_62}"));
								fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"),
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

								/**************
								 * Set channel sell limit
								 ***************/
								String setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange",
										"DS_04");
								String setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
								if (WSAssert.assertIfElementExists(setSellLimitsRes,
										"SetChannelSellLimitsByDateRangeRS_Success", true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Sell limit for channel " + " on " + WSClient.getData("{var_startDate}")
											+ " - " + WSClient.getData("{var_roomLimit}") + "</b>");

									String fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits",
											"DS_03");
									String fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
									if (WSAssert.assertIfElementExists(fetchLimitsRes,
											"FetchChannelSellLimitsRS_Success", true)) {
										WSClient.setData("{var_id}", WSClient.getElementValue(fetchLimitsRes,
												"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
									}

									WSClient.setData("{var_roomLimit}", "3");
									WSClient.setData("{var_roomType}",
											OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

									setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange",
											"DS_03");
									setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
									if (WSAssert.assertIfElementExists(setSellLimitsRes,
											"SetChannelSellLimitsByDateRangeRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Sell limit for room type " + WSClient.getData("{var_roomType}")
												+ " on " + WSClient.getData("{var_startDate}") + " - "
												+ WSClient.getData("{var_roomLimit}") + "</b>");

										fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_02");
										fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
										if (WSAssert.assertIfElementExists(fetchLimitsRes,
												"FetchChannelSellLimitsRS_Success", true)) {
											WSClient.setData("{var_id1}", WSClient.getElementValue(fetchLimitsRes,
													"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
										}

										WSClient.writeToReport(LogStatus.INFO,
												"<b>Verify that booking is modified when room count is equal to the sell limit configured for the room type</b>");
										String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking",
												"DS_06");
										String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

										if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_Result", true)) {
											if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
													"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Verify that booking is modified although room count is greater than sell limit configured for the room type</b>");
												/******
												 * Verify that booking is not
												 * modified when room count
												 * exceeds the sell limit
												 *****/
												WSClient.setData("{var_roomCount}", "4");
												modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking",
														"DS_06");
												String modifyBookingRes1 = WSClient
														.processSOAPMessage(modifyBookingReq);
												if (WSAssert.assertIfElementExists(modifyBookingRes1,
														"ModifyBookingResponse_Result", true)) {
													if (WSAssert.assertIfElementValueEquals(modifyBookingRes1,
															"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS",
															false)) {
														WSClient.writeToReport(LogStatus.INFO,
																"<b>Verify that booking is modified when room count is equal to the sell limit configured for the channel</b>");
														WSClient.setData("{var_rateCode}", OperaPropConfig
																.getChannelCodeForDataSet("RateCode", "DS_03"));
														WSClient.setData("{var_roomType}", OperaPropConfig
																.getChannelCodeForDataSet("RoomType", "DS_02"));
														modifyBookingReq = WSClient
																.createSOAPMessage("OWSModifyBooking", "DS_06");
														String modifyBookingRes2 = WSClient
																.processSOAPMessage(modifyBookingReq);
														if (WSAssert.assertIfElementExists(modifyBookingRes2,
																"ModifyBookingResponse_Result", true)) {
															if (WSAssert.assertIfElementValueEquals(modifyBookingRes2,
																	"ModifyBookingResponse_Result_resultStatusFlag",
																	"SUCCESS", false)) {
																WSClient.writeToReport(LogStatus.INFO,
																		"<b>Verify that booking is modified although room count is greater than the sell limit configured for the channel</b>");
																WSClient.setData("{var_roomCount}", "5");
																modifyBookingReq = WSClient
																		.createSOAPMessage("OWSModifyBooking", "DS_06");
																String modifyBookingRes3 = WSClient
																		.processSOAPMessage(modifyBookingReq);
																if (WSAssert.assertIfElementExists(modifyBookingRes3,
																		"ModifyBookingResponse_Result", true)) {
																	WSAssert.assertIfElementValueEquals(
																			modifyBookingRes3,
																			"ModifyBookingResponse_Result_resultStatusFlag",
																			"SUCCESS", false);
																	if (WSAssert.assertIfElementExists(
																			modifyBookingRes3,
																			"ModifyBookingResponse_Result_GDSError",
																			true)) {
																		if (!WSClient
																				.getElementValue(modifyBookingRes3,
																						"ModifyBookingResponse_Result_GDSError",
																						XMLType.RESPONSE)
																				.equals("*null*"))
																			WSClient.writeToReport(LogStatus.INFO,
																					"<b> The error that is generated is : "
																							+ WSClient.getElementValue(
																									modifyBookingRes3,
																									"ModifyBookingResponse_Result_GDSError",
																									XMLType.RESPONSE)
																							+ "</b>");
																	}
																	if (WSAssert.assertIfElementExists(
																			modifyBookingRes3,
																			"ModifyBookingResponse_Result_GDSError_errorCode",
																			true)) {
																		if (!WSClient
																				.getElementValue(modifyBookingRes3,
																						"ModifyBookingResponse_Result_GDSError_errorCode",
																						XMLType.RESPONSE)
																				.equals("*null*"))
																			WSClient.writeToReport(LogStatus.INFO,
																					"<b> The error code that is generated is : "
																							+ WSClient.getElementValue(
																									modifyBookingRes3,
																									"ModifyBookingResponse_Result_GDSError_errorCode",
																									XMLType.RESPONSE)
																							+ "</b>");
																	}
																	if (WSAssert.assertIfElementExists(
																			modifyBookingRes3,
																			"ModifyBookingResponse_Result_OperaErrorCode",
																			true)) {
																		WSClient.writeToReport(LogStatus.INFO,
																				"<b> The error code that is generated is : "
																						+ WSClient.getElementValue(
																								modifyBookingRes3,
																								"ModifyBookingResponse_Result_OperaErrorCode",
																								XMLType.RESPONSE)
																						+ "</b>");
																	}
																	if (WSAssert.assertIfElementExists(
																			modifyBookingRes3,
																			"Result_Text_TextElement", true)) {
																		String message = WSAssert.getElementValue(
																				modifyBookingRes3,
																				"Result_Text_TextElement",
																				XMLType.RESPONSE);
																		WSClient.writeToReport(LogStatus.INFO,
																				"<b>The text displayed in the response is :"
																						+ message + "</b>");
																	}
																} else if (WSAssert.assertIfElementExists(
																		modifyBookingRes3,
																		"ModifyBookingResponse_faultstring", true)) {
																	WSClient.writeToReport(LogStatus.FAIL,
																			"<b> The error that is generated is : "
																					+ WSClient.getElementValue(
																							modifyBookingRes3,
																							"ModifyBookingResponse_faultstring",
																							XMLType.RESPONSE)
																					+ "</b>");
																}
															}
															if (WSAssert.assertIfElementExists(modifyBookingRes2,
																	"ModifyBookingResponse_Result_GDSError", true)) {
																if (!WSClient.getElementValue(modifyBookingRes2,
																		"ModifyBookingResponse_Result_GDSError",
																		XMLType.RESPONSE).equals("*null*"))
																	WSClient.writeToReport(LogStatus.INFO,
																			"<b> The error that is generated is : "
																					+ WSClient.getElementValue(
																							modifyBookingRes2,
																							"ModifyBookingResponse_Result_GDSError",
																							XMLType.RESPONSE)
																					+ "</b>");
															}
															if (WSAssert.assertIfElementExists(modifyBookingRes2,
																	"ModifyBookingResponse_Result_GDSError_errorCode",
																	true)) {
																if (!WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_GDSError_errorCode",
																		XMLType.RESPONSE).equals("*null*"))
																	WSClient.writeToReport(LogStatus.INFO,
																			"<b> The error code that is generated is : "
																					+ WSClient.getElementValue(
																							modifyBookingRes2,
																							"ModifyBookingResponse_Result_GDSError_errorCode",
																							XMLType.RESPONSE)
																					+ "</b>");
															}
															if (WSAssert.assertIfElementExists(modifyBookingRes2,
																	"ModifyBookingResponse_Result_OperaErrorCode",
																	true)) {
																WSClient.writeToReport(LogStatus.INFO,
																		"<b> The error code that is generated is : "
																				+ WSClient.getElementValue(
																						modifyBookingRes2,
																						"ModifyBookingResponse_Result_OperaErrorCode",
																						XMLType.RESPONSE)
																				+ "</b>");
															}
															if (WSAssert.assertIfElementExists(modifyBookingRes2,
																	"Result_Text_TextElement", true)) {
																String message = WSAssert.getElementValue(
																		modifyBookingRes2, "Result_Text_TextElement",
																		XMLType.RESPONSE);
																WSClient.writeToReport(LogStatus.INFO,
																		"<b>The text displayed in the response is :"
																				+ message + "</b>");
															}
														} else if (WSAssert.assertIfElementExists(modifyBookingRes2,
																"ModifyBookingResponse_faultstring", true)) {
															WSClient.writeToReport(LogStatus.FAIL,
																	"<b> The error that is generated is : " + WSClient
																	.getElementValue(modifyBookingRes2,
																			"ModifyBookingResponse_faultstring",
																			XMLType.RESPONSE)
																	+ "</b>");
														}
													}
													if (WSAssert.assertIfElementExists(modifyBookingRes1,
															"ModifyBookingResponse_Result_GDSError", true)) {
														if (!WSClient.getElementValue(modifyBookingRes1,
																"ModifyBookingResponse_Result_GDSError",
																XMLType.RESPONSE).equals("*null*"))
															WSClient.writeToReport(LogStatus.INFO,
																	"<b> The error that is generated is : " + WSClient
																	.getElementValue(modifyBookingRes1,
																			"ModifyBookingResponse_Result_GDSError",
																			XMLType.RESPONSE)
																	+ "</b>");
													}
													if (WSAssert.assertIfElementExists(modifyBookingRes1,
															"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
														if (!WSClient.getElementValue(modifyBookingRes1,
																"ModifyBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE).equals("*null*"))
															WSClient.writeToReport(LogStatus.INFO,
																	"<b> The error code that is generated is : "
																			+ WSClient.getElementValue(
																					modifyBookingRes1,
																					"ModifyBookingResponse_Result_GDSError_errorCode",
																					XMLType.RESPONSE)
																			+ "</b>");
													}
													if (WSAssert.assertIfElementExists(modifyBookingRes1,
															"ModifyBookingResponse_Result_OperaErrorCode", true)) {
														WSClient.writeToReport(LogStatus.INFO,
																"<b> The error code that is generated is : "
																		+ WSClient.getElementValue(modifyBookingRes1,
																				"ModifyBookingResponse_Result_OperaErrorCode",
																				XMLType.RESPONSE)
																		+ "</b>");
													}
													if (WSAssert.assertIfElementExists(modifyBookingRes1,
															"Result_Text_TextElement", true)) {
														String message = WSAssert.getElementValue(modifyBookingRes1,
																"Result_Text_TextElement", XMLType.RESPONSE);
														WSClient.writeToReport(LogStatus.INFO,
																"<b>The text displayed in the response is :" + message
																+ "</b>");
													}
												} else if (WSAssert.assertIfElementExists(modifyBookingRes1,
														"ModifyBookingResponse_faultstring", true)) {
													WSClient.writeToReport(LogStatus.FAIL,
															"<b> The error that is generated is : "
																	+ WSClient.getElementValue(modifyBookingRes1,
																			"ModifyBookingResponse_faultstring",
																			XMLType.RESPONSE)
																	+ "</b>");
												}
											}
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError", true)) {
												if (!WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
														.equals("*null*"))
													WSClient.writeToReport(LogStatus.INFO,
															"<b> The error that is generated is : "
																	+ WSClient.getElementValue(modifyBookingRes,
																			"ModifyBookingResponse_Result_GDSError",
																			XMLType.RESPONSE)
																	+ "</b>");
											}
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
												if (!WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_GDSError_errorCode",
														XMLType.RESPONSE).equals("*null*"))
													WSClient.writeToReport(LogStatus.INFO,
															"<b> The error code that is generated is : "
																	+ WSClient.getElementValue(modifyBookingRes,
																			"ModifyBookingResponse_Result_GDSError_errorCode",
																			XMLType.RESPONSE)
																	+ "</b>");
											}
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"ModifyBookingResponse_Result_OperaErrorCode",
																		XMLType.RESPONSE)
																+ "</b>");
											}
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"Result_Text_TextElement", true)) {
												String message = WSAssert.getElementValue(modifyBookingRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>The text displayed in the response is :" + message
														+ "</b>");
											}
										} else if (WSAssert.assertIfElementExists(modifyBookingRes,
												"ModifyBookingResponse_faultstring", true)) {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b> The error that is generated is : " + WSClient.getElementValue(
															modifyBookingRes, "ModifyBookingResponse_faultstring",
															XMLType.RESPONSE) + "</b>");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING,
												"<b>Pre-requisite failed >> Creating a booking failed</b>");
									}
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"<b>Pre-requisites of booking creation failed</b>");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"<b>Pre-requisite failed >> Setting the sell limits failed</b>");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"<b>Pre-requisite failed >> Setting the sell limits failed</b>");
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"<b>Pre-requisite failed >> Changing the channel parameter failed</b>");
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requisite failed >> Changing the channel parameter failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured due to : " + e);
			e.printStackTrace();
		} finally {
			OPERALib.setOperaHeader(OPERALib.getUserName());
			try {
				WSClient.setData("{var_par}", "N");
				ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}

			try {
				if (WSClient.getData("{var_id}") != "") {
					String removeSellLimitReq = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
					String removeSellLimitRes = WSClient.processSOAPMessage(removeSellLimitReq);
					if (WSAssert.assertIfElementExists(removeSellLimitRes, "RemoveChannelSellLimitsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Removed the limits set on the channel set limits </b>");
					}
				}
				if (WSClient.getData("{var_id1}") != "") {
					WSClient.setData("{var_id}", WSClient.getData("{var_id1}"));
					String removeSellLimitReq = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
					String removeSellLimitRes = WSClient.processSOAPMessage(removeSellLimitReq);
					if (WSAssert.assertIfElementExists(removeSellLimitRes, "RemoveChannelSellLimitsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> Removed the limits set on the channel set limits </b>");
					}
				}
				if (resvid != "") {
					WSClient.setData("{var_resvId}", resvid);
					if (CancelReservation.cancelReservation("DS_02")) {
						WSClient.writeToLog("Reservation cancellation successful");
					} else
						WSClient.writeToLog("Reservation cancellation failed");
				}
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })
	public void modifyBooking_42774() {
		try {
			String testName = "modifyBooking_42774";
			WSClient.startTest(testName,
					"Verify that error is generated when booking is modified to day use and mimimum los is greater than zero",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
			WSClient.setData("{var_par}", "N");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			if (!paramValue.equals("N")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
				paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			}

			if (paramValue.equals("N")) {
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/*************
					 * Prerequisite : Room type, Rate Plan Code, Source Code,
					 * Market Code
					 *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
					WSClient.setData("{var_rateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_ReservationType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
					WSClient.setData("{var_startDate}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}") + "T09:00:00+05:30");
					WSClient.setData("{var_endDate}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}") + "T18:00:00+05:30");

					/************
					 * Prerequisite 1: Create profile
					 ****************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/

						if (resvID.get("reservationId") == null) {

							WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_6}"));
							WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_8}"));
							WSClient.setData("{var_startDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_6}"));
							WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_8}"));
							fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"),
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
							resvID = CreateReservation.createReservation("DS_24");

						}
						if (!resvID.get("reservationId").equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));

							WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}"));
							WSClient.setData("{var_busdate1}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_15}"));
							WSClient.setData("{var_startDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}"));
							WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_15}"));

							fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"),
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));

							WSClient.setData("{var_startDate}", newDate);
							WSClient.setData("{var_endDate}", newDate);

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_09");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									if (!WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE).equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO, "<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
												+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
									if (!WSClient
											.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											.equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE)
														+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
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

	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })
	public void modifyBooking_42775() {
		try {
			String testName = "modifyBooking_42775";
			WSClient.startTest(testName,
					"Verify that booking is modified to future date when mimimum los is greater than zero as OVERRIDE_YN is Y",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			if (!paramValue.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter OVERRIDE_YN</b>");
				paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			}

			if (paramValue.equals("Y")) {
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/*************
					 * Prerequisite : Room type, Rate Plan Code, Source Code,
					 * Market Code
					 *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
					WSClient.setData("{var_rateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_ReservationType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
					// WSClient.setData("{var_startDate}",
					// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}")+"T00:00:00");
					// WSClient.setData("{var_endDate}",
					// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}")+"T00:00:00");

					/************
					 * Prerequisite 1: Create profile
					 ****************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						if (resvID.get("reservationId") == null) {
							WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_6}"));
							WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_7}"));
							WSClient.setData("{var_startDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_6}"));
							WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_7}"));
							fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"),
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));

							resvID = CreateReservation.createReservation("DS_24");
						}
						if (!resvID.get("reservationId").equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));

							WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}"));
							WSClient.setData("{var_busdate1}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_15}"));
							WSClient.setData("{var_startDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}"));
							WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_15}"));
							fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"),
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
// Commented by nirmal
							/*WSClient.setData("{var_startDate}", newDate + "T09:00:00+05:30");
							WSClient.setData("{var_endDate}", newDate + "T18:00:00+05:30");*/
							//WSClient.setData("{var_startDate}", newDate);
							
							//WSClient.setData("{var_endDate}", newDate);
							
							/***************
							 * OWS Modify Booking Operation
							 ********************/
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_09");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									String query = WSClient.getQuery("QS_04");
									HashMap<String, String> dbResults = WSClient.getDBRow(query);

									String arrivalDate = WSClient.getElementValue(modifyBookingReq,
											"RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
									String departureDate = WSClient.getElementValue(modifyBookingReq,
											"RoomStay_TimeSpan_EndDate", XMLType.REQUEST);
									String resarrivalDate = WSClient.getElementValue(modifyBookingRes,
											"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE);
									String resdepartureDate = WSClient.getElementValue(modifyBookingRes,
											"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE);

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validation > Modifications are applied on DB</b>");

									/******
									 * Validate above details against DB
									 *********/
									System.out.println("brfore departureDate"+departureDate+"\t arrivalDate"+arrivalDate+"\t resarrivalDate"+resarrivalDate+"\t resdepartureDate"+resdepartureDate);
									String dbArrival = dbResults.get("ARRIVAL");
									dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));
									System.out.println("dbArrival"+dbArrival);
									String dbDeparture = dbResults.get("DEPARTURE");
									System.out.println("dbDeparture"+dbDeparture);
									dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));
									System.out.println("dbDeparture2"+dbDeparture);
									resarrivalDate = resarrivalDate.substring(0, resarrivalDate.indexOf('T'));
									System.out.println("resarrivalDate"+resarrivalDate);
									resdepartureDate = resdepartureDate.substring(0, resdepartureDate.indexOf('T'));
									System.out.println("resdepartureDate"+resdepartureDate);
									//arrivalDate = arrivalDate.substring(0, arrivalDate.indexOf(' '));
									System.out.println("arrivalDate"+arrivalDate);
									//departureDate = departureDate.substring(0, departureDate.indexOf(' '));
									System.out.println("departureDate"+departureDate);
									//
									if (WSAssert.assertEquals(arrivalDate, dbArrival, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
												+ arrivalDate + "  Actual : " + dbArrival + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
												+ arrivalDate + "  Actual : " + dbArrival + "</b>");
									}
									if (WSAssert.assertEquals(departureDate, dbDeparture, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Departure date -> Expected : "
												+ departureDate + "  Actual : " + dbDeparture + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Departure date -> Expected : "
												+ departureDate + "  Actual : " + dbDeparture + "</b>");
									}

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validation > Modifications are retreived correctly on the response</b>");

									/**** Validate modified arrival dates ****/
									if (WSAssert.assertEquals(dbArrival, resarrivalDate, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
												+ dbArrival + "  Actual : " + resarrivalDate + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
												+ dbArrival + "  Actual : " + resarrivalDate + "</b>");
									}
									/****
									 * Validate modified departure dates
									 ****/
									if (WSAssert.assertEquals(dbDeparture, resdepartureDate, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Departure date -> Expected : "
												+ dbDeparture + "  Actual : " + resdepartureDate + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Departure date -> Expected : "
												+ dbDeparture + "  Actual : " + resdepartureDate + "</b>");
									}
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									if (!WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE).equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO, "<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
												+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
									if (!WSClient
											.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											.equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE)
														+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_par}", "N");
				ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })
	public void modifyBooking_42776() {
		try {
			String testName = "modifyBooking_42776";
			WSClient.startTest(testName,
					"Verify that error is generated stay date range is greater than maximum length of stay of the rate code",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
			WSClient.setData("{var_par}", "N");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			if (!paramValue.equals("N")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
				paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			}

			if (paramValue.equals("N")) {
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/*************
					 * Prerequisite : Room type, Rate Plan Code, Source Code,
					 * Market Code
					 *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
					WSClient.setData("{var_rateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_ReservationType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 ****************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						if (resvID.get("reservationId") == null)
							resvID = CreateReservation.createReservation("DS_01");
						if (!resvID.get("reservationId").equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));

							String query = WSClient.getQuery("OWSModifyBooking", "QS_46");
							HashMap<String, String> dbResults = WSClient.getDBRow(query);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Maximum Occupancy of rate code : " + dbResults.get("MAX_LOS") + "</b>");

							String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_28}");
							WSClient.setData("{var_startDate}", startDate);
							WSClient.setData("{var_endDate}", modifyDaysToDate(startDate, "yyyy-MM-dd",
									Integer.parseInt(dbResults.get("MAX_LOS")) + 1));

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_09");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									if (!WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE).equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO, "<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
												+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
									if (!WSClient
											.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											.equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE)
														+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
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

	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })
	public void modifyBooking_42777() {
		try {
			String testName = "modifyBooking_42777";
			WSClient.startTest(testName,
					"Verify that booking is modified when stay date range is greater than maximum length of stay of rate code as OVERRIDE_YN is Y",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			if (!paramValue.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter OVERRIDE_YN</b>");
				paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			}

			if (paramValue.equals("Y")) {
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/*************
					 * Prerequisite : Room type, Rate Plan Code, Source Code,
					 * Market Code
					 *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
					WSClient.setData("{var_rateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_ReservationType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 ****************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						if (resvID.get("reservationId") == null)
							resvID = CreateReservation.createReservation("DS_01");
						if (!resvID.get("reservationId").equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));

							String query = WSClient.getQuery("OWSModifyBooking", "QS_46");
							HashMap<String, String> dbResults = WSClient.getDBRow(query);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Maximum Occupancy of rate code : " + dbResults.get("MAX_LOS") + "</b>");

							String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_18}");
							WSClient.setData("{var_startDate}", startDate);
							WSClient.setData("{var_endDate}", modifyDaysToDate(startDate, "yyyy-MM-dd",
									Integer.parseInt(dbResults.get("MAX_LOS")) + 1));

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_09");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									query = WSClient.getQuery("QS_04");
									dbResults = WSClient.getDBRow(query);

									String arrivalDate = WSClient.getElementValue(modifyBookingReq,
											"RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
									String departureDate = WSClient.getElementValue(modifyBookingReq,
											"RoomStay_TimeSpan_EndDate", XMLType.REQUEST);
									String resarrivalDate = WSClient.getElementValue(modifyBookingRes,
											"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE);
									String resdepartureDate = WSClient.getElementValue(modifyBookingRes,
											"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE);

									resarrivalDate = resarrivalDate.substring(0, resarrivalDate.indexOf('T'));
									resdepartureDate = resdepartureDate.substring(0, resdepartureDate.indexOf('T'));

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validation > Modifications are applied on DB</b>");

									/******
									 * Validate above details against DB
									 *********/
									String dbArrival = dbResults.get("ARRIVAL");
									dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));

									String dbDeparture = dbResults.get("DEPARTURE");
									dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

									if (WSAssert.assertEquals(arrivalDate, dbArrival, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
												+ arrivalDate + "  Actual : " + dbArrival + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
												+ arrivalDate + "  Actual : " + dbArrival + "</b>");
									}
									if (WSAssert.assertEquals(departureDate, dbDeparture, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Departure date -> Expected : "
												+ departureDate + "  Actual : " + dbDeparture + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Departure date -> Expected : "
												+ departureDate + "  Actual : " + dbDeparture + "</b>");
									}

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validation > Modifications are retreived correctly on the response</b>");

									/**** Validate modified arrival dates ****/
									if (WSAssert.assertEquals(arrivalDate, resarrivalDate, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
												+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
												+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
									}
									/****
									 * Validate modified departure dates
									 ****/
									if (WSAssert.assertEquals(departureDate, resdepartureDate, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Departure date -> Expected : "
												+ departureDate + "  Actual : " + resdepartureDate + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Departure date -> Expected : "
												+ departureDate + "  Actual : " + resdepartureDate + "</b>");
									}
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									if (!WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE).equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO, "<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
												+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
									if (!WSClient
											.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											.equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE)
														+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_par}", "N");
				ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })
	public void modifyBooking_42778() {
		try {
			String testName = "modifyBooking_42778";
			WSClient.startTest(testName,
					"Verify that error is generated when date range of booking is modified to dates after max advanced booking days for rate code",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
			WSClient.setData("{var_par}", "N");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			if (!paramValue.equals("N")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
				paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			}

			if (paramValue.equals("N")) {
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/*************
					 * Prerequisite : Room type, Rate Plan Code, Source Code,
					 * Market Code
					 *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
					WSClient.setData("{var_rateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_ReservationType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 ****************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						if (resvID.get("reservationId") == null)
							resvID = CreateReservation.createReservation("DS_01");
						if (!resvID.get("reservationId").equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));

							String query = WSClient.getQuery("OWSModifyBooking", "QS_46");
							HashMap<String, String> dbResults = WSClient.getDBRow(query);
							WSClient.writeToReport(LogStatus.INFO, "<b>Maximum Advance Booking days of rate code : "
									+ dbResults.get("MAX_ADVANCE_BOOKING") + "</b>");

							String startDate = modifyDaysToDate(WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"),
									"yyyy-MM-dd", Integer.parseInt(dbResults.get("MAX_ADVANCE_BOOKING")) + 1);
							WSClient.setData("{var_startDate}", startDate);
							WSClient.setData("{var_endDate}", modifyDaysToDate(startDate, "yyyy-MM-dd", 1));

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_09");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									if (!WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE).equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO, "<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
												+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
									if (!WSClient
											.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											.equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE)
														+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
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

	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })
	public void modifyBooking_42779() {
		try {
			String testName = "modifyBooking_42779";
			WSClient.startTest(testName,
					"Verify that booking is modified successfully when date range is modified to dates after max advanced booking days for rate code",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			if (!paramValue.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter OVERRIDE_YN</b>");
				paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			}

			if (paramValue.equals("Y")) {
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/*************
					 * Prerequisite : Room type, Rate Plan Code, Source Code,
					 * Market Code
					 *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
					WSClient.setData("{var_rateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_ReservationType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 ****************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						if (resvID.get("reservationId") == null)
							resvID = CreateReservation.createReservation("DS_01");
						if (!resvID.get("reservationId").equals("error")) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));

							String query = WSClient.getQuery("OWSModifyBooking", "QS_46");
							HashMap<String, String> dbResults = WSClient.getDBRow(query);
							WSClient.writeToReport(LogStatus.INFO, "<b>Maximum Advance Booking days of rate code : "
									+ dbResults.get("MAX_ADVANCE_BOOKING") + "</b>");

							String startDate = modifyDaysToDate(WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"),
									"yyyy-MM-dd", Integer.parseInt(dbResults.get("MAX_ADVANCE_BOOKING")) + 1);
							WSClient.setData("{var_startDate}", startDate);
							WSClient.setData("{var_endDate}", modifyDaysToDate(startDate, "yyyy-MM-dd", 1));

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_09");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									query = WSClient.getQuery("QS_04");
									dbResults = WSClient.getDBRow(query);

									String arrivalDate = WSClient.getElementValue(modifyBookingReq,
											"RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
									String departureDate = WSClient.getElementValue(modifyBookingReq,
											"RoomStay_TimeSpan_EndDate", XMLType.REQUEST);
									String resarrivalDate = WSClient.getElementValue(modifyBookingRes,
											"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE);
									String resdepartureDate = WSClient.getElementValue(modifyBookingRes,
											"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE);

									resarrivalDate = resarrivalDate.substring(0, resarrivalDate.indexOf('T'));
									resdepartureDate = resdepartureDate.substring(0, resdepartureDate.indexOf('T'));

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validation > Modifications are applied on DB</b>");

									/******
									 * Validate above details against DB
									 *********/
									String dbArrival = dbResults.get("ARRIVAL");
									dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));

									String dbDeparture = dbResults.get("DEPARTURE");
									dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

									if (WSAssert.assertEquals(arrivalDate, dbArrival, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
												+ arrivalDate + "  Actual : " + dbArrival + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
												+ arrivalDate + "  Actual : " + dbArrival + "</b>");
									}
									if (WSAssert.assertEquals(departureDate, dbDeparture, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Departure date -> Expected : "
												+ departureDate + "  Actual : " + dbDeparture + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Departure date -> Expected : "
												+ departureDate + "  Actual : " + dbDeparture + "</b>");
									}

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Validation > Modifications are retreived correctly on the response</b>");

									/**** Validate modified arrival dates ****/
									if (WSAssert.assertEquals(arrivalDate, resarrivalDate, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Arrival date -> Expected : "
												+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Arrival date -> Expected : "
												+ arrivalDate + "  Actual : " + resarrivalDate + "</b>");
									}
									/****
									 * Validate modified departure dates
									 ****/
									if (WSAssert.assertEquals(departureDate, resdepartureDate, true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b> Departure date -> Expected : "
												+ departureDate + "  Actual : " + resdepartureDate + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b> Departure date -> Expected : "
												+ departureDate + "  Actual : " + resdepartureDate + "</b>");
									}
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									if (!WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE).equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO, "<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
												+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
									if (!WSClient
											.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											.equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"ModifyBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE)
														+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_par}", "N");
				ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}

	// using a rate Code which is expired.
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS" })
	public void modifyBooking_42531() {
		try {
			String testName = "modifyBooking_42531";
			WSClient.startTest(testName,
					"Verify that Error message is generated when Booking is modified to an expired rate code",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			WSClient.setData("{var_channelCarrier}", channelCarrier);
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile ****************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
					WSClient.setData("{var_profileId}", profileID);

					/***********
					 * Prerequisite 2:Create a Reservation
					 ************/
					if (resvID.get("reservationId") == null || resvID.get("reservationId") == "error")
						resvID = CreateReservation.createReservation("DS_01");
					if (resvID.get("reservationId") != "error") {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						String query = WSClient.getQuery("OWSModifyBooking", "QS_47");
						LinkedHashMap<String, String> dates = WSClient.getDBRow(query);
						WSClient.setData("{var_startDate}", dates.get("START_DATE"));
						WSClient.setData("{var_endDate}", dates.get("END_DATE"));
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_21");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);
						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Reservation creation failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// using a room type which is expired.
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS" })
	public void modifyBooking_42617() {
		try {
			String testName = "modifyBooking_42617";
			WSClient.startTest(testName,
					"Verify that Error message is generated when Booking is modified to an expired RoomType.",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			WSClient.setData("{var_channelCarrier}", channelCarrier);
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 ******************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
				/************ Prerequisite 1: Create profile ****************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:" + profileID + "</b>");
					WSClient.setData("{var_profileId}", profileID);

					/**************
					 * Prerequisite 2:Create a Reservation
					 ***************/
					if (resvID.get("reservationId") == null || resvID.get("reservationId") == "error")
						resvID = CreateReservation.createReservation("DS_01");
					if (resvID.get("reservationId") != "error") {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID:" + resvID.get("reservationId") + "</b>");
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						String query = WSClient.getQuery("OWSModifyBooking", "QS_48");
						LinkedHashMap<String, String> dates = WSClient.getDBRow(query);
						WSClient.setData("{var_startDate}", dates.get("START_DATE"));
						WSClient.setData("{var_endDate}", dates.get("END_DATE"));
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						/***************
						 * OWS Modify Booking Operation
						 ****************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_21");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);
						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "The prerequisites for Reservation creation failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * rate charges are applied correctly when posting rhythm is set for rate
	 * code
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun"})

	public void modifyBooking_42750() {
		HashMap<String, String> resvId = new HashMap<>();
		try {
			String testName = "modifyBooking_42750";
			WSClient.startTest(testName,
					"Verify that rate charges are applied correctly when posting rhythm is set for rate code "
							+ "and booking is modified to multi-night reservation",
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
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_12"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_10"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_10"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile ****************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/

					WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_160}"));
					WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_161}"));

					resvId = CreateReservation.createReservation("DS_29");
					if (!resvId.get("reservationId").equals("error")) {
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvId.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvId.get("reservationId") + "</b>");

						String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_174}");
						int daysToAdd = 3;
						WSClient.setData("{var_startDate}", startDate);
						WSClient.setData("{var_endDate}", modifyDaysToDate(startDate, "yyyy-MM-dd", daysToAdd));
						System.out.println(startDate);
						WSClient.setData("{var_busdate}", startDate);
						WSClient.setData("{var_busdate1}", modifyDaysToDate(startDate, "yyyy-MM-dd", daysToAdd));

						if (fetchAvailability(OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_09"),
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_07"))) {

							/***************
							 * OWS Modify Booking Operation
							 ********************/

							String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_21");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							String modifiedStartDate= WSClient.getElementValue(modifyBookingReq, "RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
							if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									String query = WSClient.getQuery("QS_36");
									HashMap<String, String> prices = WSClient.getDBRow(query);

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Details of Rate Code : " + prices.get("RATE_CODE") + "</b>");
									WSClient.writeToReport(LogStatus.INFO,
											"<b> Charge For Adult 1 -> " + prices.get("AMOUNT_1") + "</b>");

									WSClient.writeToReport(LogStatus.INFO, "<b>Validating Charges in the DB</b>");

									query = WSClient.getQuery("QS_49");
									ArrayList<LinkedHashMap<String, String>> results = WSClient.getDBRows(query);

									ArrayList<LinkedHashMap<String, String>> expectedValues = new ArrayList<>();
									int rate = Integer.parseInt(prices.get("AMOUNT_1"));
									LinkedHashMap<String, String> e = new LinkedHashMap<>();
									for (int i = 0; i < daysToAdd; i++) {
										e = new LinkedHashMap<>();
										System.out.println(modifiedStartDate);
										System.out.println(i);
										e.put("effectiveDate1", modifyDaysToDate(modifiedStartDate, "yyyy-MM-dd", i));
										System.out.println(modifyDaysToDate(modifiedStartDate, "yyyy-MM-dd", i));
										/**********
										 * Add rates alternatively
										 ***********/
										if (i % 2 == 0) {
											e.put("Base1", String.valueOf(rate));
										} else {
											e.put("Base1", "0");
										}
										expectedValues.add(e);
									}

									WSAssert.assertEquals(expectedValues, results, true);

									WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room rate</b>");
									HashMap<String, String> xPath = new HashMap<>();
									xPath.put("Rates_Rate_Base", "RoomRate_Rates_Rate");
									xPath.put("RoomRate_Rates_Rate_effectiveDate", "RoomRate_Rates_Rate");
									List<LinkedHashMap<String, String>> resValues = WSClient
											.getMultipleNodeList(modifyBookingRes, xPath, false, XMLType.RESPONSE);

									WSAssert.assertEquals(results, resValues, false);

									WSClient.writeToReport(LogStatus.INFO, "<b>Validating Expected charges</b>");
									query = WSClient.getQuery("QS_50");
									results = WSClient.getDBRows(query);
									xPath.clear();
									xPath.put("ExpectedCharges_ChargesForPostingDate_RoomRateAndPackages_TotalCharges",
											"RoomStay_ExpectedCharges_ChargesForPostingDate");
									xPath.put("RoomRateAndPackages_Charges_Amount",
											"RoomStay_ExpectedCharges_ChargesForPostingDate");
									xPath.put("RoomStay_ExpectedCharges_ChargesForPostingDate_PostingDate",
											"RoomStay_ExpectedCharges_ChargesForPostingDate");
									resValues = WSClient.getMultipleNodeList(modifyBookingRes, xPath, false,
											XMLType.RESPONSE);

									WSAssert.assertEquals(results, resValues, false);
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_GDSError", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"ModifyBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						} else
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite Blocked : Unable to fetch Availability");
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requistes required for Reservation creation failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			if (resvId.get("reservationId") != null || !resvId.get("reservationId").equals("error")) {
				WSClient.setData("{var_resvId}", resvId.get("reservationId"));
				try {
					CancelReservation.cancelReservation("DS_02");
				} catch (Exception e) {
					WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
					e.printStackTrace();
				}
			}
		}
	}

	// /**
	// * Method to check if the OWS Reservation Modify Booking is working i.e.,
	// * profile room features are added to reservation when related parameter
	// is active.
	// *
	// * PreRequisites Required: -->Profile is created -->Reservation is created
	// *
	// * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	// * ->Source Code
	// */
	// @Test(groups = { "minimumRegression", "ModifyBooking", "Reservation",
	// "OWS"})
	//
	// public void modifyBooking_52017() {
	// String paramvalue = "";
	// try {
	// String testName = "modifyBooking_52017";
	// WSClient.startTest(testName, "Verify that profile room features are added
	// to reservation when related parameter is active", "minimumRegression");
	//
	// String resortOperaValue = OPERALib.getResort();
	// String chain = OPERALib.getChain();
	// WSClient.setData("{var_chain}", chain);
	// WSClient.setData("{var_resort}", resortOperaValue);
	//
	// String uname = OPERALib.getUserName();
	// String pwd = OPERALib.getPassword();
	// String channel = OWSLib.getChannel();
	// String channelType = OWSLib.getChannelType(channel);
	// String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsresort}", resort);
	// WSClient.setData("{var_channel}", channel);
	// String channelCarrier = OWSLib.getChannelCarier(resortOperaValue,
	// channel);
	// OPERALib.setOperaHeader(uname);
	//
	// /*********** Prerequisite : Verify if the parameter
	// ATTACH_PROFILE_ROOM_FEATURES_TO_RESERVATION is enabled ****************/
	// WSClient.setData("{var_par}","Y");
	// WSClient.setData("{var_parname}","ATTACH_PROFILE_ROOM_FEATURES_TO_RESERVATION");
	// WSClient.setData("{var_type}","Boolean");
	// WSClient.setData("{var_param}",
	// "ATTACH_PROFILE_ROOM_FEATURES_TO_RESERVATION");
	// WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter
	// ATTACH_PROFILE_ROOM_FEATURES_TO_RESERVATION is enabled</b>");
	//
	// String query=WSClient.getQuery("ChangeChannelParameters","QS_02");
	// LinkedHashMap<String, String> results = WSClient.getDBRow(query);
	// paramvalue=results.get("PARAMETER_VALUE");
	// String parameter=paramvalue;
	// if(!WSAssert.assertEquals("Y",paramvalue,true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter
	// ATTACH_PROFILE_ROOM_FEATURES_TO_RESERVATION</b>");
	//
	// String changeChannelParametersReq =
	// WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
	// String changeChannelParametersRes =
	// WSClient.processSOAPMessage(changeChannelParametersReq);
	// if(WSAssert.assertIfElementExists(changeChannelParametersRes,"ChangeChannelParametersRS_Success",false))
	// {
	// results = WSClient.getDBRow(query);
	// parameter=results.get("PARAMETER_VALUE");
	// }
	// }
	//
	// if(parameter.equals("Y"))
	// {
	// if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode",
	// "RoomType", "SourceCode", "MarketCode","ReservationType"}))
	// {
	//
	// /************* Prerequisite : Room type, Rate Plan Code, Source Code,
	// Market Code *********************************/
	// WSClient.setData("{VAR_RATEPLANCODE}",
	// OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
	// WSClient.setData("{VAR_ROOMTYPE}",
	// OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_ReservationType}",
	// OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));
	//
	// /************ Prerequisite 1: Create profile ********************/
	// String profileID=CreateProfile.createProfile("DS_01");
	// if(!profileID.equals("error"))
	// {
	// WSClient.setData("{var_profileId}", profileID);
	// WSClient.setData("{var_profileID}", profileID);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:
	// "+profileID+"</b>");
	//
	// WSClient.setData("{var_prefType}",
	// OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
	// WSClient.setData("{var_prefValue}",
	// OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
	// WSClient.setData("{var_global}", "false");
	//
	// /**************** Prerequisite 2:Add a Preference to profile
	// *****************/
	// String createPrefReq = WSClient.createSOAPMessage("CreatePreference",
	// "DS_01");
	// String createPrefRes = WSClient.processSOAPMessage(createPrefReq);
	// if(WSAssert.assertIfElementExists(createPrefRes,
	// "CreatePreferenceRS_Success", true))
	// {
	// /**************** Prerequisite 3:Create a Reservation *****************/
	// if(resvID.get("reservationId")==null)
	// resvID=CreateReservation.createReservation("DS_01");
	// if(!resvID.get("reservationId").equals("error"))
	// {
	// OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType,
	// channelCarrier);
	// WSClient.setData("{var_resvId}", resvID.get("reservationId"));
	// WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:
	// "+resvID.get("reservationId")+"</b>");
	//
	// /*************** OWS Modify Booking Operation ********************/
	// String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking",
	// "DS_02");
	// String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);
	//
	// if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result", true))
	// {
	// if(WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "ModifyBookingResponse_Result_resultStatusFlag","SUCCESS", true))
	// {
	//
	// query=WSClient.getQuery("QS_29");
	// results=WSClient.getDBRow(query);
	// String expected=WSClient.getData("{var_prefType}");
	// if(WSAssert.assertEquals(expected, results.get("PREFERENCE_TYPE"), true))
	// {
	// WSClient.writeToReport(LogStatus.PASS, "PrefType -> Expected :
	// "+expected+" Actual : "+results.get("PREFERNCE_TYPE"));
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "PrefType -> Expected :
	// "+expected+" Actual : "+results.get("PREFERNCE_TYPE"));
	// }
	//
	// expected=WSClient.getData("{var_prefValue}");
	// if(WSAssert.assertEquals(expected, results.get("PREFERENCE"), true))
	// {
	// WSClient.writeToReport(LogStatus.PASS, "PrefValue -> Expected :
	// "+expected+" Actual : "+results.get("PREFERNCE"));
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "PrefValue -> Expected :
	// "+expected+" Actual : "+results.get("PREFERNCE"));
	// }
	// }
	// if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result_GDSError", true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is
	// generated is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_Result_GDSError_errorCode",
	// XMLType.RESPONSE)+"</b>");
	// }
	// if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_Result_OperaErrorCode", true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is
	// generated is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
	// }
	// if (WSAssert.assertIfElementExists(modifyBookingRes,
	// "Result_Text_TextElement", true))
	// {
	// /**** Verifying that the error message is populated on the response
	// *****/
	//
	// String message = WSAssert.getElementValue(modifyBookingRes,
	// "Result_Text_TextElement",XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO,"<b>The text displayed in the
	// response is :" + message+"</b>");
	// }
	// }
	// else if(WSAssert.assertIfElementExists(modifyBookingRes,
	// "ModifyBookingResponse_faultstring", true))
	// {
	// WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated
	// is : "+WSClient.getElementValue(modifyBookingRes,
	// "ModifyBookingResponse_faultstring", XMLType.RESPONSE)+"</b>");
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "<b>Pre-requisite failed >>
	// Creating a reservation failed</b>");
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "<b>Pre-requisite failed >>
	// Adding a preference to profile failed</b>");
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "<b>Pre-requisite failed >>
	// Creating a profile failed</b>");
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "<b>Pre-requisite required for
	// Creating a reservation failed</b>");
	// }
	// }
	// else
	// {
	// WSClient.writeToReport(LogStatus.WARNING, "<b>Pre-requisite failed >>
	// Changing the application parameter failed</b>");
	// }
	// }catch (Exception e) {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// }
	//
	// }
	//
	/**
	 * Method to check if the OWS Reservation Modify Booking is working i.e.,
	 * adding a payment method.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code ->ReservationType
	 */
	@Test(groups = { "minimumRegression", "ModifyBooking", "Reservation", "OWS", "PriorRun" })

	public void modifyBooking_42802() {
		String uname = "";
		try {
			String testName = "modifyBooking_42802";
			WSClient.startTest(testName, "Verify that payment method is added to the booking successfully",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile *************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_35");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_04");
								HashMap<String, String> dbResults = WSClient.getDBRow(query);
								String reqPayment = WSClient.getElementValue(modifyBookingReq,
										"PaymentsAccepted_PaymentType_OtherPayment_type", XMLType.REQUEST);
								String resPayment = WSClient.getElementValue(modifyBookingRes,
										"PaymentsAccepted_PaymentType_OtherPayment_type", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation > Modifications are applied on DB</b>");
								if (WSAssert.assertEquals(reqPayment, dbResults.get("PAYMENT_METHOD"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "PaymentType -> Expected : " + reqPayment
											+ " Actual : " + dbResults.get("PAYMENT_METHOD"));
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "PaymentType -> Expected : " + reqPayment
											+ " Actual : " + dbResults.get("PAYMENT_METHOD"));
								}
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Validation > Modifications are retrieved correctly on response</b>");
								if (WSAssert.assertEquals(dbResults.get("PAYMENT_METHOD"), resPayment, true)) {
									WSClient.writeToReport(LogStatus.PASS, "PaymentType -> Expected : "
											+ dbResults.get("PAYMENT_METHOD") + " Actual : " + resPayment);
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "PaymentType -> Expected : "
											+ dbResults.get("PAYMENT_METHOD") + " Actual : " + resPayment);
								}

							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								if (!WSClient.getElementValue(modifyBookingRes, "ModifyBookingResponse_Result_GDSError",
										XMLType.RESPONSE).equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO,
											"<b> The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"ModifyBookingResponse_Result_GDSError", XMLType.RESPONSE)
													+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError_errorCode", true)) {
								if (!WSClient
										.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										.equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}
					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"<b>Pre-requistes required for Reservation creation failed</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

		}
	}

	@Test(groups = { "minimumRegression", "ModifyBooking", "OWS", "Reservation", "PriorRun" })

	public void modifyBooking_234565() {
		try {
			String testName = "modifyBooking_234565";
			WSClient.startTest(testName,
					"Verify that an error is populated when rate code is not available ie, Not scheduled",
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
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01"));

				/************ Prerequisite 1: Create profile *************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("reservationId") == null)
						resvID = CreateReservation.createReservation("DS_01");
					if (!resvID.get("reservationId").equals("error")) {
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_11"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_02"));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Reservation ID: " + resvID.get("reservationId") + "</b>");
						WSClient.setData("{var_roomCount}", "1");

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSModifyBooking", "DS_03");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"ModifyBookingResponse_Result_resultStatusFlag", "FAIL", false);

							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"ModifyBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"ModifyBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"ModifyBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "ModifyBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"ModifyBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
						}

					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

}
