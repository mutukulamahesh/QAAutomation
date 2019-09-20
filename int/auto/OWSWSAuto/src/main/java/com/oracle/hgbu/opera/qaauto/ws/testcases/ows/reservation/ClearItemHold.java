package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.util.HashMap;
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

public class ClearItemHold extends WSSetUp{
	
	/**
	 * @author ketvaidy
	 */

	String profileID="";
	
	@Test(groups ={ "sanity", "ClearItemHold", "OWS", "Reservation"})
	
	/***** Method to verify that a held item is deleted when required data is passed in the request*****/

	/*****
	 * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Hold Item Inventory
	 * 
	 *****/
	 
		public void clearItemHold_39800() {
	  try
	  {
		  String testName = "clearItemHold_39800";
		  WSClient.startTest(testName,"Verify that a held item is deleted successfully","sanity");
		  if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod","ItemCode"})) {

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
			
			// Prerequisite 1 Create Profile
			if (profileID.equals(""))
				profileID = CreateProfile.createProfile("DS_01");
			if (!profileID.equals("error")) {
				WSClient.setData("{var_profileId}", profileID);
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" +profileID+"</b>");

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
			WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :" + resvID+"</b>");

			WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
			WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
			WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
		    WSClient.setData("{var_count}","1");
		    
			// Prerequisite 3 :Hold Item Inventory 
			
		    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_01");
			String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
			
			  
			if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true)){
				
				
				// OWS Clear Item Hold
				
				WSClient.setData("{var_itemHoldId}",WSClient.getDBRow(WSClient.getQuery("OWSClearItemHold","QS_01")).get("ITEM_HOLD_ID"));
				WSClient.writeToReport(LogStatus.INFO, "<b>Item Hold ID :"+WSClient.getDBRow(WSClient.getQuery("OWSClearItemHold","QS_01")).get("ITEM_HOLD_ID").toString()+"</b>");
				OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
				String clearHoldItemReq = WSClient.createSOAPMessage("OWSClearItemHold","DS_01");
				String clearHoldItemRes = WSClient.processSOAPMessage(clearHoldItemReq);
				if(WSAssert.assertIfElementExists(clearHoldItemRes, "Result_Text_TextElement",true)) {
			          
			          /**** Verifying whether the error Message is populated on the response ****/
			          
			          String message=WSAssert.getElementValue(clearHoldItemRes, "Result_Text_TextElement", XMLType.RESPONSE);
			          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
			      }
				if(WSAssert.assertIfElementExists(clearHoldItemRes, "ClearItemHoldResponse_Result_OperaErrorCode",true)) {
			          
			          /**** Verifying whether the error Message is populated on the response ****/
			          
			          String message=WSAssert.getElementValue(clearHoldItemRes, "ClearItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
			          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
			      }
				if(WSAssert.assertIfElementExists(clearHoldItemRes, "ClearItemHoldResponse_Result_GDSError",true)) {
			          
			          /**** Verifying whether the error Message is populated on the response ****/
			          
			          String message=WSAssert.getElementValue(clearHoldItemRes, "ClearItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
			          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
			      }
				if(WSAssert.assertIfElementExists(clearHoldItemRes, "ClearItemHoldResponse_Result_resultStatusFlag", false)){
					if(WSAssert.assertIfElementValueEquals(clearHoldItemRes, "ClearItemHoldResponse_Result_resultStatusFlag", "SUCCESS",false))
					{
						// DB Validation
						LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_02"));
						if (WSAssert.assertEquals("0", db.get("COUNT"), true))
							WSClient.writeToReport(LogStatus.PASS, "Item hold Cleared successfully");
						else
							WSClient.writeToReport(LogStatus.FAIL, "Item hold cleared unsuccesful");
						
					}
					
				}
							  
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to hold item");
			}
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
				        CancelReservation.cancelReservation("DS_02");
					}
		            catch (Exception e) 
					{
		                // TODO Auto-generated catch block
		                e.printStackTrace();
		            }
	  }
	}
	
	
	@Test(groups ={ "minimumRegression", "ClearItemHold", "OWS", "Reservation"})
	
	/***** Method to verify that an error message is obtained when no hotelCode is passed in the request*****/

