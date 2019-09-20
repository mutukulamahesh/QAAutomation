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

public class CreateMarketCode extends WSSetUp{
	

		public boolean createMarketCode(String dataset)
		{
			try {
				
//				String fetchMarketCodeReq = WSClient.createSOAPMessage("FetchMarketCodes", dataset);
//	            String fetchMarketCodeRes= WSClient.processSOAPMessage(fetchMarketCodeReq);
//	            if(WSAssert.assertIfElementExists(fetchMarketCodeRes,"FetchMarketCodesRS_Success",false))
//	            {
//	            	if(WSAssert.assertIfElementExists(fetchMarketCodeRes, "FetchMarketCodesRS_MarketCodes", false))
//	            	{
//	            		if(WSAssert.assertIfElementExists(fetchMarketCodeRes, "FetchMarketCodesRS_MarketCodes_MarketCode_Code", true))
//	            		{
//	            			WSClient.writeToReport(LogStatus.INFO, "Market code "+WSClient.getElementValue(fetchMarketCodeRes, "FetchMarketCodesRS_MarketCodes_MarketCode_Code", XMLType.RESPONSE) +" already exists");
//	            			return true;
//	            		}
				WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Market Code</b>----------");
				LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
				String query = WSClient.getQuery("CreateMarketCode", "QS_01");
				dbResult = WSClient.getDBRow(query);
				String val = dbResult.get("COUNT");
				if(WSAssert.assertEquals("1", val, true)) {
					WSClient.writeToReport(LogStatus.INFO, "Market Code " + WSClient.getData("{var_MarketCode}") +" already exists");
					return true;
				}
	            		else
	            		{
							String createMarketCodeReq = WSClient.createSOAPMessage("CreateMarketCode", dataset);
							String createMarketCodeRes= WSClient.processSOAPMessage(createMarketCodeReq);
					             if(WSAssert.assertIfElementExists(createMarketCodeRes,"CreateMarketCodeRS_Success",true)) {
					            	 WSClient.writeToReport(LogStatus.PASS, "//CreateMarketCodeRS/Success exists on the response message");
										dbResult = new LinkedHashMap<String, String>();
										dbResult = WSClient.getDBRow(query);
										val = dbResult.get("COUNT");
										if(WSAssert.assertEquals("1", val, false)) {
											WSClient.writeToReport(LogStatus.INFO, "Market Code has been created");
											return true;
										}
										else {
											WSClient.writeToReport(LogStatus.WARNING, "Market Code not created");
											return false;
										}
											
									}
									else{
										if(WSAssert.assertIfElementExists(createMarketCodeRes, "CreateMarketCodeRS_Errors_Error_ShortText", false)) {
											WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createMarketCodeRes, "CreateMarketCodeRS_Errors_Error_ShortText", XMLType.RESPONSE));
										}
										return false;
									}
						         
	            	}
				               
			}catch (Exception e) {
				WSClient.writeToReport(LogStatus.ERROR,"Error is "+ e);
				e.printStackTrace();
				return false;
			}
			
		}
		
	
		@Test(groups= {"OperaConfig","createMarketCode"},dependsOnGroups={"createMarketGroup"})
		public void createMultipleMarketCodes()
		{
			String testName = "createMarketCode";
			WSClient.startTest(testName, "Creating a market code","OperaConfig");
			boolean flag = true;
			String mCode,mGroup = "";
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_Resort}",OPERALib.getResort() );
			String dataset = "";
			HashMap<String,String> dependencies = new HashMap<String,String>();
			int i;
			int length = OperaPropConfig.getLengthForCode("MarketCode") - 1;
			int row = OperaPropConfig.getRowIndex("OperaConfig","MarketCode");
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
				mGroup=OperaPropConfig.getDataSetForCode("MarketGroup", dependencies.get("MarketGroup"));
				
				mCode=OperaPropConfig.getDataSetForCode("MarketCode", dataset);
				WSClient.setData("{var_MarketCode}",mCode);
				WSClient.setData("{var_MarketGroup}",mGroup);
				flag=flag && createMarketCode(dataset);
				dependencies.clear();
			}
			if(flag == true) 
				OperaPropConfig.setPropertyConfigResults("MarketCode", "Y");
			else
				OperaPropConfig.setPropertyConfigResults("MarketCode", "N");

		}

}
