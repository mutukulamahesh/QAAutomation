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

public class CreateCreditCardsMappingOWS extends WSSetUp{

	public boolean createCreditCardsMapping(String dataset)
	{
		try {
			
			String fetchCreditCardsMappingReq = WSClient.createSOAPMessage("FetchCreditCardsMapping", dataset);
            String fetchCreditCardsMappingRes= WSClient.processSOAPMessage(fetchCreditCardsMappingReq);
            if(WSAssert.assertIfElementExists(fetchCreditCardsMappingRes,"FetchCreditCardsMappingRS_Success",false))
            {
            	if(WSAssert.assertIfElementExists(fetchCreditCardsMappingRes, "FetchCreditCardsMappingRS_CreditCardsMapping", false))
            	{
            		if(WSAssert.assertIfElementExists(fetchCreditCardsMappingRes, "FetchCreditCardsMappingRS_CreditCardsMapping_CreditCard", true))
            		{
            			WSClient.writeToReport(LogStatus.INFO, "Credit Card Mapping for "+WSClient.getElementValue(fetchCreditCardsMappingRes, "CreditCardsMapping_CreditCard_LocalSystemCode", XMLType.RESPONSE) +" already exists");
            			return true;
            		}
            		else
            		{
						String createCreditCardsMappingReq = WSClient.createSOAPMessage("CreateCreditCardsMapping", dataset);
						String createCreditCardsMappingRes= WSClient.processSOAPMessage(createCreditCardsMappingReq);
				             if(WSAssert.assertIfElementExists(createCreditCardsMappingRes,"CreateCreditCardsMappingRS_Success",false))
					         {   
				            	 WSClient.writeToReport(LogStatus.INFO, "Credit Card Mapping for "+WSClient.getElementValue(createCreditCardsMappingReq, "CreditCards_CreditCard_LocalSystemCode", XMLType.REQUEST)+" got created");
				            		 return true;
					         }
				             else
				            	 return false;
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
	
	
	@Test(groups= {"OWS","createCreditCardsMapping"}, dependsOnGroups= {"createPropertyMapping"})
	public void createMultipleCreditCardsMapping()
	{
		String testName = "createMultipleCreditCardsMapping";
		WSClient.startTest(testName, "Create Payment methods Mapping for Channel operations","OperaConfig");
		boolean flag = true;
		String localCode,channelCode;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		String resort=OPERALib.getResort();
		String channel=OWSLib.getChannel();
		WSClient.setData("{var_Resort}",resort);
		//String carrier= OWSLib.getChannelCarier(resort,channel);
		WSClient.setData("{var_systemCode}", channel);
		String dataset = "";
		int i;
		int length = OperaPropConfig.getLengthForCodeOWS("PaymentMethod") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OWS","PaymentMethod");
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
			channelCode=OperaPropConfig.getChannelCodeForDataSet("PaymentMethod", dataset);
			localCode=OperaPropConfig.getDataSetForCode("PaymentMethod", dependencies.get("PaymentMethod"));
			WSClient.setData("{var_localCode}",localCode);
			WSClient.setData("{var_externalCode}",channelCode);
			flag=flag && createCreditCardsMapping(dataset);
			dependencies.clear();
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("CreditCardsMappingOWS", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("CreditCardsMappingOWS", "N");
	}
	
}
