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

public class FetchRoomSetup extends WSSetUp {
	
	/**
	 * @author ketvaidy
	 */
//	
//	@Test(groups = {"minimumRegression","OWS","ReservationAdvanced","FetchRoomSetup"})
//	 
//	 
//	 public void fetchRoomSetup_60008() {
//	  try {
//	   String testName = "fetchRoomSetup_60008";
//	   WSClient.startTest(testName,
//	     "Verify that the correct room setup information is retrieved based on Roomtype and RoomNumber with channel where channel and carrier name are different",
//	     "minimumRegression");
//
//	 String resort = OPERALib.getResort();
//	 String channel = OWSLib.getChannel(3);
//		String owsResort=OWSLib.getChannelResort(resort, channel);
//		WSClient.setData("{var_owsResort}",owsResort);
//	 String chain=OPERALib.getChain();
//	 
//
//	   //******** Setting the OWS Header *************//
//	 
//	 if(OperaPropConfig.getPropertyConfigResults(new String[] {"Rooms","RoomType"})){
//	 OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
//	   OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
//	 OPERALib.setOperaHeader(OPERALib.getUserName());
//	 WSClient.setData("{var_resort}", resort);
//	 WSClient.setData("{var_chain}", chain);
//	 WSClient.setData("{var_roomNumber}", OperaPropConfig.getDataSetForCode("Rooms", "DS_28"));
//	 WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
//	 
//	 //************Preparing FetchRoomSetup Request and getting the response************//
//	 
//	 String fetchRoomSetupReq = WSClient.createSOAPMessage("OWSFetchRoomSetup", "DS_01");
//	 String fetchRoomSetupRes = WSClient.processSOAPMessage(fetchRoomSetupReq);
//	 
//	  
//	      if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "Result_Text_TextElement",true)) {
//	       
//	       /**** Verifying that the error message is populated on the response ********/
//	       
//	       String message=WSAssert.getElementValue(fetchRoomSetupRes, "Result_Text_TextElement", XMLType.RESPONSE);
//	       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
//	       }
//	      if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_OperaErrorCode",true)) {
//	       
//	       /**** Verifying whether the error Message is populated on the response ****/
//	       
//	       String message=WSAssert.getElementValue(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
//	       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
//	      }
//	      if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError",true)) {
//	          
//	          /**** Verifying whether the error Message is populated on the response ****/
//	          
//	          String message=WSAssert.getElementValue(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError", XMLType.RESPONSE);
//	          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
//	      }
//	      if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_resultStatusFlag", false))
//	      {
//	       /***Checking for the existence of the ResultStatusFlag**/
//	      
//	      if (WSAssert.assertIfElementValueEquals(fetchRoomSetupRes,
//	        "FetchRoomSetupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
//	       
//	        LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
//	        LinkedHashMap<String,String> actualValues= new LinkedHashMap<String,String>();
//	        HashMap<String,String> xpath=new HashMap<String,String>();
//	        xpath.put("FetchRoomSetupResponse_RoomSetup_RoomNumber", "FetchRoomSetupResponse_RoomSetup");
//	        xpath.put("FetchRoomSetupResponse_RoomSetup_SuiteType", "FetchRoomSetupResponse_RoomSetup");
//	        xpath.put("FetchRoomSetupResponse_RoomSetup_SmokingPreference", "FetchRoomSetupResponse_RoomSetup");
//	        xpath.put("RoomSetup_Features_Features_Feature", "RoomSetup_Features_Features");
//	        String query1=WSClient.getQuery("QS_01");
//	        db =WSClient.getDBRow(query1);
//	        actualValues = WSClient.getSingleNodeList(fetchRoomSetupRes,xpath,false,XMLType.RESPONSE);
//	        if(WSAssert.assertEquals(db,actualValues,false)) {
//	       
//	         
//	         
//	        }
//	        else
//	        {
//	         WSClient.writeToReport(LogStatus.FAIL, "Discrepancy found between the fetched and expected details" );
//	        }
//	       
//	      
//	      }
//	      else
//	      {
//	       WSClient.writeToReport(LogStatus.FAIL, "Fetch Room Setup Operation Unsuccessful");
//	      }
//	      }
//	      else {
//	       /**The ResultStatusFlag not found.This indicates an error in the schema ****/
//	       WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
//	      }
//	          
//	 }
//	       
//	      
//	     } 
//	    
//	  catch (Exception e) {
//	    WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
//	   }
//	}
	
@Test(groups = {"sanity","OWS","ReservationAdvanced","FetchRoomSetup"})
 
