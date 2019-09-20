package com.oracle.hgbu.opera.qaauto.ws.testcases.ows.reservation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchBooking extends WSSetUp{
	@Test(groups = { "BAT", "FetchBooking", "Reservation", "OWS" })
	@Parameters({"schema"})
	public void fetchReservation_OWS(String schema) {
		WSClient.startTest("fetchReservation_OWS", "Verify Reservation retrieval is successful via OWS Channel", "bat");
		String sResort = "", sChannel = "", sUsername = "", sPassword = "", sChain = "", sChannelType = "", sChannelCarrier = "", sExtResort = "", qryAvailability = "";
		Boolean profileCreationFlag = false, availabilityFlag = false, createBookingFlag = false;
		HashMap<String, String> availabilityMap = new HashMap<String, String>();
		try {
			
			sResort = OPERALib.getResort();
			sChannel = OWSLib.getChannel();
			sUsername = OPERALib.getUserName();
			sPassword = OPERALib.getPassword();
			if(schema.equalsIgnoreCase("CENTRAL"))
				sChain = OWSLib.getGCRChainCode(sResort, sChannel);
			else 
				sChain = OPERALib.getChain();
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
			
			/***********************************************************************
			 * Prerequisite 1: Create a Profile-
			 ***********************************************************************/
			// Set data for the Message ID Parameter required on the OWS SOAP header for Availability Operation
			WSClient.setData("{var_msgID}", UUID.randomUUID().toString());
			
			// Create Profile
			String req_RegisterName = WSClient.createSOAPMessage("OWSRegisterName", "DS_02");
			String res_RegisterName = WSClient.processSOAPMessage(req_RegisterName);

			String operaNameId = "";
			// Validate if the response shows SUCCESS
			if (WSAssert.assertIfElementValueEquals(res_RegisterName, "RegisterNameResponse_Result_resultStatusFlag", "SUCCESS", true)) {
				if (WSAssert.assertIfElementExists(res_RegisterName, "Result_IDs_IDPair_operaId", true)) {
					operaNameId = WSClient.getElementValue(res_RegisterName, "Result_IDs_IDPair_operaId", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO, "<b>Profile Id: " + operaNameId + "</b>");
					WSClient.setData("{var_profileId}", operaNameId);
					profileCreationFlag = true;
				} else {
					WSClient.writeToReport(LogStatus.WARNING, "Profile creation failed, Cannot proceed further.");
				}
			}
			else {
				WSClient.writeToReport(LogStatus.WARNING, "Profile creation failed, Cannot proceed further.");
			}
			
			/***********************************************************************
			 * Prerequisite 2: Fetch Availability
			 ***********************************************************************/
			String roomType = "", rateCode = "", beginDate = "", endDate = "", avResvType = "";
			// Proceed with retrieving availability if profile creation is successful
			if (profileCreationFlag == true) {
				
				// Set data for the Message ID Parameter required on the OWS SOAP header for Availability Operation
				WSClient.setData("{var_msgID}", UUID.randomUUID().toString());
				
				// Retrieve the Channel Rate and Room Type that has rooms available from DB
				if(schema.equals("CENTRAL")) 
					qryAvailability = WSClient.getQuery("OWSAvailability", "QS_23");
				else 
					qryAvailability = WSClient.getQuery("OWSAvailability", "QS_24");	
				
				availabilityMap = WSClient.getDBRow(qryAvailability);

				if (availabilityMap.size() > 0) {
					roomType = availabilityMap.get("GDS_ROOM_CATEGORY");
					rateCode = availabilityMap.get("GDS_RATE_CODE");
					
					if(schema.equalsIgnoreCase("CENTRAL")) {
						beginDate = WSClient.getKeywordData("{KEYWORD_SYSTEMDATE_TFORMAT}").substring(0, 19);
						endDate = WSClient.getKeywordData("{KEYWORD_SYSTEMDATE_TFORMAT_ADD_24}").substring(0, 19);
					}
					else {
						beginDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT}").substring(0, 19);
						endDate = WSClient.getKeywordData("{KEYWORD_BUSINESSDATE_TFORMAT_ADD_24}").substring(0, 19);
					}
					
					// Set the data required for Availability Request
					WSClient.setData("{var_startDate}", beginDate);
					WSClient.setData("{var_endDate}", endDate);
					WSClient.setData("{var_rateCode}", rateCode);
					WSClient.setData("{var_roomType}", roomType);

					// Fetch Availability for the given channel Room Type and
					// Rate Code
					String req_availability = WSClient.createSOAPMessage("OWSAvailability", "DS_20");
					String res_availability = WSClient.processSOAPMessage(req_availability);

					// Validate if the response shows SUCCESS
					if (WSAssert.assertIfElementValueEquals(res_availability, "AvailabilityResponse_Result_resultStatusFlag", "SUCCESS", true)) {
						if (WSAssert.assertIfElementExists(res_availability, "RoomStay_RatePlans_RatePlan_ratePlanCode", true) && WSAssert.assertIfElementExists(res_availability, "RoomStay_RoomTypes_RoomType_roomTypeCode", true)) {
							availabilityFlag = true;
							String avRate = WSClient.getElementValue(res_availability, "RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
							String avRoomType = WSClient.getElementValue(res_availability, "RoomStay_RoomTypes_RoomType_roomTypeCode", XMLType.RESPONSE);
							avResvType = WSClient.getElementValue(res_availability, "RatePlan_GuaranteeDetails_Guarantee_guaranteeType", XMLType.RESPONSE);
							if(avResvType.contains("doesn't exist"))
								avResvType = null;
							WSClient.writeToReport(LogStatus.INFO, "<b>Availability exists for Rate Code -> " + avRate + " and Room Type -> " + avRoomType + "</b>");
						} 
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Availability doesn't");
						}
					} else {
						WSClient.writeToReport(LogStatus.WARNING, "Availability doesn't");
					}
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING, "No Rate Codes/Room Types are configured for the Channel "+sChannel);
				}
			}
			

			/***********************************************************************
			 * Prerequisite 3 : Create Booking
			 ***********************************************************************/
			
			String reservationID = "", confirmationNumber = "";
			// Proceed with Reservation Creation if Availability Exists
			if (availabilityFlag == true) {
				
				// Set data for the Message ID Parameter required on the OWS SOAP header for Availability Operation
				WSClient.setData("{var_msgID}", UUID.randomUUID().toString());
				
				// Set the data required for Create Booking Request
				if(avResvType != null)
					WSClient.setData("{var_conditional_param_resvType}", avResvType);

				// Create Reservation
				String req_createBooking = WSClient.createSOAPMessage("OWSCreateBooking", "DS_48");
				String res_createBooking = WSClient.processSOAPMessage(req_createBooking);

				// Validate if the response shows SUCCESS
				if (WSAssert.assertIfElementValueEquals(res_createBooking, "CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", true)) {
					if(WSAssert.assertIfElementExists(res_createBooking, "HotelReservation_UniqueIDList_UniqueID", true)) {
						createBookingFlag = true;
						reservationID = WSClient.getElementValueByAttribute(res_createBooking, "HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
						confirmationNumber = WSClient.getElementValue(res_createBooking, "HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationID + " Confirmation Number: " + confirmationNumber + "</b>");
						WSClient.setData("{var_resvId}", reservationID);
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "Booking Creation is failed, Cannot proceed further.");
					}
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING, "Booking Creation is failed, Cannot proceed further.");
				}
			}
			
			/***********************************************************************
			 * Verification Point : Fetch Booking
			 ***********************************************************************/
			
			if(createBookingFlag == true) {
				
				// Set data for the Message ID Parameter required on the OWS SOAP header for Availability Operation
				WSClient.setData("{var_msgID}", UUID.randomUUID().toString());
				
				//Retrieve reservation
				String req_fetchBooking = WSClient.createSOAPMessage("OWSFetchBooking", "DS_06");
				String res_fetchBooking = WSClient.processSOAPMessage(req_fetchBooking);
	
				if (WSAssert.assertIfElementExists(res_fetchBooking,"FetchBookingResponse_Result_resultStatusFlag", false)) {
					if (WSAssert.assertIfElementValueEquals(res_fetchBooking,"FetchBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
						LinkedHashMap<String, String> actuals = new LinkedHashMap<String, String>();
						actuals.put("RESORT", WSClient.getElementValue(res_fetchBooking,"RoomStays_RoomStay_HotelReference_hotelCode", XMLType.RESPONSE));
						actuals.put("NAME_ID", WSClient.getElementValue(res_fetchBooking,"Profile_ProfileIDs_UniqueID", XMLType.RESPONSE));
						actuals.put("RESV_NAME_ID", WSClient.getElementValueByAttribute(res_fetchBooking,"HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE));
						actuals.put("CONFIRMATION_NO", WSClient.getElementValueByAttribute(res_fetchBooking,"HotelReservation_UniqueIDList_UniqueID", "HotelReservation_UniqueIDList_UniqueID_source","", XMLType.RESPONSE));
						actuals.put("RESV_STATUS",WSClient.getElementValue(res_fetchBooking,"FetchBookingResponse_HotelReservation_reservationStatus",XMLType.RESPONSE));
						actuals.put("BEGIN_DATE",WSClient.getElementValue(res_fetchBooking,"RoomStay_TimeSpan_StartDate", XMLType.RESPONSE));
						actuals.put("END_DATE",WSClient.getElementValue(res_fetchBooking,"RoomStay_TimeSpan_EndDate", XMLType.RESPONSE));
						LinkedHashMap<String, String> db = WSClient.getDBRow(WSClient.getQuery("QS_55"));
						WSAssert.assertEquals(db, actuals, false);
					}
					else {
						WSClient.writeToReport(LogStatus.FAIL, "Booking retrieval is failed");
					}
				}
				else {
					WSClient.writeToReport(LogStatus.FAIL, "Booking retrieval is failed");
				}
			}
			
			/***********************************************************************
			 *  Cleanup
			 ***********************************************************************/
		
			if(createBookingFlag == true) {
				
				// Set data for the Message ID Parameter required on the OWS SOAP header for Availability Operation
				WSClient.setData("{var_msgID}", UUID.randomUUID().toString());
				
				//Cancel booking
				String req_cancelBooking = WSClient.createSOAPMessage("OWSCancelBooking", "DS_01");
				String res_cancelBooking = WSClient.processSOAPMessage(req_cancelBooking);
				
				if (WSAssert.assertIfElementValueEquals(res_cancelBooking,"CancelBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					if(WSAssert.assertIfElementExists(res_cancelBooking, "Result_IDs_IDPair_operaId", true)) {
						String cancellationId = WSClient.getElementValue(res_cancelBooking,"Result_IDs_IDPair_operaId", XMLType.RESPONSE);
							WSClient.writeToReport(LogStatus.INFO, "<b> Booking is cancelled as part of clean-up activity. Cancellation ID -> "+cancellationId+ "</b>");
					}
					else {
						WSClient.writeToReport(LogStatus.INFO, "Booking not cancelled");
					}
				}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Booking not cancelled");
				}
			}

		} catch (Exception e) {
			System.out.println("Error Occurred in the Fetch Booking flow "+e.getMessage());
			e.printStackTrace();
			WSClient.writeToReport(LogStatus.ERROR, "Error Occurred in the Fetch Booking flow. "+e.getMessage());
		}
	}
	

}