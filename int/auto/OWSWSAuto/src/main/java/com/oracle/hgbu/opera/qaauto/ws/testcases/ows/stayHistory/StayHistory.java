package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.stayHistory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class StayHistory extends WSSetUp {
	
	/**
	 * @author ketvaidy
	 */
	
	String res_status="";
	String res_ExtStatus="";
	
	
//	@Test(groups = {"sanity","OWS","StayHistory","StayHistory"})
//	 
//	 /***** Method to verify that a list of past reservations are retrieved when the guest name and agent 
//	  identifiers are provided for minimum data*****/
//	 
//	 /*****
//	  * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Cancel Reservation.
//	  * 
//	  *****/
//	 
//	 public void stayHistory_3rd() {
//	  try {
//
//	   String testName = "stayHistory_3rd";
//	   WSClient.startTest(testName,
//	     "Verify that a list of past reservations based on the Profile ID with channel where channel and carrier name are different.",
//	     "sanity");
//	   if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
//	 String resort = OPERALib.getResort();
//	 String channel = OWSLib.getChannel(3);
//		String owsResort=OWSLib.getChannelResort(resort, channel);
//		WSClient.setData("{var_owsResort}",owsResort);
//	 String operaProfileID="";
//	 
//	   //******** Setting the OWS Header *************//
//	 
//	 OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
//	   OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
//	 OPERALib.setOperaHeader(OPERALib.getUserName());
//	 WSClient.setData("{var_resort}", resort);
//	 
//	 
//	  //******** Prerequisite 1:Create Profile*************//
//	 
//	 
//	if (operaProfileID.equals(""))
//					operaProfileID = CreateProfile.createProfile("DS_01");
//				if (!operaProfileID.equals("error")) {
//					WSClient.setData("{var_profileId}", operaProfileID);
//					WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
//	  
//	   WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
//	   WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
//	   WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
//	   WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
//
//	   //******** Prerequisite 2:Create Reservation*************//
//	   
//	   String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");
//
//						if (!reservationId.equals("error")) {
//
//							WSClient.setData("{var_resvId}", reservationId);
//	   
//	    WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+reservationId+"</b>");
//
//	    if (reservationId != null && reservationId != "") {
//	     
//	       //******** Prerequisite 3:Cancel Reservation*************//
//	     
//	    	if(!WSClient.getData("{var_resvId}").equals(""))
//	            CancelReservation.cancelReservation("DS_02");
//
//	      String res_status = "CANCELLED";
//	      String res_ExtStatus = "CANCELLED";
//	      WSClient.setData("{var_status}", res_status);
//	      WSClient.setData("{var_ExtStatus}", res_ExtStatus);
//	      WSClient.writeToReport(LogStatus.INFO,"<b>Successfully cancelled the Reservation:"+"</b>");
//	      
//	      String stayHistoryReq=WSClient.createSOAPMessage("OWSStayHistory","DS_01");
//	      String stayHistoryRes=WSClient.processSOAPMessage(stayHistoryReq);
//	      if(WSAssert.assertIfElementExists(stayHistoryRes, "Result_Text_TextElement",true)) {
//	       
//	       /**** Verifying that the error message is populated on the response ********/
//	       
//	       String message=WSAssert.getElementValue(stayHistoryRes, "Result_Text_TextElement", XMLType.RESPONSE);
//	       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
//	       }
//	      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode",true)) {
//	       
//	       /**** Verifying whether the error Message is populated on the response ****/
//	       
//	       String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode", XMLType.RESPONSE);
//	       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
//	      }
//	      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_GDSError",true)) {
//	          
//	          /**** Verifying whether the error Message is populated on the response ****/
//	          
//	          String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_GDSError", XMLType.RESPONSE);
//	          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
//	         }
//	      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_resultStatusFlag", false))
//	      {
//	       /***Checking for the existence of the ResultStatusFlag**/
//	      
//	      if (WSAssert.assertIfElementValueEquals(stayHistoryRes,
//	        "StayHistoryResponse_Result_resultStatusFlag", "SUCCESS", false)) {
//	       
//	        
//	        LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
//	        LinkedHashMap<String,String> actualValues= new LinkedHashMap<String,String>();
//	        HashMap<String,String> xpath=new HashMap<String,String>();
//	                    
//	                    xpath.put("Profile_ProfileIDs_UniqueID", "Profiles_Profile_ProfileIDs");
//	                    xpath.put("Customer_PersonName_firstName", "Profile_Customer_PersonName");
//	                    xpath.put("Customer_PersonName_lastName", "Profile_Customer_PersonName");
//	                    xpath.put("HotelReservation_UniqueIDList_UniqueID", "StayHistoryResponse_HotelReservations_HotelReservation");
//	                   
//	                   
//	                    String query1=WSClient.getQuery("QS_01");
//	        db =WSClient.getDBRow(query1);
//	        actualValues = WSClient.getSingleNodeList(stayHistoryRes,xpath,false,XMLType.RESPONSE);
//	       // actualValues.put("RESV_NAME_ID",reservationId);
//	        if(WSAssert.assertEquals(db,actualValues, false)) {
//	        
//	         
//	        }
//	                else
//	        {
//	         //WSClient.writeToReport(LogStatus.FAIL, "The details could not be correctly fetched" );
//	        }
//	       
//	      
//	      }
//	      else
//	      {
//	       WSClient.writeToReport(LogStatus.FAIL, "Stay History Operation Unsuccessful");
//	      }
//	      }
//	      else {
//	       /**The ResultStatusFlag not found.This indicates an error in the schema ****/
//	       WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
//	      }
//	          
//	      
//	       
//	      
//	     } }}}}
//	  catch (Exception e) {
//	    WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
//	   }
//	  }
 
 @Test(groups = {"sanity","OWS","StayHistory","StayHistory", "stayHistory_39880"})
 
 /***** Method to verify that a list of past reservations are retrieved when the guest name and agent 
  identifiers are provided for minimum data*****/
 
 /*****
  * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Cancel Reservation.
  * 
  *****/
 
 public void stayHistory_39880() {
  try {

   String testName = "stayHistory_39880";
   WSClient.startTest(testName,
     "Verify that a list of past reservations are retrived when the search is performed with Profile ID",
     "sanity");
   if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
 String resort = OPERALib.getResort();
 String channel = OWSLib.getChannel();
	String owsResort=OWSLib.getChannelResort(resort, channel);
	WSClient.setData("{var_owsResort}",owsResort);
 String operaProfileID="";
 
   //******** Setting the OWS Header *************//
 
 OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
   OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
 OPERALib.setOperaHeader(OPERALib.getUserName());
 WSClient.setData("{var_resort}", resort);
 
 
  //******** Prerequisite 1:Create Profile*************//
 
 
if (operaProfileID.equals(""))
				operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
  
   WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
   WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
   WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
   WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

   //******** Prerequisite 2:Create Reservation*************//
   
   String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");

					if (!reservationId.equals("error")) {

						WSClient.setData("{var_resvId}", reservationId);
   
    WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+reservationId+"</b>");

    if (reservationId != null && reservationId != "") {
     
       //******** Prerequisite 3:Cancel Reservation*************//
     
    	if(!WSClient.getData("{var_resvId}").equals(""))
            CancelReservation.cancelReservation("DS_02");

      String res_status = "CANCELLED";
      String res_ExtStatus = "CANCELLED";
      WSClient.setData("{var_status}", res_status);
      WSClient.setData("{var_ExtStatus}", res_ExtStatus);
      WSClient.writeToReport(LogStatus.INFO,"<b>Successfully cancelled the Reservation:"+"</b>");
      
      String stayHistoryReq=WSClient.createSOAPMessage("OWSStayHistory","DS_01");
      String stayHistoryRes=WSClient.processSOAPMessage(stayHistoryReq);
      if(WSAssert.assertIfElementExists(stayHistoryRes, "Result_Text_TextElement",true)) {
       
       /**** Verifying that the error message is populated on the response ********/
       
       String message=WSAssert.getElementValue(stayHistoryRes, "Result_Text_TextElement", XMLType.RESPONSE);
       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
       }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode",true)) {
       
       /**** Verifying whether the error Message is populated on the response ****/
       
       String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode", XMLType.RESPONSE);
       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
      }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_GDSError",true)) {
          
          /**** Verifying whether the error Message is populated on the response ****/
          
          String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_GDSError", XMLType.RESPONSE);
          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
         }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_resultStatusFlag", false))
      {
       /***Checking for the existence of the ResultStatusFlag**/
      
      if (WSAssert.assertIfElementValueEquals(stayHistoryRes,
        "StayHistoryResponse_Result_resultStatusFlag", "SUCCESS", false)) {
       
        
        LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
        LinkedHashMap<String,String> actualValues= new LinkedHashMap<String,String>();
        HashMap<String,String> xpath=new HashMap<String,String>();
                    
                    xpath.put("Profile_ProfileIDs_UniqueID", "Profiles_Profile_ProfileIDs");
                    xpath.put("Customer_PersonName_firstName", "Profile_Customer_PersonName");
                    xpath.put("Customer_PersonName_lastName", "Profile_Customer_PersonName");
                    xpath.put("HotelReservation_UniqueIDList_UniqueID", "StayHistoryResponse_HotelReservations_HotelReservation");
                   
                   
                    String query1=WSClient.getQuery("QS_01");
        db =WSClient.getDBRow(query1);
        actualValues = WSClient.getSingleNodeList(stayHistoryRes,xpath,false,XMLType.RESPONSE);
       // actualValues.put("RESV_NAME_ID",reservationId);
        if(WSAssert.assertEquals(db,actualValues, false)) {
        
         
        }
                else
        {
         //WSClient.writeToReport(LogStatus.FAIL, "The details could not be correctly fetched" );
        }
       
      
      }
      else
      {
       WSClient.writeToReport(LogStatus.FAIL, "Stay History Operation Unsuccessful");
      }
      }
      else {
       /**The ResultStatusFlag not found.This indicates an error in the schema ****/
       WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
      }
          
      
       
      
     } }}}}
  catch (Exception e) {
    WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
   }
  }
 
 
 //@Test(groups = {"minimumRegression","OWS","StayHistory","StayHistory"})
 
 /***** Method to verify that a list of past reservations are retrieved when the guest name and agent 
  identifiers are provided for minimum data*****/
 
 /*****
  * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Cancel Reservation.
  * 
  *****/
 
 public void stayHistory_39882() {
  try {

   String testName = "stayHistory_39882";
   WSClient.startTest(testName,
     "Verify that the adult and children count are correctly fetched based on the Profile ID",
     "minimumRegression");
   if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
 String resort = OPERALib.getResort();
 String channel = OWSLib.getChannel();
 String operaProfileID="";
 
   //******** Setting the OWS Header *************//
 
 OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
   OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
 OPERALib.setOperaHeader(OPERALib.getUserName());
 WSClient.setData("{var_resort}", resort);
 
 WSClient.writeToReport(LogStatus.INFO, "11");
 
  //******** Prerequisite 1:Create Profile*************//
 
 
if (operaProfileID.equals(""))
				operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
				 
   WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
   WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
   WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
   WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

   //******** Prerequisite 2:Create Reservation*************//
   
   String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");

					if (!reservationId.equals("error")) {

						 WSClient.writeToReport(LogStatus.INFO, "33");
						WSClient.setData("{var_resvId}", reservationId);
   
    WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+reservationId+"</b>");

    if (reservationId != null && reservationId != "") {
     
       //******** Prerequisite 3:Cancel Reservation*************//
    	 WSClient.writeToReport(LogStatus.INFO, "44");
    	if(!WSClient.getData("{var_resvId}").equals(""))
            CancelReservation.cancelReservation("DS_02");

      String res_status = "CANCELLED";
      String res_ExtStatus = "CANCELLED";
      WSClient.setData("{var_status}", res_status);
      WSClient.setData("{var_ExtStatus}", res_ExtStatus);
      WSClient.writeToReport(LogStatus.INFO,"<b>Successfully cancelled the Reservation:"+"</b>");
      
      String stayHistoryReq=WSClient.createSOAPMessage("OWSStayHistory","DS_01");
      String stayHistoryRes=WSClient.processSOAPMessage(stayHistoryReq);
      if(WSAssert.assertIfElementExists(stayHistoryRes, "Result_Text_TextElement",true)) {
       
       /**** Verifying that the error message is populated on the response ********/
       
       String message=WSAssert.getElementValue(stayHistoryRes, "Result_Text_TextElement", XMLType.RESPONSE);
       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
       }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode",true)) {
       
       /**** Verifying whether the error Message is populated on the response ****/
       
       String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode", XMLType.RESPONSE);
       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
      }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_GDSError",true)) {
          
          /**** Verifying whether the error Message is populated on the response ****/
          
          String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_GDSError", XMLType.RESPONSE);
          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
         }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_resultStatusFlag", false))
      {
       /***Checking for the existence of the ResultStatusFlag**/
      
      if (WSAssert.assertIfElementValueEquals(stayHistoryRes,
        "StayHistoryResponse_Result_resultStatusFlag", "SUCCESS", false)) {
       
    	  String count=WSClient.getAttributeValueByAttribute(stayHistoryRes, "ResGuests_ResGuest_GuestCounts","count","ADULT", XMLType.RESPONSE);
    	  WSClient.writeToReport(LogStatus.INFO, count);
        LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
        LinkedHashMap<String,String> actualValues= new LinkedHashMap<String,String>();
        LinkedHashMap<String,String> xpath=new LinkedHashMap<String,String>();        
       xpath.put("ResGuest_GuestCounts_GuestCount_count", "ResGuest_GuestCounts_GuestCount");
       String query3=WSClient.getQuery("QS_03");
        db =WSClient.getDBRow(query3);
       // String count=WSClient.getAttributeValueByAttribute(stayHistoryRes, "ResGuest_GuestCounts_GuestCount","count","ADULT", XMLType.RESPONSE);
        actualValues = WSClient.getSingleNodeList(stayHistoryRes,xpath,false,XMLType.RESPONSE);
//        String count = WSClient.getElementValueByAttribute(stayHistoryRes, "ResGuest_GuestCounts_GuestCount_ageQualifyingCode", "ADULT", XMLType.RESPONSE);
        //String count = WSClient.getAttributeValueByAttribute(stayHistoryRes, "ResGuest_GuestCounts_GuestCount_ageQualifyingCode", "count", "ADULT", XMLType.RESPONSE);
        //WSClient.writeToReport(LogStatus.INFO, count);
        //actualValues.put("RESV_NAME_ID",reservationId);
        //WSClient.writeToReport(LogStatus.INFO, "66");
        WSClient.writeToReport(LogStatus.INFO, actualValues.toString());
        if(WSAssert.assertEquals(db,actualValues, false)) {
        
         
        }
                else
        {
         //WSClient.writeToReport(LogStatus.FAIL, "The details could not be correctly fetched" );
        }
       
      
      }
      else
      {
       WSClient.writeToReport(LogStatus.FAIL, "Stay History Operation Unsuccessful");
      }
      }
      else {
       /**The ResultStatusFlag not found.This indicates an error in the schema ****/
       WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
      }
          
      
       
      
     } }}}}
  catch (Exception e) {
    WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
   }
  }
 
 @Test(groups = {"minimumRegression","OWS","StayHistory","StayHistory"})
 
 /***** Method to verify that a list of past reservations are retrieved when the guest name and agent 
  identifiers are provided for minimum data*****/
 
 /*****
  * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Cancel Reservation.
  * 
  *****/
 
 public void stayHistory_39886() {
  try {

   String testName = "stayHistory_39886";
   WSClient.startTest(testName,
     "Verify that the cancellation details are correctly fetched based on the Profile ID",
     "minimumRegression");
   if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
 String resort = OPERALib.getResort();
 String channel = OWSLib.getChannel();
	String owsResort=OWSLib.getChannelResort(resort, channel);
	WSClient.setData("{var_owsResort}",owsResort);
 String operaProfileID="";
 
   //******** Setting the OWS Header *************//
 
 OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
   OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
 OPERALib.setOperaHeader(OPERALib.getUserName());
 WSClient.setData("{var_resort}", resort);
 

 
  //******** Prerequisite 1:Create Profile*************//
 
 
if (operaProfileID.equals(""))
				operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
				 
   WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
   WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
   WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
   WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

   //******** Prerequisite 2:Create Reservation*************//
   
   String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");

					if (!reservationId.equals("error")) {

						WSClient.setData("{var_resvId}", reservationId);
   
    WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+reservationId+"</b>");

    if (reservationId != null && reservationId != "") {
     
       //******** Prerequisite 3:Cancel Reservation*************//
    	
    	if(!WSClient.getData("{var_resvId}").equals(""))
            CancelReservation.cancelReservation("DS_02");

      String res_status = "CANCELLED";
      String res_ExtStatus = "CANCELLED";
      WSClient.setData("{var_status}", res_status);
      WSClient.setData("{var_ExtStatus}", res_ExtStatus);
      WSClient.writeToReport(LogStatus.INFO,"<b>Successfully cancelled the Reservation:"+"</b>");
      
      String stayHistoryReq=WSClient.createSOAPMessage("OWSStayHistory","DS_01");
      String stayHistoryRes=WSClient.processSOAPMessage(stayHistoryReq);
      if(WSAssert.assertIfElementExists(stayHistoryRes, "Result_Text_TextElement",true)) {
       
       /**** Verifying that the error message is populated on the response ********/
       
       String message=WSAssert.getElementValue(stayHistoryRes, "Result_Text_TextElement", XMLType.RESPONSE);
       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
       }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode",true)) {
       
       /**** Verifying whether the error Message is populated on the response ****/
       
       String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode", XMLType.RESPONSE);
       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
      }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_GDSError",true)) {
          
          /**** Verifying whether the error Message is populated on the response ****/
          
          String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_GDSError", XMLType.RESPONSE);
          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
         }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_resultStatusFlag", false))
      {
       /***Checking for the existence of the ResultStatusFlag**/
      
      if (WSAssert.assertIfElementValueEquals(stayHistoryRes,
        "StayHistoryResponse_Result_resultStatusFlag", "SUCCESS", false)) {
       
        
        LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
        LinkedHashMap<String,String> actualValues= new LinkedHashMap<String,String>();
        LinkedHashMap<String,String> xpath=new LinkedHashMap<String,String>();        
       xpath.put("RoomStays_RoomStay_CancelTerm_cancelDate", "RoomStays_RoomStay_CancelTerm");
       xpath.put("RoomStays_RoomStay_CancelTerm_cancelType", "RoomStays_RoomStay_CancelTerm");
       xpath.put("RoomStays_RoomStay_CancelTerm_cancelNumber", "RoomStays_RoomStay_CancelTerm");
       xpath.put("HotelReservation_UniqueIDList_UniqueID", "HotelReservation_UniqueIDList_UniqueID");
       String query3=WSClient.getQuery("QS_04");
        db =WSClient.getDBRow(query3);
        db.put("cancelType1", "Cancel");
//        String count = WSClient.getElementValueByAttribute(stayHistoryRes, "ResGuest_GuestCounts_GuestCount_ageQualifyingCode", "ADULT", XMLType.RESPONSE);
        actualValues = WSClient.getSingleNodeList(stayHistoryRes,xpath,false,XMLType.RESPONSE);
        //actualValues.put("RESV_NAME_ID",reservationId);
        
        

       WSAssert.assertEquals(db,actualValues, false);
      
      }
      else
      {
       WSClient.writeToReport(LogStatus.FAIL, "Stay History Operation Unsuccessful");
      }
      }
      else {
       /**The ResultStatusFlag not found.This indicates an error in the schema ****/
       WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
      }
          
      
       
      
     } }}}}
  catch (Exception e) {
    WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
   }
  }
 
 
 
 
