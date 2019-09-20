package com.oracle.hgbu.opera.qaauto.ws.components.hotelConfig;

import java.util.HashMap;

import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.wrapper.WSConfig;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchMarketCodes {
	public static HashMap<XMLType,String> fetchMarketCodePayloads(String dataSet,String runOnEntry)  {
		String operationKey = "MDM_HotelConfig_FetchMarketCodes";
		HashMap<XMLType,String> resultMap = new HashMap<XMLType,String>();
		String resort ="", uname="";
		try {
			resort = WSConfig.getResort(runOnEntry);
			uname= WSConfig.getUser(runOnEntry);

			WSClient.setGlobalData("{var_resort}", resort);
			WSClient.setHeaderParameters(uname);

			String fetchMarketCodesRequest = WSClient.createSOAPMessage(operationKey, dataSet);
			String fetchMarketCodesResponse = WSClient.processSOAPMessage(fetchMarketCodesRequest);

			resultMap.put(XMLType.REQUEST, fetchMarketCodesRequest);
			resultMap.put(XMLType.RESPONSE, fetchMarketCodesResponse);
			resultMap.put(XMLType.OPERATION_KEY, operationKey);

		}
		catch(Exception e) {
			System.out.println("Error occurred while retrieving Market Codes");
		}
		return resultMap;
	}

	public static String fetchMarketCode(String dataSet,String runOnEntry)  {
		String operationKey = "MDM_HotelConfig_FetchMarketCodes";
		String marketCode="";
		HashMap<XMLType,String> resultMap = new HashMap<XMLType,String>();
		try {
			resultMap = fetchMarketCodePayloads(dataSet,runOnEntry);
			String response = resultMap.get(XMLType.RESPONSE);
			//Verify if the Response has a SUCCESS flag indicating that the action is successful
			if (WSAssert.assertIfElementExists(response, "FetchMarketCodesRS_Success", false,operationKey)) {
				//Verify if the Response has Market Code(s) available on the response
				if (WSAssert.assertIfElementExists(response,"FetchMarketCodesRS_MarketCodes_MarketCode_Code", true,operationKey)) 	{
					marketCode = WSClient.getElementValue(response, "FetchMarketCodesRS_MarketCodes_MarketCode_Code", XMLType.RESPONSE,operationKey);
					WSClient.writeToReport(LogStatus.INFO, "Market Code: "+marketCode);
				}
			}
		}
		catch (Exception e) {
			System.out.println("Error occurred while retrieving the market code");
		}

		return marketCode;
	}
}