 /***** Method to verify that the correct room setup information is retrieved 
  * for a configured channel with minimum data in the request*****/

/*****
 * * * PreRequisites Required: -->Rooms should be available in the property
 * 
 *****/
 
 
 public void fetchRoomSetup_40320() {
  try {
   String testName = "fetchRoomSetup_40320";
   WSClient.startTest(testName,
     "Verify that the correct room setup information is retrieved based on Roomtype and RoomNumber",
     "sanity");

 String resort = OPERALib.getResort();
 String channel = OWSLib.getChannel();
	String owsResort=OWSLib.getChannelResort(resort, channel);
	WSClient.setData("{var_owsResort}",owsResort);
 String chain=OPERALib.getChain();
 

   //******** Setting the OWS Header *************//
 
 if(OperaPropConfig.getPropertyConfigResults(new String[] {"Rooms","RoomType"})){
 OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
   OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
 OPERALib.setOperaHeader(OPERALib.getUserName());
 WSClient.setData("{var_resort}", resort);
 WSClient.setData("{var_chain}", chain);
 WSClient.setData("{var_roomNumber}", OperaPropConfig.getDataSetForCode("Rooms", "DS_28"));
 WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));
 
 //************Preparing FetchRoomSetup Request and getting the response************//
 
 String fetchRoomSetupReq = WSClient.createSOAPMessage("OWSFetchRoomSetup", "DS_01");
 String fetchRoomSetupRes = WSClient.processSOAPMessage(fetchRoomSetupReq);
 
  
      if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "Result_Text_TextElement",true)) {
       
       /**** Verifying that the error message is populated on the response ********/
       
       String message=WSAssert.getElementValue(fetchRoomSetupRes, "Result_Text_TextElement", XMLType.RESPONSE);
       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
       }
      if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_OperaErrorCode",true)) {
       
       /**** Verifying whether the error Message is populated on the response ****/
       
       String message=WSAssert.getElementValue(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
      }
      if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError",true)) {
          
          /**** Verifying whether the error Message is populated on the response ****/
          
          String message=WSAssert.getElementValue(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError", XMLType.RESPONSE);
          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
      }
      if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_resultStatusFlag", false))
      {
       /***Checking for the existence of the ResultStatusFlag**/
      
      if (WSAssert.assertIfElementValueEquals(fetchRoomSetupRes,
        "FetchRoomSetupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
       
        LinkedHashMap<String,String> db=new LinkedHashMap<String,String>();
        LinkedHashMap<String,String> actualValues= new LinkedHashMap<String,String>();
        HashMap<String,String> xpath=new HashMap<String,String>();
        xpath.put("FetchRoomSetupResponse_RoomSetup_RoomNumber", "FetchRoomSetupResponse_RoomSetup");
        xpath.put("FetchRoomSetupResponse_RoomSetup_SuiteType", "FetchRoomSetupResponse_RoomSetup");
        xpath.put("FetchRoomSetupResponse_RoomSetup_SmokingPreference", "FetchRoomSetupResponse_RoomSetup");
        xpath.put("RoomSetup_Features_Features_Feature", "RoomSetup_Features_Features");
        String query1=WSClient.getQuery("QS_01");
        db =WSClient.getDBRow(query1);
        actualValues = WSClient.getSingleNodeList(fetchRoomSetupRes,xpath,false,XMLType.RESPONSE);
        if(WSAssert.assertEquals(db,actualValues,false)) {
       
         
         
        }
        else
        {
         WSClient.writeToReport(LogStatus.FAIL, "Discrepancy found between the fetched and expected details" );
        }
       
      
      }
      else
      {
       WSClient.writeToReport(LogStatus.FAIL, "Fetch Room Setup Operation Unsuccessful");
      }
      }
      else {
       /**The ResultStatusFlag not found.This indicates an error in the schema ****/
       WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
      }
          
 }
       
      
     } 
    
  catch (Exception e) {
    WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
   }
}

@Test(groups = {"minimumRegression","OWS","ReservationAdvanced","FetchRoomSetup"})

/***** Method to verify that an error message is obtained when an invalid Room Type is given in the request*****/

