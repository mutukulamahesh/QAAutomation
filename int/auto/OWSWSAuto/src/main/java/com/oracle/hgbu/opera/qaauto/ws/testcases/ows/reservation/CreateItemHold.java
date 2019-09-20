package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ReleaseItemInventoryHold;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateItemHold extends WSSetUp{
	        
	@Test(groups = { "sanity", "CreateItemHold", "OWS", "Reservation"})
	
	/**
	 * Method to verify that a hold item is created when minimum data is passed in the request
	 * 
	 */
	/*****
	 * * * PreRequisites Required: -->An item should be available
	 * 
	 *****/
	
	public void createItemHold_39940() {
		try {
			
			
			String testName = "createItemHold_39940";
			WSClient.startTest(testName, "Verify that a hold item is created successfully", "sanity");		
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "ItemCode"})) {
						String resortOperaValue=OPERALib.getResort();
						String uname = OPERALib.getUserName();
						String resort = OPERALib.getResort();
						String chain=OPERALib.getChain();
						WSClient.setData("{var_chain}", chain);
						WSClient.setData("{var_resort}",resortOperaValue);
						String channel = OWSLib.getChannel();
						String owsResort=OWSLib.getChannelResort(resort, channel);
						WSClient.setData("{var_owsResort}",owsResort);
						String beginDate=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
						WSClient.setData("{var_beginDate}",beginDate);
						WSClient.setData("{var_endDate}",beginDate);
						WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			     
			            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
						
						
						
						String createItemHoldReq = WSClient.createSOAPMessage("OWSCreateItemHold", "DS_01");
				        String createItemHoldRes= WSClient.processSOAPMessage(createItemHoldReq);

				        if(WSAssert.assertIfElementExists(createItemHoldRes, "Result_Text_TextElement", true)) {
							String message=WSAssert.getElementValue(createItemHoldRes, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "The error displayed in the response is :"+ message+"</b>");
							
						}
					    if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", true)) {
								String operaErrorCode=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ operaErrorCode+"</b>");
							}
					    if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", true)) {
							String GDSErrorCode=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ GDSErrorCode+"</b>");
						}
					
						
						// Validation for OWS Operation
						if(WSAssert.assertIfElementExists(createItemHoldRes,"CreateItemHoldResponse_Result_resultStatusFlag", true)) {
							if(WSAssert.assertIfElementValueEquals(createItemHoldRes,"CreateItemHoldResponse_Result_resultStatusFlag", "SUCCESS", false))
						
		                {
		                	/**
		                	 *  Validating the database details 
		                	 */
								
						    String identifier=WSClient.getElementValue(createItemHoldRes,"CreateItemHoldResponse_ItemHoldID",XMLType.RESPONSE);
			                WSClient.setData("{var_itemHoldId}",identifier);
			                WSClient.writeToReport(LogStatus.INFO,"<b>ItemHold ID:"+ identifier+"</b>");
			                
		                	LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
							LinkedHashMap<String,String> actualValues = new LinkedHashMap<String,String>();
							
							String QS_01 = WSClient.getQuery("OWSCreateItemHold","QS_05");
							String itemId = WSClient.getDBRow(QS_01).get("ITEM_ID");
							WSClient.setData("{var_itemId}", itemId);
							expectedValues.put("ITEM_ID",itemId);
							expectedValues.put("BEGIN_DATE", WSClient.getElementValue(createItemHoldReq, "CreateItemHoldRequest_StayDateRange_StartDate", XMLType.REQUEST));
							expectedValues.put("END_DATE", WSClient.getElementValue(createItemHoldReq, "CreateItemHoldRequest_StayDateRange_EndDate", XMLType.REQUEST));
							String query = WSClient.getQuery("QS_01");
         					actualValues = WSClient.getDBRow(query);
							WSAssert.assertEquals(expectedValues,actualValues,false);
							
							
							
							
							
							
		            
					   }  
							else
				        	{
			        	 WSClient.writeToReport(LogStatus.FAIL, "Unable to create Item Hold ");
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
						
					     //ReleaseItemInventoryHold.releaseItemInventoryHold("DS_01");
						}
			            catch (Exception e) 
						{
			                // TODO Auto-generated catch block
			                e.printStackTrace();
			            }
		  }
			
	}
	
	
	@Test(groups = { "minimumRegression", "CreateItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify that an error message is obtained when no ItemCode is passed in the request.
	 * 
	 */
	/*****
	 * * * PreRequisites Required: -->An item should be available
	 * 
	 *****/
	public void createItemHold_40248() {
		try {
			
			
			String testName = "createItemHold_40248";
			WSClient.startTest(testName, "Verify that an error message is obtained when no ItemCode is passed in the request.", "minimumRegression");	
			String resortOperaValue=OPERALib.getResort();
			String chain=OPERALib.getChain();
			String uname = OPERALib.getUserName();
			String resort = OPERALib.getResort();
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_resort}",resortOperaValue);
			String beginDate=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
			WSClient.setData("{var_beginDate}",beginDate);
			WSClient.setData("{var_endDate}",beginDate);
			
	        String channel = OWSLib.getChannel();
	       
			String owsResort=OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}",owsResort);
            String pwd = OPERALib.getPassword();
            String channelType = OWSLib.getChannelType(channel);
            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
            String createItemHoldReq = WSClient.createSOAPMessage("OWSCreateItemHold", "DS_02");
	        String createItemHoldRes= WSClient.processSOAPMessage(createItemHoldReq);

				        if(WSAssert.assertIfElementExists(createItemHoldRes, "Result_Text_TextElement",true))
						{
			
							/**** Verifying that the error message is populated on the response ********/
				
						String message=WSAssert.getElementValue(createItemHoldRes,
							       "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the  response is :"+ message+"</b>");
						}
				        
						if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode",true)) 
						{
							
							/**** Verifying whether the error Message is populated on the response ****/
							
							String message=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
						}
						if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", true)) {
							String GDSErrorCode=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ GDSErrorCode+"</b>");
						}
					

		                if(WSAssert.assertIfElementExists(createItemHoldRes,
							      "CreateItemHoldResponse_Result_resultStatusFlag", false))
		                {
		                	
		                	WSAssert.assertIfElementValueEquals(createItemHoldRes,
								      "CreateItemHoldResponse_Result_resultStatusFlag", "FAIL", false);
		            
					   }  
						
		} 
				catch(Exception e)
				{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
				} 	
			
	}
	
	
	
	@Test(groups = { "minimumRegression", "CreateItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify that an error message is obtained when no HotelCode is passed in the request.
	 * 
	 */
	/*****
	 * * * PreRequisites Required: -->An item should be available
	 * 
	 *****/
	public void createItemHold_40249() {
		try {
			
			
			String testName = "createItemHold_40249";
			WSClient.startTest(testName, "Verify that a held item is created when no hotelCode is passed in the request.", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "ItemCode"})) {
			String chain=OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			String uname = OPERALib.getUserName();
			String resort = OPERALib.getResort();
			String beginDate=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
			WSClient.setData("{var_beginDate}",beginDate);
			WSClient.setData("{var_endDate}",beginDate);
			WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
			String channel = OWSLib.getChannel();
			
			String owsResort=OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}",owsResort);
            String pwd = OPERALib.getPassword();
            String channelType = OWSLib.getChannelType(channel);
            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
			
			
			
			String createItemHoldReq = WSClient.createSOAPMessage("OWSCreateItemHold", "DS_04");
	        String createItemHoldRes= WSClient.processSOAPMessage(createItemHoldReq);

				        if(WSAssert.assertIfElementExists(createItemHoldRes, "Result_Text_TextElement",true))
						{
			
							/**** Verifying that the error message is populated on the response ********/
				
						String message=WSAssert.getElementValue(createItemHoldRes,
							       "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the  response is :"+ message+"</b>");
						}
				        
						if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode",true)) 
						{
							
							/**** Verifying whether the error Message is populated on the response ****/
							
							String message=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
						}
						
					

		                if(WSAssert.assertIfElementExists(createItemHoldRes,
							      "CreateItemHoldResponse_Result_resultStatusFlag", false))
		                {
		                	
		                	WSAssert.assertIfElementValueEquals(createItemHoldRes,
								      "CreateItemHoldResponse_Result_resultStatusFlag", "FAIL", false);
		            
					   }  
				
			
		} }
				catch(Exception e)
				{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
				} 	
			
	}
	
	
	@Test(groups = { "minimumRegression", "CreateItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify that an error message is obtained when no Date Range is passed in the request.
	 * 
	 */
	public void createItemHold_40250() {
		try {
			
			
			String testName = "createItemHold_40250";
			WSClient.startTest(testName, "Verify that an error message is obtained when no Date Range is passed in the request.", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "ItemCode"})) {
			String chain=OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			String uname = OPERALib.getUserName();
			String resort = OPERALib.getResort();
			String beginDate=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
			String resortOperaValue=OPERALib.getResort();
			WSClient.setData("{var_resort}",resortOperaValue);
			WSClient.setData("{var_beginDate}",beginDate);
			WSClient.setData("{var_endDate}",beginDate);
			WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
			String channel = OWSLib.getChannel();
			String owsResort=OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}",owsResort);
            String pwd = OPERALib.getPassword();
            String channelType = OWSLib.getChannelType(channel);
            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
			
			
			
			String createItemHoldReq = WSClient.createSOAPMessage("OWSCreateItemHold", "DS_05");
	        String createItemHoldRes= WSClient.processSOAPMessage(createItemHoldReq);

				        if(WSAssert.assertIfElementExists(createItemHoldRes, "Result_Text_TextElement",true))
						{
			
							/**** Verifying that the error message is populated on the response ********/
				
						String message=WSAssert.getElementValue(createItemHoldRes,
							       "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the  response is :"+ message+"</b>");
						}
				        
						if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode",true)) 
						{
							
							/**** Verifying whether the error Message is populated on the response ****/
							
							String message=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
						}
						if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", true)) {
							String GDSErrorCode=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ GDSErrorCode+"</b>");
						}
					

		                if(WSAssert.assertIfElementExists(createItemHoldRes,
							      "CreateItemHoldResponse_Result_resultStatusFlag", false))
		                {
		                	
		                	WSAssert.assertIfElementValueEquals(createItemHoldRes,
								      "CreateItemHoldResponse_Result_resultStatusFlag", "FAIL", false);
		            
					   }  
				
			
		} }
				catch(Exception e)
				{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
				} 	
			
	}
	
	
	@Test(groups = { "minimumRegression", "CreateItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify that an error message is obtained when invalid ItemCode is passed in the request.
	 * 
	 */
	/*****
	 * * * PreRequisites Required: -->An item should be available
	 * 
	 *****/
	public void createItemHold_40251() {
		try {
			
			
			String testName = "createItemHold_40251";
			WSClient.startTest(testName, "Verify that an error message is obtained when an invalid ItemCode is passed in the request.", "minimumRegression");	
			String chain=OPERALib.getChain();
			String uname = OPERALib.getUserName();
			String resort = OPERALib.getResort();
			WSClient.setData("{var_chain}", chain);
			String beginDate=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
			WSClient.setData("{var_beginDate}",beginDate);
			WSClient.setData("{var_endDate}",beginDate);
			String resortOperaValue=OPERALib.getResort();
			WSClient.setData("{var_resort}",resortOperaValue);
			WSClient.setData("{var_itemCode}",WSClient.getKeywordData("{KEYWORD_ID}"));	
			
			String channel = OWSLib.getChannel();
			String owsResort=OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}",owsResort);
            String pwd = OPERALib.getPassword();
            String channelType = OWSLib.getChannelType(channel);
            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
            String createItemHoldReq = WSClient.createSOAPMessage("OWSCreateItemHold", "DS_01");
	        String createItemHoldRes= WSClient.processSOAPMessage(createItemHoldReq);

				        if(WSAssert.assertIfElementExists(createItemHoldRes, "Result_Text_TextElement",true))
						{
			
							/**** Verifying that the error message is populated on the response ********/
				
						String message=WSAssert.getElementValue(createItemHoldRes,
							       "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the  response is :"+ message+"</b>");
						}
				        
						if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode",true)) 
						{
							
							/**** Verifying whether the error Message is populated on the response ****/
							
							String message=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
						}
						if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", true)) {
							String GDSErrorCode=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ GDSErrorCode+"</b>");
						}
					

		                if(WSAssert.assertIfElementExists(createItemHoldRes,
							      "CreateItemHoldResponse_Result_resultStatusFlag", false))
		                {
		                	
		                	WSAssert.assertIfElementValueEquals(createItemHoldRes,
								      "CreateItemHoldResponse_Result_resultStatusFlag", "FAIL", false);
		            
					   }  
				
			
		} 
				catch(Exception e)
				{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
				} 	
			
	}
	
	@Test(groups = { "minimumRegression", "CreateItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify that an error message is obtained when an invalid Quantity is passed in the request.
	 * 
	 */
	/*****
	 * * * PreRequisites Required: -->An item should be available
	 * 
	 *****/
	public void createItemHold_40252() {
		try {
			
			
			String testName = "createItemHold_40252";
			WSClient.startTest(testName, "Verify that an error message is obtained when an invalid Quantity is passed in the request.", "minimumRegression");	
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "ItemCode"})) {
			String chain=OPERALib.getChain();
			String uname = OPERALib.getUserName();
			String resort = OPERALib.getResort();
			WSClient.setData("{var_chain}", chain);
			String beginDate=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
			String resortOperaValue=OPERALib.getResort();
			WSClient.setData("{var_resort}",resortOperaValue);
			WSClient.setData("{var_beginDate}",beginDate);
			WSClient.setData("{var_endDate}",beginDate);
			WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
			String channel = OWSLib.getChannel();
			String owsResort=OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}",owsResort);
            String pwd = OPERALib.getPassword();
            String channelType = OWSLib.getChannelType(channel);
            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
			
			String createItemHoldReq = WSClient.createSOAPMessage("OWSCreateItemHold", "DS_06");
	        String createItemHoldRes= WSClient.processSOAPMessage(createItemHoldReq);

				        if(WSAssert.assertIfElementExists(createItemHoldRes, "Result_Text_TextElement",true))
						{
			
							/**** Verifying that the error message is populated on the response ********/
				
						String message=WSAssert.getElementValue(createItemHoldRes,
							       "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the  response is :"+ message+"</b>");
						}
				        
						if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode",true)) 
						{
							
							/**** Verifying whether the error Message is populated on the response ****/
							
							String message=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
						}
						if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", true)) {
							String GDSErrorCode=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ GDSErrorCode+"</b>");
						}
					

		                if(WSAssert.assertIfElementExists(createItemHoldRes,
							      "CreateItemHoldResponse_Result_resultStatusFlag", false))
		                {
		                	
		                	WSAssert.assertIfElementValueEquals(createItemHoldRes,
								      "CreateItemHoldResponse_Result_resultStatusFlag", "FAIL", false);
		            
					   }  
				
			
		} }
				catch(Exception e)
				{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
				} 	
			
	}
	
	
	@Test(groups = { "minimumRegression", "CreateItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify that an error message is obtained when an invalid date range is passed in the request.
	 * 
	 */
	/*****
	 * * * PreRequisites Required: -->An item should be available
	 * 
	 *****/
	public void createItemHold_40253() {
		try {
			
			
			String testName = "createItemHold_40253";
			WSClient.startTest(testName, "Verify that an error mesaage is created when an invalid date range is passed in the request.", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "ItemCode"})) {
			String chain=OPERALib.getChain();
			String uname = OPERALib.getUserName();
			String resort = OPERALib.getResort();
			WSClient.setData("{var_chain}", chain);
			String resortOperaValue=OPERALib.getResort();
			WSClient.setData("{var_resort}",resortOperaValue);
			String beginDate=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_5}");
			WSClient.setData("{var_beginDate}",beginDate);
			String endDate=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
			WSClient.setData("{var_endDate}",endDate);
			WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
			String channel = OWSLib.getChannel();
			String owsResort=OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}",owsResort);
            String pwd = OPERALib.getPassword();
            String channelType = OWSLib.getChannelType(channel);
            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
			String createItemHoldReq = WSClient.createSOAPMessage("OWSCreateItemHold", "DS_01");
	        String createItemHoldRes= WSClient.processSOAPMessage(createItemHoldReq);

				        if(WSAssert.assertIfElementExists(createItemHoldRes, "Result_Text_TextElement",true))
						{
			
							/**** Verifying that the error message is populated on the response ********/
				
						String message=WSAssert.getElementValue(createItemHoldRes,
							       "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the  response is :"+ message+"</b>");
						}
				        
						if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode",true)) 
						{
							
							/**** Verifying whether the error Message is populated on the response ****/
							
							String message=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
						}
						if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", true)) {
							String GDSErrorCode=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ GDSErrorCode+"</b>");
						}
					

		                if(WSAssert.assertIfElementExists(createItemHoldRes,
							      "CreateItemHoldResponse_Result_resultStatusFlag", false))
		                {
		                	
		                	WSAssert.assertIfElementValueEquals(createItemHoldRes,
								      "CreateItemHoldResponse_Result_resultStatusFlag", "FAIL", false);
		            
					   }  
				
			
		}} 
				catch(Exception e)
				{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
				} 	
			
	}
	
	
	@Test(groups = { "minimumRegression", "CreateItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify that an error message is obtained when an invalid hotel Code is passed in the request.
	 * 
	 */
	/*****
	 * * * PreRequisites Required: -->An item should be available
	 * 
	 *****/
	public void createItemHold_40254() {
		try {
			
			
			String testName = "createItemHold_40254";
			WSClient.startTest(testName, "Verify that an error message is created when an invalid hotel Code is passed in the request.", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "ItemCode"})) {
			String chain=OPERALib.getChain();
			WSClient.setData("{var_chain}", chain);
			String uname = OPERALib.getUserName();
			String resort = OPERALib.getResort();
			String beginDate=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
			WSClient.setData("{var_beginDate}",beginDate);
			WSClient.setData("{var_endDate}",beginDate);
			WSClient.setData("{var_resort}",WSClient.getKeywordData("{KEYWORD_ID}"));
			WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
			String channel = OWSLib.getChannel();
			//String owsResort=OWSLib.getChannelResort(resort, channel);
			WSClient.setData("{var_owsResort}",WSClient.getKeywordData("{KEYWORD_ID}"));
            String pwd = OPERALib.getPassword();
            String channelType = OWSLib.getChannelType(channel);
            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
			String createItemHoldReq = WSClient.createSOAPMessage("OWSCreateItemHold", "DS_01");
	        String createItemHoldRes= WSClient.processSOAPMessage(createItemHoldReq);

				        if(WSAssert.assertIfElementExists(createItemHoldRes, "Result_Text_TextElement",true))
						{
			
							/**** Verifying that the error message is populated on the response ********/
				
						String message=WSAssert.getElementValue(createItemHoldRes,
							       "Result_Text_TextElement", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the  response is :"+ message+"</b>");
						}
				        
						if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode",true)) 
						{
							
							/**** Verifying whether the error Message is populated on the response ****/
							
							String message=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
						}
						if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", true)) {
							String GDSErrorCode=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ GDSErrorCode+"</b>");
						}

		                if(WSAssert.assertIfElementExists(createItemHoldRes,
							      "CreateItemHoldResponse_Result_resultStatusFlag", false))
		                {
		                	
		                	WSAssert.assertIfElementValueEquals(createItemHoldRes,
								      "CreateItemHoldResponse_Result_resultStatusFlag", "FAIL", false);
		            
					   }  
				
			
		} }
				catch(Exception e)
				{
					WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
				} 	
			
	}
	
	
	

	
	
