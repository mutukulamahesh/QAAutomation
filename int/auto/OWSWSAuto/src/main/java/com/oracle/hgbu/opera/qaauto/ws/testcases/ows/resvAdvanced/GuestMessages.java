package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;

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

public class GuestMessages extends WSSetUp{
	/**
	 * @author psarawag
	 */

	//	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	//	public void guestMessages_60010() {
	//		try {
	//			String testName = "guestMessages_60010";
	//			WSClient.startTest(testName, "Verify that the guest message is attached to the reservation with minimum required values in the request with channel when channel and carrier name is different.", "minimumRegression");
	//			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
	//				String chain=OPERALib.getChain();
	//				String resort = OPERALib.getResort();
	//				String channel = OWSLib.getChannel(3);
	//				String owsresort=OWSLib.getChannelResort(resort, channel);
	//				String uname = OPERALib.getUserName();
	//				String pwd = OPERALib.getPassword();
	//				String channelType = OWSLib.getChannelType(channel);
	//				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
	//				WSClient.setData("{var_resort}", resort);
	//				WSClient.setData("{var_chain}", chain);
	//				WSClient.setData("{var_owsresort}", owsresort);
	//				OPERALib.setOperaHeader(uname);
	//				String profileID = CreateProfile.createProfile("DS_01");
	//				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
	//				if (!profileID.equals("error")) {
	//
	//
	//					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
	//					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
	//					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
	//					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
	//
	//						HashMap<String,String> id = CreateReservation.createReservation("DS_01");
	//						if(!id.get("reservationId").equals("error")){
	//							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
	//						WSClient.setData("{var_reservationId}", id.get("reservationId"));
	//						WSClient.setData("{var_resvId}", id.get("reservationId"));
	//
	//							OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
	//
	//							String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_01");
	//							String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
	//							WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(ADD)</b>");
	//							if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
	//									"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
	//								LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_01"));
	//								LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
	//
	//								HashMap<String,String> xPath=new HashMap<String,String>();
	//								xPath.put("ReservationRequest_ReservationID_UniqueID","ReservationRequest_ReservationID_UniqueID");
	//								xPath.put("GuestMessagesRequest_GuestMessage_Value","GuestMessagesRequest_GuestMessage");
	//								actualValues=WSClient.getSingleNodeList(GuestMessagesReq, xPath, false, XMLType.REQUEST);
	//
	//								if(actualValues.containsKey("Value1")){
	//									actualValues.put("Value1",actualValues.get("Value1").trim());
	//								}
	//								if(db.containsKey("Value1")){
	//									db.put("Value1",db.get("Value1").trim());
	//								}
	//								WSAssert.assertEquals(actualValues, db, false);
	//							}
	//							if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {
	//
	//								/****
	//								 * Verifying that the error message is populated
	//								 * on the response
	//								 ********/
	//
	//								String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
	//										XMLType.RESPONSE);
	//								WSClient.writeToReport(LogStatus.INFO,
	//										"The text displayed in the response is : <b>" + message+"</b>");
	//							}
	//
	//							if (WSAssert.assertIfElementExists(GuestMessagesRes,
	//									"GuestMessagesResponse_Result_OperaErrorCode", true)) {
	//
	//								/****
	//								 * Verifying whether the error Message is
	//								 * populated on the response
	//								 ****/
	//
	//								String message = WSAssert.getElementValue(GuestMessagesRes,
	//										"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	//								WSClient.writeToReport(LogStatus.INFO,
	//										"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
	//							}
	//							if (WSAssert.assertIfElementExists(GuestMessagesRes,
	//									"GuestMessagesResponse_Result_GDSError", true)) {
	//
	//								/****
	//								 * Verifying whether the error Message is
	//								 * populated on the response
	//								 ****/
	//
	//								String message = WSAssert.getElementValue(GuestMessagesRes,
	//										"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
	//								WSClient.writeToReport(LogStatus.INFO,
	//										"The GDSerror displayed in the response is : <b>" + message+"</b>");
	//							}
	//							if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
	//									true)) {
	//								String message = WSClient.getElementValue(GuestMessagesRes,
	//										"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
	//								WSClient.writeToReport(LogStatus.FAIL,
	//										"Fault Schema in Response with message: <b>" + message+"</b>");
	//							}
	//						}
	//
	//				}
	//			} else {
	//				WSClient.writeToReport(LogStatus.WARNING,
	//						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
	//			}
	//		} catch (Exception e) {
	//			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
	//		}finally{
	//			 try {
	//				 if(!WSClient.getData("{var_resvId}").equals(""))
	//	                CancelReservation.cancelReservation("DS_02");
	//	            } catch (Exception e) {
	//	                // TODO Auto-generated catch block
	//	                e.printStackTrace();
	//	            }
	//		}
	//	}
	@Test(groups = { "sanity", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_38842() {
		try {
			String testName = "guestMessages_38842";
			WSClient.startTest(testName, "Verify that the guest message is attached to the reservation with minimum required values in the request", "sanity");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String chain=OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {


					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("reservationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_reservationId}", id.get("reservationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_01");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
						WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(ADD)</b>");
						if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
								"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_01"));
							LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();

							HashMap<String,String> xPath=new HashMap<String,String>();
							xPath.put("ReservationRequest_ReservationID_UniqueID","ReservationRequest_ReservationID_UniqueID");
							xPath.put("GuestMessagesRequest_GuestMessage_Value","GuestMessagesRequest_GuestMessage");
							actualValues=WSClient.getSingleNodeList(GuestMessagesReq, xPath, false, XMLType.REQUEST);

							if(actualValues.containsKey("Value1")){
								actualValues.put("Value1",actualValues.get("Value1").trim());
							}
							if(db.containsKey("Value1")){
								db.put("Value1",db.get("Value1").trim());
							}
							WSAssert.assertEquals(actualValues, db, false);
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is : <b>" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: <b>" + message+"</b>");
						}
					}

				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_38843() {
		try {
			String testName = "guestMessages_38843";
			WSClient.startTest(testName, "Verify that the guest message is attached to the reservation when sender and recipient values is passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("reservationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_reservationId}", id.get("reservationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));
						WSClient.setData("{var_nameTitle}", OperaPropConfig.getDataSetForCode("Title","DS_01"));
						WSClient.setData("{var_name}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
						WSClient.setData("{var_firstname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
						WSClient.setData("{var_company}", "Oracle");
						WSClient.setData("{var_rname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_02");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
						WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(ADD)</b>");
						if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
								"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_02"));
							LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();
							LinkedHashMap<String,String> expectedValues=new LinkedHashMap<String,String>();
							HashMap<String,String> xPath=new HashMap<String,String>();
							xPath.put("ReservationRequest_ReservationID_UniqueID","ReservationRequest_ReservationID_UniqueID");
							xPath.put("GuestMessagesRequest_GuestMessage_Value","GuestMessagesRequest_GuestMessage");
							xPath.put("GuestMessagesRequest_GuestMessage_SenderTitle","GuestMessagesRequest_GuestMessage");
							xPath.put("GuestMessagesRequest_GuestMessage_SenderName","GuestMessagesRequest_GuestMessage");
							xPath.put("GuestMessagesRequest_GuestMessage_SenderFirstName","GuestMessagesRequest_GuestMessage");
							xPath.put("GuestMessagesRequest_GuestMessage_SenderCompany","GuestMessagesRequest_GuestMessage");
							xPath.put("GuestMessagesRequest_GuestMessage_SenderContact","GuestMessagesRequest_GuestMessage");
							xPath.put("GuestMessagesRequest_GuestMessage_RecipientName","GuestMessagesRequest_GuestMessage");

							actualValues=WSClient.getSingleNodeList(GuestMessagesReq, xPath, false, XMLType.REQUEST);

							if(actualValues.containsKey("Value1")){
								expectedValues.put("Value1",actualValues.get("Value1").trim());
							}
							if(actualValues.containsKey("SenderContact1")){
								expectedValues.put("Value1","Phone no "+actualValues.get("SenderContact1").trim()+" "+expectedValues.get("Value1").trim());
							}
							if(actualValues.containsKey("SenderCompany1")){
								expectedValues.put("Value1","of "+actualValues.get("SenderCompany1").trim()+" "+expectedValues.get("Value1").trim());
							}
							if(actualValues.containsKey("SenderName1")){
								expectedValues.put("Value1",actualValues.get("SenderName1").trim()+" "+expectedValues.get("Value1").trim());
							}
							if(actualValues.containsKey("SenderFirstName1")){
								expectedValues.put("Value1",actualValues.get("SenderFirstName1").trim()+" "+expectedValues.get("Value1").trim());
							}
							if(actualValues.containsKey("SenderTitle1")){
								expectedValues.put("Value1",actualValues.get("SenderTitle1").trim()+" "+expectedValues.get("Value1").trim());
							}
							if(actualValues.containsKey("UniqueID1")){
								expectedValues.put("UniqueID1",actualValues.get("UniqueID1"));
							}
							if(actualValues.containsKey("RecipientName1")){
								expectedValues.put("RecipientName1",actualValues.get("RecipientName1"));
							}
							if(db.containsKey("Value1")){
								db.put("Value1",db.get("Value1").trim());
							}
							WSAssert.assertEquals(expectedValues, db, false);
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is : <b>" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: <b>" + message+"</b>");
						}

					}


				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_38844() {
		try {
			String testName = "guestMessages_38844";
			WSClient.startTest(testName, "Verify that the guest message is attached to the reservation when confirmation no is passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("confirmationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_confirmationId}", id.get("confirmationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));
						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_03");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
						WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(ADD)</b>");
						if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
								"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
							LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_03"));
							LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();

							HashMap<String,String> xPath=new HashMap<String,String>();
							xPath.put("ReservationRequest_ReservationID_UniqueID","ReservationRequest_ReservationID_UniqueID");
							xPath.put("GuestMessagesRequest_GuestMessage_Value","GuestMessagesRequest_GuestMessage");
							actualValues=WSClient.getSingleNodeList(GuestMessagesReq, xPath, false, XMLType.REQUEST);

							if(actualValues.containsKey("Value1")){
								actualValues.put("Value1",actualValues.get("Value1").trim());
							}
							if(db.containsKey("Value1")){
								db.put("Value1",db.get("Value1").trim());
							}
							WSAssert.assertEquals(actualValues, db, false);
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is : <b>" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: <b>" + message+"</b>");
						}

					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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


	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_38845() {
		try {
			String testName = "guestMessages_38845";
			WSClient.startTest(testName, "Verify that the guest message is attached to the reservation with confirmation no and Leg no is passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("confirmationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_confirmationId}", id.get("confirmationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));
						HashMap<String,String> id1 = CreateReservation.createReservation("DS_10");
						if(!id1.get("confirmationId").equals("error")){
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id1.get("reservationId") +"</b>");
							String legNo = WSClient.getDBRow(WSClient.getQuery("OWSGuestMessages","QS_04")).get("CONFIRMATION_LEG_NO");

							WSClient.setData("{var_resvId1}", id1.get("reservationId"));
							WSClient.setData("{var_confirmationId}", id1.get("confirmationId"));
							WSClient.setData("{var_legNo}", legNo);

							OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

							String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_04");
							String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
							WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(ADD)</b>");
							if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
									"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_05"));
								LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();

								HashMap<String,String> xPath=new HashMap<String,String>();
								xPath.put("ReservationRequest_ReservationID_UniqueID","GuestMessagesRequest_ReservationRequest_ReservationID");
								xPath.put("GuestMessagesRequest_GuestMessage_Value","GuestMessagesRequest_GuestMessage");
								actualValues=WSClient.getSingleNodeList(GuestMessagesReq, xPath, false, XMLType.REQUEST);

								if(actualValues.containsKey("Value1")){
									actualValues.put("Value1",actualValues.get("Value1").trim());
								}
								if(db.containsKey("Value1")){
									db.put("Value1",db.get("Value1").trim());
								}
								WSAssert.assertEquals(actualValues, db, false);
							}
							if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is : <b>" + message+"</b>");
							}

							if (WSAssert.assertIfElementExists(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(GuestMessagesRes,
										"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
							}
							if (WSAssert.assertIfElementExists(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(GuestMessagesRes,
										"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The GDSerror displayed in the response is : <b>" + message+"</b>");
							}
							if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
									true)) {
								String message = WSClient.getElementValue(GuestMessagesRes,
										"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,
										"Fault Schema in Response with message: <b>" + message+"</b>");
							}
						} }
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}finally{
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
				if(!WSClient.getData("{var_resvId1}").equals(""))
					CancelReservation.cancelReservation("DS_03");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_38846() {
		try {
			String testName = "guestMessages_38846";
			WSClient.startTest(testName, "Verify that the guest message is attached to the reservation is deleted when reservation id "
					+ "and msg id is passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("reservationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_reservationId}", id.get("reservationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));



						String GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_01");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"CreateGuestMessagesRS_Success", true)) {
							if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
								String msgId=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
								WSClient.setData("{var_msgId}", msgId);
								WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId +"</b>");
								OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
								GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_05");
								GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
								WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(DELETE)</b>");
								if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
										"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_01"));
									if(db.size()==0)
									{
										WSClient.writeToReport(LogStatus.PASS, "Guest Message Successfully deleted");
									}
								}
							}else{
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
							}
						}else{
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is : <b>" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: <b>" + message+"</b>");
						}

					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
		finally{
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_38847() {
		try {
			String testName = "guestMessages_38847";
			WSClient.startTest(testName, "Verify that the guest message is attached to the reservation is deleted when Confirmation id "
					+ "and msg id is passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort = OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("confirmationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_confirmationId}", id.get("confirmationId"));

						WSClient.setData("{var_resvId}", id.get("reservationId"));


						String GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_02");

						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"CreateGuestMessagesRS_Success", true)) {
							if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
								String msgId=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
								WSClient.setData("{var_msgId}", msgId);
								WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId +"</b>");
								OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
								GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_06");
								GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
								WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(DELETE)</b>");
								if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
										"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_03"));
									if(db.size()==0)
									{
										WSClient.writeToReport(LogStatus.PASS, "Guest Message Successfully deleted");
									}
								}
							}else{
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
							}
						}else{
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is : <b>" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: <b>" + message+"</b>");
						}

					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_38848() {
		try {
			String testName = "guestMessages_38848";
			WSClient.startTest(testName, "Verify that the guest message is attached to the reservation is deleted when Confirmation id, Leg No "
					+ "and msg id is passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("confirmationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_confirmationId}", id.get("confirmationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));
						HashMap<String,String> id1 = CreateReservation.createReservation("DS_10");
						if(!id1.get("confirmationId").equals("error")){
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id1.get("reservationId") +"</b>");
							String legNo = WSClient.getDBRow(WSClient.getQuery("OWSGuestMessages","QS_04")).get("CONFIRMATION_LEG_NO");

							WSClient.setData("{var_resvId1}", id1.get("reservationId"));
							WSClient.setData("{var_reservationId}", id1.get("reservationId"));
							WSClient.setData("{var_confirmationId}", id1.get("confirmationId"));
							WSClient.setData("{var_legNo}", legNo);


							String GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_01");

							String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);

							if (WSAssert.assertIfElementExists(GuestMessagesRes,
									"CreateGuestMessagesRS_Success", true)) {
								if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
									String msgId=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
									WSClient.setData("{var_msgId}", msgId);
									WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId +"</b>");
									OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
									GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_07");
									GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
									WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(DELETE)</b>");
									if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
											"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_05"));
										if(db.size()==0)
										{
											WSClient.writeToReport(LogStatus.PASS, "Guest Message Successfully deleted");
										}
									}
								}else{
									WSClient.writeToReport(LogStatus.WARNING,
											"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
								}
							}else{
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
							}
							if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is : <b>" + message+"</b>");
							}

							if (WSAssert.assertIfElementExists(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(GuestMessagesRes,
										"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
							}
							if (WSAssert.assertIfElementExists(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(GuestMessagesRes,
										"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The GDSerror displayed in the response is : <b>" + message+"</b>");
							}
							if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
									true)) {
								String message = WSClient.getElementValue(GuestMessagesRes,
										"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,
										"Fault Schema in Response with message: <b>" + message+"</b>");
							}
						} }
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}finally{
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
				if(!WSClient.getData("{var_resvId1}").equals(""))
					CancelReservation.cancelReservation("DS_03");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_38857() {
		try {
			String testName = "guestMessages_38857";
			WSClient.startTest(testName, "Verify that the guest message  attached to the reservation is updated when reservation Id is passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("reservationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_reservationId}", id.get("reservationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));


						String GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_01");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"CreateGuestMessagesRS_Success", true)) {
							if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
								String msgId=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
								WSClient.setData("{var_msgId}", msgId);
								WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId +"</b>");
								OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
								GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_08");
								GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
								WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(UPDATE)</b>");
								if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
										"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_01"));
									LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();

									HashMap<String,String> xPath=new HashMap<String,String>();
									xPath.put("ReservationRequest_ReservationID_UniqueID","ReservationRequest_ReservationID_UniqueID");
									xPath.put("GuestMessagesRequest_GuestMessage_Value","GuestMessagesRequest_GuestMessage");
									actualValues=WSClient.getSingleNodeList(GuestMessagesReq, xPath, false, XMLType.REQUEST);

									if(actualValues.containsKey("Value1")){
										actualValues.put("Value1",actualValues.get("Value1").trim());
									}
									if(db.containsKey("Value1")){
										db.put("Value1",db.get("Value1").trim());
									}
									WSAssert.assertEquals(actualValues, db, false);
									if(WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_GuestMessages_Value", true)){
										WSClient.writeToReport(LogStatus.FAIL, WSClient.getResponseXPath("GuestMessagesResponse_GuestMessages_Value") + " exists on the response message");
									}


								}}else{
									WSClient.writeToReport(LogStatus.WARNING,
											"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
								}
						}else{
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is : <b>" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: <b>" + message+"</b>");
						}

					} }
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_38858() {
		try {
			String testName = "guestMessages_38858";
			WSClient.startTest(testName, "Verify that the guest message attached to the reservation is fetched when reservationId is passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("reservationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_reservationId}", id.get("reservationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));


						String GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_01");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"CreateGuestMessagesRS_Success", true)) {
							if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
								String msgId=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId +"</b>");
								GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_03");
								GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
								if (WSAssert.assertIfElementExists(GuestMessagesRes,
										"CreateGuestMessagesRS_Success", true)) {
									if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
										String msgId1=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId1 +"</b>");
										OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
										GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_09");
										GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
										WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(FETCH)</b>");
										if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
												"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											List<LinkedHashMap<String,String>> db=WSClient.getDBRows(WSClient.getQuery("QS_06"));
											List<LinkedHashMap<String,String>> actualValues=new ArrayList<LinkedHashMap<String,String>>();

											HashMap<String,String> xPath=new HashMap<String,String>();
											xPath.put("GuestMessagesResponse_GuestMessages_Value","GuestMessagesResponse_GuestMessages");
											xPath.put("GuestMessagesResponse_GuestMessages_GuestMessageID","GuestMessagesResponse_GuestMessages");
											actualValues=WSClient.getMultipleNodeList(GuestMessagesRes, xPath, false, XMLType.RESPONSE);

											for(int i=0;i<actualValues.size();i++){
												if(actualValues.get(i).containsKey("Value1")){
													actualValues.get(i).put("Value1",actualValues.get(i).get("Value1").trim());
												}
											}
											for(int i=0;i<db.size();i++){
												if(db.get(i).containsKey("Value1")){
													db.get(i).put("Value1",db.get(i).get("Value1").trim());
												}}
											WSAssert.assertEquals(actualValues, db, false);
										}}else{
											WSClient.writeToReport(LogStatus.WARNING,
													"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
										}
								}else{
									WSClient.writeToReport(LogStatus.WARNING,
											"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
								}
							}else{
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
							}
						}else{
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is : <b>" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: <b>" + message+"</b>");
						}

					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_38933() {
		try {
			String testName = "guestMessages_38933";
			WSClient.startTest(testName, "Verify that the guest message is attached to the reservation is updated with Confirmation ID in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("confirmationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_confirmationId}", id.get("confirmationId"));

						WSClient.setData("{var_resvId}", id.get("reservationId"));
						String GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_02");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"CreateGuestMessagesRS_Success", true)) {
							if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
								String msgId=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
								WSClient.setData("{var_msgId}", msgId);
								WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId +"</b>");
								OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
								GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_10");
								GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
								WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(UPDATE)</b>");
								if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
										"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_03"));
									LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();

									HashMap<String,String> xPath=new HashMap<String,String>();
									xPath.put("ReservationRequest_ReservationID_UniqueID","ReservationRequest_ReservationID_UniqueID");
									xPath.put("GuestMessagesRequest_GuestMessage_Value","GuestMessagesRequest_GuestMessage");
									actualValues=WSClient.getSingleNodeList(GuestMessagesReq, xPath, false, XMLType.REQUEST);

									if(actualValues.containsKey("Value1")){
										actualValues.put("Value1",actualValues.get("Value1").trim());
									}
									if(db.containsKey("Value1")){
										db.put("Value1",db.get("Value1").trim());
									}
									WSAssert.assertEquals(actualValues, db, false);
									if(WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_GuestMessages_Value", true)){
										WSClient.writeToReport(LogStatus.FAIL, WSClient.getResponseXPath("GuestMessagesResponse_GuestMessages_Value") + " exists on the response message");
									}
								}}else{
									WSClient.writeToReport(LogStatus.WARNING,
											"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
								}
						}else{
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is : <b>" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: <b>" + message+"</b>");
						}

					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_38935() {
		try {
			String testName = "guestMessages_38935";
			WSClient.startTest(testName, "Verify that the guest message is attached to the reservation is fetched when confirmation Id is passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("confirmationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_confirmationId}", id.get("confirmationId"));

						WSClient.setData("{var_resvId}", id.get("reservationId"));

						String GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_02");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"CreateGuestMessagesRS_Success", true)) {
							if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
								String msgId=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId +"</b>");
								GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_04");
								GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
								if (WSAssert.assertIfElementExists(GuestMessagesRes,
										"CreateGuestMessagesRS_Success", true)) {
									if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
										String msgId1=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId1 +"</b>");
										OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
										GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_12");
										GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
										WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(FETCH)</b>");
										if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
												"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
											List<LinkedHashMap<String,String>> db=WSClient.getDBRows(WSClient.getQuery("QS_07"));
											List<LinkedHashMap<String,String>> actualValues=new ArrayList<LinkedHashMap<String,String>>();

											HashMap<String,String> xPath=new HashMap<String,String>();
											xPath.put("GuestMessagesResponse_GuestMessages_Value","GuestMessagesResponse_GuestMessages");
											xPath.put("GuestMessagesResponse_GuestMessages_GuestMessageID","GuestMessagesResponse_GuestMessages");
											actualValues=WSClient.getMultipleNodeList(GuestMessagesRes, xPath, false, XMLType.RESPONSE);

											for(int i=0;i<actualValues.size();i++){
												if(actualValues.get(i).containsKey("Value1")){
													actualValues.get(i).put("Value1",actualValues.get(i).get("Value1").trim());
												}
											}
											for(int i=0;i<db.size();i++){
												if(db.get(i).containsKey("Value1")){
													db.get(i).put("Value1",db.get(i).get("Value1").trim());
												}}
											WSAssert.assertEquals(actualValues, db, false);
										}}else{
											WSClient.writeToReport(LogStatus.WARNING,
													"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
										}
								}else{
									WSClient.writeToReport(LogStatus.WARNING,
											"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
								}
							}else{
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
							}
						}else{
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is : <b>" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: <b>" + message+"</b>");
						}

					}
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_38934() {
		try {
			String testName = "guestMessages_38934";
			WSClient.startTest(testName, "Verify that the guest message attached to the reservation is updated with confirmationId and legNo is passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("confirmationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_confirmationId}", id.get("confirmationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));
						HashMap<String,String> id1 = CreateReservation.createReservation("DS_10");
						if(!id1.get("confirmationId").equals("error")){
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id1.get("reservationId") +"</b>");
							String legNo = WSClient.getDBRow(WSClient.getQuery("OWSGuestMessages","QS_04")).get("CONFIRMATION_LEG_NO");

							WSClient.setData("{var_resvId1}", id1.get("reservationId"));
							WSClient.setData("{var_reservationId}", id1.get("reservationId"));
							WSClient.setData("{var_confirmationId}", id1.get("confirmationId"));
							WSClient.setData("{var_legNo}", legNo);





							String GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_01");
							String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
							if (WSAssert.assertIfElementExists(GuestMessagesRes,
									"CreateGuestMessagesRS_Success", true)) {
								if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
									String msgId=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
									WSClient.setData("{var_msgId}", msgId);
									WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId +"</b>");
									OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
									GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_11");
									GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
									WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(UPDATE)</b>");
									if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
											"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
										LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_05"));
										LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();

										HashMap<String,String> xPath=new HashMap<String,String>();
										xPath.put("ReservationRequest_ReservationID_UniqueID","GuestMessagesRequest_ReservationRequest_ReservationID");
										xPath.put("GuestMessagesRequest_GuestMessage_Value","GuestMessagesRequest_GuestMessage");
										actualValues=WSClient.getSingleNodeList(GuestMessagesReq, xPath, false, XMLType.REQUEST);

										if(actualValues.containsKey("Value1")){
											actualValues.put("Value1",actualValues.get("Value1").trim());
										}
										if(db.containsKey("Value1")){
											db.put("Value1",db.get("Value1").trim());
										}
										WSAssert.assertEquals(actualValues, db, false);
										if(WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_GuestMessages_Value", true)){
											WSClient.writeToReport(LogStatus.FAIL, WSClient.getResponseXPath("GuestMessagesResponse_GuestMessages_Value") + " exists on the response message");
										}
									}}else{
										WSClient.writeToReport(LogStatus.WARNING,
												"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
									}
							}else{
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
							}
							if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is : <b>" + message+"</b>");
							}

							if (WSAssert.assertIfElementExists(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(GuestMessagesRes,
										"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
							}
							if (WSAssert.assertIfElementExists(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(GuestMessagesRes,
										"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The GDSerror displayed in the response is : <b>" + message+"</b>");
							}
							if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
									true)) {
								String message = WSClient.getElementValue(GuestMessagesRes,
										"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,
										"Fault Schema in Response with message: <b>" + message+"</b>");
							}

						} }
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}finally{
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
				if(!WSClient.getData("{var_resvId1}").equals(""))
					CancelReservation.cancelReservation("DS_03");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_38936() {
		try {
			String testName = "guestMessages_38936";
			WSClient.startTest(testName, "Verify that the guest message attached to the reservation is fetched when confirmation Id and leg No is passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("confirmationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_confirmationId}", id.get("confirmationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));
						HashMap<String,String> id1 = CreateReservation.createReservation("DS_10");
						if(!id1.get("confirmationId").equals("error")){
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id1.get("reservationId") +"</b>");
							String legNo = WSClient.getDBRow(WSClient.getQuery("OWSGuestMessages","QS_04")).get("CONFIRMATION_LEG_NO");

							WSClient.setData("{var_resvId1}", id1.get("reservationId"));
							WSClient.setData("{var_reservationId}", id1.get("reservationId"));
							WSClient.setData("{var_confirmationId}", id1.get("confirmationId"));
							WSClient.setData("{var_legNo}", legNo);



							String GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_01");
							String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
							if (WSAssert.assertIfElementExists(GuestMessagesRes,
									"CreateGuestMessagesRS_Success", true)) {
								if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
									String msgId=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId +"</b>");
									GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_03");
									GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
									if (WSAssert.assertIfElementExists(GuestMessagesRes,
											"CreateGuestMessagesRS_Success", true)) {
										if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
											String msgId1=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId1 +"</b>");
											OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
											GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_13");
											GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
											WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(FETCH)</b>");
											if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
													"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
												List<LinkedHashMap<String,String>> db=WSClient.getDBRows(WSClient.getQuery("QS_08"));
												List<LinkedHashMap<String,String>> actualValues=new ArrayList<LinkedHashMap<String,String>>();

												HashMap<String,String> xPath=new HashMap<String,String>();
												xPath.put("GuestMessagesResponse_GuestMessages_Value","GuestMessagesResponse_GuestMessages");
												xPath.put("GuestMessagesResponse_GuestMessages_GuestMessageID","GuestMessagesResponse_GuestMessages");
												actualValues=WSClient.getMultipleNodeList(GuestMessagesRes, xPath, false, XMLType.RESPONSE);

												for(int i=0;i<actualValues.size();i++){
													if(actualValues.get(i).containsKey("Value1")){
														actualValues.get(i).put("Value1",actualValues.get(i).get("Value1").trim());
													}
												}
												for(int i=0;i<db.size();i++){
													if(db.get(i).containsKey("Value1")){
														db.get(i).put("Value1",db.get(i).get("Value1").trim());
													}}
												WSAssert.assertEquals(actualValues, db, false);
											}}else{
												WSClient.writeToReport(LogStatus.WARNING,
														"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
											}
									}else{
										WSClient.writeToReport(LogStatus.WARNING,
												"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
									}
								}else{
									WSClient.writeToReport(LogStatus.WARNING,
											"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
								}
							}else{
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
							}
							if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is : <b>" + message+"</b>");
							}

							if (WSAssert.assertIfElementExists(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(GuestMessagesRes,
										"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
							}
							if (WSAssert.assertIfElementExists(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(GuestMessagesRes,
										"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The GDSerror displayed in the response is : <b>" + message+"</b>");
							}
							if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
									true)) {
								String message = WSClient.getElementValue(GuestMessagesRes,
										"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,
										"Fault Schema in Response with message: <b>" + message+"</b>");
							}

						} }
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}finally{
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
				if(!WSClient.getData("{var_resvId1}").equals(""))
					CancelReservation.cancelReservation("DS_03");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39160() {
		try {
			String testName = "guestMessages_39160";
			WSClient.startTest(testName, "Verify that the add guest message response gives correct error if invalid reservation id is "
					+ "passed in the request", "minimumRegression");
			String chain=OPERALib.getChain();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsresort=OWSLib.getChannelResort(resort, channel);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_owsresort}", owsresort);
			WSClient.setData("{var_reservationId}", WSClient.getKeywordData("{KEYWORD_RANDNUM_8}"));

			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

			String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_01");
			String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
			WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(ADD)</b>");
			WSAssert.assertIfElementValueEquals(GuestMessagesRes,
					"GuestMessagesResponse_Result_resultStatusFlag", "FAIL", false);


			if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated
				 * on the response
				 ********/

				String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The text displayed in the response is :" + message+"</b>");
			}

			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"The GDSerror displayed in the response is :" + message);
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
					true)) {
				String message = WSClient.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.FAIL,
						"Fault Schema in Response with message: " + message);
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39161() {
		try {
			String testName = "guestMessages_39161";
			WSClient.startTest(testName, "Verify that the add guest message response gives correct error if no reservation id is "
					+ "passed in the request", "minimumRegression");
			String chain=OPERALib.getChain();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsresort=OWSLib.getChannelResort(resort, channel);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_owsresort}", owsresort);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_reservationId}", "");

			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

			String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_01");
			String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
			WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(ADD)</b>");
			WSAssert.assertIfElementValueEquals(GuestMessagesRes,
					"GuestMessagesResponse_Result_resultStatusFlag", "FAIL", false);


			if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated
				 * on the response
				 ********/

				String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The text displayed in the response is :" + message+"</b>");
			}

			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"The GDSerror displayed in the response is :" + message);
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
					true)) {
				String message = WSClient.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.FAIL,
						"Fault Schema in Response with message: " + message);
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39162() {
		try {
			String testName = "guestMessages_39162";
			WSClient.startTest(testName, "Verify that the add guest message response gives correct error if invalid confirmation id is "
					+ "passed in the request", "minimumRegression");
			String chain=OPERALib.getChain();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsresort=OWSLib.getChannelResort(resort, channel);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_owsresort}", owsresort);
			WSClient.setData("{var_confirmationId}", WSClient.getKeywordData("{KEYWORD_RANDNUM_8}"));

			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

			String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_03");
			String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
			WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(ADD)</b>");
			WSAssert.assertIfElementValueEquals(GuestMessagesRes,
					"GuestMessagesResponse_Result_resultStatusFlag", "FAIL", false);


			if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated
				 * on the response
				 ********/

				String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The text displayed in the response is :" + message+"</b>");
			}

			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"The GDSerror displayed in the response is :" + message);
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
					true)) {
				String message = WSClient.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.FAIL,
						"Fault Schema in Response with message: " + message);
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39163() {
		try {
			String testName = "guestMessages_39163";
			WSClient.startTest(testName, "Verify that the add guest message response gives correct error if confirmation id and invalid LegNo is "
					+ "passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String chain=OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("confirmationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_confirmationId}", id.get("confirmationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));
						WSClient.setData("{var_legNo}", "10");
						WSClient.setData("{var_msgId}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_04");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
						WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(ADD)</b>");
						WSAssert.assertIfElementValueEquals(GuestMessagesRes,
								"GuestMessagesResponse_Result_resultStatusFlag", "FAIL", false);


						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is :" + message);
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: " + message);
						}
					}}} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
					}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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


	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39165() {
		try {
			String testName = "guestMessages_39165";
			WSClient.startTest(testName, "Verify that the delete guest message response gives correct error if no reservation id is "
					+ "passed in the request", "minimumRegression");
			String chain=OPERALib.getChain();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsresort = OWSLib.getChannelResort(resort, channel);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_owsresort}", owsresort);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_reservationId}", "");
			WSClient.setData("{var_msgId}", "");

			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

			String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_05");
			String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
			WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(DELETE)</b>");
			WSAssert.assertIfElementValueEquals(GuestMessagesRes,
					"GuestMessagesResponse_Result_resultStatusFlag", "FAIL", false);


			if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated
				 * on the response
				 ********/

				String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The text displayed in the response is :" + message+"</b>");
			}

			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"The GDSerror displayed in the response is :" + message);
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
					true)) {
				String message = WSClient.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.FAIL,
						"Fault Schema in Response with message: " + message);
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39166() {
		try {
			String testName = "guestMessages_39166";
			WSClient.startTest(testName, "Verify that the delete guest message response gives correct error if invalid confirmation id is "
					+ "passed in the request", "minimumRegression");
			String chain=OPERALib.getChain();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsresort=OWSLib.getChannelResort(resort, channel);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_owsresort}", owsresort);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_confirmationId}", WSClient.getKeywordData("{KEYWORD_RANDNUM_8}"));
			WSClient.setData("{var_msgId}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

			String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_06");
			String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
			WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(DELETE)</b>");
			WSAssert.assertIfElementValueEquals(GuestMessagesRes,
					"GuestMessagesResponse_Result_resultStatusFlag", "FAIL", false);


			if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated
				 * on the response
				 ********/

				String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The text displayed in the response is :" + message+"</b>");
			}

			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"The GDSerror displayed in the response is :" + message);
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
					true)) {
				String message = WSClient.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.FAIL,
						"Fault Schema in Response with message: " + message);
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39167() {
		try {
			String testName = "guestMessages_39167";
			WSClient.startTest(testName, "Verify that the delete guest message response gives correct error if confirmation id and invalid LegNo is "
					+ "passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String chain=OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("confirmationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_confirmationId}", id.get("confirmationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));
						WSClient.setData("{var_legNo}", "10");
						WSClient.setData("{var_msgId}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_07");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
						WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(DELETE)</b>");
						WSAssert.assertIfElementValueEquals(GuestMessagesRes,
								"GuestMessagesResponse_Result_resultStatusFlag", "FAIL", false);


						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is :" + message);
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: " + message);
						}
					}}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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

	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39169() {
		try {
			String testName = "guestMessages_39169";
			WSClient.startTest(testName, "Verify that the delete guest message response gives correct error if no messageId is "
					+ "passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String chain=OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_chain}", chain);
				WSClient.setData("{var_owsresort}", owsresort);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("reservationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_reservationId}", id.get("reservationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));
						WSClient.setData("{var_msgId}", "");

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_05");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
						WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(DELETE)</b>");
						WSAssert.assertIfElementValueEquals(GuestMessagesRes,
								"GuestMessagesResponse_Result_resultStatusFlag", "FAIL", false);


						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is :" + message);
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: " + message);
						}
					}}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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

	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39171() {
		try {
			String testName = "guestMessages_39171";
			WSClient.startTest(testName, "Verify that the update guest message response gives correct error if no reservation id is "
					+ "passed in the request", "minimumRegression");
			String chain=OPERALib.getChain();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsresort=OWSLib.getChannelResort(resort, channel);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_owsresort}", owsresort);
			WSClient.setData("{var_reservationId}", "");
			WSClient.setData("{var_msgId}", "");

			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

			String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_08");
			String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
			WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(UPDATE)</b>");
			WSAssert.assertIfElementValueEquals(GuestMessagesRes,
					"GuestMessagesResponse_Result_resultStatusFlag", "FAIL", false);


			if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated
				 * on the response
				 ********/

				String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The text displayed in the response is :" + message+"</b>");
			}

			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"The GDSerror displayed in the response is :" + message);
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
					true)) {
				String message = WSClient.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.FAIL,
						"Fault Schema in Response with message: " + message);
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39172() {
		try {
			String testName = "guestMessages_39172";
			WSClient.startTest(testName, "Verify that the update guest message response gives correct error if invalid confirmation id is "
					+ "passed in the request", "minimumRegression");
			String chain=OPERALib.getChain();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsresort=OWSLib.getChannelResort(resort, channel);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_owsresort}", owsresort);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_confirmationId}", WSClient.getKeywordData("{KEYWORD_RANDNUM_8}"));
			WSClient.setData("{var_msgId}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

			String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_10");
			String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
			WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(UPDATE)</b>");
			WSAssert.assertIfElementValueEquals(GuestMessagesRes,
					"GuestMessagesResponse_Result_resultStatusFlag", "FAIL", false);


			if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated
				 * on the response
				 ********/

				String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The text displayed in the response is :" + message+"</b>");
			}

			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"The GDSerror displayed in the response is :" + message);
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
					true)) {
				String message = WSClient.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.FAIL,
						"Fault Schema in Response with message: " + message);
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39173() {
		try {
			String testName = "guestMessages_39173";
			WSClient.startTest(testName, "Verify that the update guest message response gives correct error if confirmation id and invalid LegNo is "
					+ "passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String chain=OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("confirmationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_confirmationId}", id.get("confirmationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));
						WSClient.setData("{var_legNo}", "10");
						WSClient.setData("{var_msgId}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_11");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
						WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(UPDATE)</b>");
						WSAssert.assertIfElementValueEquals(GuestMessagesRes,
								"GuestMessagesResponse_Result_resultStatusFlag", "FAIL", false);


						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is :" + message);
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: " + message);
						}
					}}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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

	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39176() {
		try {
			String testName = "guestMessages_39176";
			WSClient.startTest(testName, "Verify that the update guest message response gives correct error if no messageId is "
					+ "passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String chain=OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("reservationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_reservationId}", id.get("reservationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));
						WSClient.setData("{var_msgId}", "");

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_08");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
						WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(UPDATE)</b>");
						WSAssert.assertIfElementValueEquals(GuestMessagesRes,
								"GuestMessagesResponse_Result_resultStatusFlag", "FAIL", false);


						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is :" + message);
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: " + message);
						}
					}}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39178() {
		try {
			String testName = "guestMessages_39178";
			WSClient.startTest(testName, "Verify that the fetch guest message response gives correct error if invalid reservation id is "
					+ "passed in the request", "minimumRegression");
			String chain=OPERALib.getChain();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsresort=OWSLib.getChannelResort(resort, channel);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_owsresort}", owsresort);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_reservationId}", WSClient.getKeywordData("{KEYWORD_RANDNUM_8}"));

			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

			String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_09");
			String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
			WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(FETCH)</b>");
			WSAssert.assertIfElementValueEquals(GuestMessagesRes,
					"GuestMessagesResponse_Result_resultStatusFlag", "FAIL", false);


			if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated
				 * on the response
				 ********/

				String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The text displayed in the response is :" + message+"</b>");
			}

			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"The GDSerror displayed in the response is :" + message);
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
					true)) {
				String message = WSClient.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.FAIL,
						"Fault Schema in Response with message: " + message);
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39179() {
		try {
			String testName = "guestMessages_39179";
			WSClient.startTest(testName, "Verify that the fetch guest message response gives correct error if no reservation id is "
					+ "passed in the request", "minimumRegression");
			String chain=OPERALib.getChain();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsresort=OWSLib.getChannelResort(resort, channel);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_owsresort}", owsresort);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_reservationId}", "");

			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

			String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_09");
			String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
			WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(FETCH)</b>");
			WSAssert.assertIfElementValueEquals(GuestMessagesRes,
					"GuestMessagesResponse_Result_resultStatusFlag", "FAIL", false);


			if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated
				 * on the response
				 ********/

				String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The text displayed in the response is :" + message+"</b>");
			}

			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"The GDSerror displayed in the response is :" + message);
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
					true)) {
				String message = WSClient.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.FAIL,
						"Fault Schema in Response with message: " + message);
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39180() {
		try {
			String testName = "guestMessages_39180";
			WSClient.startTest(testName, "Verify that the fetch guest message response gives correct error if invalid confirmation id is "
					+ "passed in the request", "minimumRegression");
			String chain=OPERALib.getChain();
			String resort = OPERALib.getResort();
			String channel = OWSLib.getChannel();
			String owsresort=OWSLib.getChannelResort(resort, channel);
			String uname = OPERALib.getUserName();
			String pwd = OPERALib.getPassword();
			String channelType = OWSLib.getChannelType(channel);
			String channelCarrier = OWSLib.getChannelCarier(resort, channel);
			WSClient.setData("{var_resort}", resort);
			WSClient.setData("{var_chain}", chain);
			WSClient.setData("{var_owsresort}", owsresort);
			WSClient.setData("{var_confirmationId}", WSClient.getKeywordData("{KEYWORD_RANDNUM_8}"));

			OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

			String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_12");
			String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
			WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(FETCH)</b>");
			WSAssert.assertIfElementValueEquals(GuestMessagesRes,
					"GuestMessagesResponse_Result_resultStatusFlag", "FAIL", false);


			if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

				/****
				 * Verifying that the error message is populated
				 * on the response
				 ********/

				String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
						XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The text displayed in the response is :" + message+"</b>");
			}

			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_OperaErrorCode", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes,
					"GuestMessagesResponse_Result_GDSError", true)) {

				/****
				 * Verifying whether the error Message is
				 * populated on the response
				 ****/

				String message = WSAssert.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.INFO,
						"The GDSerror displayed in the response is :" + message);
			}
			if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
					true)) {
				String message = WSClient.getElementValue(GuestMessagesRes,
						"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
				WSClient.writeToReport(LogStatus.FAIL,
						"Fault Schema in Response with message: " + message);
			}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}
	}
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39181() {
		try {
			String testName = "guestMessages_39181";
			WSClient.startTest(testName, "Verify that the fetch guest message response gives correct error if confirmation id and invalid LegNo is "
					+ "passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String chain=OPERALib.getChain();
				String resort = OPERALib.getResort();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("confirmationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_confirmationId}", id.get("confirmationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));
						WSClient.setData("{var_legNo}", "10");
						WSClient.setData("{var_msgId}", WSClient.getKeywordData("{KEYWORD_RANDNUM_5}"));

						OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

						String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_13");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
						WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(FETCH)</b>");
						WSAssert.assertIfElementValueEquals(GuestMessagesRes,
								"GuestMessagesResponse_Result_resultStatusFlag", "FAIL", false);


						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The text displayed in the response is :" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"<b>The OperaErrorCode displayed in the  response is :" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is :" + message);
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: " + message);
						}
					}}} else {
						WSClient.writeToReport(LogStatus.WARNING,
								"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
					}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39182() {
		try {
			String testName = "guestMessages_39182";
			WSClient.startTest(testName, "Verify that the status of guest message attached to the reservation is updated as received", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("reservationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_reservationId}", id.get("reservationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));


						String GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_01");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"CreateGuestMessagesRS_Success", true)) {
							if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
								String msgId=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
								WSClient.setData("{var_msgId}", msgId);
								WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId +"</b>");
								OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
								GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_14");
								GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
								WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(UPDATE)</b>");
								if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
										"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									String statusFlag=WSClient.getDBRow(WSClient.getQuery("QS_09")).get("STATUS_FLAG");
									WSAssert.assertEquals("MR", statusFlag, false);

									if(WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_GuestMessages_Value", true)){
										WSClient.writeToReport(LogStatus.FAIL, WSClient.getResponseXPath("GuestMessagesResponse_GuestMessages_Value") + " exists on the response message");
									}
								}}else{
									WSClient.writeToReport(LogStatus.WARNING,
											"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
								}
						}else{
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is : <b>" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: <b>" + message+"</b>");
						}

					} }
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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

	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39281() {
		try {
			String testName = "guestMessages_39281";
			WSClient.startTest(testName, "Verify that the guest message is attached to the first reservation when two reservations are there for the "
					+ "same confirmation No and only confirmation No is passed in the request", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {

					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("confirmationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_confirmationId}", id.get("confirmationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));
						HashMap<String,String> id1 = CreateReservation.createReservation("DS_10");
						if(!id1.get("confirmationId").equals("error")){
							WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id1.get("reservationId") +"</b>");
							WSClient.setData("{var_resvId1}", id1.get("reservationId"));
							WSClient.setData("{var_confirmationId}", id1.get("confirmationId"));

							OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);

							String GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_03");
							String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
							WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(ADD)</b>");
							if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
									"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								WSClient.setData("{var_legNo}", "1");
								LinkedHashMap<String,String> db=WSClient.getDBRow(WSClient.getQuery("QS_05"));
								LinkedHashMap<String,String> actualValues=new LinkedHashMap<String,String>();

								HashMap<String,String> xPath=new HashMap<String,String>();
								xPath.put("ReservationRequest_ReservationID_UniqueID","GuestMessagesRequest_ReservationRequest_ReservationID");
								xPath.put("GuestMessagesRequest_GuestMessage_Value","GuestMessagesRequest_GuestMessage");
								actualValues=WSClient.getSingleNodeList(GuestMessagesReq, xPath, false, XMLType.REQUEST);
								actualValues.put("UniqueID2","1");
								if(actualValues.containsKey("Value1")){
									actualValues.put("Value1",actualValues.get("Value1").trim());
								}
								if(db.containsKey("Value1")){
									db.put("Value1",db.get("Value1").trim());
								}

								WSAssert.assertEquals(actualValues, db, false);
							}
							if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

								/****
								 * Verifying that the error message is populated
								 * on the response
								 ********/

								String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
										XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The text displayed in the response is : <b>" + message+"</b>");
							}

							if (WSAssert.assertIfElementExists(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(GuestMessagesRes,
										"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
							}
							if (WSAssert.assertIfElementExists(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", true)) {

								/****
								 * Verifying whether the error Message is
								 * populated on the response
								 ****/

								String message = WSAssert.getElementValue(GuestMessagesRes,
										"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.INFO,
										"The GDSerror displayed in the response is : <b>" + message+"</b>");
							}
							if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
									true)) {
								String message = WSClient.getElementValue(GuestMessagesRes,
										"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
								WSClient.writeToReport(LogStatus.FAIL,
										"Fault Schema in Response with message: <b>" + message+"</b>");
							}
						} }
				}
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
		}finally{
			try {
				if(!WSClient.getData("{var_resvId}").equals(""))
					CancelReservation.cancelReservation("DS_02");
				if(!WSClient.getData("{var_resvId1}").equals(""))
					CancelReservation.cancelReservation("DS_03");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39420() {
		try {
			String testName = "guestMessages_39420";
			WSClient.startTest(testName, "Verify that the only undelivered guest message attached to the reservation is fetched in the response", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("reservationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_reservationId}", id.get("reservationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));


						String GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_01");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"CreateGuestMessagesRS_Success", true)) {
							if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
								String msgId=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
								WSClient.setData("{var_msgId}", msgId);
								WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId +"</b>");
								GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_03");
								GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
								if (WSAssert.assertIfElementExists(GuestMessagesRes,
										"CreateGuestMessagesRS_Success", true)) {
									if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
										String msgId1=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId1 +"</b>");
										GuestMessagesReq = WSClient.createSOAPMessage("ChangeGuestMessages", "DS_01");
										GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);

										if (WSAssert.assertIfElementExists(GuestMessagesRes,
												"ChangeGuestMessagesRS_Success", true)) {
											if(WSAssert.assertIfElementExists(GuestMessagesRes, "ChangeGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
												WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId +" status changed to MR</b>");
												OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
												GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_15");
												GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
												WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(FETCH)</b>");
												if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
														"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
													List<LinkedHashMap<String,String>> db=WSClient.getDBRows(WSClient.getQuery("QS_10"));
													List<LinkedHashMap<String,String>> actualValues=new ArrayList<LinkedHashMap<String,String>>();

													HashMap<String,String> xPath=new HashMap<String,String>();
													xPath.put("GuestMessagesResponse_GuestMessages_Value","GuestMessagesResponse_GuestMessages");
													xPath.put("GuestMessagesResponse_GuestMessages_GuestMessageID","GuestMessagesResponse_GuestMessages");
													actualValues=WSClient.getMultipleNodeList(GuestMessagesRes, xPath, false, XMLType.RESPONSE);

													for(int i=0;i<actualValues.size();i++){
														if(actualValues.get(i).containsKey("Value1")){
															actualValues.get(i).put("Value1",actualValues.get(i).get("Value1").trim());
														}
													}
													for(int i=0;i<db.size();i++){
														if(db.get(i).containsKey("Value1")){
															db.get(i).put("Value1",db.get(i).get("Value1").trim());
														}}
													WSAssert.assertEquals(actualValues, db, false);
												}
											}else{
												WSClient.writeToReport(LogStatus.WARNING,
														"Prerequisite blocked --->The attached guestmessage failed to update");
											}
										}else{
											WSClient.writeToReport(LogStatus.WARNING,
													"Prerequisite blocked --->OWSGuestMessage with update action");
										}

									}else{
										WSClient.writeToReport(LogStatus.WARNING,
												"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
									}
								}else{
									WSClient.writeToReport(LogStatus.WARNING,
											"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
								}}else{
									WSClient.writeToReport(LogStatus.WARNING,
											"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
								}
						}else{
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is : <b>" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: <b>" + message+"</b>");
						}

					} }
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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
	@Test(groups = { "minimumRegression", "GuestMessages", "ResvAdvanced", "OWS" })
	public void guestMessages_39421() {
		try {
			String testName = "guestMessages_39421";
			WSClient.startTest(testName, "Verify that the all guest message attached to the reservation is fetched in the response", "minimumRegression");
			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String resort = OPERALib.getResort();
				String chain=OPERALib.getChain();
				String channel = OWSLib.getChannel();
				String owsresort=OWSLib.getChannelResort(resort, channel);
				String uname = OPERALib.getUserName();
				String pwd = OPERALib.getPassword();
				String channelType = OWSLib.getChannelType(channel);
				String channelCarrier = OWSLib.getChannelCarier(resort, channel);
				WSClient.setData("{var_resort}", resort);
				WSClient.setData("{var_owsresort}", owsresort);
				WSClient.setData("{var_chain}", chain);
				OPERALib.setOperaHeader(uname);
				String profileID = CreateProfile.createProfile("DS_01");
				WSClient.writeToReport(LogStatus.INFO, "<b>Profile ID:- " + profileID +"</b>");
				if (!profileID.equals("error")) {
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					HashMap<String,String> id = CreateReservation.createReservation("DS_01");
					if(!id.get("reservationId").equals("error")){
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation ID:- " + id.get("reservationId") +"</b>");
						WSClient.setData("{var_reservationId}", id.get("reservationId"));
						WSClient.setData("{var_resvId}", id.get("reservationId"));


						String GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_01");
						String GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"CreateGuestMessagesRS_Success", true)) {
							if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
								String msgId=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
								WSClient.setData("{var_msgId}", msgId);
								WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId +"</b>");
								GuestMessagesReq = WSClient.createSOAPMessage("CreateGuestMessages", "DS_03");
								GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
								if (WSAssert.assertIfElementExists(GuestMessagesRes,
										"CreateGuestMessagesRS_Success", true)) {
									if(WSAssert.assertIfElementExists(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
										String msgId1=WSClient.getElementValue(GuestMessagesRes, "CreateGuestMessagesRS_GuestMessages_GuestMessage_ID", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId1 +"</b>");
										GuestMessagesReq = WSClient.createSOAPMessage("ChangeGuestMessages", "DS_01");
										GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);

										if (WSAssert.assertIfElementExists(GuestMessagesRes,
												"ChangeGuestMessagesRS_Success", true)) {
											if(WSAssert.assertIfElementExists(GuestMessagesRes, "ChangeGuestMessagesRS_GuestMessages_GuestMessage_ID", true)){
												WSClient.writeToReport(LogStatus.INFO, "<b>Message ID:- " + msgId +" status changed to MR</b>");
												OWSLib.setOWSHeader(uname, pwd, resort, channelType, channelCarrier);
												GuestMessagesReq = WSClient.createSOAPMessage("OWSGuestMessages", "DS_16");
												GuestMessagesRes = WSClient.processSOAPMessage(GuestMessagesReq);
												WSClient.writeToReport(LogStatus.INFO, "<b>GuestMessages(FETCH)</b>");
												if (WSAssert.assertIfElementValueEquals(GuestMessagesRes,
														"GuestMessagesResponse_Result_resultStatusFlag", "SUCCESS", false)) {
													List<LinkedHashMap<String,String>> db=WSClient.getDBRows(WSClient.getQuery("QS_11"));
													List<LinkedHashMap<String,String>> actualValues=new ArrayList<LinkedHashMap<String,String>>();

													HashMap<String,String> xPath=new HashMap<String,String>();
													xPath.put("GuestMessagesResponse_GuestMessages_Value","GuestMessagesResponse_GuestMessages");
													xPath.put("GuestMessagesResponse_GuestMessages_GuestMessageID","GuestMessagesResponse_GuestMessages");
													xPath.put("GuestMessagesResponse_GuestMessages_StatusFlag","GuestMessagesResponse_GuestMessages");
													actualValues=WSClient.getMultipleNodeList(GuestMessagesRes, xPath, false, XMLType.RESPONSE);

													for(int i=0;i<actualValues.size();i++){
														if(actualValues.get(i).containsKey("Value1")){
															actualValues.get(i).put("Value1",actualValues.get(i).get("Value1").trim());
														}
													}
													for(int i=0;i<db.size();i++){
														if(db.get(i).containsKey("Value1")){
															db.get(i).put("Value1",db.get(i).get("Value1").trim());
														}}
													WSAssert.assertEquals(actualValues, db, false);
												}
											}else{
												WSClient.writeToReport(LogStatus.WARNING,
														"Prerequisite blocked --->The attached guestmessage failed to update");
											}
										}else{
											WSClient.writeToReport(LogStatus.WARNING,
													"Prerequisite blocked --->ChangeGuestMessages Failed");
										}

									}else{
										WSClient.writeToReport(LogStatus.WARNING,
												"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
									}
								}else{
									WSClient.writeToReport(LogStatus.WARNING,
											"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
								}}else{
									WSClient.writeToReport(LogStatus.WARNING,
											"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
								}
						}else{
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisite blocked --->The profile doesnot have any guestmessage attached");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "Result_Text_TextElement", true)) {

							/****
							 * Verifying that the error message is populated
							 * on the response
							 ********/

							String message = WSAssert.getElementValue(GuestMessagesRes, "Result_Text_TextElement",
									XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The text displayed in the response is : <b>" + message+"</b>");
						}

						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_OperaErrorCode", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_OperaErrorCode", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The OperaErrorCode displayed in the  response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes,
								"GuestMessagesResponse_Result_GDSError", true)) {

							/****
							 * Verifying whether the error Message is
							 * populated on the response
							 ****/

							String message = WSAssert.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_Result_GDSError", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO,
									"The GDSerror displayed in the response is : <b>" + message+"</b>");
						}
						if (WSAssert.assertIfElementExists(GuestMessagesRes, "GuestMessagesResponse_faultcode",
								true)) {
							String message = WSClient.getElementValue(GuestMessagesRes,
									"GuestMessagesResponse_faultstring", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.FAIL,
									"Fault Schema in Response with message: <b>" + message+"</b>");
						}

					} }
			} else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception Occured  " + e);
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
}