/*****
 * * * PreRequisites Required: -->Rooms should be available in the property
 * 
 *****/

public void fetchRoomSetup_40298() {
 try {

  String testName = "fetchRoomSetup_40298";
  WSClient.startTest(testName,
    "Verify that an error message is obtained when an invalid Room Type is given in the request",
    "minimumRegression");

String resort = OPERALib.getResort();
String channel = OWSLib.getChannel();
String owsResort=OWSLib.getChannelResort(resort, channel);
WSClient.setData("{var_owsResort}",owsResort);
String chain=OPERALib.getChain();


  //******** Setting the OWS Header *************//

if(OperaPropConfig.getPropertyConfigResults(new String[] {"Rooms","RoomType"})){
OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
  OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
OPERALib.setOperaHeader(OPERALib.getUserName());
WSClient.setData("{var_resort}", resort);

WSClient.setData("{var_chain}", chain);
WSClient.setData("{var_roomNumber}", OperaPropConfig.getDataSetForCode("Rooms", "DS_28"));
WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));

//************Preparing FetchRoomSetup Request and getting the response************//

String fetchRoomSetupReq = WSClient.createSOAPMessage("OWSFetchRoomSetup", "DS_02");
String fetchRoomSetupRes = WSClient.processSOAPMessage(fetchRoomSetupReq);

 
     if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "Result_Text_TextElement",true)) {
      
      /**** Verifying that the error message is populated on the response ********/
      
      String message=WSAssert.getElementValue(fetchRoomSetupRes, "Result_Text_TextElement", XMLType.RESPONSE);
      WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
      }
     if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_OperaErrorCode",true)) {
      
      /**** Verifying whether the error Message is populated on the response ****/
      
      String message=WSAssert.getElementValue(fetchRoomSetupRes, "StayHistoryResponse_Result_OperaErrorCode", XMLType.RESPONSE);
      WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
     }
     if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError",true)) {
         
         /**** Verifying whether the error Message is populated on the response ****/
         
         String message=WSAssert.getElementValue(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError", XMLType.RESPONSE);
         WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
     }
     if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_resultStatusFlag", false))
     {
      /***Checking for the existence of the ResultStatusFlag**/
     
     WSAssert.assertIfElementValueEquals(fetchRoomSetupRes,
       "FetchRoomSetupResponse_Result_resultStatusFlag", "FAIL", false);

      
     
     }
     
     else {
      /**The ResultStatusFlag not found.This indicates an error in the schema ****/
      WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
     }
         
}
      
     
    } 
   
 catch (Exception e) {
   WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
  }
}

@Test(groups = {"minimumRegression","OWS","ReservationAdvanced","FetchRoomSetup"})

/***** Method to verify that the correct room setup information is retrieved for a configured channel 
 * when an invalid Room Number is given in the request*****/

/*****
 * * * PreRequisites Required: -->Rooms should be available in the property
 * 
 *****/

public void fetchRoomSetup_40299() {
try {

String testName = "fetchRoomSetup_40299";
WSClient.startTest(testName,
  "Verify that an error message is obtained when an invalid Room Number is given in the request",
  "minimumRegression");

String resort = OPERALib.getResort();
String channel = OWSLib.getChannel();
String owsResort=OWSLib.getChannelResort(resort, channel);
WSClient.setData("{var_owsResort}",owsResort);
String chain=OPERALib.getChain();


//******** Setting the OWS Header *************//


if(OperaPropConfig.getPropertyConfigResults(new String[] {"Rooms","RoomType"})){
OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
OPERALib.setOperaHeader(OPERALib.getUserName());
WSClient.setData("{var_resort}", resort);
WSClient.setData("{var_chain}", chain);
WSClient.setData("{var_roomNumber}", OperaPropConfig.getDataSetForCode("Rooms", "DS_28"));
WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));

//************Preparing FetchRoomSetup Request and getting the response************//

String fetchRoomSetupReq = WSClient.createSOAPMessage("OWSFetchRoomSetup", "DS_03");
String fetchRoomSetupRes = WSClient.processSOAPMessage(fetchRoomSetupReq);


   if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "Result_Text_TextElement",true)) {
    
    /**** Verifying that the error message is populated on the response ********/
    
    String message=WSAssert.getElementValue(fetchRoomSetupRes, "Result_Text_TextElement", XMLType.RESPONSE);
    WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
    }
   if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_OperaErrorCode",true)) {
    
    /**** Verifying whether the error Message is populated on the response ****/
    
    String message=WSAssert.getElementValue(fetchRoomSetupRes, "StayHistoryResponse_Result_OperaErrorCode", XMLType.RESPONSE);
    WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
   }
   if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError",true)) {
       
       /**** Verifying whether the error Message is populated on the response ****/
       
       String message=WSAssert.getElementValue(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError", XMLType.RESPONSE);
       WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
   }
   if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_resultStatusFlag", false))
   {
    /***Checking for the existence of the ResultStatusFlag**/
   
   WSAssert.assertIfElementValueEquals(fetchRoomSetupRes,
     "FetchRoomSetupResponse_Result_resultStatusFlag", "FAIL", false);

      
    
   
   }
   
   else {
    /**The ResultStatusFlag not found.This indicates an error in the schema ****/
    WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
   }
       
   
}
   
  } 
 
