package com.oracle.hgbu.opera.qaauto.ws.components.reservation;

import java.util.HashMap;

import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSConfig;

import bsh.This;

public class CreateReservation {
	/**
	 * @author Santhoshi Basana
	 * @see This method creates a reservation and returns the reservation data
	 */
	public static HashMap<XMLType,String> createReservationPayloads(String dataSet, String runOnEntry) 	{
		HashMap<XMLType,String> resultMap = new HashMap<XMLType,String>();
		String resort ="", uname="";
		try {
			//This is the Operation Keyword from Service-Setup file
			String operationKey = "RSV_Reservation_CreateReservation";

			//WSConfig file can have multiple entries for the same environment i.e, different resort, chain, user combination.
			//Entry key is a parameter to determine against which entry this test needs to be executed
			resort = WSConfig.getResort(runOnEntry);
			uname= WSConfig.getUser(runOnEntry);

			//Assigning actual data to the variables configured in data bank
			WSClient.setGlobalData("{var_resort}", resort);

			//OCWS Cloud Services needs a user name on it's header and this method sets the User name to the SOAP header
			WSClient.setHeaderParameters(uname);

			//Construct the request message with the data configured at the given data set
			String createReservationRequest = WSClient.createSOAPMessage(operationKey, dataSet);
			//Post the above constructed request message to the corresponding end point
			String createReservationResponse = WSClient.processSOAPMessage(createReservationRequest);

			//Store the request and response messages
			resultMap.put(XMLType.REQUEST, createReservationRequest);
			resultMap.put(XMLType.RESPONSE, createReservationResponse);
			resultMap.put(XMLType.OPERATION_KEY, operationKey);
		}
		catch(Exception e) {
			System.out.println("Error occurred while createating a reservation. ");
			e.printStackTrace();
		}
		return resultMap;
	}

}
