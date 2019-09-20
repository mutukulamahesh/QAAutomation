package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2008b.activitysync;

import java.util.HashMap;

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


public class LocationNotification extends WSSetUp {

	@Test(groups = { "sanity", "LocationNotification","HTNG2008B","HTNG","locationNotification_2008_4536" })
	public void locationNotification_2008_4536() {
		try {
			String testName = "locationNotification_2008_4536";
			WSClient.startTest(testName, "Verify that the LocationNotification message has been submitted for the requested ReservationID", "sanity");
			if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

		
			OPERALib.setOperaHeader(OPERALib.getUserName());
			
			/************ Prerequisite 1: Create Profile ****************/
			
					String profileId = CreateProfile.createProfile("DS_01");
					if(!profileId.equals("error")){

					 WSClient.setData("{VAR_RATEPLANCODE}",OperaPropConfig.getDataSetForCode("RateCode","DS_01" ));
						WSClient.setData("{VAR_ROOMTYPE}",OperaPropConfig.getDataSetForCode("RoomType","DS_01" ));
						WSClient.setData("{var_sourceCode}",OperaPropConfig.getDataSetForCode("SourceCode","DS_01" ));
						WSClient.setData("{VAR_MARKETCODE}",OperaPropConfig.getDataSetForCode("MarketCode","DS_01" ));
						
					/******** Prerequisite 5: Create Reservation **************/

						String reservationId = CreateReservation.createReservation("DS_06").get("reservationId");
						
						if(!reservationId.equals("reservationId")){
							
						WSClient.setData("{var_resvId}", reservationId);
						WSClient.setData("{var_locationText}", "Sample Notification");
						WSClient.setData("{var_resvID}", reservationId);
						WSClient.setData("{var_ext_resort}", resortExtValue);

						
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
						/*************
						 Main Operation : Create a Location Notification
						 ******************/
						String CreateLocationNotificationnReq = WSClient
								.createSOAPMessage("HTNG2008BLocationNotification", "DS_01");
						String CreateLocationNotificationnRes = WSClient
								.processSOAPMessage(CreateLocationNotificationnReq);
						
						/*************
						  Validations
						 ******************/

						if (WSAssert.assertIfElementExists(CreateLocationNotificationnRes,
								"LocationNotificationResponse_Result_resultStatusFlag", false)) {

							if (WSAssert.assertIfElementValueEquals(CreateLocationNotificationnRes,
									"LocationNotificationResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								HashMap<String, String> LocationNotificationDetails = WSClient.getDBRow(WSClient.getQuery("QS_01"));

								
								if (WSAssert.assertEquals(LocationNotificationDetails.get("RESV_NAME_ID"),
										reservationId, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Reservation ID - "+"Expected: "
													+ LocationNotificationDetails.get("RESV_NAME_ID") + ", Actual: " + reservationId);
								} else {

									WSClient.writeToReport(LogStatus.FAIL,
											"Reservation ID - "+"Expected: "
													+ LocationNotificationDetails.get("RESV_NAME_ID") + ", Actual: " + reservationId);
								}

								if (WSAssert.assertEquals(LocationNotificationDetails.get("RESORT"), resortOperaValue,
										true)) {
									WSClient.writeToReport(LogStatus.PASS, "RESORT - "+"Expected: "
											+ LocationNotificationDetails.get("RESORT") + ", Actual: " + resortOperaValue);
								} else {

									WSClient.writeToReport(LogStatus.FAIL, "RESORT - "+"Expected: "
											+ LocationNotificationDetails.get("RESORT") + ", Actual: " + resortOperaValue);
								}

								if (WSAssert.assertEquals(LocationNotificationDetails.get("LOCATOR_TEXT"),
										WSClient.getData("{var_locationText}"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Locator Text - "+"Expected: " + LocationNotificationDetails.get("LOCATOR_TEXT") + ", Actual: "
											+ WSClient.getData("{var_locationText}"));
								} else {

									WSClient.writeToReport(LogStatus.FAIL, "Locator Text - "+"Expected: " + LocationNotificationDetails.get("LOCATOR_TEXT") + ", Actual: "
											+ WSClient.getData("{var_locationText}"));
								}

							}
						}

					}
				} 

		} }catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);  
		} finally{
            try {
                if(!WSClient.getData("{var_resvId}").equals(""))
                   CancelReservation.cancelReservation("DS_02");
               } catch (Exception e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
       
		}

	}
	
	
	@Test(groups = { "minimumRegression", "LocationNotification","HTNG2008B","HTNG" })
	public void locationNotification_2008_3242() {
		try {
			String testName = "locationNotification_2008_3242";
			WSClient.startTest(testName, "Verify that the LocaitonNotification message submitted for the requested ReservaitonID with ActivityTIme details", "minimumRegression");

			if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
		
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);

			
			String startDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}");
			String endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_10}");

			OPERALib.setOperaHeader(OPERALib.getUserName());
			
			/************ Prerequisite 1: Create Profile ****************/
			
			String profileId = CreateProfile.createProfile("DS_01");
			if(!profileId.equals("error")){

				WSClient.setData("{VAR_RATEPLANCODE}",OperaPropConfig.getDataSetForCode("RateCode","DS_01" ));
				WSClient.setData("{VAR_ROOMTYPE}",OperaPropConfig.getDataSetForCode("RoomType","DS_01" ));
				WSClient.setData("{var_sourceCode}",OperaPropConfig.getDataSetForCode("SourceCode","DS_01" ));
				WSClient.setData("{VAR_MARKETCODE}",OperaPropConfig.getDataSetForCode("MarketCode","DS_01" ));
				
			/******** Prerequisite 5: Create Reservation **************/

				String reservationId = CreateReservation.createReservation("DS_06").get("reservationId");
				
				if(!reservationId.equals("reservationId")){
						
					WSClient.setData("{var_resvId}", reservationId);
						WSClient.setData("{var_locationText}", "Sample Notification");
						WSClient.setData("{var_resvID}", reservationId);
						WSClient.setData("{var_ext_resort}", resortExtValue);

						WSClient.setData("{var_start_date}", startDate + "+05:30");
						WSClient.setData("{var_end_date}", endDate + "+05:30");

						
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
						String CreateLocationNotificationnReq = WSClient
								.createSOAPMessage("HTNG2008BLocationNotification", "DS_02");
						String CreateLocationNotificationnRes = WSClient
								.processSOAPMessage(CreateLocationNotificationnReq);

						if (WSAssert.assertIfElementExists(CreateLocationNotificationnRes,
								"LocationNotificationResponse_Result_resultStatusFlag", false)) {

							if (WSAssert.assertIfElementValueEquals(CreateLocationNotificationnRes,
									"LocationNotificationResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								HashMap<String, String> LocationNotificationDetails = WSClient.getDBRow(WSClient.getQuery("QS_02"));

								if (WSAssert.assertEquals(LocationNotificationDetails.get("RESV_NAME_ID"),
										reservationId, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Reservation ID - "+"Expected: "
													+ LocationNotificationDetails.get("RESV_NAME_ID") + " , Actual: " + reservationId);
								} else {

									WSClient.writeToReport(LogStatus.FAIL,
											"Reservation ID - "+"Expected: "
													+ LocationNotificationDetails.get("RESV_NAME_ID") + " , Actual: " + reservationId);
								}

								if (WSAssert.assertEquals(LocationNotificationDetails.get("RESORT"), resortOperaValue,
										true)) {
									WSClient.writeToReport(LogStatus.PASS, "RESORT - "+"Expected: "
											+ LocationNotificationDetails.get("RESORT") + " , Actual: " + resortOperaValue);
								} else {

									WSClient.writeToReport(LogStatus.FAIL, "RESORT - "+"Expected: "
											+ LocationNotificationDetails.get("RESORT") + " , Actual: " + resortOperaValue);
								}

								if (WSAssert.assertEquals(LocationNotificationDetails.get("LOCATOR_TEXT"),
										WSClient.getData("{var_locationText}"), true)) {
									WSClient.writeToReport(LogStatus.PASS, "Locator Text - "+"Expected: " + LocationNotificationDetails.get("LOCATOR_TEXT") + " , Actual: "
											+ WSClient.getData("{var_locationText}"));
								} else {

									WSClient.writeToReport(LogStatus.FAIL, "Locator Text - "+"Expected: " + LocationNotificationDetails.get("LOCATOR_TEXT") + " , Actual: "
											+ WSClient.getData("{var_locationText}"));
								}

								if (WSAssert.assertEquals(LocationNotificationDetails.get("BEGIN_DATE"),
										startDate, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"Start Date - "+"Expected: "
													+ LocationNotificationDetails.get("BEGIN_DATE") + " , Actual: " + startDate);
								} else {

									WSClient.writeToReport(LogStatus.FAIL,
											"Start Date - "+"Expected: "
													+ LocationNotificationDetails.get("BEGIN_DATE") + " , Actual: " + startDate);
								}
								
								
								if (WSAssert.assertEquals(LocationNotificationDetails.get("END_DATE"),
										endDate, true)) {
									WSClient.writeToReport(LogStatus.PASS,
											"End Date - "+"Expected: "
													+ LocationNotificationDetails.get("END_DATE") + " , Actual: " + endDate);
								} else {

									WSClient.writeToReport(LogStatus.FAIL,
											"End Date - "+"Expected: "
													+ LocationNotificationDetails.get("END_DATE") + " , Actual: " + endDate);
								}
								
								
							}
						}
					
				}

			}
		}} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}finally{
            try {
                if(!WSClient.getData("{var_resvId}").equals(""))
                   CancelReservation.cancelReservation("DS_02");
               } catch (Exception e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
       
		}
	}
	
	
	
	
	
	
	
	@Test(groups = { "fullRegression", "LocationNotification","HTNG2008B","HTNG" })
	public void locationNotification_2008_3244() {
		try {
			String testName = "locationNotification_2008_3244";
			WSClient.startTest(testName, "Verify that the error-message is present in the response when invalid reservation id is passed in the request", "fullRegression");
			if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_ext_resort}", resortExtValue);

		
			
			WSClient.setData("{var_resvID}", WSClient.getDBRow(WSClient.getQuery("HTNG2008BLocationNotification", "QS_03")).get("INVALID_RESVID"));
			
			WSClient.setData("{var_locationText}", "Sample Notification");

						
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
						/*************
						 Main Operation : Create a Location Notification
						 ******************/
						String CreateLocationNotificationnReq = WSClient
								.createSOAPMessage("HTNG2008BLocationNotification", "DS_01");
						String CreateLocationNotificationnRes = WSClient
								.processSOAPMessage(CreateLocationNotificationnReq);
						
						/*************
						  Validations
						 ******************/

						if (WSAssert.assertIfElementExists(CreateLocationNotificationnRes,
								"LocationNotificationResponse_Result_resultStatusFlag", false)) {

							if (WSAssert.assertIfElementValueEquals(CreateLocationNotificationnRes,
									"LocationNotificationResponse_Result_resultStatusFlag", "FAIL", false)) {

								
								WSClient.writeToReport(LogStatus.INFO, "<b>Error Message : "+WSClient.getElementValue(CreateLocationNotificationnRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
								


					}
				} 

		} }catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);  
		} 
	}


	
	
	@Test(groups = { "fullRegression", "LocationNotification","HTNG2008B","HTNG" })
	public void locationNotification_2008_3245() {
		try {
			String testName = "locationNotification_2008_3245";
			WSClient.startTest(testName, "Verify that the error-message is present in the response when reservation id is not passed in the request", "fullRegression");
			if(OperaPropConfig.getPropertyConfigResults(new String[] {"RateCode","RoomType","SourceCode","MarketCode"})){
			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue,interfaceName);
			
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_ext_resort}", resortExtValue);

		
			
			WSClient.setData("{var_resvID}", "");
			
			WSClient.setData("{var_locationText}", "Sample Notification");

						
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(), HTNGLib.getInterfaceFromAddress());
						/*************
						 Main Operation : Create a Location Notification
						 ******************/
						String CreateLocationNotificationnReq = WSClient
								.createSOAPMessage("HTNG2008BLocationNotification", "DS_01");
						String CreateLocationNotificationnRes = WSClient
								.processSOAPMessage(CreateLocationNotificationnReq);
						
						/*************
						  Validations
						 ******************/

						if (WSAssert.assertIfElementExists(CreateLocationNotificationnRes,
								"LocationNotificationResponse_Result_resultStatusFlag", false)) {

							if (WSAssert.assertIfElementValueEquals(CreateLocationNotificationnRes,
									"LocationNotificationResponse_Result_resultStatusFlag", "FAIL", false)) {

								
								WSClient.writeToReport(LogStatus.INFO, "<b>Error Message : "+WSClient.getElementValue(CreateLocationNotificationnRes, "Result_Text_TextElement", XMLType.RESPONSE)+"</b>");
								


					}
				} 

		} }catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);  
		} 
	}
	
	
	
	
}
