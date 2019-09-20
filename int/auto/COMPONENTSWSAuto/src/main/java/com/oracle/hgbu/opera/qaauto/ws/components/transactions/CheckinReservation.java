package com.oracle.hgbu.opera.qaauto.ws.components.transactions;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.relevantcodes.customextentreports.LogStatus;

public class CheckinReservation {

	public static boolean checkinReservation(String dataset) throws Exception{
		String resortOperaValue = OPERALib.getResort();
		String chain = OPERALib.getChain();

		WSClient.setData("{var_resort}", resortOperaValue);
		WSClient.setData("{var_chain}", chain);

		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		String checkInReq = WSClient.createSOAPMessage("CheckinReservation",dataset);
		String checkInRes = WSClient.processSOAPMessage(checkInReq);
		if(WSAssert.assertIfElementExists(checkInRes,"CheckinReservationRS_Success",true)){
			WSClient.writeToReport(LogStatus.INFO, "<b>Reservation is checked in</b>");	
			return true;
		}else{
			WSClient.writeToReport(LogStatus.WARNING, "<b>Prerequisite Blocked--->Reservation is not checked in</b>");	
		}
		return false;
	}
}
