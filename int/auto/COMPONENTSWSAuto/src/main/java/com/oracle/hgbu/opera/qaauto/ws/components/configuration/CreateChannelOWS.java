package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateChannelOWS extends WSSetUp{
	public boolean createChannelCarrier(String dataset)
	{
		try {
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Channel</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateChannels", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Channel " + WSClient.getData("{var_ChannelCode}") + " already exists");
				return true;
			}
			else {
				String createChannelReq = WSClient.createSOAPMessage("CreateChannels", dataset);
				String createChannelRes= WSClient.processSOAPMessage(createChannelReq);
	            if(WSAssert.assertIfElementExists(createChannelRes,"CreateChannelsRS_Success",true))
	            {   
	               WSClient.writeToReport(LogStatus.PASS, "//CreateChannelsRS/Success exists on the response message");
	               dbResult = WSClient.getDBRow(query);
	   			   val = dbResult.get("COUNT");
	   			if(WSAssert.assertEquals("1", val, true)) {
	   				WSClient.writeToReport(LogStatus.INFO, "Channel has been created");
					return true;
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING, "Channel not created");
					return false;
				}
	            }
				else {
					if(WSAssert.assertIfElementExists(createChannelRes, "CreateChannelsRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createChannelRes, "CreateChannelsRS_Errors_Error_ShortText", XMLType.RESPONSE));
					}
							return false;
										
					}
			}

	}catch(Exception e){
		WSClient.writeToReport(LogStatus.ERROR, "Error is " + e);
		e.printStackTrace();
		return false;
	}
		
	}
	
	
	@Test(groups= {"OWS", "createChannel"}, priority=1500, dependsOnGroups ={"OperaConfig"})
	public void createMultipleChannelCarrier()
	{
		String testName = "CreateChannel";
		WSClient.startTest(testName, "Create Channel","OWS");
		boolean flag = true;
		
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		String resort=OPERALib.getResort();
		//String channel=OWSLib.getChannel();
		WSClient.setData("{var_Resort}",resort);
		
		WSClient.setData("{var_Chain}", OPERALib.getChain());
		int i;
		String dataset = "";
		
		for(i=1;i<=1;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;      
			WSClient.setData("{var_ChannelCode}",OWSLib.getChannel());
			WSClient.setData("{var_sellBy}", OperaPropConfig.getChannelCodeForDataSet("SellBy", dataset));
			WSClient.setData("{var_ChannelType}",OperaPropConfig.getChannelCodeForDataSet("ChannelType", dataset));
			
			flag=flag && createChannelCarrier(dataset);
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("CarrierCodeOWS", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("CarrierCodeOWS", "N");
	}

}
