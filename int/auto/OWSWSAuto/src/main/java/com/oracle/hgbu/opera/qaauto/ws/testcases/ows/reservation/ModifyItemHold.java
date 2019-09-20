package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ReleaseItemInventoryHold;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class ModifyItemHold extends WSSetUp {
	
	String profileID="";	
	@Test(groups = { "sanity", "ModifyItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify that the hold item details are modified when data is passed in the request
	 * 
	 */
	
	public void modifyItemHold_39900() {
		try {
					
			
			String testName = "modifyItemHold_39900";
			WSClient.startTest(testName, "Verify that the hold item details are modified successfully", "sanity");		
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "ItemCode"})) {
						String resortOperaValue=OPERALib.getResort();
						 String resort = OPERALib.getResort();
						 String channel = OWSLib.getChannel();
							String owsResort=OWSLib.getChannelResort(resort, channel);
							WSClient.setData("{var_owsResort}",owsResort);
							
						 String chain=OPERALib.getChain();
							WSClient.setData("{var_chain}", chain);
						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
								   OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
								 OPERALib.setOperaHeader(OPERALib.getUserName());
								 
								 
								// Prerequisite 1: Create Profile
								 if (profileID.equals(""))
										profileID = CreateProfile.createProfile("DS_01");
									if (!profileID.equals("error")) {
										WSClient.setData("{var_profileId}", profileID);
										WSClient.writeToReport(LogStatus.INFO,"<b>Profile ID:"+profileID+"</b>");

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

									WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+resvID+"</b>");

									String Date=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
									WSClient.setData("{var_startDate}", Date);
									WSClient.setData("{var_endDate}", Date);
									WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
								    WSClient.setData("{var_count}","0");
								    //String heldById = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
									//WSClient.setData("{var_heldById}", heldById);
									WSClient.setData("{var_resort}",resortOperaValue);
									WSClient.setData("{var_quantity}","1");
								    
								// Prerequisite 3 : Create Item Hold 
									
								    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_01");
									String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
									  
							  
							if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true))
							{
								if(WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_HeldById_ID", false))
								{
									//Getting itemHoldId based on the heldById 
									String QS_01 = WSClient.getQuery("OWSModifyItemHold","QS_05");
									String itemHoldId = WSClient.getDBRow(QS_01).get("ITEM_HOLD_ID");
									WSClient.writeToReport(LogStatus.INFO,"<b>ItemHold ID:"+itemHoldId+"</b>");
									WSClient.setData("{var_itemHoldId}", itemHoldId);
						
						//Prerequisite	: Modify Item Hold
								
						String modifyItemHoldReq = WSClient.createSOAPMessage("OWSModifyItemHold", "DS_01");
				        String modifyItemHoldRes= WSClient.processSOAPMessage(modifyItemHoldReq);

				        if(WSAssert.assertIfElementExists(modifyItemHoldRes, "Result_Text_TextElement", true)) {
							String message=WSAssert.getElementValue(modifyItemHoldRes, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The error displayed in the response is :"+ message+"</b>");
							
						}
					    if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", true)) {
								String operaErrorCode=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ operaErrorCode+"</b>");
							}
					    if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_GDSError", true)) {
							String GDSErrorCode=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ GDSErrorCode+"</b>");
						}
						
						
						// Validation for OWS Operation
						if(WSAssert.assertIfElementExists(modifyItemHoldRes,"ModifyItemHoldResponse_Result_resultStatusFlag", true)) {
							if(WSAssert.assertIfElementValueEquals(modifyItemHoldRes,"ModifyItemHoldResponse_Result_resultStatusFlag", "SUCCESS", false))
						
		                {
		                	/**
		                	 *  Validating the database details 
		                	 */
			                
		                	LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
							LinkedHashMap<String,String> actualValues = new LinkedHashMap<String,String>();
							
							
							expectedValues.put("ITEM_HOLD_ID",WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_ItemHoldID", XMLType.REQUEST));
							expectedValues.put("BEGIN_DATE", WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_StayDateRange_StartDate", XMLType.REQUEST));
							expectedValues.put("END_DATE", WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_StayDateRange_EndDate", XMLType.REQUEST));
							
						
							
							String query = WSClient.getQuery("QS_01");
         					actualValues = WSClient.getDBRow(query);
         					
							WSAssert.assertEquals(expectedValues,actualValues,false);
							
							
		            
					   }  
							else
				        	{
				        	 WSClient.writeToReport(LogStatus.FAIL, "Unable to modify Item Hold ");
				        	}
						
				} 
								else {
									WSClient.writeToReport(LogStatus.FAIL, "Schema is incorrect");
								}}}
								else{
									WSClient.writeToReport(LogStatus.ERROR, "Unable to create Item Hold ");
								}
									}
								}}}
		        
				catch(Exception e)
				{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
				} 	
		
		finally
		  {
			// Cancel Reservation
						try
						{
					        CancelReservation.cancelReservation("DS_02");
					        ReleaseItemInventoryHold.releaseItemInventoryHold("DS_01");
						}
			            catch (Exception e) 
						{
			                // TODO Auto-generated catch block
			                e.printStackTrace();
			            }
		  }
			
	}

 @Test(groups = { "minimumRegression", "ModifyItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify that an error message is obtained when no ItemHoldID is passed in the request.
	 * 
	 */
	public void modifyItemHold_40257() {
		try {
			
			
			String testName = "modifyItemHold_40257";
			WSClient.startTest(testName, "Verify that an error message is obtained when no ItemHold ID is passed in the request.", "minimumRegression");		
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "ItemCode"})) {
						String resortOperaValue=OPERALib.getResort();
						 String resort = OPERALib.getResort();
						 String channel = OWSLib.getChannel();
							String owsResort=OWSLib.getChannelResort(resort, channel);
							WSClient.setData("{var_owsResort}",owsResort);
						 String chain=OPERALib.getChain();
							WSClient.setData("{var_chain}", chain);
						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
								   OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
								 OPERALib.setOperaHeader(OPERALib.getUserName());
								 
								 
								// Prerequisite 1: Create Profile
								 if (profileID.equals(""))
										profileID = CreateProfile.createProfile("DS_01");
									if (!profileID.equals("error")) {
										WSClient.setData("{var_profileId}", profileID);

										WSClient.writeToReport(LogStatus.INFO,"<b>Profile ID:"+profileID+"</b>");

										

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

									WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+resvID+"</b>");

									String Date=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
									WSClient.setData("{var_startDate}", Date);
									WSClient.setData("{var_endDate}", Date);
									WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
								    WSClient.setData("{var_count}","0");
								    //String heldById = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
									//WSClient.setData("{var_heldById}", heldById);
						
								    WSClient.setData("{var_resort}",resortOperaValue);
									WSClient.setData("{var_quantity}","1");
								    
								// Prerequisite 3 : Create Item Hold 
									
								    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_01");
									String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
									  
							  
							if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true))
							{
								
						//Prerequisite	: Modify Item Hold
								
						String modifyItemHoldReq = WSClient.createSOAPMessage("OWSModifyItemHold", "DS_02");
				        String modifyItemHoldRes= WSClient.processSOAPMessage(modifyItemHoldReq);

				        if(WSAssert.assertIfElementExists(modifyItemHoldRes, "Result_Text_TextElement",true))
						{
			
							/**** Verifying that the error message is populated on the response ********/
				
						String message=WSAssert.getElementValue(modifyItemHoldRes,
							       "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the  response is :"+ message+"</b>");
						}
				        
						if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode",true)) 
						{
							
							/**** Verifying whether the error Message is populated on the response ****/
							
							String message=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
						}
						if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_GDSError", true)) {
							String GDSErrorCode=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ GDSErrorCode+"</b>");
						}

		                if(WSAssert.assertIfElementExists(modifyItemHoldRes,
							      "ModifyItemHoldResponse_Result_resultStatusFlag", false))
		                {
		                	
		                	WSAssert.assertIfElementValueEquals(modifyItemHoldRes,
								      "ModifyItemHoldResponse_Result_resultStatusFlag", "FAIL", false);
		            
					   }  
				
			
		} 
							else{
								WSClient.writeToReport(LogStatus.ERROR, "Unable to create Item Hold ");
							}}}}}
				catch(Exception e)
				{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
				} 	
		finally
		  {
			// Cancel Reservation
						try
						{
					        CancelReservation.cancelReservation("DS_02");
					        
						}
			            catch (Exception e) 
						{
			                // TODO Auto-generated catch block
			                e.printStackTrace();
			            }
		  }
	}

	@Test(groups = { "minimumRegression", "ModifyItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify that the held item details are modified when all the required fields are passed in the request
	 * 
	 */
	
	public void modifyItemHold_40260() {
	try {
					
			
			String testName = "modifyItemHold_40260";
			WSClient.startTest(testName, "Verify that the held item details are modified based on Quantity and Date Range", "minimumRegression");		
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "ItemCode"})) {
						String resortOperaValue=OPERALib.getResort();
						 String resort = OPERALib.getResort();
						 String channel = OWSLib.getChannel();
							String owsResort=OWSLib.getChannelResort(resort, channel);
							WSClient.setData("{var_owsResort}",owsResort);
						 String chain=OPERALib.getChain();
							WSClient.setData("{var_chain}", chain);
						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
								   OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
								 OPERALib.setOperaHeader(OPERALib.getUserName());
								 
								 
								// Prerequisite 1: Create Profile
								 if (profileID.equals(""))
										profileID = CreateProfile.createProfile("DS_01");
									if (!profileID.equals("error")) {
										WSClient.setData("{var_profileId}", profileID);

										WSClient.writeToReport(LogStatus.INFO,"<b>Profile ID:"+profileID+"</b>");


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

									WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+resvID+"</b>");

									String Date=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
									WSClient.setData("{var_startDate}", Date);
									WSClient.setData("{var_endDate}", Date);
									WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
								    WSClient.setData("{var_count}","1");
								    //String heldById = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
									//WSClient.setData("{var_heldById}", heldById);
									WSClient.setData("{var_resort}",resortOperaValue);
									WSClient.setData("{var_quantity}","1");
								    
								// Prerequisite 3 : Create Item Hold 
									
								    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_01");
									String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
									  
							  
							if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true))
							{
								if(WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_HeldById_ID", false))
								{
									//Getting itemHoldId based on the heldById 
									String QS_01 = WSClient.getQuery("OWSModifyItemHold","QS_05");
									String itemHoldId = WSClient.getDBRow(QS_01).get("ITEM_HOLD_ID");
									WSClient.writeToReport(LogStatus.INFO,"<b>ItemHold ID:"+itemHoldId+"</b>");
									WSClient.setData("{var_itemHoldId}", itemHoldId);
						
						//Prerequisite	: Modify Item Hold
								
						String modifyItemHoldReq = WSClient.createSOAPMessage("OWSModifyItemHold", "DS_03");
				        String modifyItemHoldRes= WSClient.processSOAPMessage(modifyItemHoldReq);

				        if(WSAssert.assertIfElementExists(modifyItemHoldRes, "Result_Text_TextElement", true)) {
							String message=WSAssert.getElementValue(modifyItemHoldRes, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The error displayed in the response is :"+ message+"</b>");
							
						}
					    if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", true)) {
								String operaErrorCode=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ operaErrorCode+"</b>");
							}
					    if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_GDSError", true)) {
							String GDSErrorCode=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ GDSErrorCode+"</b>");
						}
						
						// Validation for OWS Operation
						if(WSAssert.assertIfElementExists(modifyItemHoldRes,"ModifyItemHoldResponse_Result_resultStatusFlag", true)) {
							if(WSAssert.assertIfElementValueEquals(modifyItemHoldRes,"ModifyItemHoldResponse_Result_resultStatusFlag", "SUCCESS", false))
						
		                {
		                	/**
		                	 *  Validating the database details 
		                	 */
			                
		                	LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
							LinkedHashMap<String,String> actualValues = new LinkedHashMap<String,String>();
							
							
							expectedValues.put("ITEM_HOLD_ID",WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_ItemHoldID", XMLType.REQUEST));
							expectedValues.put("BEGIN_DATE", WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_StayDateRange_StartDate", XMLType.REQUEST));
							expectedValues.put("END_DATE", WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_StayDateRange_EndDate", XMLType.REQUEST));
							expectedValues.put("QUANTITY", WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_Quantity", XMLType.REQUEST));
						
							
							String query = WSClient.getQuery("QS_02");
         					actualValues = WSClient.getDBRow(query);
         					
							WSAssert.assertEquals(expectedValues,actualValues,false);
							
							
		            
					   }  
							else
				        	{
				        	 WSClient.writeToReport(LogStatus.FAIL, "Unable to modify Item Hold ");
				        	}
						
				} 
								else {
									WSClient.writeToReport(LogStatus.FAIL, "Schema is incorrect");
								}}}
								else{
									WSClient.writeToReport(LogStatus.ERROR, "Unable to create Item Hold ");
								}
									}
								}}}
		        
				catch(Exception e)
				{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
				} 	

    finally
    {
	// Cancel Reservation
				try
				{
			        CancelReservation.cancelReservation("DS_02");
			        ReleaseItemInventoryHold.releaseItemInventoryHold("DS_01");
				}
	            catch (Exception e) 
				{
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
 }}

	  
	@Test(groups = { "minimumRegression", "ModifyItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify that an error message is obtained when no hotelCode is passed in the request.
	 * 
	 */
	public void modifyItemHold_40261(){
		try {
			
			
			String testName = "modifyItemHold_40261";
			WSClient.startTest(testName, "Verify that an error message is obtained when no hotelCode is passed in the request.", "minimumRegression");		
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "ItemCode"})) {
						String resortOperaValue=OPERALib.getResort();
						 String resort = OPERALib.getResort();
						 String channel = OWSLib.getChannel();
							String owsResort=OWSLib.getChannelResort(resort, channel);
							WSClient.setData("{var_owsResort}",owsResort);
						 String chain=OPERALib.getChain();
							WSClient.setData("{var_chain}", chain);
						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
								   OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
								 OPERALib.setOperaHeader(OPERALib.getUserName());
								 
								 
								// Prerequisite 1: Create Profile
								 if (profileID.equals(""))
										profileID = CreateProfile.createProfile("DS_01");
									if (!profileID.equals("error")) {
										WSClient.setData("{var_profileId}", profileID);

										WSClient.writeToReport(LogStatus.INFO,"<b>Profile ID:"+profileID+"</b>");


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

									WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+resvID+"</b>");

									String Date=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
									WSClient.setData("{var_startDate}", Date);
									WSClient.setData("{var_endDate}", Date);
									WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
								    WSClient.setData("{var_count}","0");
								    //String heldById = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
									//WSClient.setData("{var_heldById}", heldById);
									WSClient.setData("{var_resort}",resortOperaValue);
									WSClient.setData("{var_quantity}","1");
								    
								// Prerequisite 3 : Create Item Hold 
									
								    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_01");
									String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
									  
							  
							if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true))
							{
								if(WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_HeldById_ID", false))
								{
									//Getting itemHoldId based on the heldById 
									String QS_01 = WSClient.getQuery("OWSModifyItemHold","QS_05");
									String itemHoldId = WSClient.getDBRow(QS_01).get("ITEM_HOLD_ID");
									WSClient.writeToReport(LogStatus.INFO,"<b>ItemHold ID:"+itemHoldId+"</b>");
									WSClient.setData("{var_itemHoldId}", itemHoldId);
								
						//Prerequisite	: Modify Item Hold
								
						String modifyItemHoldReq = WSClient.createSOAPMessage("OWSModifyItemHold", "DS_04");
				        String modifyItemHoldRes= WSClient.processSOAPMessage(modifyItemHoldReq);

				        if(WSAssert.assertIfElementExists(modifyItemHoldRes, "Result_Text_TextElement",true))
						{
			
							/**** Verifying that the error message is populated on the response ********/
				
						String message=WSAssert.getElementValue(modifyItemHoldRes,
							       "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the  response is :"+ message+"</b>");
						}
				        
						if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode",true)) 
						{
							
							/**** Verifying whether the error Message is populated on the response ****/
							
							String message=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
						}
						

		                if(WSAssert.assertIfElementExists(modifyItemHoldRes,
							      "ModifyItemHoldResponse_Result_resultStatusFlag", false))
		                {
		                	
		                	WSAssert.assertIfElementValueEquals(modifyItemHoldRes,
								      "ModifyItemHoldResponse_Result_resultStatusFlag", "FAIL", false);
		            
					   }  }
							else
								{
								WSClient.writeToReport(LogStatus.ERROR, "Unable to create Item Hold ");
								}
								}}
								}
				
			
		} }
				catch(Exception e)
				{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
				} 	
		finally
		  {
			// Cancel Reservation
						try
						{
					        CancelReservation.cancelReservation("DS_02");
						}
			            catch (Exception e) 
						{
			                // TODO Auto-generated catch block
			                e.printStackTrace();
			            }
		  }
	}
	
	
	//@Test(groups = { "minimumRegression", "ModifyItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify if an error message is obtained when no date range is passed in the request.
	 * 
	 */