	/*****
	 * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Hold Item Inventory
	 * 
	 *****/
	 
	
	public void clearItemHold_40241() {
  try
  {
	  String testName = "clearItemHold_40241";
	  WSClient.startTest(testName,"Verify that an error message is obtained when no hotelCode is passed in the request","minimumRegression");
	  if (OperaPropConfig.getPropertyConfigResults(
				new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod","ItemCode"})) {

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
		
		// Prerequisite 1 Create Profile
		if (profileID.equals(""))
			profileID = CreateProfile.createProfile("DS_01");
		if (!profileID.equals("error")) {
			WSClient.setData("{var_profileId}", profileID);
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" +profileID+"</b>");

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
		WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :"+resvID+"</b>");
		WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
		WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
		WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
	    WSClient.setData("{var_count}","1");
	    
		// Prerequisite 3 : Hold Item Inventory 
		
	    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_01");
		String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
		
		  
		if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true)){
			WSClient.writeToReport(LogStatus.INFO, "<b>Successfully held item"+"</b>");
			
			// OWS Clear Item Hold
			
			WSClient.setData("{var_itemHoldId}",WSClient.getDBRow(WSClient.getQuery("OWSClearItemHold","QS_01")).get("ITEM_HOLD_ID"));
			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
			String clearHoldItemReq = WSClient.createSOAPMessage("OWSClearItemHold","DS_02");
			String clearHoldItemRes = WSClient.processSOAPMessage(clearHoldItemReq);
			if(WSAssert.assertIfElementExists(clearHoldItemRes, "Result_Text_TextElement",true)) {
		          
		          /**** Verifying whether the error Message is populated on the response ****/
		          
		          String message=WSAssert.getElementValue(clearHoldItemRes, "Result_Text_TextElement", XMLType.RESPONSE);
		          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
		      }
			if(WSAssert.assertIfElementExists(clearHoldItemRes, "ClearItemHoldResponse_Result_OperaErrorCode",true)) {
		          
		          /**** Verifying whether the error Message is populated on the response ****/
		          
		          String message=WSAssert.getElementValue(clearHoldItemRes, "ClearItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
		          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
		      }
			if(WSAssert.assertIfElementExists(clearHoldItemRes, "ClearItemHoldResponse_Result_GDSError",true)) {
		          
		          /**** Verifying whether the error Message is populated on the response ****/
		          
		          String message=WSAssert.getElementValue(clearHoldItemRes, "ClearItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
		          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
		      }
			if(WSAssert.assertIfElementExists(clearHoldItemRes, "ClearItemHoldResponse_Result_resultStatusFlag", false)){
				WSAssert.assertIfElementValueEquals(clearHoldItemRes, "ClearItemHoldResponse_Result_resultStatusFlag", "FAIL",false);
				
				
			}
						  
		}
		else
		{
			WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to hold item");
		}
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
			        CancelReservation.cancelReservation("DS_02");
				}
	            catch (Exception e) 
				{
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
  }
}

	@Test(groups ={ "minimumRegression", "ClearItemHold", "OWS", "Reservation"})
	
	/***** Method to verify an error message is obtained when an invalid hotelCode is passed is request*****/

	/*****
	 * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Hold Item Inventory
	 * 
	 *****/
	
	public void clearItemHold_40220() {
  try
  {
	  String testName = "clearItemHold_40220";
	  WSClient.startTest(testName,"Verify that an error message is obtained when an invalid hotelCode is passed is request","minimumRegression");
	  if (OperaPropConfig.getPropertyConfigResults(
				new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod","ItemCode"})) {

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
		
		// Prerequisite 1 Create Profile
		if (profileID.equals(""))
			profileID = CreateProfile.createProfile("DS_01");
		if (!profileID.equals("error")) {
			WSClient.setData("{var_profileId}", profileID);
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" +profileID+"</b>");

			WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
			WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
			WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
			WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
			WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

			// Prerequisite 2 Create Reservation
			HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
			String resvID = resv.get("reservationId");
			WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :" +resvID+"</b>");

			if (!resvID.equals("error")) {

		WSClient.setData("{var_resvId}", resvID);

		WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
		WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
		WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
	    WSClient.setData("{var_count}","1");
	    
		// Prerequisite 3 : Hold Item Inventory
		
	    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_01");
		String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
		  
		if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true)){
			WSClient.writeToReport(LogStatus.INFO, "<b>Successfully held item:"+"</b>");
			
			// OWS Clear Item Hold
			
			WSClient.setData("{var_itemHoldId}",WSClient.getDBRow(WSClient.getQuery("OWSClearItemHold","QS_01")).get("ITEM_HOLD_ID"));
			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
			String clearHoldItemReq = WSClient.createSOAPMessage("OWSClearItemHold","DS_03");
			String clearHoldItemRes = WSClient.processSOAPMessage(clearHoldItemReq);
			if(WSAssert.assertIfElementExists(clearHoldItemRes, "Result_Text_TextElement",true)) {
		          
		          /**** Verifying whether the error Message is populated on the response ****/
		          
		          String message=WSAssert.getElementValue(clearHoldItemRes, "Result_Text_TextElement", XMLType.RESPONSE);
		          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
		      }
			if(WSAssert.assertIfElementExists(clearHoldItemRes, "ClearItemHoldResponse_Result_OperaErrorCode",true)) {
		          
		          /**** Verifying whether the error Message is populated on the response ****/
		          
		          String message=WSAssert.getElementValue(clearHoldItemRes, "ClearItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
		          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
		      }
			if(WSAssert.assertIfElementExists(clearHoldItemRes, "ClearItemHoldResponse_Result_GDSError",true)) {
		          
		          /**** Verifying whether the error Message is populated on the response ****/
		          
		          String message=WSAssert.getElementValue(clearHoldItemRes, "ClearItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
		          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
		      }
			if(WSAssert.assertIfElementExists(clearHoldItemRes, "ClearItemHoldResponse_Result_resultStatusFlag", false)){
				WSAssert.assertIfElementValueEquals(clearHoldItemRes, "ClearItemHoldResponse_Result_resultStatusFlag", "FAIL",false);
				
				
			}
						  
		}
		else
		{
			WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to hold item");
		}
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
			        CancelReservation.cancelReservation("DS_02");
				}
	            catch (Exception e) 
				{
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
  }
}
	
	
	@Test(groups ={ "minimumRegression", "ClearItemHold", "OWS", "Reservation"})
	
	/***** Method to verify an error message is obtained when no ItemHold ID is passed in the request*****/

	/*****
	 * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Hold Item Inventory
	 * 
	 *****/
	
	public void clearItemHold_40242() {
  try
  {
	  String testName = "clearItemHold_40242";
	  WSClient.startTest(testName,"Verify that an error message is obtained when no ItemHold ID is passed in the request","minimumRegression");
	  if (OperaPropConfig.getPropertyConfigResults(
				new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod","ItemCode"})) {

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
		
		// Prerequisite 1 Create Profile
		if (profileID.equals(""))
			profileID = CreateProfile.createProfile("DS_01");
		if (!profileID.equals("error")) {
			WSClient.setData("{var_profileId}", profileID);
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" +profileID+"</b>");

			WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
			WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
			WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
			WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
			WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

			// Prerequisite 2 Create Reservation
			HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
			String resvID = resv.get("reservationId");
			WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :" + resvID+"</b>");

			if (!resvID.equals("error")) {

		WSClient.setData("{var_resvId}", resvID);

		WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
		WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
		WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
	    WSClient.setData("{var_count}","1");
	    
		// Prerequisite 3 : Hold Item Inventory
		
	    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_01");
		String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
		  
		if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true)){
			
			// OWS Clear Item Hold
			
			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
			String clearHoldItemReq = WSClient.createSOAPMessage("OWSClearItemHold","DS_04");
			String clearHoldItemRes = WSClient.processSOAPMessage(clearHoldItemReq);
			if(WSAssert.assertIfElementExists(clearHoldItemRes, "Result_Text_TextElement",true)) {
		          
		          /**** Verifying whether the error Message is populated on the response ****/
		          
		          String message=WSAssert.getElementValue(clearHoldItemRes, "Result_Text_TextElement", XMLType.RESPONSE);
		          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
		      }
			if(WSAssert.assertIfElementExists(clearHoldItemRes, "ClearItemHoldResponse_Result_OperaErrorCode",true)) {
		          
		          /**** Verifying whether the error Message is populated on the response ****/
		          
		          String message=WSAssert.getElementValue(clearHoldItemRes, "ClearItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
		          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
		      }
			if(WSAssert.assertIfElementExists(clearHoldItemRes, "ClearItemHoldResponse_Result_GDSError",true)) {
		          
		          /**** Verifying whether the error Message is populated on the response ****/
		          
		          String message=WSAssert.getElementValue(clearHoldItemRes, "ClearItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
		          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
		      }
			if(WSAssert.assertIfElementExists(clearHoldItemRes, "ClearItemHoldResponse_Result_resultStatusFlag", false)){
				WSAssert.assertIfElementValueEquals(clearHoldItemRes, "ClearItemHoldResponse_Result_resultStatusFlag", "FAIL",false);
				
				
			}
						  
		}
		else
		{
			WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to hold item");
		}
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
			        CancelReservation.cancelReservation("DS_02");
				}
	            catch (Exception e) 
				{
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
  }
}
	
	@Test(groups ={ "minimumRegression", "ClearItemHold", "OWS", "Reservation"})
	
	/***** Method to verify an error message is obtained when an invalid ItemHold ID is passed in the request*****/

	/*****
	 * * * PreRequisites Required: -->Create Profile-->Create a reservation-->Hold Item Inventory
	 * 
	 *****/
	public void clearItemHold_40240() {
  try
  {
	  String testName = "clearItemHold_40240";
	  WSClient.startTest(testName,"Verify that an error message is obtained when an invalid ItemHold ID is passed in the request","minimumRegression");
	  if (OperaPropConfig.getPropertyConfigResults(
				new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod","ItemCode"})) {

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
		
		// Prerequisite 1 Create Profile
		
		if (profileID.equals(""))
			profileID = CreateProfile.createProfile("DS_01");
		if (!profileID.equals("error")) {
			WSClient.setData("{var_profileId}", profileID);
			WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID :" +profileID+"</b>");

			WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
			WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
			WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
			WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
			WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));

			// Prerequisite 2 Create Reservation
			
			HashMap<String, String> resv = CreateReservation.createReservation("DS_04");
			String resvID = resv.get("reservationId");
			WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID :" +resvID+"</b>");

			if (!resvID.equals("error")) {

		WSClient.setData("{var_resvId}", resvID);

		WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
		WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
		WSClient.setData("{var_itemCode}",OperaPropConfig.getDataSetForCode("ItemCode", "DS_01"));
	    WSClient.setData("{var_count}","1");
	    
		// Prerequisite 3 : Hold Item Inventory
		
	    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_01");
		String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
		  
		if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true)){
			WSClient.writeToReport(LogStatus.INFO, "<b>Successfully held Item" +"</b>");
			
			// OWS Clear Item Hold
			
			OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
			WSClient.setData("{var_itemHoldId}", WSClient.getKeywordData("{KEYWORD_RANDSTR_5}"));
			String clearHoldItemReq = WSClient.createSOAPMessage("OWSClearItemHold","DS_01");
			String clearHoldItemRes = WSClient.processSOAPMessage(clearHoldItemReq);
			if(WSAssert.assertIfElementExists(clearHoldItemRes, "Result_Text_TextElement",true)) {
		          
		          /**** Verifying whether the error Message is populated on the response ****/
		          
		          String message=WSAssert.getElementValue(clearHoldItemRes, "Result_Text_TextElement", XMLType.RESPONSE);
		          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
		      }
			if(WSAssert.assertIfElementExists(clearHoldItemRes, "ClearItemHoldResponse_Result_OperaErrorCode",true)) {
		          
		          /**** Verifying whether the error Message is populated on the response ****/
		          
		          String message=WSAssert.getElementValue(clearHoldItemRes, "ClearItemHoldResponse_Result_OperaErrorCode", XMLType.RESPONSE);
		          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
		      }
			if(WSAssert.assertIfElementExists(clearHoldItemRes, "ClearItemHoldResponse_Result_GDSError",true)) {
		          
		          /**** Verifying whether the error Message is populated on the response ****/
		          
		          String message=WSAssert.getElementValue(clearHoldItemRes, "ClearItemHoldResponse_Result_GDSError", XMLType.RESPONSE);
		          WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the Fetch Room Setup response is :"+ message+"</b>");
		      }
			if(WSAssert.assertIfElementExists(clearHoldItemRes, "ClearItemHoldResponse_Result_resultStatusFlag", false)){
				WSAssert.assertIfElementValueEquals(clearHoldItemRes, "ClearItemHoldResponse_Result_resultStatusFlag", "FAIL",false);
				
				
			}
						  
		}
		else
		{
			WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to hold item");
		}
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
