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

public class CreatePackage extends WSSetUp{

	/**
	 * @author psarawag 
	 * Description: Method to create two packages.
	 */
	
	public boolean createPackages(String dataset) {
		try {
//			String req_fetchPackage = WSClient.createSOAPMessage("RatePlanFetchPackage", dataset);
//			String res_fetchPackage = WSClient.processSOAPMessage(req_fetchPackage);
//			
//			if(WSAssert.assertIfElementExists(res_fetchPackage, "FetchPackageRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(res_fetchPackage, "FetchPackageRS_PackageCode_Code", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "Package Code already exists");
//					return true;
//				}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Package Code</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreatePackage", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Package " + WSClient.getData("{var_PackageCode}") +" already exists");
				return true;
			}
				else{
					WSClient.writeToReport(LogStatus.INFO, "Package Code doesnot exist!!");
					String req_createPackage = WSClient.createSOAPMessage("CreatePackage", dataset);
					String res_createPackage = WSClient.processSOAPMessage(req_createPackage);
					if(WSAssert.assertIfElementExists(res_createPackage, "CreatePackageRS_Success", true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreatePackageRS/Success exists on the response message");
						query = WSClient.getQuery("CreatePackage", "QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, false)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully created Package");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Package not created");
							return false;
						}
					}
					else {
						if(WSAssert.assertIfElementExists(res_createPackage, "CreatePackageRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createPackage, "CreatePackageRS_Errors_Error_ShortText", XMLType.RESPONSE));
						}
						return false;
					}
				}
		}catch(Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, e.toString());
		return false;
		}
	}
	
	@Test(groups= {"createPackage"}, dependsOnGroups = {"createTransactionCodes","createItem" })
	public void createMultiple_Packages() {
		int i;
		boolean flag = true;
		String testName = "createPackages";
		WSClient.startTest(testName, "create Packages", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		String dataset = "";
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","PackageCode");
		int length = OperaPropConfig.getLengthForCode("PackageCode") - 1;
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
			String val_tcode = OperaPropConfig.getDataSetForCode("TransactionCode" , dependencies.get("TransactionCode"));
			WSClient.setData("{var_TransactionCode}", val_tcode);
			WSClient.setData("{var_ItemCode}", OperaPropConfig.getDataSetForCode("ItemCode", dependencies.get("ItemCode")));
			WSClient.setData("{var_ItemCode1}", OperaPropConfig.getDataSetForCode("ItemCode", dependencies.get("ItemCode1")));
			String value = OperaPropConfig.getDataSetForCode("PackageCode" , dataset);
			WSClient.setData("{var_PackageCode}", value);
			flag = flag && createPackages(dataset);
			dependencies.clear();
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("PackageCode", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("PackageCode", "N");
	}
}
