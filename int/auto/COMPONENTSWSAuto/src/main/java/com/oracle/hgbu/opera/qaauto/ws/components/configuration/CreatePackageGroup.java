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

public class CreatePackageGroup extends WSSetUp {
	/**
	 * @author psarawag 
	 * Description: Method to create two package groups.
	 */
	
	public boolean createPackageGroups(String dataset) {
		try {
//				String req_fetchPackageGroups = WSClient.createSOAPMessage("FetchPackageGroups", dataset);
//				String res_fetchPackageGroups = WSClient.processSOAPMessage(req_fetchPackageGroups);
//
//				if (WSAssert.assertIfElementExists(res_fetchPackageGroups, "FetchPackageGroupsRS_Success", false)) {
//					if (WSAssert.assertIfElementExists(res_fetchPackageGroups,
//							"PackageGroupList_PackageGroups_PackageGroup_Code", true)) {
//						WSClient.writeToReport(LogStatus.INFO, "Package Group already exists");
//						return true;
//					} 
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Package Group</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreatePackageGroup", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Package Group " + WSClient.getData("{var_PackageGroup}") +" already exists");
				return true;
			}
				
				else {
						WSClient.writeToReport(LogStatus.INFO, "Package Group doesnot exist!!");
						String req_createPackageGroups = WSClient.createSOAPMessage("CreatePackageGroup", dataset);
						String res_createPackageGroups = WSClient.processSOAPMessage(req_createPackageGroups);
						if (WSAssert.assertIfElementExists(res_createPackageGroups, "CreatePackageGroupRS_Success", true)) {
							WSClient.writeToReport(LogStatus.PASS, "//CreatePackageGroupRS/Success exists on the response message");
							dbResult = WSClient.getDBRow(query);
							val = dbResult.get("COUNT");
							if(WSAssert.assertEquals("1", val, false)) {
								WSClient.writeToReport(LogStatus.INFO, "Package Group has been created");
								return true;
							}
							else {
								WSClient.writeToReport(LogStatus.WARNING, "Package Group not created");
								return false;
							}
								
						}
						else{
							if(WSAssert.assertIfElementExists(res_createPackageGroups, "CreatePackageGroupRS_Errors_Error_ShortText", false)) {
								WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createPackageGroups, "CreatePackageGroupRS_Errors_Error_ShortText", XMLType.RESPONSE));
							}
							return false;
						}
				}

		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	@Test(groups= {"OperaConfig"}, dependsOnGroups = {"createPackage"})
	public void createMultiple_PackageGroups() {
		int i;
		boolean flag = true;
		String testName = "CreatePackageGroups";
		WSClient.startTest(testName, "Create Package Groups", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("PackageGroup") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","PackageGroup");
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
			String value = OperaPropConfig.getDataSetForCode("PackageGroup" , dataset);
			String val_code= OperaPropConfig.getDataSetForCode("PackageCode" , dependencies.get("PackageCode"));
			WSClient.setData("{var_PackageGroup}", value);
			WSClient.setData("{var_PackageCode}", val_code);
			flag = flag && createPackageGroups(dataset);
			dependencies.clear();
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("PackageGroup", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("PackageGroup", "N");
	}
}
