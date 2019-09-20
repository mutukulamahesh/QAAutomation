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

public class CreateCurrenciesMappingOWS extends WSSetUp{

	public boolean createCurrenciesMapping(String dataset)
	{
		try {
			
			String fetchCurrenciesMappingReq = WSClient.createSOAPMessage("FetchCurrenciesMapping", dataset);
            String fetchCurrenciesMappingRes= WSClient.processSOAPMessage(fetchCurrenciesMappingReq);
            if(WSAssert.assertIfElementExists(fetchCurrenciesMappingRes,"FetchCurrenciesMappingRS_Success",false))
            {
            	if(WSAssert.assertIfElementExists(fetchCurrenciesMappingRes, "FetchCurrenciesMappingRS_CurrenciesMapping", false))
            	{
            		if(WSAssert.assertIfElementExists(fetchCurrenciesMappingRes, "FetchCurrenciesMappingRS_CurrenciesMapping_Currency", true))
            		{
            			WSClient.writeToReport(LogStatus.INFO, "Currency Mapping for "+WSClient.getElementValue(fetchCurrenciesMappingRes, "CurrenciesMapping_Currency_LocalSystemCode", XMLType.RESPONSE) +" already exists");
            			return true;
            		}
            		else
            		{
						String createCurrenciesMappingReq = WSClient.createSOAPMessage("CreateCurrenciesMapping", dataset);
						String createCurrenciesMappingRes= WSClient.processSOAPMessage(createCurrenciesMappingReq);
				             if(WSAssert.assertIfElementExists(createCurrenciesMappingRes,"CreateCurrenciesMappingRS_Success",false))
					         {   
				            	 WSClient.writeToReport(LogStatus.INFO, "Currency Mapping for "+WSClient.getElementValue(createCurrenciesMappingReq, "Currencies_Currency_LocalSystemCode", XMLType.REQUEST)+" got created");
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
	
	
	@Test(groups= {"OWS"}, dependsOnGroups = {"createPropertyMapping"})
	public void createMultipleCurrenciesMapping()
	{
		String testName = "createMultipleCurrenciesMapping";
		WSClient.startTest(testName, "Create Currencies Mapping for cahnnel operations","OperaConfig");
		boolean flag = true;
		String localCode,channelCode;
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		String resort=OPERALib.getResort();
		String channel=OWSLib.getChannel();
		WSClient.setData("{var_Resort}",resort);
		//String carrier= OWSLib.getChannelCarier(resort,channel);
		WSClient.setData("{var_systemCode}", channel);
		int i;
		String dataset = "";
		int length = OperaPropConfig.getLengthForCodeOWS("Currency") - 1;
		HashMap<String,String> dependencies = new HashMap<String,String>();
		int row = OperaPropConfig.getRowIndex("OWS","Currency");
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
			channelCode=OperaPropConfig.getChannelCodeForDataSet("Currency", dataset);
			localCode=OperaPropConfig.getDataSetForCode("Currency", dependencies.get("Currency"));
			WSClient.setData("{var_localCode}",localCode);
			WSClient.setData("{var_externalCode}",channelCode);
			flag=flag && createCurrenciesMapping(dataset);
			dependencies.clear();
		}
		if(flag == true) 
			OperaPropConfig.setPropertyConfigResults("CurrenciesMappingOWS", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("CurrenciesMappingOWS", "N");
	}
}
