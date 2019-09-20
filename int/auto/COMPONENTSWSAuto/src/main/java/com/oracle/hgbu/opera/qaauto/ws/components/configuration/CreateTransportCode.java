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

public class CreateTransportCode extends WSSetUp {
	public boolean createTransportCode(String dataset){
	try{
//		
//		
		WSClient.writeToReport(LogStatus.INFO, "----------<b>Fetching Transport Code</b>----------");
		LinkedHashMap<String, String> dbResult = new LinkedHashMap<String, String>();
		String query = WSClient.getQuery("CreateTransportation", "QS_01");
		dbResult = WSClient.getDBRow(query);
		String val = dbResult.get("COUNT");
		if(WSAssert.assertEquals("1", val, true)) {
			WSClient.writeToReport(LogStatus.INFO, "Transport Code " + WSClient.getData("{var_TransportCode}") +" already exists");
			return true;
		}
		else {
			WSClient.writeToReport(LogStatus.INFO, "Transport Code doesnot exist!!");
			String createTransportationReq=WSClient.createSOAPMessage("CreateTransportation", dataset);
			String createTransportationRes=WSClient.processSOAPMessage(createTransportationReq);
//			if(WSAssert.assertIfElementExists(createTransportationRes, "CreateTransportationRS_Success", true)){
//				WSClient.writeToReport(LogStatus.PASS, "//CreateTransportationRS/Success exists on the response message");
				
			if(WSAssert.assertIfElementExists(createTransportationRes, "CreateTransportationRS_Errors_Error_ShortText", true)) {
				WSClient.writeToReport(LogStatus.PASS, "//CreateTransportationRS/Errors/Error/@ShortText exists on the response message");
				WSClient.writeToReport(LogStatus.WARNING, WSAssert.getElementValue(createTransportationRes, "CreateTransportationRS_Errors_Error_ShortText", XMLType.RESPONSE));
			
			return false;
			}
					//DB Validation
					dbResult = new LinkedHashMap<String, String>();
					query = WSClient.getQuery("QS_01");
					dbResult = WSClient.getDBRow(query);
					val = dbResult.get("COUNT");
					if(WSAssert.assertEquals("1", val, true)) {
						WSClient.writeToReport(LogStatus.INFO, "Successfully Created Transportation Code");
						return true;
					}
					else {
						WSClient.writeToReport(LogStatus.WARNING, "Transportation not created");
						return false;
					}
//				}
//				else{
					
				}
			}
		
	//	}
		
	catch (Exception e) {
		e.printStackTrace();
		WSClient.writeToReport(LogStatus.ERROR, "Exception ocuured due to :" +e);
		return false;
	} 
}

@Test(groups= {"OperaConfig"})
public void createMultiple_TransportationCode() {
	int i;
	boolean flag = true;
	String uname = OPERALib.getUserName();
	OPERALib.setOperaHeader(uname);
	WSClient.setData("{var_Resort}",OPERALib.getResort() );
	String testName="TransportationCode";
	String dataset = "";
	WSClient.startTest(testName, "Create Transportation Code", "OperaConfig");
	WSClient.setData("{var_Chain}", OPERALib.getChain());
	int length = OperaPropConfig.getLengthForCode("TransportationType") - 1;
	for(i=1;i<=length;i++) {
		if(i<=9)
			dataset = "DS_0" + i; 
		else
			dataset = "DS_" + i;
		String value = OperaPropConfig.getDataSetForCode("TransportationType" , dataset);
		WSClient.setData("{var_TransportCode}", value);
		flag = flag && createTransportCode(dataset);
	}
	
	if(flag == true) 
		OperaPropConfig.setPropertyConfigResults("TransportationType", "Y");
	else
		OperaPropConfig.setPropertyConfigResults("TransportationType", "N");
}
}



