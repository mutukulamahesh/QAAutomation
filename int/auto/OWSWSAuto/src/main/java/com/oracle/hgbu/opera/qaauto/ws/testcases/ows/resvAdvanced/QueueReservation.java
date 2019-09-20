package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CheckoutReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class QueueReservation extends WSSetUp{

	String profileID="",queueResvParameterEnabled="";
	HashMap<String, String> resvID=new HashMap<>();

	/**
	 * Method to check if the OWS Reservation Advanced Queue Reservation is working i.e.,
	 * pushing a reservation into queue when given the reservationId.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "sanity", "QueueReservation", "ResvAdvanced", "OWS"})

	public void queueReservation_38389() {
		String uname="";
		try {
			String testName = "queueReservation_38389";
			WSClient.startTest(testName, "Verify that pushing a reservation into queue is successful when given reservation id", "sanity");

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

			WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter QUEUE ROOMS is enabled</b>");

			/*********** Prerequisite : Verify if the parameter QUEUE ROOMS is enabled  ****************/
			queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
			if(queueResvParameterEnabled.equals("N"))
			{
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{

					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					/************* Prerequisite 1: Create profile *********************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");

						/******************* Prerequisite 2:Create a Reservation ************************/

						resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							/******************* OWS Queue Reservation Operation************************/
							String queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_01");
							String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

							if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", false))
							{
								WSClient.writeToReport(LogStatus.INFO, "<b>"+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE)+"</b>");
								String priority=WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE);
								priority=priority.substring(priority.indexOf("Queue number is")+15);
								System.out.println(priority);
								WSClient.setData("{var_qPriority}", priority);

								String query=WSClient.getQuery("QS_01");
								HashMap<String, String> dbResults=WSClient.getDBRow(query);

								if(dbResults.get("RESV_NAME_ID") == null)
								{
									WSClient.writeToReport(LogStatus.FAIL, "<b> The Reservation is not pushed into queue.Not inserted into DB</b>");
								}
								else
								{
									String resvID=WSClient.getElementValue(queueResvReq, "ReservationRequest_ReservationID_UniqueID", XMLType.REQUEST);
									if(WSAssert.assertEquals(resvID, dbResults.get("RESV_NAME_ID"), true))
									{
										WSClient.writeToReport(LogStatus.PASS, "<b> ReservationId -> Expected : "+resvID+" Actual : "+dbResults.get("RESV_NAME_ID")+" </b>");
									}
									else
									{
										WSClient.writeToReport(LogStatus.FAIL, "<b> ReservationId -> Expected : "+resvID+" Actual : "+dbResults.get("RESV_NAME_ID")+" </b>");
									}
								}
							}
							else if(WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","FAIL", true))
							{
								WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
							}
							else if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_faultstring", true))
							{
								WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a reservation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a profile failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING,"Prerequisites of reservation creation failed ");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter Queue Rooms failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				//WSClient.writeToReport(LogStatus.INFO, "<b>Executing Cancel Reservation to release room</b>");
				OPERALib.setOperaHeader(uname);
				if(!resvID.get("reservationId").equals("error"))
				{
					WSClient.setData("{var_resvId}", resvID.get("reservationId"));
					if(CancelReservation.cancelReservation("DS_02"))
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
	 * Method to check if the OWS Reservation Advanced Queue Reservation is working i.e.,
	 * pushing a reservation into queue when given the confirmation no.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "QueueReservation", "ResvAdvanced", "OWS"})

	public void queueReservation_38885() {
		String uname="";
		try {
			String testName = "queueReservation_38885";
			WSClient.startTest(testName, "Verify that pushing a reservation into queue is successful when given confirmation no", "minimumRegression");

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

			WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter QUEUE ROOMS is enabled</b>");

			/*********** Prerequisite : Verify if the parameter QUEUE ROOMS is enabled  ****************/
			queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
			if(queueResvParameterEnabled.equals("N"))
			{
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{

					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");

						/******************* Prerequisite 2:Create a Reservation ************************/

						resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_confirmationNo}",resvID.get("confirmationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							/******************* OWS Queue Reservation Operation************************/
							String queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_03");
							String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

							if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", false))
							{
								WSClient.writeToReport(LogStatus.INFO, "<b>"+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE)+"</b>");
								String priority=WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE);
								priority=priority.substring(priority.indexOf("Queue number is")+15);
								System.out.println(priority);
								WSClient.setData("{var_qPriority}", priority);

								String query=WSClient.getQuery("QS_01");
								HashMap<String, String> dbResults=WSClient.getDBRow(query);

								if(dbResults.get("RESV_NAME_ID") == null)
								{
									WSClient.writeToReport(LogStatus.FAIL, "<b> The Reservation is not pushed into queue.Not inserted into DB</b>");
								}
								else
								{
									if(WSAssert.assertEquals(resvID.get("reservationId"), dbResults.get("RESV_NAME_ID"), true))
									{
										WSClient.writeToReport(LogStatus.PASS, "<b> ReservationId -> Expected : "+resvID.get("reservationId")+" Actual : "+dbResults.get("RESV_NAME_ID")+" </b>");
									}
									else
									{
										WSClient.writeToReport(LogStatus.FAIL, "<b> ReservationId -> Expected : "+resvID.get("reservationId")+" Actual : "+dbResults.get("RESV_NAME_ID")+" </b>");
									}
								}
							}
							else if(WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","FAIL", true))
							{
								WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
							}
							else if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_faultstring", true))
							{
								WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a reservation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a profile failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING,"Prerequisites of reservation creation failed ");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter Queue Rooms failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				//WSClient.writeToReport(LogStatus.INFO, "<b>Executing Cancel Reservation to release room</b>");
				OPERALib.setOperaHeader(uname);
				if(!resvID.get("reservationId").equals("error"))
				{
					WSClient.setData("{var_resvId}", resvID.get("reservationId"));
					if(CancelReservation.cancelReservation("DS_02"))
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
	 * Method to check if the OWS Reservation Advanced Queue Reservation is working i.e.,
	 * pushing a reservation into queue when given the reservationId which is already in the queue.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "QueueReservation", "ResvAdvanced", "OWS"})

	public void queueReservation_38400() {
		String uname="";
		try {
			String testName = "queueReservation_38400";
			WSClient.startTest(testName, "Verify that queue reservation is not pushed into queue", "minimumRegression");

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

			WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter QUEUE ROOMS is enabled</b>");

			/*********** Prerequisite : Verify if the parameter QUEUE ROOMS is enabled  ****************/
			queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
			if(queueResvParameterEnabled.equals("N"))
			{
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{
					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));


					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");

						/******************* Prerequisite 2:Create a Reservation ************************/
						resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							String queueResvReq = WSClient.createSOAPMessage("AddReservationToQueue", "DS_01");
							String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

							/**********Prerequisite 3:Add reservation to queue for duplicate request************/
							if (WSAssert.assertIfElementExists(queueResvRes, "AddReservationToQueueRS_Success", true))
							{
								if(WSAssert.assertIfElementExists(queueResvRes, "AddReservationToQueueRS_QueueInfo_Priority", true))
								{
									String priority=WSClient.getElementValue(queueResvRes, "AddReservationToQueueRS_QueueInfo_Priority", XMLType.RESPONSE);
									System.out.println(priority);
									WSClient.setData("{var_qPriority}", priority);

									String query=WSClient.getQuery("QS_01");
									HashMap<String, String> dbResults=WSClient.getDBRow(query);

									if(dbResults.get("RESV_NAME_ID") == null)
									{
										WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite Failed : The Reservation is not pushed into queue.Not inserted into DB</b>");
									}
									else
									{
										String resvID=WSClient.getElementValue(queueResvReq, "AddReservationToQueueRQ_ReservationID_ID", XMLType.REQUEST);
										if(WSAssert.assertEquals(resvID, dbResults.get("RESV_NAME_ID"), true))
										{
											WSClient.writeToReport(LogStatus.PASS, "<b> ReservationId -> Expected : "+resvID+" Actual : "+dbResults.get("RESV_NAME_ID")+" </b>");

											/******************* OWS Queue Reservation Operation************************/
											queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_01");
											queueResvRes = WSClient.processSOAPMessage(queueResvReq);

											if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", false))
											{
												String qMsg=WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "<b>"+qMsg+"</b>");
											}
											else if(WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","FAIL", true))
											{
												WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
											}
											else if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_faultstring", true))
											{
												WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
											}
										}
										else
										{
											WSClient.writeToReport(LogStatus.WARNING, "<b> ReservationId -> Expected : "+resvID+" Actual : "+dbResults.get("RESV_NAME_ID")+" .The requested reservation id did not get inserted into DB</b>");
										}
									}
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite Failed : The Priority for the Reservation in the queue is not generated</b>");
								}
							}
							else if(WSAssert.assertIfElementExists(queueResvRes, "AddReservationToQueueRS_Errors_Error", true))
							{
								WSClient.writeToReport(LogStatus.FAIL, WSClient.getElementValue(queueResvRes, "AddReservationToQueueRS_Errors_Error", XMLType.RESPONSE));
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a reservation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a profile failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING,"Prerequisites of reservation creation failed ");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter Queue Rooms failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				//WSClient.writeToReport(LogStatus.INFO, "<b>Executing Cancel Reservation to release room</b>");
				OPERALib.setOperaHeader(uname);
				if(!resvID.get("reservationId").equals("error"))
				{
					WSClient.setData("{var_resvId}", resvID.get("reservationId"));
					if(CancelReservation.cancelReservation("DS_02"))
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
	 * Method to check if the OWS Reservation Advanced Queue Reservation is working i.e.,
	 * pushing a reservation into queue when given the invalid reservationId and resort id.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "QueueReservation", "ResvAdvanced","OWS"})

	public void queueReservation_38402() {
		String uname = null;
		try {
			String testName = "queueReservation_38402";
			WSClient.startTest(testName, "Verify that pushing a reservation into queue is not successful when reservation id is incorrect", "minimumRegression");

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

			WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter QUEUE ROOMS is enabled</b>");

			/*********** Prerequisite : Verify if the parameter QUEUE ROOMS is enabled  ****************/
			queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
			if(queueResvParameterEnabled.equals("N"))
			{
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{
					OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
					/*** for random reservation id ***/
					WSClient.setData("{var_resvId}", WSClient.getKeywordData("{KEYWORD_RANDNUM_4}"));

					/******************* OWS Queue Reservation Operation************************/
					String queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_01");
					String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

					if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_Result", true))
					{
						if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","FAIL", false))
						{
							if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", true))
							{
								WSClient.writeToReport(LogStatus.INFO, "<b> The error code on response is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
							}
							if(WSAssert.assertIfElementExists(queueResvRes, "Result_Text_TextElement", true))
							{
								if(!WSClient.getElementValue(queueResvRes, "Result_Text_TextElement", XMLType.RESPONSE).equals("*null*"))
									WSClient.writeToReport(LogStatus.INFO, "<b> The error text on response is : "+WSClient.getElementValue(queueResvRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
							}
						}
						else if(WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", true))
						{
							WSClient.writeToReport(LogStatus.FAIL, "<b>Reservation is pushed into Queue when reservation id is invalid </b>");
						}
					}
					else if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_faultstring", true))
					{
						WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
					}
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter Queue Rooms failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				//WSClient.writeToReport(LogStatus.INFO, "<b>Executing Cancel Reservation to release room</b>");
				OPERALib.setOperaHeader(uname);
				if(!resvID.get("reservationId").equals("error"))
				{
					WSClient.setData("{var_resvId}", resvID.get("reservationId"));
					if(CancelReservation.cancelReservation("DS_02"))
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
	 * Method to check if the OWS Reservation Advanced Queue Reservation is working i.e.,
	 * pushing a reservation into queue when given the reservationId and invalid resort id.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "QueueReservation", "ResvAdvanced","OWS"})

	public void queueReservation_38403() {
		try {
			String testName = "queueReservation_38403";
			WSClient.startTest(testName, "Verify that pushing a reservation into queue is not successful when hotel code is incorrect", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			WSClient.setData("{var_owsresort}", "INVALID");
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			/*********** Prerequisite : Verify if the parameter QUEUE ROOMS is enabled  ****************/
			WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter QUEUE ROOMS is enabled</b>");
			queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
			if(queueResvParameterEnabled.equals("N"))
			{
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{

					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));


					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");

						/******************* Prerequisite 2:Create a Reservation ************************/

						if(resvID.equals(""))
							resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							/******************* OWS Queue Reservation Operation************************/
							String queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_01");
							String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

							if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_Result", true))
							{
								if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","FAIL", false))
								{
									if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", true))
									{
										if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", true))
										{
											WSClient.writeToReport(LogStatus.INFO, "<b> The error code on response is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
										}
										if(WSAssert.assertIfElementExists(queueResvRes, "Result_Text_TextElement", true))
										{
											if(!WSClient.getElementValue(queueResvRes, "Result_Text_TextElement", XMLType.RESPONSE).equals("*null*"))
												WSClient.writeToReport(LogStatus.INFO, "<b> The error text on response is : "+WSClient.getElementValue(queueResvRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
										}
									}
								}
								else if(WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", true))
								{
									WSClient.writeToReport(LogStatus.FAIL, "<b>Reservation is pushed into Queue when resort id is invalid </b>");
								}
							}
							else if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_faultstring", true))
							{
								WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a reservation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a profile failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING,"Prerequisites of reservation creation failed ");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter Queue Rooms failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}





	/**
	 * Method to check if the OWS Reservation Advanced Queue Reservation is working i.e.,
	 * delete a reservation from queue when given the reservationId which is in the queue.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * ->Add reservation to queue.
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "QueueReservation", "ResvAdvanced", "OWS"})

	public void queueReservation_38880() {
		String uname=null;
		try {
			String testName = "queueReservation_38880";
			WSClient.startTest(testName, "Verify that queue reservation is removed from the queue when given reservation id", "minimumRegression");

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

			WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter QUEUE ROOMS is enabled</b>");

			/*********** Prerequisite : Verify if the parameter QUEUE ROOMS is enabled  ****************/
			queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
			if(queueResvParameterEnabled.equals("N"))
			{
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{

					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));


					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}",profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");

						/******************* Prerequisite 2:Create a Reservation ************************/
						resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							String queueResvReq = WSClient.createSOAPMessage("AddReservationToQueue", "DS_01");
							String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

							/**********Prerequisite 3:Add reservation to queue for duplicate request************/
							if (WSAssert.assertIfElementExists(queueResvRes, "AddReservationToQueueRS_Success", true))
							{
								if(WSAssert.assertIfElementExists(queueResvRes, "AddReservationToQueueRS_QueueInfo_Priority", true))
								{
									String priority=WSClient.getElementValue(queueResvRes, "AddReservationToQueueRS_QueueInfo_Priority", XMLType.RESPONSE);
									System.out.println(priority);
									WSClient.setData("{var_qPriority}", priority);

									String query=WSClient.getQuery("QS_01");
									HashMap<String, String> dbResults=WSClient.getDBRow(query);

									if(dbResults.get("RESV_NAME_ID") == null)
									{
										WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite Failed : The Reservation is not pushed into queue.Not inserted into DB</b>");
									}
									else
									{
										String resvID=WSClient.getElementValue(queueResvReq, "AddReservationToQueueRQ_ReservationID_ID", XMLType.REQUEST);
										if(WSAssert.assertEquals(resvID, dbResults.get("RESV_NAME_ID"), true))
										{
											WSClient.writeToReport(LogStatus.PASS, "<b> ReservationId -> Expected : "+resvID+" Actual : "+dbResults.get("RESV_NAME_ID")+" </b>");

											/******************* OWS Queue Reservation Operation************************/
											queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_02");
											queueResvRes = WSClient.processSOAPMessage(queueResvReq);

											if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", false))
											{
												WSClient.writeToReport(LogStatus.INFO, "<b>Queue Message is -> "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE)+"</b>");

												String query1=WSClient.getQuery("QS_05");
												HashMap<String, String> results=WSClient.getDBRow(query1);
												String sysdate=results.get("SYSDATE");
												sysdate=sysdate.substring(0,sysdate.indexOf(' '));

												query1=WSClient.getQuery("QS_02");
												results=WSClient.getDBRow(query1);
												resvID=WSClient.getElementValue(queueResvReq, "ReservationRequest_ReservationID_UniqueID", XMLType.REQUEST);

												String queueend=results.get("QUEUE_END");
												queueend=queueend.substring(0,queueend.indexOf(' '));
												if(WSAssert.assertEquals("0",results.get("QUEUE_PRIORITY"),true))
												{
													if(!WSAssert.assertEquals(sysdate, queueend, true))
													{
														WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Queue end should be set to sysdate in DB but set to "+queueend+"</b>");
													}
													else
													{
														WSClient.writeToReport(LogStatus.PASS, "<b>QueuePriority -> Expected : 0  Actual : "+results.get("QUEUE_PRIORITY")+"</b>");
														WSClient.writeToReport(LogStatus.PASS, "<b>QueueEnd -> Expected : "+sysdate+"  Actual : "+queueend+"</b>");
													}
												}
												else
												{
													WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Queue priority should be set to 0 in DB but set to "+results.get("QUEUE_PRIORITY")+"</b>");
												}
											}
											else if(WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","FAIL", true))
											{
												WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
											}
											else if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_faultstring", true))
											{
												WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
											}
										}
										else
										{
											WSClient.writeToReport(LogStatus.WARNING, "<b> ReservationId -> Expected : "+resvID+" Actual : "+dbResults.get("RESV_NAME_ID")+" .The requested reservation id did not get inserted into DB</b>");
										}
									}
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite Failed >> The Priority for the Reservation in th queue is not generated</b>");
								}
							}
							else if(WSAssert.assertIfElementExists(queueResvRes, "AddReservationToQueueRS_Errors_Error", true))
							{
								WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite Failed >> Error is - "+ WSClient.getElementValue(queueResvRes, "AddReservationToQueueRS_Errors_Error", XMLType.RESPONSE));
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a reservation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a profile failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING,"Prerequisites of reservation creation failed ");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter Queue Rooms failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				//WSClient.writeToReport(LogStatus.INFO, "<b>Executing Cancel Reservation to release room</b>");
				OPERALib.setOperaHeader(uname);
				if(!resvID.get("reservationId").equals("error"))
				{
					WSClient.setData("{var_resvId}", resvID.get("reservationId"));
					if(CancelReservation.cancelReservation("DS_02"))
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
	 * Method to check if the OWS Reservation Advanced Queue Reservation is working i.e.,
	 * error should generated when the reservationId is cancelled.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * ->Cancel the reservation
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "QueueReservation", "ResvAdvanced", "OWS"})

	public void queueReservation_38881() {
		try {
			String testName = "queueReservation_38881";
			WSClient.startTest(testName, "Verify that cancelled reservation is not pushed into queue", "minimumRegression");

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

			WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter QUEUE ROOMS is enabled</b>");

			/*********** Prerequisite : Verify if the parameter QUEUE ROOMS is enabled  ****************/
			queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
			if(queueResvParameterEnabled.equals("N"))
			{
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode","ResvCancelReason" }))
				{

					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					/************ Prerequisite 1: Create profile *******************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");

						/************** Prerequisite 2:Create a Reservation **************/

						resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							/************ Prerequisite 3 : Cancel the Reservation Operation **************/
							String cancelResvReq = WSClient.createSOAPMessage("CancelReservation", "DS_02");
							String cancelResvRes = WSClient.processSOAPMessage(cancelResvReq);

							if(WSAssert.assertIfElementExists(cancelResvRes, "CancelReservationRS_Success", true))
							{
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								WSClient.setData("{var_resvId}", resvID.get("reservationId"));

								/******************* OWS Queue Reservation Operation************************/
								String queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_01");
								String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

								if(WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","FAIL", false))
								{
									if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", true))
									{
										WSClient.writeToReport(LogStatus.INFO, "<b> The error code on response is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
									}
									if(WSAssert.assertIfElementExists(queueResvRes, "Result_Text_TextElement", true))
									{
										if(!WSClient.getElementValue(queueResvRes, "Result_Text_TextElement", XMLType.RESPONSE).equals("*null*"))
											WSClient.writeToReport(LogStatus.INFO, "<b> The error text on response is : "+WSClient.getElementValue(queueResvRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
									}
								}
								else if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", true))
								{
									String resvID=WSClient.getElementValue(queueResvReq, "ReservationRequest_ReservationID_UniqueID", XMLType.REQUEST);
									String priority=WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE);
									priority=priority.substring(priority.indexOf("Queue number is")+15);
									System.out.println(priority);
									WSClient.setData("{var_qPriority}", priority);

									String query=WSClient.getQuery("QS_01");
									HashMap<String, String> dbResults=WSClient.getDBRow(query);

									if(dbResults.get("RESV_NAME_ID") == null)
									{
										WSClient.writeToReport(LogStatus.FAIL, "<b> The Cancelled Reservation is not pushed into queue.But queue message is displayed as "+priority+"</b>");
									}
									else if(WSAssert.assertEquals(resvID, dbResults.get("RESV_NAME_ID"), true))
									{
										WSClient.writeToReport(LogStatus.FAIL, "<b> Reservation is pushed into the queue although reservation is cancelled </b>");
										queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_02");
										queueResvRes = WSClient.processSOAPMessage(queueResvReq);

										if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", true))
										{
											WSClient.writeToLog("Cancelled queue reservation is removed from the queue.");
										}
										else
										{
											WSClient.writeToLog("Cancelled queue reservation is not removed from the queue.");
										}
									}
								}
								else if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_faultstring", true))
								{
									WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
								}
							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Cancelling the reservation failed");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a reservation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a profile failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING,"Prerequisites of reservation creation failed ");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter Queue Rooms failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
		}
	}


	/**
	 * Method to check if the OWS Reservation Advanced Queue Reservation is working i.e.,
	 * error should generated when the reservationId arrival date
	 * is greater than business date.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "QueueReservation", "ResvAdvanced", "OWS"})

	public void queueReservation_38882() {
		String uname=null;
		try {
			String testName = "queueReservation_38882";
			WSClient.startTest(testName, "Verify that future booked reservation i.e., greater than business date is not pushed into queue", "minimumRegression");

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

			WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter QUEUE ROOMS is enabled</b>");

			/*********** Prerequisite : Verify if the parameter QUEUE ROOMS is enabled  ****************/
			queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
			if(queueResvParameterEnabled.equals("N"))
			{
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{

					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");

						/************** Prerequisite 2:Create a Reservation **************/

						resvID=CreateReservation.createReservation("DS_01");
						if(!resvID.get("reservationId").equals("error"))
						{
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							/******************* OWS Queue Reservation Operation************************/
							String queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_01");
							String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

							if(WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","FAIL", false))
							{
								if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", true))
								{
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code on response is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
								}
								if(WSAssert.assertIfElementExists(queueResvRes, "Result_Text_TextElement", true))
								{
									if(!WSClient.getElementValue(queueResvRes, "Result_Text_TextElement", XMLType.RESPONSE).equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO, "<b> The error text on response is : "+WSClient.getElementValue(queueResvRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
								}
							}
							else if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", true))
							{
								String priority=WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE);
								priority=priority.substring(priority.indexOf("Queue number is")+15);
								System.out.println(priority);
								WSClient.setData("{var_qPriority}", priority);

								String query=WSClient.getQuery("QS_01");
								HashMap<String, String> dbResults=WSClient.getDBRow(query);

								if(dbResults.get("RESV_NAME_ID") == null)
								{
									WSClient.writeToReport(LogStatus.FAIL, "<b> The Reservation where Guest is not arriving today is not pushed into queue.But queue message is displayed as "+priority+"</b>");
								}
								else
								{
									WSClient.writeToReport(LogStatus.FAIL, "<b> Reservation is pushed into queue but Guest is not arriving today </b>");
								}
							}
							else if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_faultstring", true))
							{
								WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a reservation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a profile failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING,"Prerequisites of reservation creation failed ");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter Queue Rooms failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				//WSClient.writeToReport(LogStatus.INFO, "<b>Executing Cancel Reservation to release room</b>");
				OPERALib.setOperaHeader(uname);
				if(!resvID.get("reservationId").equals("error"))
				{
					WSClient.setData("{var_resvId}", resvID.get("reservationId"));
					if(CancelReservation.cancelReservation("DS_02"))
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
	 * Method to check if the OWS Reservation Advanced Queue Reservation is working i.e.,
	 * error should generated when the reservation is checked in
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * ->Check in the reservation
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "QueueReservation", "ResvAdvanced", "OWS"})

	public void queueReservation_38883() {
		String uname=null;
		try {
			String testName = "queueReservation_38883";
			WSClient.startTest(testName, "Verify that checked-in reservation is not pushed into queue", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String roomNumber = "";

			uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			String resort = OWSLib.getChannelResort(resortOperaValue, channel);
			WSClient.setData("{var_owsresort}", resort);
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

			WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter QUEUE ROOMS is enabled</b>");

			/*********** Prerequisite : Verify if the parameter QUEUE ROOMS is enabled  ****************/
			queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
			if(queueResvParameterEnabled.equals("N"))
			{
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{
					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					/************* Prerequisite 1: Create profile  *********************************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");

						/************** Prerequisite 2:Create a Reservation **************/

						resvID=CreateReservation.createReservation("DS_12");
						if(!resvID.get("reservationId").equals("error"))
						{
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							/*** Prerequisite 3: Fetching available Hotel rooms with room type  ***/

							String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
							String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

							if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes,"FetchHotelRoomsRS_HotelRooms_Room", true))
							{
								roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
							}
							else
							{
								/*** Prerequisite 4: Creating a room to assign  ***/
								String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
								String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

								if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true))
								{
									roomNumber = WSClient.getElementValue(createRoomReq,"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to create room");
								}
							}

							WSClient.setData("{var_roomNumber}", roomNumber);

							/***** Prerequisite 5: Changing the room status to inspected to assign the room for checking in  ****/

							String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
							String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

							if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,"SetHousekeepingRoomStatusRS_Success", true))
							{
								/**** Prerequisite 6: Assign Room ****/

								String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
								String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

								if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",true))
								{
									/*** Prerequisite 7: CheckIn Reservation ***/

									String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
									String checkInRes = WSClient.processSOAPMessage(checkInReq);

									if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true))
									{
										OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
										WSClient.setData("{var_resvId}", resvID.get("reservationId"));

										/******************* OWS Queue Reservation Operation************************/
										String queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_01");
										String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

										if(WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","FAIL", false))
										{
											if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", true))
											{
												WSClient.writeToReport(LogStatus.INFO, "<b> The error code on response is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
											}
											if(WSAssert.assertIfElementExists(queueResvRes, "Result_Text_TextElement", true))
											{
												if(!WSClient.getElementValue(queueResvRes, "Result_Text_TextElement", XMLType.RESPONSE).equals("*null*"))
													WSClient.writeToReport(LogStatus.INFO, "<b> The error text on response is : "+WSClient.getElementValue(queueResvRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
											}
										}
										else if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", true))
										{
											String priority=WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE);
											priority=priority.substring(priority.indexOf("Queue number is")+15);
											System.out.println(priority);
											WSClient.setData("{var_qPriority}", priority);

											String query=WSClient.getQuery("QS_01");
											HashMap<String, String> dbResults=WSClient.getDBRow(query);

											if(dbResults.get("RESV_NAME_ID") == null)
											{
												WSClient.writeToReport(LogStatus.FAIL, "<b> The Checked-in Reservation is not pushed into queue.But queue message is displayed as "+priority+"</b>");
											}
											else
											{
												WSClient.writeToReport(LogStatus.FAIL, "<b> Reservation is pushed into queue although reservation is checked-in  </b>");
											}
										}
										else if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_faultstring", true))
										{
											WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
										}
									}
									else
									{
										WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >>  Unable to checkin reservation");
									}
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >>  Unable to assign room");
								}
							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >>  Unable to change the status of room to vacant and inspected");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a reservation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a profile failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING,"Prerequisites of reservation creation failed ");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter Queue Rooms failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				//WSClient.writeToReport(LogStatus.INFO, "<b>Executing Check Out Reservation to release room</b>");
				OPERALib.setOperaHeader(uname);
				if(!resvID.get("reservationId").equals("error"))
				{
					if(CheckoutReservation.checkOutReservation("DS_01"))
						WSClient.writeToLog("Reservation checking out successful");
					else
						WSClient.writeToLog("Reservation checking out failed");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}




	/**
	 * Method to check if the OWS Reservation Advanced Queue Reservation is working i.e.,
	 * delete a reservation from queue when given the confirmationNo which is in the queue.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * ->Add reservation to queue.
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "QueueReservation", "ResvAdvanced", "OWS"})

	public void queueReservation_38884() {
		String uname=null;
		try {
			String testName = "queueReservation_38884";
			WSClient.startTest(testName, "Verify that reservation in queue is removed from the queue when given confirmation no", "minimumRegression");

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

			WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter QUEUE ROOMS is enabled</b>");
			queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
			if(queueResvParameterEnabled.equals("N"))
			{
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");

				/*********** Prerequisite : Verify if the parameter QUEUE ROOMS is enabled  ****************/
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{
					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));


					/************ Prerequisite 1: Create profile *********************************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}",profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");

						/******************* Prerequisite 2:Create a Reservation ************************/
						resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							String queueResvReq = WSClient.createSOAPMessage("AddReservationToQueue", "DS_01");
							String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

							/**********Prerequisite 3:Add reservation to queue ************/
							if (WSAssert.assertIfElementExists(queueResvRes, "AddReservationToQueueRS_Success", true))
							{
								if(WSAssert.assertIfElementExists(queueResvRes, "AddReservationToQueueRS_QueueInfo_Priority", true))
								{
									String priority=WSClient.getElementValue(queueResvRes, "AddReservationToQueueRS_QueueInfo_Priority", XMLType.RESPONSE);
									System.out.println(priority);
									WSClient.setData("{var_qPriority}", priority);

									String query=WSClient.getQuery("QS_01");
									HashMap<String, String> dbResults=WSClient.getDBRow(query);

									if(dbResults.get("RESV_NAME_ID") == null)
									{
										WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite Failed >> The Reservation is not pushed into queue.Not inserted into DB</b>");
									}
									else
									{
										WSClient.setData("{var_confirmationNo}",resvID.get("confirmationId"));
										String resvID=WSClient.getElementValue(queueResvReq, "AddReservationToQueueRQ_ReservationID_ID", XMLType.REQUEST);
										if(WSAssert.assertEquals(resvID, dbResults.get("RESV_NAME_ID"), true))
										{
											WSClient.writeToReport(LogStatus.PASS, "<b> ReservationId -> Expected : "+resvID+" Actual : "+dbResults.get("RESV_NAME_ID")+" </b>");

											/******************* OWS Queue Reservation Operation************************/
											queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_04");
											queueResvRes = WSClient.processSOAPMessage(queueResvReq);

											if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", false))
											{
												WSClient.writeToReport(LogStatus.INFO, "<b>Queue Message is -> "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE)+"</b>");

												String query1=WSClient.getQuery("QS_05");
												HashMap<String, String> results=WSClient.getDBRow(query1);
												String sysdate=results.get("SYSDATE");
												sysdate=sysdate.substring(0,sysdate.indexOf(' '));

												query1=WSClient.getQuery("QS_02");
												results=WSClient.getDBRow(query1);

												String queueend=results.get("QUEUE_END");
												queueend=queueend.substring(0,queueend.indexOf(' '));
												if(WSAssert.assertEquals("0",results.get("QUEUE_PRIORITY"),true))
												{
													if(!WSAssert.assertEquals(sysdate, queueend, true))
													{
														WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Queue end should be set to sysdate in DB but set to "+queueend+"</b>");
													}
													else
													{
														WSClient.writeToReport(LogStatus.PASS, "<b>QueuePriority -> Expected : 0  Actual : "+results.get("QUEUE_PRIORITY")+"</b>");
														WSClient.writeToReport(LogStatus.PASS, "<b>QueueEnd -> Expected : "+sysdate+"  Actual : "+queueend+"</b>");
													}
												}
												else
												{
													WSClient.writeToReport(LogStatus.FAIL, "<b>"+"Queue priority should be set to 0 in DB but set to "+results.get("QUEUE_PRIORITY")+"</b>");
												}
											}
											else if(WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","FAIL", true))
											{
												WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
											}
											else if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_faultstring", true))
											{
												WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
											}
										}
										else
										{
											WSClient.writeToReport(LogStatus.WARNING, "<b> ReservationId -> Expected : "+resvID+" Actual : "+dbResults.get("RESV_NAME_ID")+" .The requested reservation id did not get inserted into DB</b>");
										}
									}
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite Failed >> The Priority for the Reservation in th queue is not generated</b>");
								}
							}
							else if(WSAssert.assertIfElementExists(queueResvRes, "AddReservationToQueueRS_Errors_Error", true))
							{
								WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite Failed >> Error is - "+WSClient.getElementValue(queueResvRes, "AddReservationToQueueRS_Errors_Error", XMLType.RESPONSE));
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a reservation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a profile failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING,"Prerequisites of reservation creation failed ");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter Queue Rooms failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				//WSClient.writeToReport(LogStatus.INFO, "<b>Executing Cancel Reservation to release room</b>");
				OPERALib.setOperaHeader(uname);
				if(!resvID.get("reservationId").equals("error"))
				{
					WSClient.setData("{var_resvId}", resvID.get("reservationId"));
					if(CancelReservation.cancelReservation("DS_02"))
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
	 * Method to check if the OWS Reservation Advanced Queue Reservation is working i.e.,
	 * error should generated when pseudo room type alloted to the reservation.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "QueueReservation", "ResvAdvanced", "OWS"})

	public void queueReservation_41304() {
		String uname=null;
		try {
			String testName = "queueReservation_41304";
			WSClient.startTest(testName, "Verify that pseudo room type alloted reservation is not pushed into queue", "minimumRegression");

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

			WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter QUEUE ROOMS is enabled</b>");

			/*********** Prerequisite : Verify if the parameter QUEUE ROOMS is enabled  ****************/
			queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
			if(queueResvParameterEnabled.equals("N"))
			{
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{
					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_07"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_04"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					/************* Prerequisite 1: Create profile *******************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");

						/************** Prerequisite 2:Create a Reservation **************/

						resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							/******************* OWS Queue Reservation Operation************************/
							String queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_01");
							String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

							if(WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","FAIL", false))
							{
								if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", true))
								{
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code on response is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
								}
								if(WSAssert.assertIfElementExists(queueResvRes, "Result_Text_TextElement", true))
								{
									if(!WSClient.getElementValue(queueResvRes, "Result_Text_TextElement", XMLType.RESPONSE).equals("*null*"))
										WSClient.writeToReport(LogStatus.INFO, "<b> The error text on response is : "+WSClient.getElementValue(queueResvRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
								}
							}
							else if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", true))
							{
								WSClient.writeToReport(LogStatus.FAIL, "<b> Psuedo Room type alloted reservation cannot pushed into queue </b>");
							}
							else if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_faultstring", true))
							{
								WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite failed >> Reservation creation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite failed >> Profile creation failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "Pre-requisites for Reservation creation failed");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter Queue Rooms failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				// WSClient.writeToReport(LogStatus.INFO, "<b>Executing Cancel Reservation to release room</b>");
				OPERALib.setOperaHeader(uname);
				if(!resvID.get("reservationId").equals("error"))
				{
					WSClient.setData("{var_resvId}", resvID.get("reservationId"));
					if(CancelReservation.cancelReservation("DS_02"))
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
	 * Method to check if the OWS Reservation Advanced Queue Reservation is working i.e.,
	 * reservation is not pushed into queue when QUEUE_ROOMS parameter is inactive.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "QueueReservation", "ResvAdvanced", "OWS"})

	public void queueReservation_41305() {
		String uname=null;
		try {
			String testName = "queueReservation_41305";
			WSClient.startTest(testName, "Verify that reservation is not pushed into queue when QUEUE_ROOMS parameter is inactive", "minimumRegression");

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

			WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter QUEUE ROOMS is enabled</b>");

			/*********** Prerequisite : Verify if the parameter QUEUE ROOMS is enabled  ****************/
			queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
			if(queueResvParameterEnabled.equals("Y"))
			{
				WSClient.setData("{var_settingValue}", "N");
				WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter QUEUE ROOMS</b>");
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("N"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{

					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");

						/************** Prerequisite 2:Create a Reservation **************/

						resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							/******************* OWS Queue Reservation Operation************************/
							String queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_01");
							String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

							if(WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","FAIL", false))
							{
								if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", true))
								{
									WSClient.writeToReport(LogStatus.INFO, "<b> The error code on response is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
								}
								if(WSAssert.assertIfElementExists(queueResvRes, "Result_Text_TextElement", true))
								{
									WSClient.writeToReport(LogStatus.INFO, "<b> The error text on response is : "+WSClient.getElementValue(queueResvRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
								}
							}
							else if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", true))
							{
								WSClient.writeToReport(LogStatus.FAIL, "<b> Reservation is pushed into queue although QUEUE_ROOMS is inactive </b>");
							}
							else if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_faultstring", true))
							{
								WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a reservation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a profile failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING,"Prerequisites of reservation creation failed ");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter Queue Rooms failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				//	WSClient.writeToReport(LogStatus.INFO, "<b>Executing Cancel Reservation to release room</b>");
				OPERALib.setOperaHeader(uname);
				if(!resvID.get("reservationId").equals("error"))
				{
					WSClient.setData("{var_resvId}", resvID.get("reservationId"));
					if(CancelReservation.cancelReservation("DS_02"))
						WSClient.writeToLog("Reservation cancellation successful");
					else
						WSClient.writeToLog("Reservation cancellation failed");

					String queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_02");
					String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

					if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", true))
					{
						WSClient.writeToLog("Cancelled queue reservation is removed from the queue.");
					}
					else
					{
						WSClient.writeToLog("Cancelled queue reservation is not removed from the queue.");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Method to check if the OWS Reservation Advanced Queue Reservation is working i.e.,
	 * shared reservation is not pushed into queue as sharer is already in queue.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * ->Create a shared reservation
	 * ->Push the sharer into queue.
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "QueueReservation", "ResvAdvanced", "OWS"})

	public void queueReservation_40316() {
		String uname="";
		HashMap<String, String> resvID1=new HashMap<>();
		try {
			String testName = "queueReservation_40316";
			WSClient.startTest(testName, "Verify that shared reservation is not pushed into queue as sharer is already in queue", "minimumRegression");

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

			WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter QUEUE ROOMS is enabled</b>");

			/*********** Prerequisite : Verify if the parameter QUEUE ROOMS is enabled  ****************/
			queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
			if(queueResvParameterEnabled.equals("N"))
			{
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{

					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_02"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");

						/******************* Prerequisite 2:Create a Reservation ************************/

						resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							/******************* Prerequisite 3:Create a Profile (Sharer)************************/
							profileID=CreateProfile.createProfile("DS_01");
							if(!profileID.equals("error"))
							{
								WSClient.setData("{var_profileId}", profileID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID(Sharer): "+profileID+"</b>");

								/******************* Prerequisite 4:Create a Reservation (Sharer)************************/

								resvID1 = CreateReservation.createReservation("DS_05");
								if(!resvID1.get("reservationId").equals("error"))
								{
									WSClient.setData("{var_resvId1}", resvID1.get("reservationId"));
									WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID(Sharer): "+resvID.get("reservationId")+"</b>");

									/******************* Prerequisite 5:Share the reservations************************/
									String combineShareReq = WSClient.createSOAPMessage("CombineShareReservations", "DS_01");
									String combineShareRes = WSClient.processSOAPMessage(combineShareReq);

									if(WSAssert.assertIfElementExists(combineShareRes, "CombineShareReservationsRS_Success", true))
									{
										WSClient.writeToReport(LogStatus.INFO, "<b>Validation of shared reservation id's</b>");
										WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
										String query=WSClient.getQuery("OWSQueueReservation","QS_03");
										ArrayList<LinkedHashMap<String, String>> dbResults1=WSClient.getDBRows(query);
										String id1=dbResults1.get(0).get("RESV_NAME_ID"),id2=dbResults1.get(1).get("RESV_NAME_ID");
										String actual1=resvID1.get("reservationId"),actual2=resvID.get("reservationId");

										if(dbResults1.size()==2 && ((id1.equals(actual1)&&id2.equals(actual2))||(id1.equals(actual2)&&id2.equals(actual1))))
										{
											OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

											/*******************Prerequisite 6: Push sharer to the queue************************/

											String queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_05");
											String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

											if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", false))
											{
												WSClient.writeToReport(LogStatus.INFO, "<b>"+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE)+"</b>");
												String priority=WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE);
												priority=priority.substring(priority.indexOf("Queue number is")+15);
												System.out.println(priority);
												WSClient.setData("{var_qPriority}", priority);

												query=WSClient.getQuery("QS_01");
												LinkedHashMap<String, String> dbResults = WSClient.getDBRow(query);

												if(dbResults.get("RESV_NAME_ID") == null)
												{
													WSClient.writeToReport(LogStatus.WARNING, "<b>Prerequisite failed >> The Sharer Reservation is not pushed into queue.Not inserted into DB</b>");
												}
												else
												{
													String resvID=WSClient.getElementValue(queueResvReq, "ReservationRequest_ReservationID_UniqueID", XMLType.REQUEST);
													if(!WSAssert.assertEquals(resvID, dbResults.get("RESV_NAME_ID"), true))
													{
														WSClient.writeToReport(LogStatus.WARNING, "<b>Prerequisite failed >> The Sharer reservation did not get inserted into DB</b>");
													}
													else
													{
														String queueResvReq1 = WSClient.createSOAPMessage("OWSQueueReservation", "DS_01");
														String queueResvRes1 = WSClient.processSOAPMessage(queueResvReq1);
														if (WSAssert.assertIfElementValueEquals(queueResvRes1, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", false))
														{
															String msg=WSClient.getElementValue(queueResvRes1, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE);
															//																query=WSClient.getQuery("QS_04");
															//																dbResults = WSClient.getDBRow(query);
															String msg1=" is already Checked In";
															String msg2="now on Queue,Queue number is";
															if(msg.toUpperCase().contains(msg1.toUpperCase()))
															{
																WSClient.writeToReport(LogStatus.FAIL, "<b> Sharer is already on queue but queue message obtained is "+msg+" </b>");
															}
															else if(msg.toUpperCase().contains(msg2.toUpperCase()))
															{
																WSClient.writeToReport(LogStatus.FAIL, "<b> Sharer is already on queue but other reservation is pushed into queue.Queue message displayed is "+msg+"</b>");
															}
															else
															{
																WSClient.writeToReport(LogStatus.INFO, "<b>"+WSClient.getElementValue(queueResvRes1, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE)+"</b>");
															}
														}
														else if(WSAssert.assertIfElementValueEquals(queueResvRes1, "QueueReservationResponse_Result_resultStatusFlag","FAIL", true))
														{
															WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(queueResvRes1, "QueueReservationResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
														}
														else if(WSAssert.assertIfElementExists(queueResvRes1, "QueueReservationResponse_faultstring", true))
														{
															WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(queueResvRes1, "QueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
														}
													}
												}
											}
											else if(WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","FAIL", true))
											{
												WSClient.writeToReport(LogStatus.WARNING, "<b> Prerequisite failed >> The error code that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
											}
											else if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_faultstring", true))
											{
												WSClient.writeToReport(LogStatus.WARNING, "<b>Prerequisite failed >> The error that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
											}
										}
										else
										{
											WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Sharing the reservations failed");
										}
									}
									else
									{
										WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Sharing the reservations failed");
									}
								}
							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a profile failed");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a reservation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a profile failed");
					}
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter Queue Rooms failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				//WSClient.writeToReport(LogStatus.INFO, "<b>Executing Cancel Reservation to release room</b>");
				OPERALib.setOperaHeader(uname);
				if(!resvID.get("reservationId").equals("error"))
				{
					WSClient.setData("{var_resvId}", resvID.get("reservationId"));
					if(CancelReservation.cancelReservation("DS_02"))
						WSClient.writeToLog("Reservation cancellation successful");
					else
						WSClient.writeToLog("Reservation cancellation failed");
					WSClient.setData("{var_resvId}", resvID1.get("reservationId"));
					if(CancelReservation.cancelReservation("DS_02"))
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
	 * Method to check if the OWS Reservation Advanced Queue Reservation is working i.e.,
	 * shared reservation is pushed into queue.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * ->Create a shared reservation
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "QueueReservation", "ResvAdvanced", "OWS"})

	public void queueReservation_40311() {
		String uname="";
		HashMap<String, String> resvID1=new HashMap<>();
		try {
			String testName = "queueReservation_40311";
			WSClient.startTest(testName, "Verify that shared reservation is pushed into queue", "minimumRegression");

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

			WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter QUEUE ROOMS is enabled</b>");

			/*********** Prerequisite : Verify if the parameter QUEUE ROOMS is enabled  ****************/
			queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
			if(queueResvParameterEnabled.equals("N"))
			{
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{

					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_02"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");

						/******************* Prerequisite 2:Create a Reservation ************************/

						resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							/******************* Prerequisite 3:Create a Profile (Sharer)************************/
							profileID=CreateProfile.createProfile("DS_01");
							if(!profileID.equals("error"))
							{
								WSClient.setData("{var_profileId}", profileID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID(Sharer): "+profileID+"</b>");

								/******************* Prerequisite 4:Create a Reservation (Sharer)************************/

								resvID1 = CreateReservation.createReservation("DS_05");
								if(!resvID1.get("reservationId").equals("error"))
								{
									WSClient.setData("{var_resvId1}", resvID1.get("reservationId"));
									WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID(Sharer): "+resvID.get("reservationId")+"</b>");

									/******************* Prerequisite 5:Share the reservations************************/
									String combineShareReq = WSClient.createSOAPMessage("CombineShareReservations", "DS_01");
									String combineShareRes = WSClient.processSOAPMessage(combineShareReq);

									if(WSAssert.assertIfElementExists(combineShareRes, "CombineShareReservationsRS_Success", true))
									{
										WSClient.writeToReport(LogStatus.INFO, "<b>Validation of shared reservation id's</b>");
										WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
										String query=WSClient.getQuery("OWSQueueReservation","QS_03");
										ArrayList<LinkedHashMap<String, String>> dbResults1=WSClient.getDBRows(query);
										String id1=dbResults1.get(0).get("RESV_NAME_ID"),id2=dbResults1.get(1).get("RESV_NAME_ID");
										String actual1=resvID1.get("reservationId"),actual2=resvID.get("reservationId");

										if(dbResults1.size()==2 && ((id1.equals(actual1)&&id2.equals(actual2))||(id1.equals(actual2)&&id2.equals(actual1))))
										{
											OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

											/***************** OWS Queue Reservation Operation *******************/
											String queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_01");
											String queueResvRes = WSClient.processSOAPMessage(queueResvReq);
											if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", false))
											{
												WSClient.writeToReport(LogStatus.INFO, "<b>"+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE)+"</b>");
												String priority=WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE);
												priority=priority.substring(priority.indexOf("Queue number is")+15);
												System.out.println(priority);
												WSClient.setData("{var_qPriority}", priority);

												query=WSClient.getQuery("QS_01");
												HashMap<String, String> dbResults=WSClient.getDBRow(query);

												if(dbResults.get("RESV_NAME_ID") == null)
												{
													WSClient.writeToReport(LogStatus.FAIL, "<b> The Reservation is not pushed into queue.Not inserted into DB</b>");
												}
												else
												{
													if(WSAssert.assertEquals(resvID.get("reservationId"), dbResults.get("RESV_NAME_ID"), true))
													{
														WSClient.writeToReport(LogStatus.PASS, "<b> ReservationId -> Expected : "+resvID.get("reservationId")+" Actual : "+dbResults.get("RESV_NAME_ID")+" </b>");
													}
													else
													{
														WSClient.writeToReport(LogStatus.FAIL, "<b> ReservationId -> Expected : "+resvID.get("reservationId")+" Actual : "+dbResults.get("RESV_NAME_ID")+" </b>");
													}
												}
											}
											else if(WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","FAIL", true))
											{
												WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
											}
											else if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_faultstring", true))
											{
												WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
											}
										}
										else
										{
											WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Sharing the reservations failed");
										}
									}
									else
									{
										WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Sharing the reservations failed");
									}
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a reservation failed");
								}
							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a profile failed");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a reservation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a profile failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING,"Prerequisites for Creating a reservation failed");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter Queue Rooms failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				//WSClient.writeToReport(LogStatus.INFO, "<b>Executing Cancel Reservation to release room</b>");
				OPERALib.setOperaHeader(uname);
				if(!resvID.get("reservationId").equals("error"))
				{
					WSClient.setData("{var_resvId}", resvID.get("reservationId"));
					if(CancelReservation.cancelReservation("DS_02"))
						WSClient.writeToLog("Reservation cancellation successful");
					else
						WSClient.writeToLog("Reservation cancellation failed");
					if(!resvID.get("reservationId").equals("error"))
					{
						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));
						if(CancelReservation.cancelReservation("DS_02"))
							WSClient.writeToLog("Reservation cancellation successful");
						else
							WSClient.writeToLog("Reservation cancellation failed");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}


	/**
	 * Method to check if the OWS Reservation Advanced Queue Reservation is working i.e.,
	 * shared reservation is not pushed into queue as sharer is checked in.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * ->Create a shared reservation
	 * ->Check in the sharer.
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "QueueReservation", "ResvAdvanced", "OWS"})

	public void queueReservation_40325() {
		String uname="";
		HashMap<String, String> resvID1=new HashMap<>();
		try {
			String testName = "queueReservation_40325";
			WSClient.startTest(testName, "Verify that shared reservation is not pushed into queue as sharer is checked in", "minimumRegression");

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

			WSClient.setData("{var_parameter}", "QUEUE_ROOMS");
			WSClient.writeToReport(LogStatus.INFO, "<b>Verify if the parameter QUEUE ROOMS is enabled</b>");

			/*********** Prerequisite : Verify if the parameter QUEUE ROOMS is enabled  ****************/
			queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
			if(queueResvParameterEnabled.equals("N"))
			{
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{

					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_02"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					/************* Prerequisite 1: Create profile **********************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");

						/******************* Prerequisite 2:Create a Reservation ************************/

						resvID=CreateReservation.createReservation("DS_12");
						if(!resvID.get("reservationId").equals("error"))
						{
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							/******************* Prerequisite 3:Create a Profile (Sharer)************************/
							profileID=CreateProfile.createProfile("DS_01");
							if(!profileID.equals("error"))
							{
								WSClient.setData("{var_profileId}", profileID);
								WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID(Sharer): "+profileID+"</b>");

								/******************* Prerequisite 4:Create a Reservation (Sharer)************************/

								resvID1 = CreateReservation.createReservation("DS_12");
								if(!resvID1.get("reservationId").equals("error"))
								{
									WSClient.setData("{var_resvId1}", resvID1.get("reservationId"));
									WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID(Sharer): "+resvID.get("reservationId")+"</b>");

									/******************* Prerequisite 5:Share the reservations************************/
									String combineShareReq = WSClient.createSOAPMessage("CombineShareReservations", "DS_01");
									String combineShareRes = WSClient.processSOAPMessage(combineShareReq);

									if(WSAssert.assertIfElementExists(combineShareRes, "CombineShareReservationsRS_Success", true))
									{
										WSClient.writeToReport(LogStatus.INFO, "<b>Validation of shared reservation id's</b>");
										WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
										String query=WSClient.getQuery("OWSQueueReservation","QS_03");
										ArrayList<LinkedHashMap<String, String>> dbResults1=WSClient.getDBRows(query);
										String id1=dbResults1.get(0).get("RESV_NAME_ID"),id2=dbResults1.get(1).get("RESV_NAME_ID");
										String actual1=resvID1.get("reservationId"),actual2=resvID.get("reservationId");

										if(dbResults1.size()==2 && ((id1.equals(actual1)&&id2.equals(actual2))||(id1.equals(actual2)&&id2.equals(actual1))))
										{
											String roomNumber="";
											/*** Prerequisite 6: Fetching available Hotel rooms with room type  ***/

											String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
											String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

											if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true) && WSAssert.assertIfElementExists(fetchHotelRoomsRes,"FetchHotelRoomsRS_HotelRooms_Room", true))
											{
												roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
											}
											else
											{
												/*** Prerequisite 7: Creating a room to assign  ***/
												String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "RoomMaint");
												String createRoomRes = WSClient.processSOAPMessage(createRoomReq);

												if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true))
												{
													roomNumber = WSClient.getElementValue(createRoomReq,"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
												}
												else
												{
													WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to create room");
												}
											}

											WSClient.setData("{var_roomNumber}", roomNumber);

											/***** Prerequisite 8: Changing the room status to inspected to assign the room for checking in  ****/

											String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
											String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);

											if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,"SetHousekeepingRoomStatusRS_Success", true))
											{
												WSClient.setData("{var_resvId}", resvID1.get("reservationId"));

												/**** Prerequisite 9: Assign Room ****/

												String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
												String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);

												if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",true))
												{
													/*** Prerequisite 10: CheckIn Reservation ***/

													String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
													String checkInRes = WSClient.processSOAPMessage(checkInReq);

													if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true))
													{
														OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
														WSClient.setData("{var_resvId}", resvID.get("reservationId"));

														/************** OWS Queue Reservation Operation **************/
														String queueResvReq = WSClient.createSOAPMessage("OWSQueueReservation", "DS_01");
														String queueResvRes = WSClient.processSOAPMessage(queueResvReq);
														if (WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","SUCCESS", false))
														{
															//WSClient.writeToReport(LogStatus.INFO, "<b>"+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE)+"</b>");
															String msg=WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE);
															//																query=WSClient.getQuery("QS_04");
															//																LinkedHashMap<String, String> dbResults = WSClient.getDBRow(query);
															String expected=" is already Checked in";
															if(msg.toUpperCase().contains(expected.toUpperCase()))
															{
																WSClient.writeToReport(LogStatus.INFO, "<b>"+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_QueueMessage", XMLType.RESPONSE)+"</b>");
															}
															else
															{
																WSClient.writeToReport(LogStatus.FAIL, "<b> Sharer is already checked in.Queue Message displayed is "+msg+"</b>");
															}
														}
														else if(WSAssert.assertIfElementValueEquals(queueResvRes, "QueueReservationResponse_Result_resultStatusFlag","FAIL", true))
														{
															WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
														}
														else if(WSAssert.assertIfElementExists(queueResvRes, "QueueReservationResponse_faultstring", true))
														{
															WSClient.writeToReport(LogStatus.FAIL, "<b> The error that is generated is : "+WSClient.getElementValue(queueResvRes, "QueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
														}
													}
													else
													{
														WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >>  Unable to checkin reservation");
													}
												}
												else
												{
													WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >>  Unable to assign room");
												}
											}
											else
											{
												WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >>  Unable to change the status of room to vacant and inspected");
											}
										}
										else
										{
											WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Sharing the reservations failed");
										}
									}
									else
									{
										WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Sharing the reservations failed");
									}
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a reservation failed");
								}
							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a profile failed");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a reservation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Creating a profile failed");
					}
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter Queue Rooms failed");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			try {
				//WSClient.writeToReport(LogStatus.INFO, "<b>Executing Cancel Reservation to release room</b>");
				OPERALib.setOperaHeader(uname);
				if(!resvID.get("reservationId").equals("error"))
				{
					WSClient.setData("{var_resvId}", resvID.get("reservationId"));
					if(CancelReservation.cancelReservation("DS_02"))
						WSClient.writeToLog("Reservation cancellation successful");
					else
						WSClient.writeToLog("Reservation cancellation failed");
					if(!resvID1.get("reservationId").equals("error"))
					{
						WSClient.setData("{var_resvId}", resvID1.get("reservationId"));
						if(CheckoutReservation.checkOutReservation("DS_01"))
							WSClient.writeToLog("Reservation check out successful");
						else
							WSClient.writeToLog("Reservation check out failed");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
