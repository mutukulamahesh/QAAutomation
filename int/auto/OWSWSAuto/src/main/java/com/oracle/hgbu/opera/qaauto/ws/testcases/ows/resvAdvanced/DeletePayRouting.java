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

public class DeletePayRouting extends WSSetUp{


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
					roomNumber = WSClient.getElementValue(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>"+"Successfully Fetched Room to assign, room Number is: "+roomNumber+"</b>");
					WSClient.setData("{var_roomNumber}", roomNumber);
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
					}
				}
				else
				{
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
				assignRoom("DS_01");
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


	//			@Test(groups={"minimumRegression","ResvAdvanced","DeletePayRouting","OWS"})
	//			public void deletePayRouting_60003() {
	//
	//				try {
	//				String testname = "deletePayRouting_60003";
	//				WSClient.startTest(testname, "Verify that pay routing associated to a reservation is deleted by passing valid reservation id and payroutings with valid window number and routing codes and billtonameid with channel where channel and carrier name are different","minimumRegression");
	//
	//				if (OperaPropConfig.getPropertyConfigResults(
	//	                    new String[] { "TransactionCode", "TransactionGroup", "TransactionSubGroup", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {
	//
	//				String operaProfileID1, operaProfileID2, reservationId1;
	//
	//					// Creating a profile
	//			        if(!(operaProfileID1 = createProfile("DS_01")).equals("error"))
	//			        {
	//			        	// Creating a reservation for above created profile
	//			        	if(!(reservationId1 = createReservation("DS_04")).equals("error"))
	//				        {
	//			        		// Setting Variables
	//			        		WSClient.setData("{var_resvId}", reservationId1);
	//			        		fetchHotelRooms("DS_03");
	//							String resort = OPERALib.getResort();
	//							WSClient.setData("{var_owsresort}", resort);
	//
	//							// Checking In Reservation
	//			        		String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
	//							String checkInRes = WSClient.processSOAPMessage(checkInReq);
	//
	//							// Creating a profile to which charges are routed
	//			        		if(!(operaProfileID2 = createProfile("DS_01")).equals("error"))
	//			    	        {
	//
	//			        				// Setting Variables
	////			    	        		WSClient.setData("{var_profileId}", "4392314");
	////			    	        		WSClient.setData("{var_payeeProfileId}", operaProfileID2);
	////			    	        		WSClient.setData("{var_resvId}", "12629618");
	//				        			WSClient.setData("{var_profileId}", operaProfileID1);
	//			    	        		WSClient.setData("{var_payeeProfileId}", operaProfileID2);
	//			    	        		WSClient.setData("{var_resvId}", reservationId1);
	//
	//			    	        		WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_01"));
	//			    					WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01"));
	//			    					WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01"));
	//
	//			    					// Routing Charges to Other Profile
	//			    	        		String createRoutingInstReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_04");
	//			    			        String createRoutingInstRes = WSClient.processSOAPMessage(createRoutingInstReq);
	//
	//			    			        if(WSAssert.assertIfElementExists(createRoutingInstRes, "CreateRoutingInstructionsRS_Success", true)) {
	//
	//
	//			    			        	// Setting Variables
	//			    			        	WSClient.setData("{var_convtrxGroup}", (OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01")));
	//				    					WSClient.setData("{var_convtrxSubGroup}", (OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01")));
	//			    			        	String query = WSClient.getQuery("OWSDeletePayRouting","QS_01");
	//			    			        	LinkedHashMap<String,String> Values = new LinkedHashMap<String,String>();
	//										Values=WSClient.getDBRow(query);
	//
	//
	//										if(Values.get("ROUTING_TYPE").equals("W"))
	//										WSClient.setData("{var_routingType}", "WINDOW");
	//										WSClient.setData("{var_trxCode}", Values.get("TRX_CODE"));
	//										WSClient.setData("{var_billNameId}", Values.get("BILL_TO_NAME_ID"));
	//
	//
	//
	//
	//										resort = OPERALib.getResort();
	//										String channel = OWSLib.getChannel(3);
	//										WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
	//										OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resort, OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
	//
	//										// Creating request and processing response for OWS DeletePayRouting Operation
	//										String DeletePayRoutingReq = WSClient.createSOAPMessage("OWSDeletePayRouting", "DS_01");
	//				    			        String DeletePayRoutingRes = WSClient.processSOAPMessage(DeletePayRoutingReq);
	//
	//				    			        // Validating response of OWS DeletepayRouting
	//				    			        if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", false)) {
	//				    			        	if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
	//
	//				    			        		LinkedHashMap<String,String> db = new LinkedHashMap<String,String>();
	//				    			        		String query_n = WSClient.getQuery("QS_02");
	//												db=WSClient.getDBRow(query_n);
	//
	//												if(Integer.valueOf(db.get("count")).intValue()<1) {
	//													WSClient.writeToReport(LogStatus.PASS, "Record Deleted---------Test Passed");
	//												}else {
	//													WSClient.writeToReport(LogStatus.FAIL, "Record not deleted------Test Failed");
	//												}
	//				    			        	}
	//				    			        }
	//
	//			    			        }else {
	//			    			        	WSClient.writeToReport(LogStatus.WARNING,"Prerequisites failed!------CreateRoutingInstructions-----Blocked");
	//			    			        }
	//			    	        }
	//			        		// Canceling Reservation
	//                            //WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
	//                            CancelReservation.cancelReservation("DS_02");
	//
	//				        }
	//			        }
	//				}else {
	//					  WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
	//				  }
	//			}catch(Exception e) {
	//				WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
	//			}
	//		}
	// Sanity Test Case : 1
	@Test(groups={"sanity","ResvAdvanced","DeletePayRouting","OWS", "deletePayRouting_39720"})
	public void deletePayRouting_39720() {

		try {
			String testname = "deletePayRouting_39720";
			WSClient.startTest(testname, "Verify that pay routing associated to a reservation is deleted by passing valid reservation id and payroutings with valid window number and routing codes and billtonameid", "sanity");

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "TransactionCode", "TransactionGroup", "TransactionSubGroup", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String operaProfileID1, operaProfileID2, reservationId1;

				// Creating a profile
				if(!(operaProfileID1 = createProfile("DS_01")).equals("error"))
				{
					// Creating a reservation for above created profile
					if(!(reservationId1 = createReservation("DS_04")).equals("error"))
					{
						// Setting Variables
						WSClient.setData("{var_resvId}", reservationId1);
						fetchHotelRooms("DS_03");
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);

						// Checking In Reservation
						String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
						String checkInRes = WSClient.processSOAPMessage(checkInReq);

						// Creating a profile to which charges are routed
						if(!(operaProfileID2 = createProfile("DS_01")).equals("error"))
						{

							// Setting Variables
							//			    	        		WSClient.setData("{var_profileId}", "4392314");
							//			    	        		WSClient.setData("{var_payeeProfileId}", operaProfileID2);
							//			    	        		WSClient.setData("{var_resvId}", "12629618");
							WSClient.setData("{var_profileId}", operaProfileID1);
							WSClient.setData("{var_payeeProfileId}", operaProfileID2);
							WSClient.setData("{var_resvId}", reservationId1);

							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_01"));
							WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01"));
							WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01"));

							// Routing Charges to Other Profile
							String createRoutingInstReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_04");
							String createRoutingInstRes = WSClient.processSOAPMessage(createRoutingInstReq);

							if(WSAssert.assertIfElementExists(createRoutingInstRes, "CreateRoutingInstructionsRS_Success", true)) {


								// Setting Variables
								WSClient.setData("{var_convtrxGroup}", (OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01")));
								WSClient.setData("{var_convtrxSubGroup}", (OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01")));
								String query = WSClient.getQuery("OWSDeletePayRouting","QS_01");
								LinkedHashMap<String,String> Values = new LinkedHashMap<String,String>();
								Values=WSClient.getDBRow(query);


								if(Values.get("ROUTING_TYPE").equals("W"))
									WSClient.setData("{var_routingType}", "WINDOW");
								WSClient.setData("{var_trxCode}", Values.get("TRX_CODE"));
								WSClient.setData("{var_billNameId}", Values.get("BILL_TO_NAME_ID"));


								setOWSHeader();

								resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								// Creating request and processing response for OWS DeletePayRouting Operation
								String DeletePayRoutingReq = WSClient.createSOAPMessage("OWSDeletePayRouting", "DS_01");
								String DeletePayRoutingRes = WSClient.processSOAPMessage(DeletePayRoutingReq);

								// Validating response of OWS DeletepayRouting
								if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", false)) {
									if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										LinkedHashMap<String,String> db = new LinkedHashMap<String,String>();
										String query_n = WSClient.getQuery("QS_02");
										db=WSClient.getDBRow(query_n);

										if(Integer.valueOf(db.get("count")).intValue()<1) {
											WSClient.writeToReport(LogStatus.PASS, "Record Deleted---------Test Passed");
										}else {
											WSClient.writeToReport(LogStatus.FAIL, "Record not deleted------Test Failed");
										}
									}
								}

							}else {
								WSClient.writeToReport(LogStatus.WARNING,"Prerequisites failed!------CreateRoutingInstructions-----Blocked");
							}
						}
						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");

					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case : 1
	@Test(groups={"minimumRegression","ResvAdvanced","DeletePayRouting","OWS"})
	public void deletePayRouting_39740() {

		try {
			String testname = "deletePayRouting_39740";
			WSClient.startTest(testname, "Verify that error message is obtained by passing invalid hotel code", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "TransactionCode", "TransactionGroup", "TransactionSubGroup", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String operaProfileID1, operaProfileID2, reservationId1;

				if(!(operaProfileID1 = createProfile("DS_01")).equals("error"))
				{
					if(!(reservationId1 = createReservation("DS_04")).equals("error"))
					{
						if(!(operaProfileID2 = createProfile("DS_01")).equals("error"))
						{
							WSClient.setData("{var_profileId}", operaProfileID1);
							WSClient.setData("{var_payeeProfileId}", operaProfileID2);
							WSClient.setData("{var_resvId}", reservationId1);

							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_01"));
							WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01"));
							WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01"));

							String createRoutingInstReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
							String createRoutingInstRes = WSClient.processSOAPMessage(createRoutingInstReq);

							if(WSAssert.assertIfElementExists(createRoutingInstRes, "CreateRoutingInstructionsRS_Success", true)) {

								WSClient.setData("{var_convtrxGroup}", (OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01")));
								WSClient.setData("{var_convtrxSubGroup}", (OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01")));
								String query = WSClient.getQuery("OWSDeletePayRouting","QS_01");
								LinkedHashMap<String,String> Values = new LinkedHashMap<String,String>();
								Values=WSClient.getDBRow(query);

								if(Values.get("ROUTING_TYPE").equals("W"))
									WSClient.setData("{var_routingType}", "WINDOW");
								WSClient.setData("{var_trxCode}", Values.get("TRX_CODE"));
								WSClient.setData("{var_billNameId}", Values.get("BILL_TO_NAME_ID"));
								WSClient.setData("{var_routingInstCode}", Values.get("ROUTING_INSTRUCTIONS_ID"));

								setOWSHeader();

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								String DeletePayRoutingReq = WSClient.createSOAPMessage("OWSDeletePayRouting", "DS_02");
								String DeletePayRoutingRes = WSClient.processSOAPMessage(DeletePayRoutingReq);

								if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", false)) {
									if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

										if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_OperaErrorCode", "INVALID_PROPERTY", false)) {

										}
									}
								}

							}else {
								WSClient.writeToReport(LogStatus.WARNING,"Prerequisites failed!------CreateRoutingInstructions-----Blocked");
							}
						}
						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case : 2
	@Test(groups={"minimumRegression","ResvAdvanced","DeletePayRouting","OWS"})
	public void deletePayRouting_41161() {

		try {
			String testname = "deletePayRouting_41161";
			WSClient.startTest(testname, "Verify that error message is obtained by not passing reservation id", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "TransactionCode", "TransactionGroup", "TransactionSubGroup", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String operaProfileID1, operaProfileID2, reservationId1;

				if(!(operaProfileID1 = createProfile("DS_01")).equals("error"))
				{
					if(!(reservationId1 = createReservation("DS_04")).equals("error"))
					{
						if(!(operaProfileID2 = createProfile("DS_01")).equals("error"))
						{

							WSClient.setData("{var_profileId}", operaProfileID1);
							WSClient.setData("{var_payeeProfileId}", operaProfileID2);
							WSClient.setData("{var_resvId}", reservationId1);

							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_01"));
							WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01"));
							WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01"));

							String createRoutingInstReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
							String createRoutingInstRes = WSClient.processSOAPMessage(createRoutingInstReq);

							if(WSAssert.assertIfElementExists(createRoutingInstRes, "CreateRoutingInstructionsRS_Success", true)) {

								WSClient.setData("{var_convtrxGroup}", (OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01")));
								WSClient.setData("{var_convtrxSubGroup}", (OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01")));
								String query = WSClient.getQuery("OWSDeletePayRouting","QS_01");
								LinkedHashMap<String,String> Values = new LinkedHashMap<String,String>();
								Values=WSClient.getDBRow(query);


								if(Values.get("ROUTING_TYPE").equals("W"))
									WSClient.setData("{var_routingType}", "WINDOW");
								WSClient.setData("{var_trxCode}", Values.get("TRX_CODE"));
								WSClient.setData("{var_billNameId}", Values.get("BILL_TO_NAME_ID"));
								WSClient.setData("{var_routingInstCode}", Values.get("ROUTING_INSTRUCTIONS_ID"));

								setOWSHeader();

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								String DeletePayRoutingReq = WSClient.createSOAPMessage("OWSDeletePayRouting", "DS_03");
								String DeletePayRoutingRes = WSClient.processSOAPMessage(DeletePayRoutingReq);

								if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", false)) {
									if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

										if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_OperaErrorCode", "BOOKING_NOT_FOUND", false)) {

										}
									}
								}

							}else {
								WSClient.writeToReport(LogStatus.WARNING,"Prerequisites failed!------CreateRoutingInstructions-----Blocked");
							}
						}
						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case : 3
	@Test(groups={"minimumRegression","ResvAdvanced","DeletePayRouting","OWS"})
	public void deletePayRouting_41163() {

		try {
			String testname = "deletePayRouting_41163";
			WSClient.startTest(testname, "Verify that error message is obtained by passing invalid routing type", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "TransactionCode", "TransactionGroup", "TransactionSubGroup", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String operaProfileID1, operaProfileID2, reservationId1;

				if(!(operaProfileID1 = createProfile("DS_01")).equals("error"))
				{
					if(!(reservationId1 = createReservation("DS_04")).equals("error"))
					{
						if(!(operaProfileID2 = createProfile("DS_01")).equals("error"))
						{

							WSClient.setData("{var_profileId}", operaProfileID1);
							WSClient.setData("{var_payeeProfileId}", operaProfileID2);
							WSClient.setData("{var_resvId}", reservationId1);

							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_01"));
							WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01"));
							WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01"));

							String createRoutingInstReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
							String createRoutingInstRes = WSClient.processSOAPMessage(createRoutingInstReq);

							if(WSAssert.assertIfElementExists(createRoutingInstRes, "CreateRoutingInstructionsRS_Success", true)) {

								WSClient.setData("{var_convtrxGroup}", (OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01")));
								WSClient.setData("{var_convtrxSubGroup}", (OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01")));
								String query = WSClient.getQuery("OWSDeletePayRouting","QS_01");
								LinkedHashMap<String,String> Values = new LinkedHashMap<String,String>();
								Values=WSClient.getDBRow(query);


								if(Values.get("ROUTING_TYPE").equals("W"))
									WSClient.setData("{var_routingType}", "WINDW");
								WSClient.setData("{var_trxCode}", Values.get("TRX_CODE"));
								WSClient.setData("{var_billNameId}", Values.get("BILL_TO_NAME_ID"));
								WSClient.setData("{var_routingInstCode}", Values.get("ROUTING_INSTRUCTIONS_ID"));

								setOWSHeader();

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								String DeletePayRoutingReq = WSClient.createSOAPMessage("OWSDeletePayRouting", "DS_01");
								String DeletePayRoutingRes = WSClient.processSOAPMessage(DeletePayRoutingReq);

								if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", false)) {
									if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

										if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "Result_Text_TextElement", "Routing type should be either ROOM or WINDOW or REQUEST or COMP", false)) {

										}
									}
								}

							}else {
								WSClient.writeToReport(LogStatus.WARNING,"Prerequisites failed!------CreateRoutingInstructions-----Blocked");
							}
						}
						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case : 4
	@Test(groups={"minimumRegression","ResvAdvanced","DeletePayRouting","OWS"})
	public void deletePayRouting_41164() {

		try {
			String testname = "deletePayRouting_41164";
			WSClient.startTest(testname, "Verify that error message is obtained by passing valid reservation id but no payrouting", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "TransactionCode", "TransactionGroup", "TransactionSubGroup", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String operaProfileID1, operaProfileID2, reservationId1;

				if(!(operaProfileID1 = createProfile("DS_01")).equals("error"))
				{
					if(!(reservationId1 = createReservation("DS_04")).equals("error"))
					{
						if(!(operaProfileID2 = createProfile("DS_01")).equals("error"))
						{

							WSClient.setData("{var_profileId}", operaProfileID1);
							WSClient.setData("{var_payeeProfileId}", operaProfileID2);
							WSClient.setData("{var_resvId}", reservationId1);

							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_01"));
							WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01"));
							WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01"));

							String createRoutingInstReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
							String createRoutingInstRes = WSClient.processSOAPMessage(createRoutingInstReq);

							if(WSAssert.assertIfElementExists(createRoutingInstRes, "CreateRoutingInstructionsRS_Success", true)) {


								setOWSHeader();

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								String DeletePayRoutingReq = WSClient.createSOAPMessage("OWSDeletePayRouting", "DS_07");
								String DeletePayRoutingRes = WSClient.processSOAPMessage(DeletePayRoutingReq);

								if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", false)) {
									if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

										if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "Result_Text_TextElement", false)) {
											if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "Result_Text_TextElement", "PayRoutings element is Missing in Request Message", false)) {

											}
										}
									}
								}

							}else {
								WSClient.writeToReport(LogStatus.WARNING,"Prerequisites failed!------CreateRoutingInstructions-----Blocked");
							}
						}
						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case : 5
	@Test(groups={"minimumRegression","ResvAdvanced","DeletePayRouting","OWS"})
	public void deletePayRouting_41165() {

		try {
			String testname = "deletePayRouting_41165";
			WSClient.startTest(testname, "Verify that error message is obtained by passing valid reservation id and payroutings without window number", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "TransactionCode", "TransactionGroup", "TransactionSubGroup", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String operaProfileID1, operaProfileID2, reservationId1;

				if(!(operaProfileID1 = createProfile("DS_01")).equals("error"))
				{
					if(!(reservationId1 = createReservation("DS_04")).equals("error"))
					{
						if(!(operaProfileID2 = createProfile("DS_01")).equals("error"))
						{

							WSClient.setData("{var_profileId}", operaProfileID1);
							WSClient.setData("{var_payeeProfileId}", operaProfileID2);
							WSClient.setData("{var_resvId}", reservationId1);

							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_01"));
							WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01"));
							WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01"));

							String createRoutingInstReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
							String createRoutingInstRes = WSClient.processSOAPMessage(createRoutingInstReq);

							if(WSAssert.assertIfElementExists(createRoutingInstRes, "CreateRoutingInstructionsRS_Success", true)) {

								WSClient.setData("{var_convtrxGroup}", (OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01")));
								WSClient.setData("{var_convtrxSubGroup}", (OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01")));
								String query = WSClient.getQuery("OWSDeletePayRouting","QS_01");
								LinkedHashMap<String,String> Values = new LinkedHashMap<String,String>();
								Values=WSClient.getDBRow(query);

								if(Values.get("ROUTING_TYPE").equals("W"))
									WSClient.setData("{var_routingType}", "WINDOW");

								setOWSHeader();

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								String DeletePayRoutingReq = WSClient.createSOAPMessage("OWSDeletePayRouting", "DS_08");
								String DeletePayRoutingRes = WSClient.processSOAPMessage(DeletePayRoutingReq);

								if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", false)) {
									if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

										if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "Result_Text_TextElement", false)) {
											if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "Result_Text_TextElement", "INVALID WINDOW NO", false)) {

											}
										}
									}
								}

							}else {
								WSClient.writeToReport(LogStatus.WARNING,"Prerequisites failed!------CreateRoutingInstructions-----Blocked");
							}
						}
						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case : 6
	@Test(groups={"minimumRegression","ResvAdvanced","DeletePayRouting","OWS"})
	public void deletePayRouting_41166() {

		try {
			String testname = "deletePayRouting_41166";
			WSClient.startTest(testname, "Verify that error message is obtained by passing valid reservation id and payroutings with invalid window number", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "TransactionCode", "TransactionGroup", "TransactionSubGroup", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String operaProfileID1, operaProfileID2, reservationId1;

				if(!(operaProfileID1 = createProfile("DS_01")).equals("error"))
				{
					if(!(reservationId1 = createReservation("DS_04")).equals("error"))
					{
						if(!(operaProfileID2 = createProfile("DS_01")).equals("error"))
						{

							WSClient.setData("{var_profileId}", operaProfileID1);
							WSClient.setData("{var_payeeProfileId}", operaProfileID2);
							WSClient.setData("{var_resvId}", reservationId1);

							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_01"));
							WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01"));
							WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01"));

							String createRoutingInstReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
							String createRoutingInstRes = WSClient.processSOAPMessage(createRoutingInstReq);

							if(WSAssert.assertIfElementExists(createRoutingInstRes, "CreateRoutingInstructionsRS_Success", true)) {

								WSClient.setData("{var_convtrxGroup}", (OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01")));
								WSClient.setData("{var_convtrxSubGroup}", (OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01")));
								String query = WSClient.getQuery("OWSDeletePayRouting","QS_01");
								LinkedHashMap<String,String> Values = new LinkedHashMap<String,String>();
								Values=WSClient.getDBRow(query);

								if(Values.get("ROUTING_TYPE").equals("W"))
									WSClient.setData("{var_routingType}", "WINDOW");

								setOWSHeader();

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								String DeletePayRoutingReq = WSClient.createSOAPMessage("OWSDeletePayRouting", "DS_09");
								String DeletePayRoutingRes = WSClient.processSOAPMessage(DeletePayRoutingReq);

								if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", false)) {
									if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

										if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "Result_Text_TextElement", false)) {
											if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "Result_Text_TextElement", "INVALID WINDOW NO", false)) {

											}
										}
									}
								}

							}else {
								WSClient.writeToReport(LogStatus.WARNING,"Prerequisites failed!------CreateRoutingInstructions-----Blocked");
							}
						}
						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case : 7
	@Test(groups={"minimumRegression","ResvAdvanced","DeletePayRouting","OWS"})
	public void deletePayRouting_41167() {

		try {
			String testname = "deletePayRouting_41167";
			WSClient.startTest(testname, "Verify that error message is obtained by passing valid reservation id and payroutings with valid window number but without routing codes", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "TransactionCode", "TransactionGroup", "TransactionSubGroup", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String operaProfileID1, operaProfileID2, reservationId1;

				if(!(operaProfileID1 = createProfile("DS_01")).equals("error"))
				{
					if(!(reservationId1 = createReservation("DS_04")).equals("error"))
					{
						if(!(operaProfileID2 = createProfile("DS_01")).equals("error"))
						{

							WSClient.setData("{var_profileId}", operaProfileID1);
							WSClient.setData("{var_payeeProfileId}", operaProfileID2);
							WSClient.setData("{var_resvId}", reservationId1);

							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_01"));
							WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01"));
							WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01"));

							String createRoutingInstReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
							String createRoutingInstRes = WSClient.processSOAPMessage(createRoutingInstReq);

							if(WSAssert.assertIfElementExists(createRoutingInstRes, "CreateRoutingInstructionsRS_Success", true)) {

								WSClient.setData("{var_convtrxGroup}", (OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01")));
								WSClient.setData("{var_convtrxSubGroup}", (OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01")));
								String query = WSClient.getQuery("OWSDeletePayRouting","QS_01");
								LinkedHashMap<String,String> Values = new LinkedHashMap<String,String>();
								Values=WSClient.getDBRow(query);

								if(Values.get("ROUTING_TYPE").equals("W"))
									WSClient.setData("{var_routingType}", "WINDOW");

								setOWSHeader();

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								String DeletePayRoutingReq = WSClient.createSOAPMessage("OWSDeletePayRouting", "DS_10");
								String DeletePayRoutingRes = WSClient.processSOAPMessage(DeletePayRoutingReq);

								if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", false)) {
									if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

										if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "Result_Text_TextElement", false)) {
											if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "Result_Text_TextElement", "Both Trx Code and BillingInstrCode cannot be null when routing type is WINDOW.", false)) {

											}
										}
									}
								}

							}else {
								WSClient.writeToReport(LogStatus.WARNING,"Prerequisites failed!------CreateRoutingInstructions-----Blocked");
							}
						}
						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case : 8
	@Test(groups={"minimumRegression","ResvAdvanced","DeletePayRouting","OWS"})
	public void deletePayRouting_41168() {

		try {
			String testname = "deletePayRouting_41168";
			WSClient.startTest(testname, "Verify that error message is obtained by passing valid reservation id and payroutings with valid window number and routing codes but without billtonameid", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "TransactionCode", "TransactionGroup", "TransactionSubGroup", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String operaProfileID1, operaProfileID2, reservationId1;

				if(!(operaProfileID1 = createProfile("DS_01")).equals("error"))
				{
					if(!(reservationId1 = createReservation("DS_04")).equals("error"))
					{
						if(!(operaProfileID2 = createProfile("DS_01")).equals("error"))
						{

							WSClient.setData("{var_profileId}", operaProfileID1);
							WSClient.setData("{var_payeeProfileId}", operaProfileID2);
							WSClient.setData("{var_resvId}", reservationId1);

							WSClient.setData("{var_trxCode}", OperaPropConfig.getDataSetForCode("TransactionCode", "DS_01"));
							WSClient.setData("{var_trxGroup}", OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01"));
							WSClient.setData("{var_trxSubGroup}", OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01"));

							String createRoutingInstReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_01");
							String createRoutingInstRes = WSClient.processSOAPMessage(createRoutingInstReq);

							if(WSAssert.assertIfElementExists(createRoutingInstRes, "CreateRoutingInstructionsRS_Success", true)) {

								WSClient.setData("{var_convtrxGroup}", (OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_01")));
								WSClient.setData("{var_convtrxSubGroup}", (OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_01")));
								String query = WSClient.getQuery("OWSDeletePayRouting","QS_01");
								LinkedHashMap<String,String> Values = new LinkedHashMap<String,String>();
								Values=WSClient.getDBRow(query);

								if(Values.get("ROUTING_TYPE").equals("W"))
									WSClient.setData("{var_routingType}", "WINDOW");
								WSClient.setData("{var_trxCode}", Values.get("TRX_CODE"));

								setOWSHeader();

								String resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								String DeletePayRoutingReq = WSClient.createSOAPMessage("OWSDeletePayRouting", "DS_11");
								String DeletePayRoutingRes = WSClient.processSOAPMessage(DeletePayRoutingReq);

								if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", false)) {
									if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

										if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "Result_Text_TextElement", false)) {
											if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "Result_Text_TextElement", "Missing Bill to Name ID", false)) {

											}
										}
									}
								}

							}else {
								WSClient.writeToReport(LogStatus.WARNING,"Prerequisites failed!------CreateRoutingInstructions-----Blocked");
							}
						}
						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case : 9
	@Test(groups={"minimumRegression","ResvAdvanced","DeletePayRouting","OWS"})
	public void deletePayRouting_41180() {

		try {
			String testname = "deletePayRouting_41180";
			WSClient.startTest(testname, "Verify that error message is obtained by passing valid reservation id and payroutings with RoutingType ROOM and without target reservationId", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "TransactionCode", "TransactionGroup", "TransactionSubGroup", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String operaProfileID1, operaProfileID2, reservationId1, reservationId2;

				if(!(operaProfileID1 = createProfile("DS_01")).equals("error"))
				{
					if(!(reservationId1 = createReservation("DS_04")).equals("error"))
					{
						if(!(operaProfileID2 = createProfile("DS_01")).equals("error"))
						{
							if(!(reservationId2 = createReservation("DS_04")).equals("error"))
							{

								// Setting Variables
								WSClient.setData("{var_resvId}", reservationId1);
								fetchHotelRooms("DS_03");
								String resort = OPERALib.getResort();
								WSClient.setData("{var_owsresort}", resort);

								// Checking In Reservation
								String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){



									WSClient.setData("{var_payeeProfileId}", operaProfileID2);
									WSClient.setData("{var_payeeResvId}", reservationId2);
									WSClient.setData("{var_resvId}", reservationId1);
									WSClient.setData("{var_profileId}", operaProfileID1);
									WSClient.setData("{var_trxCode}",
											OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
									WSClient.setData("{var_trxGroup}",
											OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
									WSClient.setData("{var_trxSubGroup}",
											OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));


									String createRoutingInstReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_02");
									String createRoutingInstRes = WSClient.processSOAPMessage(createRoutingInstReq);

									if(WSAssert.assertIfElementExists(createRoutingInstRes, "CreateRoutingInstructionsRS_Success", true)) {

										WSClient.setData("{var_convtrxGroup}", (OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03")));
										WSClient.setData("{var_convtrxSubGroup}", (OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03")));
										String query = WSClient.getQuery("OWSDeletePayRouting","QS_01");
										LinkedHashMap<String,String> Values = new LinkedHashMap<String,String>();
										Values=WSClient.getDBRow(query);

										if(Values.get("ROUTING_TYPE").equals("R"))
											WSClient.setData("{var_routingType}", "ROOM");
										WSClient.setData("{var_trxCode}", Values.get("TRX_CODE"));

										setOWSHeader();

										resort = OPERALib.getResort();
										String channel = OWSLib.getChannel();
										WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

										String DeletePayRoutingReq = WSClient.createSOAPMessage("OWSDeletePayRouting", "DS_12");
										String DeletePayRoutingRes = WSClient.processSOAPMessage(DeletePayRoutingReq);

										if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", false)) {
											if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

												if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "Result_Text_TextElement", false)) {
													if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "Result_Text_TextElement", "Missing Target Reservation ID", false)) {

													}
												}
											}
										}

									}else {
										WSClient.writeToReport(LogStatus.WARNING,"Prerequisites failed!------CreateRoutingInstructions-----Blocked");
									}

								}else {
									WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
								}
							}
						}
						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case : 10
	@Test(groups={"minimumRegression","ResvAdvanced","DeletePayRouting","OWS"})
	public void deletePayRouting_41200() {

		try {
			String testname = "deletePayRouting_41200";
			WSClient.startTest(testname, "Verify that routing associated to reservation is deleted by passing valid reservation id and payroutings with RoutingType ROOM and with target reservationId", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "TransactionCode", "TransactionGroup", "TransactionSubGroup", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String operaProfileID1, operaProfileID2, reservationId1, reservationId2;

				if(!(operaProfileID1 = createProfile("DS_01")).equals("error"))
				{
					if(!(reservationId1 = createReservation("DS_04")).equals("error"))
					{
						if(!(operaProfileID2 = createProfile("DS_01")).equals("error"))
						{
							if(!(reservationId2 = createReservation("DS_04")).equals("error"))
							{

								// Setting Variables
								WSClient.setData("{var_resvId}", reservationId1);
								fetchHotelRooms("DS_03");
								String resort = OPERALib.getResort();
								WSClient.setData("{var_owsresort}", resort);
								WSClient.setData("{var_toResvId}", reservationId2);

								// Checking In Reservation
								String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){

									// Setting Variables
									WSClient.setData("{var_resvId}", reservationId2);
									fetchHotelRooms("DS_03");

									WSClient.setData("{var_owsresort}", resort);

									// Checking In Reservation
									String checkInReq2 = WSClient.createSOAPMessage("CheckinReservation","DS_01");
									String checkInRes2 = WSClient.processSOAPMessage(checkInReq2);

									if(WSAssert.assertIfElementExists(checkInRes2,"CheckinReservationRS_Success",true)){

										WSClient.setData("{var_payeeProfileId}", operaProfileID2);
										WSClient.setData("{var_payeeResvId}", reservationId2);
										WSClient.setData("{var_resvId}", reservationId1);
										WSClient.setData("{var_profileId}", operaProfileID1);
										WSClient.setData("{var_trxCode}",
												OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
										WSClient.setData("{var_trxGroup}",
												OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
										WSClient.setData("{var_trxSubGroup}",
												OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));


										String createRoutingInstReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_02");
										String createRoutingInstRes = WSClient.processSOAPMessage(createRoutingInstReq);

										if(WSAssert.assertIfElementExists(createRoutingInstRes, "CreateRoutingInstructionsRS_Success", true)) {

											WSClient.setData("{var_convtrxGroup}", (OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03")));
											WSClient.setData("{var_convtrxSubGroup}", (OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03")));
											String query = WSClient.getQuery("OWSDeletePayRouting","QS_01");
											LinkedHashMap<String,String> Values = new LinkedHashMap<String,String>();
											Values=WSClient.getDBRow(query);

											if(Values.get("ROUTING_TYPE").equals("R"))
												WSClient.setData("{var_routingType}", "ROOM");
											WSClient.setData("{var_trxCode}", Values.get("TRX_CODE"));

											setOWSHeader();

											resort = OPERALib.getResort();
											String channel = OWSLib.getChannel();
											WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

											String DeletePayRoutingReq = WSClient.createSOAPMessage("OWSDeletePayRouting", "DS_13");
											String DeletePayRoutingRes = WSClient.processSOAPMessage(DeletePayRoutingReq);

											if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", false)) {
												if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", "SUCCESS", false)) {

													LinkedHashMap<String,String> db = new LinkedHashMap<String,String>();
													String query_n = WSClient.getQuery("QS_02");
													db=WSClient.getDBRow(query_n);

													if(Integer.valueOf(db.get("count")).intValue()<1) {
														WSClient.writeToReport(LogStatus.PASS, "Record Deleted---------Test Passed");
													}else {
														WSClient.writeToReport(LogStatus.FAIL, "Record not deleted------Test Failed");
													}

												}
											}

										}else {
											WSClient.writeToReport(LogStatus.WARNING,"Prerequisites failed!------CreateRoutingInstructions-----Blocked");
										}
									}
								}else {
									WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
								}
							}
						}
						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case : 11
	@Test(groups={"minimumRegression","ResvAdvanced","DeletePayRouting","OWS"})
	public void deletePayRouting_41201() {

		try {
			String testname = "deletePayRouting_41201";
			WSClient.startTest(testname, "Verify that error message is obtained by passing valid reservation id and payroutings with RoutingType ROOM but with invalid target reservationId", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "TransactionCode", "TransactionGroup", "TransactionSubGroup", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String operaProfileID1, operaProfileID2, reservationId1, reservationId2;

				if(!(operaProfileID1 = createProfile("DS_01")).equals("error"))
				{
					if(!(reservationId1 = createReservation("DS_04")).equals("error"))
					{
						if(!(operaProfileID2 = createProfile("DS_01")).equals("error"))
						{
							if(!(reservationId2 = createReservation("DS_04")).equals("error"))
							{

								// Setting Variables
								WSClient.setData("{var_resvId}", reservationId1);
								fetchHotelRooms("DS_03");
								String resort = OPERALib.getResort();
								WSClient.setData("{var_owsresort}", resort);


								// Checking In Reservation
								String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){

									// Setting Variables
									WSClient.setData("{var_resvId}", reservationId2);
									fetchHotelRooms("DS_03");
									WSClient.setData("{var_owsresort}", resort);

									// Checking In Reservation
									String checkInReq2 = WSClient.createSOAPMessage("CheckinReservation","DS_01");
									String checkInRes2 = WSClient.processSOAPMessage(checkInReq2);

									if(WSAssert.assertIfElementExists(checkInRes2,"CheckinReservationRS_Success",true)){

										WSClient.setData("{var_payeeProfileId}", operaProfileID2);
										WSClient.setData("{var_payeeResvId}", reservationId2);
										WSClient.setData("{var_resvId}", reservationId1);
										WSClient.setData("{var_profileId}", operaProfileID1);
										WSClient.setData("{var_trxCode}",
												OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
										WSClient.setData("{var_trxGroup}",
												OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
										WSClient.setData("{var_trxSubGroup}",
												OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));


										String createRoutingInstReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_02");
										String createRoutingInstRes = WSClient.processSOAPMessage(createRoutingInstReq);

										if(WSAssert.assertIfElementExists(createRoutingInstRes, "CreateRoutingInstructionsRS_Success", true)) {

											WSClient.setData("{var_convtrxGroup}", (OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03")));
											WSClient.setData("{var_convtrxSubGroup}", (OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03")));
											String query = WSClient.getQuery("OWSDeletePayRouting","QS_01");
											LinkedHashMap<String,String> Values = new LinkedHashMap<String,String>();
											Values=WSClient.getDBRow(query);

											if(Values.get("ROUTING_TYPE").equals("R"))
												WSClient.setData("{var_routingType}", "ROOM");
											WSClient.setData("{var_trxCode}", Values.get("TRX_CODE"));

											setOWSHeader();

											resort = OPERALib.getResort();
											String channel = OWSLib.getChannel();
											WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

											WSClient.setData("{var_toResvId}", "12345");

											String DeletePayRoutingReq = WSClient.createSOAPMessage("OWSDeletePayRouting", "DS_13");
											String DeletePayRoutingRes = WSClient.processSOAPMessage(DeletePayRoutingReq);

											if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", false)) {
												if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

													if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "Result_Text_TextElement", false)) {
														if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "Result_Text_TextElement", "Invalid Target Reservation ID", false)) {

														}
													}

												}
											}

										}else {
											WSClient.writeToReport(LogStatus.WARNING,"Prerequisites failed!------CreateRoutingInstructions-----Blocked");
										}

									}
								}else {
									WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
								}
							}
						}
						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case : 12
	@Test(groups={"minimumRegression","ResvAdvanced","DeletePayRouting","OWS"})
	public void deletePayRouting_41272() {

		try {
			String testname = "deletePayRouting_41272";
			WSClient.startTest(testname, "Verify that routing associated to reservation is deleted by passing valid reservation id and payroutings with RoutingType ROOM and with target reservationId which is not checkedin", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(
					new String[] { "TransactionCode", "TransactionGroup", "TransactionSubGroup", "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" })) {

				String operaProfileID1, operaProfileID2, reservationId1, reservationId2;

				if(!(operaProfileID1 = createProfile("DS_01")).equals("error"))
				{
					if(!(reservationId1 = createReservation("DS_04")).equals("error"))
					{
						if(!(operaProfileID2 = createProfile("DS_01")).equals("error"))
						{
							if(!(reservationId2 = createReservation("DS_04")).equals("error"))
							{

								// Setting Variables
								WSClient.setData("{var_resvId}", reservationId1);
								fetchHotelRooms("DS_03");
								String resort = OPERALib.getResort();
								WSClient.setData("{var_owsresort}", resort);
								WSClient.setData("{var_toResvId}", reservationId2);

								// Checking In Reservation
								String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
								String checkInRes = WSClient.processSOAPMessage(checkInReq);

								if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){



									WSClient.setData("{var_payeeProfileId}", operaProfileID2);
									WSClient.setData("{var_payeeResvId}", reservationId2);
									WSClient.setData("{var_resvId}", reservationId1);
									WSClient.setData("{var_profileId}", operaProfileID1);
									WSClient.setData("{var_trxCode}",
											OperaPropConfig.getDataSetForCode("TransactionCode", "DS_03"));
									WSClient.setData("{var_trxGroup}",
											OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03"));
									WSClient.setData("{var_trxSubGroup}",
											OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03"));


									String createRoutingInstReq = WSClient.createSOAPMessage("ReservationCreateRoutingInstructions", "DS_02");
									String createRoutingInstRes = WSClient.processSOAPMessage(createRoutingInstReq);

									if(WSAssert.assertIfElementExists(createRoutingInstRes, "CreateRoutingInstructionsRS_Success", true)) {

										WSClient.setData("{var_convtrxGroup}", (OperaPropConfig.getDataSetForCode("TransactionGroup", "DS_03")));
										WSClient.setData("{var_convtrxSubGroup}", (OperaPropConfig.getDataSetForCode("TransactionSubGroup", "DS_03")));
										String query = WSClient.getQuery("OWSDeletePayRouting","QS_01");
										LinkedHashMap<String,String> Values = new LinkedHashMap<String,String>();
										Values=WSClient.getDBRow(query);

										if(Values.get("ROUTING_TYPE").equals("R"))
											WSClient.setData("{var_routingType}", "ROOM");
										WSClient.setData("{var_trxCode}", Values.get("TRX_CODE"));

										setOWSHeader();

										resort = OPERALib.getResort();
										String channel = OWSLib.getChannel();
										WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

										String DeletePayRoutingReq = WSClient.createSOAPMessage("OWSDeletePayRouting", "DS_13");
										String DeletePayRoutingRes = WSClient.processSOAPMessage(DeletePayRoutingReq);

										if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", false)) {
											if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_resultStatusFlag", "FAIL", false)) {

												if(WSAssert.assertIfElementExists(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_OperaErrorCode", false)) {
													if(WSAssert.assertIfElementValueEquals(DeletePayRoutingRes, "DeletePayRoutingResponse_Result_OperaErrorCode", "INVALID_TARGET_RESV_ID", false)) {

													}
												}

											}
										}

									}else {
										WSClient.writeToReport(LogStatus.WARNING,"Prerequisites failed!------CreateRoutingInstructions-----Blocked");
									}

								}else {
									WSClient.writeToReport(LogStatus.WARNING, "The prerequisites failed!------ CheckinReservation-----Blocked");
								}
							}
						}
						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "---------------Configuration not avaialble in PropertyConfigurations--------");
			}
		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

}
