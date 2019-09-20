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

public class CreateDepartmentCode extends WSSetUp{
	public boolean createDeptCode(String dataset) {
		try {
			

			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Department Code</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateDepartments", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Department Code " + WSClient.getData("{var_DeptCode}") +" already exists");
				return true;
			}
			else
			{
				String req_createDeptCode = WSClient.createSOAPMessage("CreateDepartments", dataset);
				String res_createDeptCode = WSClient.processSOAPMessage(req_createDeptCode);
				if(WSAssert.assertIfElementExists(res_createDeptCode, "CreateDepartmentsRS_Success", true)){
					WSClient.writeToReport(LogStatus.PASS, "//CreateDepartmentsRS/Success exists on the response message");
					dbResult = WSClient.getDBRow(query);
					val = dbResult.get("COUNT");
					if(WSAssert.assertEquals("1", val, false)) {
						WSClient.writeToReport(LogStatus.INFO, "Department Code has been created");
						return true;
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "Department Code not created");
						return false;
					}
						
				}
				else{
					if(WSAssert.assertIfElementExists(res_createDeptCode, "CreateDepartmentsRS_Errors_Error_ShortText", false)) {
						WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createDeptCode, "CreateDepartmentsRS_Errors_Error_ShortText", XMLType.RESPONSE));
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
	
	
	@Test(groups= {"OperaConfig", "createDepartmentCode"})
	public void createMultipleDeptCodes() {
		int i;
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String testName = "CreateDepartmentCodes";
		WSClient.startTest(testName, "Create Department Codes", "OperaConfig");
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("DepartmentCode") - 1;
		//WSClient.writeToReport(LogStatus.ERROR, ""+length);
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			String value = OperaPropConfig.getDataSetForCode("DepartmentCode" , dataset);
			WSClient.setData("{var_DeptCode}", value);
			flag = flag && createDeptCode(dataset);
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("DepartmentCode", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("DepartmentCode", "N");
	}

}
