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

public class CreateTransactionGroups extends WSSetUp{
	

	public boolean createTransactionGroup(String dataSet) {
		
		try {
//			String fetchTGReq = WSClient.createSOAPMessage("FetchTransactionGroups", dataSet);
//			String fetchTGRes =  WSClient.processSOAPMessage(fetchTGReq);
//		
//			if(WSAssert.assertIfElementExists(fetchTGRes, "FetchTransactionGroupsRS_Success", false))
//			{	
//				if(WSAssert.assertIfElementExists(fetchTGRes, "FetchTransactionGroupsRS_TransactionGroups", false))
//            	{
//            		if(WSAssert.assertIfElementExists(fetchTGRes, "FetchTransactionGroupsRS_TransactionGroups_TransactionGroup_Code", true))
//            		{
//            			WSClient.writeToReport(LogStatus.INFO, "Transaction Group "+WSClient.getElementValue(fetchTGRes, "FetchTransactionGroupsRS_TransactionGroups_TransactionGroup_Code", XMLType.RESPONSE)+" already exists");
//            			WSClient.writeToReport(LogStatus.INFO, "Transaction Group already exists.");	
//            			return true;
//            		}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Transaction Group</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateTransactionGroups", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Transaction Group " + WSClient.getData("{var_TransactionGroup}") +" already exists");
				return true;
			}
            		else
            		{
            			WSClient.writeToReport(LogStatus.INFO, "Transaction Group doesn't exist.");
						 String createTransactionGroupReq = WSClient.createSOAPMessage("CreateTransactionGroups", dataSet);
			             String createTransactionGroupRes= WSClient.processSOAPMessage(createTransactionGroupReq);
			             if(WSAssert.assertIfElementExists(createTransactionGroupRes,"CreateTransactionGroupsRS_Success",false)){   
			            	 WSClient.writeToReport(LogStatus.PASS, "//CreateTransactionGroupsRS/Success exists on the response message");
								dbResult = WSClient.getDBRow(query);
								val = dbResult.get("COUNT");
								if(WSAssert.assertEquals("1", val, false)) {
									WSClient.writeToReport(LogStatus.INFO, "Transaction Group has been created");
									return true;
								}
								else {
									WSClient.writeToReport(LogStatus.WARNING, "Transaction Group not created");
									return false;
								}
									
							}
							else{
								if(WSAssert.assertIfElementExists(createTransactionGroupRes, "CreateTransactionGroupsRS_Errors_Error_ShortText", false)) {
									WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createTransactionGroupRes, "CreateTransactionGroupsRS_Errors_Error_ShortText", XMLType.RESPONSE));
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
	
    @Test(groups= {"createTransactionGroups","bat"},dependsOnGroups={"createTemplateTransactionGroups"})
    public void createMultipleTransactionGroups()
    {
    	try {
			String testName = "createTransactionGroups";
			WSClient.startTest(testName, "Creating a transaction group","OperaConfig");
			boolean flag=true;
			String ds,TG;
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_Resort}",OPERALib.getResort() );
			int i;
			String dataset = "";
			int length = OperaPropConfig.getLengthForCode("TransactionGroup") - 1;
			for(i=1;i<=length;i++) {
				if(i<=9)
					dataset = "DS_0" + i; 
				else
					dataset = "DS_" + i;    
				TG=OperaPropConfig.getDataSetForCode("TransactionGroup", dataset);
				WSClient.setData("{var_TransactionGroup}",TG);
				flag = flag && createTransactionGroup(dataset);
			}
			if(flag == true) 
				OperaPropConfig.setPropertyConfigResults("TransactionGroup", "Y");
			else
				OperaPropConfig.setPropertyConfigResults("TransactionGroup", "N");
    	}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR,"Error is "+ e);
			e.printStackTrace();
		}
    }

}


