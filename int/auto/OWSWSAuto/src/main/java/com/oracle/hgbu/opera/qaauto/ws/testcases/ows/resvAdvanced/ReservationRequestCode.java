package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;

import java.util.LinkedHashMap;

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

public class ReservationRequestCode extends WSSetUp{
	

		//Creating Profile 
		public String createProfile(String ds) 
		{
				String profileID = "";
				try 
				{
					String resortOperaValue = OPERALib.getResort();
	                String chain=OPERALib.getChain();
	                
	                WSClient.setData("{var_resort}", resortOperaValue);
	                WSClient.setData("{var_chain}",chain);
	
	                
	                String uname = OPERALib.getUserName();
	                OPERALib.setOperaHeader(uname);
	                
	                //WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a Profile"+"</b>");
	                
	                profileID = CreateProfile.createProfile(ds);
	                WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID : " + profileID + "</b>");
	                
				}
				catch(Exception e) 
				{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);  
				}
				return profileID;
		}
	
	
		//Creating Reservation
		public String createReservation(String ds)
		{
		String reservationId="";
		try
		{
			String resortOperaValue = OPERALib.getResort();
		    String chain=OPERALib.getChain();
			
		    WSClient.setData("{var_resort}", resortOperaValue);
		    WSClient.setData("{var_chain}",chain);
		    
		    /*************
			 * Fetch Details for rate
			 * code, payment method
			 ******************/
			WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
			WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
			WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
			WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
			WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
			WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
		
		    String uname = OPERALib.getUserName();
		    OPERALib.setOperaHeader(uname);
		    
		    //WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a Reservation"+"</b>");
		    
		    reservationId = CreateReservation.createReservation(ds).get("reservationId");
		    WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID : " + reservationId + "</b>");
		    
		}
		catch(Exception e) 
		{
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);  
		}
		return reservationId;
		}
		
		
		public void setOWSHeader() {
			try {
			String resort = OPERALib.getResort();
			String uname = OPERALib.getUserName();
			String channel = OWSLib.getChannel();
            String pwd = OPERALib.getPassword();
            String channelType = OWSLib.getChannelType(channel);
            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
			}catch(Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "OWS Header not set.");
			}
		}
