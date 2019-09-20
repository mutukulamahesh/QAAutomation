package com.oracle.hgbu.opera.qaauto.ws.components.configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.testng.annotations.Test;

import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OPERALib;
import com.oracle.hgbu.opera.qaauto.ws.soap.businessUtil.OperaPropConfig;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSAssert;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSClient.XMLType;
import com.oracle.hgbu.opera.qaauto.ws.soap.client.WSSetUp;
import com.relevantcodes.customextentreports.LogStatus;

public class CreateTransactionSubgroups extends WSSetUp{
	

	public boolean createTransactionSubGroups(String dataSet) {
		try {
//			String fetchTSGReq = WSClient.createSOAPMessage("FetchTransactionSubgroups", dataSet);
//			String fetchTSGRes =  WSClient.processSOAPMessage(fetchTSGReq);
//		
//			if(WSAssert.assertIfElementExists(fetchTSGRes, "FetchTransactionSubgroupsRS_Success", false))
//			{	
//				if(WSAssert.assertIfElementExists(fetchTSGRes, "FetchTransactionSubgroupsRS_TransactionSubgroups", false))
//            	{
//            		if(WSAssert.assertIfElementExists(fetchTSGRes, "FetchTransactionSubgroupsRS_TransactionSubgroups_TransactionSubgroup_Code", true))
//            		{
//            			WSClient.writeToReport(LogStatus.INFO, "Transactions Sub Group "+WSClient.getElementValue(fetchTSGRes, "FetchTransactionSubgroupsRS_TransactionSubgroups_TransactionSubgroup_Code", XMLType.RESPONSE)+" already exists");
//            			 WSClient.writeToReport(LogStatus.INFO, "Transaction SubGroup already exists.");
//            			return true;
//            		}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Transaction SubGroup</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateTransactionSubgroups", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Transaction SubGroup " + WSClient.getData("{var_TransactionSubGroup}") +" already exists");
				return true;
			}
            		else
            		{
            			 WSClient.writeToReport(LogStatus.INFO, "Transaction SubGroup doesn't exist.");
						 String createTransactionSubGroupReq = WSClient.createSOAPMessage("CreateTransactionSubgroups", dataSet);
			             String createTransactionSubGroupRes= WSClient.processSOAPMessage(createTransactionSubGroupReq);
			             if(WSAssert.assertIfElementExists(createTransactionSubGroupRes,"CreateTransactionSubgroupsRS_Success",false)){   
			            	 WSClient.writeToReport(LogStatus.PASS, "//CreateTransactionSubGroupsRS/Success exists on the response message");
								dbResult = WSClient.getDBRow(query);
								val = dbResult.get("COUNT");
								if(WSAssert.assertEquals("1", val, false)) {
									WSClient.writeToReport(LogStatus.INFO, "Transaction SubGroup has been created");
									return true;
								}
								else {
									WSClient.writeToReport(LogStatus.WARNING, "Transaction SubGroup not created");
									return false;
								}
									
							}
							else{
								if(WSAssert.assertIfElementExists(createTransactionSubGroupRes, "CreateTransactionSubGroupsRS_Errors_Error_ShortText", false)) {
									WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createTransactionSubGroupRes, "CreateTransactionSubGroupsRS_Errors_Error_ShortText", XMLType.RESPONSE));
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
	
    @Test(groups= {"createTransactionSubgroups","bat"},dependsOnGroups={"createTemplateTransactionSubgroups"})
    public void createMultipleTransactionSubgroups()
    {
    	try {
			String testName = "createTransactionSubgroups";
			WSClient.startTest(testName, "Creating a transaction sub group","OperaConfig");
			boolean flag=true;
			String TSG,TG;
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_Resort}",OPERALib.getResort() );
			int i;
			String dataset = "";
			int length = OperaPropConfig.getLengthForCode("TransactionSubGroup") - 1;
			HashMap<String,String> dependencies = new HashMap<String,String>();
			int row = OperaPropConfig.getRowIndex("OperaConfig","TransactionSubGroup");
			for(i=1;i<=length;i++) {
				if(i<=9)
					dataset = "DS_0" + i; 
				else
					dataset = "DS_" + i;    
				String comm = OperaPropConfig.getCellComment("OperaConfig",row, i);
				//System.out.println(comm);
				if(comm==null) {
					WSClient.writeToReport(LogStatus.WARNING, "Please provide dependencies");
				}
				dependencies = OperaPropConfig.getDependency(comm);
				TG=OperaPropConfig.getDataSetForCode("TransactionGroup", dependencies.get("TransactionGroup"));
				WSClient.setData("{var_TransactionGroup}",TG);
				TSG=OperaPropConfig.getDataSetForCode("TransactionSubGroup", dataset);
				WSClient.setData("{var_TransactionSubGroup}",TSG);
				flag= flag && createTransactionSubGroups(dataset);
				dependencies.clear();
			}
			if(flag == true) 
				OperaPropConfig.setPropertyConfigResults("TransactionSubGroup", "Y");
			else
				OperaPropConfig.setPropertyConfigResults("TransactionSubGroup", "N");
    	}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR,"Error is "+ e);
			e.printStackTrace();
		}
    }

}

