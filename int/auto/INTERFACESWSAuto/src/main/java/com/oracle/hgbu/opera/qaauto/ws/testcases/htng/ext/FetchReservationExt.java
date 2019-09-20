package com.oracle.hgbu.opera.qaauto.ws.testcases.htng.ext;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.HTNGLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class FetchReservationExt extends WSSetUp {
	@Test(groups = {"BAT", "FetchReservationExt", "HTNG2008B", "HTNG" })
	@Parameters({"schema", "version"})
	public void fetchReservationExt(String schema, String version) {
		WSClient.startTest("fetchReservationExt","Verify Reservation information is retrieved","bat");
		String intParametersQuery = "", intParametersQuerySetID ="", fromAddress ="", interfaceSetupQuerySetID="", interfaceSetupQuery ="", extResort="", existingReservationQuery="";
		String sUsername="", sPassword="", sChain="", sResort="", sInterface="";
		LinkedHashMap<String,String> intParametersMap = new LinkedHashMap<String,String>();
		LinkedHashMap<String,String> interfaceSetupMap = new LinkedHashMap<String,String>();
		LinkedHashMap<String,String> existingReservationMap = new LinkedHashMap<String,String>();
		try {
			
			sUsername = OPERALib.getUserName();
			sPassword = OPERALib.getPassword();
			sChain = OPERALib.getChain();
			sResort = OPERALib.getResort();
			sInterface = HTNGLib.getHTNGInterface();
			
			//Parameters required for the query
			WSClient.setData("{var_interface}", sInterface);
			WSClient.setData("{var_chain}", sChain);
			WSClient.setData("{var_resort}", sResort);
			
			intParametersQuerySetID = schema.equalsIgnoreCase("ASP") && !version.equalsIgnoreCase("5.5") ? "QS_15": "QS_16";
			intParametersQuery = WSClient.getQuery("HTNGExtFetchReservationExt", intParametersQuerySetID, false);
			intParametersMap = WSClient.getDBRow(intParametersQuery);
			System.out.println(intParametersMap);
			interfaceSetupQuerySetID = schema.equalsIgnoreCase("ASP")  && !version.equalsIgnoreCase("5.5") ? "QS_17": "QS_18";
			interfaceSetupQuery = WSClient.getQuery("HTNGExtFetchReservationExt", interfaceSetupQuerySetID, false);
			interfaceSetupMap = WSClient.getDBRow(interfaceSetupQuery);
			System.out.println(interfaceSetupMap);
			existingReservationQuery = WSClient.getQuery("HTNGExtFetchReservationExt", "QS_14", false);
			existingReservationMap = WSClient.getDBRow(existingReservationQuery);
			System.out.println(existingReservationMap);
			
			if(existingReservationMap.size() > 0) {
					if(intParametersMap.size() > 0 && intParametersMap.get("PARAMETER_VALUE") != null)  {
							fromAddress = intParametersMap.get("PARAMETER_VALUE");
							extResort = interfaceSetupMap.get("EXTERNAL_RESORT");
							//Parameters required for the HTNG Header
							WSClient.setData("{var_userName}", sUsername);
							WSClient.setData("{var_password}", sPassword);
							WSClient.setData("{var_fromAddress}", fromAddress);
							WSClient.setData("{var_msgID}",UUID.randomUUID().toString());
							//Parameters required for Fetch Reservation Request
							WSClient.setData("{var_resvId}", existingReservationMap.get("RESV_NAME_ID"));
							WSClient.setData("{var_extResort}", extResort);
							//Construct HTNG Fetch Reservation Request and Post it
							String fetchReservationReq = WSClient.createSOAPMessage("HTNGExtFetchReservationExt", "DS_01");
							String fetchReservationRes = WSClient.processSOAPMessage(fetchReservationReq);
							
							if (WSAssert.assertIfElementContains(fetchReservationRes, "FetchReservationExtResponse_Result_resultStatusFlag","SUCCESS", false)) {
								WSAssert.assertIfElementValueEquals(fetchReservationRes, "FetchReservationExtResponse_ReservationData_ProfileID", existingReservationMap.get("NAME_ID"), false);
								WSAssert.assertIfElementValueEquals(fetchReservationRes, "FetchReservationExtResponse_ReservationData_ResortId", extResort, false);
								WSAssert.assertIfElementValueEquals(fetchReservationRes, "FetchReservationExtResponse_ReservationData_ReservationID", existingReservationMap.get("RESV_NAME_ID"), false);
								WSAssert.assertIfElementValueEquals(fetchReservationRes, "FetchReservationExtResponse_ReservationData_ConfirmationNO", existingReservationMap.get("CONFIRMATION_NO"), false);
								WSAssert.assertIfElementValueEquals(fetchReservationRes, "FetchReservationExtResponse_ReservationData_reservationStatus", existingReservationMap.get("RESV_STATUS"), false);
								WSAssert.assertIfElementValueEquals(fetchReservationRes, "FetchReservationExtResponse_ReservationData_ReservationID_source", "OPERA", false);		
								WSAssert.assertIfElementValueEquals(fetchReservationRes, "FetchReservationExtResponse_ReservationData_ArrivalDate", existingReservationMap.get("BEGIN_DATE"), false);
								WSAssert.assertIfElementValueEquals(fetchReservationRes, "FetchReservationExtResponse_ReservationData_DepartureDate", existingReservationMap.get("END_DATE"), false);
								
							}
							else {
								WSClient.writeToReport(LogStatus.FAIL, "Reservation Retrieval is failed");
						   }
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "fromAddress attribute is not configured for "+sInterface);
					}
			}
			else {
				WSClient.writeToReport(LogStatus.WARNING, "No reservations found in Opera DB");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL Error! "+e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
