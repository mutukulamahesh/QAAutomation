package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.resvAdvanced;

import java.util.ArrayList;
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

// Requirement 30262
public class Invoice extends WSSetUp{

	// Total Test Cases : 7

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
			WSClient.setData("{var_chaincode}", chain);


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
			WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));
			WSClient.setData("{var_roomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

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
			WSClient.setData("{var_RoomType}", OperaPropConfig.getDataSetForCode("RoomType", "DS_01"));

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

	//	@Test(groups={"minimumRegression","ReservationAdvanced","Invoice","OWS"})
	//	public void invoice_60012() {
	//		try {
	//			String testname = "invoice_60012";
	//			WSClient.startTest(testname, "Verify that invoice associated to a reservation is fetched to which charges are attached with channel when channel and carrier name are different", "minimumRegression");
	//
	//			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" }))	{
	//
	//				String UniqueId,resvID;
	//
	//				// Creating a profile
	//				if(!(UniqueId = createProfile("DS_01")).equals("error"))
	//				{
	//					// Creating reservation for above profile
	//					if(!(resvID = createReservation("DS_04")).equals("error"))
	//			        {
	//						// Setting Variables
	//						System.out.println(UniqueId);
	//						WSClient.setData("{var_profileId}", UniqueId);
	//						WSClient.setData("{var_resvId}", resvID);
	//						WSClient.setData("{var_reservation_id}", resvID);
	//
	//						/********
	//                         * Prerequisite : Fetch Hotel Rooms
	//                         **************/
	//						fetchHotelRooms("DS_03");
	//						String resort = OPERALib.getResort();
	//						WSClient.setData("{var_owsresort}", resort);
	//
	//
	//						String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
	//						String checkInRes = WSClient.processSOAPMessage(checkInReq);
	//
	//						if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){
	//
	//							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");
	//
	//							WSClient.setData("{var_cashierID}", OperaPropConfig.getDataSetForCode("Cashiers","DS_03"));
	//							WSClient.setData("{var_trx}", OperaPropConfig.getDataSetForCode("TransactionCode","DS_04"));
	//							String postBillingChargesReq = WSClient.createSOAPMessage("PostBillingCharges","DS_01");
	//
	//			                String postBillingChargesRes = WSClient.processSOAPMessage(postBillingChargesReq);
	//
	//			                if(WSAssert.assertIfElementExists(postBillingChargesRes,"PostBillingChargesRS_Success",true)) {
	//
	//			                	WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Posted Billing Charges to Profile</b>");
	//
	//
	//
	//			                	resort = OPERALib.getResort();
	//								String channel = OWSLib.getChannel(3);
	//								OWSLib.setOWSHeader(OPERALib.getUserName(), OPERALib.getPassword(), resort, OWSLib.getChannelType(channel), OWSLib.getChannelCarier(resort, channel));
	//								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));
	//
	//			                	// Creating request and processing response for OWS Invoice Operation
	//					            String invoiceReq = WSClient.createSOAPMessage("OWSInvoice", "DS_01");
	//								String invoiceRes = WSClient.processSOAPMessage(invoiceReq);
	//
	//								// Validating response of OWS Invoice Operation
	//								if(WSAssert.assertIfElementExists(invoiceRes,
	//										"InvoiceResponse_Result_resultStatusFlag", true)) {
	//									if (WSAssert.assertIfElementValueEquals(invoiceRes,
	//											"InvoiceResponse_Result_resultStatusFlag", "SUCCESS", false)) {
	//
	//										// Database Validation
	//										LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
	//										LinkedHashMap<String, String> actualValues = new LinkedHashMap<String,String>();
	//										LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
	//										xPath.put("Invoice_Name_firstName", "InvoiceResponse_Invoice_Name");
	//										xPath.put("Invoice_Name_lastName", "InvoiceResponse_Invoice_Name");
	//										xPath.put("Invoice_ProfileIDs_UniqueID", "InvoiceResponse_Invoice_ProfileIDs");
	//										xPath.put("InvoiceResponse_Invoice_BillItems_TransactionNo", "InvoiceResponse_Invoice_BillItems");
	//										xPath.put("InvoiceResponse_Invoice_BillItems_TransactionCode", "InvoiceResponse_Invoice_BillItems");
	//										xPath.put("Invoice_BillItems_Amount", "InvoiceResponse_Invoice_BillItems");
	//										xPath.put("Invoice_BillItems_OriginalRoom", "InvoiceResponse_Invoice_BillItems");
	//										xPath.put("Invoice_BillItems_Quantity", "InvoiceResponse_Invoice_BillItems");
	//										xPath.put("Invoice_BillItems_Reference", "InvoiceResponse_Invoice_BillItems");
	//										actualValues = WSClient.getSingleNodeList(invoiceRes, xPath, false, XMLType.RESPONSE);
	//										String query = WSClient.getQuery("QS_01");
	//										expectedValues=WSClient.getDBRow(query);
	//										WSAssert.assertEquals(expectedValues, actualValues, false);
	//
	//
	//									}else {
	//										if(WSAssert.assertIfElementExists(invoiceRes, "Result_Text_TextElement", true)) {
	//											String message=WSAssert.getElementValue(invoiceRes, "Result_Text_TextElement", XMLType.RESPONSE);
	//											WSClient.writeToReport(LogStatus.INFO, "The error displayed in the invoice response is :"+ message);
	//											if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", true)) {
	//												String operaErrorCode=WSAssert.getElementValue(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
	//												WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);
	//
	//											}
	//										}
	//										if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_GDSError", true)) {
	//											String message=WSAssert.getElementValue(invoiceRes, "InvoiceResponse_Result_GDSError", XMLType.RESPONSE);
	//											WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the invoice response is :"+ message);
	//										}
	//									}
	//								}else {
	//									WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
	//								}
	//
	//			                } else {
	//			                	WSClient.writeToReport(LogStatus.WARNING,
	//										"Prerequisites failed!------PostBillingCharges------Blocked");
	//			                }
	//
	//			             // Canceling Reservation
	//                            //WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
	//                            CancelReservation.cancelReservation("DS_02");
	//
	//					}else {
	//						WSClient.writeToReport(LogStatus.WARNING,
	//								"Prerequisites failed!------CheckinReservation-----Blocked");
	//					}
	//			      }
	//				}
	//			}else {
	//				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
	//			}
	//
	//		}catch(Exception e) {
	//			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
	//		}
	//}

	// Sanity Test Case :1
	@Test(groups={"minimumRegression","ReservationAdvanced","Invoice","OWS"})
	public void invoice_39101() {
		try {
			String testname = "invoice_39101";
			WSClient.startTest(testname, "Verify that 'No folio records' is obtained by passing details of reservation to which no charges are attached", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" }))	{

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above profile
					if(!(resvID = createReservation("DS_04")).equals("error"))
					{

						// Setting Variables
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						setOWSHeader();

						String resort = OPERALib.getResort();
						String channel = OWSLib.getChannel();
						WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

						// Creating request and processing response for OWS Invoice Operation
						String invoiceReq = WSClient.createSOAPMessage("OWSInvoice", "DS_01");
						String invoiceRes = WSClient.processSOAPMessage(invoiceReq);

						// Validating response of OWS Invoice Operation
						if(WSAssert.assertIfElementExists(invoiceRes,
								"InvoiceResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(invoiceRes,
									"InvoiceResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								if(WSAssert.assertIfElementExists(invoiceRes, "Result_Text_TextElement", false)) {
									if(WSAssert.assertIfElementValueEquals(invoiceRes, "Result_Text_TextElement", "No Folio Records Found", false)) {

									}
								}


							}else {
								if(WSAssert.assertIfElementExists(invoiceRes, "Result_Text_TextElement", true)) {
									String message=WSAssert.getElementValue(invoiceRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "The error displayed in the invoice response is :"+ message);
									if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", true)) {
										String operaErrorCode=WSAssert.getElementValue(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

									}
								}
								if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_GDSError", true)) {
									String message=WSAssert.getElementValue(invoiceRes, "InvoiceResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the invoice response is :"+ message);
								}
							}
						}else {
							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
						}


						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :1
	@Test(groups={"minimumRegression","ReservationAdvanced","Invoice","OWS"})
	public void invoice_39102() {
		try {
			String testname = "invoice_39102";
			WSClient.startTest(testname, "Verify that 'No folio records' is obtained by passing details of reservation to which no charges are attached while passing reservation_id type as External", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" }))	{

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above profile
					if(!(resvID = createReservation("DS_04")).equals("error"))
					{
						// Setting Variables
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						setOWSHeader();

						String resort = OPERALib.getResort();
						String channel = OWSLib.getChannel();
						WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

						// Creating request and processing response for OWS Invoice Operation
						String invoiceReq = WSClient.createSOAPMessage("OWSInvoice", "DS_02");
						String invoiceRes = WSClient.processSOAPMessage(invoiceReq);

						// Validating response of OWS Invoice Operation
						if(WSAssert.assertIfElementExists(invoiceRes,
								"InvoiceResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(invoiceRes,
									"InvoiceResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								if(WSAssert.assertIfElementExists(invoiceRes, "Result_Text_TextElement", false)) {
									if(WSAssert.assertIfElementValueEquals(invoiceRes, "Result_Text_TextElement", "No Folio Records Found", false)) {

									}
								}


							}else {
								if(WSAssert.assertIfElementExists(invoiceRes, "Result_Text_TextElement", true)) {
									String message=WSAssert.getElementValue(invoiceRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "The error displayed in the invoice response is :"+ message);
									if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", true)) {
										String operaErrorCode=WSAssert.getElementValue(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

									}
								}
								if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_GDSError", true)) {
									String message=WSAssert.getElementValue(invoiceRes, "InvoiceResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the invoice response is :"+ message);
								}
							}
						}else {
							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
						}


						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :2
	@Test(groups={"minimumRegression","ReservationAdvanced","Invoice","OWS"})
	public void invoice_39103() {
		try {
			String testname = "invoice_39103";
			WSClient.startTest(testname, "Verify that error message is obtained by passing invalid hotel code", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" }))	{

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above profile
					if(!(resvID = createReservation("DS_04")).equals("error"))
					{
						// Setting Variables
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						setOWSHeader();

						String resort = OPERALib.getResort();
						String channel = OWSLib.getChannel();
						WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

						// Creating request and processing response for OWS Invoice Operation
						String invoiceReq = WSClient.createSOAPMessage("OWSInvoice", "DS_03");
						String invoiceRes = WSClient.processSOAPMessage(invoiceReq);

						// Validating response of OWS Invoice Operation
						if(WSAssert.assertIfElementExists(invoiceRes,
								"InvoiceResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(invoiceRes,
									"InvoiceResponse_Result_resultStatusFlag", "FAIL", false)) {

								if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", false)) {
									if(WSAssert.assertIfElementValueEquals(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", "INVALID_PROPERTY", false)) {

									}
								}
							}else {
								if(WSAssert.assertIfElementExists(invoiceRes, "Result_Text_TextElement", true)) {
									String message=WSAssert.getElementValue(invoiceRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "The error displayed in the invoice response is :"+ message);
									if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", true)) {
										String operaErrorCode=WSAssert.getElementValue(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

									}
								}
								if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_GDSError", true)) {
									String message=WSAssert.getElementValue(invoiceRes, "InvoiceResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the invoice response is :"+ message);
								}
							}
						}else {
							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
						}


						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :3
	@Test(groups={"minimumRegression","ReservationAdvanced","Invoice","OWS"})
	public void invoice_39104() {
		try {
			String testname = "invoice_39104";
			WSClient.startTest(testname, "Verify that 'No folio records' is obtained by passing details of reservation to which no charges are attached while passing invalid chain code", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" }))	{

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above profile
					if(!(resvID = createReservation("DS_04")).equals("error"))
					{
						// Setting Variables
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						setOWSHeader();

						String resort = OPERALib.getResort();
						String channel = OWSLib.getChannel();
						WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

						// Creating request and processing response for OWS Invoice Operation
						String invoiceReq = WSClient.createSOAPMessage("OWSInvoice", "DS_04");
						String invoiceRes = WSClient.processSOAPMessage(invoiceReq);

						// Validating response of OWS Invoice Operation
						if(WSAssert.assertIfElementExists(invoiceRes,
								"InvoiceResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(invoiceRes,
									"InvoiceResponse_Result_resultStatusFlag", "SUCCESS", false)) {

								if(WSAssert.assertIfElementExists(invoiceRes, "Result_Text_TextElement", false)) {
									if(WSAssert.assertIfElementValueEquals(invoiceRes, "Result_Text_TextElement", "No Folio Records Found", false)) {

									}
								}


							}else {
								if(WSAssert.assertIfElementExists(invoiceRes, "Result_Text_TextElement", true)) {
									String message=WSAssert.getElementValue(invoiceRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "The error displayed in the invoice response is :"+ message);
									if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", true)) {
										String operaErrorCode=WSAssert.getElementValue(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

									}
								}
								if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_GDSError", true)) {
									String message=WSAssert.getElementValue(invoiceRes, "InvoiceResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the invoice response is :"+ message);
								}
							}
						}else {
							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
						}


						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// minimum Regression Test Case :4
	@Test(groups={"minimumRegression","ReservationAdvanced","Invoice","OWS"})
	public void invoice_39107() {
		try {
			String testname = "invoice_39107";
			WSClient.startTest(testname, "Verify that error message is obtained by not passing reservation id", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" }))	{

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above profile
					if(!(resvID = createReservation("DS_04")).equals("error"))
					{
						// Setting Variables
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						setOWSHeader();

						String resort = OPERALib.getResort();
						String channel = OWSLib.getChannel();
						WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

						// Creating request and processing response for OWS Invoice Operation
						String invoiceReq = WSClient.createSOAPMessage("OWSInvoice", "DS_05");
						String invoiceRes = WSClient.processSOAPMessage(invoiceReq);

						// Validating response of OWS Invoice Operation
						if(WSAssert.assertIfElementExists(invoiceRes,
								"InvoiceResponse_Result_resultStatusFlag", true)) {
							if (WSAssert.assertIfElementValueEquals(invoiceRes,
									"InvoiceResponse_Result_resultStatusFlag", "FAIL", false)) {

								if(WSAssert.assertIfElementExists(invoiceRes, "Result_Text_TextElement", false)) {
									if(WSAssert.assertIfElementContains(invoiceRes, "Result_Text_TextElement", "Opera Reservation Id or Key Track 2 is Missing in Request Message", false)) {

									}
								}


							}else {
								if(WSAssert.assertIfElementExists(invoiceRes, "Result_Text_TextElement", true)) {
									String message=WSAssert.getElementValue(invoiceRes, "Result_Text_TextElement", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "The error displayed in the invoice response is :"+ message);
									if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", true)) {
										String operaErrorCode=WSAssert.getElementValue(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
										WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

									}
								}
								if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_GDSError", true)) {
									String message=WSAssert.getElementValue(invoiceRes, "InvoiceResponse_Result_GDSError", XMLType.RESPONSE);
									WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the invoice response is :"+ message);
								}
							}
						}else {
							WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
						}


						// Canceling Reservation
						//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
						CancelReservation.cancelReservation("DS_02");
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :5
	@Test(groups={"sanity","ReservationAdvanced","Invoice","OWS", "testmakepayment1"})
	public void invoice_39108() {
		try {
			String testname = "invoice_39108";
			WSClient.startTest(testname, "Verify that invoice associated to a reservation is fetched to which charges are attached", "sanity");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" }))	{

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above profile
					if(!(resvID = createReservation("DS_04")).equals("error"))
					{
						// Setting Variables
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						/********
						 * Prerequisite : Fetch Hotel Rooms
						 **************/
						fetchHotelRooms("DS_03");
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);


						String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
						String checkInRes = WSClient.processSOAPMessage(checkInReq);

						if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){

							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");

							WSClient.setData("{var_cashierID}", OperaPropConfig.getDataSetForCode("Cashiers","DS_01"));
							WSClient.setData("{var_trx}", OperaPropConfig.getDataSetForCode("TransactionCode","DS_04"));
							String postBillingChargesReq = WSClient.createSOAPMessage("PostBillingCharges","DS_01");
							WSClient.writeToReport(LogStatus.INFO, "reached");
							String postBillingChargesRes = WSClient.processSOAPMessage(postBillingChargesReq);

							if(WSAssert.assertIfElementExists(postBillingChargesRes,"PostBillingChargesRS_Success",true)) {

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Posted Billing Charges to Profile</b>");

								setOWSHeader();

								resort = OPERALib.getResort();
								String channel = OWSLib.getChannel();
								WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

								// Creating request and processing response for OWS Invoice Operation
								String invoiceReq = WSClient.createSOAPMessage("OWSInvoice", "DS_01");
								String invoiceRes = WSClient.processSOAPMessage(invoiceReq);

								// Validating response of OWS Invoice Operation
								if(WSAssert.assertIfElementExists(invoiceRes,
										"InvoiceResponse_Result_resultStatusFlag", true)) {
									if (WSAssert.assertIfElementValueEquals(invoiceRes,
											"InvoiceResponse_Result_resultStatusFlag", "SUCCESS", false)) {

										// Database Validation
										LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
										LinkedHashMap<String, String> actualValues = new LinkedHashMap<String,String>();
										LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
										xPath.put("Invoice_Name_firstName", "InvoiceResponse_Invoice_Name");
										xPath.put("Invoice_Name_lastName", "InvoiceResponse_Invoice_Name");
										xPath.put("Invoice_ProfileIDs_UniqueID", "InvoiceResponse_Invoice_ProfileIDs");
										xPath.put("InvoiceResponse_Invoice_BillItems_TransactionNo", "InvoiceResponse_Invoice_BillItems");
										xPath.put("InvoiceResponse_Invoice_BillItems_TransactionCode", "InvoiceResponse_Invoice_BillItems");
										xPath.put("Invoice_BillItems_Amount", "InvoiceResponse_Invoice_BillItems");
										xPath.put("Invoice_BillItems_OriginalRoom", "InvoiceResponse_Invoice_BillItems");
										xPath.put("Invoice_BillItems_Quantity", "InvoiceResponse_Invoice_BillItems");
										xPath.put("Invoice_BillItems_Reference", "InvoiceResponse_Invoice_BillItems");
										actualValues = WSClient.getSingleNodeList(invoiceRes, xPath, false, XMLType.RESPONSE);
										String query = WSClient.getQuery("QS_01");
										expectedValues=WSClient.getDBRow(query);
										WSAssert.assertEquals(expectedValues, actualValues, false);


									}else {
										if(WSAssert.assertIfElementExists(invoiceRes, "Result_Text_TextElement", true)) {
											String message=WSAssert.getElementValue(invoiceRes, "Result_Text_TextElement", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "The error displayed in the invoice response is :"+ message);
											if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", true)) {
												String operaErrorCode=WSAssert.getElementValue(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

											}
										}
										if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_GDSError", true)) {
											String message=WSAssert.getElementValue(invoiceRes, "InvoiceResponse_Result_GDSError", XMLType.RESPONSE);
											WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the invoice response is :"+ message);
										}
									}
								}else {
									WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
								}

							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisites failed!------PostBillingCharges------Blocked");
							}

							// Canceling Reservation
							//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
							CancelReservation.cancelReservation("DS_02");

						}else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisites failed!------CheckinReservation-----Blocked");
						}
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}

	// Minimum Regression Test Case :6
	// Minimum Regression Test Case :6
	@Test(groups={"minimumRegression","ReservationAdvanced","Invoice","OWS","rerun"})
	public void invoice_39109() {
		try {
			String testname = "invoice_39109";
			WSClient.startTest(testname, "Verify that multiple folio records are fetched by passing reservation to which mutilple charges are associated", "minimumRegression");

			if (OperaPropConfig.getPropertyConfigResults(new String[] { "RateCode", "RoomType", "SourceCode", "MarketCode", "PaymentMethod" }))	{

				String UniqueId,resvID;

				// Creating a profile
				if(!(UniqueId = createProfile("DS_01")).equals("error"))
				{
					// Creating reservation for above profile
					if(!(resvID = createReservation("DS_04")).equals("error"))
					{
						// Setting Variables
						System.out.println(UniqueId);
						WSClient.setData("{var_profileId}", UniqueId);
						WSClient.setData("{var_resvId}", resvID);
						WSClient.setData("{var_reservation_id}", resvID);

						/********
						 * Prerequisite : Fetch Hotel Rooms
						 **************/
						fetchHotelRooms("DS_03");
						String resort = OPERALib.getResort();
						WSClient.setData("{var_owsresort}", resort);

						String checkInReq = WSClient.createSOAPMessage("CheckinReservation","DS_01");
						String checkInRes = WSClient.processSOAPMessage(checkInReq);

						if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){

							WSClient.writeToReport(LogStatus.INFO, "<b>Successfully CheckedIn Reservation</b>");

							WSClient.setData("{var_cashierID}", OperaPropConfig.getDataSetForCode("Cashiers","DS_01"));
							WSClient.setData("{var_trx}", OperaPropConfig.getDataSetForCode("TransactionCode","DS_04"));
							String postBillingChargesReq = WSClient.createSOAPMessage("PostBillingCharges","DS_01");
							WSClient.writeToReport(LogStatus.INFO, "reached");
							String postBillingChargesRes = WSClient.processSOAPMessage(postBillingChargesReq);

							if(WSAssert.assertIfElementExists(postBillingChargesRes,"PostBillingChargesRS_Success",true)) {

								WSClient.writeToReport(LogStatus.INFO, "<b>Successfully Posted Billing Charges to Profile</b>");

								String postBillingChargesReq2 = WSClient.createSOAPMessage("PostBillingCharges","DS_01");
								WSClient.writeToReport(LogStatus.INFO, "reached");
								String postBillingChargesRes2 = WSClient.processSOAPMessage(postBillingChargesReq2);

								if(WSAssert.assertIfElementExists(postBillingChargesRes,"PostBillingChargesRS_Success",true)) {

									setOWSHeader();

									resort = OPERALib.getResort();
									String channel = OWSLib.getChannel();
									WSClient.setData("{var_owsresort}", OWSLib.getChannelResort(resort, channel));

									// Creating request and processing response for OWS Invoice Operation
									String invoiceReq = WSClient.createSOAPMessage("OWSInvoice", "DS_01");
									String invoiceRes = WSClient.processSOAPMessage(invoiceReq);

									// Validating response of OWS Invoice Operation
									if(WSAssert.assertIfElementExists(invoiceRes,
											"InvoiceResponse_Result_resultStatusFlag", true)) {
										if (WSAssert.assertIfElementValueEquals(invoiceRes,
												"InvoiceResponse_Result_resultStatusFlag", "SUCCESS", false)) {

											// Database Validation
											LinkedHashMap<String,String> expectedValues = new LinkedHashMap<String,String>();
											LinkedHashMap<String, String> actualValues = new LinkedHashMap<String,String>();
											LinkedHashMap<String,String> xPath = new LinkedHashMap<String,String>();
											List<LinkedHashMap<String,String>> expectedValues2 = new ArrayList<LinkedHashMap<String,String>>();
											List<LinkedHashMap<String, String>> actualValues2 = new ArrayList<LinkedHashMap<String,String>>();
											LinkedHashMap<String,String> xPath2 = new LinkedHashMap<String,String>();
											xPath.put("Invoice_Name_firstName", "InvoiceResponse_Invoice_Name");
											xPath.put("Invoice_Name_lastName", "InvoiceResponse_Invoice_Name");
											xPath.put("Invoice_ProfileIDs_UniqueID", "InvoiceResponse_Invoice_ProfileIDs");
											actualValues = WSClient.getSingleNodeList(invoiceRes, xPath, false, XMLType.RESPONSE);
											String query = WSClient.getQuery("QS_02");
											expectedValues=WSClient.getDBRow(query);
											WSAssert.assertEquals(expectedValues,actualValues,false);
											xPath2.put("InvoiceResponse_Invoice_BillItems_TransactionNo", "InvoiceResponse_Invoice_BillItems");
											xPath2.put("InvoiceResponse_Invoice_BillItems_TransactionCode", "InvoiceResponse_Invoice_BillItems");
											xPath2.put("Invoice_BillItems_Amount", "InvoiceResponse_Invoice_BillItems");
											xPath2.put("Invoice_BillItems_OriginalRoom", "InvoiceResponse_Invoice_BillItems");
											xPath2.put("Invoice_BillItems_Quantity", "InvoiceResponse_Invoice_BillItems");
											xPath2.put("Invoice_BillItems_Reference", "InvoiceResponse_Invoice_BillItems");
											actualValues2 = WSClient.getMultipleNodeList(invoiceRes, xPath2, false, XMLType.RESPONSE);
											String query2 = WSClient.getQuery("QS_03");
											expectedValues2=WSClient.getDBRows(query2);
											WSAssert.assertEquals(actualValues2, expectedValues2, false);


										}else {
											if(WSAssert.assertIfElementExists(invoiceRes, "Result_Text_TextElement", true)) {
												String message=WSAssert.getElementValue(invoiceRes, "Result_Text_TextElement", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "The error displayed in the invoice response is :"+ message);
												if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", true)) {
													String operaErrorCode=WSAssert.getElementValue(invoiceRes, "InvoiceResponse_Result_OperaErrorCode", XMLType.RESPONSE);
													WSClient.writeToReport(LogStatus.INFO, "Opera Error Code :"+ operaErrorCode);

												}
											}
											if(WSAssert.assertIfElementExists(invoiceRes, "InvoiceResponse_Result_GDSError", true)) {
												String message=WSAssert.getElementValue(invoiceRes, "InvoiceResponse_Result_GDSError", XMLType.RESPONSE);
												WSClient.writeToReport(LogStatus.INFO, "The gds error displayed in the invoice response is :"+ message);
											}
										}
									}else {
										WSClient.writeToReport(LogStatus.FAIL, "ResultFlag doesn't exist in Response!");
									}
								}else {
									WSClient.writeToReport(LogStatus.WARNING,
											"Prerequisites failed!------PostBillingCharges------Blocked");
								}
							} else {
								WSClient.writeToReport(LogStatus.WARNING,
										"Prerequisites failed!------PostBillingCharges------Blocked");
							}

							// Canceling Reservation
							//WSClient.writeToReport(LogStatus.INFO, "<b>"+"Cancelling Reservation"+"</b>");
							CancelReservation.cancelReservation("DS_02");

						}else {
							WSClient.writeToReport(LogStatus.WARNING,
									"Prerequisites failed!------CheckinReservation-----Blocked");
						}
					}
				}
			}else {
				WSClient.writeToReport(LogStatus.WARNING, "PropertyConfig variables not available.");
			}

		}catch(Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test case due to" + e);
		}
	}
}
