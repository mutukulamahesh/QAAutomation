package com.oracle.hgbu.opera.qaauto.ws.components.transactions;

import java.util.HashMap;

import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateReservation {


	/*******  To create a reservation and return variable resvID   ***********/
	public static HashMap<String,String> createReservation(String dataset) throws Exception
	{
			String reservationId="";
			String confirmationId="";
			HashMap<String,String> id=new HashMap<String,String>();
			String createResvReq = WSClient.createSOAPMessage("CreateReservation", dataset);
			String createResvRes = WSClient.processSOAPMessage(createResvReq);

			if (WSAssert.assertIfElementExists(createResvRes, "CreateReservationRS_Success", true)) 
			{
				reservationId = WSClient.getElementValue(createResvRes,"Reservation_ReservationIDList_UniqueID_ID", XMLType.RESPONSE);
				confirmationId=WSClient.getElementValue(createResvRes, "Reservation_ReservationIDList_UniqueID_ID_3", XMLType.RESPONSE);
				System.out.println(reservationId);
				System.out.println(confirmationId);

				if (reservationId == null || reservationId.equals("*null*"))
				{
					reservationId="error";
					confirmationId="error";
					WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Reservation ID is not created");
				}
				
			}
			else
			{
				reservationId="error";
				confirmationId="error";
				WSClient.writeToReport(LogStatus.WARNING,"Prerequisite failed >> Reservation is not created");
			}
			id.put("reservationId", reservationId);
			id.put("confirmationId",confirmationId);
		return id;	
	}
}
