package com.oracle.hgbu.opera.qaauto.ws.components.configuration;


import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OWSLib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateChannelRoomTypesOWS extends WSSetUp{
	
	public boolean createChannelRoomTypes(String dataset)
	{
		try {
			
			String createChannelRoomTypesReq = WSClient.createSOAPMessage("CreateChannelRoomTypes", dataset);
			String createChannelRoomTypesRes= WSClient.processSOAPMessage(createChannelRoomTypesReq);
			String code=WSAssert.getElementValue(createChannelRoomTypesReq, "RoomTypes_RoomType_RoomTypeCode_Code", XMLType.REQUEST);
        	
			if(WSAssert.assertIfElementExists(createChannelRoomTypesRes,"CreateChannelRoomTypesRS_Success",true))
			{   
				WSClient.writeToReport(LogStatus.INFO, "Channel Room Type "+code+" got created");
				return true;      
            }
            else if(WSAssert.assertIfElementExists(createChannelRoomTypesRes, "CreateChannelRoomTypesRS_Errors", true))
            {
            	String errorText=WSAssert.getElementValue(createChannelRoomTypesRes, "CreateChannelRoomTypesRS_Errors_Error_ShortText", XMLType.RESPONSE);
            	String channel=WSAssert.getElementValue(createChannelRoomTypesReq, "CreateChannelRoomTypesRQ_RoomTypes_BookingChannelCode", XMLType.REQUEST);
            	String error="Code "+code+" already exists for channel "+channel;
            	if(WSAssert.assertEquals(error, errorText, false))
            	{
            		WSClient.writeToReport(LogStatus.INFO, "Channel Room Type "+code+" already exists");
            		return true;
            	}
            	else
            		return false;
            }
			return false;
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR,"Error is "+ e);
			e.printStackTrace();
			return false;
		}
	}
	

	@Test(groups= {"createChannelRoomTypes"}, dependsOnGroups = {"createPropertyMapping"})
	public void createMultipleChannelRoomTypes()
	{
		String testName = "createChannelRoomTypes";
		WSClient.startTest(testName, "Creating channel room types","OperaConfig");
		boolean flag = true;
		String channelCode;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort());
		String channel=OWSLib.getChannel();
		WSClient.setData("{var_channel}", channel);
		String dataset = "";
		int i;
		int length = OperaPropConfig.getLengthForCodeOWS("RoomType") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    
			channelCode=OperaPropConfig.getChannelCodeForDataSet("RoomType", dataset);
			WSClient.setData("{var_roomType}",channelCode);
			flag=flag && createChannelRoomTypes(dataset);
		}
		
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("ChannelRoomTypes", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("ChannelRoomTypes", "N");

	}

	
}