catch (Exception e) {
 WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
}
}

//@Test(groups = {"minimumRegression","OWS","ReservationAdvanced","FetchRoomSetup"})

/***** Method to verify that the correct room setup information is retrieved for a configured channel 
 * when no Chain Code is specified in the request*****/

/*****
 * * * PreRequisites Required: -->Rooms should be available in the property
 * 
 *****/

public void fetchRoomSetup_40300() {
try {

String testName = "fetchRoomSetup_40300";
WSClient.startTest(testName,
"Verify that an error message is obtained when no Chain Code is specified in the request",
"minimumRegression");

String resort = OPERALib.getResort();
String channel = OWSLib.getChannel();
String chain=OPERALib.getChain();


//******** Setting the OWS Header *************//

if(OperaPropConfig.getPropertyConfigResults(new String[] {"Rooms","RoomType"})){
OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
OPERALib.setOperaHeader(OPERALib.getUserName());
WSClient.setData("{var_resort}", resort);
WSClient.setData("{var_chain}", chain);
WSClient.setData("{var_roomNumber}", OperaPropConfig.getDataSetForCode("Rooms", "DS_28"));
WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));


//************Preparing FetchRoomSetup Request and getting the response************//

String fetchRoomSetupReq = WSClient.createSOAPMessage("OWSFetchRoomSetup", "DS_04");
String fetchRoomSetupRes = WSClient.processSOAPMessage(fetchRoomSetupReq);

 if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "Result_Text_TextElement",true)) {
  
  /**** Verifying that the error message is populated on the response ********/
  
  String message=WSAssert.getElementValue(fetchRoomSetupRes, "Result_Text_TextElement", XMLType.RESPONSE);
  WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
  }
 if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_OperaErrorCode",true)) {
  
  /**** Verifying whether the error Message is populated on the response ****/
  
  String message=WSAssert.getElementValue(fetchRoomSetupRes, "StayHistoryResponse_Result_OperaErrorCode", XMLType.RESPONSE);
  WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
 }
 if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError",true)) {
     
     /**** Verifying whether the error Message is populated on the response ****/
     
     String message=WSAssert.getElementValue(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError", XMLType.RESPONSE);
     WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
 }
 if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_resultStatusFlag", false))
 {
  /***Checking for the existence of the ResultStatusFlag**/
 
 WSAssert.assertIfElementValueEquals(fetchRoomSetupRes,
   "FetchRoomSetupResponse_Result_resultStatusFlag", "FAIL", false);

   
 
 }
 
 else {
  /**The ResultStatusFlag not found.This indicates an error in the schema ****/
  WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
 }
     
}
  
 
} 

catch (Exception e) {
WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
}
}

@Test(groups = {"minimumRegression","OWS","ReservationAdvanced","FetchRoomSetup"})

/***** Method to verify that the correct room setup information is retrieved for a configured channel 
 * when no Hotel Code is specified in the request*****/

/*****
 * * * PreRequisites Required: -->Rooms should be available in the property
 * 
 *****/

