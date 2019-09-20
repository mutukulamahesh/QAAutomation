package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CheckoutReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchBooking extends WSSetUp{

	String profileID="";
	@Test(groups = { "sanity", "FetchBooking", "OWS" , "Reservation"})
	public void fetchBooking_40521() {
		try {
			String testName = "fetchBooking_40521";
			WSClient.startTest(testName,
					"verify that the requested booking created through PMS is fetched when FETCH_RSERVATION=ALL",
					"sanity");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				WSClient.setData("{var_channel}", channel);
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/***** check fetch_reservation parameter will be ALL *****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}
				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");

						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");

							// ******OWS Fetch Booking*******//

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchBookingReq = WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);


							if (WSAssert.assertIfElementExists(fetchBookingRes,
									"FetchBookingResponse_Result_resultStatusFlag", false)) {
								if (WSAssert.assertIfElementValueEquals(fetchBookingRes,
										"FetchBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									// DB Validation

									LinkedHashMap<String, String> actuals = new LinkedHashMap<String, String>();
									actuals.put("RESORT", WSClient.getElementValue(fetchBookingRes,
											"RoomStays_RoomStay_HotelReference_hotelCode", XMLType.RESPONSE));
									actuals.put("NAME_ID", WSClient.getElementValue(fetchBookingRes,
											"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));
									actuals.put("RESV_NAME_ID", WSClient.getElementValueByAttribute(fetchBookingRes,
											"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
									actuals.put("CONFIRMATION_NO", WSClient.getElementValueByAttribute(fetchBookingRes,
											"HotelReservation_UniqueIDList_UniqueID", "HotelReservation_UniqueIDList_UniqueID_source","", XMLType.RESPONSE));
									actuals.put("RESV_STATUS",
											WSClient.getElementValue(fetchBookingRes,
													"FetchBookingResponse_HotelReservation_reservationStatus",
													XMLType.RESPONSE));
									LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_01"));

									WSAssert.assertEquals(db, actuals, false);

								}
							}
							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
							}


						}
					}

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})

	public void fetchBooking_40522() {
		try {

			String testName = "fetchBooking_40522";
			WSClient.startTest(testName, "verify that the requested booking created through web channel is fetched when FETCH_RSERVATION=ALL","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "ReservationType" })) {

				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				WSClient.setData("{var_channel}", channel);
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				WSClient.setData("{var_owsResort}", resort);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/***** check fetch_reservation parameter will be ALL *****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}
				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");

						//  Prerequisite 2 Create Booking

						WSClient.setData("{var_profileSource}",channel);
						WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_15}")+"T06:00:00");
						WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_17}")+"T06:00:00");
						WSClient.setData("{var_time}", "09:00:00");
						WSClient.setData("{var_rate}",OperaPropConfig.getChannelCodeForDataSet("RateCode","DS_01"));
						WSClient.setData("{var_resvType}",OperaPropConfig.getChannelCodeForDataSet("ReservationType","DS_01"));
						WSClient.setData("{var_roomType}",OperaPropConfig.getChannelCodeForDataSet("RoomType","DS_01"));
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String createBookingRes = WSClient.processSOAPMessage(createBookingReq);

						if(WSAssert.assertIfElementValueEquals(createBookingRes,"CreateBookingResponse_Result_resultStatusFlag","SUCCESS", true)){

							String reservationId1=	 WSClient.getElementValueByAttribute(createBookingRes,"HotelReservation_UniqueIDList_UniqueID","RESVID", XMLType.RESPONSE);
							WSClient.setData("{var_resvId}", reservationId1);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+reservationId1+"</b>");

							//******OWS Fetch Booking*******//
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchBookingReq1= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes1 = WSClient.processSOAPMessage(fetchBookingReq1);



							if(WSAssert.assertIfElementExists(fetchBookingRes1, "FetchBookingResponse_Result_resultStatusFlag",false))
							{
								if(WSAssert.assertIfElementValueEquals(fetchBookingRes1, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
								{
									// DB Validation

									LinkedHashMap<String,String> actuals= new LinkedHashMap<String,String>();
									actuals.put("RESORT", WSClient.getElementValue(fetchBookingRes1, "RoomStays_RoomStay_HotelReference_hotelCode", XMLType.RESPONSE));
									actuals.put("NAME_ID",WSClient.getElementValue(fetchBookingRes1,"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));
									actuals.put("RESV_NAME_ID",WSClient.getElementValueByAttribute(fetchBookingRes1,"HotelReservation_UniqueIDList_UniqueID","RESVID", XMLType.RESPONSE));

									actuals.put("CONFIRMATION_NO", WSClient.getElementValueByAttribute(fetchBookingRes1,
											"HotelReservation_UniqueIDList_UniqueID", "HotelReservation_UniqueIDList_UniqueID_source","", XMLType.RESPONSE));
									actuals.put("RESV_STATUS",WSClient.getElementValue(fetchBookingRes1,"FetchBookingResponse_HotelReservation_reservationStatus", XMLType.RESPONSE));
									LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_01"));

									WSAssert.assertEquals(db, actuals, false);

								}
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes1,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes1,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes1,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes1, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes1, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes1,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes1, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes1,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes1, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to create booking");
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
			// Cancel Reservation
			try
			{
				if(!WSClient.getData("{var_resvId}").equals("")){

					CancelReservation.cancelReservation("DS_02");
				}
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation","Failed"})

	public void fetchBooking_40551() throws Exception {
		try {

			String testName = "fetchBooking_40551";
			WSClient.startTest(testName, "verify that the requested booking created through PMS is not getting fetched when FETCH_RESERVATION=CHANNELTYPE","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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

				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/*****check fetch_reservation parameter will be CHANNEL TYPE*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "CHANNELTYPE");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=CHANNELTYPE</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("CHANNELTYPE")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to CHANNELTYPE</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");

						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");


							//******OWS Fetch Booking*******//

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);




							if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
							{
								WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","FAIL", false);

							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
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
			WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
			WSClient.setData("{var_settingValue}", "ALL");

			WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
			ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			// Cancel Reservation
			try
			{  if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})

	public void fetchBooking_40553() throws Exception {
		try {

			String testName = "fetchBooking_40553";
			WSClient.startTest(testName, "verify that the requested booking create through PMS is not getting fetched when FETCH_RESERVATION=CHANNEL","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/*****check fetch_reservation parameter will be CHANNEL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "CHANNEL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=CHANNEL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("CHANNEL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to CHANNEL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");

						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");



							//******OWS Fetch Booking*******//

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);


							if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
							{
								WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","FAIL", false);

							}
							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
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
			WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
			WSClient.setData("{var_settingValue}", "ALL");

			WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");

			ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})

	public void fetchBooking_40554() throws Exception {
		try {

			String testName = "fetchBooking_40554";
			WSClient.startTest(testName, "verify that the requested booking created through WEB Channel is  getting fetched when FETCH_RESERVATION=CHANNEL","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "ReservationType"})) {

				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				WSClient.setData("{var_channel}", channel);
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				WSClient.setData("{var_owsResort}", resort);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/*****check fetch_reservation parameter will be CHANNEL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "CHANNEL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=CHANNEL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("CHANNEL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to CHANNEL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");



						//  Prerequisite 2 Create Booking

						WSClient.setData("{var_profileSource}",channel);
						WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_15}")+"T06:00:00");
						WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_17}")+"T06:00:00");
						WSClient.setData("{var_rate}",OperaPropConfig.getChannelCodeForDataSet("RateCode","DS_01"));
						WSClient.setData("{var_resvType}",OperaPropConfig.getChannelCodeForDataSet("ReservationType","DS_01"));
						WSClient.setData("{var_roomType}",OperaPropConfig.getChannelCodeForDataSet("RoomType","DS_01"));
						WSClient.setData("{var_time}", "09:00:00");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String createBookingRes = WSClient.processSOAPMessage(createBookingReq);

						if(WSAssert.assertIfElementValueEquals(createBookingRes,"CreateBookingResponse_Result_resultStatusFlag","SUCCESS", true)){

							String reservationId1=	 WSClient.getElementValueByAttribute(createBookingRes,"HotelReservation_UniqueIDList_UniqueID","RESVID", XMLType.RESPONSE);
							WSClient.setData("{var_resvId}", reservationId1);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+reservationId1+"</b>");
							//******OWS Fetch Booking*******//
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchBookingReq1= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes1 = WSClient.processSOAPMessage(fetchBookingReq1);


							if(WSAssert.assertIfElementExists(fetchBookingRes1, "FetchBookingResponse_Result_resultStatusFlag",false))
							{
								if(WSAssert.assertIfElementValueEquals(fetchBookingRes1, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
								{
									// DB Validation

									LinkedHashMap<String,String> actuals= new LinkedHashMap<String,String>();
									actuals.put("RESORT", WSClient.getElementValue(fetchBookingRes1, "RoomStays_RoomStay_HotelReference_hotelCode", XMLType.RESPONSE));
									actuals.put("NAME_ID",WSClient.getElementValue(fetchBookingRes1,"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));
									actuals.put("RESV_NAME_ID",WSClient.getElementValueByAttribute(fetchBookingRes1,"HotelReservation_UniqueIDList_UniqueID","RESVID", XMLType.RESPONSE));
									actuals.put("CONFIRMATION_NO", WSClient.getElementValueByAttribute(fetchBookingRes1,
											"HotelReservation_UniqueIDList_UniqueID", "HotelReservation_UniqueIDList_UniqueID_source","", XMLType.RESPONSE));
									actuals.put("RESV_STATUS",WSClient.getElementValue(fetchBookingRes1,"FetchBookingResponse_HotelReservation_reservationStatus", XMLType.RESPONSE));
									LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_01"));

									WSAssert.assertEquals(db, actuals, false);

								}
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes1,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes1,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes1,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes1, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes1, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes1,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes1, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes1,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes1, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to create booking");
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
			WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
			WSClient.setData("{var_settingValue}", "ALL");

			WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
			ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})

	public void fetchBooking_10861() {
		try {

			String testName = "fetchBooking_10861";
			WSClient.startTest(testName, "verify that the requested booking is getting fetched when Fetch Booking is called with confirmation number","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				WSClient.setData("{var_channel}", channel);
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);


				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {

						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");

						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation

						HashMap<String,String> resv=CreateReservation.createReservation("DS_04");
						String resvID = resv.get("reservationId");
						String confirmationNo = resv.get("confirmationId");
						if(!resvID.equals("error"))
						{

							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_confNo}", confirmationNo);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");

							//******OWS Fetch Booking*******//

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_02");
							String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);

							if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
							{
								if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
								{
									// DB Validation
									LinkedHashMap<String,String> actuals= new LinkedHashMap<String,String>();
									actuals.put("RESORT", WSClient.getElementValue(fetchBookingRes, "RoomStays_RoomStay_HotelReference_hotelCode", XMLType.RESPONSE));
									actuals.put("NAME_ID",WSClient.getElementValue(fetchBookingRes,"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));
									actuals.put("RESV_NAME_ID",WSClient.getElementValueByAttribute(fetchBookingRes,"HotelReservation_UniqueIDList_UniqueID","RESVID", XMLType.RESPONSE));
									actuals.put("CONFIRMATION_NO", WSClient.getElementValueByAttribute(fetchBookingRes,
											"HotelReservation_UniqueIDList_UniqueID", "HotelReservation_UniqueIDList_UniqueID_source","", XMLType.RESPONSE));
									actuals.put("RESV_STATUS",WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_HotelReservation_reservationStatus", XMLType.RESPONSE));
									LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_02"));

									WSAssert.assertEquals(db, actuals, false);

								}
							}


							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
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
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation","Failed"})

	public void fetchBooking_10872() {
		try {

			String testName = "fetchBooking_10872";
			WSClient.startTest(testName, "verify that room details are being displayed correctly on fetch booking response","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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
				String roomNumber="";

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");

						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");


							// Prerequisite 3: Fetching available Hotel rooms with room type


							String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms","DS_03");
							String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

							if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes,"FetchHotelRoomsRS_HotelRooms_Room", true)) {

								roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

							}
							else
							{

								//Prerequisite 4: Creating a room to assign

								String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
								String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

								if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

									roomNumber = WSClient.getElementValue(createRoomReq,"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to create room");
								}
							}
							if(!roomNumber.equals("")){

								WSClient.setData("{var_roomNumber}", roomNumber);
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully fetched/created room.</b>");
								//Prerequisite 5: Changing the room status to inspected to assign the room for checking in

								String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
								String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

								if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,"SetHousekeepingRoomStatusRS_Success", true)) {

									// Prerequisite 6: Assign Room
									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully changed the status of room.</b>");
									String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
									String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

									if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",true)) {
										WSClient.writeToReport(LogStatus.INFO, "<b>Successfully assigned room.</b>");

										//******OWS Fetch Booking*******//


										OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
										String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");

										String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);


										if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
										{
											if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
											{
												// DB Validation
												LinkedHashMap<String,String> actuals= new LinkedHashMap<String,String>();
												actuals.put("ROOM", WSClient.getElementValue(fetchBookingRes, "RoomTypes_RoomType_RoomNumber", XMLType.RESPONSE));
												actuals.put("ROOM_STATUS",WSClient.getElementValue(fetchBookingRes,"RoomStay_RoomTypes_RoomType_roomStatus", XMLType.RESPONSE));
												actuals.put("ROOM_TYPE",WSClient.getElementValue(fetchBookingRes,"RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.RESPONSE));
												actuals.put("RATE_CODE",WSClient.getElementValue(fetchBookingRes,"RoomStay_RoomRates_RoomRate_ratePlanCode", XMLType.RESPONSE));

												LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_03"));

												WSAssert.assertEquals(db, actuals, false);


											}
										}


										if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
											if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

												String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
											}
										}

										if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

											String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
													XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is :" + message+"</b>");
										}

										if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

											String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
										}

										if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

											String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
										}

									}
									else
									{
										WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to assign room");
									}
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to change the status of room to vacant and inspected");
								}
							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to fetch room");
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
			// Cancel Reservation
			try
			{
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation","Failed"})

	public void fetchBooking_40560() {
		try {

			String testName = "fetchBooking_40560";
			WSClient.startTest(testName, "verify that package details are being displayed correctly on fetch booking response","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}


				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {


					profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {

						WSClient.setData("{var_profileId}",profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));

						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));


						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_22");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
							WSClient.setData("{var_packageCode}",OperaPropConfig.getDataSetForCode("PackageCode", "DS_06"));

							// Pre-requisite : Change Reservation to add accompany guest

							String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_07");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							//******OWS Fetch Booking*******//

							if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added package to the reservation.</b>");

								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
								String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);



								if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
								{
									if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
									{
										// DB Validation
										LinkedHashMap<String,String> actuals= new LinkedHashMap<String,String>();
										actuals.put("PRODUCT_ID", WSClient.getElementValue(fetchBookingRes, "RoomStay_Packages_Package_packageCode", XMLType.RESPONSE));
										actuals.put("DESCRIPTION", WSClient.getElementValue(fetchBookingRes, "Description_Text_TextElement_3", XMLType.RESPONSE));
										actuals.put("SHORT_DESCRIPTION", WSClient.getElementValue(fetchBookingRes, "ShortDescription_Text_TextElement_3", XMLType.RESPONSE));
										actuals.put("PRODUCT_SOURCE", WSClient.getElementValue(fetchBookingRes, "RoomStay_Packages_Package_source", XMLType.RESPONSE));

										LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_04"));
										WSAssert.assertEquals(db, actuals, false);
										LinkedHashMap<String,String> db1=WSClient.getDBRow(WSClient.getQuery("QS_42"));
										LinkedHashMap<String,String> actuals1= new LinkedHashMap<String,String>();
										actuals1.put("PRICE", WSClient.getElementValue(fetchBookingRes, "Packages_Package_PackageAmount_3", XMLType.RESPONSE));
										actuals1.put("ALLOWANCE_AMOUNT", WSClient.getElementValue(fetchBookingRes, "Packages_Package_Allowance_3", XMLType.RESPONSE));
										// actuals1.put("PRODUCT_SOURCE", WSClient.getElementValue(fetchBookingRes, "Packages_Package_Allowance", XMLType.RESPONSE));

										WSAssert.assertEquals(db1, actuals1, false);


									}
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

										String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
									}
								}

								if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

									String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

									String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

									String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
								}

							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to add package to reservation");
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
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation","Failed"})

	public void fetchBooking_40562() {
		try {

			String testName = "fetchBooking_40562";
			WSClient.startTest(testName, "verify that correct information are being displayed on fetch booking response for cancelled reservation.","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod","ResvCancelReason"})) {

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


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");


							//  Prerequisite 3 - Cancel Reservation

							if(CancelReservation.cancelReservation("DS_02")){

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully cancelled reservation.</b>");

								//******OWS Fetch Booking*******//

								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
								String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);



								if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
								{
									if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
									{

										// DB Validation
										LinkedHashMap<String,String> actuals= new LinkedHashMap<String,String>();
										actuals.put("RESV_NAME_ID",WSClient.getElementValueByAttribute(fetchBookingRes,"HotelReservation_UniqueIDList_UniqueID","RESVID", XMLType.RESPONSE));
										String status=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_HotelReservation_reservationStatus", XMLType.RESPONSE);
										if(status.equals("CANCELED"))
											status="CANCELLED";
										actuals.put("RESV_STATUS",status);
										actuals.put("CANCELLATION_NO",WSClient.getElementValue(fetchBookingRes,"RoomStays_RoomStay_CancelTerm_cancelNumber", XMLType.RESPONSE));
										actuals.put("CANCELLATION_DATE",WSClient.getElementValue(fetchBookingRes,"RoomStays_RoomStay_CancelTerm_cancelDate", XMLType.RESPONSE));
										LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_05"));

										WSAssert.assertEquals(db, actuals, false);

									}
								}


								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

										String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
									}
								}

								if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

									String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

									String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

									String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
								}

							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to cancel reservation");
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

		}
	}
	
	
	//@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation","Failed"})

	public void fetchBooking_40583() {
		try {

			String testName = "fetchBooking_40583";
			WSClient.startTest(testName, "verify that correct information are being displayed on fetch booking response for checked in reservation.","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod","Cashiers","TransactionCode"})) {

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


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);
				String roomNumber = "";

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation

						HashMap<String,String> resv=CreateReservation.createReservation("DS_12");
						String resvID = resv.get("reservationId");
						String confirmationNo = resv.get("confirmationId");
						if(!resvID.equals("error"))
						{

							WSClient.setData("{var_resvId}", resvID);
							WSClient.setData("{var_confNo}", confirmationNo);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
							// Prerequisite 3: Fetching available Hotel rooms with room type


							String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
							String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

							if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes,"FetchHotelRoomsRS_HotelRooms_Room", true)) {

								roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

							}
							else{

								//Prerequisite 4: Creating a room to assign

								String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
								String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

								if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

									roomNumber = WSClient.getElementValue(createRoomReq,"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);

								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to create room");
								}
							}
							if(!roomNumber.equals("")){
								WSClient.setData("{var_roomNumber}", roomNumber);
								WSClient.writeToReport(LogStatus.INFO,"<b>Successfully fetched/created room.</b>");
								//Prerequisite 5: Changing the room status to inspected to assign the room for checking in

								String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
								String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

								if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,"SetHousekeepingRoomStatusRS_Success", true)) {

									// Prerequisite 6: Assign Room
									WSClient.writeToReport(LogStatus.INFO,"<b>Successfully changed status of room.</b>");
									String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
									String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

									if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",true)) {
										WSClient.writeToReport(LogStatus.INFO,"<b>Successfully assigned room.</b>");
										//Prerequisite 7: CheckIn Reservation

										String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
										String checkInRes = WSClient.processSOAPMessage(checkInReq);

										if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){
											WSClient.writeToReport(LogStatus.INFO,"<b>Successfully checked in the reservation.</b>");
											//Prerequisite 8 :Post billing charges

											WSClient.setData("{var_cashierID}", OperaPropConfig.getDataSetForCode("Cashiers","DS_03"));
											WSClient.setData("{var_trx}", OperaPropConfig.getDataSetForCode("TransactionCode","DS_04"));
											String postBillingReq = WSClient.createSOAPMessage("PostBillingCharges","DS_01");
											String postBillingRes = WSClient.processSOAPMessage(postBillingReq);

											String query=WSClient.getQuery("QS_01");
											LinkedHashMap<String,String> count=WSClient.getDBRow(query);
											String c=count.get("COUNT");
											int no=Integer.parseInt(c);
											if(WSAssert.assertIfElementExists(postBillingRes,"PostBillingChargesRS_Success",true) && no>0){

												//*****OWS Fetch Booking*********//
												WSClient.writeToReport(LogStatus.INFO,"<b>Successfully posted the charges.</b>");
												OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
												String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
												String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);


												if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
												{
													if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
													{
														// DB Validation
														LinkedHashMap<String,String> actuals= new LinkedHashMap<String,String>();
														actuals.put("RESV_NAME_ID",WSClient.getElementValueByAttribute(fetchBookingRes,"HotelReservation_UniqueIDList_UniqueID","RESVID", XMLType.RESPONSE));

														String resvStatus=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_HotelReservation_reservationStatus", XMLType.RESPONSE);
														if(resvStatus.equalsIgnoreCase("INHOUSE"))
															actuals.put("RESV_STATUS","CHECKED IN");
														else

															actuals.put("RESV_STATUS", resvStatus);

														actuals.put("ROOM", WSClient.getElementValue(fetchBookingRes, "RoomTypes_RoomType_RoomNumber", XMLType.RESPONSE));
														actuals.put("NET_AMOUNT",WSClient.getElementValue(fetchBookingRes,"RoomStays_RoomStay_CurrentBalance", XMLType.RESPONSE));
														actuals.put("GUARANTEE_CODE",WSClient.getElementValue(fetchBookingRes,"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE));

														LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_06"));

														WSAssert.assertEquals(db, actuals, false);


													}
												}

												if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
													if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

														String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
														WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
													}
												}

												if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

													String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
															XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>The text displayed in the response is :" + message+"</b>");
												}

												if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

													String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
												}

												if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

													String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
												}


											}

											else
											{
												WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to post charges");
											}
										}
										else
										{
											WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to checkin reservation");
										}
									}
									else
									{
										WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to assign room");
									}

								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to change the status of room to vacant and inspected");
								}

							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to fetch/create room");
							}
						}
					}
				}
			}

		}
		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		finally
		{
			// checking out reservation
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CheckoutReservation.checkOutReservation("DS_01");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation","Failed"})

	public void fetchBooking_40642() {
		try {

			String testName = "fetchBooking_40642";
			WSClient.startTest(testName, "Verify that correct information are being displayed for shared reservation","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {


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

										//******OWS Fetch Booking*******//

										OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
										String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
										String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);


										if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
										{
											if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
											{
												// Validate from DB from reservation_name table
												List<LinkedHashMap<String,String>> db=WSClient.getDBRows(WSClient.getQuery("QS_16"));
												HashMap<String,String> xPath=new HashMap<String,String>();
												xPath.put("ShareReservationDetails_UniqueIDList_UniqueID","HotelReservation_ShareReservations_ShareReservationDetails");
												xPath.put("Profile_ProfileIDs_UniqueID_2","HotelReservation_ShareReservations_ShareReservationDetails");
												xPath.put("Customer_PersonName_lastName_2","HotelReservation_ShareReservations_ShareReservationDetails");
												xPath.put("Customer_PersonName_firstName_2","HotelReservation_ShareReservations_ShareReservationDetails");

												List<LinkedHashMap<String,String>> actuals=WSClient.getMultipleNodeList(fetchBookingRes, xPath,true, XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO,"<b>Validating shared reservation</b>");
												WSAssert.assertEquals( actuals,db,false);
											}
										}

										if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
											if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

												String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
											}
										}

										if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

											String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
													XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is :" + message+"</b>");
										}

										if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

											String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
										}

										if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

											String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
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

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS", "Reservation" })

	public void fetchBooking_40600() {
		try {

			String testName = "fetchBooking_40600";
			WSClient.startTest(testName, "verify that correct information are being displayed on fetch booking response for checked out reservation.", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				WSClient.setData("{var_channel}", channel);
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
				String roomNumber = "";

				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/***** check fetch_reservation parameter will be ALL *****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_12");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
							// Prerequisite 3: Fetching available Hotel rooms with
							// room type

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
							if(!roomNumber.equals("")){
								WSClient.setData("{var_roomNumber}", roomNumber);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>Succesfully fetched/create room.</b>");
								// Prerequisite 5: Changing the room status to inspected
								// to assign the room for checking in

								String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus",
										"DS_01");
								String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

								if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,
										"SetHousekeepingRoomStatusRS_Success", true)) {
									WSClient.writeToReport(LogStatus.INFO,
											"<b>Succesfully changed the status of room.</b>");

									// Prerequisite 6: Assign Room

									String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
									String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

									if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {
										WSClient.writeToReport(LogStatus.INFO,
												"<b>Succesfully assigned room.</b>");

										// Prerequisite 7: CheckIn Reservation

										String checkInReq = WSClient.createSOAPMessage("CheckinReservation", "DS_01");
										String checkInRes = WSClient.processSOAPMessage(checkInReq);

										if (WSAssert.assertIfElementExists(checkInRes, "CheckinReservationRS_Success", true)) {

											WSClient.writeToReport(LogStatus.INFO,
													"<b>Succesfully checked in the reservation.</b>");


											if (CheckoutReservation.checkOutReservation("DS_01")) {

												WSClient.writeToReport(LogStatus.INFO,
														"<b>Succesfully checked out the reservation.</b>");
												// ******OWS Fetch Booking*******//

												OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
												String fetchBookingReq = WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
												String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);


												if (WSAssert.assertIfElementExists(fetchBookingRes,
														"FetchBookingResponse_Result_resultStatusFlag", false)) {
													if (WSAssert.assertIfElementValueEquals(fetchBookingRes,
															"FetchBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
														// DB Validation

														LinkedHashMap<String,String> actuals= new LinkedHashMap<String,String>();

														actuals.put("RESORT", WSClient.getElementValue(fetchBookingRes, "RoomStays_RoomStay_HotelReference_hotelCode", XMLType.RESPONSE));

														actuals.put("NAME_ID",WSClient.getElementValue(fetchBookingRes,"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));

														actuals.put("RESV_NAME_ID",WSClient.getElementValueByAttribute(fetchBookingRes,"HotelReservation_UniqueIDList_UniqueID","RESVID", XMLType.RESPONSE));

														actuals.put("CONFIRMATION_NO", WSClient.getElementValueByAttribute(fetchBookingRes,
																"HotelReservation_UniqueIDList_UniqueID", "HotelReservation_UniqueIDList_UniqueID_source","", XMLType.RESPONSE));

														String resvStatus=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_HotelReservation_reservationStatus", XMLType.RESPONSE);

														if(resvStatus.equalsIgnoreCase("CHECKEDOUT"))
															actuals.put("RESV_STATUS","CHECKED OUT");
														else

															actuals.put("RESV_STATUS", resvStatus);
														if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_HotelReservation_checkOutTime",true))
															actuals.put("CHECK_OUT_TIME",WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_HotelReservation_checkOutTime", XMLType.RESPONSE).substring(0,8));
														else
															actuals.put("CHECK_OUT_TIME",WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_HotelReservation_checkOutTime", XMLType.RESPONSE));

														LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_07"));

														WSAssert.assertEquals(db, actuals, false);
													}
												}

												if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
													if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

														String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
														WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
													}
												}

												if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

													String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
															XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO,
															"<b>The text displayed in the response is :" + message+"</b>");
												}

												if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

													String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
												}

												if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

													String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
												}



											} else {
												WSClient.writeToReport(LogStatus.WARNING,
														"Blocked : Unable to checkout reservation");
											}
										}

										else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Blocked : Unable to checkin reservation");
										}
									} else {
										WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to assign room");
									}
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,
											"Blocked : Unable to fetch/create room");
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

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation","Failed"})

	public void fetchBooking_10865() {
		try {

			String testName = "fetchBooking_10865";
			WSClient.startTest(testName, "verify that the requested booking is getting fetched when Fetch Booking is called with confirmation number and leg number","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				WSClient.setData("{var_channel}", channel);
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);


				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
						String confID = resv.get("confirmationId");

						if (!confID.equals("error")) {

							WSClient.setData("{var_confirmationId}", confID);

							WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation No.: "+confID+"</b>");

							HashMap<String, String> resv1 = CreateReservation.createReservation("DS_10");
							String resvID1 = resv1.get("reservationId");
							String confID1 = resv1.get("confirmationId");

							if (!confID1.equals("error")) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Confirmation No.: "+confID1+"</b>");

								WSClient.setData("{var_confirmationId}", confID1);
								WSClient.setData("{var_resvId}", resvID1);

								String legNo = WSClient.getDBRow(WSClient.getQuery("OWSFetchBooking","QS_08")).get("CONFIRMATION_LEG_NO");
								WSClient.setData("{var_legNo}", legNo);

								WSClient.setData("{var_confNo}", confID1);

								WSClient.writeToReport(LogStatus.INFO, "<b>Leg Number : "+legNo+"</b>");

								//******OWS Fetch Booking*******//

								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

								String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_03");
								String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);


								if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
								{
									if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
									{
										// DB Validation
										LinkedHashMap<String,String> actuals= new LinkedHashMap<String,String>();
										actuals.put("RESORT", WSClient.getElementValue(fetchBookingRes, "RoomStays_RoomStay_HotelReference_hotelCode", XMLType.RESPONSE));
										actuals.put("NAME_ID",WSClient.getElementValue(fetchBookingRes,"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));
										actuals.put("RESV_NAME_ID",WSClient.getElementValueByAttribute(fetchBookingRes,"HotelReservation_UniqueIDList_UniqueID","RESVID", XMLType.RESPONSE));
										actuals.put("CONFIRMATION_NO", WSClient.getElementValueByAttribute(fetchBookingRes,
												"HotelReservation_UniqueIDList_UniqueID", "HotelReservation_UniqueIDList_UniqueID_source","", XMLType.RESPONSE));
										actuals.put("CONFIRMATION_LEG_NO",WSClient.getElementValueByAttribute(fetchBookingRes,"HotelReservation_UniqueIDList_UniqueID","LEGNUMBER", XMLType.RESPONSE));
										actuals.put("RESV_STATUS",WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_HotelReservation_reservationStatus", XMLType.RESPONSE));
										LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_09"));

										WSAssert.assertEquals(db, actuals, false);

									}
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

										String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
									}
								}

								if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

									String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

									String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

									String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
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
			// Cancel Reservation for
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation","Failed"})

	public void fetchBooking_40620() {
		try {

			String testName = "fetchBooking_40620";
			WSClient.startTest(testName, "verify that queued reservation is populated in fetch booking response","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				/**** check parameter value of QUEUE_ROOMS ****/
				WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if QUEUE_ROOMS=Y/b>");
				String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter1.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Checking if QUEUE_ROOMS=Y</b>");
					Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}
				// Prerequisite 1 - create profile

				if (!Parameter.equals("error") && !Parameter1.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_05");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
							String queueResvReq = WSClient.createSOAPMessage("AddReservationToQueue", "DS_01");
							String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

							//****Add to Queue ***//
							if (WSAssert.assertIfElementExists(queueResvRes, "AddReservationToQueueRS_Success", true))
							{
								if(WSAssert.assertIfElementExists(queueResvRes, "AddReservationToQueueRS_QueueInfo_Priority", true))
								{
									String priority=WSClient.getElementValue(queueResvRes, "AddReservationToQueueRS_QueueInfo_Priority", XMLType.RESPONSE);

									WSClient.setData("{var_qPriority}", priority);

									String query=WSClient.getQuery("QS_01");
									HashMap<String, String> dbResults=WSClient.getDBRow(query);

									if(dbResults.get("RESV_NAME_ID") == null)
									{
										WSClient.writeToReport(LogStatus.WARNING, "<b> Blocked : The Reservation is not pushed into queue.Not inserted into DB</b>");
									}
									else
									{
										WSClient.writeToReport(LogStatus.INFO, "<b>Successfully put the reservation in queue.</b>");
										//******OWS Fetch Booking*******//

										OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
										String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
										String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);



										if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
										{
											if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
											{

												String queueExists=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_HotelReservation_queueExists", XMLType.RESPONSE);

												if(queueExists.equals("true"))
												{

													WSClient.writeToReport(LogStatus.PASS, "queueExists : Expected -> true  Actual -> "+queueExists);

												}
												else
												{
													WSClient.writeToReport(LogStatus.FAIL, "queueExists : Expected -> true  Actual -> "+queueExists);

												}
												if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_HotelReservation_queueNumber", true))
												{
													String queueNo=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_HotelReservation_queueNumber", XMLType.RESPONSE);
													String query1=WSClient.getQuery("QS_10");
													String expectedQueueNo=WSClient.getDBRow(query1).get("QUEUE_PRIORITY");
													WSAssert.assertEquals(expectedQueueNo, queueNo, false);

												}

											}
										}
										if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
											if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

												String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
											}
										}

										if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

											String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
													XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO,
													"<b>The text displayed in the response is :" + message+"</b>");
										}

										if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

											String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
										}

										if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

											String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
										}

									}
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable add to queue");
								}
							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to add to queue");
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
			// Cancel Reservation for
			try {
				WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
				WSClient.setData("{var_settingValue}", "N");

				WSClient.writeToReport(LogStatus.INFO, "<b>Changing QUEUE_ROOMS to N</b>");
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation","Failed"})

	public void fetchBooking_40602() {
		try {

			String testName = "fetchBooking_40602";
			WSClient.startTest(testName, "verify that profile information is being populated correctly in fetch booking response","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}


				if (!Parameter.equals("error")) {

					// Prerequisite 1 - create profile
					WSClient.setData("{var_gender}","F");
					WSClient.setData("{var_nationality}",OperaPropConfig.getDataSetForCode("Nationality", "DS_02"));
					WSClient.setData("{var_nameTitle}",OperaPropConfig.getDataSetForCode("Title", "DS_01"));
					WSClient.setData("{var_vipCode}",OperaPropConfig.getDataSetForCode("VipLevel", "DS_01"));
					WSClient.setData("{var_birthDate}","1995-11-09");

					profileID = CreateProfile.createProfile("DS_29");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
							//******OWS Fetch Booking*******//

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);



							if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
							{
								if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
								{
									// DB Validation
									LinkedHashMap<String,String> actuals= new LinkedHashMap<String,String>();
									actuals.put("FIRST", WSClient.getElementValue(fetchBookingRes, "Customer_PersonName_firstName", XMLType.RESPONSE));
									actuals.put("MIDDLE",WSClient.getElementValue(fetchBookingRes,"Customer_PersonName_middleName", XMLType.RESPONSE));
									actuals.put("LAST",WSClient.getElementValue(fetchBookingRes,"Customer_PersonName_lastName", XMLType.RESPONSE));
									actuals.put("TITLE",WSClient.getElementValue(fetchBookingRes,"Customer_PersonName_nameTitle",XMLType.RESPONSE));
									actuals.put("VIP_STATUS",WSClient.getElementValue(fetchBookingRes,"ResGuest_Profiles_Profile_vipCode", XMLType.RESPONSE));
									actuals.put("NATIONALITY",WSClient.getElementValue(fetchBookingRes,"ResGuest_Profiles_Profile_nationality", XMLType.RESPONSE));

									if(WSClient.getElementValue(fetchBookingRes,"Profiles_Profile_Customer_gender", XMLType.RESPONSE).equals("MALE"))
									{
										actuals.put("GENDER","M");
									}
									else if(WSClient.getElementValue(fetchBookingRes,"Profiles_Profile_Customer_gender", XMLType.RESPONSE).equals("FEMALE"))
									{
										actuals.put("GENDER","F");
									}
									else if(WSClient.getElementValue(fetchBookingRes,"Profiles_Profile_Customer_gender", XMLType.RESPONSE).equals("UNKNOWN"))
									{
										actuals.put("GENDER","U");
									}
									else
									{
										actuals.put("GENDER",WSClient.getElementValue(fetchBookingRes,"Profiles_Profile_Customer_gender", XMLType.RESPONSE));
									}


									actuals.put("LANGUAGE",WSClient.getElementValue(fetchBookingRes,"ResGuest_Profiles_Profile_languageCode", XMLType.RESPONSE));

									LinkedHashMap<String,String> expected=WSClient.getDBRow(WSClient.getQuery("QS_11"));

									WSAssert.assertEquals(expected, actuals, false);
								}
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
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
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})

	public void fetchBooking_40601() {
		try {

			String testName = "fetchBooking_40601";
			WSClient.startTest(testName, "verify that address information is being populated correctly in fetch booking response","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					WSClient.setData("{var_addressType}", OperaPropConfig.getDataSetForCode("AddressType","DS_01"));
					HashMap<String, String> address = new HashMap<String, String>();
					address=OPERALib.fetchAddressLOV();
					WSClient.setData("{var_city}", address.get("City"));
					WSClient.setData("{var_state}", address.get("State"));
					WSClient.setData("{var_country}", address.get("Country"));
					WSClient.setData("{var_zip}", address.get("Zip"));

					profileID = CreateProfile.createProfile("DS_12");

					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
							//******OWS Fetch Booking*******//

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);

							if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
							{
								if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
								{
									// DB Validation
									LinkedHashMap<String,String> actuals= new LinkedHashMap<String,String>();

									actuals.put("ADDRESS_ID",WSClient.getElementValue(fetchBookingRes,"Profile_Addresses_NameAddress_operaId",XMLType.RESPONSE));
									actuals.put("ADDRESS_TYPE",WSClient.getElementValue(fetchBookingRes,"Profile_Addresses_NameAddress_addressType", XMLType.RESPONSE));
									actuals.put("ADDRESS1", WSClient.getElementValue(fetchBookingRes, "Addresses_NameAddress_AddressLine", XMLType.RESPONSE));
									actuals.put("CITY",WSClient.getElementValue(fetchBookingRes,"Addresses_NameAddress_cityName", XMLType.RESPONSE));
									actuals.put("STATE",WSClient.getElementValue(fetchBookingRes,"Addresses_NameAddress_stateProv", XMLType.RESPONSE));
									actuals.put("COUNTRY",WSClient.getElementValue(fetchBookingRes,"Addresses_NameAddress_countryCode", XMLType.RESPONSE));
									actuals.put("ZIP_CODE",WSClient.getElementValue(fetchBookingRes,"Addresses_NameAddress_postalCode", XMLType.RESPONSE));
									actuals.put("LANGUAGE_CODE",WSClient.getElementValue(fetchBookingRes,"Profile_Addresses_NameAddress_languageCode", XMLType.RESPONSE));

									LinkedHashMap<String,String> expected=WSClient.getDBRow(WSClient.getQuery("QS_12"));

									WSAssert.assertEquals(expected, actuals, false);

								}
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
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
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}



	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})

	public void fetchBooking_10879() {
		try {

			String testName = "fetchBooking_10879";
			WSClient.startTest(testName, "verify that membership information is coming correctly in the response.","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {


					WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType","DS_01"));


					profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						// Prerequisite : create membership
						WSClient.setData("{var_fname}", WSClient.getDBRow(WSClient.getQuery("CreateMembership","QS_01")).get("FIRST"));
						WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_03"));
						WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_03"));
						WSClient.setData("{var_memNo}", WSClient.getKeywordData("{KEYWORD_RANDNUM_6}"));


						String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_02");
						String createMembershipResponseXML = WSClient.processSOAPMessage(createMembershipReq);

						if (WSAssert.assertIfElementExists(createMembershipResponseXML, "CreateMembershipRS_Success", true)) {

							if (WSAssert.assertIfElementExists(createMembershipResponseXML, "CreateMembershipRS_ProfileMemberships_ProfileMembership_MembershipID", true)){
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created membership.</b>");
								WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
								WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
								WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
								WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
								WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

								// Prerequisite 2 Create Reservation
								HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
								String resvID = resv.get("reservationId");

								if (!resvID.equals("error")) {

									WSClient.setData("{var_resvId}", resvID);
									WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
									//******OWS Fetch Booking*******//

									OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
									String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
									String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);

									if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
									{
										if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
										{
											// DB Validation
											LinkedHashMap<String,String> actuals= new LinkedHashMap<String,String>();
											actuals.put("MEMBERSHIP_TYPE", WSClient.getElementValue(fetchBookingRes, "Memberships_NameMembership_membershipType",XMLType.RESPONSE));
											actuals.put("MEMBERSHIP_LEVEL", WSClient.getElementValue(fetchBookingRes, "Memberships_NameMembership_membershipLevel",XMLType.RESPONSE));
											actuals.put("EXPIRATION_DATE", WSClient.getElementValue(fetchBookingRes, "Memberships_NameMembership_expirationDate",XMLType.RESPONSE));
											actuals.put("MEMBERSHIP_ID", WSClient.getElementValue(fetchBookingRes, "Memberships_NameMembership_membershipid",XMLType.RESPONSE));
											actuals.put("MEMBERSHIP_CARD_NO", WSClient.getElementValue(fetchBookingRes, "Memberships_NameMembership_membershipNumber",XMLType.RESPONSE));
											actuals.put("MEMBER_NAME", WSClient.getElementValue(fetchBookingRes, "Memberships_NameMembership_memberName",XMLType.RESPONSE));
											//actuals.put("POINTS", WSClient.getElementValue(fetchBookingRes, "Memberships_NameMembership_currentPoints",XMLType.RESPONSE));
											LinkedHashMap<String,String> expected= new LinkedHashMap<String,String>();

											expected=WSClient.getDBRow(WSClient.getQuery("QS_13"));
											WSAssert.assertEquals(expected, actuals, false);

										}
									}

									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
										if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

											String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
										}
									}

									if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

										String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
												XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The text displayed in the response is :" + message+"</b>");
									}

									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

										String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
									}

									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

										String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
									}

								}
							}

							else
							{
								WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to change profile");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to change profile");
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
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})

	public void fetchBooking_40640() {
		try {

			String testName = "fetchBooking_40640";
			WSClient.startTest(testName, "Verify that guest count and time span of stay are coming correctly in response","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/*****check fetch_reservation parameter will be ALL*****/

				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {
					profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_17");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");

							//******OWS Fetch Booking*******//

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);


							if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
							{
								if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
								{
									// DB Validation for guest count
									LinkedHashMap<String,String> actuals= new LinkedHashMap<String,String>();
									actuals.put("ADULTS",WSClient.getAttributeValueByAttribute(fetchBookingRes,"RoomStay_GuestCounts_GuestCount","count", "ADULT", XMLType.RESPONSE));
									actuals.put("CHILDREN",WSClient.getAttributeValueByAttribute(fetchBookingRes,"RoomStay_GuestCounts_GuestCount","count", "CHILD", XMLType.RESPONSE));
									LinkedHashMap<String,String> expected= new LinkedHashMap<String,String>();
									expected=WSClient.getDBRow(WSClient.getQuery("QS_14"));
									WSAssert.assertEquals(expected, actuals, false);
									LinkedHashMap<String,String> actuals1= new LinkedHashMap<String,String>();
									actuals1.put("BEGIN_DATE",WSClient.getElementValue(fetchBookingRes,"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE).replace("T"," "));
									actuals1.put("END_DATE",WSClient.getElementValue(fetchBookingRes,"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE).replace("T"," "));
									LinkedHashMap<String,String> expected1= new LinkedHashMap<String,String>();
									expected1=WSClient.getDBRow(WSClient.getQuery("QS_15"));
									WSAssert.assertEquals(expected1, actuals1, false);



								}
							}


							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
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
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation","Failed"})

	public void fetchBooking_40643() {
		try {

			String testName = "fetchBooking_40643";
			WSClient.startTest(testName, "verify that correct information of accompany guest in fetch booking response","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");

				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}


				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {


					String profileID1 = CreateProfile.createProfile("DS_01");
					if (!profileID1.equals("error")) {
						String profileID2=CreateProfile.createProfile("DS_01");

						if (!profileID2.equals("error")) {

							WSClient.setData("{var_profileId2}", profileID2);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID 1 : "+profileID2+"</b>");
							WSClient.setData("{var_profileId1}",profileID1);
							WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID 2: "+profileID1+"</b>");

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

								String query = WSClient.getQuery("OWSFetchBooking","QS_17");
								LinkedHashMap<String,String> temp = new LinkedHashMap<String,String>();
								temp=WSClient.getDBRow(query);

								WSClient.setData("{var_firstName}", temp.get("FIRST"));
								WSClient.setData("{var_lastName}", temp.get("LAST"));


								// Pre-requisite : Change Reservation to add accompany guest

								String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_01");
								String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

								//******OWS Fetch Booking*******//

								if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added accompany guest.</b>");
									OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
									String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
									String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);


									if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
									{
										if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
										{
											// DB Validation
											LinkedHashMap<String,String> expected = WSClient.getDBRow(WSClient.getQuery("QS_18"));
											LinkedHashMap<String,String> actuals=new LinkedHashMap<String,String>();
											actuals.put("NAME_ID", WSClient.getElementValue(fetchBookingRes, "AccompanyGuests_AccompanyGuest_NameID", XMLType.RESPONSE));
											actuals.put("FIRST", WSClient.getElementValue(fetchBookingRes, "AccompanyGuests_AccompanyGuest_FirstName", XMLType.RESPONSE));
											actuals.put("LAST", WSClient.getElementValue(fetchBookingRes, "AccompanyGuests_AccompanyGuest_LastName", XMLType.RESPONSE));
											WSAssert.assertEquals(expected, actuals, false);
										}
									}

									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
										if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

											String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
										}
									}

									if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

										String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
												XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The text displayed in the response is :" + message+"</b>");
									}

									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

										String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
									}

									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

										String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
									}
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to add accompany guest");
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
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation","Failed"})

	public void fetchBooking_10878() {
		try {

			String testName = "fetchBooking_10878";
			WSClient.startTest(testName, "Verify that preference information is retrieved correctly in fetch booking response","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					String operaProfileID = CreateProfile.createProfile("DS_01");
					if(!operaProfileID.equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+operaProfileID+"</b>");
						WSClient.setData("{var_profileId}", operaProfileID);
						WSClient.setData("{var_profileID}", operaProfileID);


						String prefValue = OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01");

						String prefType = OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01");


						WSClient.setData("{var_prefType}", prefType);
						WSClient.setData("{var_prefValue}", prefValue);
						String query1 = WSClient.getQuery("CreatePreference", "QS_02");
						LinkedHashMap<String, String> prefGlobal = WSClient.getDBRow(query1);
						if (prefGlobal.size() == 0) {
							WSClient.setData("{var_global}", "true");
						} else {
							WSClient.setData("{var_global}", "false");
						}
						WSClient.setData("{var_prefDesc}", prefType + " " + prefValue);

						// adding preference to profile :

						String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_01");
						String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);

						if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success",true)) {
							String query2 = WSClient.getQuery("QS_01");
							LinkedHashMap<String, String> preference = WSClient.getDBRow(query2);
							if (preference.size() >= 1) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added the preference.</b>");
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

									OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
									String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
									String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);

									if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
									{
										if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
										{
											// DB Validation
											LinkedHashMap<String,String> expected = WSClient.getDBRow(WSClient.getQuery("QS_19"));
											LinkedHashMap<String,String> actuals=new LinkedHashMap<String,String>();
											actuals.put("PREFERENCE_TYPE", WSClient.getElementValue(fetchBookingRes, "HotelReservation_Preferences_Preference_preferenceType", XMLType.RESPONSE));
											actuals.put("PREFERENCE_VALUE", WSClient.getElementValue(fetchBookingRes, "HotelReservation_Preferences_Preference_preferenceValue", XMLType.RESPONSE));
											WSAssert.assertEquals(expected, actuals, false);

										}
									}

									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
										if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

											String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
										}
									}

									if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

										String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
												XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The text displayed in the response is :" + message+"</b>");
									}

									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

										String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
									}

									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

										String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
									}

								}
							}

							else
							{
								WSClient.writeToReport(LogStatus.ERROR, "Blocked : Unable to create preferences" );
							}

						}
						else
						{
							WSClient.writeToReport(LogStatus.ERROR, "Blocked : Unable to create preferences" );
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
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})

	public void fetchBooking_10875() {
		try {

			String testName = "fetchBooking_10875";
			WSClient.startTest(testName, "Verify that payment information is retrieved correctly in fetch booking response","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}


				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
							// ******OWS Fetch Booking*******//

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchBookingReq = WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);


							if (WSAssert.assertIfElementExists(fetchBookingRes,
									"FetchBookingResponse_Result_resultStatusFlag", false)) {
								if (WSAssert.assertIfElementValueEquals(fetchBookingRes,
										"FetchBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									// DB Validation

									LinkedHashMap<String, String> actuals = new LinkedHashMap<String, String>();
									LinkedHashMap<String, String> actuals1 = new LinkedHashMap<String, String>();
									LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_20"));
									WSClient.writeToReport(LogStatus.INFO,"<b>Validation 1 for PaymentsAccepted Element </b>");
									actuals.put("PAYMENT_METHOD", WSClient.getElementValue(fetchBookingRes, "PaymentsAccepted_PaymentType_OtherPayment_type", XMLType.RESPONSE));
									WSAssert.assertEquals(db, actuals, false);

									WSClient.writeToReport(LogStatus.INFO,"<b>Validation 2 for ReservationPayments Element</b>");
									actuals1.put("PAYMENT_METHOD", WSClient.getElementValue(fetchBookingRes, "HotelReservation_ReservationPayments_ReservationPaymentInfo_PaymentType", XMLType.RESPONSE));
									WSAssert.assertEquals(db, actuals1, false);


								}
							}
							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
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
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	/*@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})
	@Parameters({"resortEntryTwo"})
	public void fetchBooking_41040(){
		try {

			String testName = "fetchBooking_41040";
			WSClient.startTest(testName, "verify that the requested booking created through another WEB Channel is not getting fetched when FETCH_RESERVATION=CHANNEL","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "ReservationType"})) {

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
				WSClient.setData("{var_owsResort}", resort);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

				String channel1 = OWSLib.getChannel(2);
				String channelType1 = OWSLib.getChannelType(channel1);
				String resort1 = OWSLib.getChannelResort(resortOperaValue, channel1);
				String channelCarrier1 = OWSLib.getChannelCarier(resortOperaValue, channel1);

				OPERALib.setOperaHeader(uname);

	 *//*****check fetch_reservation parameter will be CHANNEL*****//*
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "CHANNEL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=CHANNEL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("CHANNEL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to CHANNEL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");


						//  Prerequisite 2 Create Booking

						WSClient.setData("{var_profileSource}",channel);
						WSClient.setData("{var_rate}",OperaPropConfig.getChannelCodeForDataSet("RateCode","DS_01"));
						WSClient.setData("{var_resvType}",OperaPropConfig.getChannelCodeForDataSet("ReservationType","DS_01"));
						WSClient.setData("{var_roomType}",OperaPropConfig.getChannelCodeForDataSet("RoomType","DS_01"));
						WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_15}")+"T06:00:00");
						WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_17}")+"T06:00:00");
						WSClient.setData("{var_time}", "09:00:00");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String createBookingRes = WSClient.processSOAPMessage(createBookingReq);

						if(WSAssert.assertIfElementValueEquals(createBookingRes,"CreateBookingResponse_Result_resultStatusFlag","SUCCESS", true)){

							String reservationId1=	 WSClient.getElementValueByAttribute(createBookingRes,"HotelReservation_UniqueIDList_UniqueID","RESVID", XMLType.RESPONSE);
							WSClient.setData("{var_resvId}", reservationId1);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+reservationId1+"</b>");


							OWSLib.setOWSHeader(uname, pwd,resortOperaValue, channelType1, channelCarrier1);
							String fetchBookingReq1= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes1 = WSClient.processSOAPMessage(fetchBookingReq1);


							if(WSAssert.assertIfElementExists(fetchBookingRes1, "FetchBookingResponse_Result_resultStatusFlag",false))
							{
								WSAssert.assertIfElementValueEquals(fetchBookingRes1, "FetchBookingResponse_Result_resultStatusFlag","FAIL", false);

							}

							if(WSAssert.assertIfElementExists(fetchBookingRes1,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes1,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes1,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes1, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes1, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes1,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes1, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes1,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes1, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
							}



						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to create booking");
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

			// Cancel Reservation
			try
			{  WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
			WSClient.setData("{var_settingValue}", "ALL");
			ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/

	/*@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})

	public void fetchBooking_41039()  {
		try {

			String testName = "fetchBooking_41039";
			WSClient.startTest(testName, "verify that the requested booking created through another WEB Channel is getting fetched when FETCH_RESERVATION=CHANNELTYPE","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "ReservationType" })) {

				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				WSClient.setData("{var_channel}", channel);
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				WSClient.setData("{var_owsResort}", resort);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);

				String channel1 = OWSLib.getChannel(2);
				String channelType1 = OWSLib.getChannelType(channel1);
				String resort1 = OWSLib.getChannelResort(resortOperaValue, channel1);
				String channelCarrier1 = OWSLib.getChannelCarier(resortOperaValue, channel1);


				OPERALib.setOperaHeader(uname);

	 *//*****check fetch_reservation parameter will be CHANNELTYPE*****//*
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "CHANNELTYPE");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=CHANNELTYPE</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("CHANNELTYPE")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to CHANNELTYPE</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");


						//  Prerequisite 2 Create Booking

						WSClient.setData("{var_profileSource}",channel);
						WSClient.setData("{var_rate}",OperaPropConfig.getChannelCodeForDataSet("RateCode","DS_01"));
						WSClient.setData("{var_resvType}",OperaPropConfig.getChannelCodeForDataSet("ReservationType","DS_01"));
						WSClient.setData("{var_roomType}",OperaPropConfig.getChannelCodeForDataSet("RoomType","DS_01"));
						WSClient.setData("{var_busdate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_15}")+"T06:00:00");
						WSClient.setData("{var_busdate1}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_17}")+"T06:00:00");
						WSClient.setData("{var_time}", "09:00:00");
						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
						String createBookingReq = WSClient.createSOAPMessage("OWSCreateBooking", "DS_01");
						String createBookingRes = WSClient.processSOAPMessage(createBookingReq);

						if(WSAssert.assertIfElementValueEquals(createBookingRes,"CreateBookingResponse_Result_resultStatusFlag","SUCCESS", true)){

							String reservationId1=	 WSClient.getElementValueByAttribute(createBookingRes,"HotelReservation_UniqueIDList_UniqueID","RESVID", XMLType.RESPONSE);
							WSClient.setData("{var_resvId}", reservationId1);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+reservationId1+"</b>");

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType1, channelCarrier1);
							String fetchBookingReq1= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes1 = WSClient.processSOAPMessage(fetchBookingReq1);



							if(WSAssert.assertIfElementExists(fetchBookingRes1, "FetchBookingResponse_Result_resultStatusFlag",false))
							{
								if(WSAssert.assertIfElementValueEquals(fetchBookingRes1, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
								{
									// DB Validation

									LinkedHashMap<String,String> actuals= new LinkedHashMap<String,String>();
									actuals.put("RESORT", WSClient.getElementValue(fetchBookingRes1, "RoomStays_RoomStay_HotelReference_hotelCode", XMLType.RESPONSE));
									actuals.put("NAME_ID",WSClient.getElementValue(fetchBookingRes1,"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));
									actuals.put("RESV_NAME_ID",WSClient.getElementValueByAttribute(fetchBookingRes1,"HotelReservation_UniqueIDList_UniqueID","RESVID", XMLType.RESPONSE));
									actuals.put("CONFIRMATION_NO", WSClient.getElementValueByAttribute(fetchBookingRes1,
											"HotelReservation_UniqueIDList_UniqueID", "HotelReservation_UniqueIDList_UniqueID_source","", XMLType.RESPONSE));
									actuals.put("RESV_STATUS",WSClient.getElementValue(fetchBookingRes1,"FetchBookingResponse_HotelReservation_reservationStatus", XMLType.RESPONSE));
									LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_01"));

									WSAssert.assertEquals(db, actuals, false);

								}
							}
							if(WSAssert.assertIfElementExists(fetchBookingRes1,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes1,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes1,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes1, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes1, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes1,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes1, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes1,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes1, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
							}


						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to create booking");
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

			// Cancel Reservation
			try
			{   WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
			WSClient.setData("{var_settingValue}", "ALL");
			WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
			ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS", "Reservation","Failed" })

	public void fetchBooking_41325() {
		try {

			String testName = "fetchBooking_41325";
			WSClient.startTest(testName,
					"verify that reservation comment are being displayed in the fetch booking response correctly.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/***** check fetch_reservation parameter will be ALL *****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_19");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
							// ******OWS Fetch Booking*******//

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchBookingReq = WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);



							if (WSAssert.assertIfElementExists(fetchBookingRes,
									"FetchBookingResponse_Result_resultStatusFlag", false)) {
								if (WSAssert.assertIfElementValueEquals(fetchBookingRes,
										"FetchBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									// DB Validation

									LinkedHashMap<String, String> actuals = new LinkedHashMap<String, String>();
									actuals.put("COMMENTS", WSClient.getElementValue(fetchBookingRes,"Comments_Comment_Text", XMLType.RESPONSE));

									LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_21"));

									WSAssert.assertEquals(db, actuals, false);

								}
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
							}


						}
					}

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Test(groups = { "minimumRegression", "FetchBooking", "OWS", "Reservation" })

	public void fetchBooking_41326() {
		try {

			String testName = "fetchBooking_41326";
			WSClient.startTest(testName,
					"verify that guarantee code are being displayed in the fetch booking response correctly.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				WSClient.setData("{var_channel}", channel);
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/***** check fetch_reservation parameter will be ALL *****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");

						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
						WSClient.setData("{var_guaranteeCode}", OperaPropConfig.getDataSetForCode("ReservationType", "DS_01"));
						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_20");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
							// ******OWS Fetch Booking*******//

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchBookingReq = WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);


							if (WSAssert.assertIfElementExists(fetchBookingRes,
									"FetchBookingResponse_Result_resultStatusFlag", false)) {
								if (WSAssert.assertIfElementValueEquals(fetchBookingRes,
										"FetchBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									// DB Validation

									LinkedHashMap<String, String> actuals = new LinkedHashMap<String, String>();
									actuals.put("GUARANTEE_CODE", WSClient.getElementValue(fetchBookingRes,"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.RESPONSE));

									LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_22"));

									WSAssert.assertEquals(db, actuals, false);

								}
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
							}


						}
					}

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})

	public void fetchBooking_41327() {
		try {

			String testName = "fetchBooking_41327";
			WSClient.startTest(testName, "verify that special requests are being fetched correctly in fetch booking response","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod"})) {

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

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}


				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {


					profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {

						WSClient.setData("{var_profileId1}",profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");

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
							WSClient.setData("{var_prefType}","SPECIALS");
							WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_04"));

							// Pre-requisite : Change Reservation to add accompany guest

							String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_05");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							//******OWS Fetch Booking*******//

							if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added the specials to the reservation.</b>");
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
								String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);



								if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
								{
									if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
									{
										// DB Validation

										LinkedHashMap<String, String> actuals = new LinkedHashMap<String, String>();
										actuals.put("SPECIAL_REQUEST_CODE", WSClient.getElementValue(fetchBookingRes,"RoomStay_SpecialRequests_SpecialRequest_requestCode", XMLType.RESPONSE));
										LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_23"));

										WSAssert.assertEquals(db, actuals, false);

									}
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

										String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
									}
								}

								if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

									String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

									String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

									String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
								}

							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to add specials");
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
			// Cancel Reservation
			try
			{
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS", "Reservation" })

	public void fetchBooking_41328() {
		try {

			String testName = "fetchBooking_41328";
			WSClient.startTest(testName,
					"verify that transportation details are coming correctly in fetch booking response.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod","TransportationType" })) {

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


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/***** check fetch_reservation parameter will be ALL *****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
						WSClient.setData("{var_transportCode}", "5478");
						WSClient.setData("{var_transportType}",OperaPropConfig.getDataSetForCode("TransportationType", "DS_01"));

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_15");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
							// ******OWS Fetch Booking*******//

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchBookingReq = WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);


							if (WSAssert.assertIfElementExists(fetchBookingRes,
									"FetchBookingResponse_Result_resultStatusFlag", false)) {
								if (WSAssert.assertIfElementValueEquals(fetchBookingRes,
										"FetchBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									// DB Validation

									LinkedHashMap<String, String> actuals = new LinkedHashMap<String, String>();
									actuals.put("ARRIVAL_DATE_TIME",WSClient.getElementValue(fetchBookingRes, "ResGuests_ResGuest_ArrivalTransport_time", XMLType.RESPONSE).replace("T"," "));
									actuals.put("ARRIVAL_CARRIER_CODE",WSClient.getElementValue(fetchBookingRes, "ResGuests_ResGuest_ArrivalTransport_carrierCode", XMLType.RESPONSE));
									actuals.put("ARRIVAL_STATION_CODE",WSClient.getElementValue(fetchBookingRes, "ResGuests_ResGuest_ArrivalTransport_locationCode", XMLType.RESPONSE));
									actuals.put("ARRIVAL_TRANSPORT_TYPE",WSClient.getElementValue(fetchBookingRes, "ResGuests_ResGuest_ArrivalTransport_type", XMLType.RESPONSE));
									actuals.put("ARRIVAL_TRANSPORT_CODE",WSClient.getElementValue(fetchBookingRes, "ResGuests_ResGuest_ArrivalTransport_id", XMLType.RESPONSE));
									actuals.put("DEPARTURE_DATE_TIME",WSClient.getElementValue(fetchBookingRes, "ResGuests_ResGuest_DepartureTransport_time", XMLType.RESPONSE).replace("T"," "));
									actuals.put("DEPARTURE_CARRIER_CODE",WSClient.getElementValue(fetchBookingRes, "ResGuests_ResGuest_DepartureTransport_carrierCode", XMLType.RESPONSE));
									actuals.put("DEPARTURE_STATION_CODE",WSClient.getElementValue(fetchBookingRes, "ResGuests_ResGuest_DepartureTransport_locationCode", XMLType.RESPONSE));
									actuals.put("DEPARTURE_TRANSPORT_TYPE",WSClient.getElementValue(fetchBookingRes, "ResGuests_ResGuest_DepartureTransport_type", XMLType.RESPONSE));
									actuals.put("DEPARTURE_TRANSPORT_CODE",WSClient.getElementValue(fetchBookingRes, "ResGuests_ResGuest_DepartureTransport_id", XMLType.RESPONSE));
									LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_24"));

									WSAssert.assertEquals(db, actuals, false);

								}
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
							}



						}
					}

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})

	public void fetchBooking_10876() {
		try {

			String testName = "fetchBooking_10876";
			WSClient.startTest(testName, "verify thar correct email information is fetched in fetch booking response","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}


				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					WSClient.setData("{var_email}","test@gmail.com");
					WSClient.setData("{var_emailType}", OperaPropConfig.getDataSetForCode("CommunicationType","DS_04"));
//					WSClient.setData("{var_emailMethod}", OperaPropConfig.getDataSetForCode("CommunicationMethod","DS_02"));

					profileID = CreateProfile.createProfile("DS_55");

					if (!profileID.equals("error")) {

						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation

						HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");

							//******OWS Fetch Booking*******//

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);



							if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
							{
								if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
								{
									// DB Validation
									WSClient.writeToReport(LogStatus.INFO,"<b>Validation 1</b>");
									LinkedHashMap<String,String> actuals= new LinkedHashMap<String,String>();
									actuals.put("PHONE_ID",WSClient.getElementValue(fetchBookingRes, "Profile_Phones_NamePhone_operaId", XMLType.RESPONSE));
									actuals.put("PHONE_ROLE",WSClient.getElementValue(fetchBookingRes, "Profile_Phones_NamePhone_phoneRole", XMLType.RESPONSE));
									actuals.put("PHONE_TYPE",WSClient.getElementValue(fetchBookingRes, "Profile_Phones_NamePhone_phoneType", XMLType.RESPONSE));
									actuals.put("PHONE_NUMBER",WSClient.getElementValue(fetchBookingRes, "Phones_NamePhone_PhoneNumber", XMLType.RESPONSE));
									if(WSClient.getElementValue(fetchBookingRes, "Profile_Phones_NamePhone_primary", XMLType.RESPONSE).equalsIgnoreCase("true"))
										actuals.put("PRIMARY_YN","Y");
									else if(WSClient.getElementValue(fetchBookingRes, "Profile_Phones_NamePhone_primary", XMLType.RESPONSE).equalsIgnoreCase("false"))
										actuals.put("PRIMARY_YN","N");
									else
										actuals.put("PRIMARY_YN",WSClient.getElementValue(fetchBookingRes, "Profile_Phones_NamePhone_primary", XMLType.RESPONSE));

									LinkedHashMap<String,String> expected= WSClient.getDBRow(WSClient.getQuery("QS_25"));
									WSAssert.assertEquals(expected, actuals, false);

									WSClient.writeToReport(LogStatus.INFO,"<b>Validation 2</b>");
									LinkedHashMap<String,String> actuals1= new LinkedHashMap<String,String>();
									actuals1.put("EMAIL_TYPE",WSClient.getElementValue(fetchBookingRes, "Profile_EMails_NameEmail_emailType", XMLType.RESPONSE));
									actuals1.put("EMAIL_ID",WSClient.getElementValue(fetchBookingRes, "Profile_EMails_NameEmail", XMLType.RESPONSE));
									if(WSClient.getElementValue(fetchBookingRes, "Profile_EMails_NameEmail_primary", XMLType.RESPONSE).equalsIgnoreCase("true"))
										actuals1.put("PRIMARY_YN","Y");
									else if(WSClient.getElementValue(fetchBookingRes, "Profile_EMails_NameEmail_primary", XMLType.RESPONSE).equalsIgnoreCase("false"))
										actuals.put("PRIMARY_YN","N");
									else
										actuals.put("PRIMARY_YN",WSClient.getElementValue(fetchBookingRes, "Profile_EMails_NameEmail_primary", XMLType.RESPONSE));

									LinkedHashMap<String,String> expected1= WSClient.getDBRow(WSClient.getQuery("QS_26"));
									WSAssert.assertEquals(expected1, actuals1, false);

								}
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
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
			// Cancel Reservation
			try

			{
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");

			}catch(Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})

	public void fetchBooking_10877() {
		try {

			String testName = "fetchBooking_10877";
			WSClient.startTest(testName, "verify thar correct phone information is fetched in fetch booking response","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					WSClient.setData("{var_phoneType}", OperaPropConfig.getDataSetForCode("CommunicationType","DS_01"));
					WSClient.setData("{var_phoneMethod}", OperaPropConfig.getDataSetForCode("CommunicationMethod","DS_01"));

					profileID = CreateProfile.createProfile("DS_54");

					if (!profileID.equals("error")) {

						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation

						HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");

							//******OWS Fetch Booking*******//

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);



							if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
							{
								if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
								{
									// DB Validation
									LinkedHashMap<String,String> actuals= new LinkedHashMap<String,String>();
									actuals.put("PHONE_ID",WSClient.getElementValue(fetchBookingRes, "Profile_Phones_NamePhone_operaId", XMLType.RESPONSE));
									actuals.put("PHONE_ROLE",WSClient.getElementValue(fetchBookingRes, "Profile_Phones_NamePhone_phoneRole", XMLType.RESPONSE));
									actuals.put("PHONE_TYPE",WSClient.getElementValue(fetchBookingRes, "Profile_Phones_NamePhone_phoneType", XMLType.RESPONSE));
									actuals.put("PHONE_NUMBER",WSClient.getElementValue(fetchBookingRes, "Phones_NamePhone_PhoneNumber", XMLType.RESPONSE));
									if(WSClient.getElementValue(fetchBookingRes, "Profile_Phones_NamePhone_primary", XMLType.RESPONSE).equalsIgnoreCase("true"))
										actuals.put("PRIMARY_YN","Y");
									else if(WSClient.getElementValue(fetchBookingRes, "Profile_Phones_NamePhone_primary", XMLType.RESPONSE).equalsIgnoreCase("false"))
										actuals.put("PRIMARY_YN","N");
									else
										actuals.put("PRIMARY_YN",WSClient.getElementValue(fetchBookingRes, "Profile_Phones_NamePhone_primary", XMLType.RESPONSE));
									LinkedHashMap<String,String> expected= WSClient.getDBRow(WSClient.getQuery("QS_25"));
									WSAssert.assertEquals(expected, actuals, false);

								}
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
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
			// Cancel Reservation
			try

			{		if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");

			}catch(Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation","Failed"})

	public void fetchBooking_41330() {
		try {

			String testName = "fetchBooking_41330";
			WSClient.startTest(testName, "verify that reservation UDFs are being fetched correctly in fetch booking response","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod"})) {

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

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}


				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {


					profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {

						WSClient.setData("{var_profileId}",profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));


						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");

							// Pre-requisite : Change Reservation to add UDF
							String charUDF = HTNGLib.getUDFLabel("C", "R");
							String numUDF = HTNGLib.getUDFLabel("N", "R");
							String dateUDF = HTNGLib.getUDFLabel("D", "R");
							String charName = HTNGLib.getUDFName("C", charUDF, "R");
							String numName = HTNGLib.getUDFName("N", numUDF, "R");
							String dateName = HTNGLib.getUDFName("D", dateUDF, "R");
							WSClient.setData("{var_charName}", charName);
							WSClient.setData("{var_numericName}", numName);
							WSClient.setData("{var_dateName}", dateName);

							String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_06");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							//******OWS Fetch Booking*******//

							if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added UDFs to reservation.</b>");
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
								String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);


								if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
								{
									if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
									{
										// DB Validation

										LinkedHashMap<String, String> actuals = new LinkedHashMap<String, String>();

										actuals.put(WSClient.getData("{var_dateName}"), WSClient.getElementValue(fetchBookingRes,"UserDefinedValues_UserDefinedValue_DateValue_2", XMLType.RESPONSE).replace("T", " "));
										actuals.put(WSClient.getData("{var_numericName}"), WSClient.getElementValue(fetchBookingRes,"UserDefinedValues_UserDefinedValue_NumericValue_2", XMLType.RESPONSE));
										actuals.put(WSClient.getData("{var_charName}"), WSClient.getElementValue(fetchBookingRes,"UserDefinedValues_UserDefinedValue_CharacterValue_2", XMLType.RESPONSE));
										LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_27"));

										WSAssert.assertEquals(db, actuals, false);

									}
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

										String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
									}
								}

								if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

									String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

									String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

									String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
								}


							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to add UDFs");
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
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})

	public void fetchBooking_41787() {
		try {

			String testName = "fetchBooking_41787";
			WSClient.startTest(testName, "verify that package is being displayed correctly on fetch booking response for multi night booking","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod"})) {

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

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}


				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {


					profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {

						WSClient.setData("{var_profileId}",profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));


						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_22");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
							WSClient.setData("{var_packageCode}",OperaPropConfig.getDataSetForCode("PackageCode", "DS_05"));

							// Pre-requisite : Change Reservation to add accompany guest

							String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_07");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							//******OWS Fetch Booking*******//

							if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added package to reservation.</b>");
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
								String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);



								if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
								{
									if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
									{

										// DB Validation
										List<LinkedHashMap<String,String>> actuals= new ArrayList<LinkedHashMap<String,String>>();
										HashMap<String,String> xpath=new HashMap<String,String>();
										xpath.put("RoomStay_Packages_Package_packageCode", "RoomStay_Packages_Package");
										actuals=WSClient.getMultipleNodeList(fetchBookingRes, xpath, true, XMLType.RESPONSE);

										List<LinkedHashMap<String,String>> db=WSClient.getDBRows(WSClient.getQuery("QS_37"));
										WSAssert.assertEquals(actuals,db, false);


									}
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

										String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
									}
								}

								if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

									String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

									String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

									String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
								}

							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to add package to reservation");
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
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
// Need to update the channelValidation="false" in the header	
//	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})

	public void fetchBooking_10880() {
		try {

			String testName = "fetchBooking_10880";
			WSClient.startTest(testName, "verify that reservation alerts are being fetched correctly in fetch booking response","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod","AlertCode"})) {

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

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}



				/**** check parameter value of  ****/
				WSClient.setData("{var_parameter}", "SEND_ALERTS_AND_STATISTICS_ON_RESV_FETCH");
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if SEND_ALERTS_AND_STATISTICS_ON_RESV_FETCH=Y</b>");
				String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter1.equals("Y")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Checking if SEND_ALERTS_AND_STATISTICS_ON_RESV_FETCH=Y</b>");
					Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}
				// Prerequisite 1 - create profile

				if (!Parameter.equals("error") && !Parameter1.equals("error")) {

					profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {

						WSClient.setData("{var_profileId}",profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));


						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
							WSClient.setData("{var_alertCode}", OperaPropConfig.getDataSetForCode("AlertCode", "DS_01"));

							// Pre-requisite : Change Reservation to add alert

							String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_03");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							//******OWS Fetch Booking*******//

							if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added the alerts to reservation.</b>");
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
								String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);




								if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
								{
									if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
									{
										// DB Validation

										LinkedHashMap<String, String> actuals = new LinkedHashMap<String, String>();

										actuals.put("RESV_ALERT_ID", WSClient.getElementValue(fetchBookingRes,"HotelReservation_Alerts_AlertId", XMLType.RESPONSE));
										actuals.put("ALERT_CODE", WSClient.getElementValue(fetchBookingRes,"HotelReservation_Alerts_Code", XMLType.RESPONSE));
										actuals.put("AREA", WSClient.getElementValue(fetchBookingRes,"HotelReservation_Alerts_Area", XMLType.RESPONSE));
										actuals.put("DESCRIPTION", WSClient.getElementValue(fetchBookingRes,"HotelReservation_Alerts_Description", XMLType.RESPONSE));
										actuals.put("SCREEN_YN", WSClient.getElementValue(fetchBookingRes,"HotelReservation_Alerts_ScreenNotification", XMLType.RESPONSE));
										actuals.put("PRINTER_YN", WSClient.getElementValue(fetchBookingRes,"HotelReservation_Alerts_PrinterNotification", XMLType.RESPONSE));

										LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_28"));

										WSAssert.assertEquals(db, actuals, false);



									}
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

										String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
									}
								}

								if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

									String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

									String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

									String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
								}
							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to add alerts");
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
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

