package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateHouseKeepingAttendants extends WSSetUp{
	public boolean createHouseKeepingAttendant(String dataset){
		try{
//			
//			
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Housekeeping Attendant</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateHousekeepingAttendants", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Attendant ID " + WSClient.getData("{var_AttendantID}") +" already exists");
				return true;
			}
			else {
				WSClient.writeToReport(LogStatus.INFO, "Housekeeping Attendant doesnot exist!!");
				String createHousekeepingAttendantsReq=WSClient.createSOAPMessage("CreateHousekeepingAttendants", dataset);
				String createHousekeepingAttendantsRes=WSClient.processSOAPMessage(createHousekeepingAttendantsReq);
				if(WSAssert.assertIfElementExists(createHousekeepingAttendantsRes, "CreateHousekeepingAttendantsRS_Success", true)){
					WSClient.writeToReport(LogStatus.PASS, "//CreateHousekeepingAttendantsRS/Success exists on the response message");
						
						//DB Validation
						dbResult = new LinkedHashMap<String, String>();
						query = WSClient.getQuery("QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully Created HouseKeeping Attendant");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "HouseKeeping Attendant not created");
							return false;
						}
					}
					else{
						if(WSAssert.assertIfElementExists(createHousekeepingAttendantsRes, "CreateHousekeepingAttendantsRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createHousekeepingAttendantsRes, "CreateHousekeepingAttendantsRS_Errors_Error_ShortText", XMLType.RESPONSE));
						}
						return false;
					}
				}
			
			}
			
		catch (Exception e) {
			e.printStackTrace();
			WSClient.writeToReport(LogStatus.ERROR, "Exception ocuured due to :" +e);
			return false;
		} 
	}

	@Test(groups= {"OperaConfig"})
	public void createMultiple_HouseKeepingAttendants() {
		int i;
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String testName="HouseKeepingAttendants";
		String dataset = "";
		WSClient.startTest(testName, "Create HouseKeeping Attendants", "OperaConfig");
		WSClient.setData("{var_Chain}", OPERALib.getChain());
		int length = OperaPropConfig.getLengthForCode("HouseKeepingAttendants") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;
			String value = OperaPropConfig.getDataSetForCode("HouseKeepingAttendants" , dataset);
			WSClient.setData("{var_AttendantID}", value);
			flag = flag && createHouseKeepingAttendant(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("HouseKeepingAttendants", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("HouseKeepingAttendants", "N");
}

}
