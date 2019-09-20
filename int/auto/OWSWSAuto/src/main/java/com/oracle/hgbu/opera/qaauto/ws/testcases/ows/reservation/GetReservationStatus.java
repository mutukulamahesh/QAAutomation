package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CheckoutReservation;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;
public class GetReservationStatus extends WSSetUp {
		String reservationId="",reservationId1="";
		@Test(groups = { "sanity","GetReservationStatus" ,"Reservation","OWS","in-QA"})
	public void getReservationStatus_38695() throws Exception
	{
		try{
			String testName = "getReservationStatus_38695";
			WSClient.startTest(testName, "Verify that the  ReservationStatus is populated correctly" ,"sanity");

			String interfaceName = OWSLib.getChannel();	
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String Channel = OWSLib.getChannel();
			String ChannelType = OWSLib.getChannelType(Channel);
			String ChannelCarier = OWSLib.getChannelCarier(resortOperaValue, Channel);
			String username = OPERALib.getUserName();
			String pswd = OPERALib.getPassword();

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			
			OPERALib.setOperaHeader(username);
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);
			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true)) {
				if (WSAssert.assertIfElementExists(createProfileResponseXML,
						"CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {
					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
							"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
					WSClient.setData("{var_profileId}", operaProfileID);
					if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"}))
					{     
							
				           WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				           WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				           WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				           WSClient.setData("{var_sourceCode}",OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));					
					
				           
				           String createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_01");
							String createResvRes = WSClient.processSOAPMessage(createResvReq);
		
							if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success", true)) 
							{
		
								 reservationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Reservation", XMLType.RESPONSE);
														
								String ConfirmationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Confirmation", XMLType.RESPONSE);
								WSClient.setData("{var_confirmationId}", ConfirmationId);
																
								if (reservationId != null && reservationId != "") 
								{
									OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
									String getStatusReq = WSClient.createSOAPMessage("OWSGetReservationStatus", "DS_01");
									String getStatusRes = WSClient.processSOAPMessage(getStatusReq);
									if (WSAssert.assertIfElementExists(getStatusRes, "Result_Text_TextElement", true)) {

										WSClient.writeToReport(LogStatus.INFO, "<b>Error in response is:</b>"+WSClient.getElementValue(getStatusRes,
												"Result_Text_TextElement", XMLType.RESPONSE));

									}
									if (WSAssert.assertIfElementExists(getStatusRes,
											"GetReservationStatusResponse_Result_GDSError", true)) {
									
									
										WSClient.writeToReport(LogStatus.INFO,"<b>Error in the response is:</b>"+ WSClient.getElementValue(getStatusRes,
												"GetReservationStatusResponse_Result_GDSError", XMLType.RESPONSE));

									}
									
									if(WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_Result_resultStatusFlag", "SUCCESS", false))
									{
										
										LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
										String query=WSClient.getQuery("QS_01");
										db = WSClient.getDBRow(query);
																												
										WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_ReservationStatus",db.get("ReservationStatus1"), false);
										WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_ConfirmationNumber",db.get("ConfirmationNumber1"), false);
									}	
																
									
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Reservation ID is not created");
								}
							}
					else 
					{
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
					}

				} else{
					
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
				}
				}
					
					
					else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Pre-requisite----------profile ID is not created---------------");

				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite 1  -----------CreateProfile Failed----------");

			}			
		
		}
		catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
			e.printStackTrace();
		}
		finally {
			WSClient.setData("{var_resvId}", reservationId);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!reservationId.equals("error"))
				CancelReservation.cancelReservation("DS_02");
		}
	}
	
	@Test(groups = { "minimumRegression","GetReservationStatus" ,"Reservation","OWS","in-QA"})
	
	public void getReservationStatus_38700() throws Exception
	{
		try{
			String testName = "getReservationStatus_38700";
			WSClient.startTest(testName, "Verify that the  ReservationStatus is populated correctly when legNumber is passed" ,"minimumRegression");

			String interfaceName = OWSLib.getChannel();	
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String Channel = OWSLib.getChannel();
			String ChannelType = OWSLib.getChannelType(Channel);
			String ChannelCarier = OWSLib.getChannelCarier(resortOperaValue, Channel);
			String username = OPERALib.getUserName();
			String pswd = OPERALib.getPassword();

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			
			OPERALib.setOperaHeader(username);
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);
			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true)) {
				if (WSAssert.assertIfElementExists(createProfileResponseXML,
						"CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {
					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
							"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
					WSClient.setData("{var_profileId}", operaProfileID);
					if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"}))
					{     
							
				           WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				           WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				           WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				           WSClient.setData("{var_sourceCode}",OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));					
					
				           
				           String createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_01");
							String createResvRes = WSClient.processSOAPMessage(createResvReq);
		
							if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success", true)) 
							{
		
								 reservationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Reservation", XMLType.RESPONSE);
														
								String ConfirmationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Confirmation", XMLType.RESPONSE);
								WSClient.setData("{var_confirmationId}", ConfirmationId);
								
																
								if (reservationId != null && reservationId != "") 
									
								{
									createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_10");
									 createResvRes = WSClient.processSOAPMessage(createResvReq);
									 reservationId1 = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Reservation", XMLType.RESPONSE);
										
									 if(reservationId1!=null){
									OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
									WSClient.setData("{var_leg}", "2");
									String getStatusReq = WSClient.createSOAPMessage("OWSGetReservationStatus", "DS_06");
									String getStatusRes = WSClient.processSOAPMessage(getStatusReq);
									if (WSAssert.assertIfElementExists(getStatusRes, "Result_Text_TextElement", true)) {

										WSClient.writeToReport(LogStatus.INFO, "<b>Error in response is:</b>"+WSClient.getElementValue(getStatusRes,
												"Result_Text_TextElement", XMLType.RESPONSE));

									}
									if (WSAssert.assertIfElementExists(getStatusRes,
											"GetReservationStatusResponse_Result_GDSError", true)) {
									
									
										WSClient.writeToReport(LogStatus.INFO,"<b>Error in the response is:</b>"+ WSClient.getElementValue(getStatusRes,
												"GetReservationStatusResponse_Result_GDSError", XMLType.RESPONSE));

									}
									
									if(WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_Result_resultStatusFlag", "SUCCESS", false))
									{
										
										LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
										String query=WSClient.getQuery("QS_05");
										db = WSClient.getDBRow(query);
																												
										WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_ReservationStatus",db.get("ReservationStatus1"), false);
										WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_ConfirmationNumber",db.get("ConfirmationNumber1"), false);
									}	
																
									
								}else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Reservation ID is not created");
								}
									
									 
								}
								
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Reservation ID is not created");
								}
							}
					else 
					{
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
					}

				} else{
					
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
				}
				}
					
					
					else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Pre-requisite----------profile ID is not created---------------");

				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite 1  -----------CreateProfile Failed----------");

			}			
		
		}
		catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
			e.printStackTrace();
		}
		finally {
			WSClient.setData("{var_resvId}", reservationId);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!reservationId.equals("error"))
				CancelReservation.cancelReservation("DS_02");
			WSClient.setData("{var_resvId}", reservationId);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!reservationId.equals("error"))
				CancelReservation.cancelReservation("DS_02");
		}
	}
	
	@Test(groups = { "minimumRegression","GetReservationStatus" ,"Reservation","OWS","in-QA"})
	public void getReservationStatus_38698() throws Exception
	{
		try{
			String testName = "getReservationStatus_38698";
			WSClient.startTest(testName, "Verifying all the details in the response when confirmationId is passed in the request", "minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String Channel = OWSLib.getChannel();
			String ChannelType = OWSLib.getChannelType(Channel);
			String ChannelCarier = OWSLib.getChannelCarier(resortOperaValue, Channel);
			String username = OPERALib.getUserName();
			String pswd = OPERALib.getPassword();

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			
			OPERALib.setOperaHeader(username);
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);
			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true)) {
				if (WSAssert.assertIfElementExists(createProfileResponseXML,
						"CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {
					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
							"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
					WSClient.setData("{var_profileId}", operaProfileID);
					if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"}))
					{     
							System.out.println("before config details");
				           WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				           WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				           WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				           WSClient.setData("{var_sourceCode}",OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));					
					
				          
				           String createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_01");
							String createResvRes = WSClient.processSOAPMessage(createResvReq);
		
							if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success", true)) 
							{
		
								 reservationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Reservation", XMLType.RESPONSE);
														
								String ConfirmationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Confirmation", XMLType.RESPONSE);
								WSClient.setData("{var_confirmationId}", ConfirmationId);
								System.out.println(reservationId);
								
								if (reservationId != null && reservationId != "") 
								{
									OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
									String getStatusReq = WSClient.createSOAPMessage("OWSGetReservationStatus", "DS_01");
									String getStatusRes = WSClient.processSOAPMessage(getStatusReq);
									if (WSAssert.assertIfElementExists(getStatusRes, "Result_Text_TextElement", true)) {

										WSClient.writeToReport(LogStatus.INFO, "<b>Error in response is:</b>"+WSClient.getElementValue(getStatusRes,
												"Result_Text_TextElement", XMLType.RESPONSE));

									}
									if (WSAssert.assertIfElementExists(getStatusRes,
											"GetReservationStatusResponse_Result_GDSError", true)) {
									
									
										WSClient.writeToReport(LogStatus.INFO,"<b>Error in the response is:</b>"+ WSClient.getElementValue(getStatusRes,
												"GetReservationStatusResponse_Result_GDSError", XMLType.RESPONSE));

									}
									
									if(WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_Result_resultStatusFlag", "SUCCESS", false))
									{
										
										LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
											String query=WSClient.getQuery("QS_02");
											db = WSClient.getDBRow(query);
										
										HashMap<String, String> xpath = new HashMap<String, String>();
										LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
										xpath.put("GetReservationStatusResponse_ConfirmationNumber","_GetReservationStatusResponse");
										xpath.put("GetReservationStatusResponse_FirstName","_GetReservationStatusResponse");
										xpath.put("GetReservationStatusResponse_ReservationStatus","_GetReservationStatusResponse");
										xpath.put("GetReservationStatusResponse_LastName", "_GetReservationStatusResponse");
										xpath.put("GetReservationStatusResponse_StayDateRange_StartDate", "GetReservationStatusResponse_StayDateRange");
										xpath.put("GetReservationStatusResponse_StayDateRange_EndDate", "GetReservationStatusResponse_StayDateRange");
										actualValues=WSClient.getSingleNodeList(getStatusRes,xpath, false, XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>Validating response with Database</b>");
										
										WSAssert.assertEquals(db, actualValues, false);															
																				
									}	
																
									
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Reservation ID is not created");
								}
							}
					else 
					{
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
					}

				} else{
					
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
				}
				}
					
					
					else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Pre-requisite----------profile ID is not created---------------");

				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite 1  -----------CreateProfile Failed----------");

			}			
		
		}
		catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
		finally {
			WSClient.setData("{var_resvId}", reservationId);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!reservationId.equals("error"))
				CancelReservation.cancelReservation("DS_02");
		}
}
	
	@Test(groups = { "minimumRegression","GetReservationStatus" ,"Reservation","OWS","in-QA"})
	public void getReservationStatus_38696() throws Exception
	{
		try{
			String testName = "getReservationStatus_38696";
			WSClient.startTest(testName, "Verifying that the ReservationStatus is cancelled if a cancelled id is passed in the request", "minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String Channel = OWSLib.getChannel();
			String ChannelType = OWSLib.getChannelType(Channel);
			String ChannelCarier = OWSLib.getChannelCarier(resortOperaValue, Channel);
			String username = OPERALib.getUserName();
			String pswd = OPERALib.getPassword();

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			
			OPERALib.setOperaHeader(username);
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);
			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true)) {
				if (WSAssert.assertIfElementExists(createProfileResponseXML,
						"CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {
					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
							"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
					WSClient.setData("{var_profileId}", operaProfileID);
					if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"}))
					{     
							
				           WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				           WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				           WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				           WSClient.setData("{var_sourceCode}",OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));					
					
				           
				           String createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_01");
							String createResvRes = WSClient.processSOAPMessage(createResvReq);
		
							if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success", true)) 
							{
		
								 reservationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Reservation", XMLType.RESPONSE);
														
								String ConfirmationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Confirmation", XMLType.RESPONSE);
								WSClient.setData("{var_confirmationId}", ConfirmationId);
								System.out.println(reservationId);
								WSClient.setData("{var_reservation_id}",reservationId);
								
								if (reservationId != null && reservationId != "") 
								{
									String cancelReservationReq = WSClient.createSOAPMessage("CancelReservation",
											"DS_01");
									String cancelReservationRes = WSClient.processSOAPMessage(cancelReservationReq);
									if (WSAssert.assertIfElementExists(cancelReservationRes,
											"CancelReservationRS_Success", true)) {

									OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
									String getStatusReq = WSClient.createSOAPMessage("OWSGetReservationStatus", "DS_01");
									String getStatusRes = WSClient.processSOAPMessage(getStatusReq);
									if (WSAssert.assertIfElementExists(getStatusRes, "Result_Text_TextElement", true)) {

										WSClient.writeToReport(LogStatus.INFO, "<b>Error in response is:</b>"+WSClient.getElementValue(getStatusRes,
												"Result_Text_TextElement", XMLType.RESPONSE));

									}
									if (WSAssert.assertIfElementExists(getStatusRes,
											"GetReservationStatusResponse_Result_GDSError", true)) {
									
									
										WSClient.writeToReport(LogStatus.INFO,"<b>Error in the response is:</b>"+ WSClient.getElementValue(getStatusRes,
												"GetReservationStatusResponse_Result_GDSError", XMLType.RESPONSE));

									}
									
									if(WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_Result_resultStatusFlag", "SUCCESS", false))
									{
										
										LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
										String query=WSClient.getQuery("QS_01");
										db = WSClient.getDBRow(query);
										
										WSClient.writeToReport(LogStatus.INFO, "<b>Validating response with Database</b>");
										
										WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_ReservationStatus",db.get("ReservationStatus1"), false);
										WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_ConfirmationNumber",db.get("ConfirmationNumber1"), false);
																								
																				
									}	
																
									
								}
									else
									{
										WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Reservation is not cancelled");
									}
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Reservation ID is not created");
								}
							}
					else 
					{
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
					}

				} else{
					
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
				}
				}
					
					
					else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Pre-requisite----------profile ID is not created---------------");

				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite 1  -----------CreateProfile Failed----------");

			}			
		
		}
		catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
		finally {
			WSClient.setData("{var_resvId}", reservationId);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!reservationId.equals("error"))
				CancelReservation.cancelReservation("DS_02");
		}
	}
	
	
	@Test(groups = { "minimumRegression","GetReservationStatus" ,"Reservation","OWS","in-QA"})
	public void getReservationStatus_38697() throws Exception
	{
		try{
			String testName = "getReservationStatus_38697";
			WSClient.startTest(testName, "verifying that ReservationStatus when checked-in Id is passed in the request ", "minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String Channel = OWSLib.getChannel();
			String ChannelType = OWSLib.getChannelType(Channel);
			String ChannelCarier = OWSLib.getChannelCarier(resortOperaValue, Channel);
			String username = OPERALib.getUserName();
			String pswd = OPERALib.getPassword();
			
			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String roomNumber="";
			OPERALib.setOperaHeader(username);
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);
			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true)) {
				if (WSAssert.assertIfElementExists(createProfileResponseXML,
						"CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {
					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
							"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
					WSClient.setData("{var_profileId}", operaProfileID);
					if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"}))
					{     
							
				           WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				           WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				           WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				           WSClient.setData("{var_sourceCode}",OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));	
				           WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				           WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				           WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				           
				           String createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_12");
							String createResvRes = WSClient.processSOAPMessage(createResvReq);
		
							if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success", true)) 
							{
								
								 reservationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Reservation", XMLType.RESPONSE);
														
								String ConfirmationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Confirmation", XMLType.RESPONSE);
								WSClient.setData("{var_confirmationId}", ConfirmationId);
								System.out.println(reservationId);
								WSClient.setData("{var_reservation_id}",reservationId);
								WSClient.setData("{var_resvId}",reservationId);
								if (reservationId != null && reservationId != "") 
								{
									String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", "DS_03");
									String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);
												
									if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true)) {
									     if (WSAssert.assertIfElementExists(fetchHotelRoomsRes,"FetchHotelRoomsRS_HotelRooms_Room", true)) {
														
									    	 roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
														
													}
												}
									if(roomNumber.equals("")){

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
												
									  WSClient.setData("{var_roomNumber}", roomNumber);
									
									  //Prerequisite 5: Changing the room status to inspected to assign the room for checking in

										String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", "DS_01");
										String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);
													
										if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes,"SetHousekeepingRoomStatusRS_Success", true)) {

												// Prerequisite 6: Assign Room

										String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");
			                            String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);
			                                
			                            if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",true)){
									  
									  
									  
									  String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
										String checkInRes = WSClient.processSOAPMessage(checkInReq);
										  
								if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true))
															
									 {

									OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
									String getStatusReq = WSClient.createSOAPMessage("OWSGetReservationStatus", "DS_01");
									String getStatusRes = WSClient.processSOAPMessage(getStatusReq);
									if (WSAssert.assertIfElementExists(getStatusRes, "Result_Text_TextElement", true)) {

										WSClient.writeToReport(LogStatus.INFO, "<b>Error in response is:</b>"+WSClient.getElementValue(getStatusRes,
												"Result_Text_TextElement", XMLType.RESPONSE));

									}
									if (WSAssert.assertIfElementExists(getStatusRes,
											"GetReservationStatusResponse_Result_GDSError", true)) {
									
									
										WSClient.writeToReport(LogStatus.INFO,"<b>Error in the response is:</b>"+ WSClient.getElementValue(getStatusRes,
												"GetReservationStatusResponse_Result_GDSError", XMLType.RESPONSE));

									}
									
									if(WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_Result_resultStatusFlag", "SUCCESS", false))
									{
										
										LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
										String query=WSClient.getQuery("QS_01");
										db = WSClient.getDBRow(query);
																													
										WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_ReservationStatus",db.get("ReservationStatus1"), false);
										WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_ConfirmationNumber",db.get("ConfirmationNumber1"), false);
																				
									}	
																
									
								}
									else
									{
										WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Reservation is not checked-in");
									}
								}
			                          else
										{
											WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Room is not assigned");
										}
								}
										else
										{
											WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to change the status of room to vacant and inspected");
										}
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Reservation ID is not created");
								}
							}
					else 
					{
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
					}

				} else{
					
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
				}
				}				
					
					else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Pre-requisite----------profile ID is not created---------------");

				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite 1  -----------CreateProfile Failed----------");

			}			
		
		}
		catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
		finally {
			WSClient.setData("{var_resvId}", reservationId);
			
			if (!reservationId.equals("error"))
				CheckoutReservation.checkOutReservation("DS_01");
		}
	}
	
	
		
		
	

	
	@Test(groups = { "minimumRegression","GetReservationStatus" ,"Reservation","OWS","in-QA"})
	public void getReservationStatus_38699() throws Exception
	{
		try{
			String testName = "getReservationStatus_38699";
			WSClient.startTest(testName, "verifying the response when invalid name is passed in the resquest", "minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String Channel = OWSLib.getChannel();
			String ChannelType = OWSLib.getChannelType(Channel);
			String ChannelCarier = OWSLib.getChannelCarier(resortOperaValue, Channel);
			String username = OPERALib.getUserName();
			String pswd = OPERALib.getPassword();

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_fname}", firstName);
			WSClient.setData("{var_lname}", lastName);
			String InvalidfirstName ="invalidName";
			WSClient.setData("{var_Ifname}",InvalidfirstName);
			
			OPERALib.setOperaHeader(username);
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_05");
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);
			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true)) {
				if (WSAssert.assertIfElementExists(createProfileResponseXML,
						"CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {
					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
							"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
					WSClient.setData("{var_profileId}", operaProfileID);
					if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"}))
					{     
							
				           WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				           WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				           WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				           WSClient.setData("{var_sourceCode}",OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));					
					
				           String createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_01");
							String createResvRes = WSClient.processSOAPMessage(createResvReq);
		
							if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success", true)) 
							{
		
								 reservationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Reservation", XMLType.RESPONSE);
														
								String ConfirmationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Confirmation", XMLType.RESPONSE);
								WSClient.setData("{var_confirmationId}", ConfirmationId);
								System.out.println(reservationId);
								
								if (reservationId != null && reservationId != "") 
								{
									OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
									String getStatusReq = WSClient.createSOAPMessage("OWSGetReservationStatus", "DS_02");
									String getStatusRes = WSClient.processSOAPMessage(getStatusReq);
									
									WSAssert.writeToReport(LogStatus.INFO,"Passing invalid name in the request");
									WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_Result_resultStatusFlag", "FAIL", false);
									
									if (WSAssert.assertIfElementExists(getStatusRes, "Result_Text_TextElement", true)) {

										WSClient.writeToReport(LogStatus.INFO, "<b>Error in response is:</b>"+WSClient.getElementValue(getStatusRes,
												"Result_Text_TextElement", XMLType.RESPONSE));

									}
									if (WSAssert.assertIfElementExists(getStatusRes,
											"GetReservationStatusResponse_Result_GDSError", true)) {
									
									
										WSClient.writeToReport(LogStatus.INFO,"<b>Error in the response is:</b>"+ WSClient.getElementValue(getStatusRes,
												"GetReservationStatusResponse_Result_GDSError", XMLType.RESPONSE));

									}
									
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Reservation ID is not created");
								}
							}
					else 
					{
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
					}

				} else{
					
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
				}
				}
					
					
					else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Pre-requisite----------profile ID is not created---------------");

				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite 1  -----------CreateProfile Failed----------");

			}			
		
		}
		catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
		finally {
			WSClient.setData("{var_resvId}", reservationId);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!reservationId.equals("error"))
				CancelReservation.cancelReservation("DS_02");
		}
	}
	