@Test(groups = {"minimumRegression","OWS","StayHistory","StayHistory"})
 
 /***** Method to verify that a list of past reservations are retrieved when the guest name and agent 
  identifiers are provided for minimum data*****/
 
 /*****
  * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Cancel Reservation.
  * 
  *****/
 
 public void stayHistory_39889() {
  try {

   String testName = "stayHistory_39889";
   WSClient.startTest(testName,
     "Verify that the Reservation Timespan details are correctly fetched based on the Profile ID",
     "minimumRegression");
   if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
 String resort = OPERALib.getResort();
 String channel = OWSLib.getChannel();
	String owsResort=OWSLib.getChannelResort(resort, channel);
	WSClient.setData("{var_owsResort}",owsResort);
 String operaProfileID="";
 
   //******** Setting the OWS Header *************//
 
 OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
   OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
 OPERALib.setOperaHeader(OPERALib.getUserName());
 WSClient.setData("{var_resort}", resort);
 

 
  //******** Prerequisite 1:Create Profile*************//
 
 
if (operaProfileID.equals(""))
				operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
				 
   WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
   WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
   WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
   WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

   //******** Prerequisite 2:Create Reservation*************//
   
   String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");

					if (!reservationId.equals("error")) {

						WSClient.setData("{var_resvId}", reservationId);
   
    WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+reservationId+"</b>");

    if (reservationId != null && reservationId != "") {
     
       //******** Prerequisite 3:Cancel Reservation*************//
    	
    	if(!WSClient.getData("{var_resvId}").equals(""))
            CancelReservation.cancelReservation("DS_02");

      String res_status = "CANCELLED";
      String res_ExtStatus = "CANCELLED";
      WSClient.setData("{var_status}", res_status);
      WSClient.setData("{var_ExtStatus}", res_ExtStatus);
      WSClient.writeToReport(LogStatus.INFO,"<b>Successfully cancelled the Reservation:"+"</b>");
      
      String stayHistoryReq=WSClient.createSOAPMessage("OWSStayHistory","DS_01");
      String stayHistoryRes=WSClient.processSOAPMessage(stayHistoryReq);
      if(WSAssert.assertIfElementExists(stayHistoryRes, "Result_Text_TextElement",true)) {
       
       /**** Verifying that the error message is populated on the response ********/
       
       String message=WSAssert.getElementValue(stayHistoryRes, "Result_Text_TextElement", XMLType.RESPONSE);
       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
       }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode",true)) {
       
       /**** Verifying whether the error Message is populated on the response ****/
       
       String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode", XMLType.RESPONSE);
       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
      }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_GDSError",true)) {
          
          /**** Verifying whether the error Message is populated on the response ****/
          
          String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_GDSError", XMLType.RESPONSE);
          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
         }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_resultStatusFlag", false))
      {
       /***Checking for the existence of the ResultStatusFlag**/
      
      if (WSAssert.assertIfElementValueEquals(stayHistoryRes,
        "StayHistoryResponse_Result_resultStatusFlag", "SUCCESS", false)) {
       
        
        LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
        LinkedHashMap<String,String> actualValues= new LinkedHashMap<String,String>();
        LinkedHashMap<String,String> xpath=new LinkedHashMap<String,String>();        
       xpath.put("RoomStay_TimeSpan_StartDate", "RoomStays_RoomStay_TimeSpan");
       xpath.put("RoomStay_TimeSpan_EndDate", "RoomStays_RoomStay_TimeSpan");
       xpath.put("HotelReservation_UniqueIDList_UniqueID", "HotelReservation_UniqueIDList_UniqueID");
       String query3=WSClient.getQuery("QS_07");
        db =WSClient.getDBRow(query3);
//        String count = WSClient.getElementValueByAttribute(stayHistoryRes, "ResGuest_GuestCounts_GuestCount_ageQualifyingCode", "ADULT", XMLType.RESPONSE);
        actualValues = WSClient.getSingleNodeList(stayHistoryRes,xpath,false,XMLType.RESPONSE);
        //actualValues.put("RESV_NAME_ID",reservationId);
        actualValues.put("StartDate1",actualValues.get("StartDate1").substring(0, 10));
        actualValues.put("EndDate1",actualValues.get("EndDate1").substring(0, 10));
        
        

        if(WSAssert.assertEquals(db,actualValues, false)) {
        
         
        }
                else
        {
         //WSClient.writeToReport(LogStatus.FAIL, "The details could not be correctly fetched" );
        }
       
      
      }
      else
      {
       WSClient.writeToReport(LogStatus.FAIL, "Stay History Operation Unsuccessful");
      }
      }
      else {
       /**The ResultStatusFlag not found.This indicates an error in the schema ****/
       WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
      }
          
      
       
      
     } }}}}
  catch (Exception e) {
    WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
   }
  }
 
 