public void fetchRoomSetup_40301() {
try {

String testName = "fetchRoomSetup_40301";
WSClient.startTest(testName,
"Verify that an error message is obtained when no Hotel Code is specified in the request",
"minimumRegression");

String resort = OPERALib.getResort();
String channel = OWSLib.getChannel();
String owsResort=OWSLib.getChannelResort(resort, channel);
WSClient.setData("{var_owsResort}",owsResort);
String chain=OPERALib.getChain();


//******** Setting the OWS Header *************//

if(OperaPropConfig.getPropertyConfigResults(new String[] {"Rooms","RoomType"})){
OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
OPERALib.setOperaHeader(OPERALib.getUserName());
WSClient.setData("{var_resort}", resort);
WSClient.setData("{var_chain}", chain);
WSClient.setData("{var_roomNumber}", OperaPropConfig.getDataSetForCode("Rooms", "DS_28"));
WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));

//************Preparing FetchRoomSetup Request and getting the response************//

String fetchRoomSetupReq = WSClient.createSOAPMessage("OWSFetchRoomSetup", "DS_05");
String fetchRoomSetupRes = WSClient.processSOAPMessage(fetchRoomSetupReq);


if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "Result_Text_TextElement",true)) {

/**** Verifying that the error message is populated on the response ********/

String message=WSAssert.getElementValue(fetchRoomSetupRes, "Result_Text_TextElement", XMLType.RESPONSE);
WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
}
if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_OperaErrorCode",true)) {

/**** Verifying whether the error Message is populated on the response ****/

String message=WSAssert.getElementValue(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
}
if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError",true)) {
    
    /**** Verifying whether the error Message is populated on the response ****/
    
    String message=WSAssert.getElementValue(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError", XMLType.RESPONSE);
    WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
}
if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_resultStatusFlag", false))
{
/***Checking for the existence of the ResultStatusFlag**/

WSAssert.assertIfElementValueEquals(fetchRoomSetupRes,
 "FetchRoomSetupResponse_Result_resultStatusFlag", "FAIL", false); 

}

else {
/**The ResultStatusFlag not found.This indicates an error in the schema ****/
WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
}
   

}

} 

catch (Exception e) {
WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
}
}
 
@Test(groups = {"minimumRegression","OWS","ReservationAdvanced","FetchRoomSetup"})

/***** Method to verify that the correct room setup information is retrieved for a configured channel 
 * when no Room Number is specified in the request*****/

/*****
 * * * PreRequisites Required: -->Rooms should be available in the property
 * 
 *****/

public void fetchRoomSetup_40302() {
try {

String testName = "fetchRoomSetup_40302";
WSClient.startTest(testName,
"Verify that the correct room setup information is retrieved based on the Room Type",
"minimumRegression");

String resort = OPERALib.getResort();
String channel = OWSLib.getChannel();
String owsResort=OWSLib.getChannelResort(resort, channel);
WSClient.setData("{var_owsResort}",owsResort);
String chain=OPERALib.getChain();
WSClient.setData("{var_chain}", chain);

//******** Setting the OWS Header *************//

if(OperaPropConfig.getPropertyConfigResults(new String[] {"Rooms","RoomType"})){
OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
OPERALib.setOperaHeader(OPERALib.getUserName());
WSClient.setData("{var_resort}", resort);

WSClient.setData("{var_roomNumber}", OperaPropConfig.getDataSetForCode("Rooms", "DS_28"));
WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));


//************Preparing FetchRoomSetup Request and getting the response************//

String fetchRoomSetupReq = WSClient.createSOAPMessage("OWSFetchRoomSetup", "DS_06");
String fetchRoomSetupRes = WSClient.processSOAPMessage(fetchRoomSetupReq);

if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "Result_Text_TextElement",true)) {

/**** Verifying that the error message is populated on the response ********/

String message=WSAssert.getElementValue(fetchRoomSetupRes, "Result_Text_TextElement", XMLType.RESPONSE);
WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
}
if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_OperaErrorCode",true)) {

/**** Verifying whether the error Message is populated on the response ****/

String message=WSAssert.getElementValue(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
}
if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError",true)) {
    
    /**** Verifying whether the error Message is populated on the response ****/
    
    String message=WSAssert.getElementValue(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError", XMLType.RESPONSE);
    WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
}