//As per the code, Traces will only be fetched if the channel used is the O2G and not any other channel.(https://jira.oraclecorp.com/jira/browse/HOWSS-5740)	
//	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation","Failed"})

	public void fetchBooking_41331() {
		try {

			String testName = "fetchBooking_41331";
			WSClient.startTest(testName, "verify that  traces are being fetched correctly in fetch booking response","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod","DepartmentCode"})) {

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

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {


					profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {

						WSClient.setData("{var_profileId}",profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
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
							WSClient.setData("{var_departmentId}", OperaPropConfig.getDataSetForCode("DepartmentCode", "DS_01"));

							// Pre-requisite : Change Reservation to add trace

							String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_04");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							//******OWS Fetch Booking*******//

							if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added traces to reservation.</b>");
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
								String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);


								if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
								{
									if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
									{
										// DB Validation

										LinkedHashMap<String, String> actuals = new LinkedHashMap<String, String>();

										actuals.put("RESV_TRACE_ID", WSClient.getElementValue(fetchBookingRes,"Traces_Trace_TraceId", XMLType.RESPONSE));
										actuals.put("TRACE_TEXT", WSClient.getElementValue(fetchBookingRes,"Traces_Trace_Text", XMLType.RESPONSE));
										actuals.put("DEPT_ID", WSClient.getElementValue(fetchBookingRes,"RoomStay_Traces_Trace_department", XMLType.RESPONSE));
										actuals.put("TRACE_DATE", WSClient.getElementValue(fetchBookingRes,"Traces_Trace_TraceDate", XMLType.RESPONSE).replace("T"," "));
										actuals.put("TRACE_TIME", WSClient.getElementValue(fetchBookingRes,"Traces_Trace_TraceTime", XMLType.RESPONSE));
										actuals.put("STATUS_FLAG", WSClient.getElementValue(fetchBookingRes,"RoomStay_Traces_Trace_resolved", XMLType.RESPONSE));
										if(WSClient.getElementValue(fetchBookingRes,"RoomStay_Traces_Trace_resolved", XMLType.RESPONSE).equals("Y")){
											actuals.put("RESOLVED_ON", WSClient.getElementValue(fetchBookingRes,"Traces_Trace_ResolvedOn", XMLType.RESPONSE));
											actuals.put("RESOLVED_BY", WSClient.getElementValue(fetchBookingRes,"Traces_Trace_ResolvedBy", XMLType.RESPONSE));
										}

										LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_29"));

										WSAssert.assertEquals(db, actuals, false);



									}
								}


								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

										String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
									}
								}

								if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

									String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

									String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

									String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
								}

							}

							else
							{
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to add traces");
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
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


// Valid Scenario - Need to fix the channelValidation at header level	
//	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation","Failed"})

	public void fetchBooking_41332() {
		try {

			String testName = "fetchBooking_41332";
			WSClient.startTest(testName, "verify that pay routing details are being fetched correctly in fetch booking response","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod"})) {

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

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					profileID = CreateProfile.createProfile("DS_01");

					if (!profileID.equals("error")) {

						WSClient.setData("{var_profileId}",profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));


						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_13");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
							WSClient.setData("{var_resvId}", resvID);

							/************
							 * Prerequisite 3: Create profile for Payee
							 *****************/

							String payeeProfileID = CreateProfile.createProfile("DS_01");

							if (!payeeProfileID.equals("error")) {
								WSClient.writeToReport(LogStatus.INFO, "<b>Payee Profile ID : "+payeeProfileID+"</b>");
								WSClient.setData("{var_payeeProfileId}", payeeProfileID);
								WSClient.setData("{var_resvId}", resvID);
								WSClient.setData("{var_profileId}", profileID);
								WSClient.setData("{var_trxCode}",
										OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
								WSClient.setData("{var_trxGroup}",
										OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
								WSClient.setData("{var_trxSubGroup}",
										OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));

								/*** Prerequisite 4: Create Routing Instructions **/

								String payroutReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions",	"DS_01");
								String payroutRes = WSClient.processSOAPMessage(payroutReq);

								if (WSAssert.assertIfElementExists(payroutRes, "CreateRoutingInstructionsRS_Success",
										true)) {
									WSClient.writeToReport(LogStatus.INFO, "<b>Successfully created routings.</b>");
									OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
									String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
									String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);



									if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
									{
										if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
										{
											// DB Validation

											LinkedHashMap<String, String> actuals = new LinkedHashMap<String, String>();
											LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_30"));
											actuals.put("ROUTING_INSTRUCTIONS_ID", WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_HotelReservation_PayRoutings_RoutingInstruction", XMLType.RESPONSE));
											actuals.put("OWNER", WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_HotelReservation_PayRoutings_Owner", XMLType.RESPONSE));
											actuals.put("FOLIO_VIEW", WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_HotelReservation_PayRoutings_Window", XMLType.RESPONSE));

											WSAssert.assertEquals(db, actuals, false);



										}
									}
									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
										if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

											String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
										}
									}

									if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

										String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
												XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"<b>The text displayed in the response is :" + message+"</b>");
									}

									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

										String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
									}

									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

										String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
									}



								}

								else
								{
									WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to add pay routing");
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
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	//@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})
	//
	//public void fetchBooking_3() {
	//	try {
	//
	//		String testName = "fetchBooking_23562";
	//		WSClient.startTest(testName, "verify that expected charges are being displayed correctly on fetch booking response  when package with posting ryhtm as every night is added to reservation.","minimumRegression");
	//		if (OperaPropConfig.getPropertyConfigResults(
	//				new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod"})) {
	//
	//			String resortOperaValue = OPERALib.getResort();
	//			String chain = OPERALib.getChain();
	//			WSClient.setData("{var_chain}", chain);
	//			WSClient.setData("{var_resort}", resortOperaValue);
	//
	//			String uname = OPERALib.getUserName();
	//			String pwd = OPERALib.getPassword();
	//			String channel = OWSLib.getChannel();
	//			String channelType = OWSLib.getChannelType(channel);
	//			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	//			WSClient.setData("{var_owsresort}", resort);
	//			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
	//
	//			OPERALib.setOperaHeader(uname);
	//
	//			/*****check fetch_reservation parameter will be ALL*****/
	//			WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
	//			WSClient.setData("{var_settingValue}", "ALL");
	//			WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
	//			String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
	//			if (!Parameter.equals("ALL")) {
	//				WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
	//				Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
	//			}
	//
	//			// Prerequisite 1 - create profile
	//			if (!Parameter.equals("error")) {
	//
	//
	//					profileID = CreateProfile.createProfile("DS_01");
	//				if (!profileID.equals("error")) {
	//					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
	//					WSClient.setData("{var_profileId}",profileID);
	//
	//					//WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	//					WSClient.setData("{VAR_RATEPLANCODE}","CKRRC");
	//					WSClient.setData("{var_rateCode}","CKRRC");
	//					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	//					WSClient.setData("{VAR_ROOMTYPE}", "OWSRT");
	//					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	//					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	//					WSClient.setData("{VAR_MARKETCODE}","MO2165");
	//					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	//
	//
	//					// Prerequisite 2 Create Reservation
	//					HashMap<String, String> resv = CreateReservation.createReservation("DS_22");
	//					String resvID = resv.get("reservationId");
	//
	//					if (!resvID.equals("error")) {
	//						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
	//						WSClient.setData("{var_resvId}", resvID);
	//						String pkg=OperaPropConfig.getDataSetForCode("PackageCode", "DS_05");
	//						pkg="QA_CHOCOLATE";
	//						WSClient.setData("{var_packageCode}",pkg);
	//
	//						// Pre-requisite : Change Reservation to add accompany guest
	//
	//						String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_07");
	//						String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);
	//
	//						//******OWS Fetch Booking*******//
	//
	//						if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
	//							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added package.</b>");
	//						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
	//						//WSClient.setData("{var_resvId}", "2212644709");
	//						String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
	//						String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);
	//
	//
	//
	//						if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
	//						{
	//						  if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
	//						  {
	//
	//
	//							  String totalCharge=WSClient.getElementValue(fetchBookingRes, "RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages", XMLType.RESPONSE);
	//							  String packageCode=WSClient.getElementValue(fetchBookingRes,"RoomStay_Packages_Package_packageCode", XMLType.RESPONSE);
	//							  String totalTaxAndCharge=WSClient.getElementValue(fetchBookingRes, "RoomStays_RoomStay_Total", XMLType.RESPONSE);
	//							  String packageTax=WSClient.getElementValue(fetchBookingRes, "Packages_Package_TaxAmount_3", XMLType.RESPONSE);
	//							  String totalTax=WSClient.getElementValue(fetchBookingRes, "RoomStays_RoomStay_ExpectedCharges_TotalTaxesAndFees", XMLType.RESPONSE);
	//
	//							  List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
	//							  LinkedHashMap<String,String> db1= new LinkedHashMap<String,String>();
	//							  db1.put("RoomRateAndPackages_Charges_Amount", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("RoomRateAndPackages_Charges_Amount_2", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("TaxesAndFees_Charges_Amount", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("Packages_Package_TaxAmount_3", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("TaxesAndFees_Charges_Description", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("RoomStay_ExpectedCharges_ChargesForPostingDate_PostingDate", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("ExpectedCharges_ChargesForPostingDate_RoomRateAndPackages_TotalCharges", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db=WSClient.getMultipleNodeList(fetchBookingRes,db1,false,XMLType.RESPONSE);
	//							  System.out.println(db);
	//
	//							  String query = WSClient.getQuery("OWSFetchBooking", "QS_31");
	//							  String price="";
	//							  price = WSClient.getDBRow(query).get("PRICE");
	//							  query=WSClient.getQuery("OWSFetchBooking", "QS_33");
	//							  String rate="";
	//							  rate= WSClient.getDBRow(query).get("MIN_AMT");
	//							  Integer totalA=Integer.parseInt(price)+Integer.parseInt(rate);
	//							  String total=totalA.toString();
	//							  System.out.println(total);
	//							  query=WSClient.getQuery("OWSFetchBooking", "QS_34");
	//							  String percent= WSClient.getDBRow(query).get("PERCENTAGE");
	//							  query=WSClient.getQuery("OWSFetchBooking", "QS_32");
	//							  String desc= WSClient.getDBRow(query).get("DES");
	//							  query=WSClient.getQuery("OWSFetchBooking", "QS_35");
	//							  String taxA1= WSClient.getDBRow(query).get("MIN_AMT");
	//							  query=WSClient.getQuery("OWSFetchBooking", "QS_36");
	//							  String percent1= WSClient.getDBRow(query).get("PERCENTAGE");
	//
	//
	//							  /*** Room and Package charges  are calculated ***/
	//
	//							  String tax=null;
	//							  Integer a=0,a3=0;
	//							  if(percent!=null){
	//							  Integer des=(Integer.parseInt(rate)*Integer.parseInt(percent));
	//							  a=(Integer) (des/100);
	//							   tax=a.toString();
	//							  System.out.println(a);
	//							  }
	//							  else
	//							   tax="0";
	//
	//							  String tax1 = null;
	//							  Integer a1 = 0;
	//							  if(percent1!=null){
	//							  Integer des1=(Integer.parseInt(taxA1)*Integer.parseInt(percent1));
	//							   a1=(Integer) (des1/100);
	//							  tax1=a1.toString();
	//							  System.out.println(a1);
	//							  }
	//							  else{
	//							   tax1="0";
	//							  }
	//							  Integer totalA2=totalA+a1+a;
	//
	//							  Integer totalP2=0,totalP1=0;
	//
	//							 if(WSAssert.assertEquals(pkg,packageCode, true)){
	//
	//							   WSClient.writeToReport(LogStatus.PASS, "Package Code : Expected -> "+ pkg+" Actual -> "+packageCode);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Package Code : Expected -> "+ pkg+" Actual -> "+packageCode);
	//
	//
	//
	//							  for(int i=0;i<db.size();i++){
	//								LinkedHashMap<String,String>  values=db.get(i);
	//								 String rateAmount="*null*";
	//								 String packAmount="*null*";
	//							  if(values.get("RateAndPackagesCharges2Amount1")==null)
	//								  rateAmount=values.get("mRateAndPackagesChargesAmount1");
	//							  else{
	//							    packAmount=values.get("mRateAndPackagesChargesAmount1");
	//							    rateAmount=values.get("RateAndPackagesCharges2Amount1");
	//							  }
	//
	//							    String roomTax=values.get("TaxesAndFeesChargesAmount1");
	//							    String totalAmount=values.get("omRateAndPackagesTotalCharges1");
	//							    String taxDesc=values.get("axesAndFeesChargesDescription1");
	//							   String startDate=values.get("PostingDate1");
	//
	//
	//							   WSClient.writeToReport(LogStatus.INFO, "<b>Validating for the date "+startDate+"</b>");
	//							  if(!price.equals("")){
	//							  if(WSAssert.assertEquals(price,packAmount, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, "Package Amount : Expected -> "+ price+" Actual -> "+packAmount);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Package Amount : Expected -> "+ price+" Actual -> "+packAmount);
	//
	//							   }
	//
	//							  if(WSAssert.assertEquals(rate,rateAmount, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, "Room Rate : Expected -> "+ rate+" Actual -> "+rateAmount);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Room Rate : Expected -> "+ rate+" Actual -> "+rateAmount);
	//
	//							  if(WSAssert.assertEquals(total,totalAmount, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, " Package and Room Charges : Expected -> "+ total+" Actual -> "+totalAmount);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, " Package and Room Charges : Expected -> "+ total+" Actual -> "+totalAmount);
	//                              if(tax1!="0"){
	//							  if(WSAssert.assertEquals(tax1,packageTax, true)){
	//
	//							   WSClient.writeToReport(LogStatus.PASS, "Package Tax : Expected ->"+ tax1+" Actual -> "+packageTax);
	//							  }
	//
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Package Tax : Expected -> "+ tax1+" Actual -> "+packageTax);
	//                              }
	//
	//							  if(WSAssert.assertEquals(tax,roomTax, true)){
	//
	//							   WSClient.writeToReport(LogStatus.PASS, "Room Tax : Expected ->"+ tax+" Actual -> "+roomTax);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Room Tax : Expected -> "+ tax+" Actual ->"+roomTax);
	//
	//							  if(WSAssert.assertEquals(desc,taxDesc, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, "Room Tax Description : Expected -> "+ desc+" Actual -> "+taxDesc);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Room Tax Description : Expected -> "+ desc+" Actual -> "+taxDesc);
	//
	//							    totalP2 = totalP2+totalA2;
	//							    totalP1 = totalP1+totalA;
	//							   a3=a1+a3+a;
	//
	//
	//							  }
	//
	//							  String totalAm=totalP1.toString();
	//							  String tax3=a3.toString();
	//							  String totalAmm=totalP2.toString();
	//
	//							  WSClient.writeToReport(LogStatus.INFO, "<b>Validating for total charges </b>");
	//
	//							  if(WSAssert.assertEquals(totalAm,totalCharge, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, " Total Package and Room Charges : Expected -> "+ totalAm+" Actual -> "+totalCharge);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, " Total Package and Room Charges : Expected -> "+ totalAm+" Actual -> "+totalCharge);
	//
	//
	//							  if(WSAssert.assertEquals(tax3,totalTax ,true)){
	//							   WSClient.writeToReport(LogStatus.PASS, "Charges with Tax : Expected -> "+ tax3+" Actual -> "+totalTax);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Charges with Tax  : Expected -> "+ tax3+" Actual -> "+totalTax);
	//
	//
	//
	//							  if(WSAssert.assertEquals(totalAmm,totalTaxAndCharge, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, "Total  Charge : Expected -> "+ totalAmm+" Actual -> "+totalTaxAndCharge);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Total Charge : Expected -> "+ totalAmm+" Actual -> "+totalTaxAndCharge);
	//
	//
	//						  }
	//
	//						}
	//
	//						 if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
	//							 if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){
	//
	//								String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
	//									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
	//							 }
	//						}
	//
	//						if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {
	//
	//							String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
	//									XMLType.RESPONSE);
	//							WSClient.writeToReport(LogStatus.INFO,
	//									"<b>The text displayed in the response is :" + message+"</b>");
	//						}
	//
	//						if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){
	//
	//							 String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	//			                 WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
	//			            }
	//
	//			            if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){
	//
	//			            	   String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
	//			                   WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
	//			               }
	//
	//					   }
	//						else
	//						{
	//							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to add package to reservation");
	//					}
	//					}
	//
	//				}
	//			}
	//		}
	//
	//	}
	//	catch(Exception e)
	//	{
	//		WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	//	}
	//	finally
	//	{
	//		// Cancel Reservation
	//        try
	//        {   //if(!WSClient.getData("{var_resvId}").equals(""))
	//           // CancelReservation.cancelReservation("DS_02");
	//        }
	//        catch (Exception e)
	//        {
	//            // TODO Auto-generated catch block
	//            e.printStackTrace();
	//        }
	//	}
	//	}
	//

	//@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})
	//
	//public void fetchBooking_1() {
	//	try {
	//
	//		String testName = "fetchBooking_23562";
	//		WSClient.startTest(testName, "verify that expected charges are being displayed correctly on fetch booking response","minimumRegression");
	//		if (OperaPropConfig.getPropertyConfigResults(
	//				new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod"})) {
	//
	//			String resortOperaValue = OPERALib.getResort();
	//			String chain = OPERALib.getChain();
	//			WSClient.setData("{var_chain}", chain);
	//			WSClient.setData("{var_resort}", resortOperaValue);
	//
	//			String uname = OPERALib.getUserName();
	//			String pwd = OPERALib.getPassword();
	//			String channel = OWSLib.getChannel();
	//			String channelType = OWSLib.getChannelType(channel);
	//			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	//			WSClient.setData("{var_owsresort}", resort);
	//			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
	//
	//			OPERALib.setOperaHeader(uname);
	//
	//			/*****check fetch_reservation parameter will be ALL*****/
	//			WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
	//			WSClient.setData("{var_settingValue}", "ALL");
	//			WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
	//			String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
	//			if (!Parameter.equals("ALL")) {
	//				WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
	//				Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
	//			}
	//
	//			// Prerequisite 1 - create profile
	//			if (!Parameter.equals("error")) {
	//
	//
	//					profileID = CreateProfile.createProfile("DS_01");
	//				if (!profileID.equals("error")) {
	//					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
	//					WSClient.setData("{var_profileId}",profileID);
	//
	//					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	//					WSClient.setData("{VAR_RATEPLANCODE}","CKRRC");
	//					WSClient.setData("{var_rateCode}","CKRRC");
	//					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	//					WSClient.setData("{VAR_ROOMTYPE}", "OWSRT");
	//					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	//					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	//					WSClient.setData("{VAR_MARKETCODE}","MO2165");
	//					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	//
	//
	//					// Prerequisite 2 Create Reservation
	//					HashMap<String, String> resv = CreateReservation.createReservation("DS_24");
	//					String resvID = resv.get("reservationId");
	//
	//					if (!resvID.equals("error")) {
	//						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
	//						WSClient.setData("{var_resvId}", resvID);
	//						String pkg=OperaPropConfig.getDataSetForCode("PackageCode", "DS_06");
	//						WSClient.setData("{var_packageCode}","QA_CHOCOLATE1");
	//
	//						// Pre-requisite : Change Reservation to add accompany guest
	//
	//						String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_02");
	//						String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);
	//
	//						//******OWS Fetch Booking*******//
	//
	//						if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
	//							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added package.</b>");
	//						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
	//						//WSClient.setData("{var_resvId}", "2212644709");
	//						String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
	//						String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);
	//
	//
	//
	//						if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
	//						{
	//						  if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
	//						  {
	//
	//
	//							  String totalCharge=WSClient.getElementValue(fetchBookingRes, "RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages", XMLType.RESPONSE);
	//							  String packageCode=WSClient.getElementValue(fetchBookingRes,"RoomStay_Packages_Package_packageCode", XMLType.RESPONSE);
	//							  String totalTaxAndCharge=WSClient.getElementValue(fetchBookingRes, "RoomStays_RoomStay_Total", XMLType.RESPONSE);
	//							  String packageTax=WSClient.getElementValue(fetchBookingRes, "Packages_Package_TaxAmount_3", XMLType.RESPONSE);
	//							  String totalTax=WSClient.getElementValue(fetchBookingRes, "RoomStays_RoomStay_ExpectedCharges_TotalTaxesAndFees", XMLType.RESPONSE);
	//
	//							  List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
	//							  LinkedHashMap<String,String> db1= new LinkedHashMap<String,String>();
	//							  db1.put("RoomRateAndPackages_Charges_Amount", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("RoomRateAndPackages_Charges_Amount_2", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("TaxesAndFees_Charges_Amount", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("Packages_Package_TaxAmount_3", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("TaxesAndFees_Charges_Description", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("RoomStay_ExpectedCharges_ChargesForPostingDate_PostingDate", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("ExpectedCharges_ChargesForPostingDate_RoomRateAndPackages_TotalCharges", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db=WSClient.getMultipleNodeList(fetchBookingRes,db1,false,XMLType.RESPONSE);
	//							  System.out.println(db);
	//
	//							  String query = WSClient.getQuery("OWSFetchBooking", "QS_31");
	//							  String price="";
	//							  price = WSClient.getDBRow(query).get("PRICE");
	//							  query=WSClient.getQuery("OWSFetchBooking", "QS_33");
	//							  String rate="";
	//							  rate= WSClient.getDBRow(query).get("MIN_AMT");
	//							  Integer totalA=Integer.parseInt(price)+Integer.parseInt(rate);
	//							  String total=totalA.toString();
	//							  System.out.println(total);
	//							  query=WSClient.getQuery("OWSFetchBooking", "QS_34");
	//							  String percent= WSClient.getDBRow(query).get("PERCENTAGE");
	//							  query=WSClient.getQuery("OWSFetchBooking", "QS_32");
	//							  String desc= WSClient.getDBRow(query).get("DES");
	//							  query=WSClient.getQuery("OWSFetchBooking", "QS_35");
	//							  String taxA1= WSClient.getDBRow(query).get("MIN_AMT");
	//							  query=WSClient.getQuery("OWSFetchBooking", "QS_36");
	//							  String percent1= WSClient.getDBRow(query).get("PERCENTAGE");
	//
	//
	//							  /*** Room and Package charges  are calculated ***/
	//
	//							  String tax=null;
	//							  Integer a=0,a3=0;
	//							  if(percent!=null){
	//							  Integer des=(Integer.parseInt(rate)*Integer.parseInt(percent));
	//							  a=(Integer) (des/100);
	//							   tax=a.toString();
	//							  System.out.println(a);
	//							  }
	//							  else
	//							   tax="0";
	//
	//							  String tax1 = null;
	//							  Integer a1 = 0;
	//							  if(percent1!=null){
	//							  Integer des1=(Integer.parseInt(taxA1)*Integer.parseInt(percent1));
	//							   a1=(Integer) (des1/100);
	//							  tax1=a1.toString();
	//							  System.out.println(a1);
	//							  }
	//							  else{
	//							   tax1="0";
	//							  }
	//							  Integer totalA2=totalA+a1+a;
	//
	//							  Integer totalP2=0,totalP1=0;
	//
	//							 if(WSAssert.assertEquals(pkg,packageCode, true)){
	//
	//							   WSClient.writeToReport(LogStatus.PASS, "Package Code : Expected -> "+ pkg+" Actual -> "+packageCode);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Package Code : Expected -> "+ pkg+" Actual -> "+packageCode);
	//
	//
	//
	//							  for(int i=0;i<db.size();i++){
	//								LinkedHashMap<String,String>  values=db.get(i);
	//								 String rateAmount="*null*";
	//								 String packAmount="*null*";
	//							  if(values.get("RateAndPackagesCharges2Amount1")==null)
	//								  rateAmount=values.get("mRateAndPackagesChargesAmount1");
	//							  else{
	//							    packAmount=values.get("mRateAndPackagesChargesAmount1");
	//							    rateAmount=values.get("RateAndPackagesCharges2Amount1");
	//							  }
	//
	//							    String roomTax=values.get("TaxesAndFeesChargesAmount1");
	//							    String totalAmount=values.get("omRateAndPackagesTotalCharges1");
	//							    String taxDesc=values.get("axesAndFeesChargesDescription1");
	//							   String startDate=values.get("PostingDate1");
	//
	//
	//							   WSClient.writeToReport(LogStatus.INFO, "<b>Validating for the date "+startDate+"</b>");
	//							  if(!price.equals("")){
	//							  if(WSAssert.assertEquals(price,packAmount, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, "Package Amount : Expected -> "+ price+" Actual -> "+packAmount);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Package Amount : Expected -> "+ price+" Actual -> "+packAmount);
	//
	//							   }
	//
	//							  if(WSAssert.assertEquals(rate,rateAmount, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, "Room Rate : Expected -> "+ rate+" Actual -> "+rateAmount);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Room Rate : Expected -> "+ rate+" Actual -> "+rateAmount);
	//
	//							  if(WSAssert.assertEquals(total,totalAmount, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, " Package and Room Charges : Expected -> "+ total+" Actual -> "+totalAmount);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, " Package and Room Charges : Expected -> "+ total+" Actual -> "+totalAmount);
	//                              if(tax1!="0"){
	//							  if(WSAssert.assertEquals(tax1,packageTax, true)){
	//
	//							   WSClient.writeToReport(LogStatus.PASS, "Package Tax : Expected ->"+ tax1+" Actual -> "+packageTax);
	//							  }
	//
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Package Tax : Expected -> "+ tax1+" Actual -> "+packageTax);
	//                              }
	//
	//							  if(WSAssert.assertEquals(tax,roomTax, true)){
	//
	//							   WSClient.writeToReport(LogStatus.PASS, "Room Tax : Expected ->"+ tax+" Actual -> "+roomTax);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Room Tax : Expected -> "+ tax+" Actual ->"+roomTax);
	//
	//							  if(WSAssert.assertEquals(desc,taxDesc, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, "Room Tax Description : Expected -> "+ desc+" Actual -> "+taxDesc);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Room Tax Description : Expected -> "+ desc+" Actual -> "+taxDesc);
	//
	//							    totalP2 = totalP2+totalA2;
	//							    totalP1 = totalP1+totalA;
	//							   a3=a1+a3+a;
	//
	//
	//							  }
	//
	//							  String totalAm=totalP1.toString();
	//							  String tax3=a3.toString();
	//							  String totalAmm=totalP2.toString();
	//
	//							  WSClient.writeToReport(LogStatus.INFO, "<b>Validating for total charges </b>");
	//
	//							  if(WSAssert.assertEquals(totalAm,totalCharge, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, " Total Package and Room Charges : Expected -> "+ totalAm+" Actual -> "+totalCharge);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, " Total Package and Room Charges : Expected -> "+ totalAm+" Actual -> "+totalCharge);
	//
	//
	//							  if(WSAssert.assertEquals(tax3,totalTax ,true)){
	//							   WSClient.writeToReport(LogStatus.PASS, "Charges with Tax : Expected -> "+ tax3+" Actual -> "+totalTax);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Charges with Tax  : Expected -> "+ tax3+" Actual -> "+totalTax);
	//
	//
	//
	//							  if(WSAssert.assertEquals(totalAmm,totalTaxAndCharge, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, "Total  Charge : Expected -> "+ totalAmm+" Actual -> "+totalTaxAndCharge);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Total Charge : Expected -> "+ totalAmm+" Actual -> "+totalTaxAndCharge);
	//
	//
	//						  }
	//
	//						}
	//
	//						 if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
	//							 if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){
	//
	//								String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
	//									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
	//							 }
	//						}
	//
	//						if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {
	//
	//							String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
	//									XMLType.RESPONSE);
	//							WSClient.writeToReport(LogStatus.INFO,
	//									"<b>The text displayed in the response is :" + message+"</b>");
	//						}
	//
	//						if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){
	//
	//							 String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	//			                 WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
	//			            }
	//
	//			            if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){
	//
	//			            	   String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
	//			                   WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
	//			               }
	//
	//					   }
	//						else
	//						{
	//							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to add package to reservation");
	//					}
	//					}
	//
	//				}
	//			}
	//		}
	//
	//	}
	//	catch(Exception e)
	//	{
	//		WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	//	}
	//	finally
	//	{
	//		// Cancel Reservation
	//        try
	//        {   if(!WSClient.getData("{var_resvId}").equals(""))
	//            CancelReservation.cancelReservation("DS_02");
	//        }
	//        catch (Exception e)
	//        {
	//            // TODO Auto-generated catch block
	//            e.printStackTrace();
	//        }
	//	}
	//	}
	//
	//@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})
	//
	//public void fetchBooking_2() {
	//	try {
	//
	//		String testName = "fetchBooking_23562";
	//		WSClient.startTest(testName, "verify that expected charges are being displayed correctly on fetch booking response when package with posting ryhtm as arrival night is added to reservation.","minimumRegression");
	//		if (OperaPropConfig.getPropertyConfigResults(
	//				new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod"})) {
	//
	//			String resortOperaValue = OPERALib.getResort();
	//			String chain = OPERALib.getChain();
	//			WSClient.setData("{var_chain}", chain);
	//			WSClient.setData("{var_resort}", resortOperaValue);
	//
	//			String uname = OPERALib.getUserName();
	//			String pwd = OPERALib.getPassword();
	//			String channel = OWSLib.getChannel();
	//			String channelType = OWSLib.getChannelType(channel);
	//			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	//			WSClient.setData("{var_owsresort}", resort);
	//			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
	//
	//			OPERALib.setOperaHeader(uname);
	//
	//			/*****check fetch_reservation parameter will be ALL*****/
	//			WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
	//			WSClient.setData("{var_settingValue}", "ALL");
	//			WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
	//			String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
	//			if (!Parameter.equals("ALL")) {
	//				WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
	//				Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
	//			}
	//
	//			// Prerequisite 1 - create profile
	//			if (!Parameter.equals("error")) {
	//
	//
	//					profileID = CreateProfile.createProfile("DS_01");
	//				if (!profileID.equals("error")) {
	//					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
	//					WSClient.setData("{var_profileId}",profileID);
	//
	//					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	//					WSClient.setData("{VAR_RATEPLANCODE}","CKRRC");
	//					WSClient.setData("{var_rateCode}","CKRRC");
	//					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	//					WSClient.setData("{VAR_ROOMTYPE}", "OWSRT");
	//					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	//					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	//					WSClient.setData("{VAR_MARKETCODE}","MO2165");
	//					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	//
	//
	//					// Prerequisite 2 Create Reservation
	//					HashMap<String, String> resv = CreateReservation.createReservation("DS_22");
	//					String resvID = resv.get("reservationId");
	//
	//					if (!resvID.equals("error")) {
	//						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
	//						WSClient.setData("{var_resvId}", resvID);
	//						String pkg=OperaPropConfig.getDataSetForCode("PackageCode", "DS_07");
	//						WSClient.setData("{var_packageCode}",pkg);
	//
	//						// Pre-requisite : Change Reservation to add accompany guest
	//
	//						String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_07");
	//						String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);
	//
	//						//******OWS Fetch Booking*******//
	//
	//						if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
	//							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added package.</b>");
	//						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
	//						//WSClient.setData("{var_resvId}", "2212644709");
	//						String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
	//						String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);
	//
	//
	//
	//						if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
	//						{
	//						  if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
	//						  {
	//
	//
	//							  String totalCharge=WSClient.getElementValue(fetchBookingRes, "RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages", XMLType.RESPONSE);
	//							  String packageCode=WSClient.getElementValue(fetchBookingRes,"RoomStay_Packages_Package_packageCode", XMLType.RESPONSE);
	//							  String totalTaxAndCharge=WSClient.getElementValue(fetchBookingRes, "RoomStays_RoomStay_Total", XMLType.RESPONSE);
	//							  String packageTax=WSClient.getElementValue(fetchBookingRes, "Packages_Package_TaxAmount_3", XMLType.RESPONSE);
	//							  String totalTax=WSClient.getElementValue(fetchBookingRes, "RoomStays_RoomStay_ExpectedCharges_TotalTaxesAndFees", XMLType.RESPONSE);
	//
	//							  List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
	//							  LinkedHashMap<String,String> db1= new LinkedHashMap<String,String>();
	//							  db1.put("RoomRateAndPackages_Charges_Amount", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("RoomRateAndPackages_Charges_Amount_2", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("TaxesAndFees_Charges_Amount", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("Packages_Package_TaxAmount_3", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("TaxesAndFees_Charges_Description", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("RoomStay_ExpectedCharges_ChargesForPostingDate_PostingDate", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db1.put("ExpectedCharges_ChargesForPostingDate_RoomRateAndPackages_TotalCharges", "RoomStay_ExpectedCharges_ChargesForPostingDate");
	//							  db=WSClient.getMultipleNodeList(fetchBookingRes,db1,false,XMLType.RESPONSE);
	//							  System.out.println(db);
	//
	//							  String query = WSClient.getQuery("OWSFetchBooking", "QS_31");
	//							  String price="";
	//							  price = WSClient.getDBRow(query).get("PRICE");
	//							  query=WSClient.getQuery("OWSFetchBooking", "QS_33");
	//							  String rate="";
	//							  rate= WSClient.getDBRow(query).get("MIN_AMT");
	//							  Integer totalA=Integer.parseInt(price)+Integer.parseInt(rate);
	//							  String total=totalA.toString();
	//							  System.out.println(total);
	//							  query=WSClient.getQuery("OWSFetchBooking", "QS_34");
	//							  String percent= WSClient.getDBRow(query).get("PERCENTAGE");
	//							  query=WSClient.getQuery("OWSFetchBooking", "QS_32");
	//							  String desc= WSClient.getDBRow(query).get("DES").trim();
	//							  query=WSClient.getQuery("OWSFetchBooking", "QS_35");
	//							  String taxA1= WSClient.getDBRow(query).get("MIN_AMT");
	//							  query=WSClient.getQuery("OWSFetchBooking", "QS_36");
	//							  String percent1= WSClient.getDBRow(query).get("PERCENTAGE");
	//
	//
	//							  /*** Room and Package charges  are calculated ***/
	//
	//							  String tax=null;
	//							  Integer a=0,a3=0;
	//							  if(percent!=null){
	//							  Integer des=(Integer.parseInt(rate)*Integer.parseInt(percent));
	//							  a=(Integer) (des/100);
	//							   tax=a.toString();
	//							  System.out.println(a);
	//							  }
	//							  else
	//							   tax="0";
	//
	//							  String tax1 = null;
	//							  Integer a1 = 0;
	//							  if(percent1!=null){
	//							  Integer des1=(Integer.parseInt(taxA1)*Integer.parseInt(percent1));
	//							   a1=(Integer) (des1/100);
	//							  tax1=a1.toString();
	//							  System.out.println(a1);
	//							  }
	//							  else{
	//							   tax1="0";
	//							  }
	//							  Integer totalA2=totalA+a1+a;
	//
	//							  Integer totalP2=0,totalP1=0;
	//
	//							 if(WSAssert.assertEquals(pkg,packageCode, true)){
	//
	//							   WSClient.writeToReport(LogStatus.PASS, "Package Code : Expected -> "+ pkg+" Actual -> "+packageCode);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Package Code : Expected -> "+ pkg+" Actual -> "+packageCode);
	//
	//
	//							 int date=0;
	//							  for(int i=0;i<db.size();i++){
	//								  ++date;
	//								LinkedHashMap<String,String>  values=db.get(i);
	//								 String rateAmount="*null*";
	//								 String packAmount="*null*";
	//							  if(values.get("RateAndPackagesCharges2Amount1")==null)
	//								  rateAmount=values.get("mRateAndPackagesChargesAmount1");
	//							  else{
	//							    packAmount=values.get("mRateAndPackagesChargesAmount1");
	//							    rateAmount=values.get("RateAndPackagesCharges2Amount1");
	//							  }
	//
	//							    String roomTax=values.get("TaxesAndFeesChargesAmount1");
	//							    String totalAmount=values.get("omRateAndPackagesTotalCharges1");
	//							    String taxDesc=values.get("axesAndFeesChargesDescription1").trim();
	//							   String startDate=values.get("PostingDate1");
	//
	//							     if(date==1){
	//							   WSClient.writeToReport(LogStatus.INFO, "<b>Validating for the date "+startDate+"</b>");
	//							  if(!price.equals("")){
	//							  if(WSAssert.assertEquals(price,packAmount, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, "Package Amount : Expected -> "+ price+" Actual -> "+packAmount);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Package Amount : Expected -> "+ price+" Actual -> "+packAmount);
	//
	//							   }
	//
	//							  if(WSAssert.assertEquals(rate,rateAmount, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, "Room Rate : Expected -> "+ rate+" Actual -> "+rateAmount);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Room Rate : Expected -> "+ rate+" Actual -> "+rateAmount);
	//
	//							  if(WSAssert.assertEquals(total,totalAmount, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, " Package and Room Charges : Expected -> "+ total+" Actual -> "+totalAmount);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, " Package and Room Charges : Expected -> "+ total+" Actual -> "+totalAmount);
	//                              if(tax1!="0"){
	//							  if(WSAssert.assertEquals(tax1,packageTax, true)){
	//
	//							   WSClient.writeToReport(LogStatus.PASS, "Package Tax : Expected ->"+ tax1+" Actual -> "+packageTax);
	//							  }
	//
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Package Tax : Expected -> "+ tax1+" Actual -> "+packageTax);
	//                              }
	//
	//							  if(WSAssert.assertEquals(tax,roomTax, true)){
	//
	//							   WSClient.writeToReport(LogStatus.PASS, "Room Tax : Expected ->"+ tax+" Actual -> "+roomTax);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Room Tax : Expected -> "+ tax+" Actual ->"+roomTax);
	//
	//							  if(WSAssert.assertEquals(desc,taxDesc, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, "Room Tax Description : Expected -> "+ desc+" Actual -> "+taxDesc);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Room Tax Description : Expected -> "+ desc+" Actual -> "+taxDesc);
	//
	//
	//							  totalP2 = totalP2+totalA2;
	//							    totalP1 = totalP1+totalA;
	//							   a3=a1+a3+a;
	//
	//							     }
	//							     else
	//							     {
	//
	//										   WSClient.writeToReport(LogStatus.INFO, "<b>Validating for the date "+startDate+"</b>");
	//										  if(!price.equals("")){
	//										  if(WSAssert.assertEquals("*null*",packAmount, true)){
	//										   WSClient.writeToReport(LogStatus.PASS, "Package Amount : Expected -> "+"*null*"+" Actual -> "+packAmount);
	//										  }
	//										  else
	//										   WSClient.writeToReport(LogStatus.FAIL, "Package Amount : Expected -> "+ "*null*"+" Actual -> "+packAmount);
	//
	//										   }
	//
	//										  if(WSAssert.assertEquals(rate,rateAmount, true)){
	//										   WSClient.writeToReport(LogStatus.PASS, "Room Rate : Expected -> "+ rate+" Actual -> "+rateAmount);
	//										  }
	//										  else
	//										   WSClient.writeToReport(LogStatus.FAIL, "Room Rate : Expected -> "+ rate+" Actual -> "+rateAmount);
	//
	//										  if(WSAssert.assertEquals(rate,totalAmount, true)){
	//										   WSClient.writeToReport(LogStatus.PASS, " Package and Room Charges : Expected -> "+rate+" Actual -> "+totalAmount);
	//										  }
	//										  else
	//										   WSClient.writeToReport(LogStatus.FAIL, " Package and Room Charges : Expected -> "+ rate+" Actual -> "+totalAmount);
	//			                              if(tax1!="0"){
	//										  if(WSAssert.assertEquals("*null*",packageTax, true)){
	//
	//										   WSClient.writeToReport(LogStatus.PASS, "Package Tax : Expected ->"+"*null*"+" Actual -> "+packageTax);
	//										  }
	//
	//										  else
	//										   WSClient.writeToReport(LogStatus.FAIL, "Package Tax : Expected -> "+"*null*"+" Actual -> "+packageTax);
	//			                              }
	//
	//										  if(WSAssert.assertEquals(tax,roomTax, true)){
	//
	//										   WSClient.writeToReport(LogStatus.PASS, "Room Tax : Expected ->"+ tax+" Actual -> "+roomTax);
	//										  }
	//										  else
	//										   WSClient.writeToReport(LogStatus.FAIL, "Room Tax : Expected -> "+ tax+" Actual ->"+roomTax);
	//
	//										  if(WSAssert.assertEquals(desc,taxDesc, true)){
	//										   WSClient.writeToReport(LogStatus.PASS, "Room Tax Description : Expected -> "+ desc+" Actual -> "+taxDesc);
	//										  }
	//										  else
	//										   WSClient.writeToReport(LogStatus.FAIL, "Room Tax Description : Expected -> "+ desc+" Actual -> "+taxDesc);
	//
	//
	//										    totalP2 = totalP2+Integer.parseInt(tax)+Integer.parseInt(rate);
	//										    totalP1 = totalP1+Integer.parseInt(rate);
	//										   a3=a1+a3+a;
	//
	//							     }
	//
	//
	//
	//
	//							  }
	//
	//							  String totalAm=totalP1.toString();
	//							  String tax3=a3.toString();
	//							  String totalAmm=totalP2.toString();
	//
	//							  WSClient.writeToReport(LogStatus.INFO, "<b>Validating for total charges </b>");
	//
	//							  if(WSAssert.assertEquals(totalAm,totalCharge, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, " Total Package and Room Charges : Expected -> "+ totalAm+" Actual -> "+totalCharge);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, " Total Package and Room Charges : Expected -> "+ totalAm+" Actual -> "+totalCharge);
	//
	//
	//							  if(WSAssert.assertEquals(tax3,totalTax ,true)){
	//							   WSClient.writeToReport(LogStatus.PASS, "Charges with Tax : Expected -> "+ tax3+" Actual -> "+totalTax);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Charges with Tax  : Expected -> "+ tax3+" Actual -> "+totalTax);
	//
	//
	//
	//							  if(WSAssert.assertEquals(totalAmm,totalTaxAndCharge, true)){
	//							   WSClient.writeToReport(LogStatus.PASS, "Total  Charge : Expected -> "+ totalAmm+" Actual -> "+totalTaxAndCharge);
	//							  }
	//							  else
	//							   WSClient.writeToReport(LogStatus.FAIL, "Total Charge : Expected -> "+ totalAmm+" Actual -> "+totalTaxAndCharge);
	//
	//
	//
	//						  }
	//
	//						}
	//
	//						 if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
	//							 if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){
	//
	//								String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
	//									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
	//							 }
	//						}
	//
	//						if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {
	//
	//							String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
	//									XMLType.RESPONSE);
	//							WSClient.writeToReport(LogStatus.INFO,
	//									"<b>The text displayed in the response is :" + message+"</b>");
	//						}
	//
	//						if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){
	//
	//							 String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	//			                 WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
	//			            }
	//
	//			            if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){
	//
	//			            	   String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
	//			                   WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
	//			               }
	//
	//					   }
	//						else
	//						{
	//							WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to add package to reservation");
	//					}
	//					}
	//
	//				}
	//			}
	//		}
	//
	//	}
	//	catch(Exception e)
	//	{
	//		WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	//	}
	//	finally
	//	{
	//		// Cancel Reservation
	//        try
	//        {   if(!WSClient.getData("{var_resvId}").equals(""))
	//            CancelReservation.cancelReservation("DS_02");
	//        }
	//        catch (Exception e)
	//        {
	//            // TODO Auto-generated catch block
	//            e.printStackTrace();
	//        }
	//	}
	//	}

	
	// Need to update this script later
//	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation", "fetchBooking_41803_Uday"})

	public void fetchBooking_41803() {
		try {

			String testName = "fetchBooking_41803";
			WSClient.startTest(testName, "verify that expected charges are being displayed correctly on fetch booking response.","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod"})) {

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

				/*****check fetch_reservation parameter will be ALL*****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {


					profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{var_profileId}",profileID);

						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));
						//WSClient.setData("{VAR_RATEPLANCODE}","CKRRC");
						WSClient.setData("{var_rateCode}", OperaPropConfig.getDataSetForCode("RateCode", "DS_03"));
						//WSClient.setData("{var_rateCode}","CKRRC");
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						//WSClient.setData("{VAR_ROOMTYPE}", "OWSRT");
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						//WSClient.setData("{VAR_MARKETCODE}","MO2165");
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));


						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_28");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
							WSClient.setData("{var_resvId}", resvID);
							String pkg=OperaPropConfig.getDataSetForCode("PackageCode", "DS_06");
							WSClient.setData("{var_packageCode}",pkg);

							// Pre-requisite : Change Reservation to add accompany guest

							String changeReservationReq= WSClient.createSOAPMessage("ChangeReservation", "DS_12");
							String changeReservationRes = WSClient.processSOAPMessage(changeReservationReq);

							//******OWS Fetch Booking*******//

							if(WSAssert.assertIfElementExists(changeReservationRes,"ChangeReservationRS_Success",true)){
								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully added package.</b>");
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								//WSClient.setData("{var_resvId}", "2212644709");
								String fetchBookingReq= WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
								String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);



								if(WSAssert.assertIfElementExists(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag",false))
								{
									if(WSAssert.assertIfElementValueEquals(fetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
									{


										String totalCharge=WSClient.getElementValue(fetchBookingRes, "RoomStays_RoomStay_ExpectedCharges_TotalRoomRateAndPackages", XMLType.RESPONSE);
										String packageCode=WSClient.getElementValue(fetchBookingRes,"RoomStay_Packages_Package_packageCode", XMLType.RESPONSE);
										String totalTaxAndCharge=WSClient.getElementValue(fetchBookingRes, "RoomStays_RoomStay_Total", XMLType.RESPONSE);
										String packageTax=WSClient.getElementValue(fetchBookingRes, "Packages_Package_TaxAmount_3", XMLType.RESPONSE);
										String totalTax=WSClient.getElementValue(fetchBookingRes, "RoomStays_RoomStay_ExpectedCharges_TotalTaxesAndFees", XMLType.RESPONSE);

										List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
										LinkedHashMap<String,String> db1= new LinkedHashMap<String,String>();
										db1.put("RoomRateAndPackages_Charges_Amount", "RoomStay_ExpectedCharges_ChargesForPostingDate");
										db1.put("RoomRateAndPackages_Charges_Amount_2", "RoomStay_ExpectedCharges_ChargesForPostingDate");
										db1.put("TaxesAndFees_Charges_Amount", "RoomStay_ExpectedCharges_ChargesForPostingDate");
										db1.put("Packages_Package_TaxAmount_3", "RoomStay_ExpectedCharges_ChargesForPostingDate");
										db1.put("TaxesAndFees_Charges_Description", "RoomStay_ExpectedCharges_ChargesForPostingDate");
										db1.put("RoomStay_ExpectedCharges_ChargesForPostingDate_PostingDate", "RoomStay_ExpectedCharges_ChargesForPostingDate");
										db1.put("ExpectedCharges_ChargesForPostingDate_RoomRateAndPackages_TotalCharges", "RoomStay_ExpectedCharges_ChargesForPostingDate");
										db=WSClient.getMultipleNodeList(fetchBookingRes,db1,false,XMLType.RESPONSE);
										System.out.println("This is from actual" + db);

										String query = WSClient.getQuery("OWSFetchBooking", "QS_31");
										String price="";
										price = WSClient.getDBRow(query).get("PRICE");
										query=WSClient.getQuery("OWSFetchBooking", "QS_33");
										String rate="";
										rate= WSClient.getDBRow(query).get("MIN_AMT");
										Integer totalA=Integer.parseInt(price)+Integer.parseInt(rate);
										String total=totalA.toString();
										System.out.println(total);
										query=WSClient.getQuery("OWSFetchBooking", "QS_34");
										String percent= WSClient.getDBRow(query).get("PERCENTAGE");
										query=WSClient.getQuery("OWSFetchBooking", "QS_32");
										String desc= WSClient.getDBRow(query).get("DES");
										System.out.println("####");
										query=WSClient.getQuery("OWSFetchBooking", "QS_35");
										String percent1= WSClient.getDBRow(query).get("PERCENTAGE");

										List<LinkedHashMap<String, String>> db2 = new ArrayList<LinkedHashMap<String, String>>();
										db2=WSClient.getDBRows(WSClient.getQuery("QS_36"));


										/*** Room and Package charges  are calculated ***/

										String tax=null;
										Integer a=0,a3=0;
										if(percent!=null){
											Integer des=(Integer.parseInt(rate)*Integer.parseInt(percent));
											a=des/100;
											tax=a.toString();
											System.out.println(a);
										}
										else
											tax="0";

										String tax1 = null;
										Integer a1 = 0;
										if(percent1!=null){
											Integer des1=(Integer.parseInt(price)*Integer.parseInt(percent1));
											a1=des1/100;
											tax1=a1.toString();
											System.out.println(a1);
										}
										else{
											tax1="0";
										}
										Integer totalA2=totalA+a1+a;

										Integer totalP2=0,totalP1=0;

										if(WSAssert.assertEquals(pkg,packageCode, true)){

											WSClient.writeToReport(LogStatus.PASS, "Package Code : Expected -> "+ pkg+" Actual -> "+packageCode);
										}
										else
											WSClient.writeToReport(LogStatus.FAIL, "Package Code : Expected -> "+ pkg+" Actual -> "+packageCode);


										int date=0;
										int duration=db2.size();
										for(int i=0;i<db.size();i++){
											++date;
											LinkedHashMap<String,String>  values=db.get(i);
											String rateAmount="*null*";
											String packAmount="*null*";
											System.out.println("This is from values "+values);
											if(values.get("RateAndPackagesCharges2Amount1")==null)
												rateAmount=values.get("mRateAndPackagesChargesAmount1");
											else{
												packAmount=values.get("mRateAndPackagesChargesAmount1");
												rateAmount=values.get("RateAndPackagesCharges2Amount1");
											}

											String roomTax=values.get("TaxesAndFeesChargesAmount1");
											String totalAmount=values.get("omRateAndPackagesTotalCharges1");
											String taxDesc=values.get("axesAndFeesChargesDescription1");
											String startDate=values.get("PostingDate1");

											if(date<=duration){
												WSClient.writeToReport(LogStatus.INFO, "<b>Validating for the date "+startDate+"</b>");
												if(!price.equals("")){
													if(WSAssert.assertEquals(price,packAmount, true)){
														WSClient.writeToReport(LogStatus.PASS, "Package Amount : Expected -> "+ price+" Actual -> "+packAmount);
													}
													else
														WSClient.writeToReport(LogStatus.FAIL, "Package Amount : Expected -> "+ price+" Actual -> "+packAmount);

												}

												if(WSAssert.assertEquals(rate,rateAmount, true)){
													WSClient.writeToReport(LogStatus.PASS, "Room Rate : Expected -> "+ rate+" Actual -> "+rateAmount);
												}
												else
													WSClient.writeToReport(LogStatus.FAIL, "Room Rate : Expected -> "+ rate+" Actual -> "+rateAmount);

												if(WSAssert.assertEquals(total,totalAmount, true)){
													WSClient.writeToReport(LogStatus.PASS, " Package and Room Charges : Expected -> "+ total+" Actual -> "+totalAmount);
												}
												else
													WSClient.writeToReport(LogStatus.FAIL, " Package and Room Charges : Expected -> "+ total+" Actual -> "+totalAmount);
												if(tax1!="0"){
													if(WSAssert.assertEquals(tax1,packageTax, true)){

														WSClient.writeToReport(LogStatus.PASS, "Package Tax : Expected ->"+ tax1+" Actual -> "+packageTax);
													}

													else
														WSClient.writeToReport(LogStatus.FAIL, "Package Tax : Expected -> "+ tax1+" Actual -> "+packageTax);
												}

												//							  if(WSAssert.assertEquals(tax,roomTax, true)){
												//
												//							   WSClient.writeToReport(LogStatus.PASS, "Room Tax : Expected ->"+ tax+" Actual -> "+roomTax);
												//							  }
												//							  else
												//							   WSClient.writeToReport(LogStatus.FAIL, "Room Tax : Expected -> "+ tax+" Actual ->"+roomTax);

												//							  if(WSAssert.assertEquals(desc,taxDesc, true)){
												//							   WSClient.writeToReport(LogStatus.PASS, "Room Tax Description : Expected -> "+ desc+" Actual -> "+taxDesc);
												//							  }
												//							  else
												//							   WSClient.writeToReport(LogStatus.FAIL, "Room Tax Description : Expected -> "+ desc+" Actual -> "+taxDesc);
												//

												totalP2 = totalP2+totalA2;
												totalP1 = totalP1+totalA;
												a3=a1+a3+a;

											}
											else
											{

												WSClient.writeToReport(LogStatus.INFO, "<b>Validating for the date "+startDate+"</b>");
												if(!price.equals("")){
													if(WSAssert.assertEquals("*null*",packAmount, true)){
														WSClient.writeToReport(LogStatus.PASS, "Package Amount : Expected -> "+"*null*"+" Actual -> "+packAmount);
													}
													else
														WSClient.writeToReport(LogStatus.FAIL, "Package Amount : Expected -> "+ "*null*"+" Actual -> "+packAmount);

												}

												if(WSAssert.assertEquals(rate,rateAmount, true)){
													WSClient.writeToReport(LogStatus.PASS, "Room Rate : Expected -> "+ rate+" Actual -> "+rateAmount);
												}
												else
													WSClient.writeToReport(LogStatus.FAIL, "Room Rate : Expected -> "+ rate+" Actual -> "+rateAmount);

												if(WSAssert.assertEquals(rate,totalAmount, true)){
													WSClient.writeToReport(LogStatus.PASS, " Package and Room Charges : Expected -> "+rate+" Actual -> "+totalAmount);
												}
												else
													WSClient.writeToReport(LogStatus.FAIL, " Package and Room Charges : Expected -> "+ rate+" Actual -> "+totalAmount);
												if(tax1!="0"){
													if(WSAssert.assertEquals("*null*",packageTax, true)){

														WSClient.writeToReport(LogStatus.PASS, "Package Tax : Expected ->"+"*null*"+" Actual -> "+packageTax);
													}

													else
														WSClient.writeToReport(LogStatus.FAIL, "Package Tax : Expected -> "+"*null*"+" Actual -> "+packageTax);
												}

												//										  if(WSAssert.assertEquals(tax,roomTax, true)){
												//
												//										   WSClient.writeToReport(LogStatus.PASS, "Room Tax : Expected ->"+ tax+" Actual -> "+roomTax);
												//										  }
												//										  else
												//										   WSClient.writeToReport(LogStatus.FAIL, "Room Tax : Expected -> "+ tax+" Actual ->"+roomTax);

												//										  if(WSAssert.assertEquals(desc,taxDesc, true)){
												//										   WSClient.writeToReport(LogStatus.PASS, "Room Tax Description : Expected -> "+ desc+" Actual -> "+taxDesc);
												//										  }
												//										  else
												//										   WSClient.writeToReport(LogStatus.FAIL, "Room Tax Description : Expected -> "+ desc+" Actual -> "+taxDesc);
												//

												totalP2 = totalP2+Integer.parseInt(tax)+Integer.parseInt(rate);
												totalP1 = totalP1+Integer.parseInt(rate);
												a3=a1+a3+a;

											}


										}

										String totalAm=totalP1.toString();
										String tax3=a3.toString();
										String totalAmm=totalP2.toString();

										WSClient.writeToReport(LogStatus.INFO, "<b>Validating for total charges </b>");

										if(WSAssert.assertEquals(totalAm,totalCharge, true)){
											WSClient.writeToReport(LogStatus.PASS, " Total Package and Room Charges : Expected -> "+ totalAm+" Actual -> "+totalCharge);
										}
										else
											WSClient.writeToReport(LogStatus.FAIL, " Total Package and Room Charges : Expected -> "+ totalAm+" Actual -> "+totalCharge);


										if(WSAssert.assertEquals(tax3,totalTax ,true)){
											WSClient.writeToReport(LogStatus.PASS, "Charges with Tax : Expected -> "+ tax3+" Actual -> "+totalTax);
										}
										else
											WSClient.writeToReport(LogStatus.FAIL, "Charges with Tax  : Expected -> "+ tax3+" Actual -> "+totalTax);



										if(WSAssert.assertEquals(totalAmm,totalTaxAndCharge, true)){
											WSClient.writeToReport(LogStatus.PASS, "Total  Charge : Expected -> "+ totalAmm+" Actual -> "+totalTaxAndCharge);
										}
										else
											WSClient.writeToReport(LogStatus.FAIL, "Total Charge : Expected -> "+ totalAmm+" Actual -> "+totalTaxAndCharge);



									}

								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
									if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

										String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
									}
								}

								if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

									String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
											XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,
											"<b>The text displayed in the response is :" + message+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

									String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
								}

								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

									String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
								}

							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING, "Blocked : Unable to add package to reservation");
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
			// Cancel Reservation
			try
			{   if(!WSClient.getData("{var_resvId}").equals(""))
				CancelReservation.cancelReservation("DS_02");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Test(groups = { "minimumRegression", "FetchBooking", "OWS", "Reservation" })

	public void fetchBooking_41802() {
		try {

			String testName = "fetchBooking_41802";
			WSClient.startTest(testName,
					"verify that rate plan information are coming correctly in fetch booking response",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

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


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/***** check fetch_reservation parameter will be ALL *****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}
				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {

					if (profileID.equals(""))
						profileID = CreateProfile.createProfile("DS_01");
					if (!profileID.equals("error")) {
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");

						WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_11"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

						// Prerequisite 2 Create Reservation
						HashMap<String, String> resv = CreateReservation.createReservation("DS_01");
						String resvID = resv.get("reservationId");

						if (!resvID.equals("error")) {

							WSClient.setData("{var_resvId}", resvID);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");

							// ******OWS Fetch Booking*******//

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							String fetchBookingReq = WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);


							if (WSAssert.assertIfElementExists(fetchBookingRes,
									"FetchBookingResponse_Result_resultStatusFlag", false)) {
								if (WSAssert.assertIfElementValueEquals(fetchBookingRes,
										"FetchBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									// DB Validation
									LinkedHashMap<String,String> rateDetExp=WSClient.getDBRow(WSClient.getQuery("QS_40"));
									LinkedHashMap<String,String> rateDetAct=new LinkedHashMap<String,String>();
									rateDetAct.put("RATE_CODE", WSClient.getElementValue(fetchBookingRes,"RoomStay_RoomRates_RoomRate_ratePlanCode",XMLType.RESPONSE));
									rateDetAct.put("DESCRIPTION", WSClient.getElementValue(fetchBookingRes,"RatePlan_RatePlanDescription_Text",XMLType.RESPONSE));
									WSAssert.assertEquals(rateDetExp, rateDetAct, false);
									List<LinkedHashMap<String,String>> resValues=new ArrayList<LinkedHashMap<String,String>>();
									HashMap<String,String> xPath=new HashMap<String,String>();
									xPath.put("RatePlan_AdditionalDetails_AdditionalDetail_detailType", "RatePlan_AdditionalDetails_AdditionalDetail");
									xPath.put("AdditionalDetail_AdditionalDetailDescription_Text", "RatePlan_AdditionalDetails_AdditionalDetail");
									resValues=WSClient.getMultipleNodeList(fetchBookingRes, xPath, true, XMLType.RESPONSE);
									String cancelDate=WSClient.getElementValue(fetchBookingRes, "RatePlans_RatePlan_CancellationDateTime", XMLType.RESPONSE);
									String depositAmount=WSClient.getElementValue(fetchBookingRes, "RatePlan_DepositRequired_DepositAmount", XMLType.RESPONSE);
									String dueDate=WSClient.getElementValue(fetchBookingRes, "RatePlan_DepositRequired_DueDate", XMLType.RESPONSE);
									String depositDueAmount=WSClient.getElementValue(fetchBookingRes, "RatePlan_DepositRequired_DepositDueAmount", XMLType.RESPONSE);

									for(int i=0;i<resValues.size();i++){
										if(resValues.get(i).get("DetailType").equals("CancelPolicy")){
											resValues.get(i).put("CancellationDate", cancelDate.substring(0,cancelDate.indexOf('T')));
											resValues.get(i).put("CancellationTime", cancelDate.substring(cancelDate.indexOf('T')+1,cancelDate.length()));
										}
										if(resValues.get(i).get("DetailType").equals("DepositPolicy")){
											resValues.get(i).put("DepositAmount", depositAmount);
											resValues.get(i).put("DepositDueAmount", depositDueAmount);
											resValues.get(i).put("DueDate", dueDate);
										}
									}

									LinkedHashMap<String,String> depositValues=WSClient.getDBRow(WSClient.getQuery("QS_39"));
									LinkedHashMap<String,String> cancelValues=WSClient.getDBRow(WSClient.getQuery("QS_38"));
									depositValues.put("DepositDueAmount",WSClient.getDBRow(WSClient.getQuery("QS_41")).get("ROOM_DEPOSIT"));

									if(cancelValues.size()>0){
										cancelValues.put("DetailType", "CancelPolicy");
									}
									if(cancelValues.containsKey("Text")){
										cancelValues.put("Text", "Cancel By "+cancelValues.get("Text"));
									}
									if(depositValues.size()>0){
										depositValues.put("DetailType", "DepositPolicy");
									}
									if(depositValues.containsKey("DepositAmount")&&depositValues.containsKey("Text")){
										depositValues.put("Text", "A deposit of "+depositValues.get("DepositAmount")+".00 is due by "+depositValues.get("Text")+" in order to guarantee your reservation.");
									}

									for(int i=0;i<resValues.size();i++){
										if(resValues.get(i).get("DetailType").equals("CancelPolicy")){
											WSClient.writeToReport(LogStatus.INFO, "<b>Validating Cancellation Policy</b>");
											WSAssert.assertEquals(cancelValues, resValues.get(i), false);
										}
										if(resValues.get(i).get("DetailType").equals("DepositPolicy")){
											WSClient.writeToReport(LogStatus.INFO, "<b>Validating Deposit Policy</b>");
											WSAssert.assertEquals(depositValues, resValues.get(i), false);
										}
									}

								}
							}
							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
								if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

									String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
								}
							}

							if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

								String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"<b>The text displayed in the response is :" + message+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

								String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
							}

							if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

								String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
							}


						}
					}

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// Cancel Reservation
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchBooking", "Reservation", "OWS"})

	public void fetchBooking_41904() {
		try {
			String testName = "fetchBooking_41904";
			WSClient.startTest(testName, "Verify that cancellation and deposit policy attached to reservation type are present in the response", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode","ReservationType"}))
			{
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

				/***** check fetch_reservation parameter will be ALL *****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				if (!Parameter.equals("error")) {


					/************ Prerequisite 1: Create profile *********************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:"+ profileID +"</b>");
						WSClient.setData("{var_profileId}", profileID);

						/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
						WSClient.setData("{VAR_RATEPLANCODE}",OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
						WSClient.setData("{var_guaranteeCode}", OperaPropConfig.getDataSetForCode("ReservationType", "DS_04"));

						WSClient.setData("{var_ReservationType}", OperaPropConfig.getDataSetForCode("ReservationType", "DS_04"));

						/**************** Prerequisite 2:Create a Reservation *****************/

						HashMap<String,String> resvID1=CreateReservation.createReservation("DS_20");
						if(resvID1.get("reservationId")!="error")
						{
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:"+ resvID1.get("reservationId") +"</b>");
							WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							/***************   OWS Fetch Booking Operation  ********************/
							String FetchBookingReq = WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String FetchBookingRes = WSClient.processSOAPMessage(FetchBookingReq);

							if(WSAssert.assertIfElementExists(FetchBookingRes, "FetchBookingResponse_Result", true))
							{
								if (WSAssert.assertIfElementValueEquals(FetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
								{


									List<LinkedHashMap<String,String>> resValues=new ArrayList<LinkedHashMap<String,String>>();
									HashMap<String,String> xPath=new HashMap<String,String>();
									xPath.put("RatePlan_AdditionalDetails_AdditionalDetail_detailType", "RatePlan_AdditionalDetails_AdditionalDetail");
									xPath.put("AdditionalDetail_AdditionalDetailDescription_Text", "RatePlan_AdditionalDetails_AdditionalDetail");
									resValues=WSClient.getMultipleNodeList(FetchBookingRes, xPath, true, XMLType.RESPONSE);
									String cancelDate=WSClient.getElementValue(FetchBookingRes, "RatePlans_RatePlan_CancellationDateTime", XMLType.RESPONSE);
									String depositAmount=WSClient.getElementValue(FetchBookingRes, "RatePlan_DepositRequired_DepositAmount", XMLType.RESPONSE);
									String dueDate=WSClient.getElementValue(FetchBookingRes, "RatePlan_DepositRequired_DueDate", XMLType.RESPONSE);
									for(int i=0;i<resValues.size();i++){
										if(resValues.get(i).get("DetailType").equals("CancelPolicy"))
										{
											resValues.get(i).put("CancelDate", cancelDate.substring(0,cancelDate.indexOf('T')));
										}
										if(resValues.get(i).get("DetailType").equals("DepositPolicy"))
										{
											resValues.get(i).put("DepositAmount", depositAmount);
											resValues.get(i).put("DueDate", dueDate);
										}
									}

									LinkedHashMap<String,String> depositValues=WSClient.getDBRow(WSClient.getQuery("QS_44"));
									LinkedHashMap<String,String> cancelValues=WSClient.getDBRow(WSClient.getQuery("QS_43"));
									if(cancelValues.size()>0)
									{
										cancelValues.put("DetailType", "CancelPolicy");
									}
									if(cancelValues.containsKey("Text"))
									{
										cancelValues.put("Text", "Cancel By "+cancelValues.get("Text"));
									}
									if(depositValues.size()>0){
										depositValues.put("DetailType", "DepositPolicy");
									}
									if(depositValues.containsKey("DepositAmount")&&depositValues.containsKey("Text"))
									{
										depositValues.put("Text", "A deposit of "+depositValues.get("DepositAmount")+".00 is due by "+depositValues.get("Text")+" in order to guarantee your reservation.");
									}
									for(int i=0;i<resValues.size();i++){
										if(resValues.get(i).get("DetailType").equals("CancelPolicy"))
										{
											WSClient.writeToReport(LogStatus.INFO, "<b>Validating Cancellation Policy</b>");
											WSAssert.assertEquals(cancelValues, resValues.get(i), false);
										}
										if(resValues.get(i).get("DetailType").equals("DepositPolicy"))
										{
											WSClient.writeToReport(LogStatus.INFO, "<b>Validating Deposit Policy</b>");
											WSAssert.assertEquals(depositValues, resValues.get(i), false);
										}
									}
								}
								if(WSAssert.assertIfElementExists(FetchBookingRes, "FetchBookingResponse_Result_GDSError", true))
								{
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "+WSClient.getElementValue(FetchBookingRes, "FetchBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)+"</b>");
								}
								if(WSAssert.assertIfElementExists(FetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", true))
								{
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "+WSClient.getElementValue(FetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
								}
								if (WSAssert.assertIfElementExists(FetchBookingRes, "Result_Text_TextElement", true))
								{
									/**** Verifying that the error message is populated on the response *****/

									String message = WSAssert.getElementValue(FetchBookingRes, "Result_Text_TextElement",XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,"<b>The text displayed in the response is :" + message+"</b>");
								}
							}
							else if(WSAssert.assertIfElementExists(FetchBookingRes, "FetchBookingResponse_faultstring", true))
							{
								WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(FetchBookingRes, "FetchBookingResponse_faultstring", XMLType.RESPONSE)+"</b>");
							}
						}
					}

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
//	@Test(groups = { "minimumRegression", "FetchBooking", "Reservation", "OWS", "fetchBooking_41904A"})

	public void fetchBooking_41904A() {
		try {
			String testName = "fetchBooking_41904A";
			WSClient.startTest(testName, "Verify that cancellation and deposit policy attached to reservation type are present in the response", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode","ReservationType"}))
			{
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

				/***** check fetch_reservation parameter will be ALL *****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}

				if (!Parameter.equals("error")) {


					/************ Prerequisite 1: Create profile *********************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:"+ profileID +"</b>");
						WSClient.setData("{var_profileId}", profileID);

						/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
						WSClient.setData("{VAR_RATEPLANCODE}",OperaPropConfig.getDataSetForCode("RateCode", "DS_05"));
						WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
						WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
						WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
						WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
						WSClient.setData("{var_guaranteeCode}", OperaPropConfig.getDataSetForCode("ReservationType", "DS_04"));

						WSClient.setData("{var_ReservationType}", OperaPropConfig.getDataSetForCode("ReservationType", "DS_04"));
						
						String varCancelDate=WSClient.getDBRow(WSClient.getQuery("OWSFetchBooking","QS_45")).get("END_DATE");
						
						WSClient.setData("{var_RoomStay_TimeSpan_EndtDate}", varCancelDate);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						Calendar c;
						c = Calendar.getInstance();
						c.setTime(sdf.parse(varCancelDate));
						c.add(Calendar.DATE, 6);
						WSClient.setData("{var_RoomStay_TimeSpan_EndtDate}", sdf.format(c.getTime()));
						c = Calendar.getInstance();
						c.setTime(sdf.parse(varCancelDate));
						c.add(Calendar.DATE, 1);
						WSClient.setData("{var_RoomStay_TimeSpan_StartDate}", sdf.format(c.getTime()));

						/**************** Prerequisite 2:Create a Reservation *****************/

						HashMap<String,String> resvID1=CreateReservation.createReservation("DS_37");
						if(resvID1.get("reservationId")!="error")
						{
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:"+ resvID1.get("reservationId") +"</b>");
							WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

							/***************   OWS Fetch Booking Operation  ********************/
							String FetchBookingReq = WSClient.createSOAPMessage("OWSFetchBooking", "DS_01");
							String FetchBookingRes = WSClient.processSOAPMessage(FetchBookingReq);

							if(WSAssert.assertIfElementExists(FetchBookingRes, "FetchBookingResponse_Result", true))
							{
								if (WSAssert.assertIfElementValueEquals(FetchBookingRes, "FetchBookingResponse_Result_resultStatusFlag","SUCCESS", false))
								{


									List<LinkedHashMap<String,String>> resValues=new ArrayList<LinkedHashMap<String,String>>();
									HashMap<String,String> xPath=new HashMap<String,String>();
									xPath.put("RatePlan_AdditionalDetails_AdditionalDetail_detailType", "RatePlan_AdditionalDetails_AdditionalDetail");
									xPath.put("AdditionalDetail_AdditionalDetailDescription_Text", "RatePlan_AdditionalDetails_AdditionalDetail");
									resValues=WSClient.getMultipleNodeList(FetchBookingRes, xPath, true, XMLType.RESPONSE);
									String cancelDate=WSClient.getElementValue(FetchBookingRes, "RatePlans_RatePlan_CancellationDateTime", XMLType.RESPONSE);
									String depositAmount=WSClient.getElementValue(FetchBookingRes, "RatePlan_DepositRequired_DepositAmount", XMLType.RESPONSE);
									String dueDate=WSClient.getElementValue(FetchBookingRes, "RatePlan_DepositRequired_DueDate", XMLType.RESPONSE);
									for(int i=0;i<resValues.size();i++){
										if(resValues.get(i).get("DetailType").equals("CancelPolicy"))
										{
											resValues.get(i).put("CancelDate", cancelDate.substring(0,cancelDate.indexOf('T')));
										}
										if(resValues.get(i).get("DetailType").equals("DepositPolicy"))
										{
											resValues.get(i).put("DepositAmount", depositAmount);
											resValues.get(i).put("DueDate", dueDate);
										}
									}

									LinkedHashMap<String,String> depositValues=WSClient.getDBRow(WSClient.getQuery("QS_44"));
									LinkedHashMap<String,String> cancelValues=WSClient.getDBRow(WSClient.getQuery("QS_43"));
									if(cancelValues.size()>0)
									{
										cancelValues.put("DetailType", "CancelPolicy");
									}
									if(cancelValues.containsKey("Text"))
									{
										cancelValues.put("Text", "Cancel By "+cancelValues.get("Text"));
									}
									if(depositValues.size()>0){
										depositValues.put("DetailType", "DepositPolicy");
									}
									if(depositValues.containsKey("DepositAmount")&&depositValues.containsKey("Text"))
									{
										depositValues.put("Text", "A deposit of "+depositValues.get("DepositAmount")+".00 is due by "+depositValues.get("Text")+" in order to guarantee your reservation.");
									}
									for(int i=0;i<resValues.size();i++){
										if(resValues.get(i).get("DetailType").equals("CancelPolicy"))
										{
											WSClient.writeToReport(LogStatus.INFO, "<b>Validating Cancellation Policy</b>");
											WSAssert.assertEquals(cancelValues, resValues.get(i), false);
										}
										if(resValues.get(i).get("DetailType").equals("DepositPolicy"))
										{
											WSClient.writeToReport(LogStatus.INFO, "<b>Validating Deposit Policy</b>");
											WSAssert.assertEquals(depositValues, resValues.get(i), false);
										}
									}
								}
								if(WSAssert.assertIfElementExists(FetchBookingRes, "FetchBookingResponse_Result_GDSError", true))
								{
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "+WSClient.getElementValue(FetchBookingRes, "FetchBookingResponse_Result_GDSError_errorCode", XMLType.RESPONSE)+"</b>");
								}
								if(WSAssert.assertIfElementExists(FetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", true))
								{
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "+WSClient.getElementValue(FetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
								}
								if (WSAssert.assertIfElementExists(FetchBookingRes, "Result_Text_TextElement", true))
								{
									/**** Verifying that the error message is populated on the response *****/

									String message = WSAssert.getElementValue(FetchBookingRes, "Result_Text_TextElement",XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO,"<b>The text displayed in the response is :" + message+"</b>");
								}
							}
							else if(WSAssert.assertIfElementExists(FetchBookingRes, "FetchBookingResponse_faultstring", true))
							{
								WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(FetchBookingRes, "FetchBookingResponse_faultstring", XMLType.RESPONSE)+"</b>");
							}
						}
					}

				}
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})

	public void fetchBooking_42224() {
		try {

			String testName = "fetchBooking_42224";
			WSClient.startTest(testName,
					"verify that error message is coming when invalid reservation id is passed in the request.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);

				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				WSClient.setData("{var_channel}", channel);
				String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);


				WSClient.setData("{var_resort}", resortOperaValue);
				OPERALib.setOperaHeader(uname);

				/***** check fetch_reservation parameter will be ALL *****/
				WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
				WSClient.setData("{var_settingValue}", "ALL");
				WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
				String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
				if (!Parameter.equals("ALL")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
					Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
				}
				// Prerequisite 1 - create profile
				if (!Parameter.equals("error")) {


					// ******OWS Fetch Booking*******//

					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					String fetchBookingReq = WSClient.createSOAPMessage("OWSFetchBooking", "DS_04");
					String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);


					if (WSAssert.assertIfElementExists(fetchBookingRes,
							"FetchBookingResponse_Result_resultStatusFlag", false)) {
						if (WSAssert.assertIfElementValueEquals(fetchBookingRes,
								"FetchBookingResponse_Result_resultStatusFlag", "FAIL", false)) {

						}
					}
					if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
						if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){

							String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
						}
					}

					if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {

						String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
								XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,
								"<b>The text displayed in the response is :" + message+"</b>");
					}

					if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){

						String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
					}

					if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){

						String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
					}


				}
			}



		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}


	//@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})
	//
	//public void fetchBooking_10886() {
	//	try {
	//
	//		String testName = "fetchBooking_10886";
	//		WSClient.startTest(testName,
	//				"verify that error message is coming when invalid confirmation no is passed in the request.",
	//				"minimumRegression");
	//		if (OperaPropConfig.getPropertyConfigResults(
	//				new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
	//
	//			String resortOperaValue = OPERALib.getResort();
	//			String chain = OPERALib.getChain();
	//			WSClient.setData("{var_chain}", chain);
	//			WSClient.setData("{var_resort}", resortOperaValue);
	//
	//			String uname = OPERALib.getUserName();
	//			String pwd = OPERALib.getPassword();
	//			String channel = OWSLib.getChannel();
	//			WSClient.setData("{var_channel}", channel);
	//			String channelType = OWSLib.getChannelType(channel);
	//			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	//			WSClient.setData("{var_owsresort}", resort);
	//			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
	//
	//
	//			WSClient.setData("{var_resort}", resortOperaValue);
	//			OPERALib.setOperaHeader(uname);
	//
	//			/***** check fetch_reservation parameter will be ALL *****/
	//			WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
	//			WSClient.setData("{var_settingValue}", "ALL");
	//			WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
	//			String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
	//			if (!Parameter.equals("ALL")) {
	//				WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
	//				Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
	//			}
	//			// Prerequisite 1 - create profile
	//			if (!Parameter.equals("error")) {
	//                     WSClient.setData("{var_confNo}","94893024527624984");
	//
	//						// ******OWS Fetch Booking*******//
	//
	//						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
	//						String fetchBookingReq = WSClient.createSOAPMessage("OWSFetchBooking", "DS_02");
	//						String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);
	//
	//
	//						if (WSAssert.assertIfElementExists(fetchBookingRes,
	//								"FetchBookingResponse_Result_resultStatusFlag", false)) {
	//							if (WSAssert.assertIfElementValueEquals(fetchBookingRes,
	//									"FetchBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
	//
	//							}
	//						}
	//						 if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
	//							 if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){
	//
	//								String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
	//									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
	//							 }
	//						}
	//
	//						if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {
	//
	//							String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
	//									XMLType.RESPONSE);
	//							WSClient.writeToReport(LogStatus.INFO,
	//									"<b>The text displayed in the response is :" + message+"</b>");
	//						}
	//
	//						if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){
	//
	//							 String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	//			                 WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
	//			            }
	//
	//			            if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){
	//
	//			            	   String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
	//			                   WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
	//			               }
	//
	//
	//					}
	//				}
	//
	//
	//
	//	} catch (Exception e) {
	//		WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	//	}
	//}
	//
	//@Test(groups = { "minimumRegression", "FetchBooking", "OWS" , "Reservation"})
	//
	//public void fetchBooking_10884() {
	//	try {
	//
	//		String testName = "fetchBooking_10884";
	//		WSClient.startTest(testName,
	//				"verify that error message is coming when invalid property is passed in the request.",
	//				"minimumRegression");
	//		if (OperaPropConfig.getPropertyConfigResults(
	//				new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
	//
	//			String resortOperaValue = OPERALib.getResort();
	//			String chain = OPERALib.getChain();
	//			WSClient.setData("{var_chain}", chain);
	//			WSClient.setData("{var_resort}", resortOperaValue);
	//
	//			String uname = OPERALib.getUserName();
	//			String pwd = OPERALib.getPassword();
	//			String channel = OWSLib.getChannel();
	//			WSClient.setData("{var_channel}", channel);
	//			String channelType = OWSLib.getChannelType(channel);
	//			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
	//			WSClient.setData("{var_owsresort}", resort);
	//			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
	//
	//
	//			WSClient.setData("{var_resort}", resortOperaValue);
	//			OPERALib.setOperaHeader(uname);
	//
	//			/***** check fetch_reservation parameter will be ALL *****/
	//			WSClient.setData("{var_parameter}", "FETCH_RESERVATION");
	//			WSClient.setData("{var_settingValue}", "ALL");
	//			WSClient.writeToReport(LogStatus.INFO, "<b>Checking if FETCH_RESERVATION=ALL</b>");
	//			String Parameter = FetchApplicationParameters.getApplicationParameter("DS_01");
	//			if (!Parameter.equals("ALL")) {
	//				WSClient.writeToReport(LogStatus.INFO, "<b>Changing FETCH_RESERVATION to ALL</b>");
	//				Parameter=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
	//			}
	//			// Prerequisite 1 - create profile
	//			if (!Parameter.equals("error")) {
	//				if (profileID.equals(""))
	//					profileID = CreateProfile.createProfile("DS_01");
	//				if (!profileID.equals("error")) {
	//					WSClient.setData("{var_profileId}", profileID);
	//					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
	//
	//					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	//					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	//					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	//					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	//					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
	//
	//					// Prerequisite 2 Create Reservation
	//					HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
	//					String resvID = resv.get("reservationId");
	//
	//					if (!resvID.equals("error")) {
	//
	//						WSClient.setData("{var_resvId}", resvID);
	//						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : "+resvID+"</b>");
	//
	//
	//						// ******OWS Fetch Booking*******//
	//
	//						OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
	//						String fetchBookingReq = WSClient.createSOAPMessage("OWSFetchBooking", "DS_05");
	//						String fetchBookingRes = WSClient.processSOAPMessage(fetchBookingReq);
	//
	//
	//						if (WSAssert.assertIfElementExists(fetchBookingRes,
	//								"FetchBookingResponse_Result_resultStatusFlag", false)) {
	//							if (WSAssert.assertIfElementValueEquals(fetchBookingRes,
	//									"FetchBookingResponse_Result_resultStatusFlag", "FAIL", false)) {
	//
	//							}
	//						}
	//						 if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultcode", true)){
	//							 if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_faultstring", true)){
	//
	//								String message=WSClient.getElementValue(fetchBookingRes,"FetchBookingResponse_faultstring", XMLType.RESPONSE);
	//									WSClient.writeToReport(LogStatus.FAIL, "<b>"+message+"</b>");
	//							 }
	//						}
	//
	//						if (WSAssert.assertIfElementExists(fetchBookingRes, "Result_Text_TextElement", true)) {
	//
	//							String message = WSClient.getElementValue(fetchBookingRes, "Result_Text_TextElement",
	//									XMLType.RESPONSE);
	//							WSClient.writeToReport(LogStatus.INFO,
	//									"<b>The text displayed in the response is :" + message+"</b>");
	//						}
	//
	//						if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_OperaErrorCode", true)){
	//
	//							 String code=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	//			                 WSClient.writeToReport(LogStatus.INFO, "<b>The error code displayed in the response is :"+ code+"</b>");
	//			            }
	//
	//			            if(WSAssert.assertIfElementExists(fetchBookingRes,"FetchBookingResponse_Result_GDSError",true)){
	//
	//			            	   String message=WSClient.getElementValue(fetchBookingRes, "FetchBookingResponse_Result_GDSError", XMLType.RESPONSE);
	//			                   WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
	//			               }
	//
	//
	//					}
	//				}
	//
	//			}
	//		}
	//
	//	} catch (Exception e) {
	//		WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
	//	}
	//}
}
