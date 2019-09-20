package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.v2006.reservation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CheckoutReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.SetHousekeepingRoomStatus;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class ReservationLookUp extends WSSetUp {
	String profileID="";
	HashMap<String, String> resvID=new HashMap<>();
	@Test(groups = { "sanity", "ReservationLookup", "HTNG2006", "HTNG" })
	
	public void ReservationLookUp_2006_20474() {
			
		try {
			String testName = "reservationLookUp_2006_20474";
			WSClient.startTest(testName, "Verify that reservation details are populated correctly on the response when reservationStatus & ResortId are passed on the request message", "sanity");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
			
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);

				OPERALib.setOperaHeader(OPERALib.getUserName());

				

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				profileID=CreateProfile.createProfile("DS_01");
				if(!profileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", profileID);
					
						/******************* Prerequisite 2:Create a Reservation ************************/
						
							resvID=CreateReservation.createReservation("DS_01");
						if(!resvID.equals("error"))
						{
							
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							
				
				
				
								
								WSClient.setData("{var_reservation_id}", resvID.get("reservationId"));
								WSClient.setData("{var_Confirmation}", resvID.get("confirmationId"));
								

								/********
								 * Prerequisite 3: Cancel Reservation
								 **************/
								String cancelReservationReq = WSClient.createSOAPMessage("CancelReservation",
										"DS_01");
								String cancelReservationRes = WSClient.processSOAPMessage(cancelReservationReq);
								if (WSAssert.assertIfElementExists(cancelReservationRes,
										"CancelReservationRS_Success", true)) {

									String res_status = "CANCELLED";
									String res_ExtStatus = "OTHER";

									WSClient.setData("{var_status}", res_status);
									WSClient.setData("{var_ExtStatus}", res_ExtStatus);
									WSClient.setData("{var_extResort}", resortExtValue);
									WSClient.setData("{var_resort}", resortOperaValue);

									/********
									 * Invoke operation to be validated:
									 * Reservation Look Up for Cancelled
									 * reservations
									 **************/
									HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
									HTNGLib.getInterfaceFromAddress());
									String ReservationLookupReq = WSClient
											.createSOAPMessage("HTNG2006ReservationLookup", "DS_01");
									String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);
									
									if(WSAssert.assertIfElementValueEquals(ReservationLookupRes,"ReservationLookupResponse_Result_resultStatusFlag","SUCCESS", false))
									{
										LinkedHashMap<String, String> db = new LinkedHashMap<String,String>();

									HashMap<String, String> xpath = new HashMap<String, String>();
									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
									WSClient.writeToReport(LogStatus.INFO,"<b>Query to fetch reservation Lookup details from DataBase</b>");
									String query=WSClient.getQuery("QS_11");
									db = WSClient.getDBRow(query);

									xpath.put("ReservationLookups_ReservationLookup_ReservationID",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookups_ReservationLookup_ProfileID",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put(
											"ReservationLookupResponse_ReservationLookups_ReservationLookup_reservationStatus",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookups_ReservationLookup_ConfirmationNo",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookups_ReservationLookup_ResortId",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");

									actualValues = WSClient.getSingleNodeList(ReservationLookupRes, xpath, false,
											XMLType.RESPONSE);
									
																	
									WSClient.writeToReport(LogStatus.INFO,"<b>Validating the response with DataBase</b>");
									WSAssert.assertEquals(actualValues, db, false);
									
									}
									else
									{
										WSClient.writeToReport(LogStatus.FAIL,"Reservation Lookup failed--------->>>>Success flag doesnt exist");
									}
									if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", true)) {
					    				/****
					    				 * Verify that the error message is populated on the
					    				 * response
					 					 ********/
					    		
					    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
					    								XMLType.RESPONSE);
					    				WSClient.writeToReport(LogStatus.INFO,
					    						"The text displayed in the response is :" + message);
					    								}

								} else {

									WSClient.writeToReport(LogStatus.WARNING, "Reservation is not cancelled");
								}
							}		
												
								
						 

					} 
				} 
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			// logger.endExtentTest();
			
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                   CancelReservation.cancelReservation("DS_02");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
			
		}
	}
	
	
	
		
	
	@Test(groups = { "minimumRegression", "ReservationLookup", "HTNG2006", "HTNG" })
	public void ReservationLookUp_2006_38832() {
		try {
			String testName = "reservationLookUp_2006_38832";
			WSClient.startTest(testName, "Verify that the  reservationLookup  is successful for a given cancelled id", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
	
				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				
				String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_fname}", firstName);
				WSClient.setData("{var_lname}", lastName);
				
			

				OPERALib.setOperaHeader(OPERALib.getUserName());

				
				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				profileID=CreateProfile.createProfile("DS_01");
				if(!profileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", profileID);
					
						/******************* Prerequisite 2:Create a Reservation ************************/
						
							resvID=CreateReservation.createReservation("DS_01");
						if(!resvID.equals("error"))
						{
							
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							
				
				
								WSClient.setData("{var_reservation_id}", resvID.get("reservationId"));
								
								resvID=CreateReservation.createReservation("DS_09");
																
								

								/********
								 * Prerequisite 3: Cancel Reservation
								 **************/
								String cancelReservationReq = WSClient.createSOAPMessage("CancelReservation",
										"DS_01");
								String cancelReservationRes = WSClient.processSOAPMessage(cancelReservationReq);
								if (WSAssert.assertIfElementExists(cancelReservationRes,
										"CancelReservationRS_Success", true)) {

									String res_status = "CANCELLED";
									String res_ExtStatus = "CANCELLED";

									WSClient.setData("{var_status}", res_status);
									WSClient.setData("{var_ExtStatus}", res_ExtStatus);
									WSClient.setData("{var_extResort}", resortExtValue);
									WSClient.setData("{var_resort}", resortOperaValue);

									/********
									 * Invoke operation to be validated:
									 * Reservation Look Up for Cancelled
									 * reservations
									 **************/
									HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
											HTNGLib.getInterfaceFromAddress());

									String ReservationLookupReq = WSClient
											.createSOAPMessage("HTNG2006ReservationLookup", "DS_01");
									String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);
									if(WSAssert.assertIfElementValueEquals(ReservationLookupRes,"ReservationLookupResponse_Result_resultStatusFlag","SUCCESS", false))
									{
									List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

									HashMap<String, String> xpath = new HashMap<String, String>();
									List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
									
									WSClient.writeToReport(LogStatus.INFO,"Query to fetch reservation Lookup details from DataBase");
									String query=WSClient.getQuery("QS_01");
									db = WSClient.getDBRows(query);

									xpath.put("ReservationLookups_ReservationLookup_ReservationID",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookups_ReservationLookup_ProfileID",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put(
											"ReservationLookupResponse_ReservationLookups_ReservationLookup_reservationStatus",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookups_ReservationLookup_ConfirmationNo",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookups_ReservationLookup_ResortId",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");

									actualValues = WSClient.getMultipleNodeList(ReservationLookupRes, xpath, false,
											XMLType.RESPONSE);
									
	
									WSClient.writeToReport(LogStatus.INFO,"<b>Validating response with database</b>");
									
									if(db.containsAll(actualValues))
									{
										
										WSClient.writeToReport(LogStatus.PASS, "Cancelled Reservations are all properly fetched from the database");
									}		
									else
									{
										WSClient.writeToReport(LogStatus.PASS, "Cancelled Reservations inthe response and database are not matched ");
									}
									
									}
									
									else
									{
										WSClient.writeToReport(LogStatus.FAIL,"Reservation Lookup failed-->Success flag is not populated on response");
										
									}
									if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", true)) {
					    				/****
					    				 * Verify that the error message is populated on the
					    				 * response
					 					 ********/
					    		
					    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
					    								XMLType.RESPONSE);
					    				WSClient.writeToReport(LogStatus.INFO,
					    						"The text displayed in the response is :" + message);
					    								}

								} else {

									WSClient.writeToReport(LogStatus.WARNING, "Reservation is not cancelled");
								}
							}														
												
									

					} 
				} 
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                   CancelReservation.cancelReservation("DS_02");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
			
		}
	}


	
	@Test(groups = { "minimumRegression", "ReservationLookup", "HTNG2006", "HTNG" })
	public void ReservationLookUp_2006_20460() {
		try {
			String testName = "reservationLookUp_2006_20460";
			WSClient.startTest(testName, "Verify that reservation deatils populated correctly on the response  when  Room number and ResortId are passed in the ReservationLookUpRequest", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				
				WSClient.setData("{var_profileSource}", interfaceName);
				
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				
				
				OPERALib.setOperaHeader(OPERALib.getUserName());

				
				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				
				
				
				profileID=CreateProfile.createProfile("DS_01");
				if(!profileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", profileID);
					
						/******************* Prerequisite 2:Create a Reservation ************************/
						
							resvID=CreateReservation.createReservation("DS_08");
						if(!resvID.equals("error"))
						{
							
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
								
								String roomNumber;
								String fetchHotelRoomsXML = WSClient.createSOAPMessage("FetchHotelRooms", "DS_09");
								String fetchHotelResponse = WSClient.processSOAPMessage(fetchHotelRoomsXML);
								
								if (WSAssert.assertIfElementExists(fetchHotelResponse, "FetchHotelRoomsRS_Success", true)) {

									roomNumber = WSClient.getElementValue(fetchHotelResponse,
											"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
									if (roomNumber.equals("")) {
										String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "FirstFloor");
										String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
										
										if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

											roomNumber = WSClient.getElementValue(createRoomReq,
													"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
											
											
										}
										else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Pre_requisite-Create Room is unsuccessful,unable to perform  ");
										}
									}
									WSClient.setData("{var_roomNumber}", roomNumber);
								}
								else{
									
									
										String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "FirstFloor");
										String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
										
										if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

											roomNumber = WSClient.getElementValue(createRoomReq,
													"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
											WSClient.setData("{var_roomNumber}", roomNumber);
										}
										else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Pre_requisite-Create Room is unsuccessful,unable to perform  ");
										}
										
									}
									
									
								
								
									

								
									/************
									 * Operation Assign Room : Performing
									 * the Assign Room operation to the
									 * reservation created
									 ****************/

									String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");

									String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);
									if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",true)) {

										/********
										 * Invoke operation to be validated:
										 * Reservation Look Up reservations
										 **************/
										HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
												HTNGLib.getInterfaceFromAddress());

										String ReservationLookupReq = WSClient
												.createSOAPMessage("HTNG2006ReservationLookup", "DS_11");
										String ReservationLookupRes = WSClient
												.processSOAPMessage(ReservationLookupReq);
										if(WSAssert.assertIfElementValueEquals(ReservationLookupRes,"ReservationLookupResponse_Result_resultStatusFlag","SUCCESS", false))
										{
										List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

										HashMap<String, String> xpath = new HashMap<String, String>();
										List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

										xpath.put("ReservationLookups_ReservationLookup_ReservationID",
												"ReservationLookupResponse_ReservationLookups_ReservationLookup");
										xpath.put("ReservationLookups_ReservationLookup_RoomNumber",
												"ReservationLookupResponse_ReservationLookups_ReservationLookup");

										String query=WSClient.getQuery("QS_10");
										db = WSClient.getDBRows(query);

										actualValues = WSClient.getMultipleNodeList(ReservationLookupRes, xpath,
												false, XMLType.RESPONSE);
										// Validating db values with
										// response
										WSAssert.assertEquals(actualValues, db, false);

									} else {
										WSClient.writeToReport(LogStatus.FAIL,
												"Reservation Lookup failed--------Success flag is not populated on response ");
									}
										if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", true)) {
						    				/****
						    				 * Verify that the error message is populated on the
						    				 * response
						 					 ********/
						    		
						    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
						    								XMLType.RESPONSE);
						    				WSClient.writeToReport(LogStatus.INFO,
						    						"The text displayed in the response is :" + message);
						    								}
									}
									else {
										WSClient.writeToReport(LogStatus.WARNING,
												"Pre_requisite-Assign Room is unsuccessful,unable to perform ");
									}
								
							}
							
													
								

					} 
				} 
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                   CancelReservation.cancelReservation("DS_02");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
		}
	}
	
	
	

	@Test(groups = { "minimumRegression", "ReservationLookup", "HTNG2006", "HTNG" })
	public void ReservationLookUp_2006_20458() {
		try {
			String testName = "reservationLookUp_2006_20458";
			WSClient.startTest(testName, "Verify that reservation details are populated correctly on the response when  reservationID and ResortId are passed in the ReservationLookupRequest.", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
				System.out.println("address :"+state_code+" "+state);
				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address_country :"+country_code);
				HashMap<String, String> addLOV = new HashMap<String, String>();
				
				addLOV=OPERALib.fetchAddressLOV(state,country_code);
				WSClient.setData("{var_state}", state);
				WSClient.setData("{var_country}", country_code);
				WSClient.setData("{var_addressType}",
						OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));				

				OPERALib.setOperaHeader(OPERALib.getUserName());

				

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				profileID=CreateProfile.createProfile("DS_04");
				if(!profileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", profileID);
					
						/******************* Prerequisite 2:Create a Reservation ************************/
						
							resvID=CreateReservation.createReservation("DS_01");
						if(!resvID.equals("error"))
						{
							
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
				
				
								WSClient.setData("{var_reservation_id}", resvID.get("reservationId"));
								WSClient.setData("{var_Confirmation}", resvID.get("confirmationId"));
								
								
								HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
										HTNGLib.getInterfaceFromAddress());
							
									String ReservationLookupReq = WSClient
											.createSOAPMessage("HTNG2006ReservationLookup", "DS_02");
									String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);
									if(WSAssert.assertIfElementValueEquals(ReservationLookupRes,"ReservationLookupResponse_Result_resultStatusFlag","SUCCESS", false))
									{
																
									LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();

									HashMap<String, String> xpath = new HashMap<String, String>();
									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
									WSClient.writeToReport(LogStatus.INFO,"Query to fetch all the values for a single reservation");
									String query=WSClient.getQuery("QS_02");
									db = WSClient.getDBRow(query);

									xpath.put("ReservationLookups_ReservationLookup_ReservationID",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookups_ReservationLookup_ProfileID",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookups_ReservationLookup_ConfirmationNo",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookups_ReservationLookup_ResortId",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookup_ProfileInfo_NameTitle",
											"ReservationLookups_ReservationLookup_ProfileInfo");
									xpath.put("ReservationLookup_ProfileInfo_FirstName",
											"ReservationLookups_ReservationLookup_ProfileInfo");
									xpath.put("ReservationLookup_ProfileInfo_MiddleName",
											"ReservationLookups_ReservationLookup_ProfileInfo");
									xpath.put("ReservationLookup_ProfileInfo_LastName",
											"ReservationLookups_ReservationLookup_ProfileInfo");
									xpath.put("ReservationLookup_DateRange_Start",
											"ReservationLookups_ReservationLookup_DateRange");
									xpath.put("ReservationLookup_DateRange_End",
											"ReservationLookups_ReservationLookup_DateRange");
									xpath.put(
											"ReservationLookupResponse_ReservationLookups_ReservationLookup_reservationStatus",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookup_ReservationAddress_AddressLine",
											"ReservationLookups_ReservationLookup_ReservationAddress");
									xpath.put("ReservationLookups_ReservationLookup_ReservationAddress_addressType",
											"ReservationLookups_ReservationLookup_ReservationAddress");
									xpath.put("ReservationLookup_ReservationAddress_CityName",
											"ReservationLookups_ReservationLookup_ReservationAddress");
									xpath.put("ReservationLookup_ReservationAddress_StateProv",
											"ReservationLookups_ReservationLookup_ReservationAddress");
									xpath.put("ReservationLookup_ReservationAddress_CountryCode",
											"ReservationLookups_ReservationLookup_ReservationAddress");
									xpath.put("ReservationLookup_ReservationAddress_PostalCode",
											"ReservationLookups_ReservationLookup_ReservationAddress");

									actualValues = WSClient
											.getSingleNodeList(ReservationLookupRes, xpath, false, XMLType.RESPONSE);
											
									WSClient.writeToReport(LogStatus.INFO, "<b>Validating response with Database</b>");
									WSAssert.assertEquals(db, actualValues, false);
									
									
														
									}
									else
									{WSClient.writeToReport(LogStatus.FAIL,"ReservationLookup falied----Success flag is not populated on response");}
									
									if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", true)) {
					    				/****
					    				 * Verify that the error message is populated on the
					    				 * response
					 					 ********/
					    		
					    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
					    								XMLType.RESPONSE);
					    				WSClient.writeToReport(LogStatus.INFO,
					    						"The text displayed in the response is :" + message);
					    								}
								
							}						
																			
								
								
			
						 

					} 
				} 
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                   CancelReservation.cancelReservation("DS_02");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
		}
	}
	
	@Test(groups = { "minimumRegression", "ReservationLookup", "HTNG2006", "HTNG" })
	public void ReservationLookUp_2006_20463() {
		try {
			String testName = "reservationLookUp_2006_20463";
			WSClient.startTest(testName, "Verify that reservation details are populated correctly on the response when  ConfirmationID and ResortId are passed in the ReservationLookupRequest.", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);
				
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				WSClient.setData("{var_fname}", WSClient.getKeywordData("{KEYWORD_FNAME}"));
				WSClient.setData("{var_lname}", WSClient.getKeywordData("{KEYWORD_LNAME}"));
				String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
				System.out.println("address :"+state_code+" "+state);
				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address_country :"+country_code);
				HashMap<String, String> addLOV = new HashMap<String, String>();
				
				addLOV=OPERALib.fetchAddressLOV(state,country_code);
				WSClient.setData("{var_state}", state);
				WSClient.setData("{var_country}", country_code);
				WSClient.setData("{var_addressType}",
						OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));				

				OPERALib.setOperaHeader(OPERALib.getUserName());

				

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				profileID=CreateProfile.createProfile("DS_04");
				if(!profileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", profileID);
					
						/******************* Prerequisite 2:Create a Reservation ************************/
						
							resvID=CreateReservation.createReservation("DS_01");
						if(!resvID.equals("error"))
						{
							
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
				
				
								WSClient.setData("{var_reservation_id}", resvID.get("reservationId"));
								WSClient.setData("{var_Confirmation}", resvID.get("confirmationId"));
								
								
								HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
										HTNGLib.getInterfaceFromAddress());
							
									String ReservationLookupReq = WSClient
											.createSOAPMessage("HTNG2006ReservationLookup", "DS_02");
									String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);
									if(WSAssert.assertIfElementValueEquals(ReservationLookupRes,"ReservationLookupResponse_Result_resultStatusFlag","SUCCESS", false))
									{
																
									LinkedHashMap<String, String> db = new LinkedHashMap<String, String>();

									HashMap<String, String> xpath = new HashMap<String, String>();
									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
									
									String query=WSClient.getQuery("QS_13");
									db = WSClient.getDBRow(query);
									xpath.put("ReservationLookups_ReservationLookup_ReservationID",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookups_ReservationLookup_ConfirmationNo",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookups_ReservationLookup_ResortId",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									

									actualValues = WSClient
											.getSingleNodeList(ReservationLookupRes, xpath, false, XMLType.RESPONSE);
											
									WSClient.writeToReport(LogStatus.INFO, "<b>Validating response with Database</b>");
									WSAssert.assertEquals(db, actualValues, false);
									
									
														
									}
									else
									{WSClient.writeToReport(LogStatus.FAIL,"ReservationLookup falied----Success flag is not populated on response");}
									
									if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", true)) {
					    				/****
					    				 * Verify that the error message is populated on the
					    				 * response
					 					 ********/
					    		
					    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
					    								XMLType.RESPONSE);
					    				WSClient.writeToReport(LogStatus.INFO,
					    						"The text displayed in the response is :" + message);
					    								}
								
							}						
																			
								
								
			
						 

					} 
				} 
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                   CancelReservation.cancelReservation("DS_02");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
		}
	}
	
	@Test(groups = { "minimumRegression", "ReservationLookup", "HTNG2006", "HTNG" })
	public void ReservationLookUp_2006_20462() {
		try {
			String testName = "reservationLookUp_2006_20462";
			WSClient.startTest(testName, "Verify that reservation details are populated correctly on the response when FirstName & LastName and ResortId are passed in the ReservationLookupRequest.", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				
				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				
				String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_fname}", firstName);
				WSClient.setData("{var_lname}", lastName);
				

				OPERALib.setOperaHeader(OPERALib.getUserName());

				
				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				profileID=CreateProfile.createProfile("DS_06");
				if(!profileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", profileID);
					
						/******************* Prerequisite 2:Create a Reservation ************************/
						
							resvID=CreateReservation.createReservation("DS_01");
						if(!resvID.equals("error"))
						{
							
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
				
				
				
													
								HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
										HTNGLib.getInterfaceFromAddress());

								String ReservationLookupReq = WSClient
											.createSOAPMessage("HTNG2006ReservationLookup", "DS_03");
									String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);
									
									if(WSAssert.assertIfElementValueEquals(ReservationLookupRes,"ReservationLookupResponse_Result_resultStatusFlag","SUCCESS", false))
									{
										List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

										HashMap<String, String> xpath = new HashMap<String, String>();
										List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
										
										WSClient.writeToReport(LogStatus.INFO,"Query to fetch reservations with their first Name and Last name ");
										String query=WSClient.getQuery("QS_03");
										db = WSClient.getDBRows(query);

										xpath.put("ReservationLookups_ReservationLookup_ReservationID",
												"ReservationLookupResponse_ReservationLookups_ReservationLookup");

										xpath.put("ReservationLookup_ProfileInfo_FirstName",
												"ReservationLookupResponse_ReservationLookups_ReservationLookup");

										xpath.put("ReservationLookup_ProfileInfo_LastName",
												"ReservationLookupResponse_ReservationLookups_ReservationLookup");

										actualValues = WSClient.getMultipleNodeList(ReservationLookupRes, xpath, false,
												XMLType.RESPONSE);
										// Validating db values with response
										WSClient.writeToReport(LogStatus.INFO, "<b>Validating db values with response</b>");
										WSAssert.assertEquals(actualValues, db, false);
									
									
								} else{
									
									WSClient.writeToReport(LogStatus.FAIL,"ReservationLookup failed----success flag is not populated on response");
								}
									if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", true)) {
					    				/****
					    				 * Verify that the error message is populated on the
					    				 * response
					 					 ********/
					    		
					    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
					    								XMLType.RESPONSE);
					    				WSClient.writeToReport(LogStatus.INFO,
					    						"The text displayed in the response is :" + message);
					    								}
							
							}						
							
							
						

					}
				} 
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                   CancelReservation.cancelReservation("DS_02");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
		}
	}

	

	
	@Test(groups = { "minimumRegression", "ReservationLookup", "HTNG2006", "HTNG" })
	public void ReservationLookUp_2006_38835() {
		try {
			String testName = "reservationLookUp_2006_38835";
			WSClient.startTest(testName, "Verify that the  reservationLookup  is successful for a given zip", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				
				String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_fname}", firstName);
				WSClient.setData("{var_lname}", lastName);
				String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
				System.out.println("address :"+state_code+" "+state);
				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address_country :"+country_code);
				HashMap<String, String> addLOV = new HashMap<String, String>();
				
				addLOV=OPERALib.fetchAddressLOV(state,country_code);
				WSClient.setData("{var_state}", state);
				WSClient.setData("{var_country}", country_code);
				WSClient.setData("{var_addressType}",
						OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));

			

				OPERALib.setOperaHeader(OPERALib.getUserName());

				

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				profileID=CreateProfile.createProfile("DS_04");
				if(!profileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", profileID);
					
						/******************* Prerequisite 2:Create a Reservation ************************/
						
							resvID=CreateReservation.createReservation("DS_01");
						if(!resvID.equals("error"))
						{
							
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							
				
				
													
								HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
										HTNGLib.getInterfaceFromAddress());
								String ReservationLookupReq = WSClient
											.createSOAPMessage("HTNG2006ReservationLookup", "DS_06");
									String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);
									
									if(WSAssert.assertIfElementValueEquals(ReservationLookupRes,"ReservationLookupResponse_Result_resultStatusFlag","SUCCESS", false))
									{
										List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

										HashMap<String, String> xpath = new HashMap<String, String>();
										List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
										
										WSClient.writeToReport(LogStatus.INFO,"Query to fetch reservations with their Zipcode ");
										String query=WSClient.getQuery("QS_06");
										db = WSClient.getDBRows(query);
								
										xpath.put("ReservationLookups_ReservationLookup_ReservationID",
																	"ReservationLookupResponse_ReservationLookups_ReservationLookup");
								
										xpath.put("ReservationLookup_ReservationAddress_PostalCode",
																				"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									
										actualValues = WSClient.getMultipleNodeList(ReservationLookupRes, xpath, false,
												XMLType.RESPONSE);
										// Validating db values with response
										WSClient.writeToReport(LogStatus.INFO, "<b>Validating db values with response</b>");
										
										WSAssert.assertEquals(actualValues, db, false);
									
									
								} else{
									
									WSClient.writeToReport(LogStatus.FAIL,"ReservationLookup failed----success flag is not populated on response");
								}
									if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", true)) {
					    				/****
					    				 * Verify that the error message is populated on the
					    				 * response
					 					 ********/
					    		
					    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
					    								XMLType.RESPONSE);
					    				WSClient.writeToReport(LogStatus.INFO,
					    						"The text displayed in the response is :" + message);
					    								}
							}						
							
							
						

					} 
				} 
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                   CancelReservation.cancelReservation("DS_02");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
		}
	}
	
	@Test(groups = { "minimumRegression", "ReservationLookup", "HTNG2006", "HTNG" })
	public void ReservationLookUp_2006_20464() {
		try {
			String testName = "reservationLookUp_2006_20464";
			WSClient.startTest(testName, "Verify that the  reservationLookup  is successful for a given country", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				
				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				
				String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_fname}", firstName);
				WSClient.setData("{var_lname}", lastName);
				String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
				System.out.println("address :"+state_code+" "+state);
				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address_country :"+country_code);
				HashMap<String, String> addLOV = new HashMap<String, String>();
				
				addLOV=OPERALib.fetchAddressLOV(state,country_code);
				WSClient.setData("{var_state}", state);
				WSClient.setData("{var_country}", country_code);
				WSClient.setData("{var_addressType}",
						OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));
				WSClient.setData("{var_ext_country}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "COUNTRY_CODE", country_code));

				OPERALib.setOperaHeader(OPERALib.getUserName());

				

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				profileID=CreateProfile.createProfile("DS_04");
				if(!profileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", profileID);
					
						/******************* Prerequisite 2:Create a Reservation ************************/
						
							resvID=CreateReservation.createReservation("DS_01");
						if(!resvID.equals("error"))
						{
							
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
				
				
													
								HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
										HTNGLib.getInterfaceFromAddress());
								String ReservationLookupReq = WSClient
											.createSOAPMessage("HTNG2006ReservationLookup", "DS_09");
									String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);
									
									if(WSAssert.assertIfElementValueEquals(ReservationLookupRes,"ReservationLookupResponse_Result_resultStatusFlag","SUCCESS", false))
									{
										List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

										HashMap<String, String> xpath = new HashMap<String, String>();
										List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
										
										WSClient.writeToReport(LogStatus.INFO,"Query to fetch reservations with their Country ");
										String query=WSClient.getQuery("QS_09");
										db = WSClient.getDBRows(query);
								
										xpath.put("ReservationLookups_ReservationLookup_ReservationID",
																	"ReservationLookupResponse_ReservationLookups_ReservationLookup");
								
										xpath.put("ReservationLookup_ReservationAddress_CountryCode",
																				"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									
										actualValues = WSClient.getMultipleNodeList(ReservationLookupRes, xpath, false,
												XMLType.RESPONSE);
										// Validating db values with response
										WSClient.writeToReport(LogStatus.INFO, "<b>Validating db values with response</b>");
										
										WSAssert.assertEquals(actualValues, db, false);
									
									
								} else{
									
									WSClient.writeToReport(LogStatus.FAIL,"ReservationLookup failed----success flag is not populated on response");
								}
									if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", true)) {
					    				/****
					    				 * Verify that the error message is populated on the
					    				 * response
					 					 ********/
					    		
					    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
					    								XMLType.RESPONSE);
					    				WSClient.writeToReport(LogStatus.INFO,
					    						"The text displayed in the response is :" + message);
					    								}
							}						
							
							
						

					} 
				} 
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                   CancelReservation.cancelReservation("DS_02");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
		}
	}
	
	
	@Test(groups = { "minimumRegression", "ReservationLookup", "HTNG2006", "HTNG" })
	public void ReservationLookUp_2006_38833() {
		try {
			String testName = "reservationLookUp_2006_38833";
			WSClient.startTest(testName, "Verify that the  reservationLookup  is successful for a given city", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				
				String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_fname}", firstName);
				WSClient.setData("{var_lname}", lastName);
				String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
				System.out.println("address :"+state_code+" "+state);
				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address_country :"+country_code);
				HashMap<String, String> addLOV = new HashMap<String, String>();
				
				addLOV=OPERALib.fetchAddressLOV(state,country_code);
				WSClient.setData("{var_state}", state);
				WSClient.setData("{var_country}", country_code);
				WSClient.setData("{var_addressType}",
						OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", addLOV.get("City"));
				WSClient.setData("{var_ext_state}", state_code);
				WSClient.setData("{var_ext_country}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "COUNTRY_CODE", country_code));

				OPERALib.setOperaHeader(OPERALib.getUserName());

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				profileID=CreateProfile.createProfile("DS_04");
				if(!profileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", profileID);
					
						/******************* Prerequisite 2:Create a Reservation ************************/
						
							resvID=CreateReservation.createReservation("DS_01");
						if(!resvID.equals("error"))
						{
							
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
				
				
													
								String ReservationLookupReq = WSClient
											.createSOAPMessage("HTNG2006ReservationLookup", "DS_04");
									String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);
									
									if(WSAssert.assertIfElementValueEquals(ReservationLookupRes,"ReservationLookupResponse_Result_resultStatusFlag","SUCCESS", false))
									{
										List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

										HashMap<String, String> xpath = new HashMap<String, String>();
										List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
										
										WSClient.writeToReport(LogStatus.INFO,"Query to fetch reservations with their city ");
										String query=WSClient.getQuery("QS_04");
										db = WSClient.getDBRows(query);
								
										xpath.put("ReservationLookups_ReservationLookup_ReservationID",
																	"ReservationLookupResponse_ReservationLookups_ReservationLookup");
								
										xpath.put("ReservationLookup_ReservationAddress_CityName",
																				"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									
										actualValues = WSClient.getMultipleNodeList(ReservationLookupRes, xpath, false,
												XMLType.RESPONSE);
										// Validating db values with response
										WSClient.writeToReport(LogStatus.INFO, "<b>Validating db values with response</b>");
										
										WSAssert.assertEquals(actualValues, db, false);
									
									
								} else{
									
									WSClient.writeToReport(LogStatus.FAIL,"ReservationLookup failed----success flag is not populated on response");
								}
									if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", true)) {
					    				/****
					    				 * Verify that the error message is populated on the
					    				 * response
					 					 ********/
					    		
					    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
					    								XMLType.RESPONSE);
					    				WSClient.writeToReport(LogStatus.INFO,
					    						"The text displayed in the response is :" + message);
					    								}
							}						
							
							
						 

					}
				} 
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                   CancelReservation.cancelReservation("DS_02");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
		}
	}
	
	@Test(groups = { "minimumRegression", "ReservationLookup", "HTNG2006", "HTNG" })
		public void ReservationLookUp_2006_20461() {
			try {
				String testName = "reservationLookUp_2006_20461";
				WSClient.startTest(testName, "Verify that the  reservationLookup  is successful for a given Date Range",
						"minimumRegression");
				if (OperaPropConfig
						.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
					String interfaceName = HTNGLib.getHTNGInterface();
					String resortOperaValue = OPERALib.getResort();
					String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

					WSClient.setData("{var_profileSource}", interfaceName);

					WSClient.setData("{var_resort}", resortOperaValue);
					WSClient.setData("{var_extResort}", resortExtValue);
					String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
					String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
					WSClient.setData("{var_fname}", firstName);
					WSClient.setData("{var_lname}", lastName);
					


					String start = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}");
					String end = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_3}");
					WSClient.setData("{var_start}", start);
					WSClient.setData("{var_end}", end);

					String NEW_FORMAT = "dd-MMM-yyyy";
					String OLD_FORMAT = "yyyy-MM-dd";

					SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
					Date d1 = sdf.parse(start);
					Date d2 = sdf.parse(end);

					sdf.applyPattern(NEW_FORMAT);
					String start1 = sdf.format(d1);
					String end1 = sdf.format(d2);
					System.out.println(start1);
					WSClient.setData("{var_pms_start}", start1);
					WSClient.setData("{var_pms_end}", end1);
					

					OPERALib.setOperaHeader(OPERALib.getUserName());

					

					/************
					 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
					 * Code
					 *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					
					profileID=CreateProfile.createProfile("DS_05");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						
							/******************* Prerequisite 2:Create a Reservation ************************/
							
								resvID=CreateReservation.createReservation("DS_01");
							if(!resvID.equals("error"))
							{
								
								WSClient.setData("{var_resvId}", resvID.get("reservationId"));
					

								HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
										HTNGLib.getInterfaceFromAddress());
								String ReservationLookupReq = WSClient.createSOAPMessage("HTNG2006ReservationLookup",
										"DS_07");
								String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);

								if (WSAssert.assertIfElementValueEquals(ReservationLookupRes,
										"ReservationLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

									HashMap<String, String> xpath = new HashMap<String, String>();
									List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

									WSClient.writeToReport(LogStatus.INFO, "Query to fetch reservations with their Date Range ");
									String query=WSClient.getQuery("QS_07");
									db = WSClient.getDBRows(query);

									xpath.put("ReservationLookups_ReservationLookup_ReservationID",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookup_DateRange_Start",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookup_DateRange_End",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									
									actualValues=WSClient.getMultipleNodeList(ReservationLookupRes, xpath, false, XMLType.RESPONSE);
									// Validating db values with response
									WSClient.writeToReport(LogStatus.INFO, "<b>Validating db values with response</b>");
									
									WSAssert.assertEquals(actualValues, db, false);

								} else {

									WSClient.writeToReport(LogStatus.FAIL,
											"ReservationLookup failed----success flag is not populated on response");
								}
								if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", true)) {
				    				/****
				    				 * Verify that the error message is populated on the
				    				 * response
				 					 ********/
				    		
				    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
				    								XMLType.RESPONSE);
				    				WSClient.writeToReport(LogStatus.INFO,
				    						"The text displayed in the response is :" + message);
				    								}
							}

							

						} 
					} 
				 else {
					WSClient.writeToReport(LogStatus.WARNING,
							"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
				}
			}
	 
		catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			} finally {
				 try {
		                if(!WSClient.getData("{var_resvId}").equals(""))
		                   CancelReservation.cancelReservation("DS_02");
		               } catch (Exception e) {
		                   
		                   e.printStackTrace();
		               }
			}
		}

	

	@Test(groups = { "minimumRegression", "ReservationLookup", "HTNG2006", "HTNG" })
	public void ReservationLookup_2006_20459() {
		try {
			String testName = "reservationLookup_2006_20459";
			WSClient.startTest(testName, "Verify that the  reservationLookup  is successful for a given Profile ID", "minimumRegression");

			String interfaceName = HTNGLib.getHTNGInterface();
			String resortOperaValue = OPERALib.getResort();
			String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
		


			WSClient.setData("{var_profileSource}", interfaceName);
		
			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_extResort}", resortExtValue);
			
			OPERALib.setOperaHeader(OPERALib.getUserName());
			
								
			
			/************
			 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
			 * Code
			 *********************************/
			WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
			WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
			WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
			WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));
			
			
			/************ Prerequisite 1: Create Profile ****************/
			profileID=CreateProfile.createProfile("DS_01");
			if(!profileID.equals("error"))
			{
				WSClient.setData("{var_profileId}", profileID);
				
					/******************* Prerequisite 2:Create a Reservation ************************/
					
						resvID=CreateReservation.createReservation("DS_01");
					if(!resvID.equals("error"))
					{
						
						WSClient.setData("{var_resvId}", resvID.get("reservationId"));
			
			
						/******************
						 * Prerequisite 3:Create a Reservation
						 ************************/
						resvID=CreateReservation.createReservation("DS_09");
											


									/********
									 * Invoke operation to be validated:
									 * Reservation Look Up reservations
									 **************/
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
								HTNGLib.getInterfaceFromAddress());
									String ReservationLookupReq = WSClient
											.createSOAPMessage("HTNG2006ReservationLookup", "DS_08");
									String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);
									if (WSAssert.assertIfElementValueEquals(ReservationLookupRes,
											"ReservationLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
									List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

									HashMap<String, String> xpath = new HashMap<String, String>();
									List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

									xpath.put("ReservationLookups_ReservationLookup_ReservationID",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookups_ReservationLookup_ProfileID",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookups_ReservationLookup_ConfirmationNo",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");

									String query=WSClient.getQuery("QS_08");
									db = WSClient.getDBRows(query);

									actualValues = WSClient.getMultipleNodeList(ReservationLookupRes, xpath, false,
											XMLType.RESPONSE);
									// Validating db values with response
									WSAssert.assertEquals(actualValues, db, false);
									} else {
										
										WSClient.writeToReport(LogStatus.FAIL,
															"ReservationLookup failed----success flag is not populated on response");
											}
									if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", true)) {
										/****
										 * Verify that the error message is populated on the
										 * response
										 ********/
														    		
										String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
												XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO,
												"The text displayed in the response is :" + message);
														    								}
						} 
								} 
							
				
			 
		}

		catch (Exception e) {

			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} 
		
		finally{
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                   CancelReservation.cancelReservation("DS_02");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
		}
	}

	
	
	@Test(groups = { "minimumRegression", "ReservationLookup", "HTNG2006", "HTNG" })
	public void ReservationLookUp_2006_20465() {
		try {
			String testName = "reservationLookUp_2006_20465";
			WSClient.startTest(testName, "Verify that reservation details are populated correctly on the response  when FirstName and ResortId are passed in the ReservationLookupRequest.", "minimumRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				
				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				
				String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_fname}", firstName);
				WSClient.setData("{var_lname}", lastName);
				

				OPERALib.setOperaHeader(OPERALib.getUserName());

				
				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				/************ Prerequisite 1: Create Profile ****************/
				profileID=CreateProfile.createProfile("DS_06");
				if(!profileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", profileID);
					
						/******************* Prerequisite 2:Create a Reservation ************************/
						
							resvID=CreateReservation.createReservation("DS_01");
						if(!resvID.equals("error"))
						{
							
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
				
				
				
													
								HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
										HTNGLib.getInterfaceFromAddress());

								String ReservationLookupReq = WSClient
											.createSOAPMessage("HTNG2006ReservationLookup", "DS_10");
									String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);
									
									if(WSAssert.assertIfElementValueEquals(ReservationLookupRes,"ReservationLookupResponse_Result_resultStatusFlag","SUCCESS", false))
									{
										List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

										HashMap<String, String> xpath = new HashMap<String, String>();
										List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
										
										WSClient.writeToReport(LogStatus.INFO,"Query to fetch reservations with their first Name");
										String query=WSClient.getQuery("QS_12");
										db = WSClient.getDBRows(query);

										xpath.put("ReservationLookups_ReservationLookup_ReservationID",
												"ReservationLookupResponse_ReservationLookups_ReservationLookup");

										xpath.put("ReservationLookup_ProfileInfo_FirstName",
												"ReservationLookupResponse_ReservationLookups_ReservationLookup");


										actualValues = WSClient.getMultipleNodeList(ReservationLookupRes, xpath, false,
												XMLType.RESPONSE);
										// Validating db values with response
										WSClient.writeToReport(LogStatus.INFO, "<b>Validating db values with response</b>");
										WSAssert.assertEquals(actualValues, db, false);
									
									
								} else{
									
									WSClient.writeToReport(LogStatus.FAIL,"ReservationLookup failed----success flag is not populated on response");
								}
									if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", true)) {
					    				/****
					    				 * Verify that the error message is populated on the
					    				 * response
					 					 ********/
					    		
					    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
					    								XMLType.RESPONSE);
					    				WSClient.writeToReport(LogStatus.INFO,
					    						"The text displayed in the response is :" + message);
					    								}
							
							}						
							
							
						 
					} 
				} 
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                   CancelReservation.cancelReservation("DS_02");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
		}
	}
	
	@Test(groups = { "fullRegression", "ReservationLookup", "HTNG2006", "HTNG" })
	public void ReservationLookUp_2006_20472() {
		try {
			String testName = "reservationLookUp_2006_20472";
			WSClient.startTest(testName, "Verify error message response when  ResortId and Invalid Date range are passed in the ReservationLookupRequest.",
					"fullRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_fname}", firstName);
				WSClient.setData("{var_lname}", lastName);
				


				String start = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_4}");
				String end = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
				WSClient.setData("{var_start}", start);
				WSClient.setData("{var_end}", end);

				String NEW_FORMAT = "dd-MMM-yyyy";
				String OLD_FORMAT = "yyyy-MM-dd";

				SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
				Date d1 = sdf.parse(start);
				Date d2 = sdf.parse(end);

				sdf.applyPattern(NEW_FORMAT);
				String start1 = sdf.format(d1);
				String end1 = sdf.format(d2);
				System.out.println(start1);
				WSClient.setData("{var_pms_start}", start1);
				WSClient.setData("{var_pms_end}", end1);
				

				OPERALib.setOperaHeader(OPERALib.getUserName());
						
										
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
									HTNGLib.getInterfaceFromAddress());
							String ReservationLookupReq = WSClient.createSOAPMessage("HTNG2006ReservationLookup",
									"DS_07");
							String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);

							WSAssert.assertIfElementValueEquals(ReservationLookupRes,
									"ReservationLookupResponse_Result_resultStatusFlag", "SUCCESS", false);
								
							if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", false)) {
			    				/****
			    				 * Verify that the error message is populated on the
			    				 * response
			 					 ********/
			    		
			    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
			    								XMLType.RESPONSE);
			    				WSClient.writeToReport(LogStatus.INFO,
			    						"The text displayed in the response is :" + message);
			    				}
						}

				

		 
 
			else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
				}
		}
		 catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                   CancelReservation.cancelReservation("DS_02");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
		}
	}
	
	
	
	@Test(groups = { "targetedRegression", "ReservationLookup", "HTNG2006", "HTNG" })
	public void ReservationLookUp_2006_20471() {
		try {
			String testName = "reservationLookUp_2006_20471";
			WSClient.startTest(testName, "Verify that reservation details are populated correctly on the response  when LastName and ResortId are passed in the ReservationLookupRequest.", "targetedRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				
				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				
				String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_fname}", firstName);
				WSClient.setData("{var_lname}", lastName);
				

				OPERALib.setOperaHeader(OPERALib.getUserName());

				
				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				/************ Prerequisite 1: Create Profile ****************/
				profileID=CreateProfile.createProfile("DS_06");
				if(!profileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", profileID);
					
						/******************* Prerequisite 2:Create a Reservation ************************/
						
							resvID=CreateReservation.createReservation("DS_01");
						if(!resvID.equals("error"))
						{
							
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
				
				
													
								HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
										HTNGLib.getInterfaceFromAddress());

								String ReservationLookupReq = WSClient
											.createSOAPMessage("HTNG2006ReservationLookup", "DS_13");
									String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);
									
									if(WSAssert.assertIfElementValueEquals(ReservationLookupRes,"ReservationLookupResponse_Result_resultStatusFlag","SUCCESS", false))
									{
										List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

										HashMap<String, String> xpath = new HashMap<String, String>();
										List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
										
										WSClient.writeToReport(LogStatus.INFO,"Query to fetch reservations with their Last Name");
										String query=WSClient.getQuery("QS_14");
										db = WSClient.getDBRows(query);

										xpath.put("ReservationLookups_ReservationLookup_ReservationID",
												"ReservationLookupResponse_ReservationLookups_ReservationLookup");

										xpath.put("ReservationLookup_ProfileInfo_LastName",
												"ReservationLookupResponse_ReservationLookups_ReservationLookup");


										actualValues = WSClient.getMultipleNodeList(ReservationLookupRes, xpath, false,
												XMLType.RESPONSE);
										// Validating db values with response
										WSClient.writeToReport(LogStatus.INFO, "<b>Validating db values with response</b>");
										WSAssert.assertEquals(actualValues, db, false);
									
									
								} else{
									
									WSClient.writeToReport(LogStatus.FAIL,"ReservationLookup failed----success flag is not populated on response");
								}
									if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", true)) {
					    				/****
					    				 * Verify that the error message is populated on the
					    				 * response
					 					 ********/
					    		
					    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
					    								XMLType.RESPONSE);
					    				WSClient.writeToReport(LogStatus.INFO,
					    						"The text displayed in the response is :" + message);
					    								}
							
							}						
							
							
						 

					} 
				} 
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                   CancelReservation.cancelReservation("DS_02");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
		}
	}
	
	@Test(groups = { "targetedRegression", "ReservationLookup", "HTNG2006", "HTNG" })
	public void ReservationLookUp_2006_20473() {
		try {
			String testName = "reservationLookUp_2006_20473";
			WSClient.startTest(testName, "Verify that the  reservationLookup  is successful for a given Start Date Range",
					"targetedRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_fname}", firstName);
				WSClient.setData("{var_lname}", lastName);
				


				String start = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
				String end = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
				WSClient.setData("{var_start}", start);
				

				String NEW_FORMAT = "dd-MMM-yyyy";
				String OLD_FORMAT = "yyyy-MM-dd";

				SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
				Date d1 = sdf.parse(start);
				Date d2 = sdf.parse(end);

				sdf.applyPattern(NEW_FORMAT);
				String start1 = sdf.format(d1);
				
				
				WSClient.setData("{var_pms_start}", start1);
				

				OPERALib.setOperaHeader(OPERALib.getUserName());

				

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				profileID=CreateProfile.createProfile("DS_05");
				if(!profileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", profileID);
					
						/******************* Prerequisite 2:Create a Reservation ************************/
						
							resvID=CreateReservation.createReservation("DS_05");
						if(!resvID.equals("error"))
						{
							
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							
				
										

							HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
									HTNGLib.getInterfaceFromAddress());
							String ReservationLookupReq = WSClient.createSOAPMessage("HTNG2006ReservationLookup",
									"DS_14");
							String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);

							if (WSAssert.assertIfElementValueEquals(ReservationLookupRes,
									"ReservationLookupResponse_Result_resultStatusFlag", "SUCCESS", false)) {
								List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

								HashMap<String, String> xpath = new HashMap<String, String>();
								List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();

								WSClient.writeToReport(LogStatus.INFO, "Query to fetch reservations with their Date Range ");
								String query=WSClient.getQuery("QS_15");
								db = WSClient.getDBRows(query);

								xpath.put("ReservationLookups_ReservationLookup_ReservationID",
										"ReservationLookupResponse_ReservationLookups_ReservationLookup");
								xpath.put("ReservationLookup_DateRange_Start",
										"ReservationLookupResponse_ReservationLookups_ReservationLookup");
								
								
								actualValues=WSClient.getMultipleNodeList(ReservationLookupRes, xpath, false, XMLType.RESPONSE);
								// Validating db values with response
								WSClient.writeToReport(LogStatus.INFO, "<b>Validating db values with response</b>");
						
								WSAssert.assertEquals(actualValues, db, false);

							} else {

								WSClient.writeToReport(LogStatus.FAIL,
										"ReservationLookup failed----success flag is not populated on response");
							}
							if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", true)) {
			    				/****
			    				 * Verify that the error message is populated on the
			    				 * response
			 					 ********/
			    		
			    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
			    								XMLType.RESPONSE);
			    				WSClient.writeToReport(LogStatus.INFO,
			    						"The text displayed in the response is :" + message);
			    				}
						}

				}

		} 
 
			else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
				}
		}
		 catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                   CancelReservation.cancelReservation("DS_02");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
		}
	}
	
	@Test(groups = { "fullRegression", "ReservationLookup", "HTNG2006", "HTNG" })
	public void ReservationLookUp_2006_20469() {
		try {
			String testName = "reservationLookUp_2006_20469";
			WSClient.startTest(testName, "Verify error message response when Invalid Membership-Type and Membership-Number is passed are passed in the ReservationLookupRequest.",
					"fullRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_fname}", firstName);
				WSClient.setData("{var_lname}", lastName);
				 String member_num=WSClient.getKeywordData("{KEYWORD_RANDNUM_6}");
		   			WSClient.setData("{var_memNo}",member_num);
		   			WSClient.setData("{var_membershipType}","invalid");
											

				
						
										
						HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
									HTNGLib.getInterfaceFromAddress());
							String ReservationLookupReq = WSClient.createSOAPMessage("HTNG2006ReservationLookup",
									"DS_12");
							String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);

							WSAssert.assertIfElementValueEquals(ReservationLookupRes,
									"ReservationLookupResponse_Result_resultStatusFlag", "SUCCESS", false);
								
							if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", false)) {
			    				/****
			    				 * Verify that the error message is populated on the
			    				 * response
			 					 ********/
			    		
			    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
			    								XMLType.RESPONSE);
			    				WSClient.writeToReport(LogStatus.INFO,
			    						"The text displayed in the response is :" + message);
			    				}
						}

				

		 
 
			else {
				WSClient.writeToReport(LogStatus.WARNING,
						"The prerequisites for RateCode, RoomType, SourceCode, MarketCode failed! -----Blocked");
				}
		
		}
		 catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                   CancelReservation.cancelReservation("DS_02");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
		}
	}
		
	@Test(groups = { "fullRegression", "ReservationLookup", "HTNG2006", "HTNG" })
	public void ReservationLookUp_2006_20468() {
		try {
			String testName = "reservationLookUp_2006_20468";
			WSClient.startTest(testName, "Verifying error message response when valid reservationStatus & ResortId and invalid reservation address details are passed in the ReservationLookupRequest", "fullRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

				WSClient.setData("{var_profileSource}", interfaceName);

				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				
				String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
				String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
				WSClient.setData("{var_fname}", firstName);
				WSClient.setData("{var_lname}", lastName);
				String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
				String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
				System.out.println("address :"+state_code+" "+state);
				String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
				System.out.println("address_country :"+country_code);
				HashMap<String, String> addLOV = new HashMap<String, String>();
				
				addLOV=OPERALib.fetchAddressLOV(state,country_code);
				WSClient.setData("{var_state}", state);
				WSClient.setData("{var_country}", country_code);
				WSClient.setData("{var_addressType}",
						OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
				WSClient.setData("{var_zip}", addLOV.get("Zip"));
				WSClient.setData("{var_city}", "invalidCity");
				WSClient.setData("{var_ext_state}", "invalidState");
				WSClient.setData("{var_ext_country}", "InvalidCountry");

				OPERALib.setOperaHeader(OPERALib.getUserName());

				HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
						HTNGLib.getInterfaceFromAddress());

				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				String createProfileReq = WSClient.createSOAPMessage("CreateProfile", "DS_04");
				String createProfileResponseXML = WSClient.processSOAPMessage(createProfileReq);

				if (WSAssert.assertIfElementExists(createProfileResponseXML, "CreateProfileRS_Success", true)) {

					if (WSAssert.assertIfElementExists(createProfileResponseXML,
							"CreateProfileRS_ProfileIDList_UniqueID_ID", true)) {
						String profileId = WSClient.getElementValue(createProfileResponseXML,
								"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE);
						WSClient.setData("{var_profileId}", profileId);


						/******************
						 * Prerequisite 2:Create a Reservation
						 ************************/
						String createResvReq = WSClient.createSOAPMessage("CreateReservation", "DS_01");
						String createResvRes = WSClient.processSOAPMessage(createResvReq);											
							
											
							if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success",
									true)) {

								String reservationId = WSClient.getElementValue(createResvRes,
										"Reservation_ReservationIDList_UniqueID_ID", XMLType.RESPONSE);
								
								if (reservationId == null) {
									
									WSClient.writeToReport(LogStatus.WARNING,
											"Prerequisite failed >> Reservation ID is not created");
								}

													
								String ReservationLookupReq = WSClient
											.createSOAPMessage("HTNG2006ReservationLookup", "DS_04");
									String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);
									
									WSAssert.assertIfElementValueEquals(ReservationLookupRes,"ReservationLookupResponse_Result_resultStatusFlag","SUCCESS", false);
									
									if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", false)) {
					    				/****
					    				 * Verify that the error message is populated on the
					    				 * response
					 					 ********/
					    		
					    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
					    								XMLType.RESPONSE);
					    				WSClient.writeToReport(LogStatus.INFO,
					    						"The text displayed in the response is :" + message);
					    								}
							}						
							
						

					} 
				} 
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                   CancelReservation.cancelReservation("DS_02");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
		}
	}
	
	@Test(groups = { "targetedRegression", "ReservationLookup", "HTNG2006", "HTNG","ReservationLookUp_2006_20457"})
	public void ReservationLookUp_2006_20457() {
		try {
			String testName = "reservationLookUp_2006_20457";
			WSClient.startTest(testName, "Verifying the reservation deatils in the response when reservationStatus and ResrotId are passed in the ReservationLookupRequest for checked-in reservation", "targetedRegression");
			if (OperaPropConfig
					.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
				String interfaceName = HTNGLib.getHTNGInterface();
				String resortOperaValue = OPERALib.getResort();
				String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
				
				WSClient.setData("{var_profileSource}", interfaceName);
				
				WSClient.setData("{var_resort}", resortOperaValue);
				WSClient.setData("{var_extResort}", resortExtValue);
				 WSClient.setData("{var_payment}", OperaPropConfig.getDataSetForCode("PaymentMethod", "DS_01"));
				
				
				OPERALib.setOperaHeader(OPERALib.getUserName());

				
				/************
				 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
				 * Code
				 *********************************/
				WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
				WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
				WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
				WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

				/************
				 * Prerequisite 1: Create profile
				 *********************************/
				
				
				
				profileID=CreateProfile.createProfile("DS_01");
				if(!profileID.equals("error"))
				{
					WSClient.setData("{var_profileId}", profileID);
					
						/******************* Prerequisite 2:Create a Reservation ************************/
						
							resvID=CreateReservation.createReservation("DS_12");
						if(!resvID.equals("error"))
						{
							
							WSClient.setData("{var_resvId}", resvID.get("reservationId"));
							WSClient.setData("{var_reservation_id}", resvID.get("reservationId"));
							
								
								String roomNumber;
								String fetchHotelRoomsXML = WSClient.createSOAPMessage("FetchHotelRooms", "DS_13");
								String fetchHotelResponse = WSClient.processSOAPMessage(fetchHotelRoomsXML);
								
								if (WSAssert.assertIfElementExists(fetchHotelResponse, "FetchHotelRoomsRS_Success", true)) {

									roomNumber = WSClient.getElementValue(fetchHotelResponse,
											"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
									if (roomNumber.equals("")) {
										String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "FirstFloor");
										String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
										
										if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

											roomNumber = WSClient.getElementValue(createRoomReq,
													"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
											
											
										}
										else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Pre_requisite-Create Room is unsuccessful,unable to perform  ");
										}
									}
									WSClient.setData("{var_roomNumber}", roomNumber);
									SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
								}
								else{
									
									
										String createRoomReq = WSClient.createSOAPMessage("CreateRoom", "FirstFloor");
										String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
										
										if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true)) {

											roomNumber = WSClient.getElementValue(createRoomReq,
													"CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
											WSClient.setData("{var_roomNumber}", roomNumber);
											SetHousekeepingRoomStatus.setHousekeepingRoomstatus("DS_01");
										}
										else {
											WSClient.writeToReport(LogStatus.WARNING,
													"Pre_requisite-Create Room is unsuccessful,unable to perform  ");
										}
										
									}
									
									
								
								
									

								
									/************
									 * Operation Assign Room : Performing
									 * the Assign Room operation to the
									 * reservation created
									 ****************/

									String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", "DS_01");

									String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);
									if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success",true)) {

										/********
										 * Invoke operation to be validated:
										 * Reservation Look Up reservations
										 **************/
										String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
										String checkInRes = WSClient.processSOAPMessage(checkInReq);
										  
								if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true))
															
									 {
									String res_status = "CHECKED IN";
									String res_ExtStatus = "CHECKED_IN";

									WSClient.setData("{var_status}", res_status);
									WSClient.setData("{var_ExtStatus}", res_ExtStatus);
									WSClient.setData("{var_extResort}", resortExtValue);
									WSClient.setData("{var_resort}", resortOperaValue);

									/********
									 * Invoke operation to be validated:
									 * Reservation Look Up for Cancelled
									 * reservations
									 **************/
									HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
									HTNGLib.getInterfaceFromAddress());
									String ReservationLookupReq = WSClient
											.createSOAPMessage("HTNG2006ReservationLookup", "DS_01");
									String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);
									
									if(WSAssert.assertIfElementValueEquals(ReservationLookupRes,"ReservationLookupResponse_Result_resultStatusFlag","SUCCESS", false))
									{
										LinkedHashMap<String, String> db = new LinkedHashMap<String,String>();

									HashMap<String, String> xpath = new HashMap<String, String>();
									LinkedHashMap<String, String> actualValues = new LinkedHashMap<String, String>();
									WSClient.writeToReport(LogStatus.INFO,"<b>Query to fetch reservation Lookup details from DataBase</b>");
									String query=WSClient.getQuery("QS_11");
									db = WSClient.getDBRow(query);

									xpath.put("ReservationLookups_ReservationLookup_ReservationID",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookups_ReservationLookup_ProfileID",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put(
											"ReservationLookupResponse_ReservationLookups_ReservationLookup_reservationStatus",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookups_ReservationLookup_ConfirmationNo",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");
									xpath.put("ReservationLookups_ReservationLookup_ResortId",
											"ReservationLookupResponse_ReservationLookups_ReservationLookup");

									actualValues = WSClient.getSingleNodeList(ReservationLookupRes, xpath, false,
											XMLType.RESPONSE);
									
																	
									WSClient.writeToReport(LogStatus.INFO,"<b>Validating the response with DataBase</b>");
									WSAssert.assertEquals(actualValues, db, false);
									
									}
									else
									{
										WSClient.writeToReport(LogStatus.FAIL,"Reservation Lookup failed--------->>>>Success flag doesnt exist");
									}
									if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", true)) {
					    				/****
					    				 * Verify that the error message is populated on the
					    				 * response
					 					 ********/
					    		
					    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
					    								XMLType.RESPONSE);
					    				WSClient.writeToReport(LogStatus.INFO,
					    						"The text displayed in the response is :" + message);
					    								}

								} 
								else{
									WSClient.writeToReport(LogStatus.WARNING,
											"Pre_requisite-Check-In,unable to perform ");
								}
								}
									else {
										WSClient.writeToReport(LogStatus.WARNING,
												"Pre_requisite-Assign Room is unsuccessful,unable to perform ");
									}
								
							}
							
													
								

						}					} 
				 
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		} finally {
			 try {
	                if(!WSClient.getData("{var_resvId}").equals(""))
	                	CheckoutReservation.checkOutReservation("DS_01");
	               } catch (Exception e) {
	                   
	                   e.printStackTrace();
	               }
		}
		}
		
		@Test(groups = { "fullRegression", "ReservationLookup", "HTNG2006", "HTNG" })
		public void ReservationLookUp_2006_20467() {
			try {
				String testName = "reservationLookUp_2006_20467";
				WSClient.startTest(testName, "Verifying error messages in the response when valid reservationStatus & ResortId, invalid FirstName & LastName are passed in the request", "fullRegression");
				if (OperaPropConfig
						.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
					String interfaceName = HTNGLib.getHTNGInterface();
					String resortOperaValue = OPERALib.getResort();
					String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);
					
					WSClient.setData("{var_profileSource}", interfaceName);

					WSClient.setData("{var_resort}", resortOperaValue);
					WSClient.setData("{var_extResort}", resortExtValue);
					
					String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
					String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
					WSClient.setData("{var_fname}", firstName);
					WSClient.setData("{var_lname}", lastName);
					

					OPERALib.setOperaHeader(OPERALib.getUserName());

					
					/************
					 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
					 * Code
					 *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					profileID=CreateProfile.createProfile("DS_06");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						
							/******************* Prerequisite 2:Create a Reservation ************************/
							
								resvID=CreateReservation.createReservation("DS_01");
							if(!resvID.equals("error"))
							{
								
								WSClient.setData("{var_resvId}", resvID.get("reservationId"));		
							
					

							
									HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
											HTNGLib.getInterfaceFromAddress());
									WSClient.setData("{var_fname}", "invalidbhal");
									WSClient.setData("{var_lname}", "invalidbhal");

									String ReservationLookupReq = WSClient
												.createSOAPMessage("HTNG2006ReservationLookup", "DS_03");
										String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);
										
										WSAssert.assertIfElementValueEquals(ReservationLookupRes,"ReservationLookupResponse_Result_resultStatusFlag","SUCCESS", false);
										
										if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", false)) {
						    				/****
						    				 * Verify that the error message is populated on the
						    				 * response
						 					 ********/
						    		
						    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
						    								XMLType.RESPONSE);
						    				WSClient.writeToReport(LogStatus.INFO,
						    						"The text displayed in the response is :" + message);
						    								}
								
								}						
								
								
							 

						} 
					} 
				
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			} finally {
				 try {
		                if(!WSClient.getData("{var_resvId}").equals(""))
		                   CancelReservation.cancelReservation("DS_02");
		               } catch (Exception e) {
		                   
		                   e.printStackTrace();
		               }
			}
		}
		
		@Test(groups = { "minimumRegression", "ReservationLookup", "HTNG2006", "HTNG" })
		public void ReservationLookUp_2006_38834() {
			try {
				String testName = "reservationLookUp_2006_38834";
				WSClient.startTest(testName, "Verify that reservation details are populated correctly on the response  when StateProv and ResortId are passed in the ReservationLookupRequest.", "minimumRegression");
				if (OperaPropConfig
						.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode" })) {
					String interfaceName = HTNGLib.getHTNGInterface();
					String resortOperaValue = OPERALib.getResort();
					String resortExtValue = HTNGLib.getExtResort(resortOperaValue, interfaceName);

					WSClient.setData("{var_profileSource}", interfaceName);
					
					WSClient.setData("{var_resort}", resortOperaValue);
					WSClient.setData("{var_extResort}", resortExtValue);
					
					String firstName = WSClient.getKeywordData("{KEYWORD_FNAME}");
					String lastName = WSClient.getKeywordData("{KEYWORD_LNAME}");
					WSClient.setData("{var_fname}", firstName);
					WSClient.setData("{var_lname}", lastName);
					String state_code = HTNGLib.getRandomExtValue(resortOperaValue,interfaceName,"STATE");
					String state = HTNGLib.getPmsValue(resortOperaValue,interfaceName,"STATE",state_code);
					System.out.println("address :"+state_code+" "+state);
					String country_code = HTNGLib.getMasterValue(resortOperaValue, interfaceName, "STATE", state_code);
					System.out.println("address_country :"+country_code);
					HashMap<String, String> addLOV = new HashMap<String, String>();
					
					addLOV=OPERALib.fetchAddressLOV(state,country_code);
					WSClient.setData("{var_state}", state);
					WSClient.setData("{var_country}", country_code);
					WSClient.setData("{var_addressType}",
							OperaPropConfig.getDataSetForCode("AddressType", "DS_01"));
					WSClient.setData("{var_zip}", addLOV.get("Zip"));
					WSClient.setData("{var_city}", addLOV.get("City"));
					WSClient.setData("{var_ext_state}", state_code);
					WSClient.setData("{var_ext_country}", HTNGLib.getExtValue(resortOperaValue, interfaceName, "COUNTRY_CODE", country_code));

					OPERALib.setOperaHeader(OPERALib.getUserName());

					

					/************
					 * Prerequisite : Room type, Rate Plan Code, Source Code, Market
					 * Code
					 *********************************/
					WSClient.setData("{VAR_RATEPLANCODE}", OperaPropConfig.getDataSetForCode("RateCode", "DS_01"));
					WSClient.setData("{VAR_ROOMTYPE}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
					WSClient.setData("{var_sourceCode}", OperaPropConfig.getDataSetForCode("SourceCode", "DS_01"));
					WSClient.setData("{VAR_MARKETCODE}", OperaPropConfig.getDataSetForCode("MarketCode", "DS_01"));

					/************
					 * Prerequisite 1: Create profile
					 *********************************/
					profileID=CreateProfile.createProfile("DS_04");
					if(!profileID.equals("error"))
					{
						WSClient.setData("{var_profileId}", profileID);
						
							/******************* Prerequisite 2:Create a Reservation ************************/
							
								resvID=CreateReservation.createReservation("DS_01");
							if(!resvID.equals("error"))
							{
								
								WSClient.setData("{var_resvId}", resvID.get("reservationId"));
								
					
												
														
									HTNGLib.setHTNGHeader(OPERALib.getUserName(), OPERALib.getPassword(),
											HTNGLib.getInterfaceFromAddress());
									String ReservationLookupReq = WSClient
												.createSOAPMessage("HTNG2006ReservationLookup", "DS_05");
										String ReservationLookupRes = WSClient.processSOAPMessage(ReservationLookupReq);
										
										if(WSAssert.assertIfElementValueEquals(ReservationLookupRes,"ReservationLookupResponse_Result_resultStatusFlag","SUCCESS", false))
										{
											List<LinkedHashMap<String, String>> db = new ArrayList<LinkedHashMap<String, String>>();

											HashMap<String, String> xpath = new HashMap<String, String>();
											List<LinkedHashMap<String, String>> actualValues = new ArrayList<LinkedHashMap<String, String>>();
											
											WSClient.writeToReport(LogStatus.INFO,"Query to fetch reservations with their State ");
											String query=WSClient.getQuery("QS_05");
											db = WSClient.getDBRows(query);

											xpath.put("ReservationLookups_ReservationLookup_ReservationID",
													"ReservationLookupResponse_ReservationLookups_ReservationLookup");

											xpath.put("ReservationLookup_ReservationAddress_StateProv",
													"ReservationLookupResponse_ReservationLookups_ReservationLookup");

											actualValues = WSClient.getMultipleNodeList(ReservationLookupRes, xpath, false,
													XMLType.RESPONSE);
											// Validating db values with response
											WSClient.writeToReport(LogStatus.INFO, "<b>Validating db values with response</b>");
											
											WSAssert.assertEquals(actualValues, db, false);
										
										
									} else{
										
										WSClient.writeToReport(LogStatus.FAIL,"ReservationLookup failed----success flag is not populated on response");
									}
										if (WSAssert.assertIfElementExists(ReservationLookupRes, "Result_Text_TextElement", true)) {
						    				/****
						    				 * Verify that the error message is populated on the
						    				 * response
						 					 ********/
						    		
						    				String message = WSAssert.getElementValue(ReservationLookupRes, "Result_Text_TextElement",
						    								XMLType.RESPONSE);
						    				WSClient.writeToReport(LogStatus.INFO,
						    						"The text displayed in the response is :" + message);
						    								}
								}						
								
								
							 

						} 
					} 
				
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			} finally {
				 try {
		                if(!WSClient.getData("{var_resvId}").equals(""))
		                   CancelReservation.cancelReservation("DS_02");
		               } catch (Exception e) {
		                   
		                   e.printStackTrace();
		               }
			}
		}
	}
	

	