//@Test(groups = { "minimumRegression", "CreateItemHold", "OWS", "Reservation"})
/**
 * Method to verify that a held item can be created when a quantity greater than stock is passed in the request when the parameter mandatory_yn is 'Y' and order_external_yn is 'Y'
 * 
 */
 public void createItemHold_4533() {
		try {
			
			
			String testName = "createItemHold_4533";
			WSClient.startTest(testName, "Verify that a held item is created when a quantity greater than stock is passed in the request when the parameter mandatory_yn is 'Y' and order_external_yn is 'Y'", "minimumRegression");		
			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "ItemCode"})) {
						String resortOperaValue=OPERALib.getResort();
						
						String chain=OPERALib.getChain();
						WSClient.setData("{var_chain}", chain);
						WSClient.setData("{var_resort}",resortOperaValue);
						String uname = OPERALib.getUserName();
						String resort = OPERALib.getResort();
						String beginDate=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
						WSClient.setData("{var_beginDate}",beginDate);
						WSClient.setData("{var_endDate}",beginDate);
						WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						LinkedHashMap<String,String> quantityValues = new LinkedHashMap<String,String>();
					    String query3=WSClient.getQuery("QS_03");
						quantityValues=WSClient.getDBRow(query3);
						WSClient.writeToReport(LogStatus.INFO, quantityValues.toString());
						WSClient.setData("{var_quantity}",(quantityValues.get("QTY_IN_STOCK")+1));
						String channel = OWSLib.getChannel();
						String owsResort=OWSLib.getChannelResort(resort, channel);
						WSClient.setData("{var_owsResort}",owsResort);
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
			            String createItemHoldReq = WSClient.createSOAPMessage("OWSCreateItemHold", "DS_07");
				        String createItemHoldRes= WSClient.processSOAPMessage(createItemHoldReq);

				        if(WSAssert.assertIfElementExists(createItemHoldRes, "Result_Text_TextElement", true)) {
							String message=WSAssert.getElementValue(createItemHoldRes, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
							
						}
					    if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", true)) {
								String operaErrorCode=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ operaErrorCode+"</b>");
							}
						
					    if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", true)) {
							String operaErrorCode=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ operaErrorCode+"</b>");
						}
					
						// Validation for OWS Operation
						if(WSAssert.assertIfElementExists(createItemHoldRes,"CreateItemHoldResponse_Result_resultStatusFlag", true)) {
							if(WSAssert.assertIfElementValueEquals(createItemHoldRes,"CreateItemHoldResponse_Result_resultStatusFlag", "SUCCESS", false))
						
		                {
		                	/**
		                	 *  Validating the database details 
		                	 */
							LinkedHashMap<String,String> parameterValues = new LinkedHashMap<String,String>();
							String query2=WSClient.getQuery("QS_02");
							parameterValues=WSClient.getDBRow(query2);
							if((parameterValues.get("mandatory_yn")=="Y"))
							{
							WSClient.writeToReport(LogStatus.INFO,"The parameter mandatory_yn is on");
						    if((parameterValues.get("order_external_yn")=="Y"))
						  	{
							WSClient.writeToReport(LogStatus.INFO,"The parameter order_external_yn is on");
							
						    String identifier=WSClient.getElementValue(createItemHoldRes, "CreateItemHoldResponse_ItemHoldID",XMLType.RESPONSE);
			                WSClient.setData("{var_itemHoldId}", identifier);
			                
			                LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
							LinkedHashMap<String,String> actualValues = new LinkedHashMap<String,String>();
							
							
							expectedValues.put("ITEM_ID",WSClient.getElementValue(createItemHoldReq, "CreateItemHoldRequest_ItemCode", XMLType.REQUEST));
							expectedValues.put("BEGIN_DATE", WSClient.getElementValue(createItemHoldReq, "CreateItemHoldRequest_StayDateRange_StartDate", XMLType.REQUEST));
							expectedValues.put("END_DATE", WSClient.getElementValue(createItemHoldReq, "CreateItemHoldRequest_StayDateRange_EndDate", XMLType.REQUEST));
							
						
							
							String query = WSClient.getQuery("QS_01");
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
				        	 WSClient.writeToReport(LogStatus.FAIL, "Unable to create Item Hold ");
				        	}
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
			     ReleaseItemInventoryHold.releaseItemInventoryHold("DS_01");
				}
	            catch (Exception e) 
				{
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
  }
			
	}

	
	//@Test(groups = { "minimumRegression", "CreateItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify that an error message is obtained when a quantity greater than stock is passed in the request when the parameter mandatory_yn is 'Y' and order_external_yn is 'N'
	 * 
	 */
	 public void createItemHold_4534() {
			try {
				
				
				String testName = "createItemHold_4534";
				WSClient.startTest(testName, "Verify that an error message is obtained when a quantity greater than stock is passed in the request when the parameter mandatory_yn is 'Y' and order_external_yn is 'N'", "minimumRegression");		
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "ItemCode"})) {
							String resortOperaValue=OPERALib.getResort();
							
							String chain=OPERALib.getChain();
							WSClient.setData("{var_chain}", chain);
							WSClient.setData("{var_resort}",resortOperaValue);
							String uname = OPERALib.getUserName();
							String resort = OPERALib.getResort();
							String beginDate=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
							WSClient.setData("{var_beginDate}",beginDate);
							WSClient.setData("{var_endDate}",beginDate);
							WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
							LinkedHashMap<String,String> quantityValues = new LinkedHashMap<String,String>();
						    String query3=WSClient.getQuery("QS_03");
							quantityValues=WSClient.getDBRow(query3);
							WSClient.setData("{var_quantity}",quantityValues.get("qty_in_stock")+1);
							String channel = OWSLib.getChannel();
							String owsResort=OWSLib.getChannelResort(resort, channel);
							WSClient.setData("{var_owsResort}",owsResort);
				            String pwd = OPERALib.getPassword();
				            String channelType = OWSLib.getChannelType(channel);
				            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
							
				            String createItemHoldReq = WSClient.createSOAPMessage("OWSCreateItemHold", "DS_07");
					        String createItemHoldRes= WSClient.processSOAPMessage(createItemHoldReq);
					        
	
					        if(WSAssert.assertIfElementExists(createItemHoldRes, "Result_Text_TextElement", true)) {
								String message=WSAssert.getElementValue(createItemHoldRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
								
							}
						    if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", true)) {
									String operaErrorCode=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ operaErrorCode+"</b>");
								}
						    if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", true)) {
								String GDSErrorCode=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ GDSErrorCode+"</b>");
							}
						
							
							
							// Validation for OWS Operation
							if(WSAssert.assertIfElementExists(createItemHoldRes,"CreateItemHoldResponse_Result_resultStatusFlag", true)) {
								WSAssert.assertIfElementValueEquals(createItemHoldRes,"CreateItemHoldResponse_Result_resultStatusFlag", "FAIL", false);
							
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
			     ReleaseItemInventoryHold.releaseItemInventoryHold("DS_01");
				}
	            catch (Exception e) 
				{
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
 }}
				
	
	
	//@Test(groups = { "minimumRegression", "CreateItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify that a held item is created when a quantity greater than stock is passed in the request when the parameter mandatory_yn is 'N'
	 * 
	 */
	 public void createItemHold_39948() {
			try {
				
				
				String testName = "createItemHold_39948";
				WSClient.startTest(testName, "Verify that a held item is created when a quantity greater than stock is passed in the request when the parameter mandatory_yn is 'N'", "minimumRegression");		
				if (OperaPropConfig.getPropertyConfigResults(
						new String[] { "ItemCode"})) {
							String resortOperaValue=OPERALib.getResort();
							
							String chain=OPERALib.getChain();
							WSClient.setData("{var_chain}", chain);
							WSClient.setData("{var_resort}",resortOperaValue);
							String beginDate=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
							String uname = OPERALib.getUserName();
							String resort = OPERALib.getResort();
							WSClient.setData("{var_beginDate}",beginDate);
							WSClient.setData("{var_endDate}",beginDate);
							WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
							LinkedHashMap<String,String> quantityValues = new LinkedHashMap<String,String>();
						    String query3=WSClient.getQuery("QS_03");
							quantityValues=WSClient.getDBRow(query3);
							WSClient.setData("{var_quantity}",quantityValues.get("QTY_IN_STOCK")+1);
							String channel = OWSLib.getChannel();
				            String pwd = OPERALib.getPassword();
				            String channelType = OWSLib.getChannelType(channel);
				            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
				            String createItemHoldReq = WSClient.createSOAPMessage("OWSCreateItemHold", "DS_07");
					        String createItemHoldRes= WSClient.processSOAPMessage(createItemHoldReq);
	
					        if(WSAssert.assertIfElementExists(createItemHoldRes, "Result_Text_TextElement", true)) {
								String message=WSAssert.getElementValue(createItemHoldRes, "Result_Text_TextElement", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
								
							}
						    if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", true)) {
									String operaErrorCode=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ operaErrorCode+"</b>");
								}
						    if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", true)) {
								String GDSErrorCode=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ GDSErrorCode+"</b>");
							}
						
							
							// Validation for OWS Operation
							if(WSAssert.assertIfElementExists(createItemHoldRes,"CreateItemHoldResponse_Result_resultStatusFlag", true)) {
								if(WSAssert.assertIfElementValueEquals(createItemHoldRes,"CreateItemHoldResponse_Result_resultStatusFlag", "SUCCESS", false))
							
			                {
			                	/**
			                	 *  Validating the database details 
			                	 */
								LinkedHashMap<String,String> parameterValues = new LinkedHashMap<String,String>();
								String query2=WSClient.getQuery("QS_02");
								parameterValues=WSClient.getDBRow(query2);
								if((parameterValues.get("MANDATORY_YN").equals("N")))
								{
							    WSClient.writeToReport(LogStatus.INFO,"The parameter mandatory_yn is off");
								if((parameterValues.get("ORDER_EXTERNAL_YN").equals("Y")))
								{
								WSClient.writeToReport(LogStatus.INFO,"The parameter order_external_yn is on");
							    String identifier=WSClient.getElementValue(createItemHoldRes, "CreateItemHoldResponse_ItemHoldID",XMLType.RESPONSE);
							    WSClient.setData("{var_itemHoldId}", identifier);
			                	LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
								LinkedHashMap<String,String> actualValues = new LinkedHashMap<String,String>();
								expectedValues.put("ITEM_ID",WSClient.getElementValue(createItemHoldReq, "CreateItemHoldRequest_ItemCode", XMLType.REQUEST));
								expectedValues.put("QUANTITY", WSClient.getElementValue(createItemHoldReq, "CreateItemHoldRequest_Quantity", XMLType.REQUEST));
								expectedValues.put("BEGIN_DATE", WSClient.getElementValue(createItemHoldReq, "CreateItemHoldRequest_StayDateRange_StartDate", XMLType.REQUEST));
								expectedValues.put("END_DATE", WSClient.getElementValue(createItemHoldReq, "CreateItemHoldRequest_StayDateRange_EndDate", XMLType.REQUEST));
								String query = WSClient.getQuery("QS_01");
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
								WSClient.writeToReport(LogStatus.INFO,"The parameter mandatory_yn is on");
								}
								
			            
						   }  
								else
					        	{
					        	 WSClient.writeToReport(LogStatus.FAIL, "Unable to create Item Hold ");
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
				     ReleaseItemInventoryHold.releaseItemInventoryHold("DS_01");
					}
		            catch (Exception e) 
					{
		                // TODO Auto-generated catch block
		                e.printStackTrace();
		            }
	  }
				
			
		}
	
	@Test(groups = { "minimumRegression", "CreateItemHold", "OWS", "Reservation"})
	/**
	 * Method to verify that a hold item is created when all the required fields are passed in the request
	 * 
	 */
	/*****
	 * * * PreRequisites Required: -->An item should be available
	 * 
	 *****/
	
	public void createItemHold_40255() {
		try {
			
			
			String testName = "createItemHold_40255";
			WSClient.startTest(testName, "Verify that a hold item is created when the Item Details are passed in the request.", "minimumRegression");		
						
						String resortOperaValue=OPERALib.getResort();
						
						String chain=OPERALib.getChain();
						WSClient.setData("{var_chain}", chain);
						WSClient.setData("{var_resort}",resortOperaValue);
						String uname = OPERALib.getUserName();
						String resort = OPERALib.getResort();
						String beginDate=WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
						WSClient.setData("{var_beginDate}",beginDate);
						WSClient.setData("{var_endDate}",beginDate);
						WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
						WSClient.setData("{var_quantity}","1");
						String channel = OWSLib.getChannel();
						String owsResort=OWSLib.getChannelResort(resort, channel);
						WSClient.setData("{var_owsResort}",owsResort);
			            String pwd = OPERALib.getPassword();
			            String channelType = OWSLib.getChannelType(channel);
			            String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			            OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
						
						
						
						String createItemHoldReq = WSClient.createSOAPMessage("OWSCreateItemHold", "DS_07");
				        String createItemHoldRes= WSClient.processSOAPMessage(createItemHoldReq);

				        if(WSAssert.assertIfElementExists(createItemHoldRes, "Result_Text_TextElement", true)) {
							String message=WSAssert.getElementValue(createItemHoldRes, "Result_Text_TextElement", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
							
						}
					    if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", true)) {
								String operaErrorCode=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ operaErrorCode+"</b>");
							}
					    
					    if(WSAssert.assertIfElementExists(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", true)) {
							String GDSErrorCode=WSAssert.getElementValue(createItemHoldRes, "CreateItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is:"+ GDSErrorCode+"</b>");
						}
					
						
						
						// Validation for OWS Operation
						if(WSAssert.assertIfElementExists(createItemHoldRes,"CreateItemHoldResponse_Result_resultStatusFlag", true)) {
							if(WSAssert.assertIfElementValueEquals(createItemHoldRes,"CreateItemHoldResponse_Result_resultStatusFlag", "SUCCESS", false))
						
		                {
		                	/**
		                	 *  Validating the database details 
		                	 */
						    String identifier=WSClient.getElementValue(createItemHoldRes,"CreateItemHoldResponse_ItemHoldID",XMLType.RESPONSE);
			                WSClient.setData("{var_itemHoldId}",identifier);
			                
			                LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
							LinkedHashMap<String,String> actualValues = new LinkedHashMap<String,String>();
							
							String QS_01 = WSClient.getQuery("OWSCreateItemHold","QS_05");
							String itemId = WSClient.getDBRow(QS_01).get("ITEM_ID");
							WSClient.setData("{var_itemId}", itemId);
							expectedValues.put("ITEM_ID",itemId);
							expectedValues.put("QUANTITY", WSClient.getElementValue(createItemHoldReq, "CreateItemHoldRequest_Quantity", XMLType.REQUEST));
							expectedValues.put("BEGIN_DATE", WSClient.getElementValue(createItemHoldReq, "CreateItemHoldRequest_StayDateRange_StartDate", XMLType.REQUEST));
							expectedValues.put("END_DATE", WSClient.getElementValue(createItemHoldReq, "CreateItemHoldRequest_StayDateRange_EndDate", XMLType.REQUEST));
							
						
							
							String query = WSClient.getQuery("QS_04");
         					actualValues = WSClient.getDBRow(query);
							WSAssert.assertEquals(expectedValues,actualValues,false);
							//LinkedHashMap<String,String> HeldById = new LinkedHashMap<String,String>();
							//HeldById=WSClient.getDBRow(WSClient.getQuery("QS_06"));
							//WSClient.setData("{var_resvId}", HeldById.toString());
							
							
		            
					   }  
							else
				        	{
				        	 WSClient.writeToReport(LogStatus.FAIL, "Unable to create Item Hold ");
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
				     //ReleaseItemInventoryHold.releaseItemInventoryHold("DS_01");
					}
		            catch (Exception e) 
					{
		                // TODO Auto-generated catch block
		                e.printStackTrace();
		            }
	  }
			
	}
	
	}


