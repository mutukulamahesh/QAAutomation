package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.HashMap;
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

public class CreateRateMappingOWS extends WSSetUp {
	public boolean createRateCodeMapping(String dataset) {
		try {
			
			
//			String req_fetchRateCode = WSClient.createSOAPMessage("FetchChannelRateMapping", "DS_01");
//			String res_fetchRateCode = WSClient.processSOAPMessage(req_fetchRateCode);
//			
//			if(WSAssert.assertIfElementExists(res_fetchRateCode, "FetchChannelRateMappingRS_Success", false)) {
//				if(WSAssert.assertIfElementExists(res_fetchRateCode, "FetchChannelRateMappingRS_ChannelRateMappings_ChannelRateMapping_ChannelRatePlanCode", true)) {
//					WSClient.writeToReport(LogStatus.INFO, "Rate Code Mapping already exists");
//					return true;
//				}
//			
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Channel Rate Mappings</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateChannelRateMapping", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Rate Mapping " + WSClient.getData("{var_ExtRateCode}") + " for Rate Code " + WSClient.getData("{var_RateCode}") +" already exists");
				return true;
			}
				else {
					WSClient.writeToReport(LogStatus.INFO, "Rate Code Mapping does not  exist!!");
					String req_createRateCode= WSClient.createSOAPMessage("CreateChannelRateMapping", dataset);
					String res_createRateCode = WSClient.processSOAPMessage(req_createRateCode);
					if(WSAssert.assertIfElementExists(res_createRateCode, "CreateChannelRateMappingRS_Success", true)) {
						
						 WSClient.writeToReport(LogStatus.PASS, "//CreateChannelRateMappingRS/Success exists on the response message");
			               dbResult = WSClient.getDBRow(query);
			   			   val = dbResult.get("COUNT");
			   			if(WSAssert.assertEquals("1", val, true)) {
			   				WSClient.writeToReport(LogStatus.INFO, "Rate Mapping has been created");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Rate Mapping not created");
							return false;
						}
			            }
						else {
							if(WSAssert.assertIfElementExists(res_createRateCode, "CreateChannelRateMappingRS_Errors_Error_ShortText", false)) {
									WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createRateCode, "CreateChannelRateMappingRS_Errors_Error_ShortText", XMLType.RESPONSE));
							}
									return false;
												
							}
		}
		}
		catch(Exception e) {
		WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	
	public boolean createRateSchedules(String dataset) {
		try {
			String req_fetchRateCodeSch= WSClient.createSOAPMessage("FetchRatePlanSchedules", dataset);
			String res_fetchRateCodeSch = WSClient.processSOAPMessage(req_fetchRateCodeSch);
			if(WSAssert.assertIfElementExists(res_fetchRateCodeSch, "FetchRatePlanSchedulesRS_RatePlanScheduleList_RatePlanCode", true)) {
				WSClient.writeToReport(LogStatus.INFO, "Rate Schedule already exists");
				return true;
			}
			else {
				WSClient.writeToReport(LogStatus.INFO, "Rate Schedule does not  exist!!");
				String req_createRateCodeSch= WSClient.createSOAPMessage("CreateRatePlanSchedules", dataset);
				String res_createRateCodeSch = WSClient.processSOAPMessage(req_createRateCodeSch);
				if(WSAssert.assertIfElementExists(res_createRateCodeSch, "CreateRatePlanSchedulesRS_Success", false)) {
					WSClient.writeToReport(LogStatus.INFO, "Successfully created Rate Schedule");
					return true;
				}
				else
					return false;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
	}
	
	
	@Test(groups= {"OWS"}, dependsOnGroups = {"createPropertyMapping", "createChannelRoomMapping"})
	public void createMultiple_RateCodes() {
		int i;
		boolean flag = true;
		String testName = "createRateCodesOWS";
		WSClient.startTest(testName, "Create Rate Codes OWS", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		WSClient.setData("{var_ChannelCode}", OWSLib.getChannel());
		String ds = "", dataset="";
		int length = OperaPropConfig.getLengthForCodeOWS("RateCode") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OWS","RateCode");
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
			String roomType=OperaPropConfig.getDataSetForCode("RoomType", dependencies.get("RoomType"));
			String loc_value = OperaPropConfig.getDataSetForCode("RateCode" , dependencies.get("RateCode"));
			String rateCategory=OperaPropConfig.getDataSetForCode("RateCategory", dependencies.get("RateCategory"));
			String value = OperaPropConfig.getChannelCodeForDataSet("RateCode" , dataset);
			WSClient.setData("{var_RoomType}", roomType);
			WSClient.setData("{var_RateCategory}", rateCategory);
			WSClient.setData("{var_RateCode}", loc_value);
			WSClient.setData("{var_ExtRateCode}", value);
			flag = flag && createRateCodeMapping(dataset);
			flag = flag && createRateSchedules(dataset);
			dependencies.clear();
		}
		
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("RateCodeOWS", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("RateCodeOWS", "N");
	}


}
