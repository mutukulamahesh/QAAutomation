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


public class CreateRoom extends WSSetUp {

	public boolean createRoom(String dataSet) {
		try {
			
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Room</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateRoom", "QS_02");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Room " + WSClient.getData("{var_CreateRoom}") +" already exists");
				return true;
			}
			else {
				String createRoomReq = WSClient.createSOAPMessage("CreateRoom", dataSet);
				String createRoomResponseXML = WSClient.processSOAPMessage(createRoomReq);
				if (WSAssert.assertIfElementExists(createRoomResponseXML, "CreateRoomRS_Success",
						true) ) {
					WSClient.writeToReport(LogStatus.PASS, "//CreateRoomRS/Success exists on the response message");
					query = WSClient.getQuery("CreateRoom", "QS_02");
					dbResult = WSClient.getDBRow(query);
					val = dbResult.get("COUNT");
					if(WSAssert.assertEquals("1", val, true)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Room " + WSClient.getData("{var_CreateRoom}"));
						return true;
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "Room not created");
						return false;
					}
				} 
			else {
				if(WSAssert.assertIfElementExists(createRoomResponseXML, "CreateRoomRS_Errors_Error_ShortText",false)) {
					WSClient.writeToReport(LogStatus.WARNING,WSAssert.getElementValue(createRoomResponseXML, "CreateRoomRS_Errors_Error_ShortText", XMLType.RESPONSE)) ;
				}
					return false;
			}
			}
						
		}catch(Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	@Test(groups= {"OperaConfig","bat","createRoom"}, dependsOnGroups = {"createRoomType", "CreateFloors", "CreateRoomFeatures"})
	public void createMultiple_CreateRoom() {
		int i;
		String testName = "CreateRoom";
		boolean flag=true;
		WSClient.startTest(testName, "Create Room", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_resort}",OPERALib.getResort());
		String dataset, val_RoomType="";
		int length = OperaPropConfig.getLengthForCode("Rooms") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","Rooms");
		for(i=1;i<=length;i++) {
			if(i<10)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;
			String comm = OperaPropConfig.getCellComment("OperaConfig",row, i);
			//System.out.println(comm);
			if(comm==null) {
				WSClient.writeToReport(LogStatus.WARNING, "Please provide dependencies");
			}
			dependencies = OperaPropConfig.getDependency(comm);
			
			val_RoomType = OperaPropConfig.getDataSetForCode("RoomType" , dependencies.get("RoomType"));
			WSClient.setData("{var_Floor}", OperaPropConfig.getDataSetForCode("RoomFloor" , dependencies.get("RoomFloor")));
			WSClient.setData("{var_RoomFeature}", OperaPropConfig.getDataSetForCode("RoomFeature" , dependencies.get("RoomFeature")));
			WSClient.setData("{var_MRoom}", OperaPropConfig.getDataSetForCode("Rooms",dependencies.get("MasterRoom")));
			WSClient.setData("{var_MRoomType}", OperaPropConfig.getDataSetForCode("RoomType", dependencies.get("MRoomType")));
			String ds = dependencies.get("Dataset");
			String value = OperaPropConfig.getDataSetForCode("Rooms" , dataset);
			WSClient.writeToReport(LogStatus.INFO, value);
			WSClient.setData("{var_CreateRoom}", value);
			WSClient.setData("{var_RoomType}", val_RoomType);
			flag = flag && createRoom(ds);
			dependencies.clear();
		}
		
		//Create Pseudo Room
		row = OperaPropConfig.getRowIndex("OperaConfig","PseudoRoom");
		length = OperaPropConfig.getLengthForCode("PseudoRoom") - 1;
		for(i=1;i<=length;i++) {
			if(i<10)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;
			String comm = OperaPropConfig.getCellComment("OperaConfig",row, i);
			//System.out.println(comm);
			if(comm==null) {
				WSClient.writeToReport(LogStatus.WARNING, "Please provide dependencies");
			}
			dependencies = OperaPropConfig.getDependency(comm);
			val_RoomType = OperaPropConfig.getDataSetForCode("RoomType" , dependencies.get("RoomType"));
			WSClient.setData("{var_RoomType}", val_RoomType);
			String value = OperaPropConfig.getDataSetForCode("PseudoRoom", dataset);
			WSClient.setData("{var_CreateRoom}", value);
			String ds = dependencies.get("Dataset");
			flag = flag && createRoom(ds);
			dependencies.clear();
		}
		
		
		//Create Out Of Order Room
		row = OperaPropConfig.getRowIndex("OperaConfig","OOORoom");
		length = OperaPropConfig.getLengthForCode("OOORoom") - 1;
		for(i=1;i<=length;i++) {
			if(i<10)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;
			String comm = OperaPropConfig.getCellComment("OperaConfig",row, i);
			//System.out.println(comm);
			if(comm==null) {
				WSClient.writeToReport(LogStatus.WARNING, "Please provide dependencies");
			}
			dependencies = OperaPropConfig.getDependency(comm);
			val_RoomType = OperaPropConfig.getDataSetForCode("RoomType" , dependencies.get("RoomType"));
			String value = OperaPropConfig.getDataSetForCode("OOORoom", dataset);
			WSClient.setData("{var_CreateRoom}", value);
			WSClient.setData("{var_RoomType}", val_RoomType);
			String ds = dependencies.get("Dataset");
			flag = flag && createRoom(ds);
			dependencies.clear();
		}
		
		//Create Out Of Service Room
		row = OperaPropConfig.getRowIndex("OperaConfig","OOSRoom");
		length = OperaPropConfig.getLengthForCode("OOSRoom") - 1;
		for(i=1;i<=length;i++) {
			if(i<10)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;
			String comm = OperaPropConfig.getCellComment("OperaConfig",row, i);
			//System.out.println(comm);
			if(comm==null) {
				WSClient.writeToReport(LogStatus.WARNING, "Please provide dependencies");
			}
			dependencies = OperaPropConfig.getDependency(comm);
			val_RoomType = OperaPropConfig.getDataSetForCode("RoomType" , dependencies.get("RoomType"));
			String value = OperaPropConfig.getDataSetForCode("OOSRoom", dataset);
			WSClient.setData("{var_CreateRoom}", value);
			WSClient.setData("{var_RoomType}", val_RoomType);
			String ds = dependencies.get("Dataset");
			flag = flag && createRoom(ds);
			dependencies.clear();
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("Rooms", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("Rooms", "N");;
		}
	}

		