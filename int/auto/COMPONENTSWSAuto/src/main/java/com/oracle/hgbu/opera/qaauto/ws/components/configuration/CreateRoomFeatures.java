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

public class CreateRoomFeatures extends WSSetUp{

	public boolean createRoomFeature(String dataset) {
		try {
			
//			String req_fetchRoomFeature = WSClient.createSOAPMessage("FetchRoomFeatures", dataset);
//			String res_fetchRoomFeature = WSClient.processSOAPMessage(req_fetchRoomFeature);
//			if(WSAssert.assertIfElementExists(res_fetchRoomFeature, "FetchRoomFeaturesRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(res_fetchRoomFeature, "FetchRoomFeaturesRS_RoomFeatures_RoomFeature_Code", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "Room Feature already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Room Feature</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateRoomFeatures", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Room Feature " + WSClient.getData("{var_RoomFeature}") +" already exists");
				return true;
			}
			
				else {
					WSClient.writeToReport(LogStatus.INFO, "Room Feature doesnot exist!!");
					String req_createRoomFeature = WSClient.createSOAPMessage("CreateRoomFeatures", dataset);
					String res_createRoomFeature = WSClient.processSOAPMessage(req_createRoomFeature);
					if(WSAssert.assertIfElementExists(res_createRoomFeature, "CreateRoomFeaturesRS_Success", true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreateRoomFeaturesRS/Success exists on the response message");
						query = WSClient.getQuery("CreateRoomFeatures", "QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully created Room Feature");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Room Feature not created");
							return false;
						}
					}
					else {
						if(WSAssert.assertIfElementExists(res_createRoomFeature, "CreateRoomFeaturesRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createRoomFeature, "CreateRoomFeaturesRS_Errors_Error_ShortText", XMLType.RESPONSE));
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
	
	
	@Test(groups= {"CreateRoomFeatures","OperaConfig"})
	public void createMultiple_RoomFeatures() {
		int i;
		boolean flag = true;
		String testName = "CreateRoomFeatures";
		WSClient.startTest(testName, "Create Room Feature", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort());
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("RoomFeature") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("RoomFeature" , dataset);
			WSClient.setData("{var_RoomFeature}", value);
			flag = flag && createRoomFeature(dataset);
		}
		if(flag == true)
			OperaPropConfig.setPropertyConfigResults("RoomFeature", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("RoomFeature", "N");
	}
}
