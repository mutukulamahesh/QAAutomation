package com.oracle.hgbu.opera.qaauto.ws.components.transactions;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;

public class CancelReservation {

	
	/*******  To cancel a reservation and return the status of cancellation  ***********/
	public static boolean cancelReservation(String dataset) throws Exception
	{
		if (OperaPropConfig.getPropertyConfigResults(new String[] { "ResvCancelReason" })) {
            WSClient.setData("{var_cancelReasonCode}", OperaPropConfig.getDataSetForCode("ResvCancelReason", "DS_01"));
            String cancelResvReq = WSClient.createSOAPMessage("CancelReservation", dataset);
            String cancelResvRes = WSClient.processSOAPMessage(cancelResvReq);


			if (WSAssert.assertIfElementExists(cancelResvRes, "CancelReservationRS_Success", true)) 
			{
				return true;	
			}
		}
		return false;
	}
	
}
