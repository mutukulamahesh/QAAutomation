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

public class CreateMarketGroup extends WSSetUp{
	
	public boolean createMarketGroup(String dataSet)
	{
		try {
//			String fetchMarketGroupReq = WSClient.createSOAPMessage("FetchMarketGroups", dataSet);
//            String fetchMarketGroupRes= WSClient.processSOAPMessage(fetchMarketGroupReq);
//            if(WSAssert.assertIfElementExists(fetchMarketGroupRes,"FetchMarketGroupsRS_Success",false))
//            {
//            	if(WSAssert.assertIfElementExists(fetchMarketGroupRes, "FetchMarketGroupsRS_MarketGroups", false))
//            	{
//            		if(WSAssert.assertIfElementExists(fetchMarketGroupRes, "FetchMarketGroupsRS_MarketGroups_MarketGroup_Code", true))
//            		{
//            			WSClient.writeToReport(LogStatus.INFO, "Market Group "+WSClient.getElementValue(fetchMarketGroupRes, "FetchMarketGroupsRS_MarketGroups_MarketGroup_Code", XMLType.RESPONSE)+" already exists");
//            			return true;
//            		}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Market Group</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateMarketGroup", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Market Group " + WSClient.getData("{var_MarketGroup}") +" already exists");
				return true;
			}
            		else
            		{
						 String createMarketGroupReq = WSClient.createSOAPMessage("CreateMarketGroup", dataSet);
			             String createMarketGroupRes= WSClient.processSOAPMessage(createMarketGroupReq);
			             if(WSAssert.assertIfElementExists(createMarketGroupRes,"CreateMarketGroupRS_Success",true)) {
			            	 WSClient.writeToReport(LogStatus.PASS, "//CreateMarketGroupRS/Success exists on the response message");
								dbResult = WSClient.getDBRow(query);
								val = dbResult.get("COUNT");
								if(WSAssert.assertEquals("1", val, false)) {
									WSClient.writeToReport(LogStatus.INFO, "Market Group has been created");
									return true;
								}
								else {
									WSClient.writeToReport(LogStatus.WARNING, "Market Group not created");
									return false;
								}
									
							}
							else{
								if(WSAssert.assertIfElementExists(createMarketGroupRes, "CreateMarketGroupRS_Errors_Error_ShortText", false)) {
									WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createMarketGroupRes, "CreateMarketGroupRS_Errors_Error_ShortText", XMLType.RESPONSE));
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

	@Test(groups= {"createMarketGroup"})
    public void createMultipleMarketGroups()
    {
		String testName = "createMarketGroup";
		WSClient.startTest(testName, "Creating a market group","OperaConfig");
		boolean flag=true;
		String ds,mGroup;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		int i;
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("MarketGroup") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			mGroup=OperaPropConfig.getDataSetForCode("MarketGroup", dataset);
			WSClient.setData("{var_MarketGroup}",mGroup);
			flag=flag && createMarketGroup(dataset);
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("MarketGroup", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("MarketGroup", "N");

    }

}
