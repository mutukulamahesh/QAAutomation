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

public class CreateOutOfOrderServiceReasons extends WSSetUp{

	
	public boolean createOutOfOrderServiceReason(String dataSet)
	{
		try
		{
//			String fetchOoosReq= WSClient.createSOAPMessage("FetchOutOfOrderServiceReasons", dataSet);
//			String fetchOoosRes =  WSClient.processSOAPMessage(fetchOoosReq);
//		
//			if(WSAssert.assertIfElementExists(fetchOoosRes, "FetchOutOfOrderServiceReasonsRS_Success", false))
//			{	
//				if(WSAssert.assertIfElementExists(fetchOoosRes, "FetchOutOfOrderServiceReasonsRS_OutOfOrderServiceReasons", false))
//	        	{
//	        		if(WSAssert.assertIfElementExists(fetchOoosRes, "FetchOutOfOrderServiceReasonsRS_OutOfOrderServiceReasons_OutOfOrderServiceReason_ReasonCode", true))
//	        		{
//	        			WSClient.writeToReport(LogStatus.INFO, "Out of Order/Service reason "+WSClient.getElementValue(fetchOoosRes, "FetchOutOfOrderServiceReasonsRS_OutOfOrderServiceReasons_OutOfOrderServiceReason_ReasonCode", XMLType.RESPONSE)+" already exists");
//	        			return true;
//	        		}
			
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Out Of Order Reasons</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateOutOfOrderServiceReasons", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Out Of Order/Service Reason " + WSClient.getData("{var_OOOSReason}") +" already exists");
				return true;
			}
	        		else
	        		{
						 String createOOOSReq = WSClient.createSOAPMessage("CreateOutOfOrderServiceReasons", dataSet);
			             String createOOOSRes= WSClient.processSOAPMessage(createOOOSReq);
			             if(WSAssert.assertIfElementExists(createOOOSRes,"CreateOutOfOrderServiceReasonsRS_Success",true)) {
			            	 WSClient.writeToReport(LogStatus.PASS, "//CreateOutOfOrderServiceReasonsRS/Success exists on the response message");
			            	 dbResult = WSClient.getDBRow(query);
			     			 val = dbResult.get("COUNT");
			     			 if(WSAssert.assertEquals("1", val, false)){
									WSClient.writeToReport(LogStatus.INFO, "Out Of Order/Service Reason has been created");
									return true;
								}
								else {
									WSClient.writeToReport(LogStatus.WARNING, "Out Of Order/Service Reason not created");
									return false;
								}
									
							}
							else{
								if(WSAssert.assertIfElementExists(createOOOSRes, "CreateOutOfOrderServiceReasonsRS_Errors_Error_ShortText", false)) {
									WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createOOOSRes, "CreateOutOfOrderServiceReasonsRS_Errors_Error_ShortText", XMLType.RESPONSE));
								}
								return false;
							}
			            
	            	}
		}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR,"Error is "+ e);
			//e.printStackTrace();
			return false;
		}
	}
	
	
	
	@Test(groups= {"OperaConfig","OOOSReasons"})
	public void createMultipleOutOfOrderServiceReasons()
	{
			String testName = "createOutOfOrderServiceReasons";
			WSClient.startTest(testName, "Creating a Out Of Order / Service Reasons","OperaConfig");
			boolean flag = true;
			String reason;
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_Resort}",OPERALib.getResort() );
			int i;
			String dataset = "";
			int length = OperaPropConfig.getLengthForCode("OOOSReason") - 1;
			for(i=1;i<=length;i++) {
				if(i<=9)
					dataset = "DS_0" + i; 
				else
					dataset = "DS_" + i;    
				reason=OperaPropConfig.getDataSetForCode("OOOSReason", dataset);
				WSClient.setData("{var_OOOSReason}",reason);
				flag=flag && createOutOfOrderServiceReason(dataset);
			}
			if(flag == true) 
				OperaPropConfig.setPropertyConfigResults("OOOSReason", "Y");
			else
				OperaPropConfig.setPropertyConfigResults("OOOSReason", "N");
			
	}
	
}
