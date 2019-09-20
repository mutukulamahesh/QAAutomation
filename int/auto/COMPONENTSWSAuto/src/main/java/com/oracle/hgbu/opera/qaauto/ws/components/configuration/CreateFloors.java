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

public class CreateFloors extends WSSetUp{
	public boolean createFloor(String dataset) {
		try {
			
//			String req_fetchFloors = WSClient.createSOAPMessage("FetchFloors", dataset);
//			String res_fetchFloors = WSClient.processSOAPMessage(req_fetchFloors);
//			if(WSAssert.assertIfElementExists(res_fetchFloors, "FetchFloorsRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(res_fetchFloors, "HotelFloors_Floor_Floor", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "Floor already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Floor</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateFloors", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Floor " + WSClient.getData("{var_Floor}") +" already exists");
				return true;
			}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Floor doesnot exist!!");
					String req_createFloors = WSClient.createSOAPMessage("CreateFloors", dataset);
					String res_createFloors = WSClient.processSOAPMessage(req_createFloors);
					if(WSAssert.assertIfElementExists(res_createFloors, "CreateFloorsRS_Success", true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreateFloorsRS/Success exists on the response message");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, false)) {
							WSClient.writeToReport(LogStatus.INFO, "New Floor has been created");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Floor not created");
							return false;
						}
							
					}
					else{
						if(WSAssert.assertIfElementExists(res_createFloors, "CreateFloorsRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createFloors, "CreateFloorsRS_Errors_Error_ShortText", XMLType.RESPONSE));
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
	
	
	@Test(groups= {"CreateFloors", "OperaConfig"})
	public void createMultiple_Floors() {
		int i;
		boolean flag = true;
		String testName = "CreateFloors";
		WSClient.startTest(testName, "Create Floors", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("RoomFloor") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("RoomFloor" , dataset);
			WSClient.setData("{var_Floor}", value);
			flag = flag && createFloor(dataset);
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("RoomFloor", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("RoomFloor", "N");
	}


}
