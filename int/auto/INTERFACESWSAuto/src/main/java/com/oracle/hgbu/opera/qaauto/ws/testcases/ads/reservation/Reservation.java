package com.oracle.hgbu.opera.qaauto.ws.testcases.ads.reservation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.relevantcodes.customextentreports.LogStatus;

public class Reservation extends WSSetUp {
	@Test(groups = {"BAT", "Reservation", "ADS" })
	@Parameters({"schema"})
	public void reservationCreationADS(String schema) {
		WSClient.startTest("reservationCreationADS","Verify Reservation is created successfully","bat");
		String sResort = "", sChannel = "", sUsername = "", sPassword = "", sChain = "", sChannelType = "", sChannelCarrier = "", sExtResort = "", qryAddressTypes = "", pmsChain="";
		Boolean availabilityFlag = false;
		try {
			sResort = OPERALib.getResort();
			sChannel = OWSLib.getChannel();
			sUsername = OPERALib.getUserName();
			sPassword = OPERALib.getPassword();
			pmsChain = OPERALib.getChain();
			if(schema.equalsIgnoreCase("CENTRAL"))
				sChain = OWSLib.getGCRChainCode(sResort, sChannel);
			else 
			    sChain = pmsChain;
			sChannelType = OWSLib.getChannelType(sChannel);
			sChannelCarrier = OWSLib.getChannelCarier(sResort, sChannel);
			sExtResort = OWSLib.getChannelResort(sResort, sChannel);

			// Set data for the Parameters required on the OWS SOAP header for all OWS Operations in this test case
			WSClient.setData("{var_userName}", sUsername);
			WSClient.setData("{var_password}", sPassword);
			WSClient.setData("{var_extResort}", sExtResort);
			WSClient.setData("{var_resort}", sResort);
			WSClient.setData("{var_systemType}", sChannelType);
			WSClient.setData("{var_channelCarrier}", sChannelCarrier);
			WSClient.setData("{var_chain}", sChain);
			WSClient.setData("{var_channel}", sChannel);
			WSClient.setData("{var_pmsChain}", pmsChain);
	
			/***********************************************************************
			 * Prerequisite 2: Fetch Availability
			 ***********************************************************************/
			String roomType = "", rateCode = "", beginDate = "", endDate = "", qryAvailability="", avTotalRoomRate="", avRate="", avRoomType="";
			LinkedHashMap<String,String> availabilityMap = new LinkedHashMap<String,String>();
			WSClient.setData("{var_msgID}", UUID.randomUUID().toString());
			
			if(schema.equals("CENTRAL")) 
				qryAvailability = WSClient.getQuery("HotelAvail", "QS_02",false);
			else 
				qryAvailability = WSClient.getQuery("HotelAvail", "QS_03",false);	
			
			availabilityMap = WSClient.getDBRow(qryAvailability);
			if (availabilityMap.size() > 0) { 
					roomType = availabilityMap.get("GDS_ROOM_CATEGORY");
					rateCode = availabilityMap.get("GDS_RATE_CODE");
					
					if(schema.equalsIgnoreCase("CENTRAL")) {
						beginDate = WSClient.getKeywordData("{KEYWORD_SYSTEMDATE}");
						endDate = WSClient.getKeywordData("{KEYWORD_SYSTEMDATE_ADD_1}");
					}
					else {
						beginDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE}");
						endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_ADD_1}");
					}
		
					System.out.println(beginDate);
					// Set the data required for Availability Request
					WSClient.setData("{var_startDate}", beginDate);
					WSClient.setData("{var_endDate}", endDate);					
					WSClient.setData("{var_rateCode}", rateCode);
					WSClient.setData("{var_roomType}", roomType);
					
					// Fetch Availability for the given channel Room Type and Rate Code
					String req_availability = WSClient.createXMLMessage("HotelAvail", "DS_01");
					String res_availability = WSClient.processXMLMessage(req_availability, "POST");
					
					if( WSAssert.assertIfElementExists(res_availability, "FIDELIO_HotelAvailRS_Success", false)) {
						if (WSAssert.assertIfElementExists(res_availability, "AvailabilityResponse_RatePlans_RatePlan_ratePlanCode", true) && WSAssert.assertIfElementExists(res_availability, "AvailabilityResponse_RoomTypes_RoomType_roomTypeCode", true)) {
								availabilityFlag = true;
								avRate = WSClient.getElementValue(res_availability, "AvailabilityResponse_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
								avRoomType  = WSClient.getElementValue(res_availability, "AvailabilityResponse_RoomTypes_RoomType_roomTypeCode", XMLType.RESPONSE);
								avTotalRoomRate = WSClient.getElementValue(res_availability, "RoomRates_RoomRate_TotalRoomRate", XMLType.RESPONSE);
								WSClient.setData("{var_rateCode}", avRate);
								WSClient.setData("{var_roomType}", avRoomType);
								WSClient.setData("{var_totalRoomRate}", avTotalRoomRate);
								WSClient.writeToReport(LogStatus.INFO, "<b>Availability exists for Rate Code -> " + avRate + " and Room Type -> " + avRoomType + "</b>");
						} 
						else {
								WSClient.writeToReport(LogStatus.WARNING, "Availability doesn't exist");
						}
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "Availability doesn't exist");
					}
			}
			else {
				WSClient.writeToReport(LogStatus.WARNING, "Rate Codes and Room Types are not configured for the channel "+sChannel);
			}
			
			
			/***********************************************************************
			 * Verification Point : Create Booking
			 ***********************************************************************/
				
