package com.oracle.hgbu.opera.qaauto.ws.components.availability;

import java.util.HashMap;

import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSConfig;
import com.relevantcodes.customextentreports.LogStatus;

import bsh.This;

public class FetchHotelAvailability {
	public enum hotelAvailabilityElements {ROOMTYPE, RATECODE}
	/**
	 * @author Santhoshi Basana
	 * @see This method retrieves the hotel availability
	 */
	public static HashMap<XMLType,String> fetchHotelAvailabilityPayloads(String dataSet, String runOnEntry) {
		String operationKey = "PAR_Availability_FetchHotelAvailability";
		HashMap<XMLType,String> resultMap = new HashMap<XMLType,String>();
		String resort ="", uname="";
		try {
			resort = WSConfig.getResort(runOnEntry);
			uname= WSConfig.getUser(runOnEntry);
			WSClient.setGlobalData("{var_resort}", resort);
			WSClient.setHeaderParameters(uname);

			String hotelAvailabilityRequest = WSClient.createSOAPMessage(operationKey, "DS_01");
			String hotelAvailabilityResponse = WSClient.processSOAPMessage(hotelAvailabilityRequest);

			resultMap.put(XMLType.REQUEST, hotelAvailabilityRequest);
			resultMap.put(XMLType.RESPONSE, hotelAvailabilityResponse);
			resultMap.put(XMLType.OPERATION_KEY, operationKey);

		}
		catch(Exception e) {
			System.out.println("Error Occurred while retrieving the hotel availablity");
		}

		return resultMap;
	}

	public static HashMap<hotelAvailabilityElements,String> fetchHotelAvailability(String dataSet, String runOnEntry) {
		String operationKey = "PAR_Availability_FetchHotelAvailability";
		HashMap<hotelAvailabilityElements,String> availabilityMap = new HashMap<hotelAvailabilityElements,String>();
		HashMap<XMLType,String> resultMap = new HashMap<XMLType,String>();
		try {
			resultMap = fetchHotelAvailabilityPayloads(dataSet,runOnEntry);
			String response = resultMap.get(XMLType.RESPONSE);
			//Verify if the Response has a SUCCESS flag indicating that the action is successful
			if (WSAssert.assertIfElementExists(response, "FetchHotelAvailabilityRS_Success", false,operationKey)) {
				//Verify if the Response has Room Type and Rate Code
				if(WSAssert.assertIfElementExists(response, "RoomStay_RoomRates_RoomRate_RatePlanCode", true,operationKey) &&
						(WSAssert.assertIfElementExists(response, "RoomStay_RoomRates_RoomRate_RoomType", true,operationKey))) {

					//Read Room Type and Rate Plan from Fetch Hotel Availability Response Payload
					String rateCode = WSClient.getElementValue(response, "RoomStay_RoomRates_RoomRate_RatePlanCode", XMLType.RESPONSE,operationKey);
					String roomType = WSClient.getElementValue(response, "RoomStay_RoomRates_RoomRate_RoomType", XMLType.RESPONSE,operationKey);

					availabilityMap.put(hotelAvailabilityElements.ROOMTYPE, roomType);
					availabilityMap.put(hotelAvailabilityElements.RATECODE, rateCode);
					WSClient.writeToReport(LogStatus.INFO, "Room Type: "+roomType +" "+"Rate Code: "+rateCode);
				}
			}
		}
		catch (Exception e) {
			System.out.println("Error occurred while retrieving the hotel availability");
		}
		return availabilityMap;
	}
}


