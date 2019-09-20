package com.oracle.hgbu.opera.qaauto.ws.components.integrationprocessor;

import java.util.HashMap;

import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSConfig;

public class FetchBusinessEvents {
	/* Description -> This method retrieves the business events that are ready to be dequeued using FetchBusinessEvents operation and
	   returns the request/response payloads that are generated as part of the test run */
	public static HashMap<XMLType,String> fetchBusinessEventsPayloads(String dataSet, String runOnEntry) 	{
		HashMap<XMLType,String> resultMap = new HashMap<XMLType,String>();
		String resort ="", uname="", extSystem="";
		try {
			//This is the Operation Keyword from Service-Setup file
			String operationKey = "INT_IntegrationProcessor_FetchBusinessEvents";

			//WSConfig file can have multiple entries for the same environment where each entry is a different resort, chain, user and interface combination.
			//Entry key is a parameter to determine against which resort/chain/user/interface this test needs to be executed
			resort = WSConfig.getResort(runOnEntry);
			uname= WSConfig.getUser(runOnEntry);
			extSystem= WSConfig.getInterface(runOnEntry);

			if(resort != "" && uname != "" && extSystem !="") {
				//Assigning data read above to the variables configured in data bank
				WSClient.setGlobalData("{var_resort}", resort);
				WSClient.setGlobalData("{var_extSystem}", extSystem);

				//OCWS Cloud Services needs a user name on it's header and this method sets the User name to the SOAP header
				WSClient.setHeaderParameters(uname);

				//Construct the request message with the data configured at the given data set
				String fetchBusinessEventsRequest = WSClient.createSOAPMessage(operationKey, dataSet);
				//Post the above constructed request message to the corresponding end point
				String fetchBusinessEventsResponse = WSClient.processSOAPMessage(fetchBusinessEventsRequest);

				//Store the request and response messages
				resultMap.put(XMLType.REQUEST, fetchBusinessEventsRequest);
				resultMap.put(XMLType.RESPONSE, fetchBusinessEventsResponse);
				resultMap.put(XMLType.OPERATION_KEY, operationKey);
			}
			else {
				System.out.println("Resort or User or interface values are missing for the given resort entry "+runOnEntry);
			}
		}
		catch(Exception e) {
			System.out.println("Error occurred while fetching business events.");
			e.printStackTrace();
		}
		return resultMap;
	}
}
