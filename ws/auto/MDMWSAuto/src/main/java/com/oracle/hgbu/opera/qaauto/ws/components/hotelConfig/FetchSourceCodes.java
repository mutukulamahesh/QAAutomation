package com.oracle.hgbu.opera.qaauto.ws.components.hotelConfig;

import java.util.HashMap;

import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSConfig;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchSourceCodes {
	public static HashMap<XMLType,String> fetchSourceCodePayloads(String dataSet,String runOnEntry) {
		String operationKey = "MDM_HotelConfig_FetchSourceCodes";
		HashMap<XMLType,String> resultMap = new HashMap<XMLType,String>();
		String resort ="", uname="";
		try {
			resort = WSConfig.getResort(runOnEntry);
			uname= WSConfig.getUser(runOnEntry);

			WSClient.setGlobalData("{var_resort}", resort);
			WSClient.setHeaderParameters(uname);

			String fetchSourceCodesRequest = WSClient.createSOAPMessage(operationKey, dataSet);
			String fetchSourceCodesResponse= WSClient.processSOAPMessage(fetchSourceCodesRequest);

			resultMap.put(XMLType.REQUEST, fetchSourceCodesRequest);
			resultMap.put(XMLType.RESPONSE, fetchSourceCodesResponse);
			resultMap.put(XMLType.OPERATION_KEY, operationKey);
		}
		catch (Exception e) {
			System.out.println("Error occurred while retrieving the Source Codes");
		}
		return resultMap;
	}

	public static String fetchSourceCode(String dataSet,String runOnEntry) {
		String operationKey = "MDM_HotelConfig_FetchSourceCodes";
		String sourceCode="";
		HashMap<XMLType,String> resultMap = new HashMap<XMLType,String>();
		try {
			resultMap = fetchSourceCodePayloads(dataSet,runOnEntry);
			String response = resultMap.get(XMLType.RESPONSE);
			//Verify if the Response has a SUCCESS flag indicating that the action is successful
			if (WSAssert.assertIfElementExists(response, "FetchSourceCodesRS_Success", false,operationKey)) {
				//Verify if the Response has Source Code(s) available on the response
				if (WSAssert.assertIfElementExists(response,"FetchSourceCodesRS_SourceCodes_SourceCode_Code", false,operationKey)) 	{
					sourceCode = WSClient.getElementValue(response, "FetchSourceCodesRS_SourceCodes_SourceCode_Code", XMLType.RESPONSE,operationKey);
					WSClient.writeToReport(LogStatus.INFO, "Source Code: "+sourceCode);
				}
			}
		}
		catch (Exception e) {
			System.out.println("Error occurred while retrieving the source codes");
		}
		return sourceCode;
	}
}
