package com.oracle.hgbu.opera.qaauto.ws.components.transactions;

import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchApplicationParameters {

	
	private static String paramValue;

	/**
	 * To fetch an application parameter details
	 * @throws Exception ***********************/
	public static String getApplicationParameter(String dataset) throws Exception
	{
		String getAppParamReq = WSClient.createSOAPMessage("GetParameters", dataset);
		String getAppParamRes = WSClient.processSOAPMessage(getAppParamReq);

		if (WSAssert.assertIfElementExists(getAppParamRes, "GetParametersRS_Success", true)){
			if(WSAssert.assertIfElementExists(getAppParamRes, "GetParametersRS_Parameters_Parameter", true)){
				
				String query = WSClient.getQuery("QS_01");
				paramValue = WSClient.getDBRow(query).get("PARAMETER_VALUE");
			} else {
				paramValue="error";
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Checking the Application parameter failed");
			}
		} else {
			paramValue="error";
			WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Checking the Application parameter failed");
		}
		
		return paramValue;
	}
		

}
