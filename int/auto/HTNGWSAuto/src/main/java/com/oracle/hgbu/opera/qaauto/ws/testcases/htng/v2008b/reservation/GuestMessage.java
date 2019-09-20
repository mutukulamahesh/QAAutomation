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

public class GuestMessage extends WSSetUp {

	/**
	 * @author nilsaini Description: Verify if the message is submitted to the
	 *         reservation sent on the request
	 */

	@Test(groups = { "sanity", "GuestMessage", "HTNG2008B", "HTNG" })
	public void guestMessage_2008_413() {
		try {
			String testName = "guestMessage_2008_413";
			WSClient.startTest(testName, "Verify that the message is submitted to the reservation sent on the request",
					"sanity");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);


				OPERALib.setOperaHeader(uname);
				String operaProfileID=CreateProfile.createProfile("DS_01");

				if(!operaProfileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
					WSClient.setData("{var_profileId}", operaProfileID);
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> resv=CreateReservation.createReservation("DS_01");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					if(!reservationId.equals("error")) {
						WSClient.setData("{var_resvId}", reservationId);
						WSClient.setData("{var_confirmationId}", confirmationId);
						WSClient.setData("{var_extResort}", resortExtValue);

						// HTNG Guest Mesage

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String guestMessageReq = WSClient.createSOAPMessage("HTNG2008BGuestMessage", "DS_01");
						String guestMessageResponse = WSClient.processSOAPMessage(guestMessageReq);

						if (WSAssert.assertIfElementExists(guestMessageResponse, "GuestMessageResponse_Result",
								false)) {
							if (WSAssert.assertIfElementValueEquals(guestMessageResponse,
									"GuestMessageResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								LinkedHashMap<String, String> dbValue = new LinkedHashMap<String, String>();

								String query=WSClient.getQuery("QS_01");
								dbValue = WSClient.getDBRow(query);
								/*
									HashMap<String, String> res_values = new HashMap<>();
									res_values.put("GuestMessageRequest_ReservationID", "GuestMessageRequest_ReservationID");
									res_values.put("GuestMessageRequest_Message", "GuestMessageRequest_Message");
								 */
								LinkedHashMap<String, String> resResult = new LinkedHashMap<String, String>();
								resResult.put("ReservationID1", WSClient.getElementValue(guestMessageReq, "GuestMessageRequest_ReservationID", XMLType.REQUEST));
								resResult.put("Message1", WSClient.getElementValue(guestMessageReq, "GuestMessageRequest_Message", XMLType.REQUEST));


								WSAssert.assertEquals(resResult, dbValue, false);
							}
						}
						if (WSAssert.assertIfElementExists(guestMessageResponse, "Result_Text_TextElement", true)) {
							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(guestMessageResponse, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"---- Prerequisite createReservation Blocked ----\nReservation is not created");
					}

				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
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

	/**
	 * @author nilsaini Description: Provide valid ReservationID, From, Contact
	 *         & Message; Verify a success response is generated on the
	 *         GuestMessageResponse.
	 */

	@Test(groups = { "minimumRegression", "GuestMessage", "HTNG2008B", "HTNG" })
	public void guestMessage_2008_411() {
		try {
			String testName = "guestMessage_2008_411";
			WSClient.startTest(testName,
					"Provide valid ReservationID, From, Contact & Message; Verify a success response is generated on the GuestMessageResponse.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(uname);
				String operaProfileID=CreateProfile.createProfile("DS_01");

				if(!operaProfileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
					WSClient.setData("{var_profileId}", operaProfileID);

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}",OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}",OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					HashMap<String,String> resv=CreateReservation.createReservation("DS_01");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					if(!reservationId.equals("error")) {
						WSClient.setData("{var_resvId}", reservationId);
						WSClient.setData("{var_confirmationId}", confirmationId);
						WSClient.setData("{var_extResort}", resortExtValue);

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String guestMessageReq = WSClient.createSOAPMessage("HTNG2008BGuestMessage",
								"DS_02");
						String guestMessageResponse = WSClient.processSOAPMessage(guestMessageReq);

						if (WSAssert.assertIfElementExists(guestMessageResponse,
								"GuestMessageResponse_Result", false)) {
							if (WSAssert.assertIfElementValueEquals(guestMessageResponse,
									"GuestMessageResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								LinkedHashMap<String, String> dbValues = new LinkedHashMap<String, String>();
								LinkedHashMap<String,String> dbValues2 = new LinkedHashMap<String, String>();

								String query = WSClient.getQuery("QS_02");
								dbValues = WSClient.getDBRow(query);

								String query2 = WSClient.getQuery("CreateProfile", "QS_14");
								dbValues2 = WSClient.getDBRow(query2);

								LinkedHashMap<String, String> resResult = new LinkedHashMap<String, String>();
								resResult.put("ReservationID1", WSClient.getElementValue(guestMessageReq, "GuestMessageRequest_ReservationID", XMLType.REQUEST));
								resResult.put("Message1", WSClient.getElementValue(guestMessageReq, "GuestMessageRequest_Message", XMLType.REQUEST));
								resResult.put("From1", WSClient.getElementValue(guestMessageReq, "GuestMessageRequest_From", XMLType.REQUEST));
								resResult.put("Contact1", dbValues2.get("CONTACT"));

								WSAssert.assertEquals(resResult, dbValues, false);
							}
						}
						if (WSAssert.assertIfElementExists(guestMessageResponse, "Result_Text_TextElement", true)) {
							/****
							 * Verifying that the error message is populated on the
							 * response
							 ********/

							String message = WSAssert.getElementValue(guestMessageResponse, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is :" + message);
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"---- Prerequisite createReservation Blocked ----\nReservation is not created");
					}




				}
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
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

	/**
	 * @author nilsaini Description: Provide ReservationID & Message text of
	 *         length 2000 characters; A success response should be generated
	 *         and message text should be posted to the guest reservation.
	 */

	@Test(groups = { "minimumRegression", "GuestMessage", "HTNG2008B", "HTNG" })
	public void guestMessage_2008_421() {
		try {
			String testName = "guestMessage_2008_421";
			WSClient.startTest(testName,
					"Provide ReservationID & Message text of length 2000 characters; A success response should be generated and message text should be posted to the guest reservation.",
					"minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress=HTNGLib.getInterfaceFromAddress();

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(uname);
				String operaProfileID=CreateProfile.createProfile("DS_01");

				if(!operaProfileID.equals("error")) {
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
					WSClient.setData("{var_profileId}", operaProfileID);

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}",OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_sourceCode}",OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));

					HashMap<String,String> resv=CreateReservation.createReservation("DS_01");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					if(!reservationId.equals("error")) {
						WSClient.setData("{var_resvId}", reservationId);
						WSClient.setData("{var_confirmationId}", confirmationId);
						WSClient.setData("{var_extResort}", resortExtValue);

						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String guestMessageReq = WSClient.createSOAPMessage("HTNG2008BGuestMessage",
								"DS_03");
						String guestMessageResponse = WSClient.processSOAPMessage(guestMessageReq);

						if (WSAssert.assertIfElementExists(guestMessageResponse,
								"GuestMessageResponse_Result", true)) {
							if (WSAssert.assertIfElementValueEquals(guestMessageResponse,
									"GuestMessageResponse_Result_resultStatusFlag", "SUCCESS", true)) {
								List<LinkedHashMap<String, String>> dbValue = new ArrayList<LinkedHashMap<String, String>>();
								String query=WSClient.getQuery("QS_03");
								dbValue = WSClient.getDBRows(query);
								/*
								HashMap<String, String> res_values = new HashMap<>();
								res_values.put("GuestMessageRequest_ReservationID", "GuestMessageRequest_ReservationID");
								res_values.put("GuestMessageRequest_Message", "GuestMessageRequest_Message");
								 */
								LinkedHashMap<String, String> resResult = new LinkedHashMap<String, String>();
								resResult.put("<b>ReservationID1</b>", WSClient.getElementValue(guestMessageReq, "GuestMessageRequest_ReservationID", XMLType.REQUEST));
								resResult.put("<b>Message1</b>", WSClient.getElementValue(guestMessageReq, "GuestMessageRequest_Message", XMLType.REQUEST));
								List<LinkedHashMap<String, String>> resValue = new ArrayList<LinkedHashMap<String, String>>();
								resValue.add(resResult);

								WSAssert.assertEquals(resValue, dbValue, false);
							}
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"---- Prerequisite createReservation Blocked ----\nReservation is not created");
					}
				}

			}else{
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
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

	/**
	 * @author heegupta
	 * Description: Verifying the GuestMessageReponse when reservationID, From & Contact details are provided in the GuestMessageRequest.
	 *
	 */
	@Test(groups = { "fullRegression", "GuestMessage", "HTNG2008B", "HTNG" })
	public void guestMessage_2008_412()
	{
		try
		{
			String testName = "guestMessage_2008_412";
			WSClient.startTest(testName, "Verifying the GuestMessageReponse when reservationID, From & Contact details are provided in the GuestMessageRequest", "fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" }))
			{
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);


				OPERALib.setOperaHeader(uname);
				String operaProfileID=CreateProfile.createProfile("DS_01");

				if(!operaProfileID.equals("error"))
				{
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
					WSClient.setData("{var_profileId}", operaProfileID);
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> resv=CreateReservation.createReservation("DS_01");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					if(!reservationId.equals("error"))
					{
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
						WSClient.setData("{var_resvId}", reservationId);
						WSClient.setData("{var_confirmationId}", confirmationId);
						WSClient.setData("{var_extResort}", resortExtValue);

						// HTNG Guest Message
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String guestMessageReq = WSClient.createSOAPMessage("HTNG2008BGuestMessage", "DS_04");
						String guestMessageResponse = WSClient.processSOAPMessage(guestMessageReq);

						if (WSAssert.assertIfElementExists(guestMessageResponse, "GuestMessageResponse_Result", false))
						{
							if (WSAssert.assertIfElementValueEquals(guestMessageResponse, "GuestMessageResponse_Result_resultStatusFlag", "FAIL", false))
							{
								WSClient.writeToReport(LogStatus.PASS, "Test Case failed as expected!");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.FAIL, "Test Case does not failed!");
						}

						if (WSAssert.assertIfElementExists(guestMessageResponse, "Result_Text_TextElement", true))
						{

							String message = WSAssert.getElementValue(guestMessageResponse, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,"<b>" + "The text displayed in the response is :" + message + "</b>");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING, "---- Prerequisite createReservation Blocked ----Reservation is not created");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "---- Prerequisite createProfile Blocked ----Profile is not created");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING, "---- Prerequisite for \"RateCode\", \"RoomType\", \"SourceCode\", \"MarketCode\" blocked");
			}
		}
		catch (Exception e)
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

	/**
	 * @author heegupta
	 * Description: Verifying the GuestMessageResopnse when From, Contact & Message details are provided in the GuestMessageRequest.
	 */
	@Test(groups = { "fullRegression", "GuestMessage", "HTNG2008B", "HTNG" })
	public void guestMessage_2008_414()
	{
		try
		{
			String testName = "guestMessage_2008_414";
			WSClient.startTest(testName, "Verify the error message in the repsonse when reservation Id is missed in the request", "fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" }))
			{
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);


				OPERALib.setOperaHeader(uname);
				String operaProfileID=CreateProfile.createProfile("DS_01");

				if(!operaProfileID.equals("error"))
				{
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
					WSClient.setData("{var_profileId}", operaProfileID);
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_extResort}", resortExtValue);

					// HTNG Guest Message
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String guestMessageReq = WSClient.createSOAPMessage("HTNG2008BGuestMessage", "DS_05");
					String guestMessageResponse = WSClient.processSOAPMessage(guestMessageReq);

					if (WSAssert.assertIfElementExists(guestMessageResponse, "GuestMessageResponse_Result", false))
					{
						if (WSAssert.assertIfElementValueEquals(guestMessageResponse, "GuestMessageResponse_Result_resultStatusFlag", "FAIL", false))
						{
							WSClient.writeToReport(LogStatus.PASS, "Test Case failed as expected!");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.FAIL, "Test Case does not failed!");
					}

					if (WSAssert.assertIfElementExists(guestMessageResponse, "Result_Text_TextElement", true))
					{

						String message = WSAssert.getElementValue(guestMessageResponse, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,"<b>" + "The text displayed in the response is :" + message + "</b>");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "---- Prerequisite createProfile Blocked ----Profile is not created");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING, "---- Prerequisite for \"RateCode\", \"RoomType\", \"SourceCode\", \"MarketCode\" blocked");
			}
		}
		catch (Exception e)
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

	/**
	 * @author heegupta
	 * Description : Verifying the GuestMessage response for a CANCELLED reservation.
	 */

	@Test(groups = { "fullRegression", "GuestMessage", "HTNG2008B", "HTNG" })
	public void guestMessage_2008_423()
	{
		try
		{
			String testName = "guestMessage_2008_423";
			WSClient.startTest(testName, "Verifying the GuestMessage response for a CANCELLED reservation", "fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" }))
			{
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);


				OPERALib.setOperaHeader(uname);
				String operaProfileID=CreateProfile.createProfile("DS_01");

				if(!operaProfileID.equals("error"))
				{
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
					WSClient.setData("{var_profileId}", operaProfileID);
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> resv=CreateReservation.createReservation("DS_01");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					if(!reservationId.equals("error"))
					{
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
						WSClient.setData("{var_resvId}", reservationId);
						WSClient.setData("{var_confirmationId}", confirmationId);
						WSClient.setData("{var_extResort}", resortExtValue);


						//Canceling the created reservation
						if(!WSClient.getData("{var_resvId}").equals(""))
							CancelReservation.cancelReservation("DS_02");


						// HTNG Guest Message
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String guestMessageReq = WSClient.createSOAPMessage("HTNG2008BGuestMessage", "DS_06");
						String guestMessageResponse = WSClient.processSOAPMessage(guestMessageReq);

						if (WSAssert.assertIfElementExists(guestMessageResponse, "GuestMessageResponse_Result", false))
						{
							if (WSAssert.assertIfElementValueEquals(guestMessageResponse, "GuestMessageResponse_Result_resultStatusFlag", "SUCCESS", false))
							{
								WSClient.writeToReport(LogStatus.PASS, "Test Case Passed as expected!");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.FAIL, "Test Case does not passed!");
						}

						if (WSAssert.assertIfElementExists(guestMessageResponse, "Result_Text_TextElement", true))
						{

							String message = WSAssert.getElementValue(guestMessageResponse, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,"<b>" + "The text displayed in the response is :" + message + "</b>");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING, "---- Prerequisite createReservation Blocked ----Reservation is not created");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "---- Prerequisite createProfile Blocked ----Profile is not created");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING, "---- Prerequisite for \"RateCode\", \"RoomType\", \"SourceCode\", \"MarketCode\" blocked");
			}
		}
		catch (Exception e)
		{
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		finally
		{

		}
	}
	/**
	 * @author heegupta
	 * Description: Verifying the GuestMessageResponse when only reservationID is passed on the GuestMessageRequest.
	 */

	@Test(groups = { "fullRegression", "GuestMessage", "HTNG2008B", "HTNG" })
	public void guestMessage_2008_415()
	{
		try
		{
			String testName = "guestMessage_2008_415";
			WSClient.startTest(testName, "Verifying the GuestMessageResponse when only reservationID is passed on the GuestMessageRequest.", "fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" }))
			{
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);


				OPERALib.setOperaHeader(uname);
				String operaProfileID=CreateProfile.createProfile("DS_01");

				if(!operaProfileID.equals("error"))
				{
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
					WSClient.setData("{var_profileId}", operaProfileID);
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> resv=CreateReservation.createReservation("DS_01");
					String reservationId = resv.get("reservationId");
					String confirmationId = resv.get("confirmationId");
					if(!reservationId.equals("error"))
					{
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationId + "</b>");
						WSClient.setData("{var_resvId}", reservationId);
						WSClient.setData("{var_confirmationId}", confirmationId);
						WSClient.setData("{var_extResort}", resortExtValue);

						// HTNG Guest Message
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String guestMessageReq = WSClient.createSOAPMessage("HTNG2008BGuestMessage", "DS_07");
						String guestMessageResponse = WSClient.processSOAPMessage(guestMessageReq);

						if (WSAssert.assertIfElementExists(guestMessageResponse, "GuestMessageResponse_Result", false))
						{
							if (WSAssert.assertIfElementValueEquals(guestMessageResponse, "GuestMessageResponse_Result_resultStatusFlag", "FAIL", false))
							{
								WSClient.writeToReport(LogStatus.PASS, "Test Case failed as expected!");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.FAIL, "Test Case does not failed!");
						}

						if (WSAssert.assertIfElementExists(guestMessageResponse, "Result_Text_TextElement", true))
						{

							String message = WSAssert.getElementValue(guestMessageResponse, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,"<b>" + "The text displayed in the response is :" + message + "</b>");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING, "---- Prerequisite createReservation Blocked ----Reservation is not created");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "---- Prerequisite createProfile Blocked ----Profile is not created");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING, "---- Prerequisite for \"RateCode\", \"RoomType\", \"SourceCode\", \"MarketCode\" blocked");
			}
		}
		catch (Exception e)
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

	/**
	 *@author heegupta
	 *Description: Verifying the GuestMessageResponse when invalid reservationID, valid From, Contact & Message details are provided in the GuestMessageRequest.
	 */

	@Test(groups = { "fullRegression", "GuestMessage", "HTNG2008B", "HTNG" })
	public void guestMessage_2008_418()
	{
		try
		{
			String testName = "guestMessage_2008_418";
			WSClient.startTest(testName, "Verifying the GuestMessageResponse when invalid reservationID, valid From, Contact & Message details are provided in the GuestMessageRequest", "fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" }))
			{
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);


				OPERALib.setOperaHeader(uname);
				String operaProfileID=CreateProfile.createProfile("DS_01");

				if(!operaProfileID.equals("error"))
				{
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
					WSClient.setData("{var_profileId}", operaProfileID);
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					//Taking an invalid reservation ID and Confirmation ID
					String reservationId = WSClient.getKeywordData("{KEYWORD_RANDNUM_8}");
					String confirmationId = WSClient.getKeywordData("{KEYWORD_RANDNUM_8}");;
					if(!reservationId.equals("error"))
					{
						WSClient.writeToReport(LogStatus.INFO, "<b>Taking some invalid Reservation Id: " + reservationId + "</b>");
						WSClient.setData("{var_resvId}", reservationId);
						WSClient.setData("{var_confirmationId}", confirmationId);
						WSClient.setData("{var_extResort}", resortExtValue);

						// HTNG Guest Message
						HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
						String guestMessageReq = WSClient.createSOAPMessage("HTNG2008BGuestMessage", "DS_02");
						String guestMessageResponse = WSClient.processSOAPMessage(guestMessageReq);

						if (WSAssert.assertIfElementExists(guestMessageResponse, "GuestMessageResponse_Result", false))
						{
							if (WSAssert.assertIfElementValueEquals(guestMessageResponse, "GuestMessageResponse_Result_resultStatusFlag", "FAIL", false))
							{
								WSClient.writeToReport(LogStatus.PASS, "Test Case failed as expected!");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.FAIL, "Test Case does not failed!");
						}

						if (WSAssert.assertIfElementExists(guestMessageResponse, "Result_Text_TextElement", true))
						{

							String message = WSAssert.getElementValue(guestMessageResponse, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,"<b>" + "The text displayed in the response is :" + message + "</b>");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING, "---- Prerequisite createReservation Blocked ----Reservation is not created");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "---- Prerequisite createProfile Blocked ----Profile is not created");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING, "---- Prerequisite for \"RateCode\", \"RoomType\", \"SourceCode\", \"MarketCode\" blocked");
			}
		}
		catch (Exception e)
		{
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		finally
		{

		}
	}

	/**
	 * @author heegupta
	 * Description: Verify GuestMessageResposne when only From details are provided in the GuestMessageRequest.
	 */
	@Test(groups = { "fullRegression", "GuestMessage", "HTNG2008B", "HTNG" })
	public void guestMessage_2008_416()
	{
		try
		{
			String testName = "guestMessage_2008_416";
			WSClient.startTest(testName, "Verify GuestMessageResposne when only From details are provided in the GuestMessageRequest.", "fullRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" }))
			{
				String resortOperaValue = OPERALib.getResort();
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String fromAddress = HTNGLib.getInterfaceFromAddress();
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);


				OPERALib.setOperaHeader(uname);
				String operaProfileID=CreateProfile.createProfile("DS_01");

				if(!operaProfileID.equals("error"))
				{
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaProfileID + "</b>");
					WSClient.setData("{var_profileId}", operaProfileID);
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_extResort}", resortExtValue);

					// HTNG Guest Message
					HTNGLib.setHTNGHeader(uname, pwd, fromAddress);
					String guestMessageReq = WSClient.createSOAPMessage("HTNG2008BGuestMessage", "DS_08");
					String guestMessageResponse = WSClient.processSOAPMessage(guestMessageReq);

					if (WSAssert.assertIfElementExists(guestMessageResponse, "GuestMessageResponse_Result", false))
					{
						if (WSAssert.assertIfElementValueEquals(guestMessageResponse, "GuestMessageResponse_Result_resultStatusFlag", "FAIL", false))
						{
							WSClient.writeToReport(LogStatus.PASS, "Test Case failed as expected!");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.FAIL, "Test Case does not failed!");
					}

					if (WSAssert.assertIfElementExists(guestMessageResponse, "Result_Text_TextElement", true))
					{

						String message = WSAssert.getElementValue(guestMessageResponse, "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO,"<b>" + "The text displayed in the response is :" + message + "</b>");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "---- Prerequisite createProfile Blocked ----Profile is not created");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING, "---- Prerequisite for \"RateCode\", \"RoomType\", \"SourceCode\", \"MarketCode\" blocked");
			}
		}
		catch (Exception e)
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


}