@Test(groups = {"minimumRegression","OWS","StayHistory","StayHistory"})
 
 /***** Method to verify that a list of past reservations are retrieved when the guest name and agent 
  identifiers are provided for minimum data*****/
 
 /*****
  * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Cancel Reservation.
  * 
  *****/
 
 public void stayHistory_39887() {
  try {

   String testName = "stayHistory_39887";
   WSClient.startTest(testName,
     "Verify that the hotel details are correctly fetched based on the Profile ID",
     "minimumRegression");
   if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
 String resort = OPERALib.getResort();
 String channel = OWSLib.getChannel();
	String owsResort=OWSLib.getChannelResort(resort, channel);
	WSClient.setData("{var_owsResort}",owsResort);
 String operaProfileID="";
 
   //******** Setting the OWS Header *************//
 
 OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
   OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
 OPERALib.setOperaHeader(OPERALib.getUserName());
 WSClient.setData("{var_resort}", resort);
 

 
  //******** Prerequisite 1:Create Profile*************//
 
 
if (operaProfileID.equals(""))
				operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
				 
   WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
   WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
   WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
   WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

   //******** Prerequisite 2:Create Reservation*************//
   
   String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");

					if (!reservationId.equals("error")) {

						WSClient.setData("{var_resvId}", reservationId);
   
    WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+reservationId+"</b>");

    if (reservationId != null && reservationId != "") {
     
       //******** Prerequisite 3:Cancel Reservation*************//
    	
    	if(!WSClient.getData("{var_resvId}").equals(""))
            CancelReservation.cancelReservation("DS_02");

      String res_status = "CANCELLED";
      String res_ExtStatus = "CANCELLED";
      WSClient.setData("{var_status}", res_status);
      WSClient.setData("{var_ExtStatus}", res_ExtStatus);
      WSClient.writeToReport(LogStatus.INFO,"<b>Successfully cancelled the Reservation:"+"</b>");
      
      String stayHistoryReq=WSClient.createSOAPMessage("OWSStayHistory","DS_01");
      String stayHistoryRes=WSClient.processSOAPMessage(stayHistoryReq);
      if(WSAssert.assertIfElementExists(stayHistoryRes, "Result_Text_TextElement",true)) {
       
       /**** Verifying that the error message is populated on the response ********/
       
       String message=WSAssert.getElementValue(stayHistoryRes, "Result_Text_TextElement", XMLType.RESPONSE);
       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
       }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode",true)) {
       
       /**** Verifying whether the error Message is populated on the response ****/
       
       String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode", XMLType.RESPONSE);
       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
      }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_GDSError",true)) {
          
          /**** Verifying whether the error Message is populated on the response ****/
          
          String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_GDSError", XMLType.RESPONSE);
          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
         }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_resultStatusFlag", false))
      {
       /***Checking for the existence of the ResultStatusFlag**/
      
      if (WSAssert.assertIfElementValueEquals(stayHistoryRes,
        "StayHistoryResponse_Result_resultStatusFlag", "SUCCESS", false)) {
       
        
        LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
        LinkedHashMap<String,String> actualValues= new LinkedHashMap<String,String>();
        LinkedHashMap<String,String> xpath=new LinkedHashMap<String,String>();        
       xpath.put("RoomStays_RoomStay_HotelReference_hotelCode", "RoomStays_RoomStay_HotelReference");
       xpath.put("RoomStays_RoomStay_HotelReference_chainCode", "RoomStays_RoomStay_HotelReference");
       xpath.put("HotelReservation_UniqueIDList_UniqueID", "HotelReservation_UniqueIDList_UniqueID");
       String query3=WSClient.getQuery("QS_06");
        db =WSClient.getDBRow(query3);
//        String count = WSClient.getElementValueByAttribute(stayHistoryRes, "ResGuest_GuestCounts_GuestCount_ageQualifyingCode", "ADULT", XMLType.RESPONSE);
        actualValues = WSClient.getSingleNodeList(stayHistoryRes,xpath,false,XMLType.RESPONSE);
        //actualValues.put("RESV_NAME_ID",reservationId);
      //  WSClient.writeToReport(LogStatus.INFO,actualValues.toString());
        

        if(WSAssert.assertEquals(db,actualValues, false)) {
        
         
        }
                else
        {
         //WSClient.writeToReport(LogStatus.FAIL, "The details could not be correctly fetched" );
        }
       
      
      }
      else
      {
       WSClient.writeToReport(LogStatus.FAIL, "Stay History Operation Unsuccessful");
      }
      }
      else {
       /**The ResultStatusFlag not found.This indicates an error in the schema ****/
       WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
      }
          
      
       
      
     } }}}}
  catch (Exception e) {
    WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
   }
  }
    

