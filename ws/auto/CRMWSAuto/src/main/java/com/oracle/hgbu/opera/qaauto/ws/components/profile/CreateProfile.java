package com.oracle.hgbu.opera.qaauto.ws.components.profile;

import java.util.HashMap;

import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSConfig;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateProfile {

	public static HashMap<XMLType,String> createProfilePayloads(String dataSetID, String runOnEntry) {
		String operationKey = "CRM_Profile_CreateProfile";
		HashMap<XMLType,String> resultMap = new HashMap<XMLType,String>();
		String resort ="", uname="";
		try {
			resort = WSConfig.getResort(runOnEntry);
			uname= WSConfig.getUser(runOnEntry);

			WSClient.setGlobalData("{var_resort}", resort);
			WSClient.setHeaderParameters(uname);

			String createProfileRequest = WSClient.createSOAPMessage(operationKey, dataSetID);
			String createProfileResponse = WSClient.processSOAPMessage(createProfileRequest);

			resultMap.put(XMLType.REQUEST, createProfileRequest);
			resultMap.put(XMLType.RESPONSE, createProfileResponse);
			resultMap.put(XMLType.OPERATION_KEY, operationKey);

			//Verify if the Response has a SUCCESS flag indicating that the action is successful
			if (WSAssert.assertIfElementExists(createProfileResponse, "CreateProfileRS_Success", true, operationKey)){
				if (WSAssert.assertIfElementExists(createProfileResponse,"CreateProfileRS_ProfileIDList_UniqueID_ID", true,operationKey)) {
					WSClient.writeToReport(LogStatus.PASS, "Guest Profile creation is successful.");
				} else {
					WSClient.writeToReport(LogStatus.FAIL, "Guest Profile creation is failed");
				}
			}

		}
		catch(Exception e) {
			System.out.println("Error occurred while createating a profile. ");
			e.printStackTrace();
		}
		return resultMap;
	}

	public static String createOperaProfile(String dataSet,String runOnEntry)  {
		String operationKey = "CRM_Profile_CreateProfile";
		String profileID="";
		HashMap<XMLType,String> resultMap = new HashMap<XMLType,String>();
		try {
			resultMap = createProfilePayloads(dataSet,runOnEntry);
			String response = resultMap.get(XMLType.RESPONSE);
			//Verify if the Response has a SUCCESS flag indicating that the action is successful
			if (WSAssert.assertIfElementExists(response, "CreateProfileRS_Success", true, operationKey)){
				if (WSAssert.assertIfElementExists(response,"CreateProfileRS_ProfileIDList_UniqueID_ID", true,operationKey)) {
					profileID = WSClient.getElementValue(response,"CreateProfileRS_ProfileIDList_UniqueID_ID", XMLType.RESPONSE,operationKey);
					WSClient.writeToReport(LogStatus.INFO, "Profile ID: "+profileID);
				}
			}
		}
		catch (Exception e) {
			System.out.println("Error occurred while creating profile");
		}

		return profileID;
	}

}
