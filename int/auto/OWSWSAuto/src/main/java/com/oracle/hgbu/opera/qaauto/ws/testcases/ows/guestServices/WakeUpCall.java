package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.guestServices;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CancelReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.ChangeApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CheckoutReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.transactions.FetchApplicationParameters;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class WakeUpCall extends WSSetUp {

	// Total Test Cases : 13

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

	//Fetch Hotel Rooms
	public void fetchHotelRooms(String ds)
	{
		String roomNumber;
		try
		{
			String resortOperaValue = OPERALib.getResort();
			String chain=OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",chain);
			WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_04"));
			WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_04"));

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Fetching available Hotel Rooms to assign a room"+"</b>");
			String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", ds);
			String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);
			if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true))
			{
				if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true))
				{
					List<LinkedHashMap<String,String>> rooms = new ArrayList<LinkedHashMap<String,String>>();
					LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
					xPath.put("FetchHotelRoomsRS_HotelRooms_Room", "FetchHotelRoomsRS_HotelRooms");
					xPath.put("FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", "FetchHotelRoomsRS_HotelRooms_Room");
					rooms = WSClient.getMultipleNodeList(fetchHotelRoomsRes, xPath, false, XMLType.RESPONSE);
					int len  = rooms.size();

					Random randomNum = new Random();
					int num = randomNum.nextInt(len);

					//roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);

					roomNumber = rooms.get(num).get("RoomNumber1");
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully Fetched Room to assign, room Number is: "+roomNumber+"</b>");
					WSClient.setData("{var_roomNumber}", roomNumber);
					setHousekeepingRoomStatus("DS_01");
					boolean assignFlag = assignRoom("DS_01");
					if(assignFlag == false)
					{
						WSClient.writeToReport(LogStatus.INFO, "<b>"+"Unable to assign the fetched Room so Preparing to create a new Room"+"</b>");
						/************
						 * Prerequisite : Creating a room to assign
						 ****************/
						roomNumber = createRoom("FirstFloor");
						WSClient.setData("{var_roomNumber}", roomNumber);

						/************
						 * Prerequisite : Changing the room
						 * status to inspected and then ASSIGN the
						 * room for release room
						 ****************/
						setHousekeepingRoomStatus("DS_01");
						assignRoom("DS_01");
					}
				}
				else
				{
					/************
					 * Prerequisite : Creating a room to assign
					 ****************/
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"No Room available so Preparing to create a new Room"+"</b>");
					roomNumber = createRoom("FirstFloor");
					WSClient.setData("{var_roomNumber}", roomNumber);

					/************
					 * Prerequisite : Changing the room
					 * status to inspected and then ASSIGN the
					 * room for release room
					 ****************/
					setHousekeepingRoomStatus("DS_01");
					assignRoom("DS_01");
				}
			}
			else
			{
				WSClient.writeToReport(LogStatus.INFO, "Fetching Room Unsuccessful, creating a room now");

				/************
				 * Prerequisite : Creating a room to assign
				 ****************/
				roomNumber = createRoom("FirstFloor");
				WSClient.setData("{var_roomNumber}", roomNumber);

				/************
				 * Prerequisite : Changing the room
				 * status to inspected and then ASSIGN the
				 * room for release room
				 ****************/
				setHousekeepingRoomStatus("DS_01");
				assignRoom("DS_01");
			}
		}
		catch(Exception e)
		{
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
	}

	//Creating Room to assign
	public String createRoom(String ds)
	{
		String roomNumber="";
		try
		{
			String resortOperaValue = OPERALib.getResort();
			String chain=OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",chain);
			WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_04"));

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Creating a Room to assign to the reservation"+"</b>");
			String createRoomReq = WSClient.createSOAPMessage("CreateRoom", ds);
			String createRoomRes = WSClient.processSOAPMessage(createRoomReq);
			if (WSAssert.assertIfElementExists(createRoomRes, "CreateRoomRS_Success", true))
			{

				roomNumber = WSClient.getElementValue(createRoomReq, "CreateRoomRQ_Room_RoomDetails_RoomNumber", XMLType.REQUEST);
				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully created Room, room number is:"+roomNumber+"</b>");
				WSClient.setData("{var_roomNumber}", roomNumber);
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING, "Not able to create Room");
			}

		}
		catch(Exception ex)
		{
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		}

		return roomNumber;
	}

	//Setting Housekeeping Room Status and calling Assign Room
	public void setHousekeepingRoomStatus(String ds)
	{
		try
		{
			String resortOperaValue = OPERALib.getResort();
			String chain=OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",chain);


			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Changing HouseKeeping status"+"</b>");
			String setHousekeepingRoomStatusReq = WSClient.createSOAPMessage("SetHousekeepingRoomStatus", ds);
			String setHousekeepingRoomStatusRes = WSClient.processSOAPMessage(setHousekeepingRoomStatusReq);
			if (WSAssert.assertIfElementExists(setHousekeepingRoomStatusRes, "SetHousekeepingRoomStatusRS_Success", true))
			{
				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully changed room status to vacant and inspected"+"</b>");
				//assignRoom("DS_01");
			}
			else
			{
				WSClient.writeToReport(LogStatus.WARNING, "SetHousekeepingRoomStatus blocked");
			}

		}
		catch(Exception ex)
		{
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		}
	}

	//Assigning Room
	public boolean assignRoom(String ds)
	{
		try
		{
			String resortOperaValue = OPERALib.getResort();
			String chain=OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",chain);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Assigning the Room"+"</b>");
			String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", ds);
			String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);
			if(WSAssert.assertIfElementExists(assignRoomRes,"AssignRoomRS_Success",true))
			{
				WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully Assigned Room"+"</b>");

				return true;
			}
			else
			{

				return false;
			}

		}
		catch(Exception ex)
		{
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		}
		return false;
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

	public void setOperaHeader() {
		try {
			String resortOperaValue = OPERALib.getResort();
			String chain=OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}",chain);


			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Opera Header not set.");
		}
	}

	// Sanity Test Case :1
	//						@Test(groups={"minimumRegression","GuestServices","WakeUpCall","OWS"})
	//						public void wakeUpCall_60018() {
	//						String previousValue="Y";
	//					try {
	//						String testname = "wakeUpCall_60018";
	//						WSClient.startTest(testname, "Verify that wakeup call is associated to a reservation by passing valid values in mandatory fields with channel where channel and carrier name are different", "minimumRegression");
	//
	//						setOperaHeader();
	//
	//						// Setting WAKEUP Parameter
	//						WSClient.setData("{var_parameter}", "WAKEUP");
	//			            WSClient.setData("{var_settingValue}", "Y");
	//			            String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
	//			            previousValue=Parameter1;
	//			            if (!Parameter1.equals("Y")) {
	//			                Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
	//			            }
	//
	//						if (OperaPropConfig.getPropertyConfigResults(
	//			                    new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
	//
	//							String UniqueId,resvID;
	//
	//							// Creating a profile
	//							if(!(UniqueId = createProfile("DS_01")).equals("error"))
	//							{
	//								// Creating reservation for above created profile
	//								if(!(resvID = createReservation("DS_12")).equals("error"))
	//						        {
	//									System.out.println(UniqueId);
	//									WSClient.setData("{var_profileId}", UniqueId);
	//									WSClient.setData("{var_resvId}", resvID);
	//									WSClient.setData("{var_reservation_id}", resvID);
	//
	//									// Fetching and assigning room to reservation
	//									fetchHotelRooms("DS_13");
	//									String resort = OPERALib.getResort();
	//									WSClient.setData("{var_owsresort}", resort);
	//
	//									// Checking In Reservation
	//									String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
	//									String checkInRes = WSClient.processSOAPMessage(checkInReq);
	//
	//									if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){
	//
	//										WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");
	//
	//
	//
	//										resort = OPERALib.getResort();
	//										String channel = OWSLib.getChannel(3);
	//										OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resort, OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
	//										WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
	//
	//										// Setting variables for WakeUpCall
	//							            WSClient.setData("{var_fromDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
	//							            WSClient.setData("{var_toDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
	//							            WSClient.setData("{var_time}", "15:30:00");
	//							            WSClient.setData("{var_comments}", "Wake him up at 4:30");
	//							            WSClient.setData("{var_resort}", resort);
	//							            WSClient.setData("{var_actionType}", "ADD");
	//
	//							         // Creating request and processing response for OWS WakeUpCall Operation
	//							            String wakeUpCallReq = WSClient.createSOAPMessage("OWSWakeUpCall", "DS_01");
	//										String wakeUpCallRes = WSClient.processSOAPMessage(wakeUpCallReq);
	//
	//										// Validating response of OWS WakeUpCall Operation
	//										if(WSAssert.assertIfElementExists(wakeUpCallRes,
	//			    								"WakeUpCallResponse_Result_resultStatusFlag", true)) {
	//			    							if (WSAssert.assertIfElementValueEquals(wakeUpCallRes,
	//			    									"WakeUpCallResponse_Result_resultStatusFlag", "SUCCESS", false)) {
	//
	//			    								// Database Validation
	//			    								LinkedHashMap<String,String> expectedValues =new LinkedHashMap<String,String>();
	//												LinkedHashMap<String, String> actualValues =new  LinkedHashMap<String,String>();
	//												LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
	//												xPath.put("WakeUpCallRequest_RoomNumber", "_WakeUpCallRequest");
	//												xPath.put("WakeUpCallRequest_ResvNameId", "_WakeUpCallRequest");
	//												xPath.put("WakeUpCallRequest_WakeUpCallDetails_Comments", "WakeUpCallRequest_WakeUpCallDetails");
	//												expectedValues = WSClient.getSingleNodeList(wakeUpCallReq, xPath, false, XMLType.REQUEST);
	//												actualValues=WSClient.getDBRow(WSClient.getQuery("QS_01"));
	//												WSAssert.assertEquals(expectedValues,actualValues,false);
	//											}else {
	//			    								if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", true)) {
	//			    									String message=WSAssert.getElementValue(wakeUpCallRes, "Result_Text_TextElement", XMLType.RESPONSE);
	//			    									WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the response is :"+ message+"</b>");
	//			    									if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", true)) {
	//			    										String operaErrorCode=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	//			    										WSClient.writeToReport(LogStatus.INFO, "<b>Opera Error Code :"+ operaErrorCode+"</b>");
	//
	//			    									}
	//			    								}
	//			    								if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", true)) {
	//			    									String message=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", XMLType.RESPONSE);
	//			    									WSClient.writeToReport(LogStatus.INFO, "<b>The gds error displayed in the WAkeUpCall response is :"+ message+"</b>");
	//			    								}
	//			    							}
	//										}else {
	//			    							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
	//			    						}
	//									}else {
	//										WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
	//									}
	//
	//									// CheckingOut Reservation
	//									if(CheckoutReservation.checkOutReservation("DS_01")) WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedOut Reservation</b>");
	//
	//							   }
	//					        }
	//						}else {
	//							WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
	//						}
	//
	//					}catch(Exception e) {
	//						WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
	//					}finally {
	//						// 	Setting WAKEUP Parameter back to previous value
	//						WSClient.setData("{var_parameter}", "WAKEUP");
	//			            WSClient.setData("{var_settingValue}", previousValue);
	//			            try {
	//							ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
	//						} catch (Exception e) {
	//							WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : WAKEUP");
	//						}
	//					}
	//				}



	// Sanity Test Case :1
	@Test(groups={"sanity","GuestServices","WakeUpCall","OWS"})
	public void wakeUpCall_38674() {
		String previousValue="Y";
		try {
			String testname = "wakeUpCall_38674";
			WSClient.startTest(testname, "Verify that wakeup call is associated to a reservation by passing valid values in mandatory fields", "sanity");

			setOperaHeader();

			// Setting WAKEUP Parameter
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue=Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above created profile
					if(!(resvID = createReservation("DS_12")).equals("error"))
					{
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						// Fetching and assigning room to reservation
						fetchHotelRooms("DS_13");
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);

						// Checking In Reservation
						String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
						String checkInRes = WSClient.processSOAPMessage(checkInReq);

						if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){

							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");

							setOWSHeader();

							resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

							// Setting variables for WakeUpCall
							WSClient.setData("{var_fromDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_toDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_time}", "15:30:00");
							WSClient.setData("{var_comments}", "Wake him up at 4:30");
							WSClient.setData("{var_resort}", resort);
							WSClient.setData("{var_actionType}", "ADD");

							// Creating request and processing response for OWS WakeUpCall Operation
							String wakeUpCallReq = WSClient.createSOAPMessage("OWSWakeUpCall", "DS_01");
							String wakeUpCallRes = WSClient.processSOAPMessage(wakeUpCallReq);

							// Validating response of OWS WakeUpCall Operation
							if(WSAssert.assertIfElementExists(wakeUpCallRes,
									"WakeUpCallResponse_Result_resultStatusFlag", true)) {
								if (WSAssert.assertIfElementValueEquals(wakeUpCallRes,
										"WakeUpCallResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									// Database Validation
									LinkedHashMap<String,String> expectedValues =new LinkedHashMap<String,String>();
									LinkedHashMap<String, String> actualValues =new  LinkedHashMap<String,String>();
									LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
									xPath.put("WakeUpCallRequest_RoomNumber", "_WakeUpCallRequest");
									xPath.put("WakeUpCallRequest_ResvNameId", "_WakeUpCallRequest");
									xPath.put("WakeUpCallRequest_WakeUpCallDetails_Comments", "WakeUpCallRequest_WakeUpCallDetails");
									expectedValues = WSClient.getSingleNodeList(wakeUpCallReq, xPath, false, XMLType.REQUEST);
									actualValues=WSClient.getDBRow(WSClient.getQuery("QS_01"));
									WSAssert.assertEquals(expectedValues,actualValues,false);
								}else {
									if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", true)) {
										String message=WSAssert.getElementValue(wakeUpCallRes, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The error displayed in the InsertUpdateName response is :"+ message+"</b>");
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", true)) {
											String operaErrorCode=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "<b>Opera Error Code :"+ operaErrorCode+"</b>");

										}
									}
									if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", true)) {
										String message=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "<b>The gds error displayed in the WAkeUpCall response is :"+ message+"</b>");
									}
								}
							}else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
						}

						// CheckingOut Reservation
						if(CheckoutReservation.checkOutReservation("DS_01")) WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedOut Reservation</b>");

					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {
			// 	Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : WAKEUP");
			}
		}
	}

	// Minimum Regression Test Case :1
	@Test(groups={"minimumRegression","GuestServices","WakeUpCall","OWS"})
	public void wakeUpCall_38675() {
		String previousValue="Y";
		try {
			String testname = "wakeUpCall_38675";
			WSClient.startTest(testname, "Verify that wakeup call is associated to a reservation by passing reservation id type as External", "minimumRegression");

			setOperaHeader();

			// Setting WAKEUP Parameter
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue=Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above created profile
					if(!(resvID = createReservation("DS_12")).equals("error"))
					{
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						// Fetching and assigning room to reservation
						fetchHotelRooms("DS_13");
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);

						// Checking In Reservation
						String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
						String checkInRes = WSClient.processSOAPMessage(checkInReq);

						if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){

							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");

							setOWSHeader();

							resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

							// Setting variables for WakeUpCall
							WSClient.setData("{var_fromDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_toDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_time}", "15:30:00");
							WSClient.setData("{var_comments}", "Wake him up at 4:30");
							WSClient.setData("{var_resort}", resort);
							WSClient.setData("{var_actionType}", "ADD");

							// Creating request and processing response for OWS WakeUpCall Operation
							String wakeUpCallReq = WSClient.createSOAPMessage("OWSWakeUpCall", "DS_02");
							String wakeUpCallRes = WSClient.processSOAPMessage(wakeUpCallReq);

							// Validating response of OWS WakeUpCall Operation
							if(WSAssert.assertIfElementExists(wakeUpCallRes,
									"WakeUpCallResponse_Result_resultStatusFlag", true)) {
								if (WSAssert.assertIfElementValueEquals(wakeUpCallRes,
										"WakeUpCallResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									LinkedHashMap<String,String> expectedValues =new LinkedHashMap<String,String>();
									LinkedHashMap<String, String> actualValues =new  LinkedHashMap<String,String>();
									LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
									xPath.put("WakeUpCallRequest_RoomNumber", "_WakeUpCallRequest");
									xPath.put("WakeUpCallRequest_ResvNameId", "_WakeUpCallRequest");
									xPath.put("WakeUpCallRequest_WakeUpCallDetails_Comments", "WakeUpCallRequest_WakeUpCallDetails");
									expectedValues = WSClient.getSingleNodeList(wakeUpCallReq, xPath, false, XMLType.REQUEST);
									actualValues=WSClient.getDBRow(WSClient.getQuery("QS_01"));
									WSAssert.assertEquals(expectedValues,actualValues,false);
								}else {
									if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", true)) {
										String message=WSAssert.getElementValue(wakeUpCallRes, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "The error displayed in the InsertUpdateName response is :"+ message);
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", true)) {
											String operaErrorCode=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

										}
									}
									if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", true)) {
										String message=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the WAkeUpCall response is :"+ message);
									}
								}
							}else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
						}

						// CheckingOut Reservation
						if(CheckoutReservation.checkOutReservation("DS_01")) WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedOut Reservation</b>");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {
			// Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : WAKEUP");
			}
		}
	}

	// Minimum Regression Test Case : 2
	@Test(groups={"minimumRegression","GuestServices","WakeUpCall","OWS"})
	public void wakeUpCall_38680() {
		String previousValue="Y";
		try {
			String testname = "wakeUpCall_38680";
			WSClient.startTest(testname, "Verify that error message is populated by passing invalid hotel code in request", "minimumRegression");

			setOperaHeader();

			// Setting WAKEUP Parameter
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue=Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above created profile
					if(!(resvID = createReservation("DS_12")).equals("error"))
					{
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						// Fetching and assigning room to reservation
						fetchHotelRooms("DS_13");
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);

						// Checking In Reservation
						String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
						String checkInRes = WSClient.processSOAPMessage(checkInReq);

						if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){

							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");

							setOWSHeader();

							resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

							// Setting variables for WakeUpCall
							WSClient.setData("{var_fromDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_toDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_time}", "15:30:00");
							WSClient.setData("{var_comments}", "Wake him up at 4:30");
							WSClient.setData("{var_resort}", resort);
							WSClient.setData("{var_actionType}", "ADD");

							// Creating request and processing response for OWS WakeUpCall Operation
							String wakeUpCallReq = WSClient.createSOAPMessage("OWSWakeUpCall", "DS_03");
							String wakeUpCallRes = WSClient.processSOAPMessage(wakeUpCallReq);

							// Validating response of OWS WakeUpCall Operation
							if(WSAssert.assertIfElementExists(wakeUpCallRes,
									"WakeUpCallResponse_Result_resultStatusFlag", true)) {
								if (WSAssert.assertIfElementValueEquals(wakeUpCallRes,
										"WakeUpCallResponse_Result_resultStatusFlag", "FAIL", true)) {
									if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", true) && WSAssert.assertIfElementValueEquals(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", "INVALID_PROPERTY", true)) {
										WSAssert.assertIfElementValueEquals(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", "INVALID_PROPERTY", false);
									}else if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", true) && WSAssert.assertIfElementContains(wakeUpCallRes, "Result_Text_TextElement", "Unable to identify hotel code conversion", false)) {

									}else {
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", true)) {
											String operaErrorCode=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
										}
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", true)) {
											String message=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the WAkeUpCall response is :"+ message);
										}
									}
								}else {
									WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
								}
							}else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
						}

						// CheckingOut Reservation
						if(CheckoutReservation.checkOutReservation("DS_01")) WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedOut Reservation</b>");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {
			// Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : WAKEUP");
			}
		}
	}

	// Minimum Regression Test Case : 3
	@Test(groups={"minimumRegression","GuestServices","WakeUpCall","OWS"})
	public void wakeUpCall_38681() {

		String previousValue="Y";

		try {
			String testname = "wakeUpCall_38681";
			WSClient.startTest(testname, "Verify that error message is populated by passing invalid room number in request", "minimumRegression");

			setOperaHeader();

			// Setting WAKEUP Parameter
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue=Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above created profile
					if(!(resvID = createReservation("DS_12")).equals("error"))
					{
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						// Fetching and assigning room to reservation
						fetchHotelRooms("DS_13");
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);

						// Checking In Reservation
						String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
						String checkInRes = WSClient.processSOAPMessage(checkInReq);

						if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){

							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");

							setOWSHeader();

							resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

							// Setting variables for WakeUpCall
							WSClient.setData("{var_fromDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_toDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_time}", "15:30:00");
							WSClient.setData("{var_comments}", "Wake him up at 4:30");
							WSClient.setData("{var_resort}", resort);
							WSClient.setData("{var_actionType}", "ADD");
							String roomnum = WSClient.getData("{var_roomNumber}");
							WSClient.setData("{var_roomNumber}", roomnum.substring(2));

							// Creating request and processing response for OWS WakeUpCall Operation
							String wakeUpCallReq = WSClient.createSOAPMessage("OWSWakeUpCall", "DS_01");
							String wakeUpCallRes = WSClient.processSOAPMessage(wakeUpCallReq);

							// Validating response of OWS WakeUpCall Operation
							if(WSAssert.assertIfElementExists(wakeUpCallRes,
									"WakeUpCallResponse_Result_resultStatusFlag", true)) {
								if (WSAssert.assertIfElementValueEquals(wakeUpCallRes,
										"WakeUpCallResponse_Result_resultStatusFlag", "FAIL", true)) {

									if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", false)){
										if(WSAssert.assertIfElementValueEquals(wakeUpCallRes, "Result_Text_TextElement", "NO MATCH FOUND FOR REQUESTED RESV NAME ID FOR GIVEN ROOM NO.", true)) {
											WSAssert.assertIfElementValueEquals(wakeUpCallRes, "Result_Text_TextElement", "NO MATCH FOUND FOR REQUESTED RESV NAME ID FOR GIVEN ROOM NO.", false);
										}else if(WSAssert.assertIfElementContains(wakeUpCallRes, "Result_Text_TextElement", "Wake up call must belong to a room or in house reservation", false)) {

										}
									}else {
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", true)) {
											String operaErrorCode=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
										}
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", true)) {
											String message=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the WAkeUpCall response is :"+ message);
										}
									}
								}else {
									WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
								}
							}else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
						}

						// CheckingOut Reservation
						if(CheckoutReservation.checkOutReservation("DS_01")) WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedOut Reservation</b>");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {
			// Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : WAKEUP");
			}
		}
	}

	// Minimum Regression Test Case : 4
	@Test(groups={"minimumRegression","GuestServices","WakeUpCall","OWS"})
	public void wakeUpCall_38682() {
		String previousValue="Y";
		try {
			String testname = "wakeUpCall_38682";
			WSClient.startTest(testname, "Verify that error message is populated by not passing reservation id in request", "minimumRegression");

			setOperaHeader();

			// Setting WAKEUP Parameter
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue=Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above created profile
					if(!(resvID = createReservation("DS_12")).equals("error"))
					{
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						// Fetching and assigning room to reservation
						fetchHotelRooms("DS_13");
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);

						// Checking In Reservation
						String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
						String checkInRes = WSClient.processSOAPMessage(checkInReq);

						if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){

							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");

							setOWSHeader();

							resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

							// Setting variables for WakeUpCall
							WSClient.setData("{var_fromDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_toDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_time}", "15:30:00");
							WSClient.setData("{var_comments}", "Wake him up at 4:30");
							WSClient.setData("{var_resort}", resort);
							WSClient.setData("{var_actionType}", "ADD");
							String roomnum = WSClient.getData("{var_roomNumber}");
							WSClient.setData("{var_roomNumber}", roomnum.substring(2));

							// Creating request and processing response for OWS WakeUpCall Operation
							String wakeUpCallReq = WSClient.createSOAPMessage("OWSWakeUpCall", "DS_04");
							String wakeUpCallRes = WSClient.processSOAPMessage(wakeUpCallReq);

							// Validating response of OWS WakeUpCall Operation
							if(WSAssert.assertIfElementExists(wakeUpCallRes,
									"WakeUpCallResponse_Result_resultStatusFlag", true)) {
								if (WSAssert.assertIfElementValueEquals(wakeUpCallRes,
										"WakeUpCallResponse_Result_resultStatusFlag", "FAIL", true)) {

									if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", false)) {
										if(WSAssert.assertIfElementValueEquals(wakeUpCallRes, "Result_Text_TextElement", "NO MATCH FOUND FOR REQUESTED RESV NAME ID FOR GIVEN ROOM NO.", true)) {
											WSAssert.assertIfElementValueEquals(wakeUpCallRes, "Result_Text_TextElement", "NO MATCH FOUND FOR REQUESTED RESV NAME ID FOR GIVEN ROOM NO.", false);
										}else if(WSAssert.assertIfElementContains(wakeUpCallRes, "Result_Text_TextElement", "Room number/reservation id is not valid", false)) {

										}
									}else {
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", true)) {
											String operaErrorCode=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
										}
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", true)) {
											String message=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the WAkeUpCall response is :"+ message);
										}
									}

								}else {
									WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
								}
							}else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
						}

						// CheckingOut Reservation
						if(CheckoutReservation.checkOutReservation("DS_01")) WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedOut Reservation</b>");

					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {
			// Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : WAKEUP");
			}
		}
	}

	// Minimum Regression Test Case : 5
	@Test(groups={"minimumRegression","GuestServices","WakeUpCall","OWS"})
	public void wakeUpCall_38683() {
		String previousValue="Y";
		try {
			String testname = "wakeUpCall_38683";
			WSClient.startTest(testname, "Verify that error message is populated by not passing wake up time in request", "minimumRegression");

			setOperaHeader();

			// Setting WAKEUP Parameter
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue=Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above created profile
					if(!(resvID = createReservation("DS_12")).equals("error"))
					{
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						// Fetching and assigning room to reservation
						fetchHotelRooms("DS_13");
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);

						// Checking In Reservation
						String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
						String checkInRes = WSClient.processSOAPMessage(checkInReq);

						if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){

							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");

							setOWSHeader();

							resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

							// Setting variables for WakeUpCall
							WSClient.setData("{var_fromDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_toDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_time}", "15:30:00");
							WSClient.setData("{var_comments}", "Wake him up at 4:30");
							WSClient.setData("{var_resort}", resort);
							WSClient.setData("{var_actionType}", "ADD");
							String roomnum = WSClient.getData("{var_roomNumber}");
							WSClient.setData("{var_roomNumber}", roomnum.substring(2));

							// Creating request and processing response for OWS WakeUpCall Operation
							String wakeUpCallReq = WSClient.createSOAPMessage("OWSWakeUpCall", "DS_05");
							String wakeUpCallRes = WSClient.processSOAPMessage(wakeUpCallReq);

							// Validating response of OWS WakeUpCall Operation
							if(WSAssert.assertIfElementExists(wakeUpCallRes,
									"WakeUpCallResponse_Result_resultStatusFlag", true)) {
								if (WSAssert.assertIfElementValueEquals(wakeUpCallRes,
										"WakeUpCallResponse_Result_resultStatusFlag", "FAIL", true)) {


									if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", false) && WSAssert.assertIfElementContains(wakeUpCallRes, "Result_Text_TextElement", "WakeupTime must be specified", false)) {

									}else {
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", true)) {
											String message=WSAssert.getElementValue(wakeUpCallRes, "Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "The error displayed in the WAkeUpCall response is :"+ message);
											if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", true)) {
												String operaErrorCode=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
											}
										}
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", true)) {
											String message=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the WAkeUpCall response is :"+ message);
										}
									}

								}else {
									WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
								}
							}else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
						}

						// CheckingOut Reservation
						if(CheckoutReservation.checkOutReservation("DS_01")) WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedOut Reservation</b>");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {
			// Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : WAKEUP");
			}
		}
	}

	// Minimum Regression Test Case : 6
	@Test(groups={"minimumRegression","GuestServices","WakeUpCall","OWS"})
	public void wakeUpCall_38684() {
		String previousValue="Y";
		try {
			String testname = "wakeUpCall_38684";
			WSClient.startTest(testname, "Verify that wakeup call is associated to a reservation by not passing any comments", "minimumRegression");

			setOperaHeader();

			// Setting WAKEUP Parameter
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue=Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above created profile
					if(!(resvID = createReservation("DS_12")).equals("error"))
					{
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						// Fetching and assigning room to reservation
						fetchHotelRooms("DS_13");
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);

						// Checking In Reservation
						String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
						String checkInRes = WSClient.processSOAPMessage(checkInReq);

						if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){

							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");

							setOWSHeader();

							resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

							// Setting variables for WakeUpCall
							WSClient.setData("{var_fromDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_toDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_time}", "15:30:00");
							WSClient.setData("{var_comments}", "Wake him up at 4:30");
							WSClient.setData("{var_resort}", resort);
							WSClient.setData("{var_actionType}", "ADD");

							// Creating request and processing response for OWS WakeUpCall Operation
							String wakeUpCallReq = WSClient.createSOAPMessage("OWSWakeUpCall", "DS_06");
							String wakeUpCallRes = WSClient.processSOAPMessage(wakeUpCallReq);

							// Validating response of OWS WakeUpCall Operation
							if(WSAssert.assertIfElementExists(wakeUpCallRes,
									"WakeUpCallResponse_Result_resultStatusFlag", true)) {
								if (WSAssert.assertIfElementValueEquals(wakeUpCallRes,
										"WakeUpCallResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									// Database Validation
									LinkedHashMap<String,String> expectedValues =new LinkedHashMap<String,String>();
									LinkedHashMap<String, String> actualValues =new  LinkedHashMap<String,String>();
									LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
									xPath.put("WakeUpCallRequest_RoomNumber", "_WakeUpCallRequest");
									xPath.put("WakeUpCallRequest_ResvNameId", "WakeUpCallRequest_ResvNameId");
									expectedValues = WSClient.getSingleNodeList(wakeUpCallReq, xPath, false, XMLType.REQUEST);
									actualValues=WSClient.getDBRow(WSClient.getQuery("QS_01"));
									WSAssert.assertEquals(expectedValues,actualValues,false);

								}else {
									if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", true)) {
										String message=WSAssert.getElementValue(wakeUpCallRes, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "The error displayed in the InsertUpdateName response is :"+ message);
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", true)) {
											String operaErrorCode=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

										}
									}
									if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", true)) {
										String message=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the WAkeUpCall response is :"+ message);
									}
								}
							}else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
						}

						// CheckingOut Reservation
						if(CheckoutReservation.checkOutReservation("DS_01")) WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedOut Reservation</b>");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {
			// Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : WAKEUP");
			}
		}
	}

	// Minimum Regression Test Case : 7
	@Test(groups={"minimumRegression","GuestServices","WakeUpCall","OWS"})
	public void wakeUpCall_38685() {
		String previousValue="Y";
		try {
			String testname = "wakeUpCall_38685";
			WSClient.startTest(testname, "Verify that multiple wakeup calls are associated to a reservation by passing valid values in mandatory fields", "minimumRegression");

			setOperaHeader();

			// Setting WAKEUP Parameter
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue=Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above created profile
					if(!(resvID = createReservation("DS_04")).equals("error"))
					{
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						// Fetching and assigning room to reservation
						fetchHotelRooms("DS_14");
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);

						// Checking In Reservation
						String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
						String checkInRes = WSClient.processSOAPMessage(checkInReq);

						if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){

							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");

							setOWSHeader();

							resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

							// Setting variables for WakeUpCall
							WSClient.setData("{var_fromDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_toDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_1}"));
							WSClient.setData("{var_time}", "15:30:00");
							WSClient.setData("{var_comments}", "Wake him up at 4:30");
							WSClient.setData("{var_resort}", resort);
							WSClient.setData("{var_actionType}", "ADD");

							// Creating request and processing response for OWS WakeUpCall Operation
							String wakeUpCallReq = WSClient.createSOAPMessage("OWSWakeUpCall", "DS_01");
							String wakeUpCallRes = WSClient.processSOAPMessage(wakeUpCallReq);

							// Validating response of OWS WakeUpCall Operation
							if(WSAssert.assertIfElementExists(wakeUpCallRes,
									"WakeUpCallResponse_Result_resultStatusFlag", true)) {
								if (WSAssert.assertIfElementValueEquals(wakeUpCallRes,
										"WakeUpCallResponse_Result_resultStatusFlag", "SUCCESS", false)) {

									// Database Validation
									List<LinkedHashMap<String,String>> expectedValues =new ArrayList<LinkedHashMap<String,String>>();
									List<LinkedHashMap<String, String>> actualValues =new  ArrayList<LinkedHashMap<String,String>>();
									LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
									xPath.put("WakeUpCallRequest_RoomNumber", "_WakeUpCallRequest");
									xPath.put("WakeUpCallRequest_ResvNameId", "_WakeUpCallRequest");
									xPath.put("WakeUpCallRequest_WakeUpCallDetails_FromDate", "WakeUpCallRequest_WakeUpCallDetails");
									xPath.put("WakeUpCallRequest_WakeUpCallDetails_ToDate", "WakeUpCallRequest_WakeUpCallDetails");
									xPath.put("WakeUpCallRequest_WakeUpCallDetails_Comments", "WakeUpCallRequest_WakeUpCallDetails");
									expectedValues = WSClient.getMultipleNodeList(wakeUpCallReq, xPath, false, XMLType.REQUEST);

									String WakeDate1 = expectedValues.get(0).get("FromDate1");
									String WakeDate2 = expectedValues.get(0).get("ToDate1");

									expectedValues.get(0).remove("FromDate1");
									expectedValues.get(0).remove("ToDate1");
									expectedValues.get(0).put("WakeDate1", WakeDate1);

									LinkedHashMap<String,String> expectedValuesTemp =new LinkedHashMap<String,String>();
									expectedValuesTemp = WSClient.getSingleNodeList(wakeUpCallReq, xPath, false, XMLType.REQUEST);

									expectedValuesTemp.remove("FromDate1");
									expectedValuesTemp.remove("ToDate1");
									expectedValuesTemp.put("WakeDate1", WakeDate2);

									expectedValues.add(expectedValuesTemp);

									actualValues=WSClient.getDBRows(WSClient.getQuery("QS_04"));

									actualValues.get(0).put("WakeDate1", actualValues.get(0).get("WakeDate1").substring(0, 10));
									actualValues.get(1).put("WakeDate1", actualValues.get(1).get("WakeDate1").substring(0, 10));
									WSAssert.assertEquals(actualValues,expectedValues,false);

								}else {
									if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", true)) {
										String message=WSAssert.getElementValue(wakeUpCallRes, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "The error displayed in the InsertUpdateName response is :"+ message);
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", true)) {
											String operaErrorCode=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

										}
									}
									if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", true)) {
										String message=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the WAkeUpCall response is :"+ message);
									}
								}
							}else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
						}

						//Cancelling Reservation
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {
			// Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : WAKEUP");
			}
		}
	}

	// Minimum Regression Test Case : 8
	@Test(groups={"minimumRegression","GuestServices","WakeUpCall","OWS"})
	public void wakeUpCall_38686() {
		String previousValue="Y";
		try {
			String testname = "wakeUpCall_38686";
			WSClient.startTest(testname, "Verify that error message is populated by passing date range period beyond reservation period in request", "minimumRegression");

			setOperaHeader();

			// Setting WAKEUP Parameter
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue=Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above created profile
					if(!(resvID = createReservation("DS_12")).equals("error"))
					{
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						// Fetching and assigning room to reservation
						fetchHotelRooms("DS_13");
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);

						// Checking In Reservation
						String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
						String checkInRes = WSClient.processSOAPMessage(checkInReq);

						if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){

							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");

							setOWSHeader();

							resort = OPERALib.getResort();
							String channel = OWSLib.getChannel();
							WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

							// Setting variables for WakeUpCall
							WSClient.setData("{var_fromDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_toDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_2}"));
							WSClient.setData("{var_time}", "15:30:00");
							WSClient.setData("{var_comments}", "Wake him up at 4:30");
							WSClient.setData("{var_resort}", resort);
							WSClient.setData("{var_actionType}", "ADD");

							// Creating request and processing response for OWS WakeUpCall Operation
							String wakeUpCallReq = WSClient.createSOAPMessage("OWSWakeUpCall", "DS_01");
							String wakeUpCallRes = WSClient.processSOAPMessage(wakeUpCallReq);

							// Validating response of OWS WakeUpCall Operation
							if(WSAssert.assertIfElementExists(wakeUpCallRes,
									"WakeUpCallResponse_Result_resultStatusFlag", true)) {
								if (WSAssert.assertIfElementValueEquals(wakeUpCallRes,
										"WakeUpCallResponse_Result_resultStatusFlag", "FAIL", true)) {

									if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", false)) {
										if(WSAssert.assertIfElementContains(wakeUpCallRes, "Result_Text_TextElement", "REQUEST NOT DURING STAY.", true)) {
											WSAssert.assertIfElementContains(wakeUpCallRes, "Result_Text_TextElement", "REQUEST NOT DURING STAY.", false);
										}else if(WSAssert.assertIfElementContains(wakeUpCallRes, "Result_Text_TextElement", "Wake up calls cannot continue after the reservation has departed", false)) {

										}
									}else {

										if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", true)) {
											String message=WSAssert.getElementValue(wakeUpCallRes, "Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "The error displayed in the WAkeUpCall response is :"+ message);
											if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", true)) {
												String operaErrorCode=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
											}
										}
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", true)) {
											String message=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the WAkeUpCall response is :"+ message);
										}
									}

								}else {
									WSClient.writeToReport(LogStatus.FAIL, "Test Failed!!");
								}
							}else {
								WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
						}

						// CheckingOut Reservation
						if(CheckoutReservation.checkOutReservation("DS_01")) WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedOut Reservation</b>");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {
			// Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : WAKEUP");
			}
		}
	}

	// Minimum Regression Test Case : 9
	@Test(groups={"minimumRegression","GuestServices","WakeUpCall","OWS"})
	public void wakeUpCall_38687() {
		String previousValue="Y";
		try {
			String testname = "wakeUpCall_38687";
			WSClient.startTest(testname, "Verify that wakeup call associated to a reservation is fetched by passing valid values in mandatory fields", "minimumRegression");

			setOperaHeader();

			// Setting WAKEUP Parameter
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue=Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above created profile
					if(!(resvID = createReservation("DS_12")).equals("error"))
					{
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						// Fetching and assigning room to reservation
						fetchHotelRooms("DS_13");
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);

						// Checking In Reservation
						String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
						String checkInRes = WSClient.processSOAPMessage(checkInReq);

						if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){

							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");

							// Setting variables for Creating WakeUpCall
							WSClient.setData("{var_fromDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_toDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_time}", "16:30:00");
							WSClient.setData("{var_comments}", "Wake him up at 4:30");

							// Creating WakeUpCall for reservation
							String createWakeUpCallReq = WSClient.createSOAPMessage("CreateWakeUpCalls","DS_01");
							String createWakeUpCallRes = WSClient.processSOAPMessage(createWakeUpCallReq);


							if(WSAssert.assertIfElementExists(createWakeUpCallRes,"CreateWakeUpCallsRS_Success",true)) {

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Associated WakeUpCalls to Reservation</b>");

								setOWSHeader();

								resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								WSClient.setData("{var_resort}", resort);
								WSClient.setData("{var_actionType}", "FETCH");

								// Creating request and processing response for OWS WakeUpCall Operation
								String wakeUpCallReq = WSClient.createSOAPMessage("OWSWakeUpCall", "DS_07");
								String wakeUpCallRes = WSClient.processSOAPMessage(wakeUpCallReq);

								// Validating response of OWS WakeUpCall Operation
								if(WSAssert.assertIfElementExists(wakeUpCallRes,
										"WakeUpCallResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(wakeUpCallRes,
											"WakeUpCallResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										// Database Validation
										LinkedHashMap<String,String> expectedValues =new LinkedHashMap<String,String>();
										LinkedHashMap<String, String> actualValues =new  LinkedHashMap<String,String>();
										LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
										xPath.put("WakeUpCallDetails_WakeUpCalls_WakeupTime", "WakeUpCallResponse_WakeUpCallDetails_WakeUpCalls");
										xPath.put("WakeUpCallDetails_WakeUpCalls_FromDate", "WakeUpCallResponse_WakeUpCallDetails_WakeUpCalls");
										xPath.put("WakeUpCallDetails_WakeUpCalls_Comments", "WakeUpCallResponse_WakeUpCallDetails_WakeUpCalls");
										xPath.put("WakeUpCallDetails_WakeUpCalls_ToDate", "WakeUpCallResponse_WakeUpCallDetails_WakeUpCalls");
										actualValues = WSClient.getSingleNodeList(wakeUpCallRes, xPath, false, XMLType.RESPONSE);
										expectedValues=WSClient.getDBRow(WSClient.getQuery("QS_02"));
										expectedValues.put("WakeupTime1", expectedValues.get("WakeupTime1").substring(11, 19));
										expectedValues.put("FromDate1", expectedValues.get("FromDate1").substring(0, 10));
										expectedValues.put("ToDate1", expectedValues.get("ToDate1").substring(0, 10));
										actualValues.put("FromDate1", actualValues.get("FromDate1").substring(0, 10));
										actualValues.put("ToDate1", actualValues.get("ToDate1").substring(0, 10));
										actualValues.put("WakeupTime1", actualValues.get("WakeupTime1").substring(0, 8));
										WSAssert.assertEquals(expectedValues,actualValues,false);
									}else {
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", true)) {
											String message=WSAssert.getElementValue(wakeUpCallRes, "Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "The error displayed in the InsertUpdateName response is :"+ message);
											if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", true)) {
												String operaErrorCode=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

											}
										}
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", true)) {
											String message=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the WAkeUpCall response is :"+ message);
										}

									}
								}else {
									WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
								}
							}else {
								WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreateWakeUpCalls-----Blocked");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
						}

						// CheckingOut Reservation
						if(CheckoutReservation.checkOutReservation("DS_01")) WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedOut Reservation</b>");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {
			// Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : WAKEUP");
			}
		}
	}

	// // Minimum Regression Test Case : 10
	@Test(groups={"minimumRegression","GuestServices","WakeUpCall","OWS"})
	public void wakeUpCall_38688() {
		String previousValue="Y";
		try {
			String testname = "wakeUpCall_38688";
			WSClient.startTest(testname, "Verify that multiple wakeup calls associated to a reservation of more than one day are fetched by passing valid values in mandatory fields", "minimumRegression");

			setOperaHeader();

			// Setting WAKEUP Parameter
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue=Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above created profile
					if(!(resvID = createReservation("DS_04")).equals("error"))
					{
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						// Fetching and assigning room to reservation
						fetchHotelRooms("DS_14");
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);

						// Checking In Reservation
						String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
						String checkInRes = WSClient.processSOAPMessage(checkInReq);

						if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){

							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");

							// Setting variables for WakeUpCall
							WSClient.setData("{var_fromDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_toDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_1}"));
							WSClient.setData("{var_time}", "17:30:00");
							WSClient.setData("{var_comments}", "Wake him up at 4:30");

							// Creating WakeUpCall for reservation
							String createWakeUpCallReq = WSClient.createSOAPMessage("CreateWakeUpCalls","DS_01");
							String createWakeUpCallRes = WSClient.processSOAPMessage(createWakeUpCallReq);

							if(WSAssert.assertIfElementExists(createWakeUpCallRes,"CreateWakeUpCallsRS_Success",true)) {

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Associated WakeUpCalls to Reservation</b>");

								setOWSHeader();

								resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								WSClient.setData("{var_resort}", resort);
								WSClient.setData("{var_actionType}", "FETCH");

								// Creating request and processing response for OWS WakeUpCall Operation
								String wakeUpCallReq = WSClient.createSOAPMessage("OWSWakeUpCall", "DS_07");
								String wakeUpCallRes = WSClient.processSOAPMessage(wakeUpCallReq);

								// Validating response of OWS WakeUpCall Operation
								if(WSAssert.assertIfElementExists(wakeUpCallRes,
										"WakeUpCallResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(wakeUpCallRes,
											"WakeUpCallResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										// Database Validation
										List<LinkedHashMap<String,String>> expectedValues =new ArrayList<LinkedHashMap<String,String>>();
										List<LinkedHashMap<String, String>> actualValues =new  ArrayList<LinkedHashMap<String,String>>();
										LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
										xPath.put("WakeUpCallDetails_WakeUpCalls_WakeupTime", "WakeUpCallResponse_WakeUpCallDetails_WakeUpCalls");
										xPath.put("WakeUpCallDetails_WakeUpCalls_FromDate", "WakeUpCallResponse_WakeUpCallDetails_WakeUpCalls");
										xPath.put("WakeUpCallDetails_WakeUpCalls_Comments", "WakeUpCallResponse_WakeUpCallDetails_WakeUpCalls");
										xPath.put("WakeUpCallDetails_WakeUpCalls_ToDate", "WakeUpCallResponse_WakeUpCallDetails_WakeUpCalls");
										actualValues = WSClient.getMultipleNodeList(wakeUpCallRes, xPath, false, XMLType.RESPONSE);
										expectedValues=WSClient.getDBRows(WSClient.getQuery("QS_02"));
										for(int i=0;i<expectedValues.size();i++) {
											expectedValues.get(i).put("WakeupTime1", expectedValues.get(i).get("WakeupTime1").substring(11, 19));
											expectedValues.get(i).put("FromDate1", expectedValues.get(i).get("FromDate1").substring(0, 10));
											expectedValues.get(i).put("ToDate1", expectedValues.get(i).get("ToDate1").substring(0, 10));
											actualValues.get(i).put("FromDate1", actualValues.get(i).get("FromDate1").substring(0, 10));
											actualValues.get(i).put("ToDate1", actualValues.get(i).get("ToDate1").substring(0, 10));
											actualValues.get(i).put("WakeupTime1", actualValues.get(i).get("WakeupTime1").substring(0, 8));
										}

										WSAssert.assertEquals(actualValues,expectedValues,false);
									}else {
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", true)) {
											String message=WSAssert.getElementValue(wakeUpCallRes, "Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "The error displayed in the InsertUpdateName response is :"+ message);
											if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", true)) {
												String operaErrorCode=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

											}
										}
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", true)) {
											String message=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the WAkeUpCall response is :"+ message);
										}

									}
								}else {
									WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
								}
							}else {
								WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreateWakeUpCalls-----Blocked");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
						}

						//Cancelling Reservation
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {
			// Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : WAKEUP");
			}
		}
	}

	// Minimum Regression Test Case : 11
	@Test(groups={"minimumRegression","GuestServices","WakeUpCall","OWS"})
	public void wakeUpCall_38689() {
		String previousValue="Y";
		try {
			String testname = "wakeUpCall_38689";
			WSClient.startTest(testname, "Verify that wakeup call associated to a reservation is deleted by passing valid values in mandatory fields", "minimumRegression");

			setOperaHeader();

			// Setting WAKEUP Parameter
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue=Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {


				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above created profile
					if(!(resvID = createReservation("DS_12")).equals("error"))
					{
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						// Fetching and assigning room to reservation
						fetchHotelRooms("DS_13");
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);

						// Checking In Reservation
						String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
						String checkInRes = WSClient.processSOAPMessage(checkInReq);

						if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){

							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");

							// Setting variables for WakeUpCall
							WSClient.setData("{var_fromDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_toDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
							WSClient.setData("{var_time}", "17:30:00");
							WSClient.setData("{var_comments}", "Wake him up at 4:30");

							// Creating WakeUpCall for reservation
							String createWakeUpCallReq = WSClient.createSOAPMessage("CreateWakeUpCalls","DS_01");
							String createWakeUpCallRes = WSClient.processSOAPMessage(createWakeUpCallReq);

							if(WSAssert.assertIfElementExists(createWakeUpCallRes,"CreateWakeUpCallsRS_Success",true)) {


								LinkedHashMap<String,String> count =new LinkedHashMap<String,String>();
								Integer i = new Integer(0);
								count=WSClient.getDBRow(WSClient.getQuery("OWSWakeUpCall", "QS_02"));

								if(count.size()<0)
									WSClient.writeToReport(LogStatus.WARNING, "Unable to associate WakeUpCalls Reservation");
								else {
									WSClient.writeToReport(LogStatus.INFO,"<b>"+ count.get("Comments1")+ " -> Successfully Associated WakeUpCalls to Reservation"+"</b>");

									setOWSHeader();

									resort = OPERALib.getResort();
									String channel = OWSLib.getChannel();
									WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

									WSClient.setData("{var_resort}", resort);
									WSClient.setData("{var_actionType}", "DELETE");

									// Creating request and processing response for OWS WakeUpCall Operation
									String wakeUpCallReq = WSClient.createSOAPMessage("OWSWakeUpCall", "DS_08");
									String wakeUpCallRes = WSClient.processSOAPMessage(wakeUpCallReq);

									// Validating response of OWS WakeUpCall Operation
									if(WSAssert.assertIfElementExists(wakeUpCallRes,
											"WakeUpCallResponse_Result_resultStatusFlag", true)) {
										if (WSAssert.assertIfElementValueEquals(wakeUpCallRes,
												"WakeUpCallResponse_Result_resultStatusFlag", "SUCCESS", false)) {

											// Database Validation
											count=WSClient.getDBRow(WSClient.getQuery("QS_06"));
											String result = count.get("result");
											if(WSAssert.assertEquals("D", result, false)) {
												WSClient.writeToReport(LogStatus.PASS, "Wakeup Call associated to the reservation successfully deleted.");
											}else {
												WSClient.writeToReport(LogStatus.FAIL, "Wakeup Call associated to the reservation not deleted.");
											}

										}else {
											WSClient.writeToReport(LogStatus.FAIL, "Test Failed.");
											if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", true)) {
												String message=WSAssert.getElementValue(wakeUpCallRes, "Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "The error displayed in the InsertUpdateName response is :"+ message);
												if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", true)) {
													String operaErrorCode=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

												}
											}
											if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", true)) {
												String message=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the WAkeUpCall response is :"+ message);
											}

										}
									}else {
										WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
									}
								}
							}else {
								WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CreateWakeUpCalls-----Blocked");
							}
						}else {
							WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
						}

						// CheckingOut Reservation
						if(CheckoutReservation.checkOutReservation("DS_01")) WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedOut Reservation</b>");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {
			// Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : WAKEUP");
			}
		}
	}

	// Minimum Regression Test Case : 12
	@Test(groups={"minimumRegression","WakeUpCall","GuestServices","OWS"})
	public void wakeUpCall_41602() {
		String previousValue="Y";
		try {
			String testname = "wakeUpCall_41602";
			WSClient.startTest(testname, "Verify that error message is populated if reseravtion is not checkedin", "minimumRegression");

			setOperaHeader();

			// Setting WAKEUP Parameter
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", "Y");
			String Parameter1 = FetchApplicationParameters.getApplicationParameter("DS_01");
			previousValue=Parameter1;
			if (!Parameter1.equals("Y")) {
				Parameter1=ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			}

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above created profile
					if(!(resvID = createReservation("DS_12")).equals("error"))
					{
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						// Fetching and assigning room to reservation
						fetchHotelRooms("DS_13");
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);

						setOWSHeader();

						resort = OPERALib.getResort();
						String channel = OWSLib.getChannel();
						WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

						// Setting variables for WakeUpCall
						WSClient.setData("{var_fromDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_toDate}", WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}"));
						WSClient.setData("{var_time}", "15:30:00");
						WSClient.setData("{var_comments}", "Wake him up at 4:30");
						WSClient.setData("{var_resort}", resort);
						WSClient.setData("{var_actionType}", "ADD");

						// Creating request and processing response for OWS WakeUpCall Operation
						String wakeUpCallReq = WSClient.createSOAPMessage("OWSWakeUpCall", "DS_01");
						String wakeUpCallRes = WSClient.processSOAPMessage(wakeUpCallReq);

						// Validating response of OWS WakeUpCall Operation
						if(WSAssert.assertIfElementExists(wakeUpCallRes,
								"WakeUpCallResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(wakeUpCallRes,
									"WakeUpCallResponse_Result_resultStatusFlag", "FAIL", true)) {

								if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", false)) {

									if(WSAssert.assertIfElementValueEquals(wakeUpCallRes, "Result_Text_TextElement", "GUEST IS NOT CHECKED IN HOTEL YET.", true)) {
										WSAssert.assertIfElementValueEquals(wakeUpCallRes, "Result_Text_TextElement", "GUEST IS NOT CHECKED IN HOTEL YET.", false);
									}else if(WSAssert.assertIfElementContains(wakeUpCallRes, "Result_Text_TextElement", "Wake up calls cannot be created for reservation that has not been checked in", false)) {

									}
								}else {

									if(WSAssert.assertIfElementExists(wakeUpCallRes, "Result_Text_TextElement", true)) {
										String message=WSAssert.getElementValue(wakeUpCallRes, "Result_Text_TextElement", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "The error displayed in the WAkeUpCall response is :"+ message);
										if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", true)) {
											String operaErrorCode=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_OperaErrorCode", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
										}
									}
									if(WSAssert.assertIfElementExists(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", true)) {
										String message=WSAssert.getElementValue(wakeUpCallRes, "WakeUpCallResponse_Result_GDSError", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the WAkeUpCall response is :"+ message);
									}
								}

							}else {
								WSClient.writeToReport(LogStatus.FAIL, "Test Failed");
							}
						}else {
							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
						}
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}finally {
			// 	Setting WAKEUP Parameter back to previous value
			WSClient.setData("{var_parameter}", "WAKEUP");
			WSClient.setData("{var_settingValue}", previousValue);
			try {
				ChangeApplicationParameters.changeApplicationParameter("DS_01","DS_01");
			} catch (Exception e) {
				WSClient.writeToReport(LogStatus.INFO, "Unable to revert back application parameter : WAKEUP");
			}
		}
	}

}
