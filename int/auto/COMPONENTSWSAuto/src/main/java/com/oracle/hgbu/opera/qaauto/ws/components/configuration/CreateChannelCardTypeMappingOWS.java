package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateChannelCardTypeMappingOWS  extends WSSetUp{

	public boolean createChannelCardTypeMapping(String dataset)
	{
		try {
			
			String fetchChannelCardTypeMappingReq = WSClient.createSOAPMessage("FetchChannelCardTypeMappings", dataset);
            String fetchChannelCardTypeMappingRes= WSClient.processSOAPMessage(fetchChannelCardTypeMappingReq);
            if(WSAssert.assertIfElementExists(fetchChannelCardTypeMappingRes,"FetchChannelCardTypeMappingsRS_Success",false))
            {
            	if(WSAssert.assertIfElementExists(fetchChannelCardTypeMappingRes, "FetchChannelCardTypeMappingsRS_ChannelCardTypeMappings", false))
            	{
            		if(WSAssert.assertIfElementExists(fetchChannelCardTypeMappingRes, "FetchChannelCardTypeMappingsRS_ChannelCardTypeMappings_ChannelCardTypeMapping", true))
            		{
            			WSClient.writeToReport(LogStatus.INFO, "Channel Card Type Mapping for "+WSClient.getElementValue(fetchChannelCardTypeMappingRes, "FetchChannelCardTypeMappingsRS_ChannelCardTypeMappings_ChannelCardTypeMapping_CardType", XMLType.RESPONSE) +" already exists");
            			return true;
            		}
            		else
            		{
						String createChannelCardTypeMappingReq = WSClient.createSOAPMessage("CreateChannelCardTypeMapping", dataset);
						String createChannelCardTypeMappingRes= WSClient.processSOAPMessage(createChannelCardTypeMappingReq);
				             if(WSAssert.assertIfElementExists(createChannelCardTypeMappingRes,"CreateChannelCardTypeMappingRS_Success",false))
					         {   
				            	 WSClient.writeToReport(LogStatus.INFO, "Channel Card Type Mapping for "+WSClient.getElementValue(createChannelCardTypeMappingReq, "CreateChannelCardTypeMappingRQ_ChannelCardTypeMapping_CardType", XMLType.REQUEST)+" got created");
				            		 return true;
					         }
				             else
				            	 return false;
            		}
            	}
            	else
    				return false;
            }
            else
				return false;
			               
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR,"Error is "+ e);
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	@Test(groups= {"OWS"}, dependsOnGroups ={"createCreditCardsMapping"})
	public void createMultipleChannelCardTypeMapping()
	{
		String testName = "createMultipleChannelCardTypeMapping";
		WSClient.startTest(testName, "Create Payment methods Mapping for Channel operations","OperaConfig");
		boolean flag = true;
		String dataset,localCode,channelCode;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		String resort=OPERALib.getResort();
		String channel=OWSLib.getChannel();
		WSClient.setData("{var_Resort}",resort);
		//String carrier= OWSLib.getChannelCarier(resort,channel);
		WSClient.setData("{var_systemCode}", channel);
		int i = 0;
		int length = OperaPropConfig.getLengthForCodeOWS("PaymentMethod") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OWS","PaymentMethod");
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;  
			String comm = OperaPropConfig.getCellComment("OWS",row, i);
			//System.out.println(comm);
			if(comm==null) {
				WSClient.writeToReport(LogStatus.WARNING, "Please provide dependencies");
			}
			dependencies = OperaPropConfig.getDependency(comm);
			channelCode=OperaPropConfig.getChannelCodeForDataSet("PaymentMethod", dependencies.get("PaymentMethod"));
			localCode=OperaPropConfig.getDataSetForCode("PaymentMethod", dataset);
			WSClient.setData("{var_localCode}",localCode);
			WSClient.setData("{var_externalCode}",channelCode);
			flag=flag && createChannelCardTypeMapping(dataset);
			dependencies.clear();
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("ChannelCardTypeMappingOWS", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("ChannelCardTypeMappingOWS", "N");
	}
	
}
