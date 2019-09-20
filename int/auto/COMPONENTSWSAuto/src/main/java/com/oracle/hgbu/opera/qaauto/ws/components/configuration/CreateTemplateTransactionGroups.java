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

public class CreateTemplateTransactionGroups extends WSSetUp{
	

	public boolean createTemplateTransactionGroup(String dataSet) {
		try {
//			String fetchTemplateTGReq = WSClient.createSOAPMessage("FetchTemplateTransactionGroups", dataSet);
//			String fetchTemplateTGRes =  WSClient.processSOAPMessage(fetchTemplateTGReq);
//		
//			if(WSAssert.assertIfElementExists(fetchTemplateTGRes, "FetchTemplateTransactionGroupsRS_Success", false))
//			{	
//				if(WSAssert.assertIfElementExists(fetchTemplateTGRes, "FetchTemplateTransactionGroupsRS_TransactionGroups", false))
//            	{
//            		if(WSAssert.assertIfElementExists(fetchTemplateTGRes, "FetchTemplateTransactionGroupsRS_TransactionGroups_TransactionGroup_Code", true))
//            		{
//            			WSClient.writeToReport(LogStatus.INFO, "Transaction Template Group "+WSClient.getElementValue(fetchTemplateTGRes, "FetchTemplateTransactionGroupsRS_TransactionGroups_TransactionGroup_Code", XMLType.RESPONSE)+" already exists");
//            			 WSClient.writeToReport(LogStatus.INFO, "Transaction Template Group already exists");
//            			return true;
//            		}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Template Transaction Group</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateTemplateTransactionGroups", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Template for Transaction Group " + WSClient.getData("{var_TransactionGroup}") +" already exists");
				return true;
			}
            		else
            		{
            			 WSClient.writeToReport(LogStatus.INFO, "Transaction Template Group doesn't exist");
						 String createTransactionGroupReq = WSClient.createSOAPMessage("CreateTemplateTransactionGroups", dataSet);
			             String createTransactionGroupRes= WSClient.processSOAPMessage(createTransactionGroupReq);
			             if(WSAssert.assertIfElementExists(createTransactionGroupRes,"CreateTemplateTransactionGroupsRS_Success",true)){
			            	 WSClient.writeToReport(LogStatus.PASS, "//CreateTemplateTransactionGroupsRS/Success exists on the response message");
								dbResult = WSClient.getDBRow(query);
								val = dbResult.get("COUNT");
								if(WSAssert.assertEquals("1", val, false)) {
									WSClient.writeToReport(LogStatus.INFO, "Template for Transaction Group has been created");
									return true;
								}
								else {
									WSClient.writeToReport(LogStatus.WARNING, "Template for Transaction Group not created");
									return false;
								}
									
							}
							else{
								if(WSAssert.assertIfElementExists(createTransactionGroupRes, "CreateTemplateTransactionGroupsRS_Errors_Error_ShortText", false)) {
									WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createTransactionGroupRes, "CreateTemplateTransactionGroupsRS_Errors_Error_ShortText", XMLType.RESPONSE));
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
	
    @Test(groups= {"createTemplateTransactionGroups","bat"})
    public void createMultipleTemplateTransactionGroups()
    {
    	try {
			String testName = "createTemplateTransactionGroups";
			WSClient.startTest(testName, "Creating a template transaction group","OperaConfig");
			boolean flag=true;
			String tempTG;
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_Resort}",OPERALib.getResort() );
			WSClient.setData("{var_Chain}", OPERALib.getChain());
			int i;
			String dataset = "";
			int length = OperaPropConfig.getLengthForCode("TransactionGroup") - 1;
			for(i=1;i<=length;i++) {
				if(i<=9)
					dataset = "DS_0" + i; 
				else
					dataset = "DS_" + i;    
				tempTG=OperaPropConfig.getDataSetForCode("TransactionGroup", dataset);
				WSClient.setData("{var_TransactionGroup}",tempTG);
				flag = flag && createTemplateTransactionGroup(dataset);
			}
			if(flag == true) 
				OperaPropConfig.setPropertyConfigResults("TemplateTransactionGroups", "Y");
			else
				OperaPropConfig.setPropertyConfigResults("TemplateTransactionGroups", "N");
    	}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR,"Error is "+ e);
			e.printStackTrace();
		}
    }

}

