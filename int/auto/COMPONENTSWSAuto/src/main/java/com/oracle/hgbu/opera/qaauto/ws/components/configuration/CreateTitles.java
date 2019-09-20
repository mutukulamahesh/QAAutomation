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

public class CreateTitles extends WSSetUp
{
	public boolean createTitles(String dataSet) 
	{
		try 
		{
//			WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Titles</b>----------");
			LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
			String query = WSClient.getQuery("CreateGuestTitles", "QS_01");
			dbResult = WSClient.getDBRow(query);
			String val = dbResult.get("COUNT");
			if(WSAssert.assertEquals("1", val, true)) {
				WSClient.writeToReport(LogStatus.INFO, "Title " + WSClient.getData("{var_Title}") +" already exists");
				return true;
			}
					else 
					{
						WSClient.writeToReport(LogStatus.INFO, "Title doesnot exist!!");
						String req_createTitles = WSClient.createSOAPMessage("CreateGuestTitles", dataSet);
						String res_createTitles = WSClient.processSOAPMessage(req_createTitles);
						if(WSAssert.assertIfElementExists(res_createTitles, "CreateGuestTitlesRS_Success", false)) {
							WSClient.writeToReport(LogStatus.PASS, "//CreateGuestTitlesRS/Success exists on the response message");
						
						//DB Validation
						dbResult = new LinkedHashMap<String, String>();
						query = WSClient.getQuery("QS_01");
						dbResult = WSClient.getDBRow(query);
						val = dbResult.get("COUNT");
						if(WSAssert.assertEquals("1", val, true)) {
							WSClient.writeToReport(LogStatus.INFO, "Successfully Created Title");
							return true;
						}
						else {
							WSClient.writeToReport(LogStatus.WARNING, "Title not created");
							return false;
						}
					}
					else{
						if(WSAssert.assertIfElementExists(res_createTitles, "CreateGuestTitlesRS_Errors_Error_ShortText", false)) {
							WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(res_createTitles, "CreateGuestTitlesRS_Errors_Error_ShortText", XMLType.RESPONSE));
						}
						return false;
					}
					}
					
		}	
		catch(Exception e) 
		{
			WSClient.writeToReport(LogStatus.ERROR, e.toString());
			return false;
		}
	}
	
	
	@Test(groups= {"OperaConfig"})
	public void createMultiple_Titles() 
	{
		int i;
		boolean flag = true;
		String testName = "createTitles";
		WSClient.startTest(testName, "CreateTitles", "OperaConfig");
		String uname = OPERALib.getUserName();
		OPERALib.setOperaHeader(uname);
		WSClient.setData("{var_Resort}",OPERALib.getResort() );
		WSClient.setData("{var_chain}", OPERALib.getChain());
		String dataset = "";
		int length = OperaPropConfig.getLengthForCode("Title") - 1;
		for(i=1;i<=length;i++) {
			if(i<=9)
				dataset = "DS_0" + i; 
			else
				dataset = "DS_" + i;    			
			String value = OperaPropConfig.getDataSetForCode("Title" , dataset);
			WSClient.setData("{var_Title}", value);
			flag = flag && createTitles(dataset);
		}
		
		if(flag == true)
			OperaPropConfig.setPropertyConfigResults("Title", "Y");
		else
			OperaPropConfig.setPropertyConfigResults("Title", "N");
	}
}
