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


public class RoomClass extends WSSetUp {

	public boolean create_RoomClass(String dataset) {
		try {
			
//			String fetchRoomTypeReq = WSClient.createSOAPMessage("FetchRoomClasses", dataset);
//			String fetchRoomTypeResponseXML = WSClient.processSOAPMessage(fetchRoomTypeReq);
//			if(WSAssert.assertIfElementExists(fetchRoomTypeResponseXML, "FetchRoomClassesRS_Success", false)){
//			if (WSAssert.assertIfElementExists(fetchRoomTypeResponseXML,
//					"FetchRoomClassesRS_RoomClasses_RoomClass", true)) {
//				WSClient.writeToReport(LogStatus.INFO, "Room Class already exists");
//				return true;
//			} 
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Room Class</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateRoomClass", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Room Class " + WSClient.getData("{var_RoomClass}") +" already exists");
				return true;
			}
			else { 
				WSClient.writeToReport(LogStatus.INFO, "Room Class doesnot exist!!");
				String CreateRoomClassReq = WSClient.createSOAPMessage("CreateRoomClass", dataset);
				String CreateRoomClassRes = WSClient.processSOAPMessage(CreateRoomClassReq);
				if (WSAssert.assertIfElementExists(CreateRoomClassRes,
						"CreateRoomClassRS_Success", true)) {
					WSClient.writeToReport(LogStatus.PASS, "//CreateRoomClassRS/Success exists on the response message");
					dbResult = WSClient.getDBRow(query);
					val = dbResult.get("COUNT");
					if(WSAssert.assertEquals("1", val, true)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Room Class");
						return true;
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "Room Class not created");
						return false;
					}				
				}
				else {
					if(WSAssert.assertIfElementExists(CreateRoomClassRes, "CreateRoomClassRS_Errors_Error_ShortText", false)) {
						WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(CreateRoomClassRes, "CreateRoomClassRS_Errors_Error_ShortText", XMLType.RESPONSE));
					}
					return false;
				}
		} 
	}
		catch(Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	@Test(groups= {"createRoomClass", "OperaConfig"})
	public void createMultiple_RoomClass() {
		int i;
		String testName = "CreateRoomClass";
		boolean flag=true;
		WSClient.startTest(testName, "Create Room Class", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		int length = OperaPropConfig.getLengthForCode("RoomClass") - 1;
		String dataset = "";
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;
			String value = OperaPropConfig.getDataSetForCode("RoomClass" , dataset);
			
				
			WSClient.setData("{var_RoomClass}", value);
			flag = flag && create_RoomClass(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("RoomClass", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("RoomClass", "N");;
		}
}


