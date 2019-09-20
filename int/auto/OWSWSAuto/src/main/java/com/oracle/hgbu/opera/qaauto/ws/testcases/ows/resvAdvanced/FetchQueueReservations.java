package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;


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
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchQueueReservations extends WSSetUp{

	String profileID="",queueResvParameterEnabled="";
	HashMap<String, String> resvID=new HashMap<>();

	/**
	 * Method to check if the OWS Reservation Advanced Fetch Queue Reservation is working i.e.,
	 * fetching all the reservations in the queue for the given resort.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * -->Reservation is pushed into the queue
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "sanity", "FetchQueueReservations", "ResvAdvanced", "OWS", "fetchQueueReservation_39049"})

	public void fetchQueueReservation_39049() {
		String uname=null;
		try {
			String testName = "fetchQueueReservations_39049";
			WSClient.startTest(testName, "Verify that all the queue reservations in the given resort are retrieved successfully", "sanity");

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
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");
				WSClient.setData("{var_settingValue}", "Y");
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
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");

						/******************* Prerequisite 2:Create a Reservation ************************/

						resvID=CreateReservation.createReservation("DS_05");
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						if(!resvID.get("reservationId").equals("error"))
						{
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							/******************* PreRequisite 3: Add Reservation to queue Operation************************/
							String queueResvReq = WSClient.createSOAPMessage("AddReservationToQueue", "DS_01");
							String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

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
										String resvID=WSClient.getElementValue(queueResvReq, "AddReservationToQueueRQ_ReservationID_ID", XMLType.REQUEST);
										System.out.println(resvID+" "+ dbResults.get("RESV_NAME_ID"));
										if(WSAssert.assertEquals(resvID, dbResults.get("RESV_NAME_ID"), false))
										{
											WSClient.writeToReport(LogStatus.PASS, "<b> ReservationId -> Expected : "+resvID+" Actual : "+dbResults.get("RESV_NAME_ID")+" </b>");

											OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

											String fetchQueueResvReq = WSClient.createSOAPMessage("OWSFetchQueueReservation", "DS_01");
											String fetchQueueResvRes = WSClient.processSOAPMessage(fetchQueueResvReq);
											if (WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","SUCCESS", false))
											{
												query=WSClient.getQuery("QS_01");
												ArrayList<LinkedHashMap<String, String>>  DBResults=WSClient.getDBRows(query);
												HashMap<String, String> xPath=new HashMap<>();
												xPath.put("ReservationRequest_ReservationID_UniqueID", "FetchQueueReservationsResponse_QueueDetails_QueueDatas");
												List<LinkedHashMap<String, String>>  response=WSClient.getMultipleNodeList(fetchQueueResvRes, xPath, true, XMLType.RESPONSE);
												WSAssert.assertEquals(response, DBResults, false);

												WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_QueueTime_TotalRooms", String.valueOf(DBResults.size()), false);
											}
											else if(WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","FAIL", true))
											{
												if(WSAssert.assertIfElementExists(fetchQueueResvRes, "Result_Text_TextElement", true))
												{
													WSClient.writeToReport(LogStatus.FAIL, "<b> The error text that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
												}
												if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", true))
												{
													WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
												}
											}
											else if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", true))
											{
												WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
											}
										}
										else
										{
											WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> The requested reservation id did not get inserted into DB</b>");
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
								WSClient.writeToReport(LogStatus.WARNING,"<b> Pre-Requisite Failed >> The error that is generated is : "+ WSClient.getElementValue(queueResvRes, "AddReservationToQueueRS_Errors_Error_ShortText", XMLType.RESPONSE)+"</b>");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> Reservation creation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> Profile creation failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite of Reservation creation failed");
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
	 * Method to check if the OWS Reservation Advanced Fetch Queue Reservation is working i.e.,
	 * fetching all the reservations in the queue for the given resort
	 * based on the given room type.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * -->Reservation is pushed into the queue
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "FetchQueueReservations", "ResvAdvanced", "OWS", "fetchQueueReservations_39049"})

	public void fetchQueueReservation_39052() {
		String uname=null;
		try {
			String testName = "fetchQueueReservations_39052";
			WSClient.startTest(testName, "Verify that all the queue reservations are retrieved successfully based on the room type", "minimumRegression");

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
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						/******************* Prerequisite 2:Create a Reservation ************************/

						resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							/******************* PreRequisite 3: Add Reservation to queue Operation************************/
							String queueResvReq = WSClient.createSOAPMessage("AddReservationToQueue", "DS_01");
							String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

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
										String resvID=WSClient.getElementValue(queueResvReq, "AddReservationToQueueRQ_ReservationID_ID", XMLType.REQUEST);
										System.out.println(resvID+" "+ dbResults.get("RESV_NAME_ID"));
										if(WSAssert.assertEquals(resvID, dbResults.get("RESV_NAME_ID"), true))
										{
											WSClient.writeToReport(LogStatus.PASS, "<b> ReservationId -> Expected : "+resvID+" Actual : "+dbResults.get("RESV_NAME_ID")+" </b>");

											OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

											String fetchQueueResvReq = WSClient.createSOAPMessage("OWSFetchQueueReservation", "DS_02");
											String fetchQueueResvRes = WSClient.processSOAPMessage(fetchQueueResvReq);
											if (WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","SUCCESS", false))
											{
												query=WSClient.getQuery("QS_02");
												ArrayList<LinkedHashMap<String, String>>  DBResults=WSClient.getDBRows(query);
												HashMap<String, String> xPath=new HashMap<>();
												xPath.put("ReservationRequest_ReservationID_UniqueID", "FetchQueueReservationsResponse_QueueDetails_QueueDatas");
												xPath.put("QueueDetails_QueueDatas_RoomStay_roomTypeCode", "FetchQueueReservationsResponse_QueueDetails_QueueDatas");
												List<LinkedHashMap<String, String>>  response=WSClient.getMultipleNodeList(fetchQueueResvRes, xPath, true, XMLType.RESPONSE);
												WSAssert.assertEquals(response, DBResults, false);
											}
											else if(WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","FAIL", true))
											{
												if(WSAssert.assertIfElementExists(fetchQueueResvRes, "Result_Text_TextElement", true))
												{
													WSClient.writeToReport(LogStatus.FAIL, "<b> The error text that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
												}
												if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", true))
												{
													WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
												}
											}
											else if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", true))
											{
												WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
											}
										}
										else
										{
											WSClient.writeToReport(LogStatus.WARNING, "<b> The requested reservation id did not get inserted into DB</b>");
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
								WSClient.writeToReport(LogStatus.WARNING,"<b> Pre-Requisite Failed >> Error generated is -"+ WSClient.getElementValue(queueResvRes, "AddReservationToQueueRS_Errors_Error_ShortText", XMLType.RESPONSE)+"</b>");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> Reservation creation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> Profile creation failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite of Reservation creation failed");
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
	 * Method to check if the OWS Reservation Advanced Fetch Queue Reservation is working i.e.,
	 * error code is generated when given invalid resort id.
	 *
	 */
	@Test(groups = { "minimumRegression", "FetchQueueReservations", "ResvAdvanced", "OWS"})

	public void fetchQueueReservation_39056() {
		try {
			String testName = "fetchQueueReservations_39056";
			WSClient.startTest(testName, "Verify that error code is generated when given invalid hotel code ", "minimumRegression");

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}", resortOperaValue);

			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channel = OWSLib.getChannel();
			String channelType = OWSLib.getChannelType(channel);
			WSClient.setData("{var_owsresort}", "InvalidPROP");
			String channelCarrier = OWSLib.getChannelCarier(resortOperaValue, channel);
			OPERALib.setOperaHeader(uname);

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
				OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

				String fetchQueueResvReq = WSClient.createSOAPMessage("OWSFetchQueueReservation", "DS_01");
				String fetchQueueResvRes = WSClient.processSOAPMessage(fetchQueueResvReq);
				if (WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","SUCCESS", true))
				{
					WSClient.writeToReport(LogStatus.FAIL, "<b> Queue Reservations should not be fetched as given invalid resort id </b>");
				}
				else if(WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","FAIL", false))
				{
					if(WSAssert.assertIfElementExists(fetchQueueResvRes, "Result_Text_TextElement", true))
					{
						WSClient.writeToReport(LogStatus.INFO, "<b> The error text that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
					}
					if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", true))
					{
						WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
					}
				}
				else if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", true))
				{
					WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
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
	 * Method to check if the OWS Reservation Advanced Fetch Queue Reservation is working i.e.,
	 * fetching all the reservations in the queue for the given resort
	 * based on the given room class.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * -->Reservation is pushed into the queue
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "FetchQueueReservations", "ResvAdvanced", "OWS","Failed"})

	public void fetchQueueReservation_39055() {
		String uname=null;
		try {
			String testName = "fetchQueueReservations_39055";
			WSClient.startTest(testName, "Verify that all the queue reservations are retrieved successfully based on the room class", "minimumRegression");

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
					WSClient.setData("{var_roomClass}", OperaPropConfig.getDataSetForCode("RoomClass", "DS_02"));

					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));

						/******************* Prerequisite 2:Create a Reservation ************************/
						resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							/******************* PreRequisite 3: Add Reservation to queue Operation************************/
							String queueResvReq = WSClient.createSOAPMessage("AddReservationToQueue", "DS_01");
							String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

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
										String resvID=WSClient.getElementValue(queueResvReq, "AddReservationToQueueRQ_ReservationID_ID", XMLType.REQUEST);
										System.out.println(resvID+" "+ dbResults.get("RESV_NAME_ID"));
										if(WSAssert.assertEquals(resvID, dbResults.get("RESV_NAME_ID"), true))
										{
											WSClient.writeToReport(LogStatus.PASS, "<b> ReservationId -> Expected : "+resvID+" Actual : "+dbResults.get("RESV_NAME_ID")+" </b>");

											OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

											String fetchQueueResvReq = WSClient.createSOAPMessage("OWSFetchQueueReservation", "DS_03");
											String fetchQueueResvRes = WSClient.processSOAPMessage(fetchQueueResvReq);
											if (WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","SUCCESS", false))
											{
												query=WSClient.getQuery("QS_03");
												System.out.println(query);
												ArrayList<LinkedHashMap<String, String>>  DBResults=WSClient.getDBRows(query);
												HashMap<String, String> xPath=new HashMap<>();
												xPath.put("ReservationRequest_ReservationID_UniqueID", "FetchQueueReservationsResponse_QueueDetails_QueueDatas");
												xPath.put("QueueDetails_QueueDatas_RoomStay_roomClass", "FetchQueueReservationsResponse_QueueDetails_QueueDatas");
												List<LinkedHashMap<String, String>>  response=WSClient.getMultipleNodeList(fetchQueueResvRes, xPath, true, XMLType.RESPONSE);
												WSAssert.assertEquals(response, DBResults, false);
											}
											else if(WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","FAIL", true))
											{
												if(WSAssert.assertIfElementExists(fetchQueueResvRes, "Result_Text_TextElement", true))
												{
													WSClient.writeToReport(LogStatus.FAIL, "<b> The error text that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
												}
												if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", true))
												{
													WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
												}
											}
											else if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", true))
											{
												WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
											}
										}
										else
										{
											WSClient.writeToReport(LogStatus.WARNING, "<b> The requested reservation id did not get inserted into DB</b>");
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
								WSClient.writeToReport(LogStatus.WARNING,"<b> Pre-Requisite Failed >> Error generated is : "+ WSClient.getElementValue(queueResvRes, "AddReservationToQueueRS_Errors_Error_ShortText", XMLType.RESPONSE)+"</b>");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> Reservation creation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> Profile creation failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite of Reservation creation failed");
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
	 * all the queue reservation details are populated correctly on response
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 *
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "FetchQueueReservations", "ResvAdvanced", "OWS","Failed"})

	public void fetchQueueReservation_39152() {
		String uname=null;
		try {
			String testName = "fetchQueueReservation_39152";
			WSClient.startTest(testName, "Verify that all the queue reservation details are populated correctly on response", "minimumRegression");

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
			queueResvParameterEnabled=FetchApplicationParameters.getApplicationParameter("DS_01");
			if(queueResvParameterEnabled.equals("N"))
			{
				WSClient.setData("{var_settingValue}", "Y");
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode",
						"Title","VipLevel"}))
				{

					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					WSClient.setData("{var_nameTitle}", OperaPropConfig.getDataSetForCode("Title", "DS_01"));
					WSClient.setData("{var_birthDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_MINUS_1000}"));
					WSClient.setData("{var_vipCode}", OperaPropConfig.getDataSetForCode("VipLevel", "DS_01"));

					profileID=CreateProfile.createProfile("DS_22");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");

						/************** Prerequisite 2:Create a Reservation **************/
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
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
									/******************* PreRequisite 7: Add Reservation to queue Operation************************/
									String queueResvReq = WSClient.createSOAPMessage("AddReservationToQueue", "DS_01");
									String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

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
												String resvID=WSClient.getElementValue(queueResvReq, "AddReservationToQueueRQ_ReservationID_ID", XMLType.REQUEST);
												System.out.println(resvID+" "+ dbResults.get("RESV_NAME_ID"));
												if(WSAssert.assertEquals(resvID, dbResults.get("RESV_NAME_ID"), true))
												{
													OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
													WSClient.writeToReport(LogStatus.PASS, "<b> ReservationId -> Expected : "+resvID+" Actual : "+dbResults.get("RESV_NAME_ID")+" </b>");


													/***************** Fetch Queue Reservations Operation ********************/
													String fetchQueueResvReq = WSClient.createSOAPMessage("OWSFetchQueueReservation", "DS_01");
													String fetchQueueResvRes = WSClient.processSOAPMessage(fetchQueueResvReq);
													if (WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","SUCCESS", false))
													{
														int nodeNo=WSClient.getNodeIndex(fetchQueueResvRes, WSClient.getData("{var_resvId}"), "FetchQueueReservationsResponse_QueueDetails_QueueDatas", "ReservationRequest_ReservationID_UniqueID", XMLType.RESPONSE);
														System.out.println(nodeNo);
														LinkedHashMap<String, Integer> nodeValues=new LinkedHashMap<>();
														nodeValues.put("FetchQueueReservationsResponse_QueueDetails_QueueDatas", nodeNo);
														nodeValues.put("Guests_PersonName_lastName", 1);
														String lname=WSClient.getElementValueByIndex(fetchQueueResvRes, nodeValues, XMLType.RESPONSE);
														query=WSClient.getQuery("QS_04");
														LinkedHashMap<String, String>  DBResults=WSClient.getDBRow(query);
														if(WSAssert.assertEquals(DBResults.get("GUEST_NAME"),lname,true))
														{
															WSClient.writeToReport(LogStatus.PASS, "lastName -> <b>Expected:</b>"+DBResults.get("GUEST_NAME")+"  <b>Actual :</b> "+lname);
														}
														else
														{
															WSClient.writeToReport(LogStatus.FAIL, "lastName -> <b>Expected:</b>"+DBResults.get("GUEST_NAME")+"  <b>Actual :</b> "+lname);
														}

														nodeValues.clear();
														nodeValues.put("FetchQueueReservationsResponse_QueueDetails_QueueDatas", nodeNo);
														nodeValues.put("Guests_PersonName_firstName", 1);
														String fname=WSClient.getElementValueByIndex(fetchQueueResvRes, nodeValues, XMLType.RESPONSE);
														if(WSAssert.assertEquals(DBResults.get("GUEST_FIRST_NAME"),fname,true))
														{
															WSClient.writeToReport(LogStatus.PASS, "firstName -> <b>Expected:</b>"+DBResults.get("GUEST_FIRST_NAME")+"  <b>Actual :</b> "+fname);
														}
														else
														{
															WSClient.writeToReport(LogStatus.FAIL, "firstName -> <b>Expected:</b>"+DBResults.get("GUEST_FIRST_NAME")+"  <b>Actual :</b> "+fname);
														}

														nodeValues.clear();
														nodeValues.put("FetchQueueReservationsResponse_QueueDetails_QueueDatas", nodeNo);
														nodeValues.put("QueueDetails_QueueDatas_RoomStay_roomClass", 1);
														String roomClass=WSClient.getElementValueByIndex(fetchQueueResvRes, nodeValues, XMLType.RESPONSE);
														if(WSAssert.assertEquals(DBResults.get("ROOM_CLASS"),roomClass,true))
														{
															WSClient.writeToReport(LogStatus.PASS, "RoomStay@roomClass -> <b>Expected:</b>"+DBResults.get("ROOM_CLASS")+"  <b>Actual :</b> "+roomClass);
														}
														else
														{
															WSClient.writeToReport(LogStatus.FAIL, "RoomStay@roomClass -> <b>Expected:</b>"+DBResults.get("ROOM_CLASS")+"  <b>Actual :</b> "+roomClass);
														}

														nodeValues.clear();
														nodeValues.put("FetchQueueReservationsResponse_QueueDetails_QueueDatas", nodeNo);
														nodeValues.put("QueueDetails_QueueDatas_RoomStay_roomTypeCode", 1);
														String roomCategory=WSClient.getElementValueByIndex(fetchQueueResvRes, nodeValues, XMLType.RESPONSE);
														if(WSAssert.assertEquals(DBResults.get("ROOM_CATEGORY_LABEL"),roomCategory,true))
														{
															WSClient.writeToReport(LogStatus.PASS, "RoomStay@roomTypeCode -> <b>Expected:</b>"+DBResults.get("ROOM_CATEGORY_LABEL")+"  <b>Actual :</b> "+roomCategory);
														}
														else
														{
															WSClient.writeToReport(LogStatus.FAIL, "RoomStay@roomTypeCode -> <b>Expected:</b>"+DBResults.get("ROOM_CATEGORY_LABEL")+"  <b>Actual :</b> "+roomCategory);
														}

														nodeValues.clear();
														nodeValues.put("FetchQueueReservationsResponse_QueueDetails_QueueDatas", nodeNo);
														nodeValues.put("QueueDatas_RoomStay_RoomNumber", 1);
														String room=WSClient.getElementValueByIndex(fetchQueueResvRes, nodeValues, XMLType.RESPONSE);
														if(WSAssert.assertEquals(DBResults.get("ROOM"),room,true))
														{
															WSClient.writeToReport(LogStatus.PASS, "RoomNumber -> <b>Expected:</b>"+DBResults.get("ROOM")+"  <b>Actual :</b> "+room);
														}
														else
														{
															WSClient.writeToReport(LogStatus.FAIL, "RoomNumber -> <b>Expected:</b>"+DBResults.get("ROOM")+"  <b>Actual :</b> "+room);
														}

														nodeValues.clear();
														nodeValues.put("FetchQueueReservationsResponse_QueueDetails_QueueDatas", nodeNo);
														nodeValues.put("QueueDetails_QueueDatas_VipCode", 1);
														String vip=WSClient.getElementValueByIndex(fetchQueueResvRes, nodeValues, XMLType.RESPONSE);
														if(WSAssert.assertEquals(DBResults.get("VIP_STATUS"),vip,true))
														{
															WSClient.writeToReport(LogStatus.PASS, "VipCode -> <b>Expected:</b>"+DBResults.get("VIP_STATUS")+"  <b>Actual :</b> "+vip);
														}
														else
														{
															WSClient.writeToReport(LogStatus.FAIL, "VipCode -> <b>Expected:</b>"+DBResults.get("VIP_STATUS")+"  <b>Actual :</b> "+vip);
														}

														nodeValues.clear();
														nodeValues.put("FetchQueueReservationsResponse_QueueDetails_QueueDatas", nodeNo);
														nodeValues.put("QueueDatas_Guests_BusinessTitle", 1);
														String title=WSClient.getElementValueByIndex(fetchQueueResvRes, nodeValues, XMLType.RESPONSE);
														if(WSAssert.assertEquals(DBResults.get("TITLE"),title,true))
														{
															WSClient.writeToReport(LogStatus.PASS, "Title -> <b>Expected:</b>"+DBResults.get("TITLE")+"  <b>Actual :</b> "+title);
														}
														else
														{
															WSClient.writeToReport(LogStatus.FAIL, "Title -> <b>Expected:</b>"+DBResults.get("TITLE")+"  <b>Actual :</b> "+title);
														}

														nodeValues.clear();
														nodeValues.put("FetchQueueReservationsResponse_QueueDetails_QueueDatas", nodeNo);
														nodeValues.put("QueueDetails_QueueDatas_FrontOfficeStatus", 1);
														String foStatus=WSClient.getElementValueByIndex(fetchQueueResvRes, nodeValues, XMLType.RESPONSE);
														if(WSAssert.assertEquals(DBResults.get("FO_STATUS"),foStatus,true))
														{
															WSClient.writeToReport(LogStatus.PASS, "FrontOfficeStatus -> <b>Expected:</b>"+DBResults.get("FO_STATUS")+"  <b>Actual :</b> "+foStatus);
														}
														else
														{
															WSClient.writeToReport(LogStatus.FAIL, "FrontOfficeStatus -> <b>Expected:</b>"+DBResults.get("FO_STATUS")+"  <b>Actual :</b> "+foStatus);
														}

														nodeValues.clear();
														nodeValues.put("FetchQueueReservationsResponse_QueueDetails_QueueDatas", nodeNo);
														nodeValues.put("QueueDetails_QueueDatas_RoomStatus", 1);
														String roomStatus=WSClient.getElementValueByIndex(fetchQueueResvRes, nodeValues, XMLType.RESPONSE);
														if(WSAssert.assertEquals(DBResults.get("ROOM_STATUS"),roomStatus,true))
														{
															WSClient.writeToReport(LogStatus.PASS, "RoomStatus -> <b>Expected:</b>"+DBResults.get("ROOM_STATUS")+"  <b>Actual :</b> "+roomStatus);
														}
														else
														{
															WSClient.writeToReport(LogStatus.FAIL, "RoomStatus -> <b>Expected:</b>"+DBResults.get("ROOM_STATUS")+"  <b>Actual :</b> "+roomStatus);
														}
													}
													else if(WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","FAIL", true))
													{
														if(WSAssert.assertIfElementExists(fetchQueueResvRes, "Result_Text_TextElement", true))
														{
															WSClient.writeToReport(LogStatus.FAIL, "<b> The error text that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
														}
														if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", true))
														{
															WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
														}
													}
													else if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", true))
													{
														WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
													}
												}
												else
												{
													WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite Failed >> The requested reservation id did not get inserted into DB</b>");
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
										WSClient.writeToReport(LogStatus.WARNING,"<b> Pre-Requisite Failed >> Error generated is -"+ WSClient.getElementValue(queueResvRes, "AddReservationToQueueRS_Errors_Error_ShortText", XMLType.RESPONSE)+"</b>");
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
							WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> Reservation creation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> Profile creation failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite of Reservation creation failed");
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
				//WSClient.writeToReport(LogStatus.INFO, "<b>Executing Check In & Check Out Reservation to release room</b>");
				OPERALib.setOperaHeader(uname);
				if(!resvID.get("reservationId").equals("error"))
				{
					String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
					String checkInRes = WSClient.processSOAPMessage(checkInReq);

					if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true))
					{
						if(CheckoutReservation.checkOutReservation("DS_01"))
							WSClient.writeToLog("Reservation checking out successful");
						else
							WSClient.writeToLog("Reservation checking out failed");
					}
					else
					{
						WSClient.writeToLog("Reservation checking in failed");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				WSClient.writeToLog("Exception occured in finally block");
			}
		}
	}

	/**
	 * Method to check if the OWS Reservation Advanced Fetch Queue Reservation is working i.e.,
	 * queue reservations are not fetched when QUEUE ROOMS functionality is inactive.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * -->Reservation is pushed into the queue
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 *//*
	@Test(groups = { "minimumRegression", "FetchQueueReservations", "ResvAdvanced", "OWS"})

	public void fetchQueueReservation_39149() {
		String uname=null;
		try {
			String testName = "fetchQueueReservations_39149";
			WSClient.startTest(testName, "Verify that queue reservations are not fetched when QUEUE ROOMS functionality is inactive", "minimumRegression");

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
				WSClient.writeToReport(LogStatus.INFO, "<b>Enabling the parameter QUEUE ROOMS</b>");
				WSClient.setData("{var_settingValue}", "Y");
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{
	  *//************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************//*
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));


	   *//************
	   * Prerequisite 1: Create profile
	   *********************************//*
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID: "+profileID+"</b>");
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));

	    *//******************* Prerequisite 2:Create a Reservation ************************//*

						resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

	     *//******************* PreRequisite 3: Add Reservation to queue Operation************************//*
							String queueResvReq = WSClient.createSOAPMessage("AddReservationToQueue", "DS_01");
							String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

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
										String resvID=WSClient.getElementValue(queueResvReq, "AddReservationToQueueRQ_ReservationID_ID", XMLType.REQUEST);
										System.out.println(resvID+" "+ dbResults.get("RESV_NAME_ID"));
										if(WSAssert.assertEquals(resvID, dbResults.get("RESV_NAME_ID"), true))
										{
											WSClient.writeToReport(LogStatus.INFO, "<b>Disabling the parameter QUEUE ROOMS</b>");
											WSClient.setData("{var_settingValue}", "N");
											queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
											if(queueResvParameterEnabled.equals("N"))
											{
												OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

												String fetchQueueResvReq = WSClient.createSOAPMessage("OWSFetchQueueReservation", "DS_01");
												String fetchQueueResvRes = WSClient.processSOAPMessage(fetchQueueResvReq);
												if(WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","FAIL", true))
												{
													if(WSAssert.assertIfElementExists(fetchQueueResvRes, "Result_Text_TextElement", true))
													{
														WSClient.writeToReport(LogStatus.INFO, "<b> The error text that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
													}
													if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", true))
													{
														WSClient.writeToReport(LogStatus.INFO, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
													}
												}
												else if (WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","SUCCESS", true))
												{
													if(WSAssert.assertIfElementExists(fetchQueueResvRes,"FetchQueueReservationsResponse_QueueDetails_QueueDatas",true))
													{
														WSClient.writeToReport(LogStatus.FAIL, "Queue reservations are fetched although Queue Rooms functionality is inactive");
													}
													else
													{
														WSClient.writeToReport(LogStatus.INFO, "Queue reservations are not fetched as Queue Rooms functionality is inactive");
													}
												}
												else if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", true))
												{
													WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
												}
											}
											else
											{
												WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter Queue Rooms failed");
											}
										}
										else
										{
											WSClient.writeToReport(LogStatus.WARNING, "<b> The requested reservation id did not get inserted into DB</b>");
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
								WSClient.writeToReport(LogStatus.WARNING,"<b> Pre-Requisite Failed >> The error that is generated is : "+ WSClient.getElementValue(queueResvRes, "AddReservationToQueueRS_Errors_Error_ShortText", XMLType.RESPONSE)+"</b>");
							}
						}
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
	}*/

	/**
	 * Method to check if the OWS Reservation Advanced Fetch Queue Reservation is working i.e.,
	 * the reservation in the queue for the given resort
	 * based on the given room number.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * -->Reservation is pushed into the queue
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "FetchQueueReservations", "ResvAdvanced", "OWS"})

	public void fetchQueueReservation_39352() {
		String uname=null;
		try {
			String testName = "fetchQueueReservations_39352";
			WSClient.startTest(testName, "Verify that queue reservation for given room number is retrieved successfully", "minimumRegression");

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
					WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
					WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));

					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");

						/******************* Prerequisite 2:Create a Reservation ************************/
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							/*** Prerequisite 3: Fetching available Hotel rooms with room type  ***/

							String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
							String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);

							String roomNumber = null;
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
									WSClient.writeToReport(LogStatus.WARNING,"Pre-Requisite failure >> Unable to create room");
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
									/******************* PreRequisite 7: Add Reservation to queue Operation************************/
									String queueResvReq = WSClient.createSOAPMessage("AddReservationToQueue", "DS_01");
									String queueResvRes = WSClient.processSOAPMessage(queueResvReq);

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
												String resvID=WSClient.getElementValue(queueResvReq, "AddReservationToQueueRQ_ReservationID_ID", XMLType.REQUEST);
												System.out.println(resvID+" "+ dbResults.get("RESV_NAME_ID"));
												if(WSAssert.assertEquals(resvID, dbResults.get("RESV_NAME_ID"), true))
												{
													OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
													WSClient.writeToReport(LogStatus.PASS, "<b> ReservationId -> Expected : "+resvID+" Actual : "+dbResults.get("RESV_NAME_ID")+" </b>");

													String fetchQueueResvReq = WSClient.createSOAPMessage("OWSFetchQueueReservation", "DS_04");
													String fetchQueueResvRes = WSClient.processSOAPMessage(fetchQueueResvReq);
													if (WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","SUCCESS", false))
													{
														query=WSClient.getQuery("QS_04");
														ArrayList<LinkedHashMap<String, String>>  DBResults=WSClient.getDBRows(query);

														if(DBResults.size()==1)
														{
															LinkedHashMap<String, String> record=DBResults.get(0);
															WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "ReservationRequest_ReservationID_UniqueID", record.get("RESV_NAME_ID"), false);
															WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "QueueDatas_RoomStay_RoomNumber", record.get("ROOM"), false);
														}
														else
														{
															WSAssert.writeToReport(LogStatus.FAIL, "Selection criteria room number gives more than one record on the response");
														}
													}
													else if(WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","FAIL", true))
													{
														if(WSAssert.assertIfElementExists(fetchQueueResvRes, "Result_Text_TextElement", true))
														{
															WSClient.writeToReport(LogStatus.FAIL, "<b> The error text that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
														}
														if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", true))
														{
															WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
														}
													}
													else if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", true))
													{
														WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
													}
												}
												else
												{
													WSClient.writeToReport(LogStatus.WARNING, "<b> The requested reservation id did not get inserted into DB</b>");
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
										WSClient.writeToReport(LogStatus.WARNING,"<b> Pre-Requisite Failed >> Error generated is -"+ WSClient.getElementValue(queueResvRes, "AddReservationToQueueRS_Errors_Error_ShortText", XMLType.RESPONSE)+"</b>");
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
							WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> Reservation creation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> Profile creation failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisites of Reservation creation failed");
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
	 * Method to check if the OWS Reservation Advanced Fetch Queue Reservation is working i.e.,
	 * zero queue reservations are retrieved when room type with no queue reservations is given.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * -->Reservation is pushed into the queue
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "FetchQueueReservations", "ResvAdvanced", "OWS"})

	public void fetchQueueReservation_39652() {
		String uname=null;
		try {
			String testName = "fetchQueueReservations_39652";
			WSClient.startTest(testName, "Verify that zero reservations are retrieved when room type having no queue reservations is given", "minimumRegression");

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
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{
					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_09"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_08"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_08"));


					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));

						/******************* Prerequisite 2:Create a Reservation ************************/

						resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							String query=WSClient.getQuery("OWSFetchQueueReservation","QS_02");
							ArrayList<LinkedHashMap<String, String>>  DBResults=WSClient.getDBRows(query);
							if(DBResults.size()==0)
							{
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

								String fetchQueueResvReq = WSClient.createSOAPMessage("OWSFetchQueueReservation", "DS_02");
								String fetchQueueResvRes = WSClient.processSOAPMessage(fetchQueueResvReq);
								if (WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","SUCCESS", false))
								{
									WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_QueueTime_TotalRooms", "0", false);
								}
								else if(WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","FAIL", true))
								{
									if(WSAssert.assertIfElementExists(fetchQueueResvRes, "Result_Text_TextElement", true))
									{
										WSClient.writeToReport(LogStatus.FAIL, "<b> The error text that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
									}
									if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", true))
									{
										WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
									}
								}
								else if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", true))
								{
									WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
								}
							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> Room type has queue rooms");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> Reservation creation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> Profile creation failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite of Reservation creation failed");
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
	 * Method to check if the OWS Reservation Advanced Fetch Queue Reservation is working i.e.,
	 * zero queue reservations are retrieved when room class with no queue reservations is given.
	 *
	 * PreRequisites Required: -->Profile is created -->Reservation is created
	 * -->Reservation is pushed into the queue
	 *
	 * ->Room Type ->Rate Code and Room Type attached to it ->Market Code
	 * ->Source Code
	 */
	@Test(groups = { "minimumRegression", "FetchQueueReservations", "ResvAdvanced", "OWS"})

	public void fetchQueueReservation_49652() {
		String uname=null;
		try {
			String testName = "fetchQueueReservations_49652";
			WSClient.startTest(testName, "Verify that zero reservations are retrieved when room class having no queue reservations is given", "minimumRegression");

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
				queueResvParameterEnabled=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}
			if(queueResvParameterEnabled.equals("Y"))
			{
				if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode"}))
				{
					/************* Prerequisite : Room type, Rate Plan Code, Source Code, Market Code *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_09"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_08"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_roomClass}", OperaPropConfig.getDataSetForCode("RoomClass", "DS_04"));

					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					if(profileID.equals(""))
						profileID=CreateProfile.createProfile("DS_01");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : "+profileID+"</b>");
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));

						/******************* Prerequisite 2:Create a Reservation ************************/

						resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.get("reservationId").equals("error"))
						{
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID: "+resvID.get("reservationId")+"</b>");

							String query=WSClient.getQuery("OWSFetchQueueReservation","QS_03");
							System.out.println(query);
							ArrayList<LinkedHashMap<String, String>>  DBResults=WSClient.getDBRows(query);
							if(DBResults.size()==0)
							{
								OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);

								String fetchQueueResvReq = WSClient.createSOAPMessage("OWSFetchQueueReservation", "DS_03");
								String fetchQueueResvRes = WSClient.processSOAPMessage(fetchQueueResvReq);
								if (WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","SUCCESS", false))
								{
									WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_QueueTime_TotalRooms", "0", false);
								}
								else if(WSAssert.assertIfElementValueEquals(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_resultStatusFlag","FAIL", true))
								{
									if(WSAssert.assertIfElementExists(fetchQueueResvRes, "Result_Text_TextElement", true))
									{
										WSClient.writeToReport(LogStatus.FAIL, "<b> The error text that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
									}
									if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", true))
									{
										WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationsResponse_Result_OperaErrorCode", XMLType.RESPONSE)+"</b>");
									}
								}
								else if(WSAssert.assertIfElementExists(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", true))
								{
									WSClient.writeToReport(LogStatus.FAIL, "<b> The error code that is generated is : "+WSClient.getElementValue(fetchQueueResvRes, "FetchQueueReservationResponse_faultstring", XMLType.RESPONSE)+"</b>");
								}
							}
							else
							{
								WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> Room class has queue rooms");
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> Reservation creation failed");
						}
					}
					else
					{
						WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite failed >> Profile creation failed");
					}
				}
				else
				{
					WSClient.writeToReport(LogStatus.WARNING, "<b> Pre-Requisite of Reservation creation failed");
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
}