//	@Test(groups = { "minimumRegression","GetReservationStatus" ,"Reservation","OWS","in-QA"})
//	public void getReservationStatus_incom()
//	{
//		try{
//			String testName = "getReservationStatus_incom";
//			WSClient.startTest(testName, "Verifying the response when confimationID and fname are passed in the request", "minimumRegression");
//
//			String interfaceName = OWSLib.getChannel();
//			String resortOperaValue = OPERALib.getResort();
//			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
//			String Channel = OWSLib.getChannel();
//			String ChannelType = OWSLib.getChannelType(Channel);
//			String ChannelCarier = OWSLib.getChannelCarier(resortExtValue, Channel);
//			String username = OPERALib.getUserName();
//			String pswd = OPERALib.getPassword();
//
//			WSClient.setData("{var_profileSource}", interfaceName);
//			WSClient.setData("{var_resort}", resortOperaValue);
//			WSClient.setData("{var_extResort}", resortExtValue);
//			String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
//			String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
//			WSClient.setData("{var_fname}", firstName);
//			WSClient.setData("{var_lname}", lastName);
//			
//			OPERALib.setOperaHeader(username);
//			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_05");
//			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);
//			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true)) {
//				if (WSAssert.assertIfElementExists(createProfileResponseXML,
//						"CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {
//					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
//							"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
//					WSClient.setData("{var_profileId}", operaProfileID);
//					if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"}))
//					{     
//							System.out.println("before config details");
//				           WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
//				           WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
//				           WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
//				           WSClient.setData("{var_sourceCode}",OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));					
//					
//				           	String createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_01");
//							String createResvRes = WSClient.processSOAPMessage(createResvReq);
//		
//							if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success", true)) 
//							{							
//										
//								String reservationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Reservation", XMLType.RESPONSE);
//														
//															
//								if (reservationId != null && reservationId != "") 
//								{
//									createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_01");
//									createResvRes = WSClient.processSOAPMessage(createResvReq);							
//									
//									reservationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Reservation", XMLType.RESPONSE);
//									
//									String ConfirmationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Confirmation", XMLType.RESPONSE);
//									WSClient.setData("{var_confirmationId}", ConfirmationId);		
//									
//									OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
//									String getStatusReq = WSClient.createSOAPMessage("OWSGetReservationStatus", "DS_03");
//									String getStatusRes = WSClient.processSOAPMessage(getStatusReq);
//									if (WSAssert.assertIfElementExists(getStatusRes, "Result_Text_TextElement", true)) {
//
//										WSClient.writeToReport(LogStatus.INFO, "<b>Error in response is:</b>"+WSClient.getElementValue(getStatusRes,
//												"Result_Text_TextElement", XMLType.RESPONSE));
//
//								}
//									if (WSAssert.assertIfElementExists(getStatusRes,
//											"GetReservationStatusResponse_Result_GDSError", true)) {
//									
//									
//										WSClient.writeToReport(LogStatus.INFO,"<b>Error in the response is:</b>"+ WSClient.getElementValue(getStatusRes,
//												"GetReservationStatusResponse_Result_GDSError", XMLType.RESPONSE));
//
//									}
//									
////									if(WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_Result_resultStatusFlag", "SUCCESS", false))
////									{
////										
////										LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
////										String query=WSClient.getQuery("QS_04");
////										db = WSClient.getDBRow(query);										
////										HashMap<String, String> xpath = new HashMap<String, String>();
////										LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
////										xpath.put("GetReservationStatusResponse_ConfirmationNumber","_GetReservationStatusResponse");
////										xpath.put("GetReservationStatusResponse_ReservationStatus","_GetReservationStatusResponse");
////										xpath.put("GetReservationStatusResponse_FirstName","_GetReservationStatusResponse");
////										xpath.put("GetReservationStatusResponse_LastName","_GetReservationStatusResponse");
////										actualValues=WSClient.getSingleNodeList(getStatusRes,xpath, false, XMLType.RESPONSE);
////										WSClient.writeToReport(LogStatus.INFO, "<b>Validating response with Database</b>");										
////										WSAssert.assertEquals(db, actualValues, false);	
//////										WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_ReservationStatus",db.get("ReservationStatus1"), false);
//////										WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_ConfirmationNumber",db.get("ConfirmationNumber1"), false);
//////										WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_FirstName",db.get("FirstName1"), false);
//////										WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_LastName",db.get("LastName1"), false);
////																				
////									}	
//																
//									if(WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_Result_resultStatusFlag", "SUCCESS", false))
//									{
//										
//										List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();
//											String query=WSClient.getQuery("QS_04");
//											db = WSClient.getDBRows(query);
//										
//										HashMap<String, String> xpath = new HashMap<String, String>();
//										List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
//										xpath.put("GetReservationStatusResponse_ConfirmationNumber","GetReservationStatusResponse_ConfirmationNumber");
//										xpath.put("GetReservationStatusResponse_FirstName","GetReservationStatusResponse_FirstName");
//										xpath.put("GetReservationStatusResponse_ReservationStatus","GetReservationStatusResponse_ReservationStatus");
//										xpath.put("GetReservationStatusResponse_LastName", "GetReservationStatusResponse_LastName");
////										xpath.put("GetReservationStatusResponse_StayDateRange_StartDate", "GetReservationStatusResponse_StayDateRange");
////										xpath.put("GetReservationStatusResponse_StayDateRange_EndDate", "GetReservationStatusResponse_StayDateRange");
//										actualValues=WSClient.getMultipleNodeList(getStatusRes,xpath, false, XMLType.RESPONSE);
//										WSClient.writeToReport(LogStatus.INFO, "<b>Validating response with Database</b>");
//										
//										WSAssert.assertEquals( actualValues,db, false);															
//																				
//									}	
//								}
//								else
//								{
//									WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Reservation ID is not created");
//								}
//							}
//					else 
//					{
//						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
//					}
//
//				} else{
//					
//					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
//				}
//				}
//					
//					
//					else {
//					WSClient.writeToReport(LogStatus.WARNING,
//							"Pre-requisite----------profile ID is not created---------------");
//
//				}
//
//			} else {
//				WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite 1  -----------CreateProfile Failed----------");
//
//			}			
//		
//		}
//		catch(Exception e){
//			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
//		}
//	}
//	
	@Test(groups = { "minimumRegression","GetReservationStatus" ,"Reservation","OWS","in-QA"})
	public void getReservationStatus_38701() throws Exception
	{
		try{
			String testName = "getReservationStatus_38701";
			WSClient.startTest(testName, "Verifying that error message is populated on the response when same comfimation ID associated with multiple profiles is passed","minimumRegression");

			String interfaceName = OWSLib.getChannel();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
			String Channel = OWSLib.getChannel();
			String ChannelType = OWSLib.getChannelType(Channel);
			String ChannelCarier = OWSLib.getChannelCarier(resortOperaValue, Channel);
			String username = OPERALib.getUserName();
			String pswd = OPERALib.getPassword();

			WSClient.setData("{var_profileSource}", interfaceName);
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
			String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
			WSClient.setData("{var_fname}", firstName);
			WSClient.setData("{var_lname}", lastName);
			
			OPERALib.setOperaHeader(username);
			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_05");
			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);
			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true)) {
				if (WSAssert.assertIfElementExists(createProfileResponseXML,
						"CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {
					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
							"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
					WSClient.setData("{var_profileId}", operaProfileID);
					
					createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_05");
					 createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);
					 operaProfileID = WSClient.getElementValue(createProfileResponseXML,
								"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
						WSClient.setData("{var_profileId2}", operaProfileID);
					
					
					if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"}))
					{     
							
				           WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				           WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				           WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
				           WSClient.setData("{var_sourceCode}",OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));					
					
				           	String createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_01");
							String createResvRes = WSClient.processSOAPMessage(createResvReq);
							
							
		
							if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success", true)) 
							{			
													
										
								 reservationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Reservation", XMLType.RESPONSE);
														
															
								if (reservationId != null && reservationId != "") 
								{																	
									String ConfirmationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Confirmation", XMLType.RESPONSE);
									WSClient.setData("{var_confirmationId}", ConfirmationId);										
									String createResvReq2 = WSClient.createSOAPMessage("CreateReservation", "DS_10");
									String createResvRes2 = WSClient.processSOAPMessage(createResvReq2);
									if(WSAssert.assertIfElementExists(createResvRes2, "CreateReservationRS_Success", false));										
									OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
									String getStatusReq = WSClient.createSOAPMessage("OWSGetReservationStatus", "DS_01");
									String getStatusRes = WSClient.processSOAPMessage(getStatusReq);
									if (WSAssert.assertIfElementExists(getStatusRes, "Result_Text_TextElement", true)) {

										WSClient.writeToReport(LogStatus.INFO, "<b>Error in response is:</b>"+WSClient.getElementValue(getStatusRes,
												"Result_Text_TextElement", XMLType.RESPONSE));

								}
									if (WSAssert.assertIfElementExists(getStatusRes,
											"GetReservationStatusResponse_Result_GDSError", true)) {
									
									
										WSClient.writeToReport(LogStatus.INFO,"<b>Error in the response is:</b>"+ WSClient.getElementValue(getStatusRes,
												"GetReservationStatusResponse_Result_GDSError", XMLType.RESPONSE));

									}
									
									WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_Result_resultStatusFlag", "FAIL", false);
																								
									
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Reservation ID is not created");
								}
							}
					else 
					{
						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
					}

				} else{
					
					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
				}
				}
					
					
					else {
					WSClient.writeToReport(LogStatus.WARNING,
							"Pre-requisite----------profile ID is not created---------------");

				}

			} else {
				WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite 1  -----------CreateProfile Failed----------");

			}			
		
		}
		catch(Exception e){
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
		finally {
			WSClient.setData("{var_resvId}", reservationId);
			WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
			if (!reservationId.equals("error"))
				CancelReservation.cancelReservation("DS_02");
		}
	}
	
	
	
	
	
