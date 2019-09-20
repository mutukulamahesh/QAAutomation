package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeChannelParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchChannelParameters;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateBooking extends WSSetUp {

	String profileID = "", queueResvParameterEnabled = "", resvId = "", resv = null, resv1 = null;
	HashMap<String, String> resvID = new HashMap<>();

	public void fetchAvailability(String rate, String roomType) {
		try {
			int i = 100;
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
			WSClient.setData("{var_channelCarrier}", channelCarrier);
			String resvt = OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			int k = 0;
			while (k < 50) {
				String exRate = rate;
				String rt = roomType;
				// String exRate = "CKRC";
				// String rt = "3RT";
				WSClient.setData("{var_rt}", rt);
				WSClient.setData("{var_rate}", exRate);

				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_" + i + "}") + "T00:00:00");

				int j = i + 2;
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_" + j + "}") + "T00:00:00");

				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
				WSClient.setData("{var_oresort}", owsresort);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>Checking if rooms are available for the room type in the given date range</b>");

				String req_deleteDoc = WSClient.createSOAPMessage("OWSAvailability", "DS_01");
				String res_deleteDoc = WSClient.processSOAPMessage(req_deleteDoc);

				if (WSAssert.assertIfElementValueEquals(res_deleteDoc, "AvailabilityResponse_Result_resultStatusFlag",
						"SUCCESS", true)) {
					break;
				} else
					i = i + 1;
				k++;
			}

			if (k == 50)
				WSClient.writeToReport(LogStatus.WARNING, "AVAILABILITY FAILS");
		} catch (Exception e) {

		}
	}

	public String modifyDaysToDate(String date, String timeFormat, int days) {
		SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
		Calendar c;
		try {
			c = Calendar.getInstance();
			c.setTime(sdf.parse(date));
			c.add(Calendar.DATE, days); // number of days to modify
			date = sdf.format(c.getTime());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return date;
	}

	//Need to update to sanity
	@Test(groups = { "sanity", "CreateBooking", "OWS", "Reservation", "createBooking_1438395"})

	public void createBooking() {
		try {
			String testName = "createBooking_1438395";
			resv = null;

			WSClient.startTest(testName, "Verify that booking is Created by passing minimum required information", "sanity");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String channel = OWSLib.getChannel();
			WSClient.setData("{var_channel}", channel);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", OWSLib.getChannelResort(resortOperaValue, interfaceName));
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			// WSClient.setData("{var_rate}", "CKRC");
			// WSClient.setData("{var_roomType}", "4RT");
			// WSClient.setData("{var_resvType}", "QA_4PM");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
							profileID, false);
					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);

					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStays_RoomStay_Guarantee_guaranteeType",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					String resv1 = WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
					WSClient.writeToReport(LogStatus.INFO, "<b> Validating the records in DB </b>");
					String query = WSClient.getQuery("QS_01");
					LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
					String query1 = WSClient.getQuery("QS_03");
					LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
					if (WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"), bookingDets1.get("CONFIRMATION_NO"),
							true))
						WSClient.writeToReport(LogStatus.PASS,
								"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
										+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));
					else
						WSClient.writeToReport(LogStatus.FAIL,
								"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
										+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));

					if (WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"), bookingDets1.get("RESV_NAME_ID"), true))
						WSClient.writeToReport(LogStatus.PASS,
								"Reservation ID -> Expected value:" + bookingDets.get("RESV_NAME_ID")
										+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));
					else
						WSClient.writeToReport(LogStatus.FAIL,
								"Reservation ID-> Expected value:" + bookingDets.get("RESV_NAME_ID") + " Actual value :"
										+ bookingDets1.get("RESV_NAME_ID"));

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

					if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));

				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured :" + e);
		} finally {
			if (resv != null)
				try {
					CancelReservation.cancelReservation("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation", "createBooking_23981" })

	public void createBooking_23981() {
		try {
			resv = null;
			String resortOperaValue = OPERALib.getResort();

			String testName = "createBooking_23981";
			WSClient.startTest(testName, "Verify that a Single Day booking is created", "minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			// WSClient.setData("{var_resvType}", "QA_4PM");
			// WSClient.setData("{var_rate}", "CKRC");
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_160}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_165}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Fetch Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
							profileID, false);
					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);

					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStays_RoomStay_Guarantee_guaranteeType",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					String query = WSClient.getQuery("QS_01");
					LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

					if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));

				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured :" + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_23982() {
		try {
			resv = null;

			String testName = "createBooking_23982";
			WSClient.startTest(testName, "Verify that a Multi Day Booking is created", "minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			// WSClient.setData("{var_resvType}", "QA_4PM");
			// WSClient.setData("{var_rate}", "CKRC");
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//

			System.out.println("Profile Creation is successful 1");
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_170}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_175}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Fetch Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));

					}

					// validating the data
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
							profileID, false);
					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);

					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStays_RoomStay_Guarantee_guaranteeType",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					String query = WSClient.getQuery("QS_01");
					LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

					if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured :" + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	public void createBooking_24010() {
		try {
			resv = null;

			String testName = "createBooking_24010";
			WSClient.startTest(testName, "Verify that CreateBooking populates Reservation History accurately",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			// WSClient.setData("{var_resvType}", "QA_4PM");
			// WSClient.setData("{var_rate}", "CKRC");
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Fetch Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));

					}

					// validating the data
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					String query = WSClient.getQuery("QS_02");
					LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"CreateBookingResponse_HotelReservation_ReservationHistory_insertDate",
							bookingDets.get("INSERT_DATE"), false);

					WSClient.setData("{var_id}", bookingDets.get("INSERT_USER"));
					String query3 = WSClient.getQuery("QS_05");
					LinkedHashMap<String, String> bookingDets3 = WSClient.getDBRow(query3);
					String u = bookingDets3.get("APP_USER");
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"CreateBookingResponse_HotelReservation_ReservationHistory_insertUser", u, false);
					WSClient.setData("{var_id}", bookingDets.get("UPDATE_USER"));
					String query4 = WSClient.getQuery("QS_05");
					LinkedHashMap<String, String> bookingDets4 = WSClient.getDBRow(query4);
					String u1 = bookingDets4.get("APP_USER");

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"CreateBookingResponse_HotelReservation_ReservationHistory_updateUser", u1, false);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"CreateBookingResponse_HotelReservation_ReservationHistory_updateDate",
							bookingDets.get("UPDATE_DATE"), false);

				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured :" + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured :" + e);
			}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_39811() {
		try {
			resv = null;

			String testName = "createBooking_3981";
			WSClient.startTest(testName, "Verify that a profile is created using CreateBooking", "minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_extResort}", resortExtValue);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			// WSClient.setData("{var_resvType}", "QA_4PM");
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_120}") + "T06:00:00");
			WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_125}") + "T06:00:00");
			WSClient.setData("{var_time}", "09:00:00");

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
					OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
			fetchAvailability(rate, rt);

			String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_02");
			String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
			if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
					"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
				WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
						true)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Create Booking Response gives the error message-----></b> " + WSClient.getElementValue(
									updateProfileResponseXML, "CreateBookingResponse_Result_Text", XMLType.RESPONSE));
				}

				// validating the data

				String elem = WSClient.getElementValue(updateProfileResponseXML,
						"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
				String elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RatePlans_RatePlan_ratePlanCode",
						XMLType.REQUEST);
				WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

				if (WSAssert.assertEquals(elem, elem1, true)) {
					WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
				} else
					WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
				// WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
				// "Profile_ProfileIDs_UniqueID",
				// operaProfileID, false);
				elem = WSClient.getElementValue(updateProfileResponseXML, "RoomStay_RoomRates_RoomRate_roomTypeCode",
						XMLType.RESPONSE);
				elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
						XMLType.REQUEST);
				WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

				if (WSAssert.assertEquals(elem, elem1, true)) {
					WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
				} else
					WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);

				elem = WSClient.getElementValue(updateProfileResponseXML, "RoomStays_RoomStay_Guarantee_guaranteeType",
						XMLType.RESPONSE);
				elem1 = WSClient.getElementValue(updateProfileReq, "RoomStays_RoomStay_Guarantee_guaranteeType",
						XMLType.REQUEST);
				WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

				if (WSAssert.assertEquals(elem, elem1, true)) {
					WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
				} else
					WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
				WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
				WSClient.setData("{var_profileId}", WSClient.getElementValue(updateProfileResponseXML,
						"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID", false)) {
					WSClient.writeToReport(LogStatus.PASS,
							"Profile ID " + WSClient.getElementValue(updateProfileResponseXML,
									"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Profile Details from DB</b>");
					String query3 = WSClient.getQuery("QS_04");
					LinkedHashMap<String, String> bookingDets2 = WSClient.getDBRow(query3);
					String title = WSClient.getElementValue(updateProfileReq, "Customer_PersonName_nameTitle",
							XMLType.REQUEST);
					String first = WSClient.getElementValue(updateProfileReq, "Customer_PersonName_firstName",
							XMLType.REQUEST);
					String last = WSClient.getElementValue(updateProfileReq, "Customer_PersonName_lastName",
							XMLType.REQUEST);
					String nat = WSClient.getElementValue(updateProfileReq, "ResGuest_Profiles_Profile_nationality",
							XMLType.REQUEST);

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating title</b>");
					if (WSAssert.assertEquals(title, bookingDets2.get("TITLE"), true))
						WSClient.writeToReport(LogStatus.PASS,
								"<b>Expected : " + title + " Actual: " + bookingDets2.get("TITLE") + "</b>");
					else
						WSClient.writeToReport(LogStatus.FAIL,
								"<b>Expected : " + title + " Actual: " + bookingDets2.get("TITLE") + "</b>");

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating first name</b>");
					if (WSAssert.assertEquals(first, bookingDets2.get("SFIRST"), true))
						WSClient.writeToReport(LogStatus.PASS,
								"<b>Expected : " + first + " Actual: " + bookingDets2.get("SFIRST") + "</b>");
					else
						WSClient.writeToReport(LogStatus.FAIL,
								"<b>Expected : " + first + " Actual: " + bookingDets2.get("SFIRST") + "</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating last name</b>");

					if (WSAssert.assertEquals(last, bookingDets2.get("LAST"), true))
						WSClient.writeToReport(LogStatus.PASS,
								"<b>Expected : " + last + " Actual: " + bookingDets2.get("LAST") + "</b>");
					else
						WSClient.writeToReport(LogStatus.FAIL,
								"<b>Expected : " + last + " Actual: " + bookingDets2.get("LAST") + "</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating nationality</b>");

					if (WSAssert.assertEquals(nat, bookingDets2.get("NATIONALITY"), true))
						WSClient.writeToReport(LogStatus.PASS,
								"<b>Expected : " + nat + " Actual: " + bookingDets2.get("NATIONALITY") + "</b>");
					else
						WSClient.writeToReport(LogStatus.FAIL,
								"<b>Expected : " + nat + " Actual: " + bookingDets2.get("NATIONALITY") + "</b>");

				} else

					WSClient.writeToReport(LogStatus.FAIL, "Profile is not created");
			} else

				WSClient.writeToReport(LogStatus.FAIL, "Create Booking fails");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured :" + e);

		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	//
	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_38395() {
		try {
			resv = null;

			String testName = "createBooking_28395";
			WSClient.startTest(testName, "Verify that a Day Use booking is created", "minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			// WSClient.setData("{var_resvType}", "QA_4PM");
			// WSClient.setData("{var_rate}", "CKRC");
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_180}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_180}") + "T16:00:00");
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				WSClient.writeToReport(LogStatus.INFO, "<b>Create Booking with Day Use reservation</b> ");

				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
							profileID, false);
					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);

					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStays_RoomStay_Guarantee_guaranteeType",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
					String query = WSClient.getQuery("QS_01");
					LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
					String query1 = WSClient.getQuery("QS_03");
					LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
					WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"), bookingDets1.get("CONFIRMATION_NO"),
							false);
					WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"), bookingDets1.get("RESV_NAME_ID"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

					if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured :" + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_6729() {
		try {
			resv = null;

			String testName = "createBooking_6729";
			WSClient.startTest(testName, "Verify that booking is created uisng comments when guest viewable is True",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);

			WSClient.setData("{var_commentType}", "RESERVATION");

			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				// WSClient.setData("{var_busdate}",
				// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_150}").substring(0,
				// 19));
				// WSClient.setData("{var_busdate1}",
				// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_155}").substring(0,
				// 19));
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_130}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_135}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				WSClient.setData("{var_flag}", "true");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_03");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					String comment = "";
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data
					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));

					if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"RoomStay_Comments_Comment_guestViewable", "TRUE", false)) {
						comment = WSClient.getElementValue(updateProfileReq, "Comments_Comment_Text", XMLType.REQUEST);
						WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Comments_Comment_Text", comment,
								false);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Comment is not visible when guest viewable is true");
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating against DB</b>");
					LinkedHashMap<String, String> dbComments = WSClient
							.getDBRow(WSClient.getQuery("OWSModifyBooking", "QS_12"));

					if (WSAssert.assertEquals("RESERVATION", dbComments.get("COMMENT_TYPE"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Comment Type - Expected: RESERVATION,Actual : " + dbComments.get("COMMENT_TYPE"));
					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"Comment Type - Expected: RESERVATION,Actual : " + dbComments.get("COMMENT_TYPE"));
					}

					if (WSAssert.assertEquals(comment, dbComments.get("COMMENTS"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Comment Text - Expected: " + comment + ",Actual : " + dbComments.get("COMMENTS"));
					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"Comment Text - Expected: " + comment + ",Actual : " + dbComments.get("COMMENTS"));
					}
				} else

					WSClient.writeToReport(LogStatus.FAIL, "Create Booking fails");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_6730() {
		try {
			resv = null;

			String testName = "createBooking_6730";
			WSClient.startTest(testName, "Verify that booking is created using comments when guest viewable is false",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_commentType}", "IN HOUSE");

			WSClient.setData("{var_reservationType}", resvt);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				// WSClient.setData("{var_busdate}",
				// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_150}").substring(0,
				// 19));
				// WSClient.setData("{var_busdate1}",
				// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_155}").substring(0,
				// 19));
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_140}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_145}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				WSClient.setData("{var_flag}", "false");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_03");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data
					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));

					if (WSAssert.assertIfElementExists(updateProfileResponseXML,
							"RoomStay_Comments_Comment_guestViewable", true)) {
						WSClient.writeToReport(LogStatus.FAIL, "Comment is displyed when guest viewable is true");
					} else
						WSClient.writeToReport(LogStatus.PASS, "Comment is not displyed when guest viewable is false");
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating against DB</b>");
					LinkedHashMap<String, String> dbComments = WSClient
							.getDBRow(WSClient.getQuery("OWSModifyBooking", "QS_12"));

					if (WSAssert.assertEquals("IN HOUSE", dbComments.get("COMMENT_TYPE"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Comment Type - Expected: RESERVATION,Actual : " + dbComments.get("COMMENT_TYPE"));
					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"Comment Type - Expected: RESERVATION,Actual : " + dbComments.get("COMMENT_TYPE"));
					}
					String comment = WSClient.getElementValue(updateProfileReq, "Comments_Comment_Text",
							XMLType.REQUEST);
					if (WSAssert.assertEquals(comment, dbComments.get("COMMENTS"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Comment Text - Expected: " + comment + ",Actual : " + dbComments.get("COMMENTS"));
					} else {
						WSClient.writeToReport(LogStatus.FAIL,
								"Comment Text - Expected: " + comment + ",Actual : " + dbComments.get("COMMENTS"));
					}
				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation", "createBooking_48395_Uday" })

	public void createBooking_48395() {
		try {
			resv = null;

			String testName = "createBooking_48395";
			WSClient.startTest(testName, "Verify that booking is created with a web bookable package",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			String pkg = OperaPropConfig.getDataSetForCode("PackageCode", "DS_03");
			WSClient.setData("{var_pkg}", pkg);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);

			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true) == false) {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->Create Profile fails");
			} else {
				profileID = CreateProfile.createProfile("DS_00");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					WSClient.setData("{var_profileSource}", interfaceName);
					WSClient.setData("{var_resort}", resortOperaValue);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_busdate}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_250}") + "T06:00:00");
					WSClient.setData("{var_busdate1}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_255}") + "T06:00:00");
					WSClient.setData("{var_time}", "09:00:00");
					WSClient.writeToReport(LogStatus.INFO, "<b>Create Booking with packages</b>");

					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
							OWSLib.getChannelType(interfaceName),
							OWSLib.getChannelCarier(resortOperaValue, interfaceName));
					fetchAvailability(rate, rt);

					String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_08");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
					if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
						resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
						WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
						WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
						if (WSAssert.assertIfElementExists(updateProfileResponseXML,
								"CreateBookingResponse_Result_Text", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Create Booking Response gives the error message-----></b> "
											+ WSClient.getElementValue(updateProfileResponseXML,
													"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
						}

						// validating the data

						String elem = WSClient.getElementValue(updateProfileResponseXML,
								"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
						String elem1 = WSClient.getElementValue(updateProfileReq,
								"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

						if (WSAssert.assertEquals(elem, elem1, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + elem + " Actual value :" + elem1);
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + elem + " Actual value :" + elem1);
						WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
								profileID, false);
						elem = WSClient.getElementValue(updateProfileResponseXML,
								"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
						elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
								XMLType.REQUEST);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

						if (WSAssert.assertEquals(elem, elem1, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + elem + " Actual value :" + elem1);
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + elem + " Actual value :" + elem1);

						elem = WSClient.getElementValue(updateProfileResponseXML,
								"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
						elem1 = WSClient.getElementValue(updateProfileReq, "RoomStays_RoomStay_Guarantee_guaranteeType",
								XMLType.REQUEST);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

						if (WSAssert.assertEquals(elem, elem1, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + elem + " Actual value :" + elem1);
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + elem + " Actual value :" + elem1);
						WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));

						String elem2 = WSClient.getElementValue(updateProfileReq,
								"RoomStay_Packages_Package_packageCode", XMLType.REQUEST);
						WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"RoomStay_Packages_Package_packageCode", elem2, false);
						
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating from DB</b>");
						String query = WSClient.getQuery("OWSModifyBooking", "QS_05");
						LinkedHashMap<String, String> dbResults = WSClient.getDBRow(query);
						if (WSAssert.assertEquals(elem2, dbResults.get("packageCode1"), true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected : " + elem2 + " Actual : " + dbResults.get("packageCode1"));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected : " + elem2 + " Actual : " + dbResults.get("packageCode1"));

					} else

						WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				// if (resv != null)

				// CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking", "OWS",
	// "Reservation" })
	//
	// public void createBooking_multiplePkgs() {
	// try{
	// resv = null;
	//
	// String testName = "createBooking_38395";
	// WSClient.startTest(testName, "CreateBooking using multiple web bookable
	// packages", "minimumRegression");
	//
	// String interfaceName = OWSLib.getChannel();
	// String resortOperaValue = OPERALib.getResort();
	// String resortExtValue = OWSLib.getChannelResort(resortOperaValue,
	// interfaceName);
	//
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	//
	// WSClient.setData("{var_profileSource}", interfaceName);
	// String rate=OperaPropConfig.getChannelCodeForDataSet("RateCode",
	// "DS_02");
	// WSClient.setData("{var_rate}", rate);
	// String rt=OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
	// WSClient.setData("{var_roomType}", rt);
	// String ort=OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
	// WSClient.setData("{var_orate}", ort);
	// String resvt=OperaPropConfig.getDataSetForCode("ReservationType",
	// "DS_01");
	// WSClient.setData("{var_resvType}", resvt);
	// WSClient.setData("{var_reservationType}", resvt);
	// WSClient.setData("{var_chain}", OPERALib.getChain());
	//
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	// WSClient.setData("{var_owsresort}", resortExtValue);
	//
	// String pkg = OperaPropConfig.getDataSetForCode("PackageCode", "DS_03");
	// WSClient.setData("{var_pkg}", pkg);
	// String pkg2 = OperaPropConfig.getDataSetForCode("PackageCode", "DS_02");
	// WSClient.setData("{var_pkg2}", pkg2);
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// // ****** Prerequisite : Creating a Profile with basic details*****//
	//
	// profileID = CreateProfile.createProfile("DS_00");
	// if (!profileID.equals("error")) {
	// WSClient.setData("{var_profileId}", profileID);
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	// WSClient.setData("{var_busdate}",
	// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_150}").substring(0,
	// 19));
	// WSClient.setData("{var_busdate1}",
	// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_155}").substring(0,
	// 19));
	// WSClient.setData("{var_time}", "09:00:00");
	// WSClient.writeToReport(LogStatus.INFO, "<b>Create Booking with multiple
	// packages</b>");
	//
	// OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// resortExtValue,
	// OWSLib.getChannelType(interfaceName),
	// OWSLib.getChannelCarier(resortOperaValue, interfaceName));
	// String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking",
	// "DS_29");
	// String updateProfileResponseXML =
	// WSClient.processSOAPMessage(updateProfileReq);
	// if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
	// "CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
	// WSClient.setData("{var_resvId}",
	// WSClient.getElementValueByAttribute(updateProfileResponseXML,
	// "HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
	// resv=WSClient.getElementValueByAttribute(updateProfileResponseXML,
	// "HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
	// WSClient.setData("{var_resvId}",
	// WSClient.getElementValueByAttribute(updateProfileResponseXML,
	// "HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
	// WSClient.setData("{var_resv}",
	// WSClient.getElementValue(updateProfileResponseXML,
	// "HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
	// if (WSAssert.assertIfElementExists(updateProfileResponseXML,
	// "CreateBookingResponse_Result_Text",
	// true)) {
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>Create Booking Response gives the error message-----></b> " +
	// WSClient.getElementValue(
	// updateProfileResponseXML, "CreateBookingResponse_Result_Text",
	// XMLType.RESPONSE));
	// }
	//
	// // validating the data
	//
	// String elem = WSClient.getElementValue(updateProfileResponseXML,
	// "RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
	// String elem1 = WSClient.getElementValue(updateProfileReq,
	// "RoomStay_RatePlans_RatePlan_ratePlanCode",
	// XMLType.REQUEST);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan
	// Code</b>");
	//
	// if (WSAssert.assertEquals(elem, elem1, true)) {
	// WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + "
	// Actual value :" + elem1);
	// } else
	// WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + "
	// Actual value :" + elem1);
	// WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
	// "Profile_ProfileIDs_UniqueID", profileID,
	// false);
	// elem = WSClient.getElementValue(updateProfileResponseXML,
	// "RoomStay_RoomRates_RoomRate_roomTypeCode",
	// XMLType.RESPONSE);
	// elem1 = WSClient.getElementValue(updateProfileReq,
	// "RoomStay_RoomTypes_RoomType_roomTypeCode",
	// XMLType.REQUEST);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Packages</b>");
	//
	// WSClient.setData("{var_resv}",
	// WSClient.getElementValue(updateProfileResponseXML,
	// "HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
	//
	// HashMap<String, String> xPath = new HashMap<String, String>();
	// xPath.put("RoomStay_Packages_Package_packageCode",
	// "RoomStays_RoomStay_Packages");
	// xPath.put("RoomStay_Packages_Package[2]_packageCode",
	// "RoomStays_RoomStay_Packages");
	// List<LinkedHashMap<String, String>> respData =
	// WSClient.getMultipleNodeList(updateProfileReq, xPath,
	// false, XMLType.REQUEST);
	//
	// xPath.put("RoomStay_Packages_Package_packageCode",
	// "RoomStays_RoomStay_Packages");
	// xPath.put("RoomStay_Packages_Package[2]_packageCode",
	// "RoomStays_RoomStay_Packages");
	// List<LinkedHashMap<String, String>> reqData =
	// WSClient.getMultipleNodeList(updateProfileResponseXML,
	// xPath, false, XMLType.RESPONSE);
	//
	// WSAssert.assertEquals(reqData, respData, false);
	//
	// } else
	//
	// WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
	//
	// }
	// }
	// catch(Exception e)
	// {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due
	// to:" + e);
	// }finally
	// {
	// try {
	// if(resv!=null)
	// CancelReservation.cancelReservation("DS_02");
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// }

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_383111() {
		try {
			resv = null;

			String testName = "createBooking_381115";
			WSClient.startTest(testName, "Verify that booking is not created using non web bookable package",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String pkg = OperaPropConfig.getDataSetForCode("PackageCode", "DS_01");
			WSClient.setData("{var_pkg}", pkg);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);

			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true) == false) {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->Create Profile fails");
			} else {
				profileID = CreateProfile.createProfile("DS_00");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					WSClient.setData("{var_profileSource}", interfaceName);
					WSClient.setData("{var_resort}", resortOperaValue);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_busdate}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_350}") + "T06:00:00");
					WSClient.setData("{var_busdate1}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_355}") + "T06:00:00");
					WSClient.setData("{var_time}", "09:00:00");
					WSClient.writeToReport(LogStatus.INFO, "<b>Create Booking with non web bookable packages</b>");

					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
							OWSLib.getChannelType(interfaceName),
							OWSLib.getChannelCarier(resortOperaValue, interfaceName));
					fetchAvailability(rate, rt);

					String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_08");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML,
							"CreateBookingResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
							if (WSAssert.assertIfElementExists(updateProfileResponseXML,
									"CreateBookingResponse_Result_OperaErrorCode", false)) {
								WSClient.setData("{var_resvId}",
										WSClient.getElementValueByAttribute(updateProfileResponseXML,
												"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
								resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
										"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
								WSClient.setData("{var_resvId}",
										WSClient.getElementValueByAttribute(updateProfileResponseXML,
												"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
								WSClient.writeToReport(LogStatus.PASS,
										"<b>Create Booking Response gives the error message-----></b> "
												+ WSClient.getElementValue(updateProfileResponseXML,
														"CreateBookingResponse_Result_OperaErrorCode",
														XMLType.RESPONSE));
							}

						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Create Booking Response does not give an error message when a web bookable package is attached");
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Create Booking fails");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking",
	// "OWS","Reservation"})
	//
	// public void createBooking_1111() {
	// String testName = "createBooking_38395";
	// WSClient.startTest(testName, "CreateBooking", "minimumRegression");
	// String interfaceName = OWSLib.getChannel();
	// String resortOperaValue = OPERALib.getResort();
	// String resortExtValue = OWSLib.getChannelResort(resortOperaValue,
	// interfaceName);
	//
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	//
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// //****** Prerequisite : Creating a Profile with basic details*****//
	//
	// profileID=CreateProfile.createProfile("DS_01");
	// if(!profileID.equals("error"))
	// {
	// WSClient.setData("{var_profileId}", profileID);
	//
	//
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	// WSClient.setData("{var_busdate}",
	// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_150}").substring(0,
	// 19));
	// WSClient.setData("{var_busdate1}",
	// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_155}").substring(0,
	// 19));
	// WSClient.setData("{var_time}", "09:00:00");
	// WSClient.writeToReport(LogStatus.INFO, "<b>Create Booking with
	// packages</b>");
	//
	// OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// resortExtValue,
	// OWSLib.getChannelType(interfaceName),
	// OWSLib.getChannelCarier(resortOperaValue, interfaceName));
	// String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking",
	// "DS_05");
	// String updateProfileResponseXML =
	// WSClient.processSOAPMessage(updateProfileReq);
	// if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
	// "CreateBookingResponse_Result_resultStatusFlag","SUCCESS", false))
	// {
	// if(WSAssert.assertIfElementExists(updateProfileResponseXML,
	// "CreateBookingResponse_Result_Text", true))
	// {
	// WSClient.writeToReport(LogStatus.INFO,"<b>Create Booking Response gives
	// the error message-----></b>
	// "+WSClient.getElementValue(updateProfileResponseXML,
	// "CreateBookingResponse_Result_Text", XMLType.RESPONSE));
	// }
	//
	// // validating the data
	//
	// String elem=WSClient.getElementValue(updateProfileResponseXML,
	// "RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
	// String elem1=WSClient.getElementValue(updateProfileReq,
	// "RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan
	// Code</b>");
	//
	// if(WSAssert.assertEquals(elem, elem1, true)){
	// WSClient.writeToReport(LogStatus.PASS, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// }
	// else
	// WSClient.writeToReport(LogStatus.FAIL, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
	// "Profile_ProfileIDs_UniqueID",
	// profileID, false);
	// elem=WSClient.getElementValue(updateProfileResponseXML,
	// "RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
	// elem1=WSClient.getElementValue(updateProfileReq,
	// "RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type
	// Code</b>");
	//
	// if(WSAssert.assertEquals(elem, elem1, true)){
	// WSClient.writeToReport(LogStatus.PASS, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// }
	// else
	// WSClient.writeToReport(LogStatus.FAIL, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	//
	// elem=WSClient.getElementValue(updateProfileResponseXML,
	// "RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
	// elem1=WSClient.getElementValue(updateProfileReq,
	// "RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type
	// Code</b>");
	//
	// if(WSAssert.assertEquals(elem, elem1, true)){
	// WSClient.writeToReport(LogStatus.PASS, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// }
	// else
	// WSClient.writeToReport(LogStatus.FAIL, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// WSClient.setData("{var_resv}",WSClient.getElementValue(updateProfileResponseXML,"HotelReservation_UniqueIDList_UniqueID",
	// XMLType.RESPONSE));
	// String query=WSClient.getQuery("QS_01");
	// LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
	// String query1=WSClient.getQuery("QS_03");
	// LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
	// WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"),bookingDets1.get("CONFIRMATION_NO")
	// ,false);
	// WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"),bookingDets1.get("RESV_NAME_ID")
	// ,false);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation
	// No</b>");
	//
	// WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
	// "HotelReservation_UniqueIDList_UniqueID",
	// bookingDets.get("CONFIRMATION_NO"), false);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation
	// No</b>");
	//
	// WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
	// "CreateBookingResponse_HotelReservation_UniqueIDList",
	// bookingDets.get("CONFIRMATION_NO")+bookingDets.get("RESV_NAME_ID"),
	// false);
	//
	//
	// }else
	//
	// WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
	// }
	// }

	// @Test(groups = { "minimumRegression", "CreateBooking", "OWS",
	// "Reservation" })

	public void createBooking_3981() {
		try {
			resv = null;

			String testName = "createBooking_3981";
			WSClient.startTest(testName, "Verify that Create booking retrieves valid profile details",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_100}") + "T06:00:00");
			WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_105}") + "T06:00:00");
			WSClient.setData("{var_time}", "09:00:00");

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
					OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
			fetchAvailability(rate, rt);

			String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_02");
			String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
			if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
					"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
				WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
						true)) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Create Booking Response gives the error message-----></b> " + WSClient.getElementValue(
									updateProfileResponseXML, "CreateBookingResponse_Result_Text", XMLType.RESPONSE));
				}

				// validating the data

				String elem = WSClient.getElementValue(updateProfileResponseXML,
						"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
				String elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RatePlans_RatePlan_ratePlanCode",
						XMLType.REQUEST);
				WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

				if (WSAssert.assertEquals(elem, elem1, true)) {
					WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
				} else
					WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
				// WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
				// "Profile_ProfileIDs_UniqueID",
				// operaProfileID, false);
				elem = WSClient.getElementValue(updateProfileResponseXML, "RoomStay_RoomRates_RoomRate_roomTypeCode",
						XMLType.RESPONSE);
				elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
						XMLType.REQUEST);
				WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

				if (WSAssert.assertEquals(elem, elem1, true)) {
					WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
				} else
					WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);

				elem = WSClient.getElementValue(updateProfileResponseXML, "RoomStays_RoomStay_Guarantee_guaranteeType",
						XMLType.RESPONSE);
				elem1 = WSClient.getElementValue(updateProfileReq, "RoomStays_RoomStay_Guarantee_guaranteeType",
						XMLType.REQUEST);
				WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

				if (WSAssert.assertEquals(elem, elem1, true)) {
					WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
				} else
					WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
				WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
				WSClient.setData("{var_profileId}", WSClient.getElementValue(updateProfileResponseXML,
						"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID", false)) {
					WSClient.writeToReport(LogStatus.PASS,
							"Profile ID " + WSClient.getElementValue(updateProfileResponseXML,
									"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));
					String query = WSClient.getQuery("QS_01");
					LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
					String query1 = WSClient.getQuery("QS_03");
					LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
					WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"), bookingDets1.get("CONFIRMATION_NO"),
							false);
					WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"), bookingDets1.get("RESV_NAME_ID"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");
					String query3 = WSClient.getQuery("QS_04");
					LinkedHashMap<String, String> bookingDets2 = WSClient.getDBRow(query3);
					String title = WSClient.getElementValue(updateProfileReq, "Customer_PersonName_nameTitle",
							XMLType.REQUEST);
					String first = WSClient.getElementValue(updateProfileReq, "Customer_PersonName_firstName",
							XMLType.REQUEST);
					String last = WSClient.getElementValue(updateProfileReq, "Customer_PersonName_lastName",
							XMLType.REQUEST);
					String nat = WSClient.getElementValue(updateProfileReq, "ResGuest_Profiles_Profile_nationality",
							XMLType.REQUEST);

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating title</b>");
					WSAssert.assertEquals(title, bookingDets2.get("TITLE"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating first name</b>");
					WSAssert.assertEquals(first, bookingDets2.get("SFIRST"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating last name</b>");

					WSAssert.assertEquals(last, bookingDets2.get("LAST"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating nationality</b>");

					WSAssert.assertEquals(nat, bookingDets2.get("NATIONALITY"), false);

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

					if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
				} else

					WSClient.writeToReport(LogStatus.FAIL, "Profile is not created");
			} else

				WSClient.writeToReport(LogStatus.FAIL, "Create Booking fails");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);

		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking2() {
		try {
			resv = null;

			String testName = "createBooking_35395";
			WSClient.startTest(testName, "Verify that Booking is created with arrival and departure transport details",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//

			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_120}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_125}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				WSClient.writeToReport(LogStatus.INFO,
						"<b>Create Booking with arrival and departure transport details</b>");
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);
				WSClient.setData("{var_busdate}", WSClient.getData("{var_busdate}").substring(0, 10));
				WSClient.setData("{var_busdate1}", WSClient.getData("{var_busdate1}").substring(0, 10));

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_10");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					WSClient.setData("{var_resv_Id}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
							profileID, false);
					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Arrival and Departure Details</b>");
					String arr_cc = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_ArrivalTransport_carrierCode", XMLType.REQUEST);
					String arr_id = WSClient.getElementValue(updateProfileReq, "ResGuests_ResGuest_ArrivalTransport_id",
							XMLType.REQUEST);
					String arr_lc = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_ArrivalTransport_locationCode", XMLType.REQUEST);
					String arr_time = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_ArrivalTransport_time", XMLType.REQUEST) + "T00:00:00";
					String dep_cc = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_DepartureTransport_carrierCode", XMLType.REQUEST);
					String dep_id = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_DepartureTransport_id", XMLType.REQUEST);
					String dep_lc = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_DepartureTransport_locationCode", XMLType.REQUEST);
					String dep_time = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_DepartureTransport_time", XMLType.REQUEST) + "T00:00:00";
					String dep_type = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_DepartureTransport_type", XMLType.REQUEST);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"ResGuests_ResGuest_ArrivalTransport_carrierCode", arr_cc, false);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"ResGuests_ResGuest_ArrivalTransport_id", arr_id, false);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"ResGuests_ResGuest_ArrivalTransport_locationCode", arr_lc, false);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"ResGuests_ResGuest_ArrivalTransport_time", arr_time, false);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"ResGuests_ResGuest_DepartureTransport_carrierCode", dep_cc, false);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"ResGuests_ResGuest_DepartureTransport_id", dep_id, false);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"ResGuests_ResGuest_DepartureTransport_locationCode", dep_lc, false);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"ResGuests_ResGuest_DepartureTransport_time", dep_time, false);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"ResGuests_ResGuest_DepartureTransport_type", dep_type, false);
					String query = WSClient.getQuery("OWSModifyBooking", "QS_37");
					LinkedHashMap<String, String> results = WSClient.getDBRow(query);

					String reqAid = WSClient.getElementValue(updateProfileReq, "ResGuests_ResGuest_ArrivalTransport_id",
							XMLType.REQUEST);
					String reqAlocation = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_ArrivalTransport_locationCode", XMLType.REQUEST);
					String reqAtime = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_ArrivalTransport_time", XMLType.REQUEST);
					// String reqAtype =
					// WSClient.getElementValue(updateProfileReq,
					// "ResGuests_ResGuest_ArrivalTransport_type",
					// XMLType.REQUEST);
					// String reqAflag =
					// WSClient.getElementValue(updateProfileReq,
					// "ResGuests_ResGuest_ArrivalTransport_transportationRequired",
					// XMLType.REQUEST);
					String reqACarrier = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_ArrivalTransport_carrierCode", XMLType.REQUEST);

					WSClient.writeToReport(LogStatus.INFO,
							"<b>Validating addition of arrival transport details in the DB</b>");
					/***************
					 * Validating arrival transport details
					 **************/
					if (WSAssert.assertEquals(reqAid, results.get("ARRIVAL_TRANSPORT_CODE"), true)) {
						WSClient.writeToReport(LogStatus.PASS, "<b> TransportId -> Expected : " + reqAid + "  Actual : "
								+ results.get("ARRIVAL_TRANSPORT_CODE") + "</b>");
					} else {
						WSClient.writeToReport(LogStatus.FAIL, "<b> TransportId -> Expected : " + reqAid + "  Actual : "
								+ results.get("ARRIVAL_TRANSPORT_CODE") + "</b>");
					}

					if (WSAssert.assertEquals(reqAlocation, results.get("ARRIVAL_STATION_CODE"), true)) {
						WSClient.writeToReport(LogStatus.PASS, "<b> TransportLocation -> Expected : " + reqAlocation
								+ "  Actual : " + results.get("ARRIVAL_STATION_CODE") + "</b>");
					} else {
						WSClient.writeToReport(LogStatus.FAIL, "<b> TransportLocation -> Expected : " + reqAlocation
								+ "  Actual : " + results.get("ARRIVAL_STATION_CODE") + "</b>");
					}

					// \
					// if (WSAssert.assertEquals(reqAflag,
					// results.get("ARRIVAL_TRANPORTATION_YN"), true)) {
					// WSClient.writeToReport(LogStatus.PASS,
					// "<b> TransportRequired -> Expected : " + reqAflag + "
					// Actual : "
					// + results.get("ARRIVAL_TRANPORTATION_YN") + "</b>");
					// } else {
					// WSClient.writeToReport(LogStatus.FAIL,
					// "<b> TransportRequired -> Expected : " + reqAflag + "
					// Actual : "
					// + results.get("ARRIVAL_TRANPORTATION_YN") + "</b>");
					// }
					if (WSAssert.assertEquals(reqACarrier, results.get("ARRIVAL_CARRIER_CODE"), true)) {
						WSClient.writeToReport(LogStatus.PASS, "<b> TransportCarrier -> Expected : " + reqACarrier
								+ "  Actual : " + results.get("ARRIVAL_CARRIER_CODE") + "</b>");
					} else {
						WSClient.writeToReport(LogStatus.FAIL, "<b> TransportCarrier -> Expected : " + reqACarrier
								+ "  Actual : " + results.get("ARRIVAL_CARRIER_CODE") + "</b>");
					}

					String reqDid = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_DepartureTransport_id", XMLType.REQUEST);
					String reqDlocation = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_DepartureTransport_locationCode", XMLType.REQUEST);
					String reqDtime = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_DepartureTransport_time", XMLType.REQUEST);
					String reqDtype = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_DepartureTransport_type", XMLType.REQUEST);
					String reqDflag = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_DepartureTransport_transportationRequired", XMLType.REQUEST);
					String reqDCarrier = WSClient.getElementValue(updateProfileReq,
							"ResGuests_ResGuest_DepartureTransport_carrierCode", XMLType.REQUEST);

					/***************
					 * Validating departure transport details
					 **************/
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Validating addition of departure transport details in the DB</b>");
					if (WSAssert.assertEquals(reqDid, results.get("DEPARTURE_TRANSPORT_CODE"), true)) {
						WSClient.writeToReport(LogStatus.PASS, "<b> TransportId -> Expected : " + reqDid + "  Actual : "
								+ results.get("DEPARTURE_TRANSPORT_CODE") + "</b>");
					} else {
						WSClient.writeToReport(LogStatus.FAIL, "<b> TransportId -> Expected : " + reqDid + "  Actual : "
								+ results.get("DEPARTURE_TRANSPORT_CODE") + "</b>");
					}

					if (WSAssert.assertEquals(reqDlocation, results.get("DEPARTURE_STATION_CODE"), true)) {
						WSClient.writeToReport(LogStatus.PASS, "<b> TransportLocation -> Expected : " + reqDlocation
								+ "  Actual : " + results.get("DEPARTURE_STATION_CODE") + "</b>");
					} else {
						WSClient.writeToReport(LogStatus.FAIL, "<b> TransportLocation -> Expected : " + reqDlocation
								+ "  Actual : " + results.get("DEPARTURE_STATION_CODE") + "</b>");
					}
					String dtime = results.get("DEPARTURE_DATE_TIME");
					dtime = dtime.substring(0, dtime.indexOf(' ')) + "T" + dtime.substring(dtime.indexOf(' ') + 1);

					if (WSAssert.assertEquals(reqDtype, results.get("DEPARTURE_TRANSPORT_TYPE"), true)) {
						WSClient.writeToReport(LogStatus.PASS, "<b> TransportType -> Expected : " + reqDtype
								+ "  Actual : " + results.get("DEPARTURE_TRANSPORT_TYPE") + "</b>");
					} else {
						WSClient.writeToReport(LogStatus.FAIL, "<b> TransportType -> Expected : " + reqDtype
								+ "  Actual : " + results.get("DEPARTURE_TRANSPORT_TYPE") + "</b>");
					}
					// if (WSAssert.assertEquals(reqDflag,
					// results.get("DEPARTURE_TRANSPORTATION_YN"), true)) {
					// WSClient.writeToReport(LogStatus.PASS,
					// "<b> TransportRequired -> Expected : " + reqDflag + "
					// Actual : "
					// + results.get("DEPARTURE_TRANSPORTATION_YN") + "</b>");
					// } else {
					// WSClient.writeToReport(LogStatus.FAIL,
					// "<b> TransportRequired -> Expected : " + reqDflag + "
					// Actual : "
					// + results.get("DEPARTURE_TRANSPORTATION_YN") + "</b>");
					// }
					if (WSAssert.assertEquals(reqDCarrier, results.get("DEPARTURE_CARRIER_CODE"), true)) {
						WSClient.writeToReport(LogStatus.PASS, "<b> TransportCarrier -> Expected : " + reqDCarrier
								+ "  Actual : " + results.get("DEPARTURE_CARRIER_CODE") + "</b>");
					} else {
						WSClient.writeToReport(LogStatus.FAIL, "<b> TransportCarrier -> Expected : " + reqDCarrier
								+ "  Actual : " + results.get("DEPARTURE_CARRIER_CODE") + "</b>");
					}

					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));

				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking11() {
		try {
			resv = null;

			String testName = "createBooking_377395";
			WSClient.startTest(testName, "Verify that Booking is created with special requests", "minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_156}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_158}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				WSClient.writeToReport(LogStatus.INFO, "<b>Create Booking using special requests</b>");
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_11");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
							profileID, false);
					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
							XMLType.REQUEST);

					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Special Requests with response</b>");

					String reqCode = WSClient.getElementValue(updateProfileReq,
							"RoomStay_SpecialRequests_SpecialRequest_requestCode", XMLType.REQUEST);
					String reqCode2 = WSClient.getElementValue(updateProfileReq,
							"RoomStay_SpecialRequests_SpecialRequest[2]_requestCode", XMLType.REQUEST);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"RoomStay_SpecialRequests_SpecialRequest_requestCode", reqCode, false);
					HashMap<String, String> xPath = new HashMap<String, String>();
					xPath.put("RoomStay_SpecialRequests_SpecialRequest_requestCode",
							"RoomStay_SpecialRequests_SpecialRequest");
					List<LinkedHashMap<String, String>> respData = WSClient.getMultipleNodeList(updateProfileReq, xPath,
							false, XMLType.REQUEST);
					xPath.put("RoomStay_SpecialRequests_SpecialRequest_requestCode",
							"RoomStay_SpecialRequests_SpecialRequest");
					List<LinkedHashMap<String, String>> reqData = WSClient.getMultipleNodeList(updateProfileResponseXML,
							xPath, false, XMLType.RESPONSE);

					WSAssert.assertEquals(reqData, respData, false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Special Requests with DB</b>");
					String query = WSClient.getQuery("OWSCreateBooking", "QS_28");
					ArrayList<LinkedHashMap<String, String>> results = WSClient.getDBRows(query);

					List<LinkedHashMap<String, String>> resValues = WSAssert
							.getMultipleNodeList(updateProfileResponseXML, xPath, false, XMLType.RESPONSE);
					List<LinkedHashMap<String, String>> reqValues = WSAssert.getMultipleNodeList(updateProfileReq,
							xPath, false, XMLType.REQUEST);

					WSAssert.assertEquals(reqValues, results, false);
				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

//	Need to debug later
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_383395_GuestCounts() {
		try {
			resv = null;

			String testName = "createBooking_383395";
			WSClient.startTest(testName,
					"Verify the Guest Counts are correctly populated on the Create Booking response message",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//

			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_550}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_555}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				fetchAvailability(rate, rt);

				WSClient.writeToReport(LogStatus.INFO, "<b>Create Booking using Guest Count</b>");
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_12");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					HashMap<String, String> xPath = new HashMap<String, String>();
					xPath.put("RoomStay_GuestCounts_GuestCount_ageQualifyingCode", "RoomStay_GuestCounts_GuestCount");
					xPath.put("RoomStay_GuestCounts_GuestCount_count", "RoomStay_GuestCounts_GuestCount");

					List<LinkedHashMap<String, String>> reqData = WSClient.getMultipleNodeList(updateProfileReq, xPath,
							false, XMLType.REQUEST);
					HashMap<String, String> xPath1 = new HashMap<String, String>();

					xPath1.put("RoomStay_GuestCounts_GuestCount_ageQualifyingCode", "RoomStay_GuestCounts_GuestCount");
					xPath1.put("RoomStay_GuestCounts_GuestCount_count", "RoomStay_GuestCounts_GuestCount");

					List<LinkedHashMap<String, String>> respData = WSClient
							.getMultipleNodeList(updateProfileResponseXML, xPath1, false, XMLType.RESPONSE);

					WSAssert.assertEquals(reqData, respData, false);
					String query = WSClient.getQuery("OWSModifyBooking", "QS_04");
					HashMap<String, String> dbResults = WSClient.getDBRow(query);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Against DB</b>");
					/**** Validate modified adult guest count ****/
					String adultCount = WSClient.getElementValue(updateProfileReq,
							"RoomStay_GuestCounts_GuestCount_count", XMLType.REQUEST);
					String childCount = WSClient.getElementValue(updateProfileReq,
							"RoomStay_GuestCounts_GuestCount[2]_count", XMLType.REQUEST);
					if (WSAssert.assertEquals(adultCount, dbResults.get("ADULTS"), true)) {
						WSClient.writeToReport(LogStatus.PASS, "<b> Adult Guest Count -> Expected : " + adultCount
								+ "  Actual : " + dbResults.get("ADULTS") + "</b>");
					} else {
						WSClient.writeToReport(LogStatus.FAIL, "<b> Adult Guest Count -> Expected : " + adultCount
								+ "  Actual : " + dbResults.get("ADULTS") + "</b>");
					}
					/**** Validate modified child guest count ****/
					if (WSAssert.assertEquals(childCount, dbResults.get("CHILDREN"), true)) {
						WSClient.writeToReport(LogStatus.PASS, "<b> Child Guest Count -> Expected : " + childCount
								+ "  Actual : " + dbResults.get("CHILDREN") + "</b>");
					} else {
						WSClient.writeToReport(LogStatus.FAIL, "<b> Child Guest Count -> Expected : " + childCount
								+ "  Actual : " + dbResults.get("CHILDREN") + "</b>");
					}
					// WSAssert.assertEquals(reqData1, respData1, false);
				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking",
	// "OWS","Reservation"})
	//
	// public void createBooking1111() {
	// String testName = "createBooking_38395";
	// WSClient.startTest(testName, "CreateBooking", "minimumRegression");
	// String interfaceName = OWSLib.getChannel();
	// String resortOperaValue = OPERALib.getResort();
	// String resortExtValue = OWSLib.getChannelResort(resortOperaValue,
	// interfaceName);
	//
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	//
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// //****** Prerequisite : Creating a Profile with basic details*****//
	// String createProfileReq = WSClient.createSOAPMessage("CreateProfile",
	// "DS_01");
	// String createProfileResponseXML =
	// WSClient.processSOAPMessage(createProfileReq);
	//
	// System.out.println("Profile Creation is successful 1");
	// profileID=CreateProfile.createProfile("DS_01");
	// if(!profileID.equals("error"))
	// {
	// WSClient.setData("{var_profileId}", profileID);
	//
	//
	//
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	// WSClient.setData("{var_busdate}",
	// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_555}").substring(0,
	// 19));
	// WSClient.setData("{var_busdate1}",
	// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_556}").substring(0,
	// 19));
	// WSClient.setData("{var_time}", "09:00:00");
	// WSClient.writeToReport(LogStatus.INFO, "Create Booking using Guest
	// Count");
	// OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// resortExtValue,
	// OWSLib.getChannelType(interfaceName),
	// OWSLib.getChannelCarier(resortOperaValue, interfaceName));
	// String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking",
	// "DS_12");
	// String updateProfileResponseXML =
	// WSClient.processSOAPMessage(updateProfileReq);
	// if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
	// "CreateBookingResponse_Result_resultStatusFlag","SUCCESS", false))
	// {
	// if(WSAssert.assertIfElementExists(updateProfileResponseXML,
	// "CreateBookingResponse_Result_Text", true))
	// {
	// WSClient.writeToReport(LogStatus.INFO,"<b>Create Booking Response gives
	// the error message-----></b>
	// "+WSClient.getElementValue(updateProfileResponseXML,
	// "CreateBookingResponse_Result_Text", XMLType.RESPONSE));
	// }
	//
	// // validating the data
	//
	// String elem=WSClient.getElementValue(updateProfileResponseXML,
	// "RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
	// String elem1=WSClient.getElementValue(updateProfileReq,
	// "RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan
	// Code</b>");
	//
	// if(WSAssert.assertEquals(elem, elem1, true)){
	// WSClient.writeToReport(LogStatus.PASS, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// }
	// else
	// WSClient.writeToReport(LogStatus.FAIL, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
	// "Profile_ProfileIDs_UniqueID",
	// profileID, false);
	// elem=WSClient.getElementValue(updateProfileResponseXML,
	// "RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
	// elem1=WSClient.getElementValue(updateProfileReq,
	// "RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type
	// Code</b>");
	//
	// if(WSAssert.assertEquals(elem, elem1, true)){
	// WSClient.writeToReport(LogStatus.PASS, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// }
	// else
	// WSClient.writeToReport(LogStatus.FAIL, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// WSClient.setData("{var_resv}",WSClient.getElementValue(updateProfileResponseXML,"HotelReservation_UniqueIDList_UniqueID",
	// XMLType.RESPONSE));
	// String query=WSClient.getQuery("QS_01");
	// LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
	// String query1=WSClient.getQuery("QS_03");
	// LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
	// WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"),bookingDets1.get("CONFIRMATION_NO")
	// ,false);
	// WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"),bookingDets1.get("RESV_NAME_ID")
	// ,false);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation
	// No</b>");
	//
	// WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
	// "HotelReservation_UniqueIDList_UniqueID",
	// bookingDets.get("CONFIRMATION_NO"), false);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation
	// No</b>");
	//
	// WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
	// "CreateBookingResponse_HotelReservation_UniqueIDList",
	// bookingDets.get("CONFIRMATION_NO")+bookingDets.get("RESV_NAME_ID"),
	// false);
	//
	// HashMap<String,String> xPath=new HashMap<String,String>();
	// xPath.put("RoomStay_GuestCounts_GuestCount_ageQualifyingCode","RoomStay_GuestCounts_GuestCount");
	// List<LinkedHashMap<String, String>>
	// reqData=WSClient.getMultipleNodeList(updateProfileReq,
	// xPath, false, XMLType.REQUEST);
	// WSClient.writeToReport(LogStatus.INFO, reqData.toString());
	// xPath.put("RoomStay_GuestCounts_GuestCount_ageQualifyingCode","RoomStay_GuestCounts_GuestCount");
	// List<LinkedHashMap<String, String>>
	// respData=WSClient.getMultipleNodeList(updateProfileResponseXML,
	// xPath, false, XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, respData.toString());
	//
	// WSAssert.assertEquals(reqData, respData, false);
	//
	// HashMap<String,String> xPath1=new HashMap<String,String>();
	// xPath1.put("RoomStay_GuestCounts_GuestCount_count","RoomStay_GuestCounts_GuestCount");
	// List<LinkedHashMap<String, String>>
	// reqData1=WSClient.getMultipleNodeList(updateProfileReq,
	// xPath1, false, XMLType.REQUEST);
	// WSClient.writeToReport(LogStatus.INFO, reqData1.toString());
	// xPath1.put("RoomStay_GuestCounts_GuestCount_count","RoomStay_GuestCounts_GuestCount");
	// List<LinkedHashMap<String, String>>
	// respData1=WSClient.getMultipleNodeList(updateProfileResponseXML,
	// xPath1, false, XMLType.RESPONSE);
	// WSClient.writeToReport(LogStatus.INFO, respData1.toString());
	//
	// WSAssert.assertEquals(reqData1, respData1, false);
	// }else
	//
	// WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
	// }
	// }

	// @Test(groups = { "minimumRegression", "CreateBooking", "OWS",
	// "Reservation" })

	public void createBooking11111() {
		try {
			resv = null;

			String testName = "createBooking_38395";
			WSClient.startTest(testName, "Verify that Booking is created with User Defined Functions",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);

			System.out.println("Profile Creation is successful 1");
			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true) == false) {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->Create Profile fails");
			} else {
				if (WSAssert.assertIfElementExists(createProfileResponseXML,
						"CreateProfileRS_ProfileIDList_UniqueID_ID", false)) {

					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
							"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
					WSClient.setData("{var_profileId}", operaProfileID);

					WSClient.setData("{var_profileSource}", interfaceName);
					WSClient.setData("{var_resort}", resortOperaValue);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_busdate}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_650}") + "T06:00:00");
					WSClient.setData("{var_busdate1}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_655}") + "T06:00:00");
					WSClient.setData("{var_time}", "09:00:00");
					fetchAvailability(rate, rt);

					WSClient.writeToReport(LogStatus.INFO, "Create Booking using User Defined Functions");
					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
							OWSLib.getChannelType(interfaceName),
							OWSLib.getChannelCarier(resortOperaValue, interfaceName));
					String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_13");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
					if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(updateProfileResponseXML,
								"CreateBookingResponse_Result_Text", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Create Booking Response gives the error message-----></b> "
											+ WSClient.getElementValue(updateProfileResponseXML,
													"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
						}

						// validating the data
						WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
						String elem = WSClient.getElementValue(updateProfileResponseXML,
								"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
						String elem1 = WSClient.getElementValue(updateProfileReq,
								"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

						if (WSAssert.assertEquals(elem, elem1, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + elem + " Actual value :" + elem1);
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + elem + " Actual value :" + elem1);
						WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
								operaProfileID, false);
						elem = WSClient.getElementValue(updateProfileResponseXML,
								"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
						elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
								XMLType.REQUEST);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

						if (WSAssert.assertEquals(elem, elem1, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + elem + " Actual value :" + elem1);
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + elem + " Actual value :" + elem1);
						WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));

						HashMap<String, String> xPath = new HashMap<String, String>();
						xPath.put("HotelReservation_UserDefinedValues_UserDefinedValue_valueName",
								"HotelReservation_UserDefinedValues_UserDefinedValue");
						List<LinkedHashMap<String, String>> reqData = WSClient.getMultipleNodeList(updateProfileReq,
								xPath, false, XMLType.REQUEST);

						xPath.put("HotelReservation_UserDefinedValues_UserDefinedValue_valueName",
								"HotelReservation_UserDefinedValues_UserDefinedValue");
						List<LinkedHashMap<String, String>> respData = WSClient
								.getMultipleNodeList(updateProfileResponseXML, xPath, false, XMLType.RESPONSE);

						WSAssert.assertEquals(reqData, respData, false);
						xPath = new HashMap<String, String>();
						xPath.put("UserDefinedValues_UserDefinedValue_CharacterValue_3",
								"HotelReservation_UserDefinedValues_UserDefinedValue");
						reqData = WSClient.getMultipleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);
						WSClient.writeToReport(LogStatus.INFO, reqData.toString());
						xPath.put("UserDefinedValues_UserDefinedValue_CharacterValue_2",
								"HotelReservation_UserDefinedValues_UserDefinedValue");
						respData = WSClient.getMultipleNodeList(updateProfileResponseXML, xPath, false,
								XMLType.RESPONSE);

						WSAssert.assertEquals(reqData, respData, false);

					} else

						WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	public void createBooking111111() {
		try {
			resv = null;

			String testName = "createBooking_383695";
			WSClient.startTest(testName, "Verify that Booking is created with a Preference", "minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);

			System.out.println("Profile Creation is successful 1");
			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true) == false) {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->Create Profile fails");
			} else {
				if (WSAssert.assertIfElementExists(createProfileResponseXML,
						"CreateProfileRS_ProfileIDList_UniqueID_ID", false)) {

					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
							"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
					WSClient.setData("{var_profileId}", operaProfileID);

					WSClient.setData("{var_profileSource}", interfaceName);
					WSClient.setData("{var_resort}", resortOperaValue);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_busdate}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_190}") + "T06:00:00");
					WSClient.setData("{var_busdate1}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_195}") + "T06:00:00");
					WSClient.setData("{var_time}", "09:00:00");
					WSClient.writeToReport(LogStatus.INFO, "Create Booking using Preferences");
					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
							OWSLib.getChannelType(interfaceName),
							OWSLib.getChannelCarier(resortOperaValue, interfaceName));

					WSClient.setData("{var_prefCode}", OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_05"));
					WSClient.setData("{var_prefGroup}", OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_05"));
					WSClient.setData("{var_prefType}", OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_05"));
					WSClient.setData("{var_prefValue}", OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_05"));
					fetchAvailability(rate, rt);

					String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_14");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
					if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
						resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
						WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
						if (WSAssert.assertIfElementExists(updateProfileResponseXML,
								"CreateBookingResponse_Result_Text", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Create Booking Response gives the error message-----></b> "
											+ WSClient.getElementValue(updateProfileResponseXML,
													"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
						}

						// validating the data

						WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
						// WSClient.writeToReport(LogStatus.INFO, "<b>Validating
						// Preference Types</b>");
						HashMap<String, String> xPath = new HashMap<String, String>();
						xPath.put("HotelReservation_Preferences_Preference_preferenceType",
								"HotelReservation_Preferences_Preference");
						List<LinkedHashMap<String, String>> reqData = WSClient.getMultipleNodeList(updateProfileReq,
								xPath, false, XMLType.REQUEST);
						// WSClient.writeToReport(LogStatus.INFO,
						// reqData.toString());
						// xPath.put("HotelReservation_Preferences_Preference_preferenceType",
						// "HotelReservation_Preferences_Preference");
						// List<LinkedHashMap<String, String>> respData =
						// WSClient
						// .getMultipleNodeList(updateProfileResponseXML, xPath,
						// false, XMLType.RESPONSE);
						// // WSClient.writeToReport(LogStatus.INFO,
						// // respData.toString());
						//
						// WSAssert.assertEquals(reqData, respData, false);
						// WSClient.writeToReport(LogStatus.INFO, "<b>Validating
						// Preference Values</b>");
						//
						// xPath = new HashMap<String, String>();
						// xPath.put("HotelReservation_Preferences_Preference_preferenceValue",
						// "HotelReservation_Preferences_Preference");
						// reqData =
						// WSClient.getMultipleNodeList(updateProfileReq, xPath,
						// false, XMLType.REQUEST);
						// // WSClient.writeToReport(LogStatus.INFO,
						// // reqData.toString());
						// xPath.put("HotelReservation_Preferences_Preference_preferenceValue",
						// "HotelReservation_Preferences_Preference");
						// respData =
						// WSClient.getMultipleNodeList(updateProfileResponseXML,
						// xPath, false,
						// XMLType.RESPONSE);
						// WSClient.writeToReport(LogStatus.INFO,
						// respData.toString());

						// WSAssert.assertEquals(reqData, respData, false);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating against Response</b>");
						WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"HotelReservation_Preferences_Preference_preferenceType",
								WSClient.getData("{var_prefType}"), false);
						WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"HotelReservation_Preferences_Preference_preferenceValue",
								WSClient.getData("{var_prefValue}"), false);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating against DB</b>");
						LinkedHashMap<String, String> dbComments = WSClient
								.getDBRow(WSClient.getQuery("OWSModifyBooking", "QS_13"));

						if (WSAssert.assertEquals(WSClient.getData("{var_prefType}"), dbComments.get("PREFERENCE_TYPE"),
								true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Preference Type - Expected: " + WSClient.getData("{var_prefType}") + ", Actual : "
											+ dbComments.get("PREFERENCE_TYPE"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									"Preference Type - Expected: " + WSClient.getData("{var_prefType}") + ", Actual : "
											+ dbComments.get("PREFERENCE_TYPE"));
						}

						if (WSAssert.assertEquals(WSClient.getData("{var_prefValue}"), dbComments.get("PREFERENCE"),
								true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Preference Value - Expected: " + WSClient.getData("{var_prefValue}")
											+ ", Actual : " + dbComments.get("PREFERENCE"));
						} else {
							WSClient.writeToReport(LogStatus.FAIL,
									"Preference Value - Expected: " + WSClient.getData("{var_prefValue}")
											+ ", Actual : " + dbComments.get("PREFERENCE"));
						}

					} else

						WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

//	Need to debug later
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	public void createBooking_3834951_Nightparameter() {
		try {
			resv = null;

			String testName = "createBooking_3834951";
			WSClient.startTest(testName,
					"Verify that booking is NOT Created for a profile when the number of nights EXCEED the maximum limit",
					"minimumRegression");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			String interfaceName = OWSLib.getChannel();
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			// WSClient.setData("{var_resvType}", "QA_4PM");
			// WSClient.setData("{var_rate}", "CKRC");
			WSClient.setData("{var_chain}", OPERALib.getChain());
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 *
				 ************************/

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				OPERALib.setOperaHeader(uname);
				WSClient.writeToReport(LogStatus.INFO, "<b>Setting the parameter : MAX_NO_OF_NIGHTS to 3</b>");
				WSClient.setData("{var_parameter}", "MAX_NO_OF_NIGHTS");
				WSClient.setData("{var_settingValue}", "3");
				queueResvParameterEnabled = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}", "2018-08-15T12:00:00");
				WSClient.setData("{var_busdate1}", "2018-08-25T12:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);
				;
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {

					if (WSAssert.assertIfElementExists(updateProfileResponseXML,
							"CreateBookingResponse_Result_OperaErrorCode", false)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}

					// validating the data
					else
						WSClient.writeToReport(LogStatus.FAIL,
								"No error is populated when the number of nights limit is exceeded");
					WSClient.setData("{var_parameter}", "MAX_NO_OF_NIGHTS");

					queueResvParameterEnabled = ChangeApplicationParameters.changeApplicationParameter("DS_02",
							"DS_01");
				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_nightparameterPositive() {
		try {
			resv = null;

			String testName = "createBooking_3836951";
			WSClient.startTest(testName,
					"Verify that booking is Created for a profile when the number of nights are LESS than the maximum limit",
					"minimumRegression");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			// WSClient.setData("{var_resvType}", "QA_4PM");
			// WSClient.setData("{var_rate}", "CKRC");
			WSClient.setData("{var_chain}", OPERALib.getChain());
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 *
				 ************************/

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				OPERALib.setOperaHeader(uname);
				WSClient.writeToReport(LogStatus.INFO, "<b>Setting the parameter : MAX_NO_OF_NIGHTS to 3</b>");
				WSClient.setData("{var_parameter}", "MAX_NO_OF_NIGHTS");
				WSClient.setData("{var_settingValue}", "3");
				queueResvParameterEnabled = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}", "2017-09-15T12:00:00");
				WSClient.setData("{var_busdate1}", "2017-09-17T12:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
							profileID, false);
					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);

					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStays_RoomStay_Guarantee_guaranteeType",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					String resv1 = WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
					WSClient.writeToReport(LogStatus.INFO, "<b> Validating the records in DB </b>");
					String query = WSClient.getQuery("QS_01");
					LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
					String query1 = WSClient.getQuery("QS_03");
					LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
					if (WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"), bookingDets1.get("CONFIRMATION_NO"),
							true))
						WSClient.writeToReport(LogStatus.PASS,
								"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
										+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));
					else
						WSClient.writeToReport(LogStatus.FAIL,
								"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
										+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));

					if (WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"), bookingDets1.get("RESV_NAME_ID"), true))
						WSClient.writeToReport(LogStatus.PASS,
								"Reservation ID -> Expected value:" + bookingDets.get("RESV_NAME_ID")
										+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));
					else
						WSClient.writeToReport(LogStatus.FAIL,
								"Reservation ID-> Expected value:" + bookingDets.get("RESV_NAME_ID") + " Actual value :"
										+ bookingDets1.get("RESV_NAME_ID"));

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

					if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));

				} else

					// validating the data
					WSClient.writeToReport(LogStatus.FAIL,
							"Create Booking fails despite not exceeding the MAX NUMBER OF NIGHTS limit");
				WSClient.setData("{var_parameter}", "MAX_NO_OF_NIGHTS");

				queueResvParameterEnabled = ChangeApplicationParameters.changeApplicationParameter("DS_02", "DS_01");
			} else

				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite Blocked");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_nightparameterEquals() {
		try {
			resv = null;

			String testName = "createBooking_3843951";
			WSClient.startTest(testName,
					"Verify that booking is Created for a profile when the number of nights EQUALS the maximum limit",
					"minimumRegression");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			// WSClient.setData("{var_resvType}", "QA_4PM");
			// WSClient.setData("{var_rate}", "CKRC");
			WSClient.setData("{var_chain}", OPERALib.getChain());
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 *
				 ************************/

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				OPERALib.setOperaHeader(uname);
				WSClient.writeToReport(LogStatus.INFO, "<b>Setting the parameter : MAX_NO_OF_NIGHTS to 3</b>");
				WSClient.setData("{var_parameter}", "MAX_NO_OF_NIGHTS");
				WSClient.setData("{var_settingValue}", "3");
				queueResvParameterEnabled = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}", "2017-08-15T12:00:00");
				WSClient.setData("{var_busdate1}", "2017-08-18T12:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
							profileID, false);
					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);

					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStays_RoomStay_Guarantee_guaranteeType",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					String resv1 = WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
					WSClient.writeToReport(LogStatus.INFO, "<b> Validating the records in DB </b>");
					String query = WSClient.getQuery("QS_01");
					LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
					String query1 = WSClient.getQuery("QS_03");
					LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
					if (WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"), bookingDets1.get("CONFIRMATION_NO"),
							true))
						WSClient.writeToReport(LogStatus.PASS,
								"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
										+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));
					else
						WSClient.writeToReport(LogStatus.FAIL,
								"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
										+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));

					if (WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"), bookingDets1.get("RESV_NAME_ID"), true))
						WSClient.writeToReport(LogStatus.PASS,
								"Reservation ID -> Expected value:" + bookingDets.get("RESV_NAME_ID")
										+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));
					else
						WSClient.writeToReport(LogStatus.FAIL,
								"Reservation ID-> Expected value:" + bookingDets.get("RESV_NAME_ID") + " Actual value :"
										+ bookingDets1.get("RESV_NAME_ID"));

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

					if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));

				} else

					// validating the data
					WSClient.writeToReport(LogStatus.FAIL,
							"Create Booking fails despite not exceeding the MAX NUMBER OF NIGHTS limit");
				WSClient.setData("{var_parameter}", "MAX_NO_OF_NIGHTS");

				queueResvParameterEnabled = ChangeApplicationParameters.changeApplicationParameter("DS_02", "DS_01");
			} else

				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite Blocked");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking", "OWS",
	// "Reservation" })

	public void createBooking_roomlimitparameter() {
		try {
			resv = null;

			String testName = "createBooking_38339512";
			WSClient.startTest(testName,
					"Verify that booking is Created for a profile when the number of boookings exceeds the maximum limit",
					"minimumRegression");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			String interfaceName = OWSLib.getChannel();
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				OPERALib.setOperaHeader(uname);
				WSClient.setData("{var_parameter}", "PER_RESERVATION_ROOM_LIMIT");
				WSClient.writeToReport(LogStatus.INFO,
						"<b>Verify if the parameter  Room Limit Per Reservation is enabled</b>");
				queueResvParameterEnabled = FetchApplicationParameters.getApplicationParameter("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Setting the parameter : Room Limit Per Reservation</b>");
				WSClient.setData("{var_settingValue}", "3");
				queueResvParameterEnabled = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}", "2017-08-15T12:00:00");
				WSClient.setData("{var_busdate1}", "2017-08-25T12:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_15");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(updateProfileResponseXML,
							"CreateBookingResponse_Result_OperaErrorCode", false)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}
				} // validating the data
				else
					WSClient.writeToReport(LogStatus.FAIL,
							"No error is populated when the number of rooms limit is exceeded");
				WSClient.setData("{var_parameter}", "PER_RESERVATION_ROOM_LIMIT");
				WSClient.setData("{var_settingValue}", "1000");
				queueResvParameterEnabled = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			} else

				WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking",
	// "OWS","Reservation"})
	//
	// public void createBooking_channelInactiveparameter() {
	// String testName = "createBooking_3839512";
	// WSClient.startTest(testName, "Verify that booking is not Created for a
	// profile when the number of boookings exceeds the maximum limit",
	// "minimumRegression");
	// String interfaceName = OWSLib.getChannel();
	// String resortOperaValue = OPERALib.getResort();
	// String resortExtValue = OWSLib.getChannelResort(resortOperaValue,
	// interfaceName);
	//
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	//
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// //****** Prerequisite : Creating a Profile with basic details*****//
	// profileID=CreateProfile.createProfile("DS_01");
	// if(!profileID.equals("error"))
	// {
	// WSClient.setData("{var_profileId}", profileID);
	//
	// /******************* Prerequisite 2:Create a Reservation
	// ************************/
	//
	// String uname = OPERALib.getUserName();
	// String pwd = OPERALib.getPassword();
	// String channel = OWSLib.getChannel();
	// String channelType = OWSLib.getChannelType(channel);
	// String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	// WSClient.setData("{var_owsresort}", resort);
	// String channelCarrier = OWSLib.getChannelCarier(resort, channel);
	// OPERALib.setOperaHeader(uname);
	// WSClient.setData("{var_parameter}", "CHANNEL_INVENTORY");
	// WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter Room
	// Limit Per Reservation is enabled</b>");
	// queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
	// WSClient.writeToReport(LogStatus.INFO, "****Setting the parameter : Room
	// Limit Per Reservation****");
	// WSClient.setData("{var_settingValue}", "N");
	// queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	// WSClient.setData("{var_busdate}", "2017-08-15T12:00:00");
	// WSClient.setData("{var_busdate1}", "2017-08-16T12:00:00");
	// WSClient.setData("{var_time}", "09:00:00");
	// OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// resortExtValue,
	// OWSLib.getChannelType(interfaceName),
	// OWSLib.getChannelCarier(resortOperaValue, interfaceName));
	// String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking",
	// "DS_15");
	// String updateProfileResponseXML =
	// WSClient.processSOAPMessage(updateProfileReq);
	// if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
	// "CreateBookingResponse_Result_resultStatusFlag","FAIL", false))
	// {
	// if(WSAssert.assertIfElementExists(updateProfileResponseXML,
	// "Result_Text_TextElement", false))
	// {
	// WSClient.writeToReport(LogStatus.INFO,"<b>Create Booking Response gives
	// the error message-----></b>
	// "+WSClient.getElementValue(updateProfileResponseXML,
	// "Result_Text_TextElement", XMLType.RESPONSE));
	// }
	//
	// // validating the data
	// else
	// WSClient.writeToReport(LogStatus.FAIL, "No error is populated when
	// channel inventory is invalidated");
	// WSClient.setData("{var_parameter}", "CHANNEL_INVENTORY");
	// WSClient.setData("{var_settingValue}", "Y");
	// queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
	// }else
	//
	//
	// WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
	// }
	// }

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_12345() {
		try {
			resv = null;

			String testName = "createBooking_383945";
			WSClient.startTest(testName, "Verify that booking is Created for a profile that is guaranteed by a company",
					"minimumRegression");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			String interfaceName = OWSLib.getChannel();
			System.out.println(interfaceName);
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_950}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_955}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_16");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
							profileID, false);
					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);

					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStays_RoomStay_Guarantee_guaranteeType",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
					String query = WSClient.getQuery("QS_01");
					LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
					String query1 = WSClient.getQuery("QS_03");
					LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
					if (WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"), bookingDets1.get("CONFIRMATION_NO"),
							true))
						WSClient.writeToReport(LogStatus.PASS,
								"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
										+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));
					else
						WSClient.writeToReport(LogStatus.FAIL,
								"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
										+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));

					if (WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"), bookingDets1.get("RESV_NAME_ID"), true))
						WSClient.writeToReport(LogStatus.PASS,
								"Reservation ID -> Expected value:" + bookingDets.get("RESV_NAME_ID")
										+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));
					else
						WSClient.writeToReport(LogStatus.FAIL,
								"Reservation ID-> Expected value:" + bookingDets.get("RESV_NAME_ID") + " Actual value :"
										+ bookingDets1.get("RESV_NAME_ID"));

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

					if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Credit Card Details</b>");

					String cc = WSClient.getElementValue(updateProfileReq,
							"GuaranteeAccepted_GuaranteeCreditCard_cardCode_2", XMLType.REQUEST);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"GuaranteesAccepted_GuaranteeAccepted_GuaranteeCreditCard_cardType_2", cc, false);

					String ch = WSClient.getElementValue(updateProfileReq,
							"GuaranteeAccepted_GuaranteeCreditCard_cardHolderName_2", XMLType.REQUEST);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"GuaranteeAccepted_GuaranteeCreditCard_cardHolderName_2", ch, false);

					String cn = "XXXXXXXXXX"
							+ WSClient
									.getElementValue(updateProfileReq,
											"GuaranteeAccepted_GuaranteeCreditCard_cardNumber_2", XMLType.REQUEST)
									.substring(11, 15);
					System.out.println(cn);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"GuaranteeAccepted_GuaranteeCreditCard_cardNumber_2", cn, false);

				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_123466() {
		try {
			resv = null;

			String testName = "createBooking_38395";
			WSClient.startTest(testName,
					"Verify that booking is not Created when stay date is not within the promotion code's stay date range ",
					"minimumRegression");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			String interfaceName = OWSLib.getChannel();
			System.out.println(interfaceName);
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String prom = OperaPropConfig.getDataSetForCode("PromotionCode", "DS_03");
			WSClient.setData("{var_prom}", prom);
			WSClient.setData("{var_promoCode}", prom);
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				String startDate = WSClient.getDBRow(WSClient.getQuery("OWSModifyBooking", "QS_33"))
						.get("STAY_ENDDATE");
				WSClient.setData("{var_busdate}", startDate.substring(0, startDate.indexOf(' ')));
				WSClient.setData("{var_busdate1}", startDate.substring(0, startDate.indexOf(' ')));
				// WSClient.setData("{var_busdate}", "2019-12-30T06:00:00");
				// WSClient.setData("{var_busdate1}", "2019-12-30T12:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				// WSClient.setData("{var_rate}", "SUITE12");
				// WSClient.setData("{var_roomType}", "CH_SUK5");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_17");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true))

					WSClient.writeToReport(LogStatus.FAIL,
							"No error is populated when prior date is populated for stay details");

				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS,
							"An error is populated when prior date is populated for stay details");

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.PASS, "Error populated is ->" + elem);

				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_12346() {
		try {
			resv = null;

			String testName = "createBooking_383955";
			WSClient.startTest(testName, "Verify that booking is Created with a promotion code", "minimumRegression");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			String interfaceName = OWSLib.getChannel();
			System.out.println(interfaceName);
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String prom = OperaPropConfig.getDataSetForCode("PromotionCode", "DS_03");
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			// String prom = "QA_HOL4";
			WSClient.setData("{var_prom}", prom);

			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_130}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_135}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_17");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
							profileID, false);
					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
							XMLType.REQUEST);

					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Promotion Code</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>Response Validation :</b>");

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_promotionCode", prom, false);
					WSClient.writeToReport(LogStatus.INFO, "<b>DB Validation :</b>");

					String query1 = WSClient.getQuery("QS_06");
					LinkedHashMap<String, String> promoDets = WSClient.getDBRow(query1);

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_promotionCode", promoDets.get("PROMOTIONS"), false);

				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking",
	// "OWS","Reservation"})
	//
	// public void createBooking_12347() {
	// String testName = "createBooking_38395";
	// WSClient.startTest(testName, "Verify that booking is Created with a
	// Block", "minimumRegression");
	// String interfaceName = OWSLib.getChannel();
	// System.out.println(interfaceName);
	// String resortOperaValue = OPERALib.getResort();
	// String resortExtValue = OWSLib.getChannelResort(resortOperaValue,
	// interfaceName);
	//
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	// String prom="QA_HOL4";
	// WSClient.setData("{var_prom}",prom );
	//
	// System.out.println(resortExtValue);
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// //****** Prerequisite : Creating a Profile with basic details*****//
	// profileID=CreateProfile.createProfile("DS_01");
	// if(!profileID.equals("error"))
	// {
	// WSClient.setData("{var_profileId}", profileID);
	// /******************* Prerequisite 2:Create a Reservation
	// ************************/
	//
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	// WSClient.setData("{var_busdate}",
	// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_150}").substring(0,
	// 19));
	// WSClient.setData("{var_busdate1}",
	// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_155}").substring(0,
	// 19));
	// WSClient.setData("{var_time}", "09:00:00");
	// String rmd = "1111";
	// String Rmd = "1111";
	// String BlockName = "Test_Block" + Rmd ;
	// WSClient.setData("{VAR_BlockName}", BlockName);
	// String BlockCode = "1611TESTBLOCK" + Rmd;
	// WSClient.setData("{VAR_BlockCode}", BlockCode);
	// String createBlockReq = WSClient.createSOAPMessage("CreateBlock",
	// "DS_01");
	// String createBlockResp = WSClient.processSOAPMessage(createBlockReq);
	// WSAssert.assertIfElementExists(createBlockResp,"CreateBlockRS_Success",
	// false);
	//
	// String BlockID =
	// WSClient.getElementValue(createBlockResp,"Block_BlockIDList_UniqueID_ID",XMLType.RESPONSE);
	//// setData("{VAR_BlockID}", BlockID);
	//// if(assertIfElementExists("CreateBlockRS_Success", false))
	//// ExtentReport.log(LogStatus.PASS, "<span style='color:green'><b>Block
	// Created Successfully</b></span>");
	//// else
	//// ExtentReport.log(LogStatus.FAIL, "<span style='color:red'><b>Exception
	// occured While Creating Block</b></span>");
	//
	// OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// resortExtValue,
	// OWSLib.getChannelType(interfaceName),
	// OWSLib.getChannelCarier(resortOperaValue, interfaceName));
	// String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking",
	// "DS_17");
	// String updateProfileResponseXML =
	// WSClient.processSOAPMessage(updateProfileReq);
	// if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
	// "CreateBookingResponse_Result_resultStatusFlag","SUCCESS", false))
	// {
	// if(WSAssert.assertIfElementExists(updateProfileResponseXML,
	// "CreateBookingResponse_Result_Text", true))
	// {
	// WSClient.writeToReport(LogStatus.INFO,"<b>Create Booking Response gives
	// the error message-----></b>
	// "+WSClient.getElementValue(updateProfileResponseXML,
	// "CreateBookingResponse_Result_Text", XMLType.RESPONSE));
	// }
	//
	//// validating the data
	//
	// String elem=WSClient.getElementValue(updateProfileResponseXML,
	// "RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
	// String elem1=WSClient.getElementValue(updateProfileReq,
	// "RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan
	// Code</b>");
	//
	// if(WSAssert.assertEquals(elem, elem1, true)){
	// WSClient.writeToReport(LogStatus.PASS, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// }
	// else
	// WSClient.writeToReport(LogStatus.FAIL, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
	// "Profile_ProfileIDs_UniqueID",
	// profileID, false);
	// elem=WSClient.getElementValue(updateProfileResponseXML,
	// "RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
	// elem1=WSClient.getElementValue(updateProfileReq,
	// "RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type
	// Code</b>");
	//
	// if(WSAssert.assertEquals(elem, elem1, true)){
	// WSClient.writeToReport(LogStatus.PASS, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// }
	// else
	// WSClient.writeToReport(LogStatus.FAIL, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	//
	// elem=WSClient.getElementValue(updateProfileResponseXML,
	// "RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
	// elem1=WSClient.getElementValue(updateProfileReq,
	// "RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type
	// Code</b>");
	//
	// if(WSAssert.assertEquals(elem, elem1, true)){
	// WSClient.writeToReport(LogStatus.PASS, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// }
	// else
	// WSClient.writeToReport(LogStatus.FAIL, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// WSClient.setData("{var_resv}",WSClient.getElementValue(updateProfileResponseXML,"HotelReservation_UniqueIDList_UniqueID",
	// XMLType.RESPONSE));
	//
	//
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Promotion
	// Code</b>");
	//
	//
	// WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
	// "RoomStay_RatePlans_RatePlan_promotionCode",
	// prom, false);
	// String query1=WSClient.getQuery("QS_06");
	// LinkedHashMap<String, String> promoDets = WSClient.getDBRow(query1);
	// WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
	// "RoomStay_RatePlans_RatePlan_promotionCode",
	// promoDets.get("PROMOTIONS"), false);
	//
	//
	// }else
	//
	// WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
	// }
	// }

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_3839566_BookingBeforeTheBusinessDate() {
		try {
			String testName = "createBooking_3839566";
			WSClient.startTest(testName, "Verify that booking is not Created before the business date",
					"minimumRegression");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String interfaceName = OWSLib.getChannel();
			System.out.println(interfaceName);
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			// WSClient.setData("{var_resvType}", "QA_4PM");
			// WSClient.setData("{var_rate}", "CKRC");
			// WSClient.setData("{var_roomType}", "4RT");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_MINUS_2}").substring(0, 19));
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_MINUS_2}").substring(0, 19));
				WSClient.setData("{var_time}", "09:00:00");
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true))

					WSClient.writeToReport(LogStatus.FAIL,
							"No error is populated when prior date is populated for stay details");

				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
					WSClient.writeToReport(LogStatus.PASS,
							"An error is populated when prior date is populated for stay details");

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.PASS, "Error populated is ->" + elem);
				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);

		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking", "OWS",
	// "Reservation" })
	//
	// public void createBooking_122208() {
	//
	// String testName = "createBooking_22208";
	// WSClient.startTest(testName, "Verify adding membership details are
	// effected successfully using Create Booking",
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
	// String channel = OWSLib.getChannel();
	// WSClient.setData("{var_channel}", channel);
	// String channelCarrier = OWSLib.getChannelCarier(resort, channel);
	// OPERALib.setOperaHeader(uname);
	//
	// if (OperaPropConfig.getPropertyConfigResults(
	// new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode",
	// "ReservationType" })) {
	//
	// /*************
	// * Prerequisite : Room type, Rate Plan Code, Source Code, Market
	// * Code
	// *********************************/
	// WSClient.setData("{VAR_RATEPLANCODE}", "CKRRC");//
	// OperaPropConfig.getDataSetForCode("RateCode",
	// // "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}", "OWSRT");//
	// OperaPropConfig.getDataSetForCode("RoomType",
	// // "DS_02"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_ReservationType}", "4PM");//
	// OperaPropConfig.getDataSetForCode("ReservationType",
	// // "DS_01"));
	//
	// /************
	// * Prerequisite 1: Create profile
	// *********************************/
	//
	// profileID = CreateProfile.createProfile("DS_00");
	//
	// WSClient.setData("{var_profileId}", profileID);
	//
	// OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	// WSClient.setData("{var_membershipType}",
	// OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
	// String lastname = "MEMBERSHIP";
	// WSClient.setData("{var_membershipName}", lastname);
	// //
	// WSClient.setData("{var_membershipLevel}",OperaPropConfig.getDataSetForCode("MembershipLevel",
	// // "DS_02"));
	// WSClient.setData("{var_membershipLevel}", "Platinum");
	// WSClient.setData("{var_busdate}",
	// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_150}").substring(0,
	// 19));
	// WSClient.setData("{var_busdate1}",
	// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_155}").substring(0,
	// 19));
	// WSClient.setData("{var_time}", "09:00:00");
	//
	// /*************** OWS Modify Booking Operation ********************/
	// String modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking",
	// "DS_18");
	// String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);
	// WSClient.setData("{var_resv}", WSClient.getElementValue(modifyBookingRes,
	// "HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
	// WSClient.setData("{var_resvId}",
	// WSClient.getElementValueByAttribute(modifyBookingReq,
	// "HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
	// if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "CreateBookingResponse_Result_resultStatusFlag",
	// "SUCCESS", true)) {
	// String memnum = WSClient.getElementValue(modifyBookingReq,
	// "Memberships_NameMembership_membershipNumber", XMLType.REQUEST);
	// WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "Memberships_NameMembership_membershipNumber",
	// memnum, false);
	//
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating with
	// Database</b>");
	// String query = WSClient.getQuery("QS_07");
	// LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
	// WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "Memberships_NameMembership_expirationDate",
	// bookingDets.get("EXP"), false);
	// String last = WSClient.getElementValue(modifyBookingRes,
	// "Customer_PersonName_lastName",
	// XMLType.RESPONSE);
	// String first = WSClient.getElementValue(modifyBookingRes,
	// "Customer_PersonName_firstName",
	// XMLType.RESPONSE);
	// String name = first + " " + last;
	//
	// WSAssert.assertEquals(name, bookingDets.get("NAME"), false);
	// String type = WSClient.getElementValue(modifyBookingRes,
	// "Memberships_NameMembership_membershipType",
	// XMLType.RESPONSE);
	// String num = WSClient.getElementValue(modifyBookingRes,
	// "Memberships_NameMembership_membershipNumber",
	// XMLType.RESPONSE);
	// String level = WSClient.getElementValue(modifyBookingRes,
	// "Memberships_NameMembership_membershipLevel",
	// XMLType.RESPONSE);
	//
	// WSAssert.assertEquals(type, bookingDets.get("TYPE"), false);
	// WSAssert.assertEquals(num, bookingDets.get("NUM"), false);
	// WSAssert.assertEquals(level, bookingDets.get("LEVEL"), false);
	//
	// }
	// }
	// }

	// @Test(groups = { "minimumRegression", "CreateBooking", "OWS",
	// "Reservation" })
	//
	// public void createBooking_22208() {
	// try{
	// String testName = "createBooking_22208";
	// WSClient.startTest(testName, "Verify Room Features are attached to the
	// profile successfully",
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
	// String channelCarrier = OWSLib.getChannelCarier(resort, channel);
	// OPERALib.setOperaHeader(uname);
	//
	// if (OperaPropConfig.getPropertyConfigResults(
	// new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode",
	// "ReservationType" })) {
	//
	// /*************
	// * Prerequisite : Room type, Rate Plan Code, Source Code, Market
	// * Code
	// *********************************/
	// WSClient.setData("{VAR_RATEPLANCODE}", "CKRRC");//
	// OperaPropConfig.getDataSetForCode("RateCode",
	// // "DS_01"));
	// WSClient.setData("{VAR_ROOMTYPE}", "OWSRT");//
	// OperaPropConfig.getDataSetForCode("RoomType",
	// // "DS_02"));
	// WSClient.setData("{var_sourceCode}",
	// OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	// WSClient.setData("{VAR_MARKETCODE}",
	// OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	// WSClient.setData("{var_ReservationType}", "4PM");//
	// OperaPropConfig.getDataSetForCode("ReservationType",
	// // "DS_01"));
	// WSClient.setData("{var_par}", "N");
	// WSClient.setData("{var_parname}",
	// "ATTACH_PROFILE_ROOM_FEATURES_TO_RESERVATION");
	// WSClient.setData("{var_type}", "Boolean");
	// WSClient.setData("{var_param}",
	// "ATTACH_PROFILE_ROOM_FEATURES_TO_RESERVATION");
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// // ****** Prerequisite : Creating a Profile with basic
	// // details*****//
	// String query1 = WSClient.getQuery("ChangeChannelParameters", "QS_02");
	// LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b> Validating the value of ATTACH_PROFILE_ROOM_FEATURES_TO_RESERVATION
	// </b>");
	// if (WSAssert.assertEquals("Y", bookingDets1.get("PARAMETER_VALUE"),
	// false)) {
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>ATTACH_PROFILE_ROOM_FEATURES_TO_RESERVATION IS ENABLED</b>");
	// } else {
	// String updateProfileReq2 =
	// WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
	// String updateProfileResponseXML2 =
	// WSClient.processSOAPMessage(updateProfileReq2);
	// if (WSAssert.assertIfElementExists(updateProfileResponseXML2,
	// "ChangeChannelParametersRS_Success",
	// false)) {
	// String query2 = WSClient.getQuery("ChangeChannelParameters", "QS_02");
	// LinkedHashMap<String, String> bookingDets2 = WSClient.getDBRow(query2);
	// if (WSAssert.assertEquals("Y", bookingDets2.get("PARAMETER_VALUE"),
	// false)) {
	// WSClient.writeToReport(LogStatus.INFO,
	// "<b>ATTACH_PROFILE_ROOM_FEATURES_TO_RESERVATION IS ENABLED</b>");
	// }
	// }
	// }
	// /************
	// * Prerequisite 1: Create profile
	// *********************************/
	//
	// profileID = CreateProfile.createProfile("DS_00");
	//
	// WSClient.setData("{var_profileId}", profileID);
	//
	// OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	//
	// WSClient.setData("{var_busdate}",
	// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_150}").substring(0,
	// 19));
	// WSClient.setData("{var_busdate1}",
	// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_155}").substring(0,
	// 19));
	// WSClient.setData("{var_time}", "09:00:00");
	//
	// /*************** OWS Modify Booking Operation ********************/
	// String modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking",
	// "DS_20");
	// String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);
	// WSClient.setData("{var_resvId}",
	// WSClient.getElementValueByAttribute(modifyBookingRes,
	// "HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
	// WSClient.setData("{var_resv}", WSClient.getElementValue(modifyBookingRes,
	// "HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
	//
	// if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
	// "CreateBookingResponse_Result_resultStatusFlag",
	// "SUCCESS", true)) {
	//
	// }
	// CancelReservation.cancelReservation("DS_02");
	//
	// }
	// }
	// catch(Exception e)
	// {
	// WSClient.writeToReport(LogStatus.ERROR, "Exception Occured " + e);
	// }
	// }

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_12333() {
		try {
			resv = null;

			String testName = "createBooking_3833395";
			WSClient.startTest(testName,
					"Verify that booking is not Created for a profile with an inactive guarantee Type",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			WSClient.setData("{var_resvType}", OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_03"));
			WSClient.setData("{var_reservationType}",
					OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_03"));

			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_950}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_955}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);
				WSClient.setData("{var_resvType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_03"));
				WSClient.setData("{var_reservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_03"));
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_21");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(updateProfileResponseXML,
							"CreateBookingResponse_Result_OperaErrorCode", false)) {
						WSClient.writeToReport(LogStatus.PASS,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}

				} else
					WSClient.writeToReport(LogStatus.FAIL,
							"Create Booking Response does not give an error message when guarantee type is inactive");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_sellLimitchk() {
		try {
			resv = null;

			String testName = "createBooking_383495";
			WSClient.startTest(testName,
					"Verify that booking is NOT created when the number of units EXCEED the sell limit ( for ROOM TYPE) for the date range",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			fetchAvailability(rate, rt);
			WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the CHANNEL INVENTORY</b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Setting the Channel Room Type limit : 1 </b>");

				String updateProfileReq1 = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_01");
				String updateProfileResponseXML1 = WSClient.processSOAPMessage(updateProfileReq1);
				if (WSAssert.assertIfElementExists(updateProfileResponseXML1,
						"SetChannelSellLimitsByDateRangeRS_Success", true)) {
					String updateProfileReq4 = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_01");
					String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML4, "FetchChannelSellLimitsRS_Success",
							true)) {
						WSClient.setData("{var_id}", WSClient.getElementValue(updateProfileResponseXML4,
								"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
					}

					profileID = CreateProfile.createProfile("DS_00");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);

						/*******************
						 * Prerequisite 2:Create a Reservation
						 ************************/

						WSClient.setData("{var_profileSource}", interfaceName);
						WSClient.setData("{var_resort}", resortOperaValue);
						WSClient.setData("{var_extResort}", resortExtValue);

						WSClient.setData("{var_time}", "09:00:00");

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));
						WSClient.writeToReport(LogStatus.INFO, "<b>Booking with 2 units </b>");

						String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_23");
						String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);
						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
								"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
							if (WSAssert.assertIfElementExists(updateProfileResponseXML3,
									"CreateBookingResponse_Result_OperaErrorCode", false)) {
								WSClient.writeToReport(LogStatus.PASS,
										"<b>Create Booking Response gives the error message-----></b> "
												+ WSClient.getElementValue(updateProfileResponseXML3,
														"CreateBookingResponse_Result_OperaErrorCode",
														XMLType.RESPONSE));
							}

						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Create Booking Response does not give an error message when sell limit is set");
						WSClient.setData("{var_resort}", resortOperaValue);

						String updateProfileReq5 = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
						String updateProfileResponseXML5 = WSClient.processSOAPMessage(updateProfileReq5);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML5,
								"RemoveChannelSellLimitsRS_Success", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b> Removed the limits set on the channel set limits </b>");
						}
					}
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite --> Set Channel Sell Limit blocked");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_sellLimitchkOverrideY() {
		try {
			resv = null;

			String testName = "createBooking_3834495";
			WSClient.startTest(testName,
					"Verify that booking is created when the number of rooms EXCEED the sell limit(for ROOM TYPE) for the date range when OVERRIDE_YN is set to Y",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			// WSClient.setData("{var_busdate11}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_166}"));
			// WSClient.setData("{var_busdate12}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_167}"));
			fetchAvailability(rate, rt);
			WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the CHANNEL INVENTORY</b>");

			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {
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

					WSClient.writeToReport(LogStatus.INFO, "<b>Setting the Channel Room Type limit to 1</b>");

					String updateProfileReq1 = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_01");
					String updateProfileResponseXML1 = WSClient.processSOAPMessage(updateProfileReq1);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML1,
							"SetChannelSellLimitsByDateRangeRS_Success", true)) {
						String updateProfileReq4 = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_01");
						String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML4,
								"FetchChannelSellLimitsRS_Success", true)) {
							WSClient.setData("{var_id}", WSClient.getElementValue(updateProfileResponseXML4,
									"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
						}

						profileID = CreateProfile.createProfile("DS_00");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);

							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							WSClient.setData("{var_profileSource}", interfaceName);
							WSClient.setData("{var_resort}", resortOperaValue);
							WSClient.setData("{var_extResort}", resortExtValue);

							WSClient.setData("{var_time}", "09:00:00");

							OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
									OWSLib.getChannelType(interfaceName),
									OWSLib.getChannelCarier(resortOperaValue, interfaceName));
							WSClient.writeToReport(LogStatus.INFO, "<b>Booking with 2 units</b>");

							String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_23");
							String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
							if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								if (WSAssert.assertIfElementExists(updateProfileResponseXML,
										"CreateBookingResponse_Result_Text", true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Create Booking Response gives the error message-----></b> "
													+ WSClient.getElementValue(updateProfileResponseXML,
															"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
								}

								// validating the data

								String elem = WSClient.getElementValue(updateProfileResponseXML,
										"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
								String elem1 = WSClient.getElementValue(updateProfileReq,
										"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
								resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}",
										WSClient.getElementValueByAttribute(updateProfileResponseXML,
												"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);
								WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
										"Profile_ProfileIDs_UniqueID", profileID, false);
								elem = WSClient.getElementValue(updateProfileResponseXML,
										"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
								elem1 = WSClient.getElementValue(updateProfileReq,
										"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);

							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Create Booking Response does not give an error message when sell limit is set");
							String updateProfileReq5 = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
							String updateProfileResponseXML5 = WSClient.processSOAPMessage(updateProfileReq5);
							if (WSAssert.assertIfElementExists(updateProfileResponseXML5,
									"RemoveChannelSellLimitsRS_Success", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> Removed the limits set on the channel set limits </b>");
							}
							WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
							WSClient.setData("{var_param}", "OVERRIDE_YN");
							WSClient.setData("{var_type}", "Boolean");
							paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
							WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
							if (!paramValue.equals("N")) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
								paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01",
										"OVERRIDE_YN");
								WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
							}

							if (paramValue.equals("N")) {
								WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN is unset</b>");

							}
						}
					} else
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite --> Set Channel Sell Limit blocked");

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured :" + e);
		} finally {
			if (resv != null)
				try {
					CancelReservation.cancelReservation("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}

	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_sellLimitchkOverrideYLess() {
		try {
			resv = null;

			String testName = "createBooking_3839555";
			WSClient.startTest(testName,
					"Verify that booking is created when the number of rooms are LESSER than the sell limit(for ROOM TYPE) for the date range when OVERRIDE_YN is set to Y",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			fetchAvailability(rate, rt);
			WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_startDate}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_endDate}", WSClient.getData("{var_busdate1}").substring(0, 10));

			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the CHANNEL INVENTORY</b>");

			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
				WSClient.setData("{var_par}", "Y");
				WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
				WSClient.setData("{var_param}", "OVERRIDE_YN");
				WSClient.setData("{var_type}", "Boolean");
				WSClient.setData("{var_roomLimit}", "3");

				String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				if (!paramValue.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter OVERRIDE_YN</b>");
					paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
					WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
				}

				if (paramValue.equals("Y")) {

					WSClient.writeToReport(LogStatus.INFO, "<b>Setting the Channel Room Type limit to 3</b>");

					String updateProfileReq1 = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_03");
					String updateProfileResponseXML1 = WSClient.processSOAPMessage(updateProfileReq1);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML1,
							"SetChannelSellLimitsByDateRangeRS_Success", true)) {
						String updateProfileReq4 = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_01");
						String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML4,
								"FetchChannelSellLimitsRS_Success", true)) {
							WSClient.setData("{var_id}", WSClient.getElementValue(updateProfileResponseXML4,
									"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
						}

						profileID = CreateProfile.createProfile("DS_00");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);

							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							WSClient.setData("{var_profileSource}", interfaceName);
							WSClient.setData("{var_resort}", resortOperaValue);
							WSClient.setData("{var_extResort}", resortExtValue);

							WSClient.setData("{var_time}", "09:00:00");

							OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
									OWSLib.getChannelType(interfaceName),
									OWSLib.getChannelCarier(resortOperaValue, interfaceName));
							WSClient.writeToReport(LogStatus.INFO, "<b>Booking with 2 units</b>");

							String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_23");
							String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
							if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								if (WSAssert.assertIfElementExists(updateProfileResponseXML,
										"CreateBookingResponse_Result_Text", true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Create Booking Response gives the error message-----></b> "
													+ WSClient.getElementValue(updateProfileResponseXML,
															"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
								}

								// validating the data

								String elem = WSClient.getElementValue(updateProfileResponseXML,
										"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
								String elem1 = WSClient.getElementValue(updateProfileReq,
										"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
								resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}",
										WSClient.getElementValueByAttribute(updateProfileResponseXML,
												"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);
								WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
										"Profile_ProfileIDs_UniqueID", profileID, false);
								elem = WSClient.getElementValue(updateProfileResponseXML,
										"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
								elem1 = WSClient.getElementValue(updateProfileReq,
										"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);

							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Create Booking Response does not give an error message when sell limit is set");
							String updateProfileReq5 = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
							String updateProfileResponseXML5 = WSClient.processSOAPMessage(updateProfileReq5);
							if (WSAssert.assertIfElementExists(updateProfileResponseXML5,
									"RemoveChannelSellLimitsRS_Success", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> Removed the limits set on the channel set limits </b>");
							}
							WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
							WSClient.setData("{var_param}", "OVERRIDE_YN");
							WSClient.setData("{var_type}", "Boolean");
							paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
							WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
							if (!paramValue.equals("N")) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
								paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01",
										"OVERRIDE_YN");
								WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
							}

							if (paramValue.equals("N")) {
								WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN is unset</b>");

							}
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured :" + e);
		} finally {
			if (resv != null)
				try {
					CancelReservation.cancelReservation("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}

	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_21234() {
		try {
			resv = null;

			String testName = "createBooking_3833395";
			WSClient.startTest(testName,
					"Verify that booking is created when number of units EXCEEDS the sell limit(for room type) when CHANNEL INVENTORY is set to N",
					"minimumRegression");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			// WSClient.setData("{var_busdate11}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_186}"));
			// WSClient.setData("{var_busdate12}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_187}"));
			fetchAvailability(rate, rt);
			WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL INVENTORY is set to Y</b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>SELL LIMIT is set to 1</b>");

				String updateProfileReq1 = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_01");
				String updateProfileResponseXML1 = WSClient.processSOAPMessage(updateProfileReq1);
				if (WSAssert.assertIfElementExists(updateProfileResponseXML1,
						"SetChannelSellLimitsByDateRangeRS_Success", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Limits are fetched</b>");

					String updateProfileReq4 = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_01");
					String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML4, "FetchChannelSellLimitsRS_Success",
							true)) {
						WSClient.setData("{var_id}", WSClient.getElementValue(updateProfileResponseXML4,
								"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
					}
					WSClient.setData("{var_par}", "N");
					WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
					WSClient.setData("{var_type}", "Boolean");
					System.out.println(resortExtValue);
					OPERALib.setOperaHeader(OPERALib.getUserName());
					// ****** Prerequisite : Creating a Profile with basic
					// details*****//
					WSClient.writeToReport(LogStatus.INFO, "<b>CHANNEL INVENTORY is set to N</b>");

					String updateProfileReq6 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
					String updateProfileResponseXML6 = WSClient.processSOAPMessage(updateProfileReq6);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML6, "ChangeChannelParametersRS_Success",
							true)) {

						profileID = CreateProfile.createProfile("DS_00");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);

							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							WSClient.setData("{var_profileSource}", interfaceName);
							WSClient.setData("{var_resort}", resortOperaValue);
							WSClient.setData("{var_extResort}", resortExtValue);

							WSClient.setData("{var_time}", "09:00:00");

							OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
									OWSLib.getChannelType(interfaceName),
									OWSLib.getChannelCarier(resortOperaValue, interfaceName));
							WSClient.writeToReport(LogStatus.INFO, "<b>Booking is created with 2 units</b>");

							String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_23");
							String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);
							WSClient.setData("{var_resvId}",
									WSClient.getElementValueByAttribute(updateProfileResponseXML3,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
							if (!WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								if (WSAssert.assertIfElementExists(updateProfileResponseXML3,
										"CreateBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b>Create Booking Response gives the error message-----></b> "
													+ WSClient.getElementValue(updateProfileResponseXML3,
															"CreateBookingResponse_Result_OperaErrorCode",
															XMLType.RESPONSE));
								}

							} else {
								WSClient.writeToReport(LogStatus.PASS,
										"Create Booking Response does not give an error message as Channel Inventory is N");
								WSClient.setData("{var_resvId}",
										WSClient.getElementValueByAttribute(updateProfileResponseXML3,
												"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
								resv = WSClient.getElementValueByAttribute(updateProfileResponseXML3,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							}

							String updateProfileReq5 = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
							String updateProfileResponseXML5 = WSClient.processSOAPMessage(updateProfileReq5);
							if (WSAssert.assertIfElementExists(updateProfileResponseXML5,
									"RemoveChannelSellLimitsRS_Success", false)) {
								WSClient.writeToReport(LogStatus.INFO,

										"<b> Removed the limits set on the channel set limits </b>");
							}
						}
					}
				} else
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite --> Set Channel Sell Limit blocked");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_1134() {
		try {
			resv = null;
			String channel = OWSLib.getChannel();
			WSClient.setData("{var_channel}", channel);
			String testName = "createBooking_3839115";
			WSClient.startTest(testName,
					"Verify that booking is created when number of units is LESSER than the sell limit( for ROOM TYPE) for the date range",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			// WSClient.setData("{var_busdate11}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_298}"));
			// WSClient.setData("{var_busdate12}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_299}"));
			fetchAvailability(rate, rt);
			WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			// WSClient.setData("{var_resvType}", "QA_4PM");
			// WSClient.setData("{var_rate}", "CKRC");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Channel Inventory is set</b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {
				WSClient.writeToReport(LogStatus.INFO,
						"<b>Channel Sell Limit for the Room Type : " + rt + " is set to 2</b>");

				String updateProfileReq1 = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_02");
				String updateProfileResponseXML1 = WSClient.processSOAPMessage(updateProfileReq1);
				if (WSAssert.assertIfElementExists(updateProfileResponseXML1,
						"SetChannelSellLimitsByDateRangeRS_Success", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Channel Sell Limit for the room type is fetched</b>");

					String updateProfileReq4 = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_01");
					String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML4, "FetchChannelSellLimitsRS_Success",
							true)) {
						WSClient.setData("{var_id}", WSClient.getElementValue(updateProfileResponseXML4,
								"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
					}
					profileID = CreateProfile.createProfile("DS_00");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);

						/*******************
						 * Prerequisite 2:Create a Reservation
						 ************************/

						WSClient.setData("{var_profileSource}", interfaceName);
						WSClient.setData("{var_resort}", resortOperaValue);
						WSClient.setData("{var_extResort}", resortExtValue);

						WSClient.setData("{var_time}", "09:00:00");

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));
						WSClient.writeToReport(LogStatus.INFO, "<b>Booking is created with 1 unit</b>");

						String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);
						WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML3,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
								"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							WSClient.setData("{var_resvId}",
									WSClient.getElementValueByAttribute(updateProfileResponseXML3,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
							resv = WSClient.getElementValueByAttribute(updateProfileResponseXML3,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							if (WSAssert.assertIfElementExists(updateProfileResponseXML3,
									"CreateBookingResponse_Result_Text", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Create Booking Response gives the error message-----></b> "
												+ WSClient.getElementValue(updateProfileResponseXML3,
														"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
							}

							// validating the data

							String elem = WSClient.getElementValue(updateProfileResponseXML3,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
							String elem1 = WSClient.getElementValue(updateProfileReq3,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
									"Profile_ProfileIDs_UniqueID", profileID, false);
							elem = WSClient.getElementValue(updateProfileResponseXML3,
									"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq3,
									"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);

							elem = WSClient.getElementValue(updateProfileResponseXML3,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq3,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML3,
									"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
							String query = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
							String query1 = WSClient.getQuery("QS_03");
							LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
							if (WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"),
									bookingDets1.get("CONFIRMATION_NO"), true))
								WSClient.writeToReport(LogStatus.PASS,
										"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
												+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));
							else
								WSClient.writeToReport(LogStatus.FAIL,
										"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
												+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));

							if (WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"), bookingDets1.get("RESV_NAME_ID"),
									true))
								WSClient.writeToReport(LogStatus.PASS,
										"Reservation ID -> Expected value:" + bookingDets.get("RESV_NAME_ID")
												+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));
							else
								WSClient.writeToReport(LogStatus.FAIL,
										"Reservation ID-> Expected value:" + bookingDets.get("RESV_NAME_ID")
												+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));

							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");

							WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
									"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"),
									false);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

							if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
						} else

							WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
						String updateProfileReq5 = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
						String updateProfileResponseXML5 = WSClient.processSOAPMessage(updateProfileReq5);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML5,
								"RemoveChannelSellLimitsRS_Success", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b> Removed the limits set on the channel set limits </b>");
						}

					}
				} else
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite --> Set Channel Sell Limit blocked");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_12134() {
		try {
			resv = null;
			String channel = OWSLib.getChannel();
			WSClient.setData("{var_channel}", channel);
			String testName = "createBooking_6638395";
			WSClient.startTest(testName,
					"Verify that booking is created when number of units EQUALS the sell limit(for ROOM TYPE) for the date range",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			fetchAvailability(rate, rt);
			WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			// WSClient.setData("{var_resvType}", "QA_4PM");
			// WSClient.setData("{var_rate}", "CKRC");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Enabling CHANNEL INVENTORY</b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Setting the Channel Roomtype Sell Limit to 1</b>");

				String updateProfileReq1 = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_01");
				String updateProfileResponseXML1 = WSClient.processSOAPMessage(updateProfileReq1);
				if (WSAssert.assertIfElementExists(updateProfileResponseXML1,
						"SetChannelSellLimitsByDateRangeRS_Success", true)) {
					String updateProfileReq4 = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_01");
					String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML4, "FetchChannelSellLimitsRS_Success",
							true)) {
						WSClient.setData("{var_id}", WSClient.getElementValue(updateProfileResponseXML4,
								"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
					}
					profileID = CreateProfile.createProfile("DS_00");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);

						/*******************
						 * Prerequisite 2:Create a Reservation
						 ************************/

						WSClient.setData("{var_profileSource}", interfaceName);
						WSClient.setData("{var_resort}", resortOperaValue);
						WSClient.setData("{var_extResort}", resortExtValue);

						WSClient.setData("{var_time}", "09:00:00");

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));
						WSClient.writeToReport(LogStatus.INFO, "<b>Booking with 1 unit</b>");

						String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);
						WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML3,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
								"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							WSClient.setData("{var_resvId}",
									WSClient.getElementValueByAttribute(updateProfileResponseXML3,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
							resv = WSClient.getElementValueByAttribute(updateProfileResponseXML3,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							if (WSAssert.assertIfElementExists(updateProfileResponseXML3,
									"CreateBookingResponse_Result_Text", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Create Booking Response gives the error message-----></b> "
												+ WSClient.getElementValue(updateProfileResponseXML3,
														"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
							}

							// validating the data

							String elem = WSClient.getElementValue(updateProfileResponseXML3,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
							String elem1 = WSClient.getElementValue(updateProfileReq3,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
									"Profile_ProfileIDs_UniqueID", profileID, false);
							elem = WSClient.getElementValue(updateProfileResponseXML3,
									"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq3,
									"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);

							elem = WSClient.getElementValue(updateProfileResponseXML3,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq3,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML3,
									"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
							String query = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
							String query1 = WSClient.getQuery("QS_03");
							LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
							if (WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"),
									bookingDets1.get("CONFIRMATION_NO"), true))
								WSClient.writeToReport(LogStatus.PASS,
										"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
												+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));
							else
								WSClient.writeToReport(LogStatus.FAIL,
										"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
												+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));

							if (WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"), bookingDets1.get("RESV_NAME_ID"),
									true))
								WSClient.writeToReport(LogStatus.PASS,
										"Reservation ID -> Expected value:" + bookingDets.get("RESV_NAME_ID")
												+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));
							else
								WSClient.writeToReport(LogStatus.FAIL,
										"Reservation ID-> Expected value:" + bookingDets.get("RESV_NAME_ID")
												+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));

							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");

							WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
									"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"),
									false);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

							if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
						} else

							WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
						String updateProfileReq5 = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
						String updateProfileResponseXML5 = WSClient.processSOAPMessage(updateProfileReq5);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML5,
								"RemoveChannelSellLimitsRS_Success", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b> Removed the limits set on the channel set limits </b>");
						}

					}
				} else
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite --> Set Channel Sell Limit blocked");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking", "OWS",
	// "Reservation" })

	public void createBooking_1213455() {
		try {
			resv = null;

			String testName = "createBooking_38395";
			WSClient.startTest(testName, "Verify that booking is created and DEFAULT_PAYMENT_TYPE is validated",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			String pay = OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01");

			OPERALib.setOperaHeader(OPERALib.getUserName());
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate11}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_568}"));
			WSClient.setData("{var_busdate12}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_569}"));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_param}", "DEFAULT_PAYMENT_METHOD");
			// WSClient.setData("{var_resvType}", "QA_4PM");
			// WSClient.setData("{var_rate}", "CKRC");
			WSClient.writeToReport(LogStatus.INFO, "<b> Retrieving the value of DEFAULT_PAYMENT_TYPE </b>");
			String query3 = WSClient.getQuery("ChangeChannelParameters", "QS_02");
			LinkedHashMap<String, String> bookingDets3 = WSClient.getDBRow(query3);
			String payment = bookingDets3.get("PARAMETER_VALUE");
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_564}") + "T14:00:00+05:30");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_565}") + "T14:00:00+05:30");
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
				String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML3,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML3,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML3,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML3, "CreateBookingResponse_Result_Text",
							true)) {

						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML3,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML3,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq3,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML3, "Profile_ProfileIDs_UniqueID",
							profileID, false);
					elem = WSClient.getElementValue(updateProfileResponseXML3,
							"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq3, "RoomStay_RoomTypes_RoomType_roomTypeCode",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Payment Type</b>");

					elem = WSClient.getElementValue(updateProfileResponseXML3,
							"PaymentsAccepted_PaymentType_OtherPayment_type", XMLType.RESPONSE);
					if (WSAssert.assertEquals(pay, elem, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + pay + " Actual value :" + elem);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + pay + " Actual value :" + elem);
					CancelReservation.cancelReservation("DS_02");

				}

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void createBooking_121345() {
		try {
			resv = null;

			String testName = "createBooking_3866395";
			WSClient.startTest(testName,
					"Verify that booking is created when DEFAULT_PAYMENT_METHOD_PER_CHANNEL is unset",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			// WSClient.setData("{var_roomType}", "4RT");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate11}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_568}"));
			WSClient.setData("{var_busdate12}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_569}"));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "N");
			WSClient.setData("{var_parname}", "DEFAULT_PAYMENT_METHOD_PER_CHANNEL");
			WSClient.setData("{var_param}", "DEFAULT_PAYMENT_METHOD_PER_CHANNEL");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			String query2 = WSClient.getQuery("ChangeChannelParameters", "QS_02");
			LinkedHashMap<String, String> bookingDets2 = WSClient.getDBRow(query2);
			if (WSAssert.assertEquals("N", bookingDets2.get("PARAMETER_VALUE"), true))
				WSClient.writeToReport(LogStatus.INFO, "<b>DEFAULT_PAYMENT_METHOD_PER_CHANNEL is disabled</b>");
			else {
				String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
				String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
				if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success",
						true)) {
					String query3 = WSClient.getQuery("ChangeChannelParameters", "QS_02");
					LinkedHashMap<String, String> bookingDets3 = WSClient.getDBRow(query3);
					if (WSAssert.assertEquals("N", bookingDets2.get("PARAMETER_VALUE"), true))
						WSClient.writeToReport(LogStatus.INFO, "<b>DEFAULT_PAYMENT_METHOD_PER_CHANNEL is disabled</b>");

				}
			}
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_564}") + "T14:00:00+05:30");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_565}") + "T14:00:00+05:30");
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
				String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML3,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML3,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML3,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML3, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML3,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML3,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq3,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML3, "Profile_ProfileIDs_UniqueID",
							profileID, false);
					elem = WSClient.getElementValue(updateProfileResponseXML3,
							"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq3, "RoomStay_RoomTypes_RoomType_roomTypeCode",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);

					elem = WSClient.getElementValue(updateProfileResponseXML3,
							"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq3, "RoomStays_RoomStay_Guarantee_guaranteeType",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML3,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
					String query = WSClient.getQuery("QS_01");
					LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
					String query1 = WSClient.getQuery("QS_03");
					LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
					if (WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"), bookingDets1.get("CONFIRMATION_NO"),
							true))
						WSClient.writeToReport(LogStatus.PASS,
								"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
										+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));
					else
						WSClient.writeToReport(LogStatus.FAIL,
								"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
										+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));

					if (WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"), bookingDets1.get("RESV_NAME_ID"), true))
						WSClient.writeToReport(LogStatus.PASS,
								"Reservation ID -> Expected value:" + bookingDets.get("RESV_NAME_ID")
										+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));
					else
						WSClient.writeToReport(LogStatus.FAIL,
								"Reservation ID-> Expected value:" + bookingDets.get("RESV_NAME_ID") + " Actual value :"
										+ bookingDets1.get("RESV_NAME_ID"));

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
							"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

					if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
				WSClient.setData("{var_par}", "Y");
				String updateProfileReq5 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
				String updateProfileResponseXML5 = WSClient.processSOAPMessage(updateProfileReq5);
				if (WSAssert.assertIfElementExists(updateProfileResponseXML5, "ChangeChannelParametersRS_Success",
						true)) {
					query2 = WSClient.getQuery("ChangeChannelParameters", "QS_02");
					bookingDets2 = WSClient.getDBRow(query2);
					if (WSAssert.assertEquals("Y", bookingDets2.get("PARAMETER_VALUE"), true))
						WSClient.writeToReport(LogStatus.INFO, "<b>DEFAULT_PAYMENT_METHOD_PER_CHANNEL is enabled</b>");

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_12321() {
		try {
			resv = null;

			String testName = "createBooking_3836695";
			WSClient.startTest(testName,
					"Verify that booking is NOT created when the number of units EXCEED the maximum room limit for a reservation",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			fetchAvailability(rate, rt);
			WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_type}", "String");
			WSClient.setData("{var_parname}", "MAXIMUMROOMLIMIT");
			WSClient.setData("{var_par}", "3");

			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Room Limit : 3</b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {

				profileID = CreateProfile.createProfile("DS_00");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					WSClient.setData("{var_profileSource}", interfaceName);
					WSClient.setData("{var_resort}", resortOperaValue);
					WSClient.setData("{var_extResort}", resortExtValue);

					WSClient.setData("{var_time}", "09:00:00");

					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
							OWSLib.getChannelType(interfaceName),
							OWSLib.getChannelCarier(resortOperaValue, interfaceName));
					WSClient.writeToReport(LogStatus.INFO, "<b>Booking is created with 4 units</b>");

					String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_07");
					String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML3,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
							"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
						if (WSAssert.assertIfElementExists(updateProfileResponseXML3,
								"CreateBookingResponse_Result_OperaErrorCode", true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"<b>Create Booking Response gives the error message-----></b> "
											+ WSClient.getElementValue(updateProfileResponseXML3,
													"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE));
						}

					}
					WSClient.setData("{var_par}", "6");

					System.out.println(resortExtValue);
					OPERALib.setOperaHeader(OPERALib.getUserName());
					// ****** Prerequisite : Creating a Profile with basic
					// details*****//
					String updateProfileReq4 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
					String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML4, "ChangeChannelParametersRS_Success",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Maximum Room Limit is set to the previous value</b>");
					}
				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_123212() {
		try {
			resv = null;

			String testName = "createBooking_3839665";
			WSClient.startTest(testName,
					"Verify that booking is created when the number of units EQUAL the maximum room limit for a reservation",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			// WSClient.setData("{var_busdate11}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_265}"));
			// WSClient.setData("{var_busdate12}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_266}"));
			fetchAvailability(rate, rt);
			WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_type}", "String");
			WSClient.setData("{var_parname}", "MAXIMUMROOMLIMIT");
			WSClient.setData("{var_par}", "3");

			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Room Limit : 3</b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {

				profileID = CreateProfile.createProfile("DS_00");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					WSClient.setData("{var_profileSource}", interfaceName);
					WSClient.setData("{var_resort}", resortOperaValue);
					WSClient.setData("{var_extResort}", resortExtValue);

					WSClient.setData("{var_time}", "09:00:00");

					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
							OWSLib.getChannelType(interfaceName),
							OWSLib.getChannelCarier(resortOperaValue, interfaceName));
					WSClient.writeToReport(LogStatus.INFO, "<b>Booking is created with 3 units</b>");

					String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_42");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						if (WSAssert.assertIfElementExists(updateProfileResponseXML,
								"CreateBookingResponse_Result_Text", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Create Booking Response gives the error message-----></b> "
											+ WSClient.getElementValue(updateProfileResponseXML,
													"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
						}

						// validating the data

						String elem = WSClient.getElementValue(updateProfileResponseXML,
								"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
						String elem1 = WSClient.getElementValue(updateProfileReq,
								"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
						resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
						WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

						if (WSAssert.assertEquals(elem, elem1, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + elem + " Actual value :" + elem1);
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + elem + " Actual value :" + elem1);
						WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
								profileID, false);
						elem = WSClient.getElementValue(updateProfileResponseXML,
								"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
						elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
								XMLType.REQUEST);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

						if (WSAssert.assertEquals(elem, elem1, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + elem + " Actual value :" + elem1);
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + elem + " Actual value :" + elem1);

						elem = WSClient.getElementValue(updateProfileResponseXML,
								"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
						elem1 = WSClient.getElementValue(updateProfileReq, "RoomStays_RoomStay_Guarantee_guaranteeType",
								XMLType.REQUEST);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

						if (WSAssert.assertEquals(elem, elem1, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + elem + " Actual value :" + elem1);
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + elem + " Actual value :" + elem1);
						String resv1 = WSClient.getElementValue(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
						WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
						WSClient.writeToReport(LogStatus.INFO, "<b> Validating the records in DB </b>");
						String query = WSClient.getQuery("QS_01");
						LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
						String query1 = WSClient.getQuery("QS_03");
						LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
						if (WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"),
								bookingDets1.get("CONFIRMATION_NO"), true))
							WSClient.writeToReport(LogStatus.PASS,
									"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
											+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));
						else
							WSClient.writeToReport(LogStatus.FAIL,
									"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
											+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));

						if (WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"), bookingDets1.get("RESV_NAME_ID"),
								true))
							WSClient.writeToReport(LogStatus.PASS,
									"Reservation ID -> Expected value:" + bookingDets.get("RESV_NAME_ID")
											+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));
						else
							WSClient.writeToReport(LogStatus.FAIL,
									"Reservation ID-> Expected value:" + bookingDets.get("RESV_NAME_ID")
											+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));

						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");

						WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"), false);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

						if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));

					} else if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"CreateBookingResponse_Result_resultStatusFlag", "FAIL", true))
						WSClient.writeToReport(LogStatus.FAIL,
								"Create Booking fails despite not exceeding the maximum room limit");

				}
				WSClient.setData("{var_par}", "6");

				System.out.println(resortExtValue);
				OPERALib.setOperaHeader(OPERALib.getUserName());
				// ****** Prerequisite : Creating a Profile with basic
				// details*****//
				String updateProfileReq4 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
				String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
				if (WSAssert.assertIfElementExists(updateProfileResponseXML4, "ChangeChannelParametersRS_Success",
						true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Maximum Room Limit is set to the previous value</b>");
				}
			} else

				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite Blocked : Change Channel Parameter");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_3839566_MaximumRoomLimitExceed() {
		try {
			resv = null;

			String testName = "createBooking_3839566";
			WSClient.startTest(testName,
					"Verify that booking is created when the number of units EXCEED the maximum room limit for a reservation when OVERRIDE is set",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			fetchAvailability(rate, rt);
			WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_type}", "String");
			WSClient.setData("{var_parname}", "MAXIMUMROOMLIMIT");
			WSClient.setData("{var_par}", "2");

			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>MAXIMUM ROOM LIMIT : 2</b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {
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
					profileID = CreateProfile.createProfile("DS_00");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);

						/*******************
						 * Prerequisite 2:Create a Reservation
						 ************************/

						WSClient.setData("{var_profileSource}", interfaceName);
						WSClient.setData("{var_resort}", resortOperaValue);
						WSClient.setData("{var_extResort}", resortExtValue);

						WSClient.setData("{var_time}", "09:00:00");

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));
						WSClient.writeToReport(LogStatus.INFO, "<b>Booking is created with 3 units</b>");

						String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_42");
						String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
						WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							if (WSAssert.assertIfElementExists(updateProfileResponseXML,
									"CreateBookingResponse_Result_Text", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Create Booking Response gives the error message-----></b> "
												+ WSClient.getElementValue(updateProfileResponseXML,
														"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
							}

							// validating the data

							String elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
							String elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
							resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							WSClient.setData("{var_resvId}",
									WSClient.getElementValueByAttribute(updateProfileResponseXML,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
									profileID, false);
							elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);

							elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							String resv1 = WSClient.getElementValue(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
							WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
							WSClient.writeToReport(LogStatus.INFO, "<b> Validating the records in DB </b>");
							String query = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
							String query1 = WSClient.getQuery("QS_03");
							LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
							if (WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"),
									bookingDets1.get("CONFIRMATION_NO"), true))
								WSClient.writeToReport(LogStatus.PASS,
										"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
												+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));
							else
								WSClient.writeToReport(LogStatus.FAIL,
										"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
												+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));

							if (WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"), bookingDets1.get("RESV_NAME_ID"),
									true))
								WSClient.writeToReport(LogStatus.PASS,
										"Reservation ID -> Expected value:" + bookingDets.get("RESV_NAME_ID")
												+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));
							else
								WSClient.writeToReport(LogStatus.FAIL,
										"Reservation ID-> Expected value:" + bookingDets.get("RESV_NAME_ID")
												+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));

							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");

							WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"),
									false);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

							if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));

						} else if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"CreateBookingResponse_Result_resultStatusFlag", "FAIL", true))
							WSClient.writeToReport(LogStatus.FAIL, "Create Booking fails despite OVERRIDE is set to Y");

					}
				}
				WSClient.setData("{var_par}", "6");

				System.out.println(resortExtValue);
				OPERALib.setOperaHeader(OPERALib.getUserName());
				// ****** Prerequisite : Creating a Profile with basic
				// details*****//
				String updateProfileReq4 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
				String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
				if (WSAssert.assertIfElementExists(updateProfileResponseXML4, "ChangeChannelParametersRS_Success",
						true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Maximum Room Limit is set to the previous value</b>");
				}
			} else

				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite Blocked : Change Channel Parameter");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_1222321() {
		try {
			resv = null;

			String testName = "createBooking_3867395";
			WSClient.startTest(testName,
					"Verify that booking is created when MAXIMUM ROOM LIMIT for a reservation is not exceeded",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");
			// WSClient.setData("{var_resvType}", "QA_4PM");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			fetchAvailability(rate, rt);
			WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_type}", "String");
			WSClient.setData("{var_parname}", "MAXIMUMROOMLIMIT");
			WSClient.setData("{var_par}", "3");

			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Room Limit is 3</b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {

				profileID = CreateProfile.createProfile("DS_00");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					WSClient.setData("{var_profileSource}", interfaceName);
					WSClient.setData("{var_resort}", resortOperaValue);
					WSClient.setData("{var_extResort}", resortExtValue);

					WSClient.setData("{var_time}", "09:00:00");

					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
							OWSLib.getChannelType(interfaceName),
							OWSLib.getChannelCarier(resortOperaValue, interfaceName));
					WSClient.writeToReport(LogStatus.INFO, "<b>Booking is created with 1 unit</b>");

					String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
					String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML3,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
							"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML3,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
						resv = WSClient.getElementValueByAttribute(updateProfileResponseXML3,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML3,
								"CreateBookingResponse_Result_Text", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Create Booking Response gives the error message-----></b> "
											+ WSClient.getElementValue(updateProfileResponseXML3,
													"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
						}

						// validating the data

						String elem = WSClient.getElementValue(updateProfileResponseXML3,
								"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
						String elem1 = WSClient.getElementValue(updateProfileReq3,
								"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

						if (WSAssert.assertEquals(elem, elem1, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + elem + " Actual value :" + elem1);
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + elem + " Actual value :" + elem1);
						WSAssert.assertIfElementValueEquals(updateProfileResponseXML3, "Profile_ProfileIDs_UniqueID",
								profileID, false);
						elem = WSClient.getElementValue(updateProfileResponseXML3,
								"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
						elem1 = WSClient.getElementValue(updateProfileReq3, "RoomStay_RoomTypes_RoomType_roomTypeCode",
								XMLType.REQUEST);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

						if (WSAssert.assertEquals(elem, elem1, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + elem + " Actual value :" + elem1);
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + elem + " Actual value :" + elem1);

						elem = WSClient.getElementValue(updateProfileResponseXML3,
								"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
						elem1 = WSClient.getElementValue(updateProfileReq3,
								"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

						if (WSAssert.assertEquals(elem, elem1, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + elem + " Actual value :" + elem1);
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + elem + " Actual value :" + elem1);
						WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML3,
								"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
						String query = WSClient.getQuery("QS_01");
						LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
						String query1 = WSClient.getQuery("QS_03");
						LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
						if (WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"),
								bookingDets1.get("CONFIRMATION_NO"), true))
							WSClient.writeToReport(LogStatus.PASS,
									"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
											+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));
						else
							WSClient.writeToReport(LogStatus.FAIL,
									"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
											+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));

						if (WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"), bookingDets1.get("RESV_NAME_ID"),
								true))
							WSClient.writeToReport(LogStatus.PASS,
									"Reservation ID -> Expected value:" + bookingDets.get("RESV_NAME_ID")
											+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));
						else
							WSClient.writeToReport(LogStatus.FAIL,
									"Reservation ID-> Expected value:" + bookingDets.get("RESV_NAME_ID")
											+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));

						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");

						WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
								"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"), false);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

						if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
						WSClient.setData("{var_par}", "6");

						System.out.println(resortExtValue);
						OPERALib.setOperaHeader(OPERALib.getUserName());
						// ****** Prerequisite : Creating a Profile with basic
						// details*****//
						String updateProfileReq4 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
						String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML4,
								"ChangeChannelParametersRS_Success", false)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Maximum Room Limit is set to the previous value</b>");
						}
					} else

						WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking", "OWS",
	// "Reservation" })

	public void createBooking_12311() {
		try {
			resv = null;

			String testName = "createBooking_38395";
			WSClient.startTest(testName,
					"Verify that booking is created when sell limit is restricted for a particular set of days and booking is created on the day when restriction hasn't been made",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate11}", "2018-08-01");
			WSClient.setData("{var_busdate12}", "2018-08-06");
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_type}", "Boolean");

			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the CHANNEL INVENTORY</b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {
				WSClient.writeToReport(LogStatus.INFO,
						"<b>SELL LIMIT is set to all the days, except FRIDAY ; LIMIT : 1</b>");

				String updateProfileReq1 = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_01");
				String updateProfileResponseXML1 = WSClient.processSOAPMessage(updateProfileReq1);
				if (WSAssert.assertIfElementExists(updateProfileResponseXML1,
						"SetChannelSellLimitsByDateRangeRS_Success", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Limits are fetched</b>");

					String updateProfileReq4 = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_01");
					String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
					if (!WSAssert.assertIfElementExists(updateProfileResponseXML4, "FetchChannelSellLimitsRS_Success",
							true)) {
						WSClient.writeToReport(LogStatus.WARNING, "Fetch Channel Sell Limit fails");
					} else {
						WSClient.setData("{var_id}", WSClient.getElementValue(updateProfileResponseXML4,
								"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
						profileID = CreateProfile.createProfile("DS_00");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);

							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							WSClient.setData("{var_profileSource}", interfaceName);
							WSClient.setData("{var_resort}", resortOperaValue);
							WSClient.setData("{var_extResort}", resortExtValue);
							WSClient.setData("{var_busdate}", "2018-08-03T06:00:00");
							WSClient.setData("{var_busdate1}", "2018-08-03T12:00:00");
							WSClient.setData("{var_time}", "09:00:00");

							OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
									OWSLib.getChannelType(interfaceName),
									OWSLib.getChannelCarier(resortOperaValue, interfaceName));
							WSClient.writeToReport(LogStatus.INFO, "<b>Booking is created with 2 units</b>");

							String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_23");
							String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);
							WSClient.setData("{var_resvId}",
									WSClient.getElementValueByAttribute(updateProfileResponseXML3,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
							if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								WSClient.setData("{var_resvId}",
										WSClient.getElementValueByAttribute(updateProfileResponseXML3,
												"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
								resv = WSClient.getElementValueByAttribute(updateProfileResponseXML3,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								if (WSAssert.assertIfElementExists(updateProfileResponseXML3,
										"CreateBookingResponse_Result_Text", true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Create Booking Response gives the error message-----></b> "
													+ WSClient.getElementValue(updateProfileResponseXML3,
															"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
								}

								// validating the data

								String elem = WSClient.getElementValue(updateProfileResponseXML3,
										"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
								String elem1 = WSClient.getElementValue(updateProfileReq3,
										"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);
								WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
										"Profile_ProfileIDs_UniqueID", profileID, false);
								elem = WSClient.getElementValue(updateProfileResponseXML3,
										"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
								elem1 = WSClient.getElementValue(updateProfileReq3,
										"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);

								elem = WSClient.getElementValue(updateProfileResponseXML3,
										"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
								elem1 = WSClient.getElementValue(updateProfileReq3,
										"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);
								WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML3,
										"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
								String query = WSClient.getQuery("QS_01");
								LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
								String query1 = WSClient.getQuery("QS_03");
								LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
								if (WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"),
										bookingDets1.get("CONFIRMATION_NO"), true))
									WSClient.writeToReport(LogStatus.PASS,
											"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
													+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));
								else
									WSClient.writeToReport(LogStatus.FAIL,
											"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
													+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));

								if (WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"),
										bookingDets1.get("RESV_NAME_ID"), true))
									WSClient.writeToReport(LogStatus.PASS,
											"Reservation ID -> Expected value:" + bookingDets.get("RESV_NAME_ID")
													+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));
								else
									WSClient.writeToReport(LogStatus.FAIL,
											"Reservation ID-> Expected value:" + bookingDets.get("RESV_NAME_ID")
													+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));

								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");

								WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
										"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"),
										false);
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

								if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
							} else

								WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

						}
					}
				} else
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite --> Set Channel Sell Limit blocked");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_122345() {
		try {
			resv = null;

			String testName = "createBooking_138395";
			WSClient.startTest(testName,
					"Verify that booking is not created when rate code that is not configured for the channel is passed ",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			// WSClient.setData("{var_roomType}", "4RT");
			String rateC = OperaPropConfig.getDataSetForCode("RateCode", "DS_02");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");

			WSClient.setData("{var_rate}", rateC);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate11}", "2018-09-24");
			WSClient.setData("{var_busdate12}", "2018-09-25");
			WSClient.setData("{var_channel}", OWSLib.getChannel());

			WSClient.setData("{var_par}", "Y");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//

			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}", "2018-08-21T06:00:00");
				WSClient.setData("{var_busdate1}", "2018-08-21T12:00:00");
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_24");
				String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML3,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
						"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(updateProfileResponseXML3,
							"CreateBookingResponse_Result_OperaErrorCode", true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML3,
												"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}

				} else
					WSClient.writeToReport(LogStatus.FAIL,
							"Create Booking Response does not give an error message when RATE CODE is not channel allowed");

			}

			else

				WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_1122345() {
		try {
			resv = null;
			String channel = OWSLib.getChannel();
			WSClient.setData("{var_channel}", channel);
			String testName = "createBooking_3778395";
			WSClient.startTest(testName,
					"Verify that booking is not created when room type that is not configured for the channel is passed ",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate11}", "2018-09-24");
			WSClient.setData("{var_busdate12}", "2018-09-25");

			WSClient.setData("{var_channel}", OWSLib.getChannel());
			// String rt = OperaPropConfig.getDataSetForCode("RoomType",
			// "DS_01");
			// WSClient.setData("{var_roomType}", rt);
			// WSClient.setData("{var_rate}", "CKRC");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String roomt = OperaPropConfig.getDataSetForCode("RoomType", "DS_01");
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");

			WSClient.setData("{var_roomType}", roomt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_par}", "Y");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//

			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}", "2018-08-21T06:00:00");
				WSClient.setData("{var_busdate1}", "2018-08-21T12:00:00");
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_25");
				String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);

				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
						"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(updateProfileResponseXML3,
							"CreateBookingResponse_Result_OperaErrorCode", true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML3,
												"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}

				} else
					WSClient.writeToReport(LogStatus.FAIL,
							"Create Booking Response does not give an error message when Room Type is not channel allowed");

			}

			else

				WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured :" + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_3839775_WhenSessionControlEnabled() {
		try {
			resv = null;

			String testName = "createBooking_3839775_WhenSessionControlEnabled";
			WSClient.startTest(testName, "Verify that booking is created when SESSION CONTROL is enabled",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate11}", "2018-02-01");
			WSClient.setData("{var_busdate12}", "2018-02-12");
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "SESSIONCONTROL");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_param}", "SESSION_CONTROL");

			// WSClient.setData("{var_resvType}", "QA_4PM");
			// WSClient.setData("{var_rate}", "CKRC");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {
				String query1 = WSClient.getQuery("QS_01");
				LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
				WSAssert.assertEquals("Y", bookingDets1.get("SESSION_CONTROL"), false);

				profileID = CreateProfile.createProfile("DS_00");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					/*******************
					 * Prerequisite 2:Create a Reservation
					 ************************/

					WSClient.setData("{var_profileSource}", interfaceName);
					WSClient.setData("{var_resort}", resortOperaValue);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_busdate}", "2018-02-09T06:00:00");
					WSClient.setData("{var_busdate1}", "2018-02-09T12:00:00");
					WSClient.setData("{var_time}", "09:00:00");

					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
							OWSLib.getChannelType(interfaceName),
							OWSLib.getChannelCarier(resortOperaValue, interfaceName));
					fetchAvailability(rate, rt);

					String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
					String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);

					if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
							"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML3,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
						WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML3,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
						resv = WSClient.getElementValueByAttribute(updateProfileResponseXML3,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML3,
								"CreateBookingResponse_Result_Text", true)) {

							WSClient.writeToReport(LogStatus.INFO,
									"<b>Create Booking Response gives the error message-----></b> "
											+ WSClient.getElementValue(updateProfileResponseXML3,
													"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
						}

						// validating the data

						String elem = WSClient.getElementValue(updateProfileResponseXML3,
								"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
						String elem1 = WSClient.getElementValue(updateProfileReq3,
								"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

						if (WSAssert.assertEquals(elem, elem1, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + elem + " Actual value :" + elem1);
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + elem + " Actual value :" + elem1);
						WSAssert.assertIfElementValueEquals(updateProfileResponseXML3, "Profile_ProfileIDs_UniqueID",
								profileID, false);

						if (WSAssert.assertEquals(elem, elem1, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + elem + " Actual value :" + elem1);
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + elem + " Actual value :" + elem1);
						WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML3,
								"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
						String query = WSClient.getQuery("QS_03");
						LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation Status</b>");
						if (WSAssert.assertEquals("PROSPECT", bookingDets.get("RESV_STATUS"), true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected: PROSPECT Actual: " + bookingDets.get("RESV_STATUS"));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected: PROSPECT Actual: " + bookingDets.get("RESV_STATUS"));

						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Code</b>");
						if (WSAssert.assertEquals("GDS_SESSION", bookingDets.get("GUARANTEE_TYPE"), true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected: GDS_SESSION Actual: " + bookingDets.get("GUARANTEE_TYPE"));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected: GDS_SESSION  Actual: " + bookingDets.get("GUARANTEE_TYPE"));

						WSClient.setData("{var_par}", "N");
						WSClient.setData("{var_parname}", "SESSIONCONTROL");
						WSClient.setData("{var_type}", "Boolean");
						WSClient.setData("{var_param}", "SESSION_CONTROL");

						System.out.println(resortExtValue);
						OPERALib.setOperaHeader(OPERALib.getUserName());
						// ****** Prerequisite : Creating a Profile with basic
						// details*****//
						String updateProfileReq4 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
						String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML4,
								"ChangeChannelParametersRS_Success", true)) {
							query1 = WSClient.getQuery("QS_01");
							bookingDets1 = WSClient.getDBRow(query1);
							WSAssert.assertEquals("N", bookingDets1.get("SESSION_CONTROL"), false);

						} else

							WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_3839775_SessionControlDisabled() {
		try {
			resv = null;
			String channel = OWSLib.getChannel();
			WSClient.setData("{var_channel}", channel);
			String testName = "createBooking_3839775_SessionControlDisabled";
			WSClient.startTest(testName, "Verify that booking is created when SESSION CONTROL is disabled",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate11}", "2018-02-01");
			WSClient.setData("{var_busdate12}", "2018-02-12");
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			// WSClient.setData("{var_resvType}", "QA_4PM");
			WSClient.setData("{var_par}", "N");
			WSClient.setData("{var_parname}", "SESSIONCONTROL");
			WSClient.setData("{var_type}", "Boolean");
			// WSClient.setData("{var_rate}", "CKRC");

			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the SESSION CONTROL</b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {
				profileID = CreateProfile.createProfile("DS_00");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{var_profileSource}", interfaceName);
					WSClient.setData("{var_resort}", resortOperaValue);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_busdate}", "2018-02-09T06:00:00");
					WSClient.setData("{var_busdate1}", "2018-02-09T12:00:00");
					WSClient.setData("{var_time}", "09:00:00");

					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
							OWSLib.getChannelType(interfaceName),
							OWSLib.getChannelCarier(resortOperaValue, interfaceName));
					fetchAvailability(rate, rt);

					String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
					String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML3,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
							"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML3,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
						resv = WSClient.getElementValueByAttribute(updateProfileResponseXML3,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML3,
								"CreateBookingResponse_Result_Text", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Create Booking Response gives the error message-----></b> "
											+ WSClient.getElementValue(updateProfileResponseXML3,
													"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
						}

						// validating the data
						WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML3,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
						String elem = WSClient.getElementValue(updateProfileResponseXML3,
								"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
						String elem1 = WSClient.getElementValue(updateProfileReq3,
								"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

						if (WSAssert.assertEquals(elem, elem1, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + elem + " Actual value :" + elem1);
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + elem + " Actual value :" + elem1);
						WSAssert.assertIfElementValueEquals(updateProfileResponseXML3, "Profile_ProfileIDs_UniqueID",
								profileID, false);

						if (WSAssert.assertEquals(elem, elem1, true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + elem + " Actual value :" + elem1);
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + elem + " Actual value :" + elem1);
						WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML3,
								"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
						String query = WSClient.getQuery("QS_03");
						LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation Status</b>");
						if (WSAssert.assertEquals("RESERVED", bookingDets.get("RESV_STATUS"), true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected: RESERVED Actual: " + bookingDets.get("RESV_STATUS"));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected: RESERVED Actual: " + bookingDets.get("RESV_STATUS"));

						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Code</b>");
						if (WSAssert.assertEquals(resvt, bookingDets.get("GUARANTEE_TYPE"), true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected: " + resvt + " Actual: " + bookingDets.get("GUARANTEE_TYPE"));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected: " + resvt + " Actual: " + bookingDets.get("GUARANTEE_TYPE"));

						WSClient.setData("{var_par}", "N");
						WSClient.setData("{var_parname}", "SESSIONCONTROL");
						WSClient.setData("{var_type}", "Boolean");
						WSClient.setData("{var_param}", "SESSION_CONTROL");

						System.out.println(resortExtValue);
						OPERALib.setOperaHeader(OPERALib.getUserName());
						// ****** Prerequisite : Creating a Profile with basic
						// details*****//
						String updateProfileReq4 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
						String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML4,
								"ChangeChannelParametersRS_Success", true)) {
							String query1 = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
							if (WSAssert.assertEquals("N", bookingDets1.get("SESSION_CONTROL"), true))
								WSClient.writeToReport(LogStatus.INFO, "<b>Session Control is disabled</b>");
							else
								WSClient.writeToReport(LogStatus.INFO, "<b>Session Control is not disabled</b>");

						} else

							WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking",
	// "OWS","Reservation"})
	//
	// public void createBooking_122311() {
	// String testName = "createBooking_38395";
	// WSClient.startTest(testName, "Verify that booking is created when SESSION
	// CONTROL is enabled", "minimumRegression");
	// String interfaceName = OWSLib.getChannel();
	// String resortOperaValue = OPERALib.getResort();
	// String resortExtValue = OWSLib.getChannelResort(resortOperaValue,
	// interfaceName);
	//
	// WSClient.setData("{var_roomType}", "4RT");
	//
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	// WSClient.setData("{var_busdate11}", "2018-02-01");
	// WSClient.setData("{var_busdate12}", "2018-02-12");
	// WSClient.setData("{var_channel}",OWSLib.getChannel());
	// WSClient.setData("{var_par}","N");
	// WSClient.setData("{var_parname}","SESSIONCONTROL");
	// WSClient.setData("{var_type}","Boolean");
	// WSClient.setData("{var_param}","SESSION_CONTROL");
	// System.out.println(resortExtValue);
	// OPERALib.setOperaHeader(OPERALib.getUserName());
	// //****** Prerequisite : Creating a Profile with basic details*****//
	// String query1=WSClient.getQuery("ChangeChannelParameters","QS_01");
	// LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
	// if(WSAssert.assertEquals("N",bookingDets1.get("SESSION_CONTROL"),true))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b>SESSION CONTROL IS
	// DISABLED</b>");
	// }
	// else
	// {
	// String updateProfileReq2 =
	// WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
	// String updateProfileResponseXML2 =
	// WSClient.processSOAPMessage(updateProfileReq2);
	// if(WSAssert.assertIfElementExists(updateProfileResponseXML2,
	// "ChangeChannelParametersRS_Success",false))
	// {
	// String query2=WSClient.getQuery("ChangeChannelParameters","QS_01");
	// LinkedHashMap<String, String> bookingDets2 = WSClient.getDBRow(query2);
	// if(WSAssert.assertEquals("N",bookingDets2.get("SESSION_CONTROL"),false))
	// {
	// WSClient.writeToReport(LogStatus.INFO, "<b>SESSION CONTROL IS
	// DISABLED</b>");
	// }
	// }
	// }
	// profileID=CreateProfile.createProfile("DS_01");
	// if(!profileID.equals("error"))
	// {
	// WSClient.setData("{var_profileId}", profileID);
	//
	// /******************* Prerequisite 2:Create a Reservation
	// ************************/
	//
	// WSClient.setData("{var_profileSource}", interfaceName);
	// WSClient.setData("{var_resort}", resortOperaValue);
	// WSClient.setData("{var_extResort}", resortExtValue);
	// WSClient.setData("{var_busdate}","2018-02-09T06:00:00");
	// WSClient.setData("{var_busdate1}","2018-02-09T12:00:00");
	// WSClient.setData("{var_time}", "09:00:00");
	// String guar="4pm";
	//
	// OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(),
	// resortExtValue,
	// OWSLib.getChannelType(interfaceName),
	// OWSLib.getChannelCarier(resortOperaValue, interfaceName));
	// String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking",
	// "DS_01");
	// String updateProfileResponseXML3 =
	// WSClient.processSOAPMessage(updateProfileReq3);
	// if(WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
	// "CreateBookingResponse_Result_resultStatusFlag","SUCCESS", false))
	// {
	// if(WSAssert.assertIfElementExists(updateProfileResponseXML3,
	// "CreateBookingResponse_Result_Text", true))
	// {
	// WSClient.writeToReport(LogStatus.INFO,"<b>Create Booking Response gives
	// the error message-----></b>
	// "+WSClient.getElementValue(updateProfileResponseXML3,
	// "CreateBookingResponse_Result_Text", XMLType.RESPONSE));
	// }
	//
	//// validating the data
	//
	// String elem=WSClient.getElementValue(updateProfileResponseXML3,
	// "RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
	// String elem1=WSClient.getElementValue(updateProfileReq3,
	// "RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan
	// Code</b>");
	//
	// if(WSAssert.assertEquals(elem, elem1, true)){
	// WSClient.writeToReport(LogStatus.PASS, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// }
	// else
	// WSClient.writeToReport(LogStatus.FAIL, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
	// "Profile_ProfileIDs_UniqueID",
	// profileID, false);
	//
	//
	//
	// if(WSAssert.assertEquals(elem, elem1, true)){
	// WSClient.writeToReport(LogStatus.PASS, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// }
	// else
	// WSClient.writeToReport(LogStatus.FAIL, "Expected value:"+ elem+" Actual
	// value :"+elem1);
	// WSClient.setData("{var_resv}",WSClient.getElementValue(updateProfileResponseXML3,"HotelReservation_UniqueIDList_UniqueID",
	// XMLType.RESPONSE));
	// String query=WSClient.getQuery("QS_03");
	// LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation
	// Status</b>");
	// WSAssert.assertEquals("RESERVED", bookingDets.get("RESV_STATUS"), false);
	// WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee
	// Code</b>");
	// WSAssert.assertEquals(guar, bookingDets.get("GUARANTEE_TYPE"), false);
	//
	//
	//
	//
	// } }else
	//
	// WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
	// }

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_11345() {
		try {
			resv = null;
			String channel = OWSLib.getChannel();
			WSClient.setData("{var_channel}", channel);
			String testName = "createBooking_3839588";
			WSClient.startTest(testName,
					"Verify that booking is created when number of units EXCEED the sell limit for a date range where sell limit is NOT SET",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate11}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_968}"));
			WSClient.setData("{var_busdate12}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_969}"));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			// WSClient.setData("{var_resvType}", "QA_4PM");
			// WSClient.setData("{var_rate}", "CKRC");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);

			WSClient.setData("{var_channel}", OWSLib.getChannel());
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Setting the CHANNEL INVENTORY</b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Setting the Sell Limit as 1</b>");

				String updateProfileReq1 = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_01");
				String updateProfileResponseXML1 = WSClient.processSOAPMessage(updateProfileReq1);
				if (WSAssert.assertIfElementExists(updateProfileResponseXML1,
						"SetChannelSellLimitsByDateRangeRS_Success", true)) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Fetching the Sell Limit</b>");

					String updateProfileReq4 = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_01");
					String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML4, "FetchChannelSellLimitsRS_Success",
							true)) {
						WSClient.setData("{var_id}", WSClient.getElementValue(updateProfileResponseXML4,
								"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
					}
					profileID = CreateProfile.createProfile("DS_00");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);

						/*******************
						 * Prerequisite 2:Create a Reservation
						 ************************/

						WSClient.setData("{var_profileSource}", interfaceName);
						WSClient.setData("{var_resort}", resortOperaValue);
						WSClient.setData("{var_extResort}", resortExtValue);
						fetchAvailability(rate, rt);

						WSClient.setData("{var_time}", "09:00:00");

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));
						WSClient.writeToReport(LogStatus.INFO, "<b>Booking is created with 2 units</b>");

						String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_23");
						String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);
						WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML3,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
								"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							WSClient.setData("{var_resvId}",
									WSClient.getElementValueByAttribute(updateProfileResponseXML3,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
							resv = WSClient.getElementValueByAttribute(updateProfileResponseXML3,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							if (WSAssert.assertIfElementExists(updateProfileResponseXML3,
									"CreateBookingResponse_Result_Text", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Create Booking Response gives the error message-----></b> "
												+ WSClient.getElementValue(updateProfileResponseXML3,
														"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
							}

							// validating the data

							String elem = WSClient.getElementValue(updateProfileResponseXML3,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
							String elem1 = WSClient.getElementValue(updateProfileReq3,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
									"Profile_ProfileIDs_UniqueID", profileID, false);
							elem = WSClient.getElementValue(updateProfileResponseXML3,
									"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq3,
									"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);

							elem = WSClient.getElementValue(updateProfileResponseXML3,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq3,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML3,
									"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
							String query = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
							String query1 = WSClient.getQuery("QS_03");
							LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
							if (WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"),
									bookingDets1.get("CONFIRMATION_NO"), true))
								WSClient.writeToReport(LogStatus.PASS,
										"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
												+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));
							else
								WSClient.writeToReport(LogStatus.FAIL,
										"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
												+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));

							if (WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"), bookingDets1.get("RESV_NAME_ID"),
									true))
								WSClient.writeToReport(LogStatus.PASS,
										"Reservation ID -> Expected value:" + bookingDets.get("RESV_NAME_ID")
												+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));
							else
								WSClient.writeToReport(LogStatus.FAIL,
										"Reservation ID-> Expected value:" + bookingDets.get("RESV_NAME_ID")
												+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));

							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");

							WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
									"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"),
									false);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

							if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
							String updateProfileReq5 = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
							String updateProfileResponseXML5 = WSClient.processSOAPMessage(updateProfileReq5);
							if (WSAssert.assertIfElementExists(updateProfileResponseXML5,
									"RemoveChannelSellLimitsRS_Success", false)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> Removed the limits set on the channel set limits </b>");
							}
						} else

							WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

					}
				} else
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite --> Set Channel Sell Limit blocked");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_depositrate() throws Exception {
		try {
			resv = null;

			String testName = "createBooking_3834495";
			WSClient.startTest(testName,
					"Verify that booking is Created for a profile with deposit policy and cancellation policy attached to rate code",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_08");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_07");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_11");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			// WSClient.setData("{var_rate}", "SUITE20");
			// WSClient.setData("{var_resvType}", "QA_4PM");
			// WSClient.setData("{var_roomType}", "CH_QTBED");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_busdate}", "2017-08-19T00:00:00");
				WSClient.setData("{var_busdate1}", "2017-08-20T00:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				WSClient.setData("{var_deposit}", "FLAT100");
				// WSClient.setData("{var_orate}", "QA_SUITE20");
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);
				fetchAvailability(rate, rt);

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_25");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

					String resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.setData("{var_resvId}", resv);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
							profileID, false);

					List<LinkedHashMap<String, String>> resValues = new ArrayList<LinkedHashMap<String, String>>();
					HashMap<String, String> xPath = new HashMap<String, String>();
					xPath.put("RatePlan_AdditionalDetails_AdditionalDetail_detailType",
							"RatePlan_AdditionalDetails_AdditionalDetail");
					xPath.put("AdditionalDetail_AdditionalDetailDescription_Text",
							"RatePlan_AdditionalDetails_AdditionalDetail");
					resValues = WSClient.getMultipleNodeList(updateProfileResponseXML, xPath, true, XMLType.RESPONSE);

					String cancelDate = WSClient.getElementValue(updateProfileResponseXML,
							"RatePlans_RatePlan_CancellationDateTime", XMLType.RESPONSE);
					String depositAmount = WSClient.getElementValue(updateProfileResponseXML,
							"RatePlan_DepositRequired_DepositAmount", XMLType.RESPONSE);

					String dueDate = WSClient.getElementValue(updateProfileResponseXML,
							"RatePlan_DepositRequired_DueDate", XMLType.RESPONSE);

					for (int i = 0; i < resValues.size(); i++) {
						if (resValues.get(i).get("DetailType").equals("CancelPolicy")) {
							resValues.get(i).put("CancelDate", cancelDate.substring(0, cancelDate.indexOf('T')));
						}
						if (resValues.get(i).get("DetailType").equals("DepositPolicy")) {
							resValues.get(i).put("DepositAmount", depositAmount);
							resValues.get(i).put("DueDate", dueDate);
						}
					}
					WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the DB values of Deposit policy</b>");
					LinkedHashMap<String, String> depositValues = WSClient
							.getDBRow(WSClient.getQuery("OWSModifyBooking", "QS_32"));
					WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the DB values of Cancellation policy</b>");

					LinkedHashMap<String, String> cancelValues = WSClient
							.getDBRow(WSClient.getQuery("OWSModifyBooking", "QS_31"));
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
										+ depositValues.get("Text") + " in order to guarantee your reservation.");
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

				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, e.toString());
		} finally {
			if (resv != null)
				CancelReservation.cancelReservation("DS_02");

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_depositrateresvType() throws Exception {
		try {
			resv = null;

			String testName = "createBooking_3448395";
			WSClient.startTest(testName,
					"Verify that booking is Created for a profile with deposit policy attached to both the rate code and reservation Type",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_08");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_07");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_11");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			// WSClient.setData("{var_rate}", "SUITE20");
			// WSClient.setData("{var_resvType}", "QA_4PM");
			// WSClient.setData("{var_roomType}", "CH_QTBED");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_busdate}", "2017-08-09T00:00:00");
				WSClient.setData("{var_busdate1}", "2017-08-10T00:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				WSClient.setData("{var_deposit}", "FLAT100");
				// WSClient.setData("{var_orate}", "QA_SUITE20");
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_25");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

					String resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.setData("{var_resvId}", resv);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
							profileID, false);

					List<LinkedHashMap<String, String>> resValues = new ArrayList<LinkedHashMap<String, String>>();
					HashMap<String, String> xPath = new HashMap<String, String>();
					xPath.put("RatePlan_AdditionalDetails_AdditionalDetail_detailType",
							"RatePlan_AdditionalDetails_AdditionalDetail");
					xPath.put("AdditionalDetail_AdditionalDetailDescription_Text",
							"RatePlan_AdditionalDetails_AdditionalDetail");
					resValues = WSClient.getMultipleNodeList(updateProfileResponseXML, xPath, true, XMLType.RESPONSE);

					String cancelDate = WSClient.getElementValue(updateProfileResponseXML,
							"RatePlans_RatePlan_CancellationDateTime", XMLType.RESPONSE);
					String depositAmount = WSClient.getElementValue(updateProfileResponseXML,
							"RatePlan_DepositRequired_DepositAmount", XMLType.RESPONSE);

					String dueDate = WSClient.getElementValue(updateProfileResponseXML,
							"RatePlan_DepositRequired_DueDate", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Deposit Policy attached to the rate code is given a higher priority than the deposit policy attached to the reservation Type</b>");

					for (int i = 0; i < resValues.size(); i++) {
						if (resValues.get(i).get("DetailType").equals("CancelPolicy")) {
							resValues.get(i).put("CancelDate", cancelDate.substring(0, cancelDate.indexOf('T')));
						}
						if (resValues.get(i).get("DetailType").equals("DepositPolicy")) {
							resValues.get(i).put("DepositAmount", depositAmount);
							resValues.get(i).put("DueDate", dueDate);
						}
					}
					WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the DB values of Deposit policy</b>");
					LinkedHashMap<String, String> depositValues = WSClient
							.getDBRow(WSClient.getQuery("OWSModifyBooking", "QS_32"));
					WSClient.writeToReport(LogStatus.INFO, "<b>Retrieving the DB values of Cancellation policy</b>");

					LinkedHashMap<String, String> cancelValues = WSClient
							.getDBRow(WSClient.getQuery("OWSModifyBooking", "QS_31"));
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
										+ depositValues.get("Text") + " in order to guarantee your reservation.");
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

				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, e.toString());
		} finally {
			if (resv != null)
				CancelReservation.cancelReservation("DS_02");

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation", "createBooking_4438395_depositresvType_Uday" })
	public void createBooking_4438395_depositresvType() {
		try {
			resv = null;

			String testName = "createBooking_4438395_depositresvType";
			WSClient.startTest(testName,
					"Verify that booking is Created for a profile with deposit policy and cancellation policy attached to guarantee code",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_rate}", "CKRC");
			// WSClient.setData("{var_roomType}", "4RT");
			// WSClient.setData("{var_resvType}", "4PM");
			String rateCode = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05");
			WSClient.setData("{var_rate}", rateCode);
			System.out.println(rateCode);
			String roomType = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04");
			WSClient.setData("{var_roomType}", roomType);
			String orate = OperaPropConfig.getDataSetForCode("Rate", "DS_12");
			WSClient.setData("{var_orate}", orate);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_ReservationType}", resvt);

			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				WSClient.setData("{var_busdate}", "2018-02-03T02:00:00");
				WSClient.setData("{var_busdate1}", "2018-02-04T02:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				WSClient.setData("{var_deposit}", "FLAT50");
				// WSClient.setData("{var_orate}", "CKRRC");
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				System.out.println(rateCode);

				fetchAvailability(rateCode, roomType);
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_04"));
				WSClient.setData("{var_resvType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_04"));

				
				
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					System.out.println(WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					List<LinkedHashMap<String, String>> resValues = new ArrayList<LinkedHashMap<String, String>>();
					HashMap<String, String> xPath = new HashMap<String, String>();
					xPath.put("RatePlan_AdditionalDetails_AdditionalDetail_detailType",
							"RatePlan_AdditionalDetails_AdditionalDetail");
					xPath.put("AdditionalDetail_AdditionalDetailDescription_Text",
							"RatePlan_AdditionalDetails_AdditionalDetail");
					resValues = WSClient.getMultipleNodeList(updateProfileResponseXML, xPath, true, XMLType.RESPONSE);
					String cancelDate = WSClient.getElementValue(updateProfileResponseXML,
							"RatePlans_RatePlan_CancellationDateTime", XMLType.RESPONSE);
					String depositAmount = WSClient.getElementValue(updateProfileResponseXML,
							"RatePlan_DepositRequired_DepositAmount", XMLType.RESPONSE);
					String dueDate = WSClient.getElementValue(updateProfileResponseXML,
							"RatePlan_DepositRequired_DueDate", XMLType.RESPONSE);
					for (int i = 0; i < resValues.size(); i++) {
						if (resValues.get(i).get("DetailType").equals("CancelPolicy")) {
							resValues.get(i).put("CancelDate", cancelDate.substring(0, cancelDate.indexOf('T')));
						}
						if (resValues.get(i).get("DetailType").equals("DepositPolicy")) {
							resValues.get(i).put("DepositAmount", depositAmount);
							resValues.get(i).put("DueDate", dueDate);
						}
					}

					LinkedHashMap<String, String> dbReservationType = WSClient
							.getDBRow(WSClient.getQuery("OWSCreateBooking", "QS_41"));
					
					WSClient.setData("{var_ReservationType}", dbReservationType.get("GuaranteeCode"));
					
					LinkedHashMap<String, String> depositValues = WSClient
							.getDBRow(WSClient.getQuery("OWSCreateBooking", "QS_40"));
					LinkedHashMap<String, String> cancelValues = WSClient
							.getDBRow(WSClient.getQuery("OWSCreateBooking", "QS_39"));
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
										+ depositValues.get("Text") + " in order to guarantee your reservation.");
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

			} else

				WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_1234() {
		try {
			resv = null;

			String testName = "createBooking_1234";
			WSClient.startTest(testName, "Verify that a company Profile is created using CreateBooking",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			OPERALib.setOperaHeader(OPERALib.getUserName());
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_companyName}", "ORACLE");
			profileID = CreateProfile.createProfile("DS_35");
			WSClient.setData("{var_profileId}", profileID);
			WSClient.setData("{var_profileSrcId}",
					WSClient.getDBRow(WSClient.getQuery("OWSCreateBooking", "QS_15")).get("NAME_CODE"));

			// ****** Prerequisite : Creating a Profile with basic
			// details*****//

			// WSClient.setData("{var_rate}", "CKRC");

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_570}") + "T06:00:00");
			WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_575}") + "T06:00:00");
			WSClient.setData("{var_time}", "09:00:00");

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
					OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
			fetchAvailability(rate, rt);

			String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_22");
			String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
			if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
					"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
						true)) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Create Booking Response gives the error message-----></b> " + WSClient.getElementValue(
									updateProfileResponseXML, "CreateBookingResponse_Result_Text", XMLType.RESPONSE));
				}

				// validating the data

				String elem = WSClient.getElementValue(updateProfileResponseXML,
						"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
				String elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RatePlans_RatePlan_ratePlanCode",
						XMLType.REQUEST);
				WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

				if (WSAssert.assertEquals(elem, elem1, true)) {
					WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
				} else
					WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
				// WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
				// "Profile_ProfileIDs_UniqueID",
				// operaProfileID, false);
				elem = WSClient.getElementValue(updateProfileResponseXML, "RoomStay_RoomRates_RoomRate_roomTypeCode",
						XMLType.RESPONSE);
				elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
						XMLType.REQUEST);
				WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

				if (WSAssert.assertEquals(elem, elem1, true)) {
					WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
				} else
					WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);

				elem = WSClient.getElementValue(updateProfileResponseXML, "RoomStays_RoomStay_Guarantee_guaranteeType",
						XMLType.RESPONSE);
				elem1 = WSClient.getElementValue(updateProfileReq, "RoomStays_RoomStay_Guarantee_guaranteeType",
						XMLType.REQUEST);
				WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

				if (WSAssert.assertEquals(elem, elem1, true)) {
					WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
				} else
					WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
				WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				WSClient.setData("{var_profileId}", WSClient.getElementValue(updateProfileResponseXML,
						"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID", false)) {
					WSClient.writeToReport(LogStatus.PASS,
							"Profile ID " + WSClient.getElementValue(updateProfileResponseXML,
									"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));
					String query = WSClient.getQuery("QS_01");
					LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
					String query1 = WSClient.getQuery("QS_03");
					LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
					WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"), bookingDets1.get("CONFIRMATION_NO"),
							false);
					WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"), bookingDets1.get("RESV_NAME_ID"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");
					String query3 = WSClient.getQuery("QS_04");
					LinkedHashMap<String, String> bookingDets2 = WSClient.getDBRow(query3);
					String title = WSClient.getElementValue(updateProfileReq, "Customer_PersonName_nameTitle",
							XMLType.REQUEST);
					String first = WSClient.getElementValue(updateProfileReq, "Customer_PersonName_firstName",
							XMLType.REQUEST);
					String last = WSClient.getElementValue(updateProfileReq, "Customer_PersonName_lastName",
							XMLType.REQUEST);
					String nat = WSClient.getElementValue(updateProfileReq, "ResGuest_Profiles_Profile_nationality",
							XMLType.REQUEST);

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating title</b>");
					if (WSAssert.assertEquals(title, bookingDets2.get("TITLE"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected value:" + title + " Actual value :" + bookingDets2.get("TITLE"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected value:" + title + " Actual value :" + bookingDets2.get("TITLE"));
					// WSAssert.assertEquals(title, bookingDets2.get("TITLE"),
					// false);

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating first name</b>");
					if (WSAssert.assertEquals(first, bookingDets2.get("SFIRST"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected value:" + first + " Actual value :" + bookingDets2.get("SFIRST"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected value:" + first + " Actual value :" + bookingDets2.get("SFIRST"));
					// WSAssert.assertEquals(first, bookingDets2.get("SFIRST"),
					// false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating last name</b>");
					if (WSAssert.assertEquals(last, bookingDets2.get("LAST"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected value:" + last + " Actual value :" + bookingDets2.get("LAST"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected value:" + last + " Actual value :" + bookingDets2.get("LAST"));
					// WSAssert.assertEquals(last, bookingDets2.get("LAST"),
					// false);

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating nationality</b>");
					if (WSAssert.assertEquals(nat, bookingDets2.get("NATIONALITY"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected value:" + nat + " Actual value :" + bookingDets2.get("NATIONALITY"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected value:" + nat + " Actual value :" + bookingDets2.get("NATIONALITY"));
					// WSAssert.assertEquals(nat,
					// bookingDets2.get("NATIONALITY"), false);

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

					if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating COMPANY DETAILS</b>");

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_Company_CompanyID",
							WSClient.getElementValue(updateProfileReq, "Profile_Company_CompanyID", XMLType.REQUEST),
							false);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_Company_CompanyType",
							WSClient.getElementValue(updateProfileReq, "Profile_Company_CompanyType", XMLType.REQUEST),
							false);
				} else

					WSClient.writeToReport(LogStatus.FAIL, "Profile is not created");
			} else

				WSClient.writeToReport(LogStatus.FAIL, "Create Booking fails");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_emailPHONE() {
		try {
			resv = null;

			String testName = "createBooking_emailPHONE";
			WSClient.startTest(testName, "Verify that booking is created for a profile with communication details",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// ****** Prerequisite : Creating a Profile with basic
			// details*****//

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_180}") + "T06:00:00");
			WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_185}") + "T06:00:00");
			WSClient.setData("{var_time}", "09:00:00");
			WSClient.setData("{var_phoneRole}", "PHONE");
			WSClient.setData("{var_phoneType}", "HOME");
			// WSClient.setData("{var_rate}", "CKRC");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
					OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
			fetchAvailability(rate, rt);

			String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_26");
			String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
			if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
					"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
						true)) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Create Booking Response gives the error message-----></b> " + WSClient.getElementValue(
									updateProfileResponseXML, "CreateBookingResponse_Result_Text", XMLType.RESPONSE));
				}

				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
				WSClient.setData("{var_profileId}", WSClient.getElementValue(updateProfileResponseXML,
						"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID", false)) {
					WSClient.writeToReport(LogStatus.PASS,
							"Profile ID " + WSClient.getElementValue(updateProfileResponseXML,
									"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));

					String query3 = WSClient.getQuery("QS_08");
					LinkedHashMap<String, String> bookingDets2 = WSClient.getDBRow(query3);
					String title = WSClient.getElementValue(updateProfileReq, "Customer_PersonName_nameTitle",
							XMLType.REQUEST);
					String first = WSClient.getElementValue(updateProfileReq, "Customer_PersonName_firstName",
							XMLType.REQUEST);
					String last = WSClient.getElementValue(updateProfileReq, "Customer_PersonName_lastName",
							XMLType.REQUEST);
					String nat = WSClient.getElementValue(updateProfileReq, "ResGuest_Profiles_Profile_nationality",
							XMLType.REQUEST);
					String email = WSClient.getElementValue(updateProfileReq, "Profile_EMails_NameEmail",
							XMLType.REQUEST);
					String phone = WSClient.getElementValue(updateProfileReq, "Phones_NamePhone_PhoneNumber",
							XMLType.REQUEST);
					String address = WSClient.getElementValue(updateProfileReq, "Addresses_NameAddress_AddressLine",
							XMLType.REQUEST);
					String state = WSClient.getElementValue(updateProfileReq, "Addresses_NameAddress_stateProv",
							XMLType.REQUEST);
					String country = WSClient.getElementValue(updateProfileReq, "Addresses_NameAddress_countryCode",
							XMLType.REQUEST);
					String zip = WSClient.getElementValue(updateProfileReq, "Addresses_NameAddress_postalCode",
							XMLType.REQUEST);
					String city = WSClient.getElementValue(updateProfileReq, "Addresses_NameAddress_cityName",
							XMLType.REQUEST);

					// WSAssert.assertEquals(title, bookingDets2.get("TITLE"),
					// false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating title</b>");
					if (WSAssert.assertEquals(title, bookingDets2.get("TITLE"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected value:" + title + " Actual value :" + bookingDets2.get("TITLE"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected value:" + title + " Actual value :" + bookingDets2.get("TITLE"));
					// WSAssert.assertEquals(title, bookingDets2.get("TITLE"),
					// false);

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating first name</b>");
					if (WSAssert.assertEquals(first, bookingDets2.get("FIRST"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected value:" + first + " Actual value :" + bookingDets2.get("FIRST"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected value:" + first + " Actual value :" + bookingDets2.get("FIRST"));
					// WSAssert.assertEquals(first, bookingDets2.get("SFIRST"),
					// false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating last name</b>");
					if (WSAssert.assertEquals(last, bookingDets2.get("LAST"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected value:" + last + " Actual value :" + bookingDets2.get("LAST"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected value:" + last + " Actual value :" + bookingDets2.get("LAST"));
					// WSAssert.assertEquals(last, bookingDets2.get("LAST"),
					// false);

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating nationality</b>");
					if (WSAssert.assertEquals(nat, bookingDets2.get("NATIONALITY"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected value:" + nat + " Actual value :" + bookingDets2.get("NATIONALITY"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected value:" + nat + " Actual value :" + bookingDets2.get("NATIONALITY"));
					// WSAssert.assertEquals(nat,
					// bookingDets2.get("NATIONALITY"), false);

					// WSClient.writeToReport(LogStatus.INFO, "<b>Validating
					// first name</b>");
					// WSAssert.assertEquals(first, bookingDets2.get("FIRST"),
					// false);
					// WSClient.writeToReport(LogStatus.INFO, "<b>Validating
					// last name</b>");
					// WSAssert.assertEquals(last, bookingDets2.get("LAST"),
					// false);
					// WSClient.writeToReport(LogStatus.INFO, "<b>Validating
					// nationality</b>");
					// WSAssert.assertEquals(nat,
					// bookingDets2.get("NATIONALITY"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating EMAIL</b>");
					if (WSAssert.assertEquals(email, bookingDets2.get("EMAIL"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected value:" + email + " Actual value :" + bookingDets2.get("EMAIL"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected value:" + email + " Actual value :" + bookingDets2.get("EMAIL"));
					// WSAssert.assertEquals(email, bookingDets2.get("EMAIL"),
					// false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating PHONE</b>");
					if (WSAssert.assertEquals(phone, bookingDets2.get("PHONE"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected value:" + phone + " Actual value :" + bookingDets2.get("PHONE"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected value:" + phone + " Actual value :" + bookingDets2.get("PHONE"));
					// WSAssert.assertEquals(phone, bookingDets2.get("PHONE"),
					// false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating ADDRESSLINE</b>");
					if (WSAssert.assertEquals(address, bookingDets2.get("ADDRESS"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected value:" + address + " Actual value :" + bookingDets2.get("ADDRESS"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected value:" + address + " Actual value :" + bookingDets2.get("ADDRESS"));
					// WSAssert.assertEquals(address,
					// bookingDets2.get("ADDRESS"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating STATE</b>");
					if (WSAssert.assertEquals(state, bookingDets2.get("STATE"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected value:" + state + " Actual value :" + bookingDets2.get("STATE"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected value:" + state + " Actual value :" + bookingDets2.get("STATE"));
					// WSAssert.assertEquals(state, bookingDets2.get("STATE"),
					// false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating COUNTRY</b>");
					if (WSAssert.assertEquals(country, bookingDets2.get("COUNTRY"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected value:" + country + " Actual value :" + bookingDets2.get("COUNTRY"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected value:" + country + " Actual value :" + bookingDets2.get("COUNTRY"));
					// WSAssert.assertEquals(country,
					// bookingDets2.get("COUNTRY"), false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating ZIP</b>");
					if (WSAssert.assertEquals(zip, bookingDets2.get("ZIP"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected value:" + zip + " Actual value :" + bookingDets2.get("ZIP"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected value:" + zip + " Actual value :" + bookingDets2.get("ZIP"));
					// WSAssert.assertEquals(zip, bookingDets2.get("ZIP"),
					// false);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating CITY</b>");
					if (WSAssert.assertEquals(city, bookingDets2.get("CITY"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Expected value:" + city + " Actual value :" + bookingDets2.get("CITY"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"Expected value:" + city + " Actual value :" + bookingDets2.get("CITY"));
					// WSAssert.assertEquals(city, bookingDets2.get("CITY"),
					// false);

				} else

					WSClient.writeToReport(LogStatus.FAIL, "Profile is not created");
			} else

				WSClient.writeToReport(LogStatus.FAIL, "Create Booking fails");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_legnumber() {
		try {
			resv = null;

			String testName = "createBooking_12345";
			WSClient.startTest(testName,
					"Verify that the leg number is populated correctly when create booking request is initiated with an existing reservation's confirmation Number",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_650}") + "T06:00:00");
			WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_655}") + "T06:00:00");
			WSClient.setData("{var_time}", "09:00:00");

			// WSClient.setData("{var_rate}", "CKRC");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
					OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
			OPERALib.setOperaHeader(OPERALib.getUserName());

			/************
			 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
			 * Code
			 *********************************/
			WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
			WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
			WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
			WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

			String profileId = CreateProfile.createProfile("DS_01");
			WSClient.setData("{var_profileId}", profileId);
			String confirmationId = CreateReservation.createReservation("DS_01").get("confirmationId");
			WSClient.setData("{var_confirmationId}", confirmationId);
			fetchAvailability(rate, rt);

			if (confirmationId != "error") {
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_27");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					WSClient.setData("{var_profileId}", WSClient.getElementValue(updateProfileResponseXML,
							"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));
					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
							false)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Profile ID " + WSClient.getElementValue(updateProfileResponseXML,
										"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));
						String query = WSClient.getQuery("QS_01");
						LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
						WSClient.setData("{var_resvId}", bookingDets.get("RESV_NAME_ID"));
						String query1 = WSClient.getQuery("QS_03");

						LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);

						String resvID = WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
						WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"), resvID, false);
						String ActlegNumber = WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", "LEGNUMBER", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO, "<b>Validating LEGNUMBER :Response</b>");
						WSAssert.assertEquals("2", ActlegNumber, false);
						query = WSClient.getQuery("QS_09");

						String ExplegNumber = WSClient.getDBRow(query).get("LEGNUMBER");

						WSClient.writeToReport(LogStatus.INFO, "<b>Validating LEGNUMBER :Database</b>");
						WSAssert.assertEquals("2", ExplegNumber, false);

					} else
						WSClient.writeToReport(LogStatus.FAIL, "Create Booking Fails");

				}
			} else

				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed: Create Reservation");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);

		} finally {
			if (resv != null)
				try {
					CancelReservation.cancelReservation("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	public void createBooking_checknull() {
		try {
			resv = null;

			String testName = "createBooking_12233";
			WSClient.startTest(testName, "Verify that no blank fields are present in the response",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_450}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_455}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				// WSClient.setData("{var_resvType}", "4PM");
				// WSClient.setData("{var_rate}", "CKRC");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					if (!WSAssert.assertIfElementExists(updateProfileResponseXML, "Profiles_Profile_Memberships",
							true)) {
						WSClient.writeToReport(LogStatus.PASS, "Memberships Elements not found");
					}

					if (!WSAssert.assertIfElementExists(updateProfileResponseXML, "Profiles_Profile_Preferences",
							true)) {
						WSClient.writeToReport(LogStatus.PASS, " Preferences Elements not found");
					}

					if (!WSAssert.assertIfElementExists(updateProfileResponseXML, "Profiles_Profile_Addresses", true)) {
						WSClient.writeToReport(LogStatus.PASS, " Address Elements not found");
					}

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

// Need to debug later  
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	public void CreateBooking_38704() {
		try {
			resv = null;
			String testName = "createBooking_38704";
			WSClient.startTest(testName,
					"Verify that the membership details are added when Always_use_memb_from_req_msg is set to N and"
							+ "1)membership details are attached to a profile profile and "
							+ "2)membership details are also passed through create booking request",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			String interfaceName = OWSLib.getChannel();

			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			//
			// WSClient.setData("{var_profileSource}", interfaceName);
			// WSClient.setData("{var_resort}", resortOperaValue);
			// WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
			WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
			WSClient.setData("{var_time}", "09:00:00");
			// WSClient.setData("{var_rate}", "CKRC");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			// Prerequisite 1 - create profile

			OPERALib.setOperaHeader(uname);
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {
				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {

					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{var_profileID}", profileID);

					if (profileID != "") {

						OPERALib.setOperaHeader(uname);
						String query;
						String member_num = WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
						WSClient.setData("{var_memNo}", member_num);

						String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}"))
								.toUpperCase();

						WSClient.setData("{var_nameOnCard}", memName);

						WSClient.setData("{var_membershipType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_membershipLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
						// WSClient.setData("{var_membershipLevel}","Platinum");
						WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_memLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));

						String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_01");
						String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);

						if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success", true)) {
							WSClient.setData("{var_membershipType}",
									OperaPropConfig.getDataSetForCode("MembershipType", "DS_03"));
							String lastname = "MEMBERSHIP";
							WSClient.setData("{var_membershipName}", lastname);
							WSClient.setData("{var_membershipLevel}",
									OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));

							WSClient.setData("{var_resort}", resort);
							WSClient.setData("{var_parameter}", "ALWAYS_USE_MEMBERSHIP_FROM_REQUEST_MESSAGE");
							WSClient.setData("{var_settingValue}", "N");
							query = WSClient.getQuery("ChangeApplicationSettings", "QS_01");
							String paramValue = WSClient.getDBRow(query).get("PARAMETER_VALUE");

							if (paramValue.equals("Y")) {
								ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
								try {
									paramValue = WSClient.getDBRow(query).get("PARAMETER_VALUE");
									
								} catch (Exception e) {
									WSClient.writeToReport(LogStatus.WARNING, "ALWAYS_USE_MEMBERSHIP_FROM_REQUEST_MESSAGE parameter not found");
								}
								
							}
							if (paramValue.equals("N")) {

								WSClient.setData("{var_resort}", resortOperaValue);
								OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
										OWSLib.getChannelType(interfaceName),
										OWSLib.getChannelCarier(resortOperaValue, interfaceName));
								fetchAvailability(rate, rt);

								String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_18");
								String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
								if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
										"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									WSClient.setData("{var_resvId}",
											WSClient.getElementValueByAttribute(updateProfileResponseXML,
													"HotelReservation_UniqueIDList_UniqueID", "RESVID",
													XMLType.RESPONSE));
									resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
									HashMap<String, String> xPath = new HashMap<String, String>();
									xPath.put("Memberships_NameMembership_membershipType",
											"Profile_Memberships_NameMembership");

									xPath.put("Memberships_NameMembership_membershipNumber",
											"Profile_Memberships_NameMembership");

									xPath.put("Memberships_NameMembership_membershipLevel",
											"Profile_Memberships_NameMembership");
									String query1 = WSClient.getQuery("QS_20");
									List<LinkedHashMap<String, String>> dbResults = WSClient.getDBRows(query1);
									WSClient.setData("{var_resvId}",
											WSClient.getElementValueByAttribute(updateProfileResponseXML,
													"HotelReservation_UniqueIDList_UniqueID", "RESVID",
													XMLType.RESPONSE));
									WSClient.writeToReport(LogStatus.INFO,
											WSClient.getElementValueByAttribute(updateProfileResponseXML,
													"Profile_ProfileIDs_UniqueID", "RESVID", XMLType.RESPONSE));
									List<LinkedHashMap<String, String>> reqValues = WSClient
											.getMultipleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);
									reqValues.add(dbResults.get(0));
									List<LinkedHashMap<String, String>> respData = WSClient.getMultipleNodeList(
											updateProfileResponseXML, xPath, false, XMLType.RESPONSE);
									ArrayList<LinkedHashMap<String, String>> dbData = WSClient
											.getDBRows(WSClient.getQuery("QS_16"));
									WSClient.writeToReport(LogStatus.INFO, "Validating request with response");
									WSAssert.assertEquals(respData, reqValues, false);
									WSClient.writeToReport(LogStatus.INFO, "Validating request with database");
									WSAssert.assertEquals(dbData, reqValues, false);

								}

							} else
								WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite failed");

						}

						else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite failed >> Membership is not created");

						}
					}

					else {

						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> profile is not created");
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {

			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

// Need to debug later
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	public void CreateBooking_3870457() {
		try {
			resv = null;

			String testName = "createBooking_3870457";
			WSClient.startTest(testName,
					"Verify that profile memberships  are added   when Always_use_memb_from_req_msg is set to N and"
							+ "1)2 memberships are attached to a profile and "
							+ "2)Membership details are added in the create booking request and"
							+ "3)Only primary membership details should be displayed on the response",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			String interfaceName = OWSLib.getChannel();
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.writeToReport(LogStatus.INFO,
					"<b>Creating 2 memberships for the user-----only primary membership details should be added to the response-----</b>");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
			WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
			WSClient.setData("{var_time}", "09:00:00");
			// WSClient.setData("{var_rate}", "CKRC");

			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {

					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{var_profileID}", profileID);

					if (profileID != "") {

						OPERALib.setOperaHeader(uname);
						String query;
						String member_num = WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
						WSClient.setData("{var_memNo}", member_num);

						String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}"))
								.toUpperCase();

						WSClient.setData("{var_nameOnCard}", memName);

						WSClient.setData("{var_membershipType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_membershipLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
						// WSClient.setData("{var_membershipLevel}","Platinum");
						WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_memLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));

						String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_01");
						String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);

						WSClient.setData("{var_membershipType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_03"));
						WSClient.setData("{var_membershipLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));
						// WSClient.setData("{var_membershipLevel}","Platinum");
						WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_03"));
						WSClient.setData("{var_memLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));

						createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_01");
						createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);

						if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success", true)) {
							WSClient.setData("{var_membershipType}",
									OperaPropConfig.getDataSetForCode("MembershipType", "DS_03"));
							String lastname = "MEMBERSHIP";
							WSClient.setData("{var_membershipName}", lastname);
							WSClient.setData("{var_membershipLevel}",
									OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));

							WSClient.setData("{var_resort}", chain);
							WSClient.setData("{var_parameter}", "ALWAYS_USE_MEMBERSHIP_FROM_REQUEST_MESSAGE");
							WSClient.setData("{var_settingValue}", "N");
							query = WSClient.getQuery("ChangeApplicationSettings", "QS_01");
							String paramValue = WSClient.getDBRow(query).get("PARAMETER_VALUE");

							if (paramValue.equals("Y"))
								ChangeApplicationParameters.changeApplicationParameter("DS_01", "1");

							if (paramValue.equals("N")) {

								WSClient.setData("{var_resort}", resortOperaValue);
								OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
										OWSLib.getChannelType(interfaceName),
										OWSLib.getChannelCarier(resortOperaValue, interfaceName));
								fetchAvailability(rate, rt);

								String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_18");
								String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
								if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
										"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									WSClient.setData("{var_resvId}",
											WSClient.getElementValueByAttribute(updateProfileResponseXML,
													"HotelReservation_UniqueIDList_UniqueID", "RESVID",
													XMLType.RESPONSE));
									resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
									HashMap<String, String> xPath = new HashMap<String, String>();
									xPath.put("Memberships_NameMembership_membershipType",
											"Profile_Memberships_NameMembership");

									xPath.put("Memberships_NameMembership_membershipNumber",
											"Profile_Memberships_NameMembership");

									xPath.put("Memberships_NameMembership_membershipLevel",
											"Profile_Memberships_NameMembership");
									WSClient.setData("{var_resvId}",
											WSClient.getElementValueByAttribute(updateProfileResponseXML,
													"HotelReservation_UniqueIDList_UniqueID", "RESVID",
													XMLType.RESPONSE));
									WSClient.writeToReport(LogStatus.INFO,
											WSClient.getElementValueByAttribute(updateProfileResponseXML,
													"Profile_ProfileIDs_UniqueID", "RESVID", XMLType.RESPONSE));
									List<LinkedHashMap<String, String>> respData = WSClient.getMultipleNodeList(
											updateProfileResponseXML, xPath, false, XMLType.RESPONSE);
									String query1 = WSClient.getQuery("QS_20");
									List<LinkedHashMap<String, String>> dbResults = WSClient.getDBRows(query1);
									WSClient.writeToReport(LogStatus.INFO,
											WSClient.getElementValueByAttribute(updateProfileResponseXML,
													"HotelReservation_UniqueIDList_UniqueID", "RESVID",
													XMLType.RESPONSE));
									List<LinkedHashMap<String, String>> reqValues = WSClient
											.getMultipleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);
									ArrayList<LinkedHashMap<String, String>> dbData = WSClient
											.getDBRows(WSClient.getQuery("QS_16"));
									reqValues.add(dbResults.get(0));
									WSClient.writeToReport(LogStatus.INFO, "Validating response with request");
									WSAssert.assertEquals(respData, reqValues, false);
									WSClient.writeToReport(LogStatus.INFO, "Validating database with request");
									WSAssert.assertEquals(dbData, reqValues, false);

								}

							} else
								WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite failed");

						}

						else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite failed >> Membership is not created");

						}
					}

					else {

						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> profile is not created");
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			if (resv != null)
				try {
					CancelReservation.cancelReservation("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}

// Need to debug later
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	public void CreateBooking_387045() {
		try {
			resv = null;

			String testName = "createBooking_387045";
			WSClient.startTest(testName,
					"Verify that  membership details are added when ALWAYS_USE_MEMB_FROM_REQ_MSG is set to N and"
							+ "1) membership details are attached to a profile and"
							+ "2)membreship details are not added in the create booking request ",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			String interfaceName = OWSLib.getChannel();

			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			// WSClient.setData("{var_rate}", "CKRC");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
			WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
			WSClient.setData("{var_time}", "09:00:00");

			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_06");
				if (!profileID.equals("error")) {

					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{var_profileID}", profileID);

					if (profileID != "") {

						OPERALib.setOperaHeader(uname);
						String query;
						String member_num = WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
						WSClient.setData("{var_memNo}", member_num);

						String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}"))
								.toUpperCase();

						WSClient.setData("{var_nameOnCard}", memName);

						WSClient.setData("{var_membershipType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_membershipLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
						// WSClient.setData("{var_membershipLevel}","Platinum");
						WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_memLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));

						String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_01");
						String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);

						WSClient.setData("{var_membershipType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_03"));
						WSClient.setData("{var_membershipLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));
						// WSClient.setData("{var_membershipLevel}","Platinum");
						WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_03"));
						WSClient.setData("{var_memLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));

						createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_01");
						createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);

						if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success", true)) {
							WSClient.setData("{var_membershipType}",
									OperaPropConfig.getDataSetForCode("MembershipType", "DS_03"));
							String lastname = "MEMBERSHIP";
							WSClient.setData("{var_membershipName}", lastname);
							WSClient.setData("{var_membershipLevel}",
									OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));

							WSClient.setData("{var_resort}", chain);
							WSClient.setData("{var_parameter}", "ALWAYS_USE_MEMBERSHIP_FROM_REQUEST_MESSAGE");
							WSClient.setData("{var_settingValue}", "N");
							query = WSClient.getQuery("ChangeApplicationSettings", "QS_01");
							String paramValue = WSClient.getDBRow(query).get("PARAMETER_VALUE");

							if (paramValue.equals("Y"))
								ChangeApplicationParameters.changeApplicationParameter("DS_01", "1");

							if (paramValue.equals("N")) {

								WSClient.setData("{var_resort}", resortOperaValue);
								OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
										OWSLib.getChannelType(interfaceName),
										OWSLib.getChannelCarier(resortOperaValue, interfaceName));
								fetchAvailability(rate, rt);

								String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_18");
								String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
								if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
										"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									WSClient.setData("{var_resvId}",
											WSClient.getElementValueByAttribute(updateProfileResponseXML,
													"HotelReservation_UniqueIDList_UniqueID", "RESVID",
													XMLType.RESPONSE));
									resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
									HashMap<String, String> xPath = new HashMap<String, String>();
									xPath.put("Memberships_NameMembership_membershipType",
											"Profile_Memberships_NameMembership");

									xPath.put("Memberships_NameMembership_membershipNumber",
											"Profile_Memberships_NameMembership");

									xPath.put("Memberships_NameMembership_membershipLevel",
											"Profile_Memberships_NameMembership");
									String query1 = WSClient.getQuery("QS_20");
									List<LinkedHashMap<String, String>> dbResults = WSClient.getDBRows(query1);
									WSClient.setData("{var_resvId}",
											WSClient.getElementValueByAttribute(updateProfileResponseXML,
													"HotelReservation_UniqueIDList_UniqueID", "RESVID",
													XMLType.RESPONSE));
									WSClient.writeToReport(LogStatus.INFO,
											WSClient.getElementValueByAttribute(updateProfileResponseXML,
													"Profile_ProfileIDs_UniqueID", "RESVID", XMLType.RESPONSE));
									List<LinkedHashMap<String, String>> reqValues = WSClient
											.getMultipleNodeList(updateProfileReq, xPath, false, XMLType.REQUEST);
									List<LinkedHashMap<String, String>> respData = WSClient.getMultipleNodeList(
											updateProfileResponseXML, xPath, false, XMLType.RESPONSE);
									reqValues.add(dbResults.get(0));
									ArrayList<LinkedHashMap<String, String>> dbData = WSClient
											.getDBRows(WSClient.getQuery("QS_16"));
									WSClient.writeToReport(LogStatus.INFO, "Validating request with response");
									WSAssert.assertEquals(respData, reqValues, false);
									WSClient.writeToReport(LogStatus.INFO, "Validating request with database");
									WSAssert.assertEquals(dbData, reqValues, false);

								}

							} else
								WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite failed");

						}

						else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite failed >> Membership is not created");

						}
					}

					else {

						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> profile is not created");
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	public void CreateBooking_387023() {
		try {
			resv = null;

			String testName = "createBooking_383223";
			WSClient.startTest(testName,
					"Verify that the membership details that are attached to a profile are not populated on the Create Booking response when 'ALWAYS_USE_MEMB_FROM_REQ_MSG' is set to Y  ",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			String interfaceName = OWSLib.getChannel();
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			// WSClient.setData("{var_resvType}", "4PM");
			// WSClient.setData("{var_rate}", "CKRC");
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
			WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
			WSClient.setData("{var_time}", "09:00:00");

			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "MembershipType", "MembershipLevel" })) {
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {

					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{var_profileID}", profileID);

					if (profileID != "") {

						OPERALib.setOperaHeader(uname);
						String query;
						String member_num = WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
						WSClient.setData("{var_memNo}", member_num);

						String memName = (WSClient.getData("{var_fname}") + "_" + WSClient.getData("{var_lname}"))
								.toUpperCase();

						WSClient.setData("{var_nameOnCard}", memName);

						WSClient.setData("{var_membershipType}",
								OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_membershipLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
						// WSClient.setData("{var_membershipLevel}","Platinum");
						WSClient.setData("{var_memType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
						WSClient.setData("{var_memLevel}",
								OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));

						String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_01");
						String createMembershipRes = WSClient.processSOAPMessage(createMembershipReq);

						if (WSAssert.assertIfElementExists(createMembershipRes, "CreateMembershipRS_Success", true)) {

							WSClient.setData("{var_resort}", chain);
							WSClient.setData("{var_parameter}", "ALWAYS_USE_MEMBERSHIP_FROM_REQUEST_MESSAGE");
							WSClient.setData("{var_settingValue}", "Y");
							query = WSClient.getQuery("ChangeApplicationSettings", "QS_01");
							String paramValue = WSClient.getDBRow(query).get("PARAMETER_VALUE");
							WSClient.setData("{var_resort}", resortOperaValue);
							if (paramValue.equals("N"))
								ChangeApplicationParameters.changeApplicationParameter("DS_01", "1");
							if (paramValue.equals("Y")) {
								OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
										OWSLib.getChannelType(interfaceName),
										OWSLib.getChannelCarier(resortOperaValue, interfaceName));
								fetchAvailability(rate, rt);

								String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
								String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
								if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
										"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									WSClient.setData("{var_resvId}",
											WSClient.getElementValueByAttribute(updateProfileResponseXML,
													"HotelReservation_UniqueIDList_UniqueID", "RESVID",
													XMLType.RESPONSE));
									resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
									WSClient.setData("{var_resvId}",
											WSClient.getElementValueByAttribute(updateProfileResponseXML,
													"HotelReservation_UniqueIDList_UniqueID", "RESVID",
													XMLType.RESPONSE));

									WSClient.writeToReport(LogStatus.INFO, "<b>Checking with the database</b>");
									if (WSClient.getDBRow(WSClient.getQuery("QS_18")).get("COUNT").equals("0")) {
										WSClient.writeToReport(LogStatus.PASS,
												"Membership is not populated when membership details are not populated in create Booking");

									}
									WSClient.writeToReport(LogStatus.INFO, "<b>Checking with the response</b>");
									if (!WSAssert.assertIfElementExists(updateProfileResponseXML,
											"Profiles_Profile_Memberships", true)) {
										WSClient.writeToReport(LogStatus.PASS, "<b>Memberships Elements not found</b>");
									}

								}
							} else
								WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite failed");

						} else
							WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite failed");

					}

					else {
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Membership is not created");

					}
				}

				else {

					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> profile is not created");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}


	
//	Need to debug Later
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	/****
	 * Verify that the package posting details for posting rhythm as
	 * "EveryNight" are populated correctly for one day reservation.
	 * Prerequisites : Profile is created -> Channel Allowed rate code should be
	 * present. ->Source Code,Market code should be present -> Package code with
	 * inclusive tax details and Posting rhythm as "EveryNight"
	 ***/
	public void createBooking_383952() throws Exception {

		try {
			String testName = "createBooking_38395442";
			WSClient.startTest(testName,
					"Verify that the package posting details for posting rhythm as every night are populated correctly for one day reservation and tax is included to the package.",
					"minimumRegression");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			String channel = OWSLib.getChannel();
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			/*************
			 * Prerequisite 1 : Room type, Rate Plan Code, PackageCode
			 *********************************/

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PackageCode" })) {

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				String pkg = OperaPropConfig.getDataSetForCode("PackageCode", "DS_13");
				// String pkg="QA_CHOCOLATE1";
				WSClient.setData("{var_pkg}", pkg);
				OPERALib.setOperaHeader(OPERALib.getUserName());

				/******
				 * Prerequisite 2 : Creating a Profile with basic details
				 *****/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");
					WSClient.setData("{var_profileSource}", interfaceName);
					WSClient.setData("{var_resort}", resortOperaValue);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}"));
					WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_3}"));
					WSClient.setData("{var_time}", "09:00:00");
					String rate1 = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_10");
					WSClient.setData("{var_rate}", rate1);
					String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_07");
					WSClient.setData("{var_roomType}", rt);
					String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
					WSClient.setData("{var_resvType}", resvt);

					/************ OWS Create Booking ***********/
					fetchAvailability(rate1, rt);

					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
							OWSLib.getChannelType(interfaceName),
							OWSLib.getChannelCarier(resortOperaValue, interfaceName));

					String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_08");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
					if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						resvId = WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvId + "</b>");
						WSClient.setData("{var_resvId}", resvId);
						WSClient.setData("{var_rateCode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_13"));

						String packAmount = WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"RoomRateAndPackages_Charges_Amount", "RoomRateAndPackages_Charges_Code", pkg,
								XMLType.RESPONSE);
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
								"TaxesAndFees_Charges_Amount", "TaxesAndFees_Charges_CodeType", "R", XMLType.RESPONSE);
						String taxDesc = WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"TaxesAndFees_Charges_Description", "TaxesAndFees_Charges_CodeType", "R",
								XMLType.RESPONSE);
						String packageTax = WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"TaxesAndFees_Charges_Amount", "TaxesAndFees_Charges_CodeType", "P", XMLType.RESPONSE);
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
						Double totalA = Double.parseDouble(price) + Double.parseDouble(rate);
						String total = totalA.toString();
						System.out.println(total);

						query = WSClient.getQuery("OWSCreateBooking", "QS_13");
						String percent = WSClient.getDBRow(query).get("PERCENTAGE");
						System.out.println(percent);
						query = WSClient.getQuery("OWSCreateBooking", "QS_11");
						String desc = WSClient.getDBRow(query).get("DES");
						System.out.println(desc);
						query = WSClient.getQuery("OWSCreateBooking", "QS_14");
						String percent1 = WSClient.getDBRow(query).get("PERCENTAGE");
						System.out.println(percent1);

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

						/***************** Validations ****************/

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

					} else

					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

						/****
						 * Verifying that the error message is populated on the
						 * response
						 ********/

						String message = WSAssert.getElementValue(updateProfileResponseXML, "Result_Text_TextElement",
								XMLType.RESPONSE);
						if (message != "")
							WSClient.writeToReport(LogStatus.INFO,
									"The error populated on the response is : <b>" + message + "</b>");
					}

					if (WSAssert.assertIfElementExists(updateProfileResponseXML,
							"CreateBookingResponse_Result_GDSError", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSAssert.getElementValue(updateProfileResponseXML,
								"CreateBookingResponse_Result_GDSError", XMLType.RESPONSE);
						if (message != "")
							WSClient.writeToReport(LogStatus.INFO,
									"The error populated on the response is : <b>" + message + "</b>");
					} else if (WSAssert.assertIfElementExists(updateProfileResponseXML,
							"CreateBookingResponse_Result_OperaErrorCode", true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSAssert.getElementValue(updateProfileResponseXML,
								"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						if (message != "")
							WSClient.writeToReport(LogStatus.INFO,
									"The error populated on the response is : <b>" + message + "</b>");
					}

					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_faultcode",
							true)) {

						/****
						 * Verifying whether the error Message is populated on
						 * the response
						 ****/

						String message = WSClient.getElementValue(updateProfileResponseXML,
								"CreateBookingResponse_faultstring", XMLType.RESPONSE);
						if (message != "")
							WSClient.writeToReport(LogStatus.INFO,
									"The error populated on the response is : <b>" + message + "</b>");
					}

				}
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked ----> Property Config Data not available!");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvId.equals("error")) {
				WSClient.setData("{var_resvId}", resvId);
				CancelReservation.cancelReservation("DS_02");

			}
		}
	}

	
	
//Need to debug later 	
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	/****
	 * Verify that the package posting details for posting rhythm as
	 * "EveryNight" are populated correctly for 3 day reservation. Prerequisites
	 * : Profile is created -> Channel Allowed rate code should be present.
	 * ->Source Code,Market code should be present -> Package code with no tax
	 * details and Posting rhythm as "EveryNight"
	 ***/
	public void createBooking_383951() throws Exception {

		try {
			String testName = "createBooking_38395144";
			WSClient.startTest(testName,
					"Verify that the package posting details for posting rhythm as every night are populated correctly for multi night reservation.",
					"minimumRegression");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			String channel = OWSLib.getChannel();
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);

			/*************
			 * Prerequisite 1 : Room type, Rate Plan Code, PackageCode
			 *********************************/

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PackageCode" })) {

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				String pkg = OperaPropConfig.getDataSetForCode("PackageCode", "DS_05");
				WSClient.setData("{var_pkg}", pkg);
				OPERALib.setOperaHeader(OPERALib.getUserName());
				String rate1 = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_10");
				WSClient.setData("{var_rate}", rate1);
				String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_07");
				WSClient.setData("{var_roomType}", rt);
				String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
				WSClient.setData("{var_resvType}", resvt);
				WSClient.setData("{var_rateCode}", rate1);

				/******
				 * Prerequisite 2 : Creating a Profile with basic details
				 *****/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b> Profile ID : " + profileID + "</b>");
					WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}"));
					WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_5}"));
					WSClient.setData("{var_time}", "09:00:00");
					WSClient.setData("{var_rateCode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_13"));

					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
							OWSLib.getChannelType(interfaceName),
							OWSLib.getChannelCarier(resortOperaValue, interfaceName));

					/************ OWS Create Booking ***********/
					fetchAvailability(rate1, rt);

					String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_08");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);

					if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						resvId = WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvId + "</b>");

						WSClient.setData("{var_resvId}", resvId);

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
						db1.put("RoomRateAndPackages_Charges_Amount", "RoomStay_ExpectedCharges_ChargesForPostingDate");
						db1.put("RoomRateAndPackages_Charges_Amount_2",
								"RoomStay_ExpectedCharges_ChargesForPostingDate");
						db1.put("RoomRateAndPackages_Charges_Code", "RoomStay_ExpectedCharges_ChargesForPostingDate");
						db1.put("TaxesAndFees_Charges_Amount", "RoomStay_ExpectedCharges_ChargesForPostingDate");
						db1.put("Packages_Package_TaxAmount_3", "RoomStay_ExpectedCharges_ChargesForPostingDate");
						db1.put("TaxesAndFees_Charges_Description", "RoomStay_ExpectedCharges_ChargesForPostingDate");
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
						Double totalA = Double.parseDouble(price) + Double.parseDouble(rate);
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

							/************ Validation ***************/

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
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							if (message != "")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateProfileResponseXML,
								"CreateBookingResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateProfileResponseXML,
									"CreateBookingResponse_Result_GDSError", XMLType.RESPONSE);
							if (message != "")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						} else if (WSAssert.assertIfElementExists(updateProfileResponseXML,
								"CreateBookingResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateProfileResponseXML,
									"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message != "")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_faultcode",
								true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSClient.getElementValue(updateProfileResponseXML,
									"CreateBookingResponse_faultstring", XMLType.RESPONSE);
							if (message != "")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}
					}

				}
			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked ----> Property Config Data not available!");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvId.equals("error")) {
				WSClient.setData("{var_resvId}", resvId);
				CancelReservation.cancelReservation("DS_02");

			}
		}
	}


	
//	Need to debug Later
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	/****
	 * Verify that the package posting details for posting rhythm as
	 * "ArrivalNight" are populated correctly for one day reservation.
	 * Prerequisites : Profile is created -> Channel Allowed rate code should be
	 * present. ->Source Code,Market code should be present -> Package code with
	 * no tax details and Posting rhythm as "ArrivalNight"
	 ***/
	public void createBooking_383953() throws Exception {

		try {
			String testName = "createBooking_3083953";
			WSClient.startTest(testName,
					"Verify that the package posting details for posting rhythm as arrival night are populated correctly for one day reservation.",
					"minimumRegression");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			String channel = OWSLib.getChannel();
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);

			/*************
			 * Prerequisite 1 : Room type, Rate Plan Code, PackageCode
			 *********************************/

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "PackageCode" })) {

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				String pkg = OperaPropConfig.getDataSetForCode("PackageCode", "DS_07");
				WSClient.setData("{var_pkg}", pkg);
				OPERALib.setOperaHeader(OPERALib.getUserName());

				/******
				 * Prerequisite 2 : Creating a Profile with basic details
				 *****/

				profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);

					WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}"));
					WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_3}"));
					WSClient.setData("{var_time}", "09:00:00");
					String rate1 = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_10");
					WSClient.setData("{var_rateCode}", rate1);
					WSClient.setData("{var_rate}", rate1);
					String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_07");
					WSClient.setData("{var_roomType}", rt);
					String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
					WSClient.setData("{var_resvType}", resvt);

					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
							OWSLib.getChannelType(interfaceName),
							OWSLib.getChannelCarier(resortOperaValue, interfaceName));

					/************ OWS Create Booking ***********/
					fetchAvailability(rate1, rt);

					String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_08");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
					if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

						resvId = WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b> Reservation ID : " + resvId + "</b>");
						WSClient.setData("{var_resvId}", resvId);
						WSClient.setData("{var_rateCode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_13"));

						String packAmount = WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"RoomRateAndPackages_Charges_Amount", "RoomRateAndPackages_Charges_Code", pkg,
								XMLType.RESPONSE);
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
								"TaxesAndFees_Charges_Amount", "TaxesAndFees_Charges_CodeType", "R", XMLType.RESPONSE);
						String taxDesc = WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"TaxesAndFees_Charges_Description", "TaxesAndFees_Charges_CodeType", "R",
								XMLType.RESPONSE);
						String packageTax = WSClient.getElementValueByAttribute(updateProfileResponseXML,
								"TaxesAndFees_Charges_Amount", "TaxesAndFees_Charges_CodeType", "P", XMLType.RESPONSE);
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
						Double totalA = Double.parseDouble(price) + Double.parseDouble(rate);
						String total = totalA.toString();
						System.out.println(total);

						query = WSClient.getQuery("OWSCreateBooking", "QS_13");
						String percent = WSClient.getDBRow(query).get("PERCENTAGE");
						System.out.println(percent);
						query = WSClient.getQuery("OWSCreateBooking", "QS_11");
						String desc = WSClient.getDBRow(query).get("DES");
						System.out.println(desc);
						query = WSClient.getQuery("OWSCreateBooking", "QS_14");
						String percent1 = WSClient.getDBRow(query).get("PERCENTAGE");
						System.out.println(percent1);

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

						/***************** Validations ****************/

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

					} else {
						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated on
							 * the response
							 ********/

							String message = WSAssert.getElementValue(updateProfileResponseXML,
									"Result_Text_TextElement", XMLType.RESPONSE);
							if (message != "")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateProfileResponseXML,
								"CreateBookingResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateProfileResponseXML,
									"CreateBookingResponse_Result_GDSError", XMLType.RESPONSE);
							if (message != "")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						} else if (WSAssert.assertIfElementExists(updateProfileResponseXML,
								"CreateBookingResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSAssert.getElementValue(updateProfileResponseXML,
									"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							if (message != "")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}

						if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_faultcode",
								true)) {

							/****
							 * Verifying whether the error Message is populated
							 * on the response
							 ****/

							String message = WSClient.getElementValue(updateProfileResponseXML,
									"CreateBookingResponse_faultstring", XMLType.RESPONSE);
							if (message != "")
								WSClient.writeToReport(LogStatus.INFO,
										"The error populated on the response is : <b>" + message + "</b>");
						}
					}

				}

			} else
				WSClient.writeToReport(LogStatus.WARNING, "Blocked ----> Property Config Data not available!");
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {

			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!resvId.equals("error")) {
				WSClient.setData("{var_resvId}", resvId);
				CancelReservation.cancelReservation("DS_02");

			}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_invalidProfile() {
		try {

			resv = null;

			String testName = "createBooking_381115";
			WSClient.startTest(testName, "Verify that booking is not created using an invalid profile ID",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String pkg = OperaPropConfig.getDataSetForCode("PackageCode", "DS_01");
			WSClient.setData("{var_pkg}", pkg);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);

			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true) == false) {
				WSClient.writeToReport(LogStatus.WARNING, "Prerequisite blocked --->Create Profile fails");
			} else {
				WSClient.writeToReport(LogStatus.INFO, "<b>Assigning an invalid profile</b>");
				LinkedHashMap<String, String> profileDetails = WSClient
						.getDBRow(WSClient.getQuery("OWSInsertUpdateDocument", "QS_07"));

				String profileID = profileDetails.get("PROFILEID");
				WSClient.writeToReport(LogStatus.INFO, "Testing with Profile Id : " + profileID);
				if (!profileID.equals("error")) {

					WSClient.setData("{var_profileId}", profileID);

					WSClient.setData("{var_profileSource}", interfaceName);
					WSClient.setData("{var_resort}", resortOperaValue);
					WSClient.setData("{var_extResort}", resortExtValue);
					WSClient.setData("{var_busdate}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
					WSClient.setData("{var_busdate1}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
					WSClient.setData("{var_time}", "09:00:00");
					WSClient.writeToReport(LogStatus.INFO, "<b>Create Booking with an invalid profile ID</b>");

					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
							OWSLib.getChannelType(interfaceName),
							OWSLib.getChannelCarier(resortOperaValue, interfaceName));
					fetchAvailability(rate, rt);

					String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_08");
					String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML,
							"CreateBookingResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
							if (WSAssert.assertIfElementExists(updateProfileResponseXML,
									"CreateBookingResponse_Result_OperaErrorCode", false)) {
								if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Result_Text_TextElement",
										false)) {
									WSClient.writeToReport(LogStatus.PASS,
											"<b>Create Booking Response gives the error message-----></b> "
													+ WSClient.getElementValue(updateProfileResponseXML,
															"Result_Text_TextElement", XMLType.RESPONSE));

								}

							}
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Create Booking Response does not give an error message when an invalid profile ID is attached");
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Create Booking fails");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}


// Need to Debug later
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	public void createBooking_38323_defaultResvType() {
		try {
			resv = null;

			String testName = "createBooking_38323";
			WSClient.startTest(testName,
					"Verify that the default reservation type is fetched from the parameter RESERVATION_TYPE, when not passed on the request",
					"minimumRegression");
			String resortOperaValue = OPERALib.getResort();
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			String interfaceName = OWSLib.getChannel();

			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
			WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
			WSClient.setData("{var_time}", "09:00:00");
			// WSClient.setData("{var_rate}", "CKRC");

			// Prerequisite 1 - create profile
			OPERALib.setOperaHeader(uname);
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "ReservationType" })) {

				profileID = CreateProfile.createProfile("DS_00");
				if (!profileID.equals("error")) {

					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{var_profileID}", profileID);

					if (profileID != "") {

						// WSClient.setData("{var_resort}", "IDC7PROP");
						WSClient.setData("{var_parameter}", "RESERVATION_TYPE");
						String resvType = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
						WSClient.setData("{var_settingValue}", resvType);
						WSClient.writeToReport(LogStatus.INFO, "<b>Retreiving the value of RESERVATION_TYPE</b>");

						String query = WSClient.getQuery("ChangeApplicationSettings", "QS_01");
						String paramValue = WSClient.getDBRow(query).get("PARAMETER_VALUE");
						System.out.println(paramValue);
						if (paramValue != "error") {
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Value is assigned to RESERVATION_TYPE as it is not previously assigned</b>");

							ChangeApplicationParameters.changeApplicationParameter("DS_01", "1");
						}

						WSClient.setData("{var_resort}", resortOperaValue);
						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));
						fetchAvailability(rate, rt);

						String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_46");
						String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							WSClient.setData("{var_resvId}",
									WSClient.getElementValueByAttribute(updateProfileResponseXML,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
							resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							String elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
							String elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
							WSClient.setData("{var_resvId}",
									WSClient.getElementValueByAttribute(updateProfileResponseXML,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
							WSClient.setData(resvId, WSClient.getElementValueByAttribute(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
									profileID, false);
							elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);

							elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);

							WSClient.setData("{var_settingValue}", paramValue);
							query = WSClient.getQuery("ChangeApplicationSettings", "QS_01");
							paramValue = WSClient.getDBRow(query).get("PARAMETER_VALUE");
							System.out.println(paramValue);

							ChangeApplicationParameters.changeApplicationParameter("DS_01", "1");

						} else {
							resvId = "error";

							WSClient.writeToReport(LogStatus.FAIL,
									"Create Booking failed with this error message-->"
											+ WSClient.getElementValue(updateProfileResponseXML,
													"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE));
						}
					}

				} else
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed");

			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_383112() {
		try {
			resv = null;

			String testName = "createBooking_381115";
			WSClient.startTest(testName,
					"Verify that booking is not created using rate code and room type without any rooms",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			/*****
			 *
			 * Add config
			 */
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_07");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_06");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_11");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			// WSClient.setData("{var_rate}", "SUITE18");
			// WSClient.setData("{var_roomType}", "CH_QTBED");
			// WSClient.setData("{var_resvType}", "QA_4PM");
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//

			profileID = CreateProfile.createProfile("DS_01");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				WSClient.writeToReport(LogStatus.INFO, "<b>Create Booking with room type without rooms</b>");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				// fetchAvailability(rate,rt);

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(updateProfileResponseXML,
							"CreateBookingResponse_Result_OperaErrorCode", false)) {
						WSClient.writeToReport(LogStatus.PASS,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}

				} else
					WSClient.writeToReport(LogStatus.FAIL,
							"Create Booking Response does not give an error message when room type without rooms are passed");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_383113() {
		try {
			resv = null;

			String testName = "createBooking_381115";
			WSClient.startTest(testName, "Verify that booking is not created  without rate code ", "minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			/*****
			 *
			 * Add config
			 */
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			// WSClient.setData("{var_roomType}", "CH_QTBED");
			// WSClient.setData("{var_resvType}", "QA_4PM");
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//

			profileID = CreateProfile.createProfile("DS_01");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				WSClient.writeToReport(LogStatus.INFO, "<b>Create Booking with room type without rooms</b>");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_33");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(updateProfileResponseXML,
							"CreateBookingResponse_Result_OperaErrorCode", false)) {
						WSClient.writeToReport(LogStatus.PASS,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}

				} else
					WSClient.writeToReport(LogStatus.FAIL,
							"Create Booking Response does not give an error message when room type without rooms are passed");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_383114() {
		try {
			resv = null;

			String testName = "createBooking_381115";
			WSClient.startTest(testName, "Verify that booking is not created without resort ", "minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			/*****
			 * Config
			 */
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			// WSClient.setData("{var_rate}", "CKRC");
			// WSClient.setData("{var_resvType}", "QA_4PM");
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//

			profileID = CreateProfile.createProfile("DS_01");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				WSClient.writeToReport(LogStatus.INFO, "<b>Create Booking with room type without resort</b>");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_34");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(updateProfileResponseXML,
							"CreateBookingResponse_Result_OperaErrorCode", true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}

				} else
					WSClient.writeToReport(LogStatus.FAIL,
							"Create Booking Response does not give an error message when resort is not passed");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	
//  Need to debug later	
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	public void createBooking_23916() {
		resv = null;

		String paramvalue = "";
		String parameter = "";
		String resvId = null;
		try {
			String testName = "createBooking_23916";
			WSClient.startTest(testName, "Verify that booking is created successfully when given multi rates",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

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
			WSClient.writeToReport(LogStatus.INFO, "Verify if the parameter ACCEPT_LOWER_RATES is enabled");

			String query = WSClient.getQuery("ChangeChannelParameters", "QS_01");
			LinkedHashMap results = WSClient.getDBRow(query);

			/****
			 * ACCEPT_LOWER_RATES parameter has to set to accept lower rates
			 * than base rate
			 ****/
			paramvalue = (String) results.get("ACCEPT_LOWER_RATES_YN");
			parameter = paramvalue;
			if (!WSAssert.assertEquals("Y", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Enabling the parameter ACCEPT_LOWER_RATES");

				String changeChannelParametersReq = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
				String changeChannelParametersRes = WSClient.processSOAPMessage(changeChannelParametersReq);
				if (WSAssert.assertIfElementExists(changeChannelParametersRes, "ChangeChannelParametersRS_Success",
						true)) {
					results = WSClient.getDBRow(query);
					parameter = (String) results.get("ACCEPT_LOWER_RATES_YN");
				}
			}

			if (parameter.equals("Y")) {
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {

					/*************
					 * Prerequisite : Room type, Rate Plan Code,ReservationType
					 *********************************/

					// String rateCode = "CKRC";//
					// OperaPropConfig.getDataSetForCode("RateCode",
					// // "DS_01"));
					// String roomType = "4RT";//
					// OperaPropConfig.getDataSetForCode("RoomType",
					// // "DS_02"));
					String rateCode = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05");
					WSClient.setData("{var_rate}", rateCode);
					String roomType = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04");
					WSClient.setData("{var_roomType}", roomType);

					String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
					WSClient.setData("{var_reservationType}", resvt);

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

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						/***************
						 * OWS Create Booking Operation
						 ********************/
						fetchAvailability(rateCode, roomType);

						String CreateBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_29");
						String CreateBookingRes = WSClient.processSOAPMessage(CreateBookingReq);

						if (WSAssert.assertIfElementExists(CreateBookingRes, "CreateBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(CreateBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
								resvId = WSClient.getElementValueByAttribute(CreateBookingRes,
										"HotelReservation_UniqueIDList_UniqueID",
										"HotelReservation_UniqueIDList_UniqueID_source", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}", resvId);

								WSClient.setData("{var_rateCode}",
										OperaPropConfig.getDataSetForCode("RateCode", "DS_13"));
								String roomStart = WSClient.getElementValue(CreateBookingReq,
										"RoomRate_Rates_Rate_effectiveDate", XMLType.REQUEST);
								String roomStart1 = WSClient.getElementValue(CreateBookingReq,
										"RoomRate_Rates_Rate_effectiveDate[2]", XMLType.REQUEST);
								String base = WSClient.getElementValue(CreateBookingReq, "Rates_Rate_Base",
										XMLType.REQUEST);
								String base1 = WSClient.getElementValue(CreateBookingReq, "Rates_Rate_Base[2]",
										XMLType.REQUEST);
								System.out.println(roomStart);
								System.out.println(roomStart1);
								System.out.println(base);
								System.out.println(base1);

								LinkedHashMap<String, String> xpath = new LinkedHashMap<String, String>();
								xpath.put("RoomStay_RoomRates_RoomRate_ratePlanCode", "RoomStay_RoomRates_RoomRate");
								xpath.put("RoomRate_Rates_Rate_effectiveDate", "RoomStay_RoomRates_RoomRate");
								xpath.put("Rates_Rate_Base", "RoomStay_RoomRates_RoomRate");
								xpath.put("RoomStay_RoomRates_RoomRate_roomTypeCode", "RoomStay_RoomRates_RoomRate");
								List<LinkedHashMap<String, String>> db = WSClient.getMultipleNodeList(CreateBookingRes,
										xpath, false, XMLType.RESPONSE);
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
											WSClient.writeToReport(LogStatus.PASS, "Rate Base Amount -> Expected    "
													+ base + " ,      Actual   : " + values.get("RatesRateBase1"));

										} else {
											WSClient.writeToReport(LogStatus.FAIL, "Rate Base Amount -> Expected    "
													+ base + " ,      Actual    " + values.get("RatesRateBase1"));

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
											WSClient.writeToReport(LogStatus.PASS, "Rate Base Amount -> Expected    "
													+ base1 + " ,      Actual   : " + values.get("RatesRateBase1"));

										} else {
											WSClient.writeToReport(LogStatus.FAIL, "Rate Base Amount -> Expected    "
													+ base1 + " ,      Actual    " + values.get("RatesRateBase1"));

										}

									}
								}

								String totalCharge = WSClient.getElementValue(CreateBookingRes,
										"RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages",
										XMLType.RESPONSE);
								String totalTaxAndCharge = WSClient.getElementValue(CreateBookingRes,
										"RoomStays_RoomStay_Total", XMLType.RESPONSE);
								String totalTax = WSClient.getElementValue(CreateBookingRes,
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
								db = WSClient.getMultipleNodeList(CreateBookingRes, db1, false, XMLType.RESPONSE);
								System.out.println(db);

								query = WSClient.getQuery("OWSCreateBooking", "QS_13");
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

								/***** Validation of expected Charges *********/

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
												"<b>Validating for the date : " + startDate + "</b>");

										if (WSAssert.assertEquals(base, rateAmount, true)) {
											WSClient.writeToReport(LogStatus.PASS,
													"Room Rate : Expected -> " + base + " Actual -> " + rateAmount);
										} else
											WSClient.writeToReport(LogStatus.FAIL,
													"Room Rate : Expected -> " + base + " Actual -> " + rateAmount);

										if (WSAssert.assertEquals(base, totalAmount, true)) {
											WSClient.writeToReport(LogStatus.PASS,
													" Package and Room Charges : Expected -> " + base + " Actual -> "
															+ totalAmount);
										} else
											WSClient.writeToReport(LogStatus.FAIL,
													" Package and Room Charges : Expected -> " + base + " Actual -> "
															+ totalAmount);

										if (WSAssert.assertEquals(tax, roomTax, true)) {

											WSClient.writeToReport(LogStatus.PASS,
													"Room Tax : Expected ->" + tax + " Actual -> " + roomTax);
										} else
											WSClient.writeToReport(LogStatus.FAIL,
													"Room Tax : Expected -> " + tax + " Actual ->" + roomTax);
									} else {
										if (startDate.equals(roomStart1)) {
											WSClient.writeToReport(LogStatus.INFO,
													"<b>Validating for the date " + startDate + "</b>");

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

											if (WSAssert.assertEquals(tax1, roomTax, true)) {

												WSClient.writeToReport(LogStatus.PASS,
														"Room Tax : Expected ->" + tax1 + " Actual -> " + roomTax);
											} else
												WSClient.writeToReport(LogStatus.FAIL,
														"Room Tax : Expected -> " + tax1 + " Actual ->" + roomTax);

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
											" Total Package and Room Charges : Expected -> " + totalAm + " Actual -> "
													+ totalCharge);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											" Total Package and Room Charges : Expected -> " + totalAm + " Actual -> "
													+ totalCharge);

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

							} else if (WSAssert.assertIfElementValueEquals(CreateBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "FAIL", true)) {

								if (WSAssert.assertIfElementExists(CreateBookingRes,
										"CreateBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.FAIL, " The error code that is generated is : "
											+ WSClient.getElementValue(CreateBookingRes,
													"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "");
								}

								else if (WSAssert.assertIfElementExists(CreateBookingRes,
										"CreateBookingResponse_Result_GDSError", true)) {
									WSClient.writeToReport(LogStatus.FAIL, " The error code that is generated is : "
											+ WSClient.getElementValue(CreateBookingRes,
													"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "");
								}
							} else if (WSAssert.assertIfElementExists(CreateBookingRes,
									"CreateBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										" The error that is generated is : "
												+ WSClient.getElementValue(CreateBookingRes,
														"CreateBookingResponse_faultstring", XMLType.RESPONSE)
												+ "");
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"Pre-Requisite failed >> Creating a reservation failed");
					}
				}
			}

			else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Pre-requisite failed >> Changing the application parameter failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				OPERALib.setOperaHeader(OPERALib.getUserName());
				if (resvId != null || !resvId.equals("error")) {
					WSClient.setData("{var_resvId}", resvId);
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
					String changeChannelParametersReq = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
					String changeChannelParametersRes = WSClient.processSOAPMessage(changeChannelParametersReq);
					if (WSAssert.assertIfElementExists(changeChannelParametersRes, "ChangeChannelParametersRS_Success",
							true)) {
						String query = WSClient.getQuery("ChangeChannelParameters", "QS_01");
						LinkedHashMap results = WSClient.getDBRow(query);
						if (results.get("ACCEPT_LOWER_RATES_YN").equals(paramvalue))
							WSClient.writeToLog("Successfully reverted the paramvalue");
						else
							WSClient.writeToLog("Paramter value did not get updated to initial value");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking", "OWS",
	// "Reservation" })

	public void createBooking_1233785() {
		try {
			resv = null;

			String testName = "createBooking_38395";
			WSClient.startTest(testName, "Verify that booking is not Created when occupancy is exceeded for a roomtype",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_extResort}", resortExtValue);
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/
				String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05");
				WSClient.setData("{var_rate}", rate);
				String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04");
				WSClient.setData("{var_roomType}", rt);
				String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_08");
				WSClient.setData("{var_orate}", ort);
				String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
				WSClient.setData("{var_resvType}", resvt);
				WSClient.setData("{var_reservationType}", resvt);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				String pkg = OperaPropConfig.getDataSetForCode("ReservationType", "DS_03");
				WSClient.setData("{var_revType}", pkg);
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_21");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(updateProfileResponseXML,
							"CreateBookingResponse_Result_OperaErrorCode", false)) {
						WSClient.writeToReport(LogStatus.PASS,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}

				} else
					WSClient.writeToReport(LogStatus.FAIL,
							"Create Booking Response does not give an error message when room occupancy is exceeded for the room Type");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	
// Need to debug later
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_122208() throws Exception {
		try {
			resv = null;

			String testName = "createBooking_22208";
			WSClient.startTest(testName,
					"Verify that membership is getting added at the time of booking when MEMBERSHIP_ENROLLMENT_AT_RESERVATION=Y",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
				WSClient.setData("{var_rate}", rate);
				String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
				WSClient.setData("{var_roomType}", rt);
				String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
				WSClient.setData("{var_orate}", ort);
				String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
				WSClient.setData("{var_resvType}", resvt);
				WSClient.setData("{var_reservationType}", resvt);
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				// OperaPropConfig.getDataSetForCode("ReservationType",
				// "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				WSClient.setData("{var_parameter}", "MEMBERSHIP_ENROLLMENT_AT_RESERVATION");
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if MEMBERSHIP_ENROLLMENT_AT_RESERVATION=Y</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing MEMBERSHIP_ENROLLMENT_AT_RESERVATION to Y</b>");
					Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}
				profileID = CreateProfile.createProfile("DS_00");

				WSClient.setData("{var_profileId}", profileID);

				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
				WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
				String lastname = "MEMBERSHIP";
				WSClient.setData("{var_membershipName}", lastname);
				//
				WSClient.setData("{var_membershipLevel}",
						OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
				// WSClient.setData("{var_membershipLevel}", "Platinum");
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");

				/***************
				 * OWS Modify Booking Operation
				 ********************/
				fetchAvailability(rate, rt);

				String modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_18");
				String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);
				WSClient.setData("{var_resv}", WSClient.getElementValue(modifyBookingRes,
						"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(modifyBookingReq,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(modifyBookingRes,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(modifyBookingRes,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating with Database</b>");
					String query = WSClient.getQuery("QS_07");
					LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);

					String last = "";
					String first = "";
					String name = "";
					if (WSAssert.assertIfElementExists(modifyBookingRes, "Customer_PersonName_lastName", true))
						last = WSClient.getElementValue(modifyBookingRes, "Customer_PersonName_lastName",
								XMLType.RESPONSE);
					if (WSAssert.assertIfElementExists(modifyBookingRes, "Customer_PersonName_firstName", true))
						first = WSClient.getElementValue(modifyBookingRes, "Customer_PersonName_firstName",
								XMLType.RESPONSE);

					if (!first.equals("") && !last.equals(""))
						name = first + " " + last;
					else
						name = first + last;
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Membership Details</b>");

					if (WSAssert.assertEquals(name, bookingDets.get("NAME"), true)) {
						WSClient.writeToReport(LogStatus.PASS,
								"name-> Expected :" + name + " Actual:" + bookingDets.get("NAME"));
					} else
						WSClient.writeToReport(LogStatus.FAIL,
								"name-> Expected :" + name + " Actual:" + bookingDets.get("NAME"));
					WSAssert.assertIfElementValueEquals(modifyBookingRes, "Memberships_NameMembership_membershipType",
							bookingDets.get("TYPE"), false);
					WSAssert.assertIfElementValueEquals(modifyBookingRes, "Memberships_NameMembership_membershipNumber",
							bookingDets.get("NUM"), false);
					WSAssert.assertIfElementValueEquals(modifyBookingRes, "Memberships_NameMembership_membershipLevel",
							bookingDets.get("LEVEL"), false);
					WSAssert.assertIfElementValueEquals(modifyBookingRes, "Memberships_NameMembership_expirationDate",
							bookingDets.get("EXP"), false);

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured :" + e);
		} finally {
			if (resv != null)
				CancelReservation.cancelReservation("DS_02");
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_1222079() throws Exception {
		try {
			resv = null;

			String testName = "createBooking_2220868";
			WSClient.startTest(testName,
					"Verify that membership is not getting added at the time of booking when MEMBERSHIP_ENROLLMENT_AT_RESERVATION=N",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {

				/*************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
				WSClient.setData("{var_rate}", rate);
				String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
				WSClient.setData("{var_roomType}", rt);
				String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
				WSClient.setData("{var_orate}", ort);
				String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
				WSClient.setData("{var_resvType}", resvt);
				WSClient.setData("{var_reservationType}", resvt);
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				// WSClient.setData("{var_ReservationType}", "4PM");//
				// OperaPropConfig.getDataSetForCode("ReservationType",
				// "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				WSClient.setData("{var_parameter}", "MEMBERSHIP_ENROLLMENT_AT_RESERVATION");
				WSClient.setData("{var_settingValue}", "N");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if MEMBERSHIP_ENROLLMENT_AT_RESERVATION=N</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("N")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing MEMBERSHIP_ENROLLMENT_AT_RESERVATION to N</b>");
					Parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
				}
				profileID = CreateProfile.createProfile("DS_00");

				WSClient.setData("{var_profileId}", profileID);

				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
				WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
				String lastname = "MEMBERSHIP";
				WSClient.setData("{var_membershipName}", lastname);
				//
				WSClient.setData("{var_membershipLevel}",
						OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
				// WSClient.setData("{var_membershipLevel}", "Platinum");
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");

				/***************
				 * OWS Modify Booking Operation
				 ********************/
				fetchAvailability(rate, rt);

				String modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_18");
				String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);
				WSClient.setData("{var_resv}", WSClient.getElementValue(modifyBookingRes,
						"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(modifyBookingReq,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(modifyBookingRes,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(modifyBookingRes,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating with Database</b>");
					String query = WSClient.getQuery("QS_07");
					LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
					if (bookingDets.size() == 0)
						WSClient.writeToReport(LogStatus.PASS, "Membership isn't created at the time of booking ");
					else
						WSClient.writeToReport(LogStatus.PASS, "Membership is created at the time of booking ");

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured : " + e);
		} finally {
			if (resv != null)
				CancelReservation.cancelReservation("DS_02");
		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking", "OWS",
	// "Reservation" })

	public void createBooking_12335() throws Exception {
		try {
			resv = null;

			String testName = "createBooking_38395";
			WSClient.startTest(testName,
					"Verify that booking is not Created when length of stay is exceeded for a rate code restriction",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_08");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_35");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(updateProfileResponseXML,
							"CreateBookingResponse_Result_OperaErrorCode", false)) {
						WSClient.writeToReport(LogStatus.PASS,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}

				} else
					WSClient.writeToReport(LogStatus.FAIL,
							"Create Booking Response does not give an error message when room occupancy is exceeded for the room Type");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_rateCodePkgs() throws Exception {
		try {
			resv = null;

			String testName = "createBooking_3830095";
			WSClient.startTest(testName, "Verify that booking is created using rate code with packages",
					"minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			WSClient.setData("{var_profileSource}", interfaceName);

			WSClient.setData("{var_chain}", OPERALib.getChain());

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsresort}", resortExtValue);

			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_03");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_02");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_06");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_02");
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_resvType}", resvt);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//

			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_650}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_655}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				fetchAvailability(rate, rt);

				WSClient.writeToReport(LogStatus.INFO,
						"<b>Create Booking with rate code attached to  multiple packages</b>");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_32");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
							profileID, false);
					elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode",
							XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Packages attached to rate code</b>");

					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));

					String query = WSClient.getQuery("QS_22");
					LinkedHashMap results = WSClient.getDBRow(query);
					String pkg1 = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_Packages_Package_packageCode", XMLType.RESPONSE);
					String pkg2 = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_Packages_Package[2]_packageCode", XMLType.RESPONSE);
					// String pkg3 =
					// WSClient.getElementValue(updateProfileResponseXML,
					// "RoomStay_Packages_Package[3]_packageCode",
					// XMLType.RESPONSE);
					// String pkg4 =
					// WSClient.getElementValue(updateProfileResponseXML,
					// "RoomStay_Packages_Package[4]_packageCode",
					// XMLType.RESPONSE);
					if (WSAssert.assertEquals(results.get("PACKAGE").toString(), pkg1 + "," + pkg2, true))
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + results.get("PACKAGE").toString()
								+ " Actual value :" + pkg1 + "," + pkg2);
					else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + results.get("PACKAGE").toString()
								+ " Actual value :" + pkg1 + "," + pkg2);

				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
			if (resv != null)
				CancelReservation.cancelReservation("DS_02");
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_12336() throws Exception {
		try {
			resv = null;

			String testName = "createBooking_3839005";
			WSClient.startTest(testName,
					"Verify that booking is not Created when booking's end date is before the start date",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_05");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_08");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_166}") + "T22:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_165}") + "T22:00:00");
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_35");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(updateProfileResponseXML,
							"CreateBookingResponse_Result_OperaErrorCode", false)) {
						WSClient.writeToReport(LogStatus.PASS,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}

				} else
					WSClient.writeToReport(LogStatus.FAIL,
							"Create Booking Response does not give an error message when booking's end date is befor the start date");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}


//	Negative case -- Need to update as per the request
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_3839500() throws Exception {
		try {
			resv = null;

			String testName = "createBooking_3839500";
			WSClient.startTest(testName,
					"Verify that booking is not Created when package's start date is before the reservation start date",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String pkg = OperaPropConfig.getDataSetForCode("PackageCode", "DS_03");
			WSClient.setData("{var_pkg}", pkg);

			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_08");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_166}") + "T22:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_167}") + "T22:00:00");

				WSClient.setData("{var_start}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_161}"));
				WSClient.setData("{var_end}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_165}"));

				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_37");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(updateProfileResponseXML,
							"CreateBookingResponse_Result_OperaErrorCode", false)) {
						WSClient.writeToReport(LogStatus.PASS,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}

				} else
					WSClient.writeToReport(LogStatus.FAIL,
							"Create Booking Response does not give an error message when the package's start date is before the booking date");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}


//Negative case -- Need to update it as per the actual result	
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_3800395() throws Exception {
		try {
			resv = null;

			String testName = "createBooking_3800395";
			WSClient.startTest(testName,
					"Verify that booking is not Created when package's end date is after the reservation's end date",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String pkg = OperaPropConfig.getDataSetForCode("PackageCode", "DS_03");
			WSClient.setData("{var_pkg}", pkg);

			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_166}") + "T22:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_167}") + "T22:00:00");

				WSClient.setData("{var_start}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_166}"));
				WSClient.setData("{var_end}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_168}"));

				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_37");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
					if (WSAssert.assertIfElementExists(updateProfileResponseXML,
							"CreateBookingResponse_Result_OperaErrorCode", false)) {
						WSClient.writeToReport(LogStatus.PASS,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE));
					}

				} else
					WSClient.writeToReport(LogStatus.FAIL,
							"Create Booking Response does not give an error message when the package's end date is after  the booking's end date");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_3008395_multiplePkgs() throws Exception {
		try {
			resv = null;

			String testName = "createBooking_3008395_multiplePkgs";
			WSClient.startTest(testName, "Verify that Booking is created with multiple web bookable packages",
					"minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsresort}", resortExtValue);

			String pkg = OperaPropConfig.getDataSetForCode("PackageCode", "DS_03");
			WSClient.setData("{var_pkg}", pkg);
			String pkg2 = OperaPropConfig.getDataSetForCode("PackageCode", "DS_02");
			WSClient.setData("{var_pkg2}", pkg2);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//

			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");
				WSClient.writeToReport(LogStatus.INFO, "<b>Create Booking with multiple packages</b>");
				fetchAvailability(rate, rt);

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_36");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq,
							"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Packages with response</b>");

					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));

					HashMap<String, String> xPath = new HashMap<String, String>();
					xPath.put("RoomStay_Packages_Package_packageCode", "RoomStays_RoomStay_Packages");
					xPath.put("RoomStay_Packages_Package[2]_packageCode", "RoomStays_RoomStay_Packages");
					List<LinkedHashMap<String, String>> respData = WSClient.getMultipleNodeList(updateProfileReq, xPath,
							false, XMLType.REQUEST);

					xPath.put("RoomStay_Packages_Package_packageCode", "RoomStays_RoomStay_Packages");
					xPath.put("RoomStay_Packages_Package[2]_packageCode", "RoomStays_RoomStay_Packages");
					List<LinkedHashMap<String, String>> reqData = WSClient.getMultipleNodeList(updateProfileResponseXML,
							xPath, false, XMLType.RESPONSE);

					WSAssert.assertEquals(reqData, respData, false);
					String query = WSClient.getQuery("OWSModifyBooking", "QS_27");
					ArrayList<LinkedHashMap<String, String>> dbResults = WSClient.getDBRows(query);

					HashMap<String, String> xPath1 = new HashMap<>();
					xPath1.put("RoomStay_Packages_Package_packageCode", "RoomStay_Packages_Package");
					
					List<LinkedHashMap<String, String>> resValues = WSAssert
							.getMultipleNodeList(updateProfileResponseXML, xPath1, false, XMLType.RESPONSE);
					List<LinkedHashMap<String, String>> reqValues = WSAssert
							.getMultipleNodeList(updateProfileResponseXML, xPath1, false, XMLType.REQUEST);

					WSClient.writeToReport(LogStatus.INFO, "<b>Validation of insertion of packages in DB</b>");
					WSAssert.assertEquals(resValues, dbResults, false);

				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
			if (resv != null)
				CancelReservation.cancelReservation("DS_02");

		}
	}

//	Need to debug Later
	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation", "createBooking_multipleAddress_Uday" })
	public void createBooking_multipleAddress() {
		try {
			resv = null;

			String testName = "createBooking_multipleAddress";
			WSClient.startTest(testName,
					"Verify that a Booking is created for a profile with multiple addresses and other communication details ",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			String fname = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lname = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_fname}", fname);
			WSClient.setData("{var_lname}", lname);
			// WSClient.setData("{var_rate}", "CKRC");
			// WSClient.setData("{var_roomType}", "4RT");
			// WSClient.setData("{var_resvType}", "QA_4PM");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			HashMap<String, String> fullAddress = OPERALib.fetchAddressLOV();
			WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_03"));
			WSClient.setData("{var_phoneMethod}", OperaPropConfig.getDataSetForCode("CommunicationMethod", "DS_01"));
			WSClient.setData("{var_email}", fname + "@ORACLE.COM");
			WSClient.setData("{var_emailType}", OperaPropConfig.getDataSetForCode("CommunicationType", "DS_02"));
			WSClient.setData("{var_primary}", "false");
			WSClient.setData("{var_gender}", OperaPropConfig.getDataSetForCode("Gender", "DS_01"));
			// Setting Variables
			String addressType = OperaPropConfig.getDataSetForCode("AddressType", "DS_01");
			WSClient.setData("{var_addressType}", addressType);
			WSClient.setData("{var_city}", fullAddress.get("City"));
			WSClient.setData("{var_zip}", fullAddress.get("Zip"));
			WSClient.setData("{var_state}", fullAddress.get("State"));
			WSClient.setData("{var_country}", fullAddress.get("Country"));
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
			WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
			WSClient.setData("{var_time}", "09:00:00");
			// WSClient.setData("{var_rate}", "CKRC");
			profileID = CreateProfile.createProfile("DS_42");
			fetchAvailability(rate, rt);

			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"Create Booking Response gives the error message-----> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
							"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
					WSClient.setData("{var_profileId}", WSClient.getElementValue(updateProfileResponseXML,
							"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
							false)) {
						WSClient.writeToReport(LogStatus.PASS,
								"Profile ID " + WSClient.getElementValue(updateProfileResponseXML,
										"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));

						String query3 = WSClient.getQuery("QS_23");
						LinkedHashMap<String, String> bookingDets2 = WSClient.getDBRow(query3);

						String email = WSClient.getElementValue(updateProfileResponseXML, "Profile_EMails_NameEmail",
								XMLType.RESPONSE);
						String phone = WSClient.getElementValue(updateProfileResponseXML,
								"Phones_NamePhone_PhoneNumber", XMLType.RESPONSE);
						String address = WSClient.getElementValue(updateProfileResponseXML,
								"Addresses_NameAddress_AddressLine", XMLType.RESPONSE);
						String address1 = WSClient.getElementValue(updateProfileResponseXML,
								"Addresses_NameAddress_AddressLine_2", XMLType.RESPONSE);

						String address2 = WSClient.getElementValue(updateProfileResponseXML,
								"Addresses_NameAddress_AddressLine_3", XMLType.RESPONSE);

						String address3 = WSClient.getElementValue(updateProfileResponseXML,
								"Addresses_NameAddress_AddressLine_4", XMLType.RESPONSE);

						String state = WSClient.getElementValue(updateProfileResponseXML,
								"Addresses_NameAddress_stateProv", XMLType.RESPONSE);
						String country = WSClient.getElementValue(updateProfileResponseXML,
								"Addresses_NameAddress_countryCode", XMLType.RESPONSE);
						String zip = WSClient.getElementValue(updateProfileResponseXML,
								"Addresses_NameAddress_postalCode", XMLType.RESPONSE);
						String city = WSClient.getElementValue(updateProfileResponseXML,
								"Addresses_NameAddress_cityName", XMLType.RESPONSE);

						WSClient.writeToReport(LogStatus.INFO, "<b>Validating EMAIL</b>");
						if (WSAssert.assertEquals(email, bookingDets2.get("EMAIL"), true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + email + " Actual value :" + bookingDets2.get("EMAIL"));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + email + " Actual value :" + bookingDets2.get("EMAIL"));
						// WSAssert.assertEquals(email,
						// bookingDets2.get("EMAIL"), false);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating PHONE</b>");
						if (WSAssert.assertEquals(phone, bookingDets2.get("PHONE"), true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + phone + " Actual value :" + bookingDets2.get("PHONE"));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + phone + " Actual value :" + bookingDets2.get("PHONE"));
						// WSAssert.assertEquals(phone,
						// bookingDets2.get("PHONE"), false);

						// WSAssert.assertEquals(address,
						// bookingDets2.get("ADDRESS"), false);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating STATE</b>");
						if (WSAssert.assertEquals(state, bookingDets2.get("STATE"), true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + state + " Actual value :" + bookingDets2.get("STATE"));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + state + " Actual value :" + bookingDets2.get("STATE"));
						// WSAssert.assertEquals(state,
						// bookingDets2.get("STATE"), false);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating COUNTRY</b>");
						if (WSAssert.assertEquals(country, bookingDets2.get("COUNTRY"), true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + country + " Actual value :" + bookingDets2.get("COUNTRY"));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + country + " Actual value :" + bookingDets2.get("COUNTRY"));
						// WSAssert.assertEquals(country,
						// bookingDets2.get("COUNTRY"), false);
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating ZIP</b>");
						if (WSAssert.assertEquals(zip, bookingDets2.get("ZIP"), true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + zip + " Actual value :" + bookingDets2.get("ZIP"));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + zip + " Actual value :" + bookingDets2.get("ZIP"));
						// WSAssert.assertEquals(zip, bookingDets2.get("ZIP"),
						// false);

						WSClient.writeToReport(LogStatus.INFO, "<b>Validating ADDRESSLINE -1</b>");
						if (WSAssert.assertEquals(address, bookingDets2.get("ADDRESS"), true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + address + " Actual value :" + bookingDets2.get("ADDRESS"));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + address + " Actual value :" + bookingDets2.get("ADDRESS"));
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating ADDRESSLINE -2</b>");
						if (WSAssert.assertEquals(address1, bookingDets2.get("ADDRESS2"), true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + address1 + " Actual value :" + bookingDets2.get("ADDRESS2"));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + address1 + " Actual value :" + bookingDets2.get("ADDRESS2"));
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating ADDRESSLINE -3</b>");
						if (WSAssert.assertEquals(address2, bookingDets2.get("ADDRESS3"), true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + address2 + " Actual value :" + bookingDets2.get("ADDRESS3"));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + address2 + " Actual value :" + bookingDets2.get("ADDRESS3"));
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating ADDRESSLINE -4</b>");
						if (WSAssert.assertEquals(address3, bookingDets2.get("ADDRESS4"), true)) {
							WSClient.writeToReport(LogStatus.PASS,
									"Expected value:" + address3 + " Actual value :" + bookingDets2.get("ADDRESS4"));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Expected value:" + address3 + " Actual value :" + bookingDets2.get("ADDRESS4"));

					} else

						WSClient.writeToReport(LogStatus.FAIL, "Profile is not created");
				} else

					WSClient.writeToReport(LogStatus.FAIL, "Create Booking fails");
				// CancelReservation.cancelReservation("DS_02");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured :" + e);

		} finally {
			try {
				if (resv != null)
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBookingINVALIDGAURANTEE() {
		try {
			String testName = "createBooking_3128395";
			resv = null;
			WSClient.startTest(testName,
					"Verify that error exists in the response when invalid reservation Type is passed",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_rate}", "CKRC");
			// WSClient.setData("{var_roomType}", "INVALID");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			WSClient.setData("{var_resvType}", "invalid");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/
				WSClient.writeToReport(LogStatus.INFO, "<b>Guarantee Code passed is : INVALID</b>");
				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
				WSClient.setData("{var_busdate1}",
						WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, rt);
				WSClient.setData("{var_resvType}", "invalid");

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {

					WSClient.writeToReport(LogStatus.PASS,
							"Create Booking Response gives the error message-----> "
									+ WSClient.getElementValue(updateProfileResponseXML,
											"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE));

				} else

					WSClient.writeToReport(LogStatus.FAIL,
							"No error message is populated when invalid reservation type is passed");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured :" + e);
		} finally {
			if (resv != null)
				try {
					CancelReservation.cancelReservation("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking", "Reservation",
	// "OWS", "PriorRun" })

	public void createBooking_222042221() {
		try {
			resv = null;
			String testName = "createBooking_222142";
			WSClient.startTest(testName,
					"Verify that new address is not added to the profile when channel parameter OWS_UPD_PROFILE_FOR_RESV is enabled",
					"minimumRegression");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "OWS_UPD_PROFILE_FOR_RESV");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_param}", "OWS_UPD_PROFILE_FOR_RESV");
			WSClient.writeToReport(LogStatus.INFO,
					"<b>Verify if the parameter OWS_UPD_PROFILE_FOR_RESV is enabled</b>");
			WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
			WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
			WSClient.setData("{var_time}", "09:00:00");

			String query = WSClient.getQuery("ChangeChannelParameters", "QS_02");
			LinkedHashMap<String, String> results = WSClient.getDBRow(query);
			String paramvalue = results.get("PARAMETER_VALUE");
			WSClient.writeToReport(LogStatus.INFO, "<b>OWS_UPD_PROFILE_FOR_RESV : " + paramvalue + "</b>");
			if (!WSAssert.assertEquals("Y", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter OWS_UPD_PROFILE_FOR_RESV</b>");

				String changeChannelParametersReq = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
				String changeChannelParametersRes = WSClient.processSOAPMessage(changeChannelParametersReq);
				if (WSAssert.assertIfElementExists(changeChannelParametersRes, "ChangeChannelParametersRS_Success",
						true)) {
					query = WSClient.getQuery("QS_02");
					results = WSClient.getDBRow(query);
					paramvalue = results.get("PARAMETER_VALUE");
					WSClient.writeToReport(LogStatus.INFO, "<b>OWS_UPD_PROFILE_FOR_RESV : " + paramvalue + "</b>");
					System.out.println(paramvalue);
				}
			}

			if (paramvalue.equals("Y")) {

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
							OperaPropConfig.getDataSetForCode("ReservationType", "DS_02"));

					/************
					 * Prerequisite 1: Create profile
					 *****************/
					profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/

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
						String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_38");
						String createBookingRes = WSClient.processSOAPMessage(createBookingReq);

						if (WSAssert.assertIfElementExists(createBookingRes, "CreateBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(createBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								resv = WSClient.getElementValueByAttribute(createBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(createBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating against DB</b>");

								LinkedHashMap<String, String> dbDetails = WSClient.getDBRow(WSClient.getQuery("QS_25"));

								if (dbDetails.size() == 0) {
									WSClient.writeToReport(LogStatus.PASS,
											"Address did not got added to the profile as expected");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Address got added to the profile");
								}

								WSClient.writeToReport(LogStatus.INFO, "<b>Validating against Response</b>");

								HashMap<String, String> xPath = new HashMap<String, String>();
								xPath.put("Profile_Addresses_NameAddress_otherAddressType",
										"Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_AddressLine", "Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_cityName", "Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_postalCode", "Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_stateProv", "Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_countryCode", "Profile_Addresses_NameAddress");
								List<LinkedHashMap<String, String>> resValues = WSClient
										.getMultipleNodeList(createBookingRes, xPath, false, XMLType.RESPONSE);
								if (resValues.size() == 0) {
									WSClient.writeToReport(LogStatus.PASS,
											"Address did not got added to the profile as expected");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, "Address got added to the profile");
								}

							}
							if (WSAssert.assertIfElementExists(createBookingRes,
									"CreateBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(createBookingRes,
												"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(createBookingRes,
									"CreateBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(createBookingRes,
														"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(createBookingRes, "Result_Text_TextElement", true)) {
								/****
								 * Verifying that the error message is populated
								 * on the response
								 ******/

								String message = WSAssert.getElementValue(createBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						}

					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Prerequisite Blocked-----Channel Parameter OWS_UPD_PROFILE_FOR_RESV not enabled");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
			if (resv != null)
				try {
					CancelReservation.cancelReservation("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking", "Reservation",
	// "OWS", "PriorRun" })

	public void createBooking_2220422213() {
		try {
			resv = null;
			String testName = "createBooking_2221424";
			WSClient.startTest(testName,
					"Verify that new address is added to profile when channel parameter OWS_UPD_PROFILE_FOR_RESV is disabled",
					"minimumRegression");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			WSClient.setData("{var_channel}", OWSLib.getChannel());
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_par}", "N");
			WSClient.setData("{var_parname}", "OWS_UPD_PROFILE_FOR_RESV");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_param}", "OWS_UPD_PROFILE_FOR_RESV");
			WSClient.writeToReport(LogStatus.INFO,
					"<b>Verify if the parameter OWS_UPD_PROFILE_FOR_RESV is NOT enabled</b>");
			WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_150}") + "T06:00:00");
			WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_155}") + "T06:00:00");
			WSClient.setData("{var_time}", "09:00:00");

			String query = WSClient.getQuery("ChangeChannelParameters", "QS_02");
			LinkedHashMap<String, String> results = WSClient.getDBRow(query);
			String paramvalue = results.get("PARAMETER_VALUE");
			WSClient.writeToReport(LogStatus.INFO, "<b>OWS_UPD_PROFILE_FOR_RESV : " + paramvalue + "</b>");
			if (!WSAssert.assertEquals("N", paramvalue, true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OWS_UPD_PROFILE_FOR_RESV</b>");

				String changeChannelParametersReq = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
				String changeChannelParametersRes = WSClient.processSOAPMessage(changeChannelParametersReq);
				if (WSAssert.assertIfElementExists(changeChannelParametersRes, "ChangeChannelParametersRS_Success",
						true)) {
					query = WSClient.getQuery("QS_02");
					results = WSClient.getDBRow(query);
					paramvalue = results.get("PARAMETER_VALUE");
					WSClient.writeToReport(LogStatus.INFO, "<b>OWS_UPD_PROFILE_FOR_RESV : " + paramvalue + "</b>");
					System.out.println(paramvalue);
				}
			}

			if (paramvalue.equals("N")) {

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
							OperaPropConfig.getDataSetForCode("ReservationType", "DS_02"));

					/************
					 * Prerequisite 1: Create profile
					 *****************/
					profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/

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
						String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_38");
						String createBookingRes = WSClient.processSOAPMessage(createBookingReq);

						if (WSAssert.assertIfElementExists(createBookingRes, "CreateBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(createBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								resv = WSClient.getElementValueByAttribute(createBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(createBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating against DB</b>");

								LinkedHashMap<String, String> dbDetails = WSClient.getDBRow(WSClient.getQuery("QS_25"));

								if (dbDetails.size() == 0) {

									WSClient.writeToReport(LogStatus.FAIL, "Address did not get added to the profile");
								} else {
									WSClient.writeToReport(LogStatus.PASS,
											"Address  got added to the profile as expected");
								}

								WSClient.writeToReport(LogStatus.INFO, "<b>Validating against Response</b>");

								HashMap<String, String> xPath = new HashMap<String, String>();
								xPath.put("Profile_Addresses_NameAddress_otherAddressType",
										"Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_AddressLine", "Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_cityName", "Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_postalCode", "Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_stateProv", "Profile_Addresses_NameAddress");
								xPath.put("Addresses_NameAddress_countryCode", "Profile_Addresses_NameAddress");
								List<LinkedHashMap<String, String>> resValues = WSClient
										.getMultipleNodeList(createBookingRes, xPath, false, XMLType.RESPONSE);
								if (resValues.size() == 0) {
									WSClient.writeToReport(LogStatus.FAIL, "Address did not got added to the profile");
								} else {

									WSClient.writeToReport(LogStatus.PASS,
											"Address got added to the profile As expected");
								}

							}
							if (WSAssert.assertIfElementExists(createBookingRes,
									"CreateBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(createBookingRes,
												"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(createBookingRes,
									"CreateBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(createBookingRes,
														"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(createBookingRes, "Result_Text_TextElement", true)) {
								/****
								 * Verifying that the error message is populated
								 * on the response
								 ******/

								String message = WSAssert.getElementValue(createBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message + "</b>");
							}
						}

					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"Prerequisite Blocked-----Channel Parameter OWS_UPD_PROFILE_FOR_RESV not enabled");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
			if (resv != null)
				try {
					CancelReservation.cancelReservation("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking12111() {
		try {
			String testName = "createBooking_3812395";
			WSClient.startTest(testName, "Verify that Booking is created with multiple User Defined Functions",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());

			String operaProfileID = CreateProfile.createProfile("DS_00");

			WSClient.setData("{var_profileId}", operaProfileID);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_145}") + "T06:00:00");
			WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_147}") + "T06:00:00");
			WSClient.setData("{var_time}", "09:00:00");
			WSClient.setData("{var_rate}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01"));
			WSClient.setData("{var_resvType}", OperaPropConfig.getDataSetForCode("ReservationType", "DS_01"));
			WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
			String rate = WSClient.getData("{var_rate}");
			String rt = WSClient.getData("{var_roomType}");

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

			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
					OWSLib.getChannelType(interfaceName),

					OWSLib.getChannelCarier(resortOperaValue, interfaceName));
			fetchAvailability(rate, rt);

			String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_13");
			String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
			if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
					"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
						true)) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Create Booking Response gives the error message-----></b> " + WSClient.getElementValue(
									updateProfileResponseXML, "CreateBookingResponse_Result_Text", XMLType.RESPONSE));
				}

				// validating the data
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML,
						"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));

				HashMap<String, String> xPath = new HashMap<String, String>();

				// xPaths of the records being verified and their
				// parents are being put in a hashmap
				xPath.put("UserDefinedValues_UserDefinedValue_CharacterValue_3",
						"HotelReservation_UserDefinedValues_UserDefinedValue");
				xPath.put("UserDefinedValues_UserDefinedValue_NumericValue_3",
						"HotelReservation_UserDefinedValues_UserDefinedValue");
				xPath.put("UserDefinedValues_UserDefinedValue_DateValue_3",
						"HotelReservation_UserDefinedValues_UserDefinedValue");
				xPath.put("HotelReservation_UserDefinedValues_UserDefinedValue_valueName",
						"HotelReservation_UserDefinedValues_UserDefinedValue");

				HashMap<String, String> xPath1 = new HashMap<String, String>();

				// xPaths of the records being verified and their
				// parents are being put in a hashmap
				xPath1.put("UserDefinedValues_UserDefinedValue_CharacterValue_2",
						"HotelReservation_UserDefinedValues_UserDefinedValue");
				xPath1.put("UserDefinedValues_UserDefinedValue_NumericValue_2",
						"HotelReservation_UserDefinedValues_UserDefinedValue");
				xPath1.put("UserDefinedValues_UserDefinedValue_DateValue_2",
						"HotelReservation_UserDefinedValues_UserDefinedValue");
				xPath1.put("HotelReservation_UserDefinedValues_UserDefinedValue_valueName",
						"HotelReservation_UserDefinedValues_UserDefinedValue");

				/***** Validating against Response *****/
				WSClient.writeToReport(LogStatus.INFO, "<b>Validating Against Response</b>");
				List<LinkedHashMap<String, String>> reqValues = WSClient.getMultipleNodeList(updateProfileReq, xPath,
						false, XMLType.REQUEST);

				List<LinkedHashMap<String, String>> resValues = WSClient.getMultipleNodeList(updateProfileResponseXML,
						xPath1, false, XMLType.RESPONSE);

				for (int i = 0; i < resValues.size(); i++) {
					if (resValues.get(i).containsKey("DateValue1")) {
						resValues.get(i).put("DateValue1", resValues.get(i).get("DateValue1").substring(0, 10));
					}
				}

				for (int i = 0; i < reqValues.size(); i++) {
					if (reqValues.get(i).containsKey("DateValue1")) {
						reqValues.get(i).put("DateValue1", reqValues.get(i).get("DateValue1"));
					}
				}

				WSAssert.assertEquals(resValues, reqValues, false);
				/***** Validating against DB *****/
				WSClient.writeToReport(LogStatus.INFO, "<b>Validating Against DB</b>");
				List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
				String query1 = WSClient.getQuery("QS_24");
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

			} else

				WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_guest4() {
		try {
			String testName = "createBooking_3831295";
			WSClient.startTest(testName,
					"Verify that Rate Details with extra adult for multi night reservation is correctly populated.",

					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			OPERALib.setOperaHeader(OPERALib.getUserName());

			String operaProfileID = CreateProfile.createProfile("DS_00");
			int nDays = 2;
			int hours = nDays * 24;
			WSClient.setData("{var_profileId}", operaProfileID);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate}",
					WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 19));
			WSClient.setData("{var_busdate1}",
					WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_" + hours + "}").substring(0, 19));
			WSClient.setData("{var_time}", "09:00:00");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05");
			String roomType = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04");
			WSClient.setData("{var_rate}", rate);
			WSClient.setData("{var_resvType}", OperaPropConfig.getDataSetForCode("ReservationType", "DS_01"));
			WSClient.setData("{var_roomType}", roomType);
			WSClient.setData("{var_ratecode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
					OWSLib.getChannelType(interfaceName),

					OWSLib.getChannelCarier(resortOperaValue, interfaceName));
			fetchAvailability(rate, roomType);

			String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_39");
			String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
			if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
					"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
						true)) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Create Booking Response gives the error message-----></b> " + WSClient.getElementValue(
									updateProfileResponseXML, "CreateBookingResponse_Result_Text", XMLType.RESPONSE));
				}

				String query = WSClient.getQuery("QS_26");
				System.out.println("Query: " + query);
				HashMap<String, String> prices = WSClient.getDBRow(query);
				int chargeForDay = Integer.parseInt(prices.get("AMOUNT_3"))
						+ Integer.parseInt(prices.get("ADULT_CHARGE"));

				WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Rate<b>");
				WSClient.writeToReport(LogStatus.INFO,
						"<b> Charges 3 Adults: " + prices.get("AMOUNT_3") + "  Extra Adult: " + prices.get

						("ADULT_CHARGE") + "</b>");
				WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"RoomStay_RoomRates_RoomRate_ratePlanCode", rate, false);
				WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"RoomStay_RoomRates_RoomRate_roomTypeCode", roomType, false);
				WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Rates_Rate_Base_currencyCode", "USD",
						false);
				WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Rates_Rate_Base",
						Integer.toString(chargeForDay), false);

				
				WSClient.writeToReport(LogStatus.INFO, "<b>Validating Expected Charges</b>");
				WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages",
						Integer.toString(chargeForDay * nDays), false);
				//roomstays_roomstay_guarantee_hotelreference_expectedcharges_totalroomrateandpackages
				
				HashMap<String, String> xpath = new HashMap<>();
				// xpath.put("RoomStay_ExpectedCharges_ChargesForPostingDate_PostingDate",
				// "RoomStay_ExpectedCharges_ChargesForPostingDate");

				xpath.put("ExpectedCharges_ChargesForPostingDate_RoomRateAndPackages_TotalCharges",

						"RoomStay_ExpectedCharges_ChargesForPostingDate");
				xpath.put("RoomRateAndPackages_Charges_Amount_currencyCode",
						"RoomStay_ExpectedCharges_ChargesForPostingDate");

				List<LinkedHashMap<String, String>> resDayData = WSClient.getMultipleNodeList(updateProfileResponseXML,
						xpath, false,

						XMLType.RESPONSE);
				List<LinkedHashMap<String, String>> expDayData = new ArrayList<LinkedHashMap<String, String>>();
				LinkedHashMap<String, String> data = new LinkedHashMap<>();
				data.put("agesChargesAmountcurrencyCode1", "USD");
				data.put("omRateAndPackagesTotalCharges1", Integer.toString(chargeForDay));
				for (int j = 1; j <= nDays; ++j)
					expDayData.add(data);

				WSAssert.assertEquals(expDayData, resDayData, false);

			} else

				WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

//	Need to debug later
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	public void createBooking_3839125_guestParam() {
		try {
			String testName = "createBooking_3839125";
			WSClient.startTest(testName,
					"Verify that Rate Details on incremental basis with extra adult for multi night reservation is correctly populated.",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String operaProfileID = CreateProfile.createProfile("DS_00");
			int nDays = 2;
			int hours = nDays * 24;
			WSClient.setData("{var_profileId}", operaProfileID);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_busdate}",
					WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 19));
			WSClient.setData("{var_busdate1}",
					WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_" + hours + "}").substring(0, 19));
			WSClient.setData("{var_time}", "09:00:00");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05");
			String roomType = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04");
			WSClient.setData("{var_rate}", rate);
			WSClient.setData("{var_resvType}", OperaPropConfig.getDataSetForCode("ReservationType", "DS_01"));
			WSClient.setData("{var_roomType}", roomType);
			WSClient.setData("{var_ratecode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
			WSClient.setData("{var_parameter}", "RATE_DETAIL_ADDED_VALUE");
			WSClient.setData("{var_settingValue}", "Y");
			String parameter = ChangeApplicationParameters.changeApplicationParameter("DS_01", "DS_01");
			WSClient.writeToReport(LogStatus.INFO, "<b>RATE_DETAIL_ADDED_VALUE_PER_HEAD is set ");
			if (parameter.contains("Y")) {
				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
						OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				fetchAvailability(rate, roomType);

				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_39");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
						"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Create Booking Response gives the error message-----></b> "
										+ WSClient.getElementValue(updateProfileResponseXML,
												"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					String query = WSClient.getQuery("QS_26");
					System.out.println("Query: " + query);
					HashMap<String, String> prices = WSClient.getDBRow(query);
					int chargeForDay = Integer.parseInt(prices.get("AMOUNT_1"))
							+ Integer.parseInt(prices.get("AMOUNT_2")) + Integer.parseInt

							(prices.get("AMOUNT_3")) + Integer.parseInt(prices.get("ADULT_CHARGE"));

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Rate<b>");
					int price3ad = chargeForDay - Integer.parseInt(prices.get("ADULT_CHARGE"));
					WSClient.writeToReport(LogStatus.INFO,
							"<b> Charges 3 Adults: " + price3ad + "  Extra Adult: " + prices.get("ADULT_CHARGE") +

									"</b>");
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"RoomStay_RoomRates_RoomRate_ratePlanCode", rate, false);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"RoomStay_RoomRates_RoomRate_roomTypeCode", roomType, false);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Rates_Rate_Base_currencyCode", "USD",
							false);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Rates_Rate_Base",
							Integer.toString(chargeForDay), false);

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Expected Charges</b>");
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
							"RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages",

							Integer.toString(chargeForDay * nDays), false);
					HashMap<String, String> xpath = new HashMap<>();
					// xpath.put("RoomStay_ExpectedCharges_ChargesForPostingDate_PostingDate",
					// "RoomStay_ExpectedCharges_ChargesForPostingDate");

					xpath.put("ExpectedCharges_ChargesForPostingDate_RoomRateAndPackages_TotalCharges",

							"RoomStay_ExpectedCharges_ChargesForPostingDate");
					xpath.put("RoomRateAndPackages_Charges_Amount_currencyCode",
							"RoomStay_ExpectedCharges_ChargesForPostingDate");

					List<LinkedHashMap<String, String>> resDayData = WSClient.getMultipleNodeList(
							updateProfileResponseXML, xpath, false,

							XMLType.RESPONSE);
					List<LinkedHashMap<String, String>> expDayData = new ArrayList<LinkedHashMap<String, String>>();
					LinkedHashMap<String, String> data = new LinkedHashMap<>();
					data.put("agesChargesAmountcurrencyCode1", "USD");
					data.put("omRateAndPackagesTotalCharges1", Integer.toString(chargeForDay));
					for (int j = 1; j <= nDays; ++j)
						expDayData.add(data);

					WSAssert.assertEquals(expDayData, resDayData, false);

				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

			} else {
				WSClient.writeToReport(LogStatus.ERROR, "Unable to set Parameter");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}

	}

	@Test(groups = { "minimumRegression", "CreateBooking", "Reservation", "OWS", "PriorRun",
			"createBooking_222062_Uday" })

	public void createBooking_222062() {
		try {
			String testName = "createBooking_222062";
			WSClient.startTest(testName,
					"Verify that guest count is populated correctly when create booking request is initiated with an existing reservation's confirmation Number",
					"minimumRegression");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			WSClient.setData("{var_ratecode}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			WSClient.setData("{var_busdate}",
					WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_120}").substring(0, 19));
			WSClient.setData("{var_busdate1}",
					WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_125}").substring(0, 19));
			WSClient.setData("{var_time}", "09:00:00");

			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
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
						OperaPropConfig.getDataSetForCode("ReservationType", "DS_02"));

				/************
				 * Prerequisite 1: Create profile
				 *************************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "Profile ID:" + profileID + "");
					WSClient.setData("{var_profileId}", profileID);

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/
					if (resvID.get("confirmationId") == null || resvID.get("confirmationId") == "error")
						resvID = CreateReservation.createReservation("DS_01");
					if (resvID.get("confirmationId") != "error") {
						WSClient.writeToReport(LogStatus.INFO, "Reservation ID:" + resvID.get("reservationId") + "");

						WSClient.setData("{var_confirmationId}", resvID.get("confirmationId"));
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));

						WSClient.setData("{var_legNo}", "2");
						fetchAvailability(rate, rt);

						String interfaceName = OWSLib.getChannel();
						String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_40");
						String createBookingRes = WSClient.processSOAPMessage(createBookingReq);

						if (WSAssert.assertIfElementExists(createBookingRes, "CreateBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(createBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String query = WSClient.getQuery("QS_27");
								HashMap<String, String> dbResults = WSClient.getDBRow(query);

								String adultCount = WSClient.getElementValue(createBookingReq,
										"RoomStay_GuestCounts_GuestCount_count", XMLType.REQUEST);
								String childCount = WSClient.getElementValue(createBookingReq,
										"RoomStay_GuestCounts_GuestCount[2]_count", XMLType.REQUEST);
								String resadultCount = WSClient.getElementValue(createBookingRes,
										"RoomStay_GuestCounts_GuestCount_count", XMLType.RESPONSE);
								String reschildCount = WSClient.getElementValue(createBookingRes,
										"RoomStay_GuestCounts_GuestCount_count_2", XMLType.RESPONSE);

								HashMap<String, String> tmpXPath = new HashMap<>();
								tmpXPath.put("RoomStay_GuestCounts_GuestCount_ageQualifyingCode",
										"RoomStay_GuestCounts_GuestCount");
								tmpXPath.put("RoomStay_GuestCounts_GuestCount_count",
										"RoomStay_GuestCounts_GuestCount");
								List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
								actualValues = WSClient.getMultipleNodeList(createBookingRes, tmpXPath, false,
										XMLType.RESPONSE);
								System.out.println(actualValues);
								int resAdultCount = 0, resChildCount = 0, count1;
								String ageQualifyingCode;

								for (LinkedHashMap<String, String> linkedHashMap : actualValues) {

									ageQualifyingCode = linkedHashMap.get("ageQualifyingCode1");
									count1 = Integer.parseInt(linkedHashMap.get("count1"));

									if (ageQualifyingCode.equalsIgnoreCase("ADULT")) {
										resAdultCount = resAdultCount + count1;
									} else if (ageQualifyingCode.equalsIgnoreCase("CHILD")) {
										resChildCount = resChildCount + count1;
									}
								}

								resadultCount = String.valueOf(resAdultCount);
								reschildCount = String.valueOf(resChildCount);

								WSAssert.assertIfElementValueEquals(createBookingRes, "Profile_ProfileIDs_UniqueID",
										WSClient.getElementValue(createBookingReq, "Profile_ProfileIDs_UniqueID",
												XMLType.REQUEST),
										false);

								/***** Validating against Response *****/
								WSClient.writeToReport(LogStatus.INFO, "Validating Against Response");

								/**** Validate modified adult guest count ****/
								if (WSAssert.assertEquals(adultCount, resadultCount, true)) {
									WSClient.writeToReport(LogStatus.PASS, " Adult Guest Count -> Expected : "
											+ adultCount + "  Actual : " + resadultCount + "");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, " Adult Guest Count -> Expected : "
											+ adultCount + "  Actual : " + resadultCount + "");
								}
								/**** Validate modified child guest count ****/
								if (WSAssert.assertEquals(childCount, reschildCount, true)) {
									WSClient.writeToReport(LogStatus.PASS, " Child Guest Count -> Expected : "
											+ childCount + "  Actual : " + reschildCount + "");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, " Child Guest Count -> Expected : "
											+ childCount + "  Actual : " + reschildCount + "");
								}

								/***** Validating against DB *****/
								WSClient.writeToReport(LogStatus.INFO, "Validating Against DB");

								/**** Validate modified adult guest count ****/
								if (WSAssert.assertEquals(adultCount, dbResults.get("ADULTS"), true)) {
									WSClient.writeToReport(LogStatus.PASS, " Adult Guest Count -> Expected : "
											+ adultCount + "  Actual : " + dbResults.get("ADULTS") + "");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, " Adult Guest Count -> Expected : "
											+ adultCount + "  Actual : " + dbResults.get("ADULTS") + "");
								}

								/**** Validate modified child guest count ****/
								if (WSAssert.assertEquals(childCount, dbResults.get("CHILDREN"), true)) {
									WSClient.writeToReport(LogStatus.PASS, " Child Guest Count -> Expected : "
											+ childCount + "  Actual : " + dbResults.get("CHILDREN") + "");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, " Child Guest Count -> Expected : "
											+ childCount + "  Actual : " + dbResults.get("CHILDREN") + "");
								}

							} else if (WSAssert.assertIfElementExists(createBookingRes,
									"CreateBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, " The error code that is generated is : "
										+ WSClient.getElementValue(createBookingRes,
												"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "");
							}
							if (WSAssert.assertIfElementExists(createBookingRes,
									"CreateBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										" The error code that is generated is : "
												+ WSClient.getElementValue(createBookingRes,
														"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "");
							}
							if (WSAssert.assertIfElementExists(createBookingRes, "Result_Text_TextElement", true)) {
								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(createBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message + "");
							}
						}

					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_invalidEmail() {
		try {
			resv = null;

			String testName = "createBooking_invalidEmail";
			WSClient.startTest(testName,
					"Verify that booking is not created when length of an email id exceeds 2000 characters",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_557}") + "T06:00:00");
			WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_559}") + "T06:00:00");
			WSClient.setData("{var_time}", "09:00:00");

			WSClient.setData("{var_email}", WSClient.getKeywordData("{KEYWORD_RANDSTR_2000}") + "@gmail.com");
			// WSClient.setData("{var_rate}", "CKRC");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
					OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
			fetchAvailability(rate, rt);

			String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_41");
			String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
			if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
					"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {

				if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text",
						true)) {
					WSClient.writeToReport(LogStatus.INFO,
							"<b>Create Booking Response gives the error message-----></b> " + WSClient.getElementValue(
									updateProfileResponseXML, "CreateBookingResponse_Result_Text", XMLType.RESPONSE));
				}

			} else

				WSClient.writeToReport(LogStatus.FAIL, "Create Booking fails");

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			try {
				if (resv != null)

					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_channelsellLimitchk() {
		try {
			resv = null;

			String testName = "createBooking_3839512";
			WSClient.startTest(testName,
					"Verify that booking is created when the number of units EQUAL the channel sell limit for the date range",
					"minimumRegression");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			// WSClient.setData("{var_startDate}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_166}"));
			// WSClient.setData("{var_endDate}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_167}"));
			// WSClient.setData("{var_busdate11}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_166}"));
			// WSClient.setData("{var_busdate12}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_167}"));
			fetchAvailability(rate, rt);
			WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_startDate}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_endDate}", WSClient.getData("{var_busdate1}").substring(0, 10));

			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_roomLimit}", "2");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Setting the Channel Sell Limit to 3<b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {

				String updateProfileReq1 = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_04");
				String updateProfileResponseXML1 = WSClient.processSOAPMessage(updateProfileReq1);
				if (WSAssert.assertIfElementExists(updateProfileResponseXML1,
						"SetChannelSellLimitsByDateRangeRS_Success", true)) {
					String updateProfileReq4 = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_03");
					String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML4, "FetchChannelSellLimitsRS_Success",
							true)) {
						WSClient.setData("{var_id}", WSClient.getElementValue(updateProfileResponseXML4,
								"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
					}

					profileID = CreateProfile.createProfile("DS_00");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);

						/*******************
						 * Prerequisite 2:Create a Reservation
						 ************************/

						WSClient.setData("{var_profileSource}", interfaceName);
						WSClient.setData("{var_resort}", resortOperaValue);
						WSClient.setData("{var_extResort}", resortExtValue);
						// WSClient.setData("{var_busdate}",
						// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_166}")
						// + "T14:00:00+05:30");
						// WSClient.setData("{var_busdate1}",
						// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_167}")
						// + "T14:00:00+05:30");
						fetchAvailability(rate, rt);
						WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
						WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
						WSClient.setData("{var_time}", "09:00:00");
						fetchAvailability(rate, rt);

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),

								OWSLib.getChannelCarier(resortOperaValue, interfaceName));
						WSClient.writeToReport(LogStatus.INFO, "<b>Creating a Booking with 2 units<b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Booking with rate code " + rate + "<b>");

						String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_23");
						String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							if (WSAssert.assertIfElementExists(updateProfileResponseXML,
									"CreateBookingResponse_Result_Text", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Create Booking Response gives the error message-----></b> "
												+ WSClient.getElementValue(updateProfileResponseXML,
														"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
							}

							// validating the data

							String elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
							String elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
							resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							WSClient.setData("{var_resvId}",
									WSClient.getElementValueByAttribute(updateProfileResponseXML,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
									profileID, false);
							elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);

							elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							String resv1 = WSClient.getElementValue(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
							WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
							WSClient.writeToReport(LogStatus.INFO, "<b> Validating the records in DB </b>");
							String query = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> bookingDets = WSClient.getDBRow(query);
							String query1 = WSClient.getQuery("QS_03");
							LinkedHashMap<String, String> bookingDets1 = WSClient.getDBRow(query1);
							if (WSAssert.assertEquals(bookingDets.get("CONFIRMATION_NO"),
									bookingDets1.get("CONFIRMATION_NO"), true))
								WSClient.writeToReport(LogStatus.PASS,
										"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
												+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));
							else
								WSClient.writeToReport(LogStatus.FAIL,
										"Conirmation No-> Expected value:" + bookingDets.get("CONFIRMATION_NO")
												+ " Actual value :" + bookingDets1.get("CONFIRMATION_NO"));

							if (WSAssert.assertEquals(bookingDets.get("RESV_NAME_ID"), bookingDets1.get("RESV_NAME_ID"),
									true))
								WSClient.writeToReport(LogStatus.PASS,
										"Reservation ID -> Expected value:" + bookingDets.get("RESV_NAME_ID")
												+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));
							else
								WSClient.writeToReport(LogStatus.FAIL,
										"Reservation ID-> Expected value:" + bookingDets.get("RESV_NAME_ID")
												+ " Actual value :" + bookingDets1.get("RESV_NAME_ID"));

							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Confirmation No</b>");

							WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", bookingDets.get("CONFIRMATION_NO"),
									false);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Reservation No</b>");

							if (WSAssert.assertEquals(resv, bookingDets.get("RESV_NAME_ID"), true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected : " + resv + " Actual : " + bookingDets.get("RESV_NAME_ID"));

						} else

							WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

						rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_03");
						WSClient.setData("{var_rate}", rate);
						rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_02");
						WSClient.setData("{var_roomType}", rt);
						ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_06");
						WSClient.setData("{var_orate}", ort);
						resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
						WSClient.setData("{var_resvType}", resvt);
						WSClient.setData("{var_reservationType}", resvt);
						WSClient.writeToReport(LogStatus.INFO, "<b>Creating a Booking with 1 unit<b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Booking with rate code " + rate + "<b>");
						String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);

						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML3,
								"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
							if (WSAssert.assertIfElementExists(updateProfileResponseXML3,
									"CreateBookingResponse_Result_OperaErrorCode", false)) {
								WSClient.writeToReport(LogStatus.PASS,
										"<b>Create Booking Response gives the error message-----></b> "
												+ WSClient.getElementValue(updateProfileResponseXML3,
														"CreateBookingResponse_Result_OperaErrorCode",
														XMLType.RESPONSE));
							}

						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Create Booking Response does not give an error message when sell limit is set");
						String updateProfileReq5 = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
						String updateProfileResponseXML5 = WSClient.processSOAPMessage(updateProfileReq5);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML5,
								"RemoveChannelSellLimitsRS_Success", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b> Removed the limits set on the channel set limits </b>");
						}
					}
				} else
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite --> Set Channel Sell Limit blocked");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}


//	Need to debug later
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	public void createBooking_channelsellLimitchkPass() {
		try {
			resv = null;

			String testName = "createBooking_1338395";
			WSClient.startTest(testName,
					"Verify that booking is created when the number of units DO NOT EXCEED the channel sell limit for the date range",
					"minimumRegression");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			// WSClient.setData("{var_startDate}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_166}"));
			// WSClient.setData("{var_endDate}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_167}"));
			// WSClient.setData("{var_busdate11}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_166}"));
			// WSClient.setData("{var_busdate12}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_167}"));
			fetchAvailability(rate, rt);
			WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_startDate}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_endDate}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_roomLimit}", "2");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Setting the Channel Sell Limit to 3<b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {

				String updateProfileReq1 = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_04");
				String updateProfileResponseXML1 = WSClient.processSOAPMessage(updateProfileReq1);
				if (WSAssert.assertIfElementExists(updateProfileResponseXML1,
						"SetChannelSellLimitsByDateRangeRS_Success", true)) {
					String updateProfileReq4 = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_03");
					String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML4, "FetchChannelSellLimitsRS_Success",
							true)) {
						WSClient.setData("{var_id}", WSClient.getElementValue(updateProfileResponseXML4,
								"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
					}

					profileID = CreateProfile.createProfile("DS_00");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);

						/*******************
						 * Prerequisite 2:Create a Reservation
						 ************************/

						WSClient.setData("{var_profileSource}", interfaceName);
						WSClient.setData("{var_resort}", resortOperaValue);
						WSClient.setData("{var_extResort}", resortExtValue);
						// WSClient.setData("{var_busdate}",
						// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_166}")
						// + "T14:00:00+05:30");
						// WSClient.setData("{var_busdate1}",
						// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_167}")
						// + "T14:00:00+05:30");
						WSClient.setData("{var_time}", "09:00:00");

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),

								OWSLib.getChannelCarier(resortOperaValue, interfaceName));
						WSClient.writeToReport(LogStatus.INFO, "<b>Creating a Booking with 1 unit<b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Booking with rate code " + rate + "<b>");

						String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							if (WSAssert.assertIfElementExists(updateProfileResponseXML,
									"CreateBookingResponse_Result_Text", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Create Booking Response gives the error message-----></b> "
												+ WSClient.getElementValue(updateProfileResponseXML,
														"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
							}

							// validating the data

							String elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
							String elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
							resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							WSClient.setData("{var_resvId}",
									WSClient.getElementValueByAttribute(updateProfileResponseXML,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
									profileID, false);
							elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);

							elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							String resv1 = WSClient.getElementValue(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
							WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));

						} else

							WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

						rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_03");
						WSClient.setData("{var_rate}", rate);
						rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_02");
						WSClient.setData("{var_roomType}", rt);
						ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_06");
						WSClient.setData("{var_orate}", ort);
						resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
						WSClient.setData("{var_resvType}", resvt);
						WSClient.setData("{var_reservationType}", resvt);
						WSClient.writeToReport(LogStatus.INFO, "<b>Creating a Booking with 1 unit<b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Booking with rate code " + rate + "<b>");
						String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);

						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							if (WSAssert.assertIfElementExists(updateProfileResponseXML,
									"CreateBookingResponse_Result_Text", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Create Booking Response gives the error message-----></b> "
												+ WSClient.getElementValue(updateProfileResponseXML,
														"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
							}

							// validating the data

							String elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
							String elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
							resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							WSClient.setData("{var_resvId}",
									WSClient.getElementValueByAttribute(updateProfileResponseXML,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
									profileID, false);
							elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);

							elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							String resv1 = WSClient.getElementValue(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
							WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Create Booking Response  gives an error message when sell limit is not exceeded");
						String updateProfileReq5 = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
						String updateProfileResponseXML5 = WSClient.processSOAPMessage(updateProfileReq5);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML5,
								"RemoveChannelSellLimitsRS_Success", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b> Removed the limits set on the channel set limits </b>");
						}
					}
				} else
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite --> Set Channel Sell Limit blocked");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking", "OWS",
	// "Reservation" })

	public void createBooking_channelsellLimitchkPassDays() {
		try {
			resv = null;

			String testName = "createBooking_38395";
			WSClient.startTest(testName,
					"Verify that booking is created when the number of units EXCEED the channel sell limit for a day within the date range ",
					"minimumRegression");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_346}"));
			WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_352}"));
			WSClient.setData("{var_busdate11}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_345}"));
			WSClient.setData("{var_busdate12}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_352}"));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_roomLimit}", "2");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Setting the Channel Sell Limit to 3<b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {

				String updateProfileReq1 = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_04");
				String updateProfileResponseXML1 = WSClient.processSOAPMessage(updateProfileReq1);
				if (WSAssert.assertIfElementExists(updateProfileResponseXML1,
						"SetChannelSellLimitsByDateRangeRS_Success", true)) {
					String updateProfileReq4 = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_03");
					String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML4, "FetchChannelSellLimitsRS_Success",
							true)) {
						WSClient.setData("{var_id}", WSClient.getElementValue(updateProfileResponseXML4,
								"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
					}

					profileID = CreateProfile.createProfile("DS_00");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);

						/*******************
						 * Prerequisite 2:Create a Reservation
						 ************************/

						WSClient.setData("{var_profileSource}", interfaceName);
						WSClient.setData("{var_resort}", resortOperaValue);
						WSClient.setData("{var_extResort}", resortExtValue);
						WSClient.setData("{var_busdate}",
								WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_346}") + "T14:00:00+05:30");
						WSClient.setData("{var_busdate1}",
								WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_347}") + "T14:00:00+05:30");
						WSClient.setData("{var_time}", "09:00:00");

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),

								OWSLib.getChannelCarier(resortOperaValue, interfaceName));
						WSClient.writeToReport(LogStatus.INFO, "<b>Creating a Booking with 1 unit<b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Booking with rate code " + rate + "<b>");

						String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							if (WSAssert.assertIfElementExists(updateProfileResponseXML,
									"CreateBookingResponse_Result_Text", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Create Booking Response gives the error message-----></b> "
												+ WSClient.getElementValue(updateProfileResponseXML,
														"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
							}

							// validating the data

							String elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
							String elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
							resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							WSClient.setData("{var_resvId}",
									WSClient.getElementValueByAttribute(updateProfileResponseXML,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID",
									profileID, false);
							elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);

							elem = WSClient.getElementValue(updateProfileResponseXML,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
							elem1 = WSClient.getElementValue(updateProfileReq,
									"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

							if (WSAssert.assertEquals(elem, elem1, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Expected value:" + elem + " Actual value :" + elem1);
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Expected value:" + elem + " Actual value :" + elem1);
							String resv1 = WSClient.getElementValue(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
							WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
									"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));

						} else

							WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

						rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_03");
						WSClient.setData("{var_rate}", rate);
						rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_02");
						WSClient.setData("{var_roomType}", rt);
						ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_06");
						WSClient.setData("{var_orate}", ort);
						resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
						WSClient.setData("{var_resvType}", resvt);
						WSClient.setData("{var_reservationType}", resvt);
						WSClient.writeToReport(LogStatus.INFO, "<b>Creating a Booking with 2 units<b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Booking with rate code " + rate + "<b>");
						String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_23");
						String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);

						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
							if (WSAssert.assertIfElementExists(updateProfileResponseXML,
									"CreateBookingResponse_Result_Text", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Create Booking Response gives the error message-----></b> "
												+ WSClient.getElementValue(updateProfileResponseXML,
														"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
							}

						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Create Booking Response does not give an error message when sell limit is exceeded");
						String updateProfileReq5 = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
						String updateProfileResponseXML5 = WSClient.processSOAPMessage(updateProfileReq5);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML5,
								"RemoveChannelSellLimitsRS_Success", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b> Removed the limits set on the channel set limits </b>");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_channelsellLimitchkPassDaysNegative() {
		try {
			resv = null;

			String testName = "createBooking_3138395";
			WSClient.startTest(testName,
					"Verify that booking is created when the number of units equal the channel sell limit",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			// WSClient.setData("{var_startDate}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_346}"));
			// WSClient.setData("{var_endDate}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_352}"));
			// WSClient.setData("{var_busdate11}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_345}"));
			// WSClient.setData("{var_busdate12}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_352}"));
			fetchAvailability(rate, rt);
			WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_startDate}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_endDate}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_roomLimit}", "2");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Setting the Channel Sell Limit to 2<b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {

				String updateProfileReq1 = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_04");
				String updateProfileResponseXML1 = WSClient.processSOAPMessage(updateProfileReq1);
				if (WSAssert.assertIfElementExists(updateProfileResponseXML1,
						"SetChannelSellLimitsByDateRangeRS_Success", true)) {
					String updateProfileReq4 = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_03");
					String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML4, "FetchChannelSellLimitsRS_Success",
							true)) {
						WSClient.setData("{var_id}", WSClient.getElementValue(updateProfileResponseXML4,
								"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
					}

					profileID = CreateProfile.createProfile("DS_00");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);

						/*******************
						 * Prerequisite 2:Create a Reservation
						 ************************/

						WSClient.setData("{var_profileSource}", interfaceName);
						WSClient.setData("{var_resort}", resortOperaValue);
						WSClient.setData("{var_extResort}", resortExtValue);
						// WSClient.setData("{var_busdate}",
						// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_346}")
						// + "T14:00:00+05:30");
						// WSClient.setData("{var_busdate1}",
						// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_347}")
						// + "T14:00:00+05:30");
						WSClient.setData("{var_time}", "09:00:00");

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),

								OWSLib.getChannelCarier(resortOperaValue, interfaceName));
						WSClient.writeToReport(LogStatus.INFO, "<b>Creating a Booking with 2 units<b>");
						WSClient.writeToReport(LogStatus.INFO, "<b>Booking with rate code " + rate + "<b>");

						String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_23");
						String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
						if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
								"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
							if (WSAssert.assertIfElementExists(updateProfileResponseXML,
									"CreateBookingResponse_Result_Text", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Create Booking Response gives the error message-----></b> "
												+ WSClient.getElementValue(updateProfileResponseXML,
														"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
							}

							// validating the data

						} else
							WSClient.writeToReport(LogStatus.FAIL,
									"Create Booking Response  gives an error message when sell limit is not exceeded");
						String updateProfileReq5 = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
						String updateProfileResponseXML5 = WSClient.processSOAPMessage(updateProfileReq5);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML5,
								"RemoveChannelSellLimitsRS_Success", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b> Removed the limits set on the channel set limits </b>");
						}
					}
				} else
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite --> Set Channel Sell Limit blocked");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_channelsellLimitchkPassOverrideY() {
		try {
			resv = null;

			String testName = "createBooking_3831395";
			WSClient.startTest(testName,
					"Verify that booking is created when the number of units equal the channel sell limit for the date range when OVERRIDE parameter is SET",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			fetchAvailability(rate, rt);
			WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_startDate}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_endDate}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_roomLimit}", "2");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Setting the Channel Sell Limit to 2<b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {
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

					String updateProfileReq1 = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_04");
					String updateProfileResponseXML1 = WSClient.processSOAPMessage(updateProfileReq1);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML1,
							"SetChannelSellLimitsByDateRangeRS_Success", true)) {
						String updateProfileReq4 = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_03");
						String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML4,
								"FetchChannelSellLimitsRS_Success", true)) {
							WSClient.setData("{var_id}", WSClient.getElementValue(updateProfileResponseXML4,
									"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
						}

						profileID = CreateProfile.createProfile("DS_00");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);

							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							WSClient.setData("{var_profileSource}", interfaceName);
							WSClient.setData("{var_resort}", resortOperaValue);
							WSClient.setData("{var_extResort}", resortExtValue);
							// WSClient.setData("{var_busdate}",
							// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_166}")
							// + "T14:00:00+05:30");
							// WSClient.setData("{var_busdate1}",
							// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_167}")
							// + "T14:00:00+05:30");
							WSClient.setData("{var_time}", "09:00:00");

							OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
									OWSLib.getChannelType(interfaceName),

									OWSLib.getChannelCarier(resortOperaValue, interfaceName));
							WSClient.writeToReport(LogStatus.INFO, "<b>Creating a Booking with 1 unit<b>");
							WSClient.writeToReport(LogStatus.INFO, "<b>Booking with rate code " + rate + "<b>");

							String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
							String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
							if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								if (WSAssert.assertIfElementExists(updateProfileResponseXML,
										"CreateBookingResponse_Result_Text", true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Create Booking Response gives the error message-----></b> "
													+ WSClient.getElementValue(updateProfileResponseXML,
															"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
								}

								// validating the data

								String elem = WSClient.getElementValue(updateProfileResponseXML,
										"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
								String elem1 = WSClient.getElementValue(updateProfileReq,
										"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
								resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}",
										WSClient.getElementValueByAttribute(updateProfileResponseXML,
												"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);
								WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
										"Profile_ProfileIDs_UniqueID", profileID, false);
								elem = WSClient.getElementValue(updateProfileResponseXML,
										"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
								elem1 = WSClient.getElementValue(updateProfileReq,
										"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);

								elem = WSClient.getElementValue(updateProfileResponseXML,
										"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
								elem1 = WSClient.getElementValue(updateProfileReq,
										"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);
								String resv1 = WSClient.getElementValue(updateProfileResponseXML,
										"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
								WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
										"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));

							} else

								WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

							rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_03");
							WSClient.setData("{var_rate}", rate);
							rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_02");
							WSClient.setData("{var_roomType}", rt);
							ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_06");
							WSClient.setData("{var_orate}", ort);
							resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
							WSClient.setData("{var_resvType}", resvt);
							WSClient.setData("{var_reservationType}", resvt);
							WSClient.writeToReport(LogStatus.INFO, "<b>Creating a Booking with 1 unit<b>");
							WSClient.writeToReport(LogStatus.INFO, "<b>Booking with rate code " + rate + "<b>");
							String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
							String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);

							if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								if (WSAssert.assertIfElementExists(updateProfileResponseXML,
										"CreateBookingResponse_Result_Text", true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Create Booking Response gives the error message-----></b> "
													+ WSClient.getElementValue(updateProfileResponseXML,
															"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
								}

								// validating the data

								String elem = WSClient.getElementValue(updateProfileResponseXML,
										"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
								String elem1 = WSClient.getElementValue(updateProfileReq,
										"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
								resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}",
										WSClient.getElementValueByAttribute(updateProfileResponseXML,
												"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);
								WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
										"Profile_ProfileIDs_UniqueID", profileID, false);
								elem = WSClient.getElementValue(updateProfileResponseXML,
										"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
								elem1 = WSClient.getElementValue(updateProfileReq,
										"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);

								elem = WSClient.getElementValue(updateProfileResponseXML,
										"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
								elem1 = WSClient.getElementValue(updateProfileReq,
										"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);
								String resv1 = WSClient.getElementValue(updateProfileResponseXML,
										"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
								WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
										"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Create Booking Response  gives an error message when sell limit is not exceeded");
							String updateProfileReq5 = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
							String updateProfileResponseXML5 = WSClient.processSOAPMessage(updateProfileReq5);
							if (WSAssert.assertIfElementExists(updateProfileResponseXML5,
									"RemoveChannelSellLimitsRS_Success", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> Removed the limits set on the channel set limits </b>");
							}
							WSClient.setData("{var_par}", "N");
							WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
							WSClient.setData("{var_param}", "OVERRIDE_YN");
							WSClient.setData("{var_type}", "Boolean");
							paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
							WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
							if (!paramValue.equals("N")) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
								paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01",
										"OVERRIDE_YN");
								WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
							}

							if (paramValue.equals("N")) {
								WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN is unset</b>");

							}
						}
					} else
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite --> Set Channel Sell Limit blocked");

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_channelsellLimitchkPassOverrideYGreater() {
		try {
			resv = null;
			resv1 = null;
			String testName = "createBooking_3839135";
			WSClient.startTest(testName,
					"Verify that booking is created when the number of units exceed the channel sell limit for the date range when OVERRIDE parameter is SET",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			// WSClient.setData("{var_roomType}", "4RT");
			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			// WSClient.setData("{var_startDate}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_166}"));
			// WSClient.setData("{var_endDate}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_167}"));
			// WSClient.setData("{var_busdate11}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_166}"));
			// WSClient.setData("{var_busdate12}",
			// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_167}"));
			fetchAvailability(rate, rt);
			WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_startDate}", WSClient.getData("{var_busdate}").substring(0, 10));
			WSClient.setData("{var_endDate}", WSClient.getData("{var_busdate1}").substring(0, 10));
			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_roomLimit}", "2");
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			WSClient.writeToReport(LogStatus.INFO, "<b>Setting the Channel Sell Limit to 2<b>");
			String updateProfileReq2 = WSClient.createSOAPMessage("ChangeChannelParameters", "DS_01");
			String updateProfileResponseXML2 = WSClient.processSOAPMessage(updateProfileReq2);
			if (WSAssert.assertIfElementExists(updateProfileResponseXML2, "ChangeChannelParametersRS_Success", true)) {
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

					String updateProfileReq1 = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_04");
					String updateProfileResponseXML1 = WSClient.processSOAPMessage(updateProfileReq1);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML1,
							"SetChannelSellLimitsByDateRangeRS_Success", true)) {
						String updateProfileReq4 = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_03");
						String updateProfileResponseXML4 = WSClient.processSOAPMessage(updateProfileReq4);
						if (WSAssert.assertIfElementExists(updateProfileResponseXML4,
								"FetchChannelSellLimitsRS_Success", true)) {
							WSClient.setData("{var_id}", WSClient.getElementValue(updateProfileResponseXML4,
									"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
						}

						profileID = CreateProfile.createProfile("DS_00");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);

							/*******************
							 * Prerequisite 2:Create a Reservation
							 ************************/

							WSClient.setData("{var_profileSource}", interfaceName);
							WSClient.setData("{var_resort}", resortOperaValue);
							WSClient.setData("{var_extResort}", resortExtValue);
							// WSClient.setData("{var_busdate}",
							// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_166}")
							// + "T14:00:00+05:30");
							// WSClient.setData("{var_busdate1}",
							// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_167}")
							// + "T14:00:00+05:30");
							WSClient.setData("{var_time}", "09:00:00");

							OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
									OWSLib.getChannelType(interfaceName),

									OWSLib.getChannelCarier(resortOperaValue, interfaceName));
							WSClient.writeToReport(LogStatus.INFO, "<b>Creating a Booking with 2 units<b>");
							WSClient.writeToReport(LogStatus.INFO, "<b>Booking with rate code " + rate + "<b>");

							String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_23");
							String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
							if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								if (WSAssert.assertIfElementExists(updateProfileResponseXML,
										"CreateBookingResponse_Result_Text", true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Create Booking Response gives the error message-----></b> "
													+ WSClient.getElementValue(updateProfileResponseXML,
															"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
								}

								// validating the data

								String elem = WSClient.getElementValue(updateProfileResponseXML,
										"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
								String elem1 = WSClient.getElementValue(updateProfileReq,
										"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
								resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}",
										WSClient.getElementValueByAttribute(updateProfileResponseXML,
												"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);
								WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
										"Profile_ProfileIDs_UniqueID", profileID, false);
								elem = WSClient.getElementValue(updateProfileResponseXML,
										"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
								elem1 = WSClient.getElementValue(updateProfileReq,
										"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);

								elem = WSClient.getElementValue(updateProfileResponseXML,
										"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
								elem1 = WSClient.getElementValue(updateProfileReq,
										"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);
								String resv1 = WSClient.getElementValue(updateProfileResponseXML,
										"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
								WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
										"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));

							} else

								WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

							rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_03");
							WSClient.setData("{var_rate}", rate);
							rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_02");
							WSClient.setData("{var_roomType}", rt);
							ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_06");
							WSClient.setData("{var_orate}", ort);
							resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
							WSClient.setData("{var_resvType}", resvt);
							WSClient.setData("{var_reservationType}", resvt);
							WSClient.writeToReport(LogStatus.INFO, "<b>Creating a Booking with 2 units<b>");
							WSClient.writeToReport(LogStatus.INFO, "<b>Booking with rate code " + rate + "<b>");
							String updateProfileReq3 = WSClient.createSOAPMessage("OWSCreateBooking", "DS_23");
							String updateProfileResponseXML3 = WSClient.processSOAPMessage(updateProfileReq3);

							if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								if (WSAssert.assertIfElementExists(updateProfileResponseXML,
										"CreateBookingResponse_Result_Text", true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Create Booking Response gives the error message-----></b> "
													+ WSClient.getElementValue(updateProfileResponseXML,
															"CreateBookingResponse_Result_Text", XMLType.RESPONSE));
								}

								// validating the data

								String elem = WSClient.getElementValue(updateProfileResponseXML,
										"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
								String elem1 = WSClient.getElementValue(updateProfileReq,
										"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
								resv = WSClient.getElementValueByAttribute(updateProfileResponseXML,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
								WSClient.setData("{var_resvId}",
										WSClient.getElementValueByAttribute(updateProfileResponseXML,
												"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);
								WSAssert.assertIfElementValueEquals(updateProfileResponseXML,
										"Profile_ProfileIDs_UniqueID", profileID, false);
								elem = WSClient.getElementValue(updateProfileResponseXML,
										"RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
								elem1 = WSClient.getElementValue(updateProfileReq,
										"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Room Type Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);

								elem = WSClient.getElementValue(updateProfileResponseXML,
										"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE);
								elem1 = WSClient.getElementValue(updateProfileReq,
										"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating Guarantee Type Code</b>");

								if (WSAssert.assertEquals(elem, elem1, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Expected value:" + elem + " Actual value :" + elem1);
								} else
									WSClient.writeToReport(LogStatus.FAIL,
											"Expected value:" + elem + " Actual value :" + elem1);
								String resv1 = WSClient.getElementValue(updateProfileResponseXML,
										"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
								WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML,
										"HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));
							} else
								WSClient.writeToReport(LogStatus.FAIL,
										"Create Booking Response  gives an error message when sell limit is not exceeded");
							String updateProfileReq5 = WSClient.createSOAPMessage("RemoveChannelSellLimits", "DS_01");
							String updateProfileResponseXML5 = WSClient.processSOAPMessage(updateProfileReq5);
							if (WSAssert.assertIfElementExists(updateProfileResponseXML5,
									"RemoveChannelSellLimitsRS_Success", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> Removed the limits set on the channel set limits </b>");
							}
							WSClient.setData("{var_par}", "N");
							WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
							WSClient.setData("{var_param}", "OVERRIDE_YN");
							WSClient.setData("{var_type}", "Boolean");
							paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
							WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
							if (!paramValue.equals("N")) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter OVERRIDE_YN</b>");
								paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01",
										"OVERRIDE_YN");
								WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
							}

							if (paramValue.equals("N")) {
								WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN is unset</b>");

							}
						}
					} else
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite --> Set Channel Sell Limit blocked");

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		} finally {
			if (resv != null)
				try {
					WSClient.setData("{var_resvId}", resv);

					CancelReservation.cancelReservation("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (resv1 != null)
				try {
					WSClient.setData("{var_resvId}", resv1);
					CancelReservation.cancelReservation("DS_02");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}
	}

	
//	Need to debug Later
//	@Test(groups = { "minimumRegression", "CreateBooking", "Reservation", "OWS", "PriorRun" })
	public void createBooking_22243_adultGuest() {
		try {
			String testName = "createBooking_22243";
			WSClient.startTest(testName,
					"Verify that error message is displayed when ADULT Guest count exceeds the set limit.",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
			WSClient.setData("{var_par}", "N");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_time}", "09:00:00");
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
								OperaPropConfig.getDataSetForCode("ReservationType", "DS_11"));
						String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05");
						WSClient.setData("{var_rate}", rate);
						String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04");
						WSClient.setData("{var_roomType}", rt);
						String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_08");
						WSClient.setData("{var_orate}", ort);
						String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
						WSClient.setData("{var_resvType}", resvt);
						WSClient.setData("{var_reservationType}", resvt);

						String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}");
						String endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_4}");
						WSClient.setData("{var_busdate}", startDate);
						WSClient.setData("{var_busdate1}", endDate);
						fetchAvailability(rate, rt);

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
						/***************
						 * OWS Modify Booking Operation
						 ********************/
						// Set Data Sheet Values
						WSClient.setData("{var_age1}", "");
						WSClient.setData("{var_age2}", "");
						WSClient.setData("{var_ageGroup1}", "ADULT");
						WSClient.setData("{var_ageGroup2}", "");
						WSClient.setData("{var_count1}", "7");
						WSClient.setData("{var_count2}", "");
						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
						WSClient.setData("{var_roomCount}", "1");

						String modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_43");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false);
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
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
		}
	}

//	Need to debug later
//	@Test(groups = { "minimumRegression", "CreateBooking", "Reservation", "OWS", "PriorRun" })
	public void createBooking_22247() {
		try {
			String testName = "createBooking_22247";
			WSClient.startTest(testName,
					"Verify that Error message is displayed when Guest count (Total OCCUPANTS) EXCEEDS the limit set",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.writeToReport(LogStatus.INFO, "<b>ROOM TYPE OCCUPANCY LIMIT is 6</b>");
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is disabled</b>");
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

						String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05");
						WSClient.setData("{var_rate}", rate);
						String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04");
						WSClient.setData("{var_roomType}", rt);
						String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_08");
						WSClient.setData("{var_orate}", ort);
						String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_02");
						WSClient.setData("{var_resvType}", resvt);
						WSClient.setData("{var_reservationType}", resvt);
						fetchAvailability(rate, rt);

						String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_12}");
						String endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}");
						WSClient.setData("{var_startDate}", WSClient.getData("{var_busdate}"));
						WSClient.setData("{var_endDate}", WSClient.getData("{var_busdate1}"));
						WSClient.setData("{var_busdate}", startDate);
						WSClient.setData("{var_busdate1}", endDate);
						WSClient.setData("{var_time}", "09:00:00");

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
						/***************
						 * OWS Modify Booking Operation
						 ********************/
						// Set Data Sheet Values
						WSClient.setData("{var_age1}", "");
						WSClient.setData("{var_age2}", "10");
						WSClient.setData("{var_ageGroup1}", "ADULT");
						WSClient.setData("{var_ageGroup2}", "CHILD");
						WSClient.setData("{var_count1}", "4");
						WSClient.setData("{var_count2}", "4");
						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
						WSClient.setData("{var_roomCount}", "1");
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Booking is created with 4 Adults and 4 children</b>");
						String interfaceName = OWSLib.getChannel();
						String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));

						String modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_43");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false);
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}

						}
					}

				} else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Pre-requisite failed >> Changing the channel parameter failed");
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "Reservation", "OWS", "PriorRun" })

	public void createBooking_22248() {
		try {
			String testName = "createBooking_22248";
			WSClient.startTest(testName,
					"Verify that error message is displayed when CHILD Guest count exceeds the limit set",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is disabled</b>");
			WSClient.setData("{var_par}", "N");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "<b>OVERRIDE_YN : " + paramValue + "</b>");
			if (!paramValue.equals("N")) {
				WSClient.writeToReport(LogStatus.INFO, "<b>Disbling the parameter OVERRIDE_YN</b>");
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
								OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_02"));
						String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05");
						WSClient.setData("{var_rate}", rate);
						String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04");
						WSClient.setData("{var_roomType}", rt);
						String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_08");
						WSClient.setData("{var_orate}", ort);
						String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_02");
						WSClient.setData("{var_resvType}", resvt);
						WSClient.setData("{var_reservationType}", resvt);
						String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_12}");
						String endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}");
						WSClient.setData("{var_startDate}", startDate);
						WSClient.setData("{var_endDate}", endDate);
						WSClient.setData("{var_busdate}", startDate);
						WSClient.setData("{var_busdate1}", endDate);
						WSClient.setData("{var_time}", "09:00:00");

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
						/***************
						 * OWS Modify Booking Operation
						 ********************/
						// Set Data Sheet Values
						WSClient.setData("{var_age1}", "");
						WSClient.setData("{var_age2}", "9");
						WSClient.setData("{var_ageGroup1}s", "ADULT");
						WSClient.setData("{var_ageGroup2}", "CHILD");
						WSClient.setData("{var_count1}", "");
						WSClient.setData("{var_count2}", "10");
						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
						WSClient.setData("{var_roomCount}", "1");
						String interfaceName = OWSLib.getChannel();
						String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortExtValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));

						String modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_31");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false);
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_GDSError", true)) {
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										"<b> The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
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
 
// Need to debug later
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	public void createBooking_23277() {
		try {
			String testName = "createBooking_23277";
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
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
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
				WSClient.setData("{var_ratecode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_11"));
				/************
				 * Prerequisite 1: Create profile
				 *********************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "Profile ID:" + profileID + "");
					WSClient.setData("{var_profileId}", profileID);

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/

					OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
					WSClient.setData("{var_rateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
					String rate = WSClient.getData("{var_rateCode}");

					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
					String rt = WSClient.getData("{var_roomType}");

					WSClient.setData("{var_roomCount}", "1");
					WSClient.setData("{var_amount}", "2");
					WSClient.setData("{var_type}", "PERCENT");
					WSClient.setData("{var_reason}", OperaPropConfig.getDataSetForCode("DiscountReasons", "DS_01"));
					WSClient.setData("{var_startDate}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_150}").substring(0, 19));
					WSClient.setData("{var_endDate}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_155}").substring(0, 19));
					WSClient.setData("{var_time}", "09:00:00");
					/***************
					 * OWS Modify Booking Operation
					 ********************/
					fetchAvailability(rate, rt);
					WSClient.setData("{var_startDate}", WSClient.getData("{var_busdate}").substring(0, 10));
					WSClient.setData("{var_endDate}", WSClient.getData("{var_busdate1}").substring(0, 10));
					String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_44");
					String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
					WSClient.setData("{var_rateCode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
					if (WSAssert.assertIfElementExists(createBookingRes, "CreateBookingResponse_Result", true)) {
						if (WSAssert.assertIfElementValueEquals(createBookingRes,
								"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							resv = WSClient.getElementValueByAttribute(createBookingRes,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(createBookingRes,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
							String query = WSClient.getQuery("QS_30");
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

							String reqDiscountType = WSClient.getElementValue(createBookingReq,
									"RatePlan_Discount_DiscountType", XMLType.REQUEST);

							String reqAmount = WSClient.getElementValue(createBookingReq,
									"RatePlan_Discount_DiscountAmount", XMLType.REQUEST);
							String reqReason = WSClient.getElementValue(createBookingReq,
									"RatePlan_Discount_DiscountReason", XMLType.REQUEST);

							query = WSClient.getQuery("QS_29");
							LinkedHashMap<String, String> results = WSClient.getDBRow(query);
							WSClient.writeToReport(LogStatus.INFO,
									"<>Validating discount details insertion into DB</b>");
							if (WSAssert.assertEquals(reqAmount, results.get("DISCOUNT_PRCNT"), true)) {
								WSClient.writeToReport(LogStatus.PASS, "DiscountPercent -> Expected : " + reqAmount
										+ "  Actual : " + results.get("DISCOUNT_PRCNT") + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "DiscountPercent -> Expected : " + reqAmount
										+ "  Actual : " + results.get("DISCOUNT_PRCNT") + "");
							}
							if (WSAssert.assertEquals(reqReason, results.get("DISCOUNT_REASON_CODE"), true)) {
								WSClient.writeToReport(LogStatus.PASS, "DiscountReason -> Expected : " + reqReason
										+ "  Actual : " + results.get("DISCOUNT_REASON_CODE") + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "DiscountReason -> Expected : " + reqReason
										+ "  Actual : " + results.get("DISCOUNT_REASON_CODE") + "");
							}

							String resDiscountType = WSClient.getElementValue(createBookingRes,
									"RatePlan_Discount_DiscountType", XMLType.RESPONSE);
							String resAmount = WSClient.getElementValue(createBookingRes,
									"RatePlan_Discount_DiscountAmount", XMLType.RESPONSE);
							String resReason = WSClient.getElementValue(createBookingRes,
									"RatePlan_Discount_DiscountReason", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Validating discount details retrieval onto the response</b>");
							if (WSAssert.assertEquals(reqAmount, resAmount, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"DiscountPercent -> Expected : " + reqAmount + "  Actual : " + resAmount + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"DiscountPercent -> Expected : " + reqAmount + "  Actual : " + resAmount + "");
							}
							if (WSAssert.assertEquals(reqReason, resReason, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"DiscountReason -> Expected : " + reqReason + "  Actual : " + resReason + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"DiscountReason -> Expected : " + reqReason + "  Actual : " + resReason + "");
							}
							if (WSAssert.assertEquals(reqDiscountType, resDiscountType, true)) {
								WSClient.writeToReport(LogStatus.PASS, "DiscountType -> Expected : " + reqDiscountType
										+ "  Actual : " + resDiscountType + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "DiscountType -> Expected : " + reqDiscountType
										+ "  Actual : " + resDiscountType + "");
							}

							WSClient.writeToReport(LogStatus.INFO, "Validating Charges of the reservation");
							String base = WSClient.getElementValue(createBookingRes, "Rates_Rate_Base",
									XMLType.RESPONSE);
							if (WSAssert.assertEquals(String.valueOf(amount), base, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Base -> Expected : " + String.valueOf(amount) + "  Actual : " + base + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Base -> Expected : " + String.valueOf(amount) + "  Actual : " + base + "");
							}
							String total = WSClient.getElementValue(createBookingRes, "RoomStays_RoomStay_Total",
									XMLType.RESPONSE);
							if (WSAssert.assertEquals(String.valueOf(amount), total, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Base -> Expected : " + String.valueOf(amount) + "  Actual : " + total + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Base -> Expected : " + String.valueOf(amount) + "  Actual : " + total + "");
							}
							total = WSClient.getElementValue(createBookingRes,
									"RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages", XMLType.RESPONSE);
							if (WSAssert.assertEquals(String.valueOf(amount), total, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Base -> Expected : " + String.valueOf(amount) + "  Actual : " + total + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Base -> Expected : " + String.valueOf(amount) + "  Actual : " + total + "");
							}
							total = WSClient.getElementValue(createBookingRes, "RoomRateAndPackages_Charges_Amount",
									XMLType.RESPONSE);
							if (WSAssert.assertEquals(String.valueOf(amount), total, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Base -> Expected : " + String.valueOf(amount) + "  Actual : " + total + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Base -> Expected : " + String.valueOf(amount) + "  Actual : " + total + "");
							}
						}
						if (WSAssert.assertIfElementExists(createBookingRes, "CreateBookingResponse_Result_GDSError",
								true)) {
							WSClient.writeToReport(LogStatus.INFO,
									" The error code that is generated is : "
											+ WSClient.getElementValue(createBookingRes,
													"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "");
						}
						if (WSAssert.assertIfElementExists(createBookingRes,
								"CreateBookingResponse_Result_OperaErrorCode", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									" The error code that is generated is : "
											+ WSClient.getElementValue(createBookingRes,
													"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "");
						}
						if (WSAssert.assertIfElementExists(createBookingRes, "Result_Text_TextElement", true)) {
							String message = WSAssert.getElementValue(createBookingRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message + "");
						}
					} else if (WSAssert.assertIfElementExists(createBookingRes, "CreateBookingResponse_faultstring",
							true)) {
						WSClient.writeToReport(LogStatus.FAIL,
								" The error that is generated is : " + WSClient.getElementValue(createBookingRes,
										"CreateBookingResponse_faultstring", XMLType.RESPONSE) + "");
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
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}
		}
	}

//  Need to debug later
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	public void createBooking_22277() {
		try {
			String testName = "createBooking_22277";
			WSClient.startTest(testName, "Verify that discount amount is applied to the reservation",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			OPERALib.setOperaHeader(uname);

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
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
				WSClient.setData("{var_ratecode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_11"));
				/************
				 * Prerequisite 1: Create profile
				 *********************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "Profile ID:" + profileID + "");
					WSClient.setData("{var_profileId}", profileID);

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/

					OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
					WSClient.setData("{var_rateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
					String rate = WSClient.getData("{var_rateCode}");
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
					String rt = WSClient.getData("{var_roomType}");

					WSClient.setData("{var_roomCount}", "1");
					WSClient.setData("{var_amount}", "10");
					WSClient.setData("{var_type}", "FLAT");
					WSClient.setData("{var_reason}", OperaPropConfig.getDataSetForCode("DiscountReasons", "DS_01"));
					WSClient.setData("{var_startDate}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_150}").substring(0, 19));
					WSClient.setData("{var_endDate}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_155}").substring(0, 19));
					WSClient.setData("{var_time}", "09:00:00");
					/***************
					 * OWS Modify Booking Operation
					 ********************/
					fetchAvailability(rate, rt);
					WSClient.setData("{var_startDate}", WSClient.getData("{var_busdate}").substring(0, 10));
					WSClient.setData("{var_endDate}", WSClient.getData("{var_busdate1}").substring(0, 10));
					String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_44");
					String createBookingRes = WSClient.processSOAPMessage(createBookingReq);
					WSClient.setData("{var_rateCode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
					if (WSAssert.assertIfElementExists(createBookingRes, "CreateBookingResponse_Result", true)) {
						if (WSAssert.assertIfElementValueEquals(createBookingRes,
								"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

							resv = WSClient.getElementValueByAttribute(createBookingRes,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(createBookingRes,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
							String query = WSClient.getQuery("QS_30");
							LinkedHashMap<String, String> prices = WSClient.getDBRow(query);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Details of Rate Code : " + prices.get("RATE_CODE") + "</b>");
							WSClient.writeToReport(LogStatus.INFO,
									"<b> Charge For Adult 1 -> " + prices.get("AMOUNT_1") + "  Adult 2 -> "
											+ prices.get("AMOUNT_2") + "  Adult 3 -> " + prices.get("AMOUNT_3")
											+ " Extra Adult -> " + prices.get("ADULT_CHARGE") + "</b>");

							int price = Integer.parseInt(prices.get("AMOUNT_1"));
							int amount = price - (Integer.parseInt(WSClient.getData("{var_amount}")));

							String reqDiscountType = WSClient.getElementValue(createBookingReq,
									"RatePlan_Discount_DiscountType", XMLType.REQUEST);

							String reqAmount = WSClient.getElementValue(createBookingReq,
									"RatePlan_Discount_DiscountAmount", XMLType.REQUEST);
							String reqReason = WSClient.getElementValue(createBookingReq,
									"RatePlan_Discount_DiscountReason", XMLType.REQUEST);

							query = WSClient.getQuery("QS_42");
							LinkedHashMap<String, String> results = WSClient.getDBRow(query);
							
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Validating discount details insertion into DB</b>");
							if (WSAssert.assertEquals(reqAmount, results.get("DISCOUNT_PRCNT"), true)) {
								WSClient.writeToReport(LogStatus.PASS, "DiscountPercent -> Expected : " + reqAmount
										+ "  Actual : " + results.get("DISCOUNT_PRCNT") + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "DiscountPercent -> Expected : " + reqAmount
										+ "  Actual : " + results.get("DISCOUNT_PRCNT") + "");
							}
							if (WSAssert.assertEquals(reqReason, results.get("DISCOUNT_REASON_CODE"), true)) {
								WSClient.writeToReport(LogStatus.PASS, "DiscountReason -> Expected : " + reqReason
										+ "  Actual : " + results.get("DISCOUNT_REASON_CODE") + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "DiscountReason -> Expected : " + reqReason
										+ "  Actual : " + results.get("DISCOUNT_REASON_CODE") + "");
							}

							String resDiscountType = WSClient.getElementValue(createBookingRes,
									"RatePlan_Discount_DiscountType", XMLType.RESPONSE);
							String resAmount = WSClient.getElementValue(createBookingRes,
									"RatePlan_Discount_DiscountAmount", XMLType.RESPONSE);
							String resReason = WSClient.getElementValue(createBookingRes,
									"RatePlan_Discount_DiscountReason", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>Validating discount details retrieval onto the response</b>");
							if (WSAssert.assertEquals(reqAmount, resAmount, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"DiscountPercent -> Expected : " + reqAmount + "  Actual : " + resAmount + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"DiscountPercent -> Expected : " + reqAmount + "  Actual : " + resAmount + "");
							}
							if (WSAssert.assertEquals(reqReason, resReason, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"DiscountReason -> Expected : " + reqReason + "  Actual : " + resReason + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"DiscountReason -> Expected : " + reqReason + "  Actual : " + resReason + "");
							}
							if (WSAssert.assertEquals(reqDiscountType, resDiscountType, true)) {
								WSClient.writeToReport(LogStatus.PASS, "DiscountType -> Expected : " + reqDiscountType
										+ "  Actual : " + resDiscountType + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL, "DiscountType -> Expected : " + reqDiscountType
										+ "  Actual : " + resDiscountType + "");
							}

							WSClient.writeToReport(LogStatus.INFO, "Validating Charges of the reservation");
							String base = WSClient.getElementValue(createBookingRes, "Rates_Rate_Base",
									XMLType.RESPONSE);
							if (WSAssert.assertEquals(String.valueOf(amount), base, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Base -> Expected : " + String.valueOf(amount) + "  Actual : " + base + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Base -> Expected : " + String.valueOf(amount) + "  Actual : " + base + "");
							}
							String total = WSClient.getElementValue(createBookingRes, "RoomStays_RoomStay_Total",
									XMLType.RESPONSE);
							if (WSAssert.assertEquals(String.valueOf(amount), total, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Base -> Expected : " + String.valueOf(amount) + "  Actual : " + total + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Base -> Expected : " + String.valueOf(amount) + "  Actual : " + total + "");
							}
							total = WSClient.getElementValue(createBookingRes,
									"RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages", XMLType.RESPONSE);
							if (WSAssert.assertEquals(String.valueOf(amount), total, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Base -> Expected : " + String.valueOf(amount) + "  Actual : " + total + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Base -> Expected : " + String.valueOf(amount) + "  Actual : " + total + "");
							}
							total = WSClient.getElementValue(createBookingRes, "RoomRateAndPackages_Charges_Amount",
									XMLType.RESPONSE);
							if (WSAssert.assertEquals(String.valueOf(amount), total, true)) {
								WSClient.writeToReport(LogStatus.PASS,
										"Base -> Expected : " + String.valueOf(amount) + "  Actual : " + total + "");
							} else {
								WSClient.writeToReport(LogStatus.FAIL,
										"Base -> Expected : " + String.valueOf(amount) + "  Actual : " + total + "");
							}
						}
						if (WSAssert.assertIfElementExists(createBookingRes, "CreateBookingResponse_Result_GDSError",
								true)) {
							WSClient.writeToReport(LogStatus.INFO,
									" The error code that is generated is : "
											+ WSClient.getElementValue(createBookingRes,
													"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "");
						}
						if (WSAssert.assertIfElementExists(createBookingRes,
								"CreateBookingResponse_Result_OperaErrorCode", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									" The error code that is generated is : "
											+ WSClient.getElementValue(createBookingRes,
													"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "");
						}
						if (WSAssert.assertIfElementExists(createBookingRes, "Result_Text_TextElement", true)) {
							String message = WSAssert.getElementValue(createBookingRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message + "");
						}
					} else if (WSAssert.assertIfElementExists(createBookingRes, "CreateBookingResponse_faultstring",
							true)) {
						WSClient.writeToReport(LogStatus.FAIL,
								" The error that is generated is : " + WSClient.getElementValue(createBookingRes,
										"CreateBookingResponse_faultstring", XMLType.RESPONSE) + "");
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
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "Reservation", "OWS" })

	public void createBooking_rateCodeExp() {

		try {
			String testName = "createBooking_22066";
			WSClient.startTest(testName,
					"Verify an Error message is displayed when  Booking is created beyond the expiry date range of the Rate Code",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

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
						OperaPropConfig.getDataSetForCode("ReservationType", "DS_02"));
				String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
				WSClient.setData("{var_rate}", rate);
				String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
				WSClient.setData("{var_roomType}", rt);
				String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
				WSClient.setData("{var_orate}", ort);
				String resvt = OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_01");
				WSClient.setData("{var_resvType}", resvt);
				WSClient.setData("{var_reservationType}", resvt);
				WSClient.setData("{var_time}", "09:00:00");

				/************ Prerequisite 1: Create profile ****************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_00");

				WSClient.setData("{var_profileId}", profileID);

				/****************
				 * Prerequisite 2:Create a Reservation
				 *****************/
				WSClient.writeToReport(LogStatus.INFO, "<b>Booking Date : Beyond the END DATE of Rate Code </b>");

				String query = WSClient.getQuery("OWSCreateBooking", "QS_31");
				LinkedHashMap<String, String> dates = WSClient.getDBRow(query);
				WSClient.setData("{var_startDate}", dates.get("START_DATE"));
				WSClient.setData("{var_endDate}", dates.get("END_DATE"));
				WSClient.setData("{var_busdate}", dates.get("START_DATE"));
				WSClient.setData("{var_busdate1}", dates.get("END_DATE"));

				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
				/***************
				 * OWS Modify Booking Operation
				 ********************/
				String modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_21");
				String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

				if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result", true)) {
					WSAssert.assertIfElementValueEquals(modifyBookingRes,
							"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false);

					// Errors Codes
					if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result_GDSError",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
					}
					if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result_OperaErrorCode",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
										+ "</b>");
					}

				} else if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_faultstring",
						true)) {
					WSClient.writeToReport(LogStatus.FAIL,
							"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
									"CreateBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	// using a rate Code which is expired.
	@Test(groups = { "minimumRegression", "CreateBooking", "Reservation", "OWS" })

	public void createBooking_roomTypeExp() {

		try {
			String testName = "createBooking_22066";
			WSClient.startTest(testName,
					"Verify an Error message is displayed when  Booking is created beyond the expiry date range of the room type",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

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
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_04"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_02"));
				String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_01");
				WSClient.setData("{var_rate}", rate);
				String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
				WSClient.setData("{var_roomType}", rt);
				String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_05");
				WSClient.setData("{var_orate}", ort);
				String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
				WSClient.setData("{var_resvType}", resvt);
				WSClient.setData("{var_reservationType}", resvt);
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

				/************ Prerequisite 1: Create profile ****************/

				profileID = CreateProfile.createProfile("DS_00");

				WSClient.setData("{var_profileId}", profileID);

				/****************
				 * Prerequisite 2:Create a Reservation
				 *****************/
				WSClient.writeToReport(LogStatus.INFO, "<b>Booking Date : Beyond the END DATE of Room Type </b>");

				String query = WSClient.getQuery("OWSCreateBooking", "QS_32");
				LinkedHashMap<String, String> dates = WSClient.getDBRow(query);
				WSClient.setData("{var_busdate}", dates.get("START_DATE"));
				WSClient.setData("{var_busdate1}", dates.get("END_DATE"));
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
				/***************
				 * OWS Modify Booking Operation
				 ********************/
				String modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_21");
				String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

				if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result", true)) {

					WSAssert.assertIfElementValueEquals(modifyBookingRes,
							"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false);

					// Errors Codes
					if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result_GDSError",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										+ "</b>");
					}
					if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result_OperaErrorCode",
							true)) {
						WSClient.writeToReport(LogStatus.INFO,
								"<b> The error code that is generated is : "
										+ WSClient.getElementValue(modifyBookingRes,
												"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
										+ "</b>");
					}
					if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
						/****
						 * Verifying that the error message is populated on the
						 * response
						 ******/

					}

				} else if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_faultstring",
						true)) {
					WSClient.writeToReport(LogStatus.FAIL,
							"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
									"CreateBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "Reservation", "OWS", "PriorRun" })
	public void createBooking_44551() {
		try {
			String testName = "createBooking_44551";
			WSClient.startTest(testName,
					"Verify that error should be generated when a day use booking is created and mimimum los is greater than zero",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "Verify if the parameter OVERRIDE_YN is enabled");
			WSClient.setData("{var_par}", "N");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			WSClient.setData("{var_time}", "09:00:00");

			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "OVERRIDE_YN : " + paramValue + "");
			if (!paramValue.equals("N")) {
				WSClient.writeToReport(LogStatus.INFO, "Disabling the parameter OVERRIDE_YN");
				paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "OVERRIDE_YN : " + paramValue + "");
			}

			if (paramValue.equals("N")) {
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "ReservationType" })) {
					/*************
					 * Prerequisite : Room type, Rate Plan Code, Source Code,
					 * Market Code
					 *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
					WSClient.setData("{var_rate}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
					WSClient.setData("{var_resvType}", resvt);
					WSClient.setData("{var_reservationType}", resvt);
					WSClient.setData("{var_startDate}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}") + "T09:00:00+05:30");
					WSClient.setData("{var_endDate}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}") + "T18:00:00+05:30");
					WSClient.setData("{var_busdate}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}") + "T09:00:00+05:30");
					WSClient.setData("{var_busdate1}",
							WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}") + "T18:00:00+05:30");

					/************
					 * Prerequisite 1: Create profile
					 ****************/

					profileID = CreateProfile.createProfile("DS_00");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false);
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_GDSError", true)) {
								if (!WSClient.getElementValue(modifyBookingRes, "CreateBookingResponse_Result_GDSError",
										XMLType.RESPONSE).equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO,
											" The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"CreateBookingResponse_Result_GDSError", XMLType.RESPONSE)
													+ "");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_GDSError_errorCode", true)) {
								if (!WSClient
										.getElementValue(modifyBookingRes,
												"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										.equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO, " The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										" The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "");
							}

						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									" The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"CreateBookingResponse_faultstring", XMLType.RESPONSE) + "");
						}
					}
				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
		}
	}

	@Test(groups = { "minimumRegression", "CreateBooking", "Reservation", "OWS", "PriorRun" })
	public void createBooking_44552() {
		try {
			String testName = "createBooking_44552";
			WSClient.startTest(testName,
					"Verify that a day use booking is created when mimimum los is greater than zero as OVERRIDE_YN is Y",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

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
				WSClient.writeToReport(LogStatus.INFO, "Enabling the parameter OVERRIDE_YN");
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
					WSClient.setData("{var_rate}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_ReservationType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_02"));
					WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}"));
					WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}"));
					WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}"));
					WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_14}"));
					WSClient.setData("{var_reservationType}",
							OperaPropConfig.getDataSetForCode("ReservationType", "DS_02"));
					WSClient.setData("{var_resvType}",
							OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_02"));
					WSClient.setData("{var_time}", "09:00:00");

					/************
					 * Prerequisite 1: Create profile
					 ****************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_00");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "Profile ID: " + profileID + "");

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/

						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(modifyBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
								String query = WSClient.getQuery("OWSModifyBooking", "QS_04");
								LinkedHashMap<String, String> dbResults = WSClient.getDBRow(query);

								String arrivalDate = WSClient.getElementValue(modifyBookingReq,
										"RoomStay_TimeSpan_StartDate", XMLType.REQUEST).substring(0, 10);
								String departureDate = WSClient
										.getElementValue(modifyBookingReq, "RoomStay_TimeSpan_EndDate", XMLType.REQUEST)
										.substring(0, 10);
								String resarrivalDate = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE).substring(0, 10);
								String resdepartureDate = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE).substring(0, 10);

								WSClient.writeToReport(LogStatus.INFO, "<b>Validations from DB</b>");

								/******
								 * Validate above details against DB
								 *********/
								String dbArrival = dbResults.get("ARRIVAL");
								dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));

								String dbDeparture = dbResults.get("DEPARTURE");
								dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

								if (WSAssert.assertEquals(arrivalDate, dbArrival, true)) {
									WSClient.writeToReport(LogStatus.PASS, " Arrival date -> Expected : " + arrivalDate
											+ "  Actual : " + dbArrival + "");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, " Arrival date -> Expected : " + arrivalDate
											+ "  Actual : " + dbArrival + "");
								}
								if (WSAssert.assertEquals(departureDate, dbDeparture, true)) {
									WSClient.writeToReport(LogStatus.PASS, " Departure date -> Expected : "
											+ departureDate + "  Actual : " + dbDeparture + "");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, " Departure date -> Expected : "
											+ departureDate + "  Actual : " + dbDeparture + "");
								}

								WSClient.writeToReport(LogStatus.INFO,
										"Validations > Retreived correctly on the response");

								/**** Validate modified arrival dates ****/
								if (WSAssert.assertEquals(arrivalDate, resarrivalDate, true)) {
									WSClient.writeToReport(LogStatus.PASS, " Arrival date -> Expected : " + arrivalDate
											+ "  Actual : " + resarrivalDate + "");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, " Arrival date -> Expected : " + arrivalDate
											+ "  Actual : " + resarrivalDate + "");
								}
								/**** Validate modified departure dates ****/
								if (WSAssert.assertEquals(departureDate, resdepartureDate, true)) {
									WSClient.writeToReport(LogStatus.PASS, " Departure date -> Expected : "
											+ departureDate + "  Actual : " + resdepartureDate + "");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, " Departure date -> Expected : "
											+ departureDate + "  Actual : " + resdepartureDate + "");
								}
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_GDSError", true)) {
								if (!WSClient.getElementValue(modifyBookingRes, "CreateBookingResponse_Result_GDSError",
										XMLType.RESPONSE).equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO,
											" The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"CreateBookingResponse_Result_GDSError", XMLType.RESPONSE)
													+ "");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_GDSError_errorCode", true)) {
								if (!WSClient
										.getElementValue(modifyBookingRes,
												"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										.equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO, " The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										" The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message + "");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_faultstring",
								true)) {
							WSClient.writeToReport(LogStatus.FAIL,
									" The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
											"CreateBookingResponse_faultstring", XMLType.RESPONSE) + "");
						}
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				WSClient.writeToReport(LogStatus.INFO, "<b> Disabling OVERRIDE_YN </b>");

				OPERALib.setOperaHeader(OPERALib.getUserName());
				WSClient.setData("{var_par}", "N");
				ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
				e.printStackTrace();
			}
		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking", "Reservation",
	// "OWS", "PriorRun" })
	public void createBooking_44553() {
		try {
			String testName = "createBooking_44553";
			WSClient.startTest(testName,
					"Verify that error should be generated stay date range is greater than maximum length of stay of the rate code",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is enabled</b>");
			WSClient.setData("{var_par}", "N");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "OVERRIDE_YN : " + paramValue + "");
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
					WSClient.setData("{var_orate}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));

					WSClient.setData("{var_rate}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					WSClient.setData("{var_reservationType}",
							OperaPropConfig.getDataSetForCode("ReservationType", "DS_02"));
					WSClient.setData("{var_resvType}", OperaPropConfig.getDataSetForCode("ReservationType", "DS_02"));
					WSClient.setData("{var_time}", "09:00:00");
					/************
					 * Prerequisite 1: Create profile
					 ****************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "Profile ID: " + profileID + "");

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/
						// WSClient.writeToReport(LogStatus.INFO, "Reservation
						// ID: "+resvID.get("reservationId")+"");
						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
						// WSClient.setData("{var_resvId}",
						// resvID.get("reservationId"));

						String query = WSClient.getQuery("OWSCreateBooking", "QS_33");
						LinkedHashMap<String, String> dbResults = WSClient.getDBRow(query);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>Maximum Occupancy of rate code : " + dbResults.get("MAX_OCCUPANCY") + "</b>");

						String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_28}");
						WSClient.setData("{var_startDate}", startDate);
						WSClient.setData("{var_endDate}", modifyDaysToDate(startDate, "yyyy-MM-dd",
								Integer.parseInt(dbResults.get("MAX_OCCUPANCY")) + 1));
						WSClient.setData("{var_busdate}", startDate);
						WSClient.setData("{var_busdate1}", modifyDaysToDate(startDate, "yyyy-MM-dd",
								Integer.parseInt(dbResults.get("MAX_OCCUPANCY")) + 1));

						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result", true)) {
							WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false);
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_GDSError", true)) {
								if (!WSClient.getElementValue(modifyBookingRes, "CreateBookingResponse_Result_GDSError",
										XMLType.RESPONSE).equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO,
											" The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"CreateBookingResponse_Result_GDSError", XMLType.RESPONSE)
													+ "");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_GDSError_errorCode", true)) {
								if (!WSClient
										.getElementValue(modifyBookingRes,
												"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										.equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO, " The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										" The error code that is generated is : <b>"
												+ WSClient.getElementValue(modifyBookingRes,
														"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "</b>");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message + "");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_faultstring",
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
		}
	}

	// @Test(groups = { "minimumRegression", "CreateBooking", "Reservation",
	// "OWS", "PriorRun" })
	public void createBooking_44554() {
		try {
			String testName = "createBooking_44554";
			WSClient.startTest(testName,
					"Verify that booking is created when stay date range is greater than maximum length of stay of rate code as OVERRIDE_YN is Y",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			WSClient.setData("{var_channel}", channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.writeToReport(LogStatus.INFO, "Verify if the parameter OVERRIDE_YN is enabled");
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_parname}", "OVERRIDEINVENTORYRESTRICTION");
			WSClient.setData("{var_param}", "OVERRIDE_YN");
			WSClient.setData("{var_type}", "Boolean");
			String paramValue = FetchChannelParameters.fetchChannelParameters("QS_01", "OVERRIDE_YN");
			WSClient.writeToReport(LogStatus.INFO, "OVERRIDE_YN : " + paramValue + "");
			if (!paramValue.equals("Y")) {
				WSClient.writeToReport(LogStatus.INFO, "Enabling the parameter OVERRIDE_YN");
				paramValue = ChangeChannelParameters.changeChannelParameters("DS_01", "QS_01", "OVERRIDE_YN");
				WSClient.writeToReport(LogStatus.INFO, "OVERRIDE_YN : " + paramValue + "");
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
					WSClient.setData("{var_rate}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_05"));

					WSClient.setData("{var_orate}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));

					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_04"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_resvType}", OperaPropConfig.getDataSetForCode("ReservationType", "DS_02"));
					WSClient.setData("{var_time}", "09:00:00");
					/************
					 * Prerequisite 1: Create profile
					 ****************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "Profile ID: " + profileID + "");

						/****************
						 * Prerequisite 2:Create a Reservation
						 *****************/

						String query = WSClient.getQuery("OWSCreateBooking", "QS_33");
						LinkedHashMap<String, String> dbResults = WSClient.getDBRow(query);
						WSClient.writeToReport(LogStatus.INFO,
								"Maximum Occupancy of rate code : " + dbResults.get("MAX_OCCUPANCY") + "");

						String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_18}");
						WSClient.setData("{var_startDate}", startDate);
						WSClient.setData("{var_endDate}", modifyDaysToDate(startDate, "yyyy-MM-dd",
								Integer.parseInt(dbResults.get("MAX_OCCUPANCY")) + 1));
						WSClient.setData("{var_busdate}", startDate);
						WSClient.setData("{var_busdate1}", modifyDaysToDate(startDate, "yyyy-MM-dd",
								Integer.parseInt(dbResults.get("MAX_OCCUPANCY")) + 1));
						/***************
						 * OWS Modify Booking Operation
						 ********************/
						String interfaceName = OWSLib.getChannel();
						String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
								OWSLib.getChannelType(interfaceName),
								OWSLib.getChannelCarier(resortOperaValue, interfaceName));

						String modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

						if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
									"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(modifyBookingRes,
										"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
								query = WSClient.getQuery("OWSModifyBooking", "QS_04");
								dbResults = WSClient.getDBRow(query);

								String arrivalDate = WSClient.getElementValue(modifyBookingReq,
										"RoomStay_TimeSpan_StartDate", XMLType.REQUEST).substring(0, 10);
								String departureDate = WSClient
										.getElementValue(modifyBookingReq, "RoomStay_TimeSpan_EndDate", XMLType.REQUEST)
										.substring(0, 10);
								String resarrivalDate = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE).substring(0, 10);
								String resdepartureDate = WSClient.getElementValue(modifyBookingRes,
										"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE).substring(0, 10);

								WSClient.writeToReport(LogStatus.INFO, "<b>Validations are applied on DB</b>");

								/******
								 * Validate above details against DB
								 *********/
								String dbArrival = dbResults.get("ARRIVAL");
								dbArrival = dbArrival.substring(0, dbArrival.indexOf(' '));

								String dbDeparture = dbResults.get("DEPARTURE");
								dbDeparture = dbDeparture.substring(0, dbDeparture.indexOf(' '));

								if (WSAssert.assertEquals(arrivalDate, dbArrival, true)) {
									WSClient.writeToReport(LogStatus.PASS, " Arrival date -> Expected : " + arrivalDate
											+ "  Actual : " + dbArrival + "");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, " Arrival date -> Expected : " + arrivalDate
											+ "  Actual : " + dbArrival + "");
								}
								if (WSAssert.assertEquals(departureDate, dbDeparture, true)) {
									WSClient.writeToReport(LogStatus.PASS, " Departure date -> Expected : "
											+ departureDate + "  Actual : " + dbDeparture + "");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, " Departure date -> Expected : "
											+ departureDate + "  Actual : " + dbDeparture + "");
								}

								WSClient.writeToReport(LogStatus.INFO, "<b>Validations on the response</b>");

								/**** Validate modified arrival dates ****/
								if (WSAssert.assertEquals(arrivalDate, resarrivalDate, true)) {
									WSClient.writeToReport(LogStatus.PASS, " Arrival date -> Expected : " + arrivalDate
											+ "  Actual : " + resarrivalDate + "");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, " Arrival date -> Expected : " + arrivalDate
											+ "  Actual : " + resarrivalDate + "");
								}
								/**** Validate modified departure dates ****/
								if (WSAssert.assertEquals(departureDate, resdepartureDate, true)) {
									WSClient.writeToReport(LogStatus.PASS, " Departure date -> Expected : "
											+ departureDate + "  Actual : " + resdepartureDate + "");
								} else {
									WSClient.writeToReport(LogStatus.FAIL, " Departure date -> Expected : "
											+ departureDate + "  Actual : " + resdepartureDate + "");
								}
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_GDSError", true)) {
								if (!WSClient.getElementValue(modifyBookingRes, "CreateBookingResponse_Result_GDSError",
										XMLType.RESPONSE).equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO,
											" The error that is generated is : "
													+ WSClient.getElementValue(modifyBookingRes,
															"CreateBookingResponse_Result_GDSError", XMLType.RESPONSE)
													+ "");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_GDSError_errorCode", true)) {
								if (!WSClient
										.getElementValue(modifyBookingRes,
												"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
										.equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO, " The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_Result_OperaErrorCode", true)) {
								WSClient.writeToReport(LogStatus.INFO,
										" The error code that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
												+ "");
							}
							if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
								String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is :" + message + "");
							}
						} else if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_faultstring",
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

	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })

	public void createBooking_22274() {
		String paramvalue, resvid = "";
		try {
			String testName = "createBooking_22274";
			WSClient.startTest(testName,
					"Verify that booking is created successfully when room count is equal to/less than/greater than the sell limit configured as OVERRIDE_YN is set to Y",
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
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_param}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			OPERALib.setOperaHeader(OPERALib.getUserName());
			WSClient.setData("{var_time}", "09:00:00");

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
					WSClient.setData("{var_rateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
					String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
					String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
					fetchAvailability(rate, rt);
					WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
					WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
					WSClient.setData("{var_startDate}", WSClient.getData("{var_busdate}").substring(0, 10));
					WSClient.setData("{var_endDate}", WSClient.getData("{var_busdate1}").substring(0, 10));
					// WSClient.setData("{var_startDate}",
					// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_171}"));
					// WSClient.setData("{var_endDate}",
					// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_172}"));
					WSClient.setData("{var_roomLimit}", "4");

					/************** Set channel sell limit ***************/
					WSClient.writeToReport(LogStatus.INFO, "<b>Channel Sell Limit  is set to 4</b>");

					String setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_04");
					String setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
					if (WSAssert.assertIfElementExists(setSellLimitsRes, "SetChannelSellLimitsByDateRangeRS_Success",
							true)) {
						String fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_03");
						String fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
						if (WSAssert.assertIfElementExists(fetchLimitsRes, "FetchChannelSellLimitsRS_Success", true)) {
							WSClient.setData("{var_id}", WSClient.getElementValue(fetchLimitsRes,
									"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
						}

						WSClient.writeToReport(LogStatus.INFO, "<b>Channel Sell Limit for room type is set to 3</b>");

						WSClient.setData("{var_roomLimit}", "3");
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

						setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_03");
						setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
						if (WSAssert.assertIfElementExists(setSellLimitsRes,
								"SetChannelSellLimitsByDateRangeRS_Success", true)) {
							fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_02");
							fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
							if (WSAssert.assertIfElementExists(fetchLimitsRes, "FetchChannelSellLimitsRS_Success",
									true)) {
								WSClient.setData("{var_id1}", WSClient.getElementValue(fetchLimitsRes,
										"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
							}
							/***********
							 * Pre-requisite : Create a Profile
							 ***********/
							profileID = CreateProfile.createProfile("DS_00");
							if (!profileID.equals("error")) {
								WSClient.setData("{var_profileId}", profileID);

								/*************
								 * Prerequisite :Create a Booking
								 ********************/
								// WSClient.setData("{var_busdate}",
								// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_126}")
								// + "T14:00:00+05:30");
								// WSClient.setData("{var_busdate1}",
								// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_127}")
								// + "T14:00:00+05:30");
								WSClient.setData("{var_time}", "09:00:00");
								WSClient.setData("{var_resvType}",
										OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_02"));
								WSClient.setData("{var_rate}",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
								WSClient.setData("{var_ReservationType}",
										OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_02"));

								OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
										OWSLib.getChannelType(interfaceName),
										OWSLib.getChannelCarier(resortOperaValue, interfaceName));

								WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:" + resvid + "</b>");

								WSClient.setData("{var_rateCode}",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
								WSClient.setData("{var_roomType}",
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
								WSClient.setData("{var_roomCount}", "3");

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Verify that booking is created when room count is equal to the sell limit configured for the room type</b>");
								String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_45");
								String createBookingRes = WSClient.processSOAPMessage(createBookingReq);

								if (WSAssert.assertIfElementExists(createBookingRes, "CreateBookingResponse_Result",
										true)) {
									if (WSAssert.assertIfElementValueEquals(createBookingRes,
											"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Verify that booking is created although room count is greater than sell limit configured for the room type</b>");
										/******
										 * Verify that booking is not modified
										 * when room count exceeds the sell
										 * limit
										 *****/
										WSClient.setData("{var_roomCount}", "4");
										createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_45");
										createBookingRes = WSClient.processSOAPMessage(createBookingReq);
										if (WSAssert.assertIfElementExists(createBookingRes,
												"CreateBookingResponse_Result", true)) {
											if (WSAssert.assertIfElementValueEquals(createBookingRes,
													"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Verify that booking is created when room count is equal to the sell limit configured for the channel</b>");
												// WSClient.setData("{var_rateCode}",
												// OperaPropConfig.getChannelCodeForDataSet("RateCode",
												// "DS_03"));
												// WSClient.setData("{var_roomType}",
												// OperaPropConfig.getChannelCodeForDataSet("RoomType",
												// "DS_02"));
												createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking",
														"DS_45");
												createBookingRes = WSClient.processSOAPMessage(createBookingReq);
												if (WSAssert.assertIfElementExists(createBookingRes,
														"CreateBookingResponse_Result", true)) {
													if (WSAssert.assertIfElementValueEquals(createBookingRes,
															"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS",
															false)) {
														WSClient.writeToReport(LogStatus.INFO,
																"<b>Verify that booking is modified although room count is greater than the sell limit configured for the channel</b>");
														WSClient.setData("{var_roomCount}", "5");
														createBookingReq = WSClient
																.createSOAPMessage("OWSCreateBooking", "DS_45");
														createBookingRes = WSClient
																.processSOAPMessage(createBookingReq);
														if (WSAssert.assertIfElementExists(createBookingRes,
																"CreateBookingResponse_Result", true)) {
															WSAssert.assertIfElementValueEquals(createBookingRes,
																	"CreateBookingResponse_Result_resultStatusFlag",
																	"SUCCESS", false);
															if (WSAssert.assertIfElementExists(createBookingRes,
																	"CreateBookingResponse_Result_GDSError", true)) {
																if (!WSClient.getElementValue(createBookingRes,
																		"CreateBookingResponse_Result_GDSError",
																		XMLType.RESPONSE).equals("*null*"))
																	WSClient.writeToReport(LogStatus.INFO,
																			"<b> The error that is generated is : "
																					+ WSClient.getElementValue(
																							createBookingRes,
																							"CreateBookingResponse_Result_GDSError",
																							XMLType.RESPONSE)
																					+ "</b>");
															}
															if (WSAssert.assertIfElementExists(createBookingRes,
																	"CreateBookingResponse_Result_GDSError_errorCode",
																	true)) {
																if (!WSClient.getElementValue(createBookingRes,
																		"CreateBookingResponse_Result_GDSError_errorCode",
																		XMLType.RESPONSE).equals("*null*"))
																	WSClient.writeToReport(LogStatus.INFO,
																			"<b> The error code that is generated is : "
																					+ WSClient.getElementValue(
																							createBookingRes,
																							"CreateBookingResponse_Result_GDSError_errorCode",
																							XMLType.RESPONSE)
																					+ "</b>");
															}
															if (WSAssert.assertIfElementExists(createBookingRes,
																	"CreateBookingResponse_Result_OperaErrorCode",
																	true)) {
																WSClient.writeToReport(LogStatus.INFO,
																		"<b> The error code that is generated is : "
																				+ WSClient.getElementValue(
																						createBookingRes,
																						"CreateBookingResponse_Result_OperaErrorCode",
																						XMLType.RESPONSE)
																				+ "</b>");
															}
															if (WSAssert.assertIfElementExists(createBookingRes,
																	"Result_Text_TextElement", true)) {
																String message = WSAssert.getElementValue(
																		createBookingRes, "Result_Text_TextElement",
																		XMLType.RESPONSE);
																WSClient.writeToReport(LogStatus.INFO,
																		"<b>The text displayed in the response is :"
																				+ message + "</b>");
															}
														} else if (WSAssert.assertIfElementExists(createBookingRes,
																"CreateBookingResponse_faultstring", true)) {
															WSClient.writeToReport(LogStatus.FAIL,
																	"<b> The error that is generated is : "
																			+ WSClient.getElementValue(createBookingRes,
																					"CreateBookingResponse_faultstring",
																					XMLType.RESPONSE)
																			+ "</b>");
														}
													}
													if (WSAssert.assertIfElementExists(createBookingRes,
															"CreateBookingResponse_Result_GDSError", true)) {
														if (!WSClient.getElementValue(createBookingRes,
																"CreateBookingResponse_Result_GDSError",
																XMLType.RESPONSE).equals("*null*"))
															WSClient.writeToReport(LogStatus.INFO,
																	"<b> The error that is generated is : "
																			+ WSClient.getElementValue(createBookingRes,
																					"CreateBookingResponse_Result_GDSError",
																					XMLType.RESPONSE)
																			+ "</b>");
													}
													if (WSAssert.assertIfElementExists(createBookingRes,
															"CreateBookingResponse_Result_GDSError_errorCode", true)) {
														if (!WSClient.getElementValue(createBookingRes,
																"CreateBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE).equals("*null*"))
															WSClient.writeToReport(LogStatus.INFO,
																	"<b> The error code that is generated is : "
																			+ WSClient.getElementValue(createBookingRes,
																					"CreateBookingResponse_Result_GDSError_errorCode",
																					XMLType.RESPONSE)
																			+ "</b>");
													}
													if (WSAssert.assertIfElementExists(createBookingRes,
															"CreateBookingResponse_Result_OperaErrorCode", true)) {
														WSClient.writeToReport(LogStatus.INFO,
																"<b> The error code that is generated is : "
																		+ WSClient.getElementValue(createBookingRes,
																				"CreateBookingResponse_Result_OperaErrorCode",
																				XMLType.RESPONSE)
																		+ "</b>");
													}
													if (WSAssert.assertIfElementExists(createBookingRes,
															"Result_Text_TextElement", true)) {
														String message = WSAssert.getElementValue(createBookingRes,
																"Result_Text_TextElement", XMLType.RESPONSE);
														WSClient.writeToReport(LogStatus.INFO,
																"<b>The text displayed in the response is :" + message
																		+ "</b>");
													}
												} else if (WSAssert.assertIfElementExists(createBookingRes,
														"CreateBookingResponse_faultstring", true)) {
													WSClient.writeToReport(LogStatus.FAIL,
															"<b> The error that is generated is : "
																	+ WSClient.getElementValue(createBookingRes,
																			"createBookingResponse_faultstring",
																			XMLType.RESPONSE)
																	+ "</b>");
												}
											}
											if (WSAssert.assertIfElementExists(createBookingRes,
													"CreateBookingResponse_Result_GDSError", true)) {
												if (!WSClient.getElementValue(createBookingRes,
														"CreateBookingResponse_Result_GDSError", XMLType.RESPONSE)
														.equals("*null*"))
													WSClient.writeToReport(LogStatus.INFO,
															"<b> The error that is generated is : "
																	+ WSClient.getElementValue(createBookingRes,
																			"CreateBookingResponse_Result_GDSError",
																			XMLType.RESPONSE)
																	+ "</b>");
											}
											if (WSAssert.assertIfElementExists(createBookingRes,
													"CreateBookingResponse_Result_GDSError_errorCode", true)) {
												if (!WSClient.getElementValue(createBookingRes,
														"CreateBookingResponse_Result_GDSError_errorCode",
														XMLType.RESPONSE).equals("*null*"))
													WSClient.writeToReport(LogStatus.INFO,
															"<b> The error code that is generated is : "
																	+ WSClient.getElementValue(createBookingRes,
																			"CreateBookingResponse_Result_GDSError_errorCode",
																			XMLType.RESPONSE)
																	+ "</b>");
											}
											if (WSAssert.assertIfElementExists(createBookingRes,
													"CreateBookingResponse_Result_OperaErrorCode", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(createBookingRes,
																		"CreateBookingResponse_Result_OperaErrorCode",
																		XMLType.RESPONSE)
																+ "</b>");
											}
											if (WSAssert.assertIfElementExists(createBookingRes,
													"Result_Text_TextElement", true)) {
												String message = WSAssert.getElementValue(createBookingRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>The text displayed in the response is :" + message
																+ "</b>");
											}
										} else if (WSAssert.assertIfElementExists(createBookingRes,
												"CreateBookingResponse_faultstring", true)) {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b> The error that is generated is : " + WSClient.getElementValue(
															createBookingRes, "CreateBookingResponse_faultstring",
															XMLType.RESPONSE) + "</b>");
										}
									}
									if (WSAssert.assertIfElementExists(createBookingRes,
											"CreateBookingResponse_Result_GDSError", true)) {
										if (!WSClient
												.getElementValue(createBookingRes,
														"CreateBookingResponse_Result_GDSError", XMLType.RESPONSE)
												.equals("*null*"))
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error that is generated is : " + WSClient.getElementValue(
															createBookingRes, "CreateBookingResponse_Result_GDSError",
															XMLType.RESPONSE) + "</b>");
									}
									if (WSAssert.assertIfElementExists(createBookingRes,
											"CreateBookingResponse_Result_GDSError_errorCode", true)) {
										if (!WSClient.getElementValue(createBookingRes,
												"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
												.equals("*null*"))
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error code that is generated is : "
															+ WSClient.getElementValue(createBookingRes,
																	"CreateBookingResponse_Result_GDSError_errorCode",
																	XMLType.RESPONSE)
															+ "</b>");
									}
									if (WSAssert.assertIfElementExists(createBookingRes,
											"CreateBookingResponse_Result_OperaErrorCode", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : " + WSClient.getElementValue(
														createBookingRes, "CreateBookingResponse_Result_OperaErrorCode",
														XMLType.RESPONSE) + "</b>");
									}
									if (WSAssert.assertIfElementExists(createBookingRes, "Result_Text_TextElement",
											true)) {
										String message = WSAssert.getElementValue(createBookingRes,
												"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The text displayed in the response is :" + message + "</b>");
									}
								} else if (WSAssert.assertIfElementExists(createBookingRes,
										"CreateBookingResponse_faultstring", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> The error that is generated is : "
													+ WSClient.getElementValue(createBookingRes,
															"CreateBookingResponse_faultstring", XMLType.RESPONSE)
													+ "</b>");
								}

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

	// @Test(groups = { "minimumRegression", "CreateBooking", "OWS",
	// "Reservation" })

	public void createBooking_222743() {
		String paramvalue, resvid = "";
		try {
			String testName = "createBooking_222743";
			WSClient.startTest(testName,
					"Verify that booking is created successfully when room count is equal to/less than/greater than the sell limit configured as OVERRIDE_YN is set to N",
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
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			WSClient.setData("{var_channel}", OWSLib.getChannel());
			WSClient.setData("{var_par}", "Y");
			WSClient.setData("{var_param}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_parname}", "CHANNEL_INVENTORY");
			WSClient.setData("{var_type}", "Boolean");
			OPERALib.setOperaHeader(OPERALib.getUserName());
			WSClient.setData("{var_time}", "09:00:00");

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
				WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter OVERRIDE_YN is Disabled</b>");
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
					String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
					String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
					fetchAvailability(rate, rt);
					WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
					WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
					WSClient.setData("{var_startDate}", WSClient.getData("{var_busdate}").substring(0, 10));
					WSClient.setData("{var_endDate}", WSClient.getData("{var_busdate1}").substring(0, 10));
					// WSClient.setData("{var_startDate}",
					// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_426}"));
					// WSClient.setData("{var_endDate}",
					// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_427}"));
					WSClient.setData("{var_roomLimit}", "4");

					/************** Set channel sell limit ***************/
					String setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_04");
					String setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
					if (WSAssert.assertIfElementExists(setSellLimitsRes, "SetChannelSellLimitsByDateRangeRS_Success",
							true)) {
						String fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_03");
						String fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
						if (WSAssert.assertIfElementExists(fetchLimitsRes, "FetchChannelSellLimitsRS_Success", true)) {
							WSClient.setData("{var_id}", WSClient.getElementValue(fetchLimitsRes,
									"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
						}
						WSClient.writeToReport(LogStatus.INFO, "id is:" + WSClient.getElementValue(fetchLimitsRes,
								"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
						WSClient.setData("{var_roomLimit}", "3");
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));

						setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_03");
						setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
						if (WSAssert.assertIfElementExists(setSellLimitsRes,
								"SetChannelSellLimitsByDateRangeRS_Success", true)) {
							fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_02");
							fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
							if (WSAssert.assertIfElementExists(fetchLimitsRes, "FetchChannelSellLimitsRS_Success",
									true)) {
								WSClient.setData("{var_id1}", WSClient.getElementValue(fetchLimitsRes,
										"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
							}
							WSClient.writeToReport(LogStatus.INFO, "id1 is:" + WSClient.getElementValue(fetchLimitsRes,
									"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
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
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_426}") + "T14:00:00+05:30");
								WSClient.setData("{var_busdate1}",
										WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_427}") + "T14:00:00+05:30");
								WSClient.setData("{var_time}", "09:00:00");
								WSClient.setData("{var_resvType}",
										OperaPropConfig.getDataSetForCode("ReservationType", "DS_02"));
								WSClient.setData("{var_rate}",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
								WSClient.setData("{var_ReservationType}",
										OperaPropConfig.getDataSetForCode("ReservationType", "DS_02"));

								OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
										OWSLib.getChannelType(interfaceName),
										OWSLib.getChannelCarier(resortOperaValue, interfaceName));

								WSClient.setData("{var_rateCode}",
										OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
								WSClient.setData("{var_roomType}",
										OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
								WSClient.setData("{var_roomCount}", "3");

								WSClient.writeToReport(LogStatus.INFO,
										"<b>Verify that booking is modified when room count is equal to the sell limit configured for the room type</b>");
								String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_45");
								String createBookingRes = WSClient.processSOAPMessage(createBookingReq);

								if (WSAssert.assertIfElementExists(createBookingRes, "CreateBookingResponse_Result",
										true)) {
									if (WSAssert.assertIfElementValueEquals(createBookingRes,
											"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Verify that booking is modified although room count is greater than sell limit configured for the room type</b>");
										/******
										 * Verify that booking is not modified
										 * when room count exceeds the sell
										 * limit
										 *****/
										WSClient.setData("{var_roomCount}", "4");
										createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_45");
										createBookingRes = WSClient.processSOAPMessage(createBookingReq);
										if (WSAssert.assertIfElementExists(createBookingRes,
												"CreateBookingResponse_Result", true)) {
											if (WSAssert.assertIfElementValueEquals(createBookingRes,
													"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS",
													false)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b>Verify that booking is modified when room count is equal to the sell limit configured for the channel</b>");
												WSClient.setData("{var_rateCode}",
														OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_03"));
												WSClient.setData("{var_roomType}",
														OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_02"));
												createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking",
														"DS_45");
												createBookingRes = WSClient.processSOAPMessage(createBookingReq);
												if (WSAssert.assertIfElementExists(createBookingRes,
														"CreateBookingResponse_Result", true)) {
													if (WSAssert.assertIfElementValueEquals(createBookingRes,
															"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS",
															false)) {
														WSClient.writeToReport(LogStatus.INFO,
																"<b>Verify that booking is modified although room count is greater than the sell limit configured for the channel</b>");
														WSClient.setData("{var_roomCount}", "5");
														createBookingReq = WSClient
																.createSOAPMessage("OWSCreateBooking", "DS_45");
														createBookingRes = WSClient
																.processSOAPMessage(createBookingReq);
														if (WSAssert.assertIfElementExists(createBookingRes,
																"CreateBookingResponse_Result", true)) {
															WSAssert.assertIfElementValueEquals(createBookingRes,
																	"CreateBookingResponse_Result_resultStatusFlag",
																	"FAIL", false);

															if (WSAssert.assertIfElementExists(createBookingRes,
																	"CreateBookingResponse_Result_OperaErrorCode",
																	true)) {
																WSClient.writeToReport(LogStatus.INFO,
																		"<b> The error code that is generated is : "
																				+ WSClient.getElementValue(
																						createBookingRes,
																						"CreateBookingResponse_Result_OperaErrorCode",
																						XMLType.RESPONSE)
																				+ "</b>");
															}
															if (WSAssert.assertIfElementExists(createBookingRes,
																	"Result_Text_TextElement", true)) {
																String message = WSAssert.getElementValue(
																		createBookingRes, "Result_Text_TextElement",
																		XMLType.RESPONSE);
																WSClient.writeToReport(LogStatus.INFO,
																		"<b>The text displayed in the response is :"
																				+ message + "</b>");
															}
														} else if (WSAssert.assertIfElementExists(createBookingRes,
																"CreateBookingResponse_faultstring", true)) {
															WSClient.writeToReport(LogStatus.FAIL,
																	"<b> The error that is generated is : "
																			+ WSClient.getElementValue(createBookingRes,
																					"CreateBookingResponse_faultstring",
																					XMLType.RESPONSE)
																			+ "</b>");
														}
													}
													if (WSAssert.assertIfElementExists(createBookingRes,
															"CreateBookingResponse_Result_GDSError", true)) {
														if (!WSClient.getElementValue(createBookingRes,
																"CreateBookingResponse_Result_GDSError",
																XMLType.RESPONSE).equals("*null*"))
															WSClient.writeToReport(LogStatus.INFO,
																	"<b> The error that is generated is : "
																			+ WSClient.getElementValue(createBookingRes,
																					"CreateBookingResponse_Result_GDSError",
																					XMLType.RESPONSE)
																			+ "</b>");
													}
													if (WSAssert.assertIfElementExists(createBookingRes,
															"CreateBookingResponse_Result_GDSError_errorCode", true)) {
														if (!WSClient.getElementValue(createBookingRes,
																"CreateBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE).equals("*null*"))
															WSClient.writeToReport(LogStatus.INFO,
																	"<b> The error code that is generated is : "
																			+ WSClient.getElementValue(createBookingRes,
																					"CreateBookingResponse_Result_GDSError_errorCode",
																					XMLType.RESPONSE)
																			+ "</b>");
													}
													if (WSAssert.assertIfElementExists(createBookingRes,
															"CreateBookingResponse_Result_OperaErrorCode", true)) {
														WSClient.writeToReport(LogStatus.INFO,
																"<b> The error code that is generated is : "
																		+ WSClient.getElementValue(createBookingRes,
																				"CreateBookingResponse_Result_OperaErrorCode",
																				XMLType.RESPONSE)
																		+ "</b>");
													}
													if (WSAssert.assertIfElementExists(createBookingRes,
															"Result_Text_TextElement", true)) {
														String message = WSAssert.getElementValue(createBookingRes,
																"Result_Text_TextElement", XMLType.RESPONSE);
														WSClient.writeToReport(LogStatus.INFO,
																"<b>The text displayed in the response is :" + message
																		+ "</b>");
													}
												} else if (WSAssert.assertIfElementExists(createBookingRes,
														"CreateBookingResponse_faultstring", true)) {
													WSClient.writeToReport(LogStatus.FAIL,
															"<b> The error that is generated is : "
																	+ WSClient.getElementValue(createBookingRes,
																			"createBookingResponse_faultstring",
																			XMLType.RESPONSE)
																	+ "</b>");
												}
											}
											if (WSAssert.assertIfElementExists(createBookingRes,
													"CreateBookingResponse_Result_GDSError", true)) {
												if (!WSClient.getElementValue(createBookingRes,
														"CreateBookingResponse_Result_GDSError", XMLType.RESPONSE)
														.equals("*null*"))
													WSClient.writeToReport(LogStatus.INFO,
															"<b> The error that is generated is : "
																	+ WSClient.getElementValue(createBookingRes,
																			"CreateBookingResponse_Result_GDSError",
																			XMLType.RESPONSE)
																	+ "</b>");
											}
											if (WSAssert.assertIfElementExists(createBookingRes,
													"CreateBookingResponse_Result_GDSError_errorCode", true)) {
												if (!WSClient.getElementValue(createBookingRes,
														"CreateBookingResponse_Result_GDSError_errorCode",
														XMLType.RESPONSE).equals("*null*"))
													WSClient.writeToReport(LogStatus.INFO,
															"<b> The error code that is generated is : "
																	+ WSClient.getElementValue(createBookingRes,
																			"CreateBookingResponse_Result_GDSError_errorCode",
																			XMLType.RESPONSE)
																	+ "</b>");
											}
											if (WSAssert.assertIfElementExists(createBookingRes,
													"CreateBookingResponse_Result_OperaErrorCode", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(createBookingRes,
																		"CreateBookingResponse_Result_OperaErrorCode",
																		XMLType.RESPONSE)
																+ "</b>");
											}
											if (WSAssert.assertIfElementExists(createBookingRes,
													"Result_Text_TextElement", true)) {
												String message = WSAssert.getElementValue(createBookingRes,
														"Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,
														"<b>The text displayed in the response is :" + message
																+ "</b>");
											}
										} else if (WSAssert.assertIfElementExists(createBookingRes,
												"CreateBookingResponse_faultstring", true)) {
											WSClient.writeToReport(LogStatus.FAIL,
													"<b> The error that is generated is : " + WSClient.getElementValue(
															createBookingRes, "CreateBookingResponse_faultstring",
															XMLType.RESPONSE) + "</b>");
										}
									}
									if (WSAssert.assertIfElementExists(createBookingRes,
											"CreateBookingResponse_Result_GDSError", true)) {
										if (!WSClient
												.getElementValue(createBookingRes,
														"CreateBookingResponse_Result_GDSError", XMLType.RESPONSE)
												.equals("*null*"))
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error that is generated is : " + WSClient.getElementValue(
															createBookingRes, "CreateBookingResponse_Result_GDSError",
															XMLType.RESPONSE) + "</b>");
									}
									if (WSAssert.assertIfElementExists(createBookingRes,
											"CreateBookingResponse_Result_GDSError_errorCode", true)) {
										if (!WSClient.getElementValue(createBookingRes,
												"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
												.equals("*null*"))
											WSClient.writeToReport(LogStatus.INFO,
													"<b> The error code that is generated is : "
															+ WSClient.getElementValue(createBookingRes,
																	"CreateBookingResponse_Result_GDSError_errorCode",
																	XMLType.RESPONSE)
															+ "</b>");
									}
									if (WSAssert.assertIfElementExists(createBookingRes,
											"CreateBookingResponse_Result_OperaErrorCode", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : " + WSClient.getElementValue(
														createBookingRes, "CreateBookingResponse_Result_OperaErrorCode",
														XMLType.RESPONSE) + "</b>");
									}
									if (WSAssert.assertIfElementExists(createBookingRes, "Result_Text_TextElement",
											true)) {
										String message = WSAssert.getElementValue(createBookingRes,
												"Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The text displayed in the response is :" + message + "</b>");
									}
								} else if (WSAssert.assertIfElementExists(createBookingRes,
										"CreateBookingResponse_faultstring", true)) {
									WSClient.writeToReport(LogStatus.FAIL,
											"<b> The error that is generated is : "
													+ WSClient.getElementValue(createBookingRes,
															"CreateBookingResponse_faultstring", XMLType.RESPONSE)
													+ "</b>");
								}

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
				WSClient.setData("{var_par}", "Y");
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

//  Need to debug later
//	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation" })
	public void createBooking_22234() {
		String paramvalue;
		resvId = "";
		try {
			String testName = "createBooking_22234";
			WSClient.startTest(testName,
					"Verify that booking is created when room count is equal to/less than sell limit configured and error is generated when room count is greater than sell limit configured",
					"minimumRegression");
			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

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
				String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02");
				String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
				fetchAvailability(rate, rt);
				WSClient.setData("{var_busdate11}", WSClient.getData("{var_busdate}").substring(0, 10));
				WSClient.setData("{var_busdate12}", WSClient.getData("{var_busdate1}").substring(0, 10));
				WSClient.setData("{var_startDate}", WSClient.getData("{var_busdate}").substring(0, 10));
				WSClient.setData("{var_endDate}", WSClient.getData("{var_busdate1}").substring(0, 10));
				// WSClient.setData("{var_startDate}",
				// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_115}"));
				// WSClient.setData("{var_endDate}",
				// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_116}"));
				WSClient.setData("{var_roomLimit}", "4");

				/************** Set channel sell limit ***************/
				WSClient.writeToReport(LogStatus.INFO, "<b>Channel Sell Limit is set to 4</b>");

				String setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_04");
				String setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
				if (WSAssert.assertIfElementExists(setSellLimitsRes, "SetChannelSellLimitsByDateRangeRS_Success",
						true)) {
					String fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_03");
					String fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
					if (WSAssert.assertIfElementExists(fetchLimitsRes, "FetchChannelSellLimitsRS_Success", true)) {
						WSClient.setData("{var_id}", WSClient.getElementValue(fetchLimitsRes,
								"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
					}

					WSClient.setData("{var_roomLimit}", "3");
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
					WSClient.writeToReport(LogStatus.INFO, "<b>Channel Sell Limit for the room Type is set to 3</b>");

					setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange", "DS_03");
					setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
					if (WSAssert.assertIfElementExists(setSellLimitsRes, "SetChannelSellLimitsByDateRangeRS_Success",
							true)) {
						fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_02");
						fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
						if (WSAssert.assertIfElementExists(fetchLimitsRes, "FetchChannelSellLimitsRS_Success", true)) {
							WSClient.setData("{var_id1}", WSClient.getElementValue(fetchLimitsRes,
									"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
						}
						/***********
						 * Pre-requisite : Create a Profile
						 ***********/
						profileID = CreateProfile.createProfile("DS_00");
						if (!profileID.equals("error")) {
							WSClient.setData("{var_profileId}", profileID);

							/*************
							 * Prerequisite :Create a Booking
							 ********************/
							// WSClient.setData("{var_busdate}",
							// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_126}")
							// + "T14:00:00+05:30");
							// WSClient.setData("{var_busdate1}",
							// WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_127}")
							// + "T14:00:00+05:30");
							WSClient.setData("{var_time}", "09:00:00");
							WSClient.setData("{var_resvType}",
									OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_02"));
							WSClient.setData("{var_rate}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
							WSClient.setData("{var_ReservationType}",
									OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_02"));

							OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
									OWSLib.getChannelType(interfaceName),
									OWSLib.getChannelCarier(resortOperaValue, interfaceName));
							// String createBookingReq =
							// WSClient.createSOAPMessage("OWSCreateBooking",
							// "DS_01");
							// String createBookingRes =
							// WSClient.processSOAPMessage(createBookingReq);
							// if(WSAssert.assertIfElementValueEquals(createBookingRes,"CreateBookingResponse_Result_resultStatusFlag","SUCCESS",
							// false))
							// {
							// resvid=WSClient.getElementValueByAttribute(createBookingRes,"HotelReservation_UniqueIDList_UniqueID",
							// "RESVID", XMLType.RESPONSE);
							// WSClient.setData("{var_resvId}", resvid);
							// WSClient.writeToReport(LogStatus.INFO,
							// "<b>Reservation ID:"+ resvid +"</b>");

							WSClient.setData("{var_rateCode}",
									OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
							WSClient.setData("{var_roomType}",
									OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
							WSClient.setData("{var_roomCount}", "3");

							WSClient.writeToReport(LogStatus.INFO,
									"<b>Verify that booking is created when room count is equal to the sell limit configured for the room type</b>");
							String modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_45");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									resvId = WSClient.getElementValueByAttribute(modifyBookingRes,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
									WSClient.setData("{var_resvId}", resvId);

									WSClient.writeToReport(LogStatus.INFO,
											"<b>Verify that booking is not created when room count is greater than sell limit configured for the room type</b>");
									/******
									 * Verify that booking is not modified when
									 * room count exceeds the sell limit
									 *****/
									WSClient.setData("{var_roomCount}", "4");
									modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_45");
									modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);
									if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result",
											true)) {
										if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
												"CreateBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"CreateBookingResponse_Result_GDSError", true)) {
												if (!WSClient.getElementValue(modifyBookingRes,
														"CreateBookingResponse_Result_GDSError", XMLType.RESPONSE)
														.equals("*null*"))
													WSClient.writeToReport(LogStatus.INFO,
															"<b> The error that is generated is : "
																	+ WSClient.getElementValue(modifyBookingRes,
																			"CreateBookingResponse_Result_GDSError",
																			XMLType.RESPONSE)
																	+ "</b>");
											}
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"CreateBookingResponse_Result_GDSError_errorCode", true)) {
												if (!WSClient.getElementValue(modifyBookingRes,
														"CreateBookingResponse_Result_GDSError_errorCode",
														XMLType.RESPONSE).equals("*null*"))
													WSClient.writeToReport(LogStatus.INFO,
															"<b> The error code that is generated is : "
																	+ WSClient.getElementValue(modifyBookingRes,
																			"CreateBookingResponse_Result_GDSError_errorCode",
																			XMLType.RESPONSE)
																	+ "</b>");
											}
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"CreateBookingResponse_Result_OperaErrorCode", true)) {
												WSClient.writeToReport(LogStatus.INFO,
														"<b> The error code that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"CreateBookingResponse_Result_OperaErrorCode",
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

											WSClient.writeToReport(LogStatus.INFO,
													"<b>Verify that booking is created when room count is equal to the sell limit configured for the channel</b>");
											// WSClient.setData("{var_rateCode}",
											// OperaPropConfig.getChannelCodeForDataSet("RateCode",
											// "DS_03"));
											// WSClient.setData("{var_roomType}",
											// OperaPropConfig.getChannelCodeForDataSet("RoomType",
											// "DS_02"));
											modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_45");
											modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);
											if (WSAssert.assertIfElementExists(modifyBookingRes,
													"CreateBookingResponse_Result", true)) {
												if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
														"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS",
														false)) {
													WSClient.writeToReport(LogStatus.INFO,
															"<b>Verify that booking is not created when room count is greater than the sell limit configured for the channel</b>");
													WSClient.setData("{var_roomCount}", "5");
													modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking",
															"DS_45");
													modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);
													if (WSAssert.assertIfElementExists(modifyBookingRes,
															"CreateBookingResponse_Result", true)) {
														WSAssert.assertIfElementValueEquals(modifyBookingRes,
																"CreateBookingResponse_Result_resultStatusFlag", "FAIL",
																false);
														if (WSAssert.assertIfElementExists(modifyBookingRes,
																"ModifyBookingResponse_Result_GDSError", true)) {
															if (!WSClient.getElementValue(modifyBookingRes,
																	"CreateBookingResponse_Result_GDSError",
																	XMLType.RESPONSE).equals("*null*"))
																WSClient.writeToReport(LogStatus.INFO,
																		"<b> The error that is generated is : "
																				+ WSClient.getElementValue(
																						modifyBookingRes,
																						"CreateBookingResponse_Result_GDSError",
																						XMLType.RESPONSE)
																				+ "</b>");
														}
														if (WSAssert.assertIfElementExists(modifyBookingRes,
																"CreateBookingResponse_Result_GDSError_errorCode",
																true)) {
															if (!WSClient.getElementValue(modifyBookingRes,
																	"CreateBookingResponse_Result_GDSError_errorCode",
																	XMLType.RESPONSE).equals("*null*"))
																WSClient.writeToReport(LogStatus.INFO,
																		"<b> The error code that is generated is : "
																				+ WSClient.getElementValue(
																						modifyBookingRes,
																						"CreateBookingResponse_Result_GDSError_errorCode",
																						XMLType.RESPONSE)
																				+ "</b>");
														}
														if (WSAssert.assertIfElementExists(modifyBookingRes,
																"CreateBookingResponse_Result_OperaErrorCode", true)) {
															WSClient.writeToReport(LogStatus.INFO,
																	"<b> The error code that is generated is : "
																			+ WSClient.getElementValue(modifyBookingRes,
																					"CreateBookingResponse_Result_OperaErrorCode",
																					XMLType.RESPONSE)
																			+ "</b>");
														}
														if (WSAssert.assertIfElementExists(modifyBookingRes,
																"Result_Text_TextElement", true)) {
															String message = WSAssert.getElementValue(modifyBookingRes,
																	"Result_Text_TextElement", XMLType.RESPONSE);
															WSClient.writeToReport(LogStatus.INFO,
																	"<b>The text displayed in the response is :"
																			+ message + "</b>");
														}
													} else if (WSAssert.assertIfElementExists(modifyBookingRes,
															"CreateBookingResponse_faultstring", true)) {
														WSClient.writeToReport(LogStatus.FAIL,
																"<b> The error that is generated is : "
																		+ WSClient.getElementValue(modifyBookingRes,
																				"CreateBookingResponse_faultstring",
																				XMLType.RESPONSE)
																		+ "</b>");
													}
												}
												if (WSAssert.assertIfElementExists(modifyBookingRes,
														"CreateBookingResponse_Result_GDSError", true)) {
													if (!WSClient.getElementValue(modifyBookingRes,
															"CreateBookingResponse_Result_GDSError", XMLType.RESPONSE)
															.equals("*null*"))
														WSClient.writeToReport(LogStatus.INFO,
																"<b> The error that is generated is : "
																		+ WSClient.getElementValue(modifyBookingRes,
																				"CreateBookingResponse_Result_GDSError",
																				XMLType.RESPONSE)
																		+ "</b>");
												}
												if (WSAssert.assertIfElementExists(modifyBookingRes,
														"CreateBookingResponse_Result_GDSError_errorCode", true)) {
													if (!WSClient.getElementValue(modifyBookingRes,
															"CretaeBookingResponse_Result_GDSError_errorCode",
															XMLType.RESPONSE).equals("*null*"))
														WSClient.writeToReport(LogStatus.INFO,
																"<b> The error code that is generated is : "
																		+ WSClient.getElementValue(modifyBookingRes,
																				"CreateBookingResponse_Result_GDSError_errorCode",
																				XMLType.RESPONSE)
																		+ "</b>");
												}
												if (WSAssert.assertIfElementExists(modifyBookingRes,
														"CreateBookingResponse_Result_OperaErrorCode", true)) {
													WSClient.writeToReport(LogStatus.INFO,
															"<b> The error code that is generated is : "
																	+ WSClient.getElementValue(modifyBookingRes,
																			"CreateBookingResponse_Result_OperaErrorCode",
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
													"CreateBookingResponse_faultstring", true)) {
												WSClient.writeToReport(LogStatus.FAIL,
														"<b> The error that is generated is : "
																+ WSClient.getElementValue(modifyBookingRes,
																		"CreateBookingResponse_faultstring",
																		XMLType.RESPONSE)
																+ "</b>");
											}
										}
									} else if (WSAssert.assertIfElementExists(modifyBookingRes,
											"CreateBookingResponse_faultstring", true)) {
										WSClient.writeToReport(LogStatus.FAIL,
												"<b> The error that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"CreateBookingResponse_faultstring", XMLType.RESPONSE)
														+ "</b>");
									}
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"CreateBookingResponse_Result_GDSError", true)) {
									if (!WSClient.getElementValue(modifyBookingRes,
											"CreateBookingResponse_Result_GDSError", XMLType.RESPONSE).equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO, "<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"CreateBookingResponse_Result_GDSError", XMLType.RESPONSE)
												+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"CreateBookingResponse_Result_GDSError_errorCode", true)) {
									if (!WSClient
											.getElementValue(modifyBookingRes,
													"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											.equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO,
												"<b> The error code that is generated is : "
														+ WSClient.getElementValue(modifyBookingRes,
																"CreateBookingResponse_Result_GDSError_errorCode",
																XMLType.RESPONSE)
														+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"CreateBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"CreateBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}

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

			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Exception occured due to : " + e);
				e.printStackTrace();
			} finally {
				if (resvId != "") {
					WSClient.setData("{var_resvId}", resvId);
					try {
						if (CancelReservation.cancelReservation("DS_02")) {
							WSClient.writeToLog("Reservation cancellation successful");
						} else
							WSClient.writeToLog("Reservation cancellation failed");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}

	}

	// @Test(groups = { "minimumRegression", "CreateBooking", "Reservation",
	// "OWS" })

	public void createBooking_22314() {
		String uname = "", paramvalue = "", parameter = "", resvid = "";
		try {
			String testName = "createBooking_22314";
			WSClient.startTest(testName,
					"Verify that rooms are deducted from channel inventory when booking is created",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

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
							OperaPropConfig.getDataSetForCode("ReservationType", "DS_02"));

					WSClient.setData("{var_rateCode}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
					WSClient.setData("{var_roomCount}", "1");
					WSClient.setData("{var_resvType}", OperaPropConfig.getDataSetForCode("ReservationType", "DS_02"));
					WSClient.setData("{var_rate}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));

					/***********
					 * Prerequisite 1: Create profile
					 ***********************/
					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");
						WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_160}"));
						WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_161}"));
						WSClient.setData("{var_time}", "09:00:00");

						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_173}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_174}"));
						WSClient.setData("{var_roomLimit}", "10");

						WSClient.setData("{var_rateCode}",
								OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_02"));
						WSClient.setData("{var_roomType}",
								OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01"));
						WSClient.setData("{var_roomCount}", "2");

						String setSellLimitsReq = WSClient.createSOAPMessage("SetChannelSellLimitsByDateRange",
								"DS_03");
						String setSellLimitsRes = WSClient.processSOAPMessage(setSellLimitsReq);
						if (WSAssert.assertIfElementExists(setSellLimitsRes,
								"SetChannelSellLimitsByDateRangeRS_Success", true)) {
							String fetchLimitsReq = WSClient.createSOAPMessage("FetchChannelSellLimits", "DS_02");
							String fetchLimitsRes = WSClient.processSOAPMessage(fetchLimitsReq);
							if (WSAssert.assertIfElementExists(fetchLimitsRes, "FetchChannelSellLimitsRS_Success",
									true)) {
								WSClient.setData("{var_id}", WSClient.getElementValue(fetchLimitsRes,
										"SellLimits_SellLimit_SellLimitID_ID", XMLType.RESPONSE));
							}
							/****************
							 * Prerequisite 2:Create a Reservation
							 *****************/

							// WSClient.writeToReport(LogStatus.INFO,
							// "<b>Reservation ID:"+ resvid +"</b>");

							String modifyBookingNumber;

							/**********
							 * To store the rooms sold prior to modification
							 ****************/
							// query=WSClient.getQuery("OWSModifyBooking","QS_11");
							// results = WSClient.getDBRow(query);
							// createBookingNumber=results.get("NUMBER_SOLD");

							WSClient.setData("{var_startDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_173}"));
							String query = WSClient.getQuery("OWSModifyBooking", "QS_11");
							LinkedHashMap<String, String> results = WSClient.getDBRow(query);
							if (results.size() != 0)
								modifyBookingNumber = results.get("NUMBER_SOLD");
							else
								modifyBookingNumber = "0";

							query = WSClient.getQuery("OWSModifyBooking", "QS_26");
							results = WSClient.getDBRow(query);
							System.out.println("gds to sell ---- " + results);
							String toSellCount = results.get("NO_ROOMS");

							WSClient.setData("{var_endDate}",
									WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_174}"));

							/***************
							 * OWS Modify Booking Operation
							 ********************/
							String interfaceName = OWSLib.getChannel();
							String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

							OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
									OWSLib.getChannelType(interfaceName),
									OWSLib.getChannelCarier(resortOperaValue, interfaceName));

							String modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
							String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

							if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result",
									true)) {
								if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
										"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									WSClient.writeToReport(LogStatus.INFO, "<b>Validation of sold rooms</b>");
									query = WSClient.getQuery("OWSModifyBooking", "QS_11");
									results = WSClient.getDBRow(query);
									Integer expected = Integer.parseInt(modifyBookingNumber)
											+ Integer.parseInt(WSClient.getData("{var_roomCount}"));
									if (WSAssert.assertEquals(expected.toString(), results.get("NUMBER_SOLD"), true)) {
										WSClient.writeToReport(LogStatus.INFO, "<b>Rooms Sold -> Expected : " + expected
												+ "   Actual : " + results.get("NUMBER_SOLD") + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b>Rooms Sold -> Expected : " + expected
												+ "   Actual : " + results.get("NUMBER_SOLD") + "</b>");
									}

									query = WSClient.getQuery("OWSModifyBooking", "QS_26");
									results = WSClient.getDBRow(query);
									System.out.println("gds to sell afer ---- " + results);

									WSClient.writeToReport(LogStatus.INFO, "<b>Validation of rooms to be sold</b>");

									expected = Integer.parseInt(toSellCount)
											- Integer.parseInt(WSClient.getData("{var_roomCount}"));
									if (WSAssert.assertEquals(expected.toString(), results.get("NO_ROOMS"), true)) {
										WSClient.writeToReport(LogStatus.INFO, "<b>Rooms To Sell -> Expected : "
												+ expected + "   Actual : " + results.get("NO_ROOMS") + "</b>");
									} else {
										WSClient.writeToReport(LogStatus.FAIL, "<b>Rooms To Sell -> Expected : "
												+ expected + "   Actual : " + results.get("NO_ROOMS") + "</b>");
									}
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"CreateBookingResponse_Result_OperaErrorCode", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
									String message = WSAssert.getElementValue(modifyBookingRes,
											"Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message + "</b>");
								}
								if (WSAssert.assertIfElementExists(modifyBookingRes,
										"CreateBookingResponse_Result_GDSError", true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
								}
							} else if (WSAssert.assertIfElementExists(modifyBookingRes,
									"CreateBookingResponse_faultstring", true)) {
								WSClient.writeToReport(LogStatus.FAIL,
										"<b> The error that is generated is : "
												+ WSClient.getElementValue(modifyBookingRes,
														"CreateBookingResponse_faultstring", XMLType.RESPONSE)
												+ "</b>");
							}
						} else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Pre-Requisite failed >> Creating a booking failed");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Pre-Requisite failed >> Setting sell limits failed");
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

//	Need to debug later
//	@Test(groups = { "minimumRegression", "CreateBooking", "Reservation", "OWS", "PriorRun" })
	public void createBooking_23225() {
		try {
			resv = "";

			String testName = "createBooking_23225";
			WSClient.startTest(testName,
					"Verify that rate charges are applied correctly when posting rhythm is set for rate code "
							+ "and booking is modified to multi-night reservation",
					"minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

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
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_08"));
				WSClient.setData("{var_rate}", OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_09"));
				WSClient.setData("{var_time}", "09:00:00");

				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_07"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_07"));

				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				WSClient.setData("{var_ReservationType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_02"));
				WSClient.setData("{var_resvType}",
						OperaPropConfig.getChannelCodeForDataSet("ReservationType", "DS_02"));

				/************ Prerequisite 1: Create profile ****************/
				if (profileID.equals(""))
					profileID = CreateProfile.createProfile("DS_01");
				if (!profileID.equals("error")) {
					WSClient.setData("{var_profileId}", profileID);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: " + profileID + "</b>");

					/****************
					 * Prerequisite 2:Create a Reservation
					 *****************/

					String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_19}");
					int daysToAdd = 3;
					WSClient.setData("{var_startDate}", startDate);
					WSClient.setData("{var_endDate}", modifyDaysToDate(startDate, "yyyy-MM-dd", daysToAdd));
					WSClient.setData("{var_busdate}", startDate);
					WSClient.setData("{var_busdate1}", modifyDaysToDate(startDate, "yyyy-MM-dd", daysToAdd));

					/***************
					 * OWS Modify Booking Operation
					 ********************/
					String interfaceName = OWSLib.getChannel();
					String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

					OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue,
							OWSLib.getChannelType(interfaceName),
							OWSLib.getChannelCarier(resortOperaValue, interfaceName));

					String modifyBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
					String modifyBookingRes = WSClient.processSOAPMessage(modifyBookingReq);

					if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result", true)) {
						if (WSAssert.assertIfElementValueEquals(modifyBookingRes,
								"CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							resv = WSClient.getElementValueByAttribute(modifyBookingRes,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
							WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(modifyBookingRes,
									"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
							String query = WSClient.getQuery("OWSModifyBooking", "QS_36");
							HashMap<String, String> prices = WSClient.getDBRow(query);

							WSClient.writeToReport(LogStatus.INFO,
									"<b>Details of Rate Code : " + prices.get("RATE_CODE") + "</b>");
							WSClient.writeToReport(LogStatus.INFO,
									"<b> Charge For Adult 1 -> " + prices.get("AMOUNT_1") + "</b>");

							WSClient.writeToReport(LogStatus.INFO, "<b>Validating Charges in the DB</b>");

							query = WSClient.getQuery("QS_34");
							ArrayList<LinkedHashMap<String, String>> results = WSClient.getDBRows(query);

							ArrayList<LinkedHashMap<String, String>> expectedValues = new ArrayList<>();
							int rate = Integer.parseInt(prices.get("AMOUNT_1"));
							LinkedHashMap<String, String> e = new LinkedHashMap<>();
							for (int i = 0; i < daysToAdd; i++) {
								e = new LinkedHashMap<>();
								e.put("effectiveDate1", modifyDaysToDate(startDate, "yyyy-MM-dd", i));
								/********** Add rates alternatively ***********/
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
							query = WSClient.getQuery("QS_35");
							results = WSClient.getDBRows(query);
							xPath.clear();
							xPath.put("ExpectedCharges_ChargesForPostingDate_RoomRateAndPackages_TotalCharges",
									"RoomStay_ExpectedCharges_ChargesForPostingDate");
							xPath.put("RoomRateAndPackages_Charges_Amount",
									"RoomStay_ExpectedCharges_ChargesForPostingDate");
							xPath.put("RoomStay_ExpectedCharges_ChargesForPostingDate_PostingDate",
									"RoomStay_ExpectedCharges_ChargesForPostingDate");
							resValues = WSClient.getMultipleNodeList(modifyBookingRes, xPath, false, XMLType.RESPONSE);

							WSAssert.assertEquals(results, resValues, false);
						}
						if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_Result_GDSError",
								true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"CreateBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)
											+ "</b>");
						}
						if (WSAssert.assertIfElementExists(modifyBookingRes,
								"CreateBookingResponse_Result_OperaErrorCode", true)) {
							WSClient.writeToReport(LogStatus.INFO,
									"<b> The error code that is generated is : "
											+ WSClient.getElementValue(modifyBookingRes,
													"CreateBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)
											+ "</b>");
						}
						if (WSAssert.assertIfElementExists(modifyBookingRes, "Result_Text_TextElement", true)) {
							String message = WSAssert.getElementValue(modifyBookingRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message + "</b>");
						}
					} else if (WSAssert.assertIfElementExists(modifyBookingRes, "CreateBookingResponse_faultstring",
							true)) {
						WSClient.writeToReport(LogStatus.FAIL,
								"<b> The error that is generated is : " + WSClient.getElementValue(modifyBookingRes,
										"CreateBookingResponse_faultstring", XMLType.RESPONSE) + "</b>");
					}
				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			if (resv != null) {
				try {
					CancelReservation.cancelReservation("DS_02");
				} catch (Exception e) {
					WSClient.writeToReport(LogStatus.INFO, "Exception occured in test due to:" + e);
					e.printStackTrace();
				}
			}
		}
	}


//************ Inorder to Execute this script, Coupon codes should be created manually from V5 application
	//Reason: To Create coupon codes "Limited use" check box should be ticketed so that Coupons field is enabled. This option is applicable in OPERA V5, where as it not implemented in CLOUD UI. 
	//So it is required to manually enable the Limited use checkbox and create Coupons manually. 
	
	@Test(groups = { "minimumRegression", "CreateBooking", "OWS", "Reservation", "P1" })

	public void createBooking_usingPromotionCode() {
		try {
			resv = null;

			String testName = "createBooking_withCoupon_PromotionCode";
			WSClient.startTest(testName, "Verify that booking is Created with a promotion code", "minimumRegression");
			WSClient.setData("{var_owsResort}", OWSLib.getChannelResort(OPERALib.getResort(), OWSLib.getChannel()));

			String rate = OperaPropConfig.getChannelCodeForDataSet("RateCode", "DS_12");
			WSClient.setData("{var_rate}", rate);
			String rt = OperaPropConfig.getChannelCodeForDataSet("RoomType", "DS_01");
			WSClient.setData("{var_roomType}", rt);
			String ort = OperaPropConfig.getDataSetForCode("RateCode", "DS_15");
			WSClient.setData("{var_orate}", ort);
			String resvt = OperaPropConfig.getDataSetForCode("ReservationType", "DS_01");
			WSClient.setData("{var_resvType}", resvt);
			WSClient.setData("{var_reservationType}", resvt);
			String interfaceName = OWSLib.getChannel();
			System.out.println(interfaceName);
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String prom = OperaPropConfig.getDataSetForCode("PromotionCode", "DS_04");
			
			WSClient.setData("{var_chain}", OPERALib.getChain());
			WSClient.setData("{var_resort}", resortOperaValue);
			// String prom = "cult";
		
			System.out.println(resortExtValue);
			OPERALib.setOperaHeader(OPERALib.getUserName());
			// ****** Prerequisite : Creating a Profile with basic
			// details*****//
			profileID = CreateProfile.createProfile("DS_00");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);

				/*******************
				 * Prerequisite 2:Create a Reservation
				 ************************/

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_130}")+"T06:00:00");
				WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_135}")+"T06:00:00");
				WSClient.setData("{var_time}", "09:00:00");

				OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resortOperaValue, OWSLib.getChannelType(interfaceName), OWSLib.getChannelCarier(resortOperaValue, interfaceName));
				
				
				fetchAvailability(rate,rt);
				System.out.println("fetch Availability is completed");
				
				// Get Coupon code from DB
				System.out.println("prom: "+prom);
				WSClient.setData("{var_promotionCode}", prom);
				String couponquery = WSClient.getQuery("OWSCreateBooking","QS_43");
				//WSClient.getQuery(operationKeyWord, querySetID)
				
				LinkedHashMap<String, String> couponMap = WSClient.getDBRow(couponquery);
				String couponCode="";
				if(couponMap.containsKey("C_COUPON_CODE")) {
					 couponCode= couponMap.get("C_COUPON_CODE");
				}
				WSClient.setData("{var_couponCode}", couponCode);
				
				//Add code block for adding a new coupon code	
				String updateProfileReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_47");
				String updateProfileResponseXML = WSClient.processSOAPMessage(updateProfileReq);
				System.out.println("Creating SOAP Request for create booking");
				WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML, "HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
				if (WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					WSClient.setData("{var_resvId}", WSClient.getElementValueByAttribute(updateProfileResponseXML, "HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
					resv = WSClient.getElementValueByAttribute(updateProfileResponseXML, "HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
					if (WSAssert.assertIfElementExists(updateProfileResponseXML, "CreateBookingResponse_Result_Text", true)) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Create Booking Response gives the error message-----></b> " + WSClient.getElementValue(updateProfileResponseXML, "CreateBookingResponse_Result_Text", XMLType.RESPONSE));
					}

					// validating the data

					String elem = WSClient.getElementValue(updateProfileResponseXML, "RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
					String elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Rate Plan Code</b>");

					if (WSAssert.assertEquals(elem, elem1, true)) {
						WSClient.writeToReport(LogStatus.PASS, "Expected value:" + elem + " Actual value :" + elem1);
					} else
						WSClient.writeToReport(LogStatus.FAIL, "Expected value:" + elem + " Actual value :" + elem1);
					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "Profile_ProfileIDs_UniqueID", profileID, false);
					elem = WSClient.getElementValue(updateProfileResponseXML, "RoomStay_RoomRates_RoomRate_roomTypeCode", XMLType.RESPONSE);
					elem1 = WSClient.getElementValue(updateProfileReq, "RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.REQUEST);

					WSClient.setData("{var_resv}", WSClient.getElementValue(updateProfileResponseXML, "HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE));

					WSClient.writeToReport(LogStatus.INFO, "<b>Validating Promotion Code</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>Response Validation :</b>");

				//	WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "RoomStay_RatePlans_RatePlan_promotionCode", prom, false);
					WSClient.writeToReport(LogStatus.INFO, "<b>DB Validation :</b>");

					String query1 = WSClient.getQuery("QS_44");
					LinkedHashMap<String, String> promoDets = WSClient.getDBRow(query1);

					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "RoomStay_RatePlans_RatePlan_promotionCode", promoDets.get("C_COUPON_CODE"), false);
					
					//************
//					String query1 = WSClient.getQuery("QS_40");
//					LinkedHashMap<String, String> promoDets = WSClient.getDBRow(query1);
//
//					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "RoomStay_RatePlans_RatePlan_promotionCode", promoDets.get("C_COUPON_CODE"), false);
					
//					// Coupon code validation
//					
//					String query2 = WSClient.getQuery("QS_39");
//					LinkedHashMap<String, String> promoCouponDets = WSClient.getDBRow(query2);
//
//					WSAssert.assertIfElementValueEquals(updateProfileResponseXML, "RoomStay_RatePlans_RatePlan_promotionCode", promoCouponDets.get("PROMOTIONS"), false);

				} else

					WSClient.writeToReport(LogStatus.ERROR, "Create Booking fails");

			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
//				System.out.println(WSClient.getData("{var_resvId}"));
//				if(WSClient.getData("{var_resvId}").toString().length()!=0){
//
//					CancelReservation.cancelReservation("DS_02");
//				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}