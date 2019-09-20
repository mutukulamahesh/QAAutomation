package com.oracle.hgbu.opera.qaauto.ws.components.transactions;

import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.relevantcodes.customextentreports.LogStatus;

public class ChangeApplicationParameters {
	
	private static String appParameterEnabled;

	/**
	 * To change an application parameter
	 * @throws Exception ***********************/
	public static String changeApplicationParameter(String dataset,String getParamDataset) throws Exception
	{
		String changeAppParamReq = WSClient.createSOAPMessage("ChangeApplicationSettings", dataset);
		String changeAppParamRes = WSClient.processSOAPMessage(changeAppParamReq);

		if (WSAssert.assertIfElementExists(changeAppParamRes, "ChangeApplicationSettingsRS_Success", true)) {
			String query = WSClient.getQuery("QS_01");
			appParameterEnabled = WSClient.getDBRow(query).get("PARAMETER_VALUE");
		}
		else{
			appParameterEnabled="error";
			WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Changing the Application parameter failed");
		}
		return appParameterEnabled;
	}
	
}