@Test(groups = {"minimumRegression","OWS","StayHistory","StayHistory"})

/***** Method to verify that a list of past reservations are retrieved when the guest name and agent 
 identifiers are provided for minimum data*****/

/*****
 * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Cancel Reservation.
 * 
 *****/

public void stayHistory_39899() {
 try {

  String testName = "stayHistory_39899";
  WSClient.startTest(testName,
    "Verify that the Rate Plan details are correctly fetched based on the Profile ID",
    "minimumRegression");
  if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
String resort = OPERALib.getResort();
String channel = OWSLib.getChannel();
String owsResort=OWSLib.getChannelResort(resort, channel);
WSClient.setData("{var_owsResort}",owsResort);
String operaProfileID="";

  //******** Setting the OWS Header *************//

OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
  OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
OPERALib.setOperaHeader(OPERALib.getUserName());
WSClient.setData("{var_resort}", resort);



 //******** Prerequisite 1:Create Profile*************//


if (operaProfileID.equals(""))
				operaProfileID = CreateProfile.createProfile("DS_01");
			if (!operaProfileID.equals("error")) {
				WSClient.setData("{var_profileId}", operaProfileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
				 
  WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
  WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
  WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
  WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

  //******** Prerequisite 2:Create Reservation*************//
  
  String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");

					if (!reservationId.equals("error")) {

						WSClient.setData("{var_resvId}", reservationId);
  
   WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+reservationId+"</b>");

   if (reservationId != null && reservationId != "") {
    
      //******** Prerequisite 3:Cancel Reservation*************//
   	
   	if(!WSClient.getData("{var_resvId}").equals(""))
           CancelReservation.cancelReservation("DS_02");

     String res_status = "CANCELLED";
     String res_ExtStatus = "CANCELLED";
     WSClient.setData("{var_status}", res_status);
     WSClient.setData("{var_ExtStatus}", res_ExtStatus);
     WSClient.writeToReport(LogStatus.INFO,"<b>Successfully cancelled the Reservation:"+"</b>");
     
     String stayHistoryReq=WSClient.createSOAPMessage("OWSStayHistory","DS_01");
     String stayHistoryRes=WSClient.processSOAPMessage(stayHistoryReq);
     if(WSAssert.assertIfElementExists(stayHistoryRes, "Result_Text_TextElement",true)) {
      
      /**** Verifying that the error message is populated on the response ********/
      
      String message=WSAssert.getElementValue(stayHistoryRes, "Result_Text_TextElement", XMLType.RESPONSE);
      WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
      }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode",true)) {
      
      /**** Verifying whether the error Message is populated on the response ****/
      
      String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode", XMLType.RESPONSE);
      WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
     }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_GDSError",true)) {
         
         /**** Verifying whether the error Message is populated on the response ****/
         
         String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_GDSError", XMLType.RESPONSE);
         WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
        }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_resultStatusFlag", false))
     {
      /***Checking for the existence of the ResultStatusFlag**/
     
     if (WSAssert.assertIfElementValueEquals(stayHistoryRes,
       "StayHistoryResponse_Result_resultStatusFlag", "SUCCESS", false)) {
      
       
       LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
       LinkedHashMap<String,String> actualValues= new LinkedHashMap<String,String>();
       LinkedHashMap<String,String> xpath=new LinkedHashMap<String,String>();        
      xpath.put("RoomStay_RatePlans_RatePlan_ratePlanCode", "RoomStay_RatePlans_RatePlan");
      xpath.put("RatePlan_RatePlanDescription_Text", "RatePlans_RatePlan_RatePlanDescription");
      xpath.put("HotelReservation_UniqueIDList_UniqueID", "HotelReservation_UniqueIDList_UniqueID");
      String query3=WSClient.getQuery("QS_08");
       db =WSClient.getDBRow(query3);
//       String count = WSClient.getElementValueByAttribute(stayHistoryRes, "ResGuest_GuestCounts_GuestCount_ageQualifyingCode", "ADULT", XMLType.RESPONSE);
       actualValues = WSClient.getSingleNodeList(stayHistoryRes,xpath,false,XMLType.RESPONSE);
       //actualValues.put("RESV_NAME_ID",reservationId);
       //WSClient.writeToReport(LogStatus.INFO,actualValues.toString());
       

       if(WSAssert.assertEquals(db,actualValues, false)) {
       
        
       }
               else
       {
        //WSClient.writeToReport(LogStatus.FAIL, "The details could not be correctly fetched" );
       }
      
     
     }
     else
     {
      WSClient.writeToReport(LogStatus.FAIL, "Stay History Operation Unsuccessful");
     }
     }
     else {
      /**The ResultStatusFlag not found.This indicates an error in the schema ****/
      WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
     }
         
     
      
     
    } }}}}
 catch (Exception e) {
   WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
  }
 }
   
 
