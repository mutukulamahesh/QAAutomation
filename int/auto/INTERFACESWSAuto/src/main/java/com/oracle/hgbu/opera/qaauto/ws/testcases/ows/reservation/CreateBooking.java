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

public class CreateBooking extends WSSetUp {
	@Test(groups = { "BAT", "CreateBooking", "Reservation", "OWS"})
	@Parameters({"schema"})
	public void reservationCreation_OWS(String schema) {
		WSClient.startTest("reservationCreation_OWS", "Verify Reservation Creation via OWS Channel", "bat");
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
			 * Prerequisite 1: Create a Profile
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
						//String req_availability = WSClient.createSOAPMessage("OWSAvailability", "DS_21");
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
								WSClient.writeToReport(LogStatus.WARNING, "Availability doesn't exist");
							}
						} 
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Availability doesn't exist");
						}
					}
					else {
							WSClient.writeToReport(LogStatus.WARNING, "No Rate Codes/Room Types are configured for the Channel "+sChannel);
					}
			}

			/***********************************************************************
			 * Verification Point : Create Booking
			 ***********************************************************************/
			
			String reservationID = "", confirmationNumber = "";
			LinkedHashMap<String,String> reservationMap = new LinkedHashMap<String,String>();
			LinkedHashMap<String,String> gdsHeaderMap = new LinkedHashMap<String,String>();
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
				if (WSAssert.assertIfElementValueEquals(res_createBooking, "CreateBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					if(WSAssert.assertIfElementExists(res_createBooking, "HotelReservation_UniqueIDList_UniqueID", true)) {
						createBookingFlag = true;
						reservationID = WSClient.getElementValueByAttribute(res_createBooking, "HotelReservation_UniqueIDList_UniqueID", "RESVID", XMLType.RESPONSE);
						confirmationNumber = WSClient.getElementValue(res_createBooking, "HotelReservation_UniqueIDList_UniqueID", XMLType.RESPONSE);
						WSClient.writeToReport(LogStatus.INFO, "<b>Reservation Id: " + reservationID + " Confirmation Number: " + confirmationNumber + "</b>");
						WSClient.setData("{var_resvId}", reservationID);
						WSClient.setData("{var_globalReservationID}", reservationID);
						
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating Response against request payload</b>");
						String reqElement = WSClient.getElementValue(req_createBooking,"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.REQUEST);
						String resElement = WSClient.getElementValue(res_createBooking,"RoomStay_RatePlans_RatePlan_ratePlanCode", XMLType.RESPONSE);
	
						if (WSAssert.assertEquals(reqElement, resElement, true)) {
							WSClient.writeToReport(LogStatus.PASS, "Rate Plan Code -> Expected value:" + reqElement + " Actual value :" + resElement);
						} else
							WSClient.writeToReport(LogStatus.FAIL, "Rate Plan Code -> Expected value:" + reqElement + " Actual value :" + resElement);
						
						reqElement = WSClient.getElementValue(req_createBooking,"RoomStays_RoomStay_Guarantee_guaranteeType", XMLType.REQUEST);
						if(!reqElement.contains("doesn't exist")) {
							resElement = WSClient.getElementValue(res_createBooking, "RoomStays_RoomStay_Guarantee_guaranteeType",XMLType.RESPONSE);
							if (WSAssert.assertEquals(reqElement, resElement, true)) {
								WSClient.writeToReport(LogStatus.PASS, "Guarantee Type -> Expected value:" + reqElement + " Actual value :" + resElement);
							} else
								WSClient.writeToReport(LogStatus.FAIL, "Guarantee Type -> Expected value:" + reqElement + " Actual value :" + resElement);
						}
							
						reqElement =  operaNameId;
						resElement = WSClient.getElementValue(res_createBooking, "Profile_ProfileIDs_UniqueID",XMLType.RESPONSE);
						if (WSAssert.assertEquals(reqElement, resElement, true)) {
							WSClient.writeToReport(LogStatus.PASS, "Profile ID -> Expected value:" + reqElement + " Actual value :" + resElement);
						} else
							WSClient.writeToReport(LogStatus.FAIL, "Profile ID -> Expected value:" + reqElement + " Actual value :" + resElement);
						
						reqElement = WSClient.getElementValue(req_createBooking,"RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
						resElement = WSClient.getElementValue(res_createBooking, "RoomStay_TimeSpan_StartDate",XMLType.RESPONSE);
						
						
						if (WSAssert.assertEquals(reqElement, resElement, true)) {
							WSClient.writeToReport(LogStatus.PASS, "Reservation Start Date -> Expected value:" + reqElement + " Actual value :" + resElement);
						} else
							WSClient.writeToReport(LogStatus.FAIL, "Reservation Start Date -> Expected value:" + reqElement + " Actual value :" + resElement);
						
						reqElement = WSClient.getElementValue(req_createBooking,"RoomStay_TimeSpan_EndDate", XMLType.REQUEST);
						resElement = WSClient.getElementValue(res_createBooking, "RoomStay_TimeSpan_EndDate",XMLType.RESPONSE);
						if (WSAssert.assertEquals(reqElement, resElement, true)) {
							WSClient.writeToReport(LogStatus.PASS, "Reservation End Date -> Expected value:" + reqElement + " Actual value :" + resElement);
						} else
							WSClient.writeToReport(LogStatus.FAIL, "Reservation End Date -> Expected value:" + reqElement + " Actual value :" + resElement);
						
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating DB against request payload</b>");
						String resvQuery = WSClient.getQuery("QS_45");
						reservationMap =  WSClient.getDBRow(resvQuery);
						
						reqElement = WSClient.getElementValue(req_createBooking,"Profile_ProfileIDs_UniqueID", XMLType.REQUEST);
						String dbElement = reservationMap.get("NAME_ID");
				
						if (WSAssert.assertEquals(reqElement, dbElement, true)) {
							WSClient.writeToReport(LogStatus.PASS, "Profile ID -> Expected value:" + reqElement + " Actual value :" + dbElement);
						} else
							WSClient.writeToReport(LogStatus.FAIL, "Profile ID -> Expected value:" + reqElement + " Actual value :" + dbElement);
						
						reqElement = WSClient.getElementValue(req_createBooking,"RoomStay_TimeSpan_StartDate", XMLType.REQUEST);
						String tmpStrArrivalTime = WSClient.getElementValue(req_createBooking, "HotelReservation_ResGuests_ResGuest_arrivalTime", XMLType.REQUEST);
						reqElement = reqElement.replace("00:00:00", tmpStrArrivalTime);
						dbElement = reservationMap.get("BEGIN_DATE");			
						if (WSAssert.assertEquals(reqElement, dbElement, true)) {
							WSClient.writeToReport(LogStatus.PASS, "Begin Date -> Expected value:" + reqElement + " Actual value :" + dbElement);
						} else
							WSClient.writeToReport(LogStatus.FAIL, "Begin Date -> Expected value:" + reqElement + " Actual value :" + dbElement);
						
						reqElement = WSClient.getElementValue(req_createBooking,"RoomStay_TimeSpan_EndDate", XMLType.REQUEST);
						dbElement = reservationMap.get("END_DATE");				
						if (WSAssert.assertEquals(reqElement, dbElement, true)) {
							WSClient.writeToReport(LogStatus.PASS, "End Date -> Expected value:" + reqElement + " Actual value :" + dbElement);
						} else
							WSClient.writeToReport(LogStatus.FAIL, "End Date -> Expected value:" + reqElement + " Actual value :" + dbElement);
						
						
						WSClient.writeToReport(LogStatus.INFO, "<b>Validating GDS$_RESERVATION_HEADER Table</b>");
						WSClient.setData("{var_confirmationNo}", confirmationNumber);
						String gdsQuery = WSClient.getQuery("QS_46");
						gdsHeaderMap =  WSClient.getDBRow(gdsQuery);
						if(gdsHeaderMap.size() > 0) {
							if(Integer.parseInt(gdsHeaderMap.get("COUNT")) > 0) {
								WSClient.writeToReport(LogStatus.PASS, "Reservation Record found in GDS$_RESERVATION_HEADER table");
							}
							else {
								WSClient.writeToReport(LogStatus.FAIL, "Reservation Record is not found in GDS$_RESERVATION_HEADER table");
							}
						}
						else {
							WSClient.writeToReport(LogStatus.FAIL, "Reservation Record is not found in GDS$_RESERVATION_HEADER table");
						}
					}
					else {
						WSClient.writeToReport(LogStatus.FAIL, "Booking Creation is failed");
					}
				}
				else {
					WSClient.writeToReport(LogStatus.FAIL, "Booking Creation is failed");
				}
			}
			
			/***********************************************************************
			 * Cleanup
			 ***********************************************************************/
			
			if(createBookingFlag == true) {
				
				// Set data for the Message ID Parameter required on the OWS SOAP header for Availability Operation
				WSClient.setData("{var_msgID}", UUID.randomUUID().toString());
				
				//Cancel Booking
				String req_cancelBooking = WSClient.createSOAPMessage("OWSCancelBooking", "DS_01");
				String res_cancelBooking = WSClient.processSOAPMessage(req_cancelBooking);
				
				if (WSAssert.assertIfElementValueEquals(res_cancelBooking,"CancelBookingResponse_Result_resultStatusFlag", "SUCCESS", false)) {
					if(WSAssert.assertIfElementExists(res_cancelBooking, "Result_IDs_IDPair_operaId", true)) {
							WSClient.writeToReport(LogStatus.INFO, "Booking is cancelled as part of clean-up activity");
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
			System.out.println("Error Occurred in the Create Booking flow "+e.getMessage());
			e.printStackTrace();
			WSClient.writeToReport(LogStatus.ERROR, "Error Occurred in the Create Booking flow. "+e.getMessage());
		}
	}
}
