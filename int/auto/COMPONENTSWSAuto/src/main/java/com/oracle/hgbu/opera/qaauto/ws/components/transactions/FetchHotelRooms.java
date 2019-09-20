package com.oracle.hgbu.opera.qaauto.ws.components.transactions;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchHotelRooms {

	public static String fetchHotelRooms(String dataset) throws Exception{
String roomNumber = "";
		
		try {
			String resortOperaValue = OPERALib.getResort();
			String chain = OPERALib.getChain();

			WSClient.setData("{var_resort}", resortOperaValue);
			WSClient.setData("{var_chain}", chain);

			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);

			String fetchHotelRoomsReq = WSClient.createSOAPMessage("FetchHotelRooms", dataset);
			String fetchHotelRoomsRes = WSClient.processSOAPMessage(fetchHotelRoomsReq);
			if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_Success", true)) {
				if (WSAssert.assertIfElementExists(fetchHotelRoomsRes, "FetchHotelRoomsRS_HotelRooms_Room", true)) {
					roomNumber = WSClient.getElementValue(fetchHotelRoomsRes,
							"FetchHotelRoomsRS_HotelRooms_Room_RoomNumber", XMLType.RESPONSE);
					WSClient.writeToReport(LogStatus.INFO,
							"<b>" + "Successfully Fetched Room, room Number is: " + roomNumber + "</b>");

				} else {
					roomNumber = "error";
					WSClient.writeToReport(LogStatus.INFO, "<b>No Room Available</b>");
				}
			} else {
				roomNumber = "error";
				WSClient.writeToReport(LogStatus.INFO, "<b>FetchHotelRooms is Blocked</b>");
			}
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
		}
		return roomNumber;
	}
}
