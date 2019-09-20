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

public class CreateChannelCarrierOWS extends WSSetUp{
	
	public boolean createChannelCarrier(String dataset)
	{
		try {
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Channel Carrier</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateChannelCarriers", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Carrier " + WSClient.getData("{var_CarrierCode}") + " for channel " + WSClient.getData("{var_ChannelCode}") +" already exists");
				return true;
			}
			else {
				String createChannelCarrierReq = WSClient.createSOAPMessage("CreateChannelCarriers", dataset);
				String createChannelCarrierRes= WSClient.processSOAPMessage(createChannelCarrierReq);
	            if(WSAssert.assertIfElementExists(createChannelCarrierRes,"CreateChannelCarriersRS_Success",true))
	            {   
	               WSClient.writeToReport(LogStatus.PASS, "//CreateChannelCarriersRS/Success exists on the response message");
	               dbResult = WSClient.getDBRow(query);
	   			   val = dbResult.get("COUNT");
	   			if(WSAssert.assertEquals("1", val, true)) {
	   				WSClient.writeToReport(LogStatus.INFO, "Channel Carrier has been created");
					return true;
				}
				else {
					WSClient.writeToReport(LogStatus.WARNING, "Channel Carrier not created");
					return false;
				}
	            }
				else {
					if(WSAssert.assertIfElementExists(createChannelCarrierRes, "CreateChannelCarriersRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createChannelCarrierRes, "CreateChannelCarriersRS_Errors_Error_ShortText", XMLType.RESPONSE));
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
	
	
	@Test(groups= {"OWS", "createChannelCarrier"}, dependsOnGroups ={"createChannel"})
	public void createMultipleChannelCarrier()
	{
		String testName = "CreateChannelCarrier";
		WSClient.startTest(testName, "Create Channel Carrier","OWS");
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		String resort=OPERALib.getResort();
		String channel=OWSLib.getChannel();
		WSClient.setData("{var_Resort}",resort);
		WSClient.setData("{var_ChannelCode}", channel);
		WSClient.setData("{var_Chain}", OPERALib.getChain());
		int i;
		String num = OperaPropConfig.getChannelCodeForDataSet("NumberOfCarriers", "DS_01");
		
		int len = (int)(Float.parseFloat(num));
		//WSClient.writeToReport(LogStatus.ERROR, ""+len);
		String dataset = "";
		for(i=1;i<=len;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;        
			
			WSClient.setData("{var_CarrierCode}",channel);
			
			flag=flag && createChannelCarrier(dataset);
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("CarrierCodeOWS", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("CarrierCodeOWS", "N");
	}

}
