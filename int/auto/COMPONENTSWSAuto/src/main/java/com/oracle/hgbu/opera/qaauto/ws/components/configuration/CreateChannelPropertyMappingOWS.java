package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateChannelPropertyMappingOWS extends WSSetUp {
	public boolean createPropertyMapping(String dataset)
	{
		try {
			String fetchPropertyMappingReq = WSClient.createSOAPMessage("FetchPropertiesMapping", dataset);
            String fetchPropertyMappingRes= WSClient.processSOAPMessage(fetchPropertyMappingReq);
            if(WSAssert.assertIfElementExists(fetchPropertyMappingRes,"FetchPropertiesMappingRS_Success",false))
            {
            	if(WSAssert.assertIfElementExists(fetchPropertyMappingRes, "PropertiesMapping_Property_ExternalSystemCode", true))
            	{
            			WSClient.writeToReport(LogStatus.INFO, "Property Mapping for the channel already exists");
            			return true;
            	}
            		else
            		{
						String createPropertyMappingReq = WSClient.createSOAPMessage("CreatePropertiesMapping", dataset);
						String createPropertyMappingRes= WSClient.processSOAPMessage(createPropertyMappingReq);
				             if(WSAssert.assertIfElementExists(createPropertyMappingRes,"CreatePropertiesMappingRS_Success",false))
					         {   
				            	 WSClient.writeToReport(LogStatus.INFO, "Successfully Mapped the property to the channel");
				            		 return true;
					         }
				             else
				            	 return false;
            	}
            }
            else
				return false;
			               
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR,"Error is "+ e);
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	@Test(groups= {"OWS","createPropertyMapping" }, dependsOnGroups ={"createChannelCarrier"})
	public void createChannelPropertyMapping()
	{
		String testName = "CreatePropertyMapping";
		WSClient.startTest(testName, "Mapping the channel to a property","OWS");
		boolean flag = true;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		String resort=OPERALib.getResort();
		String channel=OWSLib.getChannel();
		WSClient.setData("{var_Resort}",resort);
		WSClient.setData("{var_ChannelCode}", channel);
		WSClient.setData("{var_ExtResort}", "EX"+resort);
		flag=flag && createPropertyMapping("DS_01");
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("ChannelPropertyMappingOWS", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("ChannelPropertyMappingOWS", "N");
	}


}