//	@Test(groups = { "incomplete","GetReservationStatus" ,"Reservation","OWS","in-QA"})
//	public void getReservationStatus_38702()
//	{
//		try{
//			String testName = "getReservationStatus_38702";
//			WSClient.startTest(testName, "Verifying the response when membershipId and confimationID are passed in the request", "incomplete");
//
//			String interfaceName = OWSLib.getChannel();
//			String resortOperaValue = OPERALib.getResort();
//			String resortExtValue = OWSLib.getChannelResort(resortOperaValue, interfaceName);
//			String Channel = OWSLib.getChannel();
//			String ChannelType = OWSLib.getChannelType(Channel);
//			String ChannelCarier = OWSLib.getChannelCarier(resortExtValue, Channel);
//			String username = OPERALib.getUserName();
//			String pswd = OPERALib.getPassword();
//
//			WSClient.setData("{var_profileSource}", interfaceName);
//			WSClient.setData("{var_resort}", resortOperaValue);
//			WSClient.setData("{var_extResort}", resortExtValue);
//			String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
//			WSClient.setData("{var_fname}", firstName);
//			
//			OPERALib.setOperaHeader(username);
//			String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_01");
//			String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);
//			if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true)) {
//				if (WSAssert.assertIfElementExists(createProfileResponseXML,
//						"CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {
//					String operaProfileID = WSClient.getElementValue(createProfileResponseXML,
//							"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
//					WSClient.setData("{var_profileId}", operaProfileID);
//					String member_num=WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
//					WSClient.setData("{var_memNo}",member_num);
//					
//					
//					if(OperaPropConfig.getPropertyConfigResults(new String[] {"MembershipType","MembershipLevel"})){
//						
//						WSClient.setData("{var_membershipLevel}", OperaPropConfig.getDataSetForCode("MembershipLevel", "DS_02"));
//				           WSClient.setData("{var_membershipType}", OperaPropConfig.getDataSetForCode("MembershipType", "DS_02"));
//				           String createMembershipReq = WSClient.createSOAPMessage("CreateMembership", "DS_01");
//				           String createMembershipResXML = WSClient.processSOAPMessage(createMembershipReq);
//				           if(WSAssert.assertIfElementExists(createMembershipResXML,"CreateMembershipRS_Success",true))
//				           {
//				           
//				           
//					if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"}))
//					{     
//						
//				           WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
//				           WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
//				           WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
//				           WSClient.setData("{var_sourceCode}",OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));					
//					
//				           
//				           String createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_01");
//							String createResvRes = WSClient.processSOAPMessage(createResvReq);
//		
//							if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success", true)) 
//							{
//		
//								String reservationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Reservation", XMLType.RESPONSE);
//														
//								String ConfirmationId = WSClient.getElementValueByAttribute(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", "Reservation_ReservationIDList_UniqueID_Type", "Confirmation", XMLType.RESPONSE);
//								WSClient.setData("{var_confirmationId}", ConfirmationId);
//								System.out.println(reservationId);
//								
//								if (reservationId != null && reservationId != "") 
//								{
//									OWSLib.setOWSHeader(username, pswd, resortOperaValue, ChannelType, ChannelCarier);
//									String getStatusReq = WSClient.createSOAPMessage("OWSGetReservationStatus", "DS_05");
//									String getStatusRes = WSClient.processSOAPMessage(getStatusReq);
//									WSClient.writeToReport(LogStatus.INFO,"<b>Passing membership-number in the request </b>");
//									if (WSAssert.assertIfElementExists(getStatusRes, "Result_Text_TextElement", true)) {
//
//										WSClient.writeToReport(LogStatus.INFO, "<b>Error in response is:</b>"+WSClient.getElementValue(getStatusRes,
//												"Result_Text_TextElement", XMLType.RESPONSE));
//
//									}
//									if (WSAssert.assertIfElementExists(getStatusRes,
//											"GetReservationStatusResponse_Result_GDSError", true)) {
//									
//									
//										WSClient.writeToReport(LogStatus.INFO,"<b>Error in the response is:</b>"+ WSClient.getElementValue(getStatusRes,
//												"GetReservationStatusResponse_Result_GDSError", XMLType.RESPONSE));
//
//									}
//									
//									
//									if(WSAssert.assertIfElementValueEquals(getStatusRes,"GetReservationStatusResponse_Result_resultStatusFlag", "SUCCESS", false))
//									{
//										
//										LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();
//										
//										String query=WSClient.getQuery("QS_03");
//										
//										db = WSClient.getDBRow(query);
//										
//										HashMap<String, String> xpath = new HashMap<String, String>();
//										LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
//										xpath.put("GetReservationStatusResponse_ConfirmationNumber","_GetReservationStatusResponse");
//										xpath.put("GetReservationStatusResponse_FirstName","_GetReservationStatusResponse");
//										xpath.put("GetReservationStatusResponse_ReservationStatus","_GetReservationStatusResponse");
//										xpath.put("GetReservationStatusResponse_LastName", "_GetReservationStatusResponse");
//										xpath.put("GetReservationStatusResponse_StayDateRange_StartDate", "GetReservationStatusResponse_StayDateRange");
//										xpath.put("GetReservationStatusResponse_StayDateRange_EndDate", "GetReservationStatusResponse_StayDateRange");
//										actualValues=WSClient.getSingleNodeList(getStatusRes,xpath, false, XMLType.RESPONSE);
//										WSClient.writeToReport(LogStatus.INFO, "<b>Validating response with Database</b>");
//										
//										WSAssert.assertEquals(db, actualValues, false);															
//																				
//									}	
//																
//									
//								}
//								else
//								{
//									WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Reservation ID is not created");
//								}
//							}
//					else 
//					{
//						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
//					}
//
//				} else{
//					
//					WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Reservation is not created");
//				}
//				}
//				       else{
//								
//								WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Membership is not created");
//							}
//					}
//					else{
//						
//						WSClient.writeToReport(LogStatus.WARNING, "Prerequisite failed >> Membership is not created");
//					}
//					
//				}
//					else {
//					WSClient.writeToReport(LogStatus.WARNING,
//							"Pre-requisite----------profile ID is not created---------------");
//
//				}
//
//			} else {
//				WSClient.writeToReport(LogStatus.WARNING, "Pre-requisite 1  -----------CreateProfile Failed----------");
//
//			}			
//		
//		}
//		catch(Exception e){
//			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
//		}
//	}
	
	
}
