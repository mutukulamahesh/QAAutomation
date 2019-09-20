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

public class CreateChannelRoomMappingOWS extends WSSetUp{

	public boolean createChannelRoomMapping(String dataset)
	{
		try {
			String fetchChannelRoomMappingReq = WSClient.createSOAPMessage("FetchChannelRoomMapping", dataset);
            String fetchChannelRoomMappingRes= WSClient.processSOAPMessage(fetchChannelRoomMappingReq);
            if(WSAssert.assertIfElementExists(fetchChannelRoomMappingRes,"FetchChannelRoomMappingRS_Success",false))
            {
            	if(WSAssert.assertIfElementExists(fetchChannelRoomMappingRes, "FetchChannelRoomMappingRS_ChannelRoomMappingsSummary", false))
            	{
            		if(WSAssert.assertIfElementExists(fetchChannelRoomMappingRes, "FetchChannelRoomMappingRS_ChannelRoomMappingsSummary_ChannelRoomMappingSummary", true))
            		{
            			WSClient.writeToReport(LogStatus.INFO, "Channel Room Mapping for "+WSClient.getElementValue(fetchChannelRoomMappingRes, "FetchChannelRoomMappingRS_ChannelRoomMappingsSummary_ChannelRoomMappingSummary_RoomType", XMLType.RESPONSE) +" already exists");
            			return true;
            		}
            		else
            		{
						String createChannelRoomMappingReq = WSClient.createSOAPMessage("CreateChannelRoomMapping", dataset);
						String createChannelRoomMappingRes= WSClient.processSOAPMessage(createChannelRoomMappingReq);
						String code=WSAssert.getElementValue(createChannelRoomMappingReq, "CreateChannelRoomMappingRQ_ChannelRoomMappings_ChannelRoomMapping_RoomType", XMLType.REQUEST);
						if(WSAssert.assertIfElementExists(createChannelRoomMappingRes,"CreateChannelRoomMappingRS_Success",true))
						{   
							WSClient.writeToReport(LogStatus.INFO, "Channel Room Mapping for "+code+" got created");
							return true;      
			            }
			            else
			            {
			            	return false;
			            }
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
	

	@Test(groups= {"OWS", "createChannelRoomMapping"},dependsOnGroups={"createChannelRoomTypes"})
	public void createMultipleChannelRoomMapping()
	{
		String testName = "createChannelRoomMapping";
		WSClient.startTest(testName, "Creating channel room types","OperaConfig");
		boolean flag = true;
		String ds,channelCode,localCode;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort());
		String channel=OWSLib.getChannel();
		WSClient.setData("{var_channel}", channel);
		String channelType = OperaPropConfig.getChannelCodeForDataSet("ChannelType", "DS_01");
		WSClient.setData("{var_ChannelType}", channelType);
		HashMap<String,String> dependencies = new HashMap<String,String>();
		String dataset="";
		int length = OperaPropConfig.getLengthForCodeOWS("RoomType")-1;
		int row = OperaPropConfig.getRowIndex("OWS","RoomType");
		for(int i=1;i<=length;i++) {
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
			localCode = OperaPropConfig.getDataSetForCode("RoomType", dependencies.get("RoomType"));
			channelCode = OperaPropConfig.getChannelCodeForDataSet("RoomType", dataset);
			WSClient.setData("{var_roomType}",localCode);
			WSClient.setData("{var_channelRoomType}",channelCode);
			flag=flag && createChannelRoomMapping(dataset);
			dependencies.clear();
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("ChannelRoomMapping", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("ChannelRoomMapping", "N");

	}
	
}
