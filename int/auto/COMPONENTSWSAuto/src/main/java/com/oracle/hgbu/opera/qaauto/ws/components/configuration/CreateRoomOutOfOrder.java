package com.oracle.hgbu.opera.qaauto.ws.components.configuration;


import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateRoomOutOfOrder extends WSSetUp {
	public boolean createOOORoom(String dataset) {
		try {
					
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Out of Order Room</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("SetRoomOutOfOrder", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Room " + WSClient.getData("{var_OORoom}") +" already out of order");
				return true;
			}
			else {
			     	String req_createOORoom = WSClient.createSOAPMessage("SetRoomOutOfOrder", dataset);
					String res_createOORoom = WSClient.processSOAPMessage(req_createOORoom);

					if(WSAssert.assertIfElementExists(res_createOORoom, "SetRoomOutOfOrderRS_Warnings_Warning_ShortText", true)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createOORoom, "SetRoomOutOfOrderRS_Warnings_Warning_ShortText", XMLType.RESPONSE));
							return false;
					}
					else if(WSAssert.assertIfElementExists(res_createOORoom, "SetRoomOutOfOrderRS_Errors_Error_ShortText", true)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createOORoom, "SetRoomOutOfOrderRS_Errors_Error_ShortText", XMLType.RESPONSE));
							return false;
						}
					else 
					{
						if(WSAssert.assertIfElementExists(res_createOORoom, "SetRoomOutOfOrderRS_Success",false)) {
							dbResult = WSClient.getDBRow(query);
							val = dbResult.get("COUNT");
							if(WSAssert.assertEquals("1", val, true)) {
								WSClient.writeToReport(LogStatus.INFO, "Successfully created Out of Order Room");
								return true;
							}
							else {
								WSClient.writeToReport(LogStatus.WARNING, "Unable to set room to Out of Order");
								return false;
							}
						}	
					}
					return false;
			}
			
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
		
	}

	@Test(groups = {"OperaConfig"},dependsOnGroups={"createRoom", "OOOSReasons"})
	public void createMultipleOutOfOrderRooms() {
		int i;
		boolean flag=true;
		String testName = "createOutOfOrderRooms";
		WSClient.startTest(testName, "Create Out Of Order Rooms", "OperaConfig");
		String reasonCode, roomNo;
		
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("OOORoom") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","OOORoom");
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;  
			String comm = OperaPropConfig.getCellComment("OperaConfig",row, i);
			//System.out.println(comm);
			if(comm==null) {
				WSClient.writeToReport(LogStatus.WARNING, "Please provide dependencies");
			}
			dependencies = OperaPropConfig.getDependency(comm);
			reasonCode=OperaPropConfig.getDataSetForCode("OOOSReason", dependencies.get("OOOSReason"));
			WSClient.setData("{var_ReasonCode}",reasonCode);
			roomNo=OperaPropConfig.getDataSetForCode("OOORoom", dataset);
			WSClient.setData("{var_OORoom}",roomNo);
			flag=flag && createOOORoom(dataset);
			dependencies.clear();
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("OOORoom", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("OOORoom", "N");
	}

}
