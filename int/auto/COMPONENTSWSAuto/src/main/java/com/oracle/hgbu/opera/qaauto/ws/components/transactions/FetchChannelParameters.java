package com.oracle.hgbu.opera.qaauto.ws.components.transactions;

import java.util.LinkedHashMap;

import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;

public class FetchChannelParameters {

	public static String fetchChannelParameters(String queryid,String fetchValue) throws Exception
	{
		String query=WSClient.getQuery("ChangeChannelParameters",queryid);
		LinkedHashMap<String, String> results = WSClient.getDBRow(query);
		String paramvalue=results.get(fetchValue);
		return paramvalue;
	}
	
}
