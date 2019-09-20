package com.oracle.hgbu.opera.qaauto.ws.testcases.reservation;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.components.availability.FetchHotelAvailability;
import com.oracle.hgbu.opera.qaauto.ws.components.availability.FetchHotelAvailability.hotelAvailabilityElements;
import com.oracle.hgbu.opera.qaauto.ws.components.hotelConfig.FetchMarketCodes;
import com.oracle.hgbu.opera.qaauto.ws.components.hotelConfig.FetchSourceCodes;
import com.oracle.hgbu.opera.qaauto.ws.components.profile.CreateProfile;
import com.oracle.hgbu.opera.qaauto.ws.components.profile.FetchProfiles;
import com.oracle.hgbu.opera.qaauto.ws.components.reservation.CreateReservation;
import com.oracle.hgbu.opera.qaauto.ws.components.reservation.FetchReservation;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSConfig;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateReservationTest extends WSSetUp {

	/*
	 * Santhoshi Basana
	 * This method is implemented to create a standard reservation with no specific criteria; It returns the reservation payloads
	 */
	public static HashMap<XMLType,String> createBasicReservationAndReturnPayloads(String runOnEntry) {
		String roomType ="", rateCode="",sourceCode="", marketCode="", profileID="", resort="";
		HashMap<FetchHotelAvailability.hotelAvailabilityElements,String> availabilityMap = new HashMap<FetchHotelAvailability.hotelAvailabilityElements,String>();
		HashMap<XMLType,String> createResvMap = new HashMap<XMLType,String>();
		try {
			resort = WSConfig.getResort(runOnEntry);
			System.out.println(resort);
			availabilityMap = FetchHotelAvailability.fetchHotelAvailability("DS_01", runOnEntry);
			if(availabilityMap.size() > 0) {
				sourceCode = FetchSourceCodes.fetchSourceCode("DS_01",runOnEntry);
				sourceCode = (sourceCode== null)?"":sourceCode;
				marketCode = FetchMarketCodes.fetchMarketCode("DS_01",runOnEntry);
				marketCode = (marketCode == null)?"":marketCode;

				roomType = (availabilityMap.get(hotelAvailabilityElements.ROOMTYPE) == null)?"":availabilityMap.get(hotelAvailabilityElements.ROOMTYPE).trim();
				rateCode = (availabilityMap.get(hotelAvailabilityElements.RATECODE) == null)?"":availabilityMap.get(hotelAvailabilityElements.ROOMTYPE).trim();

				profileID = FetchProfiles.fetchProfiles("DS_01", runOnEntry);
				if(profileID.trim() == "") {
					profileID = CreateProfile.createOperaProfile("DS_01",runOnEntry);
				}

				if(sourceCode !="" && marketCode!= "" && roomType!="" && rateCode!= "" && profileID!="") {
					WSClient.setGlobalData("{var_profileId}", profileID);
					WSClient.setGlobalData("{var_marketCode}", marketCode);
					WSClient.setGlobalData("{var_sourceCode}", sourceCode);
					WSClient.setGlobalData("{var_ratePlanCode}", rateCode);
					WSClient.setGlobalData("{var_roomType}", roomType);
					System.out.println(roomType);
					createResvMap = CreateReservation.createReservationPayloads("DS_01", runOnEntry);
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING, "Reservation Creation cannot be initiated due to the pre-requisite failures (One/more of the following are not retrieved .. Source Code, Market Code, Availability, Profile");
				}
			}
			else {
				WSClient.writeToReport(LogStatus.WARNING, "Reservation Creation cannot be initiated due to the pre-requisite failures (No availability)");
			}
		}
		catch(Exception e) {
			System.out.println("Error in creating reservation "+e.getMessage());
			e.printStackTrace();
		}
		return createResvMap;
	}

	/*
	 * Santhoshi Basana
	 * This method is implemented to create a standard reservation with no specific criteria; It returns the reservation ID
	 */
	public static String createBasicReservation(String runOnEntry) {
		HashMap<XMLType,String> createResvMap = new HashMap<XMLType,String>();
		String reservationID ="";
		try {
			createResvMap = createBasicReservationAndReturnPayloads(runOnEntry);
			String createResvResponse = createResvMap.get(XMLType.RESPONSE);
			String createResvOperationKey = createResvMap.get(XMLType.OPERATION_KEY);
			if(WSAssert.assertIfElementExists(createResvResponse, "CreateReservationRS_Success",false,createResvOperationKey)) 	{
				if(WSAssert.assertIfElementExists(createResvResponse, "Reservation_ReservationIDList_UniqueID_ID", true, createResvOperationKey))
					reservationID = WSClient.getElementValue(createResvResponse, "Reservation_ReservationIDList_UniqueID_ID", XMLType.RESPONSE,createResvOperationKey);
				WSClient.writeToReport(LogStatus.INFO, "Reservation ID: "+reservationID);
			}
		}
		catch(Exception e) {
			System.out.println("Error in creating reservation "+e.getMessage());
			e.printStackTrace();
		}
		return reservationID;
	}

	@Test(groups = { "sanity", "CreateReservation", "CRM"})
	@Parameters({"runOnEntry"})
	public void verifyBasicReservationCreation(String runOnEntry) {
		WSClient.startTest("verifyBasicReservationCreation","Verify that a reservation is successfully created for a guest profile","sanity");
		HashMap<XMLType,String> createResvMap = new HashMap<XMLType,String>();
		HashMap<XMLType,String> fetchResvMap = new HashMap<XMLType,String>();
		String reservationID ="", createResvOperationKey ="", fetchResvOperationKey ="";

		WSClient.setResortEntry(runOnEntry);

		createResvMap = createBasicReservationAndReturnPayloads(runOnEntry);
		if(createResvMap.size() >0) {
			String createResvRequest = createResvMap.get(XMLType.REQUEST);
			String createResvResponse = createResvMap.get(XMLType.RESPONSE);
			createResvOperationKey = createResvMap.get(XMLType.OPERATION_KEY);
			try {
				if(WSAssert.assertIfElementExists(createResvResponse, "CreateReservationRS_Success",false,createResvOperationKey)) {
					if(WSAssert.assertIfElementExists(createResvResponse, "Reservation_ReservationIDList_UniqueID_ID", true, createResvOperationKey))
						reservationID = WSClient.getElementValue(createResvResponse, "Reservation_ReservationIDList_UniqueID_ID", XMLType.RESPONSE,createResvOperationKey);
					WSClient.writeToReport(LogStatus.INFO, "Reservation ID: "+reservationID);

					WSClient.setGlobalData("{var_resvId}", reservationID);
					fetchResvMap = FetchReservation.fetchReservationPayloads("DS_01", runOnEntry);
					String fetchResvResponse = fetchResvMap.get(XMLType.RESPONSE);
					fetchResvOperationKey = fetchResvMap.get(XMLType.OPERATION_KEY);

					//Verify if the Response has a SUCCESS flag indicating that the action is successful
					if (WSAssert.assertIfElementExists(fetchResvResponse, "FetchReservationRS_Success", false, fetchResvOperationKey)) {
						//Verify if the Response has Reservation ID and Confirmation Number
						if(WSAssert.assertIfElementExists(fetchResvResponse, "Reservation_ReservationIDList_UniqueID_ID", false, fetchResvOperationKey)) {
							LinkedHashMap<String,String> expectedValuesMap = new LinkedHashMap<String,String>();
							LinkedHashMap<String,String> actualValuesMap = new LinkedHashMap<String,String>();

							expectedValuesMap.put("Reservation ID", reservationID);
							expectedValuesMap.put("Profile ID", WSClient.getElementValue(createResvRequest, "ProfileInfo_ProfileIDList_UniqueID_ID", XMLType.REQUEST,createResvOperationKey));
							expectedValuesMap.put("Room Type", WSClient.getElementValue(createResvRequest, "RoomStay_RoomRates_RoomRate_RoomType", XMLType.REQUEST, createResvOperationKey));
							expectedValuesMap.put("Rate Plan", WSClient.getElementValue(createResvRequest, "RoomStay_RoomRates_RoomRate_RatePlanCode", XMLType.REQUEST, createResvOperationKey));
							expectedValuesMap.put("Reservation Start Date", WSClient.getElementValue(createResvRequest, "RoomStay_TimeSpan_StartDate_3", XMLType.REQUEST, createResvOperationKey));
							expectedValuesMap.put("Reservation End Date", WSClient.getElementValue(createResvRequest, "RoomStay_TimeSpan_EndDate_3", XMLType.REQUEST, createResvOperationKey));

							System.out.println("expectedValuesMap "+ expectedValuesMap);
							actualValuesMap.put("Reservation ID", WSClient.getElementValue(fetchResvResponse, "Reservation_ReservationIDList_UniqueID_ID", XMLType.RESPONSE, fetchResvOperationKey));
							actualValuesMap.put("Profile ID", WSClient.getElementValue(fetchResvResponse, "ProfileInfo_ProfileIDList_UniqueID_ID", XMLType.RESPONSE, fetchResvOperationKey));
							actualValuesMap.put("Room Type", WSClient.getElementValue(fetchResvResponse, "RoomStay_CurrentRoomInfo_RoomType", XMLType.RESPONSE, fetchResvOperationKey));
							actualValuesMap.put("Rate Plan", WSClient.getElementValue(fetchResvResponse, "RoomStay_RoomRates_RoomRate_RatePlanCode", XMLType.RESPONSE, fetchResvOperationKey));
							actualValuesMap.put("Reservation Start Date", WSClient.getElementValue(fetchResvResponse, "RoomStay_TimeSpan_StartDate_3", XMLType.RESPONSE, fetchResvOperationKey));
							actualValuesMap.put("Reservation End Date", WSClient.getElementValue(fetchResvResponse, "RoomStay_TimeSpan_EndDate_3", XMLType.RESPONSE, fetchResvOperationKey));

							System.out.println("actualValuesMap "+ actualValuesMap);

							WSAssert.assertEquals(expectedValuesMap, actualValuesMap, false);
						}
					}
				}
				else {
					WSClient.writeToReport(LogStatus.FAIL, "Reservation Creation is failed");
				}
			}

			catch(Exception e) {
				System.out.println("Error in verifying the reservation creation "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
