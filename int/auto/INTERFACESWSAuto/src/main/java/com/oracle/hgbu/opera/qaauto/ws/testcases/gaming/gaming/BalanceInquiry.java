package com.oracle.hgbu.opera.qaauto.ws.testcases.gaming.gaming;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.relevantcodes.customextentreports.LogStatus;

public class BalanceInquiry extends WSSetUp {
		@Test(groups = { "BAT", "BalanceInquiry", "Profile", "Gaming" })
		@Parameters({ "schema", "version" })
		public void balanceInquiryGaming(String schema, String version) {
			WSClient.startTest("balanceInquiryGaming","Verify that the the folio balance is correctly being retrieved for the given profile", "bat");
			
			String sUsername = "", sPassword = "", sChain = "", sResort = "";
			boolean subscribedAlready= false; 
			try {
				sUsername = OPERALib.getUserName();
				sPassword = OPERALib.getPassword();
				sChain = OPERALib.getChain();
				sResort = OPERALib.getResort();

				// Parameters required for the Gaming Header
				WSClient.setData("{var_userName}", sUsername);
				WSClient.setData("{var_password}", sPassword);
				WSClient.setData("{var_chain}", sChain);
				WSClient.setData("{var_resort}", sResort);
				WSClient.setData("{var_msgID}", UUID.randomUUID().toString());

					/**********************************
					 * Fetch Existing Reservation
					 *********************************/
					
					// Retrieve existing reservation
					LinkedHashMap<String, String> reservationMap = new LinkedHashMap<String, String>();
					String resvQry = WSClient.getQuery("GamingBalanceInquiry", "QS_02", false);
					reservationMap = WSClient.getDBRow(resvQry);

					if (reservationMap.size() <= 0) {
						resvQry = WSClient.getQuery("GamingBalanceInquiry", "QS_03", false);
						reservationMap = WSClient.getDBRow(resvQry);
						subscribedAlready = true;
					}
					
					if (reservationMap.size() > 0) {
						// Set the data required for Subscription operation
						String profileID = reservationMap.get("NAME_ID");
						WSClient.setData("{var_operaProfileID}", profileID);
						WSClient.writeToReport(LogStatus.INFO, "Existing Reservation ID having Financial Transactions <B>" + reservationMap.get("RESV_NAME_ID") +"</B> and Profile ID <B>"+reservationMap.get("NAME_ID")+"</B>");
						/************************************************************************
						 * Subscribe the Name ID of the existing reservation to Gaming Interface
						 ************************************************************************/
						 
						if(subscribedAlready == false ) {
							WSClient.writeToReport(LogStatus.INFO, "<b> Subscribe Profile of the existing reservation</b>");
							// Run subscription operation
							String subscriptionReq = WSClient.createSOAPMessage("GamingSubscription", "DS_01");
							String subscriptionRes = WSClient.processSOAPMessage(subscriptionReq);

							//Validate if subscription function is successful
							if (WSAssert.assertIfElementContains(subscriptionRes,"SubscriptionResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								String subscriptionQry = WSClient.getQuery("GamingSubscription", "QS_01",false);
								String databaseNameID = WSClient.getDBRow(subscriptionQry).get("DATABASE_NAME_ID") == null ? ""	: WSClient.getDBRow(subscriptionQry).get("DATABASE_NAME_ID");
								String expDatabaseNameID = WSClient.getElementValue(subscriptionReq,"Subscription_ProfileIDs_UniqueID[2]", XMLType.REQUEST);
								if (WSAssert.assertEquals(expDatabaseNameID, databaseNameID, true)) {
										WSClient.writeToReport(LogStatus.INFO, "Subscription to Gaming Interface is successful, Entry found in NAME_SUBSCRIPTIONS table");
										WSClient.setData("{var_externalProfileID}", expDatabaseNameID);
									
										/********************************************
										 * Fetch Balance Information using subscribed profile
										 *********************************************/
										WSClient.writeToReport(LogStatus.INFO, "<b> Fetch Balance Information using subscribed profile </b>");
									
										// Construct Assign ID Card Request and Post it
										String balanceInquiryReq = WSClient.createSOAPMessage("GamingBalanceInquiry", "DS_01");
										String balanceInquiryRes = WSClient.processSOAPMessage(balanceInquiryReq);
	
										// Validate if Card assignment is successful
										if (WSAssert.assertIfElementContains(balanceInquiryRes, "BalanceInquiryResponse_Result_resultStatusFlag","SUCCESS", false)) {
											
											WSAssert.assertIfElementValueEquals(balanceInquiryRes, "FolioBalance_ProfileIDs_UniqueID", expDatabaseNameID, false);
											WSAssert.assertIfElementValueEquals(balanceInquiryRes, "FolioBalance_ProfileIDs_UniqueID_source", "EXTERNAL", false);
											
											WSClient.setData("{var_reservationNameID}", reservationMap.get("RESV_NAME_ID"));
											List<LinkedHashMap<String, String>> balanceInquiryExpMap = new ArrayList<LinkedHashMap<String, String>>();
											String balanceInquiryQry = WSClient.getQuery("GamingBalanceInquiry", "QS_01");
											System.out.println("balanceInquiryQry: "+balanceInquiryQry);
											balanceInquiryExpMap = WSClient.getDBRows(balanceInquiryQry);
											
											System.out.println("from DB: "+balanceInquiryExpMap);
											
											LinkedHashMap<String, String> balanceInquiryXPaths = new LinkedHashMap<>();
											balanceInquiryXPaths.put("FolioBalance_PostDetails_PostDetail_transactionID", "FolioBalance_PostDetails_PostDetail");
											balanceInquiryXPaths.put("FolioBalance_PostDetails_PostDetail_transactionCode", "FolioBalance_PostDetails_PostDetail");
											balanceInquiryXPaths.put("PostDetails_PostDetail_resortCode", "FolioBalance_PostDetails_PostDetail");
											balanceInquiryXPaths.put("PostDetails_PostDetail_ReservationID", "FolioBalance_PostDetails_PostDetail");
											balanceInquiryXPaths.put("PostDetails_PostDetail_roomNumber", "FolioBalance_PostDetails_PostDetail");
											balanceInquiryXPaths.put("PostDetails_PostDetail_postedDate", "FolioBalance_PostDetails_PostDetail");
											balanceInquiryXPaths.put("PostDetails_PostDetail_Amount", "FolioBalance_PostDetails_PostDetail");
											balanceInquiryXPaths.put("PostDetails_PostDetail_Amount_currencyCode", "FolioBalance_PostDetails_PostDetail");
											balanceInquiryXPaths.put("PostDetails_PostDetail_marketSegment", "FolioBalance_PostDetails_PostDetail");
											balanceInquiryXPaths.put("PostDetails_PostDetail_sourceCode", "FolioBalance_PostDetails_PostDetail");
											balanceInquiryXPaths.put("PostDetails_PostDetail_revenueType", "FolioBalance_PostDetails_PostDetail");
											
											List<LinkedHashMap<String, String>> balanceInquiryActMap = new ArrayList<LinkedHashMap<String, String>>();
											balanceInquiryActMap = WSClient.getMultipleNodeList(balanceInquiryRes, balanceInquiryXPaths, false, XMLType.RESPONSE);
											
											WSAssert.assertEquals(balanceInquiryActMap, balanceInquiryExpMap, false);
										
									   } 
									   else {
										   WSClient.writeToReport(LogStatus.FAIL, "Balance Inquiry retrieval is failed");
									   }
								} 
								else {
									WSClient.writeToReport(LogStatus.WARNING, "Subscription to Gaming Interface is failed, No entry found in NAME_SUBSCRIPTIONS table");
								}
					   	 }
						 else {
							WSClient.writeToReport(LogStatus.WARNING, "Subscription to Gaming Interface is failed, SUCCESS Flag is missing on response payload");
						 }
						}
						else {
								WSClient.writeToReport(LogStatus.INFO, "<b> Profile "+profileID+" is subscribed to Gaming Interface </b>");
								
								LinkedHashMap<String, String> membershipMap = new LinkedHashMap<String, String>();
								String membershipQry = WSClient.getQuery("GamingBalanceInquiry", "QS_04", false);
								membershipMap = WSClient.getDBRow(membershipQry);
								String expDatabaseNameID = membershipMap.get("MEMBERSHIP_CARD_NO");
								
								WSClient.setData("{var_externalProfileID}", expDatabaseNameID);
								
								// Construct Assign ID Card Request and Post it
								String balanceInquiryReq = WSClient.createSOAPMessage("GamingBalanceInquiry", "DS_01");
								String balanceInquiryRes = WSClient.processSOAPMessage(balanceInquiryReq);

								// Validate if Card assignment is successful
								if (WSAssert.assertIfElementContains(balanceInquiryRes, "BalanceInquiryResponse_Result_resultStatusFlag","SUCCESS", false)) {
									
									WSAssert.assertIfElementValueEquals(balanceInquiryRes, "FolioBalance_ProfileIDs_UniqueID", expDatabaseNameID, false);
									WSAssert.assertIfElementValueEquals(balanceInquiryRes, "FolioBalance_ProfileIDs_UniqueID_source", "EXTERNAL", false);
									
									WSClient.setData("{var_reservationNameID}", reservationMap.get("RESV_NAME_ID"));
									List<LinkedHashMap<String, String>> balanceInquiryExpMap = new ArrayList<LinkedHashMap<String, String>>();
									String balanceInquiryQry = WSClient.getQuery("GamingBalanceInquiry", "QS_01");
									System.out.println("balanceInquiryQry: "+balanceInquiryQry);
									balanceInquiryExpMap = WSClient.getDBRows(balanceInquiryQry);
									
									System.out.println("from DB: "+balanceInquiryExpMap);
									
									LinkedHashMap<String, String> balanceInquiryXPaths = new LinkedHashMap<>();
									balanceInquiryXPaths.put("FolioBalance_PostDetails_PostDetail_transactionID", "FolioBalance_PostDetails_PostDetail");
									balanceInquiryXPaths.put("FolioBalance_PostDetails_PostDetail_transactionCode", "FolioBalance_PostDetails_PostDetail");
									balanceInquiryXPaths.put("PostDetails_PostDetail_resortCode", "FolioBalance_PostDetails_PostDetail");
									balanceInquiryXPaths.put("PostDetails_PostDetail_ReservationID", "FolioBalance_PostDetails_PostDetail");
									balanceInquiryXPaths.put("PostDetails_PostDetail_roomNumber", "FolioBalance_PostDetails_PostDetail");
									balanceInquiryXPaths.put("PostDetails_PostDetail_postedDate", "FolioBalance_PostDetails_PostDetail");
									balanceInquiryXPaths.put("PostDetails_PostDetail_Amount", "FolioBalance_PostDetails_PostDetail");
									balanceInquiryXPaths.put("PostDetails_PostDetail_Amount_currencyCode", "FolioBalance_PostDetails_PostDetail");
									balanceInquiryXPaths.put("PostDetails_PostDetail_marketSegment", "FolioBalance_PostDetails_PostDetail");
									balanceInquiryXPaths.put("PostDetails_PostDetail_sourceCode", "FolioBalance_PostDetails_PostDetail");
									balanceInquiryXPaths.put("PostDetails_PostDetail_revenueType", "FolioBalance_PostDetails_PostDetail");
									
									List<LinkedHashMap<String, String>> balanceInquiryActMap = new ArrayList<LinkedHashMap<String, String>>();
									balanceInquiryActMap = WSClient.getMultipleNodeList(balanceInquiryRes, balanceInquiryXPaths, false, XMLType.RESPONSE);
									
									WSAssert.assertEquals(balanceInquiryActMap, balanceInquiryExpMap, false);
								
							   } 
							   else {
								   WSClient.writeToReport(LogStatus.FAIL, "Balance Inquiry retrieval is failed");
							   }
							}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "No reservations found");
					}
				
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("SQL Error! " + e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}
