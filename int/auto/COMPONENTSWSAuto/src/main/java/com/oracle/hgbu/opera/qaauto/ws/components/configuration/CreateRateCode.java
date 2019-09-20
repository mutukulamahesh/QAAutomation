package com.oracle.hgbu.opera.qaauto.ws.components.configuration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateRateCode extends WSSetUp {

	public boolean attachPackagesToRateCode(String dataset)
	{
		try{//*** Attach Rate Plan Packages ****//
			String AttachPlanXML = WSClient.createSOAPMessage("AttachRatePlanPackages", dataset);
			String AttachPlanResponseXML = WSClient.processSOAPMessage(AttachPlanXML);
			if (WSAssert.assertIfElementExists(AttachPlanResponseXML, "AttachRatePlanPackagesRS_Success",true))
			{	
				WSClient.writeToReport(LogStatus.PASS, "//AttachRatePlanPackagesRS/Success exists on the response message");
				return true;
			} 
			else {
				if(WSAssert.assertIfElementExists(AttachPlanResponseXML, "AttachRatePlanPackagesRS_Errors_Error_ShortText", false))
					if(WSAssert.assertIfElementContains(AttachPlanResponseXML,"AttachRatePlanPackagesRS_Errors_Error_ShortText", "Exists", false)) {
						WSClient.writeToReport(LogStatus.INFO, "Packages already attached");
						return true;
					}
					else
						WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(AttachPlanResponseXML, "AttachRatePlanPackagesRS_Errors_Error_ShortText", XMLType.RESPONSE));
			}
				return false;
		}catch(Exception e)
		{
			WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	public boolean createRateCode(String dataset) {
		try {
			//*** fetch rate plan ****//
			String fetchRatePlanXML = WSClient.createSOAPMessage("FetchRatePlan", "DS_03");
			String fetchRatePlanResponseXML = WSClient.processSOAPMessage(fetchRatePlanXML);

			if (WSAssert.assertIfElementExists(fetchRatePlanResponseXML, "FetchRatePlanRS_Success", false)) 
			{
				if (WSAssert.assertIfElementExists(fetchRatePlanResponseXML,"FetchRatePlanRS_RatePlans_RatePlan_RatePlanCode", true))
				{
					WSClient.writeToReport(LogStatus.INFO, "Rate Code already exists");
					return true;
				}
				else
				{
				//*** Create Rate Plan ****//
					WSClient.writeToReport(LogStatus.INFO, "Rate Code doesn't exist");
					String createRatePlanXML = WSClient.createSOAPMessage("CreateRatePlan", dataset);
					String createRatePlanResponseXML = WSClient.processSOAPMessage(createRatePlanXML);
					if (WSAssert.assertIfElementExists(createRatePlanResponseXML, "CreateRatePlanRS_Success", true)) {
						WSClient.writeToReport(LogStatus.PASS, "//CreateRatePlanRS/Success exists on the response message");
						WSClient.writeToReport(LogStatus.INFO, "Successfully created Rate Code");
						return true;
					}
					else {
						if(WSAssert.assertIfElementExists(createRatePlanResponseXML, "CreateRatePlanRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createRatePlanResponseXML, "CreateRatePlanRS_Errors_Error_ShortText", XMLType.RESPONSE));
						}
						return false;
					}	
				}
			}
			else
				return false;
		}catch(Exception e) {
				WSClient.writeToReport(LogStatus.ERROR, e.toString());
				return false;
			}
	
	}
	
	public boolean createRateCodeWithPackage(String dataset) {
		try {
			//*** fetch rate plan ****//
			String fetchRatePlanXML = WSClient.createSOAPMessage("FetchRatePlan", dataset);
			String fetchRatePlanResponseXML = WSClient.processSOAPMessage(fetchRatePlanXML);
			if (WSAssert.assertIfElementExists(fetchRatePlanResponseXML, "FetchRatePlanRS_Success", false)) 
			{
				if (WSAssert.assertIfElementExists(fetchRatePlanResponseXML,"FetchRatePlanRS_RatePlans_RatePlan_RatePlanCode", true))
				{
					if (WSAssert.assertIfElementExists(fetchRatePlanResponseXML,"RatePackages_Packages_Package", true))
					{
						HashMap<String, String> xpath=new HashMap<>();
						xpath.put("RatePackages_Packages_Package", "RatePlan_RatePackages_Packages");
						List<LinkedHashMap<String, String>> list=WSClient.getMultipleNodeList(fetchRatePlanResponseXML, xpath, false, XMLType.RESPONSE);
						int count=list.get(0).size();
						if (count==Integer.parseInt(dataset.substring(dataset.length()-1)))
						{
							WSClient.writeToReport(LogStatus.INFO, "----The rate package is already there!----");
							return true;
						} 
						else 
							return attachPackagesToRateCode(dataset);
					}
					else
						return attachPackagesToRateCode(dataset);
				}
				else 
				{
					WSClient.writeToReport(LogStatus.INFO, "----Creating rate code!!----");
					//*** Create Rate Plan ****//
					String createRatePlanXML = WSClient.createSOAPMessage("CreateRatePlan",dataset);
					String createRatePlanResponseXML = WSClient.processSOAPMessage(createRatePlanXML);
					if (WSAssert.assertIfElementExists(createRatePlanResponseXML, "CreateRatePlanRS_Success", true))
					{
						String rateCode = WSClient.getElementValue(createRatePlanXML,"CreateRatePlanRQ_RatePlan_RatePlanCode", XMLType.REQUEST);
						WSClient.writeToReport(LogStatus.INFO, "----The rate code is created!----");
						WSClient.setData("{var_ratePlanCode}", rateCode);
						//***Attach Rate Plan Packages ****//
						return attachPackagesToRateCode(dataset);
					}
					else 
					{
							return false;
					}
				}
			}
			else 
				return false;
		} catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}

	@Test(groups = {"OperaConfig", "createRateCode"},dependsOnGroups={"createTransactionCodes", "createRateCategory", "createRoomType","createPackage","createCommissionCode", "createMarketCode"})
	public void createMultipleRateCodes() {
		int i;
		boolean flag = true;
		String testName = "createRateCode",rCode;
		WSClient.startTest(testName, "Create Rate Codes", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		String dataset="";
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		int length = OperaPropConfig.getLengthForCode("RateCode") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OperaConfig","RateCode");
		int j = 1;
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
			String roomType=OperaPropConfig.getDataSetForCode("RoomType", dependencies.get("RoomType"));
			WSClient.setData("{var_RoomType}", roomType);
			String rateCategory=OperaPropConfig.getDataSetForCode("RateCategory", dependencies.get("RateCategory"));
			WSClient.setData("{var_RateCategory}", rateCategory);
			String tCode=OperaPropConfig.getDataSetForCode("TransactionCode", dependencies.get("TransactionCode"));
			WSClient.setData("{var_TransactionCode}",tCode);
			String pack=OperaPropConfig.getDataSetForCode("PackageCode", dependencies.get("PackageCode"));
			WSClient.setData("{var_PackageCode}", pack);
			String pack1=OperaPropConfig.getDataSetForCode("PackageCode", dependencies.get("PackageCode1"));
			WSClient.setData("{var_PackageCode1}", pack1);
			String comm_code = OperaPropConfig.getDataSetForCode("CommissionCode", dependencies.get("CommissionCode"));
			WSClient.setData("{var_CommissionCode}", comm_code);
			String mkt_code = OperaPropConfig.getDataSetForCode("MarketCode", dependencies.get("MarketCode"));
			WSClient.setData("{var_MarketCode}", mkt_code);
			rCode=OperaPropConfig.getDataSetForCode("RateCode", dataset);
			WSClient.setData("{var_RateCode}", rCode);
			flag = flag && createRateCode(dataset);
			if(pack!=null || pack1!=null) {
				String ds = "";
				if(i<=9)
					ds = "DS_0" + j; 
				else
					ds = "DS_" + j;  
				flag = flag && attachPackagesToRateCode(ds);
				j++;
			}
			dependencies.clear();
		}
		
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("RateCode", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("RateCode", "N");
		
	}

}
