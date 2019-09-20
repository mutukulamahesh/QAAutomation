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

public class HouseKeepingTasks extends WSSetUp {
	
	
	
	public boolean CreateHouseKeepingTask(String ds){
		
		try{			
//			String fetchTasksReq = WSClient.createSOAPMessage("FetchHousekeepingTasks", ds);
//			String fetchTasksRes = WSClient.processSOAPMessage(fetchTasksReq);
//			boolean flag = false;
//			
//			if(WSAssert.assertIfElementExists(fetchTasksRes, "FetchHousekeepingTasksRS_Success", false) &&
//					WSAssert.assertIfElementExists(fetchTasksRes, "Tasks_Task_Code", true)){
//				WSClient.writeToReport(LogStatus.INFO, "HouseKeepingTask Already Exists");
//				flag = true;
//			}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching HouseKeeping Task Code</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateHousekeepingTasks", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Task Code " + WSClient.getData("{var_TaskCode}") +" already exists");
				return true;
			}
			else{
				    String createTasksReq = WSClient.createSOAPMessage("CreateHousekeepingTasks", ds);
					String createTasksRes = WSClient.processSOAPMessage(createTasksReq);
					if(WSAssert.assertIfElementExists(createTasksRes, "CreateHousekeepingTasksRS_Success", true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreateHousekeepingTasksRS/Success exists on the response message");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully created task code");	
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Task Code not created");
							return false;
						}
					}
					else{
						if(WSAssert.assertIfElementExists(createTasksRes, "CreateHousekeepingTasksRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createTasksRes, "CreateHousekeepingTasksRS_Errors_Error_ShortText", XMLType.RESPONSE));
						}
						return false;
					}
			}
		}
		catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, "Exception occured in test due to:" + e);
			return false;
		}
		
	}
	
	
	@Test(groups = {"OperaConfig"})
	public void createMultiple_HouseKeepingTaks(){
		String testName = "createHouseKeepingTasks";
		WSClient.startTest(testName,
				"Create House Keeping Tasks if not present",
				"OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		boolean flag = true;
		int i;
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("TaskCode") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("TaskCode" , dataset);
			WSClient.setData("{var_TaskCode}", value);
			flag = flag && CreateHouseKeepingTask(dataset);
		}
		
		if(flag)
			OperaPropConfig.setPropertyConfigResults("TaskCode", "Y");
		else 
			OperaPropConfig.setPropertyConfigResults("TaskCode", "N");
		
	}
	
}
