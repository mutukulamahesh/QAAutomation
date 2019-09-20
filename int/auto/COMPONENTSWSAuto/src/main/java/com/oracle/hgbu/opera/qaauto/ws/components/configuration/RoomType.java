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


public class RoomType extends WSSetUp {

	public boolean create_RoomType(String dataset) {
		try {
//			String fetchRoomTypeReq = WSClient.createSOAPMessage("FetchRoomTypes", "DS_01");
//			String fetchRoomTypeResponseXML = WSClient.processSOAPMessage(fetchRoomTypeReq);
//			if(WSAssert.assertIfElementExists(fetchRoomTypeResponseXML, "FetchRoomTypesRS_Success", false)){
//			if (WSAssert.assertIfElementExists(fetchRoomTypeResponseXML,
//					"FetchRoomTypesRS_RoomTypesSummary_RoomTypeSummary_RoomType", true)) {
//				WSClient.writeToReport(LogStatus.INFO, "Room Type already exists");
//				return true;
//			} 
			boolean fl = false;
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Room Type</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateRoomTypes", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Room Type " + WSClient.getData("{var_RoomType}") +" already exists");
				return true;
			}
			else {
				WSClient.writeToReport(LogStatus.INFO, "Room Type doesnot exist!!");
				//WSClient.setData("{var_RoomClass}", "MEETING1");
				WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Room Type Template</b>----------");
				query = WSClient.getQuery("CreateRoomTypeTemplates", "QS_01");
				dbResult = WSClient.getDBRow(query);
				val = dbResult.get("COUNT");
				if(WSAssert.assertEquals("1", val, true)) {
					WSClient.writeToReport(LogStatus.INFO, "Room Type Template for " + WSClient.getData("{var_RoomType}") +" already exists");
					fl= true;
				}
				else {
					String CreateRoomTypeTempReq = WSClient.createSOAPMessage("CreateRoomTypeTemplates", dataset);
					String CreateRoomTypeTempResponseXML = WSClient.processSOAPMessage(CreateRoomTypeTempReq);
					if (WSAssert.assertIfElementExists(CreateRoomTypeTempResponseXML,
							"CreateRoomTypeTemplatesRS_Success", false) == false) {
						if(WSAssert.assertIfElementExists(CreateRoomTypeTempResponseXML, "CreateRoomTypeTemplatesRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(CreateRoomTypeTempResponseXML, "CreateRoomTypeTemplatesRS_Errors_Error_ShortText", XMLType.RESPONSE));
							fl= false;
						}
						
					}
					fl = true;
				}
			if(fl==true) {
				String req_createRoomType = WSClient.createSOAPMessage("CreateRoomTypes", dataset);
				String res_createRoomType = WSClient.processSOAPMessage(req_createRoomType);
						if(WSAssert.assertIfElementExists(res_createRoomType, "CreateRoomTypesRS_Success", true)) {
							WSClient.writeToReport(LogStatus.PASS, "//CreateRoomTypesRS/Success exists on the response message");
							dbResult = WSClient.getDBRow(query);
							val = dbResult.get("COUNT");
							if(WSAssert.assertEquals("1", val, true)) {
								WSClient.writeToReport(LogStatus.INFO, "Successfully created Room Type");
								return true;
							}
							else {
								WSClient.writeToReport(LogStatus.WARNING, "Room Type not created");
								return false;
							}
						}
						else {
							if(WSAssert.assertIfElementExists(res_createRoomType, "CreateRoomTypesRS_Errors_Error_ShortText", false)) {
								WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createRoomType, "CreateRoomTypesRS_Errors_Error_ShortText", XMLType.RESPONSE));
							}
							return false;
						}
			}
			else
				return false;
				
			}
		}
			
		catch(Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	@Test(groups= {"createRoomType"}, dependsOnGroups = {"createRoomClass", "CreateRoomFeatures"})
	public void createMultiple_RoomType() {
		int i;
		String testName = "CreateRoomType";
		boolean flag=true;
		WSClient.startTest(testName, "Create Room Type", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		WSClient.setData("{var_chain}", OPERALib.getChain());
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("RoomType") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","RoomType");

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
			String value = OperaPropConfig.getDataSetForCode("RoomType" , dataset);
			WSClient.setData("{var_RoomFeature}", OperaPropConfig.getDataSetForCode("RoomFeature", dependencies.get("RoomFeature")));
			String val_roomClass = OperaPropConfig.getDataSetForCode("RoomClass" , dependencies.get("RoomClass"));
			WSClient.setData("{var_MRoomType}", OperaPropConfig.getDataSetForCode("RoomType", dependencies.get("MRoomType")));
			WSClient.setData("{var_RoomType}", value);
			WSClient.setData("{var_RoomClass}", val_roomClass);
			flag = flag && create_RoomType(dataset);
			dependencies.clear();
		}
		
		  
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("RoomType", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("RoomType", "N");;
		}
}


