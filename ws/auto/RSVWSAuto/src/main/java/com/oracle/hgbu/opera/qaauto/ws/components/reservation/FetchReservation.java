package com.oracle.hgbu.opera.qaauto.ws.components.reservation;

import java.util.HashMap;

import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSConfig;

import bsh.This;

public class FetchReservation {
	/**
	 * @author Santhoshi Basana
	 * @see This method retrieves the reservation information
	 */
	public static HashMap<XMLType,String> fetchReservationPayloads(String dataSet,String runOnEntry) {
		String operationKey = "RSV_Reservation_FetchReservation";
		HashMap<XMLType,String> resultMap = new HashMap<XMLType,String>();
		String resort ="", uname="";
		try {
			resort = WSConfig.getResort(runOnEntry);
			uname= WSConfig.getUser(runOnEntry);

			WSClient.setGlobalData("{var_resort}", resort);
			WSClient.setHeaderParameters(uname);

			//Construct Request for Fetch Reservation Operation and Post the message
			String fetchReservationRequest = WSClient.createSOAPMessage(operationKey, dataSet);
			String fetchReservationResponse = WSClient.processSOAPMessage(fetchReservationRequest);

			resultMap.put(XMLType.REQUEST, fetchReservationRequest);
			resultMap.put(XMLType.RESPONSE, fetchReservationResponse);
			resultMap.put(XMLType.OPERATION_KEY, operationKey);

		}
		catch(Exception e) {
			System.out.println("Error occurred while createating a reservation. ");
			e.printStackTrace();
		}
		return resultMap;
	}


}