//	public void modifyItemHold_39904() {
//		try {
//			
//			
//			String testName = "modifyItemHold_39904";
//			WSClient.startTest(testName, "Verify if an error message is obtained when no date range is passed in the request.", "minimumRegression");		
//			if (OperaPropConfig.getPropertyConfigResults(
//					new String[] { "ItemCode"})) {
//						String resortOperaValue=OPERALib.getResort();
//						 String resort = OPERALib.getResort();
//						 //getChannelCarier(resortOperaValue, channel)
//						 String chain=OPERALib.getChain();
//							WSClient.setData("{var_chain}", chain);
//						//OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
//								 //  OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
//								 OPERALib.setOperaHeader(OPERALib.getUserName());
//								 
//								 
//								// Prerequisite 1: Create Profile
//								 if (profileID.equals(""))
//										profileID = CreateProfile.createProfile("DS_01");
//									if (!profileID.equals("error")) {
//										WSClient.setData("{var_profileId}", profileID);
//
//										WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
//										WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
//										WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
//										WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
//										WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
//
//								// Prerequisite 2 Create Reservation
//										HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
//										String resvID = resv.get("reservationId");
//
//										if (!resvID.equals("error")) {
//
//									WSClient.setData("{var_resvId}", resvID);
//									String Date=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
//									WSClient.setData("{var_startDate}", Date);
//									WSClient.setData("{var_endDate}", Date);
//									WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
//								    WSClient.setData("{var_count}","0");
//								    //String heldById = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
//									//WSClient.setData("{var_heldById}", heldById);
//									WSClient.setData("{var_resort}",resortOperaValue);
//									WSClient.setData("{var_quantity}","1");
//								    
//								// Prerequisite 3 : Create Item Hold 
//									
//								    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_01");
//									String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
//									  
//							  
//							if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true))
//							{
//								if(WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_HeldById_ID", false))
//								{
//									//Getting itemHoldId based on the heldById 
//									String QS_01 = WSClient.getQuery("OWSModifyItemHold","QS_05");
//									String itemHoldId = WSClient.getDBRow(QS_01).get("ITEM_HOLD_ID");
//									WSClient.writeToReport(LogStatus.INFO,"<b>ItemHold ID:"+itemHoldId+"</b>");
//									WSClient.setData("{var_itemHoldId}", itemHoldId);
//								
//						//Prerequisite	: Modify Item Hold
//								
//						String modifyItemHoldReq = WSClient.createSOAPMessage("OWSModifyItemHold", "DS_05");
//				        String modifyItemHoldRes= WSClient.processSOAPMessage(modifyItemHoldReq);
//
//				        if(WSAssert.assertIfElementExists(modifyItemHoldRes, "Result_Text_TextElement",true))
//						{
//			
//							/**** Verifying that the error message is populated on the response ********/
//				
//						String message=WSAssert.getElementValue(modifyItemHoldRes,
//							       "Result_Text_TextElement", XMLType.RESPONSE);
//						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the  response is :"+ message+"</b>");
//						}
//				        
//						if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode",true)) 
//						{
//							
//							/**** Verifying whether the error Message is populated on the response ****/
//							
//							String message=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
//							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
//						}
//						if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_GDSError", true)) {
//							String GDSErrorCode=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
//							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ GDSErrorCode+"</b>");
//						}
//
//		                if(WSAssert.assertIfElementExists(modifyItemHoldRes,
//							      "ModifyItemHoldResponse_Result_resultStatusFlag", false))
//		                {
//		                	
//		                	WSAssert.assertIfElementValueEquals(modifyItemHoldRes,
//								      "ModifyItemHoldResponse_Result_resultStatusFlag", "FAIL", false);
//		            
//					   }  }
//							else
//								{
//								WSClient.writeToReport(LogStatus.ERROR, "Unable to create Item Hold ");
//								}
//								}}
//								}
//				
//			
//		} }
//				catch(Exception e)
//				{
//					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
//				} 	
//		finally
//		  {
//			// Cancel Reservation
//						try
//						{
//					        CancelReservation.cancelReservation("DS_02");
//						}
//			            catch (Exception e) 
//						{
//			                // TODO Auto-generated catch block
//			                e.printStackTrace();
//			            }
//		  }
//	}
	
	@Test(groups = { "minimumRegression", "ModifyItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify that an error message is obtained when an invalid Item Hold ID is passed in the request.
	 * 
	 */
	public void modifyItemHold_40263() {
		try {
			
			
			String testName = "modifyItemHold_40263";
			WSClient.startTest(testName, "Verify that an error message is obtained when an invalid Item Hold ID is passed in the request.", "minimumRegression");		
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "ItemCode"})) {
						String resortOperaValue=OPERALib.getResort();
						 String resort = OPERALib.getResort();
						 String channel = OWSLib.getChannel();
							String owsResort=OWSLib.getChannelResort(resort, channel);
							WSClient.setData("{var_owsResort}",owsResort);
						 String chain=OPERALib.getChain();
							WSClient.setData("{var_chain}", chain);
						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
								   OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
								 OPERALib.setOperaHeader(OPERALib.getUserName());
								 
								 
								// Prerequisite 1: Create Profile
								 if (profileID.equals(""))
										profileID = CreateProfile.createProfile("DS_01");
									if (!profileID.equals("error")) {
										WSClient.setData("{var_profileId}", profileID);

										WSClient.writeToReport(LogStatus.INFO,"<b>Profile ID:"+profileID+"</b>");


										WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
										WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
										WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
										WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
										WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
										WSClient.setData("{var_itemHoldId}",WSClient.getKeywordData("{KEYWORD_ID}"));

								// Prerequisite 2 Create Reservation
										HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
										String resvID = resv.get("reservationId");

										if (!resvID.equals("error")) {

											
									WSClient.setData("{var_resvId}", resvID);

									WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+resvID+"</b>");

									String Date=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
									WSClient.setData("{var_startDate}", Date);
									WSClient.setData("{var_endDate}", Date);
									WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
								    WSClient.setData("{var_count}","0");
								    //String heldById = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
									//WSClient.setData("{var_heldById}", heldById);
									WSClient.setData("{var_resort}",resortOperaValue);
									WSClient.setData("{var_quantity}","1");
								    
								// Prerequisite 3 : Create Item Hold 
									
								    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_01");
									String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
									  
							  
							if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true))
							{
								if(WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_HeldById_ID", false))
								{
									 
									
						//Modify Item Hold
								
						String modifyItemHoldReq = WSClient.createSOAPMessage("OWSModifyItemHold", "DS_05");
				        String modifyItemHoldRes= WSClient.processSOAPMessage(modifyItemHoldReq);

				        if(WSAssert.assertIfElementExists(modifyItemHoldRes, "Result_Text_TextElement",true))
						{
			
							/**** Verifying that the error message is populated on the response ********/
				
						String message=WSAssert.getElementValue(modifyItemHoldRes,
							       "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the  response is :"+ message+"</b>");
						}
				        
						if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode",true)) 
						{
							
							/**** Verifying whether the error Message is populated on the response ****/
							
							String message=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
						}
						if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_GDSError", true)) {
							String GDSErrorCode=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ GDSErrorCode+"</b>");
						}

		                if(WSAssert.assertIfElementExists(modifyItemHoldRes,
							      "ModifyItemHoldResponse_Result_resultStatusFlag", false))
		                {
		                	
		                	WSAssert.assertIfElementValueEquals(modifyItemHoldRes,
								      "ModifyItemHoldResponse_Result_resultStatusFlag", "FAIL", false);
		            
					   }  }
							else
								{
								WSClient.writeToReport(LogStatus.ERROR, "Unable to create Item Hold ");
								}
								}}
								}
				
			
		} }
				catch(Exception e)
				{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
				} 	
		finally
		  {
			// Cancel Reservation
						try
						{
					        CancelReservation.cancelReservation("DS_02");
						}
			            catch (Exception e) 
						{
			                // TODO Auto-generated catch block
			                e.printStackTrace();
			            }
		  }
	}
	
	
	@Test(groups = { "minimumRegression", "ModifyItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify that an error message is obtained when an invalid quantity is passed in the request.
	 * 
	 */
	public void modifyItemHold_40264() {
		try {
			
			
			String testName = "modifyItemHold_40264";
			WSClient.startTest(testName, "Verify that an error message is obtained when an invalid quantity is passed in the request.", "minimumRegression");		
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "ItemCode"})) {
						String resortOperaValue=OPERALib.getResort();
						 String resort = OPERALib.getResort();
						 String channel = OWSLib.getChannel();
							String owsResort=OWSLib.getChannelResort(resort, channel);
							WSClient.setData("{var_owsResort}",owsResort);
						 String chain=OPERALib.getChain();
							WSClient.setData("{var_chain}", chain);
						OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), OPERALib.getResort(),
								   OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
								 OPERALib.setOperaHeader(OPERALib.getUserName());
								 
								 
								// Prerequisite 1: Create Profile
								 if (profileID.equals(""))
										profileID = CreateProfile.createProfile("DS_01");
									if (!profileID.equals("error")) {
										WSClient.setData("{var_profileId}", profileID);

										WSClient.writeToReport(LogStatus.INFO,"<b>Profile ID:"+profileID+"</b>");


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

									WSClient.writeToReport(LogStatus.INFO,"<b>Reservation ID:"+resvID+"</b>");

									String Date=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
									WSClient.setData("{var_startDate}", Date);
									WSClient.setData("{var_endDate}", Date);
									WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
								    WSClient.setData("{var_count}","0");
								    //String heldById = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
									//WSClient.setData("{var_heldById}", heldById);
									WSClient.setData("{var_resort}",resortOperaValue);
									
								    
								// Prerequisite 3 : Create Item Hold 
									
								    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_01");
									String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
									  
							  
							if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true))
							{
								if(WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_HeldById_ID", false))
								{
									//Getting itemHoldId based on the heldById 
									String QS_01 = WSClient.getQuery("OWSModifyItemHold","QS_05");
									String itemHoldId = WSClient.getDBRow(QS_01).get("ITEM_HOLD_ID");
									WSClient.writeToReport(LogStatus.INFO,"<b>ItemHold ID:"+itemHoldId+"</b>");
									WSClient.setData("{var_itemHoldId}", itemHoldId); 
									
						//Modify Item Hold
								
						String modifyItemHoldReq = WSClient.createSOAPMessage("OWSModifyItemHold", "DS_06");
				        String modifyItemHoldRes= WSClient.processSOAPMessage(modifyItemHoldReq);

				        if(WSAssert.assertIfElementExists(modifyItemHoldRes, "Result_Text_TextElement",true))
						{
			
							/**** Verifying that the error message is populated on the response ********/
				
						String message=WSAssert.getElementValue(modifyItemHoldRes,
							       "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the  response is :"+ message+"</b>");
						}
				        
						if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode",true)) 
						{
							
							/**** Verifying whether the error Message is populated on the response ****/
							
							String message=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
						}
						if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_GDSError", true)) {
							String GDSErrorCode=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ GDSErrorCode+"</b>");
						}

		                if(WSAssert.assertIfElementExists(modifyItemHoldRes,
							      "ModifyItemHoldResponse_Result_resultStatusFlag", false))
		                {
		                	
		                	WSAssert.assertIfElementValueEquals(modifyItemHoldRes,
								      "ModifyItemHoldResponse_Result_resultStatusFlag", "FAIL", false);
		            
					   }  }
							else
								{
								WSClient.writeToReport(LogStatus.ERROR, "Unable to create Item Hold ");
								}
								}}
								}
				
			
		} }
				catch(Exception e)
				{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
				} 	
		finally
		  {
			// Cancel Reservation
						try
						{
					        CancelReservation.cancelReservation("DS_02");
						}
			            catch (Exception e) 
						{
			                // TODO Auto-generated catch block
			                e.printStackTrace();
			            }
		  }			
	}
	
	
	//@Test(groups = { "minimumRegression", "ModifyItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify if the held item details are modified when a quantity greater than stock is passed in the request when the parameter mandatory_yn is 'Y' and order_external_yn is 'Y'
	 * 
	 */
	 public void modifyItemHold_4528() {
			try {
				
				
				String testName = "modifyItemHold_4528";
				WSClient.startTest(testName, "Verify if the held item details are modified when a quantity greater than stock is passed in the request when the parameter mandatory_yn is 'Y' and order_external_yn is 'Y'", "minimumRegression");		
							
							String resortOperaValue=OPERALib.getResort();
							
							String chain=OPERALib.getChain();
							WSClient.setData("{var_chain}", chain);
							WSClient.setData("{var_resort}",resortOperaValue);
							String beginDate=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
							WSClient.setData("{var_beginDate}",beginDate);
							WSClient.setData("{var_endDate}",beginDate);
							String itemCode="2200379412";
							WSClient.setData("{var_itemHoldId}",itemCode);
							WSClient.writeToReport(LogStatus.INFO,"<b>Item Hold ID:"+itemCode+"</b>");
							
							LinkedHashMap<String,String> quantityValues = new LinkedHashMap<String,String>();
						    String query3=WSClient.getQuery("QS_03");
							quantityValues=WSClient.getDBRow(query3);
							WSClient.setData("{var_quantity}",quantityValues.get("QTY_IN_STOCK")+1);
							
				            String modifyItemHoldReq = WSClient.createSOAPMessage("OWSModifyItemHold", "DS_01");
					        String modifyItemHoldRes= WSClient.processSOAPMessage(modifyItemHoldReq);

					        if(WSAssert.assertIfElementExists(modifyItemHoldRes, "Result_Text_TextElement", true)) {
								String message=WSAssert.getElementValue(modifyItemHoldRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "The error displayed in the response is :"+ message+"</b>");
								
							}
						    if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", true)) {
									String operaErrorCode=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ operaErrorCode+"</b>");
								}
						    if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", true)) {
								String operaErrorCode=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ operaErrorCode+"</b>");
							}
							
							// Validation for OWS Operation
							if(WSAssert.assertIfElementExists(modifyItemHoldRes,"ModifyItemHoldResponse_Result_resultStatusFlag", true)) {
								if(WSAssert.assertIfElementValueEquals(modifyItemHoldRes,"ModifyItemHoldResponse_Result_resultStatusFlag", "SUCCESS", false))
							
			                {
			                	/**
			                	 *  Validating the database details 
			                	 */
								LinkedHashMap<String,String> parameterValues = new LinkedHashMap<String,String>();
								String query4=WSClient.getQuery("QS_04");
								parameterValues=WSClient.getDBRow(query4);
								if((parameterValues.get("mandatory_yn").equals("Y")))
								{
								WSClient.writeToReport(LogStatus.INFO,"The parameter mandatory_yn is on");
							    if((parameterValues.get("order_external_yn").equals("Y")))
							  	{
								WSClient.writeToReport(LogStatus.INFO,"The parameter order_external_yn is on");
								
				                LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
								LinkedHashMap<String,String> actualValues = new LinkedHashMap<String,String>();
								expectedValues.put("ITEM_HOLD_ID",WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_ItemHoldID", XMLType.REQUEST));
								expectedValues.put("BEGIN_DATE", WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_StayDateRange_StartDate", XMLType.REQUEST));
								expectedValues.put("END_DATE", WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_StayDateRange_EndDate", XMLType.REQUEST));
								expectedValues.put("QUANTITY", WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_Quantity", XMLType.REQUEST));
								
								String query = WSClient.getQuery("QS_02");
	         					actualValues = WSClient.getDBRow(query);
								WSAssert.assertEquals(expectedValues,actualValues,false);
								}
									
							  else
								  {
								  WSClient.writeToReport(LogStatus.INFO,"The parameter order_external_yn is off");
								  }
								  }
								else
								{
									WSClient.writeToReport(LogStatus.INFO,"The parameter mandatory_yn is off");
								}
								
			            
						   }  
								else
					        	{
					        	 WSClient.writeToReport(LogStatus.FAIL, "Unable to modify Item Hold ");
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
						        CancelReservation.cancelReservation("DS_02");
						        ReleaseItemInventoryHold.releaseItemInventoryHold("DS_01");
							}
				            catch (Exception e) 
							{
				                // TODO Auto-generated catch block
				                e.printStackTrace();
				            }
			  }
				
		}
	
	
	//@Test(groups = { "minimumRegression", "ModifyItemHold", "OWS", "Reservation"})
	/**
	 * 	 * Method to verify if an error message is obtained when a quantity greater than stock is passed in the request when the parameter mandatory_yn is 'Y' and order_external_yn is 'N'

	 * 
	 */
	 public void modifyItemHold_4529() {
			try {
				
				
				String testName = "modifyItemHold_4529";
				WSClient.startTest(testName, "Method to verify if an error message is obtained when a quantity greater than stock is passed in the request when the parameter mandatory_yn is 'Y' and order_external_yn is 'N'", "minimumRegression");		
							
							String resortOperaValue=OPERALib.getResort();
							
							String chain=OPERALib.getChain();
							WSClient.setData("{var_chain}", chain);
							WSClient.setData("{var_resort}",resortOperaValue);
							String beginDate=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
							WSClient.setData("{var_beginDate}",beginDate);
							WSClient.setData("{var_endDate}",beginDate);
							String itemCode="2200379412";
							WSClient.setData("{var_itemHoldId}",itemCode);
							WSClient.writeToReport(LogStatus.INFO,"Item Hold ID:"+itemCode);
							
							LinkedHashMap<String,String> quantityValues = new LinkedHashMap<String,String>();
						    String query3=WSClient.getQuery("QS_03");
							quantityValues=WSClient.getDBRow(query3);
							WSClient.setData("{var_quantity}",quantityValues.get("qty_in_stock")+1);
							
				            String modifyItemHoldReq = WSClient.createSOAPMessage("OWSModifyItemHold", "DS_01");
					        String modifyItemHoldRes= WSClient.processSOAPMessage(modifyItemHoldReq);

					        if(WSAssert.assertIfElementExists(modifyItemHoldRes, "Result_Text_TextElement", true)) {
								String message=WSAssert.getElementValue(modifyItemHoldRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "The error displayed in the response is :"+ message+"</b>");
								
							}
						    if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", true)) {
									String operaErrorCode=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ operaErrorCode+"</b>");
								}
						    if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", true)) {
								String operaErrorCode=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ operaErrorCode+"</b>");
							}
							
							// Validation for OWS Operation
							if(WSAssert.assertIfElementExists(modifyItemHoldRes,"ModifyItemHoldResponse_Result_resultStatusFlag", true)) {
								if(WSAssert.assertIfElementValueEquals(modifyItemHoldRes,"ModifyItemHoldResponse_Result_resultStatusFlag", "FAIL", false))
							
			                {
			                	/**
			                	 *  Validating the database details 
			                	 */
								LinkedHashMap<String,String> parameterValues = new LinkedHashMap<String,String>();
								String query4=WSClient.getQuery("QS_04");
								parameterValues=WSClient.getDBRow(query4);
								if((parameterValues.get("mandatory_yn").equals("Y")))
								{
								WSClient.writeToReport(LogStatus.INFO,"The parameter mandatory_yn is on");
							    if((parameterValues.get("order_external_yn").equals("N")))
							  	{
								WSClient.writeToReport(LogStatus.INFO,"The parameter order_external_yn is off");
								
				                LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
								LinkedHashMap<String,String> actualValues = new LinkedHashMap<String,String>();
								expectedValues.put("ITEM_HOLD_ID",WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_ItemHoldID", XMLType.REQUEST));
								expectedValues.put("BEGIN_DATE", WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_StayDateRange_StartDate", XMLType.REQUEST));
								expectedValues.put("END_DATE", WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_StayDateRange_EndDate", XMLType.REQUEST));
								expectedValues.put("QUANTITY", WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_Quantity", XMLType.REQUEST));
								
								String query = WSClient.getQuery("QS_02");
	         					actualValues = WSClient.getDBRow(query);
								WSAssert.assertEquals(expectedValues,actualValues,false);
								}
									
							  else
								  {
								  WSClient.writeToReport(LogStatus.INFO,"The parameter order_external_yn is on");
								  }
								  }
								else
								{
									WSClient.writeToReport(LogStatus.INFO,"The parameter mandatory_yn is off");
								}
								
			            
						   }  
								else
					        	{
					        	 WSClient.writeToReport(LogStatus.FAIL, "Unable to modify Item Hold ");
					        	}
							}
			                
					} 
			        
					catch(Exception e)
					{
						WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
					} 	
				
		}
	
	
	//@Test(groups = { "minimumRegression", "ModifyItemHold", "OWS", "Reservation"})
	/**
	 * 	 * Method to verify if an error message is obtained when a quantity greater than stock is passed in the request when the parameter mandatory_yn is 'N'

	 * 
	 */
	 public void modifyItemHold_4530() {
			try {
				
				
				String testName = "modifyItemHold_4530";
				WSClient.startTest(testName, "Method to verify if an error message is obtained when a quantity greater than stock is passed in the request when the parameter mandatory_yn is 'N'", "minimumRegression");		
							
							String resortOperaValue=OPERALib.getResort();
							
							String chain=OPERALib.getChain();
							WSClient.setData("{var_chain}", chain);
							WSClient.setData("{var_resort}",resortOperaValue);
							String beginDate=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
							WSClient.setData("{var_beginDate}",beginDate);
							WSClient.setData("{var_endDate}",beginDate);
							String itemCode="2200379412";
							WSClient.setData("{var_itemHoldId}",itemCode);
							WSClient.writeToReport(LogStatus.INFO,"Item Hold ID:"+itemCode);
							
							LinkedHashMap<String,String> quantityValues = new LinkedHashMap<String,String>();
						    String query3=WSClient.getQuery("QS_03");
							quantityValues=WSClient.getDBRow(query3);
							WSClient.setData("{var_quantity}",quantityValues.get("qty_in_stock")+1);
							
				            String modifyItemHoldReq = WSClient.createSOAPMessage("OWSModifyItemHold", "DS_01");
					        String modifyItemHoldRes= WSClient.processSOAPMessage(modifyItemHoldReq);

					        if(WSAssert.assertIfElementExists(modifyItemHoldRes, "Result_Text_TextElement", true)) {
								String message=WSAssert.getElementValue(modifyItemHoldRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "The error displayed in the response is :"+ message+"</b>");
								
							}
						    if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", true)) {
									String operaErrorCode=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ operaErrorCode+"</b>");
								}
						    if(WSAssert.assertIfElementExists(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", true)) {
								String operaErrorCode=WSAssert.getElementValue(modifyItemHoldRes, "ModifyItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ operaErrorCode+"</b>");
							}
							
							
							// Validation for OWS Operation
							if(WSAssert.assertIfElementExists(modifyItemHoldRes,"ModifyItemHoldResponse_Result_resultStatusFlag", true)) {
								if(WSAssert.assertIfElementValueEquals(modifyItemHoldRes,"ModifyItemHoldResponse_Result_resultStatusFlag", "SUCCESS", false))
							
			                {
			                	/**
			                	 *  Validating the database details 
			                	 */
								LinkedHashMap<String,String> parameterValues = new LinkedHashMap<String,String>();
								String query4=WSClient.getQuery("QS_04");
								parameterValues=WSClient.getDBRow(query4);
								if((parameterValues.get("mandatory_yn").equals("N")))
								{
								WSClient.writeToReport(LogStatus.INFO,"The parameter mandatory_yn is off");
							    if((parameterValues.get("order_external_yn").equals("Y")))
							  	{
								WSClient.writeToReport(LogStatus.INFO,"The parameter order_external_yn is on");
								
				                LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
								LinkedHashMap<String,String> actualValues = new LinkedHashMap<String,String>();
								expectedValues.put("ITEM_HOLD_ID",WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_ItemHoldID", XMLType.REQUEST));
								expectedValues.put("BEGIN_DATE", WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_StayDateRange_StartDate", XMLType.REQUEST));
								expectedValues.put("END_DATE", WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_StayDateRange_EndDate", XMLType.REQUEST));
								expectedValues.put("QUANTITY", WSClient.getElementValue(modifyItemHoldReq, "ModifyItemHoldRequest_Quantity", XMLType.REQUEST));
								
								String query = WSClient.getQuery("QS_02");
	         					actualValues = WSClient.getDBRow(query);
								WSAssert.assertEquals(expectedValues,actualValues,false);
								}
									
							  else
								  {
								  WSClient.writeToReport(LogStatus.INFO,"The parameter order_external_yn is on");
								  }
								  }
								else
								{
									WSClient.writeToReport(LogStatus.INFO,"The parameter mandatory_yn is off");
								}
								
			            
						   }  
								else
					        	{
					        	 WSClient.writeToReport(LogStatus.FAIL, "Unable to modify Item Hold ");
					        	}
							}
			                
					} 
			        
					catch(Exception e)
					{
						WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
					} 	
				
		}
}
	
	