//		@Test(groups={"minimumRegression","ResvAdvanced","ReservationRequestCode","OWS"})
//		public void reservationRequestCode_60015() {
//		try {
//			String testname = "reservationRequestCode_60015";
//			WSClient.startTest(testname, "Verify that the request codes associated to a reservation and its corresponding profile are fetched by passing valid values in mandatory fields with channel where channel and carrier name are different", "minimumRegression");
//			
//			if (OperaPropConfig.getPropertyConfigResults(
//                    new String[] { "PreferenceGroup", "PreferenceCode", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
//			
//			String UniqueId,resvID;
//			
//			// Creating a profile
//			if(!(UniqueId = createProfile("DS_01")).equals("error"))
//			{
//					WSClient.setData("{var_profileID}", UniqueId);
//					WSClient.setData("{var_profileId}", UniqueId);
//					
//					// Setting variables for creating preference
//					WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
//					WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
//					WSClient.setData("{var_prefType2}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_02"));
//					WSClient.setData("{var_prefValue2}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_02"));
//					WSClient.setData("{var_global}", "false");
//					WSClient.setData("{var_global2}", "false");
//					
//					// Creating a Preference for above created profile
//					String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_02");
//					String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);
//					
//					if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true) && !WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Warnings", true)) {
//						
//						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Associated Preference to Profile</b>");
//						
//	                        if (!(resvID=createReservation("DS_01")).equals("error")) {
//	                        	
//	                        	WSClient.setData("{var_resvID}", resvID);
//	                        	WSClient.setData("{var_resvId}", resvID);
//	                        	WSClient.setData("{var_reservation_id}", resvID);
//
//	                        	
//	                        	
//	                        	String resort = OPERALib.getResort();
//								String channel = OWSLib.getChannel(3);
//								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
//								
//							
//								String uname = OPERALib.getUserName();
//								 String pwd = OPERALib.getPassword();
//					            String channelType = OWSLib.getChannelType(channel);
//					            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
//					            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
//	                        	
//	                        	// Creating request and processing response for OWS ReservationRequestCode Operation
//	                        	String ReservationRequestCodeReq = WSClient.createSOAPMessage("OWSReservationRequestCode", "DS_01");
//	                            String ReservationRequestCodeRes = WSClient.processSOAPMessage(ReservationRequestCodeReq);
//	                        	
//	                            // Validating response of OWS ReservationRequestCode Operation
//	                            if(WSAssert.assertIfElementExists(ReservationRequestCodeRes,
//	    								"ReservationRequestCodeResponse_Result_resultStatusFlag", true)) {
//	    							if (WSAssert.assertIfElementValueEquals(ReservationRequestCodeRes,
//	    									"ReservationRequestCodeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
//	
//	    									// Database Validation
//	    									LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
//	    									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String,String>();
//	    									LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
//	    									xPath.put("ReservationRequestCodeResponse_ReservationRequestCodes_RequestType", "ReservationRequestCodeResponse_ReservationRequestCodes");
//	    									xPath.put("ReservationRequestCodeResponse_ReservationRequestCodes_RequestCode", "ReservationRequestCodeResponse_ReservationRequestCodes");
//	    									xPath.put("ReservationRequestCodes_RequestDescription_Text", "ReservationRequestCodeResponse_ReservationRequestCodes");
//	    									actualValues = WSClient.getSingleNodeList(ReservationRequestCodeRes, xPath, false, XMLType.RESPONSE);
//	    									expectedValues=WSClient.getDBRow(WSClient.getQuery("QS_01"));
//	    									WSAssert.assertEquals(actualValues, expectedValues,false);
//	    							}else {	
//	    								if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "Result_Text_TextElement", true)) {
//	    									String message=WSAssert.getElementValue(ReservationRequestCodeRes, "Result_Text_TextElement", XMLType.RESPONSE);
//	    									WSClient.writeToReport(LogStatus.INFO, "The error displayed in the InsertUpdateName response is :"+ message);
//	    									if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_OperaErrorCode", true)) {
//	    										String operaErrorCode=WSAssert.getElementValue(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
//	    										WSClient.writeToReport(LogStatus.INFO, "<b>Opera Error Code :"+ operaErrorCode+"</b>");
//	    										
//	    									}
//	    								}
//	    								if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_GDSError", true)) {
//	    									String message=WSAssert.getElementValue(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_GDSError", XMLType.RESPONSE);
//	    									WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the DeletePreference response is :"+ message);
//	    								}
//	    							}
//	    						}else {
//	    							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
//	    						}          
//	                           
//	                            // Canceling Reservation
//	                            //WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
//	                            CancelReservation.cancelReservation("DS_02");
//
//	                        }else {
//	                        	WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreateReservation-----Blocked");
//	                        }
//						
//					}else {
//						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreatePreference-----Blocked");
//					}
//			  }
//		  }else {
//			  WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
//		  }
//		}catch (Exception e) {
//			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
//		}
//	}
		// Sanity Test Case :1 
		@Test(groups={"sanity","ResvAdvanced","ReservationRequestCode","OWS"})
		public void reservationRequestCode_39960() {
		try {
			String testname = "reservationRequestCode_39960";
			WSClient.startTest(testname, "Verify that the request codes associated to a reservation and its corresponding profile are fetched by passing valid values in mandatory fields", "sanity");
			
			if (OperaPropConfig.getPropertyConfigResults(
                    new String[] { "PreferenceGroup", "PreferenceCode", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
			
			String UniqueId,resvID;
			
			// Creating a profile
			if(!(UniqueId = createProfile("DS_01")).equals("error"))
			{
					WSClient.setData("{var_profileID}", UniqueId);
					WSClient.setData("{var_profileId}", UniqueId);
					
					// Setting variables for creating preference
					WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
					WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
					WSClient.setData("{var_prefType2}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_02"));
					WSClient.setData("{var_prefValue2}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_02"));
					WSClient.setData("{var_global}", "false");
					WSClient.setData("{var_global2}", "false");
					
					// Creating a Preference for above created profile
					String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_02");
					String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);
					
					if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true) && !WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Warnings", true)) {
						
						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Associated Preference to Profile</b>");
						
	                        if (!(resvID=createReservation("DS_01")).equals("error")) {
	                        	
	                        	WSClient.setData("{var_resvID}", resvID);
	                        	WSClient.setData("{var_resvId}", resvID);
	                        	WSClient.setData("{var_reservation_id}", resvID);

	                        	setOWSHeader();
	                        	
	                        	String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
	                        	
	                        	// Creating request and processing response for OWS ReservationRequestCode Operation
	                        	String ReservationRequestCodeReq = WSClient.createSOAPMessage("OWSReservationRequestCode", "DS_01");
	                            String ReservationRequestCodeRes = WSClient.processSOAPMessage(ReservationRequestCodeReq);
	                        	
	                            // Validating response of OWS ReservationRequestCode Operation
	                            if(WSAssert.assertIfElementExists(ReservationRequestCodeRes,
	    								"ReservationRequestCodeResponse_Result_resultStatusFlag", true)) {
	    							if (WSAssert.assertIfElementValueEquals(ReservationRequestCodeRes,
	    									"ReservationRequestCodeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
	
	    									// Database Validation
	    									LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
	    									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String,String>();
	    									LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
	    									xPath.put("ReservationRequestCodeResponse_ReservationRequestCodes_RequestType", "ReservationRequestCodeResponse_ReservationRequestCodes");
	    									xPath.put("ReservationRequestCodeResponse_ReservationRequestCodes_RequestCode", "ReservationRequestCodeResponse_ReservationRequestCodes");
	    									xPath.put("ReservationRequestCodes_RequestDescription_Text", "ReservationRequestCodeResponse_ReservationRequestCodes");
	    									actualValues = WSClient.getSingleNodeList(ReservationRequestCodeRes, xPath, false, XMLType.RESPONSE);
	    									expectedValues=WSClient.getDBRow(WSClient.getQuery("QS_01"));
	    									WSAssert.assertEquals(actualValues, expectedValues,false);
	    							}else {	
	    								if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "Result_Text_TextElement", true)) {
	    									String message=WSAssert.getElementValue(ReservationRequestCodeRes, "Result_Text_TextElement", XMLType.RESPONSE);
	    									WSClient.writeToReport(LogStatus.INFO, "The error displayed in the InsertUpdateName response is :"+ message);
	    									if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_OperaErrorCode", true)) {
	    										String operaErrorCode=WSAssert.getElementValue(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	    										WSClient.writeToReport(LogStatus.INFO, "<b>Opera Error Code :"+ operaErrorCode+"</b>");
	    										
	    									}
	    								}
	    								if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_GDSError", true)) {
	    									String message=WSAssert.getElementValue(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_GDSError", XMLType.RESPONSE);
	    									WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the DeletePreference response is :"+ message);
	    								}
	    							}
	    						}else {
	    							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
	    						}          
	                           
	                            // Canceling Reservation
	                            //WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
	                            CancelReservation.cancelReservation("DS_02");

	                        }else {
	                        	WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreateReservation-----Blocked");
	                        }
						
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreatePreference-----Blocked");
					}
			  }
		  }else {
			  WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
		  }
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
		
		// Minimum Regression Test Case :1 
				@Test(groups={"minimumRegression","ResvAdvanced","ReservationRequestCode","OWS"})
		public void reservationRequestCode_40308() {
		try {
			String testname = "reservationRequestCode_40308";
			WSClient.startTest(testname, "Verify that request codes associated to a reservation  and its corresponding profile are fetched by passing invalid source", "minimumRegression");
			
			if (OperaPropConfig.getPropertyConfigResults(
                    new String[] { "PreferenceGroup", "PreferenceCode", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
			
				String UniqueId,resvID;
				
				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					
					WSClient.setData("{var_profileID}", UniqueId);
					WSClient.setData("{var_profileId}", UniqueId);
					
					// Setting variables for creating preference
					WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
					WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
					WSClient.setData("{var_prefType2}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_02"));
					WSClient.setData("{var_prefValue2}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_02"));
					WSClient.setData("{var_global}", "false");
					WSClient.setData("{var_global2}", "false");
					
					// Creating a Preference for above created profile
					String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_02");
					String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);
					
					if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true) && !WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Warnings", true)) {

						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Associated Preference to Profile</b>");
                        if (!(resvID=createReservation("DS_01")).equals("error")) {
                        	
                        	    WSClient.setData("{var_resvID}", resvID);
	                        	WSClient.setData("{var_resvId}", resvID);
	                        	WSClient.setData("{var_reservation_id}", resvID);
	                        	
	                        	setOWSHeader();
	                        	
	                        	String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
	                        	
	                        	// Creating request and processing response for OWS ReservationRequestCode Operation
	                        	String ReservationRequestCodeReq = WSClient.createSOAPMessage("OWSReservationRequestCode", "DS_02");
	                            String ReservationRequestCodeRes = WSClient.processSOAPMessage(ReservationRequestCodeReq);
	                        	
	                         // Validating response of OWS ReservationRequestCode Operation
	                            if(WSAssert.assertIfElementExists(ReservationRequestCodeRes,
	    								"ReservationRequestCodeResponse_Result_resultStatusFlag", true)) {
	    							if (WSAssert.assertIfElementValueEquals(ReservationRequestCodeRes,
	    									"ReservationRequestCodeResponse_Result_resultStatusFlag", "SUCCESS", false)) {
	
	    								// Database Validation
	    									LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
	    									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String,String>();
	    									LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
	    									xPath.put("ReservationRequestCodeResponse_ReservationRequestCodes_RequestType", "ReservationRequestCodeResponse_ReservationRequestCodes");
	    									xPath.put("ReservationRequestCodeResponse_ReservationRequestCodes_RequestCode", "ReservationRequestCodeResponse_ReservationRequestCodes");
	    									xPath.put("ReservationRequestCodes_RequestDescription_Text", "ReservationRequestCodeResponse_ReservationRequestCodes");
	    									actualValues = WSClient.getSingleNodeList(ReservationRequestCodeRes, xPath, false, XMLType.RESPONSE);
	    									expectedValues=WSClient.getDBRow(WSClient.getQuery("QS_01"));
	    									WSAssert.assertEquals(actualValues, expectedValues,false);
	    							}else {	
	    								if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "Result_Text_TextElement", true)) {
	    									String message=WSAssert.getElementValue(ReservationRequestCodeRes, "Result_Text_TextElement", XMLType.RESPONSE);
	    									WSClient.writeToReport(LogStatus.INFO, "The error displayed in the InsertUpdateName response is :"+ message);
	    									if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_OperaErrorCode", true)) {
	    										String operaErrorCode=WSAssert.getElementValue(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	    										WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
	    										
	    									}
	    								}
	    								if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_GDSError", true)) {
	    									String message=WSAssert.getElementValue(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_GDSError", XMLType.RESPONSE);
	    									WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the DeletePreference response is :"+ message);
	    								}
	    							}
	                            }else {
	    							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
	    						}          
	                           
	                            // Canceling Reservation
	                            //WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
	                            CancelReservation.cancelReservation("DS_02");

	                        }else {
	                        	WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreateReservation-----Blocked");
	                        }
						
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreatePreference-----Blocked");
					}
			  }
		  }else {
			  WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
		  }
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
		
		// Minimum Regression Test Case : 2
		@Test(groups={"minimumRegression","ResvAdvanced","ReservationRequestCode","OWS"})
		public void reservationRequestCode_40312() {
		try {
			String testname = "reservationRequestCode_40312";
			WSClient.startTest(testname, "Verify that error message is obtained by not passing any reservation id", "minimumRegression");
			
			if (OperaPropConfig.getPropertyConfigResults(
                    new String[] { "PreferenceGroup", "PreferenceCode", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
			
				String UniqueId,resvID;
				
				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					
					WSClient.setData("{var_profileID}", UniqueId);
					WSClient.setData("{var_profileId}", UniqueId);
					
					// Setting variables for creating preference
					WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
					WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
					WSClient.setData("{var_prefType2}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_02"));
					WSClient.setData("{var_prefValue2}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_02"));
					WSClient.setData("{var_global}", "false");
					WSClient.setData("{var_global2}", "false");
					
					// Creating a Preference for above created profile
					String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_02");
					String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);
					
					if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true) && !WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Warnings", true)) {

						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Associated Preference to Profile</b>");
                        if (!(resvID=createReservation("DS_01")).equals("error")) {
                        	
                        		WSClient.setData("{var_resvID}", resvID);
	                        	WSClient.setData("{var_resvId}", resvID);
	                        	WSClient.setData("{var_reservation_id}", resvID);
	                        	
	                        	setOWSHeader();
	                        	
	                        	String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
	                        	
	                        	// Creating request and processing response for OWS ReservationRequestCode Operation
	                        	String ReservationRequestCodeReq = WSClient.createSOAPMessage("OWSReservationRequestCode", "DS_03");
	                            String ReservationRequestCodeRes = WSClient.processSOAPMessage(ReservationRequestCodeReq);
	                        	
	                         // Validating response of OWS ReservationRequestCode Operation
	                            if(WSAssert.assertIfElementExists(ReservationRequestCodeRes,
	    								"ReservationRequestCodeResponse_Result_resultStatusFlag", true)) {
	    							if (WSAssert.assertIfElementValueEquals(ReservationRequestCodeRes,
	    									"ReservationRequestCodeResponse_Result_resultStatusFlag", "FAIL", false)) {
	
	    								String errorStr = "";
										if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "Result_Text_TextElement", true)) 
											errorStr=WSAssert.getElementValue(ReservationRequestCodeRes, "Result_Text_TextElement", XMLType.RESPONSE);
										if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_GDSError", true)) 
											errorStr=WSAssert.getElementValue(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_GDSError", XMLType.RESPONSE);
										
										if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "Result_Text_TextElement", false)) {
	    									if(WSAssert.assertIfElementValueEquals(ReservationRequestCodeRes, "Result_Text_TextElement", "Opera Reservation Id or Key Track 2 is Missing in Request Message", true)) {
	    										WSAssert.assertIfElementValueEquals(ReservationRequestCodeRes, "Result_Text_TextElement", "Opera Reservation Id or Key Track 2 is Missing in Request Message", false);
	    									}else if(WSAssert.assertIfElementContains(ReservationRequestCodeRes, "Result_Text_TextElement", "Reservation Cannot Be Empty", false)) {
	    										
	    									}
	    								}else {
											if(errorStr!="") {
											WSClient.writeToReport(LogStatus.INFO, "Error displayed : " + errorStr);
											WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
											}else {
												WSClient.writeToReport(LogStatus.FAIL, "Test Failed---------------Unexpected response--------------");
											}
										}
	    							}else {	
	    								WSClient.writeToReport(LogStatus.INFO, "Test Failed");
	    							}
	                            }else {
	    							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
	    						}          
	                           
	                            // Canceling Reservation
	                            //WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
	                            CancelReservation.cancelReservation("DS_02");

	                        }else {
	                        	WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreateReservation-----Blocked");
	                        }
						
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreatePreference-----Blocked");
					}
			  }
		  }else {
			  WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
		  }
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
		
		// Minimum Regression Test Case : 3
		@Test(groups={"minimumRegression","ResvAdvanced","ReservationRequestCode","OWS"})
		public void reservationRequestCode_40321() {
		try {
			String testname = "reservationRequestCode_40321";
			WSClient.startTest(testname, "Verify that error message is obtained by not passing hotel code", "minimumRegression");
			
			if (OperaPropConfig.getPropertyConfigResults(
                    new String[] { "PreferenceGroup", "PreferenceCode", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
			
				String UniqueId,resvID;
				
				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					
					WSClient.setData("{var_profileID}", UniqueId);
					WSClient.setData("{var_profileId}", UniqueId);
					
					// Setting variables for creating preference
					WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
					WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
					WSClient.setData("{var_prefType2}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_02"));
					WSClient.setData("{var_prefValue2}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_02"));
					WSClient.setData("{var_global}", "false");
					WSClient.setData("{var_global2}", "false");
					
					// Creating a Preference for above created profile
					String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_02");
					String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);
					
					if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true) && !WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Warnings", true)) {

						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Associated Preference to Profile</b>");
                        if (!(resvID=createReservation("DS_01")).equals("error")) {
                        	
	                        	WSClient.setData("{var_resvID}", resvID);
	                        	WSClient.setData("{var_resvId}", resvID);
	                        	WSClient.setData("{var_reservation_id}", resvID);
	                        	
	                        	setOWSHeader();
	                        	
	                        	String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
	                        	
	                        	// Creating request and processing response for OWS ReservationRequestCode Operation
	                        	String ReservationRequestCodeReq = WSClient.createSOAPMessage("OWSReservationRequestCode", "DS_04");
	                            String ReservationRequestCodeRes = WSClient.processSOAPMessage(ReservationRequestCodeReq);
	                        	
	                         // Validating response of OWS ReservationRequestCode Operation
	                            if(WSAssert.assertIfElementExists(ReservationRequestCodeRes,
	    								"ReservationRequestCodeResponse_Result_resultStatusFlag", true)) {
	    							if (WSAssert.assertIfElementValueEquals(ReservationRequestCodeRes,
	    									"ReservationRequestCodeResponse_Result_resultStatusFlag", "FAIL", false)) {
	
	    								String errorStr = "";
										if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "Result_Text_TextElement", true)) 
											errorStr=WSAssert.getElementValue(ReservationRequestCodeRes, "Result_Text_TextElement", XMLType.RESPONSE);
										if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_GDSError", true)) 
											errorStr=WSAssert.getElementValue(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_GDSError", XMLType.RESPONSE);
										
										if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "Result_Text_TextElement", false)) {
											if(WSAssert.assertIfElementValueEquals(ReservationRequestCodeRes, "Result_Text_TextElement", "Opera Resort is Missing in Request Message", true)) {
												WSAssert.assertIfElementValueEquals(ReservationRequestCodeRes, "Result_Text_TextElement", "Opera Resort is Missing in Request Message", false);
											}else if(WSAssert.assertIfElementContains(ReservationRequestCodeRes, "Result_Text_TextElement", "Hotel Code is Required", false)) {
												
											}
										}
										
										if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "Result_Text_TextElement", true) && WSAssert.assertIfElementValueEquals(ReservationRequestCodeRes, "Result_Text_TextElement", "Opera Resort is Missing in Request Message", false)) {
											
										}else if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_GDSError", true) && WSAssert.assertIfElementValueEquals(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_GDSError", "Hotel Code is Required", false)) {
											
										}else {
											WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
											if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "Result_Text_TextElement", true)) {
												String message=WSAssert.getElementValue(ReservationRequestCodeRes, "Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "The error displayed in the DeletePreference response is :"+ message);
												if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_OperaErrorCode", true)) {
													String operaErrorCode=WSAssert.getElementValue(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_OperaErrorCode", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
												}
											}
											if(WSAssert.assertIfElementExists(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_GDSError", true)) {
												String message=WSAssert.getElementValue(ReservationRequestCodeRes, "ReservationRequestCodeResponse_Result_GDSError", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the DeletePreference response is :"+ message);
											}
										}
	    							}else {	
	    								WSClient.writeToReport(LogStatus.INFO, "Test Failed");
	    							}
	                            }else {
	    							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
	    						}          
	                           
	                            // Canceling Reservation
	                            //WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
	                            CancelReservation.cancelReservation("DS_02");

	                        }else {
	                        	WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreateReservation-----Blocked");
	                        }
						
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreatePreference-----Blocked");
					}
			  }
		  }else {
			  WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
		  }
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
		
		// Minimum Regression Test Case : 4
		@Test(groups={"minimumRegression","ResvAdvanced","ReservationRequestCode","OWS"})
		public void reservationRequestCode_41222() {
		try {
			String testname = "reservationRequestCode_41222";
			WSClient.startTest(testname, "Verify that error message is obtained by passing text value for reservation id", "minimumRegression");
			
			if (OperaPropConfig.getPropertyConfigResults(
                    new String[] { "PreferenceGroup", "PreferenceCode", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
			
				String UniqueId,resvID;
				
				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					
					WSClient.setData("{var_profileID}", UniqueId);
					WSClient.setData("{var_profileId}", UniqueId);
					
					// Setting variables for creating preference
					WSClient.setData("{var_prefType}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_01"));
					WSClient.setData("{var_prefValue}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_01"));
					WSClient.setData("{var_prefType2}",OperaPropConfig.getDataSetForCode("PreferenceGroup", "DS_02"));
					WSClient.setData("{var_prefValue2}",OperaPropConfig.getDataSetForCode("PreferenceCode", "DS_02"));
					WSClient.setData("{var_global}", "false");
					WSClient.setData("{var_global2}", "false");
					
					// Creating a Preference for above created profile
					String createPreferenceReq = WSClient.createSOAPMessage("CreatePreference", "DS_02");
					String createPreferenceResponseXML = WSClient.processSOAPMessage(createPreferenceReq);
					
					if (WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Success", true) && !WSAssert.assertIfElementExists(createPreferenceResponseXML, "CreatePreferenceRS_Warnings", true)) {

						WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Associated Preference to Profile</b>");
                        if (!(resvID=createReservation("DS_01")).equals("error")) {
                        	
	                        	WSClient.setData("{var_resvID}", "Oracle");
	                        	WSClient.setData("{var_resvId}", resvID);
	                        	WSClient.setData("{var_reservation_id}", resvID);
	                        	
	                        	setOWSHeader();
	                        	
	                        	String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
	                        	
	                        	// Creating request and processing response for OWS ReservationRequestCode Operation
	                        	String ReservationRequestCodeReq = WSClient.createSOAPMessage("OWSReservationRequestCode", "DS_01");
	                            String ReservationRequestCodeRes = WSClient.processSOAPMessage(ReservationRequestCodeReq);
	                        	
	                         // Validating response of OWS ReservationRequestCode Operation
	                            if(WSAssert.assertIfElementExists(ReservationRequestCodeRes,
	    								"ReservationRequestCodeResponse_Result_resultStatusFlag", true)) {
	    							if (WSAssert.assertIfElementValueEquals(ReservationRequestCodeRes,
	    									"ReservationRequestCodeResponse_Result_resultStatusFlag", "FAIL", false)) {
	
	    								
	    							}else {	
	    								WSClient.writeToReport(LogStatus.INFO, "Test Failed");
	    							}
	                            }else {
	    							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
	    						}          
	                           
	                            // Canceling Reservation
	                            //WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
	                            CancelReservation.cancelReservation("DS_02");

	                        }else {
	                        	WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreateReservation-----Blocked");
	                        }
						
					}else {
						WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreatePreference-----Blocked");
					}
			  }
		  }else {
			  WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
		  }
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

}