@Test(groups = {"minimumRegression","OWS","StayHistory","StayHistory"})
 
 /***** Method to verify that an error message is obtained when no profile ID is 
  * given in the request for a configured channel*****/
 
 /*****
  * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Cancel Reservation.
  * 
  *****/
 
 public void stayHistory_40322() {
  try {

   String testName = "stayHistory_40322";
   WSClient.startTest(testName,
     "Verify that an error message is obtained when no profile ID is given in the request", "minimumRegression");

   if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
 String resort = OPERALib.getResort();
 String channel = OWSLib.getChannel();
	String owsResort=OWSLib.getChannelResort(resort, channel);
	WSClient.setData("{var_owsResort}",owsResort);
 String operaProfileID="";
 
 
   //******** Setting the OWS Header *************//
 
 OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
   OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
 OPERALib.setOperaHeader(OPERALib.getUserName());
 WSClient.setData("{var_resort}", resort);
 
 
  //******** Prerequisite 1:Create Profile*************//
 
 if (operaProfileID.equals(""))
		operaProfileID = CreateProfile.createProfile("DS_01");
	if (!operaProfileID.equals("error")) {
		WSClient.setData("{var_profileId}", operaProfileID);
		WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
   WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
   WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
   WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
   WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));


   //******** Prerequisite 2:Create Reservation*************//
   
   String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");

	if (!reservationId.equals("error")) {

		WSClient.setData("{var_reservation_id}", reservationId);

WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+reservationId+"</b>");

    if (reservationId != null && reservationId != "") {
     
       //******** Prerequisite 3:Cancel Reservation*************//
     
    	CancelReservation.cancelReservation("DS_01");

      WSClient.setData("{var_status}", res_status);
      WSClient.setData("{var_ExtStatus}", res_ExtStatus);
      WSClient.writeToReport(LogStatus.INFO,"<b>Successfully cancelled the Reservation:"+"</b>");
      
      String stayHistoryReq=WSClient.createSOAPMessage("OWSStayHistory","DS_02");
      String stayHistoryRes=WSClient.processSOAPMessage(stayHistoryReq);
      if(WSAssert.assertIfElementExists(stayHistoryRes, "Result_Text_TextElement",true)) {
       
       /**** Verifying that the error message is populated on the response ********/
       
       String message=WSAssert.getElementValue(stayHistoryRes, "Result_Text_TextElement", XMLType.RESPONSE);
       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
       }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode",true)) {
       
       /**** Verifying whether the error Message is populated on the response ****/
       
       String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode", XMLType.RESPONSE);
       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
      }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_GDSError",true)) {
          
          /**** Verifying whether the error Message is populated on the response ****/
          
          String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_GDSError", XMLType.RESPONSE);
          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
         }
      if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_resultStatusFlag", false))
      {
       /***Checking for the existence of the ResultStatusFlag**/
      
      WSAssert.assertIfElementValueEquals(stayHistoryRes,
        "StayHistoryResponse_Result_resultStatusFlag", "FAIL", false);
      }
      else {
       /**The ResultStatusFlag not found.This indicates an error in the schema ****/
       WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
      }
          
      
       
      
     } }}}}
  
  catch (Exception e) {
    WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
   }
  }