if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_resultStatusFlag", false))
{
 /***Checking for the existence of the ResultStatusFlag**/

if (WSAssert.assertIfElementValueEquals(fetchRoomSetupRes,
  "FetchRoomSetupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
 
 
  List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
	List<LinkedHashMap<String,String>> actualValues= new ArrayList<LinkedHashMap<String,String>>();
	HashMap<String, String> xpath=new HashMap<String, String>();
	 xpath.put("FetchRoomSetupResponse_RoomSetup_RoomNumber", "FetchRoomSetupResponse_RoomSetup");
	  xpath.put("FetchRoomSetupResponse_RoomSetup_SuiteType", "FetchRoomSetupResponse_RoomSetup");
	  xpath.put("FetchRoomSetupResponse_RoomSetup_SmokingPreference", "FetchRoomSetupResponse_RoomSetup");
	  String query1=WSClient.getQuery("QS_03");
	  db =WSClient.getDBRows(query1);
	  actualValues = WSClient.getMultipleNodeList(fetchRoomSetupRes,xpath,false,XMLType.RESPONSE);
  if(WSAssert.assertEquals(actualValues,db,false)) {

   
   
  }
  else
  {
   WSClient.writeToReport(LogStatus.FAIL, "Discrepancy found between the fetched and expected details" );
  }
	  

}
else
{
 WSClient.writeToReport(LogStatus.FAIL, "Fetch Room Setup Operation Unsuccessful");
}
}
else {
 /**The ResultStatusFlag not found.This indicates an error in the schema ****/
 WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
}
    
}
} 

catch (Exception e) {
WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
}
}

@Test(groups = {"minimumRegression","OWS","ReservationAdvanced","FetchRoomSetup"})

/***** Method to verify that the correct room setup information is retrieved for a configured channel 
 * when no Room Type is specified in the request*****/

/*****
 * * * PreRequisites Required: -->Rooms should be available in the property
 * 
 *****/

public void fetchRoomSetup_40303() {
try {

String testName = "fetchRoomSetup_40303";
WSClient.startTest(testName,
"Verify that the correct room setup information is retrieved based on the Room Number",
"minimumRegression");

String resort = OPERALib.getResort();
String channel = OWSLib.getChannel();
String owsResort=OWSLib.getChannelResort(resort, channel);
WSClient.setData("{var_owsResort}",owsResort);
String chain=OPERALib.getChain();


//******** Setting the OWS Header *************//

if(OperaPropConfig.getPropertyConfigResults(new String[] {"Rooms","RoomType"})){
OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
OPERALib.setOperaHeader(OPERALib.getUserName());
WSClient.setData("{var_resort}", resort);
WSClient.setData("{var_chain}", chain);
WSClient.setData("{var_roomNumber}", OperaPropConfig.getDataSetForCode("Rooms", "DS_28"));
WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));


//************Preparing FetchRoomSetup Request and getting the response************//

String fetchRoomSetupReq = WSClient.createSOAPMessage("OWSFetchRoomSetup", "DS_07");
String fetchRoomSetupRes = WSClient.processSOAPMessage(fetchRoomSetupReq);
if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "Result_Text_TextElement",true)) {

/**** Verifying that the error message is populated on the response ********/

String message=WSAssert.getElementValue(fetchRoomSetupRes, "Result_Text_TextElement", XMLType.RESPONSE);
WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
}
if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_OperaErrorCode",true)) {

/**** Verifying whether the error Message is populated on the response ****/

String message=WSAssert.getElementValue(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
}
if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError",true)) {
    
    /**** Verifying whether the error Message is populated on the response ****/
    
    String message=WSAssert.getElementValue(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError", XMLType.RESPONSE);
    WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
}

if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_resultStatusFlag", false))
{
 /***Checking for the existence of the ResultStatusFlag**/

if (WSAssert.assertIfElementValueEquals(fetchRoomSetupRes,
  "FetchRoomSetupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
 
 
  List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
	List<LinkedHashMap<String,String>> actualValues= new ArrayList<LinkedHashMap<String,String>>();
	HashMap<String, String> xpath=new HashMap<String, String>();
	 xpath.put("FetchRoomSetupResponse_RoomSetup_RoomNumber", "FetchRoomSetupResponse_RoomSetup");
     xpath.put("FetchRoomSetupResponse_RoomSetup_SuiteType", "FetchRoomSetupResponse_RoomSetup");
     xpath.put("FetchRoomSetupResponse_RoomSetup_SmokingPreference", "FetchRoomSetupResponse_RoomSetup");
     xpath.put("RoomSetup_Features_Features_Feature", "RoomSetup_Features_Features");
       String query1=WSClient.getQuery("QS_04");
       db =WSClient.getDBRows(query1);
       actualValues = WSClient.getMultipleNodeList(fetchRoomSetupRes,xpath,false,XMLType.RESPONSE);
    if(WSAssert.assertEquals(actualValues,db,false)) {
   
   
}
  else
  {
   WSClient.writeToReport(LogStatus.FAIL, "Discrepancy found between the fetched and expected details" );
  }
	  

}
else
{
 WSClient.writeToReport(LogStatus.FAIL, "Fetch Room Setup Operation Unsuccessful");
}
}
else {
 /**The ResultStatusFlag not found.This indicates an error in the schema ****/
 WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
}
    
 


}
} 

