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

public class CreateTemplateTransactionSubgroups extends WSSetUp{
	

	public boolean createTemplateTransactionSubgroup(String dataSet) {
		try {
//			String fetchTemplateTSGReq = WSClient.createSOAPMessage("FetchTemplateTransactionSubgroups", dataSet);
//			String fetchTemplateTSGRes =  WSClient.processSOAPMessage(fetchTemplateTSGReq);
//		
//			if(WSAssert.assertIfElementExists(fetchTemplateTSGRes, "FetchTemplateTransactionSubgroupsRS_Success", false))
//			{	
//				if(WSAssert.assertIfElementExists(fetchTemplateTSGRes, "FetchTemplateTransactionSubgroupsRS_TransactionSubgroups", false))
//            	{
//            		if(WSAssert.assertIfElementExists(fetchTemplateTSGRes, "FetchTemplateTransactionSubgroupsRS_TransactionSubgroups_TransactionSubgroups_Code", true))
//            		{
//            			WSClient.writeToReport(LogStatus.INFO, "Transaction Template Subgroup "+WSClient.getElementValue(fetchTemplateTSGRes, "FetchTemplateTransactionSubgroupsRS_TransactionSubgroups_TransactionSubgroups_Code", XMLType.RESPONSE)+" already exists");
//            			WSClient.writeToReport(LogStatus.INFO, "Transaction Template SubGroup already exists.");
//            			return true;
//            		}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Template Transaction SubGroup</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateTemplateTransactionSubgroups", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Template for Transaction SubGroup " + WSClient.getData("{var_TransactionSubGroup}") +" already exists");
				return true;
			}
            		else
            		{
            			WSClient.writeToReport(LogStatus.INFO, "Transaction Template SubGroup doesn't exist.");
						 String createTempTransactionSubGroupReq = WSClient.createSOAPMessage("CreateTemplateTransactionSubgroups", dataSet);
			             String createTempTransactionSubGroupRes= WSClient.processSOAPMessage(createTempTransactionSubGroupReq);
			             if(WSAssert.assertIfElementExists(createTempTransactionSubGroupRes,"CreateTemplateTransactionSubgroupsRS_Success",true)){   
			            	 WSClient.writeToReport(LogStatus.PASS, "//CreateTemplateTransactionSubGroupsRS/Success exists on the response message");
								dbResult = WSClient.getDBRow(query);
								val = dbResult.get("COUNT");
								if(WSAssert.assertEquals("1", val, false)) {
									WSClient.writeToReport(LogStatus.INFO, "Template for Transaction SubGroup has been created");
									return true;
								}
								else {
									WSClient.writeToReport(LogStatus.WARNING, " Template for Transaction SubGroup not created");
									return false;
								}
									
							}
							else{
								if(WSAssert.assertIfElementExists(createTempTransactionSubGroupRes, "CreateTemplateTransactionSubGroupsRS_Errors_Error_ShortText", false)) {
									WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createTempTransactionSubGroupRes, "CreateTemplateTransactionSubGroupsRS_Errors_Error_ShortText", XMLType.RESPONSE));
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
	
    @Test(groups= {"createTemplateTransactionSubgroups","bat"},dependsOnGroups={"createTransactionGroups"})
    public void createMultipleTemplateTransactionSubgroups()
    {
    	try {
			String testName = "createTemplateTransactionSubgroups";
			WSClient.startTest(testName, "Creating a template transaction sub group","OperaConfig");
			boolean flag=true;
			String tempTSG,TG;
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_Resort}",OPERALib.getResort() );
			WSClient.setData("{var_Chain}", OPERALib.getChain());
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
				tempTSG=OperaPropConfig.getDataSetForCode("TransactionSubGroup", dataset);
				WSClient.setData("{var_TransactionSubGroup}",tempTSG);
				flag = flag && createTemplateTransactionSubgroup(dataset);
				dependencies.clear();
			}
			if(flag == true) 
				OperaPropConfig.setPropertyConfigResults("TemplateTransactionSubgroups", "Y");
			else
				OperaPropConfig.setPropertyConfigResults("TemplateTransactionSubgroups", "N");
    	}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR,"Error is "+ e);
			e.printStackTrace();
		}
    }

}

