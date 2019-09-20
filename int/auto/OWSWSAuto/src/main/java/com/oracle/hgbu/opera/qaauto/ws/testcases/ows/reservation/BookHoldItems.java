package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

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

public class BookHoldItems extends WSSetUp
{
	String profileID="";
	
	//Setting OWS Header
	public void setOWSHeader() 
	{
		try 
		{
			String resort = OPERALib.getResort();
			String uname = OPERALib.getUserName();
			String channel = OWSLib.getChannel();
		    String pwd = OPERALib.getPassword();
		    String channelType = OWSLib.getChannelType(channel);
		    String channelCarrier = OWSLib.getChannelCarier(resort, channel);
		    OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
		}
		catch(Exception e) 
		{
			WSClient.writeToReport(LogStatus.ERROR, "OWS Header not set.");
		}
	}
	
	
	@Test(groups ={ "sanity", "BookHoldItems", "OWS", "Reservation"})
	public void bookHoldItems_39760()
	{
		try
		{
			String testName = "bookHoldItems_39760";
			WSClient.startTest(testName,"Verify that held inventory item is attached to the Reservation when both Reservation ID and Confirmation No. is passed in the request","sanity");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod", "ItemCode"})) 
			{
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);
				
				String uname = OPERALib.getUserName();
				//String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				//String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				//String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				
				OPERALib.setOperaHeader(uname);
				
				//Prerequisite 1	:	Creating a Profile
				if (profileID.equals(""))
				{
					profileID = CreateProfile.createProfile("DS_01");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile ID: " + profileID+"</b>");
				}
				if (!profileID.equals("error")) 
				{
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
					
					//Prerequisite 2	:	Creating a Reservation
					HashMap<String, String> id = CreateReservation.createReservation("DS_23");
					String resvID = id.get("reservationId");
					String confNo = id.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Reservation ID: " + resvID+"</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Confirmation Number: " + confNo+"</b>");
					
					if (!resvID.equals("error")) 
					{
						String heldById = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
						System.out.println("HeldByID: "+heldById);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_confNo}", confNo);
						WSClient.setData("{var_heldById}", heldById);
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_02"));
						WSClient.setData("{var_count}", "1");
					    
						// Prerequisite 3 : Create Item Hold 
					    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_02");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
						  
						if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true))
						{
							if(WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_HeldById_ID", false))
							{
								//Getting itemHoldId based on the heldById 
								String QS_01 = WSClient.getQuery("OWSBookHoldItems","QS_01");
								String itemHoldId = WSClient.getDBRow(QS_01).get("ITEM_HOLD_ID");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Item Hold ID: " + itemHoldId+"</b>");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully Held the item"+"</b>");
								System.out.println("itemHoldid: "+itemHoldId);
								WSClient.setData("{var_itemHoldId}", itemHoldId);
								
								//Fetching itemId based on the itemHoldId
								String QS_02 = WSClient.getQuery("OWSBookHoldItems", "QS_02");
								String expectedItemId = WSClient.getDBRow(QS_02).get("ITEM_ID");
								
								// OWS BookHoldItems
								setOWSHeader();
								//OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								WSClient.setData("{var_oresort}", resort);
								String bookHoldItemsReq = WSClient.createSOAPMessage("OWSBookHoldItems","DS_01");
								String bookHoldItemsRes = WSClient.processSOAPMessage(bookHoldItemsReq);
								
								//Checking for Text Element in Result
								if (WSAssert.assertIfElementExists(bookHoldItemsRes, "Result_Text_TextElement", true)) 
								{

									String message = WSAssert.getElementValue(bookHoldItemsRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
								}
								
								//Checking For OperaErrorCode
								if (WSAssert.assertIfElementExists(bookHoldItemsRes,"BookHoldItemsResponse_Result_OperaErrorCode", true)) 
								{
									String code = WSAssert.getElementValue(bookHoldItemsRes, "BookHoldItemsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error code displayed in the response is :" + code+"</b>");
								}
								
								//Checking For Fault Schema
								if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_faultcode", true))
								{
									String message=WSClient.getElementValue(bookHoldItemsRes,"BookHoldItemsResponse_faultcode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "The Response has Fault Schema with message: "+message);
								}
								
								if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_Result_resultStatusFlag", false))
								{
									if(WSAssert.assertIfElementValueEquals(bookHoldItemsRes, "BookHoldItemsResponse_Result_resultStatusFlag", "SUCCESS",false))
									{
										//Validating if the above expectedItemId is getting attached to the Reservation or not
										String actualItemId = WSClient.getDBRow(WSClient.getQuery("QS_03")).get("ITEM_ID");
										if(WSAssert.assertEquals(expectedItemId, actualItemId, true))
										{
											WSClient.writeToReport(LogStatus.PASS, "Expected Item ID: "+expectedItemId+"   Actual Item ID: "+actualItemId);
										}
										else
										{
											WSClient.writeToReport(LogStatus.FAIL, "Expected Item ID: "+expectedItemId+"   Actual Item ID: "+actualItemId);
										}
									}
									else
									{
										WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag should be SUCCESS not FAIL");
										//Checking for GDSError
										if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError",true)) 
										{
											String message=WSAssert.getElementValue(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError", XMLType.RESPONSE);
											
											if((!message.equals("")))
											{
												WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error displayed in the Release Room response is :"+ message+"</b>");
											}
										}
									}
									
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
	
	
	@Test(groups ={ "minimumRegression", "BookHoldItems", "OWS", "Reservation"})
	public void bookHoldItems_40060()
	{
		try
		{
			String testName = "bookHoldItems_40060";
			WSClient.startTest(testName,"Verify if the held inventory item is attached to the Reservation when only Reservation ID is passed in the request","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod", "ItemCode"})) 
			{
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);
				
				String uname = OPERALib.getUserName();
				//String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				//String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				//String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				
				OPERALib.setOperaHeader(uname);
				
				//Prerequisite 1	:	Creating a Profile
				if (profileID.equals(""))
				{
					profileID = CreateProfile.createProfile("DS_01");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile ID: " + profileID+"</b>");
				}
				if (!profileID.equals("error")) 
				{
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
					
					//Prerequisite 2	:	Creating a Reservation
					HashMap<String, String> id = CreateReservation.createReservation("DS_23");
					String resvID = id.get("reservationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Reservation ID: " + resvID+"</b>");
					
					if (!resvID.equals("error")) 
					{
						String heldById = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
						System.out.println("HeldByID: "+heldById);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_heldById}", heldById);
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_02"));
						WSClient.setData("{var_count}", "1");
					    
						// Prerequisite 3 : Create Item Hold 
					    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_02");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
						  
						if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true))
						{
							if(WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_HeldById_ID", false))
							{
								//Getting itemHoldId based on the heldById 
								String QS_01 = WSClient.getQuery("OWSBookHoldItems","QS_01");
								String itemHoldId = WSClient.getDBRow(QS_01).get("ITEM_HOLD_ID");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Item Hold ID: " + itemHoldId+"</b>");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully Held the item"+"</b>");
								System.out.println("itemHoldid: "+itemHoldId);
								WSClient.setData("{var_itemHoldId}", itemHoldId);
								
								//Fetching itemId based on the itemHoldId
								String QS_02 = WSClient.getQuery("OWSBookHoldItems", "QS_02");
								String expectedItemId = WSClient.getDBRow(QS_02).get("ITEM_ID");
								
								// OWS BookHoldItems
								setOWSHeader();
								//OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								WSClient.setData("{var_oresort}", resort);
								String bookHoldItemsReq = WSClient.createSOAPMessage("OWSBookHoldItems","DS_02");
								String bookHoldItemsRes = WSClient.processSOAPMessage(bookHoldItemsReq);
								
								//Checking for Text Element in Result
								if (WSAssert.assertIfElementExists(bookHoldItemsRes, "Result_Text_TextElement", true)) 
								{

									String message = WSAssert.getElementValue(bookHoldItemsRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
								}
								
								//Checking For OperaErrorCode
								if (WSAssert.assertIfElementExists(bookHoldItemsRes,"BookHoldItemsResponse_Result_OperaErrorCode", true)) 
								{
									String code = WSAssert.getElementValue(bookHoldItemsRes, "BookHoldItemsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error code displayed in the response is :" + code+"</b>");
								}
								
								//Checking For Fault Schema
								if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_faultcode", true))
								{
									String message=WSClient.getElementValue(bookHoldItemsRes,"BookHoldItemsResponse_faultcode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "The Response has Fault Schema with message: "+message);
								}
								
								if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_Result_resultStatusFlag", false))
								{
									if(WSAssert.assertIfElementValueEquals(bookHoldItemsRes, "BookHoldItemsResponse_Result_resultStatusFlag", "SUCCESS",false))
									{
										//Validating if the above expectedItemId is getting attached to the Reservation or not
										String actualItemId = WSClient.getDBRow(WSClient.getQuery("QS_03")).get("ITEM_ID");
										if(WSAssert.assertEquals(expectedItemId, actualItemId, true))
										{
											WSClient.writeToReport(LogStatus.PASS, "Expected Item ID: "+expectedItemId+"   Actual Item ID: "+actualItemId);
										}
										else
										{
											WSClient.writeToReport(LogStatus.FAIL, "Expected Item ID: "+expectedItemId+"   Actual Item ID: "+actualItemId);
										}
									}
									else
									{
										WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag should be SUCCESS not FAIL");
										//Checking for GDSError
										if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError",true)) 
										{
											String message=WSAssert.getElementValue(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError", XMLType.RESPONSE);
											
											if((!message.equals("")))
											{
												WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error displayed in the Release Room response is :"+ message+"</b>");
											}
										}
									}
									
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
	
	
	@Test(groups ={ "minimumRegression", "BookHoldItems", "OWS", "Reservation"})
	public void bookHoldItems_40061()
	{
		try
		{
			String testName = "bookHoldItems_40061";
			WSClient.startTest(testName,"Verify if the held inventory item is attached to the Reservation when only Confirmation No. is passed in the request","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod", "ItemCode"})) 
			{
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);
				
				String uname = OPERALib.getUserName();
				//String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				//String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				//String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				
				OPERALib.setOperaHeader(uname);
				
				//Prerequisite 1	:	Creating a Profile
				if (profileID.equals(""))
				{
					profileID = CreateProfile.createProfile("DS_01");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile ID: " + profileID+"</b>");
				}
				if (!profileID.equals("error")) 
				{
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
					
					//Prerequisite 2	:	Creating a Reservation
					HashMap<String, String> id = CreateReservation.createReservation("DS_23");
					String resvID = id.get("reservationId");
					String confNo = id.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Reservation ID: " + resvID+"</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Confirmation Number: " + confNo+"</b>");
					
					if (!resvID.equals("error")) 
					{
						String heldById = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
						System.out.println("HeldByID: "+heldById);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_confNo}", confNo);
						WSClient.setData("{var_heldById}", heldById);
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_02"));
						WSClient.setData("{var_count}", "1");
					    
						// Prerequisite 3 : Create Item Hold 
					    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_02");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
						  
						if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true))
						{
							if(WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_HeldById_ID", false))
							{
								//Getting itemHoldId based on the heldById 
								String QS_01 = WSClient.getQuery("OWSBookHoldItems","QS_01");
								String itemHoldId = WSClient.getDBRow(QS_01).get("ITEM_HOLD_ID");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Item Hold ID: " + itemHoldId+"</b>");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully Held the item"+"</b>");
								System.out.println("itemHoldid: "+itemHoldId);
								WSClient.setData("{var_itemHoldId}", itemHoldId);
								
								//Fetching itemId based on the itemHoldId
								String QS_02 = WSClient.getQuery("OWSBookHoldItems", "QS_02");
								String expectedItemId = WSClient.getDBRow(QS_02).get("ITEM_ID");
								
								// OWS BookHoldItems
								setOWSHeader();
								//OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								WSClient.setData("{var_oresort}", resort);
								String bookHoldItemsReq = WSClient.createSOAPMessage("OWSBookHoldItems","DS_03");
								String bookHoldItemsRes = WSClient.processSOAPMessage(bookHoldItemsReq);
								
								//Checking for Text Element in Result
								if (WSAssert.assertIfElementExists(bookHoldItemsRes, "Result_Text_TextElement", true)) 
								{

									String message = WSAssert.getElementValue(bookHoldItemsRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
								}
								
								//Checking For OperaErrorCode
								if (WSAssert.assertIfElementExists(bookHoldItemsRes,"BookHoldItemsResponse_Result_OperaErrorCode", true)) 
								{
									String code = WSAssert.getElementValue(bookHoldItemsRes, "BookHoldItemsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error code displayed in the response is :" + code+"</b>");
								}
								
								//Checking For Fault Schema
								if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_faultcode", true))
								{
									String message=WSClient.getElementValue(bookHoldItemsRes,"BookHoldItemsResponse_faultcode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "The Response has Fault Schema with message: "+message);
								}
								
								if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_Result_resultStatusFlag", false))
								{
									if(WSAssert.assertIfElementValueEquals(bookHoldItemsRes, "BookHoldItemsResponse_Result_resultStatusFlag", "SUCCESS",false))
									{
										//Validating if the above expectedItemId is getting attached to the Reservation or not
										String actualItemId = WSClient.getDBRow(WSClient.getQuery("QS_03")).get("ITEM_ID");
										if(WSAssert.assertEquals(expectedItemId, actualItemId, true))
										{
											WSClient.writeToReport(LogStatus.PASS, "Expected Item ID: "+expectedItemId+"   Actual Item ID: "+actualItemId);
										}
										else
										{
											WSClient.writeToReport(LogStatus.FAIL, "Expected Item ID: "+expectedItemId+"   Actual Item ID: "+actualItemId);
										}
									}
									else
									{
										WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag should be SUCCESS not FAIL");
										//Checking for GDSError
										if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError",true)) 
										{
											String message=WSAssert.getElementValue(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError", XMLType.RESPONSE);
											
											if((!message.equals("")))
											{
												WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error displayed in the Release Room response is :"+ message+"</b>");
											}
										}
									}
									
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
	
	
	@Test(groups ={ "minimumRegression", "BookHoldItems", "OWS", "Reservation"})
	public void bookHoldItems_40062()
	{
		try
		{
			String testName = "bookHoldItems_40062";
			WSClient.startTest(testName,"Verify that multiple held inventory items are attached to the Reservation","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod", "ItemCode"})) 
			{
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);
				
				String uname = OPERALib.getUserName();
				//String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				//String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				//String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				
				OPERALib.setOperaHeader(uname);
				
				//Prerequisite 1	:	Creating a Profile
				if (profileID.equals(""))
				{
					profileID = CreateProfile.createProfile("DS_01");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile ID: " + profileID+"</b>");
				}
				if (!profileID.equals("error")) 
				{
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
					
					//Prerequisite 2	:	Creating a Reservation
					HashMap<String, String> id = CreateReservation.createReservation("DS_23");
					String resvID = id.get("reservationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Reservation ID: " + resvID+"</b>");
					
					if (!resvID.equals("error")) 
					{
						String heldById1 = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
						String heldById2 = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
						String itemCode1 = OperaPropConfig.getDataSetForCode("ItemCode", "DS_01");
						String itemCode2 = OperaPropConfig.getDataSetForCode("ItemCode", "DS_02");
						System.out.println("HeldByID: "+heldById1 +" and "+ heldById2);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_heldById}", heldById1);
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", itemCode1);
						WSClient.setData("{var_count}", "1");
					    
						// Prerequisite 3 : Create Item Hold for 1st Item Code
					    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_02");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
						  
						if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true))
						{
							if(WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_HeldById_ID", false))
							{
								//Getting itemHoldId based on the heldById 
								String QS_01 = WSClient.getQuery("OWSBookHoldItems","QS_01");
								String itemHoldId1 = WSClient.getDBRow(QS_01).get("ITEM_HOLD_ID");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"First Item Hold ID: " + itemHoldId1+"</b>");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully Held the 1st item"+"</b>");
								System.out.println("itemHoldid 1: "+itemHoldId1);
								WSClient.setData("{var_itemHoldId}", itemHoldId1);
								
								//Fetching itemId 1 based on the itemHoldId 1
								String QS_02 = WSClient.getQuery("OWSBookHoldItems", "QS_02");
								String expectedItemId1 = WSClient.getDBRow(QS_02).get("ITEM_ID");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"First Item ID: " + expectedItemId1+"</b>");
								
								// Prerequisite 3 : Create Item Hold for 2st Item Code
								WSClient.setData("{var_heldById}", heldById2);
								WSClient.setData("{var_itemCode}", itemCode2);
								String holdItemInvReq = WSClient.createSOAPMessage("HoldItemInventory","DS_02");
								String holdItemInvRes = WSClient.processSOAPMessage(holdItemInvReq);
								
								if(WSAssert.assertIfElementExists(holdItemInvRes,"HoldItemInventoryRS_Success",true))
								{
									if(WSAssert.assertIfElementExists(holdItemInvRes, "HoldItemInventoryRS_HeldById_ID", false))
									{
										//Getting itemHoldId based on the heldById 
										String QS01 = WSClient.getQuery("OWSBookHoldItems","QS_01");
										String itemHoldId2 = WSClient.getDBRow(QS01).get("ITEM_HOLD_ID");
										WSClient.writeToReport(LogStatus.INFO, "<b>"+"Second Item Hold ID: " + itemHoldId2+"</b>");
										WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully Held the 2nd item"+"</b>");
										System.out.println("itemHoldid 2: "+itemHoldId2);
										WSClient.setData("{var_itemHoldId}", itemHoldId2);
										
										//Fetching itemId 2 based on the itemHoldId 2
										String QS02 = WSClient.getQuery("OWSBookHoldItems", "QS_02");
										String expectedItemId2 = WSClient.getDBRow(QS02).get("ITEM_ID");
										WSClient.writeToReport(LogStatus.INFO, "<b>"+"Second Item ID: " + expectedItemId2+"</b>");
										
										// OWS BookHoldItems
										WSClient.setData("{var_itemHoldId1}", itemHoldId1);
										WSClient.setData("{var_itemHoldId2}", itemHoldId2);
										setOWSHeader();
										//OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
										WSClient.setData("{var_oresort}", resort);
										String bookHoldItemsReq = WSClient.createSOAPMessage("OWSBookHoldItems","DS_04");
										String bookHoldItemsRes = WSClient.processSOAPMessage(bookHoldItemsReq);
										
										//Checking for Text Element in Result
										if (WSAssert.assertIfElementExists(bookHoldItemsRes, "Result_Text_TextElement", true)) 
										{

											String message = WSAssert.getElementValue(bookHoldItemsRes, "Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
										}
										
										//Checking For OperaErrorCode
										if (WSAssert.assertIfElementExists(bookHoldItemsRes,"BookHoldItemsResponse_Result_OperaErrorCode", true)) 
										{
											String code = WSAssert.getElementValue(bookHoldItemsRes, "BookHoldItemsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error code displayed in the response is :" + code+"</b>");
										}
										
										//Checking For Fault Schema
										if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_faultcode", true))
										{
											String message=WSClient.getElementValue(bookHoldItemsRes,"BookHoldItemsResponse_faultcode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.FAIL, "The Response has Fault Schema with message: "+message);
										}
										
										
										if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_Result_resultStatusFlag", false))
										{
											if(WSAssert.assertIfElementValueEquals(bookHoldItemsRes, "BookHoldItemsResponse_Result_resultStatusFlag", "SUCCESS",false))
											{
												//Adding expectedItemIds in a Single ListofHashMap 
												List<LinkedHashMap<String, String>> expectedItemIdList = new ArrayList<LinkedHashMap<String, String>>();
												LinkedHashMap<String,  String> expectedItemIdHashMap1 = new LinkedHashMap<String, String>();
												LinkedHashMap<String,  String> expectedItemIdHashMap2 = new LinkedHashMap<String, String>();
												expectedItemIdHashMap1.put("ITEM_ID", expectedItemId1);
												expectedItemIdList.add(expectedItemIdHashMap1);
												expectedItemIdHashMap2.put("ITEM_ID", expectedItemId2);
												expectedItemIdList.add(expectedItemIdHashMap2);
												
												//Validating if the above expectedItemId is getting attached to the Reservation or not
												List<LinkedHashMap<String, String>>actualItemIdList = WSClient.getDBRows(WSClient.getQuery("QS_03"));
												
												//Checking whether the expected item ID is same as the actual item ID populated in DB
												if(WSAssert.assertEquals(expectedItemIdList, actualItemIdList, true))
												{
													WSClient.writeToReport(LogStatus.PASS, "Expected Item IDs and Actual Item IDs are Same");
												}
												else
												{
													WSClient.writeToReport(LogStatus.FAIL, "Expected Item IDs and Actual Item IDs are not Same");
												}
											}
											else
											{
												WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag should be SUCCESS not FAIL");
												//Checking for GDSError
												if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError",true)) 
												{
													String message=WSAssert.getElementValue(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError", XMLType.RESPONSE);
													
													if((!message.equals("")))
													{
														WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error displayed in the Release Room response is :"+ message+"</b>");
													}
												}
											}
											
										}
									}
								}
								else
								{
									WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to hold Second item");
								}
							}
						}
						else
						{
							WSClient.writeToReport(LogStatus.WARNING,"Blocked : Unable to hold First item");
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
	
	
	@Test(groups ={ "minimumRegression", "BookHoldItems", "OWS", "Reservation"})
	public void bookHoldItems_40063()
	{
		try
		{
			String testName = "bookHoldItems_40063";
			WSClient.startTest(testName,"Verify that the held inventory item is NOT attached to the Reservation when an INVALID Item Hold Id is passed in the request","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod", "ItemCode"})) 
			{
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);
				
				String uname = OPERALib.getUserName();
				//String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				//String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				//String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				
				OPERALib.setOperaHeader(uname);
				
				//Prerequisite 1	:	Creating a Profile
				if (profileID.equals(""))
				{
					profileID = CreateProfile.createProfile("DS_01");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile ID: " + profileID+"</b>");
				}
				if (!profileID.equals("error")) 
				{
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
					
					//Prerequisite 2	:	Creating a Reservation
					HashMap<String, String> id = CreateReservation.createReservation("DS_23");
					String resvID = id.get("reservationId");
					String confNo = id.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Reservation ID: " + resvID+"</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Confirmation Number: " + confNo+"</b>");
					
					if (!resvID.equals("error")) 
					{
						String heldById = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
						System.out.println("HeldByID: "+heldById);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_confNo}", confNo);
						WSClient.setData("{var_heldById}", heldById);
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_02"));
						WSClient.setData("{var_count}", "1");
					    
						// Prerequisite 3 : Create Item Hold 
					    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_02");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
						  
						if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true))
						{
							if(WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_HeldById_ID", false))
							{
								//Getting an INVALID itemHoldId
								String itemHoldId = WSClient.getKeywordData("{KEYWORD_RANDNUM_8}");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Item Hold ID: " + itemHoldId+"</b>");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully Held the item"+"</b>");
								WSClient.setData("{var_itemHoldId}", itemHoldId);
								
								// OWS BookHoldItems
								setOWSHeader();
								//OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								WSClient.setData("{var_oresort}", resort);
								String bookHoldItemsReq = WSClient.createSOAPMessage("OWSBookHoldItems","DS_03");
								String bookHoldItemsRes = WSClient.processSOAPMessage(bookHoldItemsReq);
								
								//Checking for Text Element in Result
								if (WSAssert.assertIfElementExists(bookHoldItemsRes, "Result_Text_TextElement", true)) 
								{

									String message = WSAssert.getElementValue(bookHoldItemsRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
								}
								
								//Checking For OperaErrorCode
								if (WSAssert.assertIfElementExists(bookHoldItemsRes,"BookHoldItemsResponse_Result_OperaErrorCode", true)) 
								{
									String code = WSAssert.getElementValue(bookHoldItemsRes, "BookHoldItemsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error code displayed in the response is :" + code+"</b>");
								}
								
								//Checking For Fault Schema
								if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_faultcode", true))
								{
									String message=WSClient.getElementValue(bookHoldItemsRes,"BookHoldItemsResponse_faultcode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "The Response has Fault Schema with message: "+message);
								}
								
								
								if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_Result_resultStatusFlag", false))
								{
									if(WSAssert.assertIfElementValueEquals(bookHoldItemsRes, "BookHoldItemsResponse_Result_resultStatusFlag", "FAIL",false))
									{
										//When the result status flag is populated as FAIL then passing the test case if the ERROR message is same as given below.
										
										//Checking for GDSError
										//In this Case of MR GDSError will contain the Message of what actually the Error is i.e. that INVALID ITEM HOLD ID
										//Here, It is not the criterion for failing the Test Case so writing report as the INFO and not as FAIL
										if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError",true)) 
										{
											String message=WSAssert.getElementValue(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError", XMLType.RESPONSE);
																				
											if((WSAssert.assertIfElementValueEquals(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError", "INVALID ITEM HOLD ID "+itemHoldId, true)))
											{
												WSClient.writeToReport(LogStatus.PASS, "Expected :" + "INVALID ITEM HOLD ID "+itemHoldId + " Actual :" + message);
											}
											else 
											{
												WSClient.writeToReport(LogStatus.FAIL, "Expected :" + "INVALID ITEM HOLD ID "+itemHoldId + " Actual :" + message);
											}
											
										}
										else
										{
											WSClient.writeToReport(LogStatus.FAIL, "<b>"+"The GDSError is missing in the Response"+"</b>");
										}

										
									}
									else
									{
										//When the result status flag is populated as SUCCESS then failing the test case.
										WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag should be FAIL not SUCCESS");
										
									}
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
	
	
	@Test(groups ={ "minimumRegression", "BookHoldItems", "OWS", "Reservation"})
	public void bookHoldItems_41142()
	{
		try
		{
			String testName = "bookHoldItems_41142";
			WSClient.startTest(testName,"Verify that the held inventory item is NOT attached to the Reservation when an INVALID Reservation Id is passed in the request","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod", "ItemCode"})) 
			{
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);
				
				String uname = OPERALib.getUserName();
				//String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				//String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				//String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				
				OPERALib.setOperaHeader(uname);
				
				//Prerequisite 1	:	Creating a Profile
				if (profileID.equals(""))
				{
					profileID = CreateProfile.createProfile("DS_01");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile ID: " + profileID+"</b>");
				}
				if (!profileID.equals("error")) 
				{
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
					
					//Prerequisite 2	:	Invalid Reservation
					String resvID = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Reservation ID: " + resvID+"</b>");
					
					if (!resvID.equals("error")) 
					{
						String heldById = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
						System.out.println("HeldByID: "+heldById);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_heldById}", heldById);
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_02"));
						WSClient.setData("{var_count}", "1");
					    
						// Prerequisite 3 : Create Item Hold 
					    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_02");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
						  
						if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true))
						{
							if(WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_HeldById_ID", false))
							{
								//Getting itemHoldId
								String QS_01 = WSClient.getQuery("OWSBookHoldItems","QS_01");
								String itemHoldId = WSClient.getDBRow(QS_01).get("ITEM_HOLD_ID");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Item Hold ID: " + itemHoldId+"</b>");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully Held the item"+"</b>");
								WSClient.setData("{var_itemHoldId}", itemHoldId);
								
								// OWS BookHoldItems
								setOWSHeader();
								//OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								WSClient.setData("{var_oresort}", resort);
								String bookHoldItemsReq = WSClient.createSOAPMessage("OWSBookHoldItems","DS_02");
								String bookHoldItemsRes = WSClient.processSOAPMessage(bookHoldItemsReq);
								
								//Checking for Text Element in Result
								if (WSAssert.assertIfElementExists(bookHoldItemsRes, "Result_Text_TextElement", true)) 
								{

									String message = WSAssert.getElementValue(bookHoldItemsRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
								}
								
								//Checking For OperaErrorCode
								if (WSAssert.assertIfElementExists(bookHoldItemsRes,"BookHoldItemsResponse_Result_OperaErrorCode", true)) 
								{
									String code = WSAssert.getElementValue(bookHoldItemsRes, "BookHoldItemsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error code displayed in the response is :" + code+"</b>");
								}
								
								//Checking For Fault Schema
								if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_faultcode", true))
								{
									String message=WSClient.getElementValue(bookHoldItemsRes,"BookHoldItemsResponse_faultcode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "The Response has Fault Schema with message: "+message);
								}
								
								
								if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_Result_resultStatusFlag", false))
								{
									if(WSAssert.assertIfElementValueEquals(bookHoldItemsRes, "BookHoldItemsResponse_Result_resultStatusFlag", "FAIL",false))
									{
										//When the result status flag is populated as FAIL then passing the test case if the ERROR message is same as given below.
										
										//Checking for GDSError
										//In this Case of MR GDSError will contain the Message of what actually the Error is i.e. that INVALID ITEM HOLD ID
										//Here, It is not the criterion for failing the Test Case so writing report as the INFO and not as FAIL
										if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError",true)) 
										{
											String message=WSAssert.getElementValue(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError", XMLType.RESPONSE);
																				
											if((WSAssert.assertIfElementValueEquals(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError", "BOOKING NOT FOUND ", true)))
											{
												WSClient.writeToReport(LogStatus.PASS, "Expected :" + "BOOKING NOT FOUND" + " Actual :" + message);
											}
											else 
											{
												WSClient.writeToReport(LogStatus.FAIL, "Expected :" + "BOOKING NOT FOUND" + " Actual :" + message);
											}
										}
										else
										{
											WSClient.writeToReport(LogStatus.FAIL, "<b>"+"The GDSError is missing in the Response"+"</b>");
										}

										
									}
									else
									{
										//When the result status flag is populated as SUCCESS then failing the test case.
										WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag should be FAIL not SUCCESS");
										
									}
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
	
	
	@Test(groups ={ "minimumRegression", "BookHoldItems", "OWS", "Reservation"})
	public void bookHoldItems_41162()
	{
		try
		{
			String testName = "bookHoldItems_41162";
			WSClient.startTest(testName,"Verify that the held inventory item is NOT attached to the Reservation when an INVALID Confirmation Id is passed in the request","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod", "ItemCode"})) 
			{
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);
				
				String uname = OPERALib.getUserName();
				//String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				//String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				//String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				
				OPERALib.setOperaHeader(uname);
				
				//Prerequisite 1	:	Creating a Profile
				if (profileID.equals(""))
				{
					profileID = CreateProfile.createProfile("DS_01");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile ID: " + profileID+"</b>");
				}
				if (!profileID.equals("error")) 
				{
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
					
					//Prerequisite 2	:	Invalid Reservation
					String resvID = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
					String confNo = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Reservation ID: " + resvID+"</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Confirmation Number: " + confNo+"</b>");
					
					if (!resvID.equals("error")) 
					{
						String heldById = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
						System.out.println("HeldByID: "+heldById);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_confNo}", confNo);
						WSClient.setData("{var_heldById}", heldById);
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_02"));
						WSClient.setData("{var_count}", "1");
					    
						// Prerequisite 3 : Create Item Hold 
					    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_02");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
						  
						if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true))
						{
							if(WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_HeldById_ID", false))
							{
								//Getting itemHoldId
								String QS_01 = WSClient.getQuery("OWSBookHoldItems","QS_01");
								String itemHoldId = WSClient.getDBRow(QS_01).get("ITEM_HOLD_ID");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Item Hold ID: " + itemHoldId+"</b>");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully Held the item"+"</b>");
								WSClient.setData("{var_itemHoldId}", itemHoldId);
								
								// OWS BookHoldItems
								setOWSHeader();
								//OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								WSClient.setData("{var_oresort}", resort);
								String bookHoldItemsReq = WSClient.createSOAPMessage("OWSBookHoldItems","DS_03");
								String bookHoldItemsRes = WSClient.processSOAPMessage(bookHoldItemsReq);
								
								//Checking for Text Element in Result
								if (WSAssert.assertIfElementExists(bookHoldItemsRes, "Result_Text_TextElement", true)) 
								{

									String message = WSAssert.getElementValue(bookHoldItemsRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
								}
								
								//Checking For OperaErrorCode
								if (WSAssert.assertIfElementExists(bookHoldItemsRes,"BookHoldItemsResponse_Result_OperaErrorCode", true)) 
								{
									String code = WSAssert.getElementValue(bookHoldItemsRes, "BookHoldItemsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error code displayed in the response is :" + code+"</b>");
								}
								
								//Checking For Fault Schema
								if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_faultcode", true))
								{
									String message=WSClient.getElementValue(bookHoldItemsRes,"BookHoldItemsResponse_faultcode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "The Response has Fault Schema with message: "+message);
								}
								
								
								if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_Result_resultStatusFlag", false))
								{
									if(WSAssert.assertIfElementValueEquals(bookHoldItemsRes, "BookHoldItemsResponse_Result_resultStatusFlag", "FAIL",false))
									{
										//When the result status flag is populated as FAIL then passing the test case if the ERROR message is same as given below.
										
										//Checking for GDSError
										//In this Case of MR GDSError will contain the Message of what actually the Error is i.e. that INVALID ITEM HOLD ID
										//Here, It is not the criterion for failing the Test Case so writing report as the INFO and not as FAIL
										if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError",true)) 
										{
											String message=WSAssert.getElementValue(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError", XMLType.RESPONSE);
																				
											if((WSAssert.assertIfElementValueEquals(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError", "BOOKING NOT FOUND ", true)))
											{
												WSClient.writeToReport(LogStatus.PASS, "Expected :" + "BOOKING NOT FOUND" + " Actual :" + message);
											}
											else 
											{
												WSClient.writeToReport(LogStatus.FAIL, "Expected :" + "BOOKING NOT FOUND" + " Actual :" + message);
											}
										}
										else
										{
											WSClient.writeToReport(LogStatus.FAIL, "<b>"+"The GDSError is missing in the Response"+"</b>");
										}

										
									}
									else
									{
										//When the result status flag is populated as SUCCESS then failing the test case.
										WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag should be FAIL not SUCCESS");
										
									}
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
	
	
	@Test(groups ={ "minimumRegression", "BookHoldItems", "OWS", "Reservation"})
	public void bookHoldItems_41322()
	{
		try
		{
			String testName = "bookHoldItems_41322";
			WSClient.startTest(testName,"Verify that held inventory item is attached to the Reservation when Reservation ID, Confirmation No. and Leg No. is passed in the request","minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod", "ItemCode"})) 
			{
				String resortOperaValue = OPERALib.getResort();
				String chain = OPERALib.getChain();
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_resort}", resortOperaValue);
				
				String uname = OPERALib.getUserName();
				//String pwd = OPERALib.getPassword();
				String channel = OWSLib.getChannel();
				//String channelType = OWSLib.getChannelType(channel);
				String resort = OWSLib.getChannelResort(resortOperaValue, channel);
				WSClient.setData("{var_owsresort}", resort);
				//String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				
				OPERALib.setOperaHeader(uname);
				
				//Prerequisite 1	:	Creating a Profile
				if (profileID.equals(""))
				{
					profileID = CreateProfile.createProfile("DS_01");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Profile ID: " + profileID+"</b>");
				}
				if (!profileID.equals("error")) 
				{
					WSClient.setData("{var_profileId}", profileID);
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
					WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
					
					//Prerequisite 2	:	Creating a Reservation
					HashMap<String, String> id = CreateReservation.createReservation("DS_23");
					String resvID = id.get("reservationId");
					String confNo = id.get("confirmationId");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Reservation ID: " + resvID+"</b>");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Confirmation Number: " + confNo+"</b>");
					WSClient.setData("{var_resvId}", resvID);
					WSClient.setData("{var_confNo}", confNo);
					
					
					//Fetching Confirmation Leg Number from DB
					String queryForLegNo = WSClient.getQuery("CreateReservation", "QS_01");
					String legNo = WSClient.getDBRow(queryForLegNo).get("CONFIRMATION_LEG_NO");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Leg Number: " + legNo+"</b>");
					WSClient.setData("{var_legNo}", legNo);
					
					if (!resvID.equals("error")) 
					{
						String heldById = WSClient.getKeywordData("{KEYWORD_RANDNUM_10}");
						System.out.println("HeldByID: "+heldById);
						WSClient.setData("{var_heldById}", heldById);
						WSClient.setData("{var_startDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_endDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_itemCode}", OperaPropConfig.getDataSetForCode("ItemCode", "DS_02"));
						WSClient.setData("{var_count}", "1");
					    
						// Prerequisite 3 : Create Item Hold 
					    String holdItemInventoryReq = WSClient.createSOAPMessage("HoldItemInventory","DS_02");
						String holdItemInventoryRes = WSClient.processSOAPMessage(holdItemInventoryReq);
						  
						if(WSAssert.assertIfElementExists(holdItemInventoryRes,"HoldItemInventoryRS_Success",true))
						{
							if(WSAssert.assertIfElementExists(holdItemInventoryRes, "HoldItemInventoryRS_HeldById_ID", false))
							{
								//Getting itemHoldId based on the heldById 
								String QS_01 = WSClient.getQuery("OWSBookHoldItems","QS_01");
								String itemHoldId = WSClient.getDBRow(QS_01).get("ITEM_HOLD_ID");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Item Hold ID: " + itemHoldId+"</b>");
								WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully Held the item"+"</b>");
								System.out.println("itemHoldid: "+itemHoldId);
								WSClient.setData("{var_itemHoldId}", itemHoldId);
								
								//Fetching itemId based on the itemHoldId
								String QS_02 = WSClient.getQuery("OWSBookHoldItems", "QS_02");
								String expectedItemId = WSClient.getDBRow(QS_02).get("ITEM_ID");
								
								// OWS BookHoldItems
								setOWSHeader();
								//OWSLib.setOWSHeader(uname, pwd, resortOperaValue, channelType, channelCarrier);
								WSClient.setData("{var_oresort}", resort);
								String bookHoldItemsReq = WSClient.createSOAPMessage("OWSBookHoldItems","DS_05");
								String bookHoldItemsRes = WSClient.processSOAPMessage(bookHoldItemsReq);
								
								//Checking for Text Element in Result
								if (WSAssert.assertIfElementExists(bookHoldItemsRes, "Result_Text_TextElement", true)) 
								{

									String message = WSAssert.getElementValue(bookHoldItemsRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>"+"The text displayed in the response is :" + message+"</b>");
								}
								
								//Checking For OperaErrorCode
								if (WSAssert.assertIfElementExists(bookHoldItemsRes,"BookHoldItemsResponse_Result_OperaErrorCode", true)) 
								{
									String code = WSAssert.getElementValue(bookHoldItemsRes, "BookHoldItemsResponse_Result_OperaErrorCode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error code displayed in the response is :" + code+"</b>");
								}
								
								//Checking For Fault Schema
								if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_faultcode", true))
								{
									String message=WSClient.getElementValue(bookHoldItemsRes,"BookHoldItemsResponse_faultcode", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.FAIL, "The Response has Fault Schema with message: "+message);
								}
								
								if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_Result_resultStatusFlag", false))
								{
									if(WSAssert.assertIfElementValueEquals(bookHoldItemsRes, "BookHoldItemsResponse_Result_resultStatusFlag", "SUCCESS",false))
									{
										//Validating if the above expectedItemId is getting attached to the Reservation or not
										String actualItemId = WSClient.getDBRow(WSClient.getQuery("QS_03")).get("ITEM_ID");
										if(WSAssert.assertEquals(expectedItemId, actualItemId, true))
										{
											WSClient.writeToReport(LogStatus.PASS, "Expected Item ID: "+expectedItemId+"   Actual Item ID: "+actualItemId);
										}
										else
										{
											WSClient.writeToReport(LogStatus.FAIL, "Expected Item ID: "+expectedItemId+"   Actual Item ID: "+actualItemId);
										}
									}
									else
									{
										WSClient.writeToReport(LogStatus.FAIL, "Result Status Flag should be SUCCESS not FAIL");
										//Checking for GDSError
										if(WSAssert.assertIfElementExists(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError",true)) 
										{
											String message=WSAssert.getElementValue(bookHoldItemsRes, "BookHoldItemsResponse_Result_GDSError", XMLType.RESPONSE);
											
											if((!message.equals("")))
											{
												WSClient.writeToReport(LogStatus.INFO, "<b>"+"The error displayed in the Release Room response is :"+ message+"</b>");
											}
										}
									}
									
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
	
	
	
}