@Test(groups = {"minimumRegression","OWS","StayHistory","StayHistory"})

/***** Method to verify that an error message is obtained when an invalid profile ID is 
 * given in the request for a configured channel*****/

/*****
 * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Cancel Reservation.
 * 
 *****/

public void stayHistory_40323() {
 try {

  String testName = "stayHistory_40323";
  WSClient.startTest(testName,
    "Verify that an error message is obtained when an invalid profile ID is given in the request", "minimumRegression");
  if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
String resort = OPERALib.getResort();
String channel = OWSLib.getChannel();
String owsResort=OWSLib.getChannelResort(resort, channel);
WSClient.setData("{var_owsResort}",owsResort);
String operaProfileID="";

//******** Setting the OWS Header *************//

OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
  OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
OPERALib.setOperaHeader(OPERALib.getUserName());
WSClient.setData("{var_resort}", resort);


//******** Prerequisite 1:Create Profile*************//

if (operaProfileID.equals(""))
	operaProfileID = CreateProfile.createProfile("DS_01");
if (!operaProfileID.equals("error")) {
	WSClient.setData("{var_profileId}", operaProfileID);
	WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
  WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
  WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
  WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
  WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));


  //******** Prerequisite 2:Create Reservation*************//
  
  String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");

 	if (!reservationId.equals("error")) {

 		WSClient.setData("{var_reservation_id}", reservationId);

 WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+reservationId+"</b>");


   if (reservationId != null && reservationId != "") {
    
      //******** Prerequisite 3:Cancel Reservation*************//
    
	   CancelReservation.cancelReservation("DS_01");

     String res_status = "CANCELLED";
     String res_ExtStatus = "CANCELLED";

     WSClient.setData("{var_status}", res_status);
     WSClient.setData("{var_ExtStatus}", res_ExtStatus);
     WSClient.writeToReport(LogStatus.INFO,"<b>Successfully cancelled the Reservation:"+"</b>");
     
     String stayHistoryReq=WSClient.createSOAPMessage("OWSStayHistory","DS_03");
     String stayHistoryRes=WSClient.processSOAPMessage(stayHistoryReq);
     if(WSAssert.assertIfElementExists(stayHistoryRes, "Result_Text_TextElement",true)) {
      
      /**** Verifying that the error message is populated on the response ********/
      
      String message=WSAssert.getElementValue(stayHistoryRes, "Result_Text_TextElement", XMLType.RESPONSE);
      WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
      }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode",true)) {
      
      /**** Verifying whether the error Message is populated on the response ****/
      
      String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode", XMLType.RESPONSE);
      WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
     }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_GDSError",true)) {
         
         /**** Verifying whether the error Message is populated on the response ****/
         
         String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_GDSError", XMLType.RESPONSE);
         WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
        }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_resultStatusFlag", false))
     {
      /***Checking for the existence of the ResultStatusFlag**/
     
     WSAssert.assertIfElementValueEquals(stayHistoryRes,
       "StayHistoryResponse_Result_resultStatusFlag", "FAIL", false);
     }
     else {
      /**The ResultStatusFlag not found.This indicates an error in the schema ****/
      WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
     }
        
     
      
     
    } }}
 }}
 catch (Exception e) {
   WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
  }
 }
    
@Test(groups = {"minimumRegression","OWS","StayHistory","StayHistory"})

/***** Method to verify that an error message is obtained when all the required fields are 
 * given in the request for a configured channel*****/

/*****
 * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Cancel Reservation.
 * 
 *****/

public void stayHistory_40324() {
 try {

  String testName = "stayHistory_40324";
  WSClient.startTest(testName,
    "Verify that the past reservation details are fetched based on the Date Range and Profile ID", "minimumRegression");

  if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
String resort = OPERALib.getResort();
String channel = OWSLib.getChannel();
String owsResort=OWSLib.getChannelResort(resort, channel);
WSClient.setData("{var_owsResort}",owsResort);
String operaProfileID="";


//******** Setting the OWS Header *************//

OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
  OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
OPERALib.setOperaHeader(OPERALib.getUserName());
WSClient.setData("{var_resort}", resort);


//******** Prerequisite 1:Create Profile*************//

if (operaProfileID.equals(""))
	operaProfileID = CreateProfile.createProfile("DS_01");
if (!operaProfileID.equals("error")) {
	WSClient.setData("{var_profileId}", operaProfileID);
	WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
  WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
  WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
  WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
  WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));


  //******** Prerequisite 2:Create Reservation*************//
  
  String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");

 	if (!reservationId.equals("error")) {

 		WSClient.setData("{var_resvId}", reservationId);
 		WSClient.setData("{var_reservation_id}", reservationId);
 		

 WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+reservationId+"</b>");


   if (reservationId != null && reservationId != "") {
    
      //******** Prerequisite 3:Cancel Reservation*************//
    
	   CancelReservation.cancelReservation("DS_01");

     String res_status = "CANCELLED";
     String res_ExtStatus = "CANCELLED";

     WSClient.setData("{var_status}", res_status);
     WSClient.setData("{var_ExtStatus}", res_ExtStatus);
     WSClient.writeToReport(LogStatus.INFO,"<b>Successfully cancelled the first Reservation"+"</b>");
     
     //******** Prerequisite 4:Create Reservation*************//
     
     String reservationId2 = CreateReservation.createReservation("DS_27").get("reservationId");

    	if (!reservationId.equals("error")) {

    		WSClient.setData("{var_resvId2}", reservationId2);
    		WSClient.setData("{var_reservation_id2}", reservationId2);
    		 WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+reservationId2+"</b>");
    		
    		 //******** Prerequisite 5:Cancel Reservation*************//
    	    
    		   CancelReservation.cancelReservation("DS_01");

    	     String res_status2 = "CANCELLED";
    	     String res_ExtStatus2 = "CANCELLED";

    	     WSClient.setData("{var_status2}", res_status2);
    	     WSClient.setData("{var_ExtStatus2}", res_ExtStatus2);
    	     WSClient.writeToReport(LogStatus.INFO,"<b>Successfully cancelled the second Reservation:"+"</b>");
     
     String stayHistoryReq=WSClient.createSOAPMessage("OWSStayHistory","DS_04");
     String stayHistoryRes=WSClient.processSOAPMessage(stayHistoryReq);
     if(WSAssert.assertIfElementExists(stayHistoryRes, "Result_Text_TextElement",true)) {
      
      /**** Verifying that the error message is populated on the response ********/
      
      String message=WSAssert.getElementValue(stayHistoryRes, "Result_Text_TextElement", XMLType.RESPONSE);
      WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
      }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode",true)) {
      
      /**** Verifying whether the error Message is populated on the response ****/
      
      String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode", XMLType.RESPONSE);
      WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
     }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_GDSError",true)) {
         
         /**** Verifying whether the error Message is populated on the response ****/
         
         String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_GDSError", XMLType.RESPONSE);
         WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
        }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_resultStatusFlag", false))
     {
      /***Checking for the existence of the ResultStatusFlag**/
     
      if (WSAssert.assertIfElementValueEquals(stayHistoryRes,
        "StayHistoryResponse_Result_resultStatusFlag", "SUCCESS", false)) {
       
        
        LinkedHashMap<String,String>  db=new LinkedHashMap<String,String> ();
        LinkedHashMap<String,String>  actualValues= new LinkedHashMap<String,String> ();
        HashMap<String,String>  xpath=new HashMap<String,String> ();
                    xpath.put("Profile_ProfileIDs_UniqueID", "Profiles_Profile_ProfileIDs");
                    xpath.put("Customer_PersonName_firstName", "Profile_Customer_PersonName");
                    xpath.put("Customer_PersonName_lastName", "Profile_Customer_PersonName");
                    xpath.put("HotelReservation_UniqueIDList_UniqueID", "StayHistoryResponse_HotelReservations_HotelReservation");
                    String query1=WSClient.getQuery("QS_01");
        db =WSClient.getDBRow(query1);
        actualValues = WSClient.getSingleNodeList(stayHistoryRes,xpath,false,XMLType.RESPONSE);
      // actualValues.put("RESV_NAME_ID",reservationId);
       //WSClient.writeToReport(LogStatus.INFO, actualValues.toString());
        if(WSAssert.assertEquals(db,actualValues,false)) {
        
         
        }
                else
        {
         //WSClient.writeToReport(LogStatus.FAIL, "Discrepancy identified between actual and expected records" );
        }
      }
      else {
       /**The ResultStatusFlag not found.This indicates an error in the schema ****/
       WSClient.writeToReport(LogStatus.FAIL,"Stay History Operation unsuccessful");
      }}
     else {
      /**The ResultStatusFlag not found.This indicates an error in the schema ****/
      WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
     }
        
     
      
     
    } }
 }}}}
 catch (Exception e) {
   WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
  }
 } 

