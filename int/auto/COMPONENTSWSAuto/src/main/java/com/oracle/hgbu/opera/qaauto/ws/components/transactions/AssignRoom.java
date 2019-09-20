package com.oracle.hgbu.opera.qaauto.ws.components.transactions;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.relevantcodes.customextentreports.LogStatus;

public class AssignRoom {

	public static boolean assignRoom(String dataset) throws Exception{
		try {

			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			String assignRoomReq = WSClient.createSOAPMessage("AssignRoom", dataset);
			String assignRoomRes = WSClient.processSOAPMessage(assignRoomReq);
			if (WSAssert.assertIfElementExists(assignRoomRes, "AssignRoomRS_Success", true)) {
				WSClient.writeToReport(LogStatus.INFO, "<b>" + "Successfully Assigned Room" + "</b>");
				return true;
			} else {
				WSClient.writeToReport(LogStatus.WARNING, "<b>" + "Prerequisite Blocked--->Assign Room is unsuccessful" + "</b>");
				return false;
			}

		} catch (Exception ex) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + ex);
		}
		return false;
	}
}
