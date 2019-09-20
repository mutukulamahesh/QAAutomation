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

public class CreateTemplateTransactionCodes extends WSSetUp{
	

	public boolean createTemplateTransactionCode(String dataSet) {
		try {
//			String fetchTemplateTCReq = WSClient.createSOAPMessage("FetchTemplateTransactionCodes", dataSet);
//			String fetchTemplateTCGRes =  WSClient.processSOAPMessage(fetchTemplateTCReq);
//		
//			if(WSAssert.assertIfElementExists(fetchTemplateTCGRes, "FetchTemplateTransactionCodesRS_Success", false))
//			{	
//				if(WSAssert.assertIfElementExists(fetchTemplateTCGRes, "FetchTemplateTransactionCodesRS_TransactionCodes", false))
//            	{
//            		if(WSAssert.assertIfElementExists(fetchTemplateTCGRes, "FetchTemplateTransactionCodesRS_TransactionCodes_TransactionCode_Code", true))
//            		{
//            			WSClient.writeToReport(LogStatus.INFO, "Transaction Template Code "+WSClient.getElementValue(fetchTemplateTCGRes, "FetchTemplateTransactionCodesRS_TransactionCodes_TransactionCode_Code", XMLType.RESPONSE)+" already exists");
//            			WSClient.writeToReport(LogStatus.INFO, "Transaction Template Code already exists");
//            			return true;
//            		}
			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Transaction Code Template</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateTemplateTransactionCodes", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Template for Transaction Code " + WSClient.getData("{var_TransactionCode}") +" already exists");
				return true;
			}
            		else
            		{
            			WSClient.writeToReport(LogStatus.INFO, "Transaction Template Code doesnot exist");
						 String createTransactionCodesReq = WSClient.createSOAPMessage("CreateTemplateTransactionCodes", dataSet);
			             String createTransactionCodesRes= WSClient.processSOAPMessage(createTransactionCodesReq);
			             if(WSAssert.assertIfElementExists(createTransactionCodesRes,"CreateTemplateTransactionCodesRS_Success",true)){
			            	 WSClient.writeToReport(LogStatus.PASS, "//CreateTemplateTransactionCodesRS/Success exists on the response message");
								dbResult = WSClient.getDBRow(query);
								val = dbResult.get("COUNT");
								if(WSAssert.assertEquals("1", val, false)) {
									WSClient.writeToReport(LogStatus.INFO, "Template for Transaction Code has been created");
									return true;
								}
								else {
									WSClient.writeToReport(LogStatus.WARNING, "Transaction Code not created");
									return false;
								}
									
							}
							else{
								if(WSAssert.assertIfElementExists(createTransactionCodesRes, "CreateTemplateTransactionCodesRS_Errors_Error_ShortText", false)) {
									WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createTransactionCodesRes, "CreateTemplateTransactionCodesRS_Errors_Error_ShortText", XMLType.RESPONSE));
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
	
    @Test(groups= {"createTemplateTransactionCodes","bat"},dependsOnGroups={"createTransactionSubgroups"})
    public void createMultipleTemplateTransactionCodes()
    {
    	try {
			String testName = "createTemplateTransactionCodes";
			WSClient.startTest(testName, "Creating a template transaction code","OperaConfig");
			boolean flag=true;
			String TSG,TG,tCode;
			String uname = OPERALib.getUserName();
			OPERALib.setOperaHeader(uname);
			WSClient.setData("{var_Resort}",OPERALib.getResort() );
			WSClient.setData("{var_Chain}", OPERALib.getChain());
			int i;
			String dataset = "";
			int length = OperaPropConfig.getLengthForCode("TransactionCode") - 1;
			HashMap<String,String> dependencies = new HashMap<String,String>();
			int row = OperaPropConfig.getRowIndex("OperaConfig","TransactionCode");
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
				TSG=OperaPropConfig.getDataSetForCode("TransactionSubGroup", dependencies.get("TransactionSubGroup"));
				WSClient.setData("{var_TransactionSubGroup}",TSG);
				tCode=OperaPropConfig.getDataSetForCode("TransactionCode", dataset);
				WSClient.setData("{var_TransactionCode}",tCode);
				flag=flag && createTemplateTransactionCode(dataset);
				dependencies.clear();
			}
			if(flag == true) 
				OperaPropConfig.setPropertyConfigResults("TemplateTransactionCodes", "Y");
			else
				OperaPropConfig.setPropertyConfigResults("TemplateTransactionCodes", "N");

    	}catch (Exception e) {
			WSClient.writeToReport(LogStatus.ERROR,"Error is "+ e);
			e.printStackTrace();
		}
    }

}

