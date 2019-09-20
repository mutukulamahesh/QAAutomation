package com.oracle.hgbu.opera.qaauto.ws.components.transactions;

import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;

public class ReleaseItemInventoryHold {

	/*******  To release item hold***********/
	public static boolean releaseItemInventoryHold(String dataset) throws Exception
	{
		
            
            String releaseHoldReq = WSClient.createSOAPMessage("ReleaseItemInventoryHold", dataset);
            String releaseHoldRes = WSClient.processSOAPMessage(releaseHoldReq);


			if (WSAssert.assertIfElementExists(releaseHoldRes, "ReleaseItemInventoryHoldRS_Success", true)) 
			{
				return true;	
			}
		
		return false;
	}
}