catch (Exception e) {
WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
}
}

@Test(groups = {"minimumRegression","OWS","ReservationAdvanced","FetchRoomSetup"})

/***** Method to verify that the correct room setup information is retrieved for a configured channel 
 * when no Room Type and Room Number is specified in the request*****/

/*****
 * * * PreRequisites Required: -->Rooms should be available in the property
 * 
 *****/

public void fetchRoomSetup_40305() {
try {

String testName = "fetchRoomSetup_40305";
WSClient.startTest(testName,
"Verify that the setup information of all the rooms associated to a given Hotel Code is correctly fetched",
"minimumRegression");

String resort = OPERALib.getResort();
String channel = OWSLib.getChannel();
String owsResort=OWSLib.getChannelResort(resort, channel);
WSClient.setData("{var_owsResort}",owsResort);
String chain=OPERALib.getChain();


//******** Setting the OWS Header *************//

if(OperaPropConfig.getPropertyConfigResults(new String[] {"Rooms","RoomType"})){
OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
OPERALib.setOperaHeader(OPERALib.getUserName());
WSClient.setData("{var_resort}", resort);
WSClient.setData("{var_chain}", chain);
WSClient.setData("{var_roomNumber}", OperaPropConfig.getDataSetForCode("Rooms", "DS_28"));
WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_02"));

//************Preparing FetchRoomSetup Request and getting the response************//

String fetchRoomSetupReq = WSClient.createSOAPMessage("OWSFetchRoomSetup", "DS_08");
String fetchRoomSetupRes = WSClient.processSOAPMessage(fetchRoomSetupReq);


if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "Result_Text_TextElement",true)) {

/**** Verifying that the error message is populated on the response ********/

String message=WSAssert.getElementValue(fetchRoomSetupRes, "Result_Text_TextElement", XMLType.RESPONSE);
WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
}
if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_OperaErrorCode",true)) {

/**** Verifying whether the error Message is populated on the response ****/

String message=WSAssert.getElementValue(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_OperaErrorCode", XMLType.RESPONSE);
WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
}
if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError",true)) {
    
    /**** Verifying whether the error Message is populated on the response ****/
    
    String message=WSAssert.getElementValue(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_GDSError", XMLType.RESPONSE);
    WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
}
if(WSAssert.assertIfElementExists(fetchRoomSetupRes, "FetchRoomSetupResponse_Result_resultStatusFlag", false))
{
 /***Checking for the existence of the ResultStatusFlag**/

if (WSAssert.assertIfElementValueEquals(fetchRoomSetupRes,
  "FetchRoomSetupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
 
  
  List<LinkedHashMap<String,String>> db=new ArrayList<LinkedHashMap<String,String>>();
  List<LinkedHashMap<String,String>> actualValues= new ArrayList<LinkedHashMap<String,String>>();
  HashMap<String,String> xpath=new HashMap<String,String>();
  xpath.put("FetchRoomSetupResponse_RoomSetup_RoomNumber", "FetchRoomSetupResponse_RoomSetup");
  String query1=WSClient.getQuery("QS_02");
  db =WSClient.getDBRows(query1);
  actualValues = WSClient.getMultipleNodeList(fetchRoomSetupRes,xpath,false,XMLType.RESPONSE);
  if(WSAssert.assertEquals(actualValues,db,false)) {
  
    
  }
  else
  {
   WSClient.writeToReport(LogStatus.FAIL, "Discrepancy found between the fetched and expected details" );
  }
 

}
else
{
 WSClient.writeToReport(LogStatus.FAIL, "Fetch Room Setup Operation Unsuccessful");
}
}
else {
 /**The ResultStatusFlag not found.This indicates an error in the schema ****/
 WSClient.writeToReport(LogStatus.FAIL,"The schema is incorrect");
}
    

 

}
} 

catch (Exception e) {
WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
}
}
}
