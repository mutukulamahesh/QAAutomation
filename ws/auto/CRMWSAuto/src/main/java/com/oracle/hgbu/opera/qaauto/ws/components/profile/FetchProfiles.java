package com.oracle.hgbu.opera.qaauto.ws.components.profile;

import java.util.HashMap;

import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSConfig;
import com.relevantcodes.customextentreports.LogStatus;

import bsh.This;

public class FetchProfiles {
	/**
	 * @author Santhoshi Basana
	 * @see This method retrieves multiple profiles profiles based on searchCriteria
	 */
	public static HashMap<XMLType,String> fetchProfilesPayloads(String dataSet,String runOnEntry) {
		String operationKey = "CRM_Profile_FetchProfiles";
		HashMap<XMLType,String> resultMap = new HashMap<XMLType,String>();
		String resort ="", uname="";
		try {
			resort = WSConfig.getResort(runOnEntry);
			uname= WSConfig.getUser(runOnEntry);

			WSClient.setGlobalData("{var_resort}", resort);
			WSClient.setHeaderParameters(uname);

			//Construct Request for Fetch Reservation Operation and Post the message
			String fetchProfilesRequest = WSClient.createSOAPMessage(operationKey, dataSet);
			String fetchProfilesResponse = WSClient.processSOAPMessage(fetchProfilesRequest);

			resultMap.put(XMLType.REQUEST, fetchProfilesRequest);
			resultMap.put(XMLType.RESPONSE, fetchProfilesResponse);
			resultMap.put(XMLType.OPERATION_KEY, operationKey);
		}
		catch(Exception e) {
			System.out.println("Error occurred while fetching profiles. ");
			e.printStackTrace();
		}
		return resultMap;
	}

	public static String fetchProfiles(String dataSet,String runOnEntry)  {
		String operationKey = "CRM_Profile_FetchProfiles";
		String profileID="";
		HashMap<XMLType,String> resultMap = new HashMap<XMLType,String>();
		try {
			resultMap = fetchProfilesPayloads(dataSet,runOnEntry);
			String response = resultMap.get(XMLType.RESPONSE);
			//Verify if the Response has a SUCCESS flag indicating that the action is successful
			if (WSAssert.assertIfElementExists(response, "FetchProfilesRS_Success", true, operationKey)){
				profileID = WSClient.getAttributeValueByAttribute(response, "ProfileInfo_ProfileIDList_UniqueID","ID","Profile", XMLType.RESPONSE, operationKey);
				WSClient.writeToReport(LogStatus.INFO, "Profile ID: "+profileID);
			}
		}
		catch (Exception e) {
			System.out.println("Error occurred while creating profile");
		}

		return profileID;
	}
}