@Test(groups = {"minimumRegression","OWS","StayHistory","StayHistory"})

/***** Method to verify that an error message is obtained when a date range not associated with the provided profile ID is 
 * given in the request for a configured channel*****/

/*****
 * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Cancel Reservation.
 * 
 *****/

public void stayHistory_40327() {
 try {

  String testName = "stayHistory_40327";
  WSClient.startTest(testName,
    "Verify that an error message is obtained when a date range not associated with the provided profile ID is given in the request", "minimumRegression");

  if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
String resort = OPERALib.getResort();
String channel = OWSLib.getChannel();
String owsResort=OWSLib.getChannelResort(resort, channel);
WSClient.setData("{var_owsResort}",owsResort);
String operaProfileID="";

//******** Setting the OWS Header *************//

OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
  OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
OPERALib.setOperaHeader(OPERALib.getUserName());
WSClient.setData("{var_resort}", resort);


//******** Prerequisite 1:Create Profile*************//

if (operaProfileID.equals(""))
	operaProfileID = CreateProfile.createProfile("DS_01");
if (!operaProfileID.equals("error")) {
	WSClient.setData("{var_profileId}", operaProfileID);
	WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
  WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
  WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
  WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
  WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));


  //******** Prerequisite 2:Create Reservation*************//
  
  String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");

 	if (!reservationId.equals("error")) {

 		WSClient.setData("{var_reservation_id}", reservationId);

 WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+reservationId+"</b>");


   if (reservationId != null && reservationId != "") {
    
      //******** Prerequisite 3:Cancel Reservation*************//
    
	   CancelReservation.cancelReservation("DS_01");

     String res_status = "CANCELLED";
     String res_ExtStatus = "CANCELLED";

     WSClient.setData("{var_status}", res_status);
     WSClient.setData("{var_ExtStatus}", res_ExtStatus);
     WSClient.writeToReport(LogStatus.INFO,"<b>Successfully cancelled the Reservation:"+"</b>");
     
     String stayHistoryReq=WSClient.createSOAPMessage("OWSStayHistory","DS_05");
     String stayHistoryRes=WSClient.processSOAPMessage(stayHistoryReq);
     
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_resultStatusFlag", false))
     {
      /***Checking for the existence of the ResultStatusFlag**/
     
     WSAssert.assertIfElementValueEquals(stayHistoryRes,
       "StayHistoryResponse_Result_resultStatusFlag", "SUCCESS", false);
     }
     else {
      /**The ResultStatusFlag not found.This indicates an error in the schema ****/
      WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
     }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "Result_Text_TextElement",false)) {
      
      /**** Verifying that the error message is populated on the response ********/
      
      String message=WSAssert.getElementValue(stayHistoryRes, "Result_Text_TextElement", XMLType.RESPONSE);
      WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
      }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode",true)) {
      
      /**** Verifying whether the error Message is populated on the response ****/
      
      String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode", XMLType.RESPONSE);
      WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
     }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_GDSError",true)) {
         
         /**** Verifying whether the error Message is populated on the response ****/
         
         String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_GDSError", XMLType.RESPONSE);
         WSClient.writeToReport(LogStatus.FAIL, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
        }
    
        
     
      
     
    } 
 }}}}
 catch (Exception e) {
   WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
  }
 }




//@Test(groups = {"minimumRegression","OWS","StayHistory","StayHistory"})

/***** Method to verify that an error message is obtained when an invalid date range is 
 * given in the request for a configured channel*****/

/*****
 * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Cancel Reservation.
 * 
 *****/

