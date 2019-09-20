package com.oracle.hgbu.opera.qaauto.ws.components.transactions;

import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.relevantcodes.customextentreports.LogStatus;

public class ChangeChannelParameters {

	public static String changeChannelParameters(String dataset,String queryid,String fetchValue) throws Exception
	{
		String paramValue;
		String changeChannelParametersReq = WSClient.createSOAPMessage("ChangeChannelParameters", dataset);
		String changeChannelParametersRes = WSClient.processSOAPMessage(changeChannelParametersReq);
		if(WSAssert.assertIfElementExists(changeChannelParametersRes,"ChangeChannelParametersRS_Success",true))
		{
			paramValue=FetchChannelParameters.fetchChannelParameters(queryid, fetchValue);
		}
		else
		{
			paramValue="error";
			WSClient.writeToReport(LogStatus.INFO, "<b>Pre-requisite failed >> Changing the channel parameter failed</b>");
		}
		return paramValue;
	}
	
}