			String confirmationNumber = "", externalBookingNumber="";
			LinkedHashMap<String,String> reservationMap = new LinkedHashMap<String,String>();
			LinkedHashMap<String,String> gdsHeaderMap = new LinkedHashMap<String,String>();
			LinkedHashMap<String,String> depositMap = new LinkedHashMap<String,String>();
			String req_createBooking="",res_createBooking="";
			// Proceed with Reservation Creation if Availability Exists
			if (availabilityFlag == true) {
				
				// Set data for the Message ID Parameter required on the OWS SOAP header for Availability Operation
				WSClient.setData("{var_msgID}", UUID.randomUUID().toString());				
			
				 req_createBooking = WSClient.createXMLMessage("ADSReservation", "DS_01");
				 res_createBooking = WSClient.processXMLMessage(req_createBooking, "POST");
				
				if(WSAssert.assertIfElementExists(res_createBooking, "FIDELIO_HotelResRS_Success", false)) {
					confirmationNumber = WSAssert.getAttributeValueByAttribute(res_createBooking, "HotelReservation_HotelReservationIDs_HotelReservationID", "idValue", "Confirmation", XMLType.RESPONSE);
					externalBookingNumber = WSAssert.getAttributeValueByAttribute(res_createBooking, "HotelReservation_HotelReservationIDs_HotelReservationID", "idValue", "Locator", XMLType.RESPONSE);
					String reqLocator = WSClient.getElementValue(req_createBooking, "HotelReservation_HotelReservationIDs_HotelReservationID_idValue", XMLType.REQUEST);
					String sourceOfBusiness = WSClient.getElementValue(req_createBooking, "POS_Source[3]_UniqueId_id", XMLType.REQUEST);
					String noOfRooms = WSClient.getElementValue(req_createBooking, "HotelReservation_RoomStays_RoomStay_numberOfRooms", XMLType.REQUEST);
					String fname = WSClient.getElementValue(req_createBooking, "Customer_PersonName_givenName", XMLType.REQUEST);
					String sname = WSClient.getElementValue(req_createBooking, "Customer_PersonName_surname", XMLType.REQUEST);
					String mname = WSClient.getElementValue(req_createBooking, "Customer_PersonName_middleName", XMLType.REQUEST);
					
					WSClient.writeToReport(LogStatus.INFO, "<b>VALIDATING RESPONSE AGAINST REQUEST PAYLOAD</b>");
					
					if(confirmationNumber.contains("doesn't exist")) {
						confirmationNumber = "";
						WSClient.writeToReport(LogStatus.FAIL, "Confirmation Number is not populated on response");
					}
					else {
						WSClient.setData("{var_confirmationNumber}", confirmationNumber);
						WSClient.writeToReport(LogStatus.PASS, "Confirmation is populated on response >> "+confirmationNumber);
					}
					
					if(externalBookingNumber.equalsIgnoreCase(reqLocator)) 
						WSClient.writeToReport(LogStatus.PASS, "Locator/External Booking Reference Number >> Expected: "+reqLocator+ " Actual: "+externalBookingNumber);
					else 
						WSClient.writeToReport(LogStatus.FAIL, "Locator/External Booking Reference Number >> Expected: "+reqLocator+ " Actual: ");
					
					WSAssert.assertIfElementValueEquals(res_createBooking, "HotelReservation_RoomStays_RoomStay_otherReservationStatusType", "Reserved", false);
					WSAssert.assertIfElementValueEquals(res_createBooking, "HotelReservation_RoomStays_RoomStay_roomTypeCode", avRoomType, false);
					WSAssert.assertIfElementValueEquals(res_createBooking, "RoomStay_RatePlans_RatePlan_ratePlanCode", avRate, false);
					WSAssert.assertIfElementValueEquals(res_createBooking, "Rates_Rate_Amount", avTotalRoomRate, false);
					WSAssert.assertIfElementValueEquals(res_createBooking, "RoomStay_StayDateRange_HardDateRange_startDate", beginDate, false);
					WSAssert.assertIfElementValueEquals(res_createBooking, "RoomStay_StayDateRange_HardDateRange_endDate", endDate, false);
					WSAssert.assertIfElementValueEquals(res_createBooking, "FIDELIO_HotelResRS_HotelReservations_HotelReservation_chainCode", sChain, false);
					WSAssert.assertIfElementValueEquals(res_createBooking, "FIDELIO_HotelResRS_HotelReservations_HotelReservation_hotelCode", sResort, false);
					WSAssert.assertIfElementValueEquals(res_createBooking, "HotelReservation_RoomStays_RoomStay_numberOfRooms", noOfRooms, false);
					WSAssert.assertIfElementValueEquals(res_createBooking, "Customer_PersonName_givenName", fname, false);
					WSAssert.assertIfElementValueEquals(res_createBooking, "Customer_PersonName_middleName", mname, false);
					WSAssert.assertIfElementValueEquals(res_createBooking, "Customer_PersonName_surname", sname, false);
					WSAssert.assertIfElementValueEquals(res_createBooking, "Customer_PersonName_nameOrdered", sname.toUpperCase()+"/"+fname.toUpperCase(), false);
					
					WSClient.writeToReport(LogStatus.INFO, "<b>VALIDATING RESERVATION_NAME TABLE AGAINST REQUEST PAYLOAD</b>");
					if(confirmationNumber != "") {
						String resvQuery = WSClient.getQuery("QS_01");
						reservationMap =  WSClient.getDBRow(resvQuery);
		
						if(reservationMap.size() > 0) {
							if (WSAssert.assertEquals("RESERVED", reservationMap.get("RESV_STATUS"), true)) 
								WSClient.writeToReport(LogStatus.PASS, "Reservation Status -> Expected value: RESERVED Actual value : " + reservationMap.get("RESV_STATUS"));
							else
								WSClient.writeToReport(LogStatus.FAIL, "Reservation Status -> Expected value: RESERVED Actual value : " + reservationMap.get("RESV_STATUS"));
							
							if (WSAssert.assertEquals(beginDate, reservationMap.get("BEGIN_DATE"), true)) 
								WSClient.writeToReport(LogStatus.PASS, "Begin Date -> Expected value: " + beginDate + " Actual value : " + reservationMap.get("BEGIN_DATE"));
							else
								WSClient.writeToReport(LogStatus.FAIL, "Begin Date -> Expected value: " + beginDate + " Actual value : " + reservationMap.get("BEGIN_DATE"));
									
							if (WSAssert.assertEquals(endDate, reservationMap.get("END_DATE"), true)) 
								WSClient.writeToReport(LogStatus.PASS, "End Date -> Expected value: " + endDate + " Actual value : " + reservationMap.get("END_DATE"));
							else
								WSClient.writeToReport(LogStatus.FAIL, "End Date -> Expected value: " + endDate + " Actual value : " + reservationMap.get("END_DATE"));
						}
						else {
							WSClient.writeToReport(LogStatus.FAIL, "No record found in RESERVATION_NAME table");
						}
						
												
						WSClient.writeToReport(LogStatus.INFO, "<b>VALIDATING GDS$_RESERVATION_HEADER TABLE</b>");
						String gdsQuery = WSClient.getQuery("QS_02");
						gdsHeaderMap =  WSClient.getDBRow(gdsQuery);
						if(gdsHeaderMap.size() > 0) {
							if (WSAssert.assertEquals(confirmationNumber, gdsHeaderMap.get("CONFIRMATION_NO"), true)) 
								WSClient.writeToReport(LogStatus.PASS, "Confirmation No -> Expected value: " + confirmationNumber + " Actual value : " + gdsHeaderMap.get("CONFIRMATION_NO"));
							else
								WSClient.writeToReport(LogStatus.FAIL, "Confirmation No -> Expected value: " + confirmationNumber + " Actual value : " + gdsHeaderMap.get("CONFIRMATION_NO"));
							
							if (WSAssert.assertEquals("RESERVED", gdsHeaderMap.get("RESV_STATUS"), true)) 
								WSClient.writeToReport(LogStatus.PASS, "Reservation Status -> Expected value: RESERVED Actual value : " + gdsHeaderMap.get("RESV_STATUS"));
							else
								WSClient.writeToReport(LogStatus.FAIL, "Reservation Status -> Expected value: RESERVED Actual value : " + gdsHeaderMap.get("RESV_STATUS"));
							
							if (WSAssert.assertEquals(sResort, gdsHeaderMap.get("RESORT"), true)) 
								WSClient.writeToReport(LogStatus.PASS, "Resort -> Expected value: " + sResort + " Actual value : " + gdsHeaderMap.get("RESORT"));
							else
								WSClient.writeToReport(LogStatus.FAIL, "Resort-> Expected value: " + sResort + " Actual value : " + gdsHeaderMap.get("RESORT"));
							
							if (WSAssert.assertEquals(externalBookingNumber, gdsHeaderMap.get("GDS_RECORD_LOCATOR"), true)) 
								WSClient.writeToReport(LogStatus.PASS, "GDS Locator -> Expected value: " + externalBookingNumber + " Actual value : " + gdsHeaderMap.get("GDS_RECORD_LOCATOR"));
							else
								WSClient.writeToReport(LogStatus.FAIL, "GDS Locator  -> Expected value: " + externalBookingNumber + " Actual value : " + gdsHeaderMap.get("GDS_RECORD_LOCATOR"));
							
							if (WSAssert.assertEquals(sname, gdsHeaderMap.get("GUEST_NAME"), true)) 
								WSClient.writeToReport(LogStatus.PASS, "Guest Name -> Expected value: " + sname + " Actual value : " + gdsHeaderMap.get("GUEST_NAME"));
							else
								WSClient.writeToReport(LogStatus.FAIL, "Guest Name -> Expected value: " + sname + " Actual value : " + gdsHeaderMap.get("GUEST_NAME"));
							
						}
						else {
							WSClient.writeToReport(LogStatus.FAIL, "Record not found in GDS$_RESERVATION_HEADER table");
						}
					}
					else {
						WSClient.writeToReport(LogStatus.FAIL, "Booking Creation is failed");
					}
				}
				else {
					WSClient.writeToReport(LogStatus.FAIL, "Reservation Creation via ADS is failed");
				}
			}
			
				
		}
		catch(Exception e) {
			System.out.println("Error Occurred in the Create Booking flow "+e.getMessage());
			e.printStackTrace();
			WSClient.writeToReport(LogStatus.ERROR, "Error Occurred in the Create Booking flow. "+e.getMessage());
		}
	}
}