public void stayHistory_40326() {
 try {

  String testName = "stayHistory_40326";
  WSClient.startTest(testName,
    "Verify that an error message is obtained when an invalid date range is given in the request", "minimumRegression");

  if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
String resort = OPERALib.getResort();
String channel = OWSLib.getChannel();
String operaProfileID="";


//******** Setting the OWS Header *************//

OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
  OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
OPERALib.setOperaHeader(OPERALib.getUserName());
WSClient.setData("{var_resort}", resort);


//******** Prerequisite 1:Create Profile*************//

if (operaProfileID.equals(""))
	operaProfileID = CreateProfile.createProfile("DS_01");
if (!operaProfileID.equals("error")) {
	WSClient.setData("{var_profileId}", operaProfileID);
	WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
  WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
  WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
  WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
  WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));


  //******** Prerequisite 2:Create Reservation*************//
  
  String reservationId = CreateReservation.createReservation("DS_01").get("reservationId");

 	if (!reservationId.equals("error")) {

 		WSClient.setData("{var_reservation_id}", reservationId);

 WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+reservationId+"</b>");


   if (reservationId != null && reservationId != "") {
    
      //******** Prerequisite 3:Cancel Reservation*************//
    
	   CancelReservation.cancelReservation("DS_01");

     String res_status = "CANCELLED";
     String res_ExtStatus = "CANCELLED";

     WSClient.setData("{var_status}", res_status);
     WSClient.setData("{var_ExtStatus}", res_ExtStatus);
     
     String stayHistoryReq=WSClient.createSOAPMessage("OWSStayHistory","DS_06");
     String stayHistoryRes=WSClient.processSOAPMessage(stayHistoryReq);
     if(WSAssert.assertIfElementExists(stayHistoryRes, "Result_Text_TextElement",true)) {
      
      /**** Verifying that the error message is populated on the response ********/
      
      String message=WSAssert.getElementValue(stayHistoryRes, "Result_Text_TextElement", XMLType.RESPONSE);
      WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
      }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode",true)) {
      
      /**** Verifying whether the error Message is populated on the response ****/
      
      String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode", XMLType.RESPONSE);
      WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
     }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_GDSError",true)) {
         
         /**** Verifying whether the error Message is populated on the response ****/
         
         String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_GDSError", XMLType.RESPONSE);
         WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
        }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_resultStatusFlag", false))
     {
      /***Checking for the existence of the ResultStatusFlag**/
     
     WSAssert.assertIfElementValueEquals(stayHistoryRes,
       "StayHistoryResponse_Result_resultStatusFlag", "FAIL", false);
     }
     else {
      /**The ResultStatusFlag not found.This indicates an error in the schema ****/
      WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
     }
        
     
      
     
    } 
 }}}}
 catch (Exception e) {
   WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
  }
}

@Test(groups = {"minimumRegression","OWS","StayHistory","StayHistory"})

/***** Method to verify that an error message is obtained when a profile ID with more than one reservation associated to it is
 * given in the request for a configured channel*****/

/*****
 * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Cancel Reservation.
 * 
 *****/

public void stayHistory_40328() {
 try {

  String testName = "stayHistory_40328";
  WSClient.startTest(testName,
    "Verify that the past reservation details are fetched when a profile ID with more than one reservation associated to it is given in the request", "minimumRegression");

  if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
String resort = OPERALib.getResort();
String channel = OWSLib.getChannel();
String owsResort=OWSLib.getChannelResort(resort, channel);
WSClient.setData("{var_owsResort}",owsResort);
String operaProfileID="";


//******** Setting the OWS Header *************//

OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
  OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
OPERALib.setOperaHeader(OPERALib.getUserName());
WSClient.setData("{var_resort}", resort);


//******** Prerequisite 1:Create Profile*************//

if (operaProfileID.equals(""))
	operaProfileID = CreateProfile.createProfile("DS_01");
if (!operaProfileID.equals("error")) {
	WSClient.setData("{var_profileId}", operaProfileID);
	WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" + operaProfileID+"</b>");
  WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
  WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
  WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
  WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));


  //******** Prerequisite 2:Create Reservation*************//
  
  String reservationId1 = CreateReservation.createReservation("DS_01").get("reservationId");

 	if (!reservationId1.equals("error")) {

 		WSClient.setData("{var_reservation_id}", reservationId1);
 		WSClient.setData("{var_resvId}", reservationId1);

 WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+reservationId1+"</b>");

   if (reservationId1 != null && reservationId1 != "") {
    
      //******** Prerequisite 3:Cancel Reservation*************//
    
	   CancelReservation.cancelReservation("DS_01");

     String res_status = "CANCELLED";
     String res_ExtStatus = "CANCELLED";

     WSClient.setData("{var_status}", res_status);
     WSClient.setData("{var_ExtStatus}", res_ExtStatus);
     WSClient.writeToReport(LogStatus.INFO,"<b>Successfully cancelled the first Reservation:"+"</b>");
     
     String reservationId2 = CreateReservation.createReservation("DS_01").get("reservationId");

 	if (!reservationId2.equals("error")) {

 		WSClient.setData("{var_reservation_id2}", reservationId2);
 		WSClient.setData("{var_resvId2}", reservationId2);

 WSClient.writeToReport(LogStatus.INFO,"Reservation ID 2:"+reservationId2);

      if (reservationId2 != null && reservationId2 != "") {
       
         //******** Prerequisite 3:Cancel Reservation*************//
       
    	  CancelReservation.cancelReservation("DS_01");
        String res_status2 = "CANCELLED";
        String res_ExtStatus2 = "CANCELLED";

        WSClient.setData("{var_status}", res_status2);
        WSClient.setData("{var_ExtStatus}", res_ExtStatus2);
        WSClient.writeToReport(LogStatus.INFO,"<b>Successfully cancelled the second Reservation:"+"</b>");
     
     String stayHistoryReq=WSClient.createSOAPMessage("OWSStayHistory","DS_01");
     String stayHistoryRes=WSClient.processSOAPMessage(stayHistoryReq);
     if(WSAssert.assertIfElementExists(stayHistoryRes, "Result_Text_TextElement",true)) {
      
      /**** Verifying that the error message is populated on the response ********/
      
      String message=WSAssert.getElementValue(stayHistoryRes, "Result_Text_TextElement", XMLType.RESPONSE);
      WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
      }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode",true)) {
      
      /**** Verifying whether the error Message is populated on the response ****/
      
      String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_OperaErrorCode", XMLType.RESPONSE);
      WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
     }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_GDSError",true)) {
         
         /**** Verifying whether the error Message is populated on the response ****/
         
         String message=WSAssert.getElementValue(stayHistoryRes, "StayHistoryResponse_Result_GDSError", XMLType.RESPONSE);
         WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Stay History response is :"+ message+"</b>");
        }
     if(WSAssert.assertIfElementExists(stayHistoryRes, "StayHistoryResponse_Result_resultStatusFlag", false))
     {
      /***Checking for the existence of the ResultStatusFlag**/
     
    	 if (WSAssert.assertIfElementValueEquals(stayHistoryRes,
    		        "StayHistoryResponse_Result_resultStatusFlag", "SUCCESS", false)) {
    		       
    		        
    		        List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
    		        List<LinkedHashMap<String,String>> actualValues= new ArrayList<LinkedHashMap<String,String>>();
    		        HashMap<String,String> xpath=new HashMap<String,String>();
    		                    xpath.put("Profile_ProfileIDs_UniqueID", "StayHistoryResponse_HotelReservations_HotelReservation");
    		                    xpath.put("Customer_PersonName_firstName", "StayHistoryResponse_HotelReservations_HotelReservation");
    		                    xpath.put("Customer_PersonName_lastName", "StayHistoryResponse_HotelReservations_HotelReservation");
    		                    String query1=WSClient.getQuery("QS_02");
    		        db =WSClient.getDBRows(query1);
    		        actualValues = WSClient.getMultipleNodeList(stayHistoryRes,xpath,false,XMLType.RESPONSE);
   		        actualValues.get(0).put("RESV_NAME_ID",reservationId1);
   		     actualValues.get(1).put("RESV_NAME_ID",reservationId2);
    		        if(WSAssert.assertEquals(actualValues,db,false)) {
    		        
    		         
    		        }
    		               
    	 }}	       
      
     else {
      /**The ResultStatusFlag not found.This indicates an error in the schema ****/
      WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
     }}}}}}}}
        
      
       
 catch (Exception e) {
   WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
  }
 } 


}